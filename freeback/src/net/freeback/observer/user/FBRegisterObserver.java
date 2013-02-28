package net.freeback.observer.user;

import com.google.protobuf.InvalidProtocolBufferException;
import net.freeback.configure.FBConfigure;
import net.freeback.datahelper.user.FBUserHelper;
import net.freeback.entries.FBMessageProto;
import net.freeback.entries.FBMessageProto.Notify;
import net.freeback.entries.FBUserProto;
import net.freeback.observer.assistant.FBObserver;
import net.freeback.observer.assistant.FBHelper;
import net.freeback.observer.assistant.FBNotificationCenter;
import net.freeback.utils.FBMD5;
import org.apache.mina.core.session.IoSession;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: freeback
 * Date: 12-5-15
 * Time: 下午12:01
 * To change this template use File | Settings | File Templates.
 */
public class FBRegisterObserver implements FBObserver {

	public FBRegisterObserver() {
		FBNotificationCenter.sharedInstance().addObserver(Notify.UserRegister, this);
	}

	@Override
	public void messageReceived(IoSession fromSession, FBMessageProto.FBMessage message) throws InvalidProtocolBufferException, SQLException {
		System.out.println("register received message");
		FBUserProto.FBUser.Builder userBuilder = FBUserProto.FBUser.parseFrom(message.getBodies().toByteArray()).toBuilder();
		String userName = userBuilder.getUsername();
		String password = userBuilder.getPasswd() + FBConfigure.PASSWORD_CONFUSE;
		userBuilder.setPasswd(FBMD5.getMD5Str(password, FBConfigure.PASSWORD_ENCYPT_TIMES));
		int result = FBUserHelper.userRegister(userBuilder.build());
		FBMessageProto.Header.Builder headBuilder = FBHelper.buildHeader(Notify.UserRegister, FBMessageProto.Action.None);
		headBuilder.setResponse(result != -1);
		if (result == -1) {
			headBuilder.setText("用户名已经存在");
			fromSession.write(FBHelper.buildMessage(headBuilder.build(), null));
		} else {
			FBUserProto.FBUser user = FBUserHelper.getUserByUserName(userName);
			headBuilder.setText(String.format("%s", user.getCode()));
			fromSession.write(FBHelper.buildMessage(headBuilder.build(), user.toByteString()));
		}
	}
}
