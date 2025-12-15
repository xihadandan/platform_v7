<template>
  <!-- 树形下拉框备选项来源 -->
  <div>
    <a-form-model-item label="备选项来源">
      <a-select :options="optionSourceTypes" v-model="options.type" @change="changeOptionSource" />
    </a-form-model-item>
    <div v-show="options.type === 'selfDefine'">
      <div class="wcfg-select-item">
        <div class="wcfg-select-tree-label">
          <a-button size="small" type="primary" @click="addSibling">添加同级</a-button>
          <a-button size="small" type="primary" @click="addChildren">添加子级</a-button>
          <a-button size="small" type="primary" @click="handleDel">删除</a-button>
        </div>

        <template v-if="!widget.configuration.treeData.length">
          <a-empty :image="simpleImage" />
        </template>
        <!-- 备选项树 -->
        <template v-else>
          <a-directory-tree
            :tree-data="widget.configuration.treeData"
            :replaceFields="replaceFields"
            :selectedKeys.sync="selectKeys"
            :expandedKeys.sync="expandedKeys"
            @select="onSelect"
            @expand="onExpand"
          />
        </template>
      </div>
    </div>
    <div class="wcfg-select-item" v-show="options.type === 'dataProvider'">
      <div class="wcfg-select-tree-label">服务接口地址</div>
      <a-select
        placeholder="请选择"
        v-model="options.dataProviderId"
        v-show="dataProviderTreeData.length"
        :allowClear="true"
        :getPopupContainer="getPopupContainerByPs()"
        :dropdownClassName="getDropdownClassName()"
      >
        <a-select-option v-for="item in dataProviderTreeData" :key="item.id" :value="item.id">
          {{ item.text }}
        </a-select-option>
      </a-select>
    </div>
    <div v-show="options.type == 'dataDictionary'">
      <option-source-dict :showRelate="false" :designer="designer" :widget="widget" :options="widget.configuration.options" />
    </div>
    <a-form-model-item label="自动国际化翻译" v-if="options.type == 'dataSource'">
      <a-switch v-model="options.autoTranslate" />
    </a-form-model-item>
    <div v-show="options.type == 'dataSource'">
      <option-source-data-store
        :showRelate="false"
        :designer="designer"
        :widget="widget"
        :options="options"
        sortable
        :dataSourceFieldOptions="dataSourceFieldOptions"
        @columnIndexOptionChange="columnIndexOptionChange"
      />
    </div>
    <div v-if="options.type == 'apiLinkService'">
      <a-form-model-item label="API服务数据">
        <WidgetDesignDrawer :id="'apiLinkService' + widget.id" title="API 服务数据设置" :designer="designer" :width="875">
          <a-button type="primary" size="small">设置</a-button>
          <template slot="content">
            <ApiOperationDataset
              :widget="widget"
              :configuration="options"
              :designer="designer"
              :api-result-transform-schema="apiResultTransformSchema"
            >
              <template slot="dataTransformFunctionPrefix">
                <a-alert type="info" message="入参 response 为api返回结果, 需要返回以下数据结构样例" style="margin-bottom: 8px">
                  <template slot="description">
                    <pre>
[
  {
    "id": "选项1","name":"选项值1","children":[]
  }

  // 更多选项对象
  ...
] </pre
                    >
                    <p>可通过返回 promise 对象, 用于实现异步逻辑</p>
                  </template>
                </a-alert>
              </template>
            </ApiOperationDataset>
          </template>
        </WidgetDesignDrawer>
      </a-form-model-item>
    </div>
    <a-form-model-item label="页面变量" v-show="options.type == 'pageVar'">
      <a-select :options="pageVarOptions" allowClear v-model="options.bindPageVar" />
    </a-form-model-item>
    <WidgetDesignDrawer ref="addTreeDrawer" :id="`addSibling${widget.id}`" title="配置" :designer="designer">
      <template slot="content">
        <SelectTreeNodeField :treeNode="treeNode" :key="treeNode.id" :designer="designer" :widget="widget" />
      </template>
    </WidgetDesignDrawer>
  </div>
