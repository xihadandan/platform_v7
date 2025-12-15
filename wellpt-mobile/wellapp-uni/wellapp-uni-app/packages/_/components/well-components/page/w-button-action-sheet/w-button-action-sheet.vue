<template>
  <u-action-sheet
    class="w-button-action-sheet"
    :closeOnClickOverlay="true"
    :closeOnClickAction="true"
    :actions="actions"
    :cancelText="cancelText || $t('global.cancel', '取消')"
    @select="selectClick"
    @close="onClose"
    :show="show"
  ></u-action-sheet>
</template>
<style></style>
<script>
export default {
  name: "w-button-action-sheet",
  props: {
    widget: Object,
    button: Array,
    developJsInstance: [Object, Function],
    $pageJsInstance: Object,
    cancelText: {
      type: String,
      default: "",
    },
  },
  components: {},
  computed: {},
  data() {
    return { show: false };
  },
  beforeCreate() {},
  created() {},
  computed: {
    actions() {
      let actions = [];
      if (this.button && this.button.length > 0) {
        this.button.forEach((b) => {
          let title = b.title;
          if (b.titleFormatter) {
            title = b.titleFormatter(b.title);
          }
          actions.push({
            name: title,
            button: b,
          });
        });
      }
      return actions;
    },
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onClose() {
      this.show = false;
      this.$emit("close");
    },
    selectClick(e) {
      let button = e.button;
      let _this = this,
        eventHandler = button.eventHandler;
      if (eventHandler) {
        eventHandler.key = button.id || this.widget.id;
        let developJs =
          typeof this.developJsInstance === "function" ? this.developJsInstance() : this.developJsInstance;
        if (developJs == undefined) {
          developJs = {};
        }
        if (this.$pageJsInstance != undefined) {
          developJs[this.$pageJsInstance._JS_META_] = this.$pageJsInstance;
        }
        eventHandler.$developJsInstance = developJs;

        if (eventHandler.actionType) {
          _this.appContext.dispatchEvent({
            ui: _this,
            ...eventHandler,
          });
        } else {
          this.$emit("selectClick", e);
        }
      } else {
        this.$emit("selectClick", e);
      }
    },
    $t() {
      if (this.$i18n) {
        return this.$i18n.$t(this, ...arguments);
      }
      return arguments[1];
    },
  },
};
</script>
