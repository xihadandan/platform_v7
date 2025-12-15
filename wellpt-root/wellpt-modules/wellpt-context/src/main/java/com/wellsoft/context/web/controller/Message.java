/*
 * @(#)Message.java 2012-10-15 1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.web.controller;

/**
 * Description: Message.java
 *
 * @author zhulh
 * @date 2012-10-15
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-15.1	zhulh		2012-10-15		Create
 * </pre>
 */
public interface Message {
    public void addMessage(String message);

    public Object getData();

    public void setData(Object data);

    public boolean isSuccess();

    public void setSuccess(boolean success);

    public void clear();
}
