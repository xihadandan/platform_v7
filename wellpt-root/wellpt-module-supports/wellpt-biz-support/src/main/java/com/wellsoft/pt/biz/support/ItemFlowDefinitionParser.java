/*
 * @(#)12/11/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.biz.enums.EnumItemFlowElement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
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
public class ItemFlowDefinitionParser {

    /**
     * @param itemFlows
     * @return
     */
    public static Map<String, ItemFlowDefinition> parse(String itemFlows, ProcessDefinitionJsonParser parser) {
        Map<String, ItemFlowDefinition> itemFlowMap = Maps.newHashMap();
        if (StringUtils.isBlank(itemFlows)) {
            return itemFlowMap;
        }

        Map<String, Map> itemFlowConfigMap = JsonUtils.json2Object(itemFlows, Map.class);
        for (Map.Entry<String, Map> entry : itemFlowConfigMap.entrySet()) {
            String itemId = entry.getKey();
            String configJson = JsonUtils.object2Json(entry.getValue());
            ItemFlowConfig itemFlowConfig = JsonUtils.json2Object(configJson, ItemFlowConfig.class);
            itemFlowMap.put(itemId, parseItemFlowConfig(itemFlowConfig, parser));
        }
        return itemFlowMap;
    }

    /**
     * 解析事项流配置
     *
     * @param itemFlowConfig
     * @param parser
     * @return
     */
    private static ItemFlowDefinition parseItemFlowConfig(ItemFlowConfig itemFlowConfig, ProcessDefinitionJsonParser parser) {
        List<GraphCell> cells = itemFlowConfig.getGraphData().getCells();
        return new ItemFlowDefinition(parseNodes(cells, parser), parseEdges(cells, parser));
    }

    /**
     * 解析节点
     *
     * @param cells
     * @return
     */
    private static List<ItemFlowDefinition.Node> parseNodes(List<GraphCell> cells, ProcessDefinitionJsonParser parser) {
        if (CollectionUtils.isEmpty(cells)) {
            return Collections.emptyList();
        }

        List<ItemFlowDefinition.Node> nodes = Lists.newArrayList();
        cells.forEach(cell -> {
            ItemFlowDefinition.Node node = parseNode(cell, parser);
            if (node != null) {
                nodes.add(node);
            }
        });

        return nodes;
    }

    private static ItemFlowDefinition.Node parseNode(GraphCell cell, ProcessDefinitionJsonParser parser) {
        GraphCellData cellData = cell.getData();
        if (cellData == null) {
            return null;
        }

        ItemFlowDefinition.Node node = null;
        String type = cellData.getType();
        EnumItemFlowElement flowElement = EnumItemFlowElement.getByValue(type);
        if (flowElement != null) {
            switch (flowElement) {
                case StartNode:
                case EndNode:
                case Gateway:
                    node = new ItemFlowDefinition.Node(cell.getId(), cellData.getType());
                    break;
                case ItemNode:
                    node = parseItemNode(cell, cellData, parser);
                    break;
            }
        }
        return node;
    }

    /**
     * 解析事项节点
     *
     * @param cell
     * @param cellData
     * @param parser
     * @return
     */
    private static ItemFlowDefinition.Node parseItemNode(GraphCell cell, GraphCellData cellData, ProcessDefinitionJsonParser parser) {
        String id = cell.getId();
        String type = cellData.getType();
        List<String> children = cell.getChildren();
        String itemId = cellData.getItemId();
        String itemType = cellData.getItemType();
        Map<String, Object> configuration = cellData.getConfiguration();
        ProcessItemConfig itemConfig = parser.getProcessItemConfigById(itemId);
        String belongItemId = cellData.getBelongItemId();
        // 事项流组合节点中配置的事项
        if (itemConfig == null && configuration != null) {
            itemConfig = JsonUtils.json2Object(JsonUtils.object2Json(configuration), ProcessItemConfig.class);
            parser.addChildProcessItemConfig(belongItemId, itemConfig);
        }

        return new ItemFlowDefinition.ItemNode(id, type, children, itemId, itemType, itemConfig, belongItemId);
    }

    /**
     * 解析边
     *
     * @param cells
     * @return
     */
    private static List<ItemFlowDefinition.Edge> parseEdges(List<GraphCell> cells, ProcessDefinitionJsonParser parser) {
        if (CollectionUtils.isEmpty(cells)) {
            return Collections.emptyList();
        }

        List<ItemFlowDefinition.Edge> edges = Lists.newArrayList();
        cells.forEach(cell -> {
            if (StringUtils.equals(cell.getShape(), EnumItemFlowElement.Edge.getValue())) {
                ItemFlowDefinition.Edge edge = parseEdge(cell, parser);
                if (edge != null) {
                    edges.add(edge);
                }
            }
        });

        return edges;
    }

    /**
     * 解析边
     *
     * @param cell
     * @param parser
     * @return
     */
    private static ItemFlowDefinition.Edge parseEdge(GraphCell cell, ProcessDefinitionJsonParser parser) {
        String id = cell.getId();
        String fromId = cell.getSource().getCell();
        String toId = cell.getTarget().getCell();
        ItemFlowDefinition.EdgeConfiguration edgeConfiguration = null;
        GraphCellData cellData = cell.getData();
        if (cellData != null && cellData.getConfiguration() != null) {
            edgeConfiguration = JsonUtils.json2Object(JsonUtils.object2Json(cellData.getConfiguration()),
                    ItemFlowDefinition.EdgeConfiguration.class);
        }
        return new ItemFlowDefinition.Edge(id, fromId, toId, edgeConfiguration);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ItemFlowConfig extends BaseObject {
        private static final long serialVersionUID = 8545018829353891528L;

        private ItemFlowGraphData graphData;

        /**
         * @return the graphData
         */
        public ItemFlowGraphData getGraphData() {
            return graphData;
        }

        /**
         * @param graphData 要设置的graphData
         */
        public void setGraphData(ItemFlowGraphData graphData) {
            this.graphData = graphData;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ItemFlowGraphData extends BaseObject {
        private static final long serialVersionUID = 6927653793052101966L;

        private List<GraphCell> cells = Lists.newArrayListWithCapacity(0);

        /**
         * @return the cells
         */
        public List<GraphCell> getCells() {
            return cells;
        }

        /**
         * @param cells 要设置的cells
         */
        public void setCells(List<GraphCell> cells) {
            this.cells = cells;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GraphCell extends BaseObject {
        private static final long serialVersionUID = 6147923943433570751L;

        // ID，唯一标识
        private String id;

        // edge边
        private String shape;

        // 源对象
        private CellObject source;

        // 目标对象
        private CellObject target;

        // 数据
        private GraphCellData data;

        // 子节点ID列表
        private List<String> children;

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the shape
         */
        public String getShape() {
            return shape;
        }

        /**
         * @param shape 要设置的shape
         */
        public void setShape(String shape) {
            this.shape = shape;
        }

        /**
         * @return the source
         */
        public CellObject getSource() {
            return source;
        }

        /**
         * @param source 要设置的source
         */
        public void setSource(CellObject source) {
            this.source = source;
        }

        /**
         * @return the target
         */
        public CellObject getTarget() {
            return target;
        }

        /**
         * @param target 要设置的target
         */
        public void setTarget(CellObject target) {
            this.target = target;
        }

        /**
         * @return the data
         */
        public GraphCellData getData() {
            return data;
        }

        /**
         * @param data 要设置的data
         */
        public void setData(GraphCellData data) {
            this.data = data;
        }

        /**
         * @return the children
         */
        public List<String> getChildren() {
            return children;
        }

        /**
         * @param children 要设置的children
         */
        public void setChildren(List<String> children) {
            this.children = children;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CellObject extends BaseObject {
        private static final long serialVersionUID = 2320353788925131824L;

        // 对象标识
        private String cell;

        /**
         * @return the cell
         */
        public String getCell() {
            return cell;
        }

        /**
         * @param cell 要设置的cell
         */
        public void setCell(String cell) {
            this.cell = cell;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GraphCellData extends BaseObject {
        private static final long serialVersionUID = 2335385684645078829L;

        // start开始节点、gateway网关、item事项、edge边、end结束节点
        private String type;

        private String itemId;

        private String itemType;

        private Map<String, Object> configuration;

        private String belongItemId;

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type 要设置的type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return the itemId
         */
        public String getItemId() {
            return itemId;
        }

        /**
         * @param itemId 要设置的itemId
         */
        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        /**
         * @return the itemType
         */
        public String getItemType() {
            return itemType;
        }

        /**
         * @param itemType 要设置的itemType
         */
        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        /**
         * @return the configuration
         */
        public Map<String, Object> getConfiguration() {
            return configuration;
        }

        /**
         * @param configuration 要设置的configuration
         */
        public void setConfiguration(Map<String, Object> configuration) {
            this.configuration = configuration;
        }

        /**
         * @return the belongItemId
         */
        public String getBelongItemId() {
            return belongItemId;
        }

        /**
         * @param belongItemId 要设置的belongItemId
         */
        public void setBelongItemId(String belongItemId) {
            this.belongItemId = belongItemId;
        }
    }
}
