<template>
  <!-- 树形节点配置 -->
  <div>
    <a-form-model-item label="显示值">
      <a-input placeholder="请输入" v-model="treeNode.display">
        <template slot="addonAfter">
          <WI18nInput :widget="widget" :designer="designer" :target="treeNode" :code="treeNode.id" v-model="treeNode.display" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="真实值">
      <a-input placeholder="请输入" v-model="treeNode.real" />
    </a-form-model-item>
  </div>
</template>

<script>
export default {
  name: 'SelectTreeNodeField',
  props: {
    widget: Object,
    designer: Object,
    treeNode: Object
  }
};
</script>
