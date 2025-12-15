define([ "mui", "commons", "server", "appContext", "appModal","formBuilder","mui-echarts"], function(
		$, commons, server, appContext, appModal,formBuilder) {
	function showCharts(dom){
		var myChart = echarts.init(dom);
		var app = {};
		option = null;
		app.title = '环形图';

		option = {
		    tooltip: {
		        trigger: 'item',
		        formatter: "{a} <br/>{b}: {c} ({d}%)"
		    },
		    legend: {
		        orient: 'vertical',
		        x: 'left',
		        data:['直接访问','邮件营销','联盟广告','视频广告','搜索引擎']
		    },
		    series: [
		        {
		            name:'访问来源',
		            type:'pie',
		            radius: ['50%', '70%'],
		            avoidLabelOverlap: false,
		            label: {
		                normal: {
		                    show: false,
		                    position: 'center'
		                },
		                emphasis: {
		                    show: true,
		                    textStyle: {
		                        fontSize: '30',
		                        fontWeight: 'bold'
		                    }
		                }
		            },
		            labelLine: {
		                normal: {
		                    show: false
		                }
		            },
		            data:[
		                {value:335, name:'直接访问'},
		                {value:310, name:'邮件营销'},
		                {value:234, name:'联盟广告'},
		                {value:135, name:'视频广告'},
		                {value:1548, name:'搜索引擎'}
		            ]
		        }
		    ]
		};
		;
		if (option && typeof option === "object") {
		    myChart.setOption(option, true);
		}
		return myChart;
	}	
	function initPage(){
		$.ajax({
			url:ctx+"/mobile/mui/document-statistics/document-statistics.html",
			async:false,
			success:function(content) {
				 var $formPanel = formBuilder.showPanel({
						title : "文件统计",
						actionBack : {
							showNavTitle : true,
						},
						moreNum:5,
		                content:content,
		                container:"#statistic_box"
		            });
				 showCharts($("#container")[0]);
			},
			error : function(jqXHR) {
			}
		});
	}
	initPage(); 	
	
});