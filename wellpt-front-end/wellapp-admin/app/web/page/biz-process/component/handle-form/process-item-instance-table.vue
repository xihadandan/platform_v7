<script>
import ProcessInstanceTable from './process-instance-table.vue';
export default {
  extends: ProcessInstanceTable,
  data() {
    return { title: '业务事项办件', widgetTableId: 'jJZHTHeTRKoNuMqwvOShhjvfoOQHhdTg' };
  },
  methods: {
    beforeMountTable(tableDefinition) {
      // 去除行操作按钮
      tableDefinition.configuration.headerButton = {};
    }
  }
};
</script>
