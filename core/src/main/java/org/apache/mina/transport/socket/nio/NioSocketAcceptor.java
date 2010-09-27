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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.mina.IoServiceListener;
import org.apache.mina.service.AbstractIoAcceptor;
import org.apache.mina.service.SelectorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO 
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class NioSocketAcceptor extends AbstractIoAcceptor {
    
    static final Logger LOG = LoggerFactory.getLogger(NioSocketAcceptor.class);
    
    // list of bound addresses
    private Set<SocketAddress> addresses = Collections.synchronizedSet(new HashSet<SocketAddress>());
    
    // map of the created selection keys, mainly used for cancelling them.
   // private Map<SocketAddress,NioSelectorProcessor> serverSocketChannels = new ConcurrentHashMap<SocketAddress, NioSelectorProcessor>();
    
    // the strategy for dispatching servers and client to selector threads.
    private SelectorStrategy strategy;
    
    public NioSocketAcceptor(SelectorStrategy strategy) {
        this.strategy = strategy; 
    }
    
    
    @Override
    public void bind(SocketAddress... localAddress) throws IOException {
        if ( localAddress == null ) {
            // We should at least have one address to bind on
            throw new IllegalStateException( "LocalAdress cannot be null" );
        }
        
        for(SocketAddress address : localAddress) {
            // check if the address is already bound
            synchronized (this) {
                if (addresses.contains(address)) {
                    throw new IOException("address "+address+" already bound");
                }
                
                LOG.debug("binding address {}",address);
                
                addresses.add(address);
                NioSelectorProcessor processor = (NioSelectorProcessor)strategy.getSelectorForBindNewAddress();
                processor.bindAndAcceptAddress(address);
            }
        }
    }

    @Override
    public Set<SocketAddress> getLocalAddresses() {
        return addresses;
    }

    @Override
    public void unbind(SocketAddress... localAddresses) throws IOException {
        for (SocketAddress socketAddress : localAddresses) {
            LOG.debug("unbinding {}",socketAddress);
            synchronized (this) {
                strategy.unbind(socketAddress);
            }
        }
    }

    @Override
    public void unbindAll() throws IOException {
        for (SocketAddress socketAddress: addresses) {
            unbind(socketAddress);
        }
    }

    
}