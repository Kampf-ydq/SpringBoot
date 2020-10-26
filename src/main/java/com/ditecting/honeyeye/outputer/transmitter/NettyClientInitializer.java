package com.ditecting.honeyeye.outputer.transmitter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/15 21:18
 */
public class NettyClientInitializer  extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(64*1024, buf));
        socketChannel.pipeline().addLast("decoder", new StringDecoder());
        socketChannel.pipeline().addLast("encoder", new StringEncoder());
        socketChannel.pipeline().addLast(new NettyClientHandler());
    }
}