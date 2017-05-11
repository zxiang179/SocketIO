package zx.netty.ende2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class Server {
	
	public static void main(String[] args) throws InterruptedException {
		//1 ��һ���߳������ڽ���client�����ӵ�
		NioEventLoopGroup pGroup = new NioEventLoopGroup();
		//2 �ڶ����߳�������ʵ�ʵ�ҵ���������
		NioEventLoopGroup cGroup = new NioEventLoopGroup();
		//3 ����һ��������Bootstrap�����Ƕ����ǵ�Server����һϵ�е�����
		ServerBootstrap b = new ServerBootstrap();
		b.group(pGroup,cGroup)//�����������߳���������
		.channel(NioServerSocketChannel.class)//ʹ��NIOServerSocketChannel�������͵�ͨ��
		.childHandler(new ChannelInitializer<SocketChannel>() {//ʹ��childHandler�󶨾�����¼�������
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				//�����ֳ��ַ�������
				sc.pipeline().addLast(new FixedLengthFrameDecoder(5));
				//�����ַ�����ʽ�Ľ���
				sc.pipeline().addLast(new StringDecoder());
				sc.pipeline().addLast(new ServerHandler());
			}
		})
		/**
		 * ��������TCP�ں�ģ��ά���������У����ǳ�֮ΪA��B
		 * �ͻ������������connect��ʱ�� �ᷢ�ʹ���SYN��־�İ�(��һ������)
		 * �������յ��ͻ��˷�����SYNʱ����ͻ��˷���SYN ACKȷ��(�ڶ�������)
		 * ��ʱTCP�ں�ģ�齫�ͻ������Ӽ���A�����У�Ȼ��������յ��ͻ��˷�����ACKʱ(����������)
		 * TCP�ں�ģ�齫�ͻ��˴�A�����ƶ���B���У�������ɣ�Ӧ�ó����accept�᷵�ء�
		 * Ҳ����˵accept��B������ȡ������������ֵ�����
		 * A���к�B���еĳ���֮����backlog����A��B���еĳ���֮�ʹ���backlogʱ�������ӽ��ᱻTCP�ں˾ܾ�
		 * ���ԣ����backlog��С�����ܻ����accept�ٶȸ����ϣ�A��B�������ˣ������µĿͻ��޷����ӡ�
		 * Ҫע����ǣ�backlog�Գ��������������Ӱ�죬backlogӰ���֪ʶ��û�б�acceptȡ�������ӡ�
		 */
		//����TCP������
//		.option(ChannelOption.SO_BACKLOG, 128)
		//��������
//		.childOption(ChannelOption.SO_KEEPALIVE, true)
		;
		
		//��ָ���Ķ˿ڽ��м���
		ChannelFuture f = b.bind(8765).sync();
//		ChannelFuture f2 = b.bind(8764).sync();
		//������������
		f.channel().closeFuture().sync();
//		f2.channel().closeFuture().sync();
		
		pGroup.shutdownGracefully();
		cGroup.shutdownGracefully();
		
	}

}
