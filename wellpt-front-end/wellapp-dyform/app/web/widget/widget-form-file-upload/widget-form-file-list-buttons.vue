<template>
  <div :style="{ display: display, divStyle }" v-show="visible">
    <a-button
      v-for="(btn, i) in buttonAlones"
      :key="i"
      :size="size"
      :type="btn.type || defalutType"
      :loading="btn.loading"
      @click="$evt => onClick($evt, btn)"
    >
      <Icon v-if="btn.style && btn.style.icon" :type="btn.style.icon" :size="iconSize" />
      <!-- btnType：1为内置按钮 -->
      <template v-if="!(btn.style && btn.style.btnType)">
        {{ btn.btnType == '1' ? $t('WidgetFormFileUpload.button.' + btn.id, btn.buttonName) : $t(btn.id, btn.buttonName) }}
      </template>
    </a-button>

    <a-dropdown @visibleChange="dropdownVisibleChange" v-if="collapseButtons.length > 0" :getPopupContainer="getPopupContainer()">
      <a-menu slot="overlay">
        <a-menu-item v-for="(b, j) in collapseButtons" :key="'child-' + j" @click="$evt => onMenuItemClick($evt, b)">
          <Icon v-if="b.icon" :type="b.icon" :size="20" />
          {{ b.btnType == '1' ? $t('WidgetFormFileUpload.button.' + b.id, b.buttonName) : $t(b.id, b.buttonName) }}
        </a-menu-item>
      </a-menu>
      <a-button :size="size">
        {{ $t('WidgetFormFileUpload.button.more', '更多') }}
        <a-icon type="down" />
      </a-button>
    </a-dropdown>
  </div>
</template>
<script type="text/babel">
import { getPopupContainerNearestPs } from '@framework/vue/utils/function.js';

export default {
  name: 'WidgetFormFileListButtons',
  inject: ['widgetContext'],
  props: {
    buttons: Array,
    file: Object,
    fileIndex: Number,
    visibleTrigger: {
      type: String,
      default: 'none' // none ,  hover
    },
    display: {
      type: String,
      default: 'inline-block'
    },
    divStyle: {
      type: Object,
      default: () => {
        return {};
      }
    },
    collapseNumber: {
      type: Number,
      default: 3
    },
    size: {
      type: String,
      default: 'small'
    },
    defalutType: {
      type: String,
      default: 'link'
    },
    isSubformCell: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      collapseButtons: [],
      visible: this.visibleTrigger === 'none',
      dropdownButtonVisible: false
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    buttonAlones() {
      let buttonAlones = this.buttons;
      if (this.collapseNumber != undefined && this.buttons.length > this.collapseNumber) {
        //超过三个，以按钮组展示
        buttonAlones = this.buttons.slice(0, this.collapseNumber);
        this.collapseButtons = this.buttons.slice(this.collapseNumber);
      }
      return buttonAlones;
    },
    iconSize() {
      return { small: 18, default: 20, large: 24 }[this.size];
    }
  },
  created() {},
  methods: {
    $t() {
      if (this.widgetContext != undefined) {
        return this.widgetContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    getPopupContainer() {
      if (this.isSubformCell) {
        return () => {
          return document.body;
        };
      }
      return getPopupContainerNearestPs();
    },
    onClick(evt, button) {
      this.$emit('listButtonClicked', {
        button,
        file: this.file,
        fileIndex: this.fileIndex,
        evt
      });
    },
    onMenuItemClick(evt, button) {
      this.onClick(evt.domEvent, button);
    },

    dropdownVisibleChange(v) {
      this.dropdownButtonVisible = v;
      if (!v && this.file != undefined && this.visibleTrigger === 'hover' && this.file.hovered === false) {
        this.visible = false;
      }
    }
  },
  mounted() {
    this.$emit('buttonMounted', { $el: this.$el });
  },
  watch: {
    'file.hovered': {
      handler(v) {
        if (this.visibleTrigger === 'hover') {
          if (v) {
            this.visible = true;
          } else {
            if (this.dropdownButtonVisible === true) {
              this.visible = true;
            } else {
              this.visible = false;
            }
          }
        }
      }
    }
  }
};
</script>
