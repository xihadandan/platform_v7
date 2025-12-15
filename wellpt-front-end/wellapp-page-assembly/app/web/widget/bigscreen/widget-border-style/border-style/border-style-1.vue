<template>
  <svg :width="vWidth" :height="vHeight">
    <polygon
      fill="transparent"
      :points="'7, 7 ' + (vWidth - 7) + ', 7 ' + (vWidth - 7) + ', ' + (vHeight - 7) + ' 7, ' + (vHeight - 7)"
    ></polygon>
    <polyline
      :points="'2, 2 ' + (vWidth - 2) + ' ,2 ' + (vWidth - 2) + ' , ' + (vHeight - 2) + ' 2, ' + (vHeight - 2) + ' 2, 2'"
      stroke="#83bff6"
      fill="transparent"
    ></polyline>
    <polyline
      :points="'6, 6 ' + (vWidth - 6) + ', 6 ' + (vWidth - 6) + ', ' + (vHeight - 6) + ' 6, ' + (vHeight - 6) + ' 6, 6'"
      stroke="#00CED1"
      fill="transparent"
    ></polyline>
    <circle cx="11" cy="11" r="1" fill="#83bff6"></circle>
    <circle :cx="vWidth - 11" cy="11" r="1" fill="#83bff6"></circle>
    <circle :cx="vWidth - 11" :cy="vHeight - 11" r="1" fill="#83bff6"></circle>
    <circle cx="11" :cy="vHeight - 11" r="1" fill="#83bff6"></circle>
  </svg>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'BorderStyle1',
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
