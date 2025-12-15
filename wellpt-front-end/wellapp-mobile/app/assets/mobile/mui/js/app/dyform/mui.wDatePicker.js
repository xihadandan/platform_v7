define([ "mui", "constant", "commons", "server", "mui-DyformField", "formBuilder", "mui-dtpicker" ], function($, constant, commons, server, DyformField, formBuilder) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	// 扩展DtPicker
	var WDtPicker = formBuilder.WDtPicker;
	// 表单日期
	var wDatePicker = function($placeHolder, options) {
		DyformField.apply(this, arguments);
	};
	commons.inherit(wDatePicker, DyformField, {
		init : function() {
			var that = this;
			that._superApply(arguments);
			var options = that.options.fieldDefinition;
			options.endDate = that.maxDate;
			options.beginDate = that.minDate;
			options.type = that.getType(options.contentFormat);
		},
		// 渲染
		render : function() {
			var that = this;
			that._superApply(arguments);
			var options = that.options.fieldDefinition;
			if (!options.readOnly) {
				that.$editableElem.each(function(i, element) {
					var fn = function(event) {
						var picker = $.data[element.getAttribute("data-dtpicker")];
						if (!picker) {
							var id = "wdate" + (++$.uuid);
							element.setAttribute("data-dtpicker", id);
							picker = $.data[id] = new $.WDtPicker(options);
						}
						var value = that.getValue();
						// 转日期,用于格式化
						if (!($.type(value) === "date")) {
							value = that.parseDate(value);
						}
						// 统一格式
						if (value && value.format) {
							value = value.format("yyyy-MM-dd HH:mm:ss")
						}
						picker.setSelectedValue(value);
						picker.show(function(rs) {
							var value = rs.text, date;
							if (value && (date = that.parseDate(value))) {
								var format = that.getFormat(options.contentFormat);
								value = date.format(format);
							}
							that.setValue(value);
						});
						$.focus(picker.ui.ok);
						return false;
					};
					that.fnDef = that.fnDef || function(event) {
						var self = this;
						self.blur();
						// event.preventDefault();
						// event.stopPropagation();
						setTimeout(function() {
							fn.call(self, event);
						}, 100);
						return false;
					}
					if (options.showIcon !== false) {
						element.classList.add("Wdate");
					}
					element.removeEventListener("tap", that.fnDef);
					element.addEventListener("tap", that.fnDef, true);
					element.setAttribute("style", "ime-mode:disabled");
					element.setAttribute("readonly", "readonly");
				})
			}

		},
		// 设置最小值
		setMinDate : function(minDate) {
			var that = this;
			return that.setDateScope({
				beginDate : minDate
			});
		},
		// 设置最大值
		setMaxDate : function(maxDate) {
			var that = this;
			return that.setDateScope({
				endDate : maxDate
			});
		},
		// 设置日期选择参数
		setDateScope : function(options) {
			var that = this;
			that.$editableElem.each(function(i, element) {
				options = $.extend(that.options.fieldDefinition, options)
				var id = element.getAttribute("data-dtpicker"), picker;
				if (id && (picker = $.data[id])) {
					picker.dispose();
					$.data[id] = new $.WDtPicker(options);
				}
			})
			return options;
		},
        getDisplayValue: function () {
            var that = this;
            if (that.$labelElem == null) {
                return that.getValue();
            }
            var value = that.getValue();
            if (value == null || !value.trim().length) {
                return '';
            }
            var options = that.options.fieldDefinition;
            var format = that.getFormat(options.contentFormat);
            var date = that.parseDate(value);
            if (date != null) {
                value = date.format(format);
            } else if(value) {
            	if(isNaN(new Date(value).getTime())) {
            		return value;
				}
                value = new Date(value).format(format);
			}
            return value;
		},
		/* 设值到标签中 */
		setValue2LabelElem : function() {
			var that = this;
			if (that.$labelElem == null) {
				return;
			}
			var value = that.getValue();
			if (value == null || value.trim().length == 0) {
				that.$labelElem.html("");
				return;
			}
			var options = that.options.fieldDefinition;
			var format = '';
			var fmt = options.contentFormat;
			format = that.getFormat(fmt);
			var date = that.parseDate(value);
			if (date != null) {
				value = date.format(format);
				that.$labelElem.html(value);
				that.$labelElem.attr("title", value);
			} else {
				this.$labelElem.html(value);
				this.$labelElem.attr("title", value);
			}
		},
		// 字符串转日期
		parseDate : function(str) {
			if (typeof str == 'string') {
				var results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) *$/);
				if (results && results.length > 3) {
					return new Date(parseInt(results[1], 10), parseInt(results[2], 10) - 1, parseInt(results[3], 10));
				}
				results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}) *$/);
				if (results && results.length > 5) {
					return new Date(parseInt(results[1], 10), parseInt(results[2], 10) - 1, parseInt(results[3], 10), parseInt(results[4], 10), parseInt(results[5], 10));
				}
				results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);
				if (results && results.length > 6) {
					return new Date(parseInt(results[1], 10), parseInt(results[2], 10) - 1, parseInt(results[3], 10), parseInt(results[4], 10), parseInt(results[5], 10), parseInt(results[6], 10));
				}
                results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2})[\.| ](\d{1,9}) *$/);
				if (results && results.length > 7) {
					return new Date(parseInt(results[1], 10), parseInt(results[2], 10) - 1, parseInt(results[3], 10), parseInt(results[4], 10), parseInt(results[5], 10), parseInt(results[6], 10), parseInt(results[7], 10));
				}
			}
			return null;
		},
		// 获取DtPicker的日期类型
		getType : function(contentFormat) {
			var format = '';
			var fmt = contentFormat;
			if (fmt == dyDateFmt.yearMonthDate) {
				format = 'date';
			} else if (fmt == dyDateFmt.dateTimeHour) {
				format = 'hour';
			} else if (fmt == dyDateFmt.dateTimeMin) {
				format = 'datetime';
			} else if (fmt == dyDateFmt.dateTimeSec) {
				format = 'datetime';
			} else if (fmt == dyDateFmt.timeHour) {
				format = 'time';
			} else if (fmt == dyDateFmt.timeMin) {
				format = 'time';
			} else if (fmt == dyDateFmt.timeSec) {
				format = 'time';
			} else if (fmt == dyDateFmt.yearMonthDateCn) {
				format = 'date';
			} else if (fmt == dyDateFmt.yearCn) {
				format = 'month';
			} else if (fmt == dyDateFmt.yearMonthCn) {
				format = 'month';
			} else if (fmt == dyDateFmt.monthDateCn) {
				format = 'date';
			} else if (fmt == dyDateFmt.year) {
				format = 'month';
			} else if (fmt == dyDateFmt.yearMonth) {
                format = 'month';
            }
			return format;
		},
		// 获取日期格式
		getFormat : function(contentFormat) {
			var format = '';
			var fmt = contentFormat;
			if (fmt == dyDateFmt.yearMonthDate) {
				format = 'yyyy-MM-dd';
			} else if (fmt == dyDateFmt.dateTimeHour) {
				format = 'yyyy-MM-dd HH';
			} else if (fmt == dyDateFmt.dateTimeMin) {
				format = 'yyyy-MM-dd HH:mm';
			} else if (fmt == dyDateFmt.dateTimeSec) {
				format = 'yyyy-MM-dd HH:mm:ss';
			} else if (fmt == dyDateFmt.timeHour) {
				format = 'HH';
			} else if (fmt == dyDateFmt.timeMin) {
				format = 'HH:mm';
			} else if (fmt == dyDateFmt.timeSec) {
				format = 'HH:mm:ss';
			} else if (fmt == dyDateFmt.yearMonthDateCn) {
				format = 'yyyy年MM月dd日';
			} else if (fmt == dyDateFmt.yearCn) {
				format = 'yyyy年';
			} else if (fmt == dyDateFmt.yearMonthCn) {
				format = 'yyyy年MM月';
			} else if (fmt == dyDateFmt.monthDateCn) {
				format = 'MM月dd日';
			} else if (fmt == dyDateFmt.year) {
				format = 'yyyy';
			} else if (fmt == dyDateFmt.yearMonth) {
                format = 'yyyy-MM';
            }
			return format;
		}
	});
	return wDatePicker;
});