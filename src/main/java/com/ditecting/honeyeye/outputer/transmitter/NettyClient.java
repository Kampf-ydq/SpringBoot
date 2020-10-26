package com.ditecting.honeyeye.outputer.transmitter;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/15 16:24
 */
public class NettyClient {
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private String inetHost;
    private int inetPort;

    public NettyClient (String inetHost, int inetPort) {
        this.inetHost = inetHost;
        this.inetPort = inetPort;
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .option(ChannelOption.TCP_NODELAY, true)//CLose Nagle algorithm
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }

    public void start (String string){
        try {
            ChannelFuture channelFuture = bootstrap.connect(inetHost, inetPort).sync();
            channelFuture.channel().writeAndFlush(Unpooled.wrappedBuffer((string + "$_").getBytes()));
            channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close () {
        group.shutdownGracefully();
    }
}