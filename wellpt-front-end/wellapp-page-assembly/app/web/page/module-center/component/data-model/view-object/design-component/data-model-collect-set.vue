<template>
  <a-form-model :model="form" class="data-model-node-set-form pt-form" layout="vertical">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="基本信息">
        <a-form-model-item label="名称">
          <a-input v-model="form.title" />
        </a-form-model-item>
        <a-form-model-item label="别名">
          <a-tag>{{ form.alias }}</a-tag>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" :tab="isUnion ? '合并属性' : '集合属性'">
        <a-table
          rowKey="location"
          :pagination="false"
          :bordered="false"
          :columns="isUnion ? mergeColumnDefine : columnDefine"
          :data-source="form.columns"
          class="pt-table"
        >
          <template slot="titleSlot" slot-scope="text, record">
            <a-input v-model="record.title" />
          </template>
          <template slot="dataTypeSlot" slot-scope="text, record">
            <template v-if="text == 'varchar'">字符</template>
            <template v-else-if="text == 'number'">数字</template>
            <template v-else-if="text == 'timestamp'">日期时间</template>
            <template v-else-if="text == 'clob'">大字段</template>
          </template>
          <template slot="columnSlot" slot-scope="text, record">
            <a-tag color="blue">{{ text }}</a-tag>
          </template>
          <template slot="returnSlot" slot-scope="text, record, index">
            <a-switch v-model="record.return" size="small" />
          </template>
          <template slot="footer" v-if="isUnion">
            <div style="text-align: right">
              <a-button size="small" type="link" @click="clearAll" v-show="form.columns.length > 0">清空属性</a-button>
              <a-button size="small" type="link" @click="openEditMergePropModal">编辑属性</a-button>
            </div>
          </template>
        </a-table>
        <a-form-model-item label="数据去重" v-if="isUnion">
          <a-radio-group button-style="solid" v-model="form.unionType" size="small">
            <a-radio-button value="UNION ALL">不去重</a-radio-button>
            <a-radio-button value="UNION">去重</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="3" tab="数据过滤">
        <WhereConditionSet :columns="form.columns" :condition="form.conditions" ref="where" />
      </a-tab-pane>
    </a-tabs>
    <a-modal v-model="editPropModalVisible" title="合并属性" @ok="confirmMergePropOk" :destroyOnClose="true" :width="800">
      <div style="overflow-x: hidden; overflow-y: auto; height: 520px">
        <a-table
          :pagination="false"
          :bordered="false"
          :columns="editMergeColumnDefine"
          :data-source="tempColumns"
          :scroll="{ x: 700, y: 400 }"
          class="pt-table"
        >
          <template slot="titleSlot" slot-scope="text, record, index">
            <a-button type="link" size="small" @click="onDeleteTempCol(index)" title="删除">
              <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
            </a-button>
            <a-input v-model="record.title" style="width: 130px" />
          </template>
          <template v-for="(slot, i) in mergeColumnSlots" :slot="slot.id" slot-scope="text, record, index">
            <a-select
              :key="'slotColumn' + i"
              :options="slot.columnOptions"
              style="width: 100%"
              v-model="record[slot.id]"
              allow-clear
              @change="onChangeMergeCol(record, slot, i, mergeColumnSlots, index)"
            ></a-select>
          </template>
          <template slot="footer">
            <div style="text-align: right">
              <a-button type="link" @click="addMergeProp">添加属性</a-button>
            </div>
          </template>
        </a-table>
      </div>
    </a-modal>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import { Graph } from '@antv/x6';
