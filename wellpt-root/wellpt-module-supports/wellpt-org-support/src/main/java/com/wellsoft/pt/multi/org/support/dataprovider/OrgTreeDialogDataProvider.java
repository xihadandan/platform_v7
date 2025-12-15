/*
 * @(#)2018年3月7日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.support.dataprovider;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialogParams;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月7日.1	zyguo		2018年3月7日		Create
 * </pre>
 * @date 2018年3月7日
 * <p>
 * 该接口废弃 新实现接口
 * @see OrgTreeDialogProvider
 */
@Deprecated
public interface OrgTreeDialogDataProvider extends OrgTreeDialogType {

    List<TreeNode> provideData(OrgTreeDialogParams params);

}
