<template>
  <view class="w-tabs" :class="customClassCom">
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

    <view class="tab-content">
      <template v-for="(tab, i) in visibleTabs">
        <template v-if="loadingTabs.includes(tab.id)">
          <view v-show="current === i">
            <template v-if="tab.configuration.eventHandler.pageId">
              <w-app-page :pageId="tab.configuration.eventHandler.pageId" />
            </template>
            <template v-else>
              <template v-for="(wgt, w) in tab.configuration.widgets">
                <widget :widget="wgt" :parent="widget" :key="'tab_' + wgt.id"></widget>
                <!--<widget :widget="wgt" :parent="widget" :key="'tab_' + wgt.id"></widget> -->
              </template>
            </template>
          </view>
        </template>
      </template>
    </view>
  </view>
</template>

<style lang="scss">
/* #ifdef APP-PLUS */
@import "./index.scss";
/* #endif */
</style>
<script>
// #ifndef APP-PLUS
import "./index.scss";
// #endif
import mixin from "../page-widget-mixin";
import { isEmpty, each as forEach, findIndex, get } from "lodash";
import { utils } from "wellapp-uni-framework";

export default {
  mixins: [mixin],
  name: "w-tabs",
  data() {
    let ruleTabVisible = {},
      emitTabVisible = {};
    this.widget.configuration.tabs.forEach((tab) => {
      ruleTabVisible[tab.id] = undefined;
      emitTabVisible[tab.id] = undefined;
    });
    return {
      activeKey: this.widget.configuration.defaultActiveKey,
      currentKey: undefined, // 当前选中的tab索引,用于和activeKey值进行判断，相同就不进行刷新tab
      current: 0,
      tabs: [],
      visibleTabs: [], // 实际显示的tabs
      controlValues: [],
      badgeNums: [],
      scrollIntoView: "",
      ruleTabVisible,
      emitTabVisible,
      loadingTabs: [],
      vHiddenTabIds: [],
    };
  },
  components: {},
  computed: {
    vHidden() {
      if (this.visibleTabs.length) {
        return true;
      }
      return this.hidden;
    },
    tabIdMap() {
      let map = {};
      for (let i = 0, len = this.tabs.length; i < len; i++) {
        this.tabs[i].title = this.$t(this.tabs[i].id + ".title", this.tabs[i].title);
        map[this.tabs[i].id] = this.tabs[i];
      }
      return map;
    },
    // 相关方法暴露为事件，提供外部调用
    defaultEvents() {
      return [
        { id: "refetchBadge", title: "刷新徽章数量" },
        {
          id: "setTabVisible",
          title: "设置页签是否显示",
          eventParams: [
            {
              paramKey: "id",
              remark: "通过页签ID",
              valueScope: (() => {
                let scope = [];
                for (let i = 0, len = this.widget.configuration.tabs.length; i < len; i++) {
                  scope.push(this.widget.configuration.tabs[i].id);
                }
                return scope;
              })(),
            },
            {
              paramKey: "title",
              remark: "通过页签名称",
              valueScope: (() => {
                let scope = [];
                for (let i = 0, len = this.widget.configuration.tabs.length; i < len; i++) {
                  scope.push(this.widget.configuration.tabs[i].title);
                }
                return scope;
              })(),
            },
            {
              paramKey: "visible",
              remark: "显示或者隐藏",
              valueScope: (() => {
                return ["true", "false"];
              })(),
            },
          ],
        },
      ];
    },
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
  mounted: function () {},
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
        }
        this.setActiveKeyLoadingRender();
      });
    },
    onChangeTab(e) {
      let index = typeof e == "number" ? e : e.index;
      if (this.current != index) {
        this.current = index;
      }
      this.setActiveKeyLoadingRender();
    },
    setActiveKeyLoadingRender() {
      this.activeKey = this.getActiveKey(); // current不改
      if (!this.loadingTabs.includes(this.activeKey)) {
        this.loadingTabs.push(this.activeKey);
      } else if (this.visibleTabs[this.current].configuration.refresh) {
        if (this.activeKey !== this.currentKey) {
          this.onRefreshTab(this.current);
        }
      }
      this.currentKey = this.activeKey;
    },
    // 刷新tab内容,先移除再渲染
    onRefreshTab(i) {
      let key = this.visibleTabs[i].id;
      this.loadingTabs.splice(this.loadingTabs.indexOf(key), 1);
      setTimeout(() => {
        this.loadingTabs.push(key);
      }, 10);
    },
    setVisibleTabs() {
      let tabs = [];
      for (let key in this.tabIdMap) {
        if (!this.vHiddenTabIds.includes(key)) {
          tabs.push(this.tabIdMap[key]);
          if (!this.loadingTabs.includes(key)) {
            let loading = this.tabIdMap[key].configuration.forceRender === false; // 初始时渲染，forceRender：false时不渲染，loading：true时不渲染
            if (!loading) {
              this.loadingTabs.push(key);
            }
          }
        }
      }
      this.visibleTabs = tabs;
    },
    getActiveKey() {
      if (this.visibleTabs.length > this.current) {
        return this.visibleTabs[this.current].id;
      }
      return this.widget.configuration.defaultActiveKey;
    },
    refetchBadge() {
      const _self = this;
      for (let i = 0, len = this.visibleTabs.length; i < len; i++) {
        let tab = this.visibleTabs[i];
        _self.getBadgeCount(tab.configuration, function (item, data) {
          _self.$set(tab, "badge", {
            value: data,
          });
        });
      }
    },

    setTabVisible(tabId, visible = true) {
      let id = tabId,
        title = undefined;
      if (typeof tabId !== "string" && tabId.eventParams != undefined) {
        // 由事件传递进来的参数
        visible = tabId.eventParams.visible !== "false";
        id = tabId.eventParams.id;
        title = tabId.eventParams.title;
      }
      if (id) {
        if (this.tabIdMap[id] != undefined) {
          if (visible) {
            if (this.ruleTabVisible[id] != undefined) {
              this.$delete(this.ruleTabVisible, id);
            }
          }
          this.emitTabVisible[id] = visible;
        }
      } else if (title) {
        for (let i = 0, len = this.widget.configuration.tabs.length; i < len; i++) {
          if (this.widget.configuration.tabs[i].title == title) {
            id = this.widget.configuration.tabs[i].id;
            if (visible) {
              if (this.ruleTabVisible[id] != undefined) {
                this.$delete(this.ruleTabVisible, id);
              }
            }
            this.emitTabVisible[id] = visible;
            break;
          }
        }
      }
      this.reCalculateHiddenTabIds();
    },
    reCalculateHiddenTabIds() {
      let tabs = this.tabs,
        ids = [],
        promises = [];
      for (let i = 0, len = tabs.length; i < len; i++) {
        // 优先按规则
        if (this.ruleTabVisible[tabs[i].id] != undefined) {
          promises.push(Promise.resolve(this.ruleTabVisible[tabs[i].id]));
          continue;
        }
        // 一旦通过事件触发，就不再受条件变化进行显隐控制
        if (this.emitTabVisible[tabs[i].id] != undefined) {
          promises.push(Promise.resolve(this.emitTabVisible[tabs[i].id]));
          continue;
        }

        // 根据表达式判断
        let configuration = tabs[i].configuration;
        if (!this.designMode && configuration.defaultVisible != undefined) {
          promises.push(
            this.calculateVisibleByCondition(configuration.defaultVisibleVar, configuration.defaultVisible)
          );
        } else {
          promises.push(Promise.resolve(true));
        }
      }
      Promise.all(promises).then((results) => {
        for (let i = 0, len = tabs.length; i < len; i++) {
          if (!results[i]) {
            ids.push(tabs[i].id);
          }
        }
        this.vHiddenTabIds.splice(0, this.vHiddenTabIds.length, ...ids);
        this.setVisibleTabs();
      });
    },
    afterChangeableDependDataChanged() {
      this.reCalculateHiddenTabIds();
    },
  },
  watch: {
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
};
</script>
