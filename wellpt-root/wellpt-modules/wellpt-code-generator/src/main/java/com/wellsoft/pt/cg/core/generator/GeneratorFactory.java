package com.wellsoft.pt.cg.core.generator;

import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.generator.impl.DyformGenerator;
import com.wellsoft.pt.cg.core.generator.impl.EntityGenerator;
import com.wellsoft.pt.cg.core.generator.impl.FlowGenerator;
import com.wellsoft.pt.cg.core.generator.impl.TableGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成器工厂，根据生成类型生成具体的生成器
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-7.1	lmw		2015-7-7		Create
 * </pre>
 * @date 2015-6-18
 */
public final class GeneratorFactory {
    private static Map<String, Generator> generators = new HashMap<String, Generator>();

    public static Generator getGenerator(String type) {
        Generator generator = generators.get(type);
        if (generator != null)
            return generator;
		/*if(Type.OUTPUTTYPE_ENTITY.equals(type)){
			generator= new EntryGenerator();
			generators.put(Type.OUTPUTTYPE_ENTITY, generator);
			return generator;
		}else if(Type.OUTPUTTYPE_BAM.equals(type)){
			generator= new BAMGenerator();
			generators.put(Type.OUTPUTTYPE_BAM, generator);
			return generator;
		}else if(Type.OUTPUTTYPE_BASIC_SERVICE.equals(type)){
			generator= new BaseServiceGenerator();
			generators.put(Type.OUTPUTTYPE_BASIC_SERVICE, generator);
			return generator;
		}else if(Type.OUTPUTTYPE_FACADE_SERVICE.equals(type)){
			generator= new FacadeServiceGenerator();
			generators.put(Type.OUTPUTTYPE_FACADE_SERVICE, generator);
			return generator;
		}else if(Type.OUTPUTTYPE_VALUE_OBJECT.equals(type)){
			generator= new ValueObjGenerator();
			generators.put(Type.OUTPUTTYPE_VALUE_OBJECT, generator);
			return generator;
		}else{
			throw new RuntimeException("getGenerator error: the  "+type+" is not support");
		}*/
        return null;
    }

    /**
     * 获取生成器
     *
     * @param type
     * @return
     */
    public static Generator intendGenerator(int type) {
        switch (type) {
            case Type.GENTYPE_ENTITY:
                return new EntityGenerator();
            case Type.GENTYPE_FLOW_DEFINITION:
                return new FlowGenerator();
            case Type.GENTYPE_TABLE:
                return new TableGenerator();
            case Type.GENTYPE_DYFORM_DEFINITION:
                return new DyformGenerator();
            default:
                throw new RuntimeException("getGenerator error: the  " + type + " is not support");
        }
    }
}
