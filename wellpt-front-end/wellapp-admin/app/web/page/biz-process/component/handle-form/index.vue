<template>
  <a-card size="small" title="办理单" :bordered="false">
    <PerfectScrollbar style="height: calc(100vh - 120px)">
      <a-collapse :bordered="false" defaultActiveKey="1">
        <a-collapse-panel key="1" header="业务流程">
          <a-menu mode="vertical" @click="onProcessMenuClick">
            <a-menu-item key="1">业务流程办理单</a-menu-item>
            <a-menu-item key="2">业务流程办件</a-menu-item>
          </a-menu>
        </a-collapse-panel>
        <a-collapse-panel key="2" header="阶段">
          <a-menu mode="inline" @click="onProcessNodeMenuClick">
            <a-sub-menu key="g1" title="阶段办理单">
              <a-menu-item v-for="node in handleForm.nodes" :key="node.nodeId">{{ node.nodeName }}</a-menu-item>
            </a-sub-menu>
            <a-menu-item key="2">阶段办件</a-menu-item>
          </a-menu>
        </a-collapse-panel>
        <a-collapse-panel key="3" header="事项">
          <a-menu mode="inline" @click="onProcessItemMenuClick">
            <a-sub-menu key="g1" title="事项办理单">
              <a-menu-item v-for="item in handleForm.items" :key="item.itemId">{{ item.itemName }}</a-menu-item>
            </a-sub-menu>
            <a-menu-item key="2">事项办理办件</a-menu-item>
          </a-menu>
        </a-collapse-panel>
      </a-collapse>
    </PerfectScrollbar>
  </a-card>
</template>

<script>
import BizEntityDyform from '../biz-entity/biz-entity-dyform.vue';
import BizItemHandleDyForm from './biz-item-handle-dyform.vue';
import ProcessInstanceTable from './process-instance-table.vue';
import ProcessNodeInstanceTable from './process-node-instance-table.vue';
import ProcessItemInstanceTable from './process-item-instance-table.vue';
export default {
  props: {
    handleForm: Object
  },
  inject: ['assemble'],
  methods: {
    onProcessMenuClick({ key }) {
      const _this = this;
      let process = _this.handleForm.process;
      let content = { component: undefined, metadata: undefined };
      if (key == '1') {
        content = {
          component: BizEntityDyform,
          metadata: {
            entity: process,
            defaultTitle: '业务流程办理单',
            formStateFunction() {
              return _this.assemble.getHandleFormStateOfProcess(_this.handleForm.process);
            }
          }
        };
      } else if (key == '2') {
        content = {
          component: ProcessInstanceTable,
          metadata: { entity: process }
        };
      }
      _this.assemble.showContent(content);
    },
    onProcessNodeMenuClick({ key }) {
      const _this = this;
      let content = { component: undefined, metadata: undefined };
      if (key && key != '2') {
        let nodeConfig = _this.handleForm.nodes.find(node => node.nodeId == key);
        content = {
          component: BizEntityDyform,
          metadata: {
            entity: nodeConfig,
            defaultTitle: '阶段办理单',
            formStateFunction() {
              return _this.assemble.getHandleFormStateOfNode(nodeConfig);
            }
          }
        };
      } else if (key == '2') {
        content = {
          component: ProcessNodeInstanceTable,
          metadata: {}
        };
      }
      _this.assemble.showContent(content);
    },
    onProcessItemMenuClick({ key }) {
      const _this = this;
      let content = { component: undefined, metadata: undefined };
      if (key && key != '2') {
        let itemConfig = _this.handleForm.items.find(item => item.itemId == key);
        content = {
          component: BizItemHandleDyForm,
          metadata: {
            entity: itemConfig,
            defaultTitle: '事项办理单',
            formStateFunction() {
              return _this.assemble.getHandleFormStateOfItem(itemConfig);
            }
          }
        };
      } else if (key == '2') {
        content = {
          component: ProcessItemInstanceTable,
          metadata: {}
        };
      }
      _this.assemble.showContent(content);
    }
  }
};
</script>

<style lang="less" scope>
.ant-menu-submenu-arrow {
  left: 8px;
}
</style>
