<template>
  <div class="biz-org-detail-panel">
    <template v-if="!loading">
      <a-page-header>
        <template slot="title">
          <a-avatar
            shape="square"
            :size="24"
            style="background-color: var(--w-primary-color); color: #fff; margin-right: 8px; margin-top: 4px"
          >
            <Icon slot="icon" :type="'iconfont icon-luojizujian-huoquyonghuliebiao'" />
          </a-avatar>
          <label>{{ bizOrg.name }}</label>
          <a-tag style="cursor: pointer" class="primary-color" v-if="bizOrg.orgName != undefined" @click="redirectToOrgDesign">
            {{ bizOrg.orgName }}
          </a-tag>
          <!-- <label v-if="bizOrg.orgName != undefined">- {{ bizOrg.orgName }} (行政组织)</label> -->
          <a-divider type="vertical" />
          <a-tag v-if="bizOrg.expired" class="pt-tag danger bordered">已失效</a-tag>
          <a-tag v-else :class="[bizOrg.enable ? 'pt-tag success bordered' : '']">{{ bizOrg.enable ? '启用' : '停用' }}</a-tag>
        </template>
        <template slot="extra">
          <a-tag color="orange" v-if="bizOrgElementCount === 0 && bizOrgConfig.uuid == undefined">未配置</a-tag>
          <Modal
            title="业务组织配置"
            :ok="onSaveBizOrgConfig"
            :width="800"
            :destroyOnClose="true"
            :okText="bizOrgConfig.uuid == undefined ? '初始化保存' : '保存'"
            mask
          >
            <template slot="content">
              <BizOrgConfig :biz-org-uuid="bizOrg.uuid" ref="bizOrgConfig" />
            </template>
            <a-button type="link">
              <Icon type="iconfont icon-szgy-shixiangpeizhi"></Icon>
              业务组织配置
            </a-button>
          </Modal>
          <Modal title="业务组织属性" :ok="onSaveBizOrgBasicInfo" :width="800" :destroyOnClose="true" mask>
            <template slot="content">
              <BizOrgBasicInfo :uuid="bizOrg.uuid" ref="bizOrgBasicInfo" />
            </template>
            <a-button type="link">
              <Icon type="iconfont icon-ptkj-yewuguanli"></Icon>
              业务组织属性
            </a-button>
          </Modal>
          <!-- <a-button type="link" icon="security">权限配置</a-button> -->
          <!-- <a-button type="link" icon="link">查看引用</a-button> -->
        </template>
      </a-page-header>
      <a-row type="flex" style="flex-wrap: nowrap; background-color: var(--w-bg-color-layout)">
        <a-col :flex="siderWidth + 'px'" class="silder-content">
          <BizOrgElementTree
            v-if="!loading"
            :biz-org="bizOrg"
            :biz-org-config="bizOrgConfig"
            :key="bizOrgElementTreeKey"
            @selectNode="onSelectTreeNode"
            ref="bizOrgElementTree"
            @onFetchOrgElementModels="onFetchOrgElementModels"
            @fetchBizOrgElementCount="onBizOrgElementCountChange"
          />
          <div
            class="org-version-manager-sider-divider"
            @mousedown="startDrag"
            :class="{ dragging: dragging }"
            :style="{ left: siderWidth + 'px' }"
          >
            <a-divider type="vertical" title="拖动调整宽度"></a-divider>
          </div>
        </a-col>

        <a-col flex="auto" class="table-content">
          <a-card :bordered="false" class="pt-card">
            <template slot="title">
              <div style="display: flex; align-items: center">
                <a-tooltip :overlayStyle="{ maxWidth: 'calc(100vh - 50px)' }" class="">
                  <template slot="title">
                    {{ title }}
                  </template>
                  <label
                    style="margin-right: 7px; max-width: calc(100vh - 50px); text-overflow: ellipsis; overflow-x: hidden; font-weight: bold"
                  >
                    {{ title }}
                  </label>
                </a-tooltip>
                <span v-if="!counting" class="count">
                  <label v-show="viewType == 'userView'">总人数：{{ userCount }} 人</label>
                  <label v-show="viewType == 'roleView'">总角色数：{{ roleCount }} 个</label>
                </span>
                <a-spin v-else />
              </div>
            </template>
            <template slot="extra">
              <a-radio-group
                v-show="bizOrgElementSelected.id != undefined"
                v-model="viewType"
                button-style="solid"
                @change="onChangeViewType"
                style="--w-radio-button-solid-border-radius: 4px"
              >
                <a-radio-button value="userView">成员视图</a-radio-button>
                <a-radio-button value="roleView">角色视图</a-radio-button>
              </a-radio-group>
            </template>
            <div v-show="viewType === 'userView'">
              <BizOrgUserTable ref="bizOrgUserTable" @userCountChange="onUserCountChange" />
            </div>
            <div v-show="viewType === 'roleView'">
              <BizOrgRoleMemberTable ref="bizOrgRoleTable" @roleChanged="onElementRoleChange" />
            </div>
          </a-card>
        </a-col>
      </a-row>
    </template>
    <div class="spin-center" v-else>
      <a-spin />
    </div>
  </div>
