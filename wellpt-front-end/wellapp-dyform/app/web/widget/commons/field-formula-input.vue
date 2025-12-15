<template>
  <a-form-model-item v-if="configuration.formula != undefined">
    <template slot="label">
      计算公式
      <a-checkbox v-model="configuration.formula.enable" />
    </template>
    <WidgetDesignModal v-if="configuration.formula.enable" title="计算公式" :zIndex="1000" :width="700">
      <a-button icon="calculator" type="link">配置</a-button>
      <template slot="content">
        <div class="dyform-field-formula-calculator">
          <div>
            <!-- 字段选择区域 -->
            <a-row style="display: flex; margin-bottom: 12px" v-if="filterMainFormCalculateFieldOptions.length > 0">
              <a-col :flex="100" style="font-weight: bold">表单字段</a-col>
              <a-col flex="calc(100% - 100px)">
                <template v-for="(field, i) in filterMainFormCalculateFieldOptions">
                  <a-tag
                    :key="'mfield_' + i"
                    :style="{
                      marginBottom: '5px',
                      boxShadow: '1px 1px 5px 0px #969696'
                    }"
                    @click="e => onClickFormulaItem(field)"
                  >
                    {{ field.label }}
                  </a-tag>
                </template>
              </a-col>
            </a-row>
            <a-row style="display: flex; margin-bottom: 12px" v-if="widget.wtype == 'WidgetSubform'">
              <a-col :flex="100" style="font-weight: bold">从表字段</a-col>
              <a-col flex="calc(100% - 100px)">
                <template v-for="(field, i) in filterCalculateColumnFieldOptions">
                  <a-tag
                    :style="{
                      marginBottom: '5px',
                      boxShadow: '1px 1px 5px 0px #969696'
                    }"
                    :key="'mfield_' + i"
                    @click="e => onClickFormulaItem(field)"
                  >
                    {{ field.label }}
                  </a-tag>
                </template>
              </a-col>
            </a-row>
            <a-row
              style="display: flex; margin-bottom: 12px"
              v-if="widget.wtype !== 'WidgetSubform' && subformApiCalculateOptions.length > 0"
            >
              <a-col :flex="100" style="font-weight: bold">从表函数</a-col>
              <a-col flex="calc(100% - 100px)">
                <template v-for="(field, i) in subformApiCalculateOptions">
                  <a-tag
                    :key="'mfield_' + i"
                    :style="{
                      marginBottom: '5px',
                      boxShadow: '1px 1px 5px 0px #969696'
                    }"
                    @click="e => onClickFormulaItem(field)"
                  >
                    {{ field.label }}
                  </a-tag>
                </template>
              </a-col>
            </a-row>
            <a-row style="display: flex; margin-bottom: 12px">
              <a-col :flex="100" style="font-weight: bold">日期函数</a-col>
              <a-col flex="calc(100% - 100px)">
                <template v-for="(field, i) in dateApiCalculateOptions">
                  <a-tag
                    :key="'mfield_' + i"
                    :style="{
                      marginBottom: '5px',
                      boxShadow: '1px 1px 5px 0px #969696'
                    }"
                    @click="e => onClickFormulaItem(field)"
                  >
                    {{ field.label }}
                  </a-tag>
                </template>
              </a-col>
            </a-row>
            <a-row style="display: flex; margin-bottom: 12px">
              <a-col :flex="100" style="font-weight: bold">文本函数</a-col>
              <a-col flex="calc(100% - 100px)">
                <template v-for="(field, i) in textApiCalculateOptions">
                  <a-tag
                    :key="'mfield_' + i"
                    :style="{
                      marginBottom: '5px',
                      boxShadow: '1px 1px 5px 0px #969696'
                    }"
                    @click="e => onClickFormulaItem(field)"
                  >
                    {{ field.label }}
                  </a-tag>
                </template>
              </a-col>
            </a-row>
          </div>
          <a-row style="display: flex" :gutter="16">
            <a-col flex="calc(100% - 100px)">
              <div
                style="outline: 1px solid #e8e8e8; height: 112px; border-radius: 4px; padding: 12px; overflow: auto"
                @click.stop="clickToInput"
              >
                <draggable
                  handle=".ant-tag"
                  :list="configuration.formula.items"
                  style="display: flex; flex-wrap: wrap; align-items: flex-start"
                >
                  <template v-for="(item, i) in configuration.formula.items">
                    <a-tag
                      :key="'formulaLabel' + i"
                      :class="['formula-calculate-tag', item.type === 'constant' ? '' : 'primary-color']"
                      style="margin-bottom: 5px; position: relative"
                      @click.stop="clickFormulaExpressionItem(item)"
                      v-if="item.id == undefined || (item.id != undefined && !formulaInputExpressionEdit.includes(item.id))"
                    >
                      {{ item.label }}
                      <a-icon
                        type="close-circle"
                        theme="filled"
                        style="color: red; cursor: pointer; position: absolute; top: -3px; right: -3px; font-size: 10px"
                        @click.stop="removeFormulaLabel(i)"
                      />

                      <a-popover trigger="click" v-if="item.type == 'js-api'">
                        <template slot="content">
                          <div style="width: 400px">
                            <a-form-model :colon="false" labelAlign="left">
                              <div v-if="item.apiCode != undefined && item.apiCode.startsWith('subformFormulaApi')">
                                <a-form-model-item
                                  label="从表"
                                  style="display: flex; margin-bottom: 12px"
                                  :label-col="{ style: { width: '100px' } }"
                                  :wrapper-col="{ style: { width: 'calc(100% - 100px)' } }"
                                >
                                  <a-select style="width: 100%" :options="widgetSubformOptions" v-model="item.params.widgetId" />
                                </a-form-model-item>
                                <a-form-model-item
                                  label="列"
                                  v-if="item.params.widgetId != undefined"
                                  style="display: flex"
                                  :label-col="{ style: { width: '100px' } }"
                                  :wrapper-col="{ style: { width: 'calc(100% - 100px)' } }"
                                >
                                  <a-select
                                    :key="'selectSubformCols_' + item.params.widgetId"
                                    :options="getSubformColumnOptions(item.params.widgetId, ['WidgetFormInputNumber'])"
                                    v-model="item.params.dataIndex"
                                  />
                                </a-form-model-item>
                                <a-form-model-item
                                  v-if="item.apiCode == 'avgColumnValue'"
                                  label="保留小数位"
                                  style="display: flex; margin-bottom: 12px"
                                  :label-col="{ style: { width: '100px' } }"
                                  :wrapper-col="{ style: { width: 'calc(100% - 100px)' } }"
                                >
                                  <a-input-number v-model="item.params.fixedNumber" :min="0" :max="9" />
                                </a-form-model-item>
                                <!-- <a-form-model-item v-if="item.apiCode == 'avgColumnValue'" label="保留小数位"
                                  style="display: flex; margin-bottom: 12px" :label-col="{ style: { width: '100px' } }"
                                  :wrapper-col="{ style: { width: 'calc(100% - 100px)' } }">
                                  <a-input-number v-model="item.params.fixedNumber" :min="0" :max="9" />
                                </a-form-model-item> -->
                              </div>
                              <div v-if="item.apiCode === 'dateFormulaApi.dateDiff'">
                                <a-form-model-item
                                  label="开始时间字段"
                                  style="display: flex; margin-bottom: 12px"
                                  :label-col="{ style: { width: '100px' } }"
                                  :wrapper-col="{ style: { width: 'calc(100% - 100px)' } }"
                                >
                                  <a-select
                                    v-model="item.dataIndex[0]"
                                    allow-clear
                                    @change="(e, opt) => onChangeDiffSelectDateField(e, opt, item)"
                                  >
                                    <template v-if="widget.wtype == 'WidgetSubform'">
                                      <a-select-opt-group label="表单字段">
                                        <a-select-option v-for="(opt, o) in filterDatePickerFieldOptions" :value="opt.value" :key="opt.key">
                                          {{ opt.label }}
                                        </a-select-option>
                                      </a-select-opt-group>
                                      <a-select-opt-group label="从表字段">
                                        <template v-for="(col, o) in widget.configuration.columns">
                                          <a-select-option
                                            v-if="col.widget && col.widget.wtype == 'WidgetFormDatePicker'"
                                            :value="'SUBFORM.' + col.dataIndex"
                                            :key="'SUBFORM.' + col.dataIndex"
                                          >
                                            {{ col.title + (col.widget.subtype == 'Range' ? '(开始)' : '') }}
                                          </a-select-option>
                                          <a-select-option
                                            v-if="col.widget && col.widget.wtype == 'WidgetFormDatePicker' && col.widget.subtype == 'Range'"
                                            :value="'SUBFORM.' + col.widget.configuration.endDateField"
                                            :key="'SUBFORM.' + col.widget.configuration.endDateField"
                                          >
                                            {{ col.title + '(结束)' }}
                                          </a-select-option>
                                        </template>
                                      </a-select-opt-group>
                                    </template>
                                    <template v-else>
                                      <a-select-option v-for="(opt, o) in filterDatePickerFieldOptions" :value="opt.value" :key="opt.key">
                                        {{ opt.label }}
                                      </a-select-option>
                                    </template>
                                  </a-select>
                                </a-form-model-item>
                                <a-form-model-item
                                  label="结束时间字段"
                                  style="display: flex; margin-bottom: 12px"
                                  :label-col="{ style: { width: '100px' } }"
                                  :wrapper-col="{ style: { width: 'calc(100% - 100px)' } }"
                                >
                                  <a-select v-model="item.dataIndex[1]" allow-clear @change="onChangeEndFieldSelect(item)">
                                    <template v-if="widget.wtype == 'WidgetSubform'">
                                      <a-select-opt-group label="表单字段">
                                        <a-select-option v-for="(opt, o) in filterDatePickerFieldOptions" :value="opt.value" :key="opt.key">
                                          {{ opt.label }}
                                        </a-select-option>
                                      </a-select-opt-group>
                                      <a-select-opt-group label="从表字段">
                                        <template v-for="(col, o) in widget.configuration.columns">
                                          <a-select-option
                                            v-if="col.widget && col.widget.wtype == 'WidgetFormDatePicker'"
                                            :value="'SUBFORM.' + col.dataIndex"
                                            :key="'SUBFORM.' + col.dataIndex"
                                          >
                                            {{ col.title + (col.widget.subtype == 'Range' ? '(开始)' : '') }}
                                          </a-select-option>
                                          <a-select-option
                                            v-if="col.widget && col.widget.wtype == 'WidgetFormDatePicker' && col.widget.subtype == 'Range'"
                                            :value="'SUBFORM.' + col.widget.configuration.endDateField"
                                            :key="'SUBFORM.' + col.widget.configuration.endDateField"
                                          >
                                            {{ col.title + '(结束)' }}
                                          </a-select-option>
                                        </template>
                                      </a-select-opt-group>
                                    </template>
                                    <template v-else>
                                      <a-select-option v-for="(opt, o) in filterDatePickerFieldOptions" :value="opt.value" :key="opt.key">
                                        {{ opt.label }}
                                      </a-select-option>
                                    </template>
                                  </a-select>
                                  <a-checkbox
                                    v-if="item.dataIndex[1] != undefined && showIncludeEndDayOption(item.dataIndex[1])"
                                    :key="'endDateField_' + i + item.dataIndex[1]"
                                    v-model="item.params.includeEndDay"
                                  >
                                    计算包括结束日期当天
                                  </a-checkbox>
                                </a-form-model-item>
                                <a-form-model-item
                                  label="计算类型"
                                  style="display: flex; margin-bottom: 12px"
                                  :label-col="{ style: { width: '100px' } }"
                                  :wrapper-col="{ style: { width: 'calc(100% - 100px)' } }"
                                >
                                  <a-radio-group v-model="item.params.diffBy" button-style="solid" size="small">
                                    <a-radio-button value="natureDay">按自然日</a-radio-button>
                                    <a-radio-button value="workday">按工作日</a-radio-button>
                                  </a-radio-group>
                                  <template v-if="item.params.diffBy == 'workday'">
                                    <a-select
                                      v-model="item.params.workTimePlanUuid"
                                      :options="workTimePlanOptions"
                                      :filter-option="filterSelectOption"
                                      :style="{
                                        width: item.params.workTimePlanUuid != undefined ? '80%' : '100%'
                                      }"
                                      allow-clear
                                      allow-search
                                      placeholder="缺省按系统默认工作时间方案计算"
                                    />
                                    <a-button
                                      type="link"
                                      v-show="item.params.workTimePlanUuid != undefined"
                                      @click.stop="() => openUrl('/webpage/page_20240204163811?uuid=' + item.params.workTimePlanUuid)"
                                    >
                                      配置
                                    </a-button>
                                  </template>
                                </a-form-model-item>
                                <a-form-model-item
                                  label="结果换算为"
                                  style="display: flex; margin-bottom: 12px"
                                  :label-col="{ style: { width: '100px' } }"
                                  :wrapper-col="{ style: { width: 'calc(100% - 100px)' } }"
                                >
                                  <a-radio-group v-model="item.params.unit" button-style="solid" size="small">
                                    <a-radio-button value="day">{{ item.params.diffBy == 'workday' ? '工作日' : '天数' }}</a-radio-button>
                                    <a-radio-button value="hour">{{ item.params.diffBy == 'workday' ? '工时' : '小时' }}</a-radio-button>
                                    <a-radio-button value="minute" v-show="item.params.diffBy == 'natureDay'">分钟</a-radio-button>
                                  </a-radio-group>
                                </a-form-model-item>
                              </div>
                              <div v-if="item.apiCode.startsWith('dateFormulaApi.get')">
                                <a-form-model-item
                                  label="时间字段"
                                  style="display: flex; margin-bottom: 12px"
                                  :label-col="{ style: { width: '100px' } }"
                                  :wrapper-col="{ style: { width: 'calc(100% - 100px)' } }"
                                >
                                  <a-select v-model="item.dataIndex" allow-clear>
                                    <template v-if="widget.wtype == 'WidgetSubform'">
                                      <a-select-opt-group label="表单字段">
                                        <a-select-option v-for="(opt, o) in filterDatePickerFieldOptions" :value="opt.value" :key="opt.key">
                                          {{ opt.label }}
                                        </a-select-option>
                                      </a-select-opt-group>
                                      <a-select-opt-group label="从表字段">
                                        <template v-for="(col, o) in widget.configuration.columns">
                                          <a-select-option
                                            v-if="col.widget && col.widget.wtype == 'WidgetFormDatePicker'"
                                            :value="'SUBFORM.' + col.dataIndex"
                                            :key="'SUBFORM.' + col.dataIndex"
                                          >
                                            {{ col.title }}
                                          </a-select-option>
                                        </template>
                                      </a-select-opt-group>
                                    </template>
                                    <template v-else>
                                      <a-select-option v-for="(opt, o) in filterDatePickerFieldOptions" :value="opt.value" :key="opt.key">
                                        {{ opt.label }}
                                      </a-select-option>
                                    </template>
                                  </a-select>
                                </a-form-model-item>
                              </div>
                              <div v-if="item.apiCode.startsWith('textFormulaApi.concatString')">
                                <template v-for="(field, i) in item.dataIndex">
                                  <a-form-model-item
                                    style="display: flex; margin-bottom: 12px"
                                    :label-col="{ style: { width: '100px' } }"
                                    :wrapper-col="{ style: { width: 'calc(100% - 100px)' } }"
                                  >
                                    <template slot="label">
                                      <a-select v-model="field.widgetId" style="width: 100%">
                                        <a-select-option :value="null">表单字段</a-select-option>
                                        <a-select-option
                                          v-for="(sbf, s) in widgetSubformOptions"
                                          :key="'concatSubformField_' + s"
                                          :value="sbf.value"
                                          :title="sbf.label"
                                        >
                                          {{ sbf.label }}
                                        </a-select-option>
                                      </a-select>
                                    </template>
                                    <div>
                                      <a-select
                                        v-model="field.dataIndex"
                                        allow-clear
                                        mode="multiple"
                                        :key="field.widgetId || '' + 'concatField_' + i"
                                        style="width: calc(100% - 100px)"
                                        :options="getFormFieldOptions(field.widgetId)"
                                      />
                                      <a-button-group>
                                        <a-button title="删除" icon="delete" @click="item.dataIndex.splice(i, 1)"></a-button>
                                        <a-button
                                          title="上移"
                                          v-show="i != 0"
                                          icon="arrow-up"
                                          @click="onClickMoveUpOrDownItem(item.dataIndex, i, 'forward')"
                                        ></a-button>
                                        <a-button
                                          title="下移"
                                          v-show="i != item.dataIndex.length - 1"
                                          icon="arrow-down"
                                          @click="onClickMoveUpOrDownItem(item.dataIndex, i, 'down')"
                                        ></a-button>
                                      </a-button-group>
                                    </div>
                                  </a-form-model-item>
                                </template>
                                <div style="text-align: center">
                                  <a-button icon="plus" type="link" @click="item.dataIndex.push({ widgetId: '', dataIndex: undefined })">
                                    拼接字段
                                  </a-button>
                                </div>

                                <a-form-model-item
                                  label="数据去重"
                                  style="display: flex"
                                  :label-col="{ style: { width: '100px' } }"
                                  :wrapper-col="{ style: { width: 'calc(100% - 100px)' } }"
                                >
                                  <a-switch v-model="item.params.distinct" />
                                </a-form-model-item>
                                <a-form-model-item
                                  label="拼接符号"
                                  style="display: flex"
                                  :label-col="{ style: { width: '100px' } }"
                                  :wrapper-col="{ style: { width: 'calc(100% - 100px)' } }"
                                >
                                  <a-input v-model="item.params.separator" allow-clear />
                                </a-form-model-item>
                              </div>
                            </a-form-model>
                          </div>
                        </template>
                        <a-icon type="setting" />
                      </a-popover>
                    </a-tag>
                    <a-input
                      size="small"
                      style="width: 100px; margin-right: 5px"
                      v-model="item.value"
                      @change="item.label = item.value"
                      @click.stop="() => {}"
                      v-else
                    />
                  </template>
                  <a-input
                    placeholder="请输入"
                    v-model="formulaItemInput"
                    style="width: 100px"
                    v-if="inputVisible"
                    size="small"
                    @click.stop="() => {}"
                    @pressEnter="onSaveInputValue"
                  >
                    <!-- <template slot="suffix">
                      <a-icon type="check" @click.stop="onSaveInputValue" />
                    </template> -->
                  </a-input>
                </draggable>
              </div>
              <div style="margin-top: 12px; font-weight: bold">
                结果保留
                <a-input-number size="small" v-model="configuration.formula.toFixedNumber" :min="0" :max="10" />
                位小数
              </div>
            </a-col>
            <a-col :flex="100">
              <template v-for="(sign, i) in mathFormulaSign">
                <a-tag
                  :key="'sign_' + i"
                  :style="{
                    textAlign: 'center',
                    width: '30px',
                    marginBottom: '5px',
                    boxShadow: '1px 1px 5px 0px #969696'
                  }"
                  @click="e => onClickFormulaItem(sign)"
                >
                  {{ sign.label }}
                </a-tag>
              </template>
              <a-tag style="width: 68px; box-shadow: 1px 1px 5px 0px #969696; text-align: center; margin-bottom: 5px" @click="clearFormula">
                清空
              </a-tag>
            </a-col>
          </a-row>
        </div>
      </template>
    </WidgetDesignModal>
  </a-form-model-item>
