package net.freeback.observer.assistant;

import net.freeback.entries.FBMessageProto;
import org.apache.mina.core.session.IoSession;

/**
 * Created by IntelliJ IDEA.
 * User: freeback
 * Date: 12-5-15
 * Time: 上午8:25
 * To change this template use File | Settings | File Templates.
 */
public interface FBObserver {

    public void messageReceived(IoSession fromSession, FBMessageProto.FBMessage message) throws Exception;
}
