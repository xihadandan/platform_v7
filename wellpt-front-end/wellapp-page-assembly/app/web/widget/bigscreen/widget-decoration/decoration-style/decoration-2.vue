<template>
  <svg :width="vWidth" :height="vHeight">
    <rect x="0" :y="vHeight / 2" :width="vWidth" height="1" fill="#3faacb">
      <animate
        attributeName="width"
        from="0"
        :to="vWidth"
        dur="3s"
        calcMode="spline"
        keyTimes="0;1"
        keySplines=".42,0,.58,1"
        repeatCount="indefinite"
      ></animate>
    </rect>
    <rect x="0" :y="vHeight / 2" width="1" height="1" fill="#fff">
      <animate
        attributeName="x"
        from="0"
        :to="vWidth"
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
  name: 'Decoration2',
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
