package net.freeback.exceptions;
 import net.freeback.entries.FBMessageProto.Notify;
/**
 * Created with IntelliJ IDEA.
 * User: freeback
 * Date: 12-7-29
 * Time: 上午08:50
 * To change this template use File | Settings | File Templates.
 */
public class FBException {

    private Notify  _Notify = Notify.Unknown;
    private Exception _Exception = null;

    public FBException(Exception ex)
    {
        this._Exception = ex;
    }

    public FBException(Exception ex, Notify notify)
    {
        _Exception = ex;
        _Notify = notify;
    }

    public Notify getNotify()
    {
        return this._Notify;
    }

    public Exception getException()
    {
        return this._Exception;
    }
}

