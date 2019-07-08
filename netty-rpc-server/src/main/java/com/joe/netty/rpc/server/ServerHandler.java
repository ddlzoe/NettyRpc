package com.joe.netty.rpc.server;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        executorService.execute(()->{
            System.out.println("Channel Read..." + msg);
            try {
                Thread.sleep(20000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ctx.writeAndFlush("Hello Client");
        });
        super.channelRead(ctx, msg);
    }
}
