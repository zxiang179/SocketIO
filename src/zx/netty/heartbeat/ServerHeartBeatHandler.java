package zx.netty.heartbeat;

import java.util.HashMap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHeartBeatHandler extends ChannelHandlerAdapter{
	
	/**
	 * key:ip value:auth
	 */
	private static HashMap<String,String> AUTH_IP_MAP = 
			new HashMap<String,String>();
	private static final String SUCCESS_KEY = "auth_success_key";
	
	static {
		AUTH_IP_MAP.put("169.254.72.95", "1234");
	}
	/**
	 * ��֤�ͻ����Ƿ���Ȩ��
	 * @param ctx
	 * @param msg
	 * @return
	 */
	public boolean auth(ChannelHandlerContext ctx,Object msg){
		System.out.println(msg);
		String[] ret=((String)msg).split(",");
		String auth = AUTH_IP_MAP.get(ret[0]);
		if(auth!=null&&auth.equals(ret[1])){
			ctx.writeAndFlush(SUCCESS_KEY);
			return true;
		}else{
			ctx.writeAndFlush("auth failure !").addListener(ChannelFutureListener.CLOSE);
			return false;
		}
	}
	
	public void channelRead(ChannelHandlerContext ctx,Object msg)throws Exception{
		if(msg instanceof String){
			auth(ctx,msg);
		}else if(msg instanceof RequestInfo){
			RequestInfo info = (RequestInfo)msg;
			System.out.println("--------------------------------------------");
			System.out.println("��ǰ����ipΪ: " + info.getIp());
			System.out.println("��ǰ����cpu���: ");
			HashMap<String,Object> cpu = info.getCpuPercMap();
			System.out.println("��ʹ����: " + cpu.get("combined"));
			System.out.println("�û�ʹ����: " + cpu.get("user"));
			System.out.println("ϵͳʹ����: " + cpu.get("sys"));
			System.out.println("�ȴ���: " + cpu.get("wait"));
			System.out.println("������: " + cpu.get("idle"));
			
			System.out.println("��ǰ����memory���: ");
			HashMap<String,Object> memory = info.getMemoryMap();
			System.out.println("�ڴ�����: " + memory.get("total"));
			System.out.println("��ǰ�ڴ�ʹ����: " + memory.get("used"));
			System.out.println("��ǰ�ڴ�ʣ����: " + memory.get("free"));
			System.out.println("--------------------------------------------");
			
			ctx.writeAndFlush("info received!");
		}else {
			ctx.writeAndFlush("connect failure!").addListener(ChannelFutureListener.CLOSE);
		}
	}

}
