require([ "jquery", "design_commons", "config_component" ], function($, DesignUtils, component) {
	$(document).ready(function() {
		component.restoreData();

		var contenthandle = null
		require([ "ckeditor" ], function() {
			// 清除编辑器
			var instance = CKEDITOR.instances['contenteditor'];
			if (instance) {
				CKEDITOR.remove(instance);
			}
			CKEDITOR.disableAutoInline = true;
			contenthandle = CKEDITOR.replace('contenteditor', {
				language : 'zh-cn',
				customConfig : ctx + "/resources/pt/js/app/design/app_ckeditor.js",
				contentsCss : [ ctx + '/resources/bootstrap/3.3.7/css/bootstrap.css' ],
				allowedContent : true
			});
		});

		$("#estRows>.lyrow").draggable({
			connectToSortable : ".zone",
			helper : "clone",
			handle : ".drag",
			revert : "invalid",
			start : function(e, t) {
				if (!component.startdrag) {
					component.stopsave++;
				}
				component.startdrag = 1;
			},
			drag : function(e, t) {
				t.helper.width("400").height("125");
				// debugger;
				updateStateBar("拖动 : x[" + t.position["left"] + "]y[" + t.position["top"] + "]")
			},
			stop : function(e, t) {
				t.helper.width("100%").height("100%");
				updateStateBar();
				component.handleJsIds();
				$(".zone .column").sortable({
					opacity : .35,
					connectWith : ".column:not(:hidden)",
					start : function(e, t) {
						if (!component.startdrag) {
							component.stopsave++;
						}
						component.startdrag = 1;
					},
					sort : function(event, ui) {
						$(ui.item[0]).width("400").height("125");
						updateStateBar("移动 : x[" + ui.position["left"] + "]y[" + ui.position["top"] + "]")
					},
					stop : function(e, t) {
						updateStateBar();
						if (component.stopsave > 0) {
							component.stopsave--;
						}
						component.startdrag = 0;
					},
					receive : function(event, ui) {
						$(ui.item[0]).width("100%").height("100%");
					}
				});
				var layoutContainer;
				if ((layoutContainer = t.helper.find(".ui-layout-container")).length > 0) {
					layoutContainer.layout(

					);
				}
				if (component.stopsave > 0) {
					component.stopsave--;
				}
				component.startdrag = 0
			}
		});
		$("#elmBase>.box, #elmComponents>.box, #elmJS>.box, #elmWell>.box").draggable({
			connectToSortable : ".column:not(:hidden)",
			helper : "clone",
			handle : ".drag",
			revert : "invalid",
			start : function(e, t) {
				if (!component.startdrag) {
					component.stopsave++;
				}
				component.startdrag = 1;
			},
			drag : function(e, t) {
				t.helper.width("400").height("125");
				// debugger;
				updateStateBar("拖动 : x[" + t.position["left"] + "]y[" + t.position["top"] + "]")
			},
			stop : function(e, t) {
				t.helper.width("100%").height("100%");
				updateStateBar();
				component.handleJsIds();
				if (component.stopsave > 0) {
					component.stopsave--;
				}
				component.startdrag = 0;
			}
		});
		component.initContainer();

		// 编辑
		$('.edit .zone').on("click", "[data-target=#editorModal]", function(e) {
			e.preventDefault();
			// 清空配置函数
			window.beforeInitConfig = null;
			window.afterInitConfig = null;
			window.beforeSaveConfig = null;
			window.afterSaveConfig = null;
			// ckeditor
			currenteditor = $(this).parent().parent().find('.view');
			// custom
			var dataUrl = $(this).attr("data-src");
			var firstTab = $('#editorModal a[data-toggle=tab]:first');
			var $target = $(firstTab.attr("href")); // activated
			// 清空第一格页签
			$target.empty().off('execed.bs.tab').on('execed.bs.tab', function(e) {
				// debugger
				// e.preventDefault();
				var $holder = currenteditor.children(":first");// 
				if ($holder && $holder.length <= 0) {
					// 没有可用的占位符,添加一个
					$holder = $("<div class='box-holder'> " + currenteditor.html() + " </div>");
					currenteditor.empty().append($holder);
				}
				var bean = $holder.data("json") || {};
				// gid全局唯一ID,系统控制;fid字段ID,用户控制
				var gid = bean.gid || $.gguid(); // 生成唯一id
				$holder.attr("data-guid", gid);// GUID不可修改

				// @Deprecated start
				bean["id"] = bean["id"] || gid;// 未设置ID,设置默认ID为gid,用户可自行修改
				// bean["name"] = bean["name"];// 未设置NAME,设置默认NAME为fid,用户可自行修改
				// @Deprecated end
				var eText = currenteditor.html();
				// debugger;
				if (typeof window.beforeInitConfig === "function" && window.beforeInitConfig.call(currenteditor, bean, eText) === false) {
					return false;
				}
				
				// text html
				contenthandle.setData(eText);
				// json data
				DesignUtils.fillConfigurerData(bean, $("#editorModal"));
				if (typeof window.afterInitConfig === "function" && window.afterInitConfig.call(currenteditor, bean, eText)) {

				}
			}).off('errced.bs.tab').on('errced.bs.tab', function(e) {
				// console.log("errced.bs.tab");
			});
			if (dataUrl) {
				$target.wLoad(dataUrl, function(responseText, status, jqXHR) {
					// console.log("loaded.bs.tab");
					if (jqXHR && jqXHR.status >= 400) {
						$(this).html(responseText);
					}
					var e = $.Event('loaded.bs.tab', [ responseText, status, jqXHR ]);
					$target.trigger(e);
				});
				firstTab.show().tab('show')// 显示第1 标签页
			} else {
				$target.trigger('execed.bs.tab');
				firstTab.hide().parent("li").next().children("a[data-toggle=tab]").tab('show');// 隐藏第1
				// 标签页,显示第2
				// 标签页
			}
		});

		$("#savecontent").click(function(e) {
			// debugger;
			e.preventDefault();
			var $holder = currenteditor.children(":first");// .find(".box-holder:first");
			var oldhtml = currenteditor.html();
			var oldjson = $holder.data("json") || {};
			var html = contenthandle.getData();
			var json = DesignUtils.collectConfigurerData($("#editorModal")) || {};
			var helper = {
				html : html,
				json : json,
				oldHtml : oldhtml,
				oldJson : oldjson
			};
			if (typeof window.beforeSaveConfig === "function" && window.beforeSaveConfig.call(currenteditor, helper) === false) {
				return false;
			}
			// 先设置HTML,方便后面修改ID和NAME属性
			currenteditor.html(helper.html);
			// 重新取holder
			$holder = currenteditor.children(":first");// .find(".box-holder:first");
			if ($holder && $holder.length <= 0) {
				// 没有可用的占位符,添加一个
				$holder = $("<div class='box-holder'> " + currenteditor.html() + " </div>");
				currenteditor.empty().append($holder);
			}
			// 设置一些系统属性
			helper.json["id"] && $holder.attr("id", helper.json["id"]);// ID可设置,可不设置
			helper.json["name"] && $holder.attr("name", helper.json["name"]);// NAME可设置,可不设置
			var gid = $holder.attr("data-guid");
			while (!gid || $("div[data-guid=" + gid + "]").length > 1) {// 保障唯一,用来绑定数据
				$holder.attr("data-guid", (gid = $.gguid()));// GUID被修改掉或重复,重新生成一个
			}
			helper.json["gid"] = gid;
			$holder.data("json", helper.json);

			if (typeof window.afterSaveConfig === "function" && window.afterSaveConfig.call(currenteditor, helper)) {

			}
		});
		$("[data-target=#downloadModal]").click(function(e) {
			e.preventDefault();
			component.removeMenuClasses();
			$(this).addClass("active");
			// 设置标题
			var title = $(this).find("span.text").html();
			$("#downloadModal .modal-header h3").html($(this).find("span.text").html());
			$("#downloadModal .modal-footer a:first").html(title);
			component.downloadLayoutSrc();
		});
		$("[data-target=#shareModal]").click(function(e) {
			e.preventDefault();
			component.handleSaveLayout();
		});

		$("#download").click(function() {
			component.downloadLayout();
			return false
		});
		$("#downloadhtml").click(function() {
			component.downloadHtmlLayout();
			return false
		});
		$("#edit").click(function() {
			component.handleSelectable();// 启用单元格选择
			$("body").removeClass("devpreview sourcepreview");
			$("body").addClass("edit");
			component.removeMenuClasses();
			$(this).addClass("active");
			return false
		}).trigger("click");
		$("#clear").click(function(e) {
			e.preventDefault();
			component.clearDemo()
		});
		$("#devpreview").click(function() {
			component.handleSelectable(true);
			$("body").removeClass("edit sourcepreview");
			$("body").addClass("devpreview");
			component.removeMenuClasses();
			$(this).addClass("active");
			return false
		});
		$("#sourcepreview").click(function() {
			component.handleSelectable(true);
			$("body").removeClass("edit");
			$("body").addClass("devpreview sourcepreview");
			component.removeMenuClasses();
			$(this).addClass("active");
			return false
		});
		$("#fluidPage").click(function(e) {
			e.preventDefault();
			component.changeStructure("container", "container-fluid");
			$("#fixedPage").removeClass("active");
			$(this).addClass("active");
			component.downloadLayoutSrc()
		});
		$("#fixedPage").click(function(e) {
			e.preventDefault();
			component.changeStructure("container-fluid", "container");
			$("#fluidPage").removeClass("active");
			$(this).addClass("active");
			component.downloadLayoutSrc()
		});
		$(".nav-header").click(function() {
			var $next = $(this).next();
			$(".sidebar-nav .boxes, .sidebar-nav .rows").not($next).hide();
			$next.slideToggle();
		});
		$('#undo').click(function() {
			component.stopsave++;
			if (component.undoLayout()) {
				component.initContainer();
			}
			component.stopsave--;
		});
		$('#redo').click(function() {
			component.stopsave++;
			if (component.redoLayout()) {
				component.initContainer();
			}
			component.stopsave--;
		});
		component.bindRemoveElm();
		component.bindGridGenerator();

		// 初始化垃圾箱菜单
		 component.initEmptyTrash();
		// 初始化布局的表格选择
		component.initGridPicker();

		// $("#opmsg")在ready事件之后
		window.updateStateBar = (function() {
			var $opBar = $("#opmsg");
			$opBar.html("布局");
			return function(msg) {
				// console.log(msg);
				$opBar.html(msg == null ? "布局" : msg);
			};
		})();

		window.saveHtml = function() {
			component.saveHtml();
		};

		window.restoreSetting = function() {
			component.restoreSetting();
		};

		window.applySetting = function() {
			var $modal = $("#pageProperties");
			if ($modal.find("form").valid()) {
				var options = component.defaultOptions();
				$("#pageProperties").form2json(options, false);
				component.applySetting(options);
				// 应用成功,隐藏弹出框
				$modal.modal("hide");
			}
		}
	})

});