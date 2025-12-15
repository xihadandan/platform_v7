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
              :options="[{ label: '单选框', value: 'WidgetFormRadio' }]"
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
            <FieldDefaultValue :configuration="widget.configuration" dataType="options" :options="selectedOptions" :multiple="true" />
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
              <a-form-model-item label="允许全选" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.checkboxAll" />
              </a-form-model-item>
              <selected-count-configuration :widget="widget" />
              <a-form-model-item>
                <template #label>
                  <FormItemTooltip
                    label="显示值字段"
                    text="当选项选择后，显示值将一并更新至该字段。可配置隐藏字段，用于数据提交、存储。"
                  ></FormItemTooltip>
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
                label="宽度"
                v-show="widget.configuration.layout == 'horizontal' && widget.configuration.alignType == 'fixedWidth'"
              >
                <a-input-number v-model="widget.configuration.itemWidth" />
              </a-form-model-item>
            </a-collapse-panel>
          </a-collapse>
        </a-tab-pane>
        <a-tab-pane key="2" tab="校验规则">
          <ValidateRuleConfiguration :widget="widget" :unique="true"></ValidateRuleConfiguration>
        </a-tab-pane>
        <a-tab-pane key="3" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer">
            <template slot="eventParamValueHelpSlot">
              <div style="width: 600px">
                <p>
                  1. 支持通过
                  <a-tag>${ 字段编码 }</a-tag>
                  表达式解析表单数据, 例如 ${ orderCode }
                </p>

                <p>
                  2. 支持通过模板字符串逻辑输出内容值, 模板字符串内通过
                  <a-tag><% %></a-tag>
                  编写javaScript代码 ( 注意: 代码内不需要通过 ${} 表达式解析数据对象 ), 例如:
                  <code
                    style="
                      display: block;
                      padding: 10px 25px;
                      border-radius: 4px;
                      background: rgb(250 250 250);
                      outline: 1px solid #dedede;
                      margin-top: 8px;
                    "
                  >
                    <% if ( orderCode == 1 ) { %> 输出内容 <% } else { %> 输出内容 <% } %>
                  </code>
                </p>
              </div>
            </template>
          </WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>

<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import { layoutOptions } from '../../commons/constant';
export default {
  name: 'WidgetFormCheckboxConfiguration',
  mixins: [formConfigureMixin],
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
      }
    ];
    return {
      layoutOptions,
      dataSourceFieldOptions,
      alignTypeOptions: [
        { label: '固定宽度', value: 'fixedWidth' },
        { label: '固定间距', value: 'fixedSpace' }
      ],
      columnIndexOptions: [],
      selectedOptions: []
    };
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
  filters: {
    textLengthFilter(text) {
      return text ? text.length : 0;
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
  },
  configuration() {
    return {
      code: '',
      length: 64,
      tokenSeparators: ';',
      layout: 'horizontal',
      alignType: 'fixedSpace',
      itemWidth: 100,
      displayValueField: undefined,
      syncLabel2FormItem: true,
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label',
      isLabelValueWidget: true, // 标记该组件是由显示值的属性特性的组件：提供从表控件进行显示值逻辑处理
      optionDataAutoSet: false,
      options: {
        type: 'selfDefine',
        dataSourceLabelColumn: '',
        dataSourceValueColumn: '',
        dataSourceId: '',
        defineOptions: [
          { id: '1', label: '选项1', value: '选项1' },
          { id: '2', label: '选项2', value: '选项2' }
        ],
        dataDictionaryUuid: ''
      },
      editMode: {
        minCount: '',
        maxCount: ''
      },
      relateList: [], // 联动条件列表
      style: {}
    };
  }
};
</script>
