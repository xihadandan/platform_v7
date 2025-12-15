<template>
  <a-card class="form-nav-container pt-card" :bordered="false" size="small" title="表单设置" :bodyStyle="{ padding: '12px 0' }">
    <PerfectScrollbar style="height: calc(100vh - 120px)">
      <a-menu class="form-menu" mode="inline" @click="onMenuClick" :inlineIndent="20">
        <!-- 业务主体表单 -->
        <a-menu-item key="entity" value="onEntityMenuClick" :class="[getEntityFormState() ? 'hasTip' : '']">
          <MenuDiv :menuInfo="entityMenu" :stateFun="getEntityFormState"></MenuDiv>
        </a-menu-item>
        <!-- 业务流程办理单 -->
        <a-menu-item key="processForm" value="onProcessMenuClick" :class="[getHandleFormStateOfProcess() ? 'hasTip' : '']">
          <MenuDiv :menuInfo="processFormMenu" :tagFun="isRefTemplateOfProcessHandleForm" :stateFun="getHandleFormStateOfProcess"></MenuDiv>
        </a-menu-item>
        <!-- 业务流程办件 -->
        <a-menu-item key="processInst" value="onProcessMenuClick">
          <MenuDiv :menuInfo="processInstMenu"></MenuDiv>
        </a-menu-item>
        <!-- 阶段办理单 -->
        <a-sub-menu key="nodeForm">
          <span slot="title"><MenuDiv :menuInfo="nodeFormMenu" :count="handleForm.nodes.length"></MenuDiv></span>
          <a-menu-item
            v-for="node in handleForm.nodes"
            :key="node.nodeId"
            value="onProcessNodeMenuClick"
            :class="[getHandleFormStateOfNode(node) ? 'hasTip' : '']"
          >
            <MenuDiv
              :menuInfo="node"
              :tagFun="isRefTemplateOfNodeHandleForm"
              :stateFun="getHandleFormStateOfNode"
              titleParam="nodeName"
              isSub
            ></MenuDiv>
          </a-menu-item>
        </a-sub-menu>
        <!-- 阶段办件 -->
        <a-menu-item key="nodeInst" value="onProcessNodeMenuClick">
          <MenuDiv :menuInfo="{ key: 'nodeInst', title: '阶段办件' }"></MenuDiv>
        </a-menu-item>
        <!-- 事项办理单 -->
        <a-sub-menu key="itemForm">
          <span slot="title">
            <MenuDiv :menuInfo="{ key: 'itemForm', title: '事项办理单' }" :count="handleForm.items.length"></MenuDiv>
          </span>
          <a-menu-item
            v-for="item in handleForm.items"
            :key="item.itemId"
            value="onProcessItemMenuClick"
            :class="[getHandleFormStateOfItem(item) ? 'hasTip' : '']"
          >
            <MenuDiv
              :menuInfo="item"
              :tagFun="isRefTemplateOfItemHandleForm"
              :stateFun="getHandleFormStateOfItem"
              titleParam="itemName"
              isSub
            ></MenuDiv>
          </a-menu-item>
        </a-sub-menu>
        <!-- 阶段办件 -->
        <a-menu-item key="itemInst" value="onProcessItemMenuClick">
          <MenuDiv :menuInfo="{ key: 'itemInst', title: '事项办理办件' }"></MenuDiv>
        </a-menu-item>
        <!-- 事项办理单 -->
        <a-sub-menu key="itemDefinition">
          <span slot="title">
            <MenuDiv :menuInfo="{ key: 'itemDefinition', title: '事项源' }" :count="itemFormDefinitions.length"></MenuDiv>
          </span>
          <a-menu-item v-for="item in itemFormDefinitions" :key="item.uuid" value="showItemDyform">
            <MenuDiv :menuInfo="item" titleParam="name" isSub></MenuDiv>
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </PerfectScrollbar>
  </a-card>
</template>

