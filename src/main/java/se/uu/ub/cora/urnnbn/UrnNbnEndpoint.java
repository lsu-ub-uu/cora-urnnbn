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

import java.util.Iterator;
import java.util.Set;

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
import se.uu.ub.cora.urnnbn.url.UrlHandler;

@Path("")
public class UrnNbnEndpoint {
	private static final String APPLICATION_XML = "application/xml";
	HttpServletRequest request;
	private Logger log = LoggerProvider.getLoggerForClass(UrnNbnEndpoint.class);
	private String urlToClient;

	public UrnNbnEndpoint(@Context HttpServletRequest req) {
		request = req;

	}

	@GET
	@Path("{serie}")
	@Produces({ APPLICATION_XML })
	public Response readUrnNbn(@PathParam("serie") String serie,
			@QueryParam("start") @DefaultValue("0") int start,
			@QueryParam("rows") @DefaultValue("50000") int rows) {
		UrnNbn urnNbn = UrnNbnInstanceProvider.getUrnNbn();
		Set<IdAndUrnNbn> urnNbnSet = urnNbn
				.getUrnNbnFromLatestRecordsCreatedUsingRecordTypeStartAndRows(serie, start, rows);

		// TODO: Clean up! Move to constructor
		UrlHandler urlHandler = UrnNbnInstanceProvider.getUrlHandler();
		String baseUrl = urlHandler.getBaseUrl(request);
		String urlPatternForUrnNbn = SettingsProvider.getSetting("urlPatternForUrnNbn");

		urlToClient = baseUrl.concat(urlPatternForUrnNbn);

		String xml = toXml(urnNbnSet);
		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, APPLICATION_XML)
				.entity(xml).build();
	}

	private String toXml(Set<IdAndUrnNbn> urnSet) {
		StringBuilder urnNbnRecord = new StringBuilder();
		for (Iterator<IdAndUrnNbn> iterator = urnSet.iterator(); iterator.hasNext();) {
			IdAndUrnNbn idAndUrnNbn = iterator.next();
			String url = urlToClient.replace("%id%", idAndUrnNbn.id());
			urnNbnRecord.append(getRecordsXmlPartForSet(idAndUrnNbn.urnnbn(), url));
		}
		String urnNbnRecordsAsXml = """
				<records xmlns="urn:nbn:se:uu:ub:epc-schema:rs-location-mapping">
					<protocol-version>3.0</protocol-version>%s
				</records>""";
		return urnNbnRecordsAsXml.formatted(urnNbnRecord);
	}

	String getRecordsXmlPartForSet(String urnnbn, String url) {
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

	// Check
	// https://git.epc.ub.uu.se/DiVA/urn-service/-/blob/master/src/main/java/diva/service/urn/NbnEndpoint.java
	//
	// @GET
	// @Produces({ TEXT_XML, TEXT_PLAIN })
	// @Path("current/{domain}")
	// public Response getCurrentNbn(@PathParam("domain") String domain,
	// @QueryParam("start") @DefaultValue("0") String start,
	// @QueryParam("rows") @DefaultValue("5000") String rows) {
	// LOGGER.info("Read current NBN of domain " + domain);
	// SolrQuery query = createSolrQuery(domain, start, rows);
	// setDateFilter(query);
	//
	// return createResponse(query);
	// }
	//
	// @GET
	// @Produces({ TEXT_XML, TEXT_PLAIN })
	// @Path("all/{domain}")
	// public Response getNbn(@PathParam("domain") String domain,
	// @QueryParam("start") @DefaultValue("0") String start,
	// @QueryParam("rows") @DefaultValue("50000") String rows) {
	// LOGGER.info("Read NBN of domain " + domain);
	// SolrQuery query = createSolrQuery(domain, start, rows);
	//
	// return createResponse(query);
	// }
	//
	// private SolrQuery createSolrQuery(String domain, String start, String rows) {
	// SolrQuery query = new SolrQuery();
	//
	// setStart(start, query);
	// setNumberOfRows(rows, query);
	// setDomainFilter(query, domain);
	//
	// query.setSortField("timestamp", SolrQuery.ORDER.desc);
	// query.setFields("PID,nbn,domain");
	// return query;
	// }
	//
	// private void setDateFilter(final SolrQuery query) {
	// final String timeRange = "[NOW/MINUTE-1DAY TO NOW/MINUTE]";
	// query.addFilterQuery("dateUpdated:" + timeRange + " OR datePublished:" + timeRange
	// + " OR timestamp:" + timeRange);
	// }

}
