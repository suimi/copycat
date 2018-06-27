/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package io.atomix.copycat.examples;

import io.atomix.copycat.Command;

/**
 * Value delete command.
 *
 * @author <a href="http://github.com/kuujo>Jordan Halterman</a>
 */
public class DeleteCommand implements Command<Void> {
    private final Long value;

    public DeleteCommand(Long value) {
        this.value = value;
    }

    /**
     * Returns the value.
     */
    public Long value() {
        return value;
    }

    @Override public CompactionMode compaction() {
        return CompactionMode.QUORUM;
    }

    @Override public String toString() {
        return value.toString();
    }
}
