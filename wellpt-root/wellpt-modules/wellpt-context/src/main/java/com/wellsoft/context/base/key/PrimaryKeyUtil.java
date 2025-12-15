package com.wellsoft.context.base.key;

/**
 * @author lilin
 * @ClassName: PrimaryKeyUtil
 * @Description: 生产uuid
 */
public class PrimaryKeyUtil implements KeyConstants {

    /**
     * * @param name
     *
     * @return
     */
    public static String getPrimaryKey(String name) {
        SingleSequence sequence = SequenceFactory.getSequence(name);
        return sequence.getNextVal();

    }

}
