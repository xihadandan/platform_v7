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
                        sortField: params.sort,
                        search_EQ_batchDataImportUuid: $("#batchDataImportUuid").val()
                    };
                    var status = $("#detailsStatusSelect").val();
                    if (status != '') {
                        searchParams.search_EQ_status = status;
                    }

                    return searchParams;
                }
            });
        },

        initDomcumentEvent: function () {
            var _this = this;
            $("#detailsStatusSelect").on('change', function () {
                $('#table').bootstrapTable('refresh');
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