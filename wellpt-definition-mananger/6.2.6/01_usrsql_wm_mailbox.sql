-- Add/modify columns
alter table WM_MAILBOX add(
    read_receipt_status number(1,0) default 0 not null,
    priority number(1,0) default 3 not null,
    actual_to_status clob,
    actual_cc_status clob,
    actual_bcc_status clob,
    send_status number(1,0),
    send_count number(2,0) default 0 not null,
    fail_msg VARCHAR2(2000 CHAR),
    next_send_time TIMESTAMP
    );

-- Add comments to the columns
comment on column WM_MAILBOX.read_receipt_status is '阅读回执状态（0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执）';
comment on column WM_MAILBOX.priority is '优先级（1：最高，2：高，3: 正常 默认值，4：低，5：最低）';
comment on column WM_MAILBOX.actual_to_status is '实际发送状态（1:投递中，2：投递成功，3：邮件服务异常，4..5..）';
comment on column WM_MAILBOX.actual_cc_status is '实际抄送状态';
comment on column WM_MAILBOX.actual_bcc_status is '实际密送状态';
comment on column WM_MAILBOX.send_status is '发送状态（0:待发送，1:已发送，2:发送失败）';
comment on column WM_MAILBOX.send_count is '发送次数';
comment on column WM_MAILBOX.fail_msg is '失败原因';
comment on column WM_MAILBOX.next_send_time is '下次执行发送时间';
