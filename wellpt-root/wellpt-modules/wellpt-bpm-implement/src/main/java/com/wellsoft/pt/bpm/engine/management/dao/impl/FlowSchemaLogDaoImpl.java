package com.wellsoft.pt.bpm.engine.management.dao.impl;

import com.wellsoft.pt.bpm.engine.element.FlowElement;
import com.wellsoft.pt.bpm.engine.management.dao.FlowSchemaLogDao;
import com.wellsoft.pt.bpm.engine.management.entity.FlowSchemaLog;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

/**
 * 作者： linz
 * 创建时间：2015-1-30 下午8:10:05
 * 类描述：TODO
 * 备注：预算数据版本Dao层
 */
@Repository
public class FlowSchemaLogDaoImpl extends AbstractJpaDaoImpl<FlowSchemaLog, String> implements FlowSchemaLogDao {

    /**
     * 根据flowSchemaUuid查询对应的log记录
     *
     * @param flowSchemaUuid
     * @return
     */
    public List<FlowSchemaLog> getByParentUuid(String flowSchemaUuid) {
        String hql = "from FlowSchemaLog flowSchemaLog where flowSchemaLog.parentFlowSchemaUUID=:parentFlowSchemaUUID order by parentFlowSchemaUUID.createTime desc";
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("parentFlowSchemaUUID", flowSchemaUuid);
        return this.listByHQL(hql, queryMap);
    }

    public BigDecimal genVersionByParentUuid(String parId, FlowElement flowElement) {
        BigDecimal maxVersion = new BigDecimal(flowElement.getVersion()).setScale(1, RoundingMode.DOWN);
        StringBuilder sqlbuf = new StringBuilder();
        sqlbuf.append("select max(w.flow_version) from wf_flow_schema_log w where w.flow_version is not null");
        sqlbuf.append(" and parent_flow_schemauuid = '");
        sqlbuf.append(parId);
        sqlbuf.append("' ");
        List<Object> obj = this.getSession().createSQLQuery(sqlbuf.toString()).list();
        if (obj.get(0) == null) {
            maxVersion = maxVersion.add(BigDecimal.valueOf(0.0001));
        } else {
            maxVersion = ((BigDecimal) obj.get(0)).add(BigDecimal.valueOf(0.0001));
        }
        return maxVersion.setScale(4, RoundingMode.DOWN);
    }

}
