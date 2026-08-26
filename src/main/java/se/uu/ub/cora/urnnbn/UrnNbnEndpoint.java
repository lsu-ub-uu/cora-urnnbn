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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.urnnbn.dependency.UrnNbnInstanceProvider;
import se.uu.ub.cora.urnnbn.internal.FetchOptions;
import se.uu.ub.cora.urnnbn.url.UrlHandler;

@Path("")
public class UrnNbnEndpoint {
	private static final String APPLICATION_XML = "application/xml";
	HttpServletRequest request;
	private Logger log = LoggerProvider.getLoggerForClass(UrnNbnEndpoint.class);
	private String urlPatternForUrnNbn;
	private UrlHandler urlHandler;

	public UrnNbnEndpoint(@Context HttpServletRequest req) {
		request = req;
		urlPatternForUrnNbn = SettingsProvider.getSetting("urlPatternForUrnNbn");
		urlHandler = UrnNbnInstanceProvider.getUrlHandler();
	}

	@GET
	@Path("all/{serie}")
	@Produces({ APPLICATION_XML })
	public Response readAllUrnNbn(@PathParam("serie") String serie,
			@QueryParam("start") @DefaultValue("0") int start,
			@QueryParam("rows") @DefaultValue("50000") int rows) {
		FetchOptions fetchOptions = new FetchOptions("urnnbn_all_records", serie, start, rows);
		String urnNbnRecordsAsXml = readUrnAsXml(fetchOptions);

		return createOkResponse(urnNbnRecordsAsXml);
	}

	private String readUrnAsXml(FetchOptions fetchOptions) {
		Reader reader = UrnNbnInstanceProvider.getReader();
		return reader.readUrnAsXml(buildUrlToRecord(), fetchOptions);
	}

	private String buildUrlToRecord() {
		String baseUrl = urlHandler.getBaseUrl(request);
		return baseUrl.concat(urlPatternForUrnNbn);
	}

	private Response createOkResponse(String urnNbnRecordsAsXml) {
		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, APPLICATION_XML)
				.entity(urnNbnRecordsAsXml).build();
	}

	@GET
	@Path("current/{serie}")
	@Produces({ APPLICATION_XML })
	public Response readCurrentUrnNbn(@PathParam("serie") String serie,
			@QueryParam("start") @DefaultValue("0") int start,
			@QueryParam("rows") @DefaultValue("5000") int rows) {
		FetchOptions fetchOptions = new FetchOptions("urnnbn_24h_records", serie, start, rows);
		String urnNbnRecordsAsXml = readUrnAsXml(fetchOptions);

		return createOkResponse(urnNbnRecordsAsXml);
	}
}
