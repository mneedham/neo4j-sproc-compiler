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
package net.biville.florent.sproccompiler.procedures.invalid.bad_record_type;

public class BadRecord
{

    private static final int DEFAULT_AGE = 42;
    private final String label; /* nonstatic fields should be public */
    private final int age;

    public BadRecord( String label, int age )
    {
        this.label = label;
        this.age = age < 0 ? DEFAULT_AGE : age;
    }
}