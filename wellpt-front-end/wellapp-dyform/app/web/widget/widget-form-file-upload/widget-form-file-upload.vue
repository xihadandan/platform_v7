<template>
  <a-form-model-item
    v-show="!hidden"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="widgetClass"
  >
    <div :class="'widget-file-upload-container ' + widget.configuration.type + '_upload_theme'">
      <template v-if="widget.configuration.type == 'simpleList'">
        <a-upload
          v-if="showSimpleUpload"
          :multiple="true"
          :file-list="fileList"
          :customRequest="customRequest"
          @change="onFileUploadChange"
          :beforeUpload="onBeforeUpload"
          :showUploadList="false"
          :accept="accept"
          ref="simpleUpload"
        >
          <template v-if="widget.configuration.fileSourceExtend && editable">
            <!-- 附件来源扩展 -->
            <a-button-group>
              <a-button
                v-for="button in fileSourceOptions"
                :key="button.uuid"
                type="line"
                block
                @click.stop="onFileSourceButtonClick($event, button)"
              >
                <template v-if="button.icon">
                  <Icon :type="button.icon" />
                </template>
                {{ $t(button.uuid, button.sourceName) }}
              </a-button>
            </a-button-group>
          </template>
          <a-button type="line" v-else-if="editable" @click="onClickUpload">
            <Icon type="pticon iconfont icon-ptkj-shangchuan" />
            {{ $t('WidgetFormFileUpload.upload', '上传') }}
          </a-button>
          <div class="widget-file-upload-tips" v-if="widget.configuration.description">{{ widget.configuration.description }}</div>
        </a-upload>

        <div class="widget-file-upload-list">
          <a-popover
            v-for="(file, i) in fileList"
            :key="i"
            :title="file.name"
            trigger="hover"
            :visible="file.hovered"
            @visibleChange="v => onFileItemHoverVisibleChange(v, file)"
            overlayClassName="widget-file-upload-list"
          >
            <template slot="content">
              <a-space :size="16" v-if="enabledFileSize || enabledCreateTime || enabledCreateUser">
                <span class="file-item-desc" v-if="enabledFileSize">{{ file.formatSize }}</span>
                <span class="file-item-desc" v-if="enabledCreateTime">{{ file.dbFile.createTimeStr }}</span>
                <span class="file-item-desc" v-if="enabledCreateUser">
                  {{ file.orgFile ? ($i18n.locale !== 'zh_CN' ? _$USER.localUserName : file.dbFile.userName) : file.dbFile.userName }}
                </span>
              </a-space>
            </template>
            <div class="file-list-item flex" :fileid="file.dbFile.fileID || file.dbFile.uuid">
              <div class="f_s_0" style="width: 24px">
                <a-progress
                  v-if="file.showProgress && file.status == 'uploading'"
                  type="circle"
                  :percent="file.percent"
                  :width="14"
                  strokeColor="var(--w-primary-color)"
                  :strokeWidth="10"
                  :showInfo="false"
                />
                <template v-else-if="file.status === 'error'">
                  <a-icon type="exclamation-circle" theme="filled" class="error-icon" />
                </template>
                <template v-else>
                  <Icon :type="file.icon" />
                </template>
              </div>
              <div
                class="label-filename f_g_1 w-ellipsis"
                :title="file.name"
                :style="{ color: file.status === 'error' ? 'var(--w-danger-color)' : '', cursor: fileNameHandler ? 'pointer' : '' }"
                @click.stop="fileNameClick(file)"
              >
                {{ file.name }}
              </div>
              <!-- <div><a-icon type="check-circle" v-if="file.percent === 100" class="done-icon" /></div> -->
              <template v-if="file.status === 'error'">
                <a-button type="link" size="small" @click="handleReUpload(file)">
                  {{ $t('WidgetFormFileUpload.reupload', '重新上传') }}
                </a-button>
              </template>
              <template v-if="file.status == 'uploading' || file.status === 'error'">
                <a-button type="link" size="small" @click="handleCancelUpload(file, i)">
                  {{ $t('WidgetFormFileUpload.cancelUpload', '取消上传') }}
                </a-button>
              </template>
              <div class="file-list-item-buttons-absolute" v-show="file.hovered && file.status === 'done'">
                <span class="file-list-item-buttons" v-show="file.status === 'done'">
                  <WidgetFormFileListButtons
                    visibleTrigger="hover"
                    :buttons="rowButtonsComputed"
                    :file="file"
                    :fileIndex="i"
                    @listButtonClicked="onListButtonClicked"
                    :isSubformCell="$attrs.isSubformCell"
                  />
                </span>
              </div>
            </div>
          </a-popover>
        </div>
        <template v-if="fileList.length > 0">
          <span style="line-height: 24px">
            {{ $t('WidgetFormFileUpload.totalSize', { count: fileList.length }, '共' + fileList.length + '个附件') }}
          </span>
          <WidgetFormFileListButtons :buttons="headerButtonComputed" @listButtonClicked="onListButtonClicked" />
        </template>
      </template>
      <template v-else-if="widget.configuration.type == 'picture'">
        <template v-if="configuration.pictureView === 'picture'">
          <div class="picture-view-type-picture">
            <a-upload
              :customRequest="customImageRequest"
              listType="picture"
              :fileList="fileList"
              :showUploadList="false"
              @change="onFileUploadChange"
              :beforeUpload="onBeforeUpload"
              :multiple="true"
              :accept="accept"
              :disabled="pictureDisabled"
              :class="{
                'picture-for-lib': this.pictureForLib
              }"
              @download="onClickDownloadFile"
              :openFileDialogOnClick="openFileDialogOnClick"
              :transformFile="onPictureTransformFile"
              ref="pictureUpload"
            >
              <div v-if="showPictureAdd">
                <a-button type="line">
                  <Icon type="pticon iconfont icon-ptkj-tupian" />
                  {{ $t('WidgetFormFileUpload.uploadPic', '上传图片') }}
                </a-button>
              </div>
            </a-upload>
            <WidgetFormFileListButtons
              v-if="fileList.length"
              :buttons="pictureHeaderButton"
              @listButtonClicked="onListButtonClicked"
              size="default"
              defalutType="line"
            />
          </div>
          <div class="picture-view-picture-list">
            <div v-for="(file, i) in fileList" :key="i" :class="['picture-view-picture-item', `ant-upload-list-item-${file.status}`]">
              <div class="picture-view-picture-info">
                <div class="picture-view-picture-thumbnail">
                  <img v-if="file.url" :src="file.url" />
                  <Icon v-else-if="file.status === 'error'" type="pticon iconfont icon-ptkj-tupian" />
                  <!-- file.orgFile -->
                </div>
                <div class="picture-view-item-name">
                  <span>{{ file.name }}</span>
                  <span style="padding-left: 8px">{{ file.formatSize }}</span>
                </div>
                <div class="picture-view-item-actions">
                  <template v-if="file.status === 'error'">
                    <a-button type="link" size="small" @click="handleReUpload(file)">
                      {{ $t('WidgetFormFileUpload.reupload', '重新上传') }}
                    </a-button>
                  </template>
                  <template v-if="file.status == 'uploading' || file.status === 'error'">
                    <a-button type="link" size="small" @click="handleCancelUpload(file, i)">
                      {{ $t('WidgetFormFileUpload.cancelUpload', '取消上传') }}
                    </a-button>
                  </template>
                  <span v-if="file.status === 'done'" style="display: flex; align-items: center">
                    <a-checkbox
                      @change="ck => onAdvancedIconViewCheckChange(ck, file, i)"
                      :checked="rowSelection.selectedRowKeys.indexOf(file.uid) != -1"
                      style="padding: 0 var(--w-button-padding-lr-sm)"
                    />
                    <WidgetFormFileListButtons
                      :buttons="rowButtonsComputed"
                      :file="file"
                      :fileIndex="i"
                      @listButtonClicked="onListButtonClicked"
                    />
                  </span>
                </div>
              </div>
            </div>
          </div>
        </template>
        <template v-else>
          <a-upload
            :customRequest="customImageRequest"
            listType="picture-card"
            :fileList="fileList"
            :showUploadList="showUploadListPicture"
            @preview="onClickPreviewPicture"
            @change="onFileUploadChange"
            :beforeUpload="onBeforeUpload"
            :multiple="true"
            :accept="accept"
            :disabled="pictureDisabled"
            :class="{
              'picture-for-lib': this.pictureForLib,
              'picture-clear-both': this.isclearBoth
            }"
            @download="onClickDownloadFile"
            :openFileDialogOnClick="openFileDialogOnClick"
            :transformFile="onPictureTransformFile"
            ref="pictureUpload"
          >
            <div v-if="showPictureAdd">
              <Icon type="pticon iconfont icon-ptkj-jiahao" />
              <div class="ant-upload-text">{{ $t('WidgetFormFileUpload.clickUpload', '点击添加') }}</div>
            </div>
          </a-upload>
          <!-- <div class="ant-upload-list ant-upload-list-picture-card">
            <div class="ant-upload-list-picture-card-container">
              <span></span>
            </div>
          </div> -->
          <WidgetFormFileListButtons
            v-if="fileList.length"
            :buttons="pictureHeaderButton"
            @listButtonClicked="onListButtonClicked"
            size="default"
            defalutType="line"
            style="margin-bottom: 8px"
          />
        </template>
      </template>
      <template v-else-if="widget.configuration.type == 'advancedList'">
        <a-upload
          :multiple="true"
          :file-list="fileList"
          :customRequest="customRequest"
          @change="onFileUploadChange"
          :beforeUpload="onBeforeUpload"
          :showUploadList="false"
          ref="advancedUpload"
          :accept="accept"
          :reject="onReject"
        />

        <div class="widget-file-upload-advanced-list">
          <div v-if="headerButtonComputed.length" class="widget-file-upload-advanced-list-title">
            {{ $t('WidgetFormFileUpload.attachment', '附件') }}
            <span class="sub-title">({{ fileList.length }})</span>
            <div class="widget-file-upload-tips" v-if="widget.configuration.description">{{ widget.configuration.description }}</div>
          </div>
          <a-card size="small" style="width: 100%" :bordered="false">
            <template slot="title">
              <template v-if="!headerButtonComputed.length">
                <div class="widget-file-upload-advanced-list-title">
                  {{ $t('WidgetFormFileUpload.attachment', '附件') }}
                  <span class="sub-title">({{ fileList.length }})</span>
                  <div class="widget-file-upload-tips" v-if="widget.configuration.description">{{ widget.configuration.description }}</div>
                </div>
              </template>
              <!-- 高级视图 头部按钮 -->
              <WidgetFormFileListButtons
                v-else
                class="btn_has_space"
                size="default"
                display="flex"
                :divStyle="{ 'flex-wrap': 'wrap' }"
                :buttons="headerButtonComputed"
                @listButtonClicked="onListButtonClicked"
                :collapseNumber="6"
                defalutType="line"
              />
            </template>
            <template slot="extra" v-if="fileViewList.length > 1">
              <!-- 切换视图 -->
              <a-radio-group class="view-change-btn" v-model="advancedFileListType" button-style="solid" @change="onSwitchAdvancedView">
                <a-radio-button
                  v-for="item in fileViewList"
                  :value="item.value"
                  :key="item.value"
                  :title="$i18n.locale !== 'zh_CN' ? item.value : item.label"
                >
                  <Icon :type="item.icon" />
                </a-radio-button>
              </a-radio-group>
            </template>
            <template v-if="advancedFileListType == 'iconView'">
              <a-empty v-show="fileList.length == 0" :image="emptyImg" :description="$t('WidgetFormFileUpload.empty', '暂无数据')" />
              <a-row type="flex" class="widget-file-upload-icon-view" v-show="fileList.length > 0">
                <a-col v-for="(file, i) in fileList" :key="i" flex="100px" :gutter="12">
                  <div :class="['widget-file-upload-icon-view-item', file.status]" :fileid="file.dbFile.fileID || file.dbFile.uuid">
                    <div>
                      <template v-if="file.showProgress && file.status == 'uploading'">
                        <a-progress
                          type="circle"
                          :percent="file.percent"
                          :width="24"
                          strokeColor="var(--w-primary-color)"
                          :strokeWidth="10"
                          :showInfo="false"
                          :status="file.status === 'error' ? 'exception' : 'active'"
                        />
                        <label class="label-filename" :title="file.name">{{ file.name }}</label>
                        <label>{{ $t('WidgetFormFileUpload.fileUploading', '文件上传中') }}</label>
                      </template>
                      <template v-else-if="file.status === 'error'">
                        <a-icon type="exclamation-circle" theme="filled" class="error-icon" />
                        <label class="label-filename" :title="file.name">{{ file.name }}</label>
                        <label :style="{ color: 'var(--w-danger-color)' }">
                          {{ $t('WidgetFormFileUpload.fileUploadError', '上传错误') }}
                        </label>
                      </template>
                      <template v-else>
                        <a-checkbox
                          @change="ck => onAdvancedIconViewCheckChange(ck, file, i)"
                          :checked="rowSelection.selectedRowKeys.indexOf(file.uid) != -1"
                        />
                        <Icon :type="file.icon" class="file-icon" />
                        <label
                          class="label-filename"
                          :title="file.name"
                          @click.stop="fileNameClick(file)"
                          :style="{ cursor: fileNameHandler ? 'pointer' : '' }"
                        >
                          {{ file.name }}
                        </label>
                        <label class="label-filesize" v-if="enabledFileSize">{{ file.formatSize }}</label>
                        <a-popover
                          :title="file.name"
                          trigger="hover"
                          :visible="file.hovered"
                          @visibleChange="v => onFileItemHoverVisibleChange(v, file)"
                          overlayClassName="widget-file-upload-list"
                        >
                          <template slot="content">
                            <a-space :size="16" v-if="enabledFileSize || enabledCreateTime || enabledCreateUser">
                              <span class="file-item-desc" v-if="enabledFileSize">{{ file.formatSize }}</span>
                              <span class="file-item-desc" v-if="enabledCreateTime">{{ file.dbFile.createTimeStr }}</span>
                              <span class="file-item-desc" v-if="enabledCreateUser">
                                {{
                                  file.orgFile
                                    ? $i18n.locale !== 'zh_CN'
                                      ? _$USER.localUserName
                                      : file.dbFile.userName
                                    : file.dbFile.userName
                                }}
                              </span>
                            </a-space>
                            <!-- 图标视图 行按钮 -->
                            <WidgetFormFileListButtons
                              v-show="file.status === 'done'"
                              :buttons="rowButtonsComputed"
                              display="block"
                              :collapseNumber="8"
                              @listButtonClicked="onListButtonClicked"
                              :file="file"
                              :fileIndex="i"
                            />
                          </template>
                          <a-button type="icon" size="small" class="widget-file-upload-icon-view-more">
                            <Icon type="pticon iconfont icon-ptkj-gengduocaozuo"></Icon>
                          </a-button>
                        </a-popover>
                      </template>
                      <div class="file-cancal-reupload">
                        <template v-if="file.status === 'error'">
                          <a-button
                            class="file-reupload"
                            size="small"
                            @click="handleReUpload(file)"
                            :title="$t('WidgetFormFileUpload.reupload', '重新上传')"
                          >
                            <Icon type="pticon iconfont icon-ptkj-shangchuan" />
                          </a-button>
                        </template>
                        <template v-if="true || file.status == 'uploading' || file.status === 'error'">
                          <a-button
                            size="small"
                            @click="handleCancelUpload(file, i)"
                            :title="$t('WidgetFormFileUpload.cancelUpload', '取消上传')"
                          >
                            <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                          </a-button>
                        </template>
                      </div>
                    </div>
                  </div>
                </a-col>
              </a-row>
            </template>
            <template v-else>
              <!-- 列表视图、表格视图 -->
              <a-table
                size="small"
                :pagination="false"
                :bordered="false"
                rowKey="uid"
                :showHeader="advancedFileListType == 'tableView'"
                :dataSource="fileList"
                :columns="currentFileListColumns"
                :rowSelection="rowSelection"
                :locale="locale"
                :customRow="customRow"
                :scroll="tableViewScroll"
                :class="`widget-file-upload-${advancedFileListType == 'tableView' ? 'table' : 'list'}-view`"
              >
                <template slot="fileNameSlot" slot-scope="text, record, index">
                  <div v-if="advancedFileListType == 'tableView'" :fileid="record.dbFile.fileID || record.dbFile.uuid">
                    <a-progress
                      v-if="record.showProgress && record.status == 'uploading'"
                      type="circle"
                      :percent="record.percent"
                      :width="14"
                      strokeColor="var(--w-primary-color)"
                      :strokeWidth="10"
                      :showInfo="false"
                      :status="record.status === 'error' ? 'exception' : 'active'"
                    />
                    <template v-else-if="record.status === 'error'">
                      <a-icon type="exclamation-circle" theme="filled" class="error-icon" />
                    </template>
                    <Icon :type="record.icon" />
                    <label
                      class="label-filename"
                      :title="record.name"
                      :style="{ color: record.status === 'error' ? 'var(--w-danger-color)' : '', cursor: fileNameHandler ? 'pointer' : '' }"
                      @click.stop="fileNameClick(record)"
                    >
                      {{ record.name }}
                    </label>
                  </div>
                  <template v-else>
                    <div class="list-view-item" :fileid="record.dbFile.fileID || record.dbFile.uuid">
                      <a-progress
                        v-if="record.showProgress && record.status == 'uploading'"
                        type="circle"
                        :percent="record.percent"
                        :width="14"
                        strokeColor="var(--w-primary-color)"
                        :strokeWidth="10"
                        :showInfo="false"
                        :status="record.status === 'error' ? 'exception' : 'active'"
                      />
                      <template v-else-if="record.status === 'error'">
                        <a-icon type="exclamation-circle" theme="filled" class="error-icon" />
                      </template>
                      <Icon v-else :type="record.icon" />
                      <label
                        class="label-filename"
                        :title="record.name"
                        :style="{
                          color: record.status === 'error' ? 'var(--w-danger-color)' : '',
                          cursor: fileNameHandler ? 'pointer' : ''
                        }"
                        @click.stop="fileNameClick(record)"
                      >
                        {{ record.name }}
                      </label>
                      <span class="list-view-extra-info" v-if="enabledFileSize || enabledCreateTime || enabledCreateUser">
                        (
                        <a-space :size="16">
                          <span class="file-item-desc" v-if="enabledFileSize">{{ record.formatSize }}</span>
                          <span class="file-item-desc" v-if="enabledCreateTime">{{ record.dbFile.createTimeStr }}</span>
                          <span class="file-item-desc" v-if="enabledCreateUser">
                            {{
                              record.orgFile
                                ? $i18n.locale !== 'zh_CN'
                                  ? _$USER.localUserName
                                  : record.dbFile.userName
                                : record.dbFile.userName
                            }}
                          </span>
                        </a-space>
                        )
                      </span>
                      <template v-if="record.status === 'error'">
                        <a-button type="link" size="small" @click="handleReUpload(record)">
                          {{ $t('WidgetFormFileUpload.reupload', '重新上传') }}
                        </a-button>
                      </template>
                      <template v-if="record.status == 'uploading' || record.status === 'error'">
                        <a-button type="link" size="small" @click="handleCancelUpload(record, index)">
                          {{ $t('WidgetFormFileUpload.cancelUpload', '取消上传') }}
                        </a-button>
                      </template>
                      <div class="file-list-item-buttons-absolute" v-if="record.status === 'done'">
                        <span class="file-list-item-buttons">
                          <!-- 列表视图 行按钮 -->
                          <WidgetFormFileListButtons
                            visibleTrigger="hover"
                            :buttons="rowButtonsComputed"
                            @listButtonClicked="onListButtonClicked"
                            :file="record"
                            :fileIndex="index"
                          />
                        </span>
                      </div>
                    </div>
                  </template>
                </template>
                <template slot="fileSizeSlot" slot-scope="text, record">{{ record.formatSize }}</template>
                <template slot="uploadTimeSlot" slot-scope="text, record">{{ record.dbFile.createTimeStr }}</template>
                <template slot="uploaderSlot" slot-scope="text, record">
                  {{ record.orgFile ? ($i18n.locale !== 'zh_CN' ? _$USER.localUserName : record.dbFile.userName) : record.dbFile.userName }}
                </template>
                <template slot="operationSlot" slot-scope="text, record, index">
                  <template v-if="record.status === 'error'">
                    <a-button type="link" size="small" @click="handleReUpload(record)">
                      {{ $t('WidgetFormFileUpload.reupload', '重新上传') }}
                    </a-button>
                  </template>
                  <template v-if="record.status == 'uploading' || record.status === 'error'">
                    <a-button type="link" size="small" @click="handleCancelUpload(record, index)">
                      {{ $t('WidgetFormFileUpload.cancelUpload', '取消上传') }}
                    </a-button>
                  </template>
                  <!-- 表格视图 行按钮 -->
                  <WidgetFormFileListButtons
                    v-show="record.status === 'done'"
                    display="inline"
                    :buttons="rowButtonsComputed"
                    @listButtonClicked="onListButtonClicked"
                    :file="record"
                    :fileIndex="index"
                  />
                </template>
              </a-table>
            </template>
          </a-card>
        </div>
      </template>
    </div>
    <a-modal
      dialogClass="file-rename-modal"
      :title="$t('WidgetFormFileUpload.rename', '重命名')"
      :visible="visibleRename"
      @ok="confirmRename"
      @cancel="visibleRename = false"
    >
      <a-descriptions :column="1">
        <a-descriptions-item :label="$t('WidgetFormFileUpload.filename', '文件名')">
          <a-input v-model="newFileName" />
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
    <a-modal
      :title="
        showLocalHistory
          ? $t('WidgetFormFileUpload.localHistoryText', '历史记录-本机历史文件')
          : $t('WidgetFormFileUpload.historyText', '历史记录')
      "
      :visible="visibleHistory"
      :width="800"
      :footer="null"
      :maskClosable="false"
      @cancel="cancelHistory"
    >
      <div class="file-history-tips" v-show="localHistoryFiles.length && !showLocalHistory">
        <span>{{ $t('WidgetFormFileUpload.noLocalHistoryTip', '发现您的本机上存在其他历史文件') }}</span>
        <a-button type="link" size="small" @click="lookLocalHistory">{{ $t('WidgetFormFileUpload.watchHistory', '查阅') }}</a-button>
      </div>
      <div class="file-history-tips" v-show="showLocalHistory">
        {{ $t('WidgetFormFileUpload.unableViewNotLocalHistory', '查阅本机历本机历史文件保存在您的本机，非本机上无法查看!') }}
      </div>
      <a-table
        size="small"
        :pagination="false"
        :bordered="true"
        :rowKey="showLocalHistory ? 'url' : 'fileID'"
        :dataSource="fileHistoryList"
        :columns="vHistoryColumns"
        :loading="loadingHistory"
        :locale="locale"
        class="file-history-table"
      >
        <template slot="urlSlot" slot-scope="text, record, index">
          <div class="local-history-path">{{ text }}</div>
        </template>
        <template slot="operationSlot" slot-scope="text, record, index">
          <a-button size="small" @click="handleOpenHistory(record, index)">{{ $t('WidgetFormFileUpload.open', '打开') }}</a-button>
          <a-button v-show="showLocalHistory" size="small" @click="$evt => handleCopyLocalPath(record, index, $evt)">
            {{ $t('WidgetFormFileUpload.copyPath', '复制路径') }}
          </a-button>
        </template>
      </a-table>
    </a-modal>
    <a-modal
      :title="$t('WidgetFormFileUpload.addPicture', '添加图片')"
      :visible="visibleLib"
      :width="800"
      :maskClosable="false"
      dialogClass="picture-add-dialog"
      @cancel="visibleLib = false"
    >
      <template slot="footer">
        <template v-if="pictureTabKey == 'pic-lib-panel'">
          <a-button @click="visibleLib = false">{{ $t('WidgetFormFileUpload.cancel', '取消') }}</a-button>
          <a-button type="primary" @click="confirmSelectPicture">{{ $t('WidgetFormFileUpload.confirm', '确定') }}</a-button>
        </template>
        <template v-else>
          <a-button type="primary" @click="confirmUploadPicture">{{ $t('WidgetFormFileUpload.confirmUpload', '确定上传') }}</a-button>
        </template>
      </template>
      <a-tabs type="card" v-model="pictureTabKey" v-show="pictureTabs">
        <a-tab-pane key="pic-lib-panel" :tab="$t('WidgetFormFileUpload.pictureLibrary', '图片库')"></a-tab-pane>
        <a-tab-pane key="pic-upload-panel" :tab="$t('WidgetFormFileUpload.localUpload', '本地上传')"></a-tab-pane>
      </a-tabs>

      <a-row :gutter="16" v-show="pictureTabKey == 'pic-lib-panel'">
        <a-col :span="6" class="picture-selected-categories">
          <div class="panel-head">{{ $t('WidgetFormFileUpload.pictureLibraryCategory', '图片库分类') }}</div>
          <PerfectScrollbar class="picture-categories-ps">
            <div
              v-for="(item, index) in selectedCategories"
              :key="item.uuid"
              :title="$t(item.uuid, item.name)"
              :class="{ 'picture-category-item': true, 'picture-category-active': curCategory == item.uuid }"
              @click="handleCategory(item)"
            >
              <span class="msgIconShow" :style="{ backgroundColor: item.color }">
                <Icon :type="item.icon || 'pticon iconfont icon-ptkj-qiehuanshitu'" />
              </span>
              <div class="category-name">{{ $t(item.uuid, item.name) }}</div>
              <span>({{ selectedPictureMap[item.uuid] ? selectedPictureMap[item.uuid]['length'] : '0' }}/{{ item.fileIDs.length }})</span>
            </div>
          </PerfectScrollbar>
        </a-col>
        <a-col :span="18">
          <div class="panel-head">
            <a-button v-show="pictureTabs" size="small" type="danger" style="margin-bottom: 10px" @click="handleDelImgs">
              <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
              {{ $t('WidgetFormFileUpload.delete', '删除') }}
            </a-button>
            <span v-show="!pictureTabs">{{ $t('WidgetFormFileUpload.pictureList', '图片列表') }}</span>
          </div>
          <a-row :gutter="16">
            <PerfectScrollbar class="category-imgs-ps">
              <a-col :span="8" v-for="(item, index) in selectedCategoryImgs" :key="item.uuid">
                <a-card
                  :hoverable="true"
                  @click="handleSelectedPicture(item)"
                  class="picture-item-card"
                  :class="{ 'picture-item-selected-card': selectedFileIds.includes(item.fileID) }"
                >
                  <img slot="cover" :src="`/repository/file/mongo/download?fileID=${item.fileID}`" />
                  <a-card-meta :title="item.fileName">
                    <template slot="description">
                      <p>
                        {{ item.fileName.split('.')[item.fileName.split('.').length - 1].toUpperCase() }}/{{ formatSize(item.fileSize) }}
                      </p>
                      <span>{{ item.width }}x{{ item.height }}px</span>
                    </template>
                  </a-card-meta>
                </a-card>
              </a-col>
            </PerfectScrollbar>
          </a-row>
        </a-col>
      </a-row>
      <div class="pic-upload-panel" v-show="pictureTabKey == 'pic-upload-panel'">
        <a-descriptions :column="1" :colon="false">
          <a-descriptions-item :label="$t('WidgetFormFileUpload.savePicLibrary', '保存图片库')">
            <a-switch v-model="saveToPictureLib" style="margin-right: 16px" />
            {{ $t('WidgetFormFileUpload.switchPicLibraryTip', '开启时，图片会先保存至图片库，再上传') }}
          </a-descriptions-item>
          <a-descriptions-item v-if="saveToPictureLib">
            <span slot="label" class="ant-form-item-required">
              {{ $t('WidgetFormFileUpload.selectPictureCategory', '选择图片库分类') }}
            </span>
            <a-select :allowClear="true" mode="multiple" v-model="categoryUuids" style="width: 500px" showArrow>
              <a-select-option v-for="item in selectedCategories" :key="item.uuid" :value="item.uuid" :title="$t(item.uuid, item.name)">
                {{ $t(item.uuid, item.name) }}
              </a-select-option>
            </a-select>
          </a-descriptions-item>
          <a-descriptions-item>
            <span slot="label" :class="['pic-upload-label', saveToPictureLib ? 'save-to-lib' : '']">
              {{ $t('WidgetFormFileUpload.uploadPic', '上传图片') }}
            </span>
            <PerfectScrollbar class="picture-local-upload-ps" :style="{ height: saveToPictureLib ? '300px' : '' }">
              <a-upload
                :customRequest="options => customImageRequest(options, true)"
                list-type="picture-card"
                :file-list="pictureFileList"
                :showUploadList="showUploadListPicture"
                @preview="onClickPreviewPicture"
                @change="args => onFileUploadChange(args, true)"
                :beforeUpload="onBeforeUpload"
                :accept="accept"
                @download="onClickDownloadFile"
              >
                <div>
                  <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                  <div class="ant-upload-text">{{ $t('WidgetFormFileUpload.clickUpload', '点击添加') }}</div>
                </div>
              </a-upload>
            </PerfectScrollbar>
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-modal>
  </a-form-model-item>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import { preview, getFileIcon } from '@framework/vue/lib/preview/filePreviewApi';
