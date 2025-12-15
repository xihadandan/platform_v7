package com.wellsoft.pt.repository.dao.base.impl;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.repository.dao.base.BaseMongoDao;
import com.wellsoft.pt.repository.entity.mongo.file.FastDFSFileEntity;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.support.CheckedInputStream;
import com.wellsoft.pt.repository.support.DigestChecksum;
import com.wellsoft.pt.repository.support.FastDFSUtils;
import com.wellsoft.pt.repository.support.convert.FileUtil;
import com.wellsoft.pt.repository.support.enums.EnumQueryPattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

@Repository
public class BaseMongoDaoImpl implements BaseMongoDao {
    private static final ConcurrentMap<String, DB> dbPools = new ConcurrentHashMap<String, DB>();
    private static final ConcurrentMap<String, MongoDatabase> databasePools = new ConcurrentHashMap<String, MongoDatabase>();

    /*
     * private String serverURL = ""; private String serverPort = ""; private
     * String serverDbName = ""; private String serverUsername = ""; private
     * String serverPassword = "";
     */
    Logger logger = LoggerFactory.getLogger(BaseMongoDaoImpl.class);
    File uploadDir = new File(Config.UPLOAD_DIR);
    private Mongo mongo;
    @Resource(name = "nativeDao")
    private NativeDao nativeDao;

    /**
     * @throws UnknownHostException
     */
    public BaseMongoDaoImpl() {

    }

    // public BaseMongoDaoImpl(String host, int port, String dbName) throws
    // UnknownHostException {
    // this.mongo = new Mongo(host, port);
    // this.db = this.mongo.getDB(dbName);
    // }

    public static String getMongoServerUrl() {
        return Config.getValue("mongodb.server.url");
    }

    public static String getMongoServerPort() {
        return Config.getValue("mongodb.server.port");
    }

    public static String getMongoServerDbname() {
        return Config.getValue("mongodb.server.dbname");
    }

    public static String getMongoServerUsername() {
        return Config.getValue("mongodb.server.username");
    }

    public static String getMongoServerPassword() {
        return Config.getValue("mongodb.server.password");
    }

    public static int getMongoServerConnectTimeout() {
        return Integer.parseInt(Config.getValue("mongodb.server.connectTimeout", "15000"));
    }

    public static int getMongoServerSocketTimeout() {
        return Integer.parseInt(Config.getValue("mongodb.server.socketTimeout", "20000"));
    }

    public static String gridFileCollectionName() {
        String filePath = FileUtil.getPHYSICAL_TABLE();
        return filePath + ".files";
    }

    public static String gridChunkCollectionName() {
        String filePath = FileUtil.getPHYSICAL_TABLE();
        return filePath + ".chunks";
    }

