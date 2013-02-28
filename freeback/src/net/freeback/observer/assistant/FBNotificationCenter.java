package net.freeback.observer.assistant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.freeback.configure.FBConfigure;
import net.freeback.entries.FBMessageProto;
import net.freeback.entries.FBMessageProto.Notify;
import net.freeback.monitor.FBMonitor;
import net.freeback.monitor.FBMonitorMessage;
import net.freeback.monitor.FBMonitorType;
import org.apache.mina.core.session.IoSession;

/**
 * Created by IntelliJ IDEA.
 * User: freeback
 * Date: 12-5-15
 * Time: 上午8:23
 * To change this template use File | Settings | File Templates.
 */
public class FBNotificationCenter {
    private HashMap<Notify, List<FBObserver>> observerHashMap = new HashMap<Notify, List<FBObserver>>();
    private HashMap<FBMonitorType, List<FBMonitor>> monitorHashMap = new HashMap<FBMonitorType, List<FBMonitor>>();
    static private FBNotificationCenter notificationCenter = null;

    private FBNotificationCenter() {
    }

    static public FBNotificationCenter sharedInstance() {
        if (notificationCenter == null) {
            synchronized (FBNotificationCenter.class) {
                if (notificationCenter == null) {
                    notificationCenter = new FBNotificationCenter();
                }
            }
        }
        return notificationCenter;
    }

    public void addMonitor(FBMonitorType monitorType, FBMonitor monitor) {
        if (monitorHashMap.containsKey(monitorType)) {
            List<FBMonitor> monitorList = monitorHashMap.get(monitorType);
            if (!monitorList.contains(monitor)) {
                monitorList.add(monitor);
            }
        } else {
            List<FBMonitor> monitorList = new ArrayList<FBMonitor>();
            monitorList.add(monitor);
            monitorHashMap.put(monitorType, monitorList);
        }

    }

    public void addObserver(Notify notify, FBObserver observer) {
        if (observerHashMap.containsKey(notify)) {
            List<FBObserver> observerList = observerHashMap.get(notify);
            if (!observerList.contains(observer)) {
                observerList.add(observer);
            }
        } else {
            List<FBObserver> observerList = new ArrayList<FBObserver>();
            observerList.add(observer);
            observerHashMap.put(notify, observerList);
        }
    }

    public void postNotification(IoSession fromSession, FBMessageProto.FBMessage message) throws Exception {
        Notify notify = message.getHeader().getNotify();
        if (observerHashMap.containsKey(notify)) {
            List<FBObserver> observerList = observerHashMap.get(notify);
            int length = observerList.size();
            for (int i = 0; i < length; i++) {
                observerList.get(i).messageReceived(fromSession, message);
            }}

    }

    public void postMonitorNotification(FBMonitorMessage monitorMessage) {
        FBMonitorType monitorType = monitorMessage.getMonitorType();
        if (monitorHashMap.containsKey(monitorType)) {
            List<FBMonitor> monitorList = monitorHashMap.get(monitorType);
            int length = monitorList.size();
            for (int i = 0; i < length; i++) {
                monitorList.get(i).messageReceived(monitorMessage);
            }
        }
    }

    public void postMonitorNotification(Exception ex) {
        FBMonitorMessage monitorMessage = new FBMonitorMessage(FBMonitorType.Error);
        monitorMessage.setMessage(ex);
        this.postMonitorNotification(monitorMessage);
    }

    public void sendException(IoSession fromSession, FBMessageProto.Header header, Exception ex) {
        FBMessageProto.Header.Builder headBuilder = FBHelper.buildHeader(FBMessageProto.Notify.ServerError, header.getAction());
        headBuilder.setResponse(false);
        headBuilder.setText(String.format(FBConfigure.Server_Error_Format, header.getNotify().getNumber()));
        fromSession.write(FBHelper.buildMessage(headBuilder.build(), null));

        FBNotificationCenter.sharedInstance().postMonitorNotification(ex);
    }
}
