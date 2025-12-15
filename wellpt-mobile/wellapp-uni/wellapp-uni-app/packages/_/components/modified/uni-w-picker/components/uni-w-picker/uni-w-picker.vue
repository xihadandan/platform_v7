<template>
  <view>
    <view @tap="onTrigger">
      <slot></slot>
    </view>
    <uni-popup
      ref="popup"
      type="bottom"
      :borderRadius="borderRadius"
      :safe-area="safeAreaInsetBottom"
      backgroundColor="#ffffff"
      class="uni-w-picker"
      @change="popupChange"
    >
      <view class="u-datetime-picker">
        <view class="u-picker-header" @touchmove.stop.prevent="">
          <view class="left">
            <slot name="header-left">
              <view
                v-if="!closeIcon"
                class="u-btn-picker u-btn-picker--tips"
                :style="{ color: cancelColor }"
                hover-class="u-opacity"
                :hover-stay-time="150"
                @tap="getResult('cancel')"
                >{{ cancelText }}</view
              >
              <view v-else class="u-btn-picker u-btn-picker--tips"></view>
            </slot>
          </view>
          <view class="u-picker__title">
            <text>{{ title }}</text>
          </view>
          <view class="right">
            <slot name="header-right">
              <template v-if="closeIcon">
                <uni-w-button type="text" @click.stop="close" icon="iconfont icon-ptkj-dacha-xiao"></uni-w-button>
              </template>
              <view
                v-else
                class="u-btn-picker u-btn-picker--primary"
                :style="{ color: moving ? cancelColor : confirmColor }"
                hover-class="u-opacity"
                :hover-stay-time="150"
                @touchmove.stop=""
                @tap.stop="getResult('confirm')"
              >
                {{ confirmText }}
              </view>
            </slot>
          </view>
        </view>
        <view class="u-picker-body">
          <picker-view
            v-if="mode == 'region'"
            :value="valueArr"
            @change="change"
            class="u-picker-view"
            @pickstart="pickstart"
            @pickend="pickend"
            :indicator-style="indicatorStyle"
          >
            <picker-view-column v-if="!reset && params.province">
              <view class="u-column-item" v-for="(item, index) in provinces" :key="index">
                <view class="u-line-1">{{ item.label }}</view>
              </view>
            </picker-view-column>
            <picker-view-column v-if="!reset && params.city">
              <view class="u-column-item" v-for="(item, index) in citys" :key="index">
                <view class="u-line-1">{{ item.label }}</view>
              </view>
            </picker-view-column>
            <picker-view-column v-if="!reset && params.area">
              <view class="u-column-item" v-for="(item, index) in areas" :key="index">
                <view class="u-line-1">{{ item.label }}</view>
              </view>
            </picker-view-column>
          </picker-view>
          <picker-view
            v-else-if="mode == 'time'"
            :value="valueArr"
            @change="change"
            class="u-picker-view"
            @pickstart="pickstart"
            @pickend="pickend"
            :indicator-style="indicatorStyle"
          >
            <picker-view-column v-if="!reset && params.year">
              <view class="u-column-item" v-for="(item, index) in years" :key="index">
                {{ item }}
                <text class="u-text" v-if="showTimeTag">{{ $t("uni-w-picker.year", "年") }}</text>
              </view>
            </picker-view-column>
            <picker-view-column v-if="!reset && params.month">
              <view class="u-column-item" v-for="(item, index) in months" :key="index">
                {{ formatNumber(item, "month") }}
                <text class="u-text" v-if="showTimeTag">{{ $t("uni-w-picker.month", "月") }}</text>
              </view>
            </picker-view-column>
            <picker-view-column v-if="!reset && params.day">
              <view class="u-column-item" v-for="(item, index) in days" :key="index">
                {{ formatNumber(item) }}
                <text class="u-text" v-if="showTimeTag">{{ $t("uni-w-picker.day", "日") }}</text>
              </view>
            </picker-view-column>
            <picker-view-column v-if="!reset && params.hour">
              <view class="u-column-item" v-for="(item, index) in hours" :key="index">
                {{ formatNumber(item) }}
                <text class="u-text" v-if="showTimeTag">{{ $t("uni-w-picker.hour", "时") }}</text>
              </view>
            </picker-view-column>
            <picker-view-column v-if="!reset && params.minute">
              <view class="u-column-item" v-for="(item, index) in minutes" :key="index">
                {{ formatNumber(item) }}
                <text class="u-text" v-if="showTimeTag">{{ $t("uni-w-picker.minute", "分") }}</text>
              </view>
            </picker-view-column>
            <picker-view-column v-if="!reset && params.second">
              <view class="u-column-item" v-for="(item, index) in seconds" :key="index">
                {{ formatNumber(item) }}
                <text class="u-text" v-if="showTimeTag">{{ $t("uni-w-picker.second", "秒") }}</text>
              </view>
            </picker-view-column>
          </picker-view>
          <picker-view
            v-else-if="mode == 'selector'"
            :value="valueArr"
            @change="change"
            class="u-picker-view"
            @pickstart="pickstart"
            @pickend="pickend"
            :indicator-style="indicatorStyle"
          >
            <picker-view-column v-if="!reset">
              <view class="u-column-item" v-for="(item, index) in range" :key="index">
                <view class="u-line-1">{{ getItemValue(item, "selector") }}</view>
              </view>
            </picker-view-column>
          </picker-view>
          <picker-view
            v-else-if="mode == 'multiSelector'"
            :value="valueArr"
            @change="change"
            class="u-picker-view"
            @pickstart="pickstart"
            @pickend="pickend"
            :indicator-style="indicatorStyle"
          >
            <picker-view-column v-if="!reset" v-for="(item, index) in range" :key="index">
              <view class="u-column-item" v-for="(item1, index1) in item" :key="index1">
                <view class="u-line-1">{{ getItemValue(item1, "multiSelector") }}</view>
              </view>
            </picker-view-column>
          </picker-view>
        </view>
        <view v-if="closeIcon" class="bottom-button">
          <uni-w-button type="primary" block @click="getResult('confirm')">{{
            $t("global.confirm", "确定")
          }}</uni-w-button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import provinces from "../../util/province.js";
