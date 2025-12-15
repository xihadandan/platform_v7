<template>
  <a-form-model-item :label="title" class="data-model-condition-form-item">
    <template v-for="(con, i) in condition">
      <template v-if="con.sign == 'AND' || con.sign == 'OR'">
        <div style="text-align: center; padding: 8px 0">
          <a-select
            :dropdownClassName="getDropdownClassName()"
            :getPopupContainer="getPopupContainer()"
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
            <a-switch class="conType" v-model="con.sqlWord" checked-children="SQL" un-checked-children="SQL" />
            <a-popconfirm
              placement="left"
              :arrowPointAtCenter="true"
              title="确认要删除吗?"
              ok-text="删除"
              cancel-text="取消"
              @confirm="onRemoveCondition(i)"
            >
              <a-button type="link" class="delete-condition" icon="delete"></a-button>
            </a-popconfirm>
          </div>
          <template v-for="(b, i) in con.leftBracket">
            <a-tag :key="'leftB_' + i" @close="" closable color="blue" @close="onRemoveBracket(con, 'leftBracket', i)">(</a-tag>
          </template>
          <a-tag style="background: #fff; border-style: dashed" @click="addBracket('leftBracket', con, '(')">
            <a-icon type="plus" />
            添加左括号
          </a-tag>

          <template v-if="con.sqlWord === true">
            <div :style="con.error && con.error.sqlWord == false ? { outline: '2px solid red' } : {}">
              <WidgetCodeEditor lang="sql" v-model="con.sql" width="440px" height="100px" :snippets="sqlSnippets" />
            </div>
          </template>
          <template v-else>
            <a-select
              :style="{ width: '100%', outline: con.error && con.error.prop == false ? '2px solid red' : 'unset' }"
              :options="columnOptions"
              v-model="con.prop"
              style="width: 100%"
              :dropdownClassName="getDropdownClassName()"
              :getPopupContainer="getPopupContainer()"
              @change="onPropChange(con, 'prop')"
            />

            <a-select
              v-model="con.sign"
              style="width: 100%"
              @change="onChangeConSign(con)"
              :dropdownClassName="getDropdownClassName()"
              :getPopupContainer="getPopupContainer()"
            >
              <template v-for="(opt, i) in getPropComparator(con)">
                <a-select-option :value="opt.value" :key="'con_' + i">
                  <!-- <div style="display: inline-flex; width: 300px; justify-content: space-between; align-items: center">
                    {{ opt.label }}
                    <a-tag>{{ opt.value }}</a-tag>
                  </div> -->
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
                  :dropdownClassName="getDropdownClassName()"
                  :getPopupContainer="getPopupContainer()"
                  :options="
                    supportVar
                      ? [
                          { label: '属性', value: 'prop' },
                          { label: '常量', value: 'constant' },
                          { label: '变量', value: 'var' }
                        ]
                      : [
                          { label: '属性', value: 'prop' },
                          { label: '常量', value: 'constant' }
                        ]
                  "
                  v-model="con.valueType"
                  @change="onSelectPropValType(con, 'value')"
                />
                <a-input
                  v-show="con.valueType == 'constant'"
                  v-model.trim="con.value"
                  style="width: 70%"
                  :style="{ outline: con.error && con.error.value == false ? '2px solid red' : 'unset' }"
                  @blur="onBlurInputConValue(con)"
                />
                <a-input
                  v-show="con.valueType == 'var'"
                  v-model="con.value"
                  style="width: 70%"
                  :placement="con.dataType == 'timestamp' ? '日期格式: 2000-01-01 12:00:00' : ''"
                  @blur="onBlurInputConValue(con)"
                  prefix=":"
                  :style="{ outline: con.error && con.error.value == false ? '2px solid red' : 'unset' }"
                >
                  <a-popover slot="suffix" placement="topRight">
                    <template slot="title">运行时不存在该变量, 则该数据过滤将被放弃</template>
                    <template slot="content">
                      <p>默认支持以下内置系统变量:</p>
                      <ul>
                        <li>sysdate : 当前系统时间</li>
                        <li>currentSystem : 当前系统ID</li>
                        <li>currentTenantId : 当前登录用户</li>
                        <li>currentUserId : 当前用户ID</li>
                        <li>currentLoginName : 当前登录用户</li>
                      </ul>
                    </template>
                    <a-icon type="info-circle" style="color: rgba(0, 0, 0, 0.45)" />
                  </a-popover>
                  <!-- <a-tooltip slot="suffix" title="运行时不存在该变量, 则该数据过滤将被放弃">
                    <a-icon type="info-circle" style="color: rgba(0, 0, 0, 0.45)" />
                  </a-tooltip> -->
                </a-input>
                <a-select
                  :dropdownClassName="getDropdownClassName()"
                  :getPopupContainer="getPopupContainer()"
                  v-show="con.valueType == 'prop'"
                  :options="columnOptions"
                  v-model="con.value"
                  @change="onPropChange(con, 'value')"
                  :style="{ width: '70%', outline: con.error && con.error.value == false ? '2px solid red' : 'unset' }"
                />
              </a-input-group>
              <template v-if="con.sign == 'between'">
                <div style="text-align: center"><a-tag>AND</a-tag></div>
                <a-input-group compact>
                  <a-select
                    :dropdownClassName="getDropdownClassName()"
                    :getPopupContainer="getPopupContainer()"
                    style="width: 30%"
                    :options="
                      supportVar
                        ? [
                            { label: '属性', value: 'prop' },
                            { label: '常量', value: 'constant' },
                            { label: '变量', value: 'var' }
                          ]
                        : [
                            { label: '属性', value: 'prop' },
                            { label: '常量', value: 'constant' }
                          ]
                    "
                    v-model="con.valueType2"
                    @change="onSelectPropValType(con, 'value2')"
                  />
                  <a-input
                    v-show="con.valueType2 == 'constant'"
                    v-model.trim="con.value2"
                    style="width: 70%"
                    :style="{ outline: con.error && con.error.value2 == false ? '2px solid red' : 'unset' }"
                    @blur="onBlurInputConValue(con, '2')"
                  />
                  <a-input
                    v-show="con.valueType2 == 'var'"
                    v-model="con.value2"
                    style="width: 70%"
                    :placement="con.dataType == 'timestamp' ? '日期格式: 2000-01-01 12:00:00' : ''"
                    @blur="onBlurInputConValue(con, '2')"
                    prefix=":"
                    :style="{ outline: con.error && con.error.value2 == false ? '2px solid red' : 'unset' }"
                  >
                    <a-tooltip slot="suffix" title="运行时不存在该变量, 则该数据过滤将被放弃">
                      <a-icon type="info-circle" style="color: rgba(0, 0, 0, 0.45)" />
                    </a-tooltip>
                  </a-input>
                  <a-select
                    :dropdownClassName="getDropdownClassName()"
                    :getPopupContainer="getPopupContainer()"
                    v-show="con.valueType2 == 'prop'"
                    :options="columnOptions"
                    v-model="con.value2"
                    @change="onPropChange(con, 'value2')"
                    :style="{ width: '70%', outline: con.error && con.error.value2 == false ? '2px solid red' : 'unset' }"
                  />
                </a-input-group>
              </template>
            </template>
          </template>
          <template v-for="(b, i) in con.rightBracket">
            <a-tag :key="'rightB_' + i" @close="" closable color="blue" @close="onRemoveBracket(con, 'rightBracket', i)">)</a-tag>
          </template>
          <a-tag style="background: #fff; border-style: dashed" @click="addBracket('rightBracket', con, ')')">
            <a-icon type="plus" />
            添加右括号
          </a-tag>
        </div>
      </template>
    </template>
    <a-button @click="addCondition" type="link" icon="plus">添加{{ title }}</a-button>
  </a-form-model-item>
