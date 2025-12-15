<template>
  <w-section class="w-widget-card" v-if="!widget.configuration.hidden" :title="widget.title" type="line">
    <view v-for="(widget, index) in widget.configuration.widgets" :key="index">
      <w-dyform-widget-tab v-if="widget.wtype == 'WidgetTab'" :widget="widget" :formScope="formScope">
        <!-- 区块动态插槽 -->
        <!-- #ifdef H5 || APP-PLUS -->
        <slot
          v-for="(blockWidgetCode, slotIndex) in blockWidgetCodes"
          :index="slotIndex"
          :name="'block_' + blockWidgetCode"
          :slot="'block_' + blockWidgetCode"
        ></slot>
        <!-- #endif -->
        <!-- #ifdef MP -->
        <slot
          v-for="(blockWidgetCode, slotIndex) in blockWidgetCodes"
          :index="slotIndex"
          name="{{'block_' + blockWidgetCode}}"
          slot="{{'block_' + blockWidgetCode}}"
        ></slot>
        <!-- #endif -->
      </w-dyform-widget-tab>
      <w-dyform-widget-card
        v-else-if="widget.wtype == 'WidgetCard' || widget.wtype == 'WidgetUniPanel'"
        :widget="widget"
        :formScope="formScope"
      >
        <!-- 区块动态插槽 -->
        <!-- #ifdef H5 || APP-PLUS -->
        <slot
          v-for="(blockWidgetCode, slotIndex) in blockWidgetCodes"
          :index="slotIndex"
          :name="'block_' + blockWidgetCode"
          :slot="'block_' + blockWidgetCode"
        ></slot>
        <!-- #endif -->
        <!-- #ifdef MP -->
        <slot
          v-for="(blockWidgetCode, slotIndex) in blockWidgetCodes"
          :index="slotIndex"
          name="{{'block_' + blockWidgetCode}}"
          slot="{{'block_' + blockWidgetCode}}"
        ></slot>
        <!-- #endif -->
      </w-dyform-widget-card>
      <w-dyform-widget-field v-else :widget="widget" :formScope="formScope" />
    </view>
    <!-- 区块插槽 -->
    <!-- #ifdef H5 || APP-PLUS -->
    <slot :name="'block_' + widget.configuration.code"></slot>
    <!-- #endif -->
    <!-- #ifdef MP -->
    <slot name="{{'block_' + widget.configuration.code}}"></slot>
    <!-- #endif -->
  </w-section>
</template>
<script>
export default {
  name: "wDyformWidgetCard",
  props: {
    widget: {
      type: Object,
      required: true,
    },
    formScope: {
      type: Object,
      required: true,
    },
  },
  computed: {
    blockWidgetCodes() {
      return this.formScope.getBlockWidgetCodes();
    },
  },
};
</script>

<style></style>
