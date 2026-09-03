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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.urnnbn.dependency.UrnNbnInstanceFactorySpy;
import se.uu.ub.cora.urnnbn.dependency.UrnNbnInstanceProvider;
import se.uu.ub.cora.urnnbn.internal.FetchOptions;

public class ReaderTest {
	private Reader reader;
	private UrnNbnInstanceFactorySpy urnNbnFactory;
	private FetcherSpy fetcherSpy;

	@BeforeMethod
	public void beforeMethod() {
		fetcherSpy = new FetcherSpy();
		urnNbnFactory = new UrnNbnInstanceFactorySpy();
		urnNbnFactory.MRV.setDefaultReturnValuesSupplier("factorFetcher", () -> fetcherSpy);
		UrnNbnInstanceProvider.onlyForTestSetUrnNbnInstanceFactory(urnNbnFactory);
		fetchOptions = new FetchOptions("someView", "someSerie", 0, 1000);

		setSettings();
		reader = new ReaderImp();
	}

	@AfterMethod
	private void afterMethod() {
		SettingsProvider.setSettings(null);
		// LoggerProvider.setLoggerFactory(null);
	}

	private void setSettings() {
		Map<String, String> urnNbnSettings = new HashMap<>();
		urnNbnSettings.put("urlPatternForUrnNbn", "/someclient/somerecordtype/%id%");
		SettingsProvider.setSettings(urnNbnSettings);
	}
	// TODO: Not sure if BaseUrl returns / on the final or not
	// private void setBaseUrl() {
	// UrlHandlerSpy urlHandlerSpy = new UrlHandlerSpy();
	// urlHandlerSpy.MRV.setDefaultReturnValuesSupplier("getBaseUrl",
	// () -> "https://somedomain.org");
	//
	// urnNbnFactory.MRV.setDefaultReturnValuesSupplier("factorUrlHandler", () -> urlHandlerSpy);
	// }
	//
	// private void setSettings() {
	// Map<String, String> urnNbnSettings = new HashMap<>();
	// urnNbnSettings.put("urlPatternForUrnNbn", "/someclient/somerecordtype/%id%");
	// SettingsProvider.setSettings(urnNbnSettings);
	// }

	@Test
	public void testParametersArePassedOnToUrnNbn() {
		reader.readUrnAsXml("someUrl", fetchOptions);

		FetcherSpy returnedUrnNbnSpy = (FetcherSpy) urnNbnFactory.MCR
				.getReturnValue("factorFetcher", 0);
		returnedUrnNbnSpy.MCR.assertParameters("getRecordsUsingFetchOptions", 0, fetchOptions);
	}

	@Test
	public void testSerieNoRecords() {
		setUpNoRecordsReturned(0);

		String urnAsXml = reader.readUrnAsXml("someUrl", fetchOptions);

		fetcherSpy.MCR.assertParameters("getRecordsUsingFetchOptions", 0, fetchOptions);

		assertXmlWithoutFormatting(urnAsXml, responseEmpty);
	}

	String responseEmpty = """
			<records xmlns="urn:nbn:se:uu:ub:epc-schema:rs-location-mapping">
				<protocol-version>3.0</protocol-version>
			</records>""";

	@Test
	public void testReturnedIdAndUrnNbnAreTurnedIntoXML() {
		setUpNoRecordsReturned(1);

		String urnAsXml = reader.readUrnAsXml(
				"https://somedomain.org/someclient/somerecordtype/%id%", fetchOptions);

		fetcherSpy.MCR.assertParameters("getRecordsUsingFetchOptions", 0, fetchOptions);

		assertXmlWithoutFormatting(urnAsXml, responseOneRecord);
		assertEquals(urnAsXml, responseOneRecord);
	}

	String responseOneRecord = """
			<records xmlns="urn:nbn:se:uu:ub:epc-schema:rs-location-mapping">
				<protocol-version>3.0</protocol-version>
				<record>
					<header>
						<identifier>urnnbn-1</identifier>
						<destinations>
							<destination status="activated">
								<url>https://somedomain.org/someclient/somerecordtype/id-1</url>
							</destination>
						</destinations>
					</header>
				</record>
			</records>""";

	@Test
	public void testThreeRecordsReturned() {
		setUpNoRecordsReturned(3);

		String urnAsXml = reader.readUrnAsXml(
				"https://somedomain.org/someclient/somerecordtype/%id%", fetchOptions);

		fetcherSpy.MCR.assertParameters("getRecordsUsingFetchOptions", 0, fetchOptions);

		assertXmlWithoutFormatting(urnAsXml, responseThreeRecords);
		assertEquals(urnAsXml, responseThreeRecords);
	}

	String responseThreeRecords = """
			<records xmlns="urn:nbn:se:uu:ub:epc-schema:rs-location-mapping">
				<protocol-version>3.0</protocol-version>
				<record>
					<header>
						<identifier>urnnbn-1</identifier>
						<destinations>
							<destination status="activated">
								<url>https://somedomain.org/someclient/somerecordtype/id-1</url>
							</destination>
						</destinations>
					</header>
				</record>
				<record>
					<header>
						<identifier>urnnbn-2</identifier>
						<destinations>
							<destination status="activated">
								<url>https://somedomain.org/someclient/somerecordtype/id-2</url>
							</destination>
						</destinations>
					</header>
				</record>
				<record>
					<header>
						<identifier>urnnbn-3</identifier>
						<destinations>
							<destination status="activated">
								<url>https://somedomain.org/someclient/somerecordtype/id-3</url>
							</destination>
						</destinations>
					</header>
				</record>
			</records>""";
	private FetchOptions fetchOptions;

	private void setUpNoRecordsReturned(int numberofEl) {
		fetcherSpy.MRV.setDefaultReturnValuesSupplier("getRecordsUsingFetchOptions",
				() -> getListOfUrnNbns(numberofEl));
		urnNbnFactory.MRV.setDefaultReturnValuesSupplier("factorFetcher", () -> fetcherSpy);
	}

	private List<IdAndUrnNbn> getListOfUrnNbns(int numberOfElements) {
		List<IdAndUrnNbn> urnNbnList = new ArrayList<>();
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
