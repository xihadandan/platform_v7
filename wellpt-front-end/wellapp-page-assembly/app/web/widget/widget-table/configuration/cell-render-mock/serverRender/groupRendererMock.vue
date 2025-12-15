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
  name: 'groupRendererMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      /* 
      分隔符分号“;”
      SEMICOLON("Semicolon", ";"), COMMA("Comma", ","), DOT("Dot", "."), COLON("Colon", ":"), SPACE("space", " "), LINE(
            "Line Separator", System.getProperty("line.separator")), SLASH("slash", "/"), BACKSLASH("backslash", "\\"), AMPERSAND(
            "Ampersand", "&"), ASTERISK("Asterisk", "*"), SINGLE_QUOTES("Single Quotes", "'"), DOUBLE_QUOTES(
            "Double Quotes", "\""), UNDERLINE("Underline", "_"), VERTICAL("Vertical", "|");
      
      G0000000285 各中心负责人
      G0000000287 部门负责人
      */
      text: '各中心负责人;部门负责人'
    };
  }
};
</script>
