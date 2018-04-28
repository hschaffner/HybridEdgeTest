package com.solace.hybrid_edge_starter.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This processor strips header properties starting with JMS_Solace, 
 * as these can cause problems with some JMS providers.
 * 
 */
@Component
public class StripSolaceHeadersProcessor implements Processor {
	private static final Logger logger = LoggerFactory.getLogger(StripSolaceHeadersProcessor.class);
	
	private static final String SOLACE_JMS_HEADER_PREFIX = "JMS_Solace";
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		Map<String, Object> headers = message.getHeaders();
		List<String> propertiesToStrip = new ArrayList<>();
		
		for (String key : headers.keySet()) {
			if (key.startsWith(SOLACE_JMS_HEADER_PREFIX)) {
				propertiesToStrip.add(key);
			}
		}

		for (String key : propertiesToStrip) {
			logger.debug("Removing header: {} -> {}", key, message.getHeader(key));
			message.removeHeader(key);
		}
	}

}
