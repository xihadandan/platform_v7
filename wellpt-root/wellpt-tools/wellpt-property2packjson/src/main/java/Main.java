import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String JS_MODULE_PATTERN = ".js.module.*?(?=\\.id)";
    private static final String TPT_MODULE_PATTERN = ".js.template.*?(?=\\.id)";
    private static final String JS_MODULE_FLAG = ".js.module.";
    private static final String TPT_MODULE_FLAG = ".js.template.";
    private static final Map<String, Object> CONFIG = Maps.newHashMap();

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Collection<File> files = FileUtils.listFiles(new File("./properties"), new String[]{"properties"},
                false);
        try {
            FileUtils.forceDelete(new File("./json"));
        } catch (Exception e) {
        }
        FileUtils.forceMkdir(new File("./json"));
        System.out.println(Main.class.getResource("config.json").getFile());
        String[] configFilePaths = new String[]{Main.class.getResource("config.json").getFile(), "./config.json"};
        for (String fp : configFilePaths) {
            String str = null;
            if (fp.indexOf(".jar!") != -1) {
                str = IOUtils.toString(Main.class.getClassLoader().getResourceAsStream("config.json"));
            } else {
                File configJSONFile = new File(fp);
                if (configJSONFile.exists()) {
                    FileInputStream in = new FileInputStream(configJSONFile);
                    str = IOUtils.toString(in);
                    IOUtils.closeQuietly(in);
                }
            }
            if (str != null)
                CONFIG.putAll(new Gson().fromJson(str, Map.class));
        }
        System.out.println("配置：");
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(CONFIG));
        for (File f : files) {
            System.out.println("读取properties文件：" + f.getName());
            Properties properties = new Properties();
            properties.load(new FileInputStream(f));
            Set<Object> keys = properties.keySet();
            Map<String, Map<String, Object>> jsModuleMap = Maps.newHashMap();
            Map<String, Map<String, Object>> cssMap = Maps.newHashMap();
            Map<String, Map<String, Object>> templateMap = Maps.newHashMap();
            for (Object k : keys) {
                if (k.toString().startsWith("pt.js.module.") || k.toString().startsWith("app.js.module.")) {
                    String jsModule = getJsModule(k.toString());// StringUtils.trim(keyArray[3]);
                    if (StringUtils.isBlank(jsModule)) {
                        continue;
                    }
                    Map<String, Object> detail = Maps.newHashMap();
                    String id = StringUtils.defaultIfBlank(properties.getProperty("pt.js.module." + jsModule + ".id"), properties.getProperty("app.js.module." + jsModule + ".id"));
                    String name = StringUtils.defaultIfBlank(properties.getProperty("pt.js.module." + jsModule + ".name"), properties.getProperty("app.js.module." + jsModule + ".name"));
                    String isAbs = StringUtils.defaultIfBlank(properties.getProperty("pt.js.module." + jsModule + ".abstract"), properties.getProperty("app.js.module." + jsModule + ".abstract"));
                    String exports = StringUtils.defaultIfBlank(properties.getProperty("pt.js.module." + jsModule + ".exports"), properties.getProperty("app.js.module." + jsModule + ".exports"));

                    if (!"true".equals(isAbs)) {
                        detail.put("path", resolvePath(StringUtils.defaultIfBlank(properties.getProperty("pt.js.module." + jsModule + ".path"), properties.getProperty("app.js.module." + jsModule + ".path"))));
                    } else {
                        detail.put("abstract", true);
                    }
                    String parent = StringUtils.defaultIfBlank(properties.getProperty("pt.js.module." + jsModule + ".parent"), properties.getProperty("app.js.module." + jsModule + ".parent"));
                    String deps = StringUtils.defaultIfBlank(properties.getProperty("pt.js.module." + jsModule + ".dependencies"), properties.getProperty("app.js.module." + jsModule + ".dependencies"));
                    deps = (parent != null ? parent : "") + (parent != null && deps != null ? "," : "")
                            + (deps != null ? deps : "");
                    if (StringUtils.isNotBlank(deps)) {
                        detail.put("dependencies", deps.split(","));
                    }
                    detail.put("name", StringUtils.isNotBlank(name) ? name : id);
                    if (StringUtils.isNotBlank(exports)) {
                        detail.put("alias", exports);
                    }
                    jsModuleMap.put(id, detail);
                } else if (k.toString().startsWith("pt.css.") || k.toString().startsWith("app.css.")) {
                    String[] keyArray = k.toString().split("\\.");
                    String css = StringUtils.trim(keyArray[2]);
                    String cssPrefix = keyArray[0] + "." + keyArray[1] + ".";
                    String idKey = cssPrefix + css + ".id";
                    String nameKey = cssPrefix + css + ".name";
                    String pathKey = cssPrefix + css + ".path";
                    String orderKey = cssPrefix + css + ".order";
                    String id = properties.getProperty(idKey);
                    String name = properties.getProperty(nameKey);
                    String path = properties.getProperty(pathKey);
                    String order = properties.getProperty(orderKey);
                    Map<String, Object> detail = Maps.newHashMap();
                    detail.put("name", name);
                    detail.put("path", resolvePath(path));
                    if (StringUtils.isNotBlank(order)) {
                        detail.put("order", Integer.parseInt(order));
                    }
                    cssMap.put(id, detail);
                } else if (k.toString().startsWith("pt.js.template.") || k.toString().startsWith("app.js.template.")) {
                    String templateModule = getTemplate(k.toString());// StringUtils.trim(keyArray[3]);
                    if (StringUtils.isBlank(templateModule)) {
                        continue;
                    }
                    Map<String, Object> detail = Maps.newHashMap();
                    String id = StringUtils.defaultIfBlank(properties.getProperty("pt.js.template." + templateModule + ".id"), properties.getProperty("app.js.template." + templateModule + ".id"));
                    String name = StringUtils.defaultIfBlank(properties.getProperty("pt.js.template." + templateModule + ".name"), properties.getProperty("app.js.template." + templateModule + ".name"));
                    String path = StringUtils.defaultIfBlank(properties.getProperty("pt.js.template." + templateModule + ".source"), properties.getProperty("app.js.template." + templateModule + ".source"));
                    detail.put("path", resolvePath(path));
                    detail.put("name", name);
                    templateMap.put(id, detail);
                }
            }
            String filename = f.getName().replaceFirst("pt-", "").replaceFirst("properties",
                    "resource.json");
            if (CONFIG.containsKey(f.getName())) { // 文件名转换映射
                filename = CONFIG.get(f.getName()).toString();
                if (!filename.endsWith(".resource.json")) {
                    filename += ".resource.json";
                }
            }

            FileOutputStream out = new FileOutputStream(
                    new File("./json/" + filename));
            Map<String, Object> data = Maps.newHashMap();
            if (!jsModuleMap.isEmpty()) {
                data.put("js", jsModuleMap);
            }
            if (!cssMap.isEmpty()) {
                data.put("css", cssMap);
            }
            if (!templateMap.isEmpty()) {
                data.put("jsTemplate", templateMap);
            }
            if (!data.isEmpty()) {
                GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
                String json = builder.create().toJson(data);
                IOUtils.write(json, out, "UTF-8");
                IOUtils.closeQuietly(out);
            }
        }
    }

    static Map<String, Pattern> patternMap = Maps.newHashMap();


    private static String resolvePath(String path) {
        Set<String> paths = CONFIG.keySet();
        for (String p : paths) {
            if (path.startsWith(p)) {
                if (p.endsWith("/")) {
                    String str = CONFIG.get(p).toString();
                    if (!str.endsWith("/")) {
                        CONFIG.put(p, str + "/");
                    }
                }
                path = path.replaceFirst(p, CONFIG.get(p).toString());
                return path;
            }
        }
        if (path.startsWith("/resources")) {
            path = path.replaceFirst("/resources", "");
        }


        if (path.startsWith("/pt/js/app/")) {
            return path.replaceFirst("/pt/js/app/", "/cms/");
        }

        if (path.startsWith("/pt/js/dms/")) {
            return path.replaceFirst("/pt/js/dms/", "/dms/");
        }


        return path;
    }

    private static String getJsModule(String expression) {
        // 使用正则表达式的最小匹配、零宽断言
        Pattern pattern = Pattern.compile(JS_MODULE_PATTERN);
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            String group = matcher.group();
            return group.substring(JS_MODULE_FLAG.length());
        }
        return null;
    }

    private static String getTemplate(String expression) {
        // 使用正则表达式的最小匹配、零宽断言
        Pattern pattern = Pattern.compile(TPT_MODULE_PATTERN);
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            String group = matcher.group();
            return group.substring(TPT_MODULE_FLAG.length());
        }
        return null;
    }

}