<script>
const MenuDiv = {
  template: `<div :class="['flex form-menu-item']">
      <div class="f_s_0 icon-div">
        <a-popover v-if="stateFun&&stateFun(menuInfo)">
          <template slot="content">
            {{ stateFun(menuInfo).title }}
          </template>
          <Icon type="pticon iconfont icon-ptkj-weixianjinggaotishiyuqi"/>
        </a-popover>
        <Icon v-else-if="isSub" type="pticon iconfont icon-ptkj-mokuaiqukuai"/>
        <Icon v-else type="pticon iconfont icon-xinjiantubiao-01"></Icon>
      </div>
      <div class="f_g_1 title w-ellipsis" :title="menuInfo[titleParam]">
        {{menuInfo[titleParam]}}        
      </div>
      <div class="f_s_0" v-if="tagFun&&tagFun(menuInfo)"><a-tag style="text-align: right;--w-tag-background:#fafafa;--w-tag-border-radius:4px;--w-tag-border-color:#e5e5e5">模板</a-tag></div>
      <div v-if="count !== undefined" class="f_s_0 count-div">{{ count }}</div>
      </div>
    `,
  inject: ['assemble'],
  name: 'MenuDiv',
  props: {
    menuInfo: Object,
    tagFun: Function,
    stateFun: Function,
    count: Number,
    titleParam: { type: String, default: 'title' },
    isSub: Boolean
  },
  methods: {}
};

import BizEntityDyform from '../biz-entity/biz-entity-dyform.vue';
import BizItemHandleDyForm from '../handle-form/biz-item-handle-dyform.vue';
import BizNodeHandleDyform from '../handle-form/biz-node-handle-dyform.vue';
import BizProcessHandleDyform from '../handle-form/biz-process-handle-dyform.vue';
import BizItemDyform from '../biz-item/biz-item-dyform.vue';
import ProcessInstanceTable from '../handle-form/process-instance-table.vue';
import ProcessNodeInstanceTable from '../handle-form/process-node-instance-table.vue';
import ProcessItemInstanceTable from '../handle-form/process-item-instance-table.vue';
import { isEmpty, find } from 'lodash';

