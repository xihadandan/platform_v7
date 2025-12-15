<template>
  <!-- 图片配置 -->
  <a-collapse :bordered="false" expandIconPosition="right">
    <a-collapse-panel key="uploadDownload" header="上传下载">
      <a-form-model-item label="允许上传图片数量" :label-col="{ span: 10 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
        <a-input-number placeholder="为空不限制" v-model="widget.configuration.fileLimitNum" :min="1" style="width: 120px" />
      </a-form-model-item>
      <a-form-model-item label="允许上传图片大小(单个)" class="display-b" :label-col="{}" :wrapper-col="{}">
        <a-input placeholder="为空不限制" v-model="widget.configuration.fileSizeLimit">
          <a-select
            slot="addonAfter"
            v-model="widget.configuration.fileSizeLimitUnit"
            style="width: 60px"
            :getPopupContainer="getPopupContainerByPs()"
          >
            <a-select-option value="KB">KB</a-select-option>
            <a-select-option value="MB">MB</a-select-option>
            <a-select-option value="G">G</a-select-option>
          </a-select>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="允许上传图片类型" class="display-b" :label-col="{}" :wrapper-col="{}">
        <a-select
          mode="multiple"
          style="width: 100%"
          placeholder="为空不限制"
          v-model="widget.configuration.accept"
          :options="acceptOptions"
          :getPopupContainer="getPopupContainerByPs()"
          :dropdownClassName="getDropdownClassName()"
        />
      </a-form-model-item>
      <a-form-model-item label="允许上传重名图片" :label-col="{ span: 10 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
        <a-switch v-model="widget.configuration.fileNameRepeat" />
      </a-form-model-item>
      <a-form-model-item label="像素检验">
        <a-switch v-model="widget.configuration.pixelCheck" />
      </a-form-model-item>
      <template v-if="widget.configuration.pixelCheck">
        <a-form-model-item label="像素宽为">
          <a-input addonAfter="px" v-model="widget.configuration.pixelWidth" :min="1" style="width: 120px" />
        </a-form-model-item>
        <a-form-model-item label="像素高为">
          <a-input addonAfter="px" v-model="widget.configuration.pixelHeight" :min="1" style="width: 120px" />
        </a-form-model-item>
      </template>
      <a-form-model-item label="添加操作类型">
        <a-radio-group size="small" v-model="widget.configuration.pictureSource" @change="changePictureSource">
          <a-radio-button value="local">本地上传</a-radio-button>
          <a-radio-button value="lib">从图片库选择</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <div v-show="widget.configuration.pictureSource === 'lib'">
        <a-form-model-item label="是否支持上传维护" :label-col="{ span: 10 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
          <a-switch v-model="widget.configuration.uploadManage" />
        </a-form-model-item>
        <a-form-model-item label="图片库分类">
          <a-button type="primary" size="small" style="margin-right: 10px">选择分类</a-button>
          <a-button type="primary" size="small">配置分类</a-button>
        </a-form-model-item>
      </div>
    </a-collapse-panel>
    <a-collapse-panel key="buttonConfig" header="操作设置">
      <div class="button-list">
        <UploadButtonConfiguration
          title="列表操作"
          :designer="designer"
          :widget="widget"
          :buttons="widget.configuration.headerButton"
          :allowAddButton="false"
        />
        <UploadButtonConfiguration
          title="行操作"
          :designer="designer"
          :widget="widget"
          :buttons="widget.configuration.rowButton"
          :allowAddButton="false"
        />
      </div>
    </a-collapse-panel>
    <a-collapse-panel key="otherProp" header="其它属性">
      <a-form-model-item label="格式异常提示语" class="display-b" :label-col="{}" :wrapper-col="{}">
        <a-input placeholder="请输入提示语" v-model="widget.configuration.errorTips" />
      </a-form-model-item>
      <a-form-model-item label="备注" class="display-b" :label-col="{}" :wrapper-col="{ style: { marginTop: '2px' } }">
        <a-textarea :rows="4" placeholder="请输入内容" v-model="widget.configuration.remark" :maxLength="200" />
        <span class="textLengthShow">{{ widget.configuration.remark | textLengthFilter }}/200</span>
      </a-form-model-item>
    </a-collapse-panel>
  </a-collapse>
</template>

<script>
import { pictureAccept } from '../../commons/constant';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'PictureConfiguration',
  props: {
    widget: Object,
    designer: Object
  },
  filters: {
    textLengthFilter(text) {
      return text ? text.length : 0;
    }
  },
  data() {
    let acceptOptions = [];
    for (let i = 0, len = pictureAccept.length; i < len; i++) {
      acceptOptions.push({ label: pictureAccept[i], value: pictureAccept[i] });
    }
    return {
      acceptOptions
    };
  },
  created() {},
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    // 改变图片来源
    changePictureSource(source) {
      if (source === 'lib') {
      }
    }
  }
};
</script>
