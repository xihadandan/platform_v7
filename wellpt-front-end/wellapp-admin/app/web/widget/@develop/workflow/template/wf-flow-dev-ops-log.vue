<template>
  <a-config-provider :locale="locale">
    <Modal
      :bodyStyle="bodyStyle"
      :title="title"
      width="calc(-300px + 100vw)"
      maxHeight="calc(-200px + 100vh)"
      :destroyOnClose="true"
      :visible="visible"
      ref="wfFlowDevOpsIdentityReplaceLogModal"
      centered
    >
      <template slot="content">
        <div class="flex dev-ops-log">
          <div style="width: 400px" class="f_s_0 dev-ops-log-left">
            <div class="flex f_x_s" style="padding: 12px 20px">
              <a-input-search v-model="queryInfo.keyword" placeholder="查询" style="width: calc(100% - 42px)" @search="onSearch" />
              <a-button class="icon-only" @click="onOrderClick" :title="currentOrder == 'desc' ? '降序' : '升序'">
                <Icon type="iconfont icon-ptkj-qiehuanpaixu" />
              </a-button>
            </div>
            <PerfectScrollbar ref="scrollbar" style="height: calc(100% - 56px)" @ps-y-reach-end="onScrollYReachEnd">
              <div class="dev-ops-log-list">
                <div
                  :class="['dev-ops-log-list-item', item.selected ? 'selected' : '']"
                  @click="onItemClick(item)"
                  v-for="item in logItems"
                >
                  <div>
                    <a-row type="flex">
                      <a-col flex="120px" class="item-header-label">批量查找替换</a-col>
                      <a-col
                        flex="auto"
                        class="w-ellipsis"
                        :title="item.createTime + ' | ' + item.userName"
                        style="text-align: right; font-size: var(--w-font-size-sm); color: var(--w-text-color-light)"
                      >
                        {{ item.createTime }} | {{ item.userName }}
                      </a-col>
                    </a-row>
                  </div>
                  <div style="margin-top: 4px">{{ item.content }}</div>
                </div>
                <a-empty v-if="!logItems.length" class="pt-empty" style="margin-top: 100px"></a-empty>
              </div>
            </PerfectScrollbar>
          </div>
          <PerfectScrollbar style="height: calc(100%)">
            <div class="f_g_1" style="padding: 12px 20px">
              <IdentityReplaceResult
                v-if="searchForm"
                :searchForm="searchForm"
                :records="records"
                :log="currentItem"
                displayState="label"
                @changeTableStyle="changeTableStyle"
              ></IdentityReplaceResult>
              <a-empty class="pt-empty" style="margin-top: 145px" v-else></a-empty>
            </div>
          </PerfectScrollbar>
        </div>
      </template>
      <template slot="footer">
        <a-button type="primary" @click="visible = false">关闭</a-button>
      </template>
    </Modal>
  </a-config-provider>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import IdentityReplaceResult from './identity-replace-result.vue';
