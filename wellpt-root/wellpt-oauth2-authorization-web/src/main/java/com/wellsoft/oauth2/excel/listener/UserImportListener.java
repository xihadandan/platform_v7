package com.wellsoft.oauth2.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.gson.Gson;
import com.wellsoft.oauth2.dto.UserDto;
import com.wellsoft.oauth2.entity.BatchDataImportDetailsHisEntity;
import com.wellsoft.oauth2.entity.BatchDataImportHisEntity;
import com.wellsoft.oauth2.enums.DataImportTypeEnum;
import com.wellsoft.oauth2.excel.data.UserImportData;
import com.wellsoft.oauth2.service.BatchDataImportHisService;
import com.wellsoft.oauth2.service.UserAccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/25    chenq		2019/9/25		Create
 * </pre>
 */
public class UserImportListener extends AnalysisEventListener<UserImportData> {


    UserAccountService userAccountService;

    BatchDataImportHisEntity batchDataImportHisEntity;

    BatchDataImportHisService batchDataImportHisService;

    private int successCount = 0;

    private int failCount = 0;


    public UserImportListener(UserAccountService userAccountService,
                              BatchDataImportHisService batchDataImportHisService) {
        this.userAccountService = userAccountService;
        this.batchDataImportHisService = batchDataImportHisService;
    }

    @Override
    public void invoke(UserImportData userImportData, AnalysisContext analysisContext) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userImportData, userDto);
        if (batchDataImportHisEntity == null) {
            batchDataImportHisEntity = new BatchDataImportHisEntity();
            batchDataImportHisEntity.setDataImportType(DataImportTypeEnum.USER_IMPORT);
            batchDataImportHisService.save(batchDataImportHisEntity);
        }
        BatchDataImportDetailsHisEntity detailsHisEntity = new BatchDataImportDetailsHisEntity();
        detailsHisEntity.setBatchDataImportUuid(batchDataImportHisEntity.getUuid());
        detailsHisEntity.setRowIndex(analysisContext.readRowHolder().getRowIndex());
        detailsHisEntity.setImportData(new Gson().toJson(userImportData));
        StringBuilder error = new StringBuilder("");
        try {

            //校验数据
            if (StringUtils.isBlank(userDto.getAccountNumber())) {
                error.append("用户账号不为空! ");
            }
            if (StringUtils.isBlank(userDto.getUserName())) {
                error.append("用户姓名不为空! ");
            }
            if (StringUtils.isBlank(userDto.getPassword())) {
                error.append("用户密码不为空! ");
            }
            if (error.length() > 0) {
                throw new RuntimeException(error.toString());
            }
            try {
                userAccountService.addUser(userDto);
            } catch (DataIntegrityViolationException e) {
                if (e.getMessage().indexOf("idx_unique_actnbr") != -1) {
                    throw new RuntimeException("用户账号已经被注册! ");
                }
                throw new RuntimeException("保存用户账号信息异常");
            }

            detailsHisEntity.setStatus(1);
            successCount++;
        } catch (Exception e) {
            failCount++;
            detailsHisEntity.setStatus(0);
            detailsHisEntity.setErrorMsg(e.getMessage());
        }

        batchDataImportHisService.saveDetails(detailsHisEntity);


    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }


    public BatchDataImportHisEntity getBatchDataImportHisEntity() {
        return batchDataImportHisEntity;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailCount() {
        return failCount;
    }
}
