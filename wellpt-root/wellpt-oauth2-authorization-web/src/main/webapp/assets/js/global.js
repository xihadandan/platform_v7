//获取表单下的数据
$.fn.getFormJson = function () {
    var json = {};
    var _form = $(this);
    _form.find(':input').each(function () {
        var input = $(this);
        if (input.is("input[type='radio']")) {
            json[input.attr('name')] = _form.find("input[type='radio'][name='" + input.attr('name') + "']:checked").val();
        } else if (input.is("input[type='checkbox']")) {
            var $checks = _form.find("input[type='checkbox'][name='" + input.attr('name') + "']:checked");
            var ck_values = [];
            $checks.each(function () {
                ck_values.push($(this).val());
            });
            json[input.attr('name')] = ck_values.join(',');
        } else {
            json[input.attr('name') || input.attr('id')] = input.val();
        }
    });

    return json;
};


$.fn.deleteRows = function (url, successCallback) {
    var _table = $(this);
    var selections = _table.bootstrapTable('getSelections');
    if (selections.length > 0) {
        var uids = [];
        for (var i = 0; i < selections.length; i++) {
            uids.push('items[]=' + selections[i].uuid);
        }
        $.ajax({
            url: url + "?" + uids.join("&"),
            dataType: 'json',
            type: 'DELETE',
            contentType: 'application/json',
            success: function (res) {
                _table.bootstrapTable('refresh');
                if ($.isFunction(successCallback)) {
                    successCallback(res);
                }
            },
            async: true
        });
    }
};


Date.prototype.format = function (fmt) { // author: meizz
    var o = {
        "M+": this.getMonth() + 1, // 月份
        "d+": this.getDate(), // 日
        "H+": this.getHours(), // 小时
        "m+": this.getMinutes(), // 分
        "s+": this.getSeconds(), // 秒
        "q+": Math.floor((this.getMonth() + 3) / 3), // 季度
        "S": this.getMilliseconds()
        // 毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k])
                .substr(("" + o[k]).length)));
    return fmt;
};