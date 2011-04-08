package org.apache.mina.transport.tcp.bio;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Set;

import org.apache.mina.transport.tcp.AbstractTcpServer;

public class BioTcpServer extends AbstractTcpServer
{

    @Override
    public Set<SocketAddress> getLocalAddresses()
    {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void bind( SocketAddress... localAddress ) throws IOException
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void unbindAll() throws IOException
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void unbind( SocketAddress... localAddresses ) throws IOException
    {
        // TODO Auto-generated method stub

    }

}
