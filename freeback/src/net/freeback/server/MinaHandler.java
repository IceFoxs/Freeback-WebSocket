package net.freeback.server;

import net.freeback.configure.FBConfigure;
import net.freeback.entries.FBMessageProto;
import net.freeback.observer.assistant.FBHelper;
import net.freeback.observer.assistant.FBNotificationCenter;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class MinaHandler extends IoHandlerAdapter {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(MinaHandler.class);
	public static HashMap<Integer, IoSession> Sessions = new HashMap<Integer, IoSession>();
	private int maxConnection = 1000;

	public MinaHandler(int maxConnection) {
		this.maxConnection = maxConnection;
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		System.out.println("sessionCreated");
		if (Sessions.size() > this.maxConnection) {
			FBMessageProto.Header.Builder header = FBMessageProto.Header.newBuilder();
			header.setNotify(FBMessageProto.Notify.SystemConnect);
			header.setResponse(false);
			header.setText("服务器连接已满，请稍候再试...");
			FBMessageProto.FBMessage message = FBHelper.buildMessage(header.build(), null);
			session.write(message);
			session.close(true);
		}
	}

	@Override
	public void sessionClosed(org.apache.mina.core.session.IoSession session) throws java.lang.Exception {
		MinaServer.sharedInstance().unBindUserSession(session);
		System.out.println("Client session Closed");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		MinaServer.sharedInstance().unBindUserSession(session);
		logger.error(cause.getMessage(), cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) {
		FBMessageProto.FBMessage fbMessage = (FBMessageProto.FBMessage) message;
		FBMessageProto.Header header = fbMessage.getHeader();
		try {
			FBNotificationCenter.sharedInstance().postNotification(session, fbMessage);
		} catch (Exception ex) {
			FBMessageProto.Header.Builder headBuilder = header.toBuilder();
			headBuilder.setResponse(false);
			headBuilder.setText(String.format(FBConfigure.Server_Error_Format, headBuilder.getNotify().getNumber()));

			FBNotificationCenter.sharedInstance().postMonitorNotification(ex);
			logger.error(ex.getMessage(), ex);
//			fromSession.write(FBHelper.buildMessage(headBuilder.build(), null));
		}
	}
}
