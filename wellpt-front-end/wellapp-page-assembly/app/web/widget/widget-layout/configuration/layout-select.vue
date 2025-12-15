<template>
  <div class="widget-layout-type-select">
    <svg
      width="32.000000"
      height="32.000000"
      viewBox="0 0 32 32"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      xmlns:xlink="http://www.w3.org/1999/xlink"
      @click="clickToSelect('topMiddleBottom')"
      :class="selectedType == 'topMiddleBottom' ? 'selected' : ''"
    >
      <desc>布局1: 上 - 中 - 下</desc>
      <defs />
      <rect id="矩形 3" x="0.500000" y="0.500000" width="31.000000" height="7.000000" fill="#FFFFFF" />
      <rect id="矩形 3" x="0.500000" y="0.500000" width="31.000000" height="7.000000" stroke="#d9d9d9" />
      <rect id="矩形 4" x="0.500000" y="9.500000" width="31.000000" height="14.000000" fill="#FFFFFF" />
      <rect id="矩形 4" x="0.500000" y="9.500000" width="31.000000" height="14.000000" stroke="#d9d9d9" />
      <rect id="矩形 5" x="0.500000" y="25.500000" width="31.000000" height="6.000000" fill="#FFFFFF" />
      <rect id="矩形 5" x="0.500000" y="25.500000" width="31.000000" height="6.000000" stroke="#d9d9d9" />
    </svg>
    <svg
      width="32.000000"
      height="32.000000"
      viewBox="0 0 32 32"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      xmlns:xlink="http://www.w3.org/1999/xlink"
      @click="clickToSelect('topMiddleSiderBottom')"
      :class="selectedType == 'topMiddleSiderBottom' ? 'selected' : ''"
    >
      <desc>布局二: 上 - 左侧中 - 下</desc>
      <defs />
      <rect id="矩形 3" x="0.500000" y="0.500000" width="31.000000" height="7.000000" fill="#FFFFFF" />
      <rect id="矩形 3" x="0.500000" y="0.500000" width="31.000000" height="7.000000" stroke="#d9d9d9" />
      <rect id="矩形 4" x="12.499985" y="9.500000" width="19.000000" height="14.000000" fill="#FFFFFF" />
      <rect id="矩形 4" x="12.499985" y="9.500000" width="19.000000" height="14.000000" stroke="#d9d9d9" />
      <rect id="矩形 5" x="0.500000" y="25.500000" width="31.000000" height="6.000000" fill="#FFFFFF" />
      <rect id="矩形 5" x="0.500000" y="25.500000" width="31.000000" height="6.000000" stroke="#d9d9d9" />
      <rect id="矩形 6" x="0.500000" y="9.500000" width="9.000000" height="14.000000" fill="#FFFFFF" />
      <rect id="矩形 6" x="0.500000" y="9.500000" width="9.000000" height="14.000000" stroke="#d9d9d9" />
    </svg>

    <svg
      width="32.000000"
      height="32.000000"
      viewBox="0 0 32 32"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      xmlns:xlink="http://www.w3.org/1999/xlink"
      @click="clickToSelect('topMiddleRightSiderBottom')"
      :class="selectedType == 'topMiddleRightSiderBottom' ? 'selected' : ''"
    >
      <desc>布局二: 上 - 右侧中 - 下</desc>
      <defs />
      <rect id="矩形 3" x="0.500000" y="0.500000" width="31.000000" height="7.000000" fill="#FFFFFF" />
      <rect id="矩形 3" x="0.500000" y="0.500000" width="31.000000" height="7.000000" stroke="#d9d9d9" />
      <rect id="矩形 4" x="0.500000" y="9.500000" width="19.000000" height="14.000000" fill="#FFFFFF" />
      <rect id="矩形 4" x="0.500000" y="9.500000" width="19.000000" height="14.000000" stroke="#d9d9d9" />
      <rect id="矩形 5" x="0.500000" y="25.500000" width="31.000000" height="6.000000" fill="#FFFFFF" />
      <rect id="矩形 5" x="0.500000" y="25.500000" width="31.000000" height="6.000000" stroke="#d9d9d9" />
      <rect id="矩形 6" x="22.500000" y="9.500000" width="9.000000" height="14.000000" fill="#FFFFFF" />
      <rect id="矩形 6" x="22.500000" y="9.500000" width="9.000000" height="14.000000" stroke="#d9d9d9" />
    </svg>
    <svg
      width="32.000000"
      height="32.000000"
      viewBox="0 0 32 32"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      xmlns:xlink="http://www.w3.org/1999/xlink"
      @click="clickToSelect('siderTopMiddleBottom')"
      :class="selectedType == 'siderTopMiddleBottom' ? 'selected' : ''"
    >
      <desc>布局二: 左侧 - 上 - 中 - 下</desc>
      <defs />
      <rect id="矩形 3" x="12.500000" y="0.500000" width="19.000000" height="7.000000" fill="#FFFFFF" />
      <rect id="矩形 3" x="12.500000" y="0.500000" width="19.000000" height="7.000000" stroke="#d9d9d9" />
      <rect id="矩形 4" x="12.499985" y="9.500000" width="19.000000" height="14.000000" fill="#FFFFFF" />
      <rect id="矩形 4" x="12.499985" y="9.500000" width="19.000000" height="14.000000" stroke="#d9d9d9" />
      <rect id="矩形 5" x="12.500000" y="25.500000" width="19.000000" height="6.000000" fill="#FFFFFF" />
      <rect id="矩形 5" x="12.500000" y="25.500000" width="19.000000" height="6.000000" stroke="#d9d9d9" />
      <rect id="矩形 6" x="0.500000" y="0.500000" width="9.000000" height="31.000000" fill="#FFFFFF" />
      <rect id="矩形 6" x="0.500000" y="0.500000" width="9.000000" height="31.000000" stroke="#d9d9d9" />
    </svg>
  </div>
</template>
<style lang="less" scoped>
.widget-layout-type-select {
  display: flex;
  align-items: center;
  justify-content: space-evenly;
  svg {
    cursor: pointer;
    &:hover {
      fill: var(--w-primary-color-5);
    }
    &:hover,
    &.selected {
      rect {
        stroke-width: 0px;
      }
    }
    &.selected {
      fill: var(--w-primary-color);
    }
  }
}
</style>
<script type="text/babel">
export default {
  name: 'LayoutSelect',
  props: {
    value: String
  },
  components: {},
  computed: {},
  data() {
    return {
      selectedType: this.value
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    clickToSelect(type) {
      this.selectedType = type;
      this.$emit('input', type);
    }
  }
};
</script>
