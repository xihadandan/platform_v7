<template>
  <div>
    <a-form-model ref="form" :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="名称">
            <a-input v-model="widget.configuration.title" @change="widget.title = widget.configuration.title">
              <template slot="addonAfter">
                <a-switch :checked="widget.configuration.titleHidden !== true" @change="onChangeTitleHidden"></a-switch>
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
            :options="widget.configuration.options"
            :dataSourceFieldOptions="dataSourceFieldOptions"
            :widget="widget"
          ></OptionSourceConfiguration>
          <a-form-model-item label="允许取消选中">
            <a-switch v-model="widget.configuration.cancleChecked" />
          </a-form-model-item>
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
          <a-collapse :bordered="false" expandIconPosition="right">
            <a-collapse-panel key="widget-style" header="组件样式">
              <a-form-model-item label="风格样式">
                <a-select :options="styleTypeOptions" :style="{ width: '100%' }" v-model="widget.configuration.styleType"></a-select>
              </a-form-model-item>
              <a-form-model-item
                label="选中效果"
                :label-col="{ span: 6 }"
                :wrapper-col="{ span: 15 }"
                v-show="widget.configuration.styleType == 'button'"
              >
                <a-select
                  :options="buttonSelectedStyle"
                  :style="{ width: '100%' }"
                  v-model="widget.configuration.buttonSelectedStyle"
                ></a-select>
              </a-form-model-item>
              <template v-if="widget.configuration.styleType == 'radio'">
                <a-form-model-item label="排布方式">
                  <a-select :options="layoutOptions" :style="{ width: '100%' }" v-model="widget.configuration.layout"></a-select>
                </a-form-model-item>
                <a-form-model-item label="对齐方式" v-show="widget.configuration.layout == 'horizontal'">
                  <a-select :options="alignTypeOptions" :style="{ width: '100%' }" v-model="widget.configuration.alignType"></a-select>
                </a-form-model-item>
                <a-form-model-item
                  label="单选宽度"
                  v-show="widget.configuration.layout == 'horizontal' && widget.configuration.alignType == 'fixedWidth'"
                >
                  <a-input-number v-model="widget.configuration.itemWidth" />
                </a-form-model-item>
              </template>
            </a-collapse-panel>
          </a-collapse>
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
import OptionSourceConfiguration from '../../widget-checkbox/configuration/option-source-configuration.vue';
import editFormElementConfigureMixin from '../../editFormElementConfigure.mixin';

export default {
  name: 'WidgetRadioConfiguration',
  mixins: [editFormElementConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },

  data() {
    return {
      dataSourceFieldOptions: [
        {
          label: '值字段',
          value: 'dataSourceValueColumn'
        },
        {
          label: '展示字段',
          value: 'dataSourceLabelColumn'
        }
      ],
      layoutOptions: [
        { label: '水平', value: 'horizontal' },
        { label: '垂直', value: 'vertical' }
      ],
      styleTypeOptions: [
        { label: '单选框风格', value: 'radio' },
        { label: '按钮风格', value: 'button' }
      ],
      alignTypeOptions: [
        { label: '固定宽度', value: 'fixedWidth' },
        { label: '固定间距', value: 'fixedSpace' }
      ],
      buttonSelectedStyle: [
        { label: '填充底色', value: 'solid' },
        { label: '边框高亮', value: 'outline' }
      ]
    };
  },
  filters: {},
  beforeCreate() {},
  components: { OptionSourceConfiguration },
  computed: {},
  created() {},
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
      title: '单选框',
      code: '',
      styleType: 'radio',
      buttonSelectedStyle: 'solid',
      alignType: 'fixedWidth',
      itemWidth: 100,
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label',
      options: {
        type: 'selfDefine',
        dataSourceLabelColumn: '',
        dataSourceValueColumn: '',
        dataSourceId: '',
        defineOptions: [],
        dataDictionaryUuid: ''
      }
    };
  }
};
</script>
