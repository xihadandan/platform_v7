export const DEFAULT_SYS_COLUMNS = [
  {
    uuid: 'UUID',
    column: 'UUID',
    dataType: 'varchar',
    length: 64,
    notNull: true,
    title: 'UUID',
    remark: '数据唯一标识字段',
    unique: 'GLOBAL',
    isSysDefault: true
  },
  {
    uuid: 'CREATOR',
    column: 'CREATOR',
    dataType: 'varchar',
    length: 64,
    notNull: true,
    title: '创建人',
    isSysDefault: true
  },
  {
    uuid: 'CREATE_TIME',
    column: 'CREATE_TIME',
    dataType: 'timestamp',
    notNull: true,
    title: '创建时间',
    isSysDefault: true
  },
  {
    uuid: 'MODIFIER',
    column: 'MODIFIER',
    dataType: 'varchar',
    length: 64,
    title: '修改人',
    isSysDefault: true
  },
  {
    uuid: 'MODIFY_TIME',
    column: 'MODIFY_TIME',
    dataType: 'timestamp',
    title: '修改时间',
    isSysDefault: true
  },
  {
    uuid: 'TENANT',
    column: 'TENANT',
    dataType: 'varchar',
    length: 64,
    title: '归属租户',
    isSysDefault: true
  },
  {
    uuid: 'SYSTEM',
    column: 'SYSTEM',
    dataType: 'varchar',
    length: 64,
    title: '归属系统',
    isSysDefault: true
  },
  {
    uuid: 'REC_VER',
    column: 'REC_VER',
    dataType: 'number',
    length: 10,
    title: '版本号',
    isSysDefault: true
  },
  {
    uuid: 'STATUS',
    column: 'STATUS',
    dataType: 'varchar',
    length: 1,
    title: '数据状态',
    isSysDefault: true
  },
  {
    uuid: 'SYSTEM_UNIT_ID',
    column: 'SYSTEM_UNIT_ID',
    dataType: 'varchar',
    length: 32,
    title: '单位ID',
    isSysDefault: true
  },
  {
    uuid: 'FORM_UUID',
    column: 'FORM_UUID',
    dataType: 'varchar',
    length: 64,
    title: '表单UUID',
    isSysDefault: true
  }
]
