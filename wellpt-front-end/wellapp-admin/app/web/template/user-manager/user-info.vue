<template>
  <div>
    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
    <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" :colon="false">
      <a-tabs default-active-key="acctInfo" :tabPosition="tabPosition">
        <a-tab-pane key="acctInfo" tab="账号信息">
          <div style="height: 400px; padding-right: 10px">
            <div v-if="!editable" style="position: absolute; width: 100%; height: 100%; z-index: 1"></div>
            <a-form-model-item label="账号" prop="loginName">
              <a-input v-model="form.loginName" v-if="!uuid" />
              <label v-else>{{ form.loginName }}</label>
            </a-form-model-item>
            <a-form-model-item label="登录密码" prop="password" v-if="!uuid">
              <a-input-password
                v-model="form.password"
                autocomplete="new-password"
                :max-length="passwordMaxLength"
                :placeholder="passwordPlaceholder"
              />
            </a-form-model-item>
            <a-form-model-item label="确认密码" prop="confirmPassword" v-if="!uuid">
              <a-input-password v-model="form.confirmPassword" />
            </a-form-model-item>
            <a-form-model-item label="姓名" prop="userName">
              <a-input v-model.trim="form.userName" />
            </a-form-model-item>
          </div>
        </a-tab-pane>
        <a-tab-pane key="userInfo" tab="个人信息">
          <Scroll @mounted="scroll => updateScrollContentHeight(scroll, 'userInfo')" style="height: 400px; padding-right: 10px">
            <div
              v-if="!editable"
              style="position: absolute; width: 100%; height: 100%; z-index: 1"
              :style="{ height: scrollContentHeights.userInfo }"
            ></div>
            <div :style="{ textAlign: 'center', padding: '15px', position: 'relative' }">
              <span class="user-avatar">
                <!-- <a-badge>
                <a-icon type="close-square" theme="filled" slot="count" style="color: #f5222d" />

              </a-badge> -->
                <a-avatar
                  :size="120"
                  :icon="form.avatar ? null : 'user'"
                  :src="form.avatar ? '/proxy/org/user/view/photo/' + form.avatar : null"
                  title="点击头像进行上传或者替换"
                  style="cursor: pointer"
                  @click.stop="onClickUploadAvatar"
                />
                <div class="user-info-avatar-operation" v-show="form.avatar != undefined">
                  <a-button size="small" @click.stop="onClickUploadAvatar" type="primary" title="替换头像">替换</a-button>
                  <a-button size="small" @click.stop="form.avatar = undefined" type="danger" title="删除头像">删除</a-button>
                </div>
              </span>

              <a-upload
                list-type="picture-card"
                class="avatar-uploader"
                :show-upload-list="false"
                v-show="false"
                :customRequest="customImageRequest"
                accept="image/png, image/jpeg"
              >
                <a-button ref="uploadButton"></a-button>
              </a-upload>
            </div>
            <a-form-model-item label="英文名" prop="enName">
              <a-input v-model="form.enName" />
            </a-form-model-item>
            <a-form-model-item label="性别" prop="gender">
              <a-radio-group v-model="form.gender" button-style="solid">
                <a-radio-button value="MALE">男</a-radio-button>
                <a-radio-button value="FEMALE">女</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="身份证号" prop="idNumber">
              <a-input v-model="form.idNumber"></a-input>
            </a-form-model-item>
            <a-form-model-item label="手机号码" prop="ceilPhoneNumber">
              <a-input v-model="form.ceilPhoneNumber"></a-input>
            </a-form-model-item>
            <a-form-model-item label="家庭电话">
              <a-input v-model="form.familyPhoneNumber"></a-input>
            </a-form-model-item>
            <a-form-model-item label="办公电话">
              <a-input v-model="form.businessPhoneNumber"></a-input>
            </a-form-model-item>
            <a-form-model-item label="邮箱" prop="mail">
              <a-input v-model="form.mail"></a-input>
            </a-form-model-item>
            <a-form-model-item label="备注">
              <a-textarea v-model="form.remark"></a-textarea>
            </a-form-model-item>
          </Scroll>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style lang="less" scoped>
.user-info-avatar-operation {
  opacity: 0;
  position: absolute;
  bottom: 60px;
  left: 50%;
  transform: translate(-50%, 0);
}
.user-avatar:hover {
  .user-info-avatar-operation {
    opacity: 1;
  }
}
</style>
<script type="text/babel">
import { debounce } from 'lodash';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import { generateId } from '@framework/vue/utils/util';

