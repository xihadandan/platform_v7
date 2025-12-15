CKEDITOR.dialog.add('html5video', function (editor) {
  var regexGetSizeOrEmpty = /(^\s*(\d+)(px)?\s*$)|^$/i,
    commonLang = editor.lang.common;

  function validateDimension() {
    var match = this.getValue().match(regexGetSizeOrEmpty),
      isValid = !!(match && parseInt(match[1], 10) !== 0);

    if (!isValid) alert(commonLang['invalid' + CKEDITOR.tools.capitalize(this.id)]); // jshint ignore:line

    return isValid;
  }

  return {
    title: editor.lang.html5video.title,
    minWidth: 250,
    minHeight: 100,
    contents: [
      {
        id: 'info',
        label: editor.lang.html5video.infoLabel,
        elements: [
          {
            type: 'vbox',
            padding: 0,
            children: [
              {
                type: 'hbox',
                align: 'right',
                children: [
                  {
                    type: 'text',
                    id: 'url',
                    label: editor.lang.html5video.allowed,
                    required: true,
                    validate: CKEDITOR.dialog.validate.notEmpty(editor.lang.html5video.urlMissing),
                    setup: function (widget) {
                      this.setValue(widget.data.src);
                    },
                    commit: function (widget) {
                      widget.setData('src', this.getValue());
                    }
                  }
                ]
              }
            ]
          },
          {
            type: 'vbox',
            padding: 0,

            children: [
              {
                type: 'hbox',
                align: 'right',
                children: [
                  {
                    type: 'text',
                    id: 'poster',
                    label: editor.lang.html5video.poster,
                    setup: function (widget) {
                      this.setValue(widget.data.poster);
                    },
                    commit: function (widget) {
                      widget.setData('poster', this.getValue());
                    }
                  }
                ]
              }
            ]
          },
          {
            type: 'hbox',
            id: 'size',
            children: [
              {
                type: 'text',
                id: 'width',
                label: editor.lang.html5video.width,
                validate: validateDimension,
                onLoad: function () {
                  widthField = this;
                },
                setup: function (widget) {
                  this.setValue(widget.data.width);
                },
                commit: function (widget) {
                  widget.setData('width', this.getValue());
                }
              },
              {
                type: 'text',
                id: 'height',
                label: editor.lang.html5video.height,
                validate: validateDimension,
                onLoad: function () {
                  heightField = this;
                },
                setup: function (widget) {
                  if (widget.data.height) {
                    this.setValue(widget.data.height);
                  }
                },
                commit: function (widget) {
                  widget.setData('height', this.getValue());
                }
              }
            ]
          },

          {
            type: 'hbox',
            id: 'alignment',
            children: [
              {
                type: 'radio',
                id: 'align',
                label: editor.lang.common.align,
                items: [
                  [editor.lang.common.alignLeft, 'left'],
                  [editor.lang.common.alignCenter, 'center'],
                  [editor.lang.common.alignRight, 'right']
                ],
                default: 'left',
                setup: function (widget) {
                  if (widget.data.align) {
                    this.setValue(widget.data.align);
                  }
                },
                commit: function (widget) {
                  widget.setData('align', this.getValue());
                }
              }
            ]
          }
        ]
      },
      {
        id: 'Upload',
        hidden: true,
        filebrowser: 'uploadButton',
        label: editor.lang.html5video.upload,
        elements: [
          {
            type: 'file',
            id: 'upload',
            label: editor.lang.html5video.btnUpload,
            style: 'height:40px',
            size: 38,
            accept: '.mp4,.webm,.ogv',
            onChange: function (res) {
              var fileType = '.' + (res.data && res.data.value.split('.').pop());
              var accepts = res.sender.accept.split(',');
              if (accepts.indexOf(fileType) < 0) {
                top.appModal.error('上传文件格式不正确!');
                this.reset();
              }
            },
          },
          {
            type: 'fileButton',
            id: 'uploadButton',
            filebrowser: 'info:url',
            label: editor.lang.html5video.btnUpload,
            for: ['Upload', 'upload'],
            onClick: function () {
              top.appModal.showMask();
            }
          }
        ]
      },
      {
        id: 'advanced',
        label: editor.lang.html5video.advanced,
        elements: [
          {
            type: 'vbox',
            padding: 10,
            children: [
              {
                type: 'hbox',
                children: [
                  {
                    type: 'checkbox',
                    id: 'controls',
                    label: editor.lang.html5video.controls,
                    default: 'checked',
                    setup: function (widget) {
                      if (widget.data.controls != undefined) {
                        this.setValue(widget.data.controls);
                      }
                    },
                    commit: function (widget) {
                      widget.setData('controls', this.getValue() ? 'true' : '');
                    }
                  },
                  {
                    type: 'checkbox',
                    id: 'autoplay',
                    label: editor.lang.html5video.autoplay,
                    setup: function (widget) {
                      if (widget.data.autoplay) {
                        this.setValue(widget.data.autoplay);
                      }
                    },
                    commit: function (widget) {
                      widget.setData('autoplay', this.getValue());
                    }
                  },
                  {
                    type: 'checkbox',
                    id: 'loop',
                    label: editor.lang.html5video.loop,
                    setup: function (widget) {
                      if (widget.data.loop) {
                        this.setValue(widget.data.loop);
                      }
                    },
                    commit: function (widget) {
                      widget.setData('loop', this.getValue());
                    }
                  },
                  {
                    type: 'checkbox',
                    id: 'allowdownload',
                    label: editor.lang.html5video.allowdownload,
                    setup: function (widget) {
                      if (widget.data.allowdownload) {
                        this.setValue(widget.data.allowdownload);
                      }
                    },
                    commit: function (widget) {
                      widget.setData('allowdownload', this.getValue());
                    }
                  }
                ]
              },
              {
                type: 'hbox',
                children: [
                  {
                    type: 'text',
                    id: 'advisorytitle',
                    label: editor.lang.html5video.advisorytitle,
                    default: '',
                    setup: function (widget) {
                      if (widget.data.advisorytitle) {
                        this.setValue(widget.data.advisorytitle);
                      }
                    },
                    commit: function (widget) {
                      widget.setData('advisorytitle', this.getValue());
                    }
                  }
                ]
              }
            ]
          }
        ]
      }
    ]
  };
});
