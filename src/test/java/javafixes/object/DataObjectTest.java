package javafixes.object;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class DataObjectTest {

    @Test
    public void shouldBeEqualIfTheValuesAndClassIsTheSame() {
        assertThat(new User("John", "Doe"), equalTo(new User("John", "Doe")));
    }

    @Test
    public void shouldNotBeEqualIfValuesAreDifferent() {
        assertThat(new User("John", "Doe"), not(equalTo(new User("Lucy", "Mint"))));
    }

    @Test
    public void shouldNotBeEqualIfClassIsDifferent() {
        assertThat(new User("John", "Doe"), not(equalTo(new Person("John", "Doe"))));
    }

    @Test
    public void shouldPrintProvideValueOnToString() {
        assertThat(new User("John", "Doe").toString(), equalTo("DataObjectTest.User[firstName=John,lastName=Doe]"));
    }

    private static class User extends DataObject {
        public final String firstName;
        public final String lastName;

        public User(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    private static class Person extends DataObject {
        public final String firstName;
        public final String lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}