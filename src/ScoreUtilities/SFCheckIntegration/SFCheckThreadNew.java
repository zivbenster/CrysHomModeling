//package ScoreUtilities.SFCheckIntegration;
//
//import ModellingTool.MainMenu;
//import ModellingTool.RunParameters;
//import ModellingUtilities.molecularElements.SimpleProtein;
//
//import javax.swing.*;
//import java.io.*;
//import java.nio.charset.Charset;
//import java.nio.file.DirectoryStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ConcurrentLinkedDeque;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import static ScoreUtilities.ScoringGeneralHelpers.PDB_EXTENSION;
//import static ScoreUtilities.ScoringGeneralHelpers.makeSubFolderAt;
//
///**
// * SFCheck worker thread instance.
// * create these when you want to run SFCheck against some PDB file, then pass the worker to an execution pool for processing.
// */
//public class SFCheckThreadNew extends SwingWorker<Void,String[]>  {
//	private final JProgressBar sfchkProgress;
//	private RunParameters params;
//	private File SFCheckExe;
//	private File outputFolder;
////	private File output;
////	private File trueOutput;
//	private ConcurrentLinkedDeque<File> protsToProcess;
//	int checksCounter = 0;
//	private List<String[]> tempSFCheckResultSet = new ArrayList<>();
//
//	/**
//	 * constructor - gets the PDB file to check and the run parameters object.
//	 */
//	public SFCheckThreadNew(ConcurrentLinkedDeque<File> protsToProcess, RunParameters params, JProgressBar sfchkProgress) throws IOException {
//		outputFolder = makeSubFolderAt(params.getScwrlOutputFolder().getParentFile(), "sfcheck_LogFiles");
//		outputFolder = makeSubFolderAt(outputFolder, String.valueOf(params.getChainToProcess()));
//		this.protsToProcess = protsToProcess;
//		SFCheckExe = params.getSFChkexe();
//		this.params = params;
//		this.sfchkProgress = sfchkProgress;
//
//	}
//
//
//	@Override
//	/**
//	 * execution method - returns a String array of the results - first line is the protein name (which position was changed to which residue)
//	 * second line is the result - Correlation Coefficient.
//	 */
//	protected Void doInBackground() throws Exception {
//		String result = null;
//		String re1="(\\s+)";	// White Space 1
//		String re2="(Correlation)";	// Word 1
//		String re3="( )";	// White Space 2
//		String re4="(factor)";	// Word 2
//		String re5="(\\s+)";	// White Space 3
//		String re6="(:)";	// Any Single Character 1
//
//		Pattern p = Pattern.compile(re1+re2+re3+re4+re5+re6,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
//		Matcher m = p.matcher("");
//
//		String curPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//
//		SimpleProtein srcProt = new SimpleProtein(params.getPDBsrc(), params);
//		for (File protToProcess : protsToProcess) {
//			boolean fakeRun = false;
//
//			File output = new File(outputFolder.getAbsolutePath() + File.separator + protToProcess.getName().replaceFirst(PDB_EXTENSION + "+$",
// "_"));
//			File trueOutput = new File(output.getAbsolutePath() + "sfcheck.log");
//			if (trueOutput.exists()) {
//				fakeRun = true;
//			}
//
//
//
//
//
//			/**
//			 * fake runs are runs for which an SFCheck result already exists. this is sometimes used when debugging and re-running on the same files
//			 * without changing the PDB but only recalculating ZScores and such.
//			 * only read the result file without actually running the SFCheck binary.
//			 */
//			if (fakeRun) {
//
//				try (
//						BufferedReader reader = Files.newBufferedReader(trueOutput.toPath(), Charset.defaultCharset());
//						LineNumberReader lineReader = new LineNumberReader(reader)
//				) {
//					String line = null;
//					while ((line = lineReader.readLine()) != null) {
//						m.reset(line); //reset the input
//						if (m.find()) {
//							result = line.substring(24);
//							break;
//						}
//					}
//					reader.close();
//				} catch (IOException ex) {
//					ex.printStackTrace();
//				}
//				checksCounter++;
//				publish(new String[]{protToProcess.getName(), result});
//
//			} else {
//				SimpleProtein newProt = new SimpleProtein(protToProcess, params);
//				newProt.replaceTempValue(srcProt);
//				newProt.writePDB(protToProcess);
//
//				Process process = null;
//				try {
//					process = Runtime.getRuntime().exec(SFCheckExe.getAbsolutePath() +
//							" -f " + params.getMAPsrc() +
//							" -m " + protToProcess.getAbsolutePath() +
//							" -po " + output.getAbsolutePath());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//				assert process != null;
//				BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
//				BufferedReader er = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//				String line;
//
//
//				try {
//					while ((line = br.readLine()) != null) {
//						m.reset(line);
//						if (m.find()) {
//							result = line.substring(24);
//						}
//					}
//					while ((line = er.readLine()) != null) {
//						System.out.println("SFCheck error: ");
//						System.out.println(line);
//					}
//				} catch (IOException e) {
//					System.out.println("SFCheck error: ");
//					System.out.println(e.getMessage());
//				}
//
//				try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(outputFolder.toPath())) {
//					for (Path path : directoryStream) {
//						if (!path.toString().endsWith(".log")) {
//							path.toFile().delete();
//						}
//					}
//				}
//				checksCounter++;
//				publish(new String[]{protToProcess.getName(), result});
//			}
//		}
//
//
//		return null;
//	}
//
//	@Override
//	protected void process(java.util.List<String[]> updateList){
//		for (String[] result : updateList){
//			MainMenu.SFCheckResultSet.addAll(updateList);
//			MainMenu.sfckProgressCounter = checksCounter;
//			sfchkProgress.setValue(checksCounter / protsToProcess.size() * 100);
//			sfchkProgress.setString(checksCounter+" Done");
//		}
//
//	}
//
//	@Override
//	/**
//	 * when done, add the result to the SFCheck resultset collection and advance the progress counter.
//	 */
//	protected void done(){
//		setProgress(100);
//	}
//}