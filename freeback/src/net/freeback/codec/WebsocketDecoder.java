package net.freeback.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: 赵天宇
 * Date: 13-3-1
 * Time: 下午12:29
 */
public class WebsocketDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput output) throws Exception {
//		String message = ioBuffer.getString(Charset.forName("utf-8").newDecoder());
//		System.out.println(message);
//		output.write(message);
		output.write(ioBuffer);
		if (ioBuffer.hasRemaining())
		{
			return false;
		}
		return true;
	}
}
