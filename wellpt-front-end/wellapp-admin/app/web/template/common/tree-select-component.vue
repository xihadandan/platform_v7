<template>
  <w-tree-select
    ref="treeSelectRef"
    :treeData="treeData"
    :value="values"
    :multiple="multiple"
    :editMode="editMode"
    :showCheckedStrategy="selectParent ? 'SHOW_ALL' : 'SHOW_CHILD'"
    :placeholder="placeholder"
    :disabled="disabled"
    @change="onChangeTree"
    :getPopupContainer="getPopupContainer"
  />
</template>

<script>
import WTreeSelect from '@dyformWidget/widget-form-select/components/WTreeSelect';
import { each, map, isArray } from 'lodash';
export default {
  components: { WTreeSelect },
  name: 'TreeSelectComponent',
  props: {
    propTreeData: {
      type: Array,
      default: () => []
    },
    value: [String, Array],
    allowClear: {
      type: Boolean,
      default: true
    },
    selectCheckAll: {
      type: Boolean,
      default: false
    },
    selectParent: {
      type: Boolean,
      default: true
    },
    allCollapse: {
      type: Boolean,
      default: false
    },
    showCheckedStrategy: {
      type: String,
      default: 'SHOW_CHILD'
    },
    placeholder: String,
    disabled: {
      type: Boolean,
      default: false
    },
    showSearch: {
      type: Boolean,
      default: true
    },
    allPath: {
      type: Boolean,
      default: false
    },
    multiple: {
      type: Boolean,
      default: false
    },
    tokenSeparators: {
      type: String,
      default: ';'
    },
    getPopupContainer: {
      type: Function,
      default: () => document.body
    },
    replaceFields: {
      type: [Object],
      default: () => {
        return { children: 'children', title: 'name', key: 'id', value: 'id' };
      }
    }
  },
  data() {
    let treeData = this.reSetTreeData(this.propTreeData);
    let values = this.value;
    let selectedLabels = this.setSelectedLabes();
    if (typeof values == 'string') {
      values = values.split(this.tokenSeparators);
    }
    return {
      backValue: undefined,
      values,
      treeData,
      selectedLabels, // 选中label数组
      valueLabelMap: {}
    };
  },
  watch: {
    propTreeData() {
      this.getTreeData();
    },
    value: {
      handler(v) {
        this.setSelectedLabes();
      }
    }
  },
  computed: {
    editMode() {
      return {
        allowClear: this.allowClear,
        showSearch: this.showSearch,
        allPath: this.allPath,
        selectCheckAll: this.selectCheckAll,
        allCollapse: this.allCollapse
      };
    }
  },
  created() {},
  methods: {
    // 获取下拉树数据
    getTreeData() {
      this.treeData = this.reSetTreeData(this.propTreeData);
      this.setValueLabelMap(this.treeData);
      this.setSelectedLabes();
      this.emitChange();
    },
    setValueLabelMap(options) {
      if (!this.valueLabelMap) {
        this.selectedLabels = {};
      }
      const tree2Map = data => {
        data.map(child => {
          this.valueLabelMap[child.s_tree_value] = child.s_tree_label;
          if (child.children) {
            tree2Map(child.children);
          }
        });
      };
      tree2Map(options);
    },
    setSelectedLabes() {
      if (!this.selectedLabels) {
        this.selectedLabels = [];
      }
      this.selectedLabels.splice(0, this.selectedLabels.length);
      // this.selectedLabels.length = 0;
      let values = this.value;
      if (values == undefined) {
        return [];
      }
      if (typeof values == 'string') {
        values = values.split(this.tokenSeparators);
      }
      this.values = values;
      for (let i = 0, len = values.length; i < len; i++) {
        if (this.valueLabelMap && this.valueLabelMap[values[i]] != undefined) {
          this.selectedLabels.push(this.valueLabelMap[values[i]]);
        }
      }
      return this.selectedLabels;
    },
    reSetTreeData(treeData) {
      const tree2Map = data => {
        return map(data, child => {
          child.s_tree_key = child[this.replaceFields.key] || child.id || child.uuid;
          child.s_tree_value = child[this.replaceFields.value] || child.real || child.value || child.s_tree_key;
          child.s_tree_title = child[this.replaceFields.title] || child.display || child.name || child.label;
          if (!child.s_tree_label) {
            child.s_tree_label = child.s_tree_title;
          }
          let children = child[this.replaceFields.children] || child.children;
          if (children && children.length) {
            each(children, c_child => {
              c_child.s_tree_title = c_child[this.replaceFields.title] || c_child.display || c_child.name || c_child.label;
              if (this.allPath) {
                c_child.s_tree_label = child.s_tree_label + '-' + c_child.s_tree_title;
              } else {
                c_child.s_tree_label = c_child.s_tree_title;
              }
            });
            child.children = tree2Map(children);
          }
          return child;
        });
      };
      return tree2Map(treeData);
    },
    onChangeTree(value, label, extra) {
      if (!label && isArray(value)) {
        label = map(value, 'label');
      }
      this.selectTreeNodeChange(value, label, extra);
    },
    // 选中树节点
    selectTreeNodeChange(val, label, extra) {
      this.values = val;
      if (Array.isArray(val)) {
        this.selectedLabels = label;
        this.backValue = val.join(this.tokenSeparators);
      } else {
        this.selectedLabels = [label];
        this.backValue = val;
      }
      this.emitChange();
    },
    emitChange() {
      this.$emit('input', this.backValue);
      this.$emit('change', {
        label: this.selectedLabels,
        backValue: this.backValue,
        value: this.values,
        valueLabelMap: this.valueLabelMap
      });
    }
  }
};
</script>

<style lang="less" scoped></style>
