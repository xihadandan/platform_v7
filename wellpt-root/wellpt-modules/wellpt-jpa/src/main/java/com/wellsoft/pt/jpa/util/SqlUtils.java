package com.wellsoft.pt.jpa.util;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.pt.jpa.hibernate.TablePrefixMathASTTableSource;
import com.wellsoft.pt.jpa.hibernate.TenantMysqlASTVisitorAdapter;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月16日   chenq	 Create
 * </pre>
 */
public class SqlUtils {

    public static Set<String> getTables(String sql, DbType dbType) {
        List<SQLStatement> stmtList = com.alibaba.druid.sql.SQLUtils.parseStatements(sql, dbType);
        Set<String> tables = Sets.newHashSet();
        for (SQLStatement stmt : stmtList) {
            SchemaStatVisitor visitor = com.alibaba.druid.sql.SQLUtils.createSchemaStatVisitor(dbType);
            stmt.accept(visitor);
            for (SQLName originalTable : visitor.getOriginalTables()) {
                if (originalTable instanceof SQLIdentifierExpr) {
                    String tableName = ((SQLIdentifierExpr) originalTable).getName();
                    tables.add(tableName);
                }
            }
        }
        return tables;
    }

    public static List<TableStat.Column> getColumns(String sql, DbType dbType) {
        List<SQLStatement> stmtList = com.alibaba.druid.sql.SQLUtils.parseStatements(sql, dbType);
        for (SQLStatement stmt : stmtList) {
            SchemaStatVisitor visitor = com.alibaba.druid.sql.SQLUtils.createSchemaStatVisitor(dbType);
            stmt.accept(visitor);
            return Lists.newArrayList(visitor.getColumns().toArray(new TableStat.Column[]{}));

        }
        return Collections.EMPTY_LIST;
    }

