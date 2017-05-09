package zx.netty.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
	
	public static void main(String[] args) throws InterruptedException {
		//1 第一个线程组用于接受client端连接的
		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		//2 第二个线程组用于实际的业务处理操作的
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		//3 创建一个辅助类Bootstrap，就是对我们的Server进行一系列的配置
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup,workerGroup)//把两个工作线程组加入进来
		.channel(NioServerSocketChannel.class)//使用NIOServerSocketChannel这种类型的通道
		.childHandler(new ChannelInitializer<SocketChannel>() {//使用childHandler绑定具体的事件处理器
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				sc.pipeline().addLast(new ServerHandler());
			}
		})
		/**
		 * 服务器端TCP内核模块维护两个队列，我们称之为A和B
		 * 客户端向服务器端connect的时候 会发送带有SYN标志的包(第一次握手)
		 * 服务器收到客户端发来的SYN时，向客户端发送SYN ACK确认(第二次握手)
		 * 此时TCP内核模块将客户端连接加入A队列中，然后服务器收到客户端发来的ACK时(第三次握手)
		 * TCP内核模块将客户端从A队列移动到B队列，连接完成，应用程序的accept会返回。
		 * 也就是说accept从B队列中取出完成三次握手的连接
		 * A队列和B队列的长度之和是backlog。当A、B队列的长度之和大于backlog时，新连接将会被TCP内核拒绝
		 * 所以，如果backlog过小，可能会出现accept速度跟不上，A、B队列满了，导致新的客户无法连接。
		 * 要注意的是：backlog对程序的连接数并无影响，backlog影响的知识还没有被accept取出的连接。
		 */
		//设置TCP缓冲区
		.option(ChannelOption.SO_BACKLOG, 128)
		//保持连接
		.childOption(ChannelOption.SO_KEEPALIVE, true);
		
		//绑定指定的端口进行监听
		ChannelFuture f = b.bind(8765).sync();
		//将服务器阻塞
		f.channel().closeFuture().sync();
		
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		
	}

}
