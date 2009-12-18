package org.apache.mina.service;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.mina.transport.socket.nio.SelectorStrategy;

public class OneThreadSelectorStrategy implements SelectorStrategy {

    private SelectorProcessor processor;
    
    public OneThreadSelectorStrategy(SelectorFactory selectorFactory) {
        this.processor = selectorFactory.getNewSelector("uniqueSelector", this);
    }
    
    @Override
    public SelectorProcessor getSelectorForBindNewAddress() {
        return processor;
    }

    @Override
    public SelectorProcessor getSelectorForNewSession(SelectorProcessor acceptingProcessor) {
        return processor;
    }

    @Override
    public void unbind(SocketAddress address) throws IOException {
        processor.unbind(address);
    }

}
