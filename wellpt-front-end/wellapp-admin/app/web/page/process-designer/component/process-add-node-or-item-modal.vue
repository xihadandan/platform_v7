<template>
  <a-modal v-model="modalVisible" :title="title || '添加阶段/事项'" ok-text="确定" cancel-text="取消" @ok="handleAddNodeOrItemOk">
    <a-form-model ref="ruleForm" :model="formData" :rules="rules" :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
      <a-form-model-item v-show="!type" label="添加类型" prop="nodeType">
        <a-radio-group v-model="formData.nodeType" :default-value="defaultNodeType" @change="formData.refMode = 'none'">
          <a-radio v-for="(option, index) in nodeTypeOptions" :key="index" :value="option.value">{{ option.text }}</a-radio>
          <!-- <a-radio value="1">子阶段/阶段</a-radio>
            <a-radio value="2">事项</a-radio> -->
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="引用方式" prop="refMode">
        <a-radio-group v-model="formData.refMode" default-value="none">
          <a-radio value="none">不引用</a-radio>
          <a-radio v-if="formData.nodeType == '1'" value="node">阶段</a-radio>
          <a-radio v-if="formData.nodeType == '2'" value="item">事项</a-radio>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item v-if="formData.refMode == 'none'" label="数量" prop="count">
        <a-input-number v-model="formData.count" />
      </a-form-model-item>
      <a-form-model-item v-if="formData.refMode == 'node'" label="引用阶段" prop="refNode">
        <a-tree-select
          v-model="formData.refNodeTreeId"
          style="width: 100%"
          :dropdown-style="{ maxHeight: '300px', overflow: 'auto' }"
          :tree-data="nodeTreeData"
          placeholder="请选择阶段"
          @select="onSelectNode"
        >
          <span slot="title" slot-scope="node">
            <Icon v-if="node.data && node.data.type == 'process'" type="pticon iconfont icon-ptkj-suoyinshezhi"></Icon>
            <Icon v-else-if="node.data && node.data.type == 'node'" type="pticon iconfont icon-wsbs-anbinglianjieduan"></Icon>
            <Icon v-else-if="node.data && node.data.type == 'item'" type="pticon iconfont icon-oa-chulishixiang"></Icon>
            {{ node.name }}
            <template v-if="node.data && node.data.type == 'process'">
              ({{ (node.data.version + '').includes('.') ? node.data.version : node.data.version + '.0' }})
            </template>
            <template v-else-if="node.data && node.data.isRef">(引用)</template>
            <template v-else-if="node.data && node.data.isTemplate">(模板)</template>
          </span>
        </a-tree-select>
      </a-form-model-item>
      <a-form-model-item v-if="formData.refMode == 'item'" label="引用事项" prop="refItem">
        <a-tree-select
          v-model="formData.refItemTreeId"
          style="width: 100%"
          :dropdown-style="{ maxHeight: '300px', overflow: 'auto' }"
          :tree-data="itemTreeData"
          placeholder="请选择事项"
          @select="onSelectItem"
        >
          <span slot="title" slot-scope="node">
            <Icon v-if="node.data && node.data.type == 'process'" type="pticon iconfont icon-ptkj-suoyinshezhi"></Icon>
            <Icon v-else-if="node.data && node.data.type == 'node'" type="pticon iconfont icon-wsbs-anbinglianjieduan"></Icon>
            <Icon v-else-if="node.data && node.data.type == 'item'" type="pticon iconfont icon-oa-chulishixiang"></Icon>
            {{ node.name }}
            <template v-if="node.data && node.data.type == 'process'">
              ({{ (node.data.version + '').includes('.') ? node.data.version : node.data.version + '.0' }})
            </template>
            <template v-else-if="node.data && node.data.isRef">(引用)</template>
            <template v-else-if="node.data && node.data.isTemplate">(模板)</template>
          </span>
        </a-tree-select>
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>

