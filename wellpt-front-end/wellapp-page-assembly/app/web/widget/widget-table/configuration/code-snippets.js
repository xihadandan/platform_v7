export default {
  tableImportModalSnippet: `
<template>
  <a-modal @ok="onOk" v-model="visible" title="导入数据">
    <div>
      <a-upload-dragger
        name="file"
        :multiple="false"
        :showUploadList="false"
        :before-upload="e => beforeUpload(e)"
        :customRequest="e => customFileUploadRequest(e)"
      >
        <p class="ant-upload-drag-icon">
          <a-icon type="inbox" />
        </p>
        <p style="margin-bottom: 20px; line-height: 28px" v-show="fileId != undefined">
          <template>
            <label @click.stop="() => {}">
              <a-icon style="color: var(--w-success-color)" type="check-circle" theme="filled" />
              已上传:
            </label>
            <a-tag @click.stop="() => {}" class="primary-color">
              {{ fileName }}
            </a-tag>
          </template>
        </p>
        <p class="ant-upload-text">点击或者拖拽文件到此区域上传</p>
        <p class="ant-upload-hint">
          下载导入模板
          <a-button size="small" icon="link" type="link" @click.stop="downloadImportFileTemplate">
            {{ fileTemplate.name }}
          </a-button>
        </p>
      </a-upload-dragger>
      <div style="display: flex; justify-content: space-between; align-items: center; line-height: 52px">
        <div v-if="importModalInfo.importFile.fileID != undefined">
          <label @click.stop="() => {}">
            <a-icon style="color: var(--w-success-color)" type="check-circle" theme="filled" />
            已上传:
          </label>
          <a-tag
            @click.stop="() => {}"
            closable
            @close.stop="importModalInfo.importFile.fileID = undefined"
            v-show="importModalInfo.importFile.fileID != undefined"
            class="primary-color"
          >
            {{ importModalInfo.importFile.fileName }}
          </a-tag>
        </div>
      </div>
      <div v-if="importModalInfo.successCount != undefined" style="color: #999">
        <a-space>
          <TableExcelImportResultModal :result="importModalInfo.importResult">
            <a-button size="small" type="link" icon="profile">导入结果:</a-button>
          </TableExcelImportResultModal>
          <div>
            成功
            <a-badge
              :showZero="true"
              :overflowCount="9999999"
              :count="importModalInfo.successCount"
              :number-style="{ backgroundColor: '#52c41a' }"
            />
            失败
            <a-badge :showZero="true" :overflowCount="9999999" :count="importModalInfo.failCount" />
          </div>
        </a-space>
      </div>
    </div>
  </a-modal>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'TestImportModal',
  props: {
    fileTemplate: Object,
    confirmImportFile: Function,
    customFileUploadRequest: Function,
    importModalInfo: {
      type: Object,
      default: () => {
        return {
          importFile: {}
        };
      }
    }
  },
  components: {},
  computed: {},
  data() {
    return { visible: true, fileId: undefined, fileName: undefined };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onOk() {
      this.confirmImportFile({ fileID: this.fileId }).then(result => {
        console.log('收到导入结果: ', result);
        //TODO: 处理导入结果数据
      });
    },
    close() {
      this.$emit('close');
    },
    beforeUpload(file, limitSize) {
      return new Promise((resolve, reject) => {
        if (file.type.indexOf('sheet') == -1) {
          this.$message.error('只允许上传 xls 或者 xlsx 文件格式');
          reject();
          return;
        }
        resolve(file);
      });
    },
    downloadImportFileTemplate() {
      window.open(this.fileTemplate.url, '_blank');
    }
  }
};
</script>
  `,
  javaImportListenerSnippet: `
public class ExampleExcelImportListener extends AbstractEasyExcelImportListener<Row> {

  // 依赖其他服务
  // private XxxService xxxService;

  public ExampleExcelImportListener() {

    // 其他服务实例类通过构造函数注入
    // this.xxxService = ApplicationContextHolder.getBean(XxxService.class);

  }

  @Override
  public ExcelRowDataAnalysedResult dataAnalysed(Row row, int rowIndex, AnalysisContext analysisContext) {

    //TODO: 业务处理

    return new ExcelRowDataAnalysedResult(); // 返回数据行处理结果
  }

  @Override
  public String name() {
    return "自定义导入服务类";
  }

  /**
   * 数据行对象
   */
  public static class Row extends Serializable  {

    /**
     * 注解说明 @link https://easyexcel.opensource.alibaba.com/docs/2.x/api/#%E6%B3%A8%E8%A7%A3
     */
    @com.alibaba.excel.annotation.ExcelProperty("列标题")
    private String name;

    //TODO: 其他列 ...

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

  }
}
  `,

  tableSearchTemplate: `
<template>
  <div :style="{ marginBottom: '16px' }">
    <span :style="{ marginRight: '8px' }">分类:</span>
    <template v-for="tag in tags">
      <a-checkable-tag :key="tag" :checked="selectedTags.indexOf(tag) > -1" @change="checked => handleChange(tag, checked)">
        {{ tag }}
      </a-checkable-tag>
    </template>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'TestTableSearchForm',
  /**
   * 注入描述：
   * widgetTableContext: 当前表格组件实例
   * widgetTableSearchContext: 当前所在的表格搜索域组件
   */
  inject: ['widgetTableContext', 'widgetTableSearchContext'],
  props: {},
  components: {},
  computed: {},
  data() {
    return { checked1: false, checked2: false, checked3: false, tags: ['电影', '书籍', '音乐', '运动'], selectedTags: [] };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    handleChange(tag, checked) {
      const { selectedTags } = this;
      const nextSelectedTags = checked ? [...selectedTags, tag] : selectedTags.filter(t => t !== tag);
      this.selectedTags = nextSelectedTags;

      // 触发查询: 以下参数按需设置
      this.$emit('search', {
        // 关键字
        keyword: undefined,

        /**
         * 查询条件对象: 支持字段的比较查询以及sql查询
         *
         * @columnIndex: 列索引
         * @value: 条件值
         * @type: 条件判断类型: lt 小于 , le 小于等于 , gt 大于 , ge 大于等于 , eq 等于
         *                like 匹配 (like) , nlike 不匹配 (not like) , in 在集合中 (in) ,
         *                between 区间 , is null 是空 , is not null 非空 , is (is) , exists 存在查询(需要sql语句)
         * @sql: sql语句
         * @conditions: 嵌套的查询条件对象
         */

        criterions: [{ columnIndex: 'PIHAO', type: 'eq', value: '1' }],

        // 其他服务端需要的查询命名参数
        params: {
          cccc: 'cccc'
        }
      });
    }
  }
};
</script>

  `
}
