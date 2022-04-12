package io.hulk.dubbo.springfox.demo.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ImportResource({ "classpath:*.xml" })
//@ComponentScan({ "io.hulk.dubbo.springfox.demo.springboot", "io.hulk.dubbo.springfox.core" })
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
