package net.freeback.observer.common;

import net.freeback.datahelper.system.FBFileHelper;
import net.freeback.entries.FBMessageProto;
import net.freeback.observer.assistant.FBNotificationCenter;
import net.freeback.observer.assistant.FBObserver;
import net.freeback.observer.assistant.FBHelper;
import org.apache.mina.core.session.IoSession;

/**
 * Created by IntelliJ IDEA.
 * User: 赵天宇
 * Date: 12-11-16
 * Time: 下午1:45
 */
public class FBPhotoObserver implements FBObserver {

	public FBPhotoObserver() {
		FBNotificationCenter notificationCenter = FBNotificationCenter.sharedInstance();
		notificationCenter.addObserver(FBMessageProto.Notify.PhotoUser, this);
		notificationCenter.addObserver(FBMessageProto.Notify.PhotoStore, this);
		notificationCenter.addObserver(FBMessageProto.Notify.PhotoProduct, this);
	}

	@Override
	public void messageReceived(IoSession fromSession, FBMessageProto.FBMessage message) throws Exception {
		if (message.hasBodies()) {
			FBMessageProto.Header.Builder headBuilder = message.getHeader().toBuilder();
			headBuilder.setResponse(true);
			String bucket = "user";
			switch (headBuilder.getNotify())
			{
				case PhotoProduct:
					bucket = "product";
					break;
				case PhotoStore:
					bucket = "store";
					break;
			}
			switch (headBuilder.getAction()) {
				case Save:
					FBFileHelper.save(bucket, headBuilder.getText(), message.getBodies().toByteArray());
					break;
				case Remove:
					FBFileHelper.remove(bucket, headBuilder.getText());
					break;
			}
			fromSession.write(FBHelper.buildMessage(headBuilder.build(), null));
		}
	}
}
