package net.freeback.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by IntelliJ IDEA.
 * User: 赵天宇
 * Date: 13-3-1
 * Time: 下午12:31
 */
public class WebsocketFactory implements ProtocolCodecFactory {
	@Override
	public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
		return new WebsocketEncoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
		return new WebsocketDecoder();
	}
}
