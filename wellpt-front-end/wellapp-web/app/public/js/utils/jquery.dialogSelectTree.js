/*
 * 使用时需自行运用dialog和ztree相关包.多次引包可能导致异常(比如：出现两个选择框)。需注意
 * 只开放少数部分参数，如需要可以自行添加
 * 如果出现末级节点显示的图标是文件夹，在对应页面加入如下样式。
 * .dialogSelectTree .ztree li span.button.ico_docu {
 *   background-position: -110px -32px;
 *   margin-right: 2px;
 *   vertical-align: top;
 *  部分参数说明 
 *  options.jsonObjce 可以被ztree解析的json對象和options.url必須有一個
 *  options.url 可以返回ztree解析的json的web 地址和options.jsonObjce必須有一個
 *	options.otherValueKeys --树对应的json对象的导航，如：{"extra":{"id":"aa"}}域取ID值，则为extra.id。需求多值请用分号“;”隔开
 *  options.otherValueFileds--控件ID，会将options.otherValueKeys对应的值设置对应的控件中，多值请用分号“;”隔开
 *  options.valueFiledKey--树唯一标示json对象导航。树打开和关闭时，valueFiledKey取值对应的json对象哪个值。格式与otherValueKeys相同
 *  options.labelFiledKey--树labelFile取值标示json对象导航。树打开和关闭时，valueFiledKey取值对应的json对象哪个值。格式与otherValueKeys相同
 *  事件说明
 *  options.close 关闭的时候触发
 *  options.open 打开的时候触发
 *  options.onOk 确认的时候触发(返回值为false时，不关闭弹窗)
 *  options.onCancel 取消的时候触发
 */
