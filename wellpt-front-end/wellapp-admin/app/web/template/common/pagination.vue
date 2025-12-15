<template>
  <div style="text-align: right; margin-top: 20px" v-if="pagination.total">
    <a-pagination
      :show-size-changer="showSizeChanger"
      :show-quick-jumper="showQuickJumper"
      v-model="pagination.current"
      :pageSize="pagination.pageSize"
      @change="onChangePagination"
      @showSizeChange="onShowSizeChange"
      :total="pagination.total"
      :pageSizeOptions="pageSizeOptions"
      :show-total="showTotal"
    />
  </div>
</template>

<script>
export default {
  model: {
    prop: 'pagination'
  },
  props: {
    pageSizeOptions: {
      type: Array,
      default: () => ['10', '20', '50', '100']
    },
    pagination: {
      type: Object,
      default: () => {
        return {
          current: 1,
          pageSize: 10,
          total: 0
        };
      }
    },
    showSizeChanger: {
      type: Boolean,
      default: true
    },
    showQuickJumper: {
      type: Boolean,
      default: true
    },
    showTotal: {
      type: Function,
      default: total => {
        return `共${total}条记录`;
      }
    }
  },
  data() {
    return {};
  },
  computed: {},
  created() {},
  methods: {
    onChangePagination() {
      this.$emit('change');
    },
    onShowSizeChange(current, pageSize) {
      this.pagination.current = current;
      this.pagination.pageSize = pageSize;
      this.$emit('change');
    }
  }
};
</script>
