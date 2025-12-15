(function(){

	if(typeof CkPlugin == "undefined"){
		return;
	}
	var pluginName =   CkPlugin.INDENT; 
	CKEDITOR.plugins.add(pluginName,{
		init:function(a){
			 //定义一个按钮,用于触发缩进按钮
            a.ui.addButton(pluginName, {
                label: "整体缩进居中",//按钮的名称
                command: pluginName,
                icon: this.path + "images/anchor.png"//在toolbar中的图标
            });
            
          //定义命令，用于缩进操作
            a.addCommand(pluginName,{
	        	 exec : function(a)
	        	    {
	        		 
	        		 //获取到iframe里的body
	        		 
	        		 var editorBody = a.document.getBody();
	        		 
	        		 //获取富文本框的宽度
	        		 
	        		 var bodyWidth = editorBody.getClientRect().width;
	        		 
	        		//检查是否存在div;
	        		 
	        		 var div = editorBody.find(".textContainer");
	        		 
	        		 if(div.$.length == 0){
		        			 //获取选择内容
	        			 var contentArr = editorBody.find("p");
	        			 
		        		 var el = [];
	        		 
		        		 if(contentArr.$.length == 0){			        			 
		        			alert("请输入相应的内容！");
		        			return;
		        		 }else if(contentArr.$.length == 1 && contentArr.$[0].innerHTML == "<br>"){
		        				alert("请输入相应的内容！");
			        			return;
		        			}		        		 			        		 
		        		 else{	
		        			 //移除空p标签
		        			 for(var i=0;i<contentArr.$.length;i++){
			        			 if(contentArr.$[i].innerHTML == "<br>"|| contentArr.$[i].innerHTML == "&nbsp;"){
			        				contentArr.$[i].remove();
			        			 }else{
			        				el.push(contentArr.$[i]);
			        			 }			        			 
			        		 }	        			
		        		 }
        		 
	        			//新建一个外部div
		        		 var divWidth = bodyWidth-50;
		        		 
		        		 var container = a.document.createElement("div").addClass("textContainer").setStyles( {'margin':'0 auto',"width":divWidth+"px","min-height":"590px","outline":"none"} );

		        		 container.appendTo(editorBody);
		        		 
		        		 editorBody.setAttribute("contenteditable","false");
		        		 
		        		 container.setAttribute("contenteditable","true");
		        		 
		        		 for(var i in el){
		        			 var elNode = CKEDITOR.dom.node(el[i])

		        			 container.append(elNode);
		        		 }
		        		 
	        		 }else{
	        			 var divWidth = a.document.getBody().findOne("div").$.clientWidth;
	        			 
	        			 divWidth = divWidth - 50;
	        			 
	        			 a.document.getBody().findOne("div").setStyle("width",divWidth+"px");
	        			 
	        		 }	        		 
	        		 
	        	    }
            });
		}
	})
})()