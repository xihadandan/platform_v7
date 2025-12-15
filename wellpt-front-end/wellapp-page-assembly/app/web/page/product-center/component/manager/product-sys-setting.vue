<template>
  <a-tabs class="product-page-setting" tabPosition="left">
    <a-tab-pane key="loginPage" tab="系统参数定义">
      <PerfectScrollbar style="height: calc(100vh - 250px)">
        <div style="padding: 12px 20px 12px 0; display: flex; align-items: baseline; justify-content: space-between">
          <div>
            <Modal title="新增" :ok="e => onConfirmSaveParam(e)">
              <a-button type="primary" @click="prepareAddParam" style="margin-right: 8px">新增</a-button>
              <template slot="content">
                <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
                  <a-form-model-item label="名称">
                    <a-input v-model.trim="form.name" />
                  </a-form-model-item>
                  <a-form-model-item label="键">
                    <a-input v-model.trim="form.propKey" />
                  </a-form-model-item>
                  <a-form-model-item label="值">
                    <a-input v-model.trim="form.propValue" />
                  </a-form-model-item>
                  <a-form-model-item label="备注">
                    <a-textarea v-model.trim="form.remark" />
                  </a-form-model-item>
                </a-form-model>
              </template>
            </Modal>
            <a-button @click="e => deleteParams(e)">删除</a-button>
          </div>
          <a-input-search v-model.trim="keyword" @search="onSearch" style="width: 200px" allow-clear />
        </div>
        <a-table
          rowKey="uuid"
          :columns="columns"
          :data-source="vDataSource"
          :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
          :pagination="false"
          :loading="loading"
          size="small"
          :scroll="{
            y: 'calc(100vh - 400px)'
          }"
        >
          <template slot="seqSlot" slot-scope="text, record, index">
            {{ index + 1 }}
          </template>
          <template slot="operationSlot" slot-scope="text, record">
            <Modal title="编辑" :ok="e => onConfirmSaveParam(e, record)">
              <a-button size="small" @click="prepareEditParam(record)">编辑</a-button>
              <template slot="content">
                <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" :ref="'form_' + record.uuid">
                  <a-form-model-item label="名称">
                    <a-input v-model.trim="form.name" />
                  </a-form-model-item>
                  <a-form-model-item label="键">
                    <a-input v-model.trim="form.propKey" />
                  </a-form-model-item>
                  <a-form-model-item label="值">
                    <a-input v-model.trim="form.propValue" />
                  </a-form-model-item>
                  <a-form-model-item label="备注">
                    <a-textarea v-model.trim="form.remark" />
                  </a-form-model-item>
                </a-form-model>
              </template>
            </Modal>
            <a-button size="small" @click="e => deleteParams(e, record.uuid)">删除</a-button>
          </template>
        </a-table>
      </PerfectScrollbar>
    </a-tab-pane>
  </a-tabs>
</template>
<style lang="less"></style>

<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'ProductSysSetting',
  props: {
    uuid: String,
    prodId: String
  },
  components: { Modal },
  computed: {},
  data() {
    return {
      keyword: undefined,
      columns: [
        {
          title: '序号',
          width: 100,
          dataIndex: 'seqNo',
          scopedSlots: { customRender: 'seqSlot' }
        },
        {
          title: '参数名',
          dataIndex: 'name'
        },
        {
          title: '键',
          dataIndex: 'propKey'
        },
        {
          title: '值',
          dataIndex: 'propValue'
        },
        {
          title: '操作',
          width: 150,
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      dataSource: [],
      vDataSource: [],
      selectedRowKeys: [],
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      form: {
        uuid: undefined,
        propKey: undefined,
        propValue: undefined,
        remark: undefined,
        name: undefined,
        prodVersionUuid: this.uuid,
        prodId: this.prodId
      },
      rules: {
        name: [{ required: true, message: '名称必填', trigger: 'blur' }],
        propKey: [{ required: true, message: '键必填', trigger: 'blur' }]
      },
      loading: true
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchAllProdVersionParams(this.uuid).then(list => {
      this.dataSource = list;
      this.vDataSource = list;
      this.loading = false;
    });
  },
  mounted() {},
  methods: {
    deleteParams(e, uuid) {
      let uuids = uuid ? [uuid] : this.selectedRowKeys;
      if (uuids.length == 0) {
        this.$message.info('请选择要删除的参数');
        return;
      }
      $axios
        .post(`/proxy/api/app/prod/version/deleteParams`, uuids)
        .then(({ data }) => {
          if (data.code == 0) {
            let _uuids = [...uuids];
            for (let i = 0; i < this.dataSource.length; i++) {
              if (uuids.length == 0) {
                break;
              }
              if (uuids.includes(this.dataSource[i].uuid)) {
                let idx = uuids.indexOf(this.dataSource[i].uuid);
                uuids.splice(idx, 1);
                this.dataSource.splice(i--, 1);
              }
            }

            for (let i = 0; i < this.vDataSource.length; i++) {
              if (_uuids.length == 0) {
                break;
              }
              if (_uuids.includes(this.vDataSource[i].uuid)) {
                let idx = _uuids.indexOf(this.vDataSource[i].uuid);
                _uuids.splice(idx, 1);
                this.vDataSource.splice(i--, 1);
              }
            }
            this.$message.success('删除成功');
            if (uuid == undefined) {
              this.selectedRowKeys = [];
            }
          }
        })
        .catch(error => {});
    },
    onSearch() {
      this.vDataSource = this.keyword
        ? this.dataSource.filter(obj => {
            let propKey = obj.propKey.toLowerCase(),
              propValue = obj.propValue != undefined ? obj.propValue.toLowerCase() : '',
              name = obj.name.toLowerCase(),
              remark = obj.remark != undefined ? obj.remark.toLowerCase() : '';
            if (
              propKey.indexOf(this.keyword) != -1 ||
              propValue.indexOf(this.keyword) != -1 ||
              name.indexOf(this.keyword) != -1 ||
              remark.indexOf(this.keyword) != -1
            ) {
              return obj;
            }
          })
        : this.dataSource;
    },
    prepareAddParam() {
      for (let k in this.form) {
        this.form[k] = undefined;
      }
      this.form.prodVersionUuid = this.uuid;
      this.form.prodId = this.prodId;
    },
    prepareEditParam(item) {
      Object.assign(this.form, item);
    },
    onConfirmSaveParam(e, item) {
      let $form = this.form.uuid == undefined ? this.$refs.form : this.$refs['form_' + this.form.uuid];
      $form.validate((passed, msg) => {
        if (passed) {
          // for (let i = 0; i < 100; i++) {
          //   this.form.propKey = `test_prop_key_` + i;
          //   $axios.post(`/proxy/api/app/prod/version/param/save`, this.form).then(({ data }) => {});
          // }
          $axios
            .post(`/proxy/api/app/prod/version/param/save`, this.form)
            .then(({ data }) => {
              if (data.code == 0) {
                e(true);
                if (item) {
                  item.name = this.form.name;
                  item.propKey = this.form.propKey;
                  item.propValue = this.form.propValue;
                  item.remark = this.form.remark;
                } else {
                  let param = {
                    ...this.form,
                    uuid: data.data,
                    prodVersionUuid: this.uuid,
                    prodId: this.prodId
                  };
                  this.dataSource.splice(0, 0, param);
                }
              }
            })
            .catch(error => {});
        }
      });
    },
    onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys;
    },
    fetchAllProdVersionParams(uuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/version/allParams/${uuid}`, { params: {} })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    }
  }
};
</script>
