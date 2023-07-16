package com.yiwen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class LsatemsApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(LsatemsApplication.class, args);
    }

}
