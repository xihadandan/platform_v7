<!-- 组织头像或图标 -->
<template>
  <span>
    <template v-if="nodeData.iconClass">
      <a-avatar
        :size="24"
        :src="null"
        class="avatar-bg"
        :style="{
          backgroundColor: setIconBgColor(nodeData.type)
        }"
      >
        <Icon :type="nodeData.iconClass" :style="{ color: setIconColor(nodeData.type), margin: '0px' }" class="avatar-icon"></Icon>
      </a-avatar>
    </template>
    <template v-else-if="nodeData.type == 'user'">
      <a-avatar
        :size="24"
        :src="nodeData.data.avatar ? '/proxy/org/user/view/photo/' + nodeData.data.avatar : null"
        class="avatar-bg"
        style="font-size: var(--w-font-size-sm)"
      >
        <span v-if="!nodeData.data.avatar">
          {{ nodeData[titleField].slice(0, 1) }}
        </span>
      </a-avatar>
    </template>
    <template v-else-if="nodeData.type == 'dept' && nodeData.data.isDimension">
      <!-- 业务维度节点 -->
      <a-avatar :size="24" :src="null" class="avatar-bg" style="font-size: var(--w-font-size-sm)">
        <span>
          {{ nodeData.typeName.slice(0, 1) }}
        </span>
      </a-avatar>
    </template>
    <template v-else>
      <a-avatar :size="24" :style="{ backgroundColor: setIconBgColor(nodeData.type) }">
        <Icon
          :type="orgElementIcon[nodeData.type] || (nodeData.isLeaf ? 'file' : nodeData.expanded ? 'folder-open' : 'folder')"
          :style="{ color: setIconColor(nodeData.type) }"
          class="avatar-icon"
        ></Icon>
      </a-avatar>
    </template>
  </span>
</template>
<script type="text/babel">
export default {
  name: 'orgElementAvatar',
  props: {
    nodeData: Object,
    orgElementIcon: Object,
    titleField: String
  },
  methods: {
    // 设置图标颜色
    setIconColor(type) {
      if (['dept', 'classify'].includes(type)) {
        return 'var(--w-warning-color)';
      } else if (['job'].includes(type)) {
        return 'var(--w-success-color)';
      }
      return 'var(--w-primary-color)';
    },

    // 设置图标背景颜色
    setIconBgColor(type) {
      if (['dept', 'classify'].includes(type)) {
        return 'var(--w-warning-color-2)';
      } else if (['job'].includes(type)) {
        return 'var(--w-success-color-2)';
      }
      return 'var(--w-primary-color-2)';
    }
  }
};
</script>
<style lang="less" scoped>
.avatar-bg {
  background: var(--w-primary-color); //linear-gradient(180deg, var(--w-primary-color-4) 0%, var(--w-primary-color) 100%);
}
.avatar-icon {
  display: block;
  font-size: var(--w-font-size-sm);

  &.anticon {
    margin-top: e('calc((24px - var(--w-font-size-sm)) / 2)');
  }
}
</style>
