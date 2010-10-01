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

import java.net.SocketAddress;
import java.util.Set;

import org.apache.mina.service.IoHandler;

/**
 * A handle which represents connection between two end-points regardless of
 * transport types.
 * <p/>
 * {@link IoSession} provides user-defined attributes. User-defined attributes
 * are application-specific data which are associated with a session. It often
 * contains objects that represents the state of a higher-level protocol and
 * becomes a way to exchange data between filters and handlers.
 * <p/>
 * <h3>Adjusting Transport Type Specific Properties</h3>
 * <p/>
 * You can simply downcast the session to an appropriate subclass.
 * </p>
 * <p/>
 * <h3>Thread Safety</h3>
 * <p/>
 * {@link IoSession} is thread-safe. But please note that performing more than
 * one {@link #write(Object)} calls at the same time will cause the
 * {@link IoFilter#filterWrite(IoFilter.NextFilter,IoSession,WriteRequest)} to
 * be executed simultaneously, and therefore you have to make sure the
 * {@link IoFilter} implementations you're using are thread-safe, too.
 * </p>
 * <p/>
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public interface IoSession {

    /**
     * the unique identifier of this session
     * 
     * @return a unique identifier
     */
    long getId();

    /**
     * @return the {@link IoHandler} which handles this session.
     */
    IoHandler getHandler();

    /* ADDRESSES */

    /**
     * Returns the socket address of remote peer.
     */
    SocketAddress getRemoteAddress();

    /**
     * Returns the socket address of local machine which is associated with this
     * session.
     */
    SocketAddress getLocalAddress();

    /**
     * @return the {@link IoService} which provides {@link IoSession} to this
     *         session.
     */
    IoService getService();

    /* READ / WRITE / CLOSE */

    /**
     * Returns <code>true</code> if this session is connected with remote peer.
     */
    boolean isConnected();

    /**
     * Returns <code>true</tt> if and only if this session is being closed
     * (but not disconnected yet) or is closed.
     */
    boolean isClosing();

    /**
     * Closes this session immediately or after all queued write requests are
     * flushed. This operation is asynchronous. Wait for the returned
     * {@link CloseFuture} if you want to wait for the session actually closed.
     * 
     * @param immediately
     *            {@code true} to close this session immediately. {@code false}
     *            to close this session after all queued write requests are
     *            flushed.
     */
    CloseFuture close(boolean immediately);

    /* READ/WRITE PAUSE MANAGEMENT */

    /**
     * Suspends read operations for this session.
     */
    void suspendRead();

    /**
     * Suspends write operations for this session.
     */
    void suspendWrite();

    /**
     * Resumes read operations for this session.
     */
    void resumeRead();

    /**
     * Resumes write operations for this session.
     */
    void resumeWrite();

    /**
     * Is read operation is suspended for this session.
     * 
     * @return <code>true</code> if suspended
     */
    boolean isReadSuspended();

    /**
     * Is write operation is suspended for this session.
     * 
     * @return <code>true</code> if suspended
     */
    boolean isWriteSuspended();

    /* BASIC STATS */

    /**
     * Returns the total number of bytes which were read from this session.
     */
    long getReadBytes();

    /**
     * Returns the total number of bytes which were written to this session.
     */
    long getWrittenBytes();

    /* IDLE */

    /**
     * get the session configuration, it where the idle timeout are set and
     * other transport specific configuration.
     * 
     * @return the configuration of this session.
     */
    IoSessionConfig getConfig();

    /**
     * @return the session's creation time in milliseconds
     */
    long getCreationTime();

    /**
     * Returns the time in millis when I/O occurred lastly.
     */
    long getLastIoTime();

    /**
     * Returns the time in millis when read operation occurred lastly.
     */
    long getLastReadTime();

    /**
     * Returns the time in millis when write operation occurred lastly.
     */
    long getLastWriteTime();

    /* ATTACHEMENT MANAGEMENT */

    /**
     * Returns the value of the user-defined attribute of this session.
     * 
     * @param name
     *            the name of the attribute
     * @return <tt>null</tt> if there is no attribute with the specified name
     */
    Object getAttribute(Object name);

    /**
     * Sets a user-defined attribute.
     * 
     * @param name
     *            the name of the attribute
     * @param value
     *            the value of the attribute
     * @return The old value of the attribute. <tt>null</tt> if it is new.
     */
    Object setAttribute(Object name, Object value);

    /**
     * Removes a user-defined attribute with the specified name.
     * 
     * @param name
     *            the name of the attribute
     * @return The old value of the attribute. <tt>null</tt> if not found.
     */
    Object removeAttribute(Object name);

    /**
     * Returns <tt>true</tt> if this session contains the attribute with the
     * specified <tt>name</tt>.
     */
    boolean containsAttribute(Object name);

    /**
     * Returns the set of names of all user-defined attributes.
     */
    Set<Object> getAttributeNames();
}