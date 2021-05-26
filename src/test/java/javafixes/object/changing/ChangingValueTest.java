package javafixes.object.changing;

import org.junit.Test;

import static javafixes.object.changing.ChangingValue.join;
import static javafixes.object.changing.MutableValue.mutableValue;
import static javafixes.test.Random.randomBoolean;

public class ChangingValueTest {


    @Test
    public void shouldBeAbleToUseIt() {

        MutableValue<String> hostAndPort = mutableValue("host123:8080")
                .withOnValueChangedFunction(
                        value -> System.out.println("host = " + value),
                        true
                );
        ChangingValue<Boolean> isConnected = hostAndPort.map(value -> randomBoolean())
                .withOnValueChangedFunction(
                        value -> System.out.println("is connected = " + value),
                        true
                );

        ChangingValue<String> nodeInfo = join(hostAndPort, isConnected).map(tuple -> {
            System.out.println("generating new value");
            return tuple.a + " : " + (tuple.b ? "IS connected" : "UNABLE to connect");
        }).withOnValueChangedFunction(
                value -> System.out.println("hostInfo =  " + value),
                true
        );

        System.out.println(nodeInfo.value());

        hostAndPort.updateValue("host456:1234");

        System.out.println(nodeInfo.value());

        long startTime = System.currentTimeMillis();
        CharSequence nodeInfoString = nodeInfo.mapToProxy(CharSequence.class);
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("duration = " + duration);
        System.out.println(nodeInfoString);

        System.out.println(duration);


        MutableValue<String> name = mutableValue("Peter");
        MutableValue<String> surname = mutableValue("Thompson");

        ChangingValue<String> fullName = join(name, surname)
                .map(tuple -> {
                    System.out.println("Generating new name");
                    return tuple.a() + " " + tuple.b();
                });

        System.out.println(fullName.value());

        MutableValue<String> greeting = mutableValue("Hello")
                // do this only if you want to
                .withValueName("Greeting")
                .withDisposeFunction(aGreeting -> System.out.println("We just got rid of '" + aGreeting + "'"));

        ChangingValue<String> upperCaseGreeting = greeting.map(String::toUpperCase)
                .withValueName("UpperCaseGreeting");

        ChangingValue<Boolean> isGreetingLong = upperCaseGreeting
                .map(aGreeting -> aGreeting.length() > 7)
                // do this only if you want to
                .withValueName("IsGreetingLong")
                .withDisposeFunction(isLong -> System.out.println("don't know what to do with this"));

        CharSequence greetingProxy = greeting.mapToProxy(CharSequence.class);

        System.out.println("Greeting '" + greeting.value() + "' " + (isGreetingLong.value() ? "is long" : "is not long"));
        System.out.println(greetingProxy.toString());

        greeting.updateValue("Dobry den");
        System.out.println("Greeting '" + greeting.value() + "' " + (isGreetingLong.value() ? "is long" : "is not long"));
        System.out.println(greetingProxy.toString());

        greeting.updateValue(null);
        try {
            System.out.println("Greeting '" + greeting.value() + "' " + (isGreetingLong.value() ? "is long" : "is not long"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(greetingProxy.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}