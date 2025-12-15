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
          <template v-if="widget.configuration.type !== 'select-tree'">
            <a-form-model-item label="类型">
              <a-select
                v-model="widget.configuration.type"
                :options="selectTypeOptions"
                :style="{ width: '100%' }"
                @change="onSelectChangeWidgetTypeWithoutFieldName"
                :getPopupContainer="getPopupContainerByPs()"
              ></a-select>
            </a-form-model-item>
          </template>
          <FieldNameInput :widget="widget" />
          <FieldCodeInput :widget="widget" />
          <FieldLengthInput :widget="widget" />
          <template v-if="designer.terminalType == 'mobile' && widget.configuration.type == 'select'">
            <a-form-model-item label="弹出类型">
              <a-radio-group size="small" v-model="widget.configuration.uniConfiguration.dropType">
                <a-radio-button value="dropdown">下拉框</a-radio-button>
                <a-radio-button value="picker">底部弹出</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
          </template>
          <!-- 备选项来源 -->
          <template v-if="widget.configuration.type === 'select-tree'">
            <select-tree-option-source
              :widget="widget"
              :designer="designer"
              :options="widget.configuration.options"
              :dataSourceFieldOptions="currentOptionSourceDatasourceField"
              @columnIndexOptionChange="onDsColumnIndexOptionChange"
            />
          </template>
          <template v-else>
            <option-source-configuration
              :widget="widget"
              :designer="designer"
              :options="widget.configuration.options"
              :dataSourceFieldOptions="currentOptionSourceDatasourceField"
              @columnIndexOptionChange="onDsColumnIndexOptionChange"
            />
          </template>
          <!-- 默认值 -->
          <!-- 树形下拉框暂时未做 -->
          <!-- 自定义和字典使用下拉框选择默认值 -->
          <template
            v-if="
              widget.configuration.type !== 'select-tree' &&
              (widget.configuration.options.type == 'selfDefine' || widget.configuration.options.type == 'dataDictionary')
            "
          >
            <FieldDefaultValue
              :configuration="widget.configuration"
              dataType="options"
              :options="selectedOptions"
              :multiple="widget.configuration.editMode.selectMultiple"
            />
            <div v-show="false">{{ selectedOptionsChange }}</div>
          </template>
          <template v-else>
            <FieldDefaultValue :configuration="widget.configuration" />
          </template>
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
              <template v-if="widget.configuration.type === 'select-tree'">
                <SelectTreeEditMode :rules="rules" :widget="widget" :inputFieldOptions="inputFieldOptions" />
              </template>
              <template v-else>
                <SelectEditMode :widget="widget" :inputFieldOptions="inputFieldOptions" :textWidgetOptions="textWidgetOptions" />
              </template>
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
                <template v-if="widget.configuration.type == 'select-tree'">
                  <a-form-model-item label="连接线" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                    <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.uniConfiguration.showLine" />
                  </a-form-model-item>
                </template>
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
import { selectModeOptions, selectTypeOptions } from '../../commons/constant';
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import FormEventParamHelp from '../../commons/form-event-param-help.vue';
export default {
  name: 'WidgetFormSelectConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  components: { FormEventParamHelp },
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
      },
      {
        label: '扩展显示内容（辅助列）',
        value: 'dataSourceExtendColumn',
        allowClear: true
      },
      {
        label: '分组字段',
        value: 'groupColumn'
      }
    ];
    return {
      selectModeOptions,
      selectTypeOptions,
      dataSourceFieldOptions,
      columnIndexOptions: [],
      moreConfigurationDrawerKey: {
        mappingConfigureVisible: false
      },
      selectedOptions: []
    };
  },
  computed: {
    currentOptionSourceDatasourceField() {
      if (this.widget.configuration.type === 'select') {
        let arr = this.dataSourceFieldOptions.slice(0, 2);
        arr.push(this.dataSourceFieldOptions[4]);
        return arr;
      } else if (this.widget.configuration.type === 'select-group') {
        let arr = this.dataSourceFieldOptions.slice(0, 2);
        arr.push(this.dataSourceFieldOptions[5]);
        arr.push(this.dataSourceFieldOptions[4]);
        return arr;
      } else {
        return this.dataSourceFieldOptions.slice(0, 5);
      }
    },
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
    // change是否多选
    changeSelectMode(val) {
      this.widget.configuration.editMode.selectMode = 'default';
      if (val) {
        this.widget.configuration.editMode.selectMode = 'multiple';
      }
    },
    onDsColumnIndexOptionChange(opt) {
      this.columnIndexOptions = opt;
    },

    onSelectChangeWidgetTypeWithoutFieldName(val, opt) {
      if (!this.widget.configuration.name) {
        this.widget.title = opt.componentOptions.children[0].text;
      }
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
      this.$set(this.widget.configuration, 'uniConfiguration', { dropType: 'picker', bordered: false });
    }
  }
};
</script>
