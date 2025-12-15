package com.wellsoft.pt.dms.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.dms.entity.DataClassifyRelaEntity;
import com.wellsoft.pt.dms.entity.DmsDataClassifyRelaEntity;
import com.wellsoft.pt.dms.facade.service.DmsDataClassifyFacadeService;
import com.wellsoft.pt.dms.service.DmsDataClassifyRelaService;
import com.wellsoft.pt.jpa.util.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:数据分类门面服务
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
public class DmsDataClassifyFacadeServiceImpl extends AbstractApiFacade implements
        DmsDataClassifyFacadeService {

    @Resource
    DmsDataClassifyRelaService dmsDataClassifyRelaService;


    @Override
    public void addClassifyRela(List<String> dataUuids, String classifyUuid,
                                String relaEntityClass, String dataRelaType) {
        try {
            dmsDataClassifyRelaService.saveClassifyRela(
                    this.convert2RealEntityInst(relaEntityClass, dataUuids, classifyUuid),
                    dataRelaType);
        } catch (Exception e) {
            logger.error("分类标记异常：", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void addClassifyRelaByTable(List<String> dataUuids, String classifyUuid, String table,
                                       String dataRelaType) {
        try {
            dmsDataClassifyRelaService.saveClassifyRelaByTable(table, dataUuids, classifyUuid,
                    dataRelaType);
        } catch (Exception e) {
            logger.error("分类标记异常：", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteClassifyRela(List<String> dataUuids, String classifyUuid,
                                   String relaEntityClass) {
        try {
            dmsDataClassifyRelaService.deleteClassifyRela(
                    this.convert2RealEntityInst(relaEntityClass, dataUuids, classifyUuid));
        } catch (Exception e) {
            logger.error("分类标记异常：", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TreeNode> listClassifyNodes(String tableName, String uniqueColumn,
                                            String parentColumn, String displayColumn,
                                            String parentColumnValue) {
        List<QueryItem> queryItemList = dmsDataClassifyRelaService.listClassifyItems(tableName,
                uniqueColumn, parentColumn, displayColumn, parentColumnValue);
        List<TreeNode> treeNodeList = Lists.newArrayList();
        for (QueryItem qi : queryItemList) {
            TreeNode treeNode = new TreeNode(qi.getString("uuid"), qi.getString("name"), null);
            if (StringUtils.isNotBlank(parentColumn)) {
                treeNode.getChildren().addAll(
                        this.cascadeClassifyNodes(tableName, uniqueColumn, parentColumn,
                                displayColumn,
                                qi.getString("uuid"), new AtomicInteger(1)));//递归遍历获取子节点
            }
            treeNodeList.add(treeNode);
        }
        return treeNodeList;
    }

    private List<TreeNode> cascadeClassifyNodes(String tableName, String uniqueColumn,
                                                String parentColumn, String displayColumn,
                                                String parentColumnValue,
                                                AtomicInteger cascadeLevel) {
        if (cascadeLevel.incrementAndGet() == 500) {
            throw new RuntimeException("超过分类递归最大上限次数:500");
        }
        List<QueryItem> queryItemList = dmsDataClassifyRelaService.listClassifyItems(tableName,
                uniqueColumn, parentColumn, displayColumn, parentColumnValue);
        List<TreeNode> treeNodeList = Lists.newArrayList();
        for (QueryItem qi : queryItemList) {
            TreeNode treeNode = new TreeNode(qi.getString("uuid"), qi.getString("name"), null);
            treeNode.getChildren().addAll(
                    this.cascadeClassifyNodes(tableName, uniqueColumn, parentColumn, displayColumn,
                            qi.getString("uuid"), cascadeLevel));
            treeNodeList.add(treeNode);
        }
        return treeNodeList;
    }

    private List<? extends DataClassifyRelaEntity> convert2RealEntityInst(String relaEntityClass,
                                                                          List<String> dataUuids,
                                                                          String classifyUuid) throws Exception {
        List<DataClassifyRelaEntity> entityList = Lists.newArrayList();
        for (String dataUuid : dataUuids) {
            DataClassifyRelaEntity relaEntity = StringUtils.isNotBlank(
                    relaEntityClass) ? (DataClassifyRelaEntity) ClassUtils.getEntityClasses().get(
                    StringUtils.uncapitalize(
                            relaEntityClass)).newInstance() : new DmsDataClassifyRelaEntity();
            relaEntity.setDataUuid(dataUuid);
            relaEntity.setClassifyUuid(classifyUuid);
            entityList.add(relaEntity);
        }
        return entityList;
    }
}
