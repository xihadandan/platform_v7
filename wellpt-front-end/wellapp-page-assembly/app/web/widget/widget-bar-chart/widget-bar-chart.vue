<template>
  <div>
    <div
      :style="{ height: vContainerHeight, width: vContainerWidth, backgroundColor: widget.useScope == 'bigScreen' ? 'unset' : '#fff' }"
    ></div>
    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import chartMixin from './chart.mixin';
export default {
  name: 'WidgetBarChart',
  mixins: [widgetMixin, chartMixin],
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
