!function($) {
	var projectProcess = function(element, options) {
		this.options = options;
		this.$element = $(element);
		this.init();
	};
	projectProcess.prototype = {
		init : function() {
			var options = this.options;
			var $element = this.$element;
			var itemHeight = options.itemHeight;
			var itemWidth = options.itemWidth;
			var distance = options.distance;
			var data = options.data;
			var processes = data.data;
			var type = data.type;
			$element.html("");
			var html = "<div class='project'>";
			html += "<div class='project_name'><span class='projectName'>项目名称："+type+"</span></div>";
			html += "<div class='project_process'>";
			for (var i = 0; i < processes.length; i++) {
				var matters = processes[i].matter; 
				var beforeMatter = 0;
				var afterMatter = 0;
				for(var j = 0; j < matters.length; j++){
					if(matters[j].position=="after"){
						afterMatter++;
					}else{
						beforeMatter++;
					}
				}
				var stageHeight = 0;
				if(beforeMatter>afterMatter){
					stageHeight = beforeMatter*(itemHeight*2);
				}else{
					stageHeight = afterMatter*(itemHeight*2);
				}
				if(stageHeight==0){
					stageHeight = 100;
				}
				if(stageHeight<(100+itemHeight)){
					html += "<div class='project_process_child' style='height:"+(stageHeight+50+20)+"px;'>";
					//阶段
					html += "<div class='project_process_stage' style='height:"+(stageHeight+50)+"px;'>";
					html += "<div class='project_process_stage_name' style='background-color:"+options[processes[i].status]+";width:"+itemWidth+"px;height:"+itemHeight+"px;'>";
					html += "<span class='itemName index1'>"+processes[i].name+"</span>";
					html += "</div>";
					if(processes.length!=(i+1)){
						html += "<div class='project_process_stage_line' style='height:"+(stageHeight+50-itemHeight-20)+"px;'>";
						html += "<div class='project_process_stage_line_top' style='margin-top: 20px;height:"+(stageHeight-20-30)+"px;'></div>";
						html += "<div class='project_process_stage_line_bottom' style='height:30px;'></div>";
						html += "</div>";
					}
					html += "</div>";
				}else{
					html += "<div class='project_process_child' style='height:"+(stageHeight+20)+"px;'>";
					//阶段
					html += "<div class='project_process_stage' style='height:"+stageHeight+"px;'>";
					html += "<div class='project_process_stage_name' style='background-color:"+options[processes[i].status]+";width:"+itemWidth+"px;height:"+itemHeight+"px;'>";
					html += "<span class='itemName index1'>"+processes[i].name+"</span>";
					html += "</div>";
					if(processes.length!=(i+1)){
						html += "<div class='project_process_stage_line' style='height:"+(stageHeight-itemHeight-20)+"px;'>";
						html += "<div class='project_process_stage_line_top' style='margin-top: 20px;height:"+(stageHeight-itemHeight-20-30)+"px;'></div>";
						html += "<div class='project_process_stage_line_bottom' style='height:30px;'></div>";
						html += "</div>";
					}
					html += "</div>";
				}
				//项目
				var beforStr = "";
				var afterStr = "";
				var beforeCount = 1;
				var afterCount = 1;
				for(var j = 0; j < matters.length; j++){
					if(matters[j].position=="after"){
						afterStr += "<div class='project_process_matter_after_child'>";
						if(afterCount==1){
							if(beforeMatter==0){
								afterStr += "<div class='project_process_matter_after_child_line frist only' style='height:"+(2*itemHeight)+"px;'>";
							}else{
								if(afterMatter>1){
									afterStr += "<div class='project_process_matter_after_child_line frist nolast' style='height:"+(2*itemHeight)+"px;'>";
								}else{
									if(beforeMatter>afterMatter){
										afterStr += "<div class='project_process_matter_after_child_line frist last bAYa' style='height:"+(2*itemHeight)+"px;'>";
									}else{
										afterStr += "<div class='project_process_matter_after_child_line frist last' style='height:"+(2*itemHeight)+"px;'>";
									}
								}
							}
						}else if(afterCount==afterMatter){
							if(beforeMatter>afterMatter){
								afterStr += "<div class='project_process_matter_after_child_line last bAYa' style='height:"+(2*itemHeight)+"px;'>";
							}else{
								afterStr += "<div class='project_process_matter_after_child_line last' style='height:"+(2*itemHeight)+"px;'>";
							}
						}else{
							afterStr += "<div class='project_process_matter_after_child_line' style='height:"+(2*itemHeight)+"px;'>";
						}
						afterStr += "<div class='time2' style='text-align: center;margin-top: 30px;'>"+matters[j].time+"</div>";
						afterStr += "</div>";
						if(afterCount==afterMatter){
							afterStr += "<div class='project_process_matter_after_child_name'  style='background-color:"+options[matters[j].status]+";width:"+(itemWidth-5)+"px;height:"+(itemHeight-distance)+"px;margin-bottom:20px;'>";
						}else{
							afterStr += "<div class='project_process_matter_after_child_name'  style='background-color:"+options[matters[j].status]+";width:"+(itemWidth-5)+"px;height:"+(itemHeight-distance)+"px;margin-bottom: "+itemHeight+"px;'>";
						}
						afterStr += "<span class='itemName'>"+matters[j].name+"<br/><span class='matterName'>"+matters[j].matter+"</span></span>";
						afterStr += "</div>";
						afterStr += "</div>";
						afterCount++;
					}else{
						if(beforeMatter==1&&afterMatter==0){
							beforStr += "<div class='project_process_matter_before_child one' style='height:"+(2*itemHeight)+"px;'>";
							beforStr += "<div style='height:"+(itemHeight)+"px;' class='project_process_matter_before_child_line one'>";
						}else{
							if(beforeCount==1){
								beforStr += "<div class='project_process_matter_before_child frist' style='height:"+(2*itemHeight)+"px;'>";
								beforStr += "<div style='height:"+(itemHeight)+"px;' class='project_process_matter_before_child_line frist'>";
							}else if(beforeCount==beforeMatter){
								beforStr += "<div class='project_process_matter_before_child last' style='height:"+(2*itemHeight)+"px;'>";
								beforStr += "<div style='height:"+(itemHeight)+"px;' class='project_process_matter_before_child_line last'>";
							}else{
								beforStr += "<div class='project_process_matter_before_child' style='height:"+(2*itemHeight)+"px;'>";
								beforStr += "<div style='height:"+(itemHeight)+"px;' class='project_process_matter_before_child_line'>";
							}
							beforeCount++;
						}
						beforStr += "<div class='time' style='text-align: center;margin-top: 5px;'>"+matters[j].time+"</div>";
						beforStr += "</div>";
						beforStr += "<div class='project_process_matter_before_child_name' style='background-color:"+options[matters[j].status]+";width:"+(itemWidth-5)+"px;height:"+(itemHeight-distance)+"px;margin-bottom: "+distance+"px;'>";
						beforStr += "<span class='itemName'>"+matters[j].name+"<br/><span class='matterName'>"+matters[j].matter+"</span></span>";
						beforStr += "</div>";
						beforStr += "</div>";
					}
				}
				html += "<div class='project_process_matter_before' style='height:100%;'>"+beforStr+"</div>";
				if(beforeMatter==0){
					html += "<div class='project_process_matter_after' style='margin-left: 20px;'>"+afterStr+"</div>";
				}else{
					html += "<div class='project_process_matter_after' style='margin-top:"+itemHeight+"px;margin-left: 20px;'>"+afterStr+"</div>";
				}
				html += "</div>";
			}
			html += "</div>";
			
			html += "<div class='project_description'>";
			html += "<div class='project_description_content'>";
			html += "<div class='project_description_content_text'>注：</div>";
			html += "<div class='project_description_content_color' style='background-color:"+options.notDo+";'></div>";
			html += "<div class='project_description_content_text'>未办</div>";
			html += "<div class='project_description_content_color' style='background-color:"+options.dengji+";'></div>";
			html += "<div class='project_description_content_text'>登记</div>";
			html += "<div class='project_description_content_color' style='background-color:"+options.shouli+";'></div>";
			html += "<div class='project_description_content_text'>受理</div>";
			html += "<div class='project_description_content_color' style='background-color:"+options.bujian+";'></div>";
			html += "<div class='project_description_content_text'>补件</div>";
			html += "<div class='project_description_content_color' style='background-color:"+options.tuijian+";'></div>";
			html += "<div class='project_description_content_text'>退件</div>";
			html += "<div class='project_description_content_color' style='background-color:"+options.done+";'></div>";
			html += "<div class='project_description_content_text'>办结</div>";
			html += "</div>";
			html += "</div>";
			
			html += "</div>";
			$element.html(html);
			// 增加数据点击事件
			$(".icon", $element).click(function() {
				
			});
		}
	};

	/*
	 * CommercialRegister PLUGIN DEFINITION =======================
	 */
	
	$.fn.projectProcess = function(option) {
		return this.each(function() {
			var $this = $(this);
			var options = $.extend({}, $.fn.projectProcess.defaults,typeof option == 'object' && option);
			new projectProcess(this, options);
		});
	};

	$.fn.projectProcess.defaults = {
		itemHeight : 50,
		itemWidth : 195,
		notDo : "#F4E787",
		dengji : "#38A9C9",
		shouli : "#0C8D88",
		bujian : "#CE2840",
		tuijian : "#F2D321",
		done : "#0C8C51",
		distance : 0
	};
	
}(window.jQuery);