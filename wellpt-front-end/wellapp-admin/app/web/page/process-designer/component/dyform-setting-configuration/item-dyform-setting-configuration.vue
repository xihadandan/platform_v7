<template>
  <div :class="{ 'configuration-disabled': disabled }">
    <div v-if="disabled" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 1"></div>
    <a-form-model-item>
      <template slot="label">
        <a-space>
          表单设置
          <a-popover>
            <template slot="content">用于内置流转系统的表单设置</template>
            <a-icon type="info-circle" />
          </a-popover>
        </a-space>
      </template>
      <a-switch v-model="definition.enabledDyformSetting" @change="enabledDyformSettingChanged" :disabled="disabled"></a-switch>
    </a-form-model-item>
    <DyformBasicConfiguration
      v-if="enabledDyformSetting"
      :widget="definition.widgetDyformSetting"
      :designer="designer"
      :editRule="formSettingEditRule"
    >
      <template slot="selectForm">
        <div></div>
      </template>
    </DyformBasicConfiguration>
  </div>
</template>

<script>
import DyformBasicConfiguration from '@pageAssembly/app/web/widget/widget-dyform-setting/configuration/dyform-basic-configuration.vue';
import WidgetIconLib from '@pageAssembly/app/web/widget/widget-icon-lib/widget-icon-lib.vue';
import WidgetEventHandler from '@pageAssembly/app/web/widget/commons/widget-event-handler.vue';
import WidgetDesignModal from '@pageAssembly/app/web/widget/commons/widget-design-modal.vue';
import JsModuleSelect from '@pageAssembly/app/web/widget/commons/js-module-select';
import JsHookSelect from '@pageAssembly/app/web/widget/commons/js-hook-select.vue';
import WidgetDesignDrawer from './dyform-setting-drawer.vue';
import { generateId } from '@framework/vue/utils/util';
import { isEmpty } from 'lodash';
export default {
  props: {
    definition: Object,
    disabled: {
      type: Boolean,
      default: false
    },
    closeOpenDrawer: {
      type: Boolean,
      default: true
    }
  },
  components: { DyformBasicConfiguration },
  inject: ['designer', 'pageContext'],
  provide() {
    return {
      appId: '',
      designWidgetTypes: [],
      draggableConfig: {}
    };
  },
  data() {
    const _this = this;
    if (EASY_ENV_IS_BROWSER) {
      _this.registerComponentOfWidgetDyformSetting();
      _this.widgetDesignerPolyfill();
    }
    if (!_this.definition.widgetDyformSetting) {
      _this.initWidgetDyformSetting();
    }
    return {
      formSettingEditRule: {
        title: {
          hidden: true
        },
        button: {}
      }
    };
  },
  computed: {
    enabledDyformSetting() {
      return this.definition.enabledDyformSetting && !isEmpty(this.definition.widgetDyformSetting);
    }
  },
  watch: {
    'definition.formUuid': function (newValue) {
      let widgetDyformSetting = this.definition.widgetDyformSetting;
      if (widgetDyformSetting && widgetDyformSetting.configuration) {
        widgetDyformSetting.configuration.formUuid = newValue;
        widgetDyformSetting.configuration.formName = this.definition.formName;
      }
    }
  },
  methods: {
    registerComponentOfWidgetDyformSetting() {
      const _this = this;
      let components = {
        WidgetDesignDrawer,
        WidgetIconLib,
        WidgetEventHandler,
        WidgetDesignModal,
        JsModuleSelect,
        JsHookSelect
      };
      for (let componentName in components) {
        if (!_this.isComponentRegistered(componentName)) {
          Vue.component(componentName, components[componentName]);
        }
      }
    },
    isComponentRegistered(componentName) {
      return Vue.options.components[componentName];
    },
    widgetDesignerPolyfill() {
      const _this = this;

      if (!_this.designer.emitEvent) {
        _this.designer.emitEvent = _this.pageContext.emitEvent;
      }
      if (!_this.designer.handleEvent) {
        _this.designer.handleEvent = _this.pageContext.handleEvent;
      }
      if (!_this.designer.widgets) {
        _this.designer.widgets = [];
      }
      if (!_this.designer.widgetIdMap) {
        _this.designer.widgetIdMap = {};
      }
      if (!_this.vueInstance) {
        _this.designer.vueInstance = $app;
      }
    },
    getJsModule() {
      return { key: 'BizProcessItemWidgetDyformSetDevelopment', label: '业务事项办件——表单设置' };
    },
    generateDyformSettingButton(id, title, code, visibleType, formStateConditions, styleType, eventParams = []) {
      let jsModule = this.getJsModule();
      return {
        id,
        code,
        title,
        visibleType: visibleType || 'visible-condition',
        required: true,
        visibleCondition: {
          defineCondition: { cons: [], operator: 'and' },
          enableDefineCondition: false,
          formStateConditions,
          userRoleConditions: []
        },
        style: {
          type: styleType || 'primary',
          textHidden: false
        },
        eventHandler: [
          {
            id: generateId(),
            name: title,
            trigger: 'click',
            actionType: 'jsFunction',
            jsFunction: `${jsModule.key}.${code}`,
            eventParams
          }
        ]
      };
    },
    getButtons() {
      return [
        this.generateDyformSettingButton(generateId(), '保存', 'save', 'visible-condition', ['createForm', 'edit'], 'primary'),
        this.generateDyformSettingButton(generateId(), '提交', 'submit', 'visible-condition', ['createForm', 'edit', 'label'], 'primary', [
          {
            id: generateId(),
            paramKey: 'syncFlowBotId',
            remark: '同步流程集成的流程单据转换ID，用于申报表单与审批表单隔离的数据同步'
          },
          // {
          //   id: generateId(),
          //   paramKey: 'submitFlow',
          //   paramValue: 'true',
          //   remark: '是否提交流程集成已存在的流程实例，true/false'
          // },
          {
            id: generateId(),
            paramKey: 'validate',
            paramValue: 'true',
            remark: '是否检验表单'
          }
        ]),
        this.generateDyformSettingButton(generateId(), '开始计时', 'startTimer', 'hidden', ['edit', 'label'], 'primary'),
        this.generateDyformSettingButton(generateId(), '暂停计时', 'pauseTimer', 'hidden', ['edit', 'label'], 'primary'),
        this.generateDyformSettingButton(generateId(), '恢复计时', 'resumeTimer', 'hidden', ['edit', 'label'], 'primary'),
        this.generateDyformSettingButton(generateId(), '撤销', 'cancel', 'visible-condition', ['label'], 'primary', [
          {
            id: generateId(),
            paramKey: 'syncFlowBotId',
            remark: '同步流程集成的流程单据转换ID，用于申报表单与审批表单隔离的数据同步'
          },
          {
            id: generateId(),
            paramKey: 'validate',
            paramValue: 'true',
            remark: '是否检验表单'
          }
        ]),
        this.generateDyformSettingButton(generateId(), '办结', 'complete', 'visible-condition', ['label'], 'primary'),
        this.generateDyformSettingButton(generateId(), '挂起', 'suspend', 'hidden', ['label'], 'primary'),
        this.generateDyformSettingButton(generateId(), '恢复', 'resume', 'hidden', ['label'], 'primary'),
        this.generateDyformSettingButton(generateId(), '办理过程', 'viewProcess', 'visible-condition', ['edit', 'label'], 'default'),
        this.generateDyformSettingButton(generateId(), '查看流程', 'viewFlow', 'visible-condition', ['edit', 'label'], 'default'),
        this.generateDyformSettingButton(generateId(), '打印表单', 'printForm', 'visible-condition', ['edit', 'label'], 'default')
      ];
    },
    initWidgetDyformSetting() {
      const _this = this;
      let jsModule = _this.getJsModule();
      let generateWidgetDyformSettingConfiguration = () => {
        return {
          id: generateId(),
          wtype: 'WidgetDyformSetting',
          title: '业务流程 - 表单',
          configuration: {
            // authConfig: {
            //   enable: false,
            //   allowAccess: ['OWN'],
            //   allowAccessRoles: []
            // },
            title: undefined,
            titleIcon: undefined,
            formUuid: _this.definition.formUuid,
            formName: _this.definition.formName,
            jsModules: [jsModule],
            buttonPosition: 'top', // disable / top / bottom
            enableStateForm: false, // 按状态设置表单
            editStateFormUuid: undefined, //编辑表单
            labelStateFormUuid: undefined, //查阅表单
            useRequestForm: true, // 使用请求参数中的表单定义UUID
            titleVisible: true, //是否展示标题栏
            editStateTitle: undefined, // 编辑状态的标题
            labelStateTitle: undefined, // 查阅状态的标题
            // editStateTitleIcon:undefined,
            // labelStateTitleIcon:''
            titleRenderScript: undefined, // 标题渲染脚本
            formElementRules: [],
            labelStateFormElementRules: [],
            editStateFormElementRules: [],
            button: {
              buttons: _this.getButtons(),
              buttonGroup: {
                type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
                groups: [
                  // 固定分组
                  // {name:,buttonIds:[]}
                ],
                dynamicGroupName: '更多', //动态分组名称
                dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
              },

              buttonAlign: 'center'
            }
          }
        };
      };

      this.$set(this.definition, 'widgetDyformSetting', generateWidgetDyformSettingConfiguration());
    },
    enabledDyformSettingChanged() {
      const _this = this;
      if (_this.definition.enabledDyformSetting) {
        if (isEmpty(_this.definition.widgetDyformSetting)) {
          _this.initWidgetDyformSetting();
        }
      } else {
        _this.definition.widgetDyformSetting = {};
      }
    }
  }
};
</script>

<style lang="less" scoped>
.configuration-disabled {
  position: relative;

  ::v-deep .ant-select-selection,
  ::v-deep .ant-switch-checked,
  ::v-deep input {
    background: var(--w-select-selection-bg-disabled);
    border-color: var(--w-select-border-color-disabled);
  }
  ::v-deep button,
  ::v-deep .anticon-setting,
  ::v-deep .anticon-delete {
    display: none;
  }
}
</style>
