package net.freeback.observer.orders;

import net.freeback.entries.FBMessageProto;
import net.freeback.observer.assistant.FBObserver;
import org.apache.mina.core.session.IoSession;

/**
 * Created with IntelliJ IDEA.
 * User: zhaotianyu
 * Date: 12-10-15
 * Time: 上午10:01
 * To change this template use File | Settings | File Templates.
 */
public class FBOrdersObserver implements FBObserver {
    @Override
    public void messageReceived(IoSession fromSession, FBMessageProto.FBMessage message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
