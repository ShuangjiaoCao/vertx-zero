package io.vertx.up.tool;

import io.vertx.quiz.ZeroBase;
import org.junit.Test;

public class NetTc extends ZeroBase {

    @Test
    public void testSocket() {
        final boolean on = Net.isReach("localhost", 8063);
        System.out.println(on);
    }
}
