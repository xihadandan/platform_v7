<template>
  <a-form-model :model="form" layout="vertical" class="data-model-node-set-form pt-form">
    <a-form-model-item label="连接对象" v-if="relations.length == 2">
      <div>
        <a-tag color="blue" style="margin-right: 0px">{{ relations[0].name }} ( {{ relations[0].alias }} )</a-tag>
        <a-icon type="close" v-if="form.lineType == 'CROSS JOIN'" />
        <a-icon type="swap" v-else-if="form.lineType == 'INNER JOIN'" />
        <a-icon type="swap-right" v-else-if="form.lineType == 'LEFT JOIN'" />
        <a-icon type="swap-left" v-else />
        <a-tag color="blue">{{ relations[1].name }} ( {{ relations[1].alias }} )</a-tag>
      </div>
    </a-form-model-item>
    <a-form-model-item label="连接方式">
      <a-radio-group button-style="solid" v-model="form.lineType" @change="onChangeLineType">
        <a-radio-button value="CROSS JOIN">交叉连接</a-radio-button>
        <a-radio-button value="INNER JOIN">内连接</a-radio-button>
        <a-radio-button value="LEFT JOIN">左连接</a-radio-button>
        <a-radio-button value="RIGHT JOIN">右连接</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <WhereConditionSet
      v-if="form.lineType != 'CROSS JOIN'"
      :columns="onColumns"
      :condition="form.onConditions"
      title="连接条件"
      ref="onWhere"
      :supportVar="false"
      @change="e => onChange(e, 'onConditionSql')"
    />
    <WhereConditionSet
      :columns="columns"
      :condition="form.conditions"
      :alias="alias"
      ref="where"
      @change="e => onChange(e, 'conditionSql')"
    />
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import { propComparator } from './constant';
import { Graph } from '@antv/x6';
import WhereConditionSet from './where-condition-set.vue';
import md5 from '@framework/vue/utils/md5';

