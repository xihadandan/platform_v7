<template>
  <!-- 文件上传配置 -->
  <div class="e-widget-form-file-configuration">
    <a-form-model
      ref="form"
      :model="widget.configuration"
      :rules="rules"
      :label-col="{ span: 8 }"
      labelAlign="left"
      :wrapper-col="{ span: 14, style: { textAlign: 'right' } }"
    >
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <FieldNameInput :widget="widget" />
          <FieldCodeInput :widget="widget" :isReadonly="fieldCodeIsReadonly" @change="changeFieldCode" />
          <!-- <template v-if="widget.configuration.type === 'picture'">
            <PictureConfiguration :widget="widget" :designer="designer" />
          </template> -->
          <a-form-model-item label="组件默认状态" :label-col="{ span: 8 }" :wrapper-col="{ span: 16, style: { textAlign: 'right' } }">
            <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState">
              <a-radio-button value="edit">可编辑</a-radio-button>
              <a-radio-button value="unedit">不可编辑</a-radio-button>
              <a-radio-button value="hidden">隐藏</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="附件类型">
            <template v-if="designer.terminalType === 'pc'">
              <a-radio-group size="small" v-model="widget.configuration.fileNormalOrText" @change="changeFileType">
                <a-radio-button value="normal">普通附件</a-radio-button>
                <a-radio-button value="text">正文附件</a-radio-button>
              </a-radio-group>
            </template>
            <template v-else-if="designer.terminalType === 'mobile'">
              <a-radio-group size="small" v-model="widget.configuration.uniConfiguration.fileNormalOrText">
                <a-radio-button value="normal">普通附件</a-radio-button>
              </a-radio-group>
            </template>
          </a-form-model-item>
          <div>
            <a-collapse :bordered="false" expandIconPosition="right">
              <a-collapse-panel key="uploadDownload" header="上传下载">
                <a-form-model-item
                  label="允许上传附件数量"
                  :label-col="{ span: 10 }"
                  :wrapper-col="{ span: 14, style: { textAlign: 'right' } }"
                >
                  <a-input-number
                    placeholder="为空不限制"
                    v-model="widget.configuration.fileLimitNum"
                    :parser="
                      count => {
                        return intGtZero.test(count) ? count : null;
                      }
                    "
                    :min="1"
                    style="width: 120px"
                  />
                </a-form-model-item>
                <a-form-model-item label="允许上传附件大小(单个)" class="display-b" :label-col="{}" :wrapper-col="{}">
                  <a-input-group :compact="true">
                    <a-input-number
                      style="width: 276px"
                      placeholder="为空不限制"
                      v-model="widget.configuration.fileSizeLimit"
                      :parser="
                        count => {
                          return intGtZero.test(count) ? count : null;
                        }
                      "
                    />
                    <a-select
                      v-model="widget.configuration.fileSizeLimitUnit"
                      style="width: 60px"
                      :getPopupContainer="getPopupContainerByPs()"
                    >
                      <a-select-option value="KB">KB</a-select-option>
                      <a-select-option value="MB">MB</a-select-option>
                      <a-select-option value="G">G</a-select-option>
                    </a-select>
                  </a-input-group>
                </a-form-model-item>
                <a-form-model-item prop="accept" class="display-b" :label-col="{}" :wrapper-col="{}">
                  <template slot="label">
                    <a-space>
                      允许上传文件类型
                      <a-popover>
                        <template slot="content">关联材料时，若材料定义有限制电子文档类型，按材料定义的类型处理</template>
                        <a-icon type="info-circle" />
                      </a-popover>
                    </a-space>
                  </template>
                  <a-select
                    mode="tags"
                    style="width: 100%"
                    placeholder="为空不限制"
                    v-model="widget.configuration.accept"
                    @change="changeAccept"
                    :options="acceptOptionsComputed"
                    :getPopupContainer="getPopupContainerByPs()"
                    :dropdownClassName="getDropdownClassName()"
                    :showArrow="true"
                  />
                </a-form-model-item>
                <a-form-model-item
                  label="允许上传重名"
                  :label-col="{ span: 10 }"
                  :wrapper-col="{ span: 14, style: { textAlign: 'right' } }"
                >
                  <a-switch v-model="widget.configuration.fileNameRepeat" />
                </a-form-model-item>
                <a-form-model-item
                  label="禁止上传含二维码的图片"
                  :label-col="{ span: 10, style: { width: '200px' } }"
                  :wrapper-col="{ span: 14, style: { textAlign: 'right' } }"
                >
                  <a-switch v-model="widget.configuration.qrCodeNotAllowed" />
                </a-form-model-item>
                <template v-if="widget.configuration.type === 'picture'">
                  <a-form-model-item label="像素检验">
                    <a-switch v-model="widget.configuration.pixelCheck" @change="changePixelCheck" />
                  </a-form-model-item>
                  <template v-if="widget.configuration.pixelCheck">
                    <a-form-model-item label="像素宽为" prop="pixelWidth">
                      <a-input
                        addonAfter="px"
                        v-model="widget.configuration.pixelWidth"
                        @change="e => changeIntGtZero(e.target.value, 'pixelWidth')"
                        style="width: 120px"
                      />
                    </a-form-model-item>
                    <a-form-model-item label="像素高为" prop="pixelHeight">
                      <a-input
                        addonAfter="px"
                        v-model="widget.configuration.pixelHeight"
                        @change="e => changeIntGtZero(e.target.value, 'pixelHeight')"
                        style="width: 120px"
                      />
                    </a-form-model-item>
                  </template>
                  <a-form-model-item
                    label="添加操作类型"
                    :label-col="{ span: 8 }"
                    :wrapper-col="{ span: 16, style: { textAlign: 'right' } }"
                  >
                    <a-radio-group size="small" v-model="widget.configuration.pictureUploadMode">
                      <a-radio-button value="local">本地上传</a-radio-button>
                      <a-radio-button value="lib">从图片库选择</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                  <template v-if="widget.configuration.pictureUploadMode === 'lib'">
                    <a-form-model-item
                      label="是否支持上传维护"
                      :label-col="{ span: 10 }"
                      :wrapper-col="{ span: 14, style: { textAlign: 'right' } }"
                    >
                      <a-switch v-model="widget.configuration.pictureLibManage" />
                    </a-form-model-item>
                    <a-form-model-item label="图片库分类">
                      <a-button type="link" size="small" style="padding-right: 0" @click="handleOpenLib">
                        选择分类({{ configuration.pictureLib.length }})
                      </a-button>
                      <a-button type="link" size="small" @click="handleJumpClassify">配置分类</a-button>
                    </a-form-model-item>
                  </template>
                </template>

                <a-form-model-item label="批量下载方式">
                  <a-radio-group size="small" v-model="widget.configuration.downloadAllType">
                    <a-radio-button value="1">压缩包</a-radio-button>
                    <a-radio-button value="2">源文件</a-radio-button>
                  </a-radio-group>
                </a-form-model-item>

                <template v-if="widget.configuration.fileNormalOrText === 'normal' && widget.configuration.type !== 'picture'">
                  <a-form-model-item label="附件来源扩展">
                    <a-switch v-model="widget.configuration.fileSourceExtend" />
                  </a-form-model-item>

                  <a-form-model-item
                    v-show="widget.configuration.fileSourceExtend"
                    label="文件来源"
                    prop="fileSourceIds"
                    class="display-b"
                    :label-col="{}"
                    :wrapper-col="{}"
                  >
                    <a-select
                      mode="multiple"
                      style="width: 100%"
                      v-model="widget.configuration.fileSourceIds"
                      :getPopupContainer="getPopupContainerByPs()"
                    >
                      <a-select-option v-for="item in fileSourceOptions" :key="item.uuid" :value="item.uuid" :title="item.sourceName">
                        {{ item.sourceName }}
                      </a-select-option>
                    </a-select>
                  </a-form-model-item>
                </template>
                <template v-if="widget.configuration.fileNormalOrText === 'normal' || designer.terminalType == 'mobile'">
                  <a-form-model-item label="附件视图" :label-col="{ span: 6 }" :wrapper-col="{ span: 18, style: { textAlign: 'right' } }">
                    <template v-if="designer.terminalType === 'pc'">
                      <a-radio-group size="small" v-model="widget.configuration.type" @change="changeFileView">
                        <a-radio-button value="simpleList">默认视图</a-radio-button>
                        <a-radio-button value="advancedList">高级视图</a-radio-button>
                        <a-radio-button value="picture">图片上传</a-radio-button>
                      </a-radio-group>
                    </template>
                    <template v-else-if="designer.terminalType === 'mobile'">
                      <a-radio-group size="small" v-model="widget.configuration.uniConfiguration.type" @change="changeFileView">
                        <a-radio-button value="simpleList">默认视图</a-radio-button>
                        <a-radio-button value="picture">图片上传</a-radio-button>
                      </a-radio-group>
                    </template>
                  </a-form-model-item>
                </template>
                <div class="advanced-list" v-show="widget.configuration.type === 'advancedList' && designer.terminalType === 'pc'">
                  <!-- 开关打开的第一个为默认值 自动选中-->
                  <a-form-model-item
                    :label="item.label"
                    :key="item.value"
                    v-for="(item, index) in widget.configuration.advancedViewListOptions"
                  >
                    <a-switch
                      checked-children="开"
                      un-checked-children="关"
                      :checked="item.openState"
                      @change="
                        val => {
                          changeOpenView(val, item, index);
                        }
                      "
                    />
                    <a-radio-group v-model="widget.configuration.advancedFileListType" class="view-radio-group">
                      <a-radio v-if="item.openState" :value="item.value">
                        {{ widget.configuration.advancedFileListType === item.value ? '默认' : '' }}
                      </a-radio>
                    </a-radio-group>
                  </a-form-model-item>
                </div>
                <FileWatermark v-if="widget.configuration.type !== 'picture'" :widget="widget" :designer="designer" />
                <template v-if="designer.terminalType === 'pc' && widget.configuration.type === 'picture'">
                  <a-form-model-item label="视图类型">
                    <a-radio-group size="small" v-model="widget.configuration.pictureView">
                      <a-radio-button value="picture-card">卡片</a-radio-button>
                      <a-radio-button value="picture">列表</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                </template>
                <template v-if="designer.terminalType === 'mobile' && widget.configuration.uniConfiguration.type === 'picture'">
                  <a-form-model-item label="视图类型">
                    <a-radio-group size="small" v-model="widget.configuration.uniConfiguration.pictureView">
                      <a-radio-button value="picture-card">卡片</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                </template>
                <template
                  v-if="
                    designer.terminalType === 'pc' &&
                    widget.configuration.type === 'picture' &&
                    widget.configuration.pictureView === 'picture-card'
                  "
                >
                  <a-form-model-item label="多图片换行">
                    <a-switch v-model="widget.configuration.enabledPictureAutoWrap" />
                  </a-form-model-item>
                  <a-form-model-item label="图片换行数值" v-if="widget.configuration.enabledPictureAutoWrap">
                    <a-input-number
                      placeholder="为空不限制"
                      v-model="widget.configuration.pictureAutoWrapCount"
                      :min="1"
                      style="width: 120px"
                    />
                  </a-form-model-item>
                </template>
                <template v-if="designer.terminalType === 'pc' && widget.configuration.type === 'picture'">
                  <a-form-model-item label="图片压缩">
                    <a-switch v-model="configuration.enabledCompressPicture" />
                  </a-form-model-item>
                  <a-form-model-item label="图片水印设置">
                    <picture-watermark :configuration="widget.configuration" v-model="widget.configuration.pictureWatermark" />
                  </a-form-model-item>
                </template>
                <template v-if="designer.terminalType === 'mobile' && widget.configuration.type === 'picture'">
                  <a-form-model-item label="拍照控制" :label-col="{ span: 6 }" :wrapper-col="{ span: 18, style: { textAlign: 'right' } }">
                    <a-radio-group size="small" v-model="widget.configuration.uniConfiguration.pictureSourceType">
                      <a-radio-button value="album">不可拍照</a-radio-button>
                      <a-radio-button value="album,camera">可拍照也可选择图片</a-radio-button>
                      <a-radio-button value="camera">仅拍照</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                </template>
              </a-collapse-panel>
              <a-collapse-panel key="buttonConfig" header="操作设置">
                <div class="button-list">
                  <template v-if="designer.terminalType === 'pc'">
                    <UploadButtonConfiguration
                      title="列表操作"
                      :designer="designer"
                      :widget="widget"
                      :buttons="widget.configuration.headerButton"
                      @addButton="addHeaderButton"
                    />
                    <UploadButtonConfiguration
                      title="行操作"
                      :designer="designer"
                      :widget="widget"
                      :buttons="widget.configuration.rowButton"
                      @addButton="addRowButton"
                    />
                  </template>
                  <template v-else-if="designer.terminalType === 'mobile'">
                    <UploadButtonConfiguration
                      title="列表操作"
                      :designer="designer"
                      :widget="widget"
                      :buttons="widget.configuration.uniConfiguration.headerButton"
                      @addButton="addUniHeaderButton"
                    />
                    <UploadButtonConfiguration
                      title="行操作"
                      :designer="designer"
                      :widget="widget"
                      :buttons="widget.configuration.uniConfiguration.rowButton"
                      @addButton="addUniRowButton"
                    />
                  </template>
                </div>
              </a-collapse-panel>
              <a-collapse-panel key="fileInfoDisplay" header="附件信息显示" v-if="widget.configuration.type !== 'picture'">
                <a-form-model-item label="文件大小">
                  <a-switch v-model="widget.configuration.enabledFileSize" />
                </a-form-model-item>
                <a-form-model-item label="上传时间">
                  <a-switch v-model="widget.configuration.enabledCreateTime" />
                </a-form-model-item>
                <a-form-model-item label="上传人">
                  <a-switch v-model="widget.configuration.enabledCreateUser" />
                </a-form-model-item>
              </a-collapse-panel>
              <a-collapse-panel key="relatedMaterial" header="关联材料">
                <a-form-model-item
                  label="启用关联材料"
                  :label-col="{ span: 10 }"
                  :wrapper-col="{ span: 14, style: { textAlign: 'right' } }"
                >
                  <a-switch v-model="widget.configuration.relatedMaterial.enabled" @change="relatedMaterialSwitchChange" />
                </a-form-model-item>
                <div v-if="widget.configuration.relatedMaterial.enabled">
                  <a-form-model-item label="关联方式">
                    <a-radio-group size="small" v-model="widget.configuration.relatedMaterial.way">
                      <a-radio-button value="1">指定材料</a-radio-button>
                      <a-radio-button value="2">指定材料编码字段</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                  <a-form-model-item v-if="widget.configuration.relatedMaterial.way == '1'" label="指定材料">
                    <a-select
                      v-model="widget.configuration.relatedMaterial.materialCodes"
                      mode="multiple"
                      show-search
                      style="width: 100%"
                      :filter-option="false"
                      @search="handleMaterialSearch"
                      @change="handleMaterialChange"
                      :getPopupContainer="getPopupContainerByPs()"
                      :dropdownClassName="getDropdownClassName()"
                    >
                      <a-select-option v-for="d in materialOptions" :key="d.id">
                        {{ d.text }}
                      </a-select-option>
                    </a-select>
                  </a-form-model-item>
                  <a-form-model-item v-if="widget.configuration.relatedMaterial.way == '2'" label="材料编码字段">
                    <a-select
                      mode="tags"
                      style="width: 100%"
                      v-model="widget.configuration.relatedMaterial.materialCodeFields"
                      :options="fieldSelectOptionsComputed"
                      :showArrow="true"
                      :getPopupContainer="getPopupContainerByPs()"
                      :dropdownClassName="getDropdownClassName()"
                    />
                  </a-form-model-item>
                  <a-form-model-item v-if="widget.configuration.relatedMaterial.enabled" label="材料归属人">
                    <a-radio-group size="small" v-model="widget.configuration.relatedMaterial.ownerType">
                      <a-radio-button value="1">当前创建人</a-radio-button>
                      <a-radio-button value="2">指定所有者字段</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                  <a-form-model-item v-if="widget.configuration.relatedMaterial.ownerType == '2'" label="所有者字段">
                    <a-select
                      mode="tags"
                      style="width: 100%"
                      v-model="widget.configuration.relatedMaterial.ownerField"
                      :options="fieldSelectOptionsComputed"
                      :showArrow="true"
                      :getPopupContainer="getPopupContainerByPs()"
                      :dropdownClassName="getDropdownClassName()"
                    />
                  </a-form-model-item>
                </div>
              </a-collapse-panel>
              <a-collapse-panel key="otherProp" header="其它属性">
                <a-form-model-item :label-col="{ span: 9 }" :wrapper-col="{ span: 15 }" label="应用于">
                  <!-- <a-tooltip slot="label" title="应用于的说明" placement="topRight" :arrowPointAtCenter="true">
                    应用于
                    <a-icon type="info-circle" />
                  </a-tooltip> -->
                  <FieldApplySelect v-model="widget.configuration.applyToDatas" />
                </a-form-model-item>
                <a-form-model-item label="描述" :label-col="{ span: 9 }" :wrapper-col="{ span: 15 }">
                  <a-textarea v-model="widget.configuration.description" />
                </a-form-model-item>
              </a-collapse-panel>
            </a-collapse>
          </div>
        </a-tab-pane>
        <a-tab-pane key="2" tab="校验规则">
          <ValidateRuleConfiguration class="file-upload-validate-configuration" :widget="widget"></ValidateRuleConfiguration>
        </a-tab-pane>
        <a-tab-pane key="3" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
    <a-modal
      title="图片库分类"
      :visible="visibleLib"
      :width="800"
      :maskClosable="false"
      dialogClass="picture-lib-modal"
      @ok="confirmPictureCategory"
      @cancel="visibleLib = false"
    >
      <a-row :gutter="16">
        <a-col :span="12" class="picture-lib-cat">
          <div class="panel-head">选择分类：</div>
          <a-input-search placeholder="关键字" :enter-button="true" v-model="serachText" @search="searchCategories" allowClear />
          <PerfectScrollbar class="picture-lib-ps" v-show="picLibCategories.length">
            <a-checkbox
              v-for="item in picLibCategories"
              :value="item.uuid"
              class="block"
              :key="item.uuid"
              :checked="selectedCategories.includes(item)"
              @change="e => onCheckPicLib(e, item)"
            >
              <span class="msgIconShow" :style="{ backgroundColor: item.color }">
                <Icon :type="item.icon || 'pticon iconfont icon-ptkj-qiehuanshitu'" />
              </span>
              <span :title="item.name">{{ item.name }}</span>
            </a-checkbox>
          </PerfectScrollbar>
          <div v-show="picLibCategories.length == 0" class="flex f_y_c f_x_c" style="width: 100%; height: 350px">
            <a-empty />
          </div>
        </a-col>
        <a-col :span="12">
          <div class="panel-head">已选分类：</div>
          <PerfectScrollbar class="selected-categorie-ps">
            <div v-for="(item, index) in selectedCategories" :key="item.uuid" :title="item.name" class="picture-lib-item">
              <span class="msgIconShow" :style="{ backgroundColor: item.color }">
                <Icon :type="item.icon || 'pticon iconfont icon-ptkj-qiehuanshitu'" />
              </span>
              {{ item.name }}
              <a-button type="link" class="close" size="small" @click.stop="delSelectedCategorie(item, index)" title="删除">
                <Icon type="pticon iconfont icon-ptkj-dacha"></Icon>
              </a-button>
            </div>
          </PerfectScrollbar>
        </a-col>
      </a-row>
    </a-modal>
  </div>
