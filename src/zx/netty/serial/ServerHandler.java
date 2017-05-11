package zx.netty.serial;

import java.io.File;
import java.io.FileOutputStream;
import java.net.SocketAddress;

import zx.netty.util.GzipUtil;
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
		
		byte[] attachment = GzipUtil.ungzip(req.getAttachment());
		String path = System.getProperty("user.dir")+File.separatorChar+"receive"+File.separatorChar+"001.jpg";
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(attachment);
		fos.close();
		
		Resp resp = new Resp();
		resp.setId(req.getId());
		resp.setName("resp"+req.getId());
		resp.setResponseMessage("ÏìÓ¦ÄÚÈÝ"+req.getId());
		ctx.writeAndFlush(resp);
	}


}
