package com.wellsoft.pt.multi.org.support.dataprovider;

import com.wellsoft.pt.multi.org.bean.OrgTreeDialLogAsynParms;
import com.wellsoft.pt.multi.org.bean.UserNodePy;

/**
 * @author yt
 * @title: OrgTreeAllUserProvider
 * @date 2020/6/17 3:18 下午
 */
public interface OrgTreeAllUserProvider extends OrgTreeDialogType {

    /**
     * 查询组织节点下 所有用户
     *
     * @return
     */
    UserNodePy allUserSearch(OrgTreeDialLogAsynParms parms);

}
