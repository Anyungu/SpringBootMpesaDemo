package com.mpesa.demo.mpesa.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpesa.demo.mpesa.facilities.MpesaFacility;

@Service
public class MpesaService {

	@Autowired
	private MpesaFacility mpesaFacility;

	// function to pushToClientPhone
	public void pushToClientPhone(String PhoneNumber, String amount) throws Exception {

		// if amount is System Generated, You can generate it here instead
		String amountSystem = "Call Function to generate amount";

		mpesaFacility.STKPushSimulationParentPay(PhoneNumber, amount);

	}

	//process Mpesa CallBack
	public void receiveDataFromMpesaCallback(HttpServletRequest request) throws Exception {

		// read the data in whatever method you like
		BufferedReader br = request.getReader();

		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		ObjectMapper objectMapper = new ObjectMapper();

		// map the data to a class or to JSON in whatever method you like
		JsonNode productNode = objectMapper.readTree(sb.toString());

		Integer confirm = 0;
		Integer parseInt = productNode.get("Body").get("stkCallback").get("ResultCode").asInt();
		String checkOut = productNode.get("Body").get("stkCallback").get("CheckoutRequestID").asText();
		String resultDesc = productNode.get("Body").get("stkCallback").get("ResultDesc").asText();

		// if Result Code is 0, payment was successful
		if (parseInt.equals(confirm)) {

			try {
				// extract success data
				// HashMaps or Dicts may be the best way to extract these data
				JsonNode jsonNodeArr = productNode.get("Body").get("stkCallback").get("CallbackMetadata").get("Item");

				ArrayList<?> convertValue = objectMapper.convertValue(jsonNodeArr, ArrayList.class);

				HashMap<?, ?> phoneObject = objectMapper.convertValue(convertValue.get(4), HashMap.class);

				HashMap<?, ?> receiptObject = objectMapper.convertValue(convertValue.get(1), HashMap.class);

				HashMap<?, ?> amountObject = objectMapper.convertValue(convertValue.get(0), HashMap.class);

				HashMap<?, ?> dateObject = objectMapper.convertValue(convertValue.get(3), HashMap.class);

				String phone = phoneObject.get("Value").toString();
				String receipt = receiptObject.get("Value").toString();
				Integer amount = (Integer) (amountObject.get("Value"));
				String date = dateObject.get("Value").toString();
				DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
				LocalDateTime time = LocalDateTime.parse(date, format);

				// perfom your success logic here e.g activate User account for some give
				// period, or send a success sms
				// Beware of multiple callbacks that may be sent by safaricom. ENSURE SUCCESS LOGIC IS ONLY PROCESSED ONCE
				// if success logic is long, I would advise scheduling sincetoo much response delay may trigger a similar callback from safaricom 
			} catch (Exception e) {

				e.printStackTrace();
			}

			// if Result Code was not 0
		} else {

			// perform your payment fail logic here e.g logging data that the payment failed
		}

	}

}
