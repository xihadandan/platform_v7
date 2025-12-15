<template>
  <div>
    <a-form-model ref="form" :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="名称">
            <a-input v-model="widget.configuration.title" @change="widget.title = widget.configuration.title">
              <template slot="addonAfter">
                <a-switch :checked="widget.configuration.titleHidden !== true" @change="onChangeTitleHidden" title="显示名称"></a-switch>
                <WI18nInput
                  v-show="widget.configuration.titleHidden !== true"
                  :widget="widget"
                  :designer="designer"
                  :code="widget.id"
                  v-model="widget.configuration.title"
                />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="编码">
            <a-input v-model="widget.configuration.code" />
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              文字提示
              <a-checkbox v-model="widget.configuration.enableTooltip" />
            </template>
            <template v-if="widget.configuration.enableTooltip">
              <a-radio-group size="small" v-model="widget.configuration.tooltipDisplayType" button-style="solid">
                <a-radio-button value="popover">气泡卡片</a-radio-button>
                <a-radio-button value="tooltip">气泡浮层</a-radio-button>
              </a-radio-group>
              <a-input placeholder="文字提示内容" style="width: 100%; float: right" v-model="widget.configuration.tooltip" allow-clear>
                <template slot="addonAfter">
                  <WI18nInput :widget="widget" :designer="designer" :code="widget.id + '_tooltip'" v-model="widget.configuration.tooltip" />
                </template>
              </a-input>
            </template>
          </a-form-model-item>
          <a-form-model-item label="选择模式">
            <a-select
              :options="[
                { label: '单选', value: 'default' },
                { label: '多选', value: 'multiple' }
              ]"
              v-model="widget.configuration.selectMode"
            ></a-select>
          </a-form-model-item>

          <a-form-model-item label="是否可搜索">
            <a-switch v-model="widget.configuration.showSearch" />
          </a-form-model-item>
          <a-form-model-item label="默认状态">
            <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState" button-style="solid">
              <a-radio-button value="edit">可编辑</a-radio-button>
              <a-radio-button value="unedit">不可编辑</a-radio-button>
              <!-- <a-radio-button value="hidden">隐藏</a-radio-button> -->
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="不可编辑状态" class="item-lh" v-if="widget.configuration.defaultDisplayState == 'unedit'">
            <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState" button-style="solid">
              <a-radio-button value="label">纯文本</a-radio-button>
              <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
            </a-radio-group>
          </a-form-model-item>

          <OptionSourceConfiguration
            :widget="widget"
            :designer="designer"
            :options="widget.configuration.options"
            :dataSourceFieldOptions="currentOptionSourceDatasourceField"
          ></OptionSourceConfiguration>
          <DefaultVisibleConfiguration compact :designer="designer" :configuration="widget.configuration" :widget="widget">
            <template slot="extraAutoCompleteSelectGroup">
              <a-select-opt-group>
                <span slot="label">
                  <a-icon type="code" />
                  表单数据
                </span>
                <a-select-option v-for="opt in formVarOptions" :key="opt.value" :title="opt.label">{{ opt.label }}</a-select-option>
              </a-select-opt-group>
            </template>
          </DefaultVisibleConfiguration>
        </a-tab-pane>
        <a-tab-pane key="2" tab="校验规则" :forceRender="true">
          <ValidateRuleConfiguration
            ref="validateRef"
            :widget="widget"
            :trigger="true"
            :required="true"
            :unique="true"
            :regExp="true"
            :validatorFunction="true"
          ></ValidateRuleConfiguration>
        </a-tab-pane>

        <a-tab-pane key="3" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import OptionSourceConfiguration from './option-source-configuration.vue';
import ValidateRuleConfiguration from '@dyformWidget/commons/validate-rule-configuration.vue';
import editFormElementConfigureMixin from '../../editFormElementConfigure.mixin';

export default {
  name: 'WidgetSelectConfiguration',
  mixins: [editFormElementConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },

  data() {
    let dataSourceFieldOptions = [
      {
        label: '值字段',
        value: 'dataSourceValueColumn'
      },
      {
        label: '展示字段',
        value: 'dataSourceLabelColumn'
      },
      {
        label: '唯一值字段',
        value: 'dataSourceKeyColumn'
      },
      {
        label: '父级字段',
        value: 'dataSourceParentColumn'
      },
      {
        label: '扩展显示内容（辅助列）',
        value: 'dataSourceExtendColumn',
        allowClear: true
      },
      {
        label: '组字段',
        value: 'groupColumn'
      }
    ];
    return { dataSourceFieldOptions };
  },
  filters: {},
  beforeCreate() {},
  components: { OptionSourceConfiguration, ValidateRuleConfiguration },
  computed: {
    currentOptionSourceDatasourceField() {
      if (this.widget.configuration.type === 'select') {
        return this.dataSourceFieldOptions.slice(0, 2);
      } else if (this.widget.configuration.type === 'select-group') {
        let arr = this.dataSourceFieldOptions.slice(0, 2);
        arr.push(this.dataSourceFieldOptions[4]);
        return arr;
      } else {
        return this.dataSourceFieldOptions.slice(0, 5);
      }
    }
  },
  created() {
    // 已配组件增加
    if (!this.widget.configuration.hasOwnProperty('domEvents')) {
      this.$set(this.widget.configuration, 'domEvents', [
        {
          id: 'onChange',
          title: '值变更时候触发',
          codeSource: 'codeEditor',
          jsFunction: undefined,
          widgetEvent: [],
          customScript: undefined // 事件脚本
        }
      ]);
    }
  },
  methods: {
    // 生成后端的功能元素数据
    getFunctionElements(widget) {
      let functionElements = {};
      if (widget.configuration.options.type == 'dataDictionary' && widget.configuration.options.dataDictionaryUuid) {
        functionElements[widget.id] = [
          {
            id: widget.configuration.options.dataDictionaryUuid,
            name: '数据字典_' + widget.configuration.options.dataDictionaryUuid,
            functionType: 'cdDataDictionary',
            ref: true
          }
        ];
      } else if (widget.configuration.options.type == 'dataSource' && widget.configuration.options.dataSourceId) {
        functionElements[widget.id] = [
          {
            id: widget.configuration.options.dataSourceId,
            name: '数据仓库_' + widget.configuration.options.dataSourceId,
            functionType: 'dataStoreDefinition',
            ref: true
          }
        ];
      }
      return functionElements;
    }
  },
  mounted() {},
  updated() {},
  configuration() {
    return {
      title: '下拉框',
      code: '',
      type: 'select',
      selectMode: 'default',
      showSearch: false,
      selectedSortable: false,
      isDatabaseField: true,
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label',
      options: {
        type: 'selfDefine',
        dataSourceLabelColumn: '',
        dataSourceValueColumn: '',
        dataSourceId: '',
        defineOptions: [], // 常量备选项
        dataDictionaryUuid: '',
        defaultCondition: '',
        dataSourceLoadEveryTime: false,
        dataSourceDataMapping: []
      },
      domEvents: [
        {
          id: 'onChange',
          title: '值变更时候触发',
          codeSource: 'codeEditor',
          jsFunction: undefined,
          widgetEvent: [],
          customScript: undefined // 事件脚本
        }
      ]
    };
  }
};
</script>
