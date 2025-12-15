<template>
  <a-form-model>
    <a-form-model-item label="字段">
      <a-select :options="fieldSelectOptions" :style="{ width: '100%' }" v-model="column.dataIndex" @change="onChangeColSelect" />
    </a-form-model-item>
    <a-form-model-item label="标题">
      <a-input v-model="column.title">
        <template slot="addonAfter">
          <WI18nInput :widget="widget" :designer="designer" :code="column.id" v-model="column.title" :target="column" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="查询方式" v-if="column.dataIndex">
      <a-select :options="getOptions(column.dataIndex)" :style="{ width: '100%' }" v-model="column.comparator" />
    </a-form-model-item>
    <a-form-model-item label="控件分割符号" v-if="column.comparator == 'between'">
      <a-input v-model="column.separator" />
    </a-form-model-item>
  </a-form-model>
</template>
<script type="text/babel">
export default {
  name: 'WidgetSubformSearchColumnConfiguration',
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
    return {
      comparatorOptions: {
        WidgetFormDatePicker: [
          { label: '区间', value: 'between' },
          { label: '小于', value: '<' },
          { label: '小于等于', value: '<=' },
          { label: '大于', value: '>' },
          { label: '大于等于', value: '>=' },
          { label: '等于', value: '==' }
        ],
        WidgetFormDatePicker$Range: [{ label: '区间', value: 'between' }],
        WidgetFormNumericalBar: [
          { label: '等于', value: '==' },
          { label: '小于', value: '<' },
          { label: '小于等于', value: '<=' },
          { label: '大于', value: '>' },
          { label: '大于等于', value: '>=' },
          { label: '区间', value: 'between' }
        ],
        WidgetFormInputNumber: [
          { label: '等于', value: '==' },
          { label: '小于', value: '<' },
          { label: '小于等于', value: '<=' },
          { label: '大于', value: '>' },
          { label: '大于等于', value: '>=' },
          { label: '区间', value: 'between' }
        ],
        WidgetFormSelect: [{ label: '包含任一项', value: 'includes' }],
        WidgetFormCheckbox: [{ label: '包含任一项', value: 'includes' }],
        WidgetFormRadio: [{ label: '等于', value: '==' }],
        WidgetFormOrgSelect: [
          { label: '包含任一项', value: 'includes' },
          { label: '模糊匹配', value: 'like' }
        ],
        WidgetFormSwitch: [{ label: '等于', value: '==' }],
        WidgetFormTag: [
          { label: '包含任一项', value: 'includes' },
          { label: '模糊匹配', value: 'like' }
        ],
        default: [{ label: '模糊匹配', value: 'like' }]
      }
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    columnWidgetMap() {
      let map = {};
      for (let i = 0, len = this.columnIndexOptions.length; i < len; i++) {
        map[this.columnIndexOptions[i].configuration.code] = this.columnIndexOptions[i];
      }
      return map;
    },
    fieldSelectOptions() {
      let fieldSelectOptions = [];
      for (let i = 0, len = this.columnIndexOptions.length; i < len; i++) {
        if (!['WidgetFormJobSelect', 'WidgetFormColor'].includes(this.columnIndexOptions[i].wtype)) {
          fieldSelectOptions.push({
            label: this.columnIndexOptions[i].configuration.name,
            value: this.columnIndexOptions[i].configuration.code
          });
        }
      }
      return fieldSelectOptions;
    }
  },
  created() {},
  methods: {
    getOptions(dataIndex) {
      let wgt = this.columnWidgetMap[dataIndex];
      if (wgt) {
        if (wgt.subtype) {
          return (
            this.comparatorOptions[wgt.wtype + '$' + wgt.subtype] || this.comparatorOptions[wgt.wtype] || this.comparatorOptions.default
          );
        } else {
          return this.comparatorOptions[wgt.wtype] || this.comparatorOptions.default;
        }
      } else {
        return this.comparatorOptions.default;
      }
    },
    onChangeColSelect(key, opt) {
      if (this.column.dataIndex) {
        this.column.title = opt.componentOptions.children[0].text.trim();
        this.$set(this.column, 'comparator', this.getOptions(this.column.dataIndex)[0].value);
      }
    }
  },
  beforeMount() {},
  mounted() {}
};
</script>
