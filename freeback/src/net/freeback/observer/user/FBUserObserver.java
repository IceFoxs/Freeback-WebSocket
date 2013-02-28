package net.freeback.observer.user;

import net.freeback.entries.FBMessageProto;
import net.freeback.observer.assistant.FBObserver;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-2-29
 * Time: 下午6:44
 * To change this template use File | Settings | File Templates.
 */
public class FBUserObserver implements FBObserver {

    static Logger logger = Logger.getLogger(FBUserObserver.class);

    @Override
    public void messageReceived(IoSession fromSession, FBMessageProto.FBMessage message) {
    }
}
