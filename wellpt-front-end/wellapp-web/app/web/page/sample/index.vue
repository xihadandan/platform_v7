<template>
  <HtmlWrapper title="装配中心">
    <div style="width: 700px; background-color: #e8e8e8">
      <a-row :gutter="[64, 64]" style="outline: 1px solid">
        <a-col :span="12" style="outline: 1px solid red">
          <div style="height: 32px; background-color: aquamarine"></div>
        </a-col>
        <a-col :span="12" style="outline: 1px solid red"><div style="height: 32px; background-color: aquamarine"></div></a-col>
      </a-row>
    </div>
  </HtmlWrapper>
</template>
<style lang="less">
svg {
  position: fixed;
  top: 0px;
  path[id='test'] {
    fill: var(--w-primary-color);
  }
}
</style>
<script type="text/babel">
export default {
  name: 'TestIndex',
  props: {},
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
