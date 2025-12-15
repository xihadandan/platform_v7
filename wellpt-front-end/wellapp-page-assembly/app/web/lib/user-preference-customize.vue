<template>
  <Drawer
    title="个性化设置"
    :width="378"
    :bodyStyle="{
      padding: '12px 20px'
    }"
    v-model="visible"
    :okText="designMode ? '预览' : '保存'"
    :ok="e => onSave(e)"
    :zIndex="100000"
    :cancel="e => onCancel(e)"
    :container="getDrawerContainer"
    :afterVisibleChange="afterVisibleChange"
  >
    <slot></slot>
    <template slot="content">
      <div :class="['user-preference-customize-pane']" v-if="fetched">
        <a-radio-group v-model="customizeType" button-style="solid" class="select-pane-type">
          <a-radio-button value="theme" v-if="userThemeDefinable">主题风格</a-radio-button>
          <a-radio-button value="layout" v-if="userLayoutDefinable">导航布局</a-radio-button>
          <!-- <a-radio-button value="pageCustomize">自定义首页</a-radio-button> -->
        </a-radio-group>
        <Scroll style="height: calc(100vh - 185px)">
          <div v-show="customizeType == 'theme'" class="pane-content">
            <div style="display: flex; align-items: baseline; justify-content: space-between">
              <div class="sub-title">当前应用主题</div>
              <a-button
                v-if="isAdminWorkbenches && currentTheme.class != undefined"
                size="small"
                type="link"
                icon="redo"
                @click="recoverSystemDefaultTheme"
              >
                系统默认
              </a-button>
            </div>
            <div :class="[currentTheme.class, currentTheme.colorClass]" style="margin-bottom: 20px">
              <div class="theme-title">
                <div class="icon-container">
                  <a-icon type="skin" :style="{ color: '#fff' }" />
                </div>
                <label>{{ themePackMap[currentTheme.class] != undefined ? themePackMap[currentTheme.class].name : '系统默认' }}</label>
              </div>
            </div>
            <template v-if="userThemeDefinable">
              <template v-if="themePackMap[currentTheme.class] != undefined">
                <div class="form-item">
                  <label>颜色</label>
                  <div class="color-square-line">
                    <template v-for="(color, c) in themePackMap[currentTheme.class].colors">
                      <div
                        class="color-square-border"
                        :key="'color-square-' + c"
                        :style="{
                          padding: '3px',
                          backgroundColor: '#FFF',
                          outline: currentTheme.colorClass == color.colorClass ? '1px solid ' + color.value : 'unset'
                        }"
                        @click.stop="onChangeCurrentThemeColor(color)"
                      >
                        <div class="color-square" :style="{ backgroundColor: color.value }"></div>
                      </div>
                    </template>
                  </div>
                </div>
                <div class="form-item" v-if="themePackMap[currentTheme.class].fontSizeOption.length > 1">
                  <label>字体</label>
                  <a-select
                    style="width: 100px; margin-left: 12px; height: 32px"
                    v-model="currentTheme.fontSizeClass"
                    :options="themePackMap[currentTheme.class].fontSizeOption"
                    :getPopupContainer="getPopupContainer"
                  ></a-select>
                </div>
              </template>
              <template v-if="themePacks.length > 0">
                <label class="sub-title">可选主题</label>
                <div class="theme-pack-select-pane">
                  <template v-for="(item, i) in themePacks">
                    <div :class="[item.themeClass, 'primary-color-1']" :key="'theme-pack-select-' + i">
                      <a-row
                        @click.native="onSelectTheme(item, true)"
                        type="flex"
                        class="theme-pack-select-row"
                        :style="{ backgroundColor: 'var(--w-primary-color-1)' }"
                      >
                        <a-col flex="60px">
                          <div class="icon-container">
                            <a-icon type="skin" :style="{ color: 'var(--w-primary-color)' }" />
                          </div>
                        </a-col>
                        <a-col flex="auto">
                          <div class="title">
                            {{ item.name }}
                          </div>

                          <div class="color-square-line">
                            <a-icon type="loading" v-if="item.loading" style="font-size: 18px; color: var(--w-primary-color)" />
                            <template v-else>
                              <template v-for="(color, c) in item.colors">
                                <div
                                  @click.stop="onClickChangeDefaultColor(item, color)"
                                  :key="'color-square-' + c"
                                  class="color-square"
                                  :style="{
                                    backgroundColor: color.value
                                  }"
                                >
                                  <a-icon type="check" v-if="color.default" />
                                </div>
                              </template>
                            </template>
                          </div>
                        </a-col>
                        <a-col flex="18px" :class="[_CONTEXT_STATE_.SSR_THEME.themeClass, _CONTEXT_STATE_.SSR_THEME.colorClass]">
                          <a-icon type="check-circle" theme="filled" class="check" v-if="selectTheme.class == item.themeClass" />
                          <div class="circle" @click.stop="onSelectTheme(item, true)" v-else></div>
                        </a-col>
                      </a-row>
                    </div>
                  </template>
                </div>
              </template>
            </template>
          </div>
          <div v-show="customizeType == 'layout' && userLayoutDefinable" class="pane-content">
            <div style="display: flex; align-items: baseline; justify-content: space-between">
              <div class="sub-title">导航布局</div>
              <a-button v-show="layoutConfDiff" size="small" type="link" icon="redo" @click="recoverLayoutToDefault">恢复默认</a-button>
            </div>
            <MenuLayoutSelect
              :config="layoutConf"
              :siderToTop="siderToTop"
              style="justify-content: space-between; margin-bottom: 20px"
              :selectStyle="{ width: '80px', height: '54px' }"
              :divStyle="{ padding: '8px 12px' }"
            />
            <a-form class="user-preference-layout-form" :colon="false" layout="vertical">
              <!-- 菜单在头部情况下,支持菜单栏独立展示 -->
              <a-form-item label="独立菜单栏" v-if="layoutConf.menuPosition == 'header'">
                <a-switch v-model="layoutConf.topMenuBar" @change="onChangeTopMenuBar"></a-switch>
              </a-form-item>

              <template v-if="layoutConf.menuPosition == 'sider' || (layoutConf.menuPosition == 'header' && layoutConf.appMenusToSider)">
                <a-form-item label="左侧导航置顶">
                  <a-switch v-model="siderToTop" @change="changeSiderToTop"></a-switch>
                </a-form-item>
              </template>

              <a-form-item>
                <template #label>
                  {{ layoutConf.topMenuBar && layoutConf.menuPosition == 'header' ? '横幅和菜单固定' : '横幅固定' }}
                </template>
                <a-switch v-model="layoutConf.headerFixed"></a-switch>
              </a-form-item>
              <template v-if="layoutConf.menuPosition == 'sider' || (layoutConf.menuPosition == 'header' && layoutConf.appMenusToSider)">
                <a-form-item label="显示左导航折叠按钮">
                  <a-switch v-model="layoutConf.menuCollapseBtnVisible"></a-switch>
                  <!-- <a-radio-group v-model="layoutConf.menuCollapseBtnDisplayType" button-style="solid">
                    <a-radio-button value="hidden">不显示</a-radio-button>
                    <a-radio-button value="topDisplay" style="padding: 0 12px">显示在导航顶部</a-radio-button>
                    <a-radio-button value="bottomDisplay" style="padding: 0 12px">显示在导航底部</a-radio-button>
                  </a-radio-group> -->
                </a-form-item>
                <a-form-item label="左导航默认状态">
                  <a-radio-group v-model="layoutConf.siderMenuCollapseType" button-style="solid">
                    <a-radio-button value="unCollapsed">展开</a-radio-button>
                    <a-radio-button value="collapsed">折叠</a-radio-button>
                  </a-radio-group>
                </a-form-item>
                <a-form-item label="左导航风格" v-if="showSiderBgColorType">
                  <a-radio-group v-model="layoutConf.siderBgColorType" button-style="solid">
                    <a-radio-button value="light">浅色</a-radio-button>
                    <a-radio-button value="dark">深色</a-radio-button>
                    <a-radio-button value="primary-color">主题色</a-radio-button>
                  </a-radio-group>
                </a-form-item>
              </template>
              <a-form-item label="横幅风格" v-if="showHeaderBgColorType">
                <a-radio-group v-model="layoutConf.headerBgColorType" button-style="solid">
                  <a-radio-button value="light">浅色</a-radio-button>
                  <a-radio-button value="primary-color">主题色</a-radio-button>
                </a-radio-group>
              </a-form-item>

              <a-form-item label="logo显示位置" v-if="layoutConf.layoutType == 'siderTopMiddleBottom'">
                <a-radio-group v-model="layoutConf.logoPosition" button-style="solid">
                  <a-radio-button value="header">横幅</a-radio-button>
                  <a-radio-button value="sider">左导航</a-radio-button>
                </a-radio-group>
              </a-form-item>

              <a-form-item v-if="layoutConf.menuPosition == 'header'">
                <template #label>
                  {{ layoutConf.topMenuBar ? '菜单水平位置' : '导航水平位置' }}
                </template>
                <a-radio-group v-model="layoutConf.horizontalAlign" button-style="solid">
                  <a-radio-button value="left">居左</a-radio-button>
                  <a-radio-button value="center">居中</a-radio-button>
                  <a-radio-button value="right">居右</a-radio-button>
                </a-radio-group>
              </a-form-item>
              <a-form-item v-if="layoutConf.menuPosition == 'header'">
                <template #label>
                  {{ layoutConf.topMenuBar ? '菜单交互' : '导航交互' }}
                </template>
                <a-radio-group v-model="layoutConf.horizontalExpandType" button-style="solid">
                  <a-radio-button value="topDrawer">全部展开</a-radio-button>
                  <a-radio-button value="dropdown">单列展开</a-radio-button>
                </a-radio-group>
              </a-form-item>

              <div class="sub-title">导航标签页</div>
              <a-form-item label="在动态导航标签页中打开页面">
                <a-switch v-model="layoutConf.pageToTabs"></a-switch>
              </a-form-item>

              <div class="sub-title">页面底部</div>
              <a-form-item label="页面底部页脚显示">
                <a-radio-group v-model="layoutConf.footerDisplayType" button-style="solid">
                  <a-radio-button value="hidden">不显示</a-radio-button>
                  <a-radio-button value="indexShow">仅首页显示</a-radio-button>
                  <a-radio-button value="visible">全部页面显示</a-radio-button>
                </a-radio-group>
              </a-form-item>
            </a-form>
          </div>
          <div v-show="customizeType == 'pageCustomize'" class="pane-content">
            <a-result />
          </div>
        </Scroll>
      </div>
      <div v-else class="spin-center">
        <a-spin />
      </div>
    </template>
  </Drawer>
