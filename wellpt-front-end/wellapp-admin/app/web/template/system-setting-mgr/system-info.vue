<template>
  <a-page-header class="system-setting-system-info">
    <template slot="title" v-if="!loading">
      <a-avatar :size="64">
        <Icon :type="form.product.icon || 'appstore'" slot="icon" />
      </a-avatar>
      {{ form.product.name + (form.product.versionName ? ' - ' + form.product.versionName : '') }}
    </template>
    <template slot="subTitle">
      <a-skeleton active avatar :paragraph="{ rows: 1 }" :loading="loading" :title="{ width: 200 }"></a-skeleton>
      <template v-if="!loading">
        <div class="remark">{{ form.product.remark }}</div>
        <div class="version">
          版本
          <a-button size="small" type="link">{{ form.product.version }}</a-button>
          <label v-if="form.product.latestVersion != undefined" class="new-version">新版本</label>
        </div>
      </template>
    </template>

    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
    <a-form-model :model="form" :rules="rules" ref="form" layout="vertical" style="padding-left: 80px">
      <a-form-model-item label="系统显示名称" prop="title">
        <a-input v-model.trim="form.title" :maxLength="64" style="width: 400px" />
      </a-form-model-item>
      <a-form-model-item label="系统icon">
        <ImageLibrary
          v-model="form.favicon"
          width="64px"
          height="64px"
          :limitSize="10"
          :acceptType="acceptTypes"
          :acceptTip="acceptTip"
          :emptyVisible="false"
        />
        <div class="img-upload-tip">在浏览器标签页中显示icon, 建议格式: jpeg/png/ico; 大小: 64 x 64</div>
      </a-form-model-item>
      <a-form-model-item label="系统管理后台标题">
        <a-input v-model.trim="form.adminTitle" :maxLength="64" style="width: 400px" />
      </a-form-model-item>
      <a-form-model-item label="系统管理后台logo">
        <ImageLibrary
          v-model="form.adminLogo"
          width="300px"
          height="100px"
          :limitSize="50"
          :acceptType="acceptTypes"
          :acceptTip="acceptTip"
          :emptyVisible="false"
        />
        <div class="img-upload-tip">在管理后台中显示的系统logo, 建议格式: jpeg/png</div>
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          启用系统国际化
          <a-checkbox v-model="form.enableLocale" />
        </template>
        <a-space v-show="form.enableLocale">
          <label style="color: #9a9a9a; font-size: 12px">默认语言环境</label>
          <a-select :options="i18nOption" style="width: 200px" v-model="form.defaultLocale" />
        </a-space>
      </a-form-model-item>
    </a-form-model>
    <div style="text-align: center">
      <a-button type="primary" :loading="saving" @click.stop="onClickSave">保存</a-button>
    </div>
  </a-page-header>
