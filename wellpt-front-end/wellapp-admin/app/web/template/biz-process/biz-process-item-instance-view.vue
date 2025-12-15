<template>
  <div>
    <WidgetDyformSetting v-if="widgetDyformSetting" :parent="getSelf" :widget="widgetDyformSetting" :initFormData="initFormData" />
    <a-layout v-else-if="bizData" class="item-view-container" theme="light">
      <a-layout-header class="item-view-header">
        <a-row>
          <a-col span="12" class="title">
            <h1>{{ bizData.title }}</h1>
          </a-col>
          <a-col span="12" class="item-action-container">
            <a-space>
              <a-button v-for="action in actions" :key="action.id" @click="onActionClick($event, action)">
                {{ action.name }}
              </a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout-content class="item-view-content">
        <WidgetDyform
          v-if="bizData.dyFormData && !dyformComponentLoading"
          ref="dyform"
          :isNewFormData="isNewFormData"
          :displayState="dyformDisplayState"
          :definitionVjson="definitionVjson"
          :formUuid="bizData.dyFormData.formUuid"
          :dataUuid="bizData.dyFormData.dataUuid || bizData.dataUuid"
          :formDatas="bizData.dyFormData.formDatas"
          @mounted="onDyformMounted"
          @formDataChanged="onDyformDataChanged"
          :dyformStyle="{ padding: 'var(--w-padding-md)', background: 'var(--w-bg-color-body)' }"
        />
      </a-layout-content>
    </a-layout>

    <!-- 组织选择框占位符 -->
    <div class="org-select-container" style="display: none"></div>

    <a-modal
      title="选择办理时限"
      :visible="timeLimitModalVisible"
      :maskClosable="false"
      @cancel="timeLimitModalVisible = false"
      @ok="onChooseTimeLimitOk"
    >
      <a-form-model
        class="basic-info"
        :model="timeLimitFormData"
        labelAlign="left"
        ref="timeLimitForm"
        :rules="timeLimitRules"
        :label-col="{ span: 7 }"
        :wrapper-col="{ span: 17, style: { textAlign: 'left' } }"
        :colon="false"
      >
        <a-form-model-item label="办理时限" prop="timeLimit">
          <a-select v-model="timeLimitFormData.timeLimit" show-search style="width: 100%" :filter-option="false">
            <a-select-option v-for="d in timeLimitOptions" :key="d.id">
              {{ d.text }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
      </a-form-model>
    </a-modal>
    <a-modal title="办理过程" :visible="processModalVisible" @cancel="processModalVisible = false" :footer="null">
      <div v-for="item in itemOperations" :key="item.uuid">
        <a-row>
          <a-col span="16">{{ item.operateTime }}</a-col>
          <a-col span="8">{{ item.operatorName }}</a-col>
        </a-row>
        <a-row>
          <a-col>{{ item.operateName }}</a-col>
        </a-row>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { isEmpty } from 'lodash';
import WidgetDyformSetting from '@pageAssembly/app/web/widget/widget-dyform-setting/widget-dyform-setting.vue';
import WidgetTable from '@pageAssembly/app/web/widget/widget-table/widget-table.vue';
import WidgetTableSearchForm from '@pageAssembly/app/web/widget/widget-table/widget-table-search-form.vue';
import WidgetTableButtons from '@pageAssembly/app/web/widget/widget-table/widget-table-buttons.vue';
import WorkFlowErrorHandler from '@workflow/app/web/page/workflow-work/component/WorkFlowErrorHandler.js';
import WorkFlowInteraction from '@workflow/app/web/page/workflow-work/component/WorkFlowInteraction.js';
import '@installPageWidget';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
const BUILD_IN_BTN_CODES = [
  'save',
  'submit',
  'startTimer',
  'pauseTimer',
  'resumeTimer',
  'cancel',
  'complete',
  'suspend',
  'resume',
  'viewProcess',
  'viewFlow',
  'printForm'
];
export default {
  props: {
    itemData: Object
  },
  components: { WidgetDyformSetting },
  inject: ['pageContext', 'locale'],
  data() {
    this.workFlowMap = {};
    this.errorHandler = new WorkFlowErrorHandler(this);
    if (EASY_ENV_IS_BROWSER) {
      this.registerComponentOfWidgetDyformSetting();
    }
    return {
      bizData: null,
      dataUuid: '',
      definitionVjson: null,
      dyformComponentLoading: true,
      timeLimitModalVisible: false,
      timeLimitOptions: [],
      timeLimitFormData: {},
      timeLimitRules: {
        timeLimit: {
          required: true,
          message: '请选择办理时限！'
        }
      },
      processModalVisible: false,
      itemOperations: [],
      widgetDyformSetting: null,
      initFormData: null
    };
  },
  computed: {
    isNewFormData() {
      return isEmpty(this.bizData.itemInstUuid);
    },
    dataUuid() {
      return (this.bizData.dyFormData && this.bizData.dyFormData.dataUuid) || this.bizData.dataUuid;
    },
    dyformDisplayState() {
      let displayState = this.getQueryString('displayState');
      if (displayState) {
        return displayState;
      }
      return this.bizData && this.bizData.state != '30' ? 'edit' : 'label';
    },
    dyformWidget() {
      return this.$dyformWidget || this.$refs.dyform;
    },
    actions() {
      const _this = this;
      // 草稿
      if (_this.isDraft()) {
        return [
          { id: 'save', name: '保存', validate: false },
          { id: 'submit', name: '提交', validate: true },
          { id: 'printForm', name: '打印表单', validate: false }
        ];
      } else if (_this.isSuspend()) {
        // 挂起
        return [
          { id: 'resume', name: '恢复', validate: true },
          { id: 'viewProcess', name: '办理过程', validate: false },
          { id: 'printForm', name: '打印表单', validate: false }
        ];
      } else if (_this.isOver()) {
        // 是否办结
        return [
          { id: 'viewProcess', name: '办理过程', validate: false },
          { id: 'printForm', name: '打印表单', validate: false }
        ];
      } else if (_this.isTimerStarted()) {
        // 计时器已启动
        // 计时器计时中
        if (_this.isTimerTiming()) {
          return [
            { id: 'save', name: '保存', validate: false },
            { id: 'submit', name: '提交', validate: true },
            { id: 'pauseTimer', name: '暂停计时', validate: false },
            { id: 'cancel', name: '撤销', validate: true },
            { id: 'complete', name: '办结', validate: true },
            { id: 'suspend', name: '挂起', validate: true },
            { id: 'viewProcess', name: '办理过程', validate: false },
            { id: 'printForm', name: '打印表单', validate: false }
          ];
        } else {
          // 计时器暂停中
          return [
            { id: 'save', name: '保存', validate: false },
            { id: 'submit', name: '提交', validate: true },
            { id: 'resumeTimer', name: '恢复计时', validate: false },
            { id: 'cancel', name: '撤销', validate: true },
            { id: 'complete', name: '办结', validate: true },
            { id: 'suspend', name: '挂起', validate: true },
            { id: 'viewProcess', name: '办理过程', validate: false },
            { id: 'printForm', name: '打印表单', validate: false }
          ];
        }
      } else {
        // 计时器未启动
        return [
          { id: 'save', name: '保存', validate: false },
          { id: 'submit', name: '提交', validate: true },
          { id: 'startTimer', name: '开始计时', validate: false },
          { id: 'cancel', name: '撤销', validate: true },
          { id: 'complete', name: '办结', validate: true },
          { id: 'suspend', name: '挂起', validate: true },
          { id: 'viewProcess', name: '办理过程', validate: false },
          { id: 'printForm', name: '打印表单', validate: false }
        ];
      }
      return [];
    }
  },
  beforeCreate() {
    import('@dyform/app/web/framework/vue/install').then(m => {
      this.dyformComponentLoading = false;
    });
  },
  created() {
    if (EASY_ENV_IS_BROWSER) {
      this.loadData();
    }
  },
  methods: {
    loadData() {
      const _this = this;
      let itemData = _this.itemData || {};
      let processDefId = itemData.processDefId || _this.getQueryString('processDefId', '');
      let itemId = itemData.itemId || _this.getQueryString('processItemIds', '');
      let itemInstUuid = itemData.itemInstUuid || _this.getQueryString('itemInstUuid', '');
      let formUuid = itemData.formUuid || _this.getQueryString('formUuid', '');
      let dataUuid = itemData.dataUuid || _this.getQueryString('dataUuid', '');
      $axios
        .post('/proxy/api/biz/process/item/instance/get', { processDefId, itemId, itemInstUuid, formUuid, dataUuid })
        .then(({ data }) => {
          if (data.data) {
            _this.bizData = data.data;
            _this.definitionVjson = JSON.parse(_this.bizData.dyFormData.definitionVjson);
            _this.processDefId = _this.bizData.processDefId;
            _this.itemId = _this.bizData.itemId;
            _this.itemInstUuid = _this.bizData.itemInstUuid;
            _this.formUuid = _this.bizData.formUuid;
            _this.dataUuid = _this.bizData.dataUuid;

            if (
              _this.bizData.processItemConfig &&
              _this.bizData.processItemConfig.formConfig &&
              _this.bizData.processItemConfig.formConfig.enabledDyformSetting &&
              _this.bizData.processItemConfig.formConfig.widgetDyformSetting
            ) {
              let widgetDyformSetting = _this.bizData.processItemConfig.formConfig.widgetDyformSetting;
              _this.setDyformTitleIfRequired(widgetDyformSetting, _this.bizData.title);
              let formElementRules = _this.getFormElementRules(widgetDyformSetting);
              _this.updateButtonVisible(widgetDyformSetting);
              widgetDyformSetting.props = {
                isNewFormData: _this.isNewFormData,
                displayState: _this.dyformDisplayState,
                definitionVjson: _this.definitionVjson,
                formUuid: _this.bizData.dyFormData.formUuid,
                dataUuid: _this.dataUuid,
                formDatas: _this.bizData.dyFormData.formDatas,
                formElementRules
              };
              _this.widgetDyformSetting = widgetDyformSetting;
            }

            document.querySelector('title').innerText = _this.bizData.title;

            // 加载包含的事项
            if (_this.itemInstUuid) {
              _this.loadIncludeItemInstances();
            }
          }
        })
        .catch(({ response }) => {
          if (response.data && response.data.msg) {
            _this.$message.error(response.data.msg);
          } else {
            _this.$message.error('服务异常！');
          }
        });
    },
    setDyformTitleIfRequired(widgetDyformSetting, title) {
      let configuration = widgetDyformSetting.configuration || {};
      if (isEmpty(configuration.title)) {
        configuration.title = title;
      }
      if (isEmpty(configuration.editStateTitle)) {
        configuration.editStateTitle = title;
      }
      if (isEmpty(configuration.labelStateTitle)) {
        configuration.labelStateTitle = title;
      }
    },
    getFormElementRules(widgetDyformSetting) {
      const _this = this;
      let formElementRules = {};
      let configuration = widgetDyformSetting.configuration || {};
      let configFormElementRules = configuration.formElementRules || [];
      if (_this.dataUuid) {
        configFormElementRules =
          _this.dyformDisplayState == 'edit' ? configuration.editStateFormElementRules : configuration.labelStateFormElementRules;
      }
      configFormElementRules &&
        configFormElementRules.forEach(rule => {
          formElementRules[rule.id] = rule;
          if (rule.children) {
            let childrenRules = {};
            rule.children.forEach(childRule => {
              childrenRules[childRule.id] = childRule;
            });
            rule.children = childrenRules;
          }
        });
      return formElementRules;
    },
    // 根据业务逻辑更新内置按钮显示
    updateButtonVisible(widgetDyformSetting) {
      const _this = this;
      let configuration = widgetDyformSetting.configuration || {};
      let buttons = (configuration.button && configuration.button.buttons) || [];
      buttons.forEach(button => {
        let btnCode = button.code;
        if (!BUILD_IN_BTN_CODES.includes(btnCode)) {
          return;
        }

        // 草稿
        if (_this.isDraft()) {
          if (!['save', 'submit', 'printForm'].includes(btnCode)) {
            button.visibleType = 'hidden';
          }
        } else if (_this.isSuspend()) {
          // 挂起
          if (!['resume', 'viewProcess', 'viewFlow', 'printForm'].includes(btnCode)) {
            button.visibleType = 'hidden';
          }
        } else if (_this.isOver()) {
          // 是否办结
          if (!['viewProcess', 'viewFlow', 'printForm'].includes(btnCode)) {
            button.visibleType = 'hidden';
          }
        } else {
          // 不是挂起状态，隐藏恢复按钮
          if (btnCode == 'resume') {
            button.visibleType = 'hidden';
          }

          // 启动计时
          if (btnCode == 'startTimer' && _this.isTimerStarted()) {
            button.visibleType = 'hidden';
          }
          // 暂停计时
          if (btnCode == 'pauseTimer') {
            if (_this.isTimerStarted() && _this.isTimerTiming()) {
            } else {
              button.visibleType = 'hidden';
            }
          }
          // 恢复计时
          if (btnCode == 'resumeTimer') {
            if (_this.isTimerStarted() && !_this.isTimerTiming()) {
            } else {
              button.visibleType = 'hidden';
            }
          }
        }

        // 查看流程
        if (btnCode == 'viewFlow') {
          _this.updateViewFlowButtonVisible(button);
        }
      });
    },
    updateViewFlowButtonVisible(button) {
      const _this = this;
      $axios
        .get(`/proxy/api/biz/process/item/instance/getWorkflowBusinessIntegration?itemInstUuid=${this.bizData.itemInstUuid}`)
        .then(({ data: result }) => {
          if (result.data) {
            _this.workflowBusinessIntegration = result.data;
            button.visibleType = 'visible';
          } else {
            button.visibleType = 'hidden';
          }
        });
    },
    getSelf() {
      return this;
    },
    registerComponentOfWidgetDyformSetting() {
      const _this = this;
      let components = { WidgetTable, WidgetTableSearchForm, WidgetTableButtons };
      for (let componentName in components) {
        if (!_this.isComponentRegistered(componentName)) {
          Vue.component(componentName, components[componentName]);
        }
      }
    },
    isComponentRegistered(componentName) {
      return Vue.options.components[componentName];
    },
    addSystemPrefix(url) {
      const _this = this;
      if (_this._$SYSTEM_ID && url && !url.startsWith('/sys/')) {
        url = `/sys/${_this._$SYSTEM_ID}/_${url}`;
      }
      return url;
    },
    getQueryString(name, defaultValue) {
      var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
      var values = window.location.search.substr(1).match(reg);
      if (values != null) {
        return decodeURIComponent(values[2]);
      }
      if (defaultValue != null) {
        return defaultValue;
      }
      return null;
    },
    isDraft() {
      return isEmpty(this.dataUuid) || this.bizData.state == '00';
    },
    isSuspend() {
      return this.bizData.state == '20';
    },
    isOver() {
      return this.bizData.state == '30';
    },
    isTimerStarted() {
      return this.bizData.timerState != 0;
    },
    isTimerTiming() {
      return this.bizData.timerState == 1;
    },
    onActionClick(event, action) {
      if (action.validate) {
        this.dyformWidget.validateFormData(validate => {
          if (!validate) {
            return;
          }
          this[action.id](action);
        });
      } else {
        this[action.id](action);
      }
    },
    onDyformMounted() {
      this.$nextTick(() => {
        setTimeout(() => {
          // 添加材料是否必填验证
          this.addMaterialRequiredValidation();

          // 业务主体字段映射
          let processItemConfig = this.bizData && this.bizData.processItemConfig;
          if (this.hasEntityFieldMapping(processItemConfig)) {
            this.addEntityFieldMappingListener(this.dyformWidget.dyform, processItemConfig);
          }
        }, 500);
      });
    },
    onDyformDataChanged() {
      if (this.$entityIdField) {
        this.handletEntityIdFieldChangeIfRequired();
      }
    },
    // 添加材料是否必填验证
    addMaterialRequiredValidation() {
      const _this = this;
      _this.$nextTick(() => {
        let formConfig = _this.bizData.processItemConfig.formConfig;
        let materialSubformId = formConfig.materialSubformId;
        let materialRequiredField = formConfig.materialRequiredField;
        let materialFileField = formConfig.materialFileField;
        if (isEmpty(materialSubformId) || isEmpty(materialRequiredField) || isEmpty(materialFileField)) {
          return;
        }
        let subform = _this.dyformWidget.dyform.subform || {};
        for (let subformUuid in subform) {
          let subformRecords = subform[subformUuid] || [];
          subformRecords.forEach(record => {
            let materialRequired = record.formData[materialRequiredField];
            let setFieldRequired = (record, materialFileField, materialRequired) => {
              let field = record.getField(materialFileField);
              setTimeout(
                () => {
                  if (field == null) {
                    setFieldRequired(record, materialFileField, materialRequired);
                    return;
                  }
                  if (materialRequired == '1') {
                    record.setFieldRequired(materialFileField, true);
                  } else {
                    record.setFieldRequired(materialFileField, false);
                  }
                },
                field == null ? 500 : 0
              );
            };
            setFieldRequired(record, materialFileField, materialRequired);
          });
        }
      });
    },
    formDataChanged() {
      if (this.$entityIdField) {
        this.handletEntityIdFieldChangeIfRequired();
      }
    },
    addEntityFieldMappingListener(dyform, processItemConfig) {
      let entityIdField = processItemConfig.formConfig.entityIdField;
      let field = dyform.getField(entityIdField);
      if (field) {
        this.$latestEntityIdValue = field.getValue();
        this.$entityIdField = field;
      }
    },
    handletEntityIdFieldChangeIfRequired() {
      const _this = this;
      let entityIdValue = _this.$entityIdField.getValue();
      if (_this.$latestEntityIdValue != entityIdValue) {
        let processItemConfig = _this.bizData.processItemConfig;
        _this.$latestEntityIdValue = entityIdValue;
        if (processItemConfig) {
          let processDefId = _this.processDefId;
          let itemInstUuid = _this.itemInstUuid;
          $axios
            .get(`/proxy/api/biz/process/instance/getEntityFormDataOfMainform`, {
              params: {
                processDefId,
                itemInstUuid,
                entityId: entityIdValue
              }
            })
            .then(({ data: result }) => {
              if (result.data) {
                _this.setEntityFieldMapping(_this.dyformWidget.dyform, result.data, processItemConfig);
              }
            });
        }
      }
    },
    // 是否有业务主体字段映射
    hasEntityFieldMapping(processItemConfig) {
      if (!processItemConfig || !processItemConfig.formConfig || !processItemConfig.formConfig.entityIdField) {
        return false;
      }

      let formConfig = processItemConfig.formConfig || {};
      let fieldMappings = formConfig.fieldMappings || [];
      let materialFieldMappings = formConfig.materialFieldMappings || [];
      let entityMappingIndex = fieldMappings.findIndex(item => item.sourceType == 'entity');
      if (entityMappingIndex == -1) {
        entityMappingIndex = materialFieldMappings.findIndex(item => item.sourceType == 'entity');
      }
      return entityMappingIndex != -1;
    },
    // 设置业务主体字段映射
    setEntityFieldMapping(dyform, dataMap, processItemConfig) {
      let formConfig = processItemConfig.formConfig || {};
      // 办理单主表字段映射
      let fieldMappings = formConfig.fieldMappings || [];
      fieldMappings
        .filter(item => item.sourceType == 'entity')
        .forEach(item => {
          let sourceValue = dataMap[item.sourceField];
          dyform.setFieldValue(item.targetField, sourceValue);
        });

      // 办理单材料从表字段映射
      let materialFieldMappings = formConfig.materialFieldMappings || [];
      let materialSubformId = formConfig.materialSubformId;
      materialFieldMappings
        .filter(item => item.sourceType == 'entity')
        .forEach(item => {
          let sourceValue = dataMap[item.sourceField];
          let subformObject = dyform.subform || {};
          for (let subformUuid in subformObject) {
            let subformRecords = subformObject[subformUuid];
            subformRecords.forEach(record => {
              if (record.formId == materialSubformId) {
                record.setFieldValue(item.targetField, sourceValue);
              }
            });
          }
        });
    },
    setDyformWidget($dyformWidget) {
      this.$dyformWidget = $dyformWidget;
    },
    collectFormData() {
      let data = this.dyformWidget.collectFormData();
      if (data.dataUuid && !(data.dyFormData && data.dyFormData.dataUuid)) {
        this.bizData.dataUuid = data.dataUuid;
      }
      return data.dyFormData ? data.dyFormData : data;
    },
    // 重新加载单据
    reload: function (itemInstUuid) {
      if (!isEmpty(itemInstUuid)) {
        window.location.href = this.addSystemPrefix(`/biz/process/item/instance/view?itemInstUuid=${itemInstUuid}`);
      } else {
        window.location.reload();
      }
    },
    save() {
      const _this = this;
      _this.$loading('保存中');
      let bizData = this.bizData;
      bizData.dyFormData = this.collectFormData();
      $axios
        .post('/proxy/api/biz/process/item/instance/save', bizData)
        .then(({ data }) => {
          if (data.data) {
            this.$message.success('保存成功！', 2, () => {
              let itemInstUuid = data.data;
              _this.reload(itemInstUuid);
            });
          } else {
            _this.$message.error(data.msg || '保存失败！');
          }
          _this.$loading(false);
        })
        .catch(({ response }) => {
          _this.$loading(false);
          _this.$message.error((response.data && response.data.msg) || '保存失败！');
        });
    },
    // 触发提交处理
    triggerSubmit: function () {
      this.submit();
    },
    // 根据事项ID获取流程对象
    getWorkFlowByItemId: function (itemId) {
      const _this = this;
      let workFlowMap = _this.workFlowMap;
      if (workFlowMap[itemId] == null) {
        workFlowMap[itemId] = new WorkFlowInteraction();
        workFlowMap[itemId]._tempData2WorkData();
      }
      return workFlowMap[itemId];
    },
    // 收集流程对象数据
    collectWorkData: function () {
      const _this = this;
      let workFlowMap = _this.workFlowMap;
      for (let key in workFlowMap) {
        workFlowMap[key]._tempData2WorkData();
      }
    },
    // 获取流程数据Map<事项ID, 流程数据>
    getWorkDataMap: function () {
      const _this = this;
      let workDataMap = {};
      let workFlowMap = _this.workFlowMap;
      for (let key in workFlowMap) {
        workDataMap[key] = workFlowMap[key].getWorkData();
      }
      return workDataMap;
    },
    addExtraParams(params) {
      if (isEmpty(params)) {
        return;
      }
      for (let key in params) {
        this.bizData.extraParams[key] = params[key];
      }
    },
    // 提交
    submit: function ($evt) {
      const _this = this;
      _this.$loading('提交中');
      if ($evt) {
        _this.addExtraParams($evt.eventParams);
      }
      let bizData = _this.bizData;
      bizData.dyFormData = _this.collectFormData();
      _this.collectWorkData();
      let interactionTaskDataMap = _this.getWorkDataMap();
      bizData.interactionTaskDataMap = interactionTaskDataMap;
      $axios
        .post('/proxy/api/biz/process/item/instance/submit', bizData)
        .then(({ data }) => {
          if (data.code == -5002) {
            let wfOptions = {};
            wfOptions.callback = _this.triggerSubmit;
            wfOptions.callbackContext = _this;
            wfOptions.workFlow = _this.getWorkFlowByItemId(result.data.data.itemId);
            _this.errorHandler.handle(result, null, null, wfOptions);
          } else {
            _this.pageContext.emitCrossTabEvent('item:detail:change', { message: '提交成功！' });
            window.close();
          }
          _this.$loading(false);
        })
        .catch(({ response }) => {
          _this.$loading(false);
          let wfOptions = {};
          wfOptions.callback = _this.triggerSubmit;
          wfOptions.callbackContext = _this;
          if (response.data && response.data.data && response.data.data.data) {
            wfOptions.workFlow = _this.getWorkFlowByItemId(response.data.data.data.itemId);
            _this.errorHandler.handle(response, null, null, wfOptions);
          } else {
            _this.$message.error(response.data.msg || '提交失败！');
          }
        });
    },
    getFieldValue(fieldName) {
      return this.dyformWidget.dyform.formData[fieldName];
    },
    setFieldValue(fieldName, fieldValue) {
      this.dyformWidget.dyform.formData[fieldName] = fieldValue;
    },
    // 开始计时
    startTimer: function () {
      const _this = this;
      let bizData = _this.bizData;
      let processItemConfig = bizData.processItemConfig || {};
      let formConfig = processItemConfig.formConfig || {};
      let timeLimitField = formConfig.timeLimitField;
      let timeLimit = null;
      try {
        timeLimit = _this.getFieldValue(timeLimitField);
        if (!timeLimit) {
          _this.showChooseTimeLimitDialog(processItemConfig);
          return;
        }
        timeLimit = parseInt(timeLimit);
      } catch (e) {
        console.error(e);
      }
      if (isNaN(timeLimit) || timeLimit < 0) {
        _this.$message.error('请输入有效的办理时限！');
        return;
      }

      bizData.dyFormData = _this.collectFormData();
      $axios
        .post('/proxy/api/biz/process/item/instance/startTimer', bizData)
        .then(({ data }) => {
          if (data.data) {
            _this.$message.success('计时器已启动！', 2, () => {
              _this.reload(data.data);
            });
          }
        })
        .catch(({ response }) => {
          _this.$message.error(response.data.msg);
        });
    },
    showChooseTimeLimitDialog(processItemConfig) {
      this.timeLimitModalVisible = true;
      if (isEmpty(this.timeLimitOptions)) {
        this.handleTimeLimitSearch(processItemConfig);
      }
    },
    handleTimeLimitSearch(processItemConfig) {
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'bizItemDefinitionFacadeService',
          queryMethod: 'listTimeLimitDataSelectData',
          itemDefId: processItemConfig.itemDefId,
          itemCode: processItemConfig.itemCode,
          timeLimitType: processItemConfig.timeLimitType
        })
        .then(({ data }) => {
          if (data.results) {
            this.timeLimitOptions = data.results;
          }
        });
    },
    onChooseTimeLimitOk() {
      const _this = this;
      this.$refs.timeLimitForm.validate(valid => {
        if (valid) {
          let processItemConfig = _this.bizData.processItemConfig || {};
          let formConfig = processItemConfig.formConfig || {};
          let timeLimitField = formConfig.timeLimitField;
          _this.setFieldValue(timeLimitField, parseInt(_this.timeLimitFormData.timeLimit));
          _this.timeLimitModalVisible = false;
          _this.startTimer();
        }
      });
    },
    // 暂停计时
    pauseTimer() {
      const _this = this;
      let bizData = _this.bizData;
      bizData.dyFormData = _this.collectFormData();
      $axios
        .post('/proxy/api/biz/process/item/instance/pauseTimer', bizData)
        .then(({ data }) => {
          if (data.data) {
            _this.$message.success('计时器已暂停！', 2, () => {
              _this.reload(data.data);
            });
          }
        })
        .catch(({ response }) => {
          _this.$message.error(response.data.msg);
        });
    },
    // 恢复计时
    resumeTimer() {
      const _this = this;
      let bizData = _this.bizData;
      bizData.dyFormData = _this.collectFormData();
      $axios
        .post('/proxy/api/biz/process/item/instance/resumeTimer', bizData)
        .then(({ data }) => {
          if (data.data) {
            _this.$message.success('计时器已恢复！', 2, () => {
              _this.reload(data.data);
            });
          }
        })
        .catch(({ response }) => {
          _this.$message.error(response.data.msg);
        });
    },
    cancel($evt) {
      const _this = this;
      _this.$loading('撤销中');
      if ($evt) {
        _this.addExtraParams($evt.eventParams);
      }
      let bizData = _this.bizData;
      bizData.dyFormData = _this.collectFormData();
      $axios
        .post('/proxy/api/biz/process/item/instance/cancel', bizData)
        .then(({ data }) => {
          if (data.data) {
            _this.pageContext.emitCrossTabEvent('item:detail:change', { message: '已撤销！' });
            window.close();
          }
          _this.$loading(false);
        })
        .catch(({ response }) => {
          _this.$loading(false);
          _this.$message.error(response.data.msg);
        });
    },
    complete: function () {
      const _this = this;
      _this.$loading('办结中');
      let bizData = _this.bizData;
      bizData.dyFormData = _this.collectFormData();
      $axios
        .post('/proxy/api/biz/process/item/instance/complete', bizData)
        .then(({ data }) => {
          if (data.data) {
            _this.pageContext.emitCrossTabEvent('item:detail:change', { message: '已办结！' });
            window.close();
          }
          _this.$loading(false);
        })
        .catch(({ response }) => {
          _this.$loading(false);
          _this.$message.error(response.data.msg);
        });
    },
    suspend: function () {
      const _this = this;
      let bizData = _this.bizData;
      bizData.dyFormData = _this.collectFormData();
      $axios
        .post('/proxy/api/biz/process/item/instance/suspend', bizData)
        .then(({ data }) => {
          if (data.data) {
            _this.pageContext.emitCrossTabEvent('item:detail:change', { message: '已挂起！' });
            window.close();
          }
        })
        .catch(({ response }) => {
          _this.$message.error(response.data.msg);
        });
    },
    resume: function () {
      const _this = this;
      let bizData = _this.bizData;
      bizData.dyFormData = _this.collectFormData();
      $axios
        .post('/proxy/api/biz/process/item/instance/resume', bizData)
        .then(({ data }) => {
          if (data.data) {
            _this.pageContext.emitCrossTabEvent('item:detail:change', { message: '已恢复！' });
            window.close();
          }
        })
        .catch(({ response }) => {
          _this.$message.error(response.data.msg);
        });
    },
    viewProcess() {
      this.processModalVisible = true;
      if (isEmpty(this.itemOperations)) {
        this.listProcessItemOperations();
      }
    },
    listProcessItemOperations() {
      $axios
        .get(`/proxy/api/biz/process/item/instance/listProcessItemOperationByUuid?itemInstUuid=${this.itemInstUuid}`)
        .then(({ data }) => {
          if (data.data) {
            this.itemOperations = data.data;
          }
        });
    },
    printForm() {
      this.dyformWidget.print();
    },
    loadIncludeItemInstances() {
      const _this = this;
      if (!_this.bizData.processItemConfig.formConfig.includeItemPlaceHolder) {
        return;
      }

      this.getTableWidget().then(widget => {
        let $el = document.querySelector(`[w-code='${_this.bizData.processItemConfig.formConfig.includeItemPlaceHolder}']`);
        if ($el && $el.parentNode) {
          let divEl = document.createElement('div');
          $el.parentNode.appendChild(divEl);
          widget.$mount(divEl);
        }
      });
    },
    getTableWidget() {
      const _this = this;
      return _this
        .getTableDefinition()
        .then(widgetDefinition => {
          // 创建构造器
          let WidgetTable = Vue.extend({
            template: '<WidgetTable ref="widgetTable" :widget="widget" @beforeLoadData="beforeLoadData"></WidgetTable>', //
            provide() {
              return {
                pageContext: _this.pageContext,
                namespace: _this.namespace,
                vPageState: _this.vPageState,
                $pageJsInstance: _this.$pageJsInstance,
                locale: _this.locale
              };
            },
            inject: {},
            data: function () {
              return { widget: widgetDefinition };
            },
            methods: {
              beforeLoadData() {
                let tableWidget = this.$refs.widgetTable;
                let dataSource = tableWidget.getDataSourceProvider();
                if (_this.bizData.itemId.indexOf(';') != -1) {
                  dataSource.addParam('belongItemInstUuid', _this.bizData.itemInstUuid);
                } else {
                  dataSource.addParam('parentItemInstUuid', _this.bizData.itemInstUuid);
                }
              }
            }
          });
          return new WidgetTable();
        })
        .catch(res => {
          _this.$message.error('业务事项办件实例数据加载失败！');
        });
    },
    getTableDefinition() {
      let widgetTableId = 'caawwofxACcRnqUQyBTbWnfkChwUfCJi';
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'appContextService',
            methodName: 'getAppWidgetDefinitionById',
            args: JSON.stringify([widgetTableId, false])
          })
          .then(({ data: { data = {} } }) => {
            if (data.definitionJson) {
              resolve(JSON.parse(data.definitionJson));
            } else {
              reject(data);
            }
          })
          .catch(res => {
            reject(res);
          });
      });
    }
  }
};
</script>

<style lang="less" scoped>
.item-view-header {
  padding: 0 15px;
  background: var(--w-primary-color);

  .title {
    color: var(--w-white);
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    line-height: 60px;
    font-size: var(--w-font-size-lg);
    display: inline-block;
    padding-left: var(--w-padding-md);
    font-weight: bold;

    h1 {
      color: #fff;
      font-size: var(--w-font-size-3xl);
      padding-right: var(--w-padding-md);
    }
  }
}
.item-view-content {
  background: linear-gradient(to bottom, var(--w-primary-color), var(--w-widget-page-layout-bg-color) 40%);
  padding: var(--w-padding-xs) var(--w-padding-md);
}
.item-action-container {
  text-align: right;
}
</style>