</template>
<style lang="less">
.user-preference-customize-pane {
  .select-pane-type {
    display: flex;
    margin-bottom: 12px;
    > label {
      flex: 1;
      text-align: center;
    }
  }
  .pane-content {
    color: rgba(0, 0, 0, 0.65);
    .theme-title {
      display: flex;
      align-items: center;
      background-color: var(--w-primary-color);
      color: #fff;
      border-radius: 4px;
      height: 75px;
      padding: 12px 20px;
      > label {
        margin-left: 12px;
      }
    }
    .form-item {
      display: flex;
      align-items: center;
      margin-bottom: 12px;
    }
    .sub-title {
      font-weight: bold;
      display: inline-block;
      margin-bottom: 8px;
      line-height: 32px;
      color: var(--w-text-color-dark);
    }
    .color-square-line {
      display: flex;
      flex-wrap: nowrap;
      .color-square-border {
        background-color: #fff;
        padding: 3px;
        width: 24px;
        height: 24px;
        margin-left: 8px;
        border-radius: 4px;
      }
      .color-square {
        width: 18px;
        height: 18px;
        border-radius: 4px;
        display: inline-grid;
        align-items: center;
        outline: 1px solid rgba(0, 0, 0, 0.1);
        > i {
          font-size: 11px;
          color: #fff;
        }
        &:not(:first-child) {
          margin-left: 10px;
        }
      }
    }
    .icon-container {
      width: 48px;
      height: 48px;
      border-radius: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: var(--w-primary-color-5);
      > i {
        font-size: 24px;
      }
    }

    .theme-pack-select-pane {
      > div:not(:last-child) {
        margin-bottom: 12px;
      }
      .circle {
        width: 16px;
        height: 16px;
        outline: 1px solid #b3b3b3;
        border-radius: 8px;
      }
      .check {
        color: var(--w-primary-color);
        font-size: 17px;
      }
      .theme-pack-select-row {
        height: 75px;
        border-radius: 4px;
        padding: 12px 20px;
        > div {
          align-self: center;
          .title {
            line-height: 32px;
            font-weight: bold;
          }
        }

        .icon-container {
          background-color: var(--w-primary-color-3);
        }
      }
    }
  }

  .user-preference-layout-form {
    .ant-form-item {
      margin-bottom: 10px;
      > .ant-form-item-label {
        min-width: 180px;
        color: var(--w-text-color-dark);
      }
    }
  }
}
</style>
<script type="text/babel">
import Drawer from './drawer.vue';
import MenuLayoutSelect from '@pageAssembly/app/web/page/product-center/component/manager/menu-layout-select.vue';
import md5 from '@framework/vue/utils/md5';

