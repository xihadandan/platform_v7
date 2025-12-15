<template>
  <div class="biz-org-user-container">
    <div
      v-if="bizOrgElementSelected.uuid != undefined"
      style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px"
    >
      <div>
        <Modal :ok="onSaveSetBizOrgRoleMember" title="批量设置成员" mask>
          <template slot="content">
            <div>
              <a-form-model
                :model="bizOrgElementMember"
                ref="bizOrgElementMemberForm"
                :label-col="labelCol"
                :wrapper-col="wrapperCol"
                :colon="false"
                class="pt-form"
              >
                <a-form-item>
                  <template slot="label">
                    {{
                      bizOrgElementSelected.isDimension ? bizOrgDimension.name : orgElementModelMap[bizOrgElementSelected.elementType].name
                    }}
                  </template>
                  {{ bizOrgElementSelected.name }}
                </a-form-item>

                <a-divider orientation="left">角色成员</a-divider>
                <template v-if="bizOrgRolesCanUsed.length">
                  <template v-for="(role, r) in bizOrgRolesCanUsed">
                    <a-form-item :label="role.name" :key="'role_' + r">
                      <OrgSelect
                        title="选择添加的成员"
                        :orgUuid="bizOrg.orgUuid"
                        v-model="roleSelectedMember[role.id]"
                        :checkableTypes="['user']"
                        orgType="MyOrg"
                        :multiSelect="true"
                        :forcePreviewUserUnderNode="true"
                        :showBizOrgUnderOrg="false"
                      />
                    </a-form-item>
                  </template>
                </template>
                <a-empty v-else description="无可设置的业务角色"></a-empty>
              </a-form-model>
            </div>
          </template>
          <a-button type="primary" icon="plus">批量设置成员</a-button>
        </Modal>
      </div>
    </div>
    <a-table
      :data-source="rows"
      :columns="columns"
      :pagination="false"
      :row-selection="rowSelection"
      @change="onTableChange"
      rowKey="id"
      :loading="loading"
      :bordered="false"
      :scroll="{ y: 'calc(100vh - 280px)' }"
      class="pt-table pt-empty"
    >
      <template slot="operationSlot" slot-scope="text, record">
        <Modal :ok="e => onSaveSetMember(e, record)" title="成员设置" mask>
          <template slot="content">
            <a-form-model
              :model="bizOrgElementMember"
              ref="bizOrgElementMemberForm"
              :label-col="labelCol"
              :wrapper-col="wrapperCol"
              class="pt-form"
              :colon="false"
            >
              <a-form-item label="角色">{{ record.name }}</a-form-item>
              <a-form-item label="角色成员">
                <OrgSelect
                  title="成员设置"
                  :orgUuid="bizOrg.orgUuid"
                  v-model="tempMember"
                  :checkableTypes="['user']"
                  orgType="MyOrg"
                  :multiSelect="true"
                  :forcePreviewUserUnderNode="true"
                  :showBizOrgUnderOrg="false"
                />
              </a-form-item>
            </a-form-model>
          </template>
          <a-button size="small" type="link" @click="onClickOpenRowMemSet(record)">成员设置</a-button>
        </Modal>
      </template>
    </a-table>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import DataSourceBase from '@pageAssembly/app/web/assets/js/commons/dataSource.base.js';

