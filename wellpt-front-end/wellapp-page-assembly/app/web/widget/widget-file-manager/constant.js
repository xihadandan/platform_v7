const DEFAULT_ADMIN_ACTIONS = [
  'createFolder',
  'readFolder',
  'listFolderAndFiles',
  'viewFolderAttributes',
  'editFolderAttributes',
  'renameFolder',
  'moveFolder',
  'copyFolder',
  'deleteFolder',
  'forceDeleteFolder',
  'createFile',
  'createDocument',
  'readFile',
  // 'openFile',
  'downloadFile',
  'viewFileAttributes',
  'renameFile',
  'editFile',
  'moveFile',
  'copyFile',
  'deleteFile',
  'restoreFile'
];
const DEFAULT_EDITOR_ACTIONS = [
  'readFolder',
  'listFolderAndFiles',
  'viewFolderAttributes',
  'editFolderAttributes',
  'renameFolder',
  'moveFolder',
  'readFile',
  // 'openFile',
  'viewFileAttributes',
  'renameFile',
  'editFile',
  'moveFile'
];
const DEFAULT_READER_ACTIONS = ['readFolder', 'listFolderAndFiles', 'viewFolderAttributes', 'readFile', 'viewFileAttributes'];


const FILE_ACTION_OPTIONS = [
  { value: 'createFolder', label: '创建夹' },
  { value: 'readFolder', label: '读取当前夹' },
  { value: 'listFolder', label: '列出当前夹下的子夹' },
  { value: 'listAllFolder', label: '列出当前夹下的所有子夹(包含子夹)' },
  { value: 'listFiles', label: '列出当前夹下的文件' },
  { value: 'listAllFiles', label: '列出当前夹下的文件(包含子夹)' },
  { value: 'listFolderAndFiles', label: '列出当前夹下的子夹及文件' },
  { value: 'listAllFolderAndFiles', label: '列出当前夹下的子夹及文件(包含子夹)' },
  { value: 'viewFolderAttributes', label: '查看夹属性' },
  { value: 'editFolderAttributes', label: '编辑夹属性' },
  { value: 'renameFolder', label: '重命名夹' },
  { value: 'moveFolder', label: '移动夹' },
  { value: 'copyFolder', label: '复制夹' },
  { value: 'deleteFolder', label: '删除夹(有子夹、文件不可删除)' },
  { value: 'forceDeleteFolder', label: '删除夹(有子夹、文件一起删除)' },
  { value: 'createFile', label: '创建文件' },
  { value: 'createDocument', label: '创建文档' },
  { value: 'readFile', label: '读取文件' },
  // { value: 'openFile', label: '打开文件' },
  { value: 'downloadFile', label: '下载文件' },
  { value: 'viewFileAttributes', label: '查看文件属性' },
  { value: 'renameFile', label: '重命名文件' },
  { value: 'editFile', label: '编辑文件' },
  { value: 'moveFile', label: '移动文件' },
  { value: 'copyFile', label: '复制文件' },
  { value: 'deleteFile', label: '删除文件' },
  { value: 'restoreFile', label: '恢复文件' }
];

export { DEFAULT_ADMIN_ACTIONS, DEFAULT_EDITOR_ACTIONS, DEFAULT_READER_ACTIONS, FILE_ACTION_OPTIONS }
