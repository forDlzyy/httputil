package com.example.demo;

import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 * Created by qingbiao.zhu on 2018/08/31.
 */
public class RedisUtil {
    //1、互斥性，在任意时刻，只有一个客户端持有锁
    //2、不会发生死锁。即使有一个客户端在持有锁的期间崩溃而没有主动解锁，也能保证后续
    //其他客户端能加锁
    //3、具有容错性,具有大部分的redis正常节点运行，客户端没有加锁和解锁
    //4、解铃还需要系铃人。加锁和解锁必须是同一个客户端，客户端自己不能把别人的锁给解了

    private static final String LOCK_SUCCESS = "OK";

    private static final String SET_IF_NOT_EXIST = "NX";

    private static final String SET_WITH_EXPIRE_TIME = "PX";

    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 第一个为key，我们使用key来当锁，因为key是唯一的。
       第二个为value，我们传的是requestId，很多童鞋可能不明白，有key作为锁不就够了吗，为什么还要用到value？原因就是我们在上面讲到可靠性时，分布式锁要满足第四个条件解铃还须系铃人，通过给value赋值为requestId，我们就知道这把锁是哪个请求加的了，在解锁的时候就可以有依据。requestId可以使用UUID.randomUUID().toString()方法生成。
       第三个为nxxx，这个参数我们填的是NX，意思是SET IF NOT EXIST，即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作；
       第四个为expx，这个参数我们传的是PX，意思是我们要给这个key加一个过期的设置，具体时间由第五个参数决定。
       第五个为time，与第四个参数相呼应，代表key的过期时间。
     * @param jedis
     * @param lockKey
     * @param requstId
     * @param expireTime
     * @return
     */
    public static boolean tryGetDistributeLock(Jedis jedis,String lockKey,String requstId, int expireTime){
        String result = jedis.set(lockKey,requstId,SET_IF_NOT_EXIST,SET_WITH_EXPIRE_TIME,expireTime);
        if (LOCK_SUCCESS.equals(result)){
            return true;
        }
        return false;
    }

    /**
     * 解锁的方法
     * @param jedis
     * @param lockKey
     * @param requestId
     * @return
     */
    public static boolean releaseDistributeLock(Jedis jedis,String lockKey,String requestId){
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        if (RELEASE_SUCCESS.equals(result)){
            return true;
        }
        return false;
    }
}
