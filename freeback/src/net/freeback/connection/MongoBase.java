package net.freeback.connection;

import com.mongodb.DB;
import com.mongodb.Mongo;
import net.freeback.configure.FBConfigure;
import net.freeback.entries.FBConfigureProto;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: 赵天宇
 * Date: 12-11-6
 * Time: 下午3:22
 */
class MongoBase {

	static protected Logger logger   = Logger.getLogger(MongoBase.class);

	protected DB _DBFreeback = null;

	protected MongoBase(){
		try {
			FBConfigureProto.FBConfigure configure = FBConfigure.buildConfigure();
			Mongo mongo = new Mongo(configure.getMongoHost(), configure.getMongoPort());
			_DBFreeback = mongo.getDB(configure.getFreebackDB());
			boolean auth = _DBFreeback.authenticate(configure.getAuthUser(), configure.getAuthPassword().toCharArray());
			if (!auth) {
				System.out.println("error : 验证未通过");
				logger.error(String.format("error auth: %s", configure.getAuthUser()));
			}else
			{
				System.out.println(String.format("Mongod Freeback 数据库连接成功 : %s : %d", configure.getMongoHost(), configure.getMongoPort()));
			}
		} catch (UnknownHostException e) {
			System.out.println("Freeback 数据库连接失败：" + e.getMessage());
			logger.error(e.getMessage(), e);
		}
	}

	public org.bson.types.ObjectId buildObjectId() {
        return new org.bson.types.ObjectId();
    }

    public org.bson.types.ObjectId buildObjectIdByString(String Id) {
      return new org.bson.types.ObjectId(Id);
    }
}
