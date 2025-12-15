!function($) {
	var CommercialRegister = function(element, options) {
		this.options = options;
		this.$element = $(element);
		this.init();
	};

	CommercialRegister.prototype = {
		constructor : CommercialRegister,
		init : function() {
			var options = this.options;
			var $element = this.$element;
			var itemHeight = options.itemHeight;
			var itemInterval = options.itemInterval;
			var data = options.data;
			var processes = data.data;
			var type = data.type;
			var maxDept = data.maxDept;
			$element.html("");
			var html = '<div class="ssdjb" style="height:' + (maxDept * itemHeight+30) + 'px">';
			for ( var i = 0; i < processes.length; i++) {
				var process = processes[i];
				html += '<div class="ssdjb_column column'+ (i+1) +'" style="width:'+itemInterval+'px;height:100%;float: left;">';
				for(var j = 0;j<process.length;j++){
					var tempJ = j+1;
					var index = process[j].index;
					var isFirst = process[j].isFirst;
					var childNum = process[j].childNum;
//					alert("childNum "+maxDept);
					var data3 = process[j].data;
					var content = "";
					if(data3==undefined){
						content = CommercialRegister.prototype.readObj(process[j]);
					}
					if(isFirst){//层级的第一个节点
						if(index==1){//一级的第一个
							var padding = (itemHeight*(maxDept-1))/2;
							html += '<div class="ssdjb_column_row" style="padding:'+padding+'px 0;width:100%;height:'+itemHeight+'px;">';
							html += '<div class="ssdjb_column_row_type">'+type+'</div>';
							html += '<div class="ssdjb_column_row_content one">'+content+'</div>';
							html += '</div>'; 
						}else if(index==2){//二级的第一个
							var padding = (itemHeight*(childNum-1))/2;
							if(tempJ==1){
								html += '<div class="ssdjb_column_row indexFirst rowFirst row'+(j+1)+'" style="padding:'+padding+'px 0;width:100%;height:'+itemHeight+'px;">';
								if(maxDept>1){
									html += '<div class="rowFirstDiv" style="height:'+(padding+itemHeight/2)+'px;width:40%;"></div>';
								}else{
									html += '<div class="rowFirstDiv2" style="height:'+(padding+itemHeight/2)+'px;width:40%;"></div>';
								}
							}else if(tempJ==process.length){
								html += '<div class="ssdjb_column_row indexFirst rowlast row'+(j+1)+'" style="padding:'+padding+'px 0;width:100%;height:'+itemHeight+'px;">';
								html += '<div class="rowLastDiv" style="height:'+(padding+itemHeight/2)+'px;width:100%;"></div>';
							}else{
								html += '<div class="ssdjb_column_row indexFirst row'+(j+1)+'" style="padding:'+padding+'px 0;width:100%;height:'+itemHeight+'px;">';
								html += '<div class="rowMiddleDiv" style="height:'+(padding+itemHeight/2)+'px;width:40%;"></div>';
							}
							html += '<div class="ssdjb_column_row_content">'+content+'</div>';
							html += '</div>'; 
						}
					}else{
						if(index==1){//一级 
							var padding = (itemHeight*(maxDept-1))/2;
							html += '<div class="ssdjb_column_row row'+(j+1)+'" style="padding:'+padding+'px 0;width:100%;height:'+itemHeight+'px;">';
							if(processes.length==2){
								html += '<div class="ssdjb_column_row_content ssdjb_column_row_content_last">'+content+'</div>';
							}else{
								html += '<div class="ssdjb_column_row_content">'+content+'</div>';
							}
							html += '</div>'; 
						}else if(index==2){//二级
							if(data3==undefined){
								var padding = (itemHeight*(childNum-1))/2;
								if(childNum>1){
									html += '<div class="ssdjb_column_row twoIndex haveChild row'+(j+1)+'" style="padding:'+padding+'px 0;width:100%;height:'+itemHeight+'px;">';
								}else{
									html += '<div class="ssdjb_column_row twoIndex row'+(j+1)+'" style="padding:'+padding+'px 0;width:100%;height:'+itemHeight+'px;">';
								}
								html += '<div class="ssdjb_column_row_content">'+content+'</div>';
								html += '</div>'; 
							}else{//三级
								for(var ij=0;ij<data3.length;ij++){
									var content2 = CommercialRegister.prototype.readObj(data3[ij]);
									var tempIj = ij+1;
									html += '<div class="ssdjb_column_row row'+(j+1)+'" style="width:100%;height:'+itemHeight+'px;">';
									if(data3[ij].isFirst){
										if(tempIj==1){
											html += '<div class="ssdjb_column_row_content indexFirst childRow'+ij+' rowFirst">'+content2+'</div>';
											if(tempIj==data3.length){
												html += '<div class="rowFirstDiv rowLastDiv" style="height:'+(itemHeight/2)+'px;width:40%;"></div>';
											}else{
												html += '<div class="rowFirstDiv" style="height:'+(itemHeight/2)+'px;width:40%;"></div>';
											}
										}else if(tempIj==data3.length){
											html += '<div class="ssdjb_column_row_content indexFirst childRow'+ij+' rowlast">'+content2+'</div>';
											html += '<div class="rowLastDiv" style="height:'+(itemHeight/2)+'px;width:100%;"></div>';
										}else{
											html += '<div class="ssdjb_column_row_content indexFirst childRow'+ij+'">'+content2+'</div>';
											html += '<div class="rowMiddleDiv" style="height:'+(itemHeight/2)+'px;width:40%;"></div>';
										}
									}else{
										html += '<div class="ssdjb_column_row_content childRow'+ij+'">'+content2+'</div>';
									}
									html += '</div>'; 
								}
							}
						}
					}
				} 
				html += '</div>'; 
			}
			html += '</div>';
			// alert(html);
			$element.html(html);

			// 增加数据点击事件
			$(".icon", $element).click(function() {
				var url = $(this).attr("url");
				if (url != 'undefined' && StringUtils.isNotBlank(url)) {
					window.open(url);
				}
			});
		},
		readObj : function(obj){
			var html = "";
			if (obj['task'] != null) {
				var time = obj['time'];
				var recVer = "";
				if(obj['recVer']!=undefined){
					recVer = "(" + obj['recVer'] + ")";
				}
				var userName = "";
				if(obj['userName']!=undefined) {
					userName = "(" + obj['userName'] +")";
				}
				var matter = "";
				if(obj['matter']!=undefined) {
					matter = "(" + obj['matter'] +")";
				}
				html += '<div class="task" title="' + obj['task'] + recVer + userName + '">' + obj['task'] + recVer + userName + matter + '</div>';
				if(time==null || time=="" || time==undefined){
					html += '<div class="arrow gary"></div>';
				}else{
					html += '<div class="arrow"></div>';
				}
				html += '<div class="iconDiv">';
				if(time==null || time=="" || time==undefined){
					if(obj['url']!=undefined){
						html += '<div class="icon gary point" url="' + obj['url'] + '">' + obj['code'] + '</div>';
					}else{
						html += '<div class="icon gary"  url="' + obj['url'] + '">' + obj['code'] + '</div>';
					}
				}else{
					if(obj['url']!=undefined){
						html += '<div class="icon point" url="' + obj['url'] + '">' + obj['code'] + '</div>';
					}else{
						html += '<div class="icon" url="' + obj['url'] + '">' + obj['code'] + '</div>';
					}
				}
				html += '</div>';
				if(time==null){
					time = "";
				}
				if(obj['cancel']!=null&&obj['cancel']!=undefined&&obj['cancel']!=""){
					html += '<div class="time">' + time + '</br>(于' + obj['cancel'] + '撤回)</div>';
				}else{
					if(obj['limitNum']!=null&&obj['limitNum']!=undefined&&obj['limitNum']!=""){
						html += '<div class="time red">' + time + '(' + obj['limitNum'] + '天)</div>';
					}else{
						html += '<div class="time">' + time + '</div>';
					}
				}
				
			}
			return html;
		}
	};

	/*
	 * CommercialRegister PLUGIN DEFINITION =======================
	 */
	$.fn.commercialRegister = function(option) {
		return this.each(function() {
			var $this = $(this);
			var data = $this.data('commercialRegister');
			var options = $.extend({}, $.fn.commercialRegister.defaults, $this.data(),
					typeof option == 'object' && option);
			if (!data) {
				$this.data('commercialRegister', (data = new CommercialRegister(this, options)));
			}
			if (typeof option == 'string') {
				data[option]();
			}
		});
	};

	$.fn.commercialRegister.defaults = {
		itemHeight : 80,
		itemInterval : 165
	};

	$.fn.commercialRegister.Constructor = CommercialRegister;
}(window.jQuery);