package com.example.pasha.lab4_netty.network.client;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Custom handler stores all responses from server in collection,
 * adds new responses to collection, handles exceptions and provides method
 * to obtain all response messages
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    /** All response messages */
    private List<String> messages = new ArrayList<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        messages.add((String) msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public List<String> getMessages() {
        return messages;
    }
}