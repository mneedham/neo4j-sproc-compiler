/*
 * Copyright 2016-2016 the original author or authors.
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
package net.biville.florent.sproccompiler.messages;

import javax.annotation.processing.Messager;

public class MessagePrinter
{

    private final Messager messager;

    public MessagePrinter( Messager messager )
    {
        this.messager = messager;
    }

    public void print( CompilationMessage message )
    {
        messager.printMessage( message.getCategory(), message.getContents(), message.getElement(),
                message.getMirror() );
    }
}