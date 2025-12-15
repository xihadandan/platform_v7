<template>
  <HtmlWrapper :title="vTitle" ref="htmlWrapper">
    <a-layout class="widget-design-layout flow-designer-layout">
      <a-layout-header class="flow-designer-header">
        <a-row>
          <a-col :span="12">
            <Icon type="pticon iconfont icon-logo_wellinfo" title="公司LOGO" class="logo" />
            <h1>{{ workflowName }}</h1>
          </a-col>
          <a-col :span="12" :style="{ textAlign: 'right' }" v-if="!spinning && fetched">
            <a-button v-if="workFlow && workFlow.uuid" type="primary" :ghost="true" @click="saveAsNewVersion" style="margin-right: 8px">
              保存新版本
            </a-button>
            <a-button type="primary" :ghost="true" @click="e => saveDefinition()">保存</a-button>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout :hasSider="true" class="flow-designer-content">
        <a-layout-sider theme="light" class="flow-selection-sider">
          <flow-selection-panel :graphItem="graphItem" />
        </a-layout-sider>
        <a-layout-content id="design-main" class="flow-designer-main">
          <div class="flow-spin-center" v-if="(spinning && !needUpgrade) || (needUpgrade && !alreadyPromptedUpgrade)">
            <a-spin />
          </div>
          <flow-tool-bar :graphItem="graphItem" :workflowName="workflowName" @checkWorkFlow="handleCheckWorkFlow" />
          <div :class="['graph-full', graphItem && graphItem.selectedTool ? `selected-tool-${graphItem.selectedTool}` : '']">
            <div class="flow-graph-empty" v-if="showEmpty">
              <widget-build-empty-img />
              请选择节点开始设计
            </div>
            <div class="graph-wrapper">
              <div id="graph-container" @dragover.stop="onDragover" @mousemove="onMouseMove"></div>
            </div>
            <!-- 等价流程开启时显示 -->
            <div class="equal-flow-mask" v-if="equalFlowMaskShow"></div>
          </div>
          <div class="configuration-drawer-container"></div>
        </a-layout-content>
        <a-layout-sider theme="light" class="widget-configuration-sider flow-configuration-sider">
          <div class="spin-center" v-if="!fetched">
            <a-spin />
          </div>
          <flow-configuration-panel
            :graphItem="graphItem"
            v-if="fetched"
            @cellConfigurationMounted="cellConfigurationMounted"
            ref="configurationPanel"
          >
            <template slot="flowProperty">
              <flow-property-configuration :formData="workFlow.property" ref="flowProperty">
                <template slot="flowTimers">
                  <flow-property-flow-timers :formData="workFlow.property" :graphItem="graphItem" />
                </template>
              </flow-property-configuration>
            </template>
          </flow-configuration-panel>
        </a-layout-sider>
      </a-layout>
    </a-layout>
  </HtmlWrapper>
</template>

<script>
// import '@dyform/app/web/framework/vue/install';
import '@installPageWidget';
import '@pageAssembly/app/web/assets/css/design.less';
import './component/style/designer.less';
import './component/style/graph.less';
import FlowDesigner from './component/designer/FlowDesigner';
import WorkFlow from './component/designer/WorkFlow';
import FlowProperty from './component/designer/FlowProperty';
import NodeTask from './component/designer/NodeTask';
import AutoSubmitRule from './component/designer/AutoSubmitRule';
import FlowSelectionPanel from './component/flow-selection-panel/index.vue';
import FlowToolBar from './component/flow-tool-bar/index.vue';
import FlowConfigurationPanel from './component/flow-configuration-panel.vue';
import FlowPropertyConfiguration from './component/flow-property-configuration/index.vue';
import FlowPropertyFlowTimers from './component/flow-property-configuration/flow-timers.vue';
import widgetBuildEmptyImg from '@pageAssembly/app/web/page/page-designer/component/widget-build-empty.vue';
import constant, {
  propertyAddKey,
  shapeMapConfig,
  taskRules,
  subflowRules,
  directionRules,
  swimlaneRules,
  conditionRules,
  getCustomRules,
  flowTaskRights,
  constDefault,
  constCustom,
  availableBizOrgOptions,
  multiJobFlowTypeConfig,
  selectionType
} from './component/designer/constant';
import { transformGraphData } from './component/designer/utils';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
import { set } from 'lodash';

