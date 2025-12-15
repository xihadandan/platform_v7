package com.wellsoft.pt.dyform.implement.definition.exceptions;

import org.hibernate.HibernateException;

/**
 * ddl操作异常
 *
 * @author hunt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-30.1	hunt		2014-6-30		Create
 * </pre>
 * @date 2014-6-30
 */
public class HibernateDataExistException extends HibernateException {

    public HibernateDataExistException(String message) {
        super(message);
    }

}
