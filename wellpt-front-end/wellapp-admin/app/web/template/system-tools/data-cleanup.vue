<template>
  <div class="clean-up-container">
    <a-alert
      message="可清理平台下的产品、模块数据，流程、表单等设计资源以及实例数据，数据清理后不可恢复，请谨慎操作。"
      banner
      type="info"
    />
    <a-steps :current="step" style="padding: 20px 20px 8px 20px">
      <a-step title="选择清理内容"></a-step>
      <a-step title="确认清理内容" />
      <a-step title="执行数据清理" />
    </a-steps>

    <a-row type="flex">
      <a-col flex="200px" style="padding: 20px">
        <a-anchor v-show="!(step == 2 && cleanupQueue.length == 0)">
          <a-anchor-link href="#appProductModule" title="产品模块" />
          <a-anchor-link href="#designResource" title="设计资源" />
          <a-anchor-link href="#templateData" title="模板数据" />
          <a-anchor-link href="#userData" title="用户数据" />
        </a-anchor>
      </a-col>
      <a-col flex="calc(100% - 200px)">
        <div class="checkbox-all-row" v-show="step == 0">
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

        <Scroll :style="{ height: scrollHeight, padding: ' 12px 20px' }">
          <div v-show="!(step == 2 && cleanupQueue.length == 0)">
            <template v-for="(option, index) in cleanupOptions">
              <div v-show="step == 0 || option.checkOption.checked.length > 0">
                <div class="sub-title" :id="option.key">{{ option.title }}</div>
                <div v-show="step == 0" class="checkbox-all-row">
                  <a-checkbox
                    :indeterminate="option.checkOption.indeterminate"
                    :checked="option.checkOption.checkAll"
                    @change="onCheckAllOptionChange(option.checkOption)"
                  >
                    全选
                  </a-checkbox>
                </div>
                <template v-for="(item, i) in option.options">
                  <a-row
                    type="flex"
                    :key="'option_' + option.key + i"
                    class="list-item-row"
                    v-show="step == 0 || option.checkOption.checked.includes(item.id)"
                  >
                    <a-col v-if="step == 0" flex="40px" style="align-self: center; text-align: center">
                      <a-checkbox
                        :checked="option.checkOption.checked.includes(item.id)"
                        @click="onChangeRadioChecked(e, item.id, option.checkOption)"
                      />
                    </a-col>
                    <a-col
                      flex="calc(100% - 60px - 200px)"
                      @click.native.stop="e => (step == 0 ? onChangeRadioChecked(e, item.id, option.checkOption) : () => {})"
                    >
                      <a-row type="flex" style="flex-wrap: nowrap">
                        <a-col flex="60px" style="margin-right: 12px">
                          <a-avatar
                            shape="square"
                            :size="50"
                            style="background: var(--w-primary-color); display: flex; justify-content: center; align-items: center"
                          >
                            <Icon slot="icon" :type="item.icon || 'folder'" style="font-size: 30px" />
                          </a-avatar>
                        </a-col>
                        <a-col flex="auto" style="display: flex; flex-direction: column; justify-content: center">
                          <label>{{ item.name }}</label>
                          <div style="font-size: 12px; color: #999">{{ item.cleanupOptionText }}</div>
                        </a-col>
                      </a-row>
                    </a-col>
                    <a-col flex="500px">
                      <template v-if="item.cleanupOptions != undefined && item.cleanupOptions.length > 0">
                        <label style="color: #999; margin-bottom: 8px">{{ item.cleanupOptionTitle || '清理选项' }}</label>
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
            </template>
          </div>
        </Scroll>
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

    <div class="footer">
      <a-button :disabled="vCheckedCount == 0" type="primary" @click="handleNextStep(step == 0 ? 1 : -1)" v-if="step != 2">
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
  .sub-title {
    font-weight: bold;
    font-size: 16px;
    line-height: 30px;
  }
  .checkbox-all-row {
    padding: 8px;
  }
  .list-item-row {
    border-radius: 4px;
    flex-wrap: nowrap;
    outline: 1px solid #e8e8e8;
    padding: 12px;
    &:hover {
      background-color: var(--w-primary-color-1);
    }
    &:not(:last-child) {
      margin-bottom: 12px;
    }
  }
  .footer {
    padding: 20px;
    text-align: right;
  }
  .cleanup-result {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }
}
</style>
<script type="text/babel">
import { encode, decode } from 'js-base64';

