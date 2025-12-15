package com.wellsoft.pt.multi.org.support.utils;

import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author yt
 * @title: ConvertTreeNode
 * @date 2020/6/9 6:24 下午
 */
public class ConvertOrgNode {

    public static OrgNode convert(MultiOrgVersion multiOrgVersion) {
        OrgNode treeNode = new OrgNode();
        treeNode.setId(multiOrgVersion.getId());
        treeNode.setName(multiOrgVersion.getFullName());
        treeNode.setType(IdPrefix.ORG_VERSION.getValue());
        treeNode.setIconSkin(IdPrefix.ORG_VERSION.getValue());
        return treeNode;
    }

    public static OrgNode convert(MultiOrgElement element, int total) {
        OrgNode treeNode = new OrgNode();
        setTreeNode(element, total, treeNode);
        return treeNode;
    }

    public static void checked(OrgNode orgNode, List<String> checkedIds, Set<String> halfCheckSet) {
        if (checkedIds == null || checkedIds.size() == 0) {
            return;
        }
        if (checkedIds.contains(orgNode.getId())) {
            orgNode.setChecked(true);
        } else {
            if (halfCheckSet == null || halfCheckSet.size() == 0) {
                return;
            }
            if (halfCheckSet.contains(orgNode.getId())) {
                orgNode.setHalfCheck(true);
            }
        }

    }

    private static void setTreeNode(MultiOrgElement element, int total, OrgNode treeNode) {
        treeNode.setId(element.getId());
        treeNode.setName(element.getName());
        treeNode.setShortName(element.getShortName());
        treeNode.setType(element.getType());
        treeNode.setIconSkin(element.getType());
        treeNode.setIsParent(total > 0 ? true : null);
    }


    public static void setParent(OrgElementVo elementVo, String eleId) {
        if (eleId.equals(elementVo.getId())) {
            return;
        }
        if (elementVo.getParent() == null) {
            OrgElementVo parent = new OrgElementVo();
            parent.setId(eleId);
            elementVo.setParent(parent);
        } else {
            setParent(elementVo.getParent(), eleId);
        }
    }

    public static void setParentName(OrgElementVo orgElementVo, Map<String, MultiOrgElement> orgElementMap) {
        MultiOrgElement orgElement = orgElementMap.get(orgElementVo.getId());
        if (orgElement != null) {
            orgElementVo.setName(orgElement.getName());
            if (orgElementVo.getParent() != null) {
                setParentName(orgElementVo.getParent(), orgElementMap);
            }
        }
    }

    public static OrgNode convert(UserNode userNode) {
        userNode = setUserNode(userNode);
        OrgNode treeNode = new OrgNode();
        setTreeNod(userNode, treeNode);
        return treeNode;
    }

    private static void setTreeNod(UserNode userNode, OrgNode treeNode) {
        treeNode.setId(userNode.getId());
        treeNode.setName(userNode.getName());
        treeNode.setType(IdPrefix.USER.getValue());
        treeNode.setIconSkin(userNode.getIconSkin());
    }

    public static UserNode setUserNode(UserNode userNode) {
        if ("1".equals(userNode.getIconSkin())) {
            userNode.setIconSkin("man");
        } else if ("0".equals(userNode.getIconSkin())) {
            userNode.setIconSkin("women");
        } else {
            userNode.setIconSkin(IdPrefix.USER.getValue());
        }
        return userNode;
    }

