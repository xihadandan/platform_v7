<template>
  <div class="search-settings">
    <div class="search-setting-content" v-if="fetched">
      <a-form-model
        :model="formData"
        class="search-settings pt-form"
        :label-col="{ span: 6 }"
        labelAlign="left"
        :wrapper-col="{ span: 18 }"
        :colon="false"
      >
        <a-card :bordered="false">
          <span slot="title">
            <span class="title-description">设置索引的关键信息以及索引更新方式</span>
          </span>
          <div class="content-item">
            <div class="sub-title pt-title-vertical-line">索引信息</div>
            <a-tabs defaultActiveKey="module">
              <a-tab-pane key="module" tab="功能模块">
                <a-form-model-item>
                  <template slot="label">
                    <label>索引摘要</label>
                    <a-tooltip :arrowPointAtCenter="true">
                      <div slot="title">为各功能模块数据设置通用的索引摘要规则</div>
                      <a-icon type="info-circle" style="vertical-align: middle; line-height: normal" />
                    </a-tooltip>
                  </template>
                  <div>自动获取数据源的前n个字段作为索引摘要</div>
                  <a-radio-group v-model="formData.fieldCount" button-style="solid">
                    <a-radio-button v-for="item in fieldCountOptions" :key="item.value" :value="item.value">
                      {{ item.label }}
                    </a-radio-button>
                  </a-radio-group>
                  <div>获取字段时排除以下字段</div>
                  <a-checkbox-group v-model="formData.excludeFields" :options="excludeFieldsOptions" />
                </a-form-model-item>
                <a-form-model-item>
                  <template slot="label">
                    <label>表单数据附件搜索</label>
                    <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                      <div slot="title">因附件数据量较大，搜索附件内容可能导致搜索速度下降，请酌情选择开启</div>
                      <a-icon type="info-circle" style="vertical-align: middle; line-height: normal" />
                    </a-tooltip>
                  </template>
                  <a-switch v-model="formData.indexAttachment" />
                </a-form-model-item>
              </a-tab-pane>
              <a-tab-pane key="builtIn" tab="平台内置" :forceRender="true">
                <div class="search-index-builtIn-title">流程数据</div>
                <a-form-model-item label="流程分类名称">
                  <a-input v-model="formData.builtIn.workflow.categoryName" style="width: 500px" />
                </a-form-model-item>
                <a-form-model-item label="索引标题">
                  {{ formData.builtIn.workflow.titleExpression }}
                  <TitleDefineTemplate
                    ref="titleDefineTemplate"
                    v-model="formData.builtIn.workflow.titleExpression"
                    :formData="formData.builtIn.workflow"
                    prop="titleExpression"
                    :hasDyformVar="false"
                    alert="在下方编辑流程标题表达式，可插入流程内置变量和文本。"
                  >
                    <a-button slot="trigger" type="link" size="small">
                      <Icon type="iconfont icon-ptkj-shezhi"></Icon>
                      设置
                    </a-button>
                  </TitleDefineTemplate>
                </a-form-model-item>
                <a-form-model-item label="索引摘要">
                  {{ formData.builtIn.workflow.contentExpression }}
                  <TitleDefineTemplate
                    ref="contentExpression"
                    v-model="formData.builtIn.workflow.contentExpression"
                    :formData="formData.builtIn.workflow"
                    prop="contentExpression"
                    :hasDyformVar="false"
                    alert="在下方编辑流程标题表达式，可插入流程内置变量和文本。"
                  >
                    <a-button slot="trigger" type="link" size="small">
                      <Icon type="iconfont icon-ptkj-shezhi"></Icon>
                      设置
                    </a-button>
                  </TitleDefineTemplate>
                </a-form-model-item>
                <a-form-model-item>
                  <template slot="label">
                    <label>流程数据附件搜索</label>
                    <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                      <div slot="title">因附件数据量较大，搜索附件内容可能导致搜索速度下降，请酌情选择开启</div>
                      <a-icon type="info-circle" style="vertical-align: middle; line-height: normal" />
                    </a-tooltip>
                  </template>
                  <a-switch v-model="formData.builtIn.workflow.indexAttachment" />
                </a-form-model-item>
                <div class="search-index-builtIn-title">文件库</div>
                <a-form-model-item label="文件库分类名称">
                  <a-input v-model="formData.builtIn.dms_file.categoryName" style="width: 500px" />
                </a-form-model-item>
                <a-form-model-item>
                  <template slot="label">
                    <label>文件库表单数据附件搜索</label>
                    <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                      <div slot="title">因附件数据量较大，搜索附件内容可能导致搜索速度下降，请酌情选择开启</div>
                      <a-icon type="info-circle" style="vertical-align: middle; line-height: normal" />
                    </a-tooltip>
                  </template>
                  <a-switch v-model="formData.builtIn.dms_file.indexAttachment" />
                </a-form-model-item>
              </a-tab-pane>
            </a-tabs>
          </div>
          <div class="content-item">
            <div class="sub-title pt-title-vertical-line">索引更新</div>
            <a-form-model-item label="增量更新">
              <a-radio-group v-model="formData.updateMode" button-style="solid">
                <a-radio-button v-for="item in updateModeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
              </a-radio-group>
              <a-time-picker
                v-if="formData.updateMode === updateModeOptions[1]['value']"
                v-model="formData.regularTimePoint"
                format="HH:mm"
                valueFormat="HH:mm"
                style="margin-left: 10px; width: 150px"
              />
            </a-form-model-item>
            <a-form-model-item>
              <template slot="label">
                <label>全量重建</label>
                <a-tooltip placement="top" :arrowPointAtCenter="true">
                  <div slot="title">索引重建期间资源消耗高(CPU/磁盘IO)，请确保磁盘空间足够，并在业务低峰期执行</div>
                  <a-icon type="info-circle" style="vertical-align: middle; line-height: normal" />
                </a-tooltip>
              </template>
              <rebuild-index-table :dataSource="formData.rebuildRules" />
            </a-form-model-item>
          </div>
        </a-card>
      </a-form-model>
    </div>
    <div class="search-setting-button" v-if="fetched">
      <a-button type="primary" @click="handleSaveSetting">保存</a-button>
      <a-button @click="handleResetSetting">恢复默认</a-button>
    </div>
  </div>
