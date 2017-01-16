/*
 * 
 */
package se.de.hu_berlin.informatik.rankingplotter.plotter;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.cli.Option;

import se.de.hu_berlin.informatik.benchmark.api.BugLoRDConstants;
import se.de.hu_berlin.informatik.benchmark.api.BuggyFixedEntity;
import se.de.hu_berlin.informatik.rankingplotter.modules.AverageplotCSVGeneratorModule;
import se.de.hu_berlin.informatik.rankingplotter.modules.CombiningRankingsModule;
import se.de.hu_berlin.informatik.rankingplotter.modules.CsvToAverageStatisticsCollectionModule;
import se.de.hu_berlin.informatik.rankingplotter.modules.CsvToSingleStatisticsCollectionModule;
import se.de.hu_berlin.informatik.rankingplotter.modules.DataAdderModule;
import se.de.hu_berlin.informatik.rankingplotter.modules.AveragePlotLaTexGeneratorModule;
import se.de.hu_berlin.informatik.rankingplotter.modules.RankingAveragerModule;
import se.de.hu_berlin.informatik.rankingplotter.modules.SinglePlotCSVGeneratorModule;
import se.de.hu_berlin.informatik.rankingplotter.modules.SinglePlotLaTexGeneratorModule;
import se.de.hu_berlin.informatik.utils.fileoperations.FileUtils;
import se.de.hu_berlin.informatik.utils.fileoperations.SearchForFilesOrDirsModule;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.optionparser.OptionWrapperInterface;
import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.optionparser.OptionWrapper;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.ModuleLinker;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.PipeLinker;
import se.de.hu_berlin.informatik.utils.tm.pipes.ListSequencerPipe;
import se.de.hu_berlin.informatik.utils.tm.pipes.ThreadedProcessorPipe;


/**
 * Plots SBFL and NLFL rankings.
 * 
 * @author Simon Heiden
 */
public class Plotter {
	
	public final static String STRAT_NOCHANGE = "NOCHANGE";
	public final static String STRAT_BEST = "BEST";
	public final static String STRAT_AVERAGE = "AVERAGE";
	public final static String STRAT_WORST = "WORST";
	
	public enum ParserStrategy { NO_CHANGE(0), BEST_CASE(1), AVERAGE_CASE(2), WORST_CASE(3);
		private final int id;
		private ParserStrategy(int id) {
			this.id = id;
		}

		@Override
		public String toString() {
			switch(id) {
			case 0:
				return STRAT_NOCHANGE;
			case 1:
				return STRAT_BEST;
			case 2:
				return STRAT_AVERAGE;
			case 3:
				return STRAT_WORST;
			default:
				return STRAT_NOCHANGE;
			}
		}
	}
	
	public static enum CmdOptions implements OptionWrapperInterface {
		/* add options here according to your needs */
		INPUT("i", "input", true, "Path to ranking directory or directory with defects4J projects.", true),
		
		NORMAL_PLOT(Option.builder("p").longOpt("plot").hasArgs().optionalArg(true)
				.desc("Create plots of the specified subfolders of the input ranking directory "
						+ "(if given), or of all subfolders (if none are given).").build(), 0),
		AVERAGE_PLOT(Option.builder("a").longOpt("averagePlot").hasArgs()
				.desc("Parse all Rankings of the specified types (e.g. 'tarantula') that are found "
						+ "anywhere in the input directory and plot the averages.").build(), 0),
	
		OUTPUT(Option.builder("o").longOpt("output").hasArgs().numberOfArgs(2).required()
				.desc("Path to output directory and a prefix for the output files (two arguments).").build()),
	
		STRATEGY("strat", "parserStrategy", true, "What strategy should be used when encountering a range of"
				+ "equal rankings. Options are: 'BEST', 'WORST', 'NOCHANGE' and 'AVERAGE'. Default is 'NOCHANGE'.", false),	
		
		GLOBAL_PERCENTAGES(Option.builder("gp").longOpt("globalPercentages")
        		.hasArgs().desc("Global Percentages (with multiple arguments).")
        		.build()),
		
		NORMALIZED("n", "normalized", false, "If this is set, then the rankings get normalized before combination.", false);
		
		/* the following code blocks should not need to be changed */
		final private OptionWrapper option;

		//adds an option that is not part of any group
		CmdOptions(final String opt, final String longOpt, 
				final boolean hasArg, final String description, final boolean required) {
			this.option = new OptionWrapper(
					Option.builder(opt).longOpt(longOpt).required(required).
					hasArg(hasArg).desc(description).build(), NO_GROUP);
		}
		
