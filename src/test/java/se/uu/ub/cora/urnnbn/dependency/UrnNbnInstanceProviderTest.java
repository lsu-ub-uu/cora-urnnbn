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

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.urnnbn.Fetcher;
import se.uu.ub.cora.urnnbn.Reader;
import se.uu.ub.cora.urnnbn.url.UrlHandler;

public class UrnNbnInstanceProviderTest {
	private UrnNbnInstanceFactorySpy factory;

	@BeforeMethod
	private void beforeMethod() {
		UrnNbnInstanceProvider.onlyForTestResetUrnNbnInstanceFactory();
	}

	@AfterMethod
	private void afterMethod() {
		UrnNbnInstanceProvider.onlyForTestResetUrnNbnInstanceFactory();
	}

	private void setSpyFactory() {
		factory = new UrnNbnInstanceFactorySpy();
		UrnNbnInstanceProvider.onlyForTestSetUrnNbnInstanceFactory(factory);
	}

	@Test
	public void testDefaultInstanceFactoryIsSet() {
		UrnNbnInstanceFactory defaultFactory = UrnNbnInstanceProvider
				.onlyForTestGetUrnNbnInstanceFactory();
		assertTrue(defaultFactory instanceof UrnNbnInstanceFactoryImp);
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet")
	public void testPrivateConstructor() throws Exception {
		Constructor<UrnNbnInstanceProvider> constructor = UrnNbnInstanceProvider.class
				.getDeclaredConstructor();
		Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet", expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<UrnNbnInstanceProvider> constructor = UrnNbnInstanceProvider.class
				.getDeclaredConstructor();
		Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet")
	public void testGetReader() {
		setSpyFactory();
		Reader reader = UrnNbnInstanceProvider.getReader();

		factory.MCR.assertReturn("factorReader", 0, reader);
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet")
	public void testGetFetcher() {
		setSpyFactory();
		Fetcher uh = UrnNbnInstanceProvider.getFetcher();

		factory.MCR.assertReturn("factorFetcher", 0, uh);
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet")
	public void testGetUrnHandler() {
		setSpyFactory();
		UrlHandler uh = UrnNbnInstanceProvider.getUrlHandler();

		factory.MCR.assertReturn("factorUrlHandler", 0, uh);
	}

}
