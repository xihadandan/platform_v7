package com.wellsoft.pt.di.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.di.dao.DiTableColumnDataChangeDao;
import com.wellsoft.pt.di.entity.DiTableColumnDataChangeEntity;
import com.wellsoft.pt.di.service.DiTableColumnDataChangeService;
import com.wellsoft.pt.jpa.service.impl.AbstractEntityServiceImpl;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/26    chenq		2019/8/26		Create
 * </pre>
 */
@Service
public class DiTableColumnDataChangeServiceImpl extends
        AbstractEntityServiceImpl<DiTableColumnDataChangeEntity, DiTableColumnDataChangeDao> implements
        DiTableColumnDataChangeService {
    @Override
    public boolean existLobDataChange(String uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        return this.dao.countByHQL(
                "from DiTableColumnDataChangeEntity where uuid=:uuid and (dataType='CLOB' or dataTYpe='BLOB') ",
                params) > 0;

    }

    @Override
    public List<DiTableColumnDataChangeEntity> getAllColumnDataByUuid(String uuid) {
        DiTableColumnDataChangeEntity example = new DiTableColumnDataChangeEntity();
        example.setUuid(uuid);
        List<DiTableColumnDataChangeEntity> list = this.dao.listByEntity(example);
        for (DiTableColumnDataChangeEntity columnData : list) {
            try {
                if ("CLOB".equalsIgnoreCase(columnData.getDataType())) {
                    columnData.setDataClobValue(new SerialClob(IOUtils.toCharArray(
                            columnData.getDataClobValue().getCharacterStream())));
                    columnData.setLobLength(
                            columnData.getLobLength() + columnData.getDataClobValue().length());

                } else if ("BLOB".equalsIgnoreCase(columnData.getDataType())) {
                    columnData.setDataBlobValue(new SerialBlob(
                            IOUtils.toByteArray(columnData.getDataBlobValue().getBinaryStream())));
                    columnData.setLobLength(
                            columnData.getLobLength() + columnData.getDataBlobValue().length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return list;
    }
}
