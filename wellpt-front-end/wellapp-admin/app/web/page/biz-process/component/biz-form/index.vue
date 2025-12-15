<template>
  <a-card class="form-nav-container" size="small" title="表单">
    <PerfectScrollbar style="height: calc(100vh - 52px)">
      <a-collapse :bordered="false" defaultActiveKey="entity" expandIconPosition="right">
        <a-collapse-panel key="entity" header="业务主体">
          <!-- <a-menu mode="vertical" @click="onEntityMenuClick">
            <a-menu-item key="1">业务主体表单</a-menu-item>
          </a-menu> -->
          <ul>
            <li @click="e => onEntityMenuClick({ key: 'entity' })" class="form-select-item" :class="{ selected: 'entity' == selectedKey }">
              <a-popover v-if="getEntityFormState()">
                <template slot="content">
                  {{ getEntityFormState().title }}
                </template>
                <a-icon type="info-circle" style="color: red" />
              </a-popover>
              业务主体表单
            </li>
          </ul>
        </a-collapse-panel>
        <a-collapse-panel key="process" header="业务流程">
          <!-- <a-menu mode="vertical" @click="onProcessMenuClick">
            <a-menu-item key="1">业务流程办理单</a-menu-item>
            <a-menu-item key="2">业务流程办件</a-menu-item>
          </a-menu> -->
          <ul>
            <li
              @click="e => onProcessMenuClick({ key: 'processForm' })"
              class="form-select-item"
              :class="{ selected: 'processForm' == selectedKey }"
            >
              <a-row type="flex">
                <a-col flex="auto">
                  <a-popover v-if="getHandleFormStateOfProcess()">
                    <template slot="content">
                      {{ getHandleFormStateOfProcess().title }}
                    </template>
                    <a-icon type="info-circle" style="color: red" />
                  </a-popover>
                  业务流程办理单
                </a-col>
                <a-col v-if="isRefTemplateOfProcessHandleForm()" flex="50px"><a-tag style="text-align: right">模板</a-tag></a-col>
              </a-row>
            </li>
            <li
              @click="e => onProcessMenuClick({ key: 'processInst' })"
              class="form-select-item"
              :class="{ selected: 'processInst' == selectedKey }"
            >
              业务流程办件
            </li>
          </ul>
        </a-collapse-panel>
        <a-collapse-panel key="node" header="阶段">
          <!-- <a-menu mode="inline" @click="onProcessNodeMenuClick">
            <a-sub-menu key="1" title="阶段办理单">
              <a-menu-item v-for="node in handleForm.nodes" :key="node.nodeId">{{ node.nodeName }}</a-menu-item>
            </a-sub-menu>
            <a-menu-item key="2">阶段办件</a-menu-item>
          </a-menu> -->
          <a-collapse :bordered="false" defaultActiveKey="nodeForm" expandIconPosition="right">
            <a-collapse-panel key="nodeForm" :header="'阶段办理单(' + handleForm.nodes.length + ')'">
              <ul>
                <li
                  v-for="node in handleForm.nodes"
                  :key="node.nodeId"
                  @click="e => onProcessNodeMenuClick({ key: node.nodeId })"
                  class="form-select-item"
                  :class="{ selected: node.nodeId == selectedKey }"
                >
                  <a-row type="flex">
                    <a-col flex="auto">
                      <a-popover v-if="getHandleFormStateOfNode(node)">
                        <template slot="content">
                          {{ getHandleFormStateOfNode(node).title }}
                        </template>
                        <a-icon type="info-circle" style="color: red" />
                      </a-popover>
                      {{ node.nodeName }}
                    </a-col>
                    <a-col v-if="isRefTemplateOfNodeHandleForm(node)" flex="50px"><a-tag style="text-align: right">模板</a-tag></a-col>
                  </a-row>
                </li>
              </ul>
            </a-collapse-panel>
          </a-collapse>
          <ul>
            <li
              @click="e => onProcessNodeMenuClick({ key: 'nodeInst' })"
              class="form-select-item"
              :class="{ selected: 'nodeInst' == selectedKey }"
            >
              阶段办件
            </li>
          </ul>
        </a-collapse-panel>
        <a-collapse-panel key="item" header="事项">
          <!-- <a-menu mode="inline" @click="onProcessItemMenuClick">
            <a-sub-menu key="1" title="事项办理单">
              <a-menu-item v-for="item in handleForm.items" :key="item.itemId">{{ item.itemName }}</a-menu-item>
            </a-sub-menu>
            <a-menu-item key="2">事项办理办件</a-menu-item>
          </a-menu> -->
          <a-collapse :bordered="false" defaultActiveKey="itemForm" expandIconPosition="right">
            <a-collapse-panel key="itemForm" :header="'事项办理单(' + handleForm.items.length + ')'">
              <ul>
                <li
                  v-for="item in handleForm.items"
                  :key="item.itemId"
                  @click="e => onProcessItemMenuClick({ key: item.itemId })"
                  class="form-select-item"
                  :class="{ selected: item.itemId == selectedKey }"
                >
                  <a-row type="flex">
                    <a-col flex="auto">
                      <a-popover v-if="getHandleFormStateOfItem(item)">
                        <template slot="content">
                          {{ getHandleFormStateOfItem(item).title }}
                        </template>
                        <a-icon type="info-circle" style="color: red" />
                      </a-popover>
                      {{ item.itemName }}
                    </a-col>
                    <a-col v-if="isRefTemplateOfItemHandleForm(item)" flex="50px"><a-tag style="text-align: right">模板</a-tag></a-col>
                  </a-row>
                </li>
              </ul>
            </a-collapse-panel>
          </a-collapse>
          <ul>
            <li
              @click="e => onProcessItemMenuClick({ key: 'itemInst' })"
              class="form-select-item"
              :class="{ selected: 'itemInst' == selectedKey }"
            >
              事项办理办件
            </li>
          </ul>
        </a-collapse-panel>
        <a-collapse-panel key="itemDefinition" header="事项源">
          <!-- <a-menu mode="vertical">
            <a-menu-item v-for="item in itemFormDefinitions" :key="item.uuid" @click="showItemDyform(item)">
              {{ item.name }}
            </a-menu-item>
          </a-menu> -->
          <ul>
            <li
              v-for="item in itemFormDefinitions"
              :key="item.uuid"
              @click="showItemDyform(item)"
              class="form-select-item"
              :class="{ selected: item.uuid == selectedKey }"
            >
              {{ item.name }}
            </li>
          </ul>
        </a-collapse-panel>
      </a-collapse>
    </PerfectScrollbar>
  </a-card>
