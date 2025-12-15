import { debounce } from 'lodash';

export default {
  computed: {
    inputFieldOptions() {
      let opt = [];
      if (this.designer.WidgetFormInputs && this.designer.WidgetFormInputs.length) {
        for (let k = 0, len = this.designer.WidgetFormInputs.length; k < len; k++) {
          let _WidgetFormInput = this.designer.WidgetFormInputs[k];
          if (_WidgetFormInput.configuration.code) {
            opt.push({
              label: _WidgetFormInput.configuration.name || _WidgetFormInput.configuration.code,
              value: _WidgetFormInput.configuration.code
            });
          }
        }
      }
      return opt;
    },

    textWidgetOptions() {
      let opt = [];
      if (this.designer.WidgetFormTexts && this.designer.WidgetFormTexts.length) {
        for (let k = 0, len = this.designer.WidgetFormTexts.length; k < len; k++) {
          let _Widget = this.designer.WidgetFormTexts[k];
          if (_Widget.configuration.name) {
            opt.push({
              label: _Widget.configuration.name,
              value: _Widget.id
            });
          }
        }
      }
      return opt;
    },

    datePickerWidgetOptions() {
      let opt = [];
      if (this.designer.WidgetFormDatePickers && this.designer.WidgetFormDatePickers.length) {
        for (let k = 0, len = this.designer.WidgetFormDatePickers.length; k < len; k++) {
          let _Widget = this.designer.WidgetFormDatePickers[k];
          if (_Widget.configuration.code && _Widget.id != this.widget.id) {
            opt.push({
              label: _Widget.configuration.name || _Widget.configuration.code,
              value: _Widget.configuration.code
            });
          }
        }
      }
      return opt;
    }
  },

  methods: {
    onSelectChangeWidgetTypeWithoutFieldName(val, opt) {
      if (!this.widget.configuration.name) this.widget.title = opt.componentOptions.children[0].text;
    },

    onInputNameChange() {
      this.onSyncLabelChange();
    },
    onClickForSyncLabel2FormItem() {
      this.widget.configuration.syncLabel2FormItem = !this.widget.configuration.syncLabel2FormItem;
      this.onSyncLabelChange();
    },
    onSyncLabelChange() {
      if (this.widget.configuration.syncLabel2FormItem) {
        let _parent = this.designer.parentOfSelectedWidget;
        if (_parent && _parent.wtype === 'WidgetFormItem' && this.widget.configuration.syncLabel2FormItem) {
          _parent.configuration.label = this.widget.configuration.name;
        }
      }
    },
    preservedCheck: debounce(function (v) {
      let _code = this.widget.configuration.code,
        _this = this;
      for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
        // 判断字段编码是否重复
        if (this.designer.SimpleFieldInfos[i].code == _code && this.widget.id != this.designer.SimpleFieldInfos[i].id) {
          this.$message.error('字段编码 ' + _code + ' 重复', 2).then(function () {
            _this.widget.configuration.code = null;
          });
          return;
        }
      }
      let preserved = this.designer.isPreservedField(_code);
      if (preserved) {
        this.$message.error('字段编码 ' + _code + ' 为系统预留编码', 2).then(function () {
          _this.widget.configuration.code = null;
        });
      }
    }, 1000),

    onInputCodeLowerCase(e) {
      if (this.widget.configuration.code != undefined && this.widget.configuration.code.length) {
        try {
          this.widget.configuration.code = this.widget.configuration.code.toLowerCase();
          let start = e.target.selectionStart;
          this.$nextTick(() => {
            e.target.setSelectionRange && e.target.setSelectionRange(start, start);
          });

          // 保留字判断
          this.preservedCheck();
        } catch (error) {}
      }
    }
  }
};
