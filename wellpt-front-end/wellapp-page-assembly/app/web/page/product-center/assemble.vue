<template>
  <HtmlWrapper :title="vProdTitle">
    <div style="height: 100%; background-color: var(--w-primary-color-1)">
      <a-page-header class="prod-assemble-detail">
        <div class="prod-assemble-detail-bg">
          <div class="prod-assemble-detail-bg-mask"></div>
        </div>
        <template slot="title">
          <a-avatar :size="64">
            <Icon :type="product.icon || 'appstore'" slot="icon" />
          </a-avatar>
          <span class="title" :title="vProdTitle">{{ vProdTitle }}</span>
          <a-tag :color="statusMap[product.status].color" class="status-tag">{{ statusMap[product.status].label }}</a-tag>
          <a-tag v-for="(tag, i) in tags" :key="'prodTag_' + i" :title="tag.name" :color="tagsColors[i % 7]">{{ tag.name }}</a-tag>
        </template>
        <template slot="subTitle">
          <div class="w-ellipsis" style="max-width: 487px">{{ product.remark }}</div>
          <a-form layout="inline" :colon="false">
            <a-form-item label="编号">
              <div class="overflow-text">{{ product.code }}</div>
            </a-form-item>
            <a-form-item label="最新发布版本">
              <div class="overflow-text">
                {{ product.latestVersion ? (product.latestVersion.status === 'PUBLISHED' ? product.latestVersion.version : ' - ') : ' - ' }}
              </div>
            </a-form-item>
            <!-- <a-form-item label="产品负责人"><div class="overflow-text"></div></a-form-item>
            <a-form-item label="管理员"><div class="overflow-text"></div></a-form-item>
            <a-form-item label="查阅人员"><div class="overflow-text"></div></a-form-item> -->
          </a-form>
        </template>
        <template slot="extra">
          <a-switch
            v-if="product.status != 'BUILDING'"
            slot="tabBarExtraContent"
            :default-checked="product.status == 'LAUNCH'"
            checked-children="上线"
            un-checked-children="上线"
            :loading="launchLoading"
            @change="onChangeLaunch"
          />
          <a-button type="link" icon="desktop" size="small" @click="clickToSystemAdmin" v-show="product.status == 'LAUNCH'">
            系统管理后台
          </a-button>
          <a-button id="prodPubLaunchButton" type="link" size="small" @click="clickPublishLaunch" v-if="product.status == 'BUILDING'">
            <Icon type="pticon iconfont icon-ptkj-tijiaofabufasong"></Icon>
            发布上线
          </a-button>

          <Modal title="新建版本" :ok="onConfirmNewVersion">
            <a-button type="link" size="small" v-show="product.status == 'LAUNCH'">
              <Icon type="pticon iconfont icon-zwfw-xinjiangongcheng"></Icon>
              新建版本
            </a-button>
            <template slot="content">
              <a-form-model
                :colon="false"
                :model="newVersion"
                :label-col="labelCol"
                :wrapper-col="wrapperCol"
                :rules="rules"
                ref="newVersionForm"
              >
                <a-form-model-item label="版本号" prop="version"><a-input v-model.trim="newVersion.version"></a-input></a-form-model-item>
                <a-form-model-item label="配置数据来源">
                  <a-select v-model="newVersion.fromUuid" style="width: 100%" allow-clear>
                    <a-select-option v-for="(ver, i) in versions" :key="'version_select_option_' + i" :value="ver.uuid">
                      {{ ver.version }}
                    </a-select-option>
                  </a-select>
                </a-form-model-item>
              </a-form-model>
            </template>
          </Modal>
          <a-dropdown>
            <a-button type="link" icon="export" size="small">导入导出</a-button>
            <a-menu slot="overlay" @click="handleMenuExportClick">
              <a-menu-item key="import">版本导入</a-menu-item>
              <a-menu-item key="export">版本导出</a-menu-item>
            </a-menu>
          </a-dropdown>
          <!-- <Modal title="产品权限">
            <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-quanxian"></Icon>产品权限</a-button>
            <template slot="content">
              <a-form-model :label-col="labelCol" :wrapper-col="wrapperCol" ref="aclForm">
                <a-form-model-item label="产品负责人"></a-form-model-item>
                <a-form-model-item label="产品管理员"></a-form-model-item>
                <a-form-model-item label="查阅人员"></a-form-model-item>
              </a-form-model>
            </template>
          </Modal> -->
          <Modal title="编辑" :ok="onConformSaveProdBasic" :width="700">
            <a-button type="link" size="small">
              <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
              编辑
            </a-button>
            <template slot="content">
              <ProductBasicInfo :detail="product" ref="prodBasicInfo" :key="detailKey" />
            </template>
          </Modal>
        </template>
        <a-tabs class="product-version-tabs" type="card" v-model="activeTabKey" @change="onChangeTab">
          <a-tab-pane key="setting" tab="配置">
            <ProductVersionSetting
              :prodVersion="product.latestVersion"
              :key="prodVersionKey"
              ref="prodVersionSetting"
              :introducing="introducing"
            />
          </a-tab-pane>
          <a-tab-pane key="version" tab="版本">
            <ProductVersionTimeline
              :product="product"
              :key="prodVersionTmKey"
              @toProdVersionSetting="toProdVersionSetting"
              @deleteVersion="e => onDeleteVersion(e)"
            />
          </a-tab-pane>
          <a-tab-pane key="modifyLog" tab="动态"></a-tab-pane>
        </a-tabs>
        <Intro :steps="introSteps" functionId="prodAssemble" @beforechange="onIntroBeforeChange" @exit="onIntroExit" />

        <ExportDef
          ref="exportDef"
          :title="exportDef.title"
          :uuid="exportDef.uuid"
          :type="exportDef.type"
          modifyRange
          :fileName="exportDef.fileName"
        ></ExportDef>
        <ImportDef ref="importDef" title="导入产品版本" />
      </a-page-header>
    </div>
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import './css/assemble.less';
import ProductVersionSetting from './component/manager/product-version-setting.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import ProductBasicInfo from './component/manager/product-basic-info.vue';
import ProductPageSetting from './component/manager/product-page-setting.vue';
import ProductVersionTimeline from './component/manager/product-version-timeline.vue';
import Intro from '@pageAssembly/app/web/lib/intro.vue';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';
import { debounce } from 'lodash';

