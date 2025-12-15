<template>
  <!-- 使用组织树 -->
  <w-tree-select
    :value="value"
    :treeData="formatUseOrgTreeData"
    :replaceFields="{
      children: 'bizOrgs',
      title: 'name',
      label: 'name',
      key: 'id',
      value: 'id'
    }"
    :showSearch="false"
    :treeDefaultExpandAll="true"
    treeNodeLabelProp="label"
    @change="changeOrgId"
  >
    <template v-slot:name="{ name, bizOrgs }">
      <div style="display: flex; align-items: center; justify-content: space-between">
        {{ name }}
        <a-tag v-if="!bizOrgs" class="primary-color" style="margin-left: 8px">业务组织</a-tag>
      </div>
    </template>
  </w-tree-select>
</template>

<script>
import WTreeSelect from '../components/w-tree-select';
import mixins from '../mixins';

export default {
  name: 'UseOrgTreeSelect',
  mixins: [mixins],
  props: {
    value: {
      type: String
    },
    parentSelect: {
      // 父节点是否可选
      type: Boolean,
      default: true
    }
  },
  components: {
    WTreeSelect
  },
  computed: {
    formatUseOrgTreeData() {
      let treeData = [];
      this.useOrgTreeData.forEach(item => {
        if (this.parentSelect) {
          treeData.push(item);
        } else {
          if (item.bizOrgs.length) {
            treeData.push(item);
          }
        }
      });
      return treeData;
    }
  },
  methods: {
    changeOrgId(value, label, extra) {
      this.$emit('input', value);
      this.$emit('change', ...arguments);
    }
  }
};
</script>
