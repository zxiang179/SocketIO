package zx.netty.http;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel>{
	
	private final SslContext sslCtx;
	
	public HttpHelloWorldServerInitializer(SslContext sslCtx) {
		this.sslCtx=sslCtx;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
        if(sslCtx !=null){
        	p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        //将位于TCP层的netty协议转为应用层的http协议
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpHelloWorldServerHandler());
	}
}
