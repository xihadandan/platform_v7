/*
 * @(#)Feb 16, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.web.action;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.dms.config.support.Store;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.core.support.DyformDocumentData;
import com.wellsoft.pt.dms.core.support.DyformPrevNextActionData;
import com.wellsoft.pt.dms.core.web.ActionProxy;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 16, 2017.1	zhulh		Feb 16, 2017		Create
 * </pre>
 * @date Feb 16, 2017
 */
@Action("表单单据操作")
public class DyFormGetAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6185570346483889901L;

    public static String KEY_VIEW_MODE = "ep_view_mode";

    public static String VALUE_VIEW_MODE = "0";

    public static String VALUE_EDIT_MODE = "1";

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private ReadMarkerService readMarkerService;
    @Autowired
    private CdDataStoreService cdDataStoreService;

    /**
     * @return
     */
    @ActionConfig(name = "加载数据", id = DyFormActions.ACTION_GET)
    @ResponseBody
    public DyformDocumentData actionPerformed(@RequestBody DyFormActionData dyFormActionData) {
        String formUuid = dyFormActionData.getFormUuid();
        String dataUuid = dyFormActionData.getDataUuid();

        // 默认页面是查看详情状态
        String displayAsLabel = "true";// dyFormActionData.getExtra("ep_displayAsLabel");
        ActionContext actionContext = getActionContext();
        // 配置的操作按钮
        List<ActionProxy> configActions = actionContext.getActions();

        // 文件查看模式，1编辑
        // 新增固定是可编辑状态
        if (StringUtils.isBlank(dataUuid)) {
            displayAsLabel = "false";
        } else { // 编辑或查看详情操作
            String epFileViewMode = dyFormActionData.getExtraString(KEY_VIEW_MODE);
            if (VALUE_EDIT_MODE.equals(epFileViewMode)) { // 编辑操作
                displayAsLabel = "false";
            } else { // 查看详情操作
                displayAsLabel = "true";
            }
        }

        // 表单数据
        DyFormData dyFormData = null;
        Store store = actionContext.getConfiguration().getStore(actionContext, null);
        if ("true".equals(displayAsLabel) && store != null && StringUtils.isNotBlank(store.getDisplayFormUuid())) {
            dyFormData = dyFormApiFacade.getDyFormData(store.getDisplayFormUuid(), dataUuid);
        } else {
            dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
        }
        DyformDocumentData documentData = new DyformDocumentData(dyFormData);
        documentData.setExtras(dyFormActionData.getExtras());
        documentData.setDyFormData(dyFormData);

        // 查看操作，则不需要出现表单对应的的操作按钮
        if ("true".equals(displayAsLabel)) {
            documentData.setDisplayAsLabel(true);
            ArrayList<ActionProxy> actionList = new ArrayList<ActionProxy>();
            // 套打操作权限判断
            //			if (actionContext.isGrantedAction(DyFormActions.ACTION_PRINT)) {
            //				actionList.add(actionContext.buildAction(DyFormActions.ACTION_PRINT));
            //			}
            // 编辑操作权限判断
            if (actionContext.isGrantedAction(DyFormActions.ACTION_EDIT)) {
                actionList.add(actionContext.buildAction(DyFormActions.ACTION_EDIT));
            }
            // 关闭操作权限判断
            if (actionContext.isGrantedAction(DyFormActions.ACTION_CLOSE)) {
                actionList.add(actionContext.buildAction(DyFormActions.ACTION_CLOSE));
            }
            if (isOldConfigActions(configActions)) {
                documentData.setActions(actionList);
            } else {
                documentData.setActions(filterActions(configActions, actionList, documentData.isDisplayAsLabel()));
            }
        } else {
            if (StringUtils.isBlank(dataUuid)) {
                actionContext.removeAction(DyFormActions.ACTION_VIEW_READ_RECORD);
                actionContext.removeAction(DyFormActions.ACTION_VIEW_HISTORY_VERSION);
                actionContext.removeAction(DyFormActions.ACTION_SAVE_NEW_VERSION);
                actionContext.removeAction(DyFormActions.ACTION_SAVE_NEW_VERSION_WITH_REMARK);
                actionContext.removeAction(DyFormActions.ACTION_SEND_TO_APPROVE);
                actionContext.removeAction(DyFormActions.ACTION_EDIT);
                actionContext.removeAction(DyFormActions.ACTION_DELETE);
                actionContext.removeAction(DyFormActions.ACTION_DELETE_ALL_VERSION);
                // actionContext.removeAction(DyFormActions.ACTION_PRINT);
            } else {
                actionContext.removeAction(DyFormActions.ACTION_EDIT);
                // actionContext.removeAction(DyFormActions.ACTION_PRINT);
            }
            if (isOldConfigActions(configActions)) {
                documentData.setActions(actionContext.getActions());
            } else {
                documentData.setActions(filterActions(configActions, null, documentData.isDisplayAsLabel()));
            }
        }
        documentData.setTitle(actionContext.getDocumentTitle(dyFormData));

        // 启用阅读记录时，记录阅读人员
        boolean isEnableReadRecord = getActionContext().getConfiguration().isEnableReadRecord();
        if (Boolean.TRUE.equals(isEnableReadRecord) && StringUtils.isNotBlank(dataUuid)) {
            readMarkerService.markRead(dataUuid, SpringSecurityUtils.getCurrentUserId());
        }
        if (dyFormActionData.getExtra("tagDataKey") != null && StringUtils.isNotBlank(dataUuid)) {
            Map<String, Object> dataMap = dyFormData.getFormDataByFormUuidAndDataUuid(formUuid, dataUuid);
            if (dataMap != null && !dataMap.isEmpty()) {
                Object tagDataKeyValue = dataMap.get(dyFormActionData.getExtra("tagDataKey"));
                if (tagDataKeyValue != null) {
                    readMarkerService.markRead(tagDataKeyValue.toString(), SpringSecurityUtils.getCurrentUserId());
                }
            }
        }

        return documentData;
    }

    /**
     * @param configActions
     * @param buildInActions
     * @param displayAsLabel
     * @return
     */
    private List<ActionProxy> filterActions(List<ActionProxy> configActions, ArrayList<ActionProxy> buildInActions,
                                            boolean displayAsLabel) {
        List<ActionProxy> returnActions = Lists.newArrayList();
        Set<ActionProxy> returnActionSet = Sets.newLinkedHashSet();
        Set<ActionProxy> removeActionSet = Sets.newLinkedHashSet();
        returnActionSet.addAll(configActions);
        if (CollectionUtils.isNotEmpty(buildInActions)) {
            returnActionSet.addAll(buildInActions);
        }
        for (ActionProxy configAction : configActions) {
            Map<String, Object> properties = configAction.getProperties();
            String associatedDocEditModel = ObjectUtils.toString(properties.get("associatedDocEditModel"),
                    StringUtils.EMPTY);
            // 表单在显示文本状态下去掉编辑模式的按钮
            if (displayAsLabel) {
                if (StringUtils.equals(VALUE_EDIT_MODE, associatedDocEditModel)) {
                    removeActionSet.add(configAction);
                }
            } else {
                // 表单在编辑状态下去掉查看模式的按钮
                if (StringUtils.equals(VALUE_VIEW_MODE, associatedDocEditModel)) {
                    removeActionSet.add(configAction);
                }
            }
        }
        returnActionSet.removeAll(removeActionSet);
        returnActions.addAll(returnActionSet);
        return returnActions;
    }

    /**
     * @param configActions
     * @return
     */
    private boolean isOldConfigActions(List<ActionProxy> configActions) {
        for (ActionProxy configAction : configActions) {
            Map<String, Object> properties = configAction.getProperties();
            String associatedDocEditModel = ObjectUtils.toString(properties.get("associatedDocEditModel"),
                    StringUtils.EMPTY);
            if (StringUtils.isBlank(associatedDocEditModel)) {
                return true;
            }
        }
        return false;
    }


    @ListViewActionConfig(name = "加载上一笔表单数据", id = "btn_dyform_prev_document", executeJsModule = "DmsDyformPrevOrNextDocumentRefreshAction")
    @ResponseBody
    public ActionResult prevDyformDocument(@RequestBody DyformPrevNextActionData dyFormActionData) {
        return prevOrNextDyformDocument(dyFormActionData, DyformPrevNextActionData.ActionEnum.PREV);
    }

    @ListViewActionConfig(name = "加载下一笔表单数据", id = "btn_dyform_next_document", executeJsModule = "DmsDyformPrevOrNextDocumentRefreshAction")
    @ResponseBody
    public ActionResult nextDyformDocument(@RequestBody DyformPrevNextActionData dyFormActionData) {
        return prevOrNextDyformDocument(dyFormActionData, DyformPrevNextActionData.ActionEnum.NEXT);
    }

    private ActionResult prevOrNextDyformDocument(DyformPrevNextActionData dyFormActionData, DyformPrevNextActionData.ActionEnum actionEnum) {
        ActionResult result = new ActionResult();
        result.setExecuteJsModule("DmsDyformPrevOrNextDocumentRefreshAction");
        DataStoreParams dataStoreParams = dyFormActionData.getDataStoreParams();
        int index = dyFormActionData.getIndex();

        String dataUuid = dyFormActionData.getDataUuid();
        String primaryField = dyFormActionData.getPrimaryField();
        PagingInfo page = dataStoreParams.getPagingInfo();
        page.setAutoCount(true);
        if (StringUtils.isBlank(dataUuid) || StringUtils.isBlank(primaryField)) {
            logger.error("未配置主键");
            throw new BusinessException("数据异常：未配置数据主键");
        }
        // 先查询下当页数据，判断该笔数据是否还在该页该位置上
        DataStoreData dataStoreData = cdDataStoreService.loadData(dataStoreParams);
        boolean dataExisted = dataUuid.equals(dataStoreData.getData().get(index).get(primaryField).toString());
        if (!dataExisted) {// 不在位置上时候，判断是否还在该页
            // 修正实时的下标位置
            for (int i = 0, len = dataStoreData.getData().size(); i < len; i++) {
                if (dataUuid.equals(dataStoreData.getData().get(i).get(primaryField).toString())) {
                    dataExisted = true;
                    index = i;
                    break;
                }
            }
            // 不在该页时候，需要重写定位该笔数据的位置
            if (!dataExisted) { // FIXME: 实时计算可能会很耗时，当数据量变动范围大的情况
                // 判断数据是否还在筛选数据仓库内
                Condition uuidCondition = new Condition();
                uuidCondition.setColumnIndex(primaryField);
                uuidCondition.setValue(dataUuid);
                uuidCondition.setType(CriterionOperator.eq.getType());
                dataStoreParams.getCriterions().add(uuidCondition);
                long cnt = cdDataStoreService.loadCount(dataStoreParams);// 判断下是否还在筛选数据仓库内
                dataStoreParams.getCriterions().remove(dataStoreParams.getCriterions().size() - 1);
                if (cnt > 0) {
                    dataExisted = true;
                    dataStoreData = null;
                    int pageIndex = 0;
                    while (dataStoreData == null) {
                        page.setCurrentPage(pageIndex++);
                        dataStoreData = cdDataStoreService.loadData(dataStoreParams);
                        // 修正实时的下标位置
                        for (int i = 0, len = dataStoreData.getData().size(); i < len; i++) {
                            if (dyFormActionData.getDataUuid().equals(dataStoreData.getData().get(i).get(primaryField).toString())) {
                                dataExisted = true;
                                index = i;
                                break;
                            }
                        }
                    }
                }
            }
        }
        DyformPrevNextActionData actionData = new DyformPrevNextActionData();
        actionData.setPrimaryField(dyFormActionData.getPrimaryField());
        // 下一笔
        if (actionEnum.equals(DyformPrevNextActionData.ActionEnum.NEXT)) {
            if (dataExisted) {
                if (index == dataStoreData.getData().size() - 1) {// 最后一个
                    // 往后翻页
                    page.setCurrentPage(page.getCurrentPage() + 1);
                    dataStoreData = cdDataStoreService.loadData(dataStoreParams);
                    if (!dataStoreData.getData().isEmpty()) {
                        actionData.setDataStoreParams(dataStoreParams);
                        actionData.setDataUuid(dataStoreData.getData().get(0).get(dyFormActionData.getPrimaryField()).toString());
                        actionData.setIndex(0);
                        result.setData(actionData);
                    }
                } else {
                    actionData.setDataStoreParams(dataStoreParams);
                    actionData.setDataUuid(dataStoreData.getData().get(index + 1).get(dyFormActionData.getPrimaryField()).toString());
                    actionData.setIndex(index + 1);
                    result.setData(actionData);
                }
            } else {// 不存在的情况下，表示数据发生变更，被筛选掉了，那么后面的数据会往前靠
                if (dataStoreData.getData().get(index) != null) {//FIXME: 排在原数据的后一条数据，可能已经不是原来那个后一条了，如果数据发生变更的话
                    actionData.setDataStoreParams(dataStoreParams);
                    actionData.setDataUuid(dataStoreData.getData().get(index).get(dyFormActionData.getPrimaryField()).toString());
                    actionData.setIndex(index);

                    result.setData(actionData);
                }
            }
        } else if (actionEnum.equals(DyformPrevNextActionData.ActionEnum.PREV)) { // 上一笔
            if (dataExisted) {
                if (index == 0) { // 往前翻页
                    if (page.getCurrentPage() > 0) {
                        page.setCurrentPage(page.getCurrentPage() - 1);
                        dataStoreData = cdDataStoreService.loadData(dataStoreParams);
                        actionData.setDataStoreParams(dataStoreParams);
                        actionData.setDataUuid(dataStoreData.getData().get(page.getPageSize() - 1).get(dyFormActionData.getPrimaryField()).toString());
                        actionData.setIndex(page.getPageSize() - 1);
                        result.setData(actionData);
                    }

                } else {
                    actionData.setDataStoreParams(dataStoreParams);
                    actionData.setDataUuid(dataStoreData.getData().get(index - 1).get(dyFormActionData.getPrimaryField()).toString());
                    actionData.setIndex(index - 1);
                    if (dataStoreParams.getPagingInfo().getCurrentPage() == 0 && (index - 1) == 0) {
                        actionData.setFirst(true);
                    }
                    result.setData(actionData);
                }
            }
        }

        if (actionData.getIndex() == 0 && page.getCurrentPage() == 1) {
            actionData.setFirst(true);
        }
        if (actionData.getIndex() == dataStoreData.getData().size() - 1 && page.getTotalPages() == page.getCurrentPage()) {
            actionData.setLast(true);
        }
        return result;
    }

}