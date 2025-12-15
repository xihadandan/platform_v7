define(['jquery', 'server', 'commons', 'constant', 'appContext', 'appModal', 'multiOrg'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal,
  multiOrg
) {
  var JDS = server.JDS;
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var Browser = commons.Browser;
  var FileDownloadUtils = server.FileDownloadUtils;
  var shareDataTpl = 'pt-workflow-flow-share-datas';
  var distributeInfoTpl = 'pt-workflow-flow-distribute-info';
  var taskOperationTpl = 'pt-workflow-flow-task-operation';
  // 子流程承办情况
  var WorkFlowShareDataViewer = function (workView, branchTaskData, subTaskData) {
    var _self = this;
    _self.workView = workView;
    _self.branchTaskData = branchTaskData;
    _self.subTaskData = subTaskData;
    _self.branchTaskDataLoaded = false;
    _self.subTaskDataLoaded = false;

    _self.loopCount = 0;
    _self.loopTimer = null;
    //    _self.oldHtml = '';
    _self.dispatchErrorList = [];

    _self.resendSubData = {};
    _self.dispatchedCount = 0;

    _self.shareDatas = [];
    _self.showedWorkProcessDetails = false;
    _self.shareDataEls = {};
    _self.operationRecordEls = {};
    _self.distributeInfoEls = {};
  };
  $.extend(WorkFlowShareDataViewer.prototype, {
    // 初始化
    init: function () {
      var _self = this;
      // 加载分支流数据
      if (_self.branchTaskDataLoaded == false && _self.branchTaskData != null) {
        _self.loadBranchTaskData(function (branchTaskData) {
          _self.branchTaskData = branchTaskData;
          _self.showBranchTaskData();
          _self.workView.onShareDataViewerInit.apply(_self.workView, _self);
          _self.branchTaskDataLoaded = true;
          if ($('.widget-main').length > 0) {
            setTimeout(function () {
              $('.widget-main').length && $('.widget-main').getNiceScroll().resize();
            }, 20);
          }
        });
      }
      // 加载子流程数据
      if (_self.subTaskDataLoaded == false && _self.subTaskData != null) {
        //承办情况列表渲染
        _self._flowShareDatas();
        _self.loadSubTaskData(function (subTaskData) {
          _self.subTaskData = subTaskData;
          _self.showSubTaskData();
          _self.workView.onShareDataViewerInit.apply(_self.workView, _self);
          _self.subTaskDataLoaded = true;
          if ($('.widget-main').length > 0) {
            setTimeout(function () {
              $('.widget-main').length && $('.widget-main').getNiceScroll().resize();
            }, 20);
          }
        });
      }
    },
    // 加载子流程数据
    loadBranchTaskData: function (callback) {
      var _self = this;
      JDS.restfulPost({
        url: ctx + '/api/workflow/work/loadBranchTaskData',
        data: _self.branchTaskData,
        success: function (result) {
          if ($.isFunction(callback)) {
            callback.call(_self, result.data);
          }
        }
      });
    },
    // 显示子流程数据
    showBranchTaskData: function () {
      var _self = this;
      var branchTaskData = _self.branchTaskData;
      // 承办显示位置
      var undertakeSituationPlaceHolder = branchTaskData.undertakeSituationPlaceHolder;
      if ($.isArray(branchTaskData.shareDatas)) {
        if (StringUtils.isNotBlank(undertakeSituationPlaceHolder)) {
          _self._showBranchTaskUndertakeSituation(
            undertakeSituationPlaceHolder,
            branchTaskData.shareDatas,
            branchTaskData.businessType,
            branchTaskData.businessRole,
            branchTaskData
          );
        }
      } else if (branchTaskData.shareDatas) {
        $.each(branchTaskData.shareDatas, function (placeHolder, shareDatas) {
          _self._showBranchTaskUndertakeSituation(
            placeHolder,
            shareDatas,
            branchTaskData.businessType,
            branchTaskData.businessRole,
            branchTaskData
          );
        });
      }
      // 信息分发显示位置
      if ($.isArray(branchTaskData.distributeInfos)) {
        if (StringUtils.isNotBlank(infoDistributionPlaceHolder)) {
          _self._showBranchTaskInfoDistribution(infoDistributionPlaceHolder, branchTaskData.distributeInfos, branchTaskData);
        }
      } else if (branchTaskData.distributeInfos) {
        $.each(branchTaskData.distributeInfos, function (placeHolder, distributeInfos) {
          _self._showBranchTaskInfoDistribution(placeHolder, distributeInfos, branchTaskData);
        });
      }
      // 操作记录显示位置
      var operationRecordPlaceHolder = branchTaskData.operationRecordPlaceHolder;
      if ($.isArray(branchTaskData.taskOperations)) {
        if (StringUtils.isNotBlank(operationRecordPlaceHolder)) {
          _self._showBranchTaskOperationRecord(operationRecordPlaceHolder, branchTaskData.taskOperations, branchTaskData);
        }
      } else if (branchTaskData.taskOperations) {
        $.each(branchTaskData.taskOperations, function (placeHolder, taskOperations) {
          _self._showBranchTaskOperationRecord(placeHolder, taskOperations, branchTaskData);
        });
      }
    },
    _flowShareDatas: function () {
      var _self = this;
      JDS.restfulPost({
        url: ctx + '/proxy/api/workflow/work/getUndertakeSituationDatas',
        data: _self.subTaskData,
        success: function (result) {
          var shareDatas = result.data;
          var undertakeSituationPlaceHolder = _self.subTaskData.undertakeSituationPlaceHolder;
          var businessType = _self.subTaskData.businessType;
          var businessRole = _self.subTaskData.businessRole;
          _self.subTaskData.shareDatas[undertakeSituationPlaceHolder] = shareDatas;
          if (StringUtils.isNotBlank(undertakeSituationPlaceHolder)) {
            _self._showUndertakeSituation(undertakeSituationPlaceHolder, shareDatas, businessType, businessRole, _self.subTaskData);
          }
        }
      });
    },
    // 加载子流程数据
    loadSubTaskData: function (callback) {
      var _self = this;
      _self.subTaskData.shareDatas = {};
      _self.subTaskData.distributeInfos = {};
      _self.subTaskData.taskOperations = {};
      JDS.restfulPost({
        url: ctx + '/api/workflow/work/loadSubTaskData',
        data: _self.subTaskData,
        success: function (result) {
          if ($.isFunction(callback)) {
            callback.call(_self, result.data);
            if (_self.loopTimer) {
              clearInterval(_self.loopTimer);
            }
            var resultData = result.data;
            var shareDatas2 = resultData.shareDatas;
            _self.resendSubData = shareDatas2;
            $.each(shareDatas2, function (placeHolder, datas) {
              if (datas.length < 1) {
                return;
              }
              for (var i = 0; i < datas[0].shareItems.length; i++) {
                if (datas[0].shareItems[i].dispatchState == 0) {
                  _self.loopTimer = setInterval(function () {
                    _self.loopCount++;
                    _self.loadSubTaskData(callback);
                  }, 10 * 1000);
                  break;
                }
              }
            });
          }
        }
      });
    },
    // 显示子流程数据
    showSubTaskData: function () {
      var _self = this;
      var subTaskData = _self.subTaskData;
      // 承办显示位置
      // var undertakeSituationPlaceHolder = subTaskData.undertakeSituationPlaceHolder;
      // if ($.isArray(subTaskData.shareDatas)) {
      //   if (StringUtils.isNotBlank(undertakeSituationPlaceHolder)) {
      //     _self._showUndertakeSituation(
      //       undertakeSituationPlaceHolder,
      //       subTaskData.shareDatas,
      //       subTaskData.businessType,
      //       subTaskData.businessRole,
      //       subTaskData
      //     );
      //   }
      // } else if (subTaskData.shareDatas) {
      //   $.each(subTaskData.shareDatas, function (placeHolder, shareDatas) {
      //     _self._showUndertakeSituation(placeHolder, shareDatas, subTaskData.businessType, subTaskData.businessRole, subTaskData);
      //   });
      // }
      //异步取办理信息数据 渲染分发
      _self._dispatch();
      // 信息分发显示位置
      var infoDistributionPlaceHolder = subTaskData.infoDistributionPlaceHolder;
      if ($.isArray(subTaskData.distributeInfos)) {
        if (StringUtils.isNotBlank(infoDistributionPlaceHolder)) {
          _self._showInfoDistribution(infoDistributionPlaceHolder, subTaskData.distributeInfos, subTaskData);
        }
      } else {
        $.each(subTaskData.distributeInfos, function (placeHolder, distributeInfos) {
          _self._showInfoDistribution(placeHolder, distributeInfos);
        });
      }
      // 操作记录显示位置
      var operationRecordPlaceHolder = subTaskData.operationRecordPlaceHolder;
      if ($.isArray(subTaskData.taskOperations)) {
        if (StringUtils.isNotBlank(operationRecordPlaceHolder)) {
          _self._showOperationRecord(operationRecordPlaceHolder, subTaskData.taskOperations, subTaskData);
        }
      } else {
        $.each(subTaskData.taskOperations, function (placeHolder, taskOperations) {
          _self._showOperationRecord(placeHolder, taskOperations, subTaskData);
        });
      }
      _self._wfEvents(subTaskData);
    },
    // 获取共享数据
    getShareDatas: function () {
      var _self = this;
      var branchTaskShareDatas = (_self.branchTaskData && _self.branchTaskData.shareDatas) || [];
      var subTaskShareDatas = (_self.subTaskData && _self.subTaskData.shareDatas) || [];
      if (!$.isArray(branchTaskShareDatas)) {
        var tmp = [];
        for (var key in branchTaskShareDatas) {
          tmp = tmp.concat(branchTaskShareDatas[key]);
        }
        branchTaskShareDatas = tmp;
      }
      if (!$.isArray(subTaskShareDatas)) {
        var tmp = [];
        for (var key in subTaskShareDatas) {
          tmp = tmp.concat(subTaskShareDatas[key]);
        }
        subTaskShareDatas = tmp;
      }
      return branchTaskShareDatas.concat(subTaskShareDatas);
    },
    // 获取信息分发数据
    getDistributeInfos: function () {
      var _self = this;
      var branchDistributeInfos = (_self.branchTaskData && _self.branchTaskData.distributeInfos) || [];
      var subTaskDistributeInfos = (_self.subTaskData && _self.subTaskData.distributeInfos) || [];
      if (!$.isArray(branchDistributeInfos)) {
        var tmp = [];
        for (var key in branchDistributeInfos) {
          tmp = tmp.concat(branchDistributeInfos[key]);
        }
        branchDistributeInfos = tmp;
      }
      if (!$.isArray(subTaskDistributeInfos)) {
        var tmp = [];
        for (var key in subTaskDistributeInfos) {
          tmp = tmp.concat(subTaskDistributeInfos[key]);
        }
        subTaskDistributeInfos = tmp;
      }
      return branchDistributeInfos.concat(subTaskDistributeInfos);
    },
    // 获取操作记录数据
    getTaskOperations: function () {
      var _self = this;
      var branchTaskOperations = (_self.branchTaskData && _self.branchTaskData.taskOperations) || [];
      var subTaskOperations = (_self.subTaskData && _self.subTaskData.taskOperations) || [];
      if (!$.isArray(branchTaskOperations)) {
        var tmp = [];
        for (var key in branchTaskOperations) {
          tmp = tmp.concat(branchTaskOperations[key]);
        }
        branchTaskOperations = tmp;
      }
      if (!$.isArray(subTaskOperations)) {
        var tmp = [];
        for (var key in subTaskOperations) {
          tmp = tmp.concat(subTaskOperations[key]);
        }
        subTaskOperations = tmp;
      }
      return branchTaskOperations.concat(subTaskOperations);
    },
    // 显示分支流承办信息
    _showBranchTaskUndertakeSituation: function (undertakeSituationPlaceHolder, shareDatas, businessType, businessRole, subTaskData) {
      var _self = this;
      var $container = _self.workView.element;
      var $undertakeSituation =
        $("th[blockcode='" + undertakeSituationPlaceHolder + "']", $container).length > 0
          ? $("th[blockcode='" + undertakeSituationPlaceHolder + "']", $container)
          : $("td[blockcode='" + undertakeSituationPlaceHolder + "']", $container);
      $undertakeSituation = $undertakeSituation.closest('table');

      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var text = templateEngine.renderById(shareDataTpl, {
        belongBlockCode: undertakeSituationPlaceHolder,
        shareDatas: shareDatas,
        businessType: businessType,
        businessRole: businessRole,
        dataSource: 1
        // 1、分支流;2、子流程
      });

      var $wfShareData = $(text);
      $undertakeSituation.after($wfShareData);
      $undertakeSituation.hide();

      // 加载办理过程详细信息
      _self._loadBranchTaskProcessDetails(shareDatas, subTaskData);

      // 绑定承办事件
      _self.bindUndertakeSituationEvents($wfShareData);
    },
    _share_data_header_event: function (subTaskData, item, index, $wfShareData) {
      var _self = this;
      var $dataContainer = $('.wf-share-data' + index, $wfShareData);

      $dataContainer.find('.share-data-header').addClass('share-data-header-event');

      $dataContainer.find('.share-data-header-event').on('click', function () {
        appModal.showMask('数据加载中...');
        var collapsedFlag = $(this).is('.collapsed');
        var $dataContainer1 = $(this).parent();
        if (collapsedFlag) {
          $(this).removeClass('collapsed');
          var $pagination = $('.wf-share-data' + index + ' .share-data-pagination', $wfShareData);
          _self._renderPagination($pagination, 1, 10, item.shareItems.length, subTaskData, item, $dataContainer1);
          _self._renderShareDataTable(subTaskData, item, '', index, null, null, $dataContainer1);
          $dataContainer1.find('.panel-collapse').show();
        } else {
          $(this).addClass('collapsed');
          $dataContainer1.find('.panel-collapse').hide();
          appModal.hideMask();
        }
      });
    },
    // 显示子流程承办信息
    _showUndertakeSituation: function (undertakeSituationPlaceHolder, shareDatas, businessType, businessRole, subTaskData) {
      var _self = this;
      var $container = _self.workView.element || _self.workView.$element;

      // 清空内容区域
      if (_self.shareDataEls[undertakeSituationPlaceHolder]) {
        _self.shareDataEls[undertakeSituationPlaceHolder].remove();
      }

      _self.dispatchErrorList = [];

      var $undertakeSituation =
        $("th[blockcode='" + undertakeSituationPlaceHolder + "']", $container).length > 0
          ? $("th[blockcode='" + undertakeSituationPlaceHolder + "']", $container)
          : $("td[blockcode='" + undertakeSituationPlaceHolder + "']", $container);
      $undertakeSituation = $undertakeSituation.closest('table');
      // 协办流程，不可操作
      $.each(shareDatas, function () {
        if (this.isMajor != true && this.isSupervise != true) {
          this.isOver = true;
        }
      });
      // 异步取办理信息数据 渲染分发
      _self._dispatch();

      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var text = templateEngine.renderById(shareDataTpl, {
        belongBlockCode: undertakeSituationPlaceHolder,
        shareDatas: shareDatas,
        businessType: businessType,
        businessRole: businessRole,
        dataSource: 2
        // 1、分支流;2、子流程
      });
      var $wfShareData = $(text);
      $undertakeSituation.after($wfShareData);
      $undertakeSituation.hide();
      _self.shareDataEls[undertakeSituationPlaceHolder] = $wfShareData;

      //      // 分发进度保持不变
      //      if (_self.loopCount != 0) {
      //        $('.dispense-process', $container).html(_self.oldHtml);
      //      } else {
      //        _self.oldHtml = $('.dispense-process', $container).html();
      //      }
      //更改表格数据样式
      // _self._alertShareDataTableStyle(shareDatas);

      // 展示附件信息
      //_self._showWorkFlowAttachEvent(shareDatas);

      // 加载办理过程详细信息
      // _self._loadWorkProcessDetails(shareDatas);

      // 绑定承办事件
      _self.bindUndertakeSituationEvents($wfShareData);

      // 更改表格数据格式
      // _self._rebuildTableStyle(shareDatas, $wfShareData);

      // 刷新表单滚动条
      // _self.refreshFormScroller();
      //承办情况列表展开设置 subTaskData.expandList
      var latestShareDatas = [];
      $.each(shareDatas, function (index, item) {
        var belongToFlowInstUuidExpandListFlag = {
          belongToFlowInstUuid: item.belongToFlowInstUuid,
          latestExpandListFlag: true
        };
        //最新办理的承办情况是否已经展开
        var existLatestExpandListFlag = false;
        $.each(latestShareDatas, function (indexLatest, itemLatest) {
          if (item.belongToFlowInstUuid == itemLatest.belongToFlowInstUuid) {
            existLatestExpandListFlag = true;
            return;
          }
        });

        var expandListFlag = item.expandListFlag;
        var $dataContainer = $('.wf-share-data' + index, $wfShareData);
        //默认展开
        if (expandListFlag == null || expandListFlag == 1) {
          if (!existLatestExpandListFlag) {
            $dataContainer.find('.share-data-header').removeClass('collapsed');
            $dataContainer.find('.panel-collapse').addClass('in');
            $dataContainer.find('.panel-collapse').attr('aria-expanded', 'true');
            var $pagination = $('.wf-share-data' + index + ' .share-data-pagination', $wfShareData);
            _self._renderPagination($pagination, 1, 10, item.shareItems.length, subTaskData, item, $dataContainer);
            _self._renderShareDataTable(subTaskData, item, '', index, null, null, $dataContainer);
            latestShareDatas.push(belongToFlowInstUuidExpandListFlag);
          } else {
            $dataContainer.find('.panel-collapse').removeClass('in');
            $dataContainer.find('.panel-collapse').attr('aria-expanded', 'false');
            $dataContainer.find('.panel-collapse').hide();
          }
        } else {
          $dataContainer.find('.panel-collapse').removeClass('in');
          $dataContainer.find('.panel-collapse').attr('aria-expanded', 'false');
          $dataContainer.find('.panel-collapse').hide();
        }
        _self._share_data_header_event(subTaskData, item, index, $wfShareData);
      });
    },
    //异步取办理信息数据 渲染分发
    _dispatch: function () {
      var _self = this;
      JDS.restfulPost({
        url: ctx + '/proxy/api/workflow/work/getUndertakeSituationDatasAll',
        data: _self.subTaskData,
        success: function (result) {
          var shareDatas = result.data;
          var time = new Date().getTime();
          var dispatching = 0;
          var dispatchFail = 0;
          $.each(shareDatas, function (index, shareData) {
            if (shareData.distributeTime) {
              shareData.uniqueTime = ++time;
            }
            if (index == 0) {
              for (var i = 0; i < shareData.shareItems.length; i++) {
                if (shareData.shareItems[i].dispatchState == 0) {
                  dispatching++;
                }
                if (shareData.shareItems[i].dispatchState == 2) {
                  dispatchFail++;
                  var todoNameIndex = shareData.shareItems[i].columnValues.findIndex(function (currentValue, currentIndex, arr) {
                    return currentValue.index == 'todoName';
                  });
                  _self.dispatchErrorList.push({
                    id: shareData.shareItems[i].taskInstUuid,
                    todoName: todoNameIndex == -1 ? '' : shareData.shareItems[i].columnValues[todoNameIndex].value,
                    dispatchResultMsg: shareData.shareItems[i].dispatchResultMsg
                  });
                }
              }
              shareData.dispatched = shareData.shareItems.length - dispatching - _self.dispatchedCount;
              shareData.dispatchTotal = shareData.shareItems.length - _self.dispatchedCount;
            }
          });

          // 分发失败时查看切换到承办信息页签
          if (_self.dispatchErrorList && _self.dispatchErrorList.length > 0) {
            var dispatchError = Browser.getQueryString('dispatchError');
            if (dispatchError == 'true') {
              try {
                var dyform = _self.workView.getDyform();
                var tabId = $undertakeSituation.closest('.tab-pane').attr('name');
                $("a[href='#" + tabId + "']", $container).trigger('click');
                dyform.showTab(tabId);
                dyform.showBlock(undertakeSituationPlaceHolder);
              } catch (e) {
                console.error(e);
              }
            }
          }

          $.each(shareDatas, function (index, shareData) {
            if (index == 0) {
              // 如果分发进程结束，就隐藏进度
              if (dispatching == 0) {
                $('.dispense-process').hide();
              } else {
                $('.dispense-process').find('.dispatched').html(shareData.dispatched);
                $('.dispense-process').find('.dispatchTotal').html(shareData.dispatchTotal);
                $('.dispense-process').show();
                //承办情况列表渲染
                var $dataContainer = $('.wf-share-data0');
                var val = $dataContainer.find('input').val();
                _self._renderShareDataTable(_self.subTaskData, shareData, val, 0, 1, 10, $dataContainer);
              }
              // 如果分发进程结束，有分发异常的，显示一键补发
              if (shareData.dispatched + _self.dispatchedCount == shareData.shareItems.length && dispatchFail != 0) {
                $('.share-data-error').show();
                $('.share-data-error .color-red').html(dispatchFail);
                //           // 补发异常提示
                //            if(_self.dispatchedCount > 0) {
                //	          appModal.error("补发异常！");
                //            }
              } else {
                // 补发成功提示
                if (dispatching == 0 && _self.dispatchedCount > 0) {
                  appModal.success('补发成功！');
                }
              }
            }
          });
          // 展示附件信息
          _self._showWorkFlowAttachEvent(shareDatas);
        }
      });
    },
    _wfEvents: function (subTaskData) {
      var _self = this;
      $('input', '.wf-data-container')
        .off()
        .on('keyup', function (e) {
          e.stopPropagation();
          if ($(this).val() != '') {
            $(this).next().show();
          } else {
            $(this).next().hide();
          }
          if (e.keyCode == 13) {
            $(this).parent().next().find('button').trigger('click');
          }
        });

      $('.icon-ptkj-dacha', '.wf-data-container')
        .off()
        .on('click', function (e) {
          $(this).parent().prev().val('');
          $(this).parent().hide();
          $(this).parents('.keyword-search-wrap').next().find('button').trigger('click');
        });

      $('.btn-query', '.wf-data-container')
        .off()
        .on('click', function (e) {
          var $dataContainer = $(this).parents('.wf-data-container');
          var val = $dataContainer.find('input').val();
          var tableIndex = $dataContainer.first().data('index');
          var type = $dataContainer.data('type');
          if (type == 'share') {
            var blockCode = $(this).closest('.panel-group').attr('belong-block-code');
            var shareData = subTaskData.shareDatas[blockCode][tableIndex];
            _self._renderShareDataTable(subTaskData, shareData, val, tableIndex, 1, 10, $dataContainer);
          } else if (type == 'operation') {
            var blockCode = $(this).closest('.wf-task-operation').attr('belong-block-code');
            var operationData = subTaskData.taskOperations[blockCode][tableIndex];
            _self._renderOperationTable(subTaskData, operationData, val, tableIndex, 1, 10, $dataContainer);
          } else if (type == 'distribute') {
            var blockCode = $(this).closest('.wf-distribute-info').attr('belong-block-code');
            var distributeInfoData = subTaskData.distributeInfos[blockCode][tableIndex];
            _self._renderDistributeTable(subTaskData, distributeInfoData, val, tableIndex, 1, 10, $dataContainer);
          }
        });
    },
    _renderShareDataTable: function (subTaskData, shareData, val, tableIndex, page, size, $dataContainer) {
      var _self = this;
      var dataSource = $('#dataSource').val();
      var subFlowInstUuids = [];
      if (subTaskData.subFlowInstUuids) {
        subFlowInstUuids = subTaskData.subFlowInstUuids;
      }
      var pageNum = page || 1;
      var pageSize = size || 10;
      JDS.restfulPost({
        url: ctx + '/api/workflow/work/loadShareDatasByPage',
        type: 'post',
        data: {
          keyword: val,
          pageNum: pageNum,
          pageSize: pageSize,
          taskInstUuid: subTaskData.taskInstUuid,
          flowInstUuid: subTaskData.flowInstUuid,
          parentFlowInstUuid: shareData.belongToFlowInstUuid,
          subFlowInstUuids: subFlowInstUuids,
          belongToTaskInstUuid: shareData.belongToTaskInstUuid
        },
        success: function (res) {
          appModal.hideMask();
          if (res.code == 0) {
            var html = '';
            var wfShareData = res.data[0];
            if (wfShareData) {
              $.each(wfShareData.shareItems, function (index, item) {
                html += '<tr>';
                if (tableIndex == 0 && (wfShareData.major == true || wfShareData.supervise == true)) {
                  html +=
                    '<td class="td-checkbox" style="vertical-align: middle">' +
                    '<input type="checkbox" name="checkOne" taskInstUuid="' +
                    item.taskInstUuid +
                    '" flowInstUuid="' +
                    item.flowInstUuid +
                    '"/></td>';
                }
                html += '<td class="index">' + (parseInt(index) + 1) + '</td>';
                $.each(item.columnValues, function (dIndex, dItem) {
                  if (dItem.index == 'todoName') {
                    if (dataSource == '1') {
                      html += '<td class="todoName">' + dItem.value + '</td>';
                    } else {
                      html +=
                        '<td class="todoName"><a class="share-work-item" href="/workflow/work/v53/view/subflow/share?taskInstUuid=' +
                        item.taskInstUuid +
                        '&flowInstUuid=' +
                        item.flowInstUuid +
                        '&belongToFlowInstUuid=' +
                        wfShareData.belongToFlowInstUuid +
                        '" ' +
                        'target="_blank">' +
                        dItem.value +
                        '</a></td>';
                    }
                  } else if (dItem.index == 'workProcesses') {
                    html +=
                      '<td><div class="work-process-details" taskInstUuid="' +
                      item.taskInstUuid +
                      '" flowInstUuid="' +
                      item.flowInstUuid +
                      '" style="position: relative;"></div></td>';
                  } else if (dItem.type == 'attach') {
                    html = _self._attachHtml(html, tableIndex, index, dIndex, dItem);
                  } else if (dItem.index == 'dueTime' && item.timingState == 3) {
                    html += '<td class="dueTime" style="color:red;">' + dItem.value + '</td>';
                  } else if (dItem.index == 'currentTodoUserName') {
                    html += '<td class="currentTodoUserName" title="' + dItem.value + '">' + (dItem.value || '—') + '</td>';
                  } else {
                    if (dItem.value instanceof Array) {
                      html = _self._attachHtml(html, tableIndex, index, dIndex, dItem);
                    } else {
                      html += '<td>' + dItem.value + '</td>';
                    }
                  }
                });

                html += '</tr>';
              });
              $('.table', $dataContainer).find('tbody').html(html);
              _self._loadWorkProcessDetails(res.data, $dataContainer);
              _self._alertShareDataTableStyle();
            }
            _self._renderPagination($('.share-data-pagination', $dataContainer), pageNum, pageSize, res.data[0].totalCount, subTaskData);
          } else {
          }
        }
      });
    },
    //承办情况附件的html渲染
    _attachHtml: function (html, tableIndex, index, dIndex, dItem) {
      html +=
        '<td>' +
        '<div class="work-flow-attach" shareDataIndex="' +
        tableIndex +
        '" shareItemIndex="' +
        index +
        '"' +
        'columnValueIndex="' +
        dIndex +
        '">';
      var fileCount = 0;
      $.each(dItem.value, function (vIndex, vItem) {
        // html += '<div>' + '<a fileId="' + vItem.fileID + '" class="share-file-item">' + vItem.fileName + '</a>' + '</div>';
        html +=
          '<div fileid="' +
          vItem.fileID +
          '">' +
          vItem.fileName +
          '<br>' +
          '<a class="share-file-item" common-id="a-view"><i class="iconfont icon-ptkj-yulan"></i>预览</a>' +
          '<a class="share-file-item" common-id="a-download" style="margin-left:20px;"><i class="iconfont icon-ptkj-xiazai"></i>下载</a>' +
          '</div><br/>';
        fileCount++;
      });
      html += '</div>';
      if (fileCount > 0) {
        // downloadallbtn
        html +=
          '<div>共<span class="totalFileCount">' +
          fileCount +
          '</span>个附件&nbsp;&nbsp;&nbsp;' +
          '       <a class="share-file-item" common-id="a-downloadAll"><i class="iconfont icon-ptkj-xiazai"></i>全部下载</a></div>';
      }
      html += '</td>';
      return html;
    },
    _renderOperationTable: function (subTaskData, operationData, val, tableIndex, page, pageSize, $dataContainer) {
      var _self = this;
      JDS.restfulPost({
        url: ctx + '/api/workflow/work/loadRelateOperationByPage',
        type: 'post',
        data: {
          keyword: val,
          pageNum: page,
          pageSize: pageSize,
          flowInstUuid: operationData.belongToFlowInstUuid || subTaskData.flowInstUuid
        },
        success: function (res) {
          if (res.code == 0) {
            var html = '';
            var operations = res.data.result[0];
            if (operations) {
              $.each(operations.operations, function (index, item) {
                html += '<tr>';
                html += '<td>' + (index + 1) + '</td>';
                html += '<td>' + item.assigneeName + '</td>';
                html += '<td>' + item.action + '</td>';
                html += '<td>' + item.userName + '</td>';
                html += '<td>' + item.createTime + '</td>';
                html += '<td>' + item.opinionText + '</td>';

                html += '</td>';
              });
              $('.table', $dataContainer).find('tbody').html(html);
              _self._renderPagination(
                $('.operation-data-pagination', $dataContainer),
                page,
                pageSize,
                operations.operations.length,
                subTaskData
              );
            } else {
              $('.table', $dataContainer).find('tbody').html(html);
              _self._renderPagination($('.operation-data-pagination', $dataContainer), page, pageSize, 0, subTaskData);
            }
          }
        }
      });
    },
    _renderDistributeTable: function (subTaskData, distributeInfoData, val, tableIndex, page, pageSize, $dataContainer) {
      var _self = this;
      JDS.restfulPost({
        url: ctx + '/api/workflow/work/loadDistributeInfosByPage',
        type: 'post',
        data: {
          keyword: val,
          pageNum: page,
          pageSize: pageSize,
          parentFlowInstUuid: distributeInfoData.belongToFlowInstUuid || subTaskData.parentFlowInstUuid
        },
        success: function (res) {
          if (res.code == 0) {
            var html = '';
            var distributeInfos = res.data.result[0];
            if (distributeInfos) {
              $.each(distributeInfos.distributeInfos, function (index, item) {
                html += '<tr>';
                html += '<td>' + (index + 1) + '</td>';
                html += '<td>' + item.distributorName + '</td>';
                html += '<td>' + item.createTime + '</td>';
                html += '<td>' + item.content + '</td>';
                html +=
                  '<td><div class="distribute-info-attach" distributeDataIndex="' + tableIndex + '" distributeInfoIndex="' + index + '">';
                $.each(item.logicFileInfos, function (fIndex, fItem) {
                  html += '<div class="row">';
                  html +=
                    '<p class="filename" fileId="' +
                    fItem.fileID +
                    '" style="display:block;float:left;" title="123.def">' +
                    fItem.fileName +
                    '</p>';
                  html += '</div>';
                });
                ('</td>');
                html += '</td>';
              });
              $('.table', $dataContainer).find('tbody').html(html);
              _self._renderPagination(
                $('.distribute-data-pagination', $dataContainer),
                page,
                pageSize,
                distributeInfos.distributeInfos.length,
                subTaskData
              );
              _self._showSingleInfoDistributionAttach(distributeInfoData, $dataContainer);
            } else {
              $('.table', $dataContainer).find('tbody').html('');
              _self._renderPagination($('.distribute-data-pagination', $dataContainer), page, pageSize, 0, subTaskData);
            }
          }
        }
      });
    },
    _renderPagination: function ($el, page, pageSize, total, subTaskData) {
      var self = this;
      $el.wellPagination({
        showSkip: true,
        page: page,
        pageSize: pageSize,
        total: total,
        showTotal: true,
        showPageSizeList: true,
        backFun: function (page, pageSize) {
          var $dataContainer = $el.parents('.wf-data-container');
          var val = $dataContainer.find('input').val();
          var tableIndex = $dataContainer.data('index');
          var type = $dataContainer.data('type');
          if (type == 'share') {
            var blockCode = $dataContainer.closest('.panel-group').attr('belong-block-code');
            var shareData = subTaskData.shareDatas[blockCode][tableIndex];
            self._renderShareDataTable(subTaskData, shareData, val, tableIndex, page, pageSize, $dataContainer);
          } else if (type == 'operation') {
            var blockCode = $dataContainer.closest('.wf-task-operation').attr('belong-block-code');
            var operationData = subTaskData.taskOperations[blockCode][tableIndex];
            self._renderOperationTable(subTaskData, operationData, val, tableIndex, page, pageSize, $dataContainer);
          } else if (type == 'distribute') {
            var blockCode = $dataContainer.closest('.wf-distribute-info').attr('belong-block-code');
            var distributeInfoData = subTaskData.distributeInfos[blockCode][tableIndex];
            self._renderDistributeTable(subTaskData, distributeInfoData, val, tableIndex, page, pageSize, $dataContainer);
          }
        }
      });
      // 刷新表单滚动条
      self.refreshFormScroller();
    },
    // 显示分支流信息分发
    _showBranchTaskInfoDistribution: function (infoDistributionPlaceHolder, distributeInfos, subTaskData) {
      this._showInfoDistribution(infoDistributionPlaceHolder, distributeInfos, subTaskData);
    },
    // 显示子流程信息分发
    _showInfoDistribution: function (infoDistributionPlaceHolder, distributeInfos, subTaskData) {
      var _self = this;
      var $container = _self.workView.element;
      // 清空内容区域
      if (_self.distributeInfoEls[infoDistributionPlaceHolder]) {
        _self.distributeInfoEls[infoDistributionPlaceHolder].remove();
      }
      var $infoDistribution =
        $("th[blockcode='" + infoDistributionPlaceHolder + "']", $container).length > 0
          ? $("th[blockcode='" + infoDistributionPlaceHolder + "']", $container)
          : $("td[blockcode='" + infoDistributionPlaceHolder + "']", $container);
      $infoDistribution = $infoDistribution.closest('table');
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var text = templateEngine.renderById(distributeInfoTpl, {
        distributeInfos: distributeInfos,
        belongBlockCode: infoDistributionPlaceHolder
      });
      var $distributeText = $(text);
      $infoDistribution.after($distributeText);
      $infoDistribution.hide();
      _self.distributeInfoEls[infoDistributionPlaceHolder] = $distributeText;
      $('.wf-distribute-info', $container).on('click', '.filename', function () {
        var fileId = $(this).attr('fileId');
        FileDownloadUtils.downloadMongoFile({
          fileId: fileId
        });
      });

      _self._showInfoDistributionAttachEvent(distributeInfos, infoDistributionPlaceHolder);

      $.each(distributeInfos, function (index, item) {
        var $pagination = $('.wf-distribute-info' + index + ' .distribute-data-pagination', $distributeText);
        _self._renderPagination($pagination, 1, 10, item.distributeInfos.length, subTaskData);
      });
    },
    // 显示分支流操作记录
    _showBranchTaskOperationRecord: function (operationRecordPlaceHolder, taskOperations, subTaskData) {
      this._showOperationRecord(operationRecordPlaceHolder, taskOperations, subTaskData);
    },
    // 显示子流程操作记录
    _showOperationRecord: function (operationRecordPlaceHolder, taskOperations, subTaskData) {
      var _self = this;
      var $container = _self.workView.element;
      // 清空内容区域
      if (_self.operationRecordEls[operationRecordPlaceHolder]) {
        _self.operationRecordEls[operationRecordPlaceHolder].remove();
      }
      var $operationRecord =
        $("th[blockcode='" + operationRecordPlaceHolder + "']", $container).length > 0
          ? $("th[blockcode='" + operationRecordPlaceHolder + "']", $container)
          : $("td[blockcode='" + operationRecordPlaceHolder + "']", $container);
      $operationRecord = $operationRecord.closest('table');
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var text = templateEngine.renderById(taskOperationTpl, {
        taskOperations: taskOperations,
        belongBlockCode: operationRecordPlaceHolder
      });
      var $recordText = $(text);
      $operationRecord.after($recordText);
      $operationRecord.hide();
      _self.operationRecordEls[operationRecordPlaceHolder] = $recordText;

      $.each(taskOperations, function (index, item) {
        var $pagination = $('.wf-task-operation' + index + ' .operation-data-pagination', $recordText);
        _self._renderPagination($pagination, 1, 10, item.operations.length, subTaskData);
      });
    },
    // 加载分支办理过程详细信息
    _loadBranchTaskProcessDetails: function (shareDatas, subTaskData) {
      var _self = this;
      var $processDetails = $('.work-process-details', _self.element);
      if ($processDetails.length == 0) {
        return;
      }
      var taskInstUuids = [];
      $.each(shareDatas, function (i, shareData) {
        $.each(shareData.shareItems, function (j, shareItem) {
          taskInstUuids.push(shareItem.taskInstUuid);
        });
      });
      JDS.restfulPost({
        url: ctx + '/api/workflow/work/getBranchTaskProcesses',
        data: {
          taskInstUuids: taskInstUuids
        },
        version: '',
        success: function (result) {
          var data = result.data;
          _self._showBranchTaskProcessDetails(data);
        }
      });
    },
    // 显示分支办理过程详细信息
    _showBranchTaskProcessDetails: function (data) {
      var _self = this;
      var $processDetails = $('.work-process-details', _self.element);
      $.each($processDetails, function () {
        var taskInstUuid = $(this).attr('taskInstUuid');
        var processes = data[taskInstUuid];
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

    // 更改表格数据样式
    _alertShareDataTableStyle: function () {
      var _self = this;

      //todoName
      var $todoNames = $('.todoName', _self.element);
      if ($todoNames.length === 0) {
        return;
      }
      $.each($todoNames, function () {
        var $todoName = $(this);
        // 已处理不再处理
        if ($todoName.data('decorated')) {
          return;
        }
        $todoName.data('decorated', true);
        var $todoNameValue = null;
        if ($todoName.find('a').length > 0) {
          $todoNameValue = $todoName.find('a');
        } else {
          $todoNameValue = $todoName;
        }
        // console.log(0);
        // console.log($todoNameValue.parent().html());
        var todoName;
        var todoNameValue = $todoNameValue.html().trim();
        var todoNameValueSplit = todoNameValue.split(' ');
        if (todoNameValueSplit.length > 1) {
          // console.log(todoNameValue);
          todoName = todoNameValue.substring(todoNameValueSplit[0].length + 1);
          var $span = $('<span class="todoNameClass" >' + todoNameValueSplit[0] + '</span>');
          $span.prependTo($todoName);
          $todoNameValue.html(todoName);
          // console.log(1);
          // console.log($todoNameValue.parent().html());
          // console.log(2);
        }
      });
    },
    // 更改表格数据格式
    _rebuildTableStyle: function (shareDatas, $container) {
      var $table = $($container.find('.table')[0]);
      var $trs = $table.find('tbody tr');

      $trs.each(function (trIndex, trItem) {
        var $tds = $(trItem).find('td');
        $tds.each(function () {
          if ($(this).hasClass('td-checkbox')) {
            $(this).find('input').removeAttr('disabled');
          }
        });
        if (shareDatas[0].shareItems[trIndex] && shareDatas[0].shareItems[trIndex].dispatchState != 1) {
          var todoNameIndex = -1;
          var perColspans = 1;
          var nextColspans = 1;
          var perTdIndexs = [];
          var nextTdIndex = [];
          $tds.each(function () {
            if ($(this).hasClass('td-checkbox')) {
              $(this).find('input').attr('disabled', true);
            }
            if ($(this).hasClass('todoName')) {
              todoNameIndex = $(this).index();
              return false;
            }
          });
          if (todoNameIndex !== -1) {
            for (var i = 0; i < todoNameIndex; i++) {
              if ($tds.eq(i).hasClass('td-checkbox') || $tds.eq(i).hasClass('index')) {
                continue;
              } else {
                perColspans++;
                perTdIndexs.push(i);
              }
            }

            for (var j = todoNameIndex + 1; j < $tds.length; j++) {
              nextColspans++;
              nextTdIndex.push(j);
            }
            var aValue = $tds.eq(todoNameIndex).find('a') ? $tds.eq(todoNameIndex).find('a').html() : $tds.eq(todoNameIndex).html();
            var spanValue = $tds.eq(todoNameIndex).find('span');
            $tds.eq(todoNameIndex).html('').append(spanValue).append(aValue);
          } else {
            for (var i = 0; i < $tds.length; i++) {
              if ($tds.eq(i).hasClass('td-checkbox') || $tds.eq(i).hasClass('index')) {
                continue;
              } else {
                perColspans++;
                perTdIndexs.push(i);
              }
            }
          }

          console.log(perTdIndexs, nextTdIndex);

          if (perTdIndexs.length > 0) {
            for (var m = 0; m < perTdIndexs.length; m++) {
              $tds.eq(perTdIndexs[0]).attr('colspan', perColspans).html('');
              if (m != 0) {
                $tds.eq(perTdIndexs[m]).remove();
              }
            }
          }

          if (nextTdIndex.length > 0) {
            for (var n = 0; n < nextTdIndex.length; n++) {
              $tds.eq(nextTdIndex[0]).attr('colspan', nextColspans).css('text-align', 'right');
              if (shareDatas[0].shareItems[trIndex].dispatchState == 0) {
                $tds
                  .eq(nextTdIndex[0])
                  .html('<span class="dispatch-status"><i class="iconfont icon-ptkj-tijiaofabufasong"></i> 分发中</span>');
              }
              if (shareDatas[0].shareItems[trIndex].dispatchState == 2) {
                $tds
                  .eq(nextTdIndex[0])
                  .html('<span class="dispatch-status fail"><i class="iconfont icon-ptkj-tijiaofabufasong"></i> 分发错误</span>');
              }
              if (n != 0) {
                $tds.eq(nextTdIndex[n]).remove();
              }
            }
          } else {
            // 如果 承办部门在最后一列，折把分发状态显示在前面的合并列
            $tds.eq(perTdIndexs[0]).css('text-align', 'left');
            if (shareDatas[0].shareItems[trIndex].dispatchState == 0) {
              $tds
                .eq(perTdIndexs[0])
                .html('<span class="dispatch-status"><i class="iconfont icon-ptkj-tijiaofabufasong"></i> 分发中</span>');
            }
            if (shareDatas[0].shareItems[trIndex].dispatchState == 2) {
              $tds
                .eq(perTdIndexs[0])
                .html('<span class="dispatch-status fail"><i class="iconfont icon-ptkj-tijiaofabufasong"></i> 分发错误</span>');
            }
          }
        }
      });
    },
    // 刷新表单滚动条
    refreshFormScroller: function () {
      try {
        if ($('.widget-main').length > 0) {
          setTimeout(function () {
            // console.log("refreshFormScroller");
            $('.widget-main').getNiceScroll().resize();
          }, 20);
        }
      } catch (e) {}
    },
    // 显示办理过程详细信息
    _showWorkFlowAttachEvent: function (shareDatas) {
      var _self = this;
      var showedFlowAttach = false;

      $.each($('.dyform a[data-toggle="tab"]', _self.workView.element), function (index, tabA) {
        // var tabAParentClass = $(tabA).parent().attr('class');
        if (
          $(tabA).parent().hasClass('active') && //当前页签
          $($(tabA).attr('href')).find('.wf-share-data .work-flow-attach').length > 0 //承办信息页签
        ) {
          _self._showWorkFlowAttach(shareDatas);
          showedFlowAttach = true;
        }
      });

      $('.dyform a[data-toggle="tab"]', _self.workView.element).on('shown.bs.tab', function (e) {
        if (!showedFlowAttach) {
          var $workFlowAttaches = $($(e.target).attr('href')).find('.wf-share-data .work-flow-attach');
          if ($workFlowAttaches.length > 0) {
            _self._showWorkFlowAttach(shareDatas);
            showedFlowAttach = true;
          }
        }
      });
    },
    // 展示附件信息
    _showWorkFlowAttach: function (shareDatas) {
      var _self = this;
      var $workFlowAttachs = $('.work-flow-attach', _self.element);
      if ($workFlowAttachs.length === 0) {
        return;
      }
      $.each($workFlowAttachs, function () {
        var $attachItem = $(this);
        $attachItem.html('');
        var shareDataIndex = $attachItem.attr('shareDataIndex');
        var shareItemIndex = $attachItem.attr('shareItemIndex');
        var columnValueIndex = $attachItem.attr('columnValueIndex');
        var shareItem = shareDatas[parseInt(shareDataIndex)].shareItems[parseInt(shareItemIndex)];
        if (shareItem && shareItem.columnValues) {
          var columnValue = shareItem.columnValues[parseInt(columnValueIndex)].value;
          if (columnValue.length > 0) {
            var html = '';
            $.each(columnValue, function (vIndex, vItem) {
              vItem.isNew = false;
              html +=
                '<div fileid="' +
                vItem.fileID +
                '">' +
                vItem.fileName +
                '<br>' +
                '<a class="share-file-item" common-id="a-view"><i class="iconfont icon-ptkj-yulan"></i>预览</a>' +
                '<a class="share-file-item" common-id="a-download" style="margin-left:20px;"><i class="iconfont icon-ptkj-xiazai"></i>下载</a>' +
                '</div><br/>';
            });
            $attachItem.html(html);
            /*var wellFileUpload = new WellFileUpload('columnValue_' + shareDataIndex + '_' + shareItemIndex + '_' + columnValueIndex, {});
            wellFileUpload.initAllowUploadDeleteDownload(false, false, true);
            wellFileUpload.initFileUploadExtraParam(true, false);
            wellFileUpload.init(true, $attachItem, false, true, columnValue);*/
          }
        }
      });
    },
    // 显示信息分发附件
    _showInfoDistributionAttachEvent: function (distributeInfos, infoDistributionPlaceHolder) {
      var _self = this;
      var showedDistributeInfoAttach = false;

      $.each($('.dyform a[data-toggle="tab"]', _self.workView.element), function (index, tabA) {
        // var tabAParentClass = $(tabA).parent().attr('class');
        if (
          $(tabA).parent().hasClass('active') && //当前页签
          $($(tabA).attr('href')).find('.wf-distribute-info .distribute-info-attach').length > 0 //信息分发页签
        ) {
          var $tabContainer = $($(tabA).attr('href'));
          _self._showInfoDistributionAttach(distributeInfos, $tabContainer);
          showedDistributeInfoAttach = true;
        }
      });

      $('.dyform a[data-toggle="tab"]', _self.workView.element).on('shown.bs.tab', function (e) {
        if (!showedDistributeInfoAttach) {
          var distributeinfoAttaches = $($(e.target).attr('href')).find('.wf-distribute-info .distribute-info-attach');
          if (distributeinfoAttaches.length > 0) {
            var $tabContainer = $($(e.target).attr('href'));
            if ($tabContainer.find("div[belong-block-code='" + infoDistributionPlaceHolder + "']").length > 0) {
              _self._showInfoDistributionAttach(distributeInfos, $tabContainer);
              showedDistributeInfoAttach = true;
            }
          }
        }
      });
    },
    // 展示附件信息
    _showInfoDistributionAttach: function (distributeInfos, $tabContainer) {
      var _self = this;
      var $workFlowAttachs = $('.distribute-info-attach', $tabContainer || _self.element);
      if ($workFlowAttachs.length === 0 || distributeInfos.length == 0) {
        return;
      }

      $.each($workFlowAttachs, function () {
        var $attachItem = $(this);
        $attachItem.html('');
        var distributeDataIndex = $attachItem.attr('distributeDataIndex');
        var distributeInfoIndex = $attachItem.attr('distributeInfoIndex');
        var columnValueIndex = $attachItem.attr('columnValueIndex');
        var columnValue = distributeInfos[parseInt(distributeDataIndex)].distributeInfos[parseInt(distributeInfoIndex)].logicFileInfos;
        if (columnValue.length > 0) {
          $.each(columnValue, function (index, columnValueItem) {
            columnValueItem.isNew = false;
          });
          var wellFileUpload = new WellFileUpload(
            'columnValue_' + distributeDataIndex + '_' + distributeInfoIndex + '_' + columnValueIndex,
            {}
          );
          wellFileUpload.initAllowUploadDeleteDownload(false, false, true);
          wellFileUpload.initFileUploadExtraParam(true, false);
          wellFileUpload.init(true, $attachItem, false, true, columnValue);
        }
      });
    },
    // 展示单块附件信息
    _showSingleInfoDistributionAttach: function (distributeInfoData, $dataContainer) {
      var _self = this;
      var $workFlowAttachs = $('.distribute-info-attach', $dataContainer);
      if ($workFlowAttachs.length === 0 || distributeInfoData == null) {
        return;
      }
      $.each($workFlowAttachs, function () {
        var $attachItem = $(this);
        $attachItem.html('');
        var distributeDataIndex = $attachItem.attr('distributeDataIndex');
        var distributeInfoIndex = $attachItem.attr('distributeInfoIndex');
        var columnValueIndex = $attachItem.attr('columnValueIndex');
        var columnValue = distributeInfoData.distributeInfos[parseInt(distributeInfoIndex)].logicFileInfos;
        if (columnValue.length > 0) {
          $.each(columnValue, function (index, columnValueItem) {
            columnValueItem.isNew = false;
          });
          var wellFileUpload = new WellFileUpload(
            'columnValue_' + distributeDataIndex + '_' + distributeInfoIndex + '_' + columnValueIndex,
            {}
          );
          wellFileUpload.initAllowUploadDeleteDownload(false, false, true);
          wellFileUpload.initFileUploadExtraParam(true, false);
          wellFileUpload.init(true, $attachItem, false, true, columnValue);
        }
      });
    },

    // 加载办理过程详细信息
    _loadWorkProcessDetails: function (shareDatas, $dataContainer) {
      var _self = this;
      var workProcesses = _self.workProcesses || {};
      var $processDetails = $('.work-process-details', $dataContainer || _self.element);
      if ($processDetails.length == 0) {
        return;
      }
      var flowInstUuids = [];
      $.each(shareDatas, function (i, shareData) {
        $.each(shareData.shareItems, function (j, shareItem) {
          if (workProcesses[shareItem.flowInstUuid] == null && shareItem.dispatchState != 0) {
            flowInstUuids.push(shareItem.flowInstUuid);
            _self.shareDatas.push(shareItem.flowInstUuid);
          }
        });
      });
      if (flowInstUuids.length == 0) {
        _self._showWorkProcessDetailsEvent(workProcesses, $dataContainer);
        //更改表格数据样式
        _self._alertShareDataTableStyle();
        var blockCode = $dataContainer.closest('.panel-group').attr('belong-block-code');
        _self._rebuildTableStyle(shareDatas, _self.shareDataEls[blockCode]);
        return;
      }
      $.get({
        url: ctx + '/api/workflow/work/getWorkProcesses',
        data: {
          flowInstUuids: flowInstUuids.toString()
        },
        success: function (result) {
          var data = result.data;
          $.each(data, function (key, value) {
            if (value.length > 0) {
              workProcesses[key] = value;
            }
          });
          _self.workProcesses = workProcesses;
          _self._showWorkProcessDetailsEvent(workProcesses, $dataContainer);
          //更改表格数据样式
          _self._alertShareDataTableStyle();
          var blockCode = $dataContainer.closest('.panel-group').attr('belong-block-code');
          _self._rebuildTableStyle(shareDatas, _self.shareDataEls[blockCode]);
        }
      });
    },
    // 显示办理过程详细信息
    _showWorkProcessDetailsEvent: function (data, $dataContainer) {
      var _self = this;

      $.each($('.dyform a[data-toggle="tab"]', _self.workView.element), function (index, tabA) {
        // var tabAParentClass = $(tabA).parent().attr('class');
        //当前页签
        if ($(tabA).parent().hasClass('active')) {
          var $processDetails = null;
          if ($dataContainer) {
            $processDetails = $($dataContainer).find('.work-process-details');
          } else {
            $processDetails = $($(tabA).attr('href')).find('.wf-share-data .work-process-details');
          }
          if ($processDetails.length > 0) {
            _self._showWorkProcessDetails($processDetails, data);
            _self.showedWorkProcessDetails = true;
          } else {
            $processDetails = $($(tabA).attr('href')).parent().find('.wf-share-data .work-process-details');
            if ($processDetails.length > 0) {
              _self._showWorkProcessDetails($processDetails, data);
              showedWorkProcessDetails = true;
            }
          }
        }
      });

      $('.dyform a[data-toggle="tab"]', _self.workView.element).on('shown.bs.tab', function (e) {
        if (!_self.showedWorkProcessDetails) {
          var $processDetails = $($(e.target).attr('href')).find('.wf-share-data .work-process-details');
          if ($processDetails.length > 0) {
            if (_self.shareDatas.length > 0) {
              // $.get({
              //   url: ctx + '/api/workflow/work/getWorkProcesses',
              //   data: {
              //     flowInstUuids: _self.shareDatas.toString()
              //   },
              //   success: function (result) {
              //     var data = result.data;
              //     _self._showWorkProcessDetails($processDetails, data);
              //   }
              // });
              _self._showWorkProcessDetails($processDetails, data);
              _self.showedWorkProcessDetails = true;
            } else {
              $processDetails = $($(e.target).attr('href')).parent().find('.wf-share-data .work-process-details');
              if ($processDetails.length > 0) {
                _self._showWorkProcessDetails($processDetails, data);
                _self.showedWorkProcessDetails = true;
              }
            }
          }
        }
      });
    },
    // 显示办理过程详细信息
    _showWorkProcessDetails: function ($processDetails, data) {
      var _self = this;
      $.each($processDetails, function () {
        var $processDetail = $(this);
        var flowInstUuid = $processDetail.attr('flowInstUuid');
        var processes = data[flowInstUuid];
        if (processes) {
          $processDetail.html('');
          $processDetail.css('overflow', 'hidden');
          var processDetailHeight = $processDetail.parent().height();
          if (processDetailHeight <= 0) {
            processDetailHeight = Math.abs(processDetailHeight);
          }
          $processDetail.attr('orgin-height', processDetailHeight + 17);
          $processDetail.css('height', processDetailHeight + 17);
          var sb = new StringBuilder();
          sb.append('<div class="content">');
          $.each(processes, function (i, process) {
            var taskName = process.taskName;
            var assignee = process.assignee;
            var submitTime = process.submitTime;
            var opinion = process.opinion;
            var endTime = process.endTime;
            if (endTime != null) {
              sb.append(
                '<u style="color:#333333;font-weight: bold;margin-right: 10px;">' +
                  taskName +
                  '</u><span style="color: #333333;margin-right: 10px">' +
                  assignee +
                  '</span><i style="color: #999999;">' +
                  endTime +
                  '</i>'
              );
              if (opinion == null) {
                opinion = '';
              }
              sb.append('<p style="color:#666666;line-height: 100%;margin-top: 3px;">' + opinion + '</p>');
            }
          });
          sb.append('</div>');
          $processDetail.html(sb.toString());

          var $collapseBtn = $('<div class="collapse-btn"><i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i></div>');
          $collapseBtn
            .appendTo($processDetail)
            .off('click')
            .on('click', function () {
              var orginHeight = parseInt($processDetail.attr('orgin-height'));
              var contentHeight = $processDetail.find('.content').height() + 17;
              if ($collapseBtn.hasClass('collapsed')) {
                $collapseBtn.removeClass('collapsed');
                $processDetail.css('height', orginHeight);
              } else {
                $collapseBtn.addClass('collapsed');
                $processDetail.css('height', orginHeight > contentHeight ? orginHeight : contentHeight);
              }
              // 刷新表单滚动条
              _self.refreshFormScroller();
            });
        }
      });
      if ($processDetails && $processDetails.length > 0) {
        // 刷新表单滚动条
        _self.refreshFormScroller();
      }
    },
    // 获取当前承办的数据
    getShareData: function (belongToTaskInstUuid) {
      var _self = this;
      var shareDatas = _self.getShareDatas();
      for (var i = 0; i < shareDatas.length; i++) {
        if (shareDatas[i].belongToTaskInstUuid == belongToTaskInstUuid) {
          return shareDatas[i];
        }
      }
      return null;
    },
    // 检查选择项
    checkSelection: function ($btn) {
      var $checkbox = $btn.closest('.wf-share-data').find('input[name=checkOne]:checked');
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
      var belongToTaskInstUuid = $wfShareData.find('.btn-group-header').attr('belongToTaskInstUuid');
      var shareData = _self.getShareData(belongToTaskInstUuid);
      if (shareData == null) {
        return [];
      }
      var $checkboxes = $wfShareData.find('input[name=checkOne]:checked');
      var returnItems = [];
      var shareItems = shareData.shareItems;
      for (var i = 0; i < shareItems.length; i++) {
        var shareItem = shareItems[i];
        $.each($checkboxes, function () {
          if (shareItem.taskInstUuid == $(this).attr('taskInstUuid')) {
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
      var belongToTaskInstUuid = $wfShareData.find('.btn-group-header').attr('belongToTaskInstUuid');
      var shareData = _self.getShareData(belongToTaskInstUuid);
      if (shareData == null) {
        return [];
      }
      var $checkbox = $btn.closest('tr').find('input[name=checkOne]');
      var flowInstUuid = $checkbox.attr('flowinstuuid');
      var shareItems = shareData.shareItems;
      for (var i = 0; i < shareItems.length; i++) {
        var shareItem = shareItems[i];
        if (shareItem.flowInstUuid == flowInstUuid) {
          return shareItem;
        }
      }
      return {};
    },
    // 显示
    show: function () {},
    // 隐藏
    hide: function () {},
    // 绑定承办事件
    bindUndertakeSituationEvents: function ($wfShareData) {
      var _self = this;
      // 表头操作
      // 添加承办部门(人)
      $('.btn-group-header', $wfShareData).on('click', '.btn-add-subflow', function () {
        var title = $(this).text();
        var actionType = $(this).attr('btnid');
        var $groupHeader = $(this).closest('.btn-group-header');
        var dataSource = $groupHeader.attr('dataSource');
        var businessType = $groupHeader.attr('businessType');
        var businessRole = $groupHeader.attr('businessRole');
        var belongToTaskInstUuid = $groupHeader.attr('belongToTaskInstUuid');
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
      $('.btn-group-header', $wfShareData).on('click', '.btn-add-major-flow', function () {
        var title = $(this).text();
        var actionType = $(this).attr('btnid');
        var $groupHeader = $(this).closest('.btn-group-header');
        var dataSource = $groupHeader.attr('dataSource');
        var businessType = $groupHeader.attr('businessType');
        var businessRole = $groupHeader.attr('businessRole');
        var belongToTaskInstUuid = $groupHeader.attr('belongToTaskInstUuid');
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
      $('.btn-group-header', $wfShareData).on('click', '.btn-add-minor-flow', function () {
        var title = $(this).text();
        var actionType = $(this).attr('btnid');
        var $groupHeader = $(this).closest('.btn-group-header');
        var dataSource = $groupHeader.attr('dataSource');
        var businessType = $groupHeader.attr('businessType');
        var businessRole = $groupHeader.attr('businessRole');
        var belongToTaskInstUuid = $groupHeader.attr('belongToTaskInstUuid');
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
      // 关闭子流程查看本流程
      $('.btn-group-header', $wfShareData).on('click', '.btn-closeSubView', function () {
        var _this = $(this);
        var $groupHeader = $(this).closest('.btn-group-header');
        var belongToTaskInstUuid = $groupHeader.attr('belongToTaskInstUuid');
        var shareData = _self.getShareData(belongToTaskInstUuid);
        var flowInstUuid = shareData.belongToFlowInstUuid;
        var taskId = shareData.belongToTaskId;
        $.post({
          url: ctx + '/api/workflow/work/setViewMainFlow',
          data: {
            flowInstUuid: flowInstUuid,
            taskId: taskId,
            childLookParent: '0'
          },
          mask: true,
          success: function (result) {
            appModal.info('关闭成功！子流程不可查看本流程详细信息！', function () {
              _this.addClass('hide');
              _this.parent().find('.btn-allowSubView').removeClass('hide');
            });
          }
        });
      });
      // 允许子流程查看本流程
      $('.btn-group-header', $wfShareData).on('click', '.btn-allowSubView', function () {
        var _this = $(this);
        var $groupHeader = $(this).closest('.btn-group-header');
        var belongToTaskInstUuid = $groupHeader.attr('belongToTaskInstUuid');
        var shareData = _self.getShareData(belongToTaskInstUuid);
        var flowInstUuid = shareData.belongToFlowInstUuid;
        var taskId = shareData.belongToTaskId;
        $.post({
          url: ctx + '/api/workflow/work/setViewMainFlow',
          data: {
            flowInstUuid: flowInstUuid,
            taskId: taskId,
            childLookParent: '1'
          },
          mask: true,
          success: function (result) {
            appModal.info('开启成功！子流程可查看本流程详细信息！', function () {
              _this.addClass('hide');
              _this.parent().find('.btn-closeSubView').removeClass('hide');
            });
          }
        });
      });
      // 一键补发
      $('.share-data-error', $wfShareData).on('click', '#yj_replacement', function () {
        var subTaskData = _self.subTaskData;
        $.post({
          url: ctx + '/api/workflow/work/resendSubFlow',
          data: {
            belongToTaskInstUuid: subTaskData.taskInstUuid
          },
          success: function (result) {
            if (result.code == 0) {
              $('.share-data-error', $wfShareData).hide(); // 一键补发区域隐藏
              $('.dispense-process', $wfShareData).show(); // 分发进度显示
              _self.loopCount = 0; // 循环次数置0

              // 改变分发进度html
              var dispatched = 0;
              var dispatchError = 0;

              $.each(_self.resendSubData, function (placeHolder, datas) {
                for (var i = 0; i < datas.length; i++) {
                  if (i == 0) {
                    for (var j = 0; j < datas[i].shareItems.length; j++) {
                      if (datas[i].shareItems[j].dispatchState == 1) {
                        dispatched++;
                      } else {
                        dispatchError++;
                      }
                    }
                  }
                }
              });
              // 重新设置分发进度
              $('.dispense-process', $wfShareData).html(
                '<span class="circle"><i class="iconfont icon-ptkj-tijiaofabufasong"></i></span>' +
                  '<span class="detail-text">努力分发中（' +
                  0 +
                  '/' +
                  dispatchError +
                  '）</span>'
              );
              _self.dispatchedCount = dispatched;
              _self.loadSubTaskData(function (subTaskData) {
                _self.subTaskData = subTaskData;
                _self.showSubTaskData();
                _self.workView.onShareDataViewerInit.apply(_self.workView, _self);
                _self.subTaskDataLoaded = true;
              });
              appModal.success('一键补发中！');
            } else {
              appModal.error('补发失败！');
            }
          }
        });
      });
      // 查看分发错误原因
      $('.share-error-reason', $wfShareData).on('click', function () {
        var message = '<table id="error_reason_table"></table>';
        var $dialog = top.appModal.dialog({
          title: '分发失败原因',
          message: message,
          size: 'large',
          className: 'container-error-table',
          shown: function () {
            $('#error_reason_table')
              .bootstrapTable('destroy')
              .bootstrapTable({
                data: _self.dispatchErrorList || [],
                striped: true,
                pageNumber: 1,
                pagination: true,
                sidePagination: 'client',
                pageSize: 10,
                hideTotalPageText: true,
                columns: [
                  {
                    title: '序号',
                    field: 'id',
                    width: 60,
                    align: 'center',
                    formatter: function (value, row, index) {
                      var result = '';
                      result = index + 1;
                      setTimeout(function () {
                        var options = $('#error_reason_table').bootstrapTable('getOptions');
                        result = options.pageSize * (options.pageNumber - 1) + index + 1;
                      }, 200);
                      return result;
                    }
                  },
                  {
                    title: '承办部门',
                    field: 'todoName',
                    width: 194,
                    align: 'center',
                    formatter: function (value, row, index) {
                      var splitArray = [];
                      var str = '';
                      if (value) {
                        splitArray = value.trim().split(' ');
                        if (splitArray.length == 2) {
                          str +=
                            '<span style="background: #ECF3FD;color:#488CEE;font-size: 10px;border: 1px solid #A3C5F6;border-radius: 10%;padding: 2px;margin-right: 3px;">' +
                            splitArray[0] +
                            '</span>' +
                            splitArray[1];
                        } else if (splitArray.length == 1) {
                          str += splitArray[0];
                        }
                      }
                      return str;
                    }
                  },
                  {
                    title: '分发失败原因',
                    field: 'dispatchResultMsg',
                    formatter: function (value, row, index) {
                      return "<div style='white-space:normal;overflow:visible;'>" + value + '</div>';
                    }
                  }
                ]
              });
          },
          buttons: {
            close: {
              label: '关闭',
              className: 'well-btn w-btn-default',
              callback: function () {}
            }
          }
        });
      });
      // 催办
      $('.btn-group-header', $wfShareData).on('click', '.btn-remind', function () {
        if (!_self.checkSelection($(this))) {
          return;
        }
        var selection = _self.getSelection($(this));
        _self.remind(selection);
      });
      // 重办
      $('.btn-group-header', $wfShareData).on('click', '.btn-redo', function () {
        if (!_self.checkSelection($(this))) {
          return;
        }
        var $groupHeader = $(this).closest('.btn-group-header');
        var dataSource = $groupHeader.attr('dataSource');
        var selection = _self.getSelection($(this));
        _self.redo(selection, dataSource);
      });
      // 终止
      $('.btn-group-header', $wfShareData).on('click', '.btn-stop', function () {
        if (!_self.checkSelection($(this))) {
          return;
        }
        var $groupHeader = $(this).closest('.btn-group-header');
        var dataSource = $groupHeader.attr('dataSource');
        var selection = _self.getSelection($(this));
        _self.stop(selection, {}, dataSource);
      });
      // 信息分发
      $('.btn-group-header', $wfShareData).on('click', '.btn-send-message', function () {
        if (!_self.checkSelection($(this))) {
          return;
        }
        var selection = _self.getSelection($(this));
        _self.sendMessage(selection);
      });
      // 协办时限
      $('.btn-group-header', $wfShareData).on('click', '.btn-limit-time', function () {
        if (!_self.checkSelection($(this))) {
          return;
        }
        var $groupHeader = $(this).closest('.btn-group-header');
        var dataSource = $groupHeader.attr('dataSource');
        var selection = _self.getSelection($(this));
        _self.limitTime(selection, dataSource);
      });

      // 行操作
      // 催办
      $('.table', $wfShareData).on('click', 'button.btn-remind', function () {
        var rowData = _self.getRowData($(this));
        _self.remind([rowData]);
      });
      // 重办
      $('.table', $wfShareData).on('click', 'button.btn-redo', function () {
        var rowData = _self.getRowData($(this));
        _self.redo([rowData]);
      });
      // 终止
      $('.table', $wfShareData).on('click', 'button.btn-stop', function () {
        var rowData = _self.getRowData($(this));
        _self.stop([rowData], {});
      });

      // 附件下载
      $('.table', $wfShareData).on('click', 'a.share-file-item', function () {
        var fileId = $(this).parent().attr('fileId');
        var commonId = $(this).attr('common-id');

        if (commonId == 'a-download') {
          // 下载
          FileDownloadUtils.downloadMongoFile({
            fileId: fileId
          });
        } else if (commonId == 'a-view') {
          // 预览
          var wopiComponent = SystemParams.getValue('sys.context.path') + '/wopi/files/' + fileId + '?access_token=1564653762894';
          require('filePreviewApi').preview(wopiComponent);
        } else if (commonId == 'a-downloadAll') {
          // 全部下载
          var files = [];
          $(this)
            .parents('td')
            .find('.work-flow-attach')
            .find('div')
            .each(function () {
              var id = $(this).attr('fileid');
              if (id) {
                files.push({ fileID: id });
              }
            });
          var title = '附件-';
          var $dyform = DyformFacade.get$dyform();
          $dyform.getDyformTitle(
            function () {
              title += arguments[0];
            },
            function () {}
          );
          var reg = /\\|\/|\:|\*|\?|\<|\>|"|\|/g;
          title = title.replace(reg, ' ');
          var url = ctx + fileServiceURL.downAllFiles + urlencode(JSON.stringify(files)) + '&includeFolder=false';
          url += '&fileName=' + title; // 下载文件取时间格式字符（后续可以优化配置增加用户自定义下载文件命名）
          // window.location.href = url;
          WellFileUpload.downloadURL(url);
        }
      });

      // 全选/不选
      $('.table', $wfShareData).on('click', "input[name='checkAll']", function () {
        if (this.checked === true) {
          $(".table input[name='checkOne'][disabled!='disabled']", _self.element).prop('checked', 'checked');
        } else {
          $(".table input[name='checkOne'][disabled!='disabled']", _self.element).prop('checked', '');
        }
      });
    },
    // 添加承办部门(人)
    addSubflow: function (options) {
      var _self = this;
      var dataSource = options.dataSource;
      var actionType = options.actionType;
      var businessType = options.businessType;
      var applicationConfig = null;
      // 业务类别不为空，取业务类别对应的子表单配置
      if (StringUtils.isNotBlank(businessType)) {
        $.get({
          url: ctx + '/api/workflow/work/getBusinessApplicationConfig',
          data: {
            dataSource: dataSource,
            actionType: actionType,
            businessType: businessType
          },
          async: false,
          success: function (result) {
            applicationConfig = result.data;
          }
        });
      }

      if (applicationConfig != null) {
        _self.addSubflowWithApplicationConfig(options, applicationConfig);
      } else {
        _self.addSubflowWithDefaultOptions(options);
      }
    },
    addSubflowWithApplicationConfig: function (options, applicationConfig) {
      var _self = this;
      var title = options.title || '添加承办部门（人）';
      var dataSource = options.dataSource;
      var businessType = options.businessType;
      var businessRole = options.businessRole;
      var shareData = options.shareData;
      appContext.require(['DmsDataServices'], function (DmsDataServices) {
        var dmsDataServices = new DmsDataServices();
        var undertakeData = {
          dataSource: dataSource,
          actionName: title,
          businessType: businessType,
          businessRole: businessRole,
          belongToTaskInstUuid: shareData.belongToTaskInstUuid,
          belongToFlowInstUuid: shareData.belongToFlowInstUuid,
          botRuleUuid: applicationConfig.ruleUuid
        };
        var dlgOptions = {
          title: title,
          target: '_dialog',
          urlParams: {
            ac_id: 'btn_list_view_add',
            idKey: 'UUID',
            formUuid: applicationConfig.formUuid,
            ep_dmsDocumentViewFragment: 'WorkFlowUndertakeSituationFragment',
            ep_undertakeData: JSON.stringify(undertakeData)
          },
          ui: _self.workView
        };
        dmsDataServices.openDialog(dlgOptions);
      });
    },
    addSubflowWithDefaultOptions: function (options) {
      var _self = this;
      var workView = _self.workView;
      var shareData = options.shareData;
      var isMajor = options.isMajor;
      // 获取子流程的标签信息，没有标签的为子流程定义信息
      _self.loadNewFlowLabelInfos(shareData.belongToTaskInstUuid, isMajor, function (result) {
        var newFlowInfos = result.data;
        options.newFlowInfos = newFlowInfos;
        var customFlowInfo = false;
        // 自定义添加承办回调处理
        var customizeAddSubflowCallback = function (newFlowId, taskUsers) {
          _self.customizeAddSubflowCallback(newFlowId, taskUsers, options);
        };
        // 主办或协办
        if (StringUtils.isBlank(isMajor)) {
          if ($.isFunction(workView.customizeAddMajorOrMinorSubflow)) {
            customFlowInfo = true;
            workView.customizeAddMajorOrMinorSubflow(newFlowInfos, customizeAddSubflowCallback);
          }
        } else if (isMajor == '1') {
          // 主办
          if ($.isFunction(workView.customizeAddMajorSubflow)) {
            customFlowInfo = true;
            workView.customizeAddMajorSubflow(newFlowInfos, customizeAddSubflowCallback);
          }
        } else {
          // 协办
          if ($.isFunction(workView.customizeAddMinorSubflow)) {
            customFlowInfo = true;
            workView.customizeAddMinorSubflow(newFlowInfos, customizeAddSubflowCallback);
          }
        }

        // 默认弹出框选择承办处理方式
        if (!customFlowInfo) {
          _self.doAddSubflowWithDefaultOptions(options);
        }
      });
    },
    doAddSubflowWithDefaultOptions: function (options) {
      var _self = this;
      var title = options.title || '添加承办部门（人）';
      var businessType = options.businessType;
      var businessRole = options.businessRole;
      var isMajor = options.isMajor;
      var newFlowInfos = options.newFlowInfos || [];

      var containerId = UUID.createUUID();
      var containerSelector = '#' + containerId;
      var content = '<div id="' + containerId + '">';
      content += '<form class="form-horizontal" role="form">';
      content += '	<div class="form-group container-isMajor">';
      // content += ' <label class="col-sm-2 control-label">承办流程</label>';
      content += '		<div class="col-sm-12">';
      content += '			<input type="text" class="form-control" id="newFlowIdLabel" name="newFlowIdLabel"/>';
      content += '			<input type="hidden" class="form-control" id="newFlowId" name="newFlowId"/>';
      //      content += '				<option value="1" selected>主办</option>';
      //      content += '				<option value="2">协办</option>';
      //      content += '			</select>';
      content += '		</div>';
      content += '	</div>';
      content += '	<div class="form-group">';
      // content += ' <label class="col-sm-2 control-label">承办部门</label>';
      content += '		<div class="col-sm-12">';
      content += '			<textarea class="form-control" id="subflowTaskUserName" rows="3"';
      content += '			placeholder="选择组织/业务通讯录"></textarea>';
      content += '			<input type="hidden" id="subflowTaskUserId" />';
      content += '		</div>';
      content += '	</div>';
      content += '</form>';
      content += '</div>';
      var dlgOptions = {
        title: title,
        size: 'middle',
        message: content,
        // onEscape: function () {},
        // 显示弹出框后事件
        shown: function () {
          // 获取子流程的标签信息，没有标签的为子流程定义信息
          var data = [];
          $.each(newFlowInfos, function (index, object) {
            data.push({
              id: object.ids.join('#'),
              text: object.label
            });
          });
          var defaultBlank = StringUtils.isBlank(isMajor);
          $("input[name='newFlowIdLabel']", containerSelector).wSelect2({
            data: data,
            defaultBlank: defaultBlank,
            valueField: 'newFlowId',
            labelField: 'newFlowIdLabel'
          });
          if (StringUtils.isNotBlank(options.isMajor) && newFlowInfos.length > 0) {
            $("input[name='newFlowIdLabel']", containerSelector).wSelect2('val', newFlowInfos[0].ids.join('#'));
          }

          // JDS.call({
          // service : "listWorkService.getNewFlows",
          // data : [ shareData.belongToFlowInstUuid,
          // shareData.belongToTaskId ],
          // version : "",
          // success : function(result) {
          // var newFlows = result.data;
          // $.each(newFlows, function(i, newFlow) {
          // var label = newFlow.label;
          // if (StringUtils.isBlank(label)) {
          // label = newFlow.flowName;
          // }
          // var option = "<option value='" + newFlow.id + "'>" +
          // label + "</option>";
          // $("select[name='isMajor']").append(option);
          // });
          // }
          // });
          // 是否主办不为空，隐藏是否主办下拉选择
          //          if (isMajor != null) {
          //            $('select[name=isMajor]', containerSelector).val(isMajor);
          //            $('select[name=isMajor]', containerSelector).closest('.form-group').hide();
          //          }
          $('#subflowTaskUserName', containerSelector).on('click', function (event) {
            var unitOptions = null;
            if (StringUtils.isNotBlank(businessType) && StringUtils.isNotBlank(businessRole)) {
              unitOptions = {
                targetWindow: window,
                valueField: 'subflowTaskUserId',
                labelField: 'subflowTaskUserName',
                title: '业务通讯录',
                type: 'BusinessBook',
                multiple: true,
                inputReadOnly: false,
                selectTypes: 'O;B;D;J;G;DU;E;R',
                zIndex: 999999999,
                otherParams: {
                  categoryId: businessType // 此处分类的UUID,必填
                },
                callback: function (values, labels) {
                  console.log(values + '--------' + labels);
                }
              };
            } else {
              unitOptions = {
                title: '选择承办人员',
                valueField: 'subflowTaskUserId',
                labelField: 'subflowTaskUserName',
                type: 'all',
                multiple: true,
                selectTypes: 'U',
                valueFormat: 'justId',
                zIndex: 999999999,
                callback: function () {}
              };
            }
            // 二开扩展组织选择框处理
            unitOptions = _self._customizeUnitOptionsIfReuqired(options, unitOptions);
            $.unit2.open(unitOptions);
          });
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              //          	  var $option = $("select[name='isMajor']").find("option:selected");
              //              var labelInfo = $option.data("labelInfo") || {};
              //              var ids = labelInfo.ids || [];
              var newFlowId = $("input[name='newFlowId']", containerSelector).val(); //labelInfo.ids.join(";");
              if (StringUtils.isBlank(newFlowId)) {
                if (options.isMajor == '1') {
                  appModal.warning('请选择主办');
                } else if (options.isMajor == '2') {
                  appModal.warning('请选择协办');
                } else {
                  appModal.warning('请选择承办');
                }
                return false;
              }
              newFlowId = newFlowId.split('#').join(';');
              var taskUsers = $('#subflowTaskUserId', containerSelector).val();
              if (StringUtils.isBlank(taskUsers)) {
                taskUsers = [];
              } else {
                taskUsers = taskUsers.split(';');
              }
              if (taskUsers.length == 0) {
                appModal.warning('请选择组织/业务通讯录');
                return false;
              }
              // 发起子流程接口请求处理
              _self.doStartSubflow(newFlowId, taskUsers, options);
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {}
          }
        }
      };
      appModal.dialog(dlgOptions);
    },
    // 发起子流程接口请求处理
    doStartSubflow: function (newFlowId, taskUsers, options) {
      var title = options.title || '添加承办部门（人）';
      var businessType = options.businessType;
      var businessRole = options.businessRole;
      var shareData = options.shareData;
      var dataSource = options.dataSource;
      var isMajor = options.isMajor == '1';
      // 分支流
      if (dataSource == '1') {
        JDS.restfulPost({
          url: ctx + '/api/workflow/work/startBranchTask',
          data: {
            belongToTaskInstUuid: shareData.belongToTaskInstUuid,
            belongToFlowInstUuid: shareData.belongToFlowInstUuid,
            isMajor: isMajor,
            taskUsers: taskUsers,
            businessType: businessType,
            businessRole: businessRole,
            actionName: title
          },
          mask: true,
          success: function (result) {
            appModal.info('操作成功！', function () {
              appContext.getWindowManager().refresh();
            });
          }
        });
      } else {
        // 子流程
        JDS.restfulPost({
          url: ctx + '/api/workflow/work/startSubFlow',
          data: {
            belongToTaskInstUuid: shareData.belongToTaskInstUuid,
            belongToFlowInstUuid: shareData.belongToFlowInstUuid,
            newFlowId: newFlowId,
            isMajor: isMajor,
            taskUsers: taskUsers,
            businessType: businessType,
            businessRole: businessRole,
            actionName: title
          },
          mask: true,
          success: function (result) {
            appModal.info('操作成功！', function () {
              appContext.getWindowManager().refresh();
            });
          }
        });
      }
    },
    // 二开扩展接口回调处理
    customizeAddSubflowCallback: function (newFlowId, taskUsers, options) {
      var _self = this;
      var id = newFlowId;
      if ($.isArray(id)) {
        id = id.join(';');
      }
      _self.doStartSubflow(id, taskUsers, options);
    },
    // 二开扩展组织选择框处理
    _customizeUnitOptionsIfReuqired: function (options, unitOptions) {
      var _self = this;
      var workView = _self.workView;
      var isMajor = options.isMajor;
      var customUnitOptions = null;
      // 主办或协办
      if (StringUtils.isBlank(isMajor)) {
        if ($.isFunction(workView.getMajorOrMinorUserUnitOptions)) {
          customUnitOptions = workView.getMajorOrMinorUserUnitOptions();
        } else if ($.isFunction(workView.getMajorOrMinorUserFieldName)) {
          customUnitOptions = _self._getCustomizeUnitOptions(workView, workView.getMajorOrMinorUserFieldName());
        }
      } else if (isMajor == '1') {
        // 主办
        if ($.isFunction(workView.getMajorUserUnitOptions)) {
          customUnitOptions = workView.getMajorUserUnitOptions();
        } else if ($.isFunction(workView.getMajorUserFieldName)) {
          customUnitOptions = _self._getCustomizeUnitOptions(workView, workView.getMajorUserFieldName());
        }
      } else {
        // 协办
        if ($.isFunction(workView.getMinorUserUnitOptions)) {
          customUnitOptions = workView.getMinorUserUnitOptions();
        } else if ($.isFunction(workView.getMinorUserFieldName)) {
          customUnitOptions = _self._getCustomizeUnitOptions(workView, workView.getMinorUserFieldName());
        }
      }
      // 合并二开扩展组织选择项
      if (customUnitOptions != null) {
        unitOptions = $.extend(true, unitOptions, customUnitOptions);
        // otherParams传入null时后端报错处理
        if (unitOptions.otherParams == null) {
          delete unitOptions.otherParams;
        }
      }
      return unitOptions;
    },
    // 获取自定义的表单字段组织选择项配置信息
    _getCustomizeUnitOptions: function (workView, fieldName) {
      if (StringUtils.isBlank(fieldName)) {
        return null;
      }
      var dyform = workView.getDyform();
      var field = dyform.getField(fieldName);
      if (field == null) {
        // console.error("表单字段不存在：" + fieldName)
        return null;
      }
      var orgOptions = field.getOptions();
      var excludeValues = [];
      var otherParams = null;
      var filterCondition = orgOptions.filterCondition;
      if (filterCondition) {
        var paramsSchema = 'otherParams://';
        if (filterCondition.indexOf(paramsSchema) === 0) {
          try {
            otherParams = eval('(' + filterCondition.substr(paramsSchema.length) + ')'); // $.parseJSON(filterCondition.substr(paramsSchema.length));
            if (otherParams && otherParams.filterCondition) {
              excludeValues = otherParams.filterCondition.split(/;|,|；|，/);
            }
          } catch (ex) {}
        } else {
          //按中英文的逗号和分号分割
          excludeValues = filterCondition.split(/;|,|；|，/);
        }
      }
      return {
        multiple: orgOptions.mutiSelect,
        type: orgOptions.typeList.join(';'),
        defaultType: orgOptions.defaultType,
        selectTypes: orgOptions.selectTypeList.join(';'),
        otherParams: otherParams,
        excludeValues: excludeValues
      };
    },
    loadNewFlowLabelInfos: function (parentTaskInstUuid, isMajor, successCallback) {
      var _self = this;
      $.get({
        url: ctx + '/api/workflow/work/getNewFlowLabelInfos',
        data: {
          taskInstUuid: parentTaskInstUuid,
          roleFlag: StringUtils.isBlank(isMajor) ? '0' : isMajor
        },
        success: function (result) {
          successCallback.call(_self, result);
        }
      });
    },
    // 催办
    remind: function (selection) {
      var taskInstUuids = [];
      $.each(selection, function () {
        taskInstUuids.push(this.taskInstUuid);
      });
      var content = '<form class="form-horizontal" role="form">';
      // content += ' <div class="form-group">';
      // content += ' <label class="col-sm-2 control-label">消息类型</label>';
      // content += ' <div class="col-sm-10">';
      // content += ' <select class="form-control">';
      // content += ' <option value="ON_LINE">在线消息</option>';
      // content += ' <option value="SMS">短信</option>';
      // content += ' <option value="EMAIL">邮件</option>';
      // content += ' </select>';
      // content += ' </div>';
      // content += ' </div>';
      content += '	<div class="form-group">';
      content += '		<label class="col-sm-2 control-label">催办内容</label>';
      content += '		<div class="col-sm-10">';
      content += '			<textarea class="form-control" name="msgContent" rows="5"></textarea>';
      content += '		</div>';
      content += '	</div>';
      content += '</form>';
      var options = {
        title: '催办',
        size: 'middle',
        message: content,
        // 显示弹出框后事件
        shown: function () {},
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var $msgContent = $("textarea[name='msgContent']");
              var msgContent = $msgContent.val ? $msgContent.val() : $msgContent[0].value;
              JDS.restfulPost({
                url: ctx + '/api/workflow/work/remind',
                data: {
                  taskInstUuids: taskInstUuids,
                  opinionLabel: '',
                  opinionValue: '',
                  opinionText: msgContent
                },
                mask: true,
                success: function (result) {
                  appModal.info('催办成功！', function () {
                    appContext.getWindowManager().refresh();
                  });
                }
              });
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {}
          }
        }
      };
      appModal.dialog(options);
    },
    // 重办
    redo: function (selection, dataSource) {
      var taskInstUuids = [];
      $.each(selection, function () {
        taskInstUuids.push(this.taskInstUuid);
      });
      appModal.confirm('请确认是否重办？', function (result) {
        if (result) {
          // 分支流重办
          if (dataSource == '1') {
            $.post({
              url: ctx + '/api/workflow/work/redoBranchTask',
              data: {
                taskInstUuids: taskInstUuids
              },
              mask: true,
              success: function (result) {
                appModal.info('重办成功！', function () {
                  appContext.getWindowManager().refresh();
                });
              }
            });
          } else {
            // 子流程重办
            $.post({
              url: ctx + '/api/workflow/work/redoFlow',
              service: 'listWorkService.redoFlow',
              data: {
                taskInstUuids: taskInstUuids
              },
              mask: true,
              success: function (result) {
                appModal.info('重办成功！', function () {
                  appContext.getWindowManager().refresh();
                });
              }
            });
          }
        }
      });
    },
    // 终止
    stop: function (selection, interactionTaskData, dataSource) {
      var _self = this;
      var workData = _self.workView.getWorkData();
      var taskInstUuid = workData.taskInstUuid;
      var taskInstUuids = [];
      // 是否终止自身
      var stopSelf = false;
      $.each(selection, function () {
        taskInstUuids.push(this.taskInstUuid);
        if (taskInstUuid == this.taskInstUuid) {
          stopSelf = true;
        }
      });
      var stopFunction = function (taskInstUuids, interactionTaskData) {
        var stopUrl = ctx + '/api/workflow/work/stopFlow';
        // 终止分支流
        if (dataSource == '1') {
          stopUrl = ctx + '/api/workflow/work/stopBranchTask';
        }
        JDS.restfulPost({
          url: stopUrl,
          data: {
            taskInstUuids: taskInstUuids,
            interactionTaskData: interactionTaskData
          },
          mask: true,
          success: function (result) {
            if (stopSelf) {
              appModal.info({
                message: '终止成功！',
                resultCode: 0
              });
            } else {
              appModal.info('终止成功！', function () {
                appContext.getWindowManager().refresh();
              });
            }
          },
          error: function (jqXHR) {
            var callback = function () {
              stopFunction.call(_self, taskInstUuids, _self.workView.workFlow.getInteractionTaskData());
            };
            _self.workView.handlerError.call(_self.workView, jqXHR, callback);
          }
        });
      };
      appModal.confirm('请确认是否终止？', function (result) {
        if (result) {
          stopFunction.call(_self, taskInstUuids, interactionTaskData);
        }
      });
    },
    // 信息分发
    sendMessage: function (selection) {
      var taskInstUuids = [];
      $.each(selection, function () {
        taskInstUuids.push(this.taskInstUuid);
      });
      var content = '<form class="form-horizontal" role="form">';
      content += '	<div class="form-group">';
      content += '		<label class="col-sm-2 control-label">分发内容</label>';
      content += '		<div class="col-sm-10">';
      content += '			<textarea class="form-control" name="contentInfo" rows="5"></textarea>';
      content += '		</div>';
      content += '	</div>';
      content += '	<div class="form-group">';
      content += '		<label class="col-sm-2 control-label">附件</label>';
      content += '		<div class="col-sm-10">';
      content += '			<div id="subflowFileInfo" class="sub-flow-file-info"></div>';
      content += '		</div>';
      content += '	</div>';
      content += '</form>';
      var options = {
        title: '信息分发',
        size: 'middle',
        message: content,
        // onEscape: function () {},
        // 显示弹出框后事件
        shown: function () {
          // 弹出框宽度
          var fileNum = 0;
          var fieldName = 'subflowFileInfo'; // 附件字段的名字
          var $attachContainer = $('#' + fieldName); // 上传控件要存放的位置,为jquery对象
          // 创建上传控件
          var elementID = WellFileUpload.getCtlID4Dytable('', fieldName);
          var fileupload = new WellFileUpload(elementID);
          // 初始化上传控件
          fileupload.init(false, $attachContainer, false, true, []);
          $('.file_icon').css('margin-right', '10px');
          // 上传成功
          fileupload.uploadOkCallback = function (fileInfo) {
            var files = WellFileUpload.files[elementID];
            fileNum += 1;
          };
          // 删除成功
          fileupload.deleteOkCallback = function (fileInfo) {
            var files = WellFileUpload.files[elementID];
            fileNum -= 1;
          };
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var contentInfo = $("textarea[name='contentInfo']").val();
              if (!contentInfo) {
                appModal.warning('请填写分发内容');
                return false;
              }
              var repoFileIds = [];
              var elementID = WellFileUpload.getCtlID4Dytable('', 'subflowFileInfo');
              var files = WellFileUpload.files[elementID] || [];
              for (var i in files) {
                repoFileIds.push(files[i].fileID);
              }
              JDS.restfulPost({
                url: ctx + '/api/workflow/work/distributeInfo',
                data: {
                  taskInstUuids: taskInstUuids,
                  content: contentInfo,
                  fileIds: repoFileIds
                },
                mask: true,
                success: function (result) {
                  appModal.info('分发成功！', function () {
                    appContext.getWindowManager().refresh();
                  });
                }
              });
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {}
          }
        }
      };
      appModal.dialog(options);
    },
    // 协办时限
    limitTime: function (selection, dataSource) {
      var flowInstUuids = [];
      $.each(selection, function () {
        flowInstUuids.push(this.flowInstUuid);
      });
      var defalutDate = new Date().format('yyyy-MM-dd');
      var content = "反馈时限&nbsp;&nbsp;<input type='date' name='subflowDueTime' value=" + defalutDate + '  />';
      var options = {
        title: '协办时限',
        size: 'small',
        message: content,
        // onEscape: function () {},
        // 显示弹出框后事件
        shown: function () {},
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var $subflowDueTime = $("input[name='subflowDueTime']");
              var inputDate = $subflowDueTime.val ? $subflowDueTime.val() : $subflowDueTime[0].value;
              if (!inputDate) {
                appModal.warning('请选择反馈时限');
                return false;
              }
              if (inputDate != null) {
                inputDate = new Date(inputDate).format('yyyy-MM-dd 23:59');
              }
              // 分支流时限
              if (dataSource == '1') {
                JDS.restfulPost({
                  url: ctx + '/api/workflow/work/changeTaskDueTime',
                  data: {
                    taskDataItems: selection,
                    dueTime: inputDate
                  },
                  mask: true,
                  success: function (result) {
                    appModal.info('操作成功！', function () {
                      appContext.getWindowManager().refresh();
                    });
                  }
                });
              } else {
                // 子流程时限
                JDS.restfulPost({
                  url: ctx + '/api/workflow/work/changeFlowDueTime',
                  data: {
                    flowInstUuids: flowInstUuids,
                    dueTime: inputDate
                  },
                  mask: true,
                  success: function (result) {
                    appModal.info('操作成功！', function () {
                      appContext.getWindowManager().refresh();
                    });
                  }
                });
              }

              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {}
          }
        }
      };
      appModal.dialog(options);
    }
  });
  return WorkFlowShareDataViewer;
});
