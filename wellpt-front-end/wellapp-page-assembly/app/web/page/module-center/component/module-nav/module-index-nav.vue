<template>
  <a-card :bordered="false" title="模块导航" :bodyStyle="{ height: 'calc(100vh - 130px)', padding: '1px 0 0 0' }" class="module-index-nav">
    <template slot="extra">
      <span style="font-size: var(--w-font-size-base); color: var(--w-text-color-base)">页面设计模式</span>
      <a-switch v-model="definition.designable" @change="onChangeDesignable" :loading="designableUpdating" />
    </template>
    <div style="text-align: center; position: absolute; top: 40%; left: 49%">
      <a-spin :spinning="loading"></a-spin>
    </div>

    <div v-if="!loading" class="module-index-page-design">
      <a-page-header title="页面设计模式" v-if="definition.designable">
        <template slot="subTitle">
          {{ subTitle }}
          <a-alert message="未知的页面设计结构, 将无法识别关联导航组件等设计元素" banner style="margin: 8px" v-if="unknownDesign" />
        </template>
        <div style="margin-bottom: 16px">
          <a-button type="line" v-show="definition.designable" @click="onClickToEditPage" style="margin-right: 8px">编辑模块主页</a-button>
          <a-popconfirm title="确定重置模块主页吗?" ok-text="确定" cancel-text="取消" @confirm="resetPageDefinition">
            <a-button type="line" :loading="resetting" style="margin-right: 8px">重置模块主页</a-button>
          </a-popconfirm>
          <span style="font-size: var(--w-font-size-base); color: var(--w-text-color-base)">关联导航组件</span>
          <a-select
            style="width: 200px; margin-left: 4px"
            :options="widgetMenuOptions"
            v-model="moduleMenuId"
            @change="onChangeModuleMenuId"
          />
        </div>
        <div>
          <img src="/static/images/module_page_design.png" style="width: 100%; border-radius: 4px" />
        </div>
      </a-page-header>

      <a-layout v-else class="nav-layout">
        <a-layout-sider theme="light" :width="378" v-if="wMenu != undefined" class="nav-layout-sider">
          <a-radio-group v-model="pageType" button-style="solid" class="page-type-select">
            <a-radio-button value="1">导航项</a-radio-button>
            <a-radio-button value="2">导航设置</a-radio-button>
          </a-radio-group>
          <div v-show="pageType == '1'" style="height: calc(100vh - 230px); padding: 12px 0">
            <a-row :gutter="12" style="margin-bottom: 12px">
              <a-col :span="12">
                <a-button block @click="e => openEditNavDetail(undefined, wMenu.configuration.menus)">
                  <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                  添加
                </a-button>
              </a-col>
              <a-col :span="12">
                <a-button block @click="openQuickAddNavDrawer">
                  <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                  快速添加
                </a-button>
              </a-col>
            </a-row>
            <div class="menu-container" v-if="!loading">
              <PerfectScrollbar style="height: calc(100vh - 300px); margin-right: -20px; padding-right: 20px">
                <draggable
                  v-if="wMenu.configuration.menus.length > 0"
                  v-model="wMenu.configuration.menus"
                  :group="{ name: 'menu', pull: true, put: true }"
                  handle=".drag-btn-handler"
                  :data-level="1"
                >
                  <template v-for="(menu, i) in wMenu.configuration.menus">
                    <!-- 一级导航 -->
                    <a-row type="flex" :key="'menu_' + i">
                      <a-col flex="100%">
                        <a-row class="menu-item btn-ghost-container" type="flex">
                          <a-col flex="auto" @click.native="toggleMenu(menu.id)" class="level-1-col">
                            <img
                              v-if="menu.menus && menu.menus.length"
                              class="svg-iconfont"
                              style="margin-right: var(--w-padding-2xs)"
                              :src="menuOpened.includes(menu.id) ? '/static/svg/folder-open.svg' : '/static/svg/folder-close.svg'"
                            />
                            <Icon type="menu" v-if="menu.menus && menu.menus.length == 0 && !menu.icon" />
                            <div>
                              <Icon v-if="menu.icon" :type="menu.icon"></Icon>
                              {{ menu.title }}
                            </div>
                          </a-col>
                          <a-col flex="110px" style="text-align: right">
                            <a-button
                              v-if="!menu.index"
                              ghost
                              type="link"
                              size="small"
                              class="icon-only"
                              @click="e => openEditNavDetail(undefined, menu.menus, menu)"
                            >
                              <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                            </a-button>
                            <a-button ghost type="link" size="small" class="icon-only" @click="e => openEditNavDetail(menu)">
                              <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
                            </a-button>
                            <a-button
                              v-if="!menu.index"
                              ghost
                              type="link"
                              size="small"
                              class="icon-only"
                              @click="wMenu.configuration.menus.splice(i, 1)"
                            >
                              <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                            </a-button>
                            <span
                              class="drag-btn-handler ant-btn ant-btn-link ant-btn-sm ant-btn-background-ghost icon-only"
                              @click.stop="() => {}"
                            >
                              <Icon type="pticon iconfont icon-ptkj-tuodong" />
                            </span>
                          </a-col>
                        </a-row>
                      </a-col>
                      <!-- 一级导航下的导航 -->
                      <a-col flex="100%">
                        <draggable
                          v-model="menu.menus"
                          :group="{ name: 'menu', pull: true, put: canDragPut }"
                          handle=".drag-2-btn-handler"
                          :key="'menu-1-' + menu.id"
                          :data-level="2"
                        >
                          <a-row
                            type="flex"
                            v-show="menuOpened.includes(menu.id) && !menu.index"
                            v-for="(secondMenu, s) in menu.menus"
                            :key="'menu-2-' + secondMenu.id"
                          >
                            <a-col flex="100%">
                              <a-row class="menu-item btn-ghost-container level-2" type="flex">
                                <a-col flex="auto" @click.native="toggleMenu(secondMenu.id)" class="level-2-col">
                                  <Icon type="pticon iconfont icon-ptkj-yemian" v-if="secondMenu.menus.length == 0 && !secondMenu.icon" />
                                  <img
                                    v-if="secondMenu.menus.length"
                                    class="svg-iconfont"
                                    style="margin-right: var(--w-padding-2xs)"
                                    :src="
                                      menuOpened.includes(secondMenu.id) ? '/static/svg/folder-open.svg' : '/static/svg/folder-close.svg'
                                    "
                                  />
                                  <div>
                                    <Icon v-if="secondMenu.icon" :type="secondMenu.icon"></Icon>
                                    {{ secondMenu.title }}
                                  </div>
                                </a-col>
                                <a-col flex="110px" style="text-align: right">
                                  <a-button
                                    ghost
                                    type="link"
                                    size="small"
                                    class="icon-only"
                                    @click="e => openEditNavDetail(undefined, secondMenu.menus, secondMenu)"
                                  >
                                    <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                                  </a-button>
                                  <a-button type="link" class="icon-only" ghost size="small" @click="e => openEditNavDetail(secondMenu)">
                                    <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
                                  </a-button>
                                  <a-button type="link" class="icon-only" ghost size="small" @click="menu.menus.splice(s, 1)">
                                    <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                                  </a-button>
                                  <span
                                    class="drag-2-btn-handler ant-btn ant-btn-link ant-btn-sm ant-btn-background-ghost icon-only"
                                    @click.stop="() => {}"
                                  >
                                    <Icon type="pticon iconfont icon-ptkj-tuodong" />
                                  </span>
                                </a-col>
                              </a-row>
                            </a-col>
                            <a-col flex="100%" v-if="secondMenu.menus">
                              <draggable
                                v-model="secondMenu.menus"
                                :group="{ name: 'menu', pull: true, put: canDragPut }"
                                handle=".drag-3-btn-handler"
                                :data-level="3"
                              >
                                <a-row
                                  v-show="menuOpened.includes(secondMenu.id)"
                                  type="flex"
                                  v-for="(thirdMenu, t) in secondMenu.menus"
                                  :key="'menu-3-' + thirdMenu.id"
                                >
                                  <a-col flex="100%">
                                    <a-row class="menu-item btn-ghost-container level-3" type="flex">
                                      <a-col flex="auto" class="level-3-col">
                                        <Icon type="pticon iconfont icon-ptkj-yemian" v-if="!thirdMenu.icon" />
                                        <div>
                                          <Icon v-if="thirdMenu.icon" :type="thirdMenu.icon"></Icon>
                                          {{ thirdMenu.title }}
                                        </div>
                                      </a-col>
                                      <a-col flex="110px" style="text-align: right">
                                        <a-button
                                          type="link"
                                          ghost
                                          class="icon-only"
                                          size="small"
                                          @click="e => openEditNavDetail(thirdMenu)"
                                        >
                                          <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
                                        </a-button>
                                        <a-button type="link" class="icon-only" ghost size="small" @click="secondMenu.menus.splice(t, 1)">
                                          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                                        </a-button>
                                        <span
                                          class="drag-3-btn-handler ant-btn ant-btn-link ant-btn-sm ant-btn-background-ghost icon-only"
                                          @click.stop="() => {}"
                                        >
                                          <Icon type="pticon iconfont icon-ptkj-tuodong" />
                                        </span>
                                      </a-col>
                                    </a-row>
                                  </a-col>
                                </a-row>
                              </draggable>
                            </a-col>
                          </a-row>
                        </draggable>
                      </a-col>
                    </a-row>
                  </template>
                </draggable>
                <a-empty v-else description="暂无导航数据" style="padding-top: 50px" />
              </PerfectScrollbar>
            </div>
          </div>
          <div v-show="pageType == '2'" style="height: calc(100vh - 230px); padding-top: 12px">
            <a-form-model
              v-if="!loading"
              class="nav-setting-form"
              :label-col="labelCol"
              :wrapper-col="wrapperCol"
              :colon="false"
              ref="navSet"
            >
              <a-form-model-item label="默认选中导航">
                <a-tree-select
                  v-model="wMenu.configuration.defaultSelectedKey"
                  show-search
                  style="width: 200px"
                  :dropdown-style="{ maxHeight: '600px', overflow: 'auto' }"
                  :replaceFields="{ value: 'id', key: 'id', children: 'menus' }"
                  :treeData="wMenu.configuration.menus"
                  treeNodeFilterProp="title"
                  allow-clear
                  tree-default-expand-all
                ></a-tree-select>
              </a-form-model-item>
              <a-form-model-item label="导航布局">
                <a-radio-group v-model="menuPosition" button-style="solid" @change="onChangeMenuPosition">
                  <a-radio-button value="left">左侧</a-radio-button>
                  <a-radio-button value="horizontal">顶部</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <!-- TODO: 背景色 -->
              <div v-show="wMenu.configuration.mode == 'horizontal'">
                <a-form-model-item label="对齐方式">
                  <a-radio-group v-model="wMenu.configuration.align" button-style="solid">
                    <a-radio-button value="left">居左</a-radio-button>
                    <a-radio-button value="center">居中</a-radio-button>
                    <a-radio-button value="right">居右</a-radio-button>
                  </a-radio-group>
                </a-form-model-item>
                <a-form-model-item label="固定在顶部">
                  <a-switch v-model="wLayout.configuration.header.configuration.topFixed" />
                </a-form-model-item>
              </div>
              <div v-show="wMenu.configuration.mode !== 'horizontal'">
                <a-form-model-item label="导航标题">
                  <a-input v-model="wMenu.configuration.menuTitle" style="width: 200px">
                    <template slot="addonBefore">
                      <WidgetIconLibModal title="选择图标" v-model="wMenu.configuration.menuTitleIcon" @change="changeTitleAndIcon">
                        <Icon
                          :type="wMenu.configuration.menuTitleIcon"
                          style="color: var(--w-primary-color); cursor: pointer"
                          v-show="wMenu.configuration.menuTitleIcon"
                        />
                        <span style="cursor: pointer">{{ wMenu.configuration.menuTitleIcon ? '' : '图标' }}</span>
                      </WidgetIconLibModal>
                    </template>
                    <template slot="addonAfter">
                      <a-switch v-model="wMenu.configuration.menuTitleVisible" size="small" @change="changeTitleAndIcon" />
                      <WI18nInput
                        v-show="wMenu.configuration.menuTitleVisible"
                        :widget="wMenu"
                        :designer="designer"
                        :code="wMenu.id + '_menuTitle'"
                        v-model="wMenu.configuration.menuTitle"
                      />
                    </template>
                  </a-input>
                </a-form-model-item>
                <a-form-model-item label="支持伸缩">
                  <a-switch v-model="wLayout.configuration.sider.configuration.collapsible" size="small" />
                </a-form-model-item>
                <a-form-model-item label="子菜单交互">
                  <a-radio-group v-model="subMenuOpenType" button-style="solid" @change="onChangeSubMenuOpenType">
                    <a-radio-button value="dropdown">下拉展开</a-radio-button>
                    <a-radio-button value="popover">浮层展开</a-radio-button>
                  </a-radio-group>
                </a-form-model-item>
              </div>

              <a-form-model-item label="显示面包屑">
                <a-switch v-model="wMenu.configuration.breadcrumbVisible" size="small" />
              </a-form-model-item>
            </a-form-model>
          </div>
          <div>
            <a-button block type="primary" @click="e => onSave(e)">保存</a-button>
          </div>
        </a-layout-sider>
        <a-layout-content>
          <a-page-header title="导航预览" :class="['preview-page-container', menuPosition == 'horizontal' ? 'menu-top' : '']">
            <template slot="extra">
              <a-button type="link" size="small" @click="previewModulePage">
                <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
                预览页面
              </a-button>
            </template>
            <WidgetVpage v-if="!loading" :widget="definitionJson" style="height: 100%" :key="wkey" />
          </a-page-header>
        </a-layout-content>
      </a-layout>
    </div>

    <div :class="['module-quick-add-nav', 'inner-drawer', drawerKey === 'quickAdd' ? 'visible' : '']">
      <a-card class="pt-card" title="快速添加导航" :bordered="false">
        <template slot="extra">
          <a-icon type="close" style="cursor: pointer" @click="closeQuickNav" />
        </template>
        <a-alert message="您可以根据模块的页面批量添加模块导航项" type="info" show-icon style="margin-bottom: 15px" />

        <div class="resource-sider">
          <PerfectScrollbar
            :style="{
              height: wMenu != undefined && wMenu.configuration.menus.length > 0 ? 'calc(100vh - 424px)' : 'calc(100vh - 324px)',
              marginRight: '-20px',
              paddingRight: '20px'
            }"
          >
            <template v-for="(res, i) in navSources">
              <a-row type="flex" class="level-one" :key="'nav_' + res.id">
                <a-col flex="100%">
                  <a-row type="flex" class="res-item-row level-one">
                    <a-col flex="auto">
                      <span style="width: 16px; display: inline-block">
                        <img
                          v-if="res.menus && res.menus.length"
                          class="svg-iconfont"
                          style="margin-right: var(--w-padding-2xs)"
                          src="/static/svg/folder-open.svg"
                        />
                        <Icon v-else type="pticon iconfont icon-ptkj-wenjian" />
                      </span>
                      <a-input size="small" v-model="res.title" style="width: 150px" />
                    </a-col>
                    <a-col flex="120px">
                      <Modal
                        title="选择图标"
                        dialogClass="pt-modal widget-icon-lib-modal"
                        :ref="'iconModalRef_' + i"
                        :width="640"
                        :bodyStyle="{ height: '560px' }"
                        :maxHeight="560"
                      >
                        <a-button class="icon-only" size="small">
                          <Icon :type="res.icon || 'pticon iconfont icon-ptkj-jiahao'" />
                        </a-button>
                        <template slot="content">
                          <WidgetIconLib v-model="res.icon" />
                        </template>
                      </Modal>
                      <a-button size="small" type="link" @click="iconSettingHandle(res, i)">
                        {{ res.icon ? '删除图标' : '添加图标' }}
                      </a-button>
                      <a-checkbox :checked="quickChecked.includes(res.id)" @change="onQuickChecked(res.id)" />
                    </a-col>
                  </a-row>
                </a-col>
                <a-col flex="100%" class="sub-res-item-col" v-if="res.menus && res.menus.length > 0">
                  <!-- 分组下的子元素 -->
                  <template v-for="(p, j) in res.menus">
                    <a-row type="flex" class="res-item-row level-two" :key="'subnav_' + p.id">
                      <a-col flex="auto">
                        <span style="min-width: 16px; display: inline-block">
                          <Icon type="pticon iconfont icon-ptkj-wenjian" style="color: var(--w-text-color-dark)" />
                        </span>
                        <a-input size="small" v-model="p.title" style="width: 150px" />
                      </a-col>
                      <a-col flex="120px">
                        <Modal
                          title="选择图标"
                          :ref="'iconModalRef_' + i + '_' + j"
                          dialogClass="pt-modal widget-icon-lib-modal"
                          :width="640"
                          :bodyStyle="{ height: '560px' }"
                          :maxHeight="560"
                        >
                          <a-button class="icon-only" size="small">
                            <Icon :type="p.icon || 'pticon iconfont icon-ptkj-jiahao'" />
                          </a-button>
                          <template slot="content">
                            <WidgetIconLib v-model="p.icon" />
                          </template>
                        </Modal>
                        <a-button size="small" type="link" @click="iconSettingHandle(p, i, j)">
                          {{ p.icon ? '删除图标' : '添加图标' }}
                        </a-button>
                        <a-checkbox :checked="quickChecked.includes(p.id)" @change="onQuickChecked(p.id)" />
                      </a-col>
                    </a-row>
                  </template>
                </a-col>
              </a-row>
            </template>
          </PerfectScrollbar>
        </div>
        <div class="quick-add-type-select" v-show="wMenu.configuration.menus.length > 0" v-if="wMenu != undefined">
          <div style="color: var(--w-text-color-dark); margin-bottom: 8px">当前模块已设置导航项, 请选择</div>
          <a-row :gutter="12" class="quick-add-type-select-row">
            <a-col :span="12">
              <div :class="quickAddTypeSelect == 'add' ? 'selected' : ''" @click="quickAddTypeSelect = 'add'">
                <span class="icon-div"><Icon type="pticon iconfont icon-luojizujian-xinzeng"></Icon></span>
                批量追加
              </div>
            </a-col>
            <a-col :span="12">
              <div :class="quickAddTypeSelect == 'replace' ? 'selected' : ''" @click="quickAddTypeSelect = 'replace'">
                <span class="icon-div"><Icon type="pticon iconfont icon-oa-qiehuan"></Icon></span>
                全部替换
              </div>
            </a-col>
          </a-row>
        </div>
      </a-card>
      <div class="footer">
        <a-checkbox :indeterminate="indeterminate" :checked="checkAll" @change="onCheckAllChange" style="float: left; margin-top: 7px">
          全选|已选
          <span style="color: var(--w-primary-color)">{{ quickChecked.length }}</span>
          个导航项
        </a-checkbox>
        <a-button type="primary" @click="addQuickCheckedNav">添加</a-button>
        <a-button @click="closeQuickNav">取消</a-button>
      </div>
    </div>

    <div :class="['inner-drawer', drawerKey == 'navDetail' ? 'visible' : '']" v-if="wLayout != undefined">
      <NavDetail
        :nav="currentNav"
        ref="navDetail"
        :key="navEditKey"
        :container-wid="wLayout.configuration.content.id"
        :widgetSource="widgetSource"
      />
      <div class="footer">
        <a-button type="primary" @click="saveEditNav">保存</a-button>
        <a-button @click="drawerKey = undefined">取消</a-button>
      </div>
    </div>
  </a-card>
