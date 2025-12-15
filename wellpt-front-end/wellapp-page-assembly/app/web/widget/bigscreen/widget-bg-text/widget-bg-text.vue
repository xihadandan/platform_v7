<template>
  <div style="width: 100%; height: 100%" class="widget-bg-text">
    <div :style="textStyle" @click="onClickText">
      {{ vText }}
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import './css/index.less';
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import moment from 'moment';

export default {
  name: 'WidgetBgText',
  mixins: [widgetMixin],
  props: { widget: Object, designer: Object },
  components: {},
  computed: {
    vText() {
      if (this.widget.configuration.textType == 'static') {
        return this.widget.configuration.text;
      }
      if (this.widget.configuration.textType == 'datetime') {
        return this.dateString;
      }
      return undefined;
    },
    textStyle() {
      let style = {
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          height: '100%'
        },
        fontStyle = this.widget.configuration.style.fontStyle;
      style.color = fontStyle.color;
      style.justifyContent = fontStyle.justifyContent;
      style.letterSpacing = `${fontStyle.letterSpacing}px`;
      style.fontSize = `${fontStyle.fontSize}px`;
      style.fontWeight = fontStyle.fontWeight;
      if (this.widget.configuration.enableCarousel) {
        style.animation = `marquee ${this.widget.configuration.carouselSeconds}s linear infinite`;
      }
      return style;
    },

    vDatetimePattern() {
      let datetimePattern = undefined;
      if (this.widget.configuration.textType == 'datetime' && this.widget.configuration.datetimeDisplayType == 'current') {
        datetimePattern = this.widget.configuration.datetimePattern;
        if (!this.widget.configuration.fixZero) {
          datetimePattern = datetimePattern
            .replace(/MM/g, 'M')
            .replace(/DD/g, 'D')
            .replace(/HH/g, 'H')
            .replace(/mm/g, 'm')
            .replace(/ss/g, 's');
        }
      }
      return datetimePattern;
    },
    vEndDate() {
      return this.widget.configuration.endDate || moment().add(1, 'day');
    }
  },
  data() {
    return {
      dateString: undefined
    };
  },
  beforeCreate() {},
  created() {
    if (this.widget.configuration.textType == 'datetime' && this.widget.configuration.datetimeDisplayType == 'current') {
      this.dateString = moment().format(this.vDatetimePattern);
    }
  },
  beforeMount() {
    if (this.widget.configuration.textType == 'datetime') {
      this.intervalTime = setInterval(() => {
        if (this.widget.configuration.datetimeDisplayType == 'current') {
          this.dateString = moment().format(this.vDatetimePattern);
        } else if (this.widget.configuration.datetimeDisplayType == 'countdown') {
          let diffTimestamp = moment(this.vEndDate, 'yyyy年MM月DD日 HH时mm分ss秒').diff(moment());
          this.dateString = this.formatTimeDifference(diffTimestamp);
          if (diffTimestamp <= 0) {
            window.clearInterval(this.intervalTime);
          }
        }
      }, 1000);
    }
  },
  mounted() {},
  methods: {
    formatTimeDifference(milliseconds) {
      if (milliseconds < 0) {
        milliseconds = 0;
      }
      // 计算天数
      const days = Math.floor(milliseconds / (1000 * 60 * 60 * 24));

      // 计算剩余的小时数
      const remainingMillisForHours = milliseconds % (1000 * 60 * 60 * 24);
      const hours = Math.floor(remainingMillisForHours / (1000 * 60 * 60));

      // 计算剩余的分钟数
      const remainingMillisForMinutes = remainingMillisForHours % (1000 * 60 * 60);
      const minutes = Math.floor(remainingMillisForMinutes / (1000 * 60));

      // 计算剩余的秒数
      const remainingMillisForSeconds = remainingMillisForMinutes % (1000 * 60);
      const seconds = Math.floor(remainingMillisForSeconds / 1000);

      // 格式化并返回结果
      return `${days} 天 ${hours} 时 ${minutes} 分 ${seconds} 秒`;
    },
    onClickText(e) {
      if (!this.designMode) {
        this.triggerDomEvent('onClick', {}, {});
        e.stopPropagation();
      }
    }
  }
};
</script>
