package net.freeback.monitor;

/**
 * Created with IntelliJ IDEA.
 * User: freeback
 * Date: 12-7-29
 * Time: 下午13:16
 * To change this template use File | Settings | File Templates.
 */
public interface FBMonitor {

    void messageReceived(FBMonitorMessage monitorMessage);
}
