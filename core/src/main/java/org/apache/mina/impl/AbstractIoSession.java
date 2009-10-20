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
package org.apache.mina.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.mina.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of {@link IoSession} shared with all the different
 * transprot implementation. 
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public abstract class AbstractIoSession implements IoSession {
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractIoSession.class); 

    // unique identifier generator
    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    
    private long id;
    
    
    public AbstractIoSession() {
        // generated a unique id
        id = NEXT_ID.getAndIncrement();
        LOG.debug("Created new session with id : {}",id);
    }
    
    @Override
    public long getId() {
        return id;
    }
    
} 
