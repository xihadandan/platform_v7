<template>
  <div class="flow-select">
    <template v-if="mode === 'input'">
      <a-input :placeholder="placeholder" @click="open" v-model="displayValue" allowClear @change="displayValueChange">
        <Icon slot="suffix" type="pticon iconfont icon-ptkj-suoyinshezhi" />
      </a-input>
    </template>
    <template v-if="mode === 'list'">
      <a-list bordered :data-source="selectedNodes" :locale="locale" @click="open">
        <a-list-item class="selected-node" slot="renderItem" slot-scope="item">
          <a-row>
            <a-col span="24">
              <span style="margin-left: 8px">
                <a-icon v-if="item.data && item.data.startsWith('FLOW_CATEGORY_')" type="folder"></a-icon>
                <a-icon v-else-if="item.icon" :type="item.icon"></a-icon>
                <a-icon v-else type="file"></a-icon>
                {{ item.name }}
                <a-tag class="tag-flow-def-name" v-if="item.flowDefName">{{ item.flowDefName }}</a-tag>
              </span>
            </a-col>
          </a-row>
        </a-list-item>
      </a-list>
    </template>
    <a-modal
      :title="title"
      class="pt-modal"
      :visible="modalVisible"
      :width="900"
      :bodyStyle="bodyStyle"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <a-row>
        <a-col :span="treeSpan">
          <a-tabs v-if="showRecentUsed" v-model="activeKey" tabPosition="left" default-active-key="recent" style="width: 125px">
            <a-tab-pane key="recent">
              <template slot="tab">
                <Icon type="iconfont icon-oa-zuijinfangwen"></Icon>
                最近办理
              </template>
            </a-tab-pane>
            <a-tab-pane key="all">
              <template slot="tab">
                <Icon type="iconfont icon-ptkj-qiehuanshitu"></Icon>
                全部流程
              </template>
            </a-tab-pane>
          </a-tabs>
          <a-card
            v-if="showRecentUsed && activeKey == 'recent'"
            class="tree-container"
            :class="showRecentUsed ? 'show-recent-used' : ''"
            style="margin-right: 8px"
            :bodyStyle="{ height: '300px', overflow: 'auto' }"
          >
            <template v-if="checkType != 'task'" slot="title">
              <a-checkbox v-model="checkAllRecent" @change="onCheckAllRecentChange">全选</a-checkbox>
            </template>
            <a-input-search
              v-model="recentTreeSearchValue"
              @change="onSearchRecentTree"
              allowClear
              placeholder="关键字"
              style="margin-bottom: 12px"
            />
            <a-tree
              v-model="checkedKeys"
              :tree-data="recentTreeData"
              :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
              class="recent-tree"
              blockNode
              checkable
              checkStrictly
              @check="onCheck"
            >
              <template slot="title" slot-scope="{ name, icon, data }">
                <div class="tree-title">
                  <a-icon type="file"></a-icon>
                  <span v-if="recentTreeSearchValue && name.indexOf(recentTreeSearchValue) > -1">
                    {{ name.substr(0, name.indexOf(recentTreeSearchValue)) }}
                    <span style="color: #f50">{{ recentTreeSearchValue }}</span>
                    {{ name.substr(name.indexOf(recentTreeSearchValue) + recentTreeSearchValue.length) }}
                  </span>
                  <span v-else>{{ name }}</span>
                  <a-tag class="flow-category-name">{{ getFlowCategoryName(data) }}</a-tag>
                </div>
              </template>
            </a-tree>
            <a-empty v-if="!matchRecentTreeSearch || recentTreeData.length === 0" description="暂无数据">
              <template slot="image">
                <div />
              </template>
            </a-empty>
          </a-card>
          <a-card
            v-if="!showRecentUsed || activeKey == 'all'"
            class="tree-container"
            :class="showRecentUsed ? 'show-recent-used' : ''"
            style="margin-right: 8px"
            :bodyStyle="{ height: '300px', overflow: 'auto' }"
          >
            <template v-if="checkType != 'task'" slot="title">
              <a-checkbox v-model="checkAll" @change="onCheckAllChange">全选</a-checkbox>
            </template>
            <a-input-search v-model="treeSearchValue" @change="onSearchTree" allowClear placeholder="关键字" style="margin-bottom: 12px" />
            <a-tree
              v-model="checkedKeys"
              :tree-data="treeData"
              :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
              :expandedKeys="expandedKeys"
              checkable
              checkStrictly
              @check="onCheck"
              @expand="onExpand"
              :load-data="checkType == 'task' ? onLoadTaskData : undefined"
            >
              <template slot="title" slot-scope="{ name, icon, data }">
                <a-icon v-if="data && data.startsWith('FLOW_CATEGORY_')" type="folder"></a-icon>
                <a-icon v-else-if="icon" :type="icon"></a-icon>
                <a-icon v-else type="file"></a-icon>
                <span v-if="treeSearchValue && name.indexOf(treeSearchValue) > -1">
                  {{ name.substr(0, name.indexOf(treeSearchValue)) }}
                  <span style="color: #f50">{{ treeSearchValue }}</span>
                  {{ name.substr(name.indexOf(treeSearchValue) + treeSearchValue.length) }}
                </span>
                <span v-else>{{ name }}</span>
              </template>
            </a-tree>
            <a-empty v-if="!matchTreeSearch" description="暂无数据">
              <template slot="image">
                <div />
              </template>
            </a-empty>
          </a-card>
        </a-col>
        <a-col :span="selectedSpan">
          <a-card style="margin-left: 8px" :bodyStyle="{ height: '300px', overflow: 'auto' }">
            <template slot="title">已选 ({{ selectedCount }})</template>
            <template slot="extra">
              <a-button type="link" icon="delete" @click="e => removeAllSelectedNode([...treeData, ...recentTreeData])">清空</a-button>
            </template>
            <a-input-search v-model="listSearchValue" @change="onSearchList" allowClear placeholder="关键字" style="margin-bottom: 12px" />
            <a-list :data-source="listData" :locale="locale">
              <a-list-item class="selected-node" slot="renderItem" slot-scope="item">
                <a-row>
                  <a-col span="22">
                    <span style="margin-left: 8px">
                      <a-icon v-if="item.data && item.data.startsWith('FLOW_CATEGORY_')" type="folder"></a-icon>
                      <a-icon v-else-if="item.icon" :type="item.icon"></a-icon>
                      <a-icon v-else type="file"></a-icon>
                      {{ item.name }}
                      <a-tag class="tag-flow-def-name" v-if="item.flowDefName">{{ item.flowDefName }}</a-tag>
                    </span>
                  </a-col>
                  <a-col span="2">
                    <a-icon class="remove-selected" type="close" @click="removeSelectedNode(item)"></a-icon>
                  </a-col>
                </a-row>
              </a-list-item>
            </a-list>
          </a-card>
        </a-col>
      </a-row>
    </a-modal>
  </div>
