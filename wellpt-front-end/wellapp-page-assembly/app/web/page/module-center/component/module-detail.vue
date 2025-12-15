<template>
  <!-- <a-card title="基本设置" :bordered="false" bodyStyle="padding:24px 40px"> -->
  <a-form-model
    :model="form"
    :label-col="labelCol"
    :wrapper-col="wrapperCol"
    :rules="rules"
    ref="form"
    :colon="false"
    class="base-info-form"
  >
    <a-form-model-item label="模块名称" prop="name">
      <a-input v-model="form.name" />
    </a-form-model-item>
    <a-form-model-item label="模块ID" prop="id">
      <a-input v-model="form.id" :disabled="form.uuid != undefined" @change="e => onInputId2CaseFormate(e, 'toLowerCase')" />
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="form.remark" />
    </a-form-model-item>
    <a-form-model-item label="图标">
      <WidgetIconLibModal
        v-if="form.icon && typeof form.icon == 'object'"
        v-model="form.icon.icon"
        :onlyIconClass="true"
        :container="container"
      />
    </a-form-model-item>
    <ColorSelectConfiguration
      v-if="form.icon && typeof form.icon === 'object'"
      label="图标背景颜色"
      v-model="form.icon"
      :onlyValue="true"
      colorField="bgColor"
      radioSize="small"
      radioStyle="solid"
      :popupContainer="popupContainer"
    ></ColorSelectConfiguration>
    <a-form-model-item label="模块分类">
      <AppCategorySelect apply-to="AppModule" v-model="form.categoryUuid" :enable-add="true" :popupContainer="popupContainer" />
    </a-form-model-item>

    <a-form-model-item label="标签">
      <DataTag apply-to="AppModule" :data-id="form.uuid == undefined ? undefined : form.id" v-model="form.tags" size="" />
    </a-form-model-item>

    <div v-if="allowSave" style="text-align: center">
      <a-button type="primary" @click="onSave">保存</a-button>
    </div>
  </a-form-model>
  <!-- </a-card> -->
</template>
<style lang="less"></style>
<script type="text/babel">
import { debounce } from 'lodash';
import moment from 'moment';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import { generateId, deepClone, findParentVNodeByName, getDocumentByNode } from '@framework/vue/utils/util';
import ColorSelectConfiguration from '@pageAssembly/app/web/widget/commons/color-select-configuration.vue';

import EWidgetLayoutConf from '../../../widget/widget-layout/configuration/index.vue';
import EWidgetMenuConf from '../../../widget/widget-menu/configuration/index.vue';

import DataTag from './data-tag.vue';
import AppCategorySelect from './app-category-select.vue';

import addEventListener from 'ant-design-vue/es/vc-util/Dom/addEventListener';