export default {
  name: 'BizOrgRoleMemberTable',
  inject: ['bizOrgElementSelected', 'bizOrgRoles', 'bizOrg', 'orgElementModels', 'bizOrgDimension'],
  props: {},
  components: { Modal, Drawer, OrgSelect },
  computed: {
    bizOrgRolesCanUsed() {
      let list = [];
      for (let item of this.bizOrgRoles) {
        if (
          (this.bizOrgElementSelected.isDimension && item.applyTo.indexOf('BIZ_DIMENSION_ELEMENT') != -1) ||
          (!this.bizOrgElementSelected.isDimension && item.applyTo.indexOf('ORG_ELEMENT') != -1)
        ) {
          list.push(item);
        }
      }
      return list;
    },
    orgElementModelMap() {
      let map = {};
      for (let m of this.orgElementModels) {
        map[m.id] = m;
      }
      return map;
    },
    rowSelection() {
      return {
        type: 'checkbox',
        selectedRowKeys: this.selectedRowKeys,
        selectedRows: this.selectedRows,
        onChange: this.selectRowChange,
        columnWidth: 60
      };
    }
  },
  data() {
    return {
      rows: [],
      loading: false,
      keyword: undefined,
      columns: [
        {
          title: '角色',
          dataIndex: 'name',
          width: 150,
          key: 'name'
        },
        {
          title: '角色成员',
          dataIndex: 'memberNames',
          key: 'memberNames'
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
        pageSize: 8, //每页条数
        total: 0 //总条数
      },
      bizOrgElementMember: {
        members: [],
        roles: []
      },
      labelCol: { span: 6 },
      wrapperCol: { span: 18 },
      roleSelectedMember: {},
      tempMember: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchBizOrgRoleMembers();
  },
  mounted() {},
  methods: {
    onClickOpenRowMemSet(item) {
      this.tempMember.splice(0, this.tempMember.length);
      if (item.members) {
        this.tempMember.push(...item.members.split(';'));
      }
    },
    onSaveSetMember(e, item) {
      let list = [];
      for (let m of this.tempMember) {
        list.push({
          bizOrgElementId: this.bizOrgElementSelected.id,
          bizOrgRoleId: item.id,
          memberId: m,
          bizOrgUuid: this.bizOrg.uuid
        });
      }
      this.saveBizOrgElementMember(this.bizOrgElementSelected.id, item.id, list).then(() => {
        this.fetchBizOrgRoleMembers();
        e(true);
      });
    },
    onSaveSetBizOrgRoleMember(e) {
      let list = [];
      for (let key in this.roleSelectedMember) {
        let members = this.roleSelectedMember[key];
        for (let m of members) {
          list.push({
            bizOrgElementId: this.bizOrgElementSelected.id,
            bizOrgRoleId: key,
            memberId: m,
            bizOrgUuid: this.bizOrg.uuid
          });
        }
      }
      if (list.length > 0) {
        this.saveBizOrgElementMember(this.bizOrgElementSelected.id, undefined, list).then(() => {
          this.fetchBizOrgRoleMembers();
          e(true);
        });
      } else {
        e(true);
      }
    },
    saveBizOrgElementMember(bizOrgElementId, bizOrgRoleId, list) {
      return new Promise((resolve, reject) => {
        this.$axios
          .post(`/proxy/api/org/biz/saveBizOrgElementMember/${bizOrgElementId}${bizOrgRoleId ? `/${bizOrgRoleId}` : ''}`, list)
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
    fetchBizOrgRoleMembers() {
      let _this = this;
      this.loading = true;
      new DataSourceBase({
        onDataChange: function (data, count, params) {
          _this.rows = data.data;
          _this.roleSelectedMember = {};
          for (let d of data.data) {
            _this.$set(_this.roleSelectedMember, d.id, d.members ? d.members.split(';') : []);
          }
          _this.pagination.total = _this.rows.length;
          _this.pagination.current = 1;
          // _this.pagination.totalPages = data.pagination.totalPages;
          _this.loading = false;
          _this.$emit('roleChanged', {
            rows: _this.rows
          });
        },
        receiver: this,
        dataStoreId: 'CD_DS_127456079339586757',
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

    refresh() {
      this.fetchBizOrgRoleMembers();
    },
    onSearch() {
      this.pagination.current = 1;
      this.fetchBizOrgMembers();
    },
    onTableChange(pagination, filters, sorter) {
      if (typeof this.pagination !== 'boolean') {
        this.pagination.current = pagination.current;
        this.pagination.pageSize = pagination.pageSize;
      }
      // this.fetchBizOrgMembers();
    },
    selectRowChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys;
      this.selectedRows = selectedRows;
    }
  }
};
</script>
