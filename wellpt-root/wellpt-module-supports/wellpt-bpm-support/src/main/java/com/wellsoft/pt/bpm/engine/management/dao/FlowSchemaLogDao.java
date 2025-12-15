package com.wellsoft.pt.bpm.engine.management.dao;

import com.wellsoft.pt.bpm.engine.element.FlowElement;
import com.wellsoft.pt.bpm.engine.management.entity.FlowSchemaLog;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作者： linz
 * 创建时间：2015-1-30 下午8:10:05
 * 类描述：TODO
 * 备注：预算数据版本Dao层
 */
public interface FlowSchemaLogDao extends JpaDao<FlowSchemaLog, String> {

    /**
     * 根据flowSchemaUuid查询对应的log记录
     *
     * @param flowSchemaUuid
     * @return
     */
    List<FlowSchemaLog> getByParentUuid(String flowSchemaUuid);

    BigDecimal genVersionByParentUuid(String parId, FlowElement flowElement);

}
