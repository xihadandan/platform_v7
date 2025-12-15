package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.pt.dms.dao.impl.DmsDataClassifyRelaDaoImpl;
import com.wellsoft.pt.dms.entity.DataClassifyRelaEntity;
import com.wellsoft.pt.dms.entity.DmsDataClassifyRelaEntity;
import com.wellsoft.pt.dms.service.DmsDataClassifyRelaService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
@Service
public class DmsDataClassifyRelaServiceImpl extends
        AbstractJpaServiceImpl<DmsDataClassifyRelaEntity, DmsDataClassifyRelaDaoImpl, String> implements
        DmsDataClassifyRelaService {

    @Autowired
    DyFormFacade dyFormFacade;

    @Override
    @Transactional
    public void saveClassifyRela(List<? extends DataClassifyRelaEntity> entityList,
                                 String dataRelaType) throws Exception {
        for (DataClassifyRelaEntity entity : entityList) {
            if ("1".equals(dataRelaType)) {
                // 如果是1:1的数据分类关系，则需要删除该数据已绑定的分类
                DataClassifyRelaEntity paramEntity = (DataClassifyRelaEntity) entity.getClass().newInstance();
                paramEntity.setDataUuid(entity.getDataUuid());
                List<? extends DataClassifyRelaEntity> exists = listClassifyRelaByEntity(
                        paramEntity);
                for (DataClassifyRelaEntity existObj : exists) {
                    this.dao.getSession().delete(existObj);
                }
            }
            if (CollectionUtils.isEmpty(listClassifyRelaByEntity(entity))) {
                UserDetails user = SpringSecurityUtils.getCurrentUser();
                Calendar now = Calendar.getInstance();
                entity.setCreateTime(now.getTime());
                entity.setModifyTime(now.getTime());
                entity.setCreator(user.getUserId());
                entity.setModifier(user.getUserId());
                entity.setSystemUnitId(user.getSystemUnitId());
                this.dao.getSession().saveOrUpdate(entity);
            }
        }

    }

    @Override
    @Transactional
    public void deleteClassifyRela(List<? extends DataClassifyRelaEntity> entityList) {
        for (DataClassifyRelaEntity entity : entityList) {
            List result = this.listClassifyRelaByEntity(entity);
            if (!CollectionUtils.isEmpty(result)) {
                for (Object obj : result) {
                    this.dao.getSession().delete(obj);
                }
            }
        }
    }

    @Override
    public List<QueryItem> listClassifyItems(String tableName, String uniqueColumn,
                                             String parentColumn, String displayColumn,
                                             String parentColumnValue) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userId", SpringSecurityUtils.getCurrentUserId());
        if (StringUtils.isNotBlank(parentColumn) && StringUtils.isNotBlank(parentColumnValue)) {
            param.put("parentUuid", parentColumnValue);
        }
        return this.dao.listQueryItemBySQL(
                String.format("select %s as uuid %s %s as name from %s where %s creator=:userId",
                        uniqueColumn, StringUtils
                                .isNotBlank(
                                        parentColumn) ? "," + parentColumn + " as puuid ," : ",",
                        displayColumn, tableName, (param.containsKey("parentUuid") ? parentColumn
                                + "=:parentUuid and " : (StringUtils.isBlank(
                                parentColumn) ? "" : parentColumn + " is null and "))), param,
                null);

    }

    @Override
    @Transactional
    public void saveClassifyRelaByTable(String table, List<String> dataUuids, String classifyUuid,
                                        String dataRelaType) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("uuids", dataUuids);
        param.put("cuuid", classifyUuid);
        Map<String, String> tableParam = new Gson().fromJson(table, Map.class);
        String tableName = tableParam.get("relaTable");
        String dataUuidColumn = tableParam.get("dataValueColumn");
        String classifyUuidColumn = tableParam.get("classifyValueColumn");
        if ("1".equals(dataRelaType)) {
            // 如果是1:1的数据分类关系，则需要删除该数据已绑定的分类
            this.dao.deleteBySQL(
                    "delete from " + tableName + " where " + tableParam.get(
                            "dataValueColumn") + " in(:uuids) ",
                    param);
        }
        param.clear();
        param.put("creator", SpringSecurityUtils.getCurrentUserId());
        param.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        param.put("createTime", new Date());
        param.put("modifier", param.get("creator"));
        param.put("modifyTime", param.get("createTime"));
        for (String duuid : dataUuids) {
            param.put("uuid", UuidUtils.createUuid());
            param.put("dataUuid", duuid);
            param.put("classifyUuid", classifyUuid);
            Long cnt = this.dao.countBySQL(
                    "select count(1) from " + tableName + " where " + dataUuidColumn + "=:dataUuid and " + classifyUuidColumn + "=:classifyUuid",
                    param);

            if (cnt == 0) {
                this.dao.updateBySQL(
                        "insert into " + tableName + " (uuid, " + dataUuidColumn + "," + classifyUuidColumn + ",creator,create_time," +
                                "system_unit_id,modifier,modify_time) " +
                                "values (:uuid,:dataUuid,:classifyUuid,:creator,:createTime,:systemUnitId,:modifier,:modifyTime)",
                        param);

            }

        }
    }

    public List<? extends DataClassifyRelaEntity> listClassifyRelaByEntity(
            DataClassifyRelaEntity entity) {

        StringBuilder sql = new StringBuilder(
                " from " + ClassUtils.getShortName(entity.getClass()));
        Map<String, Object> param = Maps.newHashMap();
        if (StringUtils.isNotBlank(entity.getUuid())) {
            sql.append(" where uuid=:uuid ");
            param.put("uuid", entity.getUuid());
        } else if (StringUtils.isNotBlank(entity.getDataUuid()) && StringUtils.isNotBlank(
                entity.getClassifyUuid())) {
            sql.append(" where dataUuid=:dataUuid  and classifyUuid=:classifyUuid ");
            param.put("dataUuid", entity.getDataUuid());
            param.put("classifyUuid", entity.getClassifyUuid());
        } else if (StringUtils.isNotBlank(entity.getDataUuid())) {
            sql.append(" where dataUuid=:dataUuid  ");
            param.put("dataUuid", entity.getDataUuid());
        }
        Query query = this.dao.getSession().createQuery(sql.toString());
        query.setProperties(param);
        return query.list();
    }

}
