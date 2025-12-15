package com.wellsoft.pt.document;

import com.mongodb.DBObject;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.jdbc.support.QueryItem;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 日志操作接口
 *
 * @author hunt
 */
public interface MongoLogService {
    static String physicalLogTblName = "interface_log";

    /**
     * 保存日志接口
     *
     * @param moduleId       模块id
     * @param interfaceId    接口id
     * @param interfaceTitle 接口标题
     * @param logContent     日志内容, org.json.JSONObject格式
     * @return 返回日志ID
     */
    public String save(ModuleID moduleId, String interfaceId,
                       String interfaceTitle, JSONObject logContent);

    public String save(ModuleID moduleId, String interfaceId,
                       String interfaceTitle, String logContent);

    public String save2Sap(ModuleID moduleId, String interfaceId,
                           String interfaceTitle, String sapConfig, String interfacer,
                           String logContent);

    /**
     * 查询日志接口 可根据创建者，创建时间, 模块，接口ID，接口名称，日志内容(json格式)
     *
     * @param creatorId      创建者Id
     * @param minCreateTime  最早创建时间
     * @param maxCreateTime  最晚创建时间
     * @param moduleId       模块id
     * @param moduleName     模块名称
     * @param moduleCode     模块code
     * @param interfaceId    接口ID
     * @param interfaceTitle 接口名称
     * @param logContent     日志内容
     * @param creatorIp      creatorIp
     * @param tenantId       tenantId
     * @param serverIP       serverIP
     * @param pagingInfo     PagingInfo 分页条件
     * @return
     */
    public List<QueryItem> findLog(Map<String, Object> queryParams);

    /**
     * 通过id查找mongodbLog
     *
     * @param id
     * @return
     */
    public DBObject findLogById(String id);
}
