package net.freeback.datahelper.system;

import net.freeback.connection.GridFSHelper;

/**
 * Created with IntelliJ IDEA.
 * User: 赵天宇
 * Date: 12-9-13
 * Time: 下午22:32
 * To change this template use File | Settings | File Templates.
 */
public class FBFileHelper {

    static public String save(String bucket, String owner, byte[] data) throws Exception {
        return GridFSHelper.sharedInstance().save(bucket, owner, data);
    }

//    static public FbFileInfoProto.FbMasterFile find(FbFileInfoProto.FbMasterFile masterFile) {
//        GridFSHelper gridFSHelper = GridFSHelper.sharedInstance();
//        String bucket = masterFile.getBucket();
//        if (masterFile.getObjectFileCount() == 0) {
//            return buildFiles(bucket, gridFSHelper.find(bucket, GridFSHelper.buildDBObject(GridFSHelper.KEY_FILE_MASTER, masterFile.getMaster())));
//        } else {
//            FbFileInfoProto.FbObjectFile objectFile = masterFile.getObjectFile(0);
//            if (objectFile.hasOwner()) {
//                return buildFiles(bucket, gridFSHelper.find(bucket, GridFSHelper.buildDBObject(GridFSHelper.KEY_FILE_OWNER, objectFile.getOwner())));
//            } else if (objectFile.hasName()) {
//                return buildFiles(bucket, gridFSHelper.find(bucket, GridFSHelper.buildDBObject(GridFSHelper.KEY_FILE_NAME, objectFile.getName())));
//            } else if (objectFile.hasId()) {
//                return buildFiles(bucket, gridFSHelper.find(bucket, GridFSHelper.buildDBObject(GridFSHelper.KEY_FILE_ID, objectFile.getId())));
//            }
//        }
//        return null;
//    }

    static public void remove(String bucket, String name) {
        GridFSHelper gridFSHelper = GridFSHelper.sharedInstance();
		gridFSHelper.remove(bucket, GridFSHelper.buildDBObject(GridFSHelper.KEY_FILE_NAME, name));
    }
//	static public void removeByOwner(String bucket, String owner) {
//		GridFSHelper gridFSHelper = GridFSHelper.sharedInstance();
//		gridFSHelper.remove(bucket, GridFSHelper.buildDBObject(GridFSHelper.KEY_FILE_NAME, owner));
//	}

//    static private FbFileInfoProto.FbMasterFile buildFiles(String bucket, List<GridFSDBFile> gridFSDBFileList) {
//        FbFileInfoProto.FbMasterFile.Builder masterFileBuilder = FbFileInfoProto.FbMasterFile.newBuilder();
//        for (GridFSDBFile gridFSDBFile : gridFSDBFileList) {
//            if (masterFileBuilder.getObjectFileCount() == 0) {
//                masterFileBuilder.setMaster(gridFSDBFile.get(GridFSHelper.KEY_FILE_MASTER).toString());
//                masterFileBuilder.setBucket(bucket);
//            }
//            FbFileInfoProto.FbObjectFile.Builder objectFileBuilder = FbFileInfoProto.FbObjectFile.newBuilder();
//            objectFileBuilder.setId(gridFSDBFile.getId().toString());
//            objectFileBuilder.setName(gridFSDBFile.getFilename());
//            masterFileBuilder.addObjectFile(objectFileBuilder);
//        }
//        return masterFileBuilder.build();
//    }
}
