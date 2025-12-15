package com.wellsoft.pt.document.impl;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.document.MongoLogService;
import com.wellsoft.pt.repository.dao.base.BaseMongoDao;
import com.wellsoft.pt.repository.support.DateUtils4DB;
import com.wellsoft.pt.repository.support.convert.FileUtil;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Transactional
public class MongoLogServiceImpl implements MongoLogService {
    private static String ip = null;
    Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    BaseMongoDao baseMongoDao;

    {
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static String getServerIp() {
        return ip;
    }

    @Override
    public String save(ModuleID moduleId, String interfaceId, String interfaceTitle, JSONObject logContent) {
        // DBObject dbObject = transformJSONIntoBSON(logContent);
        // baseMongoDao.createDocument(physicalLogTblName, dbObject);
        return null;
    }

    private DBObject transformJSONIntoBSON(JSONObject logContent) {
        DBObject dbObject = new BasicDBObject();
        Iterator<String> it = logContent.keys();
        while (it.hasNext()) {
            String key = it.next();
            try {
                Object value = logContent.has(key) ? logContent.get(key) : null;
                if (value instanceof JSONObject) {

                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }

        }
        return null;
    }

    @Override
    public String save(ModuleID moduleId, String interfaceId, String interfaceTitle, String logContent) {
        try {
            DBObject dbObject = new BasicDBObject();
            dbObject.put("moduleCode", moduleId.getCode());
            dbObject.put("moduleName", moduleId.getValue());
            dbObject.put("moduleId", moduleId.getName());
            dbObject.put("interfaceId", interfaceId);
            dbObject.put("interfaceTitle", interfaceTitle);
            dbObject.put("createTime", DateUtils4DB.formate2DbPattern(System.currentTimeMillis()));
            dbObject.put("creatorId", SpringSecurityUtils.getCurrentUserId());
            dbObject.put("creatorName", SpringSecurityUtils.getCurrentUserName());
            dbObject.put("creatorIp", SpringSecurityUtils.getCurrentUserIp());
            dbObject.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
            dbObject.put("serverIP", getServerIp());
            dbObject.put("content", logContent);
            dbObject = baseMongoDao.createDocument(FileUtil.getCurrentTenantId(), physicalLogTblName, dbObject);
            Object _id = dbObject.get("_id");
            String id = _id.toString();
            return id;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String save2Sap(ModuleID moduleId, String interfaceId, String interfaceTitle, String sapConfig,
                           String interfacer, String logContent) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("msg", logContent);
            obj.put("sapConfig", sapConfig);
            obj.put("interfacer", interfacer);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }

        return this.save(moduleId, interfaceId, interfaceTitle, obj.toString());
    }

    /**
     * Map 转成 QueryItem
     *
     * @param map
     * @return
     */
    private QueryItem map2QueryItem(Map<String, Object> map) {
        QueryItem item = new QueryItem();
        if (null != map && !map.isEmpty()) {
            item.putAll(map);
        }
        return item;
    }

    @Override
    public DBObject findLogById(String id) {
        return baseMongoDao.findDocumentByObjectIdId(FileUtil.getCurrentTenantId(), physicalLogTblName, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<QueryItem> findLog(Map<String, Object> queryParams) {
        if (null == queryParams || queryParams.isEmpty()) {
            queryParams = new HashMap<String, Object>();
        }
        // 条件列表
        BasicDBList condList = new BasicDBList();
        // 创建者名称
        setDimQueryParam(condList, String.valueOf(queryParams.get("creatorName")), "creatorName");
        // 创建者Id
        setDimQueryParam(condList, String.valueOf(queryParams.get("creatorId")), "creatorId");
        // creatorIp
        setDimQueryParam(condList, String.valueOf(queryParams.get("creatorIp")), "creatorIp");
        // tenantId
        setDimQueryParam(condList, String.valueOf(queryParams.get("tenantId")), "tenantId");
        // serverIP
        setDimQueryParam(condList, String.valueOf(queryParams.get("serverIP")), "serverIP");
        // 模块
        setDimQueryParam(condList, String.valueOf(queryParams.get("moduleCode")), "moduleCode");
        setDimQueryParam(condList, String.valueOf(queryParams.get("moduleId")), "moduleId");
        setDimQueryParam(condList, String.valueOf(queryParams.get("moduleName")), "moduleName");
        // 接口ID
        setDimQueryParam(condList, String.valueOf(queryParams.get("interfaceId")), "interfaceId");
        // 接口名称
        setDimQueryParam(condList, String.valueOf(queryParams.get("interfaceTitle")), "interfaceTitle");
        // 日志内容
        setDimQueryParam(condList, String.valueOf(queryParams.get("content")), "content");
        // 创建时间
        if (null != queryParams.get("startDate")) {
            BasicDBObject cond = new BasicDBObject();
            cond.append(
                    "createTime",
                    new BasicDBObject("$gte", DateUtils4DB.formate2DbPattern(((Date) queryParams.get("startDate"))
                            .getTime())));
            condList.add(cond);
        }
        if (null != queryParams.get("endDate")) {
            BasicDBObject cond = new BasicDBObject();
            cond.append(
                    "createTime",
                    new BasicDBObject("$lte", DateUtils4DB.formate2DbPattern(((Date) queryParams.get("endDate"))
                            .getTime())));
            condList.add(cond);
        }

        DBObject query = new BasicDBObject();
        if (null != condList && condList.size() > 0) {
            query.put("$and", condList);
        }
        DBCursor dBCursor = baseMongoDao.findDocument(FileUtil.getCurrentTenantId(), physicalLogTblName, query);
        //按创建时间倒叙
        dBCursor.sort(new BasicDBObject("createTime", -1));
        //分页
        if (null != queryParams.get("pagingInfo")) {
            PagingInfo pagingInfo = (PagingInfo) queryParams.get("pagingInfo");
            dBCursor.limit(pagingInfo.getPageSize());
            dBCursor.skip(pagingInfo.getFirst());
            pagingInfo.setTotalCount(dBCursor.count());
        }
        List<QueryItem> listOfMaps = new ArrayList<QueryItem>();

        // 将游标中返回的结果记录到list集合中
        while (dBCursor.hasNext()) {
            listOfMaps.add(map2QueryItem(dBCursor.next().toMap()));
        }
        dBCursor.close();
        return listOfMaps;
    }

    /**
     * 设置mongolog模糊查询条件 string
     *
     * @param condList
     * @param param
     * @param paramName
     */
    private void setDimQueryParam(BasicDBList condList, String param, String paramName) {
        if (StringUtils.isBlank(param) || "null".equals(param)) {
            return;
        }
        BasicDBObject cond = new BasicDBObject();
        Pattern pattern = Pattern.compile("^.*" + param + ".*$", Pattern.CASE_INSENSITIVE);
        cond.put(paramName, pattern);
        condList.add(cond);
    }
}
