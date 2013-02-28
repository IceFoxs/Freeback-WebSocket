package net.freeback.datahelper.store;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import net.freeback.configure.FBConfigure;
import net.freeback.connection.MongoHelper;
import net.freeback.entries.FBConfigureProto;
import net.freeback.entries.store.FBStoreProto;
import net.freeback.entries.store.FBStoreProto.FBStore;
import net.freeback.utils.FBUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-3-13
 * Time: 下午9:11
 * To change this template use File | Settings | File Templates.
 */
public class FBStoreHelper {
	static final String TBL_STORE = "sys.store";

	static final String STORE_ID = "_id";
	static final String STORE_BOSS = "_1";
	static final String STORE_NAME = "_2";
	static final String STORE_INDUSTRY = "_3";
	static final String STORE_ADDRESS = "_4";
	static final String STORE_PHONE = "_5";
	static final String STORE_PHOTO = "_6";
	static final String STORE_PHOTO_NAME = "_1";
	static final String STORE_STATE = "_7";
	static final String STORE_DESCRIPTION = "_8";
    static final String STORE_MODIFIED = "_9";

	static FBStoreProto.FBStores buildStores(DBCursor cursor) {
		FBStoreProto.FBStores.Builder stores = FBStoreProto.FBStores.newBuilder();
		while (cursor.hasNext()) {
            BasicDBObject dbObject = (BasicDBObject) cursor.next();
			FBStoreProto.FBStore.Builder builder = FBStoreProto.FBStore.newBuilder();
			builder.setCode(dbObject.getString(STORE_ID));
			builder.setBoss(dbObject.getString(STORE_BOSS));
			builder.setName(dbObject.getString(STORE_NAME));
			builder.setIndustry(dbObject.getInt(STORE_INDUSTRY));
			builder.setAddress(dbObject.getString(STORE_ADDRESS));
			builder.setPhone(dbObject.getString(STORE_PHONE));
			builder.setDescription(dbObject.getString(STORE_DESCRIPTION));
            builder.setModified(dbObject.getString(STORE_MODIFIED));
            builder.setState(FBStoreProto.StoreState.valueOf(dbObject.getInt(STORE_STATE)));
//			DBObject photoObject = (DBObject) dbObject.get(STORE_PHOTO);
//			if (photoObject != null) {
//				List<DBObject> photoList = (List<DBObject>) photoObject;
//				for (DBObject photoItem : photoList) {
//					builder.addPhotos(photoItem.get(STORE_PHOTO_NAME).toString());
//				}
//			}
			stores.addStores(builder);
		}
		return stores.build();
	}

	static public FBStoreProto.FBStores findByBoss(String boss) {
		BasicDBObject query = new BasicDBObject(STORE_BOSS, boss);
		DBCursor cursor = MongoHelper.sharedInstance().query(TBL_STORE, query, null);
		FBStoreProto.FBStores stores = buildStores(cursor);
		cursor.close();
		return stores;
	}

	static public FBStore findById(String storeId) {
        BasicDBObject query = new BasicDBObject(STORE_ID, storeId);
		DBCursor cursor = MongoHelper.sharedInstance().query(TBL_STORE, query, null);
		FBStoreProto.FBStores stores = buildStores(cursor);
		cursor.close();
		return stores != null && stores.getStoresCount() > 0 ? stores.getStores(0) : null;
	}

	static public String save(FBStore store) {
		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put(STORE_BOSS, store.getBoss());
		dbObject.put(STORE_NAME, store.getName());
		dbObject.put(STORE_INDUSTRY, store.getIndustry());
		dbObject.put(STORE_ADDRESS, store.getAddress());
		dbObject.put(STORE_PHONE, store.getPhone());
        dbObject.put(STORE_STATE, store.getState().getNumber());
        dbObject.put(STORE_DESCRIPTION, store.getDescription());
        dbObject.put(STORE_MODIFIED, FBUtils.dateToString(new Date()));
//		int photoCount = store.getPhotosCount();
//		if (photoCount > 0) {
//			List<BasicDBObject> photoList = new ArrayList<BasicDBObject>();
//			for (int i = 0; i < photoCount; i++) {
//				photoList.add(new BasicDBObject(STORE_PHOTO_NAME, store.getPhotos(i)));
//			}
//			dbObject.put(STORE_PHOTO, photoList);
//		}

        String code = MongoHelper.sharedInstance().buildObjectId().toString();
		BasicDBObject qDBObject = new BasicDBObject(STORE_ID, store.getCode());
		FBConfigureProto.FBConfigure configure = FBConfigure.buildConfigure();
		if (store.getCode().length() == configure.getObjectIdLength()) {
            code = store.getCode();
			dbObject.put(STORE_ID, code);
			MongoHelper.sharedInstance().update(TBL_STORE, qDBObject, dbObject);
		} else {
			dbObject.put(STORE_ID, code);
			MongoHelper.sharedInstance().insert(TBL_STORE, dbObject);
		}
        return code;
	}
}