    public static void main(String[] args) {
        // System.out.println(getTables("select * from APP_FUNCTION a where exists ( select 1  from APP_DATA_DEF_REF_RESOURCE r where r.app_function_uuid=a.uuid and r.data_def_uuid =:uuid and r.data_def_type ='formDefinition')", DbType.dm));
        // 添加默认条件
        String sql = "select appmodule0_.uuid as uuid1_22_, appmodule0_.create_time as create_time2_22_, appmodule0_.creator as creator3_22_, appmodule0_.modifier as modifier4_22_, appmodule0_.modify_time as modify_time5_22_, appmodule0_.rec_ver as rec_ver6_22_, appmodule0_.system_unit_id as system_unit_id7_22_, appmodule0_.category_uuid as category_uuid8_22_, appmodule0_.code as code9_22_, appmodule0_.enabled as enabled10_22_, appmodule0_.icon as icon11_22_, appmodule0_.id as id12_22_, appmodule0_.js_module as js_module13_22_, appmodule0_.name as name14_22_, appmodule0_.parent_uuid as parent_uuid15_22_, appmodule0_.remark as remark16_22_, appmodule0_.title as title17_22_ from app_module appmodule0_ where 1=1 and (appmodule0_.name like ? or appmodule0_.id like ? or appmodule0_.remark like ?) order by appmodule0_.create_time desc";

        sql = "select t1.uuid        as uuid,\n" +
                "\t\t       t1.data_uuid   as data_uuid,\n" +
                "\t\t       t1.data_name   as name,\n" +
                "\t\t       t1.data_id     as id,\n" +
                "\t\t       t1.data_type   as type,\n" +
                "\t\t       t1.data_path   as path,\n" +
                "\t\t       t1.parent_uuid as parent_uuid,\n" +
                "\t\t       t1.app_product_uuid as product_uuid,\n" +
                "\t\t       t1.app_system_uuid as system_uuid,\n" +
                "\t\t       t1.app_page_uuid as page_uuid\n" +
                "\t\t      from app_product_integration t1\n" +
                "\t\t  union all\n" +
                "\t\t  select   t2.uuid             as uuid,\n" +
                "                   t2.data_uuid        as data_uuid,\n" +
                "                   t2.data_name        as name,\n" +
                "                   t2.data_id          as id,\n" +
                "                   t2.data_type        as data_type,\n" +
                "                   t2.data_path        as path,\n" +
                "                   t2.parent_uuid      as parent_uuid,\n" +
                "                   t2.app_product_uuid as app_product_uuid,\n" +
                "                   t2.app_system_uuid  as app_system_uuid,\n" +
                "                   t2.app_page_uuid    as page_uuid\n" +
                "\t\t\t  from (select CONCAT_WS('_', 'page' , t1.app_pi_uuid , t1.uuid) as uuid,\n" +
                "\t\t\t               t1.code as code,\n" +
                "\t\t\t               t1.create_time as create_time,\n" +
                "\t\t\t               t1.uuid as data_uuid,\n" +
                "\t\t\t               t1.id as data_id,\n" +
                "\t\t\t               t1.name as data_name,\n" +
                "\t\t\t               CONCAT_WS('/', t2.data_path , t1.id) as data_path,\n" +
                "\t\t\t               '4' as data_type,\n" +
                "\t\t\t               t2.uuid as parent_uuid,\n" +
                "\t\t\t               t2.app_product_uuid as app_product_uuid,\n" +
                "\t\t\t               t2.app_system_uuid as app_system_uuid,\n" +
                "\t\t\t               t1.uuid as app_page_uuid\n" +
                "\t\t\t          from app_page_definition t1\n" +
                "\t\t\t         inner join app_product_integration t2\n" +
                "\t\t\t            on t1.app_pi_uuid = t2.uuid\n" +
                "\t\t\t        union all\n" +
                "\t\t\t        select CONCAT_WS('_', 'pageref' , t3.app_pi_uuid , t1.uuid) as uuid,\n" +
                "\t\t\t\t\t       t1.code as code,\n" +
                "\t\t\t\t\t       t1.create_time as create_time,\n" +
                "\t\t\t\t\t       t1.uuid as data_uuid,\n" +
                "\t\t\t\t\t       t1.id as data_id,\n" +
                "\t\t\t\t\t       t1.name as data_name,\n" +
                "\t\t\t\t\t       CONCAT_WS('/',t2.data_path , t1.id) as data_path,\n" +
                "\t\t\t\t\t       '4' as data_type,\n" +
                "\t\t\t\t\t       t2.uuid as parent_uuid,\n" +
                "\t\t\t\t\t       t2.app_product_uuid as app_product_uuid,\n" +
                "\t\t\t\t\t       t2.app_system_uuid as app_system_uuid,\n" +
                "\t\t\t\t\t       t1.uuid as app_page_uuid\n" +
                "\t\t\t\t\t  from app_page_definition t1\n" +
                "\t\t\t\t\t inner join app_page_definition_ref t3\n" +
                "\t\t\t\t\t    on t1.uuid = t3.ref_uuid\n" +
                "\t\t\t\t\t inner join app_product_integration t2\n" +
                "\t\t\t\t\t    on t3.app_pi_uuid = t2.uuid) t2\n" +
                "\t      union all\n" +
                "\t      select   t3.uuid             as uuid,\n" +
                "                   t3.data_uuid        as data_uuid,\n" +
                "                   t3.data_name        as name,\n" +
                "                   t3.data_id          as id,\n" +
                "                   t3.data_type        as data_type,\n" +
                "                   t3.data_path        as path,\n" +
                "                   t3.parent_uuid      as parent_uuid,\n" +
                "                   t3.app_product_uuid as app_product_uuid,\n" +
                "                   t3.app_system_uuid  as app_system_uuid,\n" +
                "                   t3.app_page_uuid    as page_uuid\n" +
                "          from (\n" +
                "\t           select CONCAT_WS('_', 'funcref_page' , t1.app_pi_uuid ,   t1.app_page_uuid ,\n" +
                "\t\t               t1.app_function_uuid) as uuid,\n" +
                "\t\t               t1.app_function_uuid as data_uuid,\n" +
                "\t\t               t4.name as data_name,\n" +
                "\t\t               t4.id as data_id,\n" +
                "\t\t               '4' as data_type,\n" +
                "\t\t               CONCAT_WS('/', t2.data_path ,  t3.id ,  t4.id) as data_path,\n" +
                "\t\t               CONCAT_WS('_', 'page' , t1.app_pi_uuid ,  t1.app_page_uuid) as parent_uuid,\n" +
                "\t\t               t2.app_product_uuid as app_product_uuid,\n" +
                "\t\t               t2.app_system_uuid as app_system_uuid,\n" +
                "\t\t               t2.app_page_uuid as app_page_uuid\n" +
                "\t\t          from app_page_resource t1\n" +
                "\t\t         inner join app_product_integration t2\n" +
                "\t\t            on t1.app_pi_uuid = t2.uuid\n" +
                "\t\t         inner join app_page_definition t3\n" +
                "\t\t            on t1.app_page_uuid = t3.uuid\n" +
                "\t\t         inner join app_function t4\n" +
                "\t\t            on t1.app_function_uuid = t4.uuid\n" +
                "\t\t        union all\n" +
                "\t\t        select CONCAT_WS('_', 'funcref_pageref' , t5.app_pi_uuid ,  t1.app_page_uuid ,\n" +
                "\t\t\t\t       t1.app_function_uuid) as uuid,\n" +
                "\t\t\t\t       t1.app_function_uuid as data_uuid,\n" +
                "\t\t\t\t       t4.name as data_name,\n" +
                "\t\t\t\t       t4.id as data_id,\n" +
                "\t\t\t\t       '4' as data_type,\n" +
                "\t\t\t\t       CONCAT_WS('/', t2.data_path ,  t3.id , t4.id) as data_path,\n" +
                "\t\t\t\t       CONCAT_WS('_', 'pageref' , t5.app_pi_uuid , t1.app_page_uuid) as parent_uuid,\n" +
                "\t\t\t\t       t2.app_product_uuid as app_product_uuid,\n" +
                "\t\t\t\t       t2.app_system_uuid as app_system_uuid,\n" +
                "\t\t\t\t       t2.app_page_uuid as app_page_uuid\n" +
                "\t\t\t\t  from app_page_resource t1\n" +
                "\t\t\t\t inner join app_page_definition t3\n" +
                "\t\t\t\t    on t1.app_page_uuid = t3.uuid\n" +
                "\t\t\t\t inner join app_function t4\n" +
                "\t\t\t\t    on t1.app_function_uuid = t4.uuid\n" +
                "\t\t\t\t inner join app_page_definition_ref t5\n" +
                "\t\t\t\t    on t3.uuid = t5.ref_uuid\n" +
                "\t\t\t\t inner join app_product_integration t2\n" +
                "\t\t\t\t    on t5.app_pi_uuid = t2.uuid\n" +
                "\t      ) t3";


//        sql = "insert into  t_user values (1 ,2)";
//        sql = "update uf_ok set ok = 1";
//        sql = "delete from t_user where 1=1";
//        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, DbType.oracle, SQLParserFeature.EnableMultiUnion);
//        sqlStatement.accept(new TenantOracleASTVisitorAdapter("USR", "001", new TablePrefixMathASTTableSource("app_")));
//        System.out.println(" ========================== ");
//        System.out.println(sqlStatement.toString());

//        sql = "select * from app_function a left join app_prod p on a.id = p.code where p.xxx = a.yyy";
//        sql = "delete from  app_function  where  code  =1 ";
        sql = "insert into  app_user values (1 ,2)";
//        sql = "update app_ok set ok = 1";
        SQLStatement sqlStatement2 = SQLUtils.parseSingleStatement(sql, DbType.mysql, SQLParserFeature.EnableMultiUnion);
        sqlStatement2.accept(new TenantMysqlASTVisitorAdapter("USR", "001", new TablePrefixMathASTTableSource("app_")));

        System.out.println(" ========================== ");
        System.out.println(sqlStatement2.toString());
    }
}
