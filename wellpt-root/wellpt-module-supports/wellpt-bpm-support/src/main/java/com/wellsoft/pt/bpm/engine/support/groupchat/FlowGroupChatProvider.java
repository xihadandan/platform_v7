/*
 * @(#)4/11/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support.groupchat;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 流程群聊提供者接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/11/25.1	    zhulh		4/11/25		    Create
 * </pre>
 * @date 4/11/25
 */
public interface FlowGroupChatProvider {

    ProviderInfo getProviderInfo();

    String startGroupChat(StartGroupChat startGroupChat);

    static class ProviderInfo extends BaseObject {
        private String name;
        private String id;
        private boolean enabled;
        private String description;

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
         * @return the enabled
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * @param enabled 要设置的enabled
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description 要设置的description
         */
        public void setDescription(String description) {
            this.description = description;
        }
    }
}
