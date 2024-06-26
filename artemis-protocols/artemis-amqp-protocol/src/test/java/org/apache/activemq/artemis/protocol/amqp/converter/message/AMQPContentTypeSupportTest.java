/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.artemis.protocol.amqp.converter.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.activemq.artemis.protocol.amqp.converter.AMQPContentTypeSupport;
import org.apache.activemq.artemis.protocol.amqp.converter.AMQPMessageSupport;
import org.apache.activemq.artemis.protocol.amqp.exceptions.ActiveMQAMQPInvalidContentTypeException;
import org.junit.jupiter.api.Test;

public class AMQPContentTypeSupportTest {

   @Test
   public void testParseContentTypeWithOnlyType() throws Exception {
      assertThrows(ActiveMQAMQPInvalidContentTypeException.class, () -> {
         doParseContentTypeTestImpl("type", null);
      });
   }

   @Test
   public void testParseContentTypeEndsWithSlash() throws Exception {
      assertThrows(ActiveMQAMQPInvalidContentTypeException.class, () -> {
         doParseContentTypeTestImpl("type/", null);
      });
   }

   @Test
   public void testParseContentTypeMissingSubtype() throws Exception {
      assertThrows(ActiveMQAMQPInvalidContentTypeException.class, () -> {
         doParseContentTypeTestImpl("type/;", null);
      });
   }

   @Test
   public void testParseContentTypeEmptyString() throws Exception {
      assertThrows(ActiveMQAMQPInvalidContentTypeException.class, () -> {
         doParseContentTypeTestImpl("", null);
      });
   }

   @Test
   public void testParseContentTypeNullString() throws Exception {
      assertThrows(ActiveMQAMQPInvalidContentTypeException.class, () -> {
         doParseContentTypeTestImpl(null, null);
      });
   }

   @Test
   public void testParseContentTypeNoParamsAfterSeparatorNonTextual() throws Exception {
      // Expect null as this is not a textual type
      doParseContentTypeTestImpl("type/subtype;", null);
   }

