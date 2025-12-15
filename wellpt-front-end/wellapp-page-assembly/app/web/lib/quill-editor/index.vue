<template>
  <div :class="['quill-text-editor-container', fullscreen ? 'fullscreen' : '', disable ? 'disabled' : '', readOnly ? 'readonly' : '']">
    <div :class="[disable ? 'disable' : '', readOnly ? 'readonly' : '']"></div>
    <div v-show="!displayAsLabel">
      <div :id="id + 'toolbar'" class="ql-editor-toolbar" v-show="!displayAsLabel">
        <select v-if="!hiddenButtons.includes('fontFamily')" class="ql-font ps__child--consume">
          <option value="initial" selected="">{{ $t('quill.defaultFontFamily', '默认字体') }}</option>
          <option v-for="(item, index) in fontStyle" :key="id + '_fstyle_' + index" :value="item.value">{{ item.name }}</option>
        </select>

        <select v-if="!hiddenButtons.includes('fontSize')" class="ql-size ps__child--consume">
          <option value="initial" selected="">{{ $t('quill.defaultFontSize', '默认大小') }}</option>
          <option v-for="(item, index) in fontSize" :key="id + '_fsize_' + index" :value="item">{{ item }}</option>
        </select>
        <button v-if="!hiddenButtons.includes('bold')" class="ql-bold" :title="$t('quill.bold', '加粗')"></button>
        <button v-if="!hiddenButtons.includes('italic')" class="ql-italic" :title="$t('quill.italic', '斜体')"></button>
        <button v-if="!hiddenButtons.includes('underline')" class="ql-underline" :title="$t('quill.underline', '下划线')"></button>
        <button
          v-if="!hiddenButtons.includes('minusIndent')"
          class="ql-indent"
          value="-1"
          :title="$t('quill.minusIndent', '减少缩进量')"
        ></button>
        <button
          v-if="!hiddenButtons.includes('addIndent')"
          class="ql-indent"
          value="+1"
          :title="$t('quill.addIndent', '增加缩进量')"
        ></button>

        <select class="ql-background" :title="$t('quill.backgroundColor', '背景颜色')">
          <option v-for="(item, index) in colors" :key="id + '_color_' + index" :value="item">
            <!-- <span class="o_color" :style="'background:' + item"></span> -->
          </option>
        </select>
        <select class="ql-color" :title="$t('quill.fontColor', '文字颜色')">
          <option v-for="(item, index) in backgrounds" :key="id + '_bgcolor_' + index" :value="item"></option>
        </select>
        <!-- <button class="ql-align" value="center" /> -->
        <select v-if="!hiddenButtons.includes('align')" class="ql-align" :title="$t('quill.align', '对齐方式')">
          <option label="left" selected="" :title="$t('quill.leftAlign', '左对齐')"></option>
          <option label="center" value="center" :title="$t('quill.centerAlign', '居中对齐')"></option>
          <option label="right" value="right" :title="$t('quill.rightAlign', '右对齐')"></option>
          <option label="justify" value="justify" :title="$t('quill.justifyAlign', '两端对齐')"></option>
        </select>
        <button v-if="!hiddenButtons.includes('ordered')" class="ql-list" value="ordered" :title="$t('quill.ordered', '有序列表')"></button>
        <button v-if="!hiddenButtons.includes('list')" class="ql-list" value="bullet" :title="$t('quill.bullet', '项目符号')"></button>

        <button v-if="!hiddenButtons.includes('image')" class="ql-image" :title="$t('quill.insertImage', '插入图片')"></button>
        <button v-if="!hiddenButtons.includes('link')" class="ql-link" :title="$t('quill.insertLink', '插入链接')"></button>

        <button v-if="!hiddenButtons.includes('source')" class="ql-formats ee-flag-source" @click="showSourceEditor">
          <a-icon type="code" class="codeEditor" :title="$t('quill.sourceCode', '源代码')" style="font-size: 16px; vertical-align: top" />
        </button>

        <div class="custom fullscreen" v-if="!hiddenButtons.includes('fullscreen')">
          <a-icon
            :type="fullscreen ? 'fullscreen-exit' : 'fullscreen'"
            class="fullscreen"
            :title="fullscreen ? $t('quill.recover', '恢复') : $t('quill.maximize', '最大化')"
          />
        </div>
      </div>
      <div
        :id="id"
        class="quill-text-editor"
        @click="focusEditor"
        :style="{ minHeight: vMinHeight, height: fullscreen ? 'calc(100vh - 45px)' : '' }"
      >
        <template v-html="value"></template>
      </div>
      <link-config-dialog :visible="visibleLinkConfiguration" :editMode="editMode" v-model="form" @save="saveLink" @cancel="handleCancel" />
    </div>
    <template v-if="displayAsLabel">
      <div class="ql-editor" v-html="displayValue()" :key="quillDisplayHtmlKey"></div>
    </template>
  </div>
