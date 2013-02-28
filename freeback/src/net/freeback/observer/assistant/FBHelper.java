package net.freeback.observer.assistant;


import com.google.protobuf.ByteString;
import net.freeback.configure.FBConfigure;
import net.freeback.entries.FBMessageProto;
import net.freeback.entries.FBMessageProto.Notify;
import net.freeback.entries.FBMessageProto.Action;


/**
 * Created by IntelliJ IDEA.
 * User: freeback
 * Date: 12-5-12
 * Time: 下午10:41
 * To change this template use File | Settings | File Templates.
 */
public class FBHelper {

    static public FBMessageProto.Header.Builder buildHeader(Notify notify, Action action) {
        FBMessageProto.Header.Builder headBuilder = FBMessageProto.Header.newBuilder();
        headBuilder.setNotify(notify);
        headBuilder.setAction(action);
        headBuilder.setResponse(true);
        headBuilder.setCodecVersion(FBConfigure.buildConfigure().getVersionCodec());
        return headBuilder;
    }

    static public FBMessageProto.Header.Builder buildHeader(Notify notify) {
        return FBHelper.buildHeader(notify, Action.None);
    }

    static public FBMessageProto.FBMessage buildMessage(FBMessageProto.Header header, ByteString body) {
        FBMessageProto.FBMessage.Builder messageBuilder = FBMessageProto.FBMessage.newBuilder();
        messageBuilder.setHeader(header);
        if (body != null) {
            messageBuilder.setBodies(body);
        }
        return messageBuilder.build();
    }
    static public FBMessageProto.FBMessage buildMessage(Notify notify, Action action, ByteString body) {
        FBMessageProto.Header header = FBHelper.buildHeader(notify, action).build();
        return FBHelper.buildMessage(header, body);
    }
}
