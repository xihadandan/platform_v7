/*
 * @(#)Feb 16, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.docexchanger.web.action;

import com.google.common.collect.Lists;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleOrgUserEntity;
import com.wellsoft.pt.basicdata.business.service.BusinessRoleOrgUserService;
import com.wellsoft.pt.dms.bean.DmsDocExchangeRecordDto;
import com.wellsoft.pt.dms.config.support.DmsDocExchangeManagerConfiguration;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.support.DocExchangeActionData;
import com.wellsoft.pt.dms.core.support.DocExchangeDocumentData;
import com.wellsoft.pt.dms.core.web.ActionProxy;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.entity.DmsDocExchangeConfigEntity;
import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;
import com.wellsoft.pt.dms.facade.service.DocExchangerFacadeService;
import com.wellsoft.pt.dms.service.DmsDocExchangeConfigService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Description: 文档交换-获取数据
 *
 * @author chenq
 * @date 2018/5/17
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/17    chenq		2018/5/17		Create
 * </pre>
 */
@Action("文档交换")
public class DocExchangerGetAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6185570346483889901L;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private DmsDocExchangeConfigService dmsDocExchangeConfigService;

    @Autowired
    private DocExchangerFacadeService docExchangerFacadeService;
    @Autowired
    private BusinessRoleOrgUserService businessRoleOrgUserService;


    /**
     * @return
     */
    @ActionConfig(name = "加载文档交换数据", id = DocExchangerActions.ACTION_GET_DOC_EXCHANGER)
    @ResponseBody
    public DocExchangeDocumentData actionPerformed(
            @RequestBody DocExchangeActionData dyFormActionData,
            HttpServletRequest request) {
        String formUuid = dyFormActionData.getFormUuid();
        String dataUuid = dyFormActionData.getDataUuid();
        DocExchangeDocumentData documentData = new DocExchangeDocumentData();
        if (StringUtils.isNotBlank(formUuid)) {
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
            documentData.setDyFormData(dyFormData);
        }
        documentData.setExtras(
                MapUtils.isEmpty(dyFormActionData.getExtras()) ? new HashMap<String, Object>() :
                        dyFormActionData.getExtras());
        Object displayAsLabel = dyFormActionData.getExtra("ep_displayAsLabel");
        ActionContext actionContext = getActionContext();
        DmsDocExchangeManagerConfiguration docExchangeManagerConfiguration = (DmsDocExchangeManagerConfiguration)
                actionContext.getConfiguration();
        documentData.putExtra("docExchangeConfiguration",
                docExchangeManagerConfiguration.getStore());

        if (displayAsLabel != null && "true".equals(displayAsLabel.toString())) {
            documentData.setDisplayAsLabel(true);
        }
        documentData.setActions(actionContext.getActions());//获取CMS的文档交换器配置的操作动作

        if (StringUtils.isBlank(dataUuid) && StringUtils.isBlank(
                dyFormActionData.getDocExcRecordUuid())) { //新建发件的只会保留保存/发送按钮
            return draftDocumentData(documentData);
        }


        if (StringUtils.isNotBlank(dyFormActionData.getDocExcRecordUuid())) {
            DmsDocExchangeRecordDto recordDto = docExchangerFacadeService.getDocExchangeRecord(dyFormActionData.getDocExcRecordUuid(), null, null);
            if (recordDto != null) {
                documentData.setDocExchangeRecord(recordDto);

                //草稿状态:只会保留保存/发送操作
                if (DocExchangeRecordStatusEnum.DRAFT.ordinal() == recordDto.getRecordStatus()) {
                    return draftDocumentData(documentData);
                }

                //已办结的
                if (DocExchangeRecordStatusEnum.FINISH.ordinal() == recordDto.getRecordStatus()) {
                    documentData.setActions(null);
                    return documentData;
                }

                //收件箱的
                if (recordDto.getFromRecordDetailUuid() != null) {
                    if (!recordDto.getUserId().equals(SpringSecurityUtils.getCurrentUserId()) && StringUtils.isNotBlank(recordDto.getConfigUuid())) {
                        DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(recordDto.getConfigUuid());
                        if (StringUtils.isNotBlank(configEntity.getBusinessCategoryUuid())) {
                            List<BusinessRoleOrgUserEntity> roleOrgUserEntityList = businessRoleOrgUserService.findByOrgUuidAndRoleUuid(configEntity.getBusinessCategoryUuid(), null, configEntity.getRecipientRoleUuid());
                            boolean flg = false;
                            for (BusinessRoleOrgUserEntity businessRoleOrgUserEntity : roleOrgUserEntityList) {
                                if (businessRoleOrgUserEntity.getUsers().indexOf(SpringSecurityUtils.getCurrentUserId()) > -1) {
                                    flg = true;
                                    break;
                                }
                            }
                            if (!flg) {
                                documentData.setActions(null);
                                return documentData;
                            }
                        }
                    }

                    documentData.setActions(actionContext.getActions());
                    if (!recordDto.getIsNeedFeedback()) {//不需要反馈的，则不展示反馈按钮
                        actionContext.removeAction(
                                DocExchangerActions.ACTION_FEEDBACK_DOC_EXCHANGER);
                    }
                    if (StringUtils.isNotBlank(documentData.getDocExchangeRecord().getConfigUuid())) {
                        DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(documentData.getDocExchangeRecord().getConfigUuid());
                        if (configEntity.getIsForward() != null && configEntity.getIsForward() != 1) {
                            actionContext.removeAction(
                                    DocExchangerActions.ACTION_FORWARD_DOC_EXCHANGER);
                        }
                    }

                    return documentData;
                }


                //如果是发件箱的且是未办结的，只展示补充发送/撤回/催办/办结按钮
                if (DocExchangeRecordStatusEnum.SENDED.ordinal() == recordDto.getRecordStatus()
                        && StringUtils.isBlank(recordDto.getFromRecordDetailUuid())) {
                    actionContext.removeAction(
                            DocExchangerActions.ACTION_SAVE_VALIDATE_DOC_EXCHANGER);
                    actionContext.removeAction(DocExchangerActions.ACTION_SAVE_DOC_EXCHANGER);
                    actionContext.removeAction(DocExchangerActions.ACTION_SEND_DOC_EXCHANGER);
                    documentData.setActions(actionContext.getActions());
                }
            }

        }


        return documentData;
    }

    /**
     * 草稿状态的文档
     *
     * @param documentData
     * @return
     */
    private DocExchangeDocumentData draftDocumentData(DocExchangeDocumentData documentData) {
        ActionContext actionContext = getActionContext();
        Iterator<ActionProxy> actionProxyIterator = actionContext.getActions().iterator();
        List<String> sendActionStrList = Lists.newArrayList(
                DocExchangerActions.ACTION_SAVE_VALIDATE_DOC_EXCHANGER,
                DocExchangerActions.ACTION_SAVE_DOC_EXCHANGER,
                DocExchangerActions.ACTION_SEND_DOC_EXCHANGER);
        while (actionProxyIterator.hasNext()) {
            ActionProxy actionProxy = actionProxyIterator.next();
            if (!sendActionStrList.contains(actionProxy.getId())) {
                actionProxyIterator.remove();
            }
        }
        documentData.setActions(actionContext.getActions());
        return documentData;
    }

}
