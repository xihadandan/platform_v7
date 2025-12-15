<template>
  <HtmlWrapper title="预览">
    <a-layout v-if="!closeDesigner" class="preview-form-layout">
      <a-layout-header :style="{ textAlign: 'right' }">
        <a-row>
          <a-col :span="10">
            <h1 :style="{ float: 'left', color: '#fff' }">{{ title }}</h1>
          </a-col>
          <a-col :span="4" style="text-align: center">
            <a-radio-group
              v-model="terminalType"
              button-style="solid"
              class="design-terminal-type-radio radio-ghost"
              @change="onChangeTerminalType"
            >
              <a-radio-button value="pc" style="width: 100px">
                <i class="pticon iconfont icon-ptkj-dagou" v-show="terminalType == 'pc'" />
                PC 端
              </a-radio-button>
              <a-radio-button value="mobile" style="width: 100px">
                <i class="pticon iconfont icon-ptkj-dagou" v-show="terminalType == 'mobile'" />
                移动端
              </a-radio-button>
            </a-radio-group>
          </a-col>

          <a-col :span="10" style="text-align: right">
            <a-select :options="i18nOption" style="width: 150px" v-model="selectLocale" @change="onChangeLocale"></a-select>
            <template v-if="uuid">
              <a-select v-model="displayState" style="width: 120px" @change="onChangeDisplayState" v-show="!isVersionDataView">
                <a-select-option value="edit">可编辑</a-select-option>
                <a-select-option value="readonly">只读</a-select-option>
                <a-select-option value="disable">禁用</a-select-option>
                <a-select-option value="label">不可编辑</a-select-option>
              </a-select>
              <a-button type="primary" ghost icon="save" @click="onClickSave" :loading="saveLoading" v-show="!isVersionDataView">
                保存
              </a-button>

              <a-dropdown v-if="vDataUuid != undefined && terminalType == 'pc'">
                <a-button type="primary" ghost>
                  数据版本
                  <a-icon type="down" />
                </a-button>

                <a-menu slot="overlay">
                  <a-menu-item key="onClickSaveNewVersion" v-if="!isVersionDataView">
                    <label @click="onClickSaveNewVersion">保存为新版本</label>
                  </a-menu-item>
                  <a-menu-item key="backToLatestForm" v-if="isVersionDataView">
                    <label @click="backToLatestForm">返回最新版本</label>
                  </a-menu-item>

                  <a-menu-item key="showDataVersionList">
                    <Drawer
                      v-if="vDataUuid != undefined && terminalType == 'pc'"
                      title="数据版本列表"
                      style="text-align: left"
                      ref="versionDrawer"
                      :container="getVersionContainer"
                    >
                      <template slot="content">
                        <a-timeline>
                          <a-timeline-item v-for="(item, i) in dataVerList" :key="'datav' + i">
                            <div style="display: flex; align-items: baseline; justify-content: space-between; font-weight: bolder">
                              版本 {{ Number(item.version).toFixed(1) }}
                              <a-button size="small" type="link" @click="showDataVersionDetail(item)">查看</a-button>
                            </div>

                            <p style="color: #999">{{ item.create_time }}</p>
                          </a-timeline-item>
                        </a-timeline>
                      </template>
                      <label @click="fetchDataVersionList">查看版本列表</label>
                    </Drawer>
                  </a-menu-item>
                </a-menu>
              </a-dropdown>

              <a-button
                v-show="!isVersionDataView"
                type="danger"
                ghost
                icon="delete"
                @click="onClickDelete"
                v-if="vDataUuid"
                :loading="deleteLoading"
              >
                删除
              </a-button>
            </template>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout-content v-if="startRender">
        <div v-show="terminalType == 'pc'">
          <WidgetDyform
            :key="displayState + dyformKey"
            :displayState="displayState"
            :formUuid="formUuid"
            :definitionVjson="definitionVjson"
            :dataUuid="vDataUuid"
            ref="wDyform"
            :formDatas="formDatas"
            :isVersionDataView="isVersionDataView"
            @formDataChanged="onFormDataChanged"
            :dyformStyle="{ padding: 'var(--w-padding-md)' }"
          />
        </div>
        <div v-if="terminalType == 'mobile'" style="display: flex; align-items: center; justify-content: center">
          <MobilePreview
            displayStyle="direct"
            :previewJson="getPreviewJson"
            width="100%"
            :h5Url="h5Server"
            :pageUrl="getMobilePageUrl"
            ref="mobilePreview"
            @message="onPreviewMessage"
            :domain="domain"
            qrcode
          ></MobilePreview>
        </div>
      </a-layout-content>
    </a-layout>
    <a-result title="设计器已退出 , 无法预览" v-else></a-result>
  </HtmlWrapper>
