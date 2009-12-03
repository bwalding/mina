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

package org.apache.mina;

/**
 * Filter are interceptors/processors for incoming data received/sent.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public interface IoFilter {

    /**
     * Returns the Name of the Filter. A name is used to uniquely identify
     * the FIlter
     *
     * @return  Name of the Filter
     */
    String getName();

    /**
     * Invoked when this filter is added to a {@link IoFilterChain}
     * at the first time, so you can initialize shared resources.
     *
     * @throws Exception If an initialization error occurs
     */
    void init() throws Exception;

    /**
     * Invoked when this filter is not used by any {@link IoFilterChain} anymore,
     * so you can destroy shared resources.
     *
     * @throws Exception If an error occurs while processing
     */
    void destroy() throws Exception;

    //---- Events Functions ---
    /**
     * Invoked from an I/O processor thread when a new connection has been created.
     * Because this method is supposed to be called from the same thread that
     * handles I/O of multiple sessions, please implement this method to perform
     * tasks that consumes minimal amount of time such as socket parameter
     * and user-defined session attribute initialization.
     *
     * @param session {@link IoSession} associated with the invocation
     *
     * @throws Exception Exception If an error occurs while processing
     */
    void sessionCreated(IoSession session) throws Exception;

    /**
     * Invoked when a connection has been opened.
     *
     * @param session {@link IoSession} associated with the invocation
     * @throws Exception Exception If an error occurs while processing
     */
    void sessionOpened(IoSession session) throws Exception;

    /**
     * Invoked when a connection is closed.
     *
     * @param session {@link IoSession} associated with the invocation
     * @throws Exception Exception If an error occurs while processing
     */
    void sessionClosed(IoSession session) throws Exception;

    /**
     * Invoked with the related {@link IdleStatus} when a connection becomes idle.
     *
     * @param session {@link IoSession} associated with the invocation
     * @throws Exception Exception If an error occurs while processing
     */
    void sessionIdle(IoSession session, IdleStatus status) throws Exception;

    /**
     * Invoked when a message is received.
     *
     * @param session {@link IoSession} associated with the invocation
     * @throws Exception Exception If an error occurs while processing
     */
    void messageReceived(IoSession session, Object message) throws Exception;
}