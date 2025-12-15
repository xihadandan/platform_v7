<template>
  <a-table :columns="columns" :data-source="rows" rowKey="id" :expandedRowKeys.sync="expandedRowKeys" size="small" :pagination="false">
    <template slot="expandIcon" slot-scope="props">
      <a-button
        size="small"
        type="link"
        :style="{
          opacity: props.expandable ? 1 : 0
        }"
        :icon="props.expanded ? 'caret-down' : 'caret-right'"
        @click="clickExpand(props)"
      />
    </template>
    <template slot="title">
      <div style="display: flex; align-items: center; justify-content: space-between">
        数据结构
        <div>
          <Modal title="样例" :zIndex="1000" :bodyStyle="{ height: '400px' }">
            <template slot="content">
              <WidgetCodeEditor v-model="exampleBody" width="auto" height="300px" lang="json" ref="jsonEditor"></WidgetCodeEditor>
            </template>
            <a-button size="small" type="link" icon="file-text">样例</a-button>
          </Modal>
          <a-divider type="vertical" />
          <Modal title="通过JSON生成" :ok="jsonConvertToTableRows" :zIndex="1000" :bodyStyle="{ height: '400px' }">
            <template slot="content">
              <!-- <a-textarea v-model="jsonString" :rows="20" allow-clear /> -->
              <WidgetCodeEditor v-model="jsonString" width="auto" height="300px" lang="json" ref="jsonEditor"></WidgetCodeEditor>
            </template>
            <a-button size="small" type="link" icon="import">通过JSON生成</a-button>
          </Modal>
        </div>
      </div>
    </template>
    <template slot="propKeySlot" slot-scope="text, record, index">
      <template v-if="record.id == 'root'">
        <a-tag color="blue">根节点</a-tag>
      </template>
      <template v-else-if="record.id.startsWith('items')">
        <a-tag color="orange">数据项</a-tag>
      </template>
      <template v-else><a-input v-model="record.propKey" placeholder="属性" style="width: 120px" /></template>
    </template>
    <template slot="typeSlot" slot-scope="text, record, index">
      <a-select :options="typeOptions" style="width: 100px" v-model="record.type" @change="changeType(record)"></a-select>
    </template>
    <template slot="descriptionSlot" slot-scope="text, record, index">
      <a-input v-model="record.description" placeholder="说明" />
    </template>
    <template slot="operationSlot" slot-scope="text, record, index">
      <a-button
        icon="plus-square"
        title="添加子节点"
        type="link"
        size="small"
        v-if="record.type == 'object'"
        @click="addProperty(record)"
      ></a-button>
      <a-button
        icon="plus-square"
        type="link"
        size="small"
        v-if="(record.type == 'string' || record.type == 'number' || record.type == 'boolean') && !record.id.startsWith('items_')"
        title="添加相邻节点"
        @click="addSibling(record)"
      ></a-button>

      <a-button
        icon="minus-square"
        title="删除节点"
        type="link"
        size="small"
        @click="removeProperty(record)"
        v-if="record.id != 'root' && !record.id.startsWith('items')"
      ></a-button>
    </template>
  </a-table>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, deepClone, queryString } from '@framework/vue/utils/util';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';
import { cloneDeep } from 'lodash';

