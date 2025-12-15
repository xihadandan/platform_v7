<template>
  <view class="w-form-layout" v-show="vShow" v-if="deviceVisible" :class="widgetClass">
    <uni-w-section
      :title="$t('title', widget.configuration.title)"
      class="w-form-layout__section"
      titleColor="var(--w-text-color-mobile)"
      :headerStyle="headerStyle"
    >
      <template v-slot:decoration>
        <view class="icon-box" v-if="widget.configuration.titleIcon != undefined">
          <w-icon
            :icon="widget.configuration.titleIcon"
            color="var(--w-text-color-mobile)"
            :size="14"
            style="padding-right: 5px"
          ></w-icon>
        </view>
      </template>
      <template v-slot:default>
        <view :class="['layout-content', widget.configuration.uniConfiguration.layout]">
          <template v-for="(item, i) in rowItems">
            <view
              :class="['row', requireItemChildren[item.id].length > 0 ? 'is-required' : '', rowClass[item.id]]"
              :key="'row_' + item.id"
            >
              <view :class="['label-col']" v-if="!item.configuration.labelHidden">
                {{ $t(item.id + ".label", item.configuration.label) }}
              </view>
              <view :class="['content-col']">
                <widget
                  v-for="(wgt, w) in item.configuration.widgets"
                  :widget="wgt"
                  :parent="item"
                  :key="'formItem_' + wgt.id"
                ></widget>
              </view>
            </view>
          </template>
        </view>
      </template>
    </uni-w-section>

    <!-- 隐藏字段校验代理项 -->
    <template v-for="(obj, i) in proxyHiddenFieldWidgets">
      <FormItemHiddenField
        :widget="obj.widget"
        :key="'proxySubHField' + i"
        :parent="obj.item"
        @fieldStateChange="(e) => onHideFieldStateChange(e, obj)"
      ></FormItemHiddenField>
    </template>
  </view>
</template>

