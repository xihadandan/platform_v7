<template>
  <HtmlWrapper :title="vTitle">
    <a-layout class="widget-design-layout">
      <a-layout-header class="widget-design-header">
        <a-row>
          <a-col :span="12">
            <a-icon type="desktop" />
            <h1>大屏设计</h1>
          </a-col>
          <a-col :span="12" :style="{ textAlign: 'right' }">
            <a-button icon="save" @click="e => savePageDesign(false)">保存</a-button>
            <a-button icon="upload" @click="e => savePageDesign(true)" v-if="pageDefinition.uuid != null">保存新版本</a-button>
            <a-button icon="save" @click="saveTemp">本地暂存</a-button>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout :hasSider="true">
        <a-layout-sider theme="light" :width="364">
          <div class="spin-center" v-if="widgetMetaLoading">
            <a-spin />
          </div>
          <WidgetBigscreenSelectPanel v-else :designWidgets="designWidgets"></WidgetBigscreenSelectPanel>
        </a-layout-sider>
        <a-layout-content id="design-main">
          <WidgetBigscreenBuildPanel
            v-if="!widgetMetaLoading && !installing"
            :screen-height="pageStyle.height"
            :screen-with="pageStyle.width"
            :pageStyle="pageStyle"
          ></WidgetBigscreenBuildPanel>
        </a-layout-content>
        <a-layout-sider theme="light" class="widget-configuration-sider" :width="380">
          <WidgetConfigurationPanel :designer="designer" ref="wConfigurePanel">
            <template slot="basicInfoTab">
              <a-tab-pane key="basicInfo" tab="页面信息">
                <a-tabs size="small" class="flex-card-tabs">
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
                      <a-form-model-item label="名称" prop="name">
                        <a-input v-model="pageDefinition.name" allow-clear />
                      </a-form-model-item>
                      <a-form-model-item label="ID" prop="id">
                        <a-input v-model="pageDefinition.id" :readOnly="pageDefinition.uuid != null" :maxLength="64" />
                      </a-form-model-item>
                      <a-form-model-item label="页面脚本">
                        <JsModuleSelect :multiSelect="false" v-model="designer.pageJsModule" dependencyFilter="VuePageDevelopment" />
                      </a-form-model-item>
                      <a-form-model-item label="版本" v-show="pageDefinition.uuid != null">
                        {{ 'v' + pageDefinition.version }}
                      </a-form-model-item>
                      <a-form-model-item label="编号">
                        <a-input v-model="pageDefinition.code" />
                      </a-form-model-item>

                      <a-form-model-item label="标题">
                        <a-input v-model="pageDefinition.title" />
                      </a-form-model-item>
                      <a-form-model-item class="page-belong" prop="appId">
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
                          :filter-option="filterSelectOption"
                          :disabled="pageDefinition.uuid != undefined"
                          @change="onChangeAppIdSelect"
                        />
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
                  <a-tab-pane key="setting" tab="样式">
                    <a-form-model
                      class="basic-info"
                      labelAlign="left"
                      ref="basicForm"
                      :rules="rules"
                      :label-col="{ span: 7 }"
                      :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
                      :colon="false"
                    >
                      <a-form-model-item>
                        <template slot="label">
                          <a-popover placement="leftTop" title="推荐分辨率">
                            <template slot="content">
                              <div style="width: 200px; display: flex; flex-wrap: wrap; justify-content: space-around">
                                <template v-for="(opt, i) in bigScreenSizeOptions">
                                  <a-button
                                    type="link"
                                    size="small"
                                    @click="
                                      e => {
                                        pageStyle.width = opt[0];
                                        pageStyle.height = opt[1];
                                      }
                                    "
                                  >
                                    {{ opt[0] }} x {{ opt[1] }}
                                  </a-button>
                                </template>
                              </div>
                            </template>
                            <a-button size="small" type="link">大屏分辨率</a-button>
                          </a-popover>
                        </template>
                        <a-input-number v-model="pageStyle.width" :min="100" :max="9000" style="width: 80px" />
                        x
                        <a-input-number v-model="pageStyle.height" :min="100" :max="9000" style="width: 80px" />
                      </a-form-model-item>

                      <a-form-model-item label="背景颜色">
                        <ColorPicker v-model="pageStyle.backgroundColor" :allowClear="true" />
                      </a-form-model-item>
                      <a-form-model-item label="背景图片">
                        <ImageLibrary v-model="pageStyle.backgroundImage" style="float: right" />
                      </a-form-model-item>
                      <a-form-model-item label="背景位置">
                        <a-auto-complete
                          :data-source="['top', 'center', 'left', 'right']"
                          v-model.trim="pageStyle.backgroundPosition"
                          :filter-option="filterOption"
                          placeholder="请输入背景位置样式"
                          allow-clear
                        />
                      </a-form-model-item>
                      <a-form-model-item label="背景重复">
                        <a-auto-complete
                          :data-source="['no-repeat', 'repeat-x', 'repeat-y', 'repeat', 'space', 'round']"
                          v-model.trim="pageStyle.backgroundRepeat"
                          :filter-option="filterOption"
                          placeholder="请输入背景重复"
                          allow-clear
                        />
                      </a-form-model-item>
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
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import '../../assets/css/design.less';
import WidgetBigscreenSelectPanel from './component/bigscreen/widget-bigscreen-select-panel.vue';
import WidgetBigscreenBuildPanel from './component/bigscreen/widget-bigscreen-build-panel.vue';
import WidgetConfigurationPanel from './component/widget-configuration-panel.vue';
import { createDesigner } from '@framework/vue/designer/designer';
import { filterSelectOption } from '@framework/vue/utils/function';
import { queryString, generateId } from '@framework/vue/utils/util';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
import { customFileUploadRequest } from '@framework/vue/utils/function';
import { orderBy } from 'lodash';

