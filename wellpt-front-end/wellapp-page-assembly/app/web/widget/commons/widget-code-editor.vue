<template>
  <span>
    <div @click.stop="onClickForOpenModal" class="widget-code-editor-trigger" v-if="modal" style="display: inline">
      <slot name="default"></slot>
    </div>

    <template v-if="modal">
      <a-modal
        :width="actualWidth"
        :visible="visible"
        :bodyStyle="{ height: actualHeight, 'overflow-y': 'auto', padding: '0px' }"
        :mask="true"
        :centered="false"
        :maskClosable="false"
        :closable="false"
        dialogClass="widget-code-editor-modal"
        :id="id + 'modal'"
        :zIndex="zIndex"
        ref="modalEle"
      >
        <template slot="footer">
          <div>
            <slot name="extras"></slot>
            <a-button @click="onCancel">{{ autoSave ? '关闭' : '取消' }}</a-button>
            <a-button @click="onOk" type="primary" v-if="!autoSave">确定</a-button>
          </div>
        </template>
        <div slot="title" @dblclick.stop="onFullScreen">
          <span>{{ vTitle }}</span>
          <span class="widget-code-editor-modal-title-operation">
            <a-popover placement="leftTop" title="帮助" v-if="hasHelp" :arrowPointAtCenter="true" :getPopupContainer="getPopupContainer">
              <div slot="content" :style="{ width: vHelpTipWidth }">
                <slot name="help"></slot>
              </div>
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" title="帮助" />
            </a-popover>
            <a-button type="text" size="small" :title="fullscreen ? '退出全屏' : '全屏'" @click.stop="onFullScreen">
              <Icon :type="fullscreen ? 'pticon iconfont icon-ptkj-tuichuquanping' : 'pticon iconfont icon-ptkj-quanping'"></Icon>
            </a-button>
          </span>
        </div>
        <div :id="id" :style="{ width: vWidth, height: vHeight, 'font-size': 'inherit' }" :class="hideError ? 'hide-error' : ''"></div>
        <!-- //FIXME: 待完善历史代码功能 -->
      </a-modal>
    </template>
    <template v-else>
      <div :class="fullscreen ? 'widget-code-editor-nomodal-fullscreen' : ''">
        <div :style="{ width: vWidth }">
          <div class="widget-code-editor-nomodal-operation">
            <a-button type="link" size="small" v-if="!autoSave">
              <Icon type="pticon iconfont icon-ptkj-baocun"></Icon>
              保存
            </a-button>
            <a-button type="text" size="small" :title="fullscreen ? '退出全屏' : '全屏'" @click.stop="onFullScreen">
              <Icon :type="fullscreen ? 'pticon iconfont icon-ptkj-tuichuquanping' : 'pticon iconfont icon-ptkj-quanping'"></Icon>
            </a-button>
          </div>
        </div>
        <div :id="id" :style="{ width: vWidth, height: actualHeight, 'font-size': 'inherit' }" :class="hideError ? 'hide-error' : ''"></div>
      </div>
    </template>
  </span>
</template>
<style>
.widget-code-editor-modal-title-operation {
  float: right;
  color: rgba(0, 0, 0, 0.45);
}

.widget-code-editor-nomodal-operation {
  text-align: right;
  line-height: 1.5;
  background: #fafafa;
  border-radius: 4px 4px 0px 0px;
  padding: 1px 5px;
  border: 1px solid #dcd1d1;
  color: #000;
}

.widget-code-editor-nomodal-operation i:hover {
  color: #40a9ff;
  cursor: pointer;
}

.widget-code-editor-modal-title-operation i:hover {
  color: rgba(0, 0, 0, 0.75);
  cursor: pointer;
}

.widget-code-editor-nomodal-fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 9999;
}

.hide-error .ace_error {
  display: none;
}

.ace_scroller {
  border-right: 1px solid #dcd1d1;
  border-bottom: 1px solid #dcd1d1;
}

.ace_layer {
  border-left: 1px solid #dcd1d1;
  border-bottom: 1px solid #dcd1d1;
}
</style>
<script type="text/babel">
import { addWindowResizeHandler } from '@framework/vue/utils/util';
import { debounce } from 'lodash';
import { jsonrepair } from 'jsonrepair';

