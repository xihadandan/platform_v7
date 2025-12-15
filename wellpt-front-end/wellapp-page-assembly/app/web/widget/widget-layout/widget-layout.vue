<template>
  <div :class="['widget-layout', widget.configuration.layoutType]">
    <template v-if="widget.configuration.layoutType == 'topMiddleBottom'">
      <a-layout>
        <template v-if="designMode || (!contentFooterMergeScroll && !contentHeaderMergeScroll && !contentHeaderFooterMergeScroll)">
          <component
            v-if="widget.configuration.header.configuration.visible"
            :is="wHeader"
            :widget="widget.configuration.header"
            :parent="widget"
            :designer="designer"
            :backgroundColorType="backgroundColorType.header"
            ref="wHeader"
            :key="headerKey"
          />
          <component
            :is="wContent"
            :widget="widget.configuration.content"
            :parent="widget"
            :key="layoutContentKey"
            :designer="designer"
            ref="wContent"
          />
          <component
            v-if="widget.configuration.footer.configuration.visible"
            :is="wFooter"
            :widget="widget.configuration.footer"
            :parent="widget"
            :designer="designer"
            ref="wFooter"
          />
        </template>
        <template v-else>
          <template v-if="contentHeaderMergeScroll">
            <Scroll :style="{ height: mergeScrollHeight }">
              <component
                v-if="widget.configuration.header.configuration.visible"
                :is="wHeader"
                :widget="widget.configuration.header"
                :parent="widget"
                :designer="designer"
                :backgroundColorType="backgroundColorType.header"
                ref="wHeader"
                :key="headerKey"
              />
              <component
                :is="wContent"
                :widget="widget.configuration.content"
                :parent="widget"
                :key="layoutContentKey"
                :designer="designer"
                ref="wContent"
              />
            </Scroll>
            <component
              v-if="widget.configuration.footer.configuration.visible"
              :is="wFooter"
              :widget="widget.configuration.footer"
              :parent="widget"
              :designer="designer"
              ref="wFooter"
            />
          </template>
          <template v-else-if="contentFooterMergeScroll">
            <component
              v-if="widget.configuration.header.configuration.visible"
              :is="wHeader"
              :widget="widget.configuration.header"
              :parent="widget"
              :designer="designer"
              :backgroundColorType="backgroundColorType.header"
              ref="wHeader"
              :key="headerKey"
            />
            <Scroll :style="{ height: mergeScrollHeight }">
              <component
                :is="wContent"
                :widget="widget.configuration.content"
                :parent="widget"
                :key="layoutContentKey"
                :designer="designer"
                ref="wContent"
              />
              <component
                v-if="widget.configuration.footer.configuration.visible"
                :is="wFooter"
                :widget="widget.configuration.footer"
                :parent="widget"
                :designer="designer"
                ref="wFooter"
              />
            </Scroll>
          </template>
          <template v-else-if="contentHeaderFooterMergeScroll">
            <Scroll :style="{ height: mergeScrollHeight }">
              <component
                v-if="widget.configuration.header.configuration.visible"
                :is="wHeader"
                :widget="widget.configuration.header"
                :parent="widget"
                :designer="designer"
                :backgroundColorType="backgroundColorType.header"
                ref="wHeader"
                :key="headerKey"
              />
              <component
                :is="wContent"
                :widget="widget.configuration.content"
                :parent="widget"
                :key="layoutContentKey"
                :designer="designer"
                ref="wContent"
              />
              <component
                v-if="widget.configuration.footer.configuration.visible"
                :is="wFooter"
                :widget="widget.configuration.footer"
                :parent="widget"
                :designer="designer"
                ref="wFooter"
              />
            </Scroll>
          </template>
        </template>
      </a-layout>
    </template>
    <template
      v-else-if="
        widget.configuration.layoutType == 'topMiddleSiderBottom' || widget.configuration.layoutType == 'topMiddleRightSiderBottom'
      "
    >
      <template v-if="widget.configuration.sider.configuration.visible">
        <a-layout>
          <component
            :is="wHeader"
            :widget="widget.configuration.header"
            :parent="widget"
            :designer="designer"
            :backgroundColorType="backgroundColorType.header"
            v-if="widget.configuration.header.configuration.visible"
            ref="wHeader"
            :key="headerKey"
          />
          <a-layout
            :hasSider="true"
            :style="{ flexDirection: widget.configuration.layoutType == 'topMiddleSiderBottom' ? 'row' : 'row-reverse' }"
          >
            <component
              :is="wSider"
              :widget="widget.configuration.sider"
              :parent="widget"
              :designer="designer"
              ref="wSider"
              :key="siderKey"
              :backgroundColorType="backgroundColorType.sider"
              :siderWidth="siderWidth"
            />
            <component
              :is="wContent"
              :widget="widget.configuration.content"
              :parent="widget"
              :key="layoutContentKey"
              :designer="designer"
              ref="wContent"
            />
          </a-layout>
          <component
            :is="wFooter"
            :widget="widget.configuration.footer"
            :parent="widget"
            :designer="designer"
            v-if="widget.configuration.footer.configuration.visible"
            ref="wFooter"
          />
        </a-layout>
      </template>
      <template v-else>
        <a-layout>
          <template v-if="designMode || (!contentFooterMergeScroll && !contentHeaderMergeScroll && !contentHeaderFooterMergeScroll)">
            <component
              v-if="widget.configuration.header.configuration.visible"
              :is="wHeader"
              :widget="widget.configuration.header"
              :parent="widget"
              :designer="designer"
              :backgroundColorType="backgroundColorType.header"
              ref="wHeader"
              :key="headerKey"
            />
            <component
              :is="wContent"
              :widget="widget.configuration.content"
              :parent="widget"
              :key="layoutContentKey"
              :designer="designer"
              ref="wContent"
            />
            <component
              v-if="widget.configuration.footer.configuration.visible"
              :is="wFooter"
              :widget="widget.configuration.footer"
              :parent="widget"
              :designer="designer"
              ref="wFooter"
            />
          </template>
          <template v-else>
            <template v-if="contentHeaderMergeScroll">
              <Scroll :style="{ height: mergeScrollHeight }">
                <component
                  v-if="widget.configuration.header.configuration.visible"
                  :is="wHeader"
                  :widget="widget.configuration.header"
                  :parent="widget"
                  :designer="designer"
                  :backgroundColorType="backgroundColorType.header"
                  ref="wHeader"
                  :key="headerKey"
                />
                <component
                  :is="wContent"
                  :widget="widget.configuration.content"
                  :parent="widget"
                  :key="layoutContentKey"
                  :designer="designer"
                  ref="wContent"
                />
              </Scroll>
              <component
                v-if="widget.configuration.footer.configuration.visible"
                :is="wFooter"
                :widget="widget.configuration.footer"
                :parent="widget"
                :designer="designer"
                ref="wFooter"
              />
            </template>
            <template v-else-if="contentFooterMergeScroll">
              <component
                v-if="widget.configuration.header.configuration.visible"
                :is="wHeader"
                :widget="widget.configuration.header"
                :parent="widget"
                :designer="designer"
                :backgroundColorType="backgroundColorType.header"
                ref="wHeader"
                :key="headerKey"
              />
              <Scroll :style="{ height: mergeScrollHeight }">
                <component
                  :is="wContent"
                  :widget="widget.configuration.content"
                  :parent="widget"
                  :key="layoutContentKey"
                  :designer="designer"
                  ref="wContent"
                />
                <component
                  v-if="widget.configuration.footer.configuration.visible"
                  :is="wFooter"
                  :widget="widget.configuration.footer"
                  :parent="widget"
                  :designer="designer"
                  ref="wFooter"
                />
              </Scroll>
            </template>
            <template v-else-if="contentHeaderFooterMergeScroll">
              <Scroll :style="{ height: mergeScrollHeight }">
                <template slot="default">
                  <component
                    v-if="widget.configuration.header.configuration.visible"
                    :is="wHeader"
                    :widget="widget.configuration.header"
                    :parent="widget"
                    :designer="designer"
                    :backgroundColorType="backgroundColorType.header"
                    ref="wHeader"
                    :key="headerKey"
                  />
                  <component
                    :is="wContent"
                    :widget="widget.configuration.content"
                    :parent="widget"
                    :key="layoutContentKey"
                    :designer="designer"
                    ref="wContent"
                  />
                  <component
                    v-if="widget.configuration.footer.configuration.visible"
                    :is="wFooter"
                    :widget="widget.configuration.footer"
                    :parent="widget"
                    :designer="designer"
                    ref="wFooter"
                  />
                </template>
              </Scroll>
            </template>
          </template>
        </a-layout>
      </template>
    </template>

    <template v-else-if="widget.configuration.layoutType == 'siderTopMiddleBottom'">
      <a-layout v-if="widget.configuration.sider.configuration.visible" :hasSider="true">
        <component
          :is="wSider"
          :widget="widget.configuration.sider"
          :parent="widget"
          :designer="designer"
          :backgroundColorType="backgroundColorType.sider"
          ref="wSider"
          :key="siderKey"
          :siderWidth="siderWidth"
        />
        <!-- :style="{
            marginLeft: widget.configuration.sider.configuration.positionFixed ? siderWidth + 'px' : 'unset'
          }" -->
        <a-layout>
          <template v-if="designMode || (!contentFooterMergeScroll && !contentHeaderMergeScroll && !contentHeaderFooterMergeScroll)">
            <component
              :is="wHeader"
              :widget="widget.configuration.header"
              :parent="widget"
              :designer="designer"
              v-if="widget.configuration.header.configuration.visible"
              :backgroundColorType="backgroundColorType.header"
              ref="wHeader"
              :key="headerKey"
            />
            <component
              :is="wContent"
              :widget="widget.configuration.content"
              :parent="widget"
              :key="layoutContentKey"
              :designer="designer"
              ref="wContent"
            />
            <component
              :is="wFooter"
              :widget="widget.configuration.footer"
              :parent="widget"
              :designer="designer"
              v-if="widget.configuration.footer.configuration.visible"
              ref="wFooter"
            />
          </template>
          <template v-else>
            <template v-if="contentHeaderMergeScroll">
              <Scroll :style="{ height: mergeScrollHeight }">
                <component
                  :is="wHeader"
                  :widget="widget.configuration.header"
                  :parent="widget"
                  :designer="designer"
                  v-if="widget.configuration.header.configuration.visible"
                  :backgroundColorType="backgroundColorType.header"
                  ref="wHeader"
                  :key="headerKey"
                />
                <component
                  :is="wContent"
                  :widget="widget.configuration.content"
                  :parent="widget"
                  :key="layoutContentKey"
                  :designer="designer"
                  ref="wContent"
                />
              </Scroll>
              <component
                :is="wFooter"
                :widget="widget.configuration.footer"
                :parent="widget"
                :designer="designer"
                v-if="widget.configuration.footer.configuration.visible"
                ref="wFooter"
              />
            </template>
            <template v-if="contentFooterMergeScroll">
              <component
                :is="wHeader"
                :widget="widget.configuration.header"
                :parent="widget"
                :designer="designer"
                v-if="widget.configuration.header.configuration.visible"
                :backgroundColorType="backgroundColorType.header"
                ref="wHeader"
                :key="headerKey"
              />
              <Scroll :style="{ height: mergeScrollHeight }">
                <component
                  :is="wContent"
                  :widget="widget.configuration.content"
                  :parent="widget"
                  :key="layoutContentKey"
                  :designer="designer"
                  ref="wContent"
                />
                <component
                  :is="wFooter"
                  :widget="widget.configuration.footer"
                  :parent="widget"
                  :designer="designer"
                  v-if="widget.configuration.footer.configuration.visible"
                  ref="wFooter"
                />
              </Scroll>
            </template>
            <template v-if="contentHeaderFooterMergeScroll">
              <Scroll :style="{ height: mergeScrollHeight }">
                <component
                  :is="wHeader"
                  :widget="widget.configuration.header"
                  :parent="widget"
                  :designer="designer"
                  v-if="widget.configuration.header.configuration.visible"
                  :backgroundColorType="backgroundColorType.header"
                  ref="wHeader"
                  :key="headerKey"
                />
                <component
                  :is="wContent"
                  :widget="widget.configuration.content"
                  :parent="widget"
                  :key="layoutContentKey"
                  :designer="designer"
                  ref="wContent"
                />
                <component
                  :is="wFooter"
                  :widget="widget.configuration.footer"
                  :parent="widget"
                  :designer="designer"
                  v-if="widget.configuration.footer.configuration.visible"
                  ref="wFooter"
                />
              </Scroll>
            </template>
          </template>
        </a-layout>
      </a-layout>
      <a-layout v-else>
        <template v-if="designMode || (!contentFooterMergeScroll && !contentHeaderMergeScroll && !contentHeaderFooterMergeScroll)">
          <component
            :is="wHeader"
            :widget="widget.configuration.header"
            :parent="widget"
            :designer="designer"
            v-if="widget.configuration.header.configuration.visible"
            :backgroundColorType="backgroundColorType.header"
            ref="wHeader"
            :key="headerKey"
          />
          <component
            :is="wContent"
            :widget="widget.configuration.content"
            :parent="widget"
            :key="layoutContentKey"
            :designer="designer"
            ref="wContent"
          />
          <component
            :is="wFooter"
            :widget="widget.configuration.footer"
            :parent="widget"
            :designer="designer"
            v-if="widget.configuration.footer.configuration.visible"
            ref="wFooter"
          />
        </template>
        <template v-else>
          <template v-if="contentHeaderMergeScroll">
            <Scroll :style="{ height: mergeScrollHeight }">
              <component
                v-if="widget.configuration.header.configuration.visible"
                :is="wHeader"
                :widget="widget.configuration.header"
                :parent="widget"
                :designer="designer"
                :backgroundColorType="backgroundColorType.header"
                ref="wHeader"
                :key="headerKey"
              />
              <component
                :is="wContent"
                :widget="widget.configuration.content"
                :parent="widget"
                :key="layoutContentKey"
                :designer="designer"
                ref="wContent"
              />
            </Scroll>
            <component
              v-if="widget.configuration.footer.configuration.visible"
              :is="wFooter"
              :widget="widget.configuration.footer"
              :parent="widget"
              :designer="designer"
              ref="wFooter"
            />
          </template>
          <template v-if="contentFooterMergeScroll">
            <component
              v-if="widget.configuration.header.configuration.visible"
              :is="wHeader"
              :widget="widget.configuration.header"
              :parent="widget"
              :designer="designer"
              :backgroundColorType="backgroundColorType.header"
              ref="wHeader"
              :key="headerKey"
            />
            <Scroll :style="{ height: mergeScrollHeight }">
              <component
                :is="wContent"
                :widget="widget.configuration.content"
                :parent="widget"
                :key="layoutContentKey"
                :designer="designer"
                ref="wContent"
              />
              <component
                v-if="widget.configuration.footer.configuration.visible"
                :is="wFooter"
                :widget="widget.configuration.footer"
                :parent="widget"
                :designer="designer"
                ref="wFooter"
              />
            </Scroll>
          </template>
          <template v-if="contentHeaderFooterMergeScroll">
            <component
              v-if="widget.configuration.header.configuration.visible"
              :is="wHeader"
              :widget="widget.configuration.header"
              :parent="widget"
              :designer="designer"
              :backgroundColorType="backgroundColorType.header"
              ref="wHeader"
              :key="headerKey"
            />
            <component
              :is="wContent"
              :widget="widget.configuration.content"
              :parent="widget"
              :key="layoutContentKey"
              :designer="designer"
              ref="wContent"
            />
            <component
              v-if="widget.configuration.footer.configuration.visible"
              :is="wFooter"
              :widget="widget.configuration.footer"
              :parent="widget"
              :designer="designer"
              ref="wFooter"
            />
          </template>
        </template>
      </a-layout>
    </template>
  </div>
