<template>
  <a-drawer
    v-if="forceRender"
    :wrap-style="wrapStyle"
    :body-style="{ padding: vPadding }"
    :title="title"
    placement="right"
    :closable="widget.configuration.closable === true"
    :maskStyle="maskStyle"
    :maskClosable="widget.configuration.maskClosable === true"
    @close="onClose"
    :destroyOnClose="true"
    :visible="visible"
    :width="widget.configuration.style.width"
    :get-container="getContainer"
    :afterVisibleChange="afterVisibleChange"
    :class="drawerClass"
  >
    <div>
      <transition-group
        :key="key"
        name="fade"
        tag="div"
        class="widget-drawer-drop-panel"
        :style="{
          paddingBottom: widget.configuration.footerHidden ? 'unset' : '40px'
        }"
      >
        <template v-for="(wgt, index) in widget.configuration.widgets">
          <component
            :key="wgt.id"
            :is="resolveWidgetType(wgt)"
            :widget="wgt"
            :index="index"
            :widgetsOfParent="widget.configuration.widgets"
            :designer="designer"
            :parent="widget"
          ></component>
        </template>
      </transition-group>
      <div
        v-if="!widget.configuration.footerHidden"
        class="ant-drawer-footer"
        :style="{
          position: 'absolute',
          bottom: 0,
          width: '100%',
          borderTop: '1px solid #e8e8e8',
          padding: '10px 16px',
          textAlign: 'right',
          left: 0,
          zIndex: 2,
          background: '#fff',
          borderRadius: '0 0 4px 4px'
        }"
      >
        <WidgetTableButtons
          ref="buttons"
          :button="widget.configuration.footerButton"
          :eventWidget="getEventWidget"
          @button-click="button => buttonClicked(button)"
        />
      </div>
    </div>
  </a-drawer>
</template>
<style lang="less"></style>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import windowMixin from '@framework/vue/mixin/windowMixin';

export default {
  name: 'WidgetDrawer',
  mixins: [widgetMixin, windowMixin],
  data() {
    let wrapStyle = {};
    if (this.parent != undefined && this.parent.wtype) {
      wrapStyle.position = 'absolute';
    }
    return {
      key: this.widget.id,
      wrapStyle,
      title: this.$t(this.widget.id + '_title', this.widget.configuration.title),
      visible: false,
      forceRender: false,
      drawerClass: 'pt-drawer'
    };
  },
  provide() {
    return { $event: undefined, widgetDrawerContext: this }; // 事件传递需要
  },
  beforeCreate() {},
  components: {},
  computed: {
    defaultEvents() {
      return [
        { id: 'showDrawer', title: '打开抽屉浮层' },
        { id: 'closeDrawer', title: '关闭抽屉浮层' },
        { id: 'updateDrawerTitle', title: '更新抽屉浮层标题' }
      ];
    },
    maskStyle() {
      let style = undefined;
      if (!this.widget.configuration.mask) {
        // 无遮罩时，遮罩颜色改为透明
        style = {
          background: 'transparent'
        };
      }
      return style;
    }
  },
  created() {},
  methods: {
    setFooterButton(footerButton) {
      this.$nextTick(() => {
        this.$refs.buttons.buttonConf.buttons = footerButton;
      });
    },
    afterVisibleChange(visible) {
      if (!visible) {
        this.getContainer().style.overflowX = 'hidden';
      }
      this.pageContext.emitEvent(this.widget.id + ':drawer:afterVisibleChange', {
        visible,
        getContainer: this.getContainer,
        context: this
      });
    },
    close() {
      this.closeDrawer();
    },
    buttonClicked(button) {
      if (button.id == 'cancel') {
        this.closeDrawer();
      }
    },
    getContainer() {
      if (this.parent != undefined && this.parent.wtype) {
        return document.querySelector('#' + this.parent.id);
      }

      return document.querySelector(`div[namespace='${this.namespace}']`);
    },
    showDrawer($event, widgets, title) {
      if ($event != undefined) {
        this._provided.$event = $event;
        if ($event.eventParams && $event.eventParams.refreshVisible) {
          this.key = this.widget.id + '_' + new Date().getTime();
        }
        // 通过事件参数设置的标题
        if ($event.eventParams) {
          if ($event.eventParams.title) {
            this.title = $event.eventParams.title;
          }
        }
      }

      // 更新抽屉里面的内容组件
      if (widgets != undefined) {
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

      this.visible = true;
      if (title) {
        this.title = title;
      }
    },
    setBodyWidget(wgt) {
      if (Array.isArray(wgt)) {
        this.widget.configuration.widgets = wgt;
      } else {
        this.widget.configuration.widgets = [wgt];
      }
      this.key = this.widget.id + '_' + new Date().getTime();
    },
    onClose() {
      this.closeDrawer();
    },
    closeDrawer() {
      this.visible = false;
    },
    updateDrawerTitle(title) {
      this.title = title;
    },
    getEventWidget() {
      return this;
    }
  },
  beforeMount() {
    this.forceRender = true;
  },
  mounted() {}
};
</script>
