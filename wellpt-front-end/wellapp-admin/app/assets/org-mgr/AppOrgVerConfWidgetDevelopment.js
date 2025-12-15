define(['constant', 'commons', 'server', 'appContext', 'appModal', 'wCommonComboTree', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  wCommonComboTree,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;

  var AppOrgVerConfWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgVerConfWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var _this = this;
      var orgVersionId = Browser.getQueryString('versionId');
      var systemUnitId = Browser.getQueryString('systemUnitId');
      var type = Browser.getQueryString('type');
      var uuid = Browser.getQueryString('uuid');

      $('#uuid').val(uuid);
      $('#type').val(type);

      var formBean = {
        uuid: null,
        eleId: null,
        name: null,
        shortName: null,
        code: null,
        sapCode: null,
        remark: null,
        type: null,
        parentEleIdPath: null,
        parentEleNamePath: null,
        orgVersionId: orgVersionId,
        bossIdPaths: null, // 负责人
        bossNames: null,
        branchLeaderIdPaths: null, // 分管领导
        branchLeaderNames: null,
        managerIdPaths: null, // 管理员
        managerNames: null,
        roleUuids: null, //节点角色
        systemUnitId: systemUnitId, //对应的归属系统ID
        dutyId: null, //归属职务
        dutyName: null //归属职务
      };
      $('#org_node_form').json2form(formBean);
      //检查父级节点路径不能嵌套
      jQuery.validator.addMethod('checkParentIdPath', function (value, element) {
        var uuid = $('#uuid').val();
        if (uuid) {
          // 有值代表修改操作
          var newParentIdPath = $('#parentEleIdPath').val();
          var oldEleIdPath = $('#parentEleIdPath').attr('eleIdPath');
          // 如果新路径是以旧路径开头，说明是新父级节点要么是本身要么是本身的子节点
          var reg = new RegExp('^' + oldEleIdPath);
          if (reg.test(newParentIdPath)) {
            return false;
          }
        }
        return true;
      });
      //检查父级节点路径不能是其他的系统单位
      jQuery.validator.addMethod('checkParentUnit', function (value, element) {
        var newParentIdPath = $('#parentEleIdPath').val();
        // 如果新路径是以旧路径开头，说明是新父级节点要么是本身要么是本身的子节点
        var reg = new RegExp('^' + orgVersionId);
        if (reg.test(newParentIdPath)) {
          //如果是第三方系统单位的组织版本被禁用的的，格式还没转换过来，所以判断是否还存在其他家组织版本
          //所以这里需要再进行一次判断
          if (newParentIdPath.indexOf('V', 1) > 0) {
            return false;
          }
          return true;
        }
        return false;
      });

      // 从服务端获取表单的校验规则
      var validator = $.common.validation.validate('#org_node_form', 'orgTreeNodeVo', function (options) {
        options.rules.parentEleNamePath = {
          required: true,
          checkParentUnit: true,
          checkParentIdPath: true
        };
        options.rules.typeName = {
          required: true
        };
        options.messages.parentEleNamePath = {
          required: '不能为空!',
          checkParentUnit: '父级节点不能是其他的系统单位。',
          checkParentIdPath: '父级节点不能是自己或自己的子节点。'
        };
        options.messages.typeName = {
          required: '不能为空!'
        };
        options.ignore = '';
      });

      initFormLayout();

      if (uuid) {
        showOrgNodeInfo(uuid);
      } else {
        $('#type').val(type);
        $('#typeName').val($.common.idPrev.getName(type));
        if (type == 'J') {
          showLayout('J');
        }
        if (type == 'V') {
          showLayout('V');
          initOtherSystemUnitTree($('#eleId').val(), false);
          $('#autoUpgrade_1').attr('checked', 'checked');
        }
      }

      // 定义保存按钮的事件
      $('#org_ver_conf_btn_save').click(function () {
        if (!validator.form()) {
          return false;
        }
        $('#org_node_form').form2json(formBean);
        //获取角色的值
        var roleUuids = [];
        $('#conf_roleUuids option').each(function () {
          roleUuids.push($(this).val());
        });
        formBean.roleUuids = roleUuids.join(';');
        // 如果是引入单位组织版本的话，需要计算params参数
        if (formBean.type == 'V') {
          var unitNode = $('#org_ver_conf_name').data('unitNode');
          formBean.params = {
            systemUnitId: unitNode.data.systemUnitId,
            rootVersionId: unitNode.data.rootVersionId,
            functionType: unitNode.data.functionType,
            autoUpgrade: $('#autoUpgrade_1').attr('checked') ? 1 : 0
          };
        }
        if (_this.isOpen == '1' && formBean.type == 'J' && formBean.dutyId == '') {
          appModal.error('归属职务不能为空！');
          return false;
        }

        //保存扩展属性信息
        var attrs = $('#org_element_attribute_list').bootstrapTable('getData');
        formBean.orgElementAttrs = [];
        var attrCodes = [];
        if (attrs.length) {
          for (var a = 0; a < attrs.length; a++) {
            if (attrCodes.indexOf(attrs[a].attrCode) != -1) {
              appModal.error('自定义属性的编码不能重复');
              return false;
            }
            formBean.orgElementAttrs.push({
              code: attrs[a].attrCode,
              name: attrs[a].attrName,
              attrValue: attrs[a].attrValue,
              attrType: attrs[a].attrType,
              seq: a + 1,
              attrDisplay: attrs[a].attrDisplay
            });
          }
        }

        if (formBean.uuid) {
          // 修改操作
          var otherVersion = $('#otherVersion').data('list');
          if (otherVersion) {
            //有被其他版本引用，则直接解版。不用再提示
            submitModify(formBean, true);
          } else {
            // 没有被其他版本引用，不需要解绑
            submitModify(formBean, false);
          }
        } else {
          // 新增操作
          $.ajax({
            type: 'POST',
            url: '/api/org/multi/addOrgChildNode',
            async: false,
            dataType: 'json',
            data: formBean,
            success: function (data) {
              appModal.success({
                message: '保存成功！',
                timer: '300',
                callback: function () {
                  appContext.getNavTabWidget().closeTab('org_ver_config_edit');
                }
              });
            }
          });
        }
      });

      var $elementAttrList = $('#org_element_attribute_list');
      $elementAttrList.bootstrapTable({
        data: [],
        pagination: false,
        striped: false,
        showColumns: false,
        width: 500,
        undefinedText: '',
        clickToSelect: true,
        columns: [
          {
            checkbox: true
          },
          {
            field: 'uuid',
            title: 'uuid',
            visible: false
          },
          {
            field: 'attrName',
            title: '属性名称',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline',
              validate: function (value) {
                if (StringUtils.isBlank(value)) {
                  return '请输入属性名称!';
                }
              }
            }
          },
          {
            field: 'attrCode',
            title: '属性编码',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline',
              validate: function (value) {
                if (StringUtils.isBlank(value)) {
                  return '请输入属性编码!';
                }
              }
            }
          },
          {
            field: 'attrType',
            title: '属性类型',
            visible: false
          },
          {
            field: 'attr_type_select',
            title: '属性类型',
            formatter: function (value, row, index) {
              var op = '<option value="text" ' + (row.attrType == 'text' ? 'selected' : '') + '>文本属性</option>';
              op += '<option value="org" ' + (row.attrType == 'org' ? 'selected' : '') + '>组织属性</option>';
              op += '<option value="dict" ' + (row.attrType == 'dict' ? 'selected' : '') + '>字典属性</option>';
              return (
                '<select rowid="' +
                index +
                '" id="' +
                index +
                '_attr_type_select' +
                '" class="attrTypeSelect full-width" >' +
                op +
                '</select>'
              );
            },
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入属性类型!';
              }
            }
          },
          {
            field: 'attrValue',
            title: '属性值',
            visible: false
          },
          {
            field: 'attrDisplay',
            title: '属性值',
            visible: false
          },
          {
            field: 'value_input',
            title: '属性值',
            formatter: function (value, row, index) {
              var $input = $('<input>', {
                type: 'text',
                id: index + '_value_input_label',
                class: 'full-width row_attr_label_input',
                value: row.attrDisplay,
                rowid: index,
                readonly: row.attrType != 'text'
              });
              var $inputV = $('<input>', {
                type: 'hidden',
                id: index + '_value_input',
                value: row.attrValue
              });
              return $input[0].outerHTML + $inputV[0].outerHTML;
            },
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入属性值!';
              }
            }
          }
        ],
        onClickCell: function (field, value, row, $element) {
          if (field == 'value_input') {
            $elementAttrList.on('change', '.row_attr_label_input', function () {
              var rowid = $(this).attr('rowid');
              var value = $(this).val();
              var $attrTypeSel = $('#' + rowid + '_attr_type_select');
              if ($attrTypeSel.val() == 'text') {
                row.attrValue = value;
                row.attrDisplay = value;
              }
            });
          } else if (field == 'attr_type_select') {
            $elementAttrList.on('change', '.attrTypeSelect', function (e) {
              var attrType = $(this).val();
              var rowid = $(this).attr('rowid');
              var rowData = row;
              if (attrType != rowData.attrType) {
                row.attrValue = '';
                row.attrDisplay = '';
                row.attrType = attrType;
                $('#' + rowid + '_value_input_label , #' + rowid + '_value_input').val('');
                $('#' + rowid + '_value_input_label , #' + rowid + '_value_input').prop('readonly', false);
              }
              $('#' + rowid + '_value_input_label').off('click');
              if (attrType == 'org') {
                $('#' + rowid + '_value_input_label , #' + rowid + '_value_input').prop('readonly', true);
                $('#' + rowid + '_value_input_label').on('click', function () {
                  //组织弹出框选择
                  $.unit2.open({
                    valueField: rowid + '_value_input',
                    labelField: rowid + '_value_input_label',
                    title: '选择组织节点',
                    type: 'all',
                    unitId: SpringSecurityUtils.getCurrentUserUnitId(),
                    valueFormat: 'justId',
                    callback: function (values) {
                      row.attrValue = values.join(';');
                      row.attrDisplay = $('#' + rowid + '_value_input_label').val();
                    }
                  });
                });
              } else if (attrType == 'dict') {
                $('#' + rowid + '_value_input_label , #' + rowid + '_value_input').prop('readonly', true);
                $('#' + rowid + '_value_input_label').on('click', function () {
                  var id = rowid + '_dict';
                  var $div = $('<div>').append(
                    $('<input>', {
                      type: 'text',
                      id: id,
                      class: 'full-width',
                      placeholder: '请选择数据字典',
                      value: $('#' + rowid + '_value_input').val()
                    })
                  );
                  appModal.dialog({
                    title: '选择字典数据',
                    size: 'middle',
                    message: $div,
                    buttons: {
                      confirm: {
                        label: '确定',
                        className: 'btn-primary',
                        callback: function () {
                          $('#' + rowid + '_value_input_label').val($('#' + id).val());
                          $('#' + rowid + '_value_input').val($('#' + id).data('v'));
                          row.attrValue = $('#' + id).data('v');
                          row.attrDisplay = $('#' + id).val();
                        }
                      },
                      cancel: {
                        label: '取消',
                        className: 'btn-default'
                      }
                    },
                    shown: function (e) {
                      //下拉框选择数据字典
                      $('#' + id).wCommonComboTree({
                        service: 'dataDictionaryService.getAllAsTree',
                        serviceParams: [-1],
                        multiSelect: true, // 是否多选
                        parentSelect: true, // 父节点选择有效，默认无效
                        expandRootNode: true, // 展开根结点
                        onAfterSetValue: function (event, self, value) {
                          $('#' + id).data('v', value);
                        }
                      });
                    }
                  });
                });
              }
            });
          }
        }
      });

      $('#btn_attr_add').click(function (e) {
        var uuid = $(this).data('uuid') != undefined ? $(this).data('uuid') + 1 : 1;
        var data = {
          uuid: uuid,
          attrType: 'text'
        };
        $(this).data('uuid', uuid);
        $('#org_element_attribute_list').bootstrapTable('insertRow', {
          index: 0,
          row: data
        });
      });

      $('#btn_attr_del').click(function (e) {
        var selections = $('#org_element_attribute_list').bootstrapTable('getSelections');
        var uuids = [];
        if (selections.length > 0) {
          for (var i = 0; i < selections.length; i++) {
            uuids.push(selections[i].uuid);
          }
          $('#org_element_attribute_list').bootstrapTable('remove', {
            field: 'uuid',
            values: uuids
          });
        } else {
          appModal.error('请选择记录！');
          return false;
        }
      });

      function submitModify(formBean, isUnbind) {
        $.ajax({
          type: 'PUT',
          url: '/api/org/multi/modifyOrgChildNode?isUnbind=' + (isUnbind ? 'true' : 'false'),
          async: false,
          dataType: 'json',
          contentType: 'application/json',
          data: JSON.stringify(formBean),
          success: function (data) {
            appModal.success({
              message: '保存成功！',
              timer: 300,
              callback: function () {
                // 父级节点发生变更
                if (formBean.type !== 'V' && $('#parentEleIdPath').attr('eleidpath') != formBean.parentEleIdPath) {
                  // 重新计算用户的工作信息
                  $.ajax({
                    url: ctx + '/proxy/api/org/user/recomputeUserWorkInfoByEleId',
                    type: 'post',
                    data: {
                      orgVersionId: formBean.orgVersionId,
                      eleId: formBean.eleId
                    },
                    success: function (result) {}
                  });
                }
                appContext.getNavTabWidget().closeTab('org_ver_config_edit');
              }
            });
          }
        });
      }

      // 职务序列开关时否开启
      $.ajax({
        type: 'get',
        url: ctx + '/api/org/duty/hierarchy/switch',
        dataType: 'json',
        data: {
          isEnable: ''
        },
        success: function (res) {
          if (res.code == 0) {
            _this.isOpen = res.data;
          }
        }
      });

      function initFormLayout() {
        //初始化上级组织树
        initParentNodeTree('parentEle');
        //初始化角色树
        $.common.ztree.initRoleTree('conf_role_tree', 'conf_roleUuids');

        $('#org_ver_conf_name').click(function () {
          var type = $('#type').val();
          if ('V' == type) {
            //系统单位
            $('#div_otherSystemUnitTree').show();
          }
        });

        $('#parentEleNamePath').addClass('modal-input');

        //初始化职务的数据
        // $("#dutyName").wSelect2({
        //   serviceName: "multiOrgService",
        //   queryMethod: "queryDutyListForSelect2",
        //   labelField: "dutyName",
        //   valueField: "dutyId",
        //   placeholder: "请选择",
        //   params: {
        //     systemUnitId: systemUnitId
        //   },
        //   multiple: false,
        //   remoteSearch: false,
        //   width: "100%",
        //   height: 250
        // });

        var setting = {
          check: {
            radioType: 'level',
            chkStyle: 'radio',
            enable: true
          },
          view: {
            autoCancelSelected: true,
            addDiyDom: function (treeId, treeNode) {
              var aObj = $('#' + treeNode.tId + '_a');
              if (treeNode.data && treeNode.data.childrenType == 'D' && treeNode.data.dutySeqCode) {
                aObj.parent().append("<span style='float:right;color:#ccc'>" + treeNode.data.dutySeqCode + '</span>');
              }
            }
          },
          async: {
            otherParam: {
              serviceName: 'orgDutySeqService',
              methodName: 'queryDutySelect',
              data: [' ']
            }
          },
          callback: {
            onNodeCreated: function (event, treeId, treeNode) {},
            onClick: null,
            beforeClick: function (treeId, treeNode) {}
          }
        };

        $('#dutyName')
          .comboTree({
            labelField: 'dutyName',
            valueField: 'dutyId',
            treeSetting: setting,
            width: 220,
            height: 220,
            multiple: true,
            autoInitValue: false,
            autoCheckByValue: true,
            labelBy: 'name',
            onTreeAsyncSuccess: function (e, treeId) {
              var ztree = $.fn.zTree.getZTreeObj(treeId);
              var nodes = ztree.getNodes();
              var requireRefresh = false;
              $.each(nodes, function (i, node) {
                if (!node.nocheck) {
                  if (node.data && node.data.childrenType == 'D') {
                  } else {
                    node.nocheck = true;
                    requireRefresh = true;
                  }
                }
              });
              if (requireRefresh) {
                ztree.refresh();
              }
            }
          })
          .trigger('change');
      }

      function initOtherSystemUnitTree(eleId, isAllVersion) {
        var $treeDiv = $('#div_otherSystemUnitTree');
        var setting = {
          callback: {
            onCheck: function (event, treeId, treeNode) {
              if (treeNode == null) {
                return;
              }
              $('#org_ver_conf_name').val(treeNode.name);
              $('#eleId').val(treeNode.id);
              $('#org_ver_conf_name').data('unitNode', treeNode);
              $('#code').val(treeNode.getParentNode().data.code);
              $('#remark').val(treeNode.getParentNode().data.remark);
              if (treeNode.checked) {
                $treeDiv.hide();
              }
              return true;
            }
          },
          check: {
            enable: true,
            chkStyle: 'radio',
            radioType: 'all'
          }
        };
        $.ajax({
          url: ctx + '/api/org/version/queryVersionTreeOfMySubUnit',
          type: 'get',
          data: {
            isAllVersion: isAllVersion
          },
          success: function (result) {
            var treeNodes = result.data;
            var zTree = $.fn.zTree.init($('#tree_otherSystemUnit'), setting, treeNodes);
            var nodes = zTree.getNodes();
            // 默认展开第一个节点
            if (nodes.length > 0) {
              var node = nodes[0];
              zTree.expandNode(node, true, false, false, true);
            }
            //设置选中的值
            if (eleId) {
              var unitNode = zTree.getNodeByParam('id', eleId);
              zTree.checkNode(unitNode, true, false, true);
            }
          }
        });

        $treeDiv.mousedown(function () {
          return false;
        });

        // 全收缩,收缩到根节点
        $('#otherSystemUnit_fold_all').click(function () {
          var zTree = $.fn.zTree.getZTreeObj('tree_otherSystemUnit');
          var rootNode = zTree.getNodes();
          for (var i = 0; i < rootNode.length; i++) {
            var childNode = rootNode[i];
            zTree.expandNode(childNode, false);
          }
        });
        // 全展开
        $('#otherSystemUnit_unfold_all').click(function () {
          $.fn.zTree.getZTreeObj('tree_otherSystemUnit').expandAll(true);
        });
      }

      function initParentNodeTree(treeName) {
        var $idPathEle = $('#' + treeName + 'IdPath');
        var $namePathEle = $('#' + treeName + 'NamePath');
        var $treeDiv = $('#div_' + treeName + 'Tree');
        var setting = {
          callback: {
            onClick: function (event, treeId, treeNode) {
              if (treeNode == null) {
                return;
              }
              $idPathEle.val(treeNode.data.eleIdPath);
              $namePathEle.val(treeNode.data.eleNamePath);
              $treeDiv.hide(200);
              //通过失去焦点来触发下验证器
              $namePathEle.blur();
              return true;
            }
          }
        };

        $.common.ztree.initOrgTree('#tree_' + treeName, setting, orgVersionId);

        // 当点击父节点输入框时展示下拉树
        $namePathEle.click(function () {
          $treeDiv.show();
        });

        $treeDiv.mousedown(function () {
          return false;
        });

        // 全收缩,收缩到根节点
        $('#' + treeName + '_fold_all').click(function () {
          var zTree = $.fn.zTree.getZTreeObj('tree_' + treeName);
          var rootNode = zTree.getNodes()[0];
          for (var i = 0; i < rootNode.children.length; i++) {
            var childNode = rootNode.children[i];
            zTree.expandNode(childNode, false);
          }
        });
        // 全展开
        $('#' + treeName + '_unfold_all').click(function () {
          $.fn.zTree.getZTreeObj('tree_' + treeName).expandAll(true);
        });
      }

      function showLayout(type) {
        // 先全部隐藏，后打开各自需要的
        $('#tr_config').hide();
        $('#tr_boss').hide();

        // $("#tr_branchDept").hide();
        $('#tr_manager').hide();
        $('#tr_sap').show();
        $('#org_ver_conf_name').removeAttr('readonly');
        $('#tr_duty').hide();
        $('#code').removeAttr('readonly');
        $('#remark').removeAttr('readonly');
        if ('J' == type) {
          //职位，只显示分管部门
          $('#tr_duty').show(); //显示职务

          // 职位和系统单位下的【管理员信息】页签删除
          $('#org_node_form .nav-tabs li a[href="#org-ver-conf-tabs-2"]').hide();
        } else if ('D' == type || 'B' == type || 'O' == type) {
          //部门，业务单位，组织，显示负责人和管理员
          $('#tr_boss').show();
          $('#tr_manager').show();
          $('#dutyName').val('').trigger('change'); //清空职务数据
        } else if ('V' == type) {
          //系统单位，什么都不显示，
          $('#tr_config').show();
          $('#tr_sap').hide();
          $('#org_ver_conf_name').attr('readonly', 'readonly');
          $('#code').attr('readonly', 'readonly');
          $('#remark').attr('readonly', 'readonly');

          // 职位和系统单位下的【管理员信息】页签删除
          $('#org_node_form .nav-tabs li a[href="#org-ver-conf-tabs-2"]').hide();
        }
      }

      // 添加负责人按钮
      $('#bossNames')
        .addClass('modal-input')
        .click(function () {
          var parentEleIdPath = $('#parentEleIdPath').val();
          if (parentEleIdPath) {
            $.unit2.open({
              valueField: 'bossIdPaths',
              labelField: 'bossNames',
              title: '选择负责人',
              type: 'MyUnit',
              multiple: false,
              selectTypes: 'J',
              isNeedUser: '0',
              unitId: systemUnitId,
              orgVersionId: orgVersionId,
              valueFormat: 'all'
            });
          } else {
            appModal.error('请先配置父级节点');
          }
        });

      // 添加管理员
      $('#managerNames')
        .addClass('modal-input')
        .click(function () {
          var parentEleIdPath = $('#parentEleIdPath').val();
          if (parentEleIdPath) {
            // 找到离的最近的单位ID
            $.unit2.open({
              valueField: 'managerIdPaths',
              labelField: 'managerNames',
              title: '选择管理员',
              type: 'MyUnit',
              multiple: false,
              selectTypes: 'U',
              unitId: systemUnitId,
              orgVersionId: orgVersionId,
              valueFormat: 'all'
            });
          } else {
            appModal.error('请先配置父级节点');
          }
        });

      // 添加分管领导
      $('#branchLeaderNames')
        .addClass('modal-input')
        .click(function () {
          var parentEleIdPath = $('#parentEleIdPath').val();
          if (parentEleIdPath) {
            // 找到离的最近的单位ID
            $.unit2.open({
              valueField: 'branchLeaderIdPaths',
              labelField: 'branchLeaderNames',
              title: '选择分管领导',
              type: 'MyUnit',
              multiple: true,
              selectTypes: 'J;U',
              unitId: systemUnitId,
              orgVersionId: orgVersionId,
              valueFormat: 'all'
            });
          } else {
            appModal.error('请先配置父级节点');
          }
        });

      // 根据组织UUID获取信息
      function showOrgNodeInfo(uuid) {
        $.ajax({
          url: ctx + '/api/org/multi/getOrgNodeByTreeUuid',
          type: 'get',
          data: {
            orgTreeUuid: uuid
          },
          success: function (result) {
            // 清空所有的错误提示
            formBean = result.data;
            formBean.typeName = $.common.idPrev.getName(formBean.type);
            if ('V' == formBean.type) {
              if (formBean.params.autoUpgrade == '1') {
                $('#autoUpgrade_1').attr('checked', 'checkecd');
              } else {
                $('#autoUpgrade_0').attr('checked', 'checkecd');
              }
              initOtherSystemUnitTree(formBean.eleId, true);
            }
            $('#org_node_form').json2form(formBean);
            $('#parentEleIdPath').attr('eleIdPath', formBean.eleIdPath);
            $('#otherVersion').data('list', formBean.otherVersion);

            setTimeout(function () {
              var $zTree1 = $.fn.zTree.getZTreeObj('tree_parentEle');
              var node = $zTree1.getNodeByParam('path', formBean.parentEleIdPath);
              if (node) {
                $zTree1.selectNode(node);
              }

              var $zTree = $.fn.zTree.getZTreeObj('conf_role_tree');
              $zTree.checkAllNodes(false);
              if (formBean.roleUuids) {
                var roles = formBean.roleUuids.split(';');
                for (var i = 0; i < roles.length; i++) {
                  var nodes = $zTree.getNodesByParam('id', roles[i]);
                  if (nodes.length > 0) {
                    $zTree.checkNode(nodes[0], true, false, true);
                  }
                }
              }
            }, 500);

            //显示对应的布局
            showLayout(formBean.type);
            $('#dutyName').comboTree('initValue', formBean.dutyId).trigger('change');
            $('#dutyName').comboTree('renderTreeLabel', {
              label: formBean.dutyName,
              value: formBean.dutyId
            });
            //显示角色信息
            $('#conf_roleUuids').empty();

            $('#org_element_attribute_list').bootstrapTable('removeAll');
            if (formBean.orgElementAttrs) {
              for (var i = 0; i < formBean.orgElementAttrs.length; i++) {
                $('#org_element_attribute_list').bootstrapTable('insertRow', {
                  index: 0,
                  row: {
                    uuid: formBean.orgElementAttrs[i].uuid,
                    attrType: formBean.orgElementAttrs[i].attrType,
                    attrValue: formBean.orgElementAttrs[i].attrValue,
                    attrName: formBean.orgElementAttrs[i].name,
                    attrCode: formBean.orgElementAttrs[i].code,
                    attrDisplay: formBean.orgElementAttrs[i].attrDisplay
                  }
                });

                $('.attrTypeSelect', '#org_element_attribute_list').trigger('change');
              }
            }
            showPrivilegeResultTree(formBean.eleId);
          }
        });
      }

      function showPrivilegeResultTree(eleId) {
        var setting = {};

        $.ajax({
          url: ctx + '/api/org/multi/getOrgNodePrivilegeResultTree',
          type: 'get',
          data: {
            eleId: eleId
          },
          success: function (result) {
            var zTree = $.fn.zTree.init($('#conf_privilege_result_tree'), setting, result.data);
            var nodes = zTree.getNodes();
            // 默认展开第一个节点
            if (nodes.length > 0) {
              var node = nodes[0];
              zTree.expandNode(node, true, false, false, true);
            }
          }
        });
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppOrgVerConfWidgetDevelopment;
});
