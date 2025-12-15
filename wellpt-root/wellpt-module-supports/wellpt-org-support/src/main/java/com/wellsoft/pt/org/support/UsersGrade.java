/*
 * @(#)2016年7月28日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.support;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author huanglc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月28日.1	huanglc		2016年7月28日		Create
 * </pre>
 * @date 2016年7月28日
 */
public class UsersGrade {
    // 用户ID列表
    private List<String> userIds = Collections.emptyList();
    // 级别-用户ID列表
    private Map<String, Set<String>> gradeMap = Collections.emptyMap();

    /**
     * @return the userIds
     */
    public List<String> getUserIds() {
        return userIds;
    }

    /**
     * @param userIds 要设置的userIds
     */
    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    /**
     * @return the gradeMap
     */
    public Map<String, Set<String>> getGradeMap() {
        return gradeMap;
    }

    /**
     * @param gradeMap 要设置的gradeMap
     */
    public void setGradeMap(Map<String, Set<String>> gradeMap) {
        this.gradeMap = gradeMap;
    }

}