export default {
  name: 'ApiBodySchema',
  props: {
    initValue: Object
  },
  components: { Modal, WidgetCodeEditor },
  computed: {},
  data() {
    return {
      typeOptions: [
        { label: '字符串', value: 'string' },
        { label: '对象', value: 'object' },
        { label: '数组', value: 'array' },
        { label: '数字', value: 'number' },
        { label: '布尔', value: 'boolean' }
      ],
      rows:
        this.initValue != undefined && this.initValue.rows != undefined
          ? cloneDeep(this.initValue.rows)
          : [
              {
                id: 'root',
                propKey: undefined,
                type: 'object',
                description: undefined,
                children: []
              }
            ],
      expandedRowKeys: ['root'],
      columns: [
        {
          title: '属性名',
          dataIndex: 'name',
          scopedSlots: { customRender: 'propKeySlot' }
        },
        {
          title: '属性类型',
          dataIndex: 'type',
          width: 150,
          scopedSlots: { customRender: 'typeSlot' }
        },
        {
          title: '说明',
          dataIndex: 'description',
          width: 200,
          scopedSlots: { customRender: 'descriptionSlot' }
        },
        {
          title: '操作',
          width: 80,
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      jsonString: undefined,
      exampleBody: this.initValue != undefined && this.initValue.exampleBody != undefined ? this.initValue.exampleBody : undefined
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    clickExpand(props) {
      if (this.expandedRowKeys.includes(props.record.id)) {
        this.expandedRowKeys.splice(this.expandedRowKeys.indexOf(props.record.id), 1);
      } else {
        this.expandedRowKeys.push(props.record.id);
      }
    },
    changeType(record) {
      if (record.type == 'object' || record.type == 'array') {
        this.$set(record, 'children', []);
      } else {
        this.$delete(record, 'children');
      }

      if (record.type == 'array') {
        record.children.push({
          id: 'items_' + generateId(),
          propKey: undefined,
          description: undefined,
          type: 'string'
        });
      }
    },
    removeProperty(record) {
      try {
        let findParent = list => {
          for (let i = 0, len = list.length; i < len; i++) {
            if (list[i].id == record.id) {
              list.splice(i, 1);
              throw new Error('已处理');
            } else if (list[i].children != undefined) {
              findParent(list[i].children);
            }
          }
        };
        findParent(this.rows);
      } catch (error) {}
    },
    addSibling(record) {
      try {
        let findParent = list => {
          for (let i = 0, len = list.length; i < len; i++) {
            if (list[i].id == record.id) {
              list.splice(i + 1, 0, {
                id: generateId(),
                propKey: undefined,
                description: undefined,
                type: 'string'
              });
              throw new Error('已处理');
            } else if (list[i].children != undefined) {
              findParent(list[i].children);
            }
          }
        };
        findParent(this.rows);
      } catch (error) {}
    },
    addProperty(parent, afterNode) {
      if (parent != undefined) {
        if (!parent.hasOwnProperty('children')) {
          this.$set(parent, 'children', []);
        }
        parent.children.push({
          id: generateId(),
          propKey: undefined,
          description: undefined,
          type: 'string'
        });
        if (this.expandedRowKeys.indexOf(parent.id) == -1) {
          this.expandedRowKeys.push(parent.id);
        }
      }
    },
    getJSONSchema() {
      let convertArraySchema = (items, subSchema) => {
        subSchema.items = {
          type: items.children && items.children.length > 0 ? items.children[0].type : 'object',
          description: items.children && items.children.length > 0 ? items.children[0].description : undefined
        };
        if (items.children && items.children.length > 0) {
          if (items.children[0].type == 'object') {
            subSchema.items.properties = {};
            setChildrenSchema(item.children[0].children, subSchema.items.properties);
          } else if (items.children[0].type == 'array') {
            convertArraySchema(items.children[0], subSchema.items);
          }
        }
      };
      let setChildrenSchema = (list, schema) => {
        if (list) {
          for (let i = 0, len = list.length; i < len; i++) {
            let item = list[i];
            if (item.propKey != undefined) {
              schema[item.propKey] = {
                type: item.type,
                description: item.description
              };
              if (item.type == 'array') {
                schema[item.propKey].items = {
                  type: item.children[0].type,
                  description: item.children[0].description
                };
                if (item.children[0].type == 'object') {
                  schema[item.propKey].items.properties = {};
                  setChildrenSchema(item.children[0].children, schema[item.propKey].items.properties);
                } else if (item.children[0].type == 'array') {
                  convertArraySchema(item.children[0], schema[item.propKey].items);
                }
              } else if (item.type == 'object') {
                schema[item.propKey].properties = {};
                setChildrenSchema(item.children, schema[item.propKey].properties);
              }
            } else if (item.id.startsWith('items_')) {
              schema.type = item.type;
              schema.description = item.description;
              if (item.type == 'object') {
                schema.properties = {};
                setChildrenSchema(item.children, schema.properties);
              } else if (item.type == 'array') {
                convertArraySchema(item, schema);
              }
            }
          }
        }
      };
      let schema = { type: this.rows[0].type, description: this.rows[0].description };
      if (this.rows[0].type == 'array') {
        schema.items = {};
      } else if (this.rows[0].type == 'object') {
        schema.properties = {};
      }
      if (this.rows[0].children && this.rows[0].children.length > 0) {
        setChildrenSchema(this.rows[0].children, schema.items || schema.properties);
      }

      console.log('JSON Schema', this.rows, ' ---> ', schema);
      return schema;
    },
    jsonConvertToTableRows(e) {
      if (this.jsonString) {
        let json = undefined;
        try {
          json = JSON.parse(this.jsonString);
        } catch (error) {
          this.$message.error('json格式错误');
          return;
        }
        let obj = this.jsonToTreeStructure(json);
        if (obj.length > 0) {
          this.rows.splice(0, this.rows.length);
          if (obj[0].id.startsWith('items_')) {
            this.rows.push({
              id: 'root',
              type: 'array',
              children: obj
            });
          } else {
            this.rows.push({
              id: 'root',
              type: 'object',
              children: obj
            });
          }
        }
        this.$message.success('已生成数据结构');
        this.jsonString = undefined;
        this.$refs.jsonEditor.editor.setValue('');
        e(true);
      }
    },
    jsonToTreeStructure(data, parentKey = '') {
      // 处理数组输入的情况
      if (Array.isArray(data)) {
        if (data.length === 0) {
          return [
            {
              id: 'items_' + generateId(),
              type: 'array',
              children: null
            }
          ];
        }

        const firstItem = data[0];
        if (typeof firstItem === 'object' && firstItem !== null) {
          // 数组元素是对象
          return [
            {
              id: 'items_' + generateId(),
              type: 'object',
              children: this.jsonToTreeStructure(firstItem, parentKey ? `${parentKey}.items` : 'items')
            }
          ];
        } else {
          // 数组元素是基本类型
          return [
            {
              type: typeof firstItem,
              id: 'items_' + generateId(),
              children: null
            }
          ];
        }
      }

      // 处理对象输入的情况
      if (typeof data === 'object' && data !== null) {
        return Object.entries(data).map(([key, value]) => {
          const currentNode = {
            propKey: key,
            id: generateId(),
            type: Array.isArray(value) ? 'array' : typeof value
          };

          if (value && typeof value === 'object') {
            currentNode.children = this.jsonToTreeStructure(value, parentKey ? `${parentKey}.${key}` : key);
          }

          return currentNode;
        });
      }

      // 处理基本类型值（理论上不会执行到这里，因为最外层应该是对象或数组）
      return [
        {
          propKey: parentKey || 'value',
          id: generateId(),
          type: typeof data,
          children: null
        }
      ];
    }
  }
};
</script>
