<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model ref="form" :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>
          <a-form-model-item label="JS模块">
            <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetTreeDevelopment" width="205px" />
          </a-form-model-item>
          <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="globalSetting">
            <a-collapse-panel key="globalSetting" header="公共配置">
              <!-- <a-form-model-item label="节点标题宽度">
                <a-input-number v-model="widget.configuration.treeNodeTitleWidth" :min="100" />
              </a-form-model-item> -->
              <a-form-model-item label="可搜索">
                <a-switch v-model="widget.configuration.showSearch" />
              </a-form-model-item>
              <a-form-model-item label="显示节点图标">
                <a-switch v-model="widget.configuration.showIcon" />
              </a-form-model-item>
              <TreeNodeCommonConfiguration :widget="widget" :designer="designer" :columnOptions="columnOptions" />
              <DynamicGroupNameConfiguration
                :widget="widget"
                :designer="designer"
                :button="widget.configuration.button"
              ></DynamicGroupNameConfiguration>
            </a-collapse-panel>
            <a-collapse-panel key="buildSetting" header="构建配置">
              <a-form-model-item label="默认选中">
                <a-select v-model="widget.configuration.defaultSelectType">
                  <a-select-option value="selectFirstNode">默认选中第一个节点</a-select-option>
                  <a-select-option value="selectByKey">按值选中</a-select-option>
                </a-select>
                <a-input v-model="widget.configuration.defaultSelectKey" v-if="widget.configuration.defaultSelectType === 'selectByKey'" />
              </a-form-model-item>
              <a-form-model-item label="构建方式">
                <a-select
                  :style="{ width: '100%' }"
                  :options="buildOptions"
                  v-model="widget.configuration.buildType"
                  @change="onChangeBuildType(true)"
                />
              </a-form-model-item>

              <div v-if="widget.configuration.buildType == 'apiLinkService'">
                <a-form-model-item label="API服务数据">
                  <WidgetDesignDrawer :id="'apiLinkService' + widget.id" title="API 服务数据设置" :designer="designer" :width="875">
                    <a-button type="primary" size="small">设置</a-button>
                    <template slot="content">
                      <ApiOperationDataset
                        :widget="widget"
                        :configuration="widget.configuration"
                        :designer="designer"
                        :api-result-transform-schema="apiResultTransformSchema"
                      >
                        <template slot="dataTransformFunctionPrefix">
                          <a-alert type="info" message="入参 response 为api返回结果, 需要返回以下数据结构样例" style="margin-bottom: 8px">
                            <template slot="description">
                              <pre>