   /* public DB getDb(String dbName) {
        // String serverDbName = getMongoServerDbname();
        DB db = this.dbPools.get(dbName);
        if (db != null) {
            return db;
        } else {
            String serverUsername = getMongoServerUsername();
            String serverPassword = getMongoServerPassword();

            db = this.mongo.getDB(dbName);
            // db.command("use admin");
            //
            if (StringUtils.isNotBlank(serverUsername) && StringUtils.isNotBlank(serverPassword)) {
                logger.debug("mongo用户名密码均不为空，校验开始");
                logger.debug("连接到库{}", new String[]{dbName});
                if (!db.authenticate(serverUsername, serverPassword.toCharArray())) {
                    logger.error("mongodb校验失败【dbName={}】[serverUsername={}], [serverPassword={}] ",
                            new String[]{
                                    dbName, serverUsername, serverPassword});

                    // throw new
                    // RuntimeException("auth fail when connect the mongo db   ");

                    logger.error("切到admin库，为{}对应的库添加用户[serverUsername={}], [serverPassword={}]   ",
                            new String[]{
                                    dbName, serverUsername, serverPassword});

                    // 切到admin库，为dbName对应的库添加用户
                    db = this.mongo.getDB("admin");
                    // db.command("db.addUser('admin', 'admin')");
                    if (db.authenticate("admin", "admin".toCharArray())) {
                        logger.error("登录到admin库成功， [username=admin][password=admin]， 切换到{}库",
                                new String[]{dbName});
                        db = this.mongo.getDB(dbName);
                        logger.error("为{}库添加用户[serverUsername={}], [serverPassword={}]",
                                new String[]{dbName});
                        db.addUser(serverUsername, serverPassword.toCharArray());
                        if (!db.authenticate(serverUsername, serverPassword.toCharArray())) {
                            throw new RuntimeException("auth fail when connect the mongo db   ");
                        } else {
                            logger.debug("用户添加成功");
                            this.dbPools.put(dbName, db);
                        }
                    } else {
                        logger.error("登录到admin库失败， [username=admin][password=admin]");
                        logger.error(
                                "无法为{}库添加用户， mongo auth fail when connect admin, [username=admin][password=admin]",
                                new String[]{dbName});
                        throw new RuntimeException("  mongo auth fail  ");
                    }

                } else {
                    logger.debug("校验成功[serverUsername={}], [serverPassword={}] ",
                            new String[]{dbName, serverPassword});
                    this.dbPools.put(dbName, db);
                }
            } else {
                logger.debug("mongo用户名或者密码为空，不校验");
                this.dbPools.put(dbName, db);
            }
        }

        return db;
    }*/

    public static void main(String[] args) throws NumberFormatException, UnknownHostException {
        Mongo mongo = null;
        MongoOptions options = new MongoOptions();
        mongo = new Mongo(new ServerAddress("10.24.36.250", Integer.parseInt("38128")), options);

        // 切换到admin下面验证
        DB db = mongo.getDB("admin");
//        System.out.println(db.authenticate("admin", "admin".toCharArray()));

        // 切换到其他各库进行操作
        db = mongo.getDB("T005");
        DBCollection dc = db.getCollection("test5");
        DBObject dbo = new BasicDBObject();
        dbo.put("test", "44444444444");
        System.out.println(dc.save(dbo).toString());
        ;

        /*
         * MongoCredential credential = MongoCredential.createCredential("xzsp",
         * "admin", "0".toCharArray()); MongoClient mongoClient = new
         * MongoClient(new ServerAddress("127.0.0.1", 27017),
         * Arrays.asList(credential)); MongoDatabase db =
         * mongoClient.getDatabase("T001");
         *
         * // MongoCollection coll = db.getCollection("test"); //DBObject dbo =
         * new BasicDBObject(); //dbo.put("test", System.currentTimeMillis());
         * db.getCollection("restaurants").insertOne( new Document("address",
         * new Document().append("street", "2 Avenue").append("zipcode",
         * "10075") .append("building", "1480").append("coord",
         * "(-73.9557413, 40.7720266)")) .append("borough",
         * "Manhattan").append("cuisine", "Italian").append("name", "Vella")
         * .append("restaurant_id", "41704620"));
         */

    }

