<template>
  <a-layout-content :id="widget.id" class="widget-layout-content" :style="vContentStyle" :key="layoutContentKey">
    <div v-if="breadcrumb.length > 0" class="breadcrumb-bar" style="padding: 5px; border-radius: 2px; margin-bottom: 10px">
      <a-breadcrumb>
        <a-breadcrumb-item v-for="(item, i) in breadcrumb" :key="'breadcrumb-item-' + i">
          <Icon :type="item.icon" v-if="item.icon" />
          {{ item.title }}
        </a-breadcrumb-item>
      </a-breadcrumb>
    </div>

    <template v-if="widget.configuration.contentAsTabs">
      <a-tabs class="layout-content-tabs" v-model="activeTabKey" type="editable-card" :hideAdd="true" ref="tabs" @edit="onEditTab">
        <template v-for="(pane, i) in tabPanes">
          <a-tab-pane :id="pane.key" v-if="pane.key != -1" :key="pane.key" :closable="pane.closable !== false" :ref="'tabPane_' + pane.key">
            <template slot="tab">
              <a-dropdown :trigger="['contextmenu']">
                <label>{{ pane.title }}</label>
                <a-menu slot="overlay" @click="e => onTabContextMenuClick(e, pane, i)">
                  <a-menu-item key="refresh">{{ $t('WidgetLayout.refresh', '刷新') }}</a-menu-item>
                  <a-menu-item key="closeSelf" v-if="pane.closable !== false">{{ $t('WidgetLayout.close', '关闭') }}</a-menu-item>
                  <a-menu-item key="closeOthers" v-if="hasOtherClosableTabs(i)">
                    {{ $t('WidgetLayout.closeOtherTabs', '关闭其他标签页') }}
                  </a-menu-item>
                  <a-menu-item key="closeAll">{{ $t('WidgetLayout.closeAllTabs', '关闭全部标签页') }}</a-menu-item>
                  <a-menu-item key="closeLeft" v-if="hasLeftClosableTabs(i)">
                    {{ $t('WidgetLayout.closeLeftTabs', '关闭左侧标签页') }}
                  </a-menu-item>
                  <a-menu-item key="closeRight" v-if="hasRightClosableTabs(i)">
                    {{ $t('WidgetLayout.closeRightTabs', '关闭右侧标签页') }}
                  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </template>
            <div>
              <template v-if="pane.url">
                <div v-if="pane.loading" class="spin-loading-center">
                  <a-spin />
                </div>
                <iframe
                  :src="pane.url"
                  :style="{ height: vIframeInTabHeight, border: 'none', width: '100%' }"
                  @load="e => onIframeLoad(e, pane)"
                ></iframe>
              </template>
              <Scroll v-else :style="{ height: vTabHeight }">
                <div v-if="pane.loading" class="spin-loading-center">
                  <a-spin />
                </div>
                <template v-else>
                  <template v-for="(wgt, index) in pane.widgets">
                    <component
                      :key="wgt.id"
                      :is="wgt.wtype"
                      :widget="wgt"
                      :parent="widget"
                      :index="index"
                      :widgetsOfParent="pane.widgets"
                      :designer="designer"
                      v-bind="wgt.props"
                    ></component>
                  </template>
                </template>
              </Scroll>
            </div>
          </a-tab-pane>
        </template>
        <div slot="tabBarExtraContent" v-if="hasTabMoreOperate">
          <a-dropdown>
            <a
              class="ant-dropdown-link"
              @click="e => e.preventDefault()"
              style="padding: 0 var(--w-padding-xs); --w-link-text-color: var(--w-text-color-base)"
            >
              <Icon type="iconfont icon-ptkj-gengduocaidan" style="font-size: 18px" />
            </a>
            <a-menu slot="overlay" @click="e => onTabContextMenuClick(e)">
              <a-menu-item key="closeAll">{{ $t('WidgetLayout.closeAllTabs', '关闭全部标签页') }}</a-menu-item>
              <a-menu-item key="closeOthers" v-if="hasOtherClosableTabs">
                {{ $t('WidgetLayout.closeOtherTabs', '关闭其他标签页') }}
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </div>
      </a-tabs>
    </template>
    <template v-else>
      <template v-if="renderContentUrl">
        <iframe :src="renderContentUrl" :style="{ height: '100%', border: 'none', width: '100%' }" @load="e => onIframeLoad(e)"></iframe>
        <div v-if="loading" class="spin-loading-center">
          <a-spin />
        </div>
      </template>

      <template v-else-if="widget.configuration.autoHeight">
        <div v-if="loading" class="spin-loading-center">
          <a-spin />
        </div>
        <template v-else>
          <template v-for="(wgt, index) in widget.configuration.widgets">
            <component
              :key="wgt.id"
              :is="wgt.wtype"
              :widget="wgt"
              :parent="widget"
              :index="index"
              :widgetsOfParent="widget.configuration.widgets"
              :designer="designer"
              v-bind="wgt.props"
            ></component>
          </template>
        </template>
      </template>
      <template v-else>
        <Scroll :style="{ height: vScrollHeight }" ref="scroll" v-if="renderContentUrl == undefined">
          <div v-if="loading" class="spin-loading-center">
            <a-spin />
          </div>
          <template v-if="refreshKey != -1 && !loading">
            <template v-if="widget.configuration.widgets != undefined">
              <template v-for="(wgt, index) in widget.configuration.widgets">
                <component
                  :key="wgt.id"
                  :is="wgt.wtype"
                  :widget="wgt"
                  :parent="widget"
                  :index="index"
                  :widgetsOfParent="widget.configuration.widgets"
                  :designer="designer"
                  v-bind="wgt.props"
                ></component>
              </template>
            </template>
            <a-drawer
              :closable="false"
              wrapClassName="layout-content-drawer"
              :wrap-style="{ position: 'absolute' }"
              :body-style="{ padding: '0px' }"
              :visible="drawerVisible"
              :get-container="false"
              :mask="false"
              :destroyOnClose="true"
              :width="drawerWidth"
              :afterVisibleChange="afterDrawerVisibleChange"
              ref="drawerWidgets"
              @close="onCloseDrawer"
            >
              <Scroll :style="{ height: vScrollHeight }">
                <template v-for="(wgt, index) in drawerWidgets">
                  <component :key="wgt.id" :is="wgt.wtype" :widget="wgt" :parent="widget" :index="index" v-bind="wgt.props"></component>
                </template>
              </Scroll>
            </a-drawer>
          </template>
        </Scroll>
      </template>
    </template>
    <a-tooltip :title="$t('WidgetLayout.close', '关闭')" placement="left" v-if="drawerVisible">
      <div class="drawer-close" style="" @click="onCloseDrawer">
        <a-icon type="double-right" />
      </div>
    </a-tooltip>
  </a-layout-content>
