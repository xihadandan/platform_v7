(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($) {
	var Printtemplate = window.Printtemplate = {
		langs : {
			"zh-CN" : "简体中文(中国)",
			"zh-TW" : "繁体中文(台湾地区)",
			"zh-HK" : "繁体中文(香港)",
			"en-HK" : "英语(香港)",
			"en-US" : "英语(美国)",
			"en-GB" : "英语(英国)",
			"en-WW" : "英语(全球)",
			"en-CA" : "英语(加拿大)",
			"en-AU" : "英语(澳大利亚)",
			"en-IE" : "英语(爱尔兰)",
			"en-FI" : "英语(芬兰)",
			"fi-FI" : "芬兰语(芬兰)",
			"en-DK" : "英语(丹麦)",
			"da-DK" : "丹麦语(丹麦)",
			"en-IL" : "英语(以色列)",
			"he-IL" : "希伯来语(以色列)",
			"en-ZA" : "英语(南非)",
			"en-IN" : "英语(印度)",
			"en-NO" : "英语(挪威)",
			"en-SG" : "英语(新加坡)",
			"en-NZ" : "英语(新西兰)",
			"en-ID" : "英语(印度尼西亚)",
			"en-PH" : "英语(菲律宾)",
			"en-TH" : "英语(泰国)",
			"en-MY" : "英语(马来西亚)",
			"en-XA" : "英语(阿拉伯)",
			"ko-KR" : "韩文(韩国)",
			"ja-JP" : "日语(日本)",
			"nl-NL" : "荷兰语(荷兰)",
			"nl-BE" : "荷兰语(比利时)",
			"pt-PT" : "葡萄牙语(葡萄牙)",
			"pt-BR" : "葡萄牙语(巴西)",
			"fr-FR" : "法语(法国)",
			"fr-LU" : "法语(卢森堡)",
			"fr-CH" : "法语(瑞士)",
			"fr-BE" : "法语(比利时)",
			"fr-CA" : "法语(加拿大)",
			"es-LA" : "西班牙语(拉丁美洲)",
			"es-ES" : "西班牙语(西班牙)",
			"es-AR" : "西班牙语(阿根廷)",
			"es-US" : "西班牙语(美国)",
			"es-MX" : "西班牙语(墨西哥)",
			"es-CO" : "西班牙语(哥伦比亚)",
			"es-PR" : "西班牙语(波多黎各)",
			"de-DE" : "德语(德国)",
			"de-AT" : "德语(奥地利)",
			"de-CH" : "德语(瑞士)",
			"ru-RU" : "俄语(俄罗斯)",
			"it-IT" : "意大利语(意大利)",
			"el-GR" : "希腊语(希腊)",
			"no-NO" : "挪威语(挪威)",
			"hu-HU" : "匈牙利语(匈牙利)",
			"tr-TR" : "土耳其语(土耳其)",
			"cs-CZ" : "捷克语(捷克共和国)",
			"sl-SL" : "斯洛文尼亚语",
			"pl-PL" : "波兰语(波兰)",
			"sv-SE" : "瑞典语(瑞典)",
			"es-CL" : "西班牙语(智利)"
		}
	};
	var print_word_type = "wordType";
	var print_html_type = "htmlType";
	var print_template_type = "";
	Printtemplate.showLangsDialog = function(options) {
		var printCallback = options.printCallback;
		var printtemplateId = options.printtemplateId;
		var printtemplateUuid = options.printtemplateUuid;
		var printtemplateLang = options.printtemplateLang || "";
		var templates = [], templatesMap = {};
		JDS.call({
			service : "printTemplateService.getPrintTempalteLangs",
			data : [ printtemplateId, printtemplateUuid, printtemplateLang ],
			async : false,
			success : function(result) {
				templates = result.data;
			}
		});
		if (templates.length <= 0) {
			return printCallback.call(this, printtemplateLang, null);
		} else if (templates.length === 1) {
			printtemplateLang = templates[0].uuid;
			return printCallback.call(this, printtemplateLang, templates[0]);
		}
		var html = "<select name=\"printTemplateLangs\" style=\"width:100%;\">";
		for (var i = 0; i < templates.length; i++) {
			var template = templates[i];
			templatesMap[template.uuid] = template;
			html += "<option ";
			html += "id=\"" + template.uuid + "\"";
			html += " value=\"" + template.lang + "\"";
			html += " title=\"" + (template.remark || template.lang) + "\">";
			html += Printtemplate.langs[template.lang];
			html += (template.remark ? "(" + template.remark + ")" : ""); // 备注
			html += "</option>";
		}
		html += "</select>"
		var dlg_selector = "printTemplateLangsDlgId";
		closeDialog(dlg_selector);
		var json = $.extend({
			title : "选择语言",
			resizable : false,
			width : 480,
			height : 320,
			content : html,
			onChangePrint : true,
			dialogId : dlg_selector,
			open : function() {
				if (json.onChangePrint === true) {
					$("select[name=printTemplateLangs]"/*, "#" + dlg_selector*/).one("change", function(event) {
						var selected = $("option:selected", this);
						printtemplateLang = selected.attr("id") || "";
						printCallback.call(this, printtemplateLang, templatesMap[printtemplateLang], event);
						closeDialog(dlg_selector);
					});
				}
			},
			buttons : {
				"确定" : function(event) {
					var selected = $("select[name=printTemplateLangs]>option:selected"/*, "#" + dlg_selector*/);
					printtemplateLang = selected.attr("id");
					printCallback.call(this, printtemplateLang, templatesMap[printtemplateLang], event);
					closeDialog(dlg_selector);
				},
				"取消" : function() {
					closeDialog(dlg_selector);
				}
			},
			defaultBtnName : "取消"
		}, options);
		showDialog(json);
	};
	Printtemplate.printDyform = function(printtemplateId, formUuid, dataUuid, printtemplateUuid, printtemplateLang) {
		var printUrl = ctx + "/basicdata/printtemplate/print?t=" + (new Date()).getTime();
		printUrl += "&printtemplateId=" + printtemplateId;
		printUrl += "&printtemplateUuid=" + printtemplateUuid;
		printUrl += "&formUuid=" + formUuid;
		printUrl += "&dataUuid=" + dataUuid;
		JDS.call({
			service : "printTemplateService.getPrintTemplateType",
			data : [ printtemplateId ],
			async : false,
			success : function(result) {
				print_template_type = result.data;
			}
		});
		if (print_template_type == print_html_type) {
			Printtemplate.showLangsDialog({
				printtemplateId : printtemplateId,
				printtemplateUuid : printtemplateUuid,
				printtemplateLang : printtemplateLang,
				printCallback : function(printtemplateLang, printtemplate) {
					window.open(printUrl + "&printtemplateLang=" + printtemplateLang);
				}
			});
		}
	}
	return Printtemplate;
}));
