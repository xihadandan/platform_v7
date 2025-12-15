<template>
  <a-card class="product-version-setting-card" :bodyStyle="{ padding: '0px', height: 'calc(100vh - 250px)' }">
    <template slot="title">
      <div style="display: flex; align-items: center; justify-content: space-between">
        <a-input-group compact style="width: 240px">
          <a-input
            disabled
            value="版本"
            style="
              width: 48px;
              --w-input-color-disabled: var(--w-text-color-dark);
              --w-input-background-color-disabled: var(--w-gray-color-2);
            "
          ></a-input>
          <a-select style="min-width: 120px" v-model="currentVersion.uuid" @change="onChangeVersion" :loading="loadingAllVersions">
            <a-select-option v-for="(ver, i) in prodVersions" :value="ver.uuid" :key="ver.uuid">
              <div style="display: flex; align-items: center">
                {{ ver.version }}
                <a-tag :color="statusMap[ver.status].color" class="tag-no-border" style="margin-left: 8px">
                  {{ statusMap[ver.status].label }}
                </a-tag>
              </div>
            </a-select-option>
          </a-select>
        </a-input-group>
        <div class="card-tabs">
          <label id="prodModuleLabel" @click="selectTab('ProductModule')" :class="selectTabKey == 'ProductModule' ? 'selected' : ''">
            系统模块
            <a-icon type="loading" v-if="loadingModules" style="margin-left: 3px" />
            <span v-else>({{ currentVersion.modules.length }})</span>
          </label>

          <!-- <a-divider type="vertical" /> -->
          <label
            id="prodPageSetLabel"
            @click="selectTab('ProductPageSetting')"
            :class="selectTabKey == 'ProductPageSetting' ? 'selected' : ''"
          >
            系统设置
          </label>
          <!-- <a-divider type="vertical" /> -->
          <label
            id="prodRolePrgLabel"
            @click="selectTab('ProductRolePrivilege')"
            :class="selectTabKey == 'ProductRolePrivilege' ? 'selected' : ''"
          >
            角色权限
          </label>
          <!-- <a-divider type="vertical" /> -->
          <label
            id="prodSysSetLabel"
            @click="selectTab('ProductSysSetting')"
            :class="selectTabKey == 'ProductSysSetting' ? 'selected' : ''"
          >
            系统管理
          </label>
        </div>
        <div>
          <a-button type="link" size="small" @click.stop="saveVersion" :loading="saveLoading">
            <Icon type="pticon iconfont icon-ptkj-baocun"></Icon>
            保存配置
          </a-button>
          <a-button type="link" :loading="previewLoading" size="small" @click="onClickToPreview">
            <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
            预览
          </a-button>
          <a-button type="link" icon="login" size="small" @click="onClickToLoginPreview">登录预览</a-button>
          <ExportDef title="导出产品版本配置" :uuid="settingUuid" type="appProdVersionSetting" modifyRange :fileName="'产品版本配置'">
            <a-button type="link" size="small" :disabled="settingUuid == undefined">
              <Icon type="pticon iconfont icon-luojizujian-yemiantiaozhuan"></Icon>
              导出配置
            </a-button>
          </ExportDef>
        </div>
      </div>
    </template>
    <div
      :key="tabComponentKey"
      :style="
        introducing
          ? 'position: relative;z-index: 10000000;background: #fff;top: -18px;border-radius: 6px;height: 100%; pointer-events: none;'
          : ''
      "
    >
      <keep-alive>
        <component :is="selectTabKey" v-bind="currentVersion" :loading="tabContentLoading" />
      </keep-alive>
    </div>
  </a-card>
