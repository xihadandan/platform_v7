<template>
  <a-tooltip
    v-show="vShow"
    placement="right"
    overlayClassName="widget-tooltip-overlay"
    v-if="widget.configuration.tipType === 'popup'"
    :id="widget.id"
  >
    <template slot="title">
      <span>{{ $t(widget.id, widget.configuration.body) }}</span>
    </template>
    <span class="widget-tooltip"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" /></span>
  </a-tooltip>
  <a-alert
    :id="widget.id"
    :show-icon="widget.configuration.showIcon"
    v-else
    v-show="userTip && vShow"
    :type="widget.configuration.type"
    :closable="widget.configuration.closable"
    :class="['widget-tip', 'alert', widget.configuration.showIconType == 'tag' ? 'tag-icon' : undefined]"
    :banner="!widget.configuration.banner"
    :style="vAlertStyle"
  >
    <template slot="icon">
      <a-tag
        v-if="widget.configuration.showIconType == 'tag'"
        :color="tagColorType[widget.configuration.type]"
        style="max-width: 80px"
        class="w-ellipsis"
      >
        {{ widget.configuration.tipTagText }}
      </a-tag>

      <Icon :type="alertIcon" v-else style="font-size: var(--w-font-size-base)"></Icon>
    </template>
    <template slot="message">
      <div :class="['message-container', isPaused ? 'paused' : undefined]" @mouseenter="togglePause" @mouseleave="togglePause">
        <template v-if="widget.configuration.bodyType === 'dynamic'">
          <div :class="['announcement-scroll']">
            <div class="announcement-list" :style="listStyle" ref="announcementList">
              <div :class="['announcement-item']" v-for="(item, m) in messageSource" :key="'msg_' + m" ref="announcementItem">
                {{ item.message }}
              </div>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="message-body" :style="messageBodyStyle">
            {{ fixedMessage }}
          </div>
        </template>
        <span v-if="widget.configuration.rightButton && widget.configuration.rightButton.enable">
          <WidgetTableButtons :button="widget.configuration.rightButton" :developJsInstance="developJsInstance" :meta="getMessageMeta" />
        </span>
      </div>
    </template>
  </a-alert>
</template>
<style lang="less">
.widget-tip {
  .message-container {
    display: flex;
    width: 100%;
    &.paused .announcement-list {
      animation-play-state: paused;
      animation: none;
    }
    .message-body {
      width: 100%;
      line-height: 32px;
    }
  }
  .announcement-scroll {
    width: 100%;
    height: 32px;
    line-height: 32px;
    overflow: hidden;
    position: relative;
    /* 暂停动画状态 */

    .announcement-list {
      position: absolute;
      width: 100%;
      transition: transform 0.5s ease-in-out;
      .announcement-item {
        width: 100%;
        height: 32px;
        line-height: 32px;
        white-space: nowrap;
        cursor: pointer;
        display: flex;
        align-items: center;
      }
    }
  }
}
</style>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import './css/index.less';
import { debounce, sortBy, template as stringTemplate, get, cloneDeep, orderBy, trim, isNumber } from 'lodash';
import DataSourceBase from '@pageAssembly/app/web/assets/js/commons/dataSource.base.js';

