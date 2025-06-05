package dataprovider;

import org.testng.annotations.DataProvider;

public class ReqresDataProvider {

    @DataProvider(name = "userData")
    public Object[][] userData() {
        return new Object[][] {
                {"Matheus", "Tester"},
                {"Mix", "Hacker"}
        };
    }
}