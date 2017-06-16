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

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.litote.bson4jackson.BsonConstants;
import org.litote.bson4jackson.BsonParser;

/**
 * Deserializes BSON date type objects to calendars
 * @author Michel Kraemer
 * @since 2.3.2
 */
public class BsonCalendarDeserializer extends JsonDeserializer<Calendar> {
	@Override
	@SuppressWarnings("deprecation")
	public Calendar deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException {
		if (jp instanceof BsonParser) {
			BsonParser bsonParser = (BsonParser)jp;
			if (bsonParser.getCurrentToken() != JsonToken.VALUE_EMBEDDED_OBJECT ||
					bsonParser.getCurrentBsonType() != BsonConstants.TYPE_DATETIME) {
				throw ctxt.mappingException(Date.class);
			}
			
			Object obj = bsonParser.getEmbeddedObject();
			if (obj == null) {
				return null;
			}
			
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date)obj);
			return cal;
		} else {
			Date date = new Date(jp.getLongValue());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		}
	}
}
