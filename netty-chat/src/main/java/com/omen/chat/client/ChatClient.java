package com.omen.chat.client;

import com.omen.chat.client.handler.ChatClientHandler;
import com.omen.chat.protocol.IMDecoder;
import com.omen.chat.protocol.IMEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Description:
 * @Auther: xuzhoukai
 * @Date: 2019/4/14 16:30
 */
public class ChatClient {
    private ChatClientHandler chatClientHandler;
    public ChatClient(String nickName) {
        chatClientHandler  = new ChatClientHandler(nickName);
    }

    public void start(String host, int port){
        EventLoopGroup worker = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    //自定义编码解码
                    pipeline.addLast(new IMDecoder());
                    pipeline.addLast(new IMEncoder());

                    pipeline.addLast(chatClientHandler);
                }
            });
            ChannelFuture future = bootstrap.connect(host,port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ChatClient("开心").start("localhost",80);
    }

}
