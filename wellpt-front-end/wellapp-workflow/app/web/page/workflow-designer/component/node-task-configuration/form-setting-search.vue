<template>
  <span :class="[isShow ? '' : 'wf-form-search']">
    <a-input-search
      v-show="showSearch"
      v-model="searchVal"
      ref="inputRef"
      class="wf-form-search-input"
      :allowClear="true"
      @search="onSearch"
      @blur="searchInputBlur"
    />
    <a-button
      v-if="!isShow"
      type="link"
      size="small"
      v-show="!showSearch"
      icon="search"
      @click="onClick"
      style="margin-right: 6px"
    ></a-button>
  </span>
</template>

<script>
export default {
  name: 'FormSettingSearch',
  props: {
    isShow: {
      type: null
    }
  },
  components: {},
  data() {
    let showSearch = this.isShow || false;
    return {
      showSearch,
      searchVal: ''
    };
  },
  created() {},
  methods: {
    searchInputBlur() {
      if (!this.searchVal) {
        this.showSearch = false;
      }
    },
    onClick() {
      this.showSearch = true;
      this.$nextTick(() => {
        this.$refs.inputRef.focus();
      });
    },
    onSearch() {
      this.$emit('search', this.searchVal);
    }
  }
};
</script>
<style lang="less" scope>
.wf-form-search {
  line-height: 34px;
  .wf-form-search-input {
    width: 150px;
  }
}
</style>
