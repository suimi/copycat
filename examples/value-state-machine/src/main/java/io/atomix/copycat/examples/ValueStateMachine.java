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

import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.Snapshottable;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.StateMachineExecutor;
import io.atomix.copycat.server.storage.snapshot.SnapshotReader;
import io.atomix.copycat.server.storage.snapshot.SnapshotWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Value state machine.
 *
 * @author <a href="http://github.com/kuujo>Jordan Halterman</a>
 */
public class ValueStateMachine extends StateMachine implements Snapshottable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValueStateMachine.class);

    private Long index = 0L;

    @Override protected void configure(StateMachineExecutor executor) {
        executor.register(SetCommand.class, this::set);
        executor.register(GetQuery.class, this::get);
        executor.register(DeleteCommand.class, this::delete);
    }

    /**
     * Sets the value.
     */
    private Long set(Commit<SetCommand> commit) {
        try {
            Long result = commit.operation().value();
            LOGGER.info("set cmd:{}", result);
//            Random r = new Random();
//            int i = r.nextInt(10);
//            if (i % 8 == 0) {
//                return null;
//            }
            if (result != index + 1) {
                LOGGER.warn("result:{} error,index:{}", result, index);
                return null;
            }
            index = result;
            commit.close();
            return result;
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    /**
     * Gets the value.
     */
    private Object get(Commit<GetQuery> commit) {
        try {
            return index;
        } finally {
            commit.close();
        }
    }

    /**
     * Deletes the value.
     */
    private void delete(Commit<DeleteCommand> commit) {
        try {
            Long result = commit.operation().value();
            LOGGER.info("delete cmd:{}", result);
            commit.close();
        } catch (Exception e) {
            commit.close();
            throw e;
        }
    }

    @Override public void snapshot(SnapshotWriter writer) {
        writer.writeUTF8("" + index).flush();
    }

    @Override public void install(SnapshotReader reader) {
        String in = reader.readUTF8();
        if (in != null && !"".equals(in)) {
            LOGGER.info("install snapshot:{}", in);
            index = Long.parseLong(in);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
