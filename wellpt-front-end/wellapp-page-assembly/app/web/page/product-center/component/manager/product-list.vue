<template>
  <a-row type="flex" style="flex-wrap: nowrap" class="product-list">
    <a-col flex="240px" style="height: 100%; border-right: 1px solid #e8e8e8" v-show="!categoryColCollapsed" @click="clickCategoryZone">
      <a-row type="flex" style="padding: var(--w-padding-md) var(--w-padding-md) 0">
        <a-col flex="auto">
          <a-input-search v-model.trim="categroyKeyword" allow-clear style="width: 156px" />
        </a-col>
        <a-col flex="32px">
          <a-dropdown :trigger="['click']" ref="newCategoryDropdown">
            <a-button icon="plus" title="新建分类" />
            <div
              slot="overlay"
              @click.stop="() => {}"
              :style="{ backgroundColor: '#fff', padding: '5px 10px', outline: '1px solid #e5e5e5', borderRadius: '3px' }"
            >
              <a-input-group compact>
                <a-input @click.stop="() => {}" style="width: 200px" placeholder="请输入分类名称" />
                <a-button type="primary" @click.stop="e => confirmNewProdCategory(e)" :loading="saveNCategoryLoading">保存</a-button>
              </a-input-group>
            </div>
          </a-dropdown>
        </a-col>
      </a-row>
      <Scroll style="height: calc(100% - 52px); padding: var(--w-padding-xs) var(--w-padding-md)">
        <!-- 产品分类 -->
        <a-row class="category-row" type="flex">
          <a-col flex="auto" @click="selectAllCategory">
            <a-icon :type="collapseCategory ? 'caret-right' : 'caret-down'" @click.stop="collapseCategory = !collapseCategory" />
            全部
          </a-col>
        </a-row>

        <div class="spin-loading-center" v-show="categoryLoading">
          <a-spin :spinning="categoryLoading" />
        </div>
        <a-row
          :class="['category-row', searchParam.categoryUuid == ctg.uuid ? 'selected' : '']"
          type="flex"
          v-for="(ctg, i) in categoryList"
          :key="ctg.uuid"
          v-show="!collapseCategory && (!categroyKeyword || (categroyKeyword && ctg.name.indexOf(categroyKeyword) > -1))"
        >
          <a-col flex="auto" @click="clickCategorySearch(ctg)">
            <span v-show="!ctg.EDIT">{{ ctg.name }}</span>
            <a-input size="small" @click.stop="() => {}" v-model="ctg.rename" v-show="ctg.EDIT" style="width: 130px">
              <a-icon slot="suffix" :type="ctg.renaming ? 'loading' : 'save'" title="保存" @click.stop="rename(ctg)" />
            </a-input>
          </a-col>
          <a-col flex="26px">
            <a-dropdown :trigger="['click']">
              <a-button :icon="ctg.deleting ? 'loading' : ''" type="icon" size="small" style="--w-button-icon-size: 18px" title="更多操作">
                <Icon v-if="!ctg.deleting" type="iconfont icon-ptkj-gengduocaozuo" />
              </a-button>
              <a-menu slot="overlay" @click="e => handleCategoryMenuClick(e, ctg, i)">
                <a-menu-item key="delete">删除</a-menu-item>
                <a-menu-item key="edit">编辑</a-menu-item>
              </a-menu>
            </a-dropdown>
          </a-col>
        </a-row>
      </Scroll>
    </a-col>
    <a-col flex="auto" style="padding: var(--w-padding-md); height: 100%">
      <div class="collapse-bar" @click="collapseCategoryCol" :title="categoryColCollapsed ? '展开' : '收起'">
        <a-icon :type="categoryColCollapsed ? 'right' : 'left'" />
      </div>
      <div v-if="searchParam.name" style="color: var(--w-text-color-dark); line-height: 32px; font-weight: bold; margin-bottom: 12px">
        {{ searchParam.name }}
      </div>
      <div style="margin-bottom: 12px">
        <Modal ref="newProductModal" :ok="onConfirmSaveProd" :width="600" dialogClass="product-manager-info-form-modal">
          <template slot="title">
            新建产品
            <span style="color: var(--w-text-color-light); font-size: var(--w-font-size-sm)">｜定义产品基本信息</span>
          </template>
          <template slot="content">
            <ProductBasicInfo ref="prodBasicInfo" />
          </template>
          <a-button type="primary">
            <Icon type="iconfont icon-ptkj-jiahao" />
            新建
          </a-button>
        </Modal>
        <ImportDef title="导入产品" @importDone="importDone">
          <a-button icon="import">导入</a-button>
        </ImportDef>
        <a-input-search
          v-model.trim="searchParam.keyword"
          @search="refreshTable"
          allow-clear
          style="width: 200px; float: right; margin-left: var(--w-margin-xs)"
        />
        <a-button-group style="float: right">
          <a-button :type="searchParam.status.includes('BUILDING') ? 'primary' : 'default'" @click="checkStatus('BUILDING')">
            构建中
          </a-button>
          <a-button :type="searchParam.status.includes('LAUNCH') ? 'primary' : 'default'" @click="checkStatus('LAUNCH')">已上线</a-button>
          <a-button :type="searchParam.status.includes('NOT_LAUNCH') ? 'primary' : 'default'" @click="checkStatus('NOT_LAUNCH')">
            已下线
          </a-button>
        </a-button-group>
      </div>
      <a-table
        :columns="columns"
        :data-source="productDataSource"
        :showHeader="false"
        :pagination="pagination"
        :loading="loading"
        @change="onTableChange"
        class="product-list-table"
        :style="{ height: searchParam.name ? 'calc(100% - 144px)' : 'calc(100% - 100px)' }"
      >
        <template slot="nameSlot" slot-scope="text, record">
          <a-row type="flex">
            <a-col flex="64px">
              <a-avatar :size="54" :style="iconColorSet(record.status)">
                <Icon :type="record.icon || 'appstore'" slot="icon" style="font-size: 30px; line-height: 50px" />
              </a-avatar>
            </a-col>
            <a-col flex="auto">
              <div
                :title="record.name"
                class="w-ellipsis"
                style="max-width: 200px; font-size: var(--w-font-size-base); color: var(--w-text-color-dark); font-weight: bold"
              >
                {{ record.name }}
              </div>
              <div
                class="w-ellipsis-2"
                :title="record.remark"
                style="max-width: 200px; color: var(--w-text-color-base); font-size: var(--w-font-size-sm)"
              >
                {{ record.remark }}
              </div>
            </a-col>
          </a-row>
        </template>
        <template slot="statusSlot" slot-scope="text, record">
          <a-tag color="blue" class="tag-no-border" v-if="record.status == 'BUILDING'">构建中</a-tag>
          <a-tag color="green" class="tag-no-border" v-else-if="record.status == 'LAUNCH'">已上线</a-tag>
          <a-tag v-else class="tag-no-border">已下线</a-tag>
        </template>
        <template slot="versionSlot" slot-scope="text, record">
          <div style="color: var(--w-text-color-light)">最新发布版本</div>
          <div style="color: var(--w-text-color-dark)">{{ record.status == 'BUILDING' ? '-' : record.latestVersion.version }}</div>
        </template>
        <template slot="publishTimeSlot" slot-scope="text, record">
          <div style="color: var(--w-text-color-light)">发布时间</div>
          <div style="color: var(--w-text-color-dark)">{{ record.latestVersion.publishTime || '-' }}</div>
        </template>
        <template slot="operationSlot" slot-scope="text, record">
          <a-button size="small" type="link" @click="openProductDetail(record)">
            <Icon type="pticon iconfont icon-wsbs-anbinglianjieduan"></Icon>
            配置
          </a-button>
          <a-dropdown :trigger="['click']">
            <a-button size="small" type="link" @click="e => e.preventDefault()" title="更多操作">
              <Icon type="pticon iconfont icon-ptkj-gengduocaidan"></Icon>
            </a-button>
            <a-menu slot="overlay" @click="e => handleMoreClick(e, record, index)">
              <a-menu-item key="publishLaunch" v-if="record.status === 'BUILDING' || record.status === 'NOT_LAUNCH'">
                {{ record.status === 'BUILDING' ? '发布上线' : '上线' }}
              </a-menu-item>
              <a-menu-item key="preview">预览</a-menu-item>
              <a-menu-item key="delist" v-show="record.status === 'LAUNCH'">下线</a-menu-item>
              <a-menu-item key="export">导出</a-menu-item>
              <a-menu-item
                key="delete"
                v-if="!(ptProdId.includes(record.id) || record.id.startsWith('pt_') || record.id.startsWith('pt-'))"
              >
                删除
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </template>
      </a-table>
      <div style="text-align: right; margin-top: 20px">
        <a-pagination v-model="pagination.current" :pageSize="pagination.pageSize" @change="onChangePagination" :total="pagination.total" />
      </div>
      <ExportDef ref="exportDef" title="导出产品" :uuid="exportProdUuid" type="appProduct" modifyRange />
    </a-col>
  </a-row>
