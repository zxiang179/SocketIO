package zx.netty.http;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public final class HttpHelloWorldServer {
	
	static final boolean SSL = System.getProperty("ssl")!=null;
	static final int PORT = Integer.parseInt(System.getProperty("port",SSL?"8443":"8080"));
	
	public static void main(String[] args) throws SSLException, CertificateException, InterruptedException {
		//Configure SSL
		final SslContext sslCtx;
		if(SSL){
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
		}else{
			sslCtx = null;
		}
		
		//configure the server
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup,workGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new HttpHelloWorldServerInitializer(sslCtx));
			
			Channel ch = b.bind(PORT).sync().channel();
			
			System.err.println("Open your web browser and navigate to " +
			        (SSL? "https" : "http") + "://127.0.0.1:" + PORT + '/');
			ch.closeFuture().sync();
		} finally{
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
		
	}

}
