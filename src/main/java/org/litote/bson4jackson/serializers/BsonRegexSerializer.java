// Copyright 2010-2016 James Roper, Michel Kraemer
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

package org.litote.bson4jackson.serializers;

import java.io.IOException;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.litote.bson4jackson.BsonGenerator;

/**
 * Serializer for regular expression patterns
 * @since 1.3
 * @author James Roper
 * @author Michel Kraemer
 */
public class BsonRegexSerializer extends JsonSerializer<Pattern> {
	@Override
	public void serialize(Pattern value, JsonGenerator gen,
			SerializerProvider serializerProvider) throws IOException {
		if (value == null) {
			serializerProvider.defaultSerializeNull(gen);
		} else if (gen instanceof BsonGenerator) {
			BsonGenerator bgen = (BsonGenerator)gen;
			bgen.writeRegex(value);
		} else {
			gen.writeStartObject();
			gen.writeStringField("$pattern", value.pattern());
			gen.writeNumberField("$flags", value.flags());
			gen.writeEndObject();
		}
	}
}
