<template>
  <div class="biz-org-user-container">
    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px">
      <div>
        <Modal :ok="onSaveBizOrgElementMember" title="添加成员" mask :bodyStyle="{ minHeight: '240px' }">
          <template slot="content">
            <div>
              <a-form-model
                :model="bizOrgElementMember"
                ref="bizOrgElementMemberForm"
                :label-col="labelCol"
                :wrapper-col="wrapperCol"
                :colon="false"
                class="pt-form"
                :rules="{
                  members: { required: true, message: '请选择要添加的成员', trigger: ['blur', 'change'] },
                  roles: { required: true, message: '请选择要添加的角色', trigger: ['blur', 'change'] }
                }"
              >
                <a-form-model-item label="选择添加的成员" prop="members">
                  <OrgSelect
                    title="选择添加的成员"
                    :orgUuid="bizOrg.orgUuid"
                    v-model="bizOrgElementMember.members"
                    :checkableTypes="['user']"
                    orgType="MyOrg"
                    :multiSelect="true"
                    :forcePreviewUserUnderNode="true"
                    :showBizOrgUnderOrg="false"
                  />
                </a-form-model-item>
                <a-form-model-item label="成员角色" prop="roles">
                  <a-select v-model="bizOrgElementMember.roles" mode="multiple" allow-clear>
                    <template v-for="(role, i) in bizOrgRoles">
                      <a-select-option
                        v-if="
                          (bizOrgElementSelected.isDimension && role.applyTo.indexOf('BIZ_DIMENSION_ELEMENT') != -1) ||
                          (!bizOrgElementSelected.isDimension && role.applyTo.indexOf('ORG_ELEMENT') != -1)
                        "
                        :key="'role' + i"
                        :value="role.id"
                      >
                        {{ role.name }}
                      </a-select-option>
                    </template>
                  </a-select>
                </a-form-model-item>
              </a-form-model>
            </div>
          </template>
          <a-button type="primary" v-show="bizOrgElementSelected.uuid != undefined">
            <Icon type="iconfont icon-ptkj-jiahao"></Icon>
            添加成员
          </a-button>
        </Modal>
        <Modal :ok="onSaveBatchBizOrgElementMember" title="批量添加成员" :width="800" mask>
          <template slot="content">
            <div style="margin-bottom: 12px">
              <a-button type="primary" @click="onClickAddBatchRow">
                <Icon type="iconfont icon-ptkj-jiahao"></Icon>
                新增
              </a-button>
            </div>
            <a-table
              :scroll="{ y: '400px' }"
              :data-source="batchRows"
              :columns="batchAddColumns"
              :pagination="false"
              rowKey="uuid"
              :bordered="false"
              class="pt-table pt-empty"
            >
              <span slot="customMemberTitle">
                <label style="color: red">*</label>
                选择添加的成员
              </span>
              <span slot="customRoleTitle">
                <label style="color: red">*</label>
                成员角色
              </span>
              <template slot="memberSlot" slot-scope="text, record">
                <OrgSelect
                  title="选择添加的成员"
                  :orgUuid="bizOrg.orgUuid"
                  v-model="record.members"
                  :checkableTypes="['user']"
                  orgType="MyOrg"
                  :multiSelect="true"
                  :forcePreviewUserUnderNode="true"
                  :enableCache="true"
                  :showBizOrgUnderOrg="false"
                />
              </template>
              <template slot="roleSlot" slot-scope="text, record">
                <a-select v-model="record.roles" mode="multiple" allow-clear style="width: 100%">
                  <template v-for="(role, i) in bizOrgRoles">
                    <a-select-option
                      v-if="
                        (bizOrgElementSelected.isDimension && role.applyTo.indexOf('BIZ_DIMENSION_ELEMENT') != -1) ||
                        (!bizOrgElementSelected.isDimension && role.applyTo.indexOf('ORG_ELEMENT') != -1)
                      "
                      :key="'role' + i"
                      :value="role.id"
                    >
                      {{ role.name }}
                    </a-select-option>
                  </template>
                </a-select>
              </template>
              <template slot="operationSlot" slot-scope="text, record, index">
                <a-button size="small" type="link" @click="copyBatchRow(record, index)">复制</a-button>
                <a-button size="small" type="link" @click="batchRows.splice(index, 1)">删除</a-button>
              </template>
            </a-table>
          </template>
          <a-button @click="batchAddUser" type="line" v-show="bizOrgElementSelected.uuid != undefined">
            <Icon type="iconfont icon-ptkj-jiahao"></Icon>
            批量添加成员
          </a-button>
        </Modal>
        <a-button @click="batchRemoveUser" v-show="selectedRowKeys.length > 0">批量移出成员</a-button>
      </div>
      <a-input-search style="width: 200px" v-model="keyword" allow-clear />
    </div>

    <a-table
      :data-source="vRows"
      :columns="columns"
      :pagination="pagination"
      :row-selection="rowSelection"
      @change="onTableChange"
      :rowKey="userTableRowKey"
      :loading="loading"
      :bordered="false"
      class="biz-org-user-table pt-table pt-empty"
      :scroll="{ y: 'calc(100vh - 346px)' }"
    >
      <template slot="userNameSlot" slot-scope="text, record">
        <a-avatar
          :src="record.avatar ? '/proxy/org/user/view/photo/' + record.avatar : null"
          style="background: linear-gradient(180deg, var(--w-primary-color-4) 0%, var(--w-primary-color) 100%)"
        >
          <span v-if="!record.avatar">
            {{ text && text.slice(0, 1) }}
          </span>
        </a-avatar>
        {{ text }}
      </template>
      <template slot="operationSlot" slot-scope="text, record">
        <a-popconfirm
          placement="left"
          :arrowPointAtCenter="true"
          title="确认要移出成员吗?"
          ok-text="确定"
          cancel-text="取消"
          @confirm="onClickRemoveMember(record)"
        >
          <a-button size="small" type="link">移出成员</a-button>
        </a-popconfirm>
      </template>
    </a-table>
    <Pagination v-model="pagination" @change="onChangePagination"></Pagination>
  </div>
