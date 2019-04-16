package com.eresearch.elsevier.sciencedirect.consumer.connector.guard;

import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectSearchViewEntry;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MemoGuardStack {

    private final Stack<MemoGuard> stack;

    public MemoGuardStack() {
        stack = new Stack<>();
    }

    /*
    NOTE: this should not be called on an empty stack.
     */
    Integer accumulateUniqueEntriesSize() {

        List<MemoGuard> memoGuards = new LinkedList<>();
        while (!stack.isEmpty()) {
            memoGuards.add(stack.pop());
        }

        List<ScienceDirectSearchViewEntry> accumulatedNotUniqueAuthorSearchViewEntries = memoGuards
                .stream()
                .map(MemoGuard::getScienceDirectSearchViewEntries)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return new HashSet<>(accumulatedNotUniqueAuthorSearchViewEntries).size();
    }

    void push(MemoGuard memoGuard) {
        stack.push(memoGuard);
    }

    void clean() {
        stack.clear();
    }

    Optional<MemoGuard> peek() {
        try {
            return Optional.of(stack.peek());
        } catch (EmptyStackException ex) {
            return Optional.empty();
        }
    }

}
