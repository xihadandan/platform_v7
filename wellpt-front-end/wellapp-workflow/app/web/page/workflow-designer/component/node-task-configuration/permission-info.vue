<template>
  <div class="permission-info-wrapper">
    <div class="permission-info-table-wrapper">
      <a-table
        class="form-settings-table permission-info-table"
        rowKey="value"
        :pagination="false"
        size="small"
        :columns="columns"
        :dataSource="dataSource"
        style="margin-bottom: 10px"
      >
        <template slot="title">操作设置</template>
        <template slot="nameSlot" slot-scope="text, record">
          <!-- <a-icon type="menu" class="drag-handler" :style="{ cursor: 'move' }" /> -->
          <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-handler" :style="{ cursor: 'move' }" />
          <span>{{ text }}</span>
        </template>
        <template slot="titleSlot" slot-scope="text, record, index">
          <div class="permission-display-title">
            <template v-if="record.displayState === 'label'">
              <div class="action-title">{{ text }}</div>
            </template>
            <template v-else>
              <a-input v-model="record.title" @blur="blurTitle(index)" :autoFocus="true" />
            </template>
            <a-button type="link" size="small" @click="handleEditTitle(index, $event)">
              <Icon type="pticon iconfont icon-ptkj-bianji" />
            </a-button>
            <w-i18n-input :target="record" :code="record.uuid" v-model="record.title" v-show="record.name != record.title" />
          </div>
        </template>
        <template slot="visibleSlot" slot-scope="text, record">
          <w-switch v-model="record.defaultVisible" :checkedValue="true" :unCheckedValue="false" @change="changeVisible" />
        </template>
      </a-table>
    </div>
    <template v-if="rightCodeMap['B004002'] && rightCodeMap['B004002']['defaultVisible']">
      <a-form-model-item class="form-item-vertical" label="提交设置">
        <div>
          <w-checkbox v-model="rightConfig.requiredSubmitOpinion" :checkedValue="true" :unCheckedValue="false">提交必填意见</w-checkbox>
        </div>
        <div>
          <w-checkbox v-model="rightConfig.printAfterSubmit" :checkedValue="true" :unCheckedValue="false" @change="changeSubmitPrint">
            <span>提交自动套打</span>
            <span style="color: var(--w-gray-color-7)">（提交时将自动套打并下载套打结果）</span>
          </w-checkbox>
        </div>
        <div class="permission-unset-print" v-if="showUnsetPrint">当前环节或流程未预设套打模板</div>
      </a-form-model-item>
    </template>
    <!-- 退回设置 -->
    <template v-if="rightCodeMap['B004003'] && rightCodeMap['B004003']['defaultVisible']">
      <a-form-model-item class="form-item-vertical" label="退回设置">
        <w-checkbox v-model="rightConfig.requiredRollbackOpinion" :checkedValue="true" :unCheckedValue="false">退回必填意见</w-checkbox>
        <div>跨环节退回时</div>
        <a-radio-group v-model="rightConfig.submitModeOfAfterRollback" size="small" button-style="solid">
          <a-radio-button v-for="item in submitModeOfAfterRollback" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio-button>
        </a-radio-group>
        <template v-if="rightConfig.submitModeOfAfterRollback === submitModeOfAfterRollback[2]['value']">
          <div class="permission-copyToSkipTask">
            <w-checkbox v-model="rightConfig.copyToSkipTask" :checkedValue="true" :unCheckedValue="false">
              直接提交至本环节时抄送跳过的环节
            </w-checkbox>
          </div>
        </template>
      </a-form-model-item>
    </template>
    <template v-if="rightCodeMap['B004005'] && rightCodeMap['B004005']['defaultVisible']">
      <a-form-model-item class="form-item-vertical" label="撤回设置">
        <w-checkbox v-model="rightConfig.requiredCancelOpinion" :checkedValue="true" :unCheckedValue="false">撤回必填意见</w-checkbox>
      </a-form-model-item>
    </template>
    <!-- 转办设置 -->
    <template v-if="rightCodeMap['B004006'] && rightCodeMap['B004006']['defaultVisible']">
      <a-form-model-item class="form-item-vertical" label="转办设置">
        <w-checkbox v-model="rightConfig.requiredTransferOpinion" :checkedValue="true" :unCheckedValue="false">转办必填意见</w-checkbox>
        <div>转办人员设置</div>
        <a-radio-group v-model="rightConfig.isSetTransferUser" size="small" :options="isSetTransferUser" />
        <template v-if="rightConfig.isSetTransferUser === isSetTransferUser[1]['value']">
          <user-select-list
            v-model="rightConfig.transferUsers"
            types="unit/bizOrg/field/task/custom/filter"
            title="指定转办范围"
            style="width: 100%"
            text="转办人"
          />
        </template>
        <div>转办表单权限</div>
        <a-radio-group v-model="rightConfig.transferViewFormMode" size="small" button-style="solid">
          <a-radio-button v-for="item in transferViewFormMode" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio-button>
        </a-radio-group>
        <div>转办操作权限</div>
        <a-radio-group v-model="rightConfig.transferOperateRight" size="small" button-style="solid">
          <a-radio-button v-for="item in transferOperateRight" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio-button>
        </a-radio-group>
      </a-form-model-item>
    </template>
    <!-- 会签设置 -->
    <template v-if="rightCodeMap['B004007'] && rightCodeMap['B004007']['defaultVisible']">
      <a-form-model-item class="form-item-vertical" label="会签设置">
        <w-checkbox v-model="rightConfig.requiredCounterSignOpinion" :checkedValue="true" :unCheckedValue="false">会签必填意见</w-checkbox>
        <div>会签人员设置</div>
        <a-radio-group v-model="rightConfig.isSetCounterSignUser" size="small" :options="isSetCounterSignUser" />
        <template v-if="rightConfig.isSetCounterSignUser === isSetCounterSignUser[1]['value']">
          <user-select-list
            v-model="rightConfig.counterSignUsers"
            types="unit/bizOrg/field/task/custom/filter"
            title="指定会签范围"
            style="width: 100%"
            text="会签人"
          />
        </template>
        <div>会签表单权限</div>
        <a-radio-group v-model="rightConfig.counterSignViewFormMode" size="small" button-style="solid">
          <a-radio-button v-for="item in counterSignViewFormMode" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio-button>
        </a-radio-group>
        <div>会签操作权限</div>
        <a-radio-group v-model="rightConfig.counterSignOperateRight" size="small" button-style="solid">
          <a-radio-button v-for="item in counterSignOperateRight" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio-button>
        </a-radio-group>
      </a-form-model-item>
    </template>
    <!-- 加签设置 -->
    <template v-if="rightCodeMap['B004042'] && rightCodeMap['B004042']['defaultVisible']">
      <a-form-model-item class="form-item-vertical" label="加签设置">
        <w-checkbox v-model="rightConfig.requiredAddSignOpinion" :checkedValue="true" :unCheckedValue="false">加签必填意见</w-checkbox>
        <div>加签人员设置</div>
        <a-radio-group v-model="rightConfig.isSetAddSignUser" size="small" :options="isSetAddSignUser" />
        <template v-if="rightConfig.isSetAddSignUser === isSetAddSignUser[1]['value']">
          <user-select-list
            v-model="rightConfig.addSignUsers"
            types="unit/bizOrg/field/task/custom/filter"
            title="指定加签范围"
            style="width: 100%"
            text="加签人"
          />
        </template>
        <div>加签表单权限</div>
        <a-radio-group v-model="rightConfig.addSignViewFormMode" size="small" button-style="solid">
          <a-radio-button v-for="item in addSignViewFormMode" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio-button>
        </a-radio-group>
        <div>加签操作权限</div>
        <a-radio-group v-model="rightConfig.addSignOperateRight" size="small" button-style="solid">
          <a-radio-button v-for="item in addSignOperateRight" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio-button>
        </a-radio-group>
      </a-form-model-item>
    </template>
    <template v-if="rightCodeMap['B004014'] && rightCodeMap['B004014']['defaultVisible']">
      <a-form-model-item class="form-item-vertical" label="催办设置">
        <w-checkbox v-model="rightConfig.requiredRemindOpinion" :checkedValue="true" :unCheckedValue="false">催办必填意见</w-checkbox>
      </a-form-model-item>
    </template>
    <template v-if="rightCodeMap['B004015'] && rightCodeMap['B004015']['defaultVisible']">
      <a-form-model-item class="form-item-vertical" label="移交设置">
        <w-checkbox v-model="rightConfig.requiredHandOverOpinion" :checkedValue="true" :unCheckedValue="false">移交必填意见</w-checkbox>
      </a-form-model-item>
    </template>
    <template v-if="rightCodeMap['B004016'] && rightCodeMap['B004016']['defaultVisible']">
      <a-form-model-item class="form-item-vertical" label="跳转设置">
        <w-checkbox v-model="rightConfig.requiredGotoTaskOpinion" :checkedValue="true" :unCheckedValue="false">跳转必填意见</w-checkbox>
      </a-form-model-item>
    </template>
    <template v-if="rightCodeMap['B004010'] && rightCodeMap['B004010']['defaultVisible']">
      <a-form-model-item class="form-item-vertical" label="抄送设置">
        <div>抄送人员设置</div>
        <a-radio-group v-model="rightConfig.isSetCopyUser" size="small" :options="isSetCopyUser" />
        <template v-if="rightConfig.isSetCopyUser === isSetCopyUser[1]['value']">
          <user-select-list
            v-model="rightConfig.copyUsers"
            types="unit/bizOrg/field/task/custom/filter"
            title="指定抄送范围"
            style="width: 100%"
            text="抄送人"
          />
        </template>
      </a-form-model-item>
    </template>
  </div>
