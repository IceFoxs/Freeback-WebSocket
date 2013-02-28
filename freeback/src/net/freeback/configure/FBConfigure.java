package net.freeback.configure;

import net.freeback.entries.FBConfigureProto;
/**
 * Created by IntelliJ IDEA.
 * User: freeback
 * Date: 12-5-12
 * Time: 上午11:36
 * To change this template use File | Settings | File Templates.
 */
public class FBConfigure {
    static public final int PASSWORD_ENCYPT_TIMES = 2;
    static public final String PASSWORD_CONFUSE = "F@r_e#E$-b!Ac!K*2**0**1**3**0#@4";
    static public String Server_Error_Format = "服务器数据处理异常. ErrorCode: %d";

    static public FBConfigureProto.FBConfigure buildConfigure() {
        FBConfigureProto.FBConfigure.Builder configureBuilder = FBConfigureProto.FBConfigure.newBuilder();
        configureBuilder.setVersionCodec(1);
        configureBuilder.setVersionSystem(1);
        configureBuilder.setObjectIdLength(24);

        //configureBuilder.setMongoHost("192.168.1.20");
        configureBuilder.setMongoHost("127.0.0.1");
        configureBuilder.setMongoPort(27017);
        configureBuilder.setFileUrlPrefix("http://192.168.1.20:8080/files");

        configureBuilder.setFreebackDB("freeback");
        configureBuilder.setGridFSDB("fbgridfs");

        configureBuilder.setAuthUser("fbuser01");
        configureBuilder.setAuthPassword("fbuser01");

        //configureBuilder.setServerHost("192.168.1.20");
        configureBuilder.setServerHost("127.0.0.1");
        configureBuilder.setServerPort(8090);
        configureBuilder.setServerMaxProcessor(3);

        configureBuilder.setServerMaxConnection(1000);
        configureBuilder.setServerMinReadBufferSize(51200);
        configureBuilder.setServerMaxReadBufferSize(6000000);

        return configureBuilder.build();
    }
}
