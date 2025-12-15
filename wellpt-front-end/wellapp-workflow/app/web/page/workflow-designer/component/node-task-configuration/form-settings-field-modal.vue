<template>
  <!-- 环节属性-表单设置-表单字段-弹框 -->
  <div class="flex wf-form-settings-field-modal">
    <div style="width: 500px" class="f_s_0">
      <PerfectScrollbar :style="{ height: '480px' }" ref="scroll">
        <form-setting-field
          :formData="formData"
          :formDefinition="formDefinition"
          :customTitle="true"
          v-model="dataSource"
          ref="wfFormSettingFieldRef"
        >
          <template slot="title">
            <form-setting-search @search="onSearch" :isShow="true"></form-setting-search>
          </template>
        </form-setting-field>
      </PerfectScrollbar>
    </div>
    <div class="f_g_1">
      <a-collapse accordion v-model="activeKey">
        <a-collapse-panel key="1" header="显示字段">
          <PerfectScrollbar :style="{ height: '330px' }" ref="scroll">
            <a-tag v-for="(item, index) in hideData" :key="'h_' + index">{{ item.name }}</a-tag>
          </PerfectScrollbar>
        </a-collapse-panel>
        <a-collapse-panel key="2" header="可编辑字段">
          <PerfectScrollbar :style="{ height: '330px' }" ref="scroll">
            <a-tag v-for="(item, index) in editFields" :key="'e_' + index">{{ item.name }}</a-tag>
          </PerfectScrollbar>
        </a-collapse-panel>
        <a-collapse-panel key="3" header="必填字段">
          <PerfectScrollbar :style="{ height: '330px' }" ref="scroll">
            <a-tag v-for="(item, index) in notNullFields" :key="'n_' + index">{{ item.name }}</a-tag>
          </PerfectScrollbar>
        </a-collapse-panel>
      </a-collapse>
    </div>
  </div>
</template>

<script>
import FormSettingField from './form-settings-field.vue';
import FormSettingSearch from './form-setting-search.vue';
import { each, filter, findIndex } from 'lodash';

export default {
  name: 'FormSettingFieldModal',
  inject: ['designer'],
  props: {
    value: {
      type: Array,
      default: () => []
    },
    formData: {
      type: Object,
      default: () => {}
    },
    formDefinition: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    FormSettingField,
    FormSettingSearch
  },
  data() {
    let dataSource = this.value;
    return {
      dataSource,
      activeKey: '1'
    };
  },
  computed: {
    hideData() {
      let fields = [];
      each(this.dataSource, (item, index) => {
        let value = this.fieldValue(item);
        let hasIndex = findIndex(this.formData.hideFields, { value: value });
        if (hasIndex == -1 && (item.subformUuid || item.subformField || !item.isSubform)) {
          fields.push({
            code: value,
            name: (item.subformTitle ? item.subformTitle + '：' : '') + item.name
          });
        }
      });
      return fields;
    },
    notNullFields() {
      let fields = [];
      each(this.dataSource, (item, index) => {
        let value = this.fieldValue(item);
        let hasIndex = findIndex(this.formData.notNullFields, { value: value });
        if (hasIndex > -1 && ((item.subformUuid && item.subformField) || !(item.subformUuid || item.isSubform))) {
          fields.push({
            code: value,
            name: (item.subformTitle ? item.subformTitle + '：' : '') + item.name
          });
        }
      });
      return fields;
    },
    editFields() {
      let fields = [];
      each(this.dataSource, (item, index) => {
        let value = this.fieldValue(item);
        let hasIndex = findIndex(this.formData.editFields, { value: value });
        if (hasIndex > -1 && ((item.subformUuid && item.subformField) || !(item.subformUuid || item.isSubform))) {
          fields.push({
            code: value,
            name: (item.subformTitle ? item.subformTitle + '：' : '') + item.name
          });
        }
      });
      return fields;
    }
  },
  watch: {},
  created() {},
  methods: {
    onSearch(value) {
      this.$refs.wfFormSettingFieldRef.onSearch(value);
    },
    fieldValue(field) {
      let value = '';
      if (field.subformField) {
        value = `${field.subformUuid}:${field.code}`;
      } else {
        value = field.code;
      }
      return value;
    },
    emitChange() {
      this.$refs.wfFormSettingFieldRef.emitChange();
      this.$emit('input', this.dataSource);
    }
  }
};
</script>
