package zx.netty.runtime;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter{
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		Request request = (Request)msg;
		System.out.println("Server:"+request.getId());
		Response response = new Response();
		response.setId(request.getId());
		response.setName("response:"+request.getId());
		response.setResponseMessage("ÏìÓ¦ÄÚÈÝ"+request.getRequestMessage());;
		ctx.writeAndFlush(response);//.addListener(arg0);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
	}

}
