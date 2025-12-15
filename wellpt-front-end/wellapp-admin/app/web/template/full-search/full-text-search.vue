<template>
  <div
    :class="{
      'full-search-container': true
    }"
  >
    <template v-if="setting">
      <full-search-page :setting="setting" v-if="!showResult" />
      <full-search-result :setting="setting" v-if="showResult" />
    </template>
    <a-drawer
      :visible="visible"
      :title="drawerTitle"
      width="66%"
      wrapClassName="search-result-detail-drawer"
      :destroyOnClose="true"
      @close="closeDrawer"
    >
      <div class="spin-center" v-if="spinning">
        <a-spin />
      </div>
      <iframe
        v-show="!spinning"
        :src="viewerUrl"
        id="viewFrame"
        :style="{
          border: 'none',
          width: '100%',
          height: '100%'
        }"
      />
    </a-drawer>
  </div>
</template>

<script>
import FullTextSearch from './FullTextSearch';
import mixin from './full-text-search/mixin';
import { fetchSearch } from './api';
import { getElMaxHeightFromViewport } from '@framework/vue/utils/util';
import FullSearchPage from './full-text-search/search-page.vue';
import FullSearchResult from './full-text-search/search-result.vue';

export default {
  name: 'FullTextSearch',
  inject: ['pageContext', 'fullSearchDefinition', '$event'],
  // mixins: [mixin],
  data() {
    const searchInstance = new FullTextSearch();
    return {
      fetchedSetting: false,
      responseData: null,
      setting: null,
      showResult: false,
      searchInstance,

      spinning: true,
      visible: false,
      drawerTitle: '',
      viewerUrl: ''
    };
  },
  provide() {
    return {
      searchInstance: this.searchInstance
    };
  },
  components: {
    FullSearchPage,
    FullSearchResult
  },
  created() {
    this.pageContext.handleEvent('openSearchDetailDrawer', this.openDrawer);
  },
  beforeMount() {
    this.init();
  },
  mounted() {
    this.setContainerHeight();
  },
  methods: {
    getQueryString(name, defaultValue) {
      var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
      var values = window.location.search.substr(1).match(reg);
      if (values != null) {
        return decodeURIComponent(values[2]);
      }
      if (defaultValue != null) {
        return defaultValue;
      }
      return '';
    },
    init() {
      if (this.fullSearchDefinition) {
        this.setting = this.fullSearchDefinition;
        if (this.setting.interactiveMode === 'default') {
          this.showResult = true;
        }
        if (window && window.location) {
          const showResult = this.getQueryString('showResult');
          const booleanMap = {
            true: true,
            false: false
          };
          if (showResult !== null && booleanMap[showResult] !== undefined) {
            this.showResult = booleanMap[showResult];
          }
        }
        if (this.$event) {
          this.searchInstance.keyword = this.$event.meta.keyword;
          this.searchInstance.categoryCode = this.$event.meta.categoryCode;
        }
        this.fetchedSetting = true;
      } else {
        fetchSearch().then(res => {
          this.responseData = res;
          if (res.definitionJson) {
            this.setting = JSON.parse(res.definitionJson);
          }
          if (this.setting.interactiveMode === 'default') {
            this.showResult = true;
          }

          this.fetchedSetting = true;
        });
      }
    },
    // 设置容器高度
    setContainerHeight() {
      const { maxHeight } = getElMaxHeightFromViewport(this.$el);
      this.$el.style.cssText += `;height:${maxHeight}px`;
    },
    // 打开搜索结果详情抽屉
    openDrawer(item) {
      let that = this;
      this.visible = true;
      this.drawerTitle = item.titleStr;
      this.$nextTick(() => {
        const viewFrame = document.querySelector('#viewFrame');
        viewFrame.onload = function () {
          that.spinning = false;
        };
      });
      this.viewerUrl = item.url;
      // viewFrame.contentWindow.location.reload();
    },
    // 关闭搜索结果详情抽屉
    closeDrawer() {
      this.spinning = true;
      this.visible = false;
    }
  }
};
</script>

<style lang="less">
@import url(./style/full-text-search.less);
</style>
