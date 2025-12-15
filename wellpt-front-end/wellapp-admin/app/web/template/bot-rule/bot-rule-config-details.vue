<template>
  <Scroll :style="{ height: '500px' }">
    <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
      <a-form-model ref="form" :model="formData" :rules="rules" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <a-tabs default-active-key="1">
          <a-tab-pane key="1" tab="基本信息">
            <a-form-model-item label="名称" prop="ruleName">
              <a-input v-model="formData.ruleName" :maxLength="50" />
            </a-form-model-item>
            <a-form-model-item label="ID" prop="id">
              <a-input v-model="formData.id" :disabled="!!formData.uuid" :maxLength="50" />
            </a-form-model-item>
            <a-form-model-item label="转换类型" prop="transferType">
              <a-select v-model="formData.transferType" :options="transferTypeOptions"></a-select>
            </a-form-model-item>
            <a-form-model-item v-if="formData.transferType == 0" label="源单据" prop="sourceObjId">
              <a-select
                mode="multiple"
                allow-clear
                v-model="formData.sourceObjId"
                :options="formOptions"
                show-search
                :filter-option="filterOption"
              ></a-select>
            </a-form-model-item>
            <a-form-model-item label="目标单据" prop="targetObjId">
              <a-select
                v-model="formData.targetObjId"
                allow-clear
                :options="formOptions"
                show-search
                :filter-option="filterOption"
                @change="targetObjIdChange"
              ></a-select>
            </a-form-model-item>
            <a-form-model-item label="保存表单" prop="isPersist">
              <a-switch v-model="formData.isPersist"></a-switch>
            </a-form-model-item>
          </a-tab-pane>
          <a-tab-pane key="2" tab="字段映射" force-render>
            <div v-show="formData.transferType == 0" class="field-mapping">
              <a-space>
                <label class="title">
                  <i class="prefix"></i>
                  字段匹配映射
                </label>
                <a-switch v-model="formData.autoMapSameColumn"></a-switch>
                <span>字段编码相同的字段自动建立映射关系</span>
              </a-space>
              <p />
              <div v-show="formData.autoMapSameColumn">
                <a-space>
                  <a-input-search placeholder="请输入" enter-button="查询" allowClear @search="onSearchAutoMapping" />
                </a-space>
                <p />
                <a-table
                  rowKey="id"
                  size="small"
                  :pagination="false"
                  :bordered="true"
                  :columns="autoMappingTableColumns"
                  :locale="locale"
                  :data-source="autoMappings"
                  :class="['widget-table-auto-mapping-table no-border']"
                >
                  <template slot="isMappingSlot" slot-scope="text, record, index">
                    <a-switch v-model="record.isMapping" @change="autoMappingChange"></a-switch>
                  </template>
                </a-table>
              </div>
            </div>
            <a-divider v-show="formData.transferType == 0" />
            <div class="field-mapping">
              <a-space>
                <label class="title">
                  <i class="prefix"></i>
                  自定义字段映射
                </label>
              </a-space>
              <p />
              <div>
                <a-space>
                  <a-button type="primary" icon="plus" @click="addObjMapping">新增</a-button>
                  <a-button type="danger" icon="delete" @click="deleteObjMapping">删除</a-button>
                </a-space>
                <p />
                <a-table
                  rowKey="id"
                  size="small"
                  :pagination="false"
                  :bordered="true"
                  :columns="objMappingTableColumns"
                  :locale="locale"
                  :data-source="objMappingDtos"
                  :row-selection="objMappingSelection"
                  :class="['widget-table-obj-mapping-table no-border']"
                >
                  <template slot="sourceObjFieldSlot" slot-scope="text, record, index">
                    <a-row>
                      <a-col span="12">
                        <a-select
                          v-model="record.sourceObjId"
                          :options="sourceFormOptions"
                          show-search
                          :filter-option="filterOption"
                        ></a-select>
                      </a-col>
                      <a-col span="1"></a-col>
                      <a-col span="11">
                        <a-select
                          v-model="record.sourceObjField"
                          :options="getFormFieldOptions(record.sourceObjId)"
                          show-search
                          :filter-option="filterOption"
                          @change="(value, option) => sourceObjectFieldChange(value, option, record)"
                        ></a-select>
                      </a-col>
                    </a-row>
                  </template>
                  <template slot="targetObjFieldSlot" slot-scope="text, record, index">
                    <a-select
                      v-model="record.targetObjField"
                      :options="getFormFieldOptions(formData.targetObjId)"
                      show-search
                      :filter-option="filterOption"
                      @change="(value, option) => targetObjectFieldChange(value, option, record)"
                    ></a-select>
                  </template>
                  <template slot="renderValueTypeSlot" slot-scope="text, record, index">
                    <span @click="openRenderValueModal(record)">
                      <a v-if="record.renderValueType == 0">freemarker表达式</a>
                      <a v-else-if="record.renderValueType == 1">groovy脚本</a>
                      <a v-else>编辑规则</a>
                    </span>
                  </template>
                </a-table>
              </div>
            </div>
          </a-tab-pane>
          <a-tab-pane key="3" tab="关系维护">
            <a-form-model-item label="关系单据" prop="relaDto.relaObjId">
              <a-select
                v-model="formData.relaDto.relaObjId"
                allow-clear
                :options="formOptions"
                show-search
                :filter-option="filterOption"
              ></a-select>
            </a-form-model-item>
            <p />
            <div>
              <a-space>
                <a-button type="primary" icon="plus" @click="addRelaMapping">新增</a-button>
                <a-button type="danger" icon="delete" @click="deleteRelaMapping">删除</a-button>
              </a-space>
              <p />
              <a-table
                rowKey="id"
                size="small"
                :pagination="false"
                :bordered="true"
                :columns="relaMappingTableColumns"
                :locale="locale"
                :data-source="formData.relaDto.relaMappingDtos"
                :row-selection="relaMappingSelection"
                :class="['widget-table-rela-dto-table no-border']"
              >
                <template slot="sourceObjIdSlot" slot-scope="text, record, index">
                  <a-row>
                    <a-col span="12">
                      <a-select
                        v-model="record.sourceObjId"
                        :options="sourceAndTargetFormOptions"
                        show-search
                        :filter-option="filterOption"
                      ></a-select>
                    </a-col>
                    <a-col span="1"></a-col>
                    <a-col span="11">
                      <a-select
                        v-model="record.sourceObjField"
                        :options="getFormFieldOptions(record.sourceObjId)"
                        show-search
                        :filter-option="filterOption"
                        @change="(value, option) => sourceObjectFieldChange(value, option, record)"
                      ></a-select>
                    </a-col>
                  </a-row>
                </template>
                <template slot="relaObjFieldSlot" slot-scope="text, record, index">
                  <a-select
                    v-model="record.relaObjField"
                    :options="getFormFieldOptions(formData.relaDto.relaObjId)"
                    show-search
                    :filter-option="filterOption"
                    @change="(value, option) => relaObjectFieldChange(value, option, record)"
                  ></a-select>
                </template>
              </a-table>
            </div>
          </a-tab-pane>
          <a-tab-pane key="4" tab="字段反写规则">
            <p />
            <div>
              <a-space>
                <a-button type="primary" icon="plus" @click="addReverseObjMapping">新增</a-button>
                <a-button type="danger" icon="delete" @click="deleteReverseObjMapping">删除</a-button>
              </a-space>
              <p />
              <a-table
                rowKey="id"
                size="small"
                :pagination="false"
                :bordered="true"
                :columns="reverseObjMappingTableColumns"
                :locale="locale"
                :data-source="reverseObjMappingDtos"
                :row-selection="reverseObjMappingSelection"
                :class="['widget-table-reverse-mapping-table no-border']"
              >
                <template slot="sourceObjFieldSlot" slot-scope="text, record, index">
                  <a-row>
                    <a-col span="12">
                      <a-select
                        v-model="record.sourceObjId"
                        :options="sourceFormOptions"
                        show-search
                        :filter-option="filterOption"
                      ></a-select>
                    </a-col>
                    <a-col span="1"></a-col>
                    <a-col span="11">
                      <a-select
                        v-model="record.sourceObjField"
                        :options="getFormFieldOptions(record.sourceObjId)"
                        show-search
                        :filter-option="filterOption"
                        @change="(value, option) => sourceObjectFieldChange(value, option, record)"
                      ></a-select>
                    </a-col>
                  </a-row>
                </template>
                <template slot="targetObjFieldSlot" slot-scope="text, record, index">
                  <a-select
                    v-model="record.targetObjField"
                    :options="getFormFieldOptions(formData.targetObjId)"
                    show-search
                    :filter-option="filterOption"
                    @change="(value, option) => targetObjectFieldChange(value, option, record)"
                  ></a-select>
                </template>
                <template slot="renderValueTypeSlot" slot-scope="text, record, index">
                  <span @click="openRenderValueModal(record)">
                    <a v-if="record.renderValueType == 0">freemarker表达式</a>
                    <a v-else-if="record.renderValueType == 1">groovy脚本</a>
                    <a v-else>编辑规则</a>
                  </span>
                </template>
              </a-table>
            </div>
          </a-tab-pane>
          <a-tab-pane key="5" tab="转换侦听">
            <a-form-model-item label="单据转换前侦听代码" prop="scriptBeforeTrans">
              <WidgetCodeEditor v-model="formData.scriptBeforeTrans" width="auto" height="200px"></WidgetCodeEditor>
            </a-form-model-item>
            <a-form-model-item label="单据转换后侦听代码" prop="scriptAfterTrans">
              <WidgetCodeEditor v-model="formData.scriptAfterTrans" width="auto" height="200px"></WidgetCodeEditor>
              <span>说明：支持groovy脚本代码</span>
            </a-form-model-item>
          </a-tab-pane>
        </a-tabs>
      </a-form-model>
      <BotRenderValueModal
        :visible="renderValueModalVisible"
        :data="currentObjMapping"
        @ok="renderValueOk"
        @cancel="e => (renderValueModalVisible = false)"
      ></BotRenderValueModal>
    </a-skeleton>
  </Scroll>
