<template>
  <HtmlWrapper title="主题设计">
    <a-layout class="theme-designer">
      <a-layout-header class="theme-design-header" style="background-color: var(--w-primary-color)">
        <a-row>
          <a-col :span="12">
            <h1>主题设计器{{ themePack.name ? ' / ' + themePack.name : '' }}</h1>
            <a-tag :color="themePack.status == 'PUBLISHED' ? 'green' : null">
              {{ themePack.status == 'PUBLISHED' ? '已发布' : '未发布' }}
            </a-tag>
          </a-col>
          <a-col :span="12" class="header-buttons">
            <div style="float: right">
              <ExportDef :title="'导出主题: ' + themePack.name" :uuid="themePack.uuid" type="themePack" :fileName="themePack.name">
                <a-button icon="export">导出主题</a-button>
              </ExportDef>
              <!-- <a-button icon="code" @click="onClickExportStyle" :loading="exportLoading">导出样式</a-button> -->
              <a-button icon="history">编辑历史</a-button>
              <Drawer title="主题属性" v-if="!loading" ref="drawer" :width="680">
                <a-button icon="setting">主题属性</a-button>
                <template slot="content">
                  <ThemePackDetail :detail="themePack" ref="themePackDetail" />
                </template>
                <template slot="footer">
                  <a-button type="primary" @click="onSaveDetail">保存</a-button>
                </template>
              </Drawer>

              <a-divider type="vertical" />
              <a-button icon="play-circle" @click="onPreview">预览</a-button>
              <a-button icon="save" @click="onSave">保存</a-button>
              <a-button icon="cloud-upload" :loading="publishing" @click="onPublish(true)">发布</a-button>
              <a-button icon="cloud-upload" :loading="unPublishing" v-if="themePack.status == 'PUBLISHED'" @click="onPublish(false)">
                下架
              </a-button>
            </div>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout :hasSider="true">
        <a-layout-sider theme="light" :width="348" class="left-sider">
          <a-tabs tab-position="left" size="small" v-model="activeTabKey" @change="onChangeTab">
            <a-tab-pane key="basic" tab="基础">
              <a-card size="small" :title="search.basic.enable ? null : '基础样式'" style="background-color: #00000000" :bordered="false">
                <template slot="extra">
                  <a-input
                    size="small"
                    style="width: 240px"
                    v-model="search.basic.keyword"
                    v-show="search.basic.enable"
                    allow-clear
                    placeholder="请输入关键字"
                  >
                    <a-icon slot="suffix" type="search" />
                  </a-input>

                  <a-button
                    type="link"
                    size="small"
                    :icon="search.basic.enable ? 'export' : 'search'"
                    @click="search.basic.enable = !search.basic.enable"
                  />
                </template>
                <a-menu mode="inline" style="width: 300px" v-model="selectedMenuKey" @select="onSelectMenu">
                  <template v-for="(mn, i) in basicMenus">
                    <a-menu-item
                      :key="mn.key"
                      :configKey="mn.configKey"
                      v-show="!search.basic.enable || (search.basic.keyword ? mn.title.indexOf(search.basic.keyword) > -1 : true)"
                    >
                      {{ mn.title }}
                    </a-menu-item>
                  </template>
                </a-menu>
              </a-card>
            </a-tab-pane>
            <a-tab-pane key="component" tab="组件">
              <a-card
                size="small"
                :title="search.component.enable ? null : '组件样式'"
                style="background-color: #00000000"
                :bordered="false"
              >
                <template slot="extra">
                  <a-input
                    size="small"
                    style="width: 240px"
                    v-model="search.component.keyword"
                    v-show="search.component.enable"
                    @change="onChangeComponentSearch"
                    allow-clear
                    placeholder="请输入关键字"
                  >
                    <a-icon slot="suffix" type="search" />
                  </a-input>

                  <a-button
                    type="link"
                    size="small"
                    :icon="search.component.enable ? 'export' : 'search'"
                    @click="search.component.enable = !search.component.enable"
                  />
                </template>
                <PerfectScrollbar class="layout-sider-menu-scroll">
                  <a-menu
                    style="width: 280px"
                    mode="inline"
                    @select="onSelectMenu"
                    v-model="selectedMenuKey"
                    :open-keys="openWidgetStyleMenuKey"
                    @openChange="onOpenWidgetStyleMenuChange"
                    ref="menuRef"
                  >
                    <a-sub-menu v-for="(menu, i) in menus" :key="menu.key">
                      <span slot="title">
                        <a-icon type="appstore" />
                        <span>{{ menu.title }}</span>
                      </span>
                      <a-menu-item-group v-for="(group, g) in menu.menus" :title="group.title" :key="group.key">
                        <a-menu-item
                          v-for="(item, t) in group.menus"
                          :key="item.key"
                          :configKey="'componentConfig.' + item.key"
                          v-show="
                            !search.component.enable ||
                            (search.component.keyword ? item.title.indexOf(search.component.keyword) > -1 : true)
                          "
                        >
                          {{ item.title }}
                        </a-menu-item>
                      </a-menu-item-group>
                    </a-sub-menu>
                  </a-menu>
                </PerfectScrollbar>
              </a-card>
            </a-tab-pane>
          </a-tabs>
        </a-layout-sider>
        <a-layout-content>
          <PerfectScrollbar class="layout-content-scroll" ref="currentEditConfigRef">
            <a-skeleton active :loading="loading">
              <component
                :is="themeStyleCompName"
                :config="currentEditConfig"
                @select="onSelectProp"
                @setThemeStyle="setThemeStyle"
                @lightQuote="lightQuote"
                :themeStyle="themeStyle"
                v-if="currentEditConfig != undefined && componentKey[themeStyleCompName]"
              />
              <a-result title="暂未开放" v-else></a-result>
              <a-back-top :target="backTopTarget" style="right: 30px" />
            </a-skeleton>
          </PerfectScrollbar>
        </a-layout-content>
        <a-layout-sider theme="light" :width="300" class="layout-right-sider">
          <a-tabs :activeKey="rightSiderActiveKey" @tabClick="onRightSiderTabClick">
            <a-tab-pane key="prop" tab="属性">
              <PerfectScrollbar class="layout-sider-scroll">
                <component
                  v-if="componentKey[themeStylePropCompName] && itemKey != '-1'"
                  :is="themeStylePropCompName"
                  :item="selectStyleItem"
                  :subKey="subItemKey"
                  :key="itemKey"
                />

                <a-empty v-else></a-empty>
              </PerfectScrollbar>
            </a-tab-pane>
            <a-tab-pane key="styleDevelope" tab="样式开发" v-if="configKey.indexOf('componentConfig.') != -1">
              <WidgetCodeEditor
                title="样式"
                lang="less"
                v-model="currentEditConfig.styleDevelopment"
                width="300px"
                height="calc(100vh - 115px)"
              />
            </a-tab-pane>
          </a-tabs>
        </a-layout-sider>
      </a-layout>
    </a-layout>
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import './css/index.less';
import './component/design';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import ThemePackDetail from './component/theme-pack-detail.vue';
import { generateId, getCookie, deepClone } from '@framework/vue/utils/util';
// import '@dyform/app/web/framework/vue/themeInstall';
import { createDesigner } from '@framework/vue/designer/designer';
import WidgetCodeEditor from '@pageAssembly/app/web/widget/commons/widget-code-editor.vue';
import { comps } from './component/design/widget/export';
import { some } from 'lodash';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';

