<template>
  <view
    class="avatar"
    :style="{
      backgroundColor: setIconColor(nodeData.type),
      color: '#fff',
    }"
  >
    <template v-if="nodeData.type == 'user'">
      <img
        v-if="nodeData.data.avatar != null"
        :src="'/org/user/view/photo/' + nodeData.data.avatar"
        class="avatar-img"
      />
      <text v-else>
        {{ nodeData[titleField].slice(0, 1) }}
      </text>
    </template>
    <template v-else>
      <w-icon
        color="#fff"
        :size="18"
        :icon="
          orgElementIcon[nodeData.type] || (nodeData.isLeaf ? 'file' : nodeData.expanded ? 'folder-open' : 'folder')
        "
      />
    </template>
  </view>
</template>
<style lang="scss"></style>
<script>
export default {
  props: { nodeData: Object, orgElementIcon: Object, titleField: String },
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    console.log("节点", this.nodeData);
  },
  methods: {
    // 设置图标颜色
    setIconColor(type) {
      if (["dept", "classify"].includes(type)) {
        return "var(--color-warning)";
      } else if (["job"].includes(type)) {
        return "var(--color-success)";
      }
      return "var(--color-primary)";
    },
  },
};
</script>
