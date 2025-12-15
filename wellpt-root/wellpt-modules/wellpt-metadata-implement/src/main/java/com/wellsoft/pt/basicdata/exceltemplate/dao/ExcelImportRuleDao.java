package com.wellsoft.pt.basicdata.exceltemplate.dao;

import com.google.common.collect.Lists;
import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelImportRule;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: Excel导入规则数据层访问类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-24.1	zhouyq		2013-4-24		Create
 * </pre>
 * @date 2013-4-24
 */
@Repository
public class ExcelImportRuleDao extends HibernateDao<ExcelImportRule, String> {
    public static List<MongoFileEntity> getMongoFileEntityByFileUuid(MongoFileService mongoFileService, String fileUuid) {
        List<MongoFileEntity> files = mongoFileService.getFilesFromFolder(fileUuid, "attach");
        if (files == null || files.size() == 0) {
            MongoFileEntity file = mongoFileService.getFile(fileUuid);
            if (null != file) {
                if (null == files) {
                    files = Lists.newArrayList();
                }
                files.add(file);
            }
        }
        return files;
    }

    public boolean idIsExists(String id, String uuid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        StringBuilder hql = new StringBuilder(" from ExcelImportRule o where o.id = :id ");
        if (StringUtils.isNotBlank(uuid)) {
            hql.append(" and o.uuid <> :uuid ");
            params.put("uuid", uuid);
        }
        return this.query(hql.toString(), params, ExcelImportRule.class).size() > 0;
    }
}
