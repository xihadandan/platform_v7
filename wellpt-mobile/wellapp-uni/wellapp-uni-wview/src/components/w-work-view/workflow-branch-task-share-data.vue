<template></template>
<script>
import wWorkflowSubflowShareData from "./workflow-subflow-share-data.vue";
export default {
  extends: wWorkflowSubflowShareData,
  methods: {
    // 返回分支流变更时限接口地址
    getChangeLimitTimeUrl() {
      return "/api/workflow/work/changeTaskDueTime";
    },
    // 返回分支流重办接口地址
    getRedoUrl() {
      return "/api/workflow/work/redoBranchTask";
    },
    // 返回分支流终止接口地址
    getStopUrl() {
      return "/api/workflow/work/stopBranchTask";
    },
  },
};
</script>
