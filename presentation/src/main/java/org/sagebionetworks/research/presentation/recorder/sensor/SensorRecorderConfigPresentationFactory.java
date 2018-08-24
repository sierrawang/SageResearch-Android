package org.sagebionetworks.research.presentation.recorder.sensor;

import android.support.annotation.NonNull;

import org.sagebionetworks.research.domain.async.DeviceMotionRecorderConfiguration;
import org.sagebionetworks.research.domain.async.RecorderConfiguration;
import org.sagebionetworks.research.presentation.inject.RecorderConfigPresentationFactory;
import org.sagebionetworks.research.presentation.inject.SensorModule.Sensors;
import org.sagebionetworks.research.presentation.recorder.RecorderConfigPresentation;
import org.sagebionetworks.research.presentation.recorder.reactive.source.SensorSourceFactory.SensorConfig;
import org.sagebionetworks.research.presentation.recorder.reactive.source.SensorSourceFactory.SensorConfig.SensorConfigBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class SensorRecorderConfigPresentationFactory implements RecorderConfigPresentationFactory {
    private static final int SECONDS_TO_MICROSECONDS = 1_000_000;

    private final Map<String, Integer> sensorsMap;

    @Inject
    public SensorRecorderConfigPresentationFactory(@Sensors Map<String, Integer> sensorsMap) {
        this.sensorsMap = sensorsMap;
    }

    @NonNull
    @Override
    public RecorderConfigPresentation create(@NonNull final RecorderConfiguration config,
            @NonNull final String defaultStartStepIdentifier, @NonNull final String defaultStopStepIdentifier) {
        if (!(config instanceof DeviceMotionRecorderConfiguration)) {
            throw new IllegalArgumentException(
                    "Provided RecorderConfiguration " + config + " isn't a DeviceMotionRecorderConfiguration");
        }

        DeviceMotionRecorderConfiguration dmrConfiguration = (DeviceMotionRecorderConfiguration) config;

        Set<SensorConfig> sensorConfigs = new HashSet<>();
        for (String sensor : dmrConfiguration.getRecorderTypes()) {
            Integer sensorType = sensorsMap.get(sensor);
            if (sensorType != null) {
                SensorConfigBuilder sensorConfigBuilder = new SensorConfigBuilder(sensorType);
                if (dmrConfiguration.getFrequency() != null) {
                    int samplingPeriodInUs = (int) Math
                            .round(1 / dmrConfiguration.getFrequency()
                                    * SECONDS_TO_MICROSECONDS);
                    sensorConfigBuilder.setSamplingPeriodInUs(samplingPeriodInUs);
                }
                sensorConfigs.add(sensorConfigBuilder.build());
            }
        }

        return SensorRecorderConfigPresentationImpl.builder()
                .setIdentifier(config.getIdentifier())
                .setType(config.getType())
                .setStartStepIdentifier(
                        getStepIdOrDefault(config.getStartStepIdentifier(), defaultStartStepIdentifier))
                .setStopStepIdentifier(
                        getStepIdOrDefault(config.getStartStepIdentifier(), defaultStartStepIdentifier))
                .setSensorConfigs(sensorConfigs)
                .build();
    }
}
