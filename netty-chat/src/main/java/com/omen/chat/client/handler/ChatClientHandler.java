package com.omen.chat.client.handler;

import com.omen.chat.protocol.IMMessage;
import com.omen.chat.protocol.IMP;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @Description:
 * @Auther: xuzhoukai
 * @Date: 2019/4/14 16:40
 */
public class ChatClientHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = Logger.getLogger(ChatClientHandler.class.getName());
    private String nickName;
    private ChannelHandlerContext context;

    public ChatClientHandler(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        IMMessage message = (IMMessage) msg;
        logger.info(message.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("异常："+ctx.channel().remoteAddress().toString());
        ctx.close();
    }

    /**
     * tcp链路连通后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("登录成功");
        this.context = ctx;
        IMMessage message = new IMMessage(IMP.LOGIN.getName(),System.currentTimeMillis(),this.nickName);
        sendMessage(message);
        session();

    }

    private void session() {
        new Thread(()->{
            logger.info(this.nickName+"请输入信息：");
            IMMessage message = null;
            Scanner scanner = new Scanner(System.in);
            do{
                if(scanner.hasNext()){
                    String input = scanner.nextLine();
                    if("exit".equals(input)){
                        message = new IMMessage(IMP.LOGOUT.getName(),System.currentTimeMillis(),this.nickName);
                    }else{
                        message = new IMMessage(IMP.CHAT.getName(),System.currentTimeMillis(),this.nickName,input);
                    }
                }
            }while (sendMessage(message));
            scanner.close();
        }).start();
    }

    /**
     * 是否退出
     * @param message
     */
    private boolean sendMessage(IMMessage message) {
        this.context.channel().writeAndFlush(message);
        logger.info("消息发送到面板，请继续输入");
        return message.getCmd().equals(IMP.LOGOUT.getName())?false:true;

    }
}
