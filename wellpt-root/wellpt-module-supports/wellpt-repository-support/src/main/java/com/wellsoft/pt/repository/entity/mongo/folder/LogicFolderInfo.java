package com.wellsoft.pt.repository.entity.mongo.folder;

import com.mongodb.DBObject;
import com.wellsoft.pt.repository.support.DateUtils4DB;
import com.wellsoft.pt.repository.support.enums.EnumOperateType;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicFolderInfo {

    private String _id;
    private List<SubFile> sub_file;
    private List<OperateLog> operate_log;

    public static LogicFolderInfo parse(String json) {
        @SuppressWarnings("rawtypes")
        Map<String, Class> classMap = new HashMap<String, Class>();
        classMap.put("operate_log", OperateLog.class);
        classMap.put("sub_file", SubFile.class);

        LogicFolderInfo logicFileInfo = (LogicFolderInfo) JSONObject.toBean(JSONObject.fromObject(json),
                LogicFolderInfo.class, classMap);

        return logicFileInfo;
    }

    public static LogicFolderInfo parse(DBObject dbObj) {
        return parse(dbObj.toString());
    }

    public static OperateLog generateOperateLog(EnumOperateType operateType, String fileID, String purpose) {
        OperateLog log = new OperateLog();
        log.setOperate_type(operateType.getValue());
        log.setOperator(SpringSecurityUtils.getCurrentUserId());
        log.setOperator_time(DateUtils4DB.formate2DbPattern(System.currentTimeMillis()));
        if (fileID != null && purpose != null) {
            SubFile file = new SubFile();
            file.set_id(fileID);
            file.setPurpose(purpose);
            log.setSub_file(file);
        }
        return log;

    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<SubFile> getSub_file() {
        return sub_file;
    }

    public void setSub_file(List<SubFile> sub_file) {
        this.sub_file = sub_file;
    }

    public List<OperateLog> getOperate_log() {
        return operate_log;
    }

    public void setOperate_log(List<OperateLog> operate_log) {
        this.operate_log = operate_log;
    }

    /**
     * 添加日志
     *
     * @param type
     * @param operator
     * @param file
     */
    @SuppressWarnings("static-access")
    public void pushOperateLog(EnumOperateType operateType, String fileID, String purpose) {
        if (this.operate_log == null) {
            this.operate_log = new ArrayList<OperateLog>();
        }

        OperateLog log = this.generateOperateLog(operateType, fileID, purpose);

        this.operate_log.add(log);

    }

}
