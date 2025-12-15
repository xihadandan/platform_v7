<template>
  <div class="search-settings">
    <div class="search-setting-content" v-if="fetched">
      <a-form-model :model="formData" class="pt-form" :label-col="{ span: 6 }" labelAlign="left" :wrapper-col="{ span: 18 }" :colon="false">
        <a-card :bordered="false">
          <span slot="title">
            <span class="title-description">设置全文检索的相关功能如搜索交互、搜索推荐以及搜索结果的展示</span>
          </span>
          <div class="content-item">
            <div class="sub-title pt-title-vertical-line">基础设置</div>
            <a-form-model-item label="全文检索">
              <a-switch v-model="formData.enabled" />
            </a-form-model-item>
            <a-form-model-item label="搜索范围">
              <search-scope v-model="formData.scopeList" />
            </a-form-model-item>
            <a-form-model-item label="交互模式">
              <a-radio-group v-model="formData.interactiveMode" button-style="solid">
                <a-radio-button v-for="item in interactiveModeOptions" :key="item.value" :value="item.value">
                  {{ item.label }}
                </a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <template v-if="formData.interactiveMode === interactiveModeOptions[1]['value']">
              <a-form-model-item label="搜索页面">
                <search-page-config v-model="formData.pageConfig" />
              </a-form-model-item>
              <a-form-model-item label="搜索页面地址">
                <div>
                  PC端
                  <a-button type="link" @click="clickToOpenUrl">
                    {{ fullSearchUrl }}
                  </a-button>
                  <a-button type="link" icon="copy" title="复制" @click="onClickCopy" />
                </div>
              </a-form-model-item>
            </template>
            <a-form-model-item>
              <template slot="label">
                <label>搜索分类</label>
                <a-tooltip placement="top" :arrowPointAtCenter="true">
                  <div slot="title">开启后支持按数据分类进行搜索</div>
                  <a-icon type="info-circle" style="vertical-align: middle; line-height: normal" />
                </a-tooltip>
              </template>
              <a-switch v-model="formData.enabledCategory" />
            </a-form-model-item>
          </div>
          <div class="content-item">
            <div class="sub-title pt-title-vertical-line">搜索建议</div>
            <a-form-model-item label="热门搜索">
              <a-switch v-model="formData.enabledHot" />
            </a-form-model-item>
            <a-form-model-item label="搜索历史">
              <a-switch v-model="formData.enabledHistory" />
            </a-form-model-item>
            <a-form-model-item label="常用搜索">
              <a-switch v-model="formData.enabledCommon" />
            </a-form-model-item>
            <!-- <a-form-model-item label="推荐词补全">
              <a-switch v-model="formData.enabledRecommendedCompletion" />
            </a-form-model-item>
            <a-form-model-item label="推荐词补全依据">
              <a-checkbox-group v-model="formData.recommendCompleteSource" :options="recommendCompleteSourceOptions" />
            </a-form-model-item> -->
          </div>
          <div class="content-item">
            <div class="sub-title pt-title-vertical-line">搜索结果设置</div>
            <a-form-model-item label="结果页面">
              <result-page-config v-model="formData.resultConfig" v-if="formData.resultConfig" />
            </a-form-model-item>
            <a-form-model-item label="结果去重">
              <div>当表单关联流程时，搜索结果中</div>
              <a-radio-group v-model="formData.resultDuplication" :options="resultDuplicationOptions" />
            </a-form-model-item>
            <a-form-model-item label="结果详情打开方式">
              <a-radio-group v-model="formData.resultOpenMode" button-style="solid">
                <a-radio-button v-for="item in resultOpenModeOptions" :key="item.value" :value="item.value">
                  {{ item.label }}
                </a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item>
              <template slot="label">
                <label>表单数据权限提示</label>
                <a-tooltip placement="top" :arrowPointAtCenter="true">
                  <div slot="title">当用户无权限访问结果详情时，提示用户</div>
                  <a-icon type="info-circle" style="vertical-align: middle; line-height: normal" />
                </a-tooltip>
              </template>
              <a-input v-model="formData.formPermissionTips" />
            </a-form-model-item>
          </div>
          <!-- <div class="content-item">
            <div class="sub-title pt-title-vertical-line">其他设置</div>
            <a-form-model-item label="语言输入">
              <a-switch v-model="formData.enabledVoice" />
            </a-form-model-item>
          </div> -->
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
import { cloneDeep } from 'lodash';
import SearchSetting, {
  interactiveModeOptions,
  recommendCompleteSourceOptions,
  resultDuplicationOptions,
  resultOpenModeOptions,
  fileSearchTypeOptions
} from './SearchSetting';
import { fetchSearch, fetchSaveSetting, fetchDeleteCacheByKey } from './api';
import { getElMaxHeightFromViewport, copyToClipboard } from '@framework/vue/utils/util';
import SearchScope from './components/search-scope.vue';
import SearchPageConfig from './components/search-page-config.vue';
import ResultPageConfig from './components/result-page-config.vue';

