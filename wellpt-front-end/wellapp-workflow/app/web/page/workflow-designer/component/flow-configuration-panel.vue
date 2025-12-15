<template>
  <div class="flow-configuration-panel">
    <a-icon
      type="double-right"
      :class="['right-collapse', collapse ? 'collapsed' : '']"
      @click.stop="onClickCollapse"
      :title="collapse ? '点击展开' : '点击收缩'"
    />
    <div class="edit-widget-property-container flow-cell-property-container">
      <a-tabs v-model="tabKey">
        <a-tab-pane key="flowProperty" tab="流程属性">
          <slot name="flowProperty"></slot>
        </a-tab-pane>
        <a-tab-pane key="cellProperty" :tab="tabName" v-if="currentCell">
          <PerfectScrollbar ref="scroll">
            <div class="component-configure-container">
              <component
                ref="configurationNode"
                :graphItem="graphItem"
                :is="currentComponentName"
                :key="currentComponentKey"
                :formData="currentCellData"
                @mounted="cellConfigurationMounted"
                @click.native.stop="onClickConfiguration"
              />
            </div>
          </PerfectScrollbar>
        </a-tab-pane>
      </a-tabs>
    </div>
  </div>
</template>

<script>
import { shapeMapConfig } from './designer/constant';
import NodeTaskConfiguration from './node-task-configuration/index.vue';
import EdgeDirectionConfiguration from './edge-direction-configuration/index.vue';
import NodeConditionConfiguration from './node-condition-configuration/index.vue';
import NodeSubflowConfiguration from './node-subflow-configuration/index.vue';
import NodeSwimlaneConfiguration from './node-swimlane-configuration/index.vue';
import NodeRobotConfiguration from './node-robot-configuration/index.vue';
import NodeCollabConfiguration from './node-collab-configuration/index.vue';

export default {
  name: 'FlowConfigurationPanel',
  inject: ['designer'],
  props: {
    graphItem: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    NodeTaskConfiguration,
    EdgeDirectionConfiguration,
    NodeConditionConfiguration,
    NodeSubflowConfiguration,
    NodeSwimlaneConfiguration,
    NodeRobotConfiguration,
    NodeCollabConfiguration
  },
  data() {
    return {
      collapse: false,
      tabKey: 'flowProperty',
      currentCell: undefined,
      currentCellData: undefined
    };
  },
  computed: {
    tabName() {
      let tabName = '';
      if (this.currentCell) {
        const shape = this.currentCell.shape;
        tabName = shapeMapConfig[shape] + '属性';
      }
      return tabName;
    },
    currentComponentName() {
      let componentName = '';
      if (this.currentCell) {
        const shape = this.currentCell.shape;
        componentName = `${shape}Configuration`;
      }
      return componentName;
    },
    currentComponentKey() {
      let componentKey = '';
      if (this.currentCell) {
        componentKey = this.currentCell.id;
      }
      return componentKey;
    },
    currentCellData2() {
      let cellData = {};
      if (this.currentCell) {
        cellData = this.currentCell.getData();
      }
      return cellData;
    }
  },
  watch: {
    'designer.selectedCellId': {
      deep: true,
      handler(cellId) {
        // console.log('selectedCellId', cellId);
        if (cellId) {
          this.tabKey = 'cellProperty';
          this.currentCell = this.graphItem.getCellById(cellId);
          this.currentCellData = this.currentCell.getData();
        } else {
          this.tabKey = 'flowProperty';
          this.currentCell = undefined;
          this.currentCellData = undefined;
        }
      }
    }
  },
  methods: {
    cellConfigurationMounted(vm) {
      this.$emit('cellConfigurationMounted', vm);
    },
    onClickConfiguration(event) {
      if (event) {
        if (event.target.getAttribute('role') === 'tab') {
          this.$nextTick(() => {
            this.$refs.scroll.update();
          });
        }
      }
    },
    // 折叠
    onClickCollapse() {
      this.collapse = !this.collapse;
    }
  }
};
</script>
