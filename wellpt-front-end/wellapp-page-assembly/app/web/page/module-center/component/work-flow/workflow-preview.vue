<template>
  <div>
    <a-card class="preview-card" :bordered="false">
      <template slot="title">
        <span class="title">{{ metadata.name }}</span>
        <label style="color: rgba(0, 0, 0, 0.45)">v{{ flowVersion }}</label>
      </template>
      <template slot="extra">
        <a-button @click="onClickDesignWorkflow" size="small" type="link">
          <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
          编辑流程
        </a-button>
        <a-button @click="onClickToDownloadAsImg" size="small" type="link" :loading="downloadLoading">
          <Icon type="pticon iconfont icon-ptkj-xiazai"></Icon>
          下载流程图
        </a-button>
        <!-- <Drawer title="版本历史" ref="drawer" :width="300" :container="getDrawerContainer" :wrapStyle="{ position: 'absolute' }">
        <a-button icon="history" size="small" type="link">版本历史</a-button>
        <template slot="content">
          <VersionTimeline :lines="versionTimelines" :current="metadata.uuid" :upgradeId="metadata.id" />
        </template>
      </Drawer> -->
      </template>
      <!-- <PerfectScrollbar style="height: calc(100vh - 120px)"> -->

      <template v-if="flowDesignerData">
        <template v-if="!flowDesignerData.xmlDefinition">
          <template v-if="flowDesignerData.graphData">
            <div
              :style="{
                width: '100%',
                height: 'calc(100vh - 120px)'
              }"
            >
              <workflow-viewer
                ref="workflowViewer"
                :htmlWrapperStyle="{ height: '100%' }"
                :viewerProp="{
                  showHeader: false,
                  workFlowData: flowDesignerData
                }"
              />
            </div>
          </template>
          <template v-else>
            <a-empty :image="emptyImg" style="padding-top: 240px">
              <template #description>
                暂无设计内容
                <a-button type="link" size="small" @click="onClickDesignWorkflow">前往设计</a-button>
              </template>
            </a-empty>
          </template>
        </template>
        <template v-else>
          <iframe
            :src="viewerUrl"
            id="workflowViewFrame"
            :style="{
              border: 'none',
              width: '100%',
              height: 'calc(100vh - 120px)'
            }"
          />
        </template>
      </template>

      <!-- </PerfectScrollbar> -->
    </a-card>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { Empty } from 'ant-design-vue';
import WorkflowViewer from '@workflow/app/web/page/workflow-designer/viewer.vue';
import { toPng } from 'html-to-image';

export default {
  name: 'WorkflowPreview',
  inject: ['pageContext', 'currentModule'],
  props: {
    metadata: Object
  },
  components: {
    WorkflowViewer
  },
  computed: {
    flowVersion() {
      let version = this.metadata.version + '';
      return version.indexOf('.') != -1 ? version : version + '.0';
    }
  },
  data() {
    const viewerUrl = `/html/newWorkFlowDesignerView.html?onlyGraphics=true&id=${this.metadata.uuid}`;
    return {
      emptyImg: Empty.PRESENTED_IMAGE_SIMPLE,
      downloadLoading: false,
      flowDesignerData: null,
      viewerUrl
    };
  },
  created() {
    this.getFlowDesignerData(this.metadata.uuid).then(res => {
      this.flowDesignerData = res;
      if (this.flowDesignerData.graphData) {
        this.viewerUrl = `/workflow-viewer/index?uuid=${this.metadata.uuid}&showHeader=false`;
      }
    });
  },
  methods: {
    getFlowDesignerData(flowDefUuid) {
      return new Promise((resolve, reject) => {
        require('@workflow/app/web/page/workflow-designer/component/api')
          .fetchFlowData(flowDefUuid)
          .then(res => {
            resolve(res);
          });
      });
    },
    onClickToDownloadAsImg() {
      let _this = this;
      this.downloadLoading = true;
      let selector = '#ID_WORKAROUND';
      if (!this.flowDesignerData.xmlDefinition || this.flowDesignerData.graphData) {
        selector = '#graph-container';
        this.$refs.workflowViewer.downloadFlowChart({
          picName: this.metadata.name,
          callback: () => {
            this.downloadLoading = false;
          }
        });
        return;
      }
      const ele = document.querySelector('#workflowViewFrame').contentDocument.body.querySelector(selector);
      toPng(ele).then(function (dataUrl) {
        var link = document.createElement('a');
        link.download = `${_this.metadata.name}.png`;
        link.href = dataUrl;
        link.click();
        _this.downloadLoading = false;
        setTimeout(() => {
          link.remove();
        }, 1000);
      });
    },
    onClickDesignWorkflow() {
      const url = `/web/app/pt-mgr/pt-wf-mgr/pt-wf-designer.html?id=${this.metadata.uuid}`;
      const newUrl = `/workflow-designer/index?uuid=${this.metadata.uuid}`;
      window.open(newUrl, '_blank');
      // if (!this.flowDesignerData.xmlDefinition || this.flowDesignerData.graphData) {
      //   window.open(newUrl, '_blank');
      // } else {
      //   window.open(url, '_blank');
      // }
    }
  }
};
</script>
