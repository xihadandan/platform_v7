<template>
  <!-- 环节属性-表单设置 -->
  <div>
    <a-form-model-item class="form-item-vertical">
      <template slot="label">
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <div slot="title">为当前环节的办理人设置指定的表单展示形式</div>
          <label>
            展示表单
            <a-icon type="exclamation-circle" />
          </label>
        </a-tooltip>
      </template>
      <w-select v-model="formData.formID" :options="displayFormOptions" placeholder="请选择" @change="onDisplayFormChange" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical">
      <template slot="label">
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <div slot="title">
            1、开启：表单可编辑，前端显示的表单字段是否可编辑，读取环节中表单字段的设置，表单字段不可编辑时的显示样式，读取表单定义中控件的设置
            <br />
            2、关闭：表单不可编辑，前端显示的表单字段均不可编辑，显示样式读取表单定义中控件的设置
          </div>
          <label>
            表单可编辑
            <a-icon type="exclamation-circle" />
          </label>
        </a-tooltip>
      </template>
      <!-- 第一个环节默认打开'1'，显示编辑列；其他关闭 -->
      <w-switch v-model="formData.canEditForm" />
    </a-form-model-item>
    <task-form-settings-field
      v-if="definitionVjson"
      :modified="modified"
      :formData="formData"
      :formDefinition="definitionVjson"
      @modalShow="modalShow"
      ref="wfFormSettingFieldsRef"
    />
    <task-form-settings-layout :modified="modified" :formData="formData" :formDefinition="definitionVjson" style="padding-top: 10px" />
    <a-modal
      title="表单字段编辑"
      :visible="modalVisible"
      :width="1000"
      @cancel="modalCancel"
      wrapClassName="form-settings-modal-wrap"
      :destroyOnClose="true"
      :bodyStyle="{ padding: '12px 20px' }"
      :zIndex="998"
    >
      <form-settings-field-modal
        v-model="dataSource"
        :formData="formData"
        :formDefinition="definitionVjson"
        ref="wfFormSettingFieldsModalRef"
      />
      <template slot="footer">
        <a-button @click="modalCancel">关闭</a-button>
      </template>
    </a-modal>
  </div>
</template>

<script>
import WSwitch from '../components/w-switch';
import TaskFormSettingsLayout from './form-settings-layout.vue';
import TaskFormSettingsField from './form-settings-field.vue';
import WSelect from '../components/w-select';
import FormSettingsFieldModal from './form-settings-field-modal.vue';

export default {
  name: 'TaskPropertyFormSettings',
  inject: ['designer'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WSwitch,
    TaskFormSettingsLayout,
    TaskFormSettingsField,
    WSelect,
    FormSettingsFieldModal
  },
  data() {
    const definitionVjson = this.designer.getDefinitionVjson();
    const modified = this.formData.allFormFieldWidgetIds.length > 0; // 表单是否修改过
    return {
      definitionVjson,
      displayFormOptions: [],
      modalVisible: false,
      dataSource: [],
      modified
    };
  },
  watch: {
    'designer.formDefinition': {
      deep: true,
      handler(formDefinition) {
        this.modified = false;
        this.definitionVjson = this.designer.getDefinitionVjson();
        this.formData.formID = null;
        this.getTaskDisplayForm(formDefinition);
      }
    }
  },
  created() {
    if (this.designer.formDefinition) {
      this.getTaskDisplayForm(this.designer.formDefinition);
      if (this.formData.formID) {
        this.onDisplayFormChange(this.formData.formID);
      }
    }
  },
  methods: {
    // 获取环节展示表单
    getTaskDisplayForm(formDefinition) {
      this.$axios
        .get('/api/workflow/definition/getVformsByPformUuid', {
          params: { pformUuid: formDefinition.uuid }
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.displayFormOptions = data;
            }
          }
        });
    },
    onDisplayFormChange(value) {
      if (value) {
        this.$axios.get(`/pt/dyform/definition/getFormDefinitionByUuid?uuid=${value}`).then(({ data: formDefinition }) => {
          if (formDefinition) {
            this.definitionVjson = null;
            this.$nextTick(() => {
              this.definitionVjson = JSON.parse(formDefinition.definitionVjson);
            });
          }
        });
      } else {
        this.definitionVjson = this.designer.getDefinitionVjson();
      }
    },
    modalShow({ dataSource }) {
      this.dataSource = dataSource;
      this.modalVisible = true;
    },
    modalCancel() {
      this.$refs.wfFormSettingFieldsModalRef.emitChange();
      this.modalVisible = false;
      this.$nextTick(() => {
        this.$refs.wfFormSettingFieldsRef.refresh(this.dataSource);
      });
    }
  }
};
</script>
