/*
 * @(#)2016年3月15日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.support;

import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.common.fdext.facade.service.CdFieldFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月15日.1	zhongzh		2016年3月15日		Create
 * </pre>
 * @date 2016年3月15日
 */
public class FieldExtensionTag extends TagSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private volatile static com.wellsoft.pt.common.fdext.facade.service.CdFieldFacade facade;

    private String id;

    private String name;

    private String tenantId;

    private String groupCode;

    private boolean renderData = false;

    public FieldExtensionTag() {
        if (facade == null) {
            synchronized (FieldExtensionTag.class) {
                if (facade == null) {
                    facade = ApplicationContextHolder.getBean(CdFieldFacade.class);
                }
            }
        }
    }

    @Override
    public int doStartTag() {
        JspWriter out = pageContext.getOut();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        try {
            if (StringUtils.isBlank(name)) {
                this.name = id;
            }
            if (StringUtils.isBlank(groupCode)) {
                this.groupCode = id;
            }
            if (StringUtils.isBlank(tenantId)) {
                // 默认取当前租户
                this.tenantId = SpringSecurityUtils.getCurrentTenantId();
            }
            // 数据UUID
            Configuration cfg = new Configuration();
            cfg.setClassForTemplateLoading(RenderFactory.class, "../ftl");
            cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
            Map<String, Object> root = new HashMap<String, Object>();
            root.put("id", id);
            root.put("name", name);
            root.put("tenantId", tenantId);
            root.put("groupCode", groupCode);
            DyFieldRender render;
            if (renderData) {
                String dataUuid = request.getParameter("dataUuid");
                render = facade.getDyFieldRender(tenantId, groupCode, dataUuid);
            } else {
                render = facade.getDyFieldRender(tenantId, groupCode);
            }
            render.setRenderData(renderData);
            root.put("render", render);
            Template template = cfg.getTemplate("cd_field_ext_definition.ftl", Encoding.UTF8.getValue());
            template.process(root, out);
            // StringWriter writer = new StringWriter();
            // template.process(root, writer);
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return SKIP_BODY;
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
     * @return the tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId 要设置的tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the groupCode
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * @param groupCode 要设置的groupCode
     */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
