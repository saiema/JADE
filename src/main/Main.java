package main;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner6;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import visitor.Environment;
import visitor.PrintVisitor;
import visitor.SignatureCollectorVisitor;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;

public class Main {

	@SupportedSourceVersion(SourceVersion.RELEASE_7)
	@SupportedAnnotationTypes("*")
	private static class MyProcessor extends AbstractProcessor {
		@Override
		public boolean process(Set<? extends TypeElement> annotations,
				RoundEnvironment roundEnvironment) {
			class Scan extends ElementScanner6<Void, Void> {

				@Override
				public Void visitExecutable(ExecutableElement e, Void p) {
					System.out.println("method : " + e.toString());
					return super.visitExecutable(e, p);
				}

				@Override
				public Void visitPackage(PackageElement e, Void p) {
					System.out.println("package : " + e.toString());
					return super.visitPackage(e, p);
				}

				@Override
				public Void visitType(TypeElement e, Void p) {
					System.out.println("Type : " + e.toString());
					return super.visitType(e, p);
				}

				@Override
				public Void visitTypeParameter(TypeParameterElement e, Void p) {
					System.out.println("param : " + e.toString());
					return super.visitTypeParameter(e, p);
				}

				@Override
				public Void visitVariable(VariableElement e, Void p) {
					System.out.println("variable: " + e.toString());
					return super.visitVariable(e, p);
				}
			}

			Scan scan = new Scan();
			for (Element e : roundEnvironment.getRootElements()) {
				scan.scan(e);
			}
			return true;
		}
	}

	public static void main(String[] args) throws IOException {
		//processorTest(args[0]);
		visitorTest(args[0]);
	}
	
	private static void processorTest(String file) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		try {
			StandardJavaFileManager fileManager = compiler
					.getStandardFileManager(null, null, null);

			List<File> files = new LinkedList<File>();
			files.add(new File(file));

			Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(files);
			CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits1);
			LinkedList<MyProcessor> processors = new LinkedList<MyProcessor>();
			processors.add(new MyProcessor());
			task.setProcessors(processors);
			task.call();
			try {
				fileManager.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Throwable t) {
			System.out.println(t.getLocalizedMessage());
		}
	}
	
	private static void visitorTest(String file) throws IOException {
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		String filePath = file;
		StandardJavaFileManager jfm = javac.getStandardFileManager(null, null, null);
        Iterable<? extends javax.tools.JavaFileObject> javaFileObjects = jfm.getJavaFileObjects(filePath);

        JavacTask task = (JavacTask) javac.getTask(null, jfm, null, null, null, javaFileObjects);

        Iterable<? extends CompilationUnitTree> asts = task.parse();
        Trees trees = Trees.instance(task);

        SignatureCollectorVisitor signatureCollectorVisitor = new SignatureCollectorVisitor(trees);
        Environment astEnv = new Environment(trees);
        for (CompilationUnitTree ast : asts) {
        	signatureCollectorVisitor.visitCompilationUnit(ast, astEnv);
        	//new PrintVisitor().visitCompilationUnit(ast, null);
        }
        System.out.println(astEnv.getRootEnvironment().toString());
	}
}
