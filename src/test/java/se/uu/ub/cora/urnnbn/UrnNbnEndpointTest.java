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

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.urnnbn.dependency.UrnNbnInstanceFactorySpy;
import se.uu.ub.cora.urnnbn.dependency.UrnNbnInstanceProvider;
import se.uu.ub.cora.urnnbn.internal.FetchOptions;
import se.uu.ub.cora.urnnbn.spy.HttpServletRequestSpy;
import se.uu.ub.cora.urnnbn.spy.UrlHandlerSpy;

public class UrnNbnEndpointTest {

	private static final String APPLICATION_XML = "application/xml";

	private HttpServletRequestSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;
	private UrnNbnEndpoint endpoint;
	private UrnNbnInstanceFactorySpy urnNbnFactory;
	// private FetcherSpy urnNbnSpy;
	private ReaderSpy readerSpy;

	// TODO: handle errors
	// - On exception respon 500 and log
	// - Gräns på hur många rows vill vi acceptera

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);

		urnNbnFactory = new UrnNbnInstanceFactorySpy();
		UrnNbnInstanceProvider.onlyForTestSetUrnNbnInstanceFactory(urnNbnFactory);

		readerSpy = new ReaderSpy();
		urnNbnFactory.MRV.setDefaultReturnValuesSupplier("factorReader", () -> readerSpy);

		setSettings();
		setBaseUrl();

		requestSpy = new HttpServletRequestSpy();
		endpoint = new UrnNbnEndpoint(requestSpy);
	}

	@AfterMethod
	private void afterMethod() {
		SettingsProvider.setSettings(null);
		LoggerProvider.setLoggerFactory(null);
	}

	// TODO: Not sure if BaseUrl returns / on the final or not
	private void setBaseUrl() {
		UrlHandlerSpy urlHandlerSpy = new UrlHandlerSpy();
		urlHandlerSpy.MRV.setDefaultReturnValuesSupplier("getBaseUrl",
				() -> "https://somedomain.org");

		urnNbnFactory.MRV.setDefaultReturnValuesSupplier("factorUrlHandler", () -> urlHandlerSpy);
	}

	private void setSettings() {
		Map<String, String> urnNbnSettings = new HashMap<>();
		urnNbnSettings.put("urlPatternForUrnNbn", "/someclient/somerecordtype/%id%");
		SettingsProvider.setSettings(urnNbnSettings);
	}

	@Test
	public void testReadOnlyOnce() {
		urnNbnFactory.MCR.assertNumberOfCallsToMethod("factorUrlHandler", 1);
	}

	@Test
	public void testAnnotationsForReadAllUrnNbn() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndParameters(endpoint.getClass(),
						"readAllUrnNbn", new Class<?>[] { String.class, int.class, int.class });

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "all/{serie}");
		annotationHelper.assertProducesAnnotation(APPLICATION_XML);

		annotationHelper.assertQueryParamAnnotationByNameAndPosition("start", 1);
		annotationHelper.assertDefaultVauleParamAnnotationByNameAndPosition("0", 1);

		annotationHelper.assertQueryParamAnnotationByNameAndPosition("rows", 2);
		annotationHelper.assertDefaultVauleParamAnnotationByNameAndPosition("50000", 2);
	}

	@Test
	public void testAnnotationsForReadCurrentUrnNbn() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndParameters(endpoint.getClass(),
						"readCurrentUrnNbn", new Class<?>[] { String.class, int.class, int.class });

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "current/{serie}");
		annotationHelper.assertProducesAnnotation(APPLICATION_XML);

		annotationHelper.assertQueryParamAnnotationByNameAndPosition("start", 1);
		annotationHelper.assertDefaultVauleParamAnnotationByNameAndPosition("0", 1);

		annotationHelper.assertQueryParamAnnotationByNameAndPosition("rows", 2);
		annotationHelper.assertDefaultVauleParamAnnotationByNameAndPosition("5000", 2);
	}

	@Test
	public void testParametersArePassedOnToReadAllUrnNbn() {
		endpoint.readAllUrnNbn("someSerie", 10, 500);

		readerSpy.MCR.assertParameters("readUrnAsXml", 0,
				"https://somedomain.org/someclient/somerecordtype/%id%");

		FetchOptions fetchOptions = new FetchOptions("urnnbn_all_records", "someSerie", 10, 500);
		readerSpy.MCR.assertParameterAsEqual("readUrnAsXml", 0, "fetchOptions", fetchOptions);
	}

	@Test
	public void testParametersArePassedOnToUrnNbn() {
		endpoint.readCurrentUrnNbn("someSerie", 10, 500);

		readerSpy.MCR.assertParameters("readUrnAsXml", 0,
				"https://somedomain.org/someclient/somerecordtype/%id%");

		FetchOptions fetchOptions = new FetchOptions("urnnbn_24h_records", "someSerie", 10, 500);
		readerSpy.MCR.assertParameterAsEqual("readUrnAsXml", 0, "fetchOptions", fetchOptions);
	}

	@Test
	public void testReadAllOkResponse() {
		Response response = endpoint.readAllUrnNbn("someSerie", 0, 50);

		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
		readerSpy.MCR.assertReturn("readUrnAsXml", 0, response.getEntity().toString());
	}

	@Test
	public void testReadCurrentOkResponse() {
		Response response = endpoint.readCurrentUrnNbn("someSerie", 0, 50);

		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
		readerSpy.MCR.assertReturn("readUrnAsXml", 0, response.getEntity().toString());
	}

}
