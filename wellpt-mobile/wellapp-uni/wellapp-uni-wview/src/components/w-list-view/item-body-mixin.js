module.exports = {
  props: {
    configuration: Object,
    rowData: Object,
  },
  methods: {
    renderCustomRowContent: function (rowData) {
      let _self = this;
      let itemContent = _self.configuration.customTemplateHtml || "";
      Object.keys(rowData).forEach((key) => {
        let propertyValue = rowData[key] || "";
        // html转义字符处理
        if (typeof propertyValue == "string") {
          propertyValue = propertyValue.replace(/</g, "&lt;").replace("/>/g", "&gt;");
        }
        itemContent = itemContent.replace(new RegExp(`\{${key}\}`, "g"), propertyValue);
      });
      return itemContent;
    },
  },
};
