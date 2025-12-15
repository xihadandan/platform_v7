<template>
  <div>
    <a-table
      class="pt-table"
      :pagination="pagination"
      :columns="columnDefine"
      :data-source="rows"
      :loading="loading"
      @change="onTableChange"
      :scroll="{ x: rows.length > 0 ? scrollX : undefined, y: rows.length > 0 ? 530 : undefined }"
    ></a-table>
  </div>
</template>

<style lang="less"></style>

<script type="text/babel">
export default {
  name: 'DataModelViewObjectPreviewData',
  mixins: [],
  inject: ['dataModel'],
  props: {
    vid: String,
    viewColumns: Array
  },
  data() {
    return {
      rows: [],
      scrollX: 0,
      loading: true,
      pagination: {
        current: 1, //当前页
        pageSize: 10, //每页条数
        total: 0 //总条数
      }
    };
  },

  watch: {},
  beforeCreate() {},
  components: {},
  computed: {
    columnDefine() {
      let columns = [];
      this.scrollX = 0;
      if (this.viewColumns != undefined) {
        for (let c of this.viewColumns) {
          if (c.return === true) {
            columns.push({ title: c.title, dataIndex: c.alias });
            this.scrollX += 150;
          }
        }
      }
      return columns;
    }
  },
  created() {},
  methods: {
    refresh() {
      this.fetch();
    },
    onTableChange(pagination) {
      if (typeof this.pagination !== 'boolean') {
        this.pagination.current = pagination.current;
        this.pagination.pageSize = pagination.pageSize;
      }
      this.fetch();
    },
    fetch() {
      if (!this.dataModel.sql) {
        this.loading = false;
        this.rows = [];
        this.pagination.total = 0;
        this.pagination.current = 1;
        this.pagination.totalPages = 0;
        return false;
      }
      this.loading = true;
      $axios
        .post(`/proxy/api/dm/previewQueryViewData`, {
          sql: this.dataModel.sql,
          sqlObj: this.dataModel.sqlObjJson,
          pagingInfo: { currentPage: this.pagination.current, pageSize: this.pagination.pageSize }
        })
        .then(({ data }) => {
          this.loading = false;
          this.rows = data.data.data;
          this.pagination.total = data.data.pagination.totalCount;
          this.pagination.current = data.data.pagination.currentPage;
          this.pagination.totalPages = data.data.pagination.totalPages;
        })
        .catch(() => {
          this.loading = false;
        });
    }
  },
  beforeMount() {},
  mounted() {
    this.fetch();
  }
};
</script>
