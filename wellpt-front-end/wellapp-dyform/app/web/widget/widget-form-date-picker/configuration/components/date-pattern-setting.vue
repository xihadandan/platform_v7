<template>
  <div>
    <a-form-model-item label="日期时间补0显示" class="item-lh">
      <a-switch checked-children="是" un-checked-children="否" v-model="configuration.zeroShow" @change="changeZeroShow" />
    </a-form-model-item>
    <a-form-model-item :wrapper-col="{ style: { width: '100%' } }">
      <a-select
        v-model="configuration.datePatternType"
        style="margin-right: 8px"
        @change="onSelectTypeChange"
        :getPopupContainer="getPopupContainerByPs()"
      >
        <a-select-option v-for="item in datePatternTypeOptions" :key="item.key">
          {{ item.label }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item :wrapper-col="{ style: { width: '100%' } }">
      <a-select v-model="datePattern" @change="onSelectChange" :getPopupContainer="getPopupContainerByPs()">
        <a-select-option v-for="item in datePatternOptions[configuration.datePatternType]" :key="item.key" :value="item.key0">
          {{ configuration.zeroShow ? item.label0 : item.label }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { getTypeOptions, getOptions } from './date-pattern-options.js';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
import { find } from 'lodash';
export default {
  name: 'DatePatternValue',
  mixins: [],
  props: {
    configuration: Object
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
  computed: {
    getPatternValue() {
      const item = this.configuration.datePatternJson;
      return this.configuration.zeroShow ? item.key : item.key0;
    }
  },
  created() {
    if (!this.configuration.hasOwnProperty('datePatternType')) {
      this.$set(this.configuration, 'datePatternType', 'date');
    }
    if (!this.configuration.hasOwnProperty('datePattern')) {
      this.$set(this.configuration, 'datePattern', 'yyyy-MM-DD');
    }
    if (!this.configuration.hasOwnProperty('datePatternJson') || !this.configuration.datePatternJson.key) {
      this.$set(this.configuration, 'datePatternJson', {});
      this.onSelectChange(this.configuration.datePattern);
    } else {
      this.datePattern = this.configuration.datePatternJson.key0;
    }
  },
  methods: {
    getPopupContainerByPs,
    // 改变是否补0
    changeZeroShow(v) {
      const datePatternJson = this.configuration.datePatternJson;
      let datePattern = datePatternJson.key0;
      if (!v) {
        datePattern = datePatternJson.key;
      }
      this.configuration.datePattern = datePattern;
      this.datePattern = datePatternJson.key0;
    },
    // 选择格式
    onSelectChange(v) {
      let datePatternJson = find(this.datePatternOptions[this.configuration.datePatternType], { key0: v });
      let datePattern = datePatternJson.key0;
      this.datePattern = datePattern;
      if (!this.configuration.zeroShow) {
        datePattern = datePatternJson.key;
      }
      this.configuration.datePatternJson = datePatternJson;
      this.configuration.datePattern = datePattern;
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
