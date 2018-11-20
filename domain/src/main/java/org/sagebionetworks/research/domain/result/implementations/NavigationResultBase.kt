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

package org.sagebionetworks.research.domain.result.implementations

import org.sagebionetworks.research.domain.result.AnswerResultType
import org.sagebionetworks.research.domain.result.interfaces.AnswerResult
import org.sagebionetworks.research.domain.result.interfaces.NavigationResult
import org.sagebionetworks.research.domain.result.interfaces.Result
import org.threeten.bp.Instant

/**
 * NavigationResultDelegation can be used to attached navigation result functionality to any other result type,
 * without the need for a complex class type hierarchy.
 * For an example see NavigationResultBase or NavigationAnswerResultBase.
 */
class NavigationResultDelegation(override val skipToIdentifier: String? = null): NavigationResult

/**
 * NavigationResultBase is a simple encapsulation of a result that can control navigation when
 * the next step is determined by a StrategyBasedNavigator.
 * @param identifier the step identifier to be assigned to the result.
 * @param startTime The start Instant of this result.
 * @param endTime The end Instant of this result.
 * @param skipToIdentifier The identifier of the step to go to next.
 *                         If null, next will be use default stradegy behavior.
 */
class NavigationResultBase(
        identifier: String, startTime: Instant, endTime: Instant, skipToIdentifier: String? = null):
            Result by ResultBase(identifier, startTime, endTime),
            NavigationResult by NavigationResultDelegation(skipToIdentifier)

/**
 * NavigationAnswerResultBase is a simple encapsulation of an AnswerResult that can control navigation when
 * the next step is determined by a StrategyBasedNavigator.
 * the next step is determined by a StrategyBasedNavigator.
 * @param identifier the step identifier to be assigned to the result.
 * @param startTime The start Instant of this result.
 * @param endTime The end Instant of this result.
 * @param answer of the result, can be any generic type compatible with @AnswerResultType
 * @param answerResultType of the result
 * @param skipToIdentifier The identifier of the step to go to next.
 *                         If null, next will be use default stradegy behavior.
 */
class NavigationAnswerResultBase<T>(
        identifier: String, startTime: Instant, endTime: Instant,
        answer: T?, @AnswerResultType answerResultType: String, skipToIdentifier: String? = null):
            AnswerResult<T> by AnswerResultBase<T>(identifier, startTime, endTime, answer, answerResultType),
            NavigationResult by NavigationResultDelegation(skipToIdentifier)