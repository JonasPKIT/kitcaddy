package dk.kvalitetsit.kitcaddy.test;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dk.kvalitetsit.kitcaddy.AbstractIntegrationTest;

/**
 * 
 *    This testsetup
 * 
 *    | oio idws wsc |    ->    | oio idws wsp |     ->     | echo service |
 *
 */
public class OioIdwsRestWscIntegrationTest extends AbstractIntegrationTest {

	
	private static final String WSC_SERVICE_HOST = "wsc";
	private static final int WSC_SERVICE_PORT = 8686;
	private static final String WSC_SERVICE_URL = WSC_SERVICE_HOST+":"+WSC_SERVICE_PORT;

	private static final String WSP_SERVICE_HOST = "testserviceaa";
	private static final int WSP_SERVICE_PORT = 8443;
	private static final String WSP_SERVICE_URL = WSP_SERVICE_HOST+":"+WSP_SERVICE_PORT;

	@Rule
	public GenericContainer<?> wsc = createWsc();

	@Rule
	public GenericContainer<?> wsp = createWsp();

	public static GenericContainer<?> createWsc() {
		return 	getKitCaddyContainer(WSC_SERVICE_HOST, WSC_SERVICE_PORT, getDockerNetwork(), "wsc/wsc.config");
	}

	public static GenericContainer<?> createWsp() {
		return 	getKitCaddyContainer(WSP_SERVICE_HOST, WSP_SERVICE_PORT, getDockerNetwork(), "wsp/wsp.config");
	}

	@Test
	public void testGetServiceResponseThroughWsc() throws JsonMappingException, JsonProcessingException {

		// Given
		String echoUrl = getWscServiceUrl()+"/echo";

		// When
		ResponseEntity<String> echoResponse = restTemplate.getForEntity(echoUrl, String.class);

		// Then
		Assert.assertNotNull(echoResponse);
		JsonNode responseParsed = new ObjectMapper().readValue(echoResponse.getBody(), JsonNode.class);
		Assert.assertNotNull(responseParsed);
	}

	public String getWscServiceUrl() {
		return "http://"+wsc.getContainerIpAddress()+":"+wsc.getMappedPort(WSC_SERVICE_PORT);
	}
}
