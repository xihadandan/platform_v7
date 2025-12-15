import com.wellpt.code.builder.utils.CodeGeneratorUtils;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/1
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/1    chenq		2018/8/1		Create
 * </pre>
 */
public class Main {

    public static void main(String[] arrs) {
        try {
            CodeGeneratorUtils.generateEntityCode();
            CodeGeneratorUtils.generateServiceCode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
