<template>
  <view class="w-form-layout" v-show="vShow">
    <uni-w-section
      :title="widget.configuration.title"
      titleColor="var(--color-primary)"
      :headerStyle="{
        fontWeight: 'bolder',
      }"
    >
      <template v-slot:decoration>
        <view class="icon-box" v-if="widget.configuration.titleIcon != undefined">
          <uni-icons
            :custom-prefix="widget.configuration.titleIcon.startsWith('iconfont') ? 'iconfont' : undefined"
            :type="widget.configuration.titleIcon"
            color="var(--color-primary)"
            size="12"
            style="padding-right: 5px"
          ></uni-icons>
        </view>
      </template>
      <template v-slot:default>
        <view :class="['layout-content', widget.configuration.uniConfiguration.layout]">
          <view class="row" v-for="(item, i) in widget.configuration.items" :key="'row_' + i">
            <template v-if="!hiddenItems.includes(item.id)">
              <view :class="['label-col']">
                <text v-if="requireItemChildren[item.id].length > 0" class="is-required">*</text>
                {{ item.configuration.label }}</view
              >
              <view
                class="content-col"
                :style="{ width: widget.configuration.uniConfiguration.layout !== 'vertical' ? '0' : '' }"
              >
                <w-widget
                  v-for="(wgt, w) in item.configuration.widgets"
                  :widget="wgt"
                  :parent="item"
                  :key="'formItem_' + wgt.id"
                />
              </view>
            </template>
          </view>
        </view>
      </template>
    </uni-w-section>
  </view>
</template>

<script>
import mixin from "../w-dyform/form-element.mixin.js";
// import wDyformWidget from "../w-dyform-widget/w-dyform-widget.vue";
import "./index.scss";
import { storage, appContext } from "wellapp-uni-framework";
import { debounce } from "lodash";

export default {
  mixins: [mixin],
  name: "w-form-layout",
  props: {},
  components: { wWidget: () => import("../w-widget/w-widget.vue") },
  data() {
    let requireItemChildren = {};
    for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
      requireItemChildren[this.widget.configuration.items[i].id] = [];
    }
    return {
      hiddenWidgets: new Set(),
      hiddenItems: [],
      requireItemChildren,
    };
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

    this.proxySubFieldEvent = debounce(this.proxySubFieldEvent.bind(this), 200);
  },
  beforeMount() {},
  mounted() {},
  methods: {
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
      for (let i = 0, len = this.widget.configuration.items.length; i < len; i++) {
        // FIXME: 通过栅格进行布局的字段需要处理
        let item = this.widget.configuration.items[i];
        // console.log(`组件通过单元格代理交互事件 ${this.widget.title} ${item.id}`);
        if (this.hiddenItems.includes(item.id)) {
          /**
           * 当单元格内下只有一个字段组件，且字段组件是隐藏情况下，则需要代理该字段组件的显示、编辑等交互事件
           * 因为该单元格时不会渲染挂载，其下的字段组件也更不可能挂载显示，所以字段的相关交互事件就得在此处进行代理
           */
          let subWidgets = item.configuration.widgets;
          subWidgets.forEach((wgt) => {
            if (wgt.configuration.isDatabaseField) {
              this.dyform.offEvent(`${wgt.id}:setVisible`).handleEvent(`${wgt.id}:setVisible`, (visible) => {
                // console.log(`单元格代理组件触发变更事件 : ${wgt.configuration.name}`, visible);
                if (typeof visible !== "boolean" && visible.eventParams != undefined) {
                  // 由事件传递进来的参数
                  visible = visible.eventParams.visible !== "false";
                }
                if (visible) {
                  item.configuration.hidden = false;
                  if (_this.hiddenItems.includes(item.id)) {
                    _this.hiddenItems.splice(_this.hiddenItems.indexOf(item.id), 1);
                  }
                }
                _this.dyform.setFieldVisible(wgt.configuration.code, visible);

                // 通过事件触发的显隐，要删除对应的规则，否则组件初始化还是以规则为主
                if (_this.form.formElementRules && _this.form.formElementRules[wgt.id]) {
                  delete _this.form.formElementRules[wgt.id].hidden;
                }
              });

              this.dyform.offEvent(`${wgt.id}:setEditable`).handleEvent(`${wgt.id}:setEditable`, (editable) => {
                // console.log(`单元格代理组件触发变更事件 : ${wgt.configuration.name}`, editable);
                if (typeof editable !== "boolean" && editable.eventParams != undefined) {
                  // 由事件传递进来的参数
                  editable = editable.eventParams.editable !== "false";
                }
                wgt.configuration.defaultDisplayState = editable ? "edit" : "unedit";
                item.configuration.hidden = false;
                if (!item.configuration.hidden && _this.hiddenItems.includes(item.id)) {
                  _this.hiddenItems.splice(_this.hiddenItems.indexOf(item.id), 1);
                }
                _this.dyform.setFieldEditable(wgt.configuration.code, editable);

                // 通过事件触发的编辑，要删除对应的规则，否则组件初始化还是以规则为主
                if (_this.form.formElementRules && _this.form.formElementRules[wgt.id]) {
                  delete _this.form.formElementRules[wgt.id].editable;
                  delete _this.form.formElementRules[wgt.id].hidden;
                }
              });
            }
          });
        }
      }
    },
  },
};
</script>
