package com.wellsoft.context.util.groovy;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Description: groovy 工具类，提供groovy脚本调用方法
 *
 * @author chenq
 * @date 2018/9/6
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/6    chenq		2018/9/6		Create
 * </pre>
 */
public class GroovyUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyUtils.class);

    private static final String IMPORT = "import ";

    private final static Set<String> COMMON_USE_CLASS_SET = Sets.newLinkedHashSet();//平台常用类引入

    private final static Set<Class<?>> THIRD_CLASSES = Sets.newHashSet(); //第三方工具类引入

    private static Map<String, ScriptItem> scriptItemMap = Maps.newConcurrentMap();
    private static Queue<ScriptItem> scriptItemQueue = new PriorityBlockingQueue<>();

    static {
        THIRD_CLASSES.add(DateUtils.class);
        THIRD_CLASSES.add(StringUtils.class);
        THIRD_CLASSES.add(DateFormatUtils.class);
        THIRD_CLASSES.add(IOUtils.class);
        THIRD_CLASSES.add(CollectionUtils.class);
        THIRD_CLASSES.add(ListUtils.class);
        THIRD_CLASSES.add(MapUtils.class);
        THIRD_CLASSES.add(SetUtils.class);
        THIRD_CLASSES.add(BeanUtils.class);
        THIRD_CLASSES.add(Gson.class);
        THIRD_CLASSES.add(Sets.class);
        THIRD_CLASSES.add(Lists.class);
        THIRD_CLASSES.add(Maps.class);
    }


    /**
     * 处理groovy默认导入的以下类包：
     * java.io.*
     * java.lang.*
     * java.math.BigDecimal
     * java.math.BigInteger
     * java.net.*
     * java.util.*
     * groovy.lang.*
     * groovy.util.*
     * <p>
     * 把常用的工具类包也导进去，以及平台级服务类
     */
    public synchronized static void loadCommonUseClass() {
        if (CollectionUtils.isEmpty(COMMON_USE_CLASS_SET)) {
            //工具类
            for (Class<?> clazz : THIRD_CLASSES) {
                COMMON_USE_CLASS_SET.add(clazz.getCanonicalName());//日期工具类
            }

            LOGGER.info("开始加载GroovyUseable注解的平台类");
            Stopwatch stopwatch = Stopwatch.createStarted();
            //加载注解ImportGroovy的平台类到groovy的执行环境
            Reflections reflections = new Reflections("com.wellsoft");
            Set<Class<?>> classesList = reflections.getTypesAnnotatedWith(GroovyUseable.class,
                    true);
            if (CollectionUtils.isNotEmpty(classesList)) {
                for (Class<?> clazz : classesList) {
                    String name = clazz.getCanonicalName();
                    LOGGER.info("GroovyUseable注解类{}", name);
                    COMMON_USE_CLASS_SET.add(name);
                }
            }
            LOGGER.info("结束加载GroovyUseable注解的平台类，耗时：{}", stopwatch.stop());
            LOGGER.info("Groovy脚本引入的所有类：{}", COMMON_USE_CLASS_SET);
        }
    }

    public static Set<String> getUseClasses() {
        return COMMON_USE_CLASS_SET;
    }

    public static Object run(String script) {
        return new GroovyShell().parse(script).run();
    }

    public static String groovyImports() {
        StringBuilder importBuilder = new StringBuilder("");
        for (String im : COMMON_USE_CLASS_SET) {
            importBuilder.append(IMPORT + im + ";");
            importBuilder.append(StringUtils.CR);
        }
        importBuilder.append(StringUtils.CR);

        return importBuilder.toString();
    }

    private static String importClass(Collection<Class> importClassList) {
        StringBuilder importStr = new StringBuilder("");
        if (CollectionUtils.isNotEmpty(importClassList)) {
            for (Class c : importClassList) {
                importStr.append(IMPORT + c.getCanonicalName());
                importStr.append(StringUtils.CR);
            }
        }
        importStr.append(groovyImports());
        return importStr.toString();
    }

    /**
     * 获取脚本对象
     *
     * @param script
     * @param importClasses
     * @return
     */
    private static Script getScript(String script, Collection<Class> importClasses) {
        ScriptItem scriptItem = scriptItemMap.get(script);
        if (scriptItem == null) {
            long priority = 0;
            // 超过10个脚本对象，移除最少使用的
            if (scriptItemQueue.size() >= 10) {
                ScriptItem removedItem = scriptItemQueue.poll();
                scriptItemMap.remove(removedItem.getSource());
                priority = scriptItemQueue.peek().getPriority() + 10;
            }

            GroovyShell shell = new GroovyShell();
            String scriptContent = importClass(importClasses) + script;
            Script shellScript = shell.parse(scriptContent);
            scriptItem = new ScriptItem(script, shellScript, priority);
            scriptItemMap.put(script, scriptItem);
            scriptItemQueue.offer(scriptItem);
        }
        // 增加优先级
        scriptItem.plusPriority();

        return scriptItem.getScript();
    }

    /**
     * 执行脚本
     *
     * @param script        脚本
     * @param properties    脚本参数
     * @param importClasses 脚本依赖类
     * @return
     */
    public static Object run(String script, Map<String, Object> properties,
                             Collection<Class> importClasses) {
        Script shellScript = getScript(script, importClasses);
        Binding binding = new Binding();
        if (MapUtils.isNotEmpty(properties)) {
            Set<String> keys = properties.keySet();
            for (String k : keys) {
                binding.setVariable(k, properties.get(k));
            }
        }
        LOGGER.debug("执行groovy脚本，脚本：{}，参数：{}", script, properties);
        return InvokerHelper.createScript(shellScript.getClass(), binding).run();
    }

    /**
     * 执行脚本
     *
     * @param script     脚本
     * @param properties 脚本参数
     * @return
     */
    public static Object run(String script, Map<String, Object> properties) {
//        GroovyShell shell = new GroovyShell();
//        Script shellScript = shell.parse(importClass(null) + script);
        Script shellScript = getScript(script, null);
        Binding binding = new Binding();
        if (MapUtils.isNotEmpty(properties)) {
            Set<String> keys = properties.keySet();
            for (String k : keys) {
                binding.setVariable(k, properties.get(k));
//                shellScript.setProperty(k, properties.get(k));
            }
        }
        LOGGER.debug("执行groovy脚本，脚本：{}，参数：{}", script, properties);
//        return shellScript.run();
        return InvokerHelper.createScript(shellScript.getClass(), binding).run();
    }

    /**
     * 脚本数据项
     */
    private static class ScriptItem implements Comparable<ScriptItem> {

        private String source;

        private Script script;

        private long priority;

        /**
         * @param source
         * @param script
         * @param priority
         */
        public ScriptItem(String source, Script script, long priority) {
            this.source = source;
            this.script = script;
            this.priority = priority;
        }

        /**
         * @return the source
         */
        public String getSource() {
            return source;
        }

        /**
         * @return the script
         */
        public Script getScript() {
            return script;
        }

        /**
         * @return the priority
         */
        public long getPriority() {
            return priority;
        }

        /**
         *
         */
        public void plusPriority() {
            this.priority++;
        }

        @Override
        public int compareTo(ScriptItem item) {
            return Long.valueOf(this.priority).compareTo(item.priority);
        }
    }

}