</template>
<style lang="less">
.product-list {
  height: 100%;
  .ant-col:last-child {
    .collapse-bar {
      position: absolute;
      opacity: 0;
      cursor: pointer;
      left: -1px;
      top: 45%;
      z-index: 100;
      border-radius: 0 4px 4px 0px;
      height: 48px;
      width: 20px;
      line-height: 48px;
      text-align: center;
      background-color: var(--w-fill-color-base);
      color: var(--w-text-color-dark);
      border: 1px solid var(--w-border-color-light);
      border-left: 0;
      font-size: 10px;
      -webkit-transition: opacity 0.3s linear;
      transition: opacity 0.3s linear;
    }
    &:hover {
      .collapse-bar {
        opacity: 1;
      }
    }
  }

  .product-list-table {
    height: e('calc(100% - 100px)');
    overflow: auto;
    .ant-table-pagination {
      display: none;
    }
  }
}
.prod-manager-list-tabs {
  // .ant-table-scroll {
  //   outline: 1px solid #e8e8e8;
  // }
  .category-row {
    padding: 8px 0px 8px 12px;
    cursor: pointer;
    border-radius: 4px;
    &.selected {
      background-color: var(--w-primary-color-1);
      > div {
        color: var(--w-primary-color);
      }
    }

    > div {
      > i {
        margin-right: 5px;
        color: var(--w-text-color-light);
      }
      align-items: center;
      display: flex;
      justify-content: flex-start;
      font-size: var(--w-font-size-base);
      color: var(--w-color-black);
      > span {
        padding-left: 20px;
        text-overflow: ellipsis;
        white-space: nowrap;
        width: 120px;
        overflow: hidden;
      }
    }

    &:hover {
      background-color: var(--w-primary-color-1);
    }
  }
}
</style>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import CreateProductSteps from './create-product-steps.vue';
import ProductBasicInfo from './product-basic-info.vue';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';
import { getTopContainer } from '@framework/vue/utils/function.js';

