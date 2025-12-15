-- 全文检索词
CREATE TABLE FULLTEXT_SEARCH_WORD
(
    uuid         NUMBER(20, 0) PRIMARY KEY,
    create_time  TIMESTAMP,
    modify_time  TIMESTAMP,
    creator      VARCHAR2(64),
    modifier     VARCHAR2(64),
    rec_ver      NUMBER(10, 0),
    system       VARCHAR2(64),
    tenant       VARCHAR2(64),
    user_id      VARCHAR2(64),
    keyword      VARCHAR2(100),
    search_count NUMBER(10, 0)
);
-- 添加字段说明
COMMENT
    ON TABLE FULLTEXT_SEARCH_WORD IS '全文检索词';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD.uuid IS '主键';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD.create_time IS '创建时间';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD.modify_time IS '更新时间';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD.creator IS '创建用户';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD.modifier IS '更新用户';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD.rec_ver IS '版本号';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD.system IS '系统标识';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD.tenant IS '租户标识';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD.user_id IS '用户ID';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD.keyword IS '关键词';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD.search_count IS '查询次数';


-- 全文检索词条目
CREATE TABLE FULLTEXT_SEARCH_WORD_TERM
(
    uuid             NUMBER(20, 0) PRIMARY KEY,
    create_time      TIMESTAMP,
    modify_time      TIMESTAMP,
    creator          VARCHAR2(64),
    modifier         VARCHAR2(64),
    rec_ver          NUMBER(10, 0),
    search_word_uuid NUMBER(20, 0),
    term             VARCHAR2(100),
    sort_order       NUMBER(10, 0)
);
-- 添加字段说明
COMMENT
    ON TABLE FULLTEXT_SEARCH_WORD_TERM IS '全文检索词条目';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD_TERM.uuid IS '主键';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD_TERM.create_time IS '创建时间';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD_TERM.modify_time IS '更新时间';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD_TERM.creator IS '创建用户';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD_TERM.modifier IS '更新用户';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD_TERM.rec_ver IS '版本号';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD_TERM.search_word_uuid IS '全文检索词UUID';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD_TERM.term IS '条目';
COMMENT
    ON COLUMN FULLTEXT_SEARCH_WORD_TERM.sort_order IS '排序号';
