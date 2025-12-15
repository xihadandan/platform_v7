<template>
  <div>
    <div class="widget-tree" :style="[vBackground]">
      <a-input-search v-if="widget.configuration.showSearch" @change="onSearchChange" allowClear />
      <a-skeleton active :loading="loading">
        <a-tree
          :class="[
            'ant-tree-directory',
            'tree-more-operations',
            searchValue ? 'search' : '',
            widget.configuration.showSearch ? 'hasSearch' : ''
          ]"
          :auto-expand-parent="autoExpandParent"
          :tree-data="treeNodes"
          :show-icon="widget.configuration.showIcon"
          :expandedKeys.sync="expandedTreeKeys"
          :selectedKeys.sync="selectedKeys"
          @expand="onExpand"
          @select="onSelect"
        >
          <a-icon slot="switcherIcon" type="down" />
          <template slot="iconSlot" slot-scope="scope" v-if="widget.configuration.showIcon">
            <Icon :type="scope.dataRef.icon || widget.configuration.globalSetting.icon" :size="20" />
          </template>
          <template slot="titleSlot" slot-scope="scope">
            <div class="widget-tree-title" :title="scope.title" :data-key="scope.key">
              <span v-if="scope.title && searchValue.length > 0 && scope.title.indexOf(searchValue) > -1" class="matched">
                {{ scope.title.substr(0, scope.title.indexOf(searchValue)) }}
                <span style="color: #f50">{{ searchValue }}</span>
                {{ scope.title.substr(scope.title.indexOf(searchValue) + searchValue.length) }}
              </span>
              <span v-else :title="scope.title" :data-key="scope.key">
                <a-icon type="loading" v-if="scope.dynamic" />
                <template v-else>
                  {{ scope.title }}
                </template>
              </span>
            </div>
            <div v-if="scope.dataRef.badge && scope.dataRef.badge.enable">
              <a-badge :count="getBadgeCount(scope)" :showZero="true" />
            </div>
            <div class="widget-tree-operations">
              <div>
                <template
                  v-for="op in scope.dataRef.operations.length
                    ? getOperations(scope.dataRef.operations)
                    : getOperations(widget.configuration.globalSetting.operations)"
                >
                  <template v-if="op.children">
                    <a-dropdown :key="op.id">
                      <a-menu slot="overlay" @click="e => onClickTreeNodeMenu(e, scope)">
                        <template v-for="c_op in op.children">
                          <a-menu-item :key="c_op.id" v-if="buttonVisible(c_op, scope)" class="w-ellipsis" style="max-width: 120px">
                            <Icon v-if="c_op.icon" :type="c_op.icon" :size="20" />
                            {{ $t(c_op.id, c_op.name) }}
                          </a-menu-item>
                        </template>
                      </a-menu>
                      <a-button v-if="moreButtonVisible(op)" :key="op.id" size="small" type="link">
                        <Icon v-if="op.style.icon" :type="op.style.icon" :size="20" />
                        {{ op.textHidden ? '' : $t(widget.id + '_dynamicGroupName', op.dynamicGroupName) }}
                      </a-button>
                    </a-dropdown>
                  </template>
                  <template v-else>
                    <a-button
                      :key="op.id"
                      size="small"
                      type="link"
                      @click.stop="e => onClickNodeOperation(op, scope)"
                      v-if="buttonVisible(op, scope)"
                    >
                      <Icon v-if="op.icon" :type="op.icon" :size="20" />
                      {{ op.nameHidden ? '' : $t(op.id, op.name) }}
                    </a-button>
                  </template>
                </template>
              </div>
            </div>
          </template>
        </a-tree>
        <a-empty v-if="searchValue && !hasSearchData" class="pt-search-empty" description="无匹配数据" />
        <a-empty v-else-if="treeNodes.length == 0" description="暂无节点数据" />
      </a-skeleton>
    </div>
  </div>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import DataSourceBase from '../../assets/js/commons/dataSource.base';
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
import './css/index.less';
import { expressionCompare, deepClone, generateId } from '@framework/vue/utils/util';
import { some, orderBy, template as stringTemplate } from 'lodash';

