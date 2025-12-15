/*
 * @(#)12/11/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.biz.enums.EnumItemFlowElement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Description: 事项流定义
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/11/23.1	zhulh		12/11/23		Create
 * </pre>
 * @date 12/11/23
 */
public class ItemFlowDefinition extends BaseObject {
    private static final long serialVersionUID = 6320516000060130704L;

    // <节点ID，节点>
    private Map<String, Node> nodeMap = Maps.newHashMap();

    private List<Edge> edges;

    // <事项ID，事项节点>
    private Map<String, ItemNode> itemNodeMap = Maps.newHashMap();

    public ItemFlowDefinition(List<Node> nodes, List<Edge> edges) {
        this.edges = edges;
        initNodes(nodes);
    }

    private void initNodes(List<Node> nodes) {
        nodes.forEach(node -> {
            nodeMap.put(node.getId(), node);

            if (node instanceof ItemNode) {
                ItemNode itemNode = (ItemNode) node;
                itemNodeMap.put(itemNode.getItemId(), itemNode);
            }
        });
    }

    /**
     * 根据ID获取节点
     *
     * @param id
     */
    public Node getNodeById(String id) {
        return nodeMap.get(id);
    }

    /**
     * 根据事项ID获取事项节点
     *
     * @param itemId
     */
    public ItemNode getItemNodeByItemId(String itemId) {
        return itemNodeMap.get(itemId);
    }

    /**
     * @param id
     * @return
     */
    public List<Edge> getOutgoingEdges(String id) {
        List<Edge> outGoingEdges = Lists.newArrayList();
        this.edges.stream().forEach(edge -> {
            if (StringUtils.equals(edge.getFromId(), id)) {
                outGoingEdges.add(edge);
            }
        });
        return outGoingEdges;
    }

