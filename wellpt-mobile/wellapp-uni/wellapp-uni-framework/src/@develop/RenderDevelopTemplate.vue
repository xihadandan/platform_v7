<template>
  <view>
    <!-- 由 render-develop-template-loader.js 插入代码 -->
    <!-- #ifdef H5 || APP-PLUS -->
    <component :is="isComponent" ref="component" :widget="widget" :parent="parent" :options="options">
      <template v-for="slot in slots">
        <template v-if="slot.name" :slot="slot.name">
          <slot :name="slot.name" :options="slot.option"></slot>
        </template>
      </template>
      <slot></slot>
    </component>
    <!-- #endif -->
    <!-- 以下代码不要去掉，由 loader 动态插入内容 -->
    <!-- #ifdef MP -->
    <!-- #endif -->
  </view>
</template>
<style lang="less"></style>
<script type="text/babel">
import developComps from "./developComps.js";
export default {
  name: "RenderDevelopTemplate",
  mixins: [developComps],
  props: {
    isComponent: {
      type: String,
    },
    widget: {
      type: Object,
      required: false,
    },
    parent: Object,
    options: Object,
    slots: Array,
  },
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.$emit("templateMounted", {
      componentInst: this.$refs.component,
    });
  },
  methods: {},
};
</script>
