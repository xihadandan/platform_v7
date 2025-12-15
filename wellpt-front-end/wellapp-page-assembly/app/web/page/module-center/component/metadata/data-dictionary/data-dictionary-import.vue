<template>
  <DefDataImport
    title="导入数据字典"
    dataType="cdDataDictionary"
    uploadTipMessage="请上传需导入的数据字典定义，同时只能导入一个文件"
    @load="onLoad"
    :import="doImport"
  >
    <template slot="content">
      <a-row class="import-content">
        <a-col span="6" class="basic-info">
          <div class="name" v-if="dictionaryInfo.nameInfo" :style="{ color: dictionaryInfo.nameInfo.isDifference ? conflictColor : '' }">
            {{ dictionaryInfo.nameInfo.controlValue }}
          </div>
          <div v-if="dictionaryInfo.codeInfo" :style="{ color: dictionaryInfo.codeInfo.isDifference ? conflictColor : '' }">
            字典编码： {{ dictionaryInfo.codeInfo.controlValue }}
            <p />
          </div>
          <div v-if="dictionaryInfo.moduleIdInfo" :style="{ color: dictionaryInfo.moduleIdInfo.isDifference ? conflictColor : '' }">
            归属： {{ dictionaryInfo.moduleIdInfo.controlValue }}
            <p />
          </div>
          <div v-if="dictionaryInfo.categoryUuidInfo" :style="{ color: dictionaryInfo.categoryUuidInfo.isDifference ? conflictColor : '' }">
            分类： {{ dictionaryInfo.categoryUuidInfo.controlValue }}
            <p />
          </div>
          <div v-if="dictionaryInfo.remarkInfo" :style="{ color: dictionaryInfo.remarkInfo.isDifference ? conflictColor : '' }">
            描述： {{ dictionaryInfo.remarkInfo.controlValue }}
            <p />
          </div>
          <a-checkbox v-model="autoCreateCategory">无分类时自动创建</a-checkbox>
        </a-col>
        <a-col span="18">
          <a-checkbox v-show="treeData && treeData.length > 0" v-model="checkAll" @change="onCheckAllChange">全选</a-checkbox>
          <a-tree
            ref="itemTree"
            :tree-data="treeData"
            show-icon
            checkable
            default-expand-all
            :checkedKeys="checkedKeys"
            :replaceFields="{ title: 'name', key: 'id' }"
            @check="onItemTreeCheck"
          >
            <template slot="title" slot-scope="scope">
              <span :style="{ color: scope.data.color }">{{ getItemName(scope) }}</span>
            </template>
          </a-tree>
        </a-col>
      </a-row>
    </template>
    <template slot="import-success">
      <a-icon type="check-circle"></a-icon>
      <br />
      导入完成
      <br />
      <span class="success-remark">
        成功导入字典数据
        <span class="count">{{ checkedKeys.length + 1 }}</span>
        条
      </span>
    </template>
  </DefDataImport>
</template>

<script>
import DefDataImport from '@admin/app/web/lib/def-data-import.vue';
import { deepClone } from '@framework/vue/utils/util';
export default {
  components: {
    DefDataImport
  },
  data() {
    return {
      initTreeData: [],
      treeData: [],
      allKeys: [],
      checkAll: true,
      checkedKeys: [],
      dictionaryInfo: {
        nameInfo: undefined,
        codeInfo: undefined,
        moduleIdInfo: undefined,
        categoryUuidInfo: undefined,
        remarkInfo: undefined
      },
      autoCreateCategory: true,
      conflictColor: 'orange',
      errorColor: 'red'
    };
  },
  methods: {
    onLoad({ treeData, statistic, fileID, extractTreeKeys, extractStatistic }) {
      this.autoCreateCategory = true;
      this.initTreeData = deepClone(treeData);
      let newTreeData = [];
      this.filterImportTree(treeData, newTreeData);
      if (newTreeData.length > 0) {
        this.treeData = newTreeData[0].children || [];
        this.allKeys = [];
        extractTreeKeys(this.treeData, this.allKeys);
        statistic.totalCount = 0;
        statistic.conflictCount = 0;
        statistic.errorCount = 0;
        this.newTreeData = newTreeData;
        extractStatistic([this.newTreeData[0]], statistic);
        this.checkedKeys = [...this.allKeys];
        this.checkAll = true;
        this.dictionaryNode = this.newTreeData[0];
        this.loadDictionaryInfo(fileID, this.dictionaryNode);
      }
    },
    filterImportTree(treeNodes, nodes) {
      treeNodes.forEach(item => {
        if (!item.data || !(item.data.type == 'cdDataDictionary' || item.data.type == 'cdDataDictionaryItem')) {
          return;
        }
        nodes.push(item);
        item.children = this.filterImportTree(item.children || [], []);
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
    loadDictionaryInfo(fileID, dictionaryNode) {
      $axios
        .post('/json/data/services', {
          serviceName: 'iexportService',
          methodName: 'getDifference',
          args: JSON.stringify([fileID, dictionaryNode.id, dictionaryNode.data.type])
        })
        .then(({ data }) => {
          if (data.data) {
            this.dictionaryInfo = data.data;
            if (data.data.dataDifferenceDetails) {
              data.data.dataDifferenceDetails.forEach(fieldInfo => {
                if (fieldInfo.fieldName == 'name') {
                  this.dictionaryInfo.nameInfo = fieldInfo;
                } else if (fieldInfo.fieldName == 'code') {
                  this.dictionaryInfo.codeInfo = fieldInfo;
                } else if (fieldInfo.fieldName == 'moduleId') {
                  this.dictionaryInfo.moduleIdInfo = fieldInfo;
                } else if (fieldInfo.fieldName == 'categoryUuid') {
                  this.dictionaryInfo.categoryUuidInfo = fieldInfo;
                } else if (fieldInfo.fieldName == 'remark') {
                  this.dictionaryInfo.remarkInfo = fieldInfo;
                }
              });
            }
          }
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
    doImport(callback) {
      // 字典分类节点
      let categoryNodeIds = [];
      if (this.autoCreateCategory) {
        this.extractCategoryNodeId(this.initTreeData, this.newTreeData[0].id, categoryNodeIds);
      }
      // 选中的字典项扩展属性
      let itemAttributeIds = [];
      this.extractItemAttributeNodeId(this.initTreeData, this.checkedKeys, itemAttributeIds);

      let importIds = [this.newTreeData[0].id, ...categoryNodeIds, ...this.checkedKeys, ...itemAttributeIds];
      callback(importIds);
    }
  }
};
</script>

<style lang="less" scoped>
.import-content {
  .basic-info {
    background-color: #f6f8fa;

    .name {
      font-size: 16px;
      font-weight: bold;
    }
  }
}
.import-success {
  font-size: 24px;
  text-align: center;
  .anticon {
    color: green;
  }
  .success-remark {
    font-size: 12px;
    color: grey;

    .count {
      padding: 2px;
      color: blue;
    }
  }
}
</style>
