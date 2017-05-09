package zx.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,Server> {


	@Override
	public void completed(AsynchronousSocketChannel asc, Server attachment) {
		//当有下一个客户端接入的时候直接调用Server的accept方法，这样反复执行下去，保证多个客户端可以阻塞
		attachment.assc.accept(attachment,this);
		read(asc);
	}

	@Override
	public void failed(Throwable exc, Server attachment) {
		exc.printStackTrace();
	}
	
	private void read(final AsynchronousSocketChannel asc){
		//读取数据
		ByteBuffer buf = ByteBuffer.allocate(1024);
		asc.read(buf,buf,new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer resultSize, ByteBuffer attachment) {
				//进行读取之后，重置标志位
				attachment.flip();
				//获取读取的字节数
				System.out.println("Server-->收到客户端的数据长度为："+resultSize);
				//获取读取的数据
				String resultData = new String(attachment.array()).trim();
				System.out.println("Server-->收到客户端的数据信息为："+resultData);
				String response = "服务器响应，收到客户端发来的数据："+resultData;
				write(asc,response);
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				exc.printStackTrace();				
			}
		});
	}
	
	public void write(AsynchronousSocketChannel asc,String response){
		try {
			ByteBuffer buf = ByteBuffer.allocate(1024);
			buf.put(response.getBytes());
			buf.flip();
			asc.write(buf).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
