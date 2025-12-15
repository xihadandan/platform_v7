package com.wellsoft.pt.message.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.preferences.facade.service.CdUserPreferencesFacadeService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.dao.UserPersonaliseDao;
import com.wellsoft.pt.message.dto.UserPerClassifyDto;
import com.wellsoft.pt.message.dto.UserPerDto;
import com.wellsoft.pt.message.dto.UserPersonaliseDto;
import com.wellsoft.pt.message.entity.MessageClassify;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.entity.UserPersonalise;
import com.wellsoft.pt.message.service.MessageClassifyService;
import com.wellsoft.pt.message.service.MessageTemplateService;
import com.wellsoft.pt.message.service.UserPersonaliseService;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author yt
 * @title: UserPersonaliseServiceImpl
 * @date 2020/5/18 9:06 下午
 */
@Service
public class UserPersonaliseServiceImpl extends AbstractJpaServiceImpl<UserPersonalise, UserPersonaliseDao, String> implements UserPersonaliseService {

    @Autowired
    private MessageClassifyService messageClassifyService;
    @Autowired
    private MessageTemplateService messageTemplateService;
    @Autowired
    private CdUserPreferencesFacadeService cdUserPreferencesFacadeService;

    /**
     * 保存个性化设置
     *
     * @param mainSwitch  主开关
     * @param templateIds
     * @param isPopups
     */
    @Override
    @Transactional
    public void saveUserPersonalise(Integer mainSwitch, String[] templateIds, Integer[] isPopups) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<UserPersonalise> userPersonalises = Lists.newArrayList();
        for (int i = 0; i < templateIds.length; i++) {
            String templateId = templateIds[i];
            Integer isPopup = isPopups[i];
            UserPersonalise userPersonalise = new UserPersonalise();
            userPersonalise.setUserId(userId);
            userPersonalise.setSystemUnitId(systemUnitId);
            userPersonalise.setTemplateId(templateId);
            userPersonalise.setType(2);
            userPersonalise.setIsPopup(isPopup.intValue());
            userPersonalises.add(userPersonalise);
        }
        //主开关处理
        UserPersonalise userPersonalise = new UserPersonalise();
        userPersonalise.setUserId(userId);
        userPersonalise.setSystemUnitId(systemUnitId);
        userPersonalise.setIsPopup(mainSwitch);
        userPersonalise.setType(1);
        userPersonalises.add(userPersonalise);
        cdUserPreferencesFacadeService.save("MESSAGE", "message_settings", userId, "new_message_notice_switch", JsonUtils.object2Json(userPersonalises), "消息通知设置");
    }


    /**
     * 查询个性化设置
     *
     * @return
     */
    @Override
    public UserPerDto queryList() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        UserPerDto userPerDto = new UserPerDto();
        List<MessageClassify> classifies = messageClassifyService.facadeWinQueryList();
        List<UserPersonalise> userPersonalises = listUserPersonalise(userId);
        List<UserPerClassifyDto> userPerClassifyDtos = new ArrayList<>();
        for (MessageClassify classify : classifies) {
            UserPerClassifyDto userPerClassifyDto = new UserPerClassifyDto();
            userPerClassifyDto.setClassify(classify);
            List<MessageTemplate> messageTemplates = messageTemplateService.getMessageTemplatesByClassifyUuid(classify.getUuid());
            userPerClassifyDto.setUserPersonalises(this.addDtos(userId, messageTemplates, userPersonalises));
            userPerClassifyDtos.add(userPerClassifyDto);
        }
        UserPerClassifyDto userPerClassifyDto = new UserPerClassifyDto();
        List<MessageTemplate> messageTemplates = messageTemplateService.getMessageTemplatesByClassifyUuidIsNull();
        userPerClassifyDto.setUserPersonalises(this.addDtos(userId, messageTemplates, userPersonalises));
        userPerClassifyDtos.add(userPerClassifyDto);
        userPerDto.setUserPerClassifys(userPerClassifyDtos);

        //主开关
        Map<String, Object> values = Maps.newHashMap();
        values.put("userId", userId);
        UserPersonalise userPersonalise = getMainUserPersonalise(userId);// this.dao.getOneByHQL("from UserPersonalise t where t.type=1 and t.userId=:userId ", values);
        if (userPersonalise != null) {
            userPerDto.setMainSwitch(userPersonalise.getIsPopup());
        } else {
            userPerDto.setMainSwitch(1);
        }
        return userPerDto;
    }

    /**
     * @param userId
     * @return
     */
    private List<UserPersonalise> listUserPersonalise(String userId) {
        String dataValue = cdUserPreferencesFacadeService.getDataValue("MESSAGE", "message_settings", userId, "new_message_notice_switch");
        if (StringUtils.isBlank(dataValue)) {
            return Collections.emptyList();
        }
        List<UserPersonalise> userPersonalises = Lists.newArrayList();
        try {
            JSONArray jsonArray = new JSONArray(dataValue);
            for (int index = 0; index < jsonArray.length(); index++) {
                userPersonalises.add(JsonUtils.json2Object(jsonArray.getJSONObject(index).toString(), UserPersonalise.class));
            }
        } catch (JSONException e) {
        }
        return userPersonalises;
    }

    /**
     * @param userId
     * @return
     */
    private UserPersonalise getMainUserPersonalise(String userId) {
        List<UserPersonalise> userPersonalises = listUserPersonalise(userId);
        if (CollectionUtils.isEmpty(userPersonalises)) {
            return null;
        }
        for (UserPersonalise userPersonalise : userPersonalises) {
            if (Integer.valueOf(1).equals(userPersonalise.getType())) {
                return userPersonalise;
            }
        }
        return null;
    }

    /**
     * @param userId
     * @param templateId
     * @return
     */
    private UserPersonalise getPopupWinUserPersonalise(String userId, String templateId) {
        List<UserPersonalise> userPersonalises = listUserPersonalise(userId);
        if (CollectionUtils.isEmpty(userPersonalises)) {
            return null;
        }
        for (UserPersonalise userPersonalise : userPersonalises) {
            if (StringUtils.equals(templateId, userPersonalise.getTemplateId()) && Integer.valueOf(2).equals(userPersonalise.getType())) {
                return userPersonalise;
            }
        }
        return null;
    }

    @Override
    public boolean isMain(String userId) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("userId", userId);
        UserPersonalise mainSwitch = getMainUserPersonalise(userId);// this.dao.getOneByHQL("from UserPersonalise t where t.type=1 and t.userId=:userId ", values);
        //有主开关 并且 关闭0 返回false
        if (mainSwitch != null && mainSwitch.getIsPopup().intValue() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isPopupWin(String recipient, Message message) {
        /**
         * 消息提醒方式 2：徽标+弹窗）
         * 弹窗位置：2 浏览器中间，弹窗在浏览器中间弹出
         * 强制弹窗
         */
        if (message.getReminderType() != null && message.getPopupPosition() != null
                && message.getReminderType().intValue() == 2
                && message.getPopupPosition().intValue() == 2) {
            return true;
        }
        if (!this.isMain(recipient)) {
            return false;
        }
        // Map<String, Object> values = Maps.newHashMap();
        // values.put("userId", recipient);
        // values.put("templateId", message.getTemplateId());
        UserPersonalise userPersonalise = getPopupWinUserPersonalise(recipient, message.getTemplateId());// this.dao.getOneByHQL("from UserPersonalise t where t.type=2 and t.userId=:userId and t.templateId=:templateId", values);
        //有消息格式开关
        if (userPersonalise != null) {
            //关闭0 返回false 打开1 返回true
            return userPersonalise.getIsPopup().intValue() == 0 ? false : true;
        }

        // 消息提醒方式（1：徽标，2：徽标+弹窗）
        if (message.getReminderType() != null) {
            return message.getReminderType().intValue() == 1 ? false : true;
        }

        // 是否弹窗
        if (message.getIsOnlinePopup() != null) {
            return message.getIsOnlinePopup().equals("N") ? false : true;
        }

        //默认弹窗
        return true;

    }

    private List<UserPersonaliseDto> addDtos(String userId, List<MessageTemplate> messageTemplates, List<UserPersonalise> userPersonalises) {
        List<UserPersonaliseDto> userPersonaliseDtos = new ArrayList<>();
        for (MessageTemplate messageTemplate : messageTemplates) {
            /**
             * 消息提醒方式 2：徽标+弹窗）
             * 弹窗位置：2 浏览器中间，弹窗在浏览器中间弹出
             * 强制弹窗 禁止 自定义设置
             */
            if (messageTemplate.getReminderType() != null
                    && messageTemplate.getReminderType().intValue() == 2
                    && messageTemplate.getPopupPosition().intValue() == 2) {
                continue;
            }
            UserPersonaliseDto userPersonaliseDto = new UserPersonaliseDto();
            userPersonaliseDto.setTemplateId(messageTemplate.getId());
            userPersonaliseDto.setTemplateName(messageTemplate.getName());
            UserPersonalise userPersonalise = filterUserPersonalise(userPersonalises, 2, messageTemplate.getId());// this.dao.getOneByHQL("from UserPersonalise t where t.type=2 and t.userId=:userId and t.templateId=:templateId", values);
            if (userPersonalise != null) {
                userPersonaliseDto.setIsPopup(userPersonalise.getIsPopup());
            } else {
                userPersonaliseDto.setIsPopup(messageTemplate.getReminderType() == null ? 1 : (messageTemplate.getReminderType() == 2 ? 1 : 0));
            }
            userPersonaliseDtos.add(userPersonaliseDto);
        }
        return userPersonaliseDtos.size() > 0 ? userPersonaliseDtos : null;
    }

    /**
     * @param userPersonalises
     * @param type
     * @param templateId
     * @return
     */
    private UserPersonalise filterUserPersonalise(List<UserPersonalise> userPersonalises, Integer type, String templateId) {
        for (UserPersonalise userPersonalise : userPersonalises) {
            if (type.equals(userPersonalise.getType()) && StringUtils.equals(userPersonalise.getTemplateId(), templateId)) {
                return userPersonalise;
            }
        }
        return null;
    }


    /**
     * 恢复默认
     */
    @Override
    @Transactional
    public void reset() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        cdUserPreferencesFacadeService.delete("MESSAGE", "message_settings", userId, "new_message_notice_switch");
    }
}
