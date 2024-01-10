package lang;

import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import beaver.Parser.Exception;

import lang.ast.Program;
import lang.ast.LangParser;
import lang.ast.LangScanner;
import lang.ast.ErrorMessage;

public class Compiler {
	public static Object CodeProber_root_node; // Enable debugging with CodeProber.

	public static Object CodeProber_parse(String[] args) throws Throwable {
		try {
			String filename = args[args.length - 1];
			LangScanner scanner = new LangScanner(new FileReader(filename));
			LangParser parser = new LangParser();
			Program program = (Program) parser.parse(scanner);
			return program;
		} catch (LangParser.SyntaxError e) {
			System.out.printf("ERR@%d;%d;%s\n", e.start, e.end, e.getMessage());
			throw e;
		}
	}

	public static void main(String[] args) {
		try {
			if (args.length != 1) {
				System.err.println(
						"You must specify a source file on the command line!");
				printUsage();
				System.exit(1);
				return;
			}

			String filename = args[0];
			LangScanner scanner = new LangScanner(new FileReader(filename));
			LangParser parser = new LangParser();
			Program program = (Program) parser.parse(scanner);
			CodeProber_root_node = program; // Enable debugging with CodeProber.
			program.prettyPrint(System.out);
			if (!program.errors().isEmpty()) {
				System.err.println();
				System.err.println("Errors: ");
				for (ErrorMessage e: program.errors()) {
					System.err.println("- " + e);
				}
				System.exit(1);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void printUsage() {
		System.err.println("Usage: Compiler FILE");
		System.err.println("    where FILE is the file to be compiled");
	}
}

