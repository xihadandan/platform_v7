<template>
  <div class="action-rights">
    <a-alert
      message="配置流程操作在工作流中生效的角色，以及是否默认显示操作。例如提交操作，仅在发起、待办两个角色下默认显示"
      type="info"
      show-icon
      closable
    />
    <p />
    <a-table
      rowKey="id"
      :showHeader="true"
      size="small"
      :pagination="false"
      :bordered="false"
      :columns="rightTableColumns"
      :data-source="setting.rights"
      :class="['widget-table-button-table no-border']"
      :scroll="{ y: 'calc(100vh - 360px)' }"
    >
      <template slot="startSlot" slot-scope="text, record, index">
        <RightConfig :config="record.start" :code="record.code"></RightConfig>
      </template>
      <template slot="todoSlot" slot-scope="text, record, index">
        <RightConfig :config="record.todo" :code="record.code"></RightConfig>
      </template>
      <template slot="doneSlot" slot-scope="text, record, index">
        <RightConfig :config="record.done" :code="record.code"></RightConfig>
      </template>
      <template slot="superviseSlot" slot-scope="text, record, index">
        <RightConfig :config="record.supervise" :code="record.code"></RightConfig>
      </template>
      <template slot="monitorSlot" slot-scope="text, record, index">
        <RightConfig :config="record.monitor" :code="record.code"></RightConfig>
      </template>
      <template slot="copyToSlot" slot-scope="text, record, index">
        <RightConfig :config="record.copyTo" :code="record.code"></RightConfig>
      </template>
      <template slot="viewerSlot" slot-scope="text, record, index">
        <RightConfig :config="record.viewer" :code="record.code"></RightConfig>
      </template>
    </a-table>
  </div>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import { isEmpty } from 'lodash';

