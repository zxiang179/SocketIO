package zx.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,Server> {


	@Override
	public void completed(AsynchronousSocketChannel asc, Server attachment) {
		//������һ���ͻ��˽����ʱ��ֱ�ӵ���Server��accept��������������ִ����ȥ����֤����ͻ��˿�������
		attachment.assc.accept(attachment,this);
		read(asc);
	}

	@Override
	public void failed(Throwable exc, Server attachment) {
		exc.printStackTrace();
	}
	
	private void read(final AsynchronousSocketChannel asc){
		//��ȡ����
		ByteBuffer buf = ByteBuffer.allocate(1024);
		asc.read(buf,buf,new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer resultSize, ByteBuffer attachment) {
				//���ж�ȡ֮�����ñ�־λ
				attachment.flip();
				//��ȡ��ȡ���ֽ���
				System.out.println("Server-->�յ��ͻ��˵����ݳ���Ϊ��"+resultSize);
				//��ȡ��ȡ������
				String resultData = new String(attachment.array()).trim();
				System.out.println("Server-->�յ��ͻ��˵�������ϢΪ��"+resultData);
				String response = "��������Ӧ���յ��ͻ��˷��������ݣ�"+resultData;
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
