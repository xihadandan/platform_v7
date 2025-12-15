<template>
  <span :style="{ display: elementHide ? 'none' : 'inline-block' }">
    <span v-on:[eventName]="onTrigger">
      <slot></slot>
    </span>
    <a-drawer
      v-if="!destroy"
      :destroyOnClose="destroyOnClose"
      :width="vWidth"
      :height="height"
      :maskClosable="maskClosable"
      :closable="closable"
      :title="hasTitleSlot ? undefined : title || undefined"
      :visible="visible"
      :placement="placement"
      :drawerStyle="drawerStyle || {}"
      :wrap-style="wrapStyle || {}"
      :body-style="bodyStyle || {}"
      :wrapClassName="vWrapClassName"
      :getContainer="container ? container : false"
      :keyboard="false"
      @close="onCancel"
      :mask="mask"
      :zIndex="zIndex"
      :afterVisibleChange="visibleChange"
      :class="drawerClass"
    >
      <template slot="title" v-if="hasTitleSlot">
        <slot name="title"></slot>
      </template>
      <div
        :id="contentId"
        class="ps__child--consume"
        :style="{
          height: scrollHeight,
          overflow: 'auto'
        }"
      >
        <!-- <PerfectScrollbar :style="{ height: scrollHeight }" ></PerfectScrollbar> -->
        <template v-if="initRender">
          <!-- 未显示不渲染内容内的组件 -->
          <slot name="content"></slot>
        </template>
      </div>

      <div
        v-show="hasFooter"
        class="ant-drawer-footer"
        ref="footer"
        :style="{
          position: 'absolute',
          bottom: 0,
          width: '100%',
          borderTop: '1px solid #e8e8e8',
          padding: '10px 16px',
          textAlign: 'right',
          left: 0,
          zIndex: 1,
          background: '#fff',
          borderRadius: '0 0 4px 4px'
        }"
      >
        <slot name="footer" v-if="footerSlots"></slot>
        <template v-else>
          <a-button type="default" @click="onCancel">取消</a-button>
          <a-button type="primary" @click="onOk">{{ okText || '确定' }}</a-button>
        </template>
      </div>
    </a-drawer>
  </span>
</template>
<style lang="less">
.modal-wrap.no-footer {
  .ant-modal-footer {
    display: none;
  }
}
</style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';

