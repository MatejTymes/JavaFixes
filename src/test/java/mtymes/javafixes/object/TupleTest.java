package mtymes.javafixes.object;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TupleTest {

    @Test
    public void shouldBeAbleToMapValues() {

        Tuple<String, Integer> tuple = Tuple.tuple("Peter Smith", 42);

        String result = tuple.map((name, age) -> name + " is " + age + " years old");

        assertThat(result, equalTo("Peter Smith is 42 years old"));
    }
}