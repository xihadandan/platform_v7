<template>
  <span :title="title">
    <a-badge class="icon-set-badge">
      <a-icon
        v-if="!required && mValue"
        @click.stop="mValue = undefined"
        slot="count"
        type="close-circle"
        theme="filled"
        class="close-icon"
        title="删除图标"
      />
      <a-avatar
        v-if="!mValue"
        shape="square"
        style="background-color: #ffffff; cursor: pointer; border: 1px #cccccc dashed; color: #cccccc"
        :size="size"
      >
        <Icon slot="icon" type="iconfont icon-ptkj-jiahao"></Icon>
      </a-avatar>
      <a-avatar
        v-else-if="!valueJson || (valueJson && !valueJson.showBackground)"
        shape="square"
        style="background-color: #ffffff; color: var(--w-text-color-dark); cursor: pointer"
        :size="size"
      >
        <Icon slot="icon" :type="mValue" :style="{ 'font-size': fontSize + 'px' }"></Icon>
      </a-avatar>
      <Icon v-else :type="mValue" :style="{ 'font-size': fontSize + 'px', cursor: pointer }" :size="size"></Icon>
    </a-badge>
  </span>
</template>
<script>
export default {
  name: 'IconSetBadge',
  props: {
    value: {
      type: [String, Object],
      default: ''
    },
    required: {
      type: Boolean,
      default: false
    },
    onlyIconClass: Boolean,
    title: {
      type: String,
      default: '选择图标'
    },
    size: {
      type: Number,
      default: 32
    },
    fontSize: {
      type: Number,
      default: 20
    }
  },
  data() {
    return {
      mValue: this.value
    };
  },
  watch: {
    mValue: {
      handler(v, o) {
        if (v !== o) {
          this.emitChange();
        }
      }
    },
    value(v) {
      if (v != this.mValue) {
        this.mValue = v;
      }
    }
  },
  computed: {
    valueJson() {
      if (this.isValidJSON) {
        return JSON.parse(this.mValue);
      }
      return undefined;
    },
    isValidJSON() {
      try {
        if (!this.mValue) {
          return false;
        }
        JSON.parse(this.mValue);
        return true;
      } catch (e) {
        return false;
      }
    }
  },
  methods: {
    emitChange() {
      this.$emit('input', this.mValue);
      this.$emit('change', this.mValue);
      // 兼容旧API
      this.$emit('iconSelected', this.mValue);
      // console.log(this.mValue);
    }
  }
};
</script>
<style lang="less" scoped>
.icon-set-badge {
  cursor: pointer;
  > .close-icon {
    color: #f5222d;
    top: 3px;
    opacity: 0;
  }
  &:hover {
    > .close-icon {
      opacity: 1;
    }
  }
}
</style>
