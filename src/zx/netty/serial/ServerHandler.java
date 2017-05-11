package zx.netty.serial;

import java.net.SocketAddress;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ServerHandler extends ChannelHandlerAdapter {
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Req req =(Req)msg;
		System.out.println("Server:"+req.getId()+","+req.getName()+","+req.getRequestMessage());
		
		Resp resp = new Resp();
		resp.setId(req.getId());
		resp.setName("resp"+req.getId());
		resp.setResponseMessage("ÏìÓ¦ÄÚÈİ"+req.getId());
		ctx.writeAndFlush(resp);
	}


}
