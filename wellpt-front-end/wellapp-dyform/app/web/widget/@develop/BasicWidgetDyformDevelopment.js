import WidgetDyformDevelopment from '@develop/WidgetDyformDevelopment';
import qs from 'qs';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import { template as stringTemplate } from 'lodash';

class BasicWidgetDyformDevelopment extends WidgetDyformDevelopment {
  dataUrl(params) {
    return `/dms/data/manager?${qs.stringify(params, { allowDots: true })}`;
  }

  open(event, dataUuid) {
    let _this = this;
    if (!dataUuid) {
      if (event.$evtWidget) {
        if (event.$evtWidget.widget && event.$evtWidget.widget.wtype == 'WidgetTable') {
          dataUuid = event.meta[event.$evtWidget.primaryColumnKey];
        } else if (event.dmsTableId) {
          let table = event.$evtWidget.pageContext.getVueWidgetById(event.dmsTableId);
          if (table && table.primaryColumnKey) {
            let rows = table.getSelectedRows();
            if (rows.length > 0) {
              dataUuid = rows[0][table.primaryColumnKey];
            }
          }
        }
      }
    }

    // 跳转创建表单数据
    if (event.targetPosition === 'newWindow' || event.targetPosition === 'currentWindow') {
      let hideLoading = null;
      if (EASY_ENV_IS_BROWSER) {
        // 显示按钮的加载状态
        if (
          window.event.currentTarget.__vue__ &&
          window.event.currentTarget.__vue__.$parent &&
          window.event.currentTarget.__vue__.$parent.sLoading != undefined
        ) {
          window.event.currentTarget.__vue__.$parent.sLoading = true;
          let $loadingElement = window.event.currentTarget.__vue__.$parent;
          let icon = $loadingElement.$el.querySelector('i');
          if (icon) {
            icon.style.display = 'none';
          }
          hideLoading = function () {
            $loadingElement.sLoading = false;
            if (icon) {
              icon.style.display = 'inline-block';
            }
          };
        }
      }
      event.getWidget(
        {
          id: event.dmsId,
          pageId: event.$evtWidget.namespace,
          pageUuid: event.$evtWidget.namespace
        },
        function (widget) {
          if (hideLoading) {
            hideLoading();
          }
          let formUuid = widget.configuration.useRequestForm
            ? event.meta.formUuid || widget.configuration.formUuid
            : widget.configuration.formUuid;
          event.render({
            targetPosition: event.targetPosition,
            url: _this.dataUrl({
              from: event.$evtWidget.namespace,
              eventParams: event.eventParams,
              dmsId: event.dmsId,
              wTableId: event.dmsTableId ? event.dmsTableId : event.$evtWidget.widget ? event.$evtWidget.widget.id : undefined,
              formUuid,
              dataUuid: dataUuid || event.meta[event.$evtWidget.primaryColumnKey],
              displayState: event.option.displayState || (dataUuid != null ? 'label' : 'edit')
            })
          });
        }
      );
    } else if (event.targetPosition === 'widgetLayout' || event.targetPosition === 'widgetModal' || event.targetPosition === 'widgetTab') {
      this.refreshDms(event, {
        dmsId: event.dmsId,
        eventParams: event.eventParams,
        containerWid: event.containerWid,
        targetPosition: event.targetPosition,
        displayState: event.option.displayState || (dataUuid != null ? 'label' : 'edit'),
        dataUuid: dataUuid || event.meta[event.$evtWidget.primaryColumnKey],
        wTableId: event.dmsTableId || event.$evtWidget.widget.id,
        broadcastChannel: event.eventParams.__bc
      });
    }
  }

