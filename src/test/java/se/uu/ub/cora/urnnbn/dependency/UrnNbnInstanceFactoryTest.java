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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactoryImp;
import se.uu.ub.cora.urnnbn.url.UrlHandler;
import se.uu.ub.cora.urnnbn.url.UrlHandlerImp;

public class UrnNbnInstanceFactoryTest {
	private LoggerFactorySpy loggerFactorySpy;
	private UrnNbnInstanceFactory factory;

	@BeforeMethod
	public void setUp() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);

		factory = new UrnNbnInstanceFactoryImp();
	}

	@Test
	public void testFactorUrnNbn() {
		UrnNbnImp uh = (UrnNbnImp) factory.factorUrnNbn();

		assertTrue(uh.onlyForTestGetDatabaseFactory() instanceof SqlDatabaseFactoryImp);
	}

	@Test
	public void testDependenciesOfDatabaseFactory() {
		UrnNbnImp uh = (UrnNbnImp) factory.factorUrnNbn();

		SqlDatabaseFactoryImp dbFactory = (SqlDatabaseFactoryImp) uh
				.onlyForTestGetDatabaseFactory();

		assertEquals(dbFactory.onlyForTestGetLookupName(), "coraDatabaseLookupName");
	}

	@Test
	public void testFactorUrlHandler() {
		UrlHandler uh = factory.factorUrlHandler();

		assertTrue(uh instanceof UrlHandlerImp);
	}

}
