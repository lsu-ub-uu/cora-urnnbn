/*
 * Copyright 2025 Uppsala University Library
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

import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;
import se.uu.ub.cora.urnnbn.UrnNbn;
import se.uu.ub.cora.urnnbn.UrnNbnSpy;
import se.uu.ub.cora.urnnbn.spy.UrlHandlerSpy;
import se.uu.ub.cora.urnnbn.url.UrlHandler;

public class UrnNbnInstanceFactorySpy implements UrnNbnInstanceFactory {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public UrnNbnInstanceFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("factorUrnNbn", UrnNbnSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorUrlHandler", UrlHandlerSpy::new);
	}

	@Override
	public UrnNbn factorUrnNbn() {
		return (UrnNbn) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public UrlHandler factorUrlHandler() {
		return (UrlHandlerSpy) MCR.addCallAndReturnFromMRV();
	}
}