  refreshDms(event, options) {
    let { dmsId, containerWid, targetPosition, dataUuid, displayState, wTableId, namespace, eventParams } = options;
    let _this = this;
    event.getWidget(
      {
        id: dmsId,
        pageId: namespace || event.$evtWidget.namespace,
        pageUuid: namespace || event.$evtWidget.namespace
      },
      function (widget) {
        widget.props = {
          containerWid: containerWid, // 容器ID
          targetPosition: targetPosition,
          // 传递属性参数
          dataUuid: dataUuid,
          formUuid: widget.configuration.formUuid,
          wTableId: wTableId,
          displayState: displayState || 'edit',
          broadcastChannel: options.broadcastChannel
        };

        let ruleKey = 'formElementRules',
          formElementRules = {};
        if (dataUuid) {
          // 有数据的情况下，编辑或者查阅
          ruleKey = displayState === 'edit' ? 'editStateFormElementRules' : 'labelStateFormElementRules';
          if (widget.configuration.enableStateForm) {
            let formUuidKey = displayState === 'edit' ? 'editStateFormUuid' : 'labelStateFormUuid';
            widget.props.formUuid = widget.configuration[formUuidKey] || widget.configuration.formUuid;
          }
        }
        if (widget.configuration[ruleKey]) {
          for (let i = 0, len = widget.configuration[ruleKey].length; i < len; i++) {
            formElementRules[widget.configuration[ruleKey][i].id] = {
              readonly: widget.configuration[ruleKey][i].readonly,
              editable: widget.configuration[ruleKey][i].editable,
              disable: widget.configuration[ruleKey][i].disable,
              hidden: widget.configuration[ruleKey][i].hidden,
              displayAsLabel: widget.configuration[ruleKey][i].displayAsLabel,
              required: widget.configuration[ruleKey][i].required
            };

            let children = widget.configuration[ruleKey][i].children;
            if (children) {
              let childrenRules = {};
              formElementRules[widget.configuration[ruleKey][i].id].children = childrenRules;
              for (let j = 0, jlen = children.length; j < jlen; j++) {
                childrenRules[children[j].id] = {
                  readonly: children[j].readonly,
                  editable: children[j].editable,
                  disable: children[j].disable,
                  hidden: children[j].hidden,
                  displayAsLabel: children[j].displayAsLabel,
                  required: children[j].required
                };
              }
            }
          }
          widget.props.formElementRules = formElementRules;
        }

        let renderOption = { widgets: [widget], containerWid: containerWid, targetPosition: targetPosition, eventParams };
        if (targetPosition === 'widgetTab') {
          renderOption.title = widget.title;
          renderOption.key = dmsId;
        }
        if (targetPosition === 'widgetLayout') {
          if (dataUuid == undefined) {
            renderOption.title = widget.configuration.title;
          } else {
            renderOption.title = displayState === 'edit' ? widget.configuration.editStateTitle : widget.configuration.labelStateTitle;
          }
          renderOption.key = dmsId;
          renderOption.forceUpdate = true;
        }
        if (targetPosition === 'widgetModal') {
          renderOption.title = ''; // title 等待被更新
          widget.configuration.buttonPosition = 'disable'; // 禁止按钮在表单设置页面里展示
          widget.configuration.titleVisible = false; // 禁止内部显示标题
        }
        if (targetPosition == 'currentWindow') {
          if (event.$evtWidget && event.$evtWidget.dyform) {
            event.$evtWidget.pageContext.emitEvent(`WidgetDyformSetting:${dmsId}:refresh`, {
              props: widget.props
            });
            return;
          } else {
            renderOption.url = _this.dataUrl({
              dmsId,
              from: event.$evtWidget.namespace,
              wTableId,
              formUuid: widget.configuration.formUuid,
              dataUuid,
              displayState
            });
          }
        }
        event.render(renderOption);
      }
    );
  }

  create(event) {
    this.open(event, null);
  }

  modify(event) {
    if (event.option) {
      event.option.displayState = 'edit';
    }
    let dataUuid = undefined;
    if (event.$evtWidget) {
      if (event.$evtWidget.widget && event.$evtWidget.widget.wtype == 'WidgetTable') {
        dataUuid = event.meta[event.$evtWidget.primaryColumnKey];
        if (!dataUuid) {
          let keys = event.$evtWidget.getSelectedRowKeys();
          if (keys.length > 0) {
            dataUuid = keys[0];
          }
        }
      } else if (event.dmsTableId) {
        let table = event.$evtWidget.pageContext.getVueWidgetById(event.dmsTableId);
        if (table && table.primaryColumnKey) {
          let rows = table.getSelectedRows();
          if (rows.length > 0) {
            dataUuid = rows[0][table.primaryColumnKey];
          }
        }
      } else if (event.$evtWidget.dyform && event.$evtWidget.dyform.dataUuid) {
        // 处于表单页：刷新当前表单数据为编辑状态
        this.refreshDms(event, {
          dmsId: event.dmsId,
          containerWid: event.containerWid,
          targetPosition: event.targetPosition,
          displayState: 'edit',
          dataUuid: event.$evtWidget.dyform.dataUuid,
          wTableId: event.meta.wTableId,
          namespace: event.meta.namespace
        });
        return;
      }
    }
    if (dataUuid) {
      this.open(event, dataUuid);
    }
  }

