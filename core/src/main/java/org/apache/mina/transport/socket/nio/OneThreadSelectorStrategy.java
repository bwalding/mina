package org.apache.mina.transport.socket.nio;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class OneThreadSelectorStrategy implements SelectorStrategy {

    private NioSelectorProcessor processor;
    
    public OneThreadSelectorStrategy() {
        processor = new NioSelectorProcessor("uniqueSelector", this);
    }
    
    @Override
    public void addServerSocket(ServerSocketChannel serverSocketChannel) {
        processor.add(serverSocketChannel);
    }

    @Override
    public void removeServerSocket(ServerSocketChannel serverSocketChannel) {
        processor.remove(serverSocketChannel);
    }

    @Override
    public void createSession(NioSelectorProcessor fromProcessor, SocketChannel newClient) {
        // send it back to the processor
        
    }

}
