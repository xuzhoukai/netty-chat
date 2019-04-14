package com.omen.chat.server.handler;

import com.omen.chat.processor.MsgProcessor;
import com.omen.chat.protocol.IMMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Description:
 * @Auther: xuzhoukai
 * @Date: 2019/4/14 17:26
 */
public class SocketHandler extends SimpleChannelInboundHandler<IMMessage> {
    private MsgProcessor processor = new MsgProcessor();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws Exception {
        processor.sendMsg(ctx.channel(),msg);
    }
}
