package com.example.pasha.lab4_netty.network.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Server binds port, receives messages from clients and send their messages back
 */
public class Server {

    /**
     * Port to bind
     */
    private static final int PORT = 10000;

    /**
     * Prepares all handlers, binds port
     */
    public void bind() {

        // Configure the server: 1 boss and 4 workers
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);

        try {
            // Create bootstrap and add all handlers
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 100);
            bootstrap.childHandler(
                    new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new ServerHandler());
                        }
                    }
            );

            ChannelFuture f = bootstrap.bind(PORT).sync();
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new IllegalStateException("Process was interrupted", e);

        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
