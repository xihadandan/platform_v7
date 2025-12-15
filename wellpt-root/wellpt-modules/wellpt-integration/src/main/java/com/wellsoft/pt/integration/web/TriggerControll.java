/*
 * @(#)2014-12-3 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.web;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.integration.trigger.service.TriggerService;
import com.wellsoft.pt.jpa.util.DaoUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-12-3.1	zhulh		2014-12-3		Create
 * </pre>
 * @date 2014-12-3
 */
@Controller
@RequestMapping("/syn/trigger")
public class TriggerControll extends BaseController {

    @Autowired
    private TriggerService triggerService;

    private String defaultTableName = "WF_FLOW_SCHEMA";

    public static int executeSqls(String[] triggerSqls) {
        int result = 0;
        for (String triggerSql : triggerSqls) {
            if ((result = DaoUtils.executeSql(triggerSql)) < 0) {
                break;
            }
        }
        return result;
    }

    @RequestMapping("/index")
    public String index(@RequestParam(value = "tableName", required = false) String tableName) {
        return forward("/pt/basicdata/trigger/trigger_mgr");
    }

    @RequestMapping("/create")
    @ResponseBody
    public String create(@RequestParam(value = "tableName", required = false) String tableName) {
        List<String> fails = new ArrayList<String>();
//		if (!StringUtils.isBlank(tableName)) {
//			if (tableName.indexOf(Separator.SEMICOLON.getValue()) > -1) {
//				String[] tableNames = tableName.split(Separator.SEMICOLON.getValue());
//				for (String ltableName : tableNames) {
//					String[] triggerSql = triggerService.generateCreateTriggerSql(getTableName(ltableName));
//					if (TriggerControll.executeSqls(triggerSql) < 0) {
//						fails.add(getTableName(ltableName));
//					}
//				}
//			} else {
//				String[] triggerSql = triggerService.generateCreateTriggerSql(getTableName(tableName));
//				if (TriggerControll.executeSqls(triggerSql) < 0) {
//					fails.add(getTableName(tableName));
//				}
//			}
//		} else {
//			List<String> nameList = triggerService.getAlltableName();
//			for (String tbName : nameList) {
//				String[] triggerSql = triggerService.generateCreateTriggerSql(getTableName(tbName));
//				if (TriggerControll.executeSqls(triggerSql) < 0) {
//					fails.add(getTableName(tableName));
//				}
//			}
//		}
        return fails.isEmpty() ? "success" : "以下对象操作失败：" + StringUtils.join(fails, Separator.COMMA.getValue());
    }

    @RequestMapping("/reGenerate")
    @ResponseBody
    public String reGenerate(@RequestParam(value = "tableName", required = false) String tableName) {
        List<String> fails = new ArrayList<String>();
//		if (!StringUtils.isBlank(tableName)) {
//			if (tableName.indexOf(Separator.SEMICOLON.getValue()) > -1) {
//				String[] tableNames = tableName.split(Separator.SEMICOLON.getValue());
//				for (String ltableName : tableNames) {
//					String triggerSql = triggerService.generateDropTriggerSql(getTableName(ltableName));
//					DaoUtils.executeSql(triggerSql);
//					String[] triggerSqls = triggerService.generateCreateTriggerSql(getTableName(ltableName));
//					if (TriggerControll.executeSqls(triggerSqls) < 0) {
//						fails.add(getTableName(ltableName));
//					}
//				}
//			} else {
//				String triggerSql = triggerService.generateDropTriggerSql(getTableName(tableName));
//				DaoUtils.executeSql(triggerSql);
//				String[] triggerSqls = triggerService.generateCreateTriggerSql(getTableName(tableName));
//				if (TriggerControll.executeSqls(triggerSqls) < 0) {
//					fails.add(getTableName(tableName));
//				}
//			}
//		} else {
//			List<String> nameList = triggerService.getAlltableName();
//			for (String tbName : nameList) {
//				String triggerSql = triggerService.generateDropTriggerSql(getTableName(tbName));
//				DaoUtils.executeSql(triggerSql);
//				String[] triggerSqls = triggerService.generateCreateTriggerSql(getTableName(tbName));
//				if (TriggerControll.executeSqls(triggerSqls) < 0) {
//					fails.add(getTableName(tbName));
//				}
//			}
//		}
        return fails.isEmpty() ? "success" : "以下对象操作失败：" + StringUtils.join(fails, Separator.COMMA.getValue());
    }

