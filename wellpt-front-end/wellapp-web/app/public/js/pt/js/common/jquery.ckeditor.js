(function($) {
	ckeitorFormat();
})(jQuery);

function ckeitorFormat(){

	var obj = $(".ckeditor");
	var height_ = obj.attr("height");
	var type_ = obj.attr("type");
	$.each(obj,function(index){ 
		//判断是否存在id属性
		if(this.id.length==0){
			this.id="ckeditor";
		} 
		//初始化编辑器
		if(type_=="simple"){
			CKEDITOR.replace( this.id, {  
				allowedContent:true,
				enterMode: CKEDITOR.ENTER_P,
				
				//工具栏
				toolbar: [ 
				          ['Bold','Italic','Underline','Font','FontSize','TextColor','BGColor','JustifyLeft','JustifyCenter','JustifyRight','NumberedList','BulletedList','Link','Image','Source','Maximize','Preview']
				          ],
			
				 on: {
				        instanceReady: function( ev ) {
				            // Output paragraphs as <p>Text</p>.
				            this.dataProcessor.writer.setRules( 'p', {
				                indent: true,
				                breakBeforeOpen: false,
				                breakAfterOpen: false,
				                breakBeforeClose: false,
				                breakAfterClose: false,
				            });
				        },
				        loaded: function( ev ){
				        	if(height_!=undefined){
				        		$(".cke_contents").css("height",height_);
				        	}
				        }
				    }
			 });
		}else{
			CKEDITOR.replace( this.id, {  
				allowedContent:true,
				enterMode: CKEDITOR.ENTER_P,
				
				//工具栏
				toolbar: [ 
				          ['Bold','Italic','Underline'], ['Cut','Copy','Paste'], 
				          ['NumberedList','BulletedList','-'], 
				          ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
				          ['Link','Unlink'],['Format','Font','FontSize'],['TextColor','BGColor'],
				          ['Image','Table','Smiley'],['Source','Maximize']
				          ],
				 on: {
				        instanceReady: function( ev ) {
				            // Output paragraphs as <p>Text</p>.
				            this.dataProcessor.writer.setRules( 'p', {
				                indent: true,
				                breakBeforeOpen: false,
				                breakAfterOpen: false,
				                breakBeforeClose: false,
				                breakAfterClose: false,
				            });
				        },
				        loaded: function( ev ){
				        	if(height_!=undefined){
				        		$(".cke_contents").css("height",height_);
				        	}
				        }
				    }
			 });
		}
    }); 
}