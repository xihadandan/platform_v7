<template>
  <a-form-model-item class="form-item-vertical" label="自定义操作">
    <div class="set-and-clear">
      <modal title="自定义" :width="800" v-model="visible" :ok="save" wrapperClass="wf-custom-buttons-modal" :container="getContainer">
        <template slot="content">
          <a-form-model
            ref="form"
            :model="formData"
            :colon="false"
            :rules="rules"
            :label-col="{ flex: '120px' }"
            :wrapper-col="{ flex: 'auto' }"
            class="custom-button-info"
          >
            <a-form-model-item label="按钮类型" prop="btnSource">
              <a-radio-group v-model="formData.btnSource" @change="changeSource">
                <a-radio v-for="item in customAuthBtnSource" :key="item.id" :value="item.id">
                  {{ item.text }}
                </a-radio>
              </a-radio-group>
            </a-form-model-item>
            <template v-if="formData.btnSource === customAuthBtnSource[0]['id']">
              <!-- 内置功能 -->
              <a-form-model-item label="按钮功能" prop="btnValue">
                <w-select
                  :options="btnData"
                  v-model="formData.btnValue"
                  :replaceFields="{
                    title: 'name',
                    key: 'value',
                    value: 'value'
                  }"
                ></w-select>
              </a-form-model-item>
              <a-form-model-item label="按钮名称" prop="newName">
                <a-input v-model="formData.newName">
                  <template slot="addonAfter" v-if="formData.uuid">
                    <w-i18n-input :target="formData" :code="formData.uuid" v-model="formData.newName" />
                  </template>
                </a-input>
              </a-form-model-item>
              <a-form-model-item>
                <template slot="label">&nbsp;</template>
                <a-radio-group v-model="formData.useWay" size="small">
                  <a-radio v-for="item in customAuthUseWay" :key="item.id" :value="item.id">
                    {{ item.text }}
                  </a-radio>
                </a-radio-group>
              </a-form-model-item>
              <template v-if="['B004002', 'B004020', 'B004016'].indexOf(formData.btnValue) > -1">
                <a-form-model-item label="目标环节" prop="btnArgument">
                  <task-subflow-select
                    v-model="formData.btnArgument"
                    :hasSubFlow="true"
                    :beforeOptions="beforeOptionsNode"
                    :afterOptions="afterOptionsNode"
                  ></task-subflow-select>
                </a-form-model-item>
              </template>
              <template v-else-if="['B004003'].indexOf(formData.btnValue) > -1">
                <a-form-model-item label="目标环节" prop="btnArgument">
                  <task-subflow-select v-model="formData.btnArgument" :hasSubFlow="true"></task-subflow-select>
                </a-form-model-item>
              </template>
              <template v-if="['B004002'].indexOf(formData.btnValue) > -1">
                <a-form-model-item label="参与人">
                  <user-select
                    v-model="formData.btnUsers"
                    :types="userTypes"
                    title="选择参与人"
                    :orgVersionId="orgVersionId"
                    @change="data => usersChange('btnUserIds', data)"
                  ></user-select>
                </a-form-model-item>
                <a-form-model-item label="抄送人">
                  <user-select
                    v-model="formData.btnCopyUsers"
                    :types="userTypes"
                    title="选择抄送人"
                    :orgVersionId="orgVersionId"
                    @change="data => usersChange('btnCopyUserIds', data)"
                  ></user-select>
                </a-form-model-item>
              </template>
            </template>
            <template v-else-if="formData.btnSource === customAuthBtnSource[1]['id']">
              <!-- 事件处理 -->
              <a-form-model-item label="按钮名称" prop="newName">
                <a-input v-model="formData.newName">
                  <template slot="addonAfter" v-if="formData.uuid">
                    <w-i18n-input :target="formData" :code="formData.uuid" v-model="formData.newName" />
                  </template>
                </a-input>
              </a-form-model-item>
              <!-- <a-form-model-item label="按钮ID" prop="btnId">
                <a-input v-model="formData.btnId"></a-input>
              </a-form-model-item> -->
              <a-form-model-item label="按钮类型">
                <a-select :options="buttonTypeOptions" v-model="formData.btnClassName.type" :style="{ width: '100%' }"></a-select>
              </a-form-model-item>
              <a-form-model-item label="按钮图标">
                <WidgetIconLibModal v-model="formData.btnClassName.icon" :zIndex="1000">
                  <a-badge>
                    <a-icon
                      v-if="formData.btnClassName.icon"
                      slot="count"
                      type="close-circle"
                      style="color: #f5222d"
                      theme="filled"
                      @click.stop="formData.btnClassName.icon = undefined"
                      title="删除图标"
                    />
                    <a-button size="small" shape="round">
                      {{ formData.btnClassName.icon ? '' : '设置图标' }}
                      <Icon :type="formData.btnClassName.icon || 'setting'" />
                    </a-button>
                  </a-badge>
                </WidgetIconLibModal>
              </a-form-model-item>

              <event-handler-config
                ref="eventHandler"
                :eventModel="formData.eventHandler"
                :actionTypesProp="[
                  { label: '页面跳转', value: 'redirectPage' },
                  { label: '工作流', value: 'workflow' },
                  { label: '脚本代码', value: 'jsFunction' },
                  { label: 'JS模块', value: 'customJsModule' }
                ]"
                :formLayout="{
                  layout: 'horizontal',
                  colon: false,
                  labelCol: { flex: '120px' },
                  wrapperCol: { flex: 'auto' }
                }"
              />
              <a-form-model-item label="按钮排序号" style="margin-top: 10px">
                <a-input v-model="formData.sortOrder"></a-input>
                <div style="line-height: var(--w-line-height)">
                  内置按钮排序号：提交(10)、退回(20)、退回前办理人(30)、撤回(40)、转办(50)、会签(60)、加签(70)、保存(80)、关注(90)、抄送(100)、打印(110)、签署意见(120)、挂起(130)、恢复(140)、催办(150)、特送个人(160)、特送环节(170)、删除(180)、管理员删除(190)
                </div>
              </a-form-model-item>
            </template>
            <condition-control-configuration
              v-if="visible"
              :configuration="formData"
              configCode="configuration"
              :designer="designer"
              :fieldOptionsProp="designer.formFieldDefinition.fields"
            />
            <!-- 公共功能 -->
            <a-form-model-item label="备注">
              <a-textarea v-model="formData.btnRemark" :auto-size="{ minRows: 3, maxRows: 5 }" />
            </a-form-model-item>
            <a-form-model-item>
              <template slot="label">
                <span style="font-weight: bold">按钮权限设置</span>
              </template>
            </a-form-model-item>
            <a-form-model-item label="应用场景">
              <w-select
                popupPlacement="topLeft"
                :options="customAuthBtnRoles"
                v-model="formData.btnRole"
                mode="multiple"
                :getPopupContainer="target => target.closest('.ant-form').children[0]"
              />
            </a-form-model-item>
            <template v-if="formData.btnSource === customAuthBtnSource[0]['id']">
              <!-- 内置功能 -->
              <a-form-model-item label="使用人">
                <org-select
                  :orgVersionId="orgVersionId"
                  :orgVersionIds="orgVersionIds"
                  v-model="formData.btnOwnerIds"
                  v-if="orgVersionId"
                  @change="changeOrg"
                />
              </a-form-model-item>
              <a-form-model-item>
                <template slot="label">
                  <a-tooltip placement="topLeft" :arrowPointAtCenter="true">
                    <div slot="title">指定关联权限按钮后，当前按钮的权限等同于关联权限按钮的权限，可通过角色权限进行设置</div>
                    <label>
                      关联权限按钮
                      <a-icon type="exclamation-circle" />
                    </label>
                  </a-tooltip>
                </template>
                <w-tree-select
                  popupPlacement="topLeft"
                  :getPopupContainer="getPopupContainer"
                  :treeData="buttonTree"
                  v-model="formData.newCode"
                  :replaceFields="{
                    children: 'children',
                    title: 'name',
                    key: 'data',
                    value: 'data'
                  }"
                />
              </a-form-model-item>
            </template>
          </a-form-model>
        </template>
      </modal>
      <a-button type="link" size="small" @click="add">
        <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
        设置
      </a-button>
      <a-button type="link" size="small" @click="clear">
        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        清空
      </a-button>
    </div>
    <div class="wf-custom-buttons">
      <div class="flex custom-btn-item" v-for="(record, index) in buttons" :key="index">
        <div class="f_g_1">{{ record.newName }}</div>
        <div class="f_s_0">
          <a-button type="link" size="small" @click="set(record, index)"><Icon type="pticon iconfont icon-ptkj-shezhi"></Icon></a-button>
          <a-button type="link" size="small" @click="del(record, index)"><Icon type="pticon iconfont icon-ptkj-shanchu"></Icon></a-button>
        </div>
      </div>
    </div>
  </a-form-model-item>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import { generateId, deepClone } from '@framework/vue/utils/util';
