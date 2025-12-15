package com.wellsoft.pt.dms.facade.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.dms.facade.service.DmsFolderConfigurationFacadeService;
import com.wellsoft.pt.dms.service.DmsFolderConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class DmsFolderConfigurationFacadeServiceImpl extends AbstractApiFacade
        implements DmsFolderConfigurationFacadeService {
    @Autowired
    DmsFolderConfigurationService dmsFolderConfigurationService;

    @Override
    public List<TreeNode> getPrintTemplateTreeByUser() {
        return dmsFolderConfigurationService.getPrintTemplateTreeByUser();
    }
}
