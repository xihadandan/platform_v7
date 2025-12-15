<template>
  <!-- 环节属性-基本设置 -->
  <div>
    <a-form-model-item prop="name" label="名称">
      <a-input v-model.trim="formData.name" @blur="changeName">
        <template slot="addonAfter">
          <w-i18n-input :target="formData" :code="formData.id + '.taskName'" v-model="formData.name" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item prop="id" label="ID" :wrapper-col="{ style: { textAlign: 'left' } }">
      <template v-if="canEditId">
        <a-input v-model="formData.id" @change="changeId" @blur="blurId" />
      </template>
      <template v-else>
        {{ formData.id }}
      </template>
    </a-form-model-item>
    <a-form-model-item prop="code" label="编号">
      <a-input v-model="formData.code" />
    </a-form-model-item>
    <template v-if="formData.type === '3'">
      <!-- 协作节点 -->
      <a-form-model-item
        prop="decisionMakers"
        label="决策人设置"
        class="form-item-vertical user-select-form-item"
        :wrapper-col="{ style: { textAlign: 'left' } }"
      >
        <user-select-list
          v-model="formData.decisionMakers"
          types="unit/bizOrg/field/task/custom/filter"
          title="指定决策人"
          text="决策人"
          :orgVersionId="orgVersionId"
          displayType="merge"
          style="width: 100%; vertical-align: middle"
        />
      </a-form-model-item>
    </template>
    <a-form-model-item label="办理人设置">
      <a-radio-group v-model="formData.isSetUser" size="small" button-style="solid">
        <a-radio-button v-for="item in isSetUserConfig" :key="item.value" :value="item.value">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <template v-if="formData.isSetUser === '1'">
      <a-form-model-item prop="users" label="" class="user-select-form-item" :wrapper-col="{ style: { textAlign: 'left' } }">
        <user-select-list
          v-model="formData.users"
          types="unit/bizOrg/field/task/custom/filter"
          title="指定办理人"
          :cellData="formData"
          setKey="users"
          style="width: 100%"
        />
      </a-form-model-item>
    </template>
    <more-show-component>
      <a-form-model-item class="form-item-vertical" label="找不到办理人时">
        <w-select :options="isSetUserEmptyConfig" v-model="formData.isSetUserEmpty" />
        <template v-if="formData.isSetUserEmpty !== '0'">
          <template v-if="formData.isSetUserEmpty == '1'">
            <w-select
              v-model="formData.emptyToTask"
              :options="taskList"
              :replaceFields="{
                title: 'name',
                key: 'id',
                value: 'id'
              }"
            />
          </template>
          <template v-else-if="formData.isSetUserEmpty == '2'">
            <user-select-list
              v-model="formData.emptyToUsers"
              types="unit/bizOrg/field/task/custom/filter"
              title="选择其他办理人"
              :orgVersionId="orgVersionId"
              style="width: 100%"
            />
          </template>
          <w-checkbox v-model="formData.emptyNoteDone" unCheckedValue="">办理人为空提交时消息通知已办理人</w-checkbox>
        </template>
      </a-form-model-item>
      <a-form-model-item class="form-item-vertical" label="存在多个办理人时">
        <w-checkbox v-model="formData.isSelectAgain" unCheckedValue="">由前一环节办理人选择具体办理人</w-checkbox>
        <template v-if="formData.isSelectAgain === '1'">
          <div class="second-checkbox">
            <w-checkbox v-model="formData.isOnlyOne" unCheckedValue="">只能选择一个办理人</w-checkbox>
          </div>
        </template>
        <w-checkbox v-model="formData.isAnyone" unCheckedValue="">只需要一个人办理完成即进入下一环节</w-checkbox>
        <w-checkbox v-model="formData.isByOrder" unCheckedValue="">按人员顺序依次办理</w-checkbox>
      </a-form-model-item>
      <a-form-model-item class="form-item-vertical" label="与前一环节办理人相同时">
        <w-select :options="sameUserSubmitConfig" v-model="formData.sameUserSubmit" />
      </a-form-model-item>
    </more-show-component>
    <!-- <a-form-model-item label="转办人员设置">
      <a-radio-group v-model="formData.isSetTransferUser" size="small" button-style="solid">
        <a-radio-button v-for="item in isSetTransferUserConfig" :key="item.value" :value="item.value">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <template v-if="formData.isSetTransferUser === '1'">
      <a-form-model-item prop="transferUsers" label="" class="user-select-form-item" :wrapper-col="{ style: { textAlign: 'left' } }">
        <user-select-list
          v-model="formData.transferUsers"
          types="unit/bizOrg/field/task/custom/filter"
          title="指定转办范围"
          :orgVersionId="orgVersionId"
          displayType="merge"
          style="width: 100%"
          text="转办人"
        />
      </a-form-model-item>
    </template> -->
    <a-form-model-item label="抄送设置">
      <w-switch v-model="formData.isSetCopyUser" checkedValue="1;2" @change="arg => switchChange(arg, 'isSetCopyUser')" />
    </a-form-model-item>
    <template v-if="formData.isSetCopyUser !== '0'">
      <div class="ant-form-item">
        <div class="set-copy-container">
          <a-radio-group v-model="formData.isSetCopyUser" class="set-copy-radio" @change="arg => switchChange(arg, 'isSetCopyUser')">
            <a-radio value="2">由前一环节办理人指定</a-radio>
            <a-radio value="1">指定抄送人</a-radio>
          </a-radio-group>
          <template v-if="formData.isSetCopyUser === '1'">
            <a-form-model-item
              prop="copyUsers"
              label=""
              class="user-select-form-item"
              style="padding: 0"
              :wrapper-col="{ style: { textAlign: 'left' } }"
            >
              <user-select-list
                v-model="formData.copyUsers"
                types="unit/bizOrg/field/task/custom/filter"
                title="指定抄送人"
                :orgVersionId="orgVersionId"
                displayType="merge"
                style="width: 100%"
                text="抄送人"
              />
              <w-checkbox v-model="formData.isConfirmCopyUser" unCheckedValue="">前一环节办理人可二次选择抄送人</w-checkbox>
            </a-form-model-item>
          </template>
          <div class="set-copy-label" style="margin-top: var(--w-margin-2xs)">抄送前置条件</div>
          <a-textarea
            v-model="formData.copyUserCondition"
            :rows="4"
            :style="{
              height: 'auto'
            }"
          />
          <div>
            环节变量使用${taskId}、${fromTaskId}、${toTaskId}，
            动态表单变量使用${dyform.表单字段}。变量支持&gt;、&gt;=、&lt;、&lt;=、==、!=、contains、not
            contains等操作。多个条件用逻辑与&amp;&amp;、逻辑或||、左右括号等组合。
          </div>
        </div>
      </div>
    </template>
    <a-form-model-item label="督办设置">
      <w-switch v-model="formData.isSetMonitor" checkedValue="1;2" @change="arg => switchChange(arg, 'isSetMonitor')" />
    </a-form-model-item>
    <template v-if="formData.isSetMonitor !== '0'">
      <div class="ant-form-item">
        <div class="set-copy-container">
          <a-radio-group v-model="formData.isSetMonitor" class="set-copy-radio" @change="arg => switchChange(arg, 'isSetMonitor')">
            <a-radio value="2">由前一环节办理人指定</a-radio>
            <a-radio value="1">指定督办人</a-radio>
          </a-radio-group>
          <template v-if="formData.isSetMonitor === '1'">
            <a-form-model-item
              prop="monitors"
              label=""
              class="user-select-form-item"
              style="padding: 0"
              :wrapper-col="{ style: { textAlign: 'left' } }"
            >
              <user-select-list
                v-model="formData.monitors"
                types="unit/bizOrg/field/task/custom/filter"
                title="指定督办人"
                :orgVersionId="orgVersionId"
                displayType="merge"
                style="width: 100%"
                text="督办人"
              />
            </a-form-model-item>
          </template>
        </div>
      </div>
    </template>
    <a-form-model-item label="移动端可用" :label-col="{ span: 7 }" :wrapper-col="{ span: 16, style: { textAlign: 'right' } }">
      <w-switch v-model="formData.isAllowApp" checkedValue="" />
    </a-form-model-item>
  </div>
