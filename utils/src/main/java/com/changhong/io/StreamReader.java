package com.changhong.io;

import com.changhong.exception.IOReadException;
import com.changhong.reflect.UClass;

import java.io.InputStream;

/**
 * 自定义的输入流读取器，扩展自 {@link InputStream} 类。
 * <p>
 * 该类封装了一个原始的输入流（`InputStream`），并提供了一个便利的 `call()` 方法，
 * 用于执行传入的回调函数并自动处理资源关闭，避免手动管理流的关闭操作。
 * <p>
 * 使用时，可以通过 `call()` 方法执行指定操作，操作执行完成后，输入流将自动关闭。
 *
 * @author Luo Tiansheng
 */
public class StreamReader {

    private final InputStream stream;

    /**
     * 回调接口，用于执行自定义的流处理操作。
     */
    public interface StreamReaderResource {
        /**
         * 自定义的流处理操作方法。
         *
         * @param stream 当前的 InputStream 实例
         * @throws Exception 如果处理过程中出现异常
         */
        void call(InputStream stream) throws Exception;
    }

    /**
     * 构造方法，初始化 StreamReader。
     *
     * @param stream 原始的 {@link InputStream} 实例
     */
    public StreamReader(InputStream stream) {
        this.stream = stream;
    }

    /**
     * 执行指定的流处理操作，并确保在操作完成后关闭输入流。
     * <p>
     * 该方法会自动关闭流，避免了手动关闭的麻烦，且对异常进行了封装。
     *
     * @param streamReaderResource 自定义的流处理回调接口
     */
    public void call(StreamReaderResource streamReaderResource) {
        try {
            streamReaderResource.call(stream);
        } catch (Exception e) {
            throw new IOReadException(e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    /**
     * 根据资源名称返回一个 {@link StreamReader} 实例，用于读取指定资源文件。
     * <p>
     * 该方法通过 `ClassLoader` 加载指定名称的资源，并将返回的输入流包装成 {@link StreamReader}，
     * 使得用户可以方便地进行流操作，并自动管理资源的关闭。
     *
     * @param name 资源的名称（相对于类路径）
     * @return 一个 {@link StreamReader} 实例，用于读取指定资源文件
     */
    public static StreamReader withResource(String name) {
        return new StreamReader(UClass.classLoader.getResourceAsStream(name));
    }

}
