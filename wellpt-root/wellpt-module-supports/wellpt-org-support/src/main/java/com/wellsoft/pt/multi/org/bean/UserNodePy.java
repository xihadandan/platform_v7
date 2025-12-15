package com.wellsoft.pt.multi.org.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户节点+拼音首字母
 *
 * @author yt
 * @title: TreeNodePy
 * @date 2020/6/16 4:10 下午
 */
public class UserNodePy implements Serializable {

    private List<String> py = new ArrayList<>();

    private List<UserNode> nodes = new ArrayList<>();

    public List<String> getPy() {
        return py;
    }

    public void setPy(List<String> py) {
        this.py = py;
    }

    public List<UserNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<UserNode> nodes) {
        this.nodes = nodes;
    }
}
