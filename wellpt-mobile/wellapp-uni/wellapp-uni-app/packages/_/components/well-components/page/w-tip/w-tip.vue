<template>
  <view class="w-tip" :class="customClassCom" v-if="deviceVisible" v-show="vShow">
    <uni-icons type="info" v-if="widget.configuration.tipType === 'popup'" @click="onToast"></uni-icons>
    <view
      v-else
      :class="['w-tip-alert', widget.configuration.type, widget.configuration.banner ? 'bordered' : '']"
      :style="vAlertStyle"
    >
      <view :class="['icon-pane', widget.configuration.type]">
        <view
          v-if="widget.configuration.showIconType == 'tag'"
          :class="['tag w-ellipsis-1', widget.configuration.type]"
          :style="tagStyle"
        >
          {{ widget.configuration.tipTagText }}
        </view>
        <template v-else>
          <w-icon
            :size="18"
            :iconConfig="widget.configuration.bodyIcon"
            v-if="widget.configuration.showIconCustom && widget.configuration.bodyIcon"
          />
          <u-icon v-else :name="iconName" size="18" :color="iconColor"></u-icon>
        </template>
      </view>
      <view class="message-container">
        <template v-if="widget.configuration.bodyType === 'dynamic'">
          <view :class="['announcement-scroll']">
            <view class="announcement-list" :style="listStyle" ref="announcementList">
              <view
                :class="['announcement-item', currentIndex == m ? 'active' : '']"
                v-for="(item, m) in messageSource"
                ref="announcementItem"
              >
                {{ item.message }}
              </view>
            </view>
          </view>
        </template>
        <view v-else class="message-body" ref="messageBody">
          <view :style="listStyle"> {{ staticBody }}</view>
        </view>

        <view
          class="right-button flex f_y_c"
          v-if="
            widget.configuration.rightButton &&
            widget.configuration.rightButton.enable &&
            widget.configuration.rightButton.buttons.length > 0
          "
        >
          <w-button
            :button="widget.configuration.rightButton"
            class="f_wrap"
            :parentWidget="parentWidget"
            :meta="getButtonMeta"
            size="small"
          ></w-button>
        </view>
      </view>
    </view>
    <u-toast ref="uToast"></u-toast>
  </view>
</template>

<script>
import mixin from "../page-widget-mixin";
import { DataStore } from "wellapp-uni-framework";
import { isEmpty, isNumber } from "lodash";

