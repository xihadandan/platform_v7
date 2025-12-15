<template>
  <div>
    <a-form-model-item label="示例：">
      <template v-if="cellRenderName">
        {{ text }}
      </template>
    </a-form-model-item>
  </div>
</template>

<script>
export default {
  name: 'enumClassRendererMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      /* 
      配置
      枚举类：DyformTypeEnum
      值字段：remark
      展示字段：value
      enumClass:"com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum"
      textField:"remark"
      valueField:"value"

      输入："P"
      返回：'存储单据'
      */
      text: '存储单据'
    };
  }
};
</script>
