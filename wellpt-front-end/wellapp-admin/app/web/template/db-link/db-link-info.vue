<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" class="pt-form" :colon="false">
    <div v-show="loading" class="spin-center">
      <a-spin />
    </div>
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="form.name" :maxLength="120" />
    </a-form-model-item>

    <a-form-model-item label="连接方式">
      <a-radio-group v-model="form.linkType" button-style="solid" size="small" @change="onChangeLinkType">
        <a-radio-button value="host">主机</a-radio-button>
        <a-radio-button value="url">URL</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <div v-show="form.linkType == 'host'">
      <a-form-model-item label="服务器地址" prop="host">
        <a-input v-model="form.host" />
      </a-form-model-item>
      <a-form-model-item label="端口" prop="port">
        <a-input-number style="width: 20%" v-model="form.port" :min="1" :max="65535" />
      </a-form-model-item>
      <a-form-model-item label="数据库" prop="sname" v-if="form.connectStype == 'sname'">
        <template v-if="form.dbType == 'oracle'">
          <a-input-group compact>
            <a-select
              style="width: 20%"
              v-model="form.connectStype"
              :options="[
                { label: '服务名', value: 'sname' },
                { label: 'SID', value: 'sid' }
              ]"
              @change="resetRules"
            />
            <a-input style="width: 80%" v-model="form.sname"></a-input>
          </a-input-group>
        </template>

        <a-input v-else v-model="form.sname" />
      </a-form-model-item>
      <a-form-model-item label="数据库" prop="sid" v-if="form.connectStype == 'sid'">
        <a-input-group compact>
          <a-select
            style="width: 20%"
            v-model="form.connectStype"
            @change="resetRules"
            :options="[
              { label: '服务名', value: 'sname' },
              { label: 'SID', value: 'sid' }
            ]"
          />
          <a-input style="width: 80%" v-model="form.sid"></a-input>
        </a-input-group>
      </a-form-model-item>
    </div>
    <div v-show="form.linkType == 'url'">
      <a-form-model-item label="url" prop="url">
        <a-input v-model="form.url" />
      </a-form-model-item>
    </div>

    <a-form-model-item label="用户名" prop="userName">
      <a-input v-model="form.userName" :maxLength="120" />
    </a-form-model-item>
    <a-form-model-item label="密码" prop="password">
      <a-input-password v-model="form.password" :maxLength="120" />
    </a-form-model-item>
    <a-form-model-item label="角色" prop="userRole" v-if="form.dbType == 'oracle'">
      <a-select
        v-model="form.userRole"
        :options="[
          { label: '普通用户', value: 'normal' },
          { label: '系统管理员', value: 'SYSDBA' },
          { label: '操作管理员', value: 'SYSOPER' }
        ]"
      />
    </a-form-model-item>
    <a-form-model-item label="连接驱动类名" prop="driverClass">
      <a-input v-model="form.driverClass" :maxLength="300" />
    </a-form-model-item>

    <a-form-model-item>
      <template slot="label">
        <a-tooltip title="支持上传jar包, 优先加载包内用于连接数据库的驱动类, 如无驱动类则从应用环境加载">
          驱动包
          <a-icon type="question-circle" />
        </a-tooltip>
      </template>

      <a-upload name="file" :showUploadList="false" :before-upload="e => beforeUpload(e, 100)" :customRequest="e => customRequest(e)">
        <div>
          <a-button
            size="small"
            type="link"
            :icon="form.driverJarFileName == undefined ? 'loading' : undefined"
            @click.stop="downloadFile"
            v-if="form.driverJarFile != undefined"
          >
            {{ form.driverJarFileName }}
          </a-button>
          <a-divider type="vertical" @click.stop="() => {}" v-if="form.driverJarFile != undefined" />
          <a-button size="small" type="link" :icon="uploading ? 'loading' : 'upload'" class="favicon-upload-button">
            {{ form.driverJarFile ? '替换' : '上传' }}
          </a-button>
          <a-button
            size="small"
            icon="delete"
            class="favicon-upload-button"
            type="link"
            v-if="form.driverJarFile != undefined"
            @click.stop="form.driverJarFile = undefined"
          >
            删除
          </a-button>
        </div>
      </a-upload>
    </a-form-model-item>

    <a-form-model-item label="备注" prop="remark">
      <a-textarea v-model="form.remark" :maxLength="300" />
    </a-form-model-item>
    <a-form-model-item label="其他连接参数">
      <div style="padding: 8px; background: #f6f6f6; border-radius: 4px; margin-top: 10px" v-show="paramList.length > 0">
        <a-input-group
          compact
          v-for="(item, index) in paramList"
          :key="index"
          :style="{
            marginBottom: index != paramList.length - 1 ? '8px' : 'unset'
          }"
        >
          <a-input v-model="item.label" style="width: 50%" placeholder="参数名">
            <template slot="suffix">=</template>
          </a-input>
          <a-input v-model="item.value" style="width: 50%" placeholder="参数值">
            <template slot="suffix">
              <a-button size="small" icon="delete" type="link" @click="paramList.splice(index, 1)">删除</a-button>
            </template>
          </a-input>
        </a-input-group>
      </div>
      <a-button
        size="small"
        icon="plus"
        type="link"
        @click="
          paramList.push({
            label: undefined,
            value: undefined
          })
        "
      >
        添加
      </a-button>
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import { customFileUploadRequest, downloadLink } from '@framework/vue/utils/function';

