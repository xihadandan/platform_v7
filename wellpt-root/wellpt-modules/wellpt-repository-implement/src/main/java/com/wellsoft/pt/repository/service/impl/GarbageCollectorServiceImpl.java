/*
 * @(#)2016年3月14日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.service.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.dao.base.impl.BaseMongoDaoImpl;
import com.wellsoft.pt.repository.dao.impl.FileDaoImpl;
import com.wellsoft.pt.repository.service.GarbageCollectorService;
import com.wellsoft.pt.repository.service.MongoFileService;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.hibernate.ScrollableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月14日.1	zhulh		2016年3月14日		Create
 * </pre>
 * @date 2016年3月14日
 */
@Service
public class GarbageCollectorServiceImpl extends BaseServiceImpl implements GarbageCollectorService {

    @Autowired
    private FileDaoImpl fileDao;

    @Autowired
    private BaseMongoDaoImpl baseMongoDao;

    @Autowired
    private MongoFileService mongoFileService;

    private String GET_PHYSICAL_FILE_ID = "select distinct t.physicalFileId from LogicFileInfo t";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.GarbageCollectorService#initRepoFileMirror()
     */
    @Override
    @Transactional(readOnly = true)
    public void syncRepoFileMirror(String tenantId) {
        DB db = baseMongoDao.getDb(tenantId);
        DBCollection mirror = db.getCollection("repo_file_mirror");
        ScrollableResults sr = fileDao.createQuery(GET_PHYSICAL_FILE_ID, new HashMap<String, Object>()).scroll();
        while (sr.next()) {
            String fileId = sr.getString(0);
            if (mirror.findOne(new BasicDBObject("physical_file_id", fileId)) == null) {
                Map<String, String> record = new HashMap<String, String>();
                record.put("physical_file_id", fileId);
                DBObject dbObject = (DBObject) JSON.parse(JsonUtils.object2Json(record));
                mirror.save(dbObject);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.GarbageCollectorService#cleanGridFS(java.lang.String)
     */
    @Override
    public void cleanGridFS(String tenantId) {
        DB db = baseMongoDao.getDb(tenantId);
        String filesCollectionName = fileDao.getPHYSICAL_TABLE() + ".files";
        String chunksCollectionName = fileDao.getPHYSICAL_TABLE() + ".chunks";
        db.eval(generateCommand(filesCollectionName, chunksCollectionName));
    }

    /**
     * @param collectionName
     * @return
     */
    private String generateCommand(String filesCollectionName, String chunksCollectionName) {
        try {
            Configuration cfg = new Configuration();
            cfg.setClassForTemplateLoading(GarbageCollectorService.class, "../ftl");
            cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);

            Map<String, Object> root = new HashMap<String, Object>();
            root.put("filesCollectionName", filesCollectionName);
            root.put("chunksCollectionName", chunksCollectionName);
            Template template = cfg.getTemplate("GarbageCollector.ftl", Encoding.UTF8.getValue());

            Writer writer = new StringWriter();
            template.process(root, writer);
            writer.flush();
            writer.close();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
