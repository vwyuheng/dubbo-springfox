package io.hulk.dubbo.springfox.demo.springboot.api;

import io.hulk.dubbo.springfox.demo.springboot.model.Book;
import io.hulk.dubbo.springfox.demo.springboot.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@Tag(name = "BookService",description = "Book相关的Dubbo服务")
public interface BookService {

    @Operation(summary = "管理员添加用户1",
            description = "根据姓名添加用户并返回",
            parameters = {
                    @Parameter(name = "name", description = "姓名")
            },
            responses = {
                    @ApiResponse(description = "返回添加的用户", content = @Content(mediaType = "application/json", schema = @Schema(anyOf = {Book.class}))),
                    @ApiResponse(responseCode = "400", description = "返回400时候错误的原因")
            }
    )
    Book testCommonField(@Parameter(description = "书籍", required = true) Book book,
                         @Parameter(description = "用户") User user,
                         @Parameter(description = "书题") String title,
                         @Parameter(description = "数量", required = true, example = "1") int count);

    @Operation(summary = "测试一层泛型")
    String testGenericField(@Parameter List<Book> books);

    @Operation(summary = "测试嵌套泛型")
    String testNestingGenericField(@Parameter List<List<Book>> books);

    @Operation(summary = "测试Map")
    Map<String, Book> testMapFieldAndResp(@Parameter Map<String, Book> title2Book);

    @Operation(description = "测试方法重载，使用nickName进行区分访问uri", method = "byBookAndTitle")
    Book testCommonField(@Parameter Book book, @Parameter(description = "书题") String title);

    @Operation(description = "测试参数为空+返回为空")
    void testVoidReturn();

    @Operation(summary = "测试Long")
    Long testLongField(@Parameter(name = "id") Long id);

    @Operation(description = "测试接口抛异常")
    Book testObjectField(Book book1, Book book2) throws ArrayIndexOutOfBoundsException, NumberFormatException;
}