export default {
  name: 'ProductList',
  props: {},
  components: { Modal, Drawer, CreateProductSteps, ProductBasicInfo, ImportDef, ExportDef },
  computed: {},
  data() {
    return {
      prodBasicInfoModalVisible: false,
      step: 0,
      saveNCategoryLoading: false,
      categoryLoading: true,
      categroyKeyword: '',
      categoryList: [],
      collapseCategory: false,
      categoryColCollapsed: false,
      columns: [
        {
          title: '名称',
          dataIndex: 'name',
          scopedSlots: { customRender: 'nameSlot' }
        },
        {
          title: '状态',
          width: 100,
          dataIndex: 'status',
          scopedSlots: { customRender: 'statusSlot' }
        },
        {
          title: '版本号',
          width: 150,
          dataIndex: 'version',
          scopedSlots: { customRender: 'versionSlot' }
        },
        {
          title: '发布时间',
          width: 200,
          dataIndex: 'publishTime',
          scopedSlots: { customRender: 'publishTimeSlot' }
        },
        {
          title: '操作',
          width: 150,
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0
      },
      loading: true,
      searchParam: {
        categoryUuid: undefined,
        keyword: undefined,
        name: '',
        status: ['BUILDING', 'LAUNCH'] // 默认构建中，已上线
      },
      productDataSource: [],
      exportProdUuid: undefined,
      ptProdId: ['PRD_PT', 'system_manager']
    };
  },

  beforeCreate() {},
  created() {},
  beforeMount() {
    this.refreshTable();
    this.fetchCategories();
  },
  mounted() {},
  methods: {
    getTopContainer,
    iconColorSet(status) {
      if (status == 'BUILDING') {
        // 构建中，蓝色
        return {
          backgroundColor: '#e6f7ff',
          color: '#1890ff'
        };
      } else if (status == 'LAUNCH') {
        // 已上线，绿色
        return {
          backgroundColor: '#f6ffed',
          color: '#52c41a'
        };
      } else {
        // 下线，灰色
        return {
          backgroundColor: 'var(--w-tag-background)',
          color: 'var(--w-text-color-base)'
        };
      }
    },
    importDone() {
      this.refreshTable();
    },
    clickCategoryZone() {
      if (this.categoryList) {
        for (let c of this.categoryList) {
          c.EDIT = false;
        }
      }
    },
    rename(item) {
      let submitData = JSON.parse(JSON.stringify(item));
      submitData.name = item.rename;
      this.$set(item, 'renaming', true);
      $axios
        .post(`/proxy/api/app/category/save`, submitData)
        .then(({ data }) => {
          this.$set(item, 'renaming', false);
          if (data.code == 0) {
            item.rename = undefined;
            item.name = submitData.rename;
            item.EDIT = false;
          }
        })
        .catch(() => {
          this.$set(item, 'renaming', false);
        });
    },
    collapseCategoryCol() {
      this.categoryColCollapsed = !this.categoryColCollapsed;
    },
    onConfirmSaveProd(e) {
      this.$refs.prodBasicInfo.saveProduct().then(() => {
        e(true);
        this.refreshTable();
      });
    },
    clickCategorySearch(item) {
      this.searchParam.categoryUuid = item.uuid;
      this.searchParam.name = item.name;
      item.EDIT = false;
      this.refreshTable();
    },
    selectAllCategory() {
      this.searchParam.categoryUuid = undefined;
      this.searchParam.name = '';
      this.refreshTable();
    },
    fetchCategories() {
      $axios
        .get(`/proxy/api/app/category/getAll`, { params: { applyTo: 'AppProduct' } })
        .then(({ data }) => {
          this.categoryLoading = false;
          if (data.code == 0) {
            for (let d of data.data) {
              d.EDIT = false;
              this.categoryList.push(d);
            }
          }
        })
        .catch(error => {});
    },
    checkStatus(status) {
      let idx = this.searchParam.status.indexOf(status);
      if (idx == -1) {
        this.searchParam.status.push(status);
      } else {
        this.searchParam.status.splice(idx, 1);
      }
      this.refreshTable();
    },
    saveProduct() {
      let _this = this;
      this.$refs.productStep.save(() => {
        _this.$refs.newProductDrawer.onCancel();
      });
    },
    deleteProduct(item, index) {
      this.$set(item, 'deleting', true);
      $axios
        .get(`/proxy/api/app/prod/delete/${item.uuid}`, { params: {} })
        .then(({ data }) => {
          this.$set(item, 'deleting', false);
          if (data.code == 0) {
            this.productDataSource.splice(index, 1);
            this.refreshTable();
          }
        })
        .catch(error => {
          this.$set(item, 'deleting', false);
        });
    },
    updateProdLaunch(item, launch) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/updateStatus`, { params: { uuid: item.uuid, status: launch ? 'LAUNCH' : 'NOT_LAUNCH' } })
          .then(({ data }) => {
            if (data.code == 0) {
              item.status = launch ? 'LAUNCH' : 'NOT_LAUNCH';
              this.$message.success(launch ? '上线成功' : '下线成功');
              item.latestVersion.version = data.data.latestVersion.version;
              item.latestVersion.publishTime = data.data.latestVersion.publishTime;
            } else {
              this.$message.error((launch ? '上线' : '下线') + '失败');
            }
          })
          .catch(error => {
            this.$message.error((launch ? '上线' : '下线') + '失败');
          });
      });
    },
    updateProdVersion(uuid, status) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/version/updateStatus`, { params: { uuid, status } })
          .then(({ data }) => {
            if (data.code == 0) {
              resolve();
            }
          })
          .catch(error => {});
      });
    },
    setPublishLaunched(item) {
      this.updateProdLaunch(item, true).then(() => {
        if (item.latestVersion.uuid == undefined) {
          $axios
            .get(`/proxy/api/app/prod/version/queryEarliestCreate`, { params: { prodId: item.id } })
            .then(({ data }) => {
              if (data.data) {
                this.updateProdVersion(data.data.uuid, 'PUBLISHED').then(() => {
                  this.$message.success('发布上线成功');
                });
              }
            })
            .catch(error => {});
        }
      });
    },
    openProductDetail(item) {
      window.open(`/product/assemble/${item.uuid}`, '_blank');
    },
    handleCategoryMenuClick(e, ctg, i) {
      if (e.key == 'delete') {
        this.deleteCategory(ctg, i);
      } else if (e.key == 'edit') {
        if (!ctg.EDIT) {
          this.$set(ctg, 'rename', ctg.name);
          // ctg.rename = ctg.name;
        }
        ctg.EDIT = true;
      }
    },
    handleMoreClick(e, item, index) {
      let key = e.key;
      let _this = this;
      if (key == 'publishLaunch') {
        this.updateProdLaunch(item, true);
      } else if (key == 'delist') {
        this.updateProdLaunch(item, false);
      } else if (key == 'export') {
        this.exportProdUuid = item.uuid;
        this.$refs.exportDef.show();
      } else if (key == 'delete') {
        this.$confirm({
          title: '提示',
          content: `确定要删除产品${item.name}吗?`,
          getContainer: this.getTopContainer,
          onOk() {
            _this.deleteProduct(item, index);
          }
        });
      } else if (key == 'preview') {
        this.getLatestPublishedVersionIndexUrl(item).then(url => {
          if (url) {
            window.open(url, '_blank');
          } else {
            this.$message.info('产品未发布上线版本或者版本未配置默认访问地址');
          }
        });
      }
    },
    getLatestPublishedVersionIndexUrl(item) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/product/latestPublishedIndexUrl`, {
            params: {
              prodId: item.id
            }
          })
          .then(({ data }) => {
            resolve(data);
          })
          .catch(error => {});
      });
    },
    onChangePagination() {
      this.onTableChange();
    },
    onTableChange(pagination, filters, sorter) {
      if (typeof this.pagination !== 'boolean' && pagination) {
        this.pagination.current = pagination.current;
        this.pagination.pageSize = pagination.pageSize;
      }
      this.fetchProductList();
    },
    fetchProductList() {
      this.loading = true;
      if (!this._hasAnyRole('ROLE_ADMIN')) {
        this.searchParam.excludeIds = this.ptProdId;
      }
      $axios
        .post(`/proxy/api/app/prod/query`, {
          ...this.searchParam,
          page: {
            currentPage: this.pagination.current,
            pageSize: this.pagination.pageSize
          }
        })
        .then(({ data }) => {
          this.loading = false;
          if (data.code == 0) {
            this.productDataSource = [];
            this.pagination.total = data.data.page.totalCount;
            let prodIds = [],
              prodMap = {};
            for (let d of data.data.data) {
              d.latestVersion = {
                uuid: undefined,
                version: undefined,
                publishTime: undefined,
                status: undefined
              };
              this.productDataSource.push(d);
              prodMap[d.id] = d;
              prodIds.push(d.id);
            }
            this.setLatestVersions(prodIds, prodMap);
          }
        })
        .catch(error => {});
    },
    setLatestVersions(prodIds, prodMap) {
      $axios
        .post(`/proxy/api/app/prod/version/queryLatestPub`, prodIds)
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            for (let d of data.data) {
              if (prodMap[d.prodId]) {
                prodMap[d.prodId].latestVersion.uuid = d.uuid;
                prodMap[d.prodId].latestVersion.version = d.version;
                prodMap[d.prodId].latestVersion.publishTime = d.publishTime;
                prodMap[d.prodId].latestVersion.status = d.status;
              }
            }
          }
        })
        .catch(error => {});
    },
    refreshTable() {
      this.pagination.current = 1;
      this.fetchProductList();
    },
    deleteCategory(item, i) {
      this.$set(item, 'deleting', true);
      $axios
        .get(`/proxy/api/app/category/delete/${item.uuid}`, { params: {} })
        .then(({ data }) => {
          this.$set(item, 'deleting', false);
          if (data.code == 0) {
            this.categoryList.splice(i, 1);
            if (this.searchParam.categoryUuid == item.uuid) {
              this.searchParam.categoryUuid = undefined;
              this.refreshTable();
            }
          }
        })
        .catch(error => {
          this.$set(item, 'deleting', false);
        });
    },
    confirmNewProdCategory(e) {
      let name = e.target.previousElementSibling.value.trim();
      if (name) {
        this.saveNCategoryLoading = true;
        $axios.post(`/proxy/api/app/category/save`, { name, applyTo: 'AppProduct' }).then(({ data }) => {
          if (data.code == 0) {
            this.categoryList.push({
              uuid: data.data,
              EDIT: false,
              name,
              applyTo: 'AppProduct'
            });
          }
          this.saveNCategoryLoading = false;
          e.target.previousSibling.__vue__.handleReset();
        });
      }
    }
  }
};
</script>
