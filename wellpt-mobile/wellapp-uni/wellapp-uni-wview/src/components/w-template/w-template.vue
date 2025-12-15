<template>
  <view class="w-template" style="width: 100%; height: auto">
    <!-- #ifdef H5 || APP-PLUS -->
    <component :is="componentName" />
    <!-- #endif -->
    <!-- 以下代码不要去掉，由 loader 动态插入内容 -->
    <!-- #ifdef MP -->
    <!-- #endif -->
  </view>
</template>
<script>
export default {
  props: {
    widget: {
      type: Object,
      required: true,
    },
    parent: Object,
  },
  components: {},
  computed: {
    componentName() {
      return this.widget.configuration.templateName;
    },
  },
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {},
};
</script>
