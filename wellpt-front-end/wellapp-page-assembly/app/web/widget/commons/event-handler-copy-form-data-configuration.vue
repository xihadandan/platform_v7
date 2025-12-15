<template>
  <div
    v-if="
      (eventConfig.actionType == 'workflow' && eventConfig.sendToApprove !== true) ||
      (eventConfig.actionType == 'dataManager' && eventConfig.action == 'BasicWidgetDyformDevelopment.create')
    "
  >
    <template v-if="initTableButtonWidgetEventHandlerExtraOptions(eventConfig)">
      <a-form-model-item>
        <template slot="label">
          <a-space>
            表单数据填充
            <a-popover>
              <template slot="content">允许{{ widget.wtype == 'WidgetTable' ? '将选中的行' : '' }}数据填充到新开页面的表单内</template>
              <a-checkbox v-model="eventConfig.extraOptions.enableFillFormDataBySelectedRow" />
            </a-popover>
          </a-space>
        </template>
        <WidgetDesignModal
          title="配置表单数据填充规则"
          :width="800"
          dialogClass="pt-modal"
          :bodyStyle="{ height: '560px' }"
          :maxHeight="560"
          :z-index="100000000"
          v-if="eventConfig.extraOptions.enableFillFormDataBySelectedRow"
        >
          <template slot="content">
            <template v-if="eventConfig.extraOptions.enableFillFormDataBySelectedRow">
              <a-form-model class="design-config-form-model labe-width-150" :colon="false">
                <a-form-model-item v-if="sourceFormUuid == undefined">
                  <template slot="label">
                    <a-space>
                      数据源表单
                      <a-popover>
                        <template slot="content">通过指定方式来获取表单数据来源</template>
                        <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                      </a-popover>
                    </a-space>
                  </template>
                  <a-radio-group
                    v-model="eventConfig.extraOptions.dyformSourceType"
                    button-style="solid"
                    size="small"
                    @change="onChangeDyformSourceType(eventConfig.extraOptions)"
                  >
                    <a-radio-button value="sourceDyformConstant">指定源表单定义</a-radio-button>
                    <a-radio-button value="sourceDyformFromColumn">源表单定义列</a-radio-button>
                    <!-- <a-radio-button value="sourceDyformFromTaskInst">流程任务实例对应列</a-radio-button> -->
                  </a-radio-group>
                  <DyformDefinitionSelect
                    v-if="eventConfig.extraOptions.dyformSourceType == 'sourceDyformConstant'"
                    v-model="eventConfig.extraOptions.sourceFormUuid"
                    :displayModal="true"
                    ref="dyformDefinitionSelect"
                    @change="(e, opt) => onSelectChangeDyform(e, opt, eventConfig.extraOptions.sourceFormUuid, 'source')"
                  />
                  <template v-else-if="eventConfig.extraOptions.dyformSourceType == 'sourceDyformFromColumn'">
                    <a-input-group compact style="100%;">
                      <a-button>表单UUID</a-button>
                      <a-button>=</a-button>
                      <a-select v-model="eventConfig.extraOptions.sourceDyformUuidColumn" allow-clear style="width: calc(100% - 137px)">
                        <template v-for="(col, c) in sourceColumns">
                          <a-select-option :key="'df' + c" :value="col.dataIndex">{{ col.title }}</a-select-option>
                        </template>
                      </a-select>
                    </a-input-group>
                  </template>

                  <a-input-group
                    compact
                    style="100%;"
                    v-if="
                      eventConfig.extraOptions.dyformSourceType == 'sourceDyformConstant' ||
                      eventConfig.extraOptions.dyformSourceType == 'sourceDyformFromColumn'
                    "
                  >
                    <a-button>数据UUID</a-button>
                    <a-button>=</a-button>
                    <a-select v-model="eventConfig.extraOptions.sourceDyformDataUuidColumn" allow-clear style="width: calc(100% - 137px)">
                      <template v-for="(col, c) in sourceColumns">
                        <a-select-option :key="'dfd' + c" :value="col.dataIndex">{{ col.title }}</a-select-option>
                      </template>
                    </a-select>
                  </a-input-group>
                </a-form-model-item>

                <a-form-model-item>
                  <template slot="label">
                    主表字段填充
                    <a-checkbox v-model="eventConfig.extraOptions.enableFillMainFormData" />
                  </template>
                  <div v-show="eventConfig.extraOptions.enableFillMainFormData">
                    <a-radio-group
                      v-if="eventConfig.extraOptions.enableFillFormDataBySelectedRow"
                      v-model="eventConfig.extraOptions.fillRuleType"
                      button-style="solid"
                      size="small"
                      @change="onChangeFieldMatchRuleType(eventConfig.extraOptions)"
                    >
                      <a-radio-button value="fillDataBySameCode">按字段编码匹配填充</a-radio-button>
                      <a-radio-button
                        value="fillDataByFieldMapping"
                        v-if="eventConfig.extraOptions.dyformSourceType == 'sourceDyformConstant'"
                      >
                        指定字段匹配填充
                      </a-radio-button>
                    </a-radio-group>
                    <a-checkbox style="float: right" v-model="eventConfig.extraOptions.ignoreTargetFieldDefaultValue">
                      忽略目标表单字段默认值
                    </a-checkbox>

                    <a-table
                      v-if="eventConfig.extraOptions.fillRuleType == 'fillDataByFieldMapping'"
                      size="small"
                      :showHeader="false"
                      :columns="dyformFillTableColumns"
                      :pagination="false"
                      :dataSource="eventConfig.extraOptions.fillFieldMapping"
                    >
                      <template slot="title">
                        <div style="display: flex; align-items: center; justify-content: space-between">
                          字段映射
                          <div style="display: flex; align-items: center; justify-content: space-between">
                            目标表单
                            <DyformDefinitionSelect
                              style="margin-left: 6px"
                              v-model="eventConfig.extraOptions.targetFormUuid"
                              :displayModal="true"
                              ref="tDyformDefinitionSelect"
                              @change="(e, opt) => onSelectChangeDyform(e, opt, eventConfig.extraOptions.targetFormUuid, 'target')"
                            />
                          </div>
                        </div>
                      </template>

                      <template slot="mappingSlot" slot-scope="text, record, index">
                        <a-space>
                          <a-select
                            v-model="record.sourceField"
                            style="width: 180px"
                            @change="onChangeSourceFormField(record, eventConfig.extraOptions.targetFormUuid)"
                            :filterOption="filterSelectOption"
                            :show-search="true"
                          >
                            <a-select-opt-group label="源表单字段" v-if="dyformFieldOptions[eventConfig.extraOptions.sourceFormUuid]">
                              <a-select-option value="uuid">UUID</a-select-option>
                              <template v-for="(col, c) in dyformFieldOptions[eventConfig.extraOptions.sourceFormUuid]">
                                <a-select-option :key="'sf' + col.id" :value="col.value" :title="col.label + ' - ' + col.value">
                                  {{ col.label }}
                                </a-select-option>
                              </template>
                            </a-select-opt-group>
                            <a-select-opt-group label="表格列">
                              <template v-for="(col, c) in sourceColumns">
                                <a-select-option
                                  :key="'tf' + col.id"
                                  :value="'ROW.' + col.dataIndex"
                                  :title="col.title + ' - ' + col.dataIndex"
                                >
                                  {{ col.title }}
                                </a-select-option>
                              </template>
                            </a-select-opt-group>
                          </a-select>
                          <a-icon type="arrow-right" />
                          <a-select
                            v-if="dyformFieldOptions[eventConfig.extraOptions.targetFormUuid]"
                            :filterOption="filterSelectOption"
                            :show-search="true"
                            v-model="record.targetField"
                            style="width: 180px"
                          >
                            <template v-for="(col, c) in dyformFieldOptions[eventConfig.extraOptions.targetFormUuid]">
                              <a-select-option :key="'ttt' + col.id" :value="col.value" :title="col.label + ' - ' + col.value">
                                {{ col.label }}
                              </a-select-option>
                            </template>
                          </a-select>
                          <a-button
                            icon="delete"
                            size="small"
                            type="link"
                            title="删除"
                            @click="eventConfig.extraOptions.fillFieldMapping.splice(index, 1)"
                          ></a-button>
                        </a-space>
                      </template>
                      <template slot="footer">
                        <div style="text-align: right">
                          <a-button
                            type="link"
                            icon="plus"
                            size="small"
                            @click="onAddSourceFieldMapping(eventConfig.extraOptions.fillFieldMapping)"
                          >
                            添加
                          </a-button>
                        </div>
                      </template>
                    </a-table>
                  </div>
                </a-form-model-item>
                <a-form-model-item>
                  <template slot="label">
                    从表填充
                    <a-checkbox v-model="eventConfig.extraOptions.enableFillSubformData" @change="onChangeEnableFillSubformData" />
                  </template>
                  <div v-if="eventConfig.extraOptions.enableFillSubformData">
                    <a-radio-group v-model="eventConfig.extraOptions.fillSubformRuleType" button-style="solid" size="small">
                      <a-radio-button value="fillDataBySameCode">按从表与字段编码匹配填充</a-radio-button>
                      <a-radio-button
                        value="fillDataByFieldMapping"
                        v-if="eventConfig.extraOptions.dyformSourceType == 'sourceDyformConstant'"
                      >
                        指定从表与字段匹配填充
                      </a-radio-button>
                    </a-radio-group>

                    <a-table
                      v-if="eventConfig.extraOptions.fillSubformRuleType == 'fillDataByFieldMapping'"
                      size="small"
                      :showHeader="false"
                      :columns="dyformFillTableColumns"
                      :pagination="false"
                      :dataSource="eventConfig.extraOptions.filleSubformFieldMapping"
                    >
                      <template slot="title">
                        <div style="display: flex; align-items: center; justify-content: space-between">从表字段映射</div>
                      </template>

                      <template slot="mappingSlot" slot-scope="text, record, index">
                        <div>
                          <div style="display: flex">
                            <a-select
                              v-model="record.sourceFormUuid"
                              style="width: 50%"
                              :filterOption="filterSelectOption"
                              :show-search="true"
                              :options="sourceSubformOptions"
                              @change="onChangeSubformSourceFormSelect(record)"
                            />
                            <a-select
                              v-model="record.sourceField"
                              style="width: 50%"
                              @change="onChangeSourceFormField(record, record.targetFormUuid)"
                              :filterOption="filterSelectOption"
                              :show-search="true"
                              :key="'select_subform_field' + index + record.sourceFormUuid"
                            >
                              <a-select-option value="uuid">UUID</a-select-option>
                              <template
                                v-if="dyformFieldOptions[record.sourceFormUuid]"
                                v-for="(col, c) in dyformFieldOptions[record.sourceFormUuid]"
                              >
                                <a-select-option :key="'sf' + col.id" :value="col.value" :title="col.label + ' - ' + col.value">
                                  {{ col.label }}
                                </a-select-option>
                              </template>
                            </a-select>
                          </div>
                          <div style="text-align: center">
                            <a-icon type="arrow-down" />
                          </div>
                          <div style="display: flex">
                            <DyformDefinitionSelect
                              style="width: 50%"
                              v-model="record.targetFormUuid"
                              :displayModal="true"
                              @change="(e, opt) => onSelectChangeDyform(e, opt, record.targetFormUuid, 'target')"
                            />
                            <a-select
                              v-model="record.targetField"
                              style="width: 50%"
                              :filterOption="filterSelectOption"
                              :show-search="true"
                              :key="'select_subform_t_field' + index + record.targetFormUuid"
                            >
                              <a-select-option value="uuid">UUID</a-select-option>
                              <template
                                v-if="dyformFieldOptions[record.targetFormUuid]"
                                v-for="(col, c) in dyformFieldOptions[record.targetFormUuid]"
                              >
                                <a-select-option :key="'sf' + col.id" :value="col.value" :title="col.label + ' - ' + col.value">
                                  {{ col.label }}
                                </a-select-option>
                              </template>
                            </a-select>
                          </div>
                        </div>
                        <div style="text-align: right">
                          <a-button
                            icon="delete"
                            size="small"
                            type="link"
                            title="删除"
                            @click="eventConfig.extraOptions.filleSubformFieldMapping.splice(index, 1)"
                          ></a-button>
                          <a-button
                            icon="copy"
                            size="small"
                            type="link"
                            title="复制"
                            @click="copyRowData(eventConfig.extraOptions.filleSubformFieldMapping, record)"
                          ></a-button>
                        </div>
                      </template>

                      <template slot="footer">
                        <div style="text-align: right">
                          <a-button
                            type="link"
                            icon="plus"
                            size="small"
                            @click="
                              onAddSourceFieldMapping(eventConfig.extraOptions.filleSubformFieldMapping, {
                                targetFormUuid: undefined,
                                sourceFormUuid: undefined
                              })
                            "
                          >
                            添加
                          </a-button>
                        </div>
                      </template>
                    </a-table>
                  </div>
                </a-form-model-item>
              </a-form-model>
            </template>
          </template>
          <a-button type="link" icon="setting" size="small">配置</a-button>
        </WidgetDesignModal>
      </a-form-model-item>
    </template>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { filterSelectOption } from '@framework/vue/utils/function';
