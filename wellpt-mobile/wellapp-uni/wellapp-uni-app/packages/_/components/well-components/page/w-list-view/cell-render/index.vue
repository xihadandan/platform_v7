<template>
  <cell-data-colorful-tag-render
    v-if="name == 'CellDataColorfulTagRender'"
    :slotOption="slotOption"
    :rowIndex="rowIndex"
    :row="row"
    :text="text"
  ></cell-data-colorful-tag-render>
  <cell-data-icon-render
    v-else-if="name == 'CellDataIconRender'"
    :slotOption="slotOption"
    :rowIndex="rowIndex"
    :row="row"
    :text="text"
  ></cell-data-icon-render>
  <cell-data-org-element-render
    v-else-if="name == 'CellDataOrgElementRender'"
    :slotOption="slotOption"
    :row="row"
    :rowIndex="rowIndex"
    :text="text"
  ></cell-data-org-element-render>
  <cell-data-file-render
    v-else-if="name == 'CellDataFileRender'"
    :slotOption="slotOption"
    :row="row"
    :rowIndex="rowIndex"
    :text="text"
  ></cell-data-file-render>
  <text v-else-if="name == 'formateDateString'">{{ formateDateString(text, row) }}</text>
  <view v-else-if="name == 'vueTemplateDataRender'">
    <RenderDevelopTemplate v-if="isComponent" :isComponent="isComponent" :options="$props" ref="vueRenderTemplate" />
    <template v-if="renderedContent">
      <component :is="renderedContent" :slotOption="slotOption" :row="row" :rowIndex="rowIndex" :text="text" />
      <!-- <rich-text :nodes="renderedContent"></rich-text> -->
    </template>
  </view>
</template>
<script type="text/babel">
import cellRenderMixin from "./cellRenderMixin";
// import RenderDevelopTemplate from "@develop/RenderDevelopTemplate.vue";
import moment from "moment";

export default {
  mixins: [cellRenderMixin],
  props: {},
  components: {},
  created() {
    this.setMomentLocale();
  },
  methods: {
    moment,
    setMomentLocale() {
      if (this.$i18n.locale.startsWith("en")) {
        moment.locale("en");
        return;
      }
      let locale = this.$i18n.locale.replace("_", "-").toLowerCase();
      if (locale == "zh-cn") {
        moment.locale(locale);
        return;
      }
      let _locale = locale.split("-")[0];
      moment.locale(_locale);
    },
    /**
     * 格式化时间字符串，从原来的模式格式化为另外的模式
     * @param {*} value 时间字符串值
     * @param {*} row  行数据记录
     * @param {*} options
     * @returns
     */
    formateDateString: function (value, row) {
      let options = this.slotOption.options;
      // 只有配置的时间格式有完整的年月日，才会进行不显示当前年和时间间隔处理
      let hasTimeExtraSetting =
        options.destPattern &&
        options.datePatternJson.contentFormat.indexOf("yyyy-MM-DD") > -1 &&
        options.destPattern.startsWith("yyyy");
      let val = value
        ? moment(moment(value, options.sourcePattern || "YYYY-MM-DD HH:mm:ss")).format(options.destPattern)
        : "";
      if (
        hasTimeExtraSetting &&
        options.hideYear &&
        val &&
        moment().isSame(moment(moment(value, options.sourcePattern || "YYYY-MM-DD HH:mm:ss")), "year")
      ) {
        val = val.substring(5);
      }
      if (val && options.isRange && options.endTimeParams) {
        if (row[options.endTimeParams]) {
          let end = moment(moment(row[options.endTimeParams], options.sourcePattern || "YYYY-MM-DD HH:mm:ss")).format(
            options.endDestPattern
          );

          let hasEndHideYearSetting =
            options.endDestPattern &&
            options.endDatePatternJson.contentFormat.indexOf("yyyy-MM-DD") > -1 &&
            options.endDestPattern.startsWith("yyyy");
          if (hasTimeExtraSetting && hasEndHideYearSetting && options.hideYear && val && moment().isSame(end, "year")) {
            end = end.substring(5);
          }
          val += " ~ " + end;
        }
      } else if (hasTimeExtraSetting && options.showTimeInterval && value) {
        // 时间间隔
        let _value = moment(moment(value, options.sourcePattern || "YYYY-MM-DD HH:mm:ss"));
        let isformat = true;
        if (options.timeInterval) {
          let diff = Math.abs(_value.diff(moment(), "days"));
          if (diff >= options.timeInterval) {
            isformat = false;
          }
        }
        if (isformat) {
          val = _value.fromNow();
        }
      }
      return val;
    },
  },
};
</script>
