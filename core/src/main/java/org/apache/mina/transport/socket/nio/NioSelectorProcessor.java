/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.mina.IoSession;
import org.apache.mina.service.SelectorProcessor;
import org.apache.mina.service.SelectorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * 
 */
public class NioSelectorProcessor implements SelectorProcessor {

    private SelectorStrategy strategy;

    private Logger log;

    private Map<SocketAddress, ServerSocketChannel> serverSocketChannels = new ConcurrentHashMap<SocketAddress, ServerSocketChannel>();

    public NioSelectorProcessor(String name, SelectorStrategy strategy) {
        this.strategy = strategy;
        this.log = LoggerFactory.getLogger("SelectorProcessor[" + name + "]");
    }

    private Selector selector;

    // new binded server to add to the selector
    private final Queue<ServerSocketChannel> serverToAdd = new ConcurrentLinkedQueue<ServerSocketChannel>();

    // server to remove of the selector
    private final Queue<ServerSocketChannel> serverToRemove = new ConcurrentLinkedQueue<ServerSocketChannel>();

    // new session freshly accepted, placed here for being added to the selector
    private final Queue<IoSession> sessionToConnect = new ConcurrentLinkedQueue<IoSession>();

    // session to be removed of the selector
    private final Queue<IoSession> sessionToClose = new ConcurrentLinkedQueue<IoSession>();

    /**
     * Add a bound server channel for starting accepting new client connections.
     * 
     * @param serverChannel
     */
    public void add(ServerSocketChannel serverChannel) {
        log.debug("adding a server channel " + serverChannel);
        serverToAdd.add(serverChannel);
        wakeupWorker();
    }

    private Object workerLock = new Object();

    private SelectorWorker worker = null;

    private void wakeupWorker() {
        synchronized (workerLock) {
            if (worker == null) {
                worker = new SelectorWorker();
                worker.start();
            }
        }
        if (selector != null) {
            selector.wakeup();
        }
    }

    @Override
    public void bindAndAcceptAddress(SocketAddress address) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.socket().bind(address);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannels.put(address, serverSocketChannel);
        add(serverSocketChannel);
    }

    @Override
    public void unbind(SocketAddress address) throws IOException {
        ServerSocketChannel channel = serverSocketChannels.get(address);
        channel.socket().close();
        channel.close();
        serverSocketChannels.remove(channel);
        log.debug("removing a server channel " + channel);
        serverToRemove.add(channel);
        wakeupWorker();
    }

    @Override
    public void createSession(Object clientSocket) {
        // TODO Auto-generated method stub
    }

    private class SelectorWorker extends Thread {

        // map for finding the keys associated with a given server
        private Map<ServerSocketChannel, SelectionKey> serverKey = new HashMap<ServerSocketChannel, SelectionKey>();

        @Override
        public void run() {
            if (selector == null) {
                log.debug("opening a new selector");
                try {
                    selector = Selector.open();
                } catch (IOException e) {
                    log.error("IOException while opening a new Selector", e);
                }
            }

            for (;;) {
                try {
                    // pop server sockets for removing
                    if (serverToRemove.size() > 0) {
                        while (!serverToRemove.isEmpty()) {
                            ServerSocketChannel channel = serverToRemove.poll();
                            SelectionKey key = serverKey.remove(channel);
                            if (key == null) {
                                log.error("The server socket was already removed of the selector");
                            } else {
                                key.cancel();
                            }
                        }
                    }

                    // pop new server sockets for accepting
                    if (serverToAdd.size() > 0) {
                        while (!serverToAdd.isEmpty()) {
                            ServerSocketChannel channel = serverToAdd.poll();
                            SelectionKey key = channel.register(selector,
                                    SelectionKey.OP_ACCEPT);
                            key.attach(channel);
                        }
                    }
                    log.debug("selecting...");
                    int result = selector.select();
                    log.debug("... done selecting : " + result);

                    if (result > 0) {
                        // process selected keys
                        for (SelectionKey key : selector.selectedKeys()) {
                            if (key.isAcceptable()) {
                                log.debug("acceptable new client");
                                // accepted connection
                                SocketChannel newClientChannel = ((ServerSocketChannel) key
                                        .attachment()).accept();
                                log.debug("client accepted");
                                // and give it's to the strategy
                                strategy.getSelectorForNewSession(
                                        NioSelectorProcessor.this)
                                        .createSession(newClientChannel);

                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("IOException while selecting selector", e);
                }

                // stop the worker if needed
                synchronized (workerLock) {
                    if (selector.keys().isEmpty()) {
                        worker = null;
                        break;
                    }
                }
            }
        }
    }
}
