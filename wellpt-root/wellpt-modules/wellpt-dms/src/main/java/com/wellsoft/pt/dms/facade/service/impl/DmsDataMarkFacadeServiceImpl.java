package com.wellsoft.pt.dms.facade.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.dms.bean.MarkDataDto;
import com.wellsoft.pt.dms.entity.DataMarkEntity;
import com.wellsoft.pt.dms.facade.service.DataMarkService;
import com.wellsoft.pt.dms.facade.service.DmsDataMarkFacadeService;
import com.wellsoft.pt.jpa.util.ClassUtils;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/6/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/19    chenq		2018/6/19		Create
 * </pre>
 */
@Service
public class DmsDataMarkFacadeServiceImpl extends AbstractApiFacade implements
        DmsDataMarkFacadeService {
    @Autowired
    DataMarkService dataMarkService;

    @Override
    public void saveOrDeleteMark(MarkDataDto mark) {
        try {

            List<DataMarkEntity> dataList = mark.getDataList();
            List<DataMarkEntity> dataMarkEntityList = Lists.newArrayList();
            realDataMarkRelaEntityConvertion(dataList, dataMarkEntityList,
                    mark.getEntityClassName());
            if (StringUtils.isNotBlank(mark.getEntityClassName())) {
                dataMarkService.saveOrDeleteDataMarkRela(dataMarkEntityList,
                        mark.getIsDeleteMark());
            }

            if (StringUtils.isNotBlank(mark.getTableName()) && StringUtils.isNotBlank(
                    mark.getStatusColumn())) {
                dataMarkService.updateDataTableMarked(mark.getDataList(), mark.getTableName(),
                        mark.getStatusColumn(),
                        mark.getUpdateTimeColumn(),
                        mark.getIsDeleteMark());
            }

        } catch (Exception e) {
            logger.error("数据标记门面服务异常：", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public void saveDataMark(DataMarkEntity entity) {
        dataMarkService.saveOrDeleteDataMarkRela(Lists.<DataMarkEntity>newArrayList(entity),
                false);

    }


    @Override
    public void saveDataMarkList(List<? extends DataMarkEntity> dataMarkEntities) {
        dataMarkService.saveOrDeleteDataMarkRela(dataMarkEntities,
                false);

    }

    @Override
    public void deleteDataMark(DataMarkEntity entity) {
        dataMarkService.saveOrDeleteDataMarkRela(Lists.<DataMarkEntity>newArrayList(entity),
                true);
    }

    @Override
    public void deleteDataMarkList(List<? extends DataMarkEntity> dataMarkEntities) {
        dataMarkService.saveOrDeleteDataMarkRela(
                Lists.<DataMarkEntity>newArrayList(dataMarkEntities),
                true);
    }


    @Override
    public void saveDataMark(String dataUuid, Class<? extends DataMarkEntity> markClass) {
        dataMarkService.saveOrDeleteMarkRela(Lists.newArrayList(dataUuid), markClass, false);
    }

    @Override
    public void saveDataMarkByDataUuids(List<String> dataUuids,
                                        Class<? extends DataMarkEntity> markClass) {
        dataMarkService.saveOrDeleteMarkRela(dataUuids, markClass, false);
    }

    @Override
    public void deleteDataMark(String dataUuid, Class<? extends DataMarkEntity> markClass) {
        dataMarkService.saveOrDeleteMarkRela(Lists.newArrayList(dataUuid), markClass, true);
    }

    @Override
    public void deleteDataMarkByDataUuids(List<String> dataUuids,
                                          Class<? extends DataMarkEntity> markClass) {
        dataMarkService.saveOrDeleteMarkRela(dataUuids, markClass, false);
    }

    private void realDataMarkRelaEntityConvertion(List<DataMarkEntity> dataList,
                                                  List<DataMarkEntity> dataMarkEntityList,
                                                  String entityClassName) throws Exception {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        for (DataMarkEntity dt : dataList) {
            DataMarkEntity dataMarkEntity = StringUtils.isNotBlank(
                    entityClassName) ? (DataMarkEntity) ClassUtils.getEntityClasses().get(
                    StringUtils.uncapitalize(
                            entityClassName)).newInstance() : new DataMarkEntity();
            BeanUtils.copyProperties(dt, dataMarkEntity);
            dataMarkEntity.setUserId(user.getUserId());
            dataMarkEntityList.add(dataMarkEntity);
        }
    }

    @Override
    public Map<String, Boolean> dataMarkResult(MarkDataDto mark) {
        Map<String, Boolean> result = Maps.newHashMap();
        try {
            List<DataMarkEntity> dataList = mark.getDataList();
            List<DataMarkEntity> dataMarkEntityList = Lists.newArrayList();
            realDataMarkRelaEntityConvertion(dataList, dataMarkEntityList,
                    mark.getEntityClassName());
            List<String> requestUuids = Lists.transform(dataList,
                    new Function<DataMarkEntity, String>() {
                        @Override
                        public String apply(DataMarkEntity dataMarkEntity) {
                            return dataMarkEntity.getDataUuid();
                        }
                    });
            if (StringUtils.isNotBlank(mark.getEntityClassName())) {
                List<String> existDataUuids = dataMarkService.existMarkRelasByRelaEntities(
                        dataMarkEntityList);
                for (String u : existDataUuids) {
                    result.put(u, true);
                }
            }

            if (StringUtils.isNotBlank(mark.getTableName()) && StringUtils.isNotBlank(
                    mark.getStatusColumn())) {
                List<QueryItem> items = dataMarkService.listMarkStatusByTable(requestUuids,
                        mark.getTableName(),
                        mark.getStatusColumn());
                for (QueryItem qi : items) {
                    result.put(qi.getString("uuid"), 1 == qi.getInt("status"));
                }
            }

        } catch (Exception e) {
            logger.error("数据标记门面服务异常：", e);
            throw new RuntimeException(e);
        }
        return result;
    }


    @Override
    public Select2QueryData loadMarkRelaEntityData(Select2QueryInfo queryInfo) throws Exception {
        Map<String, Class<?>> entityClasses = ClassUtils.getEntityClasses();
        Select2QueryData result = new Select2QueryData();
        Iterator iterator = entityClasses.keySet().iterator();

        while (iterator.hasNext()) {
            String simpleName = (String) iterator.next();
            Class clazz = entityClasses.get(simpleName);

            try {
                if (Class.forName(
                        queryInfo.getRequest().getParameter(
                                "relaEntityClass").toString()).isAssignableFrom(
                        clazz)) {

                    result.addResultData(
                            new Select2DataBean(((Class) entityClasses.get(simpleName)).getName(),
                                    StringUtils.capitalize(simpleName)));
                }
            } catch (Exception e) {
                logger.error("获取标记关联实体异常：", e);
                throw new RuntimeException(e);

            }

        }

        return result;
    }

    @Override
    public Select2QueryData loadDataMarkType(Select2QueryInfo queryInfo) throws Exception {
        Select2QueryData result = new Select2QueryData();
        result.addResults(
                Lists.<Select2DataBean>newArrayList(
                        new Select2DataBean("entity", "标记数据关系实体"),
                        new Select2DataBean("table", "数据库表")));
        return result;
    }

    @Override
    public List<? extends DataMarkEntity> listByDataUuid(String dataUuid,
                                                         Class<? extends DataMarkEntity> markClass, PagingInfo page) {

        return dataMarkService.listByDataUuidAndClass(dataUuid, markClass, page);
    }
}
