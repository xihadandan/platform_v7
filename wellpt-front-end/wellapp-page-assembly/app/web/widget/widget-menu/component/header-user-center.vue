<template>
  <span @click="openDrawer">
    <slot></slot>
    <a-drawer
      wrapClassName="user-center-drawer"
      :wrap-style="{ position: 'absolute' }"
      :title="null"
      placement="top"
      height="100%"
      :closable="false"
      :visible="visible"
      :getContainer="getContainer"
      :drawerStyle="{ background: '#f0f2f5' }"
      :bodyStyle="{ height: '100%', padding: '0px' }"
      :afterVisibleChange="afterVisibleChange"
      @click.native.stop="() => {}"
    >
      <div class="spin-center" v-if="loading">
        <a-spin />
      </div>
      <a-row type="flex" style="height: 100%; flex-wrap: nowrap">
        <a-col flex="452px" style="margin-right: 12px; padding: 3px; background: #fff">
          <div class="user-info-card">
            <div class="user-name-avatar">
              <a-avatar
                :size="132"
                :src="user.userId ? '/proxy/org/user/view/photo/' + user.avatar : undefined"
                icon="user"
                style="background: #fff; color: var(--w-primary-color)"
              />
              <div class="user-name">
                {{ user.userName }}
              </div>
              <div v-if="workStateOption.length > 0" style="text-align: center; margin-bottom: 20px">
                <a-select
                  :options="workStateOption"
                  style="width: 108px"
                  v-model="workState"
                  class="work-state-select"
                  @change="onChangeWorkState"
                >
                  <template slot="suffixIcon" v-if="changingState">
                    <a-icon type="loading" />
                  </template>
                </a-select>
              </div>
            </div>

            <a-form-model
              :label-col="labelCol"
              :wrapper-col="wrapperCol"
              :colon="false"
              class="basic-info-form user-basic-info-form"
              labelAlign="right"
            >
              <a-form-model-item :label="$t('HeaderUserCenter.unit', '单位')">{{ user.unitName }}</a-form-model-item>
              <a-form-model-item :label="$t('HeaderUserCenter.department', '部门')">{{ user.deptName }}</a-form-model-item>
              <a-form-model-item :label="$t('HeaderUserCenter.position', '职位')">{{ user.mainJobName }}</a-form-model-item>
              <a-divider />
              <a-form-model-item :label="$t('HeaderUserCenter.phone', '手机')">{{ user.ceilPhoneNumber }}</a-form-model-item>
              <a-form-model-item :label="$t('HeaderUserCenter.officePhone', '办公电话')">{{ user.businessPhoneNumber }}</a-form-model-item>
              <a-form-model-item :label="$t('HeaderUserCenter.email', '邮箱')">{{ user.mail }}</a-form-model-item>
              <a-divider />
              <a-form-model-item :label="$t('HeaderUserCenter.lastLogin', '最近登录')">
                <label>{{ user.lastLoginTime }}</label>
              </a-form-model-item>
            </a-form-model>
          </div>
        </a-col>
        <a-col flex="auto" style="background: #fff; width: calc(100% - 470px)">
          <a-tabs v-model="activeTabKey">
            <a-button slot="tabBarExtraContent" type="link" icon="close" @click="visible = false">
              {{ $t('HeaderUserCenter.close', '关闭') }}
            </a-button>
            <a-tab-pane key="work-info" :tab="$t('HeaderUserCenter.workInfo', '工作信息')">
              <a-form-model :colon="false" class="basic-info-form work-info-form" labelAlign="left">
                <a-form-model-item :label="$t('HeaderUserCenter.workLocation', '工作属地')">
                  {{ editUserForm.workLocation || '-' }}
                </a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.mainJob', '主职')">{{ user.mainJobNamePath || '-' }}</a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.otherJobs', '其他职位')">
                  <template v-if="user.otherJobNamePaths">
                    <a-tag v-for="(p, i) in user.otherJobNamePaths" :key="'other-job-' + i">
                      {{ p }}
                    </a-tag>
                  </template>
                  <label v-if="user.otherJobNamePaths && user.otherJobNamePaths.length == 0">-</label>
                </a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.duty', '职务')">{{ user.dutyName || '-' }}</a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.jobRank', '职级')">{{ user.jobRankName || '-' }}</a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.directSuperior', '直接上级')">
                  <a-popover
                    placement="rightTop"
                    :getPopupContainer="getPopupContainer"
                    arrowPointAtCenter
                    overlayClassName="director-popover"
                    v-if="user.directorUserId"
                  >
                    <template slot="content">
                      <div
                        :class="{
                          'director-info-card': true,
                          'director-info-card-other': this.$i18n.locale !== 'zh_CN'
                        }"
                      >
                        <a-row type="flex" class="director-avatar-name" style="margin-bottom: 12px; flex-wrap: nowrap">
                          <a-col flex="65px">
                            <a-avatar
                              :size="64"
                              style="border: 1px solid #fff"
                              :src="user.directorUserId ? '/proxy/org/user/view/photo/' + user.directorUserId : undefined"
                              icon="user"
                            />
                          </a-col>
                          <a-col flex="auto" style="align-self: center">
                            <div style="margin-left: 12px">
                              <div class="user-name-state">
                                <label>
                                  {{ director.userName }}
                                </label>
                                <span class="work-state-tag">
                                  <label>
                                    {{ workStateLabelMap[director.workState] }}
                                  </label>
                                </span>
                              </div>
                              <div v-if="director.cellPhoneNumber" style="font-weight: 400; color: #999999">
                                {{ director.cellPhoneNumber }}
                              </div>
                            </div>
                          </a-col>
                        </a-row>

                        <a-form-model
                          :label-col="labelCol"
                          :wrapper-col="wrapperCol"
                          :colon="false"
                          class="basic-info-form work-info-form director"
                        >
                          <a-form-model-item :label="$t('HeaderUserCenter.unit', '单位')">{{ director.unitName }}</a-form-model-item>
                          <a-form-model-item :label="$t('HeaderUserCenter.department', '部门')">{{ director.deptName }}</a-form-model-item>
                          <a-form-model-item :label="$t('HeaderUserCenter.position', '职位')">{{ director.mainJobName }}</a-form-model-item>
                          <a-form-model-item :label="$t('HeaderUserCenter.phone', '手机')">
                            {{ director.ceilPhoneNumber }}
                          </a-form-model-item>
                        </a-form-model>
                      </div>
                    </template>
                    <div>
                      <a-avatar
                        style="border: 1px solid #ffffff"
                        :size="32"
                        :src="user.directorUserId ? '/proxy/org/user/view/photo/' + user.directorUserId : undefined"
                        icon="user"
                      />
                      <label class="director-name">
                        {{ director.userName }}
                      </label>
                    </div>
                  </a-popover>
                  <template v-else>-</template>
                </a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.subordinates', '下级')">
                  <template v-if="user.subordinateUsers && user.subordinateUsers.length > 0">
                    <template v-for="(sub, i) in user.subordinateUsers">
                      <a-tooltip :key="'sub-' + i">
                        <template slot="title">
                          {{ userIdNameMap[sub] }}
                        </template>
                        <a-avatar
                          :style="{
                            border: '1px solid #ffffff',
                            marginLeft: i > 0 ? '-10px' : undefined
                          }"
                          :size="32"
                          :src="'/proxy/org/user/view/photo/' + sub"
                          icon="user"
                        />
                      </a-tooltip>
                    </template>
                    <a-tooltip>
                      <template slot="title">
                        {{ $t('HeaderUserCenter.subordinateCount', '下级人数') }}: {{ user.subordinateUsers.length }}
                      </template>
                      <a-avatar
                        :style="{
                          marginLeft: '-10px',
                          backgroundColor: 'var(--w-primary-color)',
                          color: '#fff',
                          boxShadow: '0 0 0 1px var(--w-primary-color) inset'
                        }"
                      >
                        {{ user.subordinateUsers.length > 20 ? '20+' : user.subordinateUsers.length }}
                      </a-avatar>
                    </a-tooltip>
                  </template>
                  <template v-else>-</template>
                </a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.userGroups', '所属群组')">
                  <div v-if="userGroups && userGroups.length > 0" class="user-group-div">
                    <template v-for="(p, i) in userGroups">
                      <a-tag :key="i">
                        <a-icon type="user" />
                        {{ p.name }}
                      </a-tag>
                    </template>
                  </div>
                  <template v-else>-</template>
                </a-form-model-item>
              </a-form-model>
            </a-tab-pane>
            <a-tab-pane key="user-info" :tab="$t('HeaderUserCenter.personalInfo', '个人信息')">
              <a-form-model
                ref="editUserForm"
                :model="editUserForm"
                :colon="false"
                class="basic-info-form work-info-form edit"
                :rules="rules"
              >
                <a-form-model-item
                  :label="$t('HeaderUserCenter.avatar', '头像')"
                  :label-col="{
                    style: {
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'flex-end'
                    }
                  }"
                >
                  <div style="display: flex; align-items: center">
                    <div class="avatar">
                      <a-avatar
                        :size="80"
                        :src="editUserForm.userId ? '/proxy/org/user/view/photo/' + editUserForm.avatar : undefined"
                        icon="user"
                      ></a-avatar>
                      <div class="delete-cover" v-if="editUserForm.avatar">
                        <a-button
                          :title="$t('HeaderUserCenter.deleteAvatar', '删除头像')"
                          size="small"
                          type="link"
                          icon="delete"
                          @click="editUserForm.avatar = undefined"
                        />
                      </div>
                    </div>
                    <div style="margin: 0 12px">
                      <a-button icon="upload" size="small" @click.stop="onClickUploadAvatar">
                        {{ $t('HeaderUserCenter.uploadAvatar', '上传头像') }}
                      </a-button>
                      <div style="font-size: 12px; color: #999999">
                        {{ $t('HeaderUserCenter.avatarUploadTips', '支持格式: JPG、JPEG、PNG; 大小限制: 不超过5M; 建议尺寸: 128px*128px') }}
                      </div>
                    </div>

                    <a-upload
                      list-type="picture-card"
                      class="avatar-uploader"
                      :show-upload-list="false"
                      v-show="false"
                      :before-upload="e => beforeUpload(e, 5)"
                      :customRequest="
                        e =>
                          fileUpload(e, d => {
                            editUserForm.avatar = d.fileID;
                          })
                      "
                      accept="image/png, image/jpeg, image/jpg"
                    >
                      <a-button ref="uploadButton"></a-button>
                    </a-upload>
                  </div>
                </a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.englishName', '英文名')" prop="enName">
                  <a-input v-model.trim="editUserForm.enName" />
                </a-form-model-item>
                <a-form-model-item prop="gender" :label="$t('HeaderUserCenter.gender', '性别')">
                  <a-radio-group button-style="solid" v-model="editUserForm.gender" size="small">
                    <a-radio-button value="MALE">{{ $t('HeaderUserCenter.male', '男') }}</a-radio-button>
                    <a-radio-button value="FEMALE">{{ $t('HeaderUserCenter.female', '女') }}</a-radio-button>
                  </a-radio-group>
                </a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.idNumber', '身份证号码')" prop="idNumber">
                  <a-input v-model.trim="editUserForm.idNumber" />
                </a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.phone', '手机')" prop="ceilPhoneNumber">
                  <a-input v-model.trim="editUserForm.ceilPhoneNumber" />
                </a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.officePhone', '办公电话')">
                  <a-input v-model.trim="editUserForm.businessPhoneNumber" />
                </a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.familyPhone', '家庭电话')">
                  <a-input v-model.trim="editUserForm.familyPhoneNumber" />
                </a-form-model-item>
                <a-form-model-item :label="$t('HeaderUserCenter.email', '邮箱')" prop="mail">
                  <a-input v-model.trim="editUserForm.mail" />
                </a-form-model-item>
                <a-form-model-item label=" ">
                  <a-button type="primary" @click="onClickSaveUserInfo" :icon="saving ? 'loading' : 'save'">
                    {{ $t('HeaderUserCenter.save', '保存') }}
                  </a-button>
                </a-form-model-item>
              </a-form-model>
            </a-tab-pane>
          </a-tabs>
        </a-col>
      </a-row>
    </a-drawer>
  </span>
