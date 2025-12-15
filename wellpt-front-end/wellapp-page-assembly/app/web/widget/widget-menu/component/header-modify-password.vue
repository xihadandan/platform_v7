<template>
  <Modal
    title="修改密码"
    :ok="saveNewPassword"
    ref="modal"
    :closable="closable"
    :okButtonProps="okButtonProps"
    :cancelButtonProps="cancelButtonProps"
    :escKeyClosable="false"
    @show="onShow"
    :mask="mask"
    :visible="visible"
  >
    <slot></slot>
    <template slot="content">
      <a-alert :message="message" banner v-if="modifyCode && message" style="margin-bottom: 12px" />
      <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
        <a-form-model-item label="旧密码" prop="oldPassword">
          <a-input-password v-model.trim="form.oldPassword" placeholder="验证旧密码" autocomplete="off" />
        </a-form-model-item>
        <a-form-model-item label="新密码" prop="newPassword">
          <a-input-password v-model.trim="form.newPassword" :maxLength="maxLength" :placeholder="passwordPlaceholder" />
        </a-form-model-item>
        <a-form-model-item label="确认密码" prop="newPasswordAgain">
          <a-input-password v-model.trim="form.newPasswordAgain" placeholder="请再次输入新密码" />
        </a-form-model-item>
      </a-form-model>
    </template>
  </Modal>
</template>
<style lang="less"></style>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import { debounce } from 'lodash';
import { encode, decode } from 'js-base64';

