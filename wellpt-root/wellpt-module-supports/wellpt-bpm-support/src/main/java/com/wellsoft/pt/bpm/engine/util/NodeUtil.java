package com.wellsoft.pt.bpm.engine.util;

import com.wellsoft.pt.bpm.engine.core.FlowDefConstants;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author lilin
 * @ClassName: NodeUtil
 * @Description: 解析流程定义xml处理的帮助类
 */
public class NodeUtil {

    public static int getIntFromBool(boolean bool) {
        if (bool) {
            return FlowDefConstants.BOOL_TRUE;
        } else {
            return FlowDefConstants.BOOL_FALSE;
        }
    }

    public static boolean getBoolFromInt(int value) {
        if (value == FlowDefConstants.BOOL_TRUE) {
            return true;
        } else {
            return false;
        }
    }

    public static String getString(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    public static String getStringFromInt(Integer i) {
        if (i == null) {
            return "";
        }
        return String.valueOf(i);
    }

    public static String getStringFromLong(Long i) {
        if (i == null) {
            return "";
        }
        return String.valueOf(i);
    }

    public static String getStringFromFloat(Float i) {
        if (i == null) {
            return "";
        }
        return String.valueOf(i);
    }

    public static String getStringFromBool(Boolean bool) {
        if (bool == null) {
            return "";
        }
        if (bool) {
            return String.valueOf(FlowDefConstants.BOOL_TRUE);
        } else {
            return String.valueOf(FlowDefConstants.BOOL_FALSE);
        }
    }

    /**
     * 获取element的nodeName属性值
     *
     * @param element
     * @param nodeName
     * @return String
     */
    public static String getNodeAttrValue(Element element, String nodeName) {
        if (element == null) {
            return null;
        }
        String value = element.attributeValue(nodeName);
        return value;
    }

    public static Integer getNodeAttrIntValue(Element element, String nodeName) {
        if (element == null) {
            return null;
        }
        String value = element.attributeValue(nodeName);
        if (StringUtils.hasText(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;

    }

    //	public static Boolean getNodeAttrBoolValue(Element element, String nodeName) {
    //		if (element == null) {
    //			return null;
    //		}
    //		String value = element.attributeValue(nodeName);
    //		if ("false".equalsIgnoreCase(value)) {
    //			return false;
    //		} else if ("true".equalsIgnoreCase(value)) {
    //			return true;
    //		} else if ("1".equals(value)) {
    //			return true;
    //		} else if ("0".equals(value)) {
    //			return false;
    //		}
    //		return null;
    //	}

    /**
     * 获取element的path节点的值，返回String类型值
     *
     * @param element
     * @param path
     * @return String
     */
    public static String getNodeStringValue(Element element, String path) {
        if (element == null) {
            return null;
        }
        Node node = element.selectSingleNode(path);
        if (node != null) {
            return node.getText();
        }
        return null;

    }

    /**
     * 获取element的path节点的值，修改为text
     *
     * @param element
     * @param path
     * @param text
     * @return
     * @author wujx createtime 2015-12-18下午7:14:00
     */
    public static void setNodeStringValue(Element element, String path, String text) {
        if (element != null) {
            Node node = element.selectSingleNode(path);
            if (node != null) {
                node.setText(text);
            }
        }
    }

    /**
     * 获取element的path节点的值，返回Boolean类型值
     *
     * @param element
     * @param path
     * @return
     */
    //	public static Boolean getNodeBooleanValue(Element element, String path) {
    //		if (element == null) {
    //			return null;
    //		}
    //		Node node = element.selectSingleNode(path);
    //		if (node != null) {
    //			String value = node.getText();
    //			if ("false".equalsIgnoreCase(value)) {
    //				return false;
    //			} else if ("true".equalsIgnoreCase(value)) {
    //				return true;
    //			} else if ("1".equals(value)) {
    //				return true;
    //			} else if ("0".equals(value)) {
    //				return false;
    //			}
    //		}
    //		return null;
    //
    //	}

    /**
     * 获取element的path节点的值，返回int类型值
     *
     * @param element
     * @param path
     * @return
     */
    public static Integer getNodeIntValue(Element element, String path) {
        if (element == null) {
            return null;
        }
        Node node = element.selectSingleNode(path);
        if (node != null) {
            if (StringUtils.hasText(node.getText())) {
                return Integer.parseInt(node.getText());
            } else {
                return 0;
            }
        } else {
            return null;
        }
    }

    /**
     * 获取element的path节点的值，返回long类型值
     *
     * @param element
     * @param path
     * @return
     */
    public static Long getNodeLongValue(Element element, String path) {
        if (element == null) {
            return null;
        }
        Node node = element.selectSingleNode(path);
        if (node != null) {
            if (StringUtils.hasText(node.getText())) {
                return Long.parseLong(node.getText());
            } else {
                return null;
            }
        }
        return null;
    }

    public static Float getNodeFloatValue(Element element, String path) {
        if (element == null) {
            return null;
        }
        Node node = element.selectSingleNode(path);
        if (node != null) {
            if ("".equals(node.getText())) {
                return null;
            } else {
                return Float.parseFloat(node.getText());
            }
        } else {
            return null;
        }
    }

    /**
     * 获取element的path节点的值，返回double类型值
     *
     * @param element
     * @param path
     * @return
     */
    public static Double getNodeDoubleValue(Element element, String path) {
        if (element == null) {
            return null;
        }
        Node node = element.selectSingleNode(path);
        if (node != null) {
            if ("".equals(node.getText()))
                return null;
            else
                return Double.parseDouble(node.getText());
        } else
            return null;
    }

    /**
     * 获取环节或流程扩展属性
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, String> getExtProperties(Element el) {
        Map<String, String> map = new HashMap<String, String>();

        List list = el.selectNodes(FlowDefConstants.FLOW_EXT_PROPERTY);
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Element e = (Element) iter.next();
            map.put(e.attributeValue("key"), e.attributeValue("value"));
        }
        return map;
    }

    //--------------------------------------------------------------------------------

    /**
     * 获取位置left
     *
     * @param element
     * @param path
     * @return
     */
    public static String getElementLeft(Element tranEl) {
        Node position = tranEl.selectSingleNode("position");
        return ((Element) position).attributeValue("left");
    }

    /**
     * 获取位置top
     *
     * @param element
     * @return
     */
    public static String getElementTop(Element element) {
        Node position = element.selectSingleNode("position");
        return ((Element) position).attributeValue("top");
    }

    /**
     * 获取位置point
     *
     * @param element
     * @return
     */
    public static String getElementPoint(Element element) {
        Node position = element.selectSingleNode("position");
        return ((Element) position).attributeValue("point");
    }
}
