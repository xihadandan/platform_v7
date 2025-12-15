<template>
  <a-modal
    dialogClass="pt-modal wf-merge-changed-modal"
    :title="title"
    :visible="visible"
    :width="980"
    :maskClosable="false"
    @ok="onMergeOk"
    @cancel="onMergeCancel"
  >
    <div>
      <a-alert type="warning" :message="warningText" showIcon class="warning-text">
        <a-button slot="closeText" type="link" @click.stop="onMergeAllClick" style="position: relative; top: -3px">
          <a-icon type="redo" rotate="-90" />
          {{ $t('WorkflowWork.mergeData.updateAll', '全部更新') }}
        </a-button>
      </a-alert>
    </div>
    <div style="height: 450px; overflow: auto">
      <a-card size="small" :bordered="false">
        <template slot="title">
          <label class="title">
            <i class="prefix"></i>
            {{ $t('WorkflowWork.mergeData.fieldValueChanged', '字段值变更') }}
          </label>
        </template>
        <a-table
          rowKey="code"
          :columns="fieldChangeTableColumns"
          :data-source="mainFormFields"
          :locale="locale"
          :pagination="false"
          :scroll="{ x: true, y: 346 }"
        >
          <template slot="nameSlot" slot-scope="text, record">
            <font v-if="record.required" color="red">*</font>
            <span :title="record.code">{{ text }}</span>
          </template>
          <template slot="newValueSlot" slot-scope="text, record">
            <a-row type="flex" align="middle">
              <a-col :span="22" style="overflow: auto">
                <FieldContainer
                  :field="record"
                  :fieldName="record.code"
                  :endFieldName="record.endCode"
                  :fieldValue="record.newValue"
                  :endFieldValue="record.newEndValue"
                ></FieldContainer>
              </a-col>
              <a-col :span="2" style="text-align: center">
                <a-popover placement="right">
                  <template slot="content">
                    <p>编辑人员：{{ record.modifier }}</p>
                    <p>操作时间：{{ record.modifyTime }}</p>
                  </template>
                  <a-icon type="info-circle" />
                </a-popover>
              </a-col>
            </a-row>
          </template>
          <template slot="oldValueSlot" slot-scope="text, record">
            <FieldContainer
              :field="record"
              :fieldName="record.code"
              :endFieldName="record.endCode"
              :fieldValue="record.oldValue"
              :endFieldValue="record.oldEndValue"
              :isOldField="true"
            ></FieldContainer>
          </template>
          <template slot="operationSlot" slot-scope="text, record">
            <a-button v-if="!record.merged" type="default" @click="onMergeFieldClick(record)">
              <a-icon type="redo" rotate="-90" />
              {{ $t('WorkflowWork.mergeData.update', '更新') }}
            </a-button>
            <a-button v-if="record.merged" type="default" @click="onRestoreFieldClick(record)">
              <a-icon type="undo" rotate="90" />
              {{ $t('WorkflowWork.mergeData.restore', '恢复') }}
            </a-button>
          </template>
        </a-table>
      </a-card>
      <a-card v-if="subforms && subforms.length" size="small" :bordered="false">
        <template slot="title">
          <label class="title">
            <i class="prefix"></i>
            {{ $t('WorkflowWork.mergeData.tableDataChanged', '表格数据变更') }}
            <a-popover>
              <template slot="content">
                <span v-html="tableMergeRule"></span>
              </template>
              <a-icon type="info-circle" />
            </a-popover>
          </label>
        </template>
        <a-tabs :bordered="false">
          <a-tab-pane v-for="subform in subforms" :tab="subform.name" :key="subform.code" forceRender>
            {{ subform.name }}
            <a-tag>{{ $t('WorkflowWork.mergeData.latestValue', '最新值') }}</a-tag>
            <a-table
              rowKey="uuid"
              :columns="subform.newColumns"
              :data-source="subform.newValue"
              :locale="locale"
              :pagination="false"
              :scroll="{ x: subform.scrollX }"
            >
              <template v-for="(slotOption, i) in subform.customColTitleSlotOptions" :slot="slotOption.slotName">
                <span
                  :class="[slotOption.required ? 'ant-form-item-required' : '']"
                  :key="'require-col-title' + i"
                  :title="slotOption.title"
                >
                  {{ slotOption.title }}
                </span>
              </template>
              <template slot="sortOrderSlot" slot-scope="text, record, index">
                {{ index + 1 }}
              </template>
              <template slot="changedTypeSlot" slot-scope="text, record">
                <span v-if="record.__ADDED__">{{ $t('WorkflowWork.mergeData.added', '新增') }}</span>
                <span v-else-if="record.__UPDATED__">{{ $t('WorkflowWork.mergeData.updated', '更新') }}</span>
                <span v-else-if="record.__DELETED_LOCAL__">{{ $t('WorkflowWork.mergeData.localDeleted', '本地删除') }}</span>
                <span v-else-if="record.__DELETED__">{{ $t('WorkflowWork.mergeData.deleted', '删除') }}</span>
                <span v-else>{{ $t('WorkflowWork.mergeData.noChanged', '无变更') }}</span>
              </template>
            </a-table>
            <p />
            {{ subform.name }}
            <a-tag color="red">{{ $t('WorkflowWork.mergeData.currentValue', '当前值') }}</a-tag>
            <a-button v-if="!subform.merged" type="link" @click="onMergeSubformClick(subform)">
              <a-icon type="redo" rotate="-90" />
              {{ $t('WorkflowWork.mergeData.update', '更新') }}
            </a-button>
            <a-button v-else type="link" @click="onRestoreSubformClick(subform)">
              <a-icon type="undo" rotate="-90" />
              {{ $t('WorkflowWork.mergeData.restore', '恢复') }}
            </a-button>
            <a-table
              v-if="!subform.restored"
              rowKey="uuid"
              :columns="subform.oldColumns"
              :data-source="subform.oldValue"
              :customRow="customOldRow"
              :locale="locale"
              :pagination="false"
              :scroll="{ x: subform.scrollX }"
            >
              <template v-for="(slotOption, i) in subform.customColTitleSlotOptions" :slot="slotOption.slotName">
                <span
                  :class="[slotOption.required ? 'ant-form-item-required' : '']"
                  :key="'require-col-title' + i"
                  :title="slotOption.title"
                >
                  {{ slotOption.title }}
                </span>
              </template>
              <template slot="sortOrderSlot" slot-scope="text, record, index">
                {{ index + 1 }}
              </template>
            </a-table>
          </a-tab-pane>
        </a-tabs>
      </a-card>
    </div>
    <template slot="footer">
      <a-button @click="onMergeCancel">
        {{ $t('WorkflowWork.opinionManager.operation.cancel', '取消') }}
      </a-button>
      <a-button type="primary" @click="onMergeOk">
        {{ $t('WorkflowWork.mergeData.saveCurrentValue', '保存当前值') }}
      </a-button>
      <a-button v-for="key in Object.keys(buttons)" :key="key" type="primary" @click="onMergeCallbackClick(buttons[key])">
        {{ buttons[key].label }}
      </a-button>
    </template>
  </a-modal>
