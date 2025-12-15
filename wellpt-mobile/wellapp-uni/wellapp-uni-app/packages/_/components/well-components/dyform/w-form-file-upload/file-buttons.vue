<template>
  <view class="w-button-container" @click.stop>
    <uni-w-button
      v-for="(item, index) in buttonAlones"
      icon="item.icon"
      size="small"
      type="link"
      :text="item.buttonName"
      @click="buttonClick($event, item)"
    ></uni-w-button>
    <template v-if="collapseButtons.length > 0">
      <uni-w-button-group
        :buttons="collapseButtons"
        size="small"
        type="link"
        @click="buttonClick"
        :max="1"
        :moreButton="{
          title: '',
          icon: 'iconfont icon-ptkj-gengduocaozuo',
          type: 'link',
        }"
        :replaceFields="{
          type: 'type',
          size: 'size',
          icon: 'icon',
          status: 'status',
          title: 'buttonName',
        }"
      ></uni-w-button-group>
    </template>
  </view>
</template>
<script>
export default {
  name: "FileButtons",
  props: {
    buttons: {
      type: Array,
    },
    file: {
      type: Object,
    },
    fileIndex: {
      type: Number,
    },
    collapseNumber: {
      type: Number,
      default: 2,
    },
    buttonPredicate: Function,
    parentWidget: Object | Function,
    i18nPrefix: {
      type: String,
      default: "WidgetFormFileUpload.button.",
    },
  },
  data() {
    let parentVm = this.parentWidget != undefined ? this.parentWidget : undefined;
    if (typeof this.parentWidget === "function") {
      parentVm = this.parentWidget();
    }
    return {
      collapseButtons: [],
      activeIndex: null,
      parentVm,
    };
  },
  computed: {
    buttonAlones() {
      let buttonAlones = [];
      let allbuttons = [];
      for (let i = 0, len = this.buttons.length; i < len; i++) {
        let btn = this.buttons[i];
        btn.buttonName = this.$t(btn.id, btn.buttonName, btn);
        if (typeof this.buttonPredicate == "function") {
          let isTrue = this.buttonPredicate(btn);
          if (isTrue) {
            buttonAlones.push(btn);
            allbuttons.push(btn);
          }
        }
      }
      if (this.collapseNumber != undefined && buttonAlones.length > this.collapseNumber) {
        //超过三个，以按钮组展示
        buttonAlones = allbuttons.slice(0, this.collapseNumber);
        this.collapseButtons = allbuttons.slice(this.collapseNumber);
      }
      buttonAlones = buttonAlones.filter((item) => item.buttonName);
      return buttonAlones;
    },
  },
  methods: {
    onClick(event, button, index) {
      this.activeIndex = index;
      this.$emit("click", {
        button,
        file: this.file,
        fileIndex: this.fileIndex,
        event,
      });
    },
    handleMore() {
      uni.showActionSheet({
        itemList: this.collapseButtons.map((item) => {
          if (item.defaultFlag) {
            return item.buttonName;
          }
        }),
        success: (args) => {
          const tapIndex = args.tapIndex;
          this.$emit("click", {
            tapIndex,
            button: this.collapseButtons[tapIndex],
            file: this.file,
            fileIndex: this.fileIndex,
          });
        },
      });
    },
    buttonClick(e, button) {
      this.$emit("click", {
        button: button,
        file: this.file,
        fileIndex: this.fileIndex,
      });
    },
    $t() {
      if (arguments[2] && this.i18nPrefix) {
        let btn = arguments[2];
        if (btn.btnType == "1") {
          // 默认按钮使用默认配置
          return this.$t(this.i18nPrefix + btn.id, btn.buttonName);
        }
      }
      if (this.parentVm != undefined) {
        return this.parentVm.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
  },
};
</script>

<style lang="scss" scoped>
.w-button-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  align-items: center;
  ._button {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin-right: 10px;
    margin-bottom: 10px;
    padding: 5px 10px;
    border: 1px #dcdfe6 solid;
    border-radius: 3px;
    transition: border-color 0.2s;
    font-size: 14px;
    color: #666;
    cursor: pointer;

    &.is-active {
      border-color: #2979ff;
      color: #2979ff;
    }
  }
}
</style>
