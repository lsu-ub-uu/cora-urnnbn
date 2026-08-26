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

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.sqldatabase.Row;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactory;
import se.uu.ub.cora.urnnbn.IdAndUrnNbn;
import se.uu.ub.cora.urnnbn.spy.sql.sql.RowSpy;
import se.uu.ub.cora.urnnbn.spy.sql.sql.SqlDatabaseFactorySpy;
import se.uu.ub.cora.urnnbn.spy.sql.sql.TableQuerySpy;

public class UrnNbnTest {
	private static final String SERIE = "serie";
	private static final String URN_VIEW = "urnnbn_all_records";
	private static final String SERIE_EX = "someSerie";
	private UrnNbnImp urnnbn;
	private SqlDatabaseFactorySpy sqlDatabaseFactory;

	@BeforeMethod
	public void beforeMethod() {

		sqlDatabaseFactory = new SqlDatabaseFactorySpy();
		urnnbn = UrnNbnImp.createUrnNbnUsingSqlDatabaseFactory(sqlDatabaseFactory);
	}

	@Test
	public void testInit() {
		urnnbn.getUsingSeriesStartAndRows(SERIE_EX, 0, 0);

		sqlDatabaseFactory.MCR.assertMethodWasCalled("factorTableFacade");
		sqlDatabaseFactory.MCR.assertMethodWasCalled("factorTableQuery");
	}

	@Test
	public void testTableQuery() {
		urnnbn.getUsingSeriesStartAndRows(SERIE_EX, 0, 1000);

		TableQuerySpy tableQuery = (TableQuerySpy) sqlDatabaseFactory.MCR
				.assertCalledParametersReturn("factorTableQuery", URN_VIEW);

		tableQuery.MCR.assertCalledParameters("addCondition", SERIE, SERIE_EX);
		tableQuery.MCR.assertCalledParameters("setFromNo", 1L);
		tableQuery.MCR.assertCalledParameters("setToNo", 1000L);
	}

	@Test
	public void testTableQueryFrom() {
		urnnbn.getUsingSeriesStartAndRows(SERIE_EX, 1000, 1000);

		TableQuerySpy tableQuery = (TableQuerySpy) sqlDatabaseFactory.MCR
				.assertCalledParametersReturn("factorTableQuery", URN_VIEW);

		tableQuery.MCR.assertCalledParameters("addCondition", SERIE, SERIE_EX);
		tableQuery.MCR.assertCalledParameters("setFromNo", 1001L);
		tableQuery.MCR.assertCalledParameters("setToNo", 2000L);
	}

	@Test
	public void testCallTableFacade() {
		urnnbn.getUsingSeriesStartAndRows(SERIE_EX, 0, 1000);

		TableFacadeSpy tableFacade = (TableFacadeSpy) sqlDatabaseFactory.MCR
				.assertCalledParametersReturn("factorTableFacade");
		TableQuerySpy tableQuery = (TableQuerySpy) sqlDatabaseFactory.MCR
				.assertCalledParametersReturn("factorTableQuery", URN_VIEW);

		tableFacade.MCR.assertCalledParameters("readRowsForQuery", tableQuery);
	}

	@Test
	public void testParseRows() {
		TableFacadeSpy tableFacade = new TableFacadeSpy();
		List<Row> rows = creatRowsForSpy(2);

		tableFacade.MRV.setDefaultReturnValuesSupplier("readRowsForQuery", () -> rows);
		sqlDatabaseFactory.MRV.setDefaultReturnValuesSupplier("factorTableFacade",
				() -> tableFacade);

		List<IdAndUrnNbn> urnnbnRecordlist = urnnbn.getUsingSeriesStartAndRows(SERIE_EX, 0, 2);

		assertEquals(urnnbnRecordlist.size(), 2);
		assertEquals(urnnbnRecordlist.get(0).id(), "id-1");
		assertEquals(urnnbnRecordlist.get(0).urnnbn(), "urnnbn-1");
		assertEquals(urnnbnRecordlist.get(1).id(), "id-2");
		assertEquals(urnnbnRecordlist.get(1).urnnbn(), "urnnbn-2");
	}

	@Test
	public void testOnlyForTestGetDatabaseFactory() {
		SqlDatabaseFactory sqlDatabaseFactory2 = urnnbn.onlyForTestGetDatabaseFactory();
		AssertJUnit.assertSame(sqlDatabaseFactory2, sqlDatabaseFactory);
	}

	public List<Row> creatRowsForSpy(int noOfRows) {
		List<Row> list = new ArrayList<>();
		for (int i = 1; i <= noOfRows; i++) {
			final int index = i;
			RowSpy row = new RowSpy();
			row.MRV.setSpecificReturnValuesSupplier("getValueByColumn", () -> "id-" + index, "id");
			row.MRV.setSpecificReturnValuesSupplier("getValueByColumn", () -> "urnnbn-" + index,
					"urnnbn");
			list.add(row);
		}
		return list;
	}

}
