package com.wellsoft.context.util.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.base.Stopwatch;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: 使用kryo序列化工具包
 *
 * @author chenq
 * @date 2019/2/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/2/14    chenq		2019/2/14		Create
 * </pre>
 */
public class SerializationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SerializationUtils.class);


    private static final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            UnmodifiableCollectionsSerializer.registerSerializers(kryo);
            kryo.setRegistrationRequired(false);
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            kryo.getSerializer(ArrayList.class).setAcceptsNull(true);
            kryo.register(Arrays.asList().getClass(), new ArraysArrayListSerializer());
            return kryo;
        }
    };

    public static Kryo getInstance() {
        return kryoLocal.get();
    }


    /**
     * 序列化对象
     *
     * @param source 对象
     * @param <T>
     * @return
     */
    public static <T extends Serializable> byte[] serializationObject(T source) {
        Stopwatch timer = Stopwatch.createStarted();
        Kryo kryo = getInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        try {
            kryo.writeClassAndObject(output, source);
        } catch (Exception e) {
            LOGGER.error("kryo序列化对象异常：", e);
        } finally {
            kryoLocal.remove();
        }
        output.flush();
        output.close();
        LOGGER.info("serializationObject time:{}", timer.stop());
        byte[] b = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            LOGGER.error("kryo序列化io异常：", e);
        }
        return b;

    }

    /**
     * 反序列化对象
     *
     * @param source
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialization(byte[] source) {
        Kryo kryo = getInstance();
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(source);
            Input input = new Input(bais);
            return (T) kryo.readClassAndObject(input);
        } catch (Exception e) {
            LOGGER.error("kryo反序列化对象异常：", e);
        } finally {
            kryoLocal.remove();
        }
        return null;
    }


    public static class ArraysArrayListSerializer extends Serializer<List<?>> {
        @Override
        public void write(Kryo kryo, Output output, List<?> list) {
            kryo.writeObject(output, new ArrayList<>(list));
        }

        @Override
        public List<?> read(Kryo kryo, Input input, Class<List<?>> type) {
            return kryo.readObject(input, ArrayList.class);
        }
    }


}
