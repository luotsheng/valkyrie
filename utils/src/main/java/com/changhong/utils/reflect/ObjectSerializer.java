package com.changhong.utils.reflect;

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

import com.changhong.utils.exception.SerializationException;
import com.changhong.utils.io.FileByteReader;
import com.changhong.utils.io.FileByteWriter;
import com.changhong.utils.io.UFile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * #brief: 提供对象的序列化和反序列化功能
 *
 * <p>该类包含静态方法，用于将对象序列化为文件，以及从文件反序列化对象。所有操作均使用 Java 的
 * 序列化机制进行，确保对象的状态能够持久化存储。
 *
 * <p>主要功能包括将对象保存到指定文件中，并从该文件加载对象，以便在不同的程序运行间共享
 * 或恢复对象状态。
 *
 * @author Luo Tiansheng
 * @see UFile
 */
public class ObjectSerializer {

    /**
     * #brief: 将对象序列化并写入文件
     *
     * <p>将给定的对象序列化为字节流，并写入指定的文件中。若序列化过程中发生错误，则会记录
     * 错误信息到日志中，方便后续调试和错误排查。
     *
     * @param object 要序列化的对象
     * @param mutableFile 要写入的文件
     */
    public static void serialize(Object object, UFile mutableFile) {
        try (FileByteWriter fileByteWriter = mutableFile.openByteWriter();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileByteWriter)) {
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * #brief: 从文件反序列化对象
     *
     * <p>从指定文件中读取字节流，并将其反序列化为对象。如果反序列化过程中出现错误，相关
     * 错误信息将被记录，并抛出 DeserializeException，以便调用者进行处理。
     *
     * @param mutableFile 要读取的文件
     * @return 反序列化得到的对象
     * @throws SerializationException 如果反序列化失败
     */
    public static Object deserialize(UFile mutableFile) {
        try (FileByteReader fileByteReader = mutableFile.openByteReader();
             ObjectInputStream objectInputStream = new ObjectInputStream(fileByteReader)) {
            return objectInputStream.readObject();
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

}