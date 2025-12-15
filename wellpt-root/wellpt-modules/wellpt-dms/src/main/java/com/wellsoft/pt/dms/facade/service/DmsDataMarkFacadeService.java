package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.dms.bean.MarkDataDto;
import com.wellsoft.pt.dms.entity.DataMarkEntity;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据标记门面服务
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
public interface DmsDataMarkFacadeService extends Facade {

    /**
     * 标记或删除标记数据
     */
    void saveOrDeleteMark(MarkDataDto mark);

    /**
     * 标记数据
     *
     * @param dataMarkEntity
     */
    void saveDataMark(DataMarkEntity dataMarkEntity);

    /**
     * 保存标记数据，列表元素对象可以只设置dataUuid
     *
     * @param dataMarkEntities
     */
    void saveDataMarkList(List<? extends DataMarkEntity> dataMarkEntities);

    /**
     * 移除标记数据
     *
     * @param dataMarkEntity
     */
    void deleteDataMark(DataMarkEntity dataMarkEntity);

    /**
     * 异常标记数据，列表元素对象可以只设置dataUuid
     *
     * @param dataMarkEntities
     */
    void deleteDataMarkList(List<? extends DataMarkEntity> dataMarkEntities);

    /**
     * 标记数据
     *
     * @param dataUuid  数据UUID
     * @param markClass 数据标记关联实体类
     */
    void saveDataMark(String dataUuid, Class<? extends DataMarkEntity> markClass);

    /**
     * 批量标记数据
     *
     * @param dataUuids 数据UUID
     * @param markClass 数据标记关联实体类
     */
    void saveDataMarkByDataUuids(List<String> dataUuids, Class<? extends DataMarkEntity> markClass);

    /**
     * 标记数据
     *
     * @param dataUuid  数据UUID
     * @param markClass 数据标记关联实体类
     */
    void deleteDataMark(String dataUuid, Class<? extends DataMarkEntity> markClass);


    /**
     * 批量异常标记数据
     *
     * @param dataUuids
     * @param markClass
     */
    void deleteDataMarkByDataUuids(List<String> dataUuids,
                                   Class<? extends DataMarkEntity> markClass);

    /**
     * 查询标记数据状态
     *
     * @param mark
     * @return
     */
    Map<String, Boolean> dataMarkResult(MarkDataDto mark);

    /**
     * 加载标记数据的关联实体类
     *
     * @param queryInfo
     * @return
     * @throws Exception
     */
    Select2QueryData loadMarkRelaEntityData(Select2QueryInfo queryInfo) throws Exception;


    /**
     * 加载标记数据的类型
     *
     * @param queryInfo
     * @return
     * @throws Exception
     */
    Select2QueryData loadDataMarkType(Select2QueryInfo queryInfo) throws Exception;


    /**
     * 根据数据UUID，查询状态标记数据集合
     *
     * @param dataUuid
     * @param markClass 标记实体类关系类
     * @param page      可为null
     * @return
     */
    List<? extends DataMarkEntity> listByDataUuid(String dataUuid,
                                                  Class<? extends DataMarkEntity> markClass,
                                                  PagingInfo page);


}