import '@pageAssembly/app/web/page/product-center/css/index.less';
export default {
  name: 'ModuleDetail',
  props: { detail: Object, allowSave: { type: Boolean, default: false }, container: Function },
  components: { WidgetIconLibModal, Modal, DataTag, AppCategorySelect, ColorSelectConfiguration },
  computed: {},
  data() {
    let form = {
        uuid: undefined,
        name: undefined,
        id: 'module_' + moment().format('yyyyMMDDHHmmss'),
        categoryUuid: undefined,
        remark: undefined,
        icon: {
          icon: '',
          bgColor: ''
        }
      },
      rules = { name: [{ required: true, message: '模块名称必填', trigger: 'blur' }] };
    if (this.detail != undefined) {
      if (this.detail.icon) {
        this.detail.icon = this.iconDataToJson(this.detail.icon);
      }
      Object.assign(form, this.detail);
    } else {
      rules.id = [
        { required: true, message: '模块ID', trigger: 'blur' },
        { trigger: ['blur', 'change'], validator: this.checkIdExist }
      ];
    }
    if (form.tags == undefined) {
      form.tags = [];
    }
    return {
      form,
      labelCol: { span: 5 },
      // idPrefix: 'module_',
      wrapperCol: { span: 15 },
      rules
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.form.uuid != undefined) {
      this.getModuleDetail(this.form.uuid).then(d => {
        this.form.id = d.id;
        this.form.name = d.name;
        this.form.remark = d.remark;
        this.form.categoryUuid = d.categoryUuid;
        this.form.icon = this.iconDataToJson(d.icon);
        this.form.enabled = d.enabled;
        this.form.system = d.system;
      });
    }
  },
  mounted() {},
  methods: {
    popupContainer(target) {
      if (EASY_ENV_IS_BROWSER) {
        if (window.self !== window.top) {
          const that = this;

          let vcTriggerComponent;
          if (target && target.__vue__) {
            vcTriggerComponent = findParentVNodeByName(target.__vue__, 'Trigger');
          }

          if (vcTriggerComponent) {
            vcTriggerComponent.updatedCal = function () {
              const props = this.$props;
              const state = this.$data;

              if (state.sPopupVisible) {
                let currentDocument;
                if (!this.clickOutsideHandler && (this.isClickToHide() || this.isContextmenuToShow())) {
                  currentDocument = getDocumentByNode(target);
                  this.clickOutsideHandler = addEventListener(currentDocument, 'mousedown', this.onDocumentClick);
                }
                // always hide on mobile
                if (!this.touchOutsideHandler) {
                  currentDocument = currentDocument || getDocumentByNode(target);
                  this.touchOutsideHandler = addEventListener(currentDocument, 'touchstart', this.onDocumentClick);
                }
                // close popup when trigger type contains 'onContextmenu' and document is scrolling.
                if (!this.contextmenuOutsideHandler1 && this.isContextmenuToShow()) {
                  currentDocument = currentDocument || getDocumentByNode(target);
                  this.contextmenuOutsideHandler1 = addEventListener(currentDocument, 'scroll', this.onContextmenuClose);
                }
                // close popup when trigger type contains 'onContextmenu' and window is blur.
                if (!this.contextmenuOutsideHandler2 && this.isContextmenuToShow()) {
                  this.contextmenuOutsideHandler2 = addEventListener(window, 'blur', this.onContextmenuClose);
                }
              } else {
                this.clearOutsideHandler();
              }
            };
          }
          if (target) {
            return getDocumentByNode(target).body;
            // return target.parentNode;
          } else {
            return window.top.document.body;
          }
        }
        return document.body;
      }
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
    },
    onInputId2CaseFormate(e, caseType) {
      if (this.form.id != undefined) {
        if (caseType === 'toUpperCase' || caseType === 'toLowerCase') {
          // 自动转大写
          this.form.id = this.form.id[caseType]();
          let start = e.target.selectionStart;
          this.$nextTick(() => {
            e.target.setSelectionRange && e.target.setSelectionRange(start, start);
          });
        }
      }
    },
    getModuleDetail(uuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/module/details/${uuid}`, { params: {} })
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {});
      });
    },
    checkIdExist: debounce(function (rule, value, callback) {
      $axios.get(`/proxy/api/app/module/${value}/exist`, { params: {} }).then(({ data }) => {
        if (data.code == 0) {
          callback(data.data ? 'ID重复' : undefined);
        } else {
          callback('服务异常');
        }
      });
    }, 500),
    generateModulePageDefinitionJson() {
      // 创建布局 + 导航组件
      let widget = {
          id: generateId(),
          name: '模块布局',
          title: '模块布局',
          wtype: 'WidgetLayout',
          main: true, // 标记为主布局
          configuration: EWidgetLayoutConf.configuration()
        },
        menuWidget = {
          id: generateId(),
          name: '模块导航',
          title: '模块导航',
          wtype: 'WidgetMenu',
          main: true, // 标记为主导航
          configuration: EWidgetMenuConf.configuration()
        };
      widget.configuration.footer.configuration.visible = false;
      widget.configuration.header.configuration.visible = false;
      widget.configuration.layoutType = 'topMiddleSiderBottom';
      menuWidget.configuration.menus = []; // 清空默认的菜单样例
      widget.configuration.sider.configuration.widgets = [menuWidget];

      let widgetElements = EWidgetLayoutConf.methods.getWidgetDefinitionElements(widget);
      widgetElements = widgetElements.concat(EWidgetMenuConf.methods.getWidgetDefinitionElements(menuWidget));
      return { widget, widgetElements };
    },

    createModulePage(id, wtype, name, moduleId, uuid, designable) {
      // TODO: 模块主页的布局：导航内容
      let { widget, widgetElements } = this.generateModulePageDefinitionJson();
      let pageDef = {
        id,
        uuid,
        name: name,
        isPc: wtype == 'vPage' ? '1' : '0',
        isDefault: true,
        wtype,
        appId: moduleId,
        designable: !!designable,
        appWidgetDefinitionElements: widgetElements,
        definitionJson: JSON.stringify({
          wtype: wtype,
          title: name,
          id,
          uuid,
          items: [widget],
          vars: {},
          js: undefined,
          style: {},
          appWidgetDefinitionElements: widgetElements,
          pageParams: []
        })
      };
      return new Promise((resolve, reject) => {
        $axios.post('/web/design/savePageDefinition', pageDef).then(({ data }) => {
          resolve();
        });
      });
    },
    createDefaultModulePrivilege(appId) {
      return new Promise((resolve, reject) => {
        // 模块ID 与 权限 进行捆绑用于判断模块是否有权限（该权限作为系统默认权限会在创建模块任一角色时候自动关联)
        // 因此，只要产品勾选了任一模块角色，则代表有该模块的访问权限
        $axios
          .post('/proxy/api/security/privilege/savePrivilegeResource', {
            appId,
            name: '模块访问权限',
            code: 'PRIVILEGE_MOD_' + appId.toUpperCase(),
            enabled: true,
            systemDef: 1,
            otherResources: [{ type: 'appModule', resourceUuid: appId }]
          })
          .then(({ data }) => {
            resolve(data.data);
          });
      });
    },
    saveModule(data, callback) {
      this.$loading('保存中');
      data = deepClone(data);
      data.icon = JSON.stringify(data.icon);

      if (data.uuid == null && this._$SYSTEM_ID) {
        data.system = this._$SYSTEM_ID;
        data.tenant = this._$USER.tenantId;
      }
      $axios
        .post(`/proxy/api/app/module/save`, data)
        .then(({ data }) => {
          this.$loading(false);
          if (data.code == 0) {
            this.$message.success('保存成功');
            this.$emit('createDone', true);
            if (this.detail != undefined) {
              this.detail.name = this.form.name;
              this.detail.icon = JSON.stringify(this.form.icon);
              this.detail.remark = this.form.remark;
            } else {
              // 生成模块主页
              this.createModulePage(generateId('SF'), 'vPage', this.form.name, this.form.id);
              //  生成模块默认权限，权限以模块ID作为资源标识捆绑，用于判断是否有访问该模块的权限
              this.createDefaultModulePrivilege(this.form.id);
              this.form.uuid = data.data;
            }

            if (typeof callback == 'function') {
              callback(JSON.parse(JSON.stringify(this.form)));
            }
          } else {
            this.$message.error('保存失败');
          }
        })
        .catch(() => {
          this.$loading(false);
        });
    },
    onSave(callback) {
      let _this = this;
      this.$refs.form.validate((passed, msg) => {
        if (passed) {
          _this.saveModule(_this.form, callback);
        }
      });
    }
    // idSuffix() {
    //   return this.form.id ? this.form.id.split(this.idPrefix)[1] : undefined;
    // },
    // mergeIdSuffix(e) {
    //   this.form.id = e.target.value == '' ? undefined : this.idPrefix + e.target.value;
    // }
  }
};
</script>