</template>

<script>
import { Empty } from 'ant-design-vue';
import { generateId } from '@framework/vue/utils/util';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
import SelectTreeNodeField from './tree-node-field.vue';
import OptionSourceDict from '@dyformWidget/widget-form-tag/configuration/components/optionSourceDict.vue';
import OptionSourceDataStore from '@dyformWidget/widget-form-tag/configuration/components/optionSourceDataStore.vue';
import optionSourceDataModel from '@dyformWidget/widget-form-tag/configuration/components/optionSourceDataModel.vue';
import { optionSourceTypes } from '../../../../commons/constant';
import ApiOperationDataset from '@dyform/app/web/widget/commons/api-operation-dataset.vue';

export default {
  name: 'PageSelectTreeOptionSource',
  props: {
    designer: Object,
    widget: Object,
    options: Object,
    dataSourceFieldOptions: Array
  },
  components: {
    SelectTreeNodeField,
    OptionSourceDict,
    OptionSourceDataStore,
    optionSourceDataModel,
    VNodes: {
      functional: true,
      render: (h, ctx) => ctx.props.vnodes
    },
    ApiOperationDataset
  },
  data() {
    return {
      optionSourceTypes: [
        { label: '常量', value: 'selfDefine' },
        // { label: '数据服务', value: 'dataProvider' },
        { label: '数据字典', value: 'dataDictionary' },
        { label: '数据仓库', value: 'dataSource' },
        { label: 'API 服务', value: 'apiLinkService' }
      ],
      selectKeys: [], // 选择的节点
      treeNode: {}, // 节点设置
      replaceFields: { key: 'id', title: 'display' },
      expandedKeys: [], // 展开的节点
      dataProviderTreeData: [], // 数据服务
      dataDictionaryTreeData: [], // 数据字典
      dataSourceOptions: [], // 数据仓库
      columnIndexOptions: [], // 数据仓库字段
      curTreeParent: undefined,
      apiResultTransformSchema: {
        type: 'array',
        description: '树形节点数组',
        propertyEditable: false,
        items: {
          type: 'object',
          propertyEditable: false,
          properties: {
            id: {
              propertyEditable: false,
              type: 'string',
              description: '节点ID'
            },
            name: {
              propertyEditable: false,
              type: 'string',
              description: '节点名称'
            },
            children: {
              type: 'array',
              description: '子节点数组',
              propertyEditable: false,
              items: {
                type: 'object',
                propertyEditable: false,
                description: '树节点对象'
              }
            }
          }
        }
      }
    };
  },
  computed: {
    pageVarOptions() {
      // 页面变量路径
      let paths = this.designer.pageVarKeyPaths(),
        options = [];
      for (let i = 0, len = paths.length; i < len; i++) {
        options.push({ label: paths[i], value: paths[i] });
      }
      return options;
    }
  },
  beforeCreate() {
    this.simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;
  },
  created() {
    this.fetchData(this.options.type);
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
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
    // 添加同级
    addSibling() {
      let treeData = this.widget.configuration.treeData;
      if (!this.selectKeys.length && treeData.length) {
        return;
      }
      const treeNode = {};
      treeNode.id = generateId();
      this.treeNode = treeNode;
      this.$refs.addTreeDrawer.openDrawer();
      if (!treeData.length) {
        // 第一次添加
        treeData.pid = '';
        treeData.push(treeNode);
        return;
      }
      let parentChilds = this.getTreeParentChilds(treeData, this.selectKeys[0]);
      treeNode.isLeaf = true;
      treeNode.pid = this.curTreeParent.id || '';
      parentChilds.push(treeNode);
    },
    // 添加子级
    addChildren() {
      if (!this.selectKeys.length) {
        return;
      }
      let treeData = this.widget.configuration.treeData;
      if (!treeData.length) {
        return;
      }
      const treeNode = {};
      treeNode.id = generateId();
      this.treeNode = treeNode;
      this.$refs.addTreeDrawer.openDrawer();
      let selectItem = this.getTreeDataByKey(treeData, this.selectKeys[0]);
      selectItem.isLeaf = false;
      treeNode.isLeaf = true;
      treeNode.pid = selectItem.id;
      if (!selectItem.children) {
        this.$set(selectItem, 'children', []);
      }
      selectItem.children.push(treeNode);
      this.expandedKeys.push(selectItem.id);
      this.$forceUpdate();
    },
    // 删除
    handleDel() {
      if (!this.selectKeys.length) {
        return;
      }
      let treeData = this.widget.configuration.treeData;
      if (!treeData.length) {
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
      let treeData = this.widget.configuration.treeData;
      let selectItem = this.getTreeDataByKey(treeData, keys[0]);
      this.treeNode = selectItem;
      this.$refs.addTreeDrawer.openDrawer();
    },
    // 展开
    onExpand() {},
    // 改变备选项来源 常量、数据服务、数据字典、数据仓库
    changeOptionSource(type) {
      this.fetchData(type);
    },
    columnIndexOptionChange(columnIndexOptions) {
      this.$emit('columnIndexOptionChange', columnIndexOptions);
    },
    fetchData(type) {
      if (type == 'dataProvider' && this.dataProviderTreeData.length == 0) {
        this.fetchDataProviderTreeData();
      }
    },
    // 获取数据服务
    fetchDataProviderTreeData() {
      const req = {
        serviceName: 'selectiveDataService',
        queryMethod: 'getTreeNodeDataProviderSelect',
        pageSize: 1000,
        pageNo: 1
      };
      let reqStr = '';
      for (const key in req) {
        reqStr = `${reqStr}&${key}=${req[key]}`;
      }
      reqStr = reqStr.substr(1);
      $axios({
        method: 'post',
        url: '/common/select2/query',
        data: reqStr,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      }).then(({ data }) => {
        if (data.results) {
          this.dataProviderTreeData = data.results;
          console.log(this.dataProviderTreeData);
        }
      });
    },
    // 获取数据字典
    fetchDataDictionaryTreeData() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'cdDataDictionaryFacadeService',
          methodName: 'getAllDataDictionaryAsCategoryTree'
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            // data.data.selectable = false;
            _this.dataDictionaryTreeData = data.data.children;
            _this.dataDictionaryTreeData.forEach(node => {
              node.selectable = !node.nocheck;
            });
          }
        });
    },
    // 获取数据仓库
    fetchDataSourceOptions(value) {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData'
        })
        .then(({ data }) => {
          if (data.results) {
            _this.dataSourceOptions = data.results;
          }
        });
    },

    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    // 改变数据仓库id
    changeDataSourceId(value) {
      this.options.dataSourceLabelColumn = null;
      this.options.dataSourceValueColumn = null;
      this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
      // this.columnIndexOptions.length = 0;
      if (value) this.fetchColumns();
    },
    // 获取数据仓库字段
    fetchColumns() {
      let _this = this;
      if (this.options.dataSourceId) {
        $axios
          .post('/json/data/services', {
            serviceName: 'viewComponentService',
            methodName: 'getColumnsById',
            args: JSON.stringify([this.options.dataSourceId])
          })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              _this.columnIndexOptions.splice(0, _this.columnIndexOptions.length);
              // _this.columnIndexOptions.length = 0;
              for (let i = 0, len = data.data.length; i < len; i++) {
                _this.columnIndexOptions.push({
                  value: data.data[i].columnIndex,
                  label: data.data[i].title
                });
              }
              _this.$emit('columnIndexOptionChange', _this.columnIndexOptions);
            }
          });
      }
    }
  }
};
</script>
