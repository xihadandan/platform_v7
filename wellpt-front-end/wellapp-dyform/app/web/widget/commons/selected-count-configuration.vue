<template>
  <div>
    <div class="ant-row ant-form-item">
      <config-sub-title>选中数（为空时不限制）</config-sub-title>
    </div>
    <a-form-model-item label="最小值个数" prop="minCount">
      <a-input-number v-model="widget.configuration.minCount" ref="minCountRef" :min="0" :precision="0"></a-input-number>
    </a-form-model-item>
    <a-form-model-item label="最大值个数" prop="maxCount">
      <a-input-number v-model="widget.configuration.maxCount" ref="maxCountRef" :min="1" :precision="0"></a-input-number>
    </a-form-model-item>
  </div>
</template>

<script type="text/babel">
export default {
  name: 'SelectedCountConfiguration',
  props: {
    widget: Object
  }
}
</script>

