/*
 * @(#)7/5/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.bpm.engine.form.Button;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
 * 7/5/24.1	zhulh		2012-10-23		Create
 * </pre>
 * @date 7/5/24
 */
public class WorkFlowRights extends BaseObject {
    private static final long serialVersionUID = -9000679318765529963L;

    private String aclRole;

    private boolean aclRoleIsolation;

    // 发起操作按钮权限
    private List<Button> startButtons = Collections.emptyList();

    // 待办操作按钮权限
    private List<Button> todoButtons = Collections.emptyList();

    // 已办操作按钮权限
    private List<Button> doneButtons = Collections.emptyList();

    // 督办操作按钮权限
    private List<Button> monitorButtons = Collections.emptyList();

    // 监控操作按钮权限
    private List<Button> adminButtons = Collections.emptyList();

    // 抄送操作按钮权限
    private List<Button> copyToButtons = Collections.emptyList();

    // 查阅操作按钮权限
    private List<Button> viewerButtons = Collections.emptyList();

    // 附加的按钮权限
    private List<Button> extraButtons = Lists.newArrayListWithCapacity(0);

    /**
     *
     */
    public WorkFlowRights() {
    }

    /**
     * @param aclRole
     */
    public WorkFlowRights(String aclRole, boolean aclRoleIsolation) {
        this.aclRole = aclRole;
        this.aclRoleIsolation = aclRoleIsolation;
    }

    /**
     * @return the startButtons
     */
    public List<Button> getStartButtons() {
        return startButtons;
    }

    /**
     * @param startButtons 要设置的startButtons
     */
    public void setStartButtons(List<Button> startButtons) {
        this.startButtons = startButtons;
    }

    /**
     * @return the todoButtons
     */
    public List<Button> getTodoButtons() {
        return todoButtons;
    }

    /**
     * @param todoButtons 要设置的todoButtons
     */
    public void setTodoButtons(List<Button> todoButtons) {
        this.todoButtons = todoButtons;
    }

    /**
     * @return the doneButtons
     */
    public List<Button> getDoneButtons() {
        return doneButtons;
    }

    /**
     * @param doneButtons 要设置的doneButtons
     */
    public void setDoneButtons(List<Button> doneButtons) {
        this.doneButtons = doneButtons;
    }

    /**
     * @return the aclRoleIsolation
     */
    public boolean isAclRoleIsolation() {
        return aclRoleIsolation;
    }

    /**
     * @param aclRoleIsolation 要设置的aclRoleIsolation
     */
    public void setAclRoleIsolation(boolean aclRoleIsolation) {
        this.aclRoleIsolation = aclRoleIsolation;
    }

    /**
     * @return the monitorButtons
     */
    public List<Button> getMonitorButtons() {
        return monitorButtons;
    }

    /**
     * @param monitorButtons 要设置的monitorButtons
     */
    public void setMonitorButtons(List<Button> monitorButtons) {
        this.monitorButtons = monitorButtons;
    }

    /**
     * @return the adminButtons
     */
    public List<Button> getAdminButtons() {
        return adminButtons;
    }

    /**
     * @param adminButtons 要设置的adminButtons
     */
    public void setAdminButtons(List<Button> adminButtons) {
        this.adminButtons = adminButtons;
    }

    /**
     * @return the copyToButtons
     */
    public List<Button> getCopyToButtons() {
        return copyToButtons;
    }

    /**
     * @param copyToButtons 要设置的copyToButtons
     */
    public void setCopyToButtons(List<Button> copyToButtons) {
        this.copyToButtons = copyToButtons;
    }

    /**
     * @return the viewerButtons
     */
    public List<Button> getViewerButtons() {
        return viewerButtons;
    }

    /**
     * @param viewerButtons 要设置的viewerButtons
     */
    public void setViewerButtons(List<Button> viewerButtons) {
        this.viewerButtons = viewerButtons;
    }

    /**
     * @return the extraButtons
     */
    public List<Button> getExtraButtons() {
        return extraButtons;
    }

    /**
     * @param extraButtons 要设置的extraButtons
     */
    public void setExtraButtons(List<Button> extraButtons) {
        this.extraButtons = extraButtons;
    }

    /**
     * @param buttons
     */
    public void addExtraButtons(List<Button> buttons) {
        this.extraButtons.addAll(buttons);
    }

    /**
     * @param btnCodes
     */
    public void removeAll(Collection<String> btnCodes) {
        if (CollectionUtils.isEmpty(btnCodes)) {
            return;
        }
        List<Button> removedBtns = this.getButtons().stream().filter(button -> btnCodes.contains(button.getCode())).collect(Collectors.toList());
        this.startButtons.removeAll(removedBtns);
        this.todoButtons.removeAll(removedBtns);
        this.doneButtons.removeAll(removedBtns);
        this.monitorButtons.removeAll(removedBtns);
        this.adminButtons.removeAll(removedBtns);
        this.copyToButtons.removeAll(removedBtns);
        this.viewerButtons.removeAll(removedBtns);
    }

    /**
     * @return the todoButtons
     */
    public Set<Button> getButtons() {
        Set<Button> btns = Sets.newLinkedHashSet();
        if (CollectionUtils.isNotEmpty(startButtons)) {
            btns.addAll(startButtons);
        }
        if (CollectionUtils.isNotEmpty(todoButtons)) {
            btns.addAll(todoButtons);
        }
        if (CollectionUtils.isNotEmpty(doneButtons)) {
            btns.addAll(doneButtons);
        }
        if (CollectionUtils.isNotEmpty(monitorButtons)) {
            btns.addAll(monitorButtons);
        }
        if (CollectionUtils.isNotEmpty(adminButtons)) {
            btns.addAll(adminButtons);
        }
        if (CollectionUtils.isNotEmpty(copyToButtons)) {
            btns.addAll(copyToButtons);
        }
        if (CollectionUtils.isNotEmpty(viewerButtons)) {
            btns.addAll(viewerButtons);
        }
        if (CollectionUtils.isNotEmpty(extraButtons)) {
            btns.addAll(extraButtons);
        }
        return btns;
    }

    /**
     * @param code
     * @return
     */
    public boolean hasButtonByCode(String code) {
        return this.getButtons().stream().anyMatch(button -> StringUtils.equals(button.getCode(), code));
    }
}
