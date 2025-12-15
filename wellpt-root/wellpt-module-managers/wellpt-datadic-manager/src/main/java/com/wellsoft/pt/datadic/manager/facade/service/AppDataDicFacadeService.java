package com.wellsoft.pt.datadic.manager.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/11    chenq		2019/6/11		Create
 * </pre>
 */
public interface AppDataDicFacadeService extends Facade {

    TreeNode loadAppDataDicNodes(String uuid, String moduleId, Boolean includeRef);

    TreeNode loadAppDataDicNodesByPiUuid(String uuid, String piUUid, Boolean includeRef);
}
