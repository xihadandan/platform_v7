<template>
  <div class="_result-item">
    <div class="_header">
      <div class="_category" v-show="categoryLabel">{{ categoryLabel }}</div>
      <!-- <div class="_title">
        低代码开发平台使用指南 (7.0
        <span class="_highlight">使用手册</span>
        )
      </div> -->
      <div class="_title" @click="handleOpenDetail" v-html="itemData.title"></div>
    </div>
    <!--  <div class="_content" v-html="itemData.content"></div> -->
    <div class="_content">
      <span v-if="itemData.fileNames && itemData.fileNames == itemData.content">
        {{ $t('WidgetFileManager.attachmentContent', '附件内容') }}：
        <span v-if="itemData['attachments.attachment.content']" v-html="itemData['attachments.attachment.content']"></span>
        <span v-else v-html="itemData.fileNames"></span>
      </span>
      <span v-else-if="isDyform(itemData) && itemData.content">
        {{ $t('WidgetFileManager.formData', '表单数据') }}：
        <span v-html="itemData.content"></span>
      </span>
      <span v-else v-html="itemData.content"></span>
    </div>
    <div class="_bottom">
      <div>
        创建人：
        <span v-html="itemData.creator"></span>
      </div>
      <div>创建时间：{{ createTime }}</div>
      <div>更新时间：{{ modifyTime }}</div>
    </div>
  </div>
</template>

<script>
import moment from 'moment';
import mixin from './mixin';

export default {
  name: 'ResultItem',
  mixins: [mixin],
  props: {
    itemData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      defaultPattern: 'YYYY-MM-DD HH:mm:ss'
    };
  },
  computed: {
    categoryLabel() {
      let label = '';
      if (this.itemData.categoryCodes) {
        if (Array.isArray(this.itemData.categoryCodes)) {
          const code = this.itemData.categoryCodes[0];
          if (this.categoryMap[code]) {
            label = this.categoryMap[code]['label'];
          }
        }
      }
      return label;
    },
    createTime() {
      let time = '';
      if (this.itemData.createTime) {
        time = moment(Number(this.itemData.createTime)).format(this.defaultPattern);
      }
      return time;
    },
    modifyTime() {
      let time = '';
      if (this.itemData.modifyTime) {
        time = moment(Number(this.itemData.modifyTime)).format(this.defaultPattern);
      }
      return time;
    }
  },
  created() {
    this.getTitle();
  },
  methods: {
    /**
     * 判断文件是否为表单
     *
     * @param {*} file
     * @returns
     */
    isDyform(file) {
      return 'application/dyform' == file.contentType || (file.contentType && file.contentType.includes('dyform'));
    },
    getTitle(item = this.itemData) {
      const title = this.stripHtml(item.title);
      item.titleStr = title;
      return title;
    },
    stripHtml(str) {
      if (!str) return '';

      return (
        str
          // 1. 去掉 <script> 和 <style> 里的内容
          .replace(/<script[^>]*>[\s\S]*?<\/script>/gi, '')
          .replace(/<style[^>]*>[\s\S]*?<\/style>/gi, '')

          // 2. 去掉所有 HTML 标签
          .replace(/<[^>]+>/g, '')

          // 3. 去掉 HTML 实体（例如 &nbsp; &lt; &gt; &amp;）
          .replace(/&[a-zA-Z0-9#]+;/g, '')

          // 4. 去掉多余空格和换行
          .replace(/\s+/g, ' ')
          .trim()
      );
    }
  }
};
</script>

<style lang="less" scoped>
._result-item {
  padding: 12px;
  margin-bottom: 12px;
  border-radius: 4px;
  background-color: var(--w-color-white);
  ._highlight {
    color: #e60012;
  }
  ._header {
    display: flex;
    align-items: center;
    padding-top: 5px;
    padding-bottom: 9px;
  }
  ._category {
    height: 24px;
    line-height: 24px;
    padding: 0 6px;
    margin-right: 4px;
    border-radius: 4px;
    text-align: center;
    color: var(--w-primary-color);
    background-color: var(--w-primary-color-1);
  }
  ._title {
    flex: 1;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    font-weight: bold;
    font-size: 14px;
    line-height: 14px;
    cursor: pointer;
  }
  ._content {
    height: 36px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2; /* 限制文本显示为2行 */
    overflow: hidden;

    font-size: 12px;
    line-height: 18px;
    margin-bottom: 12px;
  }
  ._bottom {
    display: flex;
    font-size: 12px;
    color: var(--w-gray-color--9);
    > div {
      margin-right: 20px;
    }
  }
}
</style>
