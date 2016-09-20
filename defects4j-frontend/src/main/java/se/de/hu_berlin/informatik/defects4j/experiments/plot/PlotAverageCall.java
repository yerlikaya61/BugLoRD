/**
 * 
 */
package se.de.hu_berlin.informatik.defects4j.experiments.plot;

import java.io.File;
import java.util.concurrent.Callable;

import se.de.hu_berlin.informatik.defects4j.frontend.Prop;
import se.de.hu_berlin.informatik.rankingplotter.plotter.Plotter;
import se.de.hu_berlin.informatik.rankingplotter.plotter.Plotter.ParserStrategy;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.threaded.ADisruptorEventHandlerFactory;
import se.de.hu_berlin.informatik.utils.threaded.CallableWithInput;
import se.de.hu_berlin.informatik.utils.threaded.DisruptorEventHandler;

/**
 * {@link Callable} object that runs a single experiment.
 * 
 * @author Simon Heiden
 */
public class PlotAverageCall extends CallableWithInput<String> {

	public static class Factory extends ADisruptorEventHandlerFactory<String> {

		private final ParserStrategy strategy;
		private final String[] localizers;
		private String outputDir;
		
		/**
		 * Initializes a {@link Factory} object with the given parameters.
		 * @param strategy
		 * the strategy to use when encountering equal-rank data points
		 * @param localizers
		 * the SBFL localizers to use
		 * @param outputDir
		 * the main plot output directory
		 */
		public Factory(ParserStrategy strategy, String[] localizers, String outputDir) {
			super(PlotAverageCall.class);
			this.strategy = strategy;
			this.localizers = localizers;
			this.outputDir = outputDir;
		}

		@Override
		public DisruptorEventHandler<String> newInstance() {
			return new PlotAverageCall(strategy, localizers, outputDir);
		}
	}
	
	private final static String SEP = File.separator;
	
	private final ParserStrategy strategy;
	private final String[] localizers;
	private String outputDir;
	
	/**
	 * Initializes a {@link PlotAverageCall} object with the given parameters.
	 * @param strategy
	 * the strategy to use when encountering equal-rank data points
	 * @param localizers
	 * the SBFL localizers to use
	 * @param outputDir
	 * the main plot output directory
	 */
	public PlotAverageCall(ParserStrategy strategy, String[] localizers, String outputDir) {
		super();
		this.strategy = strategy;
		this.localizers = localizers;
		this.outputDir = outputDir;
	}

	@Override
	public void resetAndInit() {
		//not needed
	}

	@Override
	public boolean processInput(String input) {
		Prop prop = new Prop(input, "", false);
		prop.switchToArchiveMode();

		if (outputDir == null) {
			outputDir = prop.plotMainDir;
		}
		
		String height = "120";
		
		String[] gp = prop.percentages.split(" ");
		String[] lp = { "100" };
		
		if (!Prop.validateProjectAndBugID(input, 1, false)) {
			if (input.equals("super")) {
				/* #====================================================================================
				 * # plot averaged rankings for super directory
				 * #==================================================================================== */
				String plotOutputDir = outputDir + SEP + "average" + SEP + "super";
				
				Plotter.plotAverageDefects4JProject(
						prop.mainDir, plotOutputDir, strategy, height, localizers, gp, lp);
				
				return true;
			} else {
				Log.err(this, "Archive project directory doesn't exist: '" + prop.projectDir + "'.");
				return false;
			}
		}
			
		/* #====================================================================================
		 * # plot averaged rankings for given project
		 * #==================================================================================== */
		String plotOutputDir = outputDir + SEP + "average" + SEP + input;
		
		Plotter.plotAverageDefects4JProject(
				prop.projectDir, plotOutputDir, strategy, height, localizers, gp, lp);
		
		return true;
	}

}
