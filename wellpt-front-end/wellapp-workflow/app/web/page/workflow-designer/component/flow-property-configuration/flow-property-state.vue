<template>
  <a-form-model ref="form" :model="formData" :colon="false">
    <!-- 流程状态 -->
    <a-table :data-source="formData.statusList" bordered :pagination="false" rowKey="code">
      <a-table-column key="code" data-index="code" title="流程状态" :width="120">
        <template slot-scope="text, record">
          {{ record.code }}
        </template>
      </a-table-column>
      <a-table-column key="name" data-index="name" title="状态名称">
        <template slot-scope="text, record, index">
          <a-form-model-item
            style="margin-bottom: 0"
            :prop="`statusList:${index}:name`"
            :name="['statusList', index, 'name']"
            :rules="rules.name"
          >
            <a-input v-model="record.name" />
          </a-form-model-item>
        </template>
      </a-table-column>
    </a-table>
  </a-form-model>
</template>

<script>
import { flowStates } from '../designer/constant';
import { deepClone } from '@framework/vue/utils/util';
export default {
  name: 'FlowPropertyState',
  props: {
    list: {
      type: Array,
      default: []
    }
  },
  data() {
    let statusList = this.list;
    if (!statusList || statusList.length == 0) {
      statusList = deepClone(flowStates);
    }
    return {
      rules: {
        name: { required: true, message: '请完善流程状态信息！', trigger: ['blur'] }
      },
      formData: {
        statusList
      }
    };
  },
  methods: {
    save(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData.statusList });
      });
    }
  }
};
</script>
<style lang="less" scope>
.flow-timer-modal-wrap {
  .ant-table-tbody > tr > td {
    padding: var(--w-padding-3xs) 0 0;
    text-align: center;
    .ant-form-item {
      padding-left: var(--w-padding-xs);
      padding-right: var(--w-padding-xs);
    }
  }
  .ant-table-thead > tr > th {
    text-align: center;
    padding: var(--w-padding-2xs) 0;
  }
}
</style>
