<template>
  <div class="process-configuration-panel">
    <a-icon
      type="double-right"
      :class="['right-collapse', collapse ? 'collapsed' : '']"
      @click.native.stop="onClickCollapse"
      :title="collapse ? '点击展开' : '点击收缩'"
    />
    <div class="edit-widget-property-container">
      <a-tabs :activeKey="activeKey" @change="onTabChange" @tabClick="onTabClick">
        <slot name="basicInfoTab">
          <a-tab-pane key="basicInfo" tab="基本信息"></a-tab-pane>
        </slot>
        <a-tab-pane key="nodeInfo" :tab="tabName" v-show="designer.selectedNodeId && selectedNode">
          <PerfectScrollbar ref="scroll">
            <div class="component-configure-container">
              <keep-alive>
                <component
                  ref="configurationNode"
                  :is="nodeConfigType"
                  :key="nodeConfigKey"
                  :designer="designer"
                  :node="selectedNode"
                  @click.native.stop="onClickConfiguration"
                ></component>
              </keep-alive>
            </div>
          </PerfectScrollbar>
        </a-tab-pane>
      </a-tabs>
    </div>
  </div>
</template>

<script>
import NodeConfiguration from './node-configuration';
import ItemConfiguration from './item-configuration';
import StartConfiguration from './item-flow-configuration/start-configuration.vue';
import EdgeConfiguration from './item-flow-configuration/edge-configuration.vue';
import GatewayConfiguration from './item-flow-configuration/gateway-configuration.vue';
import EndConfiguration from './item-flow-configuration/end-configuration.vue';
import { upperFirst } from 'lodash';
export default {
  props: {
    designer: Object,
    name: String
  },
  inject: ['pageContext'],
  name: 'ProcessConfigurationPanel',
  components: { NodeConfiguration, ItemConfiguration, StartConfiguration, EdgeConfiguration, GatewayConfiguration, EndConfiguration },
  data() {
    return {
      collapse: false,
      activeKey: 'basicInfo',
      nodeConfigKey: null,
      selectedNode: null
    };
  },
  computed: {
    // selectedNode() {
    //   if (this.designer.selectedNodeId) {
    //     return this.designer.getSelectedNode();
    //   }
    //   return null;
    // },
    tabName() {
      let selectedNode = this.selectedNode;
      if (selectedNode == null) {
        return '';
      }
      if (selectedNode.isEdge && selectedNode.isEdge()) {
        return '连接线';
      }

      let nodeData = selectedNode.getData();
      let name = nodeData.configuration && (nodeData.configuration.itemName || nodeData.configuration.name);
      let isTempalte = this.designer.isTemplateNode(selectedNode.id, nodeData.type);
      if (isTempalte) {
        return name + '(模板)';
      }
      let isRef = this.designer.isRefNode(selectedNode.id, nodeData.type);
      if (isRef) {
        return name + '(引用)';
      }
      return name;
    },
    nodeConfigType() {
      let selectedNode = this.selectedNode;
      if (selectedNode == null) {
        return null;
      }
      if (selectedNode.isEdge && selectedNode.isEdge()) {
        return 'EdgeConfiguration';
      }

      let nodeData = selectedNode.getData();
      let configType = null;
      if (nodeData.type == 'node') {
        configType = 'NodeConfiguration';
      } else if (nodeData.type == 'item') {
        configType = 'ItemConfiguration';
      } else if (nodeData.type) {
        configType = `${upperFirst(nodeData.type)}Configuration`;
      }
      return configType;
    }
    // nodeConfigKey() {
    //   let selectedNode = this.selectedNode;
    //   if (selectedNode == null) {
    //     return null;
    //   }
    //   return selectedNode.id;
    // }
  },
  mounted() {
    this.defaultSiderStyle = {
      flex: this.$el.parentElement.parentElement.style.flex,
      width: this.$el.parentElement.parentElement.style.width
    };
    setTimeout(() => {
      if (this.designer.isEmpty) {
        this.onClickCollapse();
      }
    }, 0);
    this.pageContext.handleEvent(`processConfigurationPanel:${this.name}`, data => {
      this.collapse = !data.collapse;
      this.onClickCollapse();
    });
    this.pageContext.handleEvent('processConfigurationPanel:updateSelectedKey', () => {
      this.selectedNode = this.designer.getSelectedNode();
      this.nodeConfigKey = this.nodeConfigKey + Date.now();
    });
  },
  watch: {
    'designer.selectedNodeId': {
      handler(v) {
        if (v) {
          let selectedNode = this.designer.getSelectedNode();
          this.selectedNode = selectedNode;
          if (selectedNode) {
            this.nodeConfigKey = selectedNode.id;
          } else {
            this.nodeConfigKey = null;
          }
          if (selectedNode && selectedNode.isEdge && selectedNode.isEdge()) {
            this.activeKey = 'nodeInfo';
          } else if (selectedNode && ['node', 'item', 'start', 'gateway', 'end'].indexOf(selectedNode.getData().type) >= 0) {
            this.activeKey = 'nodeInfo';
          } else {
            this.activeKey = 'basicInfo';
          }
        } else {
          this.activeKey = 'basicInfo';
        }
      }
    }
  },
  methods: {
    // 点击左侧tab，默认会打开配置，如果之前它是折叠的，再次点击tab，折叠图标位置会出错
    collapseChange() {
      // 流程设置为空时，默认关闭配置
      if (this.designer.isEmpty === true) {
        //|| (this.designer.hasOwnProperty('itemFlows') && !this.designer.selectedNodeId)
        this.collapse = false;
        setTimeout(() => {
          this.onClickCollapse();
        }, 0);
      } else if (this.collapse) {
        this.collapse = false;
      }
    },
    onClickCollapse() {
      this.collapse = !this.collapse;
      let style = this.$el.parentElement.parentElement.style;
      if (this.collapse) {
        style.flex = '0';
        style.width = '0px';
        style.minWidth = '0px';
      } else {
        style.flex = this.defaultSiderStyle.flex;
        style.width = this.defaultSiderStyle.width;
        style.minWidth = this.defaultSiderStyle.width;
      }
    },
    onTabClick(key) {
      this.activeKey = key;
    },
    onTabChange() {},
    onClickConfiguration(e) {
      if (this.tabOffsetBottom && e.y <= this.tabOffsetBottom) {
        // 点击的是tab页签选项，更新配置区滚动条
        this.$refs.scroll.update();
      }
    }
  }
};
</script>
