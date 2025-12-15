package com.wellsoft.pt.dms.service;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dms.dao.impl.DmsDataClassifyRelaDaoImpl;
import com.wellsoft.pt.dms.entity.DataClassifyRelaEntity;
import com.wellsoft.pt.dms.entity.DmsDataClassifyRelaEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据分类关系服务
 *
 * @author chenq
 * @date 2018/6/29
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/29    chenq		2018/6/29		Create
 * </pre>
 */
public interface DmsDataClassifyRelaService extends
        JpaService<DmsDataClassifyRelaEntity, DmsDataClassifyRelaDaoImpl, String> {

    /**
     * 保存数据分类
     *
     * @param entity
     * @param dataRelaType
     */
    void saveClassifyRela(List<? extends DataClassifyRelaEntity> entity,
                          String dataRelaType) throws Exception;

    /**
     * 删除数据分类
     *
     * @param entityList
     */
    void deleteClassifyRela(List<? extends DataClassifyRelaEntity> entityList);


    List<QueryItem> listClassifyItems(String tableName, String uniqueColumn, String parentColumn,
                                      String displayColumn, String parentColumnValue);

    void saveClassifyRelaByTable(String table, List<String> dataUuids, String classifyUuid,
                                 String dataRelaType);
}
