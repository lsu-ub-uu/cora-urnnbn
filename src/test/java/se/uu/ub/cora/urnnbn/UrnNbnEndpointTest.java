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

	private HttpServletRequestSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;
	private UrnNbnEndpoint endpoint;
	private UrnNbnInstanceFactorySpy urnNbnFactory;
	private UrnNbnSpy urnNbnSpy;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);

		urnNbnFactory = new UrnNbnInstanceFactorySpy();
		UrnNbnInstanceProvider.onlyForTestSetUrnNbnInstanceFactory(urnNbnFactory);
		urnNbnSpy = new UrnNbnSpy();

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
	public void testSerieNoRecords() {
		setUpNoRecordsReturned(0);

		Response response = endpoint.readUrnNbn("someSerie", 0, 50);

		urnNbnSpy.MCR.assertParameters(
				"getUrnNbnFromLatestRecordsCreatedUsingRecordTypeStartAndRows", 0, "someSerie", 0,
				50);
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());

		assertXmlWithoutFormatting(response.getEntity().toString(), responseEmpty);
		assertEquals(response.getEntity(), responseEmpty);
	}

	String responseEmpty = """
			<records xmlns="urn:nbn:se:uu:ub:epc-schema:rs-location-mapping">
				<protocol-version>3.0</protocol-version>
			</records>""";

	@Test
	public void testParametersArePassedOnToUrnNbn() {
		endpoint.readUrnNbn("someSerie", 10, 500);

		UrnNbnSpy returnedUrnNbnSpy = (UrnNbnSpy) urnNbnFactory.MCR.getReturnValue("factorUrnNbn",
				0);
		returnedUrnNbnSpy.MCR.assertParameters(
				"getUrnNbnFromLatestRecordsCreatedUsingRecordTypeStartAndRows", 0, "someSerie", 10,
				500);
	}

	@Test
	public void testReturnedIdAndUrnNbnAreTurnedIntoXML() {
		setUpNoRecordsReturned(1);

		Response response = endpoint.readUrnNbn("someSerie", 0, 50);

		urnNbnSpy.MCR.assertParameters(
				"getUrnNbnFromLatestRecordsCreatedUsingRecordTypeStartAndRows", 0, "someSerie", 0,
				50);
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());

		assertXmlWithoutFormatting(response.getEntity().toString(), responseOneRecord);
		assertEquals(response.getEntity(), responseOneRecord);
	}

	String responseOneRecord = """
			<records xmlns="urn:nbn:se:uu:ub:epc-schema:rs-location-mapping">
				<protocol-version>3.0</protocol-version>
				<record>
					<header>
						<identifier>urnnbn-1</identifier>
						<destinations>
							<destination status="activated">
								<url>https://someDomain.org/divaclient/diva-output/id-1</url>
							</destination>
						</destinations>
					</header>
				</record>
			</records>""";

	@Test
	public void testThreeRecordsReturned() {
		setUpNoRecordsReturned(3);

		Response response = endpoint.readUrnNbn("someSerie", 0, 50);

		urnNbnSpy.MCR.assertParameters(
				"getUrnNbnFromLatestRecordsCreatedUsingRecordTypeStartAndRows", 0, "someSerie", 0,
				50);
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
		assertXmlWithoutFormatting(response.getEntity().toString(), responseThreeRecords);
		assertEquals(response.getEntity(), responseThreeRecords);
	}

	String responseThreeRecords = """
			<records xmlns="urn:nbn:se:uu:ub:epc-schema:rs-location-mapping">
				<protocol-version>3.0</protocol-version>
				<record>
					<header>
						<identifier>urnnbn-1</identifier>
						<destinations>
							<destination status="activated">
								<url>https://someDomain.org/divaclient/diva-output/id-1</url>
							</destination>
						</destinations>
					</header>
				</record>
				<record>
					<header>
						<identifier>urnnbn-2</identifier>
						<destinations>
							<destination status="activated">
								<url>https://someDomain.org/divaclient/diva-output/id-2</url>
							</destination>
						</destinations>
					</header>
				</record>
				<record>
					<header>
						<identifier>urnnbn-3</identifier>
						<destinations>
							<destination status="activated">
								<url>https://someDomain.org/divaclient/diva-output/id-3</url>
							</destination>
						</destinations>
					</header>
				</record>
			</records>""";

	private void setUpNoRecordsReturned(int numberofEl) {
		urnNbnSpy.MRV.setDefaultReturnValuesSupplier(
				"getUrnNbnFromLatestRecordsCreatedUsingRecordTypeStartAndRows",
				() -> getListOfUrnNbns(numberofEl));
		urnNbnFactory.MRV.setDefaultReturnValuesSupplier("factorUrnNbn", () -> urnNbnSpy);
	}

	private Set<IdAndUrnNbn> getListOfUrnNbns(int numberOfElements) {
		Set<IdAndUrnNbn> urnNbnList = new LinkedHashSet<>();
		for (int i = 1; i <= numberOfElements; i++) {
			IdAndUrnNbn idAndUrnNbnI = new IdAndUrnNbn("id-" + i, "urnnbn-" + i);
			urnNbnList.add(idAndUrnNbnI);
		}
		return urnNbnList;
	}

	private void assertXmlWithoutFormatting(String returnedXml, String expectedXml) {
		assertEquals(removeXmlIndent(returnedXml), removeXmlIndent(expectedXml));
	}

	String removeXmlIndent(String xml) {
		return xml.replaceAll("\n", "").replaceAll("\t", "");
	}

}
