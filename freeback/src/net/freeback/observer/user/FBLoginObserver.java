package net.freeback.observer.user;

import com.google.protobuf.InvalidProtocolBufferException;
import net.freeback.configure.FBConfigure;
import net.freeback.datahelper.user.FBUserHelper;
import net.freeback.entries.FBMessageProto;
import net.freeback.entries.FBMessageProto.Notify;
import net.freeback.entries.FBMessageProto.Action;
import net.freeback.entries.FBUserProto;
import net.freeback.observer.assistant.FBHelper;
import net.freeback.observer.assistant.FBNotificationCenter;
import net.freeback.observer.assistant.FBObserver;
import net.freeback.server.MinaServer;
import net.freeback.utils.FBMD5;
import org.apache.mina.core.session.IoSession;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: freeback
 * Date: 12-5-15
 * Time: 上午11:55
 * To change this template use File | Settings | File Templates.
 */
public class FBLoginObserver implements FBObserver {
	private final String USER_PASS_ERROR = "用户名/密码错误";
	private final String USER_VERIFY_ERROR = "密码已经变更，请重新登录";

	public FBLoginObserver() {
		FBNotificationCenter notificationCenter = FBNotificationCenter.sharedInstance();
		notificationCenter.addObserver(Notify.UserLogin, this);
		notificationCenter.addObserver(Notify.UserVerify, this);
		notificationCenter.addObserver(Notify.UserLogout, this);
	}

	@Override
	public void messageReceived(IoSession fromSession, FBMessageProto.FBMessage message) throws InvalidProtocolBufferException, SQLException {
		FBMessageProto.Header header = message.getHeader();
		switch (header.getNotify()) {
			case UserLogin:
				this.loginReceived(fromSession, message);
				break;
			case UserLogout:
				this.logoutReceived(fromSession, message);
				break;
			case UserVerify:
				this.loginVerifyReceived(fromSession, message);
				break;
		}
	}

	void loginReceived(IoSession fromSession, FBMessageProto.FBMessage message) throws SQLException, InvalidProtocolBufferException {
		FBUserProto.FBUser user = FBUserProto.FBUser.parseFrom(message.getBodies().toByteArray());
		String userName = user.getUsername();
		String passwd = user.getPasswd() + FBConfigure.PASSWORD_CONFUSE;
		passwd = FBMD5.getMD5Str(passwd, FBConfigure.PASSWORD_ENCYPT_TIMES);
		boolean logon = FBUserHelper.userLogin(userName, passwd);
		FBMessageProto.Header.Builder headBuilder = FBHelper.buildHeader(Notify.UserLogin, Action.None);
		headBuilder.setResponse(logon);
		if (logon) {
			FBUserProto.FBUser logonUser = FBUserHelper.getUserByUserName(userName);
			headBuilder.setText(String.format("%s", logonUser.getCode()));
			String key = logonUser.getCode();
			MinaServer.sharedInstance().bindUserSession(key, fromSession);
			fromSession.write(FBHelper.buildMessage(headBuilder.build(), logonUser.toByteString()));
		} else {
			headBuilder.setText(USER_PASS_ERROR);
			fromSession.write(FBHelper.buildMessage(headBuilder.build(), null));
		}
	}

	void loginVerifyReceived(IoSession fromSession, FBMessageProto.FBMessage message) throws SQLException {
		FBMessageProto.Header header = message.getHeader();
		String userName = header.getSender();
		String passwd = header.getText();
		boolean logon = FBUserHelper.userLogin(userName, passwd);
		FBMessageProto.Header.Builder headBuilder = header.toBuilder();
		headBuilder.setResponse(logon);
		if (logon) {
			FBUserProto.FBUser logonUser = FBUserHelper.getUserByUserName(userName);
			headBuilder.setText(String.format("%s", logonUser.getCode()));
			MinaServer.sharedInstance().bindUserSession(logonUser.getCode(), fromSession);
			fromSession.write(FBHelper.buildMessage(headBuilder.build(), null));
		} else {
			headBuilder.setText(USER_VERIFY_ERROR);
			fromSession.write(FBHelper.buildMessage(headBuilder.build(), null));
		}
	}

	void logoutReceived(IoSession fromSession, FBMessageProto.FBMessage message) {
		FBMessageProto.Header.Builder headBuilder = message.getHeader().toBuilder();
		MinaServer.sharedInstance().unBindUserSession(fromSession);
		headBuilder.setResponse(true);
		fromSession.write(FBHelper.buildMessage(headBuilder.build(), null));
	}
}
