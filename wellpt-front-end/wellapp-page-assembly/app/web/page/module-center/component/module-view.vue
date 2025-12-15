<template>
  <a-tabs default-active-key="1" class="module-view-tabs prod-manager-list-tabs">
    <template slot="tabBarExtraContent">
      <!-- <a-input-search allowClear @search="onSearch" /> -->
    </template>
    <a-tab-pane key="1" tab="全部">
      <PerfectScrollbar class="tab-content-scroll">
        <div style="margin-bottom: 20px">
          <a-button icon="plus" type="primary" @click="quickCreateVisible = true">创建模块</a-button>
          <a-input-search allowClear @search="onSearch" style="float: right; width: 320px" />
          <QuickCreateSelect v-model="quickCreateVisible" @createDone="refreshList" />
        </div>
        <a-list :grid="grid" :data-source="moduleList" rowKey="uuid" :loading="loading">
          <div
            slot="loadMore"
            v-if="moduleList.length > 0 && (searchParams.keyword == undefined || searchParams.keyword == '')"
            v-show="!noMore"
            style="text-align: center"
          >
            <a-button type="link" @click="loadMore" :loading="loading">加载更多</a-button>
          </div>
          <a-list-item slot="renderItem" slot-scope="item">
            <a-card hoverable class="module-item-card">
              <template slot="actions">
                <div class="ant-card-actions">
                  <div style="float: right; padding-right: 5px">
                    <ExportDef :uuid="item.uuid" type="appModule" modifyRange title="导出模块">
                      <a-button type="link" class="ant-btn-icon-only" title="导出模块">
                        <Icon type="pticon iconfont icon-luojizujian-yemiantiaozhuan"></Icon>
                      </a-button>
                    </ExportDef>
                    <a-button type="link" class="ant-btn-icon-only" title="配置" @click="openModuleAssemble(item.uuid)">
                      <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                    </a-button>
                    <Modal title="编辑模块" :ok="e => onEditModuleDetailOk(e, item)" :container="getContainer">
                      <a-button type="link" class="ant-btn-icon-only" title="编辑">
                        <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
                      </a-button>
                      <template slot="content">
                        <ModuleDetail ref="moduleDetail" :detail="item" :container="getContainer" />
                      </template>
                    </Modal>
                    <a-popconfirm
                      placement="top"
                      :arrowPointAtCenter="true"
                      title="确认要删除吗?"
                      ok-text="删除"
                      cancel-text="取消"
                      @confirm="deleteModule(item.uuid)"
                    >
                      <a-button
                        type="link"
                        class="ant-btn-icon-only"
                        title="删除"
                        v-if="
                          isSuperAdmin || (item.id && !item.id.toLowerCase().startsWith('pt-') && !item.id.toLowerCase().startsWith('pt_'))
                        "
                      >
                        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                      </a-button>
                    </a-popconfirm>
                  </div>
                </div>
              </template>
              <a-card-meta :description="item.remark || '无描述'">
                <template slot="title">
                  <div class="w-ellipsis" :title="item.name">
                    {{ item.name }}
                  </div>
                </template>
                <a-avatar
                  v-if="item.icon"
                  shape="square"
                  :size="48"
                  slot="avatar"
                  :style="{
                    background: item.icon.bgColor
                      ? item.icon.bgColor.startsWith('--')
                        ? 'var(' + item.icon.bgColor + ')'
                        : item.icon.bgColor
                      : 'var(--w-primary-color)',
                    borderRadius: '8px'
                  }"
                >
                  <Icon
                    slot="icon"
                    :type="(item.icon && item.icon.icon) || 'pticon iconfont icon-ptkj-zaitechengjian'"
                    style="font-size: 32px"
                  />
                </a-avatar>
              </a-card-meta>
            </a-card>
          </a-list-item>
        </a-list>
        <a-back-top :target="backTopTarget" style="right: 30px" />
      </PerfectScrollbar>
    </a-tab-pane>
    <!-- <a-tab-pane key="2" tab="全部"></a-tab-pane> -->
  </a-tabs>
