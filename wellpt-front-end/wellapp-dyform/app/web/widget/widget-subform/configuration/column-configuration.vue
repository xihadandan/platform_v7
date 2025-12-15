<template>
  <Scroll style="height: calc(100vh - 170px)">
    <a-form-model :label-col="{ style: { width: '23%' } }" :wrapper-col="{ style: { width: '75%' } }">
      <a-form-model-item>
        <template slot="label">
          关联字段
          <!-- <a-tooltip
            title="当前表格列的字段编辑、必填性规则作用于编辑表单"
            v-if="widget.configuration.layout == 'table' && widget.configuration.rowEditMode == 'form'"
          >
            <a-checkbox v-model="column.ruleApplyToForm" />
          </a-tooltip> -->
        </template>
        <a-select
          v-model="column.dataIndex"
          :options="fieldSelectOptions"
          :style="{ width: '100%' }"
          @change="changeField"
          :getPopupContainer="getPopupContainerByPs()"
          :dropdownClassName="getDropdownClassName()"
        />
      </a-form-model-item>
      <a-form-model-item label="列标题">
        <a-input v-model="column.title">
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :target="column" :designer="designer" :code="column.id" v-model="column.title" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="字段编码">
        <label>{{ column.dataIndex }}</label>
      </a-form-model-item>
      <a-form-model-item label="字段默认状态" class="item-lh">
        <a-radio-group size="small" v-model="column.defaultDisplayState" button-style="solid">
          <a-radio-button value="edit">可编辑</a-radio-button>
          <a-radio-button value="unedit">不可编辑</a-radio-button>
          <a-radio-button value="hidden">隐藏</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="是否必填">
        <a-switch v-model="column.required" />
      </a-form-model-item>

      <DataConditionalControlConfiguration
        :widget="widget"
        :configuration="column"
        configCode="requiredCondition"
        controlDescription="必填"
        :designer="designer"
        v-if="column.required"
      >
        <template slot="help">
          <a-popover trigger="hover" placement="left">
            <template slot="content">必填规则仅生效于从表表格行编辑以及表单卡片展示方式下的编辑</template>
            <span>
              <a-icon type="info-circle"></a-icon>
            </span>
          </a-popover>
        </template>
        <template slot="extraAutoCompleteSelectGroup">
          <a-select-opt-group>
            <span slot="label">
              <a-icon type="code" />
              当前从表行数据
            </span>
            <a-select-option
              v-for="opt in currentSubformColumnOptions"
              :key="opt.value"
              readonly
              :value="'__CURRENT_ROW_FORM_DATA__.' + opt.value"
              :title="opt.label"
            >
              {{ opt.label }}
            </a-select-option>
          </a-select-opt-group>
        </template>
      </DataConditionalControlConfiguration>

      <div v-show="widget.configuration.layout === 'table'">
        <a-form-model-item label="列宽" v-show="widget.configuration.layout === 'table'">
          <div class="input-number-suffix-wrapper">
            <a-input-number v-model="column.width" />
            <i>px</i>
          </div>
        </a-form-model-item>
        <a-form-model-item label="显示列排序">
          <a-switch v-model="column.sortable.enable" />
        </a-form-model-item>
        <div v-show="column.sortable.enable">
          <a-form-model-item label="排序方式">
            <a-select
              v-model="column.sortable.alogrithmType"
              :options="alogrithmTypeOptions"
              :style="{ width: '100%' }"
              :getPopupContainer="getPopupContainerByPs()"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="日期格式" v-show="column.sortable.alogrithmType === 'orderByDate'">
            <a-select
              v-model="column.sortable.datePattern"
              :options="datePatternOptions"
              :style="{ width: '100%' }"
              :getPopupContainer="getPopupContainerByPs()"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="排序算法" v-show="column.sortable.alogrithmType === 'orderByDefine'">
            <WidgetCodeEditor @save="value => (column.sortable.script = value)" :value="column.sortable.script" helpTipWidth="400px">
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
                      <a-tag>formData</a-tag>
                      : 比较的两行对应表单数据, row上的往往是显示需要的字符内容, 如果需要比较真实的表单字段值, 则需要使用formData
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
      </div>
      <a-form-model-item label="标题对齐方式" class="item-lh">
        <a-radio-group size="small" v-model="column.titleAlign" button-style="solid">
          <a-radio-button value="left"><a-icon type="align-left" /></a-radio-button>
          <a-radio-button value="center"><a-icon type="align-center" /></a-radio-button>
          <a-radio-button value="right"><a-icon type="align-right" /></a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="内容对齐方式" class="item-lh" v-show="widget.configuration.layout === 'table'">
        <a-radio-group size="small" v-model="column.contentAlign" button-style="solid">
          <a-radio-button value="left"><a-icon type="align-left" /></a-radio-button>
          <a-radio-button value="center"><a-icon type="align-center" /></a-radio-button>
          <a-radio-button value="right"><a-icon type="align-right" /></a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="超过宽度自动省略" v-show="widget.configuration.layout === 'table'">
        <a-switch v-model="column.ellipsis" />
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          自定义渲染模板
          <a-checkbox v-model="column.enableColRenderSlot" />
        </template>
        <div v-show="column.enableColRenderSlot">
          <VueTemplateSelect v-model="column.colRenderTemplateName" :style="{ width: '100%' }" />
          <a-alert banner>
            <template slot="message">
              <div style="display: flex; justify-content: space-between">
                <label>
                  模板内可通过默认插槽
                  <label style="color: var(--w-link-color)">&lt;slot /&gt;</label>
                  渲染默认的字段组件
                </label>
                <a-button size="small" type="link" @click="copyColumnRenderTemplateSnippet">示例</a-button>
              </div>
            </template>
          </a-alert>
        </div>
      </a-form-model-item>

      <FieldFormulaInput
        v-if="column.widget != undefined && ['WidgetFormInput', 'WidgetFormInputNumber'].includes(column.widget.wtype)"
        :configuration="column"
        :widget="widget"
        :designer="designer"
        :allowUseSubformFunction="false"
      ></FieldFormulaInput>

      <ColumnEventConfiguration :column="column" :widget="widget" :designer="designer" />
    </a-form-model>
  </Scroll>
