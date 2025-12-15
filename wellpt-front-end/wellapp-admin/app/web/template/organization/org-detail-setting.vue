<template>
  <div class="org-detail-setting">
    <template v-if="!notAllowed">
      <div v-if="loading" class="spin-center">
        <a-spin />
      </div>
      <template v-else>
        <a-page-header>
          <template slot="title">
            <label>{{ currentOrgVersion.organization ? currentOrgVersion.organization.name : null }}</label>
          </template>
          <template slot="extra">
            <div class="flex">
              <div style="height: 56px">
                <svg
                  version="1.1"
                  id="图层_1"
                  xmlns="http://www.w3.org/2000/svg"
                  xmlns:xlink="http://www.w3.org/1999/xlink"
                  x="0px"
                  y="0px"
                  width="56"
                  height="56"
                  viewBox="0 0 56 56"
                  xml:space="preserve"
                >
                  <g id="dhbj">
                    <path :style="{ fill: stateColor.bgColor }" d="M56,0H0c0,0,19.5,0,28,28s28,28,28,28V0z" />
                  </g>
                </svg>
              </div>
              <div :style="{ background: stateColor.bgColor, paddingRight: '12px' }">
                <a-space>
                  <a-tag v-if="enableVersionMgr && currentOrgVersion.state" :color="stateColor.color" style="border-radius: 16px">
                    <a-icon type="clock-circle" v-if="currentOrgVersion.state == 'DESIGNING' && currentOrgVersion.publishTime" />
                    {{
                      currentOrgVersion.state == 'PUBLISHED'
                        ? '正式版'
                        : currentOrgVersion.state == 'DESIGNING'
                        ? '设计版'
                        : '历史版' + currentOrgVersion.ver.toFixed(1)
                    }}
                  </a-tag>
                  <a-dropdown v-if="enableVersionMgr">
                    <a-button type="text">
                      <Icon type="pticon iconfont icon-ptkj-xiangmuguanli"></Icon>
                      版本管理
                    </a-button>
                    <a-menu slot="overlay" @click="handleVersionMenuItemClick">
                      <a-menu-item key="backToPublished" v-show="currentOrgVersion.state != 'PUBLISHED'">
                        <label>
                          <a-icon type="rollback" />
                          返回正式版
                        </label>
                      </a-menu-item>
                      <a-menu-item key="createNewVersion">
                        <Modal title="创建新版本" :ok="confirmCreateNewVersion" :width="360" :bodyStyle="{ height: '160px' }" mask>
                          <label>
                            <a-icon type="plus" />
                            创建新版本
                          </label>
                          <template slot="content">
                            <div>
                              <a-button
                                :type="createNewVersion.type == '0' ? 'primary' : 'default'"
                                block
                                @click="createNewVersion.type = '0'"
                              >
                                以空组织创建
                              </a-button>
                              <a-button
                                style="margin-top: 10px"
                                :type="createNewVersion.type == '1' ? 'primary' : 'default'"
                                block
                                @click="createNewVersion.type = '1'"
                              >
                                以当前组织版本的副本进行创建
                              </a-button>
                              <a-checkbox style="margin-top: 10px" v-model="createNewVersion.copyUser">拷贝当前组织版本的用户</a-checkbox>
                            </div>
                          </template>
                        </Modal>
                      </a-menu-item>
                      <a-menu-item key="viewHistory">
                        <Modal
                          title="查看历史版本"
                          v-model="viewHistoryVisible"
                          :destroyOnClose="true"
                          :width="900"
                          :bodyStyle="{ height: '500px' }"
                          mask
                          :cancel="closeViewHistoryVisible"
                        >
                          <label>
                            <a-icon type="clock-circle" />
                            查看历史版本
                          </label>
                          <template slot="content">
                            <OrgVersionHistoryTable :org-uuid="orgUuid" :org-id="orgId" @watch-version-detail="watchVersionDetail" />
                          </template>
                          <template slot="footer">
                            <a-button @click="viewHistoryVisible = false">关闭</a-button>
                          </template>
                        </Modal>
                      </a-menu-item>
                    </a-menu>
                  </a-dropdown>
                  <a-button type="text" :disabled="!orgSettingFetched" @click="orgVersionConstuctVisible = !orgVersionConstuctVisible">
                    <Icon type="pticon iconfont icon-ptkj-zuzhijiagoufenjishitu"></Icon>
                    查看架构图
                  </a-button>
                  <Modal title="组织信息" :ok="confirmSaveOrgBasicInfo" :destroyOnClose="true" mask>
                    <a-button type="text" v-show="currentOrgVersion.state != 'HISTORY'">
                      <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                      设置
                    </a-button>
                    <a-button type="text" v-show="currentOrgVersion.state == 'HISTORY'">
                      <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
                      查看
                    </a-button>
                    <template slot="content">
                      <OrgBasicInfo
                        v-if="currentOrgVersion.organization && orgSettingFetched"
                        :orgSetting="orgSetting"
                        :uuid="currentOrgVersion.organization.uuid"
                        :displayState="displayState"
                        ref="orgBasicInfo"
                      />
                    </template>
                  </Modal>
                </a-space>
              </div>
            </div>
          </template>
        </a-page-header>
        <a-row type="flex" style="flex-wrap: nowrap">
          <a-col :flex="siderWidth + 'px'" style="height: calc(100vh - 58px); padding: 20px 12px; overflow: hidden">
            <OrgVersionManageSider
              :orgSetting="orgSetting"
              :orgVersionUuid="currentOrgVersion.uuid"
              v-if="currentOrgVersion.uuid && orgSettingFetched"
              @selected="onVersionSiderSelected"
              :key="orgVersionManageSiderKey"
              :displayState="displayState"
              ref="orgVersionMangeSider"
              @orgElementChange="startSyncBizOrg"
            />
            <div
              class="org-version-manager-sider-divider"
              @mousedown="startDrag"
              :class="{ dragging: dragging }"
              :style="{ left: siderWidth + 'px' }"
            >
              <a-divider type="vertical"></a-divider>
            </div>
          </a-col>
          <a-col flex="auto">
            <a-card :bordered="false" class="pt-card">
              <template slot="title">
                <div style="display: flex; align-items: center">
                  <a-tooltip :overlayStyle="{ maxWidth: 'calc(100vh - 50px)' }" class="">
                    <template slot="title">
                      {{ title }}
                    </template>
                    <label style="margin-right: 7px; max-width: calc(100vh - 50px); text-overflow: ellipsis; overflow-x: hidden">
                      {{ title }}
                    </label>
                  </a-tooltip>
                  <span v-if="!counting" class="count">总人数：{{ userCnt }}人</span>
                  <a-spin v-else />
                </div>
              </template>
              <template slot="extra" v-if="currentOrgVersion.state == 'DESIGNING'">
                <a-button icon="cloud-upload" @click="e => publishVersion(e)" style="margin-right: var(--w-padding-3xs)">发布</a-button>
                <Modal title="定时发布" :ok="e => publishVersion(e, 1)">
                  <a-button icon="clock-circle" style="margin-right: var(--w-padding-3xs)">定时发布</a-button>
                  <template slot="content">
                    <a-form-model
                      :model="timePublish"
                      :label-col="{ span: 5 }"
                      :wrapper-col="{ span: 19 }"
                      :rules="timePublishRules"
                      ref="timePublishForm"
                      :colon="false"
                    >
                      <a-form-model-item label="选择发布时间" prop="time">
                        <a-date-picker :disabled-date="disabledDate" v-model="timePublish.time" />
                      </a-form-model-item>
                    </a-form-model>
                  </template>
                </Modal>
                <a-button type="danger" icon="delete" @click="deleteVersion">删除</a-button>
              </template>
              <OrgVersionUserTable
                :orgVersionUuid="currentOrgVersion.uuid"
                v-if="currentOrgVersion.uuid"
                :org-version="currentOrgVersion"
                :org-element-id="selectedOrgElementId"
                :org-role-uuid="selectedOrgRoleUuid"
                :displayState="displayState"
                :key="orgVersionUserTableKey"
                ref="orgVersionUserTable"
              />
            </a-card>
          </a-col>
        </a-row>
        <a-modal
          class="pt-modal"
          title="查看架构图"
          v-model="orgVersionConstuctVisible"
          :destroyOnClose="true"
          :width="900"
          :maxHeight="600"
          :bodyStyle="{ height: '600px' }"
          mask
          centered
        >
          <template slot="footer">
            <a-button @click="closeOrgVersionConstuct">关闭</a-button>
          </template>
          <OrgVersionConstuct />
        </a-modal>
      </template>
    </template>
    <Error v-else :error-code="403"></Error>
  </div>
