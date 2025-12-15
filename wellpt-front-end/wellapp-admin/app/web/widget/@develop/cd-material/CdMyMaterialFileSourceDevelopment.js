import WidgetFileSourceDevelopment from '@develop/WidgetFileSourceDevelopment';

class CdMyMaterialFileSourceDevelopment extends WidgetFileSourceDevelopment {
  get META() {
    return {
      name: '附件来源_我的材料'
    };
  }

  chooseFile() {
    console.log('chooseFile');
    let _this = this;

    // 材料选择弹出框
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
        <a-modal :title="$t('WidgetFormFileUpload.fileSource.myMaterials','我的材料')" :visible="visible" width="900px" @ok="handleOk" @cancel="handleCancel">
        <div style="height: 400px; overflow:auto"><div class="widget"></div></div>
        </a-modal>
      </a-config-provider>`,
      data: function () {
        return { visible: true, locale: _this.$widget.locale };
      },
      mounted() {
        this.$nextTick(() => {
          _this.getTableWidget().then(myMaterialWidget => {
            myMaterialWidget.$mount(modal.$el.querySelector('.widget'));
            this.myMaterialWidget = myMaterialWidget;
            debugger;
          });
        });
      },
      methods: {
        $t() {
          return _this.$widget.$t.apply(_this, arguments);
        },
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          let selectedRows = this.myMaterialWidget.$refs.widgetTable.getSelectedRows();
          let logicFileInfos = [];
          selectedRows.forEach(row => {
            if (row.logicFileInfos && Array.isArray(row.logicFileInfos)) {
              row.logicFileInfos.forEach(item => {
                logicFileInfos.push(item);
              });
            }
          });
          if (logicFileInfos.length == 0) {
            _this.$widget.$message.warning(_this.$widget.$t('WidgetFormFileUpload.fileSource.selectMaterial', '请选择要上传的材料附件！'));
            return;
          }

          // 标记引用我的材料
          logicFileInfos.forEach(item => {
            item.source = _this.$widget.$t('WidgetFormFileUpload.fileSource.referMyMaterial', '引用我的材料');
          });
          _this.$widget.addDbFiles(logicFileInfos);
          // logicFileInfos.forEach(fileInfo => {
          //   _this.$widget.addFileList({
          //     uid: fileInfo.fileID,
          //     name: fileInfo.fileName || fileInfo.filename,
          //     status: "done",
          //     size: fileInfo.fileSize,
          //     dbFile: fileInfo,
          //   });
          // });
          this.visible = false;
          this.$destroy();
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  getTableWidget() {
    const _this = this;
    return _this
      .getTableDefinition()
      .then(widgetDefinition => {
        // 创建构造器
        let WidgetTable = Vue.extend({
          template:
            '<a-config-provider :locale="locale"><WidgetTable ref="widgetTable" :widget="widget" @beforeLoadData="beforeLoadData"></WidgetTable></a-config-provider>', //
          provide() {
            return {
              pageContext: _this.$widget.pageContext,
              namespace: _this.$widget.namespace,
              vPageState: _this.$widget.vPageState,
              $pageJsInstance: _this.$widget.$pageJsInstance
            };
          },
          i18n: _this.$widget.$i18n,
          inject: {},
          data: function () {
            return { widget: widgetDefinition, locale: _this.$widget.locale };
          },
          methods: {
            beforeLoadData(params) {
              if (
                this.$refs.widgetTable &&
                this.$refs.widgetTable.serverDataRenders &&
                this.$refs.widgetTable.serverDataRenders.length == 0
              ) {
                this.$refs.widgetTable.serverDataRenders.push({
                  columnIndex: 'repoFileUuids',
                  param: {
                    rendererType: 'materialFileDataStoreRender'
                  }
                });
              }
            }
          }
        });
        return new WidgetTable();
      })
      .catch(res => {
        _this.$widget.$message.error(_this.$widget.$t('WidgetFormFileUpload.fileSource.myMaterialsLoadError', '我的材料列表加载失败！'));
      });
  }

  getTableDefinition() {
    // 无法直接取表格定义，从卡片定义下的组件取
    let widgetCardId = 'IZBtzXFOhNIsYdiYCtTBoZuhjUyDoLsA';
    let widgetTableId = 'UVNVYsHdBYpWxXhSHFAPGXxOqrCOSmUx';
    return new Promise((resolve, reject) => {
      $axios
        .get('/api/widget/getWidgetById', { params: { id: widgetTableId } })
        .then(({ data }) => {
          let wgt = data;
          if (!wgt) {
            console.warn('no found widget definition');
            reject();
          } else {
            if (wgt.i18ns) {
              let i18n = this.$widget.i18nsToI18n(wgt.i18ns);
              this.$widget.mergeWidgetI18nMessages(i18n, widgetTableId);
            }
            resolve(JSON.parse(wgt.definitionJson));
          }
        })
        .catch(res => {
          reject(res);
        });
      // $axios
      //   .post('/json/data/services', {
      //     serviceName: 'appContextService',
      //     methodName: 'getAppWidgetDefinitionById',
      //     args: JSON.stringify([widgetCardId, false])
      //   })
      //   .then(({ data: { data = {} } }) => {
      //     if (data.definitionJson) {
      //       let cardWidgetDefinition = JSON.parse(data.definitionJson);
      //       if (cardWidgetDefinition.configuration.widgets.length > 0) {
      //         resolve(cardWidgetDefinition.configuration.widgets[0]);
      //       }
      //     } else {
      //       reject(data);
      //     }
      //   })
      //   .catch(res => {
      //     reject(res);
      //   });
    });
  }
}

export default CdMyMaterialFileSourceDevelopment;