</template>

<script>
import { debounce } from 'lodash';
import constant, { isSetUserConfig, isSetTransferUserConfig, isSetUserEmptyConfig, sameUserSubmitConfig } from '../designer/constant';
import WSwitch from '../components/w-switch.js';
import WCheckbox from '../components/w-checkbox.js';
import UserSelect from '../commons/user-select.vue';
import MoreShowComponent from '../commons/more-show-component.vue';
import WSelect from '../components/w-select';
import UserSelectList from '../commons/user-select-list.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'TaskPropertyBaseInfo',
  inject: ['designer', 'workFlowData', 'graph'],
  props: {
    graphItem: {
      type: Object,
      default: () => {}
    },
    formData: {
      type: Object,
      default: () => {}
    },
    rules: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WSwitch,
    WCheckbox,
    UserSelect,
    MoreShowComponent,
    WSelect,
    UserSelectList,
    WI18nInput
  },
  data() {
    let isSetCopyUser = this.formData.isSetCopyUser;
    let isSetMonitor = this.formData.isSetMonitor;
    const oldNodeDataId = this.formData.id;
    return {
      oldNodeDataId,
      isSetUserConfig,
      isSetTransferUserConfig,
      isSetUserEmptyConfig,
      sameUserSubmitConfig,
      isSetCopyUser,
      isSetMonitor,
      tasksData: [],
      canEditId: false, // id是否能编辑
      selectedCellId: null
    };
  },
  computed: {
    taskList() {
      let taskList = [];
      if (this.graph.instance) {
        taskList = [...this.graph.instance.tasksData];
      }
      taskList.push({
        name: '流程结束',
        id: constant.EndFlowId
      });
      return taskList;
    },
    orgVersionId() {
      return this.workFlowData.property.orgVersionId || '';
    }
  },
  created() {
    this.setCanEditId();
    // 排除当前节点id, 不能动态取
    this.tasksData = this.graph.instance.tasksData.filter(item => item.id !== this.formData.id);
    this.setSelectedCellId();
  },
  methods: {
    setSelectedCellId() {
      this.selectedCellId = this.graph.instance.selectedId;
    },
    switchChange(arg, param) {
      let value = arg;
      if (value == '1') {
        this.formData[param] = this[param] == '0' ? '2' : this[param];
      } else if (arg.target) {
        value = arg.target.value;
        this[param] = value;
      }
    },
    // 更改名称
    changeName: function (event) {
      const value = event.target.value;
      if (this.graph.instance) {
        this.graph.instance.setEdgesLablesByName(value, this.selectedCellId);
      }
    },
    changeNameOld: debounce(function (event) {
      let error = false;
      const value = event.target.value;
      if (!value) {
        error = true;
      }
      if (value && !error) {
        const taskData = this.tasksData.find(item => {
          return item.name === value;
        });
        if (taskData) {
          error = true;
        }
      }
      if (error) {
        this.formData.name = '';
        this.$message.error(this.rules['name']['msg']); // 不能和message相同的key
      }
    }, 300),
    // 更改环节id
    changeId: debounce(function (event) {
      let error = false;
      const value = event.target.value;
      if (!value) {
        error = true;
      }
      if (value && !error) {
        const taskData = this.tasksData.find(item => {
          return item.id === value;
        });
        if (taskData) {
          error = true;
        }
      }
      if (error) {
        this.formData.id = '';
        this.$message.error(this.rules['id']['msg']);
      }
    }, 300),
    blurId(event) {
      const value = event.target.value;
      if (value !== this.oldNodeDataId) {
        // 更改环节id后修改流向toID、fromID
        this.graph.instance.updateNodeDataId({
          nodeDataId: value,
          oldNodeDataId: this.oldNodeDataId
        });
        this.oldNodeDataId = value;
      }
    },
    // 设置id是否能编辑
    setCanEditId() {
      const cell = this.graph.instance.getSelectedCell();
      const hasIndex = this.graph.instance.newTasksUnEdited.findIndex(t => t.id === cell.id);
      if (hasIndex > -1) {
        this.graph.instance.newTasksUnEdited.splice(hasIndex, 1);
        this.graph.instance.newTasksEdited.push(cell);
        this.canEditId = true;
      }
    }
  }
};
</script>