  submit(formData, callback) {
    return new Promise((resolve, reject) => {
      $axios
        .post('/proxy/api/dm/saveOrUpdateByFormData', { formDataJson: JSON.stringify(formData.dyFormData) })
        .then(({ data }) => {
          if (typeof callback == 'function') {
            callback(data.data);
          }
          resolve(data.data);
        })
        .catch(error => {
          reject(error);
        });
    });
  }

  // 校验并保存
  validateAndSave(event) {
    let $dyform = event.$evtWidget; // 表单组件实例
    let _this = this;
    $dyform.$loading();
    $dyform.collectFormData(event.eventParams.validate !== 'false', function (valid, msg, formData) {
      const emitSaveError = error => {
        try {
          const data = error.response.data.data;
          if (data && data.data) {
            $dyform.pageContext.emitEvent(`${formData.formUuid}:saveDataError`, {
              res: data,
              callback: () => {
                _this.validateAndSave(event);
              }
            });
          }
        } catch (error) {
          console.error(error);
        }
      };

      if (valid || event.eventParams.validate === 'false') {
        _this
          .submit(formData, function (dataUuid) {
            $dyform.$loading(false);
            if (dataUuid) {
              _this.afterSaveSuccess(event, dataUuid);
            }
          })
          .catch(error => {
            $dyform.$loading(false);
            emitSaveError(error);
          });
      } else {
        $dyform.$loading(false);
      }
      //TODO: 弹窗提示
    });
  }

  afterSaveSuccess(event, dataUuid) {
    let $dyform = event.$evtWidget; // 表单组件实例
    let _this = this;
    $dyform.$message.success($dyform.$t('WidgetDataManagerView.message.saveSuccess', '保存成功'), 1).then(function () {
      if (event.meta.wTableId) {
        // 刷新表格
        $dyform.pageContext.emitCrossTabEvent(`${event.meta.namespace || ''}:${event.meta.wTableId}:refetch`, true);
        $dyform.pageContext.emitEvent(`${event.meta.wTableId}:refetch`, true);
        $dyform.pageContext.emitCrossTabEvent(`${event.meta.wTableId}:refetch`, true);
        $dyform.pageContext.emitEvent(`${event.meta.namespace || ''}:${event.meta.wTableId}:refetch`, true);
      }

      // 跳转到编辑状态：根据数据管理的配置
      if (event.meta.dmsId) {
        $dyform.pageContext.emitEvent(`${event.meta.dmsId}:afterSaveDataSuccess`, dataUuid);
        if (event.eventParams && event.eventParams.closeAfterSave === 'true') {
          // 关闭
          $dyform.currentWindow.close();
          return;
        }

        // 判断当前是在浏览器窗口、还是弹窗、或者tab、或者布局内
        if (event.meta.containerWid) {
          _this.refreshDms(event, {
            dataUuid,
            dmsId: event.meta.dmsId,
            containerWid: event.meta.containerWid,
            targetPosition: event.meta.targetPosition,
            wTableId: event.meta.wTableId,
            displayState: 'edit'
          });
        } else {
          // 刷新
          if (location.search.indexOf(`dataUuid=${dataUuid}`) === -1) {
            const urlObj = new URL(window.location.href);
            if (dataUuid) {
              urlObj.searchParams.set('dataUuid', dataUuid);
            }
            window.location.href = urlObj.toString();
          } else {
            window.location.reload();
          }
        }
      }
    });
  }

