<template>
  <!-- 环节属性-表单设置-表单字段 -->
  <div class="ant-form-item">
    <div style="flex: 1">
      <a-table
        class="form-settings-table"
        rowKey="id"
        :pagination="false"
        size="small"
        :columns="columns"
        :dataSource="dataSource"
        :rowClassName="() => 'form-settings-field-row'"
      >
        <template slot="title">
          <template v-if="customTitle">
            <slot name="title"></slot>
          </template>
          <template v-else>
            <div class="flex f_x_s">
              <div>
                <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                  <div slot="title">设置当前环节的办理人可查看、可编辑或者必填的字段</div>
                  <label class="form-settings-table-title">
                    表单字段
                    <a-icon type="exclamation-circle" />
                  </label>
                </a-tooltip>
              </div>
              <div style="text-align: right">
                <form-setting-search @search="onSearch"></form-setting-search>
                <a-button type="link" size="small" @click="modalShow">
                  <Icon type="pticon iconfont icon-luojizujian-danchuangtishi"></Icon>
                </a-button>
              </div>
            </div>
          </template>
        </template>
        <template slot="nameSlot" slot-scope="text, record">
          <template v-if="record.widget.wtype === 'WidgetFormFileUpload' && record.visible">
            <!-- 附件 -->
            <form-settings-file-button title="附件扩展设置" :field="record" :formData="formData" :fieldItem="fieldItem" />
          </template>
          <template v-if="record.isSubform">
            <!-- 从表表头 -->
            <div style="margin-left: 18px; color: #ff9900">{{ text }}</div>
          </template>
          <template v-else-if="record.isSubWidget">
            <form-settings-subform-button :field="record" :formData="formData" :fieldItem="fieldItem" v-if="record.visible" />
            <!-- 从表扩展设置 -->
            <i class="iconfont icon-ptkj-wenjianjia-kai" />
            <span v-html="textFilter(text)"></span>
          </template>
          <template v-else-if="record.subformField">
            <!-- 从表字段 -->
            <div
              :style="{
                marginLeft: record.widget.wtype === 'WidgetFormFileUpload' && record.visible ? '14px' : '18px',
                display: 'inline-block'
              }"
            >
              <i class="iconfont icon-ptkj-wenjian" />
              <span v-html="textFilter(text)"></span>
            </div>
          </template>
          <template v-else>
            <div
              :style="{
                marginLeft: record.widget.wtype === 'WidgetFormFileUpload' && record.visible ? '-4px' : '0px',
                display: 'inline-block'
              }"
            >
              <i class="iconfont icon-ptkj-wenjian" />
              <span v-html="textFilter(text)"></span>
            </div>
          </template>
        </template>
        <!-- 显示 -->
        <template slot="visibleTitle">
          <w-checkbox v-model="checkAll" :checkedValue="true" :unCheckedValue="false" @change="changeCheckAll">显示</w-checkbox>
        </template>
        <template slot="visibleSlot" slot-scope="text, record">
          <w-checkbox
            v-model="record.visible"
            :checkedValue="true"
            :unCheckedValue="false"
            :disabled="record.disabled"
            @change="event => changeVisible(event, record)"
          />
        </template>
        <!-- 编辑 -->
        <template slot="editTitle">
          <w-checkbox
            v-model="checkAllEdit"
            :checkedValue="true"
            :unCheckedValue="false"
            :disabled="editAllDisabled"
            @change="changeAllEdit"
          >
            编辑
          </w-checkbox>
        </template>
        <template slot="editSlot" slot-scope="text, record">
          <template v-if="record.widget.wtype !== 'WidgetSubform' && record.widget.wtype !== 'WidgetFormFileUpload'">
            <w-checkbox
              v-model="record.edit"
              :checkedValue="true"
              :unCheckedValue="false"
              :disabled="record.editDisabled"
              @change="event => changeFieldEdit(event, record)"
            />
          </template>
        </template>
        <!-- 必填 -->
        <template slot="requiredTitle">
          <w-checkbox v-model="checkAllRequired" :checkedValue="true" :unCheckedValue="false" @change="changeAllRequired">必填</w-checkbox>
        </template>
        <template slot="requiredSlot" slot-scope="text, record">
          <template v-if="record.widget.wtype !== 'WidgetSubform'">
            <w-checkbox
              v-model="record.required"
              :checkedValue="true"
              :unCheckedValue="false"
              :disabled="record.disabled"
              @change="event => changeRequired(event, record)"
            />
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script>
import WCheckbox from '../components/w-checkbox';
import FormSettingsSubformButton from './form-settings-subform-button.vue';
import FormSettingsFileButton from './form-settings-file-button.vue';
import FormSettingSearch from './form-setting-search.vue';
import { deepClone } from '@framework/vue/utils/util';
import { each, filter, findIndex } from 'lodash';

