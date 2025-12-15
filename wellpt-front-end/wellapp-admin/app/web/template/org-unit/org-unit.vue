<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" class="pt-form">
      <a-form-model-item label="名称" prop="name">
        <a-input v-model="form.name" :maxLength="120">
          <template slot="addonAfter">
            <WI18nInput :target="form" code="name" v-model="form.name" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="简称">
        <a-input v-model="form.shortName" :maxLength="64">
          <template slot="addonAfter">
            <WI18nInput :target="form" code="short_name" v-model="form.shortName" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="ID" v-if="form.uuid">
        <span>{{ form.id }}</span>
      </a-form-model-item>
      <a-form-model-item label="编号">
        <a-input v-model="form.code" :maxLength="32" />
      </a-form-model-item>
      <a-form-model-item label="单位类型">
        <a-radio-group v-model="form.type">
          <a-radio value="CORPORATION">法人机构</a-radio>
          <a-radio value="NOT_CORPORATION">非法人机构</a-radio>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="单位代码">
        <a-table
          rowKey="id"
          :pagination="false"
          :data-source="form.orgUnitCodes"
          :columns="unitCodeColumns"
          :locale="{ emptyText: '暂无数据' }"
          bordered
          class="pt-table"
        >
          <template slot="codeTypeSlot" slot-scope="text, record">
            <a-select :options="codeTypeOptions" style="width: 100%" v-model="record.codeType" />
          </template>
          <template slot="codeSlot" slot-scope="text, record, index">
            <a-row>
              <a-col :span="22">
                <a-input v-model="record.code" />
              </a-col>
              <a-col :span="2">
                <a-button type="link" icon="delete" @click.stop="onDeleteUnitCodeRow(index)" />
              </a-col>
            </a-row>
          </template>
          <template slot="footer">
            <a-button type="link" icon="plus" :style="{ paddingLeft: '7px' }" @click.stop="addUnitCode">添加</a-button>
          </template>
        </a-table>
      </a-form-model-item>
      <a-form-model-item label="描述">
        <a-textarea v-model="form.remark" allowClear :maxLength="300" />
      </a-form-model-item>

      <template v-for="(attr, i) in form.orgUnitExtAttrs">
        <a-form-model-item :label="attr.attrName" :key="'extAttr_' + i">
          <a-input v-model="attr.attrVal" />
        </a-form-model-item>
      </template>
    </a-form-model>
  </a-skeleton>
</template>

