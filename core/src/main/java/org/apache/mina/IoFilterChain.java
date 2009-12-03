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

import java.util.List;

/**
 * An implementation that is responsible for perfoming IO (network, file or
 * any other kind of IO)
 *
 * The chain will look something like
 *
 *         Upstream Chain                 DownStream
 *
 *        IoHandler Filter                IoHandler Filter
 *              /|\                              |
 *               |                              \|/
 *           Filter N                        Filter D
 *              /|\                              |
 *               |                              \|/
 *           Filter C                        Filter E
 *              /|\                              |
 *               |                              \|/
 *           Filter B                        Filter F
 *              /|\                              |
 *               |                              \|/
 *           Filter A                      Acceptor/Socket
 *              /|\
 *               |
 *         Acceptor/Socket
 *
 *
 *
 * TODO
 * 1. How to handle the insertion in between the Filter's. Do we need an API?
 * 2. What to do with the fireEvent* methods?
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public interface IoFilterChain {

    /**
     * Returns the parent {@link IoSession} of this chain.
     * @return {@link IoSession}
     */
    IoSession getSession();

    /**
     * Returns all the filters that are currently present in the chain.
     * Useful to the know the current processing chain. The chain is returned
     * in the order of processing aka the first filter in th list shall be the
     * first one to be processed
     *
     * @return  List of all {@link IoFilter} present in the chain
     */
    List<IoFilter> getAll();

    /**
     * Add the specified {@link IoFilter} to the chain. The specific Filter is
     * added at the end of the current processing chain
     *
     * @param ioFilter  Filter to be added in the Chain
     */
    void addFilter(IoFilter ioFilter);

    /**
     * Removes the Filter from the Chain.
     *
     * @param ioFilter  Filter to be removed
     */
    void removeFilter(IoFilter ioFilter);
}