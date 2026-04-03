//package com.changhong.test;
//
///* -------------------------------------------------------------------------------- *\
//|*                                                                                  *|
//|*    Copyright (C) 2023 VCentGogh                                                  *|
//|*                                                                                  *|
//|*    This program is free software: you can redistribute it and/or modify          *|
//|*    it under the terms of the GNU General Public License as published by          *|
//|*    the Free Software Foundation, either version 3 of the License, or             *|
//|*    (at your option) any later version.                                           *|
//|*                                                                                  *|
//|*    This program is distributed in the hope that it will be useful,               *|
//|*    but WITHOUT ANY WARRANTY; without even the implied warranty of                *|
//|*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                 *|
//|*    GNU General Public License for more details.                                  *|
//|*                                                                                  *|
//|*    You should have received a copy of the GNU General Public License             *|
//|*    along with this program.  If not, see <https://www.gnu.org/licenses/>.        *|
//|*                                                                                  *|
//|*    This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.    *|
//|*    This is free software, and you are welcome to redistribute it                 *|
//|*    under certain conditions; type `show c' for details.                          *|
//|*                                                                                  *|
//\* -------------------------------------------------------------------------------- */
//
///* Creates on 2022/8/8. */
//
//import com.changhong.collection.Maps;
//import com.changhong.http.*;
//import com.changhong.io.IOUtils;
//import com.changhong.io.MutableFile;
//import org.karatsuba.http.*;
//import org.junit.Test;
//
//import java.util.concurrent.TimeUnit;
//
//@SuppressWarnings("ALL")
//public class HttpClientsTest {
//
//    // @Test
//    // public void callFormBodyTest() {
//    //     FormBodyBuilder formBodyBuilder = new FormBodyBuilder();
//    //     formBodyBuilder.put("document", new File("src/main/java/com/redgogh/tools/ArrayUtils.java"));
//    //     System.out.println(HttpClients.post("http://127.0.0.1:8001/security/check-file", formBodyBuilder));
//    // }
//
//    @Test
//    public void callMultipartBodyTest() {
//        MultipartBody multipartBody = new MultipartBody();
//        multipartBody.put("document", new MutableFile("src/main/java/com/redgogh/tools/ArrayUtils.java"));
//
//        Response response = HttpClient.open("POST", "http://127.0.0.1:8001/security/check/file")
//                .addRequestBody(multipartBody)
//                .newCall();
//
//        System.out.println(response);
//    }
//
//    @Test
//    public void callJSONBodyTest() {
//        Response response = HttpClient.open("POST", "http://127.0.0.1:8001/security/save")
//                .addRequestBody(Maps.fromVarargs("id", "12138", "name", "zs", "age", "18"))
//                .newCall();
//
//        System.out.println(response);
//    }
//
//    @Test
//    public void callGetTest() {
//        QueryArgumentsBuilder queryArgumentsBuilder = new QueryArgumentsBuilder();
//        queryArgumentsBuilder.putAll(Maps.fromVarargs("id", "12138", "name", "zs", "age", "18"));
//        Response response = HttpClient.open("GET", "http://127.0.0.1:8001/security/get")
//                .setQueryArgumentsBuilder(queryArgumentsBuilder)
//                .sslVerifierDisable()
//                .newCall();
//
//        System.out.println(response);
//    }
//
//    @Test
//    public void getUserTest() {
//        Response response = HttpClient.open("GET", "http://127.0.0.1:8001/testing/user")
//                .sslVerifierDisable()
//                .newCall();
//        System.out.println(response);
//    }
//
//    @Test
//    public void callAsyncTest() throws InterruptedException {
//        HttpClient.open("POST", "http://127.0.0.1:8001/testing/async-call")
//                .setQueryArgumentsBuilder(new QueryArgumentsBuilder("sleep=1"))
//                .setReadTimeout(3)
//                .newCall(new Callback() {
//                    @Override
//                    public void onFailure(Throwable e) {
//                        IOUtils.stdout.printf("请求出现异常：%s\n", e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(Response response) {
//                        IOUtils.stdout.printf("请求成功：%s\n", response);
//                    }
//                });
//        System.out.println("等待异步调用执行完成！");
//        Thread.sleep(TimeUnit.SECONDS.toMillis(5));
//        System.out.println("程序执行完毕！");
//    }
//
//    @Test
//    public void downloadFileTest() {
//        StreamResponse octet = HttpClient.open("GET", "https://repo.huaweicloud.com/java/jdk/8u202-b08-demos/jdk-8u202-windows-x64-demos.zip")
//                .sslVerifierDisable()
//                .newStreamCall();
//        octet.transferTo(new MutableFile("Desktop://jdk-8u202-windows-x64-demos.zip"));
//    }
//
//    @Test
//    public void asyncDownloadFileTest() throws InterruptedException {
//        HttpClient.open("GET", "https://repo.huaweicloud.com/java/jdk/8u202-b08-demos/jdk-8u202-windows-x64-demos.zip")
//                .sslVerifierDisable()
//                .newStreamCall(new StreamCallback() {
//                    @Override
//                    public void onFailure(Throwable e) {
//                        IOUtils.stdout.printf("请求出现异常：%s\n", e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(StreamResponse response) {
//                        response.transferTo(new MutableFile("Desktop://jdk-8u202-windows-x64-demos.zip"));
//                    }
//                });
//    }
//
//}
