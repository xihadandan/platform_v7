<template>
  <div>
    <a-form-model v-if="widget.configuration.dataBinding != undefined">
      <template v-if="underDyform">
        <a-form-model-item label="与主表同步保存">
          <a-switch v-model="widget.configuration.dataBinding.syncSaveWithMainForm" />
        </a-form-model-item>
        <a-form-model-item
          :label-col="{
            style: {
              width: '150px'
            }
          }"
          :wrapper-col="{
            style: {
              width: 'calc(100% - 150px)'
            }
          }"
          label="保存时更新关联字段"
          v-if="widget.configuration.dataBinding.syncSaveWithMainForm"
        >
          <a-select
            :style="{ width: 'calc(50% - 20px)' }"
            v-model="widget.configuration.dataBinding.syncFieldValue.mainFormField"
            @change="validateSyncUpdateCorrect"
          >
            <template v-for="(col, c) in dyformDesignFieldOptions">
              <a-select-option :key="col.code" :value="col.code">
                {{ col.label }}
                <a-tag style="position: absolute; right: 0px; top: 4px">
                  {{ col.code.toUpperCase() }}
                </a-tag>
              </a-select-option>
            </template>
          </a-select>
          <a-button
            size="small"
            :icon="widget.configuration.dataBinding.syncFieldValue.syncToCurrentForm ? 'double-right' : 'double-left'"
            type="link"
            @click="onChangeSyncToDirection"
          />
          <a-select
            :style="{ width: 'calc(50% - 20px)' }"
            v-model="widget.configuration.dataBinding.syncFieldValue.currentFormField"
            @change="validateSyncUpdateCorrect"
          >
            <a-select-option value="uuid">
              UUID
              <a-tag style="position: absolute; right: 0px; top: 4px">UUID</a-tag>
            </a-select-option>
            <template v-for="(field, f) in formFieldOptions">
              <a-select-option :value="field.value">
                {{ field.label }}
                <a-tag style="position: absolute; right: 0px; top: 4px">
                  {{ field.value.toUpperCase() }}
                </a-tag>
              </a-select-option>
            </template>
          </a-select>
        </a-form-model-item>
      </template>
      <a-form-model-item label="数据筛选">
        <a-radio-group v-model="widget.configuration.dataBinding.type" button-style="solid" size="small">
          <a-radio-button value="query">条件查询</a-radio-button>
          <a-radio-button value="useDataUuid">指定数据UUID</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item v-if="widget.configuration.dataBinding.type == 'useDataUuid'" :wrapper-col="{ style: { width: '100%' } }">
        <FormulaEditor
          :widget="widget"
          :bind-to-configuration="widget.configuration.dataBinding"
          configKey="dataUuidExpr"
          ref="formulaEditor"
          :supportVariableTypes="['dyform']"
          :enableFormulaFunction="false"
        />
      </a-form-model-item>
      <a-form-model-item v-if="widget.configuration.dataBinding.type == 'query'" :wrapper-col="{ style: { width: '100%' } }">
        <a-alert banner type="info" style="margin-bottom: 8px">
          <template slot="message">
            <div style="display: flex; justify-content: space-between">
              <label>请确保条件查询能筛选出数据, 多笔数据将取最早创建时间的数据</label>
              <a-checkbox v-model="widget.configuration.dataBinding.underCurrentSystem">当前系统下</a-checkbox>
            </div>
          </template>
        </a-alert>
        <div class="dyform-condition-form-item">
          <template v-for="(con, i) in widget.configuration.dataBinding.condition">
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
                  <a-switch class="conType" v-model="con.sqlWord" size="small" checked-children="SQL" un-checked-children="SQL" />
                  <a-popconfirm
                    placement="left"
                    :arrowPointAtCenter="true"
                    title="确认要删除吗?"
                    ok-text="删除"
                    cancel-text="取消"
                    @confirm="onRemoveCondition(i, widget.configuration.dataBinding.condition)"
                  >
                    <a-button type="link" class="delete-condition" icon="delete"></a-button>
                  </a-popconfirm>
                </div>
                <template v-for="(b, i) in con.leftBracket">
                  <a-tag :key="'leftB_' + i" closable color="blue" @close="onRemoveBracket(con, 'leftBracket', i)">(</a-tag>
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
                  <a-select :style="{ width: '100%' }" v-model="con.prop" style="width: 100%">
                    <a-select-option value="UUID">
                      UUID
                      <a-tag style="position: absolute; right: 0px; top: 4px">UUID</a-tag>
                    </a-select-option>
                    <template v-for="(field, f) in formFieldOptions">
                      <a-select-option :value="field.value">
                        {{ field.label }}
                        <a-tag style="position: absolute; right: 0px; top: 4px">
                          {{ field.value.toUpperCase() }}
                        </a-tag>
                      </a-select-option>
                    </template>
                  </a-select>
                  <a-select v-model="con.sign" style="width: 100%" @change="onChangeConSign(con)">
                    <template v-for="(opt, i) in getPropComparator(con, formFieldOptions)">
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
                        style="width: 30%"
                        :options="valueOptionType"
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
                        v-show="con.valueType == 'prop'"
                        v-model="con.value"
                        @change="onPropChange(con, 'value')"
                        :style="{
                          width: '70%',
                          outline: con.error && con.error.value == false ? '2px solid red' : 'unset'
                        }"
                      >
                        <template v-for="(col, c) in dyformDesignFieldOptions">
                          <a-select-option :key="col.value" :value="col.value">
                            {{ col.label }}
                            <a-tag style="position: absolute; right: 0px; top: 4px">
                              {{ col.code.toUpperCase() }}
                            </a-tag>
                          </a-select-option>
                        </template>
                        <template v-for="(field, f) in formFieldOptions">
                          <a-select-option :value="field.value">
                            {{ field.label }}
                            <a-tag style="position: absolute; right: 0px; top: 4px">
                              {{ field.value.toUpperCase() }}
                            </a-tag>
                          </a-select-option>
                        </template>
                      </a-select>
                    </a-input-group>
                  </template>
                </template>
                <template v-for="(b, i) in con.rightBracket">
                  <a-tag :key="'rightB_' + i" closable color="blue" @close="onRemoveBracket(con, 'rightBracket', i)">)</a-tag>
                </template>
                <a-tag style="background: #fff; border-style: dashed" @click="addBracket('rightBracket', con, ')')">
                  <a-icon type="plus" />
                  添加右括号
                </a-tag>
              </div>
            </template>
          </template>
        </div>
        <a-button style="margin-top: 12px" @click="addCondition(item)" type="default" size="small" icon="plus" block>添加条件</a-button>
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<style lang="less">
.dyform-condition-form-item {
  line-height: 40px;

  .prop-condition {
    background: #f2f2f2;
    border-radius: 4px;
    padding: 10px;
    position: relative;

    .toolbar {
      position: absolute;
      top: 0;
      right: 0;
    }
  }
}
</style>
<script type="text/babel">
export default {
  name: 'DyformDataBindingConfiguration',
  inject: ['formFieldOptions', 'dyform'],
  props: {
    widget: Object,
    designer: Object
  },
  components: {},
  computed: {
    dyformDesignFieldOptions() {
      if (this.dyform != undefined) {
        let opt = [
          {
            label: 'UUID',
            value: ':MAIN_FORM_DATA_UUID',
            code: 'UUID'
          }
        ];
        if (this.designer.SimpleFieldInfos != undefined) {
          for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
            let info = this.designer.SimpleFieldInfos[i];
            opt.push({
              label: info.name,
              value: ':MAIN_FORM_DATA_' + info.code,
              code: info.code
            });
          }
        }
        return opt;
      }
      return [];
    },
    valueOptionType() {
      let opt = [
        { label: '常量', value: 'constant' },
        { label: '变量', value: 'var' }
      ];
      if (this.dyform != undefined) {
        opt.push({ label: '表单字段', value: 'prop' });
      }
      return opt;
    }
  },
  data() {
    return {
      underDyform: this.dyform != undefined,
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
      ]
    };
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('dataBinding')) {
      this.$set(this.widget.configuration, 'dataBinding', {
        type: 'query',
        underCurrentSystem: true,
        condition: [],
        syncSaveWithMainForm: false,
        syncFieldValue: {
          mainFormField: undefined,
          currentFormField: undefined,
          syncToCurrentForm: true
        }
      });
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onChangeSyncToDirection() {
      let syncFieldValue = this.widget.configuration.dataBinding.syncFieldValue;
      syncFieldValue.syncToCurrentForm = !syncFieldValue.syncToCurrentForm;
      this.validateSyncUpdateCorrect();
    },
    validateSyncUpdateCorrect() {
      let syncFieldValue = this.widget.configuration.dataBinding.syncFieldValue;
      let { syncToCurrentForm, mainFormField, currentFormField } = syncFieldValue;
      if (syncToCurrentForm && currentFormField && currentFormField.toLowerCase() == 'uuid') {
        this.$message.error('不可更新 UUID 字段');
        syncFieldValue.currentFormField = undefined;
      } else if (!syncToCurrentForm && mainFormField && mainFormField.toLowerCase() == 'uuid') {
        this.$message.error('不可更新 UUID 字段');
        syncFieldValue.mainFormField = undefined;
      }
    },
    onBlurInputConValue(con, i) {
      let key = i == undefined ? 'value' : 'value2';
      if (con[key] != undefined && con.error && con.error[key] == false) {
        con.error[key] = true;
      }
    },
    onSelectPropValType(con, prop) {
      con[prop] = undefined;
    },
    getPropComparator(con, fields) {
      let f = undefined,
        isClob = false;
      if (con.prop) {
        for (let i = 0, len = fields.length; i < len; i++) {
          if (fields[i].value == con.prop) {
            isClob = fields[i].dbDataType == '16';
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
    onChangeConSign(con) {
      if (con.sign == 'between' && con.valueType2 == undefined) {
        con.valueType2 = 'constant';
      }
    },
    onRemoveBracket(obj, key, i) {
      obj[key].splice(i, 1);
    },
    addBracket(key, obj, v) {
      obj[key].push(v);
    },
    onRemoveCondition(i, condition) {
      let len = condition.length;
      if (i == len - 1 && len > 2) {
        condition.splice(len - 2, 2);
      } else {
        condition.splice(i, 2); // 移除条件
      }
    },
    addCondition() {
      let condition = this.widget.configuration.dataBinding.condition;
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
    }
  }
};
</script>
