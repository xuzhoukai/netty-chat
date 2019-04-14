package com.omen.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Auther: xuzhoukai
 * @Date: 2019/4/13 23:14
 */
public class IMDecoder extends ByteToMessageDecoder {

    private Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s\\-\\s(.*))?");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try{
            //选获取可以读取的字节数
            final int length = in.readableBytes();
            final byte[] array = new byte[length];
            String content = new String(array,in.readerIndex(),length);
            //空消息不解析
            if(content != null && !"".equals(content.trim())){
                if(!IMP.isIMP(content)){
                    ctx.channel().pipeline().remove(this);
                    return;
                }
            }
            //将数据读入array
            in.getBytes(in.readerIndex(),array,0,length);
            //反序列化并且加入到out集合
            out.add(new MessagePack().read(array,IMMessage.class));
            in.clear();
        }catch (MessageTypeException e){
            ctx.channel().pipeline().remove(this);
        }
    }

    /**
     * 字符串解析自定义协议
     * @param msg
     * @return
     */
    public IMMessage decode(String msg){
        if(msg == null || "".equals(msg.trim())){
            return null;
        }
        try{
            Matcher matcher = pattern.matcher(msg);
            String header = "";
            String content = "";
            if(matcher.matches()){
                header = matcher.group(1);
                content = matcher.group(3);
            }
            String[] headers = header.split("\\]\\[");
            long time = 0;
            try{
                time = Long.parseLong(headers[1]);
            }catch (Exception e){
                e.printStackTrace();
            }
            String nickName = headers[2];
            //昵称最多10个字
            nickName = nickName.length()<10?nickName:nickName.substring(0,9);
            if(msg.startsWith("["+IMP.LOGIN.getName()+"]")){
                return new IMMessage(headers[0],time,nickName);
            }else if(msg.startsWith("["+IMP.CHAT.getName()+"]")){
                return new IMMessage(headers[0],time,nickName,content);
            }else if(msg.startsWith("["+IMP.FLOWER.getName()+"]")){
                return new IMMessage(headers[0],time,nickName);
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
