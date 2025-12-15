<template>
  <view>
    <uni-w-tabs
      :list="visibleTabs"
      @change="onChangeTab"
      keyName="title"
      name="title"
      :current="current"
      equalWidth
      :isScroll="visibleTabs.length > 4"
      itemMaxWidth="120"
      v-if="widget.configuration.uniConfiguration.tabStyleType == 'default'"
    ></uni-w-tabs>
    <uni-w-subsection
      :bold="false"
      :list="visibleTabs"
      name="title"
      :current="current"
      :mode="widget.configuration.uniConfiguration.subsectionMode"
      :bgColor="
        widget.configuration.uniConfiguration.subsectionMode == 'button' ? 'var(--w-bg-color-mobile-bg)' : '#ffffff'
      "
      :height="widget.configuration.uniConfiguration.subsectionMode == 'button' ? '32' : '40'"
      @change="onChangeTab"
      v-if="widget.configuration.uniConfiguration.tabStyleType == 'subsection'"
    ></uni-w-subsection>
  </view>
</template>

<script>
import pageMixin from "../page-widget-mixin";
import { findIndex, get } from "lodash";

export default {
  name: "tabs-nav",
  mixins: [pageMixin],
  data() {
    let ruleTabVisible = {},
      emitTabVisible = {};
    this.widget.configuration.tabs.forEach((tab) => {
      ruleTabVisible[tab.id] = undefined;
      emitTabVisible[tab.id] = undefined;
    });
    return {
      activeKey: this.widget.configuration.defaultActiveKey,
      current: 0,
      tabs: [],
      visibleTabs: [], // 实际显示的tabs
      controlValues: [],
      badgeNums: [],
      scrollIntoView: "",
      ruleTabVisible,
      emitTabVisible,
    };
  },
  computed: {
    vHidden() {
      if (this.visibleTabs.length) {
        return true;
      }
      return this.hidden;
    },
    vHiddenTabIds() {
      let tabs = this.tabs,
        ids = [];
      for (let i = 0, len = tabs.length; i < len; i++) {
        // 优先按规则
        if (this.ruleTabVisible[tabs[i].id] != undefined) {
          if (!this.ruleTabVisible[tabs[i].id]) {
            ids.push(tabs[i].id);
          }
          continue;
        }
        // 一旦通过事件触发，就不再受条件变化进行显隐控制
        if (this.emitTabVisible[tabs[i].id] != undefined) {
          if (!this.emitTabVisible[tabs[i].id]) {
            ids.push(tabs[i].id);
          }
          continue;
        }

        // 根据表达式判断
        let configuration = tabs[i].configuration,
          visible = true;
        if (!this.designMode && configuration.defaultVisible != undefined) {
          visible = configuration.defaultVisible;
          if (
            // 根据条件判断显隐
            configuration.defaultVisibleVar &&
            configuration.defaultVisibleVar.enable &&
            configuration.defaultVisibleVar.conditions != undefined &&
            configuration.defaultVisibleVar.conditions.length > 0
          ) {
            // 多组条件判断
            let match = configuration.defaultVisibleVar.match == "all";
            let _showByData = {};
            if (this._vShowByData) {
              _showByData = this._vShowByData;
            } else if (this.dyform != undefined) {
              _showByData = this.dyform.formData;
            }
            let _compareDataSource = this.widgetDependentVariableDataSource();
            let _compareData = {
              ...this.vPageState,
              ..._compareDataSource,
              ...(_showByData || {}),
            };
            for (let i = 0, len = configuration.defaultVisibleVar.conditions.length; i < len; i++) {
              let { code, operator, value, valueType } = configuration.defaultVisibleVar.conditions[i];
              if (valueType == "variable") {
                try {
                  value = get(_compareData, value);
                } catch (error) {
                  console.error("无法解析变量值", value);
                }
              }
              let result = utils.expressionCompare(_compareData, code, operator, value);
              if (configuration.defaultVisibleVar.match == "all" && !result) {
                match = false;
                break;
              }
              if (configuration.defaultVisibleVar.match == "any" && result) {
                match = true;
                break;
              }
            }
            visible = match ? visible : !visible;
          }
          if (!visible) {
            ids.push(tabs[i].id);
          }
        }
      }
      return ids;
    },
    tabIdMap() {
      let map = {};
      for (let i = 0, len = this.tabs.length; i < len; i++) {
        this.tabs[i].title = this.$t(this.tabs[i].id + ".title", this.tabs[i].title);
        map[this.tabs[i].id] = this.tabs[i];
      }
      return map;
    },
  },
  watch: {
    vHiddenTabIds: {
      deep: true,
      handler(v, o) {
        if (v !== o) {
          this.setVisibleTabs();
        }
      },
    },
    visibleTabs: {
      deep: true,
      handler(v, o) {
        if (v !== o) {
          this.setCurrentTab();
        }
      },
    },
    // current(v) {
    //   console.log(v);
    // },
  },
  created() {
    if (this.widget.configuration.uniConfiguration == undefined) {
      this.widget.configuration.uniConfiguration = {
        tabStyleType: "default",
      };
    }
    const _self = this;
    let configuration = this.widget.configuration;
    let tabs = configuration.tabs || [];
    for (let i = 0, len = tabs.length; i < len; i++) {
      let tab = tabs[i];
      _self.getBadgeCount(tab.configuration, function (item, data) {
        _self.$set(tab, "badge", {
          value: data,
        });
      });
    }
    _self.tabs = tabs;
  },
  methods: {
    setCurrentTab() {
      if (this.dyform && this.dyform.formElementRules) {
        this.tabs.forEach((item) => {
          if (this.dyform.formElementRules[item.id] && this.dyform.formElementRules[item.id].hidden != undefined) {
            this.ruleTabVisible[item.id] = !this.dyform.formElementRules[item.id].hidden;
            if (item.id == this.activeKey) {
              this.activeKey = undefined;
            }
          }
        });
      }

      if (this.tabs.length && this.unauthorizedResource) {
        let noAuthorized = false;
        for (let i = 0; i < this.tabs.length; i++) {
          if (this.unauthorizedResource && this.unauthorizedResource.includes(this.tabs[i].id)) {
            console.warn("tab页签 %s 无权限", this.tabs[i].title);
            if (this.activeKey == this.tabs[i].id) {
              this.activeKey = undefined;
            }
            this.tabs.splice(i--, 1);
            noAuthorized = true;
          }
        }
        if (noAuthorized) {
          if (this.tabs.length == 0) {
            this.widgetsOfParent.splice(this.index, 1);
            return;
          }
        }
      }

      if (
        this.activeKey == undefined ||
        this.vHiddenTabIds.includes(this.activeKey) ||
        !Object.keys(this.tabIdMap).includes(this.activeKey)
      ) {
        // 无默认激活的tab项，则取第一个
        for (let i = 0, len = this.tabs.length; i < len; i++) {
          if (!this.vHiddenTabIds.includes(this.tabs[i].id)) {
            this.activeKey = this.tabs[i].id;
            break;
          }
        }
      }
      this.$nextTick(() => {
        let current = findIndex(this.visibleTabs, (item, index) => {
          return item.id == this.activeKey;
        });
        if (this.current != current) {
          this.current = current;
          this.$emit("change", this.current);
        }
      });
    },
    onChangeTab(e) {
      let index = typeof e == "number" ? e : e.index;
      if (this.current != index) {
        this.current = index;
      }
      this.$emit("change", this.current);
    },
    setVisibleTabs() {
      this.activeKey = this.getActiveKey();
      let tabs = [];
      for (let key in this.tabIdMap) {
        if (!this.vHiddenTabIds.includes(key)) {
          tabs.push(this.tabIdMap[key]);
        }
      }
      this.visibleTabs = tabs;
      this.$emit("tabs", tabs);
    },
    getActiveKey() {
      if (this.visibleTabs.length > this.current) {
        return this.visibleTabs[this.current].id;
      }
      return this.widget.configuration.defaultActiveKey;
    },
  },
};
</script>
