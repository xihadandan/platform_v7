<template>
  <HtmlWrapper :title="moduleDetail.name">
    <a-layout class="module-assemble-designer">
      <a-layout-header class="module-assemble-designer-header">
        <a-row>
          <a-col :span="12">
            <h1>
              <a-avatar shape="square" :size="24" style="background-color: #fff; color: var(--w-primary-color); margin-right: 8px">
                <Icon slot="icon" :type="moduleIcon || 'pticon iconfont icon-ptkj-zaitechengjian'" />
              </a-avatar>
              {{ moduleDetail.name }}
              <a-tag style="margin-left: 8px" :color="moduleDetail.enabled ? 'green' : 'orange'">
                {{ moduleDetail.enabled ? '上线' : '下线' }}
              </a-tag>
            </h1>
          </a-col>
          <a-col :span="12" class="header-buttons">
            <div style="float: right">
              <Drawer
                ref="drawer"
                height="calc(100vh)"
                placement="top"
                :closable="false"
                :destroyOnClose="true"
                :wrapStyle="{
                  transform: 'none'
                }"
                drawerClass="module-setting-drawer"
              >
                <div slot="title" style="display: flex; align-items: baseline; justify-content: space-between">
                  <label>
                    <a-avatar shape="square" :size="24" style="background-color: var(--w-primary-color); color: #fff; margin-right: 8px">
                      <Icon type="pticon iconfont icon-ptkj-shezhi" style="font-weight: normal; vertical-align: top" />
                    </a-avatar>
                    <span style="font-weight: bold">模块设置</span>
                  </label>
                  <a-button
                    @click="
                      () => {
                        this.$refs.drawer.onCancel();
                      }
                    "
                  >
                    返回模块
                  </a-button>
                </div>
                <template slot="content">
                  <ModuleSetting :moduleDetail="moduleDetail" v-if="moduleDetail.uuid != undefined" />
                </template>
                <a-button ghost>
                  <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                  模块设置
                </a-button>
              </Drawer>
              <a-divider type="vertical" style="height: 32px; background: #ffffff" />
              <ExportDef :uuid="moduleDetail.uuid" type="appModule" modifyRange>
                <a-button ghost style="margin-right: 8px">导出</a-button>
              </ExportDef>
              <a-button ghost @click="previewModuleIndexPage" style="margin-right: 8px">预览</a-button>
              <a-popconfirm
                v-if="!moduleDetail.enabled"
                placement="bottomRight"
                arrowPointAtCenter
                ok-text="确定"
                cancel-text="取消"
                @confirm="onClickPublishModule"
              >
                <template slot="title">确定要发布吗?</template>
                <a-button>发布</a-button>
              </a-popconfirm>
            </div>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout :hasSider="true" class="module-assemble-designer-content">
        <a-layout-sider theme="light" :width="442" class="left-sider">
          <a-tabs tab-position="left" size="small" v-model="activeTabKey" @change="onChangeTab">
            <a-tab-pane key="page">
              <span slot="tab">
                <Icon type="pticon iconfont icon-ptkj-yemian" />
                <br />
                页面
              </span>
              <ModulePageResource
                ref="pageResource"
                @select="onSelect"
                @resourceLoaded="onResourceLoaded"
                @modulePageLoaded="onModulePageLoaded"
              />
            </a-tab-pane>
            <a-tab-pane key="workflow">
              <span slot="tab">
                <Icon type="pticon iconfont icon-ptkj-suoyinshezhi" />
                <br />
                流程
              </span>
              <ModuleWorkflowResource @select="onSelect"></ModuleWorkflowResource>
            </a-tab-pane>
            <a-tab-pane key="dataModel">
              <span slot="tab">
                <Icon type="pticon iconfont icon-ptkj-zhagebuju" />
                <br />
                对象
              </span>
              <ModuleDataModelResource @select="onSelect" />
            </a-tab-pane>
            <a-tab-pane key="logic">
              <span slot="tab">
                <Icon type="pticon iconfont icon-ptkj-shujujiaohuan" />
                <br />
                逻辑
              </span>
              <a-result title="待开放" />
            </a-tab-pane>
            <a-tab-pane key="metadata">
              <span slot="tab">
                <Icon type="pticon iconfont icon-ptkj-liuchengfangzhen" />
                <br />
                元数据
              </span>
              <ModuleMetadataResource @select="onSelect" />
            </a-tab-pane>
            <a-tab-pane key="navigation">
              <span slot="tab">
                <Icon type="pticon iconfont iconfont icon-ptkj-daohangcaidan-01" />
                <br />
                导航
              </span>
              <ModuleIndexNavSetting @select="onSelect" ref="navigationRef" />
            </a-tab-pane>
            <a-tab-pane key="international">
              <a-result title="待开放" />
              <span slot="tab">
                <Icon type="pticon iconfont icon-ptkj-guojihua-01" />
                <br />
                国际化
              </span>
            </a-tab-pane>
            <a-tab-pane key="fullSearch" v-if="false">
              <span slot="tab">
                <Icon type="pticon iconfont icon-ptkj-sousuochaxun" />
                <br />
                检索
              </span>
              <module-full-search-category :moduleDetail="moduleDetail" @select="onSelect" />
            </a-tab-pane>
          </a-tabs>
        </a-layout-sider>
        <a-layout-content style="background-color: var(--w-widget-page-layout-bg-color)">
          <component
            v-if="contentComponent != undefined"
            :is="contentComponent"
            :navData="navData"
            :metadata="selectMetadata"
            :key="contentKey"
          />
          <template v-else>
            <assembleGuide @onClickCreateResource="onClickCreateResource" @changeTab="onChangeTab"></assembleGuide>
          </template>
        </a-layout-content>
      </a-layout>
    </a-layout>
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import './css/assemble.less';
import ModulePageResource from './component/page-resource/module-page-resource.vue';
import PagePreview from './component/page-resource/page-preview.vue';
import DyformPreview from './component/page-resource/dyform-preview.vue';
import WorkflowPreview from './component/work-flow/workflow-preview.vue';
import ModuleSetting from './component/module-setting.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import ModuleDataModelResource from './component/data-model/module-data-model-resource.vue';
import DataModelDetail from './component/data-model/data-model-detail.vue';
import ModuleMetadataResource from './component/metadata/module-metadata-resource.vue';
import DataDictionary from './component/metadata/data-dictionary/data-dictionary.vue';
import MetadataPreview from './component/metadata/metadata-preview.vue';
import ModuleWorkflowResource from './component/work-flow/module-wf-resource.vue';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';
import assembleGuide from './component/assemble-guide.vue';
import ModuleIndexNavSetting from './component/module-nav/module-index-nav-setting.vue';
import ModuleIndexNavContent from './component/module-nav/module-index-nav-content.vue';
import ModuleFullSearchCategory from './component/full-search/module-full-search-category.vue';
import ModuleFullSearchContent from './component/full-search/module-full-search-content.vue';
export default {
  name: 'ModuleAssemble',
  props: {},
  components: {
    ModulePageResource,
    PagePreview,
    DyformPreview,
    Modal,
    Drawer,
    ModuleSetting,
    ModuleDataModelResource,
    DataModelDetail,
    ModuleMetadataResource,
    DataDictionary,
    MetadataPreview,
    ModuleWorkflowResource,
    WorkflowPreview,
    ExportDef,
    ImportDef,
    assembleGuide,
    ModuleIndexNavSetting,
    ModuleIndexNavContent,
    ModuleFullSearchCategory,
    ModuleFullSearchContent
  },
  computed: {
    moduleIcon() {
      if (this.moduleDetail) {
        return this.iconDataToJson(this.moduleDetail.icon).icon;
      }
      return '';
    }
  },
  provide() {
    return {
      designMode: true,
      currentModule: this.moduleDetail,
      resources: [],
      getPageResources: this.getPageResources,
      getModulePage: this.getModulePage
    };
  },
  data() {
    return {
      resourceLoading: true,
      contentKey: 'contentKey_0',
      moduleSettingVisible: true,
      activeTabKey: 'page',
      contentComponent: undefined,
      selectMetadata: undefined,
      navData: undefined
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    getModulePage() {
      return this.modulePage;
    },
    getPageResources() {
      return this.$refs.pageResource.resources;
    },
    exportAsTreeData() {
      let form = {
        table: 'app_module',
        where: 'uuid = :uuid',
        params: {
          uuid: this.moduleDetail.uuid
        },
        descriptionMetadata: {
          titleColumn: 'name',
          typeName: '模块',
          idColumn: 'uuid'
        },
        subExportTables: [
          {
            table: 'app_page_definition',
            where: ' app_id = :app_module_id',
            // 关联的表单、文件、数据仓库等功能
            descriptionMetadata: {
              titleColumn: 'name',
              typeName: '页面',
              idColumn: 'uuid'
            },
            subExportTables: [
              {
                table: 'app_function'
              }
            ]
          }
        ]
      };
      $axios
        .post(`/proxy/common/iexport/service/exportTableDataAsTree`, form)
        .then(({ data }) => {
          console.log(data.data);
        })
        .catch(error => {});
    },
    previewModuleIndexPage() {
      window.open(`/webpage/${this.modulePage.id}`, '_blank');
    },
    onClickCreateResource(comp, title) {
      this.$refs.pageResource.openCreateResourceModal({ title, type: comp });
    },

    onSelect(compName, metadata, navData) {
      if (compName == 'ModuleIndexNavContent') {
        if (this.contentComponent != compName) {
          this.contentComponent = compName;
          this.contentKey = 'contentKey_' + new Date().getTime();
        }
      } else {
        this.contentComponent = compName;
        this.contentKey = 'contentKey_' + new Date().getTime();
      }
      this.selectMetadata = metadata;
      this.navData = navData;
    },
    onChangeTab(e) {
      this.activeTabKey = e;
      this.contentComponent = undefined;
      this.selectMetadata = undefined;
      this.navData = undefined;
      if (e == 'page' && this.$refs.pageResource) {
        this.$refs.pageResource.UpdateModulePageDesignable();
      } else if (e == 'navigation' && this.$refs.navigationRef) {
        this.$refs.navigationRef.onShowContent();
      }
    },
    onModulePageLoaded(data) {
      this.modulePage = data;
    },
    onResourceLoaded(data) {
      this._provided.resources = data;
      // this.resourceLoading = false;
    },
    onClickPublishModule() {
      this.$loading('发布中');
      $axios
        .get(`/proxy/api/app/module/enabled/${this.moduleDetail.uuid}`, { params: { enabled: true } })
        .then(({ data }) => {
          this.$loading(false);
          if (data.code == 0) {
            this.$message.success('发布成功');
            this.moduleDetail.enabled = true;
          }
        })
        .catch(error => {
          this.$loading(false);
        });
    },

    // 修复数据丢失使用
    checkDefaultModulePrivilege() {
      $axios
        .get(`/proxy/api/security/privilege/getPrivilegeBeanByCode/PRIVILEGE_MOD_${this.moduleDetail.id.toUpperCase()}`)
        .then(({ data }) => {
          if (data.data) {
            let prg = data.data;
            this.bindModulePrivilegeToModuleRoles(prg.uuid);
          } else {
            import('./component/module-detail.vue').then(({ default: ModuleDetail }) => {
              ModuleDetail.methods.createDefaultModulePrivilege(this.moduleDetail.id).then(uuid => {
                this.bindModulePrivilegeToModuleRoles(uuid);
              });
            });
          }
        });
    },
    bindModulePrivilegeToModuleRoles(privilegeUuid) {
      $axios.get('/proxy/api/security/role/queryAppRoles', { params: { appId: this.moduleDetail.id } }).then(({ data }) => {
        if (data.data) {
          let updateRoles = [];
          for (let i = 0, len = data.data.length; i < len; i++) {
            if (data.data.systemDef != 1) {
              updateRoles.push({
                role: { uuid: data.data[i].uuid },
                privilegeAdded: [privilegeUuid]
              });
            }
          }
          $axios.post(`/proxy/api/security/role/updateRoleMember`, updateRoles).then(({ data }) => {
            $axios.get(`/proxy/api/security/privilege/publishPrivilegeUpdatedEvent/${privilegeUuid}`);
          });
        }
      });
    },
    iconDataToJson(data) {
      if (!data) {
        data = {
          icon: '',
          bgColor: ''
        };
      } else {
        try {
          if (typeof data == 'string') {
            let iconJson = JSON.parse(data);
            if (iconJson) {
              data = iconJson;
            }
          }
        } catch (e) {
          if (typeof data == 'string') {
            let iconJson = {
              icon: data,
              bgColor: ''
            };
            data = iconJson;
          }
          return data;
        }
      }
      return data;
    }
  }
};
</script>
