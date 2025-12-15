package com.wellsoft.pt.api.internal.suport;

import com.wellsoft.context.enums.JsonDataErrorCode;

import java.util.HashMap;
import java.util.Map;

public class ErrorCodes {

    private static Map<JsonDataErrorCode, String> errorCodeMap = new HashMap<JsonDataErrorCode, String>();

    static {
        errorCodeMap.put(JsonDataErrorCode.SessionExpired, "-1");
        errorCodeMap.put(JsonDataErrorCode.WorkFlowException, "-4");
        errorCodeMap.put(JsonDataErrorCode.TaskNotAssignedUser, "-5");
        errorCodeMap.put(JsonDataErrorCode.TaskNotAssignedCopyUser, "-6");
        errorCodeMap.put(JsonDataErrorCode.TaskNotAssignedMonitor, "-7");
        errorCodeMap.put(JsonDataErrorCode.ChooseSpecificUser, "-8");
        errorCodeMap.put(JsonDataErrorCode.OnlyChooseOneUser, "-9");
        errorCodeMap.put(JsonDataErrorCode.JudgmentBranchFlowNotFound, "-10");
        errorCodeMap.put(JsonDataErrorCode.MultiJudgmentBranch, "-11");
        errorCodeMap.put(JsonDataErrorCode.SubFlowNotFound, "-12");
        errorCodeMap.put(JsonDataErrorCode.SubFlowMerge, "-13");
        errorCodeMap.put(JsonDataErrorCode.IdentityNotFlowPermission, "-14");
        errorCodeMap.put(JsonDataErrorCode.RollbackTaskNotFound, "-15");
        errorCodeMap.put(JsonDataErrorCode.PrintReasonNotAssigned, "-16");
        errorCodeMap.put(JsonDataErrorCode.StaleObjectState, "-17");
        errorCodeMap.put(JsonDataErrorCode.FileHasCheckOut, "-18");
        errorCodeMap.put(JsonDataErrorCode.SaveData, "-19");
        errorCodeMap.put(JsonDataErrorCode.ParameterException, "-20");
        errorCodeMap.put(JsonDataErrorCode.FormDefinitionFomatException, "-21");
        errorCodeMap
                .put(JsonDataErrorCode.FormDefinitionUpdateException, "-22");
        errorCodeMap.put(JsonDataErrorCode.FormDefinitionSaveException, "-23");
        errorCodeMap.put(JsonDataErrorCode.FormDataValidateException, "-24");
        errorCodeMap.put(JsonDataErrorCode.FormModelBeUsedException, "-25");
    }

    public static String getCode(JsonDataErrorCode errorCode) {
        return errorCodeMap.get(errorCode);
    }

}
