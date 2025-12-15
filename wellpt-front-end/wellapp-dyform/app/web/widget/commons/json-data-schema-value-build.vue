<template>
  <a-table
    :scroll="scroll"
    :columns="columns"
    :data-source="rows"
    rowKey="id"
    :expandedRowKeys.sync="expandedRowKeys"
    size="small"
    :pagination="false"
    :key="key"
  >
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
          <Modal title="样例" :zIndex="1000" :bodyStyle="{ height: '400px' }" v-if="showExample">
            <template slot="content">
              <WidgetCodeEditor
                :readOnly="!editable"
                v-model="exampleBody"
                width="auto"
                height="300px"
                lang="json"
                ref="jsonEditor"
              ></WidgetCodeEditor>
            </template>
            <a-button size="small" type="link" icon="file-text">样例</a-button>
          </Modal>
          <template v-if="editable && showImportGenerateJsonSchema">
            <a-divider type="vertical" />
            <Modal title="通过JSON生成" :ok="jsonConvertToTableRows" :zIndex="1000" :bodyStyle="{ height: '400px' }">
              <template slot="content">
                <!-- <a-textarea v-model="jsonString" :rows="20" allow-clear /> -->
                <WidgetCodeEditor v-model="jsonString" width="auto" height="300px" lang="json" ref="jsonEditor"></WidgetCodeEditor>
              </template>
              <a-button size="small" type="link" icon="import">通过JSON生成</a-button>
            </Modal>
          </template>
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
      <template v-else>
        <label v-if="!editable || record.propertyEditable === false">{{ record.propKey }}</label>
        <a-input v-model="record.propKey" placeholder="属性" style="width: 120px" v-else />
      </template>
    </template>
    <template slot="typeSlot" slot-scope="text, record, index">
      <a-space>
        <a-tag v-if="!editable || record.propertyEditable === false">{{ typeNames[record.type] }}</a-tag>
        <a-select v-else :options="typeOptions" style="width: 80px" v-model="record.type" @change="changeType(record)"></a-select>
        <ValueBinding v-if="editable && canBindValue(record)" :configuration="record" :designer="designer" :jsonPaths="jsonPaths" />
        <a-popover
          v-if="record.id.startsWith('items') && record.type == 'object' && record.children != undefined && record.children.length > 0"
        >
          <template slot="content">
            以下属性值可通过
            <a-tag>_$loopItem</a-tag>
            JSON路径值访问上级数组遍历的数据项对象属性, 例如:
            <a-tag>_$loopItem.code</a-tag>
          </template>
          <a-icon type="info-circle" />
        </a-popover>
      </a-space>
    </template>

    <template slot="descriptionSlot" slot-scope="text, record, index">
      <label v-if="!editable || record.propertyEditable === false">{{ record.description }}</label>
      <a-input v-else v-model="record.description" placeholder="说明" />
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
        v-if="record.id != 'root' && !record.id.startsWith('items') && editable && record.propertyEditable !== false"
      ></a-button>
    </template>
  </a-table>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, deepClone, queryString } from '@framework/vue/utils/util';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';
