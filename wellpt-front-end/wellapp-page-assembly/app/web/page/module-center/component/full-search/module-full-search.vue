<template>
  <div class="module-full-search">
    <a-card :bordered="false">
      <template slot="title">
        <label>全文检索</label>
        <a-tooltip placement="bottom" :arrowPointAtCenter="true">
          <div slot="title">设置模块中需支持全文检索的数据和分类，系统开启全文检索功能后，这些数据将自动创建索引</div>
          <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
        </a-tooltip>
      </template>
      <a-row type="flex">
        <a-col flex="378px">
          <module-full-search-category :moduleDetail="currentModule" :showTitle="false" @select="onSelect" v-if="dataModelsFetched" />
        </a-col>
        <a-col style="width: calc(100% - 378px)">
          <component
            v-if="contentComponent != undefined"
            :is="contentComponent"
            :metadata="selectMetadata"
            :subformMainMap="subformMainMap"
            :key="contentKey"
          />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script>
import ModuleFullSearchCategory from './module-full-search-category.vue';
import ModuleFullSearchContent from './module-full-search-content.vue';
import { fetchFormDefinitionByModuleIds, fetchDataModelsByType, fetchSaveAllModel } from './api';
import { createModel } from './FullSearch';

export default {
  name: 'ModuleFullSearch',
  inject: ['currentModule'],
  components: {
    ModuleFullSearchCategory,
    ModuleFullSearchContent
  },
  data() {
    const moduleId = this.currentModule.id;
    return {
      moduleId,
      contentComponent: undefined,
      contentKey: 'contentKey_0',
      selectMetadata: undefined,
      subformMainMap: {},
      dataModels: [], // 模块下的所有存储对象
      dataModelsFetched: false
    };
  },
  created() {
    this.getFormDefinitionByModuleIds();
    this.init();
  },
  methods: {
    init() {
      let allFetch = [this.getDataModelsByType()];
      Promise.all(allFetch).then(res => {
        this.dataModels = res[0];
        this.dataModelsFetched = true;
      });
    },
    // 获取存储对象
    getDataModelsByType() {
      let req = {};
      req.type = ['TABLE'];
      req.module = [this.moduleId];
      return fetchDataModelsByType(req);
    },
    // 查询表单定义（通过模块id）
    getFormDefinitionByModuleIds() {
      let subformMainMap = {};
      fetchFormDefinitionByModuleIds([this.moduleId]).then(res => {
        res.map((item, index) => {
          const definitionVjson = JSON.parse(item.definitionVjson);
          if (definitionVjson.subforms) {
            definitionVjson.subforms.map(sub => {
              if (!subformMainMap[sub.configuration.formId]) {
                subformMainMap[sub.configuration.formId] = [];
              }
              subformMainMap[sub.configuration.formId].push(item.id);
            });
          }
        });
        this.subformMainMap = subformMainMap;
        // console.log(this.subformMainMap);
      });
    },
    // 保存所有全文检索数据模型
    saveAllSearchModel(defaultCategory) {
      const models = this.dataModels.map(item => {
        return createModel({
          categoryUuid: defaultCategory.id,
          dataModelUuid: item.uuid
        });
      });

      return new Promise((resolve, reject) => {
        fetchSaveAllModel(models).then(res => {
          resolve();
        });
      });
    },
    onSelect(compName, metadata, defaultCategory) {
      const setSelectMetadata = () => {
        this.contentComponent = compName;
        this.contentKey = 'contentKey_' + new Date().getTime();
        this.selectMetadata = metadata;
      };
      if (!metadata && defaultCategory) {
        this.saveAllSearchModel(defaultCategory).then(() => {
          setSelectMetadata();
        });
      } else {
        setSelectMetadata();
      }
    }
  }
};
</script>

<style lang="less">
.module-full-search {
  height: 100%;
  > .ant-card {
    --w-card-head-title-weight: bold;
    height: 100%;
    display: flex;
    flex-direction: column;
    > .ant-card-body {
      flex: 1;
      padding: 1px 0 0;
      > .ant-row-flex {
        height: 100%;
        > .ant-col {
          &:first-child {
            border-right: solid 1px var(--w-border-color-light);
          }
        }
      }
    }
  }
  .module-full-search-container {
    height: 100%;
    .preview-card {
      height: 100%;
      display: flex;
      flex-direction: column;
      > .ant-card-head {
        border-bottom: 0;

        .title {
          color: var(--w-gray-color-10);
          font-weight: normal;
        }
      }
      > .ant-card-body {
        // flex: 1;
        height: e('calc(100vh - 174px)');
        padding: 0 !important;
        > .data-model-table {
          padding: 0 20px 12px;
        }
      }
    }
  }
}
</style>