</template>
<style lang="less">
.preview-form-layout {
  background: linear-gradient(to bottom, var(--w-primary-color), #f4f4f4 40%);

  // 顶部有bg的情况:
  // &::before {
  //   content: '';
  //   position: absolute;
  //   top: 0;
  //   left: 0;
  //   right: 0;
  //   bottom: 0;
  //   background-image: url(/static/images/form/dyform-bg.png);
  //   background-size: contain;
  //   background-position: top;
  //   background-repeat: no-repeat;
  //   opacity: 0.2;
  //   z-index: 1;
  // }

  .design-terminal-type-radio {
    .ant-radio-button-wrapper-checked {
      border-color: #fff !important;
    }
  }
  > .ant-layout-content {
    padding: 0px 20px 20px 20px;
    border-radius: 2px;
    > div {
      background-color: #fff;
      min-height: e('calc(100vh - 84px)');
    }
  }
  > .ant-layout-header {
    background: transparent;
  }
}
</style>

<script type="text/babel">
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
import { debounce, merge, set } from 'lodash';
import { deepClone, asyncLoadLocaleData } from '@framework/vue/utils/util';
import MobilePreview from '@pageAssembly/app/web/page/page-designer/component/mobile-preview.vue';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';

export default {
  name: 'DyformPagePreview',
  props: {},
  data() {
    return {
      i18nOption: [],
      deleteLoading: false,
      saveLoading: false,
      displayState: 'edit',
      title: '',
      installed: false,
      closeDesigner: false,
      terminalType: 'pc',
      dyformKey: new Date().getTime(),
      selectLocale: 'zh_CN', // this.$i18n.locale
      terminalType: 'pc',
      saveNewVersioning: false,
      dataVerList: [],
      versionDataLoading: false,
      formDatas: undefined,
      definitionVjson: undefined,
      definitionVjsonCopy: undefined // 复制初始定义值
    };
  },
  provide() {
    return {
      $pageJsInstance: {},
      designMode: false,
      draggableConfig: {
        filterWtype: [],
        filter: undefined,
        dragGroup: 'dragGroup' // 用于控制设计组件可拖拽的区域
      },
      designWidgetTypes: new Set(),
      unauthorizedResource: [],
      designer: {}
    };
  },
  beforeCreate() {},
  components: { MobilePreview, Drawer },
  computed: {
    formUuid() {
      return this.uuid || '-1';
    },
    vDataUuid() {
      return this.dataUuid;
    },
    startRender() {
      return ((this.definitionVjson != null && Object.keys(this.definitionVjson).length > 0) || this.uuid != undefined) && this.installed;
    }
  },
  created() {
    // FIXME: 设计器先按中文显示，后续要修改
    this.$i18n.locale = 'zh_CN';
  },

  methods: {
    backToLatestForm() {
      this.formDatas = undefined;
      this.isVersionDataView = false;
      this.dyformKey = new Date().getTime();
      this.displayState = 'edit';
    },
    getVersionContainer() {
      return document.body;
    },
    showDataVersionDetail(item) {
      $axios
        .post('/json/data/services', {
          serviceName: 'dyFormFacade',
          methodName: 'getVersionFormData',
          args: JSON.stringify([this.formUuid, item.uuid])
        })
        .then(({ data }) => {
          console.log('数据版本', data.data);
          this.formDatas = data.data;
          this.dyformKey = new Date().getTime();
          this.$refs.versionDrawer.hide();
          this.isVersionDataView = true;
          this.displayState = 'label';
        })
        .catch(() => {});
    },
    fetchDataVersionList() {
      this.versionDataLoading = true;
      this.dataVerList.splice(0, this.dataVerList.length);
      $axios
        .post('/json/data/services', {
          serviceName: 'dyFormFacade',
          methodName: 'getAllVersionFormData',
          args: JSON.stringify([this.formUuid, this.dataUuid])
        })
        .then(({ data }) => {
          this.versionDataLoading = false;
          let dataMap = data.data;
          if (dataMap && dataMap[this.formUuid]) {
            this.dataVerList.push(...dataMap[this.formUuid]);
          }
        })
        .catch(() => {
          this.versionDataLoading = false;
        });
    },
    fetchLocaleOptions() {
      this.$axios
        .get(`/proxy/api/app/codeI18n/getAllLocales`, { params: {} })
        .then(({ data }) => {
          if (data.code == 0) {
            for (let d of data.data) {
              this.i18nOption.push({
                label: d.name,
                value: d.locale,
                description: d.remark || d.name,
                transCode: d.translateCode
              });
            }
          }
        })
        .catch(error => {});
    },
    setWidgetI18ns(widgets) {
      let i18ns = {};
      function findI18nObjects(json, widget) {
        function traverse(obj, belongToWidget) {
          if (typeof obj !== 'object' || obj === null) return;
          if (obj.id && obj.wtype && obj.configuration) {
            belongToWidget = obj;
          }
          if (Array.isArray(obj)) {
            for (let item of obj) {
              traverse(item, belongToWidget);
            }
          } else {
            for (let key in obj) {
              if (key === 'i18n' && obj[key] != undefined) {
                let i18n = obj[key];
                for (let langCode in i18n) {
                  if (i18ns[langCode] == undefined) {
                    i18ns[langCode] = { Widget: {} };
                  }
                  for (let c in i18n[langCode]) {
                    set(i18ns[langCode].Widget, c, i18n[langCode][c]);
                  }
                }
              } else {
                traverse(obj[key], belongToWidget);
              }
            }
          }
        }
        traverse(json, widget);
      }
      for (let wgt of widgets) {
        findI18nObjects(wgt, wgt);
      }
      console.log('查询 i18ns ', i18ns);
      for (let l in i18ns) {
        this.$i18n.mergeLocaleMessage(l, i18ns[l]);
      }
    },
    onChangeLocale() {
      this.reloadI18n(this.selectLocale);
    },
    reloadI18n(locale) {
      asyncLoadLocaleData(locale).then(results => {
        if (results[0]) {
          merge($app.$children[0].locale, results[0]);
        }
        if (results[1]) {
          this._lazyLoadAppCodeI18nMessagesApplyTo('pt-app-widget').then(() => {
            this.$i18n.locale = locale;
            this.$tempStorage.clear().then(() => {
              this.$refs.wDyform.dyform.$tempStorage.clear().then(() => {
                this.dyformKey = new Date().getTime();
              });
            });

            if (results[1]) {
              $app.$i18n.mergeLocaleMessage(locale, results[1]);
            }
          });
        }
      });
    },

    getFullUniServerUrl() {
      let url = '*';
      const uniServerUrlProtocol = localStorage.getItem('uniServerUrlProtocol');
      const uniServerUrl = localStorage.getItem('uniServerUrl');
      if (uniServerUrlProtocol && uniServerUrl) {
        url = uniServerUrlProtocol + uniServerUrl;
      }
      return url;
    },
    reload() {
      const urlObj = new URL(window.location.href);
      if (this.dataUuid) {
        urlObj.searchParams.set('dataUuid', this.dataUuid);
      }
      window.location.href = urlObj.toString() + window.location.hash;
    },
    onPreviewMessage(msg) {
      if (msg.startsWith('uni server form data save success')) {
        this.dataUuid = msg.split(':')[1].trim();
        this.reload();
      }
    },
    getMobilePageUrl() {
      let url = '/packages/_/pages/dyform/preview';
      if (this.uuid || this.dataUuid) {
        url += '?';
        let query = [];
        if (this.uuid) {
          query.push('formUuid=' + this.uuid);
        }
        if (this.dataUuid) {
          query.push('dataUuid=' + this.dataUuid);
        }
        query.push('displayState=' + this.displayState);
        url += query.join('&');
      }
      return url;
    },
    onChangeDisplayState() {
      if (this.terminalType == 'mobile') {
        var iframes = document.getElementsByName('iframe_mobile_preview');
        var iframe = iframes[0];
        iframe.contentWindow.postMessage('change form displayState : ' + this.displayState, this.getFullUniServerUrl());
        return;
      }
    },
    onChangeTerminalType() {
      this.$nextTick(() => {
        this.mobilePreviewUiVisible = this.terminalType == 'mobile';
        window.location.hash = this.terminalType;
      });
    },
    getPreviewJson() {
      return this.definitionVjsonCopy ? JSON.stringify(this.definitionVjsonCopy) : JSON.stringify(this.definitionVjson);
    },
    onClickDelete() {
      let _this = this;
      this.deleteLoading = true;
      $axios
        .post('/json/data/services', {
          serviceName: 'dyFormActionService',
          methodName: 'delete',
          args: JSON.stringify([this.formUuid, this.vDataUuid, false])
        })
        .then(({ data }) => {
          if (data.code == 0) {
            window.location.href = window.location.href.split('&dataUuid=')[0];
          } else {
            console.error(data);
            _this.$message.error('删除失败');
          }
        });
    },
    onClickSaveNewVersion() {
      let _this = this;
      _this.$loading();
      let _dataUuid = this.$refs.wDyform.dataUuid;
      // this.$refs.wDyform.dataUuid = undefined;
      this.$refs.wDyform.collectFormData(
        true,
        function (valid, msg, formData) {
          console.log(formData);
          if (valid) {
            _this.$loading(false);
            _this.$confirm({
              title: '确认保存为新版本吗',
              onOk() {
                _this.$loading();
                $axios
                  .post(`/proxy/api/dyform/data/saveDataModelFormDataNewVersion/${_dataUuid}`, formData.dyFormData)
                  .then(({ data }) => {
                    _this.$loading(false);
                    if (data.data) {
                      _this.dataUuid = data.data;
                      _this.reload();
                    } else {
                      // _this.$refs.wDyform.dataUuid = _dataUuid;
                    }
                  })
                  .catch(error => {
                    _this.$loading(false);
                    _this.$message.error('保存新版本异常');
                    console.error(error.response);
                    // _this.$refs.wDyform.dataUuid = _dataUuid;
                  });
              }
            });
          } else {
            _this.$refs.wDyform.$loading(false);
            _this.$refs.wDyform.dataUuid = _dataUuid;
          }
        },
        true
      );
    },

    onClickSave() {
      // this.saveLoading = false;
      let _this = this;
      if (this.terminalType == 'mobile') {
        var iframes = document.getElementsByName('iframe_mobile_preview');
        var iframe = iframes[0];
        iframe.contentWindow.postMessage('save form data', this.getFullUniServerUrl());
        return;
      }
      _this.$loading();
      this.$refs.wDyform.collectFormData(true, function (valid, msg, formData) {
        console.log(formData);
        if (valid) {
          _this.$loading(false);
          _this.$confirm({
            title: '提示',
            content: '确认保存吗？',
            onOk() {
              _this.$loading();
              $axios
                .post('/proxy/api/dyform/data/saveFormData', formData.dyFormData)
                .then(({ data }) => {
                  _this.$loading(false);
                  if (data.code === 0) {
                    _this.$message.success('保存成功');
                    _this.dataUuid = data.data;
                    _this.reload();
                  } else {
                    _this.saveLoading = false;
                    _this.$message.error('保存失败');
                    console.error(data);
                  }
                })
                .catch(error => {
                  _this.$loading(false);
                  _this.saveLoading = false;
                  _this.$message.error('保存失败');
                  console.error(error);
                });
            }
          });
        } else {
          _this.$loading(false);
        }
      });
    },
    onFormDataChanged: debounce(function () {}, 500)
  },
  beforeMount() {
    this.fetchLocaleOptions();
    if (window.location.hash == '#pc' || window.location.hash == '#mobile') {
      this.terminalType = window.location.hash.substring(1);
    }
    if (window.opener && window.opener.$app && window.opener.$app.designer) {
      this.definitionVjson = {
        tableName: window.opener.$app.definition.tableName,
        version: window.opener.$app.definition.version || '1.0',
        widgets: deepClone(window.opener.$app.designer.widgets),
        fields: deepClone(window.opener.$app.designer.FieldWidgets),
        jsModule: window.opener.$app.jsModule != undefined ? window.opener.$app.jsModule.key : undefined,
        refDyform: deepClone(window.opener.$app.definitionVjson.refDyform),
        lifecycleHook: deepClone(window.opener.$app.definitionVjson.lifecycleHook)
      };
      this.definitionVjsonCopy = deepClone(this.definitionVjson);
      this.setWidgetI18ns(this.definitionVjson.widgets);
      Promise.all([import('@dyform/app/web/framework/vue/install'), import('@installPageWidget'), import('@installWorkflowWidget')]).then(
        () => {
          this.installed = true;
          this.$nextTick(() => {
            // 预览页面：提供变量在控制台测试使用
            window.wDyform = this.$refs.wDyform;
          });
        }
      );
    } else if (this.uuid) {
      Promise.all([import('@dyform/app/web/framework/vue/install'), import('@installPageWidget'), import('@installWorkflowWidget')]).then(
        () => {
          this.installed = true;
          this.$nextTick(() => {
            // 预览页面：提供变量在控制台测试使用
            window.wDyform = this.$refs.wDyform;
          });
        }
      );
    } else {
      this.closeDesigner = true;
    }
  },
  mounted() {}
};
</script>
