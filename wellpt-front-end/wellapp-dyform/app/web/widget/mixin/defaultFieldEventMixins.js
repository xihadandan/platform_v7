export default {
  computed: {
    supperDefaultEvents() {
      return this.widget.configuration.isDatabaseField
        ? [
            {
              id: 'setVisible',
              title: '设置为显示或者隐藏',
              eventParams: [
                {
                  paramKey: 'visible',
                  remark: '是否显示',
                  valueSource: {
                    inputType: 'select', // multi-select , checkbox , radio, input
                    options: [
                      { label: '显示', value: 'true' },
                      { label: '隐藏', value: 'false' }
                    ]
                  }
                }
              ]
            },

            {
              id: 'setEditable',
              title: '设置为可编辑或者不可编辑',
              eventParams: [
                {
                  paramKey: 'editable',
                  remark: '是否可编辑',
                  valueSource: {
                    inputType: 'select', // multi-select , checkbox , radio, input
                    options: [
                      { label: '可编辑', value: 'true' },
                      { label: '不可编辑', value: 'false' }
                    ]
                  }
                }
              ]
            },
            {
              id: 'setValueByEventParams',
              title: '设值',
              eventParams: [
                {
                  paramKey: 'value',
                  remark: '值'
                }
              ]
            },
            {
              id: 'setRequired',
              title: '设置为必填或者非必填',
              eventParams: [
                {
                  paramKey: 'required',
                  remark: '是否必填',
                  valueSource: {
                    inputType: 'select', // multi-select , checkbox , radio, input
                    options: [
                      { label: '必填', value: 'true' },
                      { label: '非必填', value: 'false' }
                    ]
                  }
                }
              ]
            },
            {
              id: 'clearValidate',
              title: '清空字段校验信息'
            }
          ]
        : [];
    }
  }
};
