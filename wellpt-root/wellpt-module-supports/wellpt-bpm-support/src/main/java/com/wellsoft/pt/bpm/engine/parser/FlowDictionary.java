/*
 * @(#)3/21/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.parser;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;

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
 * 3/21/24.1	zhulh		3/21/24		Create
 * </pre>
 * @date 3/21/24
 */
public class FlowDictionary extends BaseObject {
    private static final long serialVersionUID = -4320522541607258072L;

    private List<Category> categorys = Lists.newArrayList();

    private List<Right> startRights = Lists.newArrayList();
    private List<Right> todoRights = Lists.newArrayList();
    private List<Right> doneRights = Lists.newArrayList();
    private List<Right> monitorRights = Lists.newArrayList();
    private List<Right> adminRights = Lists.newArrayList();

    private List<DictionaryItem> buttons = Lists.newArrayList();

    private List<DictionaryItem> formats = Lists.newArrayList();

    private List<Form> forms = Lists.newArrayList();

    /**
     * @return the categorys
     */
    public List<Category> getCategorys() {
        return categorys;
    }

    /**
     * @param categorys 要设置的categorys
     */
    public void setCategorys(List<Category> categorys) {
        this.categorys = categorys;
    }

    /**
     * @return the startRights
     */
    public List<Right> getStartRights() {
        return startRights;
    }

    /**
     * @param startRights 要设置的startRights
     */
    public void setStartRights(List<Right> startRights) {
        this.startRights = startRights;
    }

    /**
     * @return the todoRights
     */
    public List<Right> getTodoRights() {
        return todoRights;
    }

    /**
     * @param todoRights 要设置的todoRights
     */
    public void setTodoRights(List<Right> todoRights) {
        this.todoRights = todoRights;
    }

    /**
     * @return the doneRights
     */
    public List<Right> getDoneRights() {
        return doneRights;
    }

    /**
     * @param doneRights 要设置的doneRights
     */
    public void setDoneRights(List<Right> doneRights) {
        this.doneRights = doneRights;
    }

    /**
     * @return the monitorRights
     */
    public List<Right> getMonitorRights() {
        return monitorRights;
    }

    /**
     * @param monitorRights 要设置的monitorRights
     */
    public void setMonitorRights(List<Right> monitorRights) {
        this.monitorRights = monitorRights;
    }

    /**
     * @return the adminRights
     */
    public List<Right> getAdminRights() {
        return adminRights;
    }

    /**
     * @param adminRights 要设置的adminRights
     */
    public void setAdminRights(List<Right> adminRights) {
        this.adminRights = adminRights;
    }

    /**
     * @return the buttons
     */
    public List<DictionaryItem> getButtons() {
        return buttons;
    }

    /**
     * @param buttons 要设置的buttons
     */
    public void setButtons(List<DictionaryItem> buttons) {
        this.buttons = buttons;
    }

    /**
     * @return the formats
     */
    public List<DictionaryItem> getFormats() {
        return formats;
    }

    /**
     * @param formats 要设置的formats
     */
    public void setFormats(List<DictionaryItem> formats) {
        this.formats = formats;
    }

    /**
     * @return the forms
     */
    public List<Form> getForms() {
        return forms;
    }

    /**
     * @param forms 要设置的forms
     */
    public void setForms(List<Form> forms) {
        this.forms = forms;
    }

    public static class Category extends BaseObject {
        private static final long serialVersionUID = 2795095539636360606L;

        private String name;
        private String uuid;
        private String parent;

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
         * @return the uuid
         */
        public String getUuid() {
            return uuid;
        }

        /**
         * @param uuid 要设置的uuid
         */
        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        /**
         * @return the parent
         */
        public String getParent() {
            return parent;
        }

        /**
         * @param parent 要设置的parent
         */
        public void setParent(String parent) {
            this.parent = parent;
        }
    }

    public static class Right extends DictionaryItem {
        private static final long serialVersionUID = -8483733264428517406L;

        private String isDefault;

        /**
         * @return the isDefault
         */
        public String getIsDefault() {
            return isDefault;
        }

        /**
         * @param isDefault 要设置的isDefault
         */
        public void setIsDefault(String isDefault) {
            this.isDefault = isDefault;
        }
    }

    public static class DictionaryItem extends BaseObject {
        private static final long serialVersionUID = 9086828192656676861L;

        private String name;
        private String code;
        private String value;

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
         * @return the code
         */
        public String getCode() {
            return code;
        }

        /**
         * @param code 要设置的code
         */
        public void setCode(String code) {
            this.code = code;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value 要设置的value
         */
        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Form extends BaseObject {
        private static final long serialVersionUID = -8480620126364554241L;

        private String name;
        private String uuid;

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
         * @return the uuid
         */
        public String getUuid() {
            return uuid;
        }

        /**
         * @param uuid 要设置的uuid
         */
        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }
}
