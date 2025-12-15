<template>
  <PerfectScrollbar :style="{ height: '600px' }" ref="scroll">
    <a-form-model ref="form" :model="formData" :colon="false" :rules="rules" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
      <a-form-model-item label="消息类型" prop="type">
        <a-select
          v-model="formData.type"
          defaultValue="TODO"
          @change="typeChange"
          :showSearch="true"
          :filterOption="filterOption"
          :getPopupContainer="getPopupContainerByPs()"
          :dropdownClassName="getDropdownClassName()"
        >
          <a-select-opt-group v-for="(gitem, gindex) in flowMessageTemplateType" :key="'g_' + gindex">
            <span slot="label" class="select-group-divider">
              <label :title="gitem.text">{{ gitem.text }}</label>
            </span>
            <a-select-option v-for="(item, index) in gitem.children" :key="'o_' + index" :title="item.text" :value="item.id">
              {{ item.text }}
            </a-select-option>
          </a-select-opt-group>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="分发人员" prop="distributerElements">
        <div v-for="(item, index) in formData.distributerElements" :key="'d_' + index">
          <w-checkbox v-model="item.checked">{{ item.distributerTypeName }}</w-checkbox>
          <div class="distributer-item" v-if="item.checked == '1'">
            <template v-if="item.label.indexOf('|other') > -1">
              <a-form-model-item
                label="选择人员"
                class="user-select-form-item"
                :prop="`distributerElements:${index}:designee`"
                :name="['distributerElements', index, 'designee']"
                :rules="distributersRules.designee"
              >
                <user-select-list
                  v-model="item.designee"
                  types="unit/bizOrg/field/task/custom/filter"
                  title="选择消息分发人员"
                  text="分发人员"
                />
              </a-form-model-item>
            </template>
            <a-form-model-item
              label="消息格式"
              :prop="`distributerElements:${index}:id`"
              :name="['distributerElements', index, 'id']"
              :rules="distributersRules.id"
            >
              <w-select
                :showSearch="true"
                :options="messageFormatList"
                v-model="item.id"
                :formData="distributerElements[index]"
                formDataFieldName="name"
              ></w-select>
            </a-form-model-item>
          </div>
        </div>
      </a-form-model-item>
      <a-form-model-item label="抄送人员">
        <user-select-list
          v-model="formData.copyMsgRecipients"
          types="unit/bizOrg/field/task/custom/filter"
          title="选择消息抄送人员"
          text="抄送人员"
        />
      </a-form-model-item>
      <a-form-model-item label="分发节点">
        <div class="distributer-item">
          <div v-for="(item, index) in formData.distributionElements" :key="'di_' + index" class="flex f_y_c distribution-item">
            <div class="f_s_0" style="width: 100px; padding-right: 4px">
              <w-select :options="distributeNodeType" v-model="item.type"></w-select>
            </div>
            <div class="f_g_1">
              <template v-if="item.type == 'task'">
                <node-task-select mode="multiple" v-model="item.value"></node-task-select>
              </template>
              <template v-else-if="item.type == 'direction'">
                <edge-direction-select mode="multiple" v-model="item.value"></edge-direction-select>
              </template>
              <template v-else-if="item.type == 'jumptask'">
                <div class="flex">
                  <div class="f_s_0" style="padding: 0 4px">从</div>
                  <div class="f_g_1"><node-task-select v-model="item.jumptask[0]"></node-task-select></div>
                  <div class="f_s_0" style="padding: 0 4px">至</div>
                  <div class="f_g_1"><node-task-select v-model="item.jumptask[1]"></node-task-select></div>
                </div>
              </template>
            </div>
            <div class="f_s_0" style="width: 30px">
              <a-button type="link" size="small" @click="delDistribution(index)">
                <Icon type="pticon iconfont icon-ptkj-shanchu" />
              </a-button>
            </div>
          </div>
          <a-button type="link" size="small" @click="addDistribution" icon="plus">添加分发节点</a-button>
        </div>
      </a-form-model-item>
      <a-form-model-item label="分发条件" prop="conditionElements">
        <w-switch v-model="formData.conditionEnable" />
        <template v-if="formData.conditionEnable == '1'">
          满足以下
          <w-select
            :allowClear="false"
            :options="distributeConditionType"
            v-model="formData.condExpressionSignal"
            style="width: 100px"
          ></w-select>
          条件时分发消息
          <div class="distributer-item">
            <div v-for="(item, index) in formData.conditionElements" :key="'di_' + index" class="flex f_y_c distribution-item">
              <div class="f_s_0" style="width: 120px; margin-right: var(--w-margin-3xs)">
                <w-select :options="conditionsTypes" v-model="item.type" @change="valiConditions"></w-select>
              </div>
              <div class="f_g_1">
                <div class="flex">
                  <template v-if="item.type == 'formField'">
                    <div class="f_g_1" style="padding-right: 4px">
                      <main-dyform-fields-select v-model="item.code" @change="valiConditions"></main-dyform-fields-select>
                    </div>
                    <div class="f_s_0" style="width: 120px; padding-right: 4px">
                      <w-select :options="logicalOperators" v-model="item.symbols" @change="valiConditions"></w-select>
                    </div>
                  </template>
                  <template v-if="item.type == 'userComment'">
                    <div class="f_s_0" style="width: 120px; padding-right: 4px">
                      <w-select :options="logicalOperatorsUserComment" v-model="item.symbols" @change="valiConditions"></w-select>
                    </div>
                  </template>
                  <div class="f_s_0" style="width: 150px"><a-input v-model="item.value" :allowClear="true" @blur="valiConditions" /></div>
                </div>
              </div>
              <div class="f_s_0" style="width: 30px">
                <a-button type="link" size="small" @click="delCondition(index)">
                  <Icon type="pticon iconfont icon-ptkj-shanchu" />
                </a-button>
              </div>
            </div>
            <a-button type="link" size="small" @click="addCondition" icon="plus">添加分发条件</a-button>
          </div>
        </template>
      </a-form-model-item>
      <a-form-model-item label="是否启用">
        <w-switch v-model="formData.isSendMsg" />
      </a-form-model-item>
    </a-form-model>
  </PerfectScrollbar>
