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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.urnnbn.UrnNbn;
import se.uu.ub.cora.urnnbn.spy.sql.sql.SqlDatabaseFactorySpy;

public class UrnNbnTest {

	private UrnNbn urnnbn;
	// private DatabaseFacadeSpy databaseFacadeSpy;
	private SqlDatabaseFactorySpy sqlDatabaseFactory;

	@BeforeMethod
	public void beforeMethod() {

		// databaseFacadeSpy = new DatabaseFacadeSpy();
		sqlDatabaseFactory = new SqlDatabaseFactorySpy();
		urnnbn = UrnNbnImp.createUrnNbnUsingSqlDatabaseFactory(sqlDatabaseFactory);
	}

	@Test
	public void testInit() {
		urnnbn.getUsingSeriesStartAndRows(null, 0, 0);

		sqlDatabaseFactory.MCR.assertMethodWasCalled("factorTableFacade");

	}

}
