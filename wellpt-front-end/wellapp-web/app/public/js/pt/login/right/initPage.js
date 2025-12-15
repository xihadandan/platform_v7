function initPage(){
	var width = document.body.scrollWidth;
	var height = document.body.scrollHeight;
	
	var boxWidth = $('.box').width() + 16;  //加上padding
	var boxHeight = $('.box').height() + 22;
	
	var left = (width - boxWidth) / 2;
	var top = (height - boxHeight) / 2;

	var contentHeight = Math.round(width * 0.42);
	var headerHeight = Math.round((height - contentHeight) * 9 / 20);
	
	
	$('.header').css('height', headerHeight + 'px').css('width', width + 'px');
	
	if(headerHeight > 90){
		$('.header').css('line-height', headerHeight + 'px');
	}
	
	$('.box').css('top', top - 10).css('right', 64).css('left', width - boxWidth - 64);
	
	var footerHeight = Math.round((height - contentHeight) * 11 / 20);
	if(footerHeight < 110){
		footerHeight = 110;
	}
	
	$('.footer').css('top', height - footerHeight).css('height', footerHeight).css('width', width + 'px');
	
	$('.content').css('background-size', width + 'px ' + contentHeight + 'px');
	$('.content').css('width', width).css('height', height).css('background-position-y', headerHeight + 'px');	
}

$(document).ready(function(){

	initPage();
	//使密码框与硬件框高度相等
	var accountHeight = $('.account').height();
	if($('.hardware').length > 0 && accountHeight > $('.hardware').height()){
		var hardwareHeight = $('.hardware').height();
		
		var paddingBottom = Math.floor((accountHeight - hardwareHeight) / 3);
		var paddingBottomEnd = paddingBottom + (accountHeight - hardwareHeight) % 3; 
		$('.hardware ul li').each(function(i){
			if(i == 0){
				$(this).css('padding-bottom', paddingBottomEnd + 'px');
			}else{
				$(this).css('padding-bottom', paddingBottom + 'px');
			}
		});
	}
	
	
	window.onresize = function() {
		initPage();	
	};
	
	$('.password-li > a').on('click', function(){
		var type = $(this).prev().attr('type');
		var value = $(this).prev().val();
		if(type == 'password'){
			$(this).prev().replaceWith('<input type="text" id="password" placeholder="请输入密码" value = "' + value + '">');
			$(this).addClass('aA');
		}else{
			$(this).prev().replaceWith('<input type="password" id="password" placeholder="请输入密码" value = "' + value + '">');
			$(this).removeClass('aA');
		}
		$('.password-li > input').on('click', function(){
			inputFocus(this);
		});
	});
	
	$('.operating-li > .remember-username').on('click', function(){
		if($(this).hasClass('check')){
			$(this).removeClass('check');
			
		}else{
			$(this).addClass('check');
		}
	});
	
	$('.operating-li > .remember-password').on('click', function(){
		if($(this).hasClass('check')){
			$(this).removeClass('check');
		}else{
			$(this).addClass('check');
		}
	});
	
	$('.account input[type="text"], .account input[type="password"]').on('click', function(){
		inputFocus(this);
	});
	
	$('.nav li').on('click', function(){
		
		var target = $(this).attr('target');
		$('.nav li').each(function(){
			$(this).find('span').removeClass('spanA');	
		});
		$(this).find('span').addClass('spanA');
		$('.login > div').hide();
		$('.' + target).show();
		$('.error-div').remove();
	});
})

function inputFocus(target){
	$('.account input[type="text"], .account input[type="password"]').each(function(){
		$(this).parent().removeClass('select');
	})
	var $li = $(target).parent();
	$li.addClass('select');
}