</template>

<script>
import { messageTemplateTypes, distributeNodeType, distributeConditionType, logicalOperators, levelOperateors } from '../designer/constant';
import { deepClone } from '@framework/vue/utils/util';
import { filter, map, each, findIndex, some } from 'lodash';
import WSwitch from '../components/w-switch.js';
import WSelect from '../components/w-select';
import WTreeSelect from '../components/w-tree-select';
import WCheckbox from '../components/w-checkbox.js';
import UserSelectList from '../commons/user-select-list.vue';
import NodeTaskSelect from '../commons/node-task-select';
import EdgeDirectionSelect from '../commons/edge-direction-select';
import mainDyformFieldsSelect from '../commons/main-dyform-fields-select';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';

export default {
  name: 'MessageTemplates',
  inject: ['designer'],
  props: {
    data: {
      type: Object,
      default: () => {
        return {};
      }
    },
    orgVersionId: {
      type: String
    }
  },
  components: {
    WSelect,
    WTreeSelect,
    WCheckbox,
    UserSelectList,
    NodeTaskSelect,
    EdgeDirectionSelect,
    WSwitch,
    mainDyformFieldsSelect
  },
  data() {
    let distributersValidate = (rule, value, callback) => {
      let data = filter(value, { checked: '1' });
      if (data.length == 0) {
        callback(new Error('请选择分发人员！'));
      } else {
        callback();
      }
    };
    let conditionsValidate = (rule, value, callback) => {
      let nopass = some(value, function (item, index) {
        if (item.type == 'formField') {
          if (!(item.code && item.symbols) || (item.symbols && !(item.symbols == '10' || item.symbols == '11') && !item.value)) {
            return true;
          }
        } else if (item.type == 'userComment') {
          if (!item.symbols || (item.symbols && !(item.symbols == '10' || item.symbols == '11') && !item.value)) {
            return true;
          }
        } else {
          return true;
        }
      });
      if (this.formData.conditionEnable == '1' && nopass) {
        callback(new Error('请完善分发条件！'));
        this.conditionsNopass = true;
      } else {
        callback();
        this.conditionsNopass = false;
      }
    };

    let formData = deepClone(this.data);
    let distributerElements = this.getDistributers(formData);
    formData.distributerElements = distributerElements;
    // 抄送人员
    if (!formData.copyMsgRecipients) {
      formData.copyMsgRecipients = [];
    }
    // 分发节点
    if (!formData.distributionElements) {
      formData.distributionElements = [];
    }
    each(formData.distributionElements, item => {
      if (item.type == 'jumptask') {
        item.jumptask = ['', ''];
        if (item.value) {
          let value = item.value.split(/;|,|；|，/);
          if (value[0]) {
            item.jumptask[0] = value[0];
          }
          if (value[1]) {
            item.jumptask[1] = value[1];
          }
        }
      }
    });
    // 分发条件
    if (formData.conditionEnable === undefined || formData.conditionEnable === null) {
      formData.conditionEnable = '0';
      formData.condExpressionSignal = 'and';
      formData.conditionElements = [];
    }
    if (formData.isSendMsg === undefined || formData.isSendMsg === null) {
      formData.isSendMsg = '1';
    }
    return {
      distributersValidate,
      conditionsValidate,
      logicalOperators,
      distributeNodeType,
      distributeConditionType,
      formData,
      messageFormatList: [],
      flowMessageTemplateType: [],
      distributerElements,
      conditionsNopass: false,
      distributersRules: {
        id: { required: true, message: '请选择消息格式！', trigger: ['blur', 'change'] },
        designee: { required: true, message: '请选择分发人员！', trigger: ['blur', 'change'] }
      }
    };
  },
  computed: {
    conditionsTypes() {
      let selectData = [
        {
          id: 'formField',
          text: '表单字段值'
        }
      ];
      let typeVal = this.formData.type; //消息类型
      if (
        ['TASK_SUBMIT_NOTIFY', 'TASK_TRANSFER_NOTIFY', 'TASK_COUNTERSIGN_NOTIFY', 'TASK_ADD_SIGN_NOTIFY', 'TASK_RETURN_NOTIFY'].indexOf(
          typeVal
        ) > -1
      ) {
        selectData.push({
          id: 'userComment',
          text: '用户意见内容'
        });
      }
      return selectData;
    },
    logicalOperatorsUserComment() {
      let operators = [];
      each(logicalOperators, item => {
        // 等于 不等于 包含 不包含
        if (['10', '11', '14', '15'].indexOf(item.id) > -1) {
          operators.push(item);
        }
      });
      return operators;
    },
    rules() {
      let rules = {
        type: { required: true, message: '请选择消息类型！', trigger: ['blur', 'change'] },
        distributerElements: { required: true, validator: this.distributersValidate, trigger: ['blur', 'change'] }
      };
      if (this.formData.conditionEnable == '1') {
        rules.conditionElements = { required: true, validator: this.conditionsValidate, trigger: ['blur', 'change'] };
      }
      return rules;
    }
  },
  filters: {},
  created() {
    this.getMessageTemplates();
    this.getSelectFlowMessageTemplateType();
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    getLabel(text) {
      if (text.indexOf('|') > -1) {
        return text.split('|')[0];
      }
      return text;
    },
    filterOption(inputValue, option) {
      if (option.componentOptions.tag == 'a-select-option') {
        return option.componentOptions.propsData.title.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0;
      }
      return false;
    },
    // 获取消息格式选项
    getMessageTemplates() {
      if (this.messageFormatList.length == 0) {
        $axios.get('/api/workflow/definition/getMessageTemplates', { params: {} }).then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.messageFormatList = map(data.data, function (item) {
              return {
                id: item.id,
                text: item.name
              };
            });
          }
        });
      }
    },
    // 获取消息类型
    getSelectFlowMessageTemplateType() {
      if (this.flowMessageTemplateType.length == 0) {
        $axios.get('/api/workflow/definition/getSelectFlowMessageTemplateType', { params: {} }).then(({ data }) => {
          if (data.code == 0 && data.data.results) {
            this.flowMessageTemplateType = data.data.results;
          }
        });
      }
    },
    // 分发人员
    getDistributers(data) {
      let distributers = data.distributerElements || [];
      if (data.type) {
        return map(messageTemplateTypes[data.type], (item, index) => {
          let label = this.getLabel(item.label);
          let checked = '0';
          let idx = -1;
          if (distributers.length) {
            // 编辑
            idx = findIndex(distributers, { distributerTypeName: label });
            if (idx > -1) {
              checked = '1';
            }
          } else {
            // 新建
            if (
              label !== '指定人员' ||
              [
                'TASK_SUBMIT_NOTIFY',
                'TASK_TRANSFER_NOTIFY',
                'TASK_COUNTERSIGN_NOTIFY',
                'TASK_ADD_SIGN_NOTIFY',
                'TASK_RETURN_NOTIFY'
              ].indexOf(data.type) > -1
            ) {
              //环节提交\转办\会签\加签\退回消息通知的分发人员默认选中
              checked = '1';
            }
          }
          return {
            label: item.label,
            checked,
            distributerTypeName: label,
            id: idx > -1 ? distributers[idx].id : item.defaultValue,
            name: idx > -1 ? distributers[idx].name : '',
            designee: idx > -1 ? distributers[idx].designee : []
          };
        });
      }
    },
    // 消息类型
    typeChange(value, option) {
      this.formData.distributerElements = this.getDistributers({
        type: value
      });
      if (value) {
        this.formData.typeName = option.componentOptions.propsData.title;
      } else {
        this.formData.typeName = '';
      }
    },
    delDistribution(index) {
      this.formData.distributionElements.splice(index, 1);
    },
    addDistribution() {
      this.formData.distributionElements.push({
        type: '',
        value: '',
        jumptask: ['', '']
      });
    },
    delCondition(index) {
      this.formData.conditionElements.splice(index, 1);
    },
    addCondition() {
      this.formData.conditionElements.push({
        type: '',
        code: '',
        symbols: '',
        value: ''
      });
    },
    valiConditions() {
      if (this.conditionsNopass) {
        this.$refs.form.validateField('conditions');
      }
    },
    save(callback) {
      this.$refs.form.validate((valid, error) => {
        let data = deepClone(this.formData);
        if (valid) {
          // 分发节点
          each(data.distributionElements, item => {
            if (item.type == 'jumptask') {
              item.value = item.jumptask.join(';');
            }
          });
          data.distributerElements = filter(data.distributerElements, item => {
            return item.checked == '1';
          });
          // console.log(data);
        }
        callback({ valid, error, data: data });
      });
    }
  }
};
</script>
<style lang="less" scope>
.flow-timer-modal-wrap {
  .distributer-item {
    border: 1px dashed var(--w-border-color-light);
    border-radius: var(--w-border-radius-base);
    padding-top: var(--w-padding-3xs);
  }
  .distribution-item {
    background: var(--w-fill-color-light);
    border-radius: var(--w-border-radius-base);
    margin: 0 var(--w-margin-3xs) var(--w-margin-2xs);
    padding-left: var(--w-padding-3xs);
  }
  .org-select-component {
    padding-top: 4px;
  }
}
</style>
