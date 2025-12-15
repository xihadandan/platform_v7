<template>
  <div class="full-search-page-container" :style="pageBgStyle">
    <div class="full-search-page">
      <div class="full-search-head">
        <div class="_logo" v-if="pageConfig.enabledLogo">
          <img :src="pageConfig.logo" />
        </div>
        <div v-if="pageConfig.enabledTitle" class="ql-editor">
          <div class="_title" v-html="pageConfig.title"></div>
        </div>
      </div>
      <div style="width: var(--w-full-search-page-width); margin-bottom: 8px" v-if="setting.enabledCategory">
        <horizontal-scroller>
          <div class="search-category">
            <div
              :class="{
                'search-category-item': true,
                _active: searchInstance.categoryCode === item.value
              }"
              v-for="(item, index) in categoryOptions"
              :key="index"
              @click="handleCategory(item)"
            >
              {{ item.label }}
            </div>
          </div>
        </horizontal-scroller>
      </div>

      <!-- <a-input v-model="searchInstance.keyword" class="search-input" placeholder="请输入搜索内容" @pressEnter="handleSearch">
        <i class="iconfont icon-ptkj-sousuochaxun" slot="prefix" />
        <span slot="suffix" @click.stop="handleSearch">搜索</span>
      </a-input> -->
      <full-search-input :meta="searchInstance" :setting="setting" :hasCategorySelect="false" class="search-input" @search="handleSearch" />

      <div class="search-page-bottom" v-if="false">
        <div class="_title">热门搜索</div>
        <div class="full-search-hot">
          <div class="search-hot-item" v-for="(item, index) in hotList" :key="index">{{ item.label }}</div>
        </div>
        <div class="_title search-history">
          <span>搜索历史</span>
          <a-button type="link" size="small">
            <Icon type="pticon iconfont icon-xmch-qingkongqingchu" />
            清空
          </a-button>
        </div>
        <div class="full-search-history">
          <div class="search-history-item" v-for="(item, index) in historyList" :key="index">
            <span>
              <i class="iconfont icon-oa-zuijinfangwen" />
              {{ item.label }}
            </span>
            <i class="_close iconfont icon-ptkj-dacha" />
          </div>
        </div>
        <div class="_title">常用搜索</div>
        <div class="full-search-common">
          <div class="search-common-item" v-for="(item, index) in hotList" :key="index">{{ item.label }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import mixin from './mixin';
import FullSearchInput from './search-input.vue';
import HorizontalScroller from './horizontal-scroller.vue';

export default {
  name: 'FullSearchPage',
  mixins: [mixin],
  data() {
    return {
      pageConfig: this.setting.pageConfig,
      hotList: [
        { label: '人工智能', value: 'ai' },
        { label: '规则制度', value: 'zhidu' }
      ],
      historyList: [
        { label: '7.0使用手册', value: 'ai' },
        { label: '威搭工作台', value: 'ai' },
        { label: '低代码平台', value: 'ai' },
        { label: '会议室管理', value: 'ai' },
        { label: '人工智能', value: 'ai' }
      ]
    };
  },
  components: {
    FullSearchInput,
    HorizontalScroller
  },
  computed: {
    pageBgStyle() {
      let style = {};
      if (this.setting && this.setting.interactiveMode === 'page') {
        const pageConfig = this.setting.pageConfig;
        if (pageConfig.backgroundColor) {
          style.backgroundColor = `var(${pageConfig.backgroundColor})`;
        }
        if (pageConfig.backgroundImage) {
          style.backgroundImage = `url(${pageConfig.backgroundImage})`;
        }
        if (pageConfig.backgroundPosition) {
          style.backgroundPosition = pageConfig.backgroundPosition;
        }
        if (pageConfig.backgroundRepeat) {
          style.backgroundRepeat = pageConfig.backgroundRepeat;
        }
      }
      return style;
    },
    scopeList() {
      let list = [];
      if (this.setting.scopeList) {
        this.setting.scopeList.forEach(item => {
          list.push(item);
        });
      }
      return list;
    }
  },
  methods: {
    // 更改分类
    handleCategory(item) {
      this.searchInstance.categoryCode = item.value;
    },
    handleSearch() {
      this.$parent.showResult = true;
    }
  }
};
</script>
