/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.xml;

import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link EntityResolver} implementation that delegates to a {@link BeansDtdResolver}
 * and a {@link PluggableSchemaResolver} for DTDs and XML schemas, respectively.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 * @since 2.0
 * @see BeansDtdResolver
 * @see PluggableSchemaResolver
 */
public class DelegatingEntityResolver implements EntityResolver {

	/** Suffix for DTD files */
	public static final String DTD_SUFFIX = ".dtd";

	/** Suffix for schema definition files */
	public static final String XSD_SUFFIX = ".xsd";


	private final EntityResolver dtdResolver;

	private final EntityResolver schemaResolver;


	/**
	 * Create a new DelegatingEntityResolver that delegates to
	 * a default {@link BeansDtdResolver} and a default {@link PluggableSchemaResolver}.
	 * <p>Configures the {@link PluggableSchemaResolver} with the supplied
	 * {@link ClassLoader}.
	 * @param classLoader the ClassLoader to use for loading
	 * (can be {@code null}) to use the default ClassLoader)
	 */
	public DelegatingEntityResolver(@Nullable ClassLoader classLoader) {
		this.dtdResolver = new BeansDtdResolver();
		this.schemaResolver = new PluggableSchemaResolver(classLoader);
	}

	/**
	 * Create a new DelegatingEntityResolver that delegates to
	 * the given {@link EntityResolver EntityResolvers}.
	 * @param dtdResolver the EntityResolver to resolve DTDs with
	 * @param schemaResolver the EntityResolver to resolve XML schemas with
	 */
	public DelegatingEntityResolver(EntityResolver dtdResolver, EntityResolver schemaResolver) {
		Assert.notNull(dtdResolver, "'dtdResolver' is required");
		Assert.notNull(schemaResolver, "'schemaResolver' is required");
		this.dtdResolver = dtdResolver;
		this.schemaResolver = schemaResolver;
	}


	@Override
	@Nullable
	/**
	 * TODO 对不同的验证模式.Spring使用了不同的解析器解析。这里简单描述一下 原理
	 * TODO ，比如加载DTD类型的BeansDtdResolver的resolveEntity是直接截取systemld最后的xx.dtd
	 * TODO 然后去当前路径下寻找，而加载XSD类型的PluggableSchemaResolver类的resolveEntity是默认到
	 * TODO META-INF/Spring.schemas文件中找到systemid所对应的XSD文件并加载
	 */
	public InputSource resolveEntity(String publicId, @Nullable String systemId) throws SAXException, IOException {
		if (systemId != null) {
			if (systemId.endsWith(DTD_SUFFIX)) {
				//TODo 如果是dtd从这里解析 BeansDtdResolver
				return this.dtdResolver.resolveEntity(publicId, systemId);
			}
			else if (systemId.endsWith(XSD_SUFFIX)) {
				//TODO 通过调用 META-INF/Spring.schemas 解析 PluggableSchemaResolver
				return this.schemaResolver.resolveEntity(publicId, systemId);
			}
		}
		return null;
	}


	@Override
	public String toString() {
		return "EntityResolver delegating " + XSD_SUFFIX + " to " + this.schemaResolver +
				" and " + DTD_SUFFIX + " to " + this.dtdResolver;
	}

}
