/**
 * 多部门人员审核列表二开
 */
define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  var AppMuldepPersonAuditListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  var rowDataOfuuid = ''; // 每行待审批/查看记录的uuid
  var uuidObject = commons.UUID;
  var searchOfJobList = [];
  var $audiDialog = null;
  commons.inherit(AppMuldepPersonAuditListDevelopment, ListViewWidgetDevelopment, {
    init: function () {
      this.getDingtalkJobList();
    },
    afterRender: function () {
      var _self = this;
      $(_self.widget.element).prepend(
        '<div  class="mul-depelopment-list-header-tip"><i class="iconfont icon-ptkj-xinxiwenxintishi"></i>对于钉钉上多部门的人员，需要人工审核人员的职位</div>'
      );
    },
    // 切换为高级搜索时的事件
    changeToFieldSearch: function () {
      var _self = this;
      $('.mul-depelopment-list-header-tip', _self.widget.element).remove();
      $(_self.widget.element).prepend(
        '<div  class="mul-depelopment-list-header-tip"><i class="iconfont icon-ptkj-xinxiwenxintishi"></i>对于钉钉上多部门的人员，需要人工审核人员的职位</div>'
      );

      // 重置
      $('.fixed-table-search-toolbar', _self.widget.element)
        .find('button[name="reset"]')
        .on('click', function () {
          $('.mul-depelopment-list-header-tip', _self.widget.element).remove();
          $(_self.widget.element).prepend(
            '<div  class="mul-depelopment-list-header-tip"><i class="iconfont icon-ptkj-xinxiwenxintishi"></i>对于钉钉上多部门的人员，需要人工审核人员的职位</div>'
          );

          // 重新构建一次组织弹出框
          $('.div_search_toolbar', _self.widget.element)
            .find('input[name="BEFORE_AUDIT_MAIN_JOB"]')
            .off('click')
            .on('click', function () {
              var $element = $(this);
              $.unit2.open({
                type: 'MyUnit',
                title: '选择主职位',
                labelField: '',
                valueField: '',
                selectTypes: 'J',
                multiple: false,
                valueFormat: 'all',
                nameFormat: 'all',
                callback: function (values, labels, treeNodes) {
                  var jobNamePath = '';
                  if (treeNodes && treeNodes.length > 0) {
                    var selectTreeNode = treeNodes[0];
                    var pathArr = selectTreeNode.getPath();
                    for (var i = 0; i < pathArr.length; i++) {
                      jobNamePath += pathArr[i].name + '/';
                    }
                    jobNamePath = jobNamePath.substr(0, jobNamePath.length - 1);
                    $element.val(jobNamePath);
                  }
                },
                close: function () {}
              });
            });

          // 审核后OA主职位
          $('.div_search_toolbar', _self.widget.element)
            .find('input[name="AFTER_AUDIT_MAIN_JOB"]')
            .off('click')
            .on('click', function () {
              var $element = $(this);
              $.unit2.open({
                type: 'MyUnit',
                title: '选择主职位',
                labelField: '',
                valueField: '',
                selectTypes: 'J',
                multiple: false,
                valueFormat: 'all',
                nameFormat: 'all',
                callback: function (values, labels, treeNodes) {
                  var jobNamePath = '';
                  if (treeNodes && treeNodes.length > 0) {
                    var selectTreeNode = treeNodes[0];
                    var pathArr = selectTreeNode.getPath();
                    for (var i = 0; i < pathArr.length; i++) {
                      jobNamePath += pathArr[i].name + '/';
                    }
                    jobNamePath = jobNamePath.substr(0, jobNamePath.length - 1);
                    $element.val(jobNamePath);
                  }
                },
                close: function () {}
              });
            });

          // 钉钉所属职位
          $('.div_search_toolbar', _self.widget.element).find('input[name="JOB_NAME"]').wSelect2({
            data: searchOfJobList,
            remoteSearch: false,
            defaultBlankText: '全部',
            defaultBlankValue: ''
          });
          $('.control-label-JOB_NAME + div', '.div_search_toolbar')
            .find('.well-select-selected-value')
            .html('全部')
            .show()
            .siblings()
            .hide();

          $('#wellSelect_AUDIT_STATUS', '.div_search_toolbar').find('.well-select-selected-value').html('全部').show().siblings().hide();
        });
      // 钉钉所属职位
      $('.div_search_toolbar', _self.widget.element).find('input[name="JOB_NAME"]').wSelect2({
        data: searchOfJobList,
        remoteSearch: false,
        defaultBlankText: '全部',
        defaultBlankValue: ''
      });

      $('.control-label-JOB_NAME + div', '.div_search_toolbar').find('.well-select-selected-value').html('全部').show().siblings().hide();
      $('#wellSelect_AUDIT_STATUS', '.div_search_toolbar').find('.well-select-selected-value').html('全部').show().siblings().hide();
      // 审核前OA主职位
      $('.div_search_toolbar', _self.widget.element)
        .find('input[name="BEFORE_AUDIT_MAIN_JOB"]')
        .off('click')
        .on('click', function () {
          var $element = $(this);
          $.unit2.open({
            type: 'MyUnit',
            title: '选择主职位',
            labelField: '',
            valueField: '',
            selectTypes: 'J',
            multiple: false,
            valueFormat: 'all',
            nameFormat: 'all',
            callback: function (values, labels, treeNodes) {
              var jobNamePath = '';
              if (treeNodes && treeNodes.length > 0) {
                var selectTreeNode = treeNodes[0];
                var pathArr = selectTreeNode.getPath();
                for (var i = 0; i < pathArr.length; i++) {
                  jobNamePath += pathArr[i].name + '/';
                }
                jobNamePath = jobNamePath.substr(0, jobNamePath.length - 1);
                $element.val(jobNamePath);
              }
            },
            close: function () {}
          });
        });

      // 审核后OA主职位
      $('.div_search_toolbar', _self.widget.element)
        .find('input[name="AFTER_AUDIT_MAIN_JOB"]')
        .off('click')
        .on('click', function () {
          var $element = $(this);
          $.unit2.open({
            type: 'MyUnit',
            title: '选择主职位',
            labelField: '',
            valueField: '',
            selectTypes: 'J',
            multiple: false,
            valueFormat: 'all',
            nameFormat: 'all',
            callback: function (values, labels, treeNodes) {
              var jobNamePath = '';
              if (treeNodes && treeNodes.length > 0) {
                var selectTreeNode = treeNodes[0];
                var pathArr = selectTreeNode.getPath();
                for (var i = 0; i < pathArr.length; i++) {
                  jobNamePath += pathArr[i].name + '/';
                }
                jobNamePath = jobNamePath.substr(0, jobNamePath.length - 1);
                $element.val(jobNamePath);
              }
            },
            close: function () {}
          });
        });
    },
    // 钉所属职位
    getDingtalkJobList: function () {
      var _self = this;
      $.ajax({
        url: ctx + '/pt/dingtalk/dingJobList',
        type: 'GET',
        data: {},
        success: function (result) {
          if (result.code == 0) {
            var data = result.data;
            for (var i = 0; i < data.length; i++) {
              searchOfJobList.push({
                id: data[i],
                text: data[i]
              });
            }
          }
        }
      });
    },
    // 确定审核
    containAudit: function (opObj) {
      // console.log(opObj);
      var _self = this;
      var oaPositions = opObj.oaPositions;
      var dingdingPositions = opObj.dingdingPositions;
      var delOaPositionIndexs = [];

      // 去掉不是主职位 && oa职位为空
      for (var i = 0; i < oaPositions.length; i++) {
        var oaPosition = oaPositions[i];
        if (!oaPosition.jobName && oaPosition.isMain == 0) {
          delOaPositionIndexs.push(i);
        }
      }
      for (var j = 0; j < delOaPositionIndexs.length; j++) {
        oaPositions.splice(delOaPositionIndexs[j], 1);
      }

      var queryParams = {
        dingJobVos: dingdingPositions,
        oaJobVos: oaPositions,
        uuid: rowDataOfuuid
      };
      appModal.showMask('审核中...');
      $.ajax({
        url: ctx + '/pt/dingtalk/multiDeptUserAudit',
        type: 'POST',
        // traditional: true,
        // contentType: 'application/json',
        data: {
          jsonStr: JSON.stringify(queryParams)
        },
        success: function (result) {
          appModal.hideMask();
          if (result.code == 0) {
            appModal.success('审核成功');
            $audiDialog.modal('hide');
            // 刷新列表
            appContext.refreshWidgetById('page_20210803110646', true);
          } else {
            appModal.error(result.msg);
          }
        }
      });
    },
    // 审核按钮
    btn_audit: function (e) {
      var _self = this;
      var $container = _self.widget.element;
      var opObj = null;
      var data_index = parseInt($(e.target).closest('tr').attr('data-index'));
      var datas = _self.getData();
      rowDataOfuuid = datas[data_index].UUID;

      $audiDialog = top.appModal.dialog({
        title: '审核',
        message: '<div></div>',
        width: '800px',
        height: '600px',
        size: 'large',
        shown: function (_$dialog) {
          opObj = _self.getOpObj();
          _self.getRowDataDetail(rowDataOfuuid, function (data) {
            opObj.orginDingdingPostions = JSON.parse(JSON.stringify(data.dingJobVos));
            opObj.dingdingPositions = JSON.parse(JSON.stringify(data.dingJobVos));
            opObj.oaPositions = data.oaJobVos;
            opObj.init(_$dialog.find('.modal-content .modal-body .bootbox-body'), false);
          });
        },
        buttons: {
          save: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var validData = opObj.validDialogData();
              if (validData.errors.length > 0) {
                appModal.error(validData.errors.join('<br/>'));
                // appModal.confirm(validData.errors.join('<br/>'), function (result) {
                //   if (result) {
                //   }
                // });
                return false;
              } else {
                if (validData.isEmpty) {
                  appModal.confirm('审核后，用户将无OA职位信息！\n' + '\n' + '确定审核吗?', function (result) {
                    if (result) {
                      // console.log(opObj.getDialogData());
                      _self.containAudit(opObj);
                    }
                  });
                } else {
                  appModal.confirm('审核后，将更新用户的OA职位信息！\n' + '\n' + '确定审核吗?', function (result) {
                    if (result) {
                      _self.containAudit(opObj);
                    }
                  });
                }
              }
              return false;
            }
          },
          close: {
            label: '取消',
            className: 'btn-default',
            callback: function () {}
          }
        }
      });
    },
    // 查看按钮
    btn_lookfor: function (e) {
      var _self = this;
      var $container = _self.widget.element;
      var opObj = null;
      var data_index = parseInt($(e.target).closest('tr').attr('data-index'));
      var datas = _self.getData();
      rowDataOfuuid = datas[data_index].UUID;

      var $dialog = top.appModal.dialog({
        title: '审核记录',
        message: '<div></div>',
        width: '800px',
        height: '600px',
        size: 'large',
        shown: function (_$dialog) {
          opObj = _self.getOpObj();
          _self.getRowDataDetail(rowDataOfuuid, function (data) {
            opObj.orginDingdingPostions = JSON.parse(JSON.stringify(data.dingJobVos));
            opObj.dingdingPositions = JSON.parse(JSON.stringify(data.dingJobVos));
            opObj.oaPositions = data.oaJobVos;
            opObj.init(_$dialog.find('.modal-content .modal-body .bootbox-body'), true);
            // 隐藏操作按钮
            $('#op_callback', $dialog).hide();
            $('#op_add', $dialog).hide();
            var dels = $('.op_del', $dialog);
            console.log(dels);
            dels.each(function () {
              $(this).hide();
            });
          });
        },
        buttons: {
          close: {
            label: '关闭',
            className: 'btn-default',
            callback: function () {}
          }
        }
      });
    },
    // 获取详情
    getRowDataDetail(uuid, callback) {
      var _self = this;
      $.ajax({
        url: ctx + '/pt/dingtalk/getMultiDeptUserAudit',
        dataType: 'json',
        type: 'POST',
        async: false,
        data: {
          uuid: uuid
        },
        success: function (result) {
          if (result.code == 0) {
            // callback.call(_self, result.data);
            callback(result.data);
          }
        },
        error: function (err) {
          appModal.error(err);
        }
      });
    },

    getOpObj: function () {
      var opDialogOjb = {
        $dialogContent: null,
        readOnly: false,
        orginDingdingPostions: [],
        dingdingPositions: [],
        oaPositions: [],

        init: function ($dialogContent, readOnly) {
          var _self = this;
          this.$dialogContent = $dialogContent;
          this.readOnly = readOnly;
          $dialogContent.html(this.generateHtml());
          // 渲染钉钉职位
          _self.restoreSynchronizedData();
          // 渲染OA职位
          _self.buildOaPosition();

          // 恢复同步数据
          $dialogContent
            .find('#op_callback')
            .off('click')
            .on('click', function () {
              _self.restoreSynchronizedData();
              // _self.addDingTalkPosition();
            });
          // 添加
          $dialogContent
            .find('#op_add')
            .off('click')
            .on('click', function () {
              _self.addOaPosition();
            });
        },

        /**
         * 恢复同步数据
         */
        restoreSynchronizedData: function () {
          var $dingContent = this.$dialogContent.find('#ding_department .mul-dep__content-content');
          if (this.orginDingdingPostions.length === 0) {
            $dingContent.html('<div class="well-search-no-match"></div>');
          } else {
            $dingContent.html('');
            $dingContent.append('<ul></ul>');
            for (var i = 0; i < this.orginDingdingPostions.length; i++) {
              var orginDingdingPostion = this.orginDingdingPostions[i];
              this.addDingTalkPosition(orginDingdingPostion);

              // var $orginDingdingPostion = $(
              //   '         <li  class="well-form-control">' +
              //   '           <div class="well-input-group">' +
              //   '             <input type="text"style="flex:0 1 640px" value="人事经理" class="input-icon" /><i class="iconfont icon-ptkj-zuzhiguanli"></i>' +
              //   '           </div>' +
              //   '           <div>' +
              //   '               <input type="radio" name="radio" ><label for="radio">主职位</label>' +
              //   '           </div>' +
              //   '           <button class="btn btn-xs well-btn w-btn-primary w-line-btn op_del">删除</button>' +
              //   '         </li>');
              //
              // $orginDingdingPostions.append($orginDingdingPostion);
            }
            // $dingContent.append($orginDingdingPostions);
          }
        },

        /**
         * 添加钉钉职位
         */
        addDingTalkPosition: function (orginDingdingPostion) {
          var _self = this;

          var $dingContent = this.$dialogContent.find('#ding_department .mul-dep__content-content');
          var $ul = $dingContent.find('ul');
          var isChecked = orginDingdingPostion.isMain == 1 ? true : false;
          if ($ul.length <= 0) {
            $dingContent.html('');
            $dingContent.append('<ul></ul>');
            $ul = $dingContent.find('ul');
          }
          // var orginDingdingPostion = this.orginDingdingPostions[i];
          var $orginDingdingPostion = $(
            '         <li  class="well-form-control">' +
              '           <div class="well-input-group ding-dept" style="width: 250px">' +
              '             <input type="text"style="flex:0 1 640px" value="' +
              orginDingdingPostion.deptName +
              '" class="input-icon" disabled="disabled"/>' +
              '           </div>' +
              '           <div class="well-input-group ding-position" style="width: 250px">' +
              '             <input type="text"style="flex:0 1 640px" value="' +
              orginDingdingPostion.jobName +
              '" class="input-icon" />' +
              '           </div>' +
              '           <div>' +
              '               <input type="radio" name="radio" id="' +
              orginDingdingPostion.deptId +
              '"><label for="' +
              orginDingdingPostion.deptId +
              '">主职位</label>' +
              '           </div>' +
              '           <button class="well-btn-sm well-btn w-btn-primary w-line-btn op_del">删除</button>' +
              '         </li>'
          );
          $orginDingdingPostion.find('input[type="radio"]').prop('checked', isChecked);

          if (!_self.readOnly) {
            $orginDingdingPostion
              .find('.op_del')
              .off('click')
              .on('click', function () {
                _self.deleteDingTalkPosition($orginDingdingPostion);
              });
          } else {
            $orginDingdingPostion.find('input[type="radio"]').prop('disabled', true);
            $orginDingdingPostion.find('.ding-position input').prop('disabled', true);
          }

          $ul.append($orginDingdingPostion);
        },

        /**
         * 删除钉钉职位
         */
        deleteDingTalkPosition: function ($dingdingPostion) {
          var _self = this;
          var $dingContent = this.$dialogContent.find('#ding_department .mul-dep__content-content');
          var $liArr = $dingContent.find('li');
          if ($liArr.length === 1) {
            $dingContent.html('<div class="well-search-no-match"></div>');
          } else {
            if ($dingdingPostion) {
              var index = $dingdingPostion.index(); // 删除那一行的索引
              _self.dingdingPositions.splice(index, 1);
              $dingdingPostion.remove();
            }
          }
        },

        // 生成oa职位dom结构
        buildOaPosition() {
          var $oaContent = this.$dialogContent.find('#oa_department .mul-dep__content-content');
          if (this.oaPositions.length === 0) {
            $oaContent.html('<div class="well-search-no-match"></div>');
          } else {
            $oaContent.html('');
            $oaContent.append('<ul></ul>');
            for (var i = 0; i < this.oaPositions.length; i++) {
              var oaPositions = this.oaPositions[i];
              this.addOaPosition(oaPositions);
            }
          }
        },

        /**
         * 添加OA职位
         */
        addOaPosition: function (oaPostion) {
          var _self = this;

          var oaPostionCopy = oaPostion ? oaPostion : { jobId: '', jobIdPath: '', jobName: '', jobNamePath: '', isMain: 0 };

          var uuid = uuidObject.createUUID();
          var isChecked = oaPostionCopy.isMain == 1 ? true : false;

          var $oaContent = this.$dialogContent.find('#oa_department .mul-dep__content-content');
          var $ul = $oaContent.find('ul');
          if ($ul.length <= 0) {
            $oaContent.html('');
            $oaContent.append('<ul></ul>');
            $ul = $oaContent.find('ul');
          }

          var $oaPostion = $(
            '         <li  class="well-form-control">' +
              '           <div class="well-input-group oa-position">' +
              '             <input type="text" style="flex:0 1 640px" value="' +
              oaPostionCopy.jobNamePath +
              '" class="input-icon" positionId="' +
              oaPostionCopy.jobId +
              '"/ >' +
              '           </div>' +
              '           <div>' +
              '               <input type="radio" name="radio" id="' +
              (oaPostionCopy.jobId ? oaPostionCopy.jobId : uuid) +
              '" ><label for="' +
              (oaPostionCopy.jobId ? oaPostionCopy.jobId : uuid) +
              '">主职位</label>' +
              '           </div>' +
              '           <button class="well-btn-sm well-btn w-btn-primary w-line-btn op_del">删除</button>' +
              '         </li>'
          );

          $oaPostion.find("input[type='radio']").prop('checked', isChecked);

          if (!oaPostion) {
            _self.oaPositions.push(oaPostionCopy);
          }

          if (!_self.readOnly) {
            $oaPostion
              .find('.oa-position')
              .off('click')
              .on('click', function () {
                var $oaPositionInput = $oaPostion.find('.oa-position input');
                $.unit2.open({
                  type: 'MyUnit',
                  title: '选择oa职位',
                  labelField: '',
                  valueField: '',
                  initValues: $oaPositionInput.attr('positionId'),
                  initLabels: $oaPositionInput.val(),
                  selectTypes: 'J',
                  multiple: false,
                  valueFormat: 'all',
                  nameFormat: 'all',
                  callback: function (values, labels, treeNodes) {
                    var jobId = '';
                    var jobIdPath = '';
                    var jobName = '';
                    var jobNamePath = '';
                    if (treeNodes && treeNodes.length > 0) {
                      var selectTreeNode = treeNodes[0];
                      var pathArr = selectTreeNode.getPath();
                      for (var i = 0; i < pathArr.length; i++) {
                        jobIdPath += pathArr[i].id + '/';
                        jobNamePath += pathArr[i].name + '/';
                      }
                      var last = pathArr[pathArr.length - 1];
                      jobId += pathArr[0].id + '/' + last.id;
                      jobIdPath = jobIdPath.substr(0, jobIdPath.length - 1);
                      jobName = last.name;
                      jobNamePath = jobNamePath.substr(0, jobNamePath.length - 1);
                    }

                    // console.log(jobId, jobIdPath, jobName, jobNamePath);

                    $oaPositionInput.attr('positionId', jobId);
                    $oaPositionInput.val(jobNamePath);

                    // 对象赋值
                    oaPostionCopy.jobId = jobId;
                    oaPostionCopy.jobIdPath = jobIdPath;
                    oaPostionCopy.jobName = jobName;
                    oaPostionCopy.jobNamePath = jobNamePath;

                    console.log('新增oa职位', _self.oaPositions);
                  },
                  close: function () {}
                });
              });
          } else {
            $oaPostion.find("input[type='radio']").prop('disabled', true);
            $oaPostion.find('.oa-position input').prop('disabled', true);
          }

          $oaPostion
            .find('.op_del')
            .off('click')
            .on('click', function () {
              _self.deleteOaPosition($oaPostion);
            });

          $ul.append($oaPostion);
        },

        /**
         * 删除OA职位
         */
        deleteOaPosition: function ($oaPostion) {
          var _self = this;
          var $oaContent = this.$dialogContent.find('#oa_department .mul-dep__content-content');
          var $liArr = $oaContent.find('li');
          if ($liArr.length === 1) {
            $oaContent.html('<div class="well-search-no-match"></div>');
          } else {
            if ($oaPostion) {
              var index = $oaPostion.index();
              _self.oaPositions.splice(index, 1);
              $oaPostion.remove();
            }
          }
        },

        /**
         * 获取弹出框数据
         */
        getDialogData: function () {
          var _self = this;
          var $dingContent = this.$dialogContent.find('#ding_department .mul-dep__content-content');
          var $dingLiArr = $dingContent.find('li');

          var dingArr = [];
          for (var i = 0; i < $dingLiArr.length; i++) {
            var dingLiArrElement = $dingLiArr[i];
            var $dingLiArrElement = $(dingLiArrElement);
            var deptDept = $dingLiArrElement.find('.ding-dept input').val();
            var deptId = _self.orginDingdingPostions[i].deptId;
            var deptPosition = $dingLiArrElement.find('.ding-position input').val();
            var radio = $dingLiArrElement.find('input[type="radio"]').prop('checked');

            // 赋值给数据
            if (_self.dingdingPositions[i]) {
              _self.dingdingPositions[i].deptId = deptId;
              _self.dingdingPositions[i].deptName = deptDept;
              _self.dingdingPositions[i].jobName = deptPosition;
              _self.dingdingPositions[i].isMain = radio == true ? 1 : 0;
            } else {
              _self.dingdingPositions.push({
                deptId: deptId,
                deptName: deptDept,
                jobName: deptPosition,
                isMain: radio == true ? 1 : 0
              });
            }

            dingArr.push({ deptDept, deptPosition, radio });
          }

          var $oaContent = this.$dialogContent.find('#oa_department .mul-dep__content-content');
          var $oaLiArr = $oaContent.find('li');

          var oaArr = [];
          for (var i = 0; i < $oaLiArr.length; i++) {
            var oaLiArrElement = $oaLiArr[i];
            var $oaLiArrElement = $(oaLiArrElement);
            var $oaPositionInput = $oaLiArrElement.find('.oa-position input');
            var positionId = $oaPositionInput.attr('positionId');
            var positionName = $oaPositionInput.val();
            var radio = $oaLiArrElement.find('input[type="radio"]').prop('checked');

            _self.oaPositions[i].isMain = radio ? 1 : 0;

            if (positionId) {
              oaArr.push({ positionId, positionName, radio });
            } else if (radio) {
              // 无职位 && 主职位 的 OA职位
              oaArr.push({ positionId, positionName, radio });
            }
          }

          return {
            dingArr,
            oaArr
          };
        },

        /**
         * 检验数据
         */
        validDialogData: function () {
          var dingPositionIsNotEmptyTip = '钉钉职位不能为空！';
          var mainPositionIsNotEmptyTip = '主职位不能为空！';
          var oaMainPositionIsNotEmptyTip = '设为主职位的OA职位不能为空！';

          var dialogData = this.getDialogData();
          var dingArr = dialogData.dingArr;
          var oaArr = dialogData.oaArr;
          var isEmpty = false;
          var errors = [];
          if (oaArr.length === 0) {
            // 空数据
            isEmpty = true;
          }
          var mainPositionEmpty = ''; // 设置为主职位的职位名称
          var mainPositionRadio = false; // 是否设置为主职位

          for (var i = 0; i < dingArr.length; i++) {
            var dingArrElement = dingArr[i];
            if (!dingArrElement.deptPosition) {
              if (errors.indexOf(dingPositionIsNotEmptyTip) < 0) {
                errors.push(dingPositionIsNotEmptyTip);
              }
            }
            if (dingArrElement.radio) {
              mainPositionEmpty = dingArrElement.deptPosition;
              mainPositionRadio = true;
            }
          }

          for (var i = 0; i < oaArr.length; i++) {
            var oaArrElement = oaArr[i];
            if (oaArrElement.radio) {
              mainPositionEmpty = oaArrElement.positionName;
              mainPositionRadio = true;

              if (!oaArrElement.positionName) {
                if (errors.indexOf(oaMainPositionIsNotEmptyTip) < 0) {
                  errors.push(oaMainPositionIsNotEmptyTip);
                }
              }
            }
          }
          if (!mainPositionEmpty) {
            if (errors.indexOf(oaMainPositionIsNotEmptyTip) < 0) {
              if (errors.indexOf(mainPositionIsNotEmptyTip) < 0) {
                errors.push(mainPositionIsNotEmptyTip);
              }
            } else {
              var oaMainPositionIsNotEmptyTipIndex = errors.indexOf(oaMainPositionIsNotEmptyTip);
              if (errors.indexOf(mainPositionIsNotEmptyTip) < 0 && !mainPositionRadio) {
                errors.splice(oaMainPositionIsNotEmptyTipIndex, 0, mainPositionIsNotEmptyTip);
              }
            }
          }

          return {
            isEmpty: isEmpty,
            errors: errors
          };
        },

        generateHtml: function () {
          return (
            '<div class="mul-dep-container">' +
            '   <p class="prompt">从钉钉职位和OA职位中，选择1个作为OA主职位，其他则作为OA其他职位</p>' +
            '   <div class="mul-dep__content" id="ding_department">' +
            '     <div class="mul-dep__content-header">' +
            '       <span>钉钉职位</span>' +
            '       <span style="cursor:pointer;color:#488cee;" id="op_callback"><i class="iconfont icon-oa-chexiao" style="margin-right:5px;"></i>恢复同步数据</span>' +
            '     </div>' +
            '     <div class="mul-dep__content-content">' +
            '       <div class="well-search-no-match"></div>' +
            '     </div>' +
            '   </div>' +
            '   <div class="mul-dep__content" id="oa_department">' +
            '     <div class="mul-dep__content-header">' +
            '       <span>OA职位</span>' +
            '       <span style="cursor:pointer;color:#488cee;" id="op_add"><i class="iconfont icon-ptkj-jiahao" style="margin-right:5px;"></i>添加</span>' +
            '     </div>' +
            '     <div class="mul-dep__content-content">' +
            '       <div class="well-search-no-match"></div>' +
            '     </div>' +
            '  </div>' +
            '</div>'
          );
        }
      };

      return opDialogOjb;
    }

    // generateHtml: function (type, data) {
    //   var _html =
    //     '<div class="mul-dep-container">' +
    //     '   <p class="prompt">从钉钉职位和OA职位中，选择1个作为OA主职位，其他则作为OA其他职位</p>' +
    //     '   <div class="mul-dep__content" id="ding_department">' +
    //     '     <div class="mul-dep__content-header">' +
    //     '       <span>钉钉职位</span>' +
    //     '       <span style="cursor:pointer;color:#488cee;" id="op_callback"><i class="iconfont icon-oa-chexiao" style="margin-right:5px;"></i>恢复同步数据</span>' +
    //     '     </div>' +
    //     '     <div class="mul-dep__content-content">' +
    //     '       <div class="well-search-no-match"></div>' +
    //     '     </div>' +
    //     '   </div>' +
    //     '   <div class="mul-dep__content" id="oa_department">' +
    //     '     <div class="mul-dep__content-header">' +
    //     '       <span>OA职位</span>' +
    //     '       <span style="cursor:pointer;color:#488cee;" id="op_add"><i class="iconfont icon-ptkj-jiahao" style="margin-right:5px;"></i>添加</span>' +
    //     '     </div>' +
    //     '     <div class="mul-dep__content-content">' +
    //     '       <ul>' +
    //     '         <li  class="well-form-control">' +
    //     '           <div class="well-input-group"><input type="text"style="flex:0 1 640px" value="会计" class="input-icon" +><i class="iconfont icon-ptkj-zuzhiguanli"></i></div></button><div ><input type="radio" name="radio" ><label for="radio">主职位</label></div><button class="btn btn-xs well-btn w-btn-primary w-line-btn op_del">删除</button>' +
    //     '         </li>' +
    //     '         <li  class="well-form-control">' +
    //     '           <div class="well-input-group"><input type="text"style="flex:0 1 640px" value="人事经理" class="input-icon" +><i class="iconfont icon-ptkj-zuzhiguanli"></i></div></button><div ><input type="radio" name="radio" ><label for="radio">主职位</label></div><button class="btn btn-xs well-btn w-btn-primary w-line-btn op_del">删除</button>' +
    //     '         </li>' +
    //     '       </ul>' +
    //     '     </div>' +
    //     '  </div>' +
    //     '</div>';
    //
    //   return _html;
    // }
  });
  return AppMuldepPersonAuditListDevelopment;
});
