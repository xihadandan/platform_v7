<template>
  <a-form-model
    :model="form"
    :label-col="labelCol"
    :wrapper-col="wrapperCol"
    :rules="rules"
    ref="form"
    :colon="false"
    class="base-info-form"
  >
    <a-form-model-item label="产品名称" prop="name">
      <a-input v-model="form.name" />
    </a-form-model-item>
    <a-form-model-item label="产品ID" prop="id">
      <span v-if="form.uuid != undefined">{{ form.id }}</span>
      <a-input v-model="form.id" v-else @change="e => onInputId2CaseFormate(e, 'toLowerCase')" />
    </a-form-model-item>
    <a-form-model-item label="产品版本">
      <a-auto-complete :data-source="['标准版', '专业版', '企业版']" v-model.trim="form.versionName" allow-clear />
    </a-form-model-item>
    <a-form-model-item label="产品分类" prop="categoryUuid">
      <AppCategorySelect apply-to="AppProduct" v-model="form.categoryUuid" enable-add />
    </a-form-model-item>
    <a-form-model-item label="编号" prop="code">
      <a-input v-model="form.code" />
    </a-form-model-item>
    <a-form-model-item label="标签">
      <DataTag apply-to="AppProduct" :data-id="form.id" v-model="form.tags" size="default" />
    </a-form-model-item>
    <a-form-model-item label="LOGO">
      <WidgetIconLibModal v-model="form.icon" :container="getTopContainer" :onlyIconClass="true"></WidgetIconLibModal>
    </a-form-model-item>
    <template v-if="form.uuid != undefined">
      <a-form-model-item label="描述">
        <a-textarea v-model="form.remark" :autosize="{ minRows: 3, maxRows: 3 }" />
      </a-form-model-item>
      <a-form-model-item label="状态">
        <a-tag :color="statusMap[form.status].color">{{ statusMap[form.status].label }}</a-tag>
      </a-form-model-item>
      <a-form-model-item label="创建时间">{{ form.createTime }}</a-form-model-item>
      <a-form-model-item label="修改时间">{{ form.modifyTime }}</a-form-model-item>
    </template>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import moment from 'moment';
import { debounce } from 'lodash';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import DataTag from '../../../module-center/component/data-tag.vue';
import AppCategorySelect from '../../../module-center/component/app-category-select.vue';
import BasicConfig from '../../../login/component/login-basic/config.vue';
import ProductPageSetting from './product-page-setting.vue';
import SystemAdminInit from '../../system-admin-init.vue';
import { getTopContainer } from '@framework/vue/utils/function.js';
import '@pageAssembly/app/web/page/product-center/css/index.less';
export default {
  name: 'ProductBasicInfo',
  props: {
    detail: Object
  },
  components: { Modal, WidgetIconLibModal, DataTag, AppCategorySelect },
  computed: {},
  data() {
    let form = { id: 'prod_' + moment().format('yyyyMMDDHHmmss'), tags: [] },
      rules = {
        name: [{ required: true, message: '产品名称必填', trigger: 'blur' }]
      };
    if (this.detail != undefined) {
      form = { ...this.detail, tags: [] };
    } else {
      rules.id = [
        { required: true, message: '产品ID', trigger: 'blur' },
        { trigger: ['blur', 'change'], validator: this.checkIdExist }
      ];
    }
    return {
      form,
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      rules,
      statusMap: {
        BUILDING: {
          label: '构建中',
          color: 'orange'
        },
        LAUNCH: {
          label: '已上线',
          color: 'green'
        },
        NOT_LAUNCH: {
          label: '已下线',
          color: undefined
        }
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    getTopContainer,
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
    saveProduct() {
      return new Promise((resolve, reject) => {
        this.$refs.form.validate(valid => {
          if (valid) {
            $axios
              .post(`/proxy/api/app/prod/save`, { ...this.form })
              .then(({ data }) => {
                if (data.code == 0) {
                  this.form.modifyTime = data.data.modifyTime;
                  if (this.form.uuid == undefined) {
                    this.createDefaultProdVersionLogin(this.form, data.data);
                    this.createDefaultVersionSetting(this.form, data.data);
                    this.createDefaultTenantSystemInfo(this.form, data.data);
                  }
                  resolve(this.form);
                } else {
                  reject();
                }
              })
              .catch(error => {
                reject();
              });
          } else {
            reject();
          }
        });
      });
    },
    createDefaultTenantSystemInfo(form, product) {
      // 默认租户下，自动授权生成系统信息关系
      if (this._$USER.tenantId == 'T001') {
        SystemAdminInit.methods.init(form.id, this._$USER.tenantId);
      }
    },
    createDefaultProdVersionLogin(form, product) {
      $axios
        .post(`/proxy/api/system/saveLoginPage`, {
          title: form.name,
          name: '默认登录页',
          prodVersionUuid: product.latestVersion.uuid,
          isDefault: true,
          isPc: true,
          defJson: JSON.stringify({
            type: 'LoginBasic',
            config: BasicConfig.methods.generateDefaultConfig()
          })
        })
        .then(({ data }) => {})
        .catch(error => {});
    },
    createDefaultVersionSetting(form, product) {
      $axios
        .post(`/proxy/api/app/prod/version/saveVersionSetting`, {
          prodVersionUuid: product.latestVersion.uuid,
          layoutConf: JSON.stringify(ProductPageSetting.methods.defaultLayoutConf()), // 默认导航布局
          deviceAnon: '00',
          title: `首页 - ${form.name}`
        })
        .then(({ data }) => {})
        .catch(error => {});
    },
    checkIdExist: debounce(function (rule, value, callback) {
      $axios.get(`/proxy/api/app/prod/${value}/exist`, { params: {} }).then(({ data }) => {
        if (data.code == 0) {
          callback(data.data ? 'ID重复' : undefined);
        } else {
          callback('服务异常');
        }
      });
    }, 500)
  }
};
</script>