</template>
<style lang="less">
.user-center-drawer {
  .user-info-card {
    height: 100%;
    background: e(
      'linear-gradient(to bottom, rgba(var(--w-primary-color-rgb), 0.1) 0%, rgba(var(--w-primary-color-rgb), 0) 100%), #ffffff;'
    );
    border-radius: 4px;
    .user-name-avatar {
      padding-top: 57px;
      display: flex;
      flex-direction: column;
      align-items: center;
      > .ant-avatar {
        margin-bottom: 40px;
        border: 2px solid #ffffff;
      }
      > .user-name {
        font-weight: 600;
        font-size: 36px;
        color: #000000;
        line-height: 36px;
        text-align: left;
        font-style: normal;
        text-transform: none;
        margin-bottom: 12px;
      }
    }
  }
  .work-state-select {
    .ant-select-selection {
      border-radius: 16px;
    }
  }
  .basic-info-form {
    .ant-form-item-label {
      > label {
        font-weight: bold;
        font-size: 14px;
        color: #999999;
      }
    }

    .ant-form-item-control {
      padding-left: 40px;
    }
  }
  .user-basic-info-form {
    padding: 0 50px;
    .ant-form-item {
      margin: 0 0 12px;
    }
    .ant-form-item-label {
      line-height: 24px;
      height: 24px;
    }
    .ant-form-item-control {
      line-height: 24px;
      height: 24px;
      > .ant-form-item-children {
        font-weight: bold;
        font-size: 14px;
        color: #333333;
      }
    }
  }

  .work-info-form {
    padding-left: 40px;
    &.edit {
      .ant-form-item {
        display: flex;
        margin: 0 0 24px;
      }
    }

    .ant-form-item {
      display: flex;
      margin: 0 0 12px;
      .ant-form-item-label {
        width: 100px;
        line-height: 32px;
        min-height: 32px;
        > label {
          font-weight: 400;
          font-size: 14px;
          color: #999999;
        }
      }
      .avatar {
        display: inline-block;
        .delete-cover {
          position: absolute;
          width: 80px;
          height: 80px;
          border-radius: 40px;
          top: 0px;
          background: #00000080;
          text-align: center;
          display: none;
          justify-content: center;
          align-items: center;
          > button {
            color: #fff;
          }
        }
        &:hover {
          .delete-cover {
            display: flex;
          }
        }
      }

      .badge-count {
        cursor: pointer;
        &:hover {
          // .ant-badge-count {
          //   background-color: var(--w-primary-color) !important;
          //   color: rgb(255, 255, 255) !important;
          // }
        }
      }

      .ant-form-item-control {
        line-height: 32px;
        min-height: 32px;
        padding-left: 54px;
        .ant-input {
          width: 342px;
        }
        > .ant-form-item-children {
          font-weight: 400;
          color: #333333;
        }
        .director-name {
          margin-left: 4px;
          font-weight: 400;
          font-size: 14px;
          color: var(--w-link-color);
        }
      }
    }
    &.director {
      .ant-form-item-label {
        width: 40px;
      }
      .ant-form-item {
        margin: 0px;
      }
    }

    .user-group-div {
      .ant-tag {
        &:hover {
          background-color: var(--w-primary-color);
          color: #fff;
        }
      }
    }
  }

  .director-popover {
    .ant-popover-inner-content {
      background-color: #fff;
      padding: 4px;
      .director-info-card {
        padding: 20px;
        width: 360px;
        background: e(
          'linear-gradient(to bottom, rgba(var(--w-primary-color-rgb), 0.1) 0%, rgba(var(--w-primary-color-rgb), 0.05) 100%), #ffffff;'
        );
        border-radius: 4px 4px 4px 4px;
        .user-name-state {
          display: flex;
          align-items: center;
          > label {
            font-weight: bold;
            font-size: 20px;
            color: #333333;
          }
        }
        .work-state-tag {
          margin-left: 8px;
          text-align: center;
          width: 64px;
          height: 24px;
          background: var(--w-primary-color);
          border-radius: 12px 12px 12px 12px;
          display: inline-block;
          > label {
            font-weight: 400;
            font-size: 12px;
            color: #ffffff;
            line-height: 12px;
          }
        }
        > form {
          background-color: #fff;
          border-radius: 4px;
          padding: 20px;

          .ant-form-item-label {
            width: 28px;
          }
          .ant-form-item-control {
            padding-left: 20px;
          }
        }
        &.director-info-card-other {
          .director-avatar-name {
            align-items: center;
          }
          .user-name-state {
            flex-direction: column;
            align-items: start;
          }
          .work-state-tag {
            margin-left: 0;
            width: auto;
            padding: 0 7px;
          }
          > form {
            .ant-form-item-label {
              width: 92px;
              text-align: left;
            }
            .ant-form-item-control {
              padding-left: 7px;
            }
          }
        }
      }
    }
  }
}
</style>
<script type="text/babel">
import { fileUpload } from '@framework/vue/utils/util';
export default {
  name: 'HeaderUserCenter',
  inject: ['widgetLayoutContext', 'pageContext'],
  props: {},
  components: {},
  computed: {
    workStateLabelMap() {
      let map = {};
      for (let i = 0, len = this.workStateOption.length; i < len; i++) {
        map[this.workStateOption[i].value] = this.workStateOption[i].label;
      }
      return map;
    }
  },
  data() {
    return {
      visible: false,
      labelCol: { span: 7 },
      wrapperCol: { span: 17 },
      loading: true,
      user: {},
      userGroups: [],
      userIdNameMap: {},
      director: {},
      editUserForm: {},
      rules: {
        mail: {
          pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
          message: this.$t('HeaderUserCenter.mailError', '请输入正确的邮件格式'),
          trigger: ['blur']
        },
        businessPhoneNumber: {
          pattern: /^\d{4}-\d{7}$|^0\d{2,3}-\d{7,8}$/,
          message: this.$t('HeaderUserCenter.businessPhoneError', '请输入正确的办公电话格式'),
          trigger: ['blur']
        },
        familyPhoneNumber: {
          pattern: /^\d{4}-\d{7}$|^0\d{2,3}-\d{7,8}$/,
          message: this.$t('HeaderUserCenter.familyPhoneError', '请输入正确的家庭电话格式'),
          trigger: ['blur']
        },
        ceilPhoneNumber: {
          pattern: /^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/,
          message: this.$t('HeaderUserCenter.ceilPhoneError', '请输入正确的手机号码'),
          trigger: ['blur']
        },
        idNumber: {
          pattern: /^[1-9]\d{5}(19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[Xx\d]$/,
          message: this.$t('HeaderUserCenter.idNumberError', '请输入正确的身份号码'),
          trigger: ['blur']
        }
      },
      saving: false,
      activeTabKey: 'work-info',
      workStateOption: [],
      changingState: false,
      workState: 'OD'
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    openDrawer() {
      this.widgetLayoutContext.getLayoutContent().$el.style.position = 'relative';
      this.$nextTick(() => {
        this.visible = true;
      });
    },
    fileUpload,
    beforeUpload(file, limitSize) {
      return new Promise((resolve, reject) => {
        let isJpgOrPng = ['image/jpeg', 'image/png', 'image/jpg'].includes(file.type);
        if (!isJpgOrPng) {
          this.$message.error(this.$t('HeaderUserCenter.uploadImageFormatError', '只允许上传 jpeg、png 或者 ico 图片格式'));
        }
        let limit = true;
        if (limitSize != undefined) {
          limit = file.size / 1024 / 1024 < limitSize;
          if (!limit) {
            this.$message.error(this.$t('HeaderUserCenter.uploadImageSizeError', { limitSize }, `图片大小应小于 ${limitSize}M`));
          }
        }

        if (isJpgOrPng && limit) {
          resolve(file);
        } else {
          reject();
        }
      });
    },
    onClickSaveUserInfo() {
      this.$refs.editUserForm.validate(valid => {
        if (valid) {
          let formData = JSON.parse(JSON.stringify(this.editUserForm));
          formData.userInfoExts = [];
          // 扩展信息
          for (let attrKey of ['idNumber', 'familyPhoneNumber', 'businessPhoneNumber', 'workLocation']) {
            formData.userInfoExts.push({
              attrKey,
              attrValue: formData[attrKey]
            });
          }
          formData.roleUuids = null;
          this.saving = true;
          formData.userName = undefined; // 特殊处理：置空姓名，避免由于国际化环境修改了用户名
          $axios
            .post('/proxy/api/user/org/save', formData)
            .then(({ data }) => {
              this.saving = false;
              if (data.code == 0) {
                this.$message.success(this.$t('HeaderUserCenter.saveSuccess', '保存成功'));

                this.user.ceilPhoneNumber = formData.ceilPhoneNumber;
                this.user.businessPhoneNumber = formData.businessPhoneNumber;
                this.user.mail = formData.mail;
                this.user.avatar = formData.avatar;
                this.pageContext.emitEvent(`User:AvatarRefresh`);
              }
            })
            .catch(error => {
              this.saving = false;
              this.$message.error(this.$t('HeaderUserCenter.serviceError', '服务异常'));
            });
        }
      });
    },
    onClickUploadAvatar() {
      this.$refs.uploadButton.$el.click();
    },
    getPopupContainer(triggerNode) {
      return document.querySelector('.user-center-drawer');
    },
    onChangeWorkState() {
      this.changingState = true;
      $axios
        .get(`/proxy/api/user/updateUserWorkState`, { params: { workState: this.workState, userId: this.user.userId } })
        .then(({ data }) => {
          this.changingState = false;
          if (data.data) {
            this.$message.success(
              this.$t(
                'HeaderUserCenter.workStateChangeSuccess',
                { state: this.workStateLabelMap[this.workState] },
                `已将状态切换为 ${this.workStateLabelMap[this.workState]}`
              )
            );
          } else {
            this.$message.error(this.$t('HeaderUserCenter.workStateChangeError', '切换状态异常'));
          }
        })
        .catch(error => {
          this.$message.error(this.$t('HeaderUserCenter.workStateChangeError', '切换状态异常'));
        });
    },
    afterVisibleChange(visible) {
      if (visible) {
        this.fetchOrgUserDetail();
        this.fetchOrgUserGroups();
        this.fetchOrgUserWorkStateOption();
        this.widgetLayoutContext.$el.addEventListener('click', this.clickLayoutToCloseDrawer);
      } else {
        this.activeTabKey = 'work-info';
        this.widgetLayoutContext.$el.removeEventListener('click', this.clickLayoutToCloseDrawer);
        this.widgetLayoutContext.getLayoutContent().$el.style.position = 'unset';
      }
    },
    clickLayoutToCloseDrawer(e) {
      if (this.widgetLayoutContext.$refs.wSider.$el.contains(e.target) || this.widgetLayoutContext.$refs.wHeader.$el.contains(e.target)) {
        this.visible = false;
      }
    },

    fetchOrgUserGroups() {
      $axios
        .get(`/proxy/api/user/org/getGroupsIncludeUser`, { params: { userId: this._$USER.userId } })
        .then(({ data }) => {
          if (data.data) {
            this.userGroups = data.data;
          }
        })
        .catch(error => {});
    },
    fetchOrgUserDetail() {
      this.loading = true;
      $axios
        .get(`/proxy/api/user/org/getUserDetailsUnderSystem`, {
          params: {
            userId: this._$USER.userId
          }
        })
        .then(({ data }) => {
          this.loading = false;
          if (data.data) {
            for (let key in data.data) {
              this.$set(this.user, key, data.data[key]);
              this.$set(this.editUserForm, key, data.data[key]);
            }
            if (!this.user.mainJobName && this.user.otherJobNames && this.user.otherJobNames.length > 0) {
              this.user.mainJobName = this.user.otherJobNames[0];
            }
            if (this.user.workState) {
              this.workState = this.user.workState;
            }
            //FIXME: 职务职级为62功能，待改造，暂时通过翻译服务输出文本
            let jobRankDutyNames = [],
              map = {};
            if (this.user.jobRankName) {
              map.jobRankName = this.user.jobRankName;
              jobRankDutyNames.push(this.user.jobRankName);
              this.user.jobRankName = undefined;
            }
            if (this.user.dutyName) {
              map.dutyName = this.user.dutyName;
              jobRankDutyNames.push(this.user.dutyName);
              this.user.dutyName = undefined;
            }
            this.$translate(jobRankDutyNames, 'zh', this.$i18n.locale.split('_')[0])
              .then(result => {
                this.user.jobRankName = result[map.jobRankName];
                this.user.dutyName = result[map.dutyName];
              })
              .catch(error => {});
            if (this.user.directorUserId) {
              $axios
                .get(`/proxy/api/user/org/getUserDetailsUnderSystem`, { params: { userId: this.user.directorUserId } })
                .then(({ data }) => {
                  if (data.data) {
                    this.$set(this, 'director', data.data);
                  }
                })
                .catch(error => {});
            }
            if (this.user.subordinateUsers && this.user.subordinateUsers.length > 0) {
              $axios
                .post(`/proxy/api/user/getUserNamesByIds`, this.user.subordinateUsers)
                .then(({ data }) => {
                  if (data.data) {
                    for (let k in data.data) {
                      this.$set(this.userIdNameMap, k, data.data[k]);
                    }
                  }
                })
                .catch(error => {});
            }
          }
        })
        .catch(error => {
          this.$message.error(this.$t('HeaderUserCenter.userInfoServiceError', '用户信息服务异常'));
          this.loading = false;
        });
    },
    fetchOrgUserWorkStateOption() {
      if (this.workStateOption.length == 0) {
        $axios
          .get(`/proxy/api/datadict/getByCode/PT_ORG_USER_WORK_STATE`, { params: { locale: true } })
          .then(({ data }) => {
            if (data.data) {
              this.workStateOption.push(...data.data.items);
            }
          })
          .catch(error => {});
      }
    },
    getContainer() {
      return this.widgetLayoutContext.getLayoutContent().$el;
    }
  }
};
</script>
