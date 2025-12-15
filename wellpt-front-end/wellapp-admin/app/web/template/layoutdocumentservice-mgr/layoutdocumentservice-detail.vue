<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model :model="form" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" :rules="rules" ref="form">
      <a-form-model-item label="服务名称" prop="serverUniqueCode">
        <a-select v-model="form.serverUniqueCode" showSearch allow-clear>
          <a-select-option v-for="d in serverOptions" :key="d.id">
            {{ d.text }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="服务唯一标识符">
        {{ form.serverUniqueCode }}
      </a-form-model-item>
      <a-form-model-item label="编号" prop="code">
        <a-input v-model="form.code" allow-clear />
      </a-form-model-item>
      <a-form-model-item label="服务地址" prop="serverUrl">
        <a-input v-model="form.serverUrl" allow-clear />
      </a-form-model-item>
      <a-form-model-item label="支持的文件扩展名" prop="fileExtensions">
        <a-input v-model="form.fileExtensions" allow-clear />
      </a-form-model-item>
      <a-form-model-item label="状态" prop="status">
        <a-switch checked-children="启用" un-checked-children="禁用" v-model="status" />
      </a-form-model-item>
    </a-form-model>
    <div style="text-align: center; margin-bottom: 30px">
      <a-button @click="saveForm" type="primary">保存</a-button>
    </div>
  </a-skeleton>
</template>

<script type="text/babel">
import { deepClone, queryString } from '@framework/vue/utils/util';
import { find } from 'lodash';
export default {
  name: 'LayoutdocumentserviceDetail',
  inject: ['pageContext', '$event', 'vPageState'],
  components: {},
  data() {
    let $event = this.$event;
    return {
      loading: $event && $event.meta && $event.meta.uuid != undefined,
      uuid: $event && $event.meta != undefined ? $event.meta.uuid : undefined,
      form: {
        serverName: null,
        serverUniqueCode: null,
        code: null,
        serverUrl: null,
        fileExtensions: null,
        status: null,
        priority: null
      },
      rules: {
        serverUniqueCodeTemp: { required: true, message: '服务名称必填', trigger: ['blur', 'change'] },
        serverUrl: { required: true, message: '服务地址必填', trigger: ['blur', 'change'] },
        fileExtensions: { required: true, message: '支持的文件扩展名必填', trigger: ['blur', 'change'] }
      },
      serverOptions: [],
      status: false
    };
  },
  computed: {},
  beforeMount() {
    let _this = this;
    let urlParams = queryString(location.search.substr(1));
    this.uuid = urlParams.uuid || '';
    if (this.uuid) {
      this.loading = true;
      this.getDetails();
    }
    this.getServerOptions();
  },
  methods: {
    getDetails() {
      let _this = this;
      $axios.get('/api/basicdata/layoutDocumentServiceConf/getByUuid', { params: { uuid: this.uuid } }).then(({ data }) => {
        _this.loading = false;
        if (data.code == 0 && data.data) {
          this.form = data.data;
          this.status = data.data.status == '1';
        }
      });
    },
    getServerOptions() {
      let _this = this;
      _this.$axios
        .post('/common/select2/query', {
          serviceName: 'dataDictionaryService',
          queryMethod: 'loadSelectData',
          type: 'LAYOUT_DOCUMENT_SERVICE',
          searchValue: '',
          pageSize: 1000,
          pageNo: 1
        })
        .then(({ data }) => {
          if (data.results) {
            _this.serverOptions = data.results;
          }
        });
    },
    saveForm() {
      let _this = this;
      this.$refs.form.validate(valid => {
        if (valid) {
          let bean = deepClone(this.form);
          bean.status = this.status ? '1' : '0';
          bean.serverName = bean.serverUniqueCode ? find(this.serverOptions, { id: bean.serverUniqueCode }).text : '';
          if (this.status) {
            _this.$confirm({
              title: '系统仅允许存在一个"启用"的服务，继续保存将修改其他"启用"的服务的状态为"禁用"，是否确定保存？',
              onOk() {
                _this.saveFormData(bean);
              },
              onCancel() {}
            });
          } else {
            this.saveFormData(bean);
          }
        } else {
          return false;
        }
      });
    },
    saveFormData(bean) {
      $axios
        .post('/api/basicdata/layoutDocumentServiceConf/saveBean', bean)
        .then(({ data }) => {
          if (data.code == 0) {
            this.$message.success('保存成功');
            if (window.opener && window.opener.$app && window.opener.$app.pageContext) {
              window.opener.$app.pageContext.emitEvent(`refetchlayoutdocumentserviceManageTable`, { saveSuccess: true });
            }
            setTimeout(() => {
              window.opener = null;
              window.close();
            }, 500);
          } else {
            this.$message.error(data.msg || '保存失败');
          }
        })
        .catch(err => {
          let data = err && err.response && err.response.data;
          this.$message.error(data.msg || '保存失败');
        });
    }
  },
  META: {
    method: {
      saveForm: '保存表单'
    }
  }
};
</script>

<style></style>