export default {
  name: 'DbLinkInfo',
  inject: ['$event', 'currentWindow', 'pageContext'],
  props: {
    showSave: {
      type: Boolean,
      default: false
    }
  },
  components: {},
  computed: {},
  data() {
    return {
      saving: false,
      form: {
        dbType: undefined,
        name: undefined,
        remark: undefined,
        linkType: 'host',
        connectStype: 'sname',
        userRole: 'normal',
        url: undefined,
        host: undefined,
        port: undefined,
        driverClass: undefined,
        driverJarFile: undefined,
        driverJarFileName: undefined,
        userName: undefined,
        password: undefined,
        sname: undefined,
        sid: undefined,
        param: undefined
      },
      labelCol: { span: 4 },
      wrapperCol: { span: 18 },
      rules: {
        name: [{ required: true, message: '必填', trigger: 'blur' }],
        port: [{ required: true, message: '必填', trigger: 'blur' }],
        host: [{ required: true, message: '必填', trigger: 'blur' }],
        url: [{ required: false, message: '必填', trigger: 'blur' }],
        userName: [{ required: true, message: '必填', trigger: 'blur' }],
        password: [{ required: true, message: '必填', trigger: 'blur' }],
        sname: [{ required: true, message: '必填', trigger: 'blur' }],
        sid: [{ required: false, message: '必填', trigger: 'blur' }]
      },
      dbTypeUrlPrefixMap: {
        oracle: 'jdbc:oracle:thin:@',
        mysql: 'jdbc:mysql://',
        kingbase: 'jdbc:kingbase8://',
        dameng: 'jdbc:dm://',
        sqlserver: 'jdbc:sqlserver://'
      },
      uploading: false,
      paramList: [],
      loading: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.$event != undefined && this.$event.meta != undefined) {
      // 初始化数据
      this.fetchLinkInfo(this.$event.meta.UUID);
      if (this.$event.meta.UUID) {
        let _this = this;
        this.pageContext
          .offEvent('deleteDbLink:' + this.$event.meta.UUID)
          .handleEvent('deleteDbLink:' + this.$event.meta.UUID, function () {
            if (_this.currentWindow) {
              _this.currentWindow.close();
            }
          });
      }
    }
  },
  mounted() {},
  methods: {
    testConnect() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const test = this.$message.loading('测试连接中...', 0);
          this.$axios
            .post(`/proxy/api/basicdata/datasource/dblink/testConnect`, this.form)
            .then(({ data }) => {
              setTimeout(test, 0);
              let msg = data.msg;
              if (data.code == 0) {
                let { connectSpendTime, driver, database } = data.data;
                this.$success({
                  title: `测试连接成功(${connectSpendTime}ms)`,
                  content: (
                    <div>
                      <div style="margin-bottom:8px">
                        <label style="font-weight:bolder">数据库版本: </label>
                        <span style="color:#000000a6">{database}</span>
                      </div>
                      <div>
                        <label style="font-weight:bolder">驱动程序: </label>
                        <span style="color:#000000a6">{driver}</span>
                      </div>
                    </div>
                  )
                });
              } else {
                this.$error({
                  title: '测试连接失败',
                  content: msg
                });
              }
            })
            .catch(error => {});
        }
      });
    },
    fetchLinkInfo(uuid) {
      if (uuid) {
        this.loading = true;
        this.$axios
          .get(`/proxy/api/basicdata/datasource/dblink/getDblinkConfig`, { params: { uuid } })
          .then(({ data }) => {
            this.loading = false;
            if (data.code == 0) {
              this.form.uuid = uuid;
              for (let key in this.form) {
                this.form[key] = data.data[key];
                this.resetRules();
              }
              if (this.form.param) {
                let lines = this.form.param.split('\n');
                for (let i = 0, len = lines.length; i < len; i++) {
                  let parts = lines[i].split('=');
                  this.paramList.push({
                    label: parts[0],
                    value: parts[1]
                  });
                }
              }
              if (this.form.driverJarFile) {
                this.getFileInfo(this.form.driverJarFile).then(file => {
                  this.form.driverJarFileName = file[0].fileName;
                });
              }
            }
          })
          .catch(error => {});
      }
    },
    resetFormData() {
      let d = this.defaultFormData();
      for (let key in this.form) {
        this.form[key] = d[key];
      }
    },
    defaultFormData() {
      return {
        dbType: undefined,
        name: undefined,
        remark: undefined,
        linkType: 'host',
        connectStype: 'sname',
        userRole: 'normal',
        url: undefined,
        host: undefined,
        port: undefined,
        driverClass: undefined,
        driverJarFile: undefined,
        userName: undefined,
        password: undefined,
        driverJarFileName: undefined,
        sname: undefined,
        sid: undefined
      };
    },

    onChangeLinkType() {
      this.resetRules();
      if (this.form.url == undefined) {
        this.form.url = this.dbTypeUrlPrefixMap[this.form.dbType];
      }
    },

    getFileInfo(fileID) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy-repository/repository/file/mongo/getNonioFiles`, {
            params: {
              fileID
            }
          })
          .then(({ data }) => {
            if (data.code == 0) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    },

    resetRules() {
      if (this.form.dbType == 'oracle') {
        this.rules.sname[0].required = false;
        this.rules.sid[0].required = false;
        if (this.form.connectStype == 'sname') {
          this.rules.sname[0].required = true;
        } else {
          this.rules.sid[0].required = true;
        }
      }
      this.rules.host[0].required = false;
      this.rules.port[0].required = false;
      this.rules.url[0].required = false;
      if (this.form.linkType == 'host') {
        this.rules.host[0].required = true;
        this.rules.port[0].required = true;
      } else {
        this.rules.host[0].required = false;
        this.rules.port[0].required = false;
        this.rules.url[0].required = true;
        this.rules.sname[0].required = false;
        this.rules.sid[0].required = false;
      }
    },

    customRequest(options) {
      this.uploading = true;
      customFileUploadRequest(options).then(dbFile => {
        this.afterUploadSuccess(options, dbFile);
      });
    },
    afterUploadSuccess(options, dbFile) {
      this.uploading = false;
      this.form.driverJarFile = dbFile.fileID;
      this.form.driverJarFileName = dbFile.filename;
    },
    downloadFile() {
      downloadLink(`/proxy-repository/repository/file/mongo/download?fileID=${this.form.driverJarFile}`);
    },
    beforeUpload(file, limitSize) {
      return new Promise((resolve, reject) => {
        if (file.name.endsWith('.jar')) {
          resolve(file);
        } else {
          this.$message.error('只允许上传 jar 文件');
          reject();
        }
      });
    },
    save() {
      return new Promise((resolve, reject) => {
        this.saving = true;
        this.$refs.form.validate(valid => {
          if (valid) {
            let formData = JSON.parse(JSON.stringify(this.form));
            formData.param = undefined;
            if (this.paramList.length) {
              formData.param = this.paramList.map(item => `${item.label}=${item.value}`).join('\n');
            }
            this.$axios
              .post(`/proxy/api/basicdata/datasource/dblink/saveDbLinkConfig`, formData)
              .then(({ data }) => {
                this.saving = false;
                if (data.code == 0) {
                  this.$message.success('保存成功');
                  if (this.currentWindow != undefined) {
                    this.currentWindow.close();
                    this.pageContext.emitEvent('uUhtVipLFFhzEXIqolDfcwrsFyqRnvbC:refetch');
                  }
                  resolve(data.data);
                } else {
                  this.$message.error('保存失败');
                }
              })
              .catch(error => {
                this.saving = false;
                this.$message.error('保存失败');
              });
          } else {
            this.saving = false;
          }
        });
      });
    }
  },

  META: {
    method: {
      save: '保存',
      testConnect: '测试连接'
    }
  }
};
</script>
