package net.freeback.datahelper.store;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mongodb.gridfs.GridFSDBFile;
import net.freeback.connection.GridFSHelper;
import net.freeback.entries.store.FBStoreDictProto;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 赵天宇
 * Date: 12-11-7
 * Time: 上午9:56
 */
public class FBStoreDictHelper {

    static public final String BUCKET = "store.dict";

	static public void save(FBStoreDictProto.FBStoreDict storeDict) {
		GridFSHelper.sharedInstance().save(BUCKET, storeDict.getStore(), storeDict.toByteArray());
	}

	static public void remove(String id){
		GridFSHelper.sharedInstance().remove(BUCKET, GridFSHelper.buildDBObject(GridFSHelper.KEY_FILE_ID, id));
	}

	static public FBStoreDictProto.FBStoreDict find(String store) throws InvalidProtocolBufferException {
        GridFSHelper gridFSHelper = GridFSHelper.sharedInstance();
		List<GridFSDBFile> gridFSDBFileList = gridFSHelper.find(BUCKET, GridFSHelper.buildDBObject(GridFSHelper.KEY_FILE_OWNER, store));
		if (gridFSDBFileList.size() == 0) return null;
		GridFSDBFile fsdbFile = gridFSDBFileList.get(0);
		byte[] data = GridFSHelper.sharedInstance().read(fsdbFile);
		return FBStoreDictProto.FBStoreDict.parseFrom(data);
	}
}
