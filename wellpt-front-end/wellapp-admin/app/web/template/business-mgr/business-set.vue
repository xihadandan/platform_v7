<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model :model="form" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" :rules="rules" ref="form" class="pt-form">
      <a-form-model-item label="所属行业" prop="attrValue">
        <TreeSelectComponent
          ref="treeSelectRef"
          :propTreeData="allOptions"
          :value="form.attrValue"
          :multiple="true"
          @change="selChange"
        ></TreeSelectComponent>
      </a-form-model-item>
    </a-form-model>
    <div style="text-align: center; margin: 20px 0 10px 0">
      <a-button @click="saveForm" type="primary">保存</a-button>
    </div>
  </a-skeleton>
</template>

<script type="text/babel">
import { deepClone, queryString, getElSpacingForTarget } from '@framework/vue/utils/util';
import TreeSelectComponent from '../common/tree-select-component';
export default {
  name: 'businessSet',
  inject: ['pageContext', '$event', 'vPageState'],
  components: { TreeSelectComponent },
  data() {
    let $event = this.$event;
    return {
      loading: $event && $event.meta && $event.meta.uuid != undefined,
      uuid: $event && $event.meta != undefined ? $event.meta.uuid : undefined,
      form: {
        attrName: null,
        attrValue: null,
        uuid: null
      },
      rules: {
        attrValue: { required: true, message: '所属行业必填', trigger: ['blur', 'change'] }
      },
      allOptions: []
    };
  },
  computed: {},
  beforeMount() {
    let _this = this;
    this.getDetails();
    this.getOptions();
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
          args: '["PT_BUSINESS_APP_CATEGORY"]',
          methodName: 'getCurrentUnitOneAttr',
          serviceName: 'multiOrgSystemUntAttrService',
          validate: false,
          version: ''
        })
        .then(({ data }) => {
          _this.loading = false;
          if (data.code == 0 && data.data) {
            this.form = data.data;
          }
        });
    },
    getOptions() {
      let _this = this;
      _this.$axios
        .post('/json/data/services', {
          args: '[null]',
          serviceName: 'flowOpinionCategoryService',
          methodName: 'getFlowOpinionCategoryTreeByBusinessAppDataDic'
        })
        .then(({ data }) => {
          if (data.data) {
            _this.$set(_this, 'allOptions', [data.data]);
            _this.$nextTick(() => {
              _this.$refs.form.clearValidate();
            });
          }
        });
    },
    selChange(arg) {
      this.form.attrValue = arg.backValue;
      this.form.attrName = arg.label ? arg.label.join(';') : '';
    },
    saveForm() {
      let _this = this;
      this.$refs.form.validate(valid => {
        if (valid) {
          let bean = {
            attrCode: 'PT_BUSINESS_APP_CATEGORY',
            attrName: this.form.attrName,
            attrValue: this.form.attrValue,
            remark: '系统单位的行业设置',
            uuid: this.form.uuid
          };
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
          serviceName: 'multiOrgSystemUntAttrService',
          methodName: 'saveUpdateCurrentUnitAttr'
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
