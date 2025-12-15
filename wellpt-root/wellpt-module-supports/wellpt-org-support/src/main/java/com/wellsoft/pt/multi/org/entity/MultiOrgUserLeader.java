/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.CollectionUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 组织节点基本类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-11-21.1	zyguo		2017-11-21		Create
 * </pre>
 * @date 2017-11-21
 */
@Entity
@Table(name = "MULTI_ORG_USER_LEADER")
@DynamicUpdate
@DynamicInsert
public class MultiOrgUserLeader extends IdEntity implements LeaderType {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1271163672033319245L;
    // 对应的元素UUID
    private String userId;
    // 领导类型， 0：负责人，1：分管领导，2：管理员，3：汇报对象
    private Integer leaderType;
    // 领导对应的对象ID
    private String leaderObjId;
    // 领导对应的对象名称
    private String leaderObjName;
    // 领导对应的对象类型，参考IdPrev 里面的说明
    private String leaderObjType;

    public static String managetList2ids(List<MultiOrgUserLeader> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            ArrayList<String> ids = new ArrayList<String>();
            for (MultiOrgUserLeader leader : list) {
                ids.add(leader.getLeaderObjId());
            }
            return StringUtils.join(ids, ";");
        }
    }

    public static String managetList2names(List<MultiOrgUserLeader> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            ArrayList<String> ids = new ArrayList<String>();
            for (MultiOrgUserLeader leader : list) {
                ids.add(leader.getLeaderObjName());
            }
            return StringUtils.join(ids, ";");
        }
    }

    /**
     * @return the leaderType
     */
    public Integer getLeaderType() {
        return leaderType;
    }

    /**
     * @param leaderType 要设置的leaderType
     */
    public void setLeaderType(Integer leaderType) {
        this.leaderType = leaderType;
    }

    /**
     * @return the leaderObjId
     */
    public String getLeaderObjId() {
        return leaderObjId;
    }

    /**
     * @param leaderObjId 要设置的leaderObjId
     */
    public void setLeaderObjId(String leaderObjId) {
        this.leaderObjId = leaderObjId;
    }

    /**
     * @return the leaderObjName
     */
    public String getLeaderObjName() {
        return leaderObjName;
    }

    /**
     * @param leaderObjName 要设置的leaderObjName
     */
    public void setLeaderObjName(String leaderObjName) {
        this.leaderObjName = leaderObjName;
    }

    /**
     * @return the leaderObjType
     */
    public String getLeaderObjType() {
        return leaderObjType;
    }

    /**
     * @param leaderObjType 要设置的leaderObjType
     */
    public void setLeaderObjType(String leaderObjType) {
        this.leaderObjType = leaderObjType;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

}
