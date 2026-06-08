package org.matsim.project;

import com.google.inject.Singleton;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.application.MATSimApplication;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.ScoringConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.population.PersonUtils;
import playground.vsp.scoring.IncomeDependentUtilityOfMoneyPersonScoringParameters;

public class ScoringModelImplementation extends MATSimApplication {

    public ScoringModelImplementation() {
        super();
    }

    public static void main(String[] args) {

        MATSimApplication.execute(
                ScoringModelImplementation.class,
                "--config",
                "scenarios/equil/config-2026.xml"
        );
    }

    @Override
    protected Config prepareConfig(Config config) {

        config.controller().setOverwriteFileSetting(
                OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists
        );

        config.controller().setLastIteration(0);

        config.scoring().setExplainScores(true);
        config.scoring().setWriteExperiencedPlans(true);

        config.scoring()
                .getOrCreateModeParams(TransportMode.car)
                .setMarginalUtilityOfTraveling(0);

        /*
         * WORK ACTIVITY
         */
        ScoringConfigGroup.ActivityParams work =
                new ScoringConfigGroup.ActivityParams("work");

        work.setTypicalDuration(8 * 3600);

        /*
         * HOME ACTIVITY
         */
        ScoringConfigGroup.ActivityParams home =
                new ScoringConfigGroup.ActivityParams("home");

        home.setTypicalDuration(12 * 3600);

        config.scoring().addActivityParams(work);
        config.scoring().addActivityParams(home);

        /*
         * remove schedule penalties
         */
        config.scoring().getScoringParameters(null)
                .setLateArrival_utils_hr(0.0);

        config.scoring().getScoringParameters(null)
                .setEarlyDeparture_utils_hr(0.0);

        config.scoring().getScoringParameters(null)
                .setMarginalUtlOfWaiting_utils_hr(0.0);

        config.scoring().getScoringParameters(null)
                .setMarginalUtlOfWaitingPt_utils_hr(0.0);

        return config;
    }

    @Override
    protected void prepareScenario(Scenario scenario) {

        /*
         * REMOVE EXISTING POPULATION
         */
        scenario.getPopulation().getPersons().clear();

        Population population = scenario.getPopulation();
        PopulationFactory factory = population.getFactory();

        /*
         * AGENT 1:
         * perfect activity duration
         */
        Person perfectAgent =
                factory.createPerson(Id.createPersonId("perfectDuration"));

        PersonUtils.setIncome(perfectAgent, 200.0);

        Plan perfectPlan = factory.createPlan();

        Activity home1 =
                factory.createActivityFromCoord(
                        "home",
                        new Coord(0, 0)
                );

        home1.setEndTime(10);

        Leg leg1 = factory.createLeg(TransportMode.car);

        Activity work =
                factory.createActivityFromCoord(
                        "work",
                        new Coord(10000, 0)
                );

        work.setEndTime(29160);

        Leg leg2 = factory.createLeg(TransportMode.car);

        Activity home2 =
                factory.createActivityFromCoord(
                        "home",
                        new Coord(0, 0)
                );

        home2.setEndTime(36360);

        perfectPlan.addActivity(home1);
        perfectPlan.addLeg(leg1);
        perfectPlan.addActivity(work);
        perfectPlan.addLeg(leg2);
        perfectPlan.addActivity(home2);

        perfectAgent.addPlan(perfectPlan);

        population.addPerson(perfectAgent);

        /*
         * AGENT 2:
         * time pressure
         */
        Person stressedAgent =
                factory.createPerson(Id.createPersonId("timePressure"));

        PersonUtils.setIncome(stressedAgent, 100.0);

        Plan stressedPlan = factory.createPlan();

        Activity stressedHome =
                factory.createActivityFromCoord(
                        "home",
                        new Coord(0, 0)
                );

        stressedHome.setEndTime(30);

        Leg stressedLeg1 =
                factory.createLeg(TransportMode.car);

        Activity stressedWork =
                factory.createActivityFromCoord(
                        "work",
                        new Coord(10000, 0)
                );

        stressedWork.setEndTime(20000);

        Leg stressedLeg2 =
                factory.createLeg(TransportMode.car);

        Activity stressedHome2 =
                factory.createActivityFromCoord(
                        "home",
                        new Coord(0, 0)
                );

        stressedPlan.addActivity(stressedHome);
        stressedPlan.addLeg(stressedLeg1);
        stressedPlan.addActivity(stressedWork);
        stressedPlan.addLeg(stressedLeg2);
        stressedPlan.addActivity(stressedHome2);

        stressedAgent.addPlan(stressedPlan);

        population.addPerson(stressedAgent);
    }

    @Override
    protected void prepareControler(Controler controler) {

        controler.addOverridingModule(
                new AbstractModule() {

                    @Override
                    public void install() {

                        /*
                         * income-dependent scoring
                         */
                        bind(org.matsim.core.scoring.functions.ScoringParametersForPerson.class)
                                .to(IncomeDependentUtilityOfMoneyPersonScoringParameters.class)
                                .asEagerSingleton();
                    }
                }
        );
    }
}