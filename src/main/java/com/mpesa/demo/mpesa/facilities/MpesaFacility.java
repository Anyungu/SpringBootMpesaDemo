package com.mpesa.demo.mpesa.facilities;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


//this class is used to trigger Mpesa Push
@Service
public class MpesaFacility {

	String appKey = "XXXXXXXXXXXXXXXXXXXXXXXX";
	String appSecret = "XXXX";

	// function to get a short life access token
	private String authenticate() throws Exception {
		String app_key = appKey;
		String app_secret = appSecret;
		String appKeySecret = app_key + ":" + app_secret;
		byte[] bytes = appKeySecret.getBytes("ISO-8859-1");
		String encoded = Base64.getEncoder().encodeToString(bytes);

		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
				.url("https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials").get()
				.addHeader("authorization", "Basic " + encoded).addHeader("cache-control", "no-cache")

				.build();

		Response response = client.newCall(request).execute();
		JSONObject jsonObject = new JSONObject(response.body().string());

		return jsonObject.getString("access_token");
	}

	// function to push to client phone
	public Response STKPushSimulationParentPay(String payerPhoneNumber, String amount) throws Exception {

		String callbackUrl = "url where saf will send response data after a push eg https://127.0.0.1:30000/afterPush";

		SimpleDateFormat rightNowFormart = new SimpleDateFormat("yyyymmddhhmmss");
		String rightNow = rightNowFormart.format(new Date());

		String sum = "Business Short Code" + "XXXXXXX, some key" + rightNow;
		byte[] bytes = sum.getBytes("ISO-8859-1");
		String encoded = Base64.getEncoder().encodeToString(bytes);

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("BusinessShortCode", "000000 depending on your use case");
		jsonObject.put("Password", encoded);
		jsonObject.put("Timestamp", rightNow);
		jsonObject.put("TransactionType", "CustomerBuyGoodsOnline");
		jsonObject.put("Amount", amount);
		jsonObject.put("PhoneNumber", payerPhoneNumber);
		jsonObject.put("PartyA", payerPhoneNumber);
		jsonObject.put("PartyB", "unique to your app. Provided by saf");
		jsonObject.put("CallBackURL", callbackUrl);
		jsonObject.put("AccountReference", "Different");
		jsonObject.put("TransactionDesc", "subscription payment for plotsApp property");

		jsonArray.put(jsonObject);

		String requestJson = jsonArray.toString().replaceAll("[\\[\\]]", "");

		OkHttpClient client = new OkHttpClient();
		String url = " https://api.safaricom.co.ke/mpesa/stkpush/v1/processrequest";
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestJson);
		Request request = new Request.Builder().url(url).post(body).addHeader("content-type", "application/json")
				.addHeader("authorization", "Bearer " + authenticate()).addHeader("cache-control", "no-cache").build();

		Response response = client.newCall(request).execute();

		return response;
	}

}
