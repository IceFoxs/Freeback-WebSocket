package net.freeback.connection;

import com.mongodb.*;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: freeback
 * Date: 12-8-18
 * Time: 上午11:23
 * To change this template use File | Settings | File Templates.
 */
public class MongoHelper extends MongoBase {

    static protected Logger logger   = Logger.getLogger(MongoHelper.class);

    protected MongoHelper() {
		super();
    }

    static private MongoHelper _Helper = null;

    static public MongoHelper sharedInstance() {
        if (_Helper == null) {
            synchronized (MongoHelper.class) {
                if (_Helper == null) {
                    _Helper = new MongoHelper();
                }
            }
        }
        return _Helper;
    }

    public WriteResult insert(String collectionName, DBObject value) {
        if (_DBFreeback != null) {
            List<DBObject> dbObjectList = new ArrayList<DBObject>();
            dbObjectList.add(value);
            return this.insert(collectionName, dbObjectList);
        }
        return null;
    }

    public WriteResult insert(String collectionName, List<DBObject> valueList) {
        if (_DBFreeback != null) {

            DBCollection collection = _DBFreeback.getCollection(collectionName);
            return collection.insert(valueList);
        }
        return null;
    }

    public WriteResult update(String collectionName, DBObject query, DBObject value) {
        if (_DBFreeback != null) {
            DBCollection collection = _DBFreeback.getCollection(collectionName);
            return collection.update(query, value);
        }
        return null;
    }

    public WriteResult delete(String collectionName, DBObject query)
    {
        if (_DBFreeback != null)
        {
            DBCollection collection = _DBFreeback.getCollection(collectionName);
            return collection.remove(query);
        }
        return null;
    }

	public DBCursor query(String collectionName, DBObject query) {
		return this.query(collectionName, query, null);
	}

    public DBCursor query(String collectionName, DBObject query, DBObject sort) {
        if (_DBFreeback != null) {
            DBCollection collection = this.getCollection(collectionName);
            return sort == null ? collection.find(query) : collection.find(query).sort(sort);
        }
        return null;
    }

    public DBCollection getCollection(String collectionName) {
        return this._DBFreeback.getCollection(collectionName);
    }
}
