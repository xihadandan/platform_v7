<template>
  <div>
    <a-select
      size="small"
      v-model="currentKey"
      @change="onCurrentKeyChange"
      :options="[
        { label: '新建时', value: 'editFormElementRules' },
        { label: '编辑时', value: 'editStateFormElementRules' },
        { label: '查阅时', value: 'labelStateFormElementRules' }
      ]"
    ></a-select>
    <a-form-model-item label="弹框标题" v-if="currentKey == 'editFormElementRules'" :label-col="{ span: 4 }" :wrapper-col="{ span: 14 }">
      <a-input v-model="widget.configuration.newFormTitle">
        <template slot="addonAfter">
          <WI18nInput :widget="widget" :designer="designer" code="newFormTitle" v-model="widget.configuration.newFormTitle" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item
      label="弹框标题"
      v-if="currentKey == 'editStateFormElementRules'"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 14 }"
    >
      <a-input v-model="widget.configuration.editStateTitle">
        <template slot="addonAfter">
          <WI18nInput :widget="widget" :designer="designer" code="editStateTitle" v-model="widget.configuration.editStateTitle" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item
      label="弹框标题"
      v-if="currentKey == 'labelStateFormElementRules'"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 14 }"
    >
      <a-input v-model="widget.configuration.labelStateTitle">
        <template slot="addonAfter">
          <WI18nInput :widget="widget" :designer="designer" code="labelStateTitle" v-model="widget.configuration.labelStateTitle" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-table
      size="small"
      :rowKey="rowKey"
      :pagination="false"
      :columns="ruleColumns"
      :data-source="configuration[currentKey]"
      :expandedRowKeys.sync="expandedRowKeys"
      :loading="loading"
    >
      <template slot="titleSlot" slot-scope="text, record, index">
        <a-icon type="table" v-if="record.children && record.children.length" />
        {{ text }}
        <a-badge status="processing" v-if="record.children && record.children.length && newSubformWgtIds.includes(record.id)" />
      </template>
      <span slot="customTitle">
        批量设置
        <a-radio-group v-model="batchState" @change="onBachSetChange" size="small">
          <a-radio-button value="hidden">隐藏</a-radio-button>
          <a-radio-button value="displayAsLabel">仅可见</a-radio-button>
          <a-radio-button value="editable" v-if="currentKey != 'labelStateFormElementRules'">可编辑</a-radio-button>
        </a-radio-group>
        <a-switch
          v-if="currentKey != 'labelStateFormElementRules'"
          v-model="batchRequired"
          size="small"
          checked-children="必填"
          un-checked-children="必填"
          @change="onBatchRequiredChange"
        />
        <a-tooltip placement="topRight" :arrowPointAtCenter="true" v-if="currentKey !== 'labelStateFormElementRules'">
          <template slot="title">
            <ul style="padding-inline-start: 20px; margin-block-end: 0px">
              <li>可编辑情况下，从表列为非隐藏且必填时,字段配置与从表列配置一致，且不可修改</li>
              <li>从表列有必填条件，弹框内表单无必填设置时，弹框内表单为非必填</li>
              <li>弹框内表单必填且有必填条件时，弹框内表单会根据表单必填条件判断字段是否必填</li>
            </ul>
          </template>
          <a-icon type="info-circle" style="padding-top: 0; vertical-align: middle; line-height: 16px" />
        </a-tooltip>
      </span>
      <template slot="operationSlot" slot-scope="text, record">
        <template
          v-if="
            columnsIdMap[record.code] &&
            currentKey !== 'labelStateFormElementRules' &&
            columnsIdMap[record.code].required &&
            columnsIdMap[record.code].defaultDisplayState !== 'hidden'
          "
        >
          <!-- 可编辑情况下，非隐藏且必填时必须跟列配置一致，且不可修改 -->
          <a-radio-group v-model="columnsIdMap[record.code].defaultDisplayState" size="small" disabled :style="{ 'padding-left': '60px' }">
            <a-radio-button value="hidden">隐藏</a-radio-button>
            <a-radio-button value="unedit">仅可见</a-radio-button>
            <a-radio-button value="edit">可编辑</a-radio-button>
          </a-radio-group>
          <a-switch
            disabled
            v-if="currentKey != 'labelStateFormElementRules'"
            :checked="columnRequierd(columnsIdMap[record.code])"
            size="small"
            checked-children="必填"
            un-checked-children="必填"
          />
        </template>
        <template v-else>
          <a-radio-group
            v-model="record.displayState"
            @change="e => onRuleChange(e, record)"
            size="small"
            :style="{ 'padding-left': '60px' }"
          >
            <a-radio-button value="hidden">隐藏</a-radio-button>
            <a-radio-button value="displayAsLabel">仅可见</a-radio-button>
            <a-radio-button value="editable" v-if="currentKey != 'labelStateFormElementRules'">可编辑</a-radio-button>
          </a-radio-group>
          <a-switch
            :disabled="record.columnRequired"
            v-if="currentKey != 'labelStateFormElementRules'"
            v-model="record.required"
            size="small"
            checked-children="必填"
            un-checked-children="必填"
            @change="checked => onRequiredChange(checked, record)"
          />
        </template>
      </template>
    </a-table>
  </div>