const SIGN_OPINION_RIGHT_CODES = [
  'B004002', // 必须签署意见
  'B004006', // 转办必填意见
  'B004007', // 会签必填意见
  'B004003', // 退回必填意见
  'B004004', // 直接退回必填意见
  'B004035', // 退回主流程必填意见
  'B004042', // 加签必填意见
  'B004015', // 特送个人必填意见
  'B004016', // 特送环节必填意见
  'B004014', // 催办环节必填意见
  'B004005' // 撤回必填意见
];
const DEFAULT_REQUIRED_OPINION_ROLE = {
  B004002: ['todo'], // 必须签署意见
  B004005: ['done'] // 撤回必填意见
};
const DEFAULT_APPLY_ROLE = {
  B004008: ['todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer'],
  B004009: ['start', 'todo', 'done', 'supervise'],
  B004010: ['todo', 'done', 'supervise', 'monitor', 'copyTo'],
  B004014: ['supervise', 'monitor'],
  B004017: ['todo'],
  B004096: ['start', 'todo', 'done', 'supervise', 'monitor']
};
const DEFAULT_VISIBLE_ROLE = {
  B004008: ['todo', 'done', 'supervise'],
  B004010: ['todo', 'done', 'supervise'],
  B004014: ['supervise', 'monitor'],
  B004017: [],
  B004096: ['todo', 'done']
};
const DEFAULT_NOT_HIDDEN_RIGHT_CODES = [
  'B004004',
  'B004009',
  'B004019',
  'B004020',
  'B004021',
  'B004022',
  'B004023',
  'B004024',
  'B004035',
  'B004037',
  'B004040',
  'B004044',
  'B004050',
  'B004095',
  'B004096'
];
export default {
  props: {
    setting: Object
  },
  components: {
    RightConfig: Vue.extend({
      template: `<div v-if="hasConfig" :style="{height: showRequiredOpinion ? '80px' : '44px'}">
        <div style='height: 22px'><a-checkbox v-model="config.apply">应用</a-checkbox></div>
        <div style='height: 22px'><a-checkbox v-if="hasConfig && config.apply" v-model="config.defaultVisible">默认显示</a-checkbox></div>
        <div style='height: 22px'><a-checkbox v-if="showRequiredOpinion" v-model="config.requiredOpinion">默认必填意见</a-checkbox></div></div>`,
      props: {
        config: Object,
        code: String
      },
      computed: {
        showRequiredOpinion() {
          return SIGN_OPINION_RIGHT_CODES.includes(this.code);
        },
        hasConfig() {
          return !isEmpty(this.config);
        }
      }
    })
  },
  data() {
    const _this = this;
    if (!_this.setting.rights.length) {
      _this.setting.rights = _this.initRights();
    }
    let defaultFilters = [
      { text: '可配置', value: 'avaible' },
      { text: '应用', value: 'apply' },
      { text: '默认显示', value: 'defaultVisible' }
    ];
    let filtersWithOpinion = [...defaultFilters, { text: '默认必填意见', value: 'requiredOpinion' }];
    return {
      rightTableColumns: [
        { title: '操作名称', dataIndex: 'title' },
        { title: '编号', dataIndex: 'code' },
        {
          title: '发起',
          dataIndex: 'start',
          scopedSlots: { customRender: 'startSlot' },
          filters: filtersWithOpinion,
          onFilter: (value, record) => (value == 'avaible' && !isEmpty(record.start)) || record.start[value]
        },
        {
          title: '待办',
          dataIndex: 'todo',
          scopedSlots: { customRender: 'todoSlot' },
          filters: filtersWithOpinion,
          onFilter: (value, record) => (value == 'avaible' && !isEmpty(record.todo)) || record.todo[value]
        },
        {
          title: '已办',
          dataIndex: 'done',
          scopedSlots: { customRender: 'doneSlot' },
          filters: filtersWithOpinion,
          onFilter: (value, record) => (value == 'avaible' && !isEmpty(record.done)) || record.done[value]
        },
        {
          title: '督办',
          dataIndex: 'supervise',
          scopedSlots: { customRender: 'superviseSlot' },
          filters: filtersWithOpinion,
          onFilter: (value, record) => (value == 'avaible' && !isEmpty(record.supervise)) || record.supervise[value]
        },
        {
          title: '监控',
          dataIndex: 'monitor',
          scopedSlots: { customRender: 'monitorSlot' },
          filters: filtersWithOpinion,
          onFilter: (value, record) => (value == 'avaible' && !isEmpty(record.monitor)) || record.monitor[value]
        },
        {
          title: '抄送',
          dataIndex: 'copyTo',
          scopedSlots: { customRender: 'copyToSlot' },
          filters: defaultFilters,
          onFilter: (value, record) => (value == 'avaible' && !isEmpty(record.copyTo)) || record.copyTo[value]
        },
        {
          title: '查阅',
          dataIndex: 'viewer',
          scopedSlots: { customRender: 'viewerSlot' },
          filters: defaultFilters,
          onFilter: (value, record) => (value == 'avaible' && !isEmpty(record.viewer)) || record.viewer[value]
        }
      ]
    };
  },
  watch: {
    'setting.buttons': {
      deep: true,
      handler(newValue) {
        this.updateRights();
      }
    }
  },
  methods: {
    initRights(btns) {
      const _this = this;
      let rights = [];
      let buttons = btns || _this.setting.buttons;
      buttons.forEach(button => {
        let code = button.code;
        let right = { id: generateId(), title: button.title, code };
        right.start = _this.getButtonRightConfig(button, 'start');
        right.todo = _this.getButtonRightConfig(button, 'todo');
        right.done = _this.getButtonRightConfig(button, 'done');
        right.supervise = _this.getButtonRightConfig(button, 'supervise');
        right.monitor = _this.getButtonRightConfig(button, 'monitor');
        right.copyTo = _this.getButtonRightConfig(button, 'copyTo');
        right.viewer = _this.getButtonRightConfig(button, 'viewer');
        rights.push(right);
      });
      return rights;
    },
    updateRights() {
      const _this = this;
      let buttons = _this.setting.buttons;
      let rights = _this.setting.rights;
      let addBtns = [];
      let deleteRights = [];
      buttons.forEach(btn => {
        let right = rights.find(right => btn.code == right.code);
        if (!right) {
          addBtns.push(btn);
        } else {
          // 更新名称
          right.title = btn.title;
          right.name = btn.name || btn.title;
        }
      });
      rights.forEach(right => {
        let btn = buttons.find(btn => btn.code == right.code);
        if (!btn) {
          deleteRights.push(right);
        }
      });

      // 删除的按钮权限
      deleteRights.forEach(deleteRight => {
        let deleteIndex = rights.findIndex(right => deleteRight.code == right.code);
        rights.splice(deleteIndex, 1);
      });

      // 添加的按钮权限
      _this.setting.rights = _this.setting.rights.concat(_this.initRights(addBtns));
    },
    getButtonRightConfig(button, role) {
      const _this = this;
      if (!button.buildIn) {
        return {
          apply: false,
          defaultVisible: false
        };
      }

      if (!button.applyTo.includes(role)) {
        return {};
      }

      let code = button.code;
      return {
        apply: _this.isDefaultApply(code, role),
        defaultVisible: _this.isDefaultVisible(code, role),
        requiredOpinion: _this.isDefaultRequiredOpinion(code, role)
      };
    },
    isDefaultApply(btnCode, role) {
      if (DEFAULT_APPLY_ROLE[btnCode]) {
        return DEFAULT_APPLY_ROLE[btnCode].includes(role);
      }
      return true;
    },
    isDefaultVisible(btnCode, role) {
      if (DEFAULT_VISIBLE_ROLE[btnCode]) {
        return DEFAULT_VISIBLE_ROLE[btnCode].includes(role);
      }
      return !DEFAULT_NOT_HIDDEN_RIGHT_CODES.includes(btnCode);
    },
    isDefaultRequiredOpinion(btnCode, role) {
      if (DEFAULT_REQUIRED_OPINION_ROLE[btnCode]) {
        return DEFAULT_REQUIRED_OPINION_ROLE[btnCode].includes(role);
      }
      return false;
    }
  }
};
</script>

<style lang="less" scoped>
.action-rights {
  padding: 15px;

  ::v-deep .ant-table-thead > tr > th .ant-table-filter-icon,
  ::v-deep .ant-table-thead > tr > th .anticon-filter {
    right: 33%;
  }
}
</style>
