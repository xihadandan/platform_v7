<template>
  <a-form-model
    ref="form"
    :model="formData"
    :colon="false"
    :rules="rules"
    labelAlign="left"
    :label-col="{ flex: '120px' }"
    :wrapper-col="{ flex: 'auto' }"
  >
    <a-form-model-item label="类型" prop="type">
      <a-radio-group v-model="formData.type" :options="typeOptions" @change="typeChange" :disabled="index != -1" />
    </a-form-model-item>
    <template v-if="formData.type == '1'">
      <a-form-model-item label="固定列" prop="index">
        <w-select :options="fixedField" placeholder="请选择固定列" v-model="formData.index" :formData="formData" formDataFieldName="name" />
      </a-form-model-item>
      <a-form-model-item label="列名" prop="name" class="error-top-addonAfter">
        <a-input v-model="formData.name" allow-clear>
          <template slot="addonAfter" v-if="formData.uuid">
            <w-i18n-input :target="formData" :code="formData.uuid" v-model="formData.name" />
          </template>
        </a-input>
      </a-form-model-item>
    </template>
    <template v-else-if="formData.type == '2'">
      <a-form-model-item label="列名" prop="name" class="error-top-addonAfter">
        <a-input v-model="formData.name" allow-clear>
          <template slot="addonAfter" v-if="formData.uuid">
            <w-i18n-input :target="formData" :code="formData.uuid" v-model="formData.name" />
          </template>
        </a-input>
      </a-form-model-item>
      <template v-if="type == 'subflow'">
        <a-form-model-item label="列值来源" prop="sources">
          <div class="branch-table">
            <div style="padding: 8px 0">
              <a-button size="small" type="primary" @click="add">增加</a-button>
              <a-button size="small" @click="del">删除</a-button>
            </div>
            <a-table
              :data-source="sources"
              :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onChange }"
              :pagination="false"
              row-key="key"
              bordered
            >
              <a-table-column key="subflow" data-index="subflow" title="子流程">
                <template slot-scope="text, record">
                  <div class="ant-form-item">
                    <w-select :options="subflowOptions" placeholder="请选择" v-model="record.subflow" style="width: 160px"></w-select>
                  </div>
                </template>
              </a-table-column>
              <a-table-column key="field" data-index="field" title="字段">
                <template slot-scope="text, record, index">
                  <div class="ant-form-item">
                    <main-dyform-fields-select
                      v-model="record.field"
                      :ref="'fieldSelect' + index"
                      :isSubflow="true"
                      :subflowId="record.subflow"
                      style="width: 160px"
                      @change="validateSourcesHandler"
                      @fieldOptionsChanged="subFlowfieldOptionsChanged"
                    ></main-dyform-fields-select>
                  </div>
                </template>
              </a-table-column>
            </a-table>
          </div>
          <div style="font-size: 12px; color: red; line-height: 1.5; margin-top: 8px">
            提示：如果需要配置多个扩展列，多个扩展列的列值来源子流程需保持一致。同一列字段的类型需保持一致。
          </div>
        </a-form-model-item>
      </template>
      <template v-else>
        <a-form-model-item label="列值来源" prop="sources">
          <main-dyform-fields-select v-model="formData.sources"></main-dyform-fields-select>
        </a-form-model-item>
      </template>
    </template>
    <a-form-model-item>
      <template slot="label">
        <label>参与关键字查询</label>
        <a-tooltip placement="top" :arrowPointAtCenter="true" :align="{ offset: [0, 8] }">
          <div slot="title">仅列配置中的前10列支持关键字查询，超出的列查询无效</div>
          <a-icon type="info-circle" />
        </a-tooltip>
      </template>
      <w-checkbox v-model="formData.searchFlag"></w-checkbox>
    </a-form-model-item>
    <a-form-model-item label="标题隐藏">
      <a-switch v-model="formData.titleHidden" />
    </a-form-model-item>
    <a-form-model-item label="列宽">
      <a-input-number v-model="formData.width" :min="0" />
    </a-form-model-item>
    <!-- <a-form-model-item label="角色可访问">
      <RoleSelect v-model="formData.role" />
    </a-form-model-item> -->
    <a-form-model-item label="是否显示">
      <a-switch
        :checked="!formData.hidden"
        @change="
          checked => {
            formData.hidden = !checked;
          }
        "
      />
    </a-form-model-item>
    <!-- 固定列不支持后端接口排序 -->
    <a-form-model-item label="列排序" v-if="formData.type == '2'">
      <a-switch v-model="formData.sortable.enable" />
    </a-form-model-item>
    <!-- 现在使用后端排序 -->
    <div v-if="formData.sortable.enable && false">
      <a-form-model-item label="排序方式">
        <a-select v-model="formData.sortable.alogrithmType" :options="alogrithmTypeOptions" :style="{ width: '100%' }"></a-select>
      </a-form-model-item>
      <a-form-model-item label="日期格式" v-show="formData.sortable.alogrithmType === 'orderByDate'">
        <a-select v-model="formData.sortable.datePattern" :options="datePatternOptions" :style="{ width: '100%' }"></a-select>
      </a-form-model-item>
      <a-form-model-item label="排序算法" v-show="formData.sortable.alogrithmType === 'orderByDefine'">
        <WidgetCodeEditor @save="value => (formData.sortable.script = value)" :value="formData.sortable.script" helpTipWidth="400px">
          <a-button icon="code">编写代码</a-button>
          <template slot="help">
            <div>
              <div>入参说明：</div>
              <ul>
                <li>
                  <a-tag>value</a-tag>
                  : 比较的两行列值
                </li>
                <li>
                  <a-tag>row</a-tag>
                  : 比较的两行显示列的数据
                </li>
                <li>
                  <a-tag>dataIndex</a-tag>
                  : 当前比较的字段编码
                </li>
              </ul>
              <div>返回说明：如果value[0]的数据比value[1]小则为负数, 如果value[0]的数据比value[1]大则为正数; 相等的时候返回 0</div>
              <div>例如：</div>
              <div>return Number(value[0]) > Number(value[1]) ? 1 : (Number(value[0]) < Number(value[1])?-1:0)</div>
            </div>
          </template>
        </WidgetCodeEditor>
      </a-form-model-item>
    </div>
    <a-form-model-item label="列提示">
      <a-row type="flex">
        <a-col flex="50px"><a-switch v-model="formData.showTip" /></a-col>
        <a-col flex="auto">
          <a-input v-model="formData.tipContent" v-show="formData.showTip" placeholder="请输入提示内容">
            <template slot="addonAfter">
              <w-i18n-input
                v-show="formData.showTip"
                :code="formData.uuid + '_tipContent'"
                :target="formData"
                v-model="formData.tipContent"
              />
            </template>
          </a-input>
        </a-col>
      </a-row>
    </a-form-model-item>
    <a-form-model-item label="标题对齐" class="item-lh">
      <a-radio-group size="small" v-model="formData.titleAlign" button-style="solid">
        <a-radio-button value="left"><a-icon type="align-left" /></a-radio-button>
        <a-radio-button value="center"><a-icon type="align-center" /></a-radio-button>
        <a-radio-button value="right"><a-icon type="align-right" /></a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="内容对齐" class="item-lh">
      <a-radio-group size="small" v-model="formData.contentAlign" button-style="solid">
        <a-radio-button value="left"><a-icon type="align-left" /></a-radio-button>
        <a-radio-button value="center"><a-icon type="align-center" /></a-radio-button>
        <a-radio-button value="right"><a-icon type="align-right" /></a-radio-button>
      </a-radio-group>
    </a-form-model-item>

    <a-form-model-item label="自定义样式">
      <a-textarea v-model="formData.customStyle" />
    </a-form-model-item>
    <a-form-model-item label="超过宽度自动省略">
      <a-switch v-model="formData.ellipsis" />
    </a-form-model-item>
    <a-form-model-item label="内容为空时显示">
      <a-input v-model="formData.defaultContentIfNull" allow-clear>
        <template slot="addonAfter">
          <w-i18n-input :code="formData.uuid + '_defaultContentIfNull'" :target="formData" v-model="formData.defaultContentIfNull" />
        </template>
      </a-input>
    </a-form-model-item>

    <a-form-model-item label="渲染器">
      <a-select
        :allowClear="true"
        :style="{
          width:
            formData.renderFunction.type && renderConfNames.includes(formData.renderFunction.type + 'Config') ? 'calc(100% - 84px)' : '100%'
        }"
        :showSearch="true"
        :filter-option="filterSelectOption"
        v-model="formData.renderFunction.type"
        @change="(val, vnode) => onSelectChangeRenderFunc(val, vnode, formData.renderFunction)"
      >
        <a-select-opt-group>
          <span slot="label">
            <a-icon type="desktop" />
            客户端渲染
          </span>

          <a-select-option v-for="(opt, i) in clientRenderOptions" :value="opt.value" :key="opt.value">
            {{ opt.label }}
          </a-select-option>
        </a-select-opt-group>
      </a-select>
      <a-button
        type="link"
        @click="renderFunctionVisible = true"
        v-if="formData.renderFunction.type && renderConfNames.includes(formData.renderFunction.type + 'Config')"
      >
        <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
        配置
      </a-button>
    </a-form-model-item>

    <template v-if="formData.clickEvent != undefined && false">
      <a-form-model-item label="列点击事件">
        <a-switch v-model="formData.clickEvent.enable" />
      </a-form-model-item>

      <EventHandlerAdmin v-if="formData.clickEvent.enable" :eventModel="formData.clickEvent.eventHandler" ref="eventHandlerModal" />
    </template>
    <!-- 渲染器配置 -->
    <a-modal
      title="渲染器配置"
      :visible="renderFunctionVisible"
      @ok="onRenderFunctionOk"
      @cancel="renderFunctionVisible = false"
      :mask="false"
      :width="700"
      :bodyStyle="{ height: '600px', 'overflow-y': 'auto', padding: '12px 20px' }"
      :destroyOnClose="true"
    >
      <a-form-model :colon="false">
        <component
          v-if="formData.renderFunction.type && renderConfNames.includes(formData.renderFunction.type + 'Config')"
          :is="formData.renderFunction.type + 'Config'"
          :options="formData.renderFunction.options"
          :column="formData"
          :columnIndexOptions="columnIndexOptions"
        />
      </a-form-model>
    </a-modal>
  </a-form-model>
