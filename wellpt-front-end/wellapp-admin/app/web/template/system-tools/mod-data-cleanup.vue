<template>
  <div class="clean-up-container">
    <a-alert
      message="可清理系统下的模块使用数据以及组织用户相关, 数据清理后不可恢复, 请谨慎操作。"
      type="info"
      style="margin-bottom: 12px"
    />
    <a-steps :current="step" class="pt-steps" style="--w-pt-steps-width: 680px; margin-bottom: 12px; border: none">
      <a-step title="选择清理内容"></a-step>
      <a-step title="确认清理内容" />
      <a-step title="执行数据清理" />
    </a-steps>
    <div v-if="loading" class="spin-center">
      <a-spin />
    </div>

    <a-row type="flex" v-else v-show="step !== 2" class="pt-row" style="background-color: #ffffff; border: none">
      <a-col flex="224px" style="padding-top: 12px">
        <a-anchor v-show="!(step == 2 && cleanupQueue.length == 0)" class="clean-up-anchor">
          <a-anchor-link href="#appModuleRuntimeData" title="模块数据" />
          <a-anchor-link href="#orgUser" title="组织用户" />
        </a-anchor>
      </a-col>
      <a-col flex="auto" style="width: 0">
        <div class="checkbox-all-row" v-show="step == 0" style="padding: 12px 20px 12px">
          <a-checkbox
            :indeterminate="headCheckAll.indeterminate"
            :checked="headCheckAll.checkAll"
            @change="onCheckAllOptionChange(undefined)"
          >
            全选
          </a-checkbox>
          <a-divider type="vertical" />
          <label style="color: #999" v-if="!headCheckAll.checkAll && !headCheckAll.indeterminate">请选择需要清理的内容</label>
          <label style="color: var(--w-primary-color)" v-else>已选 ({{ vCheckedCount }})</label>
        </div>
        <a-alert banner v-if="step == 1" type="info">
          <template slot="message">
            请确认清理内容, 共计
            <a-button size="small" type="link">{{ expectCleanupCount }}</a-button>
            行数据
          </template>
        </a-alert>
        <a-alert message="清理中" banner v-if="step == 2 && cleanupQueue.length > 0">
          <template slot="closeText">
            <a-button type="primary" size="small" @click="cancelCleanup">取消</a-button>
          </template>
        </a-alert>

        <Scroll :style="{ height: scrollHeight, padding: '0px 20px 12px' }">
          <div v-show="!(step == 2 && cleanupQueue.length == 0)">
            <template v-for="(option, index) in cleanupOptions">
              <div v-show="step == 0 || option.checkOption.checked.length > 0" class="clean-up-options">
                <div class="sub-title" :id="option.key">
                  <a-checkbox
                    v-show="step == 0"
                    class="checkbox-all-row"
                    :indeterminate="option.checkOption.indeterminate"
                    :checked="option.checkOption.checkAll"
                    @change="onCheckAllOptionChange(option.checkOption)"
                  ></a-checkbox>
                  {{ option.title }}
                </div>

                <div>
                  <template v-for="(item, i) in option.options">
                    <a-row
                      type="flex"
                      :key="'option_' + option.key + i"
                      class="list-item-row"
                      v-show="step == 0 || option.checkOption.checked.includes(item.id)"
                    >
                      <a-col v-if="step == 0" flex="40px" style="text-align: center; margin-top: 8px">
                        <a-checkbox
                          :checked="option.checkOption.checked.includes(item.id)"
                          @click="onChangeRadioChecked(e, item.id, option.checkOption)"
                        />
                      </a-col>
                      <a-col
                        flex="calc(100% - 60px - 200px)"
                        @click.native.stop="e => (step == 0 ? onChangeRadioChecked(e, item.id, option.checkOption) : () => {})"
                      >
                        <a-row type="flex" style="flex-wrap: nowrap; align-items: flex-start">
                          <a-col flex="40px" style="margin-right: 8px">
                            <a-avatar
                              shape="square"
                              :size="36"
                              :style="{
                                background: item.bgColor
                                  ? item.bgColor.startsWith('--')
                                    ? `var(${item.bgColor})`
                                    : item.bgColor
                                  : 'var(--w-primary-color)'
                              }"
                            >
                              <Icon slot="icon" :type="item.icon || option.icon" style="font-size: 18px" />
                            </a-avatar>
                          </a-col>
                          <a-col flex="auto" style="display: flex; flex-direction: column; justify-content: center">
                            <label style="font-weight: bold">{{ item.name }}</label>
                            <div style="font-size: 12px; color: #999">{{ item.cleanupOptionText }}</div>
                          </a-col>
                        </a-row>
                      </a-col>
                      <a-col flex="500px" style="padding-left: 8px">
                        <template v-if="item.cleanupOptions != undefined">
                          <label>清理选项</label>
                          <div>
                            <a-checkbox-group
                              v-if="Array.isArray(item.cleanupCheckedOption)"
                              :disabled="step > 0"
                              v-model="item.cleanupCheckedOption"
                              :options="item.cleanupOptions"
                            />
                            <a-radio-group
                              v-else
                              :disabled="step > 0"
                              :name="'item-' + option.key + '-' + i"
                              v-model="item.cleanupCheckedOption"
                              :options="item.cleanupOptions"
                            />
                          </div>
                        </template>
                      </a-col>
                      <a-col flex="100px" v-if="step > 0">
                        <label style="color: #999; margin-bottom: 8px">数据量</label>
                        <div>
                          <a-button size="small" type="link" :icon="item.expectCleanupCounting ? 'loading' : undefined">
                            <template v-if="!item.expectCleanupCounting">
                              {{ item.expectCleanupCount }}
                            </template>
                          </a-button>
                        </div>
                      </a-col>
                      <a-col flex="100px" v-if="step == 2">
                        <a-button size="small" type="link" icon="loading" v-if="item.cleanupState == 1">清理中...</a-button>
                        <a-button size="small" type="link" icon="check-circle" v-else-if="item.cleanupState == 2">已完成</a-button>
                        <label v-else-if="item.cleanupState == 3">异常</label>
                      </a-col>
                    </a-row>
                  </template>
                </div>
              </div>
            </template>
          </div>
        </Scroll>

        <div class="footer">
          <a-button
            :disabled="vCheckedCount == 0 || system == undefined"
            type="primary"
            @click="handleNextStep(step == 0 ? 1 : -1)"
            v-if="step != 2"
          >
            {{ step == 0 ? '下一步' : '上一步' }}
          </a-button>
          <a-button
            :disabled="expectCleanupCount == 0"
            :icon="expectCounting.length == 0 ? 'delete' : undefined"
            :loading="expectCounting.length > 0 || cleanupQueue.length > 0"
            type="primary"
            @click="confirmPassword.visible = true"
            v-show="step == 1"
          >
            确定清理
          </a-button>
        </div>
      </a-col>
    </a-row>

    <a-result status="success" title="数据清理完成" v-if="step == 2 && cleanupQueue.length == 0" class="cleanup-result">
      <template slot="subTitle">
        已成功清理数据
        <label style="color: var(--w-success-color)">{{ cleanResult.total }}</label>
        条，失败
        <label style="color: var(--w-danger-color)">{{ cleanResult.fail }}</label>
        条
      </template>
      <template #extra>
        <a-button @click="reset">返回</a-button>
      </template>
    </a-result>

    <a-modal title="身份验证" :visible="confirmPassword.visible" @ok="confirmOkCleanup" @cancel="confirmPassword.visible = false">
      <a-form-model ref="passwordForm" :rules="{ value: { required: true, message: '请输入密码' } }" :model="confirmPassword">
        <a-form-model-item label="请输入登录密码进行验证" prop="value">
          <a-input-password v-model.trim="confirmPassword.value" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>
