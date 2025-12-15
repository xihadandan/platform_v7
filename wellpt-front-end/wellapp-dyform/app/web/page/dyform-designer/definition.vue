<template>
  <HtmlWrapper :title="vTitle">
    <a-layout class="widget-design-layout">
      <a-layout-header class="widget-design-header">
        <a-row>
          <a-col :span="8">
            <a-avatar
              shape="square"
              :size="24"
              style="background-color: #fff; color: var(--w-primary-color); margin-right: 8px; margin-top: -4px"
            >
              <Icon type="pticon iconfont icon-ptkj-biaodanguanli" style="font-weight: normal; vertical-align: top; font-size: 16px" />
            </a-avatar>
            <span style="font-weight: bold; font-size: var(--w-font-size-lg)">
              {{ designName }}
            </span>
          </a-col>
          <a-col :span="8" style="text-align: center">
            <a-radio-group v-model="designer.terminalType" button-style="solid" class="design-terminal-type-radio radio-ghost">
              <a-radio-button value="pc" style="width: 100px">
                <i class="pticon iconfont icon-ptkj-dagou" v-show="designer.terminalType == 'pc'" />
                PC 端
              </a-radio-button>
              <a-radio-button value="mobile" style="width: 100px">
                <i class="pticon iconfont icon-ptkj-dagou" v-show="designer.terminalType == 'mobile'" />
                移动端
              </a-radio-button>
            </a-radio-group>
          </a-col>
          <a-col :span="8" :style="{ textAlign: 'right' }">
            <a-button ghost @click="e => save(e, false, false)" style="margin-right: 8px">保存</a-button>
            <a-button
              v-if="definition.uuid"
              type="primary"
              style="margin-right: 8px"
              ghost
              @click="e => update(e, '确定要升级表单定义版本吗?', true)"
            >
              保存新版本
            </a-button>
            <!-- <a-button type="primary" icon="save" @click="saveTemp">本地暂存</a-button> -->
            <a-button @click="mockData" type="primary" ghost v-if="definition.uuid">数据模拟</a-button>
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
            :designWidgets="designWidgets"
            :defaultActiveTab="dataModelUuid ? 'dyform_data_model_property' : undefined"
            :designType="designer.terminalType == 'pc' ? 'dyform' : 'mobileDyform'"
            ref="widgetSelectPanel"
            allowTemplate
            :filterCategory="[
              'feedbackComponent',
              'basicComponent',
              'advanceComponent',
              'basicContainer',
              'advanceContainer',
              'displayComponent'
            ]"
          >
            <template slot="addonBeforeTabs">
              <a-tab-pane key="dyform_data_model_property" v-if="definitionVjson.useDataModel" :forceRender="true">
                <span slot="tab">
                  <i class="iconfont icon-a-icleftzujian"></i>
                  <br />
                  属性
                </span>
                <PerfectScrollbar class="sider-select-panel-scroll">
                  <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="1" class="design-template">
                    <a-collapse-panel key="1">
                      <template slot="header">
                        <!-- <i class="line" /> -->
                        <label class="">数据模型属性</label>
                      </template>

                      <draggable
                        tag="ul"
                        :list="dataModelColumns"
                        :group="{ name: currentDragGroup, pull: 'clone', put: false }"
                        :clone="handleModelColClone"
                        :sort="false"
                        filter=".field-used"
                        class="select-field-2-rect"
                      >
                        <li
                          v-for="(select, index) in dataModelColumns"
                          :key="index"
                          :class="[
                            'widget-select-item data-model-column',
                            fieldCodeUsed.includes(select.column) ? 'field-used' : '',
                            !select.notNull ? '' : 'required',
                            modelFieldFocused.includes(select.column) ? 'focus' : ''
                          ]"
                          :title="select.title"
                          style="text-align: center"
                        >
                          {{ select.title }}
                        </li>
                      </draggable>
                    </a-collapse-panel>
                  </a-collapse>
                </PerfectScrollbar>
              </a-tab-pane>
            </template>
            <template slot="addonAfterTabs">
              <a-tab-pane key="ref_other_dyfrom" :forceRender="true">
                <span slot="tab">
                  <Icon type="pticon iconfont icon-ptkj-yinyong" />
                  <br />
                  引用
                </span>
                <RefDyformTab
                  v-if="editorInstalled"
                  :designer="designer"
                  :definition="definition"
                  :definition-vjson="definitionVjson"
                  :provideRefDyforms="refDyforms"
                />
              </a-tab-pane>
            </template>
          </WidgetSelectionPanel>
        </a-layout-sider>
        <a-layout-content id="design-main" :class="designer.terminalType">
          <WidgetBuildPanel
            :customDndContainer="designer.terminalType == 'mobile'"
            :designer="designer"
            :allowSaveAsTemplate="true"
            designType="dyform"
            :loading="!editorInstalled || widgetMetaLoading"
          >
            <template slot="preview-btn-slot">
              <a-button type="link" size="small" :title="$t('PageDesigner.toolbar.preview', '预览')" @click.stop="preview">
                <!-- 预览 -->
                <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
                {{ $t('PageDesigner.toolbar.preview', '预览') }}
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
                @message="onPreviewMessage"
                ref="mobilePreview"
              ></MobilePreview>
            </template>
          </WidgetBuildPanel>
        </a-layout-content>
        <a-layout-sider theme="light" class="widget-configuration-sider" :width="380">
          <WidgetConfigurationPanel :designer="designer" ref="confPanel">
            <template slot="basicInfoTab">
              <a-tab-pane key="basicInfo" tab="表单信息">
                <PerfectScrollbar>
                  <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="1">
                    <a-collapse-panel key="1" header="基础属性">
                      <a-form-model
                        ref="defiForm"
                        :model="definition"
                        :rules="defRules"
                        labelAlign="left"
                        :label-col="{ span: 8 }"
                        :wrapper-col="{ span: 16, style: { textAlign: 'right' } }"
                        :colon="false"
                      >
                        <a-form-model-item label="使用数据模型" v-if="definition.uuid == undefined || dataModelUuid == undefined">
                          <a-switch
                            v-model="definitionVjson.useDataModel"
                            :disabled="hasField || dataModelUuid != undefined"
                            @change="onChangeUseDataModelSwitch"
                          />
                        </a-form-model-item>
                        <a-form-model-item v-show="definitionVjson.useDataModel">
                          <template slot="label">
                            <span
                              style="cursor: pointer"
                              :class="definitionVjson.dataModelUuid ? 'ant-btn-link' : ''"
                              @click="redirectDataModelDesign(definitionVjson.dataModelUuid)"
                            >
                              数据模型
                              <a-icon type="environment" v-show="definitionVjson.dataModelUuid" style="color: inherit; line-height: 1" />
                            </span>
                          </template>
                          <a-select
                            :options="dataModelOptions"
                            v-model="definitionVjson.dataModelUuid"
                            allow-clear
                            show-search
                            :filter-option="filterOption"
                            @change="onSelectChangeDataModel"
                            :disabled="hasField || dataModelUuid != undefined"
                          />
                        </a-form-model-item>
                        <a-form-model-item label="名称" prop="name">
                          <a-input v-model="definition.name" @change="onChangeDefinitionName" />
                        </a-form-model-item>
                        <a-form-model-item label="ID" prop="id">
                          <a-input
                            :maxLength="24"
                            v-model="definition.id"
                            :readOnly="readOnlyId"
                            @change="e => onInputId2CaseFormate(e, 'toUpperCase')"
                          >
                            <a-icon
                              v-if="!readOnlyId"
                              slot="suffix"
                              title="自动翻译"
                              :type="translating ? 'loading' : 'code'"
                              style="color: rgba(0, 0, 0, 0.45); cursor: pointer"
                              @click="translateCodeFromName(true)"
                            />
                          </a-input>
                        </a-form-model-item>

                        <a-form-model-item label="编号">
                          <a-input v-model="definition.code" />
                        </a-form-model-item>
                        <a-form-model-item label="所属分类" prop="categoryUuid">
                          <a-tree-select
                            v-model="definition.categoryUuid"
                            style="width: 100%"
                            :replaceFields="{ title: 'name', key: 'uuid', value: 'uuid' }"
                            :tree-data="categoryTreeData"
                            allow-clear
                            show-search
                            treeNodeFilterProp="title"
                          />
                        </a-form-model-item>
                        <a-form-model-item prop="moduleId">
                          <template slot="label">
                            <div
                              style="cursor: pointer; display: inline-block"
                              :class="definition.moduleId ? 'ant-btn-link' : ''"
                              @click="redirectModuleAssemble(definition.moduleId)"
                            >
                              所属模块
                              <a-icon type="environment" v-show="definition.moduleId" style="color: inherit; line-height: 1" />
                            </div>
                          </template>
                          <a-select
                            :filter-option="filterOption"
                            :showSearch="true"
                            v-model="definition.moduleId"
                            :options="moduleOptions"
                            @change="onChangeModuleSelect"
                            :disabled="definition.uuid != undefined || dataModelUuid != undefined"
                            :style="{ width: '100%' }"
                          ></a-select>
                        </a-form-model-item>
                        <a-form-model-item label="JS模块">
                          <JsModuleSelect :multiSelect="false" v-model="jsModule" dependencyFilter="WidgetDyformDevelopment" />
                        </a-form-model-item>
                        <a-form-model-item label="表单标题">
                          <a-radio-group
                            size="small"
                            v-model="definitionJson.titleType"
                            defaultValue="1"
                            @change="onChangeFormTitle"
                            button-style="solid"
                          >
                            <a-radio-button value="1">默认</a-radio-button>
                            <a-tooltip placement="topRight" :overlayStyle="{ 'max-width': 'fit-content' }">
                              <a-radio-button value="2">自定义</a-radio-button>
                              <template slot="title">自定义标题为空时，等同于默认，保存后显示为默认</template>
                            </a-tooltip>
                          </a-radio-group>

                          <VariableDefineTemplate
                            v-model="definitionJson.titleContentVariables"
                            ref="variableTpt"
                            :variableTreeData="designer.variableTreeData"
                            :editable="definitionJson.titleType == '2'"
                          />
                        </a-form-model-item>

                        <a-form-model-item label="数据存储">
                          <a-select
                            defaultValue="1"
                            v-model="definitionJson.repository.mode"
                            :options="repositoryModeOptions"
                            :style="{ width: '100%' }"
                          ></a-select>
                        </a-form-model-item>
                        <a-form-model-item label="数据库表" v-show="definitionJson.repository.mode == '2'">
                          <a-select
                            :showSearch="true"
                            :allowClear="true"
                            defaultValue="1"
                            v-model="definitionJson.repository.userTableName"
                            :options="userTableOptions"
                            :style="{ width: '100%' }"
                          ></a-select>
                        </a-form-model-item>

                        <a-form-model-item label="服务地址" v-show="definitionJson.repository.mode == '4'">
                          <a-input v-model="definitionJson.repository.serviceUrl" />
                        </a-form-model-item>

                        <a-form-model-item
                          label="AccessToken"
                          v-show="definitionJson.repository.mode == '4'"
                          :label-col="{ span: 7 }"
                          :wrapper-col="{ span: 16 }"
                        >
                          <a-input v-model="definitionJson.repository.serviceToken" />
                        </a-form-model-item>

                        <a-form-model-item label="存储接口" v-show="definitionJson.repository.mode == '5'">
                          <a-select
                            :allowClear="true"
                            defaultValue="1"
                            v-model="definitionJson.repository.customInterface"
                            :options="customInterfaceOptions"
                            :style="{ width: '100%' }"
                          ></a-select>
                        </a-form-model-item>
                      </a-form-model>
                    </a-collapse-panel>
                    <a-collapse-panel key="2" header="事件管理">
                      <a-form-model :label-col="{ span: 10 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }" :colon="false">
                        <a-form-model-item label="表单创建后事件">
                          <WidgetCodeEditor
                            @save="value => (definitionVjson.lifecycleHook.created = value)"
                            :value="definitionVjson.lifecycleHook.created"
                          >
                            <a-button icon="code">编写代码</a-button>
                          </WidgetCodeEditor>
                        </a-form-model-item>

                        <a-form-model-item label="表单挂载前事件">
                          <WidgetCodeEditor
                            @save="value => (definitionVjson.lifecycleHook.beforeMount = value)"
                            :value="definitionVjson.lifecycleHook.beforeMount"
                          >
                            <a-button icon="code">编写代码</a-button>
                          </WidgetCodeEditor>
                        </a-form-model-item>

                        <a-form-model-item label="表单挂载后事件">
                          <WidgetCodeEditor
                            @save="value => (definitionVjson.lifecycleHook.mounted = value)"
                            :value="definitionVjson.lifecycleHook.mounted"
                          >
                            <a-button icon="code">编写代码</a-button>
                          </WidgetCodeEditor>
                        </a-form-model-item>
                      </a-form-model>
                    </a-collapse-panel>
                  </a-collapse>
                </PerfectScrollbar>
              </a-tab-pane>
            </template>
          </WidgetConfigurationPanel>
        </a-layout-sider>
      </a-layout>
    </a-layout>
  </HtmlWrapper>
