package com.omen.chat.server;

import com.omen.chat.protocol.IMDecoder;
import com.omen.chat.protocol.IMEncoder;
import com.omen.chat.server.handler.HttpHandler;
import com.omen.chat.server.handler.SocketHandler;
import com.omen.chat.server.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.logging.Logger;


/**
 * @Description:聊天服务器
 * @Auther: xuzhoukai
 * @Date: 2019/4/13 19:59
 */
public class ChatServer {
    private static Logger logger = Logger.getLogger(ChatServer.class.getSimpleName());
    public void start(int port){
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try{
            ServerBootstrap server = new ServerBootstrap();
            server.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            //自定义编码解码
                            pipeline.addLast(new IMDecoder());
                            pipeline.addLast(new IMEncoder());
                            pipeline.addLast(new SocketHandler());

                            //解析http请求
                            pipeline.addLast(new HttpServerCodec());
                            //主要是将http请求或者响应的多个消息变成一个fullHttpRequest完整信息
                            pipeline.addLast(new HttpObjectAggregator(64*1024));
                            //主要是用于处理大数据流，防止撑爆jvm内存
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new HttpHandler());

                            //websocket
                            pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
                            pipeline.addLast(new WebSocketHandler());

                        }
                    });
            logger.info("服务器启动，端口："+port);
            ChannelFuture future = server.bind(port).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ChatServer().start(80);
    }
}
