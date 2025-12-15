import WidgetFormDevelopment from '@develop/WidgetFormDevelopment';
import { generateId } from '@framework/vue/utils/util';
import moment from 'moment';
import PageDetail from '@pageAssembly/app/web/page/product-center/component/manager/page-detail.vue';
import ProductPageList from '@pageAssembly/app/web/page/product-center/component/manager/product-page-list.vue';

class AppSystemPortalFormDevelopment extends WidgetFormDevelopment {
  saveAndDesign(e) {
    this.submitData(e).then(data => {
      window.open(
        data.wtype == 'vUniPage' ? `/uni-page-designer/index?uuid=${data.uuid}` : `/index-designer/${data.appId}?uuid=${data.uuid}`,
        '_blank'
      );
    });
  }
  submitData(e) {
    return new Promise((resolve, reject) => {
      let formData = e.form || this.$widget.form,
        $wgt = e.$widget;
      let submitData = {
        uuid: formData.UUID,
        name: formData.NAME,
        title: formData.TITLE,
        remark: formData.REMARK,
        code: formData.CODE,
        id: formData.ID,
        isPc: formData.WTYPE !== 'vUniPage' ? '1' : '0',
        wtype: formData.WTYPE || 'vPage',
        isDefault: false,
        isAnonymous: formData.IS_ANONYMOUS == 1,
        appId: this.getSystemID(),
        designable: true,
        layoutFixed: formData.LAYOUT_FIXED == 1,
        tenant: this.getUser().tenantId
      };
      if (formData.i18n) {
        let i18ns = [];
        for (let locale in formData.i18n) {
          for (let code in formData.i18n[locale]) {
            if (formData.i18n[locale][code]) {
              i18ns.push({
                locale: locale,
                content: formData.i18n[locale][code],
                defId: formData.id,
                code,
                applyTo: 'appPageDefinition'
              });
            }
          }
        }
        submitData.i18ns = i18ns;
      }
      let definitionJson = {
        wtype: submitData.wtype,
        title: submitData.title,
        id: submitData.id,
        items: [],
        vars: {},
        js: undefined,
        style: {
          enableBackground: false,
          backgroundColor: '#00000000'
        },
        pageParams: []
      };

      if (submitData.layoutFixed) {
        // 创建布局
        let { widget, widgetElements } = PageDetail.methods.generateProdLayoutPageDefinitionJson();
        definitionJson.items.push(widget);
        submitData.appWidgetDefinitionElements = widgetElements;
        definitionJson.appWidgetDefinitionElements = widgetElements;
      }

      submitData.definitionJson = JSON.stringify(definitionJson);

      $axios
        .post(`/proxy/api/webapp/page/definition/${submitData.uuid ? 'updateBasicInfo' : 'savePageDefinition'}`, submitData)
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.$widget.$message.success('保存成功');
            this.emitEvent(`mTbpVeyctqxBHPJHfdiZVxnpiGkAJdFH:closeModal`);
            this.emitEvent(`IaNXxsnxKHjSwmiCwFYfQiDBrhArsdPz:refetch`);
            this.emitEvent(`CWhTViwyCyteuRmZuhzMJdNhTIxFunlN:refetch`);
            if (submitData.uuid == undefined) {
              // 生成默认权限
              ProductPageList.methods.createDefaultPagePrivilege(submitData.id, submitData.appId, submitData.appId);
            }

            resolve({ uuid: submitData.uuid || data.data, appId: submitData.appId, wtype: submitData.wtype });
          }
        })
        .catch(error => {
          this.$widget.$message.error('保存失败');
        });
    });
  }
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
  }
  mounted() {
    if (this.$widget.$event.meta.UUID == undefined) {
      // 新建
      this.setField('LAYOUT_FIXED', '1');
      this.setField('ID', 'page_' + moment().format('yyyyMMDDHHmmss'));
    } else {
      if (this.$widget.$event.meta.LAYOUT_FIXED == '0') {
        // 可调交互页面无法变更首页类型
        this.setFieldReadOnly('LAYOUT_FIXED');
      }
      this.getPageDefinition(this.$widget.$event.meta.UUID).then(pageDefinition => {
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
          this.$widget.$set(this.$widget.form, 'i18n', i18n);
        }
      });
    }
  }

  get META() {
    return {
      name: '系统门户表单二开',
      hook: {
        submitData: '提交表单数据',
        saveAndDesign: '保存并配置'
      }
    };
  }
}
export default AppSystemPortalFormDevelopment;