   @Test
   public void testParseContentTypeNoParamsAfterSeparatorTextualType() throws Exception {
      doParseContentTypeTestImpl("text/plain;", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeEmptyParamsAfterSeparator() throws Exception {
      doParseContentTypeTestImpl("text/plain;;", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeNoParams() throws Exception {
      doParseContentTypeTestImpl("text/plain", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithCharsetUtf8() throws Exception {
      doParseContentTypeTestImpl("text/plain;charset=utf-8", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithCharsetAscii() throws Exception {
      doParseContentTypeTestImpl("text/plain;charset=us-ascii", StandardCharsets.US_ASCII);
   }

   @Test
   public void testParseContentTypeWithMultipleParams() throws Exception {
      doParseContentTypeTestImpl("text/plain; param=value; charset=us-ascii", StandardCharsets.US_ASCII);
   }

   @Test
   public void testParseContentTypeWithCharsetQuoted() throws Exception {
      doParseContentTypeTestImpl("text/plain;charset=\"us-ascii\"", StandardCharsets.US_ASCII);
   }

   @Test
   public void testParseContentTypeWithCharsetQuotedEmpty() throws Exception {
      assertThrows(ActiveMQAMQPInvalidContentTypeException.class, () -> {
         doParseContentTypeTestImpl("text/plain;charset=\"\"", null);
      });
   }

   @Test
   public void testParseContentTypeWithCharsetQuoteNotClosed() throws Exception {
      assertThrows(ActiveMQAMQPInvalidContentTypeException.class, () -> {
         doParseContentTypeTestImpl("text/plain;charset=\"unclosed", null);
      });
   }

   @Test
   public void testParseContentTypeWithCharsetQuoteNotClosedEmpty() throws Exception {
      assertThrows(ActiveMQAMQPInvalidContentTypeException.class, () -> {
         doParseContentTypeTestImpl("text/plain;charset=\"", null);
      });
   }

   @Test
   public void testParseContentTypeWithNoCharsetValue() throws Exception {
      assertThrows(ActiveMQAMQPInvalidContentTypeException.class, () -> {
         doParseContentTypeTestImpl("text/plain;charset=", null);
      });
   }

   @Test
   public void testParseContentTypeWithTextPlain() throws Exception {
      doParseContentTypeTestImpl("text/plain;charset=iso-8859-1", StandardCharsets.ISO_8859_1);
      doParseContentTypeTestImpl("text/plain;charset=us-ascii", StandardCharsets.US_ASCII);
      doParseContentTypeTestImpl("text/plain;charset=utf-8", StandardCharsets.UTF_8);
      doParseContentTypeTestImpl("text/plain", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithTextJson() throws Exception {
      doParseContentTypeTestImpl("text/json;charset=iso-8859-1", StandardCharsets.ISO_8859_1);
      doParseContentTypeTestImpl("text/json;charset=us-ascii", StandardCharsets.US_ASCII);
      doParseContentTypeTestImpl("text/json;charset=utf-8", StandardCharsets.UTF_8);
      doParseContentTypeTestImpl("text/json", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithTextHtml() throws Exception {
      doParseContentTypeTestImpl("text/html;charset=iso-8859-1", StandardCharsets.ISO_8859_1);
      doParseContentTypeTestImpl("text/html;charset=us-ascii", StandardCharsets.US_ASCII);
      doParseContentTypeTestImpl("text/html;charset=utf-8", StandardCharsets.UTF_8);
      doParseContentTypeTestImpl("text/html", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithTextFoo() throws Exception {
      doParseContentTypeTestImpl("text/foo;charset=iso-8859-1", StandardCharsets.ISO_8859_1);
      doParseContentTypeTestImpl("text/foo;charset=us-ascii", StandardCharsets.US_ASCII);
      doParseContentTypeTestImpl("text/foo;charset=utf-8", StandardCharsets.UTF_8);
      doParseContentTypeTestImpl("text/foo", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithApplicationJson() throws Exception {
      doParseContentTypeTestImpl("application/json;charset=iso-8859-1", StandardCharsets.ISO_8859_1);
      doParseContentTypeTestImpl("application/json;charset=us-ascii", StandardCharsets.US_ASCII);
      doParseContentTypeTestImpl("application/json;charset=utf-8", StandardCharsets.UTF_8);
      doParseContentTypeTestImpl("application/json", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithApplicationJsonVariant() throws Exception {
      doParseContentTypeTestImpl("application/something+json;charset=iso-8859-1", StandardCharsets.ISO_8859_1);
      doParseContentTypeTestImpl("application/something+json;charset=us-ascii", StandardCharsets.US_ASCII);
      doParseContentTypeTestImpl("application/something+json;charset=utf-8", StandardCharsets.UTF_8);
      doParseContentTypeTestImpl("application/something+json", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithApplicationJavascript() throws Exception {
      doParseContentTypeTestImpl("application/javascript;charset=iso-8859-1", StandardCharsets.ISO_8859_1);
      doParseContentTypeTestImpl("application/javascript;charset=us-ascii", StandardCharsets.US_ASCII);
      doParseContentTypeTestImpl("application/javascript;charset=utf-8", StandardCharsets.UTF_8);
      doParseContentTypeTestImpl("application/javascript", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithApplicationEcmascript() throws Exception {
      doParseContentTypeTestImpl("application/ecmascript;charset=iso-8859-1", StandardCharsets.ISO_8859_1);
      doParseContentTypeTestImpl("application/ecmascript;charset=us-ascii", StandardCharsets.US_ASCII);
      doParseContentTypeTestImpl("application/ecmascript;charset=utf-8", StandardCharsets.UTF_8);
      doParseContentTypeTestImpl("application/ecmascript", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithApplicationXml() throws Exception {
      doParseContentTypeTestImpl("application/xml;charset=iso-8859-1", StandardCharsets.ISO_8859_1);
      doParseContentTypeTestImpl("application/xml;charset=us-ascii", StandardCharsets.US_ASCII);
      doParseContentTypeTestImpl("application/xml;charset=utf-8", StandardCharsets.UTF_8);
      doParseContentTypeTestImpl("application/xml", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithApplicationXmlVariant() throws Exception {
      doParseContentTypeTestImpl("application/something+xml;charset=iso-8859-1", StandardCharsets.ISO_8859_1);
      doParseContentTypeTestImpl("application/something+xml;charset=us-ascii", StandardCharsets.US_ASCII);
      doParseContentTypeTestImpl("application/something+xml;charset=utf-8", StandardCharsets.UTF_8);
      doParseContentTypeTestImpl("application/something+xml", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithApplicationXmlDtd() throws Exception {
      doParseContentTypeTestImpl("application/xml-dtd;charset=iso-8859-1", StandardCharsets.ISO_8859_1);
      doParseContentTypeTestImpl("application/xml-dtd;charset=us-ascii", StandardCharsets.US_ASCII);
      doParseContentTypeTestImpl("application/xml-dtd;charset=utf-8", StandardCharsets.UTF_8);
      doParseContentTypeTestImpl("application/xml-dtd", StandardCharsets.UTF_8);
   }

   @Test
   public void testParseContentTypeWithApplicationOtherNotTextual() throws Exception {
      // Expect null as this is not a textual type
      doParseContentTypeTestImpl("application/other", null);
   }

   @Test
   public void testParseContentTypeWithApplicationOctetStream() throws Exception {
      // Expect null as this is not a textual type
      doParseContentTypeTestImpl(AMQPMessageSupport.OCTET_STREAM_CONTENT_TYPE, null);
   }

   @Test
   public void testParseContentTypeWithApplicationJavaSerialized() throws Exception {
      // Expect null as this is not a textual type
      doParseContentTypeTestImpl(AMQPMessageSupport.SERIALIZED_JAVA_OBJECT_CONTENT_TYPE.toString(), null);
   }

   private void doParseContentTypeTestImpl(String contentType, Charset expected) throws ActiveMQAMQPInvalidContentTypeException {
      Charset charset = AMQPContentTypeSupport.parseContentTypeForTextualCharset(contentType);
      if (expected == null) {
         assertNull(charset, "Expected no charset, but got:" + charset);
      } else {
         assertEquals(expected, charset, "Charset not as expected");
      }
   }
}