</template>

<script>
import { isArray, trim, debounce, isEmpty } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';
import { createDyform } from '@dyform/app/web/framework/vue/dyform/dyform';
import Vue from 'vue';

let FieldContainer = Vue.extend({
  template: `<div :style="style"><component
              :is="field.widget.wtype"
              :widget="field.widget"
              :key="field.widget.id"
              :index="field.index"
              :widgetsOfParent="[]"
            /></div>`,
  props: {
    field: Object,
    fieldName: String,
    endFieldName: String,
    fieldValue: String || Number || Array || Object,
    endFieldValue: Object,
    isOldField: Boolean
  },
  data() {
    let style = {};
    if (this.field.widget.wtype === 'WidgetFormFileUpload' && this.field.widget.configuration.type == 'advancedList') {
      style.width = '500px';
    } else if (this.field.widget.wtype === 'WidgetFormDatePicker' && this.field.widget.configuration.range) {
      style.overflow = 'auto';
    }
    return {
      style
    };
  },
  provide() {
    const _this = this;
    let $dyform = _this.field.$dyform;
    let dyform = createDyform($dyform.dyform.formUuid);
    if (_this.field.newValue == _this.fieldValue) {
      dyform.formElementRules = {};
      dyform.formElementRules[_this.field.widget.id] = { editable: false, hidden: false };
    } else {
      dyform.formElementRules = $dyform.formElementRules;
      if (dyform.formElementRules[_this.field.widget.id]) {
        dyform.formElementRules[_this.field.widget.id].hidden = false;
      } else {
        dyform.formElementRules[_this.field.widget.id] = { editable: false, hidden: false };
      }
    }
    dyform.formData[_this.fieldName] = _this.fieldValue;
    if (_this.endFieldName) {
      dyform.formData[_this.endFieldName] = _this.endFieldValue;
    }
    dyform.dataUuid = $dyform.dyform.dataUuid;
    if (_this.isOldField) {
      _this.field.dyform = dyform;
    } else {
      _this.field._dyform = dyform;
    }

    let provided = { ...$dyform._provided };
    provided.dyform = dyform;
    provided.pageContext = $dyform.pageContext;
    provided.locale = $dyform.locale;
    if (provided.locale && !provided.locale.emptyText && provided.locale.Empty && provided.locale.Empty.description) {
      provided.locale.emptyText = provided.locale.Empty.description;
    }
    provided.isNewFormData = false;
    return provided;
  }
});

