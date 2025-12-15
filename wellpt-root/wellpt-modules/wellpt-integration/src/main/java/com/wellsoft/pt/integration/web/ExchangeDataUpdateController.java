package com.wellsoft.pt.integration.web;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.integration.entity.ExchangeData;
import com.wellsoft.pt.integration.entity.SysProperties;
import com.wellsoft.pt.integration.facade.ExchangedataApiFacade;
import com.wellsoft.pt.integration.ftp.CommonFtp;
import com.wellsoft.pt.integration.provider.SynUpdateClass;
import com.wellsoft.pt.integration.service.*;
import com.wellsoft.pt.integration.trigger.service.TriggerService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description: 更新数据
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-23.1	ruanhg		2014-6-23		Create
 * </pre>
 * @date 2014-6-23
 */
@Controller
@RequestMapping("/exchangedata/client")
public class ExchangeDataUpdateController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ExchangeDataUpdateService exchangeDataUpdateService;
    @Autowired
    private ExchangeDataSynchronousService exchangeDataSynchronousService;
    @Autowired
    private ExchangeDataSynService exchangeDataSynService;
    @Autowired
    private ExchangeDataOpService dataOpService;
    @Autowired
    private TriggerService triggerService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private ExchangedataApiFacade exchangedataApiFacade;
    @Autowired
    private ExchangeDataFlowService exchangeDataFlowService;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private Map<String, SynUpdateClass> synUpdateClass;

    @RequestMapping(value = "/delete")
    public String deleteDataByDataId(@RequestParam(value = "dataId") String dataId) {
        long startTime = System.currentTimeMillis();
        String str = "";// dataId 用","隔开
        String[] dataIds = str.split(";");
        for (int i = 0; i < dataIds.length; i++) {
            exchangeDataUpdateService.deleteDataByDataId(dataIds[i]);
            System.out.println("----------------num " + i + "--------------");
        }
        long endTime1 = System.currentTimeMillis();
        System.out.println("程序运行时间: " + (endTime1 - startTime) + " ms");
        return null;
    }

    @RequestMapping(value = "/goSYNTask")
    public String goSYNTask() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 小写的mm表示的是分钟
        String dstr = "2014-8-31 22:38:00";
        try {
            Date date = sdf.parse(dstr);

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            calendar.add(calendar.SECOND, -20);
            date = calendar.getTime();

            exchangeDataSynchronousService.synchronousOutData(date, "");
            exchangeDataSynchronousService.synchronousInData("");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/ftptest")
    public String ftptest() {
        // CommonFtp myFtp = new CommonFtp("10.24.36.54", 21, "dxftptest",
        // "dxftptest54");
        CommonFtp myFtp = new CommonFtp("202.109.255.87", 12200, "zwfwzx", "zwfwzx");
        try {
            if (!myFtp.isConnected()) {
                myFtp.connect();
            }
            // 判断附件是否存在
            if (myFtp.findFileByName("\\", "c6470ca85a30000188cd11401f30ae00")) {
                File downFile = myFtp.downFile("\\", "c6470ca85a30000188cd11401f30ae00");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.info(e.getMessage());
        } finally {
            myFtp.disconnect();
        }
        return "";
    }

    @RequestMapping(value = "/validFailDateUpdate")
    public String validFailDateUpdate() {
        Date starTime = new Date();
        List<ExchangeData> eds = exchangeDataUpdateService.getExchangeDataByFileSubFormData();
        int i = 1;
        for (ExchangeData ed : eds) {
            try {
                String text = IOUtils.toString(ed.getText().getCharacterStream()) == null ? "" : IOUtils.toString(ed
                        .getText().getCharacterStream());
                exchangeDataUpdateService.xmlUpdateSubFormDate(text, ed.getFormDataId(), ed.getFormId());
                System.out.println("***********************edb di**************" + i);
                i++;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            }
        }
        Date endTimer2 = new Date();
        long difference2 = (endTimer2.getTime() - starTime.getTime());
        System.out.println("***********************edb shuliang**************" + eds.size());
        System.out.println("***********************edb shijian**************" + difference2);
        return null;
    }

    @RequestMapping(value = "/moniCallbackClientAndreceiveClient")
    public String moniCallbackClientAndreceiveClient() {
        Date starTime = new Date();
        List<ExchangeData> eds = exchangeDataUpdateService.getExchangeDataByFileSubFormData();
        System.out.println("***********************edbs shuliang**************" + eds.size());
        int i = 1;
        for (ExchangeData ed : eds) {
            exchangeDataUpdateService.moniCallbackClientAndreceiveClient(ed.getUuid());
            System.out.println("***********************edbs di**************" + i);
            i++;
        }
        Date endTimer2 = new Date();
        long difference2 = (endTimer2.getTime() - starTime.getTime());
        System.out.println("***********************edbs shijian**************" + difference2);
        return null;
    }

    /**
     * 导入旧建管系统项目
     *
     * @return
     */
    @RequestMapping(value = "/importproject1")
    public String importproject1() {
        int pc = 17480;
        int zq = 5;
        for (int i = 0; i < pc / zq; i++) {
            int begin = i * zq + 1;
            int end = (i + 1) * zq;
            Date starTime2 = new Date();
            exchangeDataUpdateService.importproject(begin, end);
            Date endTimer2 = new Date();
            long difference2 = (endTimer2.getTime() - starTime2.getTime());
            logger.error("*********************** importproject1 " + (i + 1) + " 次执行时间**************" + difference2);
        }
        return null;
    }

    @RequestMapping(value = "/importproject2")
    public String importproject2() {
        int pc = 17480;
        int zq = 5;
        for (int i = 0; i < pc / zq; i++) {
            int begin = (i * zq + 1) + pc;
            int end = ((i + 1) * zq) + pc;
            Date starTime2 = new Date();
            exchangeDataUpdateService.importproject(begin, end);
            Date endTimer2 = new Date();
            long difference2 = (endTimer2.getTime() - starTime2.getTime());
            logger.error("*********************** importproject2 " + (i + 1) + " 次执行时间**************" + difference2);
        }
        return null;
    }

    @RequestMapping(value = "/importproject3")
    public String importproject3() {
        int pc = 17480;
        int zq = 5;
        for (int i = 0; i < pc / zq; i++) {
            int begin = (i * zq + 1) + pc * 2;
            int end = ((i + 1) * zq) + pc * 2;
            Date starTime2 = new Date();
            exchangeDataUpdateService.importproject(begin, end);
            Date endTimer2 = new Date();
            long difference2 = (endTimer2.getTime() - starTime2.getTime());
            logger.error("*********************** importproject3 " + (i + 1) + " 次执行时间**************" + difference2);
        }
        return null;
    }

    @RequestMapping(value = "/importproject4")
    public String importproject4() {
        int pc = 17480;
        int zq = 5;
        for (int i = 0; i < pc / zq; i++) {
            int begin = (i * zq + 1) + pc * 3;
            int end = ((i + 1) * zq) + pc * 3;
            Date starTime2 = new Date();
            exchangeDataUpdateService.importproject(begin, end);
            Date endTimer2 = new Date();
            long difference2 = (endTimer2.getTime() - starTime2.getTime());
            logger.error("*********************** importproject4 " + (i + 1) + " 次执行时间**************" + difference2);
        }
        return null;
    }

    @RequestMapping(value = "/importproject5")
    public String importproject5() {
        int pc = 17480;
        int zq = 5;
        for (int i = 0; i < pc / zq; i++) {
            int begin = (i * zq + 1) + pc * 4;
            int end = ((i + 1) * zq) + pc * 4;
            Date starTime2 = new Date();
            exchangeDataUpdateService.importproject(begin, end);
            Date endTimer2 = new Date();
            long difference2 = (endTimer2.getTime() - starTime2.getTime());
            logger.error("*********************** importproject5 " + (i + 1) + " 次执行时间**************" + difference2);
        }
        return null;
    }

    @RequestMapping(value = "/importproject6")
    public String importproject6() {
        int pc = 17480;
        int zq = 5;
        for (int i = 0; i < pc / zq; i++) {
            int begin = (i * zq + 1) + pc * 5;
            int end = ((i + 1) * zq) + pc * 5;
            Date starTime2 = new Date();
            exchangeDataUpdateService.importproject(begin, end);
            Date endTimer2 = new Date();
            long difference2 = (endTimer2.getTime() - starTime2.getTime());
            logger.error("*********************** importproject6 " + (i + 1) + " 次执行时间**************" + difference2);
        }
        return null;
    }

    @RequestMapping(value = "/importproject7")
    public String importproject7() {
        int pc = 46;
        int zq = 2;
        for (int i = 0; i < pc / zq; i++) {
            int begin = (i * zq + 1) + 104880;
            int end = ((i + 1) * zq) + 104880;
            Date starTime2 = new Date();
            exchangeDataUpdateService.importproject(begin, end);
            Date endTimer2 = new Date();
            long difference2 = (endTimer2.getTime() - starTime2.getTime());
            logger.error("*********************** importproject7" + (i + 1) + " 次执行时间**************" + difference2);
        }
        return null;
    }

    @RequestMapping(value = "/userp")
    @ResponseBody
    public String userp() {
        Map<String, String> m = new HashMap<String, String>();
        Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
        String sql = "select t.uuid as uuid,t.login_name as login_name,t.password as password,t.user_name as user_name from org_user t where t.enabled=1";
        List<Map<String, Object>> list = exchangeDataUpdateService.queryList(sql);
        for (Map<String, Object> map : list) {
            String name = map.get("login_name") == null ? "" : map.get("login_name").toString();
            String adminPwd = passwordEncoder.encodePassword("0", name);
            String upsql = "update org_user u set u.password='" + adminPwd + "' where u.uuid='"
                    + map.get("uuid").toString() + "'";
            exchangeDataUpdateService.executeUpdate(upsql);
            // if (password.equals(adminPwd)) {
            // m.put(name, map.get("user_name") == null ? "" :
            // map.get("user_name").toString());
            // }
        }
        return "success";
    }

    @RequestMapping(value = "/getPs")
    @ResponseBody
    public String getPs(@RequestParam(value = "n") String n, @RequestParam(value = "p") String p) {
        Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
        String newPas = passwordEncoder.encodePassword(p, n);
        return newPas;
    }

    @RequestMapping(value = "/userPs")
    @ResponseBody
    public String userPs() {
        Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
        String sql = "select t.uuid as uuid,t.login_name as login_name,t.password as password,t.user_name as user_name from org_user t where t.enabled=1";
        List<Map<String, Object>> list = exchangeDataUpdateService.queryList(sql);
        for (Map<String, Object> map : list) {
            String name = map.get("login_name") == null ? "" : map.get("login_name").toString();
            // String password = map.get("password") == null ? "" :
            // map.get("password").toString();
            String adminPwd = passwordEncoder.encodePassword("0", name);
            // if (password.equals(adminPwd)) {
            // String newPas = passwordEncoder.encodePassword(name + "123",
            // name);
            String upsql = "update org_user u set u.password='" + adminPwd + "' where u.uuid='"
                    + map.get("uuid").toString() + "'";
            exchangeDataUpdateService.executeUpdate(upsql);
            // }
        }
        return "success";
    }

    @RequestMapping(value = "/synOutData")
    @ResponseBody
    public synchronized String synOutData() {
        Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
        try {
            try {

                StringBuffer param = new StringBuffer(" and o.stable_name not in (");
                param.append(ExchangeDataSynService.PARAM_MSG).append(",");
                param.append(ExchangeDataSynService.PARAM_ACL).append(",");
                param.append(ExchangeDataSynService.PARAM_REPO).append(",");
                param.append(ExchangeDataSynService.PARAM_FLOW).append(") ");
                exchangeDataSynService.synOutData(sysPropertiess, param.toString(), ExchangeDataSynService.GROUP_OTHER);
            } catch (Exception e) {
                logger.error("SynchronousThreeOutTask业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name in (");
                param.append(ExchangeDataSynService.PARAM_MSG).append(") ");
                exchangeDataSynService.synOutData(sysPropertiess, param.toString(), ExchangeDataSynService.GROUP_MSG);
            } catch (Exception e) {
                logger.error("PARAM_MSG业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name in (");
                param.append(ExchangeDataSynService.PARAM_FLOW).append(") ");
                exchangeDataSynService.synOutData(sysPropertiess, param.toString(), ExchangeDataSynService.GROUP_FLOW);
            } catch (Exception e) {
                logger.error("PARAM_FLOW业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name = ");
                param.append(ExchangeDataSynService.PARAM_REPO).append(" ");
                exchangeDataSynService.synOutData(sysPropertiess, param.toString(), ExchangeDataSynService.GROUP_REPO);
            } catch (Exception e) {
                logger.error("PARAM_REPO业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name in (");
                param.append(ExchangeDataSynService.PARAM_ACL).append(") ");
                exchangeDataSynService.synOutData(sysPropertiess, param.toString(), ExchangeDataSynService.GROUP_ACL);
            } catch (Exception e) {
                logger.error("PARAM_ACL业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name not in (");
                param.append(ExchangeDataSynService.PARAM_MSG).append(",");
                param.append(ExchangeDataSynService.PARAM_ACL).append(",");
                param.append(ExchangeDataSynService.PARAM_REPO).append(",");
                param.append(ExchangeDataSynService.PARAM_FLOW).append(") ");
                exchangeDataSynService.synOutData(sysPropertiess, param.toString(), true, null);
            } catch (Exception e) {
                logger.error("SynchronousThreeOutWithClobTask业务异常");
            }

            return "SUCCESS";
        } catch (Exception e) {
            return "FAIL";
        }
    }

    @RequestMapping(value = "/runRepeat")
    @ResponseBody
    public String runRepeat() {
        exchangeDataFlowService.exchangeDataRepeatTask();
        return "";
    }

    @RequestMapping(value = "/sendTest")
    @ResponseBody
    public String sendTest() {
        exchangeDataFlowService.sendTest();
        return "";
    }

    @RequestMapping(value = "/synInData")
    @ResponseBody
    public synchronized String synInData() {
        Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
        try {
            try {
                StringBuffer param = new StringBuffer(" and o.stable_name not in (");
                param.append(ExchangeDataSynService.PARAM_MSG).append(",");
                param.append(ExchangeDataSynService.PARAM_ACL).append(",");
                param.append(ExchangeDataSynService.PARAM_REPO).append(",");
                param.append(ExchangeDataSynService.PARAM_FLOW).append(") ");
                exchangeDataSynService.synInData(sysPropertiess, param.toString(), ExchangeDataSynService.GROUP_OTHER);
            } catch (Exception e) {
                logger.error("SynchronousThreeInTask业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name in (");
                param.append(ExchangeDataSynService.PARAM_ACL).append(") ");
                exchangeDataSynService.synInData(sysPropertiess, param.toString(), ExchangeDataSynService.GROUP_ACL);
            } catch (Exception e) {
                logger.error("SynchronousThreeACLInTask业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name = ");
                param.append(ExchangeDataSynService.PARAM_REPO).append(" ");
                exchangeDataSynService.synInData(sysPropertiess, param.toString(), ExchangeDataSynService.GROUP_REPO);
            } catch (Exception e) {
                logger.error("SynchronousThreeFileInTask业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name in (");
                param.append(ExchangeDataSynService.PARAM_FLOW).append(") ");
                exchangeDataSynService.synInData(sysPropertiess, param.toString(), ExchangeDataSynService.GROUP_FLOW);
            } catch (Exception e) {
                logger.error("SynchronousThreeFLOWInTask业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name in (");
                param.append(ExchangeDataSynService.PARAM_MSG).append(") ");
                exchangeDataSynService.synInData(sysPropertiess, param.toString(), ExchangeDataSynService.GROUP_MSG);
            } catch (Exception e) {
                logger.error("SynchronousThreeMSGInTask业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name not in (");
                param.append(ExchangeDataSynService.PARAM_MSG).append(",");
                param.append(ExchangeDataSynService.PARAM_ACL).append(",");
                param.append(ExchangeDataSynService.PARAM_REPO).append(",");
                param.append(ExchangeDataSynService.PARAM_FLOW).append(") ");
                exchangeDataSynService.synInData(sysPropertiess, param.toString(), true, null);
            } catch (Exception e) {
                logger.error("SynchronousThreeInWithClobTask业务异常");
            }

            return "SUCCESS";
        } catch (Exception e) {
            return "FAIL";
        }
    }

    @RequestMapping(value = "/synBackData")
    @ResponseBody
    public synchronized String synBackData() {
        Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
        try {
            try {
                StringBuffer param = new StringBuffer(" and o.stable_name not in (");
                param.append(ExchangeDataSynService.PARAM_MSG).append(",");
                param.append(ExchangeDataSynService.PARAM_ACL).append(",");
                param.append(ExchangeDataSynService.PARAM_REPO).append(",");
                param.append(ExchangeDataSynService.PARAM_FLOW).append(") ");
                exchangeDataSynService.synBackData(sysPropertiess, param.toString());
            } catch (Exception e) {
                logger.error("SynchronousThreeInTask业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name in (");
                param.append(ExchangeDataSynService.PARAM_ACL).append(") ");
                exchangeDataSynService.synBackData(sysPropertiess, param.toString());
            } catch (Exception e) {
                logger.error("SynchronousThreeACLInTask业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name = ");
                param.append(ExchangeDataSynService.PARAM_REPO).append(" ");
                exchangeDataSynService.synBackData(sysPropertiess, param.toString());
            } catch (Exception e) {
                logger.error("SynchronousThreeFileInTask业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name in (");
                param.append(ExchangeDataSynService.PARAM_FLOW).append(") ");
                exchangeDataSynService.synBackData(sysPropertiess, param.toString());
            } catch (Exception e) {
                logger.error("SynchronousThreeFLOWInTask业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name in (");
                param.append(ExchangeDataSynService.PARAM_MSG).append(") ");
                exchangeDataSynService.synBackData(sysPropertiess, param.toString());
            } catch (Exception e) {
                logger.error("SynchronousThreeMSGInTask业务异常");
            }

            try {
                StringBuffer param = new StringBuffer(" and o.stable_name not in (");
                param.append(ExchangeDataSynService.PARAM_MSG).append(",");
                param.append(ExchangeDataSynService.PARAM_ACL).append(",");
                param.append(ExchangeDataSynService.PARAM_REPO).append(",");
                param.append(ExchangeDataSynService.PARAM_FLOW).append(") ");
                exchangeDataSynService.synBackData(sysPropertiess, param.toString(), true);
            } catch (Exception e) {
                logger.error("SynchronousThreeInWithClobTask业务异常");
            }

            return "SUCCESS";
        } catch (Exception e) {
            return "FAIL";
        }
    }

    @RequestMapping(value = "/synOutDataFeedback")
    @ResponseBody
    public synchronized String synOutDataFeedback() {
        Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
        try {
            exchangeDataSynService.synOutDataFeedback(sysPropertiess);
            return "SUCCESS";
        } catch (Exception e) {
            return "FAIL";
        }
    }

    @RequestMapping(value = "/synInDataFeedback")
    @ResponseBody
    public synchronized String synInDataFeedback() {
        Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
        try {
            exchangeDataSynService.synInDataFeedback(sysPropertiess);
            return "SUCCESS";
        } catch (Exception e) {
            return "FAIL";
        }
    }

    @RequestMapping(value = "/clearSynDataPt")
    @ResponseBody
    public String clearSynDataPt() {
        Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
        try {
            exchangeDataSynService.clearSynDataAllInOne(sysPropertiess);
            return "SUCCESS";
        } catch (Exception e) {
            return "FAIL";
        }
    }

    @RequestMapping(value = "/clearSynDataQzj")
    @ResponseBody
    public String clearSynDataQzj() {
        Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
        try {
            exchangeDataSynService.clearSynDataAllInOne(sysPropertiess);
            return "SUCCESS";
        } catch (Exception e) {
            return "FAIL";
        }
    }

    @RequestMapping(value = "/exportFile")
    public void exportFile() {
        DyFormFacade DyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        DyFormFormDefinition dyFormDefinition = DyFormFacade.getFormDefinitionById("uf_test20141201");
        String str = dataOpService.serializeEntityToString(dyFormDefinition);
        try {
            FileWriter writer = new FileWriter("D:\\dxtest\\uf_test20141201.txt");
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }

    }

    @RequestMapping(value = "/importFile")
    public void importFile() {
        String serializeStr = "";
        try {
            File file = new File("D:\\dxtest\\uf_test20141201.txt");
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = "";
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    serializeStr += lineTxt;
                    System.out.println(lineTxt);
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            logger.info(e.getMessage());
        }
        dataOpService.backData(serializeStr);
    }

    @RequestMapping(value = "/tableCount")
    public String tableCount(Model model, @RequestParam(value = "begin", required = false) String begin,
                             @RequestParam(value = "end", required = false) String end) {
        if (StringUtils.isBlank(begin)) {
            begin = "";
        }
        if (StringUtils.isBlank(end)) {
            end = "";
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String sql = "";
        if (!StringUtils.isBlank(begin) && !StringUtils.isBlank(end)) {
            sql = "select count(*) as count_ from [tableName] t where t.modify_time > to_date('" + begin
                    + "', 'yyyy-MM-dd')" + " and t.modify_time < to_date('" + end + "', 'yyyy-MM-dd')";
        } else if (!StringUtils.isBlank(begin) && StringUtils.isBlank(end)) {
            sql = "select count(*) as count_ from [tableName] t where t.modify_time > to_date('" + begin
                    + "', 'yyyy-MM-dd')";
        } else if (StringUtils.isBlank(begin) && !StringUtils.isBlank(end)) {
            sql = "select count(*) as count_ from [tableName] t where t.modify_time < to_date('" + end
                    + "', 'yyyy-MM-dd')";
        } else {
            sql = "select count(*) as count_ from [tableName] t";
        }
        String sql2 = "select count(*) as count_ from [tableName] t";
        List<String> nameList = triggerService.getAlltableName();
        int i = 1;
        for (String tbName : nameList) {
            int count_ = 0;
            String temp = "";
            try {
                temp = sql.replace("[tableName]", tbName);
                List<Map<String, Object>> countList = this.exchangeDataUpdateService.queryList(temp);
                count_ = Integer.parseInt(countList.get(0).get("count_").toString());
            } catch (Exception e) {
                temp = sql2.replace("[tableName]", tbName);
                List<Map<String, Object>> countList = this.exchangeDataUpdateService.queryList(temp);
                count_ = Integer.parseInt(countList.get(0).get("count_").toString());
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("num", i + "");
            map.put("tableName", tbName);
            map.put("count", count_ + "");
            list.add(map);
        }
        model.addAttribute("begin", begin);
        model.addAttribute("end", end);
        model.addAttribute("tablesCount", list);

        return "pt/exchangedata/tableStatistics";
    }

    @RequestMapping(value = "/existFiles")
    public String existFiles(Model model, @RequestParam(value = "begin", required = false) String begin,
                             @RequestParam(value = "end", required = false) String end) {
        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(begin)) {
            begin = "";
        }
        if (StringUtils.isBlank(end)) {
            end = "";
        }
        String sql = "";
        if (!StringUtils.isBlank(begin) && !StringUtils.isBlank(end)) {
            sql = "select uuid,file_name,file_size,physical_file_id from repo_file t where t.modify_time > to_date('"
                    + begin + "', 'yyyy-MM-dd')" + " and t.modify_time < to_date('" + end
                    + "', 'yyyy-MM-dd') order by t.modify_time asc";
        } else if (!StringUtils.isBlank(begin) && StringUtils.isBlank(end)) {
            sql = "select uuid,file_name,file_size,physical_file_id from repo_file t where t.modify_time > to_date('"
                    + begin + "', 'yyyy-MM-dd') order by t.modify_time asc";
        } else if (StringUtils.isBlank(begin) && !StringUtils.isBlank(end)) {
            sql = "select uuid,file_name,file_size,physical_file_id from repo_file t where t.modify_time < to_date('"
                    + end + "', 'yyyy-MM-dd') order by t.modify_time asc";
        } else {
            sql = "select uuid,file_name,file_size,physical_file_id from repo_file t where rownum<=100  order by t.modify_time asc";
        }
        List<Map<String, Object>> list = this.exchangeDataUpdateService.queryList(sql);
        for (Map<String, Object> m : list) {
            Map<String, Object> map = new HashMap<String, Object>();
            String physicalFileId = m.get("physical_file_id").toString();
            map.putAll(m);
            map.put("existFile", mongoFileService.existFile(physicalFileId) ? "存在" : "不存在");
            // map.put("existFile", "存在");
            sourceList.add(map);
        }
        model.addAttribute("begin", begin);
        model.addAttribute("end", end);
        model.addAttribute("list", sourceList);
        model.addAttribute("num", sourceList.size());
        return "pt/exchangedata/existFiles";
    }

    /**
     * 数据收集表分析
     *
     * @param model
     * @param begin
     * @param end
     * @return
     */
    @RequestMapping(value = "/synTableAction1Verify")
    public String synTableAction1Verify(Model model, @RequestParam(value = "begin", required = false) String begin,
                                        @RequestParam(value = "end", required = false) String end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isBlank(begin)) {
            begin = "";
        }
        if (StringUtils.isBlank(end)) {
            end = "";
        }
        DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        // 获得当前日期
        Date date = new Date();
        // 1.使用java.text.SimpleDateFormat将日期转换为字符串
        String str = formatDate.format(date);
        model.addAttribute("begin", begin);
        model.addAttribute("end", end);
        Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
        String xt_wl = sysPropertiess.get("xt_wl").getProValue();// 内网/外网 in/out
        int direction = 0;
        if (xt_wl.equals("in")) {// 当前是内网
            direction = 2;
        } else if (xt_wl.equals("out")) {// 当前是外网
            direction = 1;
        }
        String sqlCount1 = "select count(*) as count_ "
                + "from tig_table_data  where status!=6 and status!=7 and direction=" + direction;
        String sqlCount2 = "select count(*) as count_ " + "from tig_table_data  where status=4 and direction="
                + direction;
        String sqlCount3 = "select count(*) as count_ " + "from tig_table_data  where status=1 and direction="
                + direction;
        String sql = "select uuid,suuid,stable_name,create_time,composite_key"
                + " from tig_table_data  where action=1 and status=4 and direction=" + direction;
        if (!StringUtils.isBlank(begin) && !StringUtils.isBlank(end)) {
            sql += " and create_time > to_date('" + begin + "', 'yyyy-MM-dd')" + " and create_time < to_date('" + end
                    + "', 'yyyy-MM-dd') order by create_time asc";
            sqlCount1 += " and create_time > to_date('" + begin + "', 'yyyy-MM-dd')" + " and create_time < to_date('"
                    + end + "', 'yyyy-MM-dd') order by create_time asc";
            sqlCount2 += " and create_time > to_date('" + begin + "', 'yyyy-MM-dd')" + " and create_time < to_date('"
                    + end + "', 'yyyy-MM-dd') order by create_time asc";
            sqlCount3 += " and create_time > to_date('" + begin + "', 'yyyy-MM-dd')" + " and create_time < to_date('"
                    + end + "', 'yyyy-MM-dd') order by create_time asc";
        } else if (!StringUtils.isBlank(begin) && StringUtils.isBlank(end)) {
            sql += "and create_time > to_date('" + begin + "', 'yyyy-MM-dd') order by create_time asc";
            sqlCount1 += " and create_time > to_date('" + begin + "', 'yyyy-MM-dd') order by create_time asc";
            sqlCount2 += " and create_time > to_date('" + begin + "', 'yyyy-MM-dd') order by create_time asc";
            sqlCount3 += " and create_time > to_date('" + begin + "', 'yyyy-MM-dd') order by create_time asc";
        } else if (StringUtils.isBlank(begin) && !StringUtils.isBlank(end)) {
            sql += "and create_time < to_date('" + end + "', 'yyyy-MM-dd') order by create_time asc";
            sqlCount1 += " and create_time < to_date('" + end + "', 'yyyy-MM-dd') order by create_time asc";
            sqlCount2 += " and create_time < to_date('" + end + "', 'yyyy-MM-dd') order by create_time asc";
            sqlCount3 += " and create_time < to_date('" + end + "', 'yyyy-MM-dd') order by create_time asc";
        } else {
            sql += " and create_time > to_date('" + str + "', 'yyyy-MM-dd') order by create_time asc";
            sqlCount1 += " and create_time > to_date('" + str + "', 'yyyy-MM-dd') order by create_time asc";
            sqlCount2 += " and create_time > to_date('" + str + "', 'yyyy-MM-dd') order by create_time asc";
            sqlCount3 += " and create_time > to_date('" + str + "', 'yyyy-MM-dd') order by create_time asc";
        }
        List<Map<String, Object>> countlist1 = this.exchangeDataUpdateService.queryList(sqlCount1);
        List<Map<String, Object>> countlist2 = this.exchangeDataUpdateService.queryList(sqlCount2);
        List<Map<String, Object>> countlist3 = this.exchangeDataUpdateService.queryList(sqlCount3);
        int count1_ = Integer.parseInt(countlist1.get(0).get("count_") + "");
        int count2_ = Integer.parseInt(countlist2.get(0).get("count_") + "");
        int count3_ = Integer.parseInt(countlist3.get(0).get("count_") + "");
        model.addAttribute("count1", count1_);
        model.addAttribute("count2", count2_);
        model.addAttribute("count3", count3_);

        List<Map<String, String>> rList = new ArrayList<Map<String, String>>();
        for (Map<String, Object> m : this.exchangeDataUpdateService.queryList(sql)) {
            String suuid = m.get("suuid").toString();
            String composite_key = m.get("composite_key") == null ? "" : m.get("composite_key").toString();
            String stable_name = m.get("stable_name").toString();
            String sql1 = "select count(*) as count_ from " + stable_name;
            if (!StringUtils.isBlank(composite_key)) {// 联合主键
                sql1 += " where ";
                String[] compositeKeyArr = composite_key.split(";");
                String[] compositeValueArr = suuid.split(";");
                for (int i = 0; i < compositeKeyArr.length; i++) {
                    if (i == 0) {
                        sql1 += compositeKeyArr[i] + "='" + compositeValueArr[i] + "'";
                    } else {
                        sql1 += " and " + compositeKeyArr[i] + "='" + compositeValueArr[i] + "'";
                    }
                }
            } else {
                sql1 += " where uuid='" + suuid + "'";
            }
            List<Map<String, Object>> list1 = this.exchangeDataUpdateService.queryList(sql1);
            int count_ = Integer.parseInt(list1.get(0).get("count_") + "");
            if (count_ == 0) {
                String uuid = m.get("uuid").toString();
                Date create_time = (Date) m.get("create_time");
                Map<String, String> map = new HashMap<String, String>();
                map.put("uuid", uuid);
                map.put("stable", stable_name);
                map.put("suuid", suuid);
                map.put("compositeKey", composite_key);
                map.put("createTime", sdf.format(create_time));
                rList.add(map);
            }
        }
        // 过滤业务数据创建又被删除的
        List<Map<String, String>> rList2 = new ArrayList<Map<String, String>>();
        for (Map<String, String> m : rList) {
            String action3Sql = "select * from tig_table_data t where t.suuid='" + m.get("suuid").toString()
                    + "' and t.action=3 and t.status=4";
            if (this.exchangeDataUpdateService.queryList(action3Sql).size() == 0) {// 找不到删除记录
                rList2.add(m);
            }
        }
        model.addAttribute("rList", rList2);
        model.addAttribute("count4", rList2.size());
        model.addAttribute("begin", begin);
        model.addAttribute("end", end);
        return "pt/exchangedata/synTableAction1Verify";
    }

    @RequestMapping(value = "/tgcs")
    public String tgcs(Model model) {
        String sql = "select * from uf_tg_test where rownum<=100 order by px desc ";
        List<Map<String, Object>> list = this.exchangeDataUpdateService.queryList(sql);
        model.addAttribute("list", list);
        return "pt/exchangedata/tgcs";
    }

    @RequestMapping(value = "/deltgcs")
    @ResponseBody
    public String deltgcs(@RequestParam(value = "uuids", required = false) String uuids) {
        String sql = "";
        if (StringUtils.isBlank(uuids)) {
            sql = "delete uf_tg_test";
        } else {
            String uuidtemp = "'" + uuids.replaceFirst(";", "").replace(";", "','") + "'";
            sql = "delete uf_tg_test t where t.uuid in (" + uuidtemp + ")";
        }
        this.exchangeDataUpdateService.executeUpdate(sql);
        return "success";
    }

    @RequestMapping(value = "/uploadtg")
    @ResponseBody
    public String uploadtg(String fileId) {
        MongoFileEntity mfn = mongoFileService.getFilesFromFolder(fileId, "attach").get(0);
        List<Map<String, Object>> list = basicDataApiFacade.getListByExcel(mfn.getInputstream(), "tgcs");
        this.exchangeDataUpdateService.executeUpdate("delete uf_tg_test");
        for (Map<String, Object> itme : list) {
            exchangeDataUpdateService.saveYhq(itme);
        }
        return "success";
    }

    /**
     * 如何描述该方法
     *
     * @param json      json字符串
     * @param className 实现类名称
     * @return
     */
    @RequestMapping(value = "/synUpdate")
    @ResponseBody
    public String synUpdate(String json, String className) {
        return synUpdateClass.get(className).synUpdate(json);
    }

}
