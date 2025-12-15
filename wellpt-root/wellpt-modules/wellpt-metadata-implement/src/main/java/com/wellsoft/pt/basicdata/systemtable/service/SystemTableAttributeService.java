package com.wellsoft.pt.basicdata.systemtable.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.systemtable.dao.SystemTableAttributeDao;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableRelationship;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 系统表数据服务层接口
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-21.1	zhouyq		2013-3-21		Create
 * </pre>
 * @date 2013-3-21
 */

public interface SystemTableAttributeService extends JpaService<SystemTableAttribute, SystemTableAttributeDao, String> {

    /**
     * 系统表数据列表查询
     *
     * @param queryInfo
     * @return
     */
    public JqGridQueryData query(JqGridQueryInfo queryInfo);

    /**
     * 根据表的uuid返回表的所有字段的集合
     *
     * @param tableUuid
     * @return
     */
    public List<SystemTableAttribute> getSystemTableColumns(String tableUuid);

    /**
     * 根据表的uuid返回主表及从表属性的集合
     *
     * @param tableUuid
     * @return
     */
    public List<SystemTableAttribute> getAttributesByrelationship(String tableUuid);

    /**
     * 根据表的uuid返回主表及从表属性的集合(返回TreeNode)
     *
     * @param tableUuid
     * @return
     */
    public List<TreeNode> getAttributesTreeNodeByrelationship(String s, String tableUuid);

    /**
     * 根据传入的表UUID获得主从表属性
     *
     * @param tableUuid
     * @return
     */
    public List<SystemTableRelationship> getAttributesByrelationship2(String tableUuid);

    /**
     * 获得系统表
     *
     * @param tableUuid
     * @return
     */
    public SystemTable getTable(String tableUuid);

    /**
     * @param uuid
     * @return
     */
    public List<SystemTableAttribute> getBySystemTableUuid(String uuid);
}
