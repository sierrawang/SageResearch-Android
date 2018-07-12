package org.sagebionetworks.research.domain.inject;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import org.sagebionetworks.research.domain.form.DataTypes.InputDataType;
import org.sagebionetworks.research.domain.form.implementations.ChoiceBase;
import org.sagebionetworks.research.domain.form.interfaces.Choice;
import org.sagebionetworks.research.domain.result.implementations.AnswerResultBase;
import org.sagebionetworks.research.domain.result.implementations.CollectionResultBase;
import org.sagebionetworks.research.domain.result.implementations.ErrorResultBase;
import org.sagebionetworks.research.domain.result.implementations.FileResultBase;
import org.sagebionetworks.research.domain.result.implementations.TaskResultBase;
import org.sagebionetworks.research.domain.result.interfaces.AnswerResult;
import org.sagebionetworks.research.domain.result.interfaces.CollectionResult;
import org.sagebionetworks.research.domain.result.interfaces.ErrorResult;
import org.sagebionetworks.research.domain.result.interfaces.FileResult;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.implementations.ActiveUIStepBase;
import org.sagebionetworks.research.domain.step.implementations.CompletionStepBase;
import org.sagebionetworks.research.domain.step.implementations.FormUIStepBase;
import org.sagebionetworks.research.domain.step.implementations.SectionStepBase;
import org.sagebionetworks.research.domain.step.implementations.TransformerStepBase;
import org.sagebionetworks.research.domain.step.implementations.UIStepBase;
import org.sagebionetworks.research.domain.step.interfaces.ActiveUIStep;
import org.sagebionetworks.research.domain.step.interfaces.CompletionStep;
import org.sagebionetworks.research.domain.step.interfaces.FormUIStep;
import org.sagebionetworks.research.domain.step.interfaces.SectionStep;
import org.sagebionetworks.research.domain.step.interfaces.TransformerStep;
import org.sagebionetworks.research.domain.step.interfaces.UIStep;
import org.sagebionetworks.research.domain.inject.GsonModule.ClassKey;
import org.sagebionetworks.research.domain.step.ui.action.implementations.ActionBase;
import org.sagebionetworks.research.domain.step.ui.action.implementations.ReminderActionBase;
import org.sagebionetworks.research.domain.step.ui.action.implementations.SkipToStepActionBase;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.Action;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.ReminderAction;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.SkipToStepAction;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.TaskInfo;
import org.sagebionetworks.research.domain.task.TaskInfoBase;
import org.sagebionetworks.research.domain.task.navigation.TaskBase;

import java.util.Map;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

import static org.sagebionetworks.research.domain.inject.GsonModule.createPassThroughDeserializer;
import static org.sagebionetworks.research.domain.inject.GsonModule.createPassThroughSerializer;

@Module(includes = GsonModule.class)
public abstract class JsonModule {
    // region JsonDeserializers
    @Provides
    @IntoMap
    @ClassKey(ActiveUIStep.class)
    static JsonDeserializer<?> provideActiveUIStepDeserializer() {
        return createPassThroughDeserializer(ActiveUIStepBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(SectionStep.class)
    static JsonDeserializer<?> provideSectionStepDeserializer() {
        return createPassThroughDeserializer(SectionStepBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(TransformerStep.class)
    static JsonDeserializer<?> provideTransformerStepDeserializer() {
        return TransformerStepBase.getJsonDeserializer();
    }

    @Provides
    @IntoMap
    @ClassKey(FormUIStep.class)
    static JsonDeserializer<?> providedFormUIStepDeserializer() {
        return createPassThroughDeserializer(FormUIStepBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(UIStep.class)
    static JsonDeserializer<?> providedUIStepDeserializer() {
        return createPassThroughDeserializer(UIStepBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(CompletionStep.class)
    static JsonDeserializer<?> provideCompletionStepDeserializer() {
        return createPassThroughDeserializer(CompletionStepBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(Choice.class)
    static JsonDeserializer<?> provideChoiceDeserializer() {
        return ChoiceBase.getJsonDeserializer();
    }

    @Provides
    @IntoMap
    @ClassKey(InputDataType.class)
    static JsonDeserializer<?> provideInputDataTypeDeserializer() {
        return InputDataType.getJsonDeserializer();
    }

    @Provides
    @IntoMap
    @ClassKey(AnswerResult.class)
    static JsonDeserializer<?> provideAnswerResultDeserializer() {
        return createPassThroughDeserializer(AnswerResultBase.class);
    }


    @Provides
    @IntoMap
    @ClassKey(CollectionResult.class)
    static JsonDeserializer<?> provideCollectionResultDeserializer() {
        return createPassThroughDeserializer(CollectionResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(ErrorResult.class)
    static JsonDeserializer<?> provideErrorResultDeserializer() {
        return createPassThroughDeserializer(ErrorResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(FileResult.class)
    static JsonDeserializer<?> provideFileResultDeserializer() {
        return createPassThroughDeserializer(FileResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(TaskResult.class)
    static JsonDeserializer<?> provideTaskResultDeserializer() {
        return createPassThroughDeserializer(TaskResultBase.class);
    }

    /**
     * @return The json Deserializer for a task.
     */
    @Provides
    @IntoMap
    @ClassKey(Task.class)
    static JsonDeserializer<?> provideTaskDeserializer() {
        return createPassThroughDeserializer(TaskBase.class);
    }

    /**
     * @return The json Deserializer for a task info.
     */
    @Provides
    @IntoMap
    @ClassKey(TaskInfo.class)
    static JsonDeserializer<?> provideTaskInfoDeserializer() {
        return createPassThroughDeserializer(TaskInfoBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(Action.class)
    static JsonDeserializer<?> provideActionDeserializer() {
        return createPassThroughDeserializer(ActionBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(ReminderAction.class)
    static JsonDeserializer<?> provideReminderActionDeserializer() {
        return createPassThroughDeserializer(ReminderActionBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(SkipToStepAction.class)
    static JsonDeserializer<?> provideSkipToStepActionDeserializer() {
        return createPassThroughDeserializer(SkipToStepActionBase.class);
    }
    // endregion

    // region Json Serializers
    @Provides
    @IntoMap
    @ClassKey(CollectionResult.class)
    static JsonSerializer<?> provideCollectionResultSerializer() {
        return createPassThroughSerializer(CollectionResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(AnswerResult.class)
    static JsonSerializer<?> provideAnswerResultSerializer() {
        return createPassThroughSerializer(AnswerResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(ErrorResult.class)
    static JsonSerializer<?> provideErrorResultSerializer() {
        return createPassThroughSerializer(ErrorResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(FileResult.class)
    static JsonSerializer<?> provideFileResultSerializer() {
        return createPassThroughSerializer(FileResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(TaskResult.class)
    static JsonSerializer<?> provideTaskResultSerializer() {
        return createPassThroughSerializer(TaskResultBase.class);
    }
    // endregion

    @Binds
    @IntoMap
    @DependencyInjectionType.DependencyInjectionTypeKey(DependencyInjectionType.DEFAULT)
    abstract Map<Class<?>, JsonDeserializer<?>>
    bindDefaultJsonDeserializerMap(Map<Class<?>, JsonDeserializer<?>> defaultJsonDeserializerMap);

    @Binds
    @IntoMap
    @DependencyInjectionType.DependencyInjectionTypeKey(DependencyInjectionType.DEFAULT)
    abstract Map<Class<?>, JsonSerializer<?>>
    bindDefaultJsonSerializerMap(Map<Class<?>, JsonSerializer<?>> defaultJsonSerializerMap);
}
