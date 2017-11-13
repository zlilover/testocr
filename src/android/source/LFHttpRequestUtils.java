package com.sc.plugin;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFHttpRequestUtils {
	
	private static String sBaseUrl = "";

    public static void postDecodeCard(String appId, String appSecret, byte[] file,
                                      LFNetworkCallback callback) {

        LFApiParameterList parameterList = LFApiParameterList.create();
        parameterList.with("api_id", appId);
        parameterList.with("api_secret", appSecret);
        parameterList.with("file", file);

        postSyn(sBaseUrl, parameterList, callback);
    }

    private static void postSyn(String url, LFApiParameterList parameterList,
                                final LFNetworkCallback callback) {
        postDecodeSyn(url, parameterList, callback);
    }

    public static void initClient(String baseUrl) {
		sBaseUrl = baseUrl;
	}

	public static void postDecodeSyn(String url, LFApiParameterList parameterList,
                                     final LFNetworkCallback callback) {

		OkHttpClient okHttpClient;

		OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
		okhttpBuilder.connectTimeout(10, TimeUnit.SECONDS);
		okhttpBuilder.writeTimeout(10, TimeUnit.SECONDS);
		okhttpBuilder.readTimeout(10, TimeUnit.SECONDS);
		okHttpClient = okhttpBuilder.build();

		if (url == null || "".equals(url)) {
			sendFailResult(callback, 404, "URL无效");
			return;
		}

		Request.Builder builder = new Request.Builder();

		MultipartBody multipartBody = getRequestPOSTPara(parameterList);
		if (multipartBody != null) {
			builder.post(multipartBody);
		}

		try {
			builder.url(url);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		final Request request = builder.build();

		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				sendFailResult(callback, 404, "网络请求失败");
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				dealRequestResponse(response, callback);
			}
		});
	}

	private static void dealRequestResponse(Response response, final LFNetworkCallback callback) throws IOException {
		if (response != null) {
			int code = response.code();
			if (code == 200) {
				String result = response.body().string();
				if (result != null) {
					sendSuccessResult(callback, result);
				} else {
					sendFailResult(callback, 0, "");
				}
			} else {
				String result = response.body().string();
				sendFailResult(callback, code, response.message());
			}
		} else {
			sendFailResult(callback, 0, "");
		}
	}

	public static <T> void sendFailResult(final LFNetworkCallback callback, final int errorCode, final String errorString) {
		if (callback != null) {
			callback.failed(errorCode, errorString);
		}
	}

	public static void sendSuccessResult(final LFNetworkCallback callback, final String response) {

		if (callback != null) {
			callback.completed(response);
		}
	}

	private static MultipartBody getRequestPOSTPara(LFApiParameterList parameterList) {
		MultipartBody multipartBody = null;
		if (parameterList != null) {
			MultipartBody.Builder mulBuilder = new MultipartBody.Builder();
			for (LFApiParameter para : parameterList) {
				if (para.value != null) {
					if (para.value instanceof String) {
						mulBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + para.name + "\""),
								RequestBody.create(MediaType.parse("text/plain; charset=UTF-8"), (String) para.value));
					} else if (para.value instanceof Integer) {
						mulBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + para.name + "\""),
								RequestBody.create(MediaType.parse("text/plain; charset=UTF-8"),
										String.valueOf(para.value)));
					} else if (para.value instanceof byte[]) {
						RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),
								(byte[]) para.value);
						mulBuilder.addFormDataPart(para.name, para.name, fileBody);
					}
				}
			}
			mulBuilder.setType(MultipartBody.FORM);
			multipartBody = mulBuilder.build();
		}
		return multipartBody;
	}
}
