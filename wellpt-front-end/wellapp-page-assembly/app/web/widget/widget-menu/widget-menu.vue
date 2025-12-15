<template>
  <div :style="vMstyle" :class="['widget-menu', vMode, bgColorType]">
    <a-menu
      v-if="vMenuHeader"
      mode="inline"
      :theme="bgColorType == 'primary-color' ? 'light' : bgColorType"
      :class="['head-title-menu', bgColorType]"
      :selectable="false"
      v-model="selectedKeys"
    >
      <a-menu-item
        :key="this.widget.id + '_head'"
        :class="['head-title-menu-item', widget.configuration.headerContentType == 'logo' ? 'logo-image' : '']"
      >
        <template v-if="widget.configuration.headerContentType == 'iconTitle'">
          <Icon v-if="widget.configuration.menuTitleIcon" :type="widget.configuration.menuTitleIcon" />
          <span :class="widget.configuration.menuTitleIcon ? '' : 'no-icon'" v-if="widget.configuration.menuTitleVisible">
            {{ $t(widget.id + '_menuTitle', widget.configuration.menuTitle) }}
          </span>
        </template>
        <template v-else-if="widget.configuration.headerContentType == 'logo'">
          <div v-if="logo.type == 'html'" v-html="logo.value"></div>
          <img :src="logo.value" v-else-if="logo.type == 'image'" />
        </template>
      </a-menu-item>
    </a-menu>

    <template v-if="vMode == 'horizontal' && widget.configuration.subMenuExpandType == 'topDrawer'">
      <!-- 水平菜单, 并且下拉方式为展开所有一级菜单, 二级菜单以抽屉方式打开的情况下的效果 -->
      <a-dropdown
        :trigger="designMode ? [] : [menus.length ? 'click' : '']"
        :overlayClassName="'widget-menu-dropdown ddd' + widget.configuration.align + ' ' + bgColorType"
        v-model="dropdownVisible"
        :overlayStyle="{ zIndex: layoutType != undefined && layoutType.startsWith('top') ? 2 : 1 }"
        :align="{ offset: [0, 0] }"
      >
        <!-- zIndex 设值与头部导航所在的布局类型有关系-->
        <div class="header-menu" :style="{ textAlign: widget.configuration.align }" ref="headerMenuContainer">
          <!-- 此处仅渲一级菜单 -->
          <template v-for="(menu, i) in menus">
            <div
              :key="widget.id + '_root_menu_' + i"
              :class="[
                dropdownMenuHoverKey == menu.id ? 'hovered' : '',
                dropdownMenuSelectedKey == menu.id ? 'selected' : '',
                itemBadgeClass(menu)
              ]"
              @click="e => clickDropdownRootMenu(e, menu)"
              @mouseenter="mouseenterDropdownMenu(menu)"
              @mouseleave="mouseleaveHeaderMenu"
              :title="menu.title"
            >
              <Icon :type="menu.icon" />
              {{ menu.title }}
              <a-badge
                v-if="menu.badgeNum != undefined"
                :count="menu.badgeNum"
                :dot="widget.configuration.menuBadge.badgeDisplayType == 'dot'"
                :overflowCount="menu.overflowCount"
                :class="['menu-badge', widget.configuration.menuBadge.badgeDisplayType == 'text' ? 'just-label' : '']"
                :showZero="menu.showZero"
              />
            </div>
          </template>
          <!-- <a-menu
            :class="[bgColorType]"
            v-if="horizontalMoreMenus[0].menus.length > 0"
            mode="horizontal"
            @click="clickMenu"
            :selectedKeys="[]"
          >
            <template v-for="(mnu, i) in horizontalMoreMenus">
              <a-menu-item :key="mnu.id" :value="mnu" class="widget-menu-menu-item" v-if="mnu.menus == undefined || mnu.menus.length == 0">
                <Icon :type="mnu.icon || 'menu'" />
                <span>{{ translate(mnu.id, mnu.title) }}</span>
              </a-menu-item>
              <SubMenu v-else :menu-info="mnu" :key="mnu.id" :level="1" :dropdown-all="true" :widget="widget" />
            </template>
          </a-menu> -->
        </div>
        <div
          slot="overlay"
          class="widget-menu-dropdown-overlay-menus"
          ref="dropdownOverlay"
          v-if="hasDropdownItems"
          :style="{
            maxHeight: '600px',
            overflowY: 'auto',
            width: '100%',
            justifyContent:
              widget.configuration.align == 'left' ? 'flex-start' : widget.configuration.align == 'right' ? 'flex-end' : 'center'
          }"
        >
          <!-- 背景遮罩 -->
          <div class="cover" ref="dropdownCover" :style="{ height: coverHeight, maxHeight: '600px' }"></div>
          <!-- 每个二级菜单作为独立的菜单进行展示, 其中二级菜单会以抽屉方式打开 -->
          <template v-for="rootMenu in menus">
            <a-menu
              :key="rootMenu.id"
              mode="vertical"
              @click="clickMenu"
              :selectedKeys="[]"
              @mouseenter="mouseenterDropdownMenu(rootMenu)"
              @mouseleave="mouseleaveHeaderMenu"
              :class="dropdownMenuHoverKey == rootMenu.id && rootMenu.menus && rootMenu.menus.length > 0 ? 'hovered' : ''"
              ref="dropdownMenu"
              :disabled="true"
            >
              <template v-for="mnu in rootMenu.menus">
                <a-menu-item
                  v-if="mnu.menus == undefined || mnu.menus.length == 0"
                  :key="mnu.id"
                  :value="mnu"
                  :disabled="mnu.disabled"
                  :class="['widget-menu-menu-item', itemBadgeClass(mnu)]"
                >
                  <Icon :type="mnu.icon || 'menu'" />
                  <span :title="mnu.title">{{ translate(mnu.id, mnu.title) }}</span>
                  <a-badge
                    v-if="mnu.badgeNum != undefined"
                    :count="mnu.badgeNum"
                    :dot="widget.configuration.menuBadge.badgeDisplayType == 'dot'"
                    :overflowCount="mnu.overflowCount"
                    :class="['menu-badge', widget.configuration.menuBadge.badgeDisplayType == 'text' ? 'just-label' : '']"
                    :showZero="mnu.showZero"
                  />
                </a-menu-item>
                <SubMenu v-else :menu-info="mnu" :key="mnu.id" :level="1" :dropdown-all="true" :widget="widget" />
              </template>
            </a-menu>
          </template>
        </div>
      </a-dropdown>
    </template>
    <template v-else>
      <div class="spin-center" v-if="waitLoadingAppSubMenus">
        <a-spin />
      </div>
      <a-menu
        v-else
        :mode="vMode"
        :theme="bgColorType == 'primary-color' ? 'light' : bgColorType"
        :class="[bgColorType, vMode == 'horizontal' ? widget.configuration.align : undefined]"
        @click="clickMenu"
        :openKeys="openKeys"
        v-model="selectedKeys"
        @openChange="onOpenChange"
        ref="menu"
      >
        <template v-for="mnu in menus">
          <a-menu-item
            :disabled="mnu.disabled"
            v-if="mnu.menus == undefined || mnu.menus.length == 0"
            :key="mnu.id"
            :value="mnu"
            :class="itemBadgeClass(mnu)"
          >
            <a-skeleton v-if="!designMode && mnu.dynamic" active :rows="0" :title="{ width: '100%' }" />
            <template v-else>
              <Icon v-if="!mnu.iconHidden" :type="mnu.icon || 'menu'" class="menu-left-icon" />
              <span :title="mnu.title">{{ translate(mnu.id, mnu.title) }}</span>

              <a-icon
                type="loading"
                v-if="!designMode && mnu.appId != undefined && widget.configuration.mode == 'inline' && !mnu.fetched"
              />
              <a-badge
                v-if="mnu.badgeNum != undefined"
                :count="mnu.badgeNum"
                :dot="widget.configuration.menuBadge.badgeDisplayType == 'dot'"
                :overflowCount="mnu.overflowCount"
                :class="['menu-badge', widget.configuration.menuBadge.badgeDisplayType == 'text' ? 'just-label' : '']"
                :showZero="mnu.showZero"
              />
            </template>
          </a-menu-item>
          <SubMenu
            v-else
            :menu-info="mnu"
            :key="mnu.id"
            :level="1"
            :widget="widget"
            :designMode="designMode"
            :siderCollapsed="siderCollapsed"
            :mode="vMode"
          />
        </template>
      </a-menu>
    </template>

    <div :class="['menu-sys-bar', bgColorType]" v-if="vMode == 'horizontal' && widget.configuration.enableSysMenu" ref="menuSysBar">
      <template v-for="mnu in sysMenus">
        <!-- 带编码的则解析为组件渲染 -->
        <component :is="mnu.code" v-if="mnu.code" :key="'sysMnu_' + mnu.id" :menu="mnu">
          <template slot="default">
            <span :title="mnu.title">
              <div :class="['sys-menu-title-icon', itemBadgeClass(mnu)]">
                <Icon :type="mnu.icon" v-if="!mnu.iconHidden" />
                <label v-show="!mnu.titleHidden || mnu.icon == undefined" :title="mnu.title">
                  {{ mnu.title }}
                </label>
                <a-badge
                  v-if="mnu.badgeNum != undefined"
                  :count="mnu.badgeNum"
                  :dot="widget.configuration.sysMenuBadge.badgeDisplayType == 'dot'"
                  :overflowCount="mnu.overflowCount"
                  :class="['menu-badge', widget.configuration.sysMenuBadge.badgeDisplayType == 'text' ? 'just-label' : '']"
                  :showZero="mnu.showZero"
                ></a-badge>
              </div>
            </span>
          </template>
        </component>

        <span :title="mnu.title" :key="mnu.id" v-else-if="!mnu.code && mnu.menus.length == 0" @click.stop="e => clickMenu(e, mnu)">
          <div :class="['sys-menu-title-icon', itemBadgeClass(mnu)]">
            <Icon :type="mnu.icon" v-if="!mnu.iconHidden" />
            <label v-show="!mnu.titleHidden || mnu.icon == undefined" :title="mnu.title">
              {{ mnu.title }}
            </label>
            <a-badge
              v-if="mnu.badgeNum != undefined"
              :count="mnu.badgeNum"
              :dot="widget.configuration.sysMenuBadge.badgeDisplayType == 'dot'"
              :overflowCount="mnu.overflowCount"
              :class="['menu-badge', widget.configuration.sysMenuBadge.badgeDisplayType == 'text' ? 'just-label' : '']"
              :showZero="mnu.showZero"
            ></a-badge>
          </div>
        </span>
        <template v-else-if="mnu.menus.length > 0">
          <a-dropdown :key="'sysMnu_' + mnu.id" :trigger="['click']" :overlayClassName="bgColorType">
            <span :title="mnu.title">
              <div :class="['sys-menu-title-icon', itemBadgeClass(mnu)]">
                <Icon :type="mnu.icon" v-if="!mnu.iconHidden" />
                <label v-show="!mnu.titleHidden || mnu.icon == undefined" :title="mnu.title">
                  {{ mnu.title }}
                </label>
                <a-badge
                  v-if="mnu.badgeNum != undefined"
                  :count="mnu.badgeNum"
                  :dot="widget.configuration.sysMenuBadge.badgeDisplayType == 'dot'"
                  :overflowCount="mnu.overflowCount"
                  :class="['menu-badge', widget.configuration.sysMenuBadge.badgeDisplayType == 'text' ? 'just-label' : '']"
                  :showZero="mnu.showZero"
                ></a-badge>
              </div>
            </span>
            <a-menu slot="overlay">
              <a-menu-item v-for="subMenu in mnu.menus" :key="subMenu.code" :title="subMenu.title" @click="e => clickMenu(e, subMenu)">
                <span :title="subMenu.title">
                  <Icon :type="subMenu.icon" v-if="!subMenu.iconHidden" />
                  {{ subMenu.title }}
                </span>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </template>
      </template>
    </div>
  </div>
