<template>
  <a-popover
    v-model="modalVisible"
    :placement="placement"
    :align="align"
    @visibleChange="visibleChange"
    :getPopupContainer="getPopupContainer"
    overlayClassName="process-designer-select-type-popover"
  >
    <div slot="content" class="process-designer-select-type" :class="layout == 'horizontal' ? 'flex horizontal' : 'vertical'">
      <div
        v-for="(item, index) in nodeTypeOptions"
        class="type-item"
        @click.stop="selectedType(item)"
        :class="{ selected: selectType == item.value }"
      >
        <Icon :type="item.icon" style="font-size: var(--w-font-size-xl)"></Icon>
        <div>{{ item.text }}</div>
      </div>
    </div>
  </a-popover>
</template>

<script>
let allTypeOptions = [
  { text: '子阶段', value: '1', icon: 'pticon iconfont icon-ptkj-zuzhiguanli' },
  { text: '事项', value: '2', icon: 'pticon iconfont icon-oa-chulishixiang' }
];
let DEFAULT_DATA = { nodeType: '1', refMode: 'none', count: 3, childNode: true };
export default {
  props: {
    designer: Object,
    title: String,
    type: String,
    ok: Function,
    getPopupContainer: {
      type: Function,
      default: () => document.body
    }
  },
  inject: ['designer'],
  data() {
    const _this = this;
    return {
      modalVisible: false,
      nodeTypeOptions: allTypeOptions,
      selectType: this.type || '1',
      placement: 'bottom',
      layout: '',
      position: ''
    };
  },
  created() {},
  mounted() {},
  computed: {
    align() {
      if (this.placement == 'bottom') {
        return { offset: [90, 6] };
      } else if (this.placement == 'right') {
        if (this.isFirefox) {
          // 火狐的位移比谷歌高一些
          return { offset: [60, -75] };
        }
        return { offset: [60, -90] };
      }
    },
    isFirefox() {
      return /firefox/i.test(navigator.userAgent);
    }
  },
  methods: {
    open(layout, position) {
      const _this = this;
      this.layout = layout;
      this.position = position;
      if (layout == 'horizontal') {
        this.placement = 'bottom';
      } else {
        this.placement = 'right';
      }
      _this.modalVisible = true;
    },
    selectedType(item) {
      this.selectType = item.value;
      this.modalVisible = false;
      if (typeof this.ok === 'function') {
        this.ok(this.layout, this.position, this.selectType);
      }
    }
  }
};
</script>

<style lang="less">
.process-designer-select-type-popover {
  .ant-popover-inner-content {
    padding: 4px;

    .process-designer-select-type {
      .type-item {
        width: 52px;
        height: 52px;
        border-radius: 4px;
        color: var(--w-text-color-dark);
        font-size: 12px;
        text-align: center;
        padding: 4px 0;
        cursor: pointer;

        &.selected {
          color: var(--w-primary-color);
          background-color: var(--w-primary-color-1);
        }

        &:hover {
          background-color: var(--w-primary-color-1);
        }
      }

      &.horizontal {
        .type-item:first-child {
          margin-right: 4px;
        }
      }
      &.vertical {
        .type-item:first-child {
          margin-bottom: 4px;
        }
      }
    }
  }
}
</style>