<script>
import { deepClone, getCacheData } from '../designer/utils';
let allTypeOptions = [
  { text: '子阶段/阶段', value: '1' },
  { text: '事项', value: '2' }
];
let nodeTypeOptions = [{ text: '子阶段/阶段', value: '1' }];
let DEFAULT_DATA = { nodeType: '1', refMode: 'none', count: 3, childNode: true };
export default {
  props: {
    designer: Object,
    title: String,
    type: String,
    ok: Function
  },
  inject: ['designer'],
  data() {
    const _this = this;
    let defaultNodeType = this.type || '1';
    return {
      modalVisible: false,
      nodeTypeOptions: allTypeOptions,
      formData: {
        ...DEFAULT_DATA
      },
      defaultNodeType,
      rules: {
        count: [
          { required: true, message: '请输入数量', trigger: 'blur' },
          {
            validator(rule, value, callback) {
              let count = parseInt(value);
              if (_this.refMode == 'none') {
                return count >= 1 && count <= 10;
              } else {
                return true;
              }
            },
            message: '请输入1~10的数字',
            trigger: 'blur'
          }
        ]
      },
      treeData: [],
      nodeTreeData: [],
      itemTreeData: []
    };
  },
  created() {},
  methods: {
    getCacheData,
    loadProcessDefinitionTree() {
      let _this = this;
      let businessId = _this.designer.processDefinition.businessId;
      let excludeDefId = _this.designer.processDefinition.id;
      _this
        .getCacheData(`processDefinitionTree`, (resolve, reject) => {
          _this.$axios
            .get(`/proxy/api/biz/process/definition/getTemplateTree?businessId=${businessId}&excludeDefId=${excludeDefId}`)
            .then(({ data: result }) => {
              if (result.data) {
                resolve(result.data);
              }
            })
            .catch(err => reject(err));
        })
        .then(treeData => {
          _this.nodeTreeData = _this.convertNodeTreeData(treeData);
          _this.itemTreeData = _this.convertItemTreeData(treeData);
          _this.treeData = treeData;
        });
    },
    convertNodeTreeData(treeData) {
      const _this = this;
      let nodeTreeData = deepClone(treeData);
      _this.traverseTree(nodeTreeData, node => {
        node.value = node.id;
        // 配置replaceFields或存在title属性时对应的scopedSlots不生效
        // node.title = node.name;
        node.scopedSlots = { title: 'title' };
        let nodeData = node.data || {};
        if (nodeData.type == 'process' || nodeData.type == 'item') {
          node.selectable = false;
        }
        // 引用阶段不可被引用
        if (nodeData.isRef) {
          node.selectable = false;
          _this.traverseTree([node], child => {
            child.selectable = false;
            child.disabled = true;
          });
        }
      });
      return nodeTreeData;
    },
    convertItemTreeData(treeData) {
      const _this = this;
      let itemTreeData = deepClone(treeData);
      _this.traverseTree(itemTreeData, node => {
        node.value = node.id;
        // 配置replaceFields或存在title属性时对应的scopedSlots不生效
        // node.title = node.name;
        node.scopedSlots = { title: 'title' };
        let nodeData = node.data || {};
        if (nodeData.type == 'process' || nodeData.type == 'node') {
          node.selectable = false;
        }
        // 引用事项不可被引用
        if (nodeData.isRef) {
          node.selectable = false;
          _this.traverseTree([node], child => {
            // child.selectable = false;
            child.disabled = true;
          });
        }
      });
      return itemTreeData;
    },
    traverseTree(treeNodes, consumer) {
      const _this = this;
      treeNodes.forEach(node => {
        if (consumer) {
          consumer(node);
        }
        if (node.children && node.children.length) {
          _this.traverseTree(node.children, consumer);
        }
      });
    },
    markNodeTreeDataAsTemplate(id) {
      const _this = this;
      _this.traverseTree(_this.nodeTreeData, node => {
        if (node.id == id) {
          node.data.template = true;
        }
      });
      _this.nodeTreeData = [..._this.nodeTreeData];
    },
    markItemTreeDataAsTemplate(id) {
      const _this = this;
      _this.traverseTree(_this.itemTreeData, node => {
        if (node.id == id) {
          node.data.template = true;
        }
      });
      _this.itemTreeData = [..._this.itemTreeData];
    },
    open(layout, position) {
      const _this = this;
      // 初始化表单数据
      _this.formData = { ...DEFAULT_DATA };
      if (!_this.treeData.length) {
        _this.loadProcessDefinitionTree();
      }

      // 水平方向
      if (layout == 'horizontal') {
        // 添加同级阶段
        if (position == 'right') {
          _this.nodeTypeOptions = nodeTypeOptions;
          _this.formData.nodeType = _this.defaultNodeType;
          _this.formData.childNode = false;
        } else {
          // 添加子阶段/事项
          _this.nodeTypeOptions = allTypeOptions;
          if (this.type) {
            _this.formData.nodeType = this.type;
          }
          _this.formData.childNode = true;
        }
      } else {
        // 垂直方向
        // 添加子阶段/事项
        if (position == 'right') {
          _this.nodeTypeOptions = allTypeOptions;
          _this.formData.childNode = true;
          if (this.type) {
            _this.formData.nodeType = this.type;
          }
        } else {
          // 添加同级阶段
          _this.nodeTypeOptions = nodeTypeOptions;
          _this.formData.nodeType = _this.defaultNodeType;
          _this.formData.childNode = false;
        }
      }
      let nodeTypeOption = _this.nodeTypeOptions.find(item => item.value == '1');
      if (nodeTypeOption) {
        nodeTypeOption.text = _this.formData.childNode ? '子阶段' : '阶段';
      }

      // 类型过滤
      if (_this.type) {
        _this.nodeTypeOptions = _this.nodeTypeOptions.filter(item => item.value == _this.type);
        _this.formData.nodeType = _this.type;
      }

      _this.modalVisible = true;
    },
    onSelectNode(value, node, extra) {
      const _this = this;
      _this.formData.refNodeId = node.dataRef.data.id;
      _this.formData.refProcessDefUuid = node.dataRef.data.processDefUuid;
    },
    onSelectItem(value, node, extra) {
      const _this = this;
      _this.formData.refItemId = node.dataRef.data.id;
      _this.formData.refProcessDefUuid = node.dataRef.data.processDefUuid;
    },
    loadRefNodeDefinition(formData) {
      let processDefUuid = formData.refProcessDefUuid;
      let nodeId = formData.refNodeId;
      return $axios.get(`/proxy/api/biz/process/definition/node/${nodeId}?processDefUuid=${processDefUuid}`).then(({ data: result }) => {
        if (result.data) {
          this.markNodeTreeDataAsTemplate(formData.refNodeTreeId);
          result.data.refProcessDefUuid = processDefUuid;
        }
        return result.data;
      });
    },
    loadRefItemDefinition(formData) {
      let processDefUuid = formData.refProcessDefUuid;
      let itemId = formData.refItemId;
      return $axios.get(`/proxy/api/biz/process/definition/item/${itemId}?processDefUuid=${processDefUuid}`).then(({ data: result }) => {
        if (result.data) {
          this.markItemTreeDataAsTemplate(formData.refItemTreeId);
          result.data.refProcessDefUuid = processDefUuid;
        }
        return result.data;
      });
    },
    existsChildNode(nodeDefinition) {
      const _this = this;
      let exists = false;
      let checkNodeExists = (node, check) => {
        if (exists) {
          return;
        }
        if (check) {
          let existsNode = _this.designer.processTree.getTreeNodeByKey(node.id);
          if (existsNode) {
            exists = true;
            return;
          }
        }

        let children = [...(node.nodes || []), ...(node.items || [])];
        children.forEach(child => {
          if (exists) {
            return;
          }
          checkNodeExists(child, true);
        });
      };
      checkNodeExists(nodeDefinition, false);
      return exists;
    },
    handleAddNodeOrItemOk() {
      const _this = this;
      _this.$refs.ruleForm.validate(valid => {
        if (valid && _this.ok) {
          if (_this.formData.refMode == 'node') {
            if (!_this.formData.refNodeTreeId) {
              _this.$message.error('请选择引用阶段！');
              return;
            }
            _this.loadRefNodeDefinition(_this.formData).then(nodeDefinition => {
              let existsNode = _this.designer.processTree.getTreeNodeByKey(nodeDefinition.id);
              if (existsNode) {
                _this.$message.error(`阶段[${nodeDefinition.name}]已存在，不能重复引用！`);
                return;
              } else if (_this.existsChildNode(nodeDefinition)) {
                _this.$message.error(`阶段[${nodeDefinition.name}]下的子阶段/事项已存在，不能重复引用！`);
                return;
              }

              _this.formData.nodeDefinition = nodeDefinition;
              _this.ok(_this.formData);
              _this.modalVisible = false;
            });
          } else if (_this.formData.refMode == 'item') {
            if (!_this.formData.refItemTreeId) {
              _this.$message.error('请选择引用事项！');
              return;
            }
            _this.loadRefItemDefinition(_this.formData).then(itemDefinition => {
              let existsItem = _this.designer.processTree.getTreeNodeByKey(itemDefinition.id);
              if (existsItem) {
                _this.$message.error(`事项[${itemDefinition.itemName}]已存在，不能重复引用！`);
                return;
              }

              _this.formData.itemDefinition = itemDefinition;
              _this.ok(_this.formData);
              _this.modalVisible = false;
            });
          } else {
            _this.ok(_this.formData);
            _this.modalVisible = false;
          }
        }
      });
    }
  }
};
</script>

<style></style>
