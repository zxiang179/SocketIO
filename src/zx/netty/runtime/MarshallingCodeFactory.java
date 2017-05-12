package zx.netty.runtime;

import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

public class MarshallingCodeFactory {
	
	/**
	 * ����Marshalling������MarshallingDecoder
	 * @return
	 */
	public static MarshallingDecoder buildMarshallingDecoder(){
		//����ͨ��Marshalling������ľ�ͨ������ȡMarshallingʵ������ ����Serial��־��������java���л���������
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		//������MarshallingConfiguration�������õİ汾��Ϊ5
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		//����marshallingFactory��Configuration����provider
		UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
		//����Netty��MarshallingDecoder�������������ֱ�ΪProvider�͵�����Ϣ���л������󳤶�
		MarshallingDecoder decoder = new MarshallingDecoder(provider,1024*1024*1);
		return decoder;
	}

	/**
	 * ����MarshallingEncoder
	 * @return
	 */
	public static MarshallingEncoder buildMarshallingEncoder(){
		//����ͨ��Marshalling������ľ�ͨ������ȡMarshallingʵ������ ����Serial��־��������java���л���������
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		//������MarshallingConfiguration�������õİ汾��Ϊ5
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		//����marshallingFactory��Configuration����provider
		MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
		//����Netty��MarshallingEncoder����MarshallingEncoder����ʵ�����л��ӿڵ�POJO�������л�Ϊ����������
		MarshallingEncoder encoder = new MarshallingEncoder(provider);
		return encoder;
		
	}
	
}
