define(['mui', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'DyformExplain'], function (
  $,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  DyformExplain
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;

  var ACL_ROLE_UNREAD = 'UNREAD';
  var aclRoleServices = {
    DRAFT: 'workV53Service.getDraft',
    TODO: 'mobileWorkService.getTodo',
    DONE: 'mobileWorkService.getDone',
    OVER: 'mobileWorkService.getOver',
    FLAG_READ: 'mobileWorkService.getRead',
    UNREAD: 'mobileWorkService.getUnread', // openToRead
    ATTENTION: 'mobileWorkService.getAttention',
    SUPERVISE: 'mobileWorkService.getSupervise',
    MONITOR: 'mobileWorkService.getMonitor'
  };
  var getOpinionCategoryService = 'workV53Service.getCurrentUserOpinion2Sign'; //"workFlowOpinionService.getAllOpinionCategoryBeans";
  // 侧滑菜单容器
  var offCanvasSelector = '.mui-wf-off-canvas-wrap';
  var offCanvas =
    '<div class="mui-wf-off-canvas-wrap mui-off-canvas-wrap mui-draggable">\
						<aside class="mui-off-canvas-right">\
							<div class="work-process-viewer">\
								<div class="title">办理过程</div>\
								<div class="work-process-viewer-content">\
									<div class="mui-scroll-wrapper">\
										<div class="mui-scroll content work-process-tab-pane">\
										</div>\
									</div>\
								</div>\
							</div>\
						</aside>\
						<div class="mui-wf-inner-wrap mui-inner-wrap">\
							<div class="mui-off-canvas-backdrop"></div>\
						</div>\
					</div>';
  var opinionEditorPrototype = {
    init: function () {
      var self = this;
      var isRequiredOpinion = self.options.signOpinion || false;
      isRequiredOpinion = isRequiredOpinion || self.isRequiredSubmitOpinion();
      isRequiredOpinion = isRequiredOpinion || self.isRequiredTransferOpinion();
      isRequiredOpinion = isRequiredOpinion || self.isRequiredCounterSignOpinion();
      isRequiredOpinion = isRequiredOpinion || self.isRequiredRollbackOpinion();
      isRequiredOpinion = isRequiredOpinion || self.isRequiredRemindOpinion();
      isRequiredOpinion = isRequiredOpinion || self.isRequiredHandOverOpinion();
      isRequiredOpinion = isRequiredOpinion || self.isRequiredGotoTaskOpinion();
      if (isRequiredOpinion === false) {
        self.hide();
      } else {
        self.show();
      }
    },
    setSignOpinionHolder: function (signOpinionPlaceholder) {
      var self = this;
      self.signOpinionPlaceholder = signOpinionPlaceholder;
    },
    showDialog: function (showOptions) {
      var _self = this;
      // 签署意见确定回调
      if (showOptions.onOk == null) {
        showOptions.onOk = function (data) {
          var workViewElement = $(_self.workView.$element)[0];
          var opinionInput = workViewElement.querySelector('.mui-sign-opinion>input');
          if (opinionInput != null) {
            opinionInput.value = data.text;
          }
        };
      }
      var opinionCategories = {};
      var workData = _self.workView.getWorkData();
      server.JDS.call({
        service: getOpinionCategoryService,
        data: [workData.flowDefUuid, workData.taskId],
        async: false,
        success: function (result) {
          var categories = [];
          var opinionInfo = result.data;
          // 用户最近使用、常用意见组装成意见分类需要的格式
          categories.push({
            uuid: 'recentOpinion',
            name: '最近使用',
            opinions: opinionInfo.recents || []
          });
          var userOpinionCategories = opinionInfo.userOpinionCategories || [];
          if (userOpinionCategories.length > 0) {
            categories.push({
              uuid: 'publicOpinion',
              name: '常用意见',
              opinions: userOpinionCategories[0].opinions || []
            });
          }
          _self.categories = categories;
          opinionCategories.categories = _self.categories;
        }
      });
      var opinionEditorTpl = 'mui-WorkFlowOpinionEditor';
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var html = templateEngine.renderById(opinionEditorTpl, opinionCategories);
      var options = {
        message: html,
        onEscape: function () {},
        shown: function () {
          var dialog = this;
          _self.dialog = dialog;
          _self.opinionElm = this.querySelector('.wf-opinion');
          _self.textarea = _self.opinionElm.querySelector('textarea');
          // 初始化
          _self.textarea.value = _self.opinion.text;

          var deceleration = mui.os.ios ? 0.0003 : 0.0009;
          $('.wf-opinion-category-scroll').scroll({
            bounce: false,
            indicators: true, // 是否显示滚动条
            deceleration: deceleration
          });

          $(_self.opinionElm).on('tap', '.wf-opinion-category', function (e) {
            var categoryUuid = this.getAttribute('categoryUuid');
            _self.textarea.blur();
            this.focus();
            $.each(_self.categories, function (index, category) {
              if (category.uuid + '' === categoryUuid) {
                var opinions = category.opinions;
                if (opinions.length > 0) {
                  dialog.querySelector('.wf-opinion-view').classList.remove('mui-hidden');
                } else {
                  dialog.querySelector('.wf-opinion-view').classList.add('mui-hidden');
                }
                var sb = new StringBuilder();
                $.each(opinions, function (i, opinion) {
                  var opinionLi = '<li class="mui-table-view-cell wf-opinion-content">{0}</li>';
                  sb.appendFormat(opinionLi, opinion.content);
                });
                dialog.querySelector('.wf-opinion-view-content').innerHTML = sb.toString();
                $(_self.opinionElm.querySelector('.wf-opinion-view-content-scroll'))
                  .scroll({
                    deceleration: 0.0005
                    // flick 减速系数，系数越大，滚动速度越慢，滚动距离越小，默认值0.0006
                  })
                  .refresh();
              }
            });
          });
          mui.trigger(document.querySelector('.wf-opinion-category-first'), 'tap');
          $(_self.opinionElm).on('tap', 'textarea', function (e) {
            _self.dialog.querySelector('.wf-opinion-view').classList.add('mui-hidden');
          });
          $(_self.opinionElm).on('tap', '.wf-opinion-content', function (e) {
            var value = _self.textarea.value + this.innerHTML.trim();
            if (value.length > 1000) {
              value = value.substring(0, 1000);
            }
            _self.textarea.value = value;
          });
        }
      };
      options.buttons = {};
      options.buttons.cancel = {
        label: '取消',
        className: 'btn-default',
        callback: function () {}
      };
      options.buttons.ok = {
        label: '确定',
        className: 'btn-primary',
        callback: function () {
          var value = _self.textarea.value;
          if (value && value.length > 1000) {
            $.toast('签署意见最多1000个字,当前为' + value.length);
            return false;
          }
          _self.opinion.text = value;
          if ($.isFunction(showOptions.onOk)) {
            showOptions.onOk.call(this, _self.opinion);
          }
          // 保存本地
          _self.store();
          return true;
        }
      };
      if (showOptions && showOptions.buttons && showOptions.buttons.confirm) {
        var confirm = showOptions.buttons.confirm;
        var confirmCallback = confirm.callback;
        options.buttons.confirm = confirm;
        options.buttons.confirm.callback = function () {
          _self.opinion.text = _self.textarea.value;
          if ($.isFunction(showOptions.onOk)) {
            showOptions.onOk.call(this, _self.opinion);
          }
          confirmCallback.call(confirm.callbackScope);
        };
      }
      appModal.dialog(options);
    },
    // 打开签署意见
    openToSignIfRequired: function (options) {
      var _self = this;
      var action = options.action;
      var methodName = 'isRequired' + StringUtils.capitalise(action) + 'Opinion';
      var isRequired = _self[methodName].call(_self);
      if (isRequired === false || _self.requiredSignOpinion === false) {
        return false;
      }
      var text = _self.opinion.text;
      if (StringUtils.isBlank(text)) {
        _self.show(options);
        return true;
      }
      return false;
    },
    // 收集签署的意见
    collectOpinion: function () {
      var _self = this;
      var workData = _self.workView && _self.workView.getWorkData();
      if (_self.textarea) {
        var html = _self.textarea.value;
        _self.opinion.text = html;
      } else if (workData.opinionText) {
        _self.opinion.text = workData.opinionText;
      }
      return _self.opinion;
    },
    // 获取签署的意见
    getOpinion: function () {
      var self = this;
      return self.collectOpinion();
    },
    show: function (options) {
      var self = this;
      if (self.signOpinionPlaceholder) {
        self.signOpinionPlaceholder.classList.remove('mui-hidden');
      }
      if (!options || !options.action) {
        return;
      }
      self.showDialog(options);
    },
    hide: function () {
      var self = this;
      if (self.signOpinionPlaceholder) {
        self.signOpinionPlaceholder.classList.add('mui-hidden');
      }
    }
  };
  var shareDataTpl = 'pt-mobile-workflow-flow-share-datas';
  var distributeInfoTpl = 'pt-mobile-workflow-flow-distribute-info';
  var taskOperationTpl = 'pt-mobile-workflow-flow-task-operation';
  var currentItem;
  var shareDataViewerPrototype = {
    // 显示承办信息
    _showUndertakeSituation: function (undertakeSituationPlaceHolder, shareDatas) {
      var _self = this;
      var dyform = _self.workView.dyform;
      var $container = _self.workView.element;
      var blockId = dyform.getBlockId(undertakeSituationPlaceHolder);
      var $undertakeSituation = $("div[id='" + blockId + "']", $container[0]);
      var processActions = [];

      // shareDatas[0].buttons
      var buttonIaArr = [];
      if (shareDatas.length > 0) {
        var buttons = shareDatas[0].buttons;
        buttons.forEach(function (button) {
          buttonIaArr.push(button.id);
        });
      }
      if (buttonIaArr.indexOf('remind') > -1) {
        processActions.push({
          id: 'remind',
          name: '催办',
          callback: function (event, panel) {
            _self.remind([currentItem]);
          }
        });
      }
      // if(buttonIaArr.indexOf("send-message")>-1){
      //     processActions.push(
      //         {
      //             id : "send-message",
      //             name : "信息分发",
      //             callback : function(event, panel) {
      //                 _self.sendMessage(currentItem);
      //             }
      //         }
      //     );
      // }
      if (buttonIaArr.indexOf('limit-time') > -1) {
        processActions.push({
          id: 'limit-time',
          name: '协办时限',
          callback: function (event, panel) {
            var dataSource = $('#mobile-wf-flow-share-datas')[0].getAttribute('dataSource');
            _self.limitTime([currentItem], dataSource);
          }
        });
      }
      if (buttonIaArr.indexOf('redo') > -1) {
        processActions.push({
          id: 'redo1',
          name: '重办',
          callback: function (event, panel) {
            _self.redo([currentItem]);
          }
        });
      }
      if (buttonIaArr.indexOf('stop') > -1) {
        processActions.push({
          id: 'stop',
          name: '终止',
          callback: function (event, panel) {
            _self.stop([currentItem], {});
          }
        });
      }

      // 协办流程，不可操作
      $.each(shareDatas, function () {
        if (this.isMajor != true && this.isSupervise != true) {
          this.isOver = true;
        }
      });

      // add columnObject
      $.each(shareDatas, function (shareDataIndex, shareDataItem) {
        $.each(shareDataItem.shareItems, function (shareItemIndex, shareItem) {
          shareItem.columnObject = {};
          $.each(shareItem.columnValues, function (index, columnValue) {
            shareItem.columnObject[shareDataItem.columns[index].index] = columnValue.value;
            if (columnValue.type === 'attach') {
              var attachHtml = '';
              if (columnValue.value && columnValue.value.length && columnValue.value.length > 0) {
                for (var i = 0; i < columnValue.value.length; i++) {
                  var attachElement = columnValue.value[i];
                  attachHtml +=
                    '<div><a fileId="' + attachElement.fileID + '" class="share-file-item">' + attachElement.fileName + '</a></div>';
                }
              }
              shareItem.columnObject[shareDataItem.columns[index].index + '_attachHtml'] = attachHtml;
            }
          });
        });
      });

      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var text = templateEngine.renderById(shareDataTpl, {
        shareDatas: shareDatas
      });
      $($undertakeSituation[0].parentNode).append(text);
      var $scrollContent = $($undertakeSituation[0].parentNode);
      $scrollContent.on('tap', 'ul.mui-table-view>li.mui-table-view-cell', function (event) {
        var cell = this;
        var shareIdx = cell.getAttribute('share-data-index');
        var idx = cell.getAttribute('data-index');
        if ((currentItem = shareDatas[shareIdx].shareItems[idx])) {
          var itemDetailHtml = '<ul class="mui-table-view">';

          $.each(shareDatas[shareIdx].columns, function (index, column) {
            var value = '';
            if (currentItem.columnValues[index].type === 'attach') {
              value = currentItem.columnObject[column.index + '_attachHtml'];
            } else {
              value = currentItem.columnValues[index].value;
            }

            itemDetailHtml += '<li class="mui-table-view-cell">';

            if (column.index === 'workProcesses') {
              //办理过程
              itemDetailHtml +=
                '<h4>' +
                column.name +
                '</h4><h6><div class="work-process-details" taskInstUuid="' +
                currentItem.taskInstUuid +
                '" flowInstUuid="' +
                currentItem.flowInstUuid +
                '"></div></h6>';
            } else {
              itemDetailHtml += '<h4>' + column.name + '</h4><h6>' + (value || '无') + '</h6>';
            }
            itemDetailHtml += '</li>';
          });
          itemDetailHtml += '</ul>';
          var detailPanel = formBuilder.showPanel({
            title: currentItem.columnValues[0].value,
            optButton: null,
            moreNum: 5,
            actions: processActions,
            content: itemDetailHtml,
            container: '#subflow-process-detail'
          });
          $(detailPanel).on('tap', 'a.share-file-item', function () {
            var fileId = this.getAttribute('fileId');
            var fileName = this.innerText;
            $.trigger(document.body, 'file.show', {
              fileObj: {
                fileId: fileId,
                fileName: fileName
              }
            });
          });

          //加载办理过程信息
          _self._loadWorkProcessDetailsByFlowInstUuid(currentItem.flowInstUuid);
        }
      });
      dyform.hideBlock(undertakeSituationPlaceHolder);

      // 加载办理过程详细信息
      _self._loadWorkProcessDetails(shareDatas);

      // 绑定承办事件
      _self.bindUndertakeSituationEvents();
    },
    // 显示信息分发
    _showInfoDistribution: function (infoDistributionPlaceHolder, distributeInfos) {
      var _self = this;
      var dyform = _self.workView.dyform;
      var $container = _self.workView.element;
      var blockId = dyform.getBlockId(infoDistributionPlaceHolder);
      var $infoDistribution = $("div[id='" + blockId + "']", $container[0]);
      //$infoDistribution = $infoDistribution.closest("table");
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var text = templateEngine.renderById(distributeInfoTpl, {
        distributeInfos: distributeInfos
      });
      $($infoDistribution[0].parentNode).append(text);
      var $scrollContent = $($infoDistribution[0].parentNode);
      $scrollContent.on('tap', 'ul.mui-table-view>li.mui-table-view-cell', function (event) {
        var cell = this;
        var distributeInfoIndex = cell.getAttribute('distribute-info-index');
        var idx = cell.getAttribute('data-index');
        if ((currentItem = distributeInfos[distributeInfoIndex].distributeInfos[idx])) {
          var attachHtml = '';
          try {
            if (currentItem.logicFileInfos && currentItem.logicFileInfos.length > 0) {
              for (var i = 0; i < currentItem.logicFileInfos.length; i++) {
                var attachElement = currentItem.logicFileInfos[i];
                attachHtml +=
                  '<div><a fileId="' + attachElement.fileID + '" class="share-file-item">' + attachElement.fileName + '</a></div>';
              }
            }
          } catch (e) {}

          var itemDetailHtml = '<ul class="mui-table-view">';
          itemDetailHtml += '<li class="mui-table-view-cell">';
          itemDetailHtml += '<h4>分发人</h4><h6 >' + currentItem.distributorName + '</h6>';
          itemDetailHtml += '</li>';
          itemDetailHtml += '<li class="mui-table-view-cell">';
          itemDetailHtml += '<h4>分发时间</h4><h6 >' + currentItem.createTime + '</h6>';
          itemDetailHtml += '</li>';
          itemDetailHtml += '<li class="mui-table-view-cell">';
          itemDetailHtml += '<h4>分发内容</h4><h6 >' + (currentItem.content || '无') + '</h6>';
          itemDetailHtml += '</li>';
          itemDetailHtml += '<li class="mui-table-view-cell">';
          itemDetailHtml += '<h4>附件</h4><h6 >' + (attachHtml || '无') + '</h6>';
          itemDetailHtml += '</li>';
          itemDetailHtml += '</ul>';
          var detailPanel = formBuilder.showPanel({
            title: currentItem.createTime,
            optButton: null,
            moreNum: 5,
            // actions : processActions,
            content: itemDetailHtml,
            container: '#subflow-process-detail'
          });
          $(detailPanel).on('tap', 'a.share-file-item', function () {
            var fileId = this.getAttribute('fileId');
            var fileName = this.innerText;
            $.trigger(document.body, 'file.show', {
              fileObj: {
                fileId: fileId,
                fileName: fileName
              }
            });
          });
        }
      });

      dyform.hideBlock(infoDistributionPlaceHolder);
      $('.wf-distribute-info', $container[0]).on('click', '.filename', function () {
        var fileId = $(this)[0].getAttribute('fileId');
        FileDownloadUtils.downloadMongoFile({
          fileId: fileId
        });
      });
    },
    // 显示操作记录
    _showOperationRecord: function (operationRecordPlaceHolder, subflowRelateOperations) {
      var _self = this;
      var dyform = _self.workView.dyform;
      var $container = _self.workView.element;
      var blockId = dyform.getBlockId(operationRecordPlaceHolder);
      var $operationRecord = $("div[id='" + blockId + "']", $container[0]);
      //$operationRecord = $operationRecord.closest("table");
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var text = templateEngine.renderById(taskOperationTpl, {
        taskOperations: subflowRelateOperations
      });
      $($operationRecord[0].parentNode).append(text);
      var $scrollContent = $($operationRecord[0].parentNode);
      $scrollContent.on('tap', 'ul.mui-table-view>li.mui-table-view-cell', function (event) {
        var cell = this;
        var operationIndex = cell.getAttribute('operation-index');
        var idx = cell.getAttribute('data-index');
        if ((currentItem = subflowRelateOperations[operationIndex].operations[idx])) {
          var attachHtml = '';
          try {
            if (currentItem.logicFileInfos && currentItem.logicFileInfos.length > 0) {
              for (var i = 0; i < currentItem.logicFileInfos.length; i++) {
                var attachElement = currentItem.logicFileInfos[i];
                attachHtml +=
                  '<div><a fileId="' + attachElement.fileID + '" class="share-file-item">' + attachElement.fileName + '</a></div>';
              }
            }
          } catch (e) {}

          var itemDetailHtml = '<ul class="mui-table-view">';
          itemDetailHtml += '<li class="mui-table-view-cell">';
          itemDetailHtml += '<h4>操作人员</h4><h6 >' + currentItem.assigneeName + '</h6>';
          itemDetailHtml += '</li>';
          itemDetailHtml += '<li class="mui-table-view-cell">';
          itemDetailHtml += '<h4>操作名称</h4><h6 >' + currentItem.action + '</h6>';
          itemDetailHtml += '</li>';
          itemDetailHtml += '<li class="mui-table-view-cell">';
          itemDetailHtml += '<h4>操作对象</h4><h6 >' + (currentItem.userName || '无') + '</h6>';
          itemDetailHtml += '</li>';
          itemDetailHtml += '<li class="mui-table-view-cell">';
          itemDetailHtml += '<h4>操作时间</h4><h6 >' + (currentItem.createTime || '无') + '</h6>';
          itemDetailHtml += '</li>';
          itemDetailHtml += '<li class="mui-table-view-cell">';
          itemDetailHtml += '<h4>详情</h4><h6 >' + (currentItem.opinionText || '无') + '</h6>';
          itemDetailHtml += '</li>';
          itemDetailHtml += '</ul>';
          var detailPanel = formBuilder.showPanel({
            title: currentItem.action,
            optButton: null,
            moreNum: 5,
            // actions : processActions,
            content: itemDetailHtml,
            container: '#subflow-process-detail'
          });
        }
      });

      dyform.hideBlock(operationRecordPlaceHolder);
    },
    // 加载分支办理过程详细信息
    _loadWorkProcessDetailsByFlowInstUuid: function (flowInstUuid) {
      var _self = this;
      var $processDetails = $('#subflow-process-detail .work-process-details');
      if ($processDetails.length == 0) {
        return;
      }
      var flowInstUuids = [];
      flowInstUuids.push(flowInstUuid);
      server.JDS.call({
        service: 'workService.getWorkProcesses',
        data: [flowInstUuids],
        version: '',
        success: function (result) {
          var data = result.data;
          _self._showWorkProcessDetails(data);
        }
      });
    },
    // 显示办理过程详细信息
    _showWorkProcessDetails: function (data) {
      var _self = this;
      var $processDetails = $('#subflow-process-detail .work-process-details');
      $.each($processDetails, function () {
        var flowInstUuid = this.getAttribute('flowInstUuid');
        var processes = data[flowInstUuid];
        if (processes) {
          var sb = new StringBuilder();
          $.each(processes, function (i, process) {
            var taskName = process.taskName;
            var assignee = process.assignee;
            var submitTime = process.submitTime;
            var opinion = process.opinion;
            var endTime = process.endTime;
            if (endTime != null) {
              sb.append('<u>' + taskName + '(' + assignee + submitTime + ')');
              if (opinion == null) {
                opinion = '';
              }
              sb.append('<p style="line-height: 100%;padding-left: 2em;margin:0">' + opinion + '</p>');
              sb.append('<p/>');
            }
          });
          $(this).html(sb.toString());
        }
      });
    },
    // 检查选择项
    checkSelection: function ($btn) {
      var $checkbox = $('input[name=checkOne]:checked', $btn.closest('.wf-share-data'));
      if ($checkbox.length == 0) {
        appModal.error('至少选择一条记录');
        return false;
      }
      return true;
    },
    // 获取选择的数据
    getSelection: function ($btn) {
      var _self = this;
      var $wfShareData = $btn.closest('.wf-share-data');
      var belongToTaskInstUuid = $('.btn-group-header', $wfShareData)[0].getAttribute('belongToTaskInstUuid');
      var shareData = _self.getShareData(belongToTaskInstUuid);
      if (shareData == null) {
        return [];
      }
      var $checkboxes = $('input[name=checkOne]:checked', $wfShareData);
      var returnItems = [];
      var shareItems = shareData.shareItems;
      for (var i = 0; i < shareItems.length; i++) {
        var shareItem = shareItems[i];
        $.each($checkboxes, function () {
          if (shareItem.taskInstUuid == this.getAttribute('taskInstUuid')) {
            returnItems.push(shareItem);
          }
        });
      }
      return returnItems;
    },
    // 获取当前按钮操作的行数据
    getRowData: function ($btn) {
      var _self = this;
      var $wfShareData = $btn.closest('.wf-share-data');
      var belongToTaskInstUuid = $('.btn-group-header', $wfShareData)[0].getAttribute('belongToTaskInstUuid');
      var shareData = _self.getShareData(belongToTaskInstUuid);
      if (shareData == null) {
        return [];
      }
      var $checkbox = $('input[name=checkOne]', $btn.closest('tr'));
      var flowInstUuid = $checkbox.getAttribute('flowinstuuid');
      var shareItems = shareData.shareItems;
      for (var i = 0; i < shareItems.length; i++) {
        var shareItem = shareItems[i];
        if (shareItem.flowInstUuid == flowInstUuid) {
          return shareItem;
        }
      }
      return {};
    },
    // 绑定承办事件
    bindUndertakeSituationEvents: function ($wfShareData) {
      var _self = this;
      // 表头操作
      // 添加承办部门(人)
      $('.share-data-header', $wfShareData).on('tap', 'button.btn-add-subflow', function () {
        var title = $(this)[0].innerText;
        var actionType = $(this)[0].getAttribute('btnid');
        var $groupHeader = $(this).closest('.btn-group-header');
        var dataSource = $groupHeader.getAttribute('dataSource');
        var businessType = $groupHeader.getAttribute('businessType');
        var businessRole = $groupHeader.getAttribute('businessRole');
        var belongToTaskInstUuid = $groupHeader.getAttribute('belongToTaskInstUuid');
        var shareData = _self.getShareData(belongToTaskInstUuid);
        _self.addSubflow({
          title: title,
          businessType: businessType,
          businessRole: businessRole,
          shareData: shareData,
          actionType: actionType,
          dataSource: dataSource
        });
      });
      // 添加主办
      $('.share-data-header', $wfShareData).on('tap', 'button.btn-add-major-flow', function () {
        var title = $(this).innerText;
        var actionType = $(this)[0].getAttribute('btnid');
        var $groupHeader = $(this).closest('.btn-group-header');
        var dataSource = $groupHeader.getAttribute('dataSource');
        var businessType = $groupHeader.getAttribute('businessType');
        var businessRole = $groupHeader.getAttribute('businessRole');
        var belongToTaskInstUuid = $groupHeader.getAttribute('belongToTaskInstUuid');
        var shareData = _self.getShareData(belongToTaskInstUuid);
        _self.addSubflow({
          title: title,
          businessType: businessType,
          businessRole: businessRole,
          shareData: shareData,
          actionType: actionType,
          dataSource: dataSource,
          isMajor: '1'
        });
      });
      // 添加协办
      $('.share-data-header', $wfShareData).on('tap', 'button.btn-add-minor-flow', function () {
        var title = $(this).innerText;
        var actionType = $(this)[0].getAttribute('btnid');
        var $groupHeader = $(this).closest('.btn-group-header');
        var dataSource = $groupHeader.getAttribute('dataSource');
        var businessType = $groupHeader.getAttribute('businessType');
        var businessRole = $groupHeader.getAttribute('businessRole');
        var belongToTaskInstUuid = $groupHeader.getAttribute('belongToTaskInstUuid');
        var shareData = _self.getShareData(belongToTaskInstUuid);
        _self.addSubflow({
          title: title,
          businessType: businessType,
          businessRole: businessRole,
          shareData: shareData,
          dataSource: dataSource,
          actionType: actionType,
          isMajor: '2'
        });
      });
      // 催办
      $('.share-data-header', $wfShareData).on('tap', 'button.btn-remind', function () {
        if (!_self.checkSelection($(this))) {
          return;
        }
        var selection = _self.getSelection($(this));
        _self.remind(selection);
      });
      // 重办
      $('.share-data-header', $wfShareData).on('tap', 'button.btn-redo', function () {
        if (!_self.checkSelection($(this))) {
          return;
        }
        var $groupHeader = $(this).closest('.btn-group-header');
        var dataSource = $groupHeader.getAttribute('dataSource');
        var selection = _self.getSelection($(this));
        _self.redo(selection, dataSource);
      });
      // 终止
      $('.share-data-header', $wfShareData).on('tap', 'button.btn-stop', function () {
        if (!_self.checkSelection($(this))) {
          return;
        }
        var $groupHeader = $(this).closest('.btn-group-header');
        var dataSource = $groupHeader.getAttribute('dataSource');
        var selection = _self.getSelection($(this));
        _self.stop(selection, {}, dataSource);
      });
      // 信息分发
      $('.share-data-header', $wfShareData).on('tap', 'button.btn-send-message', function () {
        if (!_self.checkSelection($(this))) {
          return;
        }
        var selection = _self.getSelection($(this));
        _self.sendMessage(selection);
      });
      // 协办时限
      $('.share-data-header', $wfShareData).on('tap', 'button.btn-limit-time', function () {
        if (!_self.checkSelection($(this))) {
          return;
        }
        var $groupHeader = $(this).closest('.btn-group-header');
        var dataSource = $groupHeader.getAttribute('dataSource');
        var selection = _self.getSelection($(this));
        _self.limitTime(selection, dataSource);
      });

      // 行操作
      // 催办
      $('.table', $wfShareData).on('tap', 'button.btn-remind', function () {
        var rowData = _self.getRowData($(this));
        _self.remind([rowData]);
      });
      // 重办
      $('.table', $wfShareData).on('tap', 'button.btn-redo', function () {
        var rowData = _self.getRowData($(this));
        _self.redo([rowData]);
      });
      // 终止
      $('.table', $wfShareData).on('tap', 'button.btn-stop', function () {
        var rowData = _self.getRowData($(this));
        _self.stop([rowData], {});
      });
      // 附件下载
      $('.table', $wfShareData).on('tap', 'a.share-file-item', function () {
        var fileId = this.getAttribute('fileId');
        var fileName = this.innerText;
        $.trigger(document.body, 'file.show', {
          fileObj: {
            fileId: fileId,
            fileName: fileName
          }
        });
      });
      //查看子流程
      $('.table', $wfShareData).on('tap', 'a.share-work-item', function () {
        var hrefObj = this;
        var options = {
          showShareDataViewer: false
        };
        options.workData = {};
        var href = hrefObj.getAttribute('href');
        var params = new URLSearchParams(hrefObj.search);
        var taskInstUuid = params.get('taskInstUuid');
        var flowInstUuid = params.get('flowInstUuid');
        server.JDS.call({
          service: 'mobileWorkService.getWork',
          data: [taskInstUuid, flowInstUuid],
          async: false,
          success: function (result) {
            options.workData = result.data;
          }
        });
        var workViewId = 'wf_work_view_' + commons.UUID.createUUID();
        var wrapper = document.createElement('div');
        wrapper.id = workViewId;
        var pageContainer = appContext.getPageContainer();
        var renderPlaceholder = pageContainer.getRenderPlaceholder();
        renderPlaceholder[0].appendChild(wrapper);
        formBuilder.buildPanel({
          title: '',
          container: '#' + workViewId
        });
        $.ui.loadContent('#' + workViewId);
        $('#' + workViewId).workView(options);
      });
      // 全选/不选
      $('.table', $wfShareData).on('change', "input[name='checkAll']", function () {
        var vChecked = this.checked;
        $(".table input[name='checkOne']", _self.element).each(function (idx, element) {
          element.checked = vChecked;
        });
      });
    },
    sendMessage: function (selection) {
      $.toast('请在PC端操作');
    },
    addSubflowWithDefaultOptions: function (options) {
      $.toast('请在PC端操作');
    }
  };
  var WorkViewProxy = function (element, options) {
    this.options = options;
    this.$body = $(document.body);
    this.$element = $(element);
    this.$element[0].classList.add('wf-work-view');
    this._createWorkView();
  };
  // 创建流程二开
  WorkViewProxy.prototype._createWorkView = function () {
    this._create(function () {
      this._init();
      this._loadData();
    });
  };
  // 创建并绑定事件
  WorkViewProxy.prototype._create = function (callback) {
    var _self = this;
    if (_self.options.workData == null) {
      server.JDS.call({
        service: 'workV53Service.newWorkById',
        data: [_self.options.flowDefId],
        async: false,
        success: function (result) {
          _self.options.workData = result.data;
        }
      });
    }
    var workViewModule = _self.options.workViewModule;
    var customJsModule = _self.options.workData.customJsModule || 'WorkView';
    var customJsModules = [customJsModule, 'WorkFlowWorkProcessViewer', 'WorkFlowOpinionEditor', 'WorkFlowErrorHandler'];
    if (_self.options.workData.customJsFragmentModule) {
      customJsModules.push(_self.options.workData.customJsFragmentModule);
    }
    appContext.require(
      customJsModules,
      function (customWorkViewModule, WorkFlowWorkProcessViewer, WorkFlowOpinionEditor, WorkFlowErrorHandler, customJsFragmentModule) {
        var WorkView = function ($element) {
          customWorkViewModule.call(this, $element);
          this.$element = $element;
        };
        if (workViewModule == null) {
          workViewModule = customWorkViewModule;
        }
        if (customJsFragmentModule) {
          var workViewModule2 = workViewModule;
          var InnerWorkView = function ($element) {
            workViewModule2.call(this, $element);
            this.$element = $element;
          };
          commons.inherit(InnerWorkView, workViewModule2, customJsFragmentModule);
          workViewModule = InnerWorkView;
        }
        var notAllowAppTip = '此环节不允许在手机端保存及提交！';
        commons.inherit(WorkView, workViewModule, {
          createSidebar: function () {
            var self = this;
            self.createProcessViewer();
            self.createOpinionEditor();
          },
          reload: function () {
            var _self = this;
            var workData = _self.getWorkData();
            var taskInstUuid = workData.taskInstUuid;
            var flowInstUuid = workData.flowInstUuid;
            var options = {};
            options.workData = {};
            // 是否草稿工作
            if (_self.isDraft() || StringUtils.isBlank(taskInstUuid)) {
              server.JDS.call({
                service: aclRoleServices['DRAFT'],
                data: [flowInstUuid],
                async: false,
                success: function (result) {
                  options.workData = result.data;
                }
              });
            } else {
              var aclRole = _self.getAclRole();
              var data = [];
              data.push(taskInstUuid);
              data.push(flowInstUuid);
              if (aclRole === ACL_ROLE_UNREAD) {
                data.push(true);
              }
              server.JDS.call({
                service: aclRoleServices[aclRole],
                data: data,
                async: false,
                success: function (result) {
                  options.workData = result.data;
                }
              });
            }

            var wrapper = document.createElement('div');
            var workViewId = 'wf_work_view_' + commons.UUID.createUUID();
            wrapper.id = workViewId;
            var pageContainer = appContext.getPageContainer();
            var renderPlaceholder = pageContainer.getRenderPlaceholder();
            renderPlaceholder[0].appendChild(wrapper);
            formBuilder.buildPanel({
              title: options.workData.title,
              container: '#' + workViewId
            });
            $.ui.loadContent('#' + workViewId, true);
            options.workViewId = workViewId;
            $('#' + workViewId).workView(options);
            var workPanel = _self.$element && _self.$element[0];
            if (workPanel && workPanel.parentNode) {
              _self.$element = null;
              workPanel.parentNode.removeChild(workPanel);
            }
          },
          onLoadSuccess: function () {
            var _workView = this;
            // 创建流程单据内容点位符
            createWorkViewOffCanvas.apply(_self, arguments);
            // 创建流程单据内容点位符
            createWorkViewContent.apply(_self, arguments);
            // 创建内容操作按钮
            createActionSheel.apply(_self, arguments);
            // 调用父类提交方法
            _workView._superApply(arguments);
            // 提示不允许提交及保存
            var workData = _workView.getWorkData();
            $.ui.setTitle(_self.options.title || workData.title);
            if (workData.isAllowApp === '0') {
              appModal.info(notAllowAppTip);
            }
            $.trigger(this.$element[0], 'WorkViewCreationComplete');
          },
          // 创建子流程共享数据查看器
          createShareDataViewer: function () {
            var _self = this;
            var workData = _self.workFlow.getWorkData();
            if (workData.branchTaskData != null || workData.subTaskData != null) {
              appContext.require(['WorkFlowShareDataViewer'], function (WorkFlowShareDataViewer) {
                if (WorkFlowShareDataViewer.overWrite == null) {
                  $.extend(WorkFlowShareDataViewer.prototype, shareDataViewerPrototype);
                  WorkFlowShareDataViewer.overWrite = true;
                }
                var shareDataViewer = new WorkFlowShareDataViewer(_self, workData.branchTaskData, workData.subTaskData);
                _self.shareDataViewer = shareDataViewer;
                // 确保表单已初始化成功
                var t = 5;
                (function cb() {
                  if (_self.dyformInitSuccess) {
                    shareDataViewer.init();
                  } else {
                    t-- > 0 && setTimeout(cb, 1000);
                  }
                })();
              });
            }
          },
          // 表单初始化成功处理
          onDyformInitSuccess: function () {
            var _self = this;
            // 修改表单顶部tab布局时，表单按钮被覆盖
            var $wfTabItems = $('#' + _self.options.workViewId + ' .wf-tab-bar .mui-tab-item');
            if ($wfTabItems && $wfTabItems.length > 0) {
              var $dyformTabItems = $('#' + _self.options.workViewId + ' .mui-dyform .mui-bar-tab');
              $.each($dyformTabItems, function () {
                this.classList.add('wf-work-view-dyform-tab-bar');
              });
              if ($dyformTabItems.length > 0) {
                var $tabFooter = $('#' + _self.options.workViewId + ' .wf-work-view-footer');
                $.each($tabFooter, function () {
                  this.classList.add('wf-work-view-dyform-tab-bar');
                });
              }
            }
            // 调用父类提交方法
            this._superApply(arguments);
            _self.dyformInitSuccess = true;
            $.trigger(_self.$element[0], 'WorkViewDyformInitSuccess');
          },
          // 获取当前操作的按钮名称
          getButtonLabel: function (defaultLabel) {
            if (this.currentEvent == null) {
              return defaultLabel;
            }
            var target = this.currentEvent.target;
            var muiLabel = target;
            var tmpMuiLabel = target.querySelector('.mui-label');
            if (tmpMuiLabel != null) {
              muiLabel = tmpMuiLabel;
            }
            var label = muiLabel.innerHTML;
            if (StringUtils.isBlank(label)) {
              return defaultLabel;
            }
            return label;
          },
          // 重写提交方法
          submit: function () {
            var _self = this;
            // 提示不允许提交及保存
            var workData = _self.getWorkData();
            if (workData.isAllowApp === '0') {
              appModal.error(notAllowAppTip);
              return;
            }
            // 调用父类提交方法
            _self._superApply(arguments);
          },
          // 重写保存方法
          save: function () {
            var _self = this;
            // 提示不允许提交及保存
            var workData = _self.getWorkData();
            if (workData.isAllowApp === '0') {
              appModal.error(notAllowAppTip);
              return;
            }
            // 调用父类提交方法
            _self._superApply(arguments);
          },
          // 自定义动态按钮提交
          collectIfUseCustomDynamicButton: function () {
            var _self = this;
            if (_self.currentEvent == null) {
              return;
            }
            var workData = _self.workFlow.getWorkData();
            var target = _self.currentEvent.target;
            var $btn = $(target).closest('.mui-table-view-cell[id],.mui-tab-item[id]');
            //var $btn = target.parentNode;
            //if("A" === target.tagName) {
            //$btn = target;
            //}
            var btnId = $btn && $btn.getAttribute('id');
            var btn_submit = _self.submitButtonCode;
            // 处理自定义动态按钮
            var customDynamicButton = {};
            if (btnId && btnId !== btn_submit) {
              customDynamicButton.id = btnId;
              customDynamicButton.code = $btn.getAttribute('name');
              customDynamicButton.newCode = btnId;
            } else {
              customDynamicButton = null;
            }
            workData.customDynamicButton = customDynamicButton;
          },
          // 保存成功
          onSaveSuccess: function (result) {
            var _self = this;
            appModal.alert({
              message: '保存成功！'
            });
            _self.reload();
          },
          // 与前环节相同自动提交，不支持
          _saveUserSubmit: function (result) {
            var self = this;
            var options = {};
            var data = result.data;
            var submitTaskInstUuid = data.sameUserSubmitTaskInstUuid;
            var submitTaskOperationUuid = data.sameUserSubmitTaskOperationUuid;
            // 与前环节相同自动提交
            if (StringUtils.isNotBlank(submitTaskInstUuid) && StringUtils.isNotBlank(submitTaskOperationUuid)) {
              server.JDS.call({
                service: aclRoleServices['TODO'], // 待办
                data: [submitTaskInstUuid, data.flowInstUuid],
                async: false,
                success: function (result) {
                  options.workData = result.data;
                }
              });
              var opinion = self.opinionEditor.getOpinion() || {};
              var opinionLabel = opinion.label || '';
              var opinionValue = opinion.value || '';
              var opinionText = opinion.text || '';
              $.extend(options, {
                autoSubmit: true,
                signOpinionModel: 1,
                opinionEditor: {
                  opinionLabel: opinionLabel,
                  opinionValue: opinionValue,
                  opinionText: opinionText
                }
              });
              options.workViewModule = workViewModule;
              var wrapper = document.createElement('div');
              var workViewId = 'wf_work_view_' + commons.UUID.createUUID();
              options.workViewId = wrapper.id = workViewId;
              var pageContainer = appContext.getPageContainer();
              var renderPlaceholder = pageContainer.getRenderPlaceholder();
              renderPlaceholder[0].appendChild(wrapper);
              formBuilder.buildPanel({
                title: options.workData.title,
                container: '#' + workViewId
              });
              $.ui.loadContent('#' + workViewId, true);
              // 表单异步初始化,监听表单初始化完成时间
              $(document.body).on('WorkViewDyformInitSuccess', '#' + workViewId, function (event) {
                setTimeout(function () {
                  $.trigger($('a.mui-tab-item[id=B004002]', wrapper)[0], 'tap'); // 触发自动提交
                  $(wrapper).off('WorkViewDyformInitSuccess');
                }, 200); // FIXME 从表初始化异步,主表初始化完成时,从表可能还没有初始化成功
              });
              $('#' + options.workViewId).workView(options);
              return true;
            }
            // 清空签署意见
            this.clearOpinion();
            return false;
          },
          // 撤回成功
          onCancelSuccess: function () {
            var _self = this;
            var callback = function () {
              // 打开待办工作界面
              var success = function (result) {
                if (StringUtils.isNotBlank(result.data.uuid)) {
                  // 刷新父窗口
                  var taskInstUuid = result.data.uuid;
                  var workData = _self.getWorkData();
                  workData.taskInstUuid = taskInstUuid;
                  workData.aclRole = 'TODO';
                  _self.reload();
                } else {
                  _self.handlerSuccess.call(_self, result);
                }
              };
              var failure = function (jqXHR) {
                // 处理流程操作返回 的错误信息
                appContext.getWindowManager().closeAndRefreshParent();
              };
              _self.workFlow.getTodoTaskByFlowInstUuid(success, failure);
            };
            // 提示
            appModal.alert({
              message: '撤回成功！',
              callback: callback
            });
          },
          // 签署意见
          signOpinion: function (e) {
            var options = {};
            var target = e.target;
            if (target.classList.contains('mui-sign-opinion')) {
              target = target.querySelector('input');
            }
            // 初始化
            this.opinionEditor.sign({ text: target.value });
            this.opinionEditor.showDialog(options);
          },
          // 添加自定义按钮:action = {id:"btnid",name:"btnname",text:"btntext",cssClass:"cssClass",callback:function(){}};
          addCustomAction: function (actions, before) {
            var self = this;
            var navWrapper = self.$element[0].querySelector('#wf_tab_bar_mobile_work_view>nav');
            if (typeof navWrapper == 'undefined' || navWrapper == null) {
              console.warn('#wf_tab_bar_mobile_work_view not found');
              return false;
            } else if (actions && actions.length > 0) {
              self._showOrHideOperates(true);
            }
            $.each(actions, function (idx, action) {
              var tabBarItem = navWrapper.querySelector('.btn-custom[id=' + action.id + ']');
              if (tabBarItem != null) {
                tabBarItem.parentNode.removeChild(tabBarItem);
              }
              tabBarItem = $.dom(formBuilder.buildTabBarItems([action]))[0];
              tabBarItem.classList.add('btn-custom');
              tabBarItem.addEventListener('tap', function (event) {
                if ($.isFunction(action.callback) && action.callback.apply(self, arguments) === false) {
                  return;
                }
                event.preventDefault();
                event.stopPropagation();
                return false;
              });
              if (before === true) {
                // 在前面插入
                var beforeItem = navWrapper.querySelector('a.mui-active');
                navWrapper.insertBefore(tabBarItem, beforeItem);
              } else {
                // 默认追加
                navWrapper.appendChild(tabBarItem);
              }
            });
          },
          // 移出自定义按钮[]
          removeCustomAction: function (actions) {
            var self = this;
          },
          _showOrHideOperates: function (show) {
            var self = this;
            var options = self.options;
            var dyformContent = self.$element[0].querySelector(options.dyformSelector);
            var wfTabbar = self.$element[0].querySelector('#wf_tab_bar_mobile_work_view');
            if (dyformContent && wfTabbar) {
              wfTabbar.classList[show ? 'remove' : 'add']('mui-hidden');
              dyformContent.classList[show ? 'add' : 'remove']('wui-scroll-bottom');
            }
          },
          // 特送环节
          gotoTask: function () {
            var _self = this;
            // 签署意见
            if (_self.openToSignIfRequiredGotoTask.apply(_self, arguments)) {
              return;
            }
            _self.opinionToWorkData();
            var workData = _self.workFlow.getWorkData();
            // 获取表单数据
            _self.getDyformData(function (dyFormData) {
              workData.dyFormData = dyFormData;
              var success = function () {
                _self.onGotoTaskSuccess.apply(_self, arguments);
              };
              var failure = function () {
                _self.onGotoTaskFailure.apply(_self, arguments);
              };

              var workFlow = _self.workFlow;
              var gotoTaskId = workFlow.tempData.gotoTaskId;
              if (StringUtils.isNotBlank(gotoTaskId)) {
                // // 跳转环节
                workFlow.setTempData('gotoTaskId', gotoTaskId);
                // // 办理意见
                // // workData.opinionLabel = null;
                // // workData.opinionValue = null;
                // workFlow.setTempData("opinionText", opinionText);
                workFlow.gotoTask(success, failure);
              } else if (
                workFlow.getGotoTasks(function (result, statu, jqXHR) {
                  if (result.success && result.data) {
                    var toTasks = result.data.toTasks;
                    $.each(toTasks, function (idx, task) {
                      task.text = task.name;
                      task.value = task.value;
                    });
                    formBuilder.selectEditor({
                      title: '选择特送环节',
                      multiple: false,
                      items: toTasks,
                      callback: function (valueObj, event) {
                        var workData = workFlow.getWorkData();
                        // 办理意见
                        // workData.opinionLabel = null;
                        // workData.opinionValue = null;
                        workFlow.setTempData('opinionText', '特送环节');
                        // 跳转环节
                        workFlow.setTempData('gotoTaskId', valueObj.ids.join(';'));

                        if (false === $.isEmptyObject(workData.taskUsers)) {
                          workFlow.setTempData('taskUsers', workData.taskUsers);
                        }

                        setTimeout(function (t) {
                          workFlow.gotoTask(success, failure);
                        }, 0);
                        return false;
                      }
                    });
                  } else {
                    failure.apply(_self, arguments);
                  }
                }, failure)
              );
            });
          },
          share: function () {
            var _self = this;
            var workData = _self.workFlow.getWorkData();
            // 操作动作及类型
            _self.setEventAction('撤回', 'Cancel', workData);
            $.trigger(document.body, 'workflow.share', {
              workData: workData
            });
          }
        });

        _self.workView = new WorkView(_self.$element);

        // 查看办理过程
        var ProcessViewer = function (workView, options) {
          WorkFlowWorkProcessViewer.call(this, workView, options);
        };
        commons.inherit(ProcessViewer, WorkFlowWorkProcessViewer, {
          init: function () {}
        });
        // 签署意见
        var OpinionEditor = function (workView, options) {
          WorkFlowOpinionEditor.call(this, workView, options);
        };
        commons.inherit(OpinionEditor, WorkFlowOpinionEditor, opinionEditorPrototype);
        // 流程所需内置模块
        _self.options.processViewerModule = ProcessViewer;
        _self.options.opinionEditorModule = OpinionEditor;
        _self.options.errorHandlerModule = WorkFlowErrorHandler;
        callback.call(_self);
      },
      function (err) {
        var requireModules = err.requireModules.join(';');
        $.confirm('流程打开失败,缺少模块文件:' + requireModules, function (result) {
          setTimeout(function () {
            $.ui.goBack();
          }, 0);
        });
      }
    );
    // _self._bindEvent();
  };
  // 创建侧滑菜单容器
  function createWorkViewOffCanvas() {
    var _self = this;
    if (_self.workView.isDraft() === false) {
      var offCanvasWrapper = _self.$element.closest(offCanvasSelector); //document.querySelector(offCanvasSelector);
      if (offCanvasWrapper == null) {
        //offCanvasWrapper = document.createElement("div");
        //offCanvasWrapper.style.height = "100%";
        //offCanvasWrapper.innerHTML = offCanvas;
        offCanvasWrapper = $.dom(offCanvas)[0];
        var pageContainer = appContext.getPageContainer();
        var renderPlaceholder = pageContainer.getRenderPlaceholder();
        renderPlaceholder[0].appendChild(offCanvasWrapper);
      }
      var workViewWapper = offCanvasWrapper.querySelector('.mui-wf-inner-wrap');
      workViewWapper.appendChild(_self.$element[0]);
      offCanvasWrapper.addEventListener('hidden', function (event) {
        // 隐藏右侧内容
        var aside = offCanvasWrapper.querySelector('.mui-off-canvas-right');
        var dyformAside = offCanvasWrapper.querySelector('.mui-off-canvas-left');
        if (event.target.classList.contains('mui-wf-off-canvas-wrap')) {
          aside.style.visibility = '';
          aside.style.zIndex = -1;
          aside.classList.add('mui-hidden');
          offCanvasWrapper.querySelector('.work-process-viewer').classList.add('mui-hidden');
          // 使头部保持不滚动
          var transitionings = this.querySelectorAll('.mui-transitioning');
          $.each(transitionings, function () {
            this.style.webkitTransform = '';
          });

          if (dyformAside) {
            dyformAside.style.zIndex = 999;
            dyformAside.classList.remove('mui-hidden');
          }
        } else {
          if (dyformAside) {
            dyformAside.style.zIndex = -1;
            dyformAside.classList.add('mui-hidden');
          }
        }
        // 还原元素显示
        var dyformAsideWrapper = $('.mui-dyform')[0].querySelector('.mui-off-canvas-wrap');
        if (dyformAside && dyformAsideWrapper) {
          dyformAsideWrapper.appendChild(dyformAside);
        }
        event.preventDefault();
        event.stopPropagation();
        return false;
      });
      offCanvasWrapper.addEventListener('shown', function (event) {
        // 显示右侧内容
        var aside = offCanvasWrapper.querySelector('.mui-off-canvas-right');
        var dyformAside = offCanvasWrapper.querySelector('.mui-off-canvas-left');
        if (event.target.classList.contains('mui-wf-off-canvas-wrap')) {
          aside.style.visibility = 'visible';
          aside.style.zIndex = 0;
          aside.classList.remove('mui-hidden');
          offCanvasWrapper.querySelector('.work-process-viewer').classList.remove('mui-hidden');
          _self.workView.processViewer.show();

          if (dyformAside) {
            dyformAside.style.zIndex = -1;
            dyformAside.classList.add('mui-hidden');
          }
        } else {
          // 移动元素显示
          var asideWrapper = offCanvasWrapper.querySelector(offCanvasSelector);
          if (dyformAside) {
            asideWrapper.appendChild(dyformAside);
            dyformAside.style.zIndex = 999;
            dyformAside.classList.remove('mui-hidden');
          }
        }
        event.preventDefault();
        event.stopPropagation();
        return false;
      });
      // 办理过程可滚动
      $(offCanvasWrapper.querySelector('.work-process-viewer-content .mui-scroll-wrapper')).scroll();
      var wfOffCanvas = $(offCanvasWrapper);
      wfOffCanvas.offCanvas();
      $.ui.showAndAddRightNavEventListener('tap', function (e) {
        // var wfOffCanvas = $(offCanvasSelector);
        wfOffCanvas.offCanvas('show');
        _self.workView.processViewer.show();
      });

      // 侧滑与workView面板同步显示隐藏
      _self.$element[0].addEventListener('panel.shown', function () {
        offCanvasWrapper.classList.remove('mui-hidden');
      });
      _self.$element[0].addEventListener('panel.hidden', function () {
        offCanvasWrapper.classList.add('mui-hidden');
      });
      // workView面板退出时移出offCanvas
      _self.$element[0].addEventListener('panel.back', function () {
        // offCanvasWrapper.parentNode.removeChild(offCanvasWrapper);
      });
      _self.$element[0].addEventListener('panel.remove', function () {
        offCanvasWrapper.parentNode.removeChild(offCanvasWrapper);
      });
    }
  }
  // 创建流程单据内容点位符
  function createWorkViewContent() {
    var _self = this;
    var wrapper = document.createElement('div');
    wrapper.id = 'wf_work_view_content';
    _self.$element[0].querySelector('.mui-content').appendChild(wrapper);

    var dyformWrapper = document.createElement('div');
    dyformWrapper.id = 'wf_work_view_content_dyform';
    wrapper.appendChild(dyformWrapper);

    var footerWrapper = document.createElement('div');
    footerWrapper.classList.add('wf-work-view-footer');
    wrapper.appendChild(footerWrapper);
  }
  // 创建内容操作按钮
  function createActionSheel(result) {
    var _self = this;
    var options = _self.options;
    // 分享不需要权限控制(Or "流程管理权限"勾选"分享")
    if (window.WellAdapter && (WellAdapter.qing || WellAdapter.dingtalk)) {
      result.data.buttons.push({
        name: '分享',
        id: 'B004048',
        code: 'B004048'
      });
    }
    var rightMap = _self.options.rightMap;
    // ACL角色对应的操作
    var aclRoleOpts = _self.options.toolbar[_self.options.workData.aclRole];
    var actionSheetData = [];
    var data = [];
    var actionMoreName = 'more-' + ++$.uuid;
    $.each(result.data.buttons, function () {
      // 按钮名称
      var name = this.name;
      // 按钮编号
      var code = this.code;
      // 按钮ID
      var btnId = this.id;
      // 操作名称
      var optName = rightMap[code];
      if (optName != null && _contains(aclRoleOpts, optName) && _self.workView[optName] != null) {
        if (data.length === 2) {
          data.push({
            id: actionMoreName,
            name: actionMoreName,
            text: '更多'
          });
        }
        if (data.length < 3) {
          data.push({
            id: btnId,
            name: code,
            text: name
          });
        } else {
          actionSheetData.push({
            id: btnId,
            name: code,
            text: name
          });
        }
      }
    });
    if (_self.workView.isDraft() === false) {
      $.ui.showAndAddRightNavEventListener('tap', function (e) {
        var wfOffCanvas = $(offCanvasSelector);
        wfOffCanvas.offCanvas('show');
        _self.workView.processViewer.show();
      });
    }
    // 操作按钮
    var wrapper = _self.$element[0].querySelector('#wf_tab_bar_mobile_work_view');
    if (wrapper != null) {
      wrapper.parentNode.removeChild(wrapper); // wrapper.remove();
    }
    wrapper = document.createElement('div');
    wrapper.id = 'wf_tab_bar_mobile_work_view';
    wrapper.classList.add('wf_operate');
    wrapper.classList.add('wf-tab-bar');
    _self.$element[0].appendChild(wrapper);
    // 更多操作按钮
    var acWrapper = _self.$element[0].querySelector('#wf_action_sheet_mobile_work_view');
    if (acWrapper != null) {
      acWrapper.parentNode.removeChild(acWrapper); // acWrapper.remove();
    }
    acWrapper = document.createElement('div');
    acWrapper.id = 'wf_action_sheet_mobile_work_view';
    document.body.appendChild(acWrapper);
    formBuilder.buildTabBar({
      data: data,
      container: wrapper
    });
    formBuilder.buildActionSheet({
      sheetId: actionMoreName,
      data: actionSheetData,
      container: acWrapper
    });
    // 还原签署意见
    _self.workView.restoreOpinion();
    _self.workView._showOrHideOperates(data.length > 0);

    // 添加并绑定签署意见按钮
    var tabBar = wrapper.querySelector('.mui-bar-tab');
    if (tabBar != null) {
      var signOpinionWrapper = document.createElement('a');
      signOpinionWrapper.classList.add('mui-hidden');
      signOpinionWrapper.classList.add('mui-tab-item');
      signOpinionWrapper.classList.add('mui-sign-opinion');
      signOpinionWrapper.setAttribute('name', 'B004011');
      signOpinionWrapper.innerHTML = '<input type="text" placeholder="审批意见" />'; // T签署意见
      var isRequiredOpinion = _self.options.opinionEditor.signOpinion || false;
      var opinionEditor = _self.workView.opinionEditor;
      isRequiredOpinion = isRequiredOpinion || opinionEditor.isRequiredSubmitOpinion();
      isRequiredOpinion = isRequiredOpinion || opinionEditor.isRequiredTransferOpinion();
      isRequiredOpinion = isRequiredOpinion || opinionEditor.isRequiredCounterSignOpinion();
      isRequiredOpinion = isRequiredOpinion || opinionEditor.isRequiredRollbackOpinion();
      isRequiredOpinion = isRequiredOpinion || opinionEditor.isRequiredRemindOpinion();
      isRequiredOpinion = isRequiredOpinion || opinionEditor.isRequiredHandOverOpinion();
      isRequiredOpinion = isRequiredOpinion || opinionEditor.isRequiredGotoTaskOpinion();
      if (isRequiredOpinion === false /*||  _self.workView.isDraft()*/) {
        signOpinionWrapper.classList.add('mui-hidden');
      }
      if (_self.workView.isDraft() || _self.workView.isTodo() || _self.workView.isSupervise() || _self.workView.isMonitor()) {
        tabBar.insertBefore(signOpinionWrapper, tabBar.firstChild);
        var opinionEditor = _self.workView.opinionEditor;
        opinionEditor.setSignOpinionHolder(signOpinionWrapper);
        // 获取还原的签署意见
        var opinion = opinionEditor.getOpinion();
        signOpinionWrapper.querySelector('input').value = opinion.text;
      }
    }
    // 绑定TabBar事件
    $(wrapper).on('tap', '.mui-tab-item', function (e) {
      var name = this.getAttribute('name');
      if (name === actionMoreName) {
        showMoreActionSheel(name);
      } else {
        // 绑定当前事件
        _self.workView.setCurrentEvent(e);
        _self.workView[rightMap[name]].call(_self.workView, e);
      }
      return true;
    });
    // 绑定ActionSheet事件
    $(acWrapper).on('tap', '.mui-table-view-cell', function (e) {
      var name = this.getAttribute('name');
      if (StringUtils.isNotBlank(name)) {
        // 隐藏ActionSheet操作按钮
        $('#action_sheet_' + actionMoreName).popover('toggle');
        // 绑定当前事件
        _self.workView.setCurrentEvent(e);
        _self.workView[rightMap[name]].call(_self.workView, e);
      }
      return true;
    });
    _self.$element[0].addEventListener('panel.remove', function () {
      acWrapper.parentNode.removeChild(acWrapper);
    });
  }
  function showMoreActionSheel(name) {
    $('#action_sheet_' + name).popover('toggle');
  }
  // 绑定按钮操作事件
  WorkViewProxy.prototype._bindEvent = function () {
    var _self = this;
    var toolbarPlaceholder = _self.options.toolbarPlaceholder;
    var rightMap = _self.options.rightMap;
    // ACL角色对应的操作
    var aclRoleOpts = _self.options.toolbar[_self.options.workData.aclRole];
    $('.btn', toolbarPlaceholder).each(function () {
      // 按钮编号
      var name = $(this)[0].getAttribute('name');
      // 操作名称
      var optName = rightMap[name];
      if (optName != null && _contains(aclRoleOpts, optName) && _self.workView[optName] != null) {
        $(this).show();
        var dispatcher = appContext.getDispatcher(true);
        var callback = function (options) {
          // 绑定当前事件
          _self.workView.setCurrentEvent(options.event);
          _self.workView[options.scope].call(_self.workView, options.event);
        };
        dispatcher.register(callback, optName);
        $(this).on(
          'click',
          $.proxy(function (e) {
            var options = {};
            options.scope = optName;
            options.event = e;
            dispatcher.dispatch(options);
          }, this)
        );
      }
    });
  };
  // 初始化
  WorkViewProxy.prototype._init = function () {
    this._mergeOpinionEditorOptions();
    this.workView.init(this.options);
  };
  // 收集签署意见的权限信息
  WorkViewProxy.prototype._mergeOpinionEditorOptions = function () {
    var _self = this;
    var toolbarPlaceholder = _self.options.toolbarPlaceholder;
    var requiredMap = _self.options.opinionEditor.requiredMap;
    var rightMap = _self.options.rightMap;
    $.each(_self.options.workData.buttons, function () {
      // 按钮编号
      var name = this.code;
      // 操作名称
      var optName = rightMap[name];
      var required = requiredMap[optName];
      if (optName != null && required != null) {
        _self.options.opinionEditor.required[required] = true;
      }
      // 可编辑文档参数
      if (name === _self.options.dyformEditableCode) {
        _self.options.dyformEditable = true;
      }
      if (name === _self.options.opinionEditor.buttonCode) {
        _self.options.opinionEditor.signOpinion = true;
      }
    });
  };
  // 加载数据
  WorkViewProxy.prototype._loadData = function () {
    this.workView.load();
  };

  // 判断数据是否包含指定值
  function _contains(array, value) {
    if (array == null) {
      return false;
    }
    for (var i = 0; i < array.length; i++) {
      if (array[i] === value) {
        return true;
      }
    }
    return false;
  }

  function uploadFiles() {
    appModal.showMask('文件上传中...');
    setTimeout(function () {
      var fd = new FormData();
      var files = document.getElementById('wf_file_upload').files;
      $.each(files, function (i) {
        fd.append('wf_file_upload' + i, this);
      });
      var oXHR = new XMLHttpRequest();
      oXHR.upload.addEventListener('progress', uploadProgress, false);
      oXHR.addEventListener('load', uploadFinish, false);
      oXHR.addEventListener('error', uploadError, false);
      oXHR.addEventListener('abort', uploadAbort, false);
      oXHR.open('POST', ctx + '/repository/file/mongo/savefiles');
      oXHR.send(fd);
    }, 1000);
  }
  function uploadProgress() {
    appModal.hideMask();
  }
  function uploadFinish(event) {
    var result = JSON.parse(event.target.responseText);
    var data = result.data;
    if (data != null) {
      for (var i = 0; i < data.length; i++) {
        var fileInfo = data[i];
        appModal.error(fileInfo.fileID + ':' + fileInfo.filename);
      }
    }
    appModal.hideMask();
  }
  function uploadError() {
    appModal.hideMask();
  }
  function uploadAbort() {
    appModal.hideMask();
  }

  $.fn.workView = function (options) {
    return this.each(function () {
      var $this = $(this);
      options = $.extend(true, {}, $.fn.workView.defaults, options);
      new WorkViewProxy($this, options);
    });
  };

  $.fn.workView.defaults = {
    workViewId: 'wf_work_view',
    workViewModule: null,
    processViewerModule: null,
    opinionEditorModule: null,
    errorHandlerModule: null,
    beforeServiceCallback: function () {
      appModal.showMask('正在加载流程数据...');
      var $tabItems = $('.wf-tab-bar .mui-tab-item');
      $.each($tabItems, function () {
        this.classList.add('mui-disabled');
      });
    },
    afterServiceCallback: function () {
      appModal.hideMask();
      var $tabItems = $('.wf-tab-bar .mui-tab-item');
      $.each($tabItems, function () {
        this.classList.remove('mui-disabled');
      });
    },
    opinionEditor: {
      opinionLabel: '',
      opinionValue: '',
      opinionText: '',
      signOpinionModel: 1, // 签署意见模式
      editorPlaceholder: '.row_summernote', // 编辑器点位符
      snapshotPlaceholder: '.row_sign_opinion_text', // 签署意见快照点位符
      buttonCode: 'B004011', // 是否显示签署意见框，对应按钮权限B004011
      clickButtonToShow: false, // 点击显示签署意见框，与签署意见模式配合使用
      required: {
        submit: false,
        transfer: false,
        counterSign: false,
        rollback: false,
        remind: false,
        handOver: false,
        gotoTask: false
      },
      requiredMap: {
        requiredSubmitOpinion: 'submit',
        requiredTransferOpinion: 'transfer',
        requiredCounterSignOpinion: 'counterSign',
        requiredRollbackOpinion: 'rollback',
        requiredRemindOpinion: 'remind',
        requiredHandOverOpinion: 'handOver',
        requiredGotoTaskOpinion: 'gotoTask'
      }
    },
    processViewer: {
      buttonCode: 'B004013', // 是否显示签署意见框，对应按钮权限B004011
      clickButtonToShow: true, // 点击显示签署意见框，与签署意见模式配合使用
      viewerPlaceholder: '.work-process-viewer' //
    },
    dyformSelector: '#wf_work_view_content_dyform', // 表单选择器
    dyformEditableCode: 'B004025', // 可编辑文档代码
    toolbarPlaceholder: '.wf_operate', // 操作按钮占位符
    submitButtonCode: 'B004002', // 提交按钮代码
    // 角色操作草稿(DRAFT)、待办(TODO)、已办(DONE)、办结(OVER)、已阅(FLAG_READ)、未阅(UNREAD)、关注(ATTENTION)、督办(SUPERVISE)、监控(MONITOR)
    toolbar: {
      DRAFT: ['click', 'save', 'submit'],
      TODO: [
        'click',
        'save',
        'submit',
        'rollback',
        'directRollback',
        'attention',
        /*"copyTo",*/ 'unfollow',
        'viewProcess',
        'share',
        'transfer',
        'counterSign',
        'copyTo'
      ], // "print",
      // "transfer",
      // "counterSign",
      DONE: ['click', 'cancel', 'attention' /*, "copyTo"*/, 'unfollow', 'viewProcess', 'share'], // "print",
      OVER: ['click', 'attention' /*, "copyTo"*/, 'unfollow', 'viewProcess', 'share'], // "print",
      FLAG_READ: ['click', 'attention' /*, "copyTo"*/, 'unfollow', 'viewProcess', 'share'], // "print",
      UNREAD: ['click', 'attention' /*, "copyTo"*/, 'unfollow', 'viewProcess', 'share'], // "print",
      ATTENTION: ['click' /*, "copyTo"*/, 'unfollow', 'viewProcess', 'share'], // "print",
      SUPERVISE: ['viewProcess', 'remind', 'share'],
      MONITOR: ['viewProcess', 'remind', 'handOver', 'gotoTask', 'share', 'remove']
    },
    rightMap: {
      B004000: 'click', // 点击
      B004001: 'save', // 保存
      B004002: 'submit', // 提交
      B004003: 'rollback', // 退回
      B004004: 'directRollback', // 直接退回
      B004005: 'cancel', // 撤回
      B004006: 'transfer', // 转办
      B004007: 'counterSign', // 会签
      B004008: 'attention', // 关注
      // "B004009" : "print", // 套打
      B004010: 'copyTo', // 抄送
      B004011: 'signOpinion', // 签署意见
      B004012: 'unfollow', // 取消关注
      B004013: 'viewProcess', // 查看办理过程
      B004014: 'remind', // 催办
      B004015: 'handOver', // 移交
      B004016: 'gotoTask', // 跳转
      B004017: 'suspend', // 挂起
      B004018: 'resume', // 恢复
      B004023: 'remove', // 删除
      B004025: 'editable', // 可编辑文档
      B004026: 'requiredSubmitOpinion', // 必须签署意见
      B004029: 'requiredTransferOpinion', // 转办必填意见
      B004030: 'requiredCounterSignOpinion', // 会签必填意见
      B004031: 'requiredRollbackOpinion', // 退回必填意见
      B004032: 'requiredHandOverOpinion', // 特送个人必填意见
      B004033: 'requiredGotoTaskOpinion', // 特送环节必填意见
      B004034: 'requiredRemindOpinion', // 催办环节必填意见
      B004048: 'share' // 分享
    }
  };

  return WorkViewProxy;
});
