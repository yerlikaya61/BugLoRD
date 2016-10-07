package se.de.hu_berlin.informatik.aspectj.frontend.evaluation.sbfl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import se.de.hu_berlin.informatik.aspectj.frontend.evaluation.ibugs.Experiment;
import se.de.hu_berlin.informatik.stardust.localizer.IFaultLocalizer;
import se.de.hu_berlin.informatik.stardust.localizer.Ranking;
import se.de.hu_berlin.informatik.stardust.localizer.RankingMetric;
import se.de.hu_berlin.informatik.stardust.provider.ISpectraProvider;
import se.de.hu_berlin.informatik.stardust.spectra.INode;
import se.de.hu_berlin.informatik.stardust.spectra.ISpectra;
import se.de.hu_berlin.informatik.stardust.util.CsvUtils;

/**
 * Executes an experiment and saves the results.
 */
public class ExperimentCall implements Callable<Boolean> {
	
    private final CreateRankingsFromSpectra parent;
	private final int bugId;

    public ExperimentCall(final CreateRankingsFromSpectra parent, int bugId) {
    	super();
        this.parent = parent;
        this.bugId = bugId;
    }

    /**
     * Take benchmark
     * @param benchmarks
     * 			  map of benchmarks
     * @param id
     *            to identify benchmark
     * @return duration or -1 if just created benchmark
     */
    private String bench(Map<String, Long> benchmarks, final String id) {
        final long now = System.currentTimeMillis();
        if (benchmarks.containsKey(id)) {
            // existing benchmark
            final long duration = now - benchmarks.get(id);
            benchmarks.remove(id);
            return String.format("%f s", new Double(duration / 1000.0d));
        } else {
            benchmarks.put(id, now);
            return null;
        }
    }

    private void runSingleExperiment(final Experiment experiment) {
        FileWriter rankingWriter = null;
        FileWriter faultWriter = null;
        try {
        	parent.logger.log(Level.FINE, "Begin executing experiment");
            experiment.conduct();
            final Ranking<String> ranking = experiment.getRanking();

            final String csvHeader = CsvUtils.toCsvLine(new String[] { "BugID", "Line", "IF", "IS", "NF", "NS",
                    "BestRanking", "WorstRanking", "MinWastedEffort", "MaxWastedEffort", "Suspiciousness", });

            // save simple ranking
            ranking.save(parent.resultsFile(experiment, "ranking.rnk").toString());
            
            // store ranking
            rankingWriter = new FileWriter(parent.resultsFile(experiment, "ranking.csv"));
            rankingWriter.write(csvHeader + "\n");
            for (final INode<String> node : ranking) {
                final String metricLine = this.metricToCsvLine(ranking.getRankingMetrics(node), experiment);
                rankingWriter.write(metricLine + "\n");
            }

            // store metrics of real faults in separate file
            faultWriter = new FileWriter(parent.resultsFile(experiment, "realfaults.csv"));
            faultWriter.write(csvHeader + "\n");
            for (final INode<String> node : experiment.getRealFaultLocations()) {
                final String metricLine = this.metricToCsvLine(ranking.getRankingMetrics(node), experiment);
                faultWriter.write(metricLine + "\n");
            }

        } catch (final Exception e) { // NOCS
        	parent.logger.log(Level.SEVERE, "Executing experiment failed!", e);
        } finally {
            if (null != rankingWriter) {
                try {
                    rankingWriter.flush();
                    rankingWriter.close();
                } catch (final IOException e) {
                	parent.logger.log(Level.WARNING, "Failed closing ranking writer", e);
                }
            }
            if (null != faultWriter) {
                try {
                    faultWriter.flush();
                    faultWriter.close();
                } catch (final IOException e) {
                	parent.logger.log(Level.WARNING, "Failed closing real fault location writer", e);
                }
            }
            parent.logger.log(Level.FINE, "End executing experiment");
        }
    }

    /**
     * Helper to turn a {@link RankingMetric} into a CSV compatible line.
     *
     * @param m
     *            the metric to convert
     * @return csv line
     */
    private String metricToCsvLine(final RankingMetric<String> m, final Experiment experiment) {
        final INode<String> n = m.getNode();
        final String[] parts = new String[] { Integer.toString(experiment.getBugId()), n.getIdentifier(),
                Integer.toString(n.getEF()), Integer.toString(n.getEP()), Integer.toString(n.getNF()),
                Integer.toString(n.getNP()), Integer.toString(m.getBestRanking()),
                Integer.toString(m.getWorstRanking()), Double.toString(m.getMinWastedEffort()),
                Double.toString(m.getMaxWastedEffort()), Double.toString(m.getSuspiciousness()), };
        return CsvUtils.toCsvLine(parts);
    }

	@Override
	public Boolean call() {
    	Map<String, Long> benchmarks = new HashMap<>();
    	
        this.bench(benchmarks, "whole");
        try {
            this.bench(benchmarks, "load_spectra");
            parent.logger.log(Level.INFO, String.format("Loading spectra for %d", bugId));
            final ISpectraProvider<String> spectraProvider = 
            		parent.spectraProviderFactory.factory(bugId);
            final ISpectra<String> spectra = spectraProvider.loadSpectra();
            parent.logger.log(Level.INFO,
                    String.format("Loaded spectra for %d in %s", bugId, this.bench(benchmarks, "load_spectra")));

            // run all SBFL
            for (final IFaultLocalizer<String> fl : parent.faultLocalizers) {
                // skip if result exists
                if (parent.resultExists(bugId, fl.getName())) {
                    continue;
                }

                try {
                    final Experiment experiment = new Experiment(bugId, spectra, fl, parent.realFaults);
                    this.bench(benchmarks, "single_experiment");
                    this.runSingleExperiment(experiment);
                    parent.logger.log(Level.INFO, String.format(
                            "Finished experiment for SBFL %s with bug id %d in %s", fl.getName(), bugId,
                            this.bench(benchmarks, "single_experiment")));

                } catch (final Exception e) { // NOCS
                	parent.logger.log(Level.WARNING, String.format(
                            "Experiments for SBFL %s with bug id %d could not be finished due to exception.",
                            fl.getName(), bugId), e);
                }
            }
        } catch (final Exception e) { // NOCS
        	parent.logger.log(Level.WARNING,
                    String.format("Experiments for bug id %d could not be finished due to exception.", bugId),
                    e);
        	return false;
        } finally {
        	parent.logger.log(Level.INFO,
                    String.format("Finishing all experiments for %d in %s.", bugId, this.bench(benchmarks, "whole")));
        }
		return true;
	}
	
}
