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

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import se.uu.ub.cora.urnnbn.dependency.UrnNbnInstanceProvider;
import se.uu.ub.cora.urnnbn.internal.FetchOptions;

public class ReaderImp implements Reader {
	HttpServletRequest request;
	private String urlToClient;

	@Override
	public String readUrnAsXml(String urlToClient, FetchOptions fetchOptions) {
		this.urlToClient = urlToClient;
		Fetcher urnNbn = UrnNbnInstanceProvider.getFetcher();
		List<IdAndUrnNbn> urnNbnList = urnNbn.getRecordsUsingFetchOptions(fetchOptions);
		return parseToXml(urnNbnList);
	}

	private String parseToXml(List<IdAndUrnNbn> urnList) {
		StringBuilder urnNbnRecords = new StringBuilder();
		for (IdAndUrnNbn idAndUrnNbn : urnList) {
			String url = urlToClient.replace("%id%", idAndUrnNbn.id());
			String recordAsXml = getRecordXmlPartForSet(idAndUrnNbn.urnnbn(), url);
			urnNbnRecords.append(recordAsXml);
		}
		String urnNbnRecordsAsXml = """
				<records xmlns="urn:nbn:se:uu:ub:epc-schema:rs-location-mapping">
					<protocol-version>3.0</protocol-version>%s
				</records>""";
		return urnNbnRecordsAsXml.formatted(urnNbnRecords);
	}

	String getRecordXmlPartForSet(String urnnbn, String url) {
		String urnNbnRecordAsXml = """
				\n\t<record>
						<header>
							<identifier>%s</identifier>
							<destinations>
								<destination status="activated">
									<url>%s</url>
								</destination>
							</destinations>
						</header>
					</record>""";
		return urnNbnRecordAsXml.formatted(urnnbn, url);
	}

}
