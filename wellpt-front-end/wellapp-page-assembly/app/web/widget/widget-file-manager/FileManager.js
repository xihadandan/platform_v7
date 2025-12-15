import { generateId, download } from '@framework/vue/utils/util';
import { preview } from '@framework/vue/lib/preview/filePreviewApi';
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
let MULTI_FILE_DATA_PERMISSION = ['download', 'delete', 'restore', 'copy', 'move'];
class FileManager {
  constructor($widget, openTarget = '_blank') {
    this.$widget = $widget;
    this.openTarget = openTarget;
    this.fileActionMap = {};
    this.getFileRepositoryServerHost().then(fileRepositoryServerHost => {
      this.fileRepositoryServerHost = fileRepositoryServerHost;
    });
  }

  /**
   * 新建文件夹
   */
  createFolder($evt) {
    const _this = this;
    let selectedFolder = _this.$widget.getSelectedFolder();
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
        <a-modal :title="title" :visible="visible" width="550px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
          <div style="height: 100px; overflow:auto">
            <FolderForm ref="folderForm" :label="label" :parentFolderUuid="parentFolderUuid"></FolderForm>
          </div>
        </a-modal>
      </a-config-provider>`,
      components: { FolderForm: () => import('./folder-form.vue') },
      i18n: _this.$widget.$i18n,
      data: function () {
        return {
          title: _this.$widget.$t('WidgetFileManager.createFolder', '新建文件夹'),
          visible: true,
          locale: _this.$widget.locale,
          parentFolderUuid: selectedFolder.uuid,
          label: _this.$widget.$t('WidgetFileManager.folderName', '夹名称')
        };
      },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          this.$refs.folderForm.collect().then(folder => {
            _this
              .saveNewFolder(folder.uuid, folder.name, selectedFolder.uuid)
              .then(({ data: result }) => {
                if (result.data) {
                  _this.$widget.$message.success(_this.$widget.$t('WidgetFileManager.message.createFolderSuccess', '文件夹创建成功！'));
                  _this.$widget.refresh({ appendTreeNode: { id: result.data, name: folder.name } });
                  this.visible = false;
                  this.$destroy();
                } else {
                  _this.$widget.$message.error(
                    result.msg || _this.$widget.$t('WidgetFileManager.message.createFolderFailed', '文件夹创建失败！')
                  );
                }
              })
              .catch(({ response }) => {
                _this.$widget.$message.error(
                  (response && response.data && response.data.msg) ||
                  _this.$widget.$t('WidgetFileManager.message.serverError', '服务异常！')
                );
              });
          });
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  /**
   * 保存新建夹
   *
   * @param {*} folderUuid
   * @param {*} folderName
   * @param {*} parentFolderUuid
   * @returns
   */
  saveNewFolder(folderUuid, folderName, parentFolderUuid) {
    return $axios.post('/json/data/services', {
      serviceName: 'dmsFileManagerService',
      methodName: 'createFolder',
      args: JSON.stringify([folderUuid, folderName, parentFolderUuid])
    });
  }

  /**
   * 新建文档
   */
  createDocument($evt) {
    const _this = this;
    let selectedFolder = _this.$widget.getSelectedFolder();
    _this.getFolderDyformDefinition(selectedFolder.uuid).then(folderDyformDefinition => {
      if (folderDyformDefinition) {
        _this.openNewDocument(folderDyformDefinition, selectedFolder, $evt);
      } else {
        _this.$widget.$message.error(_this.$widget.$t('WidgetFileManager.message.notConfigForm', '没有配置表单，无法创建文档！'));
      }
    });
  }

  /**
   * 打开新建文档
   */
  openNewDocument(folderDyformDefinition, selectedFolder, $evt) {
    const _this = this;
    let eventHandler = {
      action: 'BasicWidgetDyformDevelopment.create',
      actionType: 'dataManager',
      dmsId: _this.$widget.configuration.WidgetDyformSetting.id,
      eventParams: [{ id: generateId(), paramKey: 'fd_id', paramValue: selectedFolder.uuid }],
      targetPosition: 'newWindow',
      $evt: $evt.$evt || $evt,
      $evtWidget: _this.$widget,
      meta: { formUuid: folderDyformDefinition.formUuid }
    };
    new DispatchEvent(eventHandler).dispatch();
  }

  /**
   * 获取夹表单配置
   *
   * @param {*} folderUuid
   * @returns
   */
  getFolderDyformDefinition(folderUuid) {
    return $axios.get(`/proxy/api/dms/file/getFolderDyformDefinitionByFolderUuid/${folderUuid}`).then(({ data: result }) => {
      return result.data;
    });
  }

  /**
   * 查看文件
   */
  viewFile(file, $evt) {
    const _this = this;
    if (!file || !file.uuid) {
      _this.$widget.$message.error(_this.$widget.$t('WidgetFileManager.message.selectFile', '请选择文件（夹）！'));
      return;
    }

    if (_this.isFolder(file)) {
      _this.openFolder(file);
    } else if (_this.isDyform(file)) {
      _this.viewDyform(file, $evt);
    } else {
      _this.getViewFileInfo(file).then(viewFileInfo => {
        if (viewFileInfo && viewFileInfo.viewUrl) {
          window.open(_this.addSystemPrefix(viewFileInfo.viewUrl), _this.openTarget);
        } else {
          _this.viewOnlineFile(file);
        }
      });
    }
  }

  getViewFileInfo(file) {
    return $axios
      .post('/json/data/services', {
        serviceName: 'dmsFileManagerService',
        methodName: 'getViewFileInfo',
        args: JSON.stringify([file])
      })
      .then(({ data: result }) => {
        return result.data;
      });
  }

  /**
   * 查看表单
   */
  viewDyform(file, $evt) {
    const _this = this;
    let selectedFolder = _this.$widget.getSelectedFolder();
    _this.$widget.primaryColumnKey = 'dataUuid';
    if (!file.formUuid) {
      file.formUuid = file.dataDefUuid;
    }
    let eventHandler = {
      action: 'BasicWidgetDyformDevelopment.open',
      actionType: 'dataManager',
      dmsId: _this.$widget.configuration.WidgetDyformSetting.id,
      eventParams: [
        { id: generateId(), paramKey: 'fd_id', paramValue: selectedFolder.uuid },
        { id: generateId(), paramKey: 'doc_id', paramValue: file.uuid }
      ],
      targetPosition: 'newWindow',
      $evt: $evt.$evt || $evt,
      $evtWidget: _this.$widget,
      displayState: file.fileStatus == '2' ? 'edit' : 'label', // 草稿打开编辑
      meta: file
    };
    new DispatchEvent(eventHandler).dispatch();
  }

  /**
   * 在线预览文件
   */
  viewOnlineFile(file) {
    const _this = this;
    _this.getFileRepositoryServerHost().then(fileRepositoryServerHost => {
      _this.fileRepositoryServerHost = fileRepositoryServerHost;
      preview(`${fileRepositoryServerHost}/wopi/files/${file.dataUuid}?access_token=${_this.$widget._$USER.token}`, {
        target: _this.openTarget
      });
    });
  }

  getFileRepositoryServerHost() {
    if (this.fileRepositoryServerHost) {
      return Promise.resolve(this.fileRepositoryServerHost);
    } else {
      return EASY_ENV_IS_BROWSER ? this.$widget.$clientCommonApi.getSystemParamValue('sys.context.path') : Promise.resolve(null);
    }
  }

  /**
   *
   * @param {*} file
   */
  openFolder(file) {
    const _this = this;
    _this.$widget.openFolder(file);
  }

  /**
   * 下载文件
   *
   * @param {*} files
   */
  download(files) {
    const _this = this;
    if (!files || !files.length) {
      _this.$widget.$message.error(_this.$widget.$t('WidgetFileManager.message.selectFile', '请选择文件（夹）！'));
      return;
    }

    let fileUuids = [];
    let folderUuids = [];
    files.forEach(file => {
      if (_this.isFolder(file)) {
        folderUuids.push(file.uuid);
      } else {
        fileUuids.push(file.uuid);
      }
    });

    let promise = null;
    // 检验文件是否存在
    if (fileUuids.length) {
      promise = $axios
        .post('/json/data/services', {
          serviceName: 'dmsFileManagerService',
          methodName: 'checkFileDataExists',
          args: JSON.stringify([fileUuids])
        })
        .then(({ data: result }) => {
          if (!result.data) {
            _this.$widget.$message.error(_this.$widget.$t('WidgetFileManager.message.unableToDownload', '文件数据不存在，无法下载！'));
            return false;
          } else {
            return true;
          }
        });
    } else {
      promise = Promise.resolve(true);
    }

    // 下载处理
    promise.then(valid => {
      if (valid) {
        download({
          url: '/proxy/api/dms/file/download',
          data: {
            fileUuid: fileUuids.join(';'),
            folderUuid: folderUuids.join(';')
          }
        });
      }
    });
  }

  /**
   * 删除文件
   *
   * @param {*} files
   */
  delete(files) {
    const _this = this;
    if (!files || !files.length) {
      _this.$widget.$message.error(_this.$widget.$t('WidgetFileManager.message.selectFile', '请选择文件（夹）！'));
      return;
    }

    let folderUuids = files.filter(file => _this.isFolder(file)).map(file => file.uuid);
    let fileNames = files.filter(file => !!file.name).map(file => file.name);
    _this.$widget.$confirm({
      title: _this.$widget.$t('WidgetFileManager.confirm', '确认'),
      content: `${_this.$widget.$t('WidgetFileManager.confirmDelete', '确认删除文件（夹）')}${fileNames.length ? '[' + fileNames + ']' : ''
        }？`,
      okText: _this.$widget.locale.Popconfirm.okText,
      cancelText: _this.$widget.locale.Popconfirm.cancelText,
      onOk() {
        $axios
          .post('/json/data/services', {
            serviceName: 'dmsFileManagerService',
            methodName: 'deleteFile',
            args: JSON.stringify([files])
          })
          .then(({ data: result }) => {
            if (result.success) {
              _this.$widget.$message.success(_this.$widget.$t('WidgetFileManager.message.deleteSuccess', '删除成功！'));
              _this.$widget.refresh({ deletedTreeNodeIds: folderUuids });
            } else {
              _this.$widget.$message.error(result.msg || _this.$widget.$t('WidgetFileManager.message.deleteFailed', '删除失败！'));
            }
          })
          .catch(({ response }) => {
            _this.$widget.$message.error(
              (response && response.data && response.data.msg) || _this.$widget.$t('WidgetFileManager.message.serverError', '服务异常！')
            );
          });
      }
    });
  }

  /**
   * 恢复文件
   *
   * @param {*} files
   */
  restore(files) {
    const _this = this;
    if (!files || !files.length) {
      _this.$widget.$message.error(_this.$widget.$t('WidgetFileManager.message.selectFile', '请选择文件（夹）！'));
      return;
    }

    let fileNames = files.filter(file => !!file.name).map(file => file.name);
    _this.$widget.$confirm({
      title: _this.$widget.$t('WidgetFileManager.confirm', '确认'),
      content: `${_this.$widget.$t('WidgetFileManager.confirmRestore', '确认恢复文件（夹）')}${fileNames.length ? '[' + fileNames + ']' : ''
        }？`,
      okText: _this.$widget.locale.Popconfirm.okText,
      cancelText: _this.$widget.locale.Popconfirm.cancelText,
      onOk() {
        $axios
          .post('/json/data/services', {
            serviceName: 'dmsFileManagerService',
            methodName: 'restoreFile',
            args: JSON.stringify([files])
          })
          .then(({ data: result }) => {
            if (result.success) {
              _this.$widget.$message.success(_this.$widget.$t('WidgetFileManager.message.restoreSuccess', '恢复成功！'));
              _this.$widget.refresh({ reloadTree: true });
            } else {
              _this.$widget.$message.error(result.msg || _this.$widget.$t('WidgetFileManager.message.restoreFailed', '恢复失败！'));
            }
          })
          .catch(({ response }) => {
            _this.$widget.$message.error(
              (response && response.data && response.data.msg) || _this.$widget.$t('WidgetFileManager.message.serverError', '服务异常！')
            );
          });
      }
    });
  }

  /**
   * 重命名
   *
   * @param {*} files
   */
  rename(files) {
    const _this = this;
    if (!files || !files.length) {
      _this.$widget.$message.error(_this.$widget.$t('WidgetFileManager.message.selectFile', '请选择文件（夹）！'));
      return;
    }
    if (files.length != 1) {
      _this.$widget.$message.error(_this.$widget.$t('WidgetFileManager.message.selectOneFile', '请选择一个文件（夹）！'));
      return;
    }

    let file = files[0];
    let isFolder = _this.isFolder(file);
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
        <a-modal :title="title" :visible="visible" width="550px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
          <div style="height: 100px; overflow:auto">
            <FolderForm ref="folderForm" :label="label" :initName="initName" :file="file"></FolderForm>
          </div>
        </a-modal>
      </a-config-provider>`,
      components: { FolderForm: () => import('./folder-form.vue') },
      i18n: _this.$widget.$i18n,
      data: function () {
        return {
          title: `${isFolder
            ? _this.$widget.$t('WidgetFileManager.renameFolder', '重命名文件夹')
            : _this.$widget.$t('WidgetFileManager.renameFile', '重命名文件')
            }`,
          visible: true,
          locale: _this.$widget.locale,
          label: isFolder
            ? _this.$widget.$t('WidgetFileManager.folderName', '夹名称')
            : _this.$widget.$t('WidgetFileManager.fileName', '文件名称'),
          initName: isFolder ? file.name : _this.getFileBaseName(file.name),
          file
        };
      },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          this.$refs.folderForm.collect().then(folder => {
            let newName = folder.name + (isFolder ? '' : '.' + _this.getFileExtension(file.name));
            _this
              .saveRenameFile(file.uuid, newName, isFolder)
              .then(({ data: result }) => {
                if (result.success) {
                  _this.$widget.$message.success(_this.$widget.$t('WidgetFileManager.message.renameSuccess', '重命名成功！'));
                  _this.$widget.refresh({ updateTreeNode: { id: file.uuid, name: newName } });
                  this.visible = false;
                  this.$destroy();
                } else {
                  _this.$widget.$message.error(result.msg || _this.$widget.$t('WidgetFileManager.message.renameFailed', '重命名失败！'));
                }
              })
              .catch(({ response }) => {
                _this.$widget.$message.error(
                  (response && response.data && response.data.msg) ||
                  _this.$widget.$t('WidgetFileManager.message.serverError', '服务异常！')
                );
              });
          });
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  /**
   * 保存重命名的文件
   *
   * @param {*} uuid
   * @param {*} name
   * @param {*} isFolder
   */
  saveRenameFile(uuid, name, isFolder) {
    return $axios.post('/json/data/services', {
      serviceName: 'dmsFileManagerService',
      methodName: isFolder ? 'renameFolder' : 'renameFile',
      args: JSON.stringify([uuid, name])
    });
  }

  /**
   * 复制文件
   *
   * @param {*} files
   */
  copy(files) {
    const _this = this;
    if (!files || !files.length) {
      _this.$widget.$message.error(_this.$widget.$t('WidgetFileManager.message.selectFile', '请选择文件（夹）！'));
      return;
    }

    let rootFolder = _this.$widget.getRootFolder();
    let excludeFolderUuids = []; // files.map(file => file.uuid);
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
        <a-modal :title="title" :visible="visible" width="550px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
          <FolderTreeSelect ref="folderTree" :folderUuid="folderUuid" :excludeFolderUuids="excludeFolderUuids"></FolderTreeSelect>
        </a-modal>
      </a-config-provider>`,
      components: { FolderTreeSelect: () => import('./folder-tree-select.vue') },
      i18n: _this.$widget.$i18n,
      data: function () {
        return {
          title: _this.$widget.$t('WidgetFileManager.copyTo', '复制到'),
          visible: true,
          locale: _this.$widget.locale,
          folderUuid: rootFolder.uuid,
          excludeFolderUuids
        };
      },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          this.$refs.folderTree.collect().then(folder => {
            this.visible = false;
            _this.$widget.showMask(_this.$widget.$t('WidgetFileManager.message.copying', '复制中...'));
            _this
              .copyFileTo(files, folder.uuid)
              .then(({ data: result }) => {
                if (result.success) {
                  _this.$widget.$message.success(_this.$widget.$t('WidgetFileManager.message.copySuccess', '复制成功！'));
                  _this.$widget.refresh({
                    reloadTree: true
                    // reloadTreeNode: {
                    //   id: folder.uuid, name: folder.name
                    // }
                  });
                  this.$destroy();
                } else {
                  _this.$widget.$message.error(result.msg || _this.$widget.$t('WidgetFileManager.message.copyFailed', '复制失败！'));
                }
                _this.$widget.hideMask();
              })
              .catch(({ response }) => {
                _this.$widget.hideMask();
                let responseData = response && response.data;
                if (responseData && responseData.errorCode == 'BusinessException' && responseData.data) {
                  _this.$widget.$message.error(
                    responseData.data || _this.$widget.$t('WidgetFileManager.message.serverError', '服务异常！')
                  );
                } else {
                  _this.$widget.$message.error(
                    (response && response.data && response.data.msg) ||
                    _this.$widget.$t('WidgetFileManager.message.serverError', '服务异常！')
                  );
                }
              });
          });
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  /**
   * 文件复制到
   * @param {*} files
   * @param {*} destFolderUuid
   */
  copyFileTo(files, destFolderUuid) {
    return $axios.post('/json/data/services', {
      serviceName: 'dmsFileManagerService',
      methodName: 'copyFile',
      args: JSON.stringify([files, destFolderUuid])
    });
  }

  /**
   * 移动文件
   *
   * @param {*} files
   */
  move(files) {
    const _this = this;
    if (!files || !files.length) {
      _this.$widget.$message.error(_this.$widget.$t('WidgetFileManager.message.selectFile', '请选择文件（夹）！'));
      return;
    }

    let rootFolder = _this.$widget.getRootFolder();
    let excludeFolderUuids = files.map(file => file.uuid);
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
        <a-modal :title="title" :visible="visible" width="550px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
          <FolderTreeSelect ref="folderTree" :folderUuid="folderUuid" :excludeFolderUuids="excludeFolderUuids"></FolderTreeSelect>
        </a-modal>
      </a-config-provider>`,
      components: { FolderTreeSelect: () => import('./folder-tree-select.vue') },
      i18n: _this.$widget.$i18n,
      data: function () {
        return {
          title: _this.$widget.$t('WidgetFileManager.removeTo', '移动到'),
          visible: true,
          locale: _this.$widget.locale,
          folderUuid: rootFolder.uuid,
          excludeFolderUuids
        };
      },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          this.$refs.folderTree.collect().then(folder => {
            this.visible = false;
            _this.$widget.showMask(_this.$widget.$t('WidgetFileManager.message.removing', '移动中...'));
            _this
              .moveFileTo(files, folder.uuid)
              .then(({ data: result }) => {
                if (result.success) {
                  _this.$widget.$message.success(_this.$widget.$t('WidgetFileManager.message.removeSuccess', '移动成功！'));
                  _this.$widget.refresh({
                    reloadTree: true
                    // reloadTreeNode: { id: folder.uuid, name: folder.name },
                    // deletedTreeNodeIds: files.map(file => file.uuid)
                  });
                  this.$destroy();
                } else {
                  _this.$widget.$message.error(result.msg || _this.$widget.$t('WidgetFileManager.message.removeFailed', '移动失败！'));
                }
                _this.$widget.hideMask();
              })
              .catch(({ response }) => {
                _this.$widget.hideMask();
                let responseData = response && response.data;
                if (responseData && responseData.errorCode == 'BusinessException' && responseData.data) {
                  _this.$widget.$message.error(
                    responseData.data || _this.$widget.$t('WidgetFileManager.message.serverError', '服务异常！')
                  );
                } else {
                  _this.$widget.$message.error(
                    (response && response.data && response.data.msg) ||
                    _this.$widget.$t('WidgetFileManager.message.serverError', '服务异常！')
                  );
                }
              });
          });
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  /**
   * 文件移动到
   * @param {*} files
   * @param {*} destFolderUuid
   */
  moveFileTo(files, destFolderUuid) {
    return $axios.post('/json/data/services', {
      serviceName: 'dmsFileManagerService',
      methodName: 'moveFile',
      args: JSON.stringify([files, destFolderUuid])
    });
  }

  /**
   * 查看属性
   *
   * @param {*} files
   */
  viewAttributes(files) {
    const _this = this;
    if (!files || !files.length) {
      _this.$widget.$message.error(_this.$widget.$t('WidgetFileManager.message.selectFile', '请选择文件（夹）！'));
      return;
    }
    if (files.length != 1) {
      _this.$widget.$message.error(_this.$widget.$t('WidgetFileManager.message.selectOneFile', '请选择一个文件（夹）！'));
      return;
    }

    let file = files[0];
    let isFolder = _this.isFolder(file);
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
        <a-modal :title="title" :visible="visible" width="700px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
          <FileAttributeDetails ref="attribute" :fileManager="fileManager" :file="file"></FileAttributeDetails>
        </a-modal>
      </a-config-provider>`,
      components: { FileAttributeDetails: () => import('./file-attribute-details.vue') },
      i18n: _this.$widget.$i18n,
      data: function () {
        return {
          title: isFolder
            ? _this.$widget.$t('WidgetFileManager.viewFolderProperty', '查看文件夹属性')
            : _this.$widget.$t('WidgetFileManager.viewFileProperty', '查看文件属性'),
          visible: true,
          locale: _this.$widget.locale,
          fileManager: _this,
          file
        };
      },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          this.$refs.attribute.collect().then(folderConfiguration => {
            if (folderConfiguration) {
              _this
                .saveFolderConfiguration(folderConfiguration)
                .then(({ data: result }) => {
                  if (result.success) {
                    _this.$widget.$message.success(_this.$widget.$t('WidgetFileManager.message.saveSuccess', '保存成功！'));
                    _this.$widget.refresh({ reloadTree: true });
                  } else {
                    _this.$widget.message.error(result.msg || _this.$widget.$t('WidgetFileManager.message.saveFailed', '保存失败！'));
                  }
                })
                .catch(({ response }) => {
                  _this.$widget.$message.error(
                    (response && response.data && response.data.msg) ||
                    _this.$widget.$t('WidgetFileManager.message.serverError', '服务异常！')
                  );
                });
            }
            this.visible = false;
            this.$destroy();
          });
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  saveFolderConfiguration(folderConfiguration) {
    return $axios.post('/json/data/services', {
      serviceName: 'dmsFileManagerService',
      methodName: 'saveFolderConfiguration',
      args: JSON.stringify([folderConfiguration])
    });
  }

  /**
   * 全文检索
   *
   * @param {*}
   */
  fulltextQuery() {
    const _this = this;
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
        <a-modal :title="title" :visible="visible" width="900px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel" :footer="null">
          <FileFulltextQuery ref="fulltextQuery" :fileManager="fileManager"></FileFulltextQuery>
        </a-modal>
      </a-config-provider>`,
      components: { FileFulltextQuery: () => import('./file-fulltext-query.vue') },
      i18n: _this.$widget.$i18n,
      data: function () {
        return {
          title: _this.$widget.$t('WidgetFileManager.fullTextSearch', `全文检索`),
          visible: true,
          locale: _this.$widget.locale,
          fileManager: _this
        };
      },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          this.visible = false;
          this.$destroy();
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  /**
   * 添加归属系统前缀
   *
   * @param {*} url
   * @returns
   */
  addSystemPrefix(url) {
    const _this = this;
    if (_this.$widget._$SYSTEM_ID && url && !url.startsWith('/sys/')) {
      url = `/sys/${_this.$widget._$SYSTEM_ID}/_${url}`;
    }
    return url;
  }

  /**
   * 判断文件是否为夹
   *
   * @param {*} file
   * @returns
   */
  isFolder(file) {
    return 'application/folder' == file.contentType;
  }

  /**
   * 判断文件是否为表单
   *
   * @param {*} file
   * @returns
   */
  isDyform(file) {
    return 'application/dyform' == file.contentType || (file.contentType && file.contentType.includes('dyform'));
  }

  /**
   * 获取文件基本名
   *
   * @param {*} fileName
   * @returns
   */
  getFileBaseName(fileName) {
    var lfn = fileName.toLowerCase();
    var len = lfn.length;
    var start = lfn.lastIndexOf('.');
    if (start !== -1) {
      var basename = lfn.substring(0, start);
      return basename;
    }
    return fileName;
  }

  /**
   * 获取文件扩展名
   *
   * @param {*} fileName
   * @returns
   */
  getFileExtension(fileName) {
    var lfn = fileName.toLowerCase();
    var len = lfn.length;
    var start = lfn.lastIndexOf('.');
    if (start !== -1) {
      var ext = lfn.substring(start + 1, len);
      return ext;
    }
    return '';
  }

  /**
   * 获取夹操作权限
   *
   * @param {*} folderUuid
   */
  getFolderActions(folderUuid) {
    const _this = this;
    if (_this.fileActionMap[folderUuid]) {
      return Promise.resolve(_this.fileActionMap[folderUuid]);
    }
    return $axios
      .get(`/proxy/api/dms/file/getFolderActions/${folderUuid}`)
      .then(({ data: result }) => {
        _this.fileActionMap[folderUuid] = new FileActions(result.data || [], folderUuid, true);
        return _this.fileActionMap[folderUuid];
      })
      .catch(() => {
        return new FileActions([], folderUuid, true);
      });
  }

  getFileActions(files) {
    const _this = this;
    let folderUuids = [];
    let fileUuids = [];
    let fileActions = [];
    files.forEach(file => {
      if (_this.fileActionMap[file.uuid]) {
        fileActions.push(_this.fileActionMap[file.uuid]);
        return;
      }
      if (_this.isFolder(file)) {
        folderUuids.push(file.uuid);
      } else {
        fileUuids.push(file.uuid);
      }
    });

    if (folderUuids.length || fileUuids.length) {
      return $axios
        .post(`/proxy/api/dms/file/getFileActions`, {
          folderUuids,
          fileUuids
        })
        .then(({ data: result }) => {
          let resultMap = result.data;
          if (resultMap) {
            for (let uuid in resultMap) {
              let actions = new FileActions(resultMap[uuid], uuid, folderUuids.includes(uuid));
              _this.fileActionMap[uuid] = actions;
              fileActions.push(actions);
            }
          }

          return fileActions;
        })
        .catch(() => {
          return [];
        });
    } else {
      return Promise.resolve(fileActions);
    }
  }

  getFilesDataPermission(files, union = false) {
    const _this = this;
    return _this.getFileActions(files).then(fileActions => {
      if (fileActions.length == 1) {
        return fileActions[0].getDataPermission();
      } else {
        // 并联
        if (union) {
          return _this.getMultiFileUnionDataPermission(fileActions);
        } else {
          return _this.getMultiFileIntersectDataPermission(fileActions);
        }
      }
    });
  }

  getMultiFileUnionDataPermission(fileActions) {
    let dataPermissions = [];
    fileActions.forEach(fileAction => {
      dataPermissions = dataPermissions.concat(fileAction.getDataPermission());
    });
    return dataPermissions;
  }

  getMultiFileIntersectDataPermission(fileActions) {
    let dataPermissions = [];
    MULTI_FILE_DATA_PERMISSION.forEach(dataPermission => {
      for (let index = 0; index < fileActions.length; index++) {
        let fileAction = fileActions[index];
        if (!fileAction.hasDataPermission(dataPermission)) {
          return;
        }
      }
      dataPermissions.push(dataPermission);
    });
    return dataPermissions;
  }
}

class FileActions {
  constructor(actions, fileUuid, isFolder) {
    this.actions = actions;
    this.fileUuid = fileUuid;
    this.isFolder = isFolder;
    this.actionMap = new Map();
    this.actions.forEach(action => {
      if (action.isCreator) {
        this.isCreator = action.isCreator;
      }
      this.actionMap.set(action.id, action);
    });
  }

  /**
   * 是否可移动夹
   */
  isAllowMoveFolder() {
    return this.actionMap.has('moveFolder') && this.actionMap.has('createFolder');
  }

  isAllowEditFolderAttributes() {
    return this.isFolder && this.actionMap.has('editFolderAttributes');
  }

  getActionCodes() {
    return [...this.actionMap.keys()];
  }

  getDataPermission() {
    const _this = this;
    let actions = _this.getActionCodes();
    // 上传文件数据权限
    if (actions.includes('createFile')) {
      actions.push('uploadFile');
    }
    if (actions.includes('downloadFile')) {
      actions.push('download');
    }
    if ((_this.isFolder && actions.includes('deleteFolder')) || (!_this.isFolder && actions.includes('deleteFile'))) {
      actions.push('delete');
    }
    if (actions.includes('restoreFile')) {
      actions.push('restore');
    }
    if ((_this.isFolder && actions.includes('renameFolder')) || (!_this.isFolder && actions.includes('renameFile'))) {
      actions.push('rename');
    }
    if ((_this.isFolder && actions.includes('copyFolder')) || (!_this.isFolder && actions.includes('copyFile'))) {
      actions.push('copy');
    }
    if ((_this.isFolder && actions.includes('moveFolder')) || (!_this.isFolder && actions.includes('moveFile'))) {
      actions.push('move');
    }
    if ((_this.isFolder && actions.includes('viewFolderAttributes')) || (!_this.isFolder && actions.includes('viewFileAttributes'))) {
      actions.push('viewAttributes');
    }
    // 文件创建者可读取文件、编辑文件
    if (!_this.isFolder && _this.isCreator) {
      if (!actions.includes('readFile')) {
        actions.push('readFile');
      }
      if (!actions.includes('editFile')) {
        actions.push('editFile');
      }
    }
    return actions;
  }

  getDataPermissionOfCreate() {
    const _this = this;
    let dataPermissions = [];
    if (_this.actionMap.has('createFolder')) {
      dataPermissions.push('createFolder');
    }
    if (_this.actionMap.has('createFile')) {
      dataPermissions.push('uploadFile');
    }
    if (_this.actionMap.has('createDocument')) {
      dataPermissions.push('createDocument');
    }
    return dataPermissions;
  }

  hasDataPermission(dataPermission) {
    return this.getDataPermission().includes(dataPermission);
  }
}

export default FileManager;
