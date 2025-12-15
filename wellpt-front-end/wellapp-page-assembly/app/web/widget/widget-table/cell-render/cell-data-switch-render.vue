<template>
  <div @click.stop="() => {}">
    <a-switch
      v-model="value"
      :loading="loading"
      @change="onChange"
      :checkedChildren="checkedChildren"
      :unCheckedChildren="unCheckedChildren"
    />
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import cellRenderMixin from './cellRenderMixin';
export default {
  name: 'CellDataSwitchRender',
  mixins: [cellRenderMixin],
  title: '数据开关渲染器',
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      loading: false,
      checkedChildren: this.slotOption.options.openText,
      unCheckedChildren: this.slotOption.options.closeText,
      initValue: this.text == this.slotOption.options.openValue,
      value: this.text == this.slotOption.options.openValue
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onChange(checked) {
      let jsFunction = this.slotOption.options.jsFunction;
      if (jsFunction) {
        this.loading = true;
        let result = this.invokeDevelopmentMethod(jsFunction.split('.')[1], {
          checked,
          row: this.row
        });
        if (result instanceof Promise) {
          result
            .then(() => {
              this.loading = false;

              this.$set(
                this.row,
                this.slotOption.dataIndex,
                this.value ? this.slotOption.options.openValue : this.slotOption.options.closeValue
              );
            })
            .catch(() => {
              this.value = !this.value;
              this.loading = false;
            });
        } else {
          this.loading = false;
        }
      } else {
        // 无配置二开交互的情况下，不再进行开关状态切换
        this.value = !checked;
      }
    }
  }
};
</script>
