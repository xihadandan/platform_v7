<template>
  <view
    :class="[
      'uni-w-editor-container',
      fullscreen ? 'fullscreen' : '',
      disable ? 'disabled' : '',
      readOnly ? 'readonly' : '',
    ]"
  >
    <view v-show="!displayAsLabel">
      <view :id="id + '_toolbar'" class="ql-editor-toolbar" :style="'bottom: ' + (isIOS ? keyboardHeight : 0) + 'px'">
        <view :class="['ql-editor-toolbar-mask', disable ? 'disable' : '', readOnly ? 'readonly' : '']">
          <view class="ql-editor-toolbar__block">
            <view
              @tap.stop="openFontSize"
              data-name="fontSize"
              :class="['ql-editor-toolbar__btn', formats.fontSize ? 'ql-active-fontsize' : '']"
              :title="$t('quill.fontSize', '字体大小')"
            >
              <i :class="'ant-iconfont font-size'" data-name="fontSize"></i>
              <text style="padding-left: 4px">{{ formats.fontSize || $t("quill.defaultFontSize", "默认") }}</text>
            </view>
          </view>
          <view class="ql-editor-toolbar__block">
            <view
              :class="['ql-editor-toolbar__btn', formats.bold ? 'ql-active' : '']"
              :title="$t('quill.bold', '加粗')"
            >
              <i class="ant-iconfont bold" data-name="bold" @tap.stop="format"></i>
            </view>
            <view
              :class="['ql-editor-toolbar__btn', formats.underline ? 'ql-active' : '']"
              :title="$t('quill.underline', '下划线')"
            >
              <i :class="'ant-iconfont underline'" data-name="underline" @tap.stop="format"></i>
            </view>
            <view
              :class="['ql-editor-toolbar__btn', formats.italic ? 'ql-active' : '']"
              :title="$t('quill.italic', '斜体')"
            >
              <i :class="'ant-iconfont italic'" data-name="italic" @tap.stop="format"></i>
            </view>
          </view>
          <view class="ql-editor-toolbar__block">
            <view
              :class="['ql-editor-toolbar__btn', formats.align ? 'ql-active' : '']"
              @tap.stop="popupShow('alignPopup')"
              :title="$t('quill.align', '对齐')"
            >
              <i
                :class="{
                  'ant-iconfont align-left': !formats.align || formats.align === 'left',
                  'ant-iconfont align-center': formats.align === 'center',
                  'ant-iconfont align-right': formats.align === 'right',
                  'ant-iconfont menu': formats.align === 'justify',
                }"
                data-name="align"
              ></i>
            </view>
          </view>
          <view class="ql-editor-toolbar__block ql-editor-toolbar__block-more">
            <view
              v-for="(btn, idx) in moreBtns"
              :key="'btn_' + btn.name"
              :class="[
                'ql-editor-toolbar__btn',
                formats[btn.name] && formats[btn.name] === btn.value ? 'ql-active' : '',
              ]"
              :title="btn.title"
              v-if="btn.visible"
            >
              <i
                :class="btn.class"
                @tap.stop="(e) => moreBtnsClick(e, btn)"
                :style="{
                  color:
                    btn.name == 'color' ? formats.color : btn.name == 'backgroundColor' ? formats.backgroundColor : '',
                }"
                :data-name="btn.name"
                :data-value="btn.value"
              ></i>
            </view>
            <view class="ql-editor-toolbar__btn" :title="$t('quill.more', '更多')" v-if="moreBtnLength">
              <i class="iconfont icon-ptkj-gengduocaidan" @tap.stop="popupShow('morePopup')"></i>
            </view>
          </view>
        </view>
        <view class="ql-editor-toolbar__block">
          <view
            class="fullscreen ql-editor-toolbar__btn"
            @tap.stop="fullscreen = !fullscreen"
            :title="$t('quill.maximize', '全屏')"
          >
            <i :class="['ant-iconfont ', fullscreen ? 'fullscreen-exit' : 'fullscreen']"></i>
          </view>
        </view>
      </view>
      <editor
        :id="id"
        :read-only="!editable"
        class="quill-text-editor ql-snow"
        :placeholder="placeholder"
        @statuschange="onStatusChange"
        @ready="onEditorReady"
        @input="inputEditor"
        :style="{ minHeight: vMinHeight, height: fullscreen ? '100vh' : '' }"
      >
      </editor>
    </view>
    <template v-if="displayAsLabel">
      <rich-text class="ql-editor" :nodes="showValue"></rich-text>
    </template>
    <!-- 字体大小选择 -->
    <uni-popup
      ref="fontSizePopup"
      type="bottom"
      background-color="#fff"
      :mask-click="false"
      borderRadius="16px 16px 0 0"
    >
      <view class="select-popus-content">
        <view class="select-popus-content__title"
          >{{ selectJson.title }}
          <uni-w-button
            type="text"
            @click="closeFontSize"
            icon="iconfont icon-ptkj-dacha-xiao"
            class="closeBtn"
          ></uni-w-button>
        </view>
        <scroll-view style="height: 200px" scroll-y>
          <view
            class="select-popus-content__item"
            :class="{ 'ql-active': !formats.fontSize }"
            @tap="selectItem(selectJson.param, '')"
            >{{ $t("quill.defaultFontSize", "默认") }}</view
          >
          <view
            v-for="item in selectJson.options"
            :key="'fstyle_' + item"
            class="select-popus-content__item"
            :class="{ 'ql-active': formats[selectJson.param] == item }"
            @tap="selectItem(selectJson.param, item)"
          >
            {{ item }}
          </view>
        </scroll-view>
      </view>
    </uni-popup>
    <!-- 颜色选择 -->
    <uni-popup ref="colorPopup" type="bottom" background-color="#fff" :mask-click="false" borderRadius="16px 16px 0 0">
      <view class="select-popus-content">
        <view class="select-popus-content__title"
          >{{ colorJson.title }}
          <uni-w-button
            type="text"
            @click="closeColor"
            icon="iconfont icon-ptkj-dacha-xiao"
            class="closeBtn"
          ></uni-w-button>
        </view>
        <scroll-view style="height: 200px" scroll-y>
          <view class="select-popus-content__colors">
            <view
              v-for="item in colorJson.options"
              :key="'fColor_' + item"
              class="select-popus-content__c-item"
              :class="{ 'ql-c-active': formats[colorJson.param] == item }"
              @tap="selectItem(colorJson.param, item)"
            >
              <view
                class="select-popus-content__c-item-content"
                :style="{ background: item }"
                @click="selectColor(item)"
              >
              </view>
            </view>
          </view>
        </scroll-view>
      </view>
    </uni-popup>
    <!-- 更多按钮 -->
    <uni-popup ref="morePopup" type="bottom" background-color="#fff" :mask-click="false" borderRadius="16px 16px 0 0">
      <view class="select-popus-content">
        <view class="select-popus-content__title"
          >{{ $t("quill.more", "更多") }}
          <uni-w-button
            type="text"
            @click="popupClose('morePopup')"
            icon="iconfont icon-ptkj-dacha-xiao"
            class="closeBtn"
          ></uni-w-button>
        </view>
        <scroll-view style="height: 200px" scroll-y>
          <view class="select-popus-content__btns">
            <view
              v-for="(btn, idx) in moreBtns"
              :key="'btn_' + btn.name + '_' + idx"
              :class="[
                'select-popus-content__btn',
                formats[btn.name] && formats[btn.name] === btn.value ? 'ql-active' : '',
                (btn.name == 'color' || btn.name == 'backgroundColor') && formats[btn.name] ? 'ql-active' : '',
              ]"
              :title="btn.title"
              v-if="!btn.visible"
            >
              <view class="select-popus-content__btn-icon">
                <i
                  :class="btn.class"
                  @tap.stop="(e) => moreBtnsClick(e, btn, 'morePopup')"
                  :style="{
                    color:
                      btn.name == 'color'
                        ? formats.color
                        : btn.name == 'backgroundColor'
                        ? formats.backgroundColor
                        : '',
                  }"
                  :data-name="btn.name"
                  :data-value="btn.value"
                ></i>
              </view>
              <view class="select-popus-content__btn-title">{{ btn.title }}</view>
            </view>
          </view>
        </scroll-view>
      </view>
    </uni-popup>
    <!-- 排列 -->
    <uni-popup ref="alignPopup" type="bottom" background-color="#fff" :mask-click="false" borderRadius="16px 16px 0 0">
      <view class="select-popus-content">
        <view class="select-popus-content__title"
          >{{ $t("quill.selectAlignType", "对齐方式") }}
          <uni-w-button
            type="text"
            @click="popupClose('alignPopup')"
            icon="iconfont icon-ptkj-dacha-xiao"
            class="closeBtn"
          ></uni-w-button>
        </view>
        <scroll-view style="height: 150px" scroll-y>
          <view class="select-popus-content__btns">
            <view
              v-for="(btn, idx) in alignBtns"
              :key="'btn_' + btn.value"
              :class="[
                'select-popus-content__btn',
                formats[btn.name] && formats[btn.name] === btn.value ? 'ql-active' : '',
              ]"
              :title="btn.title"
              v-if="!btn.visible"
            >
              <view class="select-popus-content__btn-icon">
                <i
                  :class="btn.class"
                  @tap.stop="(e) => moreBtnsClick(e, btn, 'alignPopup')"
                  :data-name="btn.name"
                  :data-value="btn.value"
                ></i>
              </view>
              <view class="select-popus-content__btn-title">{{ btn.title }}</view>
            </view>
          </view>
        </scroll-view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
