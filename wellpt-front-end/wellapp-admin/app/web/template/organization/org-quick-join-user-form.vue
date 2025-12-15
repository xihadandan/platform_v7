<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" :colon="false" class="pt-form">
    <a-form-model-item label="选择成员" prop="userIds">
      <a-select
        style="width: 100%"
        v-model="form.userIds"
        placeholder="搜索更多用户"
        @search="searchUser"
        mode="multiple"
        show-arrow
        @change="onChangeUser"
        :filter-option="false"
      >
        <template slot="notFoundContent" v-if="!userFetching">
          <div style="text-align: center">查无用户</div>
        </template>
        <a-select-option v-for="d in userOptions" :key="d.value">
          {{ d.label }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="组织节点" prop="orgElementId">
      <OrgSelect
        title="选择"
        ref="orgSelect"
        v-model="form.orgElementId"
        :orgVersionId="orgVersion.id"
        :checkableTypes="checkableTypes"
        :showBizOrgUnderOrg="false"
        orgType="MyOrg"
        :uncheckableTypes="['job']"
        :params="orgSelectParams"
        @change="onChangeOrgElementSelect"
        :multiSelect="false"
      />
    </a-form-model-item>
    <a-form-model-item label="职位" v-if="form.orgElementId != undefined">
      <a-select style="width: 100%" v-model="form.jobId" :options="jobOptions" show-search allow-clear></a-select>
    </a-form-model-item>
    <a-form-model-item label="直接汇报人">
      <OrgSelect
        title="选择人员或职位"
        ref="orgSelect"
        v-model="form.directReporter"
        :orgVersionId="orgVersion.id"
        :showBizOrgUnderOrg="false"
        :checkableTypes="['job', 'user']"
        orgType="MyOrg"
      />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import { debounce } from 'lodash';
export default {
  name: 'OrgQuickJoinUserForm',
  props: { orgVersion: Object },
  components: { OrgSelect },
  inject: ['getOrgElementModels', 'getOrgElementTreeNodeMap', 'orgElementManagementMap'],
  data() {
    return {
      userOptions: [],
      userFetching: false,
      allUsers: [],
      labelCol: { span: 5 },
      wrapperCol: { span: 19 },
      checkableTypes: ['unit', 'dept'],
      form: {
        orgVersionUuid: this.orgVersion.uuid,
        userIds: undefined,
        orgElementId: undefined,
        jobId: undefined,
        directReporter: undefined
      },
      rules: {
        userIds: [{ required: true, message: '请选择成员', trigger: ['blur', 'change'] }],
        orgElementId: [{ required: true, message: '请选择组织节点', trigger: ['blur', 'change'] }]
      },
      orgSelectParams: { userHidden: true }
    };
  },
  computed: {
    jobOptions() {
      let treeKeyNodeMap = this.getOrgElementTreeNodeMap(),
        jobOptions = [];
      if (this.form.orgElementId) {
        for (let key in treeKeyNodeMap) {
          if (treeKeyNodeMap[key].data.id == this.form.orgElementId) {
            let node = treeKeyNodeMap[key];
            let findJob = (list, pname) => {
              if (list) {
                for (let i = 0, len = list.length; i < len; i++) {
                  if (list[i].data.type == 'job') {
                    jobOptions.push({
                      label: (pname ? pname + '/' : '') + list[i].title,
                      value: list[i].data.id
                    });
                  }
                  // 不需要子节点的职位
                  // else {
                  //   findJob(list[i].children, (pname ? pname + '/' : '') + list[i].title);
                  // }
                }
              }
            };
            findJob(node.children, undefined);
            break;
          }
        }
      }
      return jobOptions;
    }
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    let _this = this;
    let orgEModels = this.getOrgElementModels();
    if (orgEModels) {
      for (let o of orgEModels) {
        if (!['unit', 'dept', 'job'].includes(o.id)) {
          this.checkableTypes.push(o.id);
        }
      }
    }
    if (this.orgElementManagementMap != undefined && Object.keys(this.orgElementManagementMap).length > 0) {
      // 只展示管理节点下的组织节点
      this.orgSelectParams.includeKeys = Object.keys(this.orgElementManagementMap).filter(function (key) {
        return (
          key.indexOf('_') != -1 &&
          _this.orgElementManagementMap[key].userAuthority &&
          _this.orgElementManagementMap[key].userAuthority.includes('joinUser')
        );
      });
      this.orgSelectParams.excludeKeys = Object.keys(this.orgElementManagementMap).filter(function (key) {
        if (
          key.indexOf('_') != -1 &&
          _this.orgElementManagementMap[key].userAuthority &&
          !_this.orgElementManagementMap[key].userAuthority.includes('joinUser')
        ) {
          return true;
        }
        return false;
      });
    }
  },
  mounted() {
    this.searchUser('a');
  },
  methods: {
    onChangeOrgElementSelect({ nodes }) {
      this.form.orgElementUuid == undefined;
      if (nodes && nodes.length > 0) {
        this.form.orgElementUuid = nodes[0].data.uuid;
      }
    },

    searchUser: debounce(function (value) {
      this.userFetching = true;
      $axios
        .get(`/proxy/api/user/getUsersLikeUserNameAndPinyin`, {
          params: {
            keyword: value,
            pageSize: 50
          }
        })
        .then(({ data }) => {
          this.userOptions.splice(0, this.userOptions.length);
          if (data.data) {
            for (let i = 0, len = data.data.length; i < len; i++) {
              this.userOptions.push({
                label: data.data[i].userName,
                value: data.data[i].userId
              });
            }
          }
          this.userFetching = false;
        })
        .catch(error => {});
    }, 300),
    onChangeUser() {},
    save() {
      let _this = this;
      this.$loading();
      return new Promise((resolve, reject) => {
        _this.$refs.form.validate(pass => {
          if (pass) {
            if (_this.form.directReporter) {
              _this.form.directReporter = _this.form.directReporter.split(';');
            }
            $axios
              .post(`/proxy/api/org/organization/joinOrgUser`, _this.form)
              .then(({ data }) => {
                _this.$loading(false);
                if (data.code == 0) {
                  _this.$message.success('添加成功');
                  _this.pageContext.emitEvent('refetchUserCountStatics');
                  resolve();
                }
              })
              .catch(error => {
                _this.$loading(false);
              });
          } else {
            _this.$loading(false);
          }
        });
      });
    },

    filterOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
      );
    }
  }
};
</script>
