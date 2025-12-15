<template>
  <svg :width="vWidth" :height="vHeight">
    <rect :x="vWidth / 2" y="0" width="1" :height="vHeight" fill="#3faacb">
      <animate
        attributeName="height"
        from="0"
        :to="vHeight"
        dur="3s"
        calcMode="spline"
        keyTimes="0;1"
        keySplines=".42,0,.58,1"
        repeatCount="indefinite"
      ></animate>
    </rect>
    <rect :x="vWidth / 2" y="0" width="1" height="1">
      <animate
        attributeName="y"
        from="0"
        :to="vHeight"
        dur="3s"
        calcMode="spline"
        keyTimes="0;1"
        keySplines="0.42,0,0.58,1"
        repeatCount="indefinite"
      ></animate>
    </rect>
  </svg>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'Decoration3',
  props: {
    widget: Object
  },
  components: {},
  computed: {
    vWidth() {
      return this.widget.configuration.style.width;
    },
    vHeight() {
      return this.widget.configuration.style.height;
    }
  },
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
