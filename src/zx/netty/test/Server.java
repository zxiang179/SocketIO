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
		//1 ��һ���߳������ڽ���client�����ӵ�
		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		//2 �ڶ����߳�������ʵ�ʵ�ҵ���������
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		//3 ����һ��������Bootstrap�����Ƕ����ǵ�Server����һϵ�е�����
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup,workerGroup)//�����������߳���������
		.channel(NioServerSocketChannel.class)//ʹ��NIOServerSocketChannel�������͵�ͨ��
		.childHandler(new ChannelInitializer<SocketChannel>() {//ʹ��childHandler�󶨾�����¼�������
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
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
		.option(ChannelOption.SO_BACKLOG, 128)
		//��������
		.childOption(ChannelOption.SO_KEEPALIVE, true);
		
		//��ָ���Ķ˿ڽ��м���
		ChannelFuture f = b.bind(8765).sync();
		//������������
		f.channel().closeFuture().sync();
		
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		
	}

}