</template>
<script type="text/babel">
import '../../css/assemble.less';
import ProductModule from './product-module.vue';
import ProductPageSetting from './product-page-setting.vue';
import ProductRolePrivilege from './product-role-privilege.vue';
import ProductSysSetting from './product-sys-setting.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';
export default {
  name: 'ProductVersionSetting',
  inject: ['pageContext'],
  props: {
    prodVersion: Object,
    introducing: Boolean
  },
  components: { ProductModule, ProductPageSetting, ProductRolePrivilege, ProductSysSetting, Modal, ExportDef, ImportDef },
  computed: {},
  data() {
    let currentVersion = {
      modules: [], // 系统模块ID
      setting: {
        uuid: undefined,
        // 版本配置
        title: undefined,
        icon: undefined,
        mapEnabled: false,
        pcIndexUrl: undefined,
        mobileIndexUrl: undefined,
        deviceAnon: '00',
        layoutConf: {}
      },
      anonUrls: [] //匿名地址
    };
    if (this.prodVersion != undefined) {
      currentVersion.uuid = this.prodVersion.uuid;
      currentVersion.prodId = this.prodVersion.prodId;
      currentVersion.versionId = this.prodVersion.versionId;
      currentVersion.version = this.prodVersion.version;
    }
    return {
      settingUuid: undefined,
      loadingAllVersions: true,
      originalModules: [],
      prodVersions: [],
      currentVersion,
      tabComponentKey: 'tabComponentKey_',
      statusMap: {
        BUILDING: {
          label: '设计中',
          color: 'blue'
        },
        PUBLISHED: {
          label: '已发布',
          color: 'green'
        },
        DEPRECATED: {
          label: '已废弃',
          color: undefined
        }
      },
      previewLoading: false,
      saveLoading: false,
      selectTabKey: 'ProductModule',
      prodModuleCount: 0,
      loadingModules: true,
      tabContentLoading: false
    };
  },

  beforeCreate() {},
  created() {},
  beforeMount() {
    this.refreshVersionSelect();
    if (this.currentVersion.uuid) {
      this.fetchVersionSetting(this.currentVersion.uuid).then(data => {
        this.settingUuid = data.uuid;
      });
      this.fetchVersionModules();
    }
  },
  mounted() {
    this.pageContext.handleEvent(`product:fetch:themeClass`, d => {
      this.themeClass = d;
    });
  },
  methods: {
    fetchVersionModules() {
      this.loadingModules = true;
      this.tabContentLoading = true;
      this.currentVersion.modules.splice(0, this.currentVersion.modules.length);
      this.originalModules.splice(0, this.originalModules.length);
      $axios
        .all([
          $axios.get(`/proxy/api/app/prod/version/underModules`, { params: { prodVersionUuid: this.currentVersion.uuid } }),
          $axios.get(`/proxy/api/app/prod/version/modules`, { params: { prodVersionUuid: this.currentVersion.uuid } })
        ])
        .then(
          $axios.spread((res1, res2) => {
            this.loadingModules = false;
            this.tabContentLoading = false;
            let exitModules = res1.data.data,
              validModules = res2.data.data;
            let validMap = {};
            for (let m of validModules) {
              validMap[m.id] = m;
              this.originalModules.push(m.id);
            }
            for (let e of exitModules) {
              let deleted = validMap[e.moduleId] == undefined,
                name = deleted ? e.moduleName : validMap[e.moduleId].name;
              let module = {
                moduleId: e.moduleId,
                moduleUuid: e.moduleUuid || validMap[e.moduleId].uuid,
                name,
                moduleName: name,
                deleted,
                icon: !deleted ? this.iconDataToJson(validMap[e.moduleId].icon) : undefined
              };

              this.currentVersion.modules.push(module);

              // this.selectedModules.push(module);
            }
            // this.fetchUnderModLoading = false;
          })
        )
        .catch(error => {
          // this.fetchUnderModLoading = false;
        });
    },

    onChangeVersion() {
      this.tabComponentKey = 'tabComponentKey' + new Date().getTime();
      this.currentVersion.versionId = this.prodVersionUuidMap[this.currentVersion.uuid].versionId;
      this.currentVersion.version = this.prodVersionUuidMap[this.currentVersion.uuid].version;
      this.currentVersion.modules = [];
      this.currentVersion.setting.prodVersionUuid = undefined;
      this.currentVersion.setting.pcIndexUrl = undefined;
      this.settingUuid = undefined;
      this.fetchVersionSetting(this.currentVersion.uuid).then(data => {
        this.settingUuid = data.uuid;
      });
      this.fetchVersionModules();
    },
    switchToVersion(uuid) {
      this.currentVersion.uuid = uuid;
      this.currentVersion.versionId = this.prodVersionUuidMap[uuid].versionId;
      this.onChangeVersion();
    },

    saveVersion() {
      let submitData = JSON.parse(JSON.stringify(this.currentVersion));
      if (submitData.setting) {
        if (submitData.setting.prodVersionUuid) {
          // 通过是否关联版本来判断，是否加载过前台主页数据
          if (submitData.setting.layoutConf) {
            submitData.setting.layoutConf = JSON.stringify(submitData.setting.layoutConf);
          }
          if (submitData.setting.theme) {
            // 主题配置保存
            for (let type in submitData.setting.theme) {
              let themeData = submitData.setting.theme[type];
              for (let i = 0; i < themeData.theme.length; i++) {
                delete themeData.theme[i].logo;
                if (themeData.theme[i].status == 'UNPUBLISHED' || !this.themeClass.includes(themeData.theme[i].themeClass)) {
                  if (!this.themeClass.includes(themeData.theme[i].themeClass)) {
                    this.currentVersion.setting.theme[type].theme.splice(i, 1);
                  }
                  themeData.theme.splice(i--, 1);
                  continue;
                }
                delete themeData.theme[i].status;
              }
            }
            submitData.setting.theme = JSON.stringify(submitData.setting.theme);
          }
        } else {
          delete submitData.setting; // 避免覆盖后端数据
        }
      }
      submitData.anonUrls = this.currentVersion.anonUrls;
      // 自动剔除被标记为不存在的模块
      let removeDeletedModule = list => {
        if (list) {
          for (let i = 0; i < list.length; i++) {
            if (list[i].deleted) {
              list.splice(i--, 1);
            }
          }
        }
      };
      removeDeletedModule(submitData.modules);
      this.saveLoading = true;
      $axios
        .post(`/proxy/api/app/prod/version/save`, submitData)
        .then(({ data }) => {
          this.saveLoading = false;
          if (data.code == 0) {
            this.$message.success('版本保存成功');
            removeDeletedModule(this.currentVersion.modules);
            this.pageContext.emitEvent(`${this.currentVersion.uuid}:saveVersionSuccess`);
            if (this.originalModules.length > 0) {
              let removed = [],
                ids = [];
              for (let m of this.currentVersion.modules) {
                ids.push(m.moduleId);
              }
              for (let i = 0; i < this.originalModules.length; i++) {
                if (!ids.includes(this.originalModules[i])) {
                  removed.push(...this.originalModules.splice(i--, 1));
                }
              }

              if (removed.length > 0) {
                $axios
                  .post(`/proxy/api/app/prod/version/removeUnusedProdModuleNestedRole/${this.currentVersion.uuid}`, removed)
                  .then(() => {
                    this.pageContext.emitEvent('product:fetch:roles');
                    $axios
                      .post(`/proxy/api/security/role/publishRoleUpdatedEvent?reloadAll=true`, {})
                      .then(({ data }) => {})
                      .catch(error => {});
                  });
              }
            }
            $axios.get(`/api/cache/deleteByKey`, {
              params: {
                key: [`APP_PROD_VERSION_SETTING:${this.currentVersion.uuid}`, `THEME:${this.currentVersion.uuid}`]
              }
            });
          }
        })
        .catch(error => {
          this.saveLoading = false;
          this.$message.error('保存失败');
        });

      let themes = [];
      if (submitData.setting.pageBindTheme) {
        for (let key in submitData.setting.pageBindTheme) {
          let pages = submitData.setting.pageBindTheme[key];
          for (let i = 0; i < pages.length; i++) {
            let p = pages[i];
            if (!this.themeClass.includes(p.theme.class)) {
              this.currentVersion.setting.pageBindTheme[key].splice(i, 1);
              continue;
            }
            themes.push({
              pageId: p.id,
              theme: JSON.stringify(p.theme)
            });
          }
        }

        $axios
          .post(`/proxy/api/app/prod/version/updateProdVersionRelaPageTheme/${this.currentVersion.uuid}`, themes)
          .then(({ data }) => {
            $axios
              .get(`/product/version/clearProdVersionBindPageThemeCache`, { params: { prodVersionUuid: this.currentVersion.uuid } })
              .then(({ data }) => {})
              .catch(error => {});
          })
          .catch(error => {});
      }
    },
    onClickToLoginPreview() {
      window.open(`/login/system/${this.currentVersion.prodId}/${this.currentVersion.uuid}`, '_blank');
    },
    onClickToPreview() {
      this.previewLoading = true;
      this.fetchPageDefinitions(this.currentVersion.uuid)
        .then(result => {
          window.open(`/webapp/${this.currentVersion.prodId}/${this.currentVersion.uuid}`, '_blank');
        })
        .catch(error => {
          this.$message.warning('请先配置系统首页！');
        });
    },
    fetchPageDefinitions(prodVersionUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get('/proxy/api/app/prod/version/pages', { params: { prodVersionUuid } })
          .then(({ data }) => {
            if (data.code == 0 && data.data && data.data.length > 0) {
              resolve(data);
            } else {
              reject();
            }
            this.previewLoading = false;
          })
          .catch(error => {
            reject();
            this.previewLoading = false;
          });
      });
    },
    fetchVersionSetting(versionUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/version/getSetting`, { params: { versionUuid } })
          .then(({ data }) => {
            if (data.code == 0) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    },

    selectTab(key) {
      this.selectTabKey = key;
    },
    refreshVersionSelect() {
      this.loadingAllVersions = true;
      this.fetchAllProdVersions(this.currentVersion.prodId).then(list => {
        this.prodVersions = list;
        this.prodVersionUuidMap = {};
        for (let p of this.prodVersions) {
          this.prodVersionUuidMap[p.uuid] = p;
        }
        this.loadingAllVersions = false;
      });
      this.fetchAllProdVersionAnonUrls(this.currentVersion.uuid).then(list => {
        this.currentVersion.anonUrls = list;
      });
    },
    fetchAllProdVersionAnonUrls(versionUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/version/getAnonUrls`, { params: { versionUuid } })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    },
    fetchAllProdVersions(prodId) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/version/getAll`, { params: { prodId } })
          .then(({ data }) => {
            if (data.code == 0) {
              resolve(data.data);
            }
          })
          .catch(error => {});
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
          let iconJson = JSON.parse(data);
          if (iconJson) {
            data = iconJson;
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
