<template>
  <div>
    <a-form-model
      ref="form"
      :model="widget.configuration"
      :rules="rules"
      :label-col="{ span: 8 }"
      labelAlign="left"
      :wrapper-col="{ span: 15, style: { textAlign: 'right' } }"
    >
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <FieldNameInput :widget="widget" />
          <FieldCodeInput :widget="widget" />
          <FieldLengthInput :widget="widget" />
          <FieldDefaultValue :configuration="widget.configuration" />
          <SelectTreeOptionSource
            :widget="widget"
            :designer="designer"
            :options="widget.configuration.options"
            :dataSourceFieldOptions="dataSourceFieldOptions"
            @columnIndexOptionChange="onDsColumnIndexOptionChange"
          >
            <!-- 逐级加载搜索时搜索不到未加载的内容，先注释 -->
            <!-- <template v-if="widget.configuration.options.type == 'dataDictionary'">
              <a-form-model-item>
                <template #label>
                  <span>逐级加载</span>
                  <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                    <template #title>
                      <div>字典数据过多时，建议使用逐级加载</div>
                    </template>
                    <a-icon type="exclamation-circle" />
                  </a-tooltip>
                </template>
                <a-switch v-model="widget.configuration.editMode.gradualLoad" />
              </a-form-model-item>
            </template> -->
          </SelectTreeOptionSource>
          <a-form-model-item label="数据显示层级">
            <a-input-number v-model:value="widget.configuration.datalevel" :min="2" />
          </a-form-model-item>
          <a-form-model-item label="默认状态">
            <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState">
              <a-radio-button value="edit">可编辑</a-radio-button>
              <a-radio-button value="unedit">不可编辑</a-radio-button>
              <a-radio-button value="hidden">隐藏</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-collapse :bordered="false" expandIconPosition="right">
            <!-- 编辑模式属性 -->
            <a-collapse-panel key="editMode" header="编辑模式属性">
              <CascaderEditMode
                :designer="designer"
                :rules="rules"
                :widget="widget"
                :inputFieldOptions="inputFieldOptions"
                :textWidgetOptions="textWidgetOptions"
              />
            </a-collapse-panel>
            <!-- 不可编辑模式属性 -->
            <a-collapse-panel key="unEditMode" header="不可编辑模式属性">
              <a-form-model-item label="不可编辑状态" :label-col="{ span: 8 }" :wrapper-col="{ span: 16 }">
                <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState">
                  <a-radio-button value="label">纯文本</a-radio-button>
                  <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
            </a-collapse-panel>
            <template v-if="designer.terminalType == 'mobile'">
              <a-collapse-panel key="widget-style" header="组件样式">
                <!-- <StyleConfiguration :widget="widget" /> -->
                <a-form-model-item label="显示边框" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                  <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.uniConfiguration.bordered" />
                </a-form-model-item>
              </a-collapse-panel>
            </template>
            <!-- 其它属性 -->
            <a-collapse-panel key="otherProp" header="其它属性">
              <a-form-model-item label="字段映射" v-show="['dataSource', 'dataModel'].indexOf(widget.configuration.options.type) > -1">
                <WidgetDesignDrawer :id="'fieldMappingConfigure' + widget.id" title="事件处理" :designer="designer">
                  <a-button>添加字段映射</a-button>
                  <template slot="content">
                    <WidgetFormSelectFieldMappingConfiguration
                      :widget="widget"
                      ref="fieldMapping"
                      :columnIndexOptions="columnIndexOptions"
                      :designer="designer"
                    />
                  </template>
                </WidgetDesignDrawer>
              </a-form-model-item>
              <a-form-model-item label="提示语">
                <a-input placeholder="输入框提示语" v-model="widget.configuration.placeholder" style="width: 100%">
                  <template slot="addonAfter">
                    <WI18nInput :widget="widget" :designer="designer" code="placeholder" v-model="widget.configuration.placeholder" />
                  </template>
                </a-input>
              </a-form-model-item>
              <a-form-model-item label="描述" :wrapper-col="{ style: { marginTop: '2px' } }">
                <a-textarea :rows="4" placeholder="请输入内容" v-model="widget.configuration.note" :maxLength="200" />
                <span class="textLengthShow">{{ widget.configuration.note | textLengthFilter }}/200</span>
              </a-form-model-item>
            </a-collapse-panel>
          </a-collapse>
        </a-tab-pane>
        <a-tab-pane key="2" tab="校验规则">
          <ValidateRuleConfiguration
            ref="validateRef"
            :widget="widget"
            :trigger="true"
            :required="true"
            :unique="true"
            :regExp="true"
            :validatorFunction="true"
          >
            <template slot="validatorFunctionHelp">
              <div>
                必须使用
                <a-tag>callback</a-tag>
                函数返回校验结果，例如：
              </div>
              <ul>
                <li>
                  校验通过，
                  <a-tag>callback();</a-tag>
                </li>
                <li>
                  校验不通过，
                  <a-tag>callback('这是校验不通过的提示信息');</a-tag>
                </li>
              </ul>
            </template>
          </ValidateRuleConfiguration>
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
<style></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import FormEventParamHelp from '../../commons/form-event-param-help.vue';
import SelectTreeOptionSource from '../../widget-form-select/configuration/tree-option-source';
export default {
  name: 'WidgetFormCascaderConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  components: { FormEventParamHelp, SelectTreeOptionSource },
  filters: {
    textLengthFilter(text) {
      return text ? text.length : 0;
    }
  },
  data() {
    let dataSourceFieldOptions = [
      {
        label: '显示值字段',
        value: 'dataSourceLabelColumn'
      },
      {
        label: '真实值字段',
        value: 'dataSourceValueColumn'
      },
      {
        label: '唯一值字段',
        value: 'dataSourceKeyColumn'
      },
      {
        label: '父级字段',
        value: 'dataSourceParentColumn'
      }
    ];
    return {
      dataSourceFieldOptions,
      columnIndexOptions: [],
      moreConfigurationDrawerKey: {
        mappingConfigureVisible: false
      },
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
  mounted() {},
  updated() {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', { bordered: false });
    }
  }
};
</script>
