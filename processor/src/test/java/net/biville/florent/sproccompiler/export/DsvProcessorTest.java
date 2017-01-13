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
package net.biville.florent.sproccompiler.export;

import com.google.testing.compile.CompilationRule;
import net.biville.florent.sproccompiler.testutils.JavaFileObjectUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.nio.file.Files.readAllLines;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class DsvProcessorTest
{

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Rule
    public CompilationRule compilation = new CompilationRule();
    private Processor processor = new DsvProcessor();
    private File folder;


    @Before
    public void prepare() throws IOException
    {
        folder = temporaryFolder.newFolder();
    }

    @Test
    public void dumps_procedure_definition_to_dsv_with_custom_delimiter() throws IOException
    {
        Iterable<JavaFileObject> sources =
                asList( JavaFileObjectUtils.INSTANCE.procedureSource( "valid/SimpleProcedures.java" ),
                        JavaFileObjectUtils.INSTANCE.procedureSource( "valid/SimpleUserFunctions.java" ) );

        assert_().about( javaSources() ).that( sources )
                .withCompilerOptions( "-AGeneratedDocumentationPath=" + folder.getAbsolutePath(),
                        "-ADocumentation.FieldDelimiter=|" ).processedWith( processor ).compilesWithoutError();

        String namespace = "net.biville.florent.sproccompiler.procedures.valid";
        String generatedCsv = readContents( Paths.get( folder.getAbsolutePath(), "valid.csv" ) );
        assertThat( generatedCsv ).isEqualTo(
                "" + "\"type\"|\"name\"|\"description\"|\"execution mode\"|\"location\"|\"deprecated by\"\n" +
                        "\"procedure\"|\"" + namespace + ".doSomething(int foo)\"|\"\"|\"PERFORMS_WRITE\"|\"" +
                        namespace + ".SimpleProcedures\"|\"doSomething2\"\n" + "\"procedure\"|\"" + namespace +
                        ".doSomething2(long bar)\"|\"Much better than the former version\"|\"SCHEMA\"|\"" + namespace +
                        ".SimpleProcedures\"|\"\"\n" + "\"procedure\"|\"" + namespace +
                        ".doSomething3(LongWrapper bar)\"|\"Much better with records\"|\"SCHEMA\"|\"" + namespace +
                        ".SimpleProcedures\"|\"\"\n" + "\"function\"|\"" + namespace +
                        ".sum(int a,int b)\"|\"Performs super complex maths\"|\"\"|\"" + namespace +
                        ".SimpleUserFunctions\"|\"\"" );
    }

    @Test
    public void dumps_only_exported_fields_with_default_delimiter() throws IOException
    {
        Iterable<JavaFileObject> sources =
                asList( JavaFileObjectUtils.INSTANCE.procedureSource( "valid/SimpleProcedures.java" ),
                        JavaFileObjectUtils.INSTANCE.procedureSource( "valid/SimpleUserFunctions.java" ) );

        assert_().about( javaSources() ).that( sources )
                .withCompilerOptions( "-AGeneratedDocumentationPath=" + folder.getAbsolutePath(),
                        "-ADocumentation.ExportedHeaders=execution mode,type" ).processedWith( processor )
                .compilesWithoutError();

        String generatedCsv = readContents( Paths.get( folder.getAbsolutePath(), "valid.csv" ) );
        assertThat( generatedCsv ).isEqualTo(
                "" + "\"execution mode\",\"type\"\n" + "\"PERFORMS_WRITE\",\"procedure\"\n" +
                        "\"SCHEMA\",\"procedure\"\n" + "\"SCHEMA\",\"procedure\"\n" + "\"\",\"function\"" );
    }

    private String readContents( Path path ) throws IOException
    {
        return readAllLines( path ).stream().collect( Collectors.joining( "\n" ) );
    }
}