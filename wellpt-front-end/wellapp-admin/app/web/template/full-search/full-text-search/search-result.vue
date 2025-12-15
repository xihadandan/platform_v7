<!-- 全文检索搜索结果 -->
<template>
  <div class="full-search-result">
    <div class="_header">
      <div class="_left">
        <div class="_logo" v-if="config.logo">
          <img :src="config.logo" />
        </div>
        <!-- <a-input v-model="searchInstance.keyword" class="_input full-search-input" @pressEnter="handleSearch">
          <template v-if="setting.enabledCategory" slot="addonBefore">
            <a-select :options="categoryOptions" v-model="searchInstance.categoryCode" />
          </template>
          <i class="iconfont icon-ptkj-sousuochaxun" slot="prefix" />
          <span slot="suffix" @click.stop="handleSearch">搜索</span>
        </a-input> -->
        <full-search-input :meta="searchInstance" :setting="setting" class="_input" @search="handleSearch" />
      </div>
      <!-- <a-button type="link" size="small">
        <Icon type="pticon iconfont icon-ptkj-shezhi" />
      </a-button> -->
    </div>
    <div class="_content">
      <div class="_content-head">
        <div class="result-total-tips">找到了{{ pagination.total }}个相关结果 ({{ diffSeconds }}秒)</div>
        <div class="_content-head-right">
          <a-popover
            v-model="visibleFilter"
            trigger="click"
            placement="bottomLeft"
            :arrowPointAtCenter="true"
            :getPopupContainer="getFilterContainer"
            overlayClassName="result-filter-popover"
          >
            <a-button type="link" size="small">
              <Icon type="pticon iconfont icon-ptkj-shezhi" />
              筛选
            </a-button>
            <div class="result-filter-container" slot="content">
              <a-form-model :labelCol="{ flex: '70px' }" :wrapperCol="{ flex: 'auto' }">
                <a-form-model-item label="创建人">
                  <OrgSelect
                    v-model="filterData.creator"
                    v-if="defaultOrgVersion"
                    :orgVersionId="defaultOrgVersion.id"
                    :orgIdOptions="{ xxx: [] }"
                    :orgType="['MyOrg']"
                    :getContainer="() => $el.querySelector('.result-filter-popover')"
                  />
                </a-form-model-item>
                <a-form-model-item label="类型选择">
                  <a-radio-group v-model="filterData.dateRangeField" :options="filterTimeTypeOptions" />
                </a-form-model-item>
                <a-form-model-item label="搜索时间">
                  <a-select :options="dateRangeOptions" v-model="filterData.dateRange" />
                </a-form-model-item>
                <a-form-model-item v-show="filterData.dateRange === 'CUSTOM'">
                  <template #label></template>
                  <a-range-picker
                    :format="defaultPattern"
                    :valueFormat="defaultPattern"
                    :showTime="false"
                    :disabledDate="disabledDate"
                    v-model="rangeDateStrings"
                    @change="changeCustomDateRange"
                  />
                </a-form-model-item>
              </a-form-model>
              <div style="padding-top: 10px; text-align: right">
                <a-button @click="visibleFilter = false">取消</a-button>
                <a-button @click="handleResetFilter">重置</a-button>
                <a-button type="primary" @click="handleSaveFilter">保存</a-button>
              </div>
            </div>
          </a-popover>
          <a-popover
            v-model="visibleOrder"
            trigger="click"
            placement="bottomLeft"
            :arrowPointAtCenter="true"
            :getPopupContainer="triggerNode => triggerNode.parentNode"
            overlayClassName="result-order-popover"
          >
            <a-button type="link" size="small">
              <Icon type="pticon iconfont icon-ptkj-shezhi" />
              排序
            </a-button>
            <template slot="content">
              <div
                :class="{
                  'result-order-item': true,
                  _active: activeOrder === item.value
                }"
                v-for="(item, index) in orderOptions"
                :key="index"
                @click="handleOrder(item)"
              >
                {{ item.label }}
              </div>
            </template>
          </a-popover>
        </div>
      </div>
      <div style="width: var(--w-full-search-result-width); margin-bottom: 12px" v-if="setting.enabledCategory">
        <horizontal-scroller>
          <div class="_result-category">
            <div
              :class="{
                '_category-item': true,
                _active: searchInstance.categoryCode === item.value
              }"
              v-for="item in categoryOptions"
              :key="item.value"
              @click="handleCategory(item)"
            >
              {{ item.label }}
              <template v-if="item.value">
                {{ `${categoryCountMap[item.value] ? `（${categoryCountMap[item.value]}）` : ''}` }}
              </template>
            </div>
          </div>
        </horizontal-scroller>
      </div>
      <div class="result-list-container">
        <div>
          <a-skeleton :loading="loading"></a-skeleton>
          <div class="result-item-list">
            <result-item v-for="(item, index) in dataList" :key="item.uuid" :itemData="item" :setting="setting" />
          </div>
          <div class="pagination-container" v-show="dataList.length">
            <a-pagination
              :showQuickJumper="pagination.showQuickJumper"
              :showSizeChanger="pagination.showSizeChanger"
              :current="pagination.current"
              :total="pagination.total"
              :pageSize="pagination.pageSize"
              :showTotal="pagination.showTotal"
              :showLessItems="true"
              :pageSizeOptions="pageSizeOptions"
              @change="changePage"
              @showSizeChange="changePageSize"
            />
          </div>
          <a-empty v-show="!loading && !dataList.length" />
        </div>
        <div v-if="config.enabledColumns">
          <div class="result-file-list" v-if="fileList.length">
            <file-item v-for="(item, index) in fileList" :key="index" :itemData="item" :setting="setting" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import moment from 'moment';
