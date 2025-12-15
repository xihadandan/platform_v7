/**
 * select文本类型属性的公共方法
 */
(function ($) {
  $.wSelectCommonMethod = {
    // 显示为lablel,不适用于放在从表中的控件
    setDisplayAsLabel: function () {
      var self = this;
      var options = self.options;
      var readStyle = options.columnProperty.readStyle;
      if (readStyle === dyshowType.readonly) {
        self.setEnable(false);
        return self.setReadOnly(true);
      }
      if (this.$labelElem == null) {
        // 创建标签元素
        this.markPlaceHolderAsLabelElem();
      }
      // 确保$editableElem创建，取真实值依赖控件
      this.createEditableElem();

      this.$labelElem.show();

      options.isShowAsLabel = true;
      this.hideEditableElem();
      this.setValue2LabelElem();
      if (this.setCtlField) {
        this.setCtlField();
      }
      // add by wujx 20161014 begin
      if (!this.isInSubform()) {
        this.addUrlClickEvent(urlClickEvent);
      }
      // add by wujx 20161014 end
      // add by wujx 20161101 begin
      this.removeValidate();
      // add by wujx 20161101 end

      this.renderFieldTag();
    },

    renderFieldTag: function () {
      var readStyle = this.options.columnProperty.readStyle;
      if (readStyle == dyshowType.readonly) {
        return;
      }
      this.$labelElem.siblings('.form-field-tag-wrap').remove();
      var inputMode = this.getInputMode();
      // ['Select', 'ComboSelect', 'ComboTree']
      if ($.inArray(inputMode, ['199', '191', '16']) > -1) {
        var _displayValue = this.getDisplayValue();
        if (typeof _displayValue === 'string') {
          if (_displayValue.indexOf(',') > -1) {
            _displayValue = _displayValue ? _displayValue.split(',') : [];
          } else {
            _displayValue = _displayValue ? _displayValue.split(';') : [];
          }
        }
        var $tagWrap = $('<div>', {
          class: 'form-field-tag-wrap'
        });
        // if (this.options.selectMultiple || this.options.mutiSelect) {
        //多选时显示为tag
        var showControlBtn = false;
        $.each(_displayValue, function (i, v) {
          $tagWrap.append('<div class="form-field-tag">' + v + '</div>');
        });
        this.$labelElem.hide().after($tagWrap);

        var tagWrapTop = $tagWrap.offset().top;
        $tagWrap.find('.form-field-tag').each(function () {
          var $this = $(this);
          var _top = $this.offset().top;
          if (_top - tagWrapTop > 60) {
            showControlBtn = true;
            $this.addClass('is-more-tag').hide();
          }
        });
        var $parent = $tagWrap.parent();
        if (showControlBtn) {
          $parent.addClass('form-field-tag-control').css({
            position: 'relative',
            'padding-right': '34px'
          });
          $tagWrap.after(
            '<div class="control-btn" data-status="open"><span>展开<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i></span></div>'
          );
        }
        $parent.on('click', '.control-btn', function () {
          var $this = $(this);
          var _status = $this.data('status');
          if (_status === 'open') {
            $this.data('status', 'close').html('<span>收起<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-shang"></i></span>');
            $tagWrap.find('.is-more-tag').fadeIn('fast');
          } else {
            $this.data('status', 'open').html('<span>展开<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i></span>');
            $tagWrap.find('.is-more-tag').fadeOut('fast');
          }
        });
        // }
      }
    }
  };
})(jQuery);
