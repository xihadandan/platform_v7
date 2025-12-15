<template>
  <view class="w-button-container">
    <view
      v-for="(item, index) in buttonAlones"
      :key="index"
      @click="($evt) => onClick($evt, item, index)"
      :class="{ _button: true, 'is-active': activeIndex === index }"
    >
      {{ item.buttonName }}
    </view>

    <template v-if="collapseButtons.length > 0">
      <view class="_button" @click="handleMore"> 更多</view>
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
      default: 3,
    },
  },
  data() {
    return {
      collapseButtons: [],
      activeIndex: null,
    };
  },
  computed: {
    buttonAlones() {
      let buttonAlones = this.buttons;
      if (this.collapseNumber != undefined && this.buttons.length > this.collapseNumber) {
        //超过三个，以按钮组展示
        buttonAlones = this.buttons.slice(0, this.collapseNumber);
        this.collapseButtons = this.buttons.slice(this.collapseNumber);
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
        evt,
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
  },
};
</script>

<style lang="scss" scoped>
.w-button-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
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
