<template>
  <span>
    <Drawer
      wrapClassName="online-user-drawer"
      :width="378"
      :bodyStyle="{
        padding: '12px 20px'
      }"
      v-model="visible"
      :zIndex="100000"
    >
      <template slot="title">
        <label style="font-size: var(--w-font-size-lg)">
          <i class="pticon iconfont icon-luojizujian-huoquyonghuliebiao"></i>
          {{ title }}
        </label>
      </template>
      <slot></slot>
      <template slot="content">
        <div class="online-user-drawer-container">
          <div class="spin-center" v-show="loading">
            <a-spin />
          </div>
          <div class="search-toolbar">
            <a-input placeholder="搜索" v-model.trim="searchKeyword" allow-clear @change="onChangeSearchWord">
              <i class="pticon iconfont icon-ptkj-sousuochaxun" slot="suffix" />
            </a-input>
            <div class="button-group">
              <a-button size="small" :icon="loading ? 'loading' : undefined" @click="initOnlineUserList">
                <i v-if="!loading" class="pticon iconfont icon-luojizujian-yemianshuaxin" />
              </a-button>
              <a-dropdown
                :getPopupContainer="getPopupContainer"
                v-model="filterDropdownVisible"
                :trigger="['click']"
                @visibleChange="onFilterDropdownVisibleChange"
              >
                <a-button size="small" @click.stop="() => {}">
                  <i class="pticon iconfont icon-ptkj-shaixuan" />
                </a-button>
                <div slot="overlay" class="filter-search-overlay ant-dropdown-menu">
                  <a-form-model :model="filterForm" :colon="false">
                    <a-form-model-item label="状态">
                      <a-checkbox-group
                        :options="[
                          { label: '在线', value: 'online' },
                          { label: '离开', value: 'offline' }
                        ]"
                        v-model="filterForm.state"
                      ></a-checkbox-group>
                    </a-form-model-item>
                    <a-form-model-item label="部门">
                      <OrgSelect
                        v-model="filterForm.dept"
                        v-if="defaultOrgVersion != undefined"
                        :orgVersionId="defaultOrgVersion.id"
                        orgType="MyOrg"
                        title="选择部门"
                        :checkableTypes="['dept']"
                        :params="{ userHidden: true }"
                      />
                    </a-form-model-item>
                    <div style="text-align: right">
                      <a-button type="primary" size="small" @click="onSearchFilterForm">搜索</a-button>
                      <a-button size="small" @click="resetFilterForm">重置</a-button>
                      <a-button size="small" @click="cancelFilterForm">取消</a-button>
                    </div>
                  </a-form-model>
                </div>
              </a-dropdown>

              <a-dropdown :getPopupContainer="getPopupContainer">
                <a-button size="small">
                  <i class="pticon iconfont icon-ptkj-qiehuanpaixu" />
                </a-button>
                <a-menu slot="overlay" @click="handleSortMenuClick">
                  <a-menu-item key="loginTime">
                    <label style="display: inline-block; width: 60px">最近登录</label>
                    <a-icon v-show="sort[0] == 'loginTime'" :type="sort[1] == 'asc' ? 'arrow-up' : 'arrow-down'" />
                  </a-menu-item>
                  <a-menu-item key="userNamePy">
                    <label style="display: inline-block; width: 60px">按姓名</label>
                    <a-icon v-show="sort[0] == 'userNamePy'" :type="sort[1] == 'asc' ? 'arrow-up' : 'arrow-down'" />
                  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </div>
          </div>
          <Scroll style="height: calc(100vh - 130px)">
            <div class="online-user-list">
              <a-list item-layout="horizontal" :data-source="filteredUsers">
                <a-list-item slot="renderItem" slot-scope="item, index">
                  <a-list-item-meta>
                    <a slot="title" class="user-name">{{ item.userName }}</a>
                    <template slot="description">
                      <label :title="item.deptAndJobPath" class="dept-and-job-path">{{ item.deptAndJobPath }}</label>
                      <label class="login-time">
                        {{ item.loginTimeFormat }}
                      </label>
                    </template>

                    <div slot="avatar" style="position: relative">
                      <a-avatar
                        :key="'avatar_' + item.userId"
                        icon="user"
                        class="header-user-avatar"
                        :style="{
                          background: 'var(--w-primary-color)',
                          border: item.userId ? '2px solid' : 'none'
                        }"
                        :src="'/proxy/org/user/view/photo/' + item.userId"
                      ></a-avatar>
                      <div :class="['circle-user-state', item.online ? 'online' : 'offline']"></div>
                    </div>
                  </a-list-item-meta>

                  <div slot="extra" class="extra-infos">
                    <div>
                      <span class="online-operation-button" title="发信息" @click="sendOnlineMsg(item)">
                        <i class="pticon iconfont icon-filled_faqixiaoxi"></i>
                      </span>
                    </div>
                  </div>
                </a-list-item>
              </a-list>
            </div>
          </Scroll>
          <MessageDetailModal
            :zIndex="100001"
            modalTitle="发消息"
            ref="messageDetailModal"
            v-model="messageDetailModalVisible"
            :msgData="msgData"
            :mask="false"
            type="sendMsg"
            :container="getContainer"
          />
        </div>
      </template>
    </Drawer>
  </span>
