<template>
  <a-row type="flex" class="process-header" v-if="showHeader">
    <a-col flex="auto">
      <template v-if="!hideTitle">
        <Icon v-if="titleIcon" :type="titleIcon" :size="24"></Icon>
        <span class="title">{{ $t('Widget.' + widget.id + '.title', widget.configuration.title) }}</span>
      </template>
    </a-col>
    <a-col flex="auto">
      <div style="text-align: right">
        <a-input-search v-if="search.keywordSearchEnable" allow-clear style="width: 200px" @search="searchWorkProcess"></a-input-search>
        <span :class="['btn_has_space']">
          <a-button
            v-if="allowLocate && search.locateCurrentTaskRecord"
            @click="locateCurrentTaskRecord"
            :class="[isSmall ? 'icon-only' : '']"
            :title="$t('WidgetWorkProcess.process.current', '当前')"
          >
            <Icon type="pticon iconfont icon-a-bujuzujianmaodian" v-if="isSmall"></Icon>
            <template v-else>{{ $t('WidgetWorkProcess.process.current', '当前') }}</template>
          </a-button>
          <a-button
            v-if="allowLocate && search.locateCurrentUserRecord"
            @click="locateCurrentUserRecord"
            :class="[isSmall ? 'icon-only' : '']"
            :title="$t('WidgetWorkProcess.process.my', '我的')"
          >
            <Icon type="pticon iconfont icon-oa-weituorenquxiang" v-if="isSmall"></Icon>
            <template v-else>{{ $t('WidgetWorkProcess.process.my', '我的') }}</template>
          </a-button>
          <a-button
            v-if="enabledSort && !sortAscending"
            @click="sortWorkProcessAscending"
            :class="[isSmall ? 'icon-only' : '']"
            :title="$t('WidgetWorkProcess.process.ascSort', '升序')"
          >
            <a-icon type="sort-ascending" />
            {{ isSmall ? '' : $t('WidgetWorkProcess.process.ascSort', '升序') }}
          </a-button>
          <a-button
            v-if="enabledSort && sortAscending"
            @click="sortWorkProcessDescending"
            :class="[isSmall ? 'icon-only' : '']"
            :title="$t('WidgetWorkProcess.process.descSort', '降序')"
          >
            <a-icon type="sort-descending" />
            {{ isSmall ? '' : $t('WidgetWorkProcess.process.descSort', '降序') }}
          </a-button>
        </span>
        <a-radio-group
          v-if="widget.configuration.allowSwitchDisplayStyle"
          v-model="widget.configuration.displayStyle"
          button-style="solid"
          style="margin-left: 8px"
        >
          <a-radio-button value="standard">{{ $t('WidgetWorkProcess.process.standardTimeProcess', '标准时间轴') }}</a-radio-button>
          <a-radio-button value="simple">{{ $t('WidgetWorkProcess.process.simpleTimeProcess', '简约时间轴') }}</a-radio-button>
          <a-radio-button value="table">{{ $t('WidgetWorkProcess.process.tableProcess', '表格') }}</a-radio-button>
        </a-radio-group>
      </div>
    </a-col>
  </a-row>
</template>

<script>
import { addElementResizeDetector } from '@framework/vue/utils/util';
export default {
  props: {
    widget: Object,
    bodyWidget: Object,
    allowLocate: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    showHeader() {
      return (
        !this.hideTitle ||
        this.search.keywordSearchEnable ||
        (this.allowLocate && (this.search.locateCurrentTaskRecord || this.search.locateCurrentUserRecord)) ||
        this.enabledSort ||
        this.widget.configuration.allowSwitchDisplayStyle
      );
    }
  },
  mounted() {
    let _this = this;
    addElementResizeDetector(this.$el.parentNode, e => {
      let width = _this.$el.parentNode.clientWidth;
      if (width > 460) {
        _this.isSmall = false;
      } else {
        _this.isSmall = true;
      }
    });
  },
  data() {
    let configuration = this.widget.configuration;
    return { sortAscending: true, ...configuration, isSmall: false };
  },
  methods: {
    searchWorkProcess(value) {
      this.$emit('searchWorkProcess', value);
      this.bodyWidget.searchWorkProcess(value);
    },
    locateCurrentTaskRecord() {
      this.bodyWidget.locateCurrentTaskRecord();
    },
    locateCurrentUserRecord() {
      this.bodyWidget.locateCurrentUserRecord();
    },
    sortWorkProcessAscending() {
      this.sortAscending = true;
      this.bodyWidget.sortWorkProcessAscending();
    },
    sortWorkProcessDescending() {
      this.sortAscending = false;
      this.bodyWidget.sortWorkProcessDescending();
    }
  }
};
</script>

<style lang="less" scoped>
.process-header {
  .title {
    color: var(--w-primary-color);
    font-weight: 700;
    font-size: var(--w-font-size-lg);
    line-height: 32px;
  }

  .button-group {
    button {
      padding-left: 4px;
      padding-right: 4px;
    }
  }
}
</style>
