package io.hulk.dubbo.springfox.demo.springboot.web;

import java.util.Map;

import io.hulk.dubbo.springfox.core.annotations.RequestModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.hulk.dubbo.springfox.demo.springboot.model.Book;


/**
 * @author zhaojigang
 * @date 2018/5/16
 */
@Tag(name = "UserController",description = "user相关api")
@RestController
@RequestMapping("/user")
public class UserController {
    @Operation(summary = "普通")
    @RequestMapping(value = "/testCommonField", method = RequestMethod.POST)
    public String testCommonField(@RequestParam("name") String name, @RequestModel Map<String, Book> bookMap) {
        return name;
    }

    @Operation(summary = "普通2")
    @RequestMapping(value = "/testCommonField2", method = RequestMethod.POST)
    public String testCommonField2(@RequestParam("name") String name, @RequestBody Map<String, Book> bookMap) {
        return name;
    }

    @Operation(method = "Object")
    @RequestMapping(value = "/testObject", method = RequestMethod.POST)
    public Book testObject(@RequestModel(value = "book1") Book book1, @RequestModel(value = "book1") Book book2) {
        Book book = new Book();
        book.setId(99L);
        book.setTitle(book1.getTitle());
        book.setContent(book2.getContent());
        return book;
    }

    @Operation(summary = "測試無參")
    @RequestMapping(value = "/testEmptyField", method = RequestMethod.POST)
    public void testCommonField() {
        System.out.println("hello");
    }
}
