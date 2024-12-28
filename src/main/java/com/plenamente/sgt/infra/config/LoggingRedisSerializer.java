package com.plenamente.sgt.infra.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plenamente.sgt.domain.dto.MaterialDto.RegisterMaterial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;
import java.util.List;

@Slf4j
public class LoggingRedisSerializer extends GenericJackson2JsonRedisSerializer {

    private final ObjectMapper objectMapper;

    public LoggingRedisSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        try {
            log.info("Serializando objeto: {}", t);
            byte[] result = super.serialize(t);
            log.info("Objeto serializado exitosamente: {}", new String(result));
            return result;
        } catch (SerializationException e) {
            log.error("Error serializando objeto: {}", t, e);
            throw e;
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        try {
            if (bytes == null || bytes.length == 0) {
                log.warn("Bytes vacíos durante deserialización.");
                return null;
            }

            // Deserialización explícita con TypeReference
            List<RegisterMaterial> result = objectMapper.readValue(bytes, new TypeReference<>() {});
            log.info("Objeto deserializado exitosamente: {}", result);
            return result;
        } catch (JsonProcessingException e) {
            log.error("Error deserializando objeto desde Redis: {}", new String(bytes), e);
            throw new SerializationException("Error deserializando objeto", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
