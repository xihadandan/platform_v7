package com.wellsoft.pt.bpm.engine.parser;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowDynamicButton;
import com.wellsoft.pt.bpm.engine.util.NodeUtil;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.dao.FlowCategoryDao;
import com.wellsoft.pt.workflow.entity.FlowCategory;
import com.wellsoft.pt.workflow.entity.FlowFormat;
import com.wellsoft.pt.workflow.service.FlowDefineService;
import com.wellsoft.pt.workflow.service.FlowFormatService;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lilin
 * @ClassName: DictionParser
 * @Description: 这里主要处理拼装流程定义中<diction> 字典定义xml文件
 */
@Service
public class DictionParser extends BaseServiceImpl {
    private static final String NODE_DICTION = "diction";
    private static final String NODE_CATEGORYS = "categorys";
    private static final String NODE_CATEGORY = "category";
    private static final String NODE_RIGHTS = "rights";
    private static final String NODE_START_RIGHTS = "startRights";
    private static final String NODE_RIGHT = "right";
    private static final String NODE_DONE_RIGHTS = "doneRights";
    private static final String NODE_MONITOR_RIGHTS = "monitorRights";
    private static final String NODE_ADMIN_RIGHTS = "adminRights";
    private static final String NODE_BUTTONS = "buttons";
    private static final String NODE_BUTTON = "button";
    private static final String NODE_FORMATS = "formats";
    private static final String NODE_FORMAT = "format";
    private static final String NODE_FORMS = "forms";
    private static final String NODE_FORM = "form";
    private static final String NODE_NAME = "name";
    private static final String NODE_ID = "id";
    private static final String NODE_CODE = "code";
    private static final String NODE_PARENT = "parent";
    private static final String NODE_VALUE = "value";
    private static final String NODE_ISDEFAULT = "isDefault";
    @Autowired
    private FlowCategoryDao categoryDao;
    @Autowired
    private FlowFormatService formatService;
    @Autowired
    private FlowDefineService flowDefineService;
    // @Autowired
    // private FormDefinitionService formDefinitionService;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private SecurityAuditFacadeService securityApiFacade;

    // private static final String NODE_HTML = "html";
    // private static final String NODE_ISUP = "isUp";
    // private static final String NODE_ISUNIQUE = "isUnique";
    // private static final String NODE_ISNOEMPTY = "isNoEmpty";
    // private static final String NODE_ISNOREAD = "isNoRead";

