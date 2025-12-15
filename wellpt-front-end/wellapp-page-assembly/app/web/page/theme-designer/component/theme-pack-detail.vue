<template>
  <a-form-model
    ref="form"
    :model="detail"
    :colon="false"
    :labelCol="labelCol"
    :wrapperCol="wrapperCol"
    class="theme-detail-form pt-form"
    :rules="rules"
  >
    <a-form-model-item label="主题名称" prop="name">
      <a-input v-model="detail.name">
        <template slot="addonAfter">
          <WI18nInput :code="detail.uuid" :target="detail" v-model="detail.name" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="主题类名" prop="themeClass">
      <a-input
        :disabled="detail.uuid != undefined"
        :addonBefore="themeClassPrefix"
        :value="classSuffix()"
        @change="e => mergeClassPrefix(e)"
      />
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="detail.remark" />
    </a-form-model-item>
    <a-form-model-item label="标签">
      <template v-for="(tuuid, i) in detail.tagUuids">
        <a-tag :key="tuuid" closable @close="removeThemeTag(i)" color="blue">
          {{ tagMap[tuuid] ? tagMap[tuuid].name : '' }}
        </a-tag>
      </template>
      <a-tag v-if="!selectTagVisible" style="background: #fff; border-style: dashed" @click="selectTag">
        <a-icon type="plus" />
        选择标签
      </a-tag>
      <span v-else="selectTagVisible" style="display: inline-block">
        <a-input-group compact v-if="newTagVisible || tags.length == 0" size="small">
          <a-input
            ref="input"
            type="text"
            size="small"
            :style="{ width: '120px' }"
            v-model="newTagName"
            @keyup.enter="saveNewTag"
          ></a-input>
          <a-button size="small" icon="check" @click="saveNewTag" type="primary" />
          <a-button size="small" icon="close" @click="cancelNewTag" type="danger" />
        </a-input-group>

        <a-input-group compact v-else size="small">
          <a-select allow-clear size="small" style="width: 120px" @change="onChangeSelectTag">
            <template v-for="(tag, i) in tags">
              <a-select-option v-if="!detail.tagUuids.includes(tag.uuid)" :key="'selectoption' + tag.uuid" :value="tag.uuid">
                {{ tag.name }}
              </a-select-option>
            </template>
          </a-select>
          <a-button icon="plus" @click="clickAddNewTag" size="small" />
          <a-button size="small" icon="close" @click="cancelNewTag" type="danger" />
        </a-input-group>
      </span>
    </a-form-model-item>
    <a-form-model-item label="LOGO">
      <!-- <a-input-group compact> -->
      <a-upload
        name="thumbnail"
        list-type="picture-card"
        class="logo-uploader"
        :show-upload-list="false"
        :before-upload="e => beforeUpload(e, 'logo')"
        :customRequest="e => customRequest(e, url => setTargetUrl(detail, 'logo', url))"
        :style="{ width: '40px', height: '40px' }"
      >
        <img v-if="detail.logo" :src="detail.logo" alt="avatar" style="width: 40px; height: 40px" />
        <div v-else>
          <a-icon :type="loading.logo ? 'loading' : 'plus'" />
          <div class="ant-upload-text"></div>
        </div>
      </a-upload>
      <!-- <a-select
          style="width: 100px"
          :options="[
            { label: '图片', value: 'picture' },
            { label: '图标', value: 'icon' }
          ]"
        />
      </a-input-group> -->
    </a-form-model-item>
    <a-form-model-item label="封面">
      <a-row>
        <a-col :span="15">
          <a-upload
            name="thumbnail"
            list-type="picture-card"
            :file-list="[]"
            class="thumbnail-uploader"
            :show-upload-list="false"
            :before-upload="e => beforeUpload(e, 'thumbnail')"
            :style="{ width: '310px', height: '210px' }"
            :customRequest="e => customRequest(e, url => setTargetUrl(detail, 'thumbnail', url))"
          >
            <img v-if="detail.thumbnail" :src="detail.thumbnail" alt="avatar" style="width: 300px; height: 200px" />
            <div v-else>
              <a-icon :type="loading.thumbnail ? 'loading' : 'plus'" />
              <div class="ant-upload-text">点击上传</div>
            </div>
          </a-upload>
        </a-col>
        <a-col :span="9">
          <ul class="upload-tip-ul">
            <li>支持格式: JPG、PNG、GIF</li>
            <li>大小限制: 不超过 {{ limitSize }}M</li>
            <li>建议尺寸: 300px * 200px</li>
          </ul>
        </a-col>
      </a-row>
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less">
.theme-detail-form {
  .upload-tip-ul {
    padding: 70px 0px 0px 0px;
    list-style: none;
    color: #b8b6b6;
    > li {
      line-height: 20px;
    }
  }
  .thumbnail-uploader {
    .ant-upload-select-picture-card {
      width: e('calc(300px + var(--w-picture-lr-padding) * 2)');
      height: e('calc(200px + var(--w-picture-lr-padding) * 2)');
    }
  }
  .logo-uploader {
    .ant-upload-select-picture-card {
      width: e('calc(40px + var(--w-picture-lr-padding) * 2)');
      height: e('calc(40px + var(--w-picture-lr-padding) * 2)');
    }
  }
}
</style>
<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import { debounce } from 'lodash';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'ThemePackDetail',
  props: {
    detail: Object
  },
  components: { Drawer, WI18nInput },
  computed: {
    tagMap() {
      let map = {};
      for (let tag of this.tags) {
        map[tag.uuid] = tag;
      }
      return map;
    }
  },
  data() {
    let rules = { name: [{ required: true, message: '名称必填', trigger: 'blur' }] };
    if (this.detail.uuid == undefined) {
      rules['themeClass'] = [
        { required: true, message: '主题类必填', trigger: 'blur' },
        { trigger: ['blur', 'change'], validator: this.validateThemeClass }
      ];
    }
    return {
      rules,
      themeClassPrefix: 'well-theme-',
      newTagVisible: false,
      selectTagVisible: false,
      newTagName: undefined,
      limitSize: 1,
      tags: [],
      labelCol: { span: 3 },
      wrapperCol: { span: 20 },
      loading: {
        thumbnail: false,
        logo: false
      }
    };
  },
  beforeCreate() {},
  created() {
    if (this.detail.tagUuids == undefined) {
      this.$set(this.detail, 'tagUuids', []);
    }
  },
  beforeMount() {
    this.queryTags();
  },
  mounted() {},
  methods: {
    validate() {
      return new Promise((resolve, reject) => {
        this.$refs.form.validate(valid => {
          if (valid) {
            resolve(valid);
          } else {
            reject();
          }
        });
      });
    },
    checkClass(value) {
      return new Promise((resolve, reject) => {
        $axios.get(`/proxy/api/theme/pack/getUuidByClass/${value}`, { params: {} }).then(({ data }) => {
          resolve((data.code == 0 && data.data) || data.code != 0);
        });
      });
    },
    validateThemeClass: debounce(function (rule, value, callback) {
      this.checkClass(value).then(exist => {
        callback(exist ? '主题类已存在' : undefined);
      });
    }, 500),

    mergeClassPrefix(e) {
      let value = e.target.value;
      if (value) {
        if (/[^a-zA-Z0-9_]/.test(value)) {
          this.$message.error('主题类名只允许包含字母、数字以及下划线');
        }
        value = value.replace(/[^a-zA-Z0-9_]/g, '');
      }
      this.detail.themeClass = this.themeClassPrefix + value;
    },
    classSuffix() {
      if (this.detail.themeClass) {
        return this.detail.themeClass.split(this.themeClassPrefix)[1];
      }
      return undefined;
    },
    removeThemeTag(i) {
      this.detail.tagUuids.splice(i, 1);
    },
    onChangeSelectTag(e) {
      if (e) {
        this.detail.tagUuids.push(e);
        this.selectTagVisible = false;
      }
    },
    drawerContainer() {
      return this.$el;
    },
    clickAddNewTag() {
      this.newTagVisible = true;
    },
    selectTag() {
      this.selectTagVisible = true;
      this.newTagVisible = false;
    },
    cancelNewTag() {
      this.newTagName = undefined;
      this.selectTagVisible = false;
      this.newTagVisible = false;
    },
    saveNewTag() {
      if (!this.newTagName) {
        return;
      }
      for (let i = 0, len = this.tags.length; i < len; i++) {
        if (this.tags[i].name === this.newTagName) {
          this.$message.error('标签名重复');
          return;
        }
      }

      $axios.get('/proxy/api/theme/tag/create', { params: { name: this.newTagName } }).then(({ data }) => {
        if (data.data) {
          this.detail.tagUuids.push(data.data);
          this.tags.push({ uuid: data.data, name: this.newTagName });
          // this.newTagName = undefined;
          this.selectTagVisible = false;
          this.newTagVisible = false;
        }
      });
    },

    queryTags() {
      $axios.get('/proxy/api/theme/tag/list', {}).then(({ data }) => {
        if (data.data) {
          this.tags = data.data;
        }
      });
    },
    getBase64(img, callback) {
      const reader = new FileReader();
      reader.addEventListener('load', () => callback(reader.result));
      reader.readAsDataURL(img);
    },
    setTargetUrl(target, propName, url) {
      this.$set(target, propName, url);
    },
    customRequest(options, afterUpload) {
      this.uploading = true;
      let file = options.file,
        fileSize = file.size,
        fileName = file.name,
        formData = new FormData();
      formData.set('frontUUID', file.uid);
      formData.set('localFileSourceIcon', '');
      formData.set('size', fileSize);
      let headers = {
        'Content-Disposition': `attachment; filename="${encodeURIComponent(fileName)}"`,
        'Content-Type': 'multipart/form-data'
      };
      formData.set('file', file);
      $axios
        .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
          headers: headers
        })
        .then(({ data }) => {
          this.uploading = false;
          if (data.code == 0 && data.data) {
            options.onSuccess();
            if (typeof afterUpload == 'function') {
              afterUpload.call(this, `/proxy-repository/repository/file/mongo/download?fileID=${data.data[0].fileID}`);
            }
          }
        });
    },
    // customRequest(options, key) {
    //   this.loading[key] = true;
    //   let _this = this;
    //   this.getBase64(options.file, imageUrl => {
    //     _this.detail[key] = imageUrl;
    //     _this.loading[key] = false;
    //     options.file.status = 'done';
    //   });
    // },
    beforeUpload(file) {
      console.log(file);

      let isJpgOrPng = ['image/gif', 'image/jpeg', 'image/png'].includes(file.type);
      if (!isJpgOrPng) {
        this.$message.error('只允许上传 jpeg、png 或者 gif 图片格式');
      }
      let limit = file.size / 1024 / 1024 < this.limitSize;
      if (!limit) {
        this.$message.error(`图片大小应小于 ${this.limitSize}M`);
      }
      return isJpgOrPng && limit;
    }
  }
};
</script>