import { cloneDeep, debounce } from 'lodash';
import ValueBinding from './value-binding.vue';
export default {
  name: 'JsonDataSchemaValueBuild',
  props: {
    initValue: Object,
    designer: Object,
    options: Object,
    scroll: Object
  },
  components: { Modal, WidgetCodeEditor, ValueBinding },
  computed: {
    typeNames() {
      let map = {};
      for (let o of this.typeOptions) {
        map[o.value] = o.label;
      }
      return map;
    },
    jsonPaths() {
      return this.options != undefined ? this.options.jsonPaths || [] : [];
    },
    showImportGenerateJsonSchema() {
      return this.options && this.options.showImportGenerateJsonSchema == true;
    },
    showExample() {
      return this.options && this.options.showExample == true;
    }
  },
  data() {
    let editable = this.options == undefined || this.options.editable == undefined || (this.options && this.options.editable === true);
    let columns = [
      {
        title: '属性名',
        dataIndex: 'name',
        scopedSlots: { customRender: 'propKeySlot' }
      },
      {
        title: '属性类型' + (editable ? ' / 值' : ''),
        dataIndex: 'type',
        scopedSlots: { customRender: 'typeSlot' }
      },
      {
        title: '说明',
        dataIndex: 'description',
        width: 80,
        scopedSlots: { customRender: 'descriptionSlot' }
      }
      // {
      //   title: '值',
      //   dataIndex: 'valueInput',
      //   scopedSlots: { customRender: 'valueInputSlot' }
      // },
    ];
    if (editable) {
      columns.push({
        title: '操作',
        width: 80,
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operationSlot' }
      });
    }
    return {
      editable,
      allowImportJsonToSchema: this.options && this.options.allowImportJsonToSchema === true,
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
                children: []
              }
            ],
      expandedRowKeys: ['root'],
      columns,
      jsonString: undefined,
      exampleBody: this.initValue != undefined && this.initValue.exampleBody != undefined ? this.initValue.exampleBody : undefined,
      key: new Date().getTime()
    };
  },
  beforeCreate() {},
  created() {
    this.emitChange = debounce(this.emitChange.bind(this), 300);
    if (this.initValue && this.initValue.schema) {
      this.isInitValue = true;
      this.setJsonSchemaToTreeRowStructure(cloneDeep(this.initValue.schema));
    }
  },
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
    canBindValue(record) {
      if (typeof record.assignable == 'boolean') {
        // 是否属性可以绑定值
        return record.assignable;
      }
      if (record.id.startsWith('items')) {
        // 数据项行不能绑定值
        return false;
      }
      // 类型是对象，且有子属性的情况下，也不能绑定值
      if (record.type == 'object' && record.children != undefined && record.children.length > 0) {
        return false;
      }

      return true;
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
          type: items.children[0].type,
          description: items.children[0].description
        };

        if (items.children[0].valueOption != undefined) {
          subSchema.items.valueOption = items.children[0].valueOption;
        }

        if (items.children[0].type == 'object') {
          subSchema.items.properties = {};
          setChildrenSchema(item.children[0].children, subSchema.items.properties);
        } else if (items.children[0].type == 'array') {
          convertArraySchema(items.children[0], subSchema.items);
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
              if (item.valueOption != undefined) {
                schema[item.propKey].valueOption = item.valueOption;
              }
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
      let schema = { type: this.rows[0].type };
      if (this.rows[0].type == 'array') {
        schema.items = {};
      } else if (this.rows[0].type == 'object') {
        schema.properties = {};
      }
      if (this.rows[0].valueOption != undefined) {
        schema.valueOption = this.rows[0].valueOption;
      }
      if (this.rows[0].children && this.rows[0].children.length > 0) {
        if (this.rows[0].type == 'object') {
          schema.valueOption = undefined;
        }
        setChildrenSchema(this.rows[0].children, schema.items || schema.properties);
      }

      // console.log('JSON Schema', this.rows, ' ---> ', schema);
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
    jsonSchemaToTreeRowStructure(schema) {
      let appendProperties = (r, properties) => {
        if (r.children == undefined) {
          r.children = [];
        }
        if (properties && Object.keys(properties).length > 0) {
          for (let p in properties) {
            let prop = properties[p];
            if (prop.type == 'array') {
              let items = prop.items;
              let item = {
                id: generateId(),
                type: prop.type,
                propKey: p,
                propertyEditable: prop.propertyEditable,
                description: prop.description,
                valueOption: prop.valueOption,
                children: [
                  {
                    id: 'items_' + generateId(),
                    type: items.type,
                    propertyEditable: items.propertyEditable,
                    description: items.description,
                    valueOption: items.valueOption
                  }
                ]
              };

              r.children.push(item);
              if (items.type == 'object') {
                appendProperties(item.children[0], items.properties);
              }
            } else {
              let _r = {
                id: generateId(),
                type: prop.type,
                propKey: p,
                propertyEditable: prop.propertyEditable,
                description: prop.description,
                valueOption: prop.valueOption
              };
              r.children.push(_r);
              if (prop.type == 'object') {
                appendProperties(_r, prop.properties);
              }
            }
          }
        }
      };
      let row = {
        id: 'root',
        type: schema.type,
        description: schema.description,
        propertyEditable: schema.propertyEditable,
        valueOption: schema.valueOption
      };
      if (schema.type == 'object') {
        appendProperties(row, schema.properties);
      } else if (schema.type == 'array') {
        let items = schema.items;
        let r = {
          id: 'items_' + generateId(),
          type: items.type,
          propertyEditable: items.propertyEditable,
          description: items.description,
          valueOption: items.valueOption
        };
        row.children = [r];
        if (items.type == 'object') {
          appendProperties(r, items.properties);
        }
      }
      // console.log('schema -> rows', schema, row);
      return row;
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
    },

    setJsonSchemaToTreeRowStructure(schema) {
      this.rows.splice(0, this.rows.length, this.jsonSchemaToTreeRowStructure(schema));
      this.key = new Date().getTime();
    },

    emitChange() {
      let schema = this.getJSONSchema();
      // console.log('json rows', this.rows, '----> schema: ', this.getJSONSchema());
      // console.log('json schema -> row', this.jsonSchemaToTreeRowStructure(schema));
      this.$emit('change', {
        schema,
        rows: this.rows
      });
    }
  },
  watch: {
    rows: {
      handler(val) {
        if (this.isInitValue) {
          this.isInitValue = false;
          return;
        }
        this.emitChange();
      },
      deep: true
    }
  }
};
</script>