const getParentKey = (key, tree) => {
  let parentKey;
  for (let i = 0; i < tree.length; i++) {
    const node = tree[i];
    if (node.children) {
      if (node.children.some(item => item.key === key)) {
        parentKey = node.key;
      } else if (getParentKey(key, node.children)) {
        parentKey = getParentKey(key, node.children);
      }
    }
  }
  return parentKey;
};
export default {
  name: 'WidgetTree',
  mixins: [widgetMixin],
  data() {
    let expandedTreeKeys = this.widget.configuration.treeNodes.filter(item => item.defaultExpand).map(item => item.key);
    let defaultSelectKey = undefined;
    if (this.widget.configuration.defaultSelectType == 'selectFirstNode' && this.widget.configuration.buildType == 'define') {
      if (this.widget.configuration.treeNodes.length > 0 && !this.widget.configuration.treeNodes[0].dynamic) {
        defaultSelectKey = this.widget.configuration.treeNodes[0].key;
      }
    } else if (this.widget.configuration.defaultSelectType == 'selectByKey' && this.widget.configuration.defaultSelectKey != undefined) {
      defaultSelectKey = this.widget.configuration.defaultSelectKey;
    }
    return {
      loading: true,
      autoExpandParent: true,
      searchValue: '',
      expandedTreeKeys,
      defaultSelectKey,
      initTreeData: [],
      dataList: [],
      selectedKeys: [],
      nodeKeyMap: {},
      nodeTitleWidth: this.widget.configuration.treeNodeTitleWidth ? this.widget.configuration.treeNodeTitleWidth + 'px' : '100px',
      treeNodes: this.widget.configuration.buildType === 'define' ? JSON.parse(JSON.stringify(this.widget.configuration.treeNodes)) : [],
      dynamicQueryPromise: [],
      operatorWidth: '80px',
      hasSearchData: false
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    defaultEvents() {
      return [
        {
          id: 'refetch',
          title: '重新加载数据',
          codeSnippet: `
          /**
           * 重新加载数据
           */
          this.pageContext.emitEvent({{事件编码}});
          `
        },
        {
          id: 'setSelectedKeys',
          title: '选中节点',
          eventParams: [
            {
              paramKey: 'keys',
              remark: '节点值'
            },
            {
              paramKey: 'stopPropagation',
              remark: 'true/false , 停止触发节点选中事件(默认选中节点触发事件)'
            }
          ],
          codeSnippet: `
          /**
           * 选中节点
           * keys: 节点key值数组
           * stopPropagation: 是否停止传播节点选中事件 ，默认 false
           */
          this.pageContext.emitEvent({{事件编码}}, keys, stopPropagation);
          `
        },
        {
          id: 'setExpandedTreeKeys',
          title: '展开节点',
          eventParams: [
            {
              paramKey: 'keys',
              remark: '节点值'
            }
          ],
          codeSnippet: `
          /**
           * 展开所选节点
           * keys: 节点key值数组
           */
          this.pageContext.emitEvent({{事件编码}}, keys, stopPropagation);
          `
        },
        { id: 'refetchBadge', title: '刷新徽章数量' }
      ];
    }
  },
  created() {
    if (!this.designMode && EASY_ENV_IS_BROWSER) {
      this.getDataSourceProvider();
    }
  },
  methods: {
    setExpandedByPredicate(predicate) {
      if (typeof predicate == 'function') {
        let keys = [];
        for (let key in this.nodeKeyMap) {
          if (predicate(this.nodeKeyMap[key]._originalData)) {
            keys.push(key);
          }
        }
        if (keys.length) {
          this.setExpandedTreeKeys(keys);
        }
      }
    },
    setSelectedByPredicate(predicate, stopPropagation) {
      if (typeof predicate == 'function') {
        let keys = [];
        for (let key in this.nodeKeyMap) {
          if (predicate(this.nodeKeyMap[key]._originalData)) {
            keys.push(key);
          }
        }
        if (keys.length) {
          this.setSelectedKeys(keys, stopPropagation);
        }
      }
    },
    setSelectedKeys(e, stopPropagation) {
      let keys = [],
        _stopPropagation = stopPropagation;
      if (e != undefined) {
        this.selectedKeys.splice(0, this.selectedKeys.length);
        if (Array.isArray(e)) {
          keys.push(...e);
        } else if (typeof e == 'string') {
          keys.push(e);
        } else if (e.$evtWidget && e.eventParams && e.eventParams.keys) {
          // 通过组件事件派发传递的
          keys.push(...e.eventParams.keys.split(',|;|，|；'));
          _stopPropagation = e.eventParams.stopPropagation === 'true';
        }
        this.selectedKeys.push(...keys);

        if (this.selectedKeys.length) {
          // 递归展开所选的父级
          this.expandedTreeKeys.splice(0, this.expandedTreeKeys.length);
          this.expandBySubNodeKeys(this.selectedKeys);

          // 派发节点点击事件
          if (_stopPropagation !== true) {
            try {
              let meta = { treeState: { selected: true } };
              if (this.nodeKeyMap[this.selectedKeys[0]]._originalData != undefined) {
                meta.data = this.nodeKeyMap[this.selectedKeys[0]]._originalData;
              } else {
                meta.data = { title: this.nodeKeyMap[this.selectedKeys[0]].title, key: this.nodeKeyMap[this.selectedKeys[0]].key };
              }
              // 派发节点点击事件: 已废弃改为 treeNodeSelect 交互事件
              if (
                this.widget.configuration.nodeClickEventHandler != undefined &&
                this.widget.configuration.nodeClickEventHandler.actionType != undefined
              ) {
                let eventHandler = JSON.parse(JSON.stringify(this.widget.configuration.nodeClickEventHandler));
                eventHandler.pageContext = this.pageContext;
                eventHandler.$evtWidget = this;
                eventHandler.meta = meta;
                new DispatchEvent(eventHandler).dispatch();
              }
              this.triggerDomEvent('treeNodeSelect', { selected: true }, meta);
            } catch (error) {
              console.error(error);
            }
          }
        }
      }
    },
    expandBySubNodeKeys(keys) {
      for (let i = 0, len = keys.length; i < len; i++) {
        let node = this.nodeKeyMap[keys[i]];
        if (node) {
          let parentKey = node.parentKey;
          if (parentKey != undefined && this.nodeKeyMap[parentKey]) {
            if (!this.expandedTreeKeys.includes(parentKey)) {
              this.expandedTreeKeys.push(parentKey);
            }
            this.expandBySubNodeKeys([parentKey]);
          }
        }
      }
    },
    setExpandedTreeKeys(e) {
      let keys = [];
      if (e != undefined) {
        this.expandedTreeKeys.splice(0, this.expandedTreeKeys.length);
        if (Array.isArray(e)) {
          keys.push(...e);
        } else if (typeof e == 'string') {
          keys.push(e);
        } else if (e.$evtWidget && e.eventParams && e.eventParams.keys) {
          // 通过组件事件派发传递的
          keys.push(...e.eventParams.keys.split(',|;|，|；'));
        }
        this.expandedTreeKeys.push(...keys);
        this.expandBySubNodeKeys(keys);
      }
    },
    moreButtonVisible(op) {
      if (op.children) {
        return some(op.children, 'visible');
      }
      return false;
    },
    buttonVisible(op, scope) {
      let visible = op.defaultVisible === undefined ? true : op.defaultVisible;
      if (op.defaultVisibleVar && op.defaultVisibleVar.enable && op.defaultVisibleVar.code) {
        let code = op.defaultVisibleVar.code,
          value = op.defaultVisibleVar.value,
          operator = op.defaultVisibleVar.operator;
        visible = expressionCompare(scope.dataRef._originalData, code, operator, value) ? visible : !visible;
      }
      op.visible = visible;
      return visible;
    },
    onClickNodeOperation(operation, scope) {
      let eventHandler = JSON.parse(JSON.stringify(operation.eventHandler));
      eventHandler.pageContext = this.pageContext;
      eventHandler.$evtWidget = this;
      // eventHandler.$developJsInstance = this.$developJsInstance;
      eventHandler.meta = { key: scope.dataRef.key, title: scope.dataRef.title };
      new DispatchEvent(eventHandler).dispatch();
    },
    onClickTreeNodeMenu(e, scope) {
      if (this.designMode) {
        return false;
      }
      let operations = (scope.dataRef.operations || []).concat(this.widget.configuration.globalSetting.operations);
      for (let i = 0, len = operations.length; i < len; i++) {
        if (operations[i].id === e.key) {
          // 执行节点上的操作事件
          let eventHandler = JSON.parse(JSON.stringify(operations[i].eventHandler));
          eventHandler.pageContext = this.pageContext;
          eventHandler.$evtWidget = this;
          eventHandler.$developJsInstance = this.$developJsInstance;
          eventHandler.meta = { key: scope.dataRef.key, title: scope.dataRef.title };
          new DispatchEvent(eventHandler).dispatch();
          break;
        }
      }
    },
    onExpand(expandedKeys, { node, expanded }) {
      this.expandedKeys = expandedKeys;
      this.autoExpandParent = false;
      let _this = this;
      if (expanded && node.dataRef.supportDragSort) {
        setTimeout(() => {
          _this.draggable(node.dataRef.children, node.$el.querySelector('ul'), 'li', {
            onEvent: {
              afterOnEnd: () => {
                _this.invokeDevelopmentMethod('afterDrag', node.dataRef.children);
              }
            }
          });
        }, 1000); // 延迟等待展开
      }
    },
    onSelect(selectedKeys, { selected, node, selectedNodes }) {
      let meta = { treeState: { selected } };
      if (selected) {
        meta.data = node.dataRef._originalData;
      }
      try {
        // 派发节点点击事件: 已废弃改为 treeNodeSelect 交互事件
        if (
          this.widget.configuration.nodeClickEventHandler != undefined &&
          this.widget.configuration.nodeClickEventHandler.actionType != undefined
        ) {
          let eventHandler = JSON.parse(JSON.stringify(this.widget.configuration.nodeClickEventHandler));
          eventHandler.pageContext = this.pageContext;
          eventHandler.$evtWidget = this;
          eventHandler.meta = meta;
          new DispatchEvent(eventHandler).dispatch();
        }
        this.triggerDomEvent('treeNodeSelect', { selected }, meta);
      } catch (error) {
        console.error(error);
      }

      this.pageContext.emitEvent(`${this.widget.id}:treeNodeSelected`, { selectedKeys, selected, node, selectedNodes });
    },
    onSearchChange(e) {
      let value = e.target.value,
        gData = this.treeNodes,
        expandedTreeKeys = [],
        autoExpandParent = false;
      this.hasSearchData = false;
      if (value) {
        autoExpandParent = true;
        expandedTreeKeys = this.dataList
          .map(item => {
            if (item.title.indexOf(value) > -1) {
              this.hasSearchData = true;
              return getParentKey(item.key, gData);
            }
            return null;
          })
          .filter((item, i, self) => item && self.indexOf(item) === i);
      }

      Object.assign(this, {
        expandedTreeKeys,
        searchValue: value,
        autoExpandParent
      });
    },
    getDataSourceProvider(node) {
      let _this = this;
      if (node && node.key) {
        // 动态节点，每个节点都有它的事件派发
        if (!this.nodeDataSourceProvider) {
          this.nodeDataSourceProvider = {};
        }
        if (this.nodeDataSourceProvider[node.key]) {
          return this.nodeDataSourceProvider[node.key];
        } else {
          let options = {};
          if (node.buildType == 'dataSource') {
            options.dataStoreId = node.dataSourceId;
          } else {
            options.loadDataUrl = '/proxy/api/dm/loadData/' + node.dataModelUuid;
          }
          let params = { id: node.key, pid: node.parentKey };
          // 创建数据源
          let dsOptions = {
            onDataChange: function (data, count, params) {
              _this.onDataChange(data, count, params);
            },
            params,
            defaultCriterions: this.getDefaultCondition(node),
            receiver: _this,
            pageSize: -1,
            ...options
          };
          this.nodeDataSourceProvider[node.key] = new DataSourceBase(dsOptions);
          return this.nodeDataSourceProvider[node.key];
        }
      } else if (this.widget.configuration.buildType !== 'define') {
        if (this.dataSourceProvider) {
          return this.dataSourceProvider;
        } else {
          // 非自定义的
          if (
            (this.widget.configuration.dataModelUuid != undefined || this.widget.configuration.dataSourceId != undefined) &&
            this.widget.configuration.titleField != undefined &&
            this.widget.configuration.valueField != undefined
          ) {
            let options = {};
            if (this.widget.configuration.buildType == 'dataSource') {
              options.dataStoreId = this.widget.configuration.dataSourceId;
            } else {
              options.loadDataUrl = '/proxy/api/dm/loadData/' + this.widget.configuration.dataModelUuid;
            }
            let params = {};
            // 创建数据源
            let dsOptions = {
              onDataChange: function (data, count, params) {
                _this.onDataChange(data, count, params);
              },
              params,
              defaultCriterions: this.getDefaultCondition(),
              receiver: _this,
              pageSize: -1,
              ...options
            };
            this.dataSourceProvider = new DataSourceBase(dsOptions);
            return this.dataSourceProvider;
          }
        }
      }
    },
    getDefaultCondition(node, badge) {
      let defaultCondition = '';
      if (badge) {
        if (node && node.badge && node.badge.defaultCondition) {
          defaultCondition = node.badge.defaultCondition;
        } else if (this.widget.configuration.badge && this.widget.configuration.badge.defaultCondition) {
          defaultCondition = this.widget.configuration.badge.defaultCondition;
        }
      } else {
        if (node && node.defaultCondition) {
          defaultCondition = node.defaultCondition;
        } else if (this.widget.configuration.defaultCondition) {
          defaultCondition = this.widget.configuration.defaultCondition;
        }
      }
      if (defaultCondition) {
        let compiler = stringTemplate(defaultCondition);
        let sql = defaultCondition;
        try {
          // 节点内数据
          let data = this.widgetDependentVariableDataSource();
          if (node) {
            data.treeNode = node;
          }
          sql = compiler(data);
        } catch (error) {
          console.error('解析模板字符串错误: ', error);
          throw new Error('表格默认条件变量解析异常');
        }
        return [{ sql }];
      }
      return [];
    },
    addDataSourceParams(params, node) {
      if (node && this.nodeDataSourceProvider[node.key]) {
        for (let k in params) {
          this.nodeDataSourceProvider[node.key].addParam(k, params[k]);
        }
      } else if (this.dataSourceProvider) {
        for (let k in params) {
          this.dataSourceProvider.addParam(k, params[k]);
        }
      }
    },
    clearDataSourceParams(node) {
      if (node && this.nodeDataSourceProvider[node.key]) {
        for (let k in params) {
          this.nodeDataSourceProvider[node.key].clearParams();
        }
      } else if (this.dataSourceProvider) {
        this.dataSourceProvider.clearParams();
      }
    },
    deleteDataSourceParams(key, node) {
      if (node && this.nodeDataSourceProvider[node.key]) {
        for (let k of key) {
          this.nodeDataSourceProvider[node.key].removeParam(k);
        }
      } else if (this.dataSourceProvider) {
        for (let k of key) {
          this.dataSourceProvider.removeParam(k);
        }
      }
    },
    loadDs(params, callback, node) {
      let _this = this;
      this.getDataSourceProvider(node)
        .load(params)
        .then(({ data }) => {
          if (node && node.sortField) {
            data = orderBy(_this.parseSortNumberFieldValue(data, node.sortField), [node.sortField], [node.sortType || 'asc']);
          } else if (this.widget.configuration.sortField) {
            data = orderBy(
              _this.parseSortNumberFieldValue(data, this.widget.configuration.sortField),
              [this.widget.configuration.sortField],
              [this.widget.configuration.sortType || 'asc']
            );
          }
          if (callback) {
            callback(data);
          }
        });
    },

    parseSortNumberFieldValue(data, sortField) {
      data &&
        data.forEach(item => {
          let value = item[sortField];
          if (!isNaN(parseFloat(value))) {
            item[sortField] = parseFloat(value);
          }
        });
      return data;
    },

    buildTreeData(data, field, node, dynamicNode) {
      let map = {},
        nodes = [],
        scopedSlots = { title: 'titleSlot' };

      if (this.widget.configuration.showIcon) {
        scopedSlots.icon = 'iconSlot';
      }
      for (let i = 0, len = data.length; i < len; i++) {
        let icon = node ? node.icon : dynamicNode ? dynamicNode.icon : undefined;
        if (field.icon && data[i][field.icon]) {
          icon = data[i][field.icon];
        }
        map[data[i][field.key]] = {
          title: data[i][field.title],
          key: data[i][field.key],
          parentKey: data[i][field.parentKey],
          operations: node ? node.operations : [],
          icon,
          scopedSlots,
          badge: deepClone(field.badge),
          _originalData: data[i]
        };
        if (dynamicNode) {
          // 有动态节点，使用动态节点操作
          map[data[i][field.key]].operations = dynamicNode.operations || [];
          map[data[i][field.key]].dynamicNodeKey = dynamicNode.key;
        }
        this.nodeKeyMap[data[i][field.key]] = map[data[i][field.key]];

        this.dataList.push(map[data[i][field.key]]);
        if (data[i][field.parentKey] == undefined || data[i][field.parentKey] == '') {
          map[data[i][field.key]].parentKey = node != undefined ? node.key : undefined;
          nodes.push(map[data[i][field.key]]);
        }
      }

      for (let k in map) {
        let parentKey = map[k].parentKey;
        if (parentKey && map[parentKey]) {
          if (map[parentKey].children == undefined) {
            map[parentKey].children = [];
          }
          if (dynamicNode) {
            // 如果动态节点支持子节点拖动
            map[parentKey].supportDragSort = dynamicNode.supportDragSort;
          }
          map[parentKey].children.push(map[k]);
        }
      }

      return nodes;
    },

    getOperations(operations) {
      let buttons = deepClone(operations);
      let button = this.widget.configuration.button;
      if (
        button &&
        button.buttonGroup &&
        (button.buttonGroup.dynamicGroupBtnThreshold !== undefined || button.buttonGroup.dynamicGroupBtnThreshold !== null)
      ) {
        // 如果当前按钮数大于固定按钮数，把后面几个按钮放在分组按钮里
        if (buttons.length > button.buttonGroup.dynamicGroupBtnThreshold) {
          let noGroupButtons = buttons.splice(0, button.buttonGroup.dynamicGroupBtnThreshold);
          let moreButton = deepClone(button.buttonGroup);
          if (!moreButton.id) {
            moreButton.id = generateId();
          }
          moreButton.children = buttons;
          return noGroupButtons.concat([moreButton]);
        }
      }
      return buttons;
    },

    draggable(data, parentSelector, dragHandleClass, onEvent) {
      let _this = this;
      this.$nextTick(() => {
        import('sortablejs').then(Sortable => {
          Sortable.default.create(parentSelector, {
            handle: dragHandleClass,
            onChoose: e => {},
            onUnchoose: (onEvent && onEvent.onUnchoose) || function () {},
            onMove: (onEvent && onEvent.onMove) || function () {},
            onEnd:
              (onEvent && onEvent.onEnd) ||
              function (e) {
                let temp = data.splice(e.oldIndex, 1)[0];
                data.splice(e.newIndex, 0, temp);
                _this.draggable(data, parentSelector, dragHandleClass, onEvent);
                if (onEvent && onEvent.onEvent && onEvent.onEvent.afterOnEnd) {
                  onEvent.onEvent.afterOnEnd();
                }
              }
          });
        });
      });
    },

    load(reload, options) {
      let _this = this;
      this.$emit('beforeLoadData', options);
      this.invokeDevelopmentMethod('beforeLoadData', options);
      if (reload) {
        this.treeNodes =
          this.widget.configuration.buildType === 'define' ? JSON.parse(JSON.stringify(this.widget.configuration.treeNodes)) : [];
        this.loading = false;
      }
      if (
        this.widget.configuration.buildType !== 'define' &&
        (this.widget.configuration.dataModelUuid != undefined || this.widget.configuration.dataSourceId != undefined) &&
        this.widget.configuration.titleField != undefined &&
        this.widget.configuration.valueField != undefined
      ) {
        this.loadDs(options != undefined ? options.params : {}, data => {
          // 根据字段构建树
          this.treeNodes = this.buildTreeData(data, {
            title: this.widget.configuration.titleField,
            key: this.widget.configuration.valueField,
            parentKey: this.widget.configuration.parentField,
            icon: this.widget.configuration.iconField,
            badge: this.widget.configuration.badge
          });
          this.loading = false;

          this.selectedByDefault();
          _this.refetchBadge();
        });
      }

      if (this.widget.configuration.buildType === 'define') {
        // 动态加载
        let getDynamicNodes = (nodes, parentNode) => {
          if (nodes) {
            for (let i = 0, len = nodes.length; i < len; i++) {
              if (nodes[i].dynamic) {
                let { key, titleField, valueField, parentField, iconField } = nodes[i];
                this.dynamicQueryPromise.push(
                  new Promise((resolve, reject) => {
                    this.loadDs(
                      { id: nodes[i].key, pid: nodes[i].parentKey },
                      data => {
                        let list = this.buildTreeData(
                          data,
                          { title: titleField, key: valueField, parentKey: parentField, icon: iconField, badge: nodes[i].badge },
                          parentNode,
                          nodes[i]
                        );
                        resolve({ data: list, parentNode, replaceKey: key });
                      },
                      nodes[i]
                    );
                  })
                );
              } else {
                nodes[i].title = this.$t(nodes[i].key, nodes[i].title);
                this.nodeKeyMap[nodes[i].key] = nodes[i];
                this.dataList.push(nodes[i]);
                if (nodes[i].children && nodes[i].children.length) {
                  getDynamicNodes.call(this, nodes[i].children, nodes[i]);
                }
              }
            }
          }
        };
        if (!this.designMode) {
          getDynamicNodes.call(this, this.treeNodes, undefined);
        }
        if (this.dynamicQueryPromise.length) {
          Promise.all(this.dynamicQueryPromise).then(resolves => {
            for (let i = 0, len = resolves.length; i < len; i++) {
              let { data, parentNode, replaceKey } = resolves[i];
              console.log('动态加载树节点返回数据: ', resolves[i]);
              // 替换掉父节点下的动态节点
              let list = parentNode == undefined ? this.treeNodes : parentNode.children;
              if (replaceKey) {
                for (let i = 0, len = list.length; i < len; i++) {
                  if (list[i].key == replaceKey) {
                    list.splice(i, 1, ...data);
                    break;
                  }
                }
              }
            }
            _this.loading = false;
            _this.selectedByDefault();
            _this.refetchBadge();
          });
        } else {
          this.loading = false;
          this.selectedByDefault();
          _this.refetchBadge();
        }
      }

      if (this.widget.configuration.buildType == 'apiLinkService') {
        if (!this.designMode) {
          this.fetchTreeDataByApiLink(this.widget.configuration.apiInvocationConfig);
        }
      }
    },
    fetchTreeDataByApiLink(apiInvocationConfig) {
      let _this = this;
      if (apiInvocationConfig) {
        this.fetchDataByApiLinkInvocation(apiInvocationConfig).then(results => {
          let list = results != undefined ? (Array.isArray ? results : [results]) : [];
          _this.loading = false;
          if (list.length > 0) {
            let map = {},
              scopedSlots = { title: 'titleSlot' };

            if (_this.widget.configuration.showIcon) {
              scopedSlots.icon = 'iconSlot';
            }
            _this.treeNodes = list;
            let cascadeSetNode = (array, parent) => {
              if (array && array.length > 0) {
                for (let i = 0, len = array.length; i < len; i++) {
                  array[i].title = array[i].name;
                  array[i].key = array[i].id;
                  array[i].icon = array[i].icon ? array[i].icon : undefined;
                  array[i].badge = deepClone(_this.widget.configuration.badge);
                  _this.nodeKeyMap[array[i].id] = map[array[i].id] = {
                    title: array[i].name,
                    key: array[i].id,
                    operations: [],
                    icon: array[i].icon,
                    parentKey: parent ? parent.id : undefined,
                    scopedSlots,
                    _originalData: array[i]
                  };
                  Object.assign(array[i], map[array[i].id]);
                  cascadeSetNode(array[i].children, array[i]);
                }
              }
            };
            cascadeSetNode(list);
            setTimeout(() => {
              _this.refetchBadge();
            }, 500);
          }
        });
      } else {
        _this.loading = false;
      }
    },

    selectedByDefault() {
      this.selectedKeys.splice(0, this.selectedKeys.length);
      if (this.defaultSelectKey != undefined) {
        let node = this.nodeKeyMap[this.defaultSelectKey];
        if (node) {
          // 展开上一级
          this.expandedTreeKeys.splice(0, this.expandedTreeKeys.length);
          let parent = this.nodeKeyMap[node.parentKey];
          while (parent != undefined) {
            this.expandedTreeKeys.push(parent.key);
            parent = this.nodeKeyMap[parent.parentKey];
          }
        }
      } else if (this.widget.configuration.defaultSelectType == 'selectFirstNode') {
        if (this.treeNodes.length > 0 && !this.treeNodes[0].dynamic) {
          this.defaultSelectKey = this.treeNodes[0].key;
        }
      }
      if (!this.designMode) {
        this.$nextTick(() => {
          let elNode = this.$el.querySelector(`span[data-key='${this.defaultSelectKey}']`);
          if (elNode) {
            elNode.click();
          }
        });
      }
    },
    refetch(options) {
      this.load(true, options);
    },
    refetchBadge() {
      let _this = this;
      if (this.designMode) {
        return false;
      }
      for (let i = 0, len = this.treeNodes.length; i < len; i++) {
        let _node = this.treeNodes[i];
        let computed = (badge, node) => {
          if (badge && badge.enable) {
            let { badgeSourceType, dataSourceId, countJsFunction, defaultCondition, dataModelUuid } = badge;
            if (badgeSourceType == 'jsFunction' && countJsFunction) {
              new DispatchEvent({
                actionType: 'jsFunction',
                jsFunction: countJsFunction,
                $developJsInstance: this.$developJsInstance,
                $evtWidget: _this,
                meta: node,
                after: num => {
                  _this.$set(badge, 'count', num);
                }
              }).dispatch();
            } else if (badgeSourceType == 'dataModel' || badgeSourceType == 'dataSource') {
              let _this = this;
              badge.isRequire = false;
              if (this.widget.configuration.buildType === 'define') {
                // 不是动态节点的，会单独请求；动态节点，且动态节点没有子节点的会请求
                badge.isRequire =
                  node.dynamic === false || (!!node.dynamicNodeKey && (!node.children || (node.children && node.children.length == 0)));
              } else {
                // 没有子节点的会请求
                badge.isRequire = !node.children || (node.children && node.children.length == 0);
              }
              if (badge.isRequire) {
                // 创建数据源
                let dsOptions = {
                  onDataChange: function (data, count, params) {
                    _this.$set(badge, 'count', count);
                  },
                  receiver: _this,
                  params: {},
                  defaultCriterions: this.getDefaultCondition(node, true)
                };
                if (dataSourceId && badgeSourceType == 'dataSource') {
                  dsOptions.dataStoreId = dataSourceId;
                  new DataSourceBase(dsOptions).getCount(true);
                } else if (badgeSourceType == 'dataModel' && dataModelUuid) {
                  dsOptions.loadDataUrl = '/proxy/api/dm/loadData/' + dataModelUuid;
                  dsOptions.loadDataCntUrl = '/proxy/api/dm/loadDataCount/' + dataModelUuid;
                  new DataSourceBase(dsOptions).getCount(true);
                }
              }
            }
          }
          if (node.children && node.children.length > 0) {
            for (let j = 0; j < node.children.length; j++) {
              computed(node.children[j].badge, node.children[j]);
            }
          }
        };
        computed(_node.badge, _node);
      }
    },
    // 计算节点徽标数
    getBadgeCount(node) {
      let badge = node.dataRef.badge;
      let count = badge.count;
      if (badge.hasOwnProperty('isRequire') && !badge.isRequire) {
        // 动态父节点徽标数统计
        if (node.dataRef.children && node.dataRef.children.length) {
          count = 0;
          const getChildrenCountSum = child => {
            if (child.badge.isRequire) {
              count += child.badge.count || 0;
            }
            if (child.children && child.children.length) {
              for (let j = 0; j < child.children.length; j++) {
                getChildrenCountSum(child.children[j]);
              }
            }
          };
          getChildrenCountSum(node.dataRef);
        }
      }
      return count;
    }
  },
  beforeMount() {
    this.load();
  },
  mounted() {
    if (!this.designMode) {
      this.pageContext.handleEvent(`${this.widget.id}:refetchBadge`, function () {
        _this.refetchBadge();
      });
    }
  },
  watch: {}
};
</script>
