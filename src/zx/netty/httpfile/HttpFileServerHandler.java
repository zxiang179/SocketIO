package zx.netty.httpfile;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private final String url;
	
	public HttpFileServerHandler(String url) {
		this.url=url;
	}
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx,
			FullHttpRequest request) throws Exception {
		//������Ľ����������ж�
		if(!request.decoderResult().isSuccess()){
			//400
			sendError(ctx,BAD_REQUEST);
			return;
		}
		//������ʽ�����жϣ��������get�����򷵻��쳣
		if(request.method()!=GET){
			//405
			sendError(ctx,METHOD_NOT_ALLOWED);
			return;
		}
		//��ȡ����uri·��
		final String uri = request.uri();
		//��uri���з����������ļ��ڱ���ϵͳ��·��
		final String path = sanitizeUri(uri);
		//���·�����첻�Ϸ�����pathΪnull
		if(path==null){
			//403
			sendError(ctx,FORBIDDEN);
			return;
		}
		//����file����
		File file = new File(path);
		//�ж��ļ��Ƿ�Ϊ���ػ��߲�����
		if(file.isHidden()||!file.exists()){
			//404
			sendError(ctx, NOT_FOUND);
			return;
		}
		//���Ϊ�ļ���
		if(file.isDirectory()){
			if(uri.endsWith("/")){
				//���������"/"������˵���Ƿ��ʵ�һ���ļ�Ŀ¼�������չʾ�ļ��б�web����������תһ��Controller�������ļ�����ת��һ��ҳ�棩
				sendListing(ctx,file);
			}else{
				//�����"/"���������ض��򣬲�ȫ"/"�ٴ�����
				sendRedirect(ctx,uri+'/');
			}
			return;
		}
		//���������file�������ļ�����
		if(!file.isFile()){
			//403
			sendError(ctx, FORBIDDEN);
			return;
		}
		//����ļ���д��
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "r");//��ֻ���ķ�ʽ���ļ�
		} catch (Exception e) {
			//404
			sendError(ctx,NOT_FOUND);
			return;
		}
		
		//��ȡ�ļ�����
		long fileLength = randomAccessFile.length();
	    //������Ӧ����
		HttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
		//������Ӧ��Ϣ
		HttpHeaderUtil.setContentLength(response, fileLength);
		//������Ӧͷ
		setContentTypeHeader(response,file);
		//���һֱ����������������Ӧͷ��ϢΪ��HttpHeaders.Values.KEEP_ALIVE
		if(HttpHeaderUtil.isKeepAlive(request)){
			response.headers().set(CONNECTION,HttpHeaderValues.KEEP_ALIVE);
		}
		//����д��
		ctx.write(response);
		
		//���췢���ļ��߳� ���ļ�д��chunked������
		ChannelFuture sendFileFuture;
		//д��ChunkedFile
		sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile,0,fileLength,8192),ctx.newProgressivePromise());
		//��Ӵ������
		sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
			@Override
			public void operationProgressed(ChannelProgressiveFuture future, long progress,
					long total) throws Exception {
				if(total<0){//total unknown
					System.err.println("Transfer progress: "+progress);
				}else{
					System.err.println("Transfer progress: "+progress+"/"+total);
				}
			}
			
			@Override
			public void operationComplete(ChannelProgressiveFuture arg0)
					throws Exception {
				System.out.println("Transfer complete");
			}
			
		});
		
		//ʹ��chunked���룬�������Ҫ����һ����������Ŀ�����Ϣ����б�ǣ���ʾ������Ϣ���Ѿ��ɹ��������
		ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		//�����ǰ���������keep-alive�����һ����Ϣ�������֮������������ر�����
		if(!HttpHeaderUtil.isKeepAlive(request)){
			lastContentFuture.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if(ctx.channel().isActive()){
			sendError(ctx,INTERNAL_SERVER_ERROR);
			ctx.close();
		}
	}
	
	private static void setContentTypeHeader(HttpResponse response,File file){
		//ʹ��mime�����ȡ�ļ�����
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		response.headers().set(CONTENT_TYPE,mimeTypesMap.getContentType(file.getPath()));
	}
	
	//�ض������
	private static void sendRedirect(ChannelHandlerContext ctx,String newUri){
		//������Ӧ����
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,FOUND);
		//�����µ������ַ������Ӧ������ȥ
		response.headers().set(LOCATION,newUri);
		//ʹ��ctx����д���������ĵ�SocketChannel�� �������ر�����(����ָ�رմ��������ݵ��߳�����)
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	//�ļ��Ƿ��������������֤
	private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
	
	private static void sendListing(ChannelHandlerContext ctx,File dir){
		//������Ӧ����
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
		//��Ӧͷ
		response.headers().set(CONTENT_TYPE,"text/html;charset=UTF-8");
		//׷���ı�����
		StringBuilder ret = new StringBuilder();
		String dirPath = dir.getPath();
		ret.append("<!DOCTYPE html>\r\n");
		ret.append("<html><head><title>");
		ret.append(dirPath);
		ret.append(" Ŀ¼��");
		ret.append("</title></head><body>\r\n");
		ret.append("<h3>");
		ret.append(dirPath).append(" Ŀ¼��");
		ret.append("</h3>\r\n");
		ret.append("<ul>");
		ret.append("<li>���ӣ�<a href=\"../\">..</a></li>\r\n");
		
		//�����ļ� ��ӳ�����
		for(File f:dir.listFiles()){
			//step 1:�������ػ򲻿ɶ��ļ�
			if(f.isHidden()||!f.canRead()){
				continue;
			}
			String name= f.getName();
			//step 2:��������������������ļ�
			if(!ALLOWED_FILE_NAME.matcher(name).matches()){
				continue;
			}
			//ƴ�ӳ����Ӽ���
			ret.append("<li>���ӣ�<a href=\"");
		    ret.append(name);
		    ret.append("\">");
		    ret.append(name);
		    ret.append("</a></li>\r\n");
		}
		ret.append("</ul></body></html>\r\n");
		//����ṹ��д�뻺����
		ByteBuf buffer = Unpooled.copiedBuffer(ret,CharsetUtil.UTF_8);
		//����д������
		response.content().writeBytes(buffer);
		//����д������
		buffer.release();
		//ʹ��ctx����д������ˢ�µ�SocketChannel��ȥ �������ر�����(�������رմ��������ݵ��߳�����)
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		
	}
	
	//�Ƿ�URI����
	private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
	
	private String sanitizeUri(String uri){
		try {
			//ʹ��UTF-8�ַ���
			uri=URLDecoder.decode(uri,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			try {
				uri = URLDecoder.decode(uri,"ISO-8859-1");
			} catch (UnsupportedEncodingException e1) {
				//�׳�Ԥ�����쳣��Ϣ
				throw new Error();
			}
		}
		//��uri����ϸ�����ж� �� 4����֤����
		//step1 ������֤
		if(!uri.startsWith(url)){
			return null;
		}
		//step2 ������֤
		if(!uri.startsWith("/")){
			return null;
		}
		//step3 ���ļ��ָ����滻���ز���ϵͳ���ļ�·���ָ���
		uri = uri.replace('/', File.separatorChar);
		//step4 ������֤�Ϸ���
		if (uri.contains(File.separator + '.')
				|| uri.contains('.' + File.separator) || uri.startsWith(".")
				|| uri.endsWith(".") || INSECURE_URI.matcher(uri).matches()) {
			    return null;
		}
		//��ǰ��������Ŀ¼+uri�������·������
		return System.getProperty("user.dir")+File.separator+uri;
//		return System.getProperty("user.dir")+uri;
		
	}
	
	
	private static void sendError(ChannelHandlerContext ctx,HttpResponseStatus status){
		//������Ӧ����
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status,Unpooled.copiedBuffer("Failure:"+status.toString()+"\r\n",CharsetUtil.UTF_8));
		//������Ӧͷ��Ϣ
		response.headers().set(CONTENT_TYPE,"text/plain;charset=UTF-8");
		//ʹ��ctx����д����ˢ�µ�SocketChannel��ȥ �������ر����ӣ�������ָ�رմ��������ݵ��߳����ӣ�
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	

}