</template>
<style lang="less">
.biz-org-detail-panel {
  .silder-content {
    height: e('calc(-77px + 100vh)');
    margin: 12px 0 12px 20px;
    overflow: hidden;
    background-color: #ffffff;
    padding: 12px;
    border-radius: 4px;
  }
  .org-version-manager-sider-divider {
    height: 100%;
    position: absolute;
    margin-left: -11px;
    cursor: col-resize;
    top: 0;

    .ant-divider {
      height: 100%;
      background-color: transparent;
    }
    &.dragging,
    &:hover {
      .ant-divider {
        width: 2px;
        background-color: var(--w-primary-color-3);
      }
    }
  }
  .table-content {
    margin: 12px 20px 12px 12px;
    background-color: #ffffff;
    padding: 12px;
    border-radius: 4px;
  }

  .ant-page-header {
    padding: 0;
    border-bottom: 1px solid var(--w-border-color-light);
    .ant-page-header-heading-title {
      font-size: var(--w-font-size-lg);
      color: var(--w-text-color-dark);
      line-height: 32px;
      padding: 10px var(--w-padding-md);
      font-weight: bold;

      .ant-divider-vertical {
        margin: 0 8px 0 0px;
      }
    }
    .ant-page-header-heading-extra {
      line-height: 52px;
      padding-right: 16px;
    }
  }

  .pt-card {
    .ant-card-head-title {
      .count {
        font-weight: normal;
        font-size: var(--w-font-size-base);
        color: var(--w-text-color-light);
        position: relative;
        padding-left: var(--w-padding-2xs);
        margin-right: var(--w-padding-3xs);
        vertical-align: middle;
        &::before {
          position: absolute;
          content: '';
          width: 1px;
          height: var(--w-font-size-base);
          left: 0;
          top: 50%;
          margin-top: e('calc(0px -  var(--w-font-size-base)/2)');
          background: var(--w-text-color-light);
        }
      }
    }
  }
}
</style>
<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import BizOrgUserTable from './biz-org-user-table.vue';
import BizOrgRoleMemberTable from './biz-org-role-member-table.vue';
import BizOrgElementTree from './biz-org-element-tree.vue';
import BizOrgConfig from './biz-org-config.vue';
import BizOrgBasicInfo from './biz-org-basic-info.vue';

