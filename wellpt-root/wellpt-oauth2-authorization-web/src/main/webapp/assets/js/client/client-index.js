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
                    var searchFields = ['clientId', 'clientName'];
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
                _this.openDialog('新增应用', WEB_ROOT + "client/new");
            });

            $("#del").on('click', function () {
                if ($("#table").bootstrapTable('getSelections').length == 0) {
                    top.layer.msg('请选择需要删除的应用');
                    return false;
                }
                top.layer.confirm('确定要删除应用？', {
                    btn: ['确定', '取消'] //按钮
                }, function (index, layero) {
                    $("#table").deleteRows(WEB_ROOT + "client", function (res) {
                        if (res && res.code == 0) {
                            top.layer.close(index);
                        } else {
                            top.layer.msg('删除应用失败');
                        }
                    });
                }, function () {
                });


            });

            $("#table").on('click', ".js-edit", function () {
                _this.openDialog('编辑应用', WEB_ROOT + "client/" + $(this).attr('data-uuid') + "/edit");
            });


        },

        openDialog: function (title, content, isEdit) {
            top.layer.open({
                type: 2,
                title: title || '新增应用',
                shadeClose: true,
                shade: false,
                maxmin: true, //开启最大化最小化按钮
                area: ['893px', '600px'],
                content: content || WEB_ROOT + "client/new",
                btn: ['保存', '关闭'],
                yes: function (index, layero) {
                    var $submit = $($(layero).find('iframe')[0].contentWindow.document.body).find('#submit');
                    $submit.trigger('click');
                    $(layero).find('.layui-layer-title').text('编辑应用');
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