</template>

<script type="text/babel">
import { isEmpty, findIndex, map, cloneDeep } from 'lodash';
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import formCommonMixin from '../../mixin/form-common.mixin';
import { deepClone } from '@framework/vue/utils/util';
import {
  fileUploadTypeOptions,
  advancedFileListTypeOptions,
  uploadAccept,
  pictureAccept,
  rowBtnCommon,
  rowBtnEdit,
  headerBtnSimple,
  headerBtnAdvanced,
  headerBtnText,
  rowBtnPicture,
  headerBtnPicture,
  pictureBatchDownload
} from '../../commons/constant';
import PictureWatermark from './picture-watermark.vue';
import FileWatermark from './file-watermark.vue';
export default {
  name: 'WidgetFormFileUploadConfiguration',
  mixins: [formConfigureMixin, formCommonMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    let normalAcceptOptions = [];
    for (let i = 0, len = uploadAccept.length; i < len; i++) {
      normalAcceptOptions.push({ label: uploadAccept[i], value: uploadAccept[i] });
    }
    const textAcceptOptions = normalAcceptOptions.slice(0, 2);
    textAcceptOptions.push({ label: '.wps', value: '.wps' });

    const copyRowBtnCommon = JSON.parse(JSON.stringify(rowBtnCommon));
    let rowBtnAdvanced = [rowBtnEdit, ...copyRowBtnCommon];
    const rowBtnText = JSON.parse(JSON.stringify(rowBtnAdvanced));

    let pictureAcceptOptions = [];
    for (let i = 0, len = pictureAccept.length; i < len; i++) {
      pictureAcceptOptions.push({ label: pictureAccept[i], value: pictureAccept[i] });
    }

    if (headerBtnPicture.length === 1) {
      headerBtnPicture.push(cloneDeep(pictureBatchDownload));
    }
    const fileTextAcceptValidator = (rule, value, callback) => {
      if (this.widget.configuration.fileNormalOrText === 'text') {
        if (value.length) {
          callback();
        } else {
          callback('文件类型不能为空');
        }
      } else {
        callback();
      }
    };
    return {
      mobileFilter: ['onClickReplace', 'onClickSaveAs', 'onClickLookUp', 'onClickSeal', 'onClickShowHistory'],
      headerBtnSimple,
      headerBtnAdvanced,
      headerBtnText,
      headerBtnPicture,
      rowBtnSimple: rowBtnCommon,
      rowBtnAdvanced,
      rowBtnText,
      rowBtnPicture,
      fileSourceOptions: [], // 附件来源
      normalAcceptOptions, // 普通附件扩展名备选项
      normalAccept: [],
      textAcceptOptions, // 正文附件扩展名备选项
      textAccept: ['.doc', '.docx', '.wps'], // 正文扩展名默认值
      pictureAcceptOptions,
      fileUploadTypeOptions,
      advancedFileListTypeOptions, // 高级视图列表（列表视图、图标视图、表格视图）
      buttonList: [], // 操作按钮
      rules: {
        name: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true },
        code: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true },
        accept: { required: false, validator: fileTextAcceptValidator, trigger: ['change'] }
      },
      rulesPixel: {
        pixelWidth: {
          required: true,
          message: <a-icon type="close-circle" theme="filled" />,
          trigger: ['blur', 'change']
        },
        pixelHeight: {
          required: true,
          message: <a-icon type="close-circle" theme="filled" />,
          trigger: ['blur', 'change']
        }
      },
      materialOptions: [],
      fieldCodeIsReadonly: false,
      visibleLib: false,
      picLibCategories: [],
      picLibCategoriesOrg: [],
      selectedCategories: [],
      serachText: ''
    };
  },
  components: {
    PictureWatermark,
    FileWatermark,
    VNodes: {
      functional: true,
      render: (h, ctx) => ctx.props.vnodes
    }
  },
  computed: {
    // 扩展名备选项
    acceptOptionsComputed() {
      let acceptOptions = this.normalAcceptOptions;
      if (this.widget.configuration.fileNormalOrText === 'text') {
        acceptOptions = this.textAcceptOptions;
      }
      if (this.widget.configuration.type === 'picture') {
        acceptOptions = this.pictureAcceptOptions;
      }
      return acceptOptions;
    },
    fieldSelectOptionsComputed() {
      let selectedOptions = [];
      this.designer.SimpleFieldInfos.forEach(fieldInfo => {
        if (isEmpty(fieldInfo.code) || fieldInfo.code == this.widget.configuration.code) {
          return;
        }
        selectedOptions.push({ label: fieldInfo.name, value: fieldInfo.code });
      });
      return selectedOptions;
    }
  },
  watch: {
    'designer.terminalType': {
      deep: true,
      handler(value) {
        if (value === 'mobile') {
          this.setMobileType();
        } else {
          this.setPcRowButton();
        }
      }
    }
  },
  created() {
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', { fileNormalOrText: 'normal' });
    }
    if (this.widget.configuration.code) {
      this.fieldCodeIsReadonly = true;
    }
    if (this.widget.configuration.cloneWidget) {
      this.fieldCodeIsReadonly = false;
    }
    if (this.widget.configuration.type !== 'picture') {
      if (!this.configuration.hasOwnProperty('enabledFileSize')) {
        this.$set(this.widget.configuration, 'enabledFileSize', true);
      }
      if (!this.configuration.hasOwnProperty('enabledCreateTime')) {
        this.$set(this.widget.configuration, 'enabledCreateTime', true);
      }
      if (!this.configuration.hasOwnProperty('enabledCreateUser')) {
        this.$set(this.widget.configuration, 'enabledCreateUser', true);
      }
      this.initFileUpload();
    }
    if (this.configuration.type === 'picture') {
      if (!this.configuration.hasOwnProperty('pictureUploadMode')) {
        this.$set(this.configuration, 'pictureUploadMode', 'local');
      }
      if (!this.configuration.hasOwnProperty('pictureLib')) {
        this.$set(this.configuration, 'pictureLib', []);
      }
      if (this.configuration.headerButton.length === 1) {
        this.configuration.headerButton.push(cloneDeep(pictureBatchDownload));
      }
      this.initPicture();
    }
    if (this.configuration.uniConfiguration && !this.configuration.uniConfiguration.pictureSourceType) {
      this.$set(this.configuration.uniConfiguration, 'pictureSourceType', 'album,camera');
    }
    this.initButtonConfig();
    // 直接打开移动端附件配置，uniConfiguration的按钮配置可能缺失
    if (this.designer.terminalType == 'mobile') {
      this.setMobileType();
    }
  },
  mounted() {
    // 关联材料指定具体材料时，加载下拉框
    if (this.widget.configuration.relatedMaterial.enabled && this.widget.configuration.relatedMaterial.way == '1') {
      this.handleMaterialSearch();
    }
    if (this.widget.configuration.pixelCheck) {
      this.rules = { ...this.rules, ...this.rulesPixel };
    }
  },
  methods: {
    validate() {
      let results = [];
      this.$refs.form.validate((success, msg) => {
        if (!success) {
          for (let k in msg) {
            let message = msg[k][0].message;
            if (typeof message === 'string') {
              results.push(message);
            } else if (message.constructor && message.constructor.name == 'VNode') {
              if (message.data && message.data.attrs && message.data.attrs.title) {
                results.push(message.data.attrs.title);
              }
            }
          }
        }
      });
      return results;
    },
    setMobileType() {
      let fileNormalOrText = this.widget.configuration.uniConfiguration.fileNormalOrText;
      if (fileNormalOrText === 'text' || !fileNormalOrText) {
        fileNormalOrText = 'normal';
        this.widget.configuration.uniConfiguration.fileNormalOrText = 'normal';
      }
      if (!this.widget.configuration.uniConfiguration.rowButton) {
        if (!this.widget.configuration.uniConfiguration.type) {
          this.widget.configuration.uniConfiguration.type =
            this.widget.configuration.type == 'advancedList' ? 'simpleList' : this.widget.configuration.type;
        }
        if (this.widget.configuration.type !== 'advancedList') {
          this.initMobileButtons(this.widget.configuration.rowButton, this.widget.configuration.headerButton);
        } else {
          this.initMobileButtons(this.rowBtnSimple, this.headerBtnSimple);
        }
      }
      const pictureView = this.widget.configuration.uniConfiguration.pictureView;
      if (pictureView === 'picture' || !pictureView) {
        this.widget.configuration.uniConfiguration.pictureView = 'picture-card';
      }
    },
    initPicture() {
      if (!this.configuration.hasOwnProperty('pictureView')) {
        this.$set(this.configuration, 'pictureView', 'picture-card');
      }
      if (!this.configuration.hasOwnProperty('enabledCompressPicture')) {
        this.$set(this.configuration, 'enabledCompressPicture', false);
      }
      if (!this.configuration.enabledPictureWatermark) {
        this.$set(this.configuration, 'enabledPictureWatermark', false);
      }

      let wmItem = {
        type: '',
        label: '',
        enabled: false,
        text: '',
        fontSize: '20px',
        fontColor: '#999999',
        opacity: 50,
        textAlign: 'center', // left / center / right
        baseline: 'middle' // top / middle / bottom
      };
      const wmCustomLabel = { label: '自定义文字水印', type: 'Custom', text: '自定义文字' };
      if (!this.configuration.hasOwnProperty('pictureWatermark')) {
        const watermarks = [
          { label: '上传时间', type: 'Time' },
          { label: '上传地点', type: 'Map', text: '地点示例文字' },
          { label: '姓名', type: 'UserName' },
          wmCustomLabel
        ].map(item => {
          return { ...wmItem, ...item };
        });
        this.$set(this.configuration, 'pictureWatermark', watermarks);
      }
      if (this.configuration.pictureWatermark) {
        const findIndex = this.configuration.pictureWatermark.findIndex(item => item.type === 'Custom');
        if (findIndex === -1) {
          this.configuration.pictureWatermark.push({
            ...wmItem,
            ...wmCustomLabel
          });
        }
      }
    },
    initMobileButtons(rowButton = [], headerButton = []) {
      this.widget.configuration.uniConfiguration.headerButton = deepClone(headerButton);
      this.widget.configuration.uniConfiguration.rowButton = deepClone(rowButton).filter(item => !this.mobileFilter.includes(item.code));
    },
    setPcRowButton() {
      // this.changeFileView({ value: this.widget.configuration.type });
    },
    // 删除已选分类
    delSelectedCategorie(item, index) {
      if (index == undefined) {
        index = findIndex(this.selectedCategories, { uuid: item.uuid });
      }
      this.selectedCategories.splice(index, 1);
    },
    // 搜索分类
    searchCategories() {
      if (this.serachText == '') {
        this.picLibCategories = this.picLibCategoriesOrg;
        return;
      }
      let categories = [];
      for (let index = 0; index < this.picLibCategories.length; index++) {
        const item = this.picLibCategories[index];
        if (item.name.includes(this.serachText)) {
          categories.push(item);
        }
      }
      this.picLibCategories = categories;
    },
    // 选择分类
    onCheckPicLib(e, item) {
      const checked = e.target.checked;
      if (checked) {
        this.selectedCategories.push(item);
      } else {
        this.delSelectedCategorie(item);
      }
    },
    // 打开图片库
    handleOpenLib() {
      this.$axios.get('/basicdata/img/category/queryAllCategory').then(res => {
        const result = res.data;
        if (result && result.code == '0') {
          result.data.sort(function (a, b) {
            return a.code.localeCompare(b.code);
          });
          this.picLibCategoriesOrg = result.data;
          this.picLibCategories = result.data;

          let selectedCategories = [];
          for (let index = 0; index < result.data.length; index++) {
            const item = result.data[index];
            if (this.configuration.pictureLib.includes(item.uuid)) {
              selectedCategories.push(item);
            }
          }
          this.selectedCategories = selectedCategories;
        }
      });
      this.visibleLib = true;
    },
    // 确认图片分类
    confirmPictureCategory() {
      this.$set(this.widget.configuration, 'pictureLib', map(this.selectedCategories, 'uuid'));
      this.visibleLib = false;
    },
    // 跳转分类
    handleJumpClassify() {
      // window.open(
      //   '/web/app/pt-mgr.html?pageUuid=ac525dcd-50b7-42e9-95b7-658b117ac19b#/wLeftSidebar_97FEB03AFCBC4315A504847D9626E0F1/C93BACC754F00001305F9C101BB0E030'
      // );
      window.open('/webpage/pt-basicdata-mgr/151747538704138240');
    },
    changeFieldCode() {
      if (this.widget.configuration.hasOwnProperty('cloneWidget')) {
        this.$delete(this.widget.configuration, 'cloneWidget');
      }
    },
    changePixelWH(value, key) {
      if (/^[1-9]\d*$/.test(value)) {
        this.widget.configuration[key] = Number(value);
      } else {
        this.widget.configuration[key] = null;
      }
    },
    changePixelCheck(isCheck) {
      if (isCheck) {
        this.rules = { ...this.rules, ...this.rulesPixel };
      } else {
        this.$delete(this.rules, 'pixelWidth');
        this.$delete(this.rules, 'pixelHeight');
      }
    },
    initButtonConfig() {
      if (this.widget.configuration.code === '') {
        this.widget.configuration.headerButton = this.headerBtnSimple;
        this.widget.configuration.rowButton = this.rowBtnSimple;
      }
    },
    initFileUpload() {
      const accept = this.widget.configuration.accept;
      const fileNormalOrText = this.widget.configuration.fileNormalOrText;
      if (fileNormalOrText === 'text') {
        this.rules['accept']['required'] = true;
        this.textAccept = accept;
      } else {
        this.normalAccept = accept;
      }
      if (this.widget.configuration.type === 'advancedList' || fileNormalOrText === 'text') {
        if (!this.widget.configuration.hasOwnProperty('advancedViewListOptions')) {
          this.formataAdvancedView();
        }
      }
      this.fetchFileSource();
    },
    addHeaderButton(newButton) {
      let headerButton = this.widget.configuration.headerButton;
      headerButton.push(newButton);
    },
    addRowButton(newButton) {
      let rowButton = this.widget.configuration.rowButton;
      rowButton.push(newButton);
    },
    addUniHeaderButton(newButton) {
      let headerButton = this.widget.configuration.uniConfiguration.headerButton;
      headerButton.push(newButton);
    },
    addUniRowButton(newButton) {
      let rowButton = this.widget.configuration.uniConfiguration.rowButton;
      rowButton.push(newButton);
    },
    // 改变按钮状态
    changeButtonState(item, state) {
      item.defaultFlag = state;
      let operateBtnIds = [];
      this.buttonList.forEach(item => {
        if (item.defaultFlag) {
          operateBtnIds.push(item.uuid);
        }
      });
      this.widget.configuration.operateBtnIds = operateBtnIds;
    },
    // 获取附件来源
    fetchFileSource() {
      $axios
        .post('/json/data/services', {
          serviceName: 'dyformFileListSourceConfigService',
          methodName: 'getAllBean',
          args: JSON.stringify([]),
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.fileSourceOptions = data.data;
            let fileSourceIds = this.widget.configuration.fileSourceIds;
            if (!fileSourceIds.length) {
              data.data.forEach(item => {
                if (item.defaultFlag) {
                  fileSourceIds.push(item.uuid);
                }
              });
            }
          }
        });
    },
    // 获取附件按钮
    fetchFileButton() {
      $axios
        .post('/json/data/services', {
          serviceName: 'dyformFileListButtonConfigService',
          methodName: 'getAllBean',
          args: JSON.stringify([]),
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            /*
            btnShowType: 'show' 显示类操作, 'edit':编辑类操作
            btnType: '1' 内置按钮, '0':扩展按钮
            */
            let buttonList = data.data;
            const operateBtnIds = this.widget.configuration.operateBtnIds;
            buttonList.forEach(item => {
              if (operateBtnIds.length) {
                if (operateBtnIds.includes(item.uuid)) {
                  item.defaultFlag = true;
                } else {
                  item.defaultFlag = false;
                }
              } else {
                item.defaultFlag = item.defaultFlag ? true : false;
              }
            });
            this.buttonList = buttonList;
          }
        });
    },
    // 获取列表附件配置
    fetchFileListConfig() {
      $axios.get('/fileListConfig/list').then(({ data }) => {
        console.log(data);
      });
    },
    // 格式化视图列表
    formataAdvancedView() {
      const advancedViewList = this.widget.configuration.advancedViewList;
      this.advancedFileListTypeOptions.forEach((item, index) => {
        if (advancedViewList.includes(item.value)) {
          item.openState = true;
        } else {
          item.openState = false;
        }
      });
      const advancedFileListTypeOptions = JSON.parse(JSON.stringify(this.advancedFileListTypeOptions));
      this.$set(this.widget.configuration, 'advancedViewListOptions', advancedFileListTypeOptions);
    },
    // 打开/关闭视图
    changeOpenView(state, item, index) {
      let advancedViewList = this.widget.configuration.advancedViewList;
      if (state) {
        advancedViewList.push(item.value);
      } else {
        advancedViewList.splice(
          advancedViewList.findIndex(view => view === item.value),
          1
        );
      }
      const defaultView = this.advancedFileListTypeOptions[0].value;
      if (advancedViewList.includes(defaultView)) {
        const advancedFileListType = this.widget.configuration.advancedFileListType;
        if (!advancedViewList.includes(advancedFileListType)) {
          // 第一个有打开的情况下优先选中
          this.widget.configuration.advancedFileListType = defaultView;
        }
      } else {
        this.widget.configuration.advancedFileListType = advancedViewList[0] || '';
      }
      item.openState = state;
      this.$set(this.widget.configuration.advancedViewListOptions, index, item);
    },
    // 改变扩展名
    changeAccept(accept) {
      const fileNormalOrText = this.widget.configuration.fileNormalOrText;
      if (fileNormalOrText === 'text') {
        this.textAccept = accept;
      } else {
        this.normalAccept = accept;
      }
    },
    // 改变附件类型 普通附件、正文附件
    changeFileType(e) {
      let accept, fileType, headerButton, rowButton;
      const fileNormalOrText = e.value ? e.value : e.target.value;
      if (fileNormalOrText === 'text') {
        this.rules['accept']['required'] = true;
        accept = this.textAccept;
        fileType = 'advancedList';
        headerButton = this.headerBtnText;
        rowButton = this.rowBtnText;
        this.formataAdvancedView();
      } else {
        this.rules['accept']['required'] = false;
        accept = this.normalAccept;
        fileType = 'simpleList';
        headerButton = this.headerBtnSimple;
        rowButton = this.rowBtnSimple;
      }
      this.widget.configuration.fileNormalOrText = fileNormalOrText;
      this.widget.configuration.headerButton = headerButton;
      this.widget.configuration.rowButton = rowButton;
      this.widget.configuration.type = fileType;
      this.widget.configuration.accept = accept;
      this.$nextTick(() => {
        this.$refs.form.validateField('accept');
      });
    },
    // 改变附件视图 默认视图（简易视图）、高级视图
    changeFileView(e) {
      const fileView = e.value ? e.value : e.target.value;
      let headerButton, rowButton;
      if (fileView === 'advancedList') {
        headerButton = this.headerBtnAdvanced;
        rowButton = this.rowBtnAdvanced;
        this.formataAdvancedView();
      } else if (fileView == 'picture') {
        headerButton = this.headerBtnPicture;
        rowButton = this.rowBtnPicture;
      } else {
        headerButton = this.headerBtnSimple;
        rowButton = this.rowBtnSimple;
      }

      this.widget.configuration.type = fileView;
      this.widget.configuration.accept = [];
      this.widget.configuration.fileLimitNum = null;
      this.widget.configuration.fileSizeLimit = null;

      this.widget.configuration.headerButton = deepClone(headerButton);
      this.widget.configuration.rowButton = deepClone(rowButton);

      // 如果是高级视图，移动端要改成简易视图
      if (fileView === 'advancedList') {
        this.widget.configuration.uniConfiguration.type = 'simpleList';
        this.initMobileButtons(rowBtnSimple, headerBtnSimple);
      } else {
        this.widget.configuration.uniConfiguration.type = fileView;
        this.initMobileButtons(rowButton, headerButton);
      }
      if (fileView === 'picture') {
        this.initPicture();
      }
    },
    onFileUploadTypeChange(v) {
      this.widget.configuration.inputMode = { simpleList: '6', picture: '4', advancedFileList: '33' }[v];
    },
    relatedMaterialSwitchChange(enabled) {
      if (enabled && this.materialOptions.length === 0) {
        this.handleMaterialSearch();
      }
    },
    // 材料选择数据搜索
    handleMaterialSearch(value = '') {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'cdMaterialDefinitionFacadeService',
          queryMethod: 'loadSelectData',
          searchValue: value,
          pageSize: 20
        })
        .then(({ data }) => {
          if (data.results) {
            _this.materialOptions = data.results;
          }
        });
    },
    // 材料选择数据变更
    handleMaterialChange(value) {}
  }
};
</script>

