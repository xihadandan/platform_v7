<template>
  <a-row type="flex">
    <a-col flex="auto">
      <div class="main-container" :style="mainContainerStyle">
        <slot name="main-content" />
      </div>
    </a-col>
    <a-col :flex="sidebarStickWidth">
      <div v-if="ENV_IS_BROWSER" v-show="isShowTabBar" class="flex workflow-sidebar-container" :style="sidebarContainerStyle">
        <div :style="{ width: sidebarContentWidth }" class="tab-content">
          <div v-show="isAllowSignOpinion && isShowOpinionEditor">
            <WorkflowOpinionEditor :workView="workView" @openToSignOpinion="onOpenToSignOpinion"></WorkflowOpinionEditor>
          </div>
          <div v-show="isAllowViewProcess && isShowProcessViewer">
            <WorkflowProcessViewer :workView="workView" @open="onOpenProcessViewer"></WorkflowProcessViewer>
          </div>
        </div>
        <div :style="{ width: sidebarWidth }">
          <div class="tab-nav">
            <ul class="pull-right-nav">
              <li
                :style="siderItemLiStyle"
                :class="[activeKey == item.key ? 'active' : '']"
                v-for="(item, index) in tabOptions"
                :key="index"
                v-show="item.show !== undefined ? item.show : filterShow(item.showParam)"
                @click="onTabChange(item.key, item)"
              >
                {{ item.name }}
              </li>
            </ul>
          </div>
          <div class="tab-nav-icon" :style="{ width: sidebarWidth }">
            <div @click.stop="onStickSidbarContainerClick" v-if="!isStick">
              <Icon type="iconfont icon-ptkj_fenping" :title="$t('WorkflowWork.operation.divideScreen', '分屏')" />
            </div>
            <div @click.stop="onUnStickSidbarContainerClick" v-if="isStick">
              <Icon type="iconfont icon-ptkj_bufenping" :title="$t('WorkflowWork.operation.cancelDivideScreen', '取消分屏')" />
            </div>
            <template v-if="(isAllowSignOpinion && showOpinionEditorInSidebar) || (isAllowViewProcess && showProcessViewerInSidebar)">
              <div @click.stop="onExpandSidbarContainerClick" v-if="!isOpenState">
                <Icon type="iconfont icon-ptkj-zuoshouzhan" :title="$t('WorkflowWork.operation.expand', '展开')" />
              </div>
              <div @click.stop="onCloseSidbarContainerClick" v-if="isOpenState">
                <Icon type="iconfont icon-ptkj-youshouzhan" :title="$t('WorkflowWork.operation.close', '关闭')" />
              </div>
            </template>
          </div>
        </div>
      </div>
    </a-col>
    <workflow-viewer-drawer :workView="workView" ref="viewerPreview" />
  </a-row>