  save(event) {
    this.validateAndSave(event);
  }

  saveNewVersion(event) {
    let $dyform = event.$evtWidget; // 表单组件实例
    let _this = this;
    let _dataUuid = $dyform.dataUuid;
    $dyform.$loading();
    $dyform.collectFormData(
      true,
      function (valid, msg, formData) {
        if (valid) {
          $axios
            .post(`/proxy/api/dyform/data/saveDataModelFormDataNewVersion/${_dataUuid}`, formData.dyFormData)
            .then(({ data }) => {
              $dyform.$loading(false);
              if (data.data) {
                _this.afterSaveSuccess(event, data.data);
              }
            })
            .catch(error => {
              $dyform.$loading(false);
              $dyform.$message.error('保存新版本异常');
              console.error(error.response);
            });
        } else {
          $dyform.$loading(false);
        }
        //TODO: 弹窗提示
      },
      true
    );
  }

  showVersionDataList(event) {
    let $dyform = event.$evtWidget; // 表单组件实例
    let containerElement = $dyform.$el;
    let ps = $dyform.$el.closest('.ps');
    if (ps) {
      containerElement = ps.parentElement;
    }
    let drawer = containerElement.querySelector('.dyform-data-version-drawer');
    if (!drawer) {
      $dyform.$loading();
      let div = document.createElement('div');
      containerElement.appendChild(div);
      let eventParams = event.eventParams;
      $axios
        .post('/json/data/services', {
          serviceName: 'dyFormFacade',
          methodName: 'getAllVersionFormData',
          args: JSON.stringify([$dyform.formUuid, $dyform.dataUuid])
        })
        .then(({ data }) => {
          $dyform.$loading(false);
          let dataMap = data.data,
            dataVerList = [];
          if (dataMap && dataMap[$dyform.formUuid]) {
            dataVerList.push(...dataMap[$dyform.formUuid]);
          }
          let messages = $dyform.$i18n.messages[$dyform.$i18n.locale]
            ? JSON.parse(JSON.stringify($dyform.$i18n.messages[$dyform.$i18n.locale]))
            : {};
          new (Vue.extend({
            store: $dyform.$store,
            inject: ['pageContext'],
            template: `<Drawer class="dyform-data-version-drawer"
          :title="title"
          style="text-align: left;  overscroll-behavior: contain;"
          ref="versionDrawer"
          :drawerVisible="drawerVisible"
        >
          <template slot="content">
             <a-timeline>
                <a-timeline-item v-for="(item, i) in dataVerList" :key="'datav' + i">
                <div style="display: flex; align-items: baseline; justify-content: space-between; font-weight: bolder">
                   <span v-html="displayVersionTitle(item)"></span>
                  <Drawer width="calc(100vw)" title="数据"  :container="getContainer">
                    <template slot="content">
                    <a-config-provider :locale="locale">
                    <WidgetDyform v-if="render" displayState="label" :formUuid="formUuid" :dataUuid="item.uuid" :isVersionDataView="true"></WidgetDyform>
                  </a-config-provider>
                    </template>
                    <a-button size="small" type="link" @click="showDataVersionDetail(item)">查看</a-button>
                  </Drawer>
                </div>

                <p style="color: #999">{{ item.create_time }}</p>
              </a-timeline-item>
            </a-timeline>

           </template >
        </Drawer >`,
            components: { Drawer, Modal },
            i18n: { locale: $dyform.$i18n.locale, messages: { [$dyform.$i18n.locale]: messages } },
            provide() {
              return {
                pageContext: $dyform.pageContext
              };
            },
            data() {
              return {
                title: eventParams && eventParams.title ? eventParams.title : '数据版本列表',
                drawerVisible: true,
                formUuid: $dyform.formUuid,
                dataVerList,
                render: false,
                locale: $dyform.locale
              };
            },
            methods: {
              displayVersionTitle(item) {
                let version = Number(item.version).toFixed(1);
                if (eventParams && eventParams.dataVersionTitle) {
                  let compiler = stringTemplate(eventParams.dataVersionTitle);
                  try {
                    return compiler({ ...item, version });
                  } catch (error) {
                    console.error(error);
                  }
                }
                return '版本 ' + version;
              },
              getContainer() {
                return containerElement;
              },
              showDataVersionDetail() {
                this.render = true;
              }
            },
            mounted() {}
          }))().$mount(div);
        })
        .catch(() => {});
    } else {
      drawer.__vue__.visible = true;
    }
  }

