/*
 * Copyright 2015, 2016, 2018, 2021, 2024, 2025 Uppsala University Library
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;

@Path("")
public class UrnNbnEndpoint {
	private static final String APPLICATION_XML = "application/xml";
	HttpServletRequest request;
	private Logger log = LoggerProvider.getLoggerForClass(UrnNbnEndpoint.class);

	public UrnNbnEndpoint(@Context HttpServletRequest req) {
		request = req;
	}

	@GET
	@Path("")
	@Produces({ APPLICATION_XML })
	public Response readUrnNbn() {
		String emptyResponse = """
				<records xmlns="urn:nbn:se:uu:ub:epc-schema:rs-location-mapping">
					<protocol-version>3.0</protocol-version>
					<record>
						<header>
							<identifier>urn:nbn:se:diva-2116</identifier>
							<destinations>
								<destination status="activated">
									<url>https://nordiskamuseet.diva-portal.org/divaclient/diva-output/2116</url>
								</destination>
							</destinations>
						</header>
					</record>
				</records>""";
		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, APPLICATION_XML)
				.entity(emptyResponse).build();
	}
}
