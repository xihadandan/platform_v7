package com.wellsoft.pt.common.component.jqgrid.tag;

import org.apache.commons.lang.StringUtils;

/**
 * 标签实体字符
 *
 * @author Siuon
 */
public class TagEntities {
    public final static String UNDERLINE = "_";
    public final static String UNDERLINE_ENTITIES = "&#@001";
    public final static String LEFT_BRACE = "{";
    public final static String LEFT_BRACE_ENTITIES = "&#@002";
    public final static String COMMA = ",";
    public final static String COMMA_ENTITIES = "&#@003";
    public final static String SEMICOLON = ";";
    public final static String SEMICOLON_ENTITIES = "&#@004";
    public final static String ITEM = "€";
    public final static String ITEM_ENTITIES = "&#@004";

    public static String char2Entities(String text) {
        return StringUtils.replaceEach(text, new String[]{UNDERLINE, LEFT_BRACE, COMMA, SEMICOLON, ITEM},
                new String[]{UNDERLINE_ENTITIES, LEFT_BRACE_ENTITIES, COMMA_ENTITIES, SEMICOLON_ENTITIES,
                        ITEM_ENTITIES});
    }

    public static String entities2Char(String text) {
        return StringUtils.replaceEach(text, new String[]{UNDERLINE_ENTITIES, LEFT_BRACE_ENTITIES, COMMA_ENTITIES,
                SEMICOLON_ENTITIES, ITEM_ENTITIES}, new String[]{UNDERLINE, LEFT_BRACE, COMMA, SEMICOLON, ITEM});
    }

    public static void main(String[] args) {
        System.out.println(char2Entities(",_{;,_{;,_{;"));
        System.out.println(entities2Char("&#@003&#@001&#@002&#@004&#@003&#@001&#@002&#@004&#@003&#@001&#@002&#@004"));
    }

}
