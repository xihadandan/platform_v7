<template>
  <view class="w-page-container">
    <!-- 组件定义直接传入-->
    <view v-if="widgetDefinition.wtype != 'wMobilePage'">
      <w-widget :widget="widgetDefinition" :parent="{}" :key="widgetDefinition.id"></w-widget>
    </view>
    <template v-for="item in widgetDefinition.items">
      <w-widget :widget="item" :parent="{}" :key="item.id"></w-widget>
    </template>
  </view>
</template>

<script>
import wWidget from "../w-widget/w-widget";
export default {
  components: { wWidget },
  props: {
    id: String,
    widgetDefinition: {
      type: Object,
      required: false,
    },
  },
};
</script>

<style scoped>
.w-page-container {
}
</style>