  delete(event) {
    let uuids = [],
      selectedRowKeys = [];
    let tableId = undefined,
      dataUuid = undefined;
    if (event.$evtWidget) {
      if (event.$evtWidget.widget && event.$evtWidget.widget.wtype == 'WidgetTable') {
        selectedRowKeys = event.meta.selectedRowKeys;
        tableId = event.$evtWidget.widget.id;
        dataUuid = event.meta[event.$evtWidget.primaryColumnKey];
      } else if (event.dmsTableId) {
        let table = event.$evtWidget.pageContext.getVueWidgetById(event.dmsTableId);
        tableId = event.dmsTableId;
        if (table && table.primaryColumnKey) {
          selectedRowKeys = table.getSelectedRowKeys();
        }
      }
    }

    if (dataUuid) {
      // 行级数据删除
      uuids.push({ uuid: dataUuid });
    } else if (selectedRowKeys != undefined && selectedRowKeys.length) {
      for (let k of selectedRowKeys) {
        uuids.push({ uuid: k });
      }
    }
    if (uuids.length) {
      let hideLoading = null;
      if (EASY_ENV_IS_BROWSER) {
        // 显示按钮的加载状态
        if (
          window.event.currentTarget.__vue__ &&
          window.event.currentTarget.__vue__.$parent &&
          window.event.currentTarget.__vue__.$parent.sLoading != undefined
        ) {
          window.event.currentTarget.__vue__.$parent.sLoading = true;
          let $loadingElement = window.event.currentTarget.__vue__.$parent;
          let icon = $loadingElement.$el.querySelector('i');
          if (icon) {
            icon.style.display = 'none';
          }
          hideLoading = function () {
            $loadingElement.sLoading = false;
            if (icon) {
              icon.style.display = 'inline-block';
            }
          };
        }
      }
      event.getWidget(
        {
          id: event.dmsId,
          pageId: event.$evtWidget.namespace,
          pageUuid: event.$evtWidget.namespace
        },
        function (widget) {
          $axios
            .post('/json/data/services', {
              serviceName: 'dyFormActionService',
              methodName: 'delete',
              args: JSON.stringify([uuids, widget.configuration.formUuid, false])
            })
            .then(({ data }) => {
              // console.log(data);
              event.$evtWidget.pageContext.emitCrossTabEvent(`${tableId}:refetch`, true);
              event.$evtWidget.pageContext.emitEvent(`${tableId}:refetch`, true);
              if (hideLoading) {
                hideLoading();
              }
            });
        }
      );
    }
  }
  get META() {
    return {
      name: '表单数据管理',
      hook: {
        open: '打开单据',
        create: '新建',
        modify: '修改',
        save: {
          title: '保存',
          eventParams: [
            {
              paramKey: 'closeAfterSave',
              remark: '保存后是否关闭',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '关闭', value: 'true' },
                  { label: '不关闭(刷新)', value: 'false' }
                ]
              }
            },
            {
              paramKey: 'validate',
              remark: '是否校验数据（默认校验）',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '校验', value: 'true' },
                  { label: '不校验', value: 'false' }
                ]
              }
            }
          ]
        },
        saveNewVersion: {
          title: '保存为新版本',
          eventParams: [
            {
              paramKey: 'closeAfterSave',
              remark: '保存后是否关闭',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '关闭', value: 'true' },
                  { label: '不关闭', value: 'false' }
                ]
              }
            },
            {
              paramKey: 'validate',
              remark: '是否校验数据（默认校验）',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '校验', value: 'true' },
                  { label: '不校验', value: 'false' }
                ]
              }
            }
          ]
        },
        showVersionDataList: {
          title: '查看历史版本数据'
        },
        delete: '删除'
      }
    };
  }
}

export default BasicWidgetDyformDevelopment;
