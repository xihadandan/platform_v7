<template>
  <Scroll :style="{ height: '400px' }">
    <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
      <a-form-model :model="formData" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }" :rules="rules" ref="form">
        <a-form-model-item label="规则名称" prop="opinionRuleName">
          <a-input v-model="formData.opinionRuleName" :maxLength="50" />
        </a-form-model-item>
        <a-form-model-item label="校验项">
          满足以下
          <a-select v-model="formData.satisfyCondition" :options="satisfyConditionOptions" :style="{ width: '120px' }" />
          校验项时校验通过
          <a-table
            rowKey="uuid"
            :showHeader="false"
            size="small"
            :pagination="false"
            :bordered="false"
            :columns="opinionRuleItemTableColumns"
            :locale="locale"
            :data-source="formData.opinionRuleItemEntitys"
            :class="['widget-table-opinion-item-table no-border']"
          >
            <template slot="itemNameSlot" slot-scope="text, record">
              <a-select v-model="record.itemName" :options="itemNameOptions" :style="{ width: '120px' }" />
            </template>
            <template slot="itemConditionSlot" slot-scope="text, record">
              <a-select v-model="record.itemCondition" :options="itemConditionOptions" :style="{ width: '120px' }" />
            </template>
            <template slot="itemValueSlot" slot-scope="text, record">
              <a-form-model :ref="'itemForm_' + record.uuid" layout="inline" :model="record" :rules="itemRules">
                <a-form-model-item prop="itemValue">
                  <a-input v-model="record.itemValue" />
                </a-form-model-item>
              </a-form-model>
            </template>
            <template slot="operationSlot" slot-scope="text, record, index">
              <a-icon type="delete" @click="e => formData.opinionRuleItemEntitys.splice(index, 1)" />
            </template>
          </a-table>
          <a-button type="link" icon="plus" @click="addRuleItem">添加校验项</a-button>
        </a-form-model-item>
        <a-form-model-item prop="cueWords">
          <template slot="label">
            <a-space>
              提示语
              <a-popover>
                <template slot="content">规则校验未通过时提示用户的提示语</template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <div style="display: flex">
            <a-textarea
              v-model="formData.cueWords"
              :maxLength="50"
              :style="{
                'margin-right': '10px'
              }"
            />
            <w-i18n-input
              :target="formData"
              code="cueWords"
              v-model="formData.cueWords"
              :style="{
                'line-height': '1'
              }"
            />
          </div>
        </a-form-model-item>
        <a-form-model-item label="提示3s自动关闭" prop="isAlertAutoClose">
          <a-switch v-model="formData.isAlertAutoClose" />
        </a-form-model-item>
      </a-form-model>
    </a-skeleton>
  </Scroll>
</template>

<script>
import { generateId, deepClone } from '@framework/vue/utils/util';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  inject: ['pageContext', '$event', 'vPageState'],
  components: {
    WI18nInput
  },
  data() {
    let $event = this.$event || {};
    let formData = $event.meta || {};
    let uuid = formData.uuid;
    if (!formData.satisfyCondition) {
      formData.satisfyCondition = 'SC01';
    }
    if (!formData.opinionRuleItemEntitys) {
      formData.opinionRuleItemEntitys = [];
    }
    formData.isAlertAutoClose = formData.isAlertAutoClose == '1';
    let checkItemValueLength = (rule, value, callback) => {
      let itemRule = this.formData.opinionRuleItemEntitys.find(item => item.itemName == '意见长度' && item.itemValue == value);
      if (itemRule) {
        return /^\d+$/.test(value);
      }
      return true;
    };
    return {
      loading: !!uuid,
      uuid,
      formData,
      rules: {
        opinionRuleName: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        cueWords: { required: true, message: '不能为空', trigger: ['blur', 'change'] }
      },
      satisfyConditionOptions: [
        { label: '全部', value: 'SC01' },
        { label: '任何', value: 'SC02' }
      ],
      locale: {
        emptyText: <span>暂无数据</span>
      },
      opinionRuleItemTableColumns: [
        { title: '意见内容', dataIndex: 'itemName', scopedSlots: { customRender: 'itemNameSlot' } },
        { title: '比较符', dataIndex: 'itemCondition', scopedSlots: { customRender: 'itemConditionSlot' } },
        { title: '意见值', dataIndex: 'itemValue', scopedSlots: { customRender: 'itemValueSlot' } },
        { title: '操作', dataIndex: 'operation', width: 50, scopedSlots: { customRender: 'operationSlot' } }
      ],
      itemRules: {
        itemValue: [
          { required: true, message: '不能为空', trigger: ['blur', 'change'] },
          { validator: checkItemValueLength, message: '只能输入长度', trigger: ['blur', 'change'] }
        ]
      },
      itemNameOptions: [
        { label: '意见内容', value: '意见内容' },
        { label: '意见长度', value: '意见长度' }
      ],
      itemConditionOptions: [
        { label: '等于', value: 'IC01' },
        { label: '不等于', value: 'IC02' },
        { label: '包含', value: 'IC07' },
        { label: '不包含', value: 'IC08' }
      ]
    };
  },
  created() {
    if (this.uuid) {
      this.loadFormData();
    }
  },
  methods: {
    loadFormData() {
      $axios.get(`/api/opinion/rule/getOpinionRuleDetail?uuid=${this.uuid}`).then(({ data: result }) => {
        if (result.data) {
          this.loading = false;
          this.formData = result.data;
          this.formData.isAlertAutoClose = this.formData.isAlertAutoClose == '1';
        }
      });
    },
    addRuleItem() {
      this.formData.opinionRuleItemEntitys.push({
        uuid: generateId('SF'),
        itemName: '意见内容',
        itemCondition: 'IC01'
      });
    },
    save(evt) {
      const _this = this;
      let promises = [];
      promises.push(_this.$refs.form.validate());
      this.formData.opinionRuleItemEntitys.forEach(item => {
        if (_this.$refs['itemForm_' + item.uuid]) {
          promises.push(_this.$refs['itemForm_' + item.uuid].validate());
        }
      });
      Promise.all(promises).then(valid => {
        if (valid) {
          let formData = deepClone(_this.formData);
          formData.isAlertAutoClose = formData.isAlertAutoClose ? '1' : '0';
          formData.opinionRuleItemEntitys.forEach(item => delete item.uuid);
          formData.opinionRuleItems = JSON.stringify(formData.opinionRuleItemEntitys);
          $axios
            .post('/api/opinion/rule/saveOpinionRule', formData)
            .then(({ data: result }) => {
              if (result.success) {
                _this.$message.success('保存成功！');
                _this.pageContext.emitEvent('QtInGDWQNBQaCMNBGaJADnIeMTuNByKs:closeModal');
                _this.pageContext.emitEvent('MdtvmHBKiQYcvrOMkYEztwABFJOuyRzH:refetch');
              } else {
                _this.$message.error(result.msg);
              }
            })
            .catch(({ response }) => {
              _this.$message.error(response.data.msg);
            });
        }
      });
    }
  },
  META: {
    method: {
      save: '保存校验规则'
    }
  }
};
</script>

<style lang="less" scoped>
.widget-table-opinion-item-table {
  ::v-deep .ant-form-item-with-help {
    margin-bottom: 0;
  }
  ::v-deep .has-error {
    margin-bottom: 0 !important;
  }
  ::v-deep .ant-form-explain {
    position: absolute;
    right: 0;
    top: 12px;
  }
}
</style>