</template>
<script type="text/babel">
const sidebarTabWidth = '30px';
const sidebarContainerWidth = '450px';
import { each, find, some } from 'lodash';
export default {
  name: 'WorkflowSidebarLayout',
  inject: ['pageContext'],
  props: {
    workView: Object
  },
  data() {
    return {
      sidebarStickWidth: sidebarTabWidth,
      sidebarWidth: sidebarTabWidth,
      activeKey: '1',
      latestActiveKey: '1',
      mainContainerStyle: {
        width: '100%'
      },
      sidebarContainerStyle: {
        width: sidebarContainerWidth,
        height: '700px'
      },
      tabState: {
        1: 'open',
        2: 'close'
      },
      sidebarStick: false,
      showOpinionEditorInSidebar: true,
      showProcessViewerInSidebar: true,
      showFlowDesignerInSidebar: true
    };
  },
  computed: {
    siderItemLiStyle() {
      let style = { width: this.sidebarWidth };
      if (this.$i18n.locale !== 'zh_CN') {
        style['writing-mode'] = 'vertical-lr';
        style['text-orientation'] = 'upright';
      }
      return style;
    },
    tabOptions() {
      const _this = this;
      let tabs = [];
      if (_this.showOpinionEditorInSidebar) {
        tabs.push({
          key: '1',
          code: 'signOpinion',
          name: this.$t('WorkflowWork.siderbar.signOpinion', '签署意见'),
          showParam: 'isAllowSignOpinion'
        });
      }
      if (_this.showProcessViewerInSidebar) {
        tabs.push({
          key: '2',
          code: 'viewProcess',
          name: this.$t('WorkflowWork.siderbar.viewProcess', '办理过程'),
          showParam: 'isAllowViewProcess'
        });
      }
      if (_this.showFlowDesignerInSidebar) {
        tabs.push({
          key: '3',
          code: 'viewFlow',
          name: this.$t('WorkflowWork.siderbar.viewFlow', '查阅流程'),
          showParam: 'isAllowWorkFlow'
        });
      }
      return tabs;
    },
    isShowTabBar() {
      if (!this.tabOptions.length) {
        return false;
      }
      return this.workView.isAllowSignOpinion() || this.workView.isAllowViewProcess() || this.workView.isAllowViewFlowDesigner();
    },
    isAllowSignOpinion: function () {
      return this.workView.isAllowSignOpinion();
    },
    isAllowViewProcess: function () {
      return this.workView.isAllowViewProcess();
    },
    isDraft: function () {
      return this.workView.isDraft();
    },
    isAllowWorkFlow: function () {
      // let allow = this.isAllowViewProcess || this.isDraft;
      // return allow;
      return this.workView.isAllowViewFlowDesigner();
    },
    isOpenState: function () {
      const _self = this;
      let latestActiveKey = _self.latestActiveKey;
      let states = _self.tabState;
      return states[latestActiveKey] == 'open';
    },
    isStick: function () {
      return this.sidebarStick;
    },
    // 是否显示签署意见
    isShowOpinionEditor: function () {
      return this.showOpinionEditorInSidebar && this.activeKey == '1' && this.tabState['1'] == 'open';
    },
    // 是否显示办理过程
    isShowProcessViewer: function () {
      return this.showProcessViewerInSidebar && this.activeKey == '2' && this.tabState['2'] == 'open';
    },
    sidebarContentWidth() {
      // 是否有展开tab
      if (this.activeKey) {
      }
      let hasOpenState = some(this.tabState, item => {
        return item == 'open';
      });
      if (hasOpenState) {
        return `calc( ${sidebarContainerWidth} - ${sidebarTabWidth})`;
      }
      return '';
    }
  },
  created() {
    var _self = this;
    if (EASY_ENV_IS_BROWSER && _self.isShowTabBar) {
      _self.workView.getSettings().then(settings => {
        let opinionEditorSetting = settings.get('OPINION_EDITOR') || {};
        let processViewerSetting = settings.get('PROCESS_VIEWER') || {};
        _self.showOpinionEditorInSidebar = opinionEditorSetting.showMode == 'sidebar' || opinionEditorSetting.showMode == 'all';
        _self.showProcessViewerInSidebar = processViewerSetting.showMode == 'sidebar' || processViewerSetting.showMode == 'all';
        _self.showFlowDesignerInSidebar =
          processViewerSetting.designerShowMode == 'sidebar' || processViewerSetting.designerShowMode == 'all';

        _self.loadSidebarState();
      });
    }
  },
  mounted() {
    this.sidebarContainerStyle.height = window.innerHeight - 64 + 'px';
    // 流程设置参数控制签署意见是否显示在侧边栏
    this.workView.getSettings().then(settings => {
      // 没有签署意见时，激活办理过程页签
      if (this.showOpinionEditorInSidebar && this.isAllowSignOpinion) {
      } else if (this.showProcessViewerInSidebar && this.isAllowViewProcess) {
        this.activeKey = '2';
        this.latestActiveKey = '2';
        this.tabState[1] = 'close';
        this.tabState[2] = 'open';
      } else {
        this.activeKey = '';
        this.openOrCloseSidebarContainer('close');
      }
    });
  },
  methods: {
    loadSidebarState() {
      const _self = this;
      _self.workView.getSettings().then(settings => {
        let generalSetting = settings.get('GENERAL') || {};
        let defaultOpenWorkViewSidebar =
          generalSetting.defaultOpenWorkViewSidebar != null ? generalSetting.defaultOpenWorkViewSidebar : false;
        let defaultSplitWorkView = generalSetting.defaultSplitWorkView != null ? generalSetting.defaultSplitWorkView : false;
        // 页签展开、关闭状态
        $axios
          .get('/api/user/preferences/getValue', {
            params: { dataKey: 'workflow.sign.process.tab.state', dataValue: 'tab', moduleId: 'WORKFLOW' }
          })
          .then(result => {
            if (
              (_self.isAllowSignOpinion && _self.showOpinionEditorInSidebar) ||
              (_self.isAllowViewProcess && _self.showProcessViewerInSidebar)
            ) {
              let tabState = result.data.data;
              if (!tabState) {
                tabState = defaultOpenWorkViewSidebar ? 'open' : 'close';
              }
              _self.serverTabState = tabState;
              _self.tabState[_self.activeKey] = tabState;
              _self.openOrCloseSidebarContainer(tabState);
            }
          });
        // 页面分屏、取消分屏状态
        $axios
          .get('/api/user/preferences/getValue', {
            params: { dataKey: 'workflow.sign.process.bar.state', dataValue: 'bar', moduleId: 'WORKFLOW' }
          })
          .then(result => {
            let stickState = result.data.data;
            if (!stickState) {
              stickState = defaultSplitWorkView ? 'split' : 'float';
            }
            if (stickState == 'split') {
              _self.setStickInfo(true);
            } else {
              _self.setStickInfo(false);
            }
          });
      });
    },
    onTabChange: function (key, item) {
      const _self = this;
      if (key == '3') {
        _self.openFlowDesigner();
      } else {
        _self.latestActiveKey = key;
        each(_self.tabState, function (item, index) {
          _self.tabState[index] = 'close';
        });
        if (_self.activeKey == key) {
          _self.tabState[key] = 'close';
          _self.activeKey = '';
        } else {
          _self.tabState[key] = 'open';
          _self.activeKey = key;
        }
        _self.saveTabState(_self.tabState[key]);
        _self.openOrCloseSidebarContainer(_self.tabState[key]);
      }
    },
    onTabClick: function (key) {
      const _self = this;
      _self.changeTabState(key);
    },
    openFlowDesigner: function () {
      this.$refs.viewerPreview.openDrawer();
      // this.workView.viewFlowDesigner();
    },
    changeTabState: function (key) {
      var _self = this;
      var tabState = _self.tabState;
      var state = tabState[key];
      if (_self.latestActiveKey == key) {
        if (state == 'close') {
          tabState[key] = 'open';
        } else {
          tabState[key] = 'close';
        }
      } else {
        tabState[key] = 'open';
      }
      _self.saveTabState(tabState[key]);
      _self.openOrCloseSidebarContainer(tabState[key]);
    },
    saveTabState: function (state) {
      if (this.serverTabState == state) {
        return;
      }
      this.serverTabState = state;
      $axios.post('/api/user/preferences/save', {
        dataKey: 'workflow.sign.process.tab.state',
        dataValue: state,
        moduleId: 'WORKFLOW',
        remark: '流程样式记住上一次选择（分屏/悬浮）'
      });
    },
    openOrCloseSidebarContainer: function (state) {
      const _self = this;
      if (state === 'open') {
        this.sidebarContainerStyle.width = sidebarContainerWidth;
      } else {
        this.sidebarContainerStyle.width = sidebarTabWidth;
        each(_self.tabState, function (item, index) {
          _self.tabState[index] = 'close';
        });
        _self.activeKey = '';
      }
      this.updateLayoutStyle();
    },
    // 展开
    onExpandSidbarContainerClick: function () {
      const _self = this;
      if (!_self.latestActiveKey) {
        let item = find(this.tabOptions, { show: true });
        if (item) {
          _self.latestActiveKey = item.key;
        }
      }
      _self.onTabChange(_self.latestActiveKey);
    },
    // 关闭
    onCloseSidbarContainerClick: function () {
      const _self = this;
      _self.onTabChange(_self.activeKey);
    },
    // 分屏
    onStickSidbarContainerClick: function () {
      this.setStickInfo(true);
      this.$message.success(this.$t('WorkflowWork.operation.splitScreen', '已分屏'));
      this.saveStickState('split');
    },
    // 取消分屏
    onUnStickSidbarContainerClick: function () {
      this.setStickInfo(false);
      this.$message.success(this.$t('WorkflowWork.operation.cancelDivideScreen', '取消分屏'));
      this.saveStickState('float');
    },
    setStickInfo: function (isStick) {
      this.sidebarStick = isStick;
      this.updateLayoutStyle();
    },
    updateLayoutStyle() {
      let isStick = this.sidebarStick;
      let isOpen = this.isOpenState;
      if (this.isShowTabBar) {
        if (isStick && isOpen) {
          this.mainContainerStyle.width = `calc(100vw - ${sidebarContainerWidth} - var(--w-padding-md) - var(--w-padding-xs))`;
          this.sidebarStickWidth = sidebarContainerWidth;
        } else {
          this.mainContainerStyle.width = `calc(100vw - ${sidebarTabWidth} - var(--w-padding-md) - var(--w-padding-xs))`;
          this.sidebarStickWidth = sidebarTabWidth;
        }
      } else {
        this.mainContainerStyle.width = `calc(100vw - var(--w-padding-md) * 2)`;
      }
      this.$nextTick(() => {
        this.pageContext.emitEvent('updateLayoutStyle', {});
      });
    },
    saveStickState: function (state) {
      $axios.post('/api/user/preferences/save', {
        dataKey: 'workflow.sign.process.bar.state',
        dataValue: state,
        moduleId: 'WORKFLOW',
        remark: '流程样式记住上一次选择（分屏/悬浮）'
      });
    },
    onOpenToSignOpinion: function () {
      var _self = this;
      var tabState = _self.tabState;
      if (_self.latestActiveKey == '1' && tabState['1'] == 'open') {
        return;
      }
      this.onTabClick('1');
      this.onTabChange('1');
    },
    onOpenProcessViewer: function () {
      var _self = this;
      var tabState = _self.tabState;
      if (_self.latestActiveKey == '2' && tabState['2'] == 'open') {
        return;
      }
      this.onTabClick('2');
      this.onTabChange('2');
    },
    filterShow: function (params) {
      return this[params];
    }
  }
};
</script>
<style scoped>
.sidebar-tabs {
  position: absolute;
  top: 100px;
  right: 0px;
  height: e('calc(100% - 345px)');
}
.sidebar-tabs.ant-tabs-card.ant-tabs-right >>> .ant-tabs-tab {
  writing-mode: vertical-lr;
  height: auto;
  padding: 16px 0px;
  line-height: 30px;
}
.sidebar-tabs.ant-tabs-card.ant-tabs-right >>> .ant-tabs-tab-active {
  padding-left: 0 !important;
}
.sidebar-btns {
  position: absolute;
  top: 650px;
  right: 0px;
}
</style>
<style lang="less" scoped>
.workflow-sidebar-container {
  position: fixed;
  top: 64px;
  right: 0px;
  z-index: 6;
  background: #fff;
  box-shadow: 0 2px 10px 0 rgba(0, 0, 0, 0.15);
  border-top-left-radius: var(--w-border-radius-base);

  .tab-content {
    height: 100%;
    // border-right: 1px solid #e8e8e8;
    border-top-left-radius: var(--w-border-radius-base);
  }

  .tab-nav {
    position: absolute;
    top: 160px;
    .pull-right-nav {
      li {
        cursor: pointer;
        writing-mode: lr-tb;
        padding: 13px 8px;
        font-size: 14px;
        border-radius: 0;
        color: var(--w-primary-color);
        background: var(--w-primary-color-1);
        margin-bottom: 2px;
        &.active {
          position: relative;
          color: #fff;
          background-color: var(--w-primary-color);

          &::before {
            position: absolute;
            top: 50%;
            left: -5px;
            content: '';
            display: block;
            border-top: 7px solid transparent;
            border-bottom: 7px solid transparent;
            border-right: 5px solid var(--w-primary-color);
            margin-top: -5px;
          }
        }
        &:hover {
          color: #fff;
          background-color: var(--w-primary-color);
        }
      }
    }
  }
  .tab-nav-icon {
    position: absolute;
    bottom: 0px;
    i {
      width: 100%;
      display: block;
      text-align: center;
      cursor: pointer;
      margin-bottom: 16px;

      &:hover {
        color: var(--w-primary-color);
      }
    }
  }
  ul {
    list-style-type: none;
    padding-inline-start: 0;
  }
}
</style>