[
  {
    "id": "选项1","name":"选项值1","icon":"图标值","children":[]
  }

  // 更多选项对象
  ...
] </pre
                              >
                              <p>可通过返回 promise 对象, 用于实现异步逻辑</p>
                            </template>
                          </a-alert>
                        </template>
                      </ApiOperationDataset>
                    </template>
                  </WidgetDesignDrawer>
                </a-form-model-item>
                <BadgeConfiguration :designer="designer" :widget="widget" :configuration="widget.configuration" conditionTip>
                  <template slot="defaultCondition">
                    <div>
                      <label style="font-weight: bold; line-height: 32px">二、支持节点数据</label>
                      <ol>
                        <li v-for="(item, i) in defaultConditionVar" style="margin-bottom: 8px">
                          <a-tag class="primary-color">{{ item.value }}</a-tag>
                          : {{ item.label }}
                        </li>
                      </ol>
                      <p>
                        例如: SQL 编写为
                        <a-tag>id = '${treeNode.key}'</a-tag>
                      </p>
                    </div>
                  </template>
                </BadgeConfiguration>
              </div>
              <div v-show="widget.configuration.buildType == 'dataModel' || widget.configuration.buildType == 'dataSource'">
                <a-form-model-item label="数据源" v-if="widget.configuration.buildType == 'dataSource'">
                  <DataStoreSelectModal
                    v-model="widget.configuration.dataSourceId"
                    :displayModal="true"
                    @change="(value, opt) => changeDataSourceId(value, opt, true)"
                  />
                </a-form-model-item>
                <a-form-model-item label="数据模型" v-if="widget.configuration.buildType == 'dataModel'">
                  <DataModelSelectModal
                    v-model="widget.configuration.dataModelUuid"
                    ref="dataModelSelect"
                    :dtype="['TABLE', 'VIEW']"
                    @change="onChangeDataModelSelect(true)"
                  />
                </a-form-model-item>
                <a-form-model-item label="默认条件">
                  <a-textarea v-model="widget.configuration.defaultCondition" :auto-size="{ minRows: 2, maxRows: 6 }" />
                </a-form-model-item>
                <a-form-model-item label="排序字段">
                  <a-select
                    v-model="widget.configuration.sortField"
                    :options="columnOptions"
                    show-search
                    placeholder="请选择树节点排序的字段"
                    style="width: 100%"
                    :filter-option="filterSelectOption"
                    allow-clear
                  />
                </a-form-model-item>
                <a-form-model-item label="排序方式">
                  <a-radio-group v-model="widget.configuration.sortType" button-style="solid" default-value="asc" size="small">
                    <a-radio-button value="asc">升序</a-radio-button>
                    <a-radio-button value="desc">降序</a-radio-button>
                  </a-radio-group>
                </a-form-model-item>

                <!-- 通过以下字段配置构建整个树 -->
                <a-form-model-item label="标题字段">
                  <a-select
                    v-model="widget.configuration.titleField"
                    :options="columnOptions"
                    show-search
                    placeholder="请选择展示为树节点标题的字段"
                    style="width: 100%"
                    :filter-option="filterSelectOption"
                  />
                </a-form-model-item>
                <a-form-model-item label="数据值字段">
                  <a-select
                    v-model="widget.configuration.valueField"
                    :options="columnOptions"
                    show-search
                    placeholder="请选择展示为树节点数据值的字段"
                    style="width: 100%"
                    :filter-option="filterSelectOption"
                  />
                </a-form-model-item>
                <a-form-model-item label="父级字段">
                  <a-select
                    v-model="widget.configuration.parentField"
                    :options="columnOptions"
                    show-search
                    placeholder="请选择树节点父级数据值的字段"
                    style="width: 100%"
                    :filter-option="filterSelectOption"
                    allow-clear
                  />
                </a-form-model-item>
                <a-form-model-item label="图标字段">
                  <a-select
                    v-model="widget.configuration.iconField"
                    :options="columnOptions"
                    show-search
                    placeholder="请选择树节点图标的字段"
                    style="width: 100%"
                    :filter-option="filterSelectOption"
                    allow-clear
                  />
                </a-form-model-item>
                <BadgeConfiguration :designer="designer" :widget="widget" :configuration="widget.configuration" conditionTip>
                  <template slot="defaultCondition">
                    <div>
                      <label style="font-weight: bold; line-height: 32px">二、支持节点数据</label>
                      <ol>
                        <li v-for="(item, i) in defaultConditionVar" style="margin-bottom: 8px">
                          <a-tag class="primary-color">{{ item.value }}</a-tag>
                          : {{ item.label }}
                        </li>
                      </ol>
                      <p>
                        例如: SQL 编写为
                        <a-tag>id = '${treeNode.key}'</a-tag>
                      </p>
                    </div>
                  </template>
                </BadgeConfiguration>
              </div>

              <div v-show="widget.configuration.buildType === 'define'" :style="{ margin: '0px 5px 12px' }">
                <a-card size="small" title="构建树区域">
                  <a slot="extra" href="#">
                    <WidgetDesignDrawer id="treeNodeMoreConfig_newTreeNode" title="设置" :designer="designer">
                      <a-button type="link" icon="plus">新增节点</a-button>
                      <template slot="content">
                        <TreeNodeConfiguration :widget="widget" :designer="designer" :node="newNode" />
                      </template>
                      <template slot="footer">
                        <a-button size="small" type="primary" @click.stop="onConfirmAddNewNode(-1)">确定</a-button>
                      </template>
                    </WidgetDesignDrawer>
                    <WidgetDesignDrawer id="treeNodeMoreConfig_globalSet" title="设置" :designer="designer">
                      <template slot="content">
                        <TreeNodeConfiguration :widget="widget" :designer="designer" :node="newNode" />
                      </template>
                      <template slot="footer">
                        <a-button size="small" type="primary" @click.stop="onConfirmAddNewNode(-1)">确定</a-button>
                      </template>
                    </WidgetDesignDrawer>
                  </a>
                  <a-empty v-show="widget.configuration.treeNodes.length == 0" />
                  <a-tree
                    class="ant-tree-directory tree-more-operations"
                    :tree-data="widget.configuration.treeNodes"
                    show-icon
                    default-expand-all
                    :expandedKeys.sync="expandedTreeKeys"
                  >
                    <a-icon slot="switcherIcon" type="down" />
                    <template slot="iconSlot" slot-scope="scope">
                      <Icon :type="scope.dataRef.icon" :size="20" />
                    </template>
                    <template slot="titleSlot" slot-scope="scope">
                      <span v-if="scope.dynamic">
                        动态节点
                        <a-icon type="api" />
                      </span>
                      <span
                        v-else
                        :style="{
                          overflow: 'hidden',
                          whiteSpace: 'nowrap',
                          textOverflow: 'ellipsis',
                          display: 'inline-block',
                          verticalAlign: 'middle',
                          width: '140px'
                        }"
                        :title="scope.title"
                      >
                        {{ scope.title }}
                      </span>
                      <a-dropdown>
                        <a-menu slot="overlay" @click="e => onClickTreeNodeMenu(e, scope)">
                          <a-menu-item key="add" v-show="!scope.dataRef.dynamic">
                            <WidgetDesignDrawer :id="'treeNodeMoreConfig' + scope.dataRef.key" title="设置" :designer="designer">
                              <a-button size="small" type="link">新增子节点</a-button>
                              <template slot="content">
                                <TreeNodeConfiguration :widget="widget" :designer="designer" :node="newNode" />
                              </template>
                              <template slot="footer">
                                <a-button size="small" type="primary" @click.stop="onConfirmAddNewNode(scope.dataRef)">确定</a-button>
                              </template>
                            </WidgetDesignDrawer>
                          </a-menu-item>
                          <a-menu-item key="edit">
                            <WidgetDesignDrawer :id="'treeNodeMoreConfig' + scope.dataRef.key" title="设置" :designer="designer">
                              <a-button size="small" type="link">编辑</a-button>
                              <template slot="content">
                                <TreeNodeConfiguration :widget="widget" :designer="designer" :node="scope.dataRef" />
                              </template>
                            </WidgetDesignDrawer>
                          </a-menu-item>
                          <a-menu-item key="del"><a-button size="small" type="link">删除</a-button></a-menu-item>
                        </a-menu>
                        <a-button size="small" icon="more" type="link" :style="{ marginRight: '5px', float: 'right' }" />
                      </a-dropdown>
                    </template>
                  </a-tree>
                </a-card>
              </div>
            </a-collapse-panel>
          </a-collapse>
        </a-form-model>
      </a-tab-pane>

      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer" :varOptions="[{ label: '节点是否选中', value: 'selected' }]">
          <template slot="eventParamValueHelpSlot">
            <div style="width: 600px">
              <p>
                1. 支持通过
                <a-tag>${ 参数 }</a-tag>
                表达式解析以下对象值:
              </p>
              <ul>
                <li>
                  treeState : 树形状态对象, 包含以下属性
                  <ul>
                    <li>selected : 当前是否选中节点</li>
                  </ul>
                </li>
                <li>
                  data : 选中节点的数据, 当属性节点由数据仓库或者数据模型构建而来, 则可以通过
                  <a-tag>${data.属性}</a-tag>
                  解析数据对象的属性值, 例如 ${data.uuid}
                </li>
              </ul>
              <p>
                2. 支持通过模板字符串逻辑输出内容值, 模板字符串内通过
                <a-tag><% %></a-tag>
                编写javaScript代码 ( 注意: 代码内不需要通过 ${} 表达式解析数据对象 ), 例如:
                <code
                  style="
                    display: block;
                    padding: 10px 25px;
                    border-radius: 4px;
                    background: rgb(250 250 250);
                    outline: 1px solid #dedede;
                    margin-top: 8px;
                  "
                >
                  <% if (treeState.selected) { %> 输出内容 <% } else { %> 输出内容 <% } %>
                </code>
              </p>
            </div>
          </template>
        </WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function';

