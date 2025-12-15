<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model :model="form" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" :rules="rules" ref="form" class="pt-form">
      <a-form-model-item label="数据类型名称" prop="name">
        <a-input v-model="form.name" allow-clear />
      </a-form-model-item>
      <a-form-model-item label="索引标题" prop="titleExps">
        <VariableDefineComponent
          :formData="form"
          prop="titleExps"
          :hasDyformVar="false"
          :workflowVar="workflowVar"
          ref="titleExpsRef"
        ></VariableDefineComponent>
      </a-form-model-item>
      <a-form-model-item label="索引摘要" prop="contentExps">
        <VariableDefineComponent
          :formData="form"
          prop="contentExps"
          :hasDyformVar="false"
          :workflowVar="workflowVar"
          ref="contentExpsRef"
        ></VariableDefineComponent>
      </a-form-model-item>
      <a-form-model-item label="创建人" prop="creatorExps">
        <VariableDefineComponent
          :formData="form"
          prop="creatorExps"
          :hasDyformVar="false"
          :workflowVar="workflowVar"
          ref="creatorExpsRef"
        ></VariableDefineComponent>
      </a-form-model-item>
      <a-form-model-item label="创建时间" prop="createTimeField">
        <a-select v-model="form.createTimeField" showSearch allow-clear>
          <a-select-option v-for="d in fieldData" :key="d.id">
            {{ d.text }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="修改时间" prop="modifyTimeField">
        <a-select v-model="form.modifyTimeField" showSearch allow-clear>
          <a-select-option v-for="d in fieldData" :key="d.id">
            {{ d.text }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="文档链接" prop="url">
        <a-textarea v-model="form.url" :auto-size="{ minRows: 2, maxRows: 5 }" allow-clear />
      </a-form-model-item>
    </a-form-model>
    <div style="text-align: center; margin: 20px 0 10px 0">
      <a-button @click="saveForm" type="primary">保存</a-button>
    </div>
  </a-skeleton>
</template>

<script type="text/babel">
import { deepClone, queryString, getElSpacingForTarget } from '@framework/vue/utils/util';
import VariableDefineComponent from '../common/variable-define-component';
const fieldData = [
  {
    text: '开始时间字段',
    id: 'startTime'
  },
  {
    text: '创建时间字段',
    id: 'createTime'
  },
  {
    text: '修改时间字段',
    id: 'modifyTime'
  },
  {
    text: '结束时间字段',
    id: 'endTime'
  },
  {
    text: '日期保留字段1',
    id: 'reservedDate1'
  },
  {
    text: '日期保留字段2',
    id: 'reservedDate2'
  }
];
export default {
  name: 'WorkflowIndexSet',
  inject: ['pageContext', '$event', 'vPageState'],
  components: { VariableDefineComponent },
  data() {
    let $event = this.$event;
    return {
      loading: $event && $event.meta && $event.meta.uuid != undefined,
      uuid: $event && $event.meta != undefined ? $event.meta.uuid : undefined,
      fieldData,
      form: {},
      rules: {
        attrValue: { required: true, message: '所属行业必填', trigger: ['blur', 'change'] }
      },
      workflowVar: {
        var: ['发起人姓名', '发起人所在部门名称', '发起人所在部门名称全路径']
      }
    };
  },
  computed: {},
  beforeMount() {
    let _this = this;
    this.getDetails();
  },
  mounted() {
    let $parent = this.$root.$el.classList.contains('preview') ? this.$root.$el : this.$el.closest('.widget-vpage');
    const { maxHeight, totalBottom, totalNextSibling } = getElSpacingForTarget(this.$el.querySelector('.pt-form'), $parent);
    this.$el.querySelector('.pt-form').style.cssText = `overflow-y:auto; height:${maxHeight}px`;
  },
  methods: {
    getDetails() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          argTypes: [],
          args: '["workflow"]',
          methodName: 'getByType',
          serviceName: 'indexDocTemplateService',
          validate: false,
          version: ''
        })
        .then(({ data }) => {
          _this.loading = false;
          if (data.code == 0 && data.data) {
            this.$set(this, 'form', data.data);
            this.$nextTick(() => {
              this.$refs.contentExpsRef.update();
              this.$refs.titleExpsRef.update();
              this.$refs.creatorExpsRef.update();
            });
          }
        });
    },
    saveForm() {
      let _this = this;
      this.$refs.form.validate(valid => {
        if (valid) {
          let bean = deepClone(this.form);
          this.saveFormData(bean);
        } else {
          return false;
        }
      });
    },
    saveFormData(bean) {
      let _this = this;
      _this.$axios
        .post('/json/data/services', {
          args: JSON.stringify([bean]),
          serviceName: 'indexDocTemplateService',
          methodName: 'save'
        })
        .then(({ data }) => {
          if (data.code == '0') {
            this.$message.success('保存成功');
          } else {
            this.$message.error(data.msg || '保存失败');
          }
        });
    }
  },
  META: {
    method: {
      saveForm: '保存表单'
    }
  }
};
</script>

<style></style>