import { getElSpacingForTarget } from '@framework/vue/utils/util';
export default {
  props: {
    title: {
      type: String,
      default: '批量维护记录'
    },
    bodyStyle: {
      type: Object,
      default() {
        return { height: 'calc(-200px + 100vh)', overflow: 'hidden' };
      }
    },
    pageContext: Object,
    locale: Object
  },
  components: { Modal, OrgSelect, IdentityReplaceResult },
  provide() {
    return {
      pageContext: this.pageContext,
      locale: this.locale
    };
  },
  data() {
    if (this.locale && !this.locale.emptyText) {
      this.locale.emptyText = '暂无数据';
    }
    return {
      visible: false,
      logItems: [],
      currentOrder: 'desc',
      queryInfo: {
        keyword: '',
        orderBy: 'createTime desc',
        pagingInfo: { currentPage: 1, pageSize: 10 }
      },
      hasMoreData: true,
      searchForm: null,
      records: null,
      currentItem: null
    };
  },
  mounted() {
    this.loadLogs().then(() => {
      // 加载第一条数据
      if (this.logItems && this.logItems.length) {
        this.logItems[0].selected = true;
        this.onItemClick(this.logItems[0]);
      }
    });
  },
  methods: {
    show() {
      this.visible = true;
    },
    loadData() {
      const _this = this;
      return $axios.post('/proxy/api/workflow/identity/replace/logs/list', _this.queryInfo).then(({ data: result }) => {
        if (result.data) {
          let logItems = result.data.dataList || [];
          logItems.forEach(item => (item.selected = false));
          return logItems;
        }
      });
    },
    loadLogs() {
      const _this = this;
      return _this.loadData().then(logItems => {
        if (logItems) {
          _this.logItems = logItems;
        }
      });
    },
    loadMore() {
      this.queryInfo.pagingInfo.currentPage++;
      return this.loadData().then(items => {
        if (items && items.length) {
          if (items.length < this.queryInfo.pagingInfo.pageSize) {
            this.hasMoreData = false;
          } else {
            this.hasMoreData = true;
          }
          this.logItems = [...this.logItems, ...items];
        } else {
          this.hasMoreData = false;
        }
      });
    },
    onSearch() {
      this.hasMoreData = true;
      this.queryInfo.pagingInfo.currentPage = 1;
      this.loadLogs();
    },
    onOrderClick() {
      if (this.currentOrder == 'desc') {
        this.onAscOrderClick();
      } else {
        this.onDescOrderClick();
      }
    },
    onAscOrderClick() {
      this.hasMoreData = true;
      this.queryInfo.pagingInfo.currentPage = 1;
      this.currentOrder = 'asc';
      this.queryInfo.orderBy = 'createTime asc';
      this.loadLogs();
    },
    onDescOrderClick() {
      this.hasMoreData = true;
      this.queryInfo.pagingInfo.currentPage = 1;
      this.currentOrder = 'desc';
      this.queryInfo.orderBy = 'createTime desc';
      this.loadLogs();
    },
    onItemClick(item) {
      const _this = this;
      _this.searchForm = null;
      _this.records = null;
      _this.currentItem = item;
      _this.logItems.forEach(logItem => (logItem.selected = false));

      item.selected = true;
      if (item.details) {
        _this.$nextTick(() => {
          let details = item.details;
          _this.searchForm = details.params;
          _this.records = details.records;
        });
      } else {
        $axios.get(`/proxy/api/workflow/identity/replace/logs/get?logUuid=${item.uuid}`).then(({ data: result }) => {
          if (result.data) {
            let details = JSON.parse(result.data.details || {});
            item.details = details;
            _this.searchForm = details.params;
            _this.records = details.records;
          }
        });
      }
    },
    onScrollYReachEnd() {
      if (this.hasMoreData) {
        this.loadMore().then(() => {
          this.$refs.scrollbar.update();
        });
      }
    },
    changeTableStyle($el) {
      this.$nextTick(() => {
        setTimeout(() => {
          if (!$el) {
            $el = this.$refs.tableWidget.$el.querySelector('.ant-table-content');
          }
          let { maxHeight } = getElSpacingForTarget(
            $el,
            this.$refs.wfFlowDevOpsIdentityReplaceLogModal.$refs.modalComponentRef.$el.querySelector('.ant-modal-body')
          );
          if (maxHeight) {
            maxHeight = maxHeight;
            $el.style.cssText += `;overflow-y:auto; height:${maxHeight}px;translate:height 0.3s;`;
          }
        }, 200);
      });
    }
  }
};
</script>

<style lang="less" scoped>
.dev-ops-log {
  border: 1px solid var(--w-border-color-light);
  border-radius: 4px;
  height: 100%;
  .dev-ops-log-left {
    height: 100%;
    border-right: 1px solid var(--w-border-color-light);
    .dev-ops-log-list {
      padding: 0px 20px 12px;
      .dev-ops-log-list-item {
        margin-top: 8px;
        padding: 12px;
        border-radius: 4px;
        background-color: #fafafa;
        font-size: var(--w-font-size-base);
        color: var(--w-text-color-dark);
        cursor: pointer;

        .item-header-label {
          font-weight: bold;
        }

        &:first-child {
          margin-top: 0;
        }

        &.selected {
          background-color: var(--w-primary-color-1);
          .item-header-label {
            color: var(--w-primary-color);
          }
        }
      }
    }
  }
}
</style>