import citys from "../../util/city.js";
import areas from "../../util/area.js";
import moment from "moment";

/**
 * picker picker弹出选择器
 * @description 此选择器有两种弹出模式：一是时间模式，可以配置年，日，月，时，分，秒参数 二是地区模式，可以配置省，市，区参数
 * @tutorial https://www.uviewui.com/components/picker.html
 * @property {Object} params 需要显示的参数，见官网说明
 * @property {String} mode 模式选择，region-地区类型，time-时间类型（默认time）
 * @property {String Number} start-year 可选的开始年份，mode=time时有效（默认1950）
 * @property {String Number} end-year 可选的结束年份，mode=time时有效（默认2050）
 * @property {Boolean} safe-area-inset-bottom 是否开启底部安全区适配（默认false）
 * @property {Boolean} show-time-tag 时间模式时，是否显示后面的年月日中文提示
 * @property {String} cancel-color 取消按钮的颜色（默认#606266）
 * @property {String} confirm-color 确认按钮的颜色（默认#2979ff）
 * @property {String} default-time 默认选中的时间，mode=time时有效
 * @property {String} confirm-text 确认按钮的文字
 * @property {String} cancel-text 取消按钮的文字
 * @property {String} default-region 默认选中的地区，中文形式，mode=region时有效
 * @property {String} default-code 默认选中的地区，编号形式，mode=region时有效
 * @property {Boolean} mask-close-able 是否允许通过点击遮罩关闭Picker（默认true）
 * @property {String Number} z-index 弹出时的z-index值（默认1075）
 * @property {Array} default-selector 数组形式，其中每一项表示选择了range对应项中的第几个
 * @property {Array} range 自定义选择的数据，mode=selector或mode=multiSelector时有效
 * @property {String} range-key 当range参数的元素为对象时，指定Object中的哪个key的值作为选择器显示内容
 * @event {Function} confirm 点击确定按钮，返回当前选择的值
 * @event {Function} cancel 点击取消按钮，返回当前选择的值
 * @example <uni-w-picker v-model="show" mode="time"></uni-w-picker>
 */