// #ifndef APP-PLUS
import "./index.scss";
// #endif
export default {
  name: "w-tip",
  mixins: [mixin],
  data() {
    return {
      currentIndex: 0,
      messageSource: [],
      itemHeight: 32,
      interval: (this.widget.configuration.interval || 3) * 1000,
      animate: true,
      isPaused: false,
      isHorizontalScroll: false,
      translateX: 0,
      transformXSeconds: 0,
      staticBody: undefined,
    };
  },
  components: {},
  computed: {
    tagStyle() {
      let style = {};
      style.backgroundColor = {
        info: "#108ee9",
        success: "#87d068",
        warning: "#fa8c16",
        error: "#f5222d",
      }[this.widget.configuration.type];

      return style;
    },
    iconColor() {
      switch (this.widget.configuration.type) {
        case "success":
          return "#52c41a";
        case "error":
          return "#f5222d";
        case "warning":
          return "#faad14";
        case "info":
          return "#1890ff";
        default:
          return "";
      }
    },
    iconName() {
      switch (this.widget.configuration.type) {
        case "success":
          return "checkmark-circle-fill";
        case "error":
          return "close-circle-fill";
        case "warning":
          return "info-circle-fill";
        case "info":
          return "info-circle-fill";
        case "primary":
          return "more-circle-fill";
        default:
          return "error-circle-fill";
      }
    },
    vAlertStyle() {
      let style = {};
      if (this.widget.configuration.banner) {
        style.borderColor = this.iconColor;
      }
      if (
        this.widget.configuration.enableCustomBgColor &&
        (this.widget.configuration.backgroundColorVar || this.widget.configuration.backgroundColor)
      ) {
        style.backgroundColor = this.widget.configuration.useBgColorVar
          ? `var(${this.widget.configuration.backgroundColorVar})`
          : this.widget.configuration.backgroundColor;
      }

      if (
        this.widget.configuration.enableCustomBorderColor &&
        this.widget.configuration.banner &&
        (this.widget.configuration.borderColor || this.widget.configuration.borderColorVar)
      ) {
        style.borderColor = this.widget.configuration.useBorderColorVar
          ? `var(${this.widget.configuration.borderColorVar})`
          : this.widget.configuration.borderColor;
      }

      if (this.widget.configuration.margin || this.widget.configuration.margin === 0) {
        style.margin = this.styleValueFormat(this.widget.configuration.margin);
      }
      if (this.widget.configuration.padding || this.widget.configuration.padding === 0) {
        style.padding = this.styleValueFormat(this.widget.configuration.padding);
      }
      if (this.widget.configuration.borderRadius || this.widget.configuration.borderRadius === 0) {
        style.borderRadius = this.styleValueFormat(this.widget.configuration.borderRadius);
      }
      return style;
    },
    listStyle() {
      const translateY = -this.currentIndex * this.itemHeight;
      let style = {};

      if (!this.isHorizontalScroll) {
        style.transition = this.animate ? "transform 0.2s ease-in-out" : "none";
        style.transform = `translateY(${translateY}px)`;
      } else {
        style.transition = this.animate ? `transform ${this.transformXSeconds}s linear` : "none";
        style.transform = `translate(${this.translateX}px,${translateY}px)`;
      }

      if (
        this.widget.configuration.enableCustomFontColor &&
        (this.widget.configuration.fontColor || this.widget.configuration.fontColorVar)
      ) {
        style.color = this.widget.configuration.useFontColorVar
          ? `var(${this.widget.configuration.fontColorVar})`
          : this.widget.configuration.fontColor;
      }

      return style;
    },
  },
  created() {},
  methods: {
    getButtonMeta() {
      return this.currentIndex >= 0 && this.messageSource.length > 0
        ? { TIP_DATA: this.messageSource[this.currentIndex].originalData }
        : { TIP_DATA: {} };
    },
    parentWidget() {
      return this;
    },
    /**
     * 获取数据源对象
     */
    getDataSourceProvider: function () {
      if (this.dataSourceProvider == undefined) {
        // 创建数据源
        let dsOptions = {
          receiver: this,
          params: {},
          autoCount: false,
          pageSize:
            this.widget.configuration.enableLimitSize && this.widget.configuration.limitSize != undefined
              ? this.widget.configuration.limitSize
              : undefined,
          defaultCriterions: this.getDefaultCondition(),
        };
        if (this.widget.configuration.dataSourceId && this.widget.configuration.dynamicBodyFrom == "dataSource") {
          dsOptions.dataStoreId = this.widget.configuration.dataSourceId;
          this.dataSourceProvider = new DataStore(dsOptions);
        } else if (
          this.widget.configuration.dynamicBodyFrom == "dataModel" &&
          this.widget.configuration.dataModelUuid
        ) {
          dsOptions.loadDataUrl = "/api/dm/loadData/" + this.widget.configuration.dataModelUuid;
          dsOptions.loadDataCntUrl = "/api/dm/loadDataCount/" + this.widget.configuration.dataModelUuid;
          this.dataSourceProvider = new DataStore(dsOptions);
        }
      }
      return this.dataSourceProvider;
    },
    /**
     * 获取默认条件查询
     */
    getDefaultCondition() {
      var defaultConditions = [];
      var configuration = this.widget.configuration;
      if (!isEmpty(configuration.defaultCondition)) {
        var criterion = {};
        criterion.sql = configuration.defaultCondition;
        defaultConditions.push(criterion);
      }
      return defaultConditions;
    },
    loadMessageSource() {
      if (this.widget.configuration.tipType == "fixed" && this.widget.configuration.bodyType == "dynamic") {
        this.messageSource.splice(0, this.messageSource.length);
        this.getDataSourceProvider()
          .load()
          .then(({ data }) => {
            this.currentIndex = 0;
            let items = [];
            for (let d of data) {
              items.push({
                message: d[this.widget.configuration.bodyColumn],
                originalData: d,
              });
            }
            this.messageSource.push(...items);
            this.$nextTick(() => {
              this.startScrolling();
            });
          });
      }
    },
    startHorizontalScrolling() {
      // 判断滚动内容是否超过容器宽度，如果超过则横向滚动展示内容
      this.$nextTick(() => {
        let item =
          this.widget.configuration.bodyType == "dynamic"
            ? this.$refs.announcementItem[this.currentIndex]
            : this.$refs.messageBody;
        if (item.$el.scrollWidth > (this.messageBodyClientWidth || this.announcementListWidth)) {
          this.horizontalScrolling = true;
          this.isHorizontalScroll = true;
          this.animate = false;
          let index = this.currentIndex;
          setTimeout(() => {
            this.translateX = -item.$el.scrollWidth;
            this.transformXSeconds = parseInt(item.$el.scrollWidth / 100);
            this.animate = true;
            this.resetHorizontalScroll(item.$el.scrollWidth, index);
          }, 500);
          // this.appendScrollContent(item.innerText, this.transformXSeconds, item);
        } else {
          this.translateX = 0;
          this.transformXSeconds = 0;
          this.isHorizontalScroll = false;
        }
      });
    },
    resetHorizontalScroll(scrollWidth, index) {
      this.resetHorScrollTimeout = setTimeout(() => {
        if (this.isHorizontalScroll) {
          if (this.widget.configuration.bodyType == "dynamic") {
            this.translateX = 0;
            this.animate = false;
            this.horizontalScrolling = false;
            setTimeout(() => {
              if (index == this.currentIndex && this.isHorizontalScroll) {
                this.animate = true;
                this.translateX = -scrollWidth;
              }
            }, 500);
          } else {
            this.translateX = this.messageBodyClientWidth;
            this.animate = false;
            this.horizontalScrolling = false;
            setTimeout(() => {
              this.animate = true;
              this.translateX = -scrollWidth;
            }, 100);
          }
          this.resetHorizontalScroll(scrollWidth, index);
        }
      }, this.transformXSeconds * 1000 + (this.widget.configuration.bodyType == "dynamic" ? 500 : 100));
    },

    // 开始滚动
    startScrolling() {
      if (this.messageSource.length <= 1) {
        this.startHorizontalScrolling();
        return; // 只有一条公告时不滚动
      }
      this.startHorizontalScrolling();
      this.stopScrolling(); // 先清除之前的定时器
      this.timer = setInterval(() => {
        if (this.isPaused || this.isAnimating || this.horizontalScrolling) return;
        if (this.nextRoundReset) {
          this.currentIndex = -1;
          this.nextRoundReset = false;
        }
        this.scrollNext();
        this.startHorizontalScrolling();
      }, this.interval);
    },

    // 滚动到下一条
    scrollNext() {
      if (!this.messageSource || this.messageSource.length === 0) return;
      this.isHorizontalScroll = false;
      this.horizontalScrolling = false;
      this.isAnimating = true;
      this.currentIndex++;
      this.translateX = 0;
      this.animate = true;
      clearTimeout(this.resetHorScrollTimeout);
      // 当滚动最后一条时，重置到第一条
      if (this.currentIndex + 1 >= this.messageSource.length) {
        this.nextRoundReset = true;
        setTimeout(() => {
          this.animate = false;
          this.isAnimating = false;
        }, 300); // 等待当前动画完成
      } else {
        setTimeout(() => {
          this.isAnimating = false;
        }, 300);
      }
    },
    // 停止滚动
    stopScrolling() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },

    // 暂停/恢复滚动
    togglePause() {
      this.isPaused = !this.isPaused;
    },

    onToast() {
      this.$refs.uToast.show({
        message: this.widget.configuration.body,
      });
    },
    styleValueFormat(value) {
      if (isNumber(value)) {
        return value + "px";
      } else if (Array.isArray(value)) {
        return value
          .map((item) => {
            return (item || 0) + "px";
          })
          .join(" ");
      }
      return value;
    },
  },
  mounted() {
    if (this.deviceVisible) {
      this.$nextTick(() => {
        if (this.widget.configuration.tipType == "fixed") {
          if (this.widget.configuration.bodyType == "dynamic") {
            if (this.$refs.announcementList != undefined) {
              this.announcementListWidth = this.$refs.announcementList.$el.clientWidth;
              this.loadMessageSource();
            }
          } else {
            this.messageBodyClientWidth = this.$refs.messageBody.$el.clientWidth;
            setTimeout(() => {
              this.staticBody = this.widget.configuration.body;
              this.$nextTick(() => {
                this.startHorizontalScrolling();
              });
            }, 100);
          }
        }
      });
    }
  },
};
</script>
<style lang="scss">
/* #ifdef APP-PLUS */
@import "./index.scss";
/* #endif */
</style>
