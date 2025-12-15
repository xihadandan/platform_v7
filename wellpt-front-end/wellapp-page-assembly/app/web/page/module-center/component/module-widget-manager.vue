<template>
  <a-tabs class="module-widget-manager pt-tabs">
    <a-tab-pane key="ptWidget" tab="平台组件">
      <PerfectScrollbar class="tab-content-scroll" style="height: calc(100vh - 110px)">
        <a-list :grid="grid" :data-source="ptWidgets" rowKey="wtype" class="module-function-widget-list">
          <a-list-item slot="renderItem" slot-scope="item, index">
            <a-card hoverable :bodyStyle="{ padding: '0px' }">
              <div class="item-img">
                <template v-if="item.thumbnail">
                  <img :src="item.thumbnail" v-if="item.thumbnail.indexOf('/public/') == 0" />
                  <span v-html="item.thumbnail" v-else="item.thumbnail.indexOf('<svg ') == 0"></span>
                </template>
                <a-icon v-else type="code-sandbox" style="font-size: 100px; color: var(--w-primary-color)" />
              </div>
              <a-row type="flex" class="item-detail">
                <a-col flex="1px" style="padding: 40px 0px 40px 12px"></a-col>
                <a-col flex="auto">
                  <div class="flex" style="align-items: center; justify-content: space-between">
                    <div :title="item.title" class="list-title" style="max-width: calc(100% - 64px)">
                      {{ item.title }}
                    </div>
                    <div style="padding-right: 12px">
                      <Modal title="组件国际化" :width="900" okText="保存" :ok="onConfirmSaveWidgetCodeI18ns">
                        <template slot="content">
                          <ViewWidgetLocale :wtype="item.wtype" ref="viewWidgetLocale" />
                        </template>
                        <a-button size="small" type="link" icon="global" />
                      </Modal>
                    </div>
                  </div>
                  <div :title="item.description" class="list-remark" style="max-width: calc(100% - 64px)">
                    {{ item.description }}
                  </div>
                </a-col>

                <!-- <a-col :flex="'64px'">
                  <a-button type="link" size="small">
                    <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
                    查看
                  </a-button>
                </a-col> -->
              </a-row>
            </a-card>
          </a-list-item>
        </a-list>
      </PerfectScrollbar>
    </a-tab-pane>
    <a-tab-pane key="moduleWidget" tab="我的组件">
      <div class="flex f_nowrap" style="margin-bottom: 12px">
        <a-input-search v-model.trim="searchParams.keyword" @search="refreshModuleWidgetList" style="width: 200px" allowClear />
        <a-button type="primary" @click="refreshModuleWidgetList" style="margin-left: 12px">查询</a-button>
      </div>
      <PerfectScrollbar class="tab-content-scroll" style="height: calc(100vh - 160px)">
        <a-list
          :grid="grid"
          :data-source="moduleWidgetList"
          rowKey="wtype"
          :loading="moduleWidgetLoading"
          class="module-function-widget-list"
        >
          <div slot="loadMore" v-if="functionMore" style="text-align: center">
            <a-button type="link" @click="loadMore" :loading="moduleWidgetLoading">加载更多</a-button>
          </div>
          <a-list-item slot="renderItem" slot-scope="item, index" style="min-width: 330px">
            <a-card hoverable :bodyStyle="{ padding: '0px' }">
              <div class="item-img">
                <template v-if="item.definitionJson.thumbnail">
                  <img :src="item.definitionJson.thumbnail" />
                </template>
                <a-empty v-else :description="null" style="padding-top: 32px"></a-empty>
              </div>
              <a-row type="flex" class="item-detail">
                <a-col flex="52px" style="padding: 24px 0px 24px 12px">
                  <a-avatar :size="32" style="background-color: var(--w-primary-color-2); color: var(--w-primary-color)">
                    <Icon slot="icon" type="pticon iconfont icon-szgy-zhuye"></Icon>
                  </a-avatar>
                </a-col>
                <a-col flex="auto">
                  <div class="flex">
                    <div :title="item.title" class="list-title" style="max-width: 130px">
                      {{ item.title }}
                    </div>
                    <a-tag :color="item.enabled ? 'blue' : null" class="tag-no-border">{{ item.enabled ? '已发布' : '未发布' }}</a-tag>
                  </div>
                  <div :title="item.remark" class="list-remark" style="max-width: 150px">
                    {{ item.remark }}
                  </div>
                </a-col>

                <a-col :flex="'64px'">
                  <a-button type="link" size="small" @click="editModuleFunctionWidget(item.uuid)">
                    <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
                    编辑
                  </a-button>
                </a-col>
              </a-row>
            </a-card>
          </a-list-item>
        </a-list>
      </PerfectScrollbar>
    </a-tab-pane>
  </a-tabs>
</template>
<style lang="less"></style>
<script type="text/babel">
import '../css/assemble.less';
import ViewWidgetLocale from './view-widget-locale.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'ModuleWidgetManager',
  props: {},
  components: { ViewWidgetLocale, Modal },
  computed: {},
  data() {
    return {
      ptWidgets: [],
      grid: { gutter: 16, xs: 1, sm: 1, md: 2, lg: 3, xl: 4, xxl: 4 },
      moduleWidgetLoading: true,
      moduleWidgetList: [],
      searchParams: { keyword: undefined, type: 'FUNCTION_WIDGET' },
      page: { currentPage: 1, pageSize: 12, totalCount: 0 },
      functionMore: true
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchWidgetMetaInfos();
    this.refreshModuleWidgetList();
  },
  mounted() {},
  methods: {
    onConfirmSaveWidgetCodeI18ns() {
      this.$refs.viewWidgetLocale.save();
    },
    loadMore() {
      this.moduleWidgetLoading = true;
      this.page.currentPage++;
      this.fetchModuleDefWidgets();
    },
    refreshModuleWidgetList() {
      this.moduleWidgetLoading = true;
      this.page.currentPage = 1;
      this.fetchModuleDefWidgets();
    },
    fetchModuleDefWidgets() {
      $axios
        .post(`/proxy/api/user/widgetDef/query`, { ...this.searchParams, page: this.page })
        .then(({ data }) => {
          this.widgetList = [];
          for (let d of data.data.data) {
            d.definitionJson = JSON.parse(d.definitionJson);
          }
          this.functionMore = true;
          if (this.page.currentPage > 1) {
            if (data.data.data.length) {
              this.moduleWidgetList.push(...data.data.data);
            } else {
              this.$message.info('无更多数据');
            }
          } else {
            this.moduleWidgetList = data.data.data || [];
          }
          if (!data.data.data || data.data.data.length < this.page.pageSize) {
            this.functionMore = false;
          }

          this.moduleWidgetLoading = false;
        })
        .catch(error => {
          this.moduleWidgetLoading = false;
        });
    },
    editModuleFunctionWidget(uuid) {
      window.open(`/module-widget-design/${uuid}`, '_blank');
    },
    fetchWidgetMetaInfos() {
      import('@modules/.webpack.widget.meta-info.js').then(info => {
        let widgets = info.default;
        for (let w of widgets) {
          this.ptWidgets.push({
            title: w.name,
            wtype: w.wtype,
            icon: w.icon, // 图标, 考虑支持图标类 \ svg \ base64
            thumbnail: w.thumbnail, // 缩略图, 考虑支持图标类 \ svg \ base64
            description: w.description
          });
        }
      });
    }
  }
};
</script>
