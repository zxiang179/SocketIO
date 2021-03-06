package zx.netty.serial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jboss.marshalling.Marshalling;

import zx.netty.util.GzipUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Client {
	
	public static void main(String[] args) throws Exception {
		NioEventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group)
		.channel(NioSocketChannel.class)
		.option(ChannelOption.SO_BACKLOG, 1024)
		//设置日志
		.handler(new LoggingHandler(LogLevel.INFO))
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				sc.pipeline().addLast(MarshallingCodeFactory.buildMarshallingDecoder());
				sc.pipeline().addLast(MarshallingCodeFactory.buildMarshallingEncoder());
				sc.pipeline().addLast(new ClientHandler());
			}
		});
		ChannelFuture cf = b.connect("127.0.0.1",8765).sync();
		
		for(int i=0;i<5;i++){
			Req req = new Req();
			req.setId(""+i);
			req.setName("pro"+i);
			req.setRequestMessage("数据信息"+i);
			
			//读取文件
	    	String readPath = System.getProperty("user.dir") + File.separatorChar + "sources" +  File.separatorChar + "001.jpg";
	        File file = new File(readPath);  
	        FileInputStream in = new FileInputStream(file);  
	        byte[] data = new byte[in.available()];  
	        in.read(data);  
	        in.close();  
	        req.setAttachment(GzipUtil.gzip(data));
	        
			cf.channel().writeAndFlush(req);
		}
		
		cf.channel().closeFuture().sync();
		group.shutdownGracefully();
		
	}

}
