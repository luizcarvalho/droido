package test;

import android.test.InstrumentationTestCase;

/**
 * Criado por luiz em 06/04/14.
 * Todos os direitos reservados para RedRails
 */
public class ExampleTest extends InstrumentationTestCase {
    public void test() throws Exception {
        final int expected = 1;
        final int reality = 1;
        assertEquals(expected, reality);
    }
}