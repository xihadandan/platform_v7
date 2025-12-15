package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;

import java.util.List;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2022/3/8.1	    zenghw		2022/3/8		    Create
 * </pre>
 * @date 2022/3/8
 */
public interface DmsFolderConfigurationFacadeService extends Facade {

    /**
     * 根据用户获取对应的套打模板树
     * 超管用户：可见超管和全部系统单位定义的打印模板
     * 单位管理员：可见超管和当前系统单位中创建的打印模板
     *
     * @return
     */
    public List<TreeNode> getPrintTemplateTreeByUser();
}
