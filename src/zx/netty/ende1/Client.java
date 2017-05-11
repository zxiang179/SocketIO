package zx.netty.ende1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class Client {
	
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup workgroup = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(workgroup)
		.channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				//
				ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
				sc.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,buf));
				sc.pipeline().addLast(new StringDecoder());
				sc.pipeline().addLast(new ClientHandler());
			}
		});
		
		ChannelFuture cf1 = b.connect("127.0.0.1",8765).sync();
//		ChannelFuture cf2 = b.connect("127.0.0.1",8764).sync();
		//buf
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("bbbb$_".getBytes()));
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("cccccccc&_".getBytes()));
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("aaaaaaaaaaaaa&_".getBytes()));
//		cf1.channel().flush();
		
		cf1.channel().closeFuture().sync();
		workgroup.shutdownGracefully();
	}
	

}