export default {
  name: 'WorkflowDesigner',
  data() {
    const designer = new FlowDesigner();
    let workFlow = new WorkFlow();
    let flowProperty = new FlowProperty();
    propertyAddKey.forEach(key => {
      flowProperty[key] = workFlow[key];
    });
    workFlow.property = flowProperty;
    return {
      designer,
      graphItem: undefined,
      workFlow,
      fetchedDiction: false,
      fetchedFlowSetting: false,
      graph: {
        instance: undefined
      },
      equalFlowMaskShow: false,
      curCellConfigurationVm: undefined,
      themeClass: '',
      spinning: true,
      api: undefined,
      needUpgrade: false
    };
  },
  components: {
    FlowSelectionPanel,
    FlowToolBar,
    FlowConfigurationPanel,
    FlowPropertyConfiguration,
    FlowPropertyFlowTimers,
    widgetBuildEmptyImg
  },
  provide() {
    return {
      designer: this.designer,
      workFlowData: this.workFlow,
      graph: this.graph
    };
  },
  computed: {
    vTitle() {
      let title = '流程设计';
      if (this.workFlow.property.name) {
        title += ' - ' + this.workFlow.property.name;
      }
      return title;
    },
    workflowName() {
      let title = '流程设计';
      if (this.workFlow.property.name) {
        title = this.workFlow.property.name;
      }
      if (this.workFlow.property.version) {
        title = `${title} v${this.workFlow.property.version}`;
      } else {
        if (this.workFlow.property.name) {
          title = `${title} v1.0`;
        }
      }
      return title;
    },
    showEmpty() {
      let show = false;
      if (!this.spinning && this.graphItem) {
        const { selectedTool, cellsEmpty } = this.graphItem;
        // const types = selectionType[0].list.map(item => item.type);
        // if (selectedTool && !types.includes(selectedTool)) {
        show = cellsEmpty;
        if (selectedTool) {
          show = false;
        }
      }
      return show;
    },
    fetched() {
      if (!this.spinning && this.needUpgrade && this.fetchedFlowSetting && !this.alreadyPromptedUpgrade) {
        this.alreadyPromptedUpgrade = true;
        this.upgradeConfirm();
      }
      return this.fetchedDiction && this.fetchedFlowSetting;
    }
  },
  created() {
    let titleExpressionMode = constDefault;
    if (this.workFlowData) {
      this.designer.isNewFlow = false;
      if (this.workFlowData.xmlDefinition && this.workFlowData.graphData === null) {
        this.needUpgrade = true;
      }
      const data = JSON.parse(JSON.stringify(this.workFlowData));
      if (data.property.equalFlow.id) {
        this.equalFlowMaskShow = true;
      }

      for (const key in data) {
        if (propertyAddKey.includes(key) && data[key]) {
          this.workFlow.property[key] = data[key];
          this.workFlow[key] = data[key];
        } else if (key === 'property') {
          this.workFlow[key] = { ...this.workFlow[key], ...data[key] };
        } else {
          this.workFlow[key] = data[key];
        }
      }

      if (data.titleExpression) {
        titleExpressionMode = constCustom;
      }
      let graphData;
      if (data.graphData) {
        graphData = JSON.parse(data.graphData);
        if (graphData.property) {
          titleExpressionMode = graphData.property.titleExpressionMode;

          if (graphData.property.i18n['zh_CN']) {
            if (graphData.property.i18n['zh_CN']['workflowName'] !== this.workFlow.name) {
              // 复制的流程
              graphData.property.i18n['zh_CN']['workflowName'] = this.workFlow.name;
              delete graphData.property.i18n['en_US']['workflowName'];
              delete graphData.property.i18n['th_TH']['workflowName'];
            }
          }
          this.workFlow.property.i18n = graphData.property.i18n || {};
          this.workFlow.property.autoSubmitRule = graphData.property.autoSubmitRule;
        }
      }
      if (data.timers && data.timers.length) {
        this.addTimerProperty(graphData);
      }
    }
    this.workFlow.property.titleExpressionMode = titleExpressionMode;
  },
  mounted() {
    this.api = require('./component/api/index');
    this.getFlowSetting();
    this.themeClass = this.$refs.htmlWrapper.baseClass;
    // this.userDetails this._$USER  window.__INITIAL_STATE__.userDetails相同
    if (this.userDetails) {
      this.designer.setUserDetails(this.userDetails);
      this.getAppModule(this._$SYSTEM_ID);
      this.getEnableOrgs(this._$SYSTEM_ID);
      this.getOrgs(this._$SYSTEM_ID);
    }
    if (this.workFlowData) {
      this.getDiction({
        uuid: this.workFlow.uuid,
        moduleId: this.workFlow.moduleId
      });
      let property = this.workFlow.property;
      if (!property.orgId) {
        // 62旧数据没有组织id
        property.useDefaultOrg = '1';
      }

      if (property.enableMultiOrg === '1' && property.multiOrgs) {
        // 多组织审批
        this.getMultiOrgVersionId(property.multiOrgs.map(item => item.orgId));
      }
    } else {
      this.getDiction();
    }
    this.getDefaultOrgBySystem();
    // this.initGraph();
    this.handleEvent();
    window.addEventListener('resize', this.resizeGraph);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeGraph);
  },
  methods: {
    onDragover(event) {
      this.graphItem.updateDragInfo({
        offsetX: event.offsetX,
        offsetY: event.offsetY,
        x: event.x,
        y: event.y
      });
    },
    onMouseMove(event) {
      this.graphItem.setContainerMoveEvent(event);
      // this.graphItem.currentInfo.x = event.pageX;
      // this.graphItem.currentInfo.y = event.pageY;
    },
    handleEvent() {
      this.pageContext.handleEvent('getOrgVersionId', this.getOrgVersionId);
      this.pageContext.handleEvent('equalFlowShow', ({ equalFlowShow }) => {
        this.equalFlowMaskShow = equalFlowShow;
      });
      this.pageContext.handleEvent('getDefaultOrgBySystem', () => {
        this.getDefaultOrgBySystem();
      });
      this.pageContext.handleEvent('getMultiOrgVersionId', ({ orgIds }) => {
        this.getMultiOrgVersionId(orgIds);
      });
    },
    // 窗口改变重置画布
    resizeGraph() {
      const graphFull = this.$el.querySelector('.graph-full');
      const elH = this.$el.offsetHeight;
      const headerH = this.$el.querySelector('.flow-designer-header').offsetHeight;
      const toolH = this.$el.querySelector('.graph-tool-bar').offsetHeight;
      if (this.graphItem && graphFull) {
        const { graph } = this.graphItem;
        const width = graphFull.clientWidth;
        const height = elH - headerH - toolH;
        graph.resize(width, height);
      }
    },
    importGraphData() {
      if (this.workFlow.graphData) {
        const graphData = JSON.parse(this.workFlow.graphData);
        let startTaskId = ''; // 发起环节id
        let startNode = null;
        for (let index = 0; index < graphData.cells.length; index++) {
          const cell = graphData.cells[index];
          let cellData = cell.data;
          const shape = cell.shape;
          if (shape === 'NodeCondition') {
            if (!cellData.id) {
              cellData.id = cell.id;
              cellData.name = cellData.conditionName;
              cellData.remark = cellData.conditionBody;
            }
          } else if (shape === 'EdgeDirection') {
            if (!cellData.toID) {
              // 目标节点是判断点，判断点的业务数据data没有id(旧数据)
              cellData.type = '3';
              cellData.toID = cell.target.cell;
            }
            if (cellData.autoUpdateName === undefined) {
              cellData.autoUpdateName = true;
            }
          } else if (shape === 'NodeTask' || shape === 'NodeCollab') {
            // 环节办理人修改同步
            const task = this.workFlow.tasks.find(task => task.id == cellData.id);
            let startDirection;
            if (!startTaskId) {
              startDirection = this.workFlow.directions.find(direction => {
                return direction.toID == cellData.id && direction.fromID === constant.StartFlowId;
              });
            }
            if (task && cellData) {
              const nodeTaskData = new NodeTask(task);
              for (const key in nodeTaskData) {
                if (cellData[key] === undefined) {
                  // 环节新增参数赋值
                  cellData[key] = nodeTaskData[key];
                  if ((key === 'enabledJobFlowType' || key === 'multiJobFlowType') && startDirection) {
                    startTaskId = cellData.id;
                    cellData.enabledJobFlowType = true;
                    cellData.multiJobFlowType = multiJobFlowTypeConfig[2]['id'];
                  }
                }
              }

              cellData.users = task.users;
              cellData.transferUsers = task.transferUsers;
              cellData.copyUsers = task.copyUsers;
              cellData.emptyToUsers = task.emptyToUsers;
              cellData.monitors = task.monitors;
              cellData.decisionMakers = task.decisionMakers;
              cellData.startRightConfig = task.startRightConfig;
              cellData.todoRightConfig = task.todoRightConfig;
              cellData.doneRightConfig = task.doneRightConfig;
              cellData.monitorRightConfig = task.monitorRightConfig;
              cellData.adminRightConfig = task.adminRightConfig;
              cellData.copyToRightConfig = task.copyToRightConfig;
              cellData.viewerRightConfig = task.viewerRightConfig;
            }
          } else if (shape === 'NodeSubflow') {
            // 子流程办理人修改同步
            let task = this.workFlow.tasks.find(task => task.id == cellData.id);
            if (task && cellData) {
              cellData.subTaskMonitors = task.subTaskMonitors;
            }
          } else if (shape === 'NodeCircle') {
            if (cellData.id === constant.StartFlowId) {
              startNode = cell;
            }
          }
        }
        if (startNode) {
          this.graphItem.setStartNodePosition(startNode.position);
        }
        this.graphItem.fromJSON({ cells: graphData.cells });
      }
    },
    initGraph() {
      import('./component/graph/index').then(res => {
        const FlowGraph = res.default;
        this.graphItem = new FlowGraph({
          designer: this.designer,
          themeClass: this.themeClass,
          vueInstance: this
        });
        this.graph.instance = this.graphItem;

        if (!this.needUpgrade) {
          this.importGraphData();
        }

        this.spinning = false;
      });
    },
    // 升级确认
    upgradeConfirm() {
      this.$confirm({
        title: '提示',
        content: '当前为旧版流程，为了更好的体验建议尽快升级',
        okText: '确定升级',
        onOk: () => {
          return new Promise((resolve, reject) => {
            const startConvert = () => {
              const graphData = transformGraphData(this.workFlow, this.graphItem, this.designer);
              this.workFlow.graphData = JSON.stringify(graphData);
              this.importGraphData();
              resolve();
            };
            startConvert();
          });
        },
        cancelText: '跳转旧版',
        onCancel: () => {
          let sysUri = '',
            prefixUri = '';
          if (this._$SYSTEM_ID) {
            sysUri = '&system_id=' + this._$SYSTEM_ID;
            prefixUri = '/sys/' + this._$SYSTEM_ID + '/_';
          }
          window.open(prefixUri + '/web/app/pt-mgr/pt-wf-mgr/pt-wf-designer.html?id=' + this.uuid + sysUri);
        }
      });
    },
    // 增加流程计时属性
    addTimerProperty(graphData) {
      let timersConfig = [];
      if (graphData && graphData.timers) {
        timersConfig = graphData.timers;
      }
      this.workFlow.timers.map(timer => {
        if (timer.limitUnitField === undefined) {
          timer.limitUnitField = '';
        }
        if (timer.dueTimeType === undefined) {
          timer.dueTimeType = '1';
        }
        if (timer.dueUnitType === undefined) {
          timer.dueUnitType = '1';
        }
        if (timer.dueFrequencyType === undefined) {
          timer.dueFrequencyType = '1';
        }
        if (timer.timingModeType === undefined) {
          timer.timingModeType = '';
        }
        if (timer.timingModeUnit === undefined) {
          timer.timingModeUnit = '';
        }
        if (timer.alarmElements) {
          // 预警提醒
          timer.alarmElements.map(item => {
            if (item.alarmTimeType === undefined) {
              item.alarmTimeType = '1';
            }
            if (item.alarmUnitType === undefined) {
              item.alarmUnitType = '1';
            }
            if (item.alarmFrequencyType === undefined) {
              item.alarmFrequencyType = '1';
            }
          });
        }
        const findItem = timersConfig.find(item => item.timerId === timer.timerId);
        if (findItem && findItem.i18n) {
          timer.i18n = findItem.i18n;
        }
      });
    },
    // 获取流程设置
    getFlowSetting() {
      let property = this.workFlow.property;
      Promise.all([
        this.api.fetchFlowSettingByKey('ACTION'),
        this.api.fetchFlowSettingByKey('FLOW_DEFINITION'),
        import('@framework/vue/utils/util'),
        this.api.fetchLocaleOptions(),
        import('./component/graph/index')
      ]).then(res => {
        console.log(res);
        const actions = JSON.parse(res[0]['attrVal']);
        console.log(JSON.parse(res[0]['attrVal']));
        this.designer.setFlowSettingActions(actions);
        this.designer.syncButtons(this.workFlow);

        const flowDefinition = JSON.parse(res[1]['attrVal']);
        this.designer.setFlowDefinition(flowDefinition);

        if (flowDefinition.enabledAutoSubmit && this.designer.isNewFlow) {
          // 设置后台配置的做为默认值
          property.enabledAutoSubmit = flowDefinition.enabledAutoSubmit;
        }
        if (property.enabledAutoSubmit && !property.autoSubmitRule) {
          property.autoSubmitRule = new AutoSubmitRule({
            mode: flowDefinition.autoSubmitMode
          });
        }
        this.frameworkUtil = res[2];
        this.designer.setLanguageOptions(res[3], this);
        this.initGraphAsync(res[4]);
        this.fetchedFlowSetting = true;
      });
    },
    initGraphAsync(res) {
      const FlowGraph = res.default;
      this.graphItem = new FlowGraph({
        designer: this.designer,
        themeClass: this.themeClass,
        vueInstance: this
      });
      this.graph.instance = this.graphItem;

      if (!this.needUpgrade) {
        this.importGraphData();
      }

      this.spinning = false;
    },
    // 获取模块
    getAppModule(systemUnitId) {
      const params = {
        serviceName: 'appModuleMgr',
        queryMethod: 'loadSelectData',
        pageSize: 1000,
        pageNo: 1,
        includeSuperAdmin: true,
        systemUnitId
      };

      this.$axios
        .post('/common/select2/query', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.results) {
              const data = res.data.results;
              this.designer.setModules(data);
            }
          }
        });
    },
    // 获取默认组织
    getDefaultOrgBySystem(systemId = this._$SYSTEM_ID) {
      const property = this.workFlow.property;
      const params = {
        system: systemId
      };
      this.$axios
        .get('/proxy/api/org/organization/getDefaultOrgBySystem', {
          params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.designer.setDefaultOrgData(data);
              if (property.useDefaultOrg === '1' && (systemId || !property.orgId)) {
                this.workFlow.property.orgId = data.id;
              }
              this.getOrgVersionId();
              /*
            data.id 组织ID"O_"开头
            data.uuid === orgUuid 组织UUID
            */
              console.log('获取默认组织', data);
            }
          }
        });
    },
    /*
    1、orgUuid 组织UUID
      800094164648718336
    2、orgVersionId 组织版本号ID
      "V_"开头 V_115494399042912256
    3、组织ID
      "O_"开头  O_800094164644524032 威尔组织

    通过组织ID获取组织版本ID
    */
    getOrgVersionIdByOrgId(orgId) {
      return new Promise((resolve, reject) => {
        this.api.fetchOrgVersionIdByOrgId(orgId).then(res => {
          resolve(res);
        });
      });
    },
    getOrgVersionId({ orgId = this.workFlow.property.orgId, callback } = {}) {
      if (orgId) {
        this.getOrgVersionIdByOrgId(orgId).then(orgVersionId => {
          if (callback) {
            callback(orgVersionId);
          } else {
            this.workFlow.property.orgVersionId = orgVersionId;
          }
        });
      }
    },
    getMultiOrgVersionId(multiOrgIds) {
      if (!multiOrgIds) {
        this.workFlow.property.orgVersionIds = '';
        return;
      }
      if (multiOrgIds && typeof multiOrgId === 'string') {
        multiOrgIds = multiOrgIds.split(';');
      }
      Promise.all(
        multiOrgIds.map(item => {
          return new Promise((resolve, reject) => {
            this.getOrgVersionIdByOrgId(item).then(vId => {
              resolve(vId);
            });
          });
        })
      ).then(vIds => {
        this.workFlow.property.orgVersionIds = vIds;
      });
    },
    /*
    获取组织列表

    返回
      组织ID"O_"开头
      组织名称
    */
    getOrganizationList(systemUnitId) {
      const params = {
        serviceName: 'organizationService',
        queryMethod: 'loadSelectData',
        pageSize: 1000,
        pageNo: 1,
        systemUnitId,
        status: 1
      };

      this.$axios
        .post('/common/select2/query', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.results) {
              const data = res.data.results;
              this.designer.setOrganizationList(data);
            }
          }
        });
    },
    // 查询所有的组织列表
    getOrgs(systemId) {
      this.api
        .fetchQueryOrgs({
          fetchBizOrg: true,
          system: systemId
        })
        .then(orgs => {
          this.designer.setAllOrgs(orgs);

          let orgList = [];
          orgs.forEach(item => {
            if (item.enable && !item.expired) {
              orgList.push({
                id: item.id,
                text: item.name,
                uuid: item.uuid
              });
            }
          });
          this.designer.setOrganizationList(orgList);
        });
    },
    // 获取启用的组织列表
    getEnableOrgs(systemId) {
      this.api
        .fetchEnableOrgs({
          fetchBizOrg: true,
          system: systemId
        })
        .then(res => {
          this.setBizOrgIdRoleMap(res);
          this.showErrorUseBizOrg(res);
          this.designer.setEnableOrgs(res);
        });
    },
    setBizOrgIdRoleMap(orgs) {
      const getBizOrgList = arr => {
        let list = [];
        const getChildren = data => {
          data.forEach(item => {
            if (item.bizOrgs) {
              getChildren(item.bizOrgs);
              list.push(...item.bizOrgs);
            }
          });
        };
        getChildren(arr);
        return list;
      };
      const bizOrgList = getBizOrgList(orgs);

      Promise.all(
        bizOrgList.map(org => {
          const bizOrgId = org.id;
          return this.api.fetchBizOrgRolesByBizOrgId({ bizOrgId });
        })
      ).then(res => {
        this.designer.setBizOrgIdRoleMap(res, bizOrgList);
      });
      // orgs.forEach(item => {
      //   if (item.bizOrgs.length) {
      //     item.bizOrgs.forEach(biz => {
      //       const bizOrgId = biz.id;
      //       this.api.fetchBizOrgRolesByBizOrgId({ bizOrgId }).then(res => {
      //         let ruleMap = {};
      //         res.forEach(item => {
      //           ruleMap[item.id] = item;
      //         });
      //         bizOrgIdRoleMap[bizOrgId] = ruleMap;
      //       });
      //     });
      //   }
      // });
    },
    showErrorUseBizOrg(orgs) {
      if (!this.designer.isNewFlow) {
        const property = this.workFlowData.property;
        if (property.availableBizOrg === availableBizOrgOptions[2]['value'] && property.bizOrgId) {
          const checkIsExist = id => {
            let exist = false; // 默认不存在
            for (let index = 0; index < orgs.length; index++) {
              const item = orgs[index];
              if (item.bizOrgs) {
                const hasIndex = item.bizOrgs.findIndex(f => f.id === id);
                if (hasIndex > -1) {
                  exist = true;
                  break;
                }
              }
            }

            return exist;
          };

          let valid = true;
          const bizOrgIds = property.bizOrgId.split(';');
          for (let index = 0; index < bizOrgIds.length; index++) {
            const bizOrgId = bizOrgIds[index];
            if (!checkIsExist(bizOrgId)) {
              valid = false;
              break;
            }
          }
          if (!valid) {
            this.$message.warn('当前流程的可用业务组织已部分失效，请检查配置！');
          }
        }
      }
    },
    // 数据数据字典
    getDiction({ uuid = '<NEW>', moduleId = null } = {}) {
      this.$axios.get(`/proxy/api/workflow/scheme/diction/json.action?open&uuid=${uuid}&moduleId=${moduleId}`).then(res => {
        if (res.status === 200) {
          this.designer.setDiction(res.data);
          this.fetchedDiction = true;
        }
      });
    },
    // 获取流程数据
    getFlowData(uuid) {
      this.$axios
        .get('/proxy/api/workflow/scheme/flow/json.action', {
          params: {
            uuid
          }
        })
        .then(res => {
          if (res.status === 200) {
            const { data } = res;
            for (const key in data) {
              this.workFlow[key] = data[key];
              if (propertyAddKey.includes(key)) {
                data.property[key] = data[key];
              }
            }
            this.designer.setData(data);
            if (data.graphData && this.graphItem) {
              const graphJson = JSON.parse(data.graphData);
              this.graphItem.fromJSON(graphJson);
            }
            this.getDiction({
              uuid: data.uuid,
              moduleId: data.moduleId
            });
          }
        });
    },
    // 设置元素配置实例
    cellConfigurationMounted(vm) {
      this.curCellConfigurationVm = vm;
    },
    // 设置选中元素标签页
    selectCellTab(errors) {
      if (errors[0] && errors[0].tabKey) {
        this.curCellConfigurationVm.tabKey = errors[0].tabKey;
      }
    },
    // 显示元素表单错误
    showCellError(errors, data, shape) {
      // 渲染流程属性错误
      const renderError = errors => {
        return (
          <div class="validate-notification-container flow-validate-notification">
            {Object.keys(errors).map(key => {
              return (
                <div class="validate-error" onClick={() => this.selectCellTab(errors[key])} data-index={key}>
                  <span>
                    {errors[key].map(e => {
                      return <div>{e.message}</div>;
                    })}
                  </span>
                </div>
              );
            })}
          </div>
        );
      };
      const shapeName = shapeMapConfig[shape];
      this.$notification.error({
        class: 'design-notification',
        placement: 'bottomLeft',
        key: 'validateNotifyCell',
        message: `${shapeName}错误信息`,
        description: renderError(errors),
        duration: null
      });
    },
    // 设置选中元素
    setSelectCellById(data, shape) {
      this.$refs.configurationPanel.tabKey = 'cellProperty';
      this.graphItem.resetSelection(data.id, { save: true });
      this.$nextTick(() => {
        this.curCellConfigurationVm.validate(({ valid, error, data }) => {
          if (!valid) {
            this.showCellError(error, data, shape);
          }
        });
      });
    },
    // 设置选中流程属性标签页
    selectPropertyTab(errors) {
      if (errors[0] && errors[0].tabKey) {
        this.$refs.flowProperty.tabKey = errors[0].tabKey;
      }
    },
    // 显示流程属性表单错误
    showPropertyError(errors) {
      this.$refs.configurationPanel.tabKey = 'flowProperty';
      // 渲染流程属性错误
      const renderError = errors => {
        return (
          <div class="validate-notification-container flow-validate-notification">
            {Object.keys(errors).map(key => {
              return (
                <div class="validate-error" onClick={() => this.selectPropertyTab(errors[key])} data-index={key}>
                  <span>
                    {errors[key].map(e => {
                      return <div>{e.message}</div>;
                    })}
                  </span>
                </div>
              );
            })}
          </div>
        );
      };
      this.$notification.error({
        class: 'design-notification',
        placement: 'bottomLeft',
        key: 'validateNotify',
        message: '错误信息',
        description: renderError(errors),
        duration: null
      });
    },
    // 通过规则校验数据
    validateDataByRules(data, rules) {
      const rulesCustom = getCustomRules(rules);
      let valid = true,
        errors = {};
      for (const key in rulesCustom) {
        let needVerify = true;
        const rule = rules[key];
        if (rule.condition) {
          needVerify = rule.condition(data);
        }
        if (needVerify) {
          const ruleCustom = rulesCustom[key];
          ruleCustom.validator(rule, data[key], cb => {
            if (cb) {
              errors[key] = rule;
            }
          });
        }
      }
      if (Object.keys(errors).length) {
        valid = false;
      }
      return { valid, errors, data };
    },
    // 检查流程
    checkWorkFlow(cells) {
      let pass = true,
        tasks = [], // 环节数据
        directions = [], // 流向数据
        gateways = [], // 判断节点数据
        directionFromIds = [], // 流向来源id
        directionToIds = [], // 流向目标id
        nodeStart = [], // 开始节点
        nodeEnd = [], // 结束节点
        edgeFromStart = [], // 边的来源是开始id
        edgeToStart = [], // 边的目标是开始id
        edgeFromEnd = [], // 边的来源是结束id
        edgeToEnd = [], // 边的目标是结束id
        edgeStartToEnd = [], // 边是开始指向结束
        edgeEndToStart = [], // 边是结束指向开始
        edgeStartToSubflow = [], // 边是开始指向子流程
        edgeStartToCondition = [], // 边是开始指向判断点
        edgeToSubflow = [], // 边指向子流程
        edgeTaskCondition = [], // 边环节指向判断点（source是条件分支起点）
        checkCondition = true;

      const categorys = this.designer.diction.categorys;
      const hasCategory = categorys.find(item => item.uuid === this.workFlow.property.categorySN);
      if (!hasCategory) {
        pass = false;
        this.$message.error('流程分类不存在！');
        return { pass, tasks, directions, gateways };
      }

      // 校验元素表单,一个个显示错误
      for (let index = 0; index < cells.length; index++) {
        const cell = cells[index];
        const cellData = cell.data;
        const shape = cell.shape;
        if (shape === 'NodeCircle') {
          if (cellData.id === constant.StartFlowId) {
            nodeStart.push(cell);
          }
          if (cellData.id === constant.EndFlowId) {
            nodeEnd.push(cell);
          }
        } else if (shape === 'NodeTask' || shape === 'NodeSubflow' || shape === 'NodeRobot' || shape === 'NodeCollab') {
          if (shape !== 'NodeRobot') {
            let rules = taskRules;
            if (shape === 'NodeSubflow') {
              rules = subflowRules;
            }

            const { valid } = this.validateDataByRules(cellData, rules);
            if (!valid) {
              pass = false;
              this.setSelectCellById(cellData, shape);
              break;
            }
            // 环节权限格式化，只保存开启显示的按钮
            for (const key in flowTaskRights) {
              let rights = [];
              cellData[key].forEach(item => {
                if (!item.uuid && this.frameworkUtil) {
                  item.uuid = item.value + '_' + cellData.id + '_' + this.frameworkUtil.generateId(4).toUpperCase();
                }
                if (item.defaultVisible) {
                  rights.push({
                    type: 32,
                    value: item.value,
                    argValue: null,
                    title: item.title,
                    name: item.name,
                    uuid: item.uuid,
                    i18n: item.i18n
                  });
                }
              });
              cellData[key] = rights;
            }
          }
          tasks.push(cellData);
        } else if (shape === 'EdgeDirection') {
          let needVerify = true;
          if (cellData.type === '3') {
            if (cellData.fromID === constant.StartFlowId) {
              edgeStartToCondition.push(cell);
            }
          }
          if (cellData.fromID === constant.StartFlowId) {
            edgeFromStart.push(cell);
          }
          if (cellData.toID === constant.StartFlowId) {
            edgeToStart.push(cell);
          }
          if (cellData.fromID === constant.EndFlowId) {
            edgeFromEnd.push(cell);
          }
          if (cellData.toID === constant.EndFlowId) {
            edgeToEnd.push(cell);
          }
          if (cellData.fromID === constant.StartFlowId && cellData.toID === constant.EndFlowId) {
            edgeStartToEnd.push(cell);
          }
          if (cellData.fromID === constant.EndFlowId && cellData.toID === constant.StartFlowId) {
            edgeEndToStart.push(cell);
          }
          if (cellData.toID.indexOf('S') === 0 && cellData.toID !== constant.StartFlowId) {
            // 开始节点id也有'S'
            edgeToSubflow.push(cell);
          }
          if (cellData.fromID === constant.StartFlowId && cellData.toID.indexOf('S') === 0 && cellData.toID !== constant.StartFlowId) {
            // 开始节点id也有'S'
            edgeStartToSubflow.push(cell);
          }
          if (cellData.name === '') {
            const edge = this.graphItem.getCellById(cell.id);
            const sourceCell = edge.getSourceCell();
            const targetCell = edge.getTargetCell();
            if (sourceCell.shape === 'NodeTask' && targetCell.shape === 'NodeCondition') {
              const outgoings = this.graphItem.getOutgoingEdges(sourceCell.id);
              if (outgoings && outgoings.length > 1) {
                edgeTaskCondition = outgoings;
                break;
              }
              needVerify = false;
            }
          }
          if (needVerify) {
            const { valid } = this.validateDataByRules(cellData, directionRules);
            if (!valid) {
              pass = false;
              this.setSelectCellById(cellData, shape);
              break;
            }
          }

          directionFromIds.push(cellData.fromID);
          directionToIds.push(cellData.toID);
          directions.push(cellData);
        } else if (shape === 'NodeCondition') {
          // const incomings = this.graphItem.getIncomingEdges(cell.id);
          // if (incomings && incomings.length === 1) {
          //   const sourceCell = this.graphItem.getCellById(incomings[0].source.cell);
          //   if (
          //     sourceCell.shape === 'NodeTask' ||
          //     sourceCell.shape === 'NodeSubflow' ||
          //     sourceCell.shape === 'NodeCondition' ||
          //     sourceCell.shape === 'NodeRobot' ||
          //     sourceCell.shape === 'NodeCollab'
          //   ) {
          //     checkCondition = true;
          //   } else {
          //     checkCondition = false;
          //     break;
          //   }
          // } else {
          //   checkCondition = false;
          //   break;
          // }
          const { valid } = this.validateDataByRules(cellData, conditionRules);
          if (!valid) {
            pass = false;
            this.setSelectCellById(cellData, shape);
            break;
          }
          gateways.push(cellData);
        } else if (shape === 'NodeSwimlane') {
          const { valid } = this.validateDataByRules(cellData, swimlaneRules);
          if (!valid) {
            pass = false;
            this.setSelectCellById(cellData, shape);
            break;
          }
        }
      }

      if (pass && this.workFlow.property.isFree !== '1') {
        // if (!checkCondition) {
        //   pass = false;
        //   // 判断点必须有名称，且只能有一个指向它的流向，而且这个流向必须从环节点或者子流程节点出发
        //   this.$message.error('判断点必须有名称，且只能有一个指向它的流向');
        // } else
        if (edgeTaskCondition.length > 1) {
          pass = false;
          this.$message.error('当环节为条件分支起始点时不能有多个流向指向其他节点！');
        }
      }

      if (!pass) {
        return { pass, tasks, directions, gateways };
      }

      let taskNotOut = [],
        taskNotIn = [];
      tasks.forEach(item => {
        if (!directionFromIds.includes(item.id)) {
          if (item.type !== '4') {
            taskNotOut.push(item);
          }
        }
        if (!directionToIds.includes(item.id)) {
          taskNotIn.push(item);
        }
      });

      let edgeToNodeEndIdMap = {}; // key是结束节点id  value.length > 1时有多个流向指向同一个结束节点
      let edgeMultipleToNodeEndId = ''; // 多个流向指向的结束节点id，不是constant.EndFlowId，是x6的
      for (let index = 0; index < edgeToEnd.length; index++) {
        const item = edgeToEnd[index];
        const id = item.target.cell || item.target.id;
        if (id) {
          if (!edgeToNodeEndIdMap[id]) {
            edgeToNodeEndIdMap[id] = [];
          }
          edgeToNodeEndIdMap[id].push(item);
        }
      }
      for (const id in edgeToNodeEndIdMap) {
        if (edgeToNodeEndIdMap[id].length > 1) {
          edgeMultipleToNodeEndId = id;
          break;
        }
      }
      console.log('edgeToNodeEndIdMap', edgeToNodeEndIdMap);

      let edgeToSubflowIdMap = {};
      let edgeMultipleToSubflowId = ''; // 多个流向指向的子流程id,业务数据id === x6的id
      for (let index = 0; index < edgeToSubflow.length; index++) {
        const item = edgeToSubflow[index];
        const id = item.target.cell || item.target.id;
        if (id) {
          if (!edgeToSubflowIdMap[id]) {
            edgeToSubflowIdMap[id] = [];
          }
          edgeToSubflowIdMap[id].push(item);
        }
      }
      for (const id in edgeToSubflowIdMap) {
        if (edgeToSubflowIdMap[id].length > 1) {
          edgeMultipleToSubflowId = id;
          break;
        }
      }
      console.log('edgeToSubflowIdMap', edgeToSubflowIdMap);

      const { model: graphModel } = this.graphItem.graph;
      const { nodes, incomings, outgoings } = graphModel;
      let nodeNotIn = [],
        nodeNotOut = [],
        nodeMultipleIn = [],
        nodeMultipleOut = [];
      for (const key in nodes) {
        if (!incomings[key]) {
          nodeNotIn.push(key);
        } else if (!outgoings[key]) {
          nodeNotOut.push(key);
        } else if (incomings[key] && incomings[key].length > 1) {
          nodeMultipleIn.push(key);
        } else if (outgoings[key] && outgoings[key].length > 1) {
          nodeMultipleOut.push(key);
        }
      }
      const nodeEndNotIn = nodeNotIn.filter(item => item.indexOf('End') === 0);
      const nodeSubflowNotIn = nodeNotIn.filter(item => item.indexOf('S') === 0 && item.indexOf('Start') !== 0);

      if (pass) {
        if (!nodeStart.length) {
          pass = false;
          this.$message.error('流程必须有开始节点！');
        } else if (nodeStart.length > 1) {
          pass = false;
          this.$message.error('不能创建多个开始节点！');
        } else if (!edgeFromStart.length) {
          pass = false;
          this.$message.error('开始节点没有流向指向目标节点！');
        } else if (edgeFromStart.length > 1) {
          pass = false;
          this.$message.error('开始节点的流向不可能同时指向两个或者更多的目标节点！');
        } else if (edgeToStart.length) {
          pass = false;
          this.$message.error('开始节点不能作为流向的目标节点！');
        } else if (edgeStartToEnd.length || edgeStartToSubflow.length || edgeStartToCondition.length) {
          pass = false;
          this.$message.error('开始节点不能立即指向结束节点、子流程节点或者判断点！');
        } else if (!nodeEnd.length) {
          pass = false;
          this.$message.error('流程必须有结束节点！');
        } else if (edgeFromEnd.length) {
          pass = false;
          this.$message.error('流程的结束节点存在指向其他节点的流向！');
        } else if (!edgeToEnd.length) {
          pass = false;
          this.$message.error('没有流向指向结束节点！');
        } else if (edgeEndToStart.length) {
          pass = false;
          this.$message.error('结束节点不能立即指向开始节点！');
        } else if (edgeMultipleToNodeEndId || edgeMultipleToSubflowId || nodeEndNotIn.length || nodeSubflowNotIn.length) {
          pass = false;
          this.$message.error('流程的结束节点或者子流程节点有且仅有一个来源流向！');
        }
      }

      if (pass) {
        if (this.workFlow.property.isFree === '1') {
          // 自由流程
          if (edgeFromStart.length && edgeToEnd.length) {
            pass = true;
          } else {
            pass = false;
          }
        } else {
          if (taskNotIn.length) {
            pass = false;
            this.$message.error('流程节点必须有流向指向！');
          } else if (taskNotOut.length) {
            pass = false;
            this.$message.error('流程节点必须有指向其他节点的流向！');
          }
        }
      }

      return { pass, tasks, directions, gateways };
    },
    // 检查可用业务组织
    checkUseBizOrg(data) {
      let valid = true,
        isChange = false;

      return new Promise((resolve, reject) => {
        if (this.designer.isNewFlow) {
          resolve(valid);
        } else {
          const property = this.workFlowData.property;
          if (data.availableBizOrg !== property.availableBizOrg) {
            isChange = true;
          }
          if (!isChange) {
            if (data.bizOrgId !== property.bizOrgId) {
              isChange = true;
            }
          }
          if (isChange) {
            this.api.isExistsUnfinishedFlowInstanceByFlowDefUuid({ flowDefUuid: this.uuid }).then(res => {
              if (res) {
                valid = false;
                this.$message.warn('流程的可用业务组织已变更');
              }
              resolve(valid);
            });
          } else {
            resolve(valid);
          }
        }
      });
    },
    checkSubflowBusinessType() {
      let valid = true;
      if (this.designer.subflowChangedBusinessType.length) {
        valid = false;
        this.$message.error('分发组织变更，请更新子流程的分发粒度');
      }
      return valid;
    },
    // 保存流程配置
    saveDefinition(pbNew = false) {
      this.$notification.close('validateNotify');
      this.$notification.close('validateNotifyCell');
      this.$loading('保存中');
      this.pageContext.emitEvent('collectFlowProperty', ({ valid, error, data }) => {
        if (valid) {
          if (!this.checkSubflowBusinessType()) {
            this.$loading(false);
            return;
          }
          const workFlowAdd = propertyAddKey.slice(0, 7);
          for (const key in data) {
            if (workFlowAdd.includes(key)) {
              this.workFlow[key] = data[key];
            }
          }
          this.graphItem.setEdgesLablesByName().then(() => {
            const graphJson = this.graphItem.toJSON();
            const { cells } = graphJson;
            const { pass, tasks, directions, gateways } = this.checkWorkFlow(JSON.parse(JSON.stringify(cells)));
            if (!pass) {
              this.$loading(false);
              return;
            }

            this.checkUseBizOrg(data).then(check => {
              if (!check) {
                // this.$loading(false);
                // return;
              }

              this.workFlow.tasks = tasks;
              this.workFlow.directions = directions;
              this.workFlow.gateways = gateways;
              this.workFlow.graphData = JSON.stringify({
                property: this.workFlow.property,
                cells,
                timers: this.workFlow.timers
              });
              if (this.needUpgrade) {
                pbNew = true;
              }
              if (this.workFlow.version === '') {
                this.workFlow.version = '1.0';
              } else {
                if (pbNew) {
                  let lsVersion = this.workFlow.lastVer;
                  if (!lsVersion) {
                    lsVersion = '1.0';
                  }
                  lsVersion = lsVersion - 0 + 0.111 + '';
                  lsVersion = lsVersion.substring(0, 3);
                  this.workFlow.version = lsVersion;
                  this.workFlow.uuid = '';
                }
              }
              this.workFlow.i18n = this.getJsonI18ns();
              console.log(this.workFlow, graphJson);

              let params, config;
              if (this.designer.flowDefinition.saveEncoder === 'base64') {
                const jsonStr = JSON.stringify(this.workFlow);
                // const bytes = new TextEncoder().encode(jsonStr);
                // params = btoa(String.fromCharCode.apply(null, bytes));

                params = btoa(encodeURIComponent(jsonStr));

                // params = encodeURIComponent(jsonStr);
                config = {
                  headers: {
                    'Content-Type': 'text/plain'
                  }
                };
              } else {
                params = {
                  ...this.workFlow
                };
                config = {
                  headers: {
                    'Content-Type': 'application/json'
                  }
                };
              }
              this.$axios.post(`/proxy/api/workflow/scheme/json/save.action?pbNew=${pbNew}`, params, config).then(
                res => {
                  if (res.status === 200) {
                    const { data } = res;
                    const uuid = data.uuid;
                    this.workFlow.uuid = uuid;
                    this.workFlow.version = data.version;
                    this.workFlow.property.version = data.version;
                    this.workFlowData = JSON.parse(JSON.stringify(data));
                    this.designer.isNewFlow = false;
                    this.needUpgrade = false;
                    this.graphItem.resetNewNode();
                    history.pushState({}, '流程设计', `${location.pathname}?uuid=${uuid}`);
                    this.$message.success('保存成功');
                  } else {
                    this.$message.error(res.message);
                  }
                  this.$loading(false);
                },
                err => {
                  let msg = '出错了';
                  if (err.response) {
                    if (err.response.data) {
                      if (typeof err.response.data === 'string') {
                        msg = err.response.data;
                      } else {
                        msg = err.response.data.msg || err.response.data.message;
                      }
                    }
                    msg = `${err.response.status}:${msg}`;
                  } else if (err.message) {
                    msg = err.message;
                  } else {
                    console.log(err.toJSON());
                  }

                  this.$message.error(msg);
                  this.$loading(false);
                }
              );
            });
          });
        } else {
          this.showPropertyError(error);
          this.$loading(false);
        }
      });
    },

    getJsonI18ns(config = this.workFlow) {
      delete config.i18n;
      let i18ns = {};
      function findI18nObjects(json) {
        function traverse(obj) {
          if (typeof obj !== 'object' || obj === null) return;
          if (Array.isArray(obj)) {
            for (let item of obj) {
              traverse(item);
            }
          } else {
            for (let key in obj) {
              if (key === 'i18n' && obj[key] != undefined && obj[key]['zh_CN'] != undefined) {
                let i18n = obj[key];
                for (let langCode in i18n) {
                  if (i18ns[langCode] == undefined) {
                    i18ns[langCode] = {};
                  }
                  for (let c in i18n[langCode]) {
                    // set(i18ns[langCode], c, i18n[langCode][c]);
                    i18ns[langCode][c] = i18n[langCode][c];
                  }
                }
              } else {
                traverse(obj[key]);
              }
            }
          }
        }
        traverse(json);
      }
      findI18nObjects(config);
      // console.log('获取到 i18ns ', i18ns);
      return i18ns;
    },
    //检查流程更新 原接口 api/workflow/scheme/checkFlowXmlForUpdate
    handleCheckWorkFlow() {
      this.$notification.close('validateNotify');
      this.$notification.close('validateNotifyCell');
      this.pageContext.emitEvent('collectFlowProperty', ({ valid, error, data }) => {
        if (valid) {
          const graphJson = this.graphItem.toJSON();
          const { cells } = graphJson;
          const { pass } = this.checkWorkFlow(JSON.parse(JSON.stringify(cells)));
          if (pass) {
            this.$message.success('流程设计检查通过！');
          }
        }
      });
    },
    // 保存新版本
    saveAsNewVersion() {
      this.saveDefinition(true);
    }
  }
};
</script>
