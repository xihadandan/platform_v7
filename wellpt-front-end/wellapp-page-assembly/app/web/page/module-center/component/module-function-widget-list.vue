<template>
  <a-card :bordered="false">
    <template slot="title">
      <Modal title="新增组件" :ok="addNewModuleFuncWidget">
        <a-button type="primary" icon="plus">新增</a-button>
        <template slot="content">
          <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" ref="form">
            <a-form-model-item label="组件名称">
              <a-input v-model="form.title" />
            </a-form-model-item>
            <a-form-model-item label="描述">
              <a-textarea v-model="form.remark" />
            </a-form-model-item>
          </a-form-model>
        </template>
      </Modal>
    </template>
    <template slot="extra">
      <div class="flex f_nowrap">
        <a-input-search v-model.trim="searchParams.keyword" @search="refreshList" allowClear />
        <a-button type="primary" @click="refreshList" style="margin-left: 12px">查询</a-button>
      </div>
    </template>

    <a-list :grid="grid" :data-source="widgetList" rowKey="wtype" :loading="loading" class="module-function-widget-list">
      <a-list-item slot="renderItem" slot-scope="item, index">
        <a-card hoverable :bodyStyle="{ padding: '0px' }">
          <div class="item-img">
            <template v-if="item.definitionJson.thumbnail">
              <img :src="item.definitionJson.thumbnail" />
            </template>
            <a-icon v-else type="code-sandbox" style="font-size: 140px; color: var(--w-primary-color)" />
          </div>
          <a-row type="flex" class="item-detail">
            <a-col flex="52px" style="padding: 24px 0px 24px 12px">
              <a-avatar :size="32" style="background-color: var(--w-primary-color-2); color: var(--w-primary-color)">
                <Icon slot="icon" type="pticon iconfont icon-szgy-zhuye"></Icon>
              </a-avatar>
            </a-col>
            <a-col flex="auto">
              <div class="flex">
                <div
                  :title="item.title"
                  style="
                    max-width: 130px;
                    text-overflow: ellipsis;
                    overflow: hidden;
                    white-space: nowrap;
                    padding-right: 5px;
                    color: var(--w-text-color-dark);
                    font-size: var(--w-font-size-base);
                    font-weight: bold;
                  "
                >
                  {{ item.title }}
                </div>
                <a-tag :color="item.enabled ? 'blue' : null" class="tag-no-border">{{ item.enabled ? '已发布' : '未发布' }}</a-tag>
              </div>
              <div
                :title="item.remark"
                style="
                  max-width: 150px;
                  text-overflow: ellipsis;
                  overflow: hidden;
                  white-space: nowrap;
                  padding-right: 5px;
                  color: var(--w-text-color-light);
                  font-size: var(--w-font-size-sm);
                "
              >
                {{ item.remark }}
              </div>
            </a-col>

            <a-col :flex="'34px'">
              <a-dropdown>
                <a-button type="icon" size="small" title="更多操作"><Icon type="pticon iconfont icon-ptkj-gengduocaidan"></Icon></a-button>
                <a-menu slot="overlay" @click="e => handleMenuClick(e, item, index)">
                  <a-menu-item key="edit">编辑</a-menu-item>
                  <a-menu-item :key="item.enabled ? 'unpublish' : 'publish'">{{ item.enabled ? '取消发布' : '发布' }}</a-menu-item>
                  <a-menu-item key="delete">删除</a-menu-item>
                </a-menu>
              </a-dropdown>
            </a-col>
          </a-row>
        </a-card>
      </a-list-item>
    </a-list>
  </a-card>
</template>
<style lang="less"></style>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import { generateId } from '@framework/vue/utils/util';

export default {
  name: 'ModuleFunctionWidgetList',
  props: { appId: String },
  components: { Modal },
  computed: {},
  data() {
    return {
      form: {},
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      grid: { gutter: 16, xs: 1, sm: 1, md: 2, lg: 3, xl: 3, xxl: 4 },
      widgetList: [],
      loading: true,
      searchParams: { keyword: undefined, appId: [this.appId], type: 'FUNCTION_WIDGET' },
      page: { currentPage: 1, pageSize: -1, totalCount: 0, autoCount: false }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.refreshList();
  },
  mounted() {},
  methods: {
    resetSearchForm() {
      this.searchParams.keyword = undefined;
      this.refreshList();
    },
    refreshList() {
      this.loading = true;
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
            this.widgetList.push(d);
          }
          this.loading = false;
        })
        .catch(error => {
          this.loading = false;
        });
    },
    editModuleFunctionWidget(uuid) {
      window.open(`/module-widget-design/${uuid}`, '_blank');
    },
    deleteWidget(uuid, index) {
      $axios
        .get(`/proxy/api/user/widgetDef/delete/${uuid}`, { params: {} })
        .then(({ data }) => {
          if (data.code == 0) {
            this.widgetList.splice(index, 1);
            // this.refreshList();
          }
        })
        .catch(error => {});
    },
    enableWidget(item, enabled) {
      $axios
        .get(`/proxy/api/user/widgetDef/enabled/${item.uuid}`, { params: { enabled } })
        .then(({ data }) => {
          if (data.code == 0) {
            item.enabled = enabled;
            this.$message.success(item.enabled ? '已发布' : '取消发布');
          }
        })
        .catch(error => {});
    },
    handleMenuClick(e, item, index) {
      const _this = this;
      if (e.key == 'delete') {
        this.$confirm({
          title: '提示',
          content: '确定要删除吗?',
          okText: '确定',
          cancelText: '取消',
          onOk() {
            _this.deleteWidget(item.uuid, index);
          },
          onCancel() {}
        });
      } else if (e.key == 'publish') {
        this.enableWidget(item, true);
      } else if (e.key == 'unpublish') {
        this.enableWidget(item, false);
      } else if (e.key == 'edit') {
        this.editModuleFunctionWidget(item.uuid);
      }
    },
    addNewModuleFuncWidget(e) {
      this.form.appId = this.appId;
      this.form.type = 'FUNCTION_WIDGET';
      this.form.enabled = false;
      this.form.widgetId = generateId();
      this.$loading('保存中');
      this.form.definitionJson = JSON.stringify({
        items: [],
        thumbnail: undefined
      });
      $axios
        .post(`/proxy/api/user/widgetDef/saveUserDefWidget`, this.form)
        .then(({ data }) => {
          this.$message.success('保存成功');
          this.form.title = undefined;
          this.form.remark = undefined;
          this.$loading(false);
          this.refreshList();
          e(true);
        })
        .catch(error => {
          this.$loading(false);
        });
    }
  }
};
</script>
