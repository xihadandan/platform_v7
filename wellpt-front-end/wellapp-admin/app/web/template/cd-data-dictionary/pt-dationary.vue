<template>
  <div class="pt-dictionary">
    <a-row type="flex" class="template-widget-row">
      <a-col flex="240px" class="widget-col">
        <DataDictionaryCategoryNav
          class="sidebar"
          :allowCreate="false"
          type="buildIn"
          @click="onCategoryClick"
          @load="onCategoryLoad"
          style="padding: 12px 20px"
        ></DataDictionaryCategoryNav>
      </a-col>
      <a-col flex="auto" class="widget-col" style="width: 0">
        <DataDictionaryList
          v-if="categories && categories.length > 0"
          ref="dataDictionaryList"
          :categoryUuid="selectedCategoryUuid"
          categoryType="buildIn"
          :categories="categories"
        ></DataDictionaryList>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import { getElSpacingForTarget } from '@framework/vue/utils/util';
import DataDictionaryCategoryNav from '@pageAssembly/app/web/page/module-center/component/metadata/data-dictionary/data-dictionary-category-nav.vue';
import DataDictionaryList from '@pageAssembly/app/web/page/module-center/component/metadata/data-dictionary/data-dictionary-list.vue';
export default {
  components: { DataDictionaryCategoryNav, DataDictionaryList },
  provide() {
    return {
      currentModule: { id: '' }
    };
  },
  data() {
    return {
      categories: [],
      selectedCategoryUuid: ''
    };
  },
  created() {},
  mounted() {
    this.changeTableStyle();
  },
  methods: {
    onCategoryClick(categoryUuid) {
      this.selectedCategoryUuid = categoryUuid;
      this.$nextTick(() => {
        this.$refs.dataDictionaryList.refresh();
      });
    },
    onCategoryLoad(categories) {
      this.categories = categories;
    },
    changeTableStyle() {
      if (this.$el.closest('.grid-in-layout-widget')) {
        this.$root.pageContext.emitEvent(`SET_TEMPLATE_WIDGET-ROW-COL-HEIGHT`, {
          callback: () => {
            this.$nextTick(() => {
              setTimeout(() => {
                let $el = this.$refs.dataDictionaryList.$el;
                let { maxHeight } = getElSpacingForTarget($el, this.$el);
                if (maxHeight) {
                  maxHeight = maxHeight;
                  $el.style.cssText += `height:${maxHeight}px;`;
                }
              }, 200);
            });
          }
        });
      }
    }
  }
};
</script>