</template>
<style lang="less">
.online-user-drawer {
  .ant-drawer-header {
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  }
  .circle-user-state {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    position: absolute;
    right: 0px;
    bottom: 1px;
    border: 1px solid #ffffff;
    &.online {
      background: #3dc255;
    }
    &.offline {
      background: #b3b3b3;
    }
  }
  .search-toolbar {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    i.pticon {
      color: #333;
    }
    > .button-group {
      width: 240px;
      display: flex;
      > button {
        margin-left: 12px;
        width: 32px;
        height: 32px;
      }
    }
  }

  .filter-search-overlay {
    width: 250px;
    height: auto;
    padding: 12px;
    .ant-form-item {
      margin-bottom: 8px;
    }
  }

  .online-user-list {
    .ant-list-item {
      height: 56px;
      padding: 8px;
      &:hover {
        background-color: #fafafa;
      }
      .extra-infos {
        display: flex;
        > label {
          margin-right: 12px;
        }
        .online-operation-button {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 20px;
          height: 20px;
          border-radius: 4px 4px 4px 4px;
          color: var(--w-primary-color);
          > i {
            font-size: 12px;
          }
          &:hover {
            background-color: var(--w-primary-color-2);
          }
        }
      }
      .user-name {
        font-weight: 400;
        font-size: 14px;
        color: #333;
      }

      .ant-list-item-meta-description {
        display: flex;
        align-items: baseline;
        justify-content: space-between;

        .dept-and-job-path {
          display: inline-block;
          width: 150px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          font-weight: 400;
          font-size: 12px;
          color: #999;
        }
        .login-time {
          color: #999;
          font-weight: 400;
          font-size: 12px;
          padding-right: 15px;
        }
      }
    }

    .ant-list-item-meta {
      align-items: center;
    }
  }
}
</style>
<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import MessageDetailModal from '@admin/app/web/template/message-manage/message-detail-modal.vue';
import { debounce, orderBy } from 'lodash';
import OrgSelect from '@admin/app/web/lib/org-select.vue';

