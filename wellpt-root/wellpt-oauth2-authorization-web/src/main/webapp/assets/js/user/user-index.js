(function ($) {


    var client = {

        /**
         * 表格初始化
         */
        initTable: function () {
            $('#table').bootstrapTable({
                queryParams: function (params) {
                    var searchParams = {
                        pageIndex: params.offset,
                        pageSize: params.limit,
                        sortOrder: params.order,
                        sortField: params.sort
                    };
                    var searchFields = ['accountNumber', 'userName'];
                    if (this.search && params.search !== '') {
                        //关键字搜索
                        var columns = this.columns[0];
                        //模糊匹配所有展示的字段
                        for (var i = 0; i < searchFields.length; i++) {
                            searchParams['search_LIKE_' + searchFields[i]] = params.search;
                        }

                    }
                    return searchParams;
                }
            });
        },

        initDomcumentEvent: function () {
            var _this = this;
            //新增
            $("#add").on('click', function () {
                _this.openDialog('新增账号', WEB_ROOT + "user/new");
            });

            $("#del").on('click', function () {
                if ($("#table").bootstrapTable('getSelections').length == 0) {
                    top.layer.msg('请选择需要删除的用户账号');
                    return false;
                }
                top.layer.confirm('确定要删除用户账号？', {
                    btn: ['确定', '取消'] //按钮
                }, function (index, layero) {
                    $("#table").deleteRows(WEB_ROOT + "user", function (res) {
                        if (res && res.code == 0) {
                            top.layer.close(index);
                        } else {
                            top.layer.msg('删除用户账号失败');
                        }
                    });
                }, function () {
                });


            });

            $("#table").on('click', ".js-edit", function () {
                _this.openDialog('编辑用户账号', WEB_ROOT + "user/" + $(this).attr('data-uuid') + "/edit");
            });


            $("#import").on('click', function () {
                top.layer.open({
                    type: 1,
                    title: '用户导入',
                    shadeClose: false,
                    shade: false,
                    maxmin: true, //开启最大化最小化按钮
                    area: ['300px', '200px'],
                    content: '<div class="layuimini-container"><div class="container-fluid layuimini-main">' +
                        '<form class="form-horizontal"  action="" method="post">' +
                        '<div class="form-group"><button type="button" class="layui-btn" id="upload"><i class="layui-icon"></i>上传文件</button>' +
                        '<a style=" margin-left: 10px;" href="' + WEB_ROOT + 'user/template/excel/download?filename=用户导入模板文件.xlsx">' +
                        '<i class="fa fa-file-excel-o"></i>下载模板</a></div>' +
                        '<div class="form-group" id="importResultTip"></div></form>' +
                        '</div></div>',
                    success: function (layero, index) {
                        top.layerUpload.render({
                            elem: '#upload'
                            , url: '/user/excel/upload'
                            , accept: 'file', exts: 'xlsx|xls'
                            , before: function (obj) {
                                top.layer.load();
                            }
                            , done: function (res) {
                                top.layer.closeAll('loading');
                                console.log(res);
                                if (res && res.code == 0) {
                                    $("#importResultTip", $(layero)).empty();
                                    $("#importResultTip", $(layero)).append(
                                        $("<span>", {
                                            "class": "label label-success clikc2showDetails",
                                            "status": 1, "buuid": res.data.batchImportUuid
                                        }).text('成功导入' + res.data.success + '条'),
                                        $("<span>", {
                                            "class": "label label-danger clikc2showDetails",
                                            "status": 0, "buuid": res.data.batchImportUuid
                                        }).text('失败导入' + res.data.fail + '条')
                                    )
                                    $('#table').bootstrapTable('refresh');
                                }

                            }
                        });
                        $(layero).on('click', '.clikc2showDetails', function () {
                            var checkTab = top.layuimini.checkTab('userBatchImportDetails');
                            if (!checkTab) {
                                top.layuimini.addTab('userBatchImportDetails', WEB_ROOT + "batchImportDetails?s=" + $(this).attr('status') + "&buuid=" + $(this).attr('buuid'), "用户导入情况", false);
                            }

                        });


                    },

                });
            });


        },

        openDialog: function (title, content, isEdit) {
            top.layer.open({
                type: 2,
                title: title || '新增用户账号',
                shadeClose: true,
                shade: false,
                maxmin: true, //开启最大化最小化按钮
                area: ['893px', '600px'],
                content: content || WEB_ROOT + "user/new",
                btn: ['保存', '关闭'],
                yes: function (index, layero) {
                    var $submit = $($(layero).find('iframe')[0].contentWindow.document.body).find('#submit');
                    $submit.trigger('click');
                    $(layero).find('.layui-layer-title').text('编辑用户账号');
                    $('#table').bootstrapTable('refresh');
                }, cancel: function (index, layero) {

                }
            });
        },


        init: function () {
            this.initTable();
            this.initDomcumentEvent();
        },
    };


    client.init();


})(jQuery);


function lineButtons(value, row) {
    return '<button class="layui-btn layui-btn-primary layui-btn-sm js-edit" data-uuid="' + row.uuid + '">编辑</button>'
}

function dateFormatter(value, row) {

}