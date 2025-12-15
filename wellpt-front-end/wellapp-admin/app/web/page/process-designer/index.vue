<template>
  <a-layout class="widget-design-layout process-design-layout">
    <a-layout-header class="widget-design-header" :style="headerStyle">
      <a-row>
        <a-col :span="12">
          <a-avatar
            shape="square"
            :size="24"
            style="background-color: #fff; color: var(--w-primary-color); margin-right: 8px; margin-top: -4px"
          >
            <Icon
              v-if="showTitleIcon"
              type="pticon iconfont icon-a-iconfonticon-logo_wellinfo-01"
              title="公司LOGO"
              class="logo"
              style="font-weight: normal; vertical-align: top; font-size: 16px"
            ></Icon>
            <Icon v-else type="pticon iconfont icon-szgy-zhuye" style="font-weight: normal; vertical-align: top; font-size: 16px" />
          </a-avatar>
          <span style="font-weight: bold; font-size: var(--w-font-size-lg)">业务流程设计器</span>
        </a-col>
        <a-col :span="12" :style="{ textAlign: 'right' }">
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
    <a-layout :hasSider="true" class="widget-design-content">
      <a-layout-sider theme="light" :width="345">
        <ProcessSelectionPanel
          :designerType="designerType"
          :designer="designer"
          :designNodes="designNodes"
          :itemFlowDesigner="itemFlowDesigner"
          @selectedWorkflowItem="onSelectedWorkflowItem"
          ref="selectPanelRef"
        ></ProcessSelectionPanel>
      </a-layout-sider>
      <a-layout-content id="design-main" style="background-image: none">
        <ProcessBuildPanel v-show="buildWay == 'process'" :designer="designer"></ProcessBuildPanel>
        <ItemFlowBuildPanel v-show="buildWay == 'itemFlow'" :designer="itemFlowDesigner"></ItemFlowBuildPanel>
        <BizIntegrationWorkflow
          v-show="buildWay == 'workflow'"
          v-if="workflowItemDefinition"
          :itemDefinition="workflowItemDefinition"
          :key="workflowItemDefinition.id"
        ></BizIntegrationWorkflow>
      </a-layout-content>
      <a-layout-sider
        theme="light"
        ref="configrationPanelRef"
        class="widget-configuration-sider"
        :width="380"
        v-show="buildWay != 'workflow'"
      >
        <ProcessConfigurationPanel ref="processConfigRef" name="processConfigRef" v-show="buildWay == 'process'" :designer="designer">
          <template slot="basicInfoTab">
            <a-tab-pane key="basicInfo" tab="业务流程信息">
              <PerfectScrollbar>
                <ProcessBasicInfo ref="processBasicInfo" v-if="processDefinition" :processDefinition="processDefinition"></ProcessBasicInfo>
              </PerfectScrollbar>
            </a-tab-pane>
          </template>
        </ProcessConfigurationPanel>
        <ProcessConfigurationPanel
          ref="itemFlowConfigRef"
          name="itemFlowConfigRef"
          v-show="buildWay == 'itemFlow'"
          :designer="itemFlowDesigner"
        >
          <template slot="basicInfoTab">
            <a-tab-pane key="basicInfo" tab="事项流信息">
              <ItemFlowBasicInfo v-if="itemFlowDesigner" :designer="itemFlowDesigner"></ItemFlowBasicInfo>
            </a-tab-pane>
          </template>
        </ProcessConfigurationPanel>
      </a-layout-sider>
    </a-layout>
  </a-layout>
</template>

