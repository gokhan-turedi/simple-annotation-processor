package demo.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import demo.api.ToString;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class) // resource file'ı otomatik olusturuyor
@SupportedAnnotationTypes({"demo.api.ToString"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ToStringProcessor
        extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // bolca log atarak yapıyı daha iyi anlamaya calisiyoruz
        log("process: "+annotations);

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ToString.class);
        for (Element element : elements) {
            log("target: " + element.getKind() + "|" + element.getSimpleName());
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                generateClass(classElement);
                for (Element child : classElement.getEnclosedElements()) {
                    log("child: " + child.getKind() + "|" + child.getSimpleName());
                }
            }

        }
        return true;
    }

    // helpers //

    private void generateClass(TypeElement classElement) {
        try {

            ClassName type = ClassName.get(classElement);

            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("toString")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(String.class)
                    .addParameter(type, "obj")
                    .addStatement("String r = \""+classElement.getSimpleName()+" {\"");

            for (Element element : classElement.getEnclosedElements()) {
                if (element.getKind() == ElementKind.FIELD) {
                    String n = element.getSimpleName().toString();
                    methodBuilder.addStatement("r+=\" "+n+":\"+obj."+n);
                }
            }

            methodBuilder.addStatement("r+=\" }\"");
            methodBuilder.addStatement("return r");

            TypeSpec clazz = TypeSpec.classBuilder(classElement.getSimpleName()+"ToStringHelper")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodBuilder.build())
                    .build();

            JavaFile javaFile = JavaFile.builder("gturedi.toString", clazz)
                    .build();

            javaFile.writeTo(System.out);
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            log(e.getMessage());
        }
    }

    void log(String str) {
        //System.out.println(str);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, str);
    }

}