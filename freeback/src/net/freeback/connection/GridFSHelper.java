package net.freeback.connection;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 赵天宇
 * Date: 12-11-1
 * Time: 下午2:42
 */
public class GridFSHelper extends MongoBase {
    static public final String KEY_FILE_ID = "_id";
    static public final String KEY_FILE_NAME = "filename";
    static public final String KEY_FILE_OWNER = "owner";

    static protected Logger logger = Logger.getLogger(GridFSHelper.class);

    private GridFSHelper() {
        super();
    }

    static private GridFSHelper _Helper = null;

    static public GridFSHelper sharedInstance() {
        if (_Helper == null) {
            synchronized (GridFSHelper.class) {
                if (_Helper == null) {
                    _Helper = new GridFSHelper();
                }
            }
        }
        return _Helper;
    }

    private GridFS getGridFS(String bucket) {
        return new GridFS(this._DBFreeback, bucket);
    }

    static public BasicDBObject buildDBObject(String key, String value) {
        return new BasicDBObject(key, value);
    }


    public String save(String bucket, String owner, byte[] buffer) {
        String fileName = MongoHelper.sharedInstance().buildObjectId().toString();
        GridFS gfs = this.getGridFS(bucket);
        GridFSInputFile gfsFile = gfs.createFile(buffer);
        gfsFile.setFilename(fileName);
        gfsFile.put(KEY_FILE_OWNER, owner);
        gfsFile.save();
        return fileName;
    }

    public void remove(String bucket, BasicDBObject query) {
        GridFS gfs = this.getGridFS(bucket);
        gfs.remove(query);
    }

    public List<GridFSDBFile> find(String bucket, BasicDBObject query) {
        GridFS gfs = this.getGridFS(bucket);
        return gfs.find(query);
    }

    public byte[] read(GridFSDBFile fsdbFile) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            fsdbFile.writeTo(bos);
            bos.flush();
            bos.close();
            return bos.toByteArray();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public byte[] fileReadToBuffer(String fileName, String bucket) {
        try {
            GridFS gfsPhoto = this.getGridFS(bucket);
            GridFSDBFile gridFSDBFile = gfsPhoto.findOne(fileName);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            gridFSDBFile.writeTo(bos);
            bos.flush();
            bos.close();
            return bos.toByteArray();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
