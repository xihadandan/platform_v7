package com.wellsoft.pt.cg.utils;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.generator.model.BaseServiceByTableModel;
import com.wellsoft.pt.cg.core.generator.model.EntityByTableModel;
import com.wellsoft.pt.cg.core.source.TableSource;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/7/31
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/31    chenq		2018/7/31		Create
 * </pre>
 */
public class CodeGeneratorUtils {

    //加载配置
    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(
                    DatabaseUtils.class.getResource(
                            "/").getPath() + "/code_generate.properties")));
            CodePropsUtils.loadProperties(properties);
        } catch (Exception e) {
            throw new RuntimeException("无法加载到配置文件：code_generate.properties");
        }

    }

    private static void table2Entity() throws Exception {
        Connection connection = null;
        try {
            String columnsDefine = "select t1.COLUMN_NAME as column_name,t1.DATA_TYPE, t2.COMMENTS as remark " +
                    " from all_tab_columns t1, all_col_comments t2" +
                    " where t1.TABLE_NAME = t2.TABLE_NAME" +
                    " and t1.COLUMN_NAME = t2.COLUMN_NAME" +
                    " and t1.TABLE_NAME = '%s'";
            String tables = CodePropsUtils.Properties.tables;
            if (StringUtils.isNotBlank(tables)) {
                String[] tableArr = tables.split(",");
                connection = DatabaseUtils.getConnection(
                        CodePropsUtils.Properties.dbUrl,
                        CodePropsUtils.Properties.dbUser,
                        CodePropsUtils.Properties.dbPassword);
                for (String table : tableArr) {

                    ResultSet resultSet = connection.createStatement().executeQuery(
                            String.format(columnsDefine, table.toUpperCase()));
                    /*ResultSetMetaData metaData = resultSet.getMetaData();
                    int cnt = metaData.getColumnCount();
                    for (int i = 1; i <= cnt; i++) {
                        System.out.print(String.format("%1$-30s", metaData.getColumnName(i)));
                    }

                    System.out.println("");
                    System.out.println("------------------------------------------");*/
                    TableSource tableSource = new TableSource();
                    while (resultSet.next()) {
                        /*System.out.println(
                                String.format("%1$-30s", resultSet.getString("COLUMN_NAME"))
                                        + String.format("%1$-30s", resultSet.getString("DATA_TYPE"))
                                        + resultSet.getString(
                                        "REMARK"));*/
                        TableSource.Column column = new TableSource.Column();
                        column.setColumnName(resultSet.getString("COLUMN_NAME"));
                        column.setRemark(resultSet.getString(
                                "REMARK"));
                        String dataType = resultSet.getString("DATA_TYPE");
                        if (dataType.indexOf("(") != -1) {
                            dataType = dataType.substring(0, dataType.indexOf("("));
                        }
                        column.setType(dataType);
                        tableSource.addColumn(table.toLowerCase(), column);
                    }


                    Context context = new Context();
                    context.setSource(tableSource);
                    context.setAuthor(CodePropsUtils.Properties.author);
                    context.setJavaOutputDir(CodePropsUtils.Properties.outputDir);
                    context.setPackage(CodePropsUtils.Properties.javaPackage);
                    new EntityByTableModel().freemarkerFormateProcess(context,
                            FreeMarkerTemplateUtils.getConfiguration());
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }

        }
    }


    private static void table2Service() {
        try {
            String tables = CodePropsUtils.Properties.tables;
            if (StringUtils.isNotBlank(tables)) {
                String[] tableArr = tables.split(",");
                TableSource tableSource = new TableSource();
                for (String table : tableArr) {
                    tableSource.getTableSet().add(table);
                }

                Context context = new Context();
                context.setSource(tableSource);
                context.setAuthor(CodePropsUtils.Properties.author);
                context.setJavaOutputDir(CodePropsUtils.Properties.outputDir);
                context.setPackage(CodePropsUtils.Properties.javaPackage);
                new BaseServiceByTableModel().freemarkerFormateProcess(context,
                        FreeMarkerTemplateUtils.getConfiguration());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] arrs) throws Exception {
        table2Entity();
        table2Service();

    }

}
