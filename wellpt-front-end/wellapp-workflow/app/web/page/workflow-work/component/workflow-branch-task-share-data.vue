<script>
import WorkflowSubflowShareData from './workflow-subflow-share-data.vue';
export default {
  name: 'WorkflowBranchTaskShareData',
  extends: WorkflowSubflowShareData,
  methods: {
    // 分支流不需要选择子流程
    loadNewFlowLabelInfos: function (parentTaskInstUuid, isMajor) {
      this.showSelectedNewFlow = false;
    },
    // 分页加载数据，分支流不需要
    loadShareDatasByPage(keyword = '', pagination = {}) {
      this.pagination = pagination;
    },
    // 返回发起分支流接口地址
    getStartSubflowUrl() {
      return '/api/workflow/work/startBranchTask';
    },
    // 返回分支流变更时限接口地址
    getChangeLimitTimeUrl() {
      return '/api/workflow/work/changeTaskDueTime';
    },
    // 返回分支流变更时限选择的数据
    getChangeLimitTimeData(selection = []) {
      return selection;
    },
    // 返回分支流重办接口地址
    getRedoUrl() {
      return '/api/workflow/work/redoBranchTask';
    },
    // 返回分支流终止接口地址
    getStopUrl() {
      return '/api/workflow/work/stopBranchTask';
    }
  }
};
</script>
