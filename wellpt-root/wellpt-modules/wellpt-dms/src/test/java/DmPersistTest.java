import com.wellsoft.pt.dm.factory.ddl.Table;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年04月06日   chenq	 Create
 * </pre>
 */
public class DmPersistTest {
    @Test
    public void test() throws Exception {
        Table table = new Table("testone", "测试表");
        table.addColumn(new Table.Column("id", "number(11)", "id", false, true, null));
        table.addColumn(new Table.Column("code", "number(11)", "编码", true));
        table.addIndex(new Table.Index(new String[]{"code", "id"}));
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);

        cfg.setDirectoryForTemplateLoading(new File("D:\\IdeaProjects\\wellsoft\\wpp6.2\\dev-6.2-vue.Production-0720\\wellpt-root\\wellpt-modules\\wellpt-dms\\target\\classes\\com\\wellsoft\\pt\\dm\\factory\\ddl\\"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        Template temp = cfg.getTemplate("create.sql.ftl");
        StringWriter sw = new StringWriter();
        temp.process(table, sw);
        System.out.println(sw.toString());

    }
}