    /**
     * 根据开始节点，获取下一事项信息
     *
     * @param fromNode
     * @return
     */
    public List<NextItemInfo> listNextItemInfoByNode(Node fromNode) {
        List<NextItemInfo> list = Lists.newArrayList();
        extractNextItemInfo(fromNode, list);
        // 事项节点的子节点开始
        if (fromNode instanceof ItemNode) {
            ItemNode itemNode = (ItemNode) fromNode;
            List<String> children = itemNode.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                extractNextChildrenItemInfo(itemNode, children, list);
            }
        }
        return list;
    }

    private void extractNextChildrenItemInfo(ItemNode itemNode, List<String> children, List<NextItemInfo> list) {
        for (String childId : children) {
            Node node = getNodeById(childId);
            if (node == null) {
                continue;
            }
            if (isStartNode(node)) {
                extractNextItemInfo(node, list);
            }
        }
    }

    private boolean isStartNode(Node node) {
        return EnumItemFlowElement.StartNode.getValue().equals(node.getType());
    }

    private void extractNextItemInfo(Node fromNode, List<NextItemInfo> list) {
        List<ItemFlowDefinition.Edge> edges = this.getOutgoingEdges(fromNode.getId());
        edges.stream().forEach(edge -> {
            extractNextItemInfo(edge, list);
        });
    }

    private void extractNextItemInfo(Edge edge, List<NextItemInfo> list) {
        String toId = edge.getToId();
        Node node = this.getNodeById(toId);
        EnumItemFlowElement flowElement = EnumItemFlowElement.getByValue(node.getType());
        switch (flowElement) {
            // 网关节点
            case Gateway:
                extractNextItemInfo(node, list);
                break;
            case ItemNode:
                // 事项节点
                list.add(new NextItemInfo(edge, (ItemFlowDefinition.ItemNode) node));
                break;
        }
    }

    public static class NextItemInfo extends BaseObject {
        private static final long serialVersionUID = -827490175743810536L;

        private Edge edge;

        private ItemNode itemNode;

        public NextItemInfo(Edge edge, ItemNode itemNode) {
            this.edge = edge;
            this.itemNode = itemNode;
        }

        /**
         * @return the edge
         */
        public Edge getEdge() {
            return edge;
        }

        /**
         * @param edge 要设置的edge
         */
        public void setEdge(Edge edge) {
            this.edge = edge;
        }

        /**
         * @return the itemNode
         */
        public ItemNode getItemNode() {
            return itemNode;
        }

        /**
         * @param itemNode 要设置的itemNode
         */
        public void setItemNode(ItemNode itemNode) {
            this.itemNode = itemNode;
        }
    }


    /**
     * 节点
     */
    public static class Node extends BaseObject {
        private static final long serialVersionUID = 4377435983911066619L;

        private String id;
        private String type;

        public Node(String id, String type) {
            this.id = id;
            this.type = type;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

    }

    /**
     * 事项节点
     */
    public static class ItemNode extends Node {
        private static final long serialVersionUID = 71049368626966100L;

        // 子节点ID列表
        private List<String> children;

        private String itemId;
        private String itemType;
        private ProcessItemConfig itemConfig;
        private String belongItemId;

        /**
         * @param id
         * @param type
         */
        public ItemNode(String id, String type, List<String> children, String itemId, String itemType,
                        ProcessItemConfig itemConfig, String belongItemId) {
            super(id, type);
            this.children = children;
            this.itemId = itemId;
            this.itemType = itemType;
            this.itemConfig = itemConfig;
            this.belongItemId = belongItemId;
        }

        /**
         * @return the children
         */
        public List<String> getChildren() {
            return children;
        }

        /**
         * @return the itemId
         */
        public String getItemId() {
            return itemId;
        }

        /**
         * @return the itemType
         */
        public String getItemType() {
            return itemType;
        }

        /**
         * @return the itemConfig
         */
        public ProcessItemConfig getItemConfig() {
            return itemConfig;
        }

        /**
         * @return the belongItemId
         */
        public String getBelongItemId() {
            return belongItemId;
        }
    }

    /**
     * 边
     */
    public static class Edge extends BaseObject {
        private static final long serialVersionUID = 4563923931883133196L;

        private String id;
        private String fromId;
        private String toId;
        private EdgeConfiguration configuration;

        /**
         * @param id
         * @param fromId
         * @param toId
         * @param configuration
         */
        public Edge(String id, String fromId, String toId, EdgeConfiguration configuration) {
            this.id = id;
            this.fromId = fromId;
            this.toId = toId;
            this.configuration = configuration;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @return the fromId
         */
        public String getFromId() {
            return fromId;
        }

        /**
         * @return the toId
         */
        public String getToId() {
            return toId;
        }

        /**
         * @return the configuration
         */
        public EdgeConfiguration getConfiguration() {
            return configuration;
        }
    }

    /**
     * 边配置
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EdgeConfiguration extends BaseObject {
        private static final long serialVersionUID = -758322231418147411L;

        // 名称
        private String name;
        // 发起方式，1上一办理事项结束时发起、2监听上一办理事项办理事件发生、20监听组合事项办理事件发生
        private String startWay;
        // 监听事件
        private List<String> listenEvents = Lists.newArrayListWithCapacity(0);
        // 使用单据，1使用上一办理事项单据、2上一办理事项单据转换、10使用组合事项办理单据、20组合事项单据转换
        private String formDataType;
        // 单据转换规则
        private String copyBotRuleId;
        // 事项完成时反馈
        private boolean returnWithOver;
        // 事项发生事件时反馈
        private boolean returnWithEvent;
        // 反馈事件
        private List<String> returnEvents;
        // 反馈规则
        private String returnBotRuleId;
        // 事项集成流程时可配置提交到的环节、AUTO_SUBMIT自动提交、指定环节
        private String toTaskId;
        // 备注
        private String remark;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name 要设置的name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the startWay
         */
        public String getStartWay() {
            return startWay;
        }

        /**
         * @param startWay 要设置的startWay
         */
        public void setStartWay(String startWay) {
            this.startWay = startWay;
        }

        /**
         * @return the listenEvents
         */
        public List<String> getListenEvents() {
            return listenEvents;
        }

        /**
         * @param listenEvents 要设置的listenEvents
         */
        public void setListenEvents(List<String> listenEvents) {
            this.listenEvents = listenEvents;
        }

        /**
         * @return the formDataType
         */
        public String getFormDataType() {
            return formDataType;
        }

        /**
         * @param formDataType 要设置的formDataType
         */
        public void setFormDataType(String formDataType) {
            this.formDataType = formDataType;
        }

        /**
         * @return the copyBotRuleId
         */
        public String getCopyBotRuleId() {
            return copyBotRuleId;
        }

        /**
         * @param copyBotRuleId 要设置的copyBotRuleId
         */
        public void setCopyBotRuleId(String copyBotRuleId) {
            this.copyBotRuleId = copyBotRuleId;
        }

        /**
         * @return the returnWithOver
         */
        public boolean getReturnWithOver() {
            return returnWithOver;
        }

        /**
         * @param returnWithOver 要设置的returnWithOver
         */
        public void setReturnWithOver(boolean returnWithOver) {
            this.returnWithOver = returnWithOver;
        }

        /**
         * @return the returnWithEvent
         */
        public boolean getReturnWithEvent() {
            return returnWithEvent;
        }

        /**
         * @param returnWithEvent 要设置的returnWithEvent
         */
        public void setReturnWithEvent(boolean returnWithEvent) {
            this.returnWithEvent = returnWithEvent;
        }

        /**
         * @return the returnEvents
         */
        public List<String> getReturnEvents() {
            return returnEvents;
        }

        /**
         * @param returnEvents 要设置的returnEvents
         */
        public void setReturnEvents(List<String> returnEvents) {
            this.returnEvents = returnEvents;
        }

        /**
         * @return the returnBotRuleId
         */
        public String getReturnBotRuleId() {
            return returnBotRuleId;
        }

        /**
         * @param returnBotRuleId 要设置的returnBotRuleId
         */
        public void setReturnBotRuleId(String returnBotRuleId) {
            this.returnBotRuleId = returnBotRuleId;
        }

        /**
         * @return the toTaskId
         */
        public String getToTaskId() {
            return toTaskId;
        }

        /**
         * @param toTaskId 要设置的toTaskId
         */
        public void setToTaskId(String toTaskId) {
            this.toTaskId = toTaskId;
        }

        /**
         * @return the remark
         */
        public String getRemark() {
            return remark;
        }

        /**
         * @param remark 要设置的remark
         */
        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

}
