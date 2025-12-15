<template>
  <div>
    <a-switch v-model="configuration.enabledPictureWatermark" />
    <template v-if="configuration.enabledPictureWatermark">
      <a-button type="link" @click="handleOpen" title="图片水印设置">
        <Icon type="pticon iconfont icon-ptkj-shezhi" />
      </a-button>
      <modal v-model="visible" title="图片水印设置" :ok="handleSave" :width="808" okText="确认">
        <template slot="content">
          <a-row type="flex" ref="watermark">
            <a-col flex="auto" class="water-mark-config">
              <a-form-model :labelCol="{ flex: '80px', style: { textAlign: 'left' } }" class="pt-form pt-form-flex">
                <a-collapse v-model="activeKey" expandIconPosition="right" class="w-collapse-separate">
                  <a-collapse-panel v-for="item in dataList" :key="item.type">
                    <template slot="header">
                      <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-handler" :style="{ cursor: 'move' }" />
                      <span>{{ item.label }}</span>
                      <a-switch
                        size="small"
                        v-model="item.enabled"
                        @click="(checked, event) => clickEnabled(item, checked, event)"
                        :title="item.enabled ? '已启用' : '已禁用'"
                      />
                    </template>
                    <a-form-model-item label="文字内容" :wrapperCol="{ flex: 'auto' }" v-if="item.type === 'Custom'">
                      <a-input v-model="item.text" />
                    </a-form-model-item>
                    <a-form-model-item label="字体大小" :wrapperCol="{ flex: 'auto' }">
                      <a-select :options="fontSizeOptions" v-model="item.fontSize" />
                    </a-form-model-item>
                    <a-form-model-item label="字体颜色" :wrapperCol="{ flex: 'auto' }">
                      <color-picker v-model="item.fontColor" />
                    </a-form-model-item>
                    <a-form-model-item label="水平对齐" :wrapperCol="{ flex: 'auto' }">
                      <a-select :options="textAlignOptions" v-model="item.textAlign" />
                    </a-form-model-item>
                    <a-form-model-item label="垂直区域" :wrapperCol="{ flex: 'auto' }">
                      <a-select :options="baselineOptions" v-model="item.baseline" />
                    </a-form-model-item>
                    <a-form-model-item label="透明度" :wrapperCol="{ flex: 'auto' }">
                      <a-input-number v-model="item.opacity" :min="0" :max="100" />
                    </a-form-model-item>
                  </a-collapse-panel>
                </a-collapse>
              </a-form-model>
            </a-col>
            <a-col flex="240px" class="water-mark-preview">
              <div class="tips">预览</div>
              <div class="box-content">
                <div class="box-content-container top-box">
                  <div v-for="(item, index) in topWatermark" :key="index" :style="item.style">
                    {{ item.text }}
                  </div>
                </div>
                <div class="box-content-container middle-box">
                  <div v-for="(item, index) in middleWatermark" :key="index" :style="item.style">
                    {{ item.text }}
                  </div>
                </div>
                <div class="box-content-container bottom-box">
                  <div v-for="(item, index) in bottomWatermark" :key="index" :style="item.style">
                    {{ item.text }}
                  </div>
                </div>
              </div>
            </a-col>
          </a-row>
        </template>
      </modal>
    </template>
  </div>
</template>

<script>
import moment from 'moment';
import draggable from '@framework/vue/designer/draggable';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import { cloneDeep } from 'lodash';

export default {
  name: 'PictureWatermark',
  mixins: [draggable],
  props: {
    configuration: {
      type: Object,
      default: () => {}
    },
    value: {
      type: Array,
      default: () => []
    }
  },
  components: {
    Modal,
    ColorPicker
  },
  data() {
    const dataList = cloneDeep(this.value);
    return {
      visible: false,
      readyDraggable: false,
      activeKey: '',
      dataList,
      fontSizeOptions: [
        { label: '小', value: '16px' },
        { label: '中', value: '20px' },
        { label: '大', value: '24px' }
      ],
      textAlignOptions: [
        { label: '居左对齐', value: 'left' },
        { label: '居中对齐', value: 'center' },
        { label: '居右对齐', value: 'right' }
      ],
      baselineOptions: [
        { label: '上部', value: 'top' },
        { label: '中部', value: 'middle' },
        { label: '下部', value: 'bottom' }
      ],
      addressInfo: null
    };
  },
  computed: {
    // 上部水印
    topWatermark() {
      return this.getWatermarkPreview(this.baselineOptions[0]['value']);
    },
    // 中部水印
    middleWatermark() {
      return this.getWatermarkPreview(this.baselineOptions[1]['value']);
    },
    // 下部水印
    bottomWatermark() {
      return this.getWatermarkPreview(this.baselineOptions[2]['value']);
    }
  },
  methods: {
    // 获取水印预览
    getWatermarkPreview(baseline) {
      let wm = [];
      this.dataList.map(item => {
        if (item.enabled && item.baseline === baseline) {
          wm.push(this.getItemPreview(item));
        }
      });
      return wm;
    },
    // 获取每个水印项预览样式
    getItemPreview(item) {
      let text = item.text,
        style = {
          fontSize: item.fontSize,
          textAlign: item.textAlign,
          color: item.fontColor,
          opacity: (item.opacity / 100).toFixed(2)
        };
      if (item.type === 'Time') {
        text = moment().format('YYYY-MM-DD HH:mm:ss');
      } else if (item.type === 'UserName') {
        text = this._$USER.userName;
      } else if (item.type === 'Map') {
        if (!text) {
          text = '地点示例文字';
        }
      }
      return {
        text,
        style
      };
    },
    clickEnabled(item, checked, event) {
      event.stopPropagation();
    },
    changeEnabled(item, checked, event) {
      // if (checked && item.type === 'Map' && !this.addressInfo) {
      //   createClientCommonApi()
      //     .getAMapAddress()
      //     .then(
      //       res => {
      //         this.addressInfo = res;
      //         item.text = this.addressInfo.formattedAddress;
      //       },
      //       error => {
      //         this.$message.error('获取定位失败' + error.message);
      //       }
      //     );
      // }
    },
    handleSave(callback) {
      this.$emit('input', this.dataList);
      callback(true);
    },
    handleOpen() {
      this.visible = true;
      if (!this.readyDraggable) {
        this.readyDraggable = true;
        this.$nextTick(() => {
          this.tableDraggable(this.dataList, this.$refs.watermark.$el.querySelector('.w-collapse-separate'), '.drag-handler');
        });
      }
    }
  }
};
</script>

<style lang="less">
.ant-collapse {
  &.w-collapse-separate {
    background-color: transparent;
    border: none;
    > .ant-collapse-item {
      background-color: #fafafa;
      border: 1px solid #d9d9d9;
      margin-bottom: 10px;
      border-radius: 0 !important;
      .ant-collapse-content {
        overflow: visible;
      }
    }
  }
}

.water-mark-config {
  margin: 0 10px;
  max-height: 366px;
  overflow-y: scroll;
  padding-right: 5px;
}
.water-mark-preview {
  width: 240px;
  height: 320px;
  margin: 0 30px 0 40px;
  padding: 18px;
  border: 1px solid #e8e8ea;
  .tips {
    color: grey;
    font-size: 12px;
    font-size: var(--font-size-12);
    margin: 0 0 5px;
  }
  .box-content {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    .box-content-container {
      width: 100%;
      flex: 1 1;
      text-align: center;
    }
  }
}
</style>
