;
(function($) {
	$.fileuploaders = {
		
		//只显示标准附件框,需传入附件域id
		onlyFileShow: function(id) {
			var fileHtml = '<div class="post-info">'+
			'<div class="post-hd">'+
		    '附件'+
		    '<div class="extral">'+
		    '<span class="btn btn-success fileinput-button">'+
		    '<i></i>'+
		    '<span>上传</span>'+
			"<input id='"+id+"' type='file' name='files' multiple>"+
			'</span></div></div></div>';
			return fileHtml;
		},	
		
		//同时展示正文与附件
		FileShowAndZwShow:function(id) {
			var fileHtml = '<div class="post-info">'+
			'<div class="post-hd">'+
		    '正文 / 附件'+
		    '<div class="extral">'+
		    '<input type="button" id="body_col" name="body_col" value="+ 新建正文" class="Body_col"/>'+
		    '<span class="btn btn-success fileinput-button">'+
		    '<i></i>'+
		    '<span>上传附件</span>'+
			"<input id='"+id+"' type='file' name='files' multiple>"+
			'</span></div></div></div>';
			return fileHtml;
		},
		
		appendButton: function() {
			var fileHtml = '<div id="file_clear" class="extral c">'
				+'<i></i> 清空'
				+'</div>'
				+'<div id="file_delete" class="extral d">'
				+'<i></i> 删除'
				+'</div>';
			return fileHtml;
		},
		
		
		
		//简要样式附件
		fileHtml:function() {
			var html = "";
			if($('#upload_div')!=undefined) {
				 html = '<div class="upload_div" id="upload_div">'+
					'<span class="btn btn-success fileinput-button2">'+
					'<span class="file_icon"></span>'+
					'<span class="add_icon">添加附件</span>'+
					'<input id="fileupload" type="file" name="files[]" multiple class="fileupload_css">'+
					'</span></div>'+
					'<div id="files">'+
					'</div>'+
					"<input type='hidden' id='attach' value='"+new UUID().id+"'>"
					;
			}
			return html;
		},
		//简要样式附件的上传方法
		upload: function() {
			var url = ctx	+ '/repository/file/uploadfile.action?attach='+$("#attach").val();
			$('#fileupload').fileupload({
		        url: url,
		        dataType: 'json',
		        autoUpload: true,
		        maxFileSize: 5000000, // 5 MB
		        // Enable image resizing, except for Android and Opera,
		        // which actually support image resizing, but fail to
		        // send Blob objects via XHR requests:
		        previewMaxWidth: 100,
		        previewMaxHeight: 100,
		        previewCrop: true
		    }).on('fileuploadadd', function (e, data) {
		        $.each(data.files, function (index, file) {
		        	var html = '<div class="files_div"><span class="file_icon"></span><p class="filename">'+file.name+'</p>'+
		        	' <div id="progress" class="progress progress-success progress-striped progress-div"><div class="bar bar-div" ></div></div>'+
		        	"<div class='delete_Div'><input type='button' id='delete' class='delete_input' filename='"+file.name+"' value='删除' /></div></div>";
		           $("#files").append(html);
		        });
		    }).on('fileuploadprogressall', function (e, data) {
		        var progress = parseInt(data.loaded / data.total * 100, 10);
		        $('#progress .bar').css(
		            'width',
		            progress + '%'
		        );
		    }).on('fileuploaddone', function (e, data) {
		        $.each(data.files, function (index, file) {
		        	$("#progress").remove();
		        	$(".filename").unbind("click");
		        	 $('.filename').click(function(){
			        	  	var filename = $(this).text();
							location.href = ctx + '/repository/file/downloadtemp.action?filename=' + filename + "&attach=" + $("#attach").val();
						});
		        	//删除
		        	 $("#delete").die().live("click",(function(){
		        		 var file =  $('#delete').attr("filename");
		 					var $this = $(this);
		 					$.ajax({
		 						type : "post",
		 						url : ctx + '/repository/file/deleteform.action',
		 						data : {"filename": file,"attach": $("#attach").val()},
		 						success : function(data) {
		 							$this.parent().parent().remove();
		 						},
		 						Error : function(data) {
		 							alert(data);
		 						}
		 					});
		        	 })); 
		        });
		    }).on('fileuploadfail', function (e, data) {
		        $.each(data.result.files, function (index, file) {
		            var error = $('<span/>').text(file.error);
		            $(data.context.children()[index])
		                .append('<br>')
		                .append(error);
		        });
		    });
		},
		
		downFileHtml:function() {
			var html = '<div class="files_div"></div>';
			return html;
		},
		
		downFile:function(moduleName,nodeName,supportDown,fileName) {
			for(var index=0;index<fileName.length;index++) {
				$(".files_div").append("<p>"+fileName[index]+"</p>");
			}
			$(".files_div p").click(function() {
				if(supportDown == "2") {
					location.href = ctx + '/dytable/file_show.action?fileName=' + $(this).text() + "&modulename="+moduleName+"&nodename=" + nodeName;
				}else {
					location.href = ctx + '/repository/file/download.action?filename=' + $(this).text()+ '&modulename='+moduleName+'&nodename='+nodeName;
				}
			}); 
		},
		
		//从表将指定字段定义为附件
		addFileSubField:function(param) {
			var tableId = param.tableId;
			var $dg = $('#' + tableId);
			var ids = param.rowid;
			var fieldName = param.colEnName;//附件字段的名字
			var idValue = ids;
				
			 var uploadButton = "<div id='attachContainer_" + idValue + fieldName + "'></div>"
			 $dg.jqGrid('setCell',idValue,fieldName,uploadButton);
			 
			 var $attachContainer = $("#attachContainer_" + idValue + fieldName) //上传控件要存放的位置,为jquery对象 
			  
			 console.log( "4:" + idValue); 
			 
			 //创建上传控件
			 var elementID = WellFileUpload.getCtlID4Dytable(tableId, fieldName, idValue);
			 
			 var fileupload = new WellFileUpload(elementID);

			//初始化上传控件
			fileupload.init(false,  $attachContainer,  formSign == "2", true, []);
				
		}
	};
	fileuploader_upload = function(){
		$.fileuploaders.upload();
	};
	downFile = function() {
		return $.fileuploaders.downFile();
	};
	downFileHtml = function() {
		return $.fileuploaders.downFileHtml();
	};
	fileHtmlStr = function(){
		return $.fileuploaders.fileHtml();
	};
	
})(jQuery);