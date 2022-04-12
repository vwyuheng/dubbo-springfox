package io.hulk.dubbo.springfox.demo.springboot.model;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(title = "书籍")
public class Book {
    @Schema(name = "ID")
    private Long   id;
    @Schema(title = "书标题")
    private String title;
    @Schema(description = "书的内容")
    private String content;

    public Book() {
    }

    public Book(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