import { propComparator } from './constant';
import WhereConditionSet from './where-condition-set.vue';
import md5 from '@framework/vue/utils/md5';
export default {
  name: 'DataModelCollectSet',
  inject: ['viewColumns'],
  mixins: [],
  props: {
    nid: String,
    nodeData: Object,
    graph: Graph
  },
  data() {
    let form = { ...JSON.parse(JSON.stringify(this.nodeData)) };
    // 同步视图属性的名称到设置内
    if (this.viewColumns != undefined && this.viewColumns.length > 0) {
      let columns = form.columns,
        colMap = {};
      for (let c of columns) {
        colMap[c.uuid] = c;
      }
      for (let c of this.viewColumns) {
        if (colMap[c.uuid]) {
          colMap[c.uuid].title = c.title;
        }
      }
    }
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 20 },
      form,
      isUnion: form.unionType != undefined,
      propComparator,
      editPropModalVisible: false,
      tempColumns: [],
      childrenDataMap: {},
      mergeColumnSlots: [],
      columnDefine: [
        { title: '显示名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        // { title: '别名', dataIndex: 'alias' },
        { title: '类型', dataIndex: 'dataType', scopedSlots: { customRender: 'dataTypeSlot' } },
        { title: '源属性', dataIndex: 'fullTitle', scopedSlots: { customRender: 'columnSlot' } },
        // { title: '描述', dataIndex: 'remark' },
        { title: '是否返回', dataIndex: 'return', scopedSlots: { customRender: 'returnSlot' } }
      ],
      mergeColumnDefine: [
        { title: '显示名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '类型', dataIndex: 'dataType', scopedSlots: { customRender: 'dataTypeSlot' } }
      ],
      editMergeColumnDefine: []
    };
  },
  watch: {},
  beforeCreate() {},
  components: { WhereConditionSet },
  computed: {},
  created() {},
  methods: {
    onChangeReturn(ck, record) {
      // record.noReturn = !ck;
      this.$set(record, 'noReturn', !ck);
    },
    onDeleteTempCol(i) {
      this.tempColumns.splice(i, 1);
    },
    onChangeMergeCol(record, slot, i, mergeColumnSlots, rowIndex) {
      let alias = record[slot.id];
      if (alias != undefined) {
        let dataType = this.columnMap[slot.id][alias].dataType;
        // 判断类型是否一致
        for (let m of mergeColumnSlots) {
          if (m.id != slot.id && record[m.id] && this.columnMap[m.id][record[m.id]].dataType != dataType) {
            this.$message.error('类型不一致');
            record[slot.id] = undefined;
            break;
          }
        }
      }
    },
    addMergeProp() {
      let mergeObj = {};
      for (let s of this.mergeColumnSlots) {
        mergeObj[s.id] = undefined;
      }
      this.tempColumns.push({
        alias: undefined,
        title: undefined,
        ...mergeObj
      });
    },
    clearAll() {
      this.form.columns.splice(0, this.form.columns.length);
      this.tempColumns.splice(0, this.tempColumns.length);
    },
    confirmMergePropOk() {
      let node = this.graph.getCellById(this.nid);
      let columns = [];
      let mergeObjCols = {};
      for (let t of this.tempColumns) {
        let alias = undefined,
          dataType = undefined;
        for (let m of this.mergeColumnSlots) {
          if (mergeObjCols[m.id] == undefined) {
            mergeObjCols[m.id] = [];
          }
          if (mergeObjCols[m.id].includes(t[m.id])) {
            // 一个对象的列重复使用的情况下，要对重复列使用别名处理
            let _a = null,
              j = 2;
            while (_a == null) {
              let _t = t[m.id] + '_' + j++;
              if (!mergeObjCols[m.id].includes(_t)) {
                _a = _t;
              }
            }
            t[m.id + ':alias'] = _a;
            mergeObjCols[m.id].push(_a);
          } else {
            mergeObjCols[m.id].push(t[m.id]);
          }
          // 别名、类型取合并对象出现第一个不为空的列
          if (alias == undefined && t[m.id] != undefined) {
            alias = t[m.id + ':alias'] || t[m.id];
            dataType = this.columnMap[m.id][t[m.id]].dataType;
          }
        }
        let nodeAlias = node.getData().alias;
        columns.push({
          title: t.title,
          alias,
          dataType,
          location: `${nodeAlias}.${alias}`,
          column: alias,
          return: true
        });
      }
      this.form.columns = columns;
      node.updateData({ columns, mergeColumns: JSON.parse(JSON.stringify(this.tempColumns)) });
      this.editPropModalVisible = false;
    },
    openEditMergePropModal() {
      this.editPropModalVisible = true;
      // this.initMergeDefinitionData();
    },
    getColumnSource(record) {
      let nid = record.nid;
      let node = this.graph.getCellById(nid);
      if (node) {
        let { title } = node.getData();
        return node.getData().title + '.' + record.title;
      }
      return record.title;
    },
    dataChanged() {
      if (this.nodeData.timestamp == undefined) {
        if (this.nodeData.title != this.form.title || this.form.conditions.length > 0) {
          return true;
        }
        if (this.nodeData.columns.length > 0) {
          return md5(JSON.stringify(this.nodeData.columns)) != md5(JSON.stringify(this.form.columns));
        }
      } else {
        let before = {
            title: this.nodeData.title,
            columns: this.nodeData.columns,
            conditions: this.nodeData.conditions
          },
          now = {
            title: this.form.title,
            columns: this.form.columns,
            conditions: this.form.conditions
          };
        return md5(JSON.stringify(before)) != md5(JSON.stringify(now));
      }
      return false;
    },
    onSave() {
      let node = this.graph.getCellById(this.graph.getDesigner().selectedNode.id);
      let originalData = JSON.parse(JSON.stringify(node.getData()));
      originalData.conditions = this.form.conditions;
      originalData.alias = this.form.alias;
      originalData.title = this.form.title;
      if (this.$refs.where) {
        let { sql, sqlParameter } = this.$refs.where.collect();
        originalData.conditionSql = sql;
        originalData.sqlParameter = sqlParameter;
      }
      if (this.form.unionType != undefined) {
        originalData.unionType = this.form.unionType;
      }
      originalData.columns = this.form.columns;
      originalData.timestamp = new Date().getTime(); // 修改时间戳，触发数据变动事件
      node.updateData(originalData);
    },

    initMergeDefinitionData() {
      let node = this.graph.getCellById(this.nid);
      let children = node.getChildren(),
        childrenDataMap = {},
        columnMap = {};
      this.mergeColumnSlots.splice(0, this.mergeColumnSlots.length);
      this.editMergeColumnDefine.splice(0, this.editMergeColumnDefine.length);
      // this.mergeObjOrder.splice(0,this.mergeObjOrder.length);
      this.editMergeColumnDefine.push({
        title: '显示名称',
        width: 200,
        dataIndex: 'title',
        fixed: 'left',
        scopedSlots: { customRender: 'titleSlot' }
      });
      if (children != undefined) {
        for (let child of children) {
          if (child.shape == 'edge') {
            continue;
          }
          let data = child.getData();
          this.editMergeColumnDefine.push({ title: data.title, width: 150, dataIndex: child.id, scopedSlots: { customRender: child.id } });
          let colOptions = [];
          columnMap[child.id] = {};
          for (let c of data.columns) {
            // if (c.dataType !== 'clob') {
            colOptions.push({
              label: c.title,
              value: c.alias
            });
            columnMap[child.id][c.alias] = c;
            // }
          }

          this.mergeColumnSlots.push({
            id: child.id,
            columnOptions: colOptions
          });
        }
        this.childrenDataMap = JSON.parse(JSON.stringify(childrenDataMap));
        this.columnMap = columnMap;
      }
    }
  },
  beforeMount() {
    this.initMergeDefinitionData();

    let node = this.graph.getCellById(this.nid),
      data = node.getData();
    this.tempColumns = JSON.parse(JSON.stringify(data.mergeColumns || []));
  },
  mounted() {},
  destroyed() {}
};
</script>
