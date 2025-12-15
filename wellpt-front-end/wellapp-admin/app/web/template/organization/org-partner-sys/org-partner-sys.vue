<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
      <a-form-model-item label="协作系统标识" prop="resSystemCode">
        <a-input v-model="form.resSystemCode" @change="inputChange" />
      </a-form-model-item>
      <a-form-model-item label="协作系统名称">{{ partnerSysName }}</a-form-model-item>
      <a-form-model-item label="ID" v-if="form.uuid">
        <span>{{ form.id }}</span>
      </a-form-model-item>

      <a-form-model-item label="添加至分类">
        <a-select :options="vPageState.categoryOptions" v-model="form.categoryUuid" allow-clear />
      </a-form-model-item>
      <a-form-model-item label="添加原因">
        <a-textarea v-model="form.applyReason" />
      </a-form-model-item>
    </a-form-model>
  </a-skeleton>
</template>

<script type="text/babel">
// import { encode, decode } from 'js-base64';

export default {
  name: 'OrgPartnerSys',
  props: {},
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    let uuid = this.vPageState && this.vPageState.orgUuid ? this.vPageState.orgUuid : undefined;
    if (!uuid) {
      uuid = this.$event && this.$event.meta && this.$event.meta.uuid ? this.$event.meta.uuid : undefined;
    }

    return {
      loading: uuid != undefined,
      uuid,
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      form: {
        resSystemCode: JSON.stringify({ system: 'oa', tenant: 'T001', systemName: '办公系统', tenantName: '租户A' })
      },
      limitType: '0',
      rules: {
        resSystemCode: { required: true, message: '协作系统标识必填', trigger: ['blur', 'change'] }
      }
    };
  },
  watch: {},
  beforeCreate() {},
  components: {},
  computed: {
    partnerSysName() {
      let obj = JSON.parse(this.form.resSystemCode);
      return `${obj.tenantName} (${obj.systemName})`;
    }
  },
  created() {},
  methods: {
    inputChange() {},
    save(event) {
      let _this = this;
      let res = JSON.parse(this.form.resSystemCode);
      this.form.resSystem = res.system;
      this.form.resTenant = res.tenant;
      this.form.resName = res.tenantName + '(' + res.systemName + ')';
      $axios
        .post('/proxy/api/org/organization/orgPartnerSysApply/addApply', this.form)
        .then(({ data }) => {
          if (data.code == 0) {
            _this.$message.success('保存成功');
            event.$evtWidget.closeModal(); // 关闭弹窗

            _this.pageContext.emitEvent('vghYteagYQxRSCvfdJulWdBPxeRXxCGS:refetch');
            _this.pageContext.emitEvent('byOQImqyNgnIzdeJAOwXAoXXmXmPsDYY:refetchBadge');
          }
        })
        .catch(error => {
          _this.$message.error('服务异常');
        });
    }
  },
  beforeMount() {},
  mounted() {}
};
</script>