export default {
  name: 'HeaderModifyPassword',
  props: {
    maxLength: {
      type: Number,
      default: 20
    },
    visible: { type: Boolean, default: false },
    mask: { type: Boolean, default: false },
    closable: { type: Boolean, default: true },
    okButtonProps: Object,
    cancelButtonProps: Object,
    modifyCode: String
  },
  components: { Modal },
  computed: {
    message() {
      return {
        forceModifyPasswordCauseBySystemInit: '检测到您正在使用系统初始密码, 为了账户安全, 请立即修改密码',
        forceModifyPasswordCauseByExpired: '您的密码已过期, 为了账户安全, 请立即修改密码',
        forceModifyPasswordCauseByPatternRequired: '您的密码不符合安全要求, 为了账户安全, 请立即修改密码'
      }[this.modifyCode];
    }
  },
  data() {
    return {
      passwordPlaceholder: '',
      labelCol: { span: 4 },
      // idPrefix: 'module_',
      wrapperCol: { span: 16 },
      maxLength: 20,
      passwordRules: {},
      form: {
        oldPassword: undefined,
        newPassword: undefined,
        newPasswordAgain: undefined
      },
      rules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' },
          { trigger: ['blur', 'change'], validator: this.checkCurrentPassword }
        ],
        newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
        newPasswordAgain: [
          { required: true, message: '请输入确认密码', trigger: 'blur' },
          { trigger: ['blur', 'change'], validator: this.checkConfirmPassword }
        ]
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchUserAcctPasswordRules();
  },
  mounted() {},
  methods: {
    onShow() {
      this.fetchUserAcctPasswordRules();
    },
    passwordPatternValidate(rule, value, callback) {
      let requireCnt = parseInt(this.passwordRules.charTypeNumRequire),
        mustContainsUpperLowerCaseChar = this.passwordRules.mustContainsUpperLowerCaseChar === 'true',
        minLength = parseInt(this.passwordRules.minLength),
        maxLength = parseInt(this.passwordRules.maxLength);
      let rst =
        value.length >= minLength &&
        value.length <= maxLength &&
        this.isValidPasswordFlexible(value, requireCnt, mustContainsUpperLowerCaseChar);
      callback(!rst ? this.passwordPlaceholder : undefined);
    },
    isValidPasswordFlexible(password, minRequirements, requireBothCases) {
      if (!password || password.length === 0 || minRequirements < 1 || minRequirements > 3) {
        return false;
      }

      // 检查各种字符类型
      const hasLowerCase = /[a-z]/.test(password);
      const hasUpperCase = /[A-Z]/.test(password);
      const hasDigit = /\d/.test(password);
      const hasSpecial = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password);

      // 处理字母要求
      let hasLetter = false;
      if (hasLowerCase || hasUpperCase) {
        // 存在字母的情况下
        if (requireBothCases) {
          hasLetter = hasLowerCase && hasUpperCase; // 必须同时有大小写
        } else {
          hasLetter = hasLowerCase || hasUpperCase; // 有任意一种即可
        }
      }

      // 计算满足的条件数量（字母算作一个条件）
      let metConditions = 0;
      if (hasLetter) metConditions++;
      if (hasDigit) metConditions++;
      if (hasSpecial) metConditions++;

      // 检查是否满足最小要求数量
      return metConditions >= minRequirements;
    },
    fetchUserAcctPasswordRules() {
      let _this = this;
      this.$axios
        .get(`/proxy/api/user/getAcctPasswordRules`, {
          params: {}
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.passwordRules = data.data;
            let isEmptyRule = Object.keys(data.data).length == 0;
            let newPwdRule = { trigger: ['blur', 'change'] };
            if (!isEmptyRule) {
              let requireCnt = parseInt(this.passwordRules.charTypeNumRequire),
                minLength = parseInt(this.passwordRules.minLength);
              this.maxLength = parseInt(this.passwordRules.maxLength);
              let mustContainsUpperLowerCaseChar = this.passwordRules.mustContainsUpperLowerCaseChar === 'true';
              let placeholder = minLength != this.maxLength ? `${minLength}-${this.maxLength}位长度密码` : `${this.maxLength}位长度密码`;
              placeholder += ', 要求字母、数字、特殊字符中';
              placeholder += requireCnt == 1 ? '至少包含一种' : requireCnt == 2 ? '至少包含两种' : '包含三种';
              if (mustContainsUpperLowerCaseChar) {
                placeholder += ', 且密码包含字母时必须同时包含大小写字母';
              }
              this.passwordPlaceholder = placeholder;
              newPwdRule.validator = debounce(function (rule, value, callback) {
                if (_this.form.oldPassword == _this.form.newPassword) {
                  callback('新密码不能与旧密码相同');
                  return;
                }
                _this.passwordPatternValidate(rule, value, callback);
              }, 500);
            } else {
              this.passwordPlaceholder = '至少包含字母、数字、特殊字符, 4-20位';
              newPwdRule.validator = debounce(function (rule, value, callback) {
                if (_this.form.oldPassword == _this.form.newPassword) {
                  callback('新密码不能与旧密码相同');
                  return;
                }
                callback(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[^]{4,20}$/.test(value) ? undefined : '密码必须包含字母、数字、特殊字符, 4-20位');
              }, 500);
            }

            this.rules.newPassword.push(newPwdRule);
          }
        })
        .catch(error => {});
    },
    checkConfirmPassword(rule, value, callback) {
      callback(this.form.newPasswordAgain != this.form.newPassword ? '密码不一致' : undefined);
    },
    checkCurrentPassword: debounce(function (rule, value, callback) {
      $axios
        .get(`/proxy/api/user/checkPassword`, {
          params: {
            password: encode(encodeURI(value))
          }
        })
        .then(({ data }) => {
          if (data.code == 0) {
            callback(data.data === false ? '密码错误' : undefined);
          } else {
            callback('服务异常');
          }
        });
    }, 500),
    saveNewPassword(e) {
      let _this = this;
      this.$refs.form.validate((passed, msg) => {
        if (passed) {
          $axios
            .get('/proxy/api/user/modifyPassword', {
              params: {
                newPassword: encode(encodeURI(_this.form.newPasswordAgain)),
                oldPassword: encode(encodeURI(_this.form.oldPassword))
              }
            })
            .then(({ data }) => {
              if (data.code == 0 && data.data === true) {
                e(true);
                this.$message.success('修改密码成功');
                if (this.modifyCode != undefined) {
                  document.cookie = 'force_modify_password_code=;Max-Age=0;path=/';
                }
              } else {
                this.$message.error('修改密码失败');
              }
            })
            .catch(() => {
              this.$message.error('修改密码失败');
            });
        }
      });
    }
  }
};
</script>
