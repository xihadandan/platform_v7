<template>
  <div>
    <a-input-search
      v-model="searchValue"
      :placeholder="$t('WidgetFileManager.fullTextSearch', '全文检索')"
      allow-clear
      enter-button
      @search="onSearch"
    />
    <a-spin :spinning="loading">
      <PerfectScrollbar style="height: 400px">
        <a-list item-layout="horizontal" :data-source="dataSource">
          <a-list-item slot="renderItem" slot-scope="item, index">
            <a-list-item-meta>
              <a slot="title" @click="openFile($event, item)">
                <a-icon :type="getFileIcon(item)"></a-icon>
                <span v-html="item.title"></span>
              </a>
              <template slot="description">
                <div class="content">
                  <span v-if="item.fileNames && item.fileNames == item.content">
                    {{ $t('WidgetFileManager.attachmentContent', '附件内容') }}：
                    <span v-if="item['attachments.attachment.content']" v-html="item['attachments.attachment.content']"></span>
                    <span v-else v-html="item.fileNames"></span>
                  </span>
                  <span v-else-if="isDyform(item) && item.content">
                    {{ $t('WidgetFileManager.formData', '表单数据') }}：
                    <span v-html="item.content"></span>
                  </span>
                  <span v-else v-html="item.content"></span>
                </div>
              </template>
            </a-list-item-meta>
          </a-list-item>
          <div
            v-if="showLoadingMore && dataSource.length"
            slot="loadMore"
            :style="{ textAlign: 'center', marginTop: '12px', height: '32px', lineHeight: '32px' }"
          >
            <a-spin v-if="loadingMore" />
            <a-button v-else-if="!loading" @click="onLoadMore">{{ $t('WidgetFileManager.loadMore', '加载更多') }}</a-button>
          </div>
        </a-list>
      </PerfectScrollbar>
    </a-spin>
  </div>
</template>

<script>
import { trim } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';
import { getFileIcon } from '@framework/vue/lib/preview/filePreviewApi';
export default {
  props: {
    fileManager: Object
  },
  data() {
    return {
      searchValue: '',
      libraryUuid: this.fileManager.$widget.getRootFolder().uuid,
      dataSource: [],
      pagingInfo: {
        currentPage: 1,
        pageSize: 10
      },
      loading: false,
      showLoadingMore: true,
      loadingMore: false
    };
  },
  methods: {
    onSearch(searchValue) {
      const _this = this;
      let keyword = trim(searchValue);
      _this.latestSearchValue = keyword;
      _this.pagingInfo.currentPage = 1;
      _this.showLoadingMore = true;
      _this.queryData(keyword).then(resultData => {
        if (resultData) {
          _this.dataSource = resultData.dataList;
          _this.pagingInfo = resultData.pagingInfo;
          if (_this.pagingInfo.pageSize > resultData.dataList.length) {
            _this.showLoadingMore = false;
          }
        }
      });
    },
    getFileIcon(file) {
      return getFileIcon(file.title);
    },
    onLoadMore() {
      const _this = this;
      _this.pagingInfo.currentPage++;
      let keyword = _this.latestSearchValue;
      _this.queryData(keyword).then(resultData => {
        if (resultData) {
          _this.dataSource = _this.dataSource.concat(resultData.dataList);
          _this.pagingInfo = resultData.pagingInfo;
          if (_this.pagingInfo.pageSize > resultData.dataList.length) {
            _this.showLoadingMore = false;
          }
        } else {
          _this.showLoadingMore = false;
        }
      });
    },
    queryData(keyword) {
      const _this = this;
      _this.loading = true;
      return $axios
        .post(`/proxy/api/dms/file/fulltextQuery?libraryUuid=${_this.libraryUuid}`, {
          keyword,
          pagingInfo: _this.pagingInfo
        })
        .then(({ data: result }) => {
          _this.loading = false;
          if (result.data) {
            return _this.fillFileActions(result.data);
          }
        });
    },
    fillFileActions(resultData) {
      let dataList = resultData.dataList || [];
      if (dataList.length) {
        dataList.forEach(item => (item.uuid = item.fileUuid));
        return this.fileManager.getFileActions(dataList).then(fileActions => {
          fileActions.forEach(fileAction => {
            let files = dataList.filter(item => item.uuid == fileAction.fileUuid);
            let dataPermission = fileAction.getDataPermission();
            files.forEach(file => (file.dataPermission = dataPermission));
          });
          return resultData;
        });
      } else {
        return Promise.resolve(resultData);
      }
    },
    isAllowAccessFile(file) {
      return file.dataPermission && file.dataPermission.includes('readFile');
    },
    isDyform(item) {
      return this.fileManager.isDyform(item);
    },
    openFile(event, item) {
      const _this = this;
      if (!_this.isAllowAccessFile(item)) {
        _this.$message.error(this.$t('WidgetFileManager.message.noPermissionToViewFileContent', '无权限查看文件内容！'));
        return;
      }
      let file = deepClone(item);
      file.uuid = item.fileUuid;
      _this.fileManager.viewFile(file, event);
    }
  }
};
</script>

<style lang="less" scoped>
.content {
  height: 25px;
  line-height: 25px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
