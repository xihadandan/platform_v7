<template>
  <div>
    <template v-if="option.enableColRenderSlot && option.colRenderTemplateName">
      <component v-if="option.colRenderTemplateName" :is="option.colRenderTemplateName">
        <template slot="default">
          <component
            :is="widget.wtype"
            :widget="widget"
            :form="form"
            :isSubformCell="true"
            @change="$evt => onCellComponentChange($evt)"
            @mounted="$evt => onCellComponentMounted($evt)"
            @valueChange="$evt => onValueChange($evt)"
            @requiredChange="e => onRequiredChange(e)"
            ref="widget"
          />
        </template>
      </component>
    </template>
    <template v-else>
      <component
        :is="widget.wtype"
        :widget="widget"
        :form="form"
        :isSubformCell="true"
        @change="$evt => onCellComponentChange($evt)"
        @mounted="$evt => onCellComponentMounted($evt)"
        @valueChange="$evt => onValueChange($evt)"
        @requiredChange="e => onRequiredChange(e)"
        ref="widget"
      />
    </template>

    <WidgetTableButtons
      v-if="option.button != undefined"
      :button="option.button"
      :meta="row"
      :visibleJudgementData="vTableButtonVisibleJudgementData"
      size="small"
      buttonDefaultType="link"
    ></WidgetTableButtons>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import cellMinxin from './cell.minxin';

export default {
  name: 'SubformFormCell',
  mixins: [cellMinxin],

  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.form.vueInstance = this.$parent.$parent.$parent; // 绑定事件派发的实例对象为当前单元格父级，使得派发事件在从表行级表单域内派发
  },
  mounted() {},
  methods: {
    onRequiredChange(e) {
      this.$set(this.row.__MODEL__, this.dataIndex + '_Required', e === true);
    },
    onCellComponentChange(evt) {
      let $wgt = this.$refs.widget;
      let displayValue = $wgt.displayValue(undefined, false);
      if (this.editMode !== 'modalEdit') {
        // 设置单元格显示值
        this.$set(this.row.__MODEL__.label, this.dataIndex, displayValue);
        this.$set(this.row, this.dataIndex, $wgt.getValue()); //设置真实值
      }
      this.$emit('change', evt);
    },
    onCellComponentMounted(evt) {
      this.$emit('mounted', evt);
      this.$set(this.row.__MODEL__, this.dataIndex + '_Required', this.$refs.widget.required === true);
    }
  }
};
</script>
