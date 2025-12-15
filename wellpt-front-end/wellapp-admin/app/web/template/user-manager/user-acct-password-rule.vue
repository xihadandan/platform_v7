<template>
  <div>
    <div v-if="loading" class="spin-center">
      <a-spin />
    </div>
    <a-form-model v-else :model="formData" :label-col="labelCol" :wrapper-col="wrapperCol" ref="form">
      <a-card title="格式规则" :bordered="false" size="small">
        <a-form-model-item label="字符要求">
          字母、数字、特殊字符中
          <a-select v-model="formData.charTypeNumRequire" style="width: 150px">
            <a-select-option :value="1">至少包含一种</a-select-option>
            <a-select-option :value="2">至少包含两种</a-select-option>
            <a-select-option :value="3">包含三种</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="字母限制">
          <a-checkbox v-model="formData.mustContainsUpperLowerCaseChar">必须要有大写、小写 (密码中存在字母时，对字母的限制)</a-checkbox>
        </a-form-model-item>
        <a-form-model-item label="长度要求" prop="minLength">
          最小值
          <a-input-number :min="6" :max="formData.maxLength || 30" v-model="formData.minLength" />
          最大值
          <a-input-number :min="6" :max="30" v-model="formData.maxLength" />
        </a-form-model-item>
      </a-card>
      <a-card title="安全规则" :bordered="false" size="small">
        <a-form-model-item label="密码有效期">
          <a-switch v-model="formData.enablePasswordTimeLimit" style="margin-right: 8px" />
          <span v-show="formData.enablePasswordTimeLimit">
            <a-input-number :min="1" v-model="formData.passwordLimitValidDay" />
            天后过期, 提前
            <a-input-number :min="1" :max="formData.passwordLimitValidDay" v-model="formData.notifyDayBeforeExpired" />
            天提醒, 过期后强制修改密码
          </span>
        </a-form-model-item>
        <a-form-model-item label="密码输入错误时账号锁定">
          <div>
            <a-switch v-model="formData.lockIfInputError" style="margin-right: 8px" />
            <span v-show="formData.lockIfInputError">
              连续输入错误
              <a-input-number :min="3" :max="10" v-model="formData.lockIfInputErrorNum" />
              次, 账号锁定
              <a-input-number :min="3" :max="120" v-model="formData.lockMinuteIfInputError" />
              分钟
            </span>
          </div>
          <a-checkbox v-model="formData.closeLockRuleToReleaseAcctLocked">关闭账号锁定时, 自动解锁【已锁定账号】</a-checkbox>
        </a-form-model-item>
        <a-form-model-item label="登录时校验密码格式">
          <a-switch v-model="formData.loginCheckPasswordPattern" />
          开启时, 登录密码正确, 但不满足密码格式时, 强制修改密码
        </a-form-model-item>
        <a-form-model-item label="非用户设置的密码-登录时强制修改">
          <a-switch v-model="formData.forceModifySysInitPassword" />
          非用户设置的密码：通过系统初始化用户/管理员重置密码/管理员创建用户的操作，生成的密码
        </a-form-model-item>
      </a-card>
      <a-card title="其他规则" :bordered="false" size="small">
        <a-form-model-item>
          <template slot="label">
            <a-space>
              管理员设置的密码类型
              <a-popover>
                <template slot="content">
                  随机密码: 系统自动生成, 1个用户1个密码
                  <br />
                  自定义密码: 管理员可编辑, 批量操作时,操作的所有用户为1个密码
                </template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-radio-group v-model="formData.sysInitPasswordSource" button-style="solid" size="small">
            <a-radio-button value="random">随机密码</a-radio-button>
            <a-radio-button value="customized">自定义密码</a-radio-button>
            <!-- <a-radio-button value="default">默认密码</a-radio-button> -->
          </a-radio-group>
        </a-form-model-item>
      </a-card>
      <div style="text-align: center; padding: 12px 0px">
        <a-button type="primary" @click="save" :loading="saveLoading" icon="save">{{ saveButtonTitle }}</a-button>
      </div>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'UserAcctPasswordRule',
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      formData: {
        charTypeNumRequire: 1,
        mustContainsUpperLowerCaseChar: false,
        minLength: 6,
        maxLength: 12,
        enablePasswordTimeLimit: false,
        passwordLimitValidDay: 90,
        notifyDayBeforeExpired: 3,
        lockIfInputError: false,
        lockIfInputErrorNum: 6,
        lockMinuteIfInputError: 30,
        closeLockRuleToReleaseAcctLocked: false,
        loginCheckPasswordPattern: false,
        forceModifySysInitPassword: false,
        sysInitPasswordSource: 'random'
      },
      labelCol: { span: 5 },
      wrapperCol: { span: 19 },
      saveLoading: false,
      loading: true,
      saveButtonTitle: '保存',
      remarks: {
        charTypeNumRequire: '字母、数字、特殊字符要求种类',
        minLength: '最小长度',
        maxLength: '最大长度',
        mustContainsUpperLowerCaseChar: '必须要有大写、小写 (密码中存在字母时，对字母的限制)',
        enablePasswordTimeLimit: '开启密码有效期',
        passwordLimitValidDay: '密码有效期天数',
        notifyDayBeforeExpired: '失效前几天提醒',
        lockIfInputError: '输入密码错误锁定开启',
        lockIfInputErrorNum: '输入密码错误次数后锁定',
        lockMinuteIfInputError: '超过密码输入错误次数后锁定分钟数',
        closeLockRuleToReleaseAcctLocked: '关闭账号锁定时, 自动解锁【已锁定账号】',
        loginCheckPasswordPattern: '登录时校验密码格式',
        forceModifySysInitPassword: '非用户设置的密码-登录时强制修改',
        sysInitPasswordSource: '管理员设置的密码类型'
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.fetchUserAcctPasswordRules();
  },
  methods: {
    save() {
      this.saveLoading = true;
      let list = [];
      for (let key in this.formData) {
        list.push({
          attrKey: key,
          attrVal: this.formData[key],
          remark: this.remarks[key]
        });
      }
      this.$axios
        .post(`/proxy/api/user/saveAcctPasswordRules`, list)
        .then(({ data }) => {
          this.saveLoading = false;
          this.saveButtonTitle = '保存';
          if (data.code == 0) {
            this.$message.success('保存成功');
          } else {
            this.$message.error('保存失败');
          }
        })
        .catch(error => {
          this.saveLoading = false;
          this.$message.error('保存失败');
        });
    },
    fetchUserAcctPasswordRules() {
      this.$axios
        .get(`/proxy/api/user/getAcctPasswordRules`, {
          params: {}
        })
        .then(({ data }) => {
          this.loading = false;
          if (data.code == 0 && data.data) {
            this.saveButtonTitle = Object.keys(data.data).length == 0 ? '初始化保存' : '保存';
            for (let key in data.data) {
              let v = data.data[key];
              if (/^-?\d+$/.test(v)) {
                v = parseInt(v);
              } else if (['true', 'false'].includes(v)) {
                v = v === 'true';
              }
              this.$set(this.formData, key, v);
            }
          }
        })
        .catch(error => {
          this.$message.error('加载密码规则配置异常');
          this.loading = false;
        });
    }
  }
};
</script>
