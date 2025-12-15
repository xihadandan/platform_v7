package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;

import java.util.List;

/**
 * Description: 数据分类门面服务
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
public interface DmsDataClassifyFacadeService extends BaseService {

    /**
     * 增加数据分类关系
     *
     * @param dataUuids       数据uuid集合
     * @param classifyUuid    分类uuid
     * @param relaEntityClass 数据分类关系实体类名
     * @param dataRelaType    数据与分类的关系：1 有且一比一  n 有且一比N
     */
    void addClassifyRela(List<String> dataUuids, String classifyUuid,
                         String relaEntityClass, String dataRelaType);

    void addClassifyRelaByTable(List<String> dataUuids, String classifyUuid,
                                String table, String dataRelaType);

    /**
     * 删除数据分类关系
     *
     * @param dataUuids
     * @param classifyUuid
     * @param relaEntityClass
     */
    void deleteClassifyRela(List<String> dataUuids, String classifyUuid,
                            String relaEntityClass);

    /**
     * 查询分类节点数据
     *
     * @param tableName         分类表
     * @param uniqueColumn      分类唯一标识字段
     * @param parentColumn      分类父级标识字段
     * @param diplayColumn      分类展示字段
     * @param parentColumnValue 分类父级字段值
     * @return
     */
    List<TreeNode> listClassifyNodes(String tableName,
                                     String uniqueColumn,
                                     String parentColumn,
                                     String diplayColumn, String parentColumnValue);
}
