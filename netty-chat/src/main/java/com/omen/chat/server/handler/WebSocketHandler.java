package com.omen.chat.server.handler;

import com.omen.chat.processor.MsgProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.logging.Logger;

/**
 * @Description:
 * @Auther: xuzhoukai
 * @Date: 2019/4/13 20:16
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{
    private static final Logger logger = Logger.getLogger(WebSocketHandler.class.getSimpleName());

    private MsgProcessor processor = new MsgProcessor();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
//        System.out.println(msg.text());
        processor.sendMsg(ctx.channel(), msg.text());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel client = ctx.channel();
        String addr =processor.getAddress(client);
        logger.info("client :"+addr+"加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel client = ctx.channel();
        String addr = processor.getAddress(client);
        logger.info("client :"+addr+"离开");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel client = ctx.channel();
        String addr = processor.getAddress(client);
        logger.info("client:" + addr + "掉线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel client = ctx.channel();
        String addr = processor.getAddress(client);
        logger.info("client:"+addr+"异常");
        cause.printStackTrace();
        ctx.close();
    }
}