</template>
<style lang="less">
.dyform-field-formula-calculator {
  .ant-tag {
    cursor: pointer;
  }

  .formula-calculate-tag {
    > .anticon-close-circle {
      display: none;
    }

    &:hover {
      > .anticon-close-circle {
        display: block;
      }
    }
  }
}
</style>
<script type="text/babel">
import draggable from '@framework/vue/designer/draggable';
import { generateId } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function';

export default {
  name: 'FieldFormulaInput',
  mixins: [draggable],
  inject: ['appId'],
  props: {
    widget: Object,
    designer: Object,
    configuration: Object
  },
  components: {},
  computed: {
    mathFormulaSignValues() {
      let s = [];
      this.mathFormulaSign.forEach(item => {
        s.push(item.value);
      });
      return s;
    },
    allFormFieldOptions() {
      let opt = [];
      if (this.designer.SimpleFieldInfos != undefined) {
        for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
          let info = this.designer.SimpleFieldInfos[i];
          if (this.widget != undefined && this.widget.id == info.id) {
            // 避免无限次计算，要排除当前字段组件作为计算的一部分
            continue;
          }
          if ('WidgetFormDatePicker' === info.wtype) {
            let wgt = this.designer.widgetIdMap[info.id];
            let field = {
              label: info.name,
              value: info.code,
              wid: info.id
            };
            opt.push(field);

            if (info.wtype == 'WidgetFormDatePicker' && wgt.subtype == 'Range') {
              field.label = `${info.name}(开始)`;
              opt.push({
                label: `${info.name}(结束)`,
                value: wgt.configuration.endDateField,
                wid: info.id
              });
            }
          } else {
            let field = {
              label: info.name,
              value: info.code,
              wid: info.id
            };
            opt.push(field);
          }
        }
      }

      return opt;
    },
    filterDatePickerFieldOptions() {
      let opt = [];
      if (this.designer.SimpleFieldInfos != undefined) {
        for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
          let info = this.designer.SimpleFieldInfos[i];
          if (this.widget != undefined && this.widget.id == info.id) {
            // 避免无限次计算，要排除当前字段组件作为计算的一部分
            continue;
          }
          if ('WidgetFormDatePicker' === info.wtype) {
            let wgt = this.designer.widgetIdMap[info.id];
            let field = {
              label: info.name,
              value: info.code,
              wid: info.id
            };
            opt.push(field);

            if (info.wtype == 'WidgetFormDatePicker' && wgt.subtype == 'Range') {
              field.label = `${info.name}(开始)`;
              opt.push({
                label: `${info.name}(结束)`,
                value: wgt.configuration.endDateField,
                wid: info.id
              });
            }
          }
        }
      }

      return opt;
    },
    filterMainFormCalculateFieldOptions() {
      let opt = [];
      if (this.designer.SimpleFieldInfos != undefined) {
        for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
          let info = this.designer.SimpleFieldInfos[i];
          if (this.widget != undefined && this.widget.id == info.id) {
            // 避免无限次计算，要排除当前字段组件作为计算的一部分
            continue;
          }
          if (
            ['WidgetFormInputNumber', 'WidgetFormInput', 'WidgetFormCheckbox', 'WidgetFormSelect', 'WidgetFormRadio'].includes(info.wtype)
          ) {
            let wgt = this.designer.widgetIdMap[info.id];
            if (
              info.wtype == 'WidgetFormSelect' &&
              this.designer.widgetIdMap[info.id] &&
              this.designer.widgetIdMap[info.id].configuration.editMode.selectMode == 'multiple'
            ) {
              // 多选下拉框不能参与计算
              continue;
            }
            let field = {
              label: info.name,
              value: info.code,
              type: 'dataIndex',
              dataIndex: info.code
            };
            opt.push(field);
          }
        }
      }

      return opt;
    },
    widgetSubformOptions() {
      let widgetSubforms = this.designer.WidgetSubforms,
        options = [];
      if (widgetSubforms != undefined) {
        widgetSubforms.forEach(s => {
          options.push({
            label: s.title + ` (${s.configuration.formName})`,
            value: s.id
          });
        });
      }
      return options;
    },
    subformApiCalculateOptions() {
      let widgetSubforms = this.designer.WidgetSubforms,
        options = [];
      if (widgetSubforms != undefined) {
        options.push({
          label: '列求和',
          type: 'js-api',
          apiCode: 'subformFormulaApi.sumColumnValue',
          params: {
            widgetId: undefined,
            dataIndex: undefined
          }
        });

        options.push({
          label: '列求平均值',
          type: 'js-api',
          apiCode: 'subformFormulaApi.avgColumnValue',
          params: {
            widgetId: undefined,
            dataIndex: undefined,
            fixedNumber: undefined
          }
        });

        options.push({
          label: '列求最大值',
          type: 'js-api',
          apiCode: 'subformFormulaApi.maxColumnValue',
          params: {
            widgetId: undefined,
            dataIndex: undefined
          }
        });
        options.push({
          label: '列求最小值',
          type: 'js-api',
          apiCode: 'subformFormulaApi.minColumnValue',
          params: {
            widgetId: undefined,
            dataIndex: undefined
          }
        });
      }
      return options;
    },
    dateApiCalculateOptions() {
      let options = [];
      options.push(
        {
          label: '日期时间差',
          type: 'js-api',
          apiCode: 'dateFormulaApi.dateDiff',
          dataIndex: [undefined, undefined],
          params: {
            diffBy: 'natureDay', // workday
            unit: 'day' // hour
          }
        },
        {
          label: '日期的天数(1~31)',
          type: 'js-api',
          apiCode: 'dateFormulaApi.getDay',
          dataIndex: undefined,
          params: {}
        },
        {
          label: '日期的月数(1~12)',
          type: 'js-api',
          apiCode: 'dateFormulaApi.getMonth',
          dataIndex: undefined,
          params: {}
        },
        {
          label: '日期的小时数(0~23)',
          type: 'js-api',
          apiCode: 'dateFormulaApi.getHour',
          dataIndex: undefined,
          params: {}
        },

        {
          label: '日期的分钟数(0~59)',
          type: 'js-api',
          apiCode: 'dateFormulaApi.getMinute',
          dataIndex: undefined,
          params: {}
        },
        {
          label: '日期的星期天数(1~7)',
          type: 'js-api',
          apiCode: 'dateFormulaApi.getWeekday',
          dataIndex: undefined,
          params: {}
        },
        {
          label: '日期的年份',
          type: 'js-api',
          apiCode: 'dateFormulaApi.getYear',
          dataIndex: undefined,
          params: {}
        },
        {
          label: '日期所属的季度',
          type: 'js-api',
          apiCode: 'dateFormulaApi.getQuarter',
          dataIndex: undefined,
          params: {}
        },
        {
          label: '日期所属第几周',
          type: 'js-api',
          apiCode: 'dateFormulaApi.getWeek',
          dataIndex: undefined,
          params: {}
        }
      );

      return options;
    },
    textApiCalculateOptions() {
      let options = [];
      options.push({
        label: '合并文本',
        type: 'js-api',
        apiCode: 'textFormulaApi.concatString',
        dataIndex: [],
        params: {
          distinct: true
        }
      });
      return options;
    },
    filterCalculateColumnFieldOptions() {
      let cols = [];
      if (this.widget.wtype == 'WidgetSubform') {
        for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
          let column = this.widget.configuration.columns[i];
          if (
            this.configuration.id != column.id /** 排除当前字段 */ &&
            column.widget != undefined &&
            ['WidgetFormInputNumber', 'WidgetFormInput', 'WidgetFormSelect', 'WidgetFormRadio'].includes(column.widget.wtype)
          ) {
            if (column.widget.wtype == 'WidgetFormSelect' && column.widget.configuration.editMode.selectMode == 'multiple') {
              // 多选下拉框不能参与计算
              continue;
            }
            cols.push({
              label: column.title,
              value: `__SUBFORM_DATA__.${column.dataIndex}`,
              type: 'dataIndex',
              dataIndex: column.dataIndex
            });
          }
        }
      }
      return cols;
    }
  },
  data() {
    return {
      mathFormulaSign: [
        { label: '+', value: '+' },
        { label: '-', value: '-' },
        { label: 'x', value: '*' },
        { label: '÷', value: '/' },
        { label: '(', value: '(' },
        { label: ')', value: ')' }
      ],
      inputVisible: false,
      formulaItemInput: undefined,
      formulaInputExpressionEdit: [],
      workTimePlanOptions: []
    };
  },
  beforeCreate() {},
  created() {
    if (this.configuration.formula == undefined) {
      this.$set(this.configuration, 'formula', {
        enable: false,
        toFixedNumber: undefined,
        items: []
      });
    }
  },
  beforeMount() {
    this.fetchWorkTimePlanOptions();
  },
  mounted() {},
  methods: {
    openUrl(url) {
      window.open(url, '_blank');
    },
    filterSelectOption,
    fetchWorkTimePlanOptions() {
      this.workTimePlanOptions.splice(0, this.workTimePlanOptions.length);
      $axios
        .get(`/proxy/api/app/module/getModuleRelaSystems`, {
          params: {
            moduleId: this.appId
          }
        })
        .then(({ data }) => {
          let ids = data.data || [];
          if (ids.length > 0) {
            $axios
              .post(`/proxy/api/ts/work/time/plan/getAllBySystem`, ids)
              .then(({ data }) => {
                this.fetchingFormDefinition = false;
                if (data.code == 0 && data.data) {
                  for (let i = 0, len = data.data.length; i < len; i++) {
                    this.workTimePlanOptions.push({
                      label: data.data[i].name + ` (v${data.data[i].version})`,
                      value: data.data[i].uuid
                    });
                  }
                }
              })
              .catch(error => {});
          }
        });
    },
    getSubformColumnOptions(wid, filterWtypes) {
      let options = [];
      if (this.designer.WidgetSubforms != undefined) {
        for (let i = 0, len = this.designer.WidgetSubforms.length; i < len; i++) {
          if (this.designer.WidgetSubforms[i].id == wid) {
            let columns = this.designer.WidgetSubforms[i].configuration.columns;
            columns.forEach(col => {
              if (col.widget != undefined && (filterWtypes == undefined || filterWtypes.includes(col.widget.wtype))) {
                options.push({
                  label: col.title,
                  value: col.dataIndex
                });
              }
            });
            break;
          }
        }
      }
      return options;
    },
    clearFormula() {
      this.configuration.formula.items.splice(0, this.configuration.formula.items.length);
      this.formulaItemInput = undefined;
      this.inputVisible = false;
    },
    onClickFormulaItem(e) {
      let item = JSON.parse(JSON.stringify(e));
      let f = {
        label: item.label,
        ...item
      };
      if (item.type == 'js-api') {
        f.value = '_API_VAR_' + generateId(8); // 运行时计算，此处可以用任意编码代替变量名
      }
      if (f.value != undefined && f.type !== 'constant' && f.type != undefined) {
        f.value = '${' + f.value + '}';
      }

      this.configuration.formula.items.push(f);
    },

    removeFormulaLabel(i) {
      this.configuration.formula.items.splice(i, 1);
    },
    onSaveInputValue() {
      if (this.formulaItemInput != undefined && this.formulaItemInput != '') {
        this.configuration.formula.items.push({
          label: this.formulaItemInput,
          value: this.formulaItemInput,
          type: 'constant',
          id: generateId('SF')
        });
      }
      this.inputVisible = false;
      this.formulaItemInput = undefined;
    },
    clickToInput() {
      if (this.inputVisible) {
        this.inputVisible = false;
        if (this.formulaItemInput != undefined && this.formulaItemInput.trim() != '') {
          this.onSaveInputValue();
        }
      } else if (this.formulaInputExpressionEdit.length == 0) {
        this.inputVisible = true;
      }
      this.formulaInputExpressionEdit.splice(0, this.formulaInputExpressionEdit.length);
    },
    clickFormulaExpressionItem(item) {
      if (item.id) {
        // 只能同时编辑一个
        this.formulaInputExpressionEdit.splice(0, this.formulaInputExpressionEdit.length);
        if (!this.formulaInputExpressionEdit.includes(item.id)) {
          this.formulaInputExpressionEdit.push(item.id);
        }
      }
    },
    onChangeEndFieldSelect(item) {
      if (item.dataIndex[1] == undefined || !this.showIncludeEndDayOption(item.dataIndex[1])) {
        item.params.includeEndDay = undefined;
      }
    },
    showIncludeEndDayOption(dataIndex) {
      if (dataIndex != undefined) {
        if (dataIndex.startsWith('SUBFORM.')) {
          for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
            let col = this.widget.configuration.columns[i];
            if (col.dataIndex == dataIndex.replace('SUBFORM.', '')) {
              let widget = col.widget;
              return widget != undefined && widget.configuration.datePatternJson.contentFormat.endsWith('D'); // 以天结尾的要增加选项判断差额计算是否包括结束日期的当天
            }
          }
        } else {
          for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
            let info = this.designer.SimpleFieldInfos[i];
            if (info.code == dataIndex) {
              let widget = this.designer.widgetIdMap[info.id];
              return widget != undefined && widget.configuration.datePatternJson.contentFormat.endsWith('D'); // 以天结尾的要增加选项判断差额计算是否包括结束日期的当天
            } else if (info.relaFields != undefined) {
              for (let j = 0, jlen = info.relaFields.length; j < jlen; j++) {
                if (info.relaFields[j].code == dataIndex) {
                  let widget = this.designer.widgetIdMap[info.id];
                  return widget != undefined && widget.configuration.datePatternJson.contentFormat.endsWith('D'); // 以天结尾的要增加选项判断差额计算是否包括结束日期的当天
                }
              }
            }
          }
        }
      }
      return false;
    },
    getFormFieldOptions(widgetId) {
      return widgetId ? this.getSubformColumnOptions(widgetId) : this.allFormFieldOptions;
    },
    onChangeDiffSelectDateField(e, opt, item) {
      if (item.dataIndex[0] != undefined && item.dataIndex[1] == undefined) {
        // 自动带出结束时间字段
        if (item.dataIndex[0].startsWith('SUBFORM.')) {
          for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
            let col = this.widget.configuration.columns[i];
            if ('SUBFORM.' + col.dataIndex == item.dataIndex[0]) {
              item.dataIndex[1] = 'SUBFORM.' + col.widget.configuration.endDateField;
              break;
            }
          }
        } else {
          for (let i = 0, len = this.filterDatePickerFieldOptions.length; i < len; i++) {
            let opt = this.filterDatePickerFieldOptions[i];
            if (opt.value == item.dataIndex[0]) {
              let wgt = this.designer.widgetIdMap[opt.wid];
              if (wgt != undefined && wgt.subtype == 'Range') {
                item.dataIndex[1] = this.filterDatePickerFieldOptions[i + 1].value;
              }
              break;
            }
          }
        }
      }
    },
    onClickMoveUpOrDownItem(sources, i, direction) {
      let j = direction == 'forward' ? (i == 0 ? sources.length : i - 1) : i == sources.length - 1 ? 0 : i + 1;
      let temp = sources.splice(i, 1)[0];
      sources.splice(j, 0, temp);
    }
  }
};
</script>
