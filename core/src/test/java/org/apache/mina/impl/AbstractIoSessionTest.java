package org.apache.mina.impl;

import java.net.SocketAddress;

import junit.framework.Assert;

import org.apache.mina.CloseFuture;
import org.apache.mina.IoSessionConfig;
import org.apache.mina.session.AbstractIoSession;
import org.junit.Test;

public class AbstractIoSessionTest {

    @Test
    public void testGetId() {
        AbstractIoSession aio = new AbstractIoSession(null) {

            @Override
            public CloseFuture close(boolean immediately) {
                return null;
            }

            @Override
            public IoSessionConfig getConfig() {
                return null;
            }

            @Override
            public SocketAddress getLocalAddress() {
                return null;
            }

            @Override
            public SocketAddress getRemoteAddress() {
                return null;
            }

            @Override
            public boolean isClosing() {
                return false;
            }

            @Override
            public boolean isConnected() {
                return false;
            }

            @Override
            public boolean isReadSuspended() {
                return false;
            }

            @Override
            public boolean isWriteSuspended() {
                return false;
            }

            @Override
            public void resumeRead() {
            }

            @Override
            public void resumeWrite() {
            }

            @Override
            public void suspendRead() {
            }

            @Override
            public void suspendWrite() {
            }
            
        };
        
        System.out.println("Session Id : "+aio.getId());
        String value = "value";
        Assert.assertNull( aio.getAttribute("test") );
        Assert.assertEquals(null,aio.setAttribute("test", value));
        Assert.assertTrue(aio.containsAttribute("test"));
        Assert.assertEquals(aio.getAttributeNames().size(), 1);
        Assert.assertEquals(value,aio.setAttribute("test", value));
        Assert.assertEquals(aio.getAttributeNames().size(), 1);
        Assert.assertTrue(aio.containsAttribute("test"));
        Assert.assertEquals(value,aio.getAttribute("test"));
        Assert.assertEquals(value,aio.removeAttribute("test"));
        Assert.assertEquals(aio.getAttributeNames().size(), 0);
        Assert.assertFalse(aio.containsAttribute("test"));
        
        Assert.assertEquals(null, aio.getAttribute("test"));
        
        Assert.assertNull(aio.getService());
    }

}