export default {
  name: 'WidgetTip',
  mixins: [widgetMixin],
  inject: ['designer'],
  data() {
    return {
      tagColorType: {
        info: '#108ee9',
        success: '#87d068',
        warning: '#fa8c16',
        error: '#f5222d'
      },
      userTip: this.designMode,
      currentIndex: 0,
      messageSource: [],
      itemHeight: 32,
      interval: (this.widget.configuration.interval || 3) * 1000,
      animate: true,
      isPaused: false,
      isHorizontalScroll: false,
      translateX: 0,
      transformXSeconds: 0
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    vAlertStyle() {
      let style = {};
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
      if (this.widget.configuration.borderRadius || this.widget.configuration.borderRadius === 0) {
        style.borderRadius = this.styleValueFormat(this.widget.configuration.borderRadius);
      }

      return style;
    },
    messageBodyStyle() {
      let style = {};
      if (
        this.widget.configuration.enableCustomFontColor &&
        (this.widget.configuration.fontColor || this.widget.configuration.fontColorVar)
      ) {
        style.color = this.widget.configuration.useFontColorVar
          ? `var(${this.widget.configuration.fontColorVar})`
          : this.widget.configuration.fontColor;
      }
      if (this.designMode && this.designer.terminalType !== 'pc') {
        style['white-space'] = 'nowrap';
        style['overflow'] = 'hidden';
      }
      return style;
    },
    listStyle() {
      const translateY = -this.currentIndex * this.itemHeight;
      let style = {};

      if (!this.isHorizontalScroll) {
        style.transition = this.animate ? 'transform 0.2s ease-in-out' : 'none';
        style.transform = `translateY(${translateY}px)`;
      } else {
        style.transition = this.animate ? `transform ${this.transformXSeconds}s linear` : 'none';
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

    fixedMessage() {
      if (this.widget.configuration.tipType === 'fixed') {
        if (this.widget.configuration.body && this.widget.configuration.bodyType !== 'dynamic') {
          return this.$t(this.widget.id, this.widget.configuration.body);
        }
        if (this.widget.configuration.bodyType == 'dynamic' && this.messageSource.length > 0) {
          return this.messageSource[this.messageIndex].message;
        }
      }
      return '';
    },
    alertIcon() {
      if (this.widget.configuration.tipType === 'fixed') {
        if (this.widget.configuration.showIconCustom && this.widget.configuration.bodyIcon) {
          return this.widget.configuration.bodyIcon;
        }
        if (this.widget.configuration.type == 'info') {
          return 'iconfont icon-ptkj-mianxingwenxintishi';
        } else if (this.widget.configuration.type == 'success') {
          return 'iconfont icon-ptkj-mianxingchenggongtishi';
        } else if (this.widget.configuration.type == 'error') {
          return 'iconfont icon-ptkj-mianxingshibaitishi';
        } else if (this.widget.configuration.type == 'warning') {
          return 'iconfont icon-ptkj-mianxingwenxintishi';
        }
      }
      return '';
    }
  },
  created() {},
  methods: {
    getMessageMeta() {
      return this.currentIndex >= 0 && this.messageSource.length > 0
        ? { TIP_DATA: this.messageSource[this.currentIndex].originalData }
        : { TIP_DATA: {} };
    },
    getDefaultCondition() {
      if (this.widget.configuration.defaultCondition) {
        let compiler = stringTemplate(this.widget.configuration.defaultCondition);
        let sql = this.widget.configuration.defaultCondition;
        try {
          // 在表单域内
          let data = this.widgetDependentVariableDataSource();
          if (this.dyform) {
            Object.assign(data, {
              FORM_DATA: this.dyform.formData
            });
          }
          sql = compiler(data);
        } catch (error) {
          console.error('解析模板字符串错误: ', error);
          throw new Error('表格默认条件变量解析异常');
        }
        return sql != undefined && sql.trim() !== '' ? [{ sql }] : [];
      }
      return [];
    },
    getDataSourceProvider() {
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
          defaultCriterions: this.getDefaultCondition()
        };
        if (this.widget.configuration.dataSourceId && this.widget.configuration.dynamicBodyFrom == 'dataSource') {
          dsOptions.dataStoreId = this.widget.configuration.dataSourceId;
          this.dataSourceProvider = new DataSourceBase(dsOptions);
        } else if (this.widget.configuration.dynamicBodyFrom == 'dataModel' && this.widget.configuration.dataModelUuid) {
          dsOptions.loadDataUrl = '/proxy/api/dm/loadData/' + this.configuration.dataModelUuid;
          dsOptions.loadDataCntUrl = '/proxy/api/dm/loadDataCount/' + this.configuration.dataModelUuid;
          this.dataSourceProvider = new DataSourceBase(dsOptions);
        }
      }
      return this.dataSourceProvider;
    },
    loadMessageSource() {
      if (this.widget.configuration.tipType == 'fixed' && this.widget.configuration.bodyType == 'dynamic') {
        this.messageSource.splice(0, this.messageSource.length);
        this.getDataSourceProvider()
          .load()
          .then(({ data }) => {
            this.currentIndex = 0;
            let items = [];
            for (let d of data) {
              items.push({
                message: d[this.widget.configuration.bodyColumn],
                originalData: d
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
        let item = this.$refs.announcementItem[this.currentIndex];
        if (item.scrollWidth > this.announcementListWidth) {
          this.horizontalScrolling = true;
          this.isHorizontalScroll = true;
          this.animate = false;
          let index = this.currentIndex;
          setTimeout(() => {
            this.translateX = -item.scrollWidth;
            this.transformXSeconds = parseInt(item.scrollWidth / 100);
            this.animate = true;
            this.resetHorizontalScroll(item.scrollWidth, index);
          }, 500);
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
          this.translateX = 0;
          this.animate = false;
          this.horizontalScrolling = false;
          setTimeout(() => {
            if (index == this.currentIndex && this.isHorizontalScroll) {
              this.animate = true;
              this.translateX = -scrollWidth;
            }
          }, 500);
          this.resetHorizontalScroll(scrollWidth, index);
        }
      }, this.transformXSeconds * 1000 + 500);
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

    ifUserTip() {
      let _this = this;
      if (!this.designMode && this.widget.configuration.tipType === 'fixed') {
        if (!this.widget.configuration.noTipAgain) {
          this.userTip = true;
        } else {
          let params = { moduleId: this.widget.wtype, functionId: 'NO_TIP_AGAIN', dataKey: `${this.widget.id}` };
          $axios
            .get(`/api/user/preferences/get`, {
              params: { ...params }
            })
            .then(({ data }) => {
              if (data.code == 0 && data.data.uuid == null) {
                _this.userTip = true;
                // 用户个性化设置
                $axios
                  .post(`/api/user/preferences/save`, {
                    ...params,
                    dataValue: '',
                    remark: '用户提示已查看, 不再提示'
                  })
                  .then(({ data }) => {});
              }
            });
        }
      }
    },
    styleValueFormat(value) {
      if (isNumber(value)) {
        return value + 'px';
      } else if (Array.isArray(value)) {
        return value
          .map(item => {
            return (item || 0) + 'px';
          })
          .join(' ');
      }
      return value;
    }
  },
  beforeMount() {},
  mounted() {
    this.ifUserTip();
    this.$nextTick(() => {
      this.announcementListWidth = this.$refs.announcementList && this.$refs.announcementList.clientWidth;
      this.loadMessageSource();
    });

    if (this.widget.configuration.tipType == 'fixed' && this.widget.configuration.bodyType == 'dynamic') {
      // 页面可见性变化时控制滚动
      // document.addEventListener('visibilitychange', () => {
      //   if (document.hidden) {
      //     this.stopScrolling();
      //   } else {
      //     this.startScrolling();
      //   }
      // });
    }
  }
};
</script>
