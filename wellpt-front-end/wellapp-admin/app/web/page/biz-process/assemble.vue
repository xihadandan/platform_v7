<template>
  <HtmlWrapper :title="title">
    <a-layout class="biz-process-assemble-designer widget-design-layout">
      <a-layout-header class="biz-process-assemble-designer-header">
        <a-row>
          <a-col :span="9">
            <a-avatar
              shape="square"
              :size="24"
              style="background-color: #fff; color: var(--w-primary-color); margin-right: 8px; margin-top: -4px"
            >
              <Icon type="pticon iconfont icon-szgy-zhuye" style="font-weight: normal; vertical-align: top; font-size: 16px" />
            </a-avatar>
            <span style="font-weight: bold; font-size: var(--w-font-size-lg)">
              {{ processDefinition.name }}
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
              <a-button type="link" :class="{ selected: activeKey == 'lifecycle' }" @click="onChangeNav('lifecycle')">
                <Icon type="pticon iconfont icon-shengmingzhouqisvg-01"></Icon>
                生命周期
              </a-button>
            </div>
          </a-col>
          <a-col span="8" :style="{ textAlign: 'right' }">
            <a-button ghost @click="saveProcessDefinition" type="primary">
              <Icon type="pticon iconfont icon-ptkj-baocun"></Icon>
              保存
            </a-button>
            <a-button ghost v-if="processDefinition && processDefinition.uuid" @click="saveAsNewVersion" type="primary">
              <Icon type="pticon iconfont icon-ptkj-baocun"></Icon>
              保存新版本
            </a-button>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout v-if="activeKey == 'form'" class="widget-design-content biz-process-assemble-designer-content" :hasSider="true">
        <a-layout-sider theme="light" :width="345" class="left-sider">
          <keep-alive>
            <FormSelectPanel :processAssembleJson="processAssembleJson"></FormSelectPanel>
            <!-- <BizForm :processAssembleJson="processAssembleJson"></BizForm> -->
          </keep-alive>
        </a-layout-sider>
        <a-layout-content class="designer-component-container" style="background-color: #fff">
          <keep-alive>
            <component
              v-if="contentComponent != undefined && contentKeepAlive"
              :is="contentComponent"
              v-bind="selectMetadata"
              :key="contentKey"
            />
          </keep-alive>
          <component
            v-if="contentComponent != undefined && !contentKeepAlive"
            :is="contentComponent"
            v-bind="selectMetadata"
            :key="contentKey"
          />
          <div v-else-if="!contentComponent" style="text-align: center">
            <img src="./images/biz-lifecycle.jpg" />
          </div>
        </a-layout-content>
      </a-layout>
      <keep-alive>
        <a-layout-content v-if="activeKey == 'flow'" style="background-color: #fff">
          <PerfectScrollbar style="height: calc(100vh - 52px)">
            <BizFlow ref="bizFlow"></BizFlow>
          </PerfectScrollbar>
        </a-layout-content>
        <div v-else-if="activeKey == 'lifecycle'" class="biz-process-assemble-designer-content" style="text-align: center">
          <img src="./images/biz-lifecycle.jpg" />
        </div>
      </keep-alive>
    </a-layout>
  </HtmlWrapper>
</template>

<script>
import '@pageAssembly/app/web/assets/css/design.less';
import './css/assemble.less';

import BizEntity from './component/biz-entity';
import BizItem from './component/biz-item';
import BizForm from './component/biz-form';
import FormSelectPanel from './component/biz-form/form-select-panel.vue';
import BizFlow from './component/biz-process/flow.vue';
import BizIntegration from './component/biz-integration';
import ProcessBasicInfo from './component/biz-process/process-basic-info.vue';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';

