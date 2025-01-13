package wellness.shop.Integration;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Redis {

    private final JedisPool jedisPool;

    public Redis(String host, int port) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(5);
        this.jedisPool = new JedisPool(host, port);

    }

    /**
     * Put serialized object to Redis. exTime is time object lasts
     *
     */
    public void putWithSerialize(String key, Object value, int exTime) throws IOException {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key.getBytes(), exTime, serialize(value));
        }
    }

    /**
     * Put json object to Redis. exTime is time object lasts
     *
     */
    public void put(String key, String json,int exTime) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key.getBytes(), exTime, json.getBytes());
        }
    }

    /**
     * Put json object to Redis permanently.
     *
     */
    public void putPermanently(String key, String json) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key.getBytes(), json.getBytes());
        }
    }

    /**
     * get All Key from redis.
     *
     */

    public Set<String> getKeys()  throws IOException {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys("*");
        }
    }

    /**
     * Get serialized object from Redis.
     *
     */
    public Object getWithDeserialize(String key) throws IOException, ClassNotFoundException {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] data = jedis.get(key.getBytes());
            if (data != null) {
                return deserialize(data);
            }
            return null;
        }
    }

    /**
     * Get json object from Redis.
     *
     */
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] data = jedis.get(key.getBytes());
            if (data != null) {
                return new String(data, StandardCharsets.UTF_8);
            }
            return null;
        }
    }

    /**
     * Check if such key exist in redis.
     *
     */

    public boolean keyExists(String key){
        try (Jedis jedis = jedisPool.getResource()) {
           return jedis.exists(key);
        } catch (Exception e) {
           return false;
        }
    }

    /**
     * Delete object from redis.
     *
     */

    public void delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.exists(key)) jedis.del(key);

        }
    }

    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
            out.writeObject(obj);
            return byteOut.toByteArray();
        }
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
             ObjectInputStream in = new ObjectInputStream(byteIn)) {
            return in.readObject();
        }
    }

    /**
     * Close redis.
     *
     */

    public void close() {
        jedisPool.close();
    }
}