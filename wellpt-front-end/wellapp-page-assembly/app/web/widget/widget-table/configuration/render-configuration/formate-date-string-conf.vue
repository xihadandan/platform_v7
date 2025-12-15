<template>
  <div class="pt-form">
    <a-form-model-item label="时间显示设置">
      <dateFormatComponent :options="options"></dateFormatComponent>
    </a-form-model-item>
    <template
      v-if="
        options.destPattern && options.datePatternJson.contentFormat.indexOf('yyyy-MM-DD') > -1 && options.destPattern.startsWith('yyyy')
      "
    >
      <a-form-model-item :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
        <template slot="label">
          <a-popover placement="right">
            <template slot="content">
              <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                <li>日期时间在当前年份的范围内时，不显示年份</li>
                <li>时间格式必须有完整年月日的时间</li>
              </ul>
            </template>
            不显示当前年份
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-popover>
        </template>
        <a-switch v-model="options.hideYear" />
      </a-form-model-item>
      <a-form-model-item :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
        <template slot="label">
          <a-popover placement="right">
            <template slot="content">
              <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                <li>时间区间显示设置开启，时间间隔显示设置无效</li>
                <li>时间间隔显示开启且时间间隔未设值时，默认全部时间按时间间隔显示</li>
                <li>相对当前时间前后n天的时间按时间间隔显示，n为时间间隔天数</li>
              </ul>
            </template>
            按时间间隔显示
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-popover>
        </template>
        <a-switch v-model="options.showTimeInterval" />
      </a-form-model-item>
      <a-form-model-item label="时间间隔" v-if="options.showTimeInterval" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
        <a-input-number v-model="options.timeInterval" :min="1" />
        天
      </a-form-model-item>
    </template>
    <a-form-model-item label="时间区间显示设置">
      <a-form-model-item label="是否有时间区间" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
        <a-switch checked-children="是" un-checked-children="否" v-model="options.isRange" />
      </a-form-model-item>
      <template v-if="options.isRange">
        <a-form-model-item label="结束时间字段" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
          <a-select v-model="options.endTimeParams">
            <a-select-option v-for="item in columnIndexOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <dateFormatComponent
          ref="endRef"
          :options="options"
          valueParam="endDestPattern"
          :formatParams="endTimeFormat"
          hideZero
        ></dateFormatComponent>
      </template>
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import dateFormatComponent from './date-format-component.vue';
export default {
  name: 'formateDateStringConfig',
  mixins: [],
  props: {
    options: Object,
    widget: Object,
    columnIndexOptions: Array
  },
  scope: ['pc', 'mobile'],
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
    let sourcePatternOptions = [];
    // TODO:国际化时间格式
    for (let i = 0, len = patterns.length; i < len; i++) {
      sourcePatternOptions.push({ label: patterns[i], value: patterns[i] });
    }
    return {
      sourcePatternOptions,
      endTimeFormat: {
        datePatternType: 'endDatePatternType',
        zeroShow: 'endZeroShow',
        datePatternJson: 'endDatePatternJson'
      }
    };
  },

  beforeCreate() {},
  components: { dateFormatComponent },
  computed: {},
  created() {
    if (!this.options.hasOwnProperty('sourcePattern')) {
      this.$set(this.options, 'sourcePattern', 'YYYY-MM-DD HH:mm:ss');
    }
    if (!this.options.hasOwnProperty('hideYear')) {
      this.$set(this.options, 'hideYear', false);
    }
  },
  methods: {},
  mounted() {},
  watch: {
    'options.zeroShow': {
      handler(v) {
        this.$set(this.options, 'endZeroShow', v);
        if (this.$refs.endRef && this.options.isRange) {
          this.$refs.endRef.changeZeroShow(v);
        }
      }
    }
  }
};
</script>
