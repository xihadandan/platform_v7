/*
 * @(#)2016年6月7日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
 * 2016年6月7日.1	zhulh		2016年6月7日		Create
 * </pre>
 * @date 2016年6月7日
 */
public class WmWebmailOperation {

    public static final String SAVE = "save";
    public static final String SEND = "send";
    public static final String TIMING_SEND = "timing_send";
    public static final String TRANSFER = "transfer";
    public static final String REPLY = "reply";
    public static final String REPLY_ALL = "reply_all";
    public static final String DELETE = "delete";
    public static final String DELETE_PHYSICA = "delete_physical";
    public static final String VIEWS_TATUS = "view_status";
    public static final String REVOKE = "revoke";
    public static final String EDIT_AGAIN = "edit_again";
    public static final Button BTN_SAVE;
    public static final Button BTN_SEND;
    public static final Button BTN_TRANSFER;
    public static final Button BTN_REPLY;
    public static final Button BTN_REPLY_ALL;
    public static final Button BTN_DELETE;
    public static final Button BTN_DELETE_PHYSICA;
    public static final Button BTN_VIEW_STATUS;
    public static final Button BTN_REVOKE;
    public static final Button BTN_EDIT_AGAIN_STATUS;
    public static final Button BTN_TIMING_SEND;
    private static final String BTN_PREFIX = "btn_";
    private static final Map<String, String> optName = new HashMap<String, String>();

    static {
        optName.put(SAVE, "存草稿");
        optName.put(SEND, "发送");
        optName.put(TIMING_SEND, "定时发送");
        optName.put(TRANSFER, "转发");
        optName.put(REPLY, "回复");
        optName.put(REPLY_ALL, "回复全部");
        optName.put(DELETE, "删除");
        optName.put(DELETE_PHYSICA, "彻底删除");
        optName.put(VIEWS_TATUS, "投递状态");
        optName.put(REVOKE, "撤回");
        optName.put(EDIT_AGAIN, "再次编辑");
        BTN_SAVE = new Button(BTN_PREFIX + SAVE, "B009002001", optName.get(SAVE));
        BTN_SEND = new Button(BTN_PREFIX + SEND, "B009002002", optName.get(SEND));
        BTN_TRANSFER = new Button(BTN_PREFIX + TRANSFER, "B009002003", optName.get(TRANSFER));
        BTN_REPLY = new Button(BTN_PREFIX + REPLY, "B009002004", optName.get(REPLY));
        BTN_REPLY_ALL = new Button(BTN_PREFIX + REPLY_ALL, "B009002005", optName.get(REPLY_ALL));
        BTN_DELETE = new Button(BTN_PREFIX + DELETE, "B009002006", optName.get(DELETE));
        BTN_DELETE_PHYSICA = new Button(BTN_PREFIX + DELETE_PHYSICA, "B009002007", optName.get(DELETE_PHYSICA));
        BTN_VIEW_STATUS = new Button(BTN_PREFIX + VIEWS_TATUS, "B009002008", optName.get(VIEWS_TATUS));
        BTN_REVOKE = new Button(BTN_PREFIX + REVOKE, "B009002009", optName.get(REVOKE));
        BTN_EDIT_AGAIN_STATUS = new Button(BTN_PREFIX + EDIT_AGAIN, "B0090020010", optName.get(EDIT_AGAIN));
        BTN_TIMING_SEND = new Button(BTN_PREFIX + TIMING_SEND, "B0090020011", optName.get(TIMING_SEND));
    }

    public static final String getName(String opt) {
        return optName.get(opt);
    }

    /**
     * @param userId
     * @return
     */
    public static List<Button> getNewBtns(String userId) {
        List<Button> btns = new ArrayList<WmWebmailOperation.Button>();
        btns.add(BTN_SAVE);
        btns.add(BTN_SEND);
        btns.add(BTN_TIMING_SEND);
        return btns;
    }

    /**
     * @param mailboxUuid
     * @return
     */
    public static List<Button> getDraftBtns(String userId, String mailboxUuid) {
        List<Button> btns = new ArrayList<WmWebmailOperation.Button>();
        btns.add(BTN_SAVE);
        btns.add(BTN_SEND);
        btns.add(BTN_DELETE);
        return btns;
    }

    public static List<Button> getTimingSendBtns() {
        List<Button> btns = new ArrayList<WmWebmailOperation.Button>();
        btns.add(BTN_DELETE);
        return btns;
    }

    /**
     * @param userId
     * @param mailboxUuid
     * @return
     */
    public static List<Button> getInboxBtns(String userId, String mailboxUuid) {
        List<Button> btns = new ArrayList<WmWebmailOperation.Button>();
        btns.add(BTN_TRANSFER);
        btns.add(BTN_REPLY);
        btns.add(BTN_REPLY_ALL);
        btns.add(BTN_DELETE);
        return btns;
    }

    /**
     * 如何描述该方法
     *
     * @param userId
     * @param mailboxUuid
     * @return
     */
    public static List<Button> getOutboxBtns(String userId, String mailboxUuid) {
        List<Button> btns = new ArrayList<WmWebmailOperation.Button>();
        btns.add(BTN_TRANSFER);
        btns.add(BTN_REPLY);
        btns.add(BTN_REPLY_ALL);
        btns.add(BTN_DELETE);
        return btns;
    }

    public static class Button implements Serializable {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 3940837581744945671L;
        private String id;
        private String name;
        private String title;

        /**
         * @param id
         * @param name
         * @param title
         */
        public Button(String id, String name, String title) {
            super();
            this.id = id;
            this.name = name;
            this.title = title;
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
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title 要设置的title
         */
        public void setTitle(String title) {
            this.title = title;
        }

    }

}
