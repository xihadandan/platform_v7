<!-- 全文搜索输入框 -->
<template>
  <div class="full-search-input">
    <a-input
      v-model="meta.keyword"
      :placeholder="placeholder"
      ref="input"
      @focus="onFocus"
      @blur="onBlur"
      @change="onChange"
      @pressEnter="handleSearch"
    >
      <template v-if="setting.enabledCategory && hasCategorySelect">
        <a-select :options="categoryOptions" v-model="meta.categoryCode" slot="addonBefore" />
      </template>
      <i class="iconfont icon-ptkj-sousuochaxun" slot="prefix" />
      <template slot="suffix">
        <div tabindex="0" @click="handleReset">
          <a-icon v-show="showClear" type="close-circle" theme="filled" class="ant-input-clear-icon" />
        </div>
        <span tabindex="0" @click.stop="handleSearch">搜索</span>
      </template>
    </a-input>
  </div>
</template>

<script>
import mixin from './mixin';

export default {
  name: 'FullSearchInput',
  mixins: [mixin],
  props: {
    meta: {
      type: Object,
      default: () => {}
    },
    allowClear: {
      type: Boolean,
      default: true
    },
    placeholder: {
      type: String,
      default: '请输入搜索内容'
    },
    hasCategorySelect: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    showClear() {
      let show = false;
      if (this.allowClear) {
        if (this.meta && this.meta.keyword) {
          show = true;
        }
      }
      return show;
    }
  },
  updated() {
    // let clearEl = this.$el.querySelector('.ant-input-clear-icon');
    // if (clearEl) {
    //   clearEl.setAttribute('tabIndex', '0');
    // }
  },
  methods: {
    onChange() {},
    handleReset() {
      this.meta.keyword = '';
    },
    handleSearch(event) {
      this.$emit('search');
    },
    focus() {
      this.$refs.input.focus();
    },
    onFocus() {
      this.$emit('focus');
    },
    onBlur() {
      this.$emit('blur');
    }
  }
};
</script>

<style lang="less">
.full-search-input {
  .ant-input-group-addon {
    &:first-child {
      border-top-left-radius: 8px;
      border-bottom-left-radius: 8px;
      background-color: var(--w-color-white);
    }
  }
  .ant-input {
    height: 40px;
    border-top-right-radius: 8px;
    border-bottom-right-radius: 8px;
    &:not(:last-child) {
      padding-right: 90px;
    }
  }
  .ant-input-prefix {
    .icon-ptkj-sousuochaxun {
      color: var(--w-gray-color-7);
    }
  }
  .ant-input-suffix {
    right: 4px;
    > span {
      display: inline-block;
      width: 60px;
      height: 32px;
      text-align: center;
      line-height: 32px !important;
      color: #fff;
      border-radius: 8px;
      background-color: var(--w-primary-color);
      cursor: pointer;
    }
    .ant-input-clear-icon {
      margin-right: 7px;
    }
  }
}
</style>
