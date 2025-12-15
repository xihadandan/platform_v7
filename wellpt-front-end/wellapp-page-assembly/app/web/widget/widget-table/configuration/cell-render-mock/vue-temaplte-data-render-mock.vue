<template>
  <div>
    <a-form-model-item label="示例：">
      {{ text }}
      <a-tag
        :style="{
          fontSize: '14px'
        }"
        color="var(--w-primary-color)"
        v-if="row.endTime && _$USER && _$USER.userId == row.assigneeId"
        class="current-user-tag"
      >
        我
      </a-tag>
    </a-form-model-item>
    <a-form-model-item label="示例代码：" class="_example-code">
      <a-button size="small" title="复制代码" type="link" @click="evt => copyCode(evt)" class="_code-button">
        <Icon type="pticon iconfont icon-ptkj-fuzhi" />
      </a-button>
      <div style="line-height: 20px; background-color: var(--w-gray-color-4); padding: 10px; border-radius: 6px">
        <pre>{{ codeSource }}</pre>
      </div>
    </a-form-model-item>
  </div>
</template>

<script>
const code = `
{{ text }}
<a-tag
  :style="{
    fontSize: '14px'
  }"
  color="var(--w-primary-color)"
  v-if="row.endTime && _$USER && _$USER.userId == row.assigneeId"
  class="current-user-tag"
>
  我
</a-tag>
`;
// import code from '!!raw-loader!./vue-temaplte-source.txt';
import { copyToClipboard } from '@framework/vue/utils/util';

export default {
  name: 'vueTemplateDataRenderMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      // dataIndex:"assignee"
      rowIndex: 0,
      row: {
        endTime: '2025-03-26 15:49:53',
        assignee: '三三',
        assigneeId: 'U_168403123713343488'
      },
      text: '三三',
      codeSource: code
    };
  },
  methods: {
    copyCode(evt) {
      copyToClipboard(`${this.codeSource}`, evt, success => {
        if (success) {
          this.$message.success('已复制');
        }
      });
    }
  }
};
</script>

<style lang="less" scoped>
._example-code {
  position: relative;
}
._code-button {
  position: absolute;
  top: -38px;
  right: 0;
}
</style>
