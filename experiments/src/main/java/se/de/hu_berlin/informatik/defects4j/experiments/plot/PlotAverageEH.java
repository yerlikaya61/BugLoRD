/**
 * 
 */
package se.de.hu_berlin.informatik.defects4j.experiments.plot;

import java.io.File;

import se.de.hu_berlin.informatik.benchmark.api.defects4j.Defects4J;
import se.de.hu_berlin.informatik.benchmark.api.defects4j.Defects4J.Defects4JProperties;
import se.de.hu_berlin.informatik.defects4j.experiments.BugLoRD;
import se.de.hu_berlin.informatik.defects4j.experiments.BugLoRD.BugLoRDProperties;
import se.de.hu_berlin.informatik.benchmark.api.defects4j.Defects4JEntity;
import se.de.hu_berlin.informatik.rankingplotter.plotter.Plotter;
import se.de.hu_berlin.informatik.rankingplotter.plotter.Plotter.ParserStrategy;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInput;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputFactory;

/**
 * Runs a single experiment.
 * 
 * @author Simon Heiden
 */
public class PlotAverageEH extends EHWithInput<String> {

	public static class Factory extends EHWithInputFactory<String> {

		private final ParserStrategy strategy;
		private final String project;
		private String outputDir;
		
		/**
		 * Initializes a {@link Factory} object with the given parameters.
		 * @param strategy
		 * the strategy to use when encountering equal-rank data points
		 * @param project
		 * the project
		 * @param outputDir
		 * the main plot output directory
		 */
		public Factory(ParserStrategy strategy, String project, String outputDir) {
			super(PlotAverageEH.class);
			this.strategy = strategy;
			this.project = project;
			this.outputDir = outputDir;
		}

		@Override
		public EHWithInput<String> newFreshInstance() {
			return new PlotAverageEH(strategy, project, outputDir);
		}
	}
	
	private final static String SEP = File.separator;
	
	private final ParserStrategy strategy;
	private final String project;
	private String outputDir;
	private final Defects4JEntity projectEntity;
	
	private final boolean isProject;
	
	final private static String[] gp = BugLoRD.getValueOf(BugLoRDProperties.RANKING_PERCENTAGES).split(" ");
	final private static String[] lp = { "100" };
	
	/**
	 * Initializes a {@link PlotAverageEH} object with the given parameters.
	 * @param strategy
	 * the strategy to use when encountering equal-rank data points
	 * @param project
	 * the project
	 * @param outputDir
	 * the main plot output directory
	 */
	public PlotAverageEH(ParserStrategy strategy, String project, String outputDir) {
		super();
		this.strategy = strategy;
		this.project = project;
		this.outputDir = outputDir;
		
		this.projectEntity = Defects4JEntity.getProjectEntity(this.project);
		this.isProject = Defects4JEntity.validateProject(project, false);
		
		if (!isProject && !project.equals("super")) {
			Log.abort(this, "Project doesn't exist: '" + project + "'.");
		}
		
		if (this.outputDir == null) {
			this.outputDir = Defects4J.getValueOf(Defects4JProperties.PLOT_DIR);
		}
	}

	@Override
	public void resetAndInit() {
		//not needed
	}

	@Override
	public boolean processInput(String input) {
		
		String height = "120";
		
		String[] localizer = { input };
		
		if (isProject) {
			
			/* #====================================================================================
			 * # plot averaged rankings for given project
			 * #==================================================================================== */
			String plotOutputDir = outputDir + SEP + "average" + SEP + input;
			
			Plotter.plotAverageDefects4JProject(
					projectEntity.getProjectDir().toString(), plotOutputDir, strategy, height, localizer, gp, lp);
			
		} else { //given project name was "super"; iterate over all project directories
			
			/* #====================================================================================
			 * # plot averaged rankings for super directory
			 * #==================================================================================== */
			String plotOutputDir = outputDir + SEP + "average" + SEP + "super";

			Plotter.plotAverageDefects4JProject(
					projectEntity.getBenchmarkDir().toString(), plotOutputDir, strategy, height, localizer, gp, lp);

		}
		
		return true;
	}

}