</template>

<script>
import BizEntityDyform from '../biz-entity/biz-entity-dyform.vue';
import BizItemHandleDyForm from '../handle-form/biz-item-handle-dyform.vue';
import BizNodeHandleDyform from '../handle-form/biz-node-handle-dyform.vue';
import BizProcessHandleDyform from '../handle-form/biz-process-handle-dyform.vue';
import BizItemDyform from '../biz-item/biz-item-dyform.vue';
import ProcessInstanceTable from '../handle-form/process-instance-table.vue';
import ProcessNodeInstanceTable from '../handle-form/process-node-instance-table.vue';
import ProcessItemInstanceTable from '../handle-form/process-item-instance-table.vue';
import { isEmpty } from 'lodash';

export default {
  props: {
    processAssembleJson: Object
  },
  inject: ['assemble'],
  data() {
    return {
      businessId: this.assemble.processDefinition.businessId,
      entity: this.processAssembleJson.entity,
      handleForm: this.processAssembleJson.handleForm,
      itemDefinitions: [],
      itemFormDefinitions: [],
      selectedKey: null
    };
  },
  created() {
    this.loadItemDefinition();
  },
  methods: {
    loadItemDefinition() {
      let businessId = this.businessId;
      if (!businessId) {
        return Promise.resolve();
      }

      return $axios.get(`/proxy/api/biz/item/definition/listByBusinessId?businessId=${businessId}`).then(({ data: result }) => {
        if (result.data) {
          this.itemDefinitions = result.data;
          this.loadItemFormDefinition();
        }
      });
    },
    loadItemFormDefinition() {
      const _this = this;
      if (isEmpty(_this.itemDefinitions)) {
        return;
      }

      let formIdSet = new Set();
      _this.itemDefinitions.forEach(item => {
        if (item.formId) {
          formIdSet.add(item.formId);
        }
      });

      let formIds = [...formIdSet.values()];
      $axios.get(`/proxy/api/dyform/definition/listByIds?ids=${formIds}`).then(({ data: result }) => {
        if (result.data) {
          _this.itemFormDefinitions = result.data;
        }
      });
    },
    onEntityMenuClick({ key }) {
      this.selectedKey = key;
      this.assemble.closeDrawer();
      let content = { component: undefined, metadata: undefined };
      if (key == 'entity') {
        content = {
          component: BizEntityDyform,
          metadata: { entity: this.entity }
        };
      }
      this.assemble.showContent(content);
    },
    getEntityFormState() {
      let formState = this.assemble.getBizFormState();
      return formState.title ? formState : null;
    },
    showItemDyform(formDefinition) {
      this.selectedKey = formDefinition.uuid;
      this.assemble.showContent({
        component: BizItemDyform,
        metadata: { formDefinition }
      });
    },
    onProcessMenuClick({ key }) {
      const _this = this;
      _this.assemble.closeDrawer();
      _this.selectedKey = key;
      let process = _this.handleForm.process;
      let content = { component: undefined, metadata: undefined };
      if (key == 'processForm') {
        content = {
          component: BizProcessHandleDyform,
          metadata: {
            entity: process,
            defaultTitle: '业务流程办理单',
            formStateFunction() {
              return _this.assemble.getHandleFormStateOfProcess(_this.handleForm.process);
            }
          }
        };
      } else if (key == 'processInst') {
        content = {
          component: ProcessInstanceTable,
          metadata: { entity: process }
        };
      }
      _this.assemble.showContent(content);
    },
    getHandleFormStateOfProcess() {
      let formState = this.assemble.getHandleFormStateOfProcess(this.handleForm.process);
      return formState.title ? formState : null;
    },
    isRefTemplateOfProcessHandleForm() {
      let designer = this.assemble.getProcessDesigner();
      let definitionJson = designer.processDefinition;
      if (!definitionJson) {
        return false;
      }
      if (!definitionJson.formConfig) {
        return false;
      }
      return definitionJson.formConfig.configType == '1';
    },
    onProcessNodeMenuClick({ key }) {
      const _this = this;
      _this.assemble.closeDrawer();
      _this.selectedKey = key;
      let content = { component: undefined, metadata: undefined };
      if (key && key != 'nodeInst') {
        let nodeConfig = _this.handleForm.nodes.find(node => node.nodeId == key);
        content = {
          component: BizNodeHandleDyform,
          metadata: {
            entity: nodeConfig,
            defaultTitle: '阶段办理单',
            formStateFunction() {
              return _this.assemble.getHandleFormStateOfNode(nodeConfig);
            }
          }
        };
      } else if (key == 'nodeInst') {
        content = {
          component: ProcessNodeInstanceTable,
          metadata: {}
        };
      }
      _this.assemble.showContent(content);
    },
    getHandleFormStateOfNode(nodeConfig) {
      let formState = this.assemble.getHandleFormStateOfNode(nodeConfig);
      return formState.title ? formState : null;
    },
    isRefTemplateOfNodeHandleForm(nodeConfig) {
      let designer = this.assemble.getProcessDesigner();
      let stageNode = designer.getStageNodeByNodeId(nodeConfig.nodeId);
      if (!stageNode) {
        return false;
      }
      let nodeData = stageNode.getData();
      if (!nodeData.configuration.formConfig) {
        return false;
      }
      return nodeData.configuration.formConfig.configType == '1';
    },
    onProcessItemMenuClick({ key }) {
      const _this = this;
      _this.assemble.closeDrawer();
      _this.selectedKey = key;
      let content = { component: undefined, metadata: undefined };
      if (key && key != 'itemInst') {
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
      } else if (key == 'itemInst') {
        content = {
          component: ProcessItemInstanceTable,
          metadata: {}
        };
      }
      _this.assemble.showContent(content);
    },
    getHandleFormStateOfItem(itemConfig) {
      let formState = this.assemble.getHandleFormStateOfItem(itemConfig);
      return formState.title ? formState : null;
    },
    isRefTemplateOfItemHandleForm(itemConfig) {
      let designer = this.assemble.getProcessDesigner();
      let itemNode = designer.getItemNodeByItemId(itemConfig.itemId);
      if (!itemNode) {
        return false;
      }
      let itemData = itemNode.getData();
      if (!itemData.configuration.formConfig) {
        return false;
      }
      return itemData.configuration.formConfig.configType == '1';
    }
  }
};
</script>

