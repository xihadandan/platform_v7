<template>
  <div class="widget-design-select-sider">
    <a-icon
      type="double-left"
      :class="['left-collapse', collapse ? 'collapsed' : '']"
      @click.stop="onClickCollapse"
      :title="collapse ? '点击展开' : '点击收缩'"
    />
    <a-tabs
      ref="tab"
      v-model="activeKey"
      tab-position="left"
      size="small"
      class="widget-select-tabs"
      defaultActiveKey="process"
      @tabClick="onTabClick"
    >
      <a-tab-pane key="process">
        <span slot="tab">
          <Icon type="iconfont icon-a-icleftbuju"></Icon>
          <br />
          业务流
        </span>
        <a-tabs
          v-model="processActiveKey"
          default-active-key="2"
          class="radio-group-style one-tab"
          style="--w-tabs-radio-tab-bar-padding: 20px 12px 0"
        >
          <a-tab-pane key="1" tab="布局" v-if="defaultExpandedKeys.length == 0">
            <a-collapse :bordered="false" expandIconPosition="right" :activeKey="[1, 2]">
              <a-collapse-panel key="1">
                <template slot="header">
                  <label class="">横向快速布局</label>
                </template>
                <ul class="widget-select-item-ul">
                  <li
                    v-for="(select, index) in designNodes.horizontal"
                    :key="index"
                    class="widget-select-item"
                    style="cursor: pointer"
                    :title="select.name"
                    @click="onDesignNodeClick(select)"
                  >
                    <template v-if="select.iconClass">
                      <i :class="['iconfont', select.iconClass]"></i>
                    </template>
                    <template v-else>
                      <a-icon type="control" />
                    </template>
                    {{ select.name }}
                  </li>
                </ul>
              </a-collapse-panel>
              <a-collapse-panel key="2">
                <template slot="header">
                  <label class="">纵向快速布局</label>
                </template>
                <ul class="widget-select-item-ul">
                  <li
                    v-for="(select, index) in designNodes.vertical"
                    :key="index"
                    class="widget-select-item"
                    style="cursor: pointer"
                    :title="select.name"
                    @click="onDesignNodeClick(select)"
                  >
                    <template v-if="select.iconClass">
                      <i :class="['iconfont', select.iconClass]"></i>
                    </template>
                    <template v-else>
                      <a-icon type="control" />
                    </template>
                    {{ select.name }}
                  </li>
                </ul>
              </a-collapse-panel>
            </a-collapse>
          </a-tab-pane>
          <a-tab-pane key="2" tab="结构">
            <PerfectScrollbar style="height: calc(100vh - 90px)">
              <a-tree
                v-if="defaultExpandedKeys.length > 0"
                :defaultExpandedKeys="defaultExpandedKeys"
                :tree-data="designer.processTree.treeData"
                :selectable="true"
                :showLine="false"
                :blockNode="true"
                class="process-tree ant-tree-directory"
                draggable
                @select="selectTreeNode"
                @drop="onTreeDrop"
              >
                <template slot="title" slot-scope="node">
                  <!-- <a-icon v-if="['node-group', 'node'].includes(node.dataRef && node.dataRef.data && node.dataRef.data.type)" type="folder" />
                <a-icon v-else type="file"></a-icon> -->
                  <span v-if="node.data && node.data.type == 'node-group'" class="process-tree-icon process-tree-group">
                    <Icon type="pticon iconfont icon-ptkj-liuchengguanli"></Icon>
                  </span>
                  <span v-else-if="node.data && node.data.type == 'node'" class="process-tree-icon process-tree-node">
                    <Icon type="pticon iconfont icon-ptkj-zuzhiguanli"></Icon>
                  </span>
                  <span v-else-if="node.data && node.data.type == 'item'" class="process-tree-icon process-tree-item">
                    <Icon type="pticon iconfont icon-oa-chulishixiang"></Icon>
                  </span>
                  <span class="title" :class="{ 'has-operate': isTemplateTreeNode(node) || isRefTreeNode(node) }" :title="node.title">
                    {{ node.title }}
                    <template v-if="isTemplateTreeNode(node)">(模板)</template>
                    <template v-else-if="isRefTreeNode(node)">(引用)</template>
                  </span>
                  <template v-if="isTemplateTreeNode(node)">
                    <div :style="{ float: 'right' }">
                      <a-dropdown>
                        <i class="iconfont icon-ptkj-gengduocaozuo icon-operate" style="margin-right: 4px"></i>
                        <a-menu slot="overlay">
                          <a-menu-item @click="onViewRefInfoClick(node)">查看被引用信息</a-menu-item>
                        </a-menu>
                      </a-dropdown>
                    </div>
                  </template>
                  <template v-else-if="isRefTreeNode(node)">
                    <div :style="{ float: 'right' }">
                      <a-dropdown>
                        <i class="iconfont icon-ptkj-gengduocaozuo icon-operate" style="margin-right: 4px"></i>
                        <a-menu slot="overlay">
                          <a-menu-item @click="onViewTemplateClick(node)">查看模板</a-menu-item>
                          <a-menu-item @click="onCancelRefClick(node)">取消引用</a-menu-item>
                        </a-menu>
                      </a-dropdown>
                    </div>
                  </template>
                </template>
              </a-tree>
            </PerfectScrollbar>
          </a-tab-pane>
        </a-tabs>
      </a-tab-pane>
      <!-- <a-tab-pane key="2">
        <span slot="tab">
          <i class="iconfont icon-a-icleftzujian"></i>
          <br />
          结构
        </span>
      </a-tab-pane> -->
      <a-tab-pane key="itemFlow" class="item-flow">
        <span slot="tab">
          <Icon type="iconfont icon-a-icleftzujian"></Icon>
          <br />
          事项流
        </span>
        <a-collapse :bordered="false" expandIconPosition="right" :activeKey="[1, 2]">
          <a-collapse-panel key="1">
            <template slot="header">
              <label class="">节点</label>
            </template>
            <a-row class="item-flow-nodes">
              <a-col span="8">
                <li :draggable="existsItemFlow" @dragstart="onDragstart" @dragend="onDragend($event, { type: 'start' })">
                  <div class="start-node"><Icon type="pticon iconfont icon-jiedian-kaishi-01" title="开始"></Icon></div>
                </li>
              </a-col>
              <a-col span="8">
                <li :draggable="existsItemFlow" @dragstart="onDragstart" @dragend="onDragend($event, { type: 'gateway' })">
                  <div class="gateway">
                    <Icon type="pticon iconfont icon-jiedian-wangguan-01" title="网关"></Icon>
                  </div>
                </li>
              </a-col>
              <a-col span="8">
                <li :draggable="existsItemFlow" @dragstart="onDragstart" @dragend="onDragend($event, { type: 'end' })">
                  <div class="end-node"><Icon type="pticon iconfont icon-jiedian-jieshu-01" title="结束"></Icon></div>
                </li>
              </a-col>
            </a-row>
          </a-collapse-panel>
          <a-collapse-panel key="2">
            <template slot="header">
              <label class="">事项流</label>
            </template>
            <PerfectScrollbar style="height: calc(100vh - 220px); margin-right: -20px; padding-right: 20px">
              <draggable
                class="item-flow-items widget-select-item-ul"
                tag="ul"
                filter=".disabled"
                :list="itemFlows"
                @start="onDragstart"
                @end="onDragend($event, { type: 'item', configuration: itemFlows[$event.newIndex].data.configuration || {} })"
                handle=".item-flow-item"
                :sort="false"
              >
                <li
                  v-for="(select, index) in itemFlows"
                  :key="index"
                  class="widget-select-item item-flow-item"
                  :class="{
                    disabled: !select.draggable,
                    selected: itemFlowDesigner.currentItemFlow && itemFlowDesigner.currentItemFlow.itemId == select.key
                  }"
                  :title="select.title"
                  @click="onItemFlowClick(select)"
                >
                  <template v-if="select.iconClass">
                    <Icon :type="select.iconClass"></Icon>
                  </template>
                  <template v-else>
                    <Icon type="pticon iconfont icon-ptkj-xiangmuguanli" />
                  </template>
                  {{ select.title }}
                </li>
              </draggable>
              <a-empty
                v-if="itemFlows.length == 0"
                description="暂无事项流数据！"
                class="pt-empty pt-data-empty"
                style="margin-top: 50px"
              ></a-empty>
            </PerfectScrollbar>
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>
      <a-tab-pane key="workflow" class="workflow">
        <span slot="tab">
          <Icon type="iconfont icon-a-icleftzujian"></Icon>
          <br />
          工作流
        </span>
        <a-collapse :bordered="false" expandIconPosition="right" :activeKey="[1]">
          <a-collapse-panel key="1">
            <template slot="header">
              <label class="">工作流</label>
            </template>
            <a-menu @click="onWorkflowItemMenuClick">
              <a-menu-item v-for="item in workflowIntegrationItems" :key="item.id">
                {{ item.itemName }}
              </a-menu-item>
            </a-menu>
            <a-empty
              v-if="workflowIntegrationItems.length == 0"
              description="暂无业务事项集成工作流数据！"
              class="pt-empty pt-data-empty"
              style="margin-top: 50px"
            ></a-empty>
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>
    </a-tabs>
    <a-modal v-model="modalVisible" title="创建业务阶段" @ok="handleCreateCustomNodeTemplateOk">
      <a-form-model ref="ruleForm" :model="formData" :rules="rules" :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
        <a-form-model-item label="布局起始类型" prop="layout">
          <a-radio-group v-model="formData.layout" :default-value="defaultLayout">
            <a-radio value="horizontal">横向</a-radio>
            <a-radio value="vertical">纵向</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="数量" prop="count">
          <a-input-number v-model="formData.count" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
    <a-modal v-model="templateModalVisible" :title="`引用[${viewTemplateNode.title}]的业务流程`">
      <PerfectScrollbar style="height: 300px">
        <a-list item-layout="horizontal" :data-source="definitionsOfRefTemplate">
          <a-list-item slot="renderItem" slot-scope="item">
            <a-button type="link" slot="actions" @click="onViewRefTemplateClick(item)">查看</a-button>
            <div>{{ item.name }}({{ item.version && (item.version + '').includes('.') ? item.version : item.version + '.0' }})</div>
          </a-list-item>
        </a-list>
      </PerfectScrollbar>
      <template slot="footer">
        <a-button v-if="!definitionsOfRefTemplate.length" type="danger" @click="onDeleteTemplateClick(viewTemplateNode)">删除模板</a-button>
      </template>
    </a-modal>
  </div>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import { createNodes } from '../designer/utils';
