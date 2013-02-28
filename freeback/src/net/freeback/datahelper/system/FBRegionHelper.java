package net.freeback.datahelper.system;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import net.freeback.connection.MongoHelper;
import net.freeback.entries.FBRegionProto;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-4-3
 * Time: 下午8:32
 * To change this template use File | Settings | File Templates.
 */
public class FBRegionHelper {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(FBRegionHelper.class);

    static final String TBL_REGION = "sys.region";

    static final String PROVINCE_CODE = "code";
    static final String PROVINCE_NAME = "name";
    static final String PROVINCE_DIRECTCITY = "directCity";
    static final String PROVINCE_CITYLIST = "cityList";

    static final String CITY_CODE = "code";
    static final String CITY_NAME = "name";
    static final String CITY_AREALIST = "areaList";

    static final String AREA_CODE = "code";
    static final String AREA_NAME = "name";

    static private FBRegionProto.FBRegion buildRegion(DBCursor cursor) {
        FBRegionProto.FBRegion.Builder regionBuilder = FBRegionProto.FBRegion.newBuilder();
        while (cursor.hasNext()) {
            FBRegionProto.FBProvince.Builder provinceBuilder = FBRegionProto.FBProvince.newBuilder();
            BasicDBObject provinceDBObject = (BasicDBObject) cursor.next();
            provinceBuilder.setCode(provinceDBObject.getInt(PROVINCE_CODE));
            provinceBuilder.setName(provinceDBObject.getString(PROVINCE_NAME));
            provinceBuilder.setDirectCity(provinceDBObject.getInt(PROVINCE_DIRECTCITY));

            BasicDBList cityList = (BasicDBList) provinceDBObject.get(PROVINCE_CITYLIST);
            int count = cityList.size();
            for (int i = 0; i < count; i++) {
                FBRegionProto.FBCity.Builder cityBuilder = FBRegionProto.FBCity.newBuilder();
                BasicDBObject cityDBObject = (BasicDBObject) cityList.get(i);
                cityBuilder.setCode(cityDBObject.getInt(CITY_CODE));
                cityBuilder.setName(cityDBObject.getString(CITY_NAME));

                BasicDBList areaList = (BasicDBList) cityDBObject.get(CITY_AREALIST);
                int size = areaList.size();
                for (int k = 0; k < size; k++) {
                    BasicDBObject areaDBObject = (BasicDBObject) areaList.get(k);
                    FBRegionProto.FBArea.Builder areaBuilder = FBRegionProto.FBArea.newBuilder();
                    areaBuilder.setCode(areaDBObject.getInt(AREA_CODE));
                    areaBuilder.setName(areaDBObject.getString(AREA_NAME));
                    cityBuilder.addAreas(areaBuilder);
                }
                provinceBuilder.addCities(cityBuilder);
            }
            regionBuilder.addProvinces(provinceBuilder);
        }
        return regionBuilder.build();
    }
       /*
    static public FBCodeProto.FBCodes getCodes() {
        DBCursor cursor = MongoHelper.sharedInstance().query(TBL_CODE);
        FBCodeProto.FBCodes codes = buildCodes(cursor);
        cursor.close();
        return codes;
    }*/

    static public FBRegionProto.FBRegion getRegion(int code) {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put(PROVINCE_CODE, code);
        DBCursor cursor = MongoHelper.sharedInstance().query(TBL_REGION, dbObject);
        FBRegionProto.FBRegion region = buildRegion(cursor);
        cursor.close();
        return region;
    }

    static public FBRegionProto.FBRegion buildRegion() {
        DBCollection regionCollection = MongoHelper.sharedInstance().getCollection(TBL_REGION);
        DBCursor cursor = regionCollection.find();
        FBRegionProto.FBRegion region = buildRegion(cursor);
        cursor.close();
        return region;
    }

