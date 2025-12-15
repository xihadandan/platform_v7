<script type="text/javascript" src="${base}assets/3rd-libs/jquery/jquery-1.12.4.min.js"></script>
<script type="text/javascript" src="${base}assets/3rd-libs/jquery/jquery.form.min.js"></script>
<script type="text/javascript" src="${base}assets/3rd-libs/jquery/jquery.validate.min.js"></script>
<script type="text/javascript" src="${base}assets/3rd-libs/jquery/localization/messages_zh.min.js"></script>
<script type="text/javascript" src="${base}assets/3rd-libs/bootstrap-3.4.1-dist/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${base}assets/3rd-libs/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript"
        src="${base}assets/3rd-libs/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>

<script src="${base}assets/3rd-libs/layui-v2.5.4/layui.js" type="text/javascript"></script>
<script src="${base}assets/js/global.js" type="text/javascript"></script>

<!--[if lt IE 9]>
<script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
<script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->

<script src="${base}assets/js/lay-config.js" type="text/javascript"></script>
<script>
    layui.use(['element', 'layer', 'layuimini', 'jquery', 'upload'], function () {
        var element = layui.element,
                layer = layui.layer;
        layerUpload = layui.upload;
    });
    var headers = {};
    headers[_CSRF_HEADER] = _CSRF_TOKEN;
    $.ajaxSetup({
        headers: headers
    });

</script>