</template>

<script type="text/babel">
import { findIndex } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';
export default {
  name: 'FormFieldConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    columnsIdMap: Object
  },
  data() {
    return {
      loading: false,
      batchState: 'editable',
      currentKey: 'editFormElementRules',
      requireBatchSetVisible: false,
      batchRequired: false,
      expandedRowKeys: [],
      newSubformWgtIds: [],
      ruleColumns: [
        { title: '字段', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { width: 300, dataIndex: 'operation', slots: { title: 'customTitle' }, scopedSlots: { customRender: 'operationSlot' } }
      ]
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    configuration() {
      return this.widget.configuration;
    }
  },
  created() {},
  methods: {
    rowKey(record) {
      return (record.parentIndex || 0) + '_' + record.id;
    },
    onBatchSetClick(type) {
      this.requireBatchSetVisible = type == 'editable' || type == 'required';
    },
    onSearch() {},
    onRuleChange(e, record) {
      record.hidden = false;
      record.displayAsLabel = false;
      record.editable = false;
      record[record.displayState] = true;
      // 如果是有下级的行数据，则批量修改下级数据
      if (record.children && record.children.length) {
        for (let i = 0, len = record.children.length; i < len; i++) {
          record.children[i].hidden = false;
          record.children[i].displayAsLabel = false;
          record.children[i].editable = false;
          record.children[i][record.displayState] = true;
          record.children[i].displayState = record.displayState;
        }
      } else if (record.parentIndex != undefined) {
        // 如果是在子级里，要判断同级
        let len = this.configuration[this.currentKey][record.parentIndex].children.length;
        let state = record.displayState,
          hiddenLength = 0,
          displayAsLabelLength = 0,
          editableLength = 0;
        for (let i = 0; i < len; i++) {
          if (this.configuration[this.currentKey][record.parentIndex].children[i].displayState === 'displayAsLabel') {
            displayAsLabelLength++;
          } else if (this.configuration[this.currentKey][record.parentIndex].children[i].displayState == 'hidden') {
            hiddenLength++;
          } else if (this.configuration[this.currentKey][record.parentIndex].children[i].displayState == 'editable') {
            editableLength++;
          }
        }
        this.configuration[this.currentKey][record.parentIndex].hidden = false;
        this.configuration[this.currentKey][record.parentIndex].displayAsLabel = false;
        this.configuration[this.currentKey][record.parentIndex].editable = false;
        this.configuration[this.currentKey][record.parentIndex].displayState = null;
        if (editableLength > 0) {
          //有一个列是可编辑，从表可编辑
          this.configuration[this.currentKey][record.parentIndex].editable = true;
          this.configuration[this.currentKey][record.parentIndex].displayState = 'editable';
        } else if (displayAsLabelLength > 0) {
          this.configuration[this.currentKey][record.parentIndex].displayAsLabel = true;
          this.configuration[this.currentKey][record.parentIndex].displayState = 'displayAsLabel';
        } else if (len == hiddenLength) {
          //全部隐藏，从表隐藏
          this.configuration[this.currentKey][record.parentIndex].hidden = true;
          this.configuration[this.currentKey][record.parentIndex].displayState = 'hidden';
        }
      }
      this.setBatchState();
    },
    batchSetRowDataState(paramKey, checked, resettDisplayState = true) {
      for (let i = 0, len = this.configuration[this.currentKey].length; i < len; i++) {
        let r = this.configuration[this.currentKey][i];
        if (resettDisplayState) {
          r.displayState = paramKey;
          r.hidden = false;
          r.displayAsLabel = false;
          r.editable = false;
        }
        r[paramKey] = checked;
        if (paramKey == 'required' && r.columnRequired && !checked) {
          r.required = true;
        }
        if (r.children) {
          for (let j = 0, jlen = r.children.length; j < jlen; j++) {
            let jr = r.children[j];
            if (resettDisplayState) {
              jr.displayState = paramKey;
              jr.hidden = false;
              jr.displayAsLabel = false;
              jr.editable = false;
            }
            jr[paramKey] = checked;
          }
        }
      }
    },

    fetchFormDefinitionVjson(formUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'formDefinitionService',
            methodName: 'getOne',
            args: JSON.stringify([formUuid])
          })
          .then(({ data }) => {
            this.loading = false;
            if (data.code == 0 && data.data && data.data.definitionVjson) {
              let vjson = JSON.parse(data.data.definitionVjson);
              resolve(vjson);
            }
          });
      });
    },
    setFormElementFieldRules(vjson, currentKey) {
      let _this = this;
      let exits = {};
      if (currentKey == undefined) {
        currentKey = this.currentKey;
      }
      let existCodeIndexes = [];
      let delFileds = [];
      for (let c = 0; c < _this.configuration[currentKey].length; c++) {
        let current = _this.configuration[currentKey][c];
        if (!current.children) {
          let codeOrIdInField = findIndex(vjson.fields, item => {
            return item.id == current.id || item.configuration.code == current.code;
          });
          if (codeOrIdInField == -1) {
            // 已删除的字段
            delFileds.push(c);
          } else {
            existCodeIndexes.push(current.id);
            exits[current.id] = current;
            if (exits[current.code]) {
              // 修复重复数据
              let idx = existCodeIndexes.indexOf(exits[current.code].id);
              existCodeIndexes.splice(idx, 1);
              delFileds.push(c);
            }
            exits[current.code] = current;
          }
        }
        if (current.children) {
          let codeOrIdInSub = findIndex(vjson.subform, item => {
            item.id == current.id;
          });
          if (codeOrIdInSub == -1) {
            // 已删除的从表
            delFileds.push(c);
          } else {
            exits[current.id] = current;
            for (let cc = 0, cclen = current.children.length; cc < cclen; cc++) {
              let sub_current = current.children[cc];
              let codeOrIdInSubField = findIndex(vjson.subform[codeOrIdInSub].columns, item => {
                return item.dataIndex == sub_current.code;
              });
              // 已删除的字段
              if (codeOrIdInSubField == -1) {
                delFileds.push(c + '_' + cc);
              }
              exits[sub_current.id] = sub_current;
            }
          }
        }
      }
      //移除已删除内容
      _this.configuration[currentKey] = _this.configuration[currentKey].filter((item, index) => {
        let isDel = delFileds.includes(index);
        if (isDel) {
          return false;
        } else if (item.children) {
          item.children = item.children.filter((sub_item, sub_index) => {
            let isDel = delFileds.includes(index + '_' + sub_index);
            if (isDel) {
              return false;
            }
            return true;
          });
        }
        return true;
      });

      if (vjson.fields && vjson.fields.length) {
        for (let i = 0, len = vjson.fields.length; i < len; i++) {
          if (exits[vjson.fields[i].id]) {
            exits[vjson.fields[i].id].title = vjson.fields[i].configuration.name;
            continue;
          }
          let defaultDisplayState = vjson.fields[i].configuration.defaultDisplayState;
          let _r = {
            title: vjson.fields[i].configuration.name,
            id: vjson.fields[i].id,
            code: vjson.fields[i].configuration.code,
            required: vjson.fields[i].configuration.required,
            hidden: defaultDisplayState == 'hidden',
            editable: defaultDisplayState == 'edit',
            displayAsLabel: defaultDisplayState == 'unedit',
            displayState: defaultDisplayState == 'edit' ? 'editable' : defaultDisplayState == 'hidden' ? 'hidden' : 'displayAsLabel',
            columnRequired: vjson.fields[i].column && vjson.fields[i].column.notNull
          };
          if (currentKey == 'labelStateFormElementRules') {
            _r.editable = false;
            if (defaultDisplayState == 'hidden') {
              _r.hidden = true;
              _r.displayAsLabel = false;
              _r.displayState = 'hidden';
            } else {
              _r.hidden = false;
              _r.displayAsLabel = true;
              _r.displayState = 'displayAsLabel';
            }
          }
          _this.configuration[currentKey].push(_r);
        }
      }
      if (vjson.subforms && vjson.subforms.length) {
        // 从表
        for (let i = 0, len = vjson.subforms.length; i < len; i++) {
          let children = [],
            _parentRow = {};
          _this.expandedRowKeys.push(this.rowKey(vjson.subforms[i]));
          if (exits[vjson.subforms[i].id]) {
            exits[vjson.subforms[i].id].title = vjson.subforms[i].configuration.title || vjson.subforms[i].title;
            _parentRow = exits[vjson.subforms[i].id];
            children = _parentRow ? _parentRow.children : [];
          } else {
            _this.newSubformWgtIds.push(vjson.subforms[i].id);
            _parentRow = {
              title: vjson.subforms[i].configuration.title || vjson.subforms[i].title,
              id: vjson.subforms[i].id,
              code: vjson.subforms[i].formUuid,
              required: false,
              hidden: false,
              editable: true,
              displayAsLabel: false,
              displayState: 'editable',
              children
            };
            let defaultDisplayState_sub = vjson.subforms[i].configuration.defaultDisplayState;
            if (currentKey == 'labelStateFormElementRules') {
              _parentRow.editable = false;
              if (defaultDisplayState_sub == 'hidden') {
                _parentRow.hidden = true;
                _parentRow.displayAsLabel = false;
                _parentRow.displayState = 'hidden';
              } else {
                _parentRow.hidden = false;
                _parentRow.displayAsLabel = true;
                _parentRow.displayState = 'displayAsLabel';
              }
            }
            _this.configuration[currentKey].push(_parentRow);
          }
          let _requireCount = 0,
            parentIndex = findIndex(_this.configuration[currentKey], { id: vjson.subforms[i].id }), //当前从表的索引值   _this.configuration[currentKey].length - 1,
            jlen = vjson.subforms[i].configuration.columns.length;
          for (let j = 0; j < jlen; j++) {
            if (exits[vjson.subforms[i].configuration.columns[j].widget.id]) {
              exits[vjson.subforms[i].configuration.columns[j].widget.id].title = vjson.subforms[i].configuration.columns[j].title;
              exits[vjson.subforms[i].configuration.columns[j].widget.id].parentIndex = parentIndex;
              if (exits[vjson.subforms[i].configuration.columns[j].widget.id].required) {
                _requireCount++;
              }
              continue;
            }
            let defaultDisplayState_col = vjson.subforms[i].configuration.columns[j].defaultDisplayState;
            let _c = {
              title: vjson.subforms[i].configuration.columns[j].title,
              id: vjson.subforms[i].configuration.columns[j].widget.id,
              required: vjson.subforms[i].configuration.columns[j].widget.configuration.required,
              code: vjson.subforms[i].configuration.columns[j].widget.configuration.code,
              hidden: defaultDisplayState_col == 'hidden',
              editable: defaultDisplayState_col == 'edit',
              displayAsLabel: defaultDisplayState_col == 'unedit',
              displayState:
                defaultDisplayState_col == 'edit' ? 'editable' : defaultDisplayState_col == 'hidden' ? 'hidden' : 'displayAsLabel',
              parentIndex
            };
            if (currentKey == 'labelStateFormElementRules') {
              _c.editable = false;
              if (defaultDisplayState_col == 'hidden') {
                _c.hidden = true;
                _c.displayAsLabel = false;
                _c.displayState = 'hidden';
              } else {
                _c.hidden = false;
                _c.displayAsLabel = true;
                _c.displayState = 'displayAsLabel';
              }
            }
            children.push(_c);
            if (vjson.subforms[i].configuration.columns[j].widget.configuration.required) {
              _requireCount++;
            }
          }
          if (_requireCount === jlen) {
            _parentRow.required = true;
          }
        }
      }

      _this.setBatchState(currentKey);
    },

    setBatchState(currentKey) {
      if (currentKey !== undefined) {
        this.currentKey = currentKey;
      }
      if (this.configuration[this.currentKey]) {
        let displayState = this.configuration[this.currentKey][0].displayState;
        for (let i = 1, len = this.configuration[this.currentKey].length; i < len; i++) {
          if (displayState !== this.configuration[this.currentKey][i].displayState) {
            this.batchState = null;
            return;
          }

          if (this.configuration[this.currentKey][i].children) {
            for (let cc = 0, cclen = this.configuration[this.currentKey][i].children.length; cc < cclen; cc++) {
              if (this.configuration[this.currentKey][i].children[cc].displayState !== displayState) {
                this.batchState = null;
                return;
              }
            }
          }
        }
        this.batchState = displayState;
      }
    },
    onRequiredChange(checked, record) {
      // 如果是有下级的行数据，则批量修改下级数据
      if (record.children && record.children.length) {
        for (let i = 0, len = record.children.length; i < len; i++) {
          record.children[i].required = checked;
        }
      } else if (record.parentIndex != undefined) {
        // 如果是在子级里，要判断同级
        let len = this.configuration[this.currentKey][record.parentIndex].children.length;
        let requiredCnt = 0;
        for (let i = 0; i < len; i++) {
          if (this.configuration[this.currentKey][record.parentIndex].children[i].required) {
            requiredCnt++;
          }
        }
        this.configuration[this.currentKey][record.parentIndex].required = requiredCnt == len;
      }
      if (this.configuration[this.currentKey].length) {
        let required = this.configuration[this.currentKey][0].required;
        for (let i = 1, len = this.configuration[this.currentKey].length; i < len; i++) {
          if (required !== this.configuration[this.currentKey][i].required) {
            this.batchRequired = false;
            return;
          }

          if (this.configuration[this.currentKey][i].children) {
            for (let cc = 0, cclen = this.configuration[this.currentKey][i].children.length; cc < cclen; cc++) {
              if (this.configuration[this.currentKey][i].children[cc].required !== required) {
                this.batchRequired = false;
                return;
              }
            }
          }
        }
        this.batchRequired = required;
      }
    },
    onBatchRequiredChange(checked) {
      this.batchSetRowDataState('required', checked, false);
    },
    onBachSetChange(e) {
      this.batchSetRowDataState(e.target.value, true);
    },
    onCurrentKeyChange(e) {
      this.expandedRowKeys = [];
      let formUuid = this.configuration.editFormUuid;
      if (this.configuration.enableStateForm) {
        if (this.currentKey === 'labelStateFormElementRules') {
          formUuid = this.configuration.labelStateFormUuid;
        } else if (this.currentKey === 'editStateFormElementRules') {
          formUuid = this.configuration.editStateFormUuid;
        }
      }
      // this.designer.emitEvent(`${this.widget.id}:Dyform:FieldShowSetChange`, this.currentKey);
      if (formUuid) {
        this.loading = true;
        this.fetchFormDefinitionVjson(formUuid).then(vjson => {
          this.setFormElementFieldRules(vjson);
        });
      }
    },
    // 从表列的字段是否必填
    columnRequierd(column) {
      // 必填条件规则优先
      return column.requiredCondition && column.requiredCondition.enable && column.requiredCondition.conditions.length > 0
        ? false
        : column.required;
    },
    editFormUuidChanged(v) {
      this.fetchFormDefinitionVjson(v).then(vjson => {
        this.setFormElementFieldRules(vjson);
        if (!this.widget.configuration.enableStateForm) {
          this.widget.configuration.editStateFormElementRules.splice(0, this.widget.configuration.editStateFormElementRules.length);
          this.widget.configuration.labelStateFormElementRules.splice(0, this.widget.configuration.labelStateFormElementRules.length);
          this.setFormElementFieldRules(vjson, 'editStateFormElementRules');
          this.setFormElementFieldRules(vjson, 'labelStateFormElementRules');
        } else {
          let states = [
            {
              key: 'editStateFormUuid',
              formUuid: this.widget.configuration.editStateFormUuid,
              rules: 'editStateFormElementRules',
              name: this.widget.configuration.editStateFormName
            },
            {
              key: 'labelStateFormUuid',
              formUuid: this.widget.configuration.labelStateFormUuid,
              rules: 'labelStateFormElementRules',
              name: this.widget.configuration.labelStateFormName
            }
          ];
          for (let i = 0; i < states.length; i++) {
            let state = states[i];
            if (this.currentKey == state.key) {
              this.loading = true;
            }
            if (!state.formUuid) {
              if (!this.widget.configuration[state.rules]) {
                this.widget.configuration[state.rules] = [];
              }
              this.widget.configuration[state.rules].splice(0, this.widget.configuration[state.rules].length);
              this.setFormElementFieldRules(vjson, state.rules);
            }
          }
        }
      });
    }
  },
  mounted() {
    if (this.configuration.editFormUuid) {
      this.loading = true;
      this.editFormUuidChanged(this.configuration.editFormUuid);
    }
  },
  watch: {
    'widget.configuration.editFormUuid': {
      handler(v) {
        this.widget.configuration.editFormElementRules.splice(0, this.widget.configuration.editFormElementRules.length);
        if (v) {
          this.currentKey = 'editFormElementRules';
          this.loading = true;
          this.editFormUuidChanged(v);
        }
      }
    },
    'widget.configuration.editStateFormUuid': {
      handler(v) {
        this.widget.configuration.editStateFormElementRules.splice(0, this.widget.configuration.editStateFormElementRules.length);
        if (v) {
          this.currentKey = 'editStateFormElementRules';
          this.loading = true;
          this.fetchFormDefinitionVjson(v).then(vjson => {
            this.setFormElementFieldRules(vjson);
          });
        } else if (this.widget.configuration.editFormUuid) {
          this.fetchFormDefinitionVjson(this.widget.configuration.editFormUuid).then(vjson => {
            this.setFormElementFieldRules(vjson, 'editStateFormElementRules');
          });
        }
      }
    },
    'widget.configuration.labelStateFormUuid': {
      handler(v) {
        this.widget.configuration.labelStateFormElementRules.splice(0, this.widget.configuration.labelStateFormElementRules.length);
        if (v) {
          this.currentKey = 'labelStateFormElementRules';
          this.loading = true;
          this.fetchFormDefinitionVjson(v).then(vjson => {
            this.setFormElementFieldRules(vjson);
          });
        } else if (this.widget.configuration.editFormUuid) {
          this.fetchFormDefinitionVjson(this.widget.configuration.editFormUuid).then(vjson => {
            this.setFormElementFieldRules(vjson, 'labelStateFormElementRules');
          });
        }
      }
    },
    'widget.configuration.enableStateForm': {
      handler(checked) {
        if (checked) {
          let states = [
            {
              key: 'editStateFormUuid',
              formUuid: this.widget.configuration.editStateFormUuid,
              rules: 'editStateFormElementRules',
              name: this.widget.configuration.editStateFormName
            },
            {
              key: 'labelStateFormUuid',
              formUuid: this.widget.configuration.labelStateFormUuid,
              rules: 'labelStateFormElementRules',
              name: this.widget.configuration.labelStateFormName
            }
          ];
          for (let i = 0; i < states.length; i++) {
            let state = states[i];
            if (this.currentKey == state.key) {
              this.loading = true;
            }
            if (state.formUuid) {
              this.widget.configuration[state.rules].splice(0, this.widget.configuration[state.rules].length);
              this.fetchFormDefinitionVjson(state.formUuid).then(vjson => {
                this.setFormElementFieldRules(vjson, state.rules);
              });
            } else {
              if (!this.widget.configuration[state.rules]) {
                this.widget.configuration[state.rules] = [];
              }
              this.widget.configuration[state.rules].splice(0, this.widget.configuration[state.rules].length);
              if (this.widget.configuration.editFormUuid) {
                this.fetchFormDefinitionVjson(this.widget.configuration.editFormUuid).then(vjson => {
                  this.setFormElementFieldRules(vjson, state.rules);
                });
              }
            }
          }
        } else if (this.widget.configuration.editFormUuid) {
          let states = [
            {
              key: 'editStateFormUuid',
              rules: 'editStateFormElementRules'
            },
            {
              key: 'labelStateFormUuid',
              rules: 'labelStateFormElementRules'
            }
          ];
          for (let i = 0; i < states.length; i++) {
            let state = states[i];
            if (this.currentKey == state.key) {
              this.loading = true;
            }
            this.widget.configuration[state.rules] = deepClone(this.widget.configuration.editFormElementRules);
            if (state.key === 'labelStateFormUuid') {
              this.widget.configuration[state.rules].forEach(item => {
                if (item.displayState == 'editable') {
                  item.displayState = 'displayAsLabel';
                }
              });
            }
            this.loading = false;
          }
        }
      }
    }
  }
};
</script>
