package com.wellsoft.pt.repository.entity.jcr;

import java.io.Serializable;

/**
 * @author pavila
 */
public class Repository implements Serializable {
    public static final String OKM = "well";
    public static final String OKM_URI = "http://www.openkm.org/1.0";
    public static final String ROOT = "well:root";
    public static final String TRASH = "well:trash";
    public static final String TEMPLATES = "well:templates";
    public static final String THESAURUS = "well:thesaurus";
    public static final String CATEGORIES = "well:categories";
    public static final String SYS_CONFIG = "well:config";
    public static final String SYS_CONFIG_TYPE = "well:sysConfig";
    public static final String SYS_CONFIG_UUID = "well:uuid";
    public static final String SYS_CONFIG_VERSION = "well:version";
    public static final String PERSONAL = "well:personal";
    public static final String MAIL = "well:mail";
    public static final String USER_CONFIG = "well:config";
    public static final String USER_CONFIG_TYPE = "well:userConfig";
    public static final String LOCK_TOKENS = "well:lockTokens";
    private static final long serialVersionUID = -6920884124466924375L;
    private static String uuid;
    private static String updateMsg;
    private String id;
    private String name;
    private String description;

    /**
     * Get generated UUID
     *
     * @return The UUID generated
     */
    public static String getUuid() {
        return uuid;
    }

    /**
     * Set the generated UUI
     *
     * @param uuid
     */
    public static void setUuid(String uuid) {
        Repository.uuid = uuid;
    }

    /**
     * Get the retrieved update message
     *
     * @param updateMsg
     */
    public static String getUpdateMsg() {
        return updateMsg;
    }

    /**
     * Set retrieved update message
     *
     * @return The update message
     */
    public static void setUpdateMsg(String updateMsg) {
        Repository.updateMsg = updateMsg;
    }

    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set repository name.
     *
     * @param name The respository name.
     */
    public void setName(String name) {
        this.name = name;
    }
}