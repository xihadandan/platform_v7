package com.wellsoft.context.component.select2;

import com.wellsoft.context.util.ApplicationContextHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Description: Select2 ajax查询类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年1月29日.1	Xiem		2016年1月29日		Create
 * </pre>
 * @date 2016年1月29日
 */
@Controller
@RequestMapping(value = "/common/select2")
public class Select2QueryController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/update")
    @ResponseBody
    public Select2QueryData update(Select2QueryInfo queryInfo, HttpServletRequest request) {
        queryInfo.setRequest(request);
        Map<String, Object> data = queryInfo.getParams();
        String id = (String) data.get("id");
        String text = (String) data.get("text");
        Select2DataBean bean = null;
        if (StringUtils.isNotBlank(id)) {
            bean = new Select2DataBean(id, null);// 删除操作
        } else if (StringUtils.isNotBlank(text)) {
            bean = new Select2DataBean(null, text);// 新增操作
        }
        if (StringUtils.isNotBlank(queryInfo.getQueryMethod())) {
            Object select2Query = ApplicationContextHolder.getBean(queryInfo.getServiceName());
            if (select2Query == null) {
                throw new RuntimeException("Service[" + queryInfo.getServiceName() + "]不存在！");
            }
            try {
                Method method = select2Query.getClass().getDeclaredMethod(queryInfo.getQueryMethod(),
                        Select2DataBean.class, Select2QueryInfo.class);
                try {
                    method.invoke(select2Query, bean, queryInfo);
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getFullStackTrace(e));
                    throw new RuntimeException("方法执行失败！错误信息：" + e.getMessage());
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Class[" + select2Query.getClass() + "]不存在参数类型为[Select2QueryInfo]且名称为["
                        + queryInfo.getQueryMethod() + "]的方法！");
            }
        } else {
            Select2UpdateApi select2QueryApi = (Select2UpdateApi) ApplicationContextHolder.getBean(queryInfo
                    .getServiceName());
            select2QueryApi.update(bean, queryInfo);
        }
        Select2QueryData select2QueryData = new Select2QueryData(queryInfo.getPagingInfo());
        select2QueryData.addResultData(bean);
        return select2QueryData;
    }

    @RequestMapping(value = "/query")
    @ResponseBody
    public Select2QueryData query(Select2QueryInfo queryInfo, HttpServletRequest request) {
        queryInfo.setRequest(request);

        if (StringUtils.isNotBlank(queryInfo.getQueryMethod())) {
            Object select2Query = ApplicationContextHolder.getBean(queryInfo.getServiceName());
            if (select2Query == null) {
                throw new RuntimeException("Service[" + queryInfo.getServiceName() + "]不存在！");
            }
            try {
                Method method = select2Query.getClass().getDeclaredMethod(queryInfo.getQueryMethod(),
                        Select2QueryInfo.class);
                try {
                    return (Select2QueryData) method.invoke(select2Query, queryInfo);
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getFullStackTrace(e));
                    throw new RuntimeException("方法执行失败！错误信息：" + e.getMessage());
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Class[" + select2Query.getClass() + "]不存在参数类型为[Select2QueryInfo]且名称为["
                        + queryInfo.getQueryMethod() + "]的方法！");
            }
        }
        Select2QueryApi select2QueryApi = (Select2QueryApi) ApplicationContextHolder
                .getBean(queryInfo.getServiceName());
        return select2QueryApi.loadSelectData(queryInfo);
    }

    @RequestMapping(value = "/selection")
    @ResponseBody
    public Select2QueryData initSelection(Select2QueryInfo queryInfo, HttpServletRequest request) {
        queryInfo.setRequest(request);
        if (StringUtils.isNotBlank(queryInfo.getSelectionMethod())) {
            Object select2Query = ApplicationContextHolder.getBean(queryInfo.getServiceName());
            if (select2Query == null) {
                throw new RuntimeException("Service[" + queryInfo.getServiceName() + "]不存在！");
            }
            try {
                Method method = select2Query.getClass().getDeclaredMethod(queryInfo.getSelectionMethod(),
                        Select2QueryInfo.class);
                try {
                    return (Select2QueryData) method.invoke(select2Query, queryInfo);
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getFullStackTrace(e));
                    throw new RuntimeException("方法[" + queryInfo.getSelectionMethod() + "]执行失败！错误信息：" + e.getMessage());
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Class[" + select2Query.getClass() + "]不存在参数类型为[Select2QueryInfo]且名称为["
                        + queryInfo.getSelectionMethod() + "]的方法！");
            }
        }
        Select2QueryApi select2QueryApi = (Select2QueryApi) ApplicationContextHolder
                .getBean(queryInfo.getServiceName());
        return select2QueryApi.loadSelectDataByIds(queryInfo);
    }

    @RequestMapping(value = "/group/query")
    @ResponseBody
    public Select2GroupData queryGroup(Select2QueryInfo queryInfo) {
        if (StringUtils.isNotBlank(queryInfo.getQueryMethod())) {
            Object select2Query = ApplicationContextHolder.getBean(queryInfo.getServiceName());
            if (select2Query == null) {
                throw new RuntimeException("Service[" + queryInfo.getServiceName() + "]不存在！");
            }
            try {
                Method method = select2Query.getClass().getDeclaredMethod(queryInfo.getQueryMethod(),
                        Select2QueryInfo.class);
                try {
                    Select2GroupData tt = (Select2GroupData) method.invoke(select2Query, queryInfo);
                    return tt;
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getFullStackTrace(e));
                    throw new RuntimeException("方法执行失败！错误信息：" + e.getMessage());
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Class[" + select2Query.getClass() + "]不存在参数类型为[Select2QueryInfo]且名称为["
                        + queryInfo.getQueryMethod() + "]的方法！");
            }
        }
        return null;
    }

}
