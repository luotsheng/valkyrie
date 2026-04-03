package com.changhong.io;

/* -------------------------------------------------------------------------------- *\
|*                                                                                  *|
|*    Copyright (C) 2019-2024 Luo Tiansheng All rights reserved.                    *|
|*                                                                                  *|
|*    Licensed under the Apache License, Version 2.0 (the "License");               *|
|*    you may not use this file except in compliance with the License.              *|
|*    You may obtain a copy of the License at                                       *|
|*                                                                                  *|
|*        http://www.apache.org/licenses/LICENSE-2.0                                *|
|*                                                                                  *|
|*    Unless required by applicable law or agreed to in writing, software           *|
|*    distributed under the License is distributed on an "AS IS" BASIS,             *|
|*    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.      *|
|*    See the License for the specific language governing permissions and           *|
|*    limitations under the License.                                                *|
|*                                                                                  *|
\* -------------------------------------------------------------------------------- */

import com.changhong.exception.IOReadException;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 文件输入流
 */
public class FileByteReader extends FileInputStream {

    public FileByteReader(String name) throws FileNotFoundException {
        super(name);
    }

    public FileByteReader(File file) throws FileNotFoundException {
        super(file);
    }

    public FileByteReader(FileDescriptor fdObj) {
        super(fdObj);
    }

    public interface FileByteReaderResource {
        void call(FileByteReader byteReader) throws Exception;
    }

    /**
     * #brief: 调用文件字节读取资源
     *
     * <p>该函数用于调用指定的 {@code FileByteReaderResource} 对象，并对其应用当前对象。
     * 在调用过程中，如果发生异常，会记录错误日志。
     * 函数执行完后，无论是否发生异常，都会尝试关闭当前对象的资源。
     * <p>
     * <h2>注意事项：</h2>
     * <ul>
     *   <li> 确保传入的 {@code fileByteReaderResource} 参数为非空且已经正确初始化。</li>
     *   <li> 异常被捕获后不会抛出，错误详情会记录在日志中。</li>
     *   <li> 使用 {@code IOUtils.closeQuietly(this)} 方法在 {@code finally} 块中关闭资源，
     *        不会抛出关闭异常。</li>
     * </ul>
     *
     * @param fileByteReaderResource
     *        需要应用的文件字节读取资源对象
     */
    public void call(FileByteReaderResource fileByteReaderResource) {
        try {
            fileByteReaderResource.call(this);
        } catch (Exception e) {
            throw new IOReadException(e);
        } finally {
            IOUtils.closeQuietly(this);
        }
    }

}
