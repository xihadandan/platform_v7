<template>
  <div style="position: relative">
    <a-progress
      v-show="percent != -1"
      strokeColor="var(--w-primary-color)"
      :percent="percent"
      style="position: absolute; top: 37px; z-index: 1"
      :strokeWidth="2"
      :showInfo="false"
    />
    <a-card :bordered="false" class="preview-card">
      <template slot="title">
        <span class="title">{{ title }}</span>
        <label style="color: var(--w-text-color-light); font-size: var(--w-font-size-base)">v{{ version }}</label>
      </template>
      <template slot="extra">
        <a-button @click="onClickViewPage" size="small" type="link" v-if="metadata.wtype == 'vPage'">
          <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
          预览页面
        </a-button>

        <a-button @click="onClickEditPage" size="small" type="link">
          <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
          编辑页面
        </a-button>
        <Drawer
          title="页面设置"
          ref="drawer"
          :width="500"
          :container="getDrawerContainer"
          :wrapStyle="{ position: 'absolute' }"
          :ok="savePageBasicInfo"
          okText="保存"
        >
          <a-button size="small" type="link">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            页面设置
          </a-button>
          <template slot="content">
            <a-tabs default-active-key="1" size="small">
              <a-tab-pane key="1" tab="基本信息">
                <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
                  <a-form-model-item label="页面名称" prop="name">
                    <a-input v-model="form.name">
                      <template slot="addonAfter">
                        <WI18nInput code="name" :target="form" v-model="form.name" />
                      </template>
                    </a-input>
                  </a-form-model-item>
                  <a-form-model-item label="ID">
                    <a-input disabled v-model="form.id" />
                  </a-form-model-item>
                  <a-form-model-item label="显示标题">
                    <a-input v-model="form.title">
                      <template slot="addonAfter">
                        <WI18nInput code="title" :target="form" v-model="form.title" />
                      </template>
                    </a-input>
                  </a-form-model-item>
                  <a-form-model-item label="编号">
                    <a-input v-model="form.code" />
                  </a-form-model-item>
                  <a-form-model-item label="描述">
                    <a-textarea v-model="form.remark" />
                  </a-form-model-item>
                </a-form-model>
              </a-tab-pane>
              <a-tab-pane key="2" tab="页面资源">
                <a-table
                  :pagination="false"
                  rowKey="resourceUuid"
                  :columns="columns"
                  :data-source="functionElements"
                  :expanded-row-keys.sync="expandedRowKeys"
                  class="pt-table"
                >
                  <template slot="protectedSlot" slot-scope="text, record">
                    <a-switch v-if="record.isProtected != undefined" v-model="record.isProtected" size="small" />
                  </template>
                </a-table>
              </a-tab-pane>
            </a-tabs>
          </template>
        </Drawer>

        <Drawer title="版本历史" ref="drawer" :width="500" :container="getDrawerContainer" :wrapStyle="{ position: 'absolute' }">
          <a-button icon="history" size="small" type="link">版本历史</a-button>
          <template slot="content">
            <VersionTimeline
              :lines="versionTimelines"
              :current="selectedItem.uuid"
              :upgradeId="metadata.id"
              :remove="removePageHis"
              :to-detail="toPageDesign"
              @clickItem="clickTimelineItem"
            />
          </template>
        </Drawer>
      </template>

      <!-- <div class="spin-center" v-if="loading">
        <a-spin />
      </div> -->

      <a-empty :image="emptyImg" v-if="emptyDesign" style="padding-top: 240px">
        <template #description>
          暂无设计内容
          <a-button type="link" size="small" @click="e => toPageDesign({ key: metadata.uuid })">前往设计</a-button>
        </template>
      </a-empty>
      <PerfectScrollbar style="height: calc(100vh - 117px); z-index: 1" v-else>
        <div
          v-if="metadata.wtype == 'vUniPage'"
          style="height: calc(100vh - 134px); display: flex; width: 100%; align-items: center; justify-content: center; background: #fff"
        >
          <MobilePreview
            displayStyle="direct"
            :previewJson="getPreviewJson"
            width="100%"
            :h5Url="h5Server"
            :pageUrl="getMobilePageUrl"
            ref="mobilePreview"
            @message="onPreviewMessage"
            :domain="domain"
            :phoneUiWidth="345"
            :editable="false"
          ></MobilePreview>
        </div>

        <iframe
          v-else
          :key="pageKey"
          sandbox="allow-scripts allow-same-origin"
          id="pagePreviewIframe"
          :src="'/webpage/' + url + '?iframe=1#iframe'"
          :style="{ minHeight: 'calc(100vh - 134px)', border: 'none', width: '100%', pointerEvents: 'none' }"
        ></iframe>
      </PerfectScrollbar>
    </a-card>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import VersionTimeline from './version-timeline.vue';
