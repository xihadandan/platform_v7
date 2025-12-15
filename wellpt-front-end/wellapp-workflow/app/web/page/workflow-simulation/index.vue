<template>
  <HtmlWrapper :title="vTitle" ref="htmlWrapper">
    <a-layout class="workflow-simulation">
      <a-layout-header class="workflow-simulation-header">
        <a-row>
          <a-col :span="9">
            <span class="title">
              {{ vTitle }}
            </span>
          </a-col>
          <a-col :span="7" class="header-buttons">
            <div>
              <a-button type="link" :class="{ selected: activeKey == 'flow' }" @click="onChangeNav('flow')">
                <Icon type="pticon iconfont icon-ptkj-suoyinshezhi"></Icon>
                流程
              </a-button>
              <a-button type="link" :class="{ selected: activeKey == 'form' }" @click="onChangeNav('form')">
                <Icon type="pticon iconfont icon-xinjiantubiao-01"></Icon>
                表单
              </a-button>
              <a-button type="link" :class="{ selected: activeKey == 'report' }" @click="onChangeNav('report')">
                <Icon type="pticon iconfont icon-shengmingzhouqisvg-01"></Icon>
                报告
              </a-button>
            </div>
          </a-col>
          <a-col span="8" :style="{ textAlign: 'right' }">
            <a-button v-if="['inited', 'success'].includes(simulation.state.code)" ghost @click="startFlowSimulation" type="primary">
              启动仿真
            </a-button>
            <a-button v-else-if="simulation.state.code == 'running'" ghost @click="pauseFlowSimulation" type="primary">暂停仿真</a-button>
            <a-button
              v-else-if="simulation.state.code == 'pause' || simulation.state.code == 'error'"
              ghost
              @click="resumeFlowSimulation"
              type="primary"
            >
              继续仿真
            </a-button>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout>
        <a-layout-content class="workflow-simulation-content">
          <keep-alive>
            <SimulationWorkflowViewer
              v-if="activeKey == 'flow'"
              ref="workflowViewer"
              :flowDefinition="flowDefinition"
            ></SimulationWorkflowViewer>
            <SimulationDyformViewer v-else-if="activeKey == 'form'"></SimulationDyformViewer>
            <SimulationReportViewer v-else-if="activeKey == 'report'" :flowDefinition="flowDefinition"></SimulationReportViewer>
          </keep-alive>
          <div class="configuration-drawer-container"></div>
        </a-layout-content>
        <a-layout-sider ref="configrationPanelRef" theme="light" class="workflow-simulation-sider" :width="380" :style="siderStyle">
          <a-tabs default-active-key="1" class="sider-tabs">
            <a-tab-pane key="1" tab="仿真设置">
              <SimulationParamsConfiguration></SimulationParamsConfiguration>
            </a-tab-pane>
            <a-tab-pane key="2" tab="仿真报告">
              <SimulationReportList :flowDefinition="flowDefinition"></SimulationReportList>
            </a-tab-pane>
          </a-tabs>
          <a-icon
            type="double-right"
            :class="['right-collapse', siderCollapse ? 'collapsed' : '']"
            @click.native.stop="onClickCollapse"
            :title="siderCollapse ? '点击展开' : '点击收缩'"
          />
        </a-layout-sider>
      </a-layout>
    </a-layout>
  </HtmlWrapper>
</template>

<script>
import '@installPageWidget';
import '@dyform/app/web/framework/vue/install';
import '@workflow/app/web/framework/vue/install';
import SimulationParamsConfiguration from './component/params-configuration';
import SimulationReportList from './component/report-list';
import SimulationWorkflowViewer from './component/simulation-workflow-viewer.vue';
import SimulationDyformViewer from './component/simulation-dyform-viewer.vue';
import SimulationReportViewer from './component/simulation-report-viewer.vue';
import WorkflowSimulation from './simulation/workflow-simulation.js';
import './css/index.less';
export default {
  components: {
    SimulationParamsConfiguration,
    SimulationReportList,
    SimulationWorkflowViewer,
    SimulationDyformViewer,
    SimulationReportViewer
  },
  inject: ['pageContext'],
  data() {
    return {
      siderCollapse: false,
      simulation: EASY_ENV_IS_BROWSER ? new WorkflowSimulation({ $widget: this }) : { params: { tasks: [] }, state: {} },
      activeKey: '',
      siderStyle: undefined
    };
  },
  provide() {
    return {
      simulation: this.simulation,
      flowDefinition: this.flowDefinition
    };
  },
  computed: {
    vTitle() {
      return '流程仿真' + (this.flowDefinition && this.flowDefinition.name ? ': ' + this.flowDefinition.name : '');
    }
  },
  mounted() {
    this.activeKey = 'flow';
    this.simulation.activeKey = this.activeKey;
    this.locale = this.$refs.htmlWrapper && this.$refs.htmlWrapper.locale;
  },
  methods: {
    onChangeNav(activeKey) {
      this.activeKey = activeKey;
      this.simulation.activeKey = this.activeKey;
    },
    onClickCollapse() {
      this.siderCollapse = !this.siderCollapse;

      if (!this.defaultSiderStyle) {
        this.defaultSiderStyle = {
          flex: this.$refs.configrationPanelRef.$el.style.flex,
          width: this.$refs.configrationPanelRef.$el.style.width
        };
      }

      // let style = this.$refs.configrationPanelRef.$el.style;
      if (this.siderCollapse) {
        this.siderStyle = {
          flex: '0',
          width: '0px',
          minWidth: '0px'
        };
        // style.flex = '0';
        // style.width = '0px';
        // style.minWidth = '0px';
      } else {
        this.siderStyle = {
          flex: this.defaultSiderStyle.flex,
          width: this.defaultSiderStyle.width,
          minWidth: this.defaultSiderStyle.width
        };
        // style.flex = this.defaultSiderStyle.flex;
        // style.width = this.defaultSiderStyle.width;
        // style.minWidth = this.defaultSiderStyle.width;
      }
    },
    startFlowSimulation() {
      this.activeKey = 'flow';
      this.$nextTick(() => {
        this.$refs.workflowViewer.initStartState(this.simulation.params.startTaskId || undefined);
        this.simulation.start();
      });
    },
    pauseFlowSimulation() {
      this.simulation.pause();
    },
    resumeFlowSimulation() {
      this.simulation.resume();
    }
  }
};
</script>

<style></style>
