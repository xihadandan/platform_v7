(function($) {
	/* global jQuery, $ */
	(function() {
		var lastTime = 0;
		var vendors = [ 'webkit', 'moz' ];
		for (var x = 0; x < vendors.length && !window.requestAnimationFrame; ++x) {
			window.requestAnimationFrame = window[vendors[x] + 'RequestAnimationFrame'];
			window.cancelAnimationFrame = window[vendors[x] + 'CancelAnimationFrame']
					|| window[vendors[x] + 'CancelRequestAnimationFrame'];
		}

		if (!window.requestAnimationFrame) {
			window.requestAnimationFrame = function(callback, element) {
				var currTime = new Date().getTime();
				var timeToCall = Math.max(0, 16 - (currTime - lastTime));
				var id = window.setTimeout(function() {
					callback(currTime + timeToCall);
				}, timeToCall);
				lastTime = currTime + timeToCall;
				return id;
			};
		}
		if (!window.cancelAnimationFrame) {
			window.cancelAnimationFrame = function(id) {
				clearTimeout(id);
			};
		}
	}());
	var setGridWidth = $.fn.jqGrid.setGridWidth;
	$.jgrid.extend({
		setGridWidth : function(nwidth, shrink) {
			var self = this;
			var ret = setGridWidth.apply(self, arguments);
			self.trigger("resizeGridWidth");
			return ret;
		},
		setFrozenColumns : function() {
			return this.each(function() {
				if (!this.grid) {
					return;
				}
				var $t = this, cm = $t.p.colModel, i = 0, len = cm.length, maxfrozen = -1, frozen = false;
				// TODO treeGrid and grouping Support
				if ($t.p.subGrid === true || $t.p.treeGrid === true || $t.p.cellEdit === true || $t.p.sortable
						|| $t.p.scroll || $t.p.grouping) {
					return;
				}
				if ($t.p.rownumbers) {
					i++;
				}
				if ($t.p.multiselect) {
					i++;
				}

				// get the max index of frozen col
				while (i < len) {
					// from left, no breaking frozen
					if (cm[i].frozen === true) {
						frozen = true;
						maxfrozen = i;
					} else {
						break;
					}
					i++;
				}
				if (maxfrozen >= 0 && frozen) {
					// 
					var top = $t.p.caption ? $($t.grid.cDiv).outerHeight() : 0, hth = $(".ui-jqgrid-htable",
							"#gview_" + $.jgrid.jqID($t.p.id)).height();
					// headers
					if ($t.p.toppager) {
						top = top + $($t.grid.topDiv).outerHeight();
					}
					if ($t.p.toolbar[0] === true) {
						if ($t.p.toolbar[1] != "bottom") {
							top = top + $($t.grid.uDiv).outerHeight();
						}
					}

					$t.grid.fhDiv = $('<div style="position:absolute;left:0px;top:' + top + 'px;height:' + hth
							+ 'px;" class="frozen-div ui-state-default ui-jqgrid-hdiv"></div>');
					$t.grid.fbDiv = $('<div style="position:absolute;left:0px;top:'
							+ (parseInt(top, 10) + parseInt(hth, 10) + 1)
							+ 'px;overflow-y:hidden" class="frozen-bdiv ui-jqgrid-bdiv"></div>');
					$("#gview_" + $.jgrid.jqID($t.p.id)).append($t.grid.fhDiv);
					$($t).bind(
							'resizeGridWidth.setFrozenColumns',
							function(event) {
								$t.updateFrozenHeadersViewId && cancelAnimationFrame($t.updateFrozenHeadersViewId);
								$t.updateFrozenHeadersViewId = requestAnimationFrame(function(t) {
									var htbl = $(".ui-jqgrid-htable:not(.ui-jqgrid-frozen)",
											"#gview_" + $.jgrid.jqID($t.p.id)).clone(true);
									htbl.addClass("ui-jqgrid-frozen");
									// groupheader support - only if
									// useColSpanstyle is false
									if ($t.p.groupHeader) {
										$("tr.jqg-first-row-header, tr.jqg-third-row-header", htbl).each(function() {
											$("th:gt(" + maxfrozen + ")", this).remove();
										});
										var swapfroz = -1, fdel = -1;
										$("tr.jqg-second-row-header th", htbl).each(function() {
											var cs = parseInt($(this).attr("colspan"), 10);
											if (cs) {
												swapfroz = swapfroz + cs;
												fdel++;
											}
											if (swapfroz === maxfrozen) {
												return false;
											}
										});
										if (swapfroz !== maxfrozen) {
											fdel = maxfrozen;
										}
										$("tr.jqg-second-row-header", htbl).each(function() {
											$("th:gt(" + fdel + ")", this).remove();
										});
									} else {
										$("tr", htbl).each(function() {
											$("th:gt(" + maxfrozen + ")", this).remove();
										});
									}
									$(htbl).width(1);
									// resizing stuff
									$($t.grid.fhDiv).html("").append(htbl).mousemove(function(e) {
										if ($t.grid.resizing) {
											$t.grid.dragMove(e);
											return false;
										}
									});
									$t.updateFrozenHeadersViewId = null;
								});
							}).trigger("resizeGridWidth.setFrozenColumns");
					$($t).bind('jqGridResizeStop.setFrozenColumns', function(e, w, index) {
						var rhth = $(".ui-jqgrid-htable", $t.grid.fhDiv);
						$("th:eq(" + index + ")", rhth).width(w);
						var btd = $(".ui-jqgrid-btable", $t.grid.fbDiv);
						$("tr:first td:eq(" + index + ")", btd).width(w);
					});
					// sorting stuff
					$($t).bind(
							'jqGridOnSortCol.setFrozenColumns',
							function(index, idxcol) {
								var previousSelectedTh = $("tr.ui-jqgrid-labels:last th:eq(" + $t.p.lastsort + ")",
										$t.grid.fhDiv), newSelectedTh = $("tr.ui-jqgrid-labels:last th:eq(" + idxcol
										+ ")", $t.grid.fhDiv);
								$("span.ui-grid-ico-sort", previousSelectedTh).addClass('ui-state-disabled');
								$(previousSelectedTh).attr("aria-selected", "false");
								$("span.ui-icon-" + $t.p.sortorder, newSelectedTh).removeClass('ui-state-disabled');
								$(newSelectedTh).attr("aria-selected", "true");
								if (!$t.p.viewsortcols[0]) {
									if ($t.p.lastsort != idxcol) {
										$("span.s-ico", previousSelectedTh).hide();
										$("span.s-ico", newSelectedTh).show();
									}
								}
							});
					// data stuff
					// TODO support for setRowData
					$("#gview_" + $.jgrid.jqID($t.p.id)).append($t.grid.fbDiv);
					$($t.grid.bDiv).scroll(function() {
						$($t.grid.fbDiv).scrollTop($(this).scrollTop());
					});
					if ($t.p.hoverrows === true) {
						$("#" + $.jgrid.jqID($t.p.id)).unbind('mouseover').unbind('mouseout');
					}
					$($t).bind(
							'jqGridAfterGridCompleteInner.setFrozenColumns',
							function() {
								// var start = (new Date()).getTime();
								$("#" + $.jgrid.jqID($t.p.id) + "_frozen").remove();
								// $($t.grid.fbDiv).height($($t.grid.bDiv).height());
								$($t.grid.fbDiv)[0].style.height = $($t.grid.bDiv)[0].style.height;
								var btbl = $("#" + $.jgrid.jqID($t.p.id)).clone(true);
								$("tr", btbl).each(function(idx, element) {
									$("td:gt(" + maxfrozen + ")", this).remove();
									// 设置行高
									var typeHeight = $.browser.mozilla ? "height" : "outerHeight";
									$(this)[typeHeight]($("tr:eq(" + idx + ")", $t)[typeHeight]());
								});
								btbl[0].p = $t.p;
								$(btbl).width(1).attr("id", $t.p.id + "_frozen");
								$($t.grid.fbDiv).append(btbl);
								if ($t.p.hoverrows === true) {
									$("tr.jqgrow", btbl).hover(
											function() {
												$(this).addClass("ui-state-hover");
												$("#" + $.jgrid.jqID(this.id), "#" + $.jgrid.jqID($t.p.id)).addClass(
														"ui-state-hover");
											},
											function() {
												$(this).removeClass("ui-state-hover");
												$("#" + $.jgrid.jqID(this.id), "#" + $.jgrid.jqID($t.p.id))
														.removeClass("ui-state-hover");
											});
									$("tr.jqgrow", "#" + $.jgrid.jqID($t.p.id)).hover(
											function() {
												$(this).addClass("ui-state-hover");
												$("#" + $.jgrid.jqID(this.id), "#" + $.jgrid.jqID($t.p.id) + "_frozen")
														.addClass("ui-state-hover");
											},
											function() {
												$(this).removeClass("ui-state-hover");
												$("#" + $.jgrid.jqID(this.id), "#" + $.jgrid.jqID($t.p.id) + "_frozen")
														.removeClass("ui-state-hover");
											});
								}
								btbl = null;
								// console.log &&
								// console.log("jqGridAfterGridCompleteInner.setFrozenColumns:"
								// + ((new Date()).getTime() - start));
							});
					var updateFrozenColumnsView = function(event) {
						$t.updateFrozenColumnsViewId && cancelAnimationFrame($t.updateFrozenColumnsViewId);
						$t.updateFrozenColumnsViewId = requestAnimationFrame(function(t) {
							$($t).triggerHandler("jqGridAfterGridCompleteInner.setFrozenColumns", {
								custom : true
							});
							$t.updateFrozenColumnsViewId = null;
						});
					};
					$($t).bind({
						// 'blur.setFrozenColumns' : updateFrozenColumnsView,
						// 'focus.setFrozenColumns' : updateFrozenColumnsView,
						'keyup.setFrozenColumns' : updateFrozenColumnsView,
						'click.setFrozenColumns' : updateFrozenColumnsView,
						'jqGridAfterGridComplete.setFrozenColumns' : updateFrozenColumnsView,
						'updateFrozenColumnsView.setFrozenColumns' : updateFrozenColumnsView
					});
					$t.p.frozenColumns = true;
				}
			});
		},
		fixedHeader : function(scrollContainerSelector, topContainerSelector) {
			var $this = this;
			$this.each(function(idx, table) {
				var self = this;
				var options = self.p;
				var $scrollContainer = $(scrollContainerSelector);
				if (options.fixedHeader !== true || $scrollContainer.length < 1) {
					return;
				}
				var gird = self.grid;
				var $grid = $(table);
				var $cDiv = $(gird.cDiv);
				var $hDiv = $(gird.hDiv);
				var $fbDiv = $(self.grid.fbDiv);
				var $fhDiv = $(self.grid.fhDiv);
				var cOHeight = $cDiv.outerHeight();
				var headerHeight = $cDiv.height() + $hDiv.height();
				var cDivCss = {
					"top" : $cDiv.css("top"),
					"z-index" : $cDiv.css("z-index"),
					"position" : $cDiv.css("position")
				}, hDivCss = {
					"top" : $hDiv.css("top"),
					"z-index" : $hDiv.css("z-index"),
					"position" : $hDiv.css("position")
				}, bfDivCss, hfDivCss;
				// 收集固定列原始属性（要先设置固定列再设置固定表头）
				if (self.grid.fbDiv && self.grid.fhDiv) {
					bfDivCss = {
						"top" : $fbDiv.css("top")
					};
					hfDivCss = {
						"top" : $fhDiv.css("top"),
						"left" : $fhDiv.css("left"),
						"z-index" : $fhDiv.css("z-index"),
						"position" : $fhDiv.css("position")
					};
				}
				$scrollContainer.on("resize", function(event) {
					gird.resizeAnimationFrameId && cancelAnimationFrame(gird.resizeAnimationFrameId);
					gird.resizeAnimationFrameId = requestAnimationFrame(function(t) {
						$cDiv.innerWidth($hDiv.width());
						gird.resizeAnimationFrameId = null;
					});
				}).on("scroll", function(event) {
					gird.scrollAnimationFrameId && cancelAnimationFrame(gird.scrollAnimationFrameId);
					gird.scrollAnimationFrameId = requestAnimationFrame(function(t) {
						var $topContainer = $(topContainerSelector);
						if ($topContainer.length < 1) {
							return;
						}
						var topHeight = $topContainer.height();
						// offset
						var gridOffset = $grid.offset();
						var relateOffset = $topContainer.offset() || {top:0,left:0};
						// grid
						var gridTop = gridOffset.top;
						var gridBottom = gridOffset.top + $grid.height();
						// relate
						var relateTopOfBody = relateOffset.top + topHeight + headerHeight;
						if (relateTopOfBody > gridTop && relateTopOfBody < gridBottom) {
							var relateXOffsetOfPage = $topContainer.scrollTop() + topHeight;
							$cDiv.css({
								"top" : relateXOffsetOfPage + "px",
								"z-index" : "1",
								"position" : "fixed"
							});
							$hDiv.css({
								"top" : relateXOffsetOfPage + cOHeight + "px",
								"z-index" : "1",
								"position" : "fixed"
							});
							// 固定列
							if (bfDivCss && hfDivCss) {
								$fbDiv.css({
									"top" : "0px"
								});
								var oOffset = $hDiv.offset();
								$fhDiv.css({
									"top" : relateXOffsetOfPage + cOHeight + "px",
									"left" : oOffset.left + "0px",
									"z-index" : "1",
									"position" : "fixed"
								});
							}
						} else {
							// 固定列
							if (bfDivCss && hfDivCss) {
								$fbDiv.css(bfDivCss);
								$fhDiv.css(hfDivCss);
							}
							$cDiv.css(cDivCss);
							$hDiv.css(hDivCss);
						}
						gird.scrollAnimationFrameId = null;
					});
				}).trigger("resize");
			})
		}
	});
})(jQuery);