</template>

<script>
import moment from 'moment';
import { generateId, deepClone } from '@framework/vue/utils/util';
import BotRenderValueModal from './component/bot-render-value-modal.vue';
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';

export default {
  components: { BotRenderValueModal, WidgetCodeEditor },
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    const _this = this;
    let $event = _this.$event || {};
    let formData = $event.meta || {};
    let uuid = formData.uuid;
    if (!uuid) {
      formData = Object.assign(formData, {
        id: 'BTO_' + moment().format('yyyyMMDDHHmmss'),
        transferType: 0,
        objMappingDtos: [],
        ignoreMappings: [],
        relaDto: { relaMappingDtos: [] },
        scriptBeforeTrans: '',
        scriptAfterTrans: ''
      });
    } else {
      formData.objMappingDtos = formData.objMappingDtos || [];
      formData.ignoreMappings = formData.ignoreMappings || [];
      formData.relaDto = formData.relaDto || { relaMappingDtos: [] };
    }
    return {
      loading: !!uuid,
      uuid,
      formData,
      rules: {
        ruleName: { required: true, message: '不能为空', trigger: ['blur'] },
        id: { required: true, message: '不能为空', trigger: ['blur'] }
      },
      transferTypeOptions: [
        {
          label: '单据转单据',
          value: 0
        },
        {
          label: '报文转单据',
          value: 1
        }
      ],
      formOptions: [],
      formFieldOptionsMap: {},
      locale: {
        emptyText: <span>暂无数据</span>
      },
      objMappingSelection: {
        selectedRowKeys: [],
        selectedRows: [],
        onChange(selectedRowKeys, selectedRows) {
          _this.objMappingSelection.selectedRowKeys = selectedRowKeys;
          _this.objMappingSelection.selectedRows = selectedRows;
        }
      },
      renderValueModalVisible: false,
      currentObjMapping: null,
      autoMappingTableColumns: [
        { title: '序号', dataIndex: 'index', width: '50px', align: 'center' },
        { title: '源单据', dataIndex: 'sourceObjName' },
        { title: '源字段', dataIndex: 'sourceObjFieldName' },
        { title: '目标字段', dataIndex: 'targetObjFieldName' },
        { title: '字段编码', dataIndex: 'name' },
        { title: '是否映射', dataIndex: 'isMapping', scopedSlots: { customRender: 'isMappingSlot' } }
      ],
      autoMappings: [],
      relaMappingTableColumns: [
        { title: '单据字段', dataIndex: 'sourceObjId', scopedSlots: { customRender: 'sourceObjIdSlot' } },
        { title: '关系维护字段', dataIndex: 'relaObjField', width: '40%', scopedSlots: { customRender: 'relaObjFieldSlot' } }
      ],
      relaMappingSelection: {
        selectedRowKeys: [],
        selectedRows: [],
        onChange(selectedRowKeys, selectedRows) {
          _this.relaMappingSelection.selectedRowKeys = selectedRowKeys;
          _this.relaMappingSelection.selectedRows = selectedRows;
        }
      },
      reverseObjMappingSelection: {
        selectedRowKeys: [],
        selectedRows: [],
        onChange(selectedRowKeys, selectedRows) {
          _this.reverseObjMappingSelection.selectedRowKeys = selectedRowKeys;
          _this.reverseObjMappingSelection.selectedRows = selectedRows;
        }
      }
    };
  },
  computed: {
    objMappingTableColumns() {
      let columns = [
        { title: '源字段', dataIndex: 'sourceObjField', scopedSlots: { customRender: 'sourceObjFieldSlot' } },
        { title: '目标字段', dataIndex: 'targetObjField', width: '300px', scopedSlots: { customRender: 'targetObjFieldSlot' } },
        { title: '值规则', dataIndex: 'renderValueType', width: '250px', scopedSlots: { customRender: 'renderValueTypeSlot' } }
      ];
      return this.formData.transferType == 0 ? columns : columns.filter(item => item.dataIndex != 'sourceObjField');
    },
    sourceFormOptions() {
      let sourceObjIds = this.formData.transferType == 0 ? this.formData.sourceObjId || [] : [];
      return sourceObjIds.length > 0 ? this.formOptions.filter(item => sourceObjIds.includes(item.value)) : [];
    },
    sourceAndTargetFormOptions() {
      let sourceObjIds = this.formData.transferType == 0 ? this.formData.sourceObjId || [] : [];
      let targetObjId = this.formData.targetObjId;
      let formUuids = [...sourceObjIds, targetObjId];
      return this.formOptions.filter(item => formUuids.includes(item.value));
    },
    objMappingDtos() {
      return this.formData.objMappingDtos.filter(item => item.isReverseWrite == false);
    },
    reverseObjMappingTableColumns() {
      let columns = [
        { title: '目标字段', dataIndex: 'targetObjField', width: '300px', scopedSlots: { customRender: 'targetObjFieldSlot' } },
        { title: '源字段', dataIndex: 'sourceObjField', scopedSlots: { customRender: 'sourceObjFieldSlot' } },
        { title: '值规则', dataIndex: 'renderValueType', width: '250px', scopedSlots: { customRender: 'renderValueTypeSlot' } }
      ];
      return this.formData.transferType == 0 ? columns : columns.filter(item => item.dataIndex != 'sourceObjField');
    },
    reverseObjMappingDtos() {
      return this.formData.objMappingDtos.filter(item => item.isReverseWrite == true);
    }
  },
  watch: {
    'formData.autoMapSameColumn': function (newVal, oldVal) {
      if (newVal) {
        this.syncAutoMappingColumns();
      }
    },
    'formData.sourceObjId': function (newVal, oldVal) {
      if (this.formData.autoMapSameColumn) {
        this.syncAutoMappingColumns();
      }
    }
  },
  created() {
    if (this.uuid) {
      this.loadBotRuleConf();
    }
    this.loadFormOptions();
  },
  methods: {
    loadBotRuleConf() {
      const _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'botRuleConfFacadeService',
          methodName: 'getBotRuleConfigDetail',
          args: JSON.stringify([_this.uuid])
        })
        .then(({ data: result }) => {
          _this.loading = false;
          if (result.data) {
            _this.formData = result.data;
            _this.formData.sourceObjId = _this.formData.sourceObjId ? _this.formData.sourceObjId.split(';') : [];
            _this.formData.objMappingDtos.forEach(item => (item.id = item.uuid));
            _this.formData.relaDto = _this.formData.relaDto || { relaMappingDtos: [] };
            _this.formData.relaDto.relaMappingDtos.forEach(item => (item.id = item.uuid));
            if (_this.formData.autoMapSameColumn) {
              _this.syncAutoMappingColumns();
            }
          } else {
            _this.$message.error(result.msg);
          }
        })
        .catch(({ response }) => {
          if (response.data && response.data.msg) {
            _this.$message.error(response.data.msg);
          } else {
            _this.$message.error('加载失败');
          }
        });
    },
    loadFormOptions(value = '') {
      const _this = this;
      _this.$axios
        .post('/json/data/services', {
          serviceName: 'dyFormFacade',
          methodName: 'listDyFormDefinitionBasicInfo'
        })
        .then(({ data: result }) => {
          if (result.data) {
            _this.formOptions = result.data.map(item => ({ label: item.name + '(' + item.version + ')', value: item.uuid }));
            if (_this.formData.autoMapSameColumn) {
              _this.syncAutoMappingColumns();
            }
          }
        });
    },
    filterOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        (option.componentOptions.children[0] &&
          option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0)
      );
    },
    targetObjIdChange() {
      const _this = this;
      let formOption = _this.formOptions.find(item => item.value == _this.formData.targetObjId);
      if (formOption) {
        _this.formData.targetObjName = formOption.label;
      }
    },
    addObjMapping() {
      this.formData.objMappingDtos.push({ id: generateId(), renderValueType: -1, renderValueExpression: '', isReverseWrite: false });
    },
    deleteObjMapping() {
      const _this = this;
      if (_this.objMappingSelection.selectedRowKeys.length === 0) {
        _this.$message.error('请选择记录！');
        return;
      }

      let dataList = _this.formData.objMappingDtos || [];
      for (let index = 0; index < dataList.length; index++) {
        if (_this.objMappingSelection.selectedRowKeys.includes(dataList[index].id)) {
          dataList.splice(index--, 1);
        }
      }
      _this.objMappingSelection.selectedRowKeys = [];
      _this.objMappingSelection.selectedRows = [];
    },
    getFormFieldOptions(formUuid, callback) {
      const _this = this;
      if (!formUuid) {
        if (callback) {
          callback([]);
        }
        return [];
      }

      if (_this.formFieldOptionsMap[formUuid]) {
        if (callback) {
          callback(_this.formFieldOptionsMap[formUuid]);
        }
        return _this.formFieldOptionsMap[formUuid];
      }

      // 缓存表单请求，减少重复请求
      if (!_this.formPromise) {
        _this.formPromise = {};
      }
      let promise = _this.formPromise[formUuid];
      if (!promise) {
        _this.formPromise[formUuid] = promise = $axios.post('/pt/dyform/definition/getFormDefinition', { formUuid, justDataAndDef: false });
      }

      promise.then(({ data: result }) => {
        if (result.fields) {
          // 字段
          let fieldOptions = [];
          for (let fieldName in result.fields) {
            let field = result.fields[fieldName];
            fieldOptions.push({ value: field.name, label: field.displayName });
          }
          _this.$set(_this.formFieldOptionsMap, result.uuid, fieldOptions);
          if (callback) {
            callback(fieldOptions);
          }
        } else {
          if (callback) {
            callback([]);
          }
        }
      });
      return [];
    },
    openRenderValueModal(record) {
      this.currentObjMapping = record;
      this.renderValueModalVisible = true;
    },
    renderValueOk(data) {
      this.currentObjMapping.renderValueType = data.renderValueType;
      this.currentObjMapping.renderValueExpression = data.renderValueExpression;
      this.renderValueModalVisible = false;
      this.currentObjMapping = null;
    },
    save(evt) {
      const _this = this;
      _this.$refs.form.validate().then(valid => {
        let formData = deepClone(_this.formData);
        formData.sourceObjId = Array.isArray(formData.sourceObjId) ? formData.sourceObjId.join(';') : formData.sourceObjId;
        $axios
          .post('/json/data/services', {
            serviceName: 'botRuleConfFacadeService',
            methodName: 'saveBotRuleConfig',
            args: JSON.stringify([formData])
          })
          .then(({ data: result }) => {
            if (result.success) {
              _this.$message.success('保存成功');
              _this.pageContext.emitEvent('BOJzmahterLEFXmzIsExBqACtIVfIkKi:closeModal');
              _this.pageContext.emitEvent('XnrPUJnKOBsvTsYgEEGwFmgfYQiriZAC:refetch');
            } else {
              _this.$message.error(result.msg || '保存失败');
            }
          });
      });
    },
    sourceObjectFieldChange(value, option, record) {
      let data = option.context.$options.propsData.options.find(item => item.value == value);
      if (data) {
        record.sourceObjFieldName = data.label;
      }
    },
    targetObjectFieldChange(value, option, record) {
      let data = option.context.$options.propsData.options.find(item => item.value == value);
      if (data) {
        record.targetObjFieldName = data.label;
      }
    },
    relaObjectFieldChange(value, option, record) {
      let data = option.context.$options.propsData.options.find(item => item.value == value);
      if (data) {
        record.relaObjFieldName = data.label;
      }
    },
    syncAutoMappingColumns() {
      const _this = this;
      let sourceObjId = _this.formData.sourceObjId || [];
      let targetObjId = _this.formData.targetObjId;
      if (sourceObjId.length == 0 || !targetObjId) {
        _this.autoMappings = [];
      } else {
        let sourceObjPromises = [];
        sourceObjId.forEach(formUuid => sourceObjPromises.push(_this.getFormFieldOptionsPromise(formUuid)));
        let targetObjPromise = _this.getFormFieldOptionsPromise(targetObjId);
        Promise.all([...sourceObjPromises, targetObjPromise]).then(results => {
          let target = results.pop();
          let sources = results;
          let sameFieldMap = {};
          let mappingFields = [];
          let index = 0;
          sources.forEach(source => {
            let sourceInfo = _this.formOptions.find(item => item.value == source.formUuid) || {};
            source.fields.forEach(field => {
              let sameField = target.fields.find(item => item.value == field.value);
              if (sameField && !sameFieldMap[sameField.value]) {
                sameFieldMap[sameField.value] = sameField.label;
                index++;
                mappingFields.push({
                  id: generateId(),
                  index,
                  sourceObjName: sourceInfo.label,
                  sourceObjId: source.formUuid,
                  sourceObjField: sameField.value,
                  sourceObjFieldName: sameField.label,
                  targetObjField: sameField.value,
                  targetObjFieldName: sameField.label,
                  targetObjId: target.formUuid,
                  name: sameField.value,
                  isMapping:
                    _this.formData.ignoreMappings.findIndex(
                      item =>
                        item.sourceObjId == source.formUuid && item.targetObjId == target.formUuid && item.sourceObjField == sameField.value
                    ) == -1
                });
              }
            });
          });
          _this.autoMappings = mappingFields;
          _this.allAutoMappings = deepClone(_this.autoMappings);
        });
      }
    },
    getFormFieldOptionsPromise(formUuid) {
      return new Promise((resolve, reject) => {
        this.getFormFieldOptions(formUuid, fields => {
          resolve({ formUuid, fields });
        });
      });
    },
    onSearchAutoMapping(value) {
      const _this = this;
      if (!_this.allAutoMappings) {
        return;
      }
      _this.autoMappings = _this.allAutoMappings.filter(
        item =>
          !value ||
          (item.sourceObjName && item.sourceObjName.indexOf(value) != -1) ||
          (item.sourceObjFieldName && item.sourceObjFieldName.indexOf(value) != -1) ||
          (item.targetObjFieldName && item.targetObjFieldName.indexOf(value) != -1) ||
          (item.name && item.name.indexOf(value) != -1)
      );
    },
    autoMappingChange() {
      this.formData.ignoreMappings = this.autoMappings.filter(item => !item.isMapping);
    },
    addRelaMapping() {
      this.formData.relaDto.relaMappingDtos.push({ id: generateId() });
    },
    deleteRelaMapping() {
      const _this = this;
      if (_this.relaMappingSelection.selectedRowKeys.length === 0) {
        _this.$message.error('请选择记录！');
        return;
      }

      let dataList = _this.formData.relaDto.relaMappingDtos || [];
      for (let index = 0; index < dataList.length; index++) {
        if (_this.relaMappingSelection.selectedRowKeys.includes(dataList[index].id)) {
          dataList.splice(index--, 1);
        }
      }
      _this.relaMappingSelection.selectedRowKeys = [];
      _this.relaMappingSelection.selectedRows = [];
    },
    addReverseObjMapping() {
      this.formData.objMappingDtos.push({ id: generateId(), renderValueType: -1, renderValueExpression: '', isReverseWrite: true });
    },
    deleteReverseObjMapping() {
      const _this = this;
      if (_this.reverseObjMappingSelection.selectedRowKeys.length === 0) {
        _this.$message.error('请选择记录！');
        return;
      }

      let dataList = _this.formData.objMappingDtos || [];
      for (let index = 0; index < dataList.length; index++) {
        if (_this.reverseObjMappingSelection.selectedRowKeys.includes(dataList[index].id)) {
          dataList.splice(index--, 1);
        }
      }
      _this.reverseObjMappingSelection.selectedRowKeys = [];
      _this.reverseObjMappingSelection.selectedRows = [];
    }
  },
  META: {
    method: {
      save: '保存单据转换规则'
    }
  }
};
</script>

<style lang="less" scoped>
.field-mapping {
  .title {
    line-height: 30px;
    font-size: var(--w-font-size-3xl); // 22px

    .prefix {
      height: var(--w-height-3xs); //20px;
      display: inline-block;
      width: 4px;
      background: #488cee;
      border-radius: 1px;
    }
  }
}
</style>
