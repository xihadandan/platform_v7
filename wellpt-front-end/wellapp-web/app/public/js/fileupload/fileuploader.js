function fileupload(attach,fileupload,messages,ctx){
	var file_id = "";
	$attach = attach;
//	$attach.val(new UUID().id);
	$fub = fileupload;
	$messages = messages;
//	if (!window.ctx) {
//		window.ctx = "/" + window.location.pathname.split("/")[1];
//	}
	// 获取cookie
	function getCookie(name) {
		var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
		if (arr) {
			return decodeURI(arr[2]);
		} else {
			return null;
		}
	}
	// 确保contextPath有值
	function getContextPath() {
		if (window.ctx == null || window.contextPath == null) {
			window.contextPath = getCookie("ctx");
			window.contextPath = window.contextPath == "\"\"" ? "" : window.contextPath;
			window.ctx = window.contextPath;
		}
		return window.ctx;
	}
	getContextPath();
	var ctx = window.ctx;

	var uploader = new qq.FineUploaderBasic(
			{
				button : $fub[0],
				request : {
					endpoint : ctx	+ '/repository/file/uploadfile.action?attach='
							+ $attach.val()
				},


				 callbacks: {
				        onSubmit: function(id, fileName) {
				        //  $messages.append('<div id="file-' + id + '" class="alert" style="margin: 20px 0 0"></div>');
				          var ulClass = $messages.find("ul").attr("class");
							var ID = $("."+ulClass).find("li:last").attr("id");
							var ID_1 = "";
							if(ID == undefined) {
								ID = 0;
								file_id = ID;
							}else {
								ID_1 = ID.split("-");
								file_id = ID_1[1];
							}
							window.newId = parseInt(file_id)+parseInt(1);
							$messages.find("ul").append('<li filename="'+fileName+'" id="file-' + newId + '"></li>');
				        },
				        onUpload: function(id, fileName) {
				          $('#file-' + newId).addClass('alert-info')
				                          .html('<img src="loading.gif" alt="等待中！"> ' +
				                                'Initializing ' +
				                                '“' + fileName + '”');
				        },
				        onProgress: function(id, fileName, loaded, total) {
				          if (loaded < total) {
				            progress = Math.round(loaded / total * 100) + '% of ' + Math.round(total / 1024) + ' kB';
				            $('#file-' + newId).removeClass('alert-info')
				                            .html('<img src="loading.gif" alt="上传中！"> ' +
				                                  'Uploading ' +
				                                  '“' + fileName + '” ' +
				                                  progress);
				          } else {
				            $('#file-' + newId).addClass('alert-info')
				                            .html('<img src="loading.gif" alt="正在保存！"> ' +
				                                  'Saving ' +
				                                  '“' + fileName + '”');
				          }
				        },
				        onComplete: function(id, fileName, responseJSON) {
				          if (responseJSON.success) {
				        	  $('#file-' + newId).removeClass('alert-info')
	                            .addClass('alert-success');
				        	  var filename2 = fileName.split(".");
								if(filename2.indexOf("txt") > -1) {
									 $('#file-' + newId)
										.html('<img src="'+ctx+'/resources/form/file_txt.png" alt="" /><span>'+fileName+'</span>'
												);
								 }else if(filename2.indexOf("doc") > -1 || filename2.indexOf("docx") > -1 ) {
									 $('#file-' + newId)
										.html('<img src="'+ctx+'/resources/form/word.png" alt="" /><span>'+fileName+'</span>'
												);
								 }else if(filename2.indexOf("rar") > -1 || filename2.indexOf("zip") > -1) {
									 $('#file-' + newId)
										.html('<img src="'+ctx+'/resources/form/rar.png" alt="" /><span>'+fileName+'</span>'
												);
								 }else if(filename2.indexOf("png") > -1 || filename2.indexOf("jpg") > -1) {
									 $('#file-' + newId)
										.html('<img src="'+ctx+'/resources/form/jpg.png" alt="" /><span>'+fileName+'</span>'
												);
								 }
								 else{
									 $('#file-' + newId)
										.html('<img src="'+ctx+'/resources/form/word.png" alt="" /><span>'+fileName+'</span>'
							);}
				          } else {
				            $('#file-' + newId).removeClass('alert-info')
				                            .addClass('alert-error')
				                            .html('<i class="icon-exclamation-sign"></i> ' +
				                                  '上传错误' +
				                                  '“' + fileName + '”: ' +
				                                  responseJSON.error);
				          }
//				      	//实现点击选中
//				          $('#file-' + id).unbind("click");
//				          $('#file-' + id).click(function(){
//				        	    alert(1);
//								if($(this).attr("class") != null && $(this).attr("class") != "") {
//									alert(2);
//									$(".attach-list").removeClass("bar");
//								}else {
//									alert(3);
//									$(".attach-list").addClass("bar");
//								}
//							});
				        //实现点击选中
							$(".attach-list").find("li").unbind("click");
							$(".attach-list").find("li").click(function(){
								if($(this).attr("class") != "" && $(this).attr("class").indexOf("bar") > -1  ) {
									$(this).removeClass("bar");
								}else {
									$(this).addClass("bar");
								}
							});
				          
							
							//双击下载
				          $('#file-' + newId).unbind("dblclick");
				          $('#file-' + newId).dblclick(function(){
								location.href = ctx + '/repository/file/downloadtemp.action?filename=' + fileName + "&attach=" + $attach.val();
							});
				        },

				          onError: function(id, name, reason, xhr) {
				              $('#fubErrorAlert .message').text(reason);

				              $('#fubErrorAlert button').click(function() {
				                  $('#fubErrorAlert').hide();
				              });

				              $('#fubErrorAlert').show();
				          }
				        }
				      
			});
//	var uploader = new qq.FineUploaderBasic(
//			{
//				button : $fub[0],
//				request : {
//					endpoint : ctx	+ '/repository/file/uploadfile.action?attach='
//							+ $attach.val()
//				},
//				callbacks : {
//					onSubmit : function(id,
//							fileName) {
//						
//						var ulClass = $messages.find("ul").attr("class");
//						var ID = $("."+ulClass).find("li:last").attr("id");
//						var ID_1 = "";
//						if(ID == undefined) {
//							ID = 0;
//							file_id = ID;
//						}else {
//							ID_1 = ID.split("-");
//							file_id = ID_1[1];
//						}
//						$messages.find("ul").append('<li filename="'+fileName+'" id="file-' + id + '"></li>');
////						return qq.Promise;
//						
//					},
//					onUpload : function(id,
//							fileName) {
//						$('#file-' + id)
//								.html(
//										'<img src="'+ctx+'/resources/fileupload/loading.gif" alt="Initializing. Please hold."> '
//												+ 'Initializing '
//												+ '“'
//												+ fileName
//												+ '”');
//					},
//					onProgress : function(id,
//							fileName, loaded, total) {
//						 if (loaded < total) {
//					            progress = Math.round(loaded / total * 100) + '% of ' + Math.round(total / 1024) + ' kB';
//					            $('#file-' + id).removeClass('alert-info')
//					                            .html('<img src="client/loading.gif" alt="In progress. Please hold."> ' +
//					                                  'Uploading ' +
//					                                  '“' + fileName + '” ' +
//					                                  progress);
//					          } else {
//					            $('#file-' + id).addClass('alert-info')
//					                            .html('<img src="client/loading.gif" alt="Saving. Please hold."> ' +
//					                                  'Saving ' +
//					                                  '“' + fileName + '”');
//					          }
//						
////						if (loaded < total) {
////							progress = Math
////									.round(loaded
////											/ total
////											* 100)
////									+ '% of '
////									+ Math
////											.round(total / 1024)
////									+ ' kB';
////							$('#file-' + (parseInt(file_id)+1))
////									.html(
////											'<img src="'+ctx+'/resources/fileupload/loading.gif" alt="In progress. Please hold."> '
////													+ 'Uploading '
////													+ '“'
////													+ fileName
////													+ '” '
////													+ progress);
////						} else {
////							$('#file-' + (parseInt(file_id)+1))
////									.html(
////											'<img src="'+ctx+'/resources/fileupload/loading.gif" alt="Saving. Please hold."> '
////													+ 'Saving '
////													+ '“'
////													+ fileName
////													+ '”');
////						}
//					},
//					onComplete : function(id,
//							fileName, responseJSON) {
//						if (responseJSON.success) {
//							var filename2 = fileName.split(".");
//							if(filename2[1] == "txt") {
//								 $('#file-' + id)
//									.html('<img src="'+ctx+'/resources/form/txt.PNG" alt="" /><span>'+fileName+'</span>'
//											);
//							 }else if(filename2[1] == "doc" || filename2[1] == "docx" ) {
//								 $('#file-' + id)
//									.html('<img src="'+ctx+'/resources/form/word.png" alt="" /><span>'+fileName+'</span>'
//											);
//							 }else if(filename2[1] == "rar" || filename2[1] == "zip") {
//								 $('#file-' + id)
//									.html('<img src="'+ctx+'/resources/form/rar.PNG" alt="" /><span>'+fileName+'</span>'
//											);
//							 }
////							$('#file-' + id)
////									.html('<img src="'+ctx+'/resources/form/word.png" alt="" /><span>'+fileName+'</span></a>'
////											);
//							
							
//							
//							//双击下载
//							$(".attach-list").find("li").unbind("dblclick");
//							$(".attach-list").find("li").dblclick(function(){
//								location.href = ctx + '/repository/file/downloadtemp.action?filename=' + fileName + "&attach=" + $attach.val();
//							});
//							
							//删除
							$("#file_delete").unbind("click");
							$('#file_delete').click(function() {
								if(confirm("确定要删除选中的文件吗?")){
								$(".attach-list li").each(function(){
									var name = $(this).attr("class");
									if(name == "alert-success bar") {
										var file = $(this).attr("filename");
										var fileId = $(this).attr("id");
										$.ajax({
											type : "post",
											url : ctx + '/repository/file/deleteform.action',
											data : {"filename": file,"attach": $attach.val()},
											success : function(data) {
													$('#'+fileId).remove();
											},
											Error : function(data) {
												alert(data);
											}
										});
										
									}
								});
								}else {
									return false;
								}
							}); 
							//清空
							$("#file_clear").unbind("click");
							$('#file_clear').click(function() {
								if(confirm("确定要清空文件吗?")){
								$(".attach-list").find("li").each(function(){
									var file = $(this).attr("filename");
									var fileId = $(this).attr("id");
										$.ajax({
											type : "post",
											url : ctx + '/repository/file/deleteform.action',
											data : {"filename": file,"attach": $attach.val()},
											success : function(data) {
													$('#' + fileId).remove();
											},
											Error : function(data) {
												alert(data);
											}
									});
								});
								}else {
									return false;
								}
							});
//						} 
//					}
//				}
//			});
}

function filedelete(attach,filename,file){
	$attach = attach;
	$attach.val($attach.val()+filename);
	file.remove();
}