</template>
<style lang="less"></style>

<script type="text/babel">
import 'quill/dist/quill.snow.css';
import './quill-editor.less';
import { getColors, getBgColors } from './colors.js';
// import zh_CN from './locale/zh_CN.json';
import { eeSourceBtn } from './quill.eeSourceBtn.js';
import SparkMD5 from 'spark-md5';
import moment from 'moment';
import LinkConfigDialog from './link-config-dialog.vue';
import { merge, get } from 'lodash';

/* 富文本编辑图片上传配置 */
const uploadConfig = {
  name: 'img', // 必填参数 文件的参数名
  accept: 'image/png, image/gif, image/jpeg, image/bmp, image/x-icon' // 可选 可上传的图片格式
};
let _Quill;

export default {
  name: 'QuillEditor',
  props: {
    value: String,
    editable: {
      type: Boolean,
      default: true
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    disable: {
      type: Boolean,
      default: false
    },
    displayAsLabel: {
      type: Boolean,
      default: false
    },
    htmlCodec: {
      type: Boolean,
      default: false
    },
    placeholder: String,
    hiddenButtons: {
      type: Array,
      default: () => {
        return [];
      }
    },
    minHeight: {
      type: [String, Number],
      default: '200px'
    }
  },
  // i18n: {
  //   messages: {
  //     zh_CN
  //   }
  // },
  data() {
    return {
      id: `editor-${new Date().getTime() + '' + parseInt(Math.random() * 1000)}`,
      quillDisplayHtmlKey: 'quillEditorDisplayHtml_',
      editor: null,
      fullscreen: false,

      fontSize: '8px,9px,10x,11px,12px,14px,16px,18px,20px,22px,24px,26px,28px,36px,48px,72px'.split(','),
      colors: getColors(),
      backgrounds: getBgColors(),
      align: [
        {
          left: '',
          center: 'center',
          right: 'right',
          justify: 'justify'
        }
      ],
      fileUploadMap: {},
      chunkSize: 2 * 1024 * 1024,
      arrEntities: {
        //普通字符转换成转意符
        '<': '&lt;',
        '>': '&gt;',
        '&': '&amp;',
        '"': '&quot;',
        "'": '&apos;'
      },
      md5FileObj: {},
      visibleLinkConfiguration: false,
      editMode: false,
      currentNode: null,
      form: {
        text: '',
        href: '',
        target: '',
        rel: '',
        dataId: ''
      }
    };
  },
  components: {
    LinkConfigDialog
  },
  computed: {
    fontStyle() {
      return [
        { name: this.$t('quill.fontFamily.songti', '宋体'), value: 'songti', code: 'songti' },
        { name: this.$t('quill.fontFamily.heiti', '黑体'), value: 'heiti', code: 'heiti' },
        { name: this.$t('quill.fontFamily.fangsong', '仿宋'), value: 'fangsong', code: 'fangsong' },
        { name: this.$t('quill.fontFamily.kaiti', '楷体'), value: 'kaiti', code: 'kaiti' },
        { name: this.$t('quill.fontFamily.lishu', '隶书'), value: 'lishu', code: 'lishu' },
        { name: this.$t('quill.fontFamily.youyuan', '幼圆'), value: 'youyuan', code: 'youyuan' },
        { name: this.$t('quill.fontFamily.weiruanyahei', '微软雅黑'), value: 'weiruanyahei', code: 'weiruanyahei' }
      ];
    },
    vMinHeight() {
      return typeof this.minHeight == 'number' ? `${this.minHeight}px` : this.minHeight;
    }
  },
  methods: {
    handleClick(e) {
      let target = e.target;
      if (target && (target.tagName === 'A' || target.closest('a'))) {
        if (this.editable) {
          e.preventDefault();
          e.stopPropagation();

          target = target.closest('a');
          // 记录当前节点
          this.currentNode = target;
          this.editMode = true;

          this.selectedRange = {
            index: _Quill.find(target).offset(this.editor.scroll),
            length: target.innerText.length
          };

          // 填充表单
          this.form = {
            text: target.innerText || '',
            href: target.getAttribute('href') || '',
            target: target.getAttribute('target') || '',
            rel: target.getAttribute('rel') || '',
            dataId: target.getAttribute('data-id') || ''
          };

          // 打开弹窗
          this.visibleLinkConfiguration = true;
        }
      }
    },
    openInsertDialog() {
      this.editMode = false;

      let text = '';
      const range = this.editor.getSelection();
      let form = { text, href: '', target: '_blank', rel: '', dataId: '' };
      if (range && range.length) {
        this.selectedRange = range;

        form.text = this.editor.getText(range.index, range.length);

        const formats = this.editor.getFormat(range);
        if (formats.link) {
          const link = formats.link;
          form.href = link.href;
          form.target = link.target || '_blank';
        }
      }
      this.form = form;
      this.visibleLinkConfiguration = true;
    },
    handleContextMenu(e) {
      if (e.target && e.target.tagName === 'A') {
        this.currentNode = e.target;
        this.menuX = e.clientX;
        this.menuY = e.clientY;
        this.menuVisible = true;
      } else {
        this.menuVisible = false;
      }
    },
    onMenuClick({ key }) {
      if (key === 'edit') {
        this.openEditDialog();
      } else if (key === 'remove') {
        this.removeLink();
      }
      this.menuVisible = false;
    },
    openEditDialog() {
      if (this.currentNode) {
        this.editMode = true;
        this.form = {
          text: this.currentNode.innerText || '',
          href: this.currentNode.getAttribute('href') || '',
          target: this.currentNode.getAttribute('target') || '',
          rel: this.currentNode.getAttribute('rel') || '',
          dataId: this.currentNode.getAttribute('data-id') || ''
        };
        this.visibleLinkConfiguration = true;
      }
    },
    removeLink() {
      if (this.currentNode) {
        const text = this.currentNode.innerText;
        const blot = Quill.find(this.currentNode);
        if (blot) {
          const index = blot.offset(this.editor.scroll);
          this.editor.deleteText(index, text.length);
          this.editor.insertText(index, text); // 只保留文字
        }
      }
    },
    handleCancel() {
      this.visibleLinkConfiguration = false;
      this.selectedRange = null;
    },
    saveLink(form) {
      let range;
      if (this.selectedRange) {
        range = this.selectedRange;
      } else {
        range = this.editor.getSelection(true);
      }
      if (!range) return;

      if (this.editMode && this.currentNode) {
        // const [leaf] = this.editor.getLeaf(range.index);
        const originalFormats = this.editor.getFormat(range);
        this.editor.deleteText(range.index, range.length); // 删除原文字
        this.editor.insertText(range.index, form.text, {
          ...originalFormats,
          link: JSON.stringify(form)
        });
      } else {
        if (range.length > 0) {
          const originalFormats = this.editor.getFormat(range);
          this.editor.deleteText(range.index, range.length); // 删除原文字
          this.editor.insertText(range.index, form.text, {
            ...originalFormats,
            link: JSON.stringify(form)
          });
          this.editor.setSelection(range.index, form.text.length, _Quill.sources.SILENT);
        } else {
          const index = range.index;
          if (!form.text) return;
          this.editor.insertText(index, form.text, _Quill.sources.USER);
          // 选中新插入的文本并格式化为 link
          this.editor.setSelection(index, form.text.length, _Quill.sources.SILENT);
          this.editor.format('link', JSON.stringify(form));
          // 可选：把光标移到插入文本后面
          this.editor.setSelection(index + form.text.length, 0, _Quill.sources.SILENT);
        }
      }
      this.visibleLinkConfiguration = false;
      this.selectedRange = null;
    },
    handleOpenLink() {
      // this.visibleLinkConfiguration = true;
      if (this.editable) {
        this.openInsertDialog();
      }
    },
    handleConfirmLink({ full, data }) {
      this.visibleLinkConfiguration = false;
      let length = this.editor.getSelection(true).index;
      // this.editor.insertText(length, data.label, {
      //   link: full
      // });
      this.editor.insertText(length, data.label, 'link', full);
      this.editor.setSelection(length + 1);
    },
    setHtml(html) {
      this.$editorEl.innerHTML = html;
      if (this.displayAsLabel) {
        this.displayValue();
      }
      // 只读状态下，placeholder不会自动消失，手动去掉ql-blank即可
      if (!this.editable && html && html != '') {
        this.$el.querySelector('.ql-editor').setAttribute('class', 'ql-editor');
      } else {
        this.$el.querySelector('.ql-editor').setAttribute('class', 'ql-editor ql-blank');
      }
    },
    getHtml() {
      if (this.$editorEl.innerHTML == '<p><br></p>') {
        this.$editorEl.innerHTML = '';
      }
      return this.$editorEl.innerHTML;
    },
    // 显示值
    displayValue() {
      return this.html2EscapeHandle(this.$editorEl ? this.getHtml() : null);
    },
    // HTML转义
    html2EscapeHandle(value) {
      if (value == '<p><br></p>') {
        value = '';
      }
      if (this.htmlCodec) {
        return typeof value === 'string'
          ? value.replace(/[<>&"']/g, c => {
              return this.arrEntities[c];
            })
          : value;
      }
      return value;
    },
    createQuillEditor(Quill) {
      let _this = this;
      if (this.editor == null) {
        this.editor = new Quill(document.querySelector(`#${this.id}`), {
          modules: {
            toolbar: {
              container: '#' + this.id + 'toolbar',
              handlers: {
                image: this.handleImgUpload,
                link: this.handleOpenLink
              }
            },
            eeSourceBtn: {}
          },
          placeholder: this.editable ? this.placeholder || '' : '',
          // placeholder: '请输入内容',
          readOnly: !this.editable,
          theme: 'snow'
        });
        this.editor.on('text-change', function (delta, oldDelta, source) {
          _this.$emit('input', _this.editor.root.innerHTML == '<p><br></p>' ? null : _this.editor.root.innerHTML);
        });
        this.editor.root.addEventListener('click', _this.handleClick);
        this.editor.theme.tooltip.show = args => {};
        this.$editorEl = document.querySelector('#' + this.id).querySelector('.ql-editor');
        if (this.value) {
          // 初始值
          this.$editorEl.innerHTML = this.value;
        }

        if (!this.hiddenButtons.includes('fullscreen')) {
          document
            .querySelector('#' + this.id + 'toolbar')
            .querySelector('.fullscreen')
            .addEventListener('click', function () {
              _this.fullscreen = !_this.fullscreen;
            });
        }
      }
    },
    loadQuilljs() {
      require('./quill.link.js');
      let Quill = require('quill');
      _Quill = Quill;
      var Font = Quill.import('formats/font');
      Font.whitelist = 'songti,heiti,fangsong,kaiti,lishu,youyuan,weiruanyahei'.split(',');
      Quill.register(Font, true);
      var Size = Quill.import('formats/size');
      Size.whitelist = [false].concat('8px,9px,10x,11px,12px,14px,16px,18px,20px,22px,24px,26px,28px,36px,48px,72px'.split(','));
      Size.scope = 9;
      Quill.register(Size, true);
      Quill.register('modules/eeSourceBtn', eeSourceBtn);
      this.createQuillEditor(Quill);
    },
    // 图片上传
    handleImgUpload() {
      var self = this;
      if (this.editable) {
        var fileInput = self.$el.querySelector('input.ql-image[type=file]');
        if (fileInput === null) {
          fileInput = document.createElement('input');
          fileInput.setAttribute('type', 'file');
          // 设置图片参数名
          if (uploadConfig.name) {
            fileInput.setAttribute('name', uploadConfig.name);
          }
          // 可设置上传图片的格式
          fileInput.setAttribute('accept', uploadConfig.accept);
          fileInput.classList.add('ql-image');
          // 监听选择文件
          fileInput.addEventListener('change', () => {
            self.handleUpload(fileInput.files[0], function (data) {
              let length = self.editor.getSelection(true).index;
              self.editor.insertEmbed(length, 'image', data.url);
              self.editor.setSelection(length + 1);
              fileInput.value = '';
            });
          });
          document.querySelector('#' + this.id + 'toolbar').appendChild(fileInput);
        }
        fileInput.click();
      }
    },
    uploadImage(file, callback) {
      var _this = this,
        fileUuid = file.name.split('.')[0] || 'richimguuid',
        fileSize = file.size,
        fileName = file.name,
        chunkSize = this.chunkSize,
        chunkIndexList = [];
      _this.fileUploadMap = {};
      var formData = new FormData();
      formData.set('frontUUID', fileUuid);
      formData.set('size', fileSize);
      var chunk = chunkSize != undefined && fileSize > chunkSize;
      if (chunk) {
        formData.set('chunkSize', chunkSize);
      }
      var headers = {
        'Content-Disposition': 'attachment; filename="' + encodeURIComponent(fileName) + '"',
        'Content-Type': 'multipart/form-data'
      };
      var ajaxUpload = function ajaxUpload(position) {
        var end = 0;
        if (chunk) {
          end = position + chunkSize >= fileSize ? fileSize : position + chunkSize;
          var chunkFile = _this.blobSlice(file, position, end);
          formData.set('file', new File([chunkFile], fileName));
          headers['Content-Range'] = 'bytes ' + position + '-' + (end - 1) + '/' + fileSize;
        } else {
          formData.set('file', file);
        }
        _this.$axios
          .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
            headers: headers
          })
          .then(function (_ref) {
            var data = _ref.data;
            console.log(data);
            if (data && data.success) {
              if (data.data === 'continue') {
                if (_this.fileUploadMap) {
                  _this.fileUploadMap.percent = parseInt((end / fileSize) * 100);
                  ajaxUpload(end);
                }
              } else if (!chunk || (end == fileSize && Array.isArray(data.data) && data.data.length > 0)) {
                _this.fileUploadMap.percent = 100;
                _this.fileUploadMap.dbFile = data.data[0];
                _this.fileUploadMap.url = '/proxy-repository/repository/file/mongo/download?fileID=' + data.data[0].fileID;
                if (typeof callback === 'function') {
                  callback.call(_this, _this.fileUploadMap);
                }
              }
            } else {
            }
          })
          .catch(function (error) {
            console.error('上传文件失败, 异常: ', error);
          });
      };
      var start = 0,
        chunkStoredCnt = chunkIndexList.length;
      if (chunkStoredCnt > 0) {
        while (chunkStoredCnt-- > 0) {
          start = start + chunkSize >= fileSize ? fileSize : start + chunkSize;
        }
        this.fileUploadMap.percent = parseInt((start / fileSize) * 100);
      }
      ajaxUpload(start);
    },
    blobSlice(file, start, end) {
      var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice;
      return blobSlice.call(file, start, end);
    },
    handleUpload(file, callback) {
      const options = {
        file: file
      };
      let _this = this;
      this.computeFileMD5(options.file, function () {
        // 校验文件是否存在，如果已经存在，则上传直接返回删除
        _this.loadFileChunkInfoByMD5(options.file, function (result) {
          _this.upload(options.file, result, function (_file) {
            callback(_file);
          });
        });
      });
    },
    computeFileMD5(file, callback) {
      var _this = this,
        chunkSize = this.chunkSize,
        chunks = Math.ceil(file.size / chunkSize),
        currentChunk = 0,
        spark = new SparkMD5.ArrayBuffer(),
        fileReader = new FileReader();

      fileReader.onload = function (e) {
        spark.append(e.target.result);
        currentChunk++;

        if (currentChunk < chunks) {
          loadNext();
        } else {
          let md5Str = spark.end();
          console.debug('计算文件hash值: ', md5Str); // Compute hash
          file.md5 = md5Str;
          callback();
        }
      };

      fileReader.onerror = function () {
        console.warn('读取文件失败');
      };

      function loadNext() {
        var start = currentChunk * chunkSize,
          end = start + chunkSize >= file.size ? file.size : start + chunkSize;

        fileReader.readAsArrayBuffer(_this.blobSlice(file, start, end));
      }

      loadNext();
    },
    loadFileChunkInfoByMD5(file, callback) {
      if (this.chunkSize == undefined) {
        // 无分块上传的情况
        callback(false);
        return;
      }
      let params = {
        md5: file.md5,
        chunkSize: this.chunkSize
      };

      let _this = this;
      const CancelToken = _this.$axios.CancelToken;
      const source = CancelToken.source();
      this.$axios
        .get('/proxy-repository/repository/file/mongo/getFileChunkInfo', {
          params: params,
          cancelToken: source.token
        })
        .then(({ data }) => {
          console.log(data);
          if (data.success) {
            _this.md5FileObj[params.md5] = data.data;
            // 可能存在文件名不同，文件内容相同的情况，分块进行去重
            let chunkIndexList = data.data.chunkIndexList.length ? Array.from(new Set(data.data.chunkIndexList)) : [];
            callback({ md5FileStored: data.data.hasMd5FileFlag, chunkIndexList });
          }
        })
        .catch(error => {});
    },
    upload(file, param, callback) {
      let _this = this,
        fileSize = file.size,
        fileName = file.name,
        chunkSize = this.chunkSize,
        md5FileStored = param.md5FileStored,
        chunkIndexList = param.chunkIndexList || [];
      let formData = new FormData();
      formData.set('frontUUID', file.uid);
      formData.set('md5', file.md5);
      formData.set('localFileSourceIcon', '');
      formData.set('size', fileSize);
      let source = '上传';
      formData.set('source', source);
      let chunk = chunkSize != undefined && fileSize > chunkSize;
      if (chunk) {
        formData.set('chunkSize', chunkSize);
      }
      let headers = {
        'Content-Disposition': `attachment; filename="${encodeURIComponent(fileName)}"`,
        'Content-Type': 'multipart/form-data'
      };
      let ajaxUpload = function (position) {
        let end = 0;
        if (chunk) {
          // 分块上传的数据
          end = position + chunkSize >= fileSize ? fileSize : position + chunkSize;
          let chunkFile = _this.blobSlice(file, position, end);
          formData.set('file', new File([chunkFile], fileName));
          headers['Content-Range'] = `bytes ${position}-${end - 1}/${fileSize}`;
        } else {
          formData.set('file', file);
        }
        const CancelToken = _this.$axios.CancelToken;
        const source = CancelToken.source();
        _this.$axios
          .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
            headers: headers,
            cancelToken: source.token
          })
          .then(({ data }) => {
            console.log(data);
            if (data && data.success) {
              if (!_this.fileUploadMap[file.uid]) {
                _this.fileUploadMap[file.uid] = {};
              }
              if (data.data === 'continue') {
                if (_this.fileUploadMap[file.uid]) {
                  _this.fileUploadMap[file.uid].percent = parseInt((end / fileSize) * 100);
                  ajaxUpload(end);
                }
              } else if (!chunk || (end == fileSize && Array.isArray(data.data) && data.data.length > 0)) {
                // 全部上传结束了
                _this.fileUploadMap[file.uid].percent = 100;
                data.data[0].createTimeStr = moment(data.data[0].createTime).format('YYYY-MM-DD HH:mm');
                _this.fileUploadMap[file.uid].dbFile = data.data[0];
                _this.fileUploadMap[file.uid].url = `/proxy-repository/repository/file/mongo/download?fileID=${data.data[0].fileID}`;
                if (typeof callback === 'function') {
                  callback.call(_this, _this.fileUploadMap[file.uid]);
                }
              }
            } else {
            }
          })
          .catch(function (error) {
            console.error('上传文件失败, 异常: ', error);
          });
      };
      let start = 0,
        chunkStoredCnt = chunkIndexList.length;
      if (chunkStoredCnt > 0) {
        // 断点续传，计算从哪个位置开始
        while (chunkStoredCnt-- > 0) {
          start = start + chunkSize >= fileSize ? fileSize : start + chunkSize;
        }
        this.fileUploadMap[file.uid].percent = parseInt((start / fileSize) * 100); // 更新已上传的进度
      }
      ajaxUpload(md5FileStored ? fileSize - 2 : start);
    },
    // 源代码显示切换
    showSourceEditor() {
      eeSourceBtn.prototype.showSourceEditor(this.editor);
    },
    focusEditor() {
      if (this.editable) {
        this.editor.getSelection(true);
      }
    }
  },
  mounted() {
    this.loadQuilljs();
  },
  watch: {
    value: {
      handler(v, o) {
        if (v != o && this.displayAsLabel) {
          this.quillDisplayHtmlKey = 'quillEditorDisplayHtml_' + new Date().getTime();
          this.$editorEl.innerHTML = v;
        }
        if (v != o) {
          this.$emit('change', v);
        }
      }
    },
    editable: {
      handler(v) {
        this.editor.enable(v);
      }
    }
  }
};
</script>