export default {
  name: 'HeaderOnlineUserList',
  props: {
    menu: Object
  },
  components: { Drawer, MessageDetailModal, OrgSelect },
  computed: {
    title() {
      return `在线人数 (${this.users.length}/${this.totalUser})`;
    }
  },
  data() {
    return {
      totalUser: 0,
      loading: false,
      defaultOrgVersion: undefined,
      users: [],
      filteredUsers: [],
      filterForm: {
        state: ['online', 'offline'],
        dept: []
      },
      msgData: {
        recipient: undefined,
        recipientName: undefined
      },
      visible: false,
      messageDetailModalVisible: false,
      filterDropdownVisible: false,
      searchKeyword: undefined,
      sort: ['loginTime', 'desc']
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.initOnlineUserList();
  },
  methods: {
    onChangeSearchWord: debounce(function () {
      this.searchUser();
    }, 500),
    onSearchFilterForm() {
      this.filterDropdownVisible = false;
      this.searchUser();
    },
    searchUser() {
      this.filteredUsers.splice(0, this.filteredUsers.length);
      for (let i = 0, len = this.users.length; i < len; i++) {
        let matchName =
          this.searchKeyword == undefined ||
          this.searchKeyword == '' ||
          this.matchUserNameAndDeptJobName(this.users[i], this.searchKeyword);
        let matchDept = this.filterForm.dept.length == 0 || this.matchUserInDept(this.users[i], this.filterForm.dept);
        let matchState =
          this.filterForm.state.length == 2 ||
          (this.users[i].online && this.filterForm.state.includes('online')) ||
          (!this.users[i].online && this.filterForm.state.includes('offline'));
        if (matchName && matchDept && matchState) {
          this.filteredUsers.push(this.users[i]);
        }
      }
      if (this.filteredUsers.length > 0) {
        this.sortUsers();
      }
    },
    matchUserInDept(user, dept) {
      let userSystemOrgDetails = user.userSystemOrgDetails;
      if (userSystemOrgDetails) {
        // 匹配组织节点信息模糊查询
        let details = userSystemOrgDetails.details;
        if (details.length) {
          for (let d of details) {
            let { mainJob, otherJobs } = d;
            if (mainJob && dept.includes(mainJob.deptId)) {
              return true;
            }
            if (otherJobs && otherJobs.length > 0) {
              for (let o of otherJobs) {
                if (dept.includes(o.deptId)) {
                  return true;
                }
              }
            }
          }
        }
      }
      return false;
    },
    matchUserNameAndDeptJobName(user, searchKeyword) {
      let match = user.userName.indexOf(searchKeyword) != -1;
      if (!match) {
        let userSystemOrgDetails = user.userSystemOrgDetails;
        if (userSystemOrgDetails) {
          // 匹配组织节点信息模糊查询
          let details = userSystemOrgDetails.details;
          if (details.length) {
            for (let d of details) {
              let { mainJob, otherJobs } = d;
              if (
                mainJob &&
                (mainJob.eleName.indexOf(searchKeyword) != -1 || (mainJob.deptName && mainJob.deptName.indexOf(searchKeyword) != -1))
                // mainJob.eleNamePath && mainJob.eleNamePath.replace(/\//g, '').indexOf(searchKeyword) != -1
              ) {
                return true;
              }
              if (otherJobs && otherJobs.length > 0) {
                for (let o of otherJobs) {
                  if (
                    o.eleName.indexOf(searchKeyword) != -1 ||
                    (o.deptName && o.deptName.indexOf(searchKeyword) != -1)
                    // o.eleNamePath && o.eleNamePath.replace(/\//g, '').indexOf(searchKeyword) != -1
                  ) {
                    return true;
                  }
                }
              }
            }
          }
        }
      }
      return match;
    },
    resetFilterForm() {
      this.filterForm.dept.splice(0, this.filterForm.dept.length);
      this.filterForm.state.splice(0, this.filterForm.state.length, 'online', 'offline');
    },
    cancelFilterForm() {
      this.resetFilterForm();
      this.filterDropdownVisible = false;
    },
    onFilterDropdownVisibleChange(visible) {
      this.filterDropdownVisible = true;
    },
    handleSortMenuClick(e) {
      this.sort[1] = this.sort[0] == e.key ? (this.sort[1] == 'desc' ? 'asc' : 'desc') : 'desc';
      this.sort[0] = e.key;
      this.sortUsers();
    },
    getPopupContainer(e) {
      return e.parentNode;
    },
    sortUsers() {
      let sortFunc =
          this.sort[0] == 'loginTime'
            ? [
                function (o) {
                  return parseInt(o.loginTime);
                }
              ]
            : [
                function (o) {
                  return o.userNamePy;
                }
              ],
        orders = [this.sort[1]];
      this.filteredUsers = orderBy(this.filteredUsers, sortFunc, orders);
    },
    initOnlineUserList() {
      this.loading = true;
      this.sort = ['loginTime', 'desc'];
      this.countUserUnderSystem();
      this.getDefaultOrgVersion();
      this.getOnlineUser().then(list => {
        if (this.menu.badge && this.menu.badge.enable) {
          this.$set(this.menu, 'badgeNum', list.length);
        } else {
          this.menu.title = `${list.length}人在线`;
        }
        if (list.length > 0) {
          console.log('当前在线用户: ', list);
          let userIds = [],
            userMap = {};

          for (let u of list) {
            userIds.push(u.userId);
            userMap[u.userId] = u;
          }

          this.getUserSystemOrgDetails(userIds).then(map => {
            this.users.splice(0, this.users.length);
            this.filteredUsers.splice(0, this.filteredUsers.length);
            if (map) {
              for (let key in map) {
                userMap[key].userSystemOrgDetails = map[key];
                userMap[key].deptAndJobPath = this.getUserDeptAndJob(userMap[key]);
              }
            }
            this.users.push(...list);
            this.filteredUsers.push(...list);
            this.sortUsers();
            this.loading = false;
          });
        } else {
          this.users.splice(0, this.users.length);
          this.filteredUsers.splice(0, this.filteredUsers.length);
          this.loading = false;
        }
      });
    },
    getContainer() {
      return document.querySelector('.online-user-drawer-container');
    },
    sendOnlineMsg(item) {
      this.messageDetailModalVisible = true;
      this.msgData.recipient = item.userId;
      this.msgData.recipientName = item.userName;
    },

    getUserDeptAndJob(user) {
      let userSystemOrgDetails = user.userSystemOrgDetails;
      if (userSystemOrgDetails) {
        let details = userSystemOrgDetails.details;
        if (details.length) {
          for (let d of details) {
            let { mainJob, otherJobs, mainDept, otherDepts } = d;
            if (mainJob) {
              return mainJob.eleNamePath.split('/').slice(-2).join(' - ');
            } else if (otherJobs && otherJobs.length > 0) {
              return otherJobs[0].eleNamePath.split('/').slice(-2).join(' - ');
            } else if (mainDept) {
              return mainDept.eleNamePath.split('/').slice(-2).join(' - ');
            } else if (otherDepts && otherDepts.length > 0) {
              return otherDepts[0].eleNamePath.split('/').slice(-2).join(' - ');
            }
          }
        }
      }
      return '';
    },
    getOnlineUser() {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/sys/${this._$SYSTEM_ID}/_/getOnlineUser`, { params: {} })
          .then(({ data }) => {
            // console.log('获取在线用户', data);
            resolve(data);
          })
          .catch(error => {});
      });
    },
    countUserUnderSystem() {
      $axios
        .get(`/proxy/api/org/organization/countAllUserCountUnderSystem`, { params: {} })
        .then(({ data }) => {
          this.totalUser = data.data;
        })
        .catch(error => {});
    },
    getDefaultOrgVersion() {
      $axios
        .get(`/proxy/api/org/organization/version/published`, { params: { system: this._$SYSTEM_ID } })
        .then(({ data }) => {
          this.defaultOrgVersion = data.data;
        })
        .catch(error => {});
    },
    getUserSystemOrgDetails(userIds) {
      return new Promise((resolve, reject) => {
        this.$axios
          .post(`/proxy/api/org/user/orgDetails?system=${this._$SYSTEM_ID}`, userIds)
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {});
      });
    }
  }
};
</script>
