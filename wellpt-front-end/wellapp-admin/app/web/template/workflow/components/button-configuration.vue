<template>
  <a-form-model
    ref="form"
    :model="button"
    labelAlign="left"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17 }"
    :rules="rules"
    :colon="false"
  >
    <a-form-model-item label="操作类型">
      <a-radio-group size="small" v-model="button.multistate" :disabled="button.buildIn">
        <a-radio-button :value="false">常规</a-radio-button>
        <a-radio-button :value="true">多状态</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <template v-if="button.multistate">
      <a-form-model-item label="状态管理">
        <a-table
          rowKey="id"
          :pagination="false"
          :bordered="false"
          size="small"
          :columns="stateButtonTableColumns"
          :data-source="button.states"
        >
          <template slot="titleSlot" slot-scope="text, record, index">
            <a-input v-model="record.title" />
          </template>
          <template v-if="!button.buildIn" slot="operationSlot" slot-scope="text, record, index">
            <a-icon type="delete" @click="deleteStatepButton(index)" />
          </template>
          <template v-if="!button.buildIn" slot="footer">
            <a-button type="link" @click="addStateButton" icon="plus">添加</a-button>
          </template>
        </a-table>
      </a-form-model-item>
      <a-tabs v-model="activeStateKey">
        <a-tab-pane v-for="state in button.states" :key="state.id" :tab="state.title || '状态名称'">
          <ButtonDetails :button="state"></ButtonDetails>
        </a-tab-pane>
      </a-tabs>
    </template>
    <template v-else>
      <ButtonDetails :button="button"></ButtonDetails>
    </template>
  </a-form-model>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import ButtonDetails from './button-details.vue';
export default {
  props: {
    button: {
      type: Object,
      default() {
        let time = new Date().getTime();
        return {
          id: generateId(),
          title: '',
          code: 'B' + time,
          sortOrder: time,
          buildIn: false,
          multistate: false,
          states: [],
          style: { type: 'default' },
          eventHandler: {},
          defaultVisible: true,
          defaultVisibleVar: {}
        };
      }
    }
  },
  components: { ButtonDetails },
  provide() {
    return {
      appId: () => []
    };
  },
  data() {
    let activeStateKey = '';
    if (this.button.multistate && this.button.states.length) {
      activeStateKey = this.button.states[0].id;
    }
    return {
      activeStateKey,
      rules: {
        title: [{ required: true, message: '不能为空！', trigger: 'change' }]
      },
      stateButtonTableColumns: [
        { title: '状态名称', dataIndex: 'title', width: 250, scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 50, scopedSlots: { customRender: 'operationSlot', align: 'right' } }
      ]
    };
  },
  computed: {
    configs() {
      return this.button.multistate ? this.button.states : [this.button];
    }
  },
  watch: {
    'button.states': {
      deep: true,
      handler(newValue) {
        // 多状态按钮第一个状态的基本信息代表按钮显示
        if (this.button.multistate && newValue.length) {
          this.button.id = newValue[0].id;
          this.button.title = newValue[0].title;
          this.button.code = newValue[0].code;
          this.button.defaultVisible = newValue[0].defaultVisible;
        }
      }
    }
  },
  methods: {
    addStateButton() {
      let time = new Date().getTime();
      this.activeStateKey = generateId();
      this.button.states.push({
        id: this.activeStateKey,
        title: '',
        code: 'B' + time,
        sortOrder: time,
        style: {},
        eventHandler: {},
        defaultVisible: true,
        defaultVisibleVar: {}
      });
    },
    deleteStatepButton(index) {
      this.button.states.splice(index, 1);
    },
    collect() {
      return this.$refs.form.validate().then(() => {
        return this.button;
      });
    }
  }
};
</script>

<style lang="less" scoped>
::v-deep .ant-form-item {
  margin-bottom: 5px;
}
</style>