import draggable from 'vuedraggable';
export default {
  name: 'ProcessSelectPanel',
  props: {
    designerType: String,
    designer: Object,
    designNodes: Object,
    itemFlowDesigner: Object
  },
  inject: ['pageContext'],
  components: { draggable },
  data() {
    let activeKey = 'processs';
    if (this.designerType == 'itemFlow') {
      activeKey = 'itemFlow';
    }
    return {
      activeKey,
      processActiveKey: '2',
      collapse: false,
      modalVisible: false,
      formData: {
        layout: 'horizontal',
        count: 3
      },
      defaultLayout: 'horizontal',
      templateModalVisible: false,
      viewTemplateNode: { title: '' },
      rules: {
        count: [
          { required: true, message: '请输入数量', trigger: 'blur' },
          {
            validator(rule, value, callback) {
              let count = parseInt(value);
              return count >= 1 && count <= 10;
            },
            message: '请输入1~10的数字',
            trigger: 'blur'
          }
        ]
      },
      itemFlows: [],
      definitionsOfRefTemplate: []
    };
  },
  computed: {
    defaultExpandedKeys() {
      let treeData = this.designer.processTree.treeData;
      return treeData.length > 0 ? [treeData[0].key] : [];
    },
    existsItemFlow() {
      return this.itemFlowDesigner.currentItemFlow != null;
    },
    workflowIntegrationItems() {
      let processItems = this.designer.processTree
        .getTreeDataList('item')
        .filter(item => {
          let configuration = item.data && item.data.configuration;
          if (!configuration) {
            return false;
          }
          let businessIntegrationConfigs = configuration.businessIntegrationConfigs || [];
          for (let index = 0; index < businessIntegrationConfigs.length; index++) {
            let businessIntegrationConfig = businessIntegrationConfigs[index];
            if (businessIntegrationConfig.enabled && businessIntegrationConfig.type == '1') {
              return true;
            }
          }
          return false;
        })
        .map(item => item.data.configuration);

      let itemFlows = this.itemFlows || [];
      let itemFlowItems = [];
      itemFlows.forEach(item => {
        let itemFlow = this.itemFlowDesigner.itemFlows[item.key];
        if (!itemFlow || !itemFlow.graphData || !itemFlow.graphData.cells) {
          return;
        }
        let cellItems = itemFlow.graphData.cells
          .filter(cell => {
            if (cell.shape != 'item-flow-node') {
              return false;
            }
            let configuration = cell.data && cell.data.configuration;
            if (!configuration) {
              return false;
            }
            let existItem = processItems.find(processItem => processItem.id == configuration.id);
            if (existItem) {
              return false;
            }
            let businessIntegrationConfigs = configuration.businessIntegrationConfigs || [];
            for (let index = 0; index < businessIntegrationConfigs.length; index++) {
              let businessIntegrationConfig = businessIntegrationConfigs[index];
              if (businessIntegrationConfig.enabled && businessIntegrationConfig.type == '1') {
                return true;
              }
            }
            return false;
          })
          .map(item => item.data.configuration);
        itemFlowItems = [...itemFlowItems, ...cellItems];
      });

      return [...processItems, ...itemFlowItems];
    }
  },
  watch: {
    'itemFlowDesigner.currentItemFlow': {
      deep: true,
      handler(newVal, oldVal) {
        console.log('currentItemFlow', newVal);
        let itemFlows = this.itemFlows;
        if (newVal) {
          // 放入事项流中的事项ID列表
          let itemIds = ((newVal.graphData && newVal.graphData.cells) || [])
            .filter(item => item.data && item.data.type == 'item')
            .map(item => item.data.itemId);
          itemFlows.forEach(item => {
            if (item.key == newVal.itemId || itemIds.indexOf(item.key) != -1) {
              item.draggable = false;
            } else {
              item.draggable = true;
            }
          });
        } else {
          itemFlows.forEach(item => (item.draggable = false));
        }
        // console.log('itemFlows', this.itemFlows);
      }
    },
    'designer.processTree.treeData': {
      deep: false,
      handler(newVal) {
        // 更新事项流数据
        this.updateItemFlows();

        // 处理事项流页签时，不自动切换
        if (this.activeKey == 'itemFlow' || this.activeKey == 'workflow') {
          return;
        }
        // 存在业务流程树时，切换到结构tab
        this.activeKey = 'process';
        if (newVal.length > 0) {
          this.processActiveKey = '2';
          // this.$refs.tab.$children[0].onTabClick('2');
        } else {
          this.processActiveKey = '1';
          // this.$refs.tab.$children[0].onTabClick('1');
        }
      }
    },
    'designer.buildWay': function (newVal) {
      if (this.activeKey != newVal) {
        this.activeKey = newVal;
      }
    }
  },
  created() {
    const _this = this;
    _this.initItemFlows();
  },
  mounted() {
    this.defaultSiderStyle = {
      flex: this.$el.parentElement.parentElement.style.flex,
      width: this.$el.parentElement.parentElement.style.width
    };
    setTimeout(() => {
      this.collapseChange();
    }, 0);
    this.pageContext.handleEvent(`processSelectPanel`, data => {
      this.collapse = !data.collapse;
      this.onClickCollapse();
    });
  },
  methods: {
    // 点击左侧tab，默认会打开配置，如果之前它是折叠的，再次点击tab，折叠图标位置会出错
    collapseChange() {
      // 流程设置为空时，默认关闭配置
      if (this.designer.isEmpty) {
        this.collapse = false;
        setTimeout(() => {
          this.onClickCollapse();
        }, 0);
      } else if (this.collapse) {
        this.collapse = false;
      }
    },
    initItemFlows() {
      const _this = this;
      let itemFlows = this.designer.processTree.getTreeDataList('item');
      itemFlows.forEach(item => {
        _this.$set(item, 'draggable', false);
      });
      _this.itemFlows = itemFlows;
    },
    updateItemFlows() {
      const _this = this;
      let itemFlows = this.designer.processTree.getTreeDataList('item');
      itemFlows.forEach(item => {
        let itemFlow = _this.itemFlows.find(itemFlow => itemFlow.key === item.key);
        if (itemFlow != null) {
          _this.$set(item, 'draggable', itemFlow.draggable);
        } else {
          _this.$set(item, 'draggable', this.itemFlowDesigner.currentItemFlow != null);
        }
      });
      _this.itemFlows = itemFlows;
    },
    onTabClick(key) {
      if (key == 'itemFlow') {
        this.designer.setBuildWay('itemFlow');
      } else if (key == 'process') {
        this.designer.setBuildWay('process');
      } else if (key == 'workflow') {
        this.designer.setBuildWay('workflow');
      }
      this.collapse = false;
      this.collapseOrExpand();
      this.closeDrawer();
    },
    closeDrawer() {
      const _this = this;
      if (_this.designer && _this.designer.drawerVisibleKey) {
        _this.pageContext.emitEvent('closeDrawer:' + _this.designer.drawerVisibleKey);
      }
    },
    onWorkflowItemMenuClick({ key }) {
      let itemDefinition = this.workflowIntegrationItems.find(item => item.id == key);
      this.$emit('selectedWorkflowItem', { itemDefinition });
    },
    collapseOrExpand(width) {
      let style = this.$el.parentElement.parentElement.style;
      if (this.collapse) {
        style.flex = '0 0 ' + width + 'px';
        style.width = width + 'px';
        style.minWidth = style.width;
      } else {
        style.flex = this.defaultSiderStyle.flex;
        style.width = this.defaultSiderStyle.width;
        style.minWidth = this.defaultSiderStyle.width;
      }
    },
    onClickCollapse(e) {
      this.collapse = !this.collapse;

      this.collapseOrExpand(
        64 //_target.offsetWidth
      );
    },
    onDesignNodeClick(designNode) {
      const _this = this;
      if (_this.designer.graph.getCellCount() > 0) {
        _this.$message.warning('布局已经存在，不能再次添加！');
        return;
      }

      let count = designNode.count;
      let nodes = [];
      switch (count) {
        case 'one':
          nodes = createNodes(1);
          break;
        case 'two':
          nodes = createNodes(2);
          break;
        case 'three':
          nodes = createNodes(3);
          break;
        case 'four':
          nodes = createNodes(4);
          break;
        case 'custom':
          _this.createCustomNodeData(designNode);
          return;
      }
      let nodeTemplate = Object.assign({ id: 'group_' + generateId(), nodes }, designNode);
      _this.designer.initFromNodeTemplate(nodeTemplate);
      _this.pageContext.emitEvent(`processConfigurationPanel:processConfigRef`, { collapse: false });
      _this.designer.graph.translate(0, 0); // 画布移到初始位置
    },
    onItemFlowClick(itemNode) {
      this.itemFlowDesigner.showItemFlowByItemDefinition(itemNode.data.configuration);
      this.closeDrawer();
    },
    createCustomNodeData(designNode) {
      this.modalVisible = true;
      this.formData.layout = designNode.layout;
    },
    handleCreateCustomNodeTemplateOk() {
      const _this = this;
      _this.$refs.ruleForm.validate(valid => {
        if (valid) {
          let nodes = createNodes(_this.formData.count);
          let nodeTemplate = Object.assign({ id: 'group_' + generateId(), nodes }, _this.formData);
          _this.designer.initFromNodeTemplate(nodeTemplate);
          _this.modalVisible = false;
        }
      });
    },
    isTemplateTreeNode(node) {
      return this.designer.isTemplateNode(node.key, node.data && node.data.type);
    },
    isRefTreeNode(node) {
      return node.data && node.data.configuration && node.data.configuration.refProcessDefUuid;
    },
    onViewRefInfoClick(node) {
      const _this = this;
      _this.definitionsOfRefTemplate = [];
      let refId = node.key;
      let templateType = node.data.type == 'node' ? '60' : '50';
      let processDefUuid = this.designer.processDefinition.uuid;
      $axios
        .get(
          `/proxy/api/biz/process/definition/listOfRefTemplate?refId=${refId}&templateType=${templateType}&processDefUuid=${processDefUuid}`
        )
        .then(({ data: result }) => {
          if (result.data) {
            _this.definitionsOfRefTemplate = result.data;
          } else {
            _this.definitionsOfRefTemplate = [];
          }
        });
      _this.viewTemplateNode = node;
      _this.templateModalVisible = true;
    },
    onViewTemplateClick(node) {
      let refProcessDefUuid = node.data.configuration.refProcessDefUuid;
      let url = `/biz/process/assemble/${refProcessDefUuid}?activeKey=flow`;
      window.open(this.addSystemPrefix(url));
    },
    onDeleteTemplateClick(node) {
      const _this = this;
      let id = node.key;
      let type = node.data.type == 'node' ? '60' : '50';
      let processDefUuid = this.designer.processDefinition.uuid;
      let url = `/proxy/api/biz/definition/template/deleteByIdAndTypeAndProcessDefUuid?id=${id}&type=${type}&processDefUuid=${processDefUuid}`;
      _this.$confirm({
        title: '确认',
        content: `确认删除模板[${node.title}]？`,
        onOk() {
          $axios
            .post(url)
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$message.success('删除成功');
                _this.designer.deleteNodeTemplate(id);
                _this.templateModalVisible = false;
                _this.definitionsOfRefTemplate = [];
              }
            })
            .catch(error => {
              _this.$message.error('删除失败');
            });
        }
      });
    },
    onViewRefTemplateClick(item) {
      let url = `/biz/process/assemble/${item.uuid}?activeKey=flow`;
      window.open(this.addSystemPrefix(url));
    },
    addSystemPrefix(url) {
      const _this = this;
      if (_this._$SYSTEM_ID && url && !url.startsWith('/sys/')) {
        url = `/sys/${_this._$SYSTEM_ID}/_${url}`;
      }
      return url;
    },
    onCancelRefClick(node) {
      let result = this.designer.cancelRefByNodeId(node.key);
      if (result) {
        this.designer.selectNode(node.key);
        this.$message.success('已取消、未保存！');
      } else {
        this.$message.error('取消失败！');
      }
    },
    selectTreeNode(selectedKeys) {
      this.designer.selectNode(selectedKeys[0]);
    },
    onTreeDrop(info) {
      const dragKey = info.dragNode.eventKey;
      const dropKey = info.node.eventKey;
      const dropPos = info.node.pos.split('-');
      const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);
      const dropToBottom = dropPosition == 1;
      // console.log('dropKey', dropKey, dropToBottom);
      if (dragKey == dropKey) {
        return;
      }

      let dragNodeRefInfo = this.designer.getNodeRefInfoById(dragKey);
      if (dragNodeRefInfo.ref && !dragNodeRefInfo.directRef) {
        this.$message.error('引用阶段下的阶段/事项不能拖动！');
        return;
      }
      let dropNodeRefInfo = this.designer.getNodeRefInfoById(dropKey);
      if (dropNodeRefInfo.ref && !dropNodeRefInfo.directRef) {
        this.$message.error('节点不能拖入引用阶段下！');
        return;
      } else if (dropNodeRefInfo.ref) {
        let dragNode = this.designer.graph.getCellById(dragKey);
        if (info.dropToGap && dragNode && dragNode.getData().type == 'node') {
        } else {
          this.$message.error('节点不能拖入引用阶段下！');
          return;
        }
      }

      let itemDataList = this.designer.processTree.getTreeDataList('item');
      let dragData = itemDataList.find(item => item.key == dragKey);
      // 事项节点不能拖入根节点
      if (dragData != null && dropKey == this.designer.processDefinition.id) {
        this.$message.warning('事项节点不能拖入根节点！');
        return;
      }
      // 通过业务流程定义移动节点，目标节点为事项时，作为同级放入事项节点
      this.designer.moveNodeWithProcessDefinition(dragKey, dropKey, info.dropToGap, dropToBottom);
    },
    onDragstart(e) {
      this.itemFlowDesigner.updateDragInfo({ startX: e.offsetX, startY: e.offsetY });
    },
    onDragend(e, nodeOptions = {}) {
      this.itemFlowDesigner.addDragNode({ ...nodeOptions });
    }
  }
};
</script>