<style lang="less">
.e-widget-form-file-configuration {
  .edit-status-box {
    height: 400px;
    overflow: auto;
  }
}
.picture-lib-modal {
  .panel-head {
    padding-bottom: 10px;
  }
  .picture-lib-cat {
    border-right: solid 1px #e8e8e8;
    .picture-lib-ps {
      margin-top: 16px;
      height: 350px;
      padding-right: 10px;
    }
    .ant-checkbox-wrapper.block {
      display: block;
      padding-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      padding-right: 26px;
    }
  }
  .selected-categorie-ps {
    height: 396px;
  }
  .picture-lib-item {
    position: relative;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    padding-right: 26px;
    .close {
      display: none;
      position: absolute;
      right: 0;
      top: 0;
    }
    &:hover {
      background-color: var(--w-primary-color-1);
      .close {
        display: inline;
      }
    }
  }
}
.file-upload-validate-configuration {
  > :nth-child(2) {
    .ant-form-item-label {
      width: auto;
    }
  }
}
</style>

<style lang="less" scoped>
.advanced-list {
  padding: 10px;
  margin: 10px;
  background: rgba(228, 228, 228, 1);
  .ant-form-item {
    padding-right: 0;
    margin-bottom: 10px !important;
    background: rgba(244, 244, 244, 1);
    &:last-child {
      margin-bottom: 0 !important;
    }
  }
  .view-radio-group {
    width: 45%;
    text-align: left;
    padding-left: 10px;
  }
}
.button-list {
  margin-right: 10px;
  .ant-form-item {
    padding-right: 11px;
  }
}
</style>
