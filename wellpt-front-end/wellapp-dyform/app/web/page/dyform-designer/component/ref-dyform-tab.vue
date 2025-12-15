<template>
  <div>
    <div style="text-align: right; line-height: 40px; padding-right: 12px">
      <a-popover v-model="refDyformPopoverVisible" :title="null" trigger="click" :destroyTooltipOnHide="true">
        <template slot="content">
          <div>
            <div>
              <a-select
                style="width: 200px"
                v-model="selectRefDyformId"
                :options="refDyformOptions"
                :filter-option="filterOption"
                allow-clear
                show-search
              >
                <a-icon slot="suffixIcon" type="loading" v-if="!dyformOptionsFetched" />
              </a-select>
            </div>
            <div style="text-align: right; margin-top: 8px">
              <a-button size="small" @click.stop="onConfirmRefDyformPop(false)">取消</a-button>
              <a-button size="small" type="primary" @click.stop="onConfirmRefDyformPop(true)">确认</a-button>
            </div>
          </div>
        </template>
        <a-button type="link" icon="plus" size="small">引用数据表单</a-button>
      </a-popover>
    </div>

    <a-empty v-if="refDyforms.length == 0" style="margin-top: 100px" />

    <PerfectScrollbar style="height: calc(100vh - 92px)">
      <a-collapse :bordered="false" expandIconPosition="right" class="design-template" v-model="collapseActiveKeys">
        <template v-for="(item, i) in refDyforms">
          <a-collapse-panel :key="item.id">
            <template slot="header">
              <!-- <i class="line" /> -->
              <div style="display: inline-flex; width: calc(100% - 50px); align-items: center; justify-content: space-between">
                <label
                  :title="item.title"
                  class=""
                  style="width: calc(100% - 115px); display: inline-block; text-overflow: ellipsis; text-wrap: nowrap; overflow: hidden"
                >
                  {{ item.title }}
                </label>
                <div style="align-self: flex-end">
                  <a-button
                    v-show="!item.loading"
                    title="跳转设计"
                    size="small"
                    icon="environment"
                    type="link"
                    @click.stop="e => jumpToFormDesign(item, e)"
                  />
                  <WidgetDesignDrawer :id="'refDyformDataset_' + item.id" title="设置" :designer="designer" placement="left" :width="500">
                    <a-button
                      title="设置"
                      size="small"
                      type="link"
                      class="icon-only"
                      @click="e => startEditRefDyformPropCondition(item, e)"
                    >
                      <Icon :type="item.loading ? 'loading' : 'pticon iconfont icon-ptkj-shezhi'"></Icon>
                    </a-button>
                    <template slot="content">
                      <Scroll style="height: calc(100vh - 215px)" ref="scroll">
                        <a-collapse
                          style="background-color: #fff"
                          :default-active-key="['field-set']"
                          :bordered="false"
                          expandIconPosition="right"
                          @change="onChangeCollapse"
                          accordion
                        >
                          <a-collapse-panel key="field-set">
                            <template slot="header">
                              <i class="line" />
                              字段
                            </template>
                            <a-table
                              :columns="tableColumns"
                              :data-source="item.fields"
                              rowKey="id"
                              :pagination="false"
                              style="margin-bottom: 12px"
                            >
                              <template slot="nameSlot" slot-scope="text, record">
                                {{ record.configuration.name }}
                              </template>
                              <template slot="codeSlot" slot-scope="text, record">{{ record.configuration.code }}</template>
                              <template slot="operationSlot" slot-scope="text, record">
                                <a-radio-group
                                  size="small"
                                  v-model="record.configuration.defaultDisplayState"
                                  button-style="solid"
                                  @change="onChangeRefDyformFieldDisplayState(item)"
                                >
                                  <a-radio-button value="edit">可编辑</a-radio-button>
                                  <a-radio-button value="unedit">不可编辑</a-radio-button>
                                </a-radio-group>
                              </template>
                              <template slot="customOperationTitle">
                                <a-radio-group
                                  size="small"
                                  v-model="item.defaultDisplayState"
                                  button-style="solid"
                                  @change="onChangeRefDyformDisplayState(item)"
                                >
                                  <a-radio-button value="edit">可编辑</a-radio-button>
                                  <a-radio-button value="unedit">不可编辑</a-radio-button>
                                </a-radio-group>
                              </template>
                            </a-table>
                          </a-collapse-panel>
                          <a-collapse-panel key="condition-set" style="border-bottom: unset">
                            <template slot="header">
                              <i class="line" />
                              数据条件
                            </template>
                            <a-alert message="请确保数据条件仅能筛选出唯一数据" banner type="info" style="margin-bottom: 8px" />
                            <div class="dyform-condition-form-item" v-if="tempData[item.id] != undefined">
                              <template v-for="(con, i) in tempData[item.id].condition">
                                <template v-if="con.sign == 'AND' || con.sign == 'OR'">
                                  <div style="text-align: center">
                                    <a-select
                                      size="small"
                                      style="width: 100px"
                                      :options="[
                                        { label: '并且', value: 'AND' },
                                        { label: '或者', value: 'OR' }
                                      ]"
                                      v-model="con.sign"
                                    />
                                  </div>
                                </template>
                                <template v-else>
                                  <div class="prop-condition">
                                    <div class="toolbar">
                                      <a-switch
                                        class="conType"
                                        v-model="con.sqlWord"
                                        size="small"
                                        checked-children="SQL"
                                        un-checked-children="SQL"
                                      />
                                      <a-popconfirm
                                        placement="left"
                                        :arrowPointAtCenter="true"
                                        title="确认要删除吗?"
                                        ok-text="删除"
                                        cancel-text="取消"
                                        @confirm="onRemoveCondition(i, item.condition)"
                                      >
                                        <a-button type="link" class="delete-condition" icon="delete"></a-button>
                                      </a-popconfirm>
                                    </div>
                                    <template v-for="(b, i) in con.leftBracket">
                                      <a-tag :key="'leftB_' + i" closable color="blue" @close="onRemoveBracket(con, 'leftBracket', i)">
                                        (
                                      </a-tag>
                                    </template>
                                    <a-tag style="background: #fff; border-style: dashed" @click="addBracket('leftBracket', con, '(')">
                                      <a-icon type="plus" />
                                      添加左括号
                                    </a-tag>

                                    <template v-if="con.sqlWord === true">
                                      <div :style="{}">
                                        <WidgetCodeEditor lang="sql" v-model="con.sql" width="368px" height="100px" />
                                      </div>
                                    </template>
                                    <template v-else>
                                      <a-select
                                        :style="{ width: '100%' }"
                                        v-model="con.prop"
                                        style="width: 100%"
                                        @change="onPropChange(con, 'prop')"
                                        :getPopupContainer="getPopupContainerByPs()"
                                      >
                                        <a-select-option value="UUID">UUID</a-select-option>
                                        <template v-for="(field, f) in item.fields">
                                          <a-select-option :value="field.configuration.code">
                                            {{ field.configuration.name }}
                                            <a-tag style="position: absolute; right: 0px; top: 4px">
                                              {{ field.configuration.code.toUpperCase() }}
                                            </a-tag>
                                          </a-select-option>
                                        </template>
                                      </a-select>

                                      <a-select
                                        v-model="con.sign"
                                        style="width: 100%"
                                        @change="onChangeConSign(con)"
                                        :getPopupContainer="getPopupContainerByPs()"
                                      >
                                        <template v-for="(opt, i) in getPropComparator(con, item.fields)">
                                          <a-select-option :value="opt.value" :key="'con_' + i">
                                            {{ opt.label }}
                                            <a-tag style="position: absolute; right: 0px; top: 4px">{{ opt.value.toUpperCase() }}</a-tag>
                                          </a-select-option>
                                        </template>
                                      </a-select>
                                      <!-- 非空查询 -->
                                      <template v-if="con.sign !== 'is null' && con.sign !== 'is not null'">
                                        <a-input-group compact>
                                          <a-select
                                            :getPopupContainer="getPopupContainerByPs()"
                                            style="width: 30%"
                                            :options="[
                                              { label: '常量', value: 'constant' },
                                              { label: '表单字段', value: 'prop' },
                                              { label: '变量', value: 'var' }
                                            ]"
                                            v-model="con.valueType"
                                            @change="onSelectPropValType(con, 'value')"
                                          />
                                          <a-input
                                            v-show="con.valueType == 'constant'"
                                            v-model.trim="con.value"
                                            style="width: 70%"
                                            :style="{}"
                                            @blur="onBlurInputConValue(con)"
                                          />

                                          <a-input
                                            v-show="con.valueType == 'var'"
                                            v-model="con.value"
                                            style="width: 70%"
                                            @blur="onBlurInputConValue(con)"
                                            prefix=":"
                                            :style="{ outline: 'unset' }"
                                          >
                                            <a-tooltip slot="suffix" title="运行时不存在该变量, 则该数据过滤将被放弃">
                                              <a-icon type="info-circle" style="color: rgba(0, 0, 0, 0.45)" />
                                            </a-tooltip>
                                          </a-input>

                                          <a-select
                                            :getPopupContainer="getPopupContainerByPs()"
                                            v-show="con.valueType == 'prop'"
                                            v-model="con.value"
                                            @change="onPropChange(con, 'value')"
                                            :style="{
                                              width: '70%',
                                              outline: con.error && con.error.value == false ? '2px solid red' : 'unset'
                                            }"
                                          >
                                            <template v-for="(col, c) in columnOptions">
                                              <a-select-option :key="col.value" :value="col.value">
                                                {{ col.label }}
                                                <a-tag style="position: absolute; right: 0px; top: 4px">
                                                  {{ col.code.toUpperCase() }}
                                                </a-tag>
                                              </a-select-option>
                                            </template>
                                            <template v-for="(field, f) in item.fields">
                                              <a-select-option :value="field.configuration.code">
                                                {{ field.configuration.name }}
                                                <a-tag style="position: absolute; right: 0px; top: 4px">
                                                  {{ field.configuration.code.toUpperCase() }}
                                                </a-tag>
                                              </a-select-option>
                                            </template>
                                          </a-select>
                                        </a-input-group>
                                      </template>
                                    </template>
                                    <template v-for="(b, i) in con.rightBracket">
                                      <a-tag :key="'rightB_' + i" closable color="blue" @close="onRemoveBracket(con, 'rightBracket', i)">
                                        )
                                      </a-tag>
                                    </template>
                                    <a-tag style="background: #fff; border-style: dashed" @click="addBracket('rightBracket', con, ')')">
                                      <a-icon type="plus" />
                                      添加右括号
                                    </a-tag>
                                  </div>
                                </template>
                              </template>
                            </div>
                            <a-button style="margin-top: 12px" @click="addCondition(item)" type="default" size="small" icon="plus" block>
                              添加条件
                            </a-button>
                          </a-collapse-panel>
                        </a-collapse>
                      </Scroll>
                    </template>
                    <template slot="footer" slot-scope="drawerScope">
                      <a-button type="primary" @click="e => onSaveRefDyfromSetting(drawerScope, item)">保存</a-button>
                    </template>
                  </WidgetDesignDrawer>

                  <a-popconfirm
                    placement="bottomLeft"
                    :arrowPointAtCenter="true"
                    title="确定要删除引用表单 , 删除后将自动删除设计器使用到的字段?"
                    ok-text="确定"
                    cancel-text="取消"
                    @confirm="e => deleteRefDyform(i)"
                  >
                    <a-button title="删除引用表单" size="small" class="icon-only" type="link" @click.stop="() => {}">
                      <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                    </a-button>
                  </a-popconfirm>
                </div>
              </div>
            </template>

            <div style="height: 55px; position: relative" v-if="item.loading">
              <div class="spin-center">
                <a-spin />
              </div>
            </div>

            <draggable
              tag="ul"
              :list="item.fields"
              :group="{ name: currentDragGroup, pull: 'clone', put: false }"
              :clone="e => handleRefDyformFieldClone(e, item)"
              @end="handleRedDyformFieldDragEnd"
              :sort="false"
              filter=".field-used"
              class="select-field-2-rect"
            >
              <li
                v-for="(select, index) in item.fields"
                :key="index"
                :class="[
                  'widget-select-item data-model-column',
                  fieldUsed[item.id].includes(select.configuration.code) ? 'field-used' : ''
                ]"
                :title="select.configuration.name"
                style="text-align: center"
                @click.stop="onClickRefField(select, item)"
              >
                {{ select.configuration.name || select.configuration.code }}
              </li>
            </draggable>
          </a-collapse-panel>
        </template>
      </a-collapse>
    </PerfectScrollbar>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { orderBy } from 'lodash';
