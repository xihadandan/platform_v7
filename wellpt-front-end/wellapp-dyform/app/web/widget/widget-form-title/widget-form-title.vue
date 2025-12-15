<template>
  <div class="dyform-title-container" :style="dyformTitleContainerComputed">
    <div class="title-container" :style="titleContainerComputed">
      <div class="main-title" :style="mainTitleComputed">
        <template v-if="titleComputed">
          <span v-if="designMode" :style="mainTitleSpanComputed">
            {{ widget.configuration.title ? widget.configuration.title.value : '' }}
          </span>
          <span v-else :style="mainTitleSpanComputed" @click="onClickTitle">{{ title }}</span>
        </template>
        <a-skeleton v-else active :paragraph="false" style="margin: auto; width: 200px" />
      </div>
      <div class="sub-title" v-if="widget.configuration.subtitleShow && widget.configuration.subtitle" :style="mainsubtitleComputed">
        <template v-if="subtitleComputed">
          <span v-if="designMode" :style="subtitleSpanComputed">
            {{ widget.configuration.subtitle ? widget.configuration.subtitle.value : '' }}
          </span>
          <span v-else :style="subtitleSpanComputed">{{ subtitle }}</span>
        </template>
      </div>
    </div>
    <div class="hr-container" v-if="widget.configuration.dividerShow">
      <template v-if="widget.configuration.dividerStyle.style == 'single'">
        <div :style="singleHrComputed"></div>
      </template>
      <template v-if="widget.configuration.dividerStyle.style == 'double'">
        <div :style="singleHrComputed"></div>
        <div class="double-hr" :style="doubleHrComputed"></div>
      </template>
      <template v-if="widget.configuration.dividerStyle.style == 'single-icon'">
        <div class="single-icon-hr" :style="singleHrComputed">
          <div class="divider-icon" v-if="widget.configuration.dividerStyle.icon" :style="dividerIconComputed">
            <i
              v-if="configuration.dividerStyle.icon.indexOf('iconfont') != -1"
              :class="configuration.dividerStyle.icon"
              :style="iconComputed"
            ></i>
            <a-icon v-else :type="widget.configuration.dividerStyle.icon" :style="iconComputed" />
          </div>
        </div>
      </template>
    </div>
  </div>
</template>
<style lang="less" scoped></style>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';

import { each, debounce } from 'lodash';

