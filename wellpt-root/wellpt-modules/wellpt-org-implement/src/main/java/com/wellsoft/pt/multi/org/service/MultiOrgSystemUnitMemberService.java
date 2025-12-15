/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgSystemUnitMemberDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnitMember;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
public interface MultiOrgSystemUnitMemberService extends
        JpaService<MultiOrgSystemUnitMember, MultiOrgSystemUnitMemberDao, String> {
    /**
     * 如何描述该方法
     *
     * @param unitId
     * @param isRecursion
     * @return
     */
    List<MultiOrgSystemUnitMember> queryMemberListOfUnit(String unitId, boolean isRecursion);

    /**
     * 如何描述该方法
     *
     * @param unitId
     * @param memberId
     * @param isRecursion
     * @return
     */
    boolean isMember(String unitId, String memberId, boolean isRecursion);

    /**
     * 如何描述该方法
     *
     * @param unitId
     * @return
     */
    boolean deleteMemberListOfUnit(String unitId);
}
