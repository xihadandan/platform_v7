(function($){
	$.extend($.fn, {
        getCssVal: function(key) {
            var v = parseInt(this.css(key));
            if (isNaN(v))
                return false;
            return v;
        }
    });
	// to track if the mouse button is pressed
	var isMouseDown    = false;

	// to track the current element being dragged
	var currentElement = null;

	// callback holders
	var dropCallbacks = {};
	var dragCallbacks = {};
	var dragMouseDownCallbacks = {};
	//callback holders
	var resizeCallbacks = {};
	var stopCallbacks = {};
	// global position records
	var lastMouseX;
	var lastMouseY;
	var lastElemTop;
	var lastElemLeft;
	var curType = 0;//0=��ֹ;1=��ק;2=�ı��С
	
	// track element dragStatus
	var dragStatus = {};	
	//track element resizeStatus
	var resizeStatus = {};

	// returns the mouse (cursor) current position
	$.getMousePosition = function(e){
		var posx = 0;
		var posy = 0;

		if (!e) var e = window.event;

		if (e.pageX || e.pageY) {
			posx = e.pageX;
			posy = e.pageY;
		}
		else if (e.clientX || e.clientY) {
			posx = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
			posy = e.clientY + document.body.scrollTop  + document.documentElement.scrollTop;
		}

		return { 'x': posx, 'y': posy };
	};

	// updates the position of the current element being dragged
	$.updatePosition = function(e) {
		var pos = $.getMousePosition(e);

		var spanX = (pos.x - lastMouseX);
		var spanY = (pos.y - lastMouseY);
		
		$(currentElement).css("top",  (lastElemTop + spanY));
		$(currentElement).css("left", (lastElemLeft + spanX));
	};
	
	$.updateSize = function(e){
		var pos = $.getMousePosition(e);

		var spanX = (pos.x - lastMouseX);
		var spanY = (pos.y - lastMouseY);
		
		var resizeData = currentElement.resizeData;
		
		var w = Math.min(Math.max(pos.x - resizeData.offLeft + resizeData.width, resizeData.min.width), resizeData.max.width);
        var h = Math.min(Math.max(pos.y - resizeData.offTop + resizeData.height, resizeData.min.height), resizeData.max.height);
        	
        resizeData.w = w;
        resizeData.h = h;
        resizeData.target.css({
        	width: w,
			height: h
        });
	};

	// when the mouse is moved while the mouse button is pressed
	$(document).mousemove(function(e){
		
			switch(curType){
				case 1:
					if(isMouseDown && dragStatus[currentElement.id] == 'on'){
						// update the position and call the registered function
						$.updatePosition(e);
						if(dragCallbacks[currentElement.id] != undefined){
							dragCallbacks[currentElement.id](e, currentElement);
						}
					}
					break;
				case 2:
					if(isMouseDown && resizeStatus[currentElement.id] == 'on'){
						$.updateSize(e);
						if(resizeCallbacks[currentElement.id] != undefined){
							resizeCallbacks[currentElement.id](e, currentElement);
						}
					}
					break;
			}
			return false;
	});

	// when the mouse button is released
	$(document).mouseup(function(e){
		switch(curType){
			case 1:
				if(isMouseDown && dragStatus[currentElement.id] == 'on'){
					isMouseDown = false;
					if(dropCallbacks[currentElement.id] != undefined){
						var element = $(currentElement);
						element.css("z-index","1");
						dropCallbacks[currentElement.id](e, currentElement);
					}
				}
				break;
			case 2:
				if(isMouseDown && resizeStatus[currentElement.id] == 'on'){
					
					isMouseDown = false;
					//�޸Ĵ�С��10�ı���
					var pos = $.getMousePosition(e);

					var spanX = (pos.x - lastMouseX);
					var spanY = (pos.y - lastMouseY);
					
					var resizeData = currentElement.resizeData;
					
					var w = Math.min(Math.max(pos.x - resizeData.offLeft + resizeData.width, resizeData.min.width), resizeData.max.width);
			        var h = Math.min(Math.max(pos.y - resizeData.offTop + resizeData.height, resizeData.min.height), resizeData.max.height);
			        
			        var amod = w%10;
			        if(amod != 0)
			        	w = (w - amod)+10;
			        amod = h%10;
			        if(amod != 0)
			        	h = (h - amod)+10;
			        
			        resizeData.target.css({
			        	width: w,
						height: h
			        });
					//�ı������տ�͸�
					resizeData.targetW = w;
					resizeData.targetH = h;
					
					if(stopCallbacks[currentElement.id] != undefined){
						stopCallbacks[currentElement.id](e, currentElement);
					}
					document.body.onselectstart = function() { return true; }
	                resizeData.target.css('-moz-user-select', '');
	                resizeData.target.css("z-index","1");
                }
		}
		return false;
	});

	// register the function to be called while an element is being dragged
	$.fn.ondrag = function(callback){
		return this.each(function(){
			dragCallbacks[this.id] = callback;
		});
	};

	// register the function to be called when an element is dropped
	$.fn.ondrop = function(callback){
		return this.each(function(){
			dropCallbacks[this.id] = callback;
		});
	};
	
	$.fn.ondragMouseDown = function(callback){
		return this.each(function(){
			dragMouseDownCallbacks[this.id] = callback;
		});
	};
	
	// register the function to be called while an element is being resize
	$.fn.onResize = function(callback){
		return this.each(function(){
			resizeCallbacks[this.id] = callback;
		});
	};

	// register the function to be called when an element is resize Stop
	$.fn.onStop = function(callback){
		return this.each(function(){
			stopCallbacks[this.id] = callback;
		});
	};
	
	// stop the element dragging feature
	$.fn.dragOff = function(){
		return this.each(function(){
			dragStatus[this.id] = 'off';
		});
	};
	
	
	$.fn.dragOn = function(){
		return this.each(function(){
			dragStatus[this.id] = 'on';
		});
	};
	
	// stop the element resize feature
	$.fn.resizeOff = function(){
		return this.each(function(){
			resizeStatus[this.id] = 'off';
		});
	};
	
	
	$.fn.resizeOn = function(){
		return this.each(function(){
			resizeStatus[this.id] = 'on';
		});
	};

	// set an element as draggable - allowBubbling enables/disables event bubbling
	$.fn.dragable = function(opts){
		var ps = $.extend({
            handler: null,
            allowBubbling:false
        }, opts);
		return this.each(function(){

			// if no id is defined assign a unique one
			if(undefined == this.id || !this.id.length) this.id = "easydrag"+(new Date().getTime());

			// set dragStatus 
			dragStatus[this.id] = "on";
			
			if (typeof ps.handler == 'undefined' || ps.handler == null)
                handler = $(me);
            else
                handler = (typeof ps.handler == 'string' ? $(ps.handler, this) : ps.handle);
			
			// change the mouse pointer
			handler.css("cursor", "move");

			// when an element receives a mouse press
			handler.mousedown(function(e){
				var dnr;
				if(ps.handler)
					dnr = $(this).parent();
				else
					dnr = $(this);
				currentElement = dnr.get(0);
				
				lastElemTop = dnr.offset().top;
				lastElemLeft = dnr.offset().left;
				//lastElemTop  = currentElement.offsetTop;
				//lastElemLeft = currentElement.offsetLeft;
				
				// set it as absolute positioned
				dnr.css({
					"position":"absolute",
					"z-index":"10000",
					top:lastElemTop,
					left:lastElemLeft
				});

				// update track variables
				isMouseDown    = true;
				

				// retrieve positioning properties
				var pos    = $.getMousePosition(e);
				lastMouseX = pos.x;
				lastMouseY = pos.y;
				this.srcX = pos.x;
				this.srcY = pos.y;

				
				curType = 1;
				$.updatePosition(e);
				
				if(isMouseDown && dragStatus[currentElement.id] == 'on'){
					if(dragMouseDownCallbacks[currentElement.id] != undefined){
						dragMouseDownCallbacks[currentElement.id](e, currentElement);
					}
				}
				
				return ps.allowBubbling ? true : false;
			});
		});
	};
	//set an element as resizable - allowBubbling enables/disables event bubbling
	$.fn.resizable = function(opts) {
        var ps = $.extend({
            handler: null,
            min: { width: 0, height: 0 },
            max: { width: $(document).width(), height: $(document).height() }
        }, opts);
        
        return this.each(function() {
            var me = this;
            var handler = null;
            // if no id is defined assign a unique one
			if(undefined == this.id || !this.id.length) this.id = "easyresize"+(new Date().getTime());
			// set dragStatus 
			resizeStatus[this.id] = "on";
			
            if (typeof ps.handler == 'undefined' || ps.handler == null)
                handler = $(me);
            else
                handler = (typeof ps.handler == 'string' ? $(ps.handler, this) : ps.handle);
            
            handler.bind('mousedown', { e: me }, function(s) {
                var target = $(s.data.e);
                var pos    = $.getMousePosition(s);
                
                // update track variables
				isMouseDown    = true;
				currentElement = s.data.e;
				lastMouseX = pos.x;
				lastMouseY = pos.y;
				curType = 2;
				
                var resizeData = {
                    width: target.width() || target.getCss('width'),
                    height: target.height() || target.getCss('height'),
                    offLeft: pos.x,
                    offTop: pos.y,
                    target: target,
                    min: ps.min,
                    max: ps.max
                }

                document.body.onselectstart = function() { return false; }
                target.css('-moz-user-select', 'none');
                target.css("z-index","10000");
				currentElement.resizeData = resizeData;
				
				return false;
            });
        });
    }

})(jQuery);