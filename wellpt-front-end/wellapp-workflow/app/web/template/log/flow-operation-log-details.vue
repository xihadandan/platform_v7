<template>
  <a-form-model
    class="basic-info"
    :model="details"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'left' } }"
    :colon="false"
  >
    <a-tabs>
      <a-tab-pane v-if="details.input" tab="提交参数" key="input">
        <JsonViewer :value="details.input" :expand-depth="3" boxed sort></JsonViewer>
      </a-tab-pane>
      <a-tab-pane v-if="details.output" tab="提交结果参数" key="output">
        <JsonViewer :value="details.output" :expand-depth="3" boxed sort></JsonViewer>
      </a-tab-pane>
      <a-tab-pane v-if="details.transition && details.transition.toTaskId" tab="流程走向" key="transition">
        <a-form-model-item label="目标环节" prop="toTaskName">
          {{ details.transition.toTaskName }}
        </a-form-model-item>
        <a-form-model-item v-if="details.transition.todoUserName" label="办理人" prop="todoUserName">
          {{ details.transition.todoUserName }}
        </a-form-model-item>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import JsonViewer from 'vue-json-viewer';
import 'vue-json-viewer/style.css';
export default {
  inject: ['$event'],
  components: { JsonViewer },
  data() {
    let logData = (this.$event && this.$event.meta) || {};
    let details = (logData.details && JSON.parse(logData.details)) || {};
    return { details };
  }
};
</script>

<style></style>
