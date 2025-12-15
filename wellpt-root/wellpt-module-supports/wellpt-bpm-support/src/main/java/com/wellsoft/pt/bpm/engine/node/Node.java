/*
 * @(#)2012-11-2 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.node;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.core.Transition;
import com.wellsoft.pt.bpm.engine.core.handler.Handler;
import com.wellsoft.pt.bpm.engine.core.handler.HandlerFactory;
import com.wellsoft.pt.bpm.engine.enums.ForkMode;
import com.wellsoft.pt.bpm.engine.enums.JoinMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Description: 代表任务环节的节点类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-2.1	zhulh		2012-11-2		Create
 * </pre>
 * @date 2012-11-2
 */
public abstract class Node implements Serializable {
    private static final long serialVersionUID = 2264908458308069060L;
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 结点执行动作的处理类
     */
    protected Handler handler = HandlerFactory.getHanlder(this);

    /**
     * 节点名称
     */
    protected String name;

    /**
     * 节点ID
     */
    protected String id;

    /**
     * 节点类型
     */
    protected String type;
    /**
     * 动态表单ID
     */
    protected String formID;
    /**
     * 异步
     */
    protected boolean isAsync = false;
    private int forkMode = ForkMode.SINGLE.getValue();
    private int joinMode = JoinMode.SINGLE.getValue();
    /**
     * 监听器
     */
    private String[] listeners;
    /**
     * 流向ID
     */
    private String directionId;
    private Boolean canEditForm = null;

    /**
     * @return the handler
     */
    public Handler getHandler() {
        return handler;
    }

    /**
     * @param handler 要设置的handler
     */
    public void setHandler(Handler handler) {
        this.handler = handler;
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
     * @return the formID
     */
    public String getFormID() {
        return formID;
    }

    /**
     * @param formID 要设置的formID
     */
    public void setFormID(String formID) {
        this.formID = formID;
    }

    /**
     * @return the listeners
     */
    public String[] getListeners() {
        return listeners;
    }

    /**
     * @param listeners 要设置的listeners
     */
    public void setListeners(String[] listeners) {
        this.listeners = listeners;
    }

    /**
     * @return the directionId
     */
    public String getDirectionId() {
        return directionId;
    }

    /**
     * @param directionId 要设置的directionId
     */
    public void setDirectionId(String directionId) {
        this.directionId = directionId;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the forkMode
     */
    public int getForkMode() {
        return forkMode;
    }

    /**
     * @param forkMode 要设置的forkMode
     */
    public void setForkMode(int forkMode) {
        this.forkMode = forkMode;
    }

    /**
     * @return the joinMode
     */
    public int getJoinMode() {
        return joinMode;
    }

    /**
     * @param joinMode 要设置的joinMode
     */
    public void setJoinMode(int joinMode) {
        this.joinMode = joinMode;
    }

    /**
     * @return the isAsync
     */
    public boolean isAsync() {
        return isAsync;
    }

    /**
     * @param isAsync 要设置的isAsync
     */
    public void setAsync(boolean isAsync) {
        this.isAsync = isAsync;
    }

    /**
     * 获取下一节点的ID
     *
     * @return
     */
    public abstract String getToID();

    /**
     * @param executionContext
     */
    public void enter(ExecutionContext executionContext) {
        Token token = executionContext.getToken();
        token.setNode(this);

        // remove the transition references from the runtime context
        executionContext.setTransition(null);
        executionContext.setTransitionSource(null);
    }

    /**
     * 执行节点
     *
     * @param executionContext
     */
    public abstract void execute(ExecutionContext executionContext);

    /**
     * 离开节点
     *
     * @param transition
     * @param executionContext
     */
    public void leave(Transition transition, ExecutionContext executionContext) {
        Token token = executionContext.getToken();
        token.setNode(this);
        executionContext.setTransition(transition);
        executionContext.setTransitionSource(this);
        transition.take(executionContext);
    }

    /**
     * 获取离开节点时的转向
     *
     * @return
     */
    public abstract Transition getLeavingTransition();

    /**
     * @param executionContext
     * @return
     */
    public boolean complete(ExecutionContext executionContext) {
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return id;
    }


    public Boolean getCanEditForm() {
        return canEditForm;
    }

    public void setCanEditForm(Boolean canEditForm) {
        this.canEditForm = canEditForm;
    }
}