<script type="text/babel">
import { debounce } from 'lodash';
import { generateId } from '@framework/vue/utils/util';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'OrgUnit',
  inject: ['pageContext', '$event', 'vPageState'],

  data() {
    // let idRule = [{ required: true, message: 'ID必填', trigger: this.meta ? [] : ['blur', 'change'] }];
    // if (!this.meta) {
    //   idRule.push({ trigger: ['blur', 'change'], validator: this.checkIdExist });
    // }
    // 获取父级容器的状态管理数据
    let $event = this.$event;
    return {
      loading: $event && $event.meta && $event.meta.uuid != undefined,
      uuid: $event && $event.meta != undefined ? $event.meta.uuid : undefined,
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      form: { type: 'CORPORATION', orgUnitExtAttrs: [], orgUnitCodes: [] },
      rules: {
        name: { required: true, message: '名称必填', trigger: ['blur', 'change'] }
        // id: idRule
      },
      codeTypeOptions: [
        { label: '统一社会信用代码', value: 'UNIFIED_SOCIAL_CREDIT_CODE' },
        { label: '营业执照', value: 'BUSINESS_LICENSE' },
        { label: '税务登记号', value: 'TAX_REGISTRATION_NO' }
      ],
      unitCodeColumns: [
        {
          title: '代码类型',
          dataIndex: 'codeType',
          scopedSlots: { customRender: 'codeTypeSlot' }
        },
        {
          title: '代码',
          dataIndex: 'code',
          scopedSlots: { customRender: 'codeSlot' },
          width: 300
        }
      ]
    };
  },
  watch: {},
  beforeCreate() {},
  components: { WI18nInput },
  computed: {},
  created() {},
  methods: {
    save(event) {
      // 获取数据
      let _this = this;
      this.$refs.form.validate(valid => {
        if (valid) {
          if (_this._$SYSTEM_ID) {
            _this.form.system = _this._$SYSTEM_ID;
          }
          if (_this.form.i18n) {
            let i18ns = [];
            for (let locale in _this.form.i18n) {
              for (let key in _this.form.i18n[locale]) {
                if (_this.form.i18n[locale][key]) {
                  i18ns.push({
                    locale: locale,
                    content: _this.form.i18n[locale][key],
                    dataCode: key
                  });
                }
              }
            }
            _this.form.i18ns = i18ns;
          }
          $axios
            .post('/proxy/api/org/unit/save', _this.form)
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$message.success('保存成功');
                // 刷新表格
                _this.pageContext.emitEvent('EpfvQGzoTFclidfWClJbBGdxqvcsdIlk:refetch');
                event.$evtWidget.closeModal();
              }
            })
            .catch(error => {
              _this.$message.error('服务异常');
            });
        }
      });
    },
    addUnitCode() {
      this.form.orgUnitCodes.push({ id: generateId(), codeType: 'UNIFIED_SOCIAL_CREDIT_CODE' });
    },
    onDeleteUnitCodeRow(i) {
      this.form.orgUnitCodes.splice(i, 1);
    },

    // checkIdExist: debounce(function (rule, value, callback) {
    //   $axios.get('/proxy/api/org/unit/existById', { params: { id: value } }).then(({ data }) => {
    //     if (data.code == 0) {
    //       callback(data.data ? 'ID重复' : undefined);
    //     } else {
    //       callback('服务异常');
    //     }
    //   });
    // }, 500),

    getExtAttrDefJson(callback) {
      let _this = this;
      $axios.get('/proxy/api/org/elementModel/getDefJson', { params: { id: 'unit' } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          let extAttrs = JSON.parse(data.data).extAttrs;
          let keymap = {};
          extAttrs.forEach(a => {
            keymap[a.attrKey] = a.attrName;
          });
          if (typeof callback === 'function') {
            callback.call(_this, keymap, extAttrs);
          }
        }
      });
    },
    getOrgUnitDetails() {
      let _this = this;
      $axios.get(`/proxy/api/org/unit/details/${this.uuid}`, {}).then(({ data }) => {
        _this.loading = false;
        if (data.code == 0 && data.data) {
          _this.generateExtAttrs(data.data);
        }
      });
    },

    generateExtAttrs(data) {
      this.getExtAttrDefJson((keymap, attrs) => {
        let attrMetas = [];
        if (attrs.length) {
          attrs.forEach(a => {
            attrMetas.push({
              attrKey: a.attrKey,
              attrName: a.attrName,
              attrVal: undefined
            });
          });
        }

        if (data && data.orgUnitExtAttrs && data.orgUnitExtAttrs.length) {
          let map = {};
          for (let i = 0, len = data.orgUnitExtAttrs.length; i < len; i++) {
            map[data.orgUnitExtAttrs[i].attrKey] = data.orgUnitExtAttrs[i];
          }
          for (let i = 0, len = attrMetas.length; i < len; i++) {
            let attrName = attrMetas[i].attrName;
            attrMetas[i] = { ...map[attrMetas[i].attrKey], attrName };
          }
        }
        if (data) {
          this.form = data;

          if (data.i18ns) {
            let i18n = {};
            for (let item of data.i18ns) {
              if (i18n[item.locale] == undefined) {
                i18n[item.locale] = {};
              }
              i18n[item.locale][item.dataCode] = item.content;
            }
            this.$set(this.form, 'i18n', i18n);
          }
        }
        this.form.orgUnitExtAttrs = attrMetas;
      });
    }
  },
  beforeMount() {
    let _this = this;
    if (this.uuid) {
      this.getOrgUnitDetails(() => {});
    } else {
      this.generateExtAttrs();
    }
  },
  mounted() {}
};
</script>
