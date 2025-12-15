<template>
  <DefDataExport
    ref="defDataExport"
    title="导出数据字典"
    :dataUuid="dataDictionary.uuid"
    dataType="cdDataDictionary"
    @load="onLoad"
    :modalWidth="600"
    :export="doExport"
  >
    <a-button type="link" size="small">
      <Icon type="pticon iconfont icon-luojizujian-yemiantiaozhuan"></Icon>
      导出
    </a-button>
    <template slot="content">
      <div class="data-dictionary-export">
        <div class="basic-info">
          <a-row>
            <a-col class="dictionary-name">
              {{ dataDictionary.name }}
              <a-tag v-if="moduleSource == 'none'" color="blue">通用字典</a-tag>
            </a-col>
          </a-row>
          <a-row>
            <a-col span="12">
              <span class="label">字典编码：</span>
              <span class="value">{{ dataDictionary.code }}</span>
            </a-col>
            <a-col span="12">
              <span class="label">分类：</span>
              <span class="value">{{ dataDictionary.categoryName }}</span>
            </a-col>
          </a-row>
          <a-row>
            <a-col span="12">
              <span class="label">归属：</span>
              <span class="value">{{ dataDictionary.moduleName }}</span>
            </a-col>
            <a-col span="12">
              <span class="label">描述：</span>
              <span class="value">{{ dataDictionary.remark }}</span>
            </a-col>
          </a-row>
        </div>
        <a-row class="data-dictionary-box">
          <a-col>
            <div class="sub-title">字典项</div>
            <a-checkbox v-show="treeData && treeData.length > 0" v-model="checkAll" @change="onCheckAllChange" style="margin-bottom: 12px">
              全选
            </a-checkbox>
            <a-tree
              ref="itemTree"
              class="ant-tree-directory tree-more-operations"
              :tree-data="treeData"
              show-icon
              checkable
              :checkedKeys="checkedKeys"
              :replaceFields="{ title: 'name', key: 'id' }"
              @check="onItemTreeCheck"
            >
              <template slot="title" slot-scope="scope">
                <span>{{ getItemName(scope) }}</span>
              </template>
            </a-tree>
          </a-col>
        </a-row>
      </div>
    </template>
  </DefDataExport>
</template>

<script>
import DefDataExport from '@admin/app/web/lib/def-data-export.vue';
import { deepClone } from '@framework/vue/utils/util';
export default {
  props: {
    dataDictionary: Object,
    // 模块来源，'current'当前模块，'all'系统所有模块,‘none’没有模块归属
    moduleSource: {
      type: String,
      default: 'current'
    }
  },
  components: { DefDataExport },
  data() {
    return {
      treeData: [],
      allKeys: [],
      checkedKeys: [],
      checkAll: true
    };
  },
  methods: {
    onLoad({ treeData }) {
      this.initTreeData = deepClone(treeData);
      let newTreeData = [];
      this.filterExportTree(treeData, newTreeData);
      if (newTreeData.length > 0) {
        this.treeData = newTreeData[0].children || [];
      }
      this.newTreeData = newTreeData;
      this.checkedKeys = [...this.allKeys];
    },
    filterExportTree(treeNodes, nodes) {
      treeNodes.forEach(item => {
        if (!item.data || !(item.data.type == 'cdDataDictionary' || item.data.type == 'cdDataDictionaryItem')) {
          return;
        }
        if (item.data.type == 'cdDataDictionaryItem') {
          this.allKeys.push(item.id);
        }
        nodes.push(item);
        item.children = this.filterExportTree(item.children || [], []);
      });
      return nodes;
    },
    // 提取分类节点
    extractCategoryNodeId(treeNodes, dictionaryNodeId, categoryNodeIds) {
      treeNodes.forEach(item => {
        let children = item.children || [];
        if (item.id == dictionaryNodeId) {
          // 字典节点下的分类节点
          children.forEach(child => {
            if (child.data && child.data.type == 'cdDataDictionaryCategory') {
              categoryNodeIds.push(child.id);
            }
          });
        }
        this.extractCategoryNodeId(children, dictionaryNodeId, categoryNodeIds);
      });
    },
    extractItemAttributeNodeId(treeNodes, itemNodeIds, itemAttributeNodeIds) {
      treeNodes.forEach(item => {
        let children = item.children || [];
        if (item.data && item.data.type == 'cdDataDictionaryItem') {
          let itemIndex = itemNodeIds.findIndex(itemId => itemId == item.id);
          // 字典项节点下的扩展属性
          if (itemIndex != -1) {
            children.forEach(child => {
              if (child.data && child.data.type == 'cdDataDictionaryItemAttribute') {
                itemAttributeNodeIds.push(child.id);
              }
            });
          }
        }
        this.extractItemAttributeNodeId(children, itemNodeIds, itemAttributeNodeIds);
      });
    },
    getItemName(treeNode) {
      let names = treeNode.name.split('：');
      return names.length > 1 ? names[1] : names[0];
    },
    onCheckAllChange() {
      if (this.checkAll) {
        this.checkedKeys = [...this.allKeys];
      } else {
        this.checkedKeys = [];
      }
    },
    onItemTreeCheck(checkedKeys) {
      this.checkedKeys = checkedKeys;
      if (this.checkedKeys.length == this.allKeys.length) {
        this.checkAll = true;
      } else {
        this.checkAll = false;
      }
    },
    doExport(downloadFields, callback) {
      // 字典分类节点
      let categoryNodeIds = [];
      this.extractCategoryNodeId(this.initTreeData, this.newTreeData[0].id, categoryNodeIds);
      // 选中的字典项扩展属性
      let itemAttributeIds = [];
      this.extractItemAttributeNodeId(this.initTreeData, this.checkedKeys, itemAttributeIds);

      downloadFields.fileName = this.newTreeData[0].name;
      downloadFields.treeNodeIds = [this.newTreeData[0].id, ...categoryNodeIds, ...this.checkedKeys, ...itemAttributeIds].join(';');
      this.$refs.defDataExport.download(downloadFields, () => {
        callback(true);
      });
    }
  }
};
</script>

<style lang="less">
.data-dictionary-export {
  .basic-info {
    background: #fafafa;
    border-radius: 4px 4px 4px 4px;
    padding: var(--w-padding-xs) var(--w-padding-md);
    margin-bottom: 12px;

    .dictionary-name {
      font-weight: bold;
      font-size: var(--w-font-size-lg);
      line-height: 32px;
      color: var(--w-text-color-dark);
      margin-bottom: 4px;
    }
    font-size: var(--w-font-size-base);
    line-height: 32px;
    .label {
      color: var(--w-text-color-base);
    }
    .value {
      color: var(--w-text-color-dark);
    }
  }

  .data-dictionary-box {
    border: 1px solid var(--w-border-color-light);
    border-radius: 4px;
    margin-top: 4px;
    .sub-title {
      font-weight: bold;
      font-size: var(--w-font-size-lg);
      line-height: 32px;
      color: var(--w-text-color-dark);
      margin-bottom: 4px;
    }
    > div {
      padding: 12px 20px;
      min-height: 400px;
    }
  }
}
</style>