</template>
<style lang="less"></style>

<script type="text/babel">
import './css/index.less';
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { Menu } from 'ant-design-vue';
import { generateId, deepClone } from '@framework/vue/utils/util';
import DataSourceBase from '../../assets/js/commons/dataSource.base';
import { set, camelCase } from 'lodash';

const SubMenu = {
  template: `
      <a-sub-menu :key="menuInfo.id" v-bind="$props" v-on="$listeners" :popupClassName="dropdownAll?'dropdown-all widget-menu-dropdown':'widget-menu-dropdown'"  :class="[itemBadgeClass(menuInfo)]" >
        <template slot="title">
          <Icon  :type="level==1?(menuInfo.icon||'menu'):menuInfo.icon" v-if="(level ==1 || menuInfo.icon ) && !menuInfo.iconHidden" class="menu-left-icon"/>
          <span :title="menuInfo.title"> {{ menuInfo.title }}</span>
            <a-badge v-if="menuInfo.badgeNum != undefined" :overflowCount="menuInfo.overflowCount" :count="menuInfo.badgeNum" :dot="widget.configuration.menuBadge.badgeDisplayType == 'dot'"
            :class="['menu-badge', widget.configuration.menuBadge.badgeDisplayType == 'text' ? 'just-label' : '']"
            :showZero="menuInfo.showZero"
            />
         </template>
         <template v-for="item in menuInfo.menus">
          <a-menu-item v-if="item.menus==undefined || item.menus.length ==0" :key="item.id" :value="item" :href="item.id" :class="[itemBadgeClass(item)]" >
              <Icon :type="item.icon" v-if="item.icon && !item.iconHidden" class="menu-left-icon"/>
              <span :title="item.title">{{ item.title }}</span>
              <a-icon type="loading" v-if="!designMode  && item.appId != undefined && widget.configuration.mode=='inline'" />
              <a-badge v-if="item.badgeNum != undefined" :count="item.badgeNum" :overflowCount="item.overflowCount" :dot="widget.configuration.menuBadge.badgeDisplayType == 'dot'"
              :class="['menu-badge', widget.configuration.menuBadge.badgeDisplayType == 'text' ? 'just-label' : '']"
              :showZero="item.showZero"
              />
          </a-menu-item>
          <sub-menu v-else :key="item.id" :menu-info="item" :dropdown-all="dropdownAll" :widget="widget" :designMode="designMode"/>
        </template>
      </a-sub-menu>
    `,
  name: 'SubMenu',
  isSubMenu: true,
  props: {
    ...Menu.SubMenu.props,
    level: Number,
    dropdownAll: Boolean,
    widget: Object,
    designMode: Boolean,
    siderCollapsed: Boolean,
    mode: String,
    menuInfo: {
      type: Object,
      default: () => ({})
    }
  },
  methods: {
    // 设置导航是否有徽标和属于那种徽标
    itemBadgeClass(menu) {
      if (menu.badgeNum || menu.showZero) {
        if (this.widget.configuration.menuBadge.badgeDisplayType == 'dot') {
          return 'has-dot-badge';
        } else if (this.widget.configuration.menuBadge.badgeDisplayType == 'text') {
          return 'has-just-label-badge';
        } else {
          return 'has-count-badge';
        }
      }
      return '';
    }
  }
};

