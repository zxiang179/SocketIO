package zx.netty.ende2;

import zx.netty.runtime.Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelHandlerAdapter{
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("client channel active...");
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		try{
			Response resp = (Response)msg;
			System.out.println("Client : " + resp.getId() + ", " + resp.getName() + ", " + resp.getResponseMessage());		
		}finally{
			ReferenceCountUtil.release(msg);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