import { customAuthBtnSource, customAuthUseWay, customAuthBtnRoles } from '../designer/constant';
import WSelect from '../components/w-select';
import WTreeSelect from '../components/w-tree-select';
import UserSelect from '../commons/user-select.vue';
import TaskSubflowSelect from '../commons/task-subflow-select';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import EventHandlerConfig from '../commons/event-handler-config.vue';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import { buttonTypeOptions } from '@pageAssembly/app/web/widget/commons/constant';
import { filter, map, each, findIndex, isInteger, some } from 'lodash';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
import ConditionControlConfiguration from '../commons/condition-control-configuration.vue';

export default {
  name: 'CustomButtons',
  inject: ['designer', 'workFlowData'],
  provide() {
    return {
      appId: this.workFlowData.moduleId,
      subAppIds: []
    };
  },
  props: {
    value: {
      type: Array,
      default: () => []
    }
  },
  components: {
    Modal,
    WSelect,
    WTreeSelect,
    UserSelect,
    TaskSubflowSelect,
    OrgSelect,
    WidgetIconLibModal,
    EventHandlerConfig,
    WI18nInput,
    ConditionControlConfiguration
  },
  data() {
    let buttons = deepClone(this.value);
    let initFormData = {
      btnSource: '1',
      btnRole: '',
      btnId: null,
      piUuid: null,
      piName: null,
      hashType: null,
      hash: null,
      eventParams: null,
      targetPosition: null,
      id: null,
      name: null,
      btnValue: '',
      newName: '',
      newCode: '',
      useWay: '2',
      btnArgument: null,
      btnOwners: '',
      btnOwnerIds: '',
      btnUsers: [],
      btnUserIds: null,
      btnCopyUsers: [],
      btnCopyUserIds: null,
      btnIcon: null,
      btnStyle: null,
      btnRemark: '',
      sortOrder: null,
      btnClassName: null,
      eventHandler: null,
      configuration: ''
    };

    let btnTypeOptions = deepClone(buttonTypeOptions);
    btnTypeOptions.splice(-1, 1);
    return {
      buttonTypeOptions: btnTypeOptions,
      customAuthBtnSource,
      customAuthUseWay,
      customAuthBtnRoles,
      visible: false,
      buttons,
      formData: { uuid: generateId(6) },
      initFormData,
      currentIndex: -1,
      rules: {
        btnSource: { required: true, message: '请选择按钮类型！', trigger: ['blur', 'change'] },
        btnValue: { required: true, message: '请选择按钮功能！', trigger: ['blur', 'change'] },
        newName: { required: true, message: '请填写按钮名称！', trigger: ['blur'] },
        btnArgument: { required: true, message: '请选择目标环节！', trigger: ['blur', 'change'] },
        handler: { required: true, message: '请选择事件！', trigger: ['blur', 'change'] }
      },
      buttonTree: [],
      beforeOptionsNode: [
        {
          id: 'AUTO_SUBMIT',
          name: '按流程自动流转'
        }
      ],
      afterOptionsNode: [
        {
          id: '<EndFlow>',
          name: '流程结束'
        }
      ],
      userTypes: 'unit/field/task/option'
    };
  },
  watch: {
    visible: {
      handler(visible) {
        if (visible) {
          if (this.formData.configuration) {
            this.formData.configuration = JSON.parse(this.formData.configuration);
          } else {
            this.formData.configuration = {};
          }
          if (this.formData.btnSource === this.customAuthBtnSource[1]['id']) {
            // 事件处理
            this.formData.btnClassName = JSON.parse(this.formData.btnClassName);
            this.formData.eventHandler = JSON.parse(this.formData.eventHandler);
            this.setEventHandler();
          }
        }
      }
    }
  },
  computed: {
    // 按钮功能
    btnData() {
      return this.designer.diction.buttons || [];
    },
    orgVersionId() {
      return this.workFlowData.property.orgVersionId || '';
    },
    orgVersionIds() {
      return this.workFlowData.property.orgVersionIds || [];
    }
  },
  created() {
    this.getResourceButtonTree();
  },
  methods: {
    getPopupContainer(target) {
      return document.body;
    },
    changeSource(event) {
      const value = event.target.value;
      let btnRole = '';
      if (value === this.customAuthBtnSource[1]['id']) {
        // 事件处理
        if (!this.formData.btnId) {
          this.formData.btnId = generateId();
        }
        if (!this.formData.btnClassName) {
          this.formData.btnClassName = {};
        }
        if (!this.formData.eventHandler) {
          this.formData.eventHandler = { eventParams: [] };
        }
        btnRole = 'TODO;DONE';
        this.setEventHandler();
      } else {
        this.formData.btnId = null;
        this.formData.btnClassName = null;
        this.formData.eventHandler = null;
      }
      this.formData.btnRole = btnRole;
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      });
    },
    setEventHandler() {
      this.$nextTick(() => {
        const el = this.$refs.eventHandler.$el;
        const parentNode = el.querySelector('.table-header-operation').parentNode;
        parentNode.style.cssText += ';margin-left: 137px';
      });
    },
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    },
    // 关联权限按钮
    getResourceButtonTree() {
      const params = {
        args: JSON.stringify([-1, null]),
        methodName: 'getResourceButtonTree',
        serviceName: 'resourceFacadeService'
      };
      this.$axios
        .post('/json/data/services', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.buttonTree = data;
            }
          }
        });
    },
    changeOrg({ value, label, nodes }) {
      this.formData.btnOwners = label;
    },
    usersData(ids) {
      let data = [];
      if (ids) {
        each(ids.split(','), (item, index) => {
          if (item) {
            let type = this.getTypeByIndex(index);
            if (index == 0) {
              let values = ids.split(',')[1].split(';');
              each(item.split(';'), (citem, cindex) => {
                data.push({
                  value: values[cindex],
                  argValue: citem,
                  type: type
                });
              });
            } else if (index > 1) {
              each(item.split(';'), (citem, cindex) => {
                data.push({
                  value: citem,
                  argValue: citem,
                  type: type
                });
              });
            }
          }
        });
      }
      return data;
    },
    usersChange(param, { valueNodes, selectedNodes }) {
      let value = [];
      each(this.userTypes.split('/'), item => {
        if (item) {
          if (item == 'unit') {
            value.push(map(selectedNodes[item].data, 'argValue').join(';'));
          }
          value.push(map(selectedNodes[item].data, 'value').join(';'));
        }
      });
      this.formData[param] = value.join(',');
      console.log(param, this.formData[param]);
    },
    // 根据id所在位置，获取对应的type值
    getTypeByIndex(index) {
      if (index == 0 || index == 1) {
        //unit
        return 1;
      } else if (index == 2) {
        //'field'
        return 2;
      } else if (index == 4) {
        //'option'
        return 8;
      } else if (index == 3) {
        //'task'
        return 4;
      } else if (index == 5) {
        //'custom'
        return 16;
      } else if (code == 'direction') {
        // return "16";
      }
      return 32;
    },
    // 添加
    add() {
      this.formData = deepClone(this.initFormData);
      this.formData.uuid = generateId(9);
      this.currentIndex = -1;
      this.visible = true;
    },
    // 删除
    del(record, index) {
      this.buttons.splice(index, 1);
      this.emitValueChange();
    },
    // 修改
    set(record, index) {
      let data = deepClone(record);
      if (data.btnCopyUsers) {
        let btnCopyUsers = this.usersData(data.btnCopyUserIds);
        data.btnCopyUsers = JSON.parse(data.btnCopyUsers);
        if (btnCopyUsers.length != data.btnCopyUsers.length) {
          data.btnCopyUsers = btnCopyUsers;
        }
      } else {
        data.btnCopyUsers = this.usersData(data.btnCopyUserIds);
      }
      if (data.btnUsers) {
        let btnUsers = this.usersData(data.btnUserIds);
        data.btnUsers = JSON.parse(data.btnUsers);
        if (btnUsers.length != data.btnUsers.length) {
          data.btnUsers = btnUsers;
        }
      } else {
        data.btnUsers = this.usersData(data.btnUserIds);
      }
      this.formData = data;
      this.currentIndex = index;
      this.visible = true;
    },
    // 保存
    save(callback) {
      this.$refs.form.validate((valid, error) => {
        if (valid) {
          let data = deepClone(this.formData);
          if (data.btnCopyUsers && typeof data.btnCopyUsers == 'object') {
            data.btnCopyUsers = JSON.stringify(data.btnCopyUsers);
          }
          if (data.btnUsers && typeof data.btnUsers == 'object') {
            data.btnUsers = JSON.stringify(data.btnUsers);
          }
          if (data.btnSource === this.customAuthBtnSource[1]['id']) {
            // 事件处理
            data.btnClassName = JSON.stringify(data.btnClassName);
            data.eventHandler = JSON.stringify(data.eventHandler);
          }
          data.configuration = JSON.stringify(data.configuration);
          if (this.currentIndex == -1) {
            this.buttons.push(data);
          } else {
            this.buttons.splice(this.currentIndex, 1, data);
          }
          this.emitValueChange();
        }
        callback(valid);
      });
    },
    // 清空
    clear() {
      this.buttons.splice(0, this.buttons.length);
      this.emitValueChange();
    },
    emitValueChange() {
      this.$emit('input', this.buttons);
    }
  }
};
</script>
<style lang="less">
.wf-custom-buttons-modal {
  .ant-row {
    margin-bottom: 4px;
    display: flex;
    .ant-form-item-control-wrapper {
      flex: 1 1 auto;
      .ant-form-item-control {
        .ant-form-explain {
          position: relative;
          top: 0;
          right: auto;
        }
      }
    }
    .ant-form-item-label-left {
      white-space: normal;
    }
  }
  .ant-modal-body {
    padding: 12px 20px;
  }
  .org-select-component {
    padding-top: 4px;
  }
  .has-error .ant-form-explain {
    z-index: 2;
  }
}
.wf-custom-buttons {
  margin-bottom: var(--w-margin-2xs);
  .custom-btn-item {
    width: 100%;
    height: 32px;
    line-height: 32px;
    padding-left: 7px;
    background: var(--w-fill-color-light);
    margin-bottom: 2px;
    border-radius: 4px;
    .f_s_0 i {
      margin-right: var(--w-margin-2xs);
    }
  }
}
</style>
