package com.example.aggridserversidespringbootexample.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static final ObjectMapper objectMapper;
    private static final ResourceLoader resourceLoader;

    static {
        objectMapper = new ObjectMapper();
        resourceLoader = new FileSystemResourceLoader();
    }

    public static Map getMockJson(String filepath) {
        System.out.println("request json " + filepath);
        try {
            Resource mockFile = resourceLoader.getResource(filepath);
            return objectMapper.readValue(mockFile.getInputStream(), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    public static List<String> getJsonList(String filepath) {
        System.out.println("request json " + filepath);
        try {
            Resource mockFile = resourceLoader.getResource(filepath);
            var typeReference = new TypeReference<List<String>>() {
            };
            return objectMapper.readValue(mockFile.getInputStream(), typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get Map from json file
     *
     * @param filepath example "classpath:mappers/map-filter-map.json"
     * @return JSON as HashMap
     */
    public static Map getJson(String filepath) {
        System.out.println("request json " + filepath);
        try {
            Resource mockFile = resourceLoader.getResource(filepath);
            return objectMapper.readValue(mockFile.getInputStream(), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    /**
     * Get instance of T from json file
     *
     * @param filepath example "classpath:mappers/map-filter-map.json"
     * @return instance of T from JSON
     */
    public static <T> T mapJsonToType(String filepath, Class<T> clazz) {
        System.out.println("request json " + filepath);
        try {
            Resource mockFile = resourceLoader.getResource(filepath);
            return objectMapper.readValue(mockFile.getInputStream(), clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Map<String, Object>> getJsonOfMapOfMaps(String filepath) throws IOException {
        System.out.println("request json " + filepath);
        Resource mockFile = resourceLoader.getResource(filepath);
        var typeReference = new TypeReference<Map<String, Map<String, Object>>>() {
        };
        return objectMapper.readValue(mockFile.getInputStream(), typeReference);
    }

    public static Object getObjectFromJson(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }

}
