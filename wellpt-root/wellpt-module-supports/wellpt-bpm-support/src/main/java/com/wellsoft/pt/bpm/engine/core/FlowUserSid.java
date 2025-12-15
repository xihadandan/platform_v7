/*
 * @(#)2018年9月11日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.support.SidGranularity;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月11日.1	zhulh		2018年9月11日		Create
 * </pre>
 * @date 2018年9月11日
 */
public class FlowUserSid extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -38477218587520562L;

    private String id;

    private String name;

    private Object data;

    private String granularity;

    private List<OrgUserJobDto> orgUserJobDtos;

    /**
     * @param id
     * @param name
     * @param granularity
     */
    public FlowUserSid(String id, String name, Object data, String granularity) {
        super();
        this.id = id;
        this.name = name;
        this.data = data;
        this.granularity = granularity;
    }

    /**
     * @param id
     * @param name
     * @param granularity
     */
    public FlowUserSid(String id, String name, String granularity) {
        super();
        this.id = id;
        this.name = name;
        this.granularity = granularity;
    }

    /**
     * @param id
     * @param name
     */
    public FlowUserSid(String id, String name) {
        super();
        this.id = id;
        this.name = name;
        setGranularityWithId();
    }

    /**
     * @param id
     * @param name
     */
    public FlowUserSid(String id, String name, List<OrgUserJobDto> orgUserJobDtos) {
        super();
        this.id = id;
        this.name = name;
        this.orgUserJobDtos = orgUserJobDtos;
        setGranularityWithId();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * @return the granularity
     */
    public String getGranularity() {
        return granularity;
    }

    /**
     * @param granularity 要设置的granularity
     */
    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    /**
     *
     */
    private void setGranularityWithId() {
        if (StringUtils.startsWith(id, IdPrefix.USER.getValue())) {
            setGranularity(SidGranularity.USER);
        } else if (StringUtils.startsWith(id, IdPrefix.JOB.getValue())) {
            setGranularity(SidGranularity.JOB);
        } else if (StringUtils.startsWith(id, IdPrefix.DEPARTMENT.getValue())) {
            setGranularity(SidGranularity.DEPARTMENT);
        } else if (StringUtils.startsWith(id, IdPrefix.SYSTEM_UNIT.getValue())) {
            setGranularity(SidGranularity.SYSTEM_UNIT);
        } else if (StringUtils.startsWith(id, IdPrefix.GROUP.getValue())) {
            setGranularity(SidGranularity.GROUP);
        } else {
            setGranularity(SidGranularity.ACTIVITY);
        }
    }

    /**
     * @return the orgUserJobDtos
     */
    public List<OrgUserJobDto> getOrgUserJobDtos() {
        return orgUserJobDtos;
    }

    /**
     * @param orgUserJobDtos 要设置的orgUserJobDtos
     */
    public void setOrgUserJobDtos(List<OrgUserJobDto> orgUserJobDtos) {
        this.orgUserJobDtos = orgUserJobDtos;
    }

    /**
     * @param orgUserJobDtos
     */
    public void addOrgUserJobDtos(Collection<OrgUserJobDto> orgUserJobDtos) {
        if (this.orgUserJobDtos == null) {
            this.orgUserJobDtos = Lists.newArrayList(orgUserJobDtos);
        } else {
            orgUserJobDtos.forEach(orgUserJobDto -> {
                if (!this.orgUserJobDtos.contains(orgUserJobDto)) {
                    this.orgUserJobDtos.add(orgUserJobDto);
                }
            });
        }
    }

    /**
     * @return
     */
    public String getIdentityId() {
        if (CollectionUtils.isEmpty(orgUserJobDtos)) {
            return StringUtils.EMPTY;
        }
        return orgUserJobDtos.stream().map(OrgUserJobDto::getJobId).collect(Collectors.joining(Separator.SEMICOLON.getValue()));
    }

    /**
     * @return
     */
    public String getIdentityIdPath() {
        if (CollectionUtils.isEmpty(orgUserJobDtos)) {
            return StringUtils.EMPTY;
        }
        return orgUserJobDtos.stream().map(OrgUserJobDto::getJobIdPath).collect(Collectors.joining(Separator.SEMICOLON.getValue()));
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FlowUserSid [id=" + id + ", name=" + name + "]";
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FlowUserSid other = (FlowUserSid) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
