package com.example.pasha.lab4_netty.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Opens connection to server, sends messages to server,
 * receives response messages from server
 */
public class Client {

    /** Server host */
    private static final String HOST = "127.0.0.1";

    /** Server port */
    private static final int PORT = 10000;

    /** Channel to interact with server */
    private Channel channel;

    /** Custom handler */
    private ClientHandler clientHandler;

    /**
     * Creates bootstrap and opens a new channel to interact with servers
     */
    public Client() {
        Bootstrap bootstrap = createBootstrap();
        channel = createChannel(bootstrap);
    }

    /**
     * Sends new message to server
     *
     * @param msg message to send
     */
    public void sendMessage(String msg) {
        try {
            channel.writeAndFlush(msg).sync();
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns last response from server
     *
     * @return last response
     */
    public String getResponse() {
        return clientHandler.getMessages().get(clientHandler.getMessages().size() - 1);
    }

    /**
     * Creates bootstrap used in future to create channel
     *
     * @return bootstrap
     */
    private Bootstrap createBootstrap() {

        // EventLoopGroup is used to register the channels
        // Use 4 threads
        EventLoopGroup eventLoop = new NioEventLoopGroup(4);

        // Bootstrap is used to open a connection
        Bootstrap bootstrap = new Bootstrap();
        // EventLoopGroup is used to handle all the events for the channel
        bootstrap.group(eventLoop);
        // Create channel
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

        clientHandler = new ClientHandler();
        // Add handlers to pipeline
        bootstrap.handler(
                new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(clientHandler);
                    }
                }
        );

        return bootstrap;
    }

    private Channel createChannel(Bootstrap bootstrap) {

        try {
            // Connect to host and port
            ChannelFuture channelFuture = bootstrap.connect(HOST, PORT).sync();
            // Get channel when connection is established (sync())
            channel = channelFuture.channel();

        } catch (InterruptedException e) {
            throw new IllegalStateException("Client was interrupted", e);
        }

        return channel;
    }
}
