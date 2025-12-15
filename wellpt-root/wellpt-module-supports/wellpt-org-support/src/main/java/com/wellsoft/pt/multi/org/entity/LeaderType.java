/*
 * @(#)2017年12月26日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年12月26日.1	zyguo		2017年12月26日		Create
 * </pre>
 * @date 2017年12月26日
 */
public interface LeaderType {
    public static int TYPE_BOSS = 0; // 负责人
    public static int TYPE_BRANCHED_LEADER = 1; // 分管领导
    public static int TYPE_MANAGER = 2; // 管理员
}