</template>

<script>
import { generateId, deepClone } from '@framework/vue/utils/util';
import { fixedField, datePatterns } from '../designer/constant';
import WSelect from '../components/w-select';
import WCheckbox from '../components/w-checkbox';
import mainDyformFieldsSelect from './main-dyform-fields-select';
import { filter, map, each, findIndex, every, sortBy } from 'lodash';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';

import RenderConfiguration from './render-configuration/index';
import TableRenderMixin from '@workflow/app/web/page/workflow-work/cell-render/table.renderMixin';
import CellRender from '@workflow/app/web/page/workflow-work/cell-render/index';
import { filterSelectOption } from '@framework/vue/utils/function';
import EventHandlerAdmin from '@admin/app/web/template/common/event-handler-admin.vue';

export default {
  name: 'BranchColumnsInfo',
  inject: ['graph'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    type: {
      type: String,
      default: ''
    },
    index: {
      type: [String, Number],
      default: -1
    },
    columns: {
      type: Array,
      default: () => []
    },
    subFormData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WSelect,
    WCheckbox,
    mainDyformFieldsSelect,
    WI18nInput,
    ...RenderConfiguration,
    EventHandlerAdmin,
    WidgetCodeEditor
  },
  data() {
    let sources = [];
    if (this.formData.type == '2' && this.type == 'subflow') {
      if (this.formData.sources) {
        each(this.formData.sources.split(';'), item => {
          if (item) {
            let itemArr = item.split(',');
            sources.push({
              key: generateId(),
              subflow: itemArr[0] || '',
              field: itemArr[1] || ''
            });
          }
        });
      }
    }
    if (this.formData.uuid == undefined) {
      this.formData.uuid = generateId();
    }
    let cellRenderOptions = [];
    for (let k in CellRender) {
      cellRenderOptions.push({
        label: CellRender[k].title,
        value: k
      });
    }
    let datePatternOptions = [];
    for (let i = 0, len = datePatterns.length; i < len; i++) {
      datePatternOptions.push({
        label: datePatterns[i],
        value: datePatterns[i]
      });
    }
    return {
      fixedField,
      typeOptions: [
        {
          value: '1',
          label: '固定'
        },
        {
          value: '2',
          label: '扩展'
        }
      ],
      rules: {
        type: { required: true, message: '请选择类型！', trigger: ['blur', 'change'] },
        index: { required: true, validator: this.validateIndex, trigger: ['blur', 'change'] },
        name: { required: true, message: '请填写列名！', trigger: ['blur', 'change'] },
        sources: { required: true, validator: this.validateSources, trigger: ['blur', 'change'] }
      },
      sources,
      selectedRowKeys: [],
      selectedRows: [],
      renderConfNames: Object.keys(RenderConfiguration),
      RenderConfiguration,
      options: {},
      clientRenderOptions: [].concat(cellRenderOptions).concat(TableRenderMixin.methods.getRenderMethodOptions()),
      subFlowfieldOptions: {},
      renderFunctionVisible: false,
      datePatternOptions,
      alogrithmTypeOptions: [
        { label: '按日期排序', value: 'orderByDate' },
        { label: '按数字排序', value: 'orderByNumber' },
        { label: '按拼音排序', value: 'orderByPinYin' },
        { label: '按字符排序', value: 'orderByChar' },
        { label: '自定义排序', value: 'orderByDefine' }
      ],
      optionsChanged: false
    };
  },
  watch: {},
  computed: {
    subflowOptions() {
      let options = [];
      if (this.subFormData && this.subFormData.newFlows) {
        options = map(this.subFormData.newFlows, item => {
          return {
            id: item.value,
            text: item.name,
            children: []
          };
        });
      }
      return options;
    },
    columnIndexOptions() {
      let options = [];
      for (let i = 0; i < this.columns.length; i++) {
        let item = this.columns[i];
        if (item && this.formData.uuid != item.uuid) {
          options.push({
            value: item.extraColumn,
            label: item.name
          });
        }
      }
      return options;
    }
  },
  created() {},
  methods: {
    filterSelectOption,
    validateIndex(rule, value, callback) {
      let hasIndex = findIndex(this.columns, { index: value });
      if (hasIndex > -1 && hasIndex != this.index) {
        callback(new Error('固定列不能选择重复！'));
      } else {
        callback();
      }
    },
    validateSources(rule, value, callback) {
      if (this.type !== 'subflow') {
        if (!value) {
          callback(new Error('请选择列值来源！'));
        } else {
          callback();
        }
      } else {
        if (this.sources.length == 0) {
          callback(new Error('请添加列值来源！'));
        } else {
          let inputMode = [],
            modePass = true;
          let ispass = every(this.sources, item => {
            let pass = item.subflow && item.field;
            if (pass) {
              // 获取字段类型，如果类型一致，判断通过
              let mode = this.getFieldDataType(item.subflow, item.field);
              if (mode) {
                if (inputMode.length) {
                  modePass = modePass && inputMode[0] == mode;
                  return inputMode[0] == mode;
                } else {
                  inputMode.push(mode);
                }
              }
            }
            return pass;
          });
          if (ispass) {
            callback();
          } else {
            if (!modePass) {
              callback(new Error('请统一列值字段类型！'));
            } else {
              callback(new Error('请完善列值来源！'));
            }
          }
        }
      }
    },
    validateSourcesHandler() {
      this.$refs.form.validateField('sources');
    },
    typeChange(e) {
      let type = e.target.value;
      let hasIndex = findIndex(this.typeOptions, { value: type });
      if (hasIndex > -1) {
        this.formData.typeName = this.typeOptions[hasIndex].label;
      } else {
        this.formData.typeName = '';
      }
    },
    save(callback) {
      this.$refs.form.validate((valid, error) => {
        let data = deepClone(this.formData);
        if (data.type == '2') {
          if (this.type == 'subflow') {
            let sources = map(this.sources, item => {
              return [item.subflow, item.field].join(',');
            });
            data.sources = sources.join(';');
          }
          if (!data.extraColumn) {
            data.extraColumn = this.getExtraColumn();
          }
          data.index = data.extraColumn;
        } else {
          data.extraColumn = data.index;
        }
        delete data.configuration;
        data.configuration = JSON.stringify(data);
        callback({ valid, error, data: data });
      });
    },
    onChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys; //选中的keys
      this.selectedRows = selectedRows; //选中的行
    },
    add() {
      this.sources.push({
        key: generateId(),
        subflow: '',
        field: ''
      });
    },
    del() {
      if (this.selectedRows.length == 0) {
        this.$message.error('请选择删除的项');
        return false;
      }
      const newData = filter(this.sources, (item, index) => {
        let idx = findIndex(this.selectedRows, { key: item.key });
        return idx == -1;
      });
      this.sources = newData;
      this.selectedRowKeys = [];
      this.selectedRows = [];
    },
    getExtraColumn() {
      let tableData = this.columns;
      var extraColumn = '';
      var extraColumns = filter(tableData, function (o) {
        return o.type == '2' && o.extraColumn;
      });
      if (extraColumns.length == 0) {
        extraColumn = 'extraColumn0';
      } else {
        var newExtraColumns = sortBy(extraColumns, function (o) {
          return Number(o.extraColumn.split('extraColumn')[1]);
        });
        var currExtraColumn = newExtraColumns[newExtraColumns.length - 1].extraColumn;
        var len = Number(currExtraColumn.split('extraColumn')[1]) + 1;
        extraColumn = 'extraColumn' + len;
        for (let i = 0; i < len; i++) {
          let hasIndex = findIndex(extraColumns, { extraColumn: 'extraColumn' + i });
          if (hasIndex == -1) {
            extraColumn = 'extraColumn' + i;
            break;
          }
        }
      }
      return extraColumn;
    },
    onSelectChangeRenderFunc(val, vnode, renderFunction) {
      this.$set(renderFunction, 'options', {});
    },
    // 子流程字段列表
    subFlowfieldOptionsChanged() {
      let { subflowId, subOptions } = arguments[0];
      let hasIndex = findIndex(this.subflowOptions, { id: subflowId });
      if (hasIndex > -1) {
        each(subOptions, item => {
          item._label = `${item.name}【${this.subflowOptions[hasIndex].text}】`;
          item._value = `${subflowId};${item.id}`;
        });
        if (this.subflowOptions[hasIndex].children) {
          this.subflowOptions[hasIndex].children.splice(0, this.subflowOptions[hasIndex].children.length);
        }
        this.$set(this.subflowOptions[hasIndex], 'children', subOptions);
        this.optionsChanged = !this.optionsChanged;
      }
    },
    onRenderFunctionOk() {
      this.renderFunctionVisible = false;
    },
    getFieldDataType(subflow, field) {
      let inputMode = '';
      let hasIndex = findIndex(this.subflowOptions, { id: subflow });
      if (hasIndex > -1) {
        let fieldOptions = this.subflowOptions[hasIndex].children;
        let hasIndex1 = findIndex(fieldOptions, { id: field });
        if (hasIndex1 > -1) {
          inputMode = fieldOptions[hasIndex1].type;
        }
      }
      return inputMode;
    }
  },
  beforeMount() {},
  mounted() {}
};
</script>
