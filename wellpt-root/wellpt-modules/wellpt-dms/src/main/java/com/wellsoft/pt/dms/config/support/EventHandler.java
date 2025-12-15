/*
 * @(#)Jan 22, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 22, 2018.1	zhulh		Jan 22, 2018		Create
 * </pre>
 * @date Jan 22, 2018
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventHandler extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3029762297960421247L;

    private String id;
    private String name;
    private Integer type;
    private String path;
    private String hashType;
    private String hash;

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
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path 要设置的path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the hashType
     */
    public String getHashType() {
        return hashType;
    }

    /**
     * @param hashType 要设置的hashType
     */
    public void setHashType(String hashType) {
        this.hashType = hashType;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash 要设置的hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

}
