package net.freeback.datahelper.system;

import net.freeback.configure.FBConfigure;
import net.freeback.connection.GridFSHelper;
import net.freeback.entries.FBSystemProto;
import org.bson.BasicBSONCallback;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhaotianyu
 * Date: 12-10-17
 * Time: 上午10:17
 * To change this template use File | Settings | File Templates.
 */
public class FBSystemHelper {

    static public String buildObjectId(int count)
    {
        String ids = new org.bson.types.ObjectId().toString();
        int i = 1;
        while (i < count)
        {
            ids += "," + new org.bson.types.ObjectId().toString();
        }
        return ids;
    }

    static public FBSystemProto.FBSystem buildSystem(int versionValue)
    {
        FBSystemProto.FBSystem.Builder systemBuilder = FBSystemProto.FBSystem.newBuilder();

        systemBuilder.setRegion(FBRegionHelper.buildRegion());

        systemBuilder.setVersionValue(versionValue);
        systemBuilder.addIndustires(FBSystemProto.FBIndustry.newBuilder().setCode(1000).setName("饮品"));
        systemBuilder.addIndustires(FBSystemProto.FBIndustry.newBuilder().setCode(1001).setName("水果"));
        systemBuilder.addIndustires(FBSystemProto.FBIndustry.newBuilder().setCode(1002).setName("粮油"));
        systemBuilder.addIndustires(FBSystemProto.FBIndustry.newBuilder().setCode(1003).setName("超市"));
        systemBuilder.addIndustires(FBSystemProto.FBIndustry.newBuilder().setCode(1004).setName("其它"));

        systemBuilder.addDimensions(FBSystemProto.FBDimension.newBuilder().setGroup("体积")
				.addDimension("500ML")
				.addDimension("1L")
				.addDimension("2.5L")
				.addDimension("5L")
				.build());

		systemBuilder.addDimensions(FBSystemProto.FBDimension.newBuilder().setGroup("重量")
				.addDimension("50g")
				.addDimension("1KG")
				.addDimension("2.5KG")
				.addDimension("5KG")
				.addDimension("10KG")
				.addDimension("25KG")
				.addDimension("50KG")
				.addDimension("1公斤")
				.addDimension("2.5公斤")
				.addDimension("5公斤")
				.addDimension("10公斤")
				.addDimension("25公斤")
				.addDimension("50公斤")
				.build());

        systemBuilder.addDimensions(FBSystemProto.FBDimension.newBuilder().setGroup("数量")
				.addDimension("个")
				.addDimension("只")
				.addDimension("支")
				.build());

        return systemBuilder.build();
    }
}