export default {
  name: "uni-w-picker",
  props: {
    // picker中需要显示的参数
    params: {
      type: Object,
      default() {
        return {
          year: true,
          month: true,
          day: true,
          hour: false,
          minute: false,
          second: false,
          province: true,
          city: true,
          area: true,
          timestamp: true,
        };
      },
    },
    // 当mode=selector或者mode=multiSelector时，提供的数组
    range: {
      type: Array,
      default() {
        return [];
      },
    },
    // 当mode=selector或者mode=multiSelector时，提供的默认选中的下标
    defaultSelector: {
      type: Array,
      default() {
        return [0];
      },
    },
    // 当 range 是一个 Array＜Object＞ 时，通过 range-key 来指定 Object 中 key 的值作为选择器显示内容
    rangeKey: {
      type: String,
      default: "",
    },
    // 模式选择，region-地区类型，time-时间类型，selector-单列模式，multiSelector-多列模式
    mode: {
      type: String,
      default: "time",
    },
    // 开始时间
    start: String,
    // 年份开始时间
    startYear: {
      type: [String, Number],
      default: 1950,
    },
    // 结束时间
    end: String,
    // 年份结束时间
    endYear: {
      type: [String, Number],
      default: 2100,
    },
    // "取消"按钮的颜色
    cancelColor: {
      type: String,
      default: "#606266",
    },
    // "确定"按钮的颜色
    confirmColor: {
      type: String,
      default: "#2979ff",
    },
    // 默认显示的时间，2025-07-02 || 2025-07-02 13:01:00 || 2025/07/02
    defaultTime: {
      type: String,
      default: "",
    },
    // 默认显示的地区，可传类似["河北省", "秦皇岛市", "北戴河区"]
    defaultRegion: {
      type: Array,
      default() {
        return [];
      },
    },
    // 时间模式时，是否显示后面的年月日中文提示
    showTimeTag: {
      type: Boolean,
      default: true,
    },
    // 默认显示地区的编码，defaultRegion和areaCode同时存在，areaCode优先，可传类似["13", "1303", "130304"]
    areaCode: {
      type: Array,
      default() {
        return [];
      },
    },
    safeAreaInsetBottom: {
      type: Boolean,
      default: true,
    },
    // 是否允许通过点击遮罩关闭Picker
    maskCloseAble: {
      type: Boolean,
      default: true,
    },
    // 通过双向绑定控制组件的弹出与收起
    value: {
      type: Boolean,
      default: false,
    },
    // 弹出的z-index值
    zIndex: {
      type: [String, Number],
      default: 0,
    },
    // 顶部标题
    title: {
      type: String,
      default: "",
    },
    // 取消按钮的文字
    cancelText: {
      type: String,
      default: "取消",
    },
    // 确认按钮的文字
    confirmText: {
      type: String,
      default: "确认",
    },
    borderRadius: {
      type: String,
      default: "16px 16px 0 0",
    },
    closeIcon: Boolean,
  },
  data() {
    return {
      years: [],
      months: [],
      days: [],
      hours: [],
      minutes: [],
      seconds: [],
      year: 0,
      month: 0,
      day: 0,
      hour: 0,
      minute: 0,
      second: 0,
      startDate: "",
      endDate: "",
      reset: false,
      valueArr: [],
      provinces: provinces,
      citys: citys[0],
      areas: areas[0][0],
      province: 0,
      city: 0,
      area: 0,
      moving: false, // 列是否还在滑动中，微信小程序如果在滑动中就点确定，结果可能不准确
      indicatorStyle: "height: 48px",
    };
  },
  created() {
    this.setRangeData();
  },
  mounted() {
    this.init(true);
  },
  computed: {
    propsChange() {
      // 引用这几个变量，是为了监听其变化
      return `${this.mode}-${this.defaultTime}-${this.startYear}-${this.endYear}-${this.defaultRegion}-${this.areaCode}`;
    },
    regionChange() {
      // 引用这几个变量，是为了监听其变化
      return `${this.province}-${this.city}`;
    },
    yearAndMonth() {
      return `${this.year}-${this.month}`;
    },
    yearAndMonthAndDay() {
      return `${this.year}-${this.month}-${this.day}`;
    },
    yearAndMonthAndDayAndHour() {
      return `${this.year}-${this.month}-${this.day}-${this.hour}`;
    },
    yearAndMonthAndDayAndHourAndMinute() {
      return `${this.year}-${this.month}-${this.day}-${this.hour}-${this.minute}`;
    },
    yearChange() {
      return `${this.year}`;
    },
    rangeDataChange() {
      return `${this.start}-${this.end}`;
    },
  },
  watch: {
    propsChange() {
      this.reset = true;
      setTimeout(() => this.init(true), 10);
    },
    rangeDataChange() {
      this.setRangeData();
      this.reset = true;
      setTimeout(() => this.init(true), 10);
    },
    // 如果地区发生变化，为了让picker联动起来，必须重置this.citys和this.areas
    regionChange(val) {
      this.citys = citys[this.province];
      this.areas = areas[this.province][this.city];
    },
    // watch监听月份的变化，实时变更日的天数，因为不同月份，天数不一样
    // 一个月可能有30，31天，甚至闰年2月的29天，平年2月28天
    yearAndMonth(val) {
      if (this.params.year) this.setDays();
    },
    yearChange() {
      if (this.start && this.params.month) {
        this.setMonths();
      }
    },
    yearAndMonthAndDay() {
      if (this.start && this.params.hour) {
        this.setHours();
      }
    },
    yearAndMonthAndDayAndHour() {
      if (this.start && this.params.minute) {
        this.setMinutes();
      }
    },
    yearAndMonthAndDayAndHourAndMinute() {
      if (this.start && this.params.second) {
        this.setSeconds();
      }
    },
    // 微信和QQ小程序由于一些奇怪的原因(故同时对所有平台均初始化一遍)，需要重新初始化才能显示正确的值
    value(n) {
      if (n) {
        this.reset = true;
        setTimeout(() => this.init(), 10);
      } else {
        if (this.$refs.popup) {
          this.$refs.popup.close();
        }
      }
    },
  },
  methods: {
    moment,
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    onTrigger() {
      if (!this.value) {
        this.$emit("input", true);
      }
    },
    // 弹框状态变化
    popupChange({ show }) {
      if (!show) {
        this.close();
      }
    },
    // 标识滑动开始，只有微信小程序才有这样的事件
    pickstart() {
      // #ifdef MP-WEIXIN
      this.moving = true;
      // #endif
    },
    // 标识滑动结束
    pickend() {
      // #ifdef MP-WEIXIN
      this.moving = false;
      // #endif
    },
    // 对单列和多列形式的判断是否有传入变量的情况
    getItemValue(item, mode) {
      // 目前(2020-05-25)uni-app对微信小程序编译有错误，导致v-if为false中的内容也执行，错误导致
      // 单列模式或者多列模式中的getItemValue同时被执行，故在这里再加一层判断
      if (this.mode == mode) {
        return typeof item == "object" ? item[this.rangeKey] : item;
      }
    },
    // 小于10前面补0，用于月份，日期，时分秒等
    formatNumber(num, type) {
      if (type == "month" && this.$i18n.locale.startsWith("en")) {
        let monthsShort = "Jan_Feb_Mar_Apr_May_Jun_Jul_Aug_Sep_Oct_Nov_Dec".split("_");
        return monthsShort[num - 1];
      }
      return +num < 10 ? "0" + num : String(num);
    },
    // 生成递进的数组
    generateArray: function (start, end) {
      // 转为数值格式，否则用户给end-year等传递字符串值时，下面的end+1会导致字符串拼接，而不是相加
      start = Number(start);
      end = Number(end);
      end = end > start ? end : start;
      // 生成数组，获取其中的索引，并剪出来
      return [...Array(end + 1).keys()].slice(start);
    },
    getIndex: function (arr, val) {
      let index = arr.indexOf(val);
      // 如果index为-1(即找不到index值)，~(-1)=-(-1)-1=0，导致条件不成立
      return ~index ? index : 0;
    },
    //日期时间处理
    initTimeValue() {
      // 格式化时间，在IE浏览器(uni不存在此情况)，无法识别日期间的"-"间隔符号
      let fdate = this.defaultTime.replace(/\-/g, "/");
      let now = moment().format("yyyy/MM/dd"); //2020/01/01 如果没有日期，就默认今日
      fdate = fdate && fdate.indexOf("/") == -1 ? `${now} ${fdate}` : fdate;
      let time = null;
      if (fdate) time = new Date(fdate);
      else time = new Date();
      // 获取年日月时分秒
      this.year = time.getFullYear();
      this.month = Number(time.getMonth()) + 1;
      this.day = time.getDate();
      this.hour = time.getHours();
      this.minute = time.getMinutes();
      this.second = time.getSeconds();
    },
    init(init) {
      this.valueArr = [];
      this.reset = false;
      if (this.mode == "time") {
        this.initTimeValue();
        if (this.params.year) {
          this.valueArr.push(0);
          this.setYears();
        }
        if (this.params.month) {
          this.valueArr.push(0);
          this.setMonths();
        }
        if (this.params.day) {
          this.valueArr.push(0);
          this.setDays();
        }
        if (this.params.hour) {
          this.valueArr.push(0);
          this.setHours();
        }
        if (this.params.minute) {
          this.valueArr.push(0);
          this.setMinutes();
        }
        if (this.params.second) {
          this.valueArr.push(0);
          this.setSeconds();
        }
      } else if (this.mode == "region") {
        if (this.params.province) {
          this.valueArr.push(0);
          this.setProvinces();
        }
        if (this.params.city) {
          this.valueArr.push(0);
          this.setCitys();
        }
        if (this.params.area) {
          this.valueArr.push(0);
          this.setAreas();
        }
      } else if (this.mode == "selector") {
        this.valueArr = this.defaultSelector;
      } else if (this.mode == "multiSelector") {
        this.valueArr = this.defaultSelector;
        this.multiSelectorValue = this.defaultSelector;
      }
      this.$forceUpdate();
      if (!init) {
        this.$refs.popup.open();
      }
    },
    setPickerIndex(mode) {
      let index = 0;
      let arr = ["year", "month", "day", "hour", "minute", "second"];
      for (let i = 0; i < arr.length; i++) {
        if (this.params[arr[i]]) {
          if (arr[i] == mode) {
            break;
          } else {
            index++;
          }
        }
      }
      return index;
    },
    // 设置picker的某一列值
    setYears() {
      // 获取年份集合
      if (this.startDate && this.endDate) {
        this.years = this.generateArray(this.startDate.year(), this.endDate.year());
      } else {
        this.years = this.generateArray(this.startYear, this.endYear);
      }
      // 设置this.valueArr某一项的值，是为了让picker预选中某一个值
      this.valueArr.splice(0, 1, this.getIndex(this.years, this.year));
    },
    setMonths() {
      if (this.startDate && this.endDate) {
        let startYear = this.startDate.year();
        let endYear = this.endDate.year();
        let startMonth = this.startDate.month() + 1;
        let endMonth = this.endDate.month() + 1;
        if (this.year == startYear && this.year == endYear) {
          // 同一年
          this.months = this.generateArray(startMonth, endMonth);
          if (this.month < startMonth) this.month = startMonth;
          else if (this.month > endMonth) this.month = endMonth;
        } else if (this.year == startYear) {
          // 开始年
          this.months = this.generateArray(startMonth, 12);
          if (this.month < startMonth) this.month = startMonth;
        } else if (this.year == endYear) {
          // 结束年
          this.months = this.generateArray(1, endMonth);
          if (this.month > endMonth) this.month = endMonth;
        } else {
          // 中间年
          this.months = this.generateArray(1, 12);
        }
      } else {
        this.months = this.generateArray(1, 12);
      }
      let index = this.setPickerIndex("month");
      this.valueArr.splice(index, 1, this.getIndex(this.months, this.month));
    },
    setDays() {
      let totalDays = new Date(this.year, this.month, 0).getDate();
      this.days = this.generateArray(1, totalDays);
      let index = 0;
      // 这里不能使用类似setMonths()中的this.valueArr.splice(this.valueArr.length - 1, xxx)做法
      // 因为this.month和this.year变化时，会触发watch中的this.setDays()，导致this.valueArr.length计算有误
      if (this.params.year && this.params.month) index = 2;
      else if (this.params.month) index = 1;
      else if (this.params.year) index = 1;
      else index = 0;
      // 当月份变化时，会导致日期的天数也会变化，如果原来选的天数大于变化后的天数，则重置为变化后的最大值
      // 比如原来选中3月31日，调整为2月后，日期变为最大29，这时如果day值继续为31显然不合理，于是将其置为29(picker-column从1开始)
      if (this.startDate && this.endDate) {
        let startDate = this.startDate.date();
        let endDate = this.endDate.date();
        let start_yearandMonth = this.startDate.format("yyyy-MM");
        let end_yearandMonth = this.endDate.format("yyyy-MM");
        if (
          start_yearandMonth == end_yearandMonth &&
          this.moment(start_yearandMonth).isSame(this.year + "-" + this.formatNumber(this.month))
        ) {
          // 同一个月
          this.days = this.generateArray(startDate, endDate);
          if (this.day > endDate) this.day = endDate;
          else if (this.day < startDate) this.day = startDate;
        } else if (this.moment(start_yearandMonth).isSame(this.year + "-" + this.formatNumber(this.month))) {
          // 开始月
          this.days = this.generateArray(startDate, totalDays);
          if (this.day < startDate) this.day = startDate;
        } else if (this.moment(end_yearandMonth).isSame(this.year + "-" + this.formatNumber(this.month))) {
          // 结束月
          this.days = this.generateArray(1, endDate);
          if (this.day > endDate) this.day = endDate;
        } else {
          // 中间月
          if (this.day > this.days.length) this.day = this.days.length;
        }
      } else {
        if (this.day > this.days.length) this.day = this.days.length;
      }
      this.valueArr.splice(index, 1, this.getIndex(this.days, this.day));
    },
    setHours() {
      if (this.startDate && this.endDate) {
        this.setTimeDatas(
          "hour",
          "yyyy-MM-DD",
          this.year + "-" + this.formatNumber(this.month) + "-" + this.formatNumber(this.day),
          0,
          23
        );
      } else {
        this.hours = this.generateArray(0, 23);
      }
      let index = this.setPickerIndex("hour");
      this.valueArr.splice(index, 1, this.getIndex(this.hours, this.hour));
    },
    setMinutes() {
      if (this.startDate && this.endDate) {
        this.setTimeDatas(
          "minute",
          "yyyy-MM-DD HH",
          this.year +
            "-" +
            this.formatNumber(this.month) +
            "-" +
            this.formatNumber(this.day) +
            " " +
            this.formatNumber(this.hour),
          0,
          59
        );
      } else {
        this.minutes = this.generateArray(0, 59);
      }
      let index = this.setPickerIndex("minute");
      this.valueArr.splice(index, 1, this.getIndex(this.minutes, this.minute));
    },
    setSeconds() {
      if (this.startDate && this.endDate) {
        this.setTimeDatas(
          "second",
          "yyyy-MM-DD HH:mm",
          this.year +
            "-" +
            this.formatNumber(this.month) +
            "-" +
            this.formatNumber(this.day) +
            " " +
            this.formatNumber(this.hour) +
            ":" +
            this.formatNumber(this.minute),
          0,
          59
        );
      } else {
        this.seconds = this.generateArray(0, 59);
      }
      let index = this.setPickerIndex("second");
      this.valueArr.splice(index, 1, this.getIndex(this.seconds, this.second));
    },
    setTimeDatas(mode = "hour", format = "yyyy-MM-DD", formatVal, start, end) {
      let startData = this.startDate[mode]();
      let endData = this.endDate[mode]();
      let start_format = this.startDate.format(format);
      let end_format = this.endDate.format(format);
      if (start_format == end_format && this.moment(start_format).isSame(formatVal)) {
        // 开始结束相同
        this[mode + "s"] = this.generateArray(startData, endData);
        if (this[mode] > endData) this[mode] = endData;
        else if (this[mode] < startData) this[mode] = startData;
      } else if (this.moment(start_format).isSame(formatVal)) {
        // 开始相同
        this[mode + "s"] = this.generateArray(startData, end);
        if (this[mode] < startData) this[mode] = startData;
      } else if (this.moment(end_format).isSame(formatVal)) {
        // 结束相同
        this[mode + "s"] = this.generateArray(start, endData);
        if (this[mode] > endData) this[mode] = endData;
      } else {
        // 中间日期
        this[mode + "s"] = this.generateArray(start, end);
      }
    },
    setRangeData() {
      this.startDate = "";
      this.endDate = "";
      if (this.start && this.end) {
        let start = moment(this.start);
        let end = moment(this.end);
        let now = moment();
        if (this.params.hour) {
          if (start.isSame(end, "date")) {
            // 不显示年,月, 日选项，时间份排最前面，如果开始结束年月日一致，则将开始年月日设为当前年月日
            start.year(now.year());
            end.year(now.year());
            start.month(now.month());
            end.month(now.month());
            start.date(now.date());
            end.date(now.date());
          }
        } else if (this.params.day) {
          if (start.isSame(end, "month")) {
            // 不显示年,月选项，日期份排最前面，如果开始结束年月一致，则将开始年月设为当前年月
            start.year(now.year());
            end.year(now.year());
            start.month(now.month());
            end.month(now.month());
          }
        } else if (this.params.month) {
          if (start.isSame(end, "year")) {
            // 不显示年选项，月份排最前面，如果开始结束年份一致，则将开始年设为当前年
            start.year(now.year());
            end.year(now.year());
          }
        }
        this.startDate = start;
        this.endDate = end;
      }
    },
    setProvinces() {
      // 判断是否需要province参数
      if (!this.params.province) return;
      let tmp = "";
      let useCode = false;
      // 如果同时配置了defaultRegion和areaCode，优先使用areaCode参数
      if (this.areaCode.length) {
        tmp = this.areaCode[0];
        useCode = true;
      } else if (this.defaultRegion.length) tmp = this.defaultRegion[0];
      else tmp = 0;
      // 历遍省份数组匹配
      provinces.map((v, k) => {
        if (useCode ? v.value == tmp : v.label == tmp) {
          tmp = k;
        }
      });
      this.province = tmp;
      this.provinces = provinces;
      // 设置默认省份的值
      this.valueArr.splice(0, 1, this.province);
    },
    setCitys() {
      if (!this.params.city) return;
      let tmp = "";
      let useCode = false;
      if (this.areaCode.length) {
        tmp = this.areaCode[1];
        useCode = true;
      } else if (this.defaultRegion.length) tmp = this.defaultRegion[1];
      else tmp = 0;
      citys[this.province].map((v, k) => {
        if (useCode ? v.value == tmp : v.label == tmp) {
          tmp = k;
        }
      });
      this.city = tmp;
      this.citys = citys[this.province];
      this.valueArr.splice(1, 1, this.city);
    },
    setAreas() {
      if (!this.params.area) return;
      let tmp = "";
      let useCode = false;
      if (this.areaCode.length) {
        tmp = this.areaCode[2];
        useCode = true;
      } else if (this.defaultRegion.length) tmp = this.defaultRegion[2];
      else tmp = 0;
      areas[this.province][this.city].map((v, k) => {
        if (useCode ? v.value == tmp : v.label == tmp) {
          tmp = k;
        }
      });
      this.area = tmp;
      this.areas = areas[this.province][this.city];
      this.valueArr.splice(2, 1, this.area);
    },
    close() {
      this.$emit("input", false);
    },
    // 用户更改picker的列选项
    change(e) {
      this.valueArr = e.detail.value;
      let i = 0;
      if (this.mode == "time") {
        // 这里使用i++，是因为this.valueArr数组的长度是不确定长度的，它根据this.params的值来配置长度
        // 进入if规则，i会加1，保证了能获取准确的值
        if (this.params.year) this.year = this.years[this.valueArr[i++]];
        if (this.params.month) this.month = this.months[this.valueArr[i++]];
        if (this.params.day) this.day = this.days[this.valueArr[i++]];
        if (this.params.hour) this.hour = this.hours[this.valueArr[i++]];
        if (this.params.minute) this.minute = this.minutes[this.valueArr[i++]];
        if (this.params.second) this.second = this.seconds[this.valueArr[i++]];
      } else if (this.mode == "region") {
        if (this.params.province) this.province = this.valueArr[i++];
        if (this.params.city) this.city = this.valueArr[i++];
        if (this.params.area) this.area = this.valueArr[i++];
      } else if (this.mode == "multiSelector") {
        let index = null;
        // 对比前后两个数组，寻找变更的是哪一列，如果某一个元素不同，即可判定该列发生了变化
        this.defaultSelector.map((val, idx) => {
          if (val != e.detail.value[idx]) index = idx;
        });
        // 为了让用户对多列变化时，对动态设置其他列的变更
        if (index != null) {
          this.$emit("columnchange", {
            column: index,
            index: e.detail.value[index],
          });
        }
      }
    },
    // 用户点击确定按钮
    getResult(event = null) {
      // #ifdef MP-WEIXIN
      if (this.moving) return;
      // #endif
      let result = {};
      // 只返回用户在this.params中配置了为true的字段
      if (this.mode == "time") {
        if (this.params.year) result.year = this.formatNumber(this.year || 0);
        if (this.params.month) result.month = this.formatNumber(this.month || 0);
        if (this.params.day) result.day = this.formatNumber(this.day || 0);
        if (this.params.hour) result.hour = this.formatNumber(this.hour || 0);
        if (this.params.minute) result.minute = this.formatNumber(this.minute || 0);
        if (this.params.second) result.second = this.formatNumber(this.second || 0);
        if (this.params.timestamp) result.timestamp = this.getTimestamp();
      } else if (this.mode == "region") {
        if (this.params.province) result.province = provinces[this.province];
        if (this.params.city) result.city = citys[this.province][this.city];
        if (this.params.area) result.area = areas[this.province][this.city][this.area];
      } else if (this.mode == "selector") {
        result = this.valueArr;
      } else if (this.mode == "multiSelector") {
        result = this.valueArr;
      }
      if (event) this.$emit(event, result);
      this.close();
    },
    // 获取时间戳
    getTimestamp() {
      // yyyy-mm-dd为安卓写法，不支持iOS，需要使用"/"分隔，才能二者兼容
      let time =
        this.year + "/" + this.month + "/" + this.day + " " + this.hour + ":" + this.minute + ":" + this.second;
      return new Date(time).getTime() / 1000;
    },
  },
};
</script>

