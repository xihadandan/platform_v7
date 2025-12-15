<template>
  <div :id="widget.id">
    <a-modal
      :visible="visible"
      :width="vWidth"
      :footer="widget.configuration.footerHidden ? null : undefined"
      :bodyStyle="bodyStyle"
      :destroyOnClose="destroyOnClose"
      :afterClose="afterClose"
      @cancel="closeModal"
      :closable="widget.configuration.closable || widget.configuration.closable === undefined"
      :maskClosable="maskClosable"
      :id="widget.id + '_modal'"
      :dialogClass="dialogClass"
    >
      <div slot="title" v-if="showTitle" class="flex f_y_s f_x_s">
        <div v-if="!widget.configuration.hiddenTitle">
          <span v-html="title || '&nbsp;'"></span>
        </div>
        <div v-else>&nbsp;</div>
        <div v-if="widget.configuration.switchFullscreen">
          <a-button
            :title="fullscreen ? $t('WidgetModal.exitFullscreen', '退出全屏') : $t('WidgetModal.fullscreen', '全屏')"
            type="text"
            class="fullscreen-btn"
            @click.stop="onFullScreen"
          >
            <Icon :type="fullscreen ? 'pticon iconfont icon-ptkj-tuichuquanping' : 'pticon iconfont icon-ptkj-quanping'"></Icon>
          </a-button>
        </div>
      </div>
      <div slot="footer">
        <WidgetTableButtons
          :button="widget.configuration.footerButton"
          :key="buttonKey"
          :developJsInstance="developJsInstance"
          :eventWidget="getEventWidget"
          :meta="getMetaData"
          :parentWidget="getSelf"
          @button-click="button => buttonClicked(button)"
        />
      </div>

      <a-skeleton active v-if="loading" />
      <PerfectScrollbar
        v-else
        :style="{ height: selfDefineHeight, maxHeight: vHeight, width: 'calc(100% + 1px)' }"
        :key="key"
        ref="scroll"
        :options="scrollOptions"
        @mouseenter.native.stop="onmouseenterScroll"
      >
        <template
          v-if="
            widget.configuration.contentFromPage &&
            widget.configuration.eventHandler &&
            (widget.configuration.eventHandler.pageId != undefined || widget.configuration.eventHandler.url != undefined)
          "
        >
          <WidgetVpage
            v-if="widget.configuration.eventHandler.pageType == 'page' && widget.configuration.eventHandler.pageId != undefined"
            :height="vHeight"
            :pageId="widget.configuration.eventHandler.pageId"
            :parent="widget"
          />
          <iframe
            :id="widget.id"
            v-else-if="widget.configuration.eventHandler.pageType == 'url' && widget.configuration.eventHandler.url != undefined"
            :src="widget.configuration.eventHandler.url"
            :style="{ minHeight: vHeight, border: 'none', width: '100%' }"
          ></iframe>
        </template>
        <template v-else>
          <template v-if="renderUrl">
            <iframe :src="renderUrl" :style="{ minHeight: vHeight, border: 'none', width: '100%' }"></iframe>
          </template>
          <template v-else-if="content">
            <div v-html="content"></div>
          </template>
          <template v-else v-for="(wgt, index) in widget.configuration.widgets">
            <component
              :key="wgt.id"
              :ref="wgt.id"
              :is="wgt.wtype"
              :widget="wgt"
              :index="index"
              :parent="widget"
              :containerHeight="vHeight"
              :widgetsOfParent="widget.configuration.widgets"
              v-bind="wgt.props"
            ></component>
          </template>
        </template>
      </PerfectScrollbar>
      <!-- <div class="widget-modal-body" :key="key">

      </div> -->
    </a-modal>
  </div>
</template>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
import windowMixin from '@framework/vue/mixin/windowMixin';
import { addWindowResizeHandler } from '@framework/vue/utils/util';
import { isNumber } from 'lodash';
import './css/index.less';