export default {
  name: 'UserInfo',
  props: {
    userUuid: String,
    tabPosition: {
      type: String,
      default: 'left'
    },
    displayState: {
      type: String,
      default: 'edit' // edit、label
    }
  },
  components: { Drawer },
  inject: ['pageContext', '$event', 'currentWindow'],
  data() {
    let rules = {
      userName: { required: true, message: '姓名必填', trigger: ['blur'] },
      mail: { pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/, message: '请输入正确的邮件格式', trigger: ['blur'] },
      businessPhoneNumber: {
        pattern: /^\d{4}-\d{7}$|^0\d{2,3}-\d{7,8}$/,
        message: '请输入正确的办公电话格式',
        trigger: ['blur']
      },
      familyPhoneNumber: {
        pattern: /^\d{4}-\d{7}$|^0\d{2,3}-\d{7,8}$/,
        message: '请输入正确的家庭电话格式',
        trigger: ['blur']
      },
      ceilPhoneNumber: {
        pattern: /^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/,
        message: '请输入正确的手机号码',
        trigger: ['blur']
      },
      idNumber: {
        pattern: /^[1-9]\d{5}(19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[Xx\d]$/,
        message: '请输入正确的身份号码',
        trigger: ['blur']
      }
    };
    let uuid = this.userUuid;
    if (this.$event != undefined && this.$event.meta) {
      uuid = this.$event.meta.UUID;
    }
    if (!uuid) {
      rules.loginName = [
        { required: true, message: '账号必填', trigger: ['blur'] },
        {
          pattern: /^[a-zA-Z0-9_]{2,20}$/,
          message: '账号只能是大小写字母，数字，_ 组成，长度为2-20个字符',
          trigger: ['blur', 'change']
        },
        { trigger: ['blur', 'change'], validator: this.checkLoginNameUnique }
      ];
      rules.password = [{ required: true, message: '密码必填', trigger: ['blur'] }];
      rules.confirmPassword = [
        { required: true, message: '请确认密码', trigger: ['blur'] },
        { trigger: ['blur'], validator: this.passwordDoubleCheck }
      ];
    }

    return {
      loading: uuid != undefined,
      uuid,
      labelCol: {
        span: 5,
        style: {
          width: '80px'
        }
      },

      wrapperCol: { span: 19 },
      form: { avatar: undefined },
      rules,
      extAttrs: ['idNumber', 'familyPhoneNumber', 'businessPhoneNumber'],

      initPwd: false,
      scrollContentHeights: {
        userInfo: '100%',
        workInfo: '100%'
      },
      passwordMaxLength: 20,
      passwordPlaceholder: ''
    };
  },
  computed: {
    editable() {
      return this.displayState == 'edit';
    }
  },
  watch: {},
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getUserInfo();
    this.setNewPasswordRules();
  },
  mounted() {},
  methods: {
    fetchUserAcctPasswordRules() {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy/api/user/getAcctPasswordRules`, {
            params: {}
          })
          .then(({ data }) => {
            this.loading = false;
            if (data.code == 0 && data.data) {
              this.passwordRules = data.data;
            }
            resolve();
          })
          .catch(error => {
            this.$message.error('加载密码规则配置异常');
            this.loading = false;
            resolve();
          });
      });
    },
    setNewPasswordRules() {
      if (this.uuid == undefined) {
        let _this = this;
        this.fetchUserAcctPasswordRules().then(() => {
          let isEmptyRule = Object.keys(this.passwordRules).length == 0;
          let newPwdRule = { trigger: ['blur', 'change'] };
          if (!isEmptyRule) {
            let requireCnt = parseInt(this.passwordRules.charTypeNumRequire);
            this.passwordMaxLength = parseInt(this.passwordRules.maxLength);
            this.passwordMinLength = parseInt(this.passwordRules.minLength);
            let mustContainsUpperLowerCaseChar = this.passwordRules.mustContainsUpperLowerCaseChar === 'true';
            let placeholder =
              this.passwordMinLength != this.passwordMaxLength
                ? `${this.passwordMinLength}-${this.passwordMaxLength}位长度密码`
                : `${this.passwordMaxLength}位长度密码`;
            placeholder += ', 要求字母、数字、特殊字符中';
            placeholder += requireCnt == 1 ? '至少包含一种' : requireCnt == 2 ? '至少包含两种' : '包含三种';
            if (mustContainsUpperLowerCaseChar) {
              placeholder += ', 且密码包含字母时必须同时包含大小写字母';
            }
            this.passwordPlaceholder = placeholder;
            newPwdRule.validator = debounce(function (rule, value, callback) {
              _this.passwordPatternValidate(rule, value, callback);
            }, 500);
          } else {
            newPwdRule.pattern =
              /^[a-zA-Z0-9~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]{4,20}$/;
            newPwdRule.message = '密码只能是字母、数字、特殊字符中至少包含1种，4~20位';
            this.passwordPlaceholder = '至少包含字母、数字、特殊字符, 4-20位';
          }
          this.rules.password.push(newPwdRule);
        });
      }
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
    save(e) {
      let _this = this;
      return new Promise((resolve, reject) => {
        this.$loading('保存中');

        this.$refs.form.validate(valid => {
          if (valid) {
            // 扩展信息
            _this.form.userInfoExts = [];
            for (let attrKey of _this.extAttrs) {
              _this.form.userInfoExts.push({
                attrKey,
                attrValue: _this.form[attrKey]
              });
            }
            if (e && e.eventParams && e.eventParams.accountType) {
              _this.form.accountType = e.eventParams.accountType;
            }
            $axios
              .post('/proxy/api/user/org/save', _this.form)
              .then(({ data }) => {
                _this.$loading(false);
                if (data.code == 0) {
                  _this.$message.success(_this.form.uuid ? '修改用户成功' : '新建用户成功');
                  if (_this.currentWindow != undefined) {
                    _this.currentWindow.close();
                  }
                  if (_this.$event && _this.$event.$evtWidget && _this.$event && _this.$event.$evtWidget.refetch) {
                    _this.$event.$evtWidget.refetch();
                  }
                  resolve();
                }
              })
              .catch(error => {
                _this.$message.error('服务异常');
                _this.$loading(false);
              });
          } else {
            this.$loading(false);
          }
        });
      });
    },
    // 自定义图片上传请求
    customImageRequest(options) {
      if (options.file.type.indexOf('image/') !== 0) {
        this.$message.error('上传文件非图片格式');
        return;
      }

      this.upload(options.file);
    },

    upload(file) {
      let _this = this,
        fileSize = file.size,
        fileName = file.name;
      let formData = new FormData();
      formData.set('frontUUID', file.uid);
      formData.set('md5', file.md5);
      formData.set('localFileSourceIcon', '');
      formData.set('size', fileSize);

      let headers = {
        'Content-Disposition': `attachment; filename="${encodeURIComponent(fileName)}"`,
        'Content-Type': 'multipart/form-data'
      };
      formData.set('file', file);
      $axios
        .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
          headers: headers
        })
        .then(({ data }) => {
          console.log(data);
          if (data.data) {
            _this.form.avatar = data.data[0].fileID;
          }
        })
        .catch(function (error) {
          console.error('上传文件失败, 异常: ', error);
        });
    },

    onClickUploadAvatar() {
      this.$refs.uploadButton.$el.click();
    },

    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },

    getUserInfo() {
      let _this = this;
      if (this.uuid) {
        $axios
          .get(`/proxy/api/user/org/getUserDetails`, {
            params: {
              userUuid: this.uuid
            }
          })
          .then(({ data }) => {
            _this.loading = false;
            if (data.code == 0 && data.data) {
              _this.form = data.data;
              // 扩展信息
              if (data.data.userInfoExts) {
                for (let extAttr of data.data.userInfoExts) {
                  if (_this.extAttrs.includes(extAttr.attrKey)) {
                    _this.form[extAttr.attrKey] = extAttr.attrValue;
                  }
                }
              }
            }
          });
      }
    },

    passwordDoubleCheck(rule, value, callback) {
      if (this.form.password && value === this.form.password) {
        callback();
      } else {
        callback('确认密码错误');
      }
    },
    checkLoginNameUnique: debounce(function (rule, value, callback) {
      $axios.get(`/proxy/api/user/checkLoginNameOccupied/${value}`, {}).then(({ data }) => {
        if (data.code == 0) {
          callback(data.data ? '账号已使用' : undefined);
        } else {
          callback('服务异常');
        }
      });
    }, 500),
    updateScrollContentHeight(scroll, key) {
      let perfectScrollbarRef = scroll.$refs.perfectScrollbarRef;
      if (perfectScrollbarRef && perfectScrollbarRef.ps && perfectScrollbarRef.ps.contentHeight) {
        this.scrollContentHeights[key] = perfectScrollbarRef.ps.contentHeight + 'px';
      }
    }
  },
  META: {
    method: {
      save: '保存用户'
    }
  }
};
</script>