<script type="text/babel">
import '@pageAssembly/app/web/assets/css/design.less';
import './css/designer.less';
import { register } from '@antv/x6-vue-shape';
import ProcessDesigner from './designer/process-designer.js';
import ItemFlowDesigner from './designer/item-flow-designer.js';
import ProcessSelectionPanel from './component/process-select-panel.vue';
import ProcessBuildPanel from './component/process-build-panel.vue';
import ProcessConfigurationPanel from './component/process-configuration-panel.vue';
import ItemFlowBuildPanel from './component/item-flow-build-panel.vue';
import ItemFlowBasicInfo from './component/item-flow-configuration/basic-info.vue';
import ProcessBasicInfo from './component/process-configuration/process-basic-info.vue';
import ProcessNode from './design-compoment/process-node.vue';
import ProcessItem from './design-compoment/process-item.vue';
import ItemFlowNode from './design-compoment/item-flow-node.vue';
import BizIntegrationWorkflow from '../../page/biz-process/component/biz-integration/biz-integration-workflow.vue';
import { filterSelectOption, getCacheData } from './designer/utils.js';
import { isEmpty } from 'lodash';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
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
  props: {
    initProcessDefUuid: String,
    initProcessDefinition: Object,
    headerStyle: Object,
    showTitleIcon: {
      type: Boolean,
      default: true
    }
  },
  components: {
    ProcessSelectionPanel,
    ProcessBuildPanel,
    ProcessConfigurationPanel,
    ProcessBasicInfo,
    ItemFlowBuildPanel,
    ItemFlowBasicInfo,
    BizIntegrationWorkflow
  },
  inject: ['pageContext'],
  data() {
    let designer = new ProcessDesigner('desinger-container');
    let itemFlowDesigner = new ItemFlowDesigner('item-flow-desinger-container', designer);
    let designerType = getQueryString('designer');
    if (designerType == 'process' || designerType == 'itemFlow') {
      designer.setBuildWay(designerType);
    }
    return {
      processDefUuid: this.initProcessDefUuid,
      processDefinition: this.initProcessDefinition,
      designerType,
      designer,
      itemFlowDesigner,
      designNodes: {
        horizontal: [
          {
            name: '一阶段',
            layout: 'horizontal',
            count: 'one',
            iconClass: 'icon-wei'
          },
          {
            name: '二阶段',
            layout: 'horizontal',
            count: 'two'
          },
          {
            name: '三阶段',
            layout: 'horizontal',
            count: 'three',
            iconClass: 'icon-zhagebuju'
          },
          {
            name: '四阶段',
            layout: 'horizontal',
            count: 'four'
          },
          {
            name: '自定义阶段',
            layout: 'horizontal',
            count: 'custom'
          }
        ],
        vertical: [
          {
            name: '一阶段',
            layout: 'vertical',
            count: 'one',
            iconClass: 'icon-wei'
          },
          {
            name: '二阶段',
            layout: 'vertical',
            count: 'two'
          },
          {
            name: '三阶段',
            layout: 'vertical',
            count: 'three',
            iconClass: 'icon-zhagebuju'
          },
          {
            name: '四阶段',
            layout: 'vertical',
            count: 'four'
          },
          {
            name: '自定义阶段',
            layout: 'vertical',
            count: 'custom'
          }
        ]
      },
      workflowItemDefinition: null
    };
  },
  computed: {
    buildWay() {
      return this.designer.getBuildWay();
    }
  },
  provide() {
    return {
      designer: this.designer,
      itemFlowDesigner: this.itemFlowDesigner,
      filterSelectOption,
      getCacheData
    };
  },
  created() {
    const _this = this;
    _this.loadProcessDefinition();
    _this.registerVueCompoment();
  },
  methods: {
    // 加载业务流程定义
    loadProcessDefinition() {
      const _this = this;
      let processDefUuid = _this.getProcessDefUuid();
      if (_this.initProcessDefinition) {
        _this.$nextTick(() => {
          _this.processDefinition = _this.initProcessDefinition.definitionJson
            ? JSON.parse(_this.initProcessDefinition.definitionJson)
            : _this.initProcessDefinition;
          _this.processDefinition = _this.convertBasicSelectFieldValue2Array(_this.processDefinition);
          _this.designer.init(_this.processDefinition);
          _this.itemFlowDesigner.init(_this.processDefinition);
        });
      } else if (processDefUuid) {
        _this.$axios.get(`/proxy/api/biz/process/definition/get/${processDefUuid}`).then(({ data: { data: result = {} } }) => {
          if (result.definitionJson) {
            _this.processDefinition = JSON.parse(result.definitionJson);
            _this.processDefinition = _this.convertBasicSelectFieldValue2Array(_this.processDefinition);
            _this.designer.init(_this.processDefinition);
            _this.itemFlowDesigner.init(_this.processDefinition);
          } else {
            _this.processDefinition = result;
            _this.processDefinition = _this.convertBasicSelectFieldValue2Array(_this.processDefinition);
            _this.designer.init(_this.processDefinition);
            _this.itemFlowDesigner.init(_this.processDefinition);
          }
        });
      } else {
        _this.$nextTick(() => {
          _this.processDefinition = _this.convertBasicSelectFieldValue2Array({ enabled: true, formConfig: { configType: '2' } });
          _this.designer.init(_this.processDefinition);
        });
      }
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
    getProcessDefUuid() {
      const _this = this;
      if (_this.processDefUuid) {
        return _this.processDefUuid;
      }
      _this.processDefUuid = getQueryString('processDefUuid');
      return _this.processDefUuid;
    },
    registerVueCompoment() {
      const _this = this;
      register({
        shape: 'process-node',
        width: 100,
        height: 100,
        component: {
          provide() {
            return { designer: _this.designer, pageContext: _this.pageContext };
          },
          render: h => h(ProcessNode)
        }
      });
      register({
        shape: 'process-item',
        width: 100,
        height: 100,
        component: {
          provide() {
            return { designer: _this.designer, pageContext: _this.pageContext };
          },
          render: h => h(ProcessItem)
        }
      });
      register({
        shape: 'item-flow-node',
        width: 100,
        height: 100,
        component: {
          provide() {
            return { designer: _this.designer };
          },
          render: h => h(ItemFlowNode)
        }
      });
    },
    saveProcessDefinition() {
      this.doSaveProcessDefinition('/proxy/api/biz/process/definition/config/save', processDefUuid => {
        if (!/processDefUuid=[\w-]+$/g.test(window.location.href) && !this.initProcessDefUuid) {
          window.location.href = '/biz/process/assemble/' + processDefUuid + '?activeKey=flow';
          // if (window.location.href.indexOf('?') == -1) {
          //   window.location.href = window.location.href + '?processDefUuid=' + processDefUuid;
          // } else {
          //   window.location.href = window.location.href + '&processDefUuid=' + processDefUuid;
          // }
        }
        this.$message.success('保存成功！');
      });
    },
    saveAsNewVersion() {
      this.doSaveProcessDefinition('/proxy/api/biz/process/definition/config/saveAsNewVersion', processDefUuid => {
        this.$message.success('保存成功！');
        window.location.href = '/biz/process/assemble/' + processDefUuid + '?activeKey=flow';
        // window.location.href = window.location.href.replace(/(?<=processDefUuid=)[\w-]+/g, processDefUuid);
      });
    },
    getProcessNodes() {
      let processNodes = [];
      let getNodes = function (nodes, processNodes) {
        for (let i = 0; i < nodes.length; i++) {
          let node = nodes[i];
          processNodes.push(node);
          if (node.nodes) {
            getNodes(node.nodes, processNodes);
          }
        }
      };
      getNodes(this.processDefinition.nodes || [], processNodes);
      return processNodes;
    },
    doSaveProcessDefinition(url, successCallback) {
      const _this = this;
      _this.$refs.processBasicInfo
        .validate()
        .then(valid => {
          if (valid) {
            return _this.itemFlowDesigner.validate(_this);
          }
        })
        .then(valid => {
          if (valid) {
            _this.itemFlowDesigner.toProcessDefinition();
            _this.processDefinition = this.designer.toProcessDefinition();
          }
          return valid;
        })
        .then(valid => {
          if (valid) {
            return new Promise((resolve, reject) => {
              let processNodes = _this.getProcessNodes();
              let emptyCodeNodes = processNodes.filter(item => isEmpty(item.code));
              let codeMap = {};
              let sameCodeNodes = [];
              if (isEmpty(emptyCodeNodes)) {
                processNodes.forEach(item => {
                  if (codeMap[item.code]) {
                    codeMap[item.code].push(item);
                    sameCodeNodes = codeMap[item.code];
                  } else {
                    codeMap[item.code] = [item];
                  }
                });
                if (isEmpty(sameCodeNodes)) {
                  resolve(true);
                } else {
                  _this.$message.error(`阶段[${sameCodeNodes.map(item => item.name)}]的编码不能相同！`);
                  reject(false);
                }
              } else {
                _this.$message.error(`阶段[${emptyCodeNodes.map(item => item.name)}]的编码不能为空！`);
                reject(false);
              }
            });
          }
        })
        .then(valid => {
          if (valid) {
            return new Promise((resolve, reject) => {
              let items = _this.designer.getLatestProcessTree().getTreeDataList('item');
              let itemCodeMap = {};
              let repeatCodes = [];
              items.forEach(item => {
                if (item.data && item.data.configuration) {
                  let itemCode = item.data.configuration.itemCode;
                  if (itemCode) {
                    if (itemCodeMap[itemCode]) {
                      repeatCodes.push(itemCode);
                    }
                    itemCodeMap[itemCode] = itemCode;
                  }
                }
              });
              if (repeatCodes.length > 0) {
                _this.$message.error(`事项编码不能重复[${repeatCodes}]！`);
                reject(false);
              } else {
                resolve(true);
              }
            });
          }
        })
        .then(valid => {
          if (valid) {
            _this.convertBasicSelectFieldValue2String(_this.processDefinition);
            $axios
              .post(url, _this.processDefinition)
              .then(({ data }) => {
                if (data.success) {
                  if (_this.processDefUuid) {
                    _this.pageContext.emitEvent(`process:design:change:${_this.processDefUuid}`, _this.processDefinition);
                    _this.pageContext.emitCrossTabEvent(`process:design:change:${_this.processDefUuid}`, _this.processDefinition);
                  } else {
                    _this.pageContext.emitEvent('process:design:create', _this.processDefinition);
                    _this.pageContext.emitCrossTabEvent('process:design:create', _this.processDefinition);
                  }
                  if (successCallback) {
                    successCallback.call(this, data.data);
                  } else {
                    _this.$message.success('保存成功！');
                  }
                } else {
                  _this.$message.error(data.msg || '保存失败！');
                }
              })
              .catch(({ response }) => {
                _this.$message.error((response.data && response.data.msg) || '保存失败！');
              });
          }
        });
    },
    onSelectedWorkflowItem({ itemDefinition }) {
      this.workflowItemDefinition = itemDefinition;
    }
  },
  watch: {
    buildWay(v) {
      if (v == 'process' || v == 'itemFlow') {
        if (this.$refs[v + 'ConfigRef']) {
          this.$refs[v + 'ConfigRef'].collapseChange();
        }
        if (this.$refs.selectPanelRef) {
          this.$refs.selectPanelRef.collapseChange();
        }
      }
    }
  }
};
</script>

<style lang="less">
.process-design-layout {
  // height: 100%;
  // .ant-drawer-header {
  //   padding: 11px;
  //   height: 52px;
  // }

  .widget-design-content {
    overflow: hidden;
  }
}

.process-design-layout {
  #design-main {
    .widget-design-drawer {
      .ant-drawer-content-wrapper {
        height: e('calc(100vh - 99px)');
      }
    }
  }
}

.process-design-layout .widget-select-tabs {
  height: e('calc(100vh - 58px)');

  // > .ant-tabs-content {
  //   height: e('calc(100vh - 88px)');
  // }
}

.process-design-layout .widget-build-container {
  .widget-drop-panel {
    min-height: e('calc(100vh - 102px)');
  }

  .panel-scroll.ps {
    height: e('calc(100vh - 102px)');
  }
}

.process-design-layout > .ant-layout.ant-layout-has-sider > .ant-layout-sider:last-child {
  height: e('calc(100vh - 59px)');
}
.process-design-layout {
  .edit-widget-property-container {
    .ps {
      height: e('calc(100vh - 99px)');
    }
  }

  .widget-design-select-sider {
    .sider-select-panel-scroll {
      height: e('calc(100vh - 61px)');
    }
  }
}
</style>
