<template>
  <div>
    <a-form-model-item label="日期范围">
      <a-switch v-model="options.range" />
    </a-form-model-item>
    <a-form-model-item label="日期格式">
      <a-select
        :options="datePatternOptions"
        :style="{ width: '100%' }"
        v-model="options.datePattern"
        defaultValue="YYYY-MM-DD HH:mm:ss"
        allowClear
      ></a-select>
    </a-form-model-item>

    <a-form-model-item label="日期选择限制">
      <a-select
        :options="[
          { label: '今天之前不可选', value: 'beforeTodayUnselect' },
          { label: '今天之后不可选', value: 'afterTodayUnselect' }
        ]"
        allowClear
        :style="{ width: '100%' }"
        v-model="options.selectRangeLimitType"
      ></a-select>
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'datePickerSearchConfig',
  mixins: [],
  props: {
    options: Object
  },
  data() {
    let patterns = [
      'YYYY-MM-DD HH:mm:ss',
      'YYYY-MM-DD HH:mm',
      'YYYY-MM-DD HH',
      'YYYY-MM-DD',
      'YYYY-MM',
      'YYYY',
      'YYYY年MM月DD日 HH时mm分ss秒',
      'YYYY年MM月DD日 HH时mm分',
      'YYYY年MM月DD日 HH时',
      'YYYY年MM月DD日',
      'YYYY年MM月',
      'YYYY年'
    ];
    let datePatternOptions = [];
    // TODO:国际化时间格式
    for (let i = 0, len = patterns.length; i < len; i++) {
      datePatternOptions.push({ label: patterns[i], value: patterns[i] });
    }
    return {
      datePatternOptions
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {},
  mounted() {}
};
</script>
