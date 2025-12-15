package com.wellsoft.pt.mail.support;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Description: smartupload请求类
 *
 * @author wuzq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-1	wuzq		2013-2-7		Create
 * </pre>
 * @date 2013-3-1
 */
public class Request {

    private Hashtable<String, Hashtable<Integer, String>> m_parameters;
    private int m_counter;

    Request() {
        m_parameters = new Hashtable<String, Hashtable<Integer, String>>();
        m_counter = 0;
    }

    protected void putParameter(String name, String value) {
        if (name == null)
            throw new IllegalArgumentException("The name of an element cannot be null.");
        if (m_parameters.containsKey(name)) {
            Hashtable<Integer, String> values = m_parameters.get(name);
            values.put(new Integer(values.size()), value);
        } else {
            Hashtable<Integer, String> values = new Hashtable<Integer, String>();
            values.put(new Integer(0), value);
            m_parameters.put(name, values);
            m_counter++;
        }
    }

    public String getParameter(String name) {
        if (name == null)
            throw new IllegalArgumentException("Form's name is invalid or does not exist (1305).");
        Hashtable values = m_parameters.get(name);
        if (values == null)
            return null;
        else
            return (String) values.get(new Integer(0));
    }

    public Enumeration<String> getParameterNames() {
        return m_parameters.keys();
    }

    public String[] getParameterValues(String name) {
        if (name == null)
            throw new IllegalArgumentException("Form's name is invalid or does not exist (1305).");
        Hashtable values = m_parameters.get(name);
        if (values == null)
            return null;
        String strValues[] = new String[values.size()];
        for (int i = 0; i < values.size(); i++)
            strValues[i] = (String) values.get(new Integer(i));

        return strValues;
    }
}
