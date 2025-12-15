package com.wellsoft.pt.multi.org.support.dataprovider;

import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialLogAsynParms;

import java.util.List;

/**
 * @author yt
 * @title: OrgTreeDialogDataAsynProvider
 * @date 2020/6/9 2:05 下午
 */
public interface OrgTreeDialogProvider extends OrgTreeDialogType {

    /**
     * 查询 子节点
     *
     * @return
     */
    List<OrgNode> children(OrgTreeDialLogAsynParms parms);

    /**
     * 查询  全部节点
     *
     * @return
     */
    List<OrgNode> full(OrgTreeDialLogAsynParms parms);

    /**
     * 搜索
     *
     * @return
     */
    List<OrgNode> search(OrgTreeDialLogAsynParms parms);


}
