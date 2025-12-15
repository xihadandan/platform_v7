import WidgetDyformDevelopment from "@develop/WidgetDyformDevelopment";
import qs from "qs";
import { template as stringTemplate } from "lodash";
import { pageContext } from "wellapp-uni-framework";

class BasicWidgetDyformDevelopment extends WidgetDyformDevelopment {
  dataUrl(params) {
    return `/packages/_/pages/dms/dms_dyform?${qs.stringify(params, { allowDots: true })}`;
  }
  getWidget({ id, appPageId, appPageUuid }) {
    return new Promise((resolve, reject) => {
      uni.$axios
        .get("/api/app/widget/getWidgetById", { params: { id, appPageId, appPageUuid } })
        .then(({ data }) => {
          let wgt = data.data;
          if (!wgt) {
            console.warn("no found widget definition");
            reject();
          } else {
            resolve(JSON.parse(wgt.definitionJson));
          }
        })
        .catch(() => {});
    });
  }
  open(event, dataUuid, closeAfterSave) {
    let _this = this;
    if (!dataUuid) {
      if (event.ui) {
        if (
          event.ui.widget &&
          (event.ui.widget.wtype == "WidgetTable" || event.ui.widget.wtype == "WidgetUniListView")
        ) {
          pageContext.updateComponent(event.ui.widget.id, event.ui); // 更新组件
          dataUuid = event.meta[event.ui.primaryColumnKey];
        } else if (event.dmsTableId) {
          let table = event.ui.pageContext.getVueWidgetById(event.dmsTableId);
          if (table && table.primaryColumnKey) {
            let rows = table.getSelectedRows();
            if (rows.length > 0) {
              dataUuid = rows[0][table.primaryColumnKey];
            }
          }
        }
      }
    }
    this.getWidget({
      id: event.dmsId,
      pageId: event.ui.namespace,
      pageUuid: event.ui.namespace,
    }).then((widget) => {
      let formUuid = widget.configuration.useRequestForm
          ? event.meta.formUuid || widget.configuration.formUuid
          : widget.configuration.formUuid,
        dmsTableId = event.dmsTableId
          ? event.dmsTableId
          : event.ui.dmsTableId
          ? event.ui.dmsTableId
          : event.ui.widget
          ? event.ui.widget.id
          : undefined;
      let url = _this.dataUrl({
        from: event.ui.namespace,
        eventParams: event.eventParams,
        dmsId: event.dmsId,
        title: event.name,
        dmsTableId,
        formUuid,
        dataUuid:
          dataUuid ||
          (event.ui.widget && (event.ui.widget.wtype == "WidgetTable" || event.ui.widget.wtype == "WidgetUniListView")
            ? event.meta[event.ui.widget.configuration.primaryColumnKey]
            : null),
        displayState: event.displayState || (dataUuid != null ? "label" : "edit"),
        closeAfterSave: closeAfterSave,
      });
      uni.navigateTo({
        url: url,
      });
    });
  }

  create(event) {
    this.open(event, null);
  }

  modify(event) {
    event.displayState = "edit";
    let dataUuid = undefined;
    if (event.ui) {
      if (event.ui.widget && (event.ui.widget.wtype == "WidgetTable" || event.ui.widget.wtype == "WidgetUniListView")) {
        pageContext.updateComponent(event.ui.widget.id, event.ui); // 更新组件
        dataUuid = event.meta[event.ui.primaryColumnKey];
        if (!dataUuid) {
          let keys = event.ui.getSelectedRowKeys();
          if (keys.length > 0) {
            dataUuid = keys[0];
          }
        }
      } else if (event.dmsTableId) {
        let table = event.ui.pageContext.getVueWidgetById(event.dmsTableId);
        if (table && table.primaryColumnKey) {
          let rows = table.getSelectedRows();
          if (rows.length > 0) {
            dataUuid = rows[0][table.primaryColumnKey];
          }
        }
      } else if (event.$evtWidget.dyform && event.$evtWidget.dyform.dataUuid) {
        // 处于表单页：刷新当前表单数据为编辑状态
        this.open(event, event.$evtWidget.dyform.dataUuid, "true");
        return;
      }
    }
    if (dataUuid) {
      this.open(event, dataUuid);
    }
  }