</template>
<style lang="less">
.widget-layout-content {
  .drawer-close {
    position: absolute;
    top: 50%;
    right: 5px;
    background: #c6c6c6;
    color: #fff;
    border-radius: 0 4px 4px 0px;
    cursor: pointer;
  }
  .layout-content-drawer {
    > .ant-drawer-content-wrapper {
      .ant-drawer-close {
        width: 45px;
        height: 40px;
        line-height: 40px;
      }
    }
  }
}
</style>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { generateId } from '@framework/vue/utils/util';
export default {
  name: 'WidgetLayoutContent',
  mixins: [widgetMixin],
  data() {
    return {
      height: 600,
      scrollHeight: 600,
      tabHeight: 500,
      iframeInTabHeight: 495,
      renderContentUrl: undefined,
      layoutContentKey: this.widget.id,
      loading: false,
      activeTabKey: undefined,
      containerStyle: {
        height: 600
      },
      tabPanes: [],
      breadcrumb: [],
      lastUpdateHeighOrigin: undefined,
      breadcrumbComputed: false,
      currentContentKey: undefined,
      drawerWidgets: [],
      drawerVisible: false,
      drawerWidth: '0px',
      refreshKey: this.widget.id + new Date().getTime()
    };
  },
  provide() {
    return {
      containerStyle: this.containerStyle,
      parentLayContentId: this.widget.id

      // $containerHeight: this.height
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    vContentStyle() {
      let style = {
        'overflow-y': this.renderContentUrl ? 'hidden' : 'auto',
        height: this.widget.configuration.autoHeight ? 'auto' : this.vHeight
      };
      if (this.widget.configuration.autoHeight) {
        style.minHeight = this.vHeight;
      }
      if (this.widget.configuration.contentAsTabs) {
        style.margin = '0';
      }
      return style;
    },
    vTabHeight() {
      return typeof this.tabHeight == 'number' ? this.tabHeight + 'px' : this.tabHeight;
    },
    vIframeInTabHeight() {
      return typeof this.iframeInTabHeight == 'number' ? this.iframeInTabHeight + 'px' : this.iframeInTabHeight;
    },

    vHeight() {
      return typeof this.height == 'number' ? this.height + 'px' : this.height;
    },
    vScrollHeight() {
      return typeof this.scrollHeight == 'number' ? this.scrollHeight + 'px' : this.scrollHeight;
    },
    vTabKeys() {
      let keys = [];
      for (let k of this.tabPanes) {
        if (k.key != undefined) {
          keys.push(k.key);
        }
      }
      return keys;
    },
    hasTabMoreOperate() {
      if (this.tabPanes.length > 1) {
        for (let i = 0, len = this.tabPanes.length; i < len; i++) {
          if (this.tabPanes[i].closable !== false) {
            return true;
          }
        }
      }
      return false;
    }
  },
  created() {},
  methods: {
    onChangeLayoutTab() {
      this.pageContext.emitEvent(`tab:${this.$refs.tabs.$children[0]._uid}:changeTab`, this.activeTabKey);
    },
    afterDrawerVisibleChange(visible) {
      if (!visible) {
        this.drawerWidth = '0px';
      }
    },
    open(url, target, key) {
      if (this.widget.configuration.contentAsTabs) {
        if (typeof url == 'string') {
          // this.updateData2LayoutContent({
          //   url,
          //   title: '测试',
          //   key: generateId()
          // });
        }
      }
    },
    close() {
      if (this.drawerVisible) {
        this.drawerVisible = false;
      }
      if (this.widget.configuration.contentAsTabs) {
        this.closeActiveTab(true);
      }
    },
    onCloseDrawer() {
      this.drawerVisible = false;
    },
    closeActiveTab(force = false) {
      if (this.activeTabKey) {
        for (let i = 0, len = this.tabPanes.length; i < len; i++) {
          let pane = this.tabPanes[i];
          if (pane.key == this.activeTabKey) {
            if (force || pane.closable !== false) {
              this.tabPanes.splice(i, 1);
              if (i > 0) {
                this.activeTabKey = this.tabPanes[i - 1].key;
              }
            }
            break;
          }
        }
      }
    },
    closeTabs(index, step, excludeKey) {
      if (step == 0) {
        this.tabPanes.splice(index, 1);
      } else if (step == 1) {
        // 关闭右侧
        for (let i = index + 1; i < this.tabPanes.length; i++) {
          if (this.tabPanes[i].closable !== false) {
            this.tabPanes.splice(i--, 1);
          }
        }
      } else if (step == -1) {
        // 关闭左侧
        let _index = index;
        for (let i = 0; i < _index; i++) {
          if (this.tabPanes[i].closable !== false) {
            this.tabPanes.splice(i--, 1);
            _index--;
          }
        }
      } else if (step == 'all') {
        // 关闭所有
        for (let i = 0; i < this.tabPanes.length; i++) {
          if (this.tabPanes[i].closable !== false) {
            this.tabPanes.splice(i--, 1);
          }
        }
      } else if (excludeKey != undefined) {
        for (let i = 0; i < this.tabPanes.length; i++) {
          if (this.tabPanes[i].closable !== false && this.tabPanes[i].key != excludeKey) {
            this.tabPanes.splice(i--, 1);
          }
        }
      }
    },
    hasOtherClosableTabs(index) {
      if (index == undefined) {
        index == this.tabPanes.findIndex(item => item.key == this.activeTabKey);
      }
      if (this.tabPanes.length > 1) {
        for (let i = 0, len = this.tabPanes.length; i < len; i++) {
          if (i != index && this.tabPanes[i].closable !== false) {
            return true;
          }
        }
      }
      return false;
    },
    hasLeftClosableTabs(index) {
      if (index > 0) {
        for (let i = 0, len = this.tabPanes.length; i < len; i++) {
          if (i < index && this.tabPanes[i].closable !== false) {
            return true;
          }
        }
      }
      return false;
    },
    hasRightClosableTabs(index) {
      if (index << (this.tabPanes.length - 1)) {
        for (let i = 0, len = this.tabPanes.length; i < len; i++) {
          if (i > index && this.tabPanes[i].closable !== false) {
            return true;
          }
        }
      }
      return false;
    },
    onTabContextMenuClick({ key }, pane, index) {
      if (key == 'refresh') {
        let _key = pane.key;
        pane.key = -1;
        this.$nextTick(() => {
          pane.key = _key;
        });
      } else {
        let activeTabExist = () => {
          for (let i = 0, len = this.tabPanes.length; i < len; i++) {
            if (this.tabPanes[i].key == this.activeTabKey) {
              return true;
            }
          }
          return false;
        };
        if (key == 'closeSelf') {
          this.closeTabs(index, 0);
        } else if (key == 'closeLeft') {
          this.closeTabs(index, -1);
        } else if (key == 'closeRight') {
          this.closeTabs(index, 1);
        } else if (key == 'closeOthers') {
          this.closeTabs(undefined, undefined, pane ? pane.key : this.activeTabKey);
        } else if (key == 'closeAll') {
          this.closeTabs(undefined, 'all');
          this.activeTabKey = this.tabPanes[0].key;
        }
        this.$nextTick(() => {
          if (!activeTabExist()) {
            this.activeTabKey = undefined;
            if (key !== 'closeSelf') {
              this.activeTabKey = pane.key;
            } else {
              if (this.tabPanes[index] != undefined) {
                this.activeTabKey = this.tabPanes[index].key;
              } else if (this.tabPanes[index - 1] != undefined) {
                this.activeTabKey = this.tabPanes[index - 1].key;
              }
            }
          }
        });
      }
    },
    onIframeLoad(e, pane) {
      if (pane == undefined) {
        this.loading = false;
      } else {
        pane.loading = false;
      }
    },
    onEditTab(key, action) {
      if (action == 'remove') {
        let index = undefined;
        for (let i = 0, len = this.tabPanes.length; i < len; i++) {
          if (this.tabPanes[i].key == key) {
            index = i;
            break;
          }
        }
        if (index != undefined) {
          let closeActiveTab = this.tabPanes[index].key == this.activeTabKey;
          this.tabPanes.splice(index, 1);
          if (closeActiveTab) {
            this.activeTabKey = undefined;
            if (this.tabPanes[index] != undefined) {
              this.activeTabKey = this.tabPanes[index].key;
            } else if (this.tabPanes[index - 1] != undefined) {
              this.activeTabKey = this.tabPanes[index - 1].key;
            }
          }
        }
      }
    },
    updateHeight(height) {
      this.lastUpdateHeighOrigin = height;
      let _style = window.getComputedStyle(this.$el);
      let marginHeight = parseInt(_style.marginBottom) + parseInt(_style.marginTop);
      let tabMarginHeight = 0;
      if (this.widget.configuration.contentAsTabs) {
        let tabBarStyle = window.getComputedStyle(this.$refs.tabs.$el.querySelector('.ant-tabs-bar'));
        tabMarginHeight = parseInt(tabBarStyle.height) + parseInt(tabBarStyle.marginBottom) + parseInt(tabBarStyle.marginTop);
        let tabContentStyle = window.getComputedStyle(this.$refs.tabs.$el.querySelector('.ant-tabs-content'));
        tabMarginHeight += parseInt(tabContentStyle.paddingTop) + parseInt(tabContentStyle.paddingBottom);
      }

      let breadcrumbBarHeight = 0;
      let breadcrumbBar = this.$el.querySelector('.breadcrumb-bar');
      if (breadcrumbBar) {
        let breadcrumbBarStyle = window.getComputedStyle(breadcrumbBar);
        breadcrumbBarHeight =
          parseInt(breadcrumbBarStyle.height) + parseInt(breadcrumbBarStyle.marginBottom) + parseInt(breadcrumbBarStyle.marginTop);
      }
      if (typeof height == 'number') {
        this.height = height - marginHeight;
        this.tabHeight = this.height - tabMarginHeight;
        this.iframeInTabHeight = this.tabHeight - 5;
        this.scrollHeight = this.height - breadcrumbBarHeight;
      } else if (height.indexOf('calc(') == 0) {
        let _temp = height.substring(height.indexOf('calc(') + 5, height.length - 1).trim();
        this.height = `calc(${_temp} - ${marginHeight}px)`;
        this.tabHeight = `calc(${_temp} - ${marginHeight}px - ${tabMarginHeight}px)`;
        this.iframeInTabHeight = `calc(${_temp} - ${marginHeight}px - ${tabMarginHeight}px - 5px)`;
        this.scrollHeight = `calc(${_temp} - ${marginHeight}px - ${breadcrumbBarHeight}px)`;
      } else {
        this.height = parseInt(height) - marginHeight;
        this.tabHeight = parseInt(height) - marginHeight - tabMarginHeight;
        this.iframeInTabHeight = this.tabHeight - 5;
        this.scrollHeight = parseInt(height) - marginHeight - breadcrumbBarHeight;
      }
      // 修改容器的高度，子组件需要调整高度
      this.containerStyle.height = this.scrollHeight;
    },
    restoreLayoutContent({ configuration }) {
      this.widget.configuration = configuration;
      this.renderContentUrl = undefined;
      if (this.widget.configuration.contentAsTabs) {
        this.initDefaultTabContent();
      } else {
        this.layoutContentKey = `${this.widget.id}_` + new Date().getTime();
      }
    },
    initDefaultTabContent() {
      if (this.widget.configuration.contentAsTabs && this.widget.configuration.widgets.length) {
        let key = `${this.widget.id}_tab_${0}`;
        this.activeTabKey = key;
        if (this.tabPanes.length == 0 || this.tabPanes[0].key != this.activeTabKey) {
          // 默认页签不存在，重新创建
          this.tabPanes.splice(0, 0, {
            key,
            title: this.widget.configuration.defaultTabTitle || this.$t('WidgetLayout.home', '首页'),
            closable: this.widget.configuration.defaultTabClosable === true,
            widgets: this.widget.configuration.widgets
          });
        }
      }
    },

    updateData2LayoutContent(data) {
      let _this = this;
      _this.loading = false;
      if (_this.widget.configuration.contentAsTabs) {
        let key = data.key,
          update = key != undefined && _this.vTabKeys.includes(key);
        if (key == undefined && (data.widgets == undefined || (Array.isArray(data.widgets) && data.widgets.length == 0))) {
          return;
        }
        if (key == undefined) {
          key = generateId();
        }
        _this.activeTabKey = key;

        let setTabPaneContent = pane => {
          if (data.url) {
            pane.loading = true;
            pane.url = data.url;
          } else {
            if (data.widgets instanceof Promise) {
              pane.loading = true;
              pane.widgets = [];
              data.widgets.then(d => {
                if (!pane.title) {
                  if (d.title != undefined) {
                    pane.title = d.title;
                  } else if (d.page != undefined) {
                    pane.title = d.page.title || d.page.name;
                  }
                }
                if (data.locateNavigation) {
                  // 加载模块主页的导航，并定位
                  if (d.page != undefined) {
                    pane.pageId = d.page.id;
                    pane.appId = d.page.appId;
                    _this.pageContext.emitEvent(`widgetLayout:JumpToModuleNavigation:${_this.parent.id}`, {
                      pageId: d.page.id,
                      appId: d.page.appId
                    });
                  }
                }
                pane.widgets.push(...(Array.isArray(d.widgets) ? d.widgets : [d.widgets]));
                pane.loading = false;
              });
            } else {
              pane.widgets = Array.isArray(data.widgets) ? data.widgets : [data.widgets];
            }
          }
        };
        if (!update) {
          let pane = {
            title: data.title,
            key: data.key
          };

          setTabPaneContent.call(_this, pane);
          _this.tabPanes.push(pane);
          _this.$nextTick(() => {
            _this.$refs['tabPane_' + pane.key][0]._provided.$event = data.$event; // 提供事件
          });
        } else {
          if (data.locateNavigation) {
            for (let i = 0, len = _this.tabPanes.length; i < len; i++) {
              if (_this.tabPanes[i].key == key) {
                if (_this.tabPanes[i].pageId != undefined && _this.tabPanes[i].appId != undefined) {
                  _this.pageContext.emitEvent(`widgetLayout:JumpToModuleNavigation:${_this.parent.id}`, {
                    pageId: _this.tabPanes[i].pageId,
                    appId: _this.tabPanes[i].appId
                  });
                }
                break;
              }
            }
          }
          let index = _this.vTabKeys.indexOf(data.key);
          if (data.forceUpdate === true) {
            if (index != -1) {
              let _temp = _this.tabPanes[index].key;
              _this.tabPanes[index].key = -1;
              _this.$nextTick(() => {
                _this.tabPanes[index].key = _temp;
              });
              _this.tabPanes[index].title = data.title;
              setTabPaneContent.call(_this, _this.tabPanes[index]);
            }
          }

          // TODO: 是否要刷新?
          // for (let tab of _this.tabPanes) {
          //   if (tab.key == key) {
          //     if (data.url) {
          //       delete tab.widgets;
          //       tab.url = data.url;
          //     } else {
          //       delete tab.url;
          //       tab.widgets = Array.isArray(data.widgets) ? data.widgets : [data.widgets];
          //     }
          //     break;
          //   }
          // }
        }
      } else {
        _this.drawerVisible = false;
        if (data.url) {
          // 地址 url
          if (_this.renderContentUrl != data.url) {
            _this.loading = true;
            _this.renderContentUrl = data.url;
          }
        } else {
          _this.renderContentUrl = undefined;

          if (data.widgets instanceof Promise) {
            _this.loading = true;
            if (data.key == undefined) {
              data.key = generateId();
            }
            _this.currentContentKey = data.key; // 增加当前加载内容key , 避免加载延时导致的页面加载错乱
            data.widgets.then(d => {
              let w = Array.isArray(d.widgets) ? d.widgets : [d.widgets];
              if (data.layoutNoTabsOpenType === 'drawer') {
                _this.drawerWidgets.splice(0, _this.drawerWidgets.length);
                _this.drawerWidgets.push(...w);
                let _style = window.getComputedStyle(_this.$el);
                _this.drawerWidth = _style.width;
                _this.drawerVisible = true;
                _this.$refs.drawerWidgets._provided.$event = data.$event;
                _this.loading = false;
                return;
              }
              if (data.key == _this.currentContentKey) {
                _this.refreshKey = -1;
                _this.$nextTick(() => {
                  _this.loading = false;
                  _this.refreshKey = generateId();
                  _this.widget.configuration.widgets = w;
                  _this.$refs.scroll._provided.$event = data.$event;
                });
              }
            });
          } else {
            let w = Array.isArray(data.widgets) ? data.widgets : [data.widgets];
            if (data.layoutNoTabsOpenType === 'drawer') {
              _this.drawerWidgets.splice(0, _this.drawerWidgets.length);
              _this.drawerWidgets.push(...w);
              let _style = window.getComputedStyle(_this.$el);
              _this.drawerWidth = _style.width;
              _this.drawerVisible = true;
              _this.$refs.drawerWidgets._provided.$event = data.$event;
              _this.loading = false;
              return;
            }
            _this.refreshKey = -1;
            _this.$nextTick(() => {
              _this.refreshKey = generateId();
              _this.loading = false;
              _this.widget.configuration.widgets = w;
              _this.$refs.scroll._provided.$event = data.$event;
            });
          }
        }
      }
    }
  },
  beforeMount() {
    let _this = this;
    // if (this.widget.configuration.enableBreadcrumb) {
    this.pageContext.handleEvent(`widgetLayoutContent:UpdateBreadcrumb:${this.widget.id}`, data => {
      let { index, items } = data;
      _this.breadcrumb.splice(index, _this.breadcrumb.length);
      for (let i = 0, len = items.length; i < len; i++) {
        _this.breadcrumb.push(items[i]);
      }
      if (!_this.breadcrumbComputed) {
        _this.$nextTick(() => {
          _this.updateHeight(_this.lastUpdateHeighOrigin);
          _this.breadcrumbComputed = true;
        });
      }
    });
    // }
  },
  mounted() {
    let _this = this;
    this.pageContext.handleEvent(`WidgetLayoutContent:Loading:${this.widget.id}`, function (data) {
      _this.loading = true;
    });
    this.pageContext.handleEvent(`WidgetLayoutContent:Update:${this.widget.id}`, function (data) {
      _this.updateData2LayoutContent(data);
    });
    this.initDefaultTabContent();
    if (this.$refs.tabs != undefined) {
      this.$refs.tabs.$el.querySelector('.ant-tabs-bar').addEventListener('contextmenu', e => {
        e.stopPropagation();
        e.preventDefault();
        try {
          e.target.querySelector('label').__vue__._provided.vcTriggerContext.onContextmenu(e);
        } catch (error) {}
      });
    }
  },
  watch: {
    activeTabKey: {
      handler() {
        this.onChangeLayoutTab();
      }
    }
  }
};
</script>