<style lang="less">
.clean-up-container {
  position: relative;
  background-color: var(--w-widget-page-layout-bg-color);
  .sub-title {
    font-weight: bold;
    font-size: 16px;
    line-height: 30px;
  }
  .checkbox-all-row {
    padding: 0;
  }
  .list-item-row {
    background-color: #ffffff;
    flex-wrap: nowrap;
    border-right: 1px solid var(--w-border-color-light);
    border-left: 1px solid var(--w-border-color-light);
    border-bottom: 1px solid var(--w-border-color-light);
    padding: 12px;
    &:hover {
      background-color: var(--w-primary-color-1);
    }
    &:last-child {
      border-bottom-left-radius: 4px;
      border-bottom-right-radius: 4px;
    }
    &:first-child {
      border-top: 1px solid var(--w-border-color-light);
      border-top-left-radius: 4px;
      border-top-right-radius: 4px;
    }
  }
  .footer {
    padding: 20px;
    text-align: center;
    background-color: #ffffff;
    border-top: 1px solid var(--w-border-color-light);
    box-shadow: 0px -2px 4px 0px rgba(0, 0, 0, 0.06);
  }
  .cleanup-result {
    background-color: #ffffff;
  }
  .clean-up-anchor {
    .ant-anchor-link {
      padding: 12px;
    }
    .ant-anchor-ink {
      right: 0;
      &::before {
        content: none;
      }
      .ant-anchor-ink-ball {
        left: unset;
        right: -1px;
        width: 2px;
        background-color: var(--w-primary-color);
        border: 0;
        height: e('calc(var(--w-font-size-base) + 7px)');
        border-radius: 0;
        margin-top: e('calc(0px - var(--w-font-size-base) / 2)');
        z-index: 1;
      }
    }
  }
  .clean-up-options {
    background-color: var(--w-gray-color-2);
    border-radius: 4px;
    padding: 4px 20px 20px;
    margin-bottom: 12px;
  }
}
</style>
<script type="text/babel">
import { encode, decode } from 'js-base64';

