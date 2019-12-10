package com.mpesa.demo.mpesa.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpesa.demo.mpesa.services.MpesaService;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

//Implement Security Filter to reject any requests not From safaricom servers
@RestController
public class MpesaCallBackController {

	@Autowired
	private MpesaService mpesaService;

	// CallBack url. Avoid using name mpesa in this endpoint. use
	// HttpServeletRequest Since it is already a dynamic class

	@PostMapping(path = "/afterPush")
	public Response processPayment(HttpServletRequest request) throws Exception {

		mpesaService.receiveDataFromMpesaCallback(request);

		//Make Some sense here
		Response response = null;
		MediaType contentType = MediaType.get("application/json");
		String jsonObject = "Make Sense Here!";
		ResponseBody body = ResponseBody.create(contentType, jsonObject);
		return response.newBuilder().body(body).build();
	}

}
