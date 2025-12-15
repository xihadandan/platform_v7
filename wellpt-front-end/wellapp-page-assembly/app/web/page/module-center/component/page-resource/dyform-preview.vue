<template>
  <div style="position: relative">
    <a-progress
      v-show="percent != -1"
      :percent="percent"
      strokeColor="var(--w-primary-color)"
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
        <a-button @click="onClickEditDyform" size="small" type="link">
          <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
          编辑表单
        </a-button>
        <a-button icon="plus" @click="onClickCreateMisView" size="small" :disabled="dataModel == undefined" type="link">创建视图</a-button>
        <Drawer title="版本历史" ref="drawer" :width="500" :container="getDrawerContainer" :wrapStyle="{ position: 'absolute' }">
          <a-button icon="history" size="small" type="link">版本历史</a-button>
          <template slot="content">
            <VersionTimeline
              :lines="versionTimelines"
              :current="selectedItem.uuid"
              :upgradeId="metadata.id"
              @clickItem="clickTimelineItem"
              :to-detail="toFormDesign"
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
          <a-button type="link" size="small" @click="e => toFormDesign({ key: metadata.uuid })">前往设计</a-button>
        </template>
      </a-empty>
      <PerfectScrollbar style="height: calc(100vh - 124px)" v-show="!emptyDesign">
        <div style="width: 100%; height: 100%; display: block; position: fixed; z-index: 1"></div>
        <iframe
          sandbox="allow-scripts allow-same-origin"
          id="pagePreviewIframe"
          :src="'/dyform-viewer/just-dyform?formUuid=' + selectedItem.uuid + '&v=' + pageKey + '&iframe=1#iframe'"
          :style="{ minHeight: 'calc(100vh - 134px)', border: 'none', width: '100%', pointerEvents: 'none' }"
        ></iframe>
      </PerfectScrollbar>
    </a-card>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import EWidgetDataManagerView from '../../../../widget/widget-data-manager-view/configuration/index.vue';
import moment from 'moment';
import VersionTimeline from './version-timeline.vue';
import { orderBy, random } from 'lodash';
import { Empty } from 'ant-design-vue';

