/*
* @(#)${createDate} V1.0
*
* Copyright 2015 WELL-SOFT, Inc. All rights reserved.
*/
package ${package}.listener;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.impl.DirectionListenerAdapter;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;


/**
* Description: 如何描述该类
*
* @author ${author}
* @date ${createDate}
* @version 1.0
*
*
<pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * ${createDate}.1	${author}        ${createDate}		Create
 * </pre>
*
*/
@Service
@Transactional
public class ${className} extends DirectionListenerAdapter {

/**
* (non-Javadoc)
* @see com.wellsoft.pt.bpm.engine.context.listener.Listener#getName()
*/
@Override
public String getName() {
return "${name}流向事件监听";
}

/**
* (non-Javadoc)
* @see com.wellsoft.pt.bpm.engine.context.listener.Listener#getOrder()
*/
@Override
public int getOrder() {
return 0;
}

/**
* (non-Javadoc)
* @see com.wellsoft.pt.bpm.engine.context.listener.DirectionListener#transition(com.wellsoft.pt.bpm.engine.context.event.Event)
*/
@Override
public void transition(Event event) throws WorkFlowException {
}
}
