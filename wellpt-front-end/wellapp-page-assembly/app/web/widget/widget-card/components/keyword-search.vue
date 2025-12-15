<template>
  <div
    :class="{
      '_keyword-search-container': true,
      'search-design-container': designMode
    }"
  >
    <template v-if="visibleKeywordSearch">
      <a-input
        class="_keyword-search"
        v-model="keywordSearch"
        :allowClear="true"
        :placeholder="$t('Widget.' + widget.id + '.keywordSearchPlaceholder', widget.configuration.keywordSearchPlaceholder)"
        @change="onChangeKeywordSearch"
        @pressEnter="handleKeywordSearch"
        @blur="onBlurKeywordSearch"
        @focus="onFocusKeywordSearch"
      >
        <Icon slot="suffix" :type="widget.configuration.keywordSearchIcon" @click.stop="handleKeywordSearch" />
      </a-input>
    </template>
  </div>
</template>

<script>
export default {
  name: 'KeywordSearch',
  inject: ['pageContext', 'designMode', 'widgetContext'],
  props: {
    widget: {
      type: Object,
      default: () => {}
    },
    configuration: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      // visibleKeywordSearch: false,
      keywordSearch: ''
    };
  },
  computed: {
    visibleKeywordSearch() {
      let visible = false;
      if (
        this.widget.configuration.enableKeywordSearch &&
        this.widget.configuration.keywordSearchWidgetIds &&
        this.widget.configuration.keywordSearchWidgetIds.length
      ) {
        visible = true;
      }
      return visible;
    }
  },
  watch: {
    visibleKeywordSearch: {
      immediate: true,
      handler: function (newValue) {
        this.providVisible(newValue);
      }
    }
  },
  methods: {
    providVisible(visible) {
      if (!this.widget.configuration.keywordSearchWidgetIds) {
        return;
      }
      this.widget.configuration.keywordSearchWidgetIds.map(id => {
        this.widgetContext._provided[`${id}:visibleKeywordSearch`] = visible;
      });
    },
    onBlurKeywordSearch(event) {
      if (this.designMode) {
        return;
      }
      event.target.classList.remove('_visible');
    },
    onFocusKeywordSearch(event) {
      if (this.designMode) {
        return;
      }
      event.target.classList.add('_visible');
    },
    onChangeKeywordSearch() {
      if (this.designMode) {
        return;
      }
      this.$nextTick(() => {
        if (!this.keywordSearch) {
          this.handleKeywordSearch();
        }
      });
    },
    handleKeywordSearch() {
      if (this.designMode) {
        return;
      }
      this.widget.configuration.keywordSearchWidgetIds.map(id => {
        this.pageContext.emitEvent(`${id}:keywordSearch`, this.keywordSearch);
      });
    }
  }
};
</script>

<style lang="less">
._keyword-search-container {
  display: inline-flex;
  align-items: center;
}
.ant-input-affix-wrapper {
  &._keyword-search {
    overflow: hidden;
    .ant-input {
      opacity: 0;
      transform: translate3d(100%, 0px, 0px);
    }
    .ant-input-suffix {
      cursor: pointer;
      .iconfont {
        font-size: 16px;
      }
      .ant-input-clear-icon {
        opacity: 0;
      }
    }
    &:hover {
      .ant-input {
        opacity: 1;
        transform: translate3d(0, 0px, 0px);
      }
      .ant-input-clear-icon {
        opacity: 1;
      }
    }
    &:has(> ._visible) {
      .ant-input {
        opacity: 1;
        transform: translate3d(0, 0px, 0px);
      }
      .ant-input-clear-icon {
        opacity: 1;
      }
    }
  }
}
.search-design-container {
  .ant-input-affix-wrapper {
    &._keyword-search {
      .ant-input,
      .ant-input-clear-icon {
        opacity: 0;
      }
    }
  }
}
</style>
