package com.plenamente.sgt.infra.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
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
            log.debug("Serializando objeto: {}", t);
            byte[] result = super.serialize(t);
            log.debug("Objeto serializado exitosamente");
            return result;
        } catch (SerializationException e) {
            log.error("Error serializando objeto: {}", t, e);
            throw e;
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            log.warn("Bytes vacíos durante deserialización.");
            return null;
        }

        try {
            String json = new String(bytes);

            try {
                if (json.startsWith("[")) {
                    List<RegisterMaterial> materials = objectMapper.readValue(bytes,
                            new TypeReference<>() {
                            });
                    log.debug("Deserializado como List<RegisterMaterial>");
                    return materials;
                }
            } catch (IOException e) {
                log.debug("No es una lista de materiales, intentando deserialización genérica");
            }

            // Si no es una lista de materiales, usar la deserialización genérica
            Object result = super.deserialize(bytes);
            log.debug("Objeto deserializado exitosamente mediante deserialización genérica");
            return result;

        } catch (Exception e) {
            log.error("Error deserializando objeto: {}", new String(bytes), e);
            throw new SerializationException("Error deserializando objeto", e);
        }
    }
}