</template>

<script>
import SearchIndex, { fieldCountOptions, excludeFieldsOptions, updateModeOptions } from './SearchIndex';
import { fetchIndex, fetchSaveSetting } from './api';
import { getElMaxHeightFromViewport } from '@framework/vue/utils/util';
import TitleDefineTemplate from '@workflow/app/web/page/workflow-designer/component/commons/title-define-template.vue';
import RebuildIndexTable from './components/rebuild-index-table.vue';

export default {
  name: 'SearchIndexSetting',
  data() {
    return {
      fieldCountOptions,
      excludeFieldsOptions,
      updateModeOptions,
      formData: {},
      fetched: false,
      responseData: null
    };
  },
  components: {
    TitleDefineTemplate,
    RebuildIndexTable
  },
  provide() {
    return {
      settingProvide: {}
    };
  },
  created() {
    this.getIndexSetting();
  },
  mounted() {
    this.setContainerHeight();
  },
  methods: {
    setContainerHeight() {
      const { maxHeight } = getElMaxHeightFromViewport(this.$el);
      this.$el.style.cssText += `;height:${maxHeight}px`;
    },
    getIndexSetting() {
      fetchIndex().then(res => {
        this.responseData = res;
        if (res.definitionJson) {
          this.formData = JSON.parse(res.definitionJson);
        } else {
          this.formData = new SearchIndex();
        }
        this._provided.settingProvide = res;

        if (!this.formData.regularTimePoint) {
          this.formData.regularTimePoint = '03:00';
        }
        if (this.formData.builtIn.dms_file.indexAttachment === undefined) {
          this.formData.builtIn.dms_file.indexAttachment = true;
        }
        this.fetched = true;

        this.$nextTick(() => {
          this.$refs.titleDefineTemplate.update(this.formData.builtIn.workflow.titleExpression);
          this.$refs.contentExpression.update(this.formData.builtIn.workflow.contentExpression);
        });
      });
    },
    handleSaveSetting() {
      this.responseData.type = 'index';
      fetchIndex().then(res => {
        if (res.definitionJson) {
          const definitionJson = JSON.parse(res.definitionJson);
          if (definitionJson.rebuildRules) {
            definitionJson.rebuildRules.map(rule => {
              this.formData.rebuildRules.map(item => {
                if (item.uuid === rule.uuid) {
                  item.state = rule.state;
                }
              });
            });
          }
        }
        this.responseData.definitionJson = JSON.stringify(this.formData);
        fetchSaveSetting(this.responseData).then(() => {
          this.$message.success('保存成功！');
        });
      });
    },
    handleResetSetting() {
      const rebuildRules = JSON.parse(JSON.stringify(this.formData.rebuildRules));
      this.formData = new SearchIndex();
      this.formData.rebuildRules = rebuildRules;
      this.$message.info('已恢复，未保存！');
    }
  }
};
</script>

<style lang="less">
@import url(./style/search-setting-index.less);
</style>