let SubformFieldContainer = Vue.extend({
  template: `<div><component
              :is="field.widget.wtype"
              :widget="field.widget"
              :key="field.widget.id"
              :index="index"
              :widgetsOfParent="[]"
            /></div>`,
  props: {
    subform: Object,
    column: Object,
    index: Number,
    record: Object,
    recordsProp: String,
    displayAsLabel: Boolean
  },
  data() {
    const _this = this;
    let records = _this.subform[_this.recordsProp];
    let dyform = records.find(subform => subform.formData.uuid == _this.record.uuid);
    if (dyform == null) {
      dyform = createDyform(_this.subform.formUuid, _this.record.uuid);
      dyform.formData = _this.record;
      records.push(dyform);
    }
    if (_this.displayAsLabel) {
      dyform.formElementRules = dyform.formElementRules || {};
      dyform.formElementRules[_this.column.widget.id] = { editable: false, hidden: false };
    } else {
      dyform.formElementRules = _this.subform.$dyform.formElementRules;
    }
    let $dyform = _this.subform.$dyform;
    dyform.dataUuid = $dyform.dyform.dataUuid;

    return {
      field: this.column
    };
  },
  provide() {
    const _this = this;
    let records = _this.subform[_this.recordsProp];
    let $dyform = _this.subform.$dyform;
    let provided = { ...$dyform._provided };
    let dyform = records.find(subform => subform.formData.uuid == _this.record.uuid);
    provided.dyform = dyform;
    provided.pageContext = $dyform.pageContext;
    provided.locale = $dyform.locale;
    if (provided.locale && !provided.locale.emptyText && provided.locale.Empty && provided.locale.Empty.description) {
      provided.locale.emptyText = provided.locale.Empty.description;
    }
    provided.isNewFormData = false;
    return provided;
  },
  mounted() {
    const _this = this;
    if (_this.record.__UPDATED_FIELDS__ && _this.record.__UPDATED_FIELDS__.includes(_this.column.dataIndex)) {
      _this.markFieldStyle({ color: 'red' }, { backgroundColor: 'red' }, 1);
    }
    if (_this.record.__DELETED__ || _this.record.__DELETED_LOCAL__) {
      _this.markFieldStyle({ 'text-decoration': 'line-through' }, {}, 1);
    }
  },
  methods: {
    markFieldStyle(fieldStyle = {}, containerStyle = {}, counter) {
      const _this = this;
      let text = trim(_this.$el.innerText);
      if (text) {
        let textonlyEl = _this.$el.querySelector('.textonly');
        if (textonlyEl) {
          // textonlyEl.style.color = 'red';
          for (let key in fieldStyle) {
            textonlyEl.style[key] = fieldStyle[key];
          }
        } else {
          if (_this.$el.parentNode) {
            // _this.$el.parentNode.style.backgroundColor = 'red';
            for (let key in containerStyle) {
              _this.$el.parentNode.style[key] = containerStyle[key];
            }
          }
        }
      } else {
        if (counter < 5) {
          setTimeout(() => {
            _this.markFieldStyle(fieldStyle, containerStyle, ++counter);
          }, 300);
        } else {
          if (_this.$el.parentNode) {
            // _this.$el.parentNode.style.backgroundColor = 'red';
            for (let key in containerStyle) {
              _this.$el.parentNode.style[key] = containerStyle[key];
            }
          } else {
            // _this.$el.style.backgroundColor = 'red';
            for (let key in containerStyle) {
              _this.$el.style[key] = containerStyle[key];
            }
          }
        }
      }
    }
  }
});

