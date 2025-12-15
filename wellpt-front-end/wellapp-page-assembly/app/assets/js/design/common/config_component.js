define([ "jquery", "jquery-ui", "bootstrap", "server", "commons", "jquery-layout", "jquery-htmlClean", "jquery-ui-contextmenu", "docs-min", "bootstrap-switch", "jquery-validate", "jquery-migrate" ],
		function($D, jqueryui, bootstrap, server, commons) {
			require([ "jquery-validate-additional-methods", "jquery-validate-messages_zh_CN" ]);

			$.lang = $.lang || {};
			$.extend($.lang, {
				zh_cn : {

				},
				en_us : {

				}
			});

			$.fn.extend({
				// 重载load,添加全局wRequire
				/**
				 * @method wLoad see $.load
				 * @param url
				 * @param params
				 * @param callback
				 */
				wLoad : function(url, params, callback) {
					var $that = this;
					// Export wRequire as a global.
					window.wRequire = function(deps, callback, errback, optional) {
						window.wRequire = null;
						var cb = callback, eb = errback;
						callback = function() {
							var e = $.Event('exec.bs.tab', {});
							$that.trigger(e);
							$.isFunction(cb) && cb.apply(this, arguments);
							e = $.Event('execed.bs.tab', {});
							$that.trigger(e);
						}
						errback = function() {
							var e = $.Event('errc.bs.tab', {});
							$that.trigger(e);
							$.isFunction(eb) && eb.apply(this, arguments);
							e = $.Event('errced.bs.tab', {});
							$that.trigger(e);
						}
						return require.call(this, deps, callback, errback, optional);
					};
					$that.load(url, params, callback);
				},

				// 将JSON对象的数据设置到表单中
				json2form : function(object, checkboxesAsString) {
					// 如果对象不存在属性直接忽略
					for ( var property in object) {
						$(':input[name=' + property + ']', $(this)).each(function() {
							var type = this.type;
							var name = this.name;
							var data = object[name];
							switch (type) {
							case undefined:
							case "text":
							case "password":
							case "hidden":
							case "button":
							case "reset":
							case "textarea":
							case "submit": {
								$(this).val(data);
								break;
							}
							case "checkbox":
							case "radio": {
								this.checked = false;
								if ($.isArray(data) === true) {// checkbox
									// multiple value is Array
									for(var el = 0; el < data.length; el++) {
										if (data[el] == $(this).val()) {
											this.checked = true;
										}
									}
								} else {// radio is a string single value
									if (type == "radio") {
										if (data == $(this).val()) {
											this.checked = true;
										}
									} else {
										if (checkboxesAsString && checkboxesAsString === true) {
											data = (data == null) ? "" : data;
											var a = data.split(",");
											for(var el = 0; el < a.length; el++) {
												if (a[el] == $(this).val()) {
													this.checked = true;
												}
											}
										} else {
											// 当个checkbox只有true、false
											this.checked = (data == true || data == "true");
										}
									}
								}
								break;
							}
							case "select":
							case "select-one":
							case "select-multiple": {
								$(this).find("option:selected").attr("selected", false);
								if ($.isArray(data) === true) {
									for(var el = 0; el < data.length; el++) {
										$(this).find("option[value='" + data[el] + "']").attr("selected", true);
									}
								} else {
									$(this).find("option[value='" + data + "']").attr("selected", true);
								}
								break;
							}
							}
						});
					}
				},

				// 将表单中的数据收集到指定的JSON对象中
				form2json : (function() {
					/**
					 * Returns the value of the field element.
					 */
					function fieldValue(el, successful) {
						var n = el.name, t = el.type, tag = el.tagName.toLowerCase();
						if (successful === undefined) {
							successful = true;
						}

						if (successful
								&& (!n || el.disabled || t == 'reset' || t == 'button' || (t == 'checkbox' || t == 'radio') && !el.checked || (t == 'submit' || t == 'image') && el.form && el.form.clk != el || tag == 'select' && el.selectedIndex == -1)) {
							return null;
						}

						if (tag == 'select') {
							var index = el.selectedIndex;
							if (index < 0) {
								return null;
							}
							var a = [], ops = el.options;
							var one = (t == 'select-one' || t == 'text');
							var max = (one ? index + 1 : ops.length);
							for (var i = (one ? index : 0); i < max; i++) {
								var op = ops[i];
								if (op.selected) {
									var v = op.value;
									if (!v) { // extra pain for IE...
										v = (op.attributes && op.attributes['value'] && !(op.attributes['value'].specified)) ? op.text : op.value;
									}
									if (one) {
										return v;
									}
									a.push(v);
								}
							}
							return a;
						}
						return $(el).val();
					}
					return function(object, checkboxesAsString) {
						// 如果表单元素不存在直接忽略
						for ( var property in object) {
							var elements = $(':input[name=' + property + ']', $(this));
							if (elements.length == 0) {
								continue;
							}
							// 单个元素
							if (elements.length == 1) {
								var element = elements[0];
								var v = fieldValue(element);
								if (v && v.constructor == Array) {
									var a = [];
									for (var i = 0, max = v.length; i < max; i++) {
										a.push(v[i]);
									}
									object[property] = a;
								} else {
									if (v !== null && typeof v != 'undefined') {
										object[property] = v;
									}
									// checkbox只有一个值(true、false)
									if (element.type == 'checkbox') {
										if (element.checked === true) {
											object[property] = true;
										} else {
											object[property] = false;
										}
									}
								}
							}
							// 多个元素
							if (elements.length > 1) {
								var a = [];
								for (var i = 0; i < elements.length; i++) {
									var element = elements[i];
									var v = fieldValue(element);
									if (v && v.constructor == Array) {
										for (var i = 0, max = v.length; i < max; i++) {
											a.push(v[i]);
										}
									} else {
										if (v !== null && typeof v != 'undefined') {
											a.push(v);
										}
									}
								}
								// 单选框只有一个值
								if (elements[0].type == 'radio') {
									object[property] = a.join();
								} else {
									if (elements[0].type == 'checkbox' && checkboxesAsString && checkboxesAsString === true) {
										object[property] = a.join();
									} else {
										object[property] = a;
									}
								}
							}
						}
					}
				})()

			});
			// 生成简短GUID
			window.gguid = $.gguid = function() {
				var dt = Math.floor(Math.random() * 900) + 100; // 3位随机数
				// jQuery.guid += 100; // 当前窗口jQuery对象个数
				return function(prefix) {
					// debugger;// field id
					return (prefix == null ? "ED" : prefix) + dt + (++jQuery.guid);
				}
			}();

			$.component = $.component || {};

			// option
			$.extend($.component, {
				// global
				options : {},
				webpage : "",
				stopsave : 0,
				startdrag : 0,
				$zone : undefined,
				$trash : undefined,
				$dlayout : undefined,
				demoHtml : undefined,
				saveInterval : undefined,
				layouthistory : undefined,
				currenteditor : undefined,
				definitionJson : undefined,
				// contentMenu
				contentMenu : [ {
					// Cut
					title : "剪切 <kbd>Ctrl+X</kbd>",
					cmd : "cut",
					uiIcon : "ui-icon-scissors",
					disabled : true
				}, {
					// Copy
					title : "拷贝 <kbd>Ctrl+C</kbd>",
					cmd : "copy",
					uiIcon : "ui-icon-copy",
					disabled : true
				}, {
					// Paste
					title : "黏贴 <kbd>Ctrl+V</kbd>",
					cmd : "paste",
					uiIcon : "ui-icon-clipboard",
					disabled : true
				}, {
					title : "----"
				}, {
					// Merge
					title : "合并 <kbd>Ctrl+I</kbd>",
					cmd : "zoomout",
					uiIcon : "ui-icon-zoomout",
					disabled : true
				}, {
					// Split
					title : "拆分 <kbd>Ctrl+O</kbd>",
					cmd : "zoomin",
					uiIcon : "ui-icon-zoomin",
					disabled : true
				}, ]
			});
			// option end

			// support
			$.extend($.component, {
				// support constant
				support : (function() {
					var support = {
						storage : $.type(window.localStorage) == 'object'
					}
					return support
				})(),

				// random number xxxxxx
				randomNumber : (function(e, t) {
					return function randomFromInterval() {
						return Math.floor(Math.random() * (t - e + 1) + e)
					}
				})(1, 1e6),

				defaultOptions : function() {
					return $.extend({}, {
						maxDeep : 9,
						offline : false,
						undoHisCount : 50,
						trashEnable : true,
						language : "zh-cn",
						theme : "JqueryUI1",
						autoSaveInterval : 1000,
						cols5 : "4 2 2 2 2",
						cols7 : "6 1 1 1 1 1 1",
						cols8 : "5 1 1 1 1 1 1 1",
						cols9 : "4 1 1 1 1 1 1 1 1",
						cols10 : "3 1 1 1 1 1 1 1 1 1",
						cols11 : "2 1 1 1 1 1 1 1 1 1 1"
					});
				}

			});
			// support end

			// function
			$.extend($.component,{
								/**
								 * @method handleSaveLayout
								 */
								handleSaveLayout : function(manual) {
									var demo = this.$zone.html();
									if (!this.stopsave && demo != this.zoneHtml) {
										this.stopsave++;
										this.zoneHtml = demo;
										this.saveLayout(manual);
										this.stopsave--;
									}
								},

								/**
								 * @method saveLayout
								 * @param manual :
								 *            false/true & auto/manual
								 */
								saveLayout : function(manual) {
									var data = this.layouthistory;
									if (!data) {
										data = {};
										data.count = 0;
										data.list = [];
									}
								    var saveEvent = $.Event('save.editor.zone', data);
								    this.$zone.trigger(saveEvent);
								    if (saveEvent.isDefaultPrevented()) return
									// 删除空元素
									if (data.list.length > data.count) {
										for (i = data.count; i < data.list.length; i++) {
											data.list[i] = null;
										}
									}
									data.list[data.count] = this.zoneHtml;
									data.count++;
									if (this.support.storage) {
										localStorage.setItem("layoutdata", JSON.stringify(data));
									}
									this.layouthistory = data
									
								    var savedEvent = $.Event('saved.editor.zone', this.layouthistory);
								    this.$zone.trigger(savedEvent);
								},

								/**
								 * @method undoLayout
								 */
								undoLayout : function() {
									var data = this.layouthistory;
									if (data) {
										if (data.count < 2) {
											return false;
										}
									    var undoEvent = $.Event('undo.editor.zone', data);
									    this.$zone.trigger(undoEvent);
									    if (undoEvent.isDefaultPrevented()) return
									    
										this.zoneHtml = data.list[data.count - 2];
										data.count--;
										this.$zone.html(this.zoneHtml);
										if (this.support.storage) {
											localStorage.setItem("layoutdata", JSON.stringify(data));
										}
									    var undidEvent = $.Event('undid.editor.zone', data);
									    this.$zone.trigger(undidEvent);
										return true;
									}
									return false
								},

								/**
								 * @method redoLayout
								 */
								redoLayout : function() {
									var data = this.layouthistory;
									if (data && data.list[data.count]) {
									    var redoEvent = $.Event('redo.editor.zone', data);
									    this.$zone.trigger(redoEvent);
									    if (redoEvent.isDefaultPrevented()) return
										
										this.zoneHtml = data.list[data.count];
										data.count++;
										this.$zone.html(this.zoneHtml);
										if (this.support.storage) {
											localStorage.setItem("layoutdata", JSON.stringify(data));
										}
									    var redidEvent = $.Event('redid.editor.zone', data);
									    this.$zone.trigger(redidEvent);
										return true;
									}
									return false
								},

								// handleJsIds
								/**
								 * @method handleTabsIds
								 * @param e
								 *            #myTabs
								 */
								handleTabsIds : function(e) {
									var t = this.randomNumber();
									var n = "tabs-" + t;
									e.attr("id", n);
									e.find(".tab-pane").each(function(e, t) {
										var n = $(t).attr("id");
										var r = "panel-" + $.component.randomNumber();
										$(t).attr("id", r);
										$(t).parent().parent().find("a[href=#" + n + "]").attr("href", "#" + r)
									})
								},
								/**
								 * @method handleModalIds
								 * @param e
								 *            #myModalLink
								 */
								handleModalIds : function(e) {
									var t = this.randomNumber();
									var n = "modal-container-" + t;
									var r = "modal-" + t;
									e.attr("id", r);
									e.attr("href", "#" + n);
									e.next().attr("id", n)
								},
								/**
								 * @method handleCarouselIds
								 * @param e
								 *            #myCarousel
								 */
								handleCarouselIds : function(e) {
									var t = this.randomNumber();
									var n = "carousel-" + t;
									e.attr("id", n);
									e.find(".carousel-indicators li").each(function(e, t) {
										$(t).attr("data-target", "#" + n)
									});
									e.find(".left").attr("href", "#" + n);
									e.find(".right").attr("href", "#" + n)
								},
								/**
								 * @method handleAccordionIds
								 * @param e
								 *            #myAccordion
								 */
								handleAccordionIds : function(e) {
									var t = this.randomNumber();
									var n = "accordion-" + t;
									var r;
									e.attr("id", n);
									e.find(".accordion-group").each(function(e, t) {
										r = "accordion-element-" + $.component.randomNumber();
										$(t).find(".accordion-toggle").each(function(e, t) {
											$(t).attr("data-parent", "#" + n);
											$(t).attr("href", "#" + r)
										});
										$(t).find(".accordion-body").each(function(e, t) {
											$(t).attr("id", r)
										})
									})
								},
								/**
								 * @method handleJsIds
								 */
								handleJsIds : function() {
									var element;
									if ((element = this.$zone.find("#myTabs")).length > 0) {
										this.handleTabsIds(element);
									} else if ((element = this.$zone.find("#myModalLink")).length > 0) {
										this.handleModalIds(element);
									} else if ((element = this.$zone.find("#myCarousel")).length > 0) {
										this.handleCarouselIds(element);
									} else if ((element = this.$zone.find("#myAccordion")).length > 0) {
										this.handleAccordionIds(element);
									}
								},

								// handleContextMenu
								/**
								 * @method matchSpan
								 * @param clazzes,
								 *            col-xs-xxx element classes
								 */
								matchSpan : function(clazzes) {
									var matcharr;
									if (clazzes && $.type(matcharr = clazzes.match(/^col-xs-\d+/g)) == "array" && matcharr.length > 0) {
										for (var i = 0; i < matcharr.length; i++) {
											try {
												return parseInt(matcharr[i].replace("col-xs-", ""), 10);
											} catch (ex) {
												// continue
												console && console.log(ex);
											}
										}
									}
									return false
								},
								/**
								 * @method cellMerge
								 * @param selected :
								 *            selected element col-xs-xxx
								 */
								cellMerge : function(selected) {
									for (var i = 0; i < selected.length - 1; i++) {
										var ispan, $iview = $(selected[i]);
										var iclazz = $iview.attr("class");
										if ($iview.hasClass("hide") || (ispan = this.matchSpan(iclazz)) === false) {
											continue;
										}
										var $ippview = $iview.parent().parent();
										for (var j = i + 1; j < selected.length; j++) {
											var jspan, jjspan, dspan = 0, $jview = $(selected[j]);
											var jclazz = $jview.attr("class");
											var $jppview = $($jview).parent().parent();
											if ($jview.hasClass("hide") || !$jppview.is($ippview) || (jspan = this.matchSpan(jclazz)) === false || (ispan = this.matchSpan(iclazz)) === false) {
												// 更新ispan
												continue;
											}
											var $jjview = $jview.next("div.column");
											var jjclazz = $jjview.attr("class");
											// 修复之前合并过的单元格,用于拆分时能还原到最初的布局
											while ($jjview.hasClass("hide") && (jjspan = this.matchSpan(jjclazz)) !== false) {
												dspan += jjspan;// jspan要减去的列
												// 递归到下一个显示布局
												$jjview = $jjview.next("div.column");
												jjclazz = $jjview.attr("class");
											}
											if (dspan > 0) {
												// 更新合并的class
												$jview.attr("class", (jclazz = jclazz.replace("col-xs-" + jspan, "col-xs-" + (jspan - dspan))));
											}
											$jview.addClass("hide");// 标记隐藏
											// 不改变class的顺序
											$iview.attr("class", (iclazz = iclazz.replace("col-xs-" + ispan, "col-xs-" + (ispan + jspan))));
										}
									}
								},
								/**
								 * @method cellSplit
								 * @param selected :
								 *            selected element col-xs-xxx
								 */
								cellSplit : function(selected) {
									for (var i = 0; i < selected.length; i++) {
										var ispan, dspan = 0, $iview = $(selected[i]);
										var iclazz = $iview.attr("class");
										if ($iview.hasClass("hide") || (ispan = this.matchSpan(iclazz)) === false) {
											continue;
										}
										var jspan, $jview = $iview.next("div.column");
										var jclazz = $jview.attr("class");
										// 找到非隐藏为止
										while ($jview.hasClass("hide") && (jspan = this.matchSpan(jclazz)) !== false) {
											$jview.removeClass("hide");
											dspan += jspan;// ispan要减去的列
											// 递归
											$jview = $jview.next("div.column");
											jclazz = $jview.attr("class");
										}
										if (dspan > 0) {
											$iview.attr("class", (iclazz = iclazz.replace("col-xs-" + ispan, "col-xs-" + (ispan - dspan))));
										}
									}
								},
								/**
								 * @method handleContextMenu
								 * @param data :
								 *            menu control data, etc
								 *            {copy:false}
								 * @param option :
								 *            selected element col-xs-xxx
								 */
								handleContextMenu : function(data, option) {
									var menus = $.component.contentMenu;
									for (var i = 0; i < menus.length; i++) {
										var menu = menus[i];
										if (data && $.type(data[menu.cmd]) === "boolean") {
											menu.disabled = !data[menu.cmd];
										}
									}
									this.$zone.contextmenu({
										menu : menus,
										taphold : true,
										preventSelect : true,
										delegate : ".lyrow .ui-selected",
										preventContextMenuForPopup : true,
										select : function(event, ui) {
										    var cmdEvent = $.Event(ui.cmd + '.editor.layout', option);
										    $.component.$zone.trigger(cmdEvent);
										    if (cmdEvent.isDefaultPrevented()) return
										    var $target = ui.target;
											switch (ui.cmd) {
											case "copy":
												CLIPBOARD = "";
												break
											case "paste":
												CLIPBOARD = "";
												break
											case "zoomin":
												$.component.cellSplit(option);
												break;
											case "zoomout":
												$.component.cellMerge(option);
												break;
											}
											// Optionally return false, to
											// prevent closing
										},
										beforeOpen : function(event, ui) {
										    var Event = $.Event('beforeOpen.editor.layout', event);
										    $.component.$zone.trigger(Event);
										    if (Event.isDefaultPrevented()) return
											var $menu = ui.menu, $target = ui.target, extraData = ui.extraData; // passed
											ui.menu.zIndex($(event.target).zIndex() + 1);
										}
									})
								},
								// handleContextMenu end

								// handleSelectable
								/**
								 * @method handleSelectable
								 * @param disabled :
								 *            disabled selectable
								 */
								handleSelectable : function(disabled) {
									this.$zone.selectable({
										// delay : 5,
										distance : 5,
										// 布局
										filter : '.column',
										// 拖动、配置、移除
										cancel : '.zone .drag, .zone .configuration, .zone .remove',
										// tolerance : "fit",
										disabled : disabled === true,
										start : function(event, ui) {
										},
										selected : function(event, ui) {
										},
										unselected : function(event, ui) {
										},
										stop : function(event, ui) {
											var selected = $(".ui-selected", this);
											return $.component.handleContextMenu({
												zoomin : true,
												zoomout : true
											}, selected);
										}
									});
									disabled === true && this.$zone.find(".ui-selected").removeClass("ui-selected") // unselected
								},
								// handleSelectable end
								/**
								 * @method bindGridGenerator 绑定布局录入事件
								 */
								bindGridGenerator : function() {
									// .grid-chooser gridpacker
									$("#estRows>.lyrow>.preview>input").not(".grid-chooser").bind("keyup", function() {
										var e = 0;
										var t = "";
										var grid = 12;
										var $this = $(this);
										var gval = $this.val();
										var n = $.trim(gval).split(" ", 12);
										$.each(n, function(n, r) {
											e = e + parseInt(r);
											t += '<div class="col-xs-' + r + ' column"></div>'
										});
										if (e < 0) {
											// 小于0按0布局
											t = '<div class="col-xs-0 column"></div>'
										} else if (e > grid) {
											// 超过12按12布局
											t = '<div class="col-xs-12 column"></div>'
										} else if (e < grid) {
											// 不够12添加占位符：
											// 1、在添加布局时,可根据所占列是否满足12列自动合并布局
											t += '<div class="col-xs-' + (grid - e) + ' column columnholder"></div>'
										}
										t = "<div class=\"row clearfix\"> " + t + "</div>";
										$this.parent().siblings(".view").html(t);
										$this.parent().siblings(".drag").show()
									})
								},
								/**
								 * @method bindConfigurationElm
								 *         delegate配置按钮(快捷)的事件
								 */
								bindConfigurationElm : function(e, t) {
									this.$zone.delegate(".configuration > a", "click", function(e) {
										e.preventDefault();
										var t = $(this).parent().next().next().children();
										$(this).toggleClass("active");
										t.toggleClass($(this).attr("rel"))
									});
									this.$zone.delegate(".configuration .dropdown-menu a", "click", function(e) {
										e.preventDefault();
										var t = $(this).parent().parent();
										var n = t.parent().parent().next().next().children();
										t.find("li").removeClass("active");
										$(this).parent().addClass("active");
										var r = "";
										t.find("a").each(function() {
											r += $(this).attr("rel") + " "
										});
										t.parent().removeClass("open");
										n.removeClass(r);
										n.addClass($(this).attr("rel"))
									})
								},

								/**
								 * @method clearDemo 清空布局
								 */
								clearDemo : function() {
									this.$zone.empty();
									this.layouthistory = null;
									if (this.support.storage) {
										localStorage.removeItem("layoutdata");
									}
								},
								/**
								 * @method bindRemoveElm delegate配置.remove的事件
								 */
								bindRemoveElm : function() {
									this.$zone.delegate(".remove", "click", function(e) {
										e.preventDefault();
										var $p = $(this).parent();
										var component = $.component;
										component.options.trashEnable && confirm("移到垃圾箱") ? component.deleteElement($p) : $p.remove();
										if (!component.$zone.find(".lyrow").length > 0) {
											component.clearDemo()
										}
									})
								},
								/**
								 * @method removeMenuClasses
								 */
								removeMenuClasses : function() {
									$("#menu-layoutit li button").removeClass("active")
								},
								/**
								 * @method changeStructure
								 * @param e
								 * @param t
								 */
								changeStructure : function(e, t) {
									this.$dlayout.find("." + e).removeClass(e).addClass(t)
								},
								/**
								 * @method cleanHtml
								 */
								cleanHtml : function(e) {
									$(e).parent().append($(e).children().html()).end().remove();
								},
								/**
								 * @method downloadLayoutSrc
								 */
								downloadLayoutSrc : function() {
									var e = "";
									var $dlayout = this.$dlayout;
									var cleanHtml = this.cleanHtml;
									var t = this.$dlayout.children().html(this.$zone.html());
									t.find(".preview, .configuration, .drag, .remove").remove();
									t.find(".lyrow").addClass("removeClean");
									t.find(".box-element").addClass("removeClean");
									t.find(".lyrow .lyrow .lyrow .lyrow .lyrow .removeClean").each(function() {
										cleanHtml(this)
									});
									t.find(".lyrow .lyrow .lyrow .lyrow .removeClean").each(function() {
										cleanHtml(this)
									});
									t.find(".lyrow .lyrow .lyrow .removeClean").each(function() {
										cleanHtml(this)
									});
									t.find(".lyrow .lyrow .removeClean").each(function() {
										cleanHtml(this)
									});
									t.find(".lyrow .removeClean").each(function() {
										cleanHtml(this)
									});
									t.find(".removeClean").each(function() {
										cleanHtml(this)
									});
									$dlayout.find(".column").removeClass("ui-sortable ui-selectee ui-selected");
									$dlayout.find(".row").removeClass("clearfix").children().removeClass("column");
									$dlayout.find(".box-holder").empty();// clear
									// context only placeholder
									var formatSrc = $.htmlClean($dlayout.html(), {
										format : true,
										allowedAttributes : [ [ "id" ], [ "name" ], [ "data-guid" ], [ "class" ], [ "data-toggle" ], [ "data-target" ], [ "data-parent" ], [ "role" ], [ "data-dismiss" ], [ "aria-labelledby" ], [ "aria-hidden" ],
												[ "data-slide-to" ], [ "data-slide" ] ]
									});
									$dlayout.html(formatSrc);
									this.webpage = formatSrc;
									var $dtextarea = $("#downloadModal textarea").empty().val(formatSrc);
									return formatSrc;
								},

								/**
								 * @method recoverAble
								 * @$item need recover $element
								 */
								recoverAble : function($item) {
									if ($item === null || typeof $item === "undefined") {
										return false;
									}
									// 隐藏的.column不可排序
									var connectToClazz = $item.hasClass("lyrow") ? ".zone" : ".column:not(:hidden)";
									$item.draggable({
										connectToSortable : connectToClazz,
										helper : "original",
										handle : ".drag",
										revert : "invalid",
										start : function(e, t) {
											if (!$.component.startdrag) {
												$.component.stopsave++;
											}
											$.component.startdrag = 1;
										},
										drag : function(e, t) {
											t.helper.width("400").height("125");
											// debugger;
											updateStateBar("还原 : x[" + t.position["left"] + "]y[" + t.position["top"] + "]")
										},
										stop : function(e, t) {
											t.helper.width("100%").height("100%");
											updateStateBar();
											var $element = t.helper.parents(".zone");// 查看是否在容器,还原的不更新状态
											if ($element && $element.length > 0) {
												t.helper.find("span.text-move").html("拖动");
											}
											if ($.component.stopsave > 0) {
												$.component.stopsave--;
											}
											$.component.startdrag = 0
										}
									});
								},
								/**
								 * @method deleteElement
								 * @$item $element to delete
								 */
								deleteElement : function($item) {
									var $list = this.$trash;
									$list.slideDown();// 默认一直展示回收站
									$item.attr("style", "").effect("transfer", {
										to : $list
									}, 500, function() {
										var $element = $item.find("span.text-move").html("还原");
										var pretext = $item.find(">.preview>input[type='text']").val() || $item.find(">.preview").html();
										// delete timestamp
										$item.find(">.preview").html(pretext).end().attr("title", new Date());
										$item.appendTo($list).fadeIn();// remove
										// and
										// appendTo
									});
									return this.recoverAble($item);
								},
								/**
								 * @method deleteElement
								 * @$item $element to recycle
								 */
								recycleElement : function($item) {
									$item.fadeOut(function() {
										var $list = $("ul", $trash).length ? $("ul", $trash) : $("<ul class='gallery ui-helper-reset'/>").appendTo($trash);
										$item.find("a.ui-icon-trash").remove();
										$item.append(recycle_icon).appendTo($list).fadeIn(function() {
											$item.animate({
												width : "48px"
											}).find("img").animate({
												height : "36px"
											});
										});
									});
								},
								/**
								 * @method restoreData
								 */
								restoreData : function() {
									// 还原配置选项数据
									var defaultOptions = null;
									if (this.support.storage) {
										defaultOptions = JSON.parse(localStorage.getItem("layoutoptions"));
									}
									this.applySetting(defaultOptions || this.defaultOptions());

									// 还原CMS配置数据
									var bean, pageUuid = $("input[id=page_uuid]").val();
									if (pageUuid && (bean = this.load(pageUuid)) && bean.definitionJson) {
										// debugger;
										// export definitionJson
										var definitionJson = this.definitionJson = $.parseJSON(bean.definitionJson);
										this.$zone.html(definitionJson.dhtml);
										// trash handler
										var recoverAble = this.recoverAble;
										this.$trash.html(definitionJson.thtml).slideDown().find("div.lyrow, div.box").each(function() {
											recoverAble($(this));
										});// 默认一直展示回收站

										// bind data to dom
										$.each(definitionJson.items || [], function() {
											// debugger
											var item = this;
											var selector = "[data-guid=" + item.gid + "]:first";
											$(selector).data("json", item);
										});
									} else if (this.support.storage) { 
										// offline style
										var layouthistory = this.layouthistory = (this.options.offline && JSON.parse(localStorage.getItem("layoutdata")));
										if (!layouthistory) {
											return false;
										}
										var demoHtml = this.zoneHtml = layouthistory.list[layouthistory.count - 1];
										if (demoHtml) {
											this.$zone.html(demoHtml);
										}
									}

								},
								/**
								 * @method initContainer
								 */
								initContainer : function() {
									this.$zone.find(".column").addBack().sortable({
										// 隐藏的.column不可排序
										connectWith : ".column:not(:hidden)",
										opacity : .35,
										handle : ".drag",
										start : function(event, ui) {
											if (!$.component.startdrag) {
												$.component.stopsave++;
											}
											$.component.startdrag = 1;
										},
										stop : function(event, ui) {
											updateStateBar();
											if ($.component.stopsave > 0) {
												$.component.stopsave--;
											}
											$.component.startdrag = 0;
										},
										sort : function(event, ui) {
											$(ui.placeholder).height("125");
											$(ui.item[0]).width("400").height("125");
											updateStateBar("移动 : x[" + ui.position["left"] + "]y[" + ui.position["top"] + "]")
										},
										receive : function(event, ui) {
											$(ui.item[0]).width("100%").height("100%");
										}
									});

									this.bindConfigurationElm();
								},
								/**
								 * @method initEmptyTrash
								 */
								initEmptyTrash : function() {
									// 垃圾清理
									var menus = [ {
										title : "清空 <kbd>Ctrl+D</kbd>",
										cmd : "empty",
										uiIcon : "ui-icon-scissors",
										disabled : true
									}, {
										title : "彻底删除 <kbd>Ctrl+D</kbd>",
										cmd : "delete",
										uiIcon : "ui-icon-scissors",
										disabled : true
									} ];
									var $trash = this.$trash;
									this.$trash.parent().contextmenu({
										delegate : ".nav-header, .box, .lyrow",
										preventContextMenuForPopup : true,
										preventSelect : true,
										taphold : true,
										menu : menus,
										select : function(event, ui) {
											var $target = ui.target;
											switch (ui.cmd) {
											case "empty":
												var total = $trash.find(">div").length;
												confirm("确认要清空共计[" + total + "]项内容?") && $("#elmTrash").empty();
												break
											case "delete":
												// debugger;
												var $parent = $($target).parents("div.ui-draggable"), parents;
												if ((parents = $parent.parent("#elmTrash")) && parents.length > 0) { // 删除一级目录
													// delete
													// date;
													var dt = $parent.attr("title") || "unknow date";
													// preview
													// data
													var preview = $parent.find(">div.preview").html() || "unknow data";
													confirm("确认要删除[" + preview + "](" + dt + ")?") && $parent.remove();
												}
												break
											}
										},
										beforeOpen : function(event, ui) {
											// debugger;
											var $menu = ui.menu, $target = ui.target, extraData = ui.extraData;
											var enableCommand = $target.hasClass("nav-header") ? "empty" : "delete";
											$trash.parent().contextmenu("enableEntry", "empty", enableCommand == "empty").contextmenu("enableEntry", "delete", !(enableCommand == "empty"));
											ui.menu.zIndex($(event.target).zIndex() + 1);
										}
									});
								},
								/**
								 * @method initGridPicker
								 */
								initGridPicker : function() {
									var rows = 10;
									var cols = 12;

									var grid = '<div class="grid">';
									for (var i = 0; i < rows; i++) {
										grid += '<div class="grid-row">';
										for (var c = 0; c < cols; c++) {
											grid += '<div class="square"><div class="inner"></div></div>';
										}
										grid += '</div>';
									}
									grid += '</div>';
									var $gridChooser = $('.grid-chooser');// FIXME 会选到编辑区的chooser如果有,mousedown会影响编辑区的布局
									var $grid = $(grid).height(rows * 14).width(cols * 14).insertAfter($gridChooser);
									$grid.find('.grid-row').css({
										height : 'calc(100%/' + rows + ')'
									});
									$grid.find('.square').css({
										width : 'calc(100%/' + cols + ')'
									});

									var $allSquares = $('.square');

									$grid.on('mouseover', '.square', function() {
										var $this = $(this);
										var col = $this.index() + 1;
										var row = $this.parent().index() + 1;
										$allSquares.removeClass('highlight');
										// console.log($('grid-row:nth-child(-n+'
										// + row + ')
										// .square:nth-child(-n+' + col
										// + ')').length);
										$('.grid-row:nth-child(-n+' + row + ') .square:nth-child(-n+' + col + ')').addClass('highlight');
										$gridChooser.attr("value", col + ' x ' + row).html(col + 'x' + row);
									}).on('mousedown', '.square', function(event) {
										var $this = $(this);
										var col = $this.index() + 1;
										var row = $this.parent().index() + 1;
										var html = "";
										var discols = [ 5, 7, 8, 9, 10, 11 ], colarr = [];
										// debugger;
										if ($.inArray(col, discols) > -1) {
											colarr = $.trim($.component.options["cols" + col]).split(" ", 12);
											if (colarr.length < col) {
												alert("请在首选项中配置" + col + "列默认分配");
												return false;
											}
										} else {
											for (var j = 0; j < col; j++) {
												colarr.push(12 / col);
											}
										}
										for (var i = 0; i < row; i++) {
											html += "<div class=\"row clearfix\">";
											// html += "<div class=\"row-fluid
											// clearfix\">";
											for (var j = 0; j < col; j++) {
												html += "<div class=\"col-xs-" + colarr[j] + " column\"/>"
											}
											html += "</div>"
										}
										var drop = $gridChooser.parent().siblings(".view").html(html).parent();
										// var widget =
										// drop.draggable("widget");
									});
								},
								/**
								 * @method restoreSetting
								 */
								restoreSetting : function() {
									this.applySetting(this.defaultOptions());
								},
								/**
								 * @method applySetting
								 * @options options to be applied
								 */
								applySetting : function(options) {
									this.options = $.extend(this.options, options);

									// 重新设置定时任务
									clearInterval(this.saveInterval);
									if (this.options.autoSaveInterval > 0) {
										var that = this;
										this.saveInterval = setInterval(function() {
											that.handleSaveLayout()
										}, this.options.autoSaveInterval);
									}

									$("#pageProperties").json2form(this.options, false);

									if (this.support.storage) {
										localStorage.setItem("layoutoptions", JSON.stringify(this.options));
									}
								},
								/**
								 * @method saveHtml
								 */
								saveHtml : function() {
									var save = $("button[id=saveModal]").hasClass("active");
									// var download =
									// $("button[id=downModal]").hasClass("active");
									var publishModel = $("button[id=publishModel]").hasClass("active");
									var webpage2 = '<!DOCTYPE html><html>\n<head><meta http-equiv="X-UA-Compatible" content="IE=Edge"><meta http-equiv="Content-Type" content="text/html; charset=utf-8"><meta http-equiv="Cache-Control" content="no-store"><meta http-equiv="Pragma" content="no-cache"><meta http-equiv="Expires" content="0">\n<script type="text/javascript" src="http://www.francescomalagrino.com/BootstrapPageGenerator/3/js/jquery-2.0.0.min.js"></script>\n<script type="text/javascript" src="http://www.francescomalagrino.com/BootstrapPageGenerator/3/js/jquery-ui"></script>\n<link href="http://www.francescomalagrino.com/BootstrapPageGenerator/3/css/bootstrap-combined.min.css" rel="stylesheet" media="screen">\n<script type="text/javascript" src="http://www.francescomalagrino.com/BootstrapPageGenerator/3/js/bootstrap.min.js"></script>\n</head>\n<body class="ui-wPage">\n'
											+ this.webpage + '\n</body>\n</html>'
									if (save === true || publishModel === true) {
										// debugger;
										var piUuid = $("input[id=pi_uuid]").val();
										var pageUuid = $("input[id=page_uuid]").val();
										var definitionJson = this.definitionJson || {};
										definitionJson.html = this.webpage;
										definitionJson.items = [];
										definitionJson["id"] = "home";
										definitionJson["uuid"] = pageUuid;
										definitionJson["wtype"] = "wLayoutit";
										definitionJson["title"] = $("#title").val();
										var callback = function() {
											var gid, bean, $this = $(this).children(":first");
											if ((gid = $this.attr("data-guid")) && (bean = $this.data("json"))) {
												var data = {};
												data["gid"] = gid;
												data["id"] = $this.attr("id");
												// TODO layoutit html
												data["wtype"] = $this.attr("wtype") || "wNone";
												data["title"] = data["gid"];
												$.extend(data, bean);
												definitionJson.items.push(data);
											}
										};
										this.$zone.find("div.box > div.view").each(callback);
										this.$trash.find("div.box > div.view").each(callback);
										// debugger;
										definitionJson.dhtml = this.$zone.html();
										definitionJson.thtml = this.$trash.html();
										server.JDS.call({
											service : "appPageDefinitionMgr.saveDefinitionJson",
											data : [ piUuid, JSON.stringify(definitionJson) ],
											async : false,
											success : function(result) {
												if (publishModel === false) {
													alert("保存成功！");
													window.location.reload();
												}
											}
										});
										var url = ctx + "/web/app/page/preview/" + piUuid;
										publishModel && window.open(url, "_blank"); // preview
									} else
									/*
									 * FM aka Vegetam Added the function that
									 * save the file in the directory Downloads.
									 * Work only to Chrome Firefox And IE
									 */
									if (navigator.appName == "Microsoft Internet Explorer" && window.ActiveXObject) {
										var locationFile = location.href.toString();
										var dlg = false;
										with (document) {
											ir = createElement('iframe');
											ir.id = 'ifr';
											ir.location = 'about.blank';
											ir.style.display = 'none';
											body.appendChild(ir);
											with (getElementById('ifr').contentWindow.document) {
												open("text/html", "replace");
												charset = "utf-8";
												write(webpage2);
												close();
												document.charset = "utf-8";
												dlg = execCommand('SaveAs', false, locationFile + "webpage.html");
											}
											return dlg;
										}
									} else {
										webpage2 = webpage2;
										var blob = new Blob([ webpage2 ], {
											type : "text/html;charset=utf-8"
										});
										saveAs(blob, "webpage.html");
									}
								},
								/**
								 * @method load
								 * @param pageUuid
								 *            pageUuid to be loaded
								 */
								load : function(pageUuid) {
									var bean = null;
									server.JDS.call({
										service : "appPageDefinitionMgr.getBean",
										data : [ pageUuid ],
										async : false,
										success : function(result) {
											bean = result.data;
										}
									});
									return bean;
								}
							});

			// function end

			// document ready
			$(document).ready(function() {

				var $zone = $(".zone");
				$.component.$zone = $zone;
				$.component.zoneHtml = $zone.html();
				var $trash = $("#elmTrash");
				$.component.$trash = $trash;
				var $dlayout = $("#download-layout");
				$.component.$dlayout = $dlayout;

				$(".zone .nav-tabs>.active>a[data-toggle=tab]:first").tab("show");

				$(window).resize(function() {
					$zone.css("min-height", $(window).height() - 90)
				}).trigger("resize")
			});
			return $.component;
		});