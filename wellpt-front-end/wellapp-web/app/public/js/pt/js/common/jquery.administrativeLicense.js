!function($) {
	var AdministrativeLicense = function(element, options) {
		this.options = options;
		this.$element = $(element);
		this.init();
	};

	AdministrativeLicense.prototype = {
		constructor : AdministrativeLicense,
		init : function() {
			var options = this.options;
			var $element = this.$element;
			var itemHeight = options.itemHeight;
			var data = options.data;
			var branchNumber = data.branchNumber;
			var processes = data.data;
			var type = data.type;
			var limitNum = data.limitNum;
			$element.html("");
			var html = '<div class="process" style="height:' + ((branchNumber * 70) + 30) + 'px">';
			var fork = false;
			for ( var i = 0; i < processes.length; i++) {
				var process = processes[i];
				var length = process.length;
				// 分支处理
				var startFork = false;
				if (branchNumber == length && branchNumber > 1 && fork === false) {
					// 分支高度
					var height = ((branchNumber - 1)) * itemHeight + 1;
					html += '<div class="fork" style="left:50px;height:' + height + 'px"></div>';
					fork = true;
					startFork = true;
				}
				if(i==0){
					html += '<div class="proce first">';
				}else{
					html += '<div class="proce">';
				}
				$.each(process, function(j) {
					// 处理item
					if (length == branchNumber) {
						if(i==0){
							html += '<div class="item"><div class="processType">'+type+'</div>';
						}else{
							html += '<div class="item">';
						}
					} else {
						var top = (branchNumber - 1) * (itemHeight / 2);
						if(i==0){
							html += '<div class="item" style="top: ' + top + 'px"><div class="processType">'+type+'</div>';
						}else{
							html += '<div class="item" style="top: ' + top + 'px">';
						}
					}

					if (this['task'] != null) {
						var time = this['time'];
						html += '<div class="task">' + this['task'] + '</div>';
						// 第一个结点不需要线条
						if (i != 0) {
							if (startFork == true) {
								if(time==null || time=="" || time==undefined){
									html += '<div class="arrow gary" style="left: 50px;"></div> ';
								}else{
									html += '<div class="arrow" style="left: 50px;"></div> ';
								}
							} else {
								if(time==null || time=="" || time==undefined){
									html += '<div class="arrow gary"></div>';
								}else{
									html += '<div class="arrow"></div>';
								}
							}
						}
						
						if(time==null || time=="" || time==undefined){
							if(this['url']!=undefined){
								html += '<div class="icon gary point" url="' + this['url'] + '">' + this['code'] + '</div>';
							}else{
								html += '<div class="icon gary"  url="' + this['url'] + '">' + this['code'] + '</div>';
							}
						}else{
							if(this['url']!=undefined){
								html += '<div class="icon point" url="' + this['url'] + '">' + this['code'] + '</div>';
							}else{
								html += '<div class="icon" url="' + this['url'] + '">' + this['code'] + '</div>';
							}
						}
						
						if(time==null){
							time = "";
						}
						if(this['time']!=undefined){
							time = time.substring(0, 16);
							if(this['task'] == '办结') {
								if(limitNum!=null&&limitNum!=undefined){
									html += '<div class="time red">' + time + '(' + limitNum + '天)</div>';
								} else {
									html += '<div class="time">' + time + '</div>';
								}
							}else{
								html += '<div class="time">' + time + '</div>';
							}
							
						} else {
							if(this['task'] == '办结') {
								if(limitNum!=null&&limitNum!=undefined){
									html += '<div class="time red">(' + limitNum + '天)</div>';
								} 
							}
						}
						
					}

					html += '</div>';
				});
				if (startFork === true) {
					startFork = false;
				}

				html += '</div>';
			}
			html += '</div>';
			$element.html(html);

			// 处理分支前的
			var $fork = $(".fork", $element);
			var $arrow = $(".arrow", $fork.prev());
			$arrow.css("width", ($arrow.width() + 50 + 20));

			// 增加数据点击事件
			$(".icon", $element).click(function() {
				var url = $(this).attr("url");
				if (url != 'undefined' && StringUtils.isNotBlank(url)) {
					window.open(url);
				}
			});
		}
	};

	/*
	 * CommercialRegister PLUGIN DEFINITION =======================
	 */

	$.fn.administrativeLicense = function(option) {
		return this.each(function() {
			var $this = $(this);
			var data = $this.data('administrativeLicense');
			var options = $.extend({}, $.fn.administrativeLicense.defaults, $this.data(),
					typeof option == 'object' && option);
			if (!data) {
				$this.data('administrativeLicense', (data = new AdministrativeLicense(this, options)));
			}
			if (typeof option == 'string') {
				data[option]();
			}
		});
	};

	$.fn.administrativeLicense.defaults = {
		itemHeight : 70,
		keyboard : true,
		show : true
	};

	$.fn.administrativeLicense.Constructor = AdministrativeLicense;
}(window.jQuery);