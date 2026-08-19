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

import java.util.LinkedHashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.urnnbn.dependency.UrnNbnInstanceFactorySpy;
import se.uu.ub.cora.urnnbn.dependency.UrnNbnInstanceProvider;
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

	private HttpServletRequestSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;
	private UrnNbnEndpoint endpoint;
	private UrnNbnInstanceFactorySpy urnNbnFactory;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);

		urnNbnFactory = new UrnNbnInstanceFactorySpy();
		UrnNbnInstanceProvider.onlyForTestSetUrnNbnInstanceFactory(urnNbnFactory);

		requestSpy = new HttpServletRequestSpy();
		endpoint = new UrnNbnEndpoint(requestSpy);
	}

	@Test
	public void testAnnotationsForCreateRecordJsonJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndParameters(endpoint.getClass(),
						"readUrnNbn", new Class<?>[] { String.class, int.class, int.class });

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{serie}");
		annotationHelper.assertProducesAnnotation(APPLICATION_XML);

		annotationHelper.assertQueryParamAnnotationByNameAndPosition("start", 1);
		annotationHelper.assertDefaultVauleParamAnnotationByNameAndPosition("0", 1);

		annotationHelper.assertQueryParamAnnotationByNameAndPosition("rows", 2);
		annotationHelper.assertDefaultVauleParamAnnotationByNameAndPosition("50000", 2);
	}

	@Test
	public void testSerieNotFound() {

		Response response = endpoint.readUrnNbn("notFoundSerie", 0, 50);
	}

	@Test
	public void testParametersArePassedOnToUrnNbn() {
		endpoint.readUrnNbn("someSerie", 10, 500);

		UrnNbnSpy urnNbnSpy = (UrnNbnSpy) urnNbnFactory.MCR.getReturnValue("factorUrnNbn", 0);
		urnNbnSpy.MCR.assertParameters(
				"getUrnNbnFromLatestRecordsCreatedUsingRecordTypeStartAndRows", 0, "someSerie", 10,
				500);
	}

	String emptyResponse = """
			<records xmlns="urn:nbn:se:uu:ub:epc-schema:rs-location-mapping">
				<protocol-version>3.0</protocol-version>
				<record>
					<header>
						<identifier>urnnbn-1</identifier>
						<destinations>
							<destination status="activated">
								<url>https://nordiskamuseet.diva-portal.org/divaclient/diva-output/1</url>
							</destination>
						</destinations>
					</header>
				</record>
			</records>""";

	@Test
	public void testReturnedIdAndUrnNbnAreTurnedIntoXML() {
		int numberOfElements = 1;
		UrnNbnSpy urnNbnSpy = new UrnNbnSpy();
		urnNbnSpy.MRV.setDefaultReturnValuesSupplier(
				"getUrnNbnFromLatestRecordsCreatedUsingRecordTypeStartAndRows",
				() -> getListOfUrnNbns(numberOfElements));
		urnNbnFactory.MRV.setDefaultReturnValuesSupplier("factorUrnNbn", () -> urnNbnSpy);

		Response response = endpoint.readUrnNbn("someSerie", 0, 1);
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
		assertEquals(response.getEntity(), emptyResponse);

	}

	private Set<IdAndUrnNbn> getListOfUrnNbns(int numberOfElements) {
		Set<IdAndUrnNbn> urnNbnList = new LinkedHashSet<>();
		for (int i = 0; i < numberOfElements; i++) {
			IdAndUrnNbn idAndUrnNbnI = new IdAndUrnNbn("id-" + i, "urnnbn-" + i);
			urnNbnList.add(idAndUrnNbnI);
		}
		return urnNbnList;
	}

}