export default {
  name: 'BizOrgDetail',
  inject: ['pageContext'],
  props: {
    bizOrgUuid: String // 业务组织UUID
  },
  components: { Modal, Drawer, BizOrgUserTable, BizOrgRoleMemberTable, BizOrgElementTree, BizOrgConfig, BizOrgBasicInfo },
  data() {
    return {
      loading: true,
      bizOrg: undefined,
      organization: undefined,
      siderWidth: 350,
      bizOrgElementCount: undefined,
      title: '全部',
      userCount: undefined,
      roleCount: undefined,
      viewType: 'userView',
      bizOrgConfig: {
        uuid: undefined,
        bizOrgDimensionId: undefined,
        allowDimensionLevel: undefined,
        syncOrgOption: undefined,
        enableSyncOrg: undefined,
        allowOrgEleModel: [],
        allowOrgLevel: undefined,
        bizOrgUuid: undefined
      },
      bizOrgRoles: [],
      orgElementModels: [],
      bizOrgElementTreeKey: new Date().getTime(),
      bizOrgDimension: {
        id: undefined,
        name: undefined,
        icon: undefined
      },
      bizOrgElementSelected: {
        id: undefined,
        uuid: undefined,
        name: undefined,
        isDimension: false,
        elementType: undefined
      },
      dragging: false
    };
  },
  computed: {},
  provide() {
    return {
      bizOrgRoles: this.bizOrgRoles,
      bizOrgElementSelected: this.bizOrgElementSelected,
      bizOrgDimension: this.bizOrgDimension,
      orgElementModels: this.orgElementModels
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchBizOrg(this.bizOrgUuid).then(() => {});
    this.fetchBizOrgConfig();
    BizOrgConfig.methods.fetchBizOrgRoles(this.bizOrgUuid).then(roles => {
      this.bizOrgRoles.splice(0, this.bizOrgRoles.length);
      this.bizOrgRoles.push(...roles);
    });
    document.addEventListener('mousemove', this.onDrag);
    document.addEventListener('mouseup', this.endDrag);
  },
  mounted() {},
  methods: {
    onChangeViewType() {
      if (this.viewType == 'userView') {
        this.$refs.bizOrgUserTable.refresh();
      } else {
        this.$refs.bizOrgRoleTable.refresh();
      }
    },
    redirectToOrgDesign() {
      window.open(`/org/manager?uuid=${this.bizOrg.orgUuid}`, '_blank');
    },
    onBizOrgElementCountChange(num) {
      this.bizOrgElementCount = num;
    },
    onFetchOrgElementModels(list) {
      this.orgElementModels.push(...list);
    },
    onElementRoleChange(e) {
      this.roleCount = e.rows.length;
    },
    onUserCountChange(num) {
      this.userCount = num;
    },
    onSelectTreeNode(e) {
      let keys = ['id', 'uuid', 'name', 'isDimension', 'elementType'];
      for (let k of keys) {
        this.bizOrgElementSelected[k] = e == undefined ? undefined : e.data[k];
      }

      this.title = e == undefined ? '全部' : e.titlePath;
      if (e == undefined) {
        this.viewType = 'userView';
      }
      this.$refs.bizOrgUserTable.refresh();
      this.$refs.bizOrgRoleTable.refresh();
    },
    fetchBizOrgConfig() {
      return new Promise((resolve, reject) => {
        BizOrgConfig.methods.fetchBizOrgConfig(this.bizOrgUuid).then(data => {
          if (data) {
            for (let key in this.bizOrgConfig) {
              if (key == 'allowOrgEleModel') {
                this.bizOrgConfig.allowOrgEleModel.splice(0, this.bizOrgConfig.allowOrgEleModel.length);
                this.bizOrgConfig.allowOrgEleModel.push(...(data.allowOrgEleModel || '').split(';'));
              } else {
                this.bizOrgConfig[key] = data[key];
              }
            }
          }
          if (this.bizOrgConfig != undefined && this.bizOrgConfig.bizOrgDimensionId) {
            this.fetchBizOrgDimension(this.bizOrgConfig.bizOrgDimensionId);
          } else {
            this.bizOrgDimension.id = undefined;
          }
          BizOrgConfig.methods.fetchBizOrgRoles(this.bizOrgUuid).then(roles => {
            this.bizOrgRoles.splice(0, this.bizOrgRoles.length);
            this.bizOrgRoles.push(...roles);
          });
          if (this.$refs.bizOrgElementTree) {
            this.$refs.bizOrgElementTree.updateTreeNodeCreateOperation();
          }
        });
      });
    },
    onSaveBizOrgBasicInfo(e) {
      this.$refs.bizOrgBasicInfo.save().then(data => {
        this.bizOrg.enable = data.enable;
        this.bizOrg.name = data.name;
        this.bizOrg.e;
        e(true);
      });
    },
    onSaveBizOrgConfig(e) {
      this.$refs.bizOrgConfig.save().then(data => {
        this.fetchBizOrgConfig();
        e(true);
        if (data.enableSyncOrg && data.uuid == undefined) {
          let _this = this;
          this.$confirm({
            title: '组织同步提示',
            content: '组织同步配置已开启, 是否马上进行组织同步?',
            onOk() {
              _this.$refs.bizOrgElementTree.startSyncOrgElement().then(() => {});
            },
            onCancel() {},
            okText: '立即同步',
            cancelText: '取消'
          });
        } else {
          e(true);
        }
      });
    },
    fetchBizOrg(uuid) {
      return new Promise((resolve, reject) => {
        this.$axios.get('/proxy/api/org/biz/getBizOrgByUuid', { params: { uuid } }).then(({ data }) => {
          this.bizOrg = data.data;
          this._provided.bizOrg = this.bizOrg;
          this.loading = false;
          resolve();
        });
      });
    },

    fetchBizOrgDimension(id) {
      if (id) {
        this.$axios
          .get(`/proxy/api/org/biz/getBizOrgDimensionById`, {
            params: {
              id
            }
          })
          .then(({ data }) => {
            if (data.data) {
              for (let key in this.bizOrgDimension) {
                this.bizOrgDimension[key] = data.data[key];
              }
            }
          })
          .catch(error => {});
      }
    },

    startDrag(e) {
      e.preventDefault();
      this.dragging = true;
    },
    // 拖动中
    onDrag(e) {
      if (this.dragging) {
        // 计算水平拖动的距离
        this.siderWidth = e.clientX > 600 ? 600 : e.clientX < 224 ? 224 : e.clientX;
      }
    },
    // 结束拖动
    endDrag() {
      this.dragging = false;
    }
  },
  beforeDestroy() {}
};
</script>