export default {
  name: 'WidgetFormTitle',
  extends: FormElement,
  mixins: [widgetMixin],
  data() {
    return {
      iconFontSize: 24,
      titleComputed: false,
      title: '',
      subtitleComputed: false,
      subtitle: ''
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    //标题容器样式
    dyformTitleContainerComputed() {
      return {
        paddingTop: this.widget.configuration.style.paddingTop ? this.widget.configuration.style.paddingTop + 'px' : '',
        paddingBottom: this.widget.configuration.style.paddingBottom ? this.widget.configuration.style.paddingBottom + 'px' : '',
        height: this.widget.configuration.style.height ? this.widget.configuration.style.height + 'px' : 'auto',
        minHeight: this.widget.configuration.style.fontSize + 'px',
        marginTop: this.widget.configuration.style.marginTop ? this.widget.configuration.style.marginTop + 'px' : '',
        marginBottom: this.widget.configuration.style.marginBottom ? this.widget.configuration.style.marginBottom + 'px' : ''
      };
    },
    //标题内容容器样式
    titleContainerComputed() {
      return {};
    },
    //主标题内容容器样式
    mainTitleComputed() {
      var style = [];
      if (this.widget.configuration.style.fontFamily) {
        style.push('font-family:' + this.widget.configuration.style.fontFamily);
      }
      if (this.widget.configuration.style.fontSize) {
        style.push('font-size:' + this.widget.configuration.style.fontSize + 'px');
      }
      if (this.widget.configuration.style.align) {
        style.push('text-align:' + this.widget.configuration.style.align);
      }
      if (this.widget.configuration.style.color) {
        style.push('color:' + this.getColorValue(this.widget.configuration.style.color));
      }
      if (this.widget.configuration.style.marginLR) {
        style.push('margin:' + '0 ' + this.widget.configuration.style.marginLR + 'px');
      }
      var fontStyle = this.widget.configuration.style.fontStyle;
      if (fontStyle && fontStyle.length > 0) {
        for (var i = 0; i < fontStyle.length; i++) {
          if (fontStyle[i] == 'bold') {
            style.push('font-weight:bold');
          } else if (fontStyle[i] == 'italic') {
            style.push('font-style:italic');
          } else if (fontStyle[i] == 'underline') {
            style.push('text-decoration:underline');
          }
        }
      }
      return style.join(';');
    },
    //主标题文字容器样式
    mainTitleSpanComputed() {
      var style = [];
      if (this.widget.configuration.style.bgcolor) {
        style.push('background-color:' + this.getColorValue(this.widget.configuration.style.bgcolor));
      }
      if (this.widget.configuration.url) {
        style.push('cursor: pointer');
      }
      return style.join(';');
    },
    mainsubtitleComputed() {
      var style = [];
      if (this.widget.configuration.subtitleStyle.fontFamily) {
        style.push('font-family:' + this.widget.configuration.subtitleStyle.fontFamily);
      }
      if (this.widget.configuration.subtitleStyle.fontSize) {
        style.push('font-size:' + this.widget.configuration.subtitleStyle.fontSize + 'px');
      }
      if (this.widget.configuration.subtitleStyle.align) {
        style.push('text-align:' + this.widget.configuration.subtitleStyle.align);
      }
      if (this.widget.configuration.subtitleStyle.color) {
        style.push('color:' + this.getColorValue(this.widget.configuration.subtitleStyle.color));
      }
      if (this.widget.configuration.subtitleStyle.marginTop) {
        style.push('margin-top:' + this.widget.configuration.subtitleStyle.marginTop + 'px');
      }
      if (this.widget.configuration.style.marginLR) {
        style.push('margin-left:' + this.widget.configuration.subtitleStyle.marginLR + 'px');
        style.push('margin-right:' + this.widget.configuration.subtitleStyle.marginLR + 'px');
      }
      var fontStyle = this.widget.configuration.subtitleStyle.fontStyle;
      if (fontStyle && fontStyle.length > 0) {
        for (var i = 0; i < fontStyle.length; i++) {
          if (fontStyle[i] == 'bold') {
            style.push('font-weight:bold');
          } else if (fontStyle[i] == 'italic') {
            style.push('font-style:italic');
          } else if (fontStyle[i] == 'underline') {
            style.push('text-decoration:underline');
          }
        }
      }
      return style.join(';');
    },
    subtitleSpanComputed() {
      var style = [];
      if (this.widget.configuration.subtitleStyle.bgcolor) {
        style.push('background-color:' + this.getColorValue(this.widget.configuration.subtitleStyle.bgcolor));
      }
      if (this.widget.configuration.url) {
        style.push('cursor: pointer');
      }
      return style.join(';');
    },
    //单行分隔线样式
    singleHrComputed() {
      var style = [];
      style.push(this.replaceTemplate(this.widget.configuration.dividerStyle.type_style, this.widget.configuration.dividerStyle));
      if (this.widget.configuration.dividerStyle.style == 'single-icon') {
        if (['solid', 'dotted'].indexOf(this.widget.configuration.dividerStyle.type) == -1) {
          var bottom = this.iconFontSize / 2 - (parseInt(this.widget.configuration.dividerStyle.width) - 1);
          style.push('margin-bottom: ' + bottom + 'px');
        } else {
          var bottom = this.iconFontSize / 2 + 3;
          style.push('margin-bottom: -' + bottom + 'px');
        }
      }
      return style.join(';');
    },
    //双行分隔线下面一条样式
    doubleHrComputed() {
      var dividerStyle = {
        width: this.widget.configuration.dividerStyle.width_buttom,
        color: this.getColorValue(this.widget.configuration.dividerStyle.color_buttom),
        type: this.widget.configuration.dividerStyle.type_buttom
      };
      return this.replaceTemplate(this.widget.configuration.dividerStyle.type_buttom_style, dividerStyle);
    },
    // 单行图标容器样式
    dividerIconComputed() {
      var style = [];
      var bottom = this.iconFontSize / 2 + parseInt(this.widget.configuration.dividerStyle.width);
      style.push('margin-bottom: -' + bottom + 'px');
      return style.join(';');
    },
    // 单行图标样式
    iconComputed() {
      var style = [];
      var iconFontSize = this.iconFontSize;
      if (this.widget.configuration.dividerStyle.icon.indexOf('iconfont') > -1) {
        iconFontSize = 18;
      }
      style.push('font-size:' + iconFontSize + 'px');
      if (['solid', 'dotted'].indexOf(this.widget.configuration.dividerStyle.type) == -1) {
        var top = this.iconFontSize / 2 - Math.floor(parseInt(this.widget.configuration.dividerStyle.width) / 2);
        style.push('top: -' + top + 'px');
      } else {
        var top = this.iconFontSize / 2 + parseInt(this.widget.configuration.dividerStyle.width);
        style.push('top: -' + top + 'px');
      }
      if (this.widget.configuration.dividerStyle.color_icon) {
        style.push('color:' + this.getColorValue(this.widget.configuration.dividerStyle.color_icon));
      }
      if (this.widget.configuration.bgcolor_containner) {
        style.push('background:' + this.widget.configuration.bgcolor_containner);
      }
      return style.join(';');
    },
    needComputeTitle() {
      if (this.designMode) {
        // 设计模式下, 不需要计算标题
        return false;
      }
      if (this.widget.configuration.title && this.widget.configuration.title.value != undefined) {
        let variables = this.widget.configuration.title.variables;
        if (variables && variables.length) {
          for (let i = 0, len = variables.length; i < len; i++) {
            if (variables[i].value.startsWith('${')) {
              return true;
            }
          }
        }
      }
      return false;
    },
    needComputeSubtitle() {
      if (this.designMode) {
        // 设计模式下, 不需要计算标题
        return false;
      }
      if (this.widget.configuration.subtitle && this.widget.configuration.subtitle.value != undefined) {
        let variables = this.widget.configuration.subtitle.variables;
        if (variables && variables.length) {
          for (let i = 0, len = variables.length; i < len; i++) {
            if (variables[i].value.startsWith('${')) {
              return true;
            }
          }
        }
      }
      return false;
    }
  },
  created() {},
  methods: {
    /**
     * 根据模板匹配并替换{}对应值
     * @param {*} template
     * @param {*} data
     */
    replaceTemplate(template, data) {
      var regex = /\{(.+?)\}/g; //获取只有花括号的内容
      if (template) {
        each(template.match(regex), function (item) {
          var reg = /^\{/gi;
          var reg2 = /\}$/gi;
          let citem = item.replace(reg, '');
          citem = citem.replace(reg2, '');
          template = template.replace(item, data[citem] || '');
        });
      }
      return template;
    },
    /**
     * 根据模板匹配并替换${}对应值
     * @param {*} template
     * @param {*} data
     */
    replaceTemplateUrl(template, data) {
      var regex = /\$\{(.+?)\}/g; //获取只有花括号的内容
      if (template) {
        each(template.match(regex), function (item) {
          var reg = /^\$\{/gi;
          var reg2 = /\}$/gi;
          let citem = item.replace(reg, '');
          citem = citem.replace(reg2, '');
          template = template.replace(item, data[citem] || '');
        });
      }
      return template;
    },
    onClickTitle() {
      if (this.widget.configuration.url) {
        var url = this.replaceTemplateUrl(this.widget.configuration.url, this.form.formData);
        window.open(url);
      }
    },
    getI18nTitleValue(type = 'title') {
      let key = `Widget.${this.widget.id}.${type}`;
      let title = $app.$i18n.t(key);
      return title == key ? this.widget.configuration[type].value : title;
    },
    getTitle(formData, type = 'title') {
      let titleExpression = encodeURIComponent(this.getI18nTitleValue(type));
      $axios
        .post(`/proxy/api/dyform/data/getDyformTitle?titleExpression=${titleExpression}`, formData)
        .then(({ data }) => {
          this[type] = data.data;
          this[type + 'Computed'] = true;
        })
        .catch(() => {
          this[type + 'Computed'] = true;
          // 异常情况下要去掉拼接的变量
          let _t = [];
          let variables = this.widget.configuration[type].variables;
          if (variables && variables.length) {
            for (let i = 0, len = variables.length; i < len; i++) {
              if (!variables[i].value.startsWith('${')) {
                _t.push(variables[i].value);
              }
            }
          }
          this[type] = _t.join();
        });
    },
    debounceGetTitle: debounce(function (res) {
      this.getTitle(res.wDyform.collectFormData(false).dyFormData, 'title');
    }, 300),
    debounceGetSubtitle: debounce(function (res) {
      this.getTitle(res.wDyform.collectFormData(false).dyFormData, 'subtitle');
    }, 300),
    getColorValue(color) {
      if (color) {
        return color.startsWith('#') ? color : `var(${color})`;
      }
      return '';
    }
  },
  beforeMount() {
    var _self = this;
    if (this.needComputeTitle) {
      // 存在表单字段变量值，需要等待表单数据变化通知
      this.getTitle({ formUuid: this.form.formUuid });
      this.form.handleEvent('formDataChanged', res => {
        _self.debounceGetTitle(res);
      });
    } else {
      this.titleComputed = true;
      this.title = this.getI18nTitleValue();
    }
    if (this.widget.configuration.subtitleShow && this.widget.configuration.subtitle) {
      if (this.needComputeSubtitle) {
        // 存在表单字段变量值，需要等待表单数据变化通知
        this.getTitle({ formUuid: this.form.formUuid }, 'subtitle');
        this.form.handleEvent('formDataChanged', res => {
          _self.debounceGetSubtitle(res);
        });
      } else {
        this.subtitleComputed = true;
        this.subtitle = this.getI18nTitleValue('subtitle');
      }
    }
  },
  mounted() {}
};
</script>
