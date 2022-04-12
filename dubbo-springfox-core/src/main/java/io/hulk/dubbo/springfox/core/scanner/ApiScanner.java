package io.hulk.dubbo.springfox.core.scanner;

/**
 * api scanner
 */
public interface ApiScanner<T, R> {
    R scanFromSpringContext(T t);
}
