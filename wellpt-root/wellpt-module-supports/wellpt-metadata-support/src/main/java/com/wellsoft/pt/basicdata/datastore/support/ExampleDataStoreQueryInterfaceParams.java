package com.wellsoft.pt.basicdata.datastore.support;

import com.wellsoft.pt.jpa.criteria.InterfaceParam;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/22
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/22    chenq		2019/10/22		Create
 * </pre>
 */
public class ExampleDataStoreQueryInterfaceParams implements InterfaceParam {

    @DataStoreInterfaceField(name = "文本")
    private String simpleText;

    @DataStoreInterfaceField(name = "下拉选择", domType = DataStoreInterfaceFieldElement.SELECT, dataJSON = "{ \"选项1\": 0, \"选项2\": 2}")
    private String simpleSelect;


    @DataStoreInterfaceField(name = "复选框", domType = DataStoreInterfaceFieldElement.CHECKBOX, dataJSON = "{\"篮球\":0,\"足球\":1,\"排球\":2}")
    private String[] simpleCheckbox;


    @DataStoreInterfaceField(name = "单选框", domType = DataStoreInterfaceFieldElement.RADIO, dataJSON = "{\"是\":0,\"否\":1}")
    private Boolean simpleRadio;

    @DataStoreInterfaceField(name = "选择表单", domType = DataStoreInterfaceFieldElement.SELECT, service = "dataManagementViewerComponentService.getDataTypeOfDyFormSelectData")
    private String formSelect;

    @DataStoreInterfaceField(name = "用户选择", domType = DataStoreInterfaceFieldElement.ORG_SELECT)
    private String orgSelect;

    @DataStoreInterfaceField(name = "数据字典选择", domType = DataStoreInterfaceFieldElement.DATA_DIC_SELECT)
    private String dictSelect;


    public String getSimpleText() {
        return simpleText;
    }

    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }

    public String getSimpleSelect() {
        return simpleSelect;
    }

    public void setSimpleSelect(String simpleSelect) {
        this.simpleSelect = simpleSelect;
    }

    public String[] getSimpleCheckbox() {
        return simpleCheckbox;
    }

    public void setSimpleCheckbox(String[] simpleCheckbox) {
        this.simpleCheckbox = simpleCheckbox;
    }


    public Boolean getSimpleRadio() {
        return simpleRadio;
    }

    public void setSimpleRadio(Boolean simpleRadio) {
        this.simpleRadio = simpleRadio;
    }

    public String getFormSelect() {
        return formSelect;
    }

    public void setFormSelect(String formSelect) {
        this.formSelect = formSelect;
    }

    public String getOrgSelect() {
        return orgSelect;
    }

    public void setOrgSelect(String orgSelect) {
        this.orgSelect = orgSelect;
    }

    public String getDictSelect() {
        return dictSelect;
    }

    public void setDictSelect(String dictSelect) {
        this.dictSelect = dictSelect;
    }
}
