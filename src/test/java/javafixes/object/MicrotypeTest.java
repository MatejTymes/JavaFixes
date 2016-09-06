package javafixes.object;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class MicrotypeTest {

    @Test
    public void shouldNotBeAbleToCreateInstanceWithNullValue() {
        try {
            new UserId(null);

            fail("expected IllegalArgumentException");

        } catch (IllegalArgumentException expected) {
            // this is expected
        }
    }

    @Test
    public void shouldBeAbleToGetItsValue() {
        assertThat(new UserId("mtymes").value(), equalTo("mtymes"));
        assertThat(new SystemId("theo").getValue(), equalTo("theo"));
    }

    @Test
    public void shouldBeEqualIfTheValueAndClassIsTheSame() {
        assertThat(new UserId("msmith"), equalTo(new UserId("msmith")));
        assertThat(new SystemId("zeus").hashCode(), equalTo(new SystemId("zeus").hashCode()));
    }

    @Test
    public void shouldNotBeEqualIfValueIsDifferent() {
        assertThat(new UserId("hsimpson"), not(equalTo(new SystemId("sgriffin"))));
    }

    @Test
    public void shouldNotBeEqualIfClassIsDifferent() {
        assertThat(new UserId("pluto"), not(equalTo(new SystemId("pluto"))));
    }

    @Test
    public void shouldBeAbleToMapValue() {
        UserId userId = new UserId("sgriffin");

        // When
        String name = userId.map(
                id -> id.substring(0, 1).toUpperCase() + ". " + id.substring(1, 2).toUpperCase() + id.substring(2)
        );

        // Then
        assertThat(name, equalTo("S. Griffin"));
    }

    @Test
    public void shouldPrintProvideValueOnToString() {
        assertThat(new UserId("patreid").toString(), equalTo("patreid"));
        assertThat(new Age(45).toString(), equalTo("45"));
    }

    private static class UserId extends Microtype<String> {
        public UserId(String value) {
            super(value);
        }
    }

    private static class SystemId extends Microtype<String> {
        public SystemId(String value) {
            super(value);
        }
    }

    private static class Age extends Microtype<Integer> {
        public Age(Integer value) {
            super(value);
        }
    }
}