import TreeNodeConfiguration from './tree-node-configuration.vue';
import TreeNodeCommonConfiguration from './tree-node-common-configuration.vue';
import DynamicGroupNameConfiguration from './dynamic-group-name-configuration.vue';

import ApiOperationDataset from '@dyform/app/web/widget/commons/api-operation-dataset.vue';

export default {
  name: 'WidgetTreeConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      newNode: {
        key: generateId(),
        scopedSlots: { title: 'titleSlot', icon: 'iconSlot' },
        operations: [],
        buildType: 'dataSource',
        selectable: true,
        dynamic: false
      },
      columnOptions: [],
      dataSourceOptions: [],
      expandedTreeKeys: [],
      buildOptions: [
        { label: '自定义', value: 'define' },
        { label: '数据源', value: 'dataSource' },
        { label: '数据模型', value: 'dataModel' },
        { label: 'API 服务', value: 'apiLinkService' }
      ],
      apiResultTransformSchema: {
        type: 'array',
        description: '树形节点数组',
        propertyEditable: false,
        items: {
          type: 'object',
          propertyEditable: false,
          properties: {
            id: {
              propertyEditable: false,
              type: 'string',
              description: '节点ID'
            },
            name: {
              propertyEditable: false,
              type: 'string',
              description: '节点名称'
            },
            children: {
              type: 'array',
              description: '子节点数组',
              propertyEditable: false,
              items: {
                type: 'object',
                propertyEditable: false,
                description: '树节点对象'
              }
            }
          }
        }
      },
      defaultConditionVar: [
        { label: '节点key', value: 'key' },
        { label: '父级key', value: 'parentKey' },
        { label: '标题', value: 'title' }
        // { label: '节点详细数据', value: '_originalData' }
      ]
    };
  },
  components: { TreeNodeConfiguration, TreeNodeCommonConfiguration, DynamicGroupNameConfiguration, ApiOperationDataset },
  computed: {},
  created() {
    this.fetchDataSourceOptions();
    if (this.widget.configuration.jsModules == undefined) {
      this.$set(this.widget.configuration, 'jsModules', []);
    }
    if (this.widget.configuration.button == undefined) {
      this.$set(this.widget.configuration, 'button', {
        buttonGroup: {
          style: { textHidden: false, type: 'default', icon: undefined, rightDownIconVisible: false }
        }
      });
    }

    if (this.widget.configuration.domEvents == undefined) {
      this.$set(this.widget.configuration, 'domEvents', [
        {
          id: 'treeNodeSelect',
          title: '节点点击触发',
          codeSource: 'codeEditor',
          jsFunction: undefined,
          widgetEvent: [],
          customScript: undefined // 事件脚本
        }
      ]);
    }
  },
  methods: {
    filterSelectOption,
    onConfirmAddNewNode(parent) {
      if (!this.newNode.title) {
        this.$message.error('请输入节点名称');
        return;
      }
      if (parent == -1) {
        this.widget.configuration.treeNodes.push(deepClone(this.newNode));
      } else {
        if (parent.children == undefined) {
          this.$set(parent, 'children', []);
        }
        this.newNode.parentKey = parent.key;
        parent.children.push(deepClone(this.newNode));
        if (!this.expandedTreeKeys.includes(parent.key)) {
          this.expandedTreeKeys.push(parent.key);
        }
      }

      this.newNode = {
        key: generateId(),
        scopedSlots: { title: 'titleSlot', icon: 'iconSlot' },
        dynamic: false,
        buildType: 'dataSource',
        supportDragSort: false,
        selectable: true,
        defaultExpand: false,
        operations: []
      };

      // this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
    },
    onClickTreeNodeMenu(e, scope) {
      if (e.key == 'del') {
        this.deleteByKey(this.widget.configuration.treeNodes, scope.key);
      }
    },
    deleteByKey(nodes, key) {
      for (let i = 0, len = nodes.length; i < len; i++) {
        if (nodes[i].key === key) {
          nodes.splice(i, 1);
          return true;
        } else if (nodes[i].children && nodes[i].children.length > 0) {
          if (this.deleteByKey(nodes[i].children, key)) {
            return true;
          }
        }
      }
      return false;
    },
    changeDataSourceId(value, opt, isChange) {
      this.fetchColumns();
      if (isChange) {
        this.clearNodeSelData();
      }
    },
    fetchDataSourceOptions(value) {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData'
        })
        .then(({ data }) => {
          if (data.results) {
            _this.dataSourceOptions = data.results;
          }
        });
    },

    fetchColumns() {
      let _this = this;
      this.columnOptions.splice(0, this.columnOptions.length);
      $axios
        .post('/json/data/services', {
          serviceName: 'viewComponentService',
          methodName: 'getColumnsById',
          args: JSON.stringify([this.widget.configuration.dataSourceId])
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            for (let i = 0, len = data.data.length; i < len; i++) {
              _this.columnOptions.push({ label: data.data[i].title, value: data.data[i].columnIndex });
            }
          }
        });
    },

    getFunctionElements(wgt) {
      let functionElements = {},
        elements = [];

      let configuration = wgt.configuration;

      if (configuration.buildType == 'dataSource') {
        if (configuration.dataSourceId) {
          elements.push({
            ref: true,
            functionType: 'dataStoreDefinition',
            exportType: 'dataStoreDefinition',
            configType: '1',
            id: configuration.dataSourceId,
            name: configuration.dataSourceName
          });
        }
      }

      functionElements[wgt.id] = elements;
      return functionElements;
    },

    onChangeBuildType(isChange) {
      if (this.widget.configuration.buildType == 'dataSource') {
        if (this.widget.configuration.dataSourceId) {
          this.fetchColumns();
          if (isChange) {
            this.clearNodeSelData();
          }
        }
      }

      if (this.widget.configuration.buildType == 'dataModel') {
        if (this.widget.configuration.dataModelUuid) {
          this.onChangeDataModelSelect(isChange);
        }
      }
    },

    onChangeDataModelSelect(isChange) {
      if (this.widget.configuration.dataModelUuid == undefined) {
        this.columnOptions.splice(0, this.columnOptions.length);
      } else {
        if (this.$refs.dataModelSelect) {
          this.$refs.dataModelSelect.fetchDataModelColumns(this.widget.configuration.dataModelUuid).then(list => {
            if (list) {
              this.columnOptions.splice(0, this.columnOptions.length);
              this.columnOptions.push(...list);
            }
          });
        } else {
          setTimeout(() => {
            this.onChangeDataModelSelect();
          }, 1000);
        }
      }
      if (isChange) {
        this.clearNodeSelData();
      }
    },
    clearNodeSelData() {
      this.$set(this.widget.configuration, 'sortField', undefined);
      this.$set(this.widget.configuration, 'titleField', undefined);
      this.$set(this.widget.configuration, 'valueField', undefined);
      this.$set(this.widget.configuration, 'parentField', undefined);
      this.$set(this.widget.configuration, 'iconField', undefined);
    }
  },
  beforeMount() {},
  mounted() {
    if (this.widget.configuration.buildType != 'define') {
      if (this.widget.configuration.buildType == 'dataSource' && this.widget.configuration.dataSourceId) {
        this.fetchColumns();
      } else if (this.widget.configuration.buildType == 'dataModel' && this.widget.configuration.dataModelUuid) {
        this.onChangeDataModelSelect();
      }
    }
  }
};
</script>
