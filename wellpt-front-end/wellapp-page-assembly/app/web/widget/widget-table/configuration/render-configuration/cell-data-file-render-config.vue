<template>
  <div>
    <a-form-model-item label="文件来源字段">
      <a-select v-model="options.fileUuidField" :style="{ width: '100%' }">
        <a-select-option v-for="(opt, i) in columnIndexOptions" :key="'col-option-' + i" :value="opt.value">
          {{ opt.label }}
          <a-tag style="position: absolute; right: 0px; top: 4px" @click.stop="() => {}">
            {{ opt.value }}
          </a-tag>
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item>
      <template slot="label">
        <a-tooltip>
          <template slot="title">
            <div>
              <p>1. 表单字段的附件解析需要指定表单数据UUID的字段作为文件夹字段</p>
              <p>2. 流程签署意见的附件解析需要指定流程实例UUID的字段作为文件夹字段</p>
            </div>
          </template>
          <label style="display: inline-flex; align-items: center">
            指定文件夹字段
            <a-icon type="question-circle-o" />
          </label>
        </a-tooltip>
      </template>
      <a-select v-model="options.folderUuidField" :style="{ width: '100%' }">
        <a-select-option v-for="(opt, i) in columnIndexOptions" :key="'col-option-' + i" :value="opt.value">
          {{ opt.label }}
          <a-tag style="position: absolute; right: 0px; top: 4px" @click.stop="() => {}">
            {{ opt.value }}
          </a-tag>
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="限定文件后缀">
      <a-select
        mode="tags"
        style="width: 100%"
        v-model="options.includeFileTypes"
        :showArrow="true"
        :options="includeFileTypeOptionsComputed"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="排除文件后缀">
      <a-select
        mode="tags"
        style="width: 100%"
        v-model="options.excludeFileTypes"
        :showArrow="true"
        :options="includeFileTypeOptionsComputed"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="文件操作">
      <ButtonConfiguration title="文件操作列表" :widget="widget" :designer="designer" :button="options.fileButton" :zIndex="1000">
        <template slot="extraVisibleCompleteSelectGroup">
          <a-select-opt-group>
            <span slot="label">
              <a-icon type="appstore" />
              字段
            </span>
            <a-select-option v-for="opt in columnIndexOptions" :key="opt.value" :title="opt.label">
              {{ opt.label }}
              <a-tag style="position: absolute; right: 0px; top: 4px" @click.stop="() => {}">
                {{ opt.value }}
              </a-tag>
            </a-select-option>
          </a-select-opt-group>
        </template>
      </ButtonConfiguration>
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import ButtonConfiguration from '../../../commons/buttons-configuration/index.vue';
export default {
  name: 'CellDataFileRenderConfig',
  props: { options: Object, designer: Object, widget: Object, columnIndexOptions: Array, column: Object },
  components: { ButtonConfiguration },
  computed: {
    includeFileTypeOptionsComputed() {
      let acceptOptions = [];
      for (let i = 0, len = this.commonFileTypes.length; i < len; i++) {
        acceptOptions.push({ label: this.commonFileTypes[i], value: this.commonFileTypes[i] });
      }
      return acceptOptions;
    }
  },
  data() {
    return {
      commonFileTypes: [
        '.doc',
        '.docx',
        '.ppt',
        '.pptx',
        '.xls',
        '.wps',
        '.xlsx',
        '.zip',
        '.txt',
        '.jpg',
        '.jpeg',
        '.gif',
        '.pdf',
        '.png',
        '.bmp'
      ]
    };
  },
  beforeCreate() {},
  created() {
    if (this.options.fileUuidField == undefined) {
      this.$set(this.options, 'fileUuidField', this.column.dataIndex);
    }
    if (this.options.includeFileTypes == undefined) {
      this.$set(this.options, 'includeFileTypes', []);
    }
    if (this.options.excludeFileTypes == undefined) {
      this.$set(this.options, 'excludeFileTypes', []);
    }
    if (this.options.fileButton == undefined) {
      this.$set(this.options, 'fileButton', {
        enable: false,
        buttons: [
          {
            id: 'download',
            code: 'download',
            unDeleted: true,
            title: '下载',
            style: {
              type: 'link',
              icon: 'download'
            }
          },
          {
            id: 'previewFile',
            code: 'previewFile',
            unDeleted: true,
            title: '预览',
            style: {
              type: 'link',
              icon: 'search'
            }
          }
        ],
        buttonGroup: {
          type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
          groups: [
            // 固定分组
            // {name:,buttonIds:[]}
          ],
          style: { textHidden: false, type: 'default', icon: undefined, rightDownIconVisible: false },
          dynamicGroupName: '更多', //动态分组名称
          dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
        }
      });
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
