import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.wellsoft.context.annotation.Description;
import com.wellsoft.context.config.service.SystemParamsFacadeService;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/27
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/27    chenq		2019/6/27		Create
 * </pre>
 */
public class Test {

    public static void main(String[] arrs) {

        try {
            //PropertiesLoaderUtils.loadAllProperties("class")
            String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + "/META-INF/com/wellsoft/**/description.properties";
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(
                    searchPath);
            Map<Object, Object> all = Maps.newHashMap();
            for (Resource resource : resources) {
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                System.out.println(properties.values());
                System.out.println(properties.keySet());
                all.putAll(properties);
            }
            // 使用类加载器加载类
            Class c = SystemParamsFacadeService.class;
            System.out.println(c.getCanonicalName());
            System.out.println(all.get(c.getCanonicalName()));

            System.out.println(c.getSimpleName());
            Method[] methods = c.getMethods();
            //按同名方法分组
            ImmutableListMultimap<String, Method> methodMap = Multimaps.index(
                    Lists.newArrayList(methods), new Function<Method, String>() {
                        @Nullable
                        @Override
                        public String apply(@Nullable Method method) {
                            return method.getName();
                        }
                    });

            Map<String, Collection<Method>> groupMap = methodMap.asMap();

            Set<String> methodKeys = groupMap.keySet();
            for (String m : methodKeys) {
                List<Method> methodCollection = Lists.newArrayList(groupMap.get(m));
                if (methodCollection.size() > 1) {
                    for (int i = 1; i <= methodCollection.size(); i++) {
                        Method method = methodCollection.get(i - 1);
                        String descriptText = "";
                        Object propText = all.get(
                                c.getCanonicalName() + "." + m + "#" + i);
                        if (propText != null) {
                            descriptText = propText.toString();
                        }
                        boolean hasDecription = method.isAnnotationPresent(
                                Description.class);
                        if (hasDecription) {
                            Description annotation = (Description) AnnotationUtils.getAnnotation(
                                    method, Description.class);
                            descriptText = annotation.value();
                        }


                        System.out.println(
                                methodCollection.get(
                                        i - 1).toGenericString() + " : " + descriptText);
                    }
                    continue;
                }
                System.out.println(all.get(m));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