export default {
  name: 'WidgetModal',
  mixins: [widgetMixin, windowMixin],
  provide() {
    return { $event: undefined }; // 事件传递需要
  },
  data() {
    return {
      buttonAlign: 'right',
      title: this.$t(this.widget.id + '_title', this.widget.configuration.title),
      key: this.widget.id,
      buttonKey: this.widget.id + '_footerButton',
      visible: false,
      renderUrl: undefined,
      width: 500,
      destroyOnClose: this.widget.configuration.destroyOnClose !== false,
      maskClosable: this.widget.configuration.hiddenTitle || false,
      handleEvents: [],
      buttonProps: {},
      $evtWidget: null,
      maxHeight: undefined,
      loading: false,
      content: undefined,
      bodyStyle: {},
      closable: this.widget.configuration.closable || this.widget.configuration.closable === undefined,
      fullscreen: this.widget.configuration.fullscreen,
      windowWidth: '0px',
      windowHeight: '0px',
      $modal: undefined,
      vHeightChangeFlag: false,
      scrollOptions: { scrollYMarginOffset: 1, scrollXMarginOffset: 1 }
    };
  },
  watch: {
    selfDefineHeight() {}
  },
  beforeCreate() {},
  components: {},
  computed: {
    selfDefineHeight() {
      if (this.fullscreen) {
        return this.vHeight;
      }
      if (
        this.widget.configuration.size == 'selfDefine' &&
        this.widget.configuration.height != undefined &&
        this.widget.configuration.height != 'auto'
      ) {
        return this.initHeight;
      }
      return 'auto';
    },
    vHeight() {
      let vHeightChangeFlag = this.vHeightChangeFlag;
      this.scrollUpdateHandler();
      if (this.fullscreen) {
        let height = this.windowHeight;
        if (this.$modal) {
          this.$modal.style.position = 'unset';
          this.$modal.style['padding-bottom'] = '0px';
          if (this.showTitle) {
            height = height - this.$modal.querySelector('.ant-modal-header').clientHeight;
          }
          if (!this.widget.configuration.footerHidden) {
            height = height - this.$modal.querySelector('.ant-modal-footer').clientHeight;
          }
        }
        return `calc(${height}px - var(--w-pt-modal-body-padding-t) - var(--w-pt-modal-body-padding-b) - 2px)`;
      }
      return this.initHeight;
    },
    initHeight() {
      if (this.widget.configuration.size == 'selfDefine') {
        return this.widget.configuration.height
          ? isNumber(this.widget.configuration.height)
            ? this.widget.configuration.height + 'px'
            : this.widget.configuration.height
          : 500 + 'px';
      }
      return 'calc(100vh - 400px)';
    },
    vWidth() {
      if (this.fullscreen) {
        return this.windowWidth;
      }
      return this.initWidth;
    },
    initWidth() {
      if (this.widget.configuration.size == 'selfDefine') {
        return this.widget.configuration.width || 500;
      }
      if (EASY_ENV_IS_BROWSER) {
        let screenWidth = window.screen.width;
        // 常见屏幕： 1920 、1440 、 1366 、1280
        if (this.widget.configuration.size === 'small') {
          if (screenWidth <= 1280) {
            return 520;
          } else if (screenWidth > 1280 && screenWidth <= 1366) {
            return 580;
          } else if (screenWidth > 1366 && screenWidth <= 1440) {
            return 640;
          } else if (screenWidth > 1440 && screenWidth <= 1920) {
            return 700;
          }
          return 760;
        } else if (this.widget.configuration.size === 'middle') {
          if (screenWidth <= 1280) {
            return 580;
          } else if (screenWidth > 1280 && screenWidth <= 1366) {
            return 700;
          } else if (screenWidth > 1366 && screenWidth <= 1440) {
            return 900;
          } else if (screenWidth > 1440 && screenWidth <= 1920) {
            return 1100;
          }
          return 820;
        } else if (this.widget.configuration.size === 'large') {
          if (screenWidth <= 1280) {
            return 760;
          } else if (screenWidth > 1280 && screenWidth <= 1366) {
            return 1100;
          } else if (screenWidth > 1366 && screenWidth <= 1440) {
            return 1200;
          } else if (screenWidth > 1440 && screenWidth <= 1920) {
            return 1400;
          }
          return 1200;
        }
        return 520;
      }
      return 0;
    },
    defaultEvents() {
      return [
        {
          id: 'showModal',
          title: '打开弹窗'
        },
        {
          id: 'closeModal',
          title: '关闭弹窗'
        },
        {
          id: 'updateModalTitle',
          title: '更新弹窗标题'
        },
        {
          id: 'refresh',
          title: '刷新弹窗内容'
        }
      ];
    },
    // 弹框标题，关闭按钮，全屏按钮只要有一个，就显示
    showTitle() {
      return !this.widget.configuration.hiddenTitle || this.widget.configuration.closable || this.widget.configuration.switchFullscreen;
    },
    dialogClass() {
      return [
        'pt-modal widget-modal footer-align-' + this.buttonAlign,
        this.widget.configuration.footerHidden ? 'no-footer' : '',
        this.closable ? '' : 'unclosable'
      ].join(' ');
    }
  },
  created() {},
  methods: {
    close() {
      this.closeModal();
    },
    // getContainer() {
    //   // return this.$el;
    //   // document.querySelector(`[namespace='${this.namespace}']`) || this.$el ||
    //   return document.body;
    // },
    //Modal 完全关闭后的回调
    afterClose() {
      if (this.widget.configuration.afterCloseEventHandler && this.widget.configuration.afterCloseEventHandler.actionType) {
        let _this = this,
          eventHandler = this.widget.configuration.afterCloseEventHandler;

        eventHandler.pageContext = this.pageContext;
        eventHandler.$evtWidget = this;

        // 元数据通过事件传递

        let developJs = this.$developJsInstance;
        if (developJs == undefined) {
          developJs = {};
        }
        if (this.$pageJsInstance != undefined) {
          developJs[this.$pageJsInstance._JS_META_] = this.$pageJsInstance;
        }
        eventHandler.$developJsInstance = developJs;

        if (eventHandler.actionType) {
          new DispatchEvent(eventHandler).dispatch();
        }
      }
    },
    getSelf() {
      return this;
    },
    getEventWidget() {
      return this.buttonEventWidget || this;
    },
    getMetaData() {
      return this.buttonMetaData || {};
    },

    refresh() {
      this.loading = true;
      this.$nextTick(() => {
        this.loading = false;
        // this.key = `${this.widget.id}_` + new Date().getTime();
      });
    },

    setBodyWidget(wgt) {
      if (Array.isArray(wgt)) {
        this.widget.configuration.widgets = wgt;
      } else {
        this.widget.configuration.widgets = [wgt];
      }
      this.key = this.widget.id + '_' + new Date().getTime();
    },
    showModal($event, widgets, title) {
      this.visible = true;
      let ispass = true;
      if ($event != undefined) {
        this._provided.$event = $event;
        // // 通过组件事件机制派发过来的
        // // 事件数据传递给子组件
        // for (let i = 0, len = this.widget.configuration.widgets.length; i < len; i++) {
        //   this.$set(this.widget.configuration.widgets[i], 'props', arguments[0]);
        // }
        // 通过事件参数设置的标题
        if ($event.eventParams) {
          if ($event.eventParams.title) {
            this.title = $event.eventParams.title;
          }
          if ($event.eventParams.fullscreen !== undefined) {
            this.fullscreen = $event.eventParams.fullscreen;
          }
          if ($event.eventParams.content) {
            this.key = this.widget.id + '_' + new Date().getTime();
            this.content = $event.eventParams.content;
            this.renderUrl = undefined;
            ispass = false;
          }
        }
      }

      // 更新弹窗里面的内容组件
      if (ispass && widgets != undefined) {
        this.content = undefined;
        this.renderUrl = undefined;
        if (typeof widgets === 'string') {
          // url 地址
          this.renderUrl = widgets;
        } else if (widgets instanceof Promise) {
          this.loading = true;
          widgets
            .then(d => {
              this.setBodyWidget(d.widgets);
              this.loading = false;
            })
            .catch(() => {
              this.$message.error('加载内容异常');
            });
        } else {
          this.setBodyWidget(widgets);
        }
      }

      if (title) {
        this.title = title;
      }

      this.$nextTick(() => {
        this.modalMounted();
      });
    },
    onmouseenterScroll() {
      // if (!this.scrollUpdate) {
      //   this.$refs.scroll.update();
      //   this.scrollUpdate = true;
      // }
    },
    scrollUpdateHandler() {
      // if (this.$refs.scroll) {
      //   this.$refs.scroll.update();
      // }
    },
    updateModalTitle(title) {
      this.title = title;
    },
    closeModal() {
      this.visible = false;
      this.scrollUpdate = false;
      this.$refs.scroll.destroy();
    },
    buttonClicked(button) {
      if (button.CANCEL_BUTTON || button.id == 'cancel' /** 兼容旧ID */) {
        // 取消按钮
        this.closeModal();
      }
    },
    onFullScreen() {
      if (this.fullscreen) {
        this.restoreModal();
      } else {
        this.fullscreen = true;
      }
    },
    restoreModal() {
      this.fullscreen = false;
      this.$modal.style.position = 'relative';
      this.$modal.style['padding-bottom'] = '24px';
    },
    modalMounted() {
      if (document.querySelector(`#${this.widget.id}_modal`)) {
        this.$modal = document.querySelector(`#${this.widget.id}_modal`).querySelector('.widget-modal');
        if (this.fullscreen) {
          this.$set(this, 'vHeightChangeFlag', !this.vHeightChangeFlag);
        }
      } else {
        setTimeout(() => {
          this.modalMounted();
        }, 200);
      }
    }
  },
  beforeMount() {
    this.maxHeight = window.innerHeight - 100;
  },
  mounted() {
    let _this = this;
    this.$evtWidget = _this;

    this.handleEvents.push(`WidgetModal:${this.widget.id}:SetButton`);
    this.pageContext.handleEvent(`WidgetModal:${this.widget.id}:SetButton`, function (button, $widget, meta) {
      _this.buttonEventWidget = $widget;
      _this.buttonMetaData = meta;
      _this.widget.configuration.footerButton = button;
      _this.buttonKey = `${_this.widget.id}_footerButton_${new Date().getTime()}`;
      if (meta && meta.$dyform) {
        // 数据模型下的弹窗切换表单上下文
        _this.widgetDyformContext = meta.$dyform;
      }
      // _this.buttons = buttons;
      // _this.buttonAlign = buttonAlign || _this.buttonAlign;
      // _this.buttonProps = buttonProps || _this.buttonProps;
      // _this.buttonKey = new Date().getTime();
    });
    this.handleEvents.push(`WidgetModal:${this.widget.id}:Title`);
    this.pageContext.handleEvent(`WidgetModal:${this.widget.id}:Title`, function (title) {
      _this.title = title;
    });
    this.windowHeight = window.innerHeight;
    this.windowWidth = window.innerWidth;
    addWindowResizeHandler(() => {
      this.$nextTick(() => {
        this.windowHeight = window.innerHeight;
        this.windowWidth = window.innerWidth;
      });
    });
    // 默认展示弹框，自动打开，无触发器
    if (!this.designMode && this.widget.configuration.defaultVisible) {
      this.showModal();
    }
  },
  beforeDestroy() {
    for (let i = 0, len = this.handleEvents.length; i < len; i++) {
      this.pageContext.offEvent(this.handleEvents[i]);
    }
  }
};
</script>