export default {
  name: 'DataCleanup',
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
    let scrollHeight = 'calc(100vh - 250px)';
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
          key: 'appProductModule',
          title: '产品模块',
          options: [
            {
              id: 'appProduct',
              icon: 'appstore',
              name: '产品',
              cleanupOptionText: '清理产品定义和相关配置数据（不含模块数据）',
              expectCleanupCount: 0,
              expectCleanupCounting: true,
              cleanupState: 0,
              cleanupOptionTitle: '产品状态筛选',
              cleanupOptions: [
                { label: '构建中', value: 'BUILDING' },
                { label: '已上线', value: 'LAUNCH' },
                { label: '已下线', value: 'NOT_LAUNCH' }
              ],
              cleanupCheckedOption: ['BUILDING', 'LAUNCH', 'NOT_LAUNCH']
            },
            {
              id: 'appModule',
              icon: undefined,
              name: '模块',
              cleanupOptionText: '清理模块定义和相关配置数据（不含平台内置模块），同时清除实例数据',
              expectCleanupCount: 0,
              expectCleanupCounting: true,
              cleanupState: 0,
              cleanupOptionTitle: '模块状态筛选',
              cleanupOptions: [
                { label: '上线', value: '1' },
                { label: '下线', value: '0' }
              ],
              cleanupCheckedOption: ['1', '0']
            }
          ],
          checkOption: {
            indeterminate: false,
            checkAll: false,
            checked: [],
            allKeys: ['appProduct', 'appModule']
          }
        },

        {
          key: 'designResource',
          title: '设计资源',
          options: [
            {
              id: 'formDefinition',
              icon: 'profile',
              name: '表单',
              cleanupOptionText: '清理桌面端和移动端表单、子表单定义（不含平台内置模块表单），同时清除实例数据',
              expectCleanupCount: 0,
              expectCleanupCounting: true,
              cleanupState: 0,
              cleanupOptionTitle: '存储对象清理',
              cleanupOptions: [{ label: '同时删除存储对象', value: 'dropDataModel' }],
              cleanupCheckedOption: ['dropDataModel']
            },
            {
              id: 'appPageDefinition',
              icon: 'file',
              name: '页面',
              cleanupOptionText: '清理桌面端和移动端页面定义（不含平台内置模块页面）',
              expectCleanupCount: 0,
              expectCleanupCounting: true,
              cleanupState: 0,
              cleanupOptions: [],
              cleanupCheckedOption: []
            },
            {
              id: 'flowDefinition',
              icon: 'branches',
              name: '流程',
              cleanupOptionText: '清理全部流程定义和流程实例数据（不含平台内置模块流程）',
              expectCleanupCount: 0,
              expectCleanupCounting: true,
              cleanupState: 0,
              cleanupOptions: [],
              cleanupCheckedOption: []
            },
            {
              id: 'themePack',
              icon: 'skin',
              name: '主题',
              cleanupOptionText: '清理全部桌面端和移动端的主题设计数据',
              expectCleanupCount: 0,
              expectCleanupCounting: true,
              cleanupState: 0,
              cleanupOptionTitle: '主题规范选项',
              cleanupOptions: [{ label: '清空主题规范的版本记录并重置版本号', value: 'clearThemeSpecification' }],
              cleanupCheckedOption: ['clearThemeSpecification']
            },
            {
              id: 'rolePrivilegeResource',
              icon: 'safety',
              name: '角色 / 权限 / 资源',
              cleanupOptionText: '清理角色、权限、资源的配置数据（不含平台内置模块数据）',
              expectCleanupCount: 0,
              expectCleanupCounting: true,
              cleanupState: 0,
              cleanupOptions: [
                { label: '角色', value: 'role' },
                { label: '权限', value: 'privilege' },
                { label: '资源', value: 'resource' }
              ],
              cleanupCheckedOption: ['role', 'privilege', 'resource']
            },
            {
              id: 'basicData',
              icon: 'project',
              name: '元数据',
              cleanupOptionText: '清理所选的元数据定义（不含平台内置模块数据）',
              expectCleanupCount: 0,
              expectCleanupCounting: true,
              cleanupState: 0,
              cleanupOptions: [
                { label: '数据字典', value: 'cd_data_dictionary' },
                { label: '消息格式', value: 'msg_message_template' },
                { label: '打印模板', value: 'cd_print_template' },
                { label: '流水号规则', value: 'cd_serial_number' },
                { label: '数据仓库', value: 'cd_data_store_definition' },
                { label: '计时服务', value: 'ts_timer_config' },
                { label: '工作时间方案', value: 'ts_work_time_plan' },
                { label: '节假日', value: 'ts_holiday' }
              ],
              cleanupCheckedOption: [
                'cd_data_dictionary',
                'msg_message_template',
                'cd_print_template',
                'cd_serial_number',
                'cd_data_store_definition',
                'ts_timer_config',
                'ts_work_time_plan',
                'ts_holiday'
              ]
            }
          ],
          checkOption: {
            indeterminate: false,
            checkAll: false,
            checked: [],
            allKeys: ['formDefinition', 'appPageDefinition', 'flowDefinition', 'themePack', 'rolePrivilegeResource', 'basicData']
          }
        },
        {
          key: 'templateData',
          title: '模板数据',
          options: [
            {
              id: 'dyformDesignTemplate',
              icon: 'profile',
              name: '表单模板',
              cleanupOptionText: '清理表单设计模板',
              expectCleanupCount: 0,
              expectCleanupCounting: true,
              cleanupState: 0,
              cleanupOptions: [{ label: '清理用户自定义模板', value: 'clearUserDesign' }],
              cleanupCheckedOption: ['clearUserDesign']
            },
            {
              id: 'appPageDesignTemplate',
              icon: 'profile',
              name: '页面模板',
              cleanupOptionText: '清理页面设计模板',
              expectCleanupCount: 0,
              expectCleanupCounting: true,
              cleanupState: 0,
              cleanupOptions: [{ label: '清理用户自定义模板', value: 'clearUserDesign' }],
              cleanupCheckedOption: ['clearUserDesign']
            }
          ],
          checkOption: {
            indeterminate: false,
            checkAll: false,
            checked: [],
            allKeys: ['dyformDesignTemplate', 'appPageDesignTemplate']
          }
        },

        {
          key: 'userData',
          title: '用户',
          options: [
            {
              id: 'platformUserAccount',
              icon: 'user',
              name: '用户',
              cleanupOptionText: '清理平台用户及相关数据，如开发人员',
              expectCleanupCount: 0,
              expectCleanupCounting: true,
              cleanupState: 0,
              cleanupOptions: [
                { label: '用户偏好', value: 'userPreference' },
                { label: '用户邮箱/邮件', value: 'userMailbox' },
                { label: '用户消息', value: 'userMessage' },
                { label: '用户文件夹', value: 'userFile' }
              ],
              cleanupCheckedOption: ['userPreference', 'userMailbox', 'userMessage', 'userFile']
            }
          ],
          checkOption: {
            indeterminate: false,
            checkAll: false,
            checked: [],
            allKeys: ['platformUserAccount']
          }
        }
      ],
      loading: false,
      headCheckAll: {
        indeterminate: false,
        checkAll: false,
        checked: []
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
    if (this.containerStyle != undefined && this.containerStyle.height != undefined) {
      let height = this.containerStyle.height;
      if (height.indexOf('calc(') == 0) {
        let _temp = height.substring(height.indexOf('calc(') + 5, height.length - 1).trim();
        this.scrollHeight = `calc(${_temp} - 270px)`;
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
                      if (opt.id == 'user') {
                        secondPromise.push(
                          _this.doCleanupData(opt.id, opt, {
                            clearTypes: opt.cleanupCheckedOption
                          })
                        );
                      } else {
                        promise.push(
                          _this.doCleanupData(opt.id, opt, {
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
            this.fetchExpectCleanupRows(opt.id, opt, {
              clearTypes: opt.cleanupCheckedOption
            });
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
          .post(`/proxy/api/cleanupData/cleanup/${type}`, Object.assign({}, params), {
            cancelToken: source.token
          })
          .then(({ data }) => {
            this.cleanupQueue.splice(0, 1);
            if (data.code == 0) {
              this.cleanResult.total += option.expectCleanupCount;
              option.cleanupState = 2;
              if (type == 'themePack') {
                // 删除主题缓存
                $axios
                  .get(`/api/cache/deleteByPattern`, { params: { pattern: `THEME_DESIGN_CLASS_CSS:*` } })
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
        .post(`/proxy/api/cleanupData/expectCleanupRows/${type}`, Object.assign({}, params))
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
    }
  },
  watch: {},
  META: {}
};
</script>
