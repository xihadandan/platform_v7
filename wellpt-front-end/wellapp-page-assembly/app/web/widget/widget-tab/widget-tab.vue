<template>
  <div
    class="widget-tabs-container"
    :class="[
      'tab_' + widget.configuration.tabPosition,
      !!isIframeTab ? 'current_iframe_tab' : '',
      widget.configuration.bordered ? 'widget-tabs-bordered' : ''
    ]"
  >
    <a-tabs
      :class="['widget-tab', widget.configuration.asWindow ? 'window' : '']"
      :tab-position="widget.configuration.tabPosition"
      :animated="false"
      :size="widget.configuration.size"
      hide-add
      @tabClick="onTabClick"
      @change="onChange"
      :type="widget.configuration.tabStyleType"
      :activeKey="activeKey"
      @edit="onEdit"
      v-show="!vHidden"
      ref="tabs"
    >
      <template v-for="(t, i) in tabs">
        <template v-if="!vHiddenTabs.includes(t.id)">
          <a-tab-pane :id="t.id" :key="t.id" :closable="t.configuration.closable" :forceRender="t.configuration.forceRender !== false">
            <template slot="tab">
              <template v-if="widget.configuration.tabStyleType === 'editable-card'">
                <a-dropdown :trigger="['contextmenu']">
                  <div class="flex">
                    <div class="widget-tab-title f_g_1" :title="$t(t.id + '.title', t.title)">
                      <Icon :type="t.configuration.icon" v-if="t.configuration.icon" :size="iconSize" />
                      {{ $t(t.id + '.title', t.title) }}
                    </div>
                    <div class="f_s_0 widget-tab-badge" v-if="t.configuration.badge && t.configuration.badge.enable">
                      <a-badge :count="t.configuration.badge.count" :showZero="true" />
                    </div>
                  </div>
                  <a-menu slot="overlay">
                    <a-menu-item v-show="tabs.length != 1" @click="onCloseTabs(i, 'left-right')">
                      {{ $t('WidgetLayout.closeOtherTabs', '关闭其他标签页') }}
                    </a-menu-item>
                    <a-menu-item v-show="i > 0" @click="onCloseTabs(i, 'left')">
                      {{ $t('WidgetLayout.closeLeftTabs', '关闭左侧标签页') }}
                    </a-menu-item>
                    <a-menu-item v-show="i < tabs.length - 1" @click="onCloseTabs(i, 'right')">
                      {{ $t('WidgetLayout.closeRightTabs', '关闭右侧标签页') }}
                    </a-menu-item>
                    <a-menu-item @click="onRefreshTab(i)">{{ $t('WidgetLayout.refresh', '刷新') }}</a-menu-item>
                  </a-menu>
                </a-dropdown>
              </template>
              <template v-else>
                <div class="flex">
                  <div class="widget-tab-title f_g_1" :title="t.title">
                    <Icon :type="t.configuration.icon" v-if="t.configuration.icon" :size="iconSize" />
                    {{ $t(t.id + '.title', t.title) }}
                  </div>
                  <div class="f_s_0 widget-tab-badge" v-if="t.configuration.badge && t.configuration.badge.enable">
                    <a-badge :count="t.configuration.badge.count" :showZero="true" />
                  </div>
                </div>
              </template>
            </template>
            <!-- <PerfectScrollbar :style="{ height: tabHeight }"> -->
            <div :id="t.id" v-if="!t.loading && !hasIframe(t)" :key="t.key" class="tab-pane-container">
              <!-- :style="{ padding: '5px', height: tabContentHeight, overflowY: 'auto' }" -->
              <PerfectScrollbar :style="{ height: tabContentScrollHeight }">
                <template
                  v-if="
                    t.configuration &&
                    t.configuration.eventHandler &&
                    t.configuration.eventHandler.pageType == 'page' &&
                    t.configuration.eventHandler.pageId != undefined
                  "
                >
                  <WidgetVpage
                    v-if="t.configuration.eventHandler.pageId != undefined"
                    :height="tabContentHeight"
                    :pageId="t.configuration.eventHandler.pageId"
                    :widget="t.configuration.eventHandler.pageWidget"
                    :urlHashParams="t.configuration.eventHandler.urlHashParams"
                    :_event="t.configuration.$event"
                    :parent="widget"
                  />
                </template>
                <template v-else-if="t.configuration.widgets && t.configuration.widgets.length">
                  <template v-for="(wgt, index) in t.configuration.widgets">
                    <component
                      :key="wgt.id"
                      :is="resolveWidgetType(wgt)"
                      :widget="wgt"
                      :parent="widget"
                      :index="index"
                      :widgetsOfParent="t.configuration.widgets"
                      :designer="designer"
                      v-bind="wgt.props"
                    ></component>
                  </template>
                </template>
              </PerfectScrollbar>
            </div>
            <!-- </PerfectScrollbar> -->
          </a-tab-pane>
        </template>
      </template>
      <template slot="tabBarExtraContent" v-if="currentTab != undefined">
        <div style="padding: 0px 8px">
          <keyword-search :widget="currentTab" :configuration="currentTab.configuration" />
          <template v-if="currentTab.configuration && currentTab.configuration.tabButton && currentTab.configuration.tabButton.enable">
            <WidgetTableButtons
              :key="'currentTab-' + activeKey"
              :button="currentTab.configuration.tabButton"
              :developJsInstance="developJsInstance"
            />
          </template>
        </div>
      </template>
    </a-tabs>
    <!-- iframe页，因切换tab会导致iframe页面重新渲染，将iframe放在tabpane外面，用v-show来判断显隐-->
    <template v-for="(t, i) in tabs">
      <template v-if="!vHiddenTabs.includes(t.id)">
        <div
          :id="t.id"
          v-if="!t.loading && hasIframe(t) && t.configuration.forceRender !== false"
          :key="t.key"
          class="tab-pane-container"
          v-show="activeKey == t.id"
          :style="{ visibility: iframeLoading ? 'hidden' : 'unset' }"
        >
          <iframe
            class="tab-pane-iframe"
            :id="t.id + '_iframe'"
            :src="hasIframe(t)"
            :style="{ minHeight: tabContentHeight, border: 'none', width: '100%' }"
          ></iframe>
        </div>
      </template>
    </template>
  </div>
