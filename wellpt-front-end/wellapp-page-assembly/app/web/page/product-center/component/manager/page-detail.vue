<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="form.name">
        <template slot="addonAfter">
          <WI18nInput code="name" :target="form" v-model="form.name" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="ID" prop="id">
      <a-input v-model="form.id" v-if="form.uuid == undefined" />
      <span v-else>{{ form.id }}</span>
    </a-form-model-item>
    <a-form-model-item label="显示标题">
      <a-input v-model="form.title">
        <template slot="addonAfter">
          <WI18nInput code="title" :target="form" v-model="form.title" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="统一导航布局">
      <a-switch v-model="form.layoutFixed" :disabled="!form.layoutFixed && form.uuid != undefined" />
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="form.remark" />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import moment from 'moment';
import { generateId } from '@framework/vue/utils/util';
import EWidgetLayoutConfig from '../../../../widget/widget-layout/configuration/index.vue';
import EWidgetMenuConfig from '../../../../widget/widget-menu/configuration/index.vue';
import { debounce } from 'lodash';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'PageDetail',
  props: { detail: Object, isPc: Boolean, appId: String, prodVersionId: String, prodVersionUuid: String },
  components: { WI18nInput },
  computed: {},
  data() {
    let form = { id: 'page_' + moment().format('yyyyMMDDHHmmss') },
      rules = {
        name: [{ required: true, message: '名称必填', trigger: 'blur' }]
      };
    if (this.detail != undefined) {
      Object.assign(form, this.detail);
    }
    if (form.uuid == undefined) {
      rules.id = [
        { required: true, message: 'ID必填', trigger: 'blur' },
        { trigger: ['blur', 'change'], validator: this.checkIdExist }
      ];
      form.layoutFixed = true;
    }

    return {
      form,
      rules,
      labelCol: { span: 4 },
      wrapperCol: { span: 16 }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.detail.uuid) {
      this.getPageDefinition(this.detail.uuid).then(pageDefinition => {
        if (pageDefinition.i18ns) {
          let i18n = {};
          for (let item of pageDefinition.i18ns) {
            if (i18n[item.locale] == undefined) {
              i18n[item.locale] = {};
            }
            if (item.elementId == null) {
              i18n[item.locale][item.code] = item.content;
            }
          }
          this.form.name = pageDefinition.name;
          this.form.title = pageDefinition.title;
          this.form.remark = pageDefinition.remark;
          this.$set(this.form, 'i18n', i18n);
        }
      });
    }
  },
  mounted() {},
  methods: {
    getPageDefinition(uuid) {
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'appPageDefinitionMgr',
            methodName: 'getBean',
            args: JSON.stringify([uuid])
          })
          .then(({ data }) => {
            if (data.code == 0) {
              let pageDefinition = data.data;
              resolve(pageDefinition);
            } else {
              reject();
            }
          })
          .catch(() => {
            reject();
          });
      });
    },
    checkIdExist: debounce(function (rule, value, callback) {
      $axios
        .get(`/proxy/api/webapp/page/definition/existId`, {
          params: {
            id: value
          }
        })
        .then(({ data }) => {
          if (data.code == 0) {
            callback(data.data ? 'ID重复' : undefined);
          } else {
            callback('服务异常');
          }
        });
    }, 300),
    save() {
      return new Promise((resolve, reject) => {
        const commit = () => {
          let submitData = {
            ...this.form,
            isPc: this.isPc ? '1' : '0',
            wtype: this.isPc ? 'vPage' : 'vUniPage',
            isDefault: false,
            appId: this.appId, // 页面归属产品（与产品版本只是存在关联关系）
            designable: true
          };
          let definitionJson = {
            wtype: this.isPc ? 'vPage' : 'vUniPage',
            title: this.form.title,
            id: this.form.id,
            items: [],
            vars: {},
            js: undefined,
            style: {
              enableBackground: false,
              backgroundColor: '#00000000'
            },
            pageParams: []
          };

          if (this.form.layoutFixed) {
            // 创建布局
            let { widget, widgetElements } = this.generateProdLayoutPageDefinitionJson();
            definitionJson.items.push(widget);
            submitData.appWidgetDefinitionElements = widgetElements;
            definitionJson.appWidgetDefinitionElements = widgetElements;
          }

          submitData.definitionJson = JSON.stringify(definitionJson);
          if (this.form.i18n) {
            let i18ns = [];
            for (let locale in this.form.i18n) {
              for (let key in this.form.i18n[locale]) {
                if (this.form.i18n[locale][key]) {
                  i18ns.push({
                    locale: locale,
                    content: this.form.i18n[locale][key],
                    defId: this.form.id,
                    code: key,
                    applyTo: 'appPageDefinition'
                  });
                }
              }
            }
            this.form.i18ns = i18ns;
          }

          $axios
            .post(`/proxy/api/webapp/page/definition/${submitData.uuid ? 'updateBasicInfo' : 'savePageDefinition'}`, submitData)
            .then(({ data }) => {
              if (data.code == 0 && data.data) {
                if (!submitData.uuid) {
                  this.updateProdVersionRelaPage(this.prodVersionUuid, data.data);
                }
                resolve({
                  uuid: data.data,
                  id: submitData.id,
                  name: submitData.name,
                  remark: submitData.remark,
                  appId: submitData.appId,
                  layoutFixed: submitData.layoutFixed,
                  isPc: this.isPc ? '1' : '0'
                });
              }
            })
            .catch(error => {});
        };
        let _this = this;
        this.$refs.form.validate((passed, msg) => {
          if (passed) {
            commit.call(_this);
          }
        });
      });
    },
    updateProdVersionRelaPage(prodVersionUuid, pageUuid) {
      // 更新产品版本对应的页面关系
      $axios
        .get(`/proxy/api/app/prod/version/updateProdVersionPage`, {
          params: { prodVersionUuid, pageUuid }
        })
        .then(({ data }) => {})
        .catch(error => {});
    },
    generateProdLayoutPageDefinitionJson() {
      // 创建布局 + 导航组件
      let widget = {
          id: generateId(),
          name: '首页布局',
          title: '首页布局',
          wtype: 'WidgetLayout',
          main: true,
          configuration: EWidgetLayoutConfig.configuration()
        },
        menuWidget = {
          id: generateId(),
          name: '首页导航',
          title: '首页导航',
          wtype: 'WidgetMenu',
          main: true,
          configuration: EWidgetMenuConfig.configuration()
        };
      widget.configuration.prodVersionId = this.prodVersionId;
      widget.configuration.logoPositionSelfControl = false; // 默认logo位置的控制由导航布局的设置决定
      widget.configuration.footer.configuration.visible = false;
      widget.configuration.header.configuration.visible = true;
      widget.configuration.layoutType = 'siderTopMiddleBottom';
      widget.configuration.sider.configuration.widgets = [menuWidget];

      widget.configuration.logoPosition = 'sider';
      widget.configuration.header.configuration.backgroundColorType = undefined;
      widget.configuration.sider.configuration.backgroundColorType = undefined;

      let widgetElements = EWidgetLayoutConfig.methods.getWidgetDefinitionElements(widget);
      widgetElements = widgetElements.concat(EWidgetMenuConfig.methods.getWidgetDefinitionElements(menuWidget));
      return { widget, widgetElements };
    }
  }
};
</script>
