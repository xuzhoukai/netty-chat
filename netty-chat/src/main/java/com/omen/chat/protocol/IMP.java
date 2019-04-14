package com.omen.chat.protocol;

/**
 * @Description:
 * @Auther: xuzhoukai
 * @Date: 2019/4/13 22:52
 */
public enum IMP {
    /**系统消息*/
    SYSTEM("SYSTEM"),
    /**登录*/
    LOGIN("LOGIN"),
    /**退出*/
    LOGOUT("LOGOUT"),
    /**聊天*/
    CHAT("CHAT"),
    /**发送鲜花*/
    FLOWER("FLOWER");

    private String name;

    public static boolean isIMP(String content){
        return content.matches("^\\[(SYSTEM|LOGIN|LOGOUT|CHAT|FLOWER)\\]");
    }

    IMP(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
