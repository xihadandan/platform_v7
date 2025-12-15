var Bubble = function () {
  if ($('#bubble_tooltip').size() != 0) {
  } else {
    var errorHintBubble = '<div id="bubble_tooltip">';
    errorHintBubble += '<div class="bubble_top"><div>不符合要求的内容</div><i class="remove-bubble iconfont icon-ptkj-dacha"></i></div>';
    errorHintBubble += '<div class="bubble_middle scrollbar"><ul id="bubble_tooltip_content"></ul></div>';
    errorHintBubble += '<div class="bubble_bottom"></div>';
    errorHintBubble += '</div>';
    //$().append($(errorHintBubble));
    //document.write(errorHintBubble);

    $(errorHintBubble).appendTo($('body'));
  }
  this.$html = $('#bubble_tooltip');
  $('.remove-bubble').on('click', function () {
    $('#bubble_tooltip').hide();
  });
};

Bubble.prototype = {
  constructor: Bubble,
  show: function (css) {
    this.$html.show(500);

    //if(css)
    //	this.$html.attr("style", css);

    this.scroll();
  },
  //滚动
  scroll: function () {
    var top = this.$html.css('top');
    if (top == undefined || $.trim(top).length == 0 || $.trim(top) == 'px') {
      top = 100;
    } else {
      top = parseInt(top.replace('p').replace('x'), 10);
    }
    var _this = this;
    $(window).scroll(function () {
      _this.$html.animate(
        {
          top: $(window).scrollTop() + 100 + 'px'
        },
        {
          queue: false,
          duration: 30
        }
      );
    });
  },
  //隐藏
  hide: function () {
    this.$html.hide();
  },
  //关闭
  close: function () {
    this.clear();
    this.hide();
  },
  //添加错误条目data={"id":"", title:"", displayName:""}
  addErrorItem: function (data, callback) {
    if (data.id.indexOf('___') > 0) {
      var hasSmae = false;
      var ids = data.id.split('___');
      var text = ids[ids.length - 1];
      var lis = this.$html.find('#bubble_tooltip_content').find('li');
      for (var i = 0; i < lis.length; i++) {
        var id = $(lis[i]).find('a').attr('id');
        if (id && id.indexOf('___') > 0 && id.indexOf(text) > 0) {
          hasSmae = true;
          break;
        }
      }

      if (hasSmae) {
        this.$html
          .find('#bubble_tooltip_content')
          .append("<li style='display: none'><a id='" + data.id + "' title='" + data.title + "'>" + data.displayName + '</a></li>');
      } else {
        this.$html
          .find('#bubble_tooltip_content')
          .append("<li><a id='" + data.id + "' title='" + data.title + "'>" + data.displayName + '</a></li>');
      }
    } else {
      this.$html
        .find('#bubble_tooltip_content')
        .append("<li><a id='" + data.id + "' title='" + data.title + "'>" + data.displayName + '</a></li>');
    }

    if (data.invisible) {
      this.$html
        .find('#' + data.id)
        .closest('li')
        .addClass('invisible-field');
    }

    this.$html
      .find('#bubble_tooltip_content')
      .children('li')
      .last()
      .find('a')
      .click(function () {
        if (callback) {
          callback.call(this, data);
        }
      });
  },
  //删除错误条目
  removeErrorItem: function (id) {
    if (id.indexOf('___') > 0) {
      this.$html
        .find('#bubble_tooltip_content')
        .children('li')
        .find("a[id='" + id + "']")
        .parent()
        .remove();
      var lis = this.$html.find('#bubble_tooltip_content').find('li');
      var liId = '';

      for (var i = 0; i < lis.length; i++) {
        var newId = $(lis[i]).find('a').attr('id');
        if (newId && newId.indexOf('___') && newId.split('___')[1] == id.split('___')[1]) {
          liId = newId;
          break;
        }
      }
      if (liId) {
        this.$html
          .find('#bubble_tooltip_content')
          .children('li')
          .find("a[id='" + liId + "']")
          .parent()
          .show();
      }
    } else {
      this.$html
        .find('#bubble_tooltip_content')
        .children('li')
        .find("a[id='" + id + "']")
        .parent()
        .remove();
    }

    if (this.itemCount() == 0) {
      //如果条目被删除完毕则自动关闭
      this.close();
    }
  },
  clear: function () {
    this.$html.find('#bubble_tooltip_content').empty();
  },
  itemCount: function () {
    return this.$html.find('#bubble_tooltip_content').children('li').size();
  }
};
