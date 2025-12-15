<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" :colon="false" class="pt-form">
    <a-form-model-item label="角色" prop="orgRoleUuid">
      <a-select v-model="form.orgRoleUuid" style="width: 100%">
        <a-select-option v-for="(role, i) in roleSelectList" :value="role.key" :key="'orgRoleSelect_' + i">
          {{ role.title }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="关联组织节点" prop="orgElementId">
      <a-tree-select
        v-model="form.orgElementId"
        style="width: 100%"
        :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
        allow-clear
        :replaceFields="{ value: 'id' }"
        showSearch
        treeNodeFilterProp="title"
        :tree-data="orgElementTreeData"
      ></a-tree-select>
      <!-- <OrgSelect ref="orgSelect" v-model="form.orgElementId" :orgVersionId="orgVersion.id" /> -->
    </a-form-model-item>
    <a-form-model-item label="角色成员" prop="userId">
      <OrgSelect
        ref="orgSelect"
        v-model="form.userId"
        :orgVersionId="orgVersion.id"
        :checkableTypes="['user']"
        :showBizOrgUnderOrg="false"
      />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import OrgSelect from '@admin/app/web/lib/org-select.vue';

export default {
  name: 'OrgRoleAddUserForm',
  inject: ['pageContext', 'getOrgElementTreeData', 'getOrgRole'],
  props: {
    orgVersion: Object,
    defaultOrgRoleUuid: String
  },
  components: { OrgSelect },
  computed: {},
  data() {
    return {
      labelCol: { span: 5 },
      wrapperCol: { span: 19 },
      form: { orgRoleUuid: this.defaultOrgRoleUuid, orgElementId: undefined, userId: undefined },
      rules: {
        orgRoleUuid: [{ required: true, message: '请选择角色', trigger: ['blur', 'change'] }],
        orgElementId: [{ required: true, message: '请选择关联组织节点', trigger: ['blur', 'change'] }],
        userId: [{ required: true, message: '请选择角色成员', trigger: ['blur', 'change'] }]
      },
      roleSelectList: this.getOrgRole(),
      orgElementTreeData: this.getOrgElementTreeData()
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    save() {
      let _this = this;
      return new Promise((resolve, reject) => {
        _this.$refs.form.validate(pass => {
          if (pass) {
            let members = [],
              userId = _this.form.userId.split(';');
            for (let u of userId) {
              members.push({
                orgVersionUuid: _this.orgVersion.uuid,
                orgRoleUuid: _this.form.orgRoleUuid,
                orgElementId: _this.form.orgElementId,
                member: u
              });
            }
            $axios
              .post(`/proxy/api/org/organization/version/addOrgRoleMember`, members)
              .then(({ data }) => {
                if (data.code == 0) {
                  resolve();
                }
              })
              .catch(error => {});
          }
        });
      });
    }
  }
};
</script>
