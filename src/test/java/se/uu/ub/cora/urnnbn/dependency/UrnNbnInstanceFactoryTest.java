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

package se.uu.ub.cora.urnnbn.dependency;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactoryImp;
import se.uu.ub.cora.urnnbn.Reader;
import se.uu.ub.cora.urnnbn.ReaderImp;
import se.uu.ub.cora.urnnbn.internal.FetcherImp;
import se.uu.ub.cora.urnnbn.url.UrlHandler;
import se.uu.ub.cora.urnnbn.url.UrlHandlerImp;

public class UrnNbnInstanceFactoryTest {
	private LoggerFactorySpy loggerFactorySpy;
	private UrnNbnInstanceFactory factory;

	@BeforeMethod
	public void setUp() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);

		setNeededInitParameters();

		factory = new UrnNbnInstanceFactoryImp();
	}

	@AfterMethod
	private void afterMethod() {
		SettingsProvider.setSettings(null);
		LoggerProvider.setLoggerFactory(null);
	}

	private void setNeededInitParameters() {
		Map<String, String> urnNbnSettings = new HashMap<>();
		urnNbnSettings.put("coraDatabaseLookupName", "someLookupName");
		SettingsProvider.setSettings(urnNbnSettings);
	}

	@Test
	public void testFactorFetcher() {
		FetcherImp fetcher = (FetcherImp) factory.factorFetcher();

		assertTrue(fetcher.onlyForTestGetDatabaseFactory() instanceof SqlDatabaseFactoryImp);
	}

	@Test
	public void testFactorReader() {
		Reader reader = factory.factorReader();

		assertTrue(reader instanceof ReaderImp);
	}

	@Test
	public void testDependenciesOfDatabaseFactory() {
		FetcherImp uh = (FetcherImp) factory.factorFetcher();

		SqlDatabaseFactoryImp dbFactory = (SqlDatabaseFactoryImp) uh
				.onlyForTestGetDatabaseFactory();

		assertEquals(dbFactory.onlyForTestGetLookupName(), "someLookupName");
	}

	@Test
	public void testFactorUrlHandler() {
		UrlHandler uh = factory.factorUrlHandler();

		assertTrue(uh instanceof UrlHandlerImp);
	}

}