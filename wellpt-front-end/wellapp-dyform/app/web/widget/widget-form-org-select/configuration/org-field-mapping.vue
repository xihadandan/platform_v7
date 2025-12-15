<template>
  <a-table rowKey="id" :pagination="false" bordered size="small" :columns="columns" :data-source="widget.configuration.fieldMapping">
    <template slot="sourceFieldSlot" slot-scope="text, record">
      <div style="display: flex">
        <a-input-group compact>
          <a-select
            :options="[
              { label: '内置字段', value: 'orgField' },
              { label: '自定义字段', value: 'selfDefine' }
            ]"
            :style="{ width: '120px' }"
            size="small"
            v-model="record.attrKeyType"
          ></a-select>
          <a-select
            :options="orgFields"
            :style="{ width: 'calc(100% - 120px)' }"
            v-if="record.attrKeyType == 'orgField'"
            size="small"
            show-search
            :filter-option="filterSelectOption"
            v-model="record.attrKey"
          ></a-select>
          <a-input size="small" v-else v-model="record.attrKey" allow-clear :style="{ width: '50%' }"></a-input>
        </a-input-group>
      </div>
    </template>
    <template slot="targetFieldSlot" slot-scope="text, record">
      <a-select
        :options="formFieldOptions"
        :style="{ width: '100%' }"
        size="small"
        v-model="record.targetField"
        show-search
        :filter-option="filterSelectOption"
      ></a-select>
    </template>
    <template slot="operationSlot" slot-scope="text, record, index">
      <a-button icon="delete" size="small" type="link" @click="widget.configuration.fieldMapping.splice(index, 1)"></a-button>
    </template>
    <template slot="footer">
      <a-button icon="plus" size="small" type="link" @click="addFieldMapping">添加映射</a-button>
    </template>
  </a-table>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, swapArrayElements } from '@framework/vue/utils/util';
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import { filterSelectOption } from '@framework/vue/utils/function.js';

export default {
  name: 'OrgFieldMapping',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  components: {},
  computed: {
    formFieldOptions() {
      let opt = [];
      if (this.designer.FieldWidgets && this.designer.FieldWidgets.length) {
        for (let k = 0, len = this.designer.FieldWidgets.length; k < len; k++) {
          let field = this.designer.FieldWidgets[k];
          if (field.configuration.code && field.id != this.widget.id) {
            opt.push({
              label: field.configuration.name || field.configuration.code,
              value: field.configuration.code
            });
          }
        }
      }
      return opt;
    }
  },
  data() {
    return {
      columns: [
        { title: '组织字段', dataIndex: 'orgField', scopedSlots: { customRender: 'sourceFieldSlot' } },
        { title: '映射到表单字段', dataIndex: 'formField', scopedSlots: { customRender: 'targetFieldSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' }, align: 'center' }
      ],
      selectedColumnRowKeys: [],
      selectedColumnRows: [],
      orgFields: [
        { label: '名称', value: 'title' },
        { label: '路径名称', value: 'titlePath' },
        { label: 'ID', value: 'key' },
        { label: 'ID路径', value: 'keyPath' },
        { label: '简称', value: 'shortTitle' },
        { label: '用户编号', value: 'userNo' },
        { label: '用户登录账号', value: 'loginName' },
        { label: '身份证号', value: 'idNumber' },
        { label: '手机号码', value: 'ceilPhoneNumber' },
        { label: '家庭电话', value: 'familyPhoneNumber' },
        { label: '办公电话', value: 'businessPhoneNumber' },
        { label: '邮箱', value: 'mail' },
        { label: '工作属地', value: 'workLocation' },
        { label: '职位名称', value: 'jobName' },
        { label: '职位ID', value: 'jobId' },
        { label: '部门名称', value: 'deptName' },
        { label: '部门ID', value: 'deptId' },
        { label: '单位名称', value: 'unitName' },
        { label: '单位ID', value: 'unitId' },
        { label: '业务角色ID', value: 'bizOrgRoleId' },
        { label: '业务角色名称', value: 'bizOrgRoleName' },
        { label: '业务维度ID', value: 'bizOrgDimId' },
        { label: '业务维度名称', value: 'bizOrgDimName' }
      ]
    };
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('fieldMapping')) {
      this.$set(this.widget.configuration, 'fieldMapping', []);
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    filterSelectOption,
    addFieldMapping() {
      this.widget.configuration.fieldMapping.push({
        id: generateId(),
        attrKey: undefined,
        targetField: undefined,
        attrKeyType: 'orgField'
      });
    },
    selectColumnChange(selectedRowKeys, selectedRows) {
      this.selectedColumnRowKeys = selectedRowKeys;
      this.selectedColumnRows = selectedRows;
    }
  }
};
</script>