import { OfficeApi } from '@framework/vue/lib/office/officeApi';
import {
  generateId,
  copyToClipboard,
  getCookie,
  swapArrayElements,
  deepClone,
  addWatermarksToImageFile,
  compressImage,
  queryStringify
} from '@framework/vue/utils/util';
import formMixin from '../mixin/form-common.mixin';
import SparkMD5 from 'spark-md5';
import moment from 'moment';
import 'viewerjs/dist/viewer.min.css';
import './css/file-upload.less';
import { api as viewerApi } from 'v-viewer';
import { advancedFileListTypeOptions, fileSignatures } from '../commons/constant';
import { Empty } from 'ant-design-vue';
import { each, some, map, findIndex, filter, every, pick, template as stringTemplate } from 'lodash';
import JSZip from 'jszip';

const UPLOAD_STATUS = { UPLOADING: 'uploading', DONE: 'done', ERROR: 'error', REMOVED: 'removed' };

export default {
  extends: FormElement,
  name: 'WidgetFormFileUpload',
  mixins: [widgetMixin, formMixin],
  props: {
    pbRecord: {
      type: Boolean,
      default: true // 保留痕迹
    },
    localFileReadonly: {
      type: Boolean,
      default: true // 本地文件只读
    }
  },
  inject: ['locale', '$workView'],
  data() {
    let fileList = [];
    let fileUploadMap = {};

    let extensionButtons = []; //TODO: 扩展按钮取自列表附件配置功能
    return {
      chunkSize: 2 * 1024 * 1024, // 1024 字节 = 1KB
      fileList,
      md5FileObj: {},
      fileUploadMap,
      extensionButtons,
      previewServerHost: '', // 预览服务地址
      fileRepositoryServerHost: '',
      images: [],
      emptyImg: Empty.PRESENTED_IMAGE_SIMPLE,
      advancedFileListType: this.widget.configuration.advancedFileListType,
      rowSelection: {
        columnWidth: 30,
        selectedRowKeys: [],
        selectedRows: [],
        onChange: this.onSelectFileRowChange
      },
      regExps: {},
      fileListColumns: [
        {
          title: this.$t('WidgetFormFileUpload.fileList.column.filename', '文件名'),
          dataIndex: 'fileName',
          scopedSlots: { customRender: 'fileNameSlot' }
        },
        {
          title: this.$t('WidgetFormFileUpload.fileList.column.fileSize', '大小'),
          dataIndex: 'fileSize',
          width: 100,
          scopedSlots: { customRender: 'fileSizeSlot' }
        },
        {
          title: this.$t('WidgetFormFileUpload.fileList.column.uploadTime', '上传时间'),
          dataIndex: 'uploadTime',
          width: 150,
          scopedSlots: { customRender: 'uploadTimeSlot' }
        },
        {
          title: this.$t('WidgetFormFileUpload.fileList.column.uploader', '上传人'),
          dataIndex: 'uploader',
          width: 100,
          scopedSlots: { customRender: 'uploaderSlot' }
        },
        {
          title: this.$t('WidgetFormFileUpload.fileList.column.operation', '操作'),
          dataIndex: 'operation',
          width: 300,
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      // locale: {
      //   // emptyText: <a-empty description="未上传附件" image={Empty.PRESENTED_IMAGE_SIMPLE} />
      // },
      advancedFileListTypeOptions, // 高级视图备选项
      fileSourceOptions: [], // 附件来源
      cancelSourceList: [],
      dyFormData: undefined,
      curButton: undefined,
      wellOfficeEnable: true,
      visibleRename: false, // 是否显示重命名弹窗
      newFileName: '',
      loadingHistory: false,
      visibleHistory: false,
      fileHistoryList: [], // 历史记录
      showLocalHistory: false,
      localHistoryFiles: [],
      curLocalFileReadonly: false,
      fileMultiple: false,
      replaceTemp: undefined,
      visibleLib: false,
      pictureDisabled: false,
      selectedCategories: [],
      selectedCategoryImgs: [],
      curCategory: '',
      selectedPictureMap: {},
      pictureTabKey: 'pic-lib-panel',
      pictureTabs: false,
      saveToPictureLib: false,
      categoryUuids: [],
      pictureFileList: [],
      openFileDialogOnClick: true,
      materialAccept: [],
      userNameMap: [], // 存放用户名
      fileNameHandler: '', // 系统参数获取的文件名点击事件
      fileNameHandlerName: '', // 系统参数获取的文件名点击事件
      headerButton: this.widget.configuration.headerButton || [],
      rowButtons: this.widget.configuration.rowButton || []
    };
  },
  computed: {
    pictureTips() {
      let tips = [];
      if (this.configuration.pixelCheck && this.configuration.pixelWidth && this.configuration.pixelHeight) {
        const picturePixelTip = `${this.configuration.pixelWidth}px*${this.configuration.pixelHeight}px`;
        tips.push(this.$t('WidgetFormFileUpload.picturePixelTip', { value: picturePixelTip }, `尺寸：${picturePixelTip}`));
      }
      if (tips.length) {
        return this.tips + '; ' + tips.join('; ');
      } else {
        return this.tips;
      }
    },
    tips() {
      let tips = [];
      if (this.configuration.fileLimitNum) {
        tips.push(
          this.$t(
            'WidgetFormFileUpload.fileLimitNumMax',
            { num: this.configuration.fileLimitNum },
            `附件最多${this.configuration.fileLimitNum}个`
          )
        );
      }
      if (this.fileSizeLimit != -1) {
        const fileLimitSizeMax = this.configuration.fileSizeLimit + this.configuration.fileSizeLimitUnit;
        tips.push(this.$t('WidgetFormFileUpload.fileLimitSizeMax', { value: fileLimitSizeMax }, `附件大小：不超过${fileLimitSizeMax}`));
      }
      if (this.accept) {
        tips.push(this.$t('WidgetFormFileUpload.fileLimitAccept', { value: this.accept }, `支持附件格式：${this.accept}`));
      }
      if (tips.length) {
        return tips.join('; ');
      } else {
        return '';
      }
    },
    pictureClassName() {
      let className = {
        'picture-for-lib': this.pictureForLib,
        'picture-clear-both': this.isclearBoth
      };
      let pictureView = this.configuration.pictureView || 'picture-card';

      className[`picture-view-type-${pictureView}`] = true;
      return className;
    },
    isclearBoth() {
      let clear = false;
      if (this.configuration.enabledPictureAutoWrap && this.configuration.pictureAutoWrapCount && this.fileList.length) {
        if (this.fileList.length % this.configuration.pictureAutoWrapCount === 0) {
          clear = true;
        }
      }
      return clear;
    },
    enabledFileSize() {
      let enabled = true;
      if (this.configuration.enabledFileSize === false) {
        enabled = false;
      }
      return enabled;
    },
    enabledCreateTime() {
      let enabled = true;
      if (this.configuration.enabledCreateTime === false) {
        enabled = false;
      }
      return enabled;
    },
    enabledCreateUser() {
      let enabled = true;
      if (this.configuration.enabledCreateUser === false) {
        enabled = false;
      }
      return enabled;
    },
    // 简易视图是否显示上传
    showSimpleUpload() {
      let showUpload = true;
      const headerButton = this.widget.configuration.headerButton;
      if (headerButton && headerButton.length) {
        const uploadBtn = headerButton.find(item => item.code === 'onClickUpload');
        if (uploadBtn) {
          showUpload = uploadBtn.defaultFlag;
        }
      }
      return showUpload;
    },
    showPictureAdd() {
      let show = true;
      const headerButton = this.widget.configuration.headerButton;
      show = headerButton[0].defaultFlag;
      if (!this.editable) {
        // 不可编辑状态下不显示添加
        show = false;
      } else if (this.isReplaceImgByLimitOne) {
        show = false;
      }
      return show;
    },
    // 图片是否能预览
    showPicturePreview() {
      let preview = false;
      const rowButton = this.widget.configuration.rowButton;
      if (rowButton && rowButton.length) {
        const previewBtn = rowButton.find(item => item.code.indexOf('Preview') > -1);
        preview = previewBtn.defaultFlag;
      }
      return preview;
    },
    // 图片按钮
    showUploadListPicture() {
      let showUploadList = { showPreviewIcon: false, showDownloadIcon: false, showRemoveIcon: false };
      const rowButton = this.widget.configuration.rowButton;
      if (rowButton && rowButton.length > 0) {
        showUploadList.showDownloadIcon = rowButton[1].defaultFlag;
        showUploadList.showRemoveIcon = rowButton[2].defaultFlag;
      }
      if (!this.editable) {
        // 不可编辑状态下不显示删除
        showUploadList.showRemoveIcon = false;
      } else if (this.isReplaceImgByLimitOne) {
        showUploadList.showUploadIcon = true;
      }
      if (this.configuration.pictureView === 'picture') {
        showUploadList.showPreviewIcon = this.showPicturePreview;
      }
      return showUploadList;
    },
    // 高级视图类型
    // advancedFileListType: {
    //   get: function () {
    //     return this.widget.configuration.advancedFileListType;
    //   },
    //   set: function (v) {
    //     this.widget.configuration.advancedFileListType = v;
    //   }
    // },
    simpleListRowButtons() {
      // TODO: 根据配置按钮返回
      return [
        { title: '下载', type: 'default', loadding: false, method: 'onClickDownloadFile' },
        { title: '删除', type: 'default', loadding: false, method: 'onClickDelFile' },
        { title: '预览', type: 'default', loadding: false, method: 'onClickPreviewFile' },
        { title: '复制名称', type: 'default', loadding: false, method: 'onClickCopyFilename' }
      ];
    },
    advancedListHeaderButtons() {
      return [
        { title: '全选', loadding: false, method: 'onAdvancedSelectAll' },
        { title: '添加附件', loadding: false, method: 'onAdvancedAddFile' },
        { title: '新建正文', loadding: false, method: 'onNewText' },
        { title: '导入正文', loadding: false },
        { title: '粘贴附件', loadding: false, method: 'onPasteFile' },
        { title: '编辑', loadding: false },
        { title: '替换', loadding: false },
        { title: '重命名', loadding: false },
        { title: '上移', loadding: false, method: 'onAdvancedListMoveUpItem' },
        { title: '下移', loadding: false, method: 'onAdvancedListMoveDownItem' },
        { title: '盖章', loadding: false },
        { title: '批量下载', loadding: false, method: 'onAdvancedBatchDownload' },
        { title: '批量删除', loadding: false, method: 'onAdvancedBatchDel' }
      ];
    },
    advancedListRowButtons() {
      return [
        { title: '预览', loadding: false, method: 'onClickPreviewFile' },
        { title: '下载', loadding: false, method: 'onClickDownloadFile' },
        { title: '删除', loadding: false, method: 'onClickDelFile' },
        { title: '历史记录', loadding: false }
      ];
    },
    iconViewPopverButtons() {
      return [
        { title: '预览', loadding: false, method: 'onClickPreviewFile' },
        { title: '下载', loadding: false, method: 'onClickDownloadFile' },
        { title: '删除', loadding: false, method: 'onClickDelFile' },
        { title: '历史记录', loadding: false }
      ];
    },
    fileSizeLimit() {
      if (this.widget.configuration.fileSizeLimit != null) {
        return (
          parseInt(this.widget.configuration.fileSizeLimit) *
          Math.pow(1024, ['KB', 'MB', 'G'].indexOf(this.widget.configuration.fileSizeLimitUnit) + 1)
        );
      }
      return -1;
    },
    // 上传的文件类型
    accept() {
      let accepts = this.widget.configuration.accept;
      if (this.materialAccept.length) {
        accepts = this.materialAccept;
      }
      for (let i = 0, len = accepts.length; i < len; i++) {
        if (accepts[i].indexOf('.') != 0) {
          //修正文件后缀
          accepts[i] = '.' + accepts[i];
        }
      }
      return accepts.length ? accepts.join(',') : '';
    },
    unselectAll() {
      return this.rowSelection.selectedRowKeys.length == this.fileList.length && this.rowSelection.selectedRowKeys.length > 0;
    },
    // 当前 列表视图、表格视图 的显示列
    currentFileListColumns() {
      if (this.advancedFileListType === 'tableView') {
        let fileListColumns = JSON.parse(JSON.stringify(this.fileListColumns));
        if (this.configuration.enabledFileSize === false) {
          fileListColumns.splice(
            fileListColumns.findIndex(col => col.dataIndex === 'fileSize'),
            1
          );
        }
        if (this.configuration.enabledCreateTime === false) {
          fileListColumns.splice(
            fileListColumns.findIndex(col => col.dataIndex === 'uploadTime'),
            1
          );
        }
        if (this.configuration.enabledCreateUser === false) {
          fileListColumns.splice(
            fileListColumns.findIndex(col => col.dataIndex === 'uploader'),
            1
          );
        }
        return fileListColumns;
      } else {
        return [this.fileListColumns[0]];
      }
    },
    tableViewScroll() {
      let scroll = {};
      if (this.advancedFileListType === 'tableView') {
        let x = 0;
        this.fileListColumns.forEach(col => {
          if (col.width) {
            x += col.width;
          } else {
            x += 150;
          }
        });
        scroll.x = x;
      }
      return scroll;
    },
    // 当前选中视图名称
    currentSelectFileViewTypeName() {
      return { listView: '列表视图', tableView: '表格视图', iconView: '图标视图' }[this.advancedFileListType];
    },
    // 视图列表
    fileViewList() {
      const advancedViewList = this.widget.configuration.advancedViewList;
      return this.advancedFileListTypeOptions.filter(item => {
        item.icon = this.getFileViewTypeIcon(item.value);
        return advancedViewList.includes(item.value);
      });
    },
    // 头部按钮
    headerButtonComputed() {
      let headerButton = [],
        btnShowType = 'show';
      if (this.editable) {
        // 编辑状态下 编辑类和显示类按钮都显示，除非按钮关闭
        headerButton = this.headerButton.filter(item => {
          return item.defaultFlag;
        });
      } else {
        // 不可编辑状态下 只能显示“显示类”按钮
        headerButton = this.headerButton.filter(item => {
          return item.defaultFlag && btnShowType === item.btnShowType;
        });
      }
      if (this.configuration.type == 'simpleList' && !this.designMode) {
        headerButton = headerButton.filter(item => {
          return item.code != 'onClickUpload';
        });
      }
      const selectAllIndex = headerButton.findIndex(item => item.code === 'onAdvancedSelectAll');
      if (selectAllIndex !== -1) {
        headerButton.splice(selectAllIndex, 1);
      }
      if (
        headerButton.findIndex(
          item => (item.code === 'onAdvancedBatchDownload' || item.code === 'onAdvancedBatchDel') && item.defaultFlag
        ) > -1 &&
        this.configuration.type === 'advancedList'
      ) {
        headerButton.unshift({
          id: 'onAdvancedSelectAll',
          code: 'onAdvancedSelectAll',
          buttonName: this.$t('WidgetFormFileUpload.button.selectAll', '全选'),
          defaultFlag: true
        });
      }

      return headerButton;
    },
    // 图片上传头部按钮（排除添加按钮）
    pictureHeaderButton() {
      return this.headerButtonComputed.filter(item => item.code !== 'onAddPicture');
    },
    // 图片上传是否显示批量下载
    showPictureBatchDownload() {
      let show = false;
      const hasIndex = this.pictureHeaderButton.findIndex(item => item.code === 'onPictureBatchDownload');
      if (hasIndex > -1) {
        show = true;
      }
      return show;
    },
    // 行按钮
    rowButtonsComputed() {
      let rowButton = [],
        btnShowType = 'show';
      if (this.editable) {
        rowButton = this.rowButtons.filter(item => {
          return item.defaultFlag;
        });
      } else {
        rowButton = this.rowButtons.filter(item => {
          return item.defaultFlag && btnShowType === item.btnShowType;
        });
      }
      if (this.configuration.pictureView === 'picture') {
        const pictureViewIconMap = {
          onClickPreviewPicture: 'pticon iconfont icon-wsbs-xianshi',
          onClickDownloadFile: 'pticon iconfont icon-ptkj-xiazai',
          onClickDelFile: 'pticon iconfont icon-ptkj-shanchu'
        };
        rowButton.forEach(item => {
          if (!item.style) {
            item.style = {};
          }
          if (item.style) {
            item.style.icon = pictureViewIconMap[item.code];
            item.style.btnType = 'icon';
          }
        });
      }
      return rowButton;
    },
    isReplaceImgByLimitOne() {
      return this.widget.configuration.type == 'picture' && this.widget.configuration.fileLimitNum === 1 && this.fileList.length != 0;
    },
    // 是否正文
    isText() {
      return this.widget.configuration.fileNormalOrText == 'text';
    },
    pictureForLib() {
      return this.configuration.type == 'picture' && this.configuration.pictureUploadMode == 'lib';
    },
    selectedFileIds() {
      let ids = [];
      for (const key in this.selectedPictureMap) {
        const item = this.selectedPictureMap[key];
        if (key == this.curCategory) {
          for (let index = 0; index < item.length; index++) {
            ids.push(item[index]['fileID']);
          }
        }
      }
      return ids;
    },

    vHistoryColumns() {
      let columns = [];
      let local = [
        {
          title: this.$t('WidgetFormFileUpload.history.column.createTime', '创建时间'),
          dataIndex: 'timestamp',
          width: 150,
          scopedSlots: { customRender: 'timestampSlot' }
        },
        {
          title: this.$t('WidgetFormFileUpload.history.column.filename', '文件名'),
          dataIndex: 'name',
          width: 150,
          ellipsis: true,
          scopedSlots: { customRender: 'nameSlot' }
        },
        {
          title: this.$t('WidgetFormFileUpload.history.column.localPath', '本机路径'),
          dataIndex: 'url',
          scopedSlots: { customRender: 'urlSlot' }
        },
        {
          title: this.$t('WidgetFormFileUpload.history.column.operation', '操作'),
          dataIndex: 'operation',
          width: 150,
          scopedSlots: { customRender: 'operationSlot' }
        }
      ];
      let remote = [
        {
          title: this.$t('WidgetFormFileUpload.history.column.createTime', '创建时间'),
          dataIndex: 'timestamp',
          width: 150,
          scopedSlots: { customRender: 'timestampSlot' }
        },
        {
          title: this.$t('WidgetFormFileUpload.history.column.creator', '创建人'),
          dataIndex: 'creator',
          width: 150,
          scopedSlots: { customRender: 'creatorSlot' }
        },
        {
          title: this.$t('WidgetFormFileUpload.history.column.operationType', '操作类型'),
          dataIndex: 'source',
          width: 100,
          scopedSlots: { customRender: 'sourceSlot' }
        },
        {
          title: this.$t('WidgetFormFileUpload.history.column.filename', '文件名'),
          dataIndex: 'name',
          ellipsis: true,
          scopedSlots: { customRender: 'nameSlot' }
        },
        {
          title: this.$t('WidgetFormFileUpload.history.column.operation', '操作'),
          dataIndex: 'operation',
          width: 100,
          scopedSlots: { customRender: 'operationSlot' }
        }
      ];

      columns = this.showLocalHistory ? local : remote;
      if (!this.enabledCreateTime) {
        columns.splice(
          columns.findIndex(col => col.dataIndex === 'timestamp'),
          1
        );
      }
      if (!this.showLocalHistory && !this.enabledCreateUser) {
        columns.splice(
          columns.findIndex(col => col.dataIndex === 'creator'),
          1
        );
      }
      return columns;
    }
  },
  created() {
    this.curLocalFileReadonly = this.readonly;
    this.widgetCreatedButtonSetting();
    this.fileNameClick(); // 获取系统参数配置的文件名点击事件
    if (this.formData[this.fieldCode] === undefined) {
      this.formData[this.fieldCode] = [];
    }
    if (this.configuration.type === 'picture') {
      if (this.configuration.enabledPictureWatermark) {
        const { watermarkConfig } = this.getPictureWatermarkConfig();
        this.watermarkConfig = watermarkConfig;
      }
    }
  },
  beforeMount() {},
  mounted() {
    this.getChunkSize();
    this.getPreviewHostServer();
    this.getGlobalWatermarkDisabled();
    let dataValue = this.formData[this.fieldCode];
    if (dataValue && dataValue.length) {
      let creatorIds = [];
      for (let i = 0, len = dataValue.length; i < len; i++) {
        if (dataValue[i].creator && !dataValue[i].userName) {
          this.$set(dataValue[i], 'userName', '...');
          creatorIds.push(dataValue[i].creator);
        }
        this.addFileList({
          uid: dataValue[i].fileID,
          name: dataValue[i].fileName || dataValue[i].filename,
          size: dataValue[i].fileSize,
          status: UPLOAD_STATUS.DONE,
          dbFile: dataValue[i]
        });
      }
      if (creatorIds.length > 0) {
        this.fetchFileCreatorName(creatorIds, dataValue);
      }
    }
    if (this.widget.configuration.fileSourceExtend) {
      this.fetchFileSource();
    }
    if (this.pictureForLib) {
      this.openFileDialogOnClick = false;
      this.$el.querySelector('.picture-for-lib .ant-upload-select').addEventListener('click', () => {
        this.openPictureLib();
      });
      if (this.configuration.pictureLibManage) {
        this.pictureTabs = true;
      }
    }
    // 关联材料可上传的文件类型
    this.fetchRelatedMaterialAccept();
    if (this.configuration.type === 'picture' && this.configuration.pictureView === 'picture-card') {
      let pictureTips = this.tips;
      if (this.configuration.pixelCheck) {
      }
      if (this.pictureTips) {
        const cardWrapper = this.$el.querySelector('.ant-upload-picture-card-wrapper');
        if (!cardWrapper.querySelector('.picture-card-tips')) {
          const tipsNode = document.createElement('span');
          tipsNode.setAttribute('class', 'picture-card-tips');
          tipsNode.innerHTML = this.pictureTips;
          cardWrapper.appendChild(tipsNode);
        }
      }
    }
  },
  methods: {
    widgetCreatedButtonSetting() {
      if (!this.designMode && EASY_ENV_IS_BROWSER) {
        this.$tempStorage.getCache(
          'app.fileupload.welloffice.enable',
          () => {
            return new Promise((resolve, reject) => {
              this.$clientCommonApi.getSystemParamValue(
                'app.fileupload.welloffice.enable',
                wellofficeEnable => {
                  resolve(wellofficeEnable);
                },
                error => {
                  reject(error);
                }
              );
            });
          },
          wellofficeEnable => {
            if (wellofficeEnable == 'false') {
              this.wellOfficeEnable = false;
              const buttonIndex = this.headerButton.findIndex(item => item.id == 'onPasteFile' || item.code == 'onPasteFile');
              if (buttonIndex != -1) {
                this.headerButton.splice(buttonIndex, 1);
              }
              const rowButtonIndex = this.rowButtons.findIndex(item => item.id == 'onClickSaveAs' || item.code == 'onClickSaveAs');
              if (rowButtonIndex != -1) {
                this.rowButtons.splice(rowButtonIndex, 1);
              }
            }
          }
        );
      }
      // 从表的字段规则：由外部定义传入
      if (this.form.formElementRules && this.form.formElementRules[this.widget.id] && this.form.formElementRules[this.widget.id].buttons) {
        if (this.form.formElementRules[this.widget.id].buttons) {
          let headerButton = this.form.formElementRules[this.widget.id].buttons.headerButton;
          let headerButtonArr = headerButton ? headerButton.split(';') : [];
          let rowButton = this.form.formElementRules[this.widget.id].buttons.rowButton;
          let rowButtonArr = rowButton ? rowButton.split(';') : [];
          // 按钮设置
          each(this.widget.configuration.headerButton, item => {
            if (headerButtonArr.indexOf(item.code || item.id) > -1) {
              item.defaultFlag = true;
            } else {
              item.defaultFlag = false;
            }
          });
          each(this.widget.configuration.rowButton, item => {
            if (rowButtonArr.indexOf(item.code || item.id) > -1) {
              item.defaultFlag = true;
            } else {
              item.defaultFlag = false;
            }
          });
        }
      }
    },
    // 图片库提交前校验
    picLibBeforeUpload(files, isLocal) {
      let promise = [];
      for (let index = 0, len = files.length; index < len; index++) {
        let file = files[index];
        promise.push(
          new Promise((resolve, reject) => {
            file.uid = file.uid ? file.uid : file.uuid;
            file.name = file.name ? file.name : file.fileName;
            file.size = file.size ? file.size : file.fileSize;
            if (
              !this.checkFileNameRepeat(
                file,
                filter(files, (item, idx) => {
                  return idx != index;
                })
              )
            ) {
              this.$message.error(this.$t('WidgetFormFileUpload.message.notAllowUploadSameName', '不允许上传重名'));
              reject();
              return;
            } else {
              this.onBeforeUpload(isLocal ? file.orgFile : file, files)
                .then(() => {
                  resolve();
                })
                .catch(() => {
                  reject();
                });
              return;
            }
            resolve();
          })
        );
      }
      return Promise.all(promise);
    },
    // 确定上传
    confirmUploadPicture() {
      if (this.pictureFileList.length) {
        this.picLibBeforeUpload(this.pictureFileList, true).then(() => {
          if (this.saveToPictureLib && !this.categoryUuids.length) {
            this.$message.error(this.$t('WidgetFormFileUpload.message.pleaseSelectCategory', '请选择分类'));
            return;
          }
          let fileIDs = [];
          for (let index = 0; index < this.pictureFileList.length; index++) {
            const item = this.pictureFileList[index];
            if (item.url) {
              item.uid = item.url.split('=')[1];
            }
            fileIDs.push(item.dbFile.fileID);
            this.addFileList(item);
          }
          if (this.saveToPictureLib && this.categoryUuids.length && fileIDs.length) {
            for (let index = 0; index < this.categoryUuids.length; index++) {
              const categoryUuid = this.categoryUuids[index];
              this.addImgsByCategoryUuid(categoryUuid, fileIDs);
            }
          }
          this.visibleLib = false;
        });
      }
    },
    // 选择图片
    handleSelectedPicture(item) {
      const index = findIndex(this.selectedPictureMap[this.curCategory], pic => pic.fileID == item.fileID);
      if (index == -1) {
        this.selectedPictureMap[this.curCategory].push(item);
      } else {
        this.selectedPictureMap[this.curCategory].splice(index, 1);
      }
    },
    // 点击分类
    handleCategory(item) {
      this.curCategory = item.uuid;
      this.queryImgsByCategoryUuid(item.uuid);
      if (this.selectedPictureMap[item.uuid] == undefined) {
        this.$set(this.selectedPictureMap, item.uuid, []);
      }
    },
    // 确定选择图片
    confirmSelectPicture() {
      let files = [];
      for (let index = 0; index < this.selectedCategories.length; index++) {
        const category = this.selectedCategories[index];
        const selectedFiles = this.selectedPictureMap[category.uuid];
        if (selectedFiles) {
          files = files.concat(selectedFiles);
        }
      }
      this.picLibBeforeUpload(files)
        .then(() => {
          this.addDbFiles(files);
          this.visibleLib = false;
        })
        .catch(() => {});
    },
    resetLibConfig() {
      this.selectedPictureMap = {};
      this.pictureTabKey = 'pic-lib-panel';
      this.saveToPictureLib = false;
      this.categoryUuids = [];
      this.pictureFileList = [];
    },
    // 打开图片库
    openPictureLib() {
      this.visibleLib = true;
      this.resetLibConfig();
      this.$axios.get('/basicdata/img/category/queryAllCategory').then(res => {
        const result = res.data;
        if (result && result.code == '0') {
          result.data.sort(function (a, b) {
            return a.code.localeCompare(b.code);
          });
          let selectedCategories = [];
          for (let index = 0; index < result.data.length; index++) {
            const item = result.data[index];
            if (!this.designMode) {
              if (item.i18ns && item.i18ns.length) {
                let i18n = this.i18nsToI18n(item.i18ns, 'defId');
                this.mergeWidgetI18nMessages(i18n);
              }
            }
            if (this.configuration.pictureLib.includes(item.uuid)) {
              selectedCategories.push(item);
            }
          }
          this.selectedCategories = selectedCategories;
          if (selectedCategories.length > 0) {
            this.handleCategory(selectedCategories[0]);
          }
        }
      });
    },
    // 查询分类下的图片
    queryImgsByCategoryUuid(uuid) {
      this.$axios.get(`/basicdata/img/category/queryImgs/${uuid}`).then(({ data }) => {
        if (data && data.code == '0') {
          this.selectedCategoryImgs = data.data;
          let hasIndex = findIndex(this.selectedCategories, { uuid: uuid });
          if (hasIndex > -1) {
            this.$set(this.selectedCategories[hasIndex], 'fileIDs', map(data.data, 'fileID'));
          }
        }
      });
    },
    // 点击删除
    handleDelImgs() {
      this.delImgsByCategoryUuid(this.curCategory, this.selectedFileIds);
    },
    // 删除分类下的图片
    delImgsByCategoryUuid(uuid, fileIDs) {
      this.$axios
        .post(`/basicdata/img/category/delImgs/${uuid}`, {
          fileIDs: JSON.stringify(fileIDs)
        })
        .then(({ data }) => {
          this.$set(this.selectedPictureMap, uuid, []);
          this.queryImgsByCategoryUuid(uuid);
        });
    },
    // 添加图片到图片库分类
    addImgsByCategoryUuid(uuid, fileIDs) {
      this.$axios
        .post(`/basicdata/img/category/addImgs/${uuid}`, {
          fileIDs: JSON.stringify(fileIDs)
        })
        .then(({ data }) => {});
    },
    onClickUpload() {
      this.curButton = {
        button: this.configuration.headerButton[0]
      };
    },
    getTitle(formData) {
      return new Promise((resolve, reject) => {
        $axios.post(`/proxy/api/dyform/data/getDyformTitle`, formData).then(({ data }) => {
          if (data.code == 0) {
            resolve(data.data);
          }
        });
      });
    },

    handleCancelUpload(file, fileIndex) {
      this.cancelSourceList.forEach(source => {
        source.cancel('取消请求');
      });
      this.cancelSourceList = [];
      this.onClickDelFile(file, fileIndex);
    },
    handleReUpload(file) {
      file.status = UPLOAD_STATUS.UPLOADING;
      const options = {
        file: file.orgFile
      };
      let _this = this;
      this.computeFileMD5(options.file, function () {
        // 校验文件是否存在，如果已经存在，则上传直接返回删除
        _this.loadFileChunkInfoByMD5(options.file, function (result) {
          _this.upload(options.file, result, function (_file) {
            if (_this.formData[_this.fieldCode] == null) {
              _this.formData[_this.fieldCode] = [];
            }
            _this.formData[_this.fieldCode].push(_file.dbFile);
            setTimeout(function () {
              // 进度条100%后延迟一点关闭
              _file.showProgress = false;
              options.onSuccess && options.onSuccess();
              _this.emitChange();
            }, 300);
          });
        });
      });
    },
    setPictureZoom() {
      const cursor = 'zoom-in';
      let temp = document.createElement('style');
      (document.head || document.body).appendChild(temp);
      let css = `
            .ant-upload-list-picture-card .ant-upload-list-item:hover .picture-preview-info { cursor: ${cursor} !important;}
            `;
      temp.innerHTML = css;
    },
    addPictureUploadBtn(items) {
      let _this = this;
      setTimeout(() => {
        if (this.showUploadListPicture.showUploadIcon) {
          // let nodeProp = { type: 'upload', title: '上传图片', onClick: 'uploadClick' };
          // let $upload = <a-icon {...nodeProp} />;
          let $span = document.createElement('span');
          $span.setAttribute('class', 'action-iconfont');
          let $upload1 = document.createElement('i');
          $upload1.setAttribute('class', 'iconfont icon-ptkj-shangchuan');
          $upload1.setAttribute('title', '上传图片');
          let item = items[0];

          $span.appendChild($upload1);
          item.nextElementSibling.appendChild($span);
          $upload1.addEventListener('click', function () {
            _this.uploadClick(item);
          });
        }
      }, 300);
    },
    uploadClick() {
      if (this.pictureForLib) {
        this.openPictureLib();
      } else {
        let $input = this.$el.querySelector("input[type='file']");
        $input.dispatchEvent(new MouseEvent('click'));
      }
    },
    onReject(list) {
      console.log('reject', list);
    },
    checkAcceptFile(file) {
      return new Promise((resolve, reject) => {
        let accpet = this.widget.configuration.accept;
        if (this.materialAccept.length) {
          accpet = this.materialAccept;
        }
        if (!accpet.length) {
          resolve();
          return;
        }
        const fileName = file.name;
        const suffix = fileName.substring(fileName.lastIndexOf('.'));
        if (!JSON.parse(JSON.stringify(accpet).toLowerCase()).includes(suffix.toLowerCase())) {
          reject();
          return;
        }
        let ext = fileName.split('.').pop().toLowerCase();
        if (fileSignatures[ext] && fileSignatures[ext].length > 0) {
          this.checkFileSignature(file, fileSignatures[ext], ext)
            .then(match => {
              if (match) {
                resolve();
              } else {
                console.error('检测到上传文件签名头与实际不符, 禁止上传');
                reject();
              }
            })
            .catch(() => {
              reject();
            });
          return;
        } else if (ext == 'txt') {
          this.checkIsTextFile(file)
            .then(() => {
              resolve();
            })
            .catch(() => {
              reject();
            });
          return;
        }
        resolve();
      });
    },

    checkFileSignature(file, expectedSignatures, ext) {
      return new Promise((resolve, reject) => {
        if (typeof file.slice !== 'function') {
          // 传入非文件对象
          resolve(true);
          return;
        }
        const reader = new FileReader();
        // 读取文件的前几个字节（根据签名的最大长度）
        const maxLength = Math.max(...expectedSignatures.map(sig => sig.length));
        const blob = file.slice(0, maxLength);
        reader.readAsArrayBuffer(blob);

        reader.onload = () => {
          const bytes = new Uint8Array(reader.result);

          // 遍历所有可能的签名
          const match = expectedSignatures.some(signature => {
            return signature.every((byte, index) => {
              return byte === bytes[index];
            });
          });
          if (match) {
            if (['zip', 'docx', 'xlsx'].includes(ext)) {
              // 这三类都是压缩文件签名头，需要进一步解析内容
              JSZip.loadAsync(file).then(zip => {
                if (ext == 'docx') {
                  resolve(zip.files['word/document.xml'] != undefined);
                  return;
                }
                if (ext == 'xlsx') {
                  resolve(zip.files['xl/workbook.xml'] != undefined);
                  return;
                }
                resolve(true);
              });
              return;
            }
            if (['doc', 'xls'].includes(ext)) {
              //FIXME: 如果文件较大，解析可能会占用较多内存和时间
              // const docReader = new FileReader();
              // docReader.onload = function (e) {
              //   const arrayBuffer = e.target.result;
              //   const uint8Array = new Uint8Array(arrayBuffer);
              //   // 使用 cfb 解析文件
              //   const cfb = CFB.parse(uint8Array);
              //   // 判断文件类型
              //   if ('doc' == ext) {
              //     resolve(CFB.find(cfb, 'WordDocument'));
              //   } else if ('xls' == ext) {
              //     resolve(CFB.find(cfb, 'Workbook'));
              //   } else {
              //     resolve(false);
              //   }
              // };
              // docReader.readAsArrayBuffer(file);
              // return;
            }
          }
          resolve(match);
        };

        reader.onerror = () => {
          reject(new Error('文件读取失败'));
        };
      });
    },
    checkIsTextFile(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = function (e) {
          const uint8Array = new Uint8Array(e.target.result);
          const isText = uint8Array.every(byte => {
            return (byte >= 32 && byte <= 126) || byte === 10 || byte === 13;
          });
          if (isText) {
            resolve();
          } else {
            console.error('检测到非文本内容格式文件, 禁止上传');
            reject();
          }
        };
        reader.readAsArrayBuffer(file);
      });
    },
    checkFileNameRepeat(file, fileList) {
      if (!this.configuration.fileNameRepeat) {
        if (!fileList) {
          fileList = this.fileList;
        }
        const fileIndex = fileList.findIndex(item => item.name === file.name);
        if (fileIndex !== -1) {
          return false;
        }
      }
      return true;
    },
    onBeforeUpload(file, fileList) {
      let _this = this;
      this.$refs[this.configuration.code].validateDisabled = true;
      const accpet = this.accept || this.widget.configuration.accept;
      let continueCheck = () => {
        return new Promise((resolve, reject) => {
          if (this.isReplaceImgByLimitOne) {
            // 图片限制一个，则进行替换
            this.onClickDelFile(this.fileList[0], 0);
          }
          if (this.fileSizeLimit != -1 && this.fileSizeLimit < file.size) {
            this.$message.error(
              `${this.$t('WidgetFormFileUpload.message.fileSizeLimit', '文件超过上传限制')}:${
                this.widget.configuration.fileSizeLimit + this.widget.configuration.fileSizeLimitUnit
              }`
            );
            reject();
            return;
          }

          if (!this.checkFileNameRepeat(file)) {
            this.$message.error(this.$t('WidgetFormFileUpload.message.notAllowUploadSameName', '不允许上传重名'));
            reject();
            return;
          }
          let checkLimitNum = true;
          if (this.curButton && this.curButton.button.code == 'onClickReplace') {
            checkLimitNum = false;
          }
          const fileLimitNum = this.configuration.fileLimitNum;
          if (fileLimitNum && checkLimitNum) {
            const isLimit = this.fileList.length + fileList.length > fileLimitNum;
            const indexOfFile = fileList.findIndex(item => item.uid === file.uid) + this.fileList.length;
            if (isLimit && indexOfFile >= fileLimitNum) {
              this.$message.error(this.$t('WidgetFormFileUpload.message.uploadLimit', '上传文件已达限制数量') + ': ' + fileLimitNum);
              reject();
              return;
            }

            if (fileLimitNum === this.fileList.length) {
              this.$message.error(this.$t('WidgetFormFileUpload.message.uploadLimit', '上传文件已达限制数量') + ': ' + fileLimitNum);
              reject();
              return;
            }
          }
          if (this.configuration.type == 'picture' && this.configuration.pixelCheck) {
            const pixelWidth = this.configuration.pixelWidth,
              pixelHeight = this.configuration.pixelHeight,
              _URL = window.URL || window.webkitURL;
            if (file.width && file.height) {
              const valid = file.width == pixelWidth && file.height == pixelHeight;
              if (!valid) {
                this.$message.error(
                  this.$t('WidgetFormFileUpload.message.pixelMustMatch', '像素必须满足') + `${pixelWidth}px*${pixelHeight}px`
                );
                reject();
                return;
              }
            } else {
              let imagePromise = new Promise((resolve, reject) => {
                const image = new Image();
                image.onload = () => {
                  const valid = image.width == pixelWidth && image.height == pixelHeight;
                  if (valid) {
                    resolve();
                  } else {
                    this.$message.error(
                      this.$t('WidgetFormFileUpload.message.pixelMustMatch', '像素必须满足') + `${pixelWidth}px*${pixelHeight}px`
                    );
                    reject();
                  }
                };
                image.src = _URL.createObjectURL(file);
              });
              imagePromise
                .then(() => {
                  resolve();
                })
                .catch(() => {
                  reject();
                });
            }
          } else {
            resolve();
          }
        });
      };
      let qrCheck = () => {
        return new Promise((resolve, reject) => {
          if (this.configuration.qrCodeNotAllowed !== true) {
            resolve();
            return;
          }
          let ext = file.name.split('.').pop().toLowerCase();
          if (['jpeg', 'jpg', 'png', 'bmp', 'jfif', 'tif', 'tiff'].includes(ext)) {
            if (file.fileID) {
              let img = new Image();
              img.src = `/proxy-repository/repository/file/mongo/download?fileID=${file.fileID}`;
              img.onload = function () {
                _this
                  .checkImageContainQrCode(img)
                  .then(() => {
                    resolve();
                  })
                  .catch(() => {
                    reject();
                  });
              };
              img.onerror = function () {
                console.error('图片解析错误, 非正常图片格式');
                _this.$message.error(_this.$t('WidgetFormFileUpload.message.notAllowFile', '文件不允许'));
                reject();
              };
              return;
            } else if (file.constructor == File) {
              const reader = new FileReader();
              reader.onload = function (e) {
                const img = new Image();
                img.onload = function () {
                  _this
                    .checkImageContainQrCode(img)
                    .then(() => {
                      resolve();
                    })
                    .catch(() => {
                      reject();
                    });
                };
                img.src = e.target.result;
                img.onerror = function () {
                  console.error('图片解析错误, 非正常图片格式');
                  _this.$message.error(_this.$t('WidgetFormFileUpload.message.notAllowFile', '文件不允许'));
                  reject();
                };
              };
              reader.readAsDataURL(file);
            } else {
              resolve(true);
            }
          } else {
            resolve(true);
          }
        });
      };

      const compressImageCheck = file => {
        return new Promise((resolve, reject) => {
          if (this.configuration.enabledCompressPicture) {
            compressImage(file, 0.5).then(
              newFile => {
                newFile.uid = file.uid;
                resolve(newFile);
              },
              error => {
                reject();
              }
            );
          } else {
            resolve(file);
          }
        });
      };
      return new Promise((resolve, reject) => {
        if (accpet.length) {
          this.checkAcceptFile(file)
            .then(() => {
              qrCheck()
                .then(() => {
                  continueCheck()
                    .then(() => {
                      compressImageCheck(file)
                        .then(newFile => {
                          resolve(newFile);
                        })
                        .catch(() => {
                          reject();
                        });
                    })
                    .catch(() => {
                      reject();
                    });
                })
                .catch(e => {
                  reject();
                });
            })
            .catch(() => {
              this.$message.error(this.$t('WidgetFormFileUpload.message.notAllowFile', '文件不允许'));
              reject();
            });
        } else {
          qrCheck()
            .then(() => {
              continueCheck()
                .then(() => {
                  compressImageCheck(file)
                    .then(newFile => {
                      resolve(newFile);
                    })
                    .catch(() => {
                      reject();
                    });
                })
                .catch(() => {
                  reject();
                });
            })
            .catch(() => {
              reject();
            });
        }
      });
    },
    checkImageContainQrCode(img) {
      let _this = this;
      return new Promise((resolve, reject) => {
        if (EASY_ENV_IS_BROWSER) {
          let check = () => {
            cv.then(() => {
              // 创建QRCodeDetector实例
              var qrCodeDetector = new cv.QRCodeDetector();

              // 将图片元素转换为cv.Mat对象
              var src = cv.imread(img);

              // 检测二维码的位置
              var points = new cv.Mat();
              var found = qrCodeDetector.detect(src, points);
              if (found) {
                // 解码二维码
                // var decodedText = qrCodeDetector.decode(src, points);
                _this.$message.error(_this.$t('WidgetFormFileUpload.message.notAllowQrCodeImage', '不允许上传包含二维码的图片'));
                // 释放内存
                points.delete();
                reject();
              } else {
                resolve();
              }

              // 释放内存
              src.delete();
            });
          };
          if (window.cv == undefined) {
            var script = document.createElement('script');
            script.src = '/static/js/opencv/4.5.5/opencv.js';
            script.type = 'text/javascript';
            script.async = true;
            document.head.appendChild(script);
            script.onload = function () {
              check();
            };
            script.onerror = function () {};
          } else {
            check();
          }
        }
      });
    },
    // 获取图片水印配置
    getPictureWatermarkConfig() {
      let enabled = false,
        watermarkConfig = [];
      const pictureWatermark = this.configuration.pictureWatermark;
      if (pictureWatermark) {
        for (let index = 0; index < pictureWatermark.length; index++) {
          const item = pictureWatermark[index];
          if (item.enabled) {
            enabled = true;
            watermarkConfig.push(item);
          }
        }
      }
      return { enabled, watermarkConfig };
    },
    getWatermarkTime() {
      return new Promise((resolve, reject) => {
        resolve(moment().format('YYYY-MM-DD HH:mm:ss'));
      });
    },
    getWatermarkMap() {
      return new Promise((resolve, reject) => {
        const getAddress = () => {
          let address = '';
          if (this.addressInfo) {
            address = this.addressInfo.formattedAddress || `${this.addressInfo.province}${this.addressInfo.city}`;
          }
          return address;
        };
        if (this.addressInfo) {
          resolve(getAddress());
        } else {
          this.$clientCommonApi.getAMapAddress().then(
            res => {
              this.addressInfo = res;
              resolve(getAddress());
            },
            error => {
              console.log(error);
            }
          );
        }
      });
    },
    getWatermarkUserName() {
      return new Promise((resolve, reject) => {
        resolve(this._$USER.userName);
      });
    },
    getWatermarkCustom(config) {
      return new Promise((resolve, reject) => {
        resolve(config.text);
      });
    },
    // 图片上传转换
    onPictureTransformFile(file) {
      if (!this.configuration.enabledPictureWatermark) {
        return file;
      }

      const removeTips = this.$message.info({
        content: '图片水印生成中，请稍候....',
        duration: 0
      });
      return new Promise(resolve => {
        let allFetch = [];
        this.watermarkConfig.map(config => {
          allFetch.push(this[`getWatermark${config.type}`](config));
        });
        Promise.all(allFetch).then(res => {
          let watermarks = [];
          res.map((text, index) => {
            let config = this.watermarkConfig[index];
            let opacity = 0;
            if (config.opacity) {
              opacity = (config.opacity / 100).toFixed(2);
            }
            watermarks.push({
              text,
              fontSize: config.fontSize,
              fontColor: config.fontColor,
              textAlign: config.textAlign,
              baseline: config.baseline,
              opacity
            });
          });
          addWatermarksToImageFile({
            imageFile: file,
            watermarks
          }).then(newFile => {
            newFile.uid = file.uid;
            removeTips();
            resolve(newFile);
          });
        });
      });
    },
    onTransformFile(file) {
      // if (file.type.indexOf('image/') === 0) {
      //   //FIXME: 待完善图片水印功能 https://www.npmjs.com/package/watermarkjs
      //   return new Promise(resolve => {
      //     const reader = new FileReader();
      //     reader.readAsDataURL(file);
      //     reader.onload = () => {
      //       const canvas = document.createElement('canvas');
      //       const img = document.createElement('img');
      //       img.src = reader.result;
      //       img.onload = () => {
      //         canvas.width = img.width;
      //         canvas.height = img.height;
      //         const ctx = canvas.getContext('2d');
      //         ctx.drawImage(img, 0, 0);
      //         ctx.fillStyle = 'red';
      //         ctx.textBaseline = 'middle';
      //         ctx.fillText('WELLSOFT', 20, 20);
      //         canvas.toBlob(function () {
      //           let f = new File([arguments[0]], file.name, { type: file.type });
      //           f.uid = file.uid;
      //           resolve(f);
      //         });
      //       };
      //     };
      //   });
      // }

      return file;
    },
    // 预览图片
    onClickPreviewPicture(params) {
      let { evt, file } = params;
      this.previewImage(file);
    },
    // 复制名称
    onClickCopyFilename(params) {
      let { evt, file } = params;
      let _this = this;
      copyToClipboard(file.name, evt, function (success) {
        if (success) {
          _this.$message.success(this.$t('WidgetFormFileUpload.message.copyDone', '已复制'));
        }
      });
    },
    onClickExtensionBtn(button) {},
    // 预览文件
    onClickPreviewFile(params) {
      let { file, button } = params;
      if (file.status != UPLOAD_STATUS.DONE) {
        this.$message.info(
          file.status === UPLOAD_STATUS.UPLOADING
            ? this.$t('WidgetFormFileUpload.message.fileUploadingPleaseWait', '文件上传中, 请稍后')
            : this.$t('WidgetFormFileUpload.message.fileUploadErrorCannotPreview', '文件上传失败, 无法预览')
        );
        return;
      }
      let _this = this;

      const _previewFile = (button, serv = this.fileRepositoryServerHost) => {
        let _req = {};
        if (button) {
          _req.callback = function () {
            button.loading = false;
          };
        }

        if (_this.widget.configuration.fileWatermarkConfig && _this.widget.configuration.fileWatermarkConfig.effectConditionControl) {
          _this.calculateDataConditionControlResult(_this.widget.configuration.fileWatermarkConfig.effectConditionControl).then(match => {
            _req.urlParamString = function () {
              return match ? _this.getPreviewWatermarkUrlParams() : 'forceWatermarkDisabled=true';
            };
            preview(`${serv}/wopi/files/${file.dbFile.fileID}?access_token=336dc563-1d17-44a3-a916-e8abe2e88cbb`, _req);
          });
          return;
        }
        preview(`${serv}/wopi/files/${file.dbFile.fileID}?access_token=336dc563-1d17-44a3-a916-e8abe2e88cbb`, _req);
      };

      if (_this.fileRepositoryServerHost == '') {
        button.loading = true; // 初次加载要有个loading，待预览api初始化成功会回调进行关闭
        this.$clientCommonApi.getSystemParamValue('sys.context.path', function (serv) {
          if (serv) {
            _this.fileRepositoryServerHost = 'null' === serv ? null : serv;
            _previewFile(button, serv);
          }
        });
        return;
      }
      _previewFile();
    },
    getPreviewWatermarkUrlParams() {
      let fileWatermarkConfig = this.widget.configuration.fileWatermarkConfig;
      if (
        this.dyformFileWatermarkDisabled !== true &&
        fileWatermarkConfig &&
        fileWatermarkConfig.enabled &&
        fileWatermarkConfig.effectScope.includes('preview')
      ) {
        let { type, imageFileId, text, fontColor, fontSize, fontFamily, layout, scale, opacity, verticalAlign, horizontalAlign } =
          fileWatermarkConfig;
        let params = { watermarkLayout: layout };
        params.watermarkPositions =
          verticalAlign == horizontalAlign && horizontalAlign == 'center' ? 'CENTER' : `${verticalAlign}_${horizontalAlign}`.toUpperCase();
        if (type == 'picture') {
          if (imageFileId) {
            params.watermarkPictureUrl = encodeURIComponent(`${this.fileRepositoryServerHost}/wopi/files/${imageFileId}`);
          }
          if (opacity != undefined) {
            params.watermarkAlpha = parseFloat((opacity / 100).toFixed(2));
          }
          if (scale != undefined) {
            params.scale = parseFloat((scale / 100).toFixed(2));
          }
        } else {
          params.watermarkText = encodeURIComponent(this.resolveWatermarkFormulaText());
          if (fontColor) {
            params.watermarkColor = encodeURIComponent(fontColor);
          }
          if (opacity != undefined) {
            params.watermarkAlpha = parseFloat((opacity / 100).toFixed(2));
          }
          if (fontFamily) {
            params.watermarkFontFamily = encodeURIComponent(fontFamily);
          }
          if (fontSize != undefined) {
            params.watermarkFontSize = fontSize;
          }
        }
        return queryStringify(params);
      }
      return '';
    },

    downloadFileSilence(url) {
      var _this = this;
      var hiddenIFrameID = 'hiddenDownloader' + generateId();
      var iframe = document.createElement('iframe');
      iframe.id = hiddenIFrameID;
      iframe.style.display = 'none';
      document.body.appendChild(iframe);
      iframe.src = url;

      var cnt = 0;
      var timer = setInterval(function () {
        if (cnt++ == 100) {
          clearInterval(timer);
          iframe.remove();
          return;
        }
        var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
        if (iframeDoc.readyState == 'complete' || iframeDoc.readyState == 'interactive') {
          var _text = iframeDoc.body.innerText;
          if (_text && _text.indexOf('No such file') != -1) {
            //需要等待后端响应无文件的异常
            clearInterval(timer);
            iframe.remove();
            if (_text.indexOf('try later') != -1) {
              setTimeout(function () {
                _this.downloadURL(url); //重新下载
              }, 2000);
            }
          }
          return;
        }
      }, 1000);
    },
    // 下载文件
    onClickDownloadFile(params) {
      const file = params.file ? params.file : params;
      if (file.status != UPLOAD_STATUS.DONE) {
        this.$message.info(this.$t('WidgetFormFileUpload.message.fileUploadingPleaseWait', '文件上传中, 请稍后'));
        return;
      }
      this.getWatermarkUrlQueryParams().then(urlQueryParams => {
        this.downloadFileSilence(`/proxy-repository/repository/file/mongo/download?fileID=${file.dbFile.fileID}${urlQueryParams}`);
      });
    },
    onClickDelFile() {
      // let { file, fileIndex } = params;
      let file, fileIndex;
      if (arguments.length > 1) {
        [file, fileIndex] = [...arguments];
      } else {
        file = arguments[0].file;
        fileIndex = arguments[0].fileIndex;
      }
      delete this.fileUploadMap[file.uid];
      this.fileList.splice(fileIndex, 1);
      this.formData[this.fieldCode].splice(fileIndex, 1);

      const selectedIndex = this.rowSelection.selectedRows.findIndex(f => {
        if (f.dbFile && file.dbFile) {
          if (f.dbFile.fileID === file.dbFile.fileID) {
            return true;
          }
        }
        return false;
      });
      if (selectedIndex > -1) {
        this.rowSelection.selectedRows.splice(selectedIndex, 1);
      }

      this.emitChange();
      return true;
    },
    onFileItemHoverVisibleChange(v, file) {
      if (file.dropdownButtonVisible === true) {
        file.hovered = true;
        return;
      }
      file.hovered = file.status == UPLOAD_STATUS.DONE && v;
    },
    // 获取上传input 节点
    getFileInputNode() {
      let fileInputNode;
      if (this.configuration.type == 'simpleList') {
        fileInputNode = this.$refs.simpleUpload.$el.querySelector('input');
      }
      if (this.configuration.type == 'advancedList') {
        fileInputNode = this.$refs.advancedUpload.$el.querySelector('input');
      }
      return fileInputNode;
    },
    // 开始上传、上传中、完成、失败都会调用这个函数
    onFileUploadChange(args, isLib) {
      let { file, fileList } = args;

      let _this = this;
      if (file.status === UPLOAD_STATUS.REMOVED) {
        // 移除文件
        if (isLib) {
          delete this.fileUploadMap[file.uid];
          this.pictureFileList = fileList;
        } else {
          for (let i = 0, len = this.fileList.length; i < len; i++) {
            if (this.fileList[i].uid === file.uid) {
              this.onClickDelFile(file, i);
              return;
            }
          }
        }
      }
      if (file.status === UPLOAD_STATUS.ERROR) {
        if (this.curButton && this.curButton.button.code == 'onClickReplace') {
          const fileReplace = this.curButton.file;
          const fileIndex = this.curButton.fileIndex;
          this.fileUploadMap[fileReplace.uid] = fileReplace;
          this.fileList.splice(fileIndex, 1, fileReplace);
          this.formData[this.fieldCode].splice(fileIndex, 1, fileReplace);
        }
      }
      if (file.status === UPLOAD_STATUS.DONE) {
        const dbFile = this.fileUploadMap[file.uid].dbFile;
        if (this.widget.configuration.type == 'picture') {
          this.images.push(file.url);
          this.$nextTick(() => {
            this.setPictureCardPreview();
            this.setPictureCardCheckbox();
          });
        }
        if (this.formData[this.fieldCode] == null) {
          this.formData[this.fieldCode] = [];
        }
        if (this.isText && this.curButton && this.curButton.button.code == 'onImportText') {
          // 导入正文
          this.$confirm({
            title: this.$t('WidgetFormFileUpload.message.confirmTitle', '确认'),
            content: this.$t('WidgetFormFileUpload.message.importSuccessIfOpenEdit', '导入成功,是否打开编辑') + '?',
            okText: (this.locale && this.locale.Modal && this.locale.Modal.okText) || this.$t('WidgetFormFileUpload.message.ok', '确定'),
            cancelText:
              (this.locale && this.locale.Modal && this.locale.Modal.cancelText) || this.$t('WidgetFormFileUpload.message.cancel', '取消'),
            onOk: () => {
              file.filename = file.dbFile.filename;
              file.fileID = file.dbFile.fileID;
              this.importText(file, true, file.filename);
            }
          });
        }
        if (this.curButton && this.curButton.button.code == 'onClickReplace') {
          const fileInputNode = this.getFileInputNode();

          if (this.fileMultiple && fileInputNode) {
            fileInputNode.setAttribute('multiple', 'multiple');
          }
          const fileIndex = this.curButton.fileIndex;
          this.formData[this.fieldCode].splice(fileIndex, 1, dbFile);
        } else {
          this.formData[this.fieldCode].push(dbFile);
        }
        this.$refs[this.configuration.code].onFieldChange();
        this.emitChange();
      }
    },
    valideFileName(filename) {
      if (filename.length > 80) {
        this.$message.error(
          this.$t('WidgetFormFileUpload.message.fileNameLengthLimit', '附件文件名称超长(不得超过80个字)') + '[' + fileName + ']'
        );
        return false;
      }
      let test = !/[\/\\:\*\?"%<>\|\n]/gi.test(filename);
      if (!test) {
        this.$message.error(
          this.$t('WidgetFormFileUpload.message.fileNameUnusedChar', '文件名不得含有字符') +
            ':"/ \\ : * ? " % < > |, ' +
            this.$t('WidgetFormFileUpload.message.orFileNameLengthLimit80', '或者文件名长度超过80个字符')
        );
      }
      return test;
    },
    fileAlreadyExist(filename) {
      for (let i = 0, len = this.fileList.length; i < len; i++) {
        if (this.fileList[i].name === filename) {
          return true;
        }
      }
      return false;
    },
    // 编辑文件
    onClickEdit(params) {
      this.curLocalFileReadonly = false;
      if (this.officeApi) {
        this.openLocalFile(params.file.dbFile, null, params.file.name, false, false);
      }
      if (!this.officeApi) {
        this.officeApi = new OfficeApi({
          prepared: () => {
            this.curButton.button.loading = false;
          },
          callback: () => {
            this.openLocalFile(params.file.dbFile, null, params.file.name, false, false);
          }
        });
      }
    },
    // 新建正文
    onNewText(params) {
      const fileLimitNum = this.configuration.fileLimitNum;
      if (fileLimitNum) {
        if (this.fileList.length >= fileLimitNum) {
          this.$message.error(this.$t('WidgetFormFileUpload.message.uploadLimit', '上传文件已达限制数量') + ': ' + fileLimitNum);
          return false;
        }
      }
      let _this = this,
        type = 'doc',
        button = params.button;
      button.loading = true;
      if (!this.officeApi) {
        this.officeApi = new OfficeApi({
          prepared: function () {
            button.loading = false;
          }
        });
      }
      var fnType,
        fileType,
        fileName = prompt(
          this.$t('WidgetFormFileUpload.message.pleaseInputDocName', '请输入正文文件名'),
          this.$t('WidgetFormFileUpload.message.docName', '正文')
        );
      if (fileName == null) {
        button.loading = false;
        return;
      }
      if (fileName.length === 0) {
        alert(this.$t('WidgetFormFileUpload.message.pleaseInputName', '输入文件名'));
        return this.onNewText(type);
      }
      // this.$confim({
      //   content: <a-input />,
      //   onOk() {},
      //   onCancel() {}
      // });

      // 检查文件名的合法性
      if (!this.valideFileName(fileName)) {
        return this.onNewText(type);
      }

      if (type === 'xls') {
        // word正文
        fnType = 'openET';
        fileType = '.xls';
      } else {
        // excel正文
        fnType = 'openDoc';
        fileType = '.doc';
      }
      fileName += fileType;
      if (!this.checkFileNameRepeat({ name: fileName })) {
        this.$message.error(this.$t('WidgetFormFileUpload.message.notAllowUploadSameName', '不允许上传重名'));
        return;
      }

      if (this.fileAlreadyExist(fileName)) {
        // TODO: 重名修改文件名自动加(数字)
        return this.onNewText(type);
      }
      var fileID = generateId();
      var bRevision = this.readonly != true && this.pbRecord;

      var openOptions = {
        bsMode: true,
        docId: fileID,
        newFileName: fileName,
        uploadPath: '/office/wps/savefiles?source=新建正文', // 保存文档接口
        buttonGroups: '', // btnSaveAsFile,btnImportDoc,btnPageSetup,btnInsertDate,btnSelectBookmark,btnImportTemplate
        disableBtns: 'ReviewTrackChangesMenu,FileSaveAsMenu,FileSaveAs',
        suffix: fileType,
        userName: this._$USER.userName,
        revisionCtrl: {
          // 痕迹控制 ，不传正常打开
          bOpenRevision: bRevision, // true(打开)false(关闭)修订
          bShowRevision: bRevision, // true(显示)/false(关闭)痕迹。会影响showRevisionsMode，true时，showRevisionsMode的1、3有效；false时，showRevisionsMode的2、4有效
          bEnabledRevision: !bRevision // true(有权限接受、取消保护)/false(无权限接受，受保护)痕迹
          // true(显示)/false(关闭)痕迹
        },
        openType: {
          protectType: this.readOnly === true ? 3 : -1
        },
        success: result => {
          let _f = result.data[0];
          console.log('正文上传返回: ', _f);
          this.formData[this.fieldCode] = this.formData[this.fieldCode].concat(result.data);
          let fileIndex = this.fileList.findIndex(item => item.uid === fileID);
          if (fileIndex != -1) {
            this.onClickDelFile({ uid: fileID }, fileIndex);
          }
          _f.createTimeStr =
            typeof _f.createTime === 'number'
              ? moment(_f.createTime).format('YYYY-MM-DD HH:mm')
              : moment(_f.createTime, 'YYYY-MM-DD HH:mm:ss').format('YYYY-MM-DD HH:mm');
          _f.userName = this._$USER.userName;
          this.addFileList({
            uid: fileID,
            name: _f.fileName || _f.filename,
            showProgress: false,
            status: UPLOAD_STATUS.DONE,
            size: _f.fileSize,
            dbFile: _f
          });
          this.updateFileWatermark({
            name: _f.fileName || _f.filename,
            dbFile: {
              fileID: _f.fileID
            }
          }).then(() => {});
        },
        error: function (result) {
          console.error(result);
        }
      };
      this.officeApi[fnType](openOptions);
    },
    openLocalFile(fileInfo, temp, filename, isOfd, iconReadOnly) {
      const localFileName = filename || fileInfo.fileName || fileInfo.filename;
      const bRevision = this.curLocalFileReadonly != true && this.pbRecord;
      const openOptions = {
        bsMode: true,
        newFileName: localFileName,
        origId: fileInfo.origUuid, // 用于welloffice的本地历史功能
        docId: fileInfo.fileID || generateId(),
        fileName:
          '/office/wps/download?fileID=' +
          fileInfo.fileID +
          (temp ? '&once=true' : '') +
          (filename ? '&filename=' + encodeURIComponent(filename) : '') +
          (isOfd === true ? '&isOfd=true' : ''),
        uploadPath: this.curLocalFileReadonly === true ? '' : '/office/wps/savefiles?fileID=' + fileInfo.fileID + '&source=编辑', // 保存文档接口
        buttonGroups: '',
        disableBtns: 'ReviewTrackChangesMenu,FileSaveAsMenu,FileSaveAs',
        userName: this._$USER.userName,
        revisionCtrl: {
          // 痕迹控制 ，不传正常打开
          bOpenRevision: bRevision, // true(打开)false(关闭)修订
          bShowRevision: bRevision, // true(显示)/false(关闭)痕迹。会影响showRevisionsMode，true时，showRevisionsMode的1、3有效；false时，showRevisionsMode的2、4有效
          bEnabledRevision: bRevision, // true(有权限接受、取消保护)/false(无权限接受，受保护)痕迹
          bAcceptRevision: false, // 接受所有状态
          bRejectRevision: false, // 拒绝所有状态
          showRevisionsMode: 1 // (显示标记的最终状态)/2(最终状态)/3(显示标记的原始状态)/4(原始状态)。受bShowRevision控制。对JSAPI（WPS）有效，对welloffice无效，因为MS office会奔溃
        },
        openType: {
          protectType: this.curLocalFileReadonly === true || iconReadOnly ? 3 : -1
        },
        success: result => {
          this.formData[this.fieldCode] = this.formData[this.fieldCode].concat(result.data);
          const fileIndex = this.fileList.findIndex(
            item => item.uid === fileInfo.uid || item.dbFile.fileID == (fileInfo.dbFile ? fileInfo.dbFile.fileID : fileInfo.fileID)
          );
          if (fileIndex != -1) {
            this.onClickDelFile(fileInfo, fileIndex);
          }
          for (let i = 0, len = result.data.length; i < len; i++) {
            let _f = result.data[i];
            _f.createTimeStr =
              typeof _f.createTime === 'number'
                ? moment(_f.createTime).format('YYYY-MM-DD HH:mm')
                : moment(_f.createTime, 'YYYY-MM-DD HH:mm:ss').format('YYYY-MM-DD HH:mm');
            _f.userName = this._$USER.userName;
            this.addFileList({
              uid: generateId(),
              name: _f.fileName || _f.filename,
              showProgress: false,
              status: UPLOAD_STATUS.DONE,
              size: _f.fileSize,
              dbFile: _f
            });
          }
        },
        error: result => {
          console.error(result);
        }
      };
      let fnType = 'openDoc';
      const wordSuffix = ['doc', 'docx', 'wps', 'wpsx'];
      const etSuffix = ['xls', 'xlsx', 'et', 'etx'];
      const fileExt = this.getFileExt(localFileName);
      if (wordSuffix.includes(fileExt)) {
        fnType = 'openDoc';
      } else if (etSuffix.includes(fileExt)) {
        fnType = 'openET';
      }
      this.officeApi[fnType](openOptions);
    },
    // 导入正文
    importText(params, temp, filename) {
      if (this.officeApi) {
        this.openLocalFile(params, temp, filename);
      }
      if (!this.officeApi) {
        this.officeApi = new OfficeApi({
          prepared: () => {
            this.curButton.button.loading = false;
          },
          callback: () => {
            this.openLocalFile(params, temp, filename);
          }
        });
      }
    },
    onImportText() {
      this.onAdvancedAddFile();
    },
    // 粘贴附件
    onPasteFile(params) {
      const pasteFile = () => {
        if (this.officeApi.api.pasteFile) {
          this.officeApi.api.pasteFile({
            validate: localFiles => {
              let promise = [];
              for (let i = 0, len = localFiles.length; i < len; i++) {
                promise.push(this.onBeforeUpload(localFiles[i], localFiles));
              }
              return new Promise((resolve, reject) => {
                Promise.all(promise)
                  .then(() => {
                    resolve();
                  })
                  .catch(() => {
                    reject();
                  });
              });
            },
            success: result => {
              let appendFile = _f => {
                _f.createTimeStr =
                  typeof _f.createTime === 'number'
                    ? moment(_f.createTime).format('YYYY-MM-DD HH:mm')
                    : moment(_f.createTime, 'YYYY-MM-DD HH:mm:ss').format('YYYY-MM-DD HH:mm');
                _f.userName = this._$USER.userName;
                this.formData[this.fieldCode].push(_f);
                this.addFileList({
                  uid: generateId(),
                  name: _f.fileName || _f.filename,
                  showProgress: false,
                  status: UPLOAD_STATUS.DONE,
                  size: _f.fileSize,
                  dbFile: _f
                });
              };
              const accpet = this.accept || this.widget.configuration.accept;
              for (let i = 0, len = result.data.length; i < len; i++) {
                let _f = result.data[i];
                let ext = (_f.fileName || _f.filename).split('.').pop().toLowerCase();
                if (this.widget.configuration.qrCodeNotAllowed && ['jpeg', 'jpg', 'png', 'bmp', 'jfif', 'tif', 'tiff'].includes(ext)) {
                  this.checkImageFileContainQrCode(_f.fileID)
                    .then(() => {
                      if (accpet.length > 0) {
                        this.checkDbFileMagicHeadByte(_f.fileID)
                          .then(() => {
                            appendFile(_f);
                          })
                          .catch(() => {
                            this.$message.error(this.$t('WidgetFormFileUpload.message.notAllowFile', '文件不允许'));
                            this.$axios.post(`/proxy-repository/repository/file/mongo/deleteFile?fileID=${_f.fileID}`, { params: {} });
                          });
                      } else {
                        appendFile(_f);
                      }
                    })
                    .catch(() => {
                      this.$axios.post(`/proxy-repository/repository/file/mongo/deleteFile?fileID=${_f.fileID}`, { params: {} });
                    });
                } else {
                  if (accpet.length > 0) {
                    this.checkDbFileMagicHeadByte(_f.fileID)
                      .then(() => {
                        appendFile(_f);
                      })
                      .catch(() => {
                        this.$message.error(this.$t('WidgetFormFileUpload.message.notAllowFile', '文件不允许'));
                        this.$axios.post(`/proxy-repository/repository/file/mongo/deleteFile?fileID=${_f.fileID}`, { params: {} });
                      });
                  } else {
                    appendFile(_f);
                  }
                }
              }
            },
            error: function (result) {},
            complete: function (result) {}
          });
        } else {
          console.error('不支持的操作');
        }
      };
      if (this.officeApi) {
        pasteFile();
      }
      if (!this.officeApi) {
        this.officeApi = new OfficeApi({
          prepared: () => {
            this.curButton.button.loading = false;
          },
          callback: () => {
            pasteFile();
          }
        });
      }
    },
    checkDbFileMagicHeadByte(fileID) {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy-repository/repository/file/mongo/checkFileHeadMagicByte?fileID=${fileID}`, { params: {} })
          .then(({ data }) => {
            if (data) {
              resolve();
            } else {
              reject();
            }
          })
          .catch(() => {
            reject();
          });
      });
    },
    checkImageFileContainQrCode(fileID) {
      let _this = this;
      return new Promise((resolve, reject) => {
        let img = new Image();
        img.src = `/proxy-repository/repository/file/mongo/download?fileID=${fileID}`;
        img.onload = function () {
          _this
            .checkImageContainQrCode(img)
            .then(() => {
              resolve();
            })
            .catch(() => {
              reject();
            });
        };
        img.onerror = function () {
          console.error('图片解析错误, 非正常图片格式');
          _this.$message.error(_this.$t('WidgetFormFileUpload.message.notAllowFile', '文件不允许'));
          reject();
        };
      });
    },
    // 重命名
    onClickRename() {
      this.visibleRename = true;
      const file = this.curButton.file;
      this.newFileName = file.name.substring(0, file.name.lastIndexOf('.'));
    },
    // 确认重命名
    confirmRename() {
      const file = this.curButton.file;
      const oldFileName = file.name;
      const fileIndex = this.fileList.findIndex(item => item.name === this.newFileName);
      if (fileIndex !== -1) {
        this.$message.error(
          this.$t(
            'WidgetFormFileUpload.message.fileNameExistPleaseReInput',
            { newFileName: this.newFileName },
            `文件${this.newFileName}已经存在，请重新输入！`
          )
        );
        return;
      }
      const type = oldFileName.substring(oldFileName.lastIndexOf('.'));
      const newFileName = this.newFileName + type;
      file.name = newFileName;
      file.dbFile.fileName = newFileName;
      file.dbFile.filename = newFileName;
      this.visibleRename = false;
      this.emitChange();
    },
    // 历史记录
    onClickShowHistory() {
      let localHistoryFiles = [];
      const file = this.curButton.file;
      const fileID = file.dbFile.fileID;

      const queryFileHistory = () => {
        if (this.officeApi.api.queryLocalFileHistory) {
          this.officeApi.api.queryLocalFileHistory({
            docId: fileID,
            success: res => {
              localHistoryFiles = res;
            },
            error: res => {}
          });
        }
      };
      if (this.wellOfficeEnable) {
        if (this.officeApi) {
          queryFileHistory();
        }
        if (!this.officeApi) {
          this.officeApi = new OfficeApi({
            prepared: () => {
              this.curButton.button.loading = false;
            },
            callback: () => {
              queryFileHistory();
            }
          });
        }
      }

      this.visibleHistory = true;
      this.loadingHistory = true;
      this.$axios({
        method: 'get',
        url: `/repository/file/mongo/queryFileHistory?fileID=${fileID}`
      }).then(
        ({ data }) => {
          if (data.code == 0) {
            this.fileHistoryList = data.data;
            if (!this.showLocalHistory) {
              let waitTranslateText = [];
              each(this.fileHistoryList, item => {
                if (item.source) {
                  let hasIndex = ['上传', '新建正文', '编辑'].indexOf(item.source);
                  if (hasIndex > -1) {
                    let sourceId = ['upload', 'onNewText', 'edit'][hasIndex];
                    item.source = this.$t('WidgetFormFileUpload.button.' + sourceId, item.source);
                  } else {
                    waitTranslateText.push(item.source);
                  }
                }
              });
              if (waitTranslateText.length > 0) {
                this.invokeTranslateApi(waitTranslateText).then(map => {
                  for (let item of this.fileHistoryList) {
                    if (map[item.source]) {
                      item.source = map[item.source];
                    }
                  }
                });
              }
            }
            if (this.wellOfficeEnable) {
              this.localHistoryFiles = localHistoryFiles;
            }
          }
          this.loadingHistory = false;
        },
        error => {
          this.loadingHistory = false;
        }
      );
    },
    // 查阅本地历史文件
    lookLocalHistory() {
      this.showLocalHistory = true;
      this.fileHistoryList = this.localHistoryFiles;
    },
    // 打开历史文件
    handleOpenHistory(record, index) {
      if (this.showLocalHistory) {
        // 打开本地历史文件
        const currLocalFile = record;
        const openOptions = {
          docId: currLocalFile.fileID,
          fileName: currLocalFile.name,
          localPath: currLocalFile.url,
          openType: {
            protectType: 3
          },
          success: function (result) {},
          error: function (result) {}
        };
        this.officeApi.openDoc(openOptions);
      } else {
        this.curLocalFileReadonly = true;
        this.openLocalFile(record, null, record.name);
      }
    },
    // 复制路径
    handleCopyLocalPath(record, index, evt) {
      copyToClipboard(record.url, evt, success => {
        if (success) {
          this.$message.success(this.$t('WidgetFormFileUpload.message.copyDone', '已复制'));
        }
      });
    },
    cancelHistory() {
      this.visibleHistory = false;
      this.fileHistoryList = [];
      this.showLocalHistory = false;
      this.localHistoryFiles = [];
    },
    // 另存为
    onClickSaveAs() {
      const file = this.curButton.file;
      const fileID = file.dbFile.fileID;
      const saveAs = () => {
        const options = {
          filename: file.name,
          url: `/repository/file/mongo/download4ocx?fileID=${fileID}`,
          success: result => {
            console.log('downloadSaveAs success:' + result);
          }
        };
        if (this.officeApi.api.saveAsFile) {
          this.officeApi.api.saveAsFile(options);
        }
      };
      if (this.officeApi) {
        saveAs();
      }
      if (!this.officeApi) {
        this.officeApi = new OfficeApi({
          prepared: () => {
            this.curButton.button.loading = false;
          },
          callback: () => {
            saveAs();
          }
        });
      }
    },
    // 查阅
    onClickLookUp() {
      const file = this.curButton.file;
      const lookUp = () => {
        this.openLocalFile(file.dbFile, false, file.name, false, true);
      };
      if (this.officeApi) {
        lookUp();
      }
      if (!this.officeApi) {
        this.officeApi = new OfficeApi({
          prepared: () => {
            this.curButton.button.loading = false;
          },
          callback: () => {
            lookUp();
          }
        });
      }
    },
    // 替换
    onClickReplace() {
      this.replaceTemp = this.curButton.file;
      const fileInputNode = this.getFileInputNode();
      if (fileInputNode) {
        const multiple = fileInputNode.getAttribute('multiple');
        if (multiple) {
          this.fileMultiple = true;
          fileInputNode.removeAttribute('multiple'); // 单选
        }
        fileInputNode.click();
      }
    },
    //  预览图片
    previewImage(file) {
      if (file.status !== UPLOAD_STATUS.DONE) {
        return;
      }
      let imageObjs = [],
        index = 0;
      for (let i = 0, len = this.fileList.length; i < len; i++) {
        imageObjs.push({
          src: this.fileList[i].url
        });
        if (file.uid === this.fileList[i].uid) {
          index = i;
        }
      }
      // options 查阅 ；https://www.npmjs.com/package/viewerjs
      const $viewer = viewerApi({
        options: {
          className: 'widget-file-upload-viewer',
          toolbar: true,
          url: 'src',
          title: false,
          toolbar: {
            zoomIn: 4,
            zoomOut: 4,
            oneToOne: 4,
            reset: 4,
            prev: this.fileList.length > 1 ? 1 : 0,
            // play: { // 放映模式
            //   show: 4,
            //   size: 'large',
            // },
            next: this.fileList.length > 1 ? 1 : 0,
            rotateLeft: 4,
            rotateRight: 4,
            flipHorizontal: 4,
            flipVertical: 4
          },
          initialViewIndex: index
        },
        images: imageObjs
      });
    },
    getFileExt(fileName) {
      if (fileName && fileName.lastIndexOf('.') > 0) {
        return fileName.substr(fileName.lastIndexOf('.') + 1);
      }
    },
    blobSlice(file, start, end) {
      var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice;
      return blobSlice.call(file, start, end);
    },
    computeFileMD5(file, callback) {
      var _this = this,
        chunkSize = this.chunkSize,
        chunks = Math.ceil(file.size / chunkSize),
        currentChunk = 0,
        spark = new SparkMD5.ArrayBuffer(),
        fileReader = new FileReader();

      fileReader.onload = function (e) {
        spark.append(e.target.result);
        currentChunk++;

        if (currentChunk < chunks) {
          loadNext();
        } else {
          let md5Str = spark.end();
          console.debug('计算文件hash值: ', md5Str); // Compute hash
          file.md5 = md5Str;
          callback();
        }
      };

      fileReader.onerror = function () {
        console.warn('读取文件失败');
      };

      function loadNext() {
        var start = currentChunk * chunkSize,
          end = start + chunkSize >= file.size ? file.size : start + chunkSize;

        fileReader.readAsArrayBuffer(_this.blobSlice(file, start, end));
      }

      loadNext();
    },
    setButtonControl(fileResult) {
      // 判断文件是否支持预览
      let regFileType =
        /\.(wps|png|jpe?g|gif|bmp|tif|tiff|zip|rar|7z|rtf|dot|doc|docx|ppt|pptx|xls|xlsx|vsd|vsdx|pdf|mp3|ogg|mp4|webm|text|txt|mthml|html|htm|xml|css|js|json|sql|log|properties|yaml|yml|Zat|sh|md|java|mine|groovy|jsp|ofd|wmv|ods|odt|odp|pot|potx|ppsm|ods|xls|xlsb|xlsm|xlsx|doc|docm|docx|dot|dotm|dotx|odt|odp|pot|potm|potx|pps|ppsm|ppsx|ppt|pptm|pptx)(\?.*)?/i;
      fileResult.buttonControl.canPreview = !regFileType.test(fileResult.name);
    },
    addFileList(file, isLib) {
      // file.uid =  generateId();
      if (file.dbFile && !file.dbFile.userName) {
        if (this.userNameMap[file.dbFile.creator] && this.userNameMap[file.dbFile.creator].userName) {
          file.dbFile.userName = this.userNameMap[file.dbFile.creator].userName;
        } else {
          this.fetchOrgUserDetail(file.dbFile);
        }
      }
      let fileResult = {
        uid: file.uid,
        name: file.name,
        percent: 0, //上传进度
        // url:'',thumbUrl:'',
        status: file.status || UPLOAD_STATUS.UPLOADING,
        hovered: false,
        showProgress: file.showProgress != undefined ? file.showProgress : true,
        dbFile: file.dbFile || {}, // 上传完文件，后端服务返回的文件信息
        formatSize: this.formatSize(file.size),
        buttonControl: {}
      };
      if (file.status === UPLOAD_STATUS.DONE) {
        fileResult.showProgress = false;
      }
      if (typeof fileResult.dbFile.createTime === 'string') {
        fileResult.dbFile.createTimeStr = fileResult.dbFile.createTime;
      }
      if (fileResult.dbFile.fileID) {
        fileResult.url = `/proxy-repository/repository/file/mongo/download?fileID=${fileResult.dbFile.fileID}`;
      }
      if (file.lastModified) {
        fileResult.orgFile = file;
      }
      fileResult.icon = this.getFileIcon(file.name);
      if (this.curButton && this.curButton.button.code == 'onClickReplace' && !isLib) {
        const fileReplace = this.curButton.file;
        const fileIndex = this.curButton.fileIndex;
        delete this.fileUploadMap[fileReplace.uid];
        this.fileList.splice(fileIndex, 1, fileResult);
        this.formData[this.fieldCode].splice(fileIndex, 1, fileResult);
      } else {
        if (isLib) {
          this.pictureFileList.push(fileResult);
        } else {
          this.fileList.push(fileResult);
        }
      }
      this.fileUploadMap[fileResult.uid] = fileResult;
      this.$nextTick(() => {
        this.setListWidthForcViewCard();
        this.setPictureCardPreview();
        this.setPictureCardCheckbox();
      });
      return fileResult;
    },
    // 设置图片卡片视图预览
    setPictureCardPreview() {
      const items = this.$el.querySelectorAll('.ant-upload-list-item-list-type-picture-card .ant-upload-list-item-info');
      if (items.length && this.showPicturePreview) {
        for (let index = 0; index < items.length; index++) {
          const item = items[index];
          const thumbnailNode = item.firstChild.firstChild;
          if (thumbnailNode.href) {
            const uid = thumbnailNode.href.split('=')[1];
            //if (uid == file.uid) {
            if (!item.classList.contains('picture-preview-info')) {
              item.setAttribute('class', `${item.className} picture-preview-info`);
              item.addEventListener('click', () => {
                this.previewImage(this.fileUploadMap[uid]);
              });
            }
            // }
          }
        }
      }
      this.addPictureUploadBtn(items);
    },
    // 设置图片卡片视图多选样式
    setPictureCardCheckbox() {
      const cardSelector = '.ant-upload-list-item-list-type-picture-card';
      const pictureActions = this.$el.querySelectorAll('.ant-upload-list-item-list-type-picture-card .ant-upload-list-item-actions');
      if (pictureActions.length && this.showPictureBatchDownload) {
        for (let index = 0; index < pictureActions.length; index++) {
          const item = pictureActions[index];
          if (item.previousSibling) {
            const thumbnailNode = item.previousSibling.querySelector('.ant-upload-list-item-thumbnail');
            if (thumbnailNode && thumbnailNode.href) {
              const uid = thumbnailNode.href.split('=')[1];
              const cardItemEl = item.closest(cardSelector);

              if (!cardItemEl.querySelector('.picture-card-checkbox')) {
                const boxNode = document.createElement('i');
                boxNode.setAttribute('class', 'iconfont icon-ptkj-duoxuan1-weixuan picture-card-checkbox');

                boxNode.addEventListener('click', event => {
                  if (!event.target.classList.contains('picture-card-checkbox-select')) {
                    event.target.classList.add('picture-card-checkbox-select');

                    this.rowSelection.selectedRows.push(this.fileUploadMap[uid]);
                  } else {
                    event.target.classList.remove('picture-card-checkbox-select');

                    const findIndex = this.rowSelection.selectedRows.findIndex(f => f.uid === uid || (f.dbFile && f.dbFile.fileID === uid));
                    this.rowSelection.selectedRows.splice(findIndex, 1);
                  }
                });

                // cardItemEl.insertBefore(boxNode, cardItemEl.firstChild);
                cardItemEl.appendChild(boxNode);
              }
            }
          }
        }
      }
    },
    // 设置图片卡片视图下列表宽度
    setListWidthForcViewCard() {
      let pictureItem = this.$el.querySelector('.ant-upload-list-item-list-type-picture-card');
      if (
        this.pictureItemWidth === undefined &&
        pictureItem &&
        this.configuration.enabledPictureAutoWrap &&
        this.configuration.pictureAutoWrapCount
      ) {
        // 图片上传卡片视图才生效
        let itemWidth = 0;
        console.log(pictureItem.offsetWidth);
        const pictureItemStyle = window.getComputedStyle(pictureItem);
        const marginLeft = parseFloat(pictureItemStyle.marginLeft) || 0;
        const marginRight = parseFloat(pictureItemStyle.marginRight) || 0;
        itemWidth = pictureItem.offsetWidth + marginLeft + marginRight;
        const listPictureMaxWidth = itemWidth * this.configuration.pictureAutoWrapCount;
        this.$el.querySelector('.ant-upload-list-picture-card').style.cssText += `;max-width:${listPictureMaxWidth}px`;

        // const listPictureMaxWidth = 128 * this.configuration.pictureAutoWrapCount;
        // this.$el.querySelector('.ant-upload-list-picture-card').style.cssText += `;max-width:${listPictureMaxWidth}px`;

        this.pictureItemWidth = itemWidth;
      }
    },
    setPictureViewList() {
      const pictureActions = this.$el.querySelectorAll('.ant-upload-list-item-card-actions.picture');
      if (pictureActions.length && this.showPicturePreview) {
        for (let index = 0; index < pictureActions.length; index++) {
          const item = pictureActions[index];
          if (!item.querySelector('.icon-wsbs-xianshi')) {
            const previewNode = document.createElement('i');
            previewNode.setAttribute('class', 'iconfont icon-wsbs-xianshi');
            item.insertBefore(previewNode, item.firstChild);
            previewNode.addEventListener('click', () => {
              if (item.previousSibling && item.previousSibling.href) {
                const uid = item.previousSibling.href.split('=')[1];
                this.previewImage(this.fileUploadMap[uid]);
              }
            });
          }
          // if() {
          //    item.insertAdjacentHTML('afterbegin', `<span class="file-item-desc">${formatSize}</span>`);
          // }
        }
      }
    },
    getFileIcon(filename) {
      return getFileIcon(filename);
    },
    // 自定义图片上传请求
    customImageRequest(options, isLib) {
      if (options.file.type.indexOf('image/') !== 0) {
        this.$message.error(this.$t('WidgetFormFileUpload.message.uploadUnsupportedFormat', '上传文件非图片格式'));
        return;
      }
      if (this.isReplaceImgByLimitOne && !isLib) {
        // 图片限制一个，则进行替换
        this.onClickDelFile(this.fileList[0], 0);
      }
      this.addFileList(options.file, isLib);

      this.upload(options.file, { md5FileStored: false }, function () {
        options.onSuccess();
      });
    },
    // 自定义文件上传请求
    customRequest(options) {
      let _this = this;
      debugger;
      const fileResult = this.addFileList(options.file);
      this.setButtonControl(fileResult);
      // 计算文件MD5
      this.computeFileMD5(options.file, function () {
        // 校验文件是否存在，如果已经存在，则上传直接返回删除
        _this.loadFileChunkInfoByMD5(options.file, function (result) {
          _this.upload(options.file, result, function (_file) {
            _file.status = UPLOAD_STATUS.UPLOADING;
            _this.updateFileWatermark(_file).then(() => {
              _file.status = UPLOAD_STATUS.DONE;
              setTimeout(function () {
                // 进度条100%后延迟一点关闭
                _file.showProgress = false;
                options.onSuccess();
              }, 300);
            });
          });
        });
      });
    },
    updateFileWatermark(file) {
      return new Promise((resolve, reject) => {
        let fileWatermarkConfig = this.widget.configuration.fileWatermarkConfig;
        if (
          this.dyformFileWatermarkDisabled !== true &&
          fileWatermarkConfig &&
          fileWatermarkConfig.enabled &&
          // 上传文件时候就要设置水印
          fileWatermarkConfig.effectScope.includes('upload') &&
          // 仅支持以下文件水印
          (file.name.endsWith('.doc') ||
            file.name.endsWith('.docx') ||
            file.name.endsWith('.pdf') ||
            file.name.endsWith('.xls') ||
            file.name.endsWith('.xlsx') ||
            file.name.endsWith('.ppt') ||
            file.name.endsWith('.pptx') ||
            (file.orgFile && ['image/png', 'image/jpeg', 'image/jpg'].includes(file.orgFile.type)))
        ) {
          this.calculateDataConditionControlResult(this.widget.configuration.fileWatermarkConfig.effectConditionControl).then(match => {
            if (match) {
              let fileID = file.dbFile.fileID;
              let config = pick(this.widget.configuration.fileWatermarkConfig, [
                'imageFileId',
                'type',
                'scale',
                'opacity',
                'layout',
                'fontSize',
                'fontFamily',
                'fontColor',
                'horizontalAlign',
                'verticalAlign'
              ]);
              if (config.type == 'text') {
                config.text = this.resolveWatermarkFormulaText();
              }
              if (config.scale != undefined) {
                config.scale = (config.scale / 100).toFixed(2);
              }
              if (config.opacity != undefined) {
                config.opacity = (config.opacity / 100).toFixed(2);
              }
              this.$axios.post(`/proxy/repository/file/mongo/makeWatermark/${fileID}`, config).then(({ data }) => {
                resolve(true);
              });
            } else {
              resolve();
            }
          });
        } else {
          resolve();
        }
      });
    },

    upload(file, param, callback) {
      let _this = this,
        fileSize = file.size,
        fileName = file.name,
        chunkSize = this.chunkSize,
        md5FileStored = param.md5FileStored,
        chunkIndexList = param.chunkIndexList || [];
      let formData = new FormData();
      formData.set('frontUUID', file.uid);
      formData.set('md5', file.md5);
      formData.set('localFileSourceIcon', '');
      formData.set('size', fileSize);
      let source = '上传';
      if (this.curButton) {
        source = this.curButton.button.buttonName;
      }
      formData.set('source', source);
      if (this.curButton && this.curButton.button.code == 'onClickReplace') {
        formData.set('origUuid', this.replaceTemp.dbFile.fileID);
      }
      let chunk = chunkSize != undefined && fileSize > chunkSize;
      if (chunk) {
        formData.set('chunkSize', chunkSize);
      }
      let headers = {
        'Content-Disposition': `attachment; filename="${encodeURIComponent(fileName)}"`,
        'Content-Type': 'multipart/form-data'
      };
      let ajaxUpload = function (position) {
        let end = 0;
        if (chunk) {
          // 分块上传的数据
          end = position + chunkSize >= fileSize ? fileSize : position + chunkSize;
          let chunkFile = _this.blobSlice(file, position, end);
          formData.set('file', new File([chunkFile], fileName));
          headers['Content-Range'] = `bytes ${position}-${end - 1}/${fileSize}`;
        } else {
          formData.set('file', file);
        }
        const CancelToken = _this.$axios.CancelToken;
        const source = CancelToken.source();
        _this.cancelSourceList.push(source);
        _this.$axios
          .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
            headers: headers,
            cancelToken: source.token
          })
          .then(({ data }) => {
            console.log(data);
            if (data && data.success) {
              if (data.data === 'continue') {
                if (_this.fileUploadMap[file.uid]) {
                  _this.fileUploadMap[file.uid].percent = parseInt((end / fileSize) * 100);
                  ajaxUpload(end);
                }
              } else if (!chunk || (end == fileSize && Array.isArray(data.data) && data.data.length > 0)) {
                // 全部上传结束了
                _this.fileUploadMap[file.uid].status = UPLOAD_STATUS.DONE;
                _this.fileUploadMap[file.uid].percent = 100;
                data.data[0].createTimeStr = moment(data.data[0].createTime).format('YYYY-MM-DD HH:mm');
                _this.fileUploadMap[file.uid].dbFile = data.data[0];
                _this.fileUploadMap[file.uid].url = `/proxy-repository/repository/file/mongo/download?fileID=${data.data[0].fileID}`;
                _this.fileUploadMap[data.data[0].fileID] = _this.fileUploadMap[file.uid];
                if (typeof callback === 'function') {
                  callback.call(_this, _this.fileUploadMap[file.uid]);
                }
              }
            } else {
              if (_this.fileUploadMap && _this.fileUploadMap[file.uid]) {
                _this.fileUploadMap[file.uid].status = UPLOAD_STATUS.ERROR;
              }
            }
          })
          .catch(function (error) {
            if (
              error.response &&
              error.response.status == 400 &&
              error.response.data.message &&
              error.response.data.message.indexOf('Invalid filename') != -1
            ) {
              _this.$message.error('文件格式不允许');
            }
            console.error('上传文件失败, 异常: ', error);
            if (_this.fileUploadMap && _this.fileUploadMap[file.uid]) {
              _this.fileUploadMap[file.uid].status = UPLOAD_STATUS.ERROR;
            }
          });
      };
      let start = 0,
        chunkStoredCnt = chunkIndexList.length;
      if (chunkStoredCnt > 0) {
        // 断点续传，计算从哪个位置开始
        while (chunkStoredCnt-- > 0) {
          start = start + chunkSize >= fileSize ? fileSize : start + chunkSize;
        }
        this.fileUploadMap[file.uid].percent = parseInt((start / fileSize) * 100); // 更新已上传的进度
      }
      ajaxUpload(md5FileStored ? fileSize - 2 : start);
    },
    loadFileChunkInfoByMD5(file, callback) {
      if (this.chunkSize == undefined) {
        // 无分块上传的情况
        callback(false);
        return;
      }
      let params = {
        md5: file.md5,
        chunkSize: this.chunkSize
      };

      let _this = this;
      const CancelToken = _this.$axios.CancelToken;
      const source = CancelToken.source();
      _this.cancelSourceList.push(source);
      this.$axios
        .get('/proxy-repository/repository/file/mongo/getFileChunkInfo', {
          params: params,
          cancelToken: source.token
        })
        .then(({ data }) => {
          console.log(data);
          if (data.success) {
            _this.md5FileObj[params.md5] = data.data;
            // 可能存在文件名不同，文件内容相同的情况，分块进行去重
            let chunkIndexList = data.data.chunkIndexList.length ? Array.from(new Set(data.data.chunkIndexList)) : [];
            callback({ md5FileStored: data.data.hasMd5FileFlag, chunkIndexList });
          }
        })
        .catch(error => {
          if (_this.fileUploadMap && _this.fileUploadMap[file.uid]) {
            _this.fileUploadMap[file.uid].status = UPLOAD_STATUS.ERROR;
          }
        });
    },
    // 全选
    onAdvancedSelectAll(params) {
      const spanEl = params.evt.target.querySelector('span');
      if (this.unselectAll) {
        //反选
        this.rowSelection.selectedRows = [];
        this.rowSelection.selectedRowKeys = [];
        spanEl.textContent = this.$t('WidgetFormFileUpload.button.selectAll', '全选');
        return;
      }
      this.rowSelection.selectedRows = [];
      this.rowSelection.selectedRowKeys = [];
      for (let i = 0, len = this.fileList.length; i < len; i++) {
        this.rowSelection.selectedRowKeys.push(this.fileList[i].uid);
        this.rowSelection.selectedRows.push(this.fileList[i]);
      }
      if (this.rowSelection.selectedRows.length) {
        spanEl.textContent = this.$t('WidgetFormFileUpload.button.cancelSelectAll', '取消全选');
      }
    },
    // 全部下载
    onClickAllDownload() {
      let ids = [];
      this.fileList.forEach(item => {
        if (this.configuration.downloadAllType == '2') {
          this.onClickDownloadFile(item);
        } else {
          ids.push({
            fileID: item.dbFile.fileID
          });
        }
      });
      if (this.configuration.downloadAllType == '1') {
        this.downAllFilesPre(ids);
      }
    },
    // 图片批量下载
    onPictureBatchDownload() {
      if (this.rowSelection.selectedRows.length) {
        let ids = [];
        for (let i = 0, len = this.rowSelection.selectedRows.length; i < len; i++) {
          if (this.configuration.downloadAllType == '2') {
            // 1压缩包 2下载源文件
            this.onClickDownloadFile(this.rowSelection.selectedRows[i]);
          } else {
            ids.push({
              fileID: this.rowSelection.selectedRows[i].dbFile.fileID
            });
          }
        }
        if (this.configuration.downloadAllType == '1') {
          this.downAllFilesPre(ids);
        }
      } else {
        this.$message.warn(this.$t('WidgetFormFileUpload.pleaseSelectPicture', '请选择图片'));
      }
    },
    // 批量下载
    onAdvancedBatchDownload() {
      if (this.rowSelection.selectedRows.length) {
        let ids = [];
        for (let i = 0, len = this.rowSelection.selectedRows.length; i < len; i++) {
          if (this.configuration.downloadAllType == '2') {
            // 1压缩包 2下载源文件
            this.onClickDownloadFile(this.rowSelection.selectedRows[i]);
          } else {
            ids.push({
              fileID: this.rowSelection.selectedRows[i].dbFile.fileID
            });
          }
        }
        if (this.configuration.downloadAllType == '1') {
          this.downAllFilesPre(ids);
        }
      }
    },
    // 下载前，获取下载文件标题
    downAllFilesPre(ids) {
      let fileName = `${this.$t(this.widget.id, null)}`;
      if (!fileName) {
        if (this.widgetSubformContext && this.$attrs && this.$attrs.isSubformCell) {
          if (this.widgetSubformContext.columns) {
            const fileColumn = this.widgetSubformContext.columns.find(col => col.dataIndex === this.fieldCode);
            const subformWidgetId = this.widgetSubformContext.widget.id;
            fileName = this.$t(`Widget.${subformWidgetId}.${fileColumn.id}`, this.configuration.name);
          }
        }
      }
      if (this.$workView && this.$workView.workData.title) {
        // FIXME: 暂时通过替换流程标题名称实现国际化
        if (!fileName) {
          fileName = this.widget.configuration.name;
        }
        fileName += `-${this.$workView.workData.title.replace(
          new RegExp(this.$workView.workData.title, 'g'),
          this.$t('WorkflowView.workflowName', this.$workView.workData.title)
        )}`;
        this.downAllFilesReq(ids, fileName);
      } else {
        let dyFormData = this.dyFormData;
        if (!dyFormData && this.form) {
          dyFormData = {
            formUuid: this.form.formUuid,
            formDatas: {}
          };
          dyFormData[this.form.formUuid] = deepClone(this.form.formData);
        }
        if (dyFormData) {
          if (!fileName) {
            fileName = this.widget.configuration.name;
          }
          this.getTitle(dyFormData).then(dyformTitle => {
            fileName += `-${dyformTitle}`;
            this.downAllFilesReq(ids, fileName);
          });
        } else {
          this.downAllFilesReq(ids, fileName);
        }
      }
    },
    // 下载
    downAllFilesReq(ids, fileName) {
      this.getWatermarkUrlQueryParams().then(urlQueryParams => {
        this.downloadFileSilence(
          `/proxy-repository/repository/file/mongo/downAllFiles?fileIDs=${encodeURIComponent(
            JSON.stringify(ids)
          )}&includeFolder=false&fileName=${fileName}${urlQueryParams}`
        );
      });
    },
    ifWatermarkEffectConditionMatched() {
      let effectConditionControl = this.widget.configuration.fileWatermarkConfig.effectConditionControl;
      if (effectConditionControl && effectConditionControl.enabled && effectConditionControl.conditions.length > 0) {
        // 判断条件成立才进行水印处理
      }
      return true;
    },
    resolveWatermarkFormulaText() {
      let fileWatermarkConfig = this.widget.configuration.fileWatermarkConfig;
      if (fileWatermarkConfig.textFormula && fileWatermarkConfig.textFormula.value) {
        try {
          let compiler = stringTemplate(fileWatermarkConfig.textFormula.value);
          return compiler(this.widgetDependentVariableDataSource());
        } catch (error) {
          console.error(error);
        }
      }
      return '';
    },
    getWatermarkUrlQueryParams() {
      return new Promise((resolve, reject) => {
        let fileWatermarkConfig = this.widget.configuration.fileWatermarkConfig;
        if (
          this.dyformFileWatermarkDisabled !== true &&
          fileWatermarkConfig &&
          fileWatermarkConfig.enabled &&
          fileWatermarkConfig.effectScope.includes('download')
        ) {
          this.calculateDataConditionControlResult(this.widget.configuration.fileWatermarkConfig.effectConditionControl).then(match => {
            if (match) {
              let config = pick(fileWatermarkConfig, [
                'imageFileId',
                'type',
                'scale',
                'opacity',
                'layout',
                'fontSize',
                'fontFamily',
                'fontColor',
                'horizontalAlign',
                'verticalAlign'
              ]);
              if (config.type == 'text') {
                config.text = this.resolveWatermarkFormulaText();
              }
              if (config.scale != undefined) {
                config.scale = (config.scale / 100).toFixed(2);
              }
              if (config.opacity != undefined) {
                config.opacity = (config.opacity / 100).toFixed(2);
              }
              resolve(`&watermark=${encodeURIComponent(JSON.stringify(config))}`);
            } else {
              resolve('');
            }
          });
        } else {
          resolve('');
        }
      });
    },
    // 批量删除
    onAdvancedBatchDel() {
      if (this.rowSelection.selectedRowKeys.length) {
        for (let i = 0; i < this.fileList.length; i++) {
          let j = this.rowSelection.selectedRowKeys.indexOf(this.fileList[i].uid);
          if (j != -1) {
            this.onClickDelFile(this.fileList[i], i--);
            this.rowSelection.selectedRowKeys.splice(j, 1);
            this.rowSelection.selectedRows.splice(j, 1);
          }
        }
      }
    },
    // 设置行属性
    customRow(row, index) {
      return {
        on: {
          // 事件
          click: event => {
            // 行单击选中
          },
          // dblclick: event => {},
          // contextmenu: event => {},
          mouseenter: event => {
            row.hovered = true;
          }, // 鼠标移入行
          mouseleave: event => {
            row.hovered = false;
          }
        }
      };
    },
    // 按钮功能转发
    onListButtonClicked(params) {
      this.curButton = params;
      let { button } = params;
      if (button && button.code) {
        this[button.code](params);
      }
    },
    // 添加附件
    onAdvancedAddFile() {
      this.$refs.advancedUpload.$el.querySelector('input').click();
    },
    // 选中附件
    onSelectFileRowChange(selectedRowKeys, selectedRows) {
      this.rowSelection.selectedRowKeys = selectedRowKeys;
      this.rowSelection.selectedRows = selectedRows;
    },
    // 切换高级视图（列表、表格、图标）
    onSwitchAdvancedView(e) {
      this.advancedFileListType = e.target.value;
    },
    // 上移
    onClickMoveUp() {
      this.onAdvancedListMoveItem('forward');
    },
    onAdvancedListMoveItem(direction) {
      let ids = [];
      if (this.rowSelection.selectedRows.length) {
        for (let i = 0, len = this.rowSelection.selectedRows.length; i < len; i++) {
          if (this.rowSelection.selectedRows[i].dbFile) {
            ids.push(this.rowSelection.selectedRows[i].dbFile.fileID);
          } else {
            ids.push(this.rowSelection.selectedRows[i].uid);
          }
        }
      } else {
        if (this.curButton.file.dbFile) {
          ids = [this.curButton.file.dbFile.fileID];
        } else {
          ids = [this.curButton.file.uid];
        }
      }
      swapArrayElements(
        ids,
        this.fileList,
        function (a, b) {
          return a == b.uid || (b.dbFile && a === b.dbFile.fileID);
        },
        direction,
        () => {}
      );

      swapArrayElements(
        ids,
        this.formData[this.fieldCode],
        function (a, b) {
          return a == b.fileID;
        },
        direction,
        () => {}
      );
    },
    // 下移
    onClickMoveDown() {
      this.onAdvancedListMoveItem();
    },
    onAdvancedIconViewCheckChange(e, file, index) {
      if (e.target.checked) {
        if (this.fileIndexMap == undefined) {
          this.fileIndexMap = {};
        }
        this.rowSelection.selectedRowKeys.push(file.uid);
        this.rowSelection.selectedRows.push(file);
        this.fileIndexMap[index] = file;
        this.rowSelection.selectedRows = [];
        let keys = Object.keys(this.fileIndexMap);
        for (let k in keys) {
          this.rowSelection.selectedRows.push(this.fileIndexMap[k]);
        }
      } else {
        let i = this.rowSelection.selectedRowKeys.indexOf(file.uid);
        this.rowSelection.selectedRowKeys.splice(i, 1);
        this.rowSelection.selectedRows.splice(i, 1);
      }
    },
    formatSize(size, pointLength, units) {
      var unit;
      units = units || ['B', 'KB', 'MB', 'GB', 'TB'];
      while ((unit = units.shift()) && size > 1024) {
        size = size / 1024;
      }
      return (unit === 'B' ? size : size.toFixed(pointLength || 2)) + unit;
    },
    getChunkSize() {
      //切片每次上传的块最大值
      var __size = (function (name) {
        var arr = document.cookie.match(new RegExp('(^| )' + name + '=([^;]*)(;|$)'));
        return arr ? decodeURIComponent(arr[2]) : null;
      })('fileupload.maxChunkSize');

      this.chunkSize = __size ? parseInt(__size) : undefined;
      return this.chunkSize;
    },
    // 获取预览服务地址
    getPreviewHostServer() {
      let _this = this;
      this.$tempStorage.getCache(
        'document_preview_path',
        () => {
          return new Promise((resolve, reject) => {
            this.$clientCommonApi.getSystemParamValue('document.preview.path', function (path) {
              resolve(path);
            });
          });
        },
        path => {
          _this.previewServerHost = path;
        }
      );
    },
    buttonClicked() {},
    displayValue(v, template) {
      return JSON.parse(JSON.stringify(this.formData[this.fieldCode]));
    },
    setValue(v) {
      this.formData[this.fieldCode] = v;
      this.fileList.splice(0, this.fileList.length);
      if (v && v.length) {
        // 设置的对象只有fileID属性
        if (Object.keys(v[0]).length == 1) {
          this.loadLogicFileByFiles(v).then(logicFiles => {
            this.setValue(logicFiles);
          });
        } else {
          for (let i = 0, len = v.length; i < len; i++) {
            this.addFileList({
              uid: v[i].fileID,
              name: v[i].fileName || v[i].filename,
              size: v[i].fileSize,
              status: UPLOAD_STATUS.DONE,
              dbFile: v[i]
            });
          }
        }
      }
    },
    loadLogicFileByFiles(files) {
      let fileIDs;
      if (typeof files === 'string') {
        fileIDs = files;
      } else if (Array.isArray(files)) {
        fileIDs = files.map(file => file.fileID).join(';');
      } else if (typeof files === 'object' && files.fileID) {
        fileIDs = files.fileID;
      } else {
        fileIDs = files;
      }
      return $axios
        .get(`/proxy/repository/file/mongo/getNonioFiles?fileID=${fileIDs}`)
        .then(({ data: result }) => {
          if (result.data && result.data.length) {
            return result.data;
          }
          return [];
        })
        .catch(() => {
          return files;
        });
    },
    addDbFiles(dbFiles) {
      if (dbFiles && dbFiles.length) {
        this.formData[this.fieldCode] = this.formData[this.fieldCode].concat(dbFiles);
        dbFiles.forEach(dbFile => {
          dbFile.userName = this._$USER.userName;
          this.addFileList({
            uid: dbFile.fileID,
            name: dbFile.fileName || dbFile.filename,
            size: dbFile.fileSize,
            status: UPLOAD_STATUS.DONE,
            dbFile
          });
        });
      }
    },
    fetchFileCreatorName(creatorIds, dbFiles = []) {
      $axios.post('/proxy/api/org/organization/element/getNameByOrgEleIds', creatorIds).then(({ data: result }) => {
        if (result.data) {
          let idNameMap = result.data;
          this.fileList.forEach(file => {
            if (file.dbFile && idNameMap.hasOwnProperty(file.dbFile.creator)) {
              file.dbFile.userName = idNameMap[file.dbFile.creator];
            }
          });
        }
      });
    },
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
            let fileSourceOptions = [];
            let fileSouces = data.data || [];
            let fileSourceIds = this.widget.configuration.fileSourceIds;
            fileSouces.forEach(fileSource => {
              if (fileSourceIds.findIndex(sourceId => sourceId == fileSource.uuid) >= 0) {
                if (!this.designMode) {
                  if (fileSource.i18ns && fileSource.i18ns.length) {
                    let i18n = this.i18nsToI18n(fileSource.i18ns, 'defId');
                    this.mergeWidgetI18nMessages(i18n);
                  }
                }
                fileSourceOptions.push(fileSource);
              }
            });
            this.fileSourceOptions = fileSourceOptions;
          }
        });
    },
    fetchRelatedMaterialAccept() {
      const _this = this;
      let relatedMaterial = _this.widget.configuration.relatedMaterial;
      if (!relatedMaterial || !relatedMaterial.enabled) {
        return;
      }

      let materialCodes = relatedMaterial.materialCodes;
      let materialCodeFields = relatedMaterial.materialCodeFields;
      // 指定材料编码字段
      if (relatedMaterial.way == '2') {
        materialCodes = [];
        materialCodeFields.forEach(codeField => {
          let codeValue = _this.formData[codeField];
          if (codeValue) {
            materialCodes.push(codeValue);
          }
          // 监听材料编码值变更
          if (!_this.watchRelatedMaterialCode) {
            _this.$watch('formData.' + codeField, (newValue, oldValue) => {
              _this.fetchRelatedMaterialAccept();
            });
            _this.watchRelatedMaterialCode = true;
          }
        });
      }

      if (!materialCodes.length) {
        return;
      }

      $axios.get(`/proxy/api/material/definition/listFormatByCodes?codes=${materialCodes}`).then(({ data: result }) => {
        _this.materialAccept = result.data || [];
      });
    },
    onFileSourceButtonClick: function (event, button) {
      // 本地上传
      if ('local_upload' == button.code) {
        this.$refs.simpleUpload.$el.querySelector('input').click();
      } else if (button.jsModule && /^{.*}$/gs.test(button.jsModule)) {
        let buttonJsModule = JSON.parse(button.jsModule);
        if (this.__developScript && this.__developScript[buttonJsModule.jsModule]) {
          let developJsInstance = new this.__developScript[buttonJsModule.jsModule].default(this);
          if (developJsInstance.chooseFile) {
            developJsInstance.chooseFile();
          }
        }
      }
    },
    // 获取视图图标
    getFileViewTypeIcon(type) {
      return { listView: 'menu', tableView: 'table', iconView: 'appstore' }[type];
    },
    // 获取创建附件用户信息
    fetchOrgUserDetail(item) {
      if (!this.userNameMap[item.creator]) {
        this.userNameMap[item.creator] = { files: [], userName: '' };
        $axios
          .get(`/proxy/api/user/org/getUserDetailsUnderSystem`, {
            params: {
              userId: item.creator
            }
          })
          .then(({ data }) => {
            this.userNameMap[item.creator].userName = data.data.userName;
            each(this.userNameMap[item.creator].files, citem => {
              let hasIndex = findIndex(this.fileList, { uid: citem.fileID });
              if (hasIndex > -1) {
                this.$set(this.fileList[hasIndex].dbFile, 'userName', data.data.userName);
              }
            });
          });
      }
      this.userNameMap[item.creator].files.push(item);
    },
    // 文件名点击事件(系统参数配置)
    fileNameClick(file) {
      if (!this.fileNameHandler) {
        this.$tempStorage.getCache(
          'pt.filename.click.type',
          () => {
            return new Promise((resolve, reject) => {
              this.$clientCommonApi.getSystemParamValue('pt.filename.click.type', type => {
                resolve(type);
              });
            });
          },
          type => {
            if (type) {
              if (type == 'preview') {
                this.fileNameHandler = 'onClickPreviewFile';
                this.fileNameHandlerName = '预览';
                if (file) {
                  this.onClickPreviewFile({ file, button: {} });
                }
              } else if (type == 'download') {
                this.fileNameHandler = 'onClickDownloadFile';
                this.fileNameHandlerName = '下载';
                if (file) {
                  this.onClickDownloadFile({ file });
                }
              }
            }
          }
        );
      } else {
        if (file) {
          this[this.fileNameHandler]({ file, button: {} });
        }
      }
    },

    onFilter({ searchValue, comparator, source, ignoreCase }) {
      if (source != undefined) {
        // 文件组件都按模糊匹配处理
        if (Array.isArray(source)) {
          for (let i = 0, len = source.length; i < len; i++) {
            let fileName = source[i].filename || source[i].fileName;
            if (ignoreCase ? fileName.toLowerCase().indexOf(searchValue.toLowerCase()) != -1 : fileName.indexOf(searchValue) != -1) {
              return true;
            }
          }
        }
        return false;
      }
      //TODO: 判断本组件值是否匹配
      return false;
    },
    addCustomRules() {
      return this.addUploadErrorRules();
    },
    // 增加附件上传失败校验
    addUploadErrorRules() {
      let upload_rule = {
        trigger: 'change',
        validator: (rule, value, callback) => {
          if (this.fileList.length) {
            let uploading = false;
            let error = false;
            let ispass = every(this.fileList, item => {
              if (item.status == UPLOAD_STATUS.UPLOADING) {
                uploading = true;
              } else if (item.status == UPLOAD_STATUS.ERROR) {
                error = true;
              }
              // 状态存在未完成的附件
              return !item.status || item.status == UPLOAD_STATUS.DONE;
            });
            if (!ispass) {
              if (error) {
                callback(this.$t('WidgetFormFileUpload.validateError.error', '存在上传异常的附件,请确认后继续'));
              } else if (uploading) {
                callback(this.$t('WidgetFormFileUpload.validateError.uploading', '正在上传附件，请稍后'));
              } else {
                callback(this.$t('WidgetFormFileUpload.validateError.error', '存在上传异常的附件,请确认后继续'));
              }
              return;
            }
          }
          callback();
        }
      };
      if (this.rules) {
        this.rules.push(upload_rule);
      }
      return upload_rule;
    },
    invokeTranslateApi(word, from = 'zh', t) {
      return new Promise((resolve, reject) => {
        let to = t || (this.$i18n.locale && this.$i18n.locale.split('_')[0]);
        this.$translate(word, from, to)
          .then(result => {
            resolve(result);
          })
          .catch(error => {});
      });
    },
    getGlobalWatermarkDisabled() {
      let _this = this;
      return new Promise((resolve, reject) => {
        if (_this.dyformFileWatermarkDisabled != undefined) {
          resolve(_this.dyformFileWatermarkDisabled);
          return;
        }
        this.$tempStorage.getCache(
          'getGlobalWatermarkDisabled',
          () => {
            return new Promise((resolve, reject) => {
              this.$clientCommonApi.getSystemParamValue('dyform.file.watermark.disabled', function (disalbed) {
                resolve(disalbed);
              });
            });
          },
          disalbed => {
            _this.dyformFileWatermarkDisabled = disalbed === 'true';
            resolve(_this.dyformFileWatermarkDisabled);
          }
        );
      });
    }
  },
  beforeUpdate() {
    //FIXME: 处理表单字段值发生变化后进行格式化
  },
  watch: {
    widget: {
      deep: true,
      handler(v) {
        // 可能多次更新初始组件数据，组件创建时对按钮的处理会失效，得重新处理
        this.widgetCreatedButtonSetting();
      }
    }
  }
};
</script>
