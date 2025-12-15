package com.wellsoft.pt.dms.core.web.view;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.dms.bean.DmsDocExchangeRecordDto;
import com.wellsoft.pt.dms.config.support.DmsDocExchangeManagerConfiguration;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.context.ActionContextHolder;
import com.wellsoft.pt.dms.core.support.DataType;
import com.wellsoft.pt.dms.dto.DmsDocExchangeConfigDto;
import com.wellsoft.pt.dms.dto.DmsDocExchangeDyformDto;
import com.wellsoft.pt.dms.entity.DmsDocExchangeConfigEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeDyformEntity;
import com.wellsoft.pt.dms.enums.DocExchangeEncryptionLevelEnum;
import com.wellsoft.pt.dms.enums.DocExchangeNotifyWayEnum;
import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;
import com.wellsoft.pt.dms.enums.DocExchangeUrgeLevelEnum;
import com.wellsoft.pt.dms.facade.service.DocExchangerFacadeService;
import com.wellsoft.pt.dms.service.DmsDocExchangeConfigService;
import com.wellsoft.pt.dms.service.DmsDocExchangeDyformService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Description: 文档交换_表单视图
 *
 * @author chenq
 * @date 2018/5/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/21    chenq		2018/5/21		Create
 * </pre>
 */
public class DocExchangerDyformView extends DyFormView {

    public DocExchangerDyformView(String formUuid, String dataUuid) {
        super(formUuid, dataUuid);
    }


    @Override
    public String getViewName() {
        return "/dms/document/doc_exchanger_data_view";
    }

    @Override
    public void loadActionData(Model model, ActionContext actionContext, HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        DmsDocExchangeManagerConfiguration configuration = (DmsDocExchangeManagerConfiguration) ActionContextHolder.getContext().getConfiguration();
        model.addAttribute("store", configuration.getStore());
        List<DocExchangeNotifyWayEnum> notifyWays = configuration.getStore().getNotifyTypes();
        model.addAttribute("notifyWays", notifyWays);
        if (CollectionUtils.isNotEmpty(notifyWays)) {
            List<Map<String, Object>> enumsLists = Lists.newArrayList();
            for (DocExchangeNotifyWayEnum way : notifyWays) {
                Map<String, Object> m = Maps.newHashMap();
                m.put("code", way.getCode());
                m.put("name", way.getName());
                m.put("type", way.toString());
                enumsLists.add(m);
            }
            model.addAttribute("notifyWaysMaps", enumsLists);
        }
        model.addAttribute("isNotify", !notifyWays.isEmpty());
        model.addAttribute("docEncryptionLevel", DocExchangeEncryptionLevelEnum.values());
        DocExchangeEncryptionLevelEnum[] deelevelEnums = DocExchangeEncryptionLevelEnum.values();
        List<Map<String, Object>> encryptLelMaps = Lists.newArrayList();
        for (DocExchangeEncryptionLevelEnum e : deelevelEnums) {
            Map<String, Object> m = Maps.newHashMap();
            m.put("code", e.ordinal());
            m.put("name", e.getName());
            m.put("type", e.toString());
            encryptLelMaps.add(m);
        }
        model.addAttribute("docEncryptionLevelMaps", encryptLelMaps);
        List<Map<String, Object>> urgelelMaps = Lists.newArrayList();
        for (DocExchangeUrgeLevelEnum e : DocExchangeUrgeLevelEnum.values()) {
            Map<String, Object> m = Maps.newHashMap();
            m.put("code", e.ordinal());
            m.put("name", e.getName());
            m.put("type", e.toString());
            urgelelMaps.add(m);
        }
        model.addAttribute("docUrgeLevelMaps", urgelelMaps);
        model.addAttribute("docUrgeLevel", DocExchangeUrgeLevelEnum.values());
        model.addAttribute("configUuid", configuration.getStore().getConfigUuid());
        model.addAttribute("isEncrypt", configuration.getStore().isEncrypt());
        model.addAttribute("isSign", configuration.getStore().isSign());
        model.addAttribute("isFeedback", configuration.getStore().isFeedback());
        model.addAttribute("isUrge", configuration.getStore().isUrge());
        model.addAttribute("flowUuid", configuration.getStore().getFlowUuid());
        model.addAttribute("dataType", configuration.getStore().getDataType());

        model.addAttribute("docExchangeConfigUuid", configuration.getStore().getConfigUuid());

        DocExchangerFacadeService docExchangerFacadeService = ApplicationContextHolder.getBean(
                DocExchangerFacadeService.class);
        String docExchangeRecordUuid = request.getParameter("docExchangeRecordUuid");//文档交换记录UUID
        DmsDocExchangeRecordDto recordDto = docExchangerFacadeService.getDocExchangeRecord(docExchangeRecordUuid, getFormUuid(), getDataUuid());

        if (StringUtils.isNotBlank(configuration.getStore().getConfigUuid())) {
            DmsDocExchangeConfigService dmsDocExchangeConfigService = ApplicationContextHolder.getBean(DmsDocExchangeConfigService.class);
            DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(configuration.getStore().getConfigUuid());
            DmsDocExchangeConfigDto configDto = new DmsDocExchangeConfigDto();
            BeanUtils.copyProperties(configEntity, configDto);
            model.addAttribute("configDto", JsonUtils.object2Json(configDto));
            String dyformEntityUuid = configEntity.getDmsDocExchangeDyformUuid();
            if (recordDto != null && StringUtils.isNotBlank(recordDto.getFromRecordDetailUuid())) {
                dyformEntityUuid = StringUtils.isBlank(configEntity.getReceiveDyformUuid()) ? configEntity.getDmsDocExchangeDyformUuid() : configEntity.getReceiveDyformUuid();
            }
            if (StringUtils.isNotBlank(dyformEntityUuid)) {
                DmsDocExchangeDyformService dmsDocExchangeDyformService = ApplicationContextHolder.getBean(DmsDocExchangeDyformService.class);
                DmsDocExchangeDyformEntity dyformEntity = dmsDocExchangeDyformService.getOne(dyformEntityUuid);
                DmsDocExchangeDyformDto dyformDto = new DmsDocExchangeDyformDto();
                BeanUtils.copyProperties(dyformEntity, dyformDto);
                model.addAttribute("dyformDto", JsonUtils.object2Json(dyformDto));
            }
        }

        if (recordDto != null) {
            model.addAttribute("docExchangeRecordUuid", recordDto.getUuid());
            model.addAttribute("recordDto", JsonUtils.object2Json(recordDto));
            model.addAttribute("docExchangeRecordStatus",
                    DocExchangeRecordStatusEnum.get(recordDto.getRecordStatus()));
        }


        if (DataType.DYFORM.getId().equals(
                configuration.getStore().getDataType()) && StringUtils.isNotBlank(
                this.getFormUuid()) && StringUtils.isNotBlank(this.getDataUuid())) {
            DyFormFacade dyFormApiFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
            // 单据数据
            model.addAttribute("documentData",
                    dyFormApiFacade.getDyFormData(getFormUuid(), getDataUuid()));
        }

        model.addAttribute("formUuid", getFormUuid());
        model.addAttribute("dataUuid", getDataUuid());
        // 单据二开模块
        model.addAttribute("documentViewModule", "DmsDocExchangeDocumentView");
        model.addAttribute("target", request.getParameter("target"));
        model.addAttribute("acId", request.getParameter("ac_id"));

    }
}