import { getPopupContainerByPs } from '../utils';
export default {
  name: 'RefDyformTab',
  inject: ['dyform'],
  props: {
    definition: Object,
    definitionVjson: Object,
    designer: Object,
    provideRefDyforms: Array
  },
  components: {},
  computed: {
    currentDragGroup() {
      return this.designer.currentCanDragGroup;
    },

    fieldUsed() {
      let map = {};
      if (this.definitionVjson.refDyform != undefined) {
        for (let key in this.definitionVjson.refDyform) {
          map[key] = Object.keys(this.definitionVjson.refDyform[key].field);
        }
      }
      return map;
    },

    columnOptions() {
      let options = [];
      if (this.designer.SimpleFieldInfos != undefined) {
        for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
          options.push({
            label: this.definition.name + '.' + this.designer.SimpleFieldInfos[i].name,
            value: ':MAIN_FORM_DATA_' + this.designer.SimpleFieldInfos[i].code,
            code: this.designer.SimpleFieldInfos[i].code
          });
        }
      }

      return options;
    }
  },
  data() {
    return {
      collapseActiveKeys: [],
      refDyformOptions: [],
      selectRefDyformId: undefined,
      refDyformPopoverVisible: false,
      dyformOptionsFetched: false,
      refDyforms: [],
      refDyformOriginFields: {},
      propComparator: [
        { label: '等于', value: '=' },
        { label: '不等于', value: '!=' },
        { label: '大于', value: '>' },
        { label: '大于等于', value: '>=' },
        { label: '小于', value: '<' },
        { label: '小于等于', value: '<=' },
        { label: '匹配', value: 'like' },
        { label: '不匹配', value: 'not like' },
        { label: 'IN查询', value: 'in' },
        { label: 'NOT IN查询', value: 'not in' },
        { label: '为空', value: 'is null' },
        { label: '不为空', value: 'is not null' }
      ],
      tempData: {},
      // fieldUsed: {},
      tableColumns: [
        {
          title: '名称',
          scopedSlots: { customRender: 'nameSlot' }
        },
        {
          title: '编码',
          scopedSlots: { customRender: 'codeSlot' }
        },
        ,
        {
          width: 155,
          slots: { title: 'customOperationTitle' },
          scopedSlots: { customRender: 'operationSlot' }
        }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    if (this.definitionVjson.refDyform == undefined) {
      this.definitionVjson.refDyform = {};
    }
    if (this.definitionVjson.refDyform) {
      for (let key in this.definitionVjson.refDyform) {
        let conf = this.definitionVjson.refDyform[key];
        let tab = {
          id: key,
          title: conf.name,
          fields: [],
          condition: [],
          defaultCondition: conf.defaultCondition,
          defaultDisplayState: conf.defaultDisplayState,
          loading: true,
          namedParameter: conf.namedParameter
        };
        let form = this.dyform.createRefDyForm(key, undefined);
        this.refDyformOriginFields[key] = {};
        this.fetchDyformDefinition(undefined, key).then(def => {
          let vjson = JSON.parse(def.definitionVjson);
          form.formDefinitionJson = JSON.parse(def.definitionVjson);
          form.formId = key;
          this.provideRefDyforms.push(form);
          for (let i = 0, len = vjson.fields.length; i < len; i++) {
            this.refDyformOriginFields[key][vjson.fields[i].configuration.code] = JSON.parse(JSON.stringify(vjson.fields[i]));
            vjson.fields[i].configuration.defaultDisplayState = 'unedit';
            if (conf.edit && conf.edit.includes(vjson.fields[i].configuration.code)) {
              vjson.fields[i].configuration.defaultDisplayState = 'edit';
            }
            if (conf.unedit && conf.unedit.includes(vjson.fields[i].configuration.code)) {
              vjson.fields[i].configuration.defaultDisplayState = 'unedit';
            }
            if (vjson.fields[i].configuration.defaultDisplayState == 'unedit') {
              vjson.fields[i].configuration.required = false;
            }
            tab.fields.push(vjson.fields[i]);
          }
          tab.title = def.name;
          tab.loading = false;
          tab.uuid = def.uuid;
          if (!this.collapseActiveKeys.includes(tab.id)) {
            this.collapseActiveKeys.push(tab.id);
          }
          this.$set(this.tempData, tab.id, tab);
        });
        this.refDyforms.push(tab);
      }
    }
    this.getRefDyformOptions();
    this.designer.handleEvent(`refDyformWidgetConfigurationChange`, wgt => {
      let conf = this.definitionVjson.refDyform[wgt.refDyformId].field[wgt.configuration.code].configuration;
      conf.defaultDisplayState = wgt.configuration.defaultDisplayState;
      this.designer.widgetIdMap[wgt.id].configuration.required =
        conf.defaultDisplayState == 'unedit'
          ? false
          : this.refDyformOriginFields[wgt.refDyformId][wgt.configuration.code].configuration.required;
      if (this.tempData[wgt.refDyformId] != undefined) {
        let temp = this.tempData[wgt.refDyformId];
        for (let i = 0, len = temp.fields.length; i < len; i++) {
          if (temp.fields[i].id == wgt.id) {
            temp.fields[i].configuration.defaultDisplayState = wgt.configuration.defaultDisplayState;
          }
        }
      }
    });
  },
  methods: {
    onChangeCollapse() {
      this.$nextTick(() => {
        this.$refs.scroll[0].$refs.perfectScrollbarRef.ps.element.scrollTop = 0;
      });
    },
    onClickRefField(select, item) {
      if (this.fieldUsed[item.id] != undefined && this.fieldUsed[item.id].includes(select.configuration.code)) {
        this.designer.selectedByID(select.id);
      }
    },
    getPopupContainerByPs,
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    jumpToFormDesign(item) {
      window.open(`/dyform-designer/index?uuid=${item.uuid}`, '_blank');
    },
    startEditRefDyformPropCondition(item, e) {
      if (item.loading) {
        e.stopPropagation();
        return;
      }
      let temp = this.tempData[item.id];
      temp.condition.splice(0, temp.condition.length);
      temp.condition.push(...JSON.parse(JSON.stringify(this.definitionVjson.refDyform[item.id].condition)));
      temp.defaultDisplayState = this.definitionVjson.refDyform[item.id].defaultDisplayState || 'unedit';

      // 更新字段编辑性
      for (let i = 0, len = temp.fields.length; i < len; i++) {
        let f = this.definitionVjson.refDyform[item.id].field[temp.fields[i].configuration.code];
        temp.fields[i].configuration.defaultDisplayState = f ? f.configuration.defaultDisplayState : 'unedit';
      }
    },
    getPropComparator(con, fields) {
      let f = undefined,
        isClob = false;
      if (con.prop) {
        for (let i = 0, len = fields.length; i < len; i++) {
          if (fields[i].configuration.code == con.prop) {
            isClob = fields[i].configuration.dbDataType == '16';
            break;
          }
        }
        if (isClob) {
          // 大字段不支持以下比较符号
          let opt = [];
          for (let i = 0, len = this.propComparator.length; i < len; i++) {
            if (['like', 'not like', 'is null', 'is not null'].includes(this.propComparator[i].value)) {
              opt.push(this.propComparator[i]);
            }
          }
          return opt;
        }
      }

      return this.propComparator;
    },
    onRemoveBracket(obj, key, i) {
      obj[key].splice(i, 1);
    },
    addBracket(key, obj, v) {
      obj[key].push(v);
    },
    addCondition(item) {
      let condition = this.tempData[item.id].condition;
      if (condition.length >= 1) {
        condition.push({
          sign: 'AND'
        });
      }

      condition.push({
        prop: undefined,
        sign: '=',
        valueType: 'constant',
        value: undefined,
        leftBracket: [],
        rightBracket: []
      });
    },
    onSaveRefDyfromSetting(e, item) {
      // 数据条件校验与保存
      let where = [],
        leftBracketCnt = 0,
        rightBracketCnt = 0,
        namedParameter = {};
      let hasError = false;
      let condition = this.tempData[item.id].condition;
      let fieldMap = {};
      for (let i = 0, len = item.fields.length; i < len; i++) {
        fieldMap[item.fields[i].configuration.code] = item.fields[i];
      }
      for (let con of condition) {
        if (['AND', 'OR'].includes(con.sign)) {
          where.push(con.sign);
        } else {
          leftBracketCnt += con.leftBracket.length;
          where.push(con.leftBracket.join(''));

          this.$set(con, 'error', undefined);
          if (con.sqlWord) {
            this.$set(con, 'error', con.sql == undefined || con.sql.trim() == '' ? { sqlWord: false } : undefined);
            if (con.error != undefined) {
              hasError = true;
            }
            where.push(con.sql);
          } else {
            let dataType = 'varchar';
            if (fieldMap[con.prop]) {
              if (fieldMap[con.prop].wtype == 'WidgetFormInputNumber') {
                dataType = 'number';
              } else if (fieldMap[con.prop].wtype == 'WidgetFormDatePicker') {
                dataType = 'timestamp';
              }
            }

            let formatValue = (value, valueType) => {
              let conValue = value;
              if (valueType == 'constant') {
                // 常量情况下，直接拼接，如果是日期类，则转为变量由后端格式化传入
                if (dataType == 'varchar') {
                  conValue = `'${value}'`;
                } else if (dataType == 'number') {
                  conValue = Number(value);
                } else if (dataType == 'timestamp') {
                  // 日期需要格式化函数（针对不同数据库，再实际解析时候由后端处理
                  conValue = `TO_DATE('${value}','YYYY-MM-DD HH24:MI:SS')`;
                }
              } else if (con.valueType == 'var') {
                namedParameter[conValue] = { dataType }; // 变量的值由运行期决定
                conValue = ':' + conValue;
              }
              return conValue;
            };
            let conValue1 = formatValue.call(this, con.value, con.valueType),
              conValue2 = undefined;
            if (con.sign == 'between') {
              conValue2 = formatValue.call(this, con.value2, con.valueType2);
            }
            let error = {};
            if (con.prop == undefined) {
              error.prop = false;
              hasError = true;
            }
            if (!['is null', 'is not null'].includes(con.sign) && (con.value == undefined || con.value.trim() == '')) {
              error.value = false;
              hasError = true;
            }
            if ('between' == con.sign && (con.value2 == undefined || con.value2.trim() == '')) {
              error.value2 = false;
              hasError = true;
            }
            this.$set(con, 'error', Object.keys(error).length > 0 ? error : undefined);
            if (['is null', 'is not null'].includes(con.sign)) {
              where.push(`${con.prop} ${con.sign}`);
            } else if ('between' == con.sign) {
              where.push(
                `${con.prop} ${con.sign} ${con.valueType == 'prop' ? `${con.value}` : conValue1} AND ${
                  con.valueType2 == 'prop' ? `${con.value2}` : conValue2
                }`
              );
            } else {
              where.push(`${con.prop} ${con.sign} ${con.valueType == 'prop' ? `${con.value}` : conValue1}`);
            }
          }

          rightBracketCnt += con.rightBracket.length;
          where.push(con.rightBracket.join(''));
        }
      }

      if (leftBracketCnt != rightBracketCnt) {
        this.$message.error('左右括号未闭合');
        return;
      }
      if (hasError) {
        this.$message.error('请填写完整条件');
        this.$emit('change', { sql: new Error('左右括号未闭合') });
        return;
      }

      this.definitionVjson.refDyform[item.id].condition = JSON.parse(JSON.stringify(this.tempData[item.id].condition));
      this.definitionVjson.refDyform[item.id].defaultDisplayState = this.tempData[item.id].defaultDisplayState;

      // 修改字段编辑状态
      let edit = [],
        unedit = [];
      for (let i = 0, len = this.tempData[item.id].fields.length; i < len; i++) {
        let f = this.tempData[item.id].fields[i];
        // 修改设计器内的组件默认状态
        if (this.designer.widgetIdMap[f.id]) {
          this.designer.widgetIdMap[f.id].configuration.defaultDisplayState = f.configuration.defaultDisplayState;
          this.designer.widgetIdMap[f.id].configuration.required =
            f.configuration.defaultDisplayState == 'unedit'
              ? false
              : this.refDyformOriginFields[item.id][f.configuration.code].configuration.required;
        }
        if (this.definitionVjson.refDyform[item.id].field[f.configuration.code]) {
          this.definitionVjson.refDyform[item.id].field[f.configuration.code].configuration.defaultDisplayState =
            f.configuration.defaultDisplayState;
        }
        if (f.configuration.defaultDisplayState == 'unedit') {
          unedit.push(f.configuration.code);
        }
        if (f.configuration.defaultDisplayState == 'edit') {
          edit.push(f.configuration.code);
        }
      }
      this.definitionVjson.refDyform[item.id].unedit = unedit;
      this.definitionVjson.refDyform[item.id].edit = edit;
      e.close();
    },
    onRemoveCondition(i, condition) {
      let len = condition.length;
      if (i == len - 1 && len > 2) {
        condition.splice(len - 2, 2);
      } else {
        condition.splice(i, 2); // 移除条件
      }
    },
    deleteRefDyform(index) {
      let formId = this.refDyforms[index].id;
      // 删除表单中的组件
      for (let key in this.definitionVjson.refDyform[formId].field) {
        this.designer.emitEvent(`${this.definitionVjson.refDyform[formId].field[key].id}:delete`);
      }
      this.dyform.deleteRefDyForm(formId);
      delete this.definitionVjson.refDyform[formId];
      delete this.tempData[formId];
      this.refDyforms.splice(index, 1);
    },
    onSelectPropValType(con, prop) {
      con[prop] = undefined;
    },

    handleRedDyformFieldDragEnd(e) {
      if (e.pullMode == 'clone') {
        let widget = JSON.parse(JSON.stringify(e.item._underlying_vm_));
        widget.wtype = 'WidgetRefFormFieldPlaceholder';
        this.$set(this.definitionVjson.refDyform[widget.refDyformId].field, widget.configuration.code, widget);
      }
    },
    handleRefDyformFieldClone(origin, item) {
      let data = JSON.parse(JSON.stringify(origin));
      data.refDyformId = item.id;
      data.wtype = 'WidgetRefFormFieldPlaceholder';
      return data;
    },
    onBlurInputConValue(con, i) {
      let key = i == undefined ? 'value' : 'value2';
      if (con[key] != undefined && con.error && con.error[key] == false) {
        con.error[key] = true;
      }
    },
    onChangeConSign(con) {
      if (con.sign == 'between' && con.valueType2 == undefined) {
        con.valueType2 = 'constant';
      }
    },
    getRefDyformOptions() {
      if (this.definition.moduleId) {
        $axios
          .get(`/proxy/api/app/module/queryRelaModuleIds`, {
            params: {
              moduleId: this.definition.moduleId
            }
          })
          .then(({ data }) => {
            let ids = data.data || [];
            ids.push(this.definition.moduleId);
            $axios
              .post(`/proxy/api/dyform/definition/queryFormDefinitionIgnoreJsonByModuleIds`, ids)
              .then(({ data }) => {
                this.dyformOptionsFetched = true;
                this.refDyformMap = {};
                if (data.code == 0 && data.data) {
                  let list = orderBy(
                    data.data,
                    [
                      o => {
                        return parseFloat(o.version);
                      }
                    ],
                    ['desc']
                  );
                  let added = [];
                  for (let i = 0, len = list.length; i < len; i++) {
                    if (list[i].formType == 'P' && !added.includes(list[i].id)) {
                      this.refDyformOptions.push({
                        label: list[i].name,
                        value: list[i].id,
                        uuid: list[i].uuid
                      });
                      this.refDyformMap[list[i].id] = list[i];
                      added.push(list[i].id);
                    }
                  }
                  // console.log('引用表单数据源', this.refDyformOptions);
                }
              })
              .catch(error => {});
          })
          .catch(error => {});
      }
    },
    onPropChange(con, name) {
      // let aProp = this.columnLocationMap[con[name]],
      //   aDataType = aProp ? aProp.dataType : null,
      //   bProp = this.columnLocationMap[con[name == 'prop' ? 'value' : 'prop']],
      //   bDataType = bProp ? bProp.dataType : null;
      // if (aDataType != null && bDataType != null && aDataType != bDataType) {
      //   this.$message.error('属性类型不一致');
      //   con[name] = undefined;
      // }
      // if (con[name] != undefined && con.error && con.error[name] == false) {
      //   con.error[name] = true;
      // }
      // if (name == 'prop' && con.prop != undefined && aDataType == 'clob') {
      //   con.sign = 'like';
      // }
      // if (name == 'prop' && con.prop != undefined) {
      //   con.dataType = aDataType;
      // }
    },
    onConfirmRefDyformPop(ok) {
      if (ok) {
        let ids = [];
        for (let i = 0, len = this.refDyforms.length; i < len; i++) {
          ids.push(this.refDyforms[i].id);
        }
        if (ids.includes(this.selectRefDyformId)) {
          this.$message.info('已引入数据表单, 不需要重复引入');
          return;
        }
        let id = this.selectRefDyformId;
        let tab = {
          id,
          title: this.refDyformMap[id].name,
          defaultDisplayState: 'unedit',
          fields: [],
          condition: [],
          loading: true
        };
        this.refDyforms.push(tab);
        // this.$set(this.fieldUsed, id, []);
        this.$set(this.definitionVjson.refDyform, id, {
          name: this.refDyformMap[id].name,
          defaultCondition: 'unedit',
          condition: [],
          field: {},
          namedParameter: {}
        });

        if (!this.collapseActiveKeys.includes(id)) {
          this.collapseActiveKeys.push(id);
        }
        let form = this.dyform.createRefDyForm(id, undefined);
        this.fetchDyformDefinition(this.refDyformMap[id].uuid).then(def => {
          let vjson = JSON.parse(def.definitionVjson);
          form.formDefinitionJson = JSON.parse(def.definitionVjson);
          form.formId = id;
          this.refDyformOriginFields[id] = {};
          for (let i = 0, len = vjson.fields.length; i < len; i++) {
            this.refDyformOriginFields[id][vjson.fields[i].configuration.code] = JSON.parse(JSON.stringify(vjson.fields[i]));
            vjson.fields[i].configuration.defaultDisplayState = 'unedit'; // 默认引入表单的字段都设置为不可编辑，仅查阅
            vjson.fields[i].configuration.required = false;
            tab.fields.push(vjson.fields[i]);
          }
          tab.title = def.name;
          tab.loading = false;
          tab.uuid = def.uuid;
          this.$set(this.tempData, id, tab);
          this.provideRefDyforms.push(form);
        });
      }
      this.refDyformPopoverVisible = false;
      this.selectRefDyformId = undefined;
    },
    onSetRefDyformDataSource() {},
    fetchDyformDefinition(formUuid, formId) {
      return new Promise((resolve, reject) => {
        $axios
          .post(
            `/proxy/api/dyform/definition/${
              formUuid ? 'getFormDefinitionByUuid?formUuid=' + formUuid : 'getFormDefinitionById?id=' + formId
            }`,
            {}
          )
          .then(({ data }) => {
            resolve(data);
          })
          .catch(error => {});
      });
    },
    onChangeRefDyformDisplayState(item) {
      for (let i = 0, len = item.fields.length; i < len; i++) {
        item.fields[i].configuration.defaultDisplayState = item.defaultDisplayState;
        item.fields[i].configuration.required =
          item.defaultDisplayState == 'unedit'
            ? false
            : this.refDyformOriginFields[item.id][item.fields[i].configuration.code].configuration.required;
      }
    },
    onChangeRefDyformFieldDisplayState(item) {
      let unedit = 0,
        edit = 0;
      for (let i = 0, len = item.fields.length; i < len; i++) {
        if (item.fields[i].configuration.defaultDisplayState == 'edit') {
          if (this.refDyformOriginFields[item.id][item.fields[i].configuration.code]) {
            // 根据原始配置修改必填性
            item.fields[i].configuration.required =
              this.refDyformOriginFields[item.id][item.fields[i].configuration.code].configuration.required;
          }
          edit++;
        } else {
          item.fields[i].configuration.required = false;
          unedit++;
        }
      }
      if (unedit == item.fields.length) {
        item.defaultDisplayState = 'unedit';
      }
      if (edit == item.fields.length) {
        item.defaultDisplayState = 'edit';
      }
    }
  }
};
</script>
