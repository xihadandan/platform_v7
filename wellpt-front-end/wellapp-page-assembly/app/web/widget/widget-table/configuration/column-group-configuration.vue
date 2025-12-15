<template>
  <div class="column-group-configuration" v-if="widget.configuration.defaultDisplayState == 'table'">
    <a-form-model-item>
      <template slot="label">
        <a-tooltip placement="right">
          <template slot="title">
            <ul style="padding-inline-start: 20px; margin-block-end: 0px">
              <!-- <li>分组里的列中排最前面为表头分组位置。</li> -->
              <li>设置多级分组表头和分组标题</li>
              <li>分组显示的列中排最前面的列位置为分组插入位置</li>
              <li>分组内列可拖动排序</li>
              <li>
                列冻结
                <ul style="padding-inline-start: 16px; margin-block-end: 0px">
                  <li>分组以及分组下的列不支持列冻结</li>
                  <li>设置前后冻结列时，分组之后的列不冻结</li>
                </ul>
              </li>
              <li>
                拖动列宽
                <ul style="padding-inline-start: 16px; margin-block-end: 0px">
                  <li>分组以及分组下的列不支持拖动列宽</li>
                </ul>
              </li>
            </ul>
          </template>
          表头分组
          <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
        </a-tooltip>
      </template>
      <a-switch v-model="widget.configuration.isColumnsGroup" />
    </a-form-model-item>
    <div class="wcfg-select-item" v-if="widget.configuration.isColumnsGroup">
      <div class="wcfg-select-tree-label">
        <a-button size="small" type="primary" @click="addSibling">添加同级</a-button>
        <a-button size="small" type="primary" @click="addChildren">添加子级</a-button>
        <a-button size="small" type="primary" @click="handleDel">删除</a-button>
      </div>

      <template v-if="!widget.configuration.columnsGroupNodes.length">
        <a-empty :image="simpleImage" />
      </template>
      <!-- 备选项树 -->
      <template v-else>
        <a-tree
          :class="['ant-tree-directory']"
          :tree-data="widget.configuration.columnsGroupNodes"
          :selectedKeys.sync="selectKeys"
          :expandedKeys.sync="expandedKeys"
          :replaceFields="replaceFields"
          @select="onSelect"
          @expand="onExpand"
        >
          <a-icon slot="switcherIcon" type="down" />
          <template slot="title" slot-scope="scope">
            <div class="flex w-ellipsis" :title="scope.title" :data-key="scope.id">
              <Icon v-if="scope.isColumns" type="ant-iconfont paper-clip" style="margin-right: 4px"></Icon>
              <Icon v-else type="iconfont icon-oa-wodewenjianjia" style="margin-right: 4px"></Icon>
              {{ scope.isColumns ? getColumnTitle(scope.dataIndex, scope.title) : scope.title }}
            </div>
          </template>
        </a-tree>
      </template>
    </div>
    <WidgetDesignDrawer ref="addTreeDrawer" :id="`addSibling${widget.id}`" title="配置" :designer="designer">
      <template slot="content">
        <a-form-model>
          <a-form-model-item label="列节点" v-if="hasColomnSwitch">
            <a-switch v-model="treeNode.isColumns" @change="handleIsColumnChange" />
          </a-form-model-item>
          <template v-if="treeNode && !treeNode.isColumns">
            <a-form-model-item label="分组名称">
              <a-input placeholder="请输入" v-model="treeNode.title" allowClear>
                <template slot="addonAfter">
                  <WI18nInput :widget="widget" :designer="designer" :target="treeNode" :code="treeNode.id" v-model="treeNode.title" />
                </template>
              </a-input>
            </a-form-model-item>
            <a-form-model-item label="列提示">
              <a-row type="flex">
                <a-col flex="50px"><a-switch v-model="treeNode.showTip" /></a-col>
                <a-col flex="auto">
                  <a-input v-model="treeNode.tipContent" v-show="treeNode.showTip" placeholder="请输入提示内容" allowClear>
                    <template slot="addonAfter">
                      <WI18nInput
                        v-show="treeNode.showTip"
                        :widget="widget"
                        :designer="designer"
                        :code="treeNode.id + '_tipContent'"
                        :target="treeNode"
                        v-model="treeNode.tipContent"
                      />
                    </template>
                  </a-input>
                </a-col>
              </a-row>
            </a-form-model-item>
            <!-- <a-form-model-item label="标题对齐" class="item-lh">
              <a-radio-group size="small" v-model="treeNode.titleAlign" button-style="solid">
                <a-radio-button value="left"><a-icon type="align-left" /></a-radio-button>
                <a-radio-button value="center"><a-icon type="align-center" /></a-radio-button>
                <a-radio-button value="right"><a-icon type="align-right" /></a-radio-button>
              </a-radio-group>
            </a-form-model-item> -->
          </template>
          <a-form-model-item label="分组列" v-if="treeNode && treeNode.isColumns">
            <a-select
              style="width: 100%"
              :options="currentColumnOptions"
              v-model="treeNode.dataIndex"
              allowClear
              @change="handleChange"
              show-search
              :filter-option="filterOption"
            ></a-select>
          </a-form-model-item>
        </a-form-model>
      </template>
    </WidgetDesignDrawer>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { Empty } from 'ant-design-vue';
