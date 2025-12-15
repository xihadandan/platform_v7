<template>
  <view class="uni-w-button-group-container">
    <uni-row class="uni-w-button-group" :class="{ 'uni-w-button-group__noSpace': !gutter }" :gutter="gutter">
      <uni-col v-for="button in showbuttons" :span="button.span || span" class="uni-w-button-group__item">
        <uni-w-button
          :class="{ 'w-button-arrow': button.children && button.children.length && (button.arrow || arrow) }"
          :type="button[replaceFields.type] || type"
          :size="button[replaceFields.size] || size"
          :icon="button[replaceFields.icon]"
          :status="button[replaceFields.status]"
          :block="button.block === undefined ? true : button.block"
          :newline="button.newline"
          @click="(e) => onClick(e, button)"
        >
          {{ button.textHidden ? "" : button[replaceFields.title] }}
          <w-icon
            slot="suffix"
            :icon="button.suffixIcon"
            v-if="button.suffixIcon"
            iconStyle="color: var(--w-button-font-color); font-size: var(--w-button-icon-size)"
          />
        </uni-w-button>
      </uni-col>
    </uni-row>
    <!-- <w-button-action-sheet :button="sheetActions" ref="buttonSheet" @selectClick="sheetActionClick" /> -->
  </view>
</template>
<script>
import { mapMutations } from "vuex";
export default {
  name: "uni-w-button-group",
  mixins: [],
  props: {
    buttons: Array,
    gutter: {
      type: Number,
      default: 0,
    },
    block: Boolean,
    moreButton: {
      type: Object,
      default: () => {
        return {
          title: "更多",
          type: "primary",
        };
      },
    },
    max: {
      //按钮个数大于这个值，其余用actionSheet打开更多, 0时不显示更多 ;2，共显示两个按钮，后面一个是更多
      type: Number,
      default: 0,
    },
    arrow: Boolean,
    size: {
      type: String,
      default: "default",
    },
    type: {
      type: String,
      default: "default",
    },
    replaceFields: {
      type: Object,
      default: () => {
        return {
          type: "type",
          size: "size",
          icon: "icon",
          status: "status",
          title: "title",
        };
      },
    },
  },
  components: {},
  computed: {
    showbuttons() {
      let buttons = [];
      if (this.buttons.length) {
        if (this.max == 0) {
          return this.buttons;
        } else if (this.max <= -1) {
          let size = this.buttons[0].size;
          this.moreButton.size = size;
          this.moreButton.children = [];
          for (var i = 0; i < this.buttons.length; i++) {
            let action = this.buttons[i];
            this.moreButton.children.push(action);
            if (action.children && action.children.length) {
              this.moreButton.children.push(...action.children);
            }
          }
          buttons.push(this.moreButton);
        } else if (this.buttons.length <= this.max) {
          return this.buttons;
        } else {
          let size = this.buttons[0].size;
          for (var i = 0; i < this.buttons.length; i++) {
            let action = this.buttons[i];
            if (i < this.max - 1) {
              buttons.push(action);
            } else if (i == this.max - 1) {
              this.moreButton.size = size;
              this.moreButton.children = [];
              this.moreButton.children.push(action);
              if (action.children && action.children.length) {
                this.moreButton.children.push(...action.children);
              }
            } else {
              this.moreButton.children.push(action);
              if (action.children && action.children.length) {
                this.moreButton.children.push(...action.children);
              }
            }
          }
          buttons.push(this.moreButton);
        }
      }
      return buttons;
    },
    span() {
      return 24 / this.showbuttons.length;
    },
  },
  data() {
    this.moreButton.title = this.$t("global.more", "更多");
    return {
      sheetActions: [],
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    ...mapMutations(["setGlobalActionSheet"]),
    onClick(e, button) {
      let _self = this;
      if (button.children && button.children.length) {
        this.sheetActions = button.children.map((item) => {
          item.title = item[this.replaceFields.title];
          return item;
        });
        // #ifdef H5
        //使用原生
        // uni.showActionSheet({
        //   itemList: _self.sheetActions.map((item) => item.title),
        //   itemColor: "var(--w-primary-color)",
        //   class: "w-button-action-sheet",
        //   style: this.theme,
        //   cancelText: this.$t("global.cancel", "取消"),
        //   success: function (res) {
        //     console.log(res);
        //     button = _self.sheetActions[res.tapIndex];
        //     _self.$emit("click", { button }, button);
        //   },
        //   fail: function (res) {
        //     console.log(res.errMsg);
        //   },
        // });
        // #endif
        // #ifndef H5
        // this.$refs.buttonSheet.show = true;
        // #endif

        uni.$emit("ptActionSheetShow", {
          actions: this.sheetActions,
          click: this.sheetActionClick,
          cancelText: this.$t("global.cancel", "取消"),
        });
      } else {
        this.$emit("click", e, button);
      }
    },
    sheetActionClick(e) {
      this.$emit("click", e, e.button);
    },
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
  },
};
</script>
<style lang="scss" scoped>
// .uni-w-button-group-container {
//   width: 100%;
// }
.uni-w-button-group {
  --uni-w-button-group-padding-top: 0;
  --uni-w-button-group-padding-bottom: 0;
  .uni-w-button-group__item {
    padding-top: var(--uni-w-button-group-padding-top);
    padding-bottom: var(--uni-w-button-group-padding-bottom);
  }
  &.uni-w-button-group__noSpace {
    .uni-w-button-group__item {
      .w-button {
        --w-button-border-radius: 0;
        position: relative;
        &::after {
          content: "";
          position: absolute;
          right: 0;
          height: 100%;
          width: var(--w-button-groups-line-width);
          background-color: var(--w-button-groups-line-color);
        }
      }
      &:first-child {
        .w-button {
          border-top-left-radius: 4px;
          border-bottom-left-radius: 4px;
        }
      }
      &:last-child {
        .w-button {
          border-top-right-radius: 4px;
          border-bottom-right-radius: 4px;
          &::after {
            content: none;
          }
        }
      }
    }
  }
}
</style>
