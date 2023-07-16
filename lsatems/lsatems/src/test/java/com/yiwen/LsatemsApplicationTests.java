package com.yiwen;


import com.yiwen.dao.UserLoginDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@SpringBootTest
class LsatemsApplicationTests {

/*    @Autowired
    private UserLoginDao userLoginDao;*/

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testSelectAll()
    {
//        QueryWrapper<UserLogin> q = new QueryWrapper<>();
//        List<UserLogin> userDetails = userLoginDao.getAll();
//        System.out.println(userDetails);
    }

    @Test
    void testRedis()
    {
        String k1 = stringRedisTemplate.opsForValue().get("k1");
        System.out.println(k1);
    }
}
