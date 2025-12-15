var WorkFlowOpinion = WorkFlowOpinion || {};
!function($) {
	// <!-- 签署意见 -->
	var str = '<div id="dlg_sign_opinion" class="form-horizontal" style="display:none;margin-bottom: 0px; padding-bottom: 0px; overflow:visible;"></div>';
	// <!-- 常用意见管理 -->
	var mgr = '<div id="dlg_opinion_mgr" style="display: none;">'
			+ '<div class="form-horizontal">'
			+ '<div class="control-group" style="margin-bottom: 10px;">'
			+ '<label class="control-label" style="width: 89px;" for="opinion_category">新增意见分类: </label>'
			+ '<div class="controls form_operate customButton" style="margin-left: 0px;margin-top: 0px;display:block;">'
			+ '<input id="opinion_category" type="text" placeholder="新分类" class="input-xlarge" maxlength="50" style="width: 524px">'
			+ '<button id="btn_opinion_category_add" style="margin-left:5px;">增加</button>'
			+ '</div>'
			+ '</div>'
			+ '<div class="control-group" style="margin-bottom: 10px;">'
			+ '<div class="controls form_operate" style="margin-left: 0px;">'
			+ '<span id="span_opinion_category"> <label class="radio inline label-opinion-category" style="display: none;"></label>'
			+ '</span>'
			+ '<div class="pull-right customButton" style="display:block;">'
			+ '<button id="btn_opinion_category_edit" style="margin-left:5px;">修改</button>'
			+ '<button id="btn_opinion_category_del" style="margin-left:5px;">删除</button>'
			+ '<button id="btn_opinion_category_move_left" style="margin-left:5px;">左移</button>'
			+ '<button id="btn_opinion_category_move_right" style="margin-left:5px;">右移</button>'
			+ '</div>'
			+ '</div>'
			+ '</div>'
			+ '</div>'
			+ '<div style="border: none; width: 100%; height: 1px; background-color: #ccc; margin-bottom: 10px;"></div>'
			+ '<div class="form-horizontal">'
			+ '<div class="control-group" style="margin-bottom: 10px;">'
			+ '<label class="control-label" style="width: 47px;" for="opinion_content">新意见: </label>'
			+ '<div class="controls form_operate customButton" style="margin-left: 0px;margin-top: 0px;display:block;">'
			+ '<textarea id="opinion_content" placeholder="新意见" class="input-xlarge" maxlength="2000" style="width: 566px;"></textarea>'
			+ '<button id="btn_opinion_content_add" style="margin-left:5px;">增加</button>'
			+ '</div>'
			+ '</div>'
			+ '<div class="control-group" style="margin-bottom: 10px;">'
			+ '<div class="controls form_operate" style="margin-left: 0px">'
			+ '<select id="opinion_contents" name="opinion_contents" class="input-xlarge" multiple="multiple" size="8" style="width: 626px; overflow: auto;">'
			+ '</select>' + '<div class="btn-group">'
			+ '<div class="customButton" style="margin-top: 9px;display:block;">'
			+ '<button id="btn_opinion_content_del" style="size:auto;margin-left:5px;">删除</button>'
			+ '</div>' + '<div class="customButton" style="margin-top: 9px;display:block;">'
			+ '<button id="btn_opinion_content_move_up" style="size:auto;margin-left:5px;">上移</button>'
			+ '</div>' + '<div class="customButton" style="margin-top: 9px;display:block;">'
			+ '<button id="btn_opinion_content_move_down" style="size:auto;margin-left:5px;">下移</button>'
			+ '</div>' + '</div>' + '</div>' + '</div>' + '</div>' + '</div>';
	// <!-- 常用意见分类编辑管理 -->
	var mgr_edit = '<div id="dlg_opinion_category_mgr" style="display: none;">'
			+ '<div class="control-group">'
			+ '<label class="control-label" for="textinput">意见分类</label>'
			+ '<div class="controls">'
			+ '<input id="opinion_category_edit" name="opinion_category_edit" type="text" placeholder="placeholder" class="input-xlarge">'
			+ '</div>' + '</div>' + '</div>';
	var getAllOpinionCategoryBeansService = "flowOpinionService.getAllOpinionCategoryBeans";
	var getOpinionCategoryService = "flowOpinionService.getAllOpinionCategoryBeans";
	var saveFlowOpinionCategoryBeansService = "flowOpinionService.saveFlowOpinionCategoryBeans";
	var categoryBeanMap = new HashMap();
	var opinionCategoryBean = null;// 当前分类实体
	var deletedCategoryUuids = [];

	$.workflowOpinion = {
		open : function(option) {
			if ($("#dlg_sign_opinion").length == 0) {
				$("body").after($(str));
				if($.fn.dropdown == null) {
					$.ajax({
						url : ctx + "/resources/bootstrap/js/bootstrap.js",
						dataType : "script",
						async : false
					});
				}
				$.get(ctx + "/pt/workflow/work/opinion_view.jsp", function(data) {
					$("#dlg_sign_opinion").html(data);
					init(option);
				});
			} else {
				init(option);
			}
		}
	};
	// 判断字符串不为undefined、null、空串、空格串
	function isNotBlank(string) {
		return string != null && $.trim(string) != "";
	}
	// 判断字符串为undefined、null、空串、空格串
	function isBlank(string) {
		return string == null || $.trim(string) == "";
	}
	function init(options) {
		// 初始化签署意见弹出框
		var o = {
			title : "签署意见",
			autoOpen : true,
			height : 400,
			width : 650,
			resizable : false,
			modal : true,
			open : function(e) {
				// 获取常用意见
				$("#mini_wf_opinion", "#dlg_sign_opinion").workflowMiniOpinion({
					width : 450,
					opinionChange : function(retVal) {
						$("textarea[name=opinionText]", "#dlg_sign_opinion").val(retVal.text);
					}
				});
				// 意见
				if(isNotBlank(options.text)) {
					$("textarea[name=opinionText]", "#dlg_sign_opinion").val(options.text);
				}
			},
			close : function(e) {
				$("textarea[name=opinionText]", "#dlg_sign_opinion").val("");
				// $("#mini_wf_opinion",
				// "#dlg_sign_opinion").workflowMiniOpinion("clearText");
				$("#dlg_sign_opinion").oDialog("destroy");
				$("#dlg_sign_opinion").remove();
			},
			buttons : {}
		};
		if(options.submit != null && options.submitLable != null) {
			o.buttons[options.submitLable] = function(e) {
				var opinionValue = $("input[name=opinionValue]:checked", "#dlg_sign_opinion").val();
				var opinionText = $("textarea[name=opinionText]", "#dlg_sign_opinion").val();
				if (isBlank(opinionText)) {
					oAlert("请填写意见内容!");
					return;
				}
				if (options.submit) {
					options.submit.call(this, {
						value : opinionValue,
						text : opinionText
					});
				}
				$(this).oDialog("close");
			}
		}
		o.buttons["确定"] = function(e) {
			var opinionValue = $("input[name=opinionValue]:checked", "#dlg_sign_opinion").val();
			var opinionText = $("textarea[name=opinionText]", "#dlg_sign_opinion").val();
			if (isBlank(opinionText)) {
				oAlert("请填写意见内容!");
				return;
			}
			if (options.ok) {
				options.ok.call(this, {
					value : opinionValue,
					text : opinionText
				});
			}
			$(this).oDialog("close");
		}
		o.buttons["取消"] = function(e) {
			// 去除意见立场单选框选择状态
			$("input[name=opinionValue]", "#dlg_sign_opinion").each(function(e) {
				$(this).attr("checked", false);
			});
			// 去除意见立场的文本内容
			$("textarea[name=opinionText]", "#dlg_sign_opinion").val("");
			if (options.cancel) {
				options.cancel.call(this);
			}
			$(this).oDialog("close");
		}

		if (options.opinions && options.opinions.length > 0) {
			var $text = $("textarea[name=opinionText]", "#dlg_sign_opinion");
			$text.css("height", "200px");

			$("#task_opinions").html("");
			$("#task_opinions").html(
					'<label class="control-label" style="text-align: left; width: 70px;">意见立场：</label>');
			$.each(options.opinions, function() {
				$("#task_opinions").append(
						'<label class="radio inline"> <input type="radio" name="opinionValue" value="'
								+ this.value + '">' + this.name + '</label>');
			});
		}
		$("#dlg_sign_opinion").oDialog(o);
	}
	function bindEvent() {
		if ($("#dlg_opinion_mgr").length == 0) {
			$("body").after($(mgr));
		}
		if ($("#dlg_opinion_category_mgr").length == 0) {
			$("body").after($(mgr_edit));
		}

		// 添加意见分类
		$("#btn_opinion_category_add")
				.click(
						function() {
							var category = $("#opinion_category").val();
							if (isBlank(category)) {
								oAlert("意见分类不能为空!");
								return;
							}
							var categories = categoryBeanMap.values();
							for ( var i = 0; i < categories.length; i++) {
								if (category === categories[i].name) {
									oAlert("意见分类[" + category + "]已经存在!");
									return;
								}
							}
							var id = new Date().getTime();
							var radio = $('<label class="radio inline label-opinion-category"><input type="radio" value="'
									+ category
									+ '" id="'
									+ id
									+ '" code="'
									+ id
									+ '" name="opinion_categories" style="margin-left: 0px">' + category + '</label>');
							// $(".label-opinion-category:last").after(radio);
							$("#opinion_category").val("");
							$("#span_opinion_category").append(radio);
							$("input", radio).change(function(e) {
								getOpinionByCategory(this.id);
							});

							// 更新数据
							var categoryBean = {
								name : category,
								code : id,
								opinions : []
							};
							categoryBeanMap.put(id, categoryBean);
							// 更新意见分类排序数据
							updateCategoryBean();
						});

		// 修改意见分类
		$("#btn_opinion_category_edit").click(function() {
			var $category = $("input[name=opinion_categories]:checked");
			if ($category.length == 0) {
				alert("请选择意见分类!");
				return;
			}
			var value = $category.val();
			var key = $category.attr("id");
			var $categoryInput = $("input[name=opinion_category_edit]");
			$("#dlg_opinion_category_mgr").oDialog({
				title : "编辑常用意见分类",
				width : 310,
				height : 180,
				autoOpen : true,
				resizable : false,
				modal : true,
				open : function(e) {
					$categoryInput.val(value);
				},
				buttons : {
					"确定" : function(e) {
						var v = $categoryInput.val();
						$category.val(v);
						var parent = $category.parent();
						parent.html("");
						parent.append($category);
						parent.append(v);
						// 更新数据
						var categoryBean = categoryBeanMap.get(key);
						if (categoryBean != null) {
							categoryBean.name = v;
						}
						// 收集与保存意见立场
						e.stopPropagation();
						$(this).oDialog("close");
					},
					"取消" : function(e) {
						e.stopPropagation();
						$(this).oDialog("close");
					}
				}
			});
		});
		// 删除意见分类
		$("#btn_opinion_category_del").click(function() {
			var $category = $("input[name=opinion_categories]:checked");
			if ($category.length == 0) {
				alert("请选择意见分类!");
				return;
			}
			var uuid = $category.attr("id");
			if (isNotBlank(uuid)) {
				deletedCategoryUuids.push(uuid);
			}
			oConfirm("确定要删除意见分类[" + $category.val() + "]吗?", function() {
				$category.parent().remove();
				$("#opinion_contents").html("");

				// 更新数据
				categoryBeanMap.remove(uuid);

				// 更新意见分类排序数据
				updateCategoryBean();
			});
		});
		// 左移意见分类
		$("#btn_opinion_category_move_left").click(function() {
			var $selected = $(":radio:checked", "#span_opinion_category");
			if ($selected.length == 0) {
				alert("请选择意见分类!");
				return;
			}
			var $selectedLabel = $selected.parent();
			var $labels = $("#span_opinion_category").find(".label-opinion-category:visible");
			var currentIndex = $labels.index($selectedLabel);
			if (currentIndex > 0) {
				$labels.eq(currentIndex - 1).before($selectedLabel);
				// 更新意见分类排序数据
				updateCategoryBean();
			}
		});
		// 右移意见分类
		$("#btn_opinion_category_move_right").click(function() {
			var $selected = $(":radio:checked", "#span_opinion_category");
			if ($selected.length == 0) {
				alert("请选择意见分类!");
				return;
			}
			var $selectedLabel = $selected.parent();
			var $labels = $("#span_opinion_category").find(".label-opinion-category:visible");
			var currentIndex = $labels.index($selectedLabel);
			if (currentIndex < $labels.length - 1) {
				$labels.eq(currentIndex + 1).after($selectedLabel);
				// 更新意见分类排序数据
				updateCategoryBean();
			}
		});

		// 添加意见内容
		$("#btn_opinion_content_add").click(function() {
			var content = $("#opinion_content").val();
			if (isBlank(content)) {
				oAlert("意见不能为空!");
				return;
			}
			var uuid = new Date().getTime();
			var option = '<option id="' + uuid + '" value="' + content + '">' + content + '</option>';
			$("#opinion_content").val("");
			$("#opinion_contents").append(option);

			// 更新数据
			var opinions = opinionCategoryBean.opinions;
			opinions.push({
				uuid : uuid,
				content : content,
				code : opinions.length
			});
		});
		// 删除意见内容
		$("#btn_opinion_content_del").click(function() {
			var $content = $("#opinion_contents").find("option:selected");
			if ($content.length == 0) {
				oAlert("请选择意见!");
				return;
			}
			oConfirm("确定要删除意见[" + $content.val() + "]吗?", function() {
				$content.remove();
				// 更新数据
				var uuid = $content.attr("id");
				var opinions = opinionCategoryBean.opinions;
				var newOpinions = [];
				for ( var i = 0; i < opinions.length; i++) {
					if (opinions[i].uuid != uuid) {
						newOpinions.push(opinions[i]);
					}
				}
				opinionCategoryBean.opinions = newOpinions;
			});
		});
		// 上移意见内容
		$("#btn_opinion_content_move_up").click(function() {
			var $contents = $("#opinion_contents");
			var content = $contents.find("option:selected");
			var currentIndex = $contents.find("option").index(content);
			if (currentIndex > 0) {
				$contents.find("option").eq(currentIndex - 1).before(content);
				// 更新数据
				var opinions = opinionCategoryBean.opinions;
				var temp = opinions[currentIndex - 1];
				opinions[currentIndex - 1] = opinions[currentIndex];
				opinions[currentIndex] = temp;
				for ( var i = 0; i < opinions.length; i++) {
					opinions[i].code = i;
				}
			}
		});
		// 下移意见内容
		$("#btn_opinion_content_move_down").click(function() {
			var $contents = $("#opinion_contents");
			var content = $contents.find("option:selected");
			var currentIndex = $contents.find("option").index(content);
			if (currentIndex < $contents.find("option").length - 1) {
				$contents.find("option").eq(currentIndex + 1).after(content);
				// 更新数据
				var opinions = opinionCategoryBean.opinions;
				var temp = opinions[currentIndex + 1];
				opinions[currentIndex + 1] = opinions[currentIndex];
				opinions[currentIndex] = temp;
				for ( var i = 0; i < opinions.length; i++) {
					opinions[i].code = i;
				}
			}
		});
	}

	// 意见立场管理
	function onManagerOpinion2() {
		var options = this.options;
		$("#dlg_opinion_mgr").oDialog({
			title : "常用意见管理",
			width : 700,
			height : 430,
			autoOpen : true,
			resizable : false,
			modal : true,
			open : function(e) {
				JDS.call({
					service : getOpinionCategoryService,
					data : [],
					success : function(result) {
						onGetOpinionCategorySuccess(result);
					}
				});
			},
			close : function(e) {
				categoryBeanMap = new HashMap();
				opinionCategoryBean = null;
				deletedCategoryUuids = [];
				e.stopPropagation();
				$("#span_opinion_category").html("");
			},
			buttons : {
				"确定" : function(e) {
					// 收集与保存意见立场
					collectAndSaveOpinion();
					// 加载常用意见
					JDS.call({
						service : getAllOpinionCategoryBeansService,
						data : [],
						success : function(result) {
							onGetAllOpinionCategoryBeansSuccess2(options, result);
						}
					});
					e.stopPropagation();
					$(this).oDialog("close");
				},
				"取消" : function(e) {
					e.stopPropagation();
					$(this).oDialog("close");
				}
			}
		});
	}
	WorkFlowOpinion.onManagerOpinion = onManagerOpinion2;
	// 意见立场管理
	function onManagerOpinion() {
		$("#dlg_opinion_mgr").oDialog({
			title : "常用意见管理",
			width : 700,
			height : 430,
			autoOpen : true,
			resizable : false,
			modal : true,
			open : function(e) {
				JDS.call({
					service : getOpinionCategoryService,
					data : [],
					success : function(result) {
						onGetOpinionCategorySuccess(result);
					}
				});
			},
			close : function(e) {
				categoryBeanMap = new HashMap();
				opinionCategoryBean = null;
				deletedCategoryUuids = [];
				e.stopPropagation();
				$("#span_opinion_category").html("");
			},
			buttons : {
				"确定" : function(e) {
					// 收集与保存意见立场
					collectAndSaveOpinion();
					e.stopPropagation();
					$(this).oDialog("close");
				},
				"取消" : function(e) {
					e.stopPropagation();
					$(this).oDialog("close");
				}
			}
		});
	}
	function onGetOpinionCategorySuccess(result) {
		$(result.data).each(
				function(i) {
					if(this.id === "public") {
					} else {
						categoryBeanMap.put(this.uuid, this);
						var radio = $('<label class="radio inline label-opinion-category">' + '<input id="'
								+ this.uuid + '" code="' + this.code
								+ '" name="opinion_categories" type="radio" value="' + this.name + '" style="margin-left: 0px">'
								+ this.name + '</label>');
						$("#span_opinion_category").append(radio);
						// 选中第一个radio
						if (i == 0) {
							$("#" + this.uuid).attr("checked", "checked");
							getOpinionByCategory(this.uuid);
						}
						$("input", radio).change(function(e) {
							getOpinionByCategory(this.id);
						});
					}
				});
	}
	// 收集与保存意见立场
	function collectAndSaveOpinion() {
		var beans = categoryBeanMap.values();
		JDS.call({
			service : saveFlowOpinionCategoryBeansService,
			data : [ beans, deletedCategoryUuids ],
			async : false,
			success : function(e) {
				oAlert("保存成功！", function() {
					$("#common_opinion").html("");
					JDS.call({
						service : getAllOpinionCategoryBeansService,
						data : [],
						success : function(result) {
							onGetAllOpinionCategoryBeansSuccess(result);
						}
					});
				});
			}
		});
	}

	function getOpinionByCategory(categoryUuid) {
		opinionCategoryBean = categoryBeanMap.get(categoryUuid);
		$("#opinion_contents").html("");
		if (opinionCategoryBean != null) {
			$.each(opinionCategoryBean.opinions, function() {
				var option = '<option id="' + this.uuid + '" value="' + this.content + '">' + this.content
						+ '</option>';
				$("#opinion_contents").append(option);
			});
		}
	}

	// 更新意见分类排序数据
	function updateCategoryBean() {
		$.each($("#span_opinion_category").find(".label-opinion-category:visible"), function(i) {
			var key = $(this).find(":radio").attr("id");
			var categoryBean = categoryBeanMap.get(key);
			if (categoryBean != null) {
				categoryBean.code = i;
			}
		});
	}

	// 新版签署意见
	var wfOpinion = '<div class="wf_opinion">'
			+ '<div class="sign-opinion-value">'
			+ '	<select class="opinion-select" data-placeholder="意见立场">'
			+ '		<option></option>'
			+ '	</select>'
			+ '</div>'
			+ '<div class="sign-opinion-input">'
			+ '	<textarea name="sign-opinion-tx" maxlength="4000"></textarea>'
			+ '	<span class="sign-open"></span>'
			+ '	<div class="navbar"'
			+ '		style="padding: 0px; margin: 0px; border: none;">'
			+ '		<div class="navbar-inner"'
			+ '			style="padding: 0px; margin: 0px; border: none;">'
			+ '			<ul class="nav pull-right">'
			+ '				<li><a href="#" class="dropdown-toggle"'
			+ '					data-toggle="dropdown"><span class="btn-sign-opinion"></span>'
			+ '				</a>'
			+ '					<ul'
			+ '						class="pull-right dropdown-navbar navbar-pink dropdown-menu dropdown-caret dropdown-closer dropdown-custom"'
			+ '						style="right: -7px; top: 28px;">' + '						<li class="nav-header"><span><i'
			+ '								class="icon-cog-blue"></i>管理</span></li>' + '						<li><a href="#">'
			+ '								<div class="clearfix" style="margin: 0 2px; border: none;">'
			+ '									<div class="accordion" id="accordion"><a href="#"> </a>' + '									</div>'
			+ '								</div>' + '						</a></li>' + '					</ul></li>' + '			</ul>' + '		</div>' + '	</div>'
			+ '</div>' + '</div>';
	var WorkFlowMiniOpinion = function(element, options) {
		this.$element = $(element);
		this.options = $.extend($.fn.workflowMiniOpinion.defaults, options);
		this.init();
	};
	WorkFlowMiniOpinion.prototype = {
		constructor : WorkFlowMiniOpinion,
		init : function() {
			var $this = $(this);
			var $element = this.$element;
			$element.html(wfOpinion);
			var options = this.options;

			this.setWidth(options.width);

			// 意见立场
			if (options.opinions && options.opinions.length > 0) {
				this.requiredOpinionPosition = true;
				$.each(options.opinions, function() {
					var option = '<option value="' + this.value + '">' + this.name + '</option>';
					$(".opinion-select").append(option);
				});
				$(".opinion-select").change(function(e) {
					var optionValue = $(".opinion-select").find("option:selected").val();
					var optionLabel = $(".opinion-select").find("option:selected").text();
					var $text = $("textarea[name=sign-opinion-tx]", ".sign-opinion-input");
					$text.val(optionLabel);
					var optionText = $text.val();
					// 触发意见更改事件
					options.opinionChange.call(this, {
						value : optionValue,
						label : optionLabel,
						text : optionText
					});
				});

				$(".opinion-select").chosen({
					disable_search_threshold : 10
				});
			} else {
				var $os = $(".opinion-select");
				if ($(".opinion-select-label").length == 0) {
					$os
							.after("<div class='opinion-select-label' style='padding-left:8px;font-size:13px;width:58px;'>签署意见<div>");
					$(".wf_opinion .sign-opinion-input").css("left", "91px");
				}
				$os.hide();
			}

			$(".sign-opinion-input textarea").keydown(function(e) {
				if (e.keyCode === 13) {
					$this.workflowMiniOpinion("open");
				}
			});
			$(".sign-opinion-input textarea").bind("keyup focusout", function(e) {
				// 触发意见更改事件
				var optionText = $(this).val();
				var optionValue = $(".opinion-select").find("option:selected").val();
				var optionLabel = $(".opinion-select").find("option:selected").text();
				options.opinionChange.call(this, {
					value : optionValue,
					label : optionLabel,
					text : optionText
				});
			});

			$(".sign-open").toggle(function() {
				$(this).addClass("sign-close");
				// $(".form_toolbar").css("height", "163px");
				$(".sign-opinion-input").css("height", "170px");
				$(".sign-opinion-input textarea").css("height", "164px");
			}, function() {
				$(this).removeClass("sign-close");
				// $(".form_toolbar").css("height", "auto");
				$(".sign-opinion-input").css("height", "26px");
				$(".sign-opinion-input textarea").css("height", "20px");
			});

			// 签署意见管理
			$(".nav-header>span").click($.proxy(onManagerOpinion2, this));

			$(".btn-sign-opinion").one("click", function() {
				$("#accordion").html("");
				// 加载常用意见
				JDS.call({
					service : getAllOpinionCategoryBeansService,
					data : [],
					success : function(result) {
						onGetAllOpinionCategoryBeansSuccess2(options, result);
					}
				});
			});

			bindEvent();
		},
		open : function() {
			if (!$(".sign-open").hasClass("sign-close")) {
				$(".sign-open").trigger("click");
			}
			$(".sign-opinion-input textarea").trigger("focus");
		},
		isRequiredOpinionPosition : function() {
			if(this.requiredOpinionPosition === true) {
				return $.trim($(".opinion-select").val()) === "";
			}
			return false;
		},
		setWidth : function(width) {
			var w = width - 24;
			if (w > 450) {
				w = 450;
			} else if(w < 100){
				w = 200;
			}
			var $input = $(".wf_opinion .sign-opinion-input");
			var $txt = $(".sign-opinion-input textarea");
			$input.css("width", w);
			var txtWidth = w - 36;
			$txt.css("width", txtWidth);
		},
		signOpinion : function(option) {
			$(".sign-opinion-input textarea").text(option["text"]);
			var options = this.options;
			if (options.opinionChange) {
				options.opinionChange.call(this, {
					value : option["value"],
					label : option["label"],
					text : option["text"]
				});
			}
		}
	};
	function onGetAllOpinionCategoryBeansSuccess2(options, result) {
		var data = result.data;
		var menu = "";
		if (data.length == 1) {
			menu = menu + '<div class="accordion-group" style="margin-bottom:0px;">';
			menu = menu + '<div class="accordion-heading" style="display:none;">';
			menu = menu + '<a class="accordion-toggle" href="#">' + data[0].name + '</a>';
			menu = menu + '<span class="accordion-toggle-icon"></span></div>';
			menu = menu + '<div class="accordion-body collapse">';
			menu = menu + '<div class="accordion-inner accordion-inner-single">';
			menu = menu + '<ul>';
			$.each(data[0].opinions, function(i) {
				menu = menu + '<li class="opinion-menu-item">';
				menu = menu + '<a href="#" tabindex="-1" title="' + this.content + '">';
				menu = menu + this.content;
				menu = menu + '</a></li>';
			});
			menu = menu + '</ul></div></div></div>';
		} else {
			$.each(data, function(i) {
				menu = menu + '<div class="accordion-group">';
				menu = menu + '<div class="accordion-heading">';
				menu = menu + '<a class="accordion-toggle" href="#">' + this.name + '</a>';
				menu = menu + '<span class="accordion-toggle-icon"></span></div>';
				menu = menu + '<div class="accordion-body collapse">';
				menu = menu + '<div class="accordion-inner">';
				menu = menu + '<ul>';
				$.each(this.opinions, function(j) {
					menu = menu + '<li class="opinion-menu-item">';
					menu = menu + '<a href="#" tabindex="-1" title="' + this.content + '">';
					menu = menu + this.content;
					menu = menu + '</a></li>';
				});
				menu = menu + '</ul></div></div></div>';
			});
		}
		$("#accordion").html(menu);
		$(".opinion-menu-item", "#accordion").bind("click", function() {
			var $text = $("textarea[name=opinionText]", ".textarea");
			if($text.is(":visible") === false) {
				$text = $("textarea[name=sign-opinion-tx]", ".sign-opinion-input");
			}
			$text.val($text.val() + $(this).text());

			// 触发意见更改事件
			var optionText = $text.val();
			var optionValue = $(".opinion-select").find("option:selected").val();
			var optionLabel = $(".opinion-select").find("option:selected").text();
			options.opinionChange.call(this, {
				value : optionValue,
				label : optionLabel,
				text : optionText
			});
		});

		$(".accordion-heading").click(function(e) {
			if ($(this).next().hasClass("in")) {
				$(this).next().removeClass("in");
				$(".accordion-heading").find("a").filter(".accordion-toggle").each(function() {
					$(this).removeClass("collapsed");
				});
				$(".accordion-heading").find("span").filter(".accordion-toggle-icon").each(function() {
					$(this).removeClass("collapsed");
				});
			} else {
				$(".accordion-body").filter(".in").removeClass("in");
				$(this).next().addClass("in");
				$(".accordion-heading").find("a").filter(".accordion-toggle").each(function() {
					$(this).removeClass("collapsed");
				});
				$(this).find("a").filter(".accordion-toggle").addClass("collapsed");

				$(".accordion-heading").find("span").filter(".accordion-toggle-icon").each(function() {
					$(this).removeClass("collapsed");
				});
				$(this).find("span").filter(".accordion-toggle-icon").addClass("collapsed");
			}
			e.stopPropagation();
		});
		$(".accordion-heading:first").trigger("click");
	}
	$.fn.workflowMiniOpinion = function(option) {
		var method = false;
		var args = null;
		if (arguments.length == 2) {
			method = true;
			args = arguments[1];
			// args = Array.prototype.splice.call(arguments, 1);
		}
		var methodResult = null;
		var results = this.each(function() {
			var $this = $(this);
			var data = $this.data('workflowMiniOpinion');
			var options = typeof option == 'object' && option;
			if (!data) {
				$this.data('workflowMiniOpinion', (data = new WorkFlowMiniOpinion(this, options)));
			}
			if (typeof option == 'string') {
				if (method == true && args != null) {
					methodResult = data[option](args);
				} else {
					methodResult = data[option]();
				}
			}
		});
		if(methodResult != null){
			return methodResult;
		}
		return results;
	};
	$.fn.workflowMiniOpinion.defaults = {
		width : 540,
		opinionChange : function(retVal) {
		}
	};
}(window.jQuery);