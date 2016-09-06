package javafixes.object;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TupleTest {

    @Test
    public void shouldBeAbleToGetValues() {
        Tuple<String, Integer> tuple = Tuple.tuple("Peter Smith", 42);

        assertThat(tuple.a, equalTo("Peter Smith"));
        assertThat(tuple.a(), equalTo("Peter Smith"));
        assertThat(tuple.getA(), equalTo("Peter Smith"));

        assertThat(tuple.b, equalTo(42));
        assertThat(tuple.b(), equalTo(42));
        assertThat(tuple.getB(), equalTo(42));
    }

    @Test
    public void shouldFailOnNullMapper() {
        Tuple<String, Integer> tuple = Tuple.tuple("Peter Smith", 42);

        try {
            // When
            tuple.map(null);

            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void shouldBeAbleToMapValues() {

        Tuple<String, Integer> tuple = Tuple.tuple("Peter Smith", 42);

        String result = tuple.map((name, age) -> name + " is " + age + " years old");

        assertThat(result, equalTo("Peter Smith is 42 years old"));
    }
}