package net.freeback.codec;

import net.freeback.entries.FBMessageProto;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

public class FBEncoder extends ProtocolEncoderAdapter {
    private int mMaxBufferSize = 1024 * 100;

    public FBEncoder(int maxBufferSize) {
        this.mMaxBufferSize = maxBufferSize;
    }

    @Override
    public void encode(IoSession arg0, Object arg1, ProtocolEncoderOutput out) throws Exception {
        FBMessageProto.FBMessage message = (FBMessageProto.FBMessage) arg1;
        byte[] buffer = message.toByteArray();
        int length = buffer.length;
        IoBuffer sendBuffer = IoBuffer.allocate(Integer.SIZE / 8 + length);
        sendBuffer.putInt(length);
        sendBuffer.put(buffer);
        sendBuffer.flip();
        out.write(sendBuffer);
    }
}
