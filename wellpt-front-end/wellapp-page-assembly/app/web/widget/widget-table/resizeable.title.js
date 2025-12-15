import VueDraggableResizable from 'vue-draggable-resizable';

export default (h, props, children) => {
  let draghandler = null,
    th = null;
  const { key, ...restProps } = props;
  let columns = children[0].context.$vnode.parent.componentOptions.propsData.columns;
  const col = columns.find(col => {
    return col.key === key;
  });

  if (col == null || !col.width || !col.widthResizable) {
    return <th {...restProps}>{children}</th>;
  }
  const onDrag = x => {
    // console.log('onDrag', x);
    th.style.overflow = 'visible';
  };

  const onDragstop = x => {
    col.width += x;
    draghandler.left = 1;
    th.style.overflow = 'hidden';
  };

  return (
    <th {...restProps} width={col.width} v-ant-ref={r => (th = r)} class="resize-table-th">
      {children}
      <VueDraggableResizable
        v-ant-ref={r => (draghandler = r)}
        key={col.key}
        class="table-draggable-handle"
        class-name-dragging="table-draggable-dragging"
        w={5}
        x={1}
        z={1}
        axis="x"
        draggable={true}
        resizable={false}
        onDragging={onDrag}
        onDragstop={onDragstop}
      ></VueDraggableResizable>
    </th>
  );
};
