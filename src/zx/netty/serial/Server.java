package zx.netty.serial;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
	
	public static void main(String[] args) throws InterruptedException {
		NioEventLoopGroup pGroup = new NioEventLoopGroup();
		NioEventLoopGroup cGroup = new NioEventLoopGroup();
		
		ServerBootstrap b = new ServerBootstrap();
		b.group(pGroup,cGroup)
		.channel(NioServerSocketChannel.class)
		.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				sc.pipeline().addLast(MarshallingCodeFactory.buildMarshallingDecoder());
				sc.pipeline().addLast(MarshallingCodeFactory.buildMarshallingEncoder());
				sc.pipeline().addLast(new ServerHandler());
			}
		});
		
		ChannelFuture cf = b.bind(8765).sync();
		cf.channel().closeFuture().sync();
		pGroup.shutdownGracefully();
		cGroup.shutdownGracefully();
	}

}
