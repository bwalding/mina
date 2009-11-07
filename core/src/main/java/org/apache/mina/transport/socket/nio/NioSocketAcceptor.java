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
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.service.AbstractIoAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO 
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class NioSocketAcceptor extends AbstractIoAcceptor {
    
    static final Logger LOG = LoggerFactory.getLogger(NioSocketAcceptor.class);
    
    // timeout for the selector accepting connections
    private static final int SELECT_ACCEPT_TIMEOUT = 1000;

    // map of the created selection keys, mainly used for cancelling them.
    private Map<SocketAddress,ServerSocketChannel> serverSocketChannels = new ConcurrentHashMap<SocketAddress, ServerSocketChannel>();
    
    // object in charge of selecting server socket for accepting client connections
    private ClientAcceptor acceptor = new ClientAcceptor();
    
    @Override
    public void bind(SocketAddress... localAddress) throws IOException {
   
        for(SocketAddress address : localAddress) {
            // check if the address is already bound
            synchronized (this) {
                if (serverSocketChannels.containsKey(address)) {
                    throw new IOException("address "+address+" already bound");
                }
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                LOG.debug("binding address {}",address);
                serverSocketChannel.socket().bind(address);
                serverSocketChannel.configureBlocking(false);
                serverSocketChannels.put(address,serverSocketChannel);
                // add the server socket to the acceptor selector thread
                acceptor.add(serverSocketChannel);
            }
        }
    }

    @Override
    public Set<SocketAddress> getLocalAddresses() {
        return serverSocketChannels.keySet();
    }

    @Override
    public void unbind(SocketAddress... localAddresses) throws IOException {
        for (SocketAddress socketAddress : localAddresses) {
            LOG.debug("unbinding {}",socketAddress);
            synchronized (this) {
                ServerSocketChannel channel = serverSocketChannels.get(socketAddress);
                if (channel == null) {
                    throw new InvalidParameterException("localAddresses");
                }
                channel.socket().close();
                serverSocketChannels.remove(socketAddress);
                // remove the server socket form the selector accepting connections 
                acceptor.remove(channel);
            }
        }
    }

    @Override
    public void unbindAll() throws IOException {
        for (SocketAddress socketAddress:serverSocketChannels.keySet()) {
            unbind(socketAddress);
        }
    }
    
    /**
     * Working thread accepting client connection on bound server channels.
     * @author <a href="http://mina.apache.org">Apache MINA Project</a>
     *
     */
    private class ClientAcceptor {
        
        // selector used for listen to accept events (when a client want to connect to the bound
        // server port)
        private Selector selector;
        
        // methods using this map are synchronised, so we can use a non thread safe HashMap
        private Map<ServerSocketChannel,SelectionKey> keyForChannels = new HashMap<ServerSocketChannel, SelectionKey>(1);
        
        /**
         * Add a channel to the selector, 
         * will start accepting connections.
         * @param serverSocketChannel the channel to accept
         */
        public synchronized void add(ServerSocketChannel serverSocketChannel) {
            LOG.debug("adding channel {} to the acceptor thread",serverSocketChannel.socket().getInetAddress());
            // if no selector, we create one
            try {
                if (selector == null) {
                    selector = Selector.open();
                }
                SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                keyForChannels.put(serverSocketChannel, key);
            } catch (IOException e) {
                LOG.error("IOException while registering a new ServerSocketChannel",e);
            }
            // spawn again the worker if needed
            if (worker == null || !worker.isAlive()) {
                worker = new ClientAcceptorWorker();
                worker.start();
            }
        }
        
        /**
         * Remove a channel from the selector, will stop accepting connections.
         * @param serverSocketChannel the channel to remove
         */
        public synchronized void remove(ServerSocketChannel serverSocketChannel) {
            LOG.debug("removing channel {} from the acceptor thread",serverSocketChannel.socket().getInetAddress());
            if (selector == null || serverSocketChannel == null) {
                throw new InvalidParameterException("serverSocketChannel");
            }
            // get the key and cancel it
            SelectionKey key = keyForChannels.remove(serverSocketChannel);
            if (key == null) {
                throw new InvalidParameterException("serverSocketChannel");
            }
            key.cancel();
        }
 
        private ClientAcceptorWorker worker;
        
        private class ClientAcceptorWorker extends Thread {
            
            @Override
            public void run() {
                int keyCount;
                synchronized (this) {
                    keyCount = keyForChannels.size();
                }
                while(keyCount >0) {
                    try {
                        int eventCount = selector.select(SELECT_ACCEPT_TIMEOUT);
                        for (int i = 0;i < eventCount; i++) {
                            
                        }
                    } catch (IOException e) {
                        LOG.error("IOException while accepting connections");
                    }
                    
                }
                // close the selector, because all the server socket was removed and unbound
                // a selector will be reopen for the next added selector.
                try {
                    selector.close();
                } catch (IOException e) {
                    LOG.error("IOException while closing accept selector",e);
                }
            }                
        }
    }
}