package org.litote.bson4jackson;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.litote.bson4jackson.serializers.BsonSerializers;
import org.litote.bson4jackson.types.JavaScript;
import org.litote.bson4jackson.types.ObjectId;
import org.litote.bson4jackson.types.Symbol;
import org.litote.bson4jackson.types.Timestamp;

/**
 * Test if properties that are serialized by {@link BsonSerializers} can
 * be buffered if the JSON type property is the last property in the
 * serialized object. We first specify a property that will keep the
 * type of the serialized object. We then specify a property order and
 * put this property at the very end. When deserializing Jackson will
 * need to buffer all properties in a
 * {@link com.fasterxml.jackson.databind.util.TokenBuffer} before it can
 * read the type and create the target object. This requires there are
 * appropriate serializers and deserializers for all properties that can
 * handle {@link BsonGenerator} and
 * {@link com.fasterxml.jackson.databind.util.TokenBuffer}.
 * @see <a href="https://github.com/michel-kraemer/bson4jackson/issues/67">Issue 67</a>
 * @author Michel Kraemer
 */
public class TypePropertyLastTest {
	@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type"
	)
	@JsonSubTypes({
		@JsonSubTypes.Type(name = "a", value = TypeAsPropertyA.class),
		@JsonSubTypes.Type(name = "b", value = TypeAsPropertyB.class)
	})
	// 'type' must be the last property to really test this feature!
	@JsonPropertyOrder({"uuid", "date", "calendar", "javaScript",
		"objectId", "pattern", "symbol", "timestamp", "type"})
	@SuppressWarnings("javadoc")
	public static abstract class TypeAsProperty {
		String type;
		UUID uuid;
		Date date;
		Calendar calendar;
		JavaScript javaScript;
		ObjectId objectId;
		Pattern pattern;
		Symbol symbol;
		Timestamp timestamp;
		
		public TypeAsProperty() {
			uuid = UUID.randomUUID();
			date = new Date();
			calendar = Calendar.getInstance();
			Map<String, Object> scope = new HashMap<>();
			scope.put("j", 5);
			javaScript = new JavaScript("var i;", scope);
			objectId = new ObjectId(1, 2, 3);
			pattern = Pattern.compile("[a-zA-Z0-9]+",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			symbol = new Symbol("foobar");
			timestamp = new Timestamp(100, 200);
		}
		
		public void setType(String type) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}
		
		public void setUUID(UUID uuid) {
			this.uuid = uuid;
		}
		
		public UUID getUUID() {
			return uuid;
		}
		
		public void setDate(Date date) {
			this.date = date;
		}
		
		public Date getDate() {
			return date;
		}
		
		public void setCalendar(Calendar calendar) {
			this.calendar = calendar;
		}
		
		public Calendar getCalendar() {
			return calendar;
		}
		
		public void setJavaScript(JavaScript javascript) {
			this.javaScript = javascript;
		}
		
		public JavaScript getJavaScript() {
			return javaScript;
		}
		
		public void setObjectId(ObjectId objectId) {
			this.objectId = objectId;
		}
		
		public ObjectId getObjectId() {
			return objectId;
		}
		
		public void setPattern(Pattern pattern) {
			this.pattern = pattern;
		}
		
		public Pattern getPattern() {
			return pattern;
		}
		
		public void setSymbol(Symbol symbol) {
			this.symbol = symbol;
		}
		
		public Symbol getSymbol() {
			return symbol;
		}
		
		public void setTimestamp(Timestamp timestamp) {
			this.timestamp = timestamp;
		}
		
		public Timestamp getTimestamp() {
			return timestamp;
		}
	}
	
	@SuppressWarnings("javadoc")
	public static class TypeAsPropertyA extends TypeAsProperty {
		TypeAsPropertyA() {
			super();
			type = "a";
		}
	}
	
	@SuppressWarnings("javadoc")
	public static class TypeAsPropertyB extends TypeAsProperty {
		TypeAsPropertyB() {
			super();
			type = "b";
		}
	}
	
	/**
	 * Serialize and deserialize an object of type {@link TypeAsPropertyA} and
	 * check if all properties are OK.
	 * @throws Exception if something goes wrong
	 */
	@Test
	@Category(value = RequiresJackson_v2_7.class)
	public void parse() throws Exception {
		ObjectMapper mapper = new ObjectMapper(new BsonFactory())
				.registerModule(new BsonModule());
		TypeAsPropertyA a = new TypeAsPropertyA();
		byte[] bytes = mapper.writeValueAsBytes(a);
		TypeAsProperty v = mapper.readValue(bytes,
				TypeAsProperty.class);
		assertEquals("a", v.getType());
		assertEquals(TypeAsPropertyA.class, v.getClass());
		assertEquals(a.getUUID(), v.getUUID());
		assertEquals(a.getDate(), v.getDate());
		assertEquals(a.getCalendar().getTimeInMillis(),
				v.getCalendar().getTimeInMillis());
		assertEquals(a.getJavaScript().getCode(), v.getJavaScript().getCode());
		assertEquals(a.getJavaScript().getScope(), v.getJavaScript().getScope());
		assertEquals(a.getObjectId().getTime(), v.getObjectId().getTime());
		assertEquals(a.getObjectId().getMachine(), v.getObjectId().getMachine());
		assertEquals(a.getObjectId().getInc(), v.getObjectId().getInc());
		assertEquals(a.getPattern().pattern(), v.getPattern().pattern());
		assertEquals(a.getPattern().flags(), v.getPattern().flags());
		assertEquals(a.getSymbol(), v.getSymbol());
		assertEquals(a.getTimestamp(), v.getTimestamp());
	}
}
