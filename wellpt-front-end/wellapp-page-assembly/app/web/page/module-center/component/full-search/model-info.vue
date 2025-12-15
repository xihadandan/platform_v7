<template>
  <a-form-model ref="form" :model="formData" :label-col="{ span: 5, offset: 1 }" :wrapper-col="{ span: 18 }">
    <a-form-model-item label="数据源">{{ `${dataModel.name}（${dataModel.id}）` }}</a-form-model-item>
    <a-form-model-item label="默认查询条件">
      <!-- <a-textarea
        v-model="formData.matchJson"
        :rows="3"
        :style="{
          height: 'auto'
        }"
      /> -->
      <a-form-model-item labelAlign="left" :colon="false" :labelCol="{ style: { width: '100%' } }" style="margin-bottom: 0">
        <template slot="label">
          <a-select :options="matchOptions" v-model="formData.matchJson.match" style="width: 140px" />
          <a-button size="small" type="link" @click="addCondition">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            添加
          </a-button>
        </template>
        <div class="conditions-list" v-if="formData.matchJson.conditions.length">
          <a-row v-for="(item, index) in formData.matchJson.conditions" :key="index" type="flex" align="middle">
            <a-col :flex="1">
              <a-select :options="modelColumnOptions" v-model="item.code" />
              <div style="display: flex">
                <a-select :options="operatorOptions" v-model="item.operator" />
                <a-input v-model="item.value" v-show="item.operator !== 'true' && item.operator !== 'false'" />
              </div>
            </a-col>
            <a-col flex="50px" style="text-align: center">
              <a-button type="link" size="small" @click="formData.matchJson.conditions.splice(index, 1)" title="删除">
                <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
              </a-button>
            </a-col>
          </a-row>
        </div>
        <a-empty
          v-else
          :imageStyle="{
            height: '60px',
            'margin-top': '7px'
          }"
        />
      </a-form-model-item>
    </a-form-model-item>
    <a-form-model-item label="数据查阅表单">
      <a-radio-group v-model="formData.viewDataFormSource" button-style="solid">
        <a-radio-button v-for="item in viewDataFormSourceOptions" :key="item.value" :value="item.value">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="关联表单" v-if="formData.viewDataFormSource === viewDataFormSourceOptions[1]['value']">
      <a-select v-model="formData.viewDataFormUuid" :options="viewDataFormOptions" />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { viewDataFormSourceOptions, matchOptions, operatorOptions } from './FullSearch';
import { fetchModelDetailByUuid, queryFormDefinitionIgnoreJsonByDataModelId } from './api';

export default {
  name: 'ModelInfo',
  props: {
    dataModel: {
      type: Object,
      default: () => {}
    },
    formData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    let matchJson = {
      match: 'all',
      conditions: []
    };
    if (this.formData.matchJson) {
      if (typeof this.formData.matchJson === 'string') {
        this.formData.matchJson = JSON.parse(this.formData.matchJson);
      }
    } else {
      this.formData.matchJson = matchJson;
    }
    return {
      viewDataFormSourceOptions,
      matchOptions,
      operatorOptions,
      modelColumnOptions: [],
      viewDataFormOptions: []
    };
  },
  created() {
    this.getViewDataFormOptions();
    this.getModelDetailByUuid();
  },
  methods: {
    // 添加条件
    addCondition() {
      this.formData.matchJson.conditions.push({
        code: undefined,
        operator: true,
        value: undefined
      });
    },
    // 获取数据模型详情
    getModelDetailByUuid() {
      fetchModelDetailByUuid({
        uuid: this.dataModel.uuid
      }).then(res => {
        let modelColumnOptions = [];
        JSON.parse(res.columnJson).forEach(item => {
          if (!item.isSysDefault) {
            item.label = item.title;
            item.value = item.column;
            modelColumnOptions.push(item);
          }
        });
        this.modelColumnOptions = modelColumnOptions;
      });
    },
    getViewDataFormOptions() {
      queryFormDefinitionIgnoreJsonByDataModelId({
        dataModelId: this.dataModel.id
      }).then(res => {
        this.viewDataFormOptions = res.map(item => {
          item.label = `${item.name}（${item.version}）`;
          item.value = item.uuid;
          return item;
        });
        if (res.length === 1 && !this.formData.viewDataFormUuid) {
          this.formData.viewDataFormUuid = res[0]['uuid'];
        }
      });
    }
  }
};
</script>

<style lang="less">
.conditions-list {
  padding: 5px;
  border-radius: 4px;
  border: solid 1px var(--w-border-color-base);
  .ant-row-flex {
    &:not(:last-child) {
      padding-bottom: 7px;
      margin-bottom: 7px;
      border-bottom: solid 1px var(--w-border-color-base);
    }
  }
}
</style>