</template>
<style lang="less">
.system-setting-system-info {
  height: 100%;
  .ant-page-header-heading .ant-page-header-heading-title {
    padding-left: 80px;
    > .ant-avatar {
      background: var(--w-primary-color-3);
      color: var(--w-primary-color);
      position: absolute;
      left: 20px;
      top: 20px;
      display: -webkit-box;
      display: -webkit-flex;
      display: flex;
      -webkit-box-align: center;
      -webkit-align-items: center;
      align-items: center;
      -webkit-box-pack: center;
      -webkit-justify-content: center;
      justify-content: center;
    }
  }

  .ant-page-header-heading-sub-title {
    clear: both;
    width: 100%;
    padding-left: 80px;
    > div {
      line-height: 20px;
    }
    > div.remark {
      max-width: calc(100% - 80px);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    > div.version {
      .new-version {
        color: red;
        text-decoration: underline;
      }
    }
  }

  .img-upload-tip {
    line-height: 1.3;
    color: #9a9a9a;
    font-size: 12px;
    padding-top: 7px;
  }
  .transparent-bg {
    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABGdBTUEAALGPC/xhBQAAAFpJREFUWAntljEKADAIA23p6v//qQ+wfUEcCu1yriEgp0FHRJSJcnehmmWm1Dv/lO4HIg1AAAKjTqm03ea88zMCCEDgO4HV5bS757f+7wRoAAIQ4B9gByAAgQ3pfiDmXmAeEwAAAABJRU5ErkJggg==')
      0% 0% / 28px;
  }
}
</style>
<script type="text/babel">
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
import { getElSpacingForTarget } from '@framework/vue/utils/util';
export default {
  name: 'SystemInfo',
  inject: ['pageContext'],
  props: {},
  components: { ImageLibrary },
  computed: {},
  data() {
    return {
      rules: {},
      loading: false,
      saving: false,
      form: {
        uuid: undefined,
        title: undefined,
        adminLogo: undefined,
        adminTitle: undefined,
        favicon: undefined,
        enableLocale: true,
        defaultLocale: 'zh_CN',
        product: {
          version: undefined,
          versionName: undefined,
          name: undefined,
          remark: undefined,
          latestVersion: undefined,
          icon: undefined
        }
      },
      acceptTypes: ['image/jpeg', 'image/png', 'image/x-icon'],
      acceptTip: '只允许上传 jpeg、png 或者 ico 图片格式',
      i18nOption: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this._$SYSTEM_ID != undefined) {
      this.fetchLocaleOptions();
      this.getSystemInfo().then(d => {
        this.form.system = d.system;
        this.form.tenant = d.tenant;
        this.form.title = d.title;
        this.form.adminLogo = d.adminLogo;
        this.form.adminTitle = d.adminTitle;
        this.form.favicon = d.favicon;
        this.form.prodVersionUuid = d.prodVersionUuid;
        this.form.uuid = d.uuid;
        this.form.defaultLocale = d.defaultLocale;
        this.form.enableLocale = d.enableLocale;
        this.getProdVersionInfo(d.prodVersionUuid).then(v => {
          this.loading = false;
          this.form.product.version = v.version;
          this.form.product.name = v.product.name;
          this.form.product.versionName = v.product.versionName;
          this.form.product.remark = v.product.remark;
          this.form.product.icon = v.product.icon;
          if (v.product.latestVersion.uuid != this.form.prodVersionUuid) {
            this.form.product.latestVersion = v.product.latestVersion;
          }
        });
      });
    }
  },
  mounted() {
    this.setElMaxHeightFromOuterEl(this.$el.querySelector('.ant-form'), this.$el.parentElement);
  },
  methods: {
    fetchLocaleOptions() {
      this.$axios
        .get(`/proxy/api/app/codeI18n/getAllLocales`, { params: {} })
        .then(({ data }) => {
          if (data.code == 0) {
            for (let d of data.data) {
              this.i18nOption.push({
                label: d.name,
                value: d.locale,
                description: d.remark || d.name,
                transCode: d.translateCode
              });
            }
          }
        })
        .catch(error => {});
    },
    setElMaxHeightFromOuterEl(el, target) {
      const { maxHeight, totalBottom, totalNextSibling } = getElSpacingForTarget(el, target);
      el.style.cssText += `;overflow-y:auto; height:${maxHeight - 2}px`;
      return { maxHeight, totalBottom, totalNextSibling };
    },
    clickUploadReplaceImg(className) {
      this.$el.querySelector('.' + className).click();
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
    beforeUpload(file, limitSize) {
      return new Promise((resolve, reject) => {
        let isJpgOrPng = ['image/jpeg', 'image/png', 'image/x-icon'].includes(file.type);
        console.log(file);
        if (!isJpgOrPng) {
          this.$message.error('只允许上传 jpeg、png 或者 ico 图片格式');
        }
        let limit = true;
        if (limitSize != undefined) {
          limit = file.size / 1024 / 1024 < limitSize;
          if (!limit) {
            this.$message.error(`图片大小应小于 ${limitSize}M`);
          }
        }

        if (isJpgOrPng && limit) {
          //判断图片的宽和高是否相等
          // let reader = new FileReader();
          // reader.onload = function (e) {
          //   let data = e.target.result;
          //   //加载图片获取图片真实宽度和高度
          //   let img = new Image();
          //   img.src = data;
          //   img.onload = () => {
          //     console.log(img.width, img.height);
          //   };
          // };
          // reader.readAsDataURL(file);
          resolve(file);
        } else {
          reject();
        }
      });
    },
    onClickSave() {
      this.saving = true;
      $axios
        .post(`/proxy/api/system/saveSystemInfo`, this.form)
        .then(({ data }) => {
          if (data.code == 0) {
            this.$message.success('保存成功');
            this.pageContext.emitEvent(`MainWidgetLayoutHeader:TitleLogoUpdate`, {
              title: this.form.adminTitle,
              logo: this.form.adminLogo
            });
            document.title = this.form.title;
            this.saving = false;
            if (!this.form.enableLocale) {
              var exp = new Date();
              exp.setTime(exp.getTime() - 60 * 1000);
              document.cookie = 'locale.' + this._$SYSTEM_ID + '=zh_CN;expires=' + exp.toGMTString() + '; path=/';
            }
            $axios
              .get(`/api/cache/deleteByKey`, {
                params: {
                  key: `TENANT_SYSTEM_INFO:${this._$USER.tenantId}:${this._$SYSTEM_ID}`
                }
              })
              .then(({ data }) => {})
              .catch(error => {});

            if (this.form.favicon) {
              document.querySelector('link[rel]').href = this.form.favicon;
            }
          } else {
            this.$message.error('保存失败');
          }
        })
        .catch(error => {
          this.$message.error('保存失败');
          this.saving = false;
        });
    },
    getProdVersionInfo(uuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/version/details`, { params: { uuid } })
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {});
      });
    },
    getSystemInfo() {
      return new Promise((resolve, reject) => {
        this.loading = true;
        $axios
          .get(`/proxy/api/system/getTenantSystemInfo`, { params: { system: this._$SYSTEM_ID } })
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {});
      });
    }
  }
};
</script>