    public Document paserDiction(String id, String moduleId) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_DICTION);
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        // categorys
        Element elements = root.addElement(NODE_CATEGORYS);
        for (FlowCategory element : categoryDao.getAllByUnitId(unitId)) {
            Element unitElement = elements.addElement(NODE_CATEGORY);
            parseCategoryBean(unitElement, element);
        }
        List<Resource> resources = securityApiFacade.getDynamicButtonResourcesByCode(ModuleID.WORKFLOW.getCode());
        // start rights
        // 过滤出发起的动态按钮、若没有指定应用于，则默认加入
        List<Resource> startBtns = filterDyBtn(resources, WorkFlowDynamicButton.DYBTN_WF_START.name());
        elements = root.addElement(NODE_START_RIGHTS);
        for (Resource element : startBtns) {
            Element unitElement = elements.addElement(NODE_RIGHT);
            parseRightBean(unitElement, element);
        }
        // todo rights
        // 过滤出待办的动态按钮、若没有指定应用于，则默认加入
        List<Resource> todoBtns = filterDyBtn(resources, WorkFlowDynamicButton.DYBTN_WF_TODO.name());
        elements = root.addElement(NODE_RIGHTS);
        for (Resource element : todoBtns) {
            Element unitElement = elements.addElement(NODE_RIGHT);
            parseRightBean(unitElement, element);
        }
        // done rights
        // 过滤出已办的动态按钮、若没有指定应用于，则默认加入
        List<Resource> doneBtns = filterDyBtn(resources, WorkFlowDynamicButton.DYBTN_WF_DONE.name());
        elements = root.addElement(NODE_DONE_RIGHTS);
        for (Resource element : doneBtns) {
            Element unitElement = elements.addElement(NODE_RIGHT);
            parseRightBean(unitElement, element);
        }
        // monitor rights
        // 过滤出督办的动态按钮、若没有指定应用于，则默认加入
        List<Resource> monitorBtns = filterDyBtn(resources, WorkFlowDynamicButton.DYBTN_WF_MONITOR.name());
        elements = root.addElement(NODE_MONITOR_RIGHTS);
        for (Resource element : monitorBtns) {
            Element unitElement = elements.addElement(NODE_RIGHT);
            parseRightBean(unitElement, element);
        }
        // admin rights
        // 过滤出监控的动态按钮、若没有指定应用于，则默认加入
        List<Resource> adminBtns = filterDyBtn(resources, WorkFlowDynamicButton.DYBTN_WF_ADMIN.name());
        elements = root.addElement(NODE_ADMIN_RIGHTS);
        for (Resource element : adminBtns) {
            Element unitElement = elements.addElement(NODE_RIGHT);
            parseRightBean(unitElement, element);
        }
        // buttons
        elements = root.addElement(NODE_BUTTONS);
        for (Resource element : resources) {
            Element unitElement = elements.addElement(NODE_BUTTON);
            parseButtonBean(unitElement, element);
        }
        // formats
        elements = root.addElement(NODE_FORMATS);
        for (FlowFormat element : formatService.listAllByOrderPage(new PagingInfo(1, Integer.MAX_VALUE, false),
                "code asc")) {
            Element unitElement = elements.addElement(NODE_FORMAT);
            parseFormatBean(unitElement, element);
        }
        // forms
        elements = root.addElement(NODE_FORMS);
        // 获取该归属单位所有动态表单中定义的表单
        /* modified by huanglinchuan 2014.12.17 begin */
        List<? extends DyFormFormDefinition> dyFormDefinitions = Lists.newArrayList();
        if (StringUtils.isNotBlank(id)) {
            FlowDefinition flowDefinition = flowDefineService.get(id);
            if (flowDefinition != null) {
                moduleId = flowDefinition.getModuleId();
            }
        }
        if (StringUtils.isNotBlank(moduleId)) {//按模块化配置的表单加载表单数据
            dyFormDefinitions = dyFormFacade.getDyFormDefinitionIncludeRefDyFormByModuleId(moduleId);
        } else {
            dyFormDefinitions = dyFormFacade.getAllFormDefinitionBySystemUnitId(SpringSecurityUtils
                    .getCurrentUserUnitId());
        }
        // this.dao.namedQuery("getAllDyFormDefinitionBasicInfo", params,
        // DyFormFormDefinition.class);
        // dyFormApiFacade.getAllFormDefinitions(false)
        for (DyFormFormDefinition element : dyFormDefinitions) {
            /* modified by huanglinchuan 2014.12.17 end */
            // 流程属性只能配置存储单据
            if (!StringUtils.equals(element.getUuid(), element.doGetPFormUuid())) {
                continue;
            }
            Element unitElement = elements.addElement(NODE_FORM);
            parseFormBean(unitElement, element);
        }
        return document;
    }

    /**
     * @param resources
     * @param wfRoleName
     * @return
     */
    private List<Resource> filterDyBtn(List<Resource> resources, String wfRoleName) {
        List<Resource> buttons = new ArrayList<Resource>();
        for (Resource resource : resources) {
            String applyTo = resource.getApplyTo();
            // bug#57516
//            if(resource.getIsDefault()){//默认显示的
//                buttons.add(resource);
//                continue;
//            }
            // 若应用于字段办空，则加入
            if (StringUtils.isBlank(applyTo)) {
                // buttons.add(resource);
            } else {
                String[] keys = applyTo.split(Separator.SEMICOLON.getValue());
                for (String key : keys) {
                    if (wfRoleName.equals(key)) {
                        buttons.add(resource);
                        break;
                    }
                }
            }
        }
        return buttons;
    }

    private void parseFormatBean(Element element, FlowFormat entity) {
        element.addElement(NODE_NAME).addText(entity.getName());
        element.addElement(NODE_CODE).addText(entity.getCode());
        element.addElement(NODE_VALUE).addText(entity.getCode());
        // element.addElement(NODE_HTML).addText(entity.getHtml());
        // element.addElement(NODE_ISUP).addText(NodeUtil.getStringFromInt(entity.getIsUp()));
        // element.addElement(NODE_ISUNIQUE).addText(NodeUtil.getStringFromInt(entity.getIsUnique()));
        // element.addElement(NODE_ISNOEMPTY).addText(NodeUtil.getStringFromInt(entity.getIsNoEmpty()));
        // element.addElement(NODE_ISNOREAD).addText(NodeUtil.getStringFromInt(entity.getIsNoRead()));
    }

    private void parseButtonBean(Element element, Resource entity) {
        element.addElement(NODE_NAME).addText(entity.getName());
        element.addElement(NODE_CODE).addText(entity.getCode());
        element.addElement(NODE_VALUE).addText(entity.getCode());
    }

    private void parseRightBean(Element element, Resource entity) {
        element.addElement(NODE_NAME).addText(entity.getName());
        element.addElement(NODE_CODE).addText(entity.getCode());
        element.addElement(NODE_VALUE).addText(entity.getCode());
        element.addElement(NODE_ISDEFAULT).addText(NodeUtil.getStringFromBool(entity.getIsDefault()));
    }

    private void parseCategoryBean(Element element, FlowCategory entity) {
        String name = entity.getName();
        if (StringUtils.isNotBlank(name)) {
            element.addElement(NODE_NAME).addText(name);
        } else {
            element.addElement(NODE_NAME).addText("");
        }
        String code = entity.getUuid();//流程分类UUID与流程定义关联
        if (StringUtils.isNotBlank(code)) {
            element.addElement(NODE_CODE).addText(code);
        } else {
            element.addElement(NODE_CODE).addText(code);
        }

        if (entity.getParent() != null) {
            element.addElement(NODE_PARENT).addText(entity.getParent().getName());
        } else {
            element.addElement(NODE_PARENT).addText("");
        }
    }

    private void parseFormBean(Element element, DyFormFormDefinition entity) {
        /* modified by huanglinchuan 2014.12.17 begin */
        String descname = entity.getName() + "(V" + entity.getVersion() + ")";
        /* modified by huanglinchuan 2014.12.17 end */
        if (StringUtils.isNotBlank(descname)) {
            element.addElement(NODE_NAME).addText(descname);
        } else {
            element.addElement(NODE_NAME).addText("");
        }
        String uuid = entity.getUuid();
        if (StringUtils.isNotBlank(uuid)) {
            element.addElement(NODE_ID).addText(uuid);
        } else {
            element.addElement(NODE_ID).addText("");
        }
    }

    /**
     * @param uuid
     * @param moduleId
     * @return
     */
    public FlowDictionary paserFlowDictionary(String uuid, String moduleId) {
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        // categorys
        FlowDictionary flowDictionary = new FlowDictionary();
        for (FlowCategory element : categoryDao.getAllByUnitId(unitId)) {
            FlowDictionary.Category category = new FlowDictionary.Category();
            category.setName(element.getName());
            category.setUuid(element.getUuid());
            if (category.getParent() != null) {
                category.setParent(element.getParent().getUuid());
            }
            flowDictionary.getCategorys().add(category);
        }

        List<Resource> resources = securityApiFacade.getDynamicButtonResourcesByCode(ModuleID.WORKFLOW.getCode());
        // start rights
        // 过滤出发起的动态按钮、若没有指定应用于，则默认加入
        List<Resource> startBtns = filterDyBtn(resources, WorkFlowDynamicButton.DYBTN_WF_START.name());
        for (Resource element : startBtns) {
            parseRight(element, flowDictionary.getStartRights());
        }
        // todo rights
        // 过滤出待办的动态按钮、若没有指定应用于，则默认加入
        List<Resource> todoBtns = filterDyBtn(resources, WorkFlowDynamicButton.DYBTN_WF_TODO.name());
        for (Resource element : todoBtns) {
            parseRight(element, flowDictionary.getTodoRights());
        }
        // done rights
        // 过滤出已办的动态按钮、若没有指定应用于，则默认加入
        List<Resource> doneBtns = filterDyBtn(resources, WorkFlowDynamicButton.DYBTN_WF_DONE.name());
        for (Resource element : doneBtns) {
            parseRight(element, flowDictionary.getDoneRights());
        }
        // monitor rights
        // 过滤出督办的动态按钮、若没有指定应用于，则默认加入
        List<Resource> monitorBtns = filterDyBtn(resources, WorkFlowDynamicButton.DYBTN_WF_MONITOR.name());
        for (Resource element : monitorBtns) {
            parseRight(element, flowDictionary.getMonitorRights());
        }
        // admin rights
        // 过滤出监控的动态按钮、若没有指定应用于，则默认加入
        List<Resource> adminBtns = filterDyBtn(resources, WorkFlowDynamicButton.DYBTN_WF_ADMIN.name());
        for (Resource element : adminBtns) {
            parseRight(element, flowDictionary.getAdminRights());
        }
        // buttons
        for (Resource element : resources) {
            parseButton(element, flowDictionary.getButtons());
        }
        // formats
        for (FlowFormat element : formatService.listBySystem(RequestSystemContextPathResolver.system())) {
            parseFormat(element, flowDictionary.getFormats());
        }
        // forms
        // 获取该归属单位所有动态表单中定义的表单
        /* modified by huanglinchuan 2014.12.17 begin */
        List<? extends DyFormFormDefinition> dyFormDefinitions = Lists.newArrayList();
        if (StringUtils.isNotBlank(uuid)) {
            FlowDefinition flowDefinition = flowDefineService.get(uuid);
            if (flowDefinition != null) {
                moduleId = flowDefinition.getModuleId();
            }
        }
        if (StringUtils.isNotBlank(moduleId)) {//按模块化配置的表单加载表单数据
            dyFormDefinitions = dyFormFacade.getDyFormDefinitionIncludeRefDyFormByModuleId(moduleId);
        } else {
            dyFormDefinitions = dyFormFacade.getAllFormDefinitionBySystemUnitId(SpringSecurityUtils
                    .getCurrentUserUnitId());
        }
        // this.dao.namedQuery("getAllDyFormDefinitionBasicInfo", params,
        // DyFormFormDefinition.class);
        // dyFormApiFacade.getAllFormDefinitions(false)
        for (DyFormFormDefinition element : dyFormDefinitions) {
            /* modified by huanglinchuan 2014.12.17 end */
            // 流程属性只能配置存储单据
            if (!StringUtils.equals(element.getUuid(), element.doGetPFormUuid())) {
                continue;
            }
            parseForm(element, flowDictionary.getForms());
        }

        return flowDictionary;
    }

    private void parseFormat(FlowFormat flowFormat, List<FlowDictionary.DictionaryItem> formats) {
        FlowDictionary.DictionaryItem format = new FlowDictionary.DictionaryItem();
        format.setName(flowFormat.getName());
        format.setCode(flowFormat.getCode());
        format.setValue(flowFormat.getCode());
        formats.add(format);
    }

    private void parseRight(Resource resource, List<FlowDictionary.Right> rights) {
        FlowDictionary.Right right = new FlowDictionary.Right();
        right.setName(resource.getName());
        right.setCode(resource.getCode());
        right.setValue(resource.getCode());
        right.setIsDefault(NodeUtil.getStringFromBool(resource.getIsDefault()));
        rights.add(right);
    }

    private void parseButton(Resource resource, List<FlowDictionary.DictionaryItem> buttons) {
        FlowDictionary.Right right = new FlowDictionary.Right();
        right.setName(resource.getName());
        right.setCode(resource.getCode());
        right.setValue(resource.getCode());
        buttons.add(right);
    }

    private void parseForm(DyFormFormDefinition entity, List<FlowDictionary.Form> forms) {
        FlowDictionary.Form form = new FlowDictionary.Form();
        /* modified by huanglinchuan 2014.12.17 begin */
        String descname = entity.getName() + "(V" + entity.getVersion() + ")";
        /* modified by huanglinchuan 2014.12.17 end */
        form.setName(descname);
        form.setUuid(entity.getUuid());
        forms.add(form);
    }

}