</template>
<style lang="less">
.org-detail-setting {
  .org-version-manager-sider-divider {
    height: 100%;
    position: absolute;
    margin-left: -11px;
    cursor: col-resize;
    top: 0;

    .ant-divider {
      height: 100%;
      background-color: var(--w-border-color-light);
    }
    &.dragging,
    &:hover {
      .ant-divider {
        width: 2px;
        background-color: var(--w-primary-color-3);
      }
    }
  }

  .ant-page-header {
    padding: 0;
    border-bottom: 1px solid var(--w-border-color-light);
    .ant-page-header-heading-title {
      font-size: var(--w-font-size-lg);
      color: var(--w-text-color-dark);
      line-height: 32px;
      padding: var(--w-padding-xs) var(--w-padding-md);
    }
    .ant-page-header-heading-extra {
      line-height: 56px;
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
import OrgVersionConstuct from './org-version-constuct.vue';
import OrgBasicInfo from './org-basic-info.vue';
import OrgVersionHistoryTable from './org-version-history-table.vue';
import OrgVersionManageSider from './org-version-manage-sider.vue';
import OrgVersionUserTable from './org-version-user-table.vue';
import moment from 'moment';

export default {
  name: 'OrgDetailSetting',
  inject: ['pageContext'],
  props: {
    orgUuid: String, // 组织UUID
    orgId: String // 组织ID
  },
  components: { OrgVersionConstuct, Modal, Drawer, OrgBasicInfo, OrgVersionHistoryTable, OrgVersionManageSider, OrgVersionUserTable },
  data() {
    return {
      currentOrgVersion: {},
      viewHistoryVisible: false,
      title: '全部人员',
      userCnt: 0,
      selectedOrgElementId: undefined,
      selectedOrgRoleUuid: undefined,
      orgVersionUserTableKey: 'orgVersionUserTable_0',
      orgVersionManageSiderKey: 'orgVersionManageSiderKey_0',
      createNewVersion: {
        type: '0',
        copyUser: false
      },
      orgSetting: {},
      orgSettingFetched: false,
      timePublish: {
        time: undefined
      },
      timePublishRules: {
        time: [{ required: true, message: '请选择日期', trigger: ['blur', 'change'] }]
      },
      siderWidth: 224,
      dragging: false,
      orgVersionConstuctVisible: false,
      loading: true,
      notAllowed: false,
      orgElementManagements: [],
      orgElementManagementMap: {}
    };
  },
  computed: {
    enableVersionMgr() {
      return this.orgSetting && this.orgSetting['ORG_VERSION_ENABLE'] && this.orgSetting['ORG_VERSION_ENABLE'].enable;
    },
    displayState() {
      return this.currentOrgVersion.state == 'HISTORY' ? 'label' : 'edit';
    },
    stateColor() {
      let color = {
        color: 'var(--w-gray-color-9)',
        bgColor: 'var(--w-fill-color-light)'
      };
      if (this.currentOrgVersion.state == 'PUBLISHED') {
        color = {
          color: 'var(--w-success-color)',
          bgColor: 'var(--w-success-color-2)'
        };
      } else if (this.currentOrgVersion.state == 'DESIGNING') {
        color = {
          color: 'var(--w-warning-color)',
          bgColor: 'var(--w-warning-color-2)'
        };
      }
      return color;
    }
  },
  provide() {
    return {
      getOrgElementTreeData: this.getOrgElementTreeData,
      getOrgElementTreeNodeMap: this.getOrgElementTreeNodeMap,
      getOrgElementModels: this.getOrgElementModels,
      getOrgRole: this.getOrgRole,
      currentOrgVersion: this.currentOrgVersion,
      orgSetting: this.orgSetting,
      orgElementManagementMap: this.orgElementManagementMap
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.hasPermissionToEditOrgConstruct().then(t => {
      this.notAllowed = !t[0];

      console.log('hasPermissionToEditOrgConstruct', t);
      if (!this.notAllowed) {
        Promise.all([this.fetchPublishedOrgVersion(), this.getSetting()]).then(() => {
          this._provided.orgElementManageLimited = false;
          if (
            !(
              this._hasAnyRole('ROLE_TENANT_ADMIN', 'ROLE_ADMIN') ||
              (this.currentOrgVersion.organization.manager && this.currentOrgVersion.organization.manager.includes(this._$USER.userId))
            )
          ) {
            // 限制可查阅编辑的组织、用户数据
            if (t[1] && t[1].length > 0) {
              for (let item of t[1]) {
                if (item.enableAuthority) {
                  if (item.orgAuthority) {
                    item.orgAuthority = JSON.parse(item.orgAuthority);
                  }
                  if (item.userAuthority) {
                    item.userAuthority = item.userAuthority.split(';');
                  }
                  this.orgElementManagements.push(item);
                }
              }
              this._provided.orgElementManageLimited = true;
              this.setOrgElementManagementMap();
            }
          }

          this.$emit('changeOrgVersion', this.currentOrgVersion);
          this.loading = false;
          document.addEventListener('mousemove', this.onDrag);
          document.addEventListener('mouseup', this.endDrag);
        });
      }
    });
  },
  mounted() {},
  methods: {
    setOrgElementManagementMap() {
      if (this.orgElementManagements.length > 0) {
        for (let o of this.orgElementManagements) {
          if (this.currentOrgVersion.uuid == o.orgVersionUuid) {
            this.orgElementManagementMap[o.orgElementUuid] = o;
            this.orgElementManagementMap[o.orgElementId] = o;
          }
        }
      } else {
        this.orgElementManagementMap = {};
      }
      this._provided.orgElementManagementMap = this.orgElementManagementMap;
    },
    hasPermissionToEditOrgConstruct() {
      return new Promise((resolve, reject) => {
        Promise.all([
          $axios.get(`/proxy/api/org/organization/hasPermissionToEditOrgConstruct`, { params: { orgUuid: this.orgUuid } }),
          $axios.get(`/proxy/api/org/organization/listUserOrgElementManagements`, { params: { orgUuid: this.orgUuid } })
        ]).then(responses => {
          resolve([responses[0].data.data, responses[1].data.data]);
        });
      });
    },
    getOrgRole() {
      return this.$refs.orgVersionMangeSider ? this.$refs.orgVersionMangeSider.orgRoleTreeData1 : [];
    },
    getOrgElementTreeData() {
      return this.$refs.orgVersionMangeSider ? this.$refs.orgVersionMangeSider.orgElementTreeData : [];
    },
    getOrgElementTreeNodeMap() {
      return this.$refs.orgVersionMangeSider ? this.$refs.orgVersionMangeSider.treeKeyNodeMap : {};
    },
    getOrgElementModels() {
      return this.$refs.orgVersionMangeSider ? this.$refs.orgVersionMangeSider.orgElementModels : {};
    },

    disabledDate(current) {
      return current && current < moment().add(-1, 'd').endOf('day');
    },

    publishVersion(e, type) {
      if (type === 1) {
        let _this = this;
        this.$refs.timePublishForm.validate(valid => {
          if (valid) {
            $axios
              .post('/proxy/api/org/organization/version/setPublishTime', {
                uuid: _this.currentOrgVersion.uuid,
                publishTime: _this.timePublish.time.toDate().getTime()
              })
              .then(({ data }) => {
                if (data.code == 0) {
                  _this.$message.success('发布设置成功');
                  e(true);
                }
              })
              .catch(error => {
                _this.$message.error('服务异常');
              });
          }
        });
      } else {
        $axios
          .get('/proxy/api/org/organization/version/publish', { params: { uuid: this.currentOrgVersion.uuid } })
          .then(({ data }) => {
            if (data.code == 0) {
              this.$message.success('发布成功');
              this.currentOrgVersion.state = 'PUBLISHED';
              this.startSyncBizOrg();
            }
          })
          .catch(error => {
            this.$message.error('服务异常');
          });
      }
    },
    startSyncBizOrg() {
      this.$axios
        .get(`/proxy/api/org/biz/syncBizOrgByOrgUuid`, { params: { orgUuid: this.orgUuid } })
        .then(({ data }) => {})
        .catch(error => {});
    },
    getSetting() {
      return new Promise((resolve, reject) => {
        Promise.all([
          $axios.get('/proxy/api/org/elementModel/queryOrgSetting', { params: { system: this._$SYSTEM_ID } }),
          $axios.get('/proxy/api/org/elementModel/queryOrgSetting', { params: { system: 'PRD_PT' } })
        ]).then(responses => {
          for (let j = 0; j < responses.length; j++) {
            if (responses[j] && responses[j].data.code == 0 && responses[j].data.data) {
              let orgSetting = responses[j].data.data;
              for (let i = 0, len = orgSetting.length; i < len; i++) {
                if (j == 0 && orgSetting[i].category == 'USER_UNIQUE_RULE') {
                  continue;
                }
                if (j == 0 || (j == 1 && orgSetting[i].category == 'USER_UNIQUE_RULE')) {
                  this.$set(this.orgSetting, orgSetting[i].attrKey, orgSetting[i]);
                  this.orgSetting[orgSetting[i].attrKey].attrValueJson = orgSetting[i].attrVal ? JSON.parse(orgSetting[i].attrVal) : {};
                }
              }
            }
          }
          this.orgSettingFetched = true;

          console.log('组织参数设置: ', this.orgSetting);
          resolve();
        });
      });
    },

    onVersionSiderSelected(e) {
      this.selectedOrgElementId = e.orgElementId;
      this.selectedOrgRoleUuid = e.orgRoleUuid;
      this.title = (e.orgRoleUuid ? '角色成员 / ' : '') + e.title;
      if (this.selectedOrgElementId !== undefined || this.selectedOrgRoleUuid !== undefined) {
        this.countOrgElementUser(this.currentOrgVersion.uuid, e.orgElementId, e.orgRoleUuid).then(num => {
          if (this.selectedOrgElementId == e.orgElementId && this.selectedOrgElementId != undefined) {
            this.userCnt = num;
          }
          if (this.selectedOrgRoleUuid == e.orgRoleUuid && this.selectedOrgRoleUuid != undefined) {
            this.userCnt = num;
          }
        });
      } else {
        this.userCnt = e.userCnt || 0;
      }

      if (e.refresh === false) {
        return;
      }
      this.$nextTick(() => {
        this.$refs.orgVersionUserTable.refresh();
      });
    },
    countOrgElementUser(orgVersionUuid, orgElementId, orgRoleUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/org/organization/version/${orgElementId ? 'countUserCountUnderOrgElement' : 'countUserCountUnderOrgRole'}`, {
            params: { orgVersionUuid, orgElementId, orgRoleUuid }
          })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              resolve(data.data);
            }
          });
      });
    },
    watchVersionDetail(item) {
      this.viewHistoryVisible = false;
      this.currentOrgVersion.uuid = item.uuid;
      this.currentOrgVersion.state = item.state;
      this.currentOrgVersion.ver = item.ver;
      this.timePublish.time = undefined;
      this.currentOrgVersion.publishTime = item.publishTime;
      if (item.publishTime && item.state == 'DESIGNING') {
        this.currentOrgVersion.publishTime = item.publishTime;
        this.timePublish.time = moment(item.publishTime).format('yyyy-MM-DD');
      }
      this.orgVersionUserTableKey = 'orgVersionUserTable' + new Date().getTime();
      this.orgVersionManageSiderKey = 'orgVersionManageSider' + new Date().getTime();
      this.selectedOrgElementId = undefined;
      this.selectedOrgRoleUuid = undefined;
    },
    closeViewHistoryVisible() {
      this.viewHistoryVisible = false;
    },
    closeOrgVersionConstuct() {
      this.orgVersionConstuctVisible = false;
    },
    confirmCreateNewVersion(e) {
      this.$loading('创建新版本中');
      $axios
        .get('/proxy/api/org/organization/version/new', {
          params: {
            uuid: this.currentOrgVersion.uuid,
            copyUser: this.createNewVersion.copyUser,
            copyVersion: this.createNewVersion.type == '1'
          }
        })
        .then(({ data }) => {
          this.$loading(false);
          if (data.code == 0) {
            this.$message.success('创建新版本成功');
            // 切换到新版本
            this.watchVersionDetail(data.data);
            e(true);
          } else if (!data.success && data.data) {
            this.$message.error('创建新版本失败, 已存在设计版');
          }
        })
        .catch(error => {
          this.$loading(false);
          this.$message.error('创建新版本失败');
        });
    },
    handleVersionMenuItemClick(e) {
      if (e.key == 'backToPublished') {
        this.fetchPublishedOrgVersion().then(() => {
          this.orgVersionUserTableKey = 'orgVersionUserTable' + new Date().getTime();
          this.orgVersionManageSiderKey = 'orgVersionManageSider' + new Date().getTime();
          this.selectedOrgElementId = undefined;
          this.selectedOrgRoleUuid = undefined;
        });
      } else if (e.key == 'createNewVersion') {
      } else if (e.key == 'viewHistory') {
      }
    },
    confirmSaveOrgBasicInfo(e) {
      this.$refs.orgBasicInfo.save().then(() => {
        e(true);
      });
    },
    deleteVersion() {
      let _this = this;
      this.$confirm({
        title: '确认要删除设计版吗?',
        content: undefined,
        onOk() {
          _this.$loading('删除中');
          $axios
            .get('/proxy/api/org/organization/version/delete', { params: { uuid: _this.currentOrgVersion.uuid } })
            .then(({ data }) => {
              _this.$loading(false);
              if (data.code == 0) {
                _this.$message.success('删除成功');
                _this.handleVersionMenuItemClick({ key: 'backToPublished' });
              }
            })
            .catch(error => {
              _this.$loading(false);
              _this.$message.error('删除失败');
            });
        },
        onCancel() {}
      });
    },
    fetchPublishedOrgVersion() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/org/organization/version/published`, { params: { orgUuid: this.orgUuid, orgId: this.orgId } })
          .then(({ data }) => {
            if (data.data) {
              for (let key in data.data) {
                this.$set(this.currentOrgVersion, key, data.data[key]);
              }
              // this.currentOrgVersion = data.data;
            }
            resolve();
          })
          .catch(() => {
            this.$message.error('获取组织发布版本异常');
          });
      });
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
