package javafixes.test;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;

// todo: create new maven artifact with this javafixes-test
public class OptionalMatcher<T> extends TypeSafeMatcher<Optional<T>> {

    private final boolean shouldBePresent;
    private final Optional<Matcher<T>> valueMatcher;

    public OptionalMatcher(boolean shouldBePresent, Optional<Matcher<T>> valueMatcher) {
        this.shouldBePresent = shouldBePresent;
        this.valueMatcher = valueMatcher;
    }

    @Override
    protected boolean matchesSafely(Optional<T> item) {
        return item.isPresent() == shouldBePresent && valueMatcher.map(matcher -> matcher.matches(item)).orElse(true);
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("that optional value ")
                .appendText(shouldBePresent ? "SHOULD BE" : "SHOULD NOT BE")
                .appendText(" present");
        valueMatcher.ifPresent(matcher -> {
            description.appendText(" with value ");
            matcher.describeTo(description);
        });
    }

    @Override
    protected void describeMismatchSafely(Optional<T> item, Description mismatchDescription) {
        mismatchDescription
                .appendText("it ")
                .appendText(item.isPresent() ? "WAS" : "WAS NOT")
                .appendText(" present")
                .appendText(item.isPresent() ? " containing value '" + item.get() + "'" : "");
    }

    public static <T> Matcher<Optional<T>> isPresent() {
        return new OptionalMatcher<T>(true, Optional.empty());
    }

    public static <T> Matcher<Optional<T>> isPresentAnd(Matcher<T> matcher) {
        return new OptionalMatcher<T>(true, Optional.of(matcher));
    }

    public static <T> Matcher<Optional<T>> isPresentAndEqualTo(T value) {
        return new OptionalMatcher<T>(true, Optional.of(equalTo(value)));
    }

    public static <T> Matcher<Optional<T>> isNotPresent() {
        return new OptionalMatcher<T>(false, Optional.empty());
    }
}
