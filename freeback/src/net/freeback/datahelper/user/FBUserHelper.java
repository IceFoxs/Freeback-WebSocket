package net.freeback.datahelper.user;

import com.mongodb.*;
import net.freeback.connection.MongoHelper;
import net.freeback.datahelper.system.FBRegionHelper;
import net.freeback.entries.FBRegionProto;
import net.freeback.entries.FBUserProto;

import java.sql.SQLException;

public class FBUserHelper {

    static final String TBL_USER = "sys.user";

    static final String USER_ID = "_id";
    static final String USER_USERNAME = "_1";
    static final String USER_PASSWD = "_2";
    static final String USER_NICK = "_3";
    static final String USER_EMAIL = "_4";
    static final String USER_SEXUAL = "_5";
    static final String USER_AREA = "_6";
    static final String USER_PHONE = "_7";
    static final String USER_REMARK = "_9";
    static final String USER_ADDRESS = "_10";

    /**
     * @param entry
     * @return -1 : account exists 1 : success
     */
    static public int userRegister(FBUserProto.FBUser entry) {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put(USER_USERNAME, entry.getUsername());
        dbObject.put(USER_PASSWD, entry.getPasswd());
        dbObject.put(USER_NICK, entry.getNick());
        dbObject.put(USER_EMAIL, entry.getEmail());
        dbObject.put(USER_REMARK, entry.getRemark());
        dbObject.put(USER_SEXUAL, entry.getSexual());
        dbObject.put(USER_PHONE, entry.getPhonesList());
        dbObject.put(USER_ADDRESS, entry.getAddressesList());
        FBRegionProto.FBProvince province = entry.getProvince();
        FBRegionProto.FBCity city = province.getCities(0);
        FBRegionProto.FBArea area = city.getAreas(0);
        dbObject.put(USER_AREA, area.getCode());

        WriteResult writeResult = MongoHelper.sharedInstance().insert(TBL_USER, dbObject);
        return writeResult.getError() != null ? -1 : 1;
    }

    /**
     * @param userName
     * @param passwd
     * @return
     */
    static public boolean userLogin(String userName, String passwd) throws SQLException {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put(USER_USERNAME, userName);
        dbObject.put(USER_PASSWD, passwd);
        DBCollection dbCollection = MongoHelper.sharedInstance().getCollection(TBL_USER);
        return dbCollection.getCount(dbObject) == 1;
//        DBCursor cursor = MongoHelper.sharedInstance().query(TBL_USER, dbObject);
//        int count = cursor.count();
//        cursor.close();
//        return count > 0;
    }
    //****************************************************************************************

    static public FBUserProto.FBUser buildUser(DBCursor dbCursor) {
        FBUserProto.FBUser.Builder builder = FBUserProto.FBUser.newBuilder();

        BasicDBObject dbObject = null;
        while (dbCursor.hasNext()) {
            dbObject = (BasicDBObject) dbCursor.next();
            builder.setCode(dbObject.getString(USER_ID));
            builder.setUsername(dbObject.getString(USER_USERNAME));
            builder.setPasswd(dbObject.getString(USER_PASSWD));
            builder.setNick(dbObject.getString(USER_NICK));
            BasicDBList dbList = (BasicDBList) dbObject.get(USER_PHONE);
            if (dbList != null && dbList.size() > 0) {
                for (int i = 0; i < dbList.size(); i++) {
                    builder.addPhones(dbList.get(i).toString());
                }
            }
            dbList = (BasicDBList) dbObject.get(USER_ADDRESS);
            if (dbList != null && dbList.size() > 0) {
                for (int i = 0; i < dbList.size(); i++) {
                    builder.addAddresses(dbList.get(i).toString());
                }
            }
            builder.setProvince(FBRegionHelper.rebuildRegion(dbObject.getInt(USER_AREA)));
            builder.setSexual(dbObject.getInt(USER_SEXUAL));
            builder.setEmail(dbObject. getString(USER_EMAIL));
            builder.setRemark(dbObject.getString(USER_REMARK));
        }
        dbCursor.close();
        return builder.build();
    }

    //****************************************************************************************
    static public int update(FBUserProto.FBUser entry) {
        BasicDBObject qDbObject = new BasicDBObject(USER_ID, entry.getCode());

        BasicDBObject oDbObject = new BasicDBObject();
        oDbObject.put(USER_USERNAME, entry.getUsername());
        oDbObject.put(USER_PASSWD, entry.getPasswd());
        oDbObject.put(USER_NICK, entry.getNick());
        oDbObject.put(USER_EMAIL, entry.getEmail());
        oDbObject.put(USER_REMARK, entry.getRemark());
        oDbObject.put(USER_SEXUAL, entry.getSexual());

        FBRegionProto.FBProvince province = entry.getProvince();
        FBRegionProto.FBCity city = province.getCities(0);
        FBRegionProto.FBArea area = city.getAreas(0);
        oDbObject.put(USER_AREA, area.getCode());
        WriteResult writeResult = MongoHelper.sharedInstance().update(TBL_USER, qDbObject, oDbObject);
        return writeResult.getError().isEmpty() ? 1 : -1;
    }

    static public FBUserProto.FBUser getUserByUserName(String userName) throws SQLException {

        BasicDBObject dbObject = new BasicDBObject(USER_USERNAME, userName);
        DBCursor dbCursor = MongoHelper.sharedInstance().query(TBL_USER, dbObject);
        return buildUser(dbCursor);
    }

    static public FBUserProto.FBUser getUserById(String userId) {
        BasicDBObject dbObject = new BasicDBObject(USER_ID, userId);
        DBCursor dbCursor = MongoHelper.sharedInstance().query(TBL_USER, dbObject);
        return buildUser(dbCursor);
    }
}
