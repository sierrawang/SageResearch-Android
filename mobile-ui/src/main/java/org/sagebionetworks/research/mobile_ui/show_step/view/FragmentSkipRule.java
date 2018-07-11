package org.sagebionetworks.research.mobile_ui.show_step.view;

public interface FragmentSkipRule {
    boolean shouldSkip();

    String skipToIdentifier();
}
