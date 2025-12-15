<template>
  <div class="_summary-item">
    <div class="_left" :style="`background-color: ${bgColor};`">
      <Icon
        :type="`${icon}`"
        :style="{
          color: color,
          fontSize: iconSize
        }"
      />
    </div>
    <div class="_right">
      <template v-if="$slots.titleAndSummary">
        <slot name="titleAndSummary"></slot>
      </template>
      <template v-else>
        <div class="_title">{{ title }}</div>
        <div class="_summary">{{ summary }}</div>
      </template>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FlowReportSummaryItem',
  props: {
    icon: {
      type: String,
      default: 'iconfont icon-luojizujian-faqiliucheng'
    },
    color: {
      type: String,
      default: 'var(--w-primary-color)'
    },
    bgColor: {
      type: String,
      default: 'var(--w-primary-color-1)'
    },
    iconSize: {
      type: String,
      default: '28px'
    },
    title: {
      type: String,
      default: ''
    },
    summary: {
      type: [String, Number],
      default: ''
    }
  }
};
</script>

<style lang="less">
._summary-item {
  display: flex;
  align-items: center;
  padding: 21px 20px;
  border-radius: 2px;
  background-color: var(--w-color-white);
  > ._left {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 48px;
    height: 48px;
    border-radius: 8px;
  }
  > ._right {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    margin-left: 12px;
    > ._title {
      color: var(--w-gray-color-9);
    }
    > ._summary {
      font-size: 20px;
      font-weight: bold;
    }
  }
}
</style>
