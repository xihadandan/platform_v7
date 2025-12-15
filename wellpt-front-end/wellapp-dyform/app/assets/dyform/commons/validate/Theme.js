var Theme = function () {

    var chartColors, validationRules = getValidationRules();

    // Black & Orange
    // chartColors = ["#FF9900", "#333", "#777", "#BBB", "#555", "#999",
    // "#CCC"];

    // Ocean Breeze
    chartColors = ['#94BA65', '#2B4E72', '#2790B0', '#777', '#555', '#999', '#bbb', '#ccc', '#eee'];

    // Fire Starter
    // chartColors = ['#750000', '#F90', '#777',
    // '#555','#002646','#999','#bbb','#ccc','#eee'];

    // Mean Green
    // chartColors = ['#5F9B43', '#DB7D1F', '#BA4139',
    // '#777','#555','#999','#bbb','#ccc','#eee'];

    return {
        init: init,
        chartColors: chartColors,
        validationRules: validationRules
    };

    function init() {

        enhancedAccordion();

        if ($.fn.lightbox) {
            $('.ui-lightbox').lightbox();
        }

        if ($.fn.cirque) {
            $('.ui-cirque').cirque({});
        }

        $('#wrapper').append('<div class="push"></div>');
    }

    function enhancedAccordion() {
        $('.accordion').on('show', function (e) {
            $(e.target).prev('.accordion-heading').parent().addClass('open');
        });

        $('.accordion').on('hide', function (e) {
            $(this).find('.accordion-toggle').not($(e.target)).parents('.accordion-group').removeClass('open');
        });

        $('.accordion').each(function () {
            $(this).find('.accordion-body.in').parent().addClass('open');
        });
    }

    function getValidationRules() {
        var custom = {
            focusCleanup: false,

            wrapper: 'div',
            errorElement: 'span',

            highlight: function (elementCtl) {
            	var element = elementCtl.get$InputElem();
            	if($(element).is("[type=file]")){
            		$(element).closest(".btn-primary").css("border-color", "#ff0000");
            	}else {
            		$(element).css("border-color", "#ff0000");
            	}
                // modify by wujx 20161101 begin
                if (v_checkable(element)) {
                    var name = $(element)[0].name;
                    element = $(element).parent()[0];
                } else if (v_isFile(element)) {
                	element = $(element).parents("span.value")[0];
                } else if (v_textarea(element)) {
                	element = $(element).next(".cke")[0] || element;
                }
                // console.log($(element).next(".Validform_checktip").size());
                if ($(element).parents("td").find(".Validform_checktip").size() > 0) {
                    if ($(element).not(":hidden")) {
                        $(element).parents("td").find(".Validform_checktip").show();
                    }
                    return;
                }
                $(element).after("<span class=\"Validform_checktip\"></span>");
                $(element).parents("td").find(".Validform_checktip").show();
                // modify by wujx 20161101 begin
            },
            unhighlight: function (element, errorClass, validClass) {
            	var $element = $(element);
            	if($element.is("[type=file]")){
            		$element.closest(".btn-primary").css("border-color", "");
                    $element.closest(".btn-image-uploader").css("border-color", "");
            	}else {
            		$element.css("border-color", "");
            	}
                if (typeof bubble != "undefined" && bubble != null) {// 删除气泡中的错误信息
                    bubble.removeErrorItem($element.attr("name"));
                }
               
                $element.parents("td").find(".Validform_checktip").hide();
            },

            // 对于统一上传控件的控件，第一个参数errorMsgElement的值为null
            success: function (errorMsgElement, validatedElement) {
                // modify by wujx 20161101 begin
//            	var validatedElement = validatedElementctl.get$InputElem()
                if (v_checkable(validatedElement)) {
                    var name = $(validatedElement)[0].name;
                    validatedElement = $(validatedElement).parent()[0];
                } else if (v_isFile(validatedElement)) {
                    validatedElement = $(validatedElement).parents("span.value")[0];
                } else if (v_textarea(validatedElement)) {
                    validatedElement = $(validatedElement).next(".cke")[0] || validatedElement;
                }
                $(validatedElement).attr("valid", "true");
                $(validatedElement).parents("td").find(".Validform_checktip").remove();
                // modify by wujx 20161101 end
            },
            errorPlacement: function (error, validatedElement) {
//            	var validatedElement = validatedElementctl.get$InputElem()
                var html = error.html();
                if (html.indexOf("><") != -1) {
                    return;
                }
                // modify by wujx 20161101 begin
                if (v_checkable(validatedElement)) {
                    var name = $(validatedElement)[0].name;
                    validatedElement = $(validatedElement).parent()[0];
                } else if (v_isFile(validatedElement)) {
                    validatedElement = $(validatedElement).parents("span.value")[0];
                } else if (v_textarea(validatedElement)) {
                    validatedElement = $(validatedElement).next(".cke")[0] || validatedElement;
                }
                $(validatedElement).attr("valid", "false");
                $(validatedElement).parents("td").find(".Validform_checktip").html(html);
                // modify by wujx 20161101 end
                if ($.browser.msie) {
                    $(".Validform_checktip .error").css("display", "block");
                }
                $(".Validform_checktip .error").attr("style", "display: block;position:relative;width:auto;").addClass("iconfont");
            }
        };

        return custom;
    }

}();

var v_checkable = function (element) {
    if (element == undefined || (typeof element == 'Array' && (element.length == 0 || $(element.size() == 0)))) {
        return false;
    }
    if ($(element)[0] == undefined) {
        return false;
    }
    if ((/radio|checkbox/i).test($(element)[0].type)) {
        return true;
    } else {
        return false;
    }
}

var v_isFile = function (element) {
    if (element == undefined || (typeof element == 'Array' && (element.length == 0 || $(element.size() == 0)))) {
        return false;
    }
    if ($(element)[0] == undefined) {
        return false;
    }
    if ((/file/i).test($(element)[0].type)) {
        return true;
    } else {
        return false;
    }
}

var v_textarea = function (element) {
    if (element == undefined || (typeof element == 'Array' && (element.length == 0 || $(element.size() == 0)))) {
        return false;
    }
    if ($(element).is("textarea")) {
        return true;
    } else {
        return false;
    }
}