</template>

<script>
import { isEmpty, debounce, isArray } from 'lodash';
export default {
  props: {
    title: {
      type: String,
      default: '选择流程'
    },
    value: {
      type: [String, Array]
    },
    placeholder: {
      type: String,
      default: '请选择'
    },
    bodyStyle: {
      type: Object,
      default() {
        return {
          width: '100%',
          height: '350px'
        };
      }
    },
    mode: {
      type: String,
      default: 'input' // input、list
    },
    // 显示最近使用
    showRecentUsed: {
      type: Boolean,
      default: false
    },
    categoryDataAsId: {
      type: Boolean,
      default: false
    },
    checkType: {
      type: String,
      default: 'flow' // flow、task
    }
  },
  inject: ['locale'],
  data() {
    if (this.locale && !this.locale.emptyText) {
      this.locale.emptyText = '暂无数据';
    }
    let checked = [];
    let valueAsArray = false;
    if (this.value) {
      valueAsArray = isArray(this.value);
      if (valueAsArray) {
        checked = this.value;
      } else {
        checked = this.value.split(';');
      }
    }
    return {
      modalVisible: false,
      activeKey: this.showRecentUsed ? 'recent' : 'all',
      checkAllRecent: false,
      recentTreeData: [],
      recentTreeSearchValue: '',
      matchRecentTreeSearch: true,
      checkAll: false,
      treeData: [],
      checkedKeys: { checked },
      expandedKeys: [],
      displayValue: '',
      selectedNodes: [],
      treeSearchValue: '',
      listSearchValue: '',
      matchTreeSearch: true,
      valueAsArray,
      notFoundKeys: [],
      watchValue: true
    };
  },
  computed: {
    selectedCount() {
      return this.checkedKeys.checked.length;
    },
    listData() {
      return this.selectedNodes.filter(node => !node.hidden);
    },
    treeSpan() {
      return this.showRecentUsed ? 14 : 12;
    },
    selectedSpan() {
      return this.showRecentUsed ? 10 : 12;
    }
  },
  watch: {
    value(val) {
      if (this.watchValue) {
        if (this.valueAsArray) {
          this.checkedKeys.checked = val || [];
        } else {
          this.checkedKeys.checked = (val && val.split(';')) || [];
        }
        this.updateDisplayValue();
      }
    }
  },
  mounted() {
    const _this = this;
    let promises = [];
    if (_this.showRecentUsed) {
      promises.push(_this.loadRecentUsed());
    }
    if (_this.checkedKeys.checked.length) {
      promises.push(_this.loadTreeData());
    }
    if (_this.checkType == 'task' && _this.checkedKeys.checked.length) {
      Promise.all(promises)
        .then(() => {
          return _this.loadSelectedTaskData(_this.checkedKeys.checked);
        })
        .then(() => {
          if (_this.notFoundKeys.length) {
            _this.$emit('keyNotFound', _this.notFoundKeys);
          }
        });
    } else {
      Promise.all(promises).then(() => {
        if (_this.notFoundKeys.length) {
          _this.$emit('keyNotFound', _this.notFoundKeys);
        }
      });
    }
  },
  methods: {
    open() {
      this.modalVisible = true;
      this.loadTreeData().then(() => {
        this.initValue = this.checkedKeys.checked.join(';');
        this.initDisplayValue = this.displayValue;
      });
    },
    loadRecentUsed() {
      const _this = this;
      if (_this.showRecentUsed) {
        return $axios
          .get(`/proxy/api/workflow/new/work/getUserRecentUseFlowDefinitions`)
          .then(({ data: result }) => {
            _this.loading = false;
            if (result.code == 0) {
              if (result.data && result.data.children) {
                _this.recentTreeData = result.data.children;
                return result.data;
              } else {
                return [];
              }
            } else {
              _this.$message.error(result.msg || '系统服务异常！');
              return [];
            }
          })
          .catch(err => {
            _this.loading = false;
            _this.$message.error('系统服务异常！');
            return [];
          });
      } else {
        return Promise.resolve([]);
      }
    },
    getFlowCategoryName(flowDefId) {
      const _this = this;
      let treeData = _this.treeData;
      for (let index = 0; index < treeData.length; index++) {
        let children = treeData[index].children || [];
        for (let i = 0; i < children.length; i++) {
          let child = children[i];
          if (child.data === flowDefId) {
            return treeData[index].name;
          }
        }
      }
      return '无分类';
    },
    loadTreeData() {
      let _this = this;
      if (_this.treeData.length) {
        return Promise.resolve();
      }
      return this.$axios
        .post('/json/data/services', {
          serviceName: 'flowSchemeService',
          methodName: 'getAllFlowTree',
          args: JSON.stringify(['-1'])
        })
        .then(({ data: result }) => {
          if (result.data) {
            if (_this.checkType === 'task') {
              _this.unCheckableTreeNodes(result.data);
            }
            if (_this.categoryDataAsId) {
              result.data.forEach(item => (item.id = item.data));
            }
            _this.treeData = result.data;
            _this.updateDisplayValue();
          }
        });
    },
    onLoadTaskData(treeNode) {
      if (treeNode.dataRef && treeNode.dataRef.children && treeNode.dataRef.children.length > 0) {
        return Promise.resolve([]);
      }
      return $axios.get(`/proxy/api/workflow/definition/getFlowTasks?flowDefId=${treeNode.dataRef.id}`).then(({ data: result }) => {
        if (result.data) {
          treeNode.dataRef.children = this.tasks2TreeNodes(result.data, treeNode.dataRef);
        }
      });
    },
    tasks2TreeNodes(tasks = [], flowNodeData) {
      let children = [];
      tasks.forEach(task => {
        children.push({
          name: task.name,
          id: flowNodeData.id + ':' + task.id,
          isLeaf: true,
          icon: 'menu',
          flowDefName: flowNodeData.name
        });
      });
      return children;
    },
    loadSelectedTaskData(keys = []) {
      const _this = this;
      let flowDefIds = [];
      _this.checkedKeys.checked.forEach(key => {
        if (key && key.includes(':')) {
          let keyParts = key.split(':');
          if (!flowDefIds.includes(keyParts[0])) {
            flowDefIds.push(keyParts[0]);
          }
        }
      });
      if (flowDefIds.length) {
        return $axios.get(`/proxy/api/workflow/definition/getFlowTaskMap?flowDefIds=${flowDefIds}`).then(({ data: result }) => {
          if (result.data) {
            flowDefIds.forEach(flowDefId => {
              let tasks = result.data[flowDefId];
              if (tasks) {
                let treeNode = _this.getTreeNodeByKey(_this.treeData, flowDefId);
                if (treeNode) {
                  treeNode.children = _this.tasks2TreeNodes(tasks, treeNode);
                }
                if (_this.showRecentUsed) {
                  let recentTreeNode = _this.getTreeNodeByKey(_this.recentTreeData, flowDefId);
                  if (recentTreeNode) {
                    recentTreeNode.children = _this.tasks2TreeNodes(tasks, recentTreeNode);
                  }
                }
              }
            });
            _this.updateDisplayValue();
          }
        });
      } else {
        return Promise.resolve();
      }
    },
    unCheckableTreeNodes(treeNodes = []) {
      treeNodes.forEach(node => {
        node.checkable = false;
        this.unCheckableTreeNodes(node.children);
      });
    },
    onSearchTree: debounce(function () {
      const _this = this;
      let expandedKeys = [];
      _this.matchTreeSearch = false;
      _this.treeData.forEach(category => {
        let children = category.children || [];
        let matchParent = category.name.indexOf(_this.treeSearchValue) != -1;
        let matchChild = false;
        children.forEach(item => {
          let match = item.name.indexOf(_this.treeSearchValue) != -1;
          if (matchParent || match) {
            if (match) {
              matchChild = true;
            }
            item.style = {};
            _this.matchTreeSearch = true;
          } else {
            item.style = { display: 'none' };
          }
        });

        if (matchChild || matchParent) {
          category.style = {};
          _this.matchTreeSearch = true;
          if (matchChild) {
            expandedKeys.push(category.id);
          }
        } else {
          category.style = { display: 'none' };
        }
      });

      if (isEmpty(_this.treeSearchValue)) {
        _this.expandedKeys = [];
      } else {
        _this.expandedKeys = expandedKeys;
      }
    }, 200),
    onSearchList: debounce(function () {
      const _this = this;
      _this.selectedNodes.forEach(node => {
        _this.$set(node, 'hidden', node.name.indexOf(_this.listSearchValue) == -1);
      });
    }, 200),
    onSearchRecentTree() {
      const _this = this;
      _this.matchRecentTreeSearch = false;
      _this.recentTreeData.forEach(category => {
        let match = category.name.indexOf(_this.recentTreeSearchValue) != -1;
        if (match) {
          category.style = {};
          _this.matchRecentTreeSearch = true;
        } else {
          category.style = { display: 'none' };
        }
      });
    },
    onCheckAllRecentChange(e) {
      if (e.target.checked) {
        this.checkNodes(this.recentTreeData);
        this.$nextTick(() => {
          this.$refs.flowTree && this.$refs.flowTree.$forceUpdate();
        });
        this.updateDisplayValue();
      } else {
        this.removeAllSelectedNode(this.recentTreeData);
      }
    },
    onCheckAllChange(e) {
      if (e.target.checked) {
        this.checkNodes(this.treeData);
        this.$nextTick(() => {
          this.$refs.flowTree && this.$refs.flowTree.$forceUpdate();
        });
        this.updateDisplayValue();
      } else {
        this.removeAllSelectedNode(this.treeData);
      }
    },
    checkNodes(treeNodes) {
      const _this = this;
      for (let i = 0; i < treeNodes.length; i++) {
        let treeNode = treeNodes[i];
        if (treeNode.style && treeNode.style.display === 'none') {
          continue;
        }
        if (!_this.checkedKeys.checked.includes(treeNode.id)) {
          _this.checkedKeys.checked.push(treeNode.id);
        }
        if (treeNode.children) {
          _this.checkNodes(treeNode.children);
        }
      }
    },
    onCheck(checkedKeys, e) {
      this.checkedKeys = checkedKeys;
      this.updateDisplayValue();
    },
    onExpand(expandedKeys) {
      this.expandedKeys = expandedKeys;
    },
    displayValueChange() {
      const _this = this;
      if (isEmpty(_this.displayValue) && _this.checkedKeys.checked.length) {
        _this.removeAllSelectedNode([...this.recentTreeData, ...this.treeData]);

        let value = _this.checkedKeys.checked.join(';');
        _this.watchValue = false;
        _this.$emit('change', { value: value, label: _this.displayValue });
        _this.$emit('input', value);
        _this.$emit('update:modelValue', value);
        _this.$nextTick(() => {
          _this.watchValue = true;
        });
      }
    },
    updateDisplayValue() {
      const _this = this;
      let names = [];
      let checked = _this.checkedKeys.checked || [];
      let selectedNodes = [];
      checked.forEach(key => {
        let treeNode = _this.getTreeNodeByKey(_this.treeData, key);
        if (treeNode) {
          names.push(treeNode.name);
          selectedNodes.push(treeNode);
        }
      });
      _this.displayValue = names.join(';');
      _this.selectedNodes = selectedNodes;
    },
    removeSelectedNode(nodeData) {
      this.checkedKeys.checked.splice(this.checkedKeys.checked.indexOf(nodeData.id), 1);
      this.updateDisplayValue();
    },
    removeAllSelectedNode(nodes = []) {
      let keys = [];
      let extraceKeys = nodes => {
        nodes.forEach(node => {
          keys.push(node.id);
          if (node.children) {
            extraceKeys(node.children);
          }
        });
      };
      extraceKeys(nodes);

      keys.forEach(key => {
        let keyIndex = this.checkedKeys.checked.indexOf(key);
        if (keyIndex != -1) {
          this.checkedKeys.checked.splice(keyIndex, 1);
        }
      });
      //this.checkedKeys.checked = [];
      this.updateDisplayValue();
      this.checkAllRecent = false;
      this.checkAll = false;
    },
    getTreeNodeByKey(treeNodes, key) {
      const _this = this;
      for (let i = 0; i < treeNodes.length; i++) {
        let treeNode = treeNodes[i];
        if (treeNode.id === key) {
          return treeNode;
        } else if (treeNode.children) {
          let data = _this.getTreeNodeByKey(treeNode.children, key);
          if (data != null) {
            if (_this.notFoundKeys.includes(key)) {
              _this.notFoundKeys.splice(_this.notFoundKeys.indexOf(key), 1);
            }
            return data;
          } else {
            if (key && !_this.notFoundKeys.includes(key)) {
              _this.notFoundKeys.push(key);
            }
          }
        }
      }

      return null;
    },
    handleCancel() {
      const _this = this;
      let initValue = _this.initValue;
      let initDisplayValue = _this.initDisplayValue;
      if (initValue) {
        _this.checkedKeys.checked = initValue.split(';');
      } else {
        _this.checkedKeys.checked = [];
      }
      _this.displayValue = initDisplayValue;
      _this.updateDisplayValue();
      _this.modalVisible = false;
    },
    handleOk() {
      const _this = this;
      _this.watchValue = false;
      if (_this.valueAsArray) {
        let value = _this.checkedKeys.checked;
        let displayValue = (_this.displayValue && _this.displayValue.split(';')) || [];
        _this.$emit('change', { value: value, label: displayValue, nodes: _this.selectedNodes });
        _this.$emit('input', value);
        _this.$emit('update:modelValue', value);
      } else {
        let value = _this.checkedKeys.checked.join(';');
        _this.$emit('change', { value: value, label: _this.displayValue, nodes: _this.selectedNodes });
        _this.$emit('input', value);
        _this.$emit('update:modelValue', value);
      }
      _this.$nextTick(() => {
        _this.watchValue = true;
      });
      _this.modalVisible = false;
    }
  }
};
</script>

<style lang="less" scoped>
.flow-select {
  display: inline-block;
}
.tree-container {
  ::v-deep .ant-empty-image {
    height: 10px;
  }
  &.show-recent-used {
    position: absolute;
    top: 0;
    right: 0;
    width: 373px;
  }

  .recent-tree {
    ::v-deep .ant-tree-switcher-noop {
      width: 0px;
    }
    .tree-title {
      position: relative;
      .flow-category-name {
        position: absolute;
        right: -40px;
      }
    }
  }
}
.selected-node {
  width: 100%;
  display: inline-block;
  position: relative;

  .remove-selected {
    display: none;
  }
  &:hover {
    background: var(--w-primary-color-3);
    .remove-selected {
      display: inline-block;
    }
  }

  .tag-flow-def-name {
    position: absolute;
    right: 0;
  }
}
</style>
