<template>
  <view v-show="show" class="uni-w-color-picker__wrapper" @touchmove.stop.prevent="moveHandle">
    <view class="uni-w-color-picker__mask active" :class="{ active: active }" @click.stop="close"></view>
    <view class="uni-w-color-picker__box" :class="{ active: active }">
      <view class="uni-w-color-picker__header">
        <view class="uni-w-color-picker__header-button" @click.stop="close">{{ $t("global.cancel", "取消") }}</view>
        <view class="uni-w-color-picker__header-button">{{ hex }}</view>
        <view class="uni-w-color-picker__header-button" style="color: var(--w-primary-color)" @click.stop="confirm">{{
          $t("global.confirm", "确定")
        }}</view>
      </view>
      <view v-show="['Sketch', 'Chrome'].indexOf(type) > -1">
        <!-- 颜色选择区块 -->
        <view class="uni-w-color-picker__color__box" :style="colorBoxStyle">
          <view
            class="uni-w-color-picker__background boxs"
            @touchstart="touchstart($event, 0)"
            @touchmove="touchmove($event, 0)"
            @touchend="touchend($event, 0)"
          >
            <view class="uni-w-color-picker__color-mask"></view>
            <view class="uni-w-color-picker__pointer" :style="colorBoxPointerStyle"></view>
          </view>
        </view>
        <!-- 颜色选择区块 -->
        <view class="uni-w-color-picker__control__box">
          <!-- 选中颜色显示 -->
          <view class="uni-w-color-picker__control__color">
            <view class="uni-w-color-picker__control__color-content" :style="controlBoxStyle"></view>
          </view>
          <view class="uni-w-color-picker__control-box__item">
            <!-- 色值选择 -->
            <view
              class="uni-w-color-picker__controller boxs"
              @touchstart="touchstart($event, 1)"
              @touchmove="touchmove($event, 1)"
              @touchend="touchend($event, 1)"
            >
              <view class="uni-w-color-picker__hue">
                <view class="uni-w-color-picker__circle" :style="controlBoxHueStyle"></view>
              </view>
            </view>
            <!-- 透明度选择 -->
            <view
              class="uni-w-color-picker__controller boxs"
              @touchstart="touchstart($event, 2)"
              @touchmove="touchmove($event, 2)"
              @touchend="touchend($event, 2)"
            >
              <view class="uni-w-color-picker__transparency">
                <view class="uni-w-color-picker__circle" :style="controlBoxTransparencyStyle"></view>
              </view>
            </view>
          </view>
        </view>
        <!-- 手动设置颜色值 -->
        <view class="flex f_x_s uni-w-color-picker__color-input">
          <view class="uni-w-color-picker__color-input-item uni-w-color-picker__color-input-hex">
            Hex<u-input v-model="inputHex" @change="selectColor(inputHex)"></u-input>
          </view>
          <view class="uni-w-color-picker__color-input-item">
            R<u-input v-model="inputRgba.r" @change="selectColor(inputRgba)"></u-input>
          </view>
          <view class="uni-w-color-picker__color-input-item">
            G<u-input v-model="inputRgba.g" @change="selectColor(inputRgba)"></u-input>
          </view>
          <view class="uni-w-color-picker__color-input-item">
            B<u-input v-model="inputRgba.b" @change="selectColor(inputRgba)"></u-input>
          </view>
          <view class="uni-w-color-picker__color-input-item">
            A<u-input v-model="inputRgba.a" @change="selectColor(inputRgba)"></u-input>
          </view>
        </view>
      </view>
      <!-- 选中预选颜色 -->
      <view v-show="['Sketch', 'Twitter'].indexOf(type) > -1" class="uni-w-color-picker__alternative">
        <view class="uni-w-color-picker__alternative__item" v-for="(item, index) in colorList" :key="index">
          <view
            class="uni-w-color-picker__alternative__item-content"
            :style="{ background: item }"
            @click="selectColor(item)"
          >
          </view>
        </view>
      </view>
      <view v-if="type == 'Twitter'">
        <view class="uni-w-color-picker__twitter-input">
          <uni-w-easyinput
            v-model="inputHex"
            @change="selectColor(inputHex)"
            :style="twitterInputStyle"
            :clearable="false"
          >
            <view class="addonBefore" slot="addonBefore">Hex</view>
          </uni-w-easyinput>
        </view>
      </view>
      <slot name="bottom"></slot>
    </view>
  </view>