export default {
  name: 'UserPreferenceCustomize',
  inject: ['_CONTEXT_STATE_', 'ENVIRONMENT'],
  props: {
    designMode: { type: Boolean, default: false }
  },
  components: { Drawer, MenuLayoutSelect },
  computed: {
    themePackMap() {
      let map = {};
      for (let i = 0, len = this.themePacks.length; i < len; i++) {
        map[this.themePacks[i].themeClass] = this.themePacks[i];
      }
      return map;
    },
    showHeaderBgColorType() {
      if (this.ENVIRONMENT.layoutConf) {
        return this.ENVIRONMENT.layoutConf.showHeaderBgColorType;
      }
      return true;
    },
    showSiderBgColorType() {
      if (this.ENVIRONMENT.layoutConf) {
        return this.ENVIRONMENT.layoutConf.showSiderBgColorType;
      }
      return true;
    }
  },
  data() {
    return {
      visible: false,
      customizeType: 'theme',
      selectTheme: {
        class: undefined,
        name: undefined,
        colorClass: undefined,
        fontSize: undefined,
        fontSizeClass: undefined
      },
      currentTheme: {
        class: this._CONTEXT_STATE_.SSR_THEME.themeClass || undefined,
        colorClass: this._CONTEXT_STATE_.SSR_THEME.colorClass,
        fontSizeClass: this._CONTEXT_STATE_.SSR_THEME.fontSizeClass || 'font-size-1'
      },
      themePacks: [],
      themePackColor: {},
      themePackFontSize: {},
      siderToTop: this.ENVIRONMENT.layoutConf != undefined ? this.ENVIRONMENT.layoutConf.layoutType == 'siderTopMiddleBottom' : false,
      userLayoutDefinable: this.ENVIRONMENT.userLayoutDefinable == true,
      userThemeDefinable: this.ENVIRONMENT.userThemeDefinable == true,
      layoutConf: this.ENVIRONMENT.layoutConf != undefined ? JSON.parse(JSON.stringify(this.ENVIRONMENT.layoutConf)) : {},
      layoutConfDiff: false,
      originalTheme: JSON.parse(JSON.stringify(this._CONTEXT_STATE_.SSR_THEME)),
      originalLayoutConf: this.ENVIRONMENT.layoutConf != undefined ? JSON.parse(JSON.stringify(this.ENVIRONMENT.layoutConf)) : {},
      fetched: false,
      isAdminWorkbenches: false
    };
  },
  beforeCreate() {},
  created() {
    if (!this.userThemeDefinable && this.userLayoutDefinable) {
      this.customizeType = 'layout';
    }
  },
  beforeMount() {
    this.isAdminWorkbenches = this.$root.SYSTEM_ADMIN_MANAGER_WORKBENCHES;
  },
  mounted() {},
  methods: {
    getPopupContainer() {
      return document.querySelector('.user-preference-customize-pane');
    },
    afterVisibleChange(visible) {
      if (visible && !this.fetched) {
        if (this.userThemeDefinable) {
          let themeStyle = {};
          if (this.ENVIRONMENT != undefined && this.ENVIRONMENT.themeStyle != undefined) {
            let themes = this.ENVIRONMENT.themeStyle.pc.theme;
            for (let i = 0, len = themes.length; i < len; i++) {
              themeStyle[themes[i].themeClass] = themes[i];
            }
          }
          if (
            this._CONTEXT_STATE_.SSR_THEME != undefined &&
            this._CONTEXT_STATE_.SSR_THEME.classScope != undefined &&
            this._CONTEXT_STATE_.SSR_THEME.classScope.length > 0
          ) {
            // 当前需要加载的主题类
            this.fetchThemePackInfosByClasses(this._CONTEXT_STATE_.SSR_THEME.classScope).then(list => {
              this.fetched = true;

              if (list) {
                for (let i = 0, len = list.length; i < len; i++) {
                  let l = list[i];
                  if (l.themeColors != undefined) {
                    this.setThemeColorsAndFontSizeOptions(l, l.themeColors, l.fontSizes, themeStyle);
                  } else {
                    l.loading = true;
                    this.fetchDefJson(l.uuid).then(defJson => {
                      let { fontConfig, colorConfig } = defJson.body;
                      let colorClassify = colorConfig.themeColor.classify,
                        fontSizeClassify = fontConfig.fontSize.classify,
                        colors = [],
                        fontSizes = [];
                      colorClassify.forEach(c => {
                        colors.push(c.value);
                      });
                      fontSizeClassify.forEach(c => {
                        fontSizes.push(`${c.title}:${c.value}`);
                      });
                      this.$set(l, 'colors', []);
                      this.$set(l, 'fontSizeOption', []);
                      this.setThemeColorsAndFontSizeOptions(l, colors.join(';'), fontSizes.join(';'), themeStyle);
                      l.loading = false;
                    });
                  }
                  this.themePacks.push(l);
                }
              }
            });
          } else {
            this.fetched = true;
          }
        } else {
          this.fetched = true;
        }

        if (this.userLayoutDefinable) {
          this.layoutConfDiff = md5(JSON.stringify(this.ENVIRONMENT.layoutConf)) != md5(JSON.stringify(this.ENVIRONMENT.defaultLayoutConf));
        }
      }
    },
    setThemeColorsAndFontSizeOptions(l, themeColors, fontSizeString, themeStyle) {
      let colors = themeColors.split(';'),
        defaultSet = false,
        defaultIndex = 0;
      l.colors = [];
      for (let c = 0, clen = colors.length; c < clen; c++) {
        let colorClass = `primary-color-${c + 1}`,
          isDefault = false;
        if (themeStyle[l.themeClass] && themeStyle[l.themeClass].colorClass == colorClass) {
          isDefault = true;
          defaultSet = true;
        }
        if (colors[c].default) {
          defaultIndex = c;
        }
        l.colors.push({
          colorClass,
          value: colors[c],
          default: isDefault
        });
      }
      if (!defaultSet) {
        l.colors[defaultIndex].default = true;
      }

      this.$set(this.themePackColor, l.themeClass, l.colors);
      l.fontSizeOption = [];
      if (fontSizeString != undefined) {
        let fontSizes = fontSizeString.split(';');
        for (let f = 0, flen = fontSizes.length; f < flen; f++) {
          let parts = fontSizes[f].split(':');
          l.fontSizeOption.push({
            label: parts[0],
            value: `font-size-${f + 1}`
          });
        }
        this.$set(this.themePackFontSize, l.themeClass, l.fontSizeOption);
      }
    },

    fetchDefJson(uuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/theme/pack/details/${uuid}`, { params: {} })
          .then(({ data }) => {
            if (data.data) {
              resolve(JSON.parse(data.data.defJson));
            }
          })
          .catch(error => {});
      });
    },
    changeSiderToTop(checked) {
      this.layoutConf.layoutType = checked ? 'siderTopMiddleBottom' : 'topMiddleSiderBottom';
      if (!checked) {
        // 布局变更，logo位置也要随之变化
        this.layoutConf.logoPosition = 'header';
      }
    },
    onChangeTopMenuBar(checked) {
      if (this.layoutConf.appMenusToSider && checked) {
        this.layoutConf.layoutType = 'topMiddleSiderBottom';
        this.siderToTop = this.layoutConf.layoutType == 'siderTopMiddleBottom';
      }
    },
    resetSelectTheme() {
      this.selectTheme.class = undefined;
      this.selectTheme.colorClass = undefined;
    },
    onChangeCurrentThemeColor(color) {
      this.currentTheme.colorClass = color.colorClass;
      this.resetSelectTheme();
    },
    onClickChangeDefaultColor(item, color) {
      for (let i = 0, len = item.colors.length; i < len; i++) {
        item.colors[i].default = false;
      }
      color.default = true;
      if (this.selectTheme.class == item.themeClass) {
        this.selectTheme.colorClass = color.colorClass;
      }
      if (this.selectTheme.class !== item.themeClass) {
        this.onSelectTheme(item);
      }
    },
    onSelectTheme(item, checked) {
      if (this.selectTheme.class == item.themeClass) {
        this.selectTheme.class = undefined;
        this.selectTheme.colorClass = undefined;
      } else {
        this.selectTheme.class = item.themeClass;
        for (let i = 0, len = item.colors.length; i < len; i++) {
          if (item.colors[i].default) {
            this.selectTheme.colorClass = item.colors[i].colorClass;
            break;
          }
        }
      }
    },
    fetchThemePackInfosByClasses(classes) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/theme/pack/listDetailsIgnoreDefJsonByThemeClasses`, classes)
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {});
      });
    },
    recoverLayoutToDefault() {
      for (let key in this.ENVIRONMENT.defaultLayoutConf) {
        this.$set(this.layoutConf, key, this.ENVIRONMENT.defaultLayoutConf[key]);
        if (key == 'menuCollapseBtnDisplayType' && !this.ENVIRONMENT.defaultLayoutConf.hasOwnProperty('menuCollapseBtnVisible')) {
          this.ENVIRONMENT.defaultLayoutConf.menuCollapseBtnVisible = this.ENVIRONMENT.defaultLayoutConf[key] !== 'hidden';
        }
      }
      this.siderToTop = this.layoutConf.layoutType == 'siderTopMiddleBottom';
    },
    commitThemeData() {
      return new Promise((resolve, reject) => {
        if (
          this.selectTheme.class != undefined ||
          this.currentTheme.colorClass != this.originalTheme.colorClass ||
          this.currentTheme.fontSizeClass != this.originalTheme.fontSizeClass
        ) {
          let dataValue = {
            class: this._CONTEXT_STATE_.SSR_THEME.themeClass,
            colorClass: this.currentTheme.colorClass,
            fontSizeClass: this.currentTheme.fontSizeClass
          };
          if (this.selectTheme.class != undefined) {
            dataValue.class = this.selectTheme.class;
            dataValue.colorClass = this.selectTheme.colorClass;
            dataValue.fontSizeClass = undefined;
          }
          if (this.designMode) {
            this.updatePageTheme(dataValue);
            resolve();
            return;
          }

          let dataValueString = JSON.stringify(dataValue);
          let m = this.isAdminWorkbenches ? `${this._$SYSTEM_ID}:system_manager` : this._$SYSTEM_ID;
          let data = new FormData();
          data.append('dataKey', 'USER_THEME');
          data.append('dataValue', dataValueString);
          data.append('moduleId', m);
          data.append('remark', '用户主题类');
          $axios
            .post('/proxy/api/user/preferences/save', data, {
              headers: { USER_THEME_UPDATE: dataValueString } // 提交更新的值到header，后端response后由app.js进行session更新
            })
            .then(() => {
              this.updatePageTheme(dataValue);
              this.resetSelectTheme();
              resolve();
              // 更新用户主题缓存
              $axios
                .post(`/api/cache/set`, {
                  value: dataValueString,
                  key: `user:preference:${m}:${this._$USER.userId}:theme`
                })
                .then(({ data }) => {})
                .catch(error => {});
            });
        } else {
          resolve();
        }
      });
    },
    updatePageTheme(dataValue) {
      this._CONTEXT_STATE_.SSR_THEME.themeClass = dataValue.class;
      this._CONTEXT_STATE_.SSR_THEME.colorClass = dataValue.colorClass;
      this._CONTEXT_STATE_.SSR_THEME.fontSizeClass = dataValue.fontSizeClass;
      this.originalTheme = JSON.parse(JSON.stringify(this._CONTEXT_STATE_.SSR_THEME));
      this.currentTheme.colorClass = dataValue.colorClass;
      this.currentTheme.class = dataValue.class;
      this.currentTheme.fontSizeClass = dataValue.fontSizeClass || 'font-size-1';
    },
    commitLayoutConf() {
      return new Promise((resolve, reject) => {
        let dataValueString = JSON.stringify(this.layoutConf);
        if (md5(dataValueString) != md5(JSON.stringify(this.originalLayoutConf))) {
          if (this.designMode) {
            this.updatePageLayout();
            resolve(true);
            return;
          }
          let data = new FormData();
          let m = this.$root.SYSTEM_ADMIN_MANAGER_WORKBENCHES ? `${this._$SYSTEM_ID}:system_manager` : this._$SYSTEM_ID;
          data.append('dataKey', 'USER_SYSTEM_LAYOUT');
          data.append('dataValue', dataValueString);
          data.append('moduleId', m);
          data.append('remark', '用户导航布局');
          $axios.post('/proxy/api/user/preferences/save', data, {}).then(() => {
            resolve(true);
            this.updatePageLayout();
            $axios
              .post(`/api/cache/set`, {
                value: dataValueString,
                key: `user:preference:${m}:${this._$USER.userId}:systemLayout`
              })
              .then(({ data }) => {})
              .catch(error => {});
          });
        } else {
          resolve();
        }
      });
    },
    recoverSystemDefaultTheme() {
      //FIXME: 系统默认要根据系统主题设置的默认进行设置
      this._CONTEXT_STATE_.SSR_THEME.themeClass = undefined;
      this._CONTEXT_STATE_.SSR_THEME.colorClass = undefined;
      this._CONTEXT_STATE_.SSR_THEME.fontSizeClass = undefined;
      this.resetSelectTheme();
    },
    updatePageLayout() {
      for (let key in this.layoutConf) {
        this.$set(this.ENVIRONMENT.layoutConf, key, this.layoutConf[key]);
      }
      this.originalLayoutConf = JSON.parse(JSON.stringify(this.layoutConf));
    },
    onSave(e) {
      this.$loading();
      Promise.all([this.commitThemeData(), this.commitLayoutConf()]).then(() => {
        this.$loading(false);
        e(true);
      });
    },

    onCancel(e) {
      this.resetSelectTheme();
      // 还原主题内容
      this.cancelThemeChange();
      // 还原布局内容
      this.cancelLayoutConfChange();
    },
    cancelLayoutConfChange() {
      for (let key in this.originalLayoutConf) {
        this.$set(this.layoutConf, key, this.originalLayoutConf[key]);
      }
    },

    cancelThemeChange() {
      this._CONTEXT_STATE_.SSR_THEME.themeClass = this.originalTheme.themeClass;
      this._CONTEXT_STATE_.SSR_THEME.colorClass = this.originalTheme.colorClass;
      this._CONTEXT_STATE_.SSR_THEME.fontSizeClass = this.originalTheme.fontSizeClass;
    },
    getDrawerContainer() {
      return document.body;
    }
  },
  watch: {
    layoutConf: {
      deep: true,
      handler(v) {
        // console.log('布局变更', v);
        // this.layoutConfDiff = md5(JSON.stringify(v)) != md5(JSON.stringify(this.ENVIRONMENT.defaultLayoutConf));
        for (let key in this.layoutConf) {
          this.$set(this.ENVIRONMENT.layoutConf, key, this.layoutConf[key]);
          if (key == 'layoutType') {
            this.siderToTop = this.layoutConf.layoutType == 'siderTopMiddleBottom';
          }
        }
      }
    },
    selectTheme: {
      deep: true,
      handler(v) {
        if (this.selectTheme.class != undefined) {
          // 按照可选主题预览更新主题效果
          this._CONTEXT_STATE_.SSR_THEME.themeClass = this.selectTheme.class;
          this._CONTEXT_STATE_.SSR_THEME.colorClass = this.selectTheme.colorClass;
          this._CONTEXT_STATE_.SSR_THEME.fontSizeClass = undefined; // 按默认展示
        }
      }
    },
    currentTheme: {
      deep: true,
      handler(v) {
        if (this.selectTheme.class == undefined) {
          this._CONTEXT_STATE_.SSR_THEME.themeClass = this.currentTheme.class;
          this._CONTEXT_STATE_.SSR_THEME.colorClass = this.currentTheme.colorClass;
          this._CONTEXT_STATE_.SSR_THEME.fontSizeClass = this.currentTheme.fontSizeClass; // 按默认展示
        }
      }
    }
  }
};
</script>
