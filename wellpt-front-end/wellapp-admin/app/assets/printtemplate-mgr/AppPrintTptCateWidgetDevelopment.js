define(['constant', 'commons', 'server', 'HtmlWidgetDevelopment'], function (constant, commons, server, HtmlWidgetDevelopment) {
  var JDS = server.JDS;
  // 页面组件二开基础
  var AppPrintTptCateWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppPrintTptCateWidgetDevelopment, HtmlWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var $container = this.widget.element;
      var categroyTreeData = [];
      var editCateUuidList = [];
      getList();
      $('#addFlowCategory', $container)
        .off()
        .on('click', function () {
          openDialog('新建打印模板分类');
        });

      $('#searchFlowCategory', $container)
        .off()
        .on('click', function () {
          var val = $('#fullFlowName', $container).val();
          getList(val);
        });
      $('#fullFlowName', $container)
        .off()
        .on('keyup', function () {
          if ($(this).val() != '') {
            $('#deleteFlowCategory', $container).show();
          } else {
            $('#deleteFlowCategory', $container).hide();
          }
        })
        .on('keypress', function (e) {
          if (e.keyCode == 13) {
            getList($(this).val());
          }
        });

      $('#deleteFlowCategory', $container)
        .off()
        .on('click', function () {
          $('#fullFlowName', $container).val('');
          $(this).hide();
          getList();
        });

      $('#flow_category_content', $container)
        .off()
        .on('click', '.hasList', function () {
          if ($(this).hasClass('icon-ptkj-shixinjiantou-you')) {
            $(this).removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
            $('#flow_category_tree', $container).slideDown();
          } else {
            $(this).removeClass('icon-ptkj-shixinjiantou-xia').addClass('icon-ptkj-shixinjiantou-you');
            $('#flow_category_tree', $container).slideUp();
          }
        });

      $('.msg-category-folder', $container)
        .off()
        .on('click', function () {
          var width = $(this).parents('.panel-body').width();
          if ($(this).find('i').hasClass('icon-ptkj-youshouzhan')) {
            $(this)
              .parents('.panel-body')
              .find('.ui-wHtml')
              .animate(
                {
                  width: '230px'
                },
                300
              )
              .find('.msg-category-wrap')
              .show();
            $(this).parents('.msg-category-content').animate(
              {
                width: '220px'
              },
              300
            );
            $(this)
              .parents('.panel-body')
              .find('.ui-wBootstrapTable')
              .animate(
                {
                  width: width - 670 + 'px'
                },
                300
              );
            $(this)
              .parents('.panel-body')
              .find('.ui-wHtml-detail')
              .animate(
                {
                  width: 416 + 'px'
                },
                300
              );
            $(this).find('i').removeClass('icon-ptkj-youshouzhan').addClass('icon-ptkj-zuoshouzhan');
          } else {
            $(this)
              .closest('.ui-wHtml')
              .animate(
                {
                  width: '0'
                },
                300
              )
              .find('.msg-category-wrap')
              .hide();
            $(this).parents('.msg-category-content').animate(
              {
                width: '0'
              },
              300
            );
            $(this)
              .parents('.panel-body')
              .find('.ui-wBootstrapTable')
              .animate(
                {
                  width: width - 580 + 'px'
                },
                300
              );
            $(this)
              .parents('.panel-body')
              .find('.ui-wHtml-detail')
              .animate(
                {
                  width: 520 + 'px'
                },
                300
              );

            $(this).find('i').addClass('icon-ptkj-youshouzhan').removeClass('icon-ptkj-zuoshouzhan');
          }
        });

      $('#flowAllCategory', $container)
        .off('click')
        .on('click', function () {
          $('.msg-category-item', $container).removeClass('hasSelectCate');
          var msgFormateTable = $('#flow_category_content', $container).parents('.panel-body').find('.ui-wBootstrapTable').attr('id');
          $('#' + msgFormateTable).wBootstrapTable('removeParam', 'categoryUuid');
          $('#' + msgFormateTable).wBootstrapTable('refresh');
        });

      $('#flow_category_tree', $container)
        .off()
        .on('click', '.msg-category-item', function (e) {
          e.stopPropagation();
          $(this).addClass('hasSelectCate').siblings().removeClass('hasSelectCate');
          var uuid = $(this).data('uuid');
          var msgFormateTable = $('#flow_category_content', $container).parents('.panel-body').find('.ui-wBootstrapTable').attr('id');

          $('#' + msgFormateTable)
            .wBootstrapTable('addParam', 'categoryUuid', uuid)
            .wBootstrapTable('refresh');
        });

      function getList(val) {
        var _self = this;
        JDS.restfulPost({
          url: ctx + '/api/printTemplate/category/getTreeAllBySystemUnitIdsLikeName',
          data: {
            name: val || ''
          },
          success: function (result) {
            if (result.code == 0) {
              var data = result.data;
              var lis = '';
              lis += buildHtml(data, -1);
              if (data.length > 0) {
                categroyTreeData = data;
                if ($('.icon-folders', $container).hasClass('icon-ptkj-shixinjiantou-you')) {
                  $('.icon-folders').addClass('hasList icon-ptkj-shixinjiantou-xia').removeClass('icon-ptkj-shixinjiantou-you');
                  $('#flow_category_tree', $container).slideDown();
                }
              }

              $('#flow_category_tree', $container).html(lis);

              $('.msg-category-item i.icon-folders', $container)
                .off()
                .on('click', function (e) {
                  e.stopPropagation();
                  var thisUUid = $(this).closest('li').data('uuid');
                  var oo = getEditCategroy(thisUUid, data);
                  var editCateUuidList = getSelectUuid(oo);
                  var isShowChildren = true;
                  if ($(this).hasClass('icon-ptkj-shixinjiantou-xia')) {
                    $(this).removeClass('icon-ptkj-shixinjiantou-xia').addClass('icon-ptkj-shixinjiantou-you');
                    isShowChildren = false;
                  } else {
                    $(this).removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
                    isShowChildren = true;
                  }

                  for (var i = 1; i < editCateUuidList.length; i++) {
                    isShowChildren
                      ? $('.msg-category-item[data-uuid=' + editCateUuidList[i] + ']', $container).show()
                      : $('.msg-category-item[data-uuid=' + editCateUuidList[i] + ']', $container).hide();
                    isShowChildren
                      ? $('.msg-category-item[data-uuid=' + editCateUuidList[i] + ']', $container)
                          .find('.hasChild')
                          .removeClass('icon-ptkj-shixinjiantou-you')
                          .addClass('icon-ptkj-shixinjiantou-xia')
                      : $('.msg-category-item[data-uuid=' + editCateUuidList[i] + ']', $container)
                          .find('.hasChild')
                          .removeClass('icon-ptkj-shixinjiantou-xia')
                          .addClass('icon-ptkj-shixinjiantou-you');
                  }
                });
              setTimeout(function () {
                resetSelectedItem();
              }, 300);
            }
          }
        });
      }
      function buildHtml(serviceData, count) {
        var html = '';
        count++;
        for (var i = 0; i < serviceData.length; i++) {
          var data = serviceData[i].data;
          var icon = data.icon ? data.icon : 'iconfont icon-ptkj-fenlei2';
          var background = data.iconColor ? data.iconColor : '#64B3EA';
          var remark = data.remark || '';
          var title = remark;
          var className = 'msg-category-item-' + count;
          // if (data.name.length > 9 && remark != '') {
          //   title = data.name + '\n' + remark;
          // } else if (data.name.length <= 9 && remark != '') {
          //   title = remark;
          // } else if (data.name.length > 9 && remark == '') {
          //   title = data.name;
          // }
          var $li = '';
          if (serviceData[i].children.length > 0) {
            $li =
              "<li class='msg-category-item " +
              className +
              "' data-code='" +
              data.code +
              "' data-uuid='" +
              data.uuid +
              "' title='" +
              title +
              "'>" +
              "<i class='iconfont icon-folders icon-ptkj-shixinjiantou-xia hasChild' style='font-size:14px;margin-right:5px;color:#999;'></i>" +
              "<span class='" +
              icon +
              "' style='background:" +
              background +
              "'></span>" +
              "<span class='msg-category-text'>" +
              data.name +
              '</span>' +
              "<i class='iconfont icon-ptkj-gengduocaozuo icon-operate'></i>" +
              "<ul class='category-item-operate'>" +
              "<li class='btn-edit'><i class='iconfont icon-ptkj-bianji'></i>编辑</li>" +
              "<li class='btn-delete'><i class='iconfont icon-ptkj-shanchu'></i>删除</li>" +
              '</ul>' +
              '<ul class="category-item-children"></ul>' +
              '</li>';
          } else {
            $li =
              "<li class='msg-category-item " +
              className +
              "' data-code='" +
              data.code +
              "' data-uuid='" +
              data.uuid +
              "' title='" +
              title +
              "'>" +
              "<span class='" +
              icon +
              "' style='background:" +
              background +
              ";margin-left:19px'>" +
              '</span>' +
              "<span class='msg-category-text'>" +
              data.name +
              '</span>' +
              "<i class='iconfont icon-ptkj-gengduocaozuo icon-operate'></i>" +
              "<ul class='category-item-operate'>" +
              "<li class='btn-edit'><i class='iconfont icon-ptkj-bianji'></i>编辑</li>" +
              "<li class='btn-delete'><i class='iconfont icon-ptkj-shanchu'></i>删除</li>" +
              '</ul>' +
              '<ul class="category-item-children"></ul>' +
              '</li>';
          }

          html += $li;
          if (serviceData[i].children && serviceData[i].children.length > 0) {
            html += buildHtml(serviceData[i].children, count);
          }
        }

        return html;
      }
      function resetSelectedItem() {
        var msgFormateTable = $('#flow_category_content', $container).parents('.panel-tab-content').find('.ui-wBootstrapTable').attr('id');
        var $table = $('#' + msgFormateTable);
        if ($table.length) {
          var selectedCategoryUuid = $table.wBootstrapTable('getParam', 'categoryUuid');
          if (typeof selectedCategoryUuid === 'string') {
            $('#flow_category_content .msg-category-item[data-uuid="' + selectedCategoryUuid + '"]', $container).addClass('hasSelectCate');
          }
        }
      }

      function getDetail(bean, uuid, $dialog) {
        JDS.restfulGet({
          url: ctx + '/api/printTemplate/category/get',
          data: {
            uuid: uuid
          },
          async: false,
          contentType: 'application/x-www-form-urlencoded',
          success: function (result) {
            if (result.success || result.code == 0) {
              bean = result.data;
              $('#messageForm', $dialog).json2form(bean);
              $('#msgIconShow', $dialog).addClass(bean.icon).css({
                background: bean.iconColor
              });
              $('#selectIconBg', $dialog).find('div').attr('data-color', bean.iconColor).css({
                background: bean.iconColor
              });
            }
          }
        });
      }

      // 得到编辑的分类和他的子集
      function getEditCategroy(uuid, allData) {
        var obj = {};
        function get(uuid, allData) {
          for (var i = 0; i < allData.length; i++) {
            if (allData[i].data.uuid == uuid) {
              obj = allData[i];
              break;
            } else if (allData[i].children.length > 0) {
              get(uuid, allData[i].children);
            }
          }
        }
        get(uuid, allData);
        return obj;
      }
      function getSelectUuid(object) {
        var arr = [];
        function pushUUid(object) {
          arr.push(object.data.uuid);
          if (object.children.length > 0) {
            for (var i = 0; i < object.children.length; i++) {
              arr.push(object.children[i].data.uuid);
              if (object.children[i].children.length > 0) {
                pushUUid(object.children[i]);
              }
            }
          }
        }
        pushUUid(object);
        return arr;
      }

      function openDialog(title, uuid) {
        var _self = this;
        var bean = {
          uuid: uuid || '',
          name: '',
          icon: '',
          iconColor: '',
          code: '',
          remark: '',
          parentUuid: ''
        };
        var $dialog = null;
        var message = getDialogContent();
        var options = {
          title: title,
          message: message,
          size: 'middle',
          zIndex: 100,
          buttons: {
            ok: {
              label: '保存',
              className: 'well-btn w-btn-primary',
              callback: function () {
                $('#messageForm', $dialog).form2json(bean);
                console.log(editCateUuidList);
                if (bean.name == '') {
                  appModal.error('分类名称不能为空！');
                  return false;
                }

                if (bean.code == '') {
                  appModal.error('编号不能为空！');
                  return false;
                }

                if (bean.parentUuid && editCateUuidList.toString().indexOf(bean.parentUuid) > -1) {
                  appModal.error('父级分类不能选择当前类型或其子类型！');
                  return false;
                }

                JDS.restfulPost({
                  url: ctx + '/api/printTemplate/category/save',
                  data: bean,
                  success: function (result) {
                    if (result.success || result.code == 0) {
                      appModal.success('保存成功');
                      getList();
                    } else {
                      appModal.error(result.msg);
                    }
                  },
                  error: function (err) {
                    appModal.error({
                      message: err.responseJSON.msg,
                      timer: 3000
                    });
                  }
                });
              }
            },
            cancel: {
              label: '关闭',
              className: 'well-btn btn-default'
            }
          },
          shown: function () {
            editCateUuidList = [];
            if (uuid) {
              var oo = getEditCategroy(uuid, categroyTreeData);
              editCateUuidList = getSelectUuid(oo);
              getDetail(bean, uuid, $dialog);
            }

            // 选择图标
            $('#addIcon', $dialog)
              .off()
              .on('click', function () {
                $.WCommonPictureLib.show({
                  selectTypes: [3],
                  confirm: function (data) {
                    var fileIDs = data.fileIDs;
                    $('#icon', $dialog).val(fileIDs);
                    $('#msgIconShow', $dialog).attr('iconClass', fileIDs);
                    $('#msgIconShow', $dialog).attr('class', fileIDs);
                  }
                });
              });

            var items = initBgColor();
            $('.bg-choose-box', $dialog).append(items);

            // 色块选择
            $('.bg-choose-item', $dialog.find('.icon-bg-wrap'))
              .off()
              .on('click', function () {
                var color = $(this).data('color');
                $(this).addClass('hasChoose').siblings().removeClass('hasChoose');
                $('#iconColor', $dialog).val(color);
                $('#selectIconBg', $dialog).find('div').data('color', color).css('background', color);
                $('#msgIconShow', $dialog).css({
                  background: color
                });
              });
            // 关闭色块
            $(document).on('click', function (e) {
              if (!$(e.target).hasClass('icon-bg-wrap') && !$(e.target).parents().hasClass('icon-bg-wrap')) {
                $('.bg-choose-list').hide();
                $('.minicolors').hide();
              }
            });
            // 显示色块
            $('#selectIconBg', $dialog)
              .off()
              .on('click', function () {
                var currColor = $(this).find('div').data('color');
                $('.bg-choose-list', $dialog).css('display', 'inline-block');
                var chooseItem = $('.bg-choose-list', $dialog).find('.bg-choose-item');
                chooseItem.removeClass('hasChoose');
                $.each(chooseItem, function (index, item) {
                  if ($(item).data('color') == currColor) {
                    $(item).addClass('hasChoose');
                  }
                });
              });
            // 更多
            $('.bg-choose-more', $dialog.find('.icon-bg-wrap'))
              .off()
              .on('click', function () {
                $('.bg-choose-list', $dialog).hide();
                if ($('.minicolors', $dialog).size() > 0) {
                  $('.minicolors', $dialog).show();
                } else {
                  var opacity = false;
                  $('#iconColor', $dialog).minicolors({
                    control: 'hue',
                    format: 'hex',
                    color: '#0070C0',
                    letterCase: 'lowercase',
                    opacity: opacity,
                    position: 'bottom left',
                    theme: 'bootstrap',
                    change: function (value, opacity) {
                      $('#iconColor', $dialog).focus();
                      $('#selectIconBg', $dialog).find('div').data('color', value).css('background', value);
                      $('#msgIconShow', $dialog).css({
                        background: value
                      });
                    },
                    hide: function () {
                      $('#iconColor', $dialog).hide();
                      $('.icon-bg-wrap', $dialog).find('.minicolors').hide();
                    },
                    show: function () {}
                  });
                  $('#iconColor', $dialog).focus();
                  if (!opacity) {
                    $('.minicolors-input-swatch', $dialog).hide();
                  }
                }
              });

            var setting = {
              view: {
                showLine: true
              },
              check: {
                enable: false
                // chkStyle: 'radio'
              },
              async: {
                otherParam: {
                  serviceName: 'printTemplateCategoryService',
                  methodName: 'getPrintTemplateCategoryTree',
                  data: ['']
                }
              },
              callback: {
                onAsyncSuccess: function (event, treeId, treeNode, msg) {
                  var zTree = $.fn.zTree.getZTreeObj('parentUuid_ztree');
                  zTree.expandAll(true);
                }
              }
            };

            $('#parentUuid').comboTree({
              labelField: 'parentName',
              valueField: 'parentUuid',
              treeSetting: setting,
              width: 220,
              height: 220,
              multiple: false,
              selectParent: true,
              autoInitValue: false,
              autoCheckByValue: true
            });

            // 默认全部展开
            // setTimeout(function(){
            //   debugger
            //   var zTree = $.fn.zTree.getZTreeObj('parentUuid_ztree');
            //   zTree.expandAll(true);
            // },1000)
          }
        };
        $dialog = appModal.dialog(options);
      }

      function getDialogContent() {
        var html = '';
        html +=
          "<form id='messageForm' class='dyform'>" +
          "<table class='well-form form-horizontal'>" +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'><font style='color:#f00;'>*</font>分类名称</td>" +
          "<td><input type='text' name='name' id='name' class='form-control' placeholder='分类名称'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>图标</td>" +
          '<td>' +
          "<input type='hidden' name='icon' id='icon'/>" +
          "<span id='msgIconShow' style='margin-right: 5px;' style='background: transparent'></span>" +
          "<button id='addIcon' type='button' class='well-btn w-btn-primary' style='vertical-align: top;'>选择图标</button>" +
          "<div class='icon-bg-wrap'>" +
          "<div class='currBg' id='selectIconBg' data-color=''><div></div></div>" +
          "<div class='bg-choose-list'>" +
          "<ul class='bg-choose-box'></ul>" +
          "<div class='bg-choose-more'>更多<i></i></div>" +
          '</div>' +
          "<input type='hidden' name='iconColor' id='iconColor'/>" +
          '</div>' +
          '</td>' +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'><font style='color:#f00;'>*</font>编号</td>" +
          "<td><input type='text' name='code' id='code' class='form-control' placeholder='编号'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>父级分类</td>" +
          '<td>' +
          '<input type="hidden" name="parentUuid" id="parentUuid" class="form-control"/>' +
          '<input type="hidden" name="parentName" id="parentName" class="form-control"/>' +
          '</td>' +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>描述</td>" +
          "<td><textarea name='remark' id='remark' cols='30' rows='5' class='form-control'></textarea></td>" +
          '</tr>' +
          '</table>' +
          '</form>';

        return html;
      }

      function initBgColor() {
        var colors = [
          '#64B3EA',
          '#FCB862',
          '#92D678',
          '#F38F8A',
          '#9584EE',
          '#44C8E5',
          '#E28DE9',
          '#65DEAA',
          '#FE88A7',
          '#77A8EE',
          '#D9D688',
          '#F6ABBB'
        ];
        var items = '';
        // 渲染背景颜色
        $.each(colors, function (index, item) {
          items +=
            '<li class="bg-choose-item" data-color="' +
            item +
            '" style="background: ' +
            item +
            '"><i class="iconfont icon-ptkj-dagou"></i></li>';
        });

        return items;
      }

      $('#flow_category_tree', $container).on('click', '.btn-edit', function (e) {
        e.stopPropagation();
        var uuid = $(this).parents('.msg-category-item').data('uuid');
        openDialog('编辑打印模板分类', uuid);
      });

      $('#flow_category_tree', $container).on('click', '.btn-delete', function (e) {
        e.stopPropagation();
        var $el = $(this).parents('.msg-category-item');
        var uuid = $el.data('uuid');
        appModal.confirm('确定删除分类吗', function (success) {
          if (success) {
            JDS.restfulPost({
              url: ctx + '/api/printTemplate/category/deleteWhenNotUsed',
              data: {
                uuid: uuid
              },
              contentType: 'application/x-www-form-urlencoded',
              success: function (result) {
                if (result.success || result.code == 0) {
                  if (result.data === 1 || result.data === 0) {
                    appModal.success('删除成功');
                    $el.remove();
                    $('#flowAllCategory', $container).trigger('click');
                    getList();
                  } else if (result.data === -1) {
                    appModal.error('打印模板分类下存在模板，无法删除！');
                  }
                } else {
                  appModal.error('系统异常');
                }
              }
            });
          }
        });
      });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppPrintTptCateWidgetDevelopment;
});