import { cloneDeep } from 'lodash';

export default {
  name: 'EventHandlerCopyFormDataConfiguration',
  props: {
    eventConfig: Object,
    sourceColumns: Array,
    widget: Object
  },
  components: {},
  computed: {},
  data() {
    return {
      sourceFormUuid:
        this.widget != undefined && this.widget.wtype == 'WidgetDyformSetting' ? this.widget.configuration.formUuid : undefined,
      dyformFieldOptions: {},
      dyformFillTableColumns: [
        {
          dataIndex: 'sourceDataIndex',
          width: '100%',
          scopedSlots: { customRender: 'mappingSlot' }
        }
      ],
      sourceSubformOptions: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.eventConfig && this.eventConfig.extraOptions) {
      if (this.eventConfig.extraOptions.sourceFormUuid) {
        this.onSelectChangeDyform(null, null, this.eventConfig.extraOptions.sourceFormUuid, 'source');
      }
      if (this.eventConfig.extraOptions.targetFormUuid) {
        this.onSelectChangeDyform(null, null, this.eventConfig.extraOptions.targetFormUuid, 'target');
      }
    }
  },
  mounted() {},
  methods: {
    onChangeSubformSourceFormSelect(item) {
      if (item.sourceFormUuid && item.targetFormUuid == undefined) {
        this.$set(item, 'targetFormUuid', item.sourceFormUuid);
      }
    },
    copyRowData(target, item) {
      target.push(cloneDeep(item));
    },
    onChangeEnableFillSubformData() {
      if (this.eventConfig.extraOptions.enableFillSubformData) {
        if (this.eventConfig.extraOptions.filleSubformFieldMapping == undefined) {
          this.$set(this.eventConfig.extraOptions, 'filleSubformFieldMapping', []);
        }
      }
    },
    onChangeDyformSourceType(options) {
      if (options.dyformSourceType == 'sourceDyformFromColumn') {
        options.fillRuleType = 'fillDataBySameCode';
      }
    },
    filterSelectOption,
    onChangeSourceFormField(item, targetFormUuid) {
      if (item.targetField == undefined && item.sourceField) {
        if (this.dyformFieldOptions[targetFormUuid]) {
          let targetFields = this.dyformFieldOptions[targetFormUuid];
          if (targetFields.length > 0) {
            for (let f of targetFields) {
              if (f.value == item.sourceField) {
                item.targetField = f.value;
                break;
              }
            }
          }
        }
      }
    },
    onAddSourceFieldMapping(list, option = {}) {
      list.push({
        sourceField: undefined,
        targetField: undefined,
        ...option
      });
    },
    fetchDyformDefinitionJSON(formUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/dyform/definition/getFormDefinitionByUuid?formUuid=${formUuid}`, {})
          .then(({ data }) => {
            resolve(data);
          })
          .catch(error => {});
      });
    },
    onChangeFieldMatchRuleType(options) {
      if (options.fillRuleType == 'fillDataByFieldMapping' && options.targetFormUuid == undefined && options.sourceFormUuid) {
        options.targetFormUuid = options.sourceFormUuid;

        this.onSelectChangeDyform(null, null, options.targetFormUuid, 'target');
      }
    },
    onSelectChangeDyform(e, opt, formUuid, type) {
      if (formUuid) {
        this.fetchDyformDefinitionJSON(formUuid).then(data => {
          let vjson = JSON.parse(data.definitionVjson);
          if (vjson.fields && vjson.fields.length) {
            let fieldOptions = [];
            vjson.fields.forEach(f => {
              fieldOptions.push({
                label: f.configuration.name,
                value: f.configuration.code,
                dbDataType: f.configuration.dbDataType
              });
              if (f.configuration.relaFieldConfigures) {
                f.configuration.relaFieldConfigures.forEach(item => {
                  fieldOptions.push({
                    label: f.configuration.name + ' - ' + item.name,
                    value: item.code,
                    dbDataType: f.configuration.dbDataType
                  });
                });
              }
            });
            this.$set(this.dyformFieldOptions, formUuid, fieldOptions);
          }

          if (type == 'source') {
            this.sourceSubformOptions.splice(0, this.sourceSubformOptions.length);
            if (vjson.subforms) {
              vjson.subforms.forEach(item => {
                this.sourceSubformOptions.push({
                  label: item.configuration.formName,
                  value: item.configuration.formUuid
                });
                this.fetchDyformDefinitionJSON(item.configuration.formUuid).then(d => {
                  let json = JSON.parse(d.definitionVjson);
                  if (json.fields && json.fields.length) {
                    let fields = [];
                    json.fields.forEach(f => {
                      fields.push({
                        label: f.configuration.name,
                        value: f.configuration.code,
                        dbDataType: f.configuration.dbDataType
                      });
                      if (f.configuration.relaFieldConfigures) {
                        f.configuration.relaFieldConfigures.forEach(item => {
                          fields.push({
                            label: f.configuration.name + ' - ' + item.name,
                            value: item.code,
                            dbDataType: f.configuration.dbDataType
                          });
                        });
                      }
                    });
                    this.$set(this.dyformFieldOptions, item.configuration.formUuid, fields);
                  }
                });
              });
            }
          }
        });
      }
    },
    initTableButtonWidgetEventHandlerExtraOptions(eventHandler) {
      if (!eventHandler.hasOwnProperty('extraOptions')) {
        this.$set(eventHandler, 'extraOptions', {
          enableFillFormDataBySelectedRow: false,
          dyformSourceType: 'sourceDyformConstant',
          fillRuleType: 'fillDataBySameCode',
          targetFormUuid: undefined,
          sourceFormUuid: this.sourceFormUuid,
          ignoreTargetFieldDefaultValue: false,
          fillFieldMapping: []
        });

        if (this.sourceFormUuid) {
          this.onSelectChangeDyform(null, null, this.sourceFormUuid, 'source');
        }
      }
      return true;
    },
    getModalContainer() {
      return this.$el.parentElement;
    }
  }
};
</script>
