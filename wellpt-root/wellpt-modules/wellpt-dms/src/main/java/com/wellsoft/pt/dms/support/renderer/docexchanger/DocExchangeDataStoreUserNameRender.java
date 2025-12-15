package com.wellsoft.pt.dms.support.renderer.docexchanger;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.dms.entity.DmsDocExcContactBookEntity;
import com.wellsoft.pt.dms.enums.DocExcContactBookIdPrefixEnum;
import com.wellsoft.pt.dms.service.DmsDocExcContactBookService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description: 文档交换-文档用户名称渲染器
 *
 * @author chenq
 * @date 2018/5/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/26    chenq		2018/5/26		Create
 * </pre>
 */
@Component
public class DocExchangeDataStoreUserNameRender extends DocExchangeDataStoreRender {


    @Autowired
    OrgApiFacade orgApiFacade;

    @Autowired
    DmsDocExcContactBookService dmsDocExcContactBookService;

    @Override
    public String doRenderData(String columnIndex, Object userId, Map<String, Object> rowData,
                               String param) {

        if (userId != null) {
            if (userId.toString().startsWith(IdPrefix.ORG_VERSION.getValue())) {
                String[] userIdArr = userId.toString().split(Separator.SLASH.getValue());
                if (userIdArr.length == 2) {
                    return getUserName(userIdArr[1]);

                }
            } else if (userId.toString().startsWith(IdPrefix.USER.getValue())) {
                return getUserName(userId.toString());
            } else if (userId.toString().startsWith(
                    DocExcContactBookIdPrefixEnum.CONTACT_ID.getId())) {//文档交换模块通讯录
                DmsDocExcContactBookEntity bookEntity = dmsDocExcContactBookService.getByContactId(
                        userId.toString());
                return bookEntity != null ? bookEntity.getContactName() : "";
            }
        }

        return "";
    }


    private String getUserName(String userId) {
        OrgUserVo userVo = orgApiFacade.getUserVoById(userId);
        return userVo != null ? userVo.getUserName() : "";
    }

    @Override
    public String getType() {
        return "docExchangeDataStoreUserNameRender";
    }

    @Override
    public String getName() {
        return "数据管理_文档交换_文档用户名称渲染器";
    }
}
