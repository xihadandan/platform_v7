/**
   * 日历插件，BY Alen 2016.9.7  解决了左右滚动太灵敏的问题
   * 注意，该插件仅适用于基于mui的webapp项目，其他web项目若想使用该插件请导入mui.js
   * @param {Object} $
   * @param {Object} window
   */

/*添加农历和节气和节日*/

var lunarInfo=new Array(
    0x04bd8,0x04ae0,0x0a570,0x054d5,0x0d260,0x0d950,0x16554,0x056a0,0x09ad0,0x055d2,
    0x04ae0,0x0a5b6,0x0a4d0,0x0d250,0x1d255,0x0b540,0x0d6a0,0x0ada2,0x095b0,0x14977,
    0x04970,0x0a4b0,0x0b4b5,0x06a50,0x06d40,0x1ab54,0x02b60,0x09570,0x052f2,0x04970,
    0x06566,0x0d4a0,0x0ea50,0x06e95,0x05ad0,0x02b60,0x186e3,0x092e0,0x1c8d7,0x0c950,
    0x0d4a0,0x1d8a6,0x0b550,0x056a0,0x1a5b4,0x025d0,0x092d0,0x0d2b2,0x0a950,0x0b557,
    0x06ca0,0x0b550,0x15355,0x04da0,0x0a5d0,0x14573,0x052b0,0x0a9a8,0x0e950,0x06aa0,
    0x0aea6,0x0ab50,0x04b60,0x0aae4,0x0a570,0x05260,0x0f263,0x0d950,0x05b57,0x056a0,
    0x096d0,0x04dd5,0x04ad0,0x0a4d0,0x0d4d4,0x0d250,0x0d558,0x0b540,0x0b5a0,0x195a6,
    0x095b0,0x049b0,0x0a974,0x0a4b0,0x0b27a,0x06a50,0x06d40,0x0af46,0x0ab60,0x09570,
    0x04af5,0x04970,0x064b0,0x074a3,0x0ea50,0x06b58,0x055c0,0x0ab60,0x096d5,0x092e0,
    0x0c960,0x0d954,0x0d4a0,0x0da50,0x07552,0x056a0,0x0abb7,0x025d0,0x092d0,0x0cab5,
    0x0a950,0x0b4a0,0x0baa4,0x0ad50,0x055d9,0x04ba0,0x0a5b0,0x15176,0x052b0,0x0a930,
    0x07954,0x06aa0,0x0ad50,0x05b52,0x04b60,0x0a6e6,0x0a4e0,0x0d260,0x0ea65,0x0d530,
    0x05aa0,0x076a3,0x096d0,0x04bd7,0x04ad0,0x0a4d0,0x1d0b6,0x0d250,0x0d520,0x0dd45,
    0x0b5a0,0x056d0,0x055b2,0x049b0,0x0a577,0x0a4b0,0x0aa50,0x1b255,0x06d20,0x0ada0,
    0x14b63);
    
    var solarMonth=new Array(31,28,31,30,31,30,31,31,30,31,30,31);
    var Gan=new Array("甲","乙","丙","丁","戊","己","庚","辛","壬","癸");
    var Zhi=new Array("子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥");
    var solarTerm = new Array("小寒","大寒","立春","雨水","惊蛰","春分","清明","谷雨","立夏","小满","芒种","夏至","小暑","大暑","立秋","处暑","白露","秋分","寒露","霜降","立冬","小雪","大雪","冬至");
    var sTermInfo = new Array(0,21208,42467,63836,85337,107014,128867,150921,173149,195551,218072,240693,263343,285989,308563,331033,353350,375494,397447,419210,440795,462224,483532,504758);
    var nStr1 = new Array('日','一','二','三','四','五','六','七','八','九','十');
    var nStr2 = new Array('初','十','廿','卅','□');
    
    var sFtv = new Array(
    "0101*元旦",
    "0214 情人节",
    "0308 妇女节",
    "0501 劳动节",
    "0504 青年节",
    "0601 儿童节",
    "0701 建党节 香港回归纪念",
    "0801 建军节",
    "1001*国庆节");
    
    //农历节日 *表示放假日
    var lFtv = new Array(
    "0101*春节",
    "0115 元宵节",
    "0505 端午节",
    "0815 中秋节",
    "0909 重阳节",
    "1224 小年",
    "0100*除夕");
    
    //某月的第几个星期几。 5,6,7,8 表示到数第 1,2,3,4 个星期几
    var wFtv = new Array(
    "0520 母亲节",
    "0630 父亲节",
    "1144 感恩节");

    var Today = new Date();
    var tY = Today.getFullYear();
    var tM = Today.getMonth();
    var tD = Today.getDate();
    
    /*****************************************************************************
                                          日期计算
    *****************************************************************************/
    
    //====================================== 传回农历 y年的总天数
    function lYearDays(y) {
       var i, sum = 348;
       for(i=0x8000; i>0x8; i>>=1) sum += (lunarInfo[y-1900] & i)? 1: 0;
       return(sum+leapDays(y));
    }
    
    //====================================== 传回农历 y年闰月的天数
    function leapDays(y) {
       if(leapMonth(y))  return((lunarInfo[y-1900] & 0x10000)? 30: 29);
       else return(0);
    }
    
    //====================================== 传回农历 y年闰哪个月 1-12 , 没闰传回 0
    function leapMonth(y) {
       return(lunarInfo[y-1900] & 0xf);
    }
    
    //====================================== 传回农历 y年m月的总天数
    function monthDays(y,m) {
       return( (lunarInfo[y-1900] & (0x10000>>m))? 30: 29 );
    }
    
    
    //====================================== 算出农历, 传入日期物件, 传回农历日期物件
    //                                       该物件属性有 .year .month .day .isLeap
    function Lunar(objDate) {
    
       var i, leap=0, temp=0;
       var offset   = (Date.UTC(objDate.getFullYear(),objDate.getMonth(),objDate.getDate()) - Date.UTC(1900,0,31))/86400000;
    
       for(i=1900; i<2050 && offset>0; i++) { temp=lYearDays(i); offset-=temp; }
    
       if(offset<0) { offset+=temp; i--; }
    
       this.year = i;
    
       leap = leapMonth(i); //闰哪个月
       this.isLeap = false;
    
       for(i=1; i<13 && offset>0; i++) {
          //闰月
          if(leap>0 && i==(leap+1) && this.isLeap==false)
             { --i; this.isLeap = true; temp = leapDays(this.year); }
          else
             { temp = monthDays(this.year, i); }
    
          //解除闰月
          if(this.isLeap==true && i==(leap+1)) this.isLeap = false;
    
          offset -= temp;
       }
    
       if(offset==0 && leap>0 && i==leap+1)
          if(this.isLeap)
             { this.isLeap = false; }
          else
             { this.isLeap = true; --i; }
    
       if(offset<0){ offset += temp; --i; }
    
       this.month = i;
       this.day = offset + 1;
    }
    
    //==============================传回国历 y年某m+1月的天数
    function solarDays(y,m) {
       if(m==1)
          return(((y%4 == 0) && (y%100 != 0) || (y%400 == 0))? 29: 28);
       else
          return(solarMonth[m]);
    }
    //============================== 传入 offset 传回干支, 0=甲子
    function cyclical(num) {
       return(Gan[num%10]+Zhi[num%12]);
    }
    
    //============================== 月历属性
    function calElement(sYear,sMonth,sDay,week,lYear,lMonth,lDay,isLeap,cYear,cMonth,cDay) {
    
          this.isToday    = false;
          //国历
          this.sYear      = sYear;   //西元年4位数字
          this.sMonth     = sMonth;  //西元月数字
          this.sDay       = sDay;    //西元日数字
          this.week       = week;    //星期, 1个中文
          //农历
          this.lYear      = lYear;   //西元年4位数字
          this.lMonth     = lMonth;  //农历月数字
          this.lDay       = lDay;    //农历日数字
          this.isLeap     = isLeap;  //是否为农历闰月?
          //八字
          this.cYear      = cYear;   //年柱, 2个中文
          this.cMonth     = cMonth;  //月柱, 2个中文
          this.cDay       = cDay;    //日柱, 2个中文
    
          this.color      = '';
    
          this.lunarFestival = ''; //农历节日
          this.solarFestival = ''; //国历节日
          this.solarTerms    = ''; //节气
    }
    
    //===== 某年的第n个节气为几日(从0小寒起算)
    function sTerm(y,n) {
       var offDate = new Date( ( 31556925974.7*(y-1900) + sTermInfo[n]*60000  ) + Date.UTC(1900,0,6,2,5) );
       return(offDate.getUTCDate());
    }
    
    
    
    
    //============================== 传回月历物件 (y年,m+1月)
    /*
    功能说明: 传回整个月的日期资料物件
    
    使用方式: OBJ = new calendar(年,零起算月);
    
      OBJ.length      传回当月最大日
      OBJ.firstWeek   传回当月一日星期
    
      由 OBJ[日期].属性名称 即可取得各项值
    
      OBJ[日期].isToday  传回是否为今日 true 或 false
    
      其他 OBJ[日期] 属性参见 calElement() 中的注解
    */
    function calendar(y,m) {
    
       var sDObj, lDObj, lY, lM, lD=1, lL, lX=0, tmp1, tmp2, tmp3;
       var cY, cM, cD; //年柱,月柱,日柱
       var lDPOS = new Array(3);
       var n = 0;
       var firstLM = 0;
    
       sDObj = new Date(y,m,1,0,0,0,0);    //当月一日日期
    
       this.length    = solarDays(y,m);    //国历当月天数
       this.firstWeek = sDObj.getDay();    //国历当月1日星期几
    
       ////////年柱 1900年春分后为庚子年(60进制36)
       if(m<2) cY=cyclical(y-1900+36-1);
       else cY=cyclical(y-1900+36);
       var term2=sTerm(y,2); //立春日期
    
       ////////月柱 1900年1月小寒以前为 丙子月(60进制12)
       var firstNode = sTerm(y,m*2) //传回当月「节」为几日开始
       cM = cyclical((y-1900)*12+m+12);
    
       //当月一日与 1900/1/1 相差天数
       //1900/1/1与 1970/1/1 相差25567日, 1900/1/1 日柱为甲戌日(60进制10)
       var dayCyclical = Date.UTC(y,m,1,0,0,0,0)/86400000+25567+10;
    
       for(var i=0;i<this.length;i++) {
    
          if(lD>lX) {
             sDObj = new Date(y,m,i+1);    //当月一日日期
             lDObj = new Lunar(sDObj);     //农历
             lY    = lDObj.year;           //农历年
             lM    = lDObj.month;          //农历月
             lD    = lDObj.day;            //农历日
             lL    = lDObj.isLeap;         //农历是否闰月
             lX    = lL? leapDays(lY): monthDays(lY,lM); //农历当月最后一天
    
             if(n==0) firstLM = lM;
             lDPOS[n++] = i-lD+1;
          }
          //依节气调整二月分的年柱, 以春分为界
          if(m==1 && (i+1)==term2) cY=cyclical(y-1900+36);
          //依节气月柱, 以「节」为界
          if((i+1)==firstNode) cM = cyclical((y-1900)*12+m+13);
          //日柱
          cD = cyclical(dayCyclical+i);
          //sYear,sMonth,sDay,week,
          //lYear,lMonth,lDay,isLeap,
          //cYear,cMonth,cDay
          this[i] = new calElement(y, m+1, i+1, nStr1[(i+this.firstWeek)%7],lY, lM, lD++, lL,cY ,cM, cD );
       }
    
    
       //节气
       tmp1=sTerm(y,m*2  )-1;
       tmp2=sTerm(y,m*2+1)-1;
       this[tmp1].solarTerms = solarTerm[m*2];
       this[tmp2].solarTerms = solarTerm[m*2+1];
       if(m==3) this[tmp1].color = 'red'; //清明颜色
    
       //国历节日
       for(i in sFtv)
          if(sFtv[i].match(/^(\d{2})(\d{2})([\s\*])(.+)$/))
             if(Number(RegExp.$1)==(m+1)) {
                this[Number(RegExp.$2)-1].solarFestival += RegExp.$4 + ' ';
                if(RegExp.$3=='*') this[Number(RegExp.$2)-1].color = 'red';
             }
    
       //月周节日
       for(i in wFtv)
          if(wFtv[i].match(/^(\d{2})(\d)(\d)([\s\*])(.+)$/))
             if(Number(RegExp.$1)==(m+1)) {
                tmp1=Number(RegExp.$2);
                tmp2=Number(RegExp.$3);
                if(tmp1<5)
                   this[((this.firstWeek>tmp2)?7:0) + 7*(tmp1-1) + tmp2 - this.firstWeek].solarFestival += RegExp.$5 + ' ';
                else {
                   tmp1 -= 5;
                   tmp3 = (this.firstWeek+this.length-1)%7; //当月最后一天星期?
                   this[this.length - tmp3 - 7*tmp1 + tmp2 - (tmp2>tmp3?7:0) - 1 ].solarFestival += RegExp.$5 + ' ';
                }
             }
    
       //农历节日
       for(i in lFtv)
          if(lFtv[i].match(/^(\d{2})(.{2})([\s\*])(.+)$/)) {
             tmp1=Number(RegExp.$1)-firstLM;
             if(tmp1==-11) tmp1=1;
             if(tmp1 >=0 && tmp1<n) {
                tmp2 = lDPOS[tmp1] + Number(RegExp.$2) -1;
                if( tmp2 >= 0 && tmp2<this.length && this[tmp2].isLeap!=true) {
                   this[tmp2].lunarFestival += RegExp.$4 + ' ';
                   if(RegExp.$3=='*') this[tmp2].color = 'red';
                }
             }
          }
    
    
       //复活节只出现在3或4月
       if(m==2 || m==3) {
          var estDay = new easter(y);
          if(m == estDay.m)
             this[estDay.d-1].solarFestival = this[estDay.d-1].solarFestival+' 复活节';
       }
    
    
       //if(m==2) this[20].solarFestival = this[20].solarFestival+unescape('%20%u6D35%u8CE2%u751F%u65E5');
       
       //黑色星期五
       if((this.firstWeek+12)%7==5)
          this[12].solarFestival += '黑色星期五 ';
       
       //今日
       if(y==tY && m==tM) this[tD-1].isToday = true;
    }
    
    //======================================= 传回该年的复活节(春分后第一次满月周后的第一主日)
    function easter(y) {
    
       var term2=sTerm(y,5); //取得春分日期
       var dayTerm2 = new Date(Date.UTC(y,2,term2,0,0,0,0)); //取得春分的国历日期物件(春分一定出现在3月)
       var lDayTerm2 = new Lunar(dayTerm2); //取得取得春分农历
    
       if(lDayTerm2.day<15) //取得下个月圆的相差天数
          var lMlen= 15-lDayTerm2.day;
       else
          var lMlen= (lDayTerm2.isLeap? leapDays(y): monthDays(y,lDayTerm2.month)) - lDayTerm2.day + 15;
    
       //一天等于 1000*60*60*24 = 86400000 毫秒
       var l15 = new Date(dayTerm2.getTime() + 86400000*lMlen ); //求出第一次月圆为国历几日
       var dayEaster = new Date(l15.getTime() + 86400000*( 7-l15.getUTCDay() ) ); //求出下个周日
    
       this.m = dayEaster.getUTCMonth();
       this.d = dayEaster.getUTCDate();
    
    }
    
    //====================== 中文日期
    function cDay(d){
       var s;
    
       switch (d) {
          case 10:
             s = '初十'; break;
          case 20:
             s = '二十'; break;
             break;
          case 30:
             s = '三十'; break;
             break;
          default :
             s = nStr2[Math.floor(d/10)];
             s += nStr1[d%10];
       }
       return(s);
    }
    
    function addLunar(y,m,d){
    	var lunar = new calendar(y,m-1);
    	var lunarHtml,s;
    	for(var key in lunar){
		 if(d == lunar[key].sDay) { //日期内
	         if(lunar[key].lDay==1) //显示农历月
	        	 lunarHtml = '<b>'+(lunar[key].isLeap?'闰':'') + lunar[key].lMonth + '月' + (monthDays(lunar[key].lYear,lunar[key].lMonth)==29?'小':'大')+'</b>';
	         else //显示农历日
	        	 lunarHtml = cDay(lunar[key].lDay);

	         s=lunar[key].lunarFestival;
	         if(s.length>0) { //农历节日
	            if(s.length>6) s = s.substr(0, 4)+'...';
	            s = s.fontcolor('red');
	         }
	         else { //国历节日
	            s=lunar[key].solarFestival;
	            if(s.length>0) {
	               size = (s.charCodeAt(0)>0 && s.charCodeAt(0)<128)?8:4;
	               if(s.length>size+2) s = s.substr(0, size)+'...';
	               s = s.fontcolor('blue');
	            }
	            else { //廿四节气
	               s=lunar[key].solarTerms;
	               if(s.length>0) s = s.fontcolor('#FF00FF');
	            }
	         }
	         if(s.length>0) lunarHtml = s;
    	}
     }
     return lunarHtml;
    }

 /*添加农历和节气和节日结束*/

