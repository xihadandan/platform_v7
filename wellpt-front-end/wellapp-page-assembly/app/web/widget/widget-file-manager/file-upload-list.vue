<template>
  <a-list v-if="fileList && fileList.length > 0" item-layout="horizontal" :data-source="fileList" :bordered="false">
    <a-list-item slot="renderItem" slot-scope="item, index" class="file-item" :class="item.status">
      <a-icon v-if="item.status == 'uploading'" type="loading" />
      <a-icon v-if="item.status == 'error'" type="info-circle" />
      <a-icon v-else :type="getFileIcon(item.name)"></a-icon>
      &nbsp;
      {{ item.name }}
      <a-progress :percent="item.percent" size="small" :show-info="true" />
    </a-list-item>
  </a-list>
</template>

<script>
import { getFileIcon } from '@framework/vue/lib/preview/filePreviewApi';

export default {
  props: {
    fileList: Array
  },
  methods: {
    getFileIcon(fileName) {
      return getFileIcon(fileName);
    }
  }
};
</script>

<style></style>
