import WidgetWorkViewDevelopment from '@develop/WidgetWorkViewDevelopment';
import { isEmpty } from 'lodash';

class WorkViewSendToApproveFragment extends WidgetWorkViewDevelopment {
  /**
   * 表单组件加载
   *
   * @param {*} $dyformWidget
   */
  dyformMounted($dyformWidget) {
    const _this = this;
    let workData = _this.$widget.workView.getWorkData();
    let extraParams = workData.extraParams || {};
    // 获取发送的内容
    let epSendContent = extraParams.ep_sendContent || extraParams.custom_rt_sentContent;
    if (!isEmpty(epSendContent)) {
      let sendContent = JSON.parse(epSendContent);
      let sendContentType = sendContent.type;
      // 复制源文送审批
      if (sendContentType == 2 && isEmpty(workData.flowInstUuid)) {
        _this.setCopySource2Dyform(sendContent, $dyformWidget);
      }
      // 原文作为链接送审批
      _this.setLink2Dyform(sendContent, $dyformWidget);
    }
  }

  /**
   * 复制源文送审批
   */
  setCopySource2Dyform(sendContent, $dyformWidget) {
    const _this = this;
    let formUuid = sendContent.formUuid;
    let dataUuid = sendContent.dataUuid;
    let botRuleId = sendContent.botRuleId;
    if (!isEmpty(botRuleId)) {
      _this.setBotSource2Dyform(formUuid, dataUuid, botRuleId, sendContent, $dyformWidget);
    } else {
      // 表单数据，已从后端通过url参数加载
      // var dyformData = DyformFunction.loadFormDefinitionData(formUuid, dataUuid);
      // _this.setNewFormData2Dyform(dyformData, sendContent);
    }
  }

  /**
   * 单据转换后的表单数据设置到表单
   *
   * @param {*} formUuid
   * @param {*} dataUuid
   * @param {*} botRuleId
   * @param {*} sendContent
   */
  setBotSource2Dyform(formUuid, dataUuid, botRuleId, sendContent, $dyformWidget) {
    if (isEmpty(formUuid)) {
      this.workView.showError({ message: "表单定义UUID为空，无法通过单据转换复制源文档！" });
      return;
    }
    if (isEmpty(dataUuid)) {
      this.workView.showError({ message: "表单数据UUID为空，无法通过单据转换复制源文档！" });
      return;
    }

    $axios
      .post(`/api/workflow/approve/convertDyformDataByBotRuleId`, {
        sourceFormUuid: formUuid,
        sourceDataUuid: dataUuid,
        botRuleId
      })
      .then(({ data }) => {
        if (data.data) {
          this.setNewFormData2Dyform(data.data, sendContent, $dyformWidget);
        } else {
          this.$widget.$message.error(data.msg || "送审批单据转换失败！");
        }
      });
  }

  /**
   * 设置新的表单数据到表单中
   *
   * @param {*} dyformData
   * @param {*} sendContent
   */
  setNewFormData2Dyform(dyFormData, sendContent, $dyformWidget) {
    const _this = this;
    let onlyFillEditableField = false;
    if (sendContent.onlyFillEditableField || sendContent.onlyFillEditableField == 'true') {
      onlyFillEditableField = true;
    }
    // 表单定义
    let formDefinition = JSON.parse(dyFormData.formDefinition);
    // 主表字段
    let fields = formDefinition.fields || {};
    let mainFormData = _this.getFormDataOfMain(dyFormData);
    for (let fieldName in fields) {
      if ('uuid' == fieldName) {
        continue;
      }
      if (!mainFormData.hasOwnProperty(fieldName)) {
        continue;
      }
      let fieldValue = mainFormData[fieldName];
      let fieldDefinition = fields[fieldName];
      let endDateValue = null;
      let hasEndDate = false;
      // 时间范围有两个字段
      if (fieldDefinition && fieldDefinition.inputMode == "30" && fieldDefinition.configuration
        && fieldDefinition.configuration.endDateField && fieldName != fieldDefinition.configuration.endDateField) {
        endDateValue = mainFormData[fieldDefinition.configuration.endDateField];
        hasEndDate = true;
      }
      let field = $dyformWidget.dyform.getField(fieldName);
      if (field && hasEndDate && field.formData && endDateValue) {
        field.formData[fieldDefinition.configuration.endDateField] = endDateValue;
      }
      // 只填充可编辑的字段
      if (onlyFillEditableField == true) {
        if (field && field.getCurrentState().editable) {
          $dyformWidget.dyform.setFieldValue(fieldName, fieldValue, true);
        }
      } else {
        $dyformWidget.dyform.setFieldValue(fieldName, fieldValue, true);
      }
    }

    // 从表处理
    // 单据转换不支持从表转换，忽略掉
  }

  /**
   * 获取主表数据
   *
   * @param {*} dyFormData
   */
  getFormDataOfMain(dyFormData) {
    let formUuid = dyFormData.formUuid;
    let formDatas = dyFormData.formDatas || {};
    let mainFormDatas = formDatas[formUuid];
    return mainFormDatas != null && mainFormDatas.length != 0 ? mainFormDatas[0] : {};
  }

  /**
   * 原文作为链接送审批
   */
  setLink2Dyform(sendContent, $dyformWidget) {
    const _this = this;
    let links = sendContent.links;
    if (links == null || links.length == 0) {
      console.error('links is empty', sendContent);
      return;
    }

    let workData = _this.$widget.workView.getWorkData();
    // 发起流程时添加流程运行时参数
    if (isEmpty(workData.flowInstUuid)) {
      _this.$widget.workView.addExtraParam('custom_rt_sentContent', JSON.stringify(sendContent));
      _this.$widget.workView.addExtraParam('custom_rt_workViewFragment', 'WorkViewSendToApproveFragment');
    }

    let WorkflowSendToApproveLinks = Vue.extend({
      template: '<WorkflowSendToApproveLinks :links="links" :flowInstUuid="flowInstUuid" :evtWidget="evtWidget"></WorkflowSendToApproveLinks>',
      components: { WorkflowSendToApproveLinks: () => import('./template/workflow-send-to-approve-links.vue') },
      data: function () {
        return {
          links,
          flowInstUuid: workData.flowInstUuid,
          evtWidget: $dyformWidget
        };
      }
    });
    let dyformContainer = $dyformWidget.$el.querySelector('.dyform-container');
    let linksDiv = document.createElement('div');
    dyformContainer.appendChild(linksDiv);
    let workflowSendToApproveLinks = new WorkflowSendToApproveLinks();
    workflowSendToApproveLinks.$mount(linksDiv);
  }
}

export default WorkViewSendToApproveFragment;
