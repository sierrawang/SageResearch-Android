/*
 * BSD 3-Clause License
 *
 * Copyright 2018  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sagebionetworks.research.presentation.recorder.reactive.source;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

/**
 * Tracks the user's location as a stream of Location objects.
 */
public class ReactiveLocationFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveLocationFactory.class);

    public static final long MIN_TIME = 0L;

    public static final float MIN_DISTANCE = 0f;

    private final Context context;

    @Inject
    public ReactiveLocationFactory(Context context) {
        this.context = context;
    }

    /**
     * Returns a Flowable that represents the user's location as a stream of Location objects.
     *
     * @return a Flowable that represents the user's location as a stream of Location objects.
     */
    // TODO rkolmos 08/15/2018 remove this missing permission suppression and fix the permissions.
    @SuppressLint("MissingPermission")
    @NonNull
    public Flowable<Location> getLocation() {
        if (PermissionChecker.checkSelfPermission(context, permission.ACCESS_FINE_LOCATION)
                != PermissionChecker.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, permission.ACCESS_COARSE_LOCATION)
                        != PermissionChecker.PERMISSION_GRANTED) {
            LOGGER.warn("Location Sensor doesn't have necessary permissions");
            return Flowable.error(new SecurityException("Missing permission"));
        }
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        final AtomicReference<LocationListener> listenerHolder = new AtomicReference<LocationListener>();
        return Flowable.<Location>create(
                emitter -> {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_HIGH);

                    listenerHolder.set(new LocationListener() {
                        @Override
                        public void onLocationChanged(final Location location) {
                            emitter.onNext(location);
                        }

                        @Override
                        public void onStatusChanged(final String s, final int i, final Bundle bundle) {
                        }

                        @Override
                        public void onProviderEnabled(final String s) {
                        }

                        @Override
                        public void onProviderDisabled(final String s) {
                        }
                    });

                    try {
                        if (manager != null) {
                            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE,
                                    listenerHolder.get());
                        }
                    } catch (SecurityException e) {
                        emitter.onError(e);
                    }

                }, BackpressureStrategy.BUFFER)
                .doOnCancel(() -> {
                    manager.removeUpdates(listenerHolder.get());
                    listenerHolder.set(null);
                });
    }
}
