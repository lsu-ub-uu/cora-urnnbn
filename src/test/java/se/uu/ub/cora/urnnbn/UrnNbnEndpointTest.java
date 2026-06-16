/*
 * Copyright 2026 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.urnnbn;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.urnnbn.spy.HttpServletRequestSpy;

public class UrnNbnEndpointTest {

	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_XML_QS01 = "application/xml;qs=0.1";
	private static final String APPLICATION_VND_CORA_RECORD_XML = "application/vnd.cora.record+xml";
	private static final String APPLICATION_VND_CORA_RECORD_JSON = "application/vnd.cora.record+json";
	private static final String APPLICATION_VND_CORA_RECORD_JSON_QS09 = "application/vnd.cora.record+json;qs=0.9";
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String PLACE_0001 = "place:0001";
	private static final String PLACE = "place";
	private static final String AUTH_TOKEN = "authToken";

	// private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new
	// JsonToDataConverterFactorySpy();
	//
	// private RecordEndpointRead recordEndpoint;
	// private OldSpiderInstanceFactorySpy spiderInstanceFactorySpy;
	// private Response response;
	private HttpServletRequestSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;
	// private DataFactorySpy dataFactorySpy;
	//
	// private DataToJsonConverterFactoryCreatorSpy converterFactoryCreatorSpy;
	// private ConverterFactorySpy converterFactorySpy;
	// private StringToExternallyConvertibleConverterSpy stringToExternallyConvertibleConverterSpy;
	// private TheRestInstanceFactorySpy instanceFactory;
	private UrnNbnEndpoint endpoint;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		// dataFactorySpy = new DataFactorySpy();
		// DataProvider.onlyForTestSetDataFactory(dataFactorySpy);
		// setupUrlHandler();
		//
		// converterFactoryCreatorSpy = new DataToJsonConverterFactoryCreatorSpy();
		// DataToJsonConverterProvider
		// .setDataToJsonConverterFactoryCreator(converterFactoryCreatorSpy);
		//
		// stringToExternallyConvertibleConverterSpy = new
		// StringToExternallyConvertibleConverterSpy();
		// converterFactorySpy = new ConverterFactorySpy();
		// converterFactorySpy.MRV.setDefaultReturnValuesSupplier(
		// "factorStringToExternallyConvertableConverter",
		// () -> stringToExternallyConvertibleConverterSpy);
		// ConverterProvider.setConverterFactory("xml", converterFactorySpy);
		//
		// jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();
		// JsonToDataConverterProvider.setJsonToDataConverterFactory(jsonToDataConverterFactorySpy);
		// spiderInstanceFactorySpy = new OldSpiderInstanceFactorySpy();
		// SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);

		requestSpy = new HttpServletRequestSpy();
		endpoint = new UrnNbnEndpoint(requestSpy);
	}

	// private void setupUrlHandler() {
	// instanceFactory = new TheRestInstanceFactorySpy();
	// TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(instanceFactory);
	// }

	// @Test
	// public void testInit() {
	// recordEndpoint = new RecordEndpointRead(requestSpy);
	// }
	//

	@Test
	public void testInit() throws Exception {
		Response response = endpoint.readUrnNbn();

		assertEquals(response.getStatusInfo(), Response.Status.OK);
		assertEquals(response.getHeaderString(HttpHeaders.CONTENT_TYPE), APPLICATION_XML);

		String emptyResponse = """
				<records xmlns="urn:nbn:se:uu:ub:epc-schema:rs-location-mapping">
					<protocol-version>3.0</protocol-version>
					</records>""";
		assertEquals(response.getEntity(), emptyResponse);
	}

	@Test
	public void testWith() throws Exception {

	}

}
