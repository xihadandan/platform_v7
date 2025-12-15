;
(function($) { 
	/**
	 * 
	 */
	$.fn.initResourceBroad = function(){
		
		/*
		//日期格式转换函数,将日期转化为相应的格式
		Date.prototype.format =function(format)
		{
			var o = {
			"M+" : this.getMonth()+1, //month
			"d+" : this.getDate(), //day
			"h+" : this.getHours(), //hour
			"m+" : this.getMinutes(), //minute
			"s+" : this.getSeconds(), //second
			"q+" : Math.floor((this.getMonth()+3)/3), //quarter
			"S" : this.getMilliseconds() //millisecond
			}
			if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
			(this.getFullYear()+"").substr(4- RegExp.$1.length));
			for(var k in o)if(new RegExp("("+ k +")").test(format))
			format = format.replace(RegExp.$1,
			RegExp.$1.length==1? o[k] :
			("00"+ o[k]).substr((""+ o[k]).length));
			return format;
		}*/
		
		showPage(new Date(),"");
		
		//获得当前的日期(字符串类型)
		function getCurStrDate(){
			var curDate = new Date();			
			var rdate = curDate.format("yyyy-MM-dd");
			return rdate;
		}
		
		//字符串类型转化为日期类型
		function strParseDate(dateStr){
			var strTime = dateStr; //字符串日期格式             
			var date = new Date(Date.parse(strTime.replace(/-/g,   "/"))); //转换成Data();
			return date;
		
		}
		
		//获得当前的日期(日期类型)
		function getCurDate(){
			var curDate = new Date();					
			return curDate;
		}
		
		//根据当前日期获得周一时间(字符串类型)
		function getMondayByCurdate(date){
			var now = date;
			var a = new Date(now-(now.getDay()-1)*86400000);
			var rdate = a.format("yyyy-MM-dd");
			return rdate;
		}
		
		//根据当前日期获得周日时间(字符串类型)
		function getSundayByCurdate(theDate){
			var lastDateOfWeek;
			theDate.setDate(theDate.getDate() + 7 - theDate.getDay());
			lastDateOfWeek = theDate;
			return lastDateOfWeek.format("yyyy-MM-dd");
		}
		
		//第二中方法,得到周一日期(字符串类型)
		function getFirstDateOfWeek(theDate){
			var firstDateOfWeek;
			theDate.setDate(theDate.getDate() - theDate.getDay() + 1); //  
			firstDateOfWeek = theDate;
			//alert(firstDateOfWeek.format("yyyy-MM-dd"));
			return firstDateOfWeek.format("yyyy-MM-dd"); 
		}
		
		//根据当前日期获得周一至周末的时间({['星期一','2014-08-04'],['星期二','2014-08-05'],['星期三','2014-08-06'] ...})
		function getWeekInfo(curDate){
			var arr1 = new Array('星期一','星期二','星期三','星期四','星期五','星期六','星期日');
			var arr2 = new Array(7);
			
			for(var i=0;i<7;i++){
				var arr3 = new Array(2);
				var myDate = getMondayByCurdate(curDate);
				var myDate = strParseDate(myDate);//转化为日期格式
				myDate.setDate(myDate.getDate() + i);
				var tempDate = myDate.format("yyyy-MM-dd");
				arr3[0] = tempDate;
				arr3[1] = arr1[i];
				arr2[i] = arr3;
			}
			return arr2;
		}
		//测试代码,循环遍历每个['2014-08-02','周一'];
		function printWeekInfo(arr){
			for(var i=0;i<arr.length;i++){
				alert(arr[i]);
			}
		}
		
		//测试代码,循环遍历每个JSon对象
		function printJson(jsonData){
			for(var i=0;i<jsonData.length;i++){
				
				alert(jsonData[i].UUID);
				alert(jsonData[i].resName);
				jsonData[i].timeEmploy[0].startTime.substring(8,10);
				var temp = jsonData[i].timeEmploy[0].startTime.substring(0,10);
				alert(temp);
				var tempDate = strParseDate(temp);
				alert(tempDate.getDay());
				alert(jsonData[i].timeEmploy[1].startTime);
			}
		}
		
		//上一周点击事件
		$(".s_prev_day").live("click",function(){
			var ldate = $(this).attr("ldate");
			var newDate = dicDayForDate(strParseDate(ldate),7);
			//请求页面
			showPage(strParseDate(newDate),"pre_day");
			}
		);
		
		
		//下一周点击事件
		$(".s_next_day").live("click",function(){
			var ldate = $(this).attr("ldate");
			var newDate = addDayForDate(strParseDate(ldate),1);
			showPage(strParseDate(newDate),"next_day");
			}
		);
		
		//对日期加x天(字符串类型)
		function addDayForDate(date,num){
			var now = date;
			var a = new Date((now/1000 + 86400)*1000);
			var rdate = a.format("yyyy-MM-dd");
			return rdate;
		}
		
		//对日期减X天(字符串类型)
		function dicDayForDate(date,num){
			var now = date;
			var a = new Date(now - num*86400000);
			var rdate = a.format("yyyy-MM-dd");
			return rdate;
		}
		
		/**
		 * @param ldate:一周的起始时间 
		 * @param isToday:点击上周,isToday为"pre_day";点击下周,isToday为"next_day";
		 */
		function showPage(ldate,isToday){
			var html = "";
			var formatDate = "";
			if(ldate != ""){
				formatDate = ldate.format("yyyy-MM-dd");
			}
			
			
			//向后台发起请求,获取数据
			var url = "";
			url = ctx + '/meeting/meeting_board_test.action?ladte=' + formatDate;
			$.ajax({
				type : "post",
				url : url,
				success : function(data) {
					//获得后台的Json数据
					var finalJson ={
							"start":"date/yearMonth",
							"dateCssList":[{"date":"2014-08-03","css":"style里具体CSS代码"},{"date":"2014-08-04","css":"style里具体CSS代码"}],
							"resList":
							[
							{
							"UUID":"8c8875d7-0ada-41c8-a462-00bb5df65ada",
							"resName":"亚洲议室",
							"css":".xxCss",
							"timeEmploy":
								[{"startTime":"2014-08-04 02:00:00","endTime":"2014-08-04 04:00:00","title":"会议模块讨论","url":"www.baidu.com","css":".xxCss","clickJS":"点击要执行的JS"},
								 {"startTime":"2014-08-04 06:00:00","endTime":"2014-08-04 08:00:00","title":"资源看板模块讨论","url":"www.google.com","css":".xxCss","clickJS":"点击要执行的JS"}
								]
							},
							
							{
								"UUID":"8c8875d7-0ada-41c8-a462-00bb5df65ada",
								"resName":"小会议室",
								"css":".xxCss",
								"timeEmploy":
									[{"startTime":"2014-08-06 04:00:00","endTime":"2014-08-06 06:00:00","title":"会议模块讨论","url":"www.baidu.com","css":".xxCss","clickJS":"点击要执行的JS"},
									 {"startTime":"2014-08-06 06:00:00","endTime":"2014-08-06 08:00:00","title":"资源看板模块讨论","url":"www.google.com","css":".xxCss","clickJS":"点击要执行的JS"}
									]
							},
							{
								"UUID":"8c8875d7-0ada-41c8-a462-00bb5df65ada",
								"resName":"非洲会议室",
								"css":".xxCss",
								"timeEmploy":
									[{"startTime":"2014-08-05 04:00:00","endTime":"2014-08-05 06:00:00","title":"会议模块讨论","url":"www.baidu.com","css":".xxCss","clickJS":"点击要执行的JS"},
									 {"startTime":"2014-08-05 06:00:00","endTime":"2014-08-05 08:00:00","title":"资源看板模块讨论","url":"www.google.com","css":".xxCss","clickJS":"点击要执行的JS"}
									]
							},
							
							{
								"UUID":"8c8875d7-0ada-41c8-a462-00bb5df65ada",
								"resName":"欧洲会议室",
								"css":".xxCss",
								"timeEmploy":""
									
							},
							
							],
							
							
							"resTypeID":"MEET_RESOURCE",
							"interfaceName":"a"
					};
					//取出时间占用的列表
					var jsonData = finalJson.resList;
					//如果是date的话，取出的是一周的开始时间，如果是yearMonth，取出的是具体的年月
					var start = finalJson.start;
					//资源的类别Id
					var resTypeID = finalJson.resTypeID;
					//取出资源接口名称
					var interfaceName = finalJson.interfaceName;
					
					//得到当前日期所在的周一日期
					var curDateOfMonday = getFirstDateOfWeek(ldate);
					//得到当前日期所在的周末日期
					var curDateOfSunday = getSundayByCurdate(ldate);
					
					//--------------start 拼接html ---------------------------------
					html = html + "<div class='ui-dialog-content ui-widget-content' style='padding: 0px; margin: 0px; width: auto; min-height: 0px; height: 860px;' scrolltop='0' scrollleft='0'>";
					htm = html + "<div class='dialogcontent'>";

					html = html + "<div id='container' class='schedule_group_list schedule_css'>";
					htm = html + "<div class='content'>";
					
					html = html	+ "<div class='toolbar'>";
					html = html + "<table>";
					html = html + "<tbody>";
					html = html + "<tr>";
					html = html + "<td align='left'>";
					html = html + "<a class='s_prev_day' href='#' ldate='"+ curDateOfMonday + "'></a>";
					html = html + "<span class='s_taday' name='firstday' size='7' type='text'>" +  curDateOfMonday + "</span>";
					html = html + "<a class='s_next_day' href='#' ldate='" + curDateOfSunday + "'></a>";
					html = html + "</td>";
					html = html + "</tr>";
					html = html + "</tbody>";
					html = html + "</table>";
					html = html + "</div>";
					
					
					html = html + "<div class='schedule_leader_list_content_div'>";
					html = html + "<table class='schedule_leader_list_content' width='100%' height='100%' border='2'>";
					
					html = html + "<tr class='tr_title'>";
					html = html + "<td width='16%' align='center' >会议室</td>";
					
					//获得一周的信息
					var weekInfo = getWeekInfo(strParseDate(curDateOfMonday));
					
					for(var i=0;i<weekInfo.length;i++){
						var dayAndWeek = weekInfo[i];
						var day = dayAndWeek[0];//day :星期一
						var week = dayAndWeek[1];//week :2014-08-05 
						//TODO 判断当前日期和是否等于取出的日期，如果相等的话，字体为红色。
						if(day == new Date().format("yyyy-MM-dd")){
							html = html + "<td align='center' width='12%'><font color='red'>" + week + "(" + day.substring(8,10) + ")" + "</font></td>";
						}else{
							html = html + "<td align='center' width='12%'>" + week + "(" + day.substring(8,10) + ")" + "</td>";
						}
					}
					html = html + "</tr>";
					
					for(var i=0;i<jsonData.length;i++){
						
						html = html + "<tr class='tr_content' style=''>";
						html = html + "<td height='100' class='tr_content_l1' width='16%' align='center'>" + jsonData[i].resName + "</td>";
						html = html + "<td class='tr_content_td' height='100' width='12%'  id='Mondy'><div style='width: 100px; overflow: hidden; display: inline; border: 1px; font-size: 12px;'>";
						for(var j=0;j<jsonData[i].timeEmploy.length;j++){
							if(strParseDate(jsonData[i].timeEmploy[j].startTime.substring(0,10)).getDay()== 1){
								html = html + "<div><a>" + jsonData[i].timeEmploy[j].startTime.substring(11,16) + ":" + jsonData[i].timeEmploy[j].endTime.substring(11,16) + "-" + jsonData[i].timeEmploy[j].title + "</a></div>";
							}
						}
						html = html + "</div></td>"
						
						html = html + "<td class='tr_content_td' height='100' width='12%'  id='Tuesday'><div style='width: 100px; overflow: hidden; display: inline; border: 1px; font-size: 12px;'>";
						for(var j=0;j<jsonData[i].timeEmploy.length;j++){
							if(strParseDate(jsonData[i].timeEmploy[j].startTime.substring(0,10)).getDay()== 2){
								html = html + "<div><a>" + jsonData[i].timeEmploy[j].startTime.substring(11,16) + ":" + jsonData[i].timeEmploy[j].endTime.substring(11,16) + "-" + jsonData[i].timeEmploy[j].title + "</a></div>";
							}
						}
						
						html = html + "</div></td>"
						
						html = html + "<td class='tr_content_td' height='100' width='12%' id='Wednesday'><div style='width: 100px; overflow: hidden; display: inline; border: 1px; font-size: 12px;'>";
						for(var j=0;j<jsonData[i].timeEmploy.length;j++){
							if(strParseDate(jsonData[i].timeEmploy[j].startTime.substring(0,10)).getDay()== 3){
								html = html + "<div><a>" + jsonData[i].timeEmploy[j].startTime.substring(11,16) + ":" + jsonData[i].timeEmploy[j].endTime.substring(11,16) + "-" + jsonData[i].timeEmploy[j].title + "</a></div>";
							}
						}
						
						html = html + "</div></td>"
						
						html = html + "<td class='tr_content_td' height='100' width='12%'  id='Thursday'><div style='width: 100px; overflow: hidden; display: inline; border: 1px; font-size: 12px;'>";
						for(var j=0;j<jsonData[i].timeEmploy.length;j++){
							if(strParseDate(jsonData[i].timeEmploy[j].startTime.substring(0,10)).getDay()== 4){
								html = html + "<div><a>" + jsonData[i].timeEmploy[j].startTime.substring(11,16) + ":" + jsonData[i].timeEmploy[j].endTime.substring(11,16) + "-" + jsonData[i].timeEmploy[j].title + "</a></div>";
							}
						}
						
						html = html + "</div></td>"
						
						html = html + "<td class='tr_content_td' height='100' width='12%'  id='Friday'><div style='width: 100px; overflow: hidden; display: inline; border: 1px; font-size: 12px;'>";
						for(var j=0;j<jsonData[i].timeEmploy.length;j++){
							if(strParseDate(jsonData[i].timeEmploy[j].startTime.substring(0,10)).getDay()== 5){
								html = html + "<div><a>" + jsonData[i].timeEmploy[j].startTime.substring(11,16) + ":" + jsonData[i].timeEmploy[j].endTime.substring(11,16) + "-" + jsonData[i].timeEmploy[j].title + "</a></div>";
							}
						}
						
						html = html + "</div></td>"
						
						html = html + "<td class='tr_content_td' height='100' width='12%' id='Saturday'><div style='width: 100px; overflow: hidden; display: inline; border: 1px; font-size: 12px;'>";
						for(var j=0;j<jsonData[i].timeEmploy.length;j++){
							if(strParseDate(jsonData[i].timeEmploy[j].startTime.substring(0,10)).getDay()== 6){
								html = html + "<div><a>" + jsonData[i].timeEmploy[j].startTime.substring(11,16) + ":" + jsonData[i].timeEmploy[j].endTime.substring(11,16) + "-" + jsonData[i].timeEmploy[j].title + "</a></div>";
							}
						}
						
						html = html + "</div></td>"
						
						html = html + "<td class='tr_content_td' height='100' width='12%' id='Sunday'><div style='width: 100px; overflow: hidden; display: inline; border: 1px; font-size: 12px;'>";
						for(var j=0;j<jsonData[i].timeEmploy.length;j++){
							if(strParseDate(jsonData[i].timeEmploy[j].startTime.substring(0,10)).getDay()== 7){
								html = html + "<div><a>" + jsonData[i].timeEmploy[j].startTime.substring(11,16) + ":" + jsonData[i].timeEmploy[j].endTime.substring(11,16) + "-" + jsonData[i].timeEmploy[j].title + "</a></div>";
							}
						}
						
						html = html + "</div></td>"
						html = html + "</tr>";
					}

					html = html + "</table>";
					html = html + "</div>";
					html = html + "<div class='view_foot'></div>";
					
					html = html + "</div>";
					
					
					html = html + "</div>";
					
					
					
					html = html + "</div>";
					html = html + "</div>";
					html = html + "</div>";
					//-----------------------end 拼接html-------------------------------------
					var json = "";
					json = {title:"资源占用看板",closeOnEscape: true,draggable: true,resizable: true,height: 600,width: 1000,content: html,defaultBtnName:"关闭"};
					if(isToday == ""){
						//弹出对话框
						showDialog(json);
					}else{
						//如果是下周或上周，直接把html嵌入相应的<div>中
						$(".dialogcontent").html(html);
					}
					
				}
			});

		}
		
		
	}
	
})(jQuery);