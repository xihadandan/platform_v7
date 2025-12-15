<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    :label-col="$i18n.locale !== 'zh_CN' ? { span: 6 } : { span: 4 }"
    :wrapper-col="$i18n.locale !== 'zh_CN' ? { span: 17 } : { span: 19 }"
    :colon="false"
  >
    <a-form-model-item :label="$t('WorkflowWork.groupName', '群名称')" prop="groupName">
      <a-input v-model="formData.groupName"></a-input>
    </a-form-model-item>
    <a-form-model-item :label="$t('WorkflowWork.groupMember', '群成员')" prop="memberIds">
      <OrgSelect
        v-if="orgVersionIds"
        v-model="formData.memberIds"
        :orgVersionId="orgVersionId"
        :orgVersionIds="orgVersionIds"
        :checkableTypes="['user']"
        :params="orgParams"
        :orgType="['MyOrg', 'TaskUsers', 'TaskDoneUsers']"
      ></OrgSelect>
    </a-form-model-item>
    <a-form-model-item :label="$t('WorkflowWork.groupType', '群类型')" prop="provider">
      <a-radio-group v-model="formData.provider" :options="providerOptions" />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import OrgSelect from '@admin/app/web/lib/org-select.vue';
export default {
  name: 'WorkflowStartGroupChat',
  props: {
    workView: Object
  },
  components: {
    OrgSelect
  },
  created() {
    this.loadOrgVersionIds();
    this.loadProviderOptions();
  },
  data() {
    const _this = this;
    let workData = _this.workView.getWorkData();
    const title = _this.$i18n.locale !== 'zh_CN' ? workData.titleI18n || workData.title : workData.title;
    return {
      orgVersionId: null,
      orgVersionIds: null,
      formData: {
        flowInstUuid: workData.flowInstUuid,
        taskInstUuid: workData.taskInstUuid,
        title,
        groupName: _this.$t('WorkflowWork.groupChat', '群聊') + '：' + title,
        memberIds: _this._$USER.userId,
        ownerId: _this._$USER.userId,
        provider: 'feishu',
        corpId: ''
      },
      orgParams: { taskInstUuid: workData.taskInstUuid },
      providerOptions: [{ label: '飞书', value: 'feishu' }],
      rules: {
        groupName: [{ required: true, message: _this.$t('WorkflowWork.groupNameRequired', '请输入群名称！'), trigger: 'blur' }],
        memberIds: [
          { required: true, message: _this.$t('WorkflowWork.groupMemberRequired', '请选择群成员！'), trigger: 'blur' },
          {
            validator(rule, value, callback) {
              if (!value) {
                return false;
              }
              return value.split(';').length > 1;
            },
            message: _this.$t('WorkflowWork.groupMemberCountRequired', '至少选择两个成员'),
            trigger: 'change'
          },
          {
            validator(rule, value, callback) {
              if (!value) {
                return false;
              }
              return value.split(';').includes(_this._$USER.userId);
            },
            message: _this.$t('WorkflowWork.groupMemberIncludeSelf', '成员必须包含自己'),
            trigger: 'change'
          }
        ],
        provider: [{ required: true, message: _this.$t('WorkflowWork.groupTypeRequired', '请选择群类型！'), trigger: 'blur' }]
      }
    };
  },
  methods: {
    loadOrgVersionIds() {
      this.workView.getOrgVersionIds().then(orgVersionIds => {
        if (orgVersionIds && orgVersionIds.length > 0) {
          this.orgVersionId = orgVersionIds[0];
        }
        this.orgVersionIds = orgVersionIds;
      });
    },
    loadProviderOptions() {
      this.workView.listGroupChatProvider().then(providerOptions => {
        this.providerOptions = providerOptions;
        this.providerOptions.map(item => {
          item.label = this.$t('WorkflowWork.' + item.value, item.label);
        });
        let dingtalkItem = this.providerOptions.find(item => item.value == 'dingtalk');
        if (dingtalkItem) {
          this.loadCorpId();
        }
      });
    },
    loadCorpId() {
      return $axios
        .get('/proxy/api/dingtalk/config/getEnabledCorpId')
        .then(({ data: result }) => {
          if (result.data) {
            this.formData.corpId = result.data;
          }
        })
        .catch(error => {});
    },
    collect() {
      return this.$refs.form.validate().then(valid => {
        if (valid) {
          return this.formData;
        } else {
          return Promise.reject('表单验证失败');
        }
      });
    }
  }
};
</script>

<style></style>