</template>
<style lang="less">
.biz-org-user-table {
  height: e('calc(100vh - 270px)');
  overflow: auto;
  .ant-table-pagination {
    display: none;
  }
}
</style>
<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import DataSourceBase from '@pageAssembly/app/web/assets/js/commons/dataSource.base.js';
import { generateId, deepClone } from '@framework/vue/utils/util';
import Pagination from '@admin/app/web/template/common/pagination.vue';

export default {
  name: 'BizOrgUserTable',
  inject: ['bizOrgElementSelected', 'bizOrgRoles', 'bizOrg'],
  props: {},
  components: { Modal, Drawer, OrgSelect, Pagination },
  computed: {
    rowSelection() {
      return {
        type: 'checkbox',
        selectedRowKeys: this.selectedRowKeys,
        selectedRows: this.selectedRows,
        onChange: this.selectRowChange,
        columnWidth: 60
      };
    },
    vRows() {
      let result = [];
      if (this.keyword == undefined || this.keyword == '') {
        return this.rows;
      }
      for (let r of this.rows) {
        if (
          (r.userName && r.userName.indexOf(this.keyword) >= 0) ||
          (r.underBizOrgElementAndRole && r.underBizOrgElementAndRole.indexOf(this.keyword) >= 0) ||
          (r.deptAndJob && r.deptAndJob.indexOf(this.keyword) >= 0)
        ) {
          result.push(r);
        }
      }
      return result;
    },
    rowTotal() {
      return this.vRows.length;
    }
  },
  data() {
    return {
      rows: [],
      loading: false,
      keyword: undefined,
      columns: [
        {
          title: '姓名',
          dataIndex: 'userName',
          width: 150,
          key: 'userName',
          scopedSlots: { customRender: 'userNameSlot' }
        },
        {
          title: '部门 / 角色',
          dataIndex: 'underBizOrgElementAndRole',
          key: 'underBizOrgElementAndRole'
        },
        {
          title: '关联组织部门 / 职位',
          dataIndex: 'deptAndJob',
          key: 'deptAndJob'
        },
        {
          title: '操作',
          dataIndex: 'operation',
          key: 'operation',
          width: 150,
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      batchRows: [],
      batchAddColumns: [
        {
          dataIndex: 'members',
          key: 'members',
          slots: { title: 'customMemberTitle' },
          scopedSlots: { customRender: 'memberSlot' }
        },
        {
          dataIndex: 'roles',
          key: 'roles',
          slots: { title: 'customRoleTitle' },
          scopedSlots: { customRender: 'roleSlot' }
        },
        {
          title: '操作',
          dataIndex: 'operation',
          key: 'operation',
          width: 150,
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      selectedRowKeys: [],
      selectedRows: [],
      pagination: {
        hideOnSinglePage: true,
        current: 1, //当前页
        pageSize: 10, //每页条数
        total: 0 //总条数
      },
      bizOrgElementMember: {
        members: [],
        roles: []
      },
      labelCol: { span: 6 },
      wrapperCol: { span: 16 },
      searchRows: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchBizOrgMembers();
  },
  mounted() {},
  methods: {
    userTableRowKey(row) {
      return row.userId + '_' + row.bizOrgElementId;
    },
    onClickAddBatchRow() {
      this.batchRows.push({
        uuid: generateId(),
        members: [],
        roles: []
      });
    },
    copyBatchRow(item, i) {
      this.batchRows.splice(i + 1, 0, {
        uuid: generateId(),
        roles: [].concat(item.roles),
        members: [].concat(item.members)
      });
    },
    fetchBizOrgMembers() {
      let _this = this;
      this.loading = true;
      this.rows.splice(0, this.rows.length);
      new DataSourceBase({
        onDataChange: function (data, count, params) {
          _this.rows = data.data;
          let userIds = new Set();
          for (let d of data.data) {
            userIds.add(d.userId);
          }
          _this.$emit('userCountChange', userIds.size);
          _this.pagination.total = count;
          _this.pagination.current = data.pagination.currentPage;
          _this.pagination.totalPages = data.pagination.totalPages;
          // _this.pagination.current = 1;
          _this.loading = false;
        },
        receiver: this,
        dataStoreId: 'CD_DS_127447356424586437',
        params: {
          bizOrgUuid: this.bizOrg.uuid,
          orgUuid: this.bizOrg.orgUuid,
          bizOrgElementId: this.bizOrgElementSelected.id,
          keyword: this.keyword
        },
        pageSize: -1
      }).load({
        pagination: {
          currentPage: this.pagination.current,
          pageSize: this.pagination.pageSize
        }
      });
    },
    onSaveBatchBizOrgElementMember(e) {
      if (this.batchRows.length) {
        let memberRoleMap = {};
        for (let r of this.batchRows) {
          let { members, roles } = r;
          for (let m of members) {
            if (!memberRoleMap[m]) {
              memberRoleMap[m] = [];
            }
            memberRoleMap[m].push(...roles);
            memberRoleMap[m] = Array.from(new Set(memberRoleMap[m]));
          }
        }
        let list = [];
        for (let uid in memberRoleMap) {
          let roles = memberRoleMap[uid];
          for (let r of roles) {
            list.push({
              bizOrgUuid: this.bizOrg.uuid,
              memberId: uid,
              bizOrgElementId: this.bizOrgElementSelected.id,
              bizOrgRoleId: r
            });
          }
        }
        this.saveBizOrgElementMember(list).then(() => {
          e(true);
          this.batchRows.splice(0, this.batchRows.length);
        });
      }
    },
    saveBizOrgElementMember(list) {
      return new Promise((resolve, reject) => {
        this.$axios
          .post(`/proxy/api/org/biz/addBizOrgElementMember`, list)
          .then(({ data }) => {
            if (data.code == 0) {
              this.$message.success('保存成功');
              resolve();
            } else {
              reject();
              this.$message.error('保存失败');
            }
          })
          .catch(error => {
            reject();
            this.$message.error('保存失败');
          });
      });
    },
    onSaveBizOrgElementMember(e) {
      this.$refs.bizOrgElementMemberForm.validate(valid => {
        if (valid) {
          let list = [];
          for (let u of this.bizOrgElementMember.members) {
            let temp = {
              bizOrgUuid: this.bizOrg.uuid,
              memberId: u,
              bizOrgElementId: this.bizOrgElementSelected.id
            };
            for (let r of this.bizOrgElementMember.roles) {
              list.push({
                ...temp,
                bizOrgRoleId: r
              });
            }
          }
          this.saveBizOrgElementMember(list).then(() => {
            this.fetchBizOrgMembers();
            this.bizOrgElementMember.members.splice(0, this.bizOrgElementMember.members.length);
            this.bizOrgElementMember.roles.splice(0, this.bizOrgElementMember.roles.length);

            e(true);
          });
        }
      });
    },

    onClickRemoveMember(item) {
      this.requestRemoveMember([
        {
          bizOrgUuid: this.bizOrg.uuid,
          memberId: item.userId,
          bizOrgElementId: item.bizOrgElementId || this.bizOrgElementSelected.id
        }
      ]).then(() => {
        for (let i = 0, len = this.rows.length; i < len; i++) {
          if (this.rows[i].userId == item.userId) {
            this.rows.splice(i, 1);
            break;
          }
        }
      });
    },
    requestRemoveMember(list) {
      return new Promise((resolve, reject) => {
        this.$axios
          .post(`/proxy/api/org/biz/removeBizOrgElementMember`, list)
          .then(({ data }) => {
            if (data.code == 0) {
              this.$message.success('移出成功');
              resolve();
            } else {
              this.$message.error('移出失败');
            }
          })
          .catch(error => {
            this.$message.error('移出失败');
          });
      });
    },
    batchRemoveUser() {
      let _this = this;
      this.$confirm({
        title: '确认框',
        content: '确认要批量移出成员吗?',
        okText: '确定',
        cancelText: '取消',
        onOk() {
          let list = [];
          for (let r of _this.selectedRows) {
            list.push({
              bizOrgUuid: _this.bizOrg.uuid,
              memberId: r.userId,
              bizOrgElementId: r.bizOrgElementId || (_this.bizOrgElementSelected != undefined ? _this.bizOrgElementSelected.id : null)
            });
          }
          _this.requestRemoveMember(list).then(() => {
            _this.selectedRowKeys.splice(0, _this.selectedRowKeys.length);
            _this.selectedRows.splice(0, _this.selectedRows.length);
            _this.refresh();
          });
        }
      });
    },
    refresh() {
      this.fetchBizOrgMembers();
    },

    onTableChange(pagination, filters, sorter) {
      if (typeof this.pagination !== 'boolean' && pagination) {
        this.pagination.current = pagination.current;
        this.pagination.pageSize = pagination.pageSize;
      }
      this.fetchBizOrgMembers();
    },
    selectRowChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys;
      this.selectedRows = selectedRows;
    },
    onChangePagination() {
      this.onTableChange();
    }
  },
  watch: {
    rowTotal: {
      handler() {
        this.pagination.total = this.rowTotal;
      }
    }
  }
};
</script>
