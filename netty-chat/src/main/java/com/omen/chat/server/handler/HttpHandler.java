package com.omen.chat.server.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @Description:
 * @Auther: xuzhoukai
 * @Date: 2019/4/13 20:34
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static Logger logger = Logger.getLogger(HttpHandler.class.getSimpleName());
    //获取class路径
    private URL  url = HttpHandler.class.getProtectionDomain().getCodeSource().getLocation();
    private final String webroot = "webroot";
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.getUri();
        RandomAccessFile file = null;
        logger.info("uri:"+uri);
        try{
            String fileName = "/".equals(uri)?"chat.html":uri;
            file = new RandomAccessFile(getResouce(fileName),"r");
        }catch (Exception e){
            logger.info("web端文件读取异常");
            ctx.fireChannelRead(request.retain());
            return;
        }
        HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
        String contextType = "text/html;";
        if(uri.endsWith(".css")){
            contextType = "text/css;";
        }else if(uri.endsWith(".js")){
            contextType = "text/javascript;";
        }else if(uri.endsWith("(jpg|png|gif)$")){
            String ext = uri.substring(uri.lastIndexOf("."));
            contextType = "image/"+ext;
        }
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contextType + "charset=utf-8;");

        boolean keepalived = HttpHeaders.isKeepAlive(request);
        if(keepalived){
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(response);
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if(!keepalived){
            future.addListener(ChannelFutureListener.CLOSE);
        }
        file.close();
    }

    private File getResouce(String fileName) throws Exception {
        String path = url.toURI()+webroot+"/"+fileName;
        if(path.startsWith("file:")){
            path = path.substring(5);
        }
        path.replaceAll("/+","/");
        return new File(path);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel client = ctx.channel();
        logger.info("Client:"+client.remoteAddress()+"异常");
        cause.printStackTrace();
        //出现异常关闭链接
        ctx.close();
    }

    public static void main(String[] args) {
        String path = "//ddd/dd";
        path = path.replaceAll("/+","/");
        System.out.println(path);
    }
}