// #ifndef APP-PLUS
import "quill/dist/quill.snow.css";
import "../../css/quill-editor.scss";
// #endif
import { getColors, getBgColors } from "../../js/colors.js";
import { utils, storage } from "wellapp-uni-framework";
import SparkMD5 from "spark-md5";

// #ifdef H5
import quill from "quill";
window.Quill = quill;
// #endif

/* 富文本编辑图片上传配置 */
const uploadConfig = {
  name: "img", // 必填参数 文件的参数名
  accept: "image/png, image/gif, image/jpeg, image/bmp, image/x-icon", // 可选 可上传的图片格式
};
const jwtToken = storage.getAccessToken();
const wellappBackendUrl = storage.getWellappBackendUrl();

const proxyApi = "/server-api";
export default {
  name: "uni-w-editor",
  props: {
    value: String,
    editable: {
      type: Boolean,
      default: true,
    },
    readOnly: {
      type: Boolean,
      default: false,
    },
    disable: {
      type: Boolean,
      default: false,
    },
    displayAsLabel: {
      type: Boolean,
      default: false,
    },
    htmlCodec: {
      type: Boolean,
      default: false,
    },
    placeholder: String,
    hiddenButtons: {
      type: Array,
      default: () => {
        return [];
      },
    },
    minHeight: {
      type: [String, Number],
      default: "200px",
    },
  },
  data() {
    let editorContent = this.valueToEditor(this.value || "");
    return {
      editorContent,
      fontColor: "#000",
      formats: {},
      editorHeight: 300,
      keyboardHeight: 0,
      isIOS: false,
      id: `editor-${utils.generateId()}`,
      editor: undefined,
      fontStyle: [
        {
          name: this.$t("quill.fontFamily.songti", "宋体"),
          value: "songti",
          code: "songti",
        },
        {
          name: this.$t("quill.fontFamily.heiti", "黑体"),
          value: "heiti",
          code: "heiti",
        },
        {
          name: this.$t("quill.fontFamily.fangsong", "仿宋"),
          value: "fangsong",
          code: "fangsong",
        },
        {
          name: this.$t("quill.fontFamily.kaiti", "楷体"),
          value: "kaiti",
          code: "kaiti",
        },
        {
          name: this.$t("quill.fontFamily.lishu", "隶书"),
          value: "lishu",
          code: "lishu",
        },
        {
          name: this.$t("quill.fontFamily.youyuan", "幼圆"),
          value: "youyuan",
          code: "youyuan",
        },
        {
          name: this.$t("quill.fontFamily.weiruanyahei", "微软雅黑"),
          value: "weiruanyahei",
          code: "weiruanyahei",
        },
      ],
      fontSize: "8px,9px,10x,11px,12px,14px,16px,18px,20px,22px,24px,26px,28px,36px,48px,72px".split(","),
      selectJson: {
        title: this.$t("quill.fontSize", "字体大小"),
        options: [],
        param: "fontSize",
      },
      colors: getColors(),
      backgrounds: getBgColors(),
      colorJson: {
        title: this.$t("quill.color", "字体颜色"),
        options: [],
        param: "color",
      },
      fullscreen: false,
      fileUploadMap: {},
      chunkSize: 2 * 1024 * 1024,
      arrEntities: {
        //普通字符转换成转意符
        "<": "&lt;",
        ">": "&gt;",
        "&": "&amp;",
        '"': "&quot;",
        "'": "&apos;",
      },
      md5FileObj: {},
      isChange: false,
      btnShowLength: 2,
      moreBtnLength: 0,
      alignBtns: [
        {
          class: "ant-iconfont align-left",
          value: "left",
          name: "align",
          title: this.$t("quill.leftAlign", "左对齐"),
          click: "format",
        },
        {
          class: "ant-iconfont align-center",
          value: "center",
          name: "align",
          title: this.$t("quill.centerAlign", "居中对齐"),
          click: "format",
        },
        {
          class: "ant-iconfont align-right",
          value: "right",
          name: "align",
          title: this.$t("quill.rightAlign", "右对齐"),
          click: "format",
        },
        {
          class: "ant-iconfont menu",
          value: "justify",
          name: "align",
          title: this.$t("quill.justifyAlign", "两端对齐"),
          click: "format",
        },
      ],
    };
  },
  computed: {
    vMinHeight() {
      if (this.fullscreen) {
        return "100%";
      }
      return typeof this.minHeight == "number" ? `${this.minHeight}px` : this.minHeight;
    },
    showValue() {
      if (this.displayAsLabel && this.isChange) {
        return this.html2EscapeHandle(this.editorContent ? this.editorContent : null);
      }
      return this.editorContent || "";
    },
    moreBtns() {
      this.moreBtnLength = 0;
      let btns = [
        {
          class: "ant-iconfont font-colors",
          value: "",
          name: "color",
          title: this.$t("quill.color", "字体颜色"),
          click: "openColor",
        },
        {
          class: "ant-iconfont bg-colors",
          value: "",
          name: "backgroundColor",
          title: this.$t("quill.backgroundColor", "背景颜色"),
          click: "openColor",
        },
        {
          class: "ant-iconfont ordered-list",
          value: "ordered",
          name: "list",
          title: this.$t("quill.ordered", "有序列表"),
          click: "format",
        },
        {
          class: "ant-iconfont unordered-list",
          value: "bullet",
          name: "list",
          title: this.$t("quill.bullet", "无序列表"),
          click: "format",
        },
        {
          class: "ant-iconfont menu-fold",
          value: "-1",
          name: "indent",
          title: this.$t("quill.minusIndent", "减少缩进量"),
          click: "format",
        },
        {
          class: "ant-iconfont menu-unfold",
          value: "+1",
          name: "indent",
          title: this.$t("quill.addIndent", "增加缩进量"),
          click: "format",
        },
        {
          class: "ant-iconfont picture",
          value: "",
          name: "picture",
          title: this.$t("quill.insertImage", "添加图片"),
          click: "handleImgUpload",
        },
      ];
      for (let i = 0; i < btns.length; i++) {
        if (i < this.btnShowLength - 1) {
          btns[i].visible = true;
        } else {
          this.moreBtnLength++;
          btns[i].visible = false;
        }
      }
      return btns;
    },
  },
  mounted() {
    setTimeout(() => {
      this.getToolBarRect();
    }, 10);
  },
  methods: {
    hideKey() {
      uni.hideKeyboard();
    },
    onEditorReady() {
      let _self = this;
      uni
        .createSelectorQuery()
        .select("#" + _self.id)
        .context(function (res) {
          _self.editor = res.context;
          _self.editor.setContents({
            html: _self.editorContent,
            // delta:contents
          });
        })
        .exec();
    },
    undo() {
      this.editor.undo();
    },

    redo() {
      this.editor.redo();
    },

    blur() {
      this.editor.blur();
    },

    format(e) {
      // this.hideKey();
      let { name, value } = e.currentTarget.dataset;
      console.log("format", name, value);
      if (!name) return;
      this.editor.format(name, value);
    },

    onStatusChange(e) {
      this.formats = e.detail;
    },

    insertDivider() {
      this.editor.insertDivider({
        success: function () {
          console.log("insert divider success");
        },
      });
    },

    store(e) {
      this.editor.getContents({
        success: function (res) {
          e.currentTarget.id == 1
            ? console.log("保存内容:", res.html)
            : uni.navigateTo({
                url: `../preview/preview?rich=${encodeURIComponent(res.html)}`,
              });
        },
      });
    },

    clear() {
      this.editor.clear({
        success: function (res) {
          console.log("clear success");
        },
      });
    },

    removeFormat() {
      this.editor.removeFormat();
    },

    insertDate() {
      const date = new Date();
      const formatDate = `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`;
      this.editor.insertText({
        text: formatDate,
      });
    },

    insertImage() {
      let _self = this;
      uni.chooseImage({
        count: 1,
        success: function (res) {
          _self.editor.insertImage({
            src: res.tempFilePaths[0],
            data: {
              id: "abcd",
              role: "god",
            },
            width: "80%",
            success: function () {
              console.log("insert image success");
            },
          });
        },
      });
    },

    // 字体大小选择
    openFontSize(e) {
      let { name } = e.currentTarget.dataset;
      if (name) {
        if (name == "fontSize") {
          this.selectJson = {
            title: this.$t("quill.fontSize", "字体大小"),
            options: this.fontSize,
            param: "fontSize",
          };
        }
        this.$refs.fontSizePopup.open();
      }
    },

    closeFontSize() {
      this.$refs.fontSizePopup.close();
    },

    selectItem(name, value) {
      this.editor.format(name, value);
      this.closeFontSize();
    },

    // 颜色选择
    openColor(e) {
      let { name } = e.currentTarget.dataset;
      if (name) {
        if (name == "color") {
          this.colorJson = {
            title: this.$t("quill.color", "字体颜色"),
            options: this.colors,
            param: "color",
          };
        } else if (name == "backgroundColor") {
          this.colorJson = {
            title: this.$t("quill.backgroundColor", "背景颜色"),
            options: this.backgrounds,
            param: "backgroundColor",
          };
        }
        this.popupClose("morePopup");
        this.$refs.colorPopup.open();
      }
    },

    closeColor() {
      this.$refs.colorPopup.close();
    },

    selectColor(item) {
      this.editor.format(this.colorJson.param, item);
      this.closeColor();
    },

    // 打开按钮
    popupShow(popup) {
      popup && this.$refs[popup] && this.$refs[popup].open();
    },
    popupClose(popup) {
      popup && this.$refs[popup] && this.$refs[popup].close();
    },
    // 按钮点击事件
    moreBtnsClick(e, btn, popupName) {
      if (typeof this[btn.click] == "function") {
        this[btn.click](e);
      }
      if (popupName) {
        this.popupClose(popupName);
      }
    },

    inputEditor(e) {
      this.editorContent = e.detail.html;
      this.$emit("input", this.valueToFormData(this.editorContent));
      this.$emit("change", this.valueToFormData(this.editorContent), e.detail);
    },

    setHtml(html) {
      this.editorContent = this.valueToEditor(html);
      this.editor &&
        this.editor.setContents({
          html: this.editorContent,
        });
      if (this.displayAsLabel) {
        this.isChange = !this.isChange;
      }
    },
    getHtml() {
      return this.valueToFormData(this.editorContent);
    },
    // 显示值
    displayValue() {
      return this.html2EscapeHandle(this.editorContent ? this.editorContent : null);
    },
    // HTML转义
    html2EscapeHandle(value) {
      if (value == "<p><br></p>") {
        value = "";
      }
      if (this.htmlCodec) {
        return typeof value === "string"
          ? value.replace(/[<>&"']/g, (c) => {
              return this.arrEntities[c];
            })
          : value;
      }
      return value || "";
    },
    handleImgUpload() {
      var self = this;
      if (this.editable) {
        uni.chooseImage({
          count: 1,
          success: (res) => {
            console.log("handleImgUpload", res);
            self.uploadImage(res.tempFiles[0], function (data) {
              self.editor.insertImage({
                src: data.url,
                success: function () {
                  console.log("insert image success");
                },
              });
            });
          },
        });
      }
    },
    uploadImage(file, callback) {
      var _this = this;
      const options = {
        file: file,
      };
      _this.uploadFile(options.file, {}, function (_file) {
        callback(_file);
      });
    },
    uploadFile(file, parameter, callback) {
      var _this = this;
      const filePath = file.path;
      uni.uploadFile({
        url: wellappBackendUrl + "/repository/file/mongo/savefiles?jwt=" + jwtToken,
        filePath,
        name: "fileUpload",
        formData: {},
        success: (uploadFileRes) => {
          console.log("upload success", uploadFileRes);
          let result = JSON.parse(uploadFileRes.data);
          if (result.code == 0 && result.data) {
            let dbFile = result.data[0];
            // #ifndef APP-PLUS
            let url = `${proxyApi}/repository/file/mongo/download?fileID=${dbFile.fileID}&jwt=${jwtToken}`;
            // #endif
            // #ifdef APP-PLUS
            let url = `${wellappBackendUrl}/repository/file/mongo/download?fileID=${dbFile.fileID}&jwt=${jwtToken}`;
            // #endif
            if (typeof callback === "function") {
              callback.call(_this, { url });
            }
          }
        },
        fail: (err) => {
          console.log("upload fail", err);
        },
      });
    },
    // 源代码显示切换
    // showSourceEditor() {
    // 	eeSourceBtn.prototype.showSourceEditor(this.editor);
    // },
    /**
     * 以/proxy-repository开头的图片地址要通过fillAccessResourceUrl转换才可以正常显示
     * 数据库存的图片地址：/proxy-repository/repository/file/mongo/download?fileID=XXX
     */
    valueToFormData(value) {
      var newContent = value.replace(/<img [^>]*src=['"]([^'"]+)[^>]*>/gi, function (match, capture) {
        if (capture.includes("/repository/file/mongo/download")) {
          // #ifndef APP-PLUS
          capture = capture.replace(proxyApi, "/proxy-repository");
          // #endif
          // #ifdef APP-PLUS
          capture = capture.replace(wellappBackendUrl, "/proxy-repository");
          // #endif
          let captureArr = capture.split("&amp;jwt=");
          capture = captureArr[0];
        }
        //capture,返回每个匹配的字符串
        var newStr = '<img src="' + capture + '"/>';
        return newStr;
      });
      return newContent;
    },
    /**
     * 以/proxy-repository开头的图片地址要通过fillAccessResourceUrl转换才可以正常显示
     */
    valueToEditor(value) {
      if (!value) {
        value = "";
      }
      var newContent = value.replace(/<img [^>]*src=['"]([^'"]+)[^>]*>/gi, function (match, capture) {
        if (capture.includes("/proxy-repository")) {
          // #ifndef APP-PLUS
          capture = capture.replace("/proxy-repository", proxyApi);
          // #endif
          // #ifdef APP-PLUS
          capture = capture.replace("/proxy-repository", wellappBackendUrl);
          // #endif
          capture += `&jwt=${jwtToken}`;
        }
        //capture,返回每个匹配的字符串
        var newStr = '<img src="' + capture + '"/>';
        return newStr;
      });
      return newContent;
    },
    getToolBarRect() {
      const views = uni.createSelectorQuery().in(this);
      views
        .select(".ql-editor-toolbar-mask")
        .boundingClientRect((data) => {
          if (data) {
            this.getToolBarMoreRect(data);
          }
        })
        .exec();
    },
    getToolBarMoreRect(maskData) {
      const views = uni.createSelectorQuery().in(this);
      views
        .select(".ql-editor-toolbar__block-more")
        .boundingClientRect((data) => {
          if (data) {
            let leftWidth = data.left - maskData.left;
            let visibleWidth = maskData.width - leftWidth;
            this.btnShowLength = Math.floor(visibleWidth / 30); // 能够显示的按钮数
          }
        })
        .exec();
    },
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
  },
  watch: {
    value: {
      handler(v, o) {
        console.log("value", v);
      },
    },
  },
};
</script>
<style lang="scss" scoped>
/* #ifdef APP-PLUS */
@import "quill/dist/quill.snow.css";
@import "../../css/quill-editor.scss";
/* #endif */
</style>
