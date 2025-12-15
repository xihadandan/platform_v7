<template>
  <HtmlWrapper :title="vTitle">
    <a-layout class="widget-design-layout" v-if="designable">
      <a-layout-header class="widget-design-header">
        <a-row>
          <a-col :span="8">
            <a-avatar
              shape="square"
              :size="24"
              style="background-color: #fff; color: var(--w-primary-color); margin-right: 8px; margin-top: -4px"
            >
              <Icon
                :type="layoutFixed ? 'pticon iconfont icon-szgy-zhuye' : 'pticon iconfont icon-ptkj-yemian'"
                style="font-weight: normal; vertical-align: top; font-size: 16px"
              />
            </a-avatar>
            <span style="font-weight: bold; font-size: var(--w-font-size-lg)">
              {{
                functionWidgetDesign
                  ? pageDefinition.title || '组件设计'
                  : layoutFixed
                  ? pageDefinition.name || '首页设计'
                  : pageDefinition.name || '页面设计'
              }}
            </span>
          </a-col>
          <a-col :span="8" style="text-align: center">
            <!-- 考虑到首页差异较大的原因，首页设计暂不进行pc端兼容设计 -->
            <!-- FIXME: PC端页面是否考虑做兼容设计，取决于二者页面的可复用性、差异性，暂时放开兼容设计，实际以移动端兼容渲染为最终渲染结果为准 -->
            <a-radio-group
              v-if="isIndexDesign == undefined && !uniDesign"
              v-model="designer.terminalType"
              button-style="solid"
              class="design-terminal-type-radio radio-ghost"
            >
              <template v-for="(terminalOpt, t) in terminalOptions">
                <a-radio-button :value="terminalOpt.value" style="width: 100px" :key="'terminalOpt-' + t">
                  <i class="pticon iconfont icon-ptkj-dagou" v-show="designer.terminalType == terminalOpt.value" />
                  {{ terminalOpt.label }}
                </a-radio-button>
              </template>
            </a-radio-group>
          </a-col>
          <a-col :span="8" :style="{ textAlign: 'right' }">
            <ExportDef :uuid="pageDefinition.uuid" type="appPageDefinition">
              <a-button ghost style="margin-right: 8px">导出</a-button>
            </ExportDef>
            <ImportDef>
              <a-button ghost style="margin-right: 8px">导入</a-button>
            </ImportDef>
            <template v-if="functionWidgetDesign">
              <a-button @click="saveFunctionWidgetDef" ghost>保存</a-button>
            </template>
            <template v-else>
              <a-button @click="e => savePageDesign(false)" ghost style="margin-right: 8px">保存</a-button>
              <a-button
                type="primary"
                @click="e => savePageDesign(true)"
                v-if="pageDefinition.uuid != null"
                ghost
                style="margin-right: 8px"
              >
                保存新版本
              </a-button>
              <a-button type="primary" @click="saveTemp" ghost>本地暂存</a-button>
              <!-- <a-select style="width: 120px" v-model="$i18n.locale" @change="changeLocale">
                <a-select-option value="zh_CN">中文简体</a-select-option>
                <a-select-option value="zh_TC">中文繁體</a-select-option>
                <a-select-option value="ru_RU">Русский язык</a-select-option>
                <a-select-option value="en_US">English</a-select-option>
                <a-select-option value="ja_JP">日本語</a-select-option>
              </a-select> -->
            </template>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout :hasSider="true">
        <a-layout-sider theme="light" :width="345">
          <div class="spin-center" v-if="widgetMetaLoading">
            <a-spin />
          </div>
          <WidgetSelectionPanel
            v-else
            :designer="designer"
            allowTemplate
            :designWidgets="designWidgets"
            :designType="designer.terminalType == 'mobile' ? 'mobilePage' : 'page'"
            :defaultActiveTab="layoutFixed ? 'menu_tab' : undefined"
          >
            <template slot="addonBeforeTabs">
              <a-tab-pane key="menu_tab" v-if="layoutFixed" :forceRender="true">
                <span slot="tab">
                  <i class="pticon iconfont icon-ptkj-qiehuanshitu"></i>
                  <br />
                  导航
                </span>
                <ProdIndexNavDesign
                  :systemInfo="systemInfo"
                  :prodVersionUuid="prodVersionUuid"
                  :designer="designer"
                  v-if="editorInstalled && prodVersionUuid"
                  @layoutConfChange="onProdLayoutConfChange"
                  @navChanged="onProdNavChanged"
                  ref="prodIndexNavDesign"
                />
              </a-tab-pane>
              <a-tab-pane key="func_tab" v-if="layoutFixed">
                <span slot="tab">
                  <i class="pticon iconfont icon-ptkj-fenlei"></i>
                  <br />
                  功能
                </span>
                <ProdModuleFuncWidgetSelect :prodVersionId="prodVersion.versionId" :designer="designer" />
              </a-tab-pane>
            </template>
          </WidgetSelectionPanel>
        </a-layout-sider>
        <a-layout-content id="design-main" :class="[designer.terminalType == 'mobile' ? 'mobile' : '']">
          <WidgetBuildPanel
            :designer="designer"
            ref="buidPanel"
            :allowClear="!functionWidgetDesign && !layoutFixed"
            :allowSaveAsTemplate="!functionWidgetDesign && !layoutFixed"
            :allowPageVars="true"
            :design-style="previewPageStyle ? pageStyle : {}"
            :loading="!editorInstalled || !layoutConfLoaded || widgetMetaLoading"
            :page-id="pageDefinition.id"
            :customDndContainer="designer.terminalType == 'mobile'"
          >
            <template slot="preview-btn-slot">
              <a-button type="link" :icon="designer.terminalType == 'mobile' ? 'mobile' : 'desktop'" @click="preview" size="small">
                预览
              </a-button>
            </template>
            <template slot="dndContainer" v-if="designer.terminalType == 'mobile'">
              <MobileDesignContainer :designer="designer" :loading="!editorInstalled || loading" />
              <MobilePreview
                :designer="designer"
                :previewJson="getPreviewJson"
                v-model="mobilePreviewUiVisible"
                width="100%"
                :h5Url="h5Server"
                :pageUrl="getMobilePageUrl"
                ref="mobilePreview"
              ></MobilePreview>
            </template>
          </WidgetBuildPanel>
        </a-layout-content>
        <a-layout-sider theme="light" class="widget-configuration-sider" :width="380">
          <WidgetConfigurationPanel :designer="designer" ref="wConfigurePanel">
            <template slot="basicInfoTab">
              <a-tab-pane key="basicInfo" :tab="!functionWidgetDesign ? '页面信息' : '基本信息'">
                <a-tabs size="small" class="flex-card-tabs" @change="onChangePageInfoTabs">
                  <a-tab-pane key="pagePropInfo" tab="属性" forceRender>
                    <a-form-model
                      class="basic-info"
                      :model="pageDefinition"
                      labelAlign="left"
                      ref="basicForm"
                      :rules="rules"
                      :label-col="{ span: 7 }"
                      :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
                      :colon="false"
                    >
                      <template v-if="!functionWidgetDesign">
                        <a-form-model-item label="名称" prop="name">
                          <a-input v-model="pageDefinition.name" allow-clear>
                            <template slot="addonAfter">
                              <WI18nInput code="name" :target="pageDefinition" v-model="pageDefinition.name" />
                            </template>
                          </a-input>
                        </a-form-model-item>
                        <a-form-model-item label="ID" prop="id">
                          <a-input v-model="pageDefinition.id" :readOnly="pageDefinition.uuid != null" :maxLength="64" />
                        </a-form-model-item>
                        <a-form-model-item label="页面脚本">
                          <JsModuleSelect :multiSelect="false" v-model="designer.pageJsModule" dependencyFilter="VuePageDevelopment" />
                        </a-form-model-item>
                        <a-form-model-item label="版本" v-show="pageDefinition.uuid != null">
                          <a-input :value="'v' + pageDefinition.version" disabled />
                        </a-form-model-item>
                        <a-form-model-item label="编号">
                          <a-input v-model="pageDefinition.code" />
                        </a-form-model-item>
                      </template>

                      <a-form-model-item label="标题">
                        <a-input v-model="pageDefinition.title">
                          <template slot="addonAfter">
                            <WI18nInput code="title" :target="pageDefinition" v-model="pageDefinition.title" />
                          </template>
                        </a-input>
                      </a-form-model-item>
                      <a-form-model-item
                        class="page-belong"
                        prop="appId"
                        :label-col="
                          prodVersionUuid
                            ? {
                                style: {
                                  width: 'fit-content'
                                }
                              }
                            : null
                        "
                        :wrapper-col="
                          prodVersionUuid
                            ? {
                                span: 10,
                                style: { textAlign: 'right' }
                              }
                            : null
                        "
                      >
                        <template v-if="!functionWidgetDesign && !layoutFixed && prodVersionUuid == undefined">
                          <template slot="label">
                            <span
                              style="cursor: pointer"
                              :class="pageDefinition.appId ? 'ant-btn-link' : ''"
                              @click="redirectModuleAssemble(pageDefinition.appId)"
                              :title="pageDefinition.appId ? '打开归属模块' : ''"
                            >
                              归属模块
                              <a-icon type="environment" v-show="pageDefinition.appId" style="color: inherit; line-height: 1" />
                            </span>
                          </template>
                          <a-select
                            :options="moduleOptions"
                            v-model="pageDefinition.appId"
                            showSearch
                            :filter-option="filterOption"
                            :disabled="pageDefinition.uuid != undefined"
                            @change="onChangeAppIdSelect"
                          />
                        </template>
                        <template v-else-if="prodVersionUuid != undefined">
                          <template slot="label">
                            <span
                              style="cursor: pointer"
                              :class="product.uuid ? 'ant-btn-link' : ''"
                              @click="redirectProductAssemble(product, systemInfo)"
                              :title="product.uuid ? ('打开' + systemInfo != undefined ? '归属系统' : '归属产品版本') : ''"
                            >
                              {{ systemInfo != undefined ? '归属系统' : '归属产品版本' }}
                              <a-icon type="environment" v-show="product.uuid" style="color: inherit; line-height: 1" />
                            </span>
                          </template>
                          {{ product.name }} - {{ prodVersion.version }}
                        </template>
                      </a-form-model-item>
                      <!-- <a-form-model-item label="PC端状态">
                        <a-switch
                          :checked="pageDefinition.isPc == null || pageDefinition.isPc == '1'"
                          checked-children="启用"
                          un-checked-children="禁用"
                          @change="onSwitchChange"
                        />
                      </a-form-model-item> -->
                      <a-form-model-item label="备注">
                        <a-textarea v-model="pageDefinition.remark" :auto-size="{ minRows: 5, maxRows: 5 }" allow-clear />
                      </a-form-model-item>
                    </a-form-model>
                  </a-tab-pane>
                  <a-tab-pane key="pageResource" tab="资源" v-if="!functionWidgetDesign">
                    <a-table
                      :pagination="false"
                      rowKey="uuid"
                      :columns="resColumns"
                      size="small"
                      :data-source="pageResources"
                      :expanded-row-keys.sync="expandedResRowKeys"
                      :scroll="{ y: 'calc(100vh - 200px)' }"
                    >
                      <template slot="titleSlot" slot-scope="text, record">
                        <span
                          :title="text"
                          :style="{
                            display: 'inline-block',
                            width: record.children != undefined ? '180px' : '160px',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap',
                            overflow: 'hidden',
                            verticalAlign: 'bottom'
                          }"
                        >
                          {{ text }}
                        </span>
                      </template>
                      <template slot="protectedSlot" slot-scope="text, record">
                        <a-switch
                          v-if="record.isProtected != undefined"
                          v-model="record.isProtected"
                          size="small"
                          @change="e => onChangeResProtected(e, record)"
                        />
                      </template>
                    </a-table>
                  </a-tab-pane>
                  <a-tab-pane key="pageParams" tab="参数">
                    <div class="page-param-container">
                      <PerfectScrollbar style="max-height: calc(100vh - 200px); height: auto">
                        <draggable
                          v-model="pageParams"
                          :group="{ name: 'pageParams', pull: true, put: false }"
                          animation="300"
                          handle=".drag-btn-handler"
                        >
                          <template v-for="(p, i) in pageParams">
                            <a-row type="flex" style="" :key="'pageParam_row_' + i" class="page-params-row">
                              <a-col flex="auto">
                                <div
                                  style="width: 180px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis"
                                  :title="p.name || p.code"
                                  :class="p.required ? 'required' : ''"
                                >
                                  {{ p.name || p.code }}
                                </div>
                              </a-col>
                              <a-col flex="100px">
                                <WidgetDesignDrawer :id="'pageParamDrawer_' + i" title="编辑参数" :designer="designer">
                                  <a-button size="small" type="link" title="编辑参数">
                                    <Icon type="pticon iconfont icon-ptkj-shezhi" />
                                  </a-button>
                                  <template #content><PageParamDetail :detail="p" /></template>
                                </WidgetDesignDrawer>
                                <a-button size="small" type="link" @click="pageParams.splice(i, 1)" title="删除">
                                  <Icon type="pticon iconfont icon-ptkj-shanchu" />
                                </a-button>
                                <span class="drag-btn-handler ant-btn ant-btn-link ant-btn-sm" title="拖动排序">
                                  <Icon type="pticon iconfont icon-ptkj-tuodong" />
                                </span>
                              </a-col>
                            </a-row>
                          </template>
                        </draggable>
                      </PerfectScrollbar>
                      <WidgetDesignDrawer :id="'pageParamDrawer_add'" title="添加参数" :designer="designer">
                        <a-button icon="plus" type="link" size="small" @click="prepareAddNewParam">添加参数</a-button>
                        <template #content>
                          <PageParamDetail :detail="newPageParam" />
                        </template>
                        <template #footer>
                          <a-button @click="confirmAddNewPageParam">确定</a-button>
                        </template>
                      </WidgetDesignDrawer>
                    </div>
                  </a-tab-pane>
                  <a-tab-pane key="pageStyle" tab="样式" v-if="!functionWidgetDesign">
                    <!-- <div style="text-align: right; padding: 0px 20px 10px">
                      <a-switch v-model="previewPageStyle" size="small" title="预览样式">
                        <a-icon type="eye" slot="checkedChildren" />
                        <a-icon type="eye" slot="unCheckedChildren" />
                      </a-switch>
                    </div> -->
                    <a-form-model
                      class="basic-info"
                      labelAlign="left"
                      :label-col="{ span: 7 }"
                      :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
                      :colon="false"
                    >
                      <a-form-model-item label="预览样式">
                        <a-switch v-model="previewPageStyle"></a-switch>
                      </a-form-model-item>
                      <a-form-model-item label="启用背景">
                        <a-switch v-model="pageStyle.enableBackground"></a-switch>
                      </a-form-model-item>
                      <template v-if="pageStyle.enableBackground">
                        <a-form-model-item label="背景颜色">
                          <ColorPicker v-model="pageStyle.backgroundColor" :allowClear="true" />
                        </a-form-model-item>
                        <a-form-model-item label="背景图片" class="item-lh"></a-form-model-item>
                        <a-form-model-item>
                          <ImageLibrary v-model="pageStyle.backgroundImage" width="100%" />
                        </a-form-model-item>
                        <a-form-model-item label="背景位置">
                          <a-select
                            showSearch
                            :options="[
                              { value: 'top', label: 'top' },
                              { value: 'center', label: 'center' },
                              { value: 'left', label: 'left' },
                              { value: 'right', label: 'right' }
                            ]"
                            v-model.trim="pageStyle.backgroundPosition"
                            :filter-option="filterOption"
                            placeholder="请输入背景位置样式"
                            allow-clear
                          />
                        </a-form-model-item>
                        <a-form-model-item label="背景重复">
                          <a-select
                            showSearch
                            :options="[
                              { value: 'no-repeat', label: 'no-repeat' },
                              { value: 'repeat-x', label: 'repeat-x' },
                              { value: 'repeat-y', label: 'repeat-y' },
                              { value: 'repeat', label: 'repeat' },
                              { value: 'space', label: 'space' },
                              { value: 'round', label: 'round' }
                            ]"
                            v-model.trim="pageStyle.backgroundRepeat"
                            :filter-option="filterOption"
                            placeholder="请输入背景重复"
                            allow-clear
                          />
                        </a-form-model-item>
                      </template>
                    </a-form-model>
                  </a-tab-pane>
                  <!-- <a-tab-pane key="pageEvent" tab="事件"></a-tab-pane> -->
                </a-tabs>
              </a-tab-pane>
            </template>
          </WidgetConfigurationPanel>
        </a-layout-sider>
      </a-layout>
    </a-layout>
    <a-result v-else status="403" title="无法编辑设计">
      <template #subTitle>
        模块主页未开启页面设计模式, 无法进行编辑设计, 可前往模块设置/模块导航开启页面设计。
        <a-button type="link" size="small" @click.stop="redirectModuleAssemble(pageDefinition.appId, '_self')">[ 前往模块装配 ]</a-button>
      </template>
    </a-result>
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import '../../assets/css/design.less';
import { orderBy, camelCase, set } from 'lodash';
import { addWindowResizeHandler, generateId, queryString, deepClone } from '@framework/vue/utils/util';
import { customFileUploadRequest } from '@framework/vue/utils/function';
import WidgetSelectionPanel from './component/widget-select-panel.vue';
import WidgetBuildPanel from './component/widget-build-panel.vue';
import WidgetConfigurationPanel from './component/widget-configuration-panel.vue';
import { createDesigner } from '@framework/vue/designer/designer';
import '@framework/vue/designer/install.js';
import ThemeSet from '@pageAssembly/app/web/lib/theme-set.vue';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
import PageParamDetail from './component/page-param-detail.vue';
import MobileDesignContainer from './component/mobile-design-container.vue';
import MobilePreview from './component/mobile-preview.vue';
import { toPng } from 'html-to-image';
import ProdIndexNavDesign from './component/nav/prod-index-nav-design.vue';
import ProdModuleFuncWidgetSelect from './component/nav/prod-module-func-widget-select.vue';
import JsModuleSelect from '../../widget/commons/js-module-select.vue';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
export default {
  name: 'PageDesigner',
  data() {
    return {
      scrollerHeight: '900px',
      treeData: [],
      moduleOptions: [],
      rules: {
        name: {
          required: true
        },
        id: {
          required: true
        },
        appId: {
          required: true
        }
      },
      designer: createDesigner(),
      designWidgets: {},
      widgetMetaLoading: true,
      editorInstalled: false,
      upgradeRemark: '',
      expandedResRowKeys: [],
      pageResources: [],
      protectedResIds: [],
      draggableConfig: {
        filterWtype: [],
        filter: undefined,
        dragGroup: 'dragGroup' // 用于控制设计组件可拖拽的区域
      },
      resColumns: [
        {
          title: '名称',
          dataIndex: 'title',
          scopedSlots: { customRender: 'titleSlot' }
        },
        {
          title: '资源权限',
          dataIndex: 'isProtected',
          align: 'center',
          width: 75,
          scopedSlots: { customRender: 'protectedSlot' }
        }
      ],
      pageStyle: {
        enableBackground: true,
        backgroundColor: '#ffffff', // 白底
        backgroundImage: undefined,
        backgroundImageInput: undefined,
        bgImageUseInput: false,
        backgroundRepeat: undefined,
        backgroundPosition: undefined
      },
      pageParams: [],
      newPageParam: {},
      previewPageStyle: false,
      uploading: false,
      widgetMeta: {},
      environment: {},
      layoutConfLoaded: true,
      product: {
        uuid: undefined,
        id: undefined,
        name: undefined
      },
      prodVersion: {
        versionId: undefined,
        version: undefined
      },
      designWidgetTypes: new Set(),
      subAppIds: [],
      mobilePreviewUiVisible: false
    };
  },
  provide() {
    return {
      designer: this.designer,
      draggableConfig: this.draggableConfig,
      designMode: true,
      unauthorizedResource: [],
      appId: this.pageDefinition.appId,
      layoutFixed: this.layoutFixed,
      widgetMeta: this.widgetMeta,
      ENVIRONMENT: this.environment,
      designWidgetTypes: this.designWidgetTypes,
      pageParams: this.pageParams,
      pageStyle: this.pageStyle,
      subAppIds: this.subAppIds,
      widgetI18ns: {}
    };
  },
  beforeCreate() {},
  components: {
    WidgetSelectionPanel,
    WidgetBuildPanel,
    WidgetConfigurationPanel,
    ThemeSet,
    ColorPicker,
    ImageLibrary,
    JsModuleSelect,
    PageParamDetail,
    ProdIndexNavDesign,
    ProdModuleFuncWidgetSelect,
    ExportDef,
    ImportDef,
    draggable: () => import(/* webpackChunkName: "vuedraggable" */ 'vuedraggable'),
    MobileDesignContainer,
    MobilePreview
  },
  computed: {
    terminalOptions() {
      return this.uniDesign
        ? [
            {
              label: '移动端',
              value: 'mobile'
            },
            {
              label: 'PC 端',
              value: 'pc'
            }
          ]
        : [
            {
              label: 'PC 端',
              value: 'pc'
            },
            {
              label: '移动端',
              value: 'mobile'
            }
          ];
    },
    designable() {
      return this.pageDefinition.uuid == undefined || this.pageDefinition.designable !== false;
    },
    vTitle() {
      let title = this.title || '页面设计';

      if (this.pageDefinition.name) {
        title += ' - ' + this.pageDefinition.name;
      }
      return title;
    },
    layoutFixed() {
      return this.pageDefinition.uuid && this.pageDefinition.layoutFixed;
    }
  },
  created() {
    this.layoutConfLoaded = this.prodVersionUuid == undefined || !this.layoutFixed;
    this.designer.isBigScreenDesign = false;
    // FIXME: 设计器先按中文显示，后续要修改
    this.$i18n.locale = 'zh_CN';
    if (this.uniDesign) {
      this.designer.isUniAppDesign = true;
      this.$set(this.designer, 'terminalType', 'mobile');
    }
  },

  methods: {
    getMobilePageUrl() {
      return `/packages/_/pages/app/preview`;
    },
    getPreviewJson() {
      return JSON.stringify({
        wtype: 'vUniPage',
        name: 'uni-app页面设计器',
        id: 'page_preview',
        items: this.designer.widgets
      });
    },

    preview() {
      if (this.designer.terminalType == 'mobile') {
        this.mobilePreviewUiVisible = true;
        return;
      }

      let urlParams = queryString(location.search.substr(1)),
        _temp = urlParams._temp || generateId();
      this.designer.tempLocalStorageKey = _temp;
      try {
        window.localStorage.setItem(
          `${_temp}`,
          JSON.stringify({
            id: this.pageDefinition.id,
            widgets: this.designer.widgets,
            pageVars: JSON.parse(this.$refs.buidPanel.defaultPageVars),
            pageJsModule: this.designer.pageJsModule,
            pageStyle: this.pageStyle
          })
        );
      } catch (error) {
        window.localStorage.clear();
        this.preview();
        return;
        // this.$message.error('请清理浏览器本地缓存');
      }

      window.localStorage.setItem(`${_temp}_widgetMap`, JSON.stringify(this.designer.widgetIdMap));
      let title = '页面设计';
      if (this.systemInfo != undefined || this.prodVersionUuid != undefined) {
        title = '首页设计';
        this.previewWindow = window.open(
          this.systemInfo != undefined
            ? '/index-designer/preview/' + this.pageDefinition.appId + '/' + _temp
            : '/index-designer/preview/' + this.pageDefinition.appId + '/' + this.prodVersionUuid + '/' + _temp,
          _temp
        );
      } else {
        this.previewWindow = window.open(
          (this._$SYSTEM_ID ? '/sys/' + this._$SYSTEM_ID + '/_' : '') + '/page-designer/preview/' + _temp,
          _temp
        );
      }
      if (!urlParams._temp) {
        history.pushState({}, title, `${location.pathname}${location.search}${location.search ? '&' : '?'}_temp=${_temp}`);
      }
    },
    redirectProductAssemble(product, systemInfo) {
      if (product != undefined) {
        if (systemInfo == undefined) {
          window.open(`/product/assemble/${product.uuid}`, '_blank');
        } else {
          window.open(`/system_admin/${product.id}/index`, '_blank');
        }
      }
    },
    redirectModuleAssemble(id, target = '_blank') {
      if (id) window.open(`/module/assemble/${id}`, target);
    },
    onChangeAppIdSelect() {
      this._provided.appId = this.pageDefinition.appId;
    },
    onProdNavChanged(e) {
      this.$refs.buidPanel.refresh();
    },
    onProdLayoutConfChange(data) {
      this.$set(this.environment, 'layoutConf', data);
      this.layoutConfLoaded = true;
    },
    prepareAddNewParam() {
      this.newPageParam = {
        code: undefined,
        name: undefined,
        remark: undefined,
        valueScope: [],
        required: false
      };
    },
    confirmAddNewPageParam() {
      this.pageParams.push(deepClone(this.newPageParam));
      this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
    },
    onChangePageInfoTabs(key) {
      if (key == 'pageResource') {
        this.refreshPageResource();
      }
    },
    onSwitchChange(checked) {
      this.$set(this.pageDefinition, 'isPc', checked ? '1' : '0');
    },
    getTreePopupContainer() {
      return document.querySelector('.widget-configuration-sider');
    },
    fetchTreeData() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'appProductIntegrationMgr',
          methodName: 'getTreeByProductUuidAndDataType',
          args: JSON.stringify([this.productUuid, ['1', '2', '3']])
        })
        .then(({ data }) => {
          _this.treeData = data.data.children;
        });
    },
    saveTemp() {
      let urlParams = queryString(location.search.substr(1)),
        _temp = urlParams._temp || generateId();
      this.designer.tempLocalStorageKey = _temp;
      localStorage.setItem(
        _temp,
        JSON.stringify({
          widgets: this.designer.widgets,
          pageVars: this.designer.pageVars,
          pageJsModule: this.designer.pageJsModule,
          pageParams: this.pageParams,
          pageStyle: this.pageStyle
        })
      );
      if (!urlParams._temp) {
        history.pushState({}, '页面设计', `${location.pathname}${location.search}${location.search ? '&' : '?'}_temp=${_temp}`);
      }
    },

    // getPageResources(resolve, reject) {
    //  return $axios.get(`/proxy/api/webapp/page/definition/getAppPageResourceFunction/${this.metadata.uuid}`, {});
    // },

    refreshPageResource() {
      // 根据组件生成资源列表
      let { functionElements, appWidgetDefinitionElements } = this.getWidgetElements();
      this.pageResources.splice(0, this.pageResources.length);
      let i = 1;
      for (let wEle of appWidgetDefinitionElements) {
        if (
          // 产品首页导航不在此处进行权限资源管控
          this.$refs.prodIndexNavDesign &&
          this.$refs.prodIndexNavDesign.widgetMenu &&
          this.$refs.prodIndexNavDesign.widgetMenu.id == wEle.id
        ) {
          continue;
        }

        let wgtResource = {
          uuid: i++,
          id: wEle.id,
          title: wEle.title
          // isProtected: this.protectedResIds.includes(wEle.id) // 组件暂不考虑进行权限控制
        };

        if (functionElements[wEle.id] && functionElements[wEle.id].length > 0) {
          wgtResource.children = [];
          this.expandedResRowKeys.push(wgtResource.uuid);
          for (let fEle of functionElements[wEle.id]) {
            if (fEle.functionType == undefined) {
              //目前仅对组件设计的元素进行权限控制，比如按钮等，对于组件设计引用其他功能比如数据仓库等（会指定功能类型），此类的组件元素不进行权限控制（后续再细分针对功能类型的元素哪些需要权限控制）
              wgtResource.children.push({
                uuid: i++,
                id: fEle.id,
                title: fEle.name,
                isProtected: this.protectedResIds.includes(fEle.id)
              });
            }
          }
        }
        if (wgtResource.children && wgtResource.children.length) {
          this.pageResources.push(wgtResource);
        }
      }
    },
    onChangeResProtected(e, item) {
      let idx = this.protectedResIds.indexOf(item.id);
      if (e && idx == -1) {
        this.protectedResIds.push(item.id);
      } else if (!e && idx != -1) {
        this.protectedResIds.splice(idx, 1);
      }
    },
    getProtectedPageResIds() {
      $axios
        .post('/json/data/services', {
          serviceName: 'appPageResourceService',
          methodName: 'getProtectedIdsByAppPageUuidAndConfigType',
          args: JSON.stringify([this.pageDefinition.uuid, '1'])
        })
        .then(({ data }) => {
          console.log(data);
          if (data.code == 0 && data.data) {
            this.protectedResIds = data.data;
          }
        })
        .catch(error => {});
    },

    fetchWidgetI18ns() {
      this.fetchLocaleOptions().then(i18nOption => {
        this.$axios
          .get(`/proxy/api/app/widget/getDefElementI18nsByDefId`, {
            params: {
              defId: this.pageDefinition.id,
              version: this.pageDefinition.version,
              applyTo: 'appPageDefinition'
            }
          })
          .then(({ data }) => {
            console.log('widget i18ns ', data);
            let result = data.data;
            if (result.length) {
              let widgetI18ns = {};
              for (let row of result) {
                set(widgetI18ns, `${row.locale}.Widget.${row.code}`, row.content);
              }

              this._provided.widgetI18ns = widgetI18ns;
              this.widgetI18ns = widgetI18ns;
              console.log('加载组件国际化数据包', widgetI18ns);
            }
          })
          .catch(error => {});
      });
    },

    getWidgetI18nObjects(widget) {
      let _this = this;
      function findI18nObjects(json, i18n, nestedWidget) {
        function traverse(obj, i18n, nestedWidget) {
          if (typeof obj !== 'object' || obj === null) {
            return;
          }
          if (obj.wtype && obj.id && obj.configuration && _this.designer.widgetIdMap[obj.id] == undefined) {
            traverse(obj.configuration, i18n, obj);
            return;
          }
          if (Array.isArray(obj)) {
            for (let item of obj) {
              traverse(item, i18n);
            }
          } else {
            for (let key in obj) {
              if (key === 'i18n' && obj[key] != undefined && obj[key].zh_CN != undefined) {
                let targetWgt = nestedWidget || widget;
                if (i18n[targetWgt.id] == undefined) {
                  i18n[targetWgt.id] = [];
                }
                i18n[targetWgt.id].push(obj[key]);
                continue;
              }
              traverse(obj[key], i18n, nestedWidget);
            }
          }
        }
        traverse(json, i18n, nestedWidget);
      }
      const i18n = {};
      findI18nObjects(widget.configuration, i18n);
      return i18n;
    },
    fetchLocaleOptions() {
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
            this.languageOptions = options;
            this.designer.languageOptions = options;
            resolve();
          })
          .catch(error => {});
      });
    },
    getWidgetElements() {
      let extraWidgets = [];
      // 补充保存产品主页的导航配置组件
      if (
        this.$refs.prodIndexNavDesign &&
        this.$refs.prodIndexNavDesign.widgetMenu &&
        this.designer.widgetIdMap[this.$refs.prodIndexNavDesign.widgetMenu.id] == undefined
      ) {
        extraWidgets.push(this.$refs.prodIndexNavDesign.widgetMenu);
      }

      return this.designer.getWidgetElements(extraWidgets);
    },

    updateProtectedResource(pageDefinitionUuid) {
      if (this.pageResources.length > 0) {
        let formData = { resourceDtos: [], pageDefinitionUuid };
        for (let f of this.pageResources) {
          if (f.isProtected != undefined) {
            formData.resourceDtos.push({
              id: f.id,
              isProtected: this.protectedResIds.includes(f.id)
            });
          }

          if (f.children != undefined) {
            for (let c of f.children) {
              formData.resourceDtos.push({
                id: c.id,
                isProtected: this.protectedResIds.includes(c.id)
              });
            }
          }
        }
        $axios.post('/proxy/api/webapp/page/definition/updatePageProtected', formData).then(({ data }) => {});
      }
    },

    savePageDesign(newVersion) {
      let _this = this;

      let collectAndSubmit = function () {
        _this.$loading('保存中');
        _this.designer.emitEvent('closeDrawer:' + _this.designer.drawerVisibleKey);
        let { functionElements, appWidgetDefinitionElements } = _this.getWidgetElements();
        // console.log(appWidgetDefinitionElements);
        // return null;

        // 通过组件定义解析出功能元素
        let definitionJson = {
          wtype: this.uniDesign ? 'vUniPage' : 'vPage',
          uuid: this.pageDefinition.uuid,
          title: this.pageDefinition.title || this.pageDefinition.name,
          name: '可视化页面设计器',
          id: this.pageDefinition.id,
          items: this.designer.widgets,
          vars: this.designer.pageVars,
          js: this.designer.pageJsModule,
          appWidgetDefinitionElements: appWidgetDefinitionElements,
          functionElements: functionElements,
          style: this.pageStyle,
          pageParams: this.pageParams
        };
        if (this.pageDefinition.i18n) {
          let i18ns = [];
          for (let locale in this.pageDefinition.i18n) {
            for (let key in this.pageDefinition.i18n[locale]) {
              if (this.pageDefinition.i18n[locale][key]) {
                i18ns.push({
                  locale: locale,
                  content: this.pageDefinition.i18n[locale][key],
                  defId: this.pageDefinition.id,
                  code: key,
                  applyTo: 'appPageDefinition'
                });
              }
            }
          }
          this.pageDefinition.i18ns = i18ns;
        }
        this.pageDefinition.functionElements = functionElements;
        // this.pageDefinition.piUuid = this.pageDefinition.appPiUuid;
        this.pageDefinition.newVersion = newVersion === true;
        this.pageDefinition.isPc = definitionJson.wtype == 'vPage' ? '1' : '0';
        this.pageDefinition.appWidgetDefinitionElements = appWidgetDefinitionElements;
        this.pageDefinition.definitionJson = JSON.stringify(definitionJson);
        this.$axios
          .post('/web/design/savePageDefinition', this.pageDefinition)
          .then(({ data }) => {
            if (data.code == 0) {
              let urlParams = queryString(location.search.substr(1));
              if (urlParams._temp) {
                localStorage.setItem(
                  urlParams._temp,
                  JSON.stringify({
                    widgets: _this.designer.widgets,
                    pageVars: _this.designer.pageVars,
                    pageJsModule: _this.designer.pageJsModule
                  })
                );
              }
              let _uuid = data.data;
              // 更新受保护资源信息
              _this.updateProtectedResource(_uuid);

              if (_this.prodVersionUuid && (newVersion || _this.pageDefinition.uuid == undefined)) {
                // 归属产品版本的页面
                _this.updateProdVersionRelaPage(_this.prodVersionUuid, _uuid, newVersion === true ? _this.pageDefinition.uuid : undefined);
              }
              if (_this.pageDefinition.uuid && newVersion === true) {
                const oldUuid = _this.pageDefinition.uuid;
                _this.getLatestUuidAndVersion(_this.pageDefinition.id, data => {
                  _this.addUpgradeLog(data.uuid, data.version, _this.upgradeRemark, _this.pageDefinition.id);
                  // 如果有在资源组内，则更新资源组关联的页面UUID
                  $axios
                    .get(`/proxy/api/app/module/resGroup/updateMember`, {
                      params: { memberUuid: oldUuid, updateMemberUuid: _uuid }
                    })
                    .then(() => {
                      // _this.pageContext.emitCrossTabEvent('ModuleResourceRefresh', 1);
                    });
                  _this.currentLatestPageUuid = data.uuid;
                  _this.currentLatestVersion = data.version;
                  _this.pageDefinition.version = data.version;
                  // 通知页面设计发生变更
                  _this.pageContext.emitCrossTabEvent('page:design:change:' + _this.pageDefinition.uuid, _this.pageDefinition);
                });
              }
              if (_this.pageDefinition.uuid && !newVersion) {
                _this.pageContext.emitCrossTabEvent('page:design:change:' + _this.pageDefinition.uuid, _this.pageDefinition);
              }
              _this.pageDefinition.uuid = _uuid;
              if (!definitionJson.uuid) {
                _this.pageContext.emitCrossTabEvent('page:design:create', _this.pageDefinition);
              }
              // 保存页面引用资源
              // _this.saveDataRefResource(_this.pageDefinition, functionElements);
              // 不刷新页面
              history.pushState({}, '页面设计器', `${location.pathname}?uuid=${_uuid}`);
              _this.onSaveThumbnail(_this.pageDefinition.id);
              _this.$message.success(`保存${newVersion === true ? '新版本' : ''}成功${newVersion === true ? ', 当前为新版本' : ''}!`);
              _this.clearServerCache();
            } else {
              _this.$message.error(`保存失败 !`);
            }
            _this.$loading(false);
          })
          .catch(error => {
            console.error(error);
            _this.$loading(false);
          });
      };
      this.$refs.basicForm.validate(function (vali) {
        if (vali) {
          if (newVersion) {
            let autoSize = { minRows: 5, maxRows: 5 };
            _this.$confirm({
              title: '确定要升级为新版本吗?',
              content: h => (
                <a-textarea
                  placeholder="请输入升级说明"
                  auto-size={autoSize}
                  allow-clear
                  onChange={e => (_this.upgradeRemark = e.target.value)}
                />
              ),
              okText: '确定',
              // icon: h => <span></span>,
              cancelText: '取消',
              okText: '继续',
              onOk() {
                collectAndSubmit.call(_this);
              },
              onCancel() {}
            });
          } else {
            collectAndSubmit.call(_this);
          }
        } else {
          _this.$refs.wConfigurePanel.activeKey = 'basicInfo';
          _this.$loading(false);
        }
      });
    },
    clearServerCache() {
      $axios.get(`/api/cache/deleteByPattern`, { params: { pattern: `page:${this.pageDefinition.uuid}:widget:*` } });
      $axios.get(`/api/cache/deleteByPattern`, { params: { pattern: `page:${this.pageDefinition.id}:widget:*` } });
    },
    saveDataRefResource(pageDefinition, functionElements) {
      let list = [];
      for (let wgtId in functionElements) {
        list.push({
          dataDefUuid: pageDefinition.uuid,
          dataDefName: pageDefinition.name,
          dataDefType: 'appPageDefinition',
          itemId: wgtId,
          itemName: this.designer.widgetIdMap[wgtId].title,
          functionElements: functionElements[wgtId],
          isProtected: false,
          configType: '1',
          moduleId: pageDefinition.appId
        });
      }
      $axios
        .post(`/proxy/api/app/datadef/saveRefResources/${pageDefinition.uuid}`, list)
        .then(({ data }) => {})
        .catch(error => {});
    },

    updateProdVersionRelaPage(prodVersionUuid, pageUuid, oldPageUuid) {
      // 更新产品版本对应的页面关系
      $axios
        .get(`/proxy/api/app/prod/version/updateProdVersionPage`, {
          params: { prodVersionUuid, fromPageUuid: oldPageUuid, pageUuid }
        })
        .then(({ data }) => {})
        .catch(error => {});
    },
    changeLocale(value) {
      //修改cookie值
      var Days = 30;
      var exp = new Date();
      exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
      document.cookie = 'locale=' + escape(value) + ';expires=' + exp.toGMTString();
    },
    setRequiredRule() {
      let requiredRule = {
        required: true,
        message: <a-icon type="close-circle" theme="filled" />,
        trigger: ['blur', 'change'],
        whitespace: true
      };

      this.rules = {
        name: requiredRule,
        id: requiredRule,
        appPiUuid: requiredRule
      };
    },
    getModuleOptions() {
      $axios
        .post('/common/select2/query', {
          serviceName: 'appModuleMgr',
          queryMethod: 'loadSelectData',
          idProperty: 'id',
          includeSuperAdmin: 'true'
        })
        .then(({ data }) => {
          if (data.results) {
            this.moduleOptions = [];
            for (let i = 0, len = data.results.length; i < len; i++) {
              this.moduleOptions.push({
                label: data.results[i].text,
                value: data.results[i].id
              });
            }
          }
        });
    },
    filterOption(input, option) {
      return (
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        (option.componentOptions.propsData.value && option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0)
      );
    },
    getLatestUuidAndVersion(id, callback) {
      $axios
        .post('/json/data/services', {
          serviceName: 'appPageDefinitionService',
          methodName: 'getLatestUuidAndVersion',
          args: JSON.stringify([id])
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            if (typeof callback == 'function') {
              callback.call(this, data.data);
            }
          }
        });
    },

    addUpgradeLog(uuid, version, remark, id) {
      $axios.post(`/proxy/api/app/res/upgrade/saveLog`, { resUuid: uuid, version, remark, id });
    },

    initDesignWidgets() {
      // 加载组件元数据
      import('@modules/.webpack.widget.meta-info.js').then(info => {
        let widgets = info.default;
        // 组件归类
        // console.log('载入组件信息: ', info.default);
        this.designWidgets = {};
        this.fetchWidgetUsageFrequency().then(map => {
          widgets = orderBy(
            widgets,
            function (w) {
              let wtype = w.wtype.indexOf('$') == -1 ? w.wtype : w.wtype.split('$')[0];
              return map[wtype] == undefined ? 0 : parseInt(map[wtype]);
            },
            'desc'
          );
          for (let w of widgets) {
            if (
              w.scope == 'page' ||
              w.scope == 'mobilePage' ||
              (Array.isArray(w.scope) && (w.scope.includes('page') || w.scope.includes('mobilePage')))
            ) {
              if (w.category) {
                let _category = [];
                if (typeof w.category == 'string') {
                  _category = [w.category];
                } else if (Array.isArray(w.category)) {
                  _category = w.category;
                }
                if (_category.length) {
                  for (let c of _category) {
                    if (this.designWidgets[c] == undefined) {
                      this.designWidgets[c] = [];
                    }
                    this.designWidgets[c].push(w);
                    this.widgetMeta[w.wtype] = w;
                    if (w.wtype.indexOf('$') == -1) {
                      this.designWidgetTypes.add(w.wtype);
                    }
                  }
                }
              }
            }
          }
          this.widgetMetaLoading = false;
        });
      });
    },
    exportAsTree() {
      $axios
        .get(`/proxy/common/iexport/service/export`, {
          params: {
            uuid: this.pageDefinition.uuid,
            type: 'appPageDefinition'
          }
        })
        .then(({ data }) => {
          console.log(data.data);
        })
        .catch(error => {});
    },
    saveFunctionWidgetDef() {
      // 获取快照
      let _this = this;
      _this.designer.clearSelected();
      this.$loading('保存中');

      toPng(document.querySelector('.widget-build-drop-container'))
        .then(function (dataUrl) {
          let { functionElements, appWidgetDefinitionElements } = _this.getWidgetElements();
          let i18ns = [];
          if (appWidgetDefinitionElements.length) {
            for (let e of appWidgetDefinitionElements) {
              if (e.i18ns) {
                i18ns.push(...e.i18ns);
              }
            }
          }
          $axios
            .post(`/proxy/api/user/widgetDef/saveUserDefWidget`, {
              widgetId: _this.pageDefinition.id,
              uuid: _this.widgetUuid,
              title: _this.pageDefinition.title,
              remark: _this.pageDefinition.remark,
              appId: _this.pageDefinition.appId,
              definitionJson: JSON.stringify({
                items: _this.designer.widgets,
                widgetParams: _this.pageParams,
                thumbnail: dataUrl
              }),
              i18ns,
              type: 'FUNCTION_WIDGET'
            })
            .then(({ data }) => {
              _this.$loading(false);
              if (data.code == 0) {
                _this.$message.success('保存成功');
              }
            })
            .catch(() => {
              _this.$loading(false);
            });
        })
        .catch(function (error) {
          console.error('oops, something went wrong!', error);
        });
    },
    fetchModuleFunctionWidgetDetail() {
      let uuid = this.widgetUuid;
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/user/widgetDef/getDetail`, { params: { uuid } })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    },
    fetchProdDetail(prodVersionUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .all([
            $axios.post('/json/data/services', {
              serviceName: 'appProdVersionService',
              methodName: 'getOne',
              args: JSON.stringify([prodVersionUuid])
            }),
            $axios.post('/json/data/services', {
              serviceName: 'appProductService',
              methodName: 'getProductByProdVersionUuid',
              args: JSON.stringify([prodVersionUuid])
            })
          ])
          .then(
            $axios.spread((res1, res2) => {
              resolve({
                prodVersion: res1.data.data,
                product: res2.data.data
              });
            })
          )
          .catch(error => {});
      });
    },
    fetchSubAppIds() {
      $axios
        .get(this.systemInfo ? `/proxy/api/app/module/listModuleUnderSystem` : `/proxy/api/app/prod/version/modules`, {
          params: this.systemInfo
            ? { system: this.systemInfo.system, tenant: this.systemInfo.tenant }
            : { prodVersionUuid: this.prodVersionUuid }
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            for (let d of data.data) {
              this.subAppIds.push(d.id);
            }
          }
        })
        .catch(error => {});
    },
    fetchWidgetUsageFrequency() {
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'appWidgetDefinitionService',
            methodName: 'getVueWidgetUsageFrequency',
            args: JSON.stringify([])
          })
          .then(({ data }) => {
            if (data.data) {
              resolve(data.data);
            }
          });
      });
    },
    dataUrlToFile(dataURL, fileName) {
      // 将base64的数据部分提取出来
      const bytes = window.atob(dataURL.split(',')[1]);

      // 创建一个ArrayBuffer，并用它创建一个Blob对象
      const mimeString = dataURL.split(',')[0].match(/:(.*?);/)[1];
      const ab = new ArrayBuffer(bytes.length);
      const ia = new Uint8Array(ab);
      for (let i = 0; i < bytes.length; i++) {
        ia[i] = bytes.charCodeAt(i);
      }
      const blob = new Blob([ab], { type: mimeString });

      // 创建一个File对象
      const file = new File([blob], fileName, { type: mimeString });

      return file;
    },
    onSaveThumbnail(id) {
      let _this = this;
      if (location.pathname.startsWith('/index-designer')) {
        // 首页设计保存页面的缩略图
        toPng(document.querySelector('.widget-build-drop-container .widget-drop-panel'), { pixelRatio: 0.5 })
          .then(function (dataUrl) {
            let file = _this.dataUrlToFile(dataUrl, 'thumbnail.png', '');
            customFileUploadRequest({
              file,
              folder: {
                folderID: id,
                purpose: 'thumbnail',
                popFolderFile: true
              }
            }).then(res => {
              // console.log(`/proxy-repository/repository/file/mongo/download?fileID=${res.fileID}`);
              // var link = document.createElement('a');
              // link.download = 'my-image-name.jpeg';
              // link.href = `/proxy-repository/repository/file/mongo/download?fileID=${res.fileID}`;
              // link.click();
              // _this.$message.success('保存成功');
            });
          })
          .catch(function (error) {
            console.error('oops, something went wrong!', error);
          });
      }
    }
  },
  beforeMount() {
    Promise.all([
      import('@pageAssembly/app/web/framework/vue/installEditor'),
      import('@pageAssembly/app/web/framework/vue/installUniEditor')
    ]).then(() => {
      this.editorInstalled = true;
    });

    if (!this.designable) {
      return;
    }
    this.designer.vueInstance = this;
    window.designer = this.designer;
    this.initDesignWidgets();
    this.fetchWidgetI18ns();
    if (!this.functionWidgetDesign) {
      this.getModuleOptions();
      if (this.pageDefinition && this.pageDefinition.definitionJson) {
        let json = JSON.parse(this.pageDefinition.definitionJson);
        this.designer.widgets = json.items;
        this.designer.pageVars = json.vars;
        this.designer.pageJsModule = json.js;
        if (json.style) {
          Object.assign(this.pageStyle, json.style);
          // 处理旧图片数据
          if (this.pageStyle.bgImageUseInput && this.pageStyle.backgroundImageInput) {
            this.pageStyle.backgroundImage = `url("${this.pageStyle.backgroundImageInput}")`;
            delete this.pageStyle.backgroundImageInput;
            delete this.pageStyle.bgImageUseInput;
          }
        }
        if (json.pageParams != undefined && json.pageParams.length > 0) {
          this.pageParams.push(...json.pageParams);
        }
      }
      if (this.pageDefinition.i18ns) {
        let i18n = {};
        for (let item of this.pageDefinition.i18ns) {
          if (i18n[item.locale] == undefined) {
            i18n[item.locale] = {};
          }
          if (item.elementId == null) {
            i18n[item.locale][item.code] = item.content;
          }
        }
        this.$set(this.pageDefinition, 'i18n', i18n);
      }

      let urlParams = queryString(location.search.substr(1));
      if (urlParams._temp) {
        if (localStorage.getItem(urlParams._temp) != null) {
          let cached = JSON.parse(localStorage.getItem(urlParams._temp));
          this.designer.widgets = cached.widgets;
          this.designer.pageVars = cached.pageVars;
          this.designer.pageJsModule = cached.pageJsModule;
          if (cached.pageStyle) {
            Object.assign(this.pageStyle, cached.pageStyle);
          }
          if (cached.pageParams != undefined && cached.pageParams.length > 0) {
            this.pageParams.splice(0, this.pageParams.length);
            this.pageParams.push(...cached.pageParams);
          }
        }
      }
      if (this.pageDefinition.uuid) {
        this.getProtectedPageResIds();
        this.getLatestUuidAndVersion(this.pageDefinition.id, data => {
          this.currentLatestVersion = data.version;
          this.currentLatestPageUuid = data.uuid;
        });
      }
    } else {
      this.$loading();
      this.fetchModuleFunctionWidgetDetail().then(data => {
        this.$set(this.pageDefinition, 'title', data.title);
        this.$set(this.pageDefinition, 'remark', data.remark);
        this.$set(this.pageDefinition, 'id', data.id);
        this.$set(this.pageDefinition, 'appId', data.appId);
        this._provided.appId = data.appId;
        let json = JSON.parse(data.definitionJson);
        this.designer.widgets = json.items;
        this.pageParams = json.widgetParams || [];
        this.$loading(false);
      });
    }

    if (this.prodVersionUuid) {
      this.fetchProdDetail(this.prodVersionUuid).then(result => {
        if (result.product == null) {
          // this.$error({
          //   title: '错误',
          //   content: `${result.product == null ? '无法找到相关产品' : '无法找到相关产品版本'}`
          // });
          return;
        }
        this.product.id = result.product.id;
        this.product.name = result.product.name;
        this.product.uuid = result.product.uuid;
        this.prodVersion.version = result.prodVersion.version;
        this.prodVersion.versionId = result.prodVersion.versionId;
      });
    }

    if (this.systemInfo != undefined || this.prodVersionUuid != undefined) {
      this.fetchSubAppIds();
    }
  },
  mounted() {
    if (!this.designable) {
      return;
    }
    if (!this.functionWidgetDesign && this.designer != undefined) {
      this.$refs.buidPanel.setPageVars(this.designer.pageVars);
    }
    this.scrollerHeight = window.innerHeight - 51 + 'px';
    addWindowResizeHandler(() => {
      this.$nextTick(() => {
        this.scrollerHeight = window.innerHeight - 51 + 'px';
      });
    });

    this.setRequiredRule();
  }
};
</script>
