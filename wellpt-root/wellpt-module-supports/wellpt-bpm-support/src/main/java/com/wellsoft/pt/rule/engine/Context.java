/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-24.1	zhulh		2015-6-24		Create
 * </pre>
 * @date 2015-6-24
 */
public class Context {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, Deque<Object>> varStack = new HashMap<String, Deque<Object>>();

    private Map<Integer, Map<String, Object>> levelVarMap = new HashMap<Integer, Map<String, Object>>();

    private StringTokenizer tokenizer;
    private String currentToken;

    private OgnlContext ognlContext = new OgnlContext();

    public Context(String text) {
        init(text);
    }

    /**
     * @param text
     */
    public void init(String text) {
        tokenizer = new StringTokenizer(text);
        nextToken();
    }

    public String nextToken() {
        if (tokenizer.hasMoreTokens()) {
            currentToken = tokenizer.nextToken();
        } else {
            currentToken = null;
        }
        return currentToken;
    }

    public String currentToken() {
        return currentToken;
    }

    public void skipToken(String token) throws ParseException {
        if (!token.equals(currentToken)) {
            throw new ParseException("Warning: " + token + " is expected, but " + currentToken + " is found.");
        }
        nextToken();
    }

    public int currentNumber() throws ParseException {
        int number = 0;
        try {
            number = Integer.parseInt(currentToken);
        } catch (NumberFormatException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return number;
    }

    public Executor createExecutor(String name) {
        return (Executor) ApplicationContextHolder.getBean(name + "Executor");
    }

    public OgnlContext getOgnlContext() {
        return this.ognlContext;
    }

    public void setValue(String key, Object value) {
        ognlContext.put(key, value);
    }

    public Object getValue(String key) {
        if (ognlContext.containsKey(key)) {
            return ognlContext.get(key);
        }
        try {
            return Ognl.getValue(Ognl.parseExpression(key), ognlContext);
        } catch (OgnlException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new ExecuteException(e);
        }
    }

    public void removeValue(String key) {
        if (varStack.containsKey(key)) {
            varStack.get(key).pop();
        }
        throw new ExecuteException("source is null for getValue(\"" + key + "\")");
    }

    /**
     * 初始化所有变量
     *
     * @param key
     * @param object
     * @param level
     */
    public void put(String key, Object object, Integer level) {
        ognlContext.put(key, object);

        if (object == null || object instanceof DyFormData) {
            return;
        }

        Map<String, Object> varMap;
        if (levelVarMap.containsKey(level)) {
            varMap = levelVarMap.get(level);
        } else {
            varMap = new HashMap<String, Object>();
        }

        BeanWrapper wrapper = new BeanWrapperImpl(object);
        PropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : descriptors) {
            String propertyName = propertyDescriptor.getName();
            Object value = wrapper.getPropertyValue(propertyName);
            varMap.put(key + "." + propertyName, null);

            if (value instanceof Array) {
                List<?> list = Arrays.asList(value);
                for (int i = 0; i < list.size(); i++) {
                    varMap.put(key + ".propertyName[" + i + "]", null);
                }
            } else if (value instanceof Collection) {
                List<Object> list = new ArrayList<Object>();
                list.addAll((Collection) value);
                for (int i = 0; i < list.size(); i++) {
                    varMap.put(key + ".propertyName[" + i + "]", null);
                }
            }
        }

        levelVarMap.put(level, varMap);
    }

    /**
     * 定义变量
     *
     * @param level
     * @param varName
     * @param value
     */
    public void defineVar(Integer level, String varName, Object value) {
        // 定义变量
        defineVar(level, varName);
        // 变量设值
        setValue(varName, value);
    }

    /**
     * 定义变量
     *
     * @param level
     * @param varName
     */
    public void defineVar(Integer level, String varName) {
        if (levelVarMap.containsKey(level)) {
            Map<String, Object> varMap = levelVarMap.get(level);
            if (varMap.containsKey(varName)) {
                throw new ExecuteException("var " + varName + " has define");
            }
            varMap.put(varName, null);
        } else {
            Map<String, Object> varMap = new HashMap<String, Object>();
            varMap.put(varName, null);
            levelVarMap.put(level, varMap);
        }
        // 将上级别存在的变量放入堆栈中
        if (ognlContext.containsKey(varName)) {
            if (varStack.containsKey(varName)) {
                Deque<Object> stack = varStack.get(varName);
                stack.push(ognlContext.get(varName));
            } else {
                Deque<Object> stack = new ArrayDeque<Object>();
                stack.push(ognlContext.get(varName));
                varStack.put(varName, stack);
            }
        }
        ognlContext.put(varName, null);
    }

    public void clearVar(Integer level) {
        if (levelVarMap.containsKey(level)) {
            Map<String, Object> varMap = levelVarMap.get(level);
            Set<String> keys = varMap.keySet();
            List<String> list = new ArrayList<String>();
            list.addAll(keys);
            for (int index = 0; index < list.size(); index++) {
                String key = list.get(index);
                varMap.remove(key);

                ognlContext.remove(key);
                // 若堆栈中存在变量则放入ognlContext中
                if (varStack.containsKey(key)) {
                    Deque<Object> stack = varStack.get(key);
                    ognlContext.put(key, stack.pop());
                    if (stack.isEmpty()) {
                        varStack.remove(key);
                    }
                }
            }

            levelVarMap.remove(level);
        }
    }

    public void exeExpression(String expression) {
        try {
            Ognl.getValue(expression, ognlContext);
        } catch (OgnlException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new ExecuteException(e);
        }
    }

    /**
     * 判断变量是否存在
     *
     * @param varName
     * @return
     */
    public boolean isExistVar(String varName) {
        Set<Integer> levelSet = levelVarMap.keySet();
        for (Integer level : levelSet) {
            Map<String, Object> map = levelVarMap.get(level);
            if (map.containsKey(varName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断变量是否存在
     *
     * @param varName
     * @return
     */
    public boolean isExistVar(String varName, Integer level) {
        if (levelVarMap.containsKey(level)) {
            Map<String, Object> map = levelVarMap.get(level);
            if (map.containsKey(varName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isVarNull(String varName) {
        Object obj = ognlContext.get(varName);
        if (obj == null) {
            return true;
        }
        return false;
    }

    public boolean isVarInteger(String varName) {
        Object obj = ognlContext.get(varName);
        if (obj instanceof Integer) {
            return true;
        }
        return false;
    }

    public boolean isVarCollection(String varName) {
        try {
            Object obj = null;
            if (ognlContext.containsKey(varName)) {
                obj = ognlContext.get(varName);
            } else {
                obj = Ognl.getValue(varName, ognlContext);
            }
            if (obj instanceof Collection) {
                return true;
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    public boolean isVarArray(String varName) {
        try {
            Object obj = null;
            if (ognlContext.containsKey(varName)) {
                obj = ognlContext.get(varName);
            } else {
                obj = Ognl.getValue(varName, ognlContext);
            }
            if (obj instanceof Array) {
                return true;
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }
}
