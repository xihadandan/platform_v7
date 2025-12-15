<template>
  <!-- 树形下拉框备选项来源 -->
  <div>
    <div class="ant-row ant-form-item">
      <config-sub-title>备选项配置</config-sub-title>
    </div>
    <div class="wcfg-select-item">
      <div class="wcfg-select-tree-label">备选项来源</div>
      <a-radio-group size="small" v-model="options.type" @change="e => changeOptionSource(e.target.value)">
        <a-radio-button :value="item.value" v-for="(item, i) in optionSourceTypes" :key="i">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group>
    </div>
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
      <!-- 联动设置 -->
      <a-form-model-item label="联动设置" :wrapper-col="{ style: { textAlign: 'right' } }">
        <a-switch v-model="widget.configuration.optionDataAutoSet" />
      </a-form-model-item>
      <template v-if="widget.configuration.optionDataAutoSet">
        <RelateFieldConfiguration :widget="widget" :designer="designer" :options="widget.configuration.options" />
      </template>
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
      <option-source-dict :designer="designer" :widget="widget" :options="widget.configuration.options" />
      <a-form-model-item label="真实值字段" class="display-b" :label-col="{}" :wrapper-col="{}" v-if="false">
        <a-select
          placeholder="请选择"
          v-model="options.dataDictValueColumn"
          :allowClear="true"
          :showSearch="true"
          style="width: 100%"
          :filter-option="filterOption"
          :options="columnIndexOptions"
          :getPopupContainer="getPopupContainerByPs()"
          :dropdownClassName="getDropdownClassName()"
        />
      </a-form-model-item>
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
      <a-form-model-item label="自动国际化翻译">
        <a-switch v-model="options.autoTranslate" />
      </a-form-model-item>
    </div>
    <div v-show="options.type == 'dataSource'">
      <a-form-model-item label="自动国际化翻译" v-if="options.type !== 'selfDefine' && options.type !== 'dataDictionary'">
        <a-switch v-model="options.autoTranslate" />
      </a-form-model-item>
      <option-source-data-store
        :designer="designer"
        :widget="widget"
        :options="options"
        :dataSourceFieldOptions="dataSourceFieldOptions"
        sortable
        @columnIndexOptionChange="columnIndexOptionChange"
      />
      <a-form-model-item label="每次点击加载数据" :label-col="{ span: 10 }" :wrapper-col="{ span: 13, style: { textAlign: 'right' } }">
        <a-switch v-model="options.dataSourceLoadEveryTime" />
      </a-form-model-item>
    </div>
    <div v-if="options.type == 'dataModel'">
      <a-form-model-item label="自动国际化翻译">
        <a-switch v-model="options.autoTranslate" />
      </a-form-model-item>
      <option-source-data-model
        :designer="designer"
        :widget="widget"
        :options="widget.configuration.options"
        :fieldOptions="dataSourceFieldOptions"
        sortable
        @columnIndexOptionChange="columnIndexOptionChange"
      />
      <a-form-model-item label="每次点击加载数据" :label-col="{ span: 10 }" :wrapper-col="{ span: 13, style: { textAlign: 'right' } }">
        <a-switch v-model="options.dataSourceLoadEveryTime" />
      </a-form-model-item>
    </div>
    <slot></slot>
    <WidgetDesignDrawer ref="addTreeDrawer" :id="`addSibling${widget.id}`" title="配置" :designer="designer">
      <template slot="content">
        <SelectTreeNodeField :treeNode="treeNode" :designer="designer" :widget="widget" />
      </template>
    </WidgetDesignDrawer>
  </div>
</template>

<script>
import { Empty } from 'ant-design-vue';
import { generateId } from '@framework/vue/utils/util';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
import ApiOperationDataset from '../../commons/api-operation-dataset.vue';

export default {
  name: 'SelectTreeOptionSource',
  props: {
    designer: Object,
    widget: Object,
    options: Object,
    dataSourceFieldOptions: Array,
    optionSourceTypesProps: Array
  },
  components: {
    ApiOperationDataset,
    VNodes: {
      functional: true,
      render: (h, ctx) => ctx.props.vnodes
    }
  },
  data() {
    let optionSourceTypes = [
      { label: '常量', value: 'selfDefine' },
      // { label: '数据服务', value: 'dataProvider' },
      { label: '数据字典', value: 'dataDictionary' },
      { label: '数据仓库', value: 'dataSource' },
      { label: '数据模型', value: 'dataModel' },
      { label: 'API 服务', value: 'apiLinkService' }
    ];
    if (this.optionSourceTypesProps) {
      optionSourceTypes = this.optionSourceTypesProps;
    }
    return {
      optionSourceTypes,
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
      let options = [{ value: 'sortField' }, { value: 'defaultCondition' }];
      [...this.dataSourceFieldOptions, ...options].forEach(item => {
        if (this.widget.configuration.options.hasOwnProperty(item.value)) {
          this.$set(this.widget.configuration.options, item.value, '');
        }
      });
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