</template>

<script>
export default {
  emits: ["update:modelValue", "change", "confirm"],
  props: {
    value: {
      type: Boolean,
      default: false,
    },
    modelValue: {
      type: Boolean,
      default: false,
    },
    color: {
      type: Object,
      default: null,
    },
    hexcolor: {
      type: String,
      default: "",
    },
    spareColor: {
      type: Array,
      default() {
        return [];
      },
    },
    type: {
      type: String,
      default: "Sketch", //Twitter:预设颜色,Chrome:色盘,Sketch:组合面板
    },
  },
  computed: {
    valueCom() {
      // #ifndef VUE3
      return this.value;
      // #endif

      // #ifdef VUE3
      return this.modelValue;
      // #endif
    },
    // 判断是否深色
    isDarkColor() {
      let rgba = this.hexToRgba(this.inputHex);
      let yiq = 0;
      if (rgba) {
        // 应用转换公式计算亮度
        yiq = (rgba.r * 299 + rgba.g * 587 + rgba.b * 114) / 1000;
      }
      return yiq <= 120;
    },
    colorBoxStyle() {
      return { background: "rgb(" + this.bgcolor.r + "," + this.bgcolor.g + "," + this.bgcolor.b + ")" };
    },
    colorBoxPointerStyle() {
      return { top: this.site[0].top - 8 + "px", left: this.site[0].left - 8 + "px" };
    },
    controlBoxStyle() {
      return { background: "rgba(" + this.rgba.r + "," + this.rgba.g + "," + this.rgba.b + "," + this.rgba.a + ")" };
    },
    controlBoxHueStyle() {
      return { left: this.site[1].left - 12 + "px" };
    },
    controlBoxTransparencyStyle() {
      return { left: this.site[2].left - 12 + "px" };
    },
    twitterInputStyle() {
      return {
        width: "200px",
        "--twitter-input-addonBefore-color": this.isDarkColor ? "#ffffff" : "#000000",
        "--twitter-input-addonBefore-bg-color": this.inputHex,
      };
    },
  },
  mounted() {
    // 组件渲染完成时，检查value是否为true，如果是，弹出popup
    if (this.valueCom) {
      this.open();
    }
  },
  data() {
    let rgba = {
      r: 0,
      g: 0,
      b: 0,
      a: 1,
    };
    let hsb = {
      h: 0,
      s: 0,
      b: 0,
    };
    let hex = "#000000";
    return {
      show: false,
      active: false,
      // rgba 颜色
      rgba,
      // hsb 颜色
      hsb,
      site: [
        {
          top: 0,
          left: 0,
        },
        {
          left: 0,
        },
        {
          left: 0,
        },
      ],
      index: 0,
      bgcolor: {
        r: 255,
        g: 0,
        b: 0,
        a: 1,
      },
      hex,
      inputHex: hex,
      inputRgba: rgba,
      mode: true,
      colorList: [
        "#1677FF",
        "#8ABBFF",
        "#74C042",
        "#BADFA0",
        "#FA8C16",
        "#FDC58A",
        "#E60012",
        "#F38088",
        "#BE41D8",
        "#ABB8C3",
      ],
    };
  },
  created() {
    if (this.spareColor.length !== 0) {
      this.colorList = this.spareColor;
    }
  },
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    /**
     * 初始化
     */
    init() {
      // hsb 颜色
      if (this.color) {
        this.rgba = this.color;
        this.inputRgba = this.color;
        this.hsb = this.rgbToHex(this.rgba);
      } else if (this.hexcolor) {
        let hsb = this.hexcolor; //.replace('#','');
        let rgba = this.hexToRgba(hsb);
        this.rgba = rgba;
        this.hsb = hsb;
      }
      // this.setColor();
      this.setValue(this.rgba);
    },
    moveHandle() {},
    open() {
      this.show = true;
      this.$nextTick(() => {
        this.init();
        setTimeout(() => {
          this.active = true;
          setTimeout(() => {
            this.getSelectorQuery();
          }, 400);
        }, 5);
      });
    },
    close() {
      this.active = false;
      this.$nextTick(() => {
        setTimeout(() => {
          this.show = false;

          // #ifndef VUE3
          this.$emit("input", false);
          // #endif

          // #ifdef VUE3
          this.$emit("update:modelValue", false);
          // #endif
        }, 300);
      });
    },
    confirm() {
      this.close();
      this.$emit("confirm", {
        rgba: this.rgba,
        hex: this.hex,
      });
    },
    // 选择模式
    select() {
      this.mode = !this.mode;
    },
    isEffectiveValue(item) {
      if (typeof item == "string") {
        if (item.startsWith("#") && item.length == 7) {
          item = this.hexToRgba(item);
        } else {
          item = null;
        }
      }
      return item;
    },
    // 常用颜色选择
    selectColor(item) {
      let _item = this.isEffectiveValue(item);
      if (_item) {
        this.setColorBySelect(_item);
      }
    },
    //触摸开始事件
    touchstart(e, index) {
      const { pageX, clientY } = e.touches[0];
      this.pageX = pageX;
      this.pageY = clientY; //PageY 会取到页面的高度，和当前弹出框取色值的位置不一致
      this.setPosition(pageX, clientY, index);
    },
    //手指滑动过程
    touchmove(e, index) {
      const { pageX, clientY } = e.touches[0];
      this.moveX = pageX;
      this.moveY = clientY;
      this.setPosition(pageX, clientY, index);
    },
    //触摸结束事件
    touchend(e, index) {},
    /**
     * 设置位置
     */
    setPosition(x, y, index) {
      this.index = index;
      const { top, left, width, height } = this.position[index];
      // 设置最大最小值

      console.log(x, y, index);
      console.log(top, left, width, height);
      this.site[index].left = Math.max(0, Math.min(parseInt(x - left), width));
      if (index === 0) {
        this.site[index].top = Math.max(0, Math.min(parseInt(y - top), height));

        // 设置颜色
        this.hsb.s = parseInt((100 * this.site[index].left) / width);
        this.hsb.b = parseInt(100 - (100 * this.site[index].top) / height);
        this.setColor();
        this.setValue(this.rgba);
      } else {
        this.setControl(index, this.site[index].left);
      }
    },
    /**
     * 设置 rgb 颜色
     */
    setColor() {
      const rgb = this.HSBToRGB(this.hsb);
      this.rgba.r = rgb.r;
      this.rgba.g = rgb.g;
      this.rgba.b = rgb.b;
      this.inputRgba = this.rgba;
    },
    /**
     * 设置二进制颜色
     * @param {Object} rgb
     */
    setValue(rgb) {
      this.hex = "#" + this.rgbToHex(rgb);
      this.inputHex = this.hex;
    },
    //设置透明度
    setControl(index, x) {
      const { top, left, width, height } = this.position[index];

      if (index === 1) {
        this.hsb.h = parseInt((360 * x) / width);
        this.bgcolor = this.HSBToRGB({
          h: this.hsb.h,
          s: 100,
          b: 100,
        });
        this.setColor();
      } else {
        this.rgba.a = (x / width).toFixed(1);
      }
      this.setValue(this.rgba);
    },
    /**
     * @param {Object} hex hex转RGB
     */
    hexToRgba(hex) {
      // Expand shorthand form (e.g. "03F") to full form (e.g. "0033FF")
      let shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
      hex = hex.replace(shorthandRegex, function (m, r, g, b) {
        return r + r + g + g + b + b;
      });
      let result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})?$/i.exec(hex);
      return result
        ? {
            r: parseInt(result[1], 16),
            g: parseInt(result[2], 16),
            b: parseInt(result[3], 16),
            a: result[4] ? parseInt(result[4], 16) / 255 : 1,
          }
        : null;
    },
    /**
     * rgb 转 二进制 hex
     * @param {Object} rgb
     */
    rgbToHex(rgb) {
      let hex = [rgb.r.toString(16), rgb.g.toString(16), rgb.b.toString(16)];
      hex.map(function (str, i) {
        if (str.length == 1) {
          hex[i] = "0" + str;
        }
      });
      return hex.join("");
    },
    setColorBySelect(getrgb) {
      const { r, g, b, a } = getrgb;
      let rgb = {};
      rgb = {
        r: r ? parseInt(r) : 0,
        g: g ? parseInt(g) : 0,
        b: b ? parseInt(b) : 0,
        a: a ? a : 0,
      };
      this.rgba = rgb;
      this.hsb = this.rgbToHsb(rgb);
      this.changeViewByHsb();
    },
    changeViewByHsb() {
      // console.log("changeViewByHsb");
      const [a, b, c] = this.position;
      this.site[0].left = parseInt((this.hsb.s * a.width) / 100);
      this.site[0].top = parseInt(((100 - this.hsb.b) * a.height) / 100);
      this.setColor(this.hsb.h);
      this.setValue(this.rgba);
      this.bgcolor = this.HSBToRGB({
        h: this.hsb.h,
        s: 100,
        b: 100,
      });

      this.site[1].left = (this.hsb.h / 360) * b.width;
      this.site[2].left = this.rgba.a * c.width;
    },
    /**
     * hsb 转 rgb
     * @param {Object} 颜色模式  H(hues)表示色相，S(saturation)表示饱和度，B（brightness）表示亮度
     */
    HSBToRGB(hsb) {
      let rgb = {};
      let h = Math.round(hsb.h);
      let s = Math.round((hsb.s * 255) / 100);
      let v = Math.round((hsb.b * 255) / 100);
      if (s == 0) {
        rgb.r = rgb.g = rgb.b = v;
      } else {
        let t1 = v;
        let t2 = ((255 - s) * v) / 255;
        let t3 = ((t1 - t2) * (h % 60)) / 60;
        if (h == 360) h = 0;
        if (h < 60) {
          rgb.r = t1;
          rgb.b = t2;
          rgb.g = t2 + t3;
        } else if (h < 120) {
          rgb.g = t1;
          rgb.b = t2;
          rgb.r = t1 - t3;
        } else if (h < 180) {
          rgb.g = t1;
          rgb.r = t2;
          rgb.b = t2 + t3;
        } else if (h < 240) {
          rgb.b = t1;
          rgb.r = t2;
          rgb.g = t1 - t3;
        } else if (h < 300) {
          rgb.b = t1;
          rgb.g = t2;
          rgb.r = t2 + t3;
        } else if (h < 360) {
          rgb.r = t1;
          rgb.g = t2;
          rgb.b = t1 - t3;
        } else {
          rgb.r = 0;
          rgb.g = 0;
          rgb.b = 0;
        }
      }
      return {
        r: Math.round(rgb.r),
        g: Math.round(rgb.g),
        b: Math.round(rgb.b),
      };
    },
    rgbToHsb(rgb) {
      let hsb = {
        h: 0,
        s: 0,
        b: 0,
      };
      let min = Math.min(rgb.r, rgb.g, rgb.b);
      let max = Math.max(rgb.r, rgb.g, rgb.b);
      let delta = max - min;
      hsb.b = max;
      hsb.s = max != 0 ? (255 * delta) / max : 0;
      if (hsb.s != 0) {
        if (rgb.r == max) hsb.h = (rgb.g - rgb.b) / delta;
        else if (rgb.g == max) hsb.h = 2 + (rgb.b - rgb.r) / delta;
        else hsb.h = 4 + (rgb.r - rgb.g) / delta;
      } else hsb.h = -1;
      hsb.h *= 60;
      if (hsb.h < 0) hsb.h = 0;
      hsb.s *= 100 / 255;
      hsb.b *= 100 / 255;
      return hsb;
    },
    getSelectorQuery() {
      const views = uni.createSelectorQuery().in(this);
      views
        .selectAll(".boxs")
        .boundingClientRect((data) => {
          if (!data || data.length === 0) {
            setTimeout(() => this.getSelectorQuery(), 20);
            return;
          }
          this.position = data;
          this.setColorBySelect(this.rgba);
        })
        .exec();
    },
  },
  watch: {
    valueCom(val) {
      if (val) {
        this.open();
      } else {
        this.close();
      }
    },
    spareColor(newVal) {
      this.colorList = newVal;
    },
    type(v) {
      if (["Sketch", "Twitter"].indexOf(v) > -1) {
        setTimeout(() => this.getSelectorQuery(), 200);
      }
    },
  },
};
</script>