    @RequestMapping("/disable")
    @ResponseBody
    public String disable(@RequestParam(value = "tableName", required = false) String tableName) {
        List<String> fails = new ArrayList<String>();
//		if (!StringUtils.isBlank(tableName)) {
//			if (tableName.indexOf(Separator.SEMICOLON.getValue()) > -1) {
//				String[] tableNames = tableName.split(Separator.SEMICOLON.getValue());
//				for (String ltableName : tableNames) {
//					String triggerSql = triggerService.generateDisableTriggerSql(getTableName(ltableName));
//					if (DaoUtils.executeSql(triggerSql) < 0) {
//						fails.add(getTableName(ltableName));
//					}
//				}
//			} else {
//				String triggerSql = triggerService.generateDisableTriggerSql(getTableName(tableName));
//				if (DaoUtils.executeSql(triggerSql) < 0) {
//					fails.add(getTableName(tableName));
//				}
//			}
//		} else {
//			List<String> nameList = triggerService.getAlltableName();
//			for (String tbName : nameList) {
//				String triggerSql = triggerService.generateDisableTriggerSql(getTableName(tbName));
//				if (DaoUtils.executeSql(triggerSql) < 0) {
//					fails.add(getTableName(tbName));
//				}
//			}
//		}
        return fails.isEmpty() ? "success" : "以下对象操作失败：" + StringUtils.join(fails, Separator.COMMA.getValue());
    }

    @RequestMapping("/enable")
    @ResponseBody
    public String enable(@RequestParam(value = "tableName", required = false) String tableName) {
        List<String> fails = new ArrayList<String>();
//		if (!StringUtils.isBlank(tableName)) {
//			if (tableName.indexOf(Separator.SEMICOLON.getValue()) > -1) {
//				String[] tableNames = tableName.split(Separator.SEMICOLON.getValue());
//				for (String ltableName : tableNames) {
//					String triggerSql = triggerService.generateEnableTriggerSql(getTableName(ltableName));
//					if (DaoUtils.executeSql(triggerSql) < 0) {
//						fails.add(getTableName(ltableName));
//					}
//				}
//			} else {
//				String triggerSql = triggerService.generateEnableTriggerSql(getTableName(tableName));
//				if (DaoUtils.executeSql(triggerSql) < 0) {
//					fails.add(getTableName(tableName));
//				}
//			}
//		} else {
//			List<String> nameList = triggerService.getAlltableName();
//			for (String tbName : nameList) {
//				if (!StringUtils.isBlank(tbName)) {
//					String triggerSql = triggerService.generateEnableTriggerSql(getTableName(tbName));
//					if (DaoUtils.executeSql(triggerSql) < 0) {
//						fails.add(getTableName(tbName));
//					}
//				}
//			}
//		}
        return fails.isEmpty() ? "success" : "以下对象操作失败：" + StringUtils.join(fails, Separator.COMMA.getValue());
    }

    @RequestMapping("/drop")
    @ResponseBody
    public String drop(@RequestParam(value = "tableName", required = false) String tableName) {
        List<String> fails = new ArrayList<String>();
        if (!StringUtils.isBlank(tableName)) {
            if (tableName.indexOf(Separator.SEMICOLON.getValue()) > -1) {
                String[] tableNames = tableName.split(Separator.SEMICOLON.getValue());
                for (String ltableName : tableNames) {
                    String triggerSql = triggerService.generateDropTriggerSql(getTableName(ltableName));
                    if (DaoUtils.executeSql(triggerSql) < 0) {
                        fails.add(getTableName(ltableName));
                    }
                }
            } else {
                String triggerSql = triggerService.generateDropTriggerSql(getTableName(tableName));
                if (DaoUtils.executeSql(triggerSql) < 0) {
                    fails.add(getTableName(tableName));
                }
            }
        } else {
            List<String> nameList = triggerService.getAlltableName();
            for (String tbName : nameList) {
                String triggerSql = triggerService.generateDropTriggerSql(getTableName(tbName));
                if (DaoUtils.executeSql(triggerSql) < 0) {
                    fails.add(getTableName(tbName));
                }
            }
        }
        return fails.isEmpty() ? "success" : "以下对象操作失败：" + StringUtils.join(fails, Separator.COMMA.getValue());
    }

    /**
     * @param tableName
     * @return
     */
    private String getTableName(String tableName) {
        return StringUtils.isBlank(tableName) ? defaultTableName : tableName.toUpperCase();
    }
}
