<template>
  <a-form-model>
    <a-form-model-item label="标题">
      <a-input v-model="column.title">
        <template slot="addonAfter">
          <WI18nInput :target="column" :code="column.id" v-model="column.title" :widget="widget" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="字段" v-if="column.operator !== 'exists'">
      <a-select
        v-model="column.dataIndex"
        :style="{ width: '100%' }"
        :showSearch="true"
        :filter-option="filterSelectOption"
        @change="onChangeColSelect"
      >
        <a-select-option v-for="(opt, i) in columnIndexOptions" :key="'col-option-' + i" :value="opt.value">
          {{ opt.label }}
          <a-tag style="position: absolute; right: 0px; top: 4px" @click.stop="() => {}">
            {{ opt.value }}
          </a-tag>
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item v-if="enableDefaultValue">
      <template slot="label">
        默认值
        <!-- <a-switch v-model="column.defaultValueUseFormula" checked-children="公式" un-checked-children="公式" /> -->
      </template>
      <!-- <a-input v-show="!column.defaultValueUseFormula" v-model="column.defaultValue" placeholder="多默认值请以;间隔" allow-clear /> -->
      <FormulaEditor :widget="widget" :bind-to-configuration="column" configKey="defaultValueFormula" ref="formulaEditor" />
    </a-form-model-item>
    <a-form-model-item label="操作符">
      <a-select
        :options="filterColumnOperatorOptions()"
        :style="{ width: '100%' }"
        :key="column.id + column.dataIndex"
        v-model="column.operator"
        @change="onChangeSearchOperator"
      />
    </a-form-model-item>
    <a-form-model-item v-if="column.operator == 'exists'">
      <template slot="label">
        <a-popover placement="top">
          <template slot="content">
            <p>可输入任何有效的 SELECT 语句, 其中可以通过 ${value} 带入搜索值</p>
            <p>例如: select 1 from table_name t where t.code = ${value}</p>
          </template>
          <div>
            查询语句
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </div>
        </a-popover>
      </template>
      <WidgetCodeEditor lang="sql" v-model="column.sql" width="400px" height="100px" />
    </a-form-model-item>
    <div v-if="enableQueryTyepSet">
      <a-form-model-item label="查询类型">
        <a-select
          :allowClear="true"
          :options="searchInputTypeOptions"
          :style="{ width: '100%' }"
          v-model="column.searchInputType.type"
        ></a-select>
      </a-form-model-item>
      <component
        v-if="column.searchInputType.type && types.includes(column.searchInputType.type + 'SearchConfig')"
        :is="column.searchInputType.type + 'SearchConfig'"
        :widget="widget"
        :designer="designer"
        :column="column"
        :options="column.searchInputType.options"
      />
    </div>
  </a-form-model>
</template>
<script type="text/babel">
import { columnSearchOperatorOptions, searchInputTypeOptions } from '../../commons/constant';
import SearchInputConfiguration from './search-input-configuration/index';
import { generateId, deepClone } from '@framework/vue/utils/util';
import FormulaEditor from '@pageAssembly/app/web/widget/commons/formula-editor.vue';
import { filterSelectOption } from '@framework/vue/utils/function';

export default {
  name: 'SearchColumnConfiguration',
  mixins: [],
  inject: ['pageContext'],
  props: {
    widget: Object,
    designer: Object,
    column: Object,
    columnIndexOptions: Array,
    enableDefaultValue: {
      type: Boolean,
      default: false
    },
    enableQueryTyepSet: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return { searchInputTypeOptions, columnSearchOperatorOptions, types: Object.keys(SearchInputConfiguration) };
  },

  beforeCreate() {},
  components: { ...SearchInputConfiguration, FormulaEditor },
  computed: {
    columnIndexOptionMap() {
      let map = {};
      for (let c of this.columnIndexOptions) {
        map[c.value] = c;
      }
      return map;
    }
  },
  created() {
    // 兼容旧代码：把默认值内容转为默认值公式内容
    if (
      this.column.defaultValue != undefined &&
      (this.column.defaultValueFormula == undefined || this.column.defaultValueFormula.value == undefined)
    ) {
      let v = this.column.defaultValue.indexOf(';') ? JSON.stringify(this.column.defaultValue.split(';')) : `'${this.column.defaultValue}'`;
      this.$set(this.column, 'defaultValueFormula', {
        label: v,
        value: v,
        marks: []
      });
      delete this.column.defaultValue;
    }
  },
  methods: {
    filterSelectOption,
    onChangeColSelect() {
      if (this.column.dataIndex && this.column.operator) {
        let options = this.filterColumnOperatorOptions();
        if (options.length > 0) {
          for (let o of options) {
            if (o.value == this.column.operator) {
              return;
            }
          }
        }
        this.column.operator = undefined;
      }
    },
    filterColumnOperatorOptions() {
      if (this.column.dataIndex) {
        let col = this.columnIndexOptionMap[this.column.dataIndex];
        if (col) {
          if (col.dataType) {
            let matches = {
              string: ['eq', 'like', 'nlike', 'in', 'is null', 'is not null'],
              number: ['lt', 'le', 'gt', 'ge', 'eq', 'between', 'like', 'nlike'],
              date: ['lt', 'le', 'gt', 'ge', 'eq', 'between', 'like', 'nlike']
            };
            return this.columnSearchOperatorOptions.filter(item => {
              return matches[col.dataType] ? matches[col.dataType].includes(item.value) : true;
            });
          }
        }
      }
      return this.columnSearchOperatorOptions;
    },
    onChangeSearchOperator() {
      if (this.column.operator == 'exists' && !this.column.hasOwnProperty('sql')) {
        this.$set(this.column, 'sql', undefined);
      }
    }
  },
  beforeMount() {},
  mounted() {}
};
</script>