import HeaderLogout from './component/header-logout.vue';
import HeaderMail from './component/header-mail.vue';
import HeaderModifyPassword from './component/header-modify-password.vue';
import HeaderOnlineMsg from './component/header-online-msg.vue';
import HeaderPersonality from './component/header-personality.vue';
import HeaderThemeSet from './component/header-theme-set.vue';
import HeaderUserAvatar from './component/header-user-avatar.vue';
import HeaderUserCenter from './component/header-user-center.vue';
import HeaderSwitchWorkbenches from './component/header-switch-workbenches.vue';
import HeaderOnlineUserList from './component/header-online-user-list.vue';
import HeaderFullSearch from './component/header-full-search.vue';
import WidgetMenuConfiguration from './configuration/index.vue';

export default {
  name: 'WidgetMenu',
  mixins: [widgetMixin],
  props: { defaultOpenKeys: Array },
  components: {
    SubMenu,
    HeaderLogout,
    HeaderMail,
    HeaderModifyPassword,
    HeaderOnlineMsg,
    HeaderPersonality,
    HeaderThemeSet,
    HeaderUserAvatar,
    HeaderUserCenter,
    HeaderSwitchWorkbenches,
    HeaderOnlineUserList,
    HeaderFullSearch
  },
  inject: [
    'backgroundColorType', // 有侧边传入的背景颜色类：深色、浅色、主题色将决定菜单的样式
    'layoutType',
    'unauthorizedResource',
    'layoutSiderContext', // ant design 提供的 sider 组件实例
    'layoutFixed',
    'fullSearchDefinition',
    'ENVIRONMENT'
  ],
  data() {
    let defaultSelectedKeys = [];
    if (this.widget.configuration.defaultSelectedKey) {
      defaultSelectedKeys = [this.widget.configuration.defaultSelectedKey];
    }
    return {
      badgeDisplayType: this.widget.configuration.menuBadge.badgeDisplayType,
      badgeOverflowCount: this.widget.configuration.menuBadge.badgeOverflowCount,
      badgeOverflowCountShowType: this.widget.configuration.menuBadge.badgeOverflowCountShowType,
      badgeShowZero: this.widget.configuration.menuBadge.badgeShowZero,
      appMenusToSider: this.widget.configuration.appMenusToSider, // 模块的菜单要渲染在侧边栏内
      openKeys: [],
      selectedKeys: defaultSelectedKeys,
      // enableCollapsed:this.widget.configuration.enableCollapsed,
      inlineCollapsed: false,
      rootMenuKeys: [],
      dropdownMenuHoverKey: undefined,
      dropdownMenuSelectedKey: undefined,
      logo: {
        value: undefined,
        type: 'image'
      },
      menuSelfAdjusting: false, // 横向菜单自适应中
      waitLoadingAppSubMenus: false,
      menus: [],
      menuIdMap: {},
      sysMenus: [],
      dropdownVisible: false,
      horizontalMoreMenus: [
        { id: 'more_' + this.widget.id, title: this.$t('WidgetLayout.more', '更多'), icon: 'more', level: 1, eventHandler: {}, menus: [] }
      ],
      coverHeight: '100%',
      overlayWidth: 'auto',
      loadingAppMenus: false
      // bgColorType: this.backgroundColorType || this.widget.configuration.backgroundColorType || 'light'
    };
  },
  computed: {
    siderCollapsed() {
      if (this.layoutSiderContext != undefined) {
        return this.layoutSiderContext.collapsed;
      }
      return false;
    },
    bgColorType() {
      let parentBgColorType = this.backgroundColorType;
      if (typeof this.backgroundColorType == 'function') {
        parentBgColorType = this.backgroundColorType();
      }
      return parentBgColorType || this.widget.configuration.backgroundColorType || 'light';
    },
    vMenuHeader() {
      if (
        this.widget.configuration.headerContentType == 'iconTitle' &&
        this.widget.configuration.menuTitleVisible === false &&
        !this.widget.configuration.menuTitleIcon
      ) {
        return false;
      }
      if (
        this.widget.configuration.headerContentType == 'logo' &&
        ((!this.widget.configuration.logoUseInput && !this.widget.configuration.logo) ||
          (this.widget.configuration.logoUseInput && !this.widget.configuration.logoInputValue))
      ) {
        return false;
      }
      return this.widget.configuration.menuHeader;
    },
    vHeaderStyle() {
      let style = {};
      return style;
    },
    vMode() {
      let mode = this.widget.configuration.mode,
        subMenuExpandType = this.widget.configuration.subMenuExpandType;
      if (mode == 'inline' && (this.siderCollapsed || subMenuExpandType == 'popover')) {
        return 'vertical'; // 内嵌模式情况下，如果子菜单的打开方式为浮层，则变更为 vertical 模式渲染菜单
      }

      return mode;
    },

    hasDropdownItems() {
      // 判断是否有下拉菜单存在:
      if (this.appMenusToSider != undefined && this.vMode == 'horizontal') {
        // 判断是否有二级菜单
        for (let m of this.menus) {
          if (m.appId == undefined && m.menus && m.menus.length > 0) {
            return true;
          }
        }
        return false;
      }
      return true;
    },
    vMstyle() {
      let style = {
        opacity: this.menuSelfAdjusting ? 0 : 1
      };
      if (this.vMode == 'horizontal') {
        style.display = 'flex';
      }
      return style;
    },
    menuTreeMap() {
      let map = {};
      let cascade = (list, pid) => {
        if (list) {
          for (let i = 0, len = list.length; i < len; i++) {
            map[list[i].id] = {
              id: list[i].id,
              title: list[i].title,
              icon: list[i].icon,
              pid
            };
            cascade(list[i].menus, list[i].id);
          }
        }
      };
      cascade(this.menus, null);
      return map;
    }
  },
  beforeCreate() {},
  created() {
    if (EASY_ENV_IS_NODE) {
      let _state = this.getWidgetStateFromUrl(this.$requestServContext.url, this.widget.id);
      if (_state) {
        let { menuid, selected } = _state,
          _this = this;
        if (menuid) {
          menuid = Array.isArray(menuid) ? menuid : [menuid];
          menuid.forEach(p => {
            if (_this.menuIdMap[p]) {
              if (_this.menuIdMap[p].hasOwnProperty('eventHandler')) {
                let eventHandler = JSON.parse(JSON.stringify(_this.menuIdMap[p].eventHandler));
                if (eventHandler.title == undefined) {
                  eventHandler.title = _this.menuIdMap[p].title;
                }
                eventHandler.key = _this.menuIdMap[p].id;
                _this.dispatchEventHandler(eventHandler);
              }
            }
          });
        }
        if (selected) {
          _this.selectedKeys = [selected];
        }
      }
    } else {
      this.badgeCountQueue = [];
      this.initMenus();
      this.initHeader();
    }
    this.menuSelfAdjusting = this.vMode == 'horizontal'; // 水平态会自适应渲染菜单可见与“更多”菜单配置，因此要弄个加载中, 避免页面元素渲染抖动
  },
  beforeMount() {
    if (this.appMenusToSider == undefined && this.hasAppMenus) {
      this.waitLoadingAppSubMenus = true;
      this.asyncLoadSubMenus(this.menus).then(results => {
        for (let i = 0; i < this.menus.length; i++) {
          if (this.menus[i].noAuthorizedSubMenus) {
            this.menus.splice(i--, 1);
          }
        }
        this.originalMenus = deepClone(this.menus);
        this.waitLoadingAppSubMenus = false;
        this.$nextTick(() => {
          this.adjustHeaderMenuContainer();
          if (this.selectedKeys) {
            if (this.widget.configuration.stopFireSelectedEvent !== true) {
              this.fireSelectedMenusByKeys(this.selectedKeys);
            }
          }
        });
        this.createAsyncMenuDevelopJsInstances(results);
      });
    }
  },

  mounted() {
    this.menuBadgeCount();
    this.$nextTick(() => {
      if (this.selectedKeys && !this.waitLoadingAppSubMenus) {
        if (this.widget.configuration.stopFireSelectedEvent !== true) {
          this.fireSelectedMenusByKeys(this.selectedKeys);
        }
      }
      if (!this.waitLoadingAppSubMenus) {
        this.adjustHeaderMenuContainer();
      }
    });
    this.pageContext.handleEvent(`WidgetMenu:SelectKey:${this.widget.id}`, ({ selectedKey, fireSelectedEvent = false }) => {
      this.selectedKeys.splice(0, this.selectedKeys.length);
      this.selectedKeys.push(selectedKey);
      if (fireSelectedEvent) {
        this.fireSelectedMenusByKeys(this.selectedKeys);
      }
    });
    this.pageContext.handleEvent(`WidgetMenu:refreshMenuBadgeNumberByCode`, arg => {
      this.menuBadgeCount(arg);
    });
    this.listenOpenModuleMenuAndLocatePageMenu();
  },
  methods: {
    initHeader() {
      if (this.widget.configuration.menuHeader && this.widget.configuration.headerContentType == 'logo') {
        let logoUseInput = this.widget.configuration.logoUseInput;
        if (logoUseInput) {
          let logoValue = this.widget.configuration.logoInputValue;
          let isUrl =
            logoValue.startsWith('data:') ||
            logoValue.startsWith('http') ||
            logoValue.startsWith('/') ||
            logoValue.startsWith('../') ||
            logoValue.startsWith('./');
          if (isUrl) {
            this.logo.type = 'image';
            logoValue = `url("${logoValue}")`;
          } else {
            this.logo.type = 'html';
          }
          this.logo.value = logoValue;
        } else {
          this.logo.type = 'image';
          this.logo.value = this.widget.configuration.logo;
        }
      }

      this.sysMenus = this.cascadeMenus(this.widget.configuration.sysFunctionMenus, this.widget.configuration.sysMenuBadge);
      if (!this.designMode && this.layoutFixed && this.fullSearchDefinition) {
        if (this.fullSearchDefinition.enabled) {
          const hasItem = this.sysMenus.find(item => item.code === 'HeaderFullSearch');
          if (!hasItem) {
            this.sysMenus.splice(0, 0, {
              id: `menu-${generateId()}`,
              title: '全文检索',
              titleHidden: false,
              code: 'HeaderFullSearch',
              level: 1,
              icon: 'icon-ptkj-sousuochaxun',
              hidden: false,
              eventHandler: {}
            });
          }
        }
      }
      if (this.ENVIRONMENT && !this.ENVIRONMENT.userThemeDefinable && !this.ENVIRONMENT.userLayoutDefinable) {
        const themeIndex = this.sysMenus.findIndex(item => item.code === 'HeaderThemeSet');
        this.sysMenus.splice(themeIndex, 1);
      }
    },
    cascadeMenus(menuList, menuBadge) {
      let menus = [];
      let cascade = (list, scopes) => {
        if (list && list.length > 0) {
          for (let m of list) {
            if (this.unauthorizedResource && this.unauthorizedResource.includes(m.id)) {
              console.warn('菜单: %s 未授权', m.title);
              continue;
            }
            m.title = this.$t(m.id, m.title);
            this.menuIdMap[m.id] = m;
            if (m.hidden === true) {
              continue;
            }
            if (m.dynamic && m.dynamicDataSourceType && (m.dataSourceId || m.dataModelUuid) && m.menuTitleField && m.menuKeyField) {
              if (!this.designMode) {
                this.buildDynamicSourceMenus(m, scopes);
              }
              scopes.push(m);
              continue;
            }

            if (m.badge && m.badge.enable) {
              // 显示徽标
              m.overflowCount = this.getBadgeOverflowCount(menuBadge);
              this.badgeCountQueue.push(m);
              m.showZero = menuBadge.badgeShowZero == 'yes';
            }

            if (m.menus && m.menus.length > 0) {
              // 子菜单
              let _subMenus = [];
              cascade.call(this, m.menus, _subMenus);
              if (_subMenus.length == 0 && m.code != 'HeaderUserAvatar') {
                // 用户头像允许隐藏其下的子菜单，仅显示头像菜单
                // 子菜单因为无一权限，则父级菜单也不展示了
                continue;
              }
              m.menus = _subMenus;
            }
            scopes.push(m);
          }
        }
      };
      cascade.call(this, deepClone(menuList), menus, undefined);
      return menus;
    },
    getBadgeOverflowCount(badgeStyle = {}) {
      if (badgeStyle.badgeOverflowCountShowType == 'customize') {
        return badgeStyle.badgeOverflowCount;
      } else if (badgeStyle.badgeOverflowCountShowType == 'limitless') {
        return Infinity;
      }
      return undefined;
    },
    buildDynamicSourceMenus(menu, menuList) {
      new Promise((resolve, reject) => {
        if (menu.dynamicDataSourceType == 'dataModel') {
          this.getDataSourceProvider(
            {},
            `/proxy/api/dm/loadData/${menu.dataModelUuid}`,
            `/proxy/api/dm/loadDataCount/${menu.dataModelUuid}`
          )
            .load()
            .then(data => {
              console.log('查询数据返回', data);
              resolve(data.data);
            });
        } else if (menu.dynamicDataSourceType == 'dataSource') {
          this.getDataSourceProvider({
            dataSourceId: menu.dataSourceId
          })
            .load()
            .then(data => {
              console.log('查询数据返回', data);
              resolve(data.data);
            });
        }
      }).then(list => {
        let { menuTitleField, menuKeyField, menuIconField, menuParentKeyField } = menu,
          realMenus = [],
          menuMap = {};
        if (list.length) {
          for (let item of list) {
            let m = {
              icon: menuIconField ? item[menuIconField] || menu.icon : menu.icon,
              eventHandler: deepClone(menu.eventHandler),
              iconHidden: menu.iconHidden,
              id: item[menuKeyField],
              data: item,
              parentId: item[menuParentKeyField],
              title: item[menuTitleField]
            };
            menuMap[item[menuKeyField]] = m;
            if (menuParentKeyField == undefined || item[menuParentKeyField] == undefined) {
              realMenus.push(m);
            }
          }

          if (menuParentKeyField != undefined) {
            // 构造树形结构
            for (let key in menuMap) {
              let m = menuMap[key];
              if (m.parentId) {
                delete m.parentId;
                if (menuMap[m.parentId].menus == undefined) {
                  menuMap[m.parentId].menus = [];
                }
                menuMap[m.parentId].menus.push(m);
              }
            }
          }

          // 替换掉动态导航节点
          for (let i = 0, len = menuList.length; i < len; i++) {
            if (menuList[i].id == menu.id) {
              menuList.splice(i, 1, ...realMenus);
              break;
            }
          }
        }
      });
    },

    getDataSourceProvider(options, loadDataUrl, loadDataCntUrl) {
      let option = {
        dataStoreId: options.dataSourceId,
        onDataChange: function (data, count, params) {},
        receiver: this,
        autoCount: false,
        pageSize: undefined,
        defaultCriterions: options.defaultCondition //默认条件
          ? [
              {
                sql: options.defaultCondition
              }
            ]
          : []
      };
      if (loadDataUrl != undefined) {
        option.loadDataUrl = loadDataUrl;
      }
      if (loadDataCntUrl != undefined) {
        option.loadDataCntUrl = loadDataCntUrl;
      }
      return new DataSourceBase(option);
    },
    initMenus() {
      this.menus = this.cascadeMenus(this.widget.configuration.menus, this.widget.configuration.menuBadge);
      this.originalMenus = deepClone(this.menus);
      let firstSubMenuIdx = undefined,
        menuKeys = [];
      let cascadeMenuKeys = (list, keys, level) => {
        if (list) {
          for (let i = 0, len = list.length; i < len; i++) {
            menuKeys.push(list[i].id);
            if (level == 1) {
              if (list[i].appId != undefined) {
                this.hasAppMenus = true;
                list[i].fetched = false;
              }
              this.rootMenuKeys.push(list[i].id);
              if (firstSubMenuIdx == undefined && list[i].menus && list[i].menus.length > 0) {
                firstSubMenuIdx = i;
              }
            }
            if (list[i].menus && list[i].menus.length) {
              cascadeMenuKeys.call(this, list[i].menus, keys, level + 1);
            }
          }
        }
      };
      cascadeMenuKeys.call(this, this.menus, menuKeys, 1);

      if (this.widget.configuration.mode == 'inline' && this.widget.configuration.subMenuExpandType == 'dropdown') {
        if (this.defaultOpenKeys != undefined && this.defaultOpenKeys.length > 0) {
          this.openKeys.push(...this.defaultOpenKeys);
        } else if (this.widget.configuration.mode == 'inline') {
          if (this.widget.configuration.defaultExpandType === 'defaultExpandFirst') {
            if (firstSubMenuIdx != undefined) {
              this.openKeys.push(this.menus[firstSubMenuIdx].id);
            }
          } else if (this.widget.configuration.defaultExpandType === 'defaultExpandAll') {
            this.openKeys.push(...menuKeys);
          } else if (this.widget.configuration.defaultExpandType === 'defaultExpandAllLevelOne') {
            this.openKeys.push(...this.rootMenuKeys);
          } else if (this.widget.configuration.defaultExpandType == 'defaultByDefaultSelected') {
            this.openKeys.push(...this.getOpenKeysOfDefaultSelected());
          }
        }
      }
    },
    mouseleaveHeaderMenu() {
      this.dropdownMenuHoverKey = undefined;
    },
    clickDropdownRootMenu(e, menu) {
      if (!this.designMode) {
        this.dropdownMenuHoverKey = menu.id;
        this.dropdownMenuSelectedKey = menu.id;
        if (menu.index || (menu.menus.length == 0 && menu.eventHandler && menu.eventHandler.actionType)) {
          this.clickMenu(null, menu);
          this.dropdownVisible = false;
          e.stopPropagation();
          return;
        }
        if (this.appMenusToSider != undefined && menu.appId != undefined) {
          // 模块导航菜单显示在布局侧边内，需要构造菜单组件并推送给布局左侧执行
          this.renderAppMenusToSider(menu);
          this.dropdownVisible = false;
          e.stopPropagation();
          return;
        }

        // if (this.hasDropdownItems) {
        //   // 存在下拉菜单项的时候，遮罩的高度同二级菜单项容器高度
        //   let _this = this;
        //   setTimeout(() => {
        //     _this.$refs.dropdownCover.style.height = _this.$refs.dropdownCover.parentElement.clientHeight + 'px';
        //   }, 10);
        // }

        if (!this.dropdownVisible) {
          this.dropdownVisible = true;
        }
        // 点击一级菜单时候，如果是已经下拉显示情况下，事件阻止冒泡，避免dropdown组件触发点击事件从而导致下拉被收起
        if (this.dropdownVisible) {
          e.stopPropagation();
        }
      }
    },
    renderAppMenusToSider(menu, beforeRender) {
      if (this.appMenusToSider != undefined && menu.appId != undefined) {
        // 模块导航，加载模块首页
        let dispatchWidgetMenuToLayoutSider = (menus = [], defaultExpandType, defaultSelectedKey, jsModules) => {
          let wgt = {
            id: `widgetMenu_${menu.appId}`,
            wtype: 'WidgetMenu',
            configuration: WidgetMenuConfiguration.configuration()
          };
          wgt.configuration.menus = menus;
          wgt.configuration.defaultExpandType = defaultExpandType;
          wgt.configuration.defaultSelectedKey = defaultSelectedKey;
          wgt.configuration.jsModules = jsModules;
          if (typeof beforeRender == 'function') {
            beforeRender(wgt);
          }
          let num = this.updateMenuBreadcrumb(menu);
          wgt.configuration.breadcrumbIndex = num + 1;
          wgt.configuration.breadcrumbVisible = this.widget.configuration.breadcrumbVisible;
          wgt.configuration.menuBadge = this.widget.configuration.menuBadge;
          if (menus.length > 0 && menus[0].index) {
            // 默认选中首页导航
            // 渲染首页导航内容
            this.pageContext.emitEvent(`WidgetLayoutContent:Update:${menu.eventHandler.containerWid}`, {
              title: menus[0].title,
              key: menus[0].id,
              widgets: menus[0].eventHandler.widgets
            });
          } else {
            // 没有首页内容，则重置容器
            this.pageContext.emitEvent(`WidgetLayoutContent:Update:${menu.eventHandler.containerWid}`, {
              widgets: []
            });
          }
          this.pageContext.emitEvent(`WidgetLayoutSider:Update:${this.appMenusToSider}`, { widgets: [wgt] });
        };
        if (!menu.fetched) {
          let parent = JSON.parse(JSON.stringify(menu));
          parent.menus = [];
          menu.fetched = true;
          this.fetchAppSubMenus(menu.appId, parent).then(({ menus, defaultExpandType, defaultSelectedKey, jsModules }) => {
            dispatchWidgetMenuToLayoutSider.call(this, menus, defaultExpandType, defaultSelectedKey, jsModules);
          });
        } else {
          dispatchWidgetMenuToLayoutSider.call(
            this,
            this.cacheAppSubMenuData[menu.appId].menus,
            this.cacheAppSubMenuData[menu.appId].defaultExpandType,
            this.cacheAppSubMenuData[menu.appId].defaultSelectedKey,
            this.cacheAppSubMenuData[menu.appId].jsModules
          );
        }
      }
    },
    mouseenterDropdownMenu(menu) {
      this.dropdownMenuHoverKey = menu.id;
    },
    getPopupContainer() {
      return this.$el;
    },
    getMenuById(id, menus) {
      let targetMenus = menus || this.widget.configuration.menus;
      for (let i = 0, len = targetMenus.length; i < len; i++) {
        let m = targetMenus[i];
        if (m.id == id) {
          return m;
        } else if (m.menus && m.menus.length) {
          let subMenu = this.getMenuById(id, m.menus);
          if (subMenu) {
            return subMenu;
          }
        }
      }
    },
    onOpenChange(openKeys) {
      if (this.widget.configuration.accordion) {
        const latestOpenKey = openKeys.find(key => this.openKeys.indexOf(key) === -1);
        if (this.rootMenuKeys.indexOf(latestOpenKey) === -1) {
          this.openKeys = openKeys;
        } else {
          this.openKeys = latestOpenKey ? [latestOpenKey] : [openKeys[openKeys.length - 1]];
        }
      } else {
        this.openKeys = openKeys;
      }
      if (this.appMenusToSider == undefined) {
        this.asyncLoadSubMenusByKeys(openKeys);
      }
    },
    getWidgetDefByAppId(appId, wtype, main = false) {
      return new Promise((resolve, reject) => {
        this.$tempStorage.getCache(
          `getWidgetsByAppId:${appId}`,
          () => {
            return new Promise((resolve, reject) => {
              $axios
                .get(`/proxy/api/app/widget/getAuthorizedWidgetsByAppId`, {
                  params: { appId, wtype: Array.isArray(wtype) ? wtype.join(',') : wtype, main }
                })
                .then(({ data }) => {
                  if (data.code == 0 && data.data) {
                    if (data.data.length) {
                      for (let d of data.data) {
                        if (d.i18ns && d.i18ns.length > 0) {
                          let message = { Widget: {} };
                          for (let i of d.i18ns) {
                            set(message.Widget, i.elementPid + '.' + i.code, i[camelCase(this.$i18n.locale)]);
                          }
                          this.$i18n.mergeLocaleMessage(this.$i18n.locale, message);
                        }
                      }

                      resolve(data.data);
                    } else {
                      resolve([]);
                    }
                  }
                })
                .catch(error => {
                  resolve([]);
                });
            });
          },
          list => {
            resolve(list);
          }
        );
      });
    },

    fetchAppSubMenus(appId, parent) {
      return new Promise((resolve, reject) => {
        // TODO: 缓存
        // 获取模块主页的主导航、主布局
        // 主导航的导航项作为当前模块导航的子集导航进行渲染
        // 主布局内容区的组件内容作为模块导航的点击效果进行渲染到目标容器内
        this.getWidgetDefByAppId(appId, ['WidgetLayout', 'WidgetMenu'], true).then(wgts => {
          let widgetMenu = null,
            widgetLayout = null;
          for (let i = 0, len = wgts.length; i < len; i++) {
            if (wgts[i].wtype == 'WidgetMenu') {
              widgetMenu = wgts[i];
            } else if (wgts[i].wtype == 'WidgetLayout') {
              widgetLayout = wgts[i];
            }
          }
          if (widgetMenu != null && widgetLayout != null) {
            let unauthorizedResource = widgetMenu.unauthorizedResource;
            widgetMenu = JSON.parse(widgetMenu.definitionJson);
            widgetLayout = JSON.parse(widgetLayout.definitionJson);
            let menus = widgetMenu.configuration.menus,
              widgetLayoutContentWidgets = widgetLayout.configuration.content.configuration.widgets;
            // 判断未授权的资源
            if (unauthorizedResource != undefined && unauthorizedResource.length) {
              let _menus = [];
              let cascadeMenus = (list, scopes) => {
                if (list && list.length > 0) {
                  for (let j = 0; j < list.length; j++) {
                    let m = list[j];
                    if (unauthorizedResource.includes(m.id)) {
                      list.splice(j--, 1);
                      continue;
                    }

                    if (m.menus && m.menus.length > 0) {
                      // 子菜单
                      let _subMenus = [];
                      cascadeMenus(m.menus, _subMenus);
                      if (_subMenus.length == 0) {
                        // 子菜单因为无一权限，则父级菜单也不展示了
                        list.splice(j--, 1);
                        continue;
                      }
                      m.menus = _subMenus;
                    }
                    scopes.push(m);
                  }
                }
              };
              cascadeMenus(menus, _menus);
              widgetMenu.configuration.menus = menus = _menus;
              if (_menus.length == 0) {
                parent.noAuthorizedSubMenus = true;
              }
            }
            if (this.cacheAppSubMenuData == undefined) {
              this.cacheAppSubMenuData = {};
            }
            this.cacheAppSubMenuData[appId] = {
              menus,
              defaultExpandType: widgetMenu.configuration.defaultExpandType,
              defaultSelectedKey: widgetMenu.configuration.defaultSelectedKey,
              jsModules: widgetMenu.configuration.jsModules
            };
            // 修改模块子导航的目标容器为当前模块导航配置的目标容器
            if (menus && menus.length > 0) {
              let cascadeSet = list => {
                if (list) {
                  for (let i = 0; i < list.length; i++) {
                    let l = list[i];
                    if (l.hidden) {
                      // 被隐藏的导航项不引入
                      list.splice(i--, 1);
                      continue;
                    }
                    if (l.index) {
                      if (widgetLayoutContentWidgets.length > 0) {
                        // 如果模块主页内容区存在组件内容，则把组件内容的渲染功能赋予模块主页导航
                        l.eventHandler.actionType = 'widgetRender';
                        l.eventHandler.widgets = widgetLayoutContentWidgets;
                        l.eventHandler.containerWid = parent.eventHandler.containerWid;
                        l.eventHandler.targetPosition = parent.eventHandler.targetPosition;
                      } else {
                        // 模块布局内没有内容，则把首页导航去掉
                        list.splice(i--, 1);
                        continue;
                      }
                    }
                    if (l.menus && l.menus.length == 0) {
                      // 导航打开位置为当前布局时，需设置布局组件，其他情况按照配置打开
                      if (!l.eventHandler.targetPosition || l.eventHandler.targetPosition == parent.eventHandler.targetPosition) {
                        l.eventHandler.targetPosition = parent.eventHandler.targetPosition;
                        l.eventHandler.containerWid = parent.eventHandler.containerWid;
                      }
                    } else {
                      cascadeSet(l.menus);
                      if (l.menus.length == 0) {
                        list.splice(i--, 1);
                      }
                    }
                  }
                }
              };
              cascadeSet(menus);
              if (menus.length == 0) {
                parent.noAuthorizedSubMenus = true;
              }
              parent.menus.push(...menus);

              resolve({
                menus,
                defaultExpandType: widgetMenu.configuration.defaultExpandType,
                defaultSelectedKey: widgetMenu.configuration.defaultSelectedKey,
                jsModules: widgetMenu.configuration.jsModules
              });
            } else {
              resolve({});
            }
          } else {
            resolve({});
          }
          this.$set(parent, 'fetched', true);
        });
      });
    },

    clickMenu($evt, menu) {
      if (!this.designMode) {
        let mnu = menu || $evt.item.value;
        if (this.widget.configuration.mode == 'horizontal') {
          if (this.widget.configuration.menuDropdownType === 'dropdownAll') {
            this.dropdownVisible = false;
          }

          if (mnu.appId && this.appMenusToSider) {
            this.renderAppMenusToSider(mnu);
            this.dropdownVisible = false;
            return;
          }
        }

        if (mnu.menus && mnu.menus.length > 0) {
          this.asyncLoadSubMenus(mnu.menus);
        } else if (mnu.hasOwnProperty('eventHandler')) {
          let eventHandler = mnu.eventHandler;
          if (eventHandler.title == undefined) {
            eventHandler.title = mnu.title;
          }
          eventHandler.key = mnu.id;
          eventHandler.$evtWidget = this;
          eventHandler.meta = {
            menu: {
              id: mnu.id,
              title: mnu.title,
              data: mnu.data
            }
          };
          this.updateMenuBreadcrumb(mnu);
          this.dispatchEventHandler(eventHandler);
          this.dropdownVisible = false;
        }

        // this.updateWidgetStateAsUrl(this.widget.id, 'menuid', mnu.id);
        // this.updateWidgetStateAsUrl(this.widget.id, 'selected', mnu.id, true);
      }
    },
    updateMenuBreadcrumb(menu) {
      if (this.widget.configuration.breadcrumbVisible) {
        // 面包屑更新起始下标
        let index = this.widget.configuration.breadcrumbIndex != undefined ? this.widget.configuration.breadcrumbIndex : 0;
        if (menu.eventHandler.actionType == 'redirectPage' && menu.eventHandler.targetPosition == 'widgetLayout') {
          let titles = [],
            items = [],
            id = menu.id;
          while (id && this.menuTreeMap[id]) {
            items.push({
              icon: this.menuTreeMap[id].icon,
              title: this.menuTreeMap[id].title
            });
            id = this.menuTreeMap[id].pid;
          }
          this.pageContext.emitEvent(`widgetLayoutContent:UpdateBreadcrumb:${menu.eventHandler.containerWid}`, {
            index,
            items: items.reverse()
          });
          return items.length - 1;
        } else if (menu.eventHandler.eventId == 'restoreLayoutContent' && menu.index === true) {
          this.pageContext.emitEvent(`widgetLayout:UpdateBreadcrumb:${menu.eventHandler.eventWid}`, {
            index,
            items: [
              {
                icon: menu.icon,
                title: menu.title
              }
            ]
          });
          return 0;
        }
        return -1;
      }
    },
    asyncLoadSubMenusByKeys(keys) {
      for (let k of keys) {
        if (this.menuIdMap[k] != undefined) {
          this.asyncLoadSubMenus(this.menuIdMap[k].menus);
        }
      }
    },
    asyncLoadSubMenus(menus) {
      let promise = [];
      if (!this.designMode) {
        if (menus) {
          for (let i = 0, len = menus.length; i < len; i++) {
            let m = menus[i];
            if (m.appId && !m.fetching) {
              m.fetching = true;
              promise.push(this.fetchAppSubMenus(m.appId, m));
            }
          }
        }
      }

      return Promise.all(promise);
    },
    menuBadgeCount(arg) {
      if (this.badgeCountQueue.length && !this.designMode) {
        let executeFetchCount = (overflowCount, showZero) => {
          for (let menu of this.badgeCountQueue) {
            if (arg) {
              // 刷新单个菜单的徽标，菜单code、id、title来匹配对应菜单
              if ((arg.code && arg.code != menu.code) || (arg.id && arg.id != menu.id) || (arg.title && arg.title != menu.title)) {
                continue;
              }
            }
            let { badgeSourceType, dataSourceId, defaultCondition, dataModelUuid } = menu.badge;
            let setCount = (count, m) => {
              let num = Number(count);
              if (typeof num == 'number') {
                this.$set(m, 'badgeNum', count);
              }
              if (overflowCount != undefined) {
                this.$set(m, 'overflowCount', overflowCount);
              }
              if (showZero != undefined) {
                this.$set(m, 'showZero', showZero);
              }
            };
            if (badgeSourceType == 'countJsFunction' && menu.badge.countJsFunction) {
              let parts = menu.badge.countJsFunction.split('.');
              let promise = this.developJsInstance()[parts[0]][parts[1]]();
              promise.then(count => {
                setCount.call(this, count, menu);
                // console.log('设置菜单徽标数', menu, count);
              });
            } else if (badgeSourceType == 'dataModel' || badgeSourceType == 'dataSource') {
              let _this = this;
              // 创建数据源
              let dsOptions = {
                onDataChange: function (data, count, params) {
                  setCount.call(_this, count, menu);
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
        };
        if (
          this.widget.configuration.menuBadge.badgeOverflowCountShowType == 'systemDefault' ||
          this.widget.configuration.menuBadge.badgeShowZero == 'systemDefault'
        ) {
          //TODO: 获取系统默认设置的封顶数目、徽标0显示
          executeFetchCount.call(this);
        } else {
          executeFetchCount.call(this);
        }
      }
    },
    getOpenKeysOfDefaultSelected() {
      let openKeys = [];
      let defaultSelectedKey = this.widget.configuration.defaultSelectedKey;
      if (!defaultSelectedKey) {
        return openKeys;
      }

      let parentKeys = [];
      let fetchOpenKeys = (parentMenu, menus) => {
        if (parentMenu) {
          parentKeys.push(parentMenu.id);
        }
        menus.forEach(menu => {
          if (menu.id == defaultSelectedKey) {
            openKeys = [...parentKeys];
          }
          if (menu.menus) {
            fetchOpenKeys(menu, menu.menus);
          }
        });
        if (parentMenu) {
          parentKeys.pop();
        }
      };
      fetchOpenKeys(null, this.widget.configuration.menus);
      return openKeys;
    },
    fireSelectedMenusByKeys(selectedKeys) {
      let selectedMenus = [],
        openKeys = [];
      let fetchSelectedMenu = (menus, parent) => {
        menus.forEach(menu => {
          if (selectedKeys.indexOf(menu.id) != -1) {
            if (menu.appId == undefined || this.appMenusToSider) {
              selectedMenus.push(menu);
            } else {
              if (this.cacheAppSubMenuData != undefined && this.cacheAppSubMenuData[menu.appId].defaultSelectedKey) {
                selectedKeys.splice(selectedKeys.indexOf(menu.id), 1);
                selectedKeys.push(this.cacheAppSubMenuData[menu.appId].defaultSelectedKey);
              }
            }
            openKeys.push(menu.id);
            openKeys.push(...this.getParentIdsById(menu.id));
          }
          if (menu.menus) {
            fetchSelectedMenu(menu.menus, menu);
          }
        });
      };

      fetchSelectedMenu(this.menus);
      if (openKeys.length) {
        if (this.widget.configuration.accordion) {
          this.openKeys.splice(0, this.openKeys.length);
        }
        if (this.vMode == 'inline') {
          this.openKeys.push(...openKeys);
        }
      }

      selectedMenus.forEach(menu => {
        this.clickMenu(null, menu);
      });
    },
    getParentIdsById(id) {
      let current = this.menuTreeMap[id];
      if (current && current.pid && this.menuTreeMap[current.pid]) {
        let parentIds = [];
        parentIds.push(current.pid);
        let _pids = this.getParentIdsById(current.pid);
        parentIds.push(..._pids);
        return parentIds;
      }
      return [];
    },
    adjustHeaderMenuContainer() {
      // 水平菜单：自适应宽度调整菜单到更多里面
      if (this.vMode == 'horizontal') {
        this.menuSelfAdjusting = true;
        let totalWidth = this.$el.clientWidth;
        let menuSysBarWidth = this.$refs.menuSysBar ? this.$refs.menuSysBar.scrollWidth : 0;
        let allowMenuWidth = totalWidth - menuSysBarWidth,
          headerMenuFitCount = 9999;

        let _menus = deepClone(this.originalMenus);
        let hasDeepClone = false;
        if (this.widget.configuration.subMenuExpandType == 'topDrawer') {
          let eachItemWidth = this.$refs.headerMenuContainer.firstChild
            ? this.$refs.headerMenuContainer.firstChild.clientWidth || 150
            : 150;
          allowMenuWidth -= eachItemWidth;
          headerMenuFitCount = Math.floor(allowMenuWidth / eachItemWidth); // 最大可容纳展示的菜单数目
          // 自适应头部菜单数量，多余的分配到更多里面
          if (headerMenuFitCount < _menus.length) {
            this.horizontalMoreMenus[0].menus.splice(0, this.horizontalMoreMenus[0].menus.length);
            this.horizontalMoreMenus[0].menus.push(..._menus.splice(headerMenuFitCount));
            _menus.push(this.horizontalMoreMenus[0]);
            this.menus.splice(0, this.menus.length);
            this.menus.push(..._menus);
            hasDeepClone = hasDeepClone || true;
          }
          this.overlayWidth = this.$el.parentElement.clientWidth + 'px';
        } else {
          if (this.$refs.menu) {
            let $ul = this.$refs.menu.$el,
              mRwidth = 20, //单个导航右外边距
              width = 40, //导航栏左右内边距20+20
              j = 0;
            allowMenuWidth -= 150; // 保留一个 “更多” 的宽度
            for (let i = 0, len = $ul.children.length; i < len; i++) {
              let n = $ul.children[i];
              if (!n.classList.contains('ant-menu-overflowed-submenu')) {
                if (width + n.clientWidth > allowMenuWidth) {
                  headerMenuFitCount = j;
                  break;
                } else {
                  width += n.clientWidth + mRwidth;
                  j++;
                }
              }
            } // 自适应头部菜单数量，多余的分配到更多里面
            if (headerMenuFitCount < _menus.length) {
              this.horizontalMoreMenus[0].menus.splice(0, this.horizontalMoreMenus[0].menus.length);
              this.horizontalMoreMenus[0].menus.push(..._menus.splice(headerMenuFitCount));
              _menus.push(this.horizontalMoreMenus[0]);
              this.menus.splice(0, this.menus.length);
              this.menus.push(..._menus);
              hasDeepClone = hasDeepClone || true;
            }
          }
        }
        if (hasDeepClone) {
          // 中间导航存在深拷贝与徽标队列断开，在此重新关联
          this.badgeCountQueue.splice(0, this.badgeCountQueue.length);
          this.menus = this.cascadeMenus(this.menus, this.widget.configuration.menuBadge);
          this.menuBadgeCount();
        }
        this.$nextTick(() => {
          this.menuSelfAdjusting = false;
        });
      }
    },
    createAsyncMenuDevelopJsInstances(results = []) {
      const _this = this;
      results.forEach(configuration => {
        let jsModules = configuration.jsModules || [];
        jsModules.forEach(jsModule => {
          let js = jsModule.key;
          if (!_this.__developScript || !_this.__developScript[js] || !_this.$developJsInstance || _this.$developJsInstance[js]) {
            return;
          }
          _this.$developJsInstance[js] = new _this.__developScript[js].default(_this);
        });
      });
    },
    listenOpenModuleMenuAndLocatePageMenu() {
      this.pageContext.handleEvent(`WidgetMenu:OpenModuleMenuAndLocatePageMenu:${this.widget.id}`, data => {
        // 该逻辑仅仅只是用于选中、打开菜单效果，不进行菜单的事件触发！！！
        for (let i = 0, len = this.menus.length; i < len; i++) {
          let m = this.menus[i];
          if (m.appId == data.appId) {
            let openKeys = [];
            openKeys.push(m.id);
            let pageMenuKey = undefined;
            let locatePageMenu = parent => {
              if (parent.menus && parent.menus.length) {
                for (let i = 0, len = parent.menus.length; i < len; i++) {
                  let subMenu = parent.menus[i];
                  if (subMenu.menus && subMenu.menus.length) {
                    locatePageMenu.call(this, subMenu);
                  } else if (parent.menus[i].eventHandler && parent.menus[i].eventHandler.pageId == data.pageId) {
                    openKeys.push(parent.id);
                    pageMenuKey = parent.menus[i].id;
                    return;
                  }
                }
              }
            };

            // 定位到页面导航
            if (m.fetched) {
              locatePageMenu.call(this, m);
              if (pageMenuKey) {
                this.selectedKeys.splice(0, this.selectedKeys.length);
                this.selectedKeys.push(pageMenuKey);
              } else {
                if (m.appId && this.appMenusToSider) {
                  this.renderAppMenusToSider(m, wgt => {
                    wgt.configuration.defaultSelectedKey = undefined;
                    locatePageMenu.call(this, { menus: this.cacheAppSubMenuData[m.appId].menus });
                    if (pageMenuKey) {
                      this.selectedKeys.splice(0, this.selectedKeys.length);
                      this.selectedKeys.push(m.id);
                      wgt.configuration.defaultSelectedKey = pageMenuKey;
                      wgt.configuration.stopFireSelectedEvent = true;
                    }
                  });
                  return;
                }
              }
            } else {
              // 拉取模块下导航
              this.selectedKeys.splice(0, this.selectedKeys.length);
              this.selectedKeys.push(m.id);

              if (this.widget.configuration.mode == 'horizontal') {
                if (this.widget.configuration.menuDropdownType === 'dropdownAll') {
                  this.dropdownVisible = false;
                }

                if (m.appId && this.appMenusToSider) {
                  this.renderAppMenusToSider(m, wgt => {
                    wgt.configuration.defaultSelectedKey = undefined;
                    locatePageMenu.call(this, { menus: this.cacheAppSubMenuData[m.appId].menus });
                    if (pageMenuKey) {
                      wgt.configuration.defaultSelectedKey = pageMenuKey;
                      wgt.configuration.stopFireSelectedEvent = true;
                    }
                  });
                  return;
                }
              }
            }
            if (openKeys.length > 0) {
              openKeys = new Set(openKeys);
              this.openKeys.splice(0, this.openKeys.length);
              this.openKeys.push(...openKeys);
            }

            break;
          }
        }
      });
    },
    // 设置导航是否有徽标和属于那种徽标
    itemBadgeClass(menu) {
      if (menu.badgeNum || menu.showZero) {
        if (this.widget.configuration.menuBadge.badgeDisplayType == 'dot') {
          return 'has-dot-badge';
        } else if (this.widget.configuration.menuBadge.badgeDisplayType == 'text') {
          return 'has-just-label-badge';
        } else {
          return 'has-count-badge';
        }
      }
      return '';
    }
  },

  watch: {
    dropdownVisible: {
      handler(v, o) {
        if (v) {
          if (this.coverHeight == '100%') {
            this.$nextTick(() => {
              let maxHeight = 0;
              for (let i = 0, len = this.$refs.dropdownMenu.length; i < len; i++) {
                maxHeight = Math.max(this.$refs.dropdownMenu[i].$el.clientHeight, maxHeight);
              }
              this.coverHeight = maxHeight + 'px';
            });
          }
        }
      }
    },
    siderCollapsed(v) {
      if (v) {
        //收缩时二级菜单不跟随，先将openKey置空再赋值
        let openKeys = deepClone(this.openKeys);
        this.openKeys.splice(0, this.openKeys.length);
        // this.$nextTick(() => {
        //   setTimeout(() => {
        //     this.openKeys.push(...openKeys);
        //   }, 200);
        // });
      }
    }
  }
};
</script>