export default {
  name: 'DataModelLineSet',
  mixins: [],
  props: {
    nid: String,
    nodeData: Object,
    graph: Graph
  },
  components: { WhereConditionSet },
  data() {
    let form = { ...JSON.parse(JSON.stringify(this.nodeData)) };
    if (form.onConditions == undefined) {
      // 连接条件
      form.onConditions = [];
    }
    if (form.conditions == undefined) {
      // 数据过滤条件
      form.conditions = [];
    }
    if (form.lineType == undefined) {
      form.lineType = 'CROSS JOIN';
    }

    return {
      relations: [],
      labelCol: { span: 4 },
      wrapperCol: { span: 20 },
      form,
      alias: [],
      propComparator,
      columns: [],
      onColumns: [],
      hasError: false
    };
  },
  watch: {},
  beforeCreate() {},
  computed: {
    columnOptions() {
      let opt = [];
      if (this.columns.length) {
        for (let c of this.columns) {
          opt.push({
            tableAlias: c.tableAlias,
            label: c.title,
            value: c.alias // 取别名来做关联
          });
        }
      }
      return opt;
    },
    columnAliasMap() {
      let opt = {};
      if (this.columns.length) {
        for (let c of this.columns) {
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
        // 修改的数据
        if (this.form.conditions.length > 0 || this.form.onConditions.length > 0 || this.form.lineType != 'CROSS JOIN') {
          return true;
        }
      } else {
        let before = {
            lineType: this.nodeData.lineType,
            conditions: this.nodeData.conditions,
            onConditions: this.nodeData.onConditions
          },
          now = {
            lineType: this.form.lineType,
            conditions: this.form.conditions,
            onConditions: this.form.onConditions
          };
        return md5(JSON.stringify(before)) != md5(JSON.stringify(now));
      }
      return false;
    },
    onChange({ sql, sqlParameter }, key) {
      if (sql != undefined && sql.__proto__.name == 'Error') {
        this.hasError;
        return;
      }
      this.form[key] = key === 'onConditionSql' ? 'ON ' + sql : sql;
      this.form.sqlParameter = sqlParameter;
    },
    onChangeLineType() {
      let node = this.graph.getCellById(this.nid);

      node.setAttrs({
        line: {
          sourceMarker: { name: this.form.lineType == 'LEFT JOIN' ? '' : 'block' },
          targetMarker: { name: this.form.lineType == 'RIGHT JOIN' ? '' : 'block' }
        }
      });
      // console.log(node);
    },
    onSave() {
      return new Promise((resolve, reject) => {
        if (this.hasError) {
          return;
        }
        let node = this.graph.getDesigner().getSelectCell();
        let str = JSON.stringify(node.getData());
        let nodeData = JSON.parse(str);
        nodeData.alias = this.form.alias;
        // orignialData.onConditionSql = 'on ' + outsql.on.join(' ');
        // orignialData.conditionSql = outsql.where.
        if (this.form.lineType != 'CROSS JOIN') {
          let on = this.$refs.onWhere.collect();
          if (on.sql) {
            nodeData.onConditionSql = 'ON ' + on.sql;
          } else {
            this.$message.error('请添加连接条件');
            return;
          }
        }

        let where = this.$refs.where.collect();
        // nodeData.onConditionSql = this.form.onConditionSql;
        nodeData.conditionSql = where.sql;
        nodeData.sqlParameter = where.sqlParameter;
        nodeData.lineType = this.form.lineType;
        nodeData.conditions = this.form.conditions;
        nodeData.onConditions = this.form.onConditions;
        nodeData.timestamp = new Date().getTime();
        node.updateData(nodeData);
        resolve();
      });
    }
  },
  beforeMount() {
    let fromNode = this.graph.getCellById(this.nodeData.fromId),
      fromNodeData = fromNode.getData();
    let toNode = this.graph.getCellById(this.nodeData.toId),
      toNodeData = toNode.getData();
    console.log('from', fromNode);
    console.log('to', toNode);

    // console.log('from  -> to', fromNodeData.title, toNodeData.title);
    // this.columnOptions = [];
    this.relations.push({ name: fromNodeData.title, alias: fromNodeData.alias }, { name: toNodeData.title, alias: toNodeData.alias });
    this.alias.push(fromNodeData.alias, toNodeData.alias);
    let id = [fromNode.id, toNode.id];
    for (let i = 0, len = fromNodeData.columns.length; i < len; i++) {
      let col = fromNodeData.columns[i];
      let c = JSON.parse(JSON.stringify(col));
      c.title = fromNodeData.title + '.' + c.title;
      // c.alias = fromNodeData.alias + '.' + c.alias;
      // c.location = fromNodeData.alias + '.' + c.alias;
      // c.tableAlias = fromNodeData.alias;
      this.columns.push(c);
      this.onColumns.push(c);
      // this.columnOptions.push({
      //   label: fromNodeData.title + '.' + col.title,
      //   value: fromNodeData.alias + '.' + col.alias
      // });
    }
    for (let i = 0, len = toNodeData.columns.length; i < len; i++) {
      let col = toNodeData.columns[i];
      let c = JSON.parse(JSON.stringify(col));
      c.title = toNodeData.title + '.' + c.title;
      // c.location = toNodeData.alias + '.' + c.alias;
      // c.alias = toNodeData.alias + '.' + c.alias;
      // c.tableAlias = toNodeData.alias;
      this.columns.push(c);
      this.onColumns.push(c);
      // this.columnOptions.push({
      //   label: toNodeData.title + '.' + col.title,
      //   value: toNodeData.alias + '.' + col.alias
      // });
    }

    // 获取关联节点的列数据
    let cascadeRelateNodeColumns = nodes => {
      if (nodes != undefined) {
        let keys = Object.keys(nodes);
        for (let i = 0, len = keys.length; i < len; i++) {
          if (id.includes(keys[i])) {
            continue;
          }
          let n = this.graph.getCellById(keys[i]);
          if (n) {
            id.push(n.id);
            let _data = n.getData();
            if (_data && _data.columns) {
              for (let i = 0, len = _data.columns.length; i < len; i++) {
                let col = _data.columns[i];
                let c = JSON.parse(JSON.stringify(col));
                c.title = _data.title + '.' + c.title;
                this.columns.push(c);
              }
            }
            cascadeRelateNodeColumns.call(this, n._model.incomings);
            cascadeRelateNodeColumns.call(this, n._model.outgoings);
          }
        }
      }
    };
    cascadeRelateNodeColumns.call(this, fromNode._model.incomings);
    cascadeRelateNodeColumns.call(this, fromNode._model.outgoings);
    cascadeRelateNodeColumns.call(this, toNode._model.incomings);
    cascadeRelateNodeColumns.call(this, toNode._model.outgoings);
  },
  mounted() {},
  destroyed() {}
};
</script>