export default {
  name: 'SearchSetting',
  inject: ['tenantProdVersionUuid'],
  data() {
    return {
      interactiveModeOptions,
      recommendCompleteSourceOptions,
      resultDuplicationOptions,
      resultOpenModeOptions,
      fileSearchTypeOptions,
      formData: {},
      fetched: false,
      responseData: null,
      fullSearchUrl: ''
    };
  },
  components: {
    SearchScope,
    SearchPageConfig,
    ResultPageConfig
  },
  created() {
    this.setFullTextSearchUrl();
    this.getSearch();
  },
  mounted() {
    this.setContainerHeight();
  },
  methods: {
    setFullTextSearchUrl() {
      this.fullSearchUrl = `${location.origin}/webapp/${this._$SYSTEM_ID}/${this.tenantProdVersionUuid}/page-full-search`;
    },
    clickToOpenUrl() {
      window.open(this.fullSearchUrl, '_blank');
    },
    onClickCopy(e, text) {
      copyToClipboard(this.fullSearchUrl, e, success => {
        if (success) {
          this.$message.success('已复制');
        }
      });
    },
    setContainerHeight() {
      const { maxHeight } = getElMaxHeightFromViewport(this.$el);
      this.$el.style.cssText += `;height:${maxHeight}px`;
    },
    getSearch() {
      fetchSearch().then(res => {
        this.responseData = res;
        if (res.definitionJson) {
          this.formData = JSON.parse(res.definitionJson);
        } else {
          this.formData = new SearchSetting();
        }
        this.fetched = true;
        // this.addSettingProperty();
      });
    },
    // 添加搜索设置属性
    addSettingProperty() {
      if (this.formData.fileSearchType === undefined) {
        this.$set(this.formData, 'fileSearchType', this.fileSearchTypeOptions[0]['value']);
      }
    },
    handleSaveSetting() {
      this.responseData.type = 'search';
      this.responseData.definitionJson = JSON.stringify(this.formData);
      fetchSaveSetting(this.responseData).then(() => {
        this.$message.success('保存成功！');
        fetchDeleteCacheByKey({
          key: `SYSTEM_FULL_SEARCH_SETTING:${this._$USER.tenantId}:${this._$SYSTEM_ID}`
        });
      });
    },
    handleResetSetting() {
      let scopeList = cloneDeep(this.formData.scopeList);
      scopeList.map(item => {
        if (item.value === 'workflow' || item.value === 'dms_file') {
          item.visible = true;
        } else {
          item.visible = false;
        }
      });
      this.formData = new SearchSetting();
      this.formData.scopeList = scopeList;
      this.$message.info('已恢复，未保存！');
    }
  }
};
</script>

<style lang="less">
@import url(./style/search-setting-index.less);
</style>
