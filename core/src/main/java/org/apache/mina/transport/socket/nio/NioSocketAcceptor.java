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
import java.nio.channels.ServerSocketChannel;
import java.security.InvalidParameterException;
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

    private Map<SocketAddress,ServerSocketChannel> serverSocketChannels = new ConcurrentHashMap<SocketAddress, ServerSocketChannel>();
    
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
                serverSocketChannels.put(address,serverSocketChannel); 
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
            }
        }
    }

    @Override
    public void unbindAll() throws IOException {
        for (SocketAddress socketAddress:serverSocketChannels.keySet()) {
            unbind(socketAddress);
        }
    }
}