    static public FBRegionProto.FBProvince buildProvince(int areaCode) {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put(String.format("%s.%s.%s", PROVINCE_CITYLIST, CITY_AREALIST, AREA_CODE), areaCode);
        DBCursor cursor = MongoHelper.sharedInstance().query(TBL_REGION, dbObject);
        while (cursor.hasNext()) {
            FBRegionProto.FBProvince.Builder provinceBuilder = FBRegionProto.FBProvince.newBuilder();
            BasicDBObject provinceDBObject = (BasicDBObject) cursor.next();
            provinceBuilder.setCode(provinceDBObject.getInt(PROVINCE_CODE));
            provinceBuilder.setName(provinceDBObject.getString(PROVINCE_NAME));
            provinceBuilder.setDirectCity(provinceDBObject.getInt(PROVINCE_DIRECTCITY));

            BasicDBList cityList = (BasicDBList) provinceDBObject.get(PROVINCE_CITYLIST);
            int count = cityList.size();
            for (int i = 0; i < count; i++) {
                FBRegionProto.FBCity.Builder cityBuilder = FBRegionProto.FBCity.newBuilder();
                BasicDBObject cityDBObject = (BasicDBObject) cityList.get(i);
                cityBuilder.setCode(cityDBObject.getInt(CITY_CODE));
                cityBuilder.setName(cityDBObject.getString(CITY_NAME));

                BasicDBList areaList = (BasicDBList) cityDBObject.get(CITY_AREALIST);
                int size = areaList.size();
                for (int k = 0; k < size; k++) {
                    BasicDBObject areaDBObject = (BasicDBObject) areaList.get(k);
                    if (areaDBObject.getInt(AREA_CODE) == areaCode) {
                        FBRegionProto.FBArea.Builder areaBuilder = FBRegionProto.FBArea.newBuilder();
                        areaBuilder.setCode(areaDBObject.getInt(AREA_CODE));
                        areaBuilder.setName(areaDBObject.getString(AREA_NAME));
                        cityBuilder.addAreas(areaBuilder);
                        provinceBuilder.addCities(cityBuilder);
                        return provinceBuilder.build();
                    }
                }
            }
        }
        return null;
    }

    static public FBRegionProto.FBProvince rebuildRegion(int areaCode) {
        FBRegionProto.FBProvince province = buildProvince(areaCode);
        FBRegionProto.FBProvince.Builder provinceBuilder = province.toBuilder();
        int count = provinceBuilder.getCitiesCount();
        for (int i = count - 1; i >= 0; i--) {
            FBRegionProto.FBCity city = provinceBuilder.getCities(i);
            if (city.getCode() / 100 != areaCode / 100) {
                provinceBuilder.getCitiesList().remove(i);
            }
        }
        FBRegionProto.FBCity city = provinceBuilder.getCities(0);
        count = city.getAreasCount();
        for (int i = count - 1; i >= 0; i--) {
            FBRegionProto.FBArea area = city.getAreas(i);
            if (area.getCode() != areaCode)
            {
                city.getAreasList().remove(i);
            }
        }
        return provinceBuilder.build();
    }


/*
    public List<FBRegionCityProto.FBRegionCity> getCities(int code) throws SQLException {
        java.util.List<JdbcParameter> parameters = new ArrayList<JdbcParameter>();
        parameters.add(new JdbcParameter("_CODE", Types.INTEGER, code));
        ResultSet rs = C3P0Helper.executeQuery(this.connection, REGION_SEL_CITY, parameters);
        return this.buildCities(rs);
    }

    public List<FBRegionAreaProto.FBRegionArea> getAreas(int code) throws SQLException {
        java.util.List<JdbcParameter> parameters = new ArrayList<JdbcParameter>();
        parameters.add(new JdbcParameter("_CODE", Types.INTEGER, code));
        ResultSet rs = C3P0Helper.executeQuery(this.connection, REGION_SEL_AREA, parameters);
        return this.buildAreas(rs);
    }

    public void saveProvince(FBRegionProvinceProto.FBRegionProvince entry) throws SQLException {
        List<JdbcParameter> parameters = new ArrayList<JdbcParameter>();
        parameters.add(new JdbcParameter("_CODE", Types.INTEGER, entry.getCode()));
        parameters.add(new JdbcParameter("_LOCATION", Types.VARCHAR, entry.getLocation()));
        parameters.add(new JdbcParameter("_DIRECTCITY", Types.VARCHAR, entry.getDirectCity()));
        C3P0Helper.executeNoQuery(this.connection, REGION_INSERT_PROVINCE, parameters);
        FBRegionProvinceProto.FBRegionProvinces.Builder builder = provinces.toBuilder();
        builder.addProvinces(entry);
        provinces = builder.build();
    }

    public void saveCity(FBRegionCityProto.FBRegionCity entry) throws SQLException {
        List<JdbcParameter> parameters = new ArrayList<JdbcParameter>();
        parameters.add(new JdbcParameter("_CODE", Types.INTEGER, entry.getCode()));
        parameters.add(new JdbcParameter("_CITY", Types.VARCHAR, entry.getCity()));
        parameters.add(new JdbcParameter("_PARENT", Types.VARCHAR, entry.getParent()));
        C3P0Helper.executeNoQuery(this.connection, REGION_INSERT_CITY, parameters);
    }

    public void saveArea(FBRegionAreaProto.FBRegionArea entry) throws SQLException {
        List<JdbcParameter> parameters = new ArrayList<JdbcParameter>();
        parameters.add(new JdbcParameter("_CODE", Types.INTEGER, entry.getCode()));
        parameters.add(new JdbcParameter("_AREA", Types.VARCHAR, entry.getArea()));
        parameters.add(new JdbcParameter("_PARENT", Types.VARCHAR, entry.getParent()));
        C3P0Helper.executeNoQuery(this.connection, REGION_INSERT_AREA, parameters);
    }*/
}