</template>
<style lang="less"></style>
<script type="text/babel">
import '@installPageWidget';
import { generateId, jsonValue, deepClone } from '@framework/vue/utils/util';
import { camelCase } from 'lodash';
import md5 from '@framework/vue/utils/md5';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import ModuleDetail from '../module-detail.vue';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
import NavDetail from './nav-detail.vue';
import EWidgetLayoutConf from '../../../../widget/widget-layout/configuration/index.vue';
import EWidgetMenuConf from '../../../../widget/widget-menu/configuration/index.vue';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件

export default {
  name: 'ModuleIndexNav',
  inject: ['currentModule', 'pageContext', 'getPageResources'],
  props: {},
  components: {
    draggable: () => import(/* webpackChunkName: "vuedraggable" */ 'vuedraggable'),
    Modal,
    Drawer,
    NavDetail,
    WidgetIconLibModal,
    WI18nInput
  },
  computed: {},
  provide() {
    return {
      appId: this.currentModule.id,
      containerStyle: {
        height: 'calc(100vh - 250px)'
      }
      // $containerHeight: 'calc(100vh - 250px)'
    };
  },
  data() {
    return {
      wkey: 'preview',
      labelCol: {},
      wrapperCol: { style: { textAlign: 'right' } },
      definition: { designable: false },
      definitionJson: {},
      wMenu: { configuration: { menus: [] } },
      subMenuOpenType: 'dropdown',
      menuPosition: 'left',
      wLayout: { configuration: { content: { id: undefined } } },
      menuOpened: [],
      widgetMenuOptions: [],
      moduleMenuId: undefined,
      designableUpdating: false,
      resetting: false,
      loading: true,
      selectKey: undefined,
      menuIcon: {},
      quickChecked: [],
      navSources: [],
      drawerKey: undefined,
      quickAddTypeSelect: undefined,
      indeterminate: false,
      currentNav: { eventHandler: {} },
      currentNavSiblings: [],
      navEditKey: 'navEdit_0',
      allCheckKeys: [],
      checkAll: false,
      widgetSource: [],
      unknownDesign: false,
      pageType: '1',
      languageOptions: [],
      subTitle: `已开启模块导航的页面设计模式。当前模式下, 模块的页面列表中将显示 "模块主页", 您可以在该页面中使用页面设计器,
              自定义模块的导航布局、导航风格、导航项、模块内容区域等。 在访问模块时默认访问框架页面, 通过框架页中配置的导航,
              在框架页的内容区打开模块的功能页面`
    };
  },

  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getDefaultPageDef(() => {
      // this.buildNavSource();
    });
    this.fetchLocaleOptions();
  },
  mounted() {
    let _this = this;
    this.pageContext.offEvent('widget:mounted').handleEvent('widget:mounted', function (e) {
      for (let i = 0, len = _this.widgetSource.length; i < len; i++) {
        if (_this.widgetSource[i].id == e.widget.id) {
          return;
        }
      }
      _this.widgetSource.push(e.widget);
    });
  },
  methods: {
    changeTitleAndIcon() {
      this.wMenu.configuration.menuHeader =
        this.wMenu.configuration.menuTitleVisible || this.wMenu.configuration.menuTitleIcon != undefined;
      this.wMenu.configuration.headerContentType = 'iconTitle';
    },

    previewModulePage() {
      window.open(`/webpage/${this.definition.id}`, '_blank');
    },
    openEditNavDetail(nav, siblings, parentNav) {
      // 默认选中状态
      if (nav != undefined) {
        if (nav.id == this.wMenu.configuration.defaultSelectedKey) {
          nav.defaultSelected = true;
        } else {
          nav.defaultSelected = false;
        }
      }
      this.drawerKey = 'navDetail';
      this.currentNav = nav || { eventHandler: {} };
      this.currentNavSiblings = siblings;
      this.parentNav = parentNav;
      this.navEditKey = 'navEdit_' + new Date().getTime();
    },
    saveEditNav() {
      let form = this.$refs.navDetail.form;
      if (this.currentNav.id == undefined) {
        // 新增
        let menus = this.currentNavSiblings || this.wMenu.configuration.menus,
          eventHandler = {};
        if (form.eventHandler) {
          eventHandler = form.eventHandler;
        }
        let menu = Object.assign({}, form, {
          title: form.title,
          icon: form.icon,
          hidden: form.hidden,
          badge: form.badge,
          eventHandler,
          menus: []
        });
        if (this.parentNav != undefined) {
          menu.pid = this.parentNav.id;
        }
        menus.push(menu);
        if (form.defaultSelected) {
          this.wMenu.configuration.defaultSelectedKey = menu.id;
        }
      } else {
        this.currentNav.title = form.title;
        this.currentNav.icon = form.icon;
        this.currentNav.hidden = form.hidden;
        this.currentNav.badge = form.badge;
        if (form.eventHandler) {
          this.currentNav.eventHandler = form.eventHandler;
        } else {
          this.currentNav.eventHandler = {};
        }
        Object.assign(this.currentNav, form);
        if (form.defaultSelected) {
          this.wMenu.configuration.defaultSelectedKey = this.currentNav.id;
        } else if (this.currentNav.id == this.wMenu.configuration.defaultSelectedKey) {
          // 取消默认选中
          this.wMenu.configuration.defaultSelectedKey = null;
        }
      }
      this.drawerKey = undefined;
    },
    onCheckAllChange(e) {
      Object.assign(this, {
        indeterminate: false,
        checkAll: e.target.checked
      });
      this.quickChecked = [];
      if (e.target.checked) {
        this.quickChecked = [].concat(this.allCheckKeys);
      }
    },
    openQuickAddNavDrawer() {
      this.drawerKey = 'quickAdd';
      this.quickChecked = [];
      this.quickAddTypeSelect = undefined;
      this.buildNavSource();
    },
    addQuickCheckedNav() {
      let menus = [],
        _length = this.wMenu.configuration.menus.length,
        override = _length == 0 || this.quickAddTypeSelect == 'replace';
      if (this.quickAddTypeSelect == undefined && _length != 0) {
        this.$message.info('请选择添加方式');
        return;
      }
      let indexMenu = null;
      for (let n of this.navSources) {
        let m = null;
        if (this.quickChecked.includes(n.id)) {
          m = deepClone(n);
          m.menus = [];
          m.level = 1;
          if (m.index) {
            // 首页导航单独出来，添加到第一个
            indexMenu = m;
            continue;
          }
          menus.push(m);
        }
        if (n.menus != undefined) {
          for (let nn of n.menus) {
            if (this.quickChecked.includes(nn.id)) {
              let _nn = deepClone(nn);
              _nn.level = 2;
              _nn.pid = m && m.id;
              _nn.menus = [];
              if (m == null) {
                menus.push(_nn);
              } else {
                m.menus.push(_nn);
              }
            }
          }
        }
      }
      console.log('当前添加的导航信息: ', menus);
      if (override) {
        if (indexMenu) {
          // 首页导航放第一个
          menus.splice(0, 0, indexMenu);
        }
        this.$set(this.wMenu.configuration, 'menus', [].concat(menus));
      } else {
        // 批量追加
        if (indexMenu) {
          // 首页导航放第一个
          this.wMenu.configuration.menus.splice(0, 0, indexMenu);
        }
        this.wMenu.configuration.menus.push(...menus);
      }
      // 添加后，默认展开所有菜单
      this.openAllMenus();

      this.closeQuickNav();
    },
    openAllMenus() {
      this.menuOpened = [];
      let _open = list => {
        if (list) {
          for (let l of list) {
            if (l.menus && l.menus.length > 0) {
              this.menuOpened.push(l.id);
              _open.call(this, l.menus);
            }
          }
        }
      };
      _open.call(this, this.wMenu.configuration.menus);
    },
    closeQuickNav() {
      this.quickAddTypeSelect == undefined;
      this.quickChecked = [];
      this.drawerKey = undefined;
      this.checkAll = false;
      this.indeterminate = false;
    },
    resetPageDefinition() {
      this.resetting = true;
      let { widget, widgetElements } = ModuleDetail.methods.generateModulePageDefinitionJson();
      this.definitionJson.items = [widget];
      this.definitionJson.appWidgetDefinitionElements = widgetElements;
      this.definition.appWidgetDefinitionElements = widgetElements;
      this.onSave(null, '重置成功', () => {
        this.resetting = false;
        this.getDefaultPageDef(() => {
          // this.buildNavSource();
        });
      });
    },
    onClickToEditPage() {
      window.open(`/page-designer/index?uuid=${this.definition.uuid}`, '_blank');
    },
    onChangeDesignable(checked) {
      this.designableUpdating = true;
      $axios
        .get(`/proxy/api/webapp/page/definition/updateDesignable`, {
          params: { uuid: this.definition.uuid, designable: this.definition.designable }
        })
        .then(({ data }) => {
          this.designableUpdating = false;
          if (data.code !== 0) {
            this.definition.designable = !this.definition.designable;
          } else {
            this.pageContext.emitEvent(`ModulePageResource:UpdateModulePageDesignable`, this.definition.designable);
          }
        })
        .catch(error => {
          this.designableUpdating = false;
          this.definition.designable = !this.definition.designable;
        });
    },
    onChangeModuleMenuId() {
      let $wgt = this.pageContext.getVueWidgetById(this.moduleMenuId);
      if ($wgt) {
        $wgt.widget.main = true;
      }
      for (let opt of this.widgetMenuOptions) {
        if (opt.value != this.moduleMenuId) {
          $wgt = this.pageContext.getVueWidgetById(opt.value);
          if ($wgt) {
            delete $wgt.widget.main;
          }
        }
      }
    },
    onSave(e, successText, callback) {
      this.$loading('保存中');
      if (e != null) {
        let setLevel = (list, level) => {
          if (list) {
            for (let l of list) {
              l.level = level;
              if (l.menus) {
                setLevel(l.menus, level + 1);
              }
            }
          }
        };
        setLevel(this.wMenu.configuration.menus, 1);
        let widgetElements = EWidgetLayoutConf.methods.getWidgetDefinitionElements(this.wLayout);
        widgetElements = widgetElements.concat(EWidgetMenuConf.methods.getWidgetDefinitionElements(this.wMenu));

        let i18n = EWidgetMenuConf.methods.getWidgetI18nObjects(this.wMenu),
          rows = [];
        for (let i = 0, len = i18n.length; i < len; i++) {
          for (let opt of this.languageOptions) {
            if (i18n[i][opt.value]) {
              for (let code in i18n[i][opt.value]) {
                rows.push({ elementId: this.wMenu.id, code, locale: opt.value, content: i18n[i][opt.value][code] });
              }
            }
          }
        }

        widgetElements[widgetElements.length - 1].i18ns = rows;
        this.definition.appWidgetDefinitionElements = widgetElements;
        this.definition.functionElements = EWidgetMenuConf.methods.getFunctionElements(this.wMenu);
        this.definitionJson.appWidgetDefinitionElements = widgetElements;
        this.definitionJson.functionElements = this.definition.functionElements;
      }

      // 菜单级别更新
      this.definition.definitionJson = JSON.stringify(this.definitionJson);
      this.$axios
        .post('/web/design/savePageDefinition', this.definition)
        .then(({ data }) => {
          this.$loading(false);
          if (data.code == 0) {
            this.$message.success(successText || '保存成功');
            if (typeof callback == 'function') {
              callback.call(this);
            }
          }
        })
        .catch(() => {
          this.$loading(false);
        });
    },
    fetchLocaleOptions() {
      return new Promise((resolve, reject) => {
        this.$tempStorage.getCache(
          'allLocaleOptions',
          () => {
            return new Promise((resolve, reject) => {
              this.$axios
                .get(`/proxy/api/app/codeI18n/getAllLocales`, { params: {} })
                .then(({ data }) => {
                  let options = [];
                  if (data.code == 0) {
                    for (let d of data.data) {
                      options.push({
                        label: d.name,
                        value: d.locale,
                        description: d.remark || d.name,
                        transCode: d.translateCode
                      });
                    }
                  }
                  resolve(options);
                })
                .catch(error => {});
            });
          },
          results => {
            this.languageOptions = results;
            resolve(results);
          }
        );
      });
    },
    onChangeMenuPosition(e) {
      if (this.menuPosition == 'horizontal') {
        this.wMenu.configuration.mode = 'horizontal';
        this.wMenu.configuration.menuHeader = false;
      } else {
        // 左侧，要根据子菜单交互
        this.wMenu.configuration.mode = this.subMenuOpenType === 'dropdown' ? 'inline' : 'vertical';
        this.wMenu.configuration.menuHeader = true;
      }
      if (this.wMenu.configuration.mode == 'horizontal') {
        // 把导航组件移到布局组件的头部里
        this.wLayout.configuration.layoutType = 'topMiddleBottom';
        this.wLayout.configuration.header.configuration.visible = true;
        this.wLayout.configuration.header.configuration.widgets = [this.wMenu];
        this.wLayout.configuration.sider.configuration.visible = false;
        this.wLayout.configuration.sider.configuration.widgets = [];
      } else {
        this.wLayout.configuration.layoutType = 'topMiddleSiderBottom';
        this.wLayout.configuration.sider.configuration.visible = true;
        this.wLayout.configuration.sider.configuration.widgets = [this.wMenu];
        this.wLayout.configuration.header.configuration.visible = false;
        this.wLayout.configuration.header.configuration.widgets = [];
      }
    },
    onChangeSubMenuOpenType() {
      this.wMenu.configuration.mode = this.subMenuOpenType === 'dropdown' ? 'inline' : 'vertical';
    },

    toggleMenu(key) {
      let idx = this.menuOpened.indexOf(key);
      if (idx == -1) {
        this.menuOpened.push(key);
      } else {
        this.menuOpened.splice(idx, 1);
      }
    },
    expandMenu(key) {
      let idx = this.menuOpened.indexOf(key);
      if (idx == -1) {
        this.menuOpened.push(key);
      }
    },
    collapseMenu(key) {
      let idx = this.menuOpened.indexOf(key);
      if (idx != -1) {
        this.menuOpened.splice(idx, 1);
      }
    },
    canDragPut(putOnDraggable, pullOff, dragItem) {
      console.log(arguments);
      let draggedMenu = dragItem._underlying_vm_;
      if (draggedMenu.menus == undefined || draggedMenu.menus.length == 0) {
        return true;
      }
      // 判断拖入的元素是否满足拖入后导航层级不超过3级
      let putOnLevel = parseInt(putOnDraggable.el.dataset.level);
      if (putOnLevel == 3) {
        return false;
      }
      if (putOnLevel == 2) {
        let allowed = true;
        for (let m of draggedMenu.menus) {
          if (m.menus && m.menus.length > 0) {
            allowed = false;
            break;
          }
        }
        return allowed;
      }

      if (putOnLevel == 1) {
        let allowed = true;
        for (let m of draggedMenu.menus) {
          if (m.menus && m.menus.length > 0) {
            for (let sm of m.menus) {
              if (sm.menus && sm.menus.length > 0) {
                allowed = false;
                break;
              }
            }
            if (!allowed) {
              break;
            }
          }
        }
        return allowed;
      }

      return false;
    },
    refreshIndexPage() {
      // 获取导航组件
      let wMenu = this.pageContext.getVueWidgetById(this.widgetMenuId),
        wLayout = this.pageContext.getVueWidgetById(this.widgetLayoutId);
      if (wMenu && wLayout) {
        wMenu.widget.configuration.menus = this.menus;
        wMenu.widget.configuration.mode = this.navSetting.subMenuOpenType == 'popover' ? 'vertical' : 'inline';
        wMenu.openKeys = [];
        if (this.navSetting.titleVisible) {
          wMenu.widget.configuration.menuTitleVisible = this.navSetting.titleVisible;
          wMenu.widget.configuration.menuTitle = this.navSetting.title;
        }
        if (this.navSetting.layout == 'menuTop') {
          // 把导航组件移到布局组件的头部里
          wLayout.widget.configuration.layoutType = 'topMiddleBottom';
          wLayout.widget.configuration.header.configuration.visible = true;
          wLayout.widget.configuration.header.configuration.widgets = [wMenu.widget];
          wMenu.widget.configuration.mode = 'horizontal';
        } else {
          wLayout.widget.configuration.layoutType = 'topMiddleSiderBottom';
          wLayout.widget.configuration.sider.configuration.visible = true;
          wLayout.widget.configuration.sider.configuration.widgets = [wMenu.widget];
          wLayout.widget.configuration.header.configuration.visible = false;
          wLayout.widget.configuration.header.configuration.widgets = [];
        }
      }
    },
    getDefaultPageDef(callback) {
      let _this = this;
      $axios
        .get(`/proxy/api/webapp/page/definition/getDefaultPageDefinition`, { params: { appId: this.currentModule.id, isPc: true } })
        .then(({ data }) => {
          if (data.code == 0) {
            let init = data => {
              this.definition = data;
              this.definitionJson = JSON.parse(data.definitionJson);
              // this.initJson = JSON.stringify(this.definitionJson);
              if (this.definitionJson.items.length == 0) {
                ModuleDetail.methods
                  .createModulePage(generateId('SF'), 'vPage', _this.currentModule.name, _this.currentModule.id)
                  .then(() => {
                    _this.getDefaultPageDef(callback);
                  });
                return;
              }
              let findWidget = (obj, filter) => {
                if (obj != undefined) {
                  if (Array.isArray(obj)) {
                    for (let i = 0, len = obj.length; i < len; i++) {
                      let _get = findWidget(obj[i], filter);
                      if (_get) {
                        return _get;
                      }
                    }
                  } else if (typeof obj == 'object') {
                    if (filter(obj)) {
                      return obj;
                    } else {
                      for (let key in obj) {
                        let _get = findWidget(obj[key], filter);
                        if (_get) {
                          return _get;
                        }
                      }
                    }
                  }
                }
              }; // 查找模块导航组件
              this.wMenu = findWidget(this.definitionJson.items[0], obj => {
                return obj.wtype == 'WidgetMenu' && obj.main;
              });
              if (this.wMenu == undefined) {
                // 非标准化的模块布局设计，无法设计
                this.loading = false;
                this.unknownDesign = true;
                return;
              }
              this.moduleMenuId = this.wMenu.id;
              this.wLayout = findWidget(this.definitionJson.items[0], obj => {
                return obj.wtype == 'WidgetLayout' && obj.main;
              });
              this.subMenuOpenType = this.wMenu.configuration.mode != 'inline' ? 'popover' : 'dropdown';
              this.menuPosition = this.wMenu.configuration.mode == 'horizontal' ? 'horizontal' : 'left';
              this.loading = false;
              // 获取导航项
              let widgetElements = this.definitionJson.appWidgetDefinitionElements || this.definitionJson.widgetElements;
              this.widgetMenuOptions = [];
              for (let ele of widgetElements) {
                if (ele.wtype == 'WidgetMenu') {
                  this.widgetMenuOptions.push({
                    label: ele.title,
                    value: ele.id
                  });
                }
              }
              if (typeof callback == 'function') {
                callback.call(this);
              }
            };

            if (data.data) {
              init.call(_this, data.data);
            } else {
              ModuleDetail.methods.createModulePage(generateId('SF'), 'vPage', this.currentModule.name, this.currentModule.id).then(() => {
                _this.getDefaultPageDef(callback);
              });
            }
          }
        })
        .catch(error => {});
    },

    generateEventHandler(params) {
      return {
        trigger: 'click',
        actionType: 'redirectPage',
        actionTypeName: '页面跳转',
        targetPosition: 'widgetLayout',
        containerWid: this.wLayout.configuration.content.id,
        ...params
      };
    },

    buildNavSource() {
      this.navSources.splice(0, this.navSources.length);
      // 默认添加首页
      this.navSources.push({
        id: generateId(),
        index: true,
        title: '首页',
        hidden: false,
        eventHandler: {
          trigger: 'click',
          actionType: 'widgetEvent',
          actionTypeName: '组件事件',
          eventWid: this.wLayout.id,
          eventId: 'restoreLayoutContent'
        },
        icon: 'pticon iconfont icon-szgy-zhuye'
      });
      this.allCheckKeys.push(this.navSources[0].id);
      let resources = this.getPageResources();
      for (let r of resources) {
        let nav = {
          id: r.id || r.uuid,
          title: r.name,
          hidden: false,
          icon: undefined,
          eventHandler: {},
          menus: []
        };

        if (r.children != undefined) {
          this.navSources.push(nav);
          this.allCheckKeys.push(nav.id);
          for (let c of r.children) {
            if (c._PAGE_TYPE == 'PC' && ['page', 'link'].includes(c._RES_TYPE)) {
              let _subNav = {
                id: c.id || c.uuid,
                title: c.name,
                hidden: false,
                icon: undefined,
                eventHandler: {},
                menus: []
              };
              _subNav.eventHandler = this.generateEventHandler({
                pageType: c._RES_TYPE == 'page' ? 'page' : 'url',
                url: c._RES_TYPE == 'link' ? c.url : undefined,
                pageId: c._RES_TYPE == 'page' ? c.id : undefined,
                pageName: c._RES_TYPE == 'page' ? c.name : undefined
              });
              nav.menus.push(_subNav);
              this.allCheckKeys.push(_subNav.id);
            }
          }
        } else if (r._PAGE_TYPE == 'PC' && ['page', 'link'].includes(r._RES_TYPE)) {
          nav.eventHandler = this.generateEventHandler({
            pageType: r._RES_TYPE == 'page' ? 'page' : 'url',
            url: r._RES_TYPE == 'link' ? r.url : undefined,
            pageId: r._RES_TYPE == 'page' ? r.id : undefined,
            pageName: r._RES_TYPE == 'page' ? r.name : undefined
          });
          this.navSources.push(nav);
          this.allCheckKeys.push(nav.id);
        }
      }
      console.log('初始化模块导航数据: ', this.navSources);
    },
    onQuickChecked(key) {
      let idx = this.quickChecked.indexOf(key);
      if (idx == -1) {
        this.quickChecked.push(key);
      } else {
        this.quickChecked.splice(idx, 1);
      }
      this.triggerCheckIndeterminate();
    },
    triggerCheckIndeterminate() {
      this.indeterminate = !!this.quickChecked.length && this.quickChecked.length < this.allCheckKeys.length;
      this.checkAll = this.quickChecked.length === this.allCheckKeys.length;
    },
    iconSettingHandle(item, i, j) {
      if (item.icon) {
        // 删除
        if (j === undefined) {
          this.navSources[i].icon = '';
        } else {
          this.navSources[i].menus[j].icon = '';
        }
      } else {
        if (j === undefined) {
          this.$refs['iconModalRef_' + i][0].modalVisible = true;
        } else {
          this.$refs['iconModalRef_' + i + '_' + j][0].modalVisible = true;
        }
      }
    }
  },
  watch: {
    wMenu: {
      deep: true,
      handler(v) {
        console.log('菜单导航变更: ', v);
        if (v != undefined) {
          this.wkey = md5(JSON.stringify(v));
        }
      }
    }
  }
};
</script>