<style lang="less" scope>
// .form-nav-container {
//   height: e('calc(100vh - 52px)');

//   background: #f8f8f8;

//   .ant-card-head {
//     display: none;
//   }

//   .ant-card-body {
//     padding: 0;
//   }

//   .ant-menu-submenu-arrow {
//     left: 8px;
//   }

//   .ant-collapse-item {
//     border-bottom: 0;
//   }

//   .ant-collapse-content-box {
//     padding: 0px 15px 0px 8px;

//     ul {
//       padding: 0px;
//     }

//     .form-select-item {
//       width: 96%;
//       margin-left: 8px;
//       list-style: none;
//       margin-top: 5px;
//       padding: 5px 0px 5px 7px;
//       background-color: #fff;
//       border-radius: 2px;
//       cursor: pointer;
//       display: inline-block;
//       outline: 1px dashed rgba(204, 204, 204, 1);
//       overflow: hidden;
//       text-overflow: ellipsis;
//       white-space: nowrap;

//       &.filter {
//         background: #e9e9e9;
//         cursor: not-allowed;

//         &:hover {
//           outline: 1px dashed rgba(204, 204, 204, 1);
//           color: unset;
//         }
//       }

//       > .iconfont {
//         margin-right: 5px;
//       }
//     }

//     .form-select-item:hover {
//       outline: 1px solid var(--w-primary-color);
//       color: var(--w-primary-color);
//     }

//     .form-select-item.selected {
//       outline: 1px solid #0078d7;
//       color: #0078d7;
//     }
//   }
// }
</style>
