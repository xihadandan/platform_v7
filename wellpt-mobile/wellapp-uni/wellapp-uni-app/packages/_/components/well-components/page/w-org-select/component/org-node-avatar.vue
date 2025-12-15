<template>
  <view
    class="avatar"
    :style="{
      backgroundColor: nodeData.type == 'user' ? setIconBgColor(nodeData.type) : setIconBgColor(nodeData.type),
      color: nodeData.type == 'user' ? '#fff' : setIconColor(nodeData.type),
    }"
  >
    <template v-if="nodeData.type == 'user'">
      <img
        v-if="nodeData.data.avatar != null && !iconShow"
        :src="'/server-api/org/user/view/photo/' + nodeData.data.avatar"
        class="avatar-img"
        @error="imageError"
      />
      <text v-else>
        {{ nodeData[titleField].slice(0, 1) }}
      </text>
    </template>
    <template v-else>
      <w-icon
        :color="setIconColor(nodeData.type)"
        :size="18"
        isPc
        :icon="
          orgElementIcon[nodeData.type] || (nodeData.isLeaf ? 'file' : nodeData.expanded ? 'folder-open' : 'folder')
        "
      />
    </template>
  </view>
</template>
<style lang="scss" scoped>
.avatar {
  width: 36px;
  height: 36px;
  display: flex;
  // align-items: center;
  justify-content: center;
  border-radius: 18px;
  font-size: 18px;
  line-height: 34px;
  .avatar-img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
  }
}
</style>
<script>
export default {
  props: { nodeData: Object, orgElementIcon: Object, titleField: String },
  components: {},
  computed: {},
  data() {
    return {
      iconShow: false,
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    // console.log("节点", this.nodeData);
  },
  methods: {
    // 设置图标颜色
    setIconColor(type) {
      // if (["dept", "classify"].includes(type)) {
      //   return "var(--color-warning)";
      // } else if (["job"].includes(type)) {
      //   return "var(--color-success)";
      // }
      return "#ffffff";
    },
    // 设置图标背景颜色
    setIconBgColor(type) {
      if (["dept", "classify"].includes(type)) {
        return "var(--w-warning-color)";
      } else if (["job"].includes(type)) {
        return "var(--w-success-color)";
      }
      return "var(--w-primary-color)";
    },
    imageError: function (e) {
      this.$set(this, "iconShow", true);
    },
  },
};
</script>
