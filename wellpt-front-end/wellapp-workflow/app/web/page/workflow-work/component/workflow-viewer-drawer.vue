<template>
  <a-drawer :visible="visible" :title="drawerTitle" width="66%" wrapClassName="permission-viewer-drawer" @close="closeDrawer">
    <div class="spin-center" v-if="spinning">
      <a-spin />
    </div>
    <template v-else>
      <iframe
        v-if="viewerUrl"
        :src="viewerUrl"
        id="workflowViewFrame"
        :style="{
          border: 'none',
          width: '100%',
          height: '100%'
        }"
      />
      <workflow-viewer
        v-else
        :htmlWrapperStyle="{ height: '100%' }"
        :viewerProp="{
          showHeader: false,
          workFlowData: flowDesignerData,
          flowInstUuid: workData.flowInstUuid
        }"
      />
    </template>
  </a-drawer>
</template>

<script>
import WorkflowViewer from '../../workflow-designer/viewer.vue';
export default {
  name: 'WorkflowViewerDrawer',
  inject: ['pageContext'],
  props: {
    workView: Object
  },
  components: {
    WorkflowViewer
  },
  data() {
    const workData = this.workView.getWorkData();
    return {
      workData,
      flowDesignerData: null,
      viewerUrl: '',
      visible: false
    };
  },
  computed: {
    spinning() {
      let spinning = true;
      if (this.flowDesignerData) {
        if (this.flowDesignerData.graphData || this.viewerUrl) {
          spinning = false;
        }
      }
      return spinning;
    },
    flowVersion() {
      let version;
      if (this.flowDesignerData) {
        version = this.flowDesignerData.version + '';
        version = version.indexOf('.') != -1 ? version : version + '.0';
      }

      return version;
    },
    drawerTitle() {
      let title = this.$t('WorkflowWork.workView.flowProcess', '流转过程');
      if (!this.spinning) {
        if (!this.viewerUrl) {
          let workName = this.flowDesignerData.name;
          const locale = this.$i18n.locale;
          if (
            locale &&
            locale !== 'zh_CN' &&
            this.flowDesignerData.i18n &&
            this.flowDesignerData.i18n[locale] &&
            this.flowDesignerData.i18n[locale]['workflowName']
          ) {
            workName = this.flowDesignerData.i18n[locale]['workflowName'];
          }
          title = title + ' ' + workName + '（v' + this.flowVersion + '）';
        }
      }
      return title;
    }
  },
  created() {
    this.pageContext.handleEvent('openViewerDrawer', this.openDrawer);
  },
  methods: {
    getFlowDesignerData() {
      this.workView.getFlowDesignerData().then(res => {
        this.flowDesignerData = res;
        if (!this.flowDesignerData.graphData) {
          this.workView.getFlowViewerUrl().then(viewerUrl => {
            this.viewerUrl = viewerUrl;
          });
        }
      });
    },
    closeDrawer() {
      this.visible = false;
    },
    openDrawer() {
      this.visible = true;
      if (this.spinning) {
        this.getFlowDesignerData();
      }
      if (!this.spinning && this.viewerUrl) {
        workflowViewFrame.contentWindow.location.reload();
      }
    },
    getContainer() {
      return document.querySelector(drawerSelector);
    }
  }
};
</script>

<style lang="less">
.permission-viewer-drawer {
  height: e('calc(100vh - 64px)');
  top: 64px;
  .ant-drawer-wrapper-body {
    display: flex;
    flex-direction: column;
  }
  .ant-drawer-body {
    flex: 1;
  }
}
</style>
