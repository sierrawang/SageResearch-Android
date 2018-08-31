package org.sagebionetworks.research.presentation.recorder.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import org.sagebionetworks.research.domain.async.DeviceMotionRecorderConfiguration;
import org.sagebionetworks.research.domain.async.RecorderConfiguration;
import org.sagebionetworks.research.presentation.inject.RecorderConfigPresentationFactory;
import org.sagebionetworks.research.presentation.inject.SensorModule.Sensors;
import org.sagebionetworks.research.presentation.recorder.RecorderConfigPresentation;
import org.sagebionetworks.research.presentation.recorder.reactive.source.SensorSourceFactory.SensorConfig;
import org.sagebionetworks.research.presentation.recorder.reactive.source.SensorSourceFactory.SensorConfig.SensorConfigBuilder;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SensorRecorderConfigPresentationFactory implements RecorderConfigPresentationFactory {
    private static final int SECONDS_TO_MICROSECONDS = 1_000_000;

    private final Map<String, Integer> sensorsMap;
    private final Context context;

    @Inject
    public SensorRecorderConfigPresentationFactory(@Sensors Map<String, Integer> sensorsMap, Context context) {
        this.sensorsMap = sensorsMap;
        this.context = context;
    }

    @NonNull
    @Override
    public RecorderConfigPresentation create(@NonNull final RecorderConfiguration config) {
        if (!(config instanceof DeviceMotionRecorderConfiguration)) {
            throw new IllegalArgumentException(
                    "Provided RecorderConfiguration " + config + " isn't a DeviceMotionRecorderConfiguration");
        }

        List<Sensor> availableSensors = ((SensorManager) context.getSystemService(Context.SENSOR_SERVICE))
                .getSensorList(Sensor.TYPE_ALL);

        // TODO: map to list of matching sensor types, then filter by availability @liujoshua 2018/08/29
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
                // TODO: currently using system default sampling rate. does SR have a default? @liujoshua 2018/08/29
                sensorConfigs.add(sensorConfigBuilder.build());
            }
        }

        return SensorRecorderConfigPresentationImpl.builder()
                .setIdentifier(config.getIdentifier())
                .setType(config.getType())
                .setStartStepIdentifier(config.getStartStepIdentifier())
                .setStopStepIdentifier(config.getStopStepIdentifier())
                .setSensorConfigs(sensorConfigs)
                .build();
    }
}
