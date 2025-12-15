<template>
  <a-form-model :model="form" layout="vertical" class="data-model-node-set-form pt-form">
    <a-form-model-item label="别名">
      <a-tag>{{ form.alias }}</a-tag>
    </a-form-model-item>
    <a-form-model-item label="返回字段">
      <a-select mode="multiple" allow-clear :options="columnOptions" v-model="form.returnCols" @change="onChangeReturnCol"></a-select>
    </a-form-model-item>
    <WhereConditionSet :condition="form.conditions" :alias="form.alias" :columns="form.columns" @change="onChange" ref="where" />
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import { Graph } from '@antv/x6';
import md5 from '@framework/vue/utils/md5';
import { propComparator } from './constant';
import WhereConditionSet from './where-condition-set.vue';
export default {
  name: 'DataModelNodeSet',
  mixins: [],
  props: {
    nid: String,
    nodeData: Object,
    graph: Graph
  },
  data() {
    let form = { ...JSON.parse(JSON.stringify(this.nodeData)) },
      returnColCount = 0;
    if (form.returnCols == undefined) {
      form.returnCols = [];
      for (let i = 0, len = form.columns.length; i < len; i++) {
        if (form.columns[i].return) {
          form.returnCols.push(form.columns[i].alias);
        }
      }
      returnColCount = form.returnCols.length;
    }

    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 20 },
      form,
      propComparator,
      returnColCount
    };
  },
  components: { WhereConditionSet },
  beforeCreate() {},
  computed: {
    columnOptions() {
      let opt = [];
      if (this.nodeData != undefined && this.nodeData.columns != undefined) {
        for (let c of this.nodeData.columns) {
          opt.push({
            tableAlias: this.nodeData.alias,
            label: c.title,
            value: c.alias // 取别名来做关联
          });
        }
      }
      return opt;
    },
    columnAliasMap() {
      let opt = {};
      if (this.nodeData != undefined && this.nodeData.columns != undefined) {
        for (let c of this.nodeData.columns) {
          opt[c.alias] = c;
        }
      }
      return opt;
    }
  },
  created() {},
  methods: {
    dataChanged() {
      if (this.nodeData.timestamp == undefined) {
        //  修改的数据
        if (this.form.returnCols.length != this.returnColCount || this.form.conditions.length > 0) {
          return true;
        }
      } else {
        let before = {
            returnCols: this.nodeData.returnCols,
            conditions: this.nodeData.conditions
          },
          now = {
            returnCols: this.form.returnCols,
            conditions: this.form.conditions
          };
        return md5(JSON.stringify(before)) != md5(JSON.stringify(now));
      }
      return false;
    },
    onChangeReturnCol(value) {
      for (let c of this.nodeData.columns) {
        c.return = this.form.returnCols.includes(c.alias);
      }
    },
    onChange({ sql, sqlParameter }) {
      this.form.conditionSql = sql;
      this.form.sqlParameter = sqlParameter;
    },
    onSave() {
      let node = this.graph.getDesigner().getSelectCell();
      let nodeData = JSON.parse(JSON.stringify(node.getData()));
      nodeData.conditions = this.form.conditions;
      nodeData.alias = this.form.alias;
      let { sql, sqlParameter } = this.$refs.where.collect();
      nodeData.conditionSql = sql;
      nodeData.sqlParameter = sqlParameter;
      nodeData.columns = this.nodeData.columns;
      nodeData.timestamp = new Date().getTime();
      nodeData.returnCols = this.form.returnCols;
      node.updateData(nodeData);

      // 触发父级列变更
      if (node.parent != undefined) {
        node.parent.trigger('change:resetColumns', node.parent);
      }

      // let orignialData = JSON.parse(JSON.stringify(node.getData()));
      // orignialData.conditions = this.form.conditions;
      // orignialData.alias = this.form.alias;

      // let where = [],
      //   leftBracketCnt = 0,
      //   rightBracketCnt = 0,
      //   namedParameter = {};
      // for (let con of orignialData.conditions) {
      //   if (['and', 'or'].includes(con.sign)) {
      //     where.push(con.sign);
      //   } else {
      //     leftBracketCnt += con.leftBracket.length;
      //     where.push(con.leftBracket.join(''));
      //     if (con.sqlWord) {
      //       where.push(con.sql);
      //     } else {
      //       let namedPkey = null,
      //         conValue = con.value;
      //       if (con.valueType == 'constant') {
      //         if (this.columnAliasMap[con.prop]) {
      //           let dataType = this.columnAliasMap[con.prop].dataType;
      //           if (dataType == 'varchar') {
      //             conValue = `'${conValue}'`;
      //           } else if (dataType == 'number') {
      //             conValue = Number(conValue);
      //           } else if (dataType == 'timestamp') {
      //             // 日期需要格式由后端通过命名参数处理
      //             namedPkey = orignialData.alias + '_' + con.prop;
      //             namedParameter[namedPkey] = { dataType, value: con.value };
      //           }
      //         }
      //       }

      //       where.push(
      //         `${orignialData.alias}.${con.prop}${con.sign}${
      //           con.valueType == 'prop' ? `${orignialData.alias}.${con.value}` : namedPkey != null ? `:${namedPkey}` : conValue
      //         }`
      //       );
      //     }

      //     rightBracketCnt += con.rightBracket.length;
      //     where.push(con.rightBracket.join(''));
      //   }
      // }

      // if (leftBracketCnt != rightBracketCnt) {
      //   this.$message.error('左右括号未闭合');
      //   throw new Error('括号错误');
      // }
      // orignialData.conditionSql = where.length ? '(' + where.join(' ') + ')' : '';
      // orignialData.namedParameter = namedParameter;
      // node.updateData(orignialData);
      // console.log('sql condition is : ', where.join(' '));
    }
  },
  beforeMount() {},
  mounted() {},
  destroyed() {}
};
</script>