    public static void containsIndexOf(Set<String> set, String str) {
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String setStr = iterator.next();
            if (str.contains(setStr)) {
                iterator.remove();
            }
        }
        set.add(str);
    }

    public static void containsMinIndexOf(Set<String> set, String str) {
        set.add(str);
        for (String s : set) {
            if (str.length() > s.length() && str.contains(s)) {
                set.remove(str);
                break;
            }
        }
    }


    public static OrgNode convert(OrgNodeUserDto userDto) {
        OrgNode orgNode = new OrgNode();
        orgNode.setId(userDto.getId());
        orgNode.setType(IdPrefix.USER.getValue());
        orgNode.setName(userDto.getName());
        if ("1".equals(userDto.getSex())) {
            orgNode.setIconSkin("man");
        } else if ("0".equals(userDto.getSex())) {
            orgNode.setIconSkin("women");
        } else {
            orgNode.setIconSkin(IdPrefix.USER.getValue());
        }
        return orgNode;
    }

    public static OrgNode convert(OrgNodeDto obj) {
        OrgNode orgNode = new OrgNode();
        orgNode.setId(obj.getId());
        orgNode.setType(obj.getType());
        orgNode.setName(obj.getName());
        orgNode.setShortName(obj.getShortName());
        orgNode.setIconSkin(obj.getType());
        return orgNode;
    }

    public static void addChildren(OrgNode orgNode, Map<String, OrgNodeDto> nodeDtoMap, Map<String, OrgNode> orgNodeMap, String[] eleIdPaths, int i, List<String> checkedIds) {
        if (i >= eleIdPaths.length) {
            //结束递归
            return;
        }
        String eleId = eleIdPaths[i];
        //处理过 该节点 累加i 处理下一个
        if (orgNodeMap.containsKey(eleId)) {
            i++;
            //以 eleId 为上级节点 递归处理子节点
            addChildren(orgNodeMap.get(eleId), nodeDtoMap, orgNodeMap, eleIdPaths, i, checkedIds);
            return;
        }
        OrgNodeDto orgNodeDto = nodeDtoMap.get(eleId);
        TreeMap<String, List<OrgNode>> treeMap = orgNode.getTreeMap();
        if (treeMap == null) {
            treeMap = new TreeMap<>();
            orgNode.setTreeMap(treeMap);
        }
        String code = orgNodeDto.getCode();
        if (code == null) {
            code = StringUtils.EMPTY;
        }
        List<OrgNode> orgNodes = treeMap.get(code);
        if (orgNodes == null) {
            orgNodes = new ArrayList<>();
            //以code 排序
            treeMap.put(code, orgNodes);
        }
        //转换
        OrgNode children = ConvertOrgNode.convert(orgNodeDto);
        //在list里 不存在则添加
        if (!orgNodes.contains(children)) {
            orgNodes.add(children);
        }
        if (checkedIds != null && checkedIds.contains(children.getId())) {
            children.setChecked(true);
        }
        //保存处理记录
        orgNodeMap.put(eleId, children);
        i++;
        addChildren(children, nodeDtoMap, orgNodeMap, eleIdPaths, i, checkedIds);
    }

    public static OrgNode convert(TreeNode treeNode) {
        OrgNode orgNode = new OrgNode();
        orgNode.setId(treeNode.getId());
        orgNode.setType(treeNode.getType());
        orgNode.setName(treeNode.getName());
        orgNode.setIconSkin(treeNode.getType());
        return orgNode;
    }

    public static OrgNode convert(OrgJobDutyDto orgJobDutyDto) {
        if (null == orgJobDutyDto) {
            return null;
        }
        return convertToOrgNode(orgJobDutyDto);
    }

    public static List<OrgNode> convert2OrgJobDutyDtoList(List<OrgJobDutyDto> orgJobDutyDtoList) {
        if (CollectionUtils.isEmpty(orgJobDutyDtoList)) {
            return null;
        }
        List<OrgNode> orgNodeList = new ArrayList<>();
        for (OrgJobDutyDto orgJobDutyDto : orgJobDutyDtoList) {
            orgNodeList.add(convertToOrgNode(orgJobDutyDto));
        }
        return orgNodeList;
    }

    private static OrgNode convertToOrgNode(OrgJobDutyDto orgJobDutyDto) {
        OrgNode orgNode = new OrgNode();
        orgNode.setId(orgJobDutyDto.getEleId());
        orgNode.setType(orgJobDutyDto.getType());
        orgNode.setName(orgJobDutyDto.getName());
        if ("man".equals(orgJobDutyDto.getRemark()) || "women".equals(orgJobDutyDto.getRemark())) {
            orgNode.setIconSkin(orgJobDutyDto.getRemark());
        } else {
            orgNode.setIconSkin(orgJobDutyDto.getType());
        }
        return orgNode;
    }

    public static OrgNode convert(OrgNodeQueryItemDto orgNodeQueryItemDto) {
        OrgNode orgNode = new OrgNode();
        orgNode.setId(orgNodeQueryItemDto.getId());
        orgNode.setType("0".equals(orgNodeQueryItemDto.getType()) ? IdPrefix.GROUP.getValue() : orgNodeQueryItemDto.getType());
        orgNode.setName(orgNodeQueryItemDto.getName());
        orgNode.setIconSkin("0".equals(orgNodeQueryItemDto.getType()) ? IdPrefix.GROUP.getValue() : orgNodeQueryItemDto.getType());
        orgNode.setIsParent(null != orgNodeQueryItemDto.getChildrenCount() && orgNodeQueryItemDto.getChildrenCount() != 0 ? true : false);
        return orgNode;
    }

    public static OrgPathVo convert(OrgElementVo elementVo) {
        if (elementVo == null) {
            return null;
        }
        OrgPathVo orgPathVo = new OrgPathVo();
        List<String> idList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        addList(idList, nameList, elementVo);
        orgPathVo.setIdPath(StringUtils.join(idList, MultiOrgService.PATH_SPLIT_SYSMBOL));
        orgPathVo.setNamePath(StringUtils.join(nameList, MultiOrgService.PATH_SPLIT_SYSMBOL));
        return orgPathVo;
    }

    private static void addList(List<String> idList, List<String> nameList, OrgElementVo elementVo) {
        if (elementVo.getParent() != null) {
            addList(idList, nameList, elementVo.getParent());
        }
        idList.add(elementVo.getId());
        nameList.add(elementVo.getName());
    }

    public static UserNodePy convertUserNodePy(List<UserNode> userNodes) {
        UserNodePy userNodePy = new UserNodePy();
        if (userNodes == null || userNodes.size() == 0) {
            return userNodePy;
        }
        final TreeSet<String> pySet = new TreeSet<>();
        Collections.sort(userNodes, new Comparator<UserNode>() {
            @Override
            public int compare(UserNode o1, UserNode o2) {
                if (org.apache.commons.lang.StringUtils.isBlank(o1.getNamePy())) {
                    return -1;
                }
                String a = o1.getNamePy().substring(0, 1).toUpperCase();
                if (!Character.isUpperCase(a.charAt(0))) {
                    a = "a#";
                }
                if (org.apache.commons.lang.StringUtils.isBlank(o2.getNamePy())) {
                    return 1;
                }
                String b = o2.getNamePy().substring(0, 1).toUpperCase();
                if (!Character.isUpperCase(b.charAt(0))) {
                    b = "a#";
                }
                pySet.add(a);
                pySet.add(b);
                return a.compareTo(b);
            }
        });
        List<String> py = new ArrayList<>(pySet);
        if (py.size() > 0) {
            if (py.get(py.size() - 1).equals("a#")) {
                py.set(py.size() - 1, "#");
            }
        }
        userNodePy.setPy(py);
        userNodePy.setNodes(userNodes);
        return userNodePy;
    }
}