<style scoped lang="scss">
.uni-w-color-picker__wrapper {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  width: 100%;
  box-sizing: border-box;
  z-index: 999999;
}

.uni-w-color-picker__box {
  width: 100%;
  position: absolute;
  bottom: 0;
  padding: 30upx 0;
  padding-top: 0;
  background: #fff;
  transition: all 0.3s;
  transform: translateY(100%);
  border-radius: 8px 8px 0 0;
}

.uni-w-color-picker__box.active {
  transform: translateY(0%);
}

.uni-w-color-picker__header {
  display: flex;
  justify-content: space-between;
  width: 100%;
  height: 100upx;
  border-bottom: 1px #eee solid;
  box-shadow: 1px 0 2px rgba(0, 0, 0, 0.1);
  background: #fff;
  border-radius: 8px 8px 0 0;
}

.uni-w-color-picker__header-button {
  display: flex;
  align-items: center;
  width: 150upx;
  height: 100upx;
  font-size: 30upx;
  color: #666;
  padding-left: 20upx;
}

.uni-w-color-picker__header-button:last-child {
  justify-content: flex-end;
  padding-right: 20upx;
}

.uni-w-color-picker__mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: -1;
  transition: all 0.3s;
  opacity: 0;
}

.uni-w-color-picker__mask.active {
  opacity: 1;
}

