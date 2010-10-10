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
package org.apache.mina.service;

import org.apache.mina.IoService;
import org.apache.mina.IoServiceListener;
import org.apache.mina.IoSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation for {@link IoService}s.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public abstract class AbstractIoService implements IoService {

    static final Logger LOG = LoggerFactory.getLogger(AbstractIoService.class);
    
    private final Map<Long, IoSession> managedSessions = new ConcurrentHashMap<Long, IoSession>();
    
    /**
     * Placeholder for storing all the listeners added
     */
    private final List<IoServiceListener> listeners = new CopyOnWriteArrayList<IoServiceListener>(); 

    @Override
    public Map<Long, IoSession> getManagedSessions() {
        return managedSessions;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void addListener(IoServiceListener listener) {
        if(listener != null) {
            listeners.add(listener);
            return;
        }

        LOG.warn("Trying to add Null Listener");
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void removeListener(IoServiceListener listener) {
        if(listener != null) {
            listeners.remove(listener);    
        }
    }
}