<template>
  <!-- 环节事件监听 -->
  <w-tree-select
    class="workflow-tree-select"
    :value="taskListener"
    :treeData="treeData"
    :treeCheckable="treeCheckable"
    :replaceFields="{
      children: 'children',
      title: 'name',
      key: 'id',
      value: 'id'
    }"
    @change="changeTaskListener"
  />
</template>
<script>
import WTreeSelect from '../components/w-tree-select';
export default {
  name: 'TaskListenerTreeSelect',
  props: {
    value: {
      type: String
    },
    treeCheckable: {
      type: Boolean,
      default: false // 显示checkbox, true时multiple为true表示多选
    }
  },
  components: {
    WTreeSelect
  },
  computed: {
    taskListener() {
      return this.value;
    }
  },
  data() {
    return {
      treeData: []
    };
  },
  created() {
    this.getTaskListeners();
  },
  methods: {
    // 获取环节事件监听
    getTaskListeners() {
      const params = {
        args: JSON.stringify([-1]),
        methodName: 'getTaskListeners',
        serviceName: 'flowSchemeService'
      };
      this.$axios
        .post('/json/data/services', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.treeData = data;
            }
          }
        });
    },
    changeTaskListener(value, label, extra) {
      this.$emit('input', value);
      this.$emit('change', ...arguments);
    }
  }
};
</script>