export default {
  name: 'Drawer',
  model: {
    prop: 'drawerVisible'
  },
  props: {
    forceRender: { type: Boolean, default: true },
    drawerVisible: { type: Boolean, default: false },
    width: { type: [String, Number], default: 600 },
    zIndex: { type: Number, default: 999 },
    height: { type: Number | String, default: 500 },
    title: { type: String, default: '' },
    closable: { type: Boolean, default: true },
    destroyOnClose: { type: Boolean, default: false },
    wrapClassName: String | Array,
    drawerClass: { type: String | Object, default: 'pt-drawer' }, // 抽屉弹出层类名
    mask: { type: Boolean, default: false },
    maskClosable: { type: Boolean, default: false },
    drawerStyle: Object,
    wrapStyle: Object,
    bodyStyle: Object,
    id: String,
    placement: {
      type: String,
      default: 'right'
    },
    trigger: {
      type: String,
      default: 'click'
    },
    ok: Function,
    okText: String,
    cancel: Function,
    container: Function, // 返回一个HTMLElement元素
    afterVisibleChange: Function
  },

  data() {
    let footerSlots = this.$slots.footer && this.$slots.footer.length > 0;
    let elementHide = this.$slots.default == undefined && this.container != undefined;
    return {
      elementHide, // 没有传递触发插槽，则隐藏元素，通过v-model控制抽屉展示或者隐藏
      scrollHeight: 'auto',
      suppressScrollY: true,
      contentId: generateId(),
      contentPaddingBottom: '60px',
      destroy: false,
      vWidth: this.width,
      visible: this.drawerVisible,
      initRender: this.forceRender || this.drawerVisible,
      footerSlots,
      hasTitleSlot: this.$slots.title && this.$slots.title.length > 0,
      hasFooter: this.ok != undefined || footerSlots,
      vModel: this.$listeners.input != undefined
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    vWrapClassName() {
      let className = ['modal-wrap'];
      if (!this.hasFooter) {
        className.push('no-footer');
      }
      if (this.wrapClassName) {
        if (typeof this.wrapClassName == 'string') {
          className.push(this.wrapClassName);
        } else {
          className = className.concat(this.wrapClassName);
        }
      }
      return className.join(' ');
    },
    eventName() {
      return this.trigger;
    }
  },
  created() {
    if (!this.hasFooter) {
      this.contentPaddingBottom = '0px';
    }
  },
  methods: {
    visibleChange(visible) {
      this.initRender = true;
      if (!visible && this.wrapStyle && this.wrapStyle.position == 'absolute') {
        // 抽屉在相对布局里面时候，隐藏要销毁抽屉，否则多次打开会导致容器发生位移
        this.destroy = true;
      }
      // if (visible && this.hasFooter) {
      //   this.contentPaddingBottom = document.querySelector(`#${this.contentId}`).nextElementSibling.clientHeight + 'px';
      // }
      if (visible) {
        // 计算滚动高度
        let $content = document.querySelector(`#${this.contentId}`),
          $drawerBody = $content.parentElement,
          $header = $drawerBody.previousSibling,
          $wrapperBody = $drawerBody.parentElement;
        let headerStyle = { height: 0 },
          bodyStyle = window.getComputedStyle($drawerBody),
          wrapperBodyStyle = window.getComputedStyle($wrapperBody),
          footerHeight = this.$refs.footer.clientHeight;
        if ($header) {
          headerStyle = window.getComputedStyle($header);
        }
        let height =
          parseInt(wrapperBodyStyle.height) -
          parseInt(headerStyle.height) -
          parseInt(bodyStyle.paddingBottom) -
          parseInt(bodyStyle.paddingTop) -
          footerHeight;
        this.scrollHeight = height + 'px';
        console.log(
          `drawer 内容高度 ${this.scrollHeight} = ${wrapperBodyStyle.height} - ${headerStyle.height} - ${bodyStyle.paddingBottom} - ${bodyStyle.paddingTop} - ${footerHeight}px`
        );
        //   // this.$nextTick(() => {
        //   //   this.suppressScrollY = false;
        //   //   // $content.firstChild.__vue__.$data.ps.scrollbarYRight = 0;
        //   //   // $content.firstChild.__vue__.$data.ps.isScrollbarYUsingRight = true;
        //   //   $content.firstChild.__vue__.$data.ps.update();
        //   // });
        //   // // $content.firstChild.__vue__.$data.ps.scrollbarYRight = 0;

        // 右上角关闭按钮没有设置按钮type，此时默认type="submit"，键盘点击enter时会触发该按钮点击事件
        let closeBtn = this.$el.querySelector('.ant-drawer-close');
        if (closeBtn && !closeBtn.getAttribute('type')) {
          closeBtn.setAttribute('type', 'button');
        }
      }

      if (typeof this.afterVisibleChange == 'function') {
        this.afterVisibleChange(visible);
      }
    },
    hide() {
      this.visible = false;
      if (this.vModel) {
        this.$emit('input', false);
      }
    },
    onCancel(e) {
      let _this = this;
      if (typeof _this.cancel === 'function') {
        let canCancel = _this.cancel(e);
        if (canCancel instanceof Promise) {
          canCancel.then(close => {
            if (close) {
              _this.hide();
            }
          });
        } else if (canCancel !== false) {
          _this.hide();
        }
      } else {
        _this.hide();
      }
    },
    onOk() {
      if (typeof this.ok === 'function') {
        let _this = this;
        this.ok(close => {
          if (close) {
            _this.hide();
          }
        });
      }
    },
    onTrigger() {
      this.visible = true;
      this.destroy = false;
    }
  },

  mounted() {},
  watch: {
    drawerVisible: {
      handler(v) {
        this.initRender = true;
        this.visible = v;
        if (v) {
          this.destroy = false;
        }
      }
    }
  }
};
</script>
