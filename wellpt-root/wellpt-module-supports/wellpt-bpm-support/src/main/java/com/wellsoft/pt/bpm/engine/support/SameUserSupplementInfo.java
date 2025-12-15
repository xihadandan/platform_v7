/*
 * @(#)12/27/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import org.apache.commons.collections.MapUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/27/24.1	    zhulh		12/27/24		    Create
 * </pre>
 * @date 12/27/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SameUserSupplementInfo extends BaseObject {
    private static final long serialVersionUID = -346165592332192102L;

    // <forkTaskInstUuid, supplementTaskUuids>
    private Map<String, List<String>> supplementTaskUuidMap = Maps.newHashMap();

    /**
     * @return the supplementTaskUuidMap
     */
    public Map<String, List<String>> getSupplementTaskUuidMap() {
        return supplementTaskUuidMap;
    }

    /**
     * @param supplementTaskUuidMap 要设置的supplementTaskUuidMap
     */
    public void setSupplementTaskUuidMap(Map<String, List<String>> supplementTaskUuidMap) {
        this.supplementTaskUuidMap = supplementTaskUuidMap;
    }

    @JsonIgnore
    public List<String> getSupplementTaskUuids() {
        if (MapUtils.isEmpty(supplementTaskUuidMap)) {
            return Collections.emptyList();
        }
        return supplementTaskUuidMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @JsonIgnore
    public void addSupplementTaskUuid(String preTaskInstUuid, String supplementTaskUuid) {
        if (supplementTaskUuidMap == null) {
            supplementTaskUuidMap = Maps.newHashMap();
        }
        List<String> supplementTaskUuids = supplementTaskUuidMap.get(preTaskInstUuid);
        if (supplementTaskUuids == null) {
            supplementTaskUuids = Lists.newArrayList();
            supplementTaskUuidMap.put(preTaskInstUuid, supplementTaskUuids);
        }
        supplementTaskUuids.add(supplementTaskUuid);
    }

}