    @PostConstruct
    public void init() throws UnknownHostException {
        String serverURL = getMongoServerUrl();
        String serverPort = getMongoServerPort();
        String serverUsername = getMongoServerUsername();
        String serverPassword = getMongoServerPassword();
        String serverDbname = getMongoServerDbname();

        final ServerAddress serverAddress = new ServerAddress(serverURL, Integer.parseInt(serverPort));
        final MongoClientOptions options = MongoClientOptions.builder().connectTimeout(getMongoServerConnectTimeout()).socketTimeout(getMongoServerSocketTimeout()).build();

        if (StringUtils.isNotBlank(serverUsername) && StringUtils.isNotBlank(serverPassword)) {
            final MongoCredential credential = MongoCredential.createScramSha1Credential(serverUsername, serverDbname, serverPassword.toCharArray());
            this.mongo = new MongoClient(serverAddress, credential, options);
        } else {
            this.mongo = new MongoClient(serverAddress, options);
        }
        if (StringUtils.isNotBlank(serverDbname)) {
            getDb(serverDbname);
        }
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    public DB getDb(String dbName) {
        DB db = dbPools.get(dbName);
        if (db != null) {
            return db;
        }
        synchronized (dbPools) {
            db = dbPools.get(dbName);
            if (db != null) {
                return db;
            }
            db = this.mongo.getDB(dbName);
            dbPools.putIfAbsent(dbName, db);
            return db;
        }
    }


    public MongoFileEntity saveFile(String dbName, String filePath, String fileName,
                                    String contentType,
                                    InputStream inputStream, String id, boolean validateMd5) {
        Assert.notNull(filePath, "parameter[filePath] is null");
        Assert.notNull(fileName, "parameter[fileName]  is null");

        Assert.notNull(contentType, "parameter[contentType]  is null");
        Assert.notNull(inputStream, "parameter[inputStream]  is null");
        MongoFileEntity entity = null;
        if (filePath == null) {
            filePath = "fs";
        }

        File tempFile = null;
        FileOutputStream fos = null;
        CheckedInputStream cis = null;
        InputStream fis = inputStream;
        try {
            if (BooleanUtils.toBoolean(Config.getValue("fastDFS.enable", "false"))) {
                //切换到分布式文件系统上传
                String fileid = FastDFSUtils.uploadFile(inputStream, null, null);
                FastDFSFileEntity fastDFSFileEntity = new FastDFSFileEntity(
                        FastDFSUtils.getFileInfo(fileid), fileid);
                return fastDFSFileEntity;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        try {
            if (validateMd5) {
                tempFile = new File(uploadDir, UUID.randomUUID().toString());
                fos = new FileOutputStream(tempFile);
                cis = new CheckedInputStream(inputStream);
                IOUtils.copyLarge(cis, fos);
                IOUtils.closeQuietly(fos);// flush stream
                DigestChecksum digestChecksum = (DigestChecksum) cis.getChecksum();
                String md5 = digestChecksum.getHexString();
                entity = findOneFile(dbName, filePath, new BasicDBObject("md5", md5));
                fis = new FileInputStream(tempFile);
            }
            if (entity == null) {
                GridFS gridFS = new GridFS(this.getDb(dbName), filePath);
                GridFSInputFile gfsFile = gridFS.createFile(fis);
                gfsFile.setFilename(fileName);
                gfsFile.setContentType(contentType);
                if (id == null) {
                    id = UuidUtils.createUuid();
                }
                gfsFile.setId(id);// 这里的id千万不能为null，否则会出现乱码
                gfsFile.put("uploadDate", new Date());

                gfsFile.save();
                // String id = gfsFile.getId().toString();

                GridFSDBFile dbFile = gridFS.findOne(new BasicDBObject("_id", id));
                entity = new MongoFileEntity(dbFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(fis); // 先关闭输入流,再删除文件：tempFile
            IOUtils.closeQuietly(fos);// 确保关闭
            IOUtils.closeQuietly(cis);// 只释放资源 digest
            FileUtils.deleteQuietly(tempFile);
        }
        return entity;
    }

    @Override
    public MongoFileEntity findFileById(String dbName, String filePath, String id) {
        Assert.notNull(id, "parameter[id]  is null");
        return findOneFile(dbName, filePath, new BasicDBObject("_id", id));
    }

    @Override
    public void updateFileName(String dbName, String physicalFileId, String fileName) {

        // 更新mongodb中的文件名

        DBObject dBObject = findDocumentById(dbName, gridFileCollectionName(), physicalFileId);
        DBObject updateObj = new BasicDBObject();
        updateObj.putAll(dBObject);
        updateObj.put("filename", fileName);
        DBCollection coll = this.getDb(dbName).getCollection(gridFileCollectionName());
        coll.update(dBObject, updateObj, true, false);
    }

    @Override
    public List<MongoFileEntity> findFileByName(String dbName, String filePath, String fileName,
                                                EnumQueryPattern qpattern) {
        Assert.notNull(filePath, "parameter[filePath] is null");
        Assert.notNull(qpattern, "parameter[qpattern]  is null");
        GridFS gridFS = new GridFS(this.getDb(dbName), filePath);
        BasicDBObject query = new BasicDBObject();
        Pattern pattern = Pattern.compile(EnumQueryPattern.getMatchPatternStr(qpattern, fileName),
                Pattern.CASE_INSENSITIVE);
        query.append("filename", pattern);
        List<GridFSDBFile> dbFiles = gridFS.find(query);
        List<MongoFileEntity> files = new ArrayList<MongoFileEntity>();
        for (GridFSDBFile dbFile : dbFiles) {
            MongoFileEntity file = new MongoFileEntity(dbFile);
            files.add(file);
        }

        return files;

    }

    @Override
    public void saveChunkFile(String fileID, String fileName, String dbName, String filePath, String md5, int chunkIndex, int chunkSize, long fileSize, InputStream inputStream) {
        GridFS gridFS = new GridFS(this.getDb(dbName), filePath);
        List<GridFSDBFile> fileByMd5 = this.findFileByMetadataMd5(dbName, filePath, md5);

        for (GridFSDBFile gridFSDBFile : fileByMd5) {
            int fsDBFileChunkIndex = (int) gridFSDBFile.getMetaData().get("chunkIndex");
            if (fsDBFileChunkIndex == chunkIndex) {
                //该块已上传，删除 重新上传保存
                gridFS.remove(gridFSDBFile);
            }
        }

        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("chunkSize", chunkSize);
        //块下标，下载文件时需要合并块，用这个参数排序
        basicDBObject.append("chunkIndex", chunkIndex);
        //块大小，断点续传时，用这个参数判断该块是否完整上传
        basicDBObject.append("chunkFileSize", fileSize);
        //文件唯一标识，用来筛选对应文件的所有块
        basicDBObject.append("md5", md5);
        GridFSInputFile inputFile = gridFS.createFile(inputStream, fileName);
        //设置文件类型
        inputFile.setContentType(fileName.substring(fileName.lastIndexOf(".") + 1));
        //存入元数据
        inputFile.setMetaData(basicDBObject);
        inputFile.save();
    }

    @Override
    public List<GridFSDBFile> findFileByMetadataMd5(String dbName, String filePath, String md5) {
        GridFS gridFS = new GridFS(this.getDb(dbName), filePath);
        List<GridFSDBFile> gridFSDBFiles = gridFS.find(new BasicDBObject("metadata.md5", md5), new BasicDBObject("metadata.chunkIndex", 1));
        return gridFSDBFiles;
    }

    @Override
    public GridFSDBFile findFileByMd5(String dbName, String filePath, String md5) {
        GridFS gridFS = new GridFS(this.getDb(dbName), filePath);
        GridFSDBFile dbFile = gridFS.findOne(new BasicDBObject("md5", md5));
        return dbFile;
    }

    @Override
    public void deleteFileById(String dbName, String filePath, String id) {
        Assert.notNull(filePath, "parameter[filePath] is null");
        Assert.notNull(id, "parameter[id]  is null");
        GridFS gridFS = new GridFS(this.getDb(dbName), filePath);
        gridFS.remove(new BasicDBObject("_id", id));
    }

    @Override
    public void deleteFileByFileName(String dbName, String filePath, String fileName) {
        Assert.notNull(filePath, "parameter[filePath] is null");
        Assert.notNull(fileName, "parameter[fileName]  is null");
        GridFS gridFS = new GridFS(this.getDb(dbName), filePath);
        gridFS.remove(fileName);

    }

    @Override
    public DBObject findDocumentById(String dbName, String collectionName, String id) {
        Assert.notNull(collectionName, "parameter[collectionName] is null");
        Assert.notNull(id, "parameter[id]  is null");
        DBCollection dbCollection = this.getDb(dbName).getCollection(collectionName);

        return dbCollection.findOne(new BasicDBObject("_id", id));
    }

    public DBCursor findDocument(String dbName, String collectionName, DBObject query) {
        Assert.notNull(collectionName, "parameter[collectionName] is null");
        Assert.notNull(query, "parameter[query]  is null");
        DBCollection dbCollection = this.getDb(dbName).getCollection(collectionName);
        DBCursor dbCursor = dbCollection.find(query);
        return dbCursor;
    }

    @Override
    public DBCursor findDocument(String dbName, String collectionName, DBObject query, DBObject projection) {
        Assert.notNull(collectionName, "parameter[collectionName] is null");
        Assert.notNull(query, "parameter[query]  is null");
        DBCollection dbCollection = this.getDb(dbName).getCollection(collectionName);
        DBCursor dbCursor = dbCollection.find(query, projection);
        return dbCursor;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.dao.base.impl.well.mongodb.dao.base.BaseMongoDao#createDocument(java.lang.String, java.util.Map)
     */
    @Override
    public DBObject createDocument(String dbName, String collectionName, Map<String, Object> params,
                                   String id) {
        Assert.notNull(collectionName, "parameter[collectionName] is null");
        Assert.notNull(params, "parameter[params]  is null");

        if (params == null || collectionName == null) {
            return null;
        }

        BasicDBObject obj = new BasicDBObject();
        obj.putAll(params);

        if (id == null) {
            id = UuidUtils.createUuid();
        }

        obj.put("_id", id);

        DBObject dbObj = this.createDocument(dbName, collectionName, obj);

        return dbObj;
    }

    @Override
    public DBObject createDocument(String dbName, String collectionName, DBObject dbObject) {
        Assert.notNull(collectionName, "parameter[collectionName]  is null");
        Assert.notNull(dbObject, "parameter[dbObject]  is null");

        if (collectionName == null) {
            return null;
        }

        DBCollection dbCollection = this.getDb(dbName).getCollection(collectionName);
        if (dbObject == null) {
            return null;
        }
        dbCollection.insert(dbObject);
        return dbObject;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.dao.base.impl.well.mongodb.dao.base.BaseMongoDao#createDocument(java.lang.String, java.util.Map)
     */
    @Override
    public int updateDocument(String dbName, String collectionName, Map<String, Object> params,
                              String id) {

        Assert.notNull(collectionName, "parameter[collectionName]  is null");
        Assert.notNull(params, "parameter[params]  is null");
        Assert.notNull(id, "parameter[id]  is null");

        BasicDBObject obj = new BasicDBObject();
        obj.putAll(params);

        return this.updateDocument(dbName, collectionName, obj, new BasicDBObject("_id", id));

        // return this.findDocument(collectionName, new BasicDBObject("_id", id
        // ));
    }

    @Override
    public int updateDocument(String dbName, String collectionName, DBObject dbObject,
                              DBObject query) {

        Assert.notNull(collectionName, "parameter[collectionName]  is null");
        Assert.notNull(dbObject, "parameter[dbObject]  is null");
        Assert.notNull(query, "parameter[query]  is null");
        dbObject.removeField("_id");
        DBCollection dbCollection = this.getDb(dbName).getCollection(collectionName);
        WriteResult result = dbCollection.update(query, dbObject);
        return result.getN();
    }

    @Override
    public int updateDocument(String dbName, String collectionName, DBObject dbObject, String id) {
        Assert.notNull(collectionName, "parameter[collectionName]  is null");
        Assert.notNull(dbObject, "parameter[dbObject]  is null");
        Assert.notNull(id, "parameter[id]  is null");
        // DBCollection dbCollection = db.getCollection(collectionName);
        // dbCollection.update(new BasicDBObject("_id", new Id(id)), dbObject);
        return this.updateDocument(dbName, collectionName, dbObject, new BasicDBObject("_id", id));
        // return this.findDocument(collectionName, new BasicDBObject("_id", id
        // ));
    }

    @Override
    public void deleteByGridFSDBFile(String dbName, String filePath, GridFSDBFile gridFSDBFile) {
        Assert.notNull(gridFSDBFile, "parameter[collectionName]  is null");
        GridFS gridFS = new GridFS(this.getDb(dbName), filePath);
        gridFS.remove(gridFSDBFile);
    }

    @Override
    public void deleteDocumentById(String dbName, String collectionName, String id) {
        Assert.notNull(collectionName, "parameter[collectionName]  is null");
        Assert.notNull(id, "parameter[id]  is null");

        DBCollection dbCollection = this.getDb(dbName).getCollection(collectionName);

        dbCollection.remove(new BasicDBObject("_id", id));
    }

    @Override
    public void deleteDocument(String dbName, String collectionName, DBObject query) {
        DBCollection dbCollection = this.getDb(dbName).getCollection(collectionName);
        dbCollection.remove(query);
    }

    @Override
    public DBObject findOneDocument(String dbName, String collectionName, DBObject query) {
        Assert.notNull(collectionName, "parameter[collectionName]  is null");
        Assert.notNull(query, "parameter[query]  is null");

        DBCollection dbCollection = this.getDb(dbName).getCollection(collectionName);
        DBObject dbObj = dbCollection.findOne(query);
        return dbObj;
    }

    @Override
    public MongoFileEntity findOneFile(String dbName, String filePath, DBObject query) {
        Assert.notNull(filePath, "parameter[filePath] is null");
        Assert.notNull(query, "parameter[query]  is null");
        if (BooleanUtils.toBoolean(Config.getValue("fastDFS.enable", "false"))) {
            String fileid = query.get("_id").toString();
            try {
                FastDFSFileEntity fastDFSFileEntity = new FastDFSFileEntity(
                        FastDFSUtils.getFileInfo(fileid), fileid);
                return fastDFSFileEntity;
            } catch (Exception e) {
                logger.error("获取分布式文件系统的文件异常：", e);
                return null;
            }

        }


        GridFS gridFS = new GridFS(this.getDb(dbName), filePath);

        GridFSDBFile dbFile = gridFS.findOne(query);
        if (dbFile == null) {
            return null;
        }

        MongoFileEntity entity = new MongoFileEntity(dbFile);
        return entity;
    }

    @Override
    public List<GridFSDBFile> findProtoFiles(String dbName, String filePath, String synbeforedays) {
        GridFS gridFS = new GridFS(this.getDb(dbName), filePath);
        BasicDBObject query = new BasicDBObject();
        Calendar cl = new GregorianCalendar();
        cl.setTime(new Date());
        cl.add(Calendar.DAY_OF_YEAR, Integer.parseInt(synbeforedays) * -1);
        query.put("uploadDate", new BasicDBObject("$gt", cl.getTime()));
        List<GridFSDBFile> dbFiles = gridFS.find(query);
        return dbFiles;
    }

    @Override
    public DBObject findDocumentByObjectIdId(String dbName, String collectionName, String id) {
        Assert.notNull(collectionName, "parameter[collectionName] is null");
        Assert.notNull(id, "parameter[id]  is null");
        DBCollection dbCollection = this.getDb(dbName).getCollection(collectionName);

        return dbCollection.findOne(new BasicDBObject("_id", new ObjectId(id)));
    }

    @Override
    public long countDocument(String dbName, String collectionName, DBObject query) {
        Assert.notNull(collectionName, "parameter[collectionName] is null");
        DBCollection dbCollection = this.getDb(dbName).getCollection(collectionName);
        return dbCollection.count(query);
    }

    @Override
    public void saveOrUpdate(String collectionName, Map<String, Object> data, Map<String, Object> query) {
        DBCollection dbCollection = this.getDb(FileUtil.getCurrentTenantId()).getCollection(collectionName);
        DBObject queryDoc = new BasicDBObject();
        queryDoc.putAll(query);
        DBObject document = new BasicDBObject();
        document.putAll(data);
        DBCursor cursor = dbCollection.find(queryDoc);
        if (cursor.hasNext()) {
            document = cursor.next();
            document.putAll(data);
        }
        dbCollection.save(document);
    }

    @Override
    public Set<String> getCollectionNames() {
        return this.getDb(FileUtil.getCurrentTenantId()).getCollectionNames();
    }

}