</template>
<style lang="less"></style>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import windowMixin from '@framework/vue/mixin/windowMixin';

import { addWindowResizeHandler, deepClone, generateId } from '@framework/vue/utils/util';
import './css/index.less';
export default {
  name: 'WidgetLayout',
  mixins: [widgetMixin, windowMixin],
  inject: ['containerStyle', 'ENVIRONMENT', 'THEME'],
  props: {
    containerHeight: Number | String
  },
  provide() {
    return {
      widgetLayoutContext: this
    };
  },
  data() {
    return {
      siderWidth: 224,
      heightCalculated: false,
      wKey: this.widget.id,
      headerKey: this.widget.configuration.header.id,
      siderKey: this.widget.configuration.sider.id,
      layoutContentKey: this.widget.configuration.content.id,
      height: this.containerHeight || (this.containerStyle ? this.containerStyle.height : 'calc(100vh)'),
      backgroundColorType: {
        sider: undefined,
        header: undefined
      },
      computeSize: {
        header: {
          width: undefined,
          height: undefined
        },
        footer: {
          width: undefined,
          height: undefined
        },
        sider: {
          width: undefined,
          height: undefined
        }
      },
      scrollHeight: 'auto',
      scrollType: undefined,
      wrapperScroll: false,
      contentHeaderMergeScroll: false,
      contentFooterMergeScroll: false,
      contentHeaderFooterMergeScroll: false,
      mergeScrollHeight: '600px'
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    wHeader() {
      // 系统主页的布局，暂时不开放头部设计区域
      return this.widget.main ? 'WidgetLayoutHeader' : `${this.designer != undefined ? 'E' : ''}WidgetLayoutHeader`;
    },
    wSider() {
      return `${this.designer != undefined ? 'E' : ''}WidgetLayoutSider`;
    },
    wContent() {
      return `${this.designer != undefined ? 'E' : ''}WidgetLayoutContent`;
    },
    wFooter() {
      return `${this.designer != undefined ? 'E' : ''}WidgetLayoutFooter`;
    },
    defaultEvents() {
      return [{ id: 'restoreLayoutContent', title: '重置布局内容区' }];
    }
  },
  created() {
    // 根据产品版本设置修改布局导航形态
    this.adjustLayoutByLayoutConfig();
    this.layoutZoneFixed();
    this.initWidgetJSON = JSON.stringify(this.widget);
  },
  methods: {
    open() {
      this.$refs.wContent.open.apply(this.$refs.wContent, arguments);
    },
    close() {
      this.$refs.wContent.close();
    },
    adjustLayoutByLayoutConfig(config) {
      let layoutConf = config == undefined ? (this.ENVIRONMENT != undefined ? this.ENVIRONMENT.layoutConf : undefined) : config;

      if (layoutConf && !layoutConf.hasOwnProperty('menuCollapseBtnVisible')) {
        layoutConf.menuCollapseBtnVisible = layoutConf.menuCollapseBtnDisplayType !== 'hidden';
      }

      if (this.widget.main && layoutConf && (config != undefined || !this.widget.serverRendered)) {
        // 服务端渲染直接修改了配置
        if (EASY_ENV_IS_NODE) {
          // 标记为服务端已渲染修改定义
          this.widget.serverRendered = true;
          this.widget.originalHeaderConfigurationString = JSON.stringify(this.widget.configuration.header);
          this.widget.originalSiderConfigurationString = JSON.stringify(this.widget.configuration.sider);
        }
        if (config != undefined) {
          if (this.widget.originalHeaderConfigurationString != undefined) {
            this.$set(this.widget.configuration, 'header', JSON.parse(this.widget.originalHeaderConfigurationString));
          }
          if (this.widget.originalSiderConfigurationString != undefined) {
            this.$set(this.widget.configuration, 'sider', JSON.parse(this.widget.originalSiderConfigurationString));
          }
        }

        let originLayoutType = this.widget.configuration.layoutType,
          layoutType = layoutConf.layoutType,
          menuPosition = layoutConf.menuPosition,
          appMenusToSider = layoutConf.appMenusToSider,
          widgetMenu = null,
          hasSider = originLayoutType.toLowerCase().indexOf('sider') != -1,
          siderWidgets = this.widget.configuration.sider.configuration.widgets,
          headerWidgets = this.widget.configuration.header.configuration.widgets;
        // 获取主导航组件
        if (siderWidgets.length) {
          for (let i = 0, len = siderWidgets.length; i < len; i++) {
            if (siderWidgets[i].main && siderWidgets[i].wtype == 'WidgetMenu') {
              widgetMenu = siderWidgets[i];
              siderWidgets.splice(i, 1);
              break;
            }
          }
        }
        if (widgetMenu == null) {
          if (headerWidgets.length) {
            for (let i = 0, len = headerWidgets.length; i < len; i++) {
              if (headerWidgets[i].main && headerWidgets[i].wtype == 'WidgetMenu') {
                widgetMenu = headerWidgets[i];
                break;
              }
            }
          }
        }

        this.widget.configuration.layoutType = layoutType; // 布局调整为导航布局设置的布局类型

        if (widgetMenu != null) {
          this.widget.configuration.mainWidgetMenuId = widgetMenu.id;
          // 根据当前布局类型，修改菜单的形态
          if (menuPosition == 'header') {
            widgetMenu.configuration.mode = 'horizontal';
            widgetMenu.configuration.align = layoutConf.horizontalAlign;
            widgetMenu.configuration.enableSysMenu = true;
            // this.widget.configuration.sider.configuration.widgets = [];
            this.widget.configuration.header.configuration.widgets = [widgetMenu];
            this.widget.configuration.header.configuration.showHeaderMenuBar = layoutConf.topMenuBar;
            // 头部的菜单下拉展开方式
            widgetMenu.configuration.subMenuExpandType = layoutConf.horizontalExpandType;
            // widgetMenu.configuration.breadcrumbVisible = layoutConf.breadcrumbVisible;

            if (appMenusToSider) {
              // 头部模块导航渲染到侧边
              widgetMenu.configuration.appMenusToSider = this.widget.configuration.sider.id;
            } else {
              widgetMenu.configuration.appMenusToSider = undefined;
            }
          } else {
            // 其他情况都是菜单放在侧边栏内
            widgetMenu.configuration.mode = 'inline';
            widgetMenu.configuration.appMenusToSider = undefined;
            // widgetMenu.configuration.breadcrumbVisible = layoutConf.breadcrumbVisible;
            this.widget.configuration.sider.configuration.widgets = [widgetMenu];
            this.widget.configuration.header.configuration.widgets = [];
          }
        }

        this.widget.configuration.header.configuration.stickyOnTop = !!layoutConf.headerFixed;

        if (!layoutConf.hasOwnProperty('menuCollapseBtnVisible')) {
          if (layoutConf.menuCollapseBtnDisplayType && layoutConf.menuCollapseBtnDisplayType != 'hidden') {
            this.widget.configuration.sider.configuration.collapsible = true;
            if (layoutConf.menuCollapseBtnDisplayType == 'topDisplay') {
              // 左导航的折叠按钮位置
              this.widget.configuration.sider.configuration.collapsePosition = 'top';
            } else if (layoutConf.menuCollapseBtnDisplayType == 'bottomDisplay') {
              this.widget.configuration.sider.configuration.collapsePosition = 'bottom';
            }
          } else {
            this.widget.configuration.sider.configuration.collapsible = false;
          }
          layoutConf.menuCollapseBtnVisible = layoutConf.menuCollapseBtnDisplayType !== 'hidden';
        } else {
          this.widget.configuration.sider.configuration.collapsible = layoutConf.menuCollapseBtnVisible;
        }
        if (layoutConf.siderMenuCollapseType == 'collapsed') {
          // 左导航默认状态是否折叠
          this.widget.configuration.sider.configuration.defaultCollapsed = true;
        } else if (layoutConf.siderMenuCollapseType == 'unCollapsed') {
          this.widget.configuration.sider.configuration.defaultCollapsed = false;
        }

        // 页面以页签形式打开
        this.widget.configuration.content.configuration.contentAsTabs = !!layoutConf.pageToTabs;
        let menus = widgetMenu.configuration.menus;
        if (menus.length > 0 && menus[0].index) {
          this.widget.configuration.content.configuration.defaultTabTitle = this.$t(
            `Widget.${widgetMenu.id}.${menus[0]['id']}`,
            menus[0].title
          );
        }
        if (this.widget.configuration.layoutType.startsWith('top')) {
          // top类布局：topMiddleBottom \ topMiddleSiderBottom \ topMiddleRightSiderBottom
          // logo位置都是在头部
          this.widget.configuration.logoPosition = 'header';
        } else {
          // sider类布局：需要决定logo位置显示在sider区域或者header区域
          if (!this.widget.configuration.logoPositionSelfControl) {
            this.widget.configuration.logoPosition = layoutConf.logoPosition;
          }
        }
      }

      // 非配置性变更，计算颜色变量
      // 颜色风格: 组件未自定义则使用统一布局的配置,首页配置有背景固定色，按这个固定色配置
      // 有自定义按自定义布局
      if (this.widget.configuration.header.configuration.backgroundColorType) {
        this.backgroundColorType.header = this.widget.configuration.header.configuration.backgroundColorType;
        if (layoutConf && this.widget.main) {
          layoutConf.showHeaderBgColorType = false; //用于判断风格切换是否显示
        }
      } else if (this.widget.main && layoutConf) {
        if (layoutConf.headerBgColorType) {
          this.backgroundColorType.header = layoutConf.headerBgColorType;
        }
        layoutConf.showHeaderBgColorType = true;
      }
      if (this.widget.configuration.sider.configuration.backgroundColorType) {
        if (layoutConf && this.widget.main) {
          layoutConf.showSiderBgColorType = false; //用于判断风格切换是否显示
        }
        this.backgroundColorType.sider = this.widget.configuration.sider.configuration.backgroundColorType;
      } else if (this.widget.main && layoutConf) {
        if (layoutConf.siderBgColorType) {
          this.backgroundColorType.sider = layoutConf.siderBgColorType;
        }
        layoutConf.showSiderBgColorType = true;
      }
      if (this.widget.main && EASY_ENV_IS_BROWSER) {
        // 主导航监听跳转模块
        this.pageContext
          .offEvent(`widgetLayout:JumpToModuleNavigation:${this.widget.id}`)
          .handleEvent(`widgetLayout:JumpToModuleNavigation:${this.widget.id}`, data => {
            this.pageContext.emitEvent(`WidgetMenu:OpenModuleMenuAndLocatePageMenu:${this.widget.configuration.mainWidgetMenuId}`, {
              appId: data.appId,
              pageId: data.pageId
            });
          });
      }
    },
    restoreLayoutContent() {
      if (this.initWidgetJSON) {
        let json = JSON.parse(this.initWidgetJSON);
        this.$refs.wContent.restoreLayoutContent(json.configuration.content);
        if (this.ENVIRONMENT && this.ENVIRONMENT.layoutConf && this.ENVIRONMENT.layoutConf.appMenusToSider) {
          // 重置侧边栏区域
          this.$refs.wSider.restoreLayoutSider(json.configuration.sider);
        }
      }
    },

    // 根据高度，自适应调整布局内容区以及侧边栏的可视化高度
    adjustLayoutContentSiderHeight() {
      let wContent = this.$refs.wContent;
      let contentHeight = this.height,
        calc = false;
      if (typeof contentHeight === 'string') {
        calc = contentHeight.indexOf('calc') == 0;
        if (calc) {
          // 是计算表达式的情况下
          contentHeight = contentHeight.substring(contentHeight.indexOf('calc(') + 5, contentHeight.length - 1).trim();
        } else if (contentHeight.endsWith('px')) {
          contentHeight = parseInt(contentHeight);
        }
      }

      // 获取头部、底部占用高度
      let wHeader = this.$refs.wHeader,
        wFooter = this.$refs.wFooter,
        wSider = this.$refs.wSider,
        headerHeight = 0,
        footerHeight = 0;
      if (wHeader) {
        let _style = window.getComputedStyle(wHeader.$el);
        headerHeight = wHeader.$el.getBoundingClientRect().height + parseInt(_style.marginBottom) + parseInt(_style.marginTop);
        this.computeSize.header.height = headerHeight;
        this.computeSize.header.width = wHeader.$el.getBoundingClientRect().width;
      }

      if (wFooter) {
        let _style = window.getComputedStyle(wFooter.$el);
        footerHeight = wFooter.$el.getBoundingClientRect().height + parseInt(_style.marginBottom) + parseInt(_style.marginTop);
        this.computeSize.footer.height = footerHeight;
        this.computeSize.footer.width = wFooter.$el.getBoundingClientRect().width;
      }
      let calcHeight = null;
      if (this.contentFooterMergeScroll) {
        // 内容区与底部区合并滚动的情况下，只需要扣除顶部高度即可
        if (calc) {
          calcHeight = `calc(${contentHeight} - ${headerHeight}px)`;
          this.mergeScrollHeight = calcHeight;
          console.log(`内容区 + 底部 合并滚动高度 ${calcHeight} = ${contentHeight} - ${calcHeight}`);
        } else {
          calcHeight = contentHeight - headerHeight;
          this.mergeScrollHeight = calcHeight + 'px';
          console.log(`内容区 + 底部 合并滚动高度 ${calcHeight} = 容器高度 (${contentHeight}) - 头部高度 (${headerHeight})`);
        }
        this.updateContentHeight(wContent, calc, contentHeight, headerHeight, footerHeight);
      } else if (this.contentHeaderMergeScroll) {
        // 内容区与头部区合并滚动的情况下，只需要扣除底部部高度即可
        if (calc) {
          calcHeight = `calc(${contentHeight} - ${footerHeight}px)`;
          this.mergeScrollHeight = calcHeight;
          console.log(`内容区 + 底部 合并滚动高度 ${calcHeight} = ${contentHeight} - ${footerHeight}`);
        } else {
          calcHeight = contentHeight - footerHeight;
          this.mergeScrollHeight = calcHeight + 'px';
          console.log(`内容区 + 底部 合并滚动高度 ${calcHeight} = 容器高度 (${contentHeight}) - 低部高度 (${footerHeight})`);
        }
        this.updateContentHeight(wContent, calc, contentHeight, headerHeight, footerHeight);
      } else if (this.contentHeaderFooterMergeScroll) {
        if (calc) {
          calcHeight = `calc(${contentHeight})`;
          this.mergeScrollHeight = calcHeight;
          console.log(`头部 + 内容区 + 底部 合并滚动高度 ${calcHeight}`);
        } else {
          calcHeight = contentHeight;
          this.mergeScrollHeight = calcHeight + 'px';
          console.log(`头部 + 内容区 + 底部 合并滚动高度 ${calcHeight} = 容器高度 (${contentHeight}) - 低部高度 (${footerHeight})`);
        }

        this.updateContentHeight(wContent, calc, contentHeight, headerHeight, footerHeight);
      } else {
        this.updateContentHeight(wContent, calc, contentHeight, headerHeight, footerHeight);
      }

      if (
        this.widget.configuration.layoutType.toLowerCase().indexOf('sider') != -1 &&
        this.widget.configuration.sider.configuration.visible
      ) {
        this.computeSize.sider.width = wSider.$el.getBoundingClientRect().width;
        if (this.widget.configuration.layoutType === 'siderTopMiddleBottom') {
          wSider.updateHeight(this.height);
        } else {
          if (calcHeight == null || calcHeight.startsWith('calc')) {
            calcHeight = calc ? `calc(${contentHeight} - ${headerHeight}px)` : contentHeight - headerHeight;
          }
          wSider.updateHeight(calcHeight);
        }
      }

      console.log('布局区域计算尺寸', this.computeSize);

      this.heightCalculated = true;
    },
    updateContentHeight(wContent, calc, contentHeight, headerHeight, footerHeight) {
      let calcHeight = 'auto';
      if (calc) {
        calcHeight = `calc(${contentHeight} - ${headerHeight + footerHeight}px)`;
        console.log(`内容高度 ${calcHeight} = ${calcHeight}`);
      } else {
        calcHeight = contentHeight - headerHeight - footerHeight;
        console.log(`内容高度 ${calcHeight} = 容器高度 (${contentHeight}) - 头部高度 (${headerHeight}) - 底部高度 ${footerHeight}`);
      }

      if (calcHeight != null) {
        wContent.updateHeight(calcHeight);
      }
    },

    layoutZoneFixed() {
      // 固定区域的设定：(通过内容区高度是否自动来实现固定效果)
      if (!this.designMode) {
        let layoutType = this.widget.configuration.layoutType,
          siderConfiguration = this.widget.configuration.sider.configuration,
          footerConfiguration = this.widget.configuration.footer.configuration,
          headerConfiguration = this.widget.configuration.header.configuration,
          contentConfiguration = this.widget.configuration.content.configuration;

        let mergeScroll = () => {
          if (headerConfiguration.visible) {
            // 头部可见情况下：
            if (headerConfiguration.stickyOnTop) {
              // 头部固定的情况下，则滚动区范围由底部决定
              if (footerConfiguration.visible) {
                if (footerConfiguration.stickyOnBottom) {
                  // 底部固定，则仅内容区滚动
                  contentConfiguration.autoHeight = false;
                } else {
                  // 内容区 + 底部 合并滚动
                  this.contentFooterMergeScroll = true;
                  contentConfiguration.autoHeight = true;
                }
              } else {
                // 仅滚动内容区
                contentConfiguration.autoHeight = false;
              }
            } else {
              contentConfiguration.autoHeight = true;
              // 头部不固定情况下，则滚动区范围由底部决定
              if (footerConfiguration.visible) {
                // 底部可见情况下：
                if (footerConfiguration.stickyOnBottom) {
                  // 底部固定，则滚动区为 头部 + 内容区
                  this.contentHeaderMergeScroll = true;
                } else {
                  // 底部不固定，则滚动取为 头部 + 内容区 + 底部
                  this.contentHeaderFooterMergeScroll = true;
                }
              } else {
                // 底部不可见，则滚动区为 头部 + 内容区
                this.contentHeaderMergeScroll = true;
              }
            }
          } else {
            contentConfiguration.autoHeight = false;
            // 头部不可见情况下：
            if (footerConfiguration.visible) {
              // 底部可见情况下：
              if (!footerConfiguration.stickyOnBottom) {
                // 底部不固定，则滚动区为 内容区 + 底部
                this.contentFooterMergeScroll = true;
              }
              // 底部固定，则仅内容区滚动，通过 autoHeight = false;
            }
          }
        };
        if (layoutType == 'siderTopMiddleBottom') {
          if (siderConfiguration.visible) {
            mergeScroll.call(this);
            return;
          } else {
            layoutType = 'topMiddleBottom';
          }
        }

        if (layoutType == 'topMiddleSiderBottom' || layoutType == 'topMiddleRightSiderBottom') {
          if (siderConfiguration.visible) {
            // 内容区只能滚动（不管顶部、底部的固定情况）
            contentConfiguration.autoHeight = false;
            return;
          } else {
            layoutType = 'topMiddleBottom';
          }
        }

        if (layoutType == 'topMiddleBottom') {
          mergeScroll.call(this);
        }
      }
    },
    getLayoutContent() {
      return this.$refs.wContent;
    },

    closeActiveTab() {
      if (this.$refs.wContent) {
        this.$refs.wContent.closeActiveTab();
      }
    }
  },
  beforeMount() {
    let _this = this;
    this.pageContext.handleEvent(`widgetLayout:UpdateBreadcrumb:${this.widget.id}`, data => {
      _this.pageContext.emitEvent(`widgetLayoutContent:UpdateBreadcrumb:${this.widget.configuration.content.id}`, data);
    });
  },
  mounted() {
    if (!this.designMode) {
      if (this.height == undefined) {
        this.height = window.innerHeight;
        addWindowResizeHandler(() => {
          this.$nextTick(() => {
            this.height = window.innerHeight;
            this.adjustLayoutContentSiderHeight();
          });
        });
      }

      // this.height = 'calc(100vh)';
      this.adjustLayoutContentSiderHeight();
    }
  },
  watch: {
    THEME: {
      deep: true,
      handler() {
        // 主题变更会影响内容间距，重新调整布局高度
        this.adjustLayoutContentSiderHeight();
      }
    },
    'ENVIRONMENT.layoutConf': {
      deep: true,
      handler(v) {
        this.adjustLayoutByLayoutConfig(v);
        this.headerKey = this.widget.configuration.header.id + '_' + new Date().getTime();
        this.siderKey = this.widget.configuration.sider.id + '_' + new Date().getTime();
        this.layoutZoneFixed();
        if (!v.hasOwnProperty('menuCollapseBtnVisible')) {
          this.ENVIRONMENT.layoutConf.menuCollapseBtnVisible = layoutConf.menuCollapseBtnDisplayType !== 'hidden';
        }
        this.$nextTick(() => {
          this.adjustLayoutContentSiderHeight();
        });
      }
    },
    'widget.configuration.header.configuration.backgroundColorType': {
      deep: true,
      handler(v) {
        this.adjustLayoutByLayoutConfig();
      }
    },
    'widget.configuration.sider.configuration.backgroundColorType': {
      deep: true,
      handler(v) {
        this.adjustLayoutByLayoutConfig();
      }
    }
  }
};
</script>