</template>

<script type="text/babel">
import '@pageAssembly/app/web/assets/css/design.less';
import '../../assets/css/design.less';
import { addWindowResizeHandler, generateId, deepClone, queryString, getCookie } from '@framework/vue/utils/util';
import WidgetSelectionPanel from '@pageAssembly/app/web/page/page-designer/component/widget-select-panel.vue';
import WidgetBuildPanel from '@pageAssembly/app/web/page/page-designer/component/widget-build-panel.vue';
import WidgetConfigurationPanel from '@pageAssembly/app/web/page/page-designer/component/widget-configuration-panel.vue';
import MobileDesignContainer from '@pageAssembly/app/web/page/page-designer/component/mobile-design-container.vue';
import MobilePreview from '@pageAssembly/app/web/page/page-designer/component/mobile-preview.vue';
import { createDesigner } from '@framework/vue/designer/designer';
import { createDyform } from '../../framework/vue/dyform/dyform';
import '@framework/vue/designer/install.js';

import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
import ThemeSet from '@pageAssembly/app/web/lib/theme-set.vue';
import draggable from '@framework/vue/designer/draggable';
import { convertFormField2DataModelColumns } from './utils';
import { DEFAULT_SYS_COLUMNS } from '@pageAssembly/app/web/page/module-center/component/data-model/const';
import VariableDefineTemplate from '../../widget/commons/variable-define-template.vue';
import JsModuleSelect from '@pageWidget/commons/js-module-select.vue';
import { debounce, orderBy, find, every, camelCase } from 'lodash';
import RefDyformTab from './component/ref-dyform-tab.vue';
import { getOptions } from '../../widget/widget-form-date-picker/configuration/components/date-pattern-options.js';
export default {
  name: 'DyformDefinition',
  mixins: [draggable],
  data() {
    let dyform = createDyform();
    let designer = createDesigner();
    designer.isDyformDesign = true;
    let defaultTitleContent = '${表单名称}_${年}-${月}-${日}';
    return {
      defRules: {
        name: { required: true, message: '' },
        id: { required: true, message: '' },
        moduleId: { required: true, message: '' }
      },
      repositoryModeOptions: [
        { label: '表单内置存储', value: '1' },
        { label: '数据库表存储', value: '2' },
        { label: 'RESTful api表单存储', value: '4' },
        { label: '自定义接口存储', value: '5' }
      ],
      defaultTitleContent,
      // 旧版定义
      definitionJson: {
        repository: {
          mode: '1',
          userTableName: undefined,
          serviceUrl: undefined,
          serviceToken: undefined,
          customInterface: undefined
        },
        titleType: '1',
        titleContent: defaultTitleContent,
        titleContentVariables: {
          variables: [
            { label: '表单名称', value: '${表单名称}' },
            { label: '_', value: '_', edit: false },
            { label: '年', value: '${年}' },
            { label: '-', value: '-', edit: false },
            { label: '月', value: '${月}' },
            { label: '-', value: '-', edit: false },
            { label: '日', value: '${日}' }
          ],
          value: defaultTitleContent
        }
      },
      // 新版vue定义相关
      definitionVjson: {
        widgets: [],
        useDataModel: false,
        dataModelUuid: undefined,
        lifecycleHook: {
          beforeCreate: null,
          created: null,
          beforeMount: null,
          mounted: null
        },
        refDyform: {}
      },
      // definition: {},
      scrollerHeight: '900px',
      designer,
      dyform,
      categoryTreeData: [],
      moduleOptions: [],
      userTableOptions: [],
      customInterfaceOptions: [],
      deletedFieldNames: [],
      widgetMeta: {},
      dataModelOptions: [],
      dataModelColumns: [],
      dataModelColumnMap: {},
      fieldCodeUsed: [],
      modelFieldFocused: [],
      upgradeRemark: undefined,
      widgetMetaLoading: true,
      editorInstalled: false,
      draggableConfig: {
        filterWtype: [],
        filter: undefined,
        dragGroup: 'dragGroup' // 用于控制设计组件可拖拽的区域
      },
      designWidgets: {},
      translating: false,
      designWidgetTypes: new Set(),
      mobilePreviewUiVisible: false,
      refDyforms: []
    };
  },

  provide() {
    return {
      designer: this.designer,
      draggableConfig: this.draggableConfig,
      widgetMeta: this.widgetMeta,
      dyform: this.dyform,
      designMode: true,
      definition: this.definition,
      appId: this.definition.moduleId,
      dataModelColumns: this.dataModelColumns,
      dataModelColumnMap: this.dataModelColumnMap,
      fieldCodeUsed: this.fieldCodeUsed,
      designWidgetTypes: this.designWidgetTypes,
      pageParams: {},
      layoutFixed: false,
      refDyforms: this.refDyforms
    };
  },

  beforeCreate() {},
  components: {
    WidgetSelectionPanel,
    WidgetBuildPanel,
    WidgetConfigurationPanel,
    ThemeSet,
    VariableDefineTemplate,
    JsModuleSelect,
    RefDyformTab,
    MobileDesignContainer,
    MobilePreview
  },
  computed: {
    designName() {
      if (this.dataModelUuid) {
        return '模型表单设计';
      }
      let name = '表单设计';
      if (this.definition.name) {
        name = `${this.definition.name} v${this.definition.version || '1'}`;
      }
      return name;
    },
    vTitle() {
      return this.definition.name ? '表单设计 - ' + this.definition.name : '表单设计';
    },
    currentDragGroup() {
      return this.designer.currentCanDragGroup;
    },
    readOnlyId() {
      return this.definition.uuid != null || this.definition.uuid != undefined;
    },
    hasField() {
      return this.designer.FieldWidgets != undefined && this.designer.FieldWidgets.length > 0;
    }
  },
  created() {
    if (this.definition.uuid) {
      Object.assign(this.definitionJson, JSON.parse(this.definition.definitionJson));
      Object.assign(this.definitionVjson, JSON.parse(this.definition.definitionVjson));
    } else {
      if (this.definition.definitionJson) {
        Object.assign(this.definitionJson, JSON.parse(this.definition.definitionJson));
      }
      if (this.definition.definitionVjson) {
        Object.assign(this.definitionVjson, JSON.parse(this.definition.definitionVjson));
      }
    }
    // FIXME: 设计器先按中文显示，后续要修改
    this.$i18n.locale = 'zh_CN';
  },
  methods: {
    validateFormId: debounce(function (rule, value, callback) {
      $axios
        .post('/json/data/services', {
          serviceName: 'formDefinitionService',
          methodName: 'isFormExistById',
          args: JSON.stringify([value])
        })
        .then(({ data }) => {
          callback(data.data === false ? undefined : '表单ID已存在');
        });
    }, 300),
    init() {
      if (this.dataModelUuid) {
        this.definitionVjson.dataModelUuid = this.dataModelUuid;
        this.definitionVjson.useDataModel = true;
      }
      this.getDataModelOptions();
      this.designer.widgets.splice(0, this.designer.widgets.length);
      if (this.definitionVjson.dataModelUuid) {
        this.getDataModelColumnJson(() => {
          this.designer.widgets.push(...deepClone(this.definitionVjson.widgets));
        });
      } else {
        this.designer.widgets.push(...deepClone(this.definitionVjson.widgets));
      }

      this.loadCategoryTreeData();
      this.getModuleOptions();
      this.getUserTableOptions();
      this.getCustomInterfaceOptions();
      this.setRequiredRule();

      if (this.definition.uuid) {
        this.getLatestUuidAndVersion(this.definition.id, data => {
          this.currentLatestVersion = data.version;
          this.currentLatestDyFormUuid = data.uuid;
        });
      }
    },
    onChangeDefinitionName: debounce(function () {
      if ((this.definition.id == undefined || this.definition.id.trim() == '') && !this.readOnlyId && this.definition.name) {
        this.translateCodeFromName();
      }
    }, 600),
    translateCodeFromName: debounce(function () {
      if (this.definition.name && !this.readOnlyId) {
        this.translating = true;
        this.$translate(this.definition.name, 'zh', 'en')
          .then(text => {
            this.translating = false;
            let val = text.toUpperCase().replace(/( )/g, '_');
            if (val.length > 24) {
              val = val.substring(0, 24);
            }
            this.$set(this.definition, 'id', val);
          })
          .catch(error => {
            this.translating = false;
          });
      }
    }, 200),
    getLatestUuidAndVersion(id, callback) {
      $axios
        .post('/json/data/services', {
          serviceName: 'formDefinitionService',
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
    setRequiredRule() {
      let requiredRule = {
        required: true,
        message: <a-icon type="close-circle" theme="filled" />,
        trigger: ['blur', 'change'],
        whitespace: true
      };

      this.defRules = {
        name: requiredRule,
        id: [
          requiredRule,
          {
            pattern: /^\w+$/,
            message: (
              <a-tooltip placement="bottomRight" arrowPointAtCenter visible>
                <a-icon type="close-circle" theme="filled" />
                <template slot="title">
                  <span>表单ID只允许包含字母、数字以及下划线</span>
                </template>
              </a-tooltip>
            ),
            //'表单ID只允许包含字母、数字以及下划线'
            trigger: 'blur'
          }
        ],
        moduleId: requiredRule
      };
      if (this.definition == undefined || this.definition.uuid == undefined) {
        this.defRules.id.push({
          trigger: ['blur', 'change'],
          validator: this.validateFormId,
          message: (
            <a-tooltip placement="bottomRight" arrowPointAtCenter visible>
              <a-icon type="close-circle" theme="filled" />
              <template slot="title">
                <span>表单ID已存在</span>
              </template>
            </a-tooltip>
          )
        });
      }
    },
    redirectModuleAssemble(id) {
      if (id) window.open(`/module/assemble/${id}`, '_blank');
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    getMobilePreviewJson() {
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
      if (this.previewId == undefined) {
        this.previewId = generateId();
      }
      this.previewWindow = window.open(
        (this._$SYSTEM_ID ? '/sys/' + this._$SYSTEM_ID + '/_' : '') +
          ('/dyform-designer/preview/' + this.previewId + (this.definition && this.definition.uuid ? '?uuid=' + this.definition.uuid : '')),
        this.previewId
      );
    },

    getSelectOptions(serviceName, queryMethod, optionParamKey, params = {}) {
      var _this = this;
      $axios
        .post('/common/select2/query', {
          serviceName,
          queryMethod,
          ...params
        })
        .then(({ data }) => {
          if (data.results) {
            _this[optionParamKey].splice(0, _this[optionParamKey].length);
            // _this[optionParamKey].length = 0;
            for (let i = 0, len = data.results.length; i < len; i++) {
              _this[optionParamKey].push({
                label: data.results[i].text,
                value: data.results[i].id
              });
            }
          }
        });
    },
    loadCategoryTreeData(keyword = '') {
      $axios.get(`/proxy/api/dyform/category/query`).then(({ data: result }) => {
        if (result.data) {
          let categories = result.data;
          let childNodes = categories.filter(item => item.parentUuid);
          let movedNodes = [];
          childNodes.forEach(child => {
            let childIndex = categories.findIndex(item => item.uuid == child.uuid);
            let parentNode =
              categories.find(item => item.uuid == child.parentUuid) || movedNodes.find(item => item.uuid == child.parentUuid);
            if (parentNode != null) {
              parentNode.children = parentNode.children || [];
              parentNode.children.push(child);
              if (childIndex != -1) {
                movedNodes = movedNodes.concat(categories.splice(childIndex, 1));
              }
            }
          });
          this.categoryTreeData = categories;
        }
      });
    },
    getModuleOptions() {
      this.getSelectOptions('appModuleMgr', 'loadSelectData', 'moduleOptions', { idProperty: 'id', includeSuperAdmin: 'true' });
    },
    getUserTableOptions() {
      this.getSelectOptions('cdDataStoreDefinitionService', 'loadSelectDataByTable', 'userTableOptions');
    },

    getCustomInterfaceOptions() {
      this.getSelectOptions('formDefinitionService', 'queryAllCustomFormRepositories', 'customInterfaceOptions');
    },
    saveTemp() {
      let urlParams = queryString(location.search.substr(1)),
        _temp = urlParams._temp || generateId();
      localStorage.setItem(_temp, JSON.stringify({ widgets: this.designer.widgets }));
      localStorage.setItem(_temp + 'FieldWidgets', JSON.stringify(this.designer.FieldWidgets));
      if (!urlParams._temp) {
        history.pushState({}, '页面设计', `${location.pathname}${location.search}${location.search ? '&' : '?'}_temp=${_temp}`);
      }
    },
    update(e, title, newVersion) {
      let _this = this;
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
          _this.save(e, true, newVersion);
        },
        onCancel() {}
      });
    },
    addUpgradeLog(uuid, version, remark, id) {
      $axios.post(`/proxy/api/app/res/upgrade/saveLog`, { resUuid: uuid, version, remark, id });
    },
    mockData() {
      window.open('/dyform-designer/preview/' + generateId() + '?uuid=' + this.definition.uuid, this.definition.uuid);
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
          .catch(error => {
            console.log(error);
          });
      });
    },
    save(e, yes, newVersion) {
      let _this = this;
      _this.$notification.close('validateNotify');
      _this.designer.emitEvent('closeDrawer:' + _this.designer.drawerVisibleKey);
      if (this.definition.uuid && yes !== true && newVersion) {
        this.update(e, '确定要更新表单定义吗?', newVersion);
        return;
      }

      let collectAndSubmit = function () {
        let result = this.convert2OldDefinitionJson();
        this.definition.tableName = result.definitionJson.tableName;
        this.definition.definitionJson = JSON.stringify(result.definitionJson);
        let columnJson = null;
        if (this.definitionVjson.useDataModel && this.definitionVjson.dataModelUuid) {
          this.definition.formType = 'V';
          columnJson = convertFormField2DataModelColumns(_this.designer.FieldWidgets, 'V');
          _this.updateNewestDmColumnConfigToFieldWidget();
        } else {
          this.definition.formType = 'P';
        }

        this.definition.definitionVjson = JSON.stringify({
          tableName: this.definition.tableName,
          widgets: this.designer.widgets,
          lifecycleHook: this.definitionVjson.lifecycleHook,
          fields: deepClone(this.designer.FieldWidgets),
          subforms: this.designer.WidgetSubforms,
          jsModule: this.definition.customJsModule,
          titleContent: this.definitionJson.titleContent,
          useDataModel: this.definitionVjson.useDataModel,
          dataModelUuid: this.definitionVjson.dataModelUuid,
          refDyform: this.definitionVjson.refDyform,
          version: this.definition.version
        });

        let { functionElements, appWidgetDefinitionElements } = this.getWidgetElements();

        console.log('表单定义', this.definition);
        let formData = new FormData();
        this.definition.isUp = newVersion ? '1' : '0';
        formData.set('formDefinition', JSON.stringify(this.definition));
        formData.set('deletedFieldNames', JSON.stringify(result.deletedFieldNames));

        const post = () => {
          $axios
            .post(`/proxy/pt/dyform/definition/${this.definition.uuid ? 'update' : 'save'}`, formData)
            .then(({ data }) => {
              if (data.success) {
                // 保存成功
                const oldUuid = _this.definition.uuid;
                // _this.definition.uuid = data.data;
                _this.$set(_this.definition, 'uuid', data.data);
                _this.defRules.id = [];
                _this.$message.success(newVersion ? '保存新版本成功, 当前为新版本!' : '保存成功');
                history.pushState({}, '表单设计', `${location.pathname}?uuid=${data.data}`);
                _this.definitionJson.fields = result.definitionJson.fields;
                _this.activeTabKey = '2';
                let notifyChange = () => {
                  _this.pageContext.emitCrossTabEvent('dyform:design:change:' + _this.definition.uuid, _this.definition);
                  if (!oldUuid) {
                    _this.pageContext.emitCrossTabEvent('dyform:design:create', _this.definition);
                  }
                };
                // 保存表单引用资源
                _this.saveFormResource(_this.definition, functionElements, appWidgetDefinitionElements);
                if (newVersion) {
                  // 升级版本

                  _this.getLatestUuidAndVersion(_this.definition.id, data => {
                    _this.addUpgradeLog(data.uuid, data.version, _this.upgradeRemark, _this.definition.id);
                    // 如果有在资源组内，则添加到最新版本后
                    $axios
                      .get(`/proxy/api/app/module/resGroup/updateMember`, {
                        params: { memberUuid: oldUuid, updateMemberUuid: _this.definition.uuid }
                      })
                      .then(() => {
                        // _this.pageContext.emitCrossTabEvent('ModuleResourceRefresh', 1);
                      });
                    _this.currentLatestDyFormUuid = data.uuid;
                    _this.currentLatestVersion = data.version;
                    _this.definition.version = data.version;
                    notifyChange();
                  });
                }

                if (columnJson && columnJson.length) {
                  // 需要合并增量字段到数模
                  _this.saveDataModelColumns(
                    columnJson,
                    success => {
                      // 不刷新页面
                      notifyChange();
                      if (success) {
                        for (let f of columnJson) {
                          _this.designer.emitEvent(`${f.id}:dataModelColumnChanged`);
                        }
                      }
                    },
                    true
                  );
                } else {
                  // 保存对应的数据模型
                  if (_this.definition.formType == 'P') {
                    columnJson = convertFormField2DataModelColumns(_this.designer.FieldWidgets, 'P');
                    columnJson = JSON.parse(JSON.stringify(DEFAULT_SYS_COLUMNS)).concat(columnJson);

                    // 获取数据模型
                    $axios.get(`/proxy/api/dm/getDetails`, { params: { id: _this.definition.id.toUpperCase() } }).then(({ data }) => {
                      _this.$confirm({
                        title: '提示',
                        content: data.data.uuid ? '同步更新数据模型: ' + data.data.name + ' ?' : '同步保存为数据模型',
                        okText: '确定',
                        cancelText: '取消',
                        onOk() {
                          if (data.data.uuid) {
                            data.data.createMainTable = false;
                            data.data.createRlTable = false;
                            data.data.createVnTable = true;
                            data.data.columnJson = JSON.stringify(columnJson);
                            _this.saveDataModel(data.data, () => {
                              notifyChange();
                            });
                          } else {
                            _this.saveDataModel(
                              {
                                id: _this.definition.id.toUpperCase(),
                                name: _this.definition.name,
                                type: 'TABLE',
                                columnJson: JSON.stringify(columnJson),
                                createMainTable: false,
                                createRlTable: false,
                                createVnTable: true,
                                module: _this.definition.moduleId
                              },
                              () => {
                                notifyChange();
                              }
                            );
                          }
                        },
                        onCancel() {
                          notifyChange();
                        }
                      });
                    });
                  } else {
                    notifyChange();
                  }
                }
              } else {
                _this.$message.error('保存失败');
                console.error('保存表单失败：', data);
                // 归滚字段数据
                // if (currentModifyDmFields) {
                //   for (let f of currentModifyDmFields) {
                //     delete f.column;
                //   }
                // }
              }
              _this.$loading(false);
            })
            .catch(error => {
              console.error(error);
              _this.$loading(false);
              if (error.response.data) {
                let { data, errorCode } = error.response.data;
                if (data.indexOf('有1条非空记录 , 无法删除') != -1) {
                  this.$message.error('存在非空数据, 无法删除字段或者修改字段编码');
                } else {
                  this.$message.error(data);
                }
              }
              // 归滚字段数据
              // if (currentModifyDmFields) {
              //   for (let f of currentModifyDmFields) {
              //     delete f.column;
              //   }
              // }
            });
        };
        if (result.deletedFieldNames.length) {
          this.$loading(false);
          this.$confirm({
            title: '提示',
            content: `检测到删除字段编码: ${result.deletedFieldNames.join(' ')} , 是否确定保存?`,
            onOk() {
              _this.$loading('表单保存中');
              post.call(_this);
            },
            onCancel() {}
          });
        } else {
          post.call(_this);
        }
      };

      this.designer.errorWidgetIds = [];
      this.$loading('表单保存中');
      this.$refs.defiForm.validate(function (vali) {
        if (vali) {
          let errorMsgs = [];
          // 校验各个组件的配置
          for (let id in _this.designer.widgetConfigurations) {
            _this.designer.isExistWidget(id);
            if (!_this.designer.widgetTreeMap[id]) {
              continue;
            }
            if (typeof _this.designer.widgetConfigurations[id].validate === 'function') {
              let result = _this.designer.widgetConfigurations[id].validate();
              if (result && result.length) {
                _this.designer.errorWidgetIds.push(id);
                console.error(result);
                errorMsgs.push({ id, name: _this.designer.widgetTreeMap[id].title, errors: result });
                vali = false;
              }
            }
          }
          // 生产环境上面validate()返回空数组
          if (vali) {
            let subformsformUuids = []; // 记录该表单内从表使用的表单，一个主表内多个从表的表单不能重复
            for (let id in _this.designer.widgetIdMap) {
              let widget = _this.designer.widgetIdMap[id];
              if (widget && widget.configuration && widget.configuration.isDatabaseField) {
                if (!widget.configuration.name || widget.configuration.name.trim() == '') {
                  errorMsgs.push({ id, name: widget.title, errors: ['字段名称必填'] });
                  vali = false;
                }
                let codeArr = [{ code: widget.configuration.code, name: '' }];
                // 时间区间选择的结束时间字段编码
                if (widget.subtype == 'Range') {
                  codeArr.push({ code: widget.configuration.endDateField, name: '结束' });
                }
                for (let _index in codeArr) {
                  if (!codeArr[_index].code || codeArr[_index].code.trim() == '') {
                    errorMsgs.push({ id, name: widget.title, errors: [codeArr[_index].name + '字段编码必填'] });
                    vali = false;
                  } else {
                    let noRepeat = _this.compareSimpleFieldInfos(id, codeArr[_index].code);
                    if (!noRepeat) {
                      vali = false;
                      errorMsgs.push({ id, name: widget.title, errors: ['字段编码 ' + codeArr[_index].code + ' 重复'] });
                    } else {
                      let preserved = _this.designer.isPreservedField(codeArr[_index].code);
                      if (preserved) {
                        errorMsgs.push({ id, name: widget.title, errors: ['字段编码 ' + codeArr[_index].code + ' 为系统预留编码'] });
                        vali = false;
                      }
                    }
                  }
                }
              } else if (widget && widget.wtype == 'WidgetSubform' && widget.configuration.formUuid) {
                if (subformsformUuids.includes(widget.configuration.formUuid)) {
                  errorMsgs.push({ id, name: widget.title, errors: ['选择表单 ' + widget.configuration.formName + ' 重复'] });
                  vali = false;
                } else {
                  subformsformUuids.push(widget.configuration.formUuid);
                }
              }
            }
          }

          if (vali) {
            let requiredColumns = document.querySelectorAll('.data-model-column.required');
            if (
              _this.definitionVjson.useDataModel &&
              _this.definitionVjson.dataModelUuid &&
              requiredColumns.length > 0 &&
              document.querySelectorAll('.data-model-column.field-used').length < requiredColumns.length
            ) {
              _this.$loading(false);
              _this.$refs.widgetSelectPanel.setTabActiveKey('dyform_data_model_property');
              _this.modelFieldFocused = [];
              for (let col of _this.dataModelColumns) {
                if (!_this.fieldCodeUsed.includes(col.column) && col.notNull) {
                  // 必填但是未选的情况下
                  _this.modelFieldFocused.push(col.column);
                }
              }

              _this.$confirm({
                title: '请确认',
                content: '存在必填数据模型字段未使用, 确认是否继续保存?',
                okText: '确认',
                cancelText: '取消',
                onOk() {
                  _this.$loading('表单保存中');
                  collectAndSubmit.call(_this);
                  setTimeout(() => {
                    _this.modelFieldFocused = [];
                  }, 3000);
                },
                onCancel() {
                  setTimeout(() => {
                    _this.modelFieldFocused = [];
                  }, 3000);
                }
              });
            } else {
              collectAndSubmit.call(_this);
            }
          } else {
            _this.$loading(false);
            _this.$notification.close('validateNotify');
            _this.$notification.error({
              class: 'design-notification',
              placement: 'bottomLeft',
              key: 'validateNotify',
              message: '错误信息',
              description: _this.renderError(errorMsgs),
              duration: null
            });
          }
        } else {
          _this.$refs.confPanel.onTabClick('basicInfo');
          _this.$loading(false);
        }
      });
    },
    // 判断字段code是否重复，遇到假值就停止迭代
    compareSimpleFieldInfos(id, code) {
      // 表单类字段组件
      let _this = this;
      return every(this.designer.SimpleFieldInfos, (item, index) => {
        if (id != item.id) {
          let same = code == item.code;
          if (same) {
            return false;
          }
          if (item.relaFields && item.relaFields.length > 0) {
            // 判断关联字段是否与其他字段重复
            for (let j = 0; j < item.relaFields.length; j++) {
              let relaField = item.relaFields[j];
              if (relaField.code == code) {
                return false;
              }
            }
          }
        }

        return true;
      });
    },

    updateNewestDmColumnConfigToFieldWidget() {
      for (let i = 0, len = this.designer.FieldWidgets.length; i < len; i++) {
        let f = this.designer.FieldWidgets[i];
        if (f.column != undefined && this.dataModelColumnMap[f.column.code.toLowerCase()] != undefined) {
          let { title, column, notNull, dataType, length, scale, unique } = this.dataModelColumnMap[f.column.code.toLowerCase()];
          if (length != f.column.length) {
            f.column.length = length;
            f.configuration.length = length;
          }
          if (f.wtype == 'WidgetFormInputNumber') {
            f.configuration.precision = length;
            f.configuration.decimalPlacesNumber = scale;
            f.configuration.scale = scale;
            f.column.scale = scale;
          }

          if (notNull && !f.configuration.required) {
            f.configuration.required = true;
            f.column.notNull = true;
          }

          if ((unique == 'GLOBAL' || unique == 'TENANT') && f.configuration.validateRule) {
            f.column.unique = unique;
            f.configuration.validateRule.uniqueType = unique == 'GLOBAL' ? 'globalUnique' : 'tenantUnique';
          }
        }
      }
    },

    saveDataModel(dm, callback) {
      $axios.post(`/proxy/api/dm/save`, dm).then(({ data }) => {
        if (typeof callback == 'function') {
          callback(data.code == 0);
        }
      });
    },
    saveDataModelColumns(columnJson, callback, merge) {
      if (columnJson && columnJson.length > 0) {
        let model = deepClone(this.currentDataModelDetail),
          _this = this,
          modelColJson = null;
        if (merge) {
          modelColJson = JSON.parse(model.columnJson);
          modelColJson.push(...columnJson);
          model.columnJson = JSON.stringify(modelColJson);
        } else {
          model.columnJson = JSON.stringify(columnJson);
        }
        modelColJson = JSON.parse(model.columnJson);
        console.log('保存数据模型字段定义: ', modelColJson);

        this.saveDataModel(model, success => {
          if (success) {
            _this.dataModelColumns = [];
            for (let c of modelColJson) {
              if (!c.isSysDefault) {
                c.column = c.column.toLowerCase();
                _this.dataModelColumns.push(c);
              }
            }
          }
          if (typeof callback == 'function') {
            callback(success);
          }
        });
      }
    },
    selectWidget() {
      this.designer.selectedByID(arguments[0].currentTarget.dataset.index);
    },
    renderError(errorMsgs) {
      let _this = this;
      return (
        <div class="validate-notification-container">
          {errorMsgs.map(item => {
            return (
              <div onClick={_this.selectWidget} data-index={item.id}>
                <label>{item.name}</label>
                <div>
                  {item.errors.map(e => {
                    return <div>{e}</div>;
                  })}
                </div>
              </div>
            );
          })}
        </div>
      );
    },
    convert2OldDefinitionJson() {
      let json = {
        tableName: this.definition.tableName || `UF_${this.definition.id}`,
        blocks: {},
        fields: {},
        databaseFields: {},
        subforms: {},
        repository: this.definitionJson.repository,
        titleType: this.definitionJson.titleType,
        titleContentVariables: this.definitionJson.titleContentVariables,
        useDataModel: this.definitionVjson.useDataModel,
        dataModelUuid: this.definitionVjson.dataModelUuid,
        tabs: {}
      };
      if (this.definition.uuid) {
        json.uuid = this.definition.uuid;
      }
      if (json.titleContentVariables && json.titleContentVariables.value) {
        json.titleContent = json.titleContentVariables.value;
      } else {
        json.titleContent = this.defaultTitleContent;
      }

      if (this.jsModule && this.jsModule.key) {
        this.definition.customJsModule = this.jsModule.key;
      }
      let oldFields = this.definition.uuid ? deepClone(this.definitionJson.fields) : {},
        fieldIdMap = {},
        oldFieldIdMap = {};
      if (Object.keys(oldFields).length) {
        for (let k in oldFields) {
          oldFieldIdMap[oldFields[k].id] = oldFields[k];
        }
      }

      // 转换字段
      if (this.designer.FieldWidgets) {
        for (let i = 0, len = this.designer.FieldWidgets.length; i < len; i++) {
          let field = this.designer.FieldWidgets[i];
          let element = document.querySelector('#design-item_' + field.id);
          if (element == null) {
            continue;
          }
          let fieldJson = {
            id: field.id,
            name: field.configuration.code,
            displayName: field.configuration.name,
            dbDataType: field.configuration.dbDataType || '1', // 未定义按 1(字符串处理)
            inputMode: field.configuration.inputMode || '1', // 未定义按 1(字符类输入模式)
            length: field.configuration.length,
            // 数字精度相关
            precision: field.configuration.precision,
            scale: field.configuration.scale,
            decimal: field.configuration.decimalPlacesNumber
          };

          if (field.wtype == 'WidgetFormDatePicker' || field.wtype == 'WidgetFormDatePicker$Range') {
            // 前端日期格式转后端日期格式
            let datePatternOptions = getOptions();
            let datePatternJson = deepClone(field.configuration.datePatternJson);
            if (!datePatternJson.contentFormat) {
              datePatternJson.contentFormat = find(datePatternOptions[field.configuration.datePatternType], {
                key0: datePatternJson.key0
              }).contentFormat;
            }
            let datePattern = datePatternJson.contentFormat.replace(/D/g, 'd');
            fieldJson.contentFormat = datePattern;
          }

          if (field.configuration.hasDefaultValue) {
            // 有默认值的情况下
            fieldJson.defaultValue = field.configuration.defaultValue;
            fieldJson.valueCreateMethod = field.configuration.valueCreateMethod;
          }

          if (field.configuration.applyToDatas && field.configuration.applyToDatas.length) {
            // 应用于字段更新
            let applyTo = [];
            for (let i = 0, len = field.configuration.applyToDatas.length; i < len; i++) {
              applyTo.push(field.configuration.applyToDatas[i].value);
            }
            fieldJson.applyTo = applyTo.join(';');
          }

          if (field.configuration.relaFieldConfigures) {
            // 组件设置的其他关联字段
            field.configuration.relaFieldConfigures.forEach((cf, i) => {
              if (cf.code != undefined) {
                let temp = {
                  id: fieldJson.id + '_' + i,
                  name: cf.code,
                  displayName: cf.name || fieldJson.displayName,
                  dbDataType: cf.dbDataType || fieldJson.dbDataType,
                  inputMode: cf.inputMode || fieldJson.inputMode,
                  length: cf.length || fieldJson.length,
                  precision: cf.precision || fieldJson.precision,
                  scale: cf.scale || fieldJson.scale,
                  decimal: cf.decimal || fieldJson.decimal
                };
                if (cf.hasDefaultValue) {
                  // 有默认值的情况下
                  temp.defaultValue = cf.defaultValue;
                  temp.valueCreateMethod = cf.valueCreateMethod;
                }
                if (field.subtype == 'Range') {
                  temp.contentFormat = fieldJson.contentFormat;
                }
                json.fields[cf.code] = temp;
                fieldIdMap[temp.id] = temp;
                if (oldFieldIdMap[temp.id] && oldFieldIdMap[temp.id].name != temp.name) {
                  // 改字段名
                  temp.oldName = oldFieldIdMap[temp.id].name;
                }
              }
            });
          }

          json.fields[field.configuration.code] = fieldJson;
          fieldIdMap[fieldJson.id] = fieldJson;
          if (oldFieldIdMap[field.id] && oldFieldIdMap[field.id].name != fieldJson.name) {
            // 改字段名
            fieldJson.oldName = oldFieldIdMap[field.id].name;
          }

          // 值分隔符
          if (field.configuration.separator) {
            fieldJson.valSeparator = field.configuration.separator;
          }
        }
      }

      // 新、旧字段对比删除不再需要的字段
      let currentFieldCodes = Object.keys(json.fields),
        deletedFieldNames = [];
      for (let key in oldFields) {
        if (fieldIdMap[oldFields[key].id] == undefined && !currentFieldCodes.includes(oldFields[key].name) && oldFields[key].name) {
          deletedFieldNames.push(oldFields[key].name);
        }
      }

      // 字段名称必填、新增的字段也有可能重复
      if (this.designer.SimpleFieldInfos) {
        let emptyNameFields = this.designer.SimpleFieldInfos.filter(field => !field.name || (field.name && field.name.trim() == ''));
        if (emptyNameFields.length > 0) {
          let emptyNameTip = emptyNameFields.map(
            field => this.designer.widgetIdMap[field.id].title || this.designer.widgetIdMap[field.id].name
          );
          this.$message.error(`字段[${emptyNameTip}]的字段名称不能为空`);
          this.$loading(false);
          throw new Error('字段名称为空');
        } else {
          // 新增的字段也有可能重复
          let fieldCodeMap = {};
          for (let index = 0; index < this.designer.SimpleFieldInfos.length; index++) {
            let fieldInfo = this.designer.SimpleFieldInfos[index];
            if (fieldCodeMap[fieldInfo.code] && json.fields[fieldInfo.code]) {
              this.$message.error(`字段[${json.fields[fieldInfo.code].displayName}]的字段编码存在重复, 不允许修改`);
              this.$loading(false);
              throw new Error('重复的列名');
            } else {
              fieldCodeMap[fieldInfo.code] = fieldInfo.code;
            }
          }
        }
      }

      // 转换布局
      if (this.designer.WidgetFormLayouts) {
        for (let i = 0, len = this.designer.WidgetFormLayouts.length; i < len; i++) {
          let layout = this.designer.WidgetFormLayouts[i];
          json.blocks[layout.configuration.code] = {
            blockTitle: layout.configuration.title,
            blockCode: layout.configuration.code,
            hide: !layout.configuration.defaultVisible,
            blockAnchor: false,
            id: layout.id
          };
        }
      }

      // 转换从表
      if (this.designer.WidgetSubforms) {
        for (let i = 0, len = this.designer.WidgetSubforms.length; i < len; i++) {
          let form = this.designer.WidgetSubforms[i];
          json.subforms[form.configuration.formUuid] = {
            formUuid: form.configuration.formUuid,
            outerId: form.configuration.formId,
            name: form.configuration.formName,
            displayName: form.configuration.title,
            fields: {},
            configuration: form.configuration,
            id: form.id
          };
          if (form.configuration.columns) {
            for (let c = 0, clen = form.configuration.columns.length; c < clen; c++) {
              let col = form.configuration.columns[c];
              json.subforms[form.configuration.formUuid].fields[col.dataIndex] = {
                name: col.dataIndex,
                displayName: col.title
              };
              let colConfiguration = deepClone(col); //从表列定义值
              delete colConfiguration.widget;
              json.subforms[form.configuration.formUuid].fields[col.dataIndex].colConfiguration = colConfiguration;
              if (col.widget) {
                if (col.widget.configuration.relaFieldConfigures) {
                  for (let r of col.widget.configuration.relaFieldConfigures) {
                    json.subforms[form.configuration.formUuid].fields[r.code] = {
                      name: r.code,
                      displayName: col.title,
                      colConfiguration: colConfiguration
                    };
                  }
                }
              }
            }
          }
        }
      }

      // 转换标签页
      if (this.designer.WidgetTabs) {
        for (let i = 0, len = this.designer.WidgetTabs.length; i < len; i++) {
          let tabs = this.designer.WidgetTabs[i].configuration.tabs;
          for (let j = 0, len = tabs.length; j < len; j++) {
            let tab = tabs[j];
            json.tabs[tab.id] = {
              title: tab.title,
              id: tab.id,
              parentId: this.designer.WidgetTabs[i].id,
              isActive: this.designer.WidgetTabs[i].configuration.defaultActiveKey == tab.id
            };
          }
          json.tabs[this.designer.WidgetTabs[i].id] = {
            title: this.designer.WidgetTabs[i].title,
            id: this.designer.WidgetTabs[i].id,
            defaultActiveKey: this.designer.WidgetTabs[i].configuration.defaultActiveKey
          };
        }
      }

      return { definitionJson: json, deletedFieldNames: deletedFieldNames };
    },
    getWidgetI18nObjects(widget) {
      function findI18nObjects(json, i18n) {
        function traverse(obj, i18n) {
          if (typeof obj !== 'object' || obj === null || (obj.wtype && obj.id)) return;
          if (Array.isArray(obj)) {
            for (let item of obj) {
              traverse(item, i18n);
            }
          } else {
            for (let key in obj) {
              if (key === 'i18n' && obj[key] != undefined && obj[key].zh_CN != undefined) {
                i18n.push(obj[key]);
                continue;
              }
              traverse(obj[key], i18n);
            }
          }
        }
        traverse(json, i18n);
      }
      const i18n = [];
      findI18nObjects(widget.configuration, i18n);
      return i18n;
    },
    getWidgetElements() {
      return this.designer.getWidgetElements();
    },

    // 保存表单引用资源
    saveFormResource(formDefinition, functionElements, appWidgetDefinitionElements) {
      const _this = this;
      if (!functionElements) {
        return;
      }

      let formResources = [];
      for (let widgetId in functionElements) {
        let wgt = _this.designer.widgetIdMap[widgetId];
        formResources.push({
          dataDefUuid: formDefinition.uuid,
          dataDefName: formDefinition.name,
          dataDefType: 'formDefinition',
          itemId: widgetId,
          itemName: `${wgt.configuration.name}(${wgt.name})`,
          functionElements: functionElements[widgetId],
          isProtected: false,
          configType: '1',
          moduleId: formDefinition.moduleId
        });
      }

      $axios.post(`/proxy/api/app/datadef/saveRefResources/${formDefinition.uuid}`, formResources);
      $axios.post(`/proxy/api/webapp/page/definition/saveWidgetDefinitions`, {
        appId: formDefinition.moduleId,
        uuid: formDefinition.uuid,
        id: formDefinition.id,
        wtype: 'vForm',
        version: formDefinition.version,
        appWidgetDefinitionElements
      });
    },

    onChangeFormTitle() {
      if (this.definitionJson.titleType == '1') {
        // 默认
        // ${表单名称}_${年}-${月}-${日}
        this.$refs.variableTpt.update({
          variables: [
            { label: '表单名称', value: '${表单名称}' },
            { label: '_', value: '_', edit: false },
            { label: '年', value: '${年}' },
            { label: '-', value: '-', edit: false },
            { label: '月', value: '${月}' },
            { label: '-', value: '-', edit: false },
            { label: '日', value: '${日}' }
          ],
          value: this.defaultTitleContent
        });
      }
    },

    updateVariableTreeData() {
      let fieldVarNode = {
        title: '表单字段',
        key: 'formFieldVar',
        value: 'formFieldVar',
        selectable: false,
        children: []
      };
      this.fieldCodeUsed.splice(0, this.fieldCodeUsed.length);
      for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
        let info = this.designer.SimpleFieldInfos[i];
        fieldVarNode.children.push({
          title: info.name,
          key: info.code,
          value: '${dyform.' + info.code + '}'
        });
        this.fieldCodeUsed.push(info.code);
        if (info.relaFields) {
          for (let r of info.relaFields) {
            this.fieldCodeUsed.push(r.code);
          }
        }
      }
      // console.log('已使用字段编码: ', this.fieldCodeUsed);
      // 变量树绑定到设计器，提供设计器组件使用
      this.designer.variableTreeData = [
        this.getSimpleLabelVariables(
          {
            title: '表单变量',
            key: 'formVar',
            value: 'formVar',
            selectable: false,
            children: []
          },
          ['表单名称', '表单ID', '表单编号']
        ),
        fieldVarNode,
        this.getSimpleLabelVariables(
          {
            title: '日期变量',
            key: 'dateVar',
            value: 'dateVar',
            selectable: false,
            children: []
          },
          ['年', '简年', '月', '日', '时', '分', '秒']
        ),

        this.getSimpleLabelVariables(
          {
            title: '用户变量',
            key: 'userVar',
            value: 'userVar',
            selectable: false,
            children: []
          },
          ['当前登录用户姓名', '当前登录用户所在部门名称', '当前登录用户所在部门名称全路径']
        )
      ];
    },

    getSimpleLabelVariables(parentNode, labels) {
      for (let i = 0, len = labels.length; i < len; i++) {
        parentNode.children.push({
          title: labels[i],
          key: labels[i],
          value: '${' + labels[i] + '}'
        });
      }
      return parentNode;
    },
    onChangeUseDataModelSwitch(checked) {
      if (checked) {
        // 选中属性tab
        this.$refs.widgetSelectPanel.setTabActiveKey('dyform_data_model_property');
      } else {
        this.$refs.widgetSelectPanel.selectFirstTab();
        this.definition.tableName = undefined;
      }
    },
    onSelectChangeDataModel() {
      if (this.definitionVjson.dataModelUuid) {
        this.definition.tableName = 'UF_' + this.dataModelMap[this.definitionVjson.dataModelUuid].id;
      }
      this.getDataModelColumnJson(date => {
        let dateStr = new Date(date).format('yyyyMMDDHHmmss');
        // if (!this.definition.id) {
        this.definition.id = this.dataModelMap[this.definitionVjson.dataModelUuid].id + '_' + dateStr;
        // }
        if (!this.definition.name) {
          this.$set(this.definition, 'name', this.dataModelMap[this.definitionVjson.dataModelUuid].name + '_' + dateStr);
        }
      });
    },
    onInputId2CaseFormate(e, caseType) {
      if (this.definition.id != undefined) {
        if (caseType === 'toUpperCase' || caseType === 'toLowerCase') {
          // 自动转大写
          this.definition.id = this.definition.id[caseType]();
          let start = e.target.selectionStart;
          this.$nextTick(() => {
            e.target.setSelectionRange && e.target.setSelectionRange(start, start);
          });
        }
      }
    },
    getDataModelColumnJson(callback) {
      let _this = this;
      _this.dataModelColumns.splice(0, _this.dataModelColumns.length);
      Object.setPrototypeOf(this.dataModelColumnMap, null);
      _this.designer.dataModelColumnMap = this.dataModelColumnMap;
      if (this.definitionVjson.dataModelUuid) {
        $axios.get(`/proxy/api/dm/getDetails`, { params: { uuid: this.definitionVjson.dataModelUuid } }).then(({ data, headers }) => {
          if (data.code == 0) {
            let detail = data.data,
              columns = JSON.parse(detail.columnJson);
            if (data.data.uuid == undefined) {
              this.$error({
                title: '错误',
                content: '不存在的数据模型',
                onOk: () => {
                  window.close();
                }
              });
              return;
            }
            _this.currentDataModelDetail = detail;
            _this.definition.moduleId = data.data.module;
            _this.definition.tableName = `UF_${detail.id}`;
            if (_this.definition.uuid == undefined) {
              let date = new Date();
              let dateStr = new Date(date).format('yyyyMMDDHHmmss');
              _this.definition.id = data.data.id + '_' + dateStr;
              _this.$set(_this.definition, 'name', data.data.name + '_' + dateStr);
            }
            for (let c of columns) {
              if (!c.isSysDefault) {
                c.column = c.column.toLowerCase();
                _this.dataModelColumns.push(c);
                this.dataModelColumnMap[c.column] = c;
              }
            }

            if (typeof callback === 'function') {
              callback.call(_this, headers.date);
            }
          }
        });
      }
    },
    getDataModelOptions() {
      let _this = this;
      _this.dataModelOptions = [];
      _this.dataModelMap = {};
      $axios
        .post(`/proxy/api/dm/getDataModelsByType`, {
          type: ['TABLE'],
          module: this.definition.moduleId != undefined ? [this.definition.moduleId] : undefined
        })
        .then(({ data }) => {
          if (data.code == 0) {
            let models = data.data;
            if (models) {
              for (let m of models) {
                _this.dataModelOptions.push({ label: m.name, value: m.uuid });
                _this.dataModelMap[m.uuid] = m;
              }
            }
          }
        });
    },
    handleModelColClone(origin) {
      return {
        id: '-1',
        wtype: 'WidgetDmColumnPlaceholder',
        configurationDisabled: true,
        configuration: { ...origin }
      };
    },

    redirectDataModelDesign(uuid) {
      if (uuid) {
        window.open(`/data-model-design/index?uuid=${uuid}`, '_blank');
      }
    },
    initDesignWidgets() {
      // 加载组件元数据
      import('@modules/.webpack.widget.meta-info.js').then(info => {
        let widgets = info.default;
        // 组件归类
        // console.log('载入组件信息: ', info.default, this);
        this.designWidgets = {};
        // 按使用排序
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
                }
              }
            }
            this.widgetMeta[w.wtype] = w;
            if ((w.scope == 'dyform' || (Array.isArray(w.scope) && w.scope.includes('dyform'))) && w.wtype.indexOf('$') == -1) {
              this.designWidgetTypes.add(w.wtype);
            }
          }
          this.widgetMetaLoading = false;
        });
      });
    },
    // onDragstart(e) {
    //   let item = e.item._underlying_vm_;
    //   if (this.designer.SimpleFieldInfos && item.configuration.column) {
    //     for (let f of this.designer.SimpleFieldInfos) {
    //       if (f.code.toLowerCase() == item.configuration.column.toLowerCase()) {
    //         e.preventDefault();
    //         return false;
    //       }
    //     }
    //   }
    // }

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
    onChangeModuleSelect() {
      this._provided.appId = this.definition.moduleId;
      this.designer.emitEvent(`appId:change`, this.definition.moduleId);
    },
    getPreviewJson() {
      return JSON.stringify({
        widgets: this.designer.widgets,
        jsModule: this.jsModule != undefined ? this.jsModule.key : undefined
      });
    },
    getMobilePageUrl() {
      return `/packages/_/pages/dyform/preview${this.definition.uuid ? '?formUuid=' + this.definition.uuid : ''}`;
    }
  },
  beforeMount() {
    this.dyform.vueInstance = this;
    window.dyform = this.dyform;
    this.designer.h5Server = this.h5Server;
    let importLibs = [
      import('@dyform/app/web/framework/vue/installEditor'),
      import('@pageAssembly/app/web/framework/vue/installEditor'),
      import('@workflow/app/web/framework/vue/installEditor'),
      import('@pageAssembly/app/web/framework/vue/installUniEditor')
    ];
    Promise.all(importLibs).then(() => {
      this.editorInstalled = true;
    });
    this.fetchLocaleOptions();

    let _this = this;
    this.designer.vueInstance = this;
    this.designer.designWidgets = this.designWidgets;
    if (this.jsModule != undefined) {
      this.designer.pageJsModule = this.jsModule;
    }
    this.initDesignWidgets();
    // 字段组件集合
    if (!this.designer.hasOwnProperty('FieldWidgets')) {
      this.$set(this.designer, 'FieldWidgets', []);
    }
    if (!this.designer.hasOwnProperty('SimpleFieldInfos')) {
      this.$set(this.designer, 'SimpleFieldInfos', []);
    }

    // 保留字判断
    this.designer.isPreservedField = function (code) {
      return _this.preservedFields.indexOf(code.toLowerCase()) != -1;
    };

    this.designer.variableTreeData = []; // 变量树数据
    window.designer = this.designer;
    let urlParams = queryString(location.search.substr(1));
    if (urlParams._temp) {
      if (localStorage.getItem(urlParams._temp) != null) {
        this.designer.widgets = JSON.parse(localStorage.getItem(urlParams._temp)).widgets;
      }
    }
    this.scrollerHeight = window.innerHeight - 51 + 'px';
    addWindowResizeHandler(() => {
      this.$nextTick(() => {
        this.scrollerHeight = window.innerHeight - 51 + 'px';
      });
    });

    this.init();
  },
  mounted() {
    this.designer.handleEvent(`widget:design:destroy`, ref => {
      if (ref.widget.refDyformId != undefined) {
        // 删除字段
        this.$delete(this.definitionVjson.refDyform[ref.widget.refDyformId].field, ref.widget.configuration.code);
        // this.designer.emitEvent(`widget:refDyformField:destroy`, { formId: ref.widget.refDyformId, code: ref.widget.configuration.code });
      }
    });
  },

  watch: {
    jsModule: {
      deep: true,
      handler(v) {
        this.designer.pageJsModule = v;
        if (v == undefined) {
          this.definition.customJsModule = undefined;
        }
      }
    },
    'designer.SimpleFieldInfos': {
      deep: true,
      handler(v) {
        this.updateVariableTreeData();
      }
    }
  }
};
</script>
