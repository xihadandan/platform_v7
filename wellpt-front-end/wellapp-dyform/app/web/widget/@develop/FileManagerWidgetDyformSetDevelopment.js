import WidgetDyformSetDevelopment from '@develop/WidgetDyformSetDevelopment';
import { download } from '@framework/vue/utils/util';
import { isEmpty } from 'lodash';
import FileManager from '@pageAssembly/app/web/widget/widget-file-manager/FileManager.js';

class FileManagerWidgetDyformSetDevelopment extends WidgetDyformSetDevelopment {

  get META() {
    return {
      name: '文件管理——表单设置',
      hook: {
        saveAsDraft: '保存草稿',
        save: '保存正稿',
        edit: '编辑',
        delete: '删除',
        print: '套打'
      }
    };
  }

  getQueryString(name, defaultValue) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
    var values = window.location.search.substr(1).match(reg);
    if (values != null) {
      return decodeURIComponent(values[2]);
    }
    if (defaultValue != null) {
      return defaultValue;
    }
    return null;
  }

  reload(fileUuid, dataUuid) {
    let href = window.location.href;
    let replaceFileUuid = false;
    let replaceDataUuid = false;
    if (href.indexOf('doc_id=') != -1) {
      href = href.replace(/doc_id=([^&]*)/gi, `doc_id=${fileUuid}`);
      replaceFileUuid = true;
    }
    if (href.indexOf('eventParams.doc_id=') != -1) {
      href = href.replace(/eventParams\.doc_id=([^&]*)/gi, `eventParams.doc_id=${fileUuid}`);
      replaceFileUuid = true;
    }
    if (href.indexOf('dataUuid=') != -1) {
      href = href.replace(/dataUuid=([^&]*)/gi, `dataUuid=${dataUuid}`);
      replaceDataUuid = true;
    }

    if (!replaceFileUuid) {
      href += `&doc_id=${fileUuid}`;
    }
    if (!replaceDataUuid) {
      href += `&dataUuid=${dataUuid}`;
    }
    window.location = href;
  }

  /**
   *
   */
  mounted() {
    const _this = this;
    let folderUuid = _this.getQueryString('fd_id') || _this.getQueryString('eventParams.fd_id');
    let fileUuid = _this.getQueryString('doc_id') || _this.getQueryString('eventParams.doc_id');
    _this.folderUuid = folderUuid;
    _this.fileUuid = fileUuid;
    _this.fileManager = new FileManager(_this.$widget);
    let files = [];
    if (!isEmpty(fileUuid)) {
      files.push({ uuid: fileUuid });
    }
    if (!isEmpty(folderUuid)) {
      files.push({ uuid: folderUuid, contentType: 'application/folder' });
    }
    if (!isEmpty(files)) {
      _this.fileManager.getFilesDataPermission(files, true).then(dataPermission => {
        let buttons = _this.$widget.widget.configuration.button.buttons;
        _this.updateButtonVisible(buttons, dataPermission);
        // 无权限读取文件
        if (!(dataPermission.includes('readFile'))) {
          _this.$widget.$el.innerHTML = '<div/>'
          _this.$widget.$error({
            title: '确认框',
            content: '无权限查看表单内容！',
            okText: '确定',
            onOk() {
              window.close();
            }
          })
        }
      });
    }
  }

  /**
   *
   */
  updateButtonVisible(buttons, dataPermission) {
    const _this = this;
    buttons.forEach(button => {
      if (button.code == 'saveAsDraft' && !(dataPermission.includes('createDocument')
        || (_this.fileUuid && dataPermission.includes('editFile')))) {
        button.visible = false;
        button.visibleType = 'hidden';
        let $button = _this.$widget.$el.querySelector('.widget-dyform-setting-head button[code="saveAsDraft"]');
        $button && $button.remove();
      }
      if (button.code == 'save' && !(dataPermission.includes('createDocument')
        || (_this.fileUuid && dataPermission.includes('editFile')))) {
        button.visible = false;
        button.visibleType = 'hidden';
        let $button = _this.$widget.$el.querySelector('.widget-dyform-setting-head button[code="save"]');
        $button && $button.remove();
      }
      if (button.code == 'edit' && !dataPermission.includes('editFile')) {
        button.visible = false;
        button.visibleType = 'hidden';
        let $button = _this.$widget.$el.querySelector('.widget-dyform-setting-head button[code="edit"]');
        $button && $button.remove();
      }
      if (button.code == 'delete' && !dataPermission.includes('deleteFile')) {
        button.visible = false;
        button.visibleType = 'hidden';
        let $button = _this.$widget.$el.querySelector('.widget-dyform-setting-head button[code="delete"]');
        $button && $button.remove();
      }
      if (button.code == 'print' && !(dataPermission.includes('readFile') || dataPermission.includes('editFile'))) {
        button.visible = false;
        button.visibleType = 'hidden';
        let $button = _this.$widget.$el.querySelector('.widget-dyform-setting-head button[code="print"]');
        $button && $button.remove();
      }
    });
  }

  /**
   * 保存草稿
   *
   * @param {*} $evt
   */
  saveAsDraft($evt) {
    const _this = this;
    let $dyform = _this.$widget.$refs.wDyform || $evt.$evtWidget;
    let formData = $dyform.collectFormData();
    let documentData = {
      folderUuid: _this.folderUuid,
      fileUuid: _this.fileUuid,
      ...formData
    };
    _this.$widget.$loading('保存中...');
    $axios.post('/proxy/api/dms/file/saveDocumentAsDraft', documentData).then(({ data: result }) => {
      let dmsFile = result.data;
      if (dmsFile) {
        _this.fileUuid = dmsFile.uuid;
        _this.$widget.$message.success('保存成功！', 3, () => {
          _this.reload(_this.fileUuid, dmsFile.dataUuid);
        });
        _this.$widget.pageContext.emitCrossTabEvent('filemanager:detail:change:' + ($evt && $evt.meta && $evt.meta.dmsId), {});
      } else {
        _this.$widget.$message.error(result.msg || '保存失败！');
      }
      _this.$widget.$loading(false);
    }).catch(({ response }) => {
      _this.$widget.$loading(false);
      _this.$widget.$message.error((response && response.data && response.data.msg) || '服务异常！');
    });
  }

  /**
   * 保存正稿
   *
   * @param {*} $evt
   */
  save($evt) {
    const _this = this;
    let $dyform = _this.$widget.$refs.wDyform || $evt.$evtWidget;
    $dyform.collectFormData(true, (valid, msg, formData) => {
      if (valid) {
        let documentData = {
          folderUuid: _this.folderUuid,
          fileUuid: _this.fileUuid,
          ...formData
        };
        _this.$widget.$loading('保存中...');
        $axios.post('/proxy/api/dms/file/saveDocument', documentData).then(({ data: result }) => {
          let dmsFile = result.data;
          if (dmsFile) {
            _this.fileUuid = dmsFile.uuid;
            _this.$widget.pageContext.emitCrossTabEvent('filemanager:detail:change:' + ($evt && $evt.meta && $evt.meta.dmsId), { message: '保存成功！' });
            window.close();
          } else {
            _this.$widget.$message.error(result.msg || '保存失败！');
          }
          _this.$widget.$loading(false);
        }).catch(({ response }) => {
          _this.$widget.$loading(false);
          _this.$widget.$message.error((response && response.data && response.data.msg) || '服务异常！');
        });
      }
    });
  }

  /**
   * 编辑
   *
   * @param {*} $evt
   */
  edit($evt) {
    let href = window.location.href;
    if (href.indexOf('displayState=') != -1) {
      window.location = href.replace(/displayState=([^&]*)/gi, 'displayState=edit');
    } else {
      window.location = href + '&displayState=edit';
    }
  }

  /**
  * 删除
  *
  * @param {*} $evt
  */
  delete($evt) {
    const _this = this;
    _this.$widget.$confirm({
      title: "确认框",
      content: '确认要删除吗？',
      okText: _this.$widget.locale.Popconfirm.okText,
      cancelText: _this.$widget.locale.Popconfirm.cancelText,
      onOk() {
        let $dyform = _this.$widget.$refs.wDyform || $evt.$evtWidget;
        let formData = $dyform.collectFormData();
        let documentData = {
          folderUuid: _this.folderUuid,
          fileUuid: _this.fileUuid,
          ...formData
        };
        _this.$widget.$loading('删除中...');
        $axios.post('/proxy/api/dms/file/deleteDocument', documentData).then(({ data: result }) => {
          if (result.success) {
            _this.$widget.pageContext.emitCrossTabEvent('filemanager:detail:change:' + ($evt && $evt.meta && $evt.meta.dmsId), { message: '删除成功！' });
            window.close();
          } else {
            _this.$widget.$message.error(result.msg || '删除失败！');
          }
          _this.$widget.$loading(false);
        }).catch(({ response }) => {
          _this.$widget.$loading(false);
          _this.$widget.$message.error((response && response.data && response.data.msg) || '服务异常！');
        });
      }
    });
  }

  /**
   * 套打
   */
  print($evt) {
    const _this = this;
    let promise = null;
    if ($evt.eventParams && $evt.eventParams.printTemplateIds) {
      promise = _this.getPrintTemplateByIds($evt.eventParams.printTemplateIds);
    } else {
      promise = _this.getPrintTemplateTreeByFolderUuids([this.folderUuid]);
    }
    promise.then(printTemplates => {
      if (printTemplates.length == 1) {
        _this.doPrint(printTemplates[0].id);
      } else {
        _this.showChoosePrintTemplateDialog(printTemplates);
      }
    });
  }

  showChoosePrintTemplateDialog(printTemplates) {
    const _this = this;
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
        <a-modal :title="title" :visible="visible" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
          <div style="height: 350px; overflow: auto;">
            <a-tree
              class="ant-tree-directory"
              :selectedKeys="selectedKeys"
              :tree-data="treeData"
              :replaceFields="replaceFields"
              @select="onSelect"
            />
          </div>
        </a-modal>
      </a-config-provider>`,
      data: function () {
        return {
          title: '选择套打模板',
          visible: true,
          locale: _this.$widget.locale,
          selectedKeys: [],
          treeData: printTemplates,
          replaceFields: { title: 'name', key: 'id', value: 'id' }
        };
      },
      methods: {
        onSelect(selectedKeys, info) {
          console.log('onSelect', info);
          this.selectedKeys = selectedKeys;
        },
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          if (!this.selectedKeys.length) {
            _this.$widget.$message.error('请选择套打模板！');
            return;
          }
          this.visible = false;
          this.$destroy();
          _this.doPrint(this.selectedKeys[0]);
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  /**
   * 套打处理
   *
   * @param {*} printTemplateUuid
   */
  doPrint(printTemplateUuid) {
    const _this = this;
    let $dyform = _this.$widget.$refs.wDyform || $evt.$evtWidget;
    let formData = $dyform.collectFormData();
    _this.$widget.$loading('套打中...');
    $axios.post('/proxy/api/dms/printtemplate/print', {
      printTemplateUuId: printTemplateUuid,
      formUuid: formData.formUuid,
      dataUuid: formData.dataUuid
    }).then(({ data: result }) => {
      if (result.data && result.data.fileID) {
        let url = `/proxy-repository/repository/file/mongo/download?fileID=${result.data.fileID}`;
        download({ url });
        _this.$widget.$message.success('套打成功，下载中...');
      } else {
        _this.$widget.$message.error(result.msg || '套打失败！');
      }
      _this.$widget.$loading(false);
    }).catch(({ response }) => {
      _this.$widget.$loading(false);
      _this.$widget.$message.error((response && response.data && response.data.msg) || '服务异常！');
    });
  }

  /**
   * 获取套打模板
   *
   * @returns
   */
  getPrintTemplateByIds(ids) {
    return $axios.get(`/proxy/basicdata/printtemplate/listByIds?printTemplateIds=${ids}`)
      .then(({ data: result }) => {
        if (result.data) {
          return result.data.map(item => ({ id: item.uuid, name: item.name }));
        }
        return [];
      }).catch(() => {
        return [];
      });
  }
  /**
   * 获取套打模板
   *
   * @returns
   */
  getPrintTemplateTreeByFolderUuids(folderUuids) {
    return $axios.post('/proxy/api/dms/printtemplate/getPrintTemplateTreeByFolderUuids', { folderUuids })
      .then(({ data: result }) => {
        return result.data;
      }).catch(() => {
        return [];
      });
  }


}

export default FileManagerWidgetDyformSetDevelopment;
