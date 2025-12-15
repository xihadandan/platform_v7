package com.wellsoft.pt.cg.core.source;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * 实体类表
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-8.1	lmw		2015-7-8		Create
 * </pre>
 * @date 2015-7-8
 */
public class EntitySource implements Source {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private List<Class<?>> clazzs = new LinkedList<Class<?>>();

    public void addClass(String className) {
        try {
            clazzs.add(Class.forName(className));
        } catch (ClassNotFoundException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("class [" + className + "] is not found");
        }
    }

    public List<Class<?>> getClazzs() {
        return clazzs;
    }
}