const getQueryString = function (name, defaultValue) {
  var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
  var values = window.location.search.substr(1).match(reg);
  if (values != null) {
    return decodeURIComponent(values[2]);
  }
  if (defaultValue != null) {
    return defaultValue;
  }
  return '';
};
export default {
  name: 'BizProcessAssemble',
  components: { BizEntity, BizItem, BizForm, FormSelectPanel, BizFlow, BizIntegration, ProcessBasicInfo, Drawer, ExportDef },
  provide() {
    return {
      assemble: this,
      filterSelectOption: this.filterSelectOption
    };
  },
  data() {
    let activeKey = EASY_ENV_IS_BROWSER ? getQueryString('activeKey') || 'lifecycle' : 'flow';
    return {
      activeKey,
      activeTabKey: 'bizEntity',
      contentComponent: undefined,
      selectMetadata: undefined,
      contentKey: undefined,
      contentKeepAlive: false,
      processAssembleUuid: undefined,
      processDefinitionJson: {},
      processAssembleJson: {
        entity: {
          formId: '',
          formUuid: '',
          formName: '',
          formVersion: '',
          refFormUuid: '',
          refEntityIdField: '',
          pageUuid: '',
          pageName: '',
          pageId: '',
          pageVersion: ''
        },
        item: {
          pages: {}
        },
        handleForm: {
          process: {
            formId: '',
            formUuid: '',
            formName: '',
            refFormUuid: '',
            refEntityIdField: '',
            refEntityNameField: ''
          },
          nodes: [],
          items: []
        },
        integration: {}
      }
    };
  },
  created() {
    const _this = this;
    if (_this.processDefinition && _this.processDefinition.definitionJson) {
      _this.processDefinitionJson = JSON.parse(_this.processDefinition.definitionJson);
    }
    _this.processAssembleUuid = _this.processAssemble && _this.processAssemble.uuid;
    if (_this.processAssemble && _this.processAssemble.definitionJson) {
      _this.processAssembleJson = JSON.parse(_this.processAssemble.definitionJson);
    }
    _this.mergeDefinitionJson(_this.processDefinitionJson);
  },
  mounted() {
    const _this = this;
    let processDefUuid = _this.processDefinition.uuid;
    _this.pageContext.handleEvent(`process:design:change:${processDefUuid}`, processDefinition => {
      if (processDefinition) {
        _this.mergeDefinitionJson(processDefinition);
        _this.save();
      }
    });
    _this.pageContext.handleCrossTabEvent(`process:design:change:${processDefUuid}`, processDefinition => {
      if (processDefinition) {
        _this.mergeDefinitionJson(processDefinition);
        _this.save();
      }
    });
  },
  methods: {
    filterSelectOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
      );
    },
    mergeDefinitionJson(definitionJson) {
      const _this = this;
      Object.assign(_this.processDefinitionJson, definitionJson);
      let processDefinitionJson = _this.processDefinitionJson;
      let processAssembleJson = _this.processAssembleJson;
      // 业务主体
      if (processDefinitionJson && processDefinitionJson.entityConfig) {
        processAssembleJson.entity.refFormUuid = processDefinitionJson.entityConfig.formUuid;
        processAssembleJson.entity.refEntityIdField = processDefinitionJson.entityConfig.entityIdField;
        if (!processAssembleJson.entity.formUuid && processAssembleJson.entity.refFormUuid) {
          processAssembleJson.entity.formUuid = processAssembleJson.entity.refFormUuid;
        }
      }

      // 业务流程办理单
      if (processDefinitionJson && processDefinitionJson.formConfig) {
        if (!processAssembleJson.handleForm.process) {
          processAssembleJson.handleForm.process = { formId: '', formUuid: '', formName: '', refFormUuid: '' };
        }
        processAssembleJson.handleForm.process.refFormUuid = processDefinitionJson.formConfig.formUuid;
        processAssembleJson.handleForm.process.refEntityIdField = processDefinitionJson.formConfig.entityIdField;
        processAssembleJson.handleForm.process.refEntityNameField = processDefinitionJson.formConfig.entityNameField;
        if (!processAssembleJson.handleForm.process.formUuid && processAssembleJson.handleForm.process.refFormUuid) {
          processAssembleJson.handleForm.process.formUuid = processAssembleJson.handleForm.process.refFormUuid;
        }
      }

      // 阶段办理单
      let nodes = _this.getAllNodes(processDefinitionJson);
      let nodeConfigMap = {};
      if (!processAssembleJson.handleForm.nodes) {
        processAssembleJson.handleForm.nodes = [];
      }
      processAssembleJson.handleForm.nodes.forEach(node => (nodeConfigMap[node.nodeId] = node));
      nodes.forEach(node => {
        let nodeConfig = nodeConfigMap[node.id];
        if (nodeConfig) {
          nodeConfig.nodeName = node.name;
          nodeConfig.refFormUuid = (node.formConfig && node.formConfig.formUuid) || '';
          nodeConfig.refEntityIdField = (node.formConfig && node.formConfig.entityIdField) || '';
          nodeConfig.refEntityNameField = (node.formConfig && node.formConfig.entityNameField) || '';
          if (!nodeConfig.formUuid && nodeConfig.refFormUuid) {
            nodeConfig.formUuid = nodeConfig.refFormUuid;
          }
        } else {
          nodeConfig = {
            nodeName: node.name,
            nodeId: node.id,
            formUuid: (node.formConfig && node.formConfig.formUuid) || '',
            formId: '',
            formName: '',
            formVersion: '',
            entityIdField: (node.formConfig && node.formConfig.entityIdField) || '',
            refFormUuid: (node.formConfig && node.formConfig.formUuid) || '',
            refEntityIdField: (node.formConfig && node.formConfig.entityIdField) || '',
            refEntityNameField: (node.formConfig && node.formConfig.entityNameField) || ''
          };
          processAssembleJson.handleForm.nodes.push(nodeConfig);
        }
      });
      // 删除无效的阶段办理单信息
      for (let index = 0; index < processAssembleJson.handleForm.nodes.length; index++) {
        let nodeConfig = processAssembleJson.handleForm.nodes[index];
        if (nodes.findIndex(node => node.id == nodeConfig.nodeId) == -1) {
          processAssembleJson.handleForm.nodes.splice(index--, 1);
        }
      }
      processAssembleJson.handleForm.nodes = [...processAssembleJson.handleForm.nodes];

      // 事项办理单
      let items = _this.getAllItems(nodes);
      let itemConfigMap = {};
      if (!processAssembleJson.handleForm.items) {
        processAssembleJson.handleForm.items = [];
      }
      processAssembleJson.handleForm.items.forEach(item => (itemConfigMap[item.itemId] = item));
      items.forEach(item => {
        let itemConfig = itemConfigMap[item.id];
        if (itemConfig) {
          itemConfig.itemName = item.itemName;
          itemConfig.refFormUuid = (item.formConfig && item.formConfig.formUuid) || '';
          itemConfig.refEntityIdField = (item.formConfig && item.formConfig.entityIdField) || '';
          itemConfig.refEntityNameField = (item.formConfig && item.formConfig.entityNameField) || '';
          if (!itemConfig.formUuid && itemConfig.refFormUuid) {
            itemConfig.formUuid = itemConfig.refFormUuid;
          }
        } else {
          itemConfig = {
            itemName: item.itemName,
            itemId: item.id,
            formUuid: (item.formConfig && item.formConfig.formUuid) || '',
            formId: '',
            formName: '',
            formVersion: '',
            entityIdField: (item.formConfig && item.formConfig.entityIdField) || '',
            refFormUuid: (item.formConfig && item.formConfig.formUuid) || '',
            refEntityIdField: (item.formConfig && item.formConfig.entityIdField) || '',
            refEntityNameField: (item.formConfig && item.formConfig.entityNameField) || ''
          };
          processAssembleJson.handleForm.items.push(itemConfig);
        }
      });
      // 删除无效的事项办理单信息
      for (let index = 0; index < processAssembleJson.handleForm.items.length; index++) {
        let itemConfig = processAssembleJson.handleForm.items[index];
        if (items.findIndex(item => item.id == itemConfig.itemId) == -1) {
          processAssembleJson.handleForm.items.splice(index--, 1);
        }
      }
      processAssembleJson.handleForm.items = [...processAssembleJson.handleForm.items];

      // 事项
      if (!processAssembleJson.item.pages) {
        processAssembleJson.item.pages = {};
      }

      _this.convertBasicSelectFieldValue2Array(processDefinitionJson);
    },
    getAllNodes(processDefinitionJson) {
      let allNodes = [];
      let extractNodes = nodes => {
        nodes.forEach(node => {
          allNodes.push(node);
          if (node.nodes) {
            extractNodes(node.nodes);
          }
        });
      };
      extractNodes(processDefinitionJson.nodes || []);
      return allNodes;
    },
    getAllItems(nodes = this.getAllNodes(this.processDefinitionJson)) {
      let allItems = [];
      nodes.forEach(node => {
        node.items &&
          node.items.forEach(item => {
            allItems.push(item);
          });
      });
      return allItems;
    },
    getBizFormState() {
      let formState = this.getFormState(this.processAssembleJson.entity);
      let stateCode = formState.code;
      switch (stateCode) {
        case 'absent':
          formState.title = '业务主体表单不存在！';
          break;
        case 'unRef':
          formState.title = '业务流程定义没有引用业务主体表单！';
          break;
        case 'inconsistent':
          formState.title = '业务流程定义引用业务主体表单与定义的业务主体表单不一致！';
          break;
        case 'idFieldAbsent':
          formState.title = '业务主体ID字段未设置！';
          break;
        case 'nameFieldAbsent':
          formState.title = '业务主体名称字段未设置！';
          break;
        case 'ref':
          break;
      }
      return formState;
    },
    getHandleFormStateOfProcess(process) {
      let formState = this.getFormState(process);
      let stateCode = formState.code;
      switch (stateCode) {
        case 'absent':
          formState.title = '业务流程办理单不存在！';
          break;
        case 'unRef':
          formState.title = '业务流程定义业务流程办理单没有引用！';
          break;
        case 'inconsistent':
          formState.title = '业务流程定义引用业务流程办理单与定义的业务流程办理单不一致！';
          break;
        case 'idFieldAbsent':
          formState.title = '业务流程办理单业务主体ID字段未设置！';
          break;
        case 'nameFieldAbsent':
          formState.title = '业务流程办理单业务主体名称字段未设置！';
          break;
        case 'ref':
          break;
      }
      return formState;
    },
    getHandleFormStateOfNode(nodeConfig) {
      let formState = this.getFormState(nodeConfig);
      let stateCode = formState.code;
      switch (stateCode) {
        case 'absent':
          formState.title = '阶段办理单不存在！';
          break;
        case 'unRef':
          formState.title = '业务流程定义阶段办理单没有引用！';
          break;
        case 'inconsistent':
          formState.title = '业务流程定义引用阶段办理单与定义的阶段办理单不一致！';
          break;
        case 'idFieldAbsent':
          formState.title = '阶段办理单业务主体ID字段未设置！';
          break;
        case 'nameFieldAbsent':
          formState.title = '阶段办理单业务主体名称字段未设置！';
          break;
        case 'ref':
          break;
      }
      return formState;
    },
    getHandleFormStateOfItem(itemConfig) {
      let formState = this.getFormState(itemConfig);
      let stateCode = formState.code;
      switch (stateCode) {
        case 'absent':
          formState.title = '事项办理单不存在！';
          break;
        case 'unRef':
          formState.title = '业务流程定义事项办理单没有引用！';
          break;
        case 'inconsistent':
          formState.title = '业务流程定义引用事项办理单与定义的事项办理单不一致！';
          break;
        case 'idFieldAbsent':
          formState.title = '事项办理单业务主体ID字段未设置！';
          break;
        case 'nameFieldAbsent':
          formState.title = '事项办理单业务主体名称字段未设置！';
          break;
        case 'ref':
          break;
      }
      return formState;
    },
    getFormState(formInfo) {
      let formUuid = formInfo.formUuid;
      let refFormUuid = formInfo.refFormUuid;
      let title = '';
      let type = '';
      let code = '';
      if (!formUuid) {
        type = 'warning';
        code = 'absent';
      } else if (!refFormUuid) {
        type = 'error';
        code = 'unRef';
        // } else if (formUuid && refFormUuid && formUuid != refFormUuid) {
        //   type = 'warning';
        //   code = 'inconsistent';
      } else if (formInfo.hasOwnProperty('refEntityIdField') && !formInfo.refEntityIdField) {
        type = 'error';
        code = 'idFieldAbsent';
      } else if (formInfo.hasOwnProperty('refEntityNameField') && !formInfo.refEntityNameField) {
        type = 'error';
        code = 'nameFieldAbsent';
      } else {
        type = 'success';
        code = 'ref';
      }
      return { title, type, code };
    },
    onChangeNav(activeKey) {
      const _this = this;
      _this.closeDrawer();
      _this.activeKey = activeKey;
    },
    closeDrawer() {
      const _this = this;
      let designer = _this.getProcessDesigner();
      if (designer && designer.drawerVisibleKey) {
        _this.pageContext.emitEvent('closeDrawer:' + designer.drawerVisibleKey);
      }
    },
    onChangeTab(activeKey) {
      this.activeTabKey = activeKey;
      this.contentComponent = undefined;
      this.selectMetadata = undefined;
      if (this.$refs[activeKey] && this.$refs[activeKey].show) {
        this.$refs[activeKey].show();
      }
    },
    showContent({ component, metadata, key, keepAlive }) {
      this.contentComponent = component;
      this.selectMetadata = metadata;
      this.contentKey = key || 'contentKey_' + new Date().getTime();
      this.contentKeepAlive = keepAlive;
    },
    save() {
      $axios
        .post(`/proxy/api/biz/process/assemble/saveDefinitionJson?processDefUuid=${this.processDefUuid}`, this.processAssembleJson)
        .then(({ data: result }) => {
          if (result.data) {
            this.processAssembleUuid = result.data;
          }
        });
    },
    saveProcessDefinitionJson(e) {
      const _this = this;
      _this.$refs.processBasicInfo.validate().then(valid => {
        if (valid) {
          _this.convertBasicSelectFieldValue2String(_this.processDefinitionJson);
          $axios
            .post('/proxy/api/biz/process/definition/config/save', _this.processDefinitionJson)
            .then(({ data: result }) => {
              if (result.success) {
                e(true);
                _this.$message.success('保存成功！');
                _this.convertBasicSelectFieldValue2Array(_this.processDefinitionJson);
              } else {
                _this.$message.error(result.msg || '保存失败！');
              }
            })
            .catch(({ response }) => {
              _this.$message.error((response && response.data && response.data.msg) || '服务异常！');
            });
        }
      });
    },
    saveProcessDefinition() {
      this.$refs.bizFlow.save();
    },
    saveAsNewVersion() {
      this.$refs.bizFlow.saveAsNewVersion();
    },
    getProcessDesigner() {
      return this.$refs.bizFlow.getProcessDesigner();
    },
    convertBasicSelectFieldValue2Array(definiton) {
      const _this = this;
      if (!Array.isArray(definiton.listener) && definiton.listener) {
        definiton.listener = definiton.listener.split(';');
      } else if (!definiton.listener) {
        definiton.listener = [];
      }
      return definiton;
    },
    convertBasicSelectFieldValue2String(definiton) {
      const _this = this;
      if (Array.isArray(definiton.listener)) {
        definiton.listener = definiton.listener.join(';');
      }
      return definiton;
    },
    getDrawerContainer() {
      return this.$el.querySelector('.ant-layout-content');
    },
    openBizProcessDesigner() {
      this.activeKey = 'flow';
      this.$refs.bizFlow.setBuildWay('process');
    }
  }
};
</script>

<style lang="less" scoped>
.biz-process-assemble-designer {
}
</style>
