package com.omen.chat.processor;

import com.alibaba.fastjson.JSONObject;
import com.omen.chat.protocol.IMDecoder;
import com.omen.chat.protocol.IMEncoder;
import com.omen.chat.protocol.IMMessage;
import com.omen.chat.protocol.IMP;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @Description:
 * @Auther: xuzhoukai
 * @Date: 2019/4/13 22:35
 */
public class MsgProcessor {

    /**
     *记录在线用户
     */
    private static ChannelGroup onlineUsers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     *定义属性
     */
    private final AttributeKey<String> NICK_NAME = AttributeKey.valueOf("nickName");
    private final AttributeKey<String>IP_ADDR = AttributeKey.valueOf("ipAddr");
    private final AttributeKey<JSONObject>ATTRS =AttributeKey.valueOf("attrs");

    private IMEncoder encoder = new IMEncoder();
    private IMDecoder decoder = new IMDecoder();


    /**
     * 获取远程ip链接地址
     * @param client
     * @return
     */
    public String getAddress(Channel client) {
        return client.remoteAddress().toString().replaceFirst("/","");
    }

    /**
     * 获取昵称
     * @param client
     * @return
     */
    private String getNickName(Channel client){
        return client.attr(NICK_NAME).get();
    }



    public void sendMsg(Channel client, String text) {
        IMMessage request = decoder.decode(text);
        if(request == null){
            return;
        }
        String addr = this.getAddress(client);

        if(IMP.LOGIN.getName().equals(request.getCmd())){
            client.attr(NICK_NAME).getAndSet(request.getSender());
            client.attr(IP_ADDR).getAndSet(addr);
            onlineUsers.add(client);

            for(Channel channel : onlineUsers){
                if(channel == client){
                    request = new IMMessage(IMP.SYSTEM.getName(),sysTime(),onlineUsers.size(),"已与服务器建立链接");
                }else{
                    request = new IMMessage(IMP.SYSTEM.getName(),sysTime(),onlineUsers.size(),getNickName(channel)+"进入聊天室");
                }
                String content = encoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        }else if(IMP.CHAT.getName().equals(request.getCmd())){
            for(Channel channel : onlineUsers){
                if(channel == client){
                    request.setSender("you");
                }else{
                    request.setSender(getNickName(client));
                }
                request.setTime(sysTime());
                String content = encoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        }else if(IMP.FLOWER.getName().equals(request.getCmd())){
            JSONObject attrs =getAttrs(client);
            long currTime = sysTime();
            if(attrs != null){
                long lastTime = attrs.getLongValue("lastFlowerTime");
                int seconds = 10;
                long sub = currTime - lastTime;
                if(sub < seconds*1000){
                    request.setSender("you");
                    request.setCmd(IMP.SYSTEM.getName());
                    request.setTime(sysTime());
                    request.setContent("您送花太频繁了，请等"+(10 - sub/1000)+"秒后再试");
                    String content = encoder.encode(request);
                    client.writeAndFlush(new TextWebSocketFrame(content));
                }
            }
            for(Channel channel:onlineUsers){
                if(channel == client){
                    request.setSender("you");
                    request.setContent("你送给大家一波鲜花");
                    setAttrs(client,"lastFlowerTime",currTime);
                }else{
                    request.setSender(getNickName(client));
                    request.setTime(sysTime());
                    request.setContent("送出了一波鲜花");
                }
                request.setTime(sysTime());
                String content = encoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        }
    }

    private void setAttrs(Channel client, String key, long value) {
        try {
            JSONObject jsonObject = client.attr(ATTRS).get();
            jsonObject.put(key,value);
            client.attr(ATTRS).set(jsonObject);
        }catch (Exception e){
            JSONObject jsonObject  = new JSONObject();
            jsonObject.put(key,value);
            client.attr(ATTRS).set(jsonObject);
        }

    }

    private JSONObject getAttrs(Channel client) {
        try{
            return client.attr(ATTRS).get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private long sysTime() {
        return System.currentTimeMillis();
    }

    public void sendMsg(Channel channel, IMMessage msg) {
        String content = encoder.encode(msg);
        sendMsg(channel,content);
    }
}
