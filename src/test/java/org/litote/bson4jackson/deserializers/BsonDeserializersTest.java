// Copyright 2010-2014 Michel Kraemer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.litote.bson4jackson.deserializers;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;
import org.bson.types.Code;
import org.bson.types.CodeWScope;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.litote.bson4jackson.BsonFactory;
import org.litote.bson4jackson.BsonModule;
import org.litote.bson4jackson.types.JavaScript;
import org.litote.bson4jackson.types.ObjectId;
import org.litote.bson4jackson.types.Symbol;
import org.litote.bson4jackson.types.Timestamp;

/**
 * Tests {@link BsonDeserializers}
 * @author Michel Kraemer
 */
public class BsonDeserializersTest {
	/**
	 * A number of classes used to deserialize objects. All properties
	 * must be named 'obj'. Otherwise the beans cannot be deserialized
	 * correctly by Jackson's ObjectMapper.
	 */
	@SuppressWarnings("all")
	public static class TC {
		public static class C {
			public Calendar obj;
		};
		
		public static class D {
			public Date obj;
		};
		
		public static class J {
			public JavaScript obj;
		};
		
		public static class O {
			public ObjectId obj;
		};
		
		public static class R {
			public Pattern obj;
		};
		
		public static class S {
			public Symbol obj;
		};
		
		public static class T {
			public Timestamp obj;
		};
		
		public static class U {
			public UUID obj;
		};
	}
	
	private static <T> T generateAndParse(Object o, Class<T> cls) throws Exception {
		BSONObject bo = new BasicBSONObject();
		bo.put("obj", o); //that's why all properties of classes in TC must be named 'obj'
		BSONEncoder encoder = new BasicBSONEncoder();
		byte[] barr = encoder.encode(bo);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(barr);
		
		ObjectMapper om = new ObjectMapper(new BsonFactory());
		om.registerModule(new BsonModule());
		T r = om.readValue(bais, cls);
		return r;
	}
	
	/**
	 * Tests {@link BsonCalendarDeserializer}
	 * @throws Exception if something goes wrong
	 */
	@Test
	public void calendar() throws Exception {
		Date date = new Date();
		TC.C obj = generateAndParse(date, TC.C.class);
		assertEquals(date, obj.obj.getTime());
	}
	
	/**
	 * Tests {@link BsonDateDeserializer}
	 * @throws Exception if something goes wrong
	 */
	@Test
	public void date() throws Exception {
		Date date = new Date();
		TC.D obj = generateAndParse(date, TC.D.class);
		assertEquals(date, obj.obj);
	}
	
	/**
	 * Tests if {@code JavaScript} objects can be deserialized
	 * @throws Exception if something goes wrong
	 */
	@Test
	public void javascript() throws Exception {
		Code code = new Code("code");
		TC.J obj = generateAndParse(code, TC.J.class);
		assertEquals(code.getCode(), obj.obj.getCode());
		
		CodeWScope codews = new CodeWScope("code", new BasicBSONObject("key", "value"));
		obj = generateAndParse(codews, TC.J.class);
		assertEquals(code.getCode(), obj.obj.getCode());
		assertEquals(codews.getScope().toMap(), obj.obj.getScope());
	}
	
	/**
	 * Tests if {@code ObjectId} objects can be deserialized
	 * @throws Exception if something goes wrong
	 */
	@Test
	public void objectId() throws Exception {
		org.bson.types.ObjectId id = new org.bson.types.ObjectId(1, 2, 3);
		TC.O obj = generateAndParse(id, TC.O.class);
		assertEquals(id.getTimeSecond(), obj.obj.getTime());
		assertEquals(id.getMachine(), obj.obj.getMachine());
		assertEquals(id.getInc(), obj.obj.getInc());
	}
	
	/**
	 * Tests if {@code Pattern} objects can be deserialized
	 * @throws Exception if something goes wrong
	 */
	@Test
	public void regex() throws Exception {
		Pattern pat = Pattern.compile("[a-zA-Z0-9]+");
		TC.R obj = generateAndParse(pat, TC.R.class);
		assertEquals(pat.pattern(), obj.obj.pattern());
	}
	
	/**
	 * Tests if {@code Symbol} objects can be deserialized
	 * @throws Exception if something goes wrong
	 */
	@Test
	public void symbol() throws Exception {
		org.bson.types.Symbol sym = new org.bson.types.Symbol("symbol");
		TC.S obj = generateAndParse(sym, TC.S.class);
		assertEquals(sym.getSymbol(), obj.obj.getSymbol());
	}
	
	/**
	 * Tests if {@code Timestamp} objects can be deserialized
	 * @throws Exception if something goes wrong
	 */
	@Test
	public void timestamp() throws Exception {
		org.bson.types.BSONTimestamp ts = new org.bson.types.BSONTimestamp(1, 2);
		TC.T obj = generateAndParse(ts, TC.T.class);
		assertEquals(ts.getTime(), obj.obj.getTime());
		assertEquals(ts.getInc(), obj.obj.getInc());
	}
	
	/**
	 * Tests if {@code UUID} objects can be deserialized
	 * @throws Exception if something goes wrong
	 */
	@Test
	public void uuid() throws Exception {
		UUID uuid = UUID.randomUUID();
		TC.U obj = generateAndParse(uuid, TC.U.class);
		assertEquals(uuid, obj.obj);
	}
}
