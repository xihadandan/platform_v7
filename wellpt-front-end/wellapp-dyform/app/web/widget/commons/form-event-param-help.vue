<template>
  <div style="width: 600px">
    <p>
      1. 支持通过
      <a-tag>${ 字段编码 }</a-tag>
      表达式解析表单数据, 例如 ${ orderCode }
    </p>

    <p>
      2. 支持通过模板字符串逻辑输出内容值, 模板字符串内通过
      <a-tag><% %></a-tag>
      编写javaScript代码 ( 注意: 代码内不需要通过 ${} 表达式解析数据对象 ), 例如:
      <code
        style="
          display: block;
          padding: 10px 25px;
          border-radius: 4px;
          background: rgb(250 250 250);
          outline: 1px solid #dedede;
          margin-top: 8px;
        "
      >
        <% if ( orderCode == 1 ) { %> 输出内容 <% } else { %> 输出内容 <% } %>
      </code>
    </p>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'FormEventParamHelp'
};
</script>
