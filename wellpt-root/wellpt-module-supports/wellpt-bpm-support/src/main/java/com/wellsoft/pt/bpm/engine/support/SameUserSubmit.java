/*
 * @(#)2015-3-10 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-3-10.1	zhulh		2015-3-10		Create
 * </pre>
 * @date 2015-3-10
 */
public class SameUserSubmit {

    // 自动提交，让办理人确认是否继承上一环节意见
    public static final String CONFIRM_SUBMIT = "0";

    // 自动提交，且自动继承意见
    public static final String AUTO_SUBMIT = "1";

    // 不自动提交并关闭页面
    public static final String NO_ACTION = "2";

    // 不自动提交并刷新页面
    public static final String NO_ACTION_REFRESH = "3";

}
