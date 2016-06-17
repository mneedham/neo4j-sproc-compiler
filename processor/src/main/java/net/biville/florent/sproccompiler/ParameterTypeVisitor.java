package net.biville.florent.sproccompiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import java.util.function.Predicate;
import java.util.stream.Stream;

class ParameterTypeVisitor extends SimpleTypeVisitor8<Stream<CompilationError>, VariableElement> {

    private final TypeMirrors typeMirrors;
    private final Predicate<TypeMirror> allowedTypesValidator;

    public ParameterTypeVisitor(Types typeUtils, Elements elementUtils) {
        this.typeMirrors = new TypeMirrors(typeUtils, elementUtils);
        allowedTypesValidator = new AllowedTypesValidator(
            typeMirrors.procedureAllowedTypes(),
            typeUtils,
            elementUtils
        );
    }

    @Override
    public Stream<CompilationError> visitDeclared(DeclaredType parameterType, VariableElement initialElement) {
        return Stream.concat(
                validate(parameterType, initialElement),
                parameterType.getTypeArguments().stream().flatMap(type -> visit(type, initialElement))
        );
    }

    @Override
    public Stream<CompilationError> visitPrimitive(PrimitiveType primitive, VariableElement initialElement) {
        return validate(primitive, initialElement);
    }

    @Override
    protected Stream<CompilationError> defaultAction(TypeMirror unknown, VariableElement initialElement) {
        return compilationError(initialElement);
    }

    private Stream<CompilationError> validate(TypeMirror typeMirror, VariableElement initialElement) {
        if (!allowedTypesValidator.test(typeMirror)) {
            return compilationError(initialElement);
        }
        return Stream.empty();
    }

    private Stream<CompilationError> compilationError(VariableElement initialElement) {
        Element method = initialElement.getEnclosingElement();
        return Stream.of(new ParameterTypeError(
                initialElement,
                "Unsupported parameter type <%s> of procedure %s#%s",
                initialElement.asType().toString(),
                method.getEnclosingElement().getSimpleName(),
                method.getSimpleName()
        ));
    }
}