export default {
  name: 'ModuleDyformPreview',
  inject: ['pageContext', 'resources', 'currentModule'],
  props: { metadata: Object },
  components: { Drawer, VersionTimeline },
  computed: {},
  data() {
    return {
      versionTimelines: [],
      title: this.metadata.name,
      version: this.metadata.version || '1.0',
      selectedItem: {
        name: this.metadata.name,
        uuid: this.metadata.uuid
      },
      pageKey: 'module_dyform_preview',
      dataModel: undefined,
      loading: true,
      percent: 0,
      emptyDesign: false,
      emptyImg: Empty.PRESENTED_IMAGE_SIMPLE
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.refresh();
  },
  mounted() {
    if (this.selectedItem && this.metadata.justCreated) {
      delete this.metadata.justCreated;
      this.handleDyformCrossTabEvent();
    }
    window.addEventListener('message', this.frameListener, false);
  },
  beforeDestroy() {
    window.removeEventListener('message', this.frameListener);
  },
  methods: {
    toFormDesign(e) {
      let _this = this;
      _this.handleDyformCrossTabEvent();
      window.open(`/dyform-designer/index?uuid=${e.key}`, '_blank');
    },
    clickTimelineItem(item) {
      if (this.selectedItem.uuid != item.key) {
        this.loading = true;
        this.title = item.data.name;
        this.version = item.data.version;
        this.selectedItem = item.data;
        this.pageKey = 'module_dyform_preview_' + new Date().getTime();
      }
    },
    frameListener(event) {
      let _this = this;
      if (event.origin !== location.origin) return;
      if (event.data == 'dyform mounted') {
        let iframeEle = document.querySelector('#pagePreviewIframe');
        this.percent = 100;
        setTimeout(() => {
          clearInterval(this.percentInterval);
          this.percent = -1;
        }, 300);
        this.emptyDesign = iframeEle.contentWindow.$app.$refs.wDyform.widgets.length == 0;
        iframeEle.style.height = `${iframeEle.contentWindow.document.body.scrollHeight}px`;
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

        observer.observe(iframeEle.contentWindow.document, {
          attributes: true,
          childList: true,
          subtree: true
        });
      }
    },
    refresh(force) {
      if (force) {
        this.pageKey = 'module_dyform_preview_' + new Date().getTime();
      }
      this.rePercentLoading();
      this.getDataModel();
      this.getFormVersions();
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
    getDrawerContainer() {
      return this.$el.querySelector('.ant-card-body');
    },
    getDataModel() {
      $axios.get(`/proxy/api/dm/getDetails`, { params: { id: this.metadata.tableName.replace('UF_', '') } }).then(({ data }) => {
        if (data.data.uuid) {
          this.dataModel = data.data;
        }
      });
    },
    onClickCreateMisView() {
      if (this.dataModel != undefined) {
        this.$loading('创建视图中');
        this.createDataModelMisView(this.dataModel, this.metadata.uuid, this.getCopyName(this.selectedItem.name + '_视图'))
          .then(page => {
            this.$loading(false);
            if (page && page.uuid) {
              this.$message.success('创建视图成功');
              this.pageContext.emitEvent('CreateModuleResourceSuccess', {
                uuid: page.uuid,
                name: page.name,
                wtype: 'vPage',
                id: page.id,
                version: '1.0',
                _RES_TYPE: 'page',
                _PAGE_TYPE: 'PC'
              });
            } else {
              this.$message.error('创建视图失败');
            }
          })
          .catch(() => {
            this.$message.error('创建视图失败');
            this.$loading(false);
          });
      }
    },
    createDataModelMisView(dataModel, formUuid, viewName) {
      return new Promise((resolve, reject) => {
        if (dataModel != undefined) {
          // 获取表单
          let widget = {
            category: 'businessModel',
            id: generateId(),
            name: '数据管理',
            title: '数据管理',
            wtype: 'WidgetDataManagerView'
          };
          widget.configuration = EWidgetDataManagerView.configuration();
          let { WidgetTable, WidgetDyformSetting } = widget.configuration;
          WidgetTable.configuration.dataModelUuid = dataModel.uuid;
          WidgetTable.configuration.dataModelId = dataModel.id;
          let columnJson = JSON.parse(dataModel.columnJson),
            cols = [];
          // 根据数据模型生成表格列定义
          for (let col of columnJson) {
            if (col.isSysDefault && col.column !== 'UUID') {
              continue;
            }
            cols.push({
              dataIndex: col.column,
              hidden: col.isSysDefault,
              id: generateId(),
              primaryKey: col.column == 'UUID',
              renderFunction: {},
              title: col.title
            });
          }
          WidgetTable.configuration.columns = cols;
          WidgetDyformSetting.configuration.formUuid = formUuid;
          WidgetDyformSetting.configuration.labelStateTitle = '查看';
          WidgetDyformSetting.configuration.title = '新建';
          WidgetDyformSetting.configuration.editStateTitle = '编辑';
          console.log('创建视图json', widget);
          let appWidgetDefinitionElements = EWidgetDataManagerView.methods.getWidgetDefinitionElements(widget),
            functionElements = EWidgetDataManagerView.methods.getFunctionElements(widget);

          let name = viewName,
            id = 'page_' + moment().format('yyyyMMDDHHmmss');
          if (typeof viewName == 'function') {
            name = viewName();
          }
          let pageDefinition = {
            name,
            newVersion: false,
            id,
            isPc: '1',
            appId: dataModel.module,
            appWidgetDefinitionElements,
            definitionJson: JSON.stringify({
              wtype: 'vPage',
              id,
              title: name,
              name: '可视化页面设计器',
              id,
              items: [widget],
              vars: {},
              appWidgetDefinitionElements,
              functionElements,
              style: {},
              pageParams: {}
            })
          };
          pageDefinition.functionElements = functionElements;
          pageDefinition.appWidgetDefinitionElements = appWidgetDefinitionElements;
          console.log('创建视图页面定义: ', pageDefinition);

          $axios
            .post('/web/design/savePageDefinition', pageDefinition)
            .then(({ data }) => {
              if (data.code == 0) {
                resolve({
                  uuid: data.data,
                  name: pageDefinition.name,
                  id: pageDefinition.id
                });
              }
            })
            .catch(() => {
              reject();
            });
        } else {
          resolve({});
        }
      });
    },

    getCopyName(originName) {
      let names = [],
        name = originName + ' - 副本';
      for (let p of this.resources) {
        if (p.children == undefined && p._RES_TYPE == 'page') {
          names.push(p.name);
        }
        if (p.children != undefined && p.children.length > 0) {
          for (let c of p.children) {
            if (c._RES_TYPE == 'page') {
              names.push(c.name);
            }
          }
        }
      }
      if (!names.includes(originName)) {
        return originName;
      }
      let num = 0;
      while (names.includes(name)) {
        name = originName + ` - 副本(${++num})`;
      }
      return name;
    },
    onClickEditDyform() {
      this.handleDyformCrossTabEvent();
      window.open(`/dyform-designer/index?uuid=${this.selectedItem.uuid}`, '_blank');
    },
    handleDyformCrossTabEvent() {
      let _this = this;
      _this.pageContext.offCrossTabEvent(`dyform:design:change:${this.selectedItem.uuid}`);
      _this.pageContext.handleCrossTabEvent(`dyform:design:change:${this.selectedItem.uuid}`, metadata => {
        _this.metadata.uuid = metadata.uuid;
        _this.metadata.version = metadata.version;
        _this.metadata.name = metadata.name;
        _this.refresh(true);
      });
    },
    getFormVersions() {
      $axios
        .post('/json/data/services', {
          serviceName: 'formDefinitionService',
          methodName: 'getFormDefinitionVersions',
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
                time: l.createTime,
                data: l,
                key: l.uuid
              });
            }
          }
        });
    }
  }
};
</script>