</template>

<script type="text/babel">
import { datePatterns } from '../../commons/constant';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
import ColumnEventConfiguration from './column-event-configuration.vue';
import { copyToClipboard } from '@framework/vue/utils/util';

export default {
  name: 'ColumnConfiguration',
  props: {
    widget: Object,
    designer: Object,
    column: Object,
    fieldSelectOptions: Array
  },
  components: { ColumnEventConfiguration },
  computed: {
    currentSubformColumnOptions() {
      let options = [];
      for (let i = 0; i < this.widget.configuration.columns.length; i++) {
        if (
          this.widget.configuration.columns[i].id != this.column.id &&
          this.widget.configuration.columns[i].dataIndex !== this.column.dataIndex
        ) {
          options.push({
            label: this.widget.configuration.columns[i].title,
            value: this.widget.configuration.columns[i].dataIndex
          });
        }
      }
      return options;
    }
  },
  data() {
    let datePatternOptions = [];
    for (let i = 0, len = datePatterns.length; i < len; i++) {
      datePatternOptions.push({
        label: datePatterns[i],
        value: datePatterns[i]
      });
    }
    return {
      datePatternOptions,
      alogrithmTypeOptions: [
        { label: '按日期排序', value: 'orderByDate' },
        { label: '按数字排序', value: 'orderByNumber' },
        { label: '按拼音排序', value: 'orderByPinYin' },
        { label: '按字符排序', value: 'orderByChar' },
        { label: '自定义排序', value: 'orderByDefine' }
      ]
    };
  },
  created() {
    if (!this.column.hasOwnProperty('requiredCondition')) {
      this.$set(this.column, 'requiredCondition', {
        match: 'all', // all 、any
        conditions: []
      });
    }
  },
  methods: {
    copyColumnRenderTemplateSnippet(e) {
      let _this = this;
      import('./code-snippets').then(m => {
        copyToClipboard(m.default.subformColumnRenderTemplate, e, function (success) {
          if (success) {
            _this.$message.success('已复制示例');
          }
        });
      });
    },
    getPopupContainerByPs,
    getDropdownClassName,
    changeField(value) {
      this.$emit('change', value);
      if (
        value == undefined ||
        (this.column.widget != undefined && !['WidgetFormInput', 'WidgetFormInputNumber'].includes(this.column.widget.wtype))
      ) {
        delete this.column.formula;
      }
    }
  }
};
</script>