export default {
  name: 'WorkflowMergeChangedModal',
  props: {
    workView: Object,
    expectTaskInstRecVer: Number,
    action: String,
    actionType: String,
    buttons: {
      type: Object,
      default: () => ({})
    },
    watchValue: Boolean
  },
  components: { FieldContainer, SubformFieldContainer },
  data() {
    const _this = this;
    return {
      title: _this.$t('WorkflowWork.mergeData.mergeChangedTitle', '合并表单数据变更'),
      actionTranslate: _this.action,
      visible: false,
      latestFormDatas: {},
      fieldChangeTableColumns: [
        {
          title: _this.$t('WorkflowWork.mergeData.changeField', '变更字段'),
          dataIndex: 'name',
          width: 120,
          scopedSlots: { customRender: 'nameSlot' }
        },
        {
          title: _this.$t('WorkflowWork.mergeData.latestValue', '最新值'),
          dataIndex: 'newValue',
          width: 240,
          scopedSlots: { customRender: 'newValueSlot' }
        },
        {
          title: _this.$t('WorkflowWork.mergeData.currentValue', '当前值'),
          width: 240,
          dataIndex: 'oldValue',
          scopedSlots: { customRender: 'oldValueSlot' }
        },
        {
          title: _this.$t('WorkflowWork.mergeData.operation', '操作'),
          width: 150,
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      mainFormFields: [],
      subforms: [],
      locale: {
        emptyText: <span>暂无数据</span>
      }
    };
  },
  computed: {
    warningText() {
      let defaultText = `其他用户已${this.actionTranslate}流程，数据发生变更(${this.mainFormFields.length}字段${this.subforms.length}表格)，请确认数据后继续操作`;
      let text = this.$t(
        'WorkflowWork.mergeData.warningText',
        {
          action: this.actionTranslate,
          mainFormFieldCount: this.mainFormFields.length,
          subformCount: this.subforms.length
        },
        defaultText
      );
      return text;
    },
    tableMergeRule() {
      let defaultText = `1、无冲突的行数据不显示，包括本地新增的行数据;
                <br />
                2、最新值或当前值有更新的行数据标识为更新，更新后覆盖当前值;
                <br />
                3、最新值新增的行数据标识为新增，更新后添加到当前值中;
                <br />
                4、最新值删除的行数据标识为删除，更新后从当前值中删除;
                <br />
                5、当前值删除的行数据标识为本地删除，更新后恢复到当前值中。
                <br />`;
      let text = this.$t('WorkflowWork.mergeData.tableMergeRule', defaultText);
      return text;
    }
  },
  created() {
    if (this.$i18n && this.$i18n.locale.split('_')[0] != 'zh') {
      this.$translate(this.action, 'zh', this.$i18n.locale.split('_')[0]).then(text => {
        if (text) {
          this.actionTranslate = text;
        }
      });
    }
  },
  methods: {
    show() {
      const _this = this;
      let workData = _this.workView.getWorkData();
      let dyFormData = workData.dyFormData;
      _this.loadFormDatas(dyFormData.formUuid, dyFormData.dataUuid || workData.dataUuid).then(({ mainFormFields, subforms }) => {
        if (!mainFormFields || mainFormFields.length || !subforms || subforms.length) {
          _this.visible = true;
          setTimeout(() => {
            _this.mainFormFields = mainFormFields || [];
            _this.subforms = subforms || [];
          }, 1);
        } else {
          // 没有变更数据，自动合并流程版本信息
          if (_this.buttons && _this.buttons.confirm) {
            _this.doMergeAndCallback(_this.buttons.confirm);
          } else {
            _this.doMerge();
          }
        }
      });
    },
    hide() {
      this.onMergeCancel();
    },
    loadFormDatas(formUuid, dataUuid) {
      let mainFormFields = [];
      let subforms = [];
      return $axios
        .post(`/proxy/api/dyform/data/getFormData?formUuid=${formUuid}&dataUuid=${dataUuid}`)
        .then(({ data: result }) => {
          if (result.data) {
            this.latestFormDatas = result.data;
            let dyFormData = this.workView.collectFormData();
            mainFormFields = this.getMainFormFields(this.latestFormDatas, dyFormData);
            this.loadMainFormFieldModifyInfo(mainFormFields);
            subforms = this.getSubforms(this.latestFormDatas, dyFormData);
          }
          return { mainFormFields: mainFormFields, subforms: subforms };
        })
        .catch(e => {
          return { mainFormFields: mainFormFields, subforms: subforms };
        });
    },
    loadMainFormFieldModifyInfo(mainFormFields) {
      let fieldNames = mainFormFields.map(field => field.code);
      let workData = this.workView.getWorkData();
      let flowInstUuid = workData.flowInstUuid;
      $axios.post(`/proxy/api/workflow/work/getDyformFieldModifyInfo`, { flowInstUuid, fieldNames }).then(({ data: result }) => {
        if (result.data) {
          let formFieldMap = result.data.formFieldMap || {};
          mainFormFields.forEach(field => {
            let modifyInfo = formFieldMap[field.code];
            if (modifyInfo) {
              field.modifier = modifyInfo.modifyUserName;
              field.modifyTime = modifyInfo.modifyTime;
            }
          });
        }
      });
    },
    getMainFormFields(newFormDatas, dyFormData) {
      const _this = this;
      let mainFormFields = [];
      let newMainFormData = newFormDatas[dyFormData.formUuid][0] || {};
      let oldMainFormData = dyFormData.formDatas[dyFormData.formUuid][0] || {};
      let $dyform = _this.workView.getDyformWidget();
      let index = 0;
      let dateRangeFieldMap = {};
      let displayValueFieldMap = {};
      for (let key in newMainFormData) {
        let fieldName = key;
        if (_this.isSystemField(fieldName) || _this.isRecordField(fieldName) || dateRangeFieldMap[fieldName]) {
          continue;
        }
        let field = $dyform.dyform.getField(fieldName);
        if (!field || !field.configuration) {
          continue;
        }
        if (field.widget.configuration.displayValueField) {
          (function () {
            let displayValueField = field.widget.configuration.displayValueField;
            displayValueFieldMap[displayValueField] = field.widget.configuration.code;
            let $field = $dyform.dyform.getField(displayValueField);
            if (_this.watchValue && $field && $field.$refs[displayValueField]) {
              let onFieldChange = $field.$refs[displayValueField].onFieldChange;
              $field.$refs[displayValueField].onFieldChange = function () {
                onFieldChange && onFieldChange.apply($field, arguments);
                let mainFormField = _this.mainFormFields.find(mainFormField => mainFormField.code == displayValueField);
                if (mainFormField) {
                  mainFormField.oldValue = $field.getValue(displayValueField);
                  mainFormField.dyform.formData[displayValueField] = mainFormField.oldValue;
                }
              };
            }
          })();
        }

        let newValue = newMainFormData[fieldName];
        let oldValue = oldMainFormData[fieldName];
        let endFieldName = undefined;
        let newEndValue = undefined;
        let oldEndValue = undefined;
        if (!_this.isDiff(newValue, oldValue)) {
          continue;
        }
        let widget = deepClone(field.widget);
        if (widget.wtype === 'WidgetFormDatePicker' && widget.configuration && widget.configuration.range) {
          endFieldName = widget.configuration.endDateField;
          if (fieldName === endFieldName) {
            fieldName = widget.configuration.code;
            newEndValue = newValue;
            oldEndValue = oldValue;
            newValue = newMainFormData[fieldName];
            oldValue = oldMainFormData[fieldName];
          } else {
            newEndValue = newMainFormData[endFieldName];
            oldEndValue = oldMainFormData[endFieldName];
          }
          dateRangeFieldMap[fieldName] = fieldName;
          dateRangeFieldMap[endFieldName] = endFieldName;
        }
        widget.configuration.fieldNameVisible = false;
        // if (widget.configuration.defaultDisplayState === 'hidden') {
        //   widget.configuration.defaultDisplayState = 'unedit';
        // }
        // 禁用计算公式
        widget.configuration.formula = {};
        let fieldRule = $dyform.formElementRules && $dyform.formElementRules[widget.id];
        mainFormFields.push({
          name: field.configuration.name,
          code: fieldName,
          endCode: endFieldName,
          newValue: newValue,
          newEndValue,
          oldValue: oldValue,
          oldEndValue,
          required: fieldRule && fieldRule.required,
          modifier: newMainFormData.modifier,
          modifyTime: newMainFormData.modify_time,
          merged: false,
          widget,
          index,
          $dyform
        });
        index++;
      }

      // 显示值字段有变更时，要确保存在对应的真实值也有变更
      if (!isEmpty(displayValueFieldMap)) {
        for (let key in displayValueFieldMap) {
          let displayField = key;
          let realField = displayValueFieldMap[key];
          let realFieldIndex = mainFormFields.findIndex(field => field.code === realField);
          if (realFieldIndex == -1) {
            mainFormFields = mainFormFields.filter(field => field.code !== displayField);
          } else if (_this.watchValue) {
            // 更新值
            let mainFormField = mainFormFields.find(field => field.code === displayField);
            let $field = $dyform.dyform.getField(displayField);
            if (mainFormField && $field) {
              mainFormField.oldValue = $field.getValue(displayField);
            }
          }
        }
      }
      return mainFormFields;
    },
    getSubforms(newFormDatas, dyFormData) {
      const _this = this;
      let subforms = [];
      let $dyform = _this.workView.getDyformWidget();
      let formDatas = dyFormData.formDatas || {};
      for (let subformUuid in formDatas) {
        if (subformUuid == dyFormData.formUuid) {
          continue;
        }
        let $subform = $dyform.dyform.$subform[subformUuid];
        if (!$subform) {
          continue;
        }

        let newValue = newFormDatas[subformUuid] || [];
        let oldValue = formDatas[subformUuid] || [];
        oldValue.forEach(row => {
          delete row.__ADDED__;
          delete row.__DELETED__;
          delete row.__UPDATED__;
          delete row.__UPDATED_FIELDS__;
          delete row.__DELETED_LOCAL__;
        });
        let initValue = $dyform.originFormData[subformUuid] || [];
        let updatedUuids = _this.markAndGetUpdatedUuids(newValue, oldValue);
        let deletedNewUuids = _this.markAndGetDeletedUuids(initValue, newValue);
        let deletedOldUuids = _this.markAndGetDeletedUuids(initValue, oldValue);
        let addedNewUuids = _this.markAndGetAddedUuids(initValue, newValue);
        let addedOldUuids = _this.markAndGetAddedUuids(initValue, oldValue);
        let deletedNewRows = initValue.filter(row => deletedNewUuids.includes(row.uuid));
        let deletedOldRows = deepClone(initValue.filter(row => deletedOldUuids.includes(row.uuid)));
        deletedOldRows.forEach(row => {
          let newRow = newValue.find(newRow => newRow.uuid == row.uuid);
          if (newRow) {
            Object.assign(row, deepClone(newRow));
            delete row.__ADDED__;
            delete row.__DELETED__;
            delete row.__UPDATED__;
            // delete row.__UPDATED_FIELDS__;
            // delete row.__DELETED_LOCAL__;
          }
          delete row.__DELETED__;
          row.__DELETED_LOCAL__ = true;
        });
        let widget = deepClone($subform.widget);
        let subform = {
          $dyform,
          newRecords: [],
          oldRecords: [],
          name: widget.configuration.title || widget.configuration.formName,
          code: widget.configuration.formId,
          formUuid: subformUuid,
          widget,
          newValue: [...newValue.filter(row => row.__ADDED__ || updatedUuids.includes(row.uuid)), ...deletedNewRows, ...deletedOldRows],
          oldValue: oldValue.filter(row => !row.__ADDED__ && row.__UPDATED__ !== false),
          updatedUuids,
          deletedNewUuids,
          deletedOldUuids,
          addedNewUuids,
          addedOldUuids,
          changed: updatedUuids.length || deletedNewUuids.length || deletedOldUuids.length || addedNewUuids.length,
          restored: false
        };
        if (!subform.changed) {
          continue;
        }

        subform.newColumns = _this.getNewColumns(widget, subform);
        subform.oldColumns = _this.getOldColumns(widget, subform);
        subform.customColTitleSlotOptions = _this.getCustomColTitleSlotOptions(widget, subform);
        subform.scrollX = $subform.scrollX;
        subforms.push(subform);
      }
      return subforms;
    },
    markAndGetAddedUuids(newRows, oldRows) {
      const _this = this;
      let addedOldUuids = [];
      oldRows.forEach(oldRow => {
        let newRow = newRows.find(newRow => newRow.uuid == oldRow.uuid);
        if (newRow == null) {
          oldRow.__ADDED__ = true;
          addedOldUuids.push(oldRow.uuid);
        }
      });
      return addedOldUuids;
    },
    markAndGetDeletedUuids(newRows, oldRows) {
      const _this = this;
      let deletedUuids = [];
      newRows.forEach(newRow => {
        let oldRow = oldRows.find(oldRow => oldRow.uuid == newRow.uuid);
        if (oldRow == null) {
          newRow.__DELETED__ = true;
          deletedUuids.push(newRow.uuid);
        }
      });
      return deletedUuids;
    },
    markAndGetUpdatedUuids(newRows, oldRows) {
      const _this = this;
      let updatedUuids = [];
      newRows.forEach(newRow => {
        let oldRow = oldRows.find(oldRow => oldRow.uuid == newRow.uuid);
        if (oldRow == null) {
          return;
        }
        let updateFields = [];
        for (let key in newRow) {
          if (_this.isSystemField(key)) {
            continue;
          }
          let newValue = newRow[key];
          let oldValue = oldRow[key];
          if (_this.isDiff(newValue, oldValue)) {
            updateFields.push(key);
          }
        }
        if (updateFields.length > 0) {
          newRow.__UPDATED_FIELDS__ = updateFields;
          newRow.__UPDATED__ = true;
          oldRow.__UPDATED__ = true;
          updatedUuids.push(newRow.uuid);
        } else {
          newRow.__UPDATED__ = false;
          oldRow.__UPDATED__ = false;
        }
      });
      return updatedUuids;
    },
    getCustomColTitleSlotOptions(widget, subform) {
      let slotOptions = [];
      let columns = widget.configuration.columns;
      columns.forEach((column, columnIndex) => {
        slotOptions.push({
          title: column.title,
          slotName: `columnTitle-${column.id}`,
          required: column.required,
          dataIndex: column.dataIndex
        });
      });
      return slotOptions;
    },
    getNewColumns(widget, subform) {
      let columns = deepClone(widget.configuration.columns);
      columns.forEach((column, columnIndex) => {
        column.widget.configuration.fieldNameVisible = false;
        column.customRender = (text, record, index) => {
          return {
            children: (
              <SubformFieldContainer
                subform={subform}
                column={column}
                record={record}
                index={columnIndex}
                recordsProp={'newRecords'}
                displayAsLabel={true}
              />
            )
          };
        };
        // column.customCell = (record, rowIndex) => {
        //   if (record.__UPDATED_FIELDS__ && record.__UPDATED_FIELDS__.includes(column.dataIndex)) {
        //     return { style: 'color:green', class: 'changed' };
        //   }
        //   return {};
        // };
        column.scopedSlots = {};
        column.scopedSlots.title = `columnTitle-${column.id}`;
        delete column.title;
      });
      columns.push({
        title: this.$t('WorkflowWork.mergeData.changeType', '变更情况'),
        dataIndex: 'changedType',
        width: 120,
        scopedSlots: { customRender: 'changedTypeSlot' }
      });
      columns = [
        { title: '序号', dataIndex: 'sortOrder', width: '80px', align: 'center', scopedSlots: { customRender: 'sortOrderSlot' } },
        ...columns
      ];
      return columns;
    },
    getOldColumns(widget, subform) {
      let columns = deepClone(widget.configuration.columns);
      columns.forEach((column, columnIndex) => {
        column.widget.configuration.fieldNameVisible = false;
        if (column.widget.configuration.type == 'textarea') {
          column.widget.configuration.allowResize = true;
        }
        column.customRender = (text, record, index) => {
          return {
            children: (
              <SubformFieldContainer subform={subform} column={column} record={record} index={columnIndex} recordsProp={'oldRecords'} />
            )
          };
        };
        column.scopedSlots = {};
        column.scopedSlots.title = `columnTitle-${column.id}`;
        delete column.title;
      });
      columns = [
        { title: '序号', dataIndex: 'sortOrder', width: '80px', align: 'center', scopedSlots: { customRender: 'sortOrderSlot' } },
        ...columns
      ];
      return columns;
    },
    customOldRow(record, index) {
      if (record.__DELETED__) {
        return { style: { display: 'none' } };
      }
      return {};
    },
    isDiff(newValue, oldValue) {
      return this.workView.isDiff(newValue, oldValue);
    },
    isSystemField(fieldName) {
      return this.workView.isDyformSystemField(fieldName);
    },
    isRecordField(fieldName) {
      const _this = this;
      let workData = _this.workView.getWorkData();
      let records = workData.records;
      if (!records || records.length == 0) {
        return false;
      }
      return records.findIndex(record => record.field == fieldName) != -1;
    },
    onMergeFieldClick(record) {
      // record.oldValue = record.newValue;
      // record.formData[record.code] = record.newValue;
      try {
        if (record.endCode) {
          record.dyform.formData[record.endCode] = record.newEndValue;
        }
        record.dyform.setFieldValue(record.code, record.newValue);
        record.merged = true;
      } catch (e) {
        console.error(e);
      }
    },
    onRestoreFieldClick(record) {
      // record.oldValue = record.currentValue;
      // record.formData[record.code] = record.oldValue;
      if (record.endCode) {
        record.dyform.formData[record.endCode] = record.oldEndValue;
      }
      record.dyform.setFieldValue(record.code, record.oldValue);
      record.merged = false;
    },
    onMergeAllClick() {
      const _this = this;
      _this.mainFormFields.forEach(field => {
        _this.onMergeFieldClick(field);
      });
      _this.subforms.forEach(subform => {
        _this.onMergeSubformClick(subform);
      });
    },
    onMergeSubformClick(subform) {
      subform._oldValue = subform._oldValue || deepClone(subform.oldValue);
      subform.merged = true;

      // 更新
      let updatedRows = (subform.newValue || []).filter(row => subform.updatedUuids && subform.updatedUuids.includes(row.uuid));
      updatedRows.forEach(updatedRow => {
        let record = subform.oldRecords.find(record => record.formData.uuid == updatedRow.uuid);
        if (record) {
          for (let key in updatedRow) {
            if (!key || key.startsWith('__')) {
              continue;
            }
            record.setFieldValue(key, updatedRow[key]);
          }
        }
      });

      // 新增
      let addedNewRows = (subform.newValue || []).filter(row => {
        if (row.__DELETED_LOCAL__) {
          return false;
        }
        let oldRow = subform.oldValue && subform.oldValue.find(oldRow => oldRow.uuid == row.uuid);
        return oldRow == null;
      });
      // 本地删除
      let deletedLocalRows = (subform.newValue || []).filter(row => row.__DELETED_LOCAL__);
      subform.oldValue = [...subform.oldValue, ...deepClone(addedNewRows), ...deletedLocalRows];

      // 删除
      let deletedRows = (subform.oldValue || []).filter(row => subform.deletedNewUuids && subform.deletedNewUuids.includes(row.uuid));
      deletedRows.forEach(deletedRow => {
        deletedRow.__DELETED__ = true;
      });
    },
    onRestoreSubformClick(subform) {
      subform.restored = true;
      subform.oldValue = deepClone(subform._oldValue);
      subform.oldRecords = [];
      subform.merged = false;
      this.$nextTick(() => {
        subform.restored = false;
      });
    },
    onMergeOk: debounce(
      function () {
        this.doMerge();
      },
      1000,
      { leading: true, trailing: false }
    ),
    doMerge() {
      const _this = this;
      let $dyform = _this.workView.getDyformWidget();
      _this.mainFormFields.forEach(field => {
        if (field.endCode) {
          $dyform.dyform.formData[field.endCode] = field.dyform.formData[field.endCode];
        }
        $dyform.dyform.setFieldValue(field.code, field.dyform.formData[field.code]);
      });
      _this.mergeSubform2WorkData($dyform);

      let workData = _this.workView.getWorkData();
      workData.tempTaskInstRecVer = _this.expectTaskInstRecVer;
      let newMainFormData = (_this.latestFormDatas && _this.latestFormDatas[workData.dyFormData.formUuid][0]) || {};
      if (newMainFormData['modify_time']) {
        $dyform.dyform.formData.modify_time = newMainFormData['modify_time'];
      }
      for (let formUuid in _this.latestFormDatas) {
        let rows = _this.latestFormDatas[formUuid] || [];
        let subformRecords = $dyform.dyform.subform[formUuid] || [];
        subformRecords.forEach(subformRecord => {
          let rowData = rows.find(rowData => rowData.uuid == subformRecord.formData.uuid);
          if (rowData && rowData['modify_time']) {
            subformRecord.formData.modify_time = rowData['modify_time'];
          }
        });
        // 更新表单原始数据
        rows.forEach(row => {
          if (row.__ADDED__) {
            $dyform.originFormData[row.uuid] = row;
          }
        });
      }

      // 更新原始数据
      for (let formUuid in _this.latestFormDatas) {
        let datas = deepClone(_this.latestFormDatas[formUuid] || []);
        if ($dyform.originFormData[formUuid]) {
          $dyform.originFormData[formUuid] = datas;
        }
        datas.forEach(data => {
          if ($dyform.originFormData[data.uuid]) {
            $dyform.originFormData[data.uuid] = data;
          }
        });
      }

      this.visible = false;
    },
    mergeSubform2WorkData($dyform) {
      let _this = this;
      _this.subforms.forEach(subform => {
        let $currentSubform = $dyform.dyform.$subform[subform.formUuid];
        let currentSubforms = $dyform.dyform.subform[subform.formUuid];
        let newRecords = subform.newRecords;
        let oldRecords = subform.oldRecords;
        oldRecords.forEach(record => {
          let formData = record.formData || {};
          let currentSubform = currentSubforms.find(subform => subform.formData.uuid == record.formData.uuid);
          if (formData.__UPDATED__ && currentSubform) {
            for (let key in formData) {
              if (!key || key.startsWith('__')) {
                continue;
              }
              currentSubform.setFieldValue(key, formData[key]);
            }
          } else if (formData.__ADDED__ || formData.__DELETED_LOCAL__) {
            let addedData = deepClone(formData);
            delete addedData.__ADDED__;
            delete addedData.__DELETED__;
            delete addedData.__UPDATED__;
            delete addedData.__UPDATED_FIELDS__;
            delete addedData.__DELETED_LOCAL__;
            $currentSubform.addRow(addedData);
            // 本地删除的进行还原
            if (formData.__DELETED_LOCAL__) {
              let deletedSubformData = $dyform.dyform.deletedSubformData[subform.formUuid] || [];
              if (deletedSubformData.indexOf(formData.uuid) != -1) {
                deletedSubformData.splice(deletedSubformData.indexOf(formData.uuid), 1);
              }
            }
          } else if (formData.__DELETED__ && currentSubform) {
            $currentSubform.delRowById((currentSubform.row && currentSubform.row[$currentSubform.rowKey]) || formData.uuid);
          } else {
            // 最新值已删除，当前值没有更新合并删除，进行还原
            let oldNotDeleteIndex = subform.deletedNewUuids.findIndex(deleteNewUuid => deleteNewUuid == formData.uuid);
            if (oldNotDeleteIndex != -1) {
              $currentSubform.copyRow(currentSubform.row);
              $currentSubform.delRowById((currentSubform.row && currentSubform.row[$currentSubform.rowKey]) || formData.uuid);
            }
          }
        });

        // 新增的记录不更新合并，进行本地添加删除
        newRecords.forEach(newRecord => {
          let formData = newRecord.formData || {};
          if (!formData.__ADDED__) {
            // 本地删除的不更新不用处理
            return;
          }

          let oldIndex = oldRecords.findIndex(record => record.formData.uuid == formData.uuid);
          if (oldIndex == -1) {
            $currentSubform.addRow(formData);
            let currentSubform = currentSubforms.find(subform => subform.formData.uuid == newRecord.formData.uuid);
            if (currentSubform) {
              $currentSubform.delRowById((currentSubform.row && currentSubform.row[$currentSubform.rowKey]) || formData.uuid);
            }
          }
        });
      });
    },
    onMergeCancel() {
      this.visible = false;
    },
    onMergeCallbackClick: debounce(
      function (button) {
        this.doMergeAndCallback(button);
      },
      1000,
      { leading: true, trailing: false }
    ),
    doMergeAndCallback(button) {
      this.doMerge();

      setTimeout(() => {
        button.callback.call(button.callbackScope);
      }, 1);
    }
  }
};
</script>

<style lang="less" scoped>
::v-deep .ant-card .ant-card-body {
  padding: 0;
}
.ant-card-head-title {
  .title {
    line-height: 30px;
    font-size: var(--w-font-size-lg); // 22px

    .prefix {
      height: 14px; //var(--w-height-lg); //20px;
      display: inline-block;
      width: 4px;
      background: #488cee;
      border-radius: 1px;
    }
  }
}
.warning-text {
  ::v-deep .ant-alert-message {
    width: 90%;
    display: block;
    word-wrap: break-word;
  }
}
//   ::v-deep .ant-table-row-cell-ellipsis.changed {
//     .textonly {
//       color: red;
//     }
//     .widget-form-input-number {
//       .textonly {
//         color: red;
//       }
//     }
//   }
</style>