(function($) {
	var dialogHtml = "<div style='margin-left: 40px;' class='dialogSelectTree'><div>"+
	"名称：<input id='findDialogName_dialog' name='findDialogName_dialog' type='text'  style='width:45%'  />"+
	" <input type='button' id='treeSearch_dialog' value='搜索'  /></div>"+
	"<div style='height:450px;overflow:auto;'><ul id='dialogOrgTreesssss' class='ztree'></ul></div>" +
	"</div>";
	var orgTreedd;
	$.dialogSelectTree={
		open:function(options){
			dialogOption = {
				title:"弹框标题",  /*****标题******/
				autoOpen: true,  /*****初始化之后，是否立即显示对话框******/
				modal: true,     /*****是否模式对话框******/
				closeOnEscape: true, /*****当用户按 Esc 键之后，是否应该关闭对话框******/
				draggable: true, /*****是否允许拖动******/  
				resizable: true, /*****是否可以调整对话框的大小******/  
				defaultBtnName: "取消",
				maxHeight: 700, /*****标题******/
				width: 380   /*****标题******/
			};
			if(options.title)
				dialogOption.title = options.title;
			
			zTreeOption = {
				treeId:"dfdsfksfjl",
				edit : {
					enable : true,
					showRemoveBtn : false,
					showRenameBtn : false
				},
				check: {
					enable: true,
					chkStyle: "checkbox",
					chkboxType: { "Y": "", "N": "" },
					radioType : "all" 
				},
				data : {
					simpleData : {
						enable : true
					}
				},
				view : {
					dblClickExpand: true,
					txtSelectedEnable: true
				},
				async:{
					enable: false
				}
			};
			if(options.treeId)
				zTreeOption.treeId=options.treeId;
			if(options.chkStyle)
				zTreeOption.check.chkStyle=options.chkStyle;
			if(options.check)
				zTreeOption.check=options.check;
			if(options.async)
				zTreeOption.async=options.async;
			dialogOption.open = function(event, ui){
				$("#dialogModule_shdkhfd .dialogcontent").html(dialogHtml);
				//pageLock("show", "读取中...");
				if(options.open){
					options.open(event, ui);
				}
				if(options.jsonObjce){
					orgTreedd = $.dialogSelectTree.initZtree(options,zTreeOption,options.jsonObjce);
				}else if(options.url){
					$.dialogSelectTree.loadJsonTree(options,zTreeOption);
				}
				
			};
			
			if(options.close){
				dialogOption.close= options.close;
			}
			
			dialogOption.buttons= {
			         "确认": function() {
			        	 var value = {};
			        	 value["ids"] ="";
			        	 value["names"] ="";
			        	 var otherValueFileds =new Array();
			        	 var otherValueKeys =new Array();
			        	 if(options.otherValueFileds&&options.otherValueKeys){
			        		 otherValueFileds = options.otherValueFileds.split(";");
			        		 otherValueKeys = options.otherValueKeys.split(";");
			        	 }
			        	 if(otherValueFileds.length==otherValueKeys.length){
			        		 for(var i = 0;i<otherValueFileds.length;i++){
			        			 value[otherValueFileds[i]]="";
			        		 }
			        	 } 	 
			        	 var checkOk = true;
			        	 if(orgTreedd){
			        		 var nodes =  orgTreedd.getCheckedNodes(true);
			        		 for(var i=0;i<nodes.length;i++){
			        	        if(i!=0){
			        	        	for(var key in value ){
			        	        		value[key]+=";";
			        	        	}
			        	        }
			        	        if(options.valueFiledKey){
			        	        	value["ids"]+=$.dialogSelectTree.getNodeValueByKey(nodes[i],options.valueFiledKey);
			        	        }else{
			        	        	value["ids"]+=nodes[i].id;
			        	        }
			        	        
			        	        if(options.labelFiledKey){
			        	        	value["names"]+=$.dialogSelectTree.getNodeValueByKey(nodes[i],options.labelFiledKey);
			        	        }else{
			        	        	value["names"]+=nodes[i].name;
			        	        }
			        	        
			        	        if(otherValueFileds.length==otherValueKeys.length){
					        		 for(var j = 0;j<otherValueKeys.length;j++){
					        			 var temp =$.dialogSelectTree.getNodeValueByKey(nodes[i],otherValueKeys[j]);
					        			 value[otherValueFileds[j]]=temp;
					        		 }
					        	 }
			        	        
			        	     }
			        		 if(options.onOk){
				        		 var re = options.onOk(orgTreedd,value["ids"],value["names"],value);
				        		 if(re==false){
				        			 checkOk = false;
				        		 }
				        	 }
			        		 if(checkOk){
			              		 if(options.valueFiled){
				        			 $("#"+options.valueFiled).val(value["ids"]);
				        		 }
				        		 if(options.labelFiled){
				        			 $("#"+options.labelFiled).val(value["names"]);
				        		 }
				        		 if(otherValueFileds.length==otherValueKeys.length){
					        		 for(var j = 0;j<otherValueFileds.length;j++){
					        			 $("#"+otherValueFileds[j]).val(value[otherValueFileds[j]]);
					        		 }
					        	 }
			        		 }
			        	 }
			       
			        	 if(checkOk){
			        		 $(this).dialog('close');
			        	 }
			         },
			         "取消": function() {
			        	 if(options.onCancel){
			        		 options.onCancel(orgTreedd);
			        	 }
			             $(this).dialog('close');
			         }
			     };
			str = '<div id="dialogModule_shdkhfd" title="" style="padding:0;margin:0; display:none;"><div class="dialogcontent"></div></div>';
			if ($("#dialogModule_shdkhfd .dialogcontent").html() == undefined) {
				$("body").after(str);
			}
			$("#dialogModule_shdkhfd").dialog(dialogOption);
			//showDialog(dialogOption);
		},
		initZtree:function(options,zTreeOption,result){
			orgTreedd = $.fn.clone().zTree.init($("#dialogOrgTreesssss"),
					zTreeOption, result);
			
			$("#treeSearch_dialog").click(function(){
				if(zTreeOption.async.enable){
					$.dialogSelectTree.loadJsonTree(options,zTreeOption);
				}
				$.dialogSelectTree.checkTreeByNameOrCode(orgTreedd ,$("#findDialogName_dialog").val());
			});
			$("#findDialogName_dialog").keydown(function(event){
				if(event.keyCode==13){  
					if(zTreeOption.async.enable){
						$.dialogSelectTree.loadJsonTree(options,zTreeOption);
					}
					$.dialogSelectTree.checkTreeByNameOrCode(orgTreedd ,$("#findDialogName_dialog").val());
				}
			});
			 if(options.valueFiled){
    			 var idStr = $("#"+options.valueFiled).val();
    			 var ids = idStr.split(";");
    			 for(var i=0;i<ids.length;i++){
    				 if(orgTreedd&&ids[i]!=""){
    					 var node ;
    					 if(options.valueFiledKey){
    						 var nodes =  orgTreedd.getNodes();
    						 for(var j =0;j< nodes.length;j++){
    							 node = $.dialogSelectTree.recursionTree(nodes[j],options.valueFiledKey,ids[i]);
    							 if(node)
    								 break;
    						 }
    						 
    					 }else{
    						 node = orgTreedd.getNodeByParam("id",ids[i])
    					 }
        				 if(node){
        					 //orgTreedd.expandNode(node, true, true, false);
        					 orgTreedd.checkNode(node,true);
        					 
        				 }
    				 }
    			 }
    		 }
			 return orgTreedd;
		},
		checkTreeByNameOrCode:function(tree ,name){
			if (name == "") {
				tree.cancelSelectedNode();
				return;
			}
			tree.cancelSelectedNode();
			tree.expandAll(false);
			var rootnode = tree.getNodes();
			tree.expandNode(rootnode[0], true, false, false);
			var nodes = new Array();
			nodes = tree.getNodesByParamFuzzy("name", name, null);
			expandParent(tree,nodes);
		},
		expandParent:function(tree,nodes){
			for (var i = 0; i < nodes.length; i++) {
				tree.selectNode(nodes[i], true);
				var parentnode = nodes[i].getParentNode();
				while (parentnode != null) {
					//展开节点
					if (parentnode.getParentNode() != null) {
						tree.expandNode(parentnode, true, false, false);
					} else
						break;
					parentnode = parentnode.getParentNode();
				}
			}
		},
		getNodeValueByKey:function(node,valueKey){
			var valueKeys = valueKey.split(".");
			 var temp = node;
			 for(var z =0; z<valueKeys.length;z++){
				 temp = temp[valueKeys[z]];
			 }
			 return temp;
		},
		recursionTree:function(nodes,valueKey,value){
			var noteValue = $.dialogSelectTree.getNodeValueByKey(nodes,valueKey);
			if(noteValue == value){
				return nodes;
			}else{
				if(nodes.children){
					for(var i =0;i< nodes.children.length;i++){
						var re = $.dialogSelectTree.recursionTree(nodes.children[i],valueKey,value);
						if(re){
							return re;
						}
					}
				}
			}
			return null;
		},
		loadJsonTree:function(options,zTreeOption){
			$.ajax({
				url : options.url,
				type : "POST",
				async:false,
				data:{
					searchValue:$("#findDialogName_dialog").val(),
					chooseValue:$("#"+options.valueFiled).val()
				},
				dataType : 'json',
				success : function(result) {
					orgTreedd = $.dialogSelectTree.initZtree(options,zTreeOption,result);
					//pageLock("hide");
				},
				error : function(data) {
					alert(JSON.stringify(data));
					//pageLock("hide");
				}
			});
		}
	};
})(jQuery);