<style lang="scss" scoped>
.u-datetime-picker {
  position: relative;
  z-index: 999;
}

.u-picker-view {
  height: 100%;
  box-sizing: border-box;
}

.u-picker-header {
  padding: 14px 5rem 8px;
  font-size: var(--w-font-size-base);
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;

  .left {
    position: absolute;
    left: 10px;
  }

  .u-picker__title {
    flex: 1;
    text-align: center;
    font-size: var(--w-font-size-lg);
    color: $uni-main-color;
    font-weight: bold;
  }

  .right {
    position: absolute;
    right: 10px;
  }
}

.u-picker-body {
  width: 100%;
  height: 240px;
  overflow: hidden;
  background-color: #fff;
}

.u-column-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  font-size: var(--w-font-size-lg);
  color: var(--w-text-color-mobile);
  padding: 0 8rpx;
}

.u-text {
  font-size: var(--w-font-size-lg);
  padding-left: 8rpx;
}

.u-btn-picker {
  padding: 16rpx;
  box-sizing: border-box;
  text-align: center;
  text-decoration: none;
}

.u-opacity {
  opacity: 0.5;
}

.u-btn-picker--primary {
  color: var(--w-primary-color);
}

.u-btn-picker--tips {
  color: var(--w-primary-color);
}
.uni-w-picker {
  --uni-w-picker-header-after-border-color: transparent;
  .bottom-button {
    padding: var(--w-padding-sm) var(--w-padding-xs) 38px;
  }
}
</style>
