<template>
  <div style="position: relative">
    <a-card class="metadata-data-dictionary preview-card" :bordered="false">
      <template slot="title">
        <span class="title">{{ metadata.title }}</span>
      </template>
      <a-row type="flex" class="f_nowrap">
        <a-col flex="224px" class="sidebar" style="height: calc(100vh - 124px); border-right: 1px solid var(--w-border-color-light)">
          <DataDictionaryCategoryNav @click="onCategoryClick" @load="onCategoryLoad"></DataDictionaryCategoryNav>
          <a-button size="large" class="ref-btn" block :type="refVisible ? 'primary' : ''" @click="showDataDictionaryRefList">
            <Icon type="pticon iconfont icon-wsbs-woyaoxue"></Icon>
            引用数据字典
            <a-badge
              style="float: right; margin-top: 9px"
              :count="refCount"
              showZero
              :number-style="{
                backgroundColor: 'var(--metadata-ref-btn-number-bg)',
                color: 'var(--metadata-ref-btn-number-color)',
                boxShadow: '0 0 0 2px rgba(255,255,255,0.4)',
                minWidth: '32px',
                fongWeight: 'bold'
              }"
            />
          </a-button>
        </a-col>
        <a-col flex="auto" style="width: calc(100% - 225px)">
          <DataDictionaryList
            v-show="!refVisible"
            ref="dataDictionaryList"
            :categoryUuid="selectedCategoryUuid"
            :categories="categories"
          ></DataDictionaryList>
          <keep-alive>
            <DataDictionaryRefList
              v-if="refVisible"
              ref="dataDictionaryRefList"
              :supportsGeneric="true"
              :refCount="refCount"
            ></DataDictionaryRefList>
          </keep-alive>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import DataDictionaryCategoryNav from './data-dictionary-category-nav.vue';
import DataDictionaryList from './data-dictionary-list.vue';
import DataDictionaryRefList from './data-dictionary-ref-list.vue';
export default {
  props: {
    metadata: Object
  },
  components: { Modal, DataDictionaryCategoryNav, DataDictionaryList, DataDictionaryRefList },
  inject: ['currentModule'],
  data() {
    return {
      categories: [],
      selectedCategoryUuid: '',
      refVisible: false,
      refCount: 0
    };
  },
  created() {
    this.loadDataDictionaryRefCount();
  },
  methods: {
    onCategoryClick(categoryUuid) {
      this.selectedCategoryUuid = categoryUuid;
      this.refVisible = false;
      this.$nextTick(() => {
        this.$refs.dataDictionaryList.refresh();
      });
    },
    onCategoryLoad(categories) {
      this.categories = categories;
    },
    loadDataDictionaryRefCount() {
      $axios
        .post(`/proxy/api/datastore/loadCount`, {
          dataStoreId: 'CD_DS_DATA_DICTIONARY_REF',
          params: {
            moduleId: this.currentModule && this.currentModule.id,
            distinct: true
          }
        })
        .then(({ data }) => {
          if (data.data) {
            this.refCount = parseInt(data.data);
          }
        });
    },
    showDataDictionaryRefList() {
      this.refVisible = true;
    }
  }
};
</script>

<style lang="less">
.metadata-data-dictionary {
  height: 100%;

  .sidebar {
    background-color: #ffffff;
    padding: 20px 12px;

    .ref-btn {
      text-align: left;
      margin-top: 12px;

      --metadata-ref-btn-number-bg: var(--w-primary-color);
      --metadata-ref-btn-number-color: #ffffff;
      i {
        color: var(--w-primary-color);
      }

      &.ant-btn-primary {
        --metadata-ref-btn-number-bg: #ffffff;
        --metadata-ref-btn-number-color: var(--w-primary-color);
        i {
          color: inherit;
        }
      }
    }
  }

  .category-container {
    .ant-menu-submenu-arrow {
      left: 5px;
    }
  }
}
</style>
