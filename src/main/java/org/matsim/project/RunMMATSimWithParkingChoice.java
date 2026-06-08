package org.matsim.project;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.parking.parkingsearch.ParkingSearchStrategy;
import org.matsim.contrib.parking.parkingsearch.sim.ParkingSearchConfigGroup;
import org.matsim.contrib.parking.parkingsearch.sim.SetupParking;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.config.groups.ScoringConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;

import java.net.URL;

public class RunMMATSimWithParkingChoice {


    public static void main (String args[]) {

        URL url = IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("chessboard"), "config.xml");
        Config config= ConfigUtils.loadConfig(url, new ParkingSearchConfigGroup());
        config.qsim().setSimStarttimeInterpretation(QSimConfigGroup.StarttimeInterpretation.onlyUseStarttime);

        ScoringConfigGroup.ActivityParams parking = new ScoringConfigGroup.ActivityParams();
        parking.setActivityType("parking");
        parking.setTypicalDuration(1.0);
        config.scoring().addActivityParams(parking);


        config.controller().setLastIteration(10);


        ParkingSearchConfigGroup configGroup = (ParkingSearchConfigGroup) config.getModules().get(ParkingSearchConfigGroup.GROUP_NAME);
        configGroup.setParkingSearchStrategy(ParkingSearchStrategy.Benenson);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Controler controler = new Controler(scenario);
        SetupParking.installParkingModules(controler);
        controler.run();


    }
}