export default {
  name: 'WidgetCodeEditor',
  props: {
    title: String,
    value: {
      type: String,
      default: ''
    },
    width: {
      // 宽度
      type: [String, Number],
      default: '700px'
    },
    height: {
      // 高度
      type: String,
      default: '500px'
    },
    theme: {
      //主题: 可选值为ace编辑器内置的相关主题
      type: String,
      default: 'eclipse' //'monokai'
    },
    lang: {
      //编辑语言 : javascript 、html 等
      type: String,
      default: 'javascript'
    },
    snippets: {
      //代码提示片段 ：输入关键字，可快速生成代码片段
      type: Array
    },
    codeSegments: {
      type: Array
    }, // 代码片段，提供帮助性代码片段编写说明
    hideError: {
      type: Boolean,
      default: false
    },
    zIndex: {
      type: Number,
      default: 2000
    },
    helpTipWidth: {
      // 宽度
      type: [String, Number]
    },
    readOnly: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      autoSave: false,
      modal: this.$slots.default && this.$slots.default.length > 0,
      visible: false,
      fullscreen: false,
      windowWidth: '0px',
      windowHeight: '0px',
      id: `ace_${new Date().getTime()}`,
      initWidth: this.width,
      initHeight: this.height,
      actualWidth: this.width,
      actualHeight: this.height,
      hasHelp: this.$slots.help && this.$slots.help.length > 0
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    vTitle() {
      return this.title || `编写 ${this.lang} 代码`;
    },
    vHeight() {
      return this.actualHeight === 'auto' ? 'auto' : parseInt(this.actualHeight) + 'px';
    },
    vWidth() {
      return this.actualWidth === 'auto' ? 'auto' : typeof this.actualWidth == 'string' ? this.actualWidth : this.actualWidth + 'px';
    },
    vHelpTipWidth() {
      return this.helpTipWidth != undefined
        ? typeof this.helpTipWidth === 'number'
          ? this.helpTipWidth + 'px'
          : this.helpTipWidth
        : 'auto';
    }
  },
  created() {},
  methods: {
    getPopupContainer(node) {
      return node.closest('.ant-modal-content');
    },
    onClickForOpenModal() {
      this.visible = true;
      this.init();
    },
    adjustElementSize() {
      // 全屏
      if (this.modal) {
        this.actualHeight = parseInt(this.windowHeight) - 110 + 'px';
        this.actualWidth = this.windowWidth;
      } else {
        this.actualHeight = parseInt(this.windowHeight) + 'px';
        this.actualWidth = this.windowWidth;
      }
    },
    onFullScreen() {
      let element = this.modal ? document.querySelector(`#${this.id}modal`) : document.querySelector('#' + this.id).parentElement;
      if (document.fullscreenElement == null) {
        this.adjustElementSize();
        this.fullscreen = true;
        if (this.modal) {
          document.querySelector(`#${this.id}modal`).querySelector('.widget-code-editor-modal').style.position = 'unset';
          document.querySelector(`#${this.id}modal`).querySelector('.widget-code-editor-modal').style['padding-bottom'] = '0px';
        }

        if (element.requestFullscreen) {
          element.requestFullscreen();
        } else if (element.mozRequestFullScreen) {
          element.mozRequestFullScreen();
        } else if (element.webkitRequestFullscreen) {
          element.webkitRequestFullscreen();
        } else if (element.msRequestFullscreen) {
          element.msRequestFullscreen();
        }
      } else {
        if (this.fullscreen) {
          this.restoreModal();
        }
        document.exitFullscreen();
      }
    },

    restoreModal() {
      this.actualHeight = this.initHeight;
      this.actualWidth = this.initWidth;
      this.fullscreen = false;
      if (this.modal) {
        document.querySelector(`#${this.id}modal`).querySelector('.widget-code-editor-modal').style.position = 'relative';
        document.querySelector(`#${this.id}modal`).querySelector('.widget-code-editor-modal').style['padding-bottom'] = '24px';
      }
    },
    registerSnippets(snippets) {
      // 注册新的代码片段
      // this.snippetManager.register(
      //   [{ name: '你好', content: "var say = 'hello , ${1:user}';", tabTrigger: 'hello', trigger: 'hel|hello|hell' }],
      //   this.lang
      // );
      //

      this.snippetManager.register(snippets, this.lang);

      // console.log('代码提示片段 : ', this.snippetManager.snippetMap);
    },
    init() {
      if (this.editor == undefined) {
        let _this = this;
        let el = document.querySelector(`#${this.id}`);
        if (el) {
          this.editor = ace.edit(this.id);
          this.editor.getSession().setMode(`ace/mode/${this.lang}`);
          this.editor.setTheme(`ace/theme/${this.theme}`);
          this.editor.setOptions({
            readOnly: this.readOnly,
            enableBasicAutocompletion: true,
            enableLiveAutocompletion: true,
            enableSnippets: true,
            showPrintMargin: false
          });

          if (this.value != undefined && this.value != null) {
            this.editor.setValue(this.value);
          }
          this.registerSnippets(this.snippets);
          if (!this.autoSave) {
            this.editor.commands.addCommand({
              name: 'customSaveCodeCommand',
              bindKey: { win: 'Ctrl-S' },
              exec: function (editor) {
                _this.value = editor.getValue();
                _this.$emit('save', editor.getValue());
              }
            });
          }

          if (this.autoSave) {
            this.editor.getSession().on('change', function () {
              _this.eidtorContentChange(arguments[1].getValue());
            });
          }
          if (this.modal) {
            document
              .querySelector(`#${this.id}modal`)
              .querySelector('.widget-code-editor-modal')
              .querySelector('.ant-modal-header')
              .addEventListener('dblclick', function () {
                _this.onFullScreen();
              });
          }
        } else {
          setTimeout(function () {
            _this.init();
          }, 50);
        }
      }
    },
    onOk() {
      let outValue = this.editor.getValue();
      if (this.lang == 'json') {
        try {
          outValue = jsonrepair(this.editor.getValue());
        } catch (error) {
          this.$message.error('语法错误，请修正');
          return;
        }

        this.editor.setValue(outValue); // 取消时候值要更新未修改前的值
      }
      this.$emit('save', outValue);
      this.visible = false;
      if (this.fullscreen) {
        this.onFullScreen();
      }
    },
    eidtorContentChange: debounce(function (v) {
      this.$emit('input', v);
    }, 1000),
    onCancel() {
      this.visible = false;
      if (!this.autoSave) {
        this.editor.setValue(this.value); // 取消时候值要更新未修改前的值
      }
      if (this.fullscreen) {
        this.onFullScreen();
      }
    },
    startInit() {
      this.snippetManager = ace.acequire('ace/snippets').snippetManager; // 代码提示片段管理器
      this.autoSave = this.$listeners.input != undefined; // 通过v-model双向绑定的情况下，视为自动保存的情况
      if (!this.modal) {
        this.init();
      }
      this.windowHeight = window.innerHeight + 'px';
      this.windowWidth = window.innerWidth + 'px';
      addWindowResizeHandler(() => {
        this.$nextTick(() => {
          this.windowHeight = window.innerHeight + 'px';
          this.windowWidth = window.innerWidth + 'px';
          if (this.fullscreen) {
            this.adjustElementSize();
          }
        });
      });
      // 监听全屏状态变化
      document.addEventListener('fullscreenchange', e => {
        this.fullscreen = document.fullscreenElement != null;
        if (!this.fullscreen) {
          this.restoreModal();
        }
      });
    }
  },

  mounted() {
    let _this = this;
    require.ensure([], function (require) {
      require('brace');
      // 加载可能需要用的语言模型以及代码片段
      require(`brace/mode/text`);
      require(`brace/mode/javascript`);
      require(`brace/mode/json`);
      require(`brace/mode/html`);
      require(`brace/mode/sql`);
      require(`brace/mode/less`);
      require(`brace/mode/css`);
      require(`brace/snippets/javascript`);
      require(`brace/snippets/json`);
      require(`brace/snippets/html`);
      require(`brace/snippets/sql`);
      require(`brace/snippets/css`);
      require(`brace/snippets/less`);

      require(`brace/theme/eclipse`);
      require('brace/ext/language_tools');
      _this.startInit();
    });
  }
};
</script>
