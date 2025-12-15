package com.wellsoft.pt.integration.ftp;

public class FileUtil {

    public static String replaceEx(String str, String subStr, String reStr) {
        if (str == null)
            return null;
        if (subStr == null || subStr.equals("") || subStr.length() > str.length() || reStr == null)
            return str;
        StringBuffer sb = new StringBuffer();
        int lastIndex = 0;
        do {
            int index = str.indexOf(subStr, lastIndex);
            if (index >= 0) {
                sb.append(str.substring(lastIndex, index));
                sb.append(reStr);
                lastIndex = index + subStr.length();
            } else {
                sb.append(str.substring(lastIndex));
                return sb.toString();
            }
        } while (true);
    }

    public static String normalizePath(String path) {
        path = path.replace('\\', '/');
        path = replaceEx(path, "../", "/");
        path = replaceEx(path, "./", "/");
        if (path.endsWith("..")) {
            path = path.substring(0, path.length() - 2);
        }
        path = path.replaceAll("/+", "/");
        return path;
    }
}
