<template>
  <div>
    <a-card size="small" :bordered="false">
      <template slot="title">
        <span v-if="itemDefinition">{{ itemDefinition.name }}({{ itemDefinition.type == '10' ? '单个事项' : '组合事项' }})</span>
        <span v-else-if="eventParams && eventParams.type">
          {{ eventParams.type == '10' ? '新增单个事项配置' : '新增组合事项配置' }}
        </span>
      </template>
      <template slot="extra">
        <a-button @click="save" size="small" type="primary">保存</a-button>
        <a-button v-if="itemDefinition && itemDefinition.uuid" @click="remove" size="small" type="danger">删除</a-button>
      </template>
      <PerfectScrollbar style="height: calc(100vh - 120px)">
        <BizItemDefinitionDetails ref="details" :businessIdEditable="businessIdEditable"></BizItemDefinitionDetails>
      </PerfectScrollbar>
    </a-card>
  </div>
</template>

<script>
import BizItemDefinitionDetails from '@admin/app/web/template/biz-process/biz-item-definition-details.vue';
export default {
  props: {
    itemDefinition: Object,
    eventParams: Object,
    businessIdEditable: {
      type: Boolean,
      default: true
    }
  },
  components: {
    BizItemDefinitionDetails
  },
  inject: ['pageContext'],
  provide() {
    return { $event: { meta: this.itemDefinition, eventParams: this.eventParams } };
  },
  data() {
    return {};
  },
  methods: {
    save() {
      this.$refs.details.save({
        $evtWidget: this
      });
    },
    remove() {
      const _this = this;
      _this.$confirm({
        title: '确认框',
        content: `确认删除业务事项定义[${_this.itemDefinition.name}]？`,
        onOk() {
          $axios
            .post(`/proxy/api/biz/item/definition/deleteAll?uuids=${_this.itemDefinition.uuid}`)
            .then(({ data }) => {
              if (data.code == 0) {
                // 刷新表格
                _this.$message.success('删除成功');
                _this.pageContext.emitEvent('loadItemDefinition');
              }
            })
            .catch(error => {
              _this.$message.error('删除失败');
            });
        }
      });
    },
    closeModal() {
      if (this.itemDefinition) {
        $axios.get(`/proxy/api/biz/item/definition/get?uuid=${this.itemDefinition.uuid}`).then(({ data: result }) => {
          if (result.data) {
            Object.assign(this.itemDefinition, result.data);
          }
        });
      } else {
        this.pageContext.emitEvent('loadItemDefinition', this.$refs.details.formData);
      }
    }
  }
};
</script>

<style></style>