		//adds an option that is part of the group with the specified index (positive integer)
		//a negative index means that this option is part of no group
		//this option will not be required, however, the group itself will be
		CmdOptions(final String opt, final String longOpt, 
				final boolean hasArg, final String description, int groupId) {
			this.option = new OptionWrapper(
					Option.builder(opt).longOpt(longOpt).required(false).
					hasArg(hasArg).desc(description).build(), groupId);
		}
		
		//adds the given option that will be part of the group with the given id
		CmdOptions(Option option, int groupId) {
			this.option = new OptionWrapper(option, groupId);
		}
		
		//adds the given option that will be part of no group
		CmdOptions(Option option) {
			this(option, NO_GROUP);
		}

		@Override public String toString() { return option.getOption().getOpt(); }
		@Override public OptionWrapper getOptionWrapper() { return option; }
	}

	/**
	 * @param args
	 * -i input-dir [(-p|-a) loc1 loc2 ...] -o output-dir output-prefix [-r range] [-l] [-pdf] [-png] [-u unranked-file] [-m ranked-file] [-s] [-z]
	 */
	public static void main(String[] args) {
		
		OptionParser options = OptionParser.getOptions("Plotter", true, CmdOptions.class, args);
		
		//directory with SBFL ranking 	(format: relative/path/To/File:lineNumber: ranking)
		Path inputDir = options.isDirectory(CmdOptions.INPUT, true);
		
		//output file path
		String outputDir = options.isDirectory(CmdOptions.OUTPUT, false).toString();
		
		String outputPrefix = "";
		if (options.getOptionValues(CmdOptions.OUTPUT).length > 1) {
			outputPrefix = options.getOptionValues(CmdOptions.OUTPUT)[1];
		}
		
		ParserStrategy strategy = ParserStrategy.NO_CHANGE;
		if (options.hasOption(CmdOptions.STRATEGY)) {
			switch(options.getOptionValue(CmdOptions.STRATEGY)) {
			case STRAT_BEST:
				strategy = ParserStrategy.BEST_CASE;
				break;
			case STRAT_WORST:
				strategy = ParserStrategy.WORST_CASE;
				break;
			case STRAT_AVERAGE:
				strategy = ParserStrategy.AVERAGE_CASE;
				break;
			case STRAT_NOCHANGE:
				strategy = ParserStrategy.NO_CHANGE;
				break;
			default:
				Log.abort(Plotter.class, "Unknown strategy: '%s'", options.getOptionValue(CmdOptions.STRATEGY));
			}
		}
		
		
		if (options.hasOption(CmdOptions.NORMAL_PLOT)) {
			
			List<Path> changesFiles = new SearchForFilesOrDirsModule("**/.changes", true)
					.searchForFiles()
					.submit(inputDir)
					.getResult();
			
			for (Path changesFile : changesFiles) {
				BuggyFixedEntity entity = new WorkDataDummyBuggyFixedEntity(changesFile.getParent());
				
				String[] localizers = options.getOptionValues(CmdOptions.NORMAL_PLOT);
				if (localizers == null) {
					List<Path> localizerDirs = new SearchForFilesOrDirsModule("**/" + BugLoRDConstants.DIR_NAME_RANKING + "/*", true)
							.searchForDirectories()
							.skipSubTreeAfterMatch()
							.submit(changesFile.toAbsolutePath().getParent().resolve(BugLoRDConstants.DIR_NAME_RANKING))
							.getResult();
					List<String> localizerList = new ArrayList<>(localizerDirs.size());
					for (Path localizerDir : localizerDirs) {
						localizerList.add(localizerDir.getFileName().toString());
					}
					localizers = localizerList.toArray(new String[0]);
				}

				for (String localizerDir : localizers) {
					plotSingle(entity, localizerDir, strategy, outputDir, 
							changesFile.getParent().getParent().getFileName().toString() + File.separator + outputPrefix, 
							options.getOptionValues(CmdOptions.GLOBAL_PERCENTAGES), options.hasOption(CmdOptions.NORMALIZED));
				}

			}
		} else if (options.hasOption(CmdOptions.AVERAGE_PLOT)) {
			
			List<BuggyFixedEntity> entities = new ArrayList<>();
//			//iterate over all projects
//			for (String project : Defects4J.getAllProjects()) {
//				String[] ids = Defects4J.getAllBugIDs(project); 
//				for (String id : ids) {
//					entities.add(Defects4JEntity.getBuggyDefects4JEntity(project, id));
//				}
//			}
			
			List<Path> changesFiles = new SearchForFilesOrDirsModule("**/" + BugLoRDConstants.CHANGES_FILE_NAME, true)
					.searchForFiles()
					.submit(inputDir)
					.getResult();
			
			for (Path changesFile : changesFiles) {
				entities.add(new WorkDataDummyBuggyFixedEntity(changesFile.getParent()));
			}
			
			for (String localizerDir : options.getOptionValues(CmdOptions.AVERAGE_PLOT)) {
				plotAverage(entities, localizerDir, strategy, outputDir, outputPrefix, 
						options.getOptionValues(CmdOptions.GLOBAL_PERCENTAGES), 
						options.getNumberOfThreads(4), options.hasOption(CmdOptions.NORMALIZED));
			}
		}
		
	}
	
