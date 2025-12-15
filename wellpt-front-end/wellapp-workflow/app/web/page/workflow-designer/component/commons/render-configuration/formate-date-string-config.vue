<template>
  <div class="pt-form">
    <a-form-model-item label="时间显示设置">
      <dateFormatComponent :options="options"></dateFormatComponent>
    </a-form-model-item>
    <a-form-model-item>
      <template slot="label">
        <label>时间区间显示设置</label>
        <a-tooltip placement="topLeft" :arrowPointAtCenter="true">
          <div slot="title">请在列配置里配置对应的结束时间字段</div>
          <a-icon type="exclamation-circle" style="line-height: initial; vertical-align: middle; padding-top: 0" />
        </a-tooltip>
      </template>
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