export default {
  name: 'ThemeDesigner',
  props: {},
  components: { Drawer, ThemePackDetail, WidgetCodeEditor, ExportDef },
  provide() {
    return {
      packJson: {}
    };
  },
  computed: {
    currentEditConfig() {
      if (this.configKey && this.packJson) {
        let keys = this.configKey.split('.');
        return keys.length == 2 ? this.packJson[keys[0]][keys[1]] : this.packJson[keys[0]];
      }
      return undefined;
    }
  },

  data() {
    let designer = createDesigner();
    return {
      designer,
      search: {
        basic: { enable: false, keyword: '' },
        component: { enable: false, keyword: '' }
      },
      selectStyleItem: {},
      itemKey: '-1',
      subItemKey: undefined,
      selectedMenuKey: ['ThemeColor'],
      themeStyleCompName: 'ThemeColor',
      themeStylePropCompName: 'ThemeColorProp',
      configKey: 'colorConfig',
      rightSiderActiveKey: 'prop',
      themePack: {},
      packJson: {},
      currentEnableSpecify: {},
      loading: true,
      activeTabKey: 'basic',
      componentKey: {},
      componentStyleCategory: [],
      openWidgetStyleMenuKey: [],
      exportLoading: false,
      menus: [
        {
          key: 'common',
          title: '通用',
          menus: [
            { key: 'commonLayout', title: '布局', menus: [] },
            { key: 'commonComponent', title: '组件', menus: [] }
          ]
        },
        {
          key: 'form',
          title: '表单组件',
          menus: [
            { key: 'formLayout', title: '布局', menus: [] },
            { key: 'formBasicComp', title: '基础组件', menus: [] },
            { key: 'formAdvanceComp', title: '高级组件', menus: [] }
          ]
        },
        {
          key: 'page',
          title: '页面组件',
          menus: [
            { key: 'pageLayout', title: '布局', menus: [] },
            { key: 'pageBasicComp', title: '基础组件', menus: [] },
            { key: 'pageAdvanceComp', title: '高级组件', menus: [] }
          ]
        }
      ],
      themeStyle: {},
      publishing: false,
      unPublishing: false,
      dropdownScrollList: [
        '.ant-select-tree-dropdown',
        '.ant-select-dropdown-content>div',
        '.ant-dropdown',
        '.ant-mentions-dropdown',
        '.ant-dropdown-menu'
      ],
      ctlOpenDropDownList: ['.ant-dropdown-open', '.ant-select-open'],
      basicMenus: [
        { key: 'ThemeColor', title: '色彩', configKey: 'colorConfig' },
        { key: 'ThemeFont', title: '字体', configKey: 'fontConfig' },
        { key: 'ThemeBorder', title: '边框', configKey: 'borderConfig' },
        { key: 'ThemeRadius', title: '圆角', configKey: 'radiusConfig' },
        { key: 'ThemeSpace', title: '间距', configKey: 'spaceConfig' },
        { key: 'ThemeHeight', title: '高度', configKey: 'heightConfig' },
        { key: 'ThemeShadow', title: '阴影', configKey: 'shadowConfig' },
        { key: 'ThemeIcon', title: '图标', configKey: 'iconConfig' },
        { key: 'ThemeImage', title: '图片', configKey: 'imageConfig' },
        { key: 'ThemeButton', title: '按钮', configKey: 'buttonConfig' }
      ]
    };
  },

  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getThemeConfig(() => {
      this.getCurrentEnableThemeSpecify();
    });
    for (let k in window.Vue.options.components) {
      this.componentKey[k] = true;
    }
  },
  mounted() {
    this.perfectScrollbarEvent('currentEditConfig');
  },
  methods: {
    onChangeComponentSearch() {
      if (this.search.component.keyword) {
        this.openWidgetStyleMenuKey.splice(0, this.openWidgetStyleMenuKey.length, ...['common', 'page', 'form']);
      }
    },
    tryCompile() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/theme/pack/compile/${this.uuid}`, { params: {} })
          .then(({ data }) => {
            if (data.error) {
              let blob = new Blob([data.originalBody], { type: 'text/plain' });
              let url = window.URL.createObjectURL(blob);
              let fileName = `${this.themePack.themeClass}.less`;
              this.$error({
                title: '编译主题包异常',
                content: (
                  <div>
                    请确认主题包代码是否符合规范
                    <a href={url} style="margin-left:10px" download={fileName}>
                      [ 下载代码 ]
                    </a>
                  </div>
                )
              });

              console.error('主题包编译内容: ', data.originalBody);
              reject();
            } else {
              resolve();
            }
          })
          .catch(error => {});
      });
    },
    // 高亮引用
    lightQuote({ configKey, selectedKeys }) {
      this.configKey = configKey;
      this.selectMenu([selectedKeys]);
    },
    setThemeStyle(vars) {
      this.themeStyle = { ...this.themeStyle, ...vars };
    },
    initWidgetStyle() {
      // 初始化组件的样式属性数据
      if (this.packJson.componentConfig == undefined) {
        this.$set(this.packJson, 'componentConfig', {});
      }
      let menuMap = {};
      for (let m of this.menus) {
        for (let submenu of m.menus) {
          menuMap[submenu.key] = submenu;
        }
      }
      for (let k in comps) {
        if (comps[k].themePropConfig) {
          if (this.packJson.componentConfig[k] == undefined) {
            this.$set(this.packJson.componentConfig, k, deepClone(comps[k].themePropConfig));
          }
          if (menuMap[comps[k].category]) {
            menuMap[comps[k].category].menus.push({
              key: k,
              title: comps[k].title,
              order: comps[k].order
            });
          }
        }
      }
      // TODO: 按 order 排序
    },
    onClickExportStyle() {
      this.exportLoading = true;
      this.tryCompile()
        .then(() => {
          let ifm = document.createElement('iframe');
          ifm.hidden = true;
          let req = generateId();
          ifm.src = `/theme/pack/export/${this.uuid}?downloadReqId=${req}`;
          let _this = this;
          document.body.appendChild(ifm);
          let interval = setInterval(function () {
            if (getCookie('downloadReqId') == req) {
              clearInterval(interval);
              ifm.remove();
              _this.exportLoading = false;
            }
          }, 500);
        })
        .catch(() => {
          this.exportLoading = false;
        });
    },
    wtypeMapThemeCfgKey(v) {
      if (v === 'WidgetFormInput') {
        return 'ThemeInput';
      }
      return 'Theme' + v.replace('$', '');
    },
    onOpenWidgetStyleMenuChange(openKeys) {
      // const latestOpenKey = openKeys.find(key => this.openWidgetStyleMenuKey.indexOf(key) === -1);
      // if (['common-widget-style', 'page-widget-style', 'form-widget-style'].indexOf(latestOpenKey) === -1) {
      //   this.openWidgetStyleMenuKey = openKeys;
      // } else {
      //   this.openWidgetStyleMenuKey = latestOpenKey ? [latestOpenKey] : [];
      // }
    },
    onSelectProp(item, key, propCompName) {
      this.selectStyleItem = item;
      this.subItemKey = key;
      this.itemKey = generateId();
      if (propCompName != undefined) {
        this.themeStylePropCompName = propCompName;
      }
    },
    onRightSiderTabClick(e) {
      this.rightSiderActiveKey = e;
    },
    onPreview() {
      window.open(`/theme/pack/preview/${this.uuid}`, '_blank');
    },
    onPublish(publish) {
      this[publish ? 'publishing' : 'unPublishing'] = true;
      let commitPublish = () => {
        $axios
          .get(`/proxy/api/theme/pack/${publish ? 'publish' : 'unPublish'}/${this.uuid}`, { params: {} })
          .then(({ data }) => {
            this[publish ? 'publishing' : 'unPublishing'] = false;
            if (data.code == 0) {
              this.themePack.status = publish ? 'PUBLISHED' : 'UNPUBLISHED';
              $axios.get(`/theme/pack/pub/notify`, { params: { type: this.themePack.type } }).then(() => {
                this.$message.success(publish ? '发布成功' : '已下架');
              });
            }
          })
          .catch(error => {});
      };
      if (publish) {
        this.tryCompile()
          .then(() => {
            commitPublish.call(this);
          })
          .catch(() => {
            this.publishing = false;
          });
      } else {
        commitPublish.call(this);
      }
    },
    onSaveDetail() {
      let _this = this;
      this.$refs.themePackDetail.validate().then(() => {
        let i18ns = [];
        if (_this.themePack.i18n) {
          for (let key in _this.themePack.i18n) {
            for (let code in _this.themePack.i18n[key]) {
              i18ns.push({
                content: _this.themePack.i18n[key][code],
                locale: key,
                code: 'uuid'
              });
            }
          }
        }
        _this.themePack.i18ns = i18ns;
        _this.onSave(true);
      });
    },
    onSave(onlyBasicInfo) {
      if (onlyBasicInfo !== true) {
        this.themePack.defJson = JSON.stringify({ class: this.defJson.class, body: this.packJson });
      }
      if (typeof this.themePack.defJson !== 'string') {
        this.themePack.defJson = JSON.stringify(this.themePack.defJson);
      }
      if (this.themePack.specifyUuid == undefined) {
        this.themePack.specifyUuid = this.currentEnableSpecify.uuid;
      }
      // 提取主题色
      this.themePack.themeColors = [];
      this.themePack.defaultThemeColor = undefined;
      let colorClassify = this.packJson.colorConfig.themeColor.classify;
      for (let i = 0, len = colorClassify.length; i < len; i++) {
        this.themePack.themeColors.push(colorClassify[i].value);
        if (colorClassify[i].default === true) {
          this.themePack.defaultThemeColor = colorClassify[i].value;
        }
      }
      this.themePack.themeColors = this.themePack.themeColors.join(';');
      // 提取字体
      this.themePack.fontSizes = [];
      let fontSize = this.packJson.fontConfig.fontSize.classify;
      for (let i = 0, len = fontSize.length; i < len; i++) {
        this.themePack.fontSizes.push(`${fontSize[i].title}:${fontSize[i].value}`);
      }
      this.themePack.fontSizes = this.themePack.fontSizes.join(';');
      this.$loading('保存中');
      $axios
        .post('/proxy/api/theme/pack/save', this.themePack)
        .then(({ data }) => {
          if (data.data) {
            this.$message.success('保存成功');
          }
          this.$loading(false);
        })
        .catch(() => {
          this.$loading(false);
        });
    },
    onChangeTab(e) {
      this.activeTabKey = e;
    },
    getThemeConfig(callback) {
      $axios.get(`/proxy/api/theme/pack/details/${this.uuid}`, {}).then(({ data }) => {
        if (data.data) {
          this.themePack = data.data;
          if (this.themePack.i18ns && this.themePack.i18ns.length > 0) {
            this.themePack.i18n = {};
            this.themePack.i18ns.forEach(i18n => {
              this.themePack.i18n[i18n.locale] = {};
              this.themePack.i18n[i18n.locale][this.themePack.uuid] = i18n.content;
            });
          }
          if (this.themePack.defJson) {
            let defJson = JSON.parse(this.themePack.defJson);
            this.defJson = defJson;
            this.themePack.defJson = this.defJson;
            if (defJson.body) {
              this.packJson = defJson.body;
              this._provided.packJson = this.packJson;
              if (this.packJson.imageConfig == undefined) {
                this.$set(this.packJson, 'imageConfig', {});
              }
              this.initWidgetStyle();
            }
          }
          this.loading = false;
          callback.call(this);
        }
      });
    },
    getCurrentEnableThemeSpecify() {
      $axios.get('/proxy/api/theme/specify/getEnabled', {}).then(({ data }) => {
        if (data.data) {
          this.currentEnableSpecify = data.data;
          // FIXME: 判断规范与当前主题不一致的依据?
          // if (this.themePack.specifyUuid && this.currentEnableSpecify.defJson !== JSON.stringify(this.packJson)) {
          //   this.$confirm({
          //     title: '提示',
          //     content: h => (
          //       <div>
          //         主题的样式规范和当前平台不一致, 您可以基于当前主题创建一个匹配样式规范的新主题
          //         <a-button type="link" small="small">
          //           了解更多
          //         </a-button>
          //       </div>
          //     ),
          //     okText: '创建新主题',
          //     onOk() {
          //       //TODO:
          //     },
          //     onCancel() {}
          //   });
          // }
        }
      });
    },
    backTopTarget() {
      return this.$el.querySelector('.layout-content-scroll');
    },
    onSelectMenu({ item, key, selectedKeys }) {
      this.configKey = item.$el.getAttribute('configKey');
      this.selectMenu(selectedKeys);
    },
    selectMenu(selectedKeys) {
      this.selectedMenuKey = selectedKeys;
      this.themeStyleCompName = this.selectedMenuKey[0]; // 中间样式预览效果
      this.themeStylePropCompName = this.themeStyleCompName + 'Prop'; // 右侧属性配置
      this.itemKey = '-1';
      if (this.configKey) {
        let parts = this.configKey.split('.');
        // if (parts[0] === 'componentConfig') {
        //   this.themeStyleCompName = 'Theme' + this.themeStyleCompName.replace('$', ''); // 去掉有些组件类型带$符号
        // }
        if (!this.packJson.hasOwnProperty(parts[0])) {
          this.$set(this.packJson, parts[0], {});
        }
        if (parts.length == 2 && !this.packJson[parts[0]].hasOwnProperty(parts[1])) {
          this.$set(this.packJson[parts[0]], parts[1], {
            styleDevelopment: '' // 样式二开
          });
        }
      }
    },
    perfectScrollbarEvent(type) {
      let _this = this;
      const scrollContainer = this.$refs[type + 'Ref'];

      // add mouse wheel event listener
      scrollContainer &&
        scrollContainer.$el.addEventListener('wheel', e => {
          // e.preventDefault();
          // 滚动区域内存在搜索展开下拉框
          let hasOpen = some(_this.ctlOpenDropDownList, item => {
            return scrollContainer.$el.querySelector(item);
          });
          if (hasOpen) {
            // 如果当前指在滚动区域
            let hasOpen = some(_this.dropdownScrollList, item => {
              const $target = e.target.closest(item);
              if ($target) {
                let overflow = e.target.closest(item).style.overflow;
                let overflowY = e.target.closest(item).style.overflowY;
                let isConsume = (overflow && overflow != 'hidden') || (overflowY && overflowY != 'hidden');
                if (isConsume && !e.target.classList.contains('ps__child--consume')) {
                  // 加上样式可以阻止外层滚动事件
                  e.target.classList.add('ps__child--consume');
                }
                return isConsume;
              }
              return false;
            });
          }
        });
    }
  },

  watch: {
    packJson: {
      deep: true,
      handler(v) {
        console.log('主题包定义变更: ', v);
      }
    }
  }
};
</script>
