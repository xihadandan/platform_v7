<template>
  <BizProcessDesigner ref="bizProcessDesigner"></BizProcessDesigner>
</template>

<script>
import BizProcessDesigner from '../../page/process-designer';

export default {
  components: { BizProcessDesigner },
  inject: ['assemble'],
  data() {
    return {};
  },
  methods: {}
};
</script>

<style lang="less" scoped></style>
