<template>
  <div>
    <a-form-model-item label="日期时间补0显示" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }" v-show="!hideZero">
      <a-switch checked-children="是" un-checked-children="否" v-model="options[formatParams.zeroShow]" @change="changeZeroShow" />
    </a-form-model-item>
    <a-form-model-item label="日期时间类型" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
      <a-select v-model="options[formatParams.datePatternType]" style="margin-right: 8px" @change="onSelectTypeChange">
        <a-select-option v-for="item in datePatternTypeOptions" :key="item.key">
          {{ item.label }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="时间格式" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
      <a-select v-model="datePattern" @change="onSelectChange">
        <a-select-option v-for="item in datePatternOptions[options[formatParams.datePatternType]]" :key="item.key" :value="item.key0">
          {{ options[formatParams.zeroShow] ? item.label0 : item.label }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import {
  getTypeOptions,
  getOptions
} from '@dyform/app/web/widget/widget-form-date-picker/configuration/components/date-pattern-options.js';
import { find } from 'lodash';
export default {
  name: 'dateFormatComponent',
  mixins: [],
  props: {
    options: Object,
    formatParams: {
      type: Object,
      default: {
        datePatternType: 'datePatternType',
        zeroShow: 'zeroShow',
        datePatternJson: 'datePatternJson'
      }
    },
    valueParam: {
      type: String,
      default: 'destPattern'
    },
    hideZero: Boolean
  },
  data() {
    return {
      datePattern: undefined,
      datePatternTypeOptions: getTypeOptions(),
      datePatternOptions: getOptions()
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    if (!this.options.hasOwnProperty(this.formatParams.datePatternType)) {
      this.$set(this.options, this.formatParams.datePatternType, 'date');
    }
    if (!this.options.hasOwnProperty(this.valueParam)) {
      this.$set(this.options, this.valueParam, 'yyyy-MM-DD');
      this.$emit('change');
    }
    if (!this.options.hasOwnProperty(this.formatParams.zeroShow)) {
      this.$set(this.options, this.formatParams.zeroShow, true);
    }
    if (!this.options.hasOwnProperty(this.formatParams.datePatternJson) || !this.options[this.formatParams.datePatternJson].key) {
      this.$set(this.options, this.formatParams.datePatternJson, {});
      let value = this.options[this.valueParam].replace(/d/g, 'D');
      this.onSelectChange(value);
    } else {
      this.datePattern = this.options[this.formatParams.datePatternJson].key0;
    }
  },
  methods: {
    // 改变是否补0
    changeZeroShow(v) {
      const datePatternJson = this.options[this.formatParams.datePatternJson];
      let datePattern = datePatternJson.key0;
      if (!v) {
        datePattern = datePatternJson.key;
      }
      this.options[this.valueParam] = datePattern;
      this.datePattern = datePatternJson.key0;
      this.$emit('change');
    },
    // 选择格式
    onSelectChange(v) {
      let datePatternJson = find(this.datePatternOptions[this.options[this.formatParams.datePatternType]], { key0: v });
      let datePattern = datePatternJson.key0;
      this.datePattern = datePattern;
      if (!this.options[this.formatParams.zeroShow]) {
        datePattern = datePatternJson.key;
      }
      this.options[this.formatParams.datePatternJson] = datePatternJson;
      this.options[this.valueParam] = datePattern;
      this.$emit('change');
    },
    // 选择类型 日期/时间/日期时间
    onSelectTypeChange(v) {
      const datePattern = this.datePatternOptions[v][0].key0;
      this.datePattern = datePattern;
      this.onSelectChange(datePattern);
    }
  },
  mounted() {}
};
</script>