import MPhoneUi from '@pageAssembly/app/web/page/page-designer/component/m-phone-ui.vue';
import MobilePreview from '@pageAssembly/app/web/page/page-designer/component/mobile-preview.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

import { orderBy, debounce, random } from 'lodash';
import { Empty } from 'ant-design-vue';
export default {
  name: 'ModulePagePreview',
  inject: ['pageContext', 'currentModule'],
  props: {
    metadata: Object
  },
  provide() {
    return {};
  },
  components: { Drawer, VersionTimeline, MPhoneUi, MobilePreview, WI18nInput },
  computed: {},
  data() {
    return {
      emptyImg: Empty.PRESENTED_IMAGE_SIMPLE,
      title: this.metadata.name,
      version: this.metadata.version || '1.0',
      selectedItem: {
        uuid: this.metadata.uuid
      },
      url: this.metadata.id,
      loading: true,
      percent: 0,
      versionTimelines: [],
      columns: [
        {
          title: '名称',
          dataIndex: 'title'
        },
        {
          title: '资源权限',
          dataIndex: 'isProtected',
          width: 100,
          scopedSlots: { customRender: 'protectedSlot' }
        }
      ],
      pageKey: 'module_page_preview',
      form: { ...this.metadata },
      functionElements: [],
      expandedRowKeys: [],
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      installed: false,
      rules: {
        name: [{ required: true, message: '页面名称必填', trigger: 'blur' }]
        // pFormUuid: [{ required: !this.isPc, message: '请选择桌面表单', trigger: 'blur' }],
        // id: [
        //   { required: true, message: '表单ID', trigger: 'blur' },
        //   { pattern: /^\w+$/, message: '表单ID只允许包含字母、数字以及下划线', trigger: 'blur' }
        // ]
      },
      emptyDesign: false,
      h5Server: undefined,
      domain: undefined
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.h5Server = window.__INITIAL_STATE__.h5Server;
    this.domain = window.__INITIAL_STATE__.domain;
    this.refresh();
  },
  mounted() {
    if (this.selectedItem && this.metadata.justCreated) {
      delete this.metadata.justCreated;
      this.handlePageCrossTabEvent();
    }
    window.addEventListener('message', this.frameListener, false);
  },
  beforeDestroy() {
    window.removeEventListener('message', this.frameListener);
  },
  methods: {
    onPreviewMessage(msg) {
      if (msg.startsWith('uni mounted')) {
        this.stopPercentLoading();
      }
    },
    getMobilePageUrl() {
      return `/packages/_/pages/app/preview`;
    },
    getPreviewJson() {
      return new Promise((resolve, reject) => {
        $axios
          .get('/proxy/webapp/authenticatePage', {
            params: { uuid: this.metadata.uuid },
            headers: { debug: true }
          })
          .then(({ data }) => {
            this.loading = false;
            if (data.code == 0) {
              if (data.data) {
                resolve(data.data.definitionJson);
              }
            }
          });
      });
    },

    frameListener(event) {
      let _this = this;
      if (event.origin !== location.origin) return;
      if (event.data == 'vPage Rendered') {
        let iframeEle = document.querySelector('#pagePreviewIframe');
        // _this.loading = false;

        this.emptyDesign = iframeEle.contentWindow.$app.$refs.page.items.length == 0;
        this.stopPercentLoading();
        // 配置监听方法的属性值
        // 定义一个监听器
        var observer = new MutationObserver(mutations => {
          for (let item of mutations) {
            if (item.type === 'childList') {
              const scrollHeight = iframeEle.contentWindow.document.body.scrollHeight;
              iframeEle.style.height = `${scrollHeight}px`;
              break;
            }
          }
        });

        iframeEle.contentWindow.addEventListener('DOMContentLoaded', function (e) {
          try {
            if (iframeEle.contentWindow.document.readyState === 'interactive') {
              iframeEle.style.height = '0px';
              setTimeout(function () {
                iframeEle.style.height = `${iframeEle.contentWindow.document.body.scrollHeight}px`;
              }, 100);
              observer.observe(iframeEle.contentWindow.document, {
                attributes: true,
                childList: true,
                subtree: true
              });
            }
          } catch (err) {}
        });
      }
    },
    fetchPageInformation() {
      this.getPageResources(list => {
        this.functionElements.splice(0, this.functionElements.length);
        // 构建功能数据集
        let widgetFunMap = {},
          elements = [];
        for (let res of list) {
          if (res.appFunction && res.appFunction.type == 'appWidgetDefinition') {
            res.appFunction.definitionJson = JSON.parse(res.appFunction.definitionJson);
            elements.push({
              resourceUuid: res.uuid,
              functionUuid: res.appFunction.uuid,
              title: res.appFunction.definitionJson.title
              //  isProtected: res.isProtected // 组件暂不考虑权限控制
            });
            widgetFunMap[res.appFunction.id] = elements.length - 1;
            this.expandedRowKeys.push(res.uuid);
          }
        }
        for (let res of list) {
          if (res.appFunction && res.appFunction.type == 'AppWidgetFunctionElement') {
            res.appFunction.definitionJson = JSON.parse(res.appFunction.definitionJson);
            let wid = res.appFunction.definitionJson.widgetId,
              pushTarget = elements;
            if (wid && widgetFunMap[wid] != undefined) {
              if (elements[widgetFunMap[wid]].children == undefined) {
                elements[widgetFunMap[wid]].children = [];
              }
              pushTarget = elements[widgetFunMap[wid]].children;
            }
            pushTarget.push({
              resourceUuid: res.uuid,
              functionUuid: res.appFunction.uuid,
              isProtected: res.isProtected,
              title: res.appFunction.definitionJson.title
            });
          }
        }
        for (let i = 0; i < elements.length; i++) {
          if (elements[i].children == undefined || elements[i].children.length == 0) {
            elements.splice(i--, 1);
          }
        }

        this.functionElements.push(...elements);
      });
      this.getPageDefinition();
    },
    refresh(force) {
      if (force) {
        this.pageKey = 'module_page_preview' + new Date().getTime();
      }
      this.rePercentLoading();
      this.fetchPageInformation();
      this.getPageVersions();
    },
    stopPercentLoading() {
      this.percent = 100;
      setTimeout(() => {
        clearInterval(this.percentInterval);
        this.percent = -1;
      }, 300);
    },
    rePercentLoading() {
      // 伪进度条
      this.percent = random(2, 10);
      this.percentInterval = setInterval(() => {
        this.percent += 3;
        if (this.percent >= 100) {
          clearInterval(this.percentInterval);
        }
      }, 100);
    },
    removePageHis(e, callback) {
      $axios.delete(`/proxy/api/app/pagemanager/deletePage/${e.key}`).then(({ data }) => {
        callback(data.code == 0);
      });
    },

    clickTimelineItem(item) {
      if (this.selectedItem.uuid != item.key) {
        this.url = `${this.metadata.id}/${item.key}`;
        this.pageKey = 'module_page_preview' + new Date().getTime();
        this.loading = true;
        this.title = item.data.name;
        this.version = item.data.version;
        this.selectedItem = item.data;
        this.fetchPageInformation();
      }
    },
    toPageDesign(e) {
      window.open(
        `/${
          this.metadata.wtype == 'vUniPage' ? 'uni-page' : this.metadata.wtype == 'vBigscreen' ? 'bigscreen' : 'page'
        }-designer/index?uuid=${e.key}`,
        '_blank'
      );
    },
    getPageVersions() {
      $axios
        .post('/json/data/services', {
          serviceName: 'appPageDefinitionService',
          methodName: 'getPageDefinitionVersions',
          args: JSON.stringify([this.metadata.id])
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            let list = orderBy(
              data.data,
              [
                o => {
                  return parseFloat(o.version);
                }
              ],
              ['desc']
            );

            for (let l of list) {
              this.versionTimelines.push({
                title: l.name + ' v' + l.version,
                data: l,
                time: l.createTime,
                key: l.uuid
              });
            }
          }
        });
    },
    getPageDefinition() {
      $axios
        .post('/json/data/services', {
          serviceName: 'appPageDefinitionMgr',
          methodName: 'getBean',
          args: JSON.stringify([this.selectedItem.uuid])
        })
        .then(({ data }) => {
          if (data.code == 0) {
            let pageDefinition = data.data;
            this.form.name = pageDefinition.name;
            this.form.title = pageDefinition.title;
            this.form.code = pageDefinition.code;
            this.form.remark = pageDefinition.remark;
            this.form.id = pageDefinition.id;
            this.form.uuid = pageDefinition.uuid;
            if (pageDefinition.i18ns) {
              let i18n = {};
              for (let item of pageDefinition.i18ns) {
                if (i18n[item.locale] == undefined) {
                  i18n[item.locale] = {};
                }
                if (item.elementId == null) {
                  i18n[item.locale][item.code] = item.content;
                }
              }
              this.$set(this.form, 'i18n', i18n);
            }
          }
        });
    },
    getPageResources(callback) {
      $axios.get(`/proxy/api/webapp/page/definition/getAppPageResourceFunction/${this.selectedItem.uuid}`, {}).then(({ data }) => {
        if (data.code == 0) {
          if (typeof callback == 'function' && data.data) {
            callback.call(this, data.data);
          }
        }
      });
    },
    getDrawerContainer() {
      return this.$el.querySelector('.ant-card-body');
    },
    savePageBasicInfo(e) {
      let _this = this;
      let commit = () => {
        let i18ns = [];
        if (this.form.i18n) {
          for (let locale in this.form.i18n) {
            for (let key in this.form.i18n[locale]) {
              if (this.form.i18n[locale][key]) {
                i18ns.push({
                  locale: locale,
                  content: this.form.i18n[locale][key],
                  defId: this.form.id,
                  code: key,
                  applyTo: 'appPageDefinition'
                });
              }
            }
          }
        }
        $axios
          .post('/proxy/api/webapp/page/definition/updateBasicInfo', {
            uuid: this.form.uuid,
            id: this.form.id,
            name: this.form.name,
            title: this.form.title,
            code: this.form.code,
            remark: this.form.remark,
            i18ns
          })
          .then(({ data }) => {
            if (data.data) {
              this.$message.success('保存成功');
              if (this.form.uuid == this.metadata.uuid) {
                this.metadata.name = this.form.name;
                this.title = this.form.name;
              }
              if (this.versionTimelines) {
                for (let v of this.versionTimelines) {
                  if (v.key == this.form.uuid) {
                    v.title = this.form.name + ' v' + v.data.version;
                    v.data.name = this.form.name;
                    break;
                  }
                }
              }
              if (this.functionElements.length > 0) {
                // 更新受保护资源
                let formData = { resourceDtos: [], pageDefinitionUuid: this.form.uuid };
                for (let f of this.functionElements) {
                  if (f.isProtected != undefined) {
                    formData.resourceDtos.push({
                      uuid: f.resourceUuid,
                      isProtected: f.isProtected
                    });
                  }
                  if (f.children != undefined) {
                    for (let c of f.children) {
                      formData.resourceDtos.push({
                        uuid: c.resourceUuid,
                        isProtected: c.isProtected
                      });
                    }
                  }
                }
                $axios.post('/proxy/api/webapp/page/definition/updatePageProtected', formData).then(({ data }) => {});
              }
              e(true);
            }
          });
      };

      this.$refs.form.validate(passed => {
        if (passed) {
          commit.call(_this);
        }
      });
    },
    onClickViewPage() {
      if (this.metadata.wtype == 'vPage') {
        window.open(`/webpage/${this.metadata.id}/${this.selectedItem.uuid}`, '_blank');
      } else if (this.selectedItem.wtype == 'vUniPage') {
      }
    },
    onClickEditPage() {
      this.handlePageCrossTabEvent();
      this.toPageDesign({
        key: this.selectedItem.uuid
      });
    },
    handlePageCrossTabEvent() {
      let _this = this;
      this.pageContext.offCrossTabEvent(`page:design:change:${this.selectedItem.uuid}`);
      this.pageContext.handleCrossTabEvent(`page:design:change:${this.selectedItem.uuid}`, metadata => {
        this.selectedItem.uuid = metadata.uuid;
        this.version = metadata.version;
        this.title = metadata.name;
        if (this.metadata.uuid == this.selectedItem.uuid) {
          // 当前最新版本发生升级变更
          this.metadata.uuid = metadata.uuid;
          this.metadata.version = metadata.version;
          this.metadata.name = metadata.name;
        }
        _this.refresh(true);
      });
    }
  }
};
</script>
