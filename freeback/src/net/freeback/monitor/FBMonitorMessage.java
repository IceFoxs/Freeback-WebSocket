package net.freeback.monitor;

/**
 * Created with IntelliJ IDEA.
 * User: freeback
 * Date: 12-7-29
 * Time: 下午13:56
 * To change this template use File | Settings | File Templates.
 */
public class FBMonitorMessage {

    private Object message;
    private FBMonitorType monitorType;

    public FBMonitorMessage(FBMonitorType aMonitorType)
    {
        this.monitorType = aMonitorType;
    }

    public FBMonitorType getMonitorType()
    {
        return this.monitorType;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
