package com.plenamente.sgt.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

@Slf4j
public class LoggingRedisSerializer extends GenericJackson2JsonRedisSerializer {

    public LoggingRedisSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    @NonNull
    public byte[] serialize(@Nullable Object source) throws SerializationException {
        try {
            log.debug("Serializando objeto: {}", source);
            if (source == null) {
                return new byte[0];
            }
            byte[] result = super.serialize(source);
            log.debug("Objeto serializado exitosamente");
            return result;
        } catch (Exception e) {
            log.error("Error serializando objeto: {}", source, e);
            throw new SerializationException("Error serializando objeto", e);
        }
    }

    @Override
    @NonNull
    public Object deserialize(@NonNull byte[] source) throws SerializationException {
        try {
            if (source.length == 0) {
                return null;
            }
            Object result = super.deserialize(source);
            log.debug("Objeto deserializado exitosamente: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error deserializando objeto", e);
            throw new SerializationException("Error deserializando objeto", e);
        }
    }
}
