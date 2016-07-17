package mtymes.javafixes.object;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TripleTest {

    @Test
    public void shouldBeAbleToGetValues() {
        Triple<String, Integer, BigDecimal> triple = Triple.triple("Peter Smith", 42, new BigDecimal("45000.00"));

        assertThat(triple.a, equalTo("Peter Smith"));
        assertThat(triple.a(), equalTo("Peter Smith"));
        assertThat(triple.getA(), equalTo("Peter Smith"));

        assertThat(triple.b, equalTo(42));
        assertThat(triple.b(), equalTo(42));
        assertThat(triple.getB(), equalTo(42));

        assertThat(triple.c, equalTo(new BigDecimal("45000.00")));
        assertThat(triple.c(), equalTo(new BigDecimal("45000.00")));
        assertThat(triple.getC(), equalTo(new BigDecimal("45000.00")));
    }

    @Test
    public void shouldFailOnNullMapper() {
        Triple<String, Integer, BigDecimal> triple = Triple.triple("Peter Smith", 42, new BigDecimal("45000.00"));

        try {
            // When
            triple.map(null);

            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void shouldBeAbleToMapValues() {

        Triple<String, Integer, BigDecimal> triple = Triple.triple("Peter Smith", 42, new BigDecimal("45000.00"));

        String result = triple.map((name, age, salary) -> name + " aged " + age + " has yearly salary of " + salary);

        assertThat(result, equalTo("Peter Smith aged 42 has yearly salary of 45000.00"));
    }
}