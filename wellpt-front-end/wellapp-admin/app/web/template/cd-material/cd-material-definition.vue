<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model :model="form" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" :rules="rules" ref="form">
      <a-form-model-item label="名称" prop="name">
        <a-input v-model="form.name" />
      </a-form-model-item>
      <a-form-model-item label="编码" prop="code">
        <a-input v-model="form.code" :disabled="uuid != null" />
      </a-form-model-item>
      <a-form-model-item label="形式">
        <a-checkbox-group v-model="form.mediumTypes">
          <a-checkbox value="10" name="mediumType">原件</a-checkbox>
          <a-checkbox value="20" name="mediumType">复印件</a-checkbox>
          <a-checkbox value="30" name="mediumType">电子文档</a-checkbox>
        </a-checkbox-group>
      </a-form-model-item>
      <a-form-model-item v-show="showFormat" label="格式">
        <a-select mode="multiple" :options="formatOptions" style="width: 100%" v-model="form.formats" />
      </a-form-model-item>
      <a-form-model-item label="样例">
        <a-upload
          name="sampleRepoFile"
          :multiple="true"
          :default-file-list="defaultRepoFiles"
          :remove="handleFileRemove"
          action="/repository/file/mongo/savefiles"
          @change="handleFileChange"
        >
          <a-button>
            <a-icon type="upload" />
            上传
          </a-button>
        </a-upload>
      </a-form-model-item>
      <a-form-model-item label="说明">
        <a-input v-model="form.description" type="textarea" />
      </a-form-model-item>
    </a-form-model>
  </a-skeleton>
</template>

<script type="text/babel">
import { each as forEach } from 'lodash';
export default {
  name: 'CdMaterialDefinition',
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    let $event = this.$event;
    return {
      loading: $event && $event.meta && $event.meta.uuid != undefined,
      uuid: $event && $event.meta != undefined ? $event.meta.uuid : undefined,
      form: {},
      rules: {
        name: { required: true, message: '名称必填', trigger: ['blur', 'change'] },
        code: { required: true, message: '编码必填', trigger: ['blur', 'change'] }
      },
      formatOptions: [],
      defaultRepoFiles: [],
      sampleRepoFiles: []
    };
  },
  computed: {
    showFormat() {
      return (
        this.form.mediumTypes &&
        this.form.mediumTypes.findIndex(item => {
          return item == '30';
        }) != -1
      );
    }
  },
  beforeMount() {
    let _this = this;
    if (this.uuid) {
      this.getMaterialDetails(this.uuid);
    }
    _this.loadFormatOptions();
  },
  methods: {
    handleFileChange(info) {
      const _this = this;
      if (info.file.status === 'done') {
        let files = [];
        forEach(info.fileList, function (fileInfo) {
          if (fileInfo.response) {
            let data = fileInfo.response.data || [];
            for (let i = 0; i < data.length; i++) {
              info.file.url = `/proxy-repository/repository/file/mongo/download?fileID=${data[i].fileID}`;
              files.push(data[i]);
            }
          } else {
            files.push(fileInfo);
          }
        });
        _this.sampleRepoFiles = files;
      }
    },
    handleFileRemove(file) {
      if (file.response) {
        let data = file.response.data || [];
        forEach(data, fileInfo => {
          const index = this.sampleRepoFiles.findIndex(v => v.fileID === fileInfo.fileID);
          if (index != -1) {
            this.sampleRepoFiles.splice(index, 1);
          }
        });
      } else if (file.uid) {
        const index = this.sampleRepoFiles.findIndex(v => v.fileID === file.uid);
        if (index != -1) {
          this.sampleRepoFiles.splice(index, 1);
        }
      }
    },
    loadFormatOptions() {
      this.$axios
        .post('/json/data/services', {
          args: JSON.stringify(['MATERIAL_FORMAT']), // 传数据字典的type
          methodName: 'listItemByDictionaryCode',
          serviceName: 'cdDataDictionaryFacadeService',
          validate: false
        })
        .then(({ data: { data: dicts = [] } }) => {
          dicts.forEach(dict => {
            this.formatOptions.push(dict);
          });
        });
    },
    save(event) {
      // 保存数据
      let _this = this;
      _this.$refs.form.validate(valid => {
        if (valid) {
          // 样例文件UUID
          let fileIds = [];
          forEach(_this.sampleRepoFiles, fileInfo => {
            fileIds.push(fileInfo.fileID);
          });
          _this.form.sampleRepoFileUuid = fileIds.join(';');

          $axios
            .post('/proxy/api/material/definition/save', _this.form)
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$message.success('保存成功');
                // 刷新表格
                _this.pageContext.emitEvent('UbSdIkDJtcseZNXIyNHxYLqkinfbOeia:refetch');
                event.$evtWidget.closeModal();
              }
            })
            .catch(error => {
              if (error.response && error.response.data && error.response.data.msg) {
                _this.$message.error(error.response.data.msg);
              } else {
                _this.$message.error('服务异常');
              }
            });
        }
      });
    },
    getMaterialDetails() {
      let _this = this;
      $axios.get(`/proxy/api/material/definition/get?uuid=${this.uuid}`, {}).then(({ data }) => {
        _this.loading = false;
        let material = data.data;
        if (data.code == 0 && material) {
          this.form = material;
          if (material.sampleFileInfos) {
            material.sampleFileInfos.forEach(item => {
              this.defaultRepoFiles.push({
                uid: item.fileID,
                fileID: item.fileID,
                name: item.fileName,
                status: 'done',
                url: `/proxy-repository/repository/file/mongo/download?fileID=${item.fileID}`
              });
            });
            this.sampleRepoFiles = material.sampleFileInfos;
          }
        }
      });
    }
  },
  META: {
    method: {
      save: '保存材料定义'
    }
  }
};
</script>

<style></style>
