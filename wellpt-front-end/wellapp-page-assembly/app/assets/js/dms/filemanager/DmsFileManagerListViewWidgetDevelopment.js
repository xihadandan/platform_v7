define(["jquery", "commons", "constant", "server", "appContext", "DmsDataManagementViewDevelopment",
    "DmsListViewActionBase", "DmsDataServices", "DmsFileServices", "DmsFileManagerBreadcrumbNav",
    "DmsFileOnlineViewer"
], function($, commons, constant, server, appContext, DmsDataManagementViewDevelopment,
    DmsListViewActionBase, DmsDataServices, DmsFileServices, DmsFileManagerBreadcrumbNav, DmsFileOnlineViewer) {
    var UUID = commons.UUID;
    var StringUtils = commons.StringUtils;
    var StringBuilder = commons.StringBuilder;
    var ACTION_OPEN_VIEW = "open_view";
    var ROW_CHECK_ITEM = "rowCheckItem";
    // 数据管理——视图二开
    var DmsFileManagerListViewWidgetDevelopment = function() {
        var _self = this;
        DmsDataManagementViewDevelopment.apply(_self, arguments);
        // _self.dmsFileServices = new DmsFileServices();
        _self.navSelectedItemInToolbarActions = []
    };
    commons.inherit(DmsFileManagerListViewWidgetDevelopment, DmsDataManagementViewDevelopment, {
        getTableOptions: function(bootstrapTableOptions) {
            var _self = this;
            return $.extend(bootstrapTableOptions, {
                onCheck: function(row) {
                    _self.itemSelectionChanged();
                    return false;
                },
                onUncheck: function(row) {
                    _self.itemSelectionChanged();
                    return false;
                },
                onCheckAll: function(rows) {
                    _self.itemSelectionChanged();
                    return false;
                },
                onUncheckAll: function(rows) {
                    _self.itemSelectionChanged();
                    return false;
                },
                onCheckSome: function(rows) {
                    _self.itemSelectionChanged();
                    return false;
                },
                onUncheckSome: function(rows) {
                    _self.itemSelectionChanged();
                    return false;
                }
            });
        },
        prepare: function() {
            var _self = this;
            var fileManagerWidget = _self.getFileManagerWidget();
            _self.dmsFileServices = fileManagerWidget.dmsFileServices;
            var widget = _self.getWidget();
            // 优先设置当前文件管理列表视图的listViewWidget
            if (fileManagerWidget) {
                var dataViewManager = fileManagerWidget.getCurrentDataView();
                dataViewManager.currentView.listViewWidget = widget;
            }
        },
        init: function() {
            var _self = this;
            var widget = _self.getWidget();
            var fileManagerWidget = _self.getFileManagerWidget();
            if (fileManagerWidget && fileManagerWidget.isSupportSwitchDataView()) {
                var $columnsRight = $(".fixed-table-toolbar>.columns-right", widget.element);
                var sb = new StringBuilder();
                sb.append('<button class="btn btn-default btn-list-view" type="button" title="列表视图">');
                sb.append('<i class="glyphicon glyphicon-align-justify"></i>');
                sb.append("</button>");
                $columnsRight.append(sb.toString());
                _self.$columnsRight = $columnsRight;
            }

            // 面包屑导航
            if (fileManagerWidget.isShowBreadcrumbNav() && !fileManagerWidget.isUseCustomDataView()) {
                var $tableToolbar = $(".fixed-table-toolbar", widget.element);
                var breadcrumb = new StringBuilder();
                breadcrumb.append('<div class="file-manager-breadcrumb">');
                breadcrumb.append('</div>');
                $(breadcrumb.toString()).insertAfter($tableToolbar);

                var breadcrumbOptions = {
                    container: $(widget.element),
                    dataViewManager: fileManagerWidget.dataViewManager,
                    rootNav: fileManagerWidget.getRootFolder(),
                    widget: fileManagerWidget
                };
                widget.breadcrumbNav = new DmsFileManagerBreadcrumbNav((widget.element)
                    .find(".file-manager-breadcrumb"), breadcrumbOptions);
            }

            // 绑定自定义事件
            _self.bindCustomEvents();
        },
        getFileManagerWidget: function() {
            var _self = this;
            var fileManagerWidget = _self.fileManagerWidget;
            if (fileManagerWidget != null) {
                return fileManagerWidget;
            }
            var configuration = _self.getViewConfiguration();
            if (configuration.dmsWidgetDefinition) {
                fileManagerWidget = appContext.getWidgetById(configuration.dmsWidgetDefinition.id);
                _self.fileManagerWidget = fileManagerWidget;
            }
            return fileManagerWidget;
        },
        isFileManagerDataStore: function() {
            var _self = this;
            var fileManagerWidget = _self.getFileManagerWidget();
            if (fileManagerWidget && fileManagerWidget.isFileManagerDataStore()) {
                return true;
            }
            return false;
        },
        getSelection: function() {
            return this.getWidget().getSelections();
        },
        refresh: function(force) {
            this.getWidget().refresh(force);
        },
        beforeRender: function() {
            var _self = this;
            var widget = _self.getWidget();
            var fileManagerWidget = _self.getFileManagerWidget();
            var configuration = _self.getViewConfiguration();
            if (fileManagerWidget) {
                var listFileMode = fileManagerWidget.getListFileMode();
                var folderUuid = fileManagerWidget.getCurrentFolderUuid();
                widget.removeParam("listFileMode");
                widget.addParam("listFileMode", listFileMode);
                widget.removeParam("folderUuid");
                widget.addParam("folderUuid", folderUuid);
                // 表单数据源
                if (fileManagerWidget.isFileManagerDataStore() == false &&
                    fileManagerWidget.isUseCustomFileManagerDataStore() == false) {
                    var dataProvider = _self.getDataProvider();
                    dataProvider.options.proxy = dataProvider.options.proxy || {};
                    dataProvider.options.proxy.type = "com.wellsoft.pt.dms.file.criteria.DmsFileDyformCriteria";
                    dataProvider.options.proxy.extras = {
                        dms_id: fileManagerWidget.getId()
                    };
                }
            }
        },
        beforeLoadData: function() {
            var _self = this;
            var widget = _self.getWidget();
            var configuration = _self.getViewConfiguration();
            if (configuration.dmsWidgetDefinition) {
                var fileManagerWidget = _self.getFileManagerWidget();
                if (fileManagerWidget) {
                    var listFileMode = fileManagerWidget.getListFileMode();
                    var folderUuid = fileManagerWidget.getCurrentFolderUuid();
                    widget.removeParam("listFileMode");
                    widget.addParam("listFileMode", listFileMode);
                    widget.removeParam("folderUuid");
                    widget.addParam("folderUuid", folderUuid);
                    // 文件库数据源，动态更新工具栏操作按钮
                    // if (fileManagerWidget.isFileManagerDataStore()) {
                    // 更新工具栏操作按钮
                    // 显示导航夹操作按钮
                    if (fileManagerWidget.isShowNavFolderActions()) {
                        _self.getNavFolderActions(folderUuid);
                    }
                    // }
                }
            }
            var args = Array.prototype.slice.call(arguments);
            // 调用JS二开模块的回调接口
            _self.fileManagerWidget.invokeDevelopmentMethod("beforeLoadData", args);
            // 触发文件管理加载数据前事件
            appContext.dispatchEvent("wFileManager.beforeLoadData", null, _self.fileManagerWidget);
        },
        onLoadSuccess: function() {
            var _self = this;
            // 第一次加载数据不调用选择变更处理
            if (_self.isFirstLoaded == false) {
                _self.itemSelectionChanged();
            } else {
                _self.isFirstLoaded = false;
            }
        },
        onPostBody: function(data) {
            var _self = this;
            $(".filename-icon").each(function() {
                var fileInfo = {
                    name: $(this).text()
                };
                if (_self.dmsFileServices.isImage(fileInfo)) {
                    $(this).addClass("fa fa-file-image-o");
                } else if (_self.dmsFileServices.isExcel(fileInfo)) {
                    $(this).addClass("iconfont icon-ptkj-excel excel-color");
                } else if (_self.dmsFileServices.isPowerPoint(fileInfo)) {
                    $(this).addClass("iconfont icon-ptkj-ppt ppt-color");
                } else if (_self.dmsFileServices.isWord(fileInfo)) {
                    $(this).addClass("iconfont icon-ptkj-word word-color");
                } else if (_self.dmsFileServices.isPdf(fileInfo)) {
                    $(this).addClass("iconfont icon-ptkj-pdf pdf-color");
                } else if (_self.dmsFileServices.isArchive(fileInfo)) {
                    $(this).addClass("fa fa-file-archive-o");
                } else if (_self.dmsFileServices.isVideo(fileInfo)) {
                    $(this).addClass("fa fa-file-video-o");
                } else if (_self.dmsFileServices.isAudio(fileInfo)) {
                    $(this).addClass("fa fa-file-audio-o");
                } else {
                    $(this).addClass("iconfont icon-ptkj-tongyongfujian other-color");
                }
            });
            // 调用JS二开模块的回调接口
            _self.fileManagerWidget.invokeDevelopmentMethod("onPostBody", []);
        },
        getNavFolderActions: function(folderUuid) {
            var _self = this;
            var widget = _self.getWidget();
            // 工具栏操作按钮
            var options = {
                data: {
                    folderUuid: folderUuid
                },
                success: function(result) {
                    var dataList = result.data.dataList;
                    var actions = _self.dmsFileServices.filterNavInToolbarActions(dataList);
                    _self.navSelectedItemInToolbarActions = actions;
                    actions = _self.fileManagerWidget.fillFileActionInfoFromConfiguration(actions);
                    // 调用JS二开模块的回调接口
                    _self.fileManagerWidget.invokeDevelopmentMethod("beforeUpdateToolbar", [actions]);
                    // 生成工具栏操作按钮
                    _self.updateToolbar(actions);
                }
            };
            _self.dmsFileServices.getFolderActions(options);
        },
        updateToolbar: function(fileActions) {
            var _self = this;
            var widget = _self.getWidget();
            var $toolbar = $(".fixed-table-toolbar", widget.element);
            var $bsBars = $toolbar.find(".fm-bs-bars");
            if ($bsBars.length == 0) {
                var bsBarsHtml = new StringBuilder();
                bsBarsHtml.append('<div class="bs-bars fm-bs-bars pull-left hidden">');
                bsBarsHtml.append('</div>');
                $toolbar.prepend(bsBarsHtml.toString());
                $bsBars = $toolbar.find(".fm-bs-bars");
            }
            $bsBars.html("");
            var toolbarHtml = _self.dmsFileServices.buildToolbarHtml(fileActions);
            var btnNum = 0;
            for (var i = 0; i < fileActions.length; i++) {
                if (fileActions[i].hidden == false) {
                    btnNum++;
                }
            }
            if (btnNum == 0) {
                $bsBars.addClass("hidden");
            } else {
                $bsBars.removeClass("hidden");
            }
            $bsBars.html(toolbarHtml);
            $.each(fileActions, function(i, fileAction) {
                var selector = "button[btnId='" + fileAction.id + "'],li[btnId='" + fileAction.id + "']";
                $bsBars.find(selector).data("fileAction", fileAction);
            });
        },
        // 行选中变更，更新工具栏操作按钮
        itemSelectionChanged: function() {
            var _self = this;
            // 文件库数据源，动态更新工具栏操作按钮
            if (_self.isFileManagerDataStore() || _self.getFileManagerWidget().isShowToolbarActions()) {
                var selectedItems = _self.getSelection();
                // 获取选中文件可进行的操作
                var fileItemActions = _self.dmsFileServices.getIntersectionActions(selectedItems);
                var itemActions = _self.navSelectedItemInToolbarActions.concat(fileItemActions);
                // 最后过滤允许在工具栏上展示的按钮
                _self.fileManagerWidget.checkAllowedActionsInToolbar(fileItemActions);
                itemActions = _self.fileManagerWidget.fillFileActionInfoFromConfiguration(itemActions);
                // 调用JS二开模块的回调接口
                _self.fileManagerWidget.invokeDevelopmentMethod("beforeUpdateToolbar", [itemActions]);
                // 生成工具栏操作按钮
                _self.updateToolbar(itemActions);
            }
        },
        onClickRow: function(rowNum, row, $element, field) {
            var _self = this;
            // 禁用数据项点击事件，直接返回
            if (_self.fileManagerWidget.isDisableItemClick()) {
                return;
            }
            // 调用JS二开模块的回调接口
            _self.fileManagerWidget.invokeDevelopmentMethod("onItemClick", [rowNum, row, $element]);
            _self.fileManagerWidget.trigger("wFileManager.ItemClick", {
                index: rowNum,
                item: row,
                element: $element
            });
            // 判断文件项本身是否允许打开
            if (!_self.dmsFileServices.isAllowOpen(row)) {
                return;
            }
            // 文件库视图
            // 夹打开
            // if (_self.isFileManagerDataStore()) {
            if (_self.dmsFileServices.isFolder(row)) {
                _self.openFolder(row.uuid, row);
            } else if (_self.hasOpenFilePermission(row)) {
                if (_self.dmsFileServices.isDyform(row)) {
                    // 打开文件表单数据
                    _self.openFileDyform(row.uuid, row);
                } else if (_self.dmsFileServices.isFile(row)) {
                  if (row.contentType.indexOf("workflow + dyform") > 0) {
                    //如果是流程的表单数据归档 直接打开文件表单数据
                    _self.openFileDyform(row.uuid, row);
                  } else {
                    // 文件在线预览
                    _self.viewFileOnline(row.uuid, row);
                  }
                } else {
                    // 打开夹配置的表单数据
                    _self.openDyformInFolder(row.UUID || row.uuid, row);
                }
            } else {
                console.error("click file, but no permission to open.");
            }
            // } else {
            // // 打开夹配置的表单数据
            // _self.openDyformInFolder(row.UUID, row);
            // }
        },
        onSort: function(name, order) {
            this.fileManagerWidget.invokeDevelopmentMethod("onSort", [name, order]);
        },
        // 判断夹是否有打开文件的权限
        hasOpenFilePermission: function(row) {
            var _self = this;
            // 表单文件数据源的数据，返回true
            if (StringUtils.isNotBlank(row.formUuid)) {
                return true;
            }
            // 不是文件管理数据源，返回true
            if (!_self.isFileManagerDataStore()) {
                return true;
            }
            return _self.dmsFileServices.hasFilePermission(row.uuid, "openFile");
        },
        bindCustomEvents: function() {
            var _self = this;
            var widget = _self.getWidget();
            // 视图列表切换事件
            if (_self.$columnsRight) {
                _self.$columnsRight.on("click", ".btn-list-view", function() {
                    var eventData = {
                        currentViewType: "bootstrapTableDataView",
                        switchToViewType: "fileManagerThumbnailView",
                        keyword: widget.getKeyword()
                    };
                    appContext.dispatchEvent("wFileManager.SwitchDataView", eventData, widget);
                });
            }

            // 文件操作按钮事件
            var $toolbar = $(".fixed-table-toolbar", widget.element);
            $toolbar.on("click", ".fm-bs-bars button,.fm-bs-bars .btn-group ul li", function(e) {
                var action = $(this).attr("action");
                var fileAction = $(this).data("fileAction");
                if (StringUtils.isNotBlank(action)) {
                    if (fileAction && fileAction.eventManger && !$.isEmptyObject(fileAction.eventManger.eventHandler)) {
                        var eventHandler = fileAction.eventManger.eventHandler;
                        var eventParams = fileAction.eventManger.eventParams || {};
                        var target = fileAction.target || {};
                        var opt = {
                            target: target.position,
                            targetWidgetId: target.widgetId,
                            refreshIfExists: target.refreshIfExists,
                            appId: eventHandler.id,
                            appType: eventHandler.type,
                            appPath: eventHandler.path,
                            params: $.extend({}, eventParams.params, appContext.parseEventHashParams(eventHandler, "menuid"))
                        };
                        _self.fileManagerWidget.startApp(opt);
                    } else if ($.isFunction(_self[action])) {
                        _self[action].call(_self, e, fileAction, _self);
                    } else {
                        // 调用JS二开模块的回调接口
                        _self.fileManagerWidget.invokeDevelopmentMethod(action, [e, fileAction,
                            _self.fileManagerWidget
                        ]);
                    }
                }
            });

            // 文件列表checkbox选择变化事件
            $(widget.element).on("wFileManager.ItemSelectionChanged", function(e, ui) {
                _self.itemSelectionChanged();
            });
        },
        // 新建文件夹
        createFolder: function(e, ui) {
            var _self = this;
            var dlgId = UUID.createUUID();
            var dlgIdSelector = '#' + dlgId;
            var dialogOptions = {
                title: "新建文件夹",
                size: "small",
                message: _self._getFileBoxHtml(dlgId),
                buttons: {
                    confirm: {
                        label: "确定",
                        className: "btn-primary",
                        callback: function() {
                            var $fileNameElem = $("input[name='file-name-editable']", dlgIdSelector);
                            var fileName = $fileNameElem.val();
                            if (StringUtils.isBlank(fileName)) {
                                appModal.error("文件（夹）名称不能为空，请输入名称！");
                                return false;
                            }
                            // if
                            // (!_self.dmsFileServices.isValidFileName(fileName))
                            // {
                            // appModal.error("无效的文件夹名称！");
                            // return false;
                            // }
                            var folderUuid = _self.getFileManagerWidget().getCurrentFolderUuid();
                            if (_self.dmsFileServices.checkTheSameNameForCreateFolder(fileName, folderUuid)) {
                                appModal.error("重复的文件夹名称，请输入名称！");
                                return false;
                            }
                            // 新建夹服务处理
                            var param = {};
                            param.folderUuid = folderUuid;
                            param.data = {};
                            param.data.name = fileName;
                            var options = {};
                            options.data = param;
                            options.success = function(success, statusText, jqXHR) {
                                _self.refresh();
                                appModal.info("文件夹创建成功！")
                                    // 触发文件夹创建成功事件
                                _self.fileManagerWidget.trigger("wFileManager.FolderCreated", {
                                    parentFolderUuid: param.folderUuid,
                                    newFolderUuid: success.data
                                });
                            }
                            _self.dmsFileServices.createFolder(options);
                            return true;
                        }
                    },
                    cancel: {
                        label: "取消",
                        className: "btn-default",
                        callback: function() {}
                    }
                }
            };
            appModal.dialog(dialogOptions);
        },
        _getFileBoxHtml: function(dlgId) {
            var message = new StringBuilder();
            message.appendFormat('<div id="{0}" class="form-horizontal file-name-editable">', dlgId);
            message.appendFormat('<div class="row form-group">');
            message.appendFormat('<div class="col-xs-12">');
            message.appendFormat('<input type="text" class="form-control" name="file-name-editable" value="" />');
            message.appendFormat('</div>');
            message.appendFormat('</div>');
            message.appendFormat('</div>');
            return message.toString();
        },
        // 重命名
        rename: function() {
            var _self = this;
            var selection = _self.getSelection();
            if (selection == 0 || selection.length > 1) {
                appModal.error("请选择一个文件（夹）！");
                return;
            }
            var fileItem = selection[0];
            var renameSuccess = function() {
                _self.refresh();
                appModal.info("重命名成功！");
                // 触发文件（夹）重命名成功事件
                _self.fileManagerWidget.trigger("wFileManager.ItemRenamed", {
                    item: fileItem
                });
            }
            if (_self.dmsFileServices.isFolder(fileItem)) {
                _self.renameFolder(fileItem, renameSuccess);
            } else {
                _self.renameFile(fileItem, renameSuccess);
            }
        },
        renameFolder: function(fileItem, renameSuccess) {
            var _self = this;
            var dlgId = UUID.createUUID();
            var dlgIdSelector = '#' + dlgId;
            var dialogOptions = {
                title: "重命名文件夹",
                size: "middle",
                message: _self._getFileBoxHtml(dlgId),
                shown: function() {
                    var $fileNameElem = $("input[name='file-name-editable']", dlgIdSelector);
                    $fileNameElem.val(fileItem.name);
                },
                buttons: {
                    confirm: {
                        label: "确定",
                        className: "btn-primary",
                        callback: function() {
                            var $fileNameElem = $("input[name='file-name-editable']", dlgIdSelector);
                            var fileName = $fileNameElem.val();
                            if (StringUtils.isBlank(fileName)) {
                                appModal.error("文件（夹）名称不能为空，请输入名称！");
                                return false;
                            }
                            // if
                            // (!_self.dmsFileServices.isValidFileName(fileName))
                            // {
                            // appModal.error("无效的文件夹名称！");
                            // return false;
                            // }
                            // 重命名夹
                            var renameFolderOptions = {
                                data: {
                                    folderUuid: fileItem.uuid,
                                    newFolderName: fileName
                                },
                                success: renameSuccess
                            };
                            _self.dmsFileServices.renameFolder(renameFolderOptions);
                            return true;
                        }
                    },
                    cancel: {
                        label: "取消",
                        className: "btn-default",
                        callback: function() {}
                    }
                }
            };
            appModal.dialog(dialogOptions);
        },
        // 重命名文件
        renameFile: function(fileItem, renameSuccess) {
            var _self = this;
            var basename = _self.dmsFileServices.getFileBaseName(fileItem.name);
            var extension = _self.dmsFileServices.getFileExtension(fileItem.name);
            var dlgId = UUID.createUUID();
            var dlgIdSelector = '#' + dlgId;
            var dialogOptions = {
                title: "重命名文件",
                size: "middle",
                message: _self._getFileBoxHtml(dlgId),
                shown: function() {
                    var $fileNameElem = $("input[name='file-name-editable']", dlgIdSelector);
                    $fileNameElem.val(basename);
                },
                buttons: {
                    confirm: {
                        label: "确定",
                        className: "btn-primary",
                        callback: function() {
                            var $fileNameElem = $("input[name='file-name-editable']", dlgIdSelector);
                            var fileName = $fileNameElem.val();
                            if (StringUtils.isBlank(fileName)) {
                                appModal.error("文件（夹）名称不能为空，请输入名称！");
                                return false;
                            }
                            // if
                            // (!_self.dmsFileServices.isValidFileName(fileName))
                            // {
                            // appModal.error("无效的文件名称！");
                            // return false;
                            // }
                            if (StringUtils.isNotBlank(extension)) {
                                fileName += "." + extension;
                            }
                            // 重命名文件
                            var renameFileOptions = {
                                data: {
                                    fileUuid: fileItem.uuid,
                                    newFileName: fileName
                                },
                                success: renameSuccess
                            };
                            _self.dmsFileServices.renameFile(renameFileOptions);
                            return true;
                        }
                    },
                    cancel: {
                        label: "取消",
                        className: "btn-default",
                        callback: function() {}
                    }
                }
            };
            appModal.dialog(dialogOptions);
        },
        // 移动
        move: function() {
            var _self = this;
            var fileManagerWidget = _self.getFileManagerWidget();
            var rootFolder = fileManagerWidget.getRootFolderForDialog();
            var selection = _self.getSelection();
            var options = {
                rootFolderUuid: rootFolder.uuid,
                rootFolderName: rootFolder.name,
                selection: selection,
                callback: function(source, destFolderUuid) {
                    fileManagerWidget.dataViewManager.refresh();
                    appModal.info("移动成功！");
                    // 触发文件（夹）移动成功事件
                    _self.fileManagerWidget.trigger("wFileManager.ItemMoved", {
                        source: source,
                        destFolderUuid: destFolderUuid
                    });
                }
            };
            _self.dmsFileServices.move(options);
        },
        // 复制
        copy: function() {
            var _self = this;
            var fileManagerWidget = _self.getFileManagerWidget();
            var rootFolder = fileManagerWidget.getRootFolderForDialog();
            var selection = _self.getSelection();
            var options = {
                rootFolderUuid: rootFolder.uuid,
                rootFolderName: rootFolder.name,
                selection: selection,
                callback: function(source, destFolderUuid) {
                    fileManagerWidget.dataViewManager.refresh();
                    appModal.info("复制成功！");
                    // 触发文件（夹）复制成功事件
                    _self.fileManagerWidget.trigger("wFileManager.ItemCopied", {
                        source: source,
                        destFolderUuid: destFolderUuid
                    });
                }
            };
            _self.dmsFileServices.copy(options);
        },
        // 新建文件
        createFile: function(e) {
            var _self = this;
            var widget = _self.getWidget();
            var optins = {
                ui: widget,
                e: e,
                folderUuid: _self.getFileManagerWidget().getCurrentFolderUuid(),
                fileuploaddone: function() {
                    widget.refresh();
                }
            }
            _self.dmsFileServices.uploadFile(optins);
        },
        // 新建文档
        createDocument: function(e) {
            // var _self = this;
            // var folderUuid = _self.fileManagerWidget.getCurrentFolderUuid();
            // var _self = this;
            // var urlParams = _self.dmsListViewActionBase.getUrlParams({
            // appFunction : {
            // id : "btn_file_manager_dyform_create"
            // }
            // });
            // urlParams.fd_id = folderUuid;
            // _self.dmsDataServices.openWindow({
            // urlParams : urlParams,
            // useUniqueName : false,
            // ui : _self.getFileManagerWidget()
            // });
            var _self = this;
            var folderUuid = _self.fileManagerWidget.getCurrentFolderUuid();
            var options = {
                folderUuid: folderUuid,
                ui: _self.fileManagerWidget
            }
            _self.dmsFileServices.createDocument(options);
        },
        // 查看属性
        viewAttributes: function() {
            var _self = this;
            var selection = _self.getSelection();
            var options = {
                selection: selection
            };
            _self.dmsFileServices.viewAttributes(options);
        },
        // 分享
        share: function(e) {
            var _self = this;
            var selection = _self.getSelection();
            _self.dmsFileServices.share(selection);
        },
        // 取消分享
        cancelShare: function(e) {
            var _self = this;
            var selection = _self.getSelection();
            if (selection.length == 0) {
                appModal.error("请选择操作记录！");
                return;
            }
            _self.dmsFileServices.cancelShare({
                selection: selection,
                success: function() {
                    _self.refresh();
                    appModal.info("取消分享成功！");
                }
            });
        },
        // 下载
        download: function(e) {
            var _self = this;
            var selection = _self.getSelection();
            if (selection.length == 0) {
                appModal.error("请选择操作记录！");
                return;
            }
            _self.dmsFileServices.download(selection);
        },
        // 删除
        "delete": function(e, fileAction) {
            var _self = this;
            var selection = _self.getSelection();
            var deleteOptions = {
                data: [selection],
                success: function(success) {
                    _self.refresh();
                    appModal.success("删除成功！");
                    // 触发文件（夹）删除成功事件
                    _self.fileManagerWidget.trigger("wFileManager.ItemDeleted", {
                        selection: selection
                    });
                }
            };
            var configMsg = "确认要删除所选文件（夹）吗？";
            if (fileAction && StringUtils.isNotBlank(fileAction.confirmMsg)) {
                configMsg = fileAction.confirmMsg;
            } else if (fileAction.eventManger && fileAction.eventManger.params &&
                StringUtils.isNotBlank(fileAction.eventManger.params.confirmMsg)) {
                configMsg = fileAction.eventManger.params.confirmMsg;
            }
            var physicalDelete = false;
            if (fileAction.eventManger && fileAction.eventManger.params &&
                StringUtils.isNotBlank(fileAction.eventManger.params.physicalDelete)) {
                physicalDelete = Boolean(fileAction.eventManger.params.physicalDelete);
            }
            deleteOptions.physicalDelete = physicalDelete;
            appModal.confirm(configMsg, function(result) {
                if (result) {
                    _self.dmsFileServices.deleteFile(deleteOptions);
                }
            });
        },
        // 视图夹打开
        openFolder: function(folderUuid, row) {
            var _self = this;
            var widget = _self.getWidget();
            var fileManagerWidget = _self.getFileManagerWidget();
            var oldFolderUuid = fileManagerWidget.getCurrentFolderUuid();
            fileManagerWidget.setCurrentFolderUuid(folderUuid);
            widget.removeParam("folderUuid");
            widget.addParam("folderUuid", folderUuid);
            widget.refresh();
            // 派发夹变更事件
            var eventData = {
                currentFolderUuid: folderUuid,
                currentFolderData: row,
                oldFolderUuid: oldFolderUuid
            };
            appContext.dispatchEvent("wFileManager.OpenFolder", eventData, widget);
        },
        // 打开文件表单数据
        openFileDyform: function(fileUuid, row) {
            // var _self = this;
            // var folderUuid = _self.fileManagerWidget.getCurrentFolderUuid();
            // var _self = this;
            // var urlParams = _self.dmsListViewActionBase.getUrlParams({
            // appFunction : {
            // id : "btn_file_manager_dyform_create"
            // }
            // });
            // urlParams.idValue = row.dataUuid;
            // urlParams.fd_id = folderUuid;
            // urlParams.doc_id = fileUuid;
            // _self.dmsDataServices.openWindow({
            // urlParams : urlParams,
            // useUniqueName : false,
            // ui : _self.getFileManagerWidget()
            // });
            var _self = this;
            var folderUuid = _self.fileManagerWidget.getCurrentFolderUuid();
            var options = {
                folderUuid: row.folderUuid || folderUuid,
                fileUuid: fileUuid,
                data: row,
                ui: _self.fileManagerWidget
            }
            _self.dmsFileServices.openDyform(options);
        },
        // 打开夹配置的表单数据
        openDyformInFolder: function(idValue, row) {
            var _self = this;
            var folderUuid = _self.fileManagerWidget.getCurrentFolderUuid();
            var options = {
                folderUuid: folderUuid,
                idValue: idValue,
                data: row,
                ui: _self.fileManagerWidget
            }
            _self.dmsFileServices.openDyform(options);
        },
        // 文件在线预览
        viewFileOnline: function(fileUuid, row) {
            var viewer = new DmsFileOnlineViewer();
            viewer.viewOnline(row);
        }
    });

    return DmsFileManagerListViewWidgetDevelopment;
});
