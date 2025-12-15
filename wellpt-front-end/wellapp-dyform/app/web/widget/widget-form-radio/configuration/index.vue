<template>
  <div>
    <a-form-model
      ref="form"
      :model="widget.configuration"
      :rules="rules"
      labelAlign="left"
      :wrapper-col="{ style: { textAlign: 'right' } }"
    >
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <!-- <a-form-model-item label="切换组件">
            <a-select
              :options="[{ label: '复选框', value: 'WidgetFormCheckbox' }]"
              @change="$evt => (widget.wtype = $evt)"
              :style="{ width: '100%' }"
            />
          </a-form-model-item> -->
          <FieldNameInput :widget="widget" />
          <FieldCodeInput :widget="widget" />
          <FieldLengthInput :widget="widget" />
          <option-source-configuration
            :widget="widget"
            :designer="designer"
            :options="widget.configuration.options"
            @columnIndexOptionChange="onDsColumnIndexOptionChange"
          />
          <!-- 默认值 -->
          <!-- 自定义和字典使用下拉框选择默认值 -->
          <template v-if="widget.configuration.options.type == 'selfDefine' || widget.configuration.options.type == 'dataDictionary'">
            <FieldDefaultValue :configuration="widget.configuration" dataType="options" :options="selectedOptions" :multiple="false" />
            <div v-show="false">{{ selectedOptionsChange }}</div>
          </template>
          <template v-else>
            <FieldDefaultValue :configuration="widget.configuration" :selectVariable="true" />
          </template>
          <a-collapse :bordered="false" expandIconPosition="right">
            <a-collapse-panel key="component_mode_properties" header="组件模式属性">
              <a-form-model-item label="默认状态" class="item-lh">
                <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState" button-style="solid">
                  <a-radio-button value="edit">可编辑</a-radio-button>
                  <a-radio-button value="unedit">不可编辑</a-radio-button>
                  <a-radio-button value="hidden">隐藏</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
            </a-collapse-panel>
            <a-collapse-panel key="edit_mode_properties" header="编辑模式属性">
              <a-form-model-item label="取消选中" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.cancleChecked" />
              </a-form-model-item>
              <a-form-model-item>
                <template #label>
                  <span>显示值字段</span>
                  <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                    <template #title>
                      <div>当选项选择后，显示值将一并更新至该字段。可配置隐藏字段，用于数据提交、存储。</div>
                    </template>
                    <a-icon type="exclamation-circle" />
                  </a-tooltip>
                </template>
                <a-select
                  allowClear
                  :options="inputFieldOptions"
                  v-model="widget.configuration.displayValueField"
                  :getPopupContainer="getPopupContainerByPs()"
                  :dropdownClassName="getDropdownClassName()"
                ></a-select>
              </a-form-model-item>
            </a-collapse-panel>
            <a-collapse-panel key="un_edit_mode_properties" header="不可编辑模式属性">
              <a-form-model-item label="不可编辑状态" class="item-lh">
                <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState" button-style="solid">
                  <a-radio-button value="label">纯文本</a-radio-button>
                  <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
            </a-collapse-panel>
            <a-collapse-panel key="other_properties" header="其他属性">
              <a-form-model-item label="应用于">
                <FieldApplySelect v-model="widget.configuration.applyToDatas" />
              </a-form-model-item>
              <a-form-model-item label="描述" :wrapper-col="{ style: { marginTop: '2px' } }">
                <a-textarea :rows="4" placeholder="请输入内容" v-model="widget.configuration.note" :maxLength="200" />
                <span class="textLengthShow">{{ widget.configuration.note | textLengthFilter }}/200</span>
              </a-form-model-item>
            </a-collapse-panel>
            <a-collapse-panel key="widget-style" header="组件样式">
              <a-form-model-item label="风格样式">
                <a-select
                  :options="styleTypeOptions"
                  :style="{ width: '100%' }"
                  v-model="widget.configuration.styleType"
                  :getPopupContainer="getPopupContainerByPs()"
                ></a-select>
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
                  :getPopupContainer="getPopupContainerByPs()"
                ></a-select>
              </a-form-model-item>
              <template v-if="widget.configuration.styleType == 'radio'">
                <a-form-model-item label="排布方式">
                  <a-select
                    :options="layoutOptions"
                    :style="{ width: '100%' }"
                    v-model="widget.configuration.layout"
                    :getPopupContainer="getPopupContainerByPs()"
                  ></a-select>
                </a-form-model-item>
                <a-form-model-item label="对齐方式" v-show="widget.configuration.layout == 'horizontal'">
                  <a-select
                    :options="alignTypeOptions"
                    :style="{ width: '100%' }"
                    v-model="widget.configuration.alignType"
                    :getPopupContainer="getPopupContainerByPs()"
                  ></a-select>
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
        <a-tab-pane key="2" tab="校验规则">
          <ValidateRuleConfiguration :widget="widget" :unique="true"></ValidateRuleConfiguration>
        </a-tab-pane>
        <a-tab-pane key="3" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer">
            <template slot="eventParamValueHelpSlot"><FormEventParamHelp /></template>
          </WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>

<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import { layoutOptions } from '../../commons/constant';
import FormEventParamHelp from '../../commons/form-event-param-help.vue';

export default {
  name: 'WidgetFormRadioConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  components: { FormEventParamHelp },
  data() {
    let dataSourceFieldOptions = [
      {
        label: '值字段',
        value: 'dataSourceValueColumn'
      },
      {
        label: '展示字段',
        value: 'dataSourceLabelColumn'
      }
    ];

    return {
      dataSourceFieldOptions,
      layoutOptions,
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
      ],
      rules: {
        name: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true },
        code: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true }
      },
      columnIndexOptions: [],
      selectedOptions: []
    };
  },
  filters: {
    textLengthFilter(text) {
      return text ? text.length : 0;
    }
  },
  computed: {
    selectedOptionsChange() {
      let options = false;
      if (this.widget.configuration.hasDefaultValue) {
        if (this.widget.configuration.options.type == 'selfDefine' || this.widget.configuration.options.defineOptions) {
          options = true;
          this.getSelectedOptions();
        } else if (this.widget.configuration.options.type == 'dataDictionary' && this.widget.configuration.options.dataDictionaryUuid) {
          options = true;
          this.getSelectedOptions();
        }
      }
      return options;
    }
  },
  methods: {
    onDsColumnIndexOptionChange(opt) {
      this.columnIndexOptions = opt;
    },
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
    },
    getSelectedOptions() {
      if (this.widget.configuration.options.type == 'selfDefine') {
        this.selectedOptions = this.widget.configuration.options.defineOptions;
      } else if (this.widget.configuration.options.type == 'dataDictionary' && this.widget.configuration.options.dataDictionaryUuid) {
        this.selectedOptions = this.fetchSelectOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid, options => {
          this.selectedOptions = options;
        });
      }
    }
  }
};
</script>
