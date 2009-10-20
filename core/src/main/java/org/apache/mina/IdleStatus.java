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

import java.security.InvalidParameterException;


/**
 * Represents the type of idleness of {@link IoSession}. 
 *  There are three types of idleness:
 * <ul>
 *   <li>{@link #READ_IDLE} - No data is coming from the remote peer.</li>
 *   <li>{@link #WRITE_IDLE} - Session is not writing any data.</li>
 *   <li>{@link #READ_WRITE_IDLE} - Both {@link #READ_IDLE} and {@link #WRITE_IDLE}.</li>
 * </ul>
 * <p>
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public enum IdleStatus {
    READ_IDLE,
    WRITE_IDLE,
    READ_WRITE_IDLE;

    /**
     * Returns the string representation of this status.
     */
    @Override
    public String toString() {
        switch (this) {
        case READ_IDLE:
            return "read idle";
        case WRITE_IDLE:
            return "write idle";
        case READ_WRITE_IDLE:
            return "both idle";
        default:
            throw new InvalidParameterException("unknown IdleStatus");
        }
    }
}