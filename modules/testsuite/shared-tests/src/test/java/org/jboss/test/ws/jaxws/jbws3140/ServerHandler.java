/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.test.ws.jaxws.jbws3140;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;

public class ServerHandler implements SOAPHandler<SOAPMessageContext>
{

   @Override
   public Set<QName> getHeaders()
   {
      // FIXME getHeaders
      return new HashSet<QName>();
   }

   @Override
   public void close(MessageContext arg0)
   {

   }

   @Override
   public boolean handleFault(SOAPMessageContext msgContext)
   {
      return true;
   }

   @Override
   public boolean handleMessage(SOAPMessageContext msgContext)
   {
      SOAPMessage message = msgContext.getMessage();
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      try
      {
         message.writeTo(bout);
         if (!isOutbound(msgContext))
         {
             appendContentToRequestElement(message, isWSAEnabled(bout.toString()));
         }
      }
      catch (Exception e)
      {

      }

      return true;
   }

   private void appendContentToRequestElement(SOAPMessage message, boolean wsaEnabled) {
      try
      {
         Iterator<?> ite = message.getSOAPBody().getChildElements(new QName("http://TestEndpoint.org/xsd", "MtomRequest"));
         SOAPElement element = (SOAPElement)ite.next();
         SOAPElement requestElement = (SOAPElement)element.getChildElements(new QName("", "request")).next();
         if (wsaEnabled)
         {
            requestElement.setTextContent("--ClientAddressingEnabled");
         }
         else
         {
            requestElement.setTextContent("--ClientAddressingNotEnabled");
         }
      }
      catch (SOAPException e)
      {

      }
   }

   protected Boolean isOutbound(MessageContext msgContext)
   {
      Boolean outbound = (Boolean)msgContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      if (outbound == null)
         throw new IllegalStateException("Cannot find property: " + MessageContext.MESSAGE_OUTBOUND_PROPERTY);

      return outbound;
   }

   public boolean isMTOMEnabled(String str) {

      if (str.indexOf("http://www.w3.org/2004/08/xop/include") > -1) {
         return true;
      }
      return false;

   }

   public boolean isWSAEnabled(String str) {
      if (str.indexOf("Action") > -1)
      {
         return true;
      }
      return false;
   }

}
