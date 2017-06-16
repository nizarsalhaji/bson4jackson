// Copyright 2010-2016 Michel Kraemer
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ValueNode;

import org.litote.bson4jackson.BsonConstants;
import org.litote.bson4jackson.BsonParser;
import org.litote.bson4jackson.types.ObjectId;

/**
 * Deserializes BSON ObjectId objects
 * @author Michel Kraemer
 * @since 2.8.0
 */
public class BsonObjectIdDeserializer extends JsonDeserializer<ObjectId> {
	@Override
	@SuppressWarnings("deprecation")
	public ObjectId deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException {
		if (jp instanceof BsonParser) {
			BsonParser bsonParser = (BsonParser)jp;
			if (bsonParser.getCurrentToken() != JsonToken.VALUE_EMBEDDED_OBJECT ||
					bsonParser.getCurrentBsonType() != BsonConstants.TYPE_OBJECTID) {
				throw ctxt.mappingException(ObjectId.class);
			}
			return (ObjectId)bsonParser.getEmbeddedObject();
		} else {
			TreeNode tree = jp.getCodec().readTree(jp);
			int time = ((ValueNode)tree.get("$time")).asInt();
			int machine = ((ValueNode)tree.get("$machine")).asInt();
			int inc = ((ValueNode)tree.get("$inc")).asInt();
			return new ObjectId(time, machine, inc);
		}
	}
}
