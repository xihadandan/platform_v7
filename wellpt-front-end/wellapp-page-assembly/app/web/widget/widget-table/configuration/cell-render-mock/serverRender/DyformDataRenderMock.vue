<!-- 表单类型渲染器 -->
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
  name: 'DyformDataRenderMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      serverRenderOptions: [
        { value: 'DyformDataRender', label: '表单类型渲染器' },
        { value: 'APP_FUNCTION_TYPE_NAME_RENDER', label: '平台功能类型名称渲染器' }, // 后端找不到“PT_APP_FUNCTION_TYPE”服务
        { value: 'appModuleName', label: '模块名称渲染器' },
        { value: 'DyformNameDataStoreRender', label: '表单名称渲染器' },
        { value: 'groupRenderer', label: '群组渲染器' },
        { value: 'dateRenderer', label: '日期渲染器' },
        { value: 'dataDictionaryRenderer', label: '数据字典渲染器' },
        { value: 'customRenderer', label: '自定义渲染器' },
        { value: 'JsonPathRenderer', label: 'JsonPath模板渲染器' },
        { value: 'percentageDataStoreRenderer', label: '百分比渲染器' },
        { value: 'senderRenderer', label: '发送人渲染器' },
        { value: 'systemUnitName', label: '系统单位名称渲染器' },
        { value: 'userRenderer', label: '用户渲染器' },
        { value: 'freemarkerTemplateRenderer', label: 'Freemarker模板渲染器' },
        { value: 'favoriteRenderer', label: '收藏渲染器' },
        { value: 'enumClassRenderer', label: '平台公共功能_枚举类渲染器' },
        { value: 'materialFileDataStoreRender', label: '材料文件数据仓库渲染器' },
        { value: 'orgElementRenderer', label: '组织元素渲染器' },
        { value: 'WebmailFolderUnreadAndTotalMail', label: '平台邮件_邮件文件夹的邮件未读与总邮件数' },
        { value: 'WebmailUseCapacityDataStoreRender', label: '平台邮件_邮件占用空间可读性渲染' },
        { value: 'webmailSendStatusDataStoreRenderer', label: 'Webmail投递状态渲染' },
        { value: 'WebmailIconDataStoreRenderer', label: 'Webmail图标状态渲染' },
        { value: 'DiConfigNameDataStoreRender', label: '数据交换_配置名称渲染' }
      ],
      text: '存储单据' // P("P", "存储单据"), V("V", "展现单据"), M("M", "手机单据"), MST("MST", "子表单"), C("C", "产品定义单据");
    };
  }
};
</script>
