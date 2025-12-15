<template>
  <div class="widget-amap">
    <div
      :style="{
        height: vContainerHeight,
        width: vContainerWidth,
        backgroundColor: widget.useScope == 'bigScreen' ? 'unset' : '#fff'
      }"
      :id="id"
    ></div>
    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
  </div>
</template>
<script>
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import amapMixin from './amap.mixin';

export default {
  name: 'WidgetAmap',
  mixins: [widgetMixin, amapMixin],
  data() {
    return {};
  },
  computed: {},
  created() {},
  mounted() {},
  methods: {
    mapLoaded() {}
  }
};
</script>
<style scoped>
#container {
  width: 100%;
  height: 800px;
}
</style>
