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
	 * 创建Marshalling解码器MarshallingDecoder
	 * @return
	 */
	public static MarshallingDecoder buildMarshallingDecoder(){
		//首先通过Marshalling工具类的精通方法获取Marshalling实例对象 多数Serial标志创建的是java序列化工厂对象
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		//创建了MarshallingConfiguration对象，配置的版本号为5
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		//根据marshallingFactory和Configuration创建provider
		UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
		//构建Netty的MarshallingDecoder对象，两个参数分别为Provider和单个消息序列化后的最大长度
		MarshallingDecoder decoder = new MarshallingDecoder(provider,1024*1024*1);
		return decoder;
	}

	/**
	 * 创建MarshallingEncoder
	 * @return
	 */
	public static MarshallingEncoder buildMarshallingEncoder(){
		//首先通过Marshalling工具类的精通方法获取Marshalling实例对象 多数Serial标志创建的是java序列化工厂对象
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		//创建了MarshallingConfiguration对象，配置的版本号为5
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		//根据marshallingFactory和Configuration创建provider
		MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
		//构建Netty的MarshallingEncoder对象，MarshallingEncoder用于实现序列化接口的POJO对象序列化为二进制数组
		MarshallingEncoder encoder = new MarshallingEncoder(provider);
		return encoder;
		
	}
	
}
