/*
 *    Copyright 2017 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebionetworks.research.sdk.result;

import android.support.annotation.NonNull;

import java.util.Date;


public class ResultBase implements Result {
    @NonNull
    private final Date startDate;
    @NonNull
    private final Date endDate;

    public ResultBase(@NonNull Date startDate, @NonNull Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    @NonNull
    public Date getStartDate() {
        return startDate;
    }

    @Override
    @NonNull
    public Date getEndDate() {
        return endDate;
    }
}
