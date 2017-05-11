package zx.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class Server {

	public void run(int port){
		NioEventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioDatagramChannel.class)
			.option(ChannelOption.SO_BROADCAST, true)
			.handler(new ServerHandler());
			b.bind(port).sync().channel().closeFuture().await();
		} catch (InterruptedException e) {
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		new Server().run(8765);
		new Server().run(8764);
	}
	
}
