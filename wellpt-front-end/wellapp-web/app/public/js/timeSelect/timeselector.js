(function(factory){
    if (typeof define === 'function' && define.amd){
        define(['jquery','moment'], factory);
    }else if (typeof exports === 'object'){
        factory(require('jquery'));
        factory(require('moment'));
    }else{
        factory(jQuery,moment);
    }
      
}(function($,moment){
    var defaultConfig = {
        initTime:2018-04-26,
        timeStep:"15",
        setValue:""
    }
    var TimePicker = function(option){
        var options = $.extend({},defaultConfig,option);
        this.el = options.el;
        this.wrapper = '';
        this.step = options.timeStep;
        this._init();
        this.setValue = options.setValue;
    }

    TimePicker.prototype = {
        _init:function(){
            this._createDom();
        },
        _createDom:function(){
            var _this = this;
            if(this.el[0].tagName == 'INPUT'){
                $(this.el).wrap('<div class="time-wrapper"></div>');
                this.wrapper = $('.time-wrapper');
                this.wrapper.css('position','relative');
            }
            var yearSelector = $('<select class="year-select" id="year"/>');
            yearSelector.css("width","200px");
            var monthSelector = $('<select class="month-select" id="month"/>');
            monthSelector.css("width","200px");
            var daySelector = $('<select class="day-select" id="day"/>');
            daySelector.css("width","200px");
            var timeWrapper = $('<div class="wrapper"/>');
            timeWrapper.css("display","none");
            //初始化selector选项内容
            var yearOption = this._createYearOptions();
            var dayOption = this._createDayOption();
            var monthOption = this._createMonthOption();
            yearSelector.html(yearOption);
            monthSelector.html(monthOption);
            daySelector.html(dayOption);
            timeWrapper.append(yearSelector,monthSelector,daySelector);
            if(this.step < 60){
                var hourSelector = this._createHourSelect();
                var minuteSelector = this._createMinuteSelect();
                timeWrapper.append(hourSelector,minuteSelector);
            }else if(this.step == "半天"){
                var halfDaySelector = this._createHalfDay();
                timeWrapper.append(halfDaySelector);
            }else{
                var hourSelect = this._createHourSelect();
                timeWrapper.append(hourSelect);
            }
            this.wrapper.append(timeWrapper);
            yearSelector.on("change",function(){
                _this._reRenderYear();
                _this._reRenderDay();
            })
            monthSelector.on("change",function(){
                _this._reRenderDay();
            })
            var btn = this._createBtn();
            timeWrapper.append(btn);

            $(this.el).on('focus',function(){
                timeWrapper.slideDown();
            })

            
        },
        _computedYear:function(selectyear){
            var yearArr = new Array();
            var prevYear = moment(selectyear,"YYYY").subtract(7,'year').format("YYYY");
            var nextYear = moment(selectyear,"YYYY").add(7,'year').format("YYYY");
            var diff = moment(nextYear).diff(moment(prevYear),'year');
            for(var i=0;i<=diff;i++){
                if(i == 0){
                    yearArr.push(prevYear);
                }else{
                    yearArr.push(parseInt(prevYear)+i);
                }
            }
            return yearArr;
        },
        _createYearOptions:function(selectyear){
            var defaultYear;
            if(selectyear !== undefined && selectyear !=="" && selectyear !== null ){
                defaultYear = selectyear;
            }else{
                defaultYear = new Date().getFullYear();
            }
            var yearArr = this._computedYear(defaultYear);
            var optionHtml = "";
            yearArr.forEach(function(n){
                optionHtml += "<option value="+n+" "+(n == defaultYear ? "selected" : "")+">"+n+"年</option>";
            })
            return optionHtml;
        },
        _reRenderYear:function(){
            var yearBox = $("#year");
            var selectyear = yearBox.find("option:selected").val();
            var yearOptions = this._createYearOptions(selectyear);
            yearBox.html(yearOptions);
        },
        _computedDay:function(selectedYear,selectedMonth){
            var year;
            var month;
            var weekName = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'];
            if(selectedYear == undefined && selectedMonth == undefined || selectedYear == "" && selectedMonth == ""){
                year = new Date().getFullYear();
                month = new Date().getMonth()+1;
            }else{
                year = selectedYear;
                month = selectedMonth;
            }

            var formateTime = year + "-" +month;
            
            var monthDay = moment(formateTime,"YYYY-MM").daysInMonth();

            var monthDayArr = new Array();

            for(var i=1;i<=monthDay;i++){
                var formateDay = year +"-"+month+"-"+i;
                var week = moment(formateDay,"YYYY-MM-DD").day();
                var dayObj = new Object();
                dayObj.day = i;
                dayObj.week = weekName[week];
                monthDayArr.push(dayObj);
            }
            return monthDayArr;
        },

        _createDayOption:function(selectedYear,selectedMonth){
            var monthDays = this._computedDay(selectedYear,selectedMonth);
            var dayOption = "";
            monthDays.forEach(function(n){
                dayOption += "<option value="+n.day+">"+n.day+" "+n.week+"</option>";
            })
            return dayOption;
        },

        _reRenderDay:function(){
            var day = $("#day");
            var year = $("#year").find("option:selected").val();
            var month = $("#month").find("option:selected").val();
            var dayHtml = this._createDayOption(year,month);
            day.html(dayHtml);
        },

        _createMonthOption:function(){
            var month = 12;
            var monthOption = "";
            for(var i = 1; i<=month ;i++){
                monthOption += "<option value="+i+">"+i+"月</option>";
            }
            return monthOption;
        },

        _createHourSelect:function(){
            var hourSelect = $("<select class='hour-select' id='hour'/>");
            hourSelect.css("width","200px");
            var hours;
            if(this.step >= 60){
                hours = 24*60/ this.step;
            }else{
                hours = 24;
            }

            for(var i=0;i<hours;i++){
                var option = "<option value="+(i>=10 ? i : '0'+i)+">"+(i>=10 ? i : '0'+i)+":00</option>";
                hourSelect.append(option);
            }

            return hourSelect;

        },

        _createMinuteSelect:function(){
            var minuteSelector = $("<select class='minute-select' id='minute'/>");
            minuteSelector.css("width","200px");
            var minutes = 60 / this.step;
            var option;
            for(var i=0; i<=minutes;i++){
                if(i == 0){
                    option = "<option value= 00 >00</option>";
                }else{
                    option = "<option value="+(this.step*i>=10 ? this.step*i : '0'+this.step*i)+">"+(this.step*i>=10 ? this.step*i : '0'+this.step*i)+"</option>";
                }
                minuteSelector.append(option);
            }

            return minuteSelector;
        },

        _createHalfDay:function(){
            var halfDayHtml = "<label for='#pm'>上午</label><input type='radio' name='half-day' value='12:00' id='pm' checked>"
                            + "<label for='#am'>下午</label><input type='radio' name='half-day' value='23:00' id='am'>";
            return halfDayHtml;
        },

        _createBtn:function(){
            var _this = this;
            var submitBtn = $('<button type="button" class="btn btn-primary get-time">确定</button>');
            submitBtn.on('click',function(){
                var newDate = _this._getSelectedTime();
                if(typeof _this.setValue == 'function'){
                	_this.setValue(newDate);
                }
                $(".wrapper").slideUp();
            })
            
            return submitBtn;
        },

        _getSelectedTime :function(){
            var year = $('#year').find("option:selected").val();
            var month = $('#month').find("option:selected").val();
            var day = $('#day').find("option:selected").val();
            if($('#hour').length > 0 ){
                var hour = $('#hour').find("option:selected").val();
            }            
            if($('#minute').length > 0){
                var minute = $('#minute').find("option:selected").val();
            }
            if($('input[name="half-day"]').length > 0){
                var halfDay = $('input[name="half-day"]:checked').val();
            }
            var newDate;
            if($('#hour').length > 0 && $('#minute').length > 0){
                newDate = year+'-'+month+'-'+day+' '+hour+':'+minute;
            }else if($('input[name="half-day"]').length > 0){
                newDate = year+'-'+month+'-'+day+' '+halfDay;
            }else{
                newDate = year+'-'+month+'-'+day+' '+hour+':00';
            }
           $(this.el).val(newDate);
           return newDate;
        }


    }

    $.fn.timeSelect = function(option){
        var el = this;
        options = $.extend({},option,{el:el});
        var data = $(this).data("timeSelect");
        if(!data){
           $(this).data("timeSelect",(data = new TimePicker(options)));
        }
        return $(this).data("timeSelect");        
    }

}))