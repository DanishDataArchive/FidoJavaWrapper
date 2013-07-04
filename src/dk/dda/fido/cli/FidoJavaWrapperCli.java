package dk.dda.fido.cli;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import dk.dda.version.BuildInfo;

import dk.dda.fido.Fido;
import dk.dda.fido.callbacks.IFidoResultCallback;
import dk.dda.fido.pojos.FidoResult;

public class FidoJavaWrapperCli implements IFidoResultCallback {

	private int optsBeforeParse = 0;
	private int optsAfterParse = 0;
	private Options opts;
	private CommandLineParser optParser;
	private CommandLine cl = null;

	private Fido fido = null;

	public FidoJavaWrapperCli(String[] args) {
		opts = new Options();
		opts.addOption("h", "help", false, "show this help message and exit");
		opts.addOption("recurse", false, "recurse into subdirectories");
		opts.addOption("zip", false, "recurse into zip and tar files");
		opts.addOption("V", "version", false, "show version information");

		optParser = new BasicParser();

		optsBeforeParse = args.length;

		if(optsBeforeParse <= 0) {
			System.out.println("Got nothing to do... Try with -h or --help");
			System.exit(-1);
		}

		try {
			cl = optParser.parse(opts, args, false);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if(cl != null) {
			optsAfterParse = cl.getArgList().size();

			if(cl.hasOption("h")) {
				printHelp();
				System.exit(0);
			}
			
			if(cl.hasOption("V")) {
				System.out.println("Version: " + BuildInfo.version + "\nBuild on: " + BuildInfo.date + " " + BuildInfo.time);
				System.exit(0);
			}

			try {
				fido = new Fido();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			if(fido != null)
				fido.setRecurse(cl.hasOption("recurse"));
			if(fido != null)
				fido.setZip(cl.hasOption("zip"));

			if(optsAfterParse > 0) {
				for(String s : cl.getArgs()) {
					File f = new File(s);

					if(f.exists() && (f.isDirectory() || f.isFile())) {
						try {
							fido.setPathToInspect(f.getAbsolutePath());
							fido.addCallback(this);
							fido.inspect();
							fido.join();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						System.out.print("Uncognized option(s): ");
						System.out.print(s + " ");
					}
				}
			}
		}
	}

	private void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("FidoJavaWrapper [OPTIONS] dir/file", opts);
		System.out.println("\nVersion: " + BuildInfo.version);
	}

	@Override
	public void fidoResults(HashMap<String, List<FidoResult>> recognizedFiles, String[] unRecognizedFiles, String path) {
		new DDAFidoResultsPrinter(recognizedFiles, unRecognizedFiles, path);
	}

	public static void main(String[] argv) {
		new FidoJavaWrapperCli(argv);
	}
}
