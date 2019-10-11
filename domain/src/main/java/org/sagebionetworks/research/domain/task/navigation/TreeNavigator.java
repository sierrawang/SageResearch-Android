package org.sagebionetworks.research.domain.task.navigation;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.SectionStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class TreeNavigator implements StepNavigator {

    /*
     * A class to represent a single node in the TreeNavigator
     */
    private static final class Node {
        // The children if any that this node has. children.isEmpty() == false always, when
        // children == null the node is a leaf.
        @Nullable
        private final ImmutableList<Node> children;

        // The step that this node represents
        @Nullable
        private final Step step;

        // The parent node of this node, root nodes will have this as null
        @Nullable
        private final Node parent;

        //

        /**
         * Constructs a new node from the given list of steps. This node will represent the root of a Tree in which
         * the steps are the children.
         *
         * @param steps
         *         The list of steps to construct the node from.
         */
        private Node(@Nullable List<Step> steps) {
            this.step = null;
            this.parent = null;
            this.children = constructChildNodes(steps, null);
        }

        /**
         * Constructs a new node corresponding to the given step.
         *
         * @param step
         *         the step to construct the node from.
         */
        private Node(@NonNull Step step, @Nullable Node parentNode) {
            this.step = step;
            this.parent = parentNode;
            if (step instanceof SectionStep) {
                this.children = constructChildNodes(((SectionStep) step).getSteps(), this);
            } else {
                this.children = null;
            }
        }

        @Override
        public String toString() {
            if (this.step != null) {
                return this.step.toString() + ": " + "NODE";
            } else {
                return "ROOT";
            }
        }

        /**
         * Constructs nodes from all the steps in the given list and returns them in an ImmutableList.
         *
         * @param childSteps
         *         the list of steps to construct nodes from.
         * @return An ImmutableList of nodes constructed from the given steps.
         */
        private ImmutableList<Node> constructChildNodes(@Nullable List<Step> childSteps, @Nullable Node parentNode) {
            if (childSteps != null && !childSteps.isEmpty()) {
                List<Node> children = new ArrayList<>();
                for (Step childStep : childSteps) {
                    children.add(new Node(childStep, parentNode));
                }

                ImmutableList.Builder<Node> builder = new ImmutableList.Builder<>();
                builder.addAll(children);
                return builder.build();
            }

            return null;
        }

        /**
         * Returns true if this node is a leaf, false otherwise.
         *
         * @return true if this node is a leaf, false otherwise.
         */
        private boolean isLeaf() {
            return this.children == null;
        }

        /**
         * @return a flattened list of all nodes within the tree
         */
        @NonNull
        private List<Node> findAllNodes() {
            List<Node> leafNodes = new ArrayList<>();
            findAllNodesRecursively(leafNodes);
            return leafNodes;
        }

        /**
         * @return recursively traverse nodes until we have all the nodes in thge list
         */
        private void findAllNodesRecursively(List<Node> nodeListByRef) {
            nodeListByRef.add(this);
            if (children != null) {
                for (Node child : children) {
                    child.findAllNodesRecursively(nodeListByRef);
                }
            }
        }
    }

    /**
     * The separator used when naming sub-steps within SectionSteps
     */
    public static final String SECTION_STEP_PREFIX_SEPARATOR = "_";

    // Stores the list of progressMarkers which are the identifiers of the steps which count
    // towards computing the progress. An empty list represents the absence of progress markers,
    // while null represents that the navigator should attempt to estimate the progress without the
    // use of any progress markers.
    @Nullable
    private final ImmutableList<String> progressMarkers;

    // The root of the tree navigator.
    @NonNull
    private final TreeNavigator.Node root;

    // A map from step identifier to step.
    @NonNull
    private final ImmutableMap<String, Step> stepsById;


    /**
     * Constructs a TreeNavigator from the given list of steps, and the given progress markers
     *
     * @param steps
     *         The list of steps to construct this TreeNavigator from.
     * @param progressMarkers
     *         The list of progressMarkers to construct this TreeNavigator from.
     */
    public TreeNavigator(@NotNull List<Step> steps, @Nullable List<String> progressMarkers) {
        this.root = new Node(steps);
        this.progressMarkers = progressMarkers == null ? null : ImmutableList.copyOf(progressMarkers);
        this.stepsById = buildStepsByID(steps);
    }

    @Nullable
    @Override
    public Step getStep(@NotNull String identifier) {
        Step step = this.stepsById.get(identifier);
        if (step == null) {
            // Due to the way that SectionStep's sub-step identifiers are created in ResourceTaskRepository.
            // There may be some step identifiers that are prefixed with their sub-step identifiers.
            // However, we can detect for that scenario with isNestedWithinSectionSteps function.
            step = findStepNestedWithinSectionSteps(identifier);
        }
        return step;
    }

    /**
     * @param identifier to find at the end of the stepIdentifierPath
     * @return true if we found the sub-step identifier at the end of the path,
     *         and each part of the path is a section step containing the next part of the path.
     */
    @Nullable
    private Step findStepNestedWithinSectionSteps(@NotNull String identifier) {
        for (Node node: root.findAllNodes()) {
            if (isValidNestedStep(node, identifier)) {
                return node.step;
            }
        }
        return null;
    }

    /**
     * @param node to check the validity of its path.
     * @param matchingIdentifier that we are trying to match up with a valid leaf.
     * @return true the path of the node corresponds
     *         to the correct format of ResourceTaskRepository nested section steps.
     */
    private boolean isValidNestedStep(@NonNull Node node, @NonNull String matchingIdentifier) {
        // Node must be a leaf to check for a valid nested step
        if (node.step == null) {
            return false;
        }

        // Create the path components to validate if the node's path
        // conforms to how ResourceTaskRepository creates identifiers.
        List<String> stepIdentifierPath = Arrays.asList(node.step.getIdentifier().split(SECTION_STEP_PREFIX_SEPARATOR));
        if (stepIdentifierPath == null || node.parent == null || stepIdentifierPath.size() <= 0) {
            return false;
        }

        int pathIndex = stepIdentifierPath.size();

        // Make sure that the last part of the path is our matching identifier
        if (!matchingIdentifier.equals(stepIdentifierPath.get(pathIndex - 1))) {
            return false;
        }

        // Loop backwards and check that the node's nested step identifiers
        // match how we expect the ResourceTaskRepository to make them.
        Node parentNode = node;
        do {
            // Build the full identifier we expect for this node
            List<String> currentPathToNode = stepIdentifierPath.subList(0, pathIndex);
            String stepIdentifier = join(SECTION_STEP_PREFIX_SEPARATOR, currentPathToNode);

            // If our expectedIdentifier doesn't match what the step identifier really is, it is not valid.
            if (!(parentNode.step != null &&
                    parentNode.step.getIdentifier().equals(stepIdentifier))) {
                return false;
            }
            pathIndex--;
            parentNode = parentNode.parent;
        } while(parentNode != null);

        // If we reached this point, all checks have passed and this is a valid nested step.
        return true;
    }

    /**
     * Unit testable join function.
     * TextUtils.join() does not work with unit tests because it's in the Android framework.
     */
    @NonNull
    private String join(@NonNull String delimiter, @NonNull List<String> parts) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(parts.get(i));
        }
        return sb.toString();
    }

    @NotNull
    @Override
    public StepAndNavDirection getNextStep(@Nullable Step step, @NotNull TaskResult taskResult) {
        Step nextStep = nextStepHelper(step, this.root, new AtomicBoolean(false));
        return new StepAndNavDirection(nextStep, NavDirection.SHIFT_LEFT);
    }

    @Nullable
    @Override
    public Step getPreviousStep(@NotNull Step step, @NotNull TaskResult taskResult) {
        return previousStepHelper(step, this.root, new AtomicBoolean(false));
    }

    @Nullable
    @Override
    public TaskProgress getProgress(@NonNull final Step step, @NonNull TaskResult taskResult) {
        if (this.progressMarkers != null) {
            // Get the list of steps that have executed, and add the current step in case it hasn't
            // been added to the step history yet.
            List<String> stepIdentifiers = new ArrayList<>();
            for (Result result : taskResult.getStepHistory()) {
                stepIdentifiers.add(result.getIdentifier());
            }
            stepIdentifiers.add(step.getIdentifier());

            int idx = this.getLastIndexInMarkers(stepIdentifiers);
            if (idx == -1) {
                return null;
            }

            int current = idx + 1;
            if (current == this.progressMarkers.size() &&
                    !this.progressMarkers.contains(step.getIdentifier())) {
                // If this is the last step in the progress markers and we are beyond the step we
                // return null.
                return null;
            }

            // The progress is the current step index, out of the total number of markers, and it
            // isn't estimated.
            return new TaskProgress(current, this.progressMarkers.size(), false);
        }

        // If there are no progress markers, default to using the total number of steps, and the
        // result to figure out how many have gone.
        ImmutableSet<String> stepIDs = this.stepsById.keySet();
        Set<String> finishedStepIDs = new HashSet<>();
        for (Result result : taskResult.getStepHistory()) {
            finishedStepIDs.add(result.getIdentifier());
        }

        // The union total will store the number of elements in both sets.
        int unionTotal = 0;
        for (String stepID : finishedStepIDs) {
            if (stepIDs.contains(stepID)) {
                unionTotal++;
            }
        }

        // The number of unique elements across both sets is the total.
        int total = stepIDs.size() + finishedStepIDs.size() - unionTotal;
        // The current step hasn't been finished so we remove it.
        finishedStepIDs.remove(step.getIdentifier());
        // We add one here because the progress should be 1 indexed.
        int current = finishedStepIDs.size() + 1;
        return new TaskProgress(current, total, true);

    }

    @NotNull
    @Override
    public List<Step> getSteps() {
        return new ArrayList<>(this.stepsById.values());
    }

    /**
     * Returns the last index in the progress markers such that the given list of identifiers contains the progress
     * marker at this index.
     *
     * @param identifiers
     *         The list of identifiers to check against the progress markers.
     * @return The last index in the progress markers such that the given list of identifiers contains the progress
     * marker at this index.
     */
    private int getLastIndexInMarkers(List<String> identifiers) {
        if (this.progressMarkers != null) {
            for (int i = this.progressMarkers.size() - 1; i >= 0; i--) {
                if (identifiers.contains(progressMarkers.get(i))) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Adds the given step and all of its substeps to the given builder.
     *
     * @param step
     *         The step to add (along with all of its substeps) to the given builder.
     * @param builder
     *         The builder to add the given step to.
     */
    private static void addStepToBuilderRecursively(Step step, ImmutableMap.Builder<String, Step> builder) {
        if (step instanceof SectionStep) {
            SectionStep sectionStep = (SectionStep) step;
            builder.put(sectionStep.getIdentifier(), sectionStep);
            for (Step childStep : sectionStep.getSteps()) {
                addStepToBuilderRecursively(childStep, builder);
            }
        } else {
            builder.put(step.getIdentifier(), step);
        }
    }

    /**
     * Constructs and returns an ImmutableMap that maps step identifiers to steps for every step and every substep of
     * every step recursively, in the given list of steps.
     *
     * @param steps
     *         The list of steps to construct the map from.
     * @return An ImmutableMap that maps step identifiers to steps for every step and every substep of every step
     * recursively, in the given list of steps.
     */
    private static ImmutableMap<String, Step> buildStepsByID(List<Step> steps) {
        ImmutableMap.Builder<String, Step> builder = new ImmutableMap.Builder<>();
        for (Step step : steps) {
            addStepToBuilderRecursively(step, builder);
        }

        return builder.build();
    }

    /**
     * Returns the first leaf step that appears after the given initialStep, in this TreeNavigator. After is defined
     * as the next leaf in a pre-order traversal of the tree.
     *
     * @param initialStep
     *         The step to find the step after.
     * @param current
     *         The step that is being evaluated by this call to nextStepHelper().
     * @param hasFoundInitial
     *         True if the initialStep has already been encountered by a parent recursive call of this call to
     *         nextStepHelper(), false otherwise.
     * @return The first leaf step that appears after the given initialStep, or null if no such step exists.
     */
    @Nullable
    private static Step nextStepHelper(@Nullable Step initialStep, @Nullable Node current,
            AtomicBoolean hasFoundInitial) {
        if (current == null) {
            return null;
        }

        if (current.step != null) {
            if ((initialStep == null || hasFoundInitial.get()) && current.isLeaf()) {
                return current.step;
            }

            if (current.step.equals(initialStep)) {
                hasFoundInitial.set(true);
            }
        }

        if (current.children != null) {
            for (Node child : current.children) {
                Step found = nextStepHelper(initialStep, child, hasFoundInitial);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    /**
     * Returns the first leaf step that appears before the given initialStep, in this TreeNavigator. Before is defined
     * as the next leaf in a reverse pre-order traversal of the tree.
     *
     * @param initialStep
     *         The step to find the step before.
     * @param current
     *         The step that is being evaluated by this call to previousStepHelper().
     * @param hasFoundInitial
     *         True if the initialStep has already been encountered by a parent recursive call of this call to
     *         previousStepHelper(), false otherwise.
     * @return The first leaf step that appears before the given initialStep, or null if no such step exists.
     */
    @Nullable
    private static Step previousStepHelper(@Nullable Step initialStep, @Nullable Node current,
            AtomicBoolean hasFoundInitial) {
        if (current == null) {
            return null;
        }

        if (current.step != null) {
            if (hasFoundInitial.get() && current.isLeaf()) {
                return current.step;
            }

            if (current.step.equals(initialStep)) {
                hasFoundInitial.set(true);
            }
        }

        if (current.children != null) {
            for (Node child : current.children.reverse()) {
                Step found = previousStepHelper(initialStep, child, hasFoundInitial);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }
}
