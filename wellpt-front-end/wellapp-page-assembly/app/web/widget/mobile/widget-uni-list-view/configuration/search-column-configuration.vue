<template>
  <a-form-model>
    <a-form-model-item label="标题">
      <a-input v-model="column.label">
        <template slot="addonAfter">
          <WI18nInput :widget="widget" :designer="designer" :code="column.uuid" :target="column" v-model="column.label" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="字段" v-if="column.operator !== 'exists'">
      <a-select
        :options="columnIndexOptions"
        :style="{ width: '100%' }"
        v-model="column.name"
        :showSearch="true"
        :filter-option="filterSelectOption"
      />
    </a-form-model-item>
    <a-form-model-item label="默认值" v-if="enableDefaultValue">
      <a-input v-model="column.defaultValue" placeholder="多默认值请以;间隔" allow-clear />
    </a-form-model-item>
    <a-form-model-item label="操作符">
      <a-select
        :options="columnSearchOperatorOptions"
        :style="{ width: '100%' }"
        v-model="column.operator"
        :showSearch="true"
        :filter-option="filterSelectOption"
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
          v-model="column.queryOptions.queryType"
          :showSearch="true"
          :filter-option="filterSelectOption"
        ></a-select>
      </a-form-model-item>
      <component
        v-if="column.queryOptions.queryType && types.includes(column.queryOptions.queryType + 'SearchConfig')"
        :is="column.queryOptions.queryType + 'SearchConfig'"
        :options="column.queryOptions.options"
      />
    </div>
  </a-form-model>
</template>
<script type="text/babel">
import { columnSearchOperatorOptions } from '@pageAssembly/app/web/widget/commons/constant';
import SearchInputConfiguration from '@pageAssembly/app/web/widget/widget-table/configuration/search-input-configuration/index';
import { generateId, deepClone } from '@framework/vue/utils/util';
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
    if (!this.column.uuid) {
      this.column.uuid = generateId();
    }
    const searchInputTypeOptions = [
      { label: '文本框', value: 'input' },
      { label: '日期框', value: 'datePicker' },
      { label: '组织弹出框', value: 'organization' },
      { label: '下拉框', value: 'select' },
      // { label: '分组下拉框', value: 'groupSelect' },
      { label: '单选框', value: 'radio' },
      { label: '复选框', value: 'checkbox' }
      // { label: '下拉树形', value: 'treeSelect' }
    ];
    return { searchInputTypeOptions, columnSearchOperatorOptions, types: Object.keys(SearchInputConfiguration) };
  },

  beforeCreate() {},
  components: { ...SearchInputConfiguration },
  computed: {},
  created() {},
  methods: {
    filterSelectOption,
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
