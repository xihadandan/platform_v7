<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <div v-if="isReadonly" ref="maskDiv" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 99"></div>
    <a-form-model ref="form" :model="formData" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-model-item label="委托人" prop="consignor">
        <OrgSelect
          v-model="formData.consignor"
          :readonly="!isAdmin"
          :multiSelect="false"
          :checkableTypes="['user']"
          style="width: 85%"
          @change="consignorChange"
        />
      </a-form-model-item>
      <a-form-model-item label="受托人" prop="trustee">
        <OrgSelect
          v-if="trusteeScope == 'all' || isAdmin"
          key="all"
          v-model="formData.trustee"
          :multiSelect="false"
          :checkableTypes="['user']"
          style="width: 85%"
          @change="trusteeChange"
        />
        <OrgSelect
          v-else
          key="consignorDept"
          v-model="formData.trustee"
          :multiSelect="false"
          :checkableTypes="['user']"
          orgType="MyDept"
          style="width: 85%"
          @change="trusteeChange"
        />
      </a-form-model-item>
      <a-form-model-item label="委托内容" prop="content">
        <a-radio-group size="small" v-model="formData.contentConfig.type" @change="contentTypeChange">
          <a-radio-button value="all">全部流程</a-radio-button>
          <a-radio-button value="flow">指定流程</a-radio-button>
          <a-radio-button value="task">指定环节</a-radio-button>
        </a-radio-group>
        <!-- <a-tree-select
          v-model="formData.content"
          style="width: 100%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="contentTreeData"
          :replaceFields="{ title: 'name', key: 'data', value: 'data' }"
          tree-checkable
          :treeCheckStrictly="true"
          placeholder="请选择"
        ></a-tree-select> -->
        <div v-if="formData.contentConfig.type == 'all'">*委托人有参与权限的全部流程将作为委托内容</div>
        <div v-if="formData.contentConfig.type == 'flow'">
          <FlowSelect
            :key="flowSelectKey"
            mode="list"
            v-model="formData.contentConfig.values"
            showRecentUsed
            categoryDataAsId
            style="width: 85%"
            @change="flowSelectChange"
            @keyNotFound="flowSelectKeyNotFound"
          ></FlowSelect>
          <br />
          *选择具体流程作为委托内容
        </div>
        <div v-if="formData.contentConfig.type == 'task'">
          <FlowSelect
            title="选择环节"
            :key="taskSelectKey"
            mode="list"
            v-model="formData.contentConfig.values"
            checkType="task"
            style="width: 85%"
            @change="flowSelectChange"
            @keyNotFound="flowSelectKeyNotFound"
          ></FlowSelect>
          <br />
          *选择指定流程的某些环节进行委托
        </div>
        委托
        <a-select mode="multiple" v-model="formData.contentConfig.jobIdentities" allow-clear placeholder="全部身份" style="width: 400px">
          <a-select-option v-for="item in jobIdentityOptions" :key="item.value">
            <span style="width: 80px; display: inline-block">
              <a-tag v-if="item.tagName">{{ item.tagName }}</a-tag>
            </span>
            <span style="width: 100px; display: inline-block">
              {{ item.label }}
            </span>
            <span
              v-if="item.path"
              :title="item.path"
              style="width: 120px; text-align: left; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; float: right"
            >
              {{ item.path }}
            </span>
          </a-select-option>
        </a-select>
        的待办流程
        <br />
        <a-checkbox v-model="formData.includeCurrentWork">包括待办中的流程</a-checkbox>
        <br />
        <a v-if="!formData.conditionConfig" @click="onSetConditionClick">
          <Icon type="iconfont icon-ptkj-shaixuan"></Icon>
          设置委托条件
        </a>
      </a-form-model-item>
      <a-form-model-item v-if="formData.conditionConfig" label="委托条件" prop="conditionConfig">
        <WorkflowDelegationConditionConfiguration
          :formData="formData"
          :configuration="formData.conditionConfig"
        ></WorkflowDelegationConditionConfiguration>
      </a-form-model-item>
      <a-form-model-item label="委托时间" prop="fromTime">
        <a-range-picker
          v-model="formData.delegationTimes"
          show-time
          format="YYYY-MM-DD HH:mm"
          valueFormat="YYYY-MM-DD HH:mm:ss"
          :disabledDate="getDisabledDate"
        ></a-range-picker>
      </a-form-model-item>
      <a-form-model-item label="其他设置">
        <a-checkbox v-model="formData.dueToTakeBackWork">到期时收回受托人未办理的事项</a-checkbox>
        <br />
        <a-checkbox v-model="formData.deactiveToTakeBackWork">终止时收回受托人未办理的事项</a-checkbox>
        <br />
        <a-checkbox v-model="formData.allowSecondaryDelegation">
          是否允许二次委托
          <a-popover>
            <template slot="content">委托给受托人的工作，受拖人可再次通过委托设置，委托给其他人</template>
            <a-icon type="info-circle" />
          </a-popover>
        </a-checkbox>
      </a-form-model-item>
      <a-form-model-item label="待办显示" prop="delegationTaskVisible">
        <a-checkbox checked disabled>受托人</a-checkbox>
        <a-checkbox v-model="formData.delegationTaskVisible">委托人</a-checkbox>
      </a-form-model-item>
      <a-form-model-item v-if="isReadonly" label="状态" prop="status">
        <a-tag v-if="formData.status == 0">失效</a-tag>
        <a-tag v-else-if="formData.status == 1" color="green">生效</a-tag>
        <a-tag v-else-if="formData.status == 2" color="blue">征求意见中</a-tag>
        <a-tag v-else-if="formData.status == 3" color="red">已拒绝</a-tag>
        <a-tag v-else>{{ formData.status }}</a-tag>
      </a-form-model-item>
    </a-form-model>
    <div v-if="showCommon" class="common-container">
      <a-button class="btn-expand" v-show="!showCommonList" type="primary" @click="showCommonDelegationSetting">
        <Icon type="pticon iconfont icon-luojizujian-huifujishi" />
        常用委托
      </a-button>
      <a-card bordered v-show="showCommonList" class="card-common" :bodyStyle="commonBodyStyle">
        <template slot="title">
          <a-button type="primary" @click="showCommonDelegationSetting">
            <Icon type="pticon iconfont icon-luojizujian-huifujishi" />
            常用委托
          </a-button>
        </template>
        <template slot="extra">
          <a-button type="icon" class="icon-only" @click="showCommonList = false">
            <Icon type="pticon iconfont icon-ptkj-youshouzhan" title="收起" />
          </a-button>
        </template>
        <PerfectScrollbar :style="commonBodyStyle">
          <a-list :data-source="commonItems" :locale="locale">
            <a-list-item
              slot="renderItem"
              slot-scope="item"
              @click="onCommonItemClick(item)"
              :style="{
                marginBottom: '10px',
                background: item.selected ? 'var(--w-primary-color-3)' : 'var(--w-primary-color-1)'
              }"
            >
              <a-list-item-meta :title="item.name">
                <template slot="description">
                  <a-row>
                    <a-col span="12">受托人&nbsp;&nbsp;{{ item.setting && item.setting.trusteeName }}</a-col>
                    <a-col span="12">最近使用{{ item.usedCount }}次</a-col>
                  </a-row>
                </template>
              </a-list-item-meta>
              <template slot="actions">
                <a-dropdown>
                  <a-menu slot="overlay">
                    <a-menu-item key="edit" @click="onEditCommonItemClick(item)">
                      <i class="iconfont icon-ptkj-bianji"></i>
                      编辑
                    </a-menu-item>
                    <a-menu-item key="delete" @click="onDeleteCommonItemClick(item)">
                      <i class="iconfont icon-ptkj-shanchu"></i>
                      删除
                    </a-menu-item>
                  </a-menu>
                  <i class="iconfont icon-ptkj-gengduocaozuo icon-operate"></i>
                </a-dropdown>
              </template>
            </a-list-item>
          </a-list>
        </PerfectScrollbar>
      </a-card>
    </div>
    <a-modal
      :title="saveAsModalTitle"
      :visible="saveAsModalVisible"
      okText="保存"
      @cancel="() => (saveAsModalVisible = false)"
      @ok="onSaveCommonClick"
    >
      <a-form-model ref="saveAsForm" :model="saveAsFormData" :rules="saveAsRules" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-form-model-item label="名称" prop="name">
          <a-input v-model="saveAsFormData.name" :maxLength="100"></a-input>
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </a-skeleton>
</template>