.uni-w-color-picker__color__box {
  position: relative;
  height: 400upx;
  background: rgb(255, 0, 0);
  overflow: hidden;
  box-sizing: border-box;
  margin: 0 20upx;
  margin-top: 20upx;
  box-sizing: border-box;
  border-radius: 4px;
}

.uni-w-color-picker__background {
  position: absolute;
  z-index: 16777271;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to right, #fff, rgba(255, 255, 255, 0));
}

.uni-w-color-picker__color-mask {
  position: absolute;
  z-index: 16777271;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 400upx;
  background: linear-gradient(to top, #000, rgba(0, 0, 0, 0));
}

.uni-w-color-picker__pointer {
  position: absolute;
  bottom: -8px;
  left: -8px;
  z-index: 2;
  width: 15px;
  height: 15px;
  border: 1px #eee solid;
  border-radius: 50%;
}

.uni-w-color-picker__show-color {
  width: 100upx;
  height: 50upx;
}

.uni-w-color-picker__control__box {
  margin-top: 50upx;
  width: 100%;
  display: flex;
  padding-left: 20upx;
  box-sizing: border-box;
}

.uni-w-color-picker__control__color {
  flex-shrink: 0;
  width: 100upx;
  height: 100upx;
  border-radius: 8px;
  background-color: #fff;
  background-image: linear-gradient(45deg, #eee 25%, transparent 25%, transparent 75%, #eee 75%, #eee),
    linear-gradient(45deg, #eee 25%, transparent 25%, transparent 75%, #eee 75%, #eee);
  background-size: 36upx 36upx;
  background-position: 0 0, 18upx 18upx;
  border: 1px #eee solid;
  overflow: hidden;
}

.uni-w-color-picker__control__color-content {
  width: 100%;
  height: 100%;
}

.uni-w-color-picker__control-box__item {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 100%;
  padding: 0 30upx;
}

.uni-w-color-picker__controller {
  position: relative;
  z-index: 16777271;
  width: 100%;
  height: 16px;
  background-color: #fff;
  background-image: linear-gradient(45deg, #eee 25%, transparent 25%, transparent 75%, #eee 75%, #eee),
    linear-gradient(45deg, #eee 25%, transparent 25%, transparent 75%, #eee 75%, #eee);
  background-size: 32upx 32upx;
  background-position: 0 0, 16upx 16upx;
}

.uni-w-color-picker__hue {
  width: 100%;
  height: 100%;
  border-radius: 4px;
  background: linear-gradient(to right, #f00 0%, #ff0 17%, #0f0 33%, #0ff 50%, #00f 67%, #f0f 83%, #f00 100%);
}

.uni-w-color-picker__transparency {
  width: 100%;
  height: 100%;
  border-radius: 4px;
  background: linear-gradient(to right, rgba(0, 0, 0, 0) 0%, rgb(0, 0, 0));
}

.uni-w-color-picker__circle {
  position: absolute;
  /* right: -10px; */
  top: -2px;
  width: 20px;
  height: 20px;
  box-sizing: border-box;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 0 2px 1px rgba(0, 0, 0, 0.1);
}

.uni-w-color-picker__result__box {
  margin-top: 20upx;
  padding: 10upx;
  width: 100%;
  display: flex;
  box-sizing: border-box;
}

.uni-w-color-picker__result__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10upx;
  width: 100%;
  box-sizing: border-box;
}

.uni-w-color-picker__result__box-input {
  padding: 10upx 0;
  width: 100%;
  font-size: 28upx;
  box-shadow: 0 0 1px 1px rgba(0, 0, 0, 0.1);
  color: #999;
  text-align: center;
  background: #fff;
}

.uni-w-color-picker__result__box-text {
  margin-top: 10upx;
  font-size: 28upx;
  line-height: 2;
}

.uni-w-color-picker__select {
  flex-shrink: 0;
  width: 150upx;
  padding: 0 30upx;
}

.uni-w-color-picker__select .uni-w-color-picker__result__box-input {
  border-radius: 10upx;
  border: none;
  color: #999;
  box-shadow: 1px 1px 2px 1px rgba(0, 0, 0, 0.1);
  background: #fff;
}

.uni-w-color-picker__select .uni-w-color-picker__result__box-input:active {
  box-shadow: 0px 0px 1px 0px rgba(0, 0, 0, 0.1);
}

.uni-w-color-picker__alternative {
  display: flex;
  flex-wrap: wrap;
  /* justify-content: space-between; */
  width: 100%;
  padding: 20upx 10upx;
  box-sizing: border-box;
}

.uni-w-color-picker__alternative__item {
  margin-left: 20upx;
  margin-top: 10upx;
  width: 50upx;
  height: 50upx;
  border-radius: 10upx;
  background-color: #fff;
  background-image: linear-gradient(45deg, #eee 25%, transparent 25%, transparent 75%, #eee 75%, #eee),
    linear-gradient(45deg, #eee 25%, transparent 25%, transparent 75%, #eee 75%, #eee);
  background-size: 36upx 36upx;
  background-position: 0 0, 18upx 18upx;
  border: 1px #eee solid;
  overflow: hidden;
  &:first-child {
    margin-left: 0upx;
  }
}

.uni-w-color-picker__alternative__item-content {
  width: 50upx;
  height: 50upx;
  background: rgba(255, 0, 0, 0.5);
}

.uni-w-color-picker__alternative__item:active {
  transition: all 0.3s;
  transform: scale(1.1);
}

.uni-w-color-picker__color-input {
  margin: 0 10upx;
  margin-top: 20upx;
  .uni-w-color-picker__color-input-item {
    text-align: center;
    width: 100upx;
    padding: 0 10upx;
  }
  .uni-w-color-picker__color-input-hex {
    width: 200upx;
  }
}
.uni-w-color-picker__twitter-input {
  padding: 20upx 10upx;
  --twitter-input-addonBefore-bg-color: #e8e8e8;
  --twitter-input-addonBefore-color: #ffffff;
  .addonBefore {
    width: 50px;
    text-align: center;
    height: 36px;
    line-height: 36px;
    background: var(--twitter-input-addonBefore-bg-color);
    border-radius: 4px 0 0 4px;
    color: var(--twitter-input-addonBefore-color);
  }
}
</style>
