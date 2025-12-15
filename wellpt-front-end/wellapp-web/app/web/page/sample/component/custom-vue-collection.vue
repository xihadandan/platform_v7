<template>
  <a-card :style="{ width: width, height: height }" size="small">
    <a-divider orientation="left" style="position: absolute; top: -30px; left: 0px">
      <a-icon type="appstore" />
      数据集合
    </a-divider>
  </a-card>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'CustomVueCollection',
  inject: ['getNode'],
  mixins: [],
  data() {
    return {
      width: '500px',
      height: '500px'
    };
  },
  watch: {},
  beforeCreate() {},
  computed: {},
  created() {},
  methods: {},
  beforeMount() {},
  mounted() {
    const node = this.getNode();
    let _this = this;
    node.on('change:size', data => {
      _this.width = data.current.width + 'px';
      _this.height = data.current.height + 'px';
      // console.log('size is changed ', data);
    });
  },
  destroyed() {}
};
</script>
