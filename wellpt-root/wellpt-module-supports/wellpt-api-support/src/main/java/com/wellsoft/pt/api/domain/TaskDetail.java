/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.domain;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public class TaskDetail extends Task {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -751364255385992401L;

    // 表单数据
    private Map<String, Object> formData;

    // 操作按钮
    private List<Button> buttons;

    /**
     * @return the formData
     */
    public Map<String, Object> getFormData() {
        return formData;
    }

    /**
     * @param formData 要设置的formData
     */
    public void setFormData(Map<String, Object> formData) {
        this.formData = formData;
    }

    /**
     * @return the buttons
     */
    public List<Button> getButtons() {
        return buttons;
    }

    /**
     * @param buttons 要设置的buttons
     */
    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

    public static class Button extends BaseObject {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -6051007991602994625L;

        private String id;

        private String name;

        @JsonIgnore
        private int order;

        /**
         *
         */
        public Button() {
            super();
        }

        /**
         * @param id
         * @param name
         */
        public Button(String id, String name, int order) {
            super();
            this.id = id;
            this.name = name;
            this.order = order;
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
         * @return the order
         */
        public int getOrder() {
            return order;
        }

        /**
         * @param order 要设置的order
         */
        public void setOrder(int order) {
            this.order = order;
        }

    }

}
