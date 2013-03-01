package net.freeback.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Created by IntelliJ IDEA.
 * User: 赵天宇
 * Date: 13-3-1
 * Time: 下午12:30
 */
public class WebsocketEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput output) throws Exception {
		byte[] message = o.toString().getBytes();
		System.out.println(o.toString());
		IoBuffer ioBuffer = IoBuffer.allocate(message.length);
		ioBuffer.put(message);
		ioBuffer.flip();
		output.write(ioBuffer);
	}
}