import { generateId, deepClone } from '@framework/vue/utils/util';
export default {
  name: 'ColumnGroupConfiguration',
  props: {
    widget: Object,
    designer: Object,
    configuration: Object,
    columnIndexOptions: Array
  },
  components: {},
  computed: {
    currentColumnOptions() {
      let options = deepClone(this.widget.configuration.columns);
      let maps = deepClone(this.selectedColumnMap);
      if (this.treeNode && this.treeNode.isColumns && this.treeNode.dataIndex) {
        let index = maps.indexOf(this.treeNode.dataIndex);
        maps.splice(index, 1);
      }
      // 过滤掉已经选择的列
      let _options = [];
      options.forEach(option => {
        if (maps.indexOf(option.dataIndex) == -1) {
          _options.push({
            value: option.dataIndex,
            label: option.title
          });
        }
      });
      return _options;
    },
    hasColomnSwitch() {
      let hasSwitch = false;
      if (this.treeNode && this.treeNode.pid) {
        // 不是第一级
        if (!this.treeNode.children || (this.treeNode.children && this.treeNode.children.length == 0)) {
          hasSwitch = true;
        }
      }
      return hasSwitch;
    },
    columnIndexTitleMap() {
      let map = {};
      for (let i = 0, len = this.currentColumnOptions.length; i < len; i++) {
        map[this.currentColumnOptions[i].value] = this.currentColumnOptions[i].label;
      }
      return map;
    }
  },
  data() {
    return {
      replaceFields: { key: 'id', title: 'title' },
      selectKeys: [], // 选择的节点
      expandedKeys: [], // 展开的节点
      treeNode: {},
      curTreeParent: undefined,
      selectedColumnMap: []
    };
  },
  beforeCreate() {
    this.simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;
  },
  mounted() {},
  methods: {
    initSelectdColumnMap() {
      let treeData = this.widget.configuration.columnsGroupNodes;
      this.selectedColumnMap = [];
      let getMap = data => {
        if (Array.isArray(data)) {
          for (let i = 0; i < data.length; i++) {
            getMap(data[i]);
          }
        } else if (data) {
          if (data.children && data.children.length) {
            getMap(data.children);
          } else if (data.isColumns && data.dataIndex) {
            this.selectedColumnMap.push(data.dataIndex);
          }
        }
      };
      if (treeData.length) {
        getMap(treeData);
      }
    },
    // 根据key获取与之相等的数据对象
    getTreeDataByKey(childs = [], findKey, parent = {}) {
      let finditem = null;
      for (let i = 0, len = childs.length; i < len; i++) {
        let item = childs[i];
        if (item.id !== findKey && item.children && item.children.length > 0) {
          finditem = this.getTreeDataByKey(item.children, findKey, item);
        }
        if (item.id == findKey) {
          finditem = item;
        }
        if (finditem != null) {
          break;
        }
      }
      return finditem;
    },
    // 根据key获取父级节点children数组
    getTreeParentChilds(childs = [], findKey, parent = {}) {
      let parentChilds = [];
      for (let i = 0, len = childs.length; i < len; i++) {
        let item = childs[i];
        if (item.id !== findKey && item.children && item.children.length > 0) {
          parentChilds = this.getTreeParentChilds(item.children, findKey, item);
        }
        if (item.id == findKey) {
          this.curTreeParent = parent;
          parentChilds = childs;
        }
        if (parentChilds.length > 0) {
          break;
        }
      }
      return parentChilds;
    },
    getNewNode(id) {
      return {
        id,
        title: '',
        isColumns: false,
        showTip: false,
        tipContent: '',
        titleAlign: 'left',
        pid: '',
        dataIndex: ''
      };
    },
    // 添加同级
    addSibling() {
      let treeData = this.widget.configuration.columnsGroupNodes;
      if (!this.selectKeys.length && treeData.length) {
        this.$message.warning('请选择节点');
        return;
      }
      const treeNode = this.getNewNode(generateId());
      if (!treeData.length) {
        // 第一次添加
        treeData.pid = '';
        treeData.push(treeNode);
      } else {
        let parentChilds = this.getTreeParentChilds(treeData, this.selectKeys[0]);
        treeNode.isLeaf = true;
        treeNode.pid = this.curTreeParent.id || '';
        parentChilds.push(treeNode);
      }
      this.initSelectdColumnMap();
      this.treeNode = treeNode;
      this.$refs.addTreeDrawer.openDrawer();
    },
    // 添加子级
    addChildren() {
      if (!this.selectKeys.length) {
        this.$message.warning('请选择节点');
        return;
      }
      let treeData = this.widget.configuration.columnsGroupNodes;
      if (!treeData.length) {
        this.$message.warning('请选择父节点');
        return;
      }
      const treeNode = this.getNewNode(generateId());
      let selectItem = this.getTreeDataByKey(treeData, this.selectKeys[0]);
      this.curTreeParent = selectItem;
      if (selectItem.isColumns) {
        this.$message.warning('该节点为列节点，请选择其他节点');
        return;
      }
      selectItem.isLeaf = false;
      treeNode.isLeaf = true;
      treeNode.pid = selectItem.id;
      if (!selectItem.children) {
        this.$set(selectItem, 'children', []);
      }
      this.treeNode = treeNode;
      this.initSelectdColumnMap();
      this.$refs.addTreeDrawer.openDrawer();
      selectItem.children.push(treeNode);
      this.expandedKeys.push(selectItem.id);
      this.$forceUpdate();
    },
    // 删除
    handleDel() {
      if (!this.selectKeys.length) {
        this.$message.warning('请选择节点');
        return;
      }
      let treeData = this.widget.configuration.columnsGroupNodes;
      if (!treeData.length) {
        this.$message.warning('请选择节点');
        return;
      }
      let parentChilds = this.getTreeParentChilds(treeData, this.selectKeys[0]);
      let delIndex = parentChilds.findIndex(item => item.id == this.selectKeys[0]);
      parentChilds.splice(delIndex, 1);
      if (!parentChilds.length) {
        this.curTreeParent.isLeaf = true;
      }
    },
    // 选择
    onSelect(keys, event) {
      if (keys.length) {
        let treeData = this.widget.configuration.columnsGroupNodes;
        let selectItem = this.getTreeDataByKey(treeData, keys[0]);
        this.getTreeParentChilds(treeData, keys[0]);
        this.treeNode = selectItem;
        this.initSelectdColumnMap();
        this.$refs.addTreeDrawer.openDrawer();
      } else {
        this.treeNode = {};
        this.$refs.addTreeDrawer.closeDrawer();
      }
    },
    // 展开
    onExpand(expandedKeys, { node, expanded }) {
      this.expandedKeys = expandedKeys;
      let _this = this;
      if (expanded && !node.dataRef.isColumns) {
        setTimeout(() => {
          _this.draggable(node.dataRef.children, node.$el.querySelector('ul'), 'li');
        }, 1000); // 延迟等待展开
      }
    },
    handleChange(value, option) {
      if (option) {
        this.treeNode.title = this.columnIndexTitleMap[value];
      }
    },
    handleIsColumnChange(checked) {
      if (!checked) {
        this.treeNode.dataIndex = '';
      }
    },
    draggable(data, parentSelector, dragHandleClass, onEvent) {
      let _this = this;
      this.$nextTick(() => {
        import('sortablejs').then(Sortable => {
          Sortable.default.create(parentSelector, {
            handle: dragHandleClass,
            onChoose: e => {},
            onUnchoose: (onEvent && onEvent.onUnchoose) || function () {},
            onMove: (onEvent && onEvent.onMove) || function () {},
            onEnd:
              (onEvent && onEvent.onEnd) ||
              function (e) {
                let temp = data.splice(e.oldIndex, 1)[0];
                data.splice(e.newIndex, 0, temp);
                _this.draggable(data, parentSelector, dragHandleClass, onEvent);
                if (onEvent && onEvent.onEvent && onEvent.onEvent.afterOnEnd) {
                  onEvent.onEvent.afterOnEnd();
                }
              }
          });
        });
      });
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    getColumnTitle(dataIndex, title) {
      if (dataIndex && this.widget.configuration.columns) {
        let column = this.widget.configuration.columns.find(item => item.dataIndex == dataIndex);
        if (column) {
          return column.title || title;
        }
      }
      return title;
    }
  }
};
</script>
