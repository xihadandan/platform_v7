<template>
  <div style="overflow-x: hidden">
    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
    <a-list v-else item-layout="horizontal" :data-source="apiInvokeLogs">
      <a-list-item slot="renderItem" slot-scope="item, index" style="background-color: #fff; padding: 8px 12px; margin-bottom: 12px">
        <a-list-item-meta>
          <div slot="title" style="overflow: hidden; display: flex; align-items: center; width: 600px">
            <a-tag :color="methodColor[item.reqMethod]">{{ item.reqMethod }}</a-tag>
            <div
              :title="item.invokeUrl"
              style="display: inline-block; text-overflow: ellipsis; width: 545px; white-space: nowrap; overflow: hidden"
            >
              {{ item.invokeUrl }}
            </div>
          </div>
          <template slot="description">
            <div style="text-align: right">
              <a-space>
                <div>HTTP状态码: {{ item.resStatus }}</div>
                <div>请求时间: {{ item.reqTime }}</div>
                <div>
                  耗时:
                  <label
                    :style="{
                      color: item.latency > 500 ? '#f50' : '#87d068'
                    }"
                  >
                    {{ item.latency }} ms
                  </label>
                </div>
              </a-space>
              <a-divider type="vertical" />
              <Modal :width="800" title="请求返回详情" :z-index="1000">
                <template slot="content">
                  <div>
                    <div class="spin-center" v-if="item.loadingDetail">
                      <a-spin />
                    </div>
                    <a-descriptions bordered :column="1" class="api-link-log-descriptions">
                      <a-descriptions-item label="请求地址">
                        <a-tag :color="methodColor[item.reqMethod]">{{ item.reqMethod }}</a-tag>
                        {{ item.invokeUrl }}
                      </a-descriptions-item>
                      <a-descriptions-item label="HTTP状态码">{{ item.resStatus }}</a-descriptions-item>
                      <a-descriptions-item label="请求时间">{{ item.reqTime }}</a-descriptions-item>
                      <a-descriptions-item label="请求方法" v-if="item.protocol == 'SOAP'">
                        {{ item.path }}
                      </a-descriptions-item>
                      <a-descriptions-item label="请求参数">
                        <a-button
                          v-if="item.reqQueryParams"
                          size="small"
                          type="link"
                          style="float: right"
                          @click="e => onCopy(e, item.reqQueryParams)"
                        >
                          复制
                        </a-button>
                        {{ item.reqQueryParams }}
                      </a-descriptions-item>
                      <a-descriptions-item label="请求头部">
                        <a-button
                          v-if="item.reqHeaders"
                          size="small"
                          type="link"
                          style="float: right"
                          @click="e => onCopy(e, item.reqHeaders)"
                        >
                          复制
                        </a-button>
                        {{ item.reqHeaders }}
                      </a-descriptions-item>
                      <a-descriptions-item label="请求体">
                        <a-button v-if="item.reqBody" size="small" type="link" style="float: right" @click="e => onCopy(e, item.reqBody)">
                          复制
                        </a-button>
                        {{ item.reqBody }}
                      </a-descriptions-item>
                      <a-descriptions-item label="返回时间">{{ item.resTime }}</a-descriptions-item>
                      <a-descriptions-item label="请求耗时">{{ item.latency }} ms</a-descriptions-item>
                      <a-descriptions-item label="返回头部">
                        <a-button
                          v-if="item.resHeaders"
                          size="small"
                          type="link"
                          style="float: right"
                          @click="e => onCopy(e, item.resHeaders)"
                        >
                          复制
                        </a-button>
                        {{ item.resHeaders }}
                      </a-descriptions-item>
                      <a-descriptions-item label="返回数据">
                        <a-button v-if="item.resBody" size="small" type="link" style="float: right" @click="e => onCopy(e, item.resBody)">
                          复制
                        </a-button>
                        {{ item.resBody }}
                      </a-descriptions-item>
                      <a-descriptions-item label="异常信息" v-if="item.errorMessage">
                        <a-button size="small" type="link" style="float: right" @click="e => onCopy(e, item.errorMessage)">复制</a-button>
                        <p style="color: red">{{ item.errorMessage }}</p>
                      </a-descriptions-item>
                    </a-descriptions>
                  </div>
                </template>
                <a-button size="small" type="link" @click="fetchLogDetails(item)">详情</a-button>
              </Modal>
            </div>
          </template>
        </a-list-item-meta>
      </a-list-item>
    </a-list>
    <a-button
      v-show="apiInvokeLogs.length > 0 && !noMoreData"
      type="link"
      :block="true"
      size="small"
      :loading="fetching"
      @click="fetchApiInvokeLogs"
    >
      查看更多
    </a-button>
  </div>
</template>
<style lang="less">
.api-link-log-descriptions > .ant-descriptions-view {
  > table {
    table-layout: fixed !important;
    th {
      vertical-align: top;
      width: 130px;
    }
  }
}
</style>
<script type="text/babel">
import { generateId, copyToClipboard } from '@framework/vue/utils/util';
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'ApiInvokeLogs',
  props: {
    apiLinkUuid: String,
    apiOperationUuid: String
  },
  components: { Modal },
  computed: {
    methodColor() {
      return {
        GET: '#17b26a',
        POST: '#ef6820',
        DELETE: '#f04438',
        PUT: '#2e90fa',
        OPTIONS: '#2e90fa'
      };
    }
  },
  data() {
    return { apiInvokeLogs: [], fetching: false, pageIndex: 1, pageSize: 5, loading: true, noMoreData: false };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.fetchApiInvokeLogs();
  },
  methods: {
    onCopy(e, text) {
      let _this = this;
      copyToClipboard(text, e, function (success) {
        if (success) {
          _this.$message.success('已复制');
        }
      });
    },
    fetchLogDetails(item) {
      this.$set(item, 'loadingDetail', true);
      $axios
        .get(`/proxy/api/apiLink/getApiInvokeLogDetails`, { params: { uuid: item.uuid } })
        .then(({ data }) => {
          this.$set(item, 'loadingDetail', false);
          for (let key in data.data) {
            this.$set(item, key, data.data[key]);
          }
        })
        .catch(error => {
          this.$set(item, 'loadingDetail', false);
        });
    },
    getModalContainer() {
      return this.$el;
    },
    fetchApiInvokeLogs() {
      this.fetching = true;
      $axios
        .post(`/proxy/api/apiLink/listPageSimpleInvokeLog`, {
          pagingInfo: {
            pageIndex: this.pageIndex,
            pageSize: this.pageSize,
            autoCount: true
          },
          apiLinkUuid: this.apiLinkUuid,
          apiOperationUuid: this.apiOperationUuid
        })
        .then(({ data }) => {
          this.fetching = false;
          this.loading = false;
          if (data.data.result && data.data.result.length > 0) {
            this.apiInvokeLogs.push(...data.data.result);
            this.pageIndex++;
          } else {
            this.noMoreData = true;
            this.$message.info('无更多数据了');
          }
        })
        .catch(error => {
          this.fetching = false;
          this.loading = false;
        });
    }
  }
};
</script>
