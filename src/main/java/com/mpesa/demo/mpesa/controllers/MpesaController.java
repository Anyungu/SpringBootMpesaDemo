package com.mpesa.demo.mpesa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpesa.demo.mpesa.services.MpesaService;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

//implement Oauth2, JWT, Basic etc Security here
@RestController
public class MpesaController {

	@Autowired
	private MpesaService mpesaService;

	// Pick Phone and amount(if necesarry) from the Front End Dev Team

	@PostMapping(path = "/makePayment")
	public Response pmakePayment(String phone, String amount) throws Exception {

		mpesaService.pushToClientPhone(phone, amount);

		// Make Some sense here
		Response response = null;
		MediaType contentType = MediaType.get("application/json");
		String jsonObject = "Make Sense Here!";
		ResponseBody body = ResponseBody.create(contentType, jsonObject);
		return response.newBuilder().body(body).build();
	}

}