<style lang="less" scoped>
.process-tree {
  --w-tree-arrow-color: var(--w-text-color-light);
  // --w-tree-title-offset-width: 0px;
  .icon-operate {
    display: none;
  }
  ::v-deep .ant-tree-node-content-wrapper {
    border: 0;
  }

  ::v-deep .ant-tree-node-content-wrapper:hover .icon-operate {
    display: inline-block;
  }

  .process-tree-icon {
    width: 20px;
    height: 20px;
    display: inline-block;
    text-align: center;
    border-radius: 4px;
    vertical-align: middle;
    line-height: 20px;

    &.process-tree-group {
      background: linear-gradient(180deg, var(--w-primary-color-4) 0%, var(--w-primary-color) 100%);
      color: #ffffff;
    }
    &.process-tree-node {
      background: linear-gradient(180deg, var(--w-warning-color-4) 0%, var(--w-warning-color) 100%);
      color: #ffffff;
    }
    &.process-tree-item {
      background: transparent;
      // background: linear-gradient(180deg, var(--w-info-color-4) 0%, var(--w-info-color) 100%);
      // color: #ffffff;
    }
  }
  .has-operate {
    --w-tree-title-offset-width: 40px;
  }
}
.item-flow {
  .item-flow-nodes {
    margin-bottom: 12px;
    li {
      width: 58px;
      height: 58px;
      display: inline-block;
      border-radius: 4px;
      background: var(--w-gray-color-2);
      color: var(--w-text-color-dark);
      text-align: center;
      line-height: 58px;
      cursor: move;

      .start-node,
      .end-node,
      .gateway {
        height: inherit;
        i {
          font-size: 32px;
        }
      }

      &:hover {
        background: var(--w-primary-color-1);
        color: var(--w-primary-color);
      }
    }
  }

  .item-flow-items {
    .item-flow-item {
      margin-right: 0px !important;
      width: 100% !important;
    }

    .widget-select-item.item-flow-item.disabled {
      background-color: var(--w-gray-color-4);
      color: var(--w-text-color-light);
      i {
        color: var(--w-text-color-light);
      }
    }

    .widget-select-item.item-flow-item.selected {
      outline: 1px solid var(--w-primary-color);
      color: var(--w-primary-color);
      i {
        color: var(--w-primary-color);
      }
    }
  }
}
</style>