(function($, window) {
	var CALENDAR_SLIDER = "calendar-slider";//日历控件 控件父DOM
	var CALENDAR_LOOP = "calendar-loop";//日历控件控件日期月分组页DOM
	var CLASS_SLIDER = CALENDAR_SLIDER;
	var CLASS_SLIDER_GROUP = 'mui-slider-group';
	var CLASS_SLIDER_LOOP = 'mui-slider-loop';
	var CLASS_SLIDER_INDICATOR = 'mui-slider-indicator';
	var CLASS_ACTION_PREVIOUS = 'mui-action-previous';
	var CLASS_ACTION_NEXT = 'mui-action-next';
	var CLASS_SLIDER_ITEM = 'mui-slider-item';
	var DAYID = "day-";
	var SELECT_DAY = "day-select";
	var SHOW = "mui-block";
	var HIDE = "mui-hidden";
	var YEAR_UNIT = "-";//年单位
	var MONTH_UNIT = "";//月单位
	var DAY_UNIT ="";//日单位
	var NATIVEPATH="";//地址
	//显示样式配置
	var DAYSTATE = {
		lock:"day-lock",//档期满
		pingdan:"day-pindan",//可拼单
		rest:"day-rest",//休息(旅拍)
		select:"day-select",//选中
		default:"day-normal",//默认
		over:"day-over",//过期
		secret:"day-secret"//保密
	};
	
	var css_Style='';

	var CLASS_ACTIVE = 'mui-active';

	var SELECTOR_SLIDER_ITEM = '.' + CLASS_SLIDER_ITEM;
	var SELECTOR_SLIDER_INDICATOR = '.' + CLASS_SLIDER_INDICATOR;
	var SELECTOR_SLIDER_PROGRESS_BAR = '.mui-slider-progress-bar';
	var currenttime,daystates,run;
	
	var Calendar = $.Calendar = $.Slider.extend({
		init: function(element, options) {
			currenttime = this.getCurrentTime();//取得当前时间
			this.options = $.extend(true, {
				fingers: 1,
				interval: 0, //设置为0，则不定时轮播
				scrollY: false,
				scrollX: true,
				indicators: false,
				scrollTime: 1000,
				startX: false,
				slideTime: 0, //滑动动画时间
				snap: SELECTOR_SLIDER_ITEM,//绑定事件DOM
				time:{//时间
					year:currenttime.year,
					month:currenttime.month+1,
					today:currenttime.day
				},
				year_unit:YEAR_UNIT,
				month_unit:MONTH_UNIT,
				day_unit:DAY_UNIT,
				nativepath:NATIVEPATH,//本地路径
				calendar_slider:CALENDAR_SLIDER,//日历控件 class
				calendar_loop:CALENDAR_LOOP,//日历控件基础DOM class
				daystate:DAYSTATE,//日期状态
				showdate:false,//事件显示dom
				smallChange:false,//是否可以多选
				select_day:SELECT_DAY,//默认选中的样式
				next_item:false,//上一个页面切换监听节点class
				last_item:false,//下一个页面切换监听节点class
				clickCallback:false,//点击回调
				dragCallback:false,//滑动回调
				model:"week"
			}, options);
			run = true;
			this.options.nativepath +="/"; 
			this.daystate = this.options.daystate;//设置全局日历状态样式
			this.changeGroup = [];//选中的数组
			this.tempYear = "";
			this.tempMonth = this.options.time.month;
			this.tempDay = "";
			this.isChangeView = false;
			this._super(element, this.options);
		},
		//初始化日历插件
		_init: function() {
			this._reInit();
			if (this.scroller) {
				this.scrollerStyle = this.scroller.style;
				this.progressBar = this.wrapper.querySelector(SELECTOR_SLIDER_PROGRESS_BAR);
				if (this.progressBar) {
					this.progressBarWidth = this.progressBar.offsetWidth;
					this.progressBarStyle = this.progressBar.style;
				}
				this.year = this.options.time.year;
				this.month = this.options.time.month;
				this.today = this.options.time.today;
				this.createCalendarPanel();//创建日历界面
				this.SHOWDATE_DOM = this.options.showdate?this.element.querySelector("."+this.options.showdate):false;
				if(this.SHOWDATE_DOM){
					this.SHOWDATE_DOM.innerHTML = this.year+this.options.year_unit+this.month+this.options.month_unit;
				}
				var self = this;
				//下一月点击
				if(this.options.next_item){
					this.element.querySelector("."+this.options.next_item).addEventListener("tap",function(){
						self.nextItem();
					});
				}
				//上一月点击
				if(this.options.last_item){
					this.element.querySelector("."+this.options.last_item).addEventListener("tap",function(){
						self.lastItem();
					});
				}
				var elements = this.element.querySelectorAll(SELECTOR_SLIDER_ITEM);
				this.calendarDom = elements[self.slideNumber+1];//获取当前日历DOM
				this._super();
				this._initTimer();
				$(".mui-button-row").on("tap", ".mui-btn",function(event){
					var arrow = $(".wui-icon-arrow", this)[0];
					if(arrow && arrow.classList.contains("mui-icon-arrowdown")){
						arrow.classList.remove("mui-icon-arrowdown");
						arrow.classList.add("mui-icon-arrowup");
					}else if(arrow && arrow.classList.contains("mui-icon-arrowup")){
						arrow.classList.remove("mui-icon-arrowup");
						arrow.classList.add("mui-icon-arrowdown");
					}
					self.changeView();
				})
			}
		},
		//滑动完成，触发事件
		_triggerSlide: function(e) {
			var self = this;
			self.isInTransition = false;
			var page = self.currentPage;
			self.slideNumber = self._fixedSlideNumber();
			if (self.loop) {
				if (self.slideNumber === 0) {
					self.setTranslate(self.pages[1][0].x, 0);
				} else if (self.slideNumber === self.itemLength - 3) {
					self.setTranslate(self.pages[self.itemLength - 2][0].x, 0);
				}
			}
			if (self.lastSlideNumber != self.slideNumber) {
				self.lastSlideNumber = self.slideNumber;
				self.lastPage = self.currentPage;
				$.trigger(self.wrapper, 'slide', {
					slideNumber: self.slideNumber,
					direction:e.detail.direction
				});
			}
			self._initTimer();
		},
		_flick: function(e) {
			if (!this.moved) { //无moved
				return;
			}
			var detail = e.detail;
			var direction = detail.direction;
			this._clearRequestAnimationFrame();
			this.isInTransition = true;
			if (e.type === 'flick') {
				if (detail.deltaTime < 200) { //flick，太容易触发，额外校验一下deltaTime  
					if(Math.abs(detail.deltaX)-Math.abs(detail.deltaY)>=90){//Alen 增加X,Y轴滚动差判断，以防止上下滚动时也触发切换
						this.x = this._getPage((this.slideNumber + (direction === 'right' ? -1 : 1)), true).x;
					}
				}
				this.resetPosition(this.options.bounceTime);
			} else if (e.type === 'dragend' && !detail.flick) {
				this.resetPosition(this.options.bounceTime);
			}
			e.stopPropagation();
		},
		/**
		 * 滚动到第几帧 该方法是手动触发的
		 * @param {Number} slideNumber  页面数量
		 * @param {String} time   动画时间
		 * @param {String} dire   滚动方向
		 */
		_gotoItem: function(slideNumber, time,dire) {
			this.currentPage = this._getPage(slideNumber, true); //此处传true。可保证程序切换时，动画与人手操作一致(第一张，最后一张的切换动画)
			this.scrollTo(this.currentPage.x, 0, time, this.options.scrollEasing);
			if(dire){
				this.direction = dire;
			}
			if (time === 0) {
				$.trigger(this.wrapper, 'scrollend', this);
			}
		},
		_handleSlide: function(e) {//滑动完成后触发
			var self = this;
			if (e.target !== self.wrapper) {
				return;
			}
			var detail = e.detail;
			detail.slideNumber = detail.slideNumber || 0;
			var temps = self.scroller.querySelectorAll(SELECTOR_SLIDER_ITEM);
			var items = [];
			for (var i = 0, len = temps.length; i < len; i++) {
				var item = temps[i];
				if (item.parentNode === self.scroller) {
					items.push(item);
				}
			}
			var _slideNumber = detail.slideNumber;
			if (self.loop) {
				_slideNumber += 1;
			}
			if (!self.wrapper.classList.contains('mui-segmented-control')) {
				for (var i = 0, len = items.length; i < len; i++) {
					var item = items[i];
					if (item.parentNode === self.scroller) {
						if (i === _slideNumber) {
							item.classList.add(CLASS_ACTIVE);
							this.changeCelendar(item,e.detail);
						} else {
							item.classList.remove(CLASS_ACTIVE);
						}
					}
				}
			}
			var indicatorWrap = self.wrapper.querySelector('.mui-slider-indicator');
			if (indicatorWrap) {
				if (indicatorWrap.getAttribute('data-scroll')) { //scroll
					$(indicatorWrap).scroll().gotoPage(detail.slideNumber);
				}
				var indicators = indicatorWrap.querySelectorAll('.mui-indicator');
				if (indicators.length > 0) { //图片轮播
					for (var i = 0, len = indicators.length; i < len; i++) {
						indicators[i].classList[i === detail.slideNumber ? 'add' : 'remove'](CLASS_ACTIVE);
					}
				} else {
					var number = indicatorWrap.querySelector('.mui-number span');
					if (number) { //图文表格
						number.innerText = (detail.slideNumber + 1);
					} else { //segmented controls
						var controlItems = indicatorWrap.querySelectorAll('.mui-control-item');
						for (var i = 0, len = controlItems.length; i < len; i++) {
							controlItems[i].classList[i === detail.slideNumber ? 'add' : 'remove'](CLASS_ACTIVE);
						}
					}
				}
			}
			e.stopPropagation();
		},
		refresh: function(options) {
			if (options) {
				$.extend(this.options, options);
				this._super();
				this._initTimer();
				this._initAllDayState();//初始化日历面板
				this.year = this.options.time.year;
				this.month = this.options.time.month;
				var element = $(".slide-dom.mui-active").length == 0 ? $(".slide-dom")[0] : $(".slide-dom.mui-active")[0];
				this.createCalendarPanel(element);//创建日历界面
				if(this.SHOWDATE_DOM){
					this.SHOWDATE_DOM.innerHTML = this.year+this.options.year_unit+this.month+this.options.month_unit;
				}
			} else {
				this._super();
			}
		},
		destroy: function() {
			this._initEvent(true); //detach
			delete $.data[this.wrapper.getAttribute('calendar-slider')];
			this.wrapper.setAttribute('calendar-slider', '');
		},
		//下一个月
		nextItem:function(){
			this._gotoItem(this.slideNumber + 1, this.options.scrollTime,"left");
		},
		//上一个月
		lastItem:function(){
			this._gotoItem(this.slideNumber - 1, this.options.scrollTime,"right");
		},
		//获取当前系统时间函数
		getCurrentTime:function(year,month,day){
			var current = {};
			var current_time;
			if(year&&month&&day){
				current_time = new Date(year+'-'+month+'-'+day);
			}else{
				current_time = new Date();//当前时间
			}
			current['year'] = current_time.getFullYear();//当前年
			current['month'] = current_time.getMonth();//当前月
			current['day'] = current_time.getDate();//当前日
			current['hour'] =current_time.getHours()<10?"0"+current_time.getHours():current_time.getHours();//当前时
			current['min'] = current_time.getMinutes()<10?"0"+current_time.getMinutes():current_time.getMinutes();//当前分
			return current;
		},
		//设置参数到当前插件对象中
		setinfo:function(info){
			this.info = info;
		},
		//在当前对象中获取参数
		getinfo:function(){
			return this.info;
		},
		getActiveDay:function(){
			var dayDom = this.calendarDom.querySelectorAll("."+DAYSTATE.select)[0];
			if(dayDom){
				this.tempDay = dayDom.getAttribute('day');
				this.tempMonth = dayDom.getAttribute('month');
				this.tempYear = this.year;
				return this.tempYear+'-'+this.tempMonth+'-'+this.tempDay;
			}
		},
		//设置指定日期状态
		setDaySate:function(day,state,removeStyleName){
			if(!day){
				console.error("请输入正确的参数day");
				return false;
			}else if(!state){
				console.error("请输入正确的参数state");
				return false;
			}
			var dayDom = this.calendarDom.querySelector("#"+DAYID+day);//取得指定的日期dom
			
			// this.tempMonth = dayDom.getAttribute("month");
		
			// //此处这样判断可以防止已过期的日期被设置状态
			// if(parseInt(this.year)<parseInt(this.getCurrentTime()['year'])){//小于当前年
			// 	return false;
			// }
			// if(parseInt(this.year)==parseInt(this.getCurrentTime()['year'])){//同年
			// 	if(parseInt(this.month)<parseInt(this.getCurrentTime()['month'])+1){//小于当前月
			// 		return false;
			// 	}
			// 	if(this.tempMonth <= this.month){
			// 		if(parseInt(this.month)==parseInt(this.getCurrentTime()['month'])+1&&parseInt(day)<parseInt(this.getCurrentTime()['day'])){//同月且小于当前日
			// 			return false;
			// 		}	
			// 	}				
			// }
			
			//移除指定的class
			if(removeStyleName===true){//移除所有class
				mui.each(DAYSTATE,function(k,v){
					if(dayDom.classList.contains(v)){
						dayDom.classList.remove(v);
					}
				});
				dayDom.classList.add(state);//添加CSS样式
//				if(state==this.options.select_day){
//				dayDom.querySelector(".jiao").classList.remove(SHOW);
//				}
			}else if(removeStyleName){
				if(dayDom.classList.contains(removeStyleName)){
//					dayDom.querySelector(".jiao").classList.remove(SHOW);
					dayDom.classList.remove(removeStyleName);
				}
			}else{
				dayDom.classList.add(state);//添加CSS样式
				if(state==this.options.select_day){
//					dayDom.querySelector(".jiao").classList.add(SHOW);
				}
			}
		},
		_selectDay:function(day){
			var self =this;
			//删除其他选项的CSS属性
			mui.each(this.calendarDom.querySelectorAll("."+DAYSTATE.select),function(k,v){
				var otherday = parseInt(v.querySelector("span").innerHTML);
				self.setDaySate(otherday,DAYSTATE.secret,DAYSTATE.select);
			});
			self.setDaySate(day,DAYSTATE.secret);//设置选中
		},
		//初始化所有日期状态
		_initAllDayState:function(){
			var self = this;
			mui.each(this.calendarDom.querySelectorAll("li"),function(k,v){
				if(v.querySelector("span").innerHTML){
					var day = v.querySelector("span").innerHTML;
					if(day){
						day = parseInt(day);
						if(day){
							self.setDaySate(day,DAYSTATE.secret,true);
						}
					}
				}
			});
		},
		//获取指定日期状态
		getDayState:function(day){
			if(!day){
				console.error("请输入正确的参数");
			}
			var dayDom = this.calendarDom.querySelector("#"+DAYID+day);//取得指定的日期dom
			var clas="";
			for (var i = 0; i < dayDom.classList.length; i++) {
				clas +=dayDom.classList[i]+" ";
			}
			return clas;
		},
		//设置当前帧面板的日期状态
		setCalendar:function(options){
			var self = this;
			$.each(options, function(k,v) {
				self.setDaySate(v.day,v.state);
			});
		},
		//设置时间显示面板状态
		setViewDayPanel:function(day){
			day = day?day:this.day;
			var activeDay = this.getActiveDay().split('-');
			if(this.SHOWDATE_DOM){
				this.SHOWDATE_DOM.innerHTML = activeDay[0]+this.options.year_unit+activeDay[1]+this.options.month_unit+activeDay[2]+this.options.day_unit;
			}
		},
		//切换日期
		changeCelendar:function(element,data){
			if(!data.direction){
				return false;
			}
			var year = this.year;
			var month = this.month;
			if(data.direction=="left"){//向左滑动
				if(this.model == "week"){
					var date = new Date(this.year, this.month, 0); 
					var datecount = date.getDate(); //总天数
					var nextWeekStart = this.weekStart + 7;
					if(nextWeekStart > datecount){
						month = parseInt(month)+1;
						this.nextMonth = month;
						if(month>12){
							month = 1;
							year = parseInt(year)+1;
						}
						this.weekStart = nextWeekStart - datecount;
					}else{
						this.weekStart = nextWeekStart
					}					
				}else{
					month = parseInt(month)+1;
					if(month>12){
						month = 1;
						year = parseInt(year)+1;
					}
				}				
			}else{
				if(this.model == "week"){
					var date = new Date(this.year, this.month-1, 0); 
					var datecount = date.getDate(); //总天数
					var prevWeekStart = this.weekStart - 7;
					if(prevWeekStart < 1){
						month = parseInt(month)-1;
						this.prevMonth = month;
						if(month<1){
							month = 12;
							year = parseInt(year)-1;
						}
						this.weekStart = prevWeekStart + datecount;
					}else{
						this.weekStart = prevWeekStart
					}		
				}else{
					month = parseInt(month)-1;
					if(month<1){
						month = 12;
						year = parseInt(year)-1;
					}
				}
			}
			// console.error(year+"--"+month);
			if(this.SHOWDATE_DOM){
				this.SHOWDATE_DOM.innerHTML = year+this.options.year_unit+month+this.options.month_unit;
			}
			this.year = year;
			this.month = month;
			this.createCalendarPanel(element);
			this.changeGroup = [];//清空选中数组
			this.day = "";
			//滑动回调事件
			if ($.isFunction(this.options.dragCallback)){
				this.options.dragCallback(this);
			}
		},
		changeView:function(){
			var _self = this;
			//获取到当前选中的天
			this.isChangeView = true;
			this.getActiveDay();
			if(this.model == "week"){
				options = $.extend(this.options,{model:"month"})
				this.refresh(options);
			}else{
				options = $.extend(this.options,{model:"week"})				
				//切换至周视图时，清理其他面板的数据
				var elements = this.element.querySelectorAll(SELECTOR_SLIDER_ITEM);
				elements.forEach(function(element){
					var className = element.classList;
					if(!className.contains("mui-active")){
					   element.innerHTML = "";
					}
				})
				this.weekStart = this.today;
				this.refresh(options);
			}
			this.isChangeView = false;
		},
		_changeDay:function(day){
			if(!day){
				return;
			}
			var dayDom = this.calendarDom.querySelector("#"+DAYID+day);//取得指定的日期dom
			var self = this;
			if(dayDom.classList.contains(this.options.select_day)){//判断是否是选中状态
//				如果是取消选中则删除当前元素
				$.each(this.changeGroup, function(k,v) {
					if(v==day){
						self.changeGroup.splice(k,1);//删除指定元素
						return;
					}
				});
				if(this.options.smallChange===false){//如果是单选
					self._selectDay(day);//删除所有元素的选中状态
				}else{
					this.setDaySate(day,this.options.select_day,this.options.select_day);
				}
			}else{
				if(this.options.smallChange===false){//如果是单选
					if(this.changeGroup.length>=1){
						var lastday = this.changeGroup.pop();
						this.setDaySate(lastday,this.options.select_day,this.options.select_day);//删除队尾的数据并改变其选中状态
					}
					self._selectDay(day);//删除所有元素的选中状态
				}
				this.setDaySate(day,this.options.select_day);
				this.changeGroup.push(day);//将新数据插入数组中
			}
			self.day = day;//设置全局日期
			self.setViewDayPanel();//设置面板
			
		},
		//设置当前日期选中状态
		changeDay:function(day){
			var dayDom = this.calendarDom.querySelector("#"+DAYID+day);//取得指定的日期dom
			mui.trigger(dayDom,"tap");//触发点击事件
		},
		//计算星期
		computedWeek:function(currentDay){
			var weekNum,today;
			if(!this.weekStart){
				today = new Date().getDate();
				weekNum = new Date().getDay();								
			}else{
				if(this.isChangeView){
					var selectDay = this.tempYear +"-"+this.tempMonth+"-"+this.tempDay;
					today = new Date(selectDay).getDate();
					weekNum = new Date(selectDay).getDay();
				}else{
					var selectDay = this.year +"-"+this.month+"-"+this.weekStart;
					today = new Date(selectDay).getDate();
					weekNum = new Date(selectDay).getDay();
				}
				
			}
			switch(weekNum){
				case 0:
					this.weekStart = today;
					break;
				case 1:
					this.weekStart = today-1;
					break;
				case 2:
					this.weekStart = today-2;
					break;
				case 3:
					this.weekStart = today -3;
					break;
				case 4:
					this.weekStart = today - 4;
					break;
				case 5:
					this.weekStart = today - 5;
					break;
				case 6:
					this.weekStart = today -6;
					break;
			};
			var weekDayArr = [];
			var date = new Date(this.year, this.month, 0); 
			var datecount = date.getDate(); //总天数
			var weekStart = this.weekStart;
			for(var i = 0;i<7;i++){
				var weekday;
				weekStart = i==0 ? weekStart : weekStart+1;
				if( weekStart >= datecount && this.nextMonth){
					weekStart = 1;
					weekday = new Date(this.year,this.nextMonth,weekStart);
				}else{
					this.nextMonth = "";
					weekday = new Date(this.year,this.month-1,weekStart);
				}
				weekDayArr.push(weekday);
			}
			return weekDayArr;
		},
		//创建当前日历插件
		createCalendarPanel:function(element){
			var self = this;
			this.caledarDomClass = this.options.calendar_loop;
			var currentdate = this.getCurrentTime(this.tempYear,this.tempMonth,this.tempDay);
			if(this.year<1970|| this.month < 0 || this.month > 12){
				console.error("时间参数错误");
				return false;
			}
			var date,day;
			if(this.isChangeView){
				date = new Date(this.year, this.month, 0); 
				day = new Date(this.tempYear, parseInt(this.tempMonth)-1, 1);
				this.year = this.tempYear;
				this.month = this.tempMonth;
			}else{
				this.getMonth = "";
				this.tempYear = "";
				date = new Date(this.year, this.month, 0); 								
				day = new Date(this.year, this.month - 1, 1);				
			}
			var datecount = date.getDate(); //总天数
			var week = day.getDay(); //1号星期几 
			datecount += week;
			var dom = "";
			var thisday = 0;
			css_Style=this.options.daystate.secret;
			this.model = this.options.model;
			if(this.model == "week"){
				var weekArr = this.computedWeek();
				var selectDay = weekArr.filter(function(n){
					return ((n.getDate() == (self.tempDay || self.today)) && (n.getFullYear() == (self.tempYear || self.year) ) && (n.getMonth()+1 == (self.tempMonth || self.month)));
				})
				weekArr.forEach(function(n,i){
					var year = n.getFullYear();
					var month = n.getMonth()+1;
					var day = n.getDate();
					var dayState = '';
					if(selectDay.length != 0){
						if(day == selectDay[0].getDate()){
							dayState = self.options.daystate.select;
						}else{
							dayState = css_Style;				
						}
					}else{
						if(i==0){
							dayState = self.options.daystate.select;
						}else{
							dayState = css_Style;				
						}	
					}								
					if(year == self.options.time.year && month == self.options.time.month && day == self.options.time.today){
						dayState += ' today';
					}													
					dom += '<li class="day ' + dayState + '" id="'+DAYID+day  + '" day="'+day +'" month='+month+' ><span>' + day  + '</span><div class="lunar">'+addLunar(year,month,day)+'</div></li>';
				});

			}else{
				for (var i = 0; i < datecount; i++) {
					if(i<week){
						dom+='<li class="day day-normal"><span></span><div class="rest-icon"></div></li>';
					}else{
						thisday = i-week+1;
						var dayState = '';
						var flag = (currentdate['year'] == self.tempYear) && (currentdate['month'] + 1 == self.tempMonth);
						if(flag){//传递月份等于当前月份
							if(thisday == currentdate['day']){
								dayState = this.options.daystate.select;
							}else{
								dayState = css_Style;
							}
						}else{
							if(thisday == 1){
								dayState=self.options.daystate.select;
							}else{
								dayState = css_Style;
							}
						}						
						if(this.year == this.options.time.year && this.month == this.options.time.month && thisday == this.options.time.today){
							dayState += ' today';
						}															
					dom += '<li class="day ' + dayState + '" id="'+DAYID+thisday + '" day="'+thisday+'" month='+this.month+' ><span>' + thisday + '</span><div class="lunar">'+addLunar(this.year,this.month,thisday )+'</div></li>';
					}
				}
			}
						
			this.slideNumber = this.slideNumber>0?parseInt(this.slideNumber):0;
			if(element){
				element.innerHTML = dom;
				this.calendarDom = element;
			}else{
				if(run==true){
					run = false;
					var elements = this.element.querySelectorAll(SELECTOR_SLIDER_ITEM);
					mui(elements).each(function(k,v){//默认首次进来5个DOM全部填上初始数据
						if(k==(self.slideNumber+1)){
							this.calendarDom = v;
						}
						v.innerHTML = dom;
						//页面点击监听事件
						mui(v).on("tap","li",function(data){
							var day = this.getAttribute("day");
							self._changeDay(day);//改变当前日期状态
							if(day){
								if(self.options.clickCallback){
									self.options.clickCallback(self.changeGroup,self);
								}
							}
						});
					});
				}	
			}
			// 初始化或者滚动时触发回调
			setTimeout(function() {
				var activeDay = $(".date-select.mui-active ." + self.options.daystate.select, element)[0];
				if(activeDay) {
					activeDay.classList.remove(self.options.daystate.select);
					$.trigger(activeDay, "tap");
				}
			}, 0);
		}	
	});
	var weekStr = ["日","一","二","三","四","五","六"];
	var createSliderDom = function(){
		var calendarSlider = document.createElement("div");
		calendarSlider.className="calendar-slider date-box mui-slider";
		var slider_top = document.createElement("div");
		slider_top.className = "calendar-title";
		var showTime = document.createElement("p");
		showTime.className = "this-time timecontent";
		var calenderTitle = document.createElement("table");
		calenderTitle.setAttribute("cellpadding",0);
		calenderTitle.setAttribute("cellspacing",0);
		calenderTitle.className = "weekend-title";
		var tr = document.createElement("tr");
		for(var i=0;i<weekStr.length;i++){
			var td = document.createElement("td");
			td.innerHTML = weekStr[i];
			tr.appendChild(td);
		}
		calenderTitle.appendChild(tr);
		slider_top.appendChild(showTime);
		slider_top.appendChild(calenderTitle);
		var calendarMain = document.createElement("div");
		calendarMain.className = "calendar-loop date-select-ul mui-slider-group mui-slider-loop calendar-";
		for(var j=0;j<5;j++){
			var ul =document.createElement("ul");
			if(j==0 || j==4){
				ul.className = "date-select mui-slider-item mui-slider-item-duplicate";
			}else{
				if(j==1){
					ul.className = "date-select mui-slider-item slide-dom mui-active";
				}else{
					ul.className = "date-select mui-slider-item slide-dom";
				}
			}
			calendarMain.appendChild(ul);
		}
		var toggleBtn = document.createElement("div");
		toggleBtn.className = "mui-button-row wui-calender-toolbar";
		var btn = document.createElement("button");
		btn.className = 'mui-btn mui-btn-block';
		btn.innerHTML = "<i class='wui-icon-arrow mui-icon mui-icon-arrowdown'></i>";
		toggleBtn.appendChild(btn);
		calendarSlider.appendChild(slider_top);
		calendarSlider.appendChild(calendarMain);
		calendarSlider.appendChild(toggleBtn);
		return calendarSlider;
	}

	$.fn.calendar = function(options) {
		var  calendarApis= [];//日历对象集合
		var calendarDom = createSliderDom();
		$(this)[0].appendChild(calendarDom);
		options = options?options:{};
		this.each(function() {
			var sliderElement = this;
			var calendar = null;
			if (!this.classList.contains(CLASS_SLIDER)) {
				sliderElement = this.querySelector('.' + CLASS_SLIDER);
			}
			if (sliderElement && sliderElement.querySelector(SELECTOR_SLIDER_ITEM)) {
				var id = sliderElement.getAttribute('calendar-slider');
				if (!id) {
					id = ++$.uuid;
					$.data[id] = calendar = new Calendar(sliderElement, options);
					sliderElement.setAttribute('calendar-slider', id);
				} else {
					calendar = $.data[id];
					if (calendar && options) {
						calendar.refresh(options);
					}
				}
			}
			calendarApis.push(calendar);
		});
		return calendarApis.length===1?calendarApis[0]:calendarApis;
	};
//	$.ready(function() {
//		//		setTimeout(function() {
//		$('.mui-slider').slider();
//		$('.mui-scroll-wrapper.mui-slider-indicator.mui-segmented-control').scroll({
//			scrollY: false,
//			scrollX: true,
//			indicators: false,
//			snap: '.mui-control-item'
//		});
//		//		}, 500); //临时处理slider宽度计算不正确的问题(初步确认是scrollbar导致的)
//
//	});
})(mui, window);