package com.joe.netty.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyClient {

    private Bootstrap bootstrap;

    private ChannelFuture future;

    private ChannelFuture closingFuture;

    private String host;

    private int port;

    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient("localhost", 8899);
    }

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        EventLoopGroup workerGrp = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workerGrp);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new ObjectEncoder());
                    pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                    pipeline.addLast(new ClientHandler());
                }
            });

            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            connectServer();
            sendMsg();

            Thread.sleep(5000);
            testConnect();



            future.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGrp.shutdownGracefully();
        }


    }

    private void connectServer() throws Exception{
        future = bootstrap.connect(host, port).sync();


    }

    private void testConnect() throws Exception {
        System.out.println("Test Connect.");
        closingFuture = future;
        future = bootstrap.connect(host, port).sync();
    }

    private void sendMsg() {
        future.addListener(haha -> {
            System.out.println("##################################");
            future.channel().writeAndFlush("Hello Server!!!");
        });
    }

}
