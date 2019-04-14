package com.omen.chat.protocol;

import org.msgpack.annotation.Message;

/**
 * @Description:
 * @Auther: xuzhoukai
 * @Date: 2019/4/13 22:51
 */
@Message
public class IMMessage {
    /**ip地址及端口*/
    private String addr;
    /**命令类型SYSTEM/LOGIN/LOGOUT/CHAT/FLOWER*/
    private String cmd;
    /**发送命令时间*/
    private long time;
    /**在线人数*/
    private int online;
    /**发送者*/
    private String sender;
    /**接收者*/
    private String receiver;
    /**消息内容*/
    private String content;

    public IMMessage() {
    }

    public IMMessage(String cmd, long time, String nickName) {
        this.cmd = cmd;
        this.time = time;
        this.sender = nickName;
    }

    public IMMessage(String cmd, long time, String sender, String content) {
        this.cmd = cmd;
        this.time = time;
        this.sender = sender;
        this.content = content;
    }

    public IMMessage(String cmd,long time,int online,String content){
        this.cmd = cmd;
        this.time = time;
        this.online = online;
        this.content = content;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "IMMessage{" +
                "addr='" + addr + '\'' +
                ", cmd='" + cmd + '\'' +
                ", time=" + time +
                ", online=" + online +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