import mixin from './mixin';
import { fetchQueryFulltextIndex, fetchDefaultOrgVersion } from '../api';
import { dateRangeOptions, filterTimeTypeOptions, orderOptions } from '../FullTextSearch';
import FullSearchInput from './search-input.vue';
import ResultItem from './result-item.vue';
import FileItem from './file-item.vue';
import HorizontalScroller from './horizontal-scroller.vue';
import OrgSelect from '@admin/app/web/lib/org-select.vue';

export default {
  name: 'FullSearchResult',
  mixins: [mixin],
  components: {
    FullSearchInput,
    ResultItem,
    FileItem,
    HorizontalScroller,
    OrgSelect
  },
  data() {
    return {
      defaultPattern: 'yyyy-MM-DD',
      dateRangeOptions,
      filterTimeTypeOptions,
      orderOptions,

      config: null,
      activeOrder: '',
      visibleOrder: false,
      visibleFilter: false,
      dataList: [],
      categoryCountMap: {},
      pagination: {
        showQuickJumper: true,
        showSizeChanger: true,
        current: 1,
        total: 0,
        pageSize: 10,
        showTotal: function (total, range) {
          const totalPages = total % this.pageSize === 0 ? parseInt(total / this.pageSize) : parseInt(total / this.pageSize + 1);
          return `共 ${totalPages} 页/ ${total} 条记录`;
        }
      },
      pageSizeOptions: ['10', '20', '30', '40'],
      fileList: [],
      // keywords: '',
      // activeCategory: 'all',
      rangeDateStrings: [],
      filterData: {
        creator: '',
        dateRangeField: 'createTime',
        dateRange: dateRangeOptions[0]['value'],
        startTime: '',
        endTime: ''
      },
      diffSeconds: '',
      loading: false,
      defaultOrgVersion: undefined
    };
  },
  created() {
    this.config = this.setting.resultConfig;
    const pageSize = this.getPageSize();
    this.pagination.pageSize = Number(pageSize);
    if (!this.pageSizeOptions.includes(pageSize)) {
      this.pageSizeOptions.unshift(pageSize);
    }
    this.handleSearch();
    this.getDefaultOrgVersion();
  },
  methods: {
    changeCreator({ value, label, nodes }) {
      let userNames = [];
      nodes.map(node => {
        if (node.data) {
          userNames.push(node.data.userName);
        }
      });
      this.filterData.filterMap = {
        creator: userNames.join(';')
      };
    },
    getFilterContainer(target) {
      return target.parentNode;
    },
    // 获取默认组织版本
    getDefaultOrgVersion() {
      fetchDefaultOrgVersion({
        system: this._$SYSTEM_ID
      }).then(res => {
        this.defaultOrgVersion = res;
      });
    },
    disabledDate(current) {
      return current && current > moment();
    },
    // 更改自定义时间范围
    changeCustomDateRange(dates, dateStrings) {
      this.filterData.startTime = dateStrings[0];
      this.filterData.endTime = dateStrings[1];
    },
    // 获取一页的条数
    getPageSize() {
      const config = this.setting.resultConfig;
      let pageSize = config.pageSize;
      if (pageSize === 'custom') {
        pageSize = config.customPageSize;
      }
      // pageSize = pageSize ? Number(pageSize) : 10;
      return pageSize;
    },
    // 翻页
    changePage(page, pageSize) {
      this.pagination.pageSize = pageSize;
      this.pagination.current = page;
      this.fetchSearch();
    },
    // 改变一条多少条
    changePageSize(current, size) {
      this.pagination.pageSize = size;
      this.pagination.current = 1;
      this.fetchSearch();
    },
    // 更改排序
    handleOrder(item) {
      this.activeOrder = item.value;
      this.visibleOrder = false;
      this.pagination.current = 1;
      this.fetchSearch();
    },
    // 更改分类
    handleCategory(item) {
      this.searchInstance.categoryCode = item.value;
      this.pagination.current = 1;
      this.fetchSearch();
    },
    // 保存筛选
    handleSaveFilter() {
      this.searchInstance = { ...this.searchInstance, ...this.filterData };
      delete this.searchInstance.creator;
      this.searchInstance.filterMap = {
        creatorId: this.filterData.creator
      };
      let dateRange = this.filterData.dateRange;
      if (dateRange === this.dateRangeOptions[0]['value']) {
        dateRange = null;
      }
      this.searchInstance.dateRange = dateRange;

      this.visibleFilter = false;
    },
    // 重置筛选
    handleResetFilter() {
      this.filterData = this.$options.data().filterData;
      this.searchInstance = { ...this.searchInstance, ...this.filterData };
      let dateRange = this.filterData.dateRange;
      if (dateRange === this.dateRangeOptions[0]['value']) {
        dateRange = null;
      }
      this.searchInstance.dateRange = dateRange;
      this.visibleFilter = false;
    },
    handleSearch() {
      this.pagination.current = 1;
      let startTime = performance.now();
      this.fetchSearch(() => {
        let endTime = performance.now();
        let diff = (endTime - startTime) / 1000;

        this.diffSeconds = diff.toFixed(2);
      });
    },
    // 请求搜索
    fetchSearch(callBack) {
      // this.searchInstance.keyword = this.keywords;
      // 分类
      // this.searchInstance.categoryCode = this.activeCategory;
      // if (this.activeCategory === 'all') {
      //   this.searchInstance.categoryCode = '';
      // }

      // 分页
      this.searchInstance.pagingInfo.pageSize = this.pagination.pageSize;
      this.searchInstance.pagingInfo.currentPage = this.pagination.current;

      // 排序
      if (this.activeOrder) {
        let order = this.activeOrder.split('.');
        this.searchInstance.order = {
          property: order[0],
          direction: order[1]
        };
      } else {
        this.searchInstance.order = null;
      }
      this.loading = true;
      this.dataList = [];
      this.fileList = [];
      this.categoryCountMap = {};
      fetchQueryFulltextIndex(this.searchInstance).then(({ dataList, pagingInfo, countMap }) => {
        this.loading = false;
        if (typeof callBack === 'function') {
          callBack();
        }
        this.dataList = dataList;

        if (this.config.enabledColumns) {
          let fileList = [];
          dataList.forEach(item => {
            if (item.fileInfos) {
              const fileInfos = JSON.parse(item.fileInfos);
              if (Array.isArray(fileInfos) && fileInfos.length) {
                fileInfos.forEach(file => {
                  if (Array.isArray(file)) {
                    file.map(f => {
                      fileList.push({ ...f, resultSource: item });
                    });
                  } else {
                    fileList.push({ ...file, resultSource: item });
                  }
                });
              }
            } else if (item.attachments) {
              if (Array.isArray(item.attachments) && item.attachments.length) {
                item.attachments.map(file => {
                  fileList.push({ ...file, resultSource: item });
                });
              }
            }
          });
          this.fileList = fileList;
        }

        this.pagination.total = pagingInfo.totalCount;

        // if (!Object.keys(this.categoryCountMap).length) {
        let allCount = 0;
        this.categoryCountMap = countMap;
        for (const key in countMap) {
          if (this.categoryMap[key]) {
            allCount = allCount + Number(countMap[key]);
          }
        }
        this.categoryCountMap.all = allCount;
        // }
      });
    }
  }
};
</script>
