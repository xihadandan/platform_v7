package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.bean.DmsDataLabelDto;

import java.util.List;

/**
 * Description: 数据标签门面服务
 *
 * @author chenq
 * @date 2018/6/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/11    chenq		2018/6/11		Create
 * </pre>
 */
public interface DmsDataLabelFacadeService extends BaseService {

    /**
     * 获取标签关联数据的实体类
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData loadDataLabelRelaEntityData(Select2QueryInfo queryInfo);


    /**
     * 加载用户的某个模块定义的标签
     *
     * @param moduleId
     * @return
     */
    Select2QueryData loadUserDataLabelDtosByModuleId(String moduleId);


    /**
     * 新建标签并标记
     *
     * @param labelDto
     * @param dataUuid
     */
    void addLabelAndRelaData(DmsDataLabelDto labelDto, List<String> dataUuid);

    void addLabelAndRelaDataEntity(DmsDataLabelDto labelDto, List<String> dataUuid,
                                   String entityClassName) throws Exception;

    void addLabelRelaData(List<String> dataUuid, String labelUuid);


    /**
     * 添加数据与标签的关系实体
     *
     * @param dataUuid
     * @param labelUuid
     * @param entityClassName
     * @throws Exception
     */
    void addLabelRelaEntity(List<String> dataUuid, String labelUuid,
                            String entityClassName) throws Exception;

    /**
     * 移除数据上的所有标签
     *
     * @param dataUuid
     */
    void removeAllLableOfData(List<String> dataUuid);

    /**
     * 移除指定数据的指定标签
     *
     * @param dataUuid
     * @param labelUuid
     */
    void removeLableOfData(String dataUuid, String labelUuid);

    /**
     * 根据标签与数据的关系UUID，移除数据的标签
     *
     * @param relaUuid
     */
    void removeLableOfDataByRelaUuids(List<String> relaUuid);

    /**
     * 根据标签与数据的关系UUID，移除数据的标签
     *
     * @param relaUuid
     * @param entityClassName 标签与数据的关系实体
     */
    void removeLableOfDataByRelaUuidsAndRelaEntityClass(List<String> relaUuid,
                                                        String entityClassName);

    /**
     * 获取数据的被标记的所有标签
     *
     * @param dataUuid
     * @return
     */
    List<DmsDataLabelDto> listLabelsOfData(String dataUuid);

    /**
     * 获取数据的被标记的所有标签
     *
     * @param dataUuid
     * @param entityClassName 标签与数据的关系实体
     * @return
     */
    List<DmsDataLabelDto> listLabelByDataUuidAndEntityClass(String dataUuid,
                                                            String entityClassName);
}
