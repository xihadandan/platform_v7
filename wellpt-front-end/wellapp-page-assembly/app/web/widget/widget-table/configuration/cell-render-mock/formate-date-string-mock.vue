<template>
  <div>
    <a-form-model-item label="示例：">
      {{ formatDateStr }}
    </a-form-model-item>
  </div>
</template>

<script>
import moment from 'moment';
import TableRenderMixin from '../../table.renderMixin';
export default {
  name: 'formateDateStringMock',
  props: {
    options: {
      type: Object
    }
  },
  data() {
    const defaultPattern = 'YYYY-MM-DD HH:mm:ss';
    const text = moment().format(defaultPattern);
    return {
      formatDateStr: '',
      text,
      // text: '2025-11-28 17:34:56',
      row: {
        systemUnitId: null,
        creator: 'U_167564381264019456',
        code: '000977',
        formUuid: '161479136806699008',
        delete_time: null,
        modifier: 'U_167564381264019456',
        remark: null,
        uuid: '318708434671239168',
        version: '1',
        categoryName: '流程全局',
        enabled: '1',
        freeed: '0',
        modifyTime: '2025-11-28 17:34:56',
        isRef: '0',
        createTime: '2025-05-29 11:13:32',
        recVer: '26',
        delete_status: null,
        formName: '学生基本信息表',
        name: '流程52902',
        flowSchemaUuid: '318708434688016384',
        id: 'test52902',
        category: '199845717320138752',
        moduleId: 'module_20230907101836',
        maxVersionDefUuid: null
      }
    };
  },
  watch: {
    options: {
      deep: true,
      immediate: true,
      handler(options) {
        let row = { ...this.row };
        if (options.isRange && options.endTimeParams) {
          row[options.endTimeParams] = this.text;
        }
        this.formatDateStr = this.formateDateString(this.text, row, options);
      }
    }
  },
  methods: {
    formateDateString(value, row, options) {
      return TableRenderMixin.methods.formateDateString(value, row, options);
    }
  }
};
</script>
