package net.freeback.observer.system;

import net.freeback.configure.FBConfigure;
import net.freeback.connection.MongoHelper;
import net.freeback.datahelper.system.FBSystemHelper;
import net.freeback.entries.FBConfigureProto;
import net.freeback.entries.FBMessageProto;
import net.freeback.entries.FBRegionProto;
import net.freeback.entries.FBSystemProto;
import net.freeback.observer.assistant.FBHelper;
import net.freeback.observer.assistant.FBObserver;
import net.freeback.observer.assistant.FBNotificationCenter;
import net.freeback.entries.FBMessageProto.Notify;
import net.freeback.entries.FBMessageProto.Action;
import org.apache.mina.core.session.IoSession;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-2-29
 * Time: 下午6:30
 * To change this template use File | Settings | File Templates.
 */
public class FBSystemObserver implements FBObserver {

	public FBSystemObserver() {
		FBNotificationCenter notificationCenter = FBNotificationCenter.sharedInstance();
		notificationCenter.addObserver(Notify.SystemData, this);
        notificationCenter.addObserver(Notify.SystemId, this);
	}

	@Override
	public void messageReceived(IoSession fromSession, FBMessageProto.FBMessage message) {
		switch (message.getHeader().getNotify()) {
			case SystemData:
				this.sysDataReceived(fromSession, message);
				break;
            case SystemId:
                this.sysIdReceived(fromSession, message);
                break;
		}
	}

	private void sysIdReceived(IoSession fromSession, FBMessageProto.FBMessage message) {
		FBMessageProto.Header.Builder headBuilder = message.getHeader().toBuilder();
        int count = Integer.parseInt(headBuilder.getText());
		headBuilder.setText(FBSystemHelper.buildObjectId(count));
        fromSession.write(FBHelper.buildMessage(headBuilder.build(), null));
	}

    private void sysDataReceived(IoSession fromSession, FBMessageProto.FBMessage message) {
        FBMessageProto.Header.Builder headBuilder;
        FBMessageProto.FBMessage serverMessage;
        FBConfigureProto.FBConfigure configure = FBConfigure.buildConfigure();
        int clientVersion = Integer.parseInt(message.getHeader().getText());
        if (clientVersion < configure.getVersionSystem()) {
            FBSystemProto.FBSystem sysData = FBSystemHelper.buildSystem(configure.getVersionSystem());
            headBuilder = FBHelper.buildHeader(Notify.SystemData, Action.None);
            serverMessage = FBHelper.buildMessage(headBuilder.build(), sysData.toByteString());
        } else {
            headBuilder = FBHelper.buildHeader(Notify.SystemData, Action.None);
            headBuilder.setText("version same");
            serverMessage = FBHelper.buildMessage(headBuilder.build(), null);
        }
        fromSession.write(serverMessage);
    }
	void printProvinces(FBRegionProto.FBRegion region) {
		MongoHelper mongoHelper = MongoHelper.sharedInstance();
		int count = region.getProvincesCount();
		for (int i = 0; i < count; i++) {
			FBRegionProto.FBProvince province = region.getProvinces(i);

			System.out.println(String.format("%s", province.getName()));

			int cityCount = province.getCitiesCount();
			for (int j = 0; j < cityCount; j++) {
				FBRegionProto.FBCity city = province.getCities(j);
				System.out.println(String.format("  %s", city.getName()));

				int areaCount = city.getAreasCount();
				for (int k = 0; k < areaCount; k++) {
					FBRegionProto.FBArea area = city.getAreas(k);

					System.out.println(String.format("      %s", city.getAreas(k).getName()));
				}
			}
		}
	}
}
