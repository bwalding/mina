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

/**
 * A handle which represents connection between two end-points regardless of
 * transport types.
 * <p/>
 * {@link IoSession} provides user-defined attributes.  User-defined attributes
 * are application-specific data which are associated with a session.
 * It often contains objects that represents the state of a higher-level protocol
 * and becomes a way to exchange data between filters and handlers.
 * <p/>
 * <h3>Adjusting Transport Type Specific Properties</h3>
 * <p/>
 * You can simply downcast the session to an appropriate subclass.
 * </p>
 * <p/>
 * <h3>Thread Safety</h3>
 * <p/>
 * {@link IoSession} is thread-safe.  But please note that performing
 * more than one {@link #write(Object)} calls at the same time will
 * cause the {@link IoFilter#filterWrite(IoFilter.NextFilter,IoSession,WriteRequest)}
 * to be executed simultaneously, and therefore you have to make sure the
 * {@link IoFilter} implementations you're using are thread-safe, too.
 * </p>
 * <p/>
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public interface IoSession {
    
    /**
     * the unique identifier of this session
     * @return a unique identifier
     */
    long getId();

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
     * @return the {@link IoService} which provides {@link IoSession} to this session.
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
     * Closes this session immediately or after all queued write requests
     * are flushed.  This operation is asynchronous.  Wait for the returned
     * {@link CloseFuture} if you want to wait for the session actually closed.
     *
     * @param immediately {@code true} to close this session immediately.
     *                    {@code false} to close this session after all queued
     *                    write requests are flushed.
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
     * @return <code>true</code> if suspended
     */
    boolean isReadSuspended();
    
    /**
     * Is write operation is suspended for this session.
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
     * get the session configuration, it where the idle timeout are set
     * and other transport specific configuration. 
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
     * @param key the key of the attribute
     * @return <tt>null</tt> if there is no attribute with the specified key
     */
    Object getAttribute(Object key);

    /**
     * Returns the value of user defined attribute associated with the
     * specified key.  If there's no such attribute, the specified default
     * value is associated with the specified key, and the default value is
     * returned.  This method is same with the following code except that the
     * operation is performed atomically.
     * <pre>
     * if (containsAttribute(key)) {
     *     return getAttribute(key);
     * } else {
     *     setAttribute(key, defaultValue);
     *     return defaultValue;
     * }
     * </pre>
     */
    Object getAttribute(Object key, Object defaultValue);

    /**
     * Sets a user-defined attribute.
     *
     * @param key   the key of the attribute
     * @param value the value of the attribute
     * @return The old value of the attribute.  <tt>null</tt> if it is new.
     */
    Object setAttribute(Object key, Object value);

    /**
     * Sets a user defined attribute without a value.  This is useful when
     * you just want to put a 'mark' attribute.  Its value is set to
     * {@link Boolean#TRUE}.
     *
     * @param key the key of the attribute
     * @return The old value of the attribute.  <tt>null</tt> if it is new.
     */
    Object setAttribute(Object key);
    
    /**
     * Sets a user defined attribute if the attribute with the specified key
     * is not set yet.  This method is same with the following code except
     * that the operation is performed atomically.
     * <pre>
     * if (containsAttribute(key)) {
     *     return getAttribute(key);
     * } else {
     *     return setAttribute(key, value);
     * }
     * </pre>
     */
    Object setAttributeIfAbsent(Object key, Object value);

    /**
     * Sets a user defined attribute without a value if the attribute with
     * the specified key is not set yet.  This is useful when you just want to
     * put a 'mark' attribute.  Its value is set to {@link Boolean#TRUE}.
     * This method is same with the following code except that the operation
     * is performed atomically.
     * <pre>
     * if (containsAttribute(key)) {
     *     return getAttribute(key);  // might not always be Boolean.TRUE.
     * } else {
     *     return setAttribute(key);
     * }
     * </pre>
     */
    Object setAttributeIfAbsent(Object key);

    /**
     * Removes a user-defined attribute with the specified key.
     *
     * @return The old value of the attribute.  <tt>null</tt> if not found.
     */
    Object removeAttribute(Object key);

    /**
     * Removes a user defined attribute with the specified key if the current
     * attribute value is equal to the specified value.  This method is same
     * with the following code except that the operation is performed
     * atomically.
     * <pre>
     * if (containsAttribute(key) && getAttribute(key).equals(value)) {
     *     removeAttribute(key);
     *     return true;
     * } else {
     *     return false;
     * }
     * </pre>
     */
    boolean removeAttribute(Object key, Object value);

    /**
     * Replaces a user defined attribute with the specified key if the
     * value of the attribute is equals to the specified old value.
     * This method is same with the following code except that the operation
     * is performed atomically.
     * <pre>
     * if (containsAttribute(key) && getAttribute(key).equals(oldValue)) {
     *     setAttribute(key, newValue);
     *     return true;
     * } else {
     *     return false;
     * }
     * </pre>
     */
    boolean replaceAttribute(Object key, Object oldValue, Object newValue);

    /**
     * Returns <tt>true</tt> if this session contains the attribute with
     * the specified <tt>key</tt>.
     */
    boolean containsAttribute(Object key);

    /**
     * Returns the set of keys of all user-defined attributes.
     */
    Set<Object> getAttributeKeys();
}