</template>

<script>
/*
移交是特送个人
跳转是特送环节

*/
import {
  submitModeOfAfterRollback,
  isSetTransferUser,
  isSetCounterSignUser,
  isSetAddSignUser,
  isSetCopyUser,
  transferViewFormMode,
  transferOperateRight,
  counterSignViewFormMode,
  counterSignOperateRight,
  addSignViewFormMode,
  addSignOperateRight
} from '../designer/constant';
import WSwitch from '../components/w-switch';
import WCheckbox from '../components/w-checkbox';
import UserSelectList from '../commons/user-select-list.vue';
import mixins from '../mixins';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
import { generateId, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'PermissionInfo',
  inject: ['workFlowData'],
  mixins: [mixins],
  props: {
    dataSource: {
      type: Array,
      default: () => []
    },
    rightConfig: {
      type: Object,
      default: () => {}
    },
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WSwitch,
    WCheckbox,
    UserSelectList,
    WI18nInput
  },
  data() {
    for (let d of this.dataSource) {
      if (d.uuid) {
        let uuid, uuidHaveId; // 是否正确uuid
        if (d.uuid.indexOf(`_${this.formData.id}_`) === -1) {
          uuidHaveId = false;
          uuid = d.value + '_' + this.formData.id + '_' + generateId(4).toUpperCase();
          d.uuid = uuid;
        } else {
          uuidHaveId = true;
          uuid = d.uuid;
        }

        if (d.i18n) {
          for (const lang in d.i18n) {
            let i18n = d.i18n[lang][uuid];
            let _temp = {};
            if (i18n) {
              _temp[uuid] = d.i18n[lang][uuid];
            } else {
              let firstCode = Object.keys(d.i18n[lang])[0];
              _temp[uuid] = d.i18n[lang][firstCode];
            }
            d.i18n[lang] = _temp;
          }
        }

        continue;
      }
      d.uuid = d.value + '_' + this.formData.id + '_' + generateId(4).toUpperCase();
    }
    return {
      submitModeOfAfterRollback,
      isSetTransferUser,
      isSetCounterSignUser,
      isSetAddSignUser,
      isSetCopyUser,
      transferViewFormMode,
      transferOperateRight,
      counterSignViewFormMode,
      counterSignOperateRight,
      addSignViewFormMode,
      addSignOperateRight,
      showUnsetPrint: false,
      columns: [
        { title: '操作名称', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        { title: '显示名称', dataIndex: 'title', width: 220, scopedSlots: { customRender: 'titleSlot' } }, // 能修改的名称
        {
          title: '开启',
          dataIndex: 'visible',
          width: 80,
          scopedSlots: { customRender: 'visibleSlot' }
          // align: 'left'
        }
      ]
    };
  },
  computed: {
    rightCodeMap() {
      let rightCodeMap = {};
      if (this.dataSource) {
        this.dataSource.forEach(item => {
          const code = item.value;
          const defaultVisible = item.defaultVisible;
          rightCodeMap[code] = {
            code,
            defaultVisible
          };
        });
      }
      return rightCodeMap;
    }
  },
  mounted() {
    this.draggable(this.dataSource, this.$el.querySelector('.ant-table-tbody'), '.drag-handler');
  },
  methods: {
    changeSubmitPrint(event) {
      const checked = event.target.checked;
      if (checked) {
        if (!this.workFlowData.property.printTemplateUuid && !this.formData.printTemplateUuid) {
          this.showUnsetPrint = true;
        }
      } else {
        this.showUnsetPrint = false;
      }
    },
    // 更改开启状态
    changeVisible(checked) {},
    handleEditTitle(index, event) {
      // this.dataSource.forEach(item => {
      //   item['displayState'] = 'label';
      // });
      this.dataSource[index]['displayState'] = 'edit';
      // 手动聚焦
      // this.$nextTick(() => {
      //   event.target.previousElementSibling.focus();
      // });
    },
    blurTitle(index) {
      this.dataSource[index]['displayState'] = 'label';
    }
  }
};
</script>
