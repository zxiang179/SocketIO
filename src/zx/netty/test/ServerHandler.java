package zx.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class ServerHandler extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
//		((ByteBuf)msg).release();
		try{
			//do sth msg
			ByteBuf buf = (ByteBuf)msg;
			byte[] data = new byte[buf.readableBytes()];
			buf.readBytes(data);
			String request = new String(data,"utf-8");
			System.out.println("server:"+request);
			//д���ͻ���
			String response = "���Ƿ�������Ϣ";
			ChannelFuture channelFuture = ctx.channel().writeAndFlush(Unpooled.copiedBuffer("Hi Client!".getBytes()));
			//д���Ͽ�����
//			channelFuture.addListener(ChannelFutureListener.CLOSE);
//			ctx.channel().flush();
			
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
