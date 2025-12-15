<template>
  <a-skeleton active :loading="false" :paragraph="{ rows: 10 }">
    <a-form-model class="pt-form" :model="formData" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" :rules="rules" ref="form">
      <a-form-model-item label="标签" prop="name">
        <a-input v-model="formData.name"></a-input>
      </a-form-model-item>
      <a-form-model-item label="上级标签" prop="parentUuid">
        <a-select
          v-model="formData.parentUuid"
          show-search
          style="width: 100%"
          :options="parentTagOptions"
          :filter-option="filterSelectOption"
        ></a-select>
      </a-form-model-item>
    </a-form-model>
  </a-skeleton>
</template>
<script>
import { generateId } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function';
export default {
  components: {},
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    let $event = this.$event || {};
    let formData = $event.meta || {};
    if (!formData.uuid) {
      formData = {
        id: 'tag_' + generateId('SF')
      };
    }
    return {
      loading: false,
      uuid: formData.uuid,
      formData,
      rules: {
        name: { required: true, message: '不能为空', trigger: ['blur', 'change'] }
      },
      parentTagOptions: []
    };
  },
  created() {
    this.loadParentTagOptions();
  },
  methods: {
    filterSelectOption,
    loadParentTagOptions() {
      $axios.get('/proxy/api/dms/tag/listRootTag').then(({ data: result }) => {
        if (result.data) {
          this.parentTagOptions = result.data
            .filter(item => item.uuid != this.uuid)
            .map(item => {
              return {
                label: item.name,
                value: item.uuid
              };
            });
        }
      });
    },
    save($evt) {
      const _this = this;
      if (_this.loading) {
        return;
      }
      _this.loading = true;
      _this.$loading();

      _this.$refs.form.validate(result => {
        if (result) {
          $axios
            .post('/proxy/api/dms/tag/save', _this.formData)
            .then(({ data: result }) => {
              if (result.code == 0) {
                _this.$message.success('保存成功！');
                $evt.$evtWidget.closeModal && $evt.$evtWidget.closeModal();
                _this.$event && _this.$event.$evtWidget && _this.$event.$evtWidget.refetch && _this.$event.$evtWidget.refetch(true);
              } else {
                _this.$message.error(result.msg || '服务异常！');
              }
              setTimeout(() => {
                _this.loading = false;
                _this.$loading(false);
              }, 200);
            })
            .catch(({ response }) => {
              setTimeout(() => {
                _this.loading = false;
                _this.$loading(false);
              }, 200);
              _this.$message.error((response.data && response.data.msg) || '服务异常！');
            });
        } else {
          _this.loading = false;
          _this.$loading(false);
        }
      });
    }
  },
  META: {
    method: {
      save: '保存标签'
    }
  }
};
</script>

<style lang="less" scoped></style>
