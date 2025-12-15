<template>
  <div
    :class="{
      'scroll-wrapper': true,
      'is-not-overflow': !isOverflow
    }"
  >
    <!-- 左按钮 -->
    <div class="scroll-btn-area left" v-if="showBtn">
      <button class="scroll-btn" :disabled="scrollX === 0" @click="scrollPrev">
        <Icon type="pticon iconfont icon-ptkj-xianmiaojiantou-zuo" />
      </button>
    </div>

    <!-- 滚动内容 -->
    <div class="scroll-container" ref="scrollContainer">
      <div class="scroll-content" :style="{ transform: `translate3d(-${scrollX}px, 0, 0)` }" ref="scrollContent">
        <slot></slot>
      </div>
    </div>

    <!-- 右按钮 -->
    <div class="scroll-btn-area right" v-if="showBtn">
      <button class="scroll-btn" :disabled="scrollX >= maxScroll" @click="scrollNext">
        <Icon type="pticon iconfont icon-ptkj-xianmiaojiantou-you" />
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FullVisibleScroll',
  data() {
    return {
      scrollX: 0,
      containerWidth: 0,
      contentWidth: 0
    };
  },
  computed: {
    maxScroll() {
      return Math.max(0, this.contentWidth - this.containerWidth);
    },
    isOverflow() {
      return this.contentWidth > this.containerWidth;
    },
    showBtn() {
      return this.isOverflow;
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initObserver();
      this.calculateWidths();
    });
    window.addEventListener('resize', this.calculateWidths);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.calculateWidths);
    if (this.resizeObserver) {
      this.resizeObserver.disconnect();
    }
  },
  updated() {
    // 异步内容更新后重新计算
    this.$nextTick(() => {
      this.calculateWidths();
    });
  },
  methods: {
    initObserver() {
      if (window.ResizeObserver) {
        this.resizeObserver = new ResizeObserver(() => {
          this.calculateWidths();
        });
        if (this.$refs.scrollContent) {
          this.resizeObserver.observe(this.$refs.scrollContent);
        }
      } else {
        // 不支持 ResizeObserver 时用延迟计算兜底
        setTimeout(this.calculateWidths, 100);
      }
    },
    calculateWidths() {
      const container = this.$refs.scrollContainer;
      const content = this.$refs.scrollContent;
      if (!container || !content) return;

      this.containerWidth = container.clientWidth;
      this.contentWidth = content.scrollWidth;

      // 避免 scrollX 超出最大值
      if (this.scrollX > this.maxScroll) {
        this.scrollX = this.maxScroll;
      }
    },
    scrollPrev() {
      const distance = Math.min(this.scrollX, this.containerWidth);
      this.scrollX -= distance;
    },
    scrollNext() {
      const remaining = this.maxScroll - this.scrollX;
      const distance = Math.min(this.containerWidth, remaining);
      this.scrollX += distance;
    }
  }
};
</script>

<style lang="less" scoped>
.scroll-wrapper {
  display: flex;
  align-items: center;
}
.scroll-btn-area {
  width: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.scroll-container {
  flex: 1;
  overflow: hidden;
}
.scroll-content {
  display: flex;
  white-space: nowrap;
  transition: transform 0.3s ease;
  will-change: transform;
}
.scroll-btn {
  width: 32px;
  height: 32px;
  padding: 0;
  font-size: 16px;
  border: none;
  border-radius: 4px;
  background-color: var(--w-gray-color-5);
  color: var(--w-gray-color-11);
  cursor: pointer;
  // outline: none;
  transition: all 0.2s;
}
.scroll-btn:disabled {
  color: var(--w-button-font-color-disabled);
  background-color: var(--w-button-background-disabled);
  cursor: not-allowed;
}
</style>
