<template>
  <div class="config-sub-title">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: 'ConfigSubTitle'
};
</script>

<style lang="less" scoped>
.config-sub-title {
  position: relative;
  height: 17px;
  line-height: 17px;
  padding-left: 8px;
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 0;
    height: 12px;
    width: 2px;
    background: #488cee;
    transform: translateY(-50%);
  }
}
</style>