export default {
  name: 'ModDataCleanup',
  props: {},
  components: {},
  inject: ['containerStyle'],
  computed: {
    vCheckedCount() {
      let count = 0;
      this.cleanupOptions.forEach(item => {
        count += item.checkOption.checked.length;
      });
      return count;
    }
  },
  data() {
    let orgUserOption = [
      {
        id: 'organization',
        icon: 'iconfont icon-ptkj-zuzhi',
        bgColor: 'var(--w-success-color)',
        name: '组织',
        cleanupOptionText: '清理当前系统下的全部组织定义和组织配置数据',
        expectCleanupCount: 0,
        expectCleanupCounting: true,
        cleanupState: 0
      },

      {
        id: 'unit',
        icon: 'iconfont icon-ptkj-danwei-01',
        bgColor: undefined,
        name: '单位',
        cleanupOptionText: '清理当前系统下定义的全部单位定义',
        expectCleanupCount: 0,
        expectCleanupCounting: true,
        cleanupState: 0
      },

      {
        id: 'orgGroup',
        icon: 'iconfont icon-ptkj-qunzu',
        bgColor: 'var(--w-warning-color)',
        name: '群组',
        cleanupOptionText: '清理当前系统下定义的群组',
        cleanupOptions: [
          { label: '公共群组', value: 'publicGroup' }
          // { label: '个人群组', value: 'privateGroup' }
        ],
        cleanupCheckedOption: ['publicGroup', 'privateGroup'],
        expectCleanupCount: 0,
        expectCleanupCounting: true,
        cleanupState: 0
      },

      {
        id: 'user',
        icon: 'iconfont icon-luojizujian-huoqudangqianyonghu',
        bgColor: undefined,
        name: '用户',
        cleanupOptionText: '删除当前系统创建的全部用户或者移除全部用户',
        cleanupOptions: [
          { label: '移除全部用户（将用户从当前系统移除但未删除用户）', value: 'removeFromSystem' },
          { label: '删除用户', value: 'removeUser' }
        ],
        cleanupCheckedOption: 'removeFromSystem',
        expectCleanupCount: 0,
        expectCleanupCounting: true,
        cleanupState: 0
      },

      {
        id: 'otherUserData',
        icon: 'iconfont icon-luojizujian-huoquyonghuliebiao',
        bgColor: undefined,
        name: '用户关联数据',
        cleanupOptionText: '清理用户的相关数据',
        cleanupOptions: [
          { label: '用户偏好', value: 'userPreference' },
          { label: '用户邮箱/邮件', value: 'userMailbox' },
          { label: '用户消息', value: 'userMessage' },
          { label: '用户文件夹/文件', value: 'userFile' },
          { label: '用户群组', value: 'userGroup' },
          { label: '用户操作日志', value: 'userOperationLog' }
        ],
        cleanupCheckedOption: ['userPreference', 'userMailbox', 'userMessage', 'userFile', 'userGroup', 'userOperationLog'],
        expectCleanupCount: 0,
        expectCleanupCounting: true,
        cleanupState: 0
      }
    ];
    let orgUserKeys = [];
    orgUserOption.forEach(i => {
      orgUserKeys.push(i.id);
    });
    let scrollHeight = 'calc(100vh - 262px)';
    return {
      scrollHeight,
      cleanupQueue: [],
      expectCounting: [],
      step: 0,
      confirmPassword: {
        value: undefined,
        visible: false
      },
      cleanupOptions: [
        {
          key: 'appModuleRuntimeData',
          title: '模块数据',
          icon: 'iconfont icon-ptkj-zaitechengjian',
          options: [],
          checkOption: {
            indeterminate: false,
            checkAll: false,
            checked: [],
            allKeys: []
          }
        },
        {
          key: 'orgUser',
          title: '组织用户',
          icon: 'iconfont icon-ptkj-xiangmuku-01',
          options: orgUserOption,
          checkOption: {
            indeterminate: false,
            checkAll: false,
            checked: [],
            allKeys: orgUserKeys
          }
        }
      ],
      loading: true,
      system: this._$SYSTEM_ID,
      headCheckAll: {
        indeterminate: false,
        checkAll: false,
        checked: [],
        allKeys: [...orgUserKeys]
      },
      cleanResult: {
        total: 0,
        fail: 0
      },
      expectCleanupCount: 0
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchSystemModules();

    if (this.containerStyle != undefined && this.containerStyle.height != undefined) {
      let height = this.containerStyle.height;
      if (height.indexOf('calc(') == 0) {
        let _temp = height.substring(height.indexOf('calc(') + 5, height.length - 1).trim();
        this.scrollHeight = `calc(${_temp} - 282px)`;
      }
    }
  },
  mounted() {},
  methods: {
    reset() {
      this.step = 0;
      this.expectCleanupCount = 0;
      this.headCheckAll.checkAll = false;
      this.headCheckAll.indeterminate = false;
      this.cleanupOptions.forEach(cleanup => {
        let { checkOption, options } = cleanup;
        checkOption.checked.splice(0, checkOption.checked.length);
        checkOption.checkAll = false;
        checkOption.indeterminate = false;
        // options.forEach(opt=>{
        //   if(Array.isArray(opt.cleanupCheckedOption)){
        //     opt.cleanupCheckedOption.splice(0,opt.cleanupCheckedOption)
        //   }
        //   opt.cleanupCheckedOption
        // })
      });
    },
    cancelCleanup() {
      this.cleanupQueue.forEach(item => {
        item.cancel('停止清理');
      });
      this.cleanupQueue.splice(0, this.cleanupQueue.length);
    },
    confirmOkCleanup() {
      let _this = this;
      this.$refs.passwordForm.validate((vali, msg) => {
        if (vali) {
          $axios
            .get(`/proxy/api/user/checkPassword`, {
              params: {
                password: encode(_this.confirmPassword.value)
              }
            })
            .then(({ data }) => {
              if (data.data) {
                _this.confirmPassword.visible = false;
                _this.step = 2;
                _this.cleanupQueue.splice(0, _this.cleanupQueue.length);
                _this.confirmPassword.value = undefined;
                _this.cleanResult.total = 0;
                _this.cleanResult.fail = 0;
                let promise = [],
                  secondPromise = [];
                _this.cleanupOptions.forEach(cleanup => {
                  let { options, checkOption, key } = cleanup;
                  options.forEach(opt => {
                    if (checkOption.checked.includes(opt.id)) {
                      if (opt.id == 'user' || ['pt-webmail', 'pt-message', 'pt-biz-process'].includes(opt.id)) {
                        secondPromise.push(
                          _this.doCleanupData(opt.id, opt, {
                            id: opt.id,
                            clearTypes: opt.cleanupCheckedOption
                          })
                        );
                      } else {
                        promise.push(
                          _this.doCleanupData(key == 'appModuleRuntimeData' ? key : opt.id, opt, {
                            id: opt.id,
                            clearTypes: opt.cleanupCheckedOption
                          })
                        );
                      }
                    }
                  });
                });
                if (promise.length) {
                  Promise.all(promise).then(() => {
                    Promise.all(secondPromise);
                  });
                } else {
                  Promise.all(secondPromise);
                }
              } else {
                _this.$message.error('密码错误');
                _this.confirmPassword.value = undefined;
              }
            });
        }
      });
    },

    handleNextStep(i) {
      this.step += i;
      if (this.step == 1) {
        this.expectCleanupCount = 0;
        this.expectCleanupRows();
      }
    },
    expectCleanupRows() {
      this.expectCounting.splice(0, this.expectCounting.length);
      this.cleanupOptions.forEach(cleanup => {
        let { options, checkOption, key } = cleanup;
        options.forEach(opt => {
          if (checkOption.checked.includes(opt.id)) {
            this.expectCounting.push(opt.id);
            this.fetchExpectCleanupRows(
              key == 'appModuleRuntimeData' ? (['pt-webmail', 'pt-message', 'pt-biz-process'].includes(opt.id) ? opt.id : key) : opt.id,
              opt,
              {
                id: opt.id,
                clearTypes: opt.cleanupCheckedOption
              }
            );
          }
        });
      });
    },
    doCleanupData(type, option, params) {
      return new Promise((resolve, reject) => {
        option.cleanupState = 1; // 清理中
        let source = $axios.CancelToken.source();
        this.cleanupQueue.push(source);

        $axios
          .post(`/proxy/api/cleanupData/cleanup/${type}`, Object.assign({ system: this._$SYSTEM_ID }, params), {
            cancelToken: source.token
          })
          .then(({ data }) => {
            this.cleanupQueue.splice(0, 1);
            if (data.code == 0) {
              this.cleanResult.total += option.expectCleanupCount;
              option.cleanupState = 2;
              if (type == 'otherUserData' && params != undefined && params.clearTypes.includes('userPreference')) {
                // 清理用户偏好前端缓存
                $axios
                  .get(`/api/cache/deleteByPattern`, { params: { pattern: `user:preference:${this._$SYSTEM_ID}:*` } })
                  .then(({ data }) => {})
                  .catch(error => {});
              }
              resolve();
            } else {
              this.cleanResult.fail += option.expectCleanupCount;
              option.cleanupState = 3;
            }
          })
          .catch(error => {
            option.cleanupState = $axios.isCancel(error) ? 4 : 3;
          });
      });
    },
    fetchExpectCleanupRows(type, option, params) {
      option.expectCleanupCounting = true;
      $axios
        .post(`/proxy/api/cleanupData/expectCleanupRows/${type}`, Object.assign({ system: this._$SYSTEM_ID }, params))
        .then(({ data }) => {
          option.expectCleanupCounting = false;
          this.expectCounting.splice(this.expectCounting.indexOf(option.id), 1);
          if (data.code == 0) {
            option.expectCleanupCount = data.data ? data.data.total : 0;
            this.expectCleanupCount += Number(data.data.total || 0);
          }
        })
        .catch(error => {});
    },

    onChangeRadioChecked(e, id, checkOption) {
      let idx = checkOption.checked.indexOf(id);
      if (idx == -1) {
        checkOption.checked.push(id);
      } else {
        checkOption.checked.splice(idx, 1);
      }
      this.onChangeIndeterminate(checkOption);
    },
    onChangeIndeterminate(checkOption) {
      if (checkOption != undefined) {
        checkOption.checkAll = checkOption.allKeys.length > 0 && checkOption.allKeys.length == checkOption.checked.length;
        checkOption.indeterminate = checkOption.checked.length > 0 && checkOption.allKeys.length > checkOption.checked.length;
      }
      let checkAll = 0,
        indeterminate = 0;
      for (let i = 0, len = this.cleanupOptions.length; i < len; i++) {
        let checkOption = this.cleanupOptions[i].checkOption;
        checkAll += checkOption.checkAll ? 1 : 0;
        indeterminate += checkOption.indeterminate ? 1 : 0;
      }

      this.headCheckAll.checkAll = this.cleanupOptions.length == checkAll;
      this.headCheckAll.indeterminate = indeterminate > 0 || (checkAll > 0 && checkAll < this.cleanupOptions.length);
    },
    onCheckAllOptionChange(checkOption) {
      if (checkOption != undefined) {
        checkOption.checkAll = !checkOption.checkAll;
        checkOption.indeterminate = false;
        checkOption.checked.splice(0, checkOption.checked.length);
        if (checkOption.checkAll) {
          checkOption.checked.push(...checkOption.allKeys);
        }
        this.onChangeIndeterminate();
      } else {
        // 头部全选操作
        this.headCheckAll.checkAll = !this.headCheckAll.checkAll;
        this.headCheckAll.indeterminate = false;
        for (let i = 0, len = this.cleanupOptions.length; i < len; i++) {
          let checkOption = this.cleanupOptions[i].checkOption;
          checkOption.checkAll = this.headCheckAll.checkAll;
          checkOption.indeterminate = false;
          checkOption.checked.splice(0, checkOption.checked.length);
          if (checkOption.checkAll) {
            checkOption.checked.push(...checkOption.allKeys);
          }
        }
      }
    },
    onCheckAllChange(key) {
      this.checkAllOptions[key].checkAll = !this.checkAllOptions[key].checkAll;
      this.checkAllOptions[key].indeterminate = false;
      this.checkAllOptions[key].checked.splice(0, this.checkAllOptions[key].checked.length);
      if (this.checkAllOptions[key].checkAll) {
        this.checkAllOptions[key].checked.push(...this.checkAllOptions[key].allKeys);
      }
    },
    fetchSystemModules() {
      // 查询系统下的模块
      if (this._$SYSTEM_ID) {
        $axios
          .get(`/proxy/api/system/queryAppSystemModules`, {
            params: {
              system: this._$SYSTEM_ID
            }
          })
          .then(({ data }) => {
            this.loading = false;
            if (data.data) {
              for (let i = 0, len = data.data.length; i < len; i++) {
                let d = data.data[i];
                if (['pt-webmail', 'pt-message', 'pt-biz-process'].includes(d.id)) {
                  continue;
                }
                d.cleanupOptionText = '清理模块的使用数据, 包括表单数据、流程实例数据、文件夹和文件等';
                d.cleanupCheckedOption = ['dyformData', 'flowInstanceData', 'dmsFileData', 'dyformFixedData'];
                d.cleanupOptions = [
                  { label: '表单数据', value: 'dyformData' },
                  { label: '流程实例', value: 'flowInstanceData' },
                  { label: '文件夹/文件', value: 'dmsFileData' },
                  { label: '内置表单数据', value: 'dyformFixedData' }
                ];
                d.expectCleanupCount = 0;
                d.expectCleanupCounting = true;
                d.cleanupState = 0;
                d.iconJson = this.iconDataToJson(d.icon);
                d.icon = d.iconJson.icon;
                d.bgColor = d.iconJson.bgColor;
                this.cleanupOptions[0].options.push(d);
                this.cleanupOptions[0].checkOption.allKeys.push(d.id);
              }
              this.fetchCommonModuleOptions();
            }
          })
          .catch(error => {});
      } else {
        this.loading = false;
      }
    },
    fetchCommonModuleOptions() {
      this.cleanupOptions[0].options.push(
        {
          id: 'pt-webmail',
          icon: undefined,
          bgColor: undefined,
          name: '邮件',
          cleanupOptionText: '清理邮件的使用数据',
          cleanupOptions: [
            { label: '邮件', value: 'mailbox' },
            { label: '文件夹', value: 'mailFolder' },
            { label: '联系人分组', value: 'mailContactGroup' },
            { label: '联系人', value: 'mailContact' },
            { label: '自定义标签', value: 'mailTag' }
          ],
          cleanupCheckedOption: ['mailbox', 'mailFolder', 'mailContactGroup', 'mailContact', 'mailTag'],
          expectCleanupCount: 0,
          expectCleanupCounting: true,
          cleanupState: 0
        },
        {
          id: 'pt-message',
          icon: undefined,
          bgColor: undefined,
          name: '消息',
          cleanupOptionText: '清理消息的数据',
          cleanupOptions: [
            { label: '系统消息', value: 'systemMessage' },
            { label: '用户消息', value: 'userMessage' }
          ],
          cleanupCheckedOption: ['systemMessage', 'userMessage'],
          expectCleanupCount: 0,
          expectCleanupCounting: true,
          cleanupState: 0
        },
        {
          id: 'pt-biz-process',
          icon: undefined,
          bgColor: undefined,
          name: '业务流程',
          cleanupOptionText: '清理业务流程的数据',
          cleanupOptions: [
            { label: '业务流程定义', value: 'bizProcessDefinition' },
            { label: '业务流程实例', value: 'bizProcessInstance' },
            { label: '业务事项源', value: 'bizItemDefinitin' },
            { label: '业务标签', value: 'bizTag' }
          ],
          cleanupCheckedOption: ['bizProcessInstance'],
          expectCleanupCount: 0,
          expectCleanupCounting: true,
          cleanupState: 0
        }
      );
      this.cleanupOptions[0].checkOption.allKeys.push('pt-webmail', 'pt-message', 'pt-biz-process');
    },
    iconDataToJson(data) {
      if (!data) {
        data = {
          icon: '',
          bgColor: ''
        };
      } else {
        try {
          if (typeof data == 'string') {
            let iconJson = JSON.parse(data);
            if (iconJson) {
              data = iconJson;
            }
          }
        } catch (e) {
          if (typeof data == 'string') {
            let iconJson = {
              icon: data,
              bgColor: ''
            };
            data = iconJson;
          }
          return data;
        }
      }
      return data;
    }
  },
  watch: {},
  META: {}
};
</script>
