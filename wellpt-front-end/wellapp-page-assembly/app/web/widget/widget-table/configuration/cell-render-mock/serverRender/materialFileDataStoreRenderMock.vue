<template>
  <div>
    <CellDataFileRenderMock cellRenderName="CellDataFileRender" :showButtons="false" />
  </div>
</template>

<script>
import CellDataFileRenderMock from '../cell-data-file-render-mock.vue';
export default {
  name: 'materialFileDataStoreRenderMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      /* 
      多个用分隔符分号“;”
      输入：fileId（文件id）374254118908198912;376777904249372673
      返回：在行数据中会增加字段logicFileInfos（是个数组）
      */
      text: '材料文件数据仓库渲染器'
    };
  },
  components: {
    CellDataFileRenderMock
  }
};
</script>