</template>
<style lang="less">
.module-view-tabs {
  background-color: #fff;
  .tab-content-scroll {
    height: e('calc(100vh - 100px)');
    padding: 20px;
  }
  .create-div {
    height: 112px;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .module-item-card {
    .ant-card-actions {
      background-color: transparent;
      border-top: none;
    }
    .ant-card-meta-title {
      color: var(--w-text-color-dark);
      font-size: var(--w-font-size-base);
      font-weight: bold;
      line-height: 32px;
      margin-bottom: 4px;
    }

    .ant-card-meta-description {
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }
    .ant-card-body {
      padding: 20px 20px 0 20px;
    }
  }
}
</style>
<script type="text/babel">
import QuickCreateSelect from './module-quick-create-select.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import ModuleDetail from './module-detail.vue';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import '@pageAssembly/app/web/page/product-center/css/index.less';
import { map } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';

export default {
  name: 'ModuleView',
  props: {},
  components: { QuickCreateSelect, Modal, ModuleDetail, ExportDef },
  computed: {},
  data() {
    return {
      quickCreateVisible: false,
      grid: { gutter: 16, xs: 1, sm: 1, md: 2, lg: 3, xl: 3, xxl: 4 },
      moduleList: [{ uuid: -1 }],
      loading: true,
      searchParams: { keyword: undefined },
      page: { currentPage: 1, pageSize: 8, totalCount: 0 },
      noMore: false,
      isSuperAdmin: false,
      isInIframe: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.isSuperAdmin = this._hasAnyRole('ROLE_ADMIN');
    this.refreshList();
  },
  mounted() {
    this.isInIframe = window.self !== window.top;
    if (this.isInIframe) {
      const document = window.top.document;
      if (!document.querySelector('#vue-color-style')) {
        const link = document.createElement('link');
        link.rel = 'stylesheet';
        link.id = 'vue-color-style';
        link.href = '/static/css/vue-color.css';
        document.head.appendChild(link);
      }
    }
  },
  methods: {
    getContainer() {
      if (window.self !== window.top) {
        return window.top.document.body;
      }
      return document.body;
    },
    onSearch(e) {
      if (e != undefined && e.trim() != '') {
        this.searchParams.keyword = e.trim();
        this.refreshList();
      } else if (e == '') {
        this.searchParams.keyword = undefined;
        this.refreshList();
      }
    },
    backTopTarget() {
      return this.$el.querySelector('.tab-content-scroll');
    },
    openModuleAssemble(uuid) {
      window.open(`/module/assemble/${uuid}`, '_blank');
    },
    onEditModuleDetailOk(e, item) {
      let form = this.$refs.moduleDetail.form;
      let saveData = deepClone(form);
      saveData.icon = JSON.stringify(saveData.icon);
      $axios.post(`/proxy/api/app/module/save`, saveData).then(({ data }) => {
        if (data.code == 0) {
          e(true);
          item.name = form.name;
          item.remark = form.remark;
          item.icon = form.icon;
          this.$message.success('保存成功');
        } else {
          this.$message.error('保存失败');
        }
      });
    },
    deleteModule(uuid) {
      $axios.get(`/proxy/api/app/module/delete/${uuid}`).then(({ data }) => {
        if (data.data) {
          this.$message.success('删除成功');
          this.refreshList();
        }
      });
    },
    resetSearchForm() {
      this.searchParams.keyword = undefined;
      this.refreshList();
    },
    refreshList() {
      this.loading = true;
      this.page.currentPage = 1;
      this.fetchModules();
    },
    loadMore() {
      this.loading = true;
      this.page.currentPage++;
      this.fetchModules();
    },
    fetchModules() {
      $axios
        .post(
          `/proxy/api/app/module/query`,
          this.searchParams.keyword != undefined ? { ...this.searchParams } : { ...this.searchParams, page: this.page }
        )
        .then(({ data }) => {
          this.loading = false;
          if (this.page.currentPage > 1 && this.searchParams.keyword == undefined) {
            if (data.data.data) {
              if (data.data.data.length < this.page.pageSize) {
                this.noMore = true;
              } else {
                this.noMore = false;
              }
              let list = map(data.data.data, item => {
                item.icon = this.iconDataToJson(item.icon);
                return item;
              });
              this.moduleList.push(...list);
            } else {
              this.noMore = true;
              this.$message.info('无更多数据');
            }
          } else {
            if (!data.data.data || data.data.data.length < this.page.pageSize) {
              this.noMore = true;
            } else {
              this.noMore = false;
            }
            let list = map(data.data.data, item => {
              item.icon = this.iconDataToJson(item.icon);
              return item;
            });
            this.moduleList = [].concat(list);
          }
        })
        .catch(() => {
          this.loading = false;
        });
    },
    iconDataToJson(data) {
      if (!data) {
        data = {
          icon: '',
          bgColor: ''
        };
      } else {
        try {
          let iconJson = JSON.parse(data);
          if (iconJson) {
            data = iconJson;
          }
        } catch (e) {
          if (typeof data == 'string') {
            let iconJson = {
              icon: data,
              bgColor: ''
            };
            data = iconJson;
          }
          return data;
        }
      }
      return data;
    }
  }
};
</script>
