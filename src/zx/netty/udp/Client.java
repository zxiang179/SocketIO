package zx.netty.udp;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;


public class Client {
	
	public void run(int port) throws InterruptedException{
		NioEventLoopGroup group = new NioEventLoopGroup();
		
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioDatagramChannel.class)
			.option(ChannelOption.SO_BROADCAST, true)
			.handler(new ClientHandler());
			Channel ch = b.bind(0).sync().channel();
			//�����������еļ���㲥UDP��Ϣ
			ch.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("�����ֵ��ѯ��", CharsetUtil.UTF_8),
					new InetSocketAddress("255.255.255.255",port))).sync();
			if(ch.closeFuture().await(15000)){
				System.out.println("��ѯ��ʱ");
			}
		} finally{
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		new Client().run(8765);
	}

}