export default {
  name: 'BigScreenDesigner',
  props: {},
  components: { WidgetBigscreenSelectPanel, WidgetBigscreenBuildPanel, WidgetConfigurationPanel, ColorPicker, ImageLibrary },
  computed: {
    vTitle() {
      let title = this.title || '大屏设计';
      if (this.pageDefinition.name) {
        title += ' - ' + this.pageDefinition.name;
      }
      return title;
    }
  },
  data() {
    return {
      designer: createDesigner(),
      designWidgets: [],
      widgetMeta: {},
      designWidgetTypes: new Set(),
      widgetMetaLoading: true,
      moduleOptions: [],
      draggableConfig: {
        filterWtype: [],
        filter: undefined,
        dragGroup: 'dragGroup' // 用于控制设计组件可拖拽的区域
      },
      pageStyle: {
        position: 'absolute',
        width: 2560,
        height: 1440,
        backgroundColor: '#151a26', // 默认黑底
        backgroundImage: undefined,
        backgroundImageInput: undefined,
        bgImageUseInput: false,
        backgroundRepeat: 'round',
        backgroundPosition: undefined
      },
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
      bigScreenSizeOptions: [
        [1024, 768],
        [1280, 720],
        [2560, 1440],
        [3840, 2160],
        [5120, 2880],
        [7680, 4320]
      ],
      pageResources: [],
      upgradeRemark: ''
    };
  },
  provide() {
    return {
      appId: this.pageDefinition.appId,
      designer: this.designer,
      draggableConfig: this.draggableConfig,
      designMode: true,
      widgetMeta: this.widgetMeta,
      installing: true
    };
  },
  beforeCreate() {},
  created() {
    this.designer.isBigScreenDesign = true;
  },
  beforeMount() {
    this.designer.vueInstance = this;

    this.initDesignWidgets();
    import('@pageAssembly/app/web/framework/vue/installBigScreenWidgetDesign').then(() => {
      this.installing = false;
    });

    if (this.pageDefinition && this.pageDefinition.definitionJson) {
      let json = JSON.parse(this.pageDefinition.definitionJson);
      this.designer.widgets = json.items;
      this.designer.pageVars = json.vars;
      this.designer.pageJsModule = json.js;
      if (json.style) {
        Object.assign(this.pageStyle, json.style);
      }
      if (json.pageParams != undefined && json.pageParams.length > 0) {
        this.pageParams.push(...json.pageParams);
      }
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
  },
  mounted() {
    this.setRequiredRule();
    window.designer = this.designer;
  },
  methods: {
    getWidgetElements() {
      let functionElements = {};
      let appWidgetDefinitionElements = [];
      if (this.designer.widgets.length != 0) {
        //解析各个组件配置的功能元素、以及生成后端保存的定义信息
        let convertWidget = wgt => {
          if (wgt) {
            let type = `${wgt.wtype}Configuration`;
            if (window.Vue.options.components[type] && window.Vue.options.components[type].options.methods.getFunctionElements) {
              Object.assign(functionElements, window.Vue.options.components[type].options.methods.getFunctionElements(wgt));
            }

            if (window.Vue.options.components[type] && window.Vue.options.components[type].options.methods.getWidgetDefinitionElements) {
              appWidgetDefinitionElements = appWidgetDefinitionElements.concat(
                window.Vue.options.components[type].options.methods.getWidgetDefinitionElements(wgt)
              );
            } else {
              appWidgetDefinitionElements.push({
                wtype: wgt.wtype,
                title: wgt.title,
                id: wgt.id,
                definitionJson: JSON.stringify(wgt)
              });
            }
          }
        };
        for (let id in this.designer.widgetIdMap) {
          convertWidget(this.designer.widgetIdMap[id]);
        }

        // 补充保存产品主页的导航配置组件
        if (
          this.$refs.prodIndexNavDesign &&
          this.$refs.prodIndexNavDesign.widgetMenu &&
          this.designer.widgetIdMap[this.$refs.prodIndexNavDesign.widgetMenu.id] == undefined
        ) {
          convertWidget(this.$refs.prodIndexNavDesign.widgetMenu);
        }
      }
      return { functionElements, appWidgetDefinitionElements };
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
        history.pushState({}, '大屏设计', `${location.pathname}${location.search}${location.search ? '&' : '?'}_temp=${_temp}`);
      }
    },

    savePageDesign(newVersion) {
      let _this = this;

      let collectAndSubmit = function () {
        _this.$loading('保存中');
        _this.designer.emitEvent('closeDrawer:' + _this.designer.drawerVisibleKey);
        let { functionElements, appWidgetDefinitionElements } = _this.getWidgetElements();

        // 通过组件定义解析出功能元素
        let definitionJson = {
          wtype: 'vPage',
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

        this.pageDefinition.functionElements = functionElements;
        // this.pageDefinition.piUuid = this.pageDefinition.appPiUuid;
        this.pageDefinition.newVersion = newVersion === true;
        this.pageDefinition.isPc = '1';
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
              // _this.updateProtectedResource(_uuid);

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
              _this.$message.success(`保存${newVersion === true ? '新版本' : ''}成功${newVersion === true ? ', 当前为新版本' : ''}!`);
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
    addUpgradeLog(uuid, version, remark, id) {
      $axios.post(`/proxy/api/app/res/upgrade/saveLog`, { resUuid: uuid, version, remark, id });
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
    onChangeAppIdSelect() {
      this._provided.appId = this.pageDefinition.appId;
    },
    filterSelectOption,
    redirectModuleAssemble(id, target = '_blank') {
      if (id) window.open(`/module/assemble/${id}`, target);
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
    customRequest(e) {
      this.uploading = true;
      customFileUploadRequest(e).then(dbFile => {
        this.uploading = false;
        this.pageStyle.backgroundImage = `/proxy-repository/repository/file/mongo/download?fileID=${dbFile.fileID}`;
      });
    },
    beforeUpload(file) {
      let isJpgOrPng = ['image/gif', 'image/jpeg', 'image/png'].includes(file.type);
      if (!isJpgOrPng) {
        this.$message.error('只允许上传 jpeg、png 或者 gif 图片格式');
      }
      return isJpgOrPng;
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
            if (w.scope == 'bigScreen' || (Array.isArray(w.scope) && w.scope.includes('bigScreen'))) {
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
          console.log(this.designWidgets);
        });
      });
    }
  }
};
</script>
