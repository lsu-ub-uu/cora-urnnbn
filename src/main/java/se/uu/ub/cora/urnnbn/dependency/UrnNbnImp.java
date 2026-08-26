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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.sqldatabase.Row;
import se.uu.ub.cora.sqldatabase.SqlDatabaseFactory;
import se.uu.ub.cora.sqldatabase.table.TableFacade;
import se.uu.ub.cora.sqldatabase.table.TableQuery;
import se.uu.ub.cora.urnnbn.IdAndUrnNbn;
import se.uu.ub.cora.urnnbn.UrnNbn;

public class UrnNbnImp implements UrnNbn {

	private SqlDatabaseFactory sqlDatabaseFactory;
	private static final String URN_VIEW = "urnnbn_all_records";

	public static UrnNbnImp createUrnNbnUsingSqlDatabaseFactory(
			SqlDatabaseFactory sqlDatabaseFactory) {
		return new UrnNbnImp(sqlDatabaseFactory);
	}

	private UrnNbnImp(SqlDatabaseFactory sqlDatabaseFactory) {
		this.sqlDatabaseFactory = sqlDatabaseFactory;
	}

	@Override
	public List<IdAndUrnNbn> getUsingSeriesStartAndRows(String serie, int start, int rows) {
		TableFacade tableFacade = sqlDatabaseFactory.factorTableFacade();
		TableQuery tableQuery = createTableQuerieForSerie(serie, start, rows);
		List<Row> rowsForQuery = tableFacade.readRowsForQuery(tableQuery);
		return parseRowsToIdAndUrnNbnList(rowsForQuery);
	}

	private TableQuery createTableQuerieForSerie(String serie, int start, int rows) {
		TableQuery tableQuery = sqlDatabaseFactory.factorTableQuery(URN_VIEW);
		tableQuery.addCondition("serie", serie);
		long startAslong = Long.valueOf(start) + 1;
		tableQuery.setFromNo(startAslong);
		tableQuery.setToNo(Long.valueOf(start) + Long.valueOf(rows));
		return tableQuery;
	}

	private List<IdAndUrnNbn> parseRowsToIdAndUrnNbnList(List<Row> rowsForQuery) {
		List<IdAndUrnNbn> urnList = new ArrayList<>();
		for (Row row : rowsForQuery) {
			IdAndUrnNbn idAndUrnNbn = getIdAndUrnNbnFromRow(row);
			urnList.add(idAndUrnNbn);
		}
		return urnList;
	}

	private IdAndUrnNbn getIdAndUrnNbnFromRow(Row row) {
		String id = (String) row.getValueByColumn("id");
		String urnnbn = (String) row.getValueByColumn("urnnbn");
		return new IdAndUrnNbn(id, urnnbn);
	}

	public SqlDatabaseFactory onlyForTestGetDatabaseFactory() {
		return sqlDatabaseFactory;
	}

}
