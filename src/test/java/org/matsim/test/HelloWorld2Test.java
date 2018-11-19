package org.matsim.test;

import static org.junit.Assert.*;

import java.net.URL;

import javax.swing.tree.ExpandVetoException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.utils.eventsfilecomparison.EventsFileComparator;
import org.matsim.utils.eventsfilecomparison.EventsFileComparator.Result;

public class HelloWorld2Test {

	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();
	
	@Test
	public void test() {
		
		try {
			//Config config = ConfigUtils.createConfig();
			
			URL url = ExamplesUtils.getTestScenarioURL( "equil");
			URL configURL = IOUtils.newUrl(url,"config.xml");
			Config config = ConfigUtils.loadConfig(configURL);
			config.controler().setOutputDirectory(utils.getOutputDirectory());
			config.controler().setLastIteration(0);
			config.controler().setOverwriteFileSetting(OverwriteFileSetting.overwriteExistingFiles);
			config.controler().setWriteEventsInterval(1);
			Scenario scenario = ScenarioUtils.loadScenario(config);
			Controler controler = new Controler(scenario);
			controler.run();
			
			Config config2 = ConfigUtils.createConfig();
			config2.plans().setInputFile(utils.getInputDirectory()+"/output_plans.xml.gz");
			Scenario scenario2 = ScenarioUtils.loadScenario(config2);
			
			boolean result2 = PopulationUtils.equalPopulation(scenario2.getPopulation(), scenario.getPopulation());
			Assert.assertTrue(result2);
			

		} catch (Exception ee) {
			System.out.println(ee);
			Assert.fail("Did not work");
			fail("Not yet implemented");
		}
		
		String actualEventsFileName = utils.getOutputDirectory()+"output_events.xml.gz";
		String referenceEventsFileName = utils.getInputDirectory()+"/output_events.xml.gz";
		Result result = EventsFileComparator.compare(referenceEventsFileName, actualEventsFileName);
		Assert.assertEquals(Result.FILES_ARE_EQUAL, result);
		
		
	}

}
