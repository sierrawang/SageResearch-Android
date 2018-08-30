package org.sagebionetworks.research.domain.form.implementations;

import com.google.gson.Gson;

import org.sagebionetworks.research.domain.inject.GsonModule;
import org.sagebionetworks.research.domain.inject.InputFieldsModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {InputFieldsModule.class, GsonModule.class})
public interface InputFieldTestComponent {
    Gson gson();
}
