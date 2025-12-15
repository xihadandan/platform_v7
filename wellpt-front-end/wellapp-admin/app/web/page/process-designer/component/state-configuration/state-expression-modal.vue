<template>
  <a-modal :title="title" width="650px" :visible="visible" :destroyOnClose="true" @ok="handleStateValueOk" @cancel="handleStateValueCancel">
    <a-form-model :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
      <a-form-model-item label="状态代码来源">
        <a-radio-group v-model="data.type" default-value="1">
          <a-radio value="constant">状态定义</a-radio>
          <a-radio value="freemarker">freemarker表达式</a-radio>
          <a-radio value="groovy">groovy脚本</a-radio>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="状态代码">
        <a-row>
          <a-col span="19">
            <a-tree-select
              v-if="data.type == 'constant'"
              style="width: 100%"
              v-model="stateTreeId"
              :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
              :tree-data="stateTreeData"
              :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
              tree-default-expand-all
              @select="onSelectStateTree"
            ></a-tree-select>
            <a-textarea v-else v-model="data.value"></a-textarea>
            <div v-if="data.type == 'freemarker'" class="remark" v-html="freemarkerScriptRemark"></div>
            <div v-if="data.type == 'groovy'" class="remark" v-html="groovyScriptRemark"></div>
          </a-col>
        </a-row>
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>

<script>
import { freemarkerScriptRemark, groovyScriptRemark } from '../../designer/remarks.js';
export default {
  props: {
    title: {
      type: String,
      default: '状态表达式配置'
    },
    visible: {
      type: Boolean,
      default: false
    },
    stateTreeData: {
      type: Array,
      default() {
        return [];
      }
    },
    data: {
      type: Object,
      data() {
        return { type: 'constant', displayValue: '', value: '' };
      }
    }
  },
  data() {
    return { stateTreeId: undefined, freemarkerScriptRemark, groovyScriptRemark };
  },
  watch: {
    visible: function (newValue, oldValue) {
      if (newValue && this.data.type == 'constant') {
        this.initStateTreeIdFromData();
      }
    }
  },
  methods: {
    initStateTreeIdFromData() {
      const _this = this;
      _this.stateTreeId = null;
      _this.data.displayValue = '';
      _this.data.value = '';

      let data = _this.data;
      let stateName = data.stateName;
      let stateCode = data.stateCode;
      if (!stateName && !stateCode) {
        return;
      }
      let nodeData = _this.findStateTreeNodeDataByNameAndCode(stateName, stateCode);
      if (nodeData && nodeData.id) {
        _this.stateTreeId = nodeData.id;
        _this.data.displayValue = stateName;
        _this.data.value = stateCode;
      }
    },
    findStateTreeNodeDataByNameAndCode(name, code) {
      const _this = this;
      let treeData = _this.stateTreeData;
      let nodeData = null;
      let findTreeNode = nodes => {
        if (nodeData) {
          return;
        }
        nodes &&
          nodes.forEach(node => {
            if (nodeData) {
              return;
            }
            if (node.name == name && node.data && node.data.code == code) {
              nodeData = node;
            }
            findTreeNode(node.children);
          });
      };
      findTreeNode(treeData);
      return nodeData;
    },
    handleStateValueOk() {
      this.$emit('ok', this.data);
    },
    handleStateValueCancel() {
      this.$emit('cancel', this.data);
    },
    onSelectStateTree(value, node, extra) {
      if (node._props && node._props.dataRef && node._props.dataRef.data) {
        this.data.displayValue = node._props.dataRef.name;
        this.data.value = node._props.dataRef.data.code;
      }
    }
  }
};
</script>

<style lang="less" scoped>
.remark {
  line-height: 1.5;
  font-size: 14px;
}
</style>