	public static void plotSingle(BuggyFixedEntity entity, String localizer, ParserStrategy strategy,
			String outputDir, String outputPrefix, String[] globalPercentages, boolean normalized) {
		
			localizer = localizer.toLowerCase(Locale.getDefault());
			Log.out(Plotter.class, "Plotting rankings for '" + localizer + "'.");

			new ModuleLinker().append(
					new CombiningRankingsModule(localizer, strategy, globalPercentages, normalized), 
					new DataAdderModule(localizer),
					new SinglePlotCSVGeneratorModule(outputDir + File.separator + localizer + File.separator + outputPrefix),
					new SinglePlotLaTexGeneratorModule(outputDir + File.separator + "_latex" + File.separator + localizer + "_" + outputPrefix))
			.submit(entity);

			Log.out(Plotter.class, "...Done with '" + localizer + "'.");
	}
	
	public static void plotAverage(List<BuggyFixedEntity> entities, String localizer, ParserStrategy strategy,
			String outputDir, String outputPrefix, String[] globalPercentages, int numberOfThreads, boolean normalized) {
		
			localizer = localizer.toLowerCase(Locale.getDefault());
			Log.out(Plotter.class, "Submitting '" + localizer + "'.");
			
			//Creates a list of all directories with the same name (localizerDir), sequences the list and
			//parses all found combined rankings and computes the averages. Parsing and averaging is done 
			//as best as possible in parallel with pipes.
			//When all averages are computed, we can plot the results (collected by the averager module).
			new PipeLinker().append(
					new ListSequencerPipe<BuggyFixedEntity>(),
					new ThreadedProcessorPipe<BuggyFixedEntity, RankingFileWrapper>(numberOfThreads, 
							new CombiningRankingsEH.Factory(localizer, strategy, globalPercentages, normalized)),
					new RankingAveragerModule(localizer)
					.enableTracking(10),
					new AverageplotCSVGeneratorModule(outputDir + File.separator + localizer + File.separator + localizer + "_" + outputPrefix),
					new AveragePlotLaTexGeneratorModule(outputDir + File.separator + "_latex" + File.separator + localizer + "_" + outputPrefix))
			.submitAndShutdown(entities);
			
			Log.out(Plotter.class, "...Done with '" + localizer + "'.");
	}
	
	public static void plotFromCSV(String localizer, String inputDir, String outputDir, String outputPrefix) {
			localizer = localizer.toLowerCase(Locale.getDefault());
			Log.out(Plotter.class, "Submitting '" + localizer + "'.");
			
			File localizerDir = FileUtils.searchDirectoryContainingPattern(new File(inputDir), localizer);
			
			if (localizerDir == null) {
				Log.err(Plotter.class, "Could not find subdirectory with name '%s' in '%s'.", localizer, inputDir);
			} else {
				new ModuleLinker().append(
						new CsvToAverageStatisticsCollectionModule(localizer),
						new AveragePlotLaTexGeneratorModule(outputDir + File.separator + "_latex" + File.separator + localizer + "_" + outputPrefix))
				.submit(localizerDir);
				
				new ModuleLinker().append(
						new CsvToSingleStatisticsCollectionModule(localizer),
						new SinglePlotLaTexGeneratorModule(outputDir + File.separator + "_latex" + File.separator + localizer + "_" + outputPrefix))
				.submit(localizerDir);
			}
			
			Log.out(Plotter.class, "...Done with '" + localizer + "'.");
	}
	
	
}
