/*
 * @(#)2018年6月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.context.base.BaseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月4日.1	zhulh		2018年6月4日		Create
 * </pre>
 * @date 2018年6月4日
 */
public class FlowShareDatas extends BaseObject {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3590327459892503747L;

    private List<FlowShareData> shareDatas = new ArrayList<FlowShareData>();

    /**
     * @return the shareDatas
     */
    public List<FlowShareData> getShareDatas() {
        return shareDatas;
    }

    /**
     * @param shareDatas 要设置的shareDatas
     */
    public void setShareDatas(List<FlowShareData> shareDatas) {
        this.shareDatas = shareDatas;
    }

}
