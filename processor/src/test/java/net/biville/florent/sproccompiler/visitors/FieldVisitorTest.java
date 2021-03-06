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
package net.biville.florent.sproccompiler.visitors;

import com.google.testing.compile.CompilationRule;
import net.biville.florent.sproccompiler.messages.CompilationMessage;
import net.biville.florent.sproccompiler.testutils.ElementTestUtils;
import net.biville.florent.sproccompiler.visitors.examples.GoodContextUse;
import net.biville.florent.sproccompiler.visitors.examples.StaticNonContextMisuse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.stream.Stream;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class FieldVisitorTest
{

    @Rule
    public CompilationRule compilationRule = new CompilationRule();
    private ElementVisitor<Stream<CompilationMessage>,Void> fieldVisitor;
    private ElementTestUtils elementTestUtils;

    @Before
    public void prepare()
    {
        elementTestUtils = new ElementTestUtils( compilationRule );
        fieldVisitor = new FieldVisitor( compilationRule.getTypes(), compilationRule.getElements(), false );
    }

    @Test
    public void validates_visibility_of_fields() throws Exception
    {
        Stream<VariableElement> fields = elementTestUtils.getFields( GoodContextUse.class );

        Stream<CompilationMessage> result = fields.flatMap( fieldVisitor::visit );

        assertThat( result ).isEmpty();
    }

    @Test
    public void rejects_non_static_non_context_fields()
    {
        Stream<VariableElement> fields = elementTestUtils.getFields( StaticNonContextMisuse.class );

        Stream<CompilationMessage> result = fields.flatMap( fieldVisitor::visit );

        assertThat( result ).extracting( CompilationMessage::getCategory, CompilationMessage::getContents )
                .containsExactly(
                        tuple( Diagnostic.Kind.ERROR, "Field StaticNonContextMisuse#value should be static" ) );
    }

}