export default {
  name: 'FormSettingField',
  inject: ['designer', 'workFlowData'],
  props: {
    value: {
      type: Array
    },
    formData: {
      type: Object,
      default: () => {}
    },
    formDefinition: {
      type: Object,
      default: () => {}
    },
    customTitle: {
      type: Boolean,
      default: false
    },
    modified: {
      type: Boolean,
      default: true
    }
  },
  components: {
    WCheckbox,
    FormSettingsSubformButton,
    FormSettingsFileButton,
    FormSettingSearch
  },
  data() {
    let dataSource = this.value ? deepClone(this.value) : [];
    return {
      dataSource,
      columns: [
        { title: '字段名', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        {
          dataIndex: 'visible',
          width: 80,
          slots: { title: 'visibleTitle' },
          scopedSlots: { customRender: 'visibleSlot' },
          align: 'left'
        },
        {
          dataIndex: 'required',
          width: 80,
          slots: { title: 'requiredTitle' },
          scopedSlots: { customRender: 'requiredSlot' },
          align: 'left'
        }
      ],
      checkAll: false,
      fieldItem: () => {
        return {
          type: 32,
          value: '',
          argValue: null
        };
      },
      editColumn: {
        dataIndex: 'edit',
        width: 85,
        slots: { title: 'editTitle' },
        scopedSlots: { customRender: 'editSlot' },
        align: 'left'
      },
      checkAllRequired: false,
      checkAllEdit: false,
      searchVal: ''
    };
  },
  computed: {
    hideData() {
      return this.formData.hideFields;
    },
    notNullFields() {
      return this.formData.notNullFields;
    },
    editFields() {
      return this.formData.editFields;
    },
    allFormFields() {
      let allFormFields = [];
      const task = this.workFlowData.tasks.find(item => item.id === this.formData.id);
      if (task) {
        allFormFields = task.allFormFields;
      }
      return allFormFields;
    },
    editAllDisabled() {
      // 等于-1，表示全部隐藏
      const hideIndex = this.dataSource.findIndex(item => item.visible);
      if (hideIndex == -1) {
        return true;
      } else {
        return false;
      }
    }
  },
  watch: {
    formDefinition: {
      deep: true,
      handler(formDefinition, oldFormDefinition) {
        if (formDefinition && oldFormDefinition && formDefinition.tableName != oldFormDefinition.tableName) {
          this.formData.hideFields = [];
          this.formData.notNullFields = [];
          this.formData.editFields = [];
        }
        if (!this.value) {
          this.dataSource = this.getDataSource(formDefinition);
        }
        this.formatDataSource();
      }
    },
    'formData.canEditForm': {
      deep: false,
      handler(canEditForm) {
        if (canEditForm === '1') {
          // 显示编辑列
          this.columns.splice(2, 0, this.editColumn);
        } else {
          this.columns.splice(2, 1);
        }
      }
    }
  },
  created() {
    const { canEditForm } = this.formData;
    if (canEditForm === '1') {
      this.columns.splice(2, 0, this.editColumn);
    }
    if (!this.value) {
      this.dataSource = this.getDataSource(this.formDefinition);
    }
    this.formatDataSource();
  },
  methods: {
    // 获取表单字段数据
    /* 记录每个表单的长度
    必填的长度 === 表单长度时代表全选，反之
    必填 if(record.isSubform){从表表头 全选}
    */
    getDataSource(formDefinition) {
      this.formData.allFormFields = [];
      this.formData.allFormFieldWidgetIds = [];
      let dataSource = [];
      if (!formDefinition) {
        return dataSource;
      }
      const { fields } = formDefinition;
      /*
        subWidgetId 从表组件id
        subformField 从表字段
        subformUuid 从表uuid
      */
      const formatDatas = (datas, subformField = false, subformUuid = '', subWidgetId = '', subformTitle = '') => {
        for (let index = 0; index < datas.length; index++) {
          let id, code, name, visible, edit, required, widget, disabled, editDisabled;
          const item = datas[index];

          if (subformField) {
            if (!item.widget) {
              continue;
            }
            id = item.widget.id;
            code = item.widget.configuration.code;
            name = item.widget.configuration.name;
            visible = item.defaultDisplayState !== 'hidden';
            edit = item.defaultDisplayState === 'edit';
            required = item.required ? true : false;
            widget = item.widget;
            disabled = false;
            editDisabled = false;
          } else {
            const { configuration } = item;
            id = item.id;
            code = configuration.code;
            name = configuration.name;
            visible = configuration.defaultDisplayState !== 'hidden';
            edit = configuration.defaultDisplayState === 'edit';
            required = configuration.required ? true : false;
            widget = item;
            disabled = false;
            editDisabled = false;
          }
          const field = {
            id,
            code,
            name,
            visible,
            edit,
            required,
            widget,
            subformField,
            subformUuid,
            subWidgetId,
            subformTitle,
            disabled,
            editDisabled
          };
          dataSource.push(field);
        }
      };
      if (fields && fields.length) {
        formatDatas(fields);
      }
      if (formDefinition.subforms && formDefinition.subforms.length) {
        const { subforms } = formDefinition;
        for (let index = 0; index < subforms.length; index++) {
          const item = subforms[index];
          const { configuration } = item;
          const subformUuid = configuration.formUuid;
          const subWidgetId = item.id;
          const subformTitle = configuration.title;
          dataSource.push({
            id: subWidgetId,
            code: configuration.formName,
            name: subformTitle,
            visible: configuration.defaultDisplayState !== 'hidden',
            edit: configuration.defaultDisplayState === 'edit',
            required: configuration.required ? true : false,
            widget: item,
            isSubWidget: true,
            subformUuid,
            disabled: false
          });
          dataSource.push({
            id: subformUuid,
            code: '',
            name: '全选从表下的字段',
            visible: false,
            edit: false,
            required: false,
            widget: {},
            isSubform: true,
            allCheckbox: true, // 用于判断是否从表全选字段
            disabled: false
          });
          formatDatas(configuration.columns, true, subformUuid, subWidgetId, subformTitle);
        }
      }
      // this.designer.setFormField(dataSource);
      return dataSource;
    },
    refresh(dataSource) {
      this.dataSource = dataSource;
      this.formatDataSource();
    },
    formatDataSource() {
      for (let index = 0; index < this.dataSource.length; index++) {
        const field = this.dataSource[index];
        if (field.isSubform) {
          continue;
        }
        this.setNotNullFields(field);
        this.setHideFields(field);
        if (field.widget.wtype !== 'WidgetFormFileUpload') {
          this.setEditFields(field);
        }
        this.setAllFormFields(field);
        this.setAllFormFieldWidgetIds(field);
      }
      each(filter(this.dataSource, 'allCheckbox'), item => {
        this.setCheckAlltoSubAll(
          {
            subformField: true,
            subformUuid: item.id
          },
          'visible'
        );
        this.setCheckAlltoSubAll(
          {
            subformField: true,
            subformUuid: item.id
          },
          'edit'
        );
        this.setCheckAlltoSubAll(
          {
            subformField: true,
            subformUuid: item.id
          },
          'required'
        );
      });
      this.setCheckAll();
      this.setAllRequired();
      this.setAllEdit();
    },
    // 设置全部表单字段
    setAllFormFields(field) {
      let fieldItem = this.fieldItem();
      fieldItem.value = this.fieldValue(field);
      this.formData.allFormFields.push(fieldItem);
    },
    // 设置全部表单字段编码和id
    setAllFormFieldWidgetIds(field) {
      let fieldItem = this.fieldItem();
      let value = '';
      if (field.isSubWidget) {
        const param = {
          id: field.id,
          outerId: field.widget.configuration.formId,
          formUuid: field.subformUuid
        };
        value = `${field.code}=${JSON.stringify(param)}`;
      } else if (field.subformField) {
        const param = {
          id: field.id,
          subWidgetId: field.subWidgetId
        };
        value = `${field.subformUuid}:${field.code}=${JSON.stringify(param)}`;
      } else {
        const param = {
          id: field.id
        };
        value = `${field.code}=${JSON.stringify(param)}`;
      }
      fieldItem.value = value;
      this.formData.allFormFieldWidgetIds.push(fieldItem);
    },
    // 设置不为空字段
    setNotNullFields(field) {
      const value = this.fieldValue(field);
      const notNullItem = this.notNullFields.find(h => {
        return h.value === value;
      });
      if (notNullItem) {
        field.required = true;
      } else {
        const flowFiled = this.allFormFields.find(f => f.value === value);
        if (flowFiled) {
          field.required = false;
        } else {
          if (field.required) {
            this.addRequiredField(field);
          }
        }
      }
    },
    // 设置编辑字段
    setEditFields(field) {
      const value = this.fieldValue(field);
      const editItem = this.editFields.find(h => {
        return h.value === value;
      });
      if (editItem) {
        field.edit = true;
      } else {
        const flowFiled = this.allFormFields.find(f => f.value === value);
        if (flowFiled) {
          /*
            表单设置修改过
            打开环节配置会向allFormFields、allFormFieldWidgetIds添加，认为环节编辑过；
            又不在editFields中，认为此字段设置成不可编辑
           */
          field.edit = false;
        } else {
          // 新增的字段，取表单配置
          if (field.edit) {
            this.addEditField(field);
          }
        }
      }
    },
    // 设置隐藏字段
    setHideFields(field) {
      const value = this.fieldValue(field);
      const hideItem = this.hideData.find(h => {
        return h.value === value;
      });
      if (hideItem) {
        field.visible = false;
        if (field.subformUuid && !field.subformField) {
          // 从表不显示，所有从表选中项清除，全部从表字段失效
          // this.clearSubformFields(field.subformUuid);
        }
        // 当前项不显示，则它对应的编辑框要设置为失效且取消选中
        this.setDisabledtoItemEditCheckbox(field, true);
      } else {
        const flowFiled = this.allFormFields.find(f => f.value === value);
        if (flowFiled) {
          field.visible = true;
        } else {
          if (!field.visible) {
            this.addHideField(field);
          }
        }
      }
    },
    // 当前项不显示，则它对应的编辑框要设置为失效且取消选中
    setDisabledtoItemEditCheckbox(field, value) {
      // 如果是生效状态，不对编辑复选框做处理
      if (field.widget.wtype !== 'WidgetFormFileUpload') {
        if (value) {
          field.edit = false;
        }
        field.editDisabled = value;
        this.changeFieldEdit(false, field, false);
      }
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
    // 更改显示全选
    changeCheckAll(event) {
      const targetChecked = event.target.checked;
      this.formData.hideFields = [];
      if (targetChecked) {
        // 全显示
        this.dataSource.forEach(item => {
          item.visible = true;
          this.delHideField(item);
        });
      } else {
        // 全隐藏
        this.dataSource.forEach(item => {
          item.visible = false;
          this.addHideField(item);
        });
      }
    },
    // 更改显示
    changeVisible(event, item, value) {
      const targetChecked = event ? event.target.checked : value;
      if (targetChecked) {
        this.delHideField(item);
        if (item.subformUuid && !item.subformField) {
          // 从表显示，全部从表字段生效
          this.setSubformFieldDisabled(item.subformUuid);
        } else if (item.isSubform && !item.subformUuid) {
          // 从表字段全选
          this.subformFieldsSetting(item.id, 'delHideField', 'visible', true);
        }
      } else {
        this.addHideField(item);
        if (item.subformUuid && !item.subformField) {
          // 从表不显示，所有从表选中项清除，全部从表字段失效
          // this.clearSubformFields(item.subformUuid);
        } else if (item.isSubform && !item.subformUuid) {
          // 从表字段全不选
          this.subformFieldsSetting(item.id, 'addHideField', 'visible', false);
        }
      }
      this.setCheckAll();
      this.setCheckAlltoSubAll(item, 'visible');
    },
    // 添加隐藏字段
    addHideField(field) {
      const value = this.fieldValue(field);
      if (value) {
        let fieldItem = this.fieldItem();
        fieldItem.argValue = (field.subformTitle ? field.subformTitle + ':' : '') + field.name;
        fieldItem.value = value;
        this.formData.hideFields.push(fieldItem);
      }
      // 当前项不显示，则它对应的编辑框要设置为失效且取消选中
      this.setDisabledtoItemEditCheckbox(field, true);
    },
    // 删除隐藏字段
    delHideField(field) {
      const value = this.fieldValue(field);
      const hideIndex = this.hideData.findIndex(h => h.value === value);
      if (hideIndex !== -1) {
        this.formData.hideFields.splice(hideIndex, 1);
      }
      // 当前项显示，则它对应的编辑框要设置为生效
      this.setDisabledtoItemEditCheckbox(field, false);
    },
    // 设置显示全选
    setCheckAll() {
      const hideIndex = this.dataSource.findIndex(item => !item.visible && !item.allCheckbox);
      if (hideIndex !== -1) {
        this.checkAll = false;
      } else {
        this.checkAll = true;
      }
    },
    // 设置从表复选框全选
    setCheckAlltoSubAll(field, param) {
      if (field.subformField) {
        const allSubfieldData = filter(this.dataSource, item => {
          return item.subformField && item.subformUuid == field.subformUuid;
        });
        // 未选中的位置，-1则全部选中，其他代表存在未选中项
        const targetIndex = allSubfieldData.findIndex(item => !item[param]);
        const allCheckboxIndex = findIndex(this.dataSource, item => {
          return item.allCheckbox && item.id == field.subformUuid;
        });
        if (allCheckboxIndex > -1) {
          if (targetIndex !== -1) {
            this.$set(this.dataSource[allCheckboxIndex], param, false);
          } else {
            this.$set(this.dataSource[allCheckboxIndex], param, true);
          }
        }
      }
    },
    // 从表不显示，所有从表选中项清除，全部从表字段失效
    clearSubformFields(subformUuid) {
      each(this.dataSource, item => {
        if (item.id == subformUuid || (item.subformUuid == subformUuid && item.subformField)) {
          item.visible = false;
          item.edit = false;
          item.required = false;
          item.disabled = true;
          item.editDisabled = true;
          this.addHideField(item);
          // this.changeFieldEdit(false, item, false);
          this.delRequiredField(item);
        }
      });
      this.setAllRequired();
    },
    // 从表下所有复选框生效
    setSubformFieldDisabled(subformUuid) {
      each(this.dataSource, item => {
        if (item.id == subformUuid || (item.subformUuid == subformUuid && item.subformField)) {
          item.disabled = false;
          // item.editDisabled = false;
        }
      });
    },
    subformFieldsSetting(subformUuid, funName, param, value) {
      each(this.dataSource, item => {
        if (item.subformUuid == subformUuid && item.subformField) {
          if (param) {
            item[param] = value;
          }
          if (typeof this[funName] == 'function') {
            this[funName](item);
          }
        }
      });
    },
    // 更改必填全选
    changeAllRequired(event) {
      const targetChecked = event.target.checked;
      this.formData.notNullFields = [];
      if (targetChecked) {
        // 全必填
        this.dataSource.forEach(item => {
          item.required = true;
          this.addRequiredField(item);
        });
      } else {
        this.dataSource.forEach(item => {
          item.required = false;
          this.delRequiredField(item);
        });
      }
    },
    // 更改必填
    changeRequired(event, item, value) {
      const targetChecked = event ? event.target.checked : value;
      if (targetChecked) {
        this.addRequiredField(item);
        if (item.isSubform && !item.subformUuid) {
          // 从表字段全选
          this.subformFieldsSetting(item.id, 'addRequiredField', 'required', true);
        }
      } else {
        this.delRequiredField(item);
        if (item.isSubform && !item.subformUuid) {
          // 从表字段全选
          this.subformFieldsSetting(item.id, 'delRequiredField', 'required', false);
        }
      }
      this.setAllRequired();
      this.setCheckAlltoSubAll(item, 'required');
    },
    // 添加必填字段
    addRequiredField(field) {
      const value = this.fieldValue(field);
      if (value) {
        let fieldItem = this.fieldItem();
        fieldItem.argValue = (field.subformTitle ? field.subformTitle + ':' : '') + field.name;
        fieldItem.value = value;
        this.formData.notNullFields.push(fieldItem);
      }
    },
    // 删除必填字段
    delRequiredField(field) {
      const value = this.fieldValue(field);
      const notNullIndex = this.notNullFields.findIndex(h => h.value === value);
      if (notNullIndex !== -1) {
        this.formData.notNullFields.splice(notNullIndex, 1);
      }
    },
    // 设置必填全选
    setAllRequired() {
      const notNullIndex = this.dataSource.findIndex(item => !item.required && !item.allCheckbox && !item.isSubWidget);
      if (notNullIndex !== -1) {
        this.checkAllRequired = false;
      } else {
        this.checkAllRequired = true;
      }
    },
    // 更改编辑全选
    changeAllEdit(event) {
      const targetChecked = event.target.checked;
      this.formData.editFields = [];
      if (targetChecked) {
        // 全编辑
        this.dataSource.forEach(item => {
          // 失效状态不做处理
          if (!item.editDisabled || item.widget.wtype !== 'WidgetFormFileUpload') {
            item.edit = true;
            this.addEditField(item);
          }
        });
      } else {
        this.dataSource.forEach(item => {
          // 附件不做相关处理
          if (item.widget.wtype !== 'WidgetFormFileUpload') {
            item.edit = false;
            this.delEditField(item);
          }
        });
      }
    },
    // 更改编辑
    changeFieldEdit(event, item, value) {
      const targetChecked = event ? event.target.checked : value;
      if (targetChecked) {
        this.addEditField(item);
        if (item.isSubform && !item.subformUuid) {
          // 从表字段全选
          if (item.widget.wtype !== 'WidgetFormFileUpload') {
            this.subformFieldsSetting(item.id, 'addEditField', 'edit', true);
          }
        }
      } else {
        this.delEditField(item);
        if (item.isSubform && !item.subformUuid) {
          // 从表字段全选
          if (item.widget.wtype !== 'WidgetFormFileUpload') {
            this.subformFieldsSetting(item.id, 'delEditField', 'edit', false);
          }
        }
      }
      this.setAllEdit();
      this.setCheckAlltoSubAll(item, 'edit');
    },
    // 添加编辑字段
    addEditField(field) {
      const value = this.fieldValue(field);
      if (value) {
        const editIndex = this.editFields.findIndex(h => h.value === value);
        if (editIndex === -1) {
          let fieldItem = this.fieldItem();
          fieldItem.argValue = (field.subformTitle ? field.subformTitle + ':' : '') + field.name;
          fieldItem.value = value;
          this.formData.editFields.push(fieldItem);
        }
      }
    },
    // 删除编辑字段
    delEditField(field) {
      const value = this.fieldValue(field);
      const editIndex = this.editFields.findIndex(h => h.value === value);
      if (editIndex !== -1) {
        this.formData.editFields.splice(editIndex, 1);
      }
    },
    // 设置编辑全选
    setAllEdit() {
      const editIndex = this.dataSource.findIndex(
        item => !item.edit && !item.allCheckbox && !item.isSubWidget && item.widget.wtype !== 'WidgetFormFileUpload'
      );
      if (editIndex !== -1) {
        this.checkAllEdit = false;
      } else {
        this.checkAllEdit = true;
      }
    },
    // 从表扩展设置
    handleSubformButton() {},
    // 附件扩展设置
    handleFileUploadExtend() {},
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    },
    onSearch(value) {
      this.searchVal = value || '';
    },
    textFilter(text) {
      let hasIndex = text.indexOf(this.searchVal);
      if (hasIndex > -1) {
        var newText = text.split(this.searchVal);
        var text = newText.join("<span style='color:red;'>" + this.searchVal + '</span>');
      }
      return text;
    },
    modalShow() {
      this.$emit('modalShow', {
        dataSource: this.dataSource
      });
    },
    emitChange() {
      this.$emit('input', this.dataSource);
    }
  }
};
</script>
