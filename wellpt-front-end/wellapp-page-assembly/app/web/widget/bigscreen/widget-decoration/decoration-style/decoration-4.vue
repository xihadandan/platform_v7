<template>
  <svg :width="vWidth" :height="vHeight">
    <polyline
      fill="transparent"
      stroke-width="3"
      points="0,9.8 62.82,9.8 69.8,19.6 87.25,19.6 94.23,29.4 251.28,29.4 261.75,19.6 279.2,19.6 286.18,9.8 349,9.8"
      stroke="#3f96a5"
    >
      <animate
        attributeName="stroke-dasharray"
        attributeType="XML"
        from="0, 184.01290348585783, 0, 184.01290348585783"
        to="0, 0, 368.02580697171567, 0"
        dur="3s"
        begin="0s"
        calcMode="spline"
        keyTimes="0;1"
        keySplines="0.4,1,0.49,0.98"
        repeatCount="indefinite"
      ></animate>
    </polyline>
    <polyline fill="transparent" stroke-width="2" points="104.7,39.2 244.29999999999998,39.2" stroke="#3f96a5">
      <animate
        attributeName="stroke-dasharray"
        attributeType="XML"
        from="0, 69.79999999999998, 0, 69.79999999999998"
        to="0, 0, 139.59999999999997, 0"
        dur="3s"
        begin="0s"
        calcMode="spline"
        keyTimes="0;1"
        keySplines=".4,1,.49,.98"
        repeatCount="indefinite"
      ></animate>
    </polyline>
  </svg>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'Decoration4',
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