</template>
<style lang="less">
.data-model-condition-form-item {
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
import WidgetCodeEditor from '@pageAssembly/app/web/widget/commons/widget-code-editor.vue';
import { getDropdownClassName } from '@framework/vue/utils/function.js';

import { propComparator } from './constant';
export default {
  name: 'WhereConditionSet',
  mixins: [],
  props: {
    condition: Array,
    columns: Array,
    // columnOptions: Array,
    title: {
      type: String,
      default: '数据过滤'
    },
    alias: String | Array,
    supportVar: {
      type: Boolean,
      default: true
    }
  },
  components: { WidgetCodeEditor },
  data() {
    return {
      propComparator
    };
  },
  watch: {},
  beforeCreate() {},
  computed: {
    columnOptions() {
      let options = [];
      if (this.columns != undefined) {
        for (let col of this.columns) {
          options.push({
            label: col.title,
            value: col.location,
            dataType: col.dataType
          });
        }
      }
      return options;
    },
    sqlSnippets() {
      // let aliasColMap = {};
      // for (let col of this.columnOptions) {
      //   if (aliasColMap[col.tableAlias] == undefined) {
      //     aliasColMap[col.tableAlias] = [];
      //   }
      //   aliasColMap[col.tableAlias].push(col);
      // }

      // let alias = typeof this.alias === 'string' ? [this.alias] : this.alias;
      // let snippets = [];
      // if (alias) {
      //   for (let a of alias) {
      //     let cols = aliasColMap[a];
      //     if (cols) {
      //       for (let c of cols) {
      //         snippets.push({ name: `${c.value} : ${c.label}`, content: c.value.indexOf('.') == -1 ? `${a}.${c.value}` : `${c.value}` });
      //       }
      //     }
      //   }
      // }
      let snippets = [];
      if (this.columns != undefined) {
        for (let c of this.columns) {
          snippets.push({ name: `${c.column} : ${c.title}`, content: c.location });
        }
      }
      return snippets;
    },
    columnLocationMap() {
      let map = {};
      if (this.columns != undefined) {
        for (let c of this.columns) {
          map[c.location] = c;
        }
      }
      return map;
    }
  },
  created() {},
  methods: {
    getDropdownClassName,
    getPopupContainer() {
      return triggerNode => {
        return this.$el;
      };
    },
    onChangeConSign(con) {
      if (con.sign == 'between' && con.valueType2 == undefined) {
        con.valueType2 = 'constant';
      }
    },
    getPropComparator(con) {
      if (con.prop && this.columnLocationMap[con.prop] && this.columnLocationMap[con.prop].dataType == 'clob') {
        // 大字段不支持以下比较符号
        let opt = [];
        for (let i = 0, len = this.propComparator.length; i < len; i++) {
          if (['like', 'not like', 'is null', 'is not null'].includes(this.propComparator[i].value)) {
            opt.push(this.propComparator[i]);
          }
        }
        return opt;
      }
      return this.propComparator;
    },
    collect() {
      return this.conditionSql();
    },
    onBlurInputConValue(con, i) {
      let key = i == undefined ? 'value' : 'value2';
      if (con[key] != undefined && con.error && con.error[key] == false) {
        con.error[key] = true;
      }
    },
    onPropChange(con, name) {
      let aProp = this.columnLocationMap[con[name]],
        aDataType = aProp ? aProp.dataType : null,
        bProp = this.columnLocationMap[con[name == 'prop' ? 'value' : 'prop']],
        bDataType = bProp ? bProp.dataType : null;
      if (aDataType != null && bDataType != null && aDataType != bDataType) {
        this.$message.error('属性类型不一致');
        con[name] = undefined;
      }
      if (con[name] != undefined && con.error && con.error[name] == false) {
        con.error[name] = true;
      }
      if (name == 'prop' && con.prop != undefined && aDataType == 'clob') {
        con.sign = 'like';
      }
      if (name == 'prop' && con.prop != undefined) {
        con.dataType = aDataType;
      }
    },
    conditionSql() {
      let where = [],
        leftBracketCnt = 0,
        rightBracketCnt = 0,
        namedParameter = {};
      let hasError = false;
      for (let con of this.condition) {
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
            let dataType = this.columnLocationMap[con.prop].dataType;

            let formatValue = (value, valueType) => {
              let conValue = value;
              if (valueType == 'constant') {
                // 常量情况下，直接拼接，如果是日期类，则转为变量由后端格式化传入
                if (this.columnLocationMap[con.prop]) {
                  if (dataType == 'varchar') {
                    conValue = `'${value}'`;
                  } else if (dataType == 'number') {
                    conValue = Number(value);
                  } else if (dataType == 'timestamp') {
                    // 日期需要格式化函数（针对不同数据库，再实际解析时候由后端处理
                    conValue = `TO_DATE('${value}','YYYY-MM-DD HH24:MI:SS')`;
                  }
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
        this.$emit('change', { sql: new Error('左右括号未闭合') });
        throw new Error('括号错误');
      }
      if (hasError) {
        this.$message.error('请填写完整条件');
        this.$emit('change', { sql: new Error('左右括号未闭合') });
        throw new Error('填写不完整');
      }
      let data = { sql: where.length ? where.join(' ') : '', sqlParameter: namedParameter };
      this.$emit('change', data);
      return data;
    },
    onSelectPropValType(con, prop) {
      con[prop] = undefined;
    },
    // onChangePropValue(con){
    //   if(con.valueType==='constant'){
    //    }
    // },

    onRemoveCondition(i) {
      let len = this.condition.length;
      if (i == len - 1 && len > 2) {
        this.condition.splice(len - 2, 2);
      } else {
        this.condition.splice(i, 2); // 移除条件
      }
    },
    onRemoveBracket(obj, key, i) {
      obj[key].splice(i, 1);
    },
    addBracket(key, obj, v) {
      obj[key].push(v);
    },
    addCondition() {
      if (this.condition.length >= 1) {
        this.condition.push({
          sign: 'AND'
        });
      }

      this.condition.push({
        prop: undefined,
        sign: '=',
        valueType: 'constant',
        value: undefined,
        leftBracket: [],
        rightBracket: []
      });
    }
  },
  beforeMount() {},
  mounted() {},
  destroyed() {},
  watch: {
    condition: {
      deep: true,
      handler(v) {
        console.log('条件变更: ', v);
        // this.conditionSql();
      }
    }
  }
};
</script>
