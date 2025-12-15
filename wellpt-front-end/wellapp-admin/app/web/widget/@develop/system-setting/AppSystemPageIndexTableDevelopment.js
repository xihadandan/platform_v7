import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import { generateId, copyToClipboard } from '@framework/vue/utils/util';
import moment from 'moment';
import ProductPageList from '@pageAssembly/app/web/page/product-center/component/manager/product-page-list.vue'
import { debounce } from 'lodash';
import Modal from '@pageAssembly/app/web/lib/modal.vue';

class AppSystemPageIndexTableDevelopment extends WidgetTableDevelopment {

  beforeMount() {
    this.addDataSourceParams({
      system: this.getSystemID()
    })
  }

  enablePage(e) {
    return new Promise((resolve, reject) => {
      $axios.get(`/proxy/api/webapp/page/definition/updateEnabled`, {
        params: {
          uuid: e.row.UUID, enabled: e.row.ENABLED != 1
        }
      }).then(({ data }) => {
        resolve(true);
        this.$widget.$message.success(`${e.row.ENABLED == 1 ? '停用' : '启用'}成功`);
      }).catch(error => {
        this.$widget.$message.success(`${e.row.ENABLED == 1 ? '停用' : '启用'}失败`);
        reject();
      })
    })
  }
  updateAnnoymous(e) {
    return new Promise((resolve, reject) => {
      $axios.get(`/proxy/api/webapp/page/definition/updateAnonymous`, {
        params: {
          uuid: e.row.UUID, anonymous: e.row.IS_ANONYMOUS != 1
        }
      }).then(({ data }) => {
        resolve(true);
        this.$widget.$message.success(`${e.row.IS_ANONYMOUS == 1 ? '禁止' : '允许'}匿名访问成功`);
      }).catch(error => {
        this.$widget.$message.success(`${e.row.IS_ANONYMOUS == 1 ? '禁止' : '允许'}匿名访问失败`);
        reject();
      })
    })
  }