  submit(formData, callback) {
    return new Promise((resolve, reject) => {
      uni.$axios
        .post("/api/dm/saveOrUpdateByFormData", { formDataJson: JSON.stringify(formData.dyFormData) })
        .then(({ data }) => {
          if (typeof callback == "function") {
            callback(data.data);
          }
          resolve(data.data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  // 校验并保存
  validateAndSave(event) {
    let $dyform = event.$evtWidget; // 表单组件实例
    let _this = this;
    $dyform.$loading();
    $dyform.collectFormData(true, function (valid, msg, formData) {
      if (event.ui && event.ui.invokeDevelopmentMethod) {
        event.ui.invokeDevelopmentMethod("beforeSaveData", formData);
      }
      if (valid) {
        _this
          .submit(formData, function (dataUuid) {
            $dyform.$loading(false);
            if (dataUuid) {
              _this.afterSaveSuccess(event, dataUuid);
            }
          })
          .catch((error) => {
            $dyform.$loading(false);
          });
      } else {
        $dyform.$loading(false);
      }
    });
  }

  afterSaveSuccess(event, dataUuid) {
    let _this = this;
    uni.$ptToastShow({
      title: "保存成功",
      onCLose: function () {
        // 跳转到编辑状态：根据数据管理的配置
        let dmsId = event.dmsId || (event.ui && event.ui.dmsId);
        if (dmsId) {
          if (event.ui && event.ui.invokeDevelopmentMethod) {
            event.ui.invokeDevelopmentMethod("afterSaveDataSuccess", dataUuid);
          }
          if (
            (event.ui && event.ui.closeAfterSave === "true") ||
            (event.eventParams && event.eventParams.closeAfterSave === "true")
          ) {
            // 关闭
            uni.navigateBack({
              delta: 1,
              success: function () {
                _this.goBack(event, dmsId, dataUuid, event.ui.dmsTableId);
              },
            });
          } else {
            // 刷新数据管理表单
            uni.$emit("refresh:" + dmsId, { dataUuid });
          }
        }
      },
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
            .catch((error) => {
              $dyform.$loading(false);
              $dyform.$message.error("保存新版本异常");
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

  delete(event) {
    let uuids = [],
      selectedRowKeys = [];
    let tableId = undefined,
      dataUuid = undefined;
    if (event.ui) {
      if (event.ui.widget && (event.ui.widget.wtype == "WidgetTable" || event.ui.widget.wtype == "WidgetUniListView")) {
        dataUuid = event.meta[event.ui.primaryColumnKey];
        selectedRowKeys = event.ui.selectedRowKeys;
        tableId = event.ui.widget.id;
      } else if (event.dmsTableId) {
        let table = event.ui.pageContext.getVueWidgetById(event.dmsTableId);
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
      this.getWidget({
        id: event.dmsId,
        pageId: event.ui.namespace,
        pageUuid: event.ui.namespace,
      }).then((widget) => {
        uni.$axios
          .post("/json/data/services", {
            serviceName: "dyFormActionService",
            methodName: "delete",
            args: JSON.stringify([uuids, widget.configuration.formUuid, false]),
          })
          .then(({ data }) => {
            if (typeof event.ui.refresh == "function") {
              event.ui.refresh();
              event.ui.selection.splice(0, event.ui.selection.length);
              event.ui.checkAll = false;
              event.ui.invokeDevelopmentMethod("afterDeleteSuccess", uuids);
            }
          });
      });
    }
  }

  goBack(event, dmsId, dataUuid, dmsTableId) {
    let pages = getCurrentPages();
    // 返回到数据管理页面，刷新dmsId，其余都刷新列表数据
    if (pages.length > 1 && pages[pages.length - 2].route == "packages/_/pages/dms/dms_dyform") {
      // 刷新数据管理表单
      uni.$emit("refresh:" + dmsId, { dataUuid });
    } else if (dmsTableId) {
      let $table = pageContext.getComponent(dmsTableId);
      if ($table) {
        $table.refresh();
      }
    }
  }
  get META() {
    return {
      name: "表单数据管理",
      hook: {
        open: "打开单据",
        create: "新建",
        modify: "修改",
        save: {
          title: "保存",
          eventParams: [
            {
              paramKey: "closeAfterSave",
              remark: "保存后是否关闭",
              valueScope: (() => {
                return ["true", "false"];
              })(),
            },
          ],
        },
        saveNewVersion: {
          title: "保存为新版本",
          eventParams: [
            {
              paramKey: "closeAfterSave",
              remark: "保存后是否关闭",
              valueScope: (() => {
                return ["true", "false"];
              })(),
            },
          ],
        },

        delete: "删除",
      },
    };
  }
}

export default BasicWidgetDyformDevelopment;
