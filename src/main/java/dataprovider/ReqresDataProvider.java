package dataprovider;

import daraFactory.ReqresDataFactory;
import org.testng.annotations.DataProvider;

public class ReqresDataProvider {

    private final ReqresDataFactory reqresDataFactory = new ReqresDataFactory();

    @DataProvider(name = "creatingUser")
    public Object[][] creatingUser() {
        return new Object[][] {
                {reqresDataFactory.setUser("Matheus", "Tester")}
        };
    }

    @DataProvider(name = "updateUser")
    public Object[][] updateUser() {
        return new Object[][] {
                {reqresDataFactory.setUser("Matheus", "Dev")}
        };
    }

    @DataProvider(name = "registerSuccessful")
    public Object[][] registerSuccessful() {
        return new Object[][] {
                {reqresDataFactory.setRegister("eve.holt@reqres.in", "pistol")}
        };
    }

    @DataProvider(name = "registerUnsuccessful")
    public Object[][] registerUnsuccessful() {
        return new Object[][] {
                {reqresDataFactory.setRegister("sydney@fife", null)}
        };
    }

    @DataProvider(name = "loginSuccessful")
    public Object[][] loginSuccessful() {
        return new Object[][] {
                {reqresDataFactory.setRegister("eve.holt@reqres.in", "cityslicka")}
        };
    }

    @DataProvider(name = "loginUnsuccessful")
    public Object[][] loginUnsuccessful() {
        return new Object[][] {
                {reqresDataFactory.setRegister("peter@klaven", null)}
        };
    }
}