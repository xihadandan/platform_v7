;
(function($) {
	
	var columnProperty={
			//控件字段属性
			applyTo:null,//应用于
			columnName:null,//字段定义  fieldname
			displayName:null,//描述名称  descname
			dbDataType:'',//字段类型  datatype type
			indexed:null,//是否索引
			showed:null,//是否界面表格显示
			sorted:null,//是否排序
			sysType:null,//系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
			length:null,//长度
			showType:'1',//显示类型 1,2,3,4 datashow
			defaultValue:null,//默认值
			valueCreateMethod:'1',//默认值创建方式 1用户输入
			onlyreadUrl:null,//只读状态下设置跳转的url
	};
	
	//控件公共属性
	var commonProperty={
			inputMode:null,//输入样式 控件类型 inputDataType
			fieldCheckRules:null,
			fontSize:null,//字段的大小
			fontColor:null,//字段的颜色
			ctlWidth:null,//宽度
			ctlHight:null,//高度
			textAlign:null,//对齐方式
			
	};	
	
	/*
	 * TEXTINPUT CLASS DEFINITION ======================
	 */
	var Embedded = function($placeHolder, options) {
		this.options = $.extend({}, $.fn["wembedded"].defaults, options);
		this.value = "";
		this.$editableElem = null;
		this.$labelElem = null;
		this.$placeHolder = $placeHolder;
	};

	Embedded.prototype = {
		constructor : Embedded,
	};
	
	$.Embedded = {
			 
			
			createEditableElem:function(){
				 if(this.$editableElem != null){//创建可编辑框
					 return ;
				 }
				 var options = this.options;
				 var ctlName =  this.getCtlName();
				 var editableElem = document.createElement("input");
				 editableElem.setAttribute("class", this.editableClass);
				 editableElem.setAttribute("name", ctlName);
				 editableElem.setAttribute("type", "text");
				 editableElem.setAttribute("maxlength", options.columnProperty.length);
				
				 
				 this.$placeHolder.after($(editableElem));
				 this.$editableElem = this.$placeHolder.next("." + this.editableClass);
				 var _this = this;
				  this.$editableElem.keyup(function(event){ 
					 _this.setValue(_this.$editableElem.val(), false);//设置,再不对元素再进行渲染  
				 }); 
				  this.$editableElem.bind("paste",function(event){
					  window.setTimeout(function(){
						  _this.setValue(_this.$editableElem.val(), false);//设置,再不对元素再进行渲染  
						 },100);
				 }); 
				 
				 
				 this.$editableElem.bind('change',function(){  
					 _this.setValue(_this.$editableElem.val(), false);//设置,再不对元素再进行渲染   
				 });
			 },
			 
			 //显示为lablel
			 setDisplayAsLabel:function(){
				var options = this.options;
				
				 if(this.$labelElem == null){//创建标签元素
					 var labelElem = document.createElement("iframe");
					 labelElem.setAttribute("class", this.labelClass);
					 labelElem.setAttribute("name", this.getCtlName());
					 $(labelElem).css("border", "0");
					// $(labelElem).css(this.getTextInputCss());
					
					 
					 this.$placeHolder.after($( labelElem));
					 this.$labelElem =  this.$placeHolder.next("." + this.labelClass); 
					
					
				 }
				 
				
				 
				
				 this.$labelElem.attr("src", this.value); 
				 this.$labelElem.show(); 
				 options.isShowAsLabel=true; 
				 this.hideEditableElem();
				 this.setValue2LabelElem(); 
				 if(this.setCtlField){
					 this.setCtlField();
				 }
				 
				 if(this.value == null || typeof this.value == "undefined" || $.trim(this.value).length == 0){
					 this.$labelElem.css("height", 0);
					 return;
				 }else{
					 this.$labelElem.css(this.getTextInputCss());
				 }
				 //this.addUrlClickEvent(urlClickEvent); 
			 } ,
	
	 
		  
	};
	
	/*
	 * TEXTINPUT PLUGIN DEFINITION =========================
	 */
	$.fn.wembedded = function(option) {
	 
		var method = false;
		var args = null;
		if (arguments.length == 2) {
			method = true;
			args = arguments[1];
		}
		 
		if (typeof option == 'string') {
			if(option === 'getObject'){ //通过getObject来获取实例
				var $this = $(this);
				var data = $this.data('wembedded');
                if(data){
                    return data; //返回实例对象
                }else{
                    throw new Error('This object is not available');
                }
            }
		}
	 
		 
					var $this = $(this),
					data = $this.data('wembedded'), 
					options = typeof option == 'object'
							&& option;
					if (!data) {
						 data = new Embedded($(this), options);
						 $.extend(data,$.wControlInterface);
						$.extend(data,$.wTextCommonMethod);
						$.extend(data,$.Embedded); 
						data.init();
						 $this.data('wembedded',data );
					}
					if (typeof option == 'string') {
						if (method == true && args != null) {
							return data[option](args);
						} else {
							return data[option]();
						}
					}else{
						return data;
					}
					 
				 
	};

	$.fn.wembedded.Constructor = Embedded;

	$.fn.wembedded.defaults = {
			columnProperty:columnProperty,//字段属性
			commonProperty:commonProperty,//公共属性
	        readOnly:false,
	        disabled:false,
	        isHide:false,//是否隐藏
	        isShowAsLabel:false,
	        formulas:{}
	};
	
})(jQuery);