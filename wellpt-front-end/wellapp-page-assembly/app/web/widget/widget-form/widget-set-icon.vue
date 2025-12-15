<template>
  <a-form-model-item :ref="widget.configuration.code" :prop="widget.configuration.code" :rules="rules" :style="itemStyle" v-if="vShow">
    <template slot="label" v-if="widget.configuration.titleHidden !== true">
      {{ fieldName }}
    </template>
    <WidgetIconLibModal
      v-if="editable"
      v-model="form[widget.configuration.code]"
      :zIndex="1000"
      :onlyIconClass="onlyIconClass"
      :title="fieldName"
      :deleteTitle="$t('WidgetIconLib.delete', '删除')"
    ></WidgetIconLibModal>
    <template v-else>
      <a-avatar v-if="!form[widget.configuration.code]" shape="square" style="background-color: #f7f7f7" :size="32"></a-avatar>
      <Icon v-else :type="form[widget.configuration.code]" :size="32" />
    </template>
  </a-form-model-item>
</template>
<script type="text/babel">
import formElementMinxin from './form-element.mixin';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
export default {
  name: 'WidgetSetIcon',
  mixins: [formElementMinxin],
  data() {
    return {
      onlyIconClass: this.widget.configuration.onlyIconClass === undefined ? true : this.widget.configuration.onlyIconClass
    };
  },
  components: { WidgetIconLibModal },
  watch: {},
  beforeCreate() {},
  computed: {},
  created() {},
  methods: {
    onWidgetInputChange(e) {
      let code = this.widget.configuration.code;
      if (this.form[code] != undefined) {
        if (this.widget.configuration.formatType === 'toUpperCase' || this.widget.configuration.formatType === 'toLowerCase') {
          // 自动转大写
          this.form[code] = ''[this.widget.configuration.formatType].call(this.form[code]);
          let start = e.target.selectionStart;
          this.$nextTick(() => {
            e.target.setSelectionRange && e.target.setSelectionRange(start, start);
          });
        }
      }
    }
  },
  beforeMount() {},
  mounted() {},
  destroyed() {}
};
</script>