export default {
  name: 'ProductAssemble',
  props: {},
  components: { ExportDef, ImportDef, ProductVersionSetting, Modal, ProductBasicInfo, ProductVersionTimeline, Intro },
  computed: {
    vProdTitle() {
      return this.product.name + (this.product.versionName ? ' - ' + this.product.versionName : '');
    }
  },
  data() {
    let introSteps = [
      {
        intro: '选择当前产品包含的功能模块，构成产品的主体结构。缺少可选模块时可到装配中心进行搭建',
        selector: '#prodModuleLabel'
      },
      {
        intro: '配置产品的登录页、首页等面向用户的前台页面，作为产品的用户交互入口。同时可配置产品整体的导航布局和交互',
        selector: '#prodPageSetLabel'
      },
      {
        intro: '创建当前产品的业务角色，并初步进行角色的功能模块授权',
        selector: '#prodRolePrgLabel'
      },
      {
        intro: '配置产品的管理后台页面，将运维管理功能开放给系统管理员',
        selector: '#prodSysSetLabel'
      }
    ];

    return {
      activeTabKey: 'setting',
      launchLoading: false,
      tags: [],
      versions: [],
      newVersion: {
        fromUuid: undefined,
        version: undefined
      },
      prodVersionKey: 'prodVersion_',
      prodVersionTmKey: 'prodVersionTm_',
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      ACL_TYPE: { DIRECTOR: 'DIRECTOR', MANAGER: 'MANAGER', VIEWER: 'VIEWER' },
      acls: {
        DIRECTOR: [],
        MANAGER: [],
        VIEWER: []
      },
      statusMap: {
        BUILDING: {
          label: '构建中',
          color: '#1890ff'
        },
        LAUNCH: {
          label: '已上线',
          color: 'var(--w-success-color)'
        },
        NOT_LAUNCH: {
          label: '已下线',
          color: undefined
        }
      },
      tagsColors: ['pink', 'red', 'orange', 'blue', 'green', 'cyan', 'purple'],
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      rules: {
        version: [
          { required: true, message: '版本号必填', trigger: 'blur' },
          { message: '版本号唯一', trigger: ['blur', 'change'], validator: this.versionValidate }
        ]
      },
      introSteps,
      introducing: false,
      detailKey: new Date().getTime(),
      exportDef: {
        uuid: undefined,
        title: undefined,
        type: 'appProdVersion',
        fileName: undefined
      }
    };
  },

  beforeCreate() {},
  created() {
    if (this.product.status == 'BUILDING') {
      this.introSteps.push({
        intro: '完成产品构建和调试后，将产品发布上线',
        selector: '#prodPubLaunchButton'
      });
    }
  },
  beforeMount() {
    this.fetchTags();
    this.refreshVersions();
    this.fetchAcl();
  },
  mounted() {
    if (this.product.latestVersion == null) {
      this.createNewVersion({
        version: 'v1.0',
        prodId: this.product.id
      }).then(data => {
        this.$set(this.product, 'latestVersion', data);
        this.prodVersionKey = 'prodVersion_' + new Date();
        this.prodVersionTmKey = 'prodVersionTm_' + new Date();
      });
    }
    // let x = null;
    // introduce('prodAssembleIntroduce', {}, null, inst => {
    //   console.log(inst);
    // }).then(inst => {
    //   x = inst;
    //   console.log('初始化介绍功能结束', inst);
    // });
  },
  methods: {
    clickToSystemAdmin() {
      // if (this.product.status != 'LAUNCH') {
      //   this.$message.info('请先发布上线产品');
      //   return;
      // }
      window.open(`/system_admin/${this.product.id}/index`, '_blank');
    },
    onDeleteVersion(versionUuid) {
      this.refreshVersions().then(() => {
        let currentVersion = this.$refs.prodVersionSetting.currentVersion;
        if (currentVersion.uuid == versionUuid) {
          this.$refs.prodVersionSetting.switchToVersion(this.versions[0].uuid);
          this.$refs.prodVersionSetting.refreshVersionSelect(this.versions[0].uuid);
        }
      });
    },
    versionValidate: debounce(function (r, v, callback) {
      $axios.get(`/proxy/api/app/prod/version/exist`, { params: { version: v, prodId: this.product.id } }).then(({ data }) => {
        if (data.code == 0) {
          callback(data.data ? '版本号重复' : undefined);
        } else {
          callback('服务异常');
        }
      });
    }, 300),

    handleMenuExportClick(e) {
      if (e.key == 'import') {
        this.$refs.importDef.show();
      } else if (e.key == 'export') {
        this.exportDef.uuid = this.$refs.prodVersionSetting.currentVersion.uuid;
        this.exportDef.title = '导出产品版本: ' + this.$refs.prodVersionSetting.currentVersion.version;
        this.exportDef.fileName = `产品版本: ${this.product.name}(${this.$refs.prodVersionSetting.currentVersion.version})`;
        this.$refs.exportDef.show();
      }
    },
    onIntroExit() {
      this.introducing = false;
    },
    onIntroBeforeChange(e) {
      this.introducing = false;
      if (e.getAttribute('id') != 'prodPubLaunchButton') {
        this.introducing = true;
        e.click();
      }
    },
    onChangeTab(key) {
      this.activeTabKey = key;
    },
    toProdVersionSetting(versionUuid) {
      this.$refs.prodVersionSetting.switchToVersion(versionUuid);
      this.activeTabKey = 'setting';
    },
    onConformSaveProdBasic(e) {
      this.$refs.prodBasicInfo.saveProduct().then(data => {
        if (data) {
          this.$message.success('保存成功');
          this.product.name = data.name;
          this.product.versionName = data.versionName;
          this.product.remark = data.remark;
          this.product.code = data.code;
          this.product.icon = data.icon;
          this.product.modifyTime = data.modifyTime;
          this.product.categoryUuid = data.categoryUuid;
          this.detailKey = new Date().getTime();
          this.fetchTags();
          e(true);
        } else {
          this.$message.error('保存异常');
        }
      });
    },
    createNewVersion(formData) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/version/new`, {
            params: formData
          })
          .then(({ data }) => {
            if (data.code == 0) {
              resolve(data.data);
            } else {
              reject();
            }
          })
          .catch(error => {
            reject();
          });
      });
    },
    onConfirmNewVersion(e) {
      let _this = this;
      this.$loading('创建新版本中');
      this.$refs.newVersionForm.validate((vali, msg) => {
        if (vali) {
          _this
            .createNewVersion({
              version: _this.newVersion.version,
              sourceUuid: _this.newVersion.fromUuid,
              prodId: _this.product.id
            })
            .then(data => {
              let _continue = () => {
                _this.newVersion.fromUuid = undefined;
                _this.newVersion.version = undefined;
                _this.$message.success('保存新版本成功');
                _this.product.latestVersion = data;
                _this.prodVersionKey = 'prodVersion_' + new Date();
                _this.prodVersionTmKey = 'prodVersionTm_' + new Date();
                _this.$loading(false);
                e(true);
              };
              if (_this.newVersion.fromUuid == undefined) {
                _this.generateDefaultProdVSetting(data.uuid, data.versionId, _this.product.name).then(() => {
                  _continue();
                });
              } else {
                _continue();
              }
            })
            .catch(() => {
              _this.$message.error('保存新版本异常');
            });
        } else {
          _this.$loading(false);
        }
      });
    },
    generateDefaultProdVSetting(uuid, id, name) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/app/prod/version/saveVersionSetting`, {
            prodVersionUuid: uuid,
            prodVersionId: id,
            layoutConf: JSON.stringify(ProductPageSetting.methods.defaultLayoutConf()), // 默认导航布局
            deviceAnon: '00',
            title: `首页 - ${name}`
          })
          .then(({ data }) => {
            resolve();
          })
          .catch(error => {});
      });
    },
    refreshVersions() {
      return new Promise((resolve, reject) => {
        ProductVersionSetting.methods.fetchAllProdVersions(this.product.id).then(list => {
          this.versions.splice(0, this.versions.length);
          this.versions.push(...list);
          resolve();
        });
      });
    },
    updateProdLaunch(launch) {
      let status = launch ? 'LAUNCH' : 'NOT_LAUNCH';
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/updateStatus`, { params: { uuid: this.product.uuid, status } })
          .then(({ data, headers }) => {
            if (data.code == 0) {
              this.product.modifyTime = new Date(headers.date).format('yyyy-MM-DD HH:mm:ss');
              this.product.status = status;
              this.detailKey = new Date().getTime();
              resolve();
            }
          })
          .catch(error => {});
      });
    },
    onChangeLaunch(checked) {
      this.launchLoading = true;
      this.updateProdLaunch(checked).then(() => {
        this.launchLoading = false;
        this.$message.success((checked ? '上线' : '下线') + '成功');
        this.product.status == checked ? 'LAUNCH' : 'NOT_LAUNCH';
      });
    },
    clickPublishLaunch() {
      this.updateProdLaunch(true).then(() => {
        $axios
          .get(`/proxy/api/app/prod/version/updateStatus`, { params: { uuid: this.product.latestVersion.uuid, status: 'PUBLISHED' } })
          .then(({ data }) => {
            if (data.code == 0) {
              this.$message.success('发布上线成功');
              this.product.latestVersion.status = 'PUBLISHED';
              this.$refs.prodVersionSetting.refreshVersionSelect();
            }
          })
          .catch(error => {});
      });
    },

    fetchAcl() {
      $axios
        .get(`/proxy/api/app/prod/acl/get`, { params: { prodId: this.product.id } })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
          }
        })
        .catch(error => {});
    },
    fetchTags() {
      $axios
        .get(`/proxy/api/app/tag/queryDataTag`, { params: { dataId: this.product.id } })
        .then(({ data }) => {
          if (data.code == 0) {
            this.tags = data.data;
          }
        })
        .catch(error => {});
    }
  }
};
</script>
