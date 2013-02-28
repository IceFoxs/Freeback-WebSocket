package net.freeback.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class FBCodecFactory implements ProtocolCodecFactory {

	private final FBEncoder mEncoder;
	private final FBDecoder mDecoder;
	
	public FBCodecFactory(int maxBufferSize)
	{
		mEncoder = new FBEncoder(maxBufferSize);
		mDecoder = new FBDecoder();
	}
	
	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return this.mDecoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return this.mEncoder;
	}

}
