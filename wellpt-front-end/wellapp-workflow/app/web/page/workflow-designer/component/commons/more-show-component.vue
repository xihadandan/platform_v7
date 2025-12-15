<template>
  <div>
    <a-divider v-if="!position || position == 'top'" :class="['more-show', showMore ? 'active' : '']" @click.native="showMoreHandle">
      更多
      <Icon type="pticon iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></Icon>
    </a-divider>
    <template v-if="showMore">
      <slot></slot>
    </template>
    <a-divider v-if="position == 'bottom'" :class="['more-show', showMore ? 'active' : '']" @click.native="showMoreHandle">
      更多
      <Icon type="pticon iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></Icon>
    </a-divider>
  </div>
</template>

<script>
export default {
  name: 'MoreShowComponent',
  props: {
    position: {
      type: String,
      defalut: ''
    }
  },
  components: {},
  data() {
    return {
      showMore: false
    };
  },
  created() {},
  methods: {
    showMoreHandle() {
      this.showMore = !this.showMore;
      this.$emit('change', this.showMore);
    }
  }
};
</script>
<style lang="less" scope>
.ant-divider.more-show {
  padding-left: var(--w-padding-2xs);
  padding-right: var(--w-padding-2xs);
  margin: 0 !important;
  cursor: pointer;

  .ant-divider-inner-text {
    color: var(--w-primary-color);
    font-size: var(--w-font-size-base);
  }

  &.active {
    .iconfont::before {
      content: '\e924'; //icon-ptkj-xianmiaoshuangjiantou-shang
    }
  }
}
</style>
