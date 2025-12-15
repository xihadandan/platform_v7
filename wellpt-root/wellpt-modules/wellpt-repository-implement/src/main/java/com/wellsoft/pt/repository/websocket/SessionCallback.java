/*
 * @(#)2019年12月14日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.websocket;

import javax.websocket.Session;
import java.io.IOException;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年12月14日.1	zhongzh		2019年12月14日		Create
 * </pre>
 * @date 2019年12月14日
 */
public interface SessionCallback {

    public void doSend(Session session) throws IOException;

}