<script>
import mixin from "../w-dyform/form-element.mixin.js";
// import wDyformWidget from "../w-dyform-widget/w-dyform-widget.vue";
// #ifndef APP-PLUS
import "./index.scss";
// #endif
import { storage, appContext, utils, pageContext } from "wellapp-uni-framework";
import { debounce, each, filter } from "lodash";
import FormItemHiddenField from "./form-item-hidden-field.vue";
export default {
  mixins: [mixin],
  name: "w-form-layout",
  props: {},
  components: { FormItemHiddenField },
  options: {
    styleIsolation: "shared",
  },
  data() {
    if (this.widget.configuration.uniConfiguration == undefined) {
      this.widget.configuration.uniConfiguration = {
        layout: "horizontal",
      };
    }
    let requireItemChildren = {};
    let rowClass = {};
    for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
      requireItemChildren[this.widget.configuration.items[i].id] = [];
      rowClass[this.widget.configuration.items[i].id] = [];
      if (
        this.widget.configuration.items[i].configuration.uniConfiguration &&
        this.widget.configuration.items[i].configuration.uniConfiguration.layout
      ) {
        if (this.widget.configuration.items[i].configuration.uniConfiguration.layout == "horizontal") {
          rowClass[this.widget.configuration.items[i].id].push("content-align-right");
          rowClass[this.widget.configuration.items[i].id].push("row-horizontal");
        } else {
          rowClass[this.widget.configuration.items[i].id].push("row-vertical");
        }
      } else if (this.widget.configuration.items[i].configuration.widgets.length) {
        let widget = this.widget.configuration.items[i].configuration.widgets[0]; //按第一个组件
        // 多行文本，富文本，附件，数值条，复选框,单选
        if (
          [
            "WidgetFormRichTextEditor",
            "WidgetFormFileUpload",
            "WidgetFormNumericalBar",
            "WidgetFormCheckbox",
            "WidgetFormRadio",
            "WidgetFormAmap",
          ].indexOf(widget.wtype) > -1 ||
          (widget.wtype == "WidgetFormInput" && widget.subtype == "Textarea")
        ) {
          rowClass[this.widget.configuration.items[i].id].push("row-vertical");
        } else {
          if (this.widget.configuration.uniConfiguration.layout == "horizontal") {
            rowClass[this.widget.configuration.items[i].id].push("content-align-right");
          }
        }
      }
    }

    return {
      hiddenWidgets: new Set(),
      hiddenItems: [],
      calcVisibleItemIds: [],
      proxyHiddenFieldWidgets: [],
      requireItemChildren,
      rowClass,
      subformWidgetIds: [],
    };
  },
  computed: {
    headerStyle() {
      let style = {};
      if (this.widget.configuration.titleHidden == true) {
        style.display = "none";
      }
      return style;
    },
    rowItems() {
      let items = filter(utils.deepClone(this.widget.configuration.items), (item, index) => {
        return !this.hiddenItems.includes(item.id) && this.calcVisibleItemIds.includes(item.id);
      });
      return items;
    },
    defaultEvents() {
      return [
        { id: "showFormLayout", title: "显示表单布局" },
        { id: "hideFormLayout", title: "隐藏表单布局" },
      ];
    },
  },
  created() {
    if (!this.hidden) {
      // 非隐藏的情况下，要判断其下的子元素是否都是隐藏的
      let len = this.widget.configuration.items.length,
        hiddenItemCnt = 0;
      for (let i = 0; i < len; i++) {
        let item = this.widget.configuration.items[i];
        if (item.configuration.widgets) {
          let jlen = item.configuration.widgets.length,
            hiddenCnt = 0,
            requiredCnt = 0;
          for (let j = 0; j < jlen; j++) {
            if (item.configuration.widgets[j].wtype == "WidgetSubform") {
              this.subformWidgetIds.push(item.configuration.widgets[j].id);
            }
            if (this.form.formElementRules && this.form.formElementRules[item.configuration.widgets[j].id]) {
              if (this.form.formElementRules[item.configuration.widgets[j].id].hidden) {
                hiddenCnt++;
              }
              if (this.form.formElementRules[item.configuration.widgets[j].id].required) {
                this.requireItemChildren[item.id].push(item.configuration.widgets[j].id);
              }
              continue;
            }
            if (item.configuration.widgets[j].configuration.required) {
              this.requireItemChildren[item.id].push(item.configuration.widgets[j].id);
            }

            // 组件默认的配置
            if (item.configuration.widgets[j].configuration.defaultDisplayState === "hidden") {
              hiddenCnt++;
            }
          }
          if (hiddenCnt == jlen && jlen > 0) {
            item.configuration.hidden = true;
            hiddenItemCnt++;
            this.hiddenItems.push(item.id);
          }

          item.configuration.required = requiredCnt > 0; // 至少一个必填字段存在
        }
      }

      if (hiddenItemCnt === len && len > 0) {
        this.hidden = true;
      }
    }

    // 初始计算显示的单元格集合
    for (let item of this.widget.configuration.items) {
      let configuration = item.configuration,
        visible = !configuration.hidden;
      if (
        visible &&
        (configuration.defaultVisible == undefined ||
          (configuration.defaultVisible &&
            configuration.defaultVisibleVar &&
            (!configuration.defaultVisibleVar.enable || configuration.defaultVisibleVar.conditions.length == 0)))
      ) {
        this.calcVisibleItemIds.push(item.id);
      }
    }

    this.proxySubFieldEvent = debounce(this.proxySubFieldEvent.bind(this), 200);
  },
  beforeMount() {},
  mounted() {
    this.requiredHook();
    this.handleFormItemChildVisibleChange();
  },
  methods: {
    showFormLayout() {
      this.hidden = false;
      this.hiddenByRule = false;
      this.calculateWidgetVisible = true;
    },
    hideFormLayout() {
      this.hidden = true;
      this.hiddenByRule = true;
      this.calculateWidgetVisible = false;
    },
    handleFormItemChildVisibleChange() {
      for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
        // 绑定处理单元格内的子元素显示变更
        let item = this.widget.configuration.items[i];
        this.pageContext
          .offEvent(`Widget:${item.id}:Child:VisibleChange`)
          .handleEvent(`Widget:${item.id}:Child:VisibleChange`, ({ id, visible }) => {
            let subHiddenCount = 0;
            for (let i = 0, wLen = item.configuration.widgets.length; i < wLen; i++) {
              if (item.configuration.widgets[i].id == id) {
                if (visible) {
                  this.hiddenWidgets.delete(id);
                } else {
                  this.hiddenWidgets.add(id);
                }
              }
              if (this.hiddenWidgets.has(item.configuration.widgets[i].id)) {
                subHiddenCount++;
              }
            }
            // 单元格下子元素都隐藏了，则此单元格也需要隐藏
            item.configuration.hidden = subHiddenCount == item.configuration.widgets.length;
            if (item.configuration.hidden && !this.hiddenItems.includes(item.id)) {
              this.hiddenItems.push(item.id);
            } else if (!item.configuration.hidden && this.hiddenItems.includes(item.id)) {
              this.hiddenItems.splice(this.hiddenItems.indexOf(item.id), 1);
            }

            // 判断当前布局下的单元格是否的隐藏状况
            subHiddenCount = 0;
            for (let i = 0; i < len; i++) {
              if (this.widget.configuration.items[i].configuration.hidden) {
                subHiddenCount++;
              }
            }

            if ((this.hidden && this.hiddenWidgets.size !== len) || (!this.hidden && this.hiddenWidgets.size == len)) {
              this.hidden = this.hiddenWidgets.size == len;
            }
          });
      }
    },
    requiredHook() {
      let _this = this;
      for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
        let item = this.widget.configuration.items[i];
        this.dyform.handleEvent("onChildRequiredChanged." + item.id, function (required, id) {
          if (required && !_this.requireItemChildren[item.id].includes(id)) {
            _this.requireItemChildren[item.id].push(id);
          }
          if (!required && _this.requireItemChildren[item.id].includes(id)) {
            _this.requireItemChildren[item.id].splice(_this.requireItemChildren[item.id].indexOf(id), 1);
          }
          item.configuration.required = required;
        });
      }
    },

    proxySubFieldEvent() {
      let _this = this;
      this.proxyHiddenFieldWidgets.splice(0, this.proxyHiddenFieldWidgets.length);
      for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
        // FIXME: 通过栅格进行布局的字段需要处理
        let item = this.widget.configuration.items[i];
        if (this.hiddenItems.includes(item.id) || !this.calcVisibleItemIds.includes(item.id)) {
          let subWidgets = item.configuration.widgets;
          subWidgets.forEach((wgt) => {
            if (wgt.configuration.isDatabaseField) {
              this.proxyHiddenFieldWidgets.push({ widget: wgt, item: item });
            }
          });
        }
      }
    },

    onHideFieldStateChange(e, record) {
      let { currentState, oldState } = e,
        { widget, item } = record;
      widget.configuration.defaultDisplayState = currentState.editable ? "edit" : "unedit";
      // 通过事件触发的显隐，要删除对应的规则，否则组件初始化还是以规则为主
      if (
        oldState.hidden !== currentState.hidden &&
        this.form.formElementRules &&
        this.form.formElementRules[widget.id]
      ) {
        delete this.form.formElementRules[widget.id].hidden;
      }

      // 通过事件触发的编辑，要删除对应的规则，否则组件初始化还是以规则为主
      if (
        oldState.editable !== currentState.editable &&
        this.form.formElementRules &&
        this.form.formElementRules[widget.id]
      ) {
        delete this.form.formElementRules[widget.id].editable;
        delete this.form.formElementRules[widget.id].hidden;
      }

      item.configuration.hidden = this.isItemSubWidgetAllHide(item);
      if (!item.configuration.hidden && this.hiddenItems.includes(item.id)) {
        this.hiddenItems.splice(this.hiddenItems.indexOf(item.id), 1);
      }
      if (item.configuration.hidden && !this.hiddenItems.includes(item.id)) {
        this.hiddenItems.push(item.id);
      }
    },

    isItemSubWidgetAllHide(item) {
      if (item.configuration.widgets) {
        let jlen = item.configuration.widgets.length,
          hiddenCnt = 0;
        if (this.itemSubWidgetAllHidden.includes(item.id)) {
          return true;
        }
        for (let j = 0; j < jlen; j++) {
          if (this.form.formElementRules && this.form.formElementRules[item.configuration.widgets[j].id]) {
            if (this.form.formElementRules[item.configuration.widgets[j].id].hidden) {
              hiddenCnt++;
            }

            continue;
          }

          // 组件默认的配置
          if (item.configuration.widgets[j].configuration.defaultDisplayState === "hidden") {
            hiddenCnt++;
          }
        }
        return hiddenCnt == jlen && jlen > 0;
      }
    },

    reCalculateVisibleItemIds() {
      let ids = [],
        promises = [];
      for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
        let item = this.widget.configuration.items[i];
        let configuration = item.configuration,
          visible = !configuration.hidden;
        if (visible && configuration.defaultVisible != undefined) {
          promises.push(
            this.calculateVisibleByCondition(configuration.defaultVisibleVar, configuration.defaultVisible)
          );
        } else {
          promises.push(Promise.resolve(true));
        }
      }
      Promise.all(promises).then((results) => {
        for (let i = 0, len = results.length; i < len; i++) {
          if (results[i]) {
            ids.push(this.widget.configuration.items[i].id);
          }
          this.calcVisibleItemIds.splice(0, this.calcVisibleItemIds.length, ...ids);
        }
      });
    },
    afterChangeableDependDataChanged() {
      this.reCalculateVisibleItemIds();
    },
    // 表单布局隐藏到展开，从表折叠面板展开高度需重新计算
    refreshSubformWidgetsHeight() {
      this.subformWidgetIds.forEach((id) => {
        let $subform = pageContext.getComponent(id);
        if ($subform) {
          setTimeout(() => {
            $subform.collapseResize();
          }, 10);
        }
      });
    },
  },

  watch: {
    hiddenItems: {
      handler(v, o) {
        this.proxySubFieldEvent();
      },
    },
    calcVisibleItemIds: {
      handler(v, o) {
        this.proxySubFieldEvent();
      },
    },
    vShow: {
      handler(v, o) {
        if (v) {
          if (this.subformWidgetIds.length > 0) {
            this.refreshSubformWidgetsHeight();
          }
        }
      },
    },
  },
};
</script>

<style lang="scss" scoped>
/* #ifdef APP-PLUS */
@import "./index.scss";
/* #endif */
</style>