  deleteRows(e) {
    let _this = this, selectedRows = e.meta.selectedRows;
    let ids = [];
    if (selectedRows != undefined) {
      for (let i = 0, len = selectedRows.length; i < len; i++) {
        ids.push(selectedRows[i].ID);
      }
    } else {
      ids = [e.meta.ID];
    }
    if (ids.length > 0) {
      this.$widget.$confirm({
        title: '确定要删除吗?',
        onOk() {
          _this.$widget.$loading();
          $axios
            .post(`/proxy/api/webapp/page/definition/deletePageByIds`, ids)
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$widget.$message.success('删除成功');
                _this.$widget.$loading(false);
                _this.refetch()
              } else {
                _this.$widget.$message.success('删除异常');
              }
            })
            .catch(error => {
              _this.$widget.$message.success('删除异常');
            });


        }
      })
    }


  }

  exportData(e) {
    let _this = this, selectedRowKeys = e.meta.UUID != undefined ? [e.meta.UUID] : e.meta.selectedRowKeys;
    if (selectedRowKeys.length > 0) {
      import('@pageAssembly/app/web/lib/eximport-def/export-def.vue').then(m => {
        m.default.propsData = {
          uuid: selectedRowKeys,
          type: 'appPageDefinition',
          title: '导出'
        }
        let inst = _this.getPageContext().newVueComponent(m.default);
        let mounted = _this.getPageContext().mountAsChild(inst, _this.$widget.$el, false);
        mounted.show();
      })
    }
  }

  importData(e) {
    let _this = this;
    import('@pageAssembly/app/web/lib/eximport-def/import-def.vue').then(m => {
      m.default.propsData = {
        filterType: 'appPageDefinition',
        title: '导入'
      }
      let inst = _this.getPageContext().newVueComponent(m.default);
      inst.visible = true;
      _this.getPageContext().mountAsChild(inst, _this.$widget.$el, false)
    })

  }


  copyPage(e) {
    // this.$widget.$loading('复制中...');
    let _this = this;
    const copyPageFrom = {
      uuid: e.meta.UUID,
      fromId: e.meta.ID,
      fromName: e.meta.NAME,
      id: 'page_' + moment().format('yyyyMMDDHHmmss'),
      name: e.meta.NAME + ' - 副本'
    }
    this.getPageContext().mountVueComponentAsChild({
      template: `
        <Modal  :ok="confirmCopyPage"  :destroyOnClose="true"   v-model="copyPageVisible" :title="'复制页面: ' + copyPageFrom.fromName + '(' + copyPageFrom.fromId + ')'">
        <template slot="content">
          <a-form-model
            :model="copyPageFrom"
            :rules="copyPageRules"
            ref="copyPage"
            :colon="false"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 19 }"
          >
            <a-form-model-item prop="name" label="名称">
              <a-input v-model.trim="copyPageFrom.name" />
            </a-form-model-item>
            <a-form-model-item prop="id" label="ID">
              <a-input :maxLength="120" v-model.trim="copyPageFrom.id" />
            </a-form-model-item>
          </a-form-model>
        </template>
      </Modal>
        `,
      components: { Modal },
      data() {
        return {
          copyPageVisible: true,
          copyPageFrom,
          copyPageRules: {
            name: [{ required: true, trigger: ['blur'], message: '必填' }],
            id: [
              { required: true, trigger: ['blur', 'change'], message: '必填' },
              {
                pattern: /^\w+$/,
                message: 'ID只允许包含字母、数字以及下划线',
                trigger: ['blur', 'change']
              },
              { trigger: ['blur', 'change'], validator: this.validatePageId }
            ]
          },
        }
      },
      methods: {
        confirmCopyPage() {
          this.$refs.copyPage.validate(pass => {
            if (pass) {
              _this.$widget.$loading('复制中...');
              this.copyPageVisible = false;
              $axios
                .post('/json/data/services', {
                  serviceName: 'appPageDefinitionService',
                  methodName: 'get',
                  args: JSON.stringify([this.copyPageFrom.uuid])
                })
                .then(({ data }) => {
                  if (data.code == 0) {
                    let pageDefinition = data.data;
                    pageDefinition.uuid = undefined;
                    pageDefinition.name = this.copyPageFrom.name;
                    let jsonSource = JSON.parse(pageDefinition.definitionJson);
                    delete jsonSource.id;
                    let json = jsonSource;
                    json.uuid = undefined;
                    json.title = pageDefinition.name;
                    json.id = this.copyPageFrom.id;
                    pageDefinition.appId = _this.getSystemID();
                    pageDefinition.tenant = _this.getTenantID();
                    pageDefinition.id = json.id;
                    pageDefinition.definitionJson = JSON.stringify(json);
                    pageDefinition.appWidgetDefinitionElements = json.appWidgetDefinitionElements;
                    pageDefinition.functionElements = json.functionElements;
                    $axios
                      .post('/web/design/savePageDefinition', pageDefinition)
                      .then(({ data }) => {
                        _this.$widget.$loading(false);
                        if (data.code == 0) {
                          pageDefinition.uuid = data.data;
                          ProductPageList.methods.createDefaultPagePrivilege(pageDefinition.id, pageDefinition.appId);
                          _this.refetch();
                        }
                      })
                      .catch(() => {
                        _this.$widget.$loading(false);
                      });
                  }
                })
                .catch(() => {
                  _this.$widget.$loading(false);
                });

            }
          });
        },
        validatePageId: debounce(function (rule, value, callback) {
          $axios
            .get('/proxy/api/webapp/page/definition/existId', {
              params: { id: value }
            })
            .then(({ data }) => {
              callback(data.data === false ? undefined : '页面ID已存在');
            });
        }, 300),
      }
    }, '#app', false);
  }
  copyAnonymousUrl(e) {
    copyToClipboard(`${location.origin}/sys-public/${e.meta.APP_ID}/index?pageId=${e.meta.ID}`, e.$evt.domEvent, function (success) {
      if (success) {
        e.$evtWidget.$message.success('已复制');
      }
    });
  }

  get META() {
    return {
      name: '系统首页表格二开',
      hook: {
        enablePage: '启用停用页面',
        updateAnnoymous: '匿名访问更新',
        deleteRows: '删除',
        importData: '导入数据',
        exportData: '导出数据',
        copyPage: '复制',
        copyAnonymousUrl: '复制匿名地址'
      }
    }
  }
}
export default AppSystemPageIndexTableDevelopment;
