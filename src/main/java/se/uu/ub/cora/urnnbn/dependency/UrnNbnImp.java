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

import java.util.Set;

import se.uu.ub.cora.sqldatabase.SqlDatabaseFactory;
import se.uu.ub.cora.sqldatabase.table.TableFacade;
import se.uu.ub.cora.urnnbn.IdAndUrnNbn;
import se.uu.ub.cora.urnnbn.UrnNbn;

public class UrnNbnImp implements UrnNbn {

	private SqlDatabaseFactory sqlDatabaseFactory;

	public static UrnNbnImp createUrnNbnUsingSqlDatabaseFactory(
			SqlDatabaseFactory sqlDatabaseFactory) {
		return new UrnNbnImp(sqlDatabaseFactory);
	}

	private UrnNbnImp(SqlDatabaseFactory sqlDatabaseFactory) {
		this.sqlDatabaseFactory = sqlDatabaseFactory;
	}

	@Override
	public Set<IdAndUrnNbn> getUsingSeriesStartAndRows(String serie, int start, int rows) {
		TableFacade tableFacade = sqlDatabaseFactory.factorTableFacade();

		return null;
	}

}
