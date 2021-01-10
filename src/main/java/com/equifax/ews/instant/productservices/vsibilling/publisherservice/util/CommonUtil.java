package com.equifax.ews.instant.productservices.vsibilling.publisherservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.equifax.ews.instant.productservices.vsibilling.publisherservice.constants.BillingPublisherConstants.UTC_DATE_FORMAT;


public class CommonUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

	public static String objectToJson(Object obj){
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(UTC_DATE_FORMAT));
		if(obj!=null){
			try {
				return mapper.writeValueAsString(obj);
			} catch (JsonProcessingException e) {
				LOGGER.error("Exception", e);
			}
		}
		return null;
	}

	public static JsonNode stringToJson(String obj){
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(UTC_DATE_FORMAT));
		if(obj!=null){
			try {
				return mapper.readTree(obj);
			} catch (IOException ioe) {
				LOGGER.error("IOException", ioe);
			}
		}
		return null;
	}
	
	public static <T> T stringJsonToObject(String obj, Class<T> cls){
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setDateFormat(new SimpleDateFormat(UTC_DATE_FORMAT));
		if(obj!=null){
			try {
				return mapper.readValue(obj,cls);
			} catch (Exception e) {
				LOGGER.error("Exception", e);
			}
		}
		return null;
	}


	public static String getCurrentUTCDateString() {
		SimpleDateFormat sdf = new SimpleDateFormat(UTC_DATE_FORMAT, Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date());
	}

}
