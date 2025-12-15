<template>
  <div>
    <a-icon
      type="double-right"
      :class="['right-collapse', collapse ? 'collapsed' : '']"
      @click.native.stop="onClickCollapse"
      :title="collapse ? '点击展开' : '点击收缩'"
    />

    <div class="edit-widget-property-container">
      <a-tabs :activeKey="vPropertyWidgetType == null ? 'basicInfo' : activeKey" @change="onTabChange" @tabClick="onTabClick">
        <slot name="basicInfoTab">
          <a-tab-pane key="basicInfo" tab="基本信息"></a-tab-pane>
        </slot>

        <a-tab-pane
          key="compInfo"
          :tab="designer.selectedWidget && vPropertyWidgetType ? designer.selectedWidget.name || designer.selectedWidget.title : null"
          v-show="designer.selectedWidget && designer.selectedId"
        >
          <Scroll ref="scroll">
            <div class="component-configure-container">
              <KeepAlive>
                <component
                  v-if="designer.selectedWidget && designer.selectedWidget.configuration != undefined"
                  ref="configurationComp"
                  :is="vPropertyWidgetType"
                  :designer="designer"
                  :parent="vParent"
                  :key="vKey"
                  :widget="designer.selectedWidget"
                  @click.native.stop="onClickConfiguration"
                ></component>
              </KeepAlive>
            </div>
          </Scroll>
        </a-tab-pane>
        <template slot="tabBarExtraContent">
          <slot name="tabBarExtraContent"></slot>
        </template>
      </a-tabs>
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'WidgetConfigurationPanel',
  props: {
    designer: Object,
    unFoundConfigureTypeResolver: Function
  },
  inject: ['layoutFixed'],
  data() {
    let collapse = this.layoutFixed ? true : false;
    return { key: 0, activeKey: 'basicInfo', collapse };
  },
  provide() {
    return {
      designer: this.designer,
      designConfigPropertyPopupContainer: this.designConfigPropertyPopupContainer
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    vParent() {
      if (this.designer.selectedWidget) {
        let node = this.designer.widgetTreeMap[this.designer.selectedWidget.id];
        if (node && node.parentKey != -1) {
          return this.designer.widgetIdMap[node.parentKey];
        }
      }
      return undefined;
    },
    vKey() {
      return (
        (this.designer.selectedWidget && this.designer.selectedWidget.configuration
          ? this.designer.selectedWidget.id + 'Configuration'
          : 'empty') + this.key
      );
    },

    vPropertyWidgetType() {
      let selectedConfigurable =
        this.designer.selectedId &&
        this.designer.selectedWidget &&
        this.designer.selectedWidget.configuration &&
        this.designer.selectedWidget.configurationDisabled !== true;
      if (selectedConfigurable) {
        let type = this.designer.selectedWidget.wtype + 'Configuration';
        if (this.hasConfigurationComponent(type)) {
          return type;
        }

        // else {
        //   if (typeof this.unFoundConfigureTypeResolver == 'function') {
        //     type = this.unFoundConfigureTypeResolver(this.designer.selectedWidget);
        //     if (this.hasConfigurationComponent(type)) {
        //       return type;
        //     }
        //   }
        // }
      }
      return null;
    }
  },
  created() {},

  methods: {
    hasConfigurationComponent(name) {
      return window.Vue.options.components[name] != undefined;
    },
    designConfigPropertyPopupContainer() {
      return this.$el.querySelector('.component-configure-container');
    },
    validate() {
      return new Promise((resolve, reject) => {
        if (this.$refs.configurationComp && this.$refs.configurationComp.validateConfigure) {
          this.$refs.configurationComp.validateConfigure().then(
            res => {
              resolve(res);
            },
            err => {
              reject(err);
            }
          );
        } else {
          resolve({ vali: true });
        }
      });
    },
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    },
    onClickCollapse() {
      this.collapse = !this.collapse;
      let style = this.$el.parentElement.parentElement.style;
      if (this.collapse) {
        style.flex = '0';
        style.width = '0px';
        style.minWidth = '0px';
        this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
      } else {
        style.flex = this.defaultSiderStyle.flex;
        style.width = this.defaultSiderStyle.width;
        style.minWidth = this.defaultSiderStyle.width;
      }
    },
    onTabClick(key) {
      this.activeKey = key;
    },
    onTabChange(activeKey) {
      if (activeKey === 'compInfo') {
        this.getOffsetBottom();
      }
    },
    onClickConfiguration(e) {
      if (this.tabOffsetBottom && e.y <= this.tabOffsetBottom) {
        // 点击的是tab页签选项，更新配置区滚动条
        this.$refs.scroll.update && this.$refs.scroll.update();
      }
    },

    resolveWidgetType(widget) {
      return this.designer != null ? `E${widget.wtype}` : widget.wtype;
    },
    getOffsetBottom() {
      if (!this.tabOffsetBottom && EASY_ENV_IS_BROWSER) {
        this.$nextTick(() => {
          let tabBars = this.$el.querySelector('.component-configure-container .ant-tabs-top-bar');
          if (tabBars) {
            this.tabOffsetBottom = tabBars.offsetTop + tabBars.clientHeight;
          }
        });
      }
    }
  },
  beforeMount() {},
  mounted() {
    let _this = this;
    this.designer.handleEvent(`WidgetConfigurationPanel:Refresh`, function () {
      _this.key = new Date().getTime();
    });
    this.defaultSiderStyle = {
      flex: this.$el.parentElement.parentElement.style.flex,
      width: this.$el.parentElement.parentElement.style.width
    };
    if (this.layoutFixed) {
      this.onClickCollapse();
    }
  },
  updated() {
    if (this.designer.selectedId && this.$refs.configurationComp) {
      if (this.designer.widgetConfigurations === undefined) {
        this.designer.widgetConfigurations = {};
      }
      // if (this.designer.widgetConfigurations[this.designer.selectedId] === undefined) {
      this.designer.widgetConfigurations[this.designer.selectedId] = this.$refs.configurationComp;
      let isExist = this.designer.isExistWidget(this.designer.selectedId);
      // }
      if (!isExist) {
        delete this.designer.widgetConfigurations[this.designer.selectedId];
        this.designer.selectedId = null;
        this.designer.selectedWidget = null;
      }
    }
  },

  beforeUpdate() {},
  watch: {
    'designer.selectedId': {
      handler(v, o) {
        if (v == null) {
          this.activeKey = 'basicInfo';
        } else if (this.collapse) {
          this.onClickCollapse();
        }
        let cur = this.designer.selectedWidget,
          old = o != undefined ? this.designer.widgetIdMap[o] : undefined;
        if (!((cur && cur.forceRender === false) || (old && old.forceRender === false))) {
          this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
        }
      }
    },
    vPropertyWidgetType: {
      handler() {
        this.getOffsetBottom();
        if (this.designer.selectedId && this.designer.selectedWidget.configurationDisabled !== true) {
          this.activeKey = 'compInfo';
        }
      }
    }
  }
};
</script>
