<template>
  <div class="pt-form">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="基本信息">
        <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
          <a-form-model-item label="名称" prop="name">
            <a-input v-model="form.name" v-if="!isDefaultModel" :maxLength="64" />
            <span v-else>{{ form.name }}</span>
          </a-form-model-item>
          <a-form-model-item label="ID" prop="id">
            <a-input v-model="form.id" v-if="!form.uuid" :maxLength="64" />
            <span v-else>{{ form.id }}</span>
          </a-form-model-item>
          <a-form-model-item label="图标">
            <WidgetIconLibModal v-model="form.icon" :zIndex="1000" onlyIconClass>
              <a-badge>
                <a-icon
                  v-if="form.icon"
                  slot="count"
                  type="close-circle"
                  style="color: #f5222d"
                  theme="filled"
                  @click.stop="form.icon = undefined"
                  title="删除图标"
                />
                <a-button size="small">
                  {{ form.icon ? '' : '选择图标' }}
                  <Icon :type="form.icon" v-show="form.icon" />
                </a-button>
              </a-badge>
            </WidgetIconLibModal>
          </a-form-model-item>
          <a-form-model-item label="组织单元类型">
            <a-radio-group v-model="form.type" v-if="!isDefaultModel">
              <a-radio value="MANAGE">管理职能</a-radio>
              <a-radio value="MANAGELESS">非职能</a-radio>
            </a-radio-group>
            <span v-else>管理职能</span>
          </a-form-model-item>
          <a-form-model-item label="描述">
            <a-textarea v-model="form.remark" v-if="!isDefaultModel" :maxLength="300" />
            <span v-else>{{ form.remark }}</span>
          </a-form-model-item>
        </a-form-model>
      </a-tab-pane>
      <a-tab-pane key="2" tab="模型属性" force-render>
        <a-button @click="onAddExtAttr" icon="plus" :style="{ marginBottom: '8px' }">新建属性</a-button>
        <a-table rowKey="id" :pagination="false" :data-source="extAttrs" :columns="extAttrColumns" bordered class="org-model-pt-form-table">
          <template slot="attrNameSlot" slot-scope="text, record, index">
            <a-input v-model="record.attrName" />
          </template>
          <template slot="attrKeySlot" slot-scope="text, record, index">
            <a-input v-model="record.attrKey" />
          </template>
          <template slot="operationSlot" slot-scope="text, record, index">
            <a-popconfirm
              placement="left"
              :arrowPointAtCenter="true"
              title="确认要删除吗?"
              ok-text="删除"
              cancel-text="取消"
              @confirm="onDeleteExtAttr(index)"
            >
              <a-button type="link"><Icon type="pticon iconfont icon-ptkj-shanchu"></Icon></a-button>
            </a-popconfirm>
          </template>
        </a-table>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script type="text/babel">
import { debounce } from 'lodash';
import { generateId } from '@framework/vue/utils/util';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';

export default {
  name: 'OrgElementModelDetails',
  inject: ['$event', 'pageContext'],

  data() {
    let orgElementModel = this.$event ? this.$event.orgElementModel || { type: 'MANAGE' } : { type: 'MANAGE' };
    let idRule = [{ required: true, message: 'ID必填', trigger: orgElementModel.uuid ? [] : ['blur', 'change'] }];
    if (!orgElementModel.uuid) {
      idRule.push({ trigger: ['blur', 'change'], validator: this.checkIdExist });
    }
    return {
      orgElementModel,
      iconSelectModalVisible: false,
      labelCol: { span: 6 },
      wrapperCol: { span: 18 },
      form: JSON.parse(JSON.stringify(orgElementModel)),
      rules: {
        name: { required: true, message: '名称必填', trigger: ['blur', 'change'] },
        id: idRule
      },
      extAttrs: [],
      extAttrColumns: [
        {
          title: '属性名称',
          dataIndex: 'attrName',
          scopedSlots: { customRender: 'attrNameSlot' }
        },
        {
          title: '属性编码',
          dataIndex: 'attrKey',
          scopedSlots: { customRender: 'attrKeySlot' }
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 70,
          // align: 'right',
          class: 'operation',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ]
    };
  },
  META: {
    method: {
      save: '保存'
    }
  },
  watch: {},
  beforeCreate() {},
  components: { WidgetIconLibModal },
  computed: {
    isDefaultModel() {
      return this.orgElementModel && ['unit', 'dept', 'job'].includes(this.orgElementModel.id);
    }
  },
  created() {},
  methods: {
    save(event) {
      let _this = this;
      this.$refs.form.validate(valid => {
        if (valid) {
          _this.form.defJson = JSON.stringify({ extAttrs: _this.extAttrs });
          if (_this._$SYSTEM_ID != undefined) {
            _this.form.system = _this._$SYSTEM_ID;
            _this.form.tenant = _this._$USER.tenantId;
          }
          $axios
            .post('/proxy/api/org/elementModel/save', _this.form)
            .then(({ data }) => {
              if (data.code == 0) {
                // 刷新表格
                _this.pageContext.emitEvent('vqswVQQWuYcEvZNogYcXJUXPygSYjwxZ:refetch');
                if (!_this.form.uuid) {
                  _this.pageContext.emitEvent('refetchUserCountStatics');
                }
                // 事件由弹窗派发出来的，则$evtWidget代表弹窗，当然也可以通过emitEvent去关闭指定的弹窗
                event.$evtWidget.closeModal(); // 关闭弹窗
              }
            })
            .catch(error => {
              _this.$message.error('服务异常');
            });
        }
      });
    },
    onDeleteExtAttr(i) {
      this.extAttrs.splice(i, 1);
    },
    onAddExtAttr() {
      this.extAttrs.push({ id: generateId(), attrKey: undefined, attrName: undefined });
    },
    onClickIcon() {
      this.iconSelectModalVisible = true;
    },

    checkIdExist: debounce(function (rule, value, callback) {
      $axios.get('/proxy/api/org/elementModel/id/exist', { params: { id: value } }).then(({ data }) => {
        if (data.code == 0) {
          callback(data.data ? 'ID重复' : undefined);
        } else {
          callback('服务异常');
        }
      });
    }, 500)
  },
  beforeMount() {
    if (this.form.uuid) {
      $axios.get('/proxy/api/org/elementModel/getDetailByUuid', { params: { uuid: this.form.uuid } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          Object.assign(this.form, data.data);
          if (data.data.defJson) {
            this.extAttrs = JSON.parse(data.data.defJson).extAttrs || [];
          }
        }
      });
    }
  },
  mounted() {}
};
</script>
<style lang="less">
.org-model-pt-form-table {
  .ant-table-tbody > tr > td {
    padding: var(--w-padding-3xs) var(--w-padding-xs);
  }

  .ant-table-thead > tr > th {
    padding: var(--w-padding-2xs) var(--w-padding-xs);

    .ant-table-header-column {
      text-align: left;
    }
  }
}
</style>