</template>
<style>
.widget-tabs.ant-tabs.ant-tabs-card .ant-tabs-card-bar .ant-tabs-tab-active::before {
  border-top: 2px solid var(--w-primary-color);
}
</style>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
import DataSourceBase from '../../assets/js/commons/dataSource.base';
import { expressionCompare, getElSpacingForTarget, getBottomStylesSum, getTopStylesSum } from '@framework/vue/utils/util';
import KeywordSearch from '../widget-card/components/keyword-search.vue';
import './css/index.less';
export default {
  name: 'WidgetTab',
  mixins: [widgetMixin],
  inject: ['containerStyle', 'dyform', 'unauthorizedResource'],
  props: {
    height: Number
  },
  provide() {
    return {};
  },
  components: {
    KeywordSearch
  },
  data() {
    let ruleTabVisible = {},
      emitTabVisible = {};
    this.widget.configuration.tabs.forEach(tab => {
      ruleTabVisible[tab.id] = undefined;
      emitTabVisible[tab.id] = undefined;
      if (
        tab.configuration.enableKeywordSearch &&
        tab.configuration.keywordSearchWidgetIds &&
        tab.configuration.keywordSearchWidgetIds.length
      ) {
        tab.configuration.keywordSearchWidgetIds.map(id => {
          this._provided[`${id}:visibleKeywordSearch`] = true;
        });
      }
    });
    return {
      activeKey: this.widget.configuration.defaultActiveKey,
      tabs: this.widget.configuration.tabs || [],
      tabHeight: 'auto',
      tabContentHeight: 'auto',
      tabContentScrollHeight: 'auto',
      hidden: false,
      ruleTabVisible,
      emitTabVisible,
      iframeLoading: false,
      vHiddenTabs: []
    };
  },
  computed: {
    vHidden() {
      if (this.vHiddenTabs.length == this.tabs.length) {
        return true;
      }
      return this.hidden;
    },

    currentTab() {
      return this.tabIdMap[this.activeKey];
    },
    // 相关方法暴露为事件，提供外部调用
    defaultEvents() {
      return [
        { id: 'refetchBadge', title: '刷新徽章数量' },
        {
          id: 'setTabVisible',
          title: '设置页签是否显示',
          eventParams: [
            {
              paramKey: 'id',
              remark: '通过页签ID',
              valueScope: (() => {
                let scope = [];
                for (let i = 0, len = this.widget.configuration.tabs.length; i < len; i++) {
                  scope.push(this.widget.configuration.tabs[i].id);
                }
                return scope;
              })()
            },
            {
              paramKey: 'title',
              remark: '通过页签名称',
              valueScope: (() => {
                let scope = [];
                for (let i = 0, len = this.widget.configuration.tabs.length; i < len; i++) {
                  scope.push(this.widget.configuration.tabs[i].title);
                }
                return scope;
              })()
            },
            {
              paramKey: 'visible',
              remark: '显示或者隐藏',
              valueScope: (() => {
                return ['true', 'false'];
              })()
            }
          ]
        }
      ];
    },
    tabIdMap() {
      let map = {};
      for (let i = 0, len = this.tabs.length; i < len; i++) {
        map[this.tabs[i].id] = this.tabs[i];
        map[this.tabs[i].id]._tabIndex = i;
      }
      return map;
    },
    isIframeTab() {
      return this.hasIframe(this.tabIdMap[this.activeKey]);
    },
    iconSize() {
      return { small: 20, default: 20, large: 24 }[this.widget.configuration.size];
    }
  },
  created() {
    if (this.urlHashParams) {
      if (this.urlHashParams.selectKeys) {
        for (let item of this.widget.configuration.tabs) {
          if (this.urlHashParams.selectKeys.includes(item.id)) {
            this.activeKey = item.id;
            this.widget.configuration.defaultActiveKey = item.id;
            break;
          }
        }
      }
    }
    if (this.dyform && this.dyform.formElementRules) {
      this.tabs.forEach(item => {
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
          console.warn('tab页签 %s 无权限', this.tabs[i].title);
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

    if (this.activeKey == undefined || this.vHiddenTabs.includes(this.activeKey) || !Object.keys(this.tabIdMap).includes(this.activeKey)) {
      // 无默认激活的tab项，则取第一个
      for (let i = 0, len = this.tabs.length; i < len; i++) {
        if (!this.vHiddenTabs.includes(this.tabs[i].id)) {
          this.activeKey = this.tabs[i].id;
          break;
        }
      }
    }
    this.toForceRender();
  },
  methods: {
    onRefreshTab(i) {
      this.$set(this.tabs[i], 'loading', true);
      this.$nextTick(() => {
        this.tabs[i].loading = false;
        this.toForceRender();
      });
    },
    onCloseTabs(i, direction) {
      if (direction === 'left-right') {
        this.tabs = [this.tabs[i]];
        this.activeKey = this.tabs[0].id;
      } else if (direction === 'left') {
        let id = this.tabs[i].id;
        for (let i = 0; i < this.tabs.length; i++) {
          if (this.tabs[i].id != id) {
            if (this.tabs[i].configuration.closable !== false) {
              // 非不可关闭的tab，则关闭
              this.tabs.splice(i, 1);
              i--;
            }
          } else {
            this.activeKey = this.tabs[i].id;
            break;
          }
        }
      } else if (direction === 'right') {
        let id = this.tabs[i].id;
        for (let i = this.tabs.length - 1; i >= 0; i--) {
          if (this.tabs[i].id != id) {
            if (this.tabs[i].configuration.closable !== false) {
              // 非不可关闭的tab，则关闭
              this.tabs.splice(i, 1);
            }
          } else {
            this.activeKey = this.tabs[i].id;
            break;
          }
        }
      }
      // this.updateWidgetStateAsUrl(this.widget.id, 'activeKey', this.activeKey, true);
    },
    onTabClick(id) {
      // this.updateWidgetStateAsUrl(this.widget.id, 'activeKey', id, true);
      // console.log(arguments);
      // let eventHandler = this.tabIdMap[id].configuration.eventHandler;
      // if (eventHandler && eventHandler.pageUuid) {
      //   this.updateWidgetStateAsUrl('_sub_page_', 'uuid', eventHandler.pageUuid, true);
      // }
      this.invokeDevelopmentMethod('onTabClick', id);
    },
    addTab(options) {
      let { title, id, widgets, closable, eventHandler, $event, url } = options;
      if (!id) {
        console.error('新增tab无指定id值');
        return;
      }
      id = `tab-${id}`;
      for (let i = 0, len = this.tabs.length; i < len; i++) {
        if (this.tabs[i].id === id) {
          this.activeKey = id;
          this.tabs[i] = {
            title,
            key: 'tab-content-key-' + new Date().getTime(),
            id,
            loading: false,
            configuration: {
              url,
              $event,
              eventHandler,
              closable,
              widgets: widgets || []
            }
          };
          // this.updateWidgetStateAsUrl(this.widget.id, 'activeKey', id, true);
          // if (eventHandler.pageUuid) {
          //   this.updateWidgetStateAsUrl('_sub_page_', 'uuid', eventHandler.pageUuid, true);
          // }
          return;
        }
      }
      this.tabs.push({
        title,
        id,
        loading: false,
        configuration: {
          $event,
          eventHandler,
          closable,
          url,
          widgets: widgets || []
        }
      });
      this.activeKey = id;
      // this.updateWidgetStateAsUrl(this.widget.id, 'activeKey', id, true);
      // if (eventHandler.pageUuid) {
      //   this.updateWidgetStateAsUrl('_sub_page_', 'uuid', eventHandler.pageUuid, true);
      // }
    },
    onEdit(targetKey, action) {
      this[action](targetKey);
    },
    remove(id) {
      for (let i = 0, len = this.tabs.length; i < len; i++) {
        if (this.tabs[i].id === id) {
          this.activeKey = null;
          if (i > 0) {
            this.activeKey = this.tabs[i - 1].id;
          }
          this.tabs.splice(i, 1);
          return;
        }
      }
    },
    onChange(activeKey) {
      this.activeKey = activeKey;
      // 切换时刷新标签页
      if (this.tabIdMap[this.activeKey].configuration.refresh) {
        let tabIndex = this.tabIdMap[this.activeKey]._tabIndex;
        if (tabIndex > -1) {
          this.onRefreshTab(tabIndex);
        }
      } else {
        this.toForceRender();
      }
      this.pageContext.emitEvent(`${this.widget.id}:change`, {
        widget: this.widget,
        activeKey,
        title: this.tabIdMap[activeKey].title
      });
      this.pageContext.emitEvent(`tab:${this.$refs.tabs.$children[0]._uid}:changeTab`, activeKey);
    },

    computeBadge() {
      let _this = this;
      for (let i = 0, len = this.widget.configuration.tabs.length; i < len; i++) {
        let tab = this.widget.configuration.tabs[i];
        (badge => {
          if (badge && badge.enable) {
            let { badgeSourceType, dataSourceId, countJsFunction, defaultCondition, dataModelUuid } = badge;
            if (badgeSourceType == 'jsFunction' && countJsFunction) {
              new DispatchEvent({
                actionType: 'jsFunction',
                jsFunction: countJsFunction,
                $developJsInstance: this.$developJsInstance,
                $evtWidget: _this,
                meta: tab,
                after: num => {
                  _this.$set(badge, 'count', num);
                }
              }).dispatch();
            } else if (badgeSourceType == 'dataModel' || badgeSourceType == 'dataSource') {
              let _this = this;
              // 创建数据源
              let dsOptions = {
                onDataChange: function (data, count, params) {
                  _this.$set(badge, 'count', count);
                },
                receiver: _this,
                params: {},
                defaultCriterions: defaultCondition
                  ? [
                      {
                        sql: defaultCondition
                      }
                    ]
                  : []
              };
              if (dataSourceId && badgeSourceType == 'dataSource') {
                dsOptions.dataStoreId = dataSourceId;
                new DataSourceBase(dsOptions).getCount(true);
              } else if (badgeSourceType == 'dataModel' && dataModelUuid) {
                dsOptions.loadDataUrl = '/proxy/api/dm/loadData/' + dataModelUuid;
                dsOptions.loadDataCntUrl = '/proxy/api/dm/loadDataCount/' + dataModelUuid;
                new DataSourceBase(dsOptions).getCount(true);
              }
            }
          }
        })(tab.configuration.badge);
      }
    },

    averageTabWidth() {
      // tab 等长
      let tabs = this.$el.querySelectorAll('.ant-tabs-tab');
      let maxWidth = 0;
      for (let i = 0, len = tabs.length; i < len; i++) {
        maxWidth = Math.max(maxWidth, tabs[0].clientWidth);
      }
      for (let i = 0, len = tabs.length; i < len; i++) {
        tabs[i].style.width = maxWidth + 'px';
        if (i == 0) {
          tabs[i].parentElement.style['text-align'] = 'center';
        }
      }
    },

    resizeTabContentHeight(height) {
      let _style = window.getComputedStyle(this.$el),
        marginPaddingHeight =
          parseInt(_style.paddingBottom) + parseInt(_style.paddingTop) + parseInt(_style.marginBottom) + parseInt(_style.marginTop);

      if (this.widget.configuration.asWindow || (this.widget.configuration.style && this.widget.configuration.style.height === '100%')) {
        let $topBar = this.$el.querySelector('.ant-tabs-top-bar'),
          topBarHeight = 0;
        if ($topBar) {
          topBarHeight = $topBar.getBoundingClientRect().height + parseFloat(window.getComputedStyle($topBar).marginBottom);
        }

        // padding 值
        let padding = 0;
        let $ps = this.$el.querySelector('.tab-pane-container .ps');
        if ($ps) {
          let psStyle = window.getComputedStyle($ps);
          padding += parseInt(psStyle.paddingBottom) + parseInt(psStyle.paddingTop);
        }

        if (typeof height == 'number') {
          this.tabContentHeight = height - topBarHeight - padding + 'px';
          this.tabContentScrollHeight = height - topBarHeight + 'px';
        } else if (height.indexOf('calc(') == 0) {
          let _temp = height.substring(height.indexOf('calc(') + 5, height.length - 1).trim();
          this.tabContentHeight = `calc(${_temp} - ${topBarHeight + padding + 5}px)`;
          this.tabContentScrollHeight = `calc(${_temp} - ${topBarHeight}px)`;
        } else {
          this.tabContentHeight = parseInt(height) - topBarHeight - padding + 'px';
          this.tabContentScrollHeight = parseInt(height) - topBarHeight + 'px';
        }
      }
    },
    refetchBadge() {
      this.computeBadge();
    },
    setTabVisible(tabId, visible = true) {
      let id = tabId,
        title = undefined;
      if (typeof tabId !== 'string' && tabId.eventParams != undefined) {
        // 由事件传递进来的参数
        visible = tabId.eventParams.visible !== 'false';
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
    hasIframe(tab) {
      if (tab) {
        if (
          tab.configuration &&
          tab.configuration.eventHandler &&
          tab.configuration.eventHandler.pageType === 'url' &&
          tab.configuration.eventHandler.url != undefined
        ) {
          return tab.configuration.eventHandler.url;
        } else if (
          tab.configuration &&
          tab.configuration.eventHandler &&
          tab.configuration.eventHandler.pageType === 'page' &&
          tab.configuration.eventHandler.pageId != undefined
        ) {
          if (tab.configuration.widgets && tab.configuration.widgets.length == 0 && tab.configuration.url != undefined) {
            return tab.configuration.url;
          }
        }
      }
      return false;
    },
    toForceRender() {
      // 如果iframe初始不渲染，当切换到该tab时，才渲染iframe
      if (this.hasIframe(this.tabIdMap[this.activeKey])) {
        if (this.tabIdMap[this.activeKey].configuration.forceRender === false) {
          let hasIndex = this.tabs.findIndex(tab => tab.id === this.activeKey);
          if (hasIndex > -1) {
            this.tabs[hasIndex].configuration.forceRender = true;
          }
        }
        this.setIframeLoad();
      }
    },
    // 同步iframe页面国际化，如果不一致，重新渲染iframe页面
    setIframeLoad() {
      let _this = this;
      let $iframe = this.$el && this.$el.querySelector('#' + this.activeKey + '_iframe');
      if ($iframe) {
        _this.setIframeHeight($iframe);
        try {
          if (!$iframe.contentWindow.app) {
            // IE
            if ($iframe.attachEvent) {
              $iframe.attachEvent('onload', () => {
                // 加载成功
                //这里是回调函数
                _this.setIframeI18n($iframe);
              });
            } else {
              $iframe.onload = () => {
                // 加载成功
                //这里是回调函数
                _this.setIframeI18n($iframe);
              };
            }
          } else if ($iframe.contentWindow.app.__vue__) {
            _this.setIframeI18n($iframe);
          }
        } catch (error) {
          this.iframeLoading = false;
        }
      } else {
        this.iframeLoading = true;
        setTimeout(() => {
          _this.setIframeLoad();
        }, 10);
      }
    },
    // 同步iframe页面国际化，如果不一致，刷新iframe页面
    setIframeI18n($iframe) {
      let _this = this;
      try {
        if ($iframe.contentWindow.app) {
          let $pageVue = $iframe.contentWindow.app.__vue__;
          if ($pageVue.$i18n.locale !== _this.$i18n.locale) {
            this.iframeLoading = true;
            $pageVue.$i18n.locale = _this.$i18n.locale;
            let pagetWidget = $pageVue.widget;
            if (pagetWidget) {
              $pageVue.refresh({ pageId: pagetWidget.id });
            }
          }
          $pageVue.$nextTick(() => {
            this.iframeLoading = false;
          });
        }
      } catch (error) {
        this.iframeLoading = false;
      }
    },
    setIframeHeight($iframe) {
      if (this.tabContentHeight == 'auto') {
        let $parent = this.$root.$el.classList.contains('preview') ? this.$root.$el : this.$el.closest('.widget-vpage');
        let { maxHeight } = getElSpacingForTarget(this.$el, $parent);
        const paneStyle = window.getComputedStyle($iframe.parentElement);
        maxHeight = maxHeight - getTopStylesSum(paneStyle) - getBottomStylesSum(paneStyle) - 5;
        if (this.widget.configuration.tabPosition == 'left' || this.widget.configuration.tabPosition == 'right') {
          $iframe.style.height = `${maxHeight}px`;
        } else {
          let $tabBar = this.$el.querySelector('.ant-tabs-bar');
          const tabbarStyle = window.getComputedStyle($tabBar);
          let tabBarHeight =
            (parseFloat(tabbarStyle.marginTop) || 0) + (parseFloat(tabbarStyle.marginBottom) || 0) + (parseFloat(tabbarStyle.height) || 50);
          $iframe.style.height = `${maxHeight - tabBarHeight}px`;
        }
      }
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
          promises.push(this.calculateVisibleByCondition(configuration.defaultVisibleVar, configuration.defaultVisible));
        } else {
          promises.push(Promise.resolve(true));
        }
      }
      Promise.all(promises).then(results => {
        for (let i = 0, len = tabs.length; i < len; i++) {
          if (!results[i]) {
            ids.push(tabs[i].id);
          }
          this.vHiddenTabs.splice(0, this.vHiddenTabs.length, ...ids);
        }
      });
    },
    afterChangeableDependDataChanged() {
      this.reCalculateHiddenTabIds();
    }
  },
  beforeMount() {
    if (this.widget.configuration.height == 'auto' || this.widget.configuration.height == undefined) {
      this.tabContentHeight = 'auto';
      this.tabContentScrollHeight = 'auto';
    } else {
      let padding =
        this.widget.configuration.style != undefined && this.widget.configuration.style.padding != undefined
          ? this.widget.configuration.style.padding[0] + this.widget.configuration.style.padding[2]
          : 0;
      this.tabContentHeight = `calc( ${this.widget.configuration.height} - ${padding}px )`;
      this.tabContentScrollHeight = `calc( ${this.widget.configuration.height} )`;
    }
  },
  mounted() {
    let _this = this;
    this.pageContext.handleEvent(`${this.widget.id}:addTab`, function (options) {
      if (options.widgets != undefined && options.widgets instanceof Promise) {
        options.widgets.then(d => {
          _this.addTab(Object.assign({}, options, { widgets: d.widgets }));
        });
      } else {
        _this.addTab(options);
      }
    });
    if (this.widget.configuration.asWindow) {
      this.pageContext.handleEvent(`-1:addTab`, function (options) {
        _this.addTab(options);
      });
      if (this.containerStyle != undefined && this.containerStyle.height != undefined) {
        this.resizeTabContentHeight(this.containerStyle.height);
      }
    }
    if (this.parent) {
      this.pageContext.handleEvent(`${this.parent.id}:viewHeight`, v => {});
    }

    // this.$set(this, 'activeKey', this.widget.configuration.defaultActiveKey);
    // 计算徽标
    this.computeBadge();
    this.pageContext.handleEvent(`${this.widget.id}:refetchBadge`, function () {
      _this.computeBadge();
    });
  },
  beforeDestroy() {
    this.pageContext.offEvent(`${this.widget.id}:change`);
    if (this.widget.configuration.asWindow) {
      this.pageContext.offEvent('-1:addTab');
    }
    this.pageContext.offEvent(`${this.widget.id}:addTab`);
  },
  watch: {
    containerStyle: {
      deep: true,
      handler(v) {
        this.resizeTabContentHeight(v.height);
      }
    },
    vHiddenTabs: {
      handler(v) {
        if (this.activeKey == undefined || v.includes(this.activeKey)) {
          for (let i = 0, len = this.tabs.length; i < len; i++) {
            if (!v.includes(this.tabs[i].id)) {
              this.activeKey = this.tabs[i].id;
              break;
            }
          }
        }
      }
    }
  }
};
</script>
