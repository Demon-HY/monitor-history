package monitor.service.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import monitor.exception.UnInitilized;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {
	
	// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	private static boolean TEST_ON_BORROW = false;

    private Redis() {}
    
    private static JedisPool jedisPool;
    public static void init(PoolInfo poolInfo) {
        if (null == jedisPool) {
        	JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(poolInfo.maxActive);
            config.setMaxIdle(poolInfo.maxIdle);
            config.setMaxWaitMillis(poolInfo.maxWait);
            config.setTestOnBorrow(TEST_ON_BORROW);
            config.setTestOnReturn(false);
            config.setTestWhileIdle(true);
            config.setNumTestsPerEvictionRun(poolInfo.numTestsPerEvictionRun);
            config.setTimeBetweenEvictionRunsMillis(poolInfo.timeBetweenEvictionRunsMillis);
            config.setMinEvictableIdleTimeMillis(poolInfo.minEvictableIdleTimeMillis);
            
            jedisPool = new JedisPool(config, poolInfo.host, poolInfo.port);
        }
    }
    
    public static Redis getInst() throws UnInitilized {
        return new Redis();
    }
    
    /**
     * 从 Redis 连接池中获取 Jedis 实例
     * @return Jedis
     */
    public static Jedis getJedis() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis;
        }
    }
    
    /**
     * 释放jedis资源
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
    	if (jedis != null) {
    		jedisPool.returnResource(jedis);
    	}
	}
    
    /**
     * 获取 redis 所有键
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<String> getKeys() {
    	Jedis jedis = getJedis();
    	List<String> list = (List<String>) jedis.keys("*");
    	return list;
    }
    
    public void putList(String key, String value) {
    	Jedis jedis = getJedis();
    	jedis.lpush(key, value);
    }
    
    public void put(String key, Serializable value) throws IOException {
        Jedis jedis = getJedis();
        jedis.set(key.getBytes(), objectToByteArray(value));
        jedis.close();
    }
    
    public void put(String key, Serializable value, int age) throws IOException {
        Jedis jedis = getJedis();
        jedis.setex(key.getBytes(), age, objectToByteArray(value));
        jedis.close();
    }
    
    public Serializable get(String key) throws ClassNotFoundException, IOException {
        Jedis jedis = getJedis();
        byte[] value = jedis.get(key.getBytes());
        jedis.close();
        return ByteArrayToObject(value);
    }
    
    public void remove(String key) {
        Jedis jedis = getJedis();
        jedis.del(key);
        jedis.close();
    }
    
    
    
    public static byte[] objectToByteArray(Serializable value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(value);
        byte[] result = baos.toByteArray();
        oos.flush();
        oos.close();
        return result;
    }
    
    public static Serializable ByteArrayToObject(byte[] value) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(value);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Serializable result = (Serializable)ois.readObject();
        ois.close();
        return result;
    }
}