export default {
  props: {
    processAssembleJson: Object
  },
  inject: ['assemble'],
  components: { MenuDiv },
  data() {
    return {
      businessId: this.assemble.processDefinition.businessId,
      itemDefinitions: [],
      itemFormDefinitions: [],
      selectedKey: null,
      entityMenu: {
        key: 'entity',
        title: '业务主体表单'
      },
      processFormMenu: {
        key: 'processForm',
        title: '业务流程办理单'
      },
      processInstMenu: {
        key: 'processInst',
        title: '业务流程办件'
      },
      nodeFormMenu: {
        key: 'nodeForm',
        title: '阶段办理单'
      }
    };
  },
  computed: {
    entity() {
      return this.processAssembleJson.entity;
    },
    handleForm() {
      return this.processAssembleJson.handleForm;
    }
  },
  created() {
    this.loadItemDefinition();
  },
  methods: {
    onMenuClick({ item, key }) {
      this.assemble.closeDrawer();
      this[item.value]({ key });
    },
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
    showItemDyform({ key }) {
      let formDefinition = find(this.itemFormDefinitions, { uuid: key });
      this.selectedKey = formDefinition.uuid;
      this.assemble.showContent({
        component: BizItemDyform,
        metadata: { formDefinition }
      });
    },
    onProcessMenuClick({ key }) {
      const _this = this;
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
.form-nav-container {
  height: e('calc(100vh - 52px)');

  border-bottom: none;

  .ant-card-head-title {
    font-size: var(--w-font-size-base);
    font-weight: bold;
  }

  .form-menu {
    --w-biz-process-form-setting-icon-color: var(--w-primary-color);
    --w-biz-process-form-setting-icon-bg-color: var(--w-primary-color-1);
    --w-biz-process-form-setting-icon-color-selected: #ffffff;
    --w-biz-process-form-setting-icon-bg-color-selected: var(--w-primary-color);
    --w-biz-process-form-setting-item-bg-selected: var(--w-primary-color-1);
    --w-biz-process-form-setting-item-border-color: var(--w-primary-color-2);
    --w-biz-process-form-setting-item-border-color-selected: var(--w-primary-color);
    --w-biz-process-form-setting-item-count-color: var(--w-primary-color);

    --w-menu-item-background-selected: transparent;
    --w-menu-item-background-hover: transparent;

    &.ant-menu-inline .ant-menu-submenu-title {
      padding-right: 16px;
    }
    &.ant-menu-inline,
    .ant-menu-inline {
      > .ant-menu-item:last-child {
        margin-bottom: 0;
      }
    }

    .ant-menu-sub {
      position: relative;
      padding-top: 12px;
      > .ant-menu-item {
        height: 40px;
        line-height: 40px;
        .form-menu-item {
          position: relative;
          height: 40px;
          line-height: 40px;
          margin-left: 25px;

          .icon-div {
            > i {
              font-size: 20px;
            }
          }

          &::before {
            content: '';
            width: 25px;
            height: 20px;
            position: absolute;
            left: -25px;
            top: 0px;
            border-bottom-left-radius: 4px;
            border-bottom: 1px solid #e5e5e5;
          }
          &::after {
            content: '';
            width: 8px;
            height: 8px;
            position: absolute;
            border-radius: 50%;
            border: 1px solid #e5e5e5;
            left: -4px;
            top: 16px;
            background: #ffffff;
          }
        }
      }

      &::before {
        content: '';
        width: 1px;
        height: e('calc(100% - 21px)');
        background: #e5e5e5;
        position: absolute;
        left: 40px;
        top: 0;
      }
    }
    .ant-menu-item,
    .ant-menu-submenu {
      .ant-menu-submenu-arrow {
        display: none;
      }
      margin-bottom: 12px;
      .form-menu-item {
        cursor: pointer;
        border: 1px solid var(--w-biz-process-form-setting-item-border-color);
        height: 48px;
        line-height: 48px;
        margin-bottom: 12px;
        border-radius: 4px;
        &:hover {
          background-color: var(--w-biz-process-form-setting-item-bg-selected);
          border-bottom-color: var(--w-biz-process-form-setting-item-border-color);
        }
      }
      &.ant-menu-item-selected {
        .form-menu-item {
          background-color: var(--w-biz-process-form-setting-item-bg-selected);
          --w-biz-process-form-setting-icon-color: var(--w-biz-process-form-setting-icon-color-selected);
          --w-biz-process-form-setting-icon-bg-color: var(--w-biz-process-form-setting-icon-bg-color-selected);
          --w-biz-process-form-setting-item-border-color: var(--w-biz-process-form-setting-item-border-color-selected);
        }
      }
      &.hasTip {
        --w-biz-process-form-setting-icon-color: var(--w-danger-color);
        --w-biz-process-form-setting-icon-bg-color: var(--w-danger-color-1);
        --w-biz-process-form-setting-icon-color-selected: #ffffff;
        --w-biz-process-form-setting-icon-bg-color-selected: var(--w-danger-color);
        --w-biz-process-form-setting-item-bg-selected: var(--w-danger-color-1);
        --w-biz-process-form-setting-item-border-color: var(--w-danger-color-2);
        --w-biz-process-form-setting-item-border-color-selected: var(--w-danger-color);
        --w-biz-process-form-setting-item-count-color: var(--w-danger-color);
        .title {
          color: var(--w-danger-color);
        }
      }

      .icon-div {
        > i {
          font-size: 24px;
        }
        color: var(--w-biz-process-form-setting-icon-color);
        background-color: var(--w-biz-process-form-setting-icon-bg-color);
        width: 48px;
        text-align: center;
        font-weight: normal;
      }
      .title {
        color: var(--w-text-color-darker);
        font-size: var(--w-font-size-base);
        padding: 0 12px;
      }
      .count-div {
        font-size: 20px;
        color: var(--w-biz-process-form-setting-item-count-color);
        font-weight: bold;
        width: 48px;
        text-align: center;
        border-left: 1px solid var(--w-biz-process-form-setting-item-border-color);
      }
    }
  }

  .ant-collapse-content-box {
    padding: 0px 15px 0px 8px;

    ul {
      padding: 0px;
    }

    .form-select-item {
      width: 96%;
      margin-left: 8px;
      list-style: none;
      margin-top: 5px;
      padding: 5px 0px 5px 7px;
      background-color: #fff;
      border-radius: 2px;
      cursor: pointer;
      display: inline-block;
      outline: 1px dashed rgba(204, 204, 204, 1);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;

      &.filter {
        background: #e9e9e9;
        cursor: not-allowed;

        &:hover {
          outline: 1px dashed rgba(204, 204, 204, 1);
          color: unset;
        }
      }

      > .iconfont {
        margin-right: 5px;
      }
    }

    .form-select-item:hover {
      outline: 1px solid var(--w-primary-color);
      color: var(--w-primary-color);
    }

    .form-select-item.selected {
      outline: 1px solid #0078d7;
      color: #0078d7;
    }
  }
}
</style>
