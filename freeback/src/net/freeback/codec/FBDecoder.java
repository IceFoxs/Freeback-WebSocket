package net.freeback.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import net.freeback.entries.FBMessageProto;
import net.freeback.utils.FBUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.LoggerFactory;

import java.nio.charset.CharacterCodingException;

public class FBDecoder extends CumulativeProtocolDecoder {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(FBDecoder.class);

    private final AttributeKey CONTEXT = new AttributeKey(getClass(), "context");

    public DecoderMessage getContext(IoSession session) {
        if (session.getAttribute(CONTEXT) == null) {
            session.setAttribute(CONTEXT, new DecoderMessage());
        }
        return (DecoderMessage) session.getAttribute(CONTEXT);
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer inBuffer, ProtocolDecoderOutput out) throws CharacterCodingException {
        try {
            DecoderMessage decoderMessage = getContext(session);
            if (inBuffer.hasRemaining()) {
                if (decoderMessage.size == -1 && inBuffer.remaining() >= Integer.SIZE / 8) {
                    byte[] buffer = new byte[Integer.SIZE / 8];
                    inBuffer.get(buffer);
                    decoderMessage.size = FBUtils.byteToInt(buffer);
                }

                if (decoderMessage.size <= inBuffer.remaining()) {
                    byte[] buffer = new byte[decoderMessage.size];
                    inBuffer.get(buffer);
                    decoderMessage.message = FBMessageProto.FBMessage.parseFrom(buffer);
                    out.write(decoderMessage.message);
                    session.setAttribute(CONTEXT, null);
                }
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    class DecoderMessage {
        public int size;
        public FBMessageProto.FBMessage message;

        public DecoderMessage() {
            size = -1;
        }
    }
}
