package com.wellpt.code.builder.utils;


import com.wellpt.code.builder.support.AbstractDialect;
import com.wellpt.code.builder.support.Context;
import com.wellpt.code.builder.support.TableSource;

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
            properties.load(new FileInputStream(new File("./config.properties")));
            CodePropsUtils.loadProperties(properties);
        } catch (Exception e) {
            throw new RuntimeException("无法加载到配置文件：config.properties");
        }

    }

    public static void generateEntityCode() throws Exception {

        Connection connection = null;
        try {
            String columnsDefine = "select t1.COLUMN_NAME as column_name,t1.DATA_TYPE, t2.COMMENTS as remark " +
                    " from all_tab_columns t1, all_col_comments t2" +
                    " where t1.TABLE_NAME = t2.TABLE_NAME" +
                    " and t1.COLUMN_NAME = t2.COLUMN_NAME" +
                    " and t1.TABLE_NAME = '%s'";
            String tables = CodePropsUtils.Properties.tables;
            if (CommonUtils.isNotBlank(tables)) {
                String[] tableArr = tables.split(",");
                connection = DatabaseUtils.getConnection(
                        CodePropsUtils.Properties.dbUrl,
                        CodePropsUtils.Properties.dbUser,
                        CodePropsUtils.Properties.dbPassword);
                for (String table : tableArr) {
                    ResultSet resultSet = connection.createStatement().executeQuery(
                            String.format(columnsDefine, table.toUpperCase()));

                    TableSource tableSource = new TableSource();
                    while (resultSet.next()) {

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
                    context.setDialect(AbstractDialect.build(CodePropsUtils.Properties.dbUrl));
                    ConvertionUtils.table2Entity(context,
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


    public static void generateServiceCode() {
        try {
            String tables = CodePropsUtils.Properties.tables;
            if (CommonUtils.isNotBlank(tables)) {
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
                context.setDialect(AbstractDialect.build(CodePropsUtils.Properties.dbUrl));
                ConvertionUtils.table2ServiceDao(context,
                        FreeMarkerTemplateUtils.getConfiguration());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