<script>
import { isEmpty } from 'lodash';
import moment from 'moment';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import FlowSelect from '@workflow/app/web/lib/flow-select.vue';
import WorkflowDelegationConditionConfiguration from './component/delegation-condition-configuration.vue';
import WorkflowDelegationSettings from '@workflow/app/web/widget/@develop/WorkflowDelegationSettings.js';
import { deepClone, generateId } from '@framework/vue/utils/util';
export default {
  props: {
    settings: Object
  },
  inject: ['pageContext', '$event', 'vPageState', 'locale'],
  components: { OrgSelect, FlowSelect, WorkflowDelegationConditionConfiguration },
  data() {
    const _this = this;
    let $event = _this.$event;
    let consignorValidator = (rule, value, callback) => {
      if (_this.formData.trustee == value) {
        callback(new Error('委托人不能与受托人一致！'));
      } else {
        callback();
      }
    };
    let trusteeValidator = (rule, value, callback) => {
      if (_this.formData.consignor == value) {
        callback(new Error('受托人与委托人不能一致！'));
      } else {
        callback();
      }
    };
    // let fromTimeValidator = (rule, value, callback) => {
    //   if (isEmpty(value)) {
    //     if (isEmpty(_this.formData.toTime)) {
    //       callback();
    //     } else {
    //       // 结束时间要大于当前时间
    //       let toTime = moment(_this.formData.toTime);
    //       let now = (now = moment());
    //       if (toTime.isBefore(now)) {
    //         callback(new Error('结束时间不能小于当前时间！'));
    //       } else {
    //         callback();
    //       }
    //     }
    //   } else {
    //     if (!isEmpty(_this.formData.toTime)) {
    //       // 结束时间要大于开始时间、当前时间
    //       let fromTime = moment(_this.formData.fromTime);
    //       let toTime = moment(_this.formData.toTime);
    //       let now = (now = moment());
    //       if (!fromTime.isBefore(toTime)) {
    //         callback(new Error('开始时间要小于结束时间！'));
    //       } else if (toTime.isBefore(now)) {
    //         callback(new Error('结束时间不能小于当前时间！'));
    //       } else {
    //         callback();
    //       }
    //     } else {
    //       callback();
    //     }
    //   }
    // };
    let uuid = $event && $event.meta != undefined ? $event.meta.uuid : (this.settings && this.settings.uuid) || undefined;
    let fromTime = moment().format('YYYY-MM-DD HH:mm:ss');
    let toTime = moment().add(7, 'day').format('YYYY-MM-DD HH:mm:ss');
    return {
      moment,
      loading: $event && $event.meta && $event.meta.uuid != undefined,
      flowSelectKey: generateId(),
      taskSelectKey: generateId(),
      uuid,
      currentUser: this.$store.state._CONTEXT_STATE_.USER || { admin: false },
      trusteeScope: 'all',
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      contentTreeData: [],
      jobIdentityOptions: [],
      formData: {
        consignorName: '',
        consignor: '',
        trusteeName: '',
        trustee: '',
        contentConfig: { type: 'all', values: [], labels: [], jobIdentities: [] },
        contentName: '全部流程',
        includeCurrentWork: false,
        dueToTakeBackWork: false,
        deactiveToTakeBackWork: false,
        delegationTaskVisible: false,
        status: $event && $event.meta != undefined ? $event.meta.status : 1,
        delegationTimes: [fromTime, toTime],
        fromTime,
        toTime,
        consultMessageId: null
      },
      rules: {
        consignor: [
          { required: true, message: '委托人不能为空！', trigger: 'change' },
          { validator: consignorValidator, trigger: 'change' }
        ],
        trustee: [
          { required: true, message: '受托人不能为空！', trigger: 'change' },
          { validator: trusteeValidator, trigger: 'change' }
        ]
        // fromTime: [
        //   {
        //     validator: (rule, value, callback) => {
        //       if (!isEmpty(_this.formData.toTime) && !isEmpty(value)) {
        //         // 开始时间要小于结束时间
        //         let fromTime = moment(value);
        //         let toTime = moment(_this.formData.toTime);
        //         return fromTime.isBefore(toTime);
        //       }
        //       return true;
        //     },
        //     message: '开始时间要小于结束时间！',
        //     trigger: ['blur', 'change']
        //   },
        //   {
        //     validator: (rule, value, callback) => {
        //       if (!isEmpty(_this.formData.toTime)) {
        //         let toTime = moment(_this.formData.toTime);
        //         let now = (now = moment());
        //         return toTime.isAfter(now);
        //       }
        //       return true;
        //     },
        //     message: '结束时间不能小于当前时间！',
        //     trigger: ['blur', 'change']
        //   }
        // ]
      },
      saveAsModalVisible: false,
      saveAsModalTitle: '保存常用委托',
      saveAsFormData: {
        name: undefined
      },
      saveAsRules: {
        name: [{ required: true, message: '不能为空！', trigger: 'change' }]
      },
      showCommonList: false,
      commonItems: [],
      commonBodyStyle: {
        height: '500px'
      }
    };
  },
  computed: {
    isAdmin() {
      return this.currentUser.admin || this._hasRole('ROLE_TENANT_ADMIN');
    },
    showCommon() {
      return isEmpty(this.uuid);
    },
    isReadonly() {
      return !isEmpty(this.uuid); //!this.isAdmin && this.formData.consignor && this.formData.consignor != this.currentUser.userId;
    },
    isCreateAgain() {
      return this.$event && this.$event.eventParams && this.$event.eventParams.createAgain == 'true';
    }
  },
  created() {
    let _this = this;
    _this.loadDelegationSetting();
    if (_this.uuid) {
      _this.updateModalButtons(_this.$event, false, _this.uuid);
    } else {
      _this.updateModalButtons(_this.$event, true, _this.uuid);
    }
    if (_this.uuid) {
      _this.getFlowDelegationSettings(this.uuid).then(() => {
        if (!this.showCommon && this.isCreateAgain) {
          this.createAgain();
        }
      });
    } else if (!_this.isAdmin) {
      // 当前用户不是管理员，默认设置当前用户
      _this.formData.consignor = _this.currentUser.userId;
      _this.formData.consignorName = _this.currentUser.userName;
      _this.loadJobIdentityOptions();
    }
    // _this.loadContentTreeData();
  },
  methods: {
    updateModalButtons($event, create, delegationSettingsUuid) {
      const _this = this;
      let widgetModal = _this.widgetModal;
      if (widgetModal == null) {
        if (!$event || !$event.$evtWidget) {
          return;
        }
        let $eventWidget = $event.$evtWidget;
        let items = null;
        let $parent = $eventWidget;
        while (items == null && $parent != null) {
          $parent = $parent.$parent;
          items = $parent.items;
        }
        if (items == null) {
          return;
        }
        widgetModal = items.find(item => item.wtype == 'WidgetModal');
        _this.widgetModal = widgetModal;
      }
      if (widgetModal == null) {
        return;
      }

      let $widgetModal = _this.pageContext.getVueWidgetById(widgetModal.id);
      $widgetModal.delegationSettingsUuid = delegationSettingsUuid;
      let buttons = widgetModal.configuration.footerButton.buttons || [];
      buttons.forEach(button => {
        if (create) {
          if (['active', 'consult', 'saveAs', 'cancel'].includes(button.code)) {
            button.defaultVisible = true;
          } else {
            button.defaultVisible = false;
          }
        } else {
          if (['deactive', 'viewData'].includes(button.code)) {
            if (['2', '3'].includes(_this.formData.status)) {
              if (button.code != 'deactive') {
                button.defaultVisible = false;
              }
            } else if (_this.formData.status == '0') {
              if (button.code == 'deactive') {
                button.defaultVisible = false;
              } else {
                button.defaultVisible = true;
              }
            } else {
              button.defaultVisible = true;
            }
          } else if (['agreen', 'refuse'].includes(button.code)) {
            if (_this.formData.status == '2') {
              button.defaultVisible = true;
            } else {
              button.defaultVisible = false;
            }
          } else if ('delete' == button.code) {
            if (_this.formData.status != '1') {
              button.defaultVisible = true;
            } else {
              button.defaultVisible = false;
            }
          } else if (['saveAs', 'createAgain', 'cancel'].includes(button.code)) {
            button.defaultVisible = true;
          } else {
            button.defaultVisible = false;
          }
        }
      });
      widgetModal.configuration.footerHidden = true;
      _this.$nextTick(() => {
        widgetModal.configuration.footerHidden = false;
      });
    },
    loadDelegationSetting() {
      return $axios.get('/proxy/api/workflow/setting/getByKey?key=DELEGATION').then(({ data: result }) => {
        if (result.data && result.data.attrVal) {
          this.trusteeScope = JSON.parse(result.data.attrVal).trusteeScope;
        }
      });
    },
    updateMaskIfRequired() {
      const _this = this;
      setTimeout(() => {
        if (_this.$refs.maskDiv && _this.$refs.form) {
          _this.$refs.maskDiv.style.height = _this.$refs.form.$el.clientHeight + 'px';
        }
      }, 500);
    },
    fixedFormData(formData) {
      let contentConfig = null;
      if (!formData.contentConfig) {
        if (formData.content) {
          if (formData.content.startsWith('{')) {
            contentConfig = JSON.parse(formData.content);
          } else {
            let contentValues = formData.content.split(';');
            contentConfig = { type: 'flow', values: contentValues, jobIdentities: ['all'] };
          }
        } else {
          contentConfig = { type: 'all', values: [], labels: [], jobIdentities: ['all'] };
        }
        this.$set(formData, 'contentConfig', contentConfig);
      }
      // formData.jobIdentityValues = formData.jobIdentity ? formData.jobIdentity.split(';') : [];
      if (formData.conditionJson) {
        formData.conditionConfig = JSON.parse(formData.conditionJson);
      }
      formData.delegationTimes = [formData.fromTime, formData.toTime];
    },
    getFlowDelegationSettings(uuid) {
      const _this = this;
      return $axios
        .get(`/api/workflow/delegation/settiongs/get?uuid=${uuid}`)
        .then(({ data: result }) => {
          if (result.code === 0) {
            this.loading = false;
            let formData = result.data || {};
            _this.fixedFormData(formData);
            _this.formData = formData;
            _this.loadJobIdentityOptions();
            _this.updateMaskIfRequired();
          } else {
            this.$message.error('系统服务异常！');
          }
        })
        .catch(error => {
          this.loading = false;
          this.$message.error('系统服务异常！');
        });
    },
    consignorChange({ value, label, nodes }) {
      this.formData.consignorName = label;
      if (this.formData.contentConfig) {
        this.formData.contentConfig.jobIdentities = [];
      }
      this.loadJobIdentityOptions(true);
    },
    trusteeChange({ value, label, nodes }) {
      this.formData.trusteeName = label;
    },
    contentTypeChange() {
      this.formData.contentConfig.values = [];
      this.formData.contentConfig.labels = [];
      if (this.formData.contentConfig.type == 'all') {
        this.formData.contentName = '全部流程';
      } else {
        this.formData.contentName = '';
      }
    },
    flowSelectChange({ value, label, nodes }) {
      this.formData.contentConfig.labels = label;
      if (this.formData.contentConfig.type == 'flow') {
        this.formData.contentName = '指定流程：' + label.join('；');
      } else if (this.formData.contentConfig.type == 'task') {
        let taskNameMap = {};
        nodes.forEach(node => {
          if (!taskNameMap[node.flowDefName]) {
            taskNameMap[node.flowDefName] = [];
          }
          taskNameMap[node.flowDefName].push(node.name);
        });
        let taskNames = [];
        for (let flowName in taskNameMap) {
          taskNames.push(flowName + '(' + taskNameMap[flowName].join('、') + ')');
        }
        this.formData.contentName = '指定环节：' + taskNames.join('；');
      } else {
        this.formData.contentName = '';
      }
    },
    flowSelectKeyNotFound(nodeFoundKeys) {
      const _this = this;
      _this.$message.info('部分委托内容不存在，请注意检查！');
      if (_this.formData.contentConfig) {
        let values = _this.formData.contentConfig.values || [];
        let labels = _this.formData.contentConfig.labels || [];
        if (values.length == labels.length) {
          for (let index = 0; index < values.length; index++) {
            if (nodeFoundKeys.includes(values[index])) {
              values.splice(index, 1);
              labels.splice(index, 1);
              index--;
            }
          }
        }
      }
    },
    loadContentTreeData() {
      let _this = this;
      this.$axios
        .post('/json/data/services', {
          serviceName: 'workflowDelegationSettiongsService',
          methodName: 'getContentAsTreeAsync',
          args: JSON.stringify(['-1'])
        })
        .then(({ data: result }) => {
          if (result.data) {
            _this.contentTreeData = result.data;
          }
        });
    },
    loadJobIdentityOptions(force = false) {
      const _this = this;
      _this.jobIdentityOptions = []; //[{ label: '全部身份', value: 'all' }];
      if (!_this.formData.consignor && !force) {
        return;
      }

      let orgUserPromise = $axios
        .get(`/proxy/api/user/org/getUserOrgs?userId=${_this.formData.consignor}`)
        .then(({ data: result }) => {
          let options = [];
          if (result.data) {
            result.data.forEach(item => {
              let paths = (item.orgElementCnPath && item.orgElementCnPath.split('/')) || [];
              if (paths.length) {
                paths.splice(paths.length - 1, 1);
              }
              if (item.type == 'MEMBER_USER') {
                options.push({
                  label: item.orgElementName,
                  value: item.orgElementId,
                  tagName: '部门',
                  path: paths.join('/')
                });
              } else {
                options.push({
                  label: item.orgElementName,
                  value: item.orgElementId,
                  tagName: '职位',
                  path: paths.join('/')
                });
              }
            });
          }
          return options;
        })
        .catch(() => {
          return [];
        });

      let bizPromise = $axios
        .get(`/proxy/api/org/biz/getUserBizOrgElementRoles?userId=${_this.formData.consignor}`)
        .then(({ data: result }) => {
          let options = [];
          if (result.data) {
            result.data.forEach(item => {
              let orgName = item.bizOrg && item.bizOrg.name;
              let cnPath = item.bizOrgElementPath && item.bizOrgElementPath.cnPath;
              options.push({
                label: item.bizOrgRole && item.bizOrgRole.name,
                value: item.bizOrgElementId + '/' + item.bizOrgRoleId,
                tagName: '角色',
                path: orgName + (orgName && cnPath ? '/' + cnPath : '')
              });
            });
          }
          return options;
        })
        .catch(() => {
          return [];
        });
      Promise.all([orgUserPromise, bizPromise]).then(results => {
        _this.jobIdentityOptions = [..._this.jobIdentityOptions, ...results[0], ...results[1]];
      });
    },
    onSetConditionClick() {
      this.$set(this.formData, 'conditionConfig', { match: 'all', conditions: [{ operator: undefined }] });
    },
    getDisabledDate(current) {
      return current && current < moment().startOf('date');
    },
    save(event, status, successMsg) {
      // 保存数据
      let _this = this;
      if (_this.saving) {
        return;
      }
      _this.$refs.form.validate(valid => {
        if (valid) {
          _this.saving = true;
          _this.$loading('保存中...');
          let formData = { ..._this.formData };
          if (Array.isArray(formData.contentConfig)) {
            let contentValues = [];
            let contentLabels = [];
            formData.content.forEach(item => {
              if (item.label) {
                contentLabels.push(item.label);
              }
              contentValues.push(item.value);
            });
            formData.content = contentValues.join(';');
            let contentName = contentLabels.join(';');
            if (!formData.content || (formData.content && contentName)) {
              formData.contentName = contentName;
            }
          } else if (typeof formData.contentConfig == 'object') {
            formData.content = JSON.stringify(formData.contentConfig);
            if (formData.contentConfig.type == 'flow' && formData.contentConfig.values.length == 0) {
              _this.$message.error('指定流程不能为空！');
              _this.$loading(false);
              _this.saving = false;
              return;
            } else if (formData.contentConfig.type == 'task' && formData.contentConfig.values.length == 0) {
              _this.$message.error('指定环节不能为空！');
              _this.$loading(false);
              _this.saving = false;
              return;
            }
          }
          // formData.jobIdentity = formData.jobIdentityValues.join(';');
          if (formData.conditionConfig) {
            formData.conditionJson = JSON.stringify(formData.conditionConfig);
          }
          if (formData.delegationTimes) {
            formData.fromTime = formData.delegationTimes[0];
            formData.toTime = formData.delegationTimes[1];
          }
          if (status) {
            formData.status = status;
          }
          $axios
            .post('/api/workflow/delegation/settiongs/save', formData)
            .then(({ data: result }) => {
              _this.saving = false;
              _this.$loading(false);
              if (result.code == 0) {
                _this.$message.success(successMsg || '保存成功');
                // 刷新表格
                _this.pageContext.emitEvent('lMFDwGRpXmqqNNkHsOdCAvQHGanSFNfp:refetch');
                event.$evtWidget.closeModal();
              } else if (result.msg) {
                _this.$message.error(result.msg);
              } else {
                _this.$message.error('系统服务异常！');
              }
            })
            .catch(({ response }) => {
              _this.saving = false;
              _this.$loading(false);
              _this.$message.error((response && response.data && response.data.msg) || '系统服务异常！');
            });
        }
      });
    },
    active(event) {
      this.save(event, 1, '已生效！');
    },
    consult(event) {
      this.save(event, 2, '征求意见中');
    },
    createAgain(event) {
      const _this = this;
      if (event && event.$evtWidget && event.$evtWidget.updateModalTitle && event.eventParams) {
        event.$evtWidget.updateModalTitle(event.eventParams.title || '新增委托');
      }
      _this.$loading(true);
      _this.formData = deepClone(_this.formData);
      _this.formData.uuid = _this.uuid = null;
      _this.formData.status = 1;
      let fromTime = moment().format('YYYY-MM-DD HH:mm:ss');
      let toTime = moment().add(7, 'day').format('YYYY-MM-DD HH:mm:ss');
      _this.formData.fromTime = fromTime;
      _this.formData.toTime = toTime;
      _this.formData.delegationTimes = [fromTime, toTime];
      setTimeout(() => {
        _this.updateModalButtons(event, true, _this.formData.uuid);
        _this.$loading(false);
      }, 300);
    },
    saveAs() {
      const _this = this;
      _this.saveAsModalVisible = true;
      _this.saveAsModalTitle = '保存常用委托';
      _this.saveAsFormData = {
        name: '流程委托',
        definitionJson: JSON.stringify(_this.formData),
        usedCount: 0
      };
    },
    onSaveCommonClick() {
      const _this = this;
      _this.$loading('保存中...');
      _this.$refs.saveAsForm.validate(valid => {
        if (valid) {
          $axios
            .post('/proxy/api/workflow/delegation/settiongs/saveCommon', _this.saveAsFormData)
            .then(({ data: result }) => {
              _this.$loading(false);
              if (result.code == 0) {
                _this.$message.success('保存成功');
                _this.saveAsModalVisible = false;
                _this.loadCommonDelegationSetting();
              } else {
                _this.$message.error(result.msg || '系统服务异常！');
              }
            })
            .catch(({ response }) => {
              _this.$loading(false);
              _this.$message.error((response && response.data && response.data.msg) || '系统服务异常！');
            });
        } else {
          _this.$loading(false);
        }
      });
    },
    showCommonDelegationSetting() {
      this.showCommonList = true;
      this.loadCommonDelegationSetting();
      this.commonBodyStyle.height = this.$refs.form.$el.clientHeight + 'px';
    },
    loadCommonDelegationSetting() {
      $axios
        .post('/proxy/api/workflow/delegation/settiongs/listCurrentUserCommon', {
          currentPage: 1,
          pageSize: 100
        })
        .then(({ data: result }) => {
          if (result && result.data) {
            let items = result.data;
            items.forEach(item => {
              item.selected = false;
              if (item.definitionJson) {
                item.setting = JSON.parse(item.definitionJson);
              }
            });
            this.commonItems = items;
          }
        });
    },
    onCommonItemClick(item) {
      const _this = this;
      _this.commonItems.forEach(item => (item.selected = false));
      item.selected = true;

      let formData = deepClone(item.setting);
      formData.uuid = null;
      formData.status = 1;
      let fromTime = moment().format('YYYY-MM-DD HH:mm:ss');
      let toTime = moment().add(7, 'day').format('YYYY-MM-DD HH:mm:ss');
      formData.fromTime = fromTime;
      formData.toTime = toTime;
      formData.delegationTimes = [fromTime, toTime];
      _this.fixedFormData(formData);
      let jobIdentities = formData.contentConfig.jobIdentities || [];
      formData.contentConfig.jobIdentities = _this.jobIdentityOptions
        .filter(option => jobIdentities.includes(option.value))
        .map(option => option.value);
      _this.formData = formData;
      _this.formData.commonUuid = item.uuid;
      _this.flowSelectKey = generateId();
      _this.taskSelectKey = generateId();
    },
    onEditCommonItemClick(item) {
      const _this = this;
      _this.saveAsFormData = item;
      _this.saveAsModalVisible = true;
      _this.saveAsModalTitle = '编辑常用委托';
    },
    onDeleteCommonItemClick(item) {
      const _this = this;
      _this.$confirm({
        title: '确认框',
        content: `确认删除常用委托[${item.name}]？`,
        onOk() {
          _this.$loading('删除中...');
          $axios
            .post(`/proxy/api/workflow/delegation/settiongs/deleteCommon?uuid=${item.uuid}`)
            .then(({ data: result }) => {
              _this.$loading(false);
              if (result.code == 0) {
                _this.$message.success('删除成功！');
                _this.loadCommonDelegationSetting();
              } else {
                _this.$message.error(result.msg || '系统服务异常！');
              }
            })
            .catch(({ response }) => {
              _this.$loading(false);
              _this.$message.error((response && response.data && response.data.msg) || '系统服务异常！');
            });
        }
      });
    },
    deactive(event) {
      let _this = this;

      if (_this.formData.status == 0) {
        _this.$message.error('只可终止状态为生效或征求意见中的委托！');
        return;
      }

      let uuids = [_this.formData.uuid];
      _this.$loading('终止中...');
      $axios
        .post('/api/workflow/delegation/settiongs/deactiveAll', { uuids })
        .then(({ data: result }) => {
          _this.$loading(false);
          if (result.code == 0) {
            _this.$message.success('终止成功！');
            // 刷新表格
            _this.pageContext.emitEvent('lMFDwGRpXmqqNNkHsOdCAvQHGanSFNfp:refetch');
            event.$evtWidget.closeModal();
          } else {
            _this.$message.success(result.msg || '终止失败！');
          }
        })
        .catch(({ response }) => {
          _this.$loading(false);
          _this.$message.error((response && response.data && response.data.msg) || '系统服务异常！');
        });
    },
    delete(event) {
      const _this = this;
      let uuids = [_this.formData.uuid];
      _this.$confirm({
        title: '确认框',
        content: '确认要删除吗？',
        onOk() {
          _this.$loading('删除中...');
          $axios
            .post(`/api/workflow/delegation/settiongs/deleteAll`, { uuids })
            .then(({ data: result }) => {
              _this.$loading(false);
              if (result.code == 0) {
                _this.$message.success('删除成功！');
                // 刷新表格
                _this.pageContext.emitEvent('lMFDwGRpXmqqNNkHsOdCAvQHGanSFNfp:refetch');
                event.$evtWidget.closeModal();
              } else {
                _this.$message.error(result.msg || '删除失败！');
              }
            })
            .catch(({ response }) => {
              _this.$loading(false);
              _this.$message.error((response && response.data && response.data.msg) || '系统服务异常！');
            });
        }
      });
    },
    agreen(event) {
      const _this = this;
      let uuid = _this.formData.uuid;
      let settings = new WorkflowDelegationSettings(_this);
      settings.agreen(uuid).then(success => {
        if (success) {
          // 刷新表格
          _this.pageContext.emitEvent('WiqRCKJRiDWCLeiIPSqFlvENHMXvSGgM:refetch');
          event.$evtWidget.closeModal();
        }
      });
    },
    refuse(event) {
      const _this = this;
      let uuid = _this.formData.uuid;
      let settings = new WorkflowDelegationSettings(_this);
      settings.refuse(uuid).then(success => {
        if (success) {
          // 刷新表格
          _this.pageContext.emitEvent('WiqRCKJRiDWCLeiIPSqFlvENHMXvSGgM:refetch');
          event.$evtWidget.closeModal();
        }
      });
    }
  },
  META: {
    method: {
      active: '立即生效',
      consult: '征求受托人意见',
      createAgain: '再次委托',
      saveAs: '保存为常用',
      deactive: '终止',
      delete: '删除',
      agreen: '同意',
      refuse: '拒绝'
    }
  }
};
</script>

<style lang="less" scoped>
.common-container {
  position: absolute;
  top: -3px;
  right: 3px;

  .btn-expand {
    margin-top: 8px;
  }

  .card-common {
    width: 300px;
  }
}
</style>
