<script type="text/jsx">
import { TreeSelect } from 'ant-design-vue';

const treeDataDef = [
  {
    title: 'Node1',
    value: '0-0',
    key: '0-0',
    children: [
      {
        name: 'name',
        value: '0-0-1',
        key: '0-0-1',
        scopedSlots: {
          // custom title
          title: 'title',
        },
      },
      {
        title: 'Child Node2',
        value: '0-0-2',
        key: '0-0-2',
      },
    ],
  },
  {
    title: 'Node2',
    value: '0-1',
    key: '0-1',
  },
];
const replaceFieldsDef = {children:'children', title:'title', key:'key', value: 'value' };

export default {
  name: 'WTreeSelect',
  extends: TreeSelect,
  render(h){
    let {
      treeDataDef,
      showSearch,
      allowClear,
      treeData,
      replaceFields
    } = this.$props;
    
    replaceFields = treeData ? replaceFields : replaceFieldsDef,
    treeData = treeData ? treeData : treeDataDef
    console.log(this.$props)
    return (
      <a-tree-select
        treeData={treeData}
        showSearch={showSearch}
        allowClear={allowClear}
        replaceFields={replaceFields}
      >
      </a-tree-select>
    )
  },
  props: {
    treeDataDef:{
      type: Array,
      default: () => treeDataDef
    },
    mode:{
      type: String,
      default: 'selfDefine',
      validator: function (mode) {
        // 常量 数据服务 数据字典 数据仓库
        return ['selfDefine','dataProvider','dataDictionary','dataSource'].includes(mode);
      }
    },
    displayType:{
      type: String,
      default: 'node',
      validator: function (type) {
        // 节点名称 全路径名称
        return ['node','all'].includes(type);
      }
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    disable: {
      type: Boolean,
      default: false
    },
    displayAsLabel: {
      type: Boolean,
      default: false
    },
  },
  data() {
    return {
      
    }
  }
}
</script>
