<template>
  <u-action-sheet
    :closeOnClickOverlay="true"
    :closeOnClickAction="true"
    :actions="actions"
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
  },
  components: {},
  computed: {},
  data() {
    return { show: false, actions: [] };
  },
  beforeCreate() {},
  created() {
    if (this.button && this.button.length > 0) {
      this.button.forEach((b) => {
        this.actions.push({
          name: b.title,
          button: b,
        });
      });
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onClose() {
      this.show = false;
    },
    selectClick(e) {
      let button = e.button;
      let _this = this,
        eventHandler = button.eventHandler;
      eventHandler.key = button.id || this.widget.id;
      let developJs = typeof this.developJsInstance === "function" ? this.developJsInstance() : this.developJsInstance;
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
      }
    },
